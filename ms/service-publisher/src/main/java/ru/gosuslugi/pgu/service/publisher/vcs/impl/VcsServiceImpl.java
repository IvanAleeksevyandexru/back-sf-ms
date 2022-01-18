package ru.gosuslugi.pgu.service.publisher.vcs.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.jgit.util.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.zeroturnaround.zip.ZipUtil;
import ru.gosuslugi.pgu.service.publisher.vcs.VcsException;
import ru.gosuslugi.pgu.service.publisher.vcs.VcsProperties;
import ru.gosuslugi.pgu.service.publisher.vcs.VcsService;
import ru.gosuslugi.pgu.service.publisher.vcs.dto.GitTagDto;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class VcsServiceImpl implements VcsService {

    public static final Duration AMOUNT_TO_SUBTRACT = Duration.ofDays(30);

    private final Git git;
    private final VcsProperties vcsProperties;
    private final Map<String, String> servicesFromConfig;
    private final CredentialsProvider credentialsProvider;

    private Repository repository;

    @Override
    @SneakyThrows
    @Scheduled(fixedRateString = "${vcs.schedule-rate-milliseconds}")
    public void refresh() {
        log.info("git pull --tags");
        git.pull().setCredentialsProvider(credentialsProvider).setTagOpt(TagOpt.FETCH_TAGS).call();
    }

    @Override
    @SneakyThrows
    public List<GitTagDto> getVersions() {
        Instant monthAgo = Instant.now().minus(AMOUNT_TO_SUBTRACT);
        RevWalk revWalk = new RevWalk(repository);
        var tags = git.tagList().call();
        var resultList = tags.stream()
                .map(tag -> apply(tag, revWalk))
                .filter(gitTagDto -> monthAgo.isBefore(gitTagDto.getTimestamp()))
                .collect(Collectors.toList());
        revWalk.close();
        return resultList;
    }

    @Override
    public File getTemplates(String versionTag, String serviceId) {
        Ref tag = getTagByName(versionTag);
        if (!getServicesList(versionTag).containsKey(serviceId) || isNull(tag)) {
            throw new VcsException("serviceId or version is wrong!");
        }
        String objectId = tag.getObjectId().getName();
        return createZipFor(objectId, serviceId);
    }

    @Override
    public byte[] getDescriptor(String versionTag, String serviceId) {
        Ref gitTagDto = getTagByName(versionTag);
        if (isNull(gitTagDto)) {
            throw new VcsException("version is wrong");
        }
        String serviceFileName = getServicesList(versionTag).get(serviceId);
        if (isNull(serviceFileName)) {
            throw new VcsException("serviceId is wrong");
        }
        return getBytesFromTag(gitTagDto.getObjectId(), serviceFileName);
    }

    @Override
    public byte[] getConfig(String versionTag, String serviceId) {
        Ref gitTagDto = getTagByName(versionTag);
        if (!getServicesList(versionTag).containsKey(serviceId) || isNull(gitTagDto)) {
            throw new VcsException("serviceId or version is wrong!");
        }
        return getBytesFromTag(gitTagDto.getObjectId(), vcsProperties.getServiceConfigPath(serviceId));
    }

    @SneakyThrows
    private Ref getTagByName(String tagname) {
        List<Ref> tags = requireNonNullElse(git.tagList().call(), Collections.emptyList());
        var resultList = tags.stream()
                .filter(tag -> tag.getName().contains(tagname))
                .findFirst().orElseThrow();
        return resultList;
    }

    @SneakyThrows
    private File getServiceTemplatePackageDir(String objectId, String serviceId) {
        var commitTag = ObjectId.fromString(objectId);
        RevWalk revWalk = new RevWalk(repository);
        RevCommit commit = revWalk.parseCommit(commitTag);

        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.setRecursive(false);

        File baseDir = new File(vcsProperties.getServicesTempPath());
        treeWalk.addTree(commit.getTree());
        treeWalk.setFilter(createFilter(vcsProperties.getServiceTemplatePath(serviceId)));
        while (treeWalk.next()) {
            if (treeWalk.isSubtree()) {
                treeWalk.enterSubtree();
                continue;
            }
            File f = new File(baseDir, treeWalk.getPathString());
            FileUtils.mkdirs(f.getParentFile(), true);
            ObjectId id = treeWalk.getObjectId(0);
            try (FileOutputStream fos = new FileOutputStream(f)) {
                repository.open(id).copyTo(fos);
            }
        }
        treeWalk.close();
        revWalk.close();
        return new File(baseDir, vcsProperties.getServiceTemplatePath(serviceId));
    }

    @SneakyThrows
    private byte[] getBytesFromTag(ObjectId tag, String filename) {
        byte[] content = null;
        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit commit = revWalk.parseCommit(tag);
            RevTree tree = commit.getTree();
            var configFile = PathFilter.create(filename);
            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(tree);
                treeWalk.setRecursive(true);
                treeWalk.setFilter(configFile);
                if (!treeWalk.next()) {
                    throw new VcsException("Did not find expected file: " + filename);
                }
                ObjectId objectId = treeWalk.getObjectId(0);
                ObjectLoader loader = repository.open(objectId);
                content = loader.getBytes();
            }
            revWalk.dispose();
        }
        return content;
    }

    @SneakyThrows
    public File createZipFor(String objectId, String serviceId) {
        File serviceDir = getServiceTemplatePackageDir(objectId, serviceId);

        var zipFile = Files.createTempFile(serviceId + "_output", ".zip").toFile();
        log.info(zipFile.getAbsolutePath());
        ZipUtil.pack(serviceDir, zipFile);
        return zipFile;
    }

    private TreeFilter createFilter(String serviceTemplatePath) {
        if (StringUtils.isEmpty(serviceTemplatePath))
            return TreeFilter.ALL;
        return PathFilter.create(serviceTemplatePath);
    }

    @PostConstruct
    public void init() {
        repository = git.getRepository();
    }

    @SneakyThrows
    private GitTagDto apply(Ref tag, RevWalk revWalk) {
        RevCommit commit = revWalk.parseCommit(tag.getTarget().getObjectId());
        return GitTagDto.builder()
                .objectId(tag.getObjectId().getName())
                .name(tag.getName())
                .description(commit.getFullMessage())
                .timestamp(Instant.ofEpochSecond(commit.getCommitTime()))
                .build();
    }

    @Override
    public Map<String, String> getServicesList(String tag) {
        if (Boolean.TRUE.equals(vcsProperties.getConfigSourceEnabled())) {
            return getServicesListFromConfig();
        }
        int serviceCodeIndex = 0;
        int serviceFileIndex = 1;
        Ref gitTag = getTagByName(tag);
        if (isNull(gitTag)) {
            throw new VcsException("version is wrong!");
        }
        byte[] configFileBytes = getBytesFromTag(gitTag.getObjectId(), vcsProperties.getAllServicesConfigFile());
        String[] serviceMappings = new String(configFileBytes).split("\\n");
        Map<String, String> serviceCodeToFileMap = new HashMap<>();
        for (String serviceMapping : serviceMappings) {
            String[] serviceCodeAndFile = serviceMapping.split(":");
            if (serviceCodeAndFile.length == 2) {
                serviceCodeToFileMap.put(serviceCodeAndFile[serviceCodeIndex], serviceCodeAndFile[serviceFileIndex]);
            }
        }
        return serviceCodeToFileMap;
    }

    @Override
    public Map<String, String> getServicesListFromConfig() {
        return servicesFromConfig;
    }
}
