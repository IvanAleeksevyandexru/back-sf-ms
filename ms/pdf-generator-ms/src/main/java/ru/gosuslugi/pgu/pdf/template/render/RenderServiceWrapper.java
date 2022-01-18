package ru.gosuslugi.pgu.pdf.template.render;

import ru.gosuslugi.pgu.dto.pdf.RenderRequestDto;
import ru.gosuslugi.pgu.dto.pdf.RenderTemplateResponse;

/**
 * Base service for working with vm files: loading, inserting data into velocity context etc
 */
public interface RenderServiceWrapper {

    RenderTemplateResponse render(RenderRequestDto renderRequest);
}
