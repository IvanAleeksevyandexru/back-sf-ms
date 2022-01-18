package ru.gosuslugi.pgu.sp.adapter.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.dto.pdf.RenderRequestDto;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.dto.pdf.data.FileType;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;
import ru.gosuslugi.pgu.sp.adapter.service.AbstractTemplateRenderService;
import ru.gosuslugi.pgu.sp.adapter.service.RenderService;
import ru.gosuslugi.pgu.sp.adapter.service.XmlTemplateRenderService;
import ru.gosuslugi.pgu.sp.adapter.types.EscaperType;

@Service
@RequiredArgsConstructor
@Slf4j
public class XmlTemplateRenderServiceImpl extends AbstractTemplateRenderService implements XmlTemplateRenderService {

    private final RenderService renderService;

    @Override
    public String getXmlRequest(TemplatesDataContext dataContext, FileDescription options) {
        return getXmlRequest(dataContext, options, false);
    }

    @Override
    public String getXmlRequest(TemplatesDataContext dataContext, FileDescription options, boolean ignoreErrors) {
        if (options == null) {
            log.error("options for create XML is null.");
            return null;
        }
        if (options.getType() != FileType.XML && options.getType() != FileType.REQUEST) {
            log.error("type in options for create XML is not \"{}\" or \"{}\".", FileType.XML.getValue(), FileType.REQUEST.getValue());
            return null;
        }
        String templateFileName = options.getTemplates().get(dataContext.getRoleId());
        if (StringUtils.isEmpty(templateFileName)) {
            log.warn("not defined name template in SpConfig option for role {}", dataContext.getRoleId());
        }

        boolean required = options.getType() == FileType.REQUEST;
        RenderRequestDto renderRequest = getRenderRequest(dataContext, templateFileName, required, EscaperType.XML);
        return renderService.executeTemplate(renderRequest, ignoreErrors);
    }

}
