package ru.gosuslugi.pgu.pdf.template.service.impl;

import static org.springframework.util.ObjectUtils.isEmpty;
import ru.gosuslugi.pgu.common.rendering.render.data.RenderRequest;
import ru.gosuslugi.pgu.common.rendering.render.exception.RenderTemplateException;
import ru.gosuslugi.pgu.common.rendering.render.exception.TemplateNotFoundException;
import ru.gosuslugi.pgu.common.rendering.render.service.RenderService;
import ru.gosuslugi.pgu.dto.SpDescriptionSection;
import ru.gosuslugi.pgu.dto.descriptor.ScreenDescriptor;
import ru.gosuslugi.pgu.dto.pdf.ComponentInfo;
import ru.gosuslugi.pgu.dto.pdf.DescriptorStructure;
import ru.gosuslugi.pgu.dto.pdf.RenderRequestDto;
import ru.gosuslugi.pgu.dto.pdf.RenderTemplateResponse;
import ru.gosuslugi.pgu.pdf.template.exception.DefaultPDFGenerationException;
import ru.gosuslugi.pgu.pdf.template.exception.PdfGenerateException;
import ru.gosuslugi.pgu.pdf.template.service.DefaultTemplateService;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

/**
 * Загружает шаблон для автоматической генерации PDF в случае, если основной шаблон для услуги не
 * найден.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultTemplateServiceImpl implements DefaultTemplateService {
    private static final String ORDER_ID_KEY = "orderId";
    private static final String CONTEXT_KEY = "context";
    private static final String TEMPLATE_EXTENSION = ".vm";
    public static final String DEFAULT_SERVICE_NAME = "default";
    public static final String HEADER_TEMPLATE =
            DEFAULT_SERVICE_NAME + File.separator + "header.vm";
    public static final String FOOTER_TEMPLATE =
            DEFAULT_SERVICE_NAME + File.separator + "footer.vm";
    private static final String SERVICE_ID_KEY = "serviceId";
    private static final String SERVICE_NAME_KEY = "serviceName";
    private final RenderService renderService;


    @Override
    public RenderTemplateResponse renderDefaultPdf(RenderRequestDto renderRequestDto) {
        RenderRequest renderRequest = prepareRenderRequest(renderRequestDto);

        String header = mergeTemplate(HEADER_TEMPLATE, renderRequest, "header", renderRequestDto.getOrderId());
        String footer = mergeTemplate(FOOTER_TEMPLATE, renderRequest, "footer", renderRequestDto.getOrderId());

        if (header == null || footer == null) {
            throw new PdfGenerateException("Header or footer is empty for default PDF generation");
        }

        String renderedComponents = renderRequestDto.getDescriptorStructure().getFieldToComponentType().values().stream()
                .map(component -> {
                    Object componentValue = renderRequest.getContext().get(component.getFieldId());
                    if (!isEmpty(componentValue)) {
                        return mergeTemplate(
                                        filenameForComponent(component.getComponentType()),
                                        prepareRenderRequestForComponent(renderRequest, componentValue, component.getScreenHeader()),
                                        component.getComponentType(),
                                        renderRequestDto.getOrderId());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(","));

        if (isEmpty(renderedComponents)) {
            return new RenderTemplateResponse(null, header + "\n" + footer);
        }
        return new RenderTemplateResponse(null, header + ",\n" + renderedComponents + footer);
    }

    @Override
    public DescriptorStructure prepareTemplateContext(SpDescriptionSection descriptor) {
        val result = new DescriptorStructure();

        result.setService(descriptor.getService());

        result.setFieldToComponentType(descriptor.getApplicationFields().stream()
                .map(field -> ComponentInfo.builder()
                        .fieldId(field.getId())
                        .componentType(field.getType().name())
                        .screenHeader(descriptor.getScreens().stream()
                                .filter(screen -> screen.getComponentIds() != null && screen.getComponentIds().contains(field.getId()))
                                .findFirst()
                                .map(ScreenDescriptor::getHeader)
                                .orElse(null))
                        .build())
                .collect(Collectors.toMap(
                        ComponentInfo::getFieldId,
                        componentInfo -> componentInfo,
                        (a, b) -> b,
                        LinkedHashMap::new)));

        return result;
    }

    private String mergeTemplate(String templateFileName, RenderRequest request, String componentType, Long orderId) {
        request.setTemplateFileName(templateFileName);
        try {
            return renderService.render(request);
        } catch (RenderTemplateException | TemplateNotFoundException e) {
            throw new DefaultPDFGenerationException(String.format("При генерации шаблона по "
                    + "умолчанию для компонента %s с orderId %s произошла ошибка: %s.",
                    componentType, orderId, e.getMessage()));
        }
    }

    private RenderRequest prepareRenderRequest(RenderRequestDto renderRequestDto) {
        final RenderRequest request = new RenderRequest();
        request.setServiceId(DEFAULT_SERVICE_NAME);
        request.getContext().putAll(renderRequestDto.getValues());
        request.getContext().put(ORDER_ID_KEY, renderRequestDto.getOrderId());
        request.getContext().put(CONTEXT_KEY, renderRequestDto.getValues());
        request.getContext().put(SERVICE_ID_KEY, renderRequestDto.getServiceId());
        if (renderRequestDto.getDescriptorStructure() != null) {
            request.getContext().put(SERVICE_NAME_KEY, renderRequestDto.getDescriptorStructure().getService());
        }
        return request;
    }

    private RenderRequest prepareRenderRequestForComponent(RenderRequest request, Object value, String screenHeader) {
        request.getContext().put("componentValue", value);
        request.getContext().put("screenHeader", screenHeader);
        return request;
    }

    private String filenameForComponent(String componentType) {
        return "components" + File.separator + componentType + TEMPLATE_EXTENSION;
    }
}
