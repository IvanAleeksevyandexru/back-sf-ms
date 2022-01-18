package ru.gosuslugi.pgu.sp.adapter.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.dto.pdf.RenderRequestDto;
import ru.gosuslugi.pgu.sp.adapter.exceptions.SpAdapterInputDataException;
import ru.gosuslugi.pgu.sp.adapter.exceptions.SpAdapterNotFoundException;
import ru.gosuslugi.pgu.sp.adapter.placeholder.ContextGetCollector;
import ru.gosuslugi.pgu.sp.adapter.service.RenderService;
import ru.gosuslugi.pgu.sp.adapter.service.TemplatePackageService;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@Slf4j
@RequiredArgsConstructor
public class RenderServiceImpl implements RenderService {

    protected static final String DATE_TOOL_VAR_NAME = "dateTool";
    protected static final String DATE_SERVICE_VAR_NAME = "dateService";
    protected static final String STRING_SERVICE_VAR_NAME = "stringService";
    protected static final String XML_SERVICE_VAR_NAME = "xmlService";
    public static final String CONSOLE_NAME = "console";
    protected static final String ADDRESS_SERVICE_VAR_NAME = "addressService";
    protected static final String MATH_TOOL_VAR_NAME = "mathTool";
    protected static final String DATE_COMPARE_TOOL_VAR_NAME = "compareDateTool";
    protected static final String STRING_TOOL_VAR_NAME = "strTool";
    protected static final String INTEGER_VAR_NAME = "Integer";
    protected static final String TEMPLATE_EXTENSION = ".vm";

    protected static final String SERVICE_INFO_KEY = "serviceInfo";

    private final VelocityEngine engine;

    private final TemplatePackageService templatePackageService;

    private final VelocityContext prototypeTemplateContext;

    public String executeTemplate(RenderRequestDto renderRequest, boolean ignoreErrors) {
        VelocityContext context = prepareTemplateContext(renderRequest);
        Template template = loadTemplate(renderRequest);

        if (template == null) {
            if (ignoreErrors) {
                log.info("Cannot retrieve template for serviceId: {} and orderId: {} and templateFileName: {}",
                    renderRequest.getServiceId(), renderRequest.getOrderId(), renderRequest.getTemplateFileName());
            } else {
                throw new SpAdapterInputDataException(
                    "Template with name is missing " + renderRequest.getTemplateFileName() + TEMPLATE_EXTENSION);
            }
            return null;
        }

        return mergeTemplateContext(renderRequest, template, context, ignoreErrors);
    }

    private Template loadTemplate(RenderRequestDto renderRequest) {
        ReentrantReadWriteLock lock = templatePackageService.getLockForService(renderRequest.getServiceId());
        lock.readLock().lock();
        String templateFileName = renderRequest.getTemplateFileName().endsWith(TEMPLATE_EXTENSION)
                ? renderRequest.getTemplateFileName()
                : renderRequest.getTemplateFileName() + TEMPLATE_EXTENSION;
        try {
            log.info("Loading template with name {} for orderId {}", templateFileName, renderRequest.getOrderId());
            return engine.getTemplate(templateFileName, "UTF-8");
        } catch (ResourceNotFoundException e) {
            if(renderRequest.getRequired()) {
                log.error("Error getting template file {} for service with id {}", templateFileName, renderRequest.getServiceId(), e);
                throw new SpAdapterInputDataException(String.format("Error getting template file %s for service with id %s", templateFileName, renderRequest.getServiceId()));
            }
        } catch (ParseErrorException e) {
            if(renderRequest.getRequired()) {
                log.error("Velocity template parse error for file {}", renderRequest.getTemplateFileName(), e);
                throw new SpAdapterNotFoundException(String.format("Velocity template parse error for file %s", templateFileName));
            }
        } finally {
            lock.readLock().unlock();
        }
        return null;
    }

    private String mergeTemplateContext(RenderRequestDto renderRequest, Template template, VelocityContext context, boolean ignoreErrors) {
        ContextGetCollector wrappedContext = new ContextGetCollector(context);

        StringWriter writer = new StringWriter();
        template.merge(wrappedContext, writer);

        if (!wrappedContext.getLiteralKeys().isEmpty()) {
            List<String> existedPlaceholders = wrappedContext.getLiteralKeys();
            if (ignoreErrors) {
                log.error("При генерации из шаблона {} с orderId {} в тексте остались следущие плейсхолдеры: {}. SpAdapterInputDataException исключение будет выброшено", renderRequest.getTemplateFileName()+TEMPLATE_EXTENSION, renderRequest.getOrderId(), existedPlaceholders);
            } else {
                throw new SpAdapterInputDataException("Проверка сгенеренной XML: остались плейсхолдеры - " + existedPlaceholders);
            }
        }
        return writer.toString();
    }

    /**
     * Method injects all additional values into velocity context
     * @param values
     * @param context
     * @return
     */
    private void addParametersToVelocityContext(Map<String, Object> values, VelocityContext context) {
        values.forEach((key, val) -> context.put(key, val));
    }

    private VelocityContext prepareTemplateContext(RenderRequestDto renderRequest) {
        final VelocityContext context = new VelocityContext(prototypeTemplateContext);
        context.put("orderId", renderRequest.getOrderId());
        addParametersToVelocityContext(renderRequest.getValues(), context);
        context.put("context", renderRequest.getValues());
        return context;
    }

}
