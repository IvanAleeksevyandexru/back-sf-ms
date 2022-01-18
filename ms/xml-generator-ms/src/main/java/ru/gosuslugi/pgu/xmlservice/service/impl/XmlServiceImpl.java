package ru.gosuslugi.pgu.xmlservice.service.impl;

import ru.gosuslugi.pgu.common.rendering.render.data.RenderRequest;
import ru.gosuslugi.pgu.common.rendering.render.service.RenderService;
import ru.gosuslugi.pgu.dto.ApplicantRole;
import ru.gosuslugi.pgu.xmlservice.context.data.TemplateDataContext;
import ru.gosuslugi.pgu.xmlservice.context.service.TemplateDataService;
import ru.gosuslugi.pgu.xmlservice.data.GenerateXmlRequest;
import ru.gosuslugi.pgu.xmlservice.data.StoreResponse;
import ru.gosuslugi.pgu.xmlservice.service.XmlService;
import ru.gosuslugi.pgu.xmlservice.storing.service.FileStoreService;
import ru.gosuslugi.pgu.xmlservice.validation.service.ValidationService;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class XmlServiceImpl implements XmlService {
    private static final String ORDER_ID_KEY = "orderId";
    private static final String CONTEXT_KEY = "context";
    private final RenderService renderService;
    private final TemplateDataService dataService;
    private final ValidationService<GenerateXmlRequest> requestValidator;
    private final ValidationService<String> xmlValidator;
    private final FileStoreService storeService;
    private final ThreadLocal<TemplateDataContext> templateContext = new ThreadLocal<>();

    @Override
    public String generate(GenerateXmlRequest request) {
        requestValidator.validate(request);
        TemplateDataContext contextData = dataService.prepare(request);
        templateContext.set(contextData);
        RenderRequest renderRequest = buildRenderRequest(contextData);
        final String fileContent = renderService.render(renderRequest);
        xmlValidator.validate(fileContent);
        return fileContent;
    }

    @Override
    public byte[] generateStream(GenerateXmlRequest request) {
        return generate(request).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public StoreResponse generateAndStore(GenerateXmlRequest request) {
        byte[] fileContent = generateStream(request);
        return storeService.store(fileContent, templateContext.get());
    }

    private RenderRequest buildRenderRequest(TemplateDataContext contextData) {
        val renderRequest = new RenderRequest();
        final Map<String, Object> context = new HashMap<>();
        context.putAll(contextData.getValues());
        context.putAll(contextData.getAdditionalValues());
        context.putAll(contextData.getServiceParameters());
        renderRequest.setTemplateFileName(computeTemplateFileName(contextData));
        renderRequest.getContext().putAll(context);
        renderRequest.getContext().put(CONTEXT_KEY, context);
        renderRequest.getContext().put(ORDER_ID_KEY, contextData.getOrderId());
        renderRequest.setServiceId(contextData.getServiceId());
        return renderRequest;
    }

    private String computeTemplateFileName(TemplateDataContext context) {
        return context.getFileDescription()
                .getTemplates()
                .get(ApplicantRole.valueOf(context.getRoleId()));
    }
}
