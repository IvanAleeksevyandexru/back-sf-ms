package ru.gosuslugi.pgu.pdf.template.render.impl;

import ru.gosuslugi.pgu.common.rendering.render.data.RenderRequest;
import ru.gosuslugi.pgu.common.rendering.render.exception.RenderTemplateException;
import ru.gosuslugi.pgu.common.rendering.render.exception.TemplateNotFoundException;
import ru.gosuslugi.pgu.common.rendering.render.service.RenderService;
import ru.gosuslugi.pgu.dto.pdf.RenderRequestDto;
import ru.gosuslugi.pgu.dto.pdf.RenderTemplateResponse;
import ru.gosuslugi.pgu.pdf.template.exception.CriticalPdfGenerationException;
import ru.gosuslugi.pgu.pdf.template.render.RenderServiceWrapper;
import ru.gosuslugi.pgu.pdf.template.service.DefaultTemplateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RenderServiceWrapperImpl implements RenderServiceWrapper {
    private final RenderService renderService;
    private final DefaultTemplateService defaultTemplateService;
    private static final String ORDER_ID_KEY = "orderId";
    private static final String CONTEXT_KEY = "context";

    @Override
    public RenderTemplateResponse render(RenderRequestDto renderRequestDto) {
        RenderTemplateResponse result = new RenderTemplateResponse();
        try {
            String rendered = renderService.render(toRenderRequest(renderRequestDto));
            result.setResultData(rendered);
        } catch (TemplateNotFoundException e) {
            if (renderRequestDto.isAllowDefaultGeneration()) {
                return defaultTemplateService.renderDefaultPdf(renderRequestDto);
            }
            if(renderRequestDto.getRequired()) {
                throw new CriticalPdfGenerationException(e.getMessage(), e);
            }
            result.setErrorInfo(e.getMessage());
        } catch (RenderTemplateException e) {
            if(renderRequestDto.getRequired()) {
                throw new CriticalPdfGenerationException(e.getMessage(), e);
            }
            result.setErrorInfo(e.getMessage());
        }
        return result;
    }

    private RenderRequest toRenderRequest(RenderRequestDto renderRequestDto) {
        final RenderRequest request = new RenderRequest();
        request.getContext().putAll(renderRequestDto.getValues());
        request.getContext().put(CONTEXT_KEY, renderRequestDto.getValues());
        request.getContext().put(ORDER_ID_KEY, renderRequestDto.getOrderId());
        request.setServiceId(renderRequestDto.getServiceId());
        request.setTemplateFileName(renderRequestDto.getTemplateFileName());
        return request;
    }
}
