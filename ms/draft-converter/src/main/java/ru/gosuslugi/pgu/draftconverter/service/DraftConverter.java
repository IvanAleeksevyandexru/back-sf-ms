package ru.gosuslugi.pgu.draftconverter.service;

import ru.gosuslugi.pgu.common.rendering.render.data.RenderRequest;
import ru.gosuslugi.pgu.draftconverter.data.ContextBuildingRequest;
import ru.gosuslugi.pgu.draftconverter.data.TemplateDataContext;

import java.util.HashMap;
import java.util.Map;

public interface DraftConverter <T, U> {
    U convert(T t);

    ContextBuildingRequest prepareContextBuildingRequest(T request);

    default RenderRequest buildRenderRequest(TemplateDataContext contextData) {
        var renderRequest = new RenderRequest();
        final Map<String, Object> context = new HashMap<>(contextData.getValues());
        context.putAll(contextData.getServiceParameters());
        renderRequest.getContext().putAll(context);
        renderRequest.getContext().put("context", context);
        renderRequest.getContext().put("xml", contextData.getXmlTree());
        renderRequest.getContext().put("json", contextData.getJsonTree());
        renderRequest.setTemplateFileName(contextData.getTemplateFileName());
        renderRequest.setServiceId(contextData.getServiceId());
        return renderRequest;
    }
}
