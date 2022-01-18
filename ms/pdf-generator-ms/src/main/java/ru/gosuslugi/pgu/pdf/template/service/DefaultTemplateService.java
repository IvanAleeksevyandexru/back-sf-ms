package ru.gosuslugi.pgu.pdf.template.service;

import ru.gosuslugi.pgu.dto.pdf.DescriptorStructure;
import ru.gosuslugi.pgu.dto.pdf.RenderRequestDto;
import ru.gosuslugi.pgu.dto.pdf.RenderTemplateResponse;
import ru.gosuslugi.pgu.dto.SpDescriptionSection;

/**
 * Загружает шаблон для автоматический генерации PDF в случае, если основной шаблон для услуги не найден
 */
public interface DefaultTemplateService {

    RenderTemplateResponse renderDefaultPdf(RenderRequestDto renderRequest);

    DescriptorStructure prepareTemplateContext(SpDescriptionSection descriptor);

}
