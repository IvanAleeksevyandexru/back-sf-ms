package ru.gosuslugi.pgu.service.publisher.vcs;

import ru.gosuslugi.pgu.service.publisher.vcs.dto.GitTagDto;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface VcsService {

    Map<String, String> getServicesList(String tag);
    Map<String, String> getServicesListFromConfig();
    List<GitTagDto> getVersions();

    byte[] getDescriptor(String versionTag, String serviceId);
    File getTemplates(String versionTag, String serviceId);
    byte[] getConfig(String versionTag, String serviceId);

    void refresh();

}
