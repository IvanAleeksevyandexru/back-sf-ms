package ru.gosuslugi.pgu.service.descriptor.storage.test.model;

import lombok.Data;

import java.util.Map;

@Data
public class DescriptorForTests {
    private String businessXmlName;
    private Map<String, String> replacedHeaders;
    private String serviceCustomId;
    private Boolean alwaysAttachServicePdf;
}
