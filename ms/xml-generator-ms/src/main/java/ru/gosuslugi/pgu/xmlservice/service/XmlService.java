package ru.gosuslugi.pgu.xmlservice.service;

import ru.gosuslugi.pgu.xmlservice.data.GenerateXmlRequest;
import ru.gosuslugi.pgu.xmlservice.data.StoreResponse;


public interface XmlService {

    /**
     * Создает файл XML.
     *
     * @param request запрос.
     * @return содержимое XML-файла.
     */
    String generate(GenerateXmlRequest request);

    /**
     * Создает файл XML.
     *
     * @param request запрос.
     * @return содержимое XML-файла.
     */
    byte[] generateStream(GenerateXmlRequest request);

    /**
     * Создает файл XML и сохраняет его в хранилище.
     *
     * @param request запрос.
     * @return ответ, содержащий результат сохранения.
     */
    StoreResponse generateAndStore(GenerateXmlRequest request);
}
