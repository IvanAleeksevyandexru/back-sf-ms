package ru.gosuslugi.pgu.sp.adapter.service;

import ru.gosuslugi.pgu.dto.pdf.RenderRequestDto;

/**
 * Base service for working with vm files: loading, inserting data into velocity context etc
 */
public interface RenderService {
    String executeTemplate(RenderRequestDto renderRequest, boolean ignoreErrors);
}
