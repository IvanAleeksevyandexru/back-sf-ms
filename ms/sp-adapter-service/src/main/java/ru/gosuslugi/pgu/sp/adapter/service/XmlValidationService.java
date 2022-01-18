package ru.gosuslugi.pgu.sp.adapter.service;

/**
 * XML tags Validation Service
 */
public interface XmlValidationService {

    /**
     * Validates xml string for tags consistency
     * @param xml - XML as string
     */
    void validate(String xml);

}
