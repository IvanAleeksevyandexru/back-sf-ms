package ru.gosuslugi.pgu.pdf.template.model.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gosuslugi.pgu.dto.ApplicantRole;
import ru.gosuslugi.pgu.dto.PdfFilePackage;
import ru.gosuslugi.pgu.dto.pdf.DescriptorStructure;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ru.gosuslugi.pgu.pdf.template.service.impl.TemplatesDataContextServiceImpl.SP_REQUEST_GUID;
import static ru.gosuslugi.pgu.pdf.template.service.impl.TemplatesDataContextServiceImpl.SP_REQUEST_HASH;


@Data
@NoArgsConstructor
public class TemplatesDataContext {

    public static final String MAIN_ROLE_NAME = "Applicant";
    public static final String EMPOWERMENT_ID_PARAM = "empowerment";
    static public final String ORG_EMPOWERMENTS_ATTR_NAME = "orgEmpowerments";

    private String serviceId;
    private Long orderId;
    private ApplicantRole roleId;
    private Long oid;
    private Long orgId;
    private String authorityId;
    private Map<String, Object> values = new HashMap<>();
    private Map<String, Object> additionalValues = new HashMap<>();
    private Set<String> attachments = new HashSet<>();
    private Set<String> generatedFiles = new HashSet<>();
    //TODO объединить в одну спеку, в которой в явном виде управлять какие файлы с какой стратегией генерить
    /** Название вложения бизнес-xml */
    @Deprecated
    private String businessXmlName;
    /** Название вложения дополнительной pdf-ки - потом объединить со осовными*/
    private Map<String, Map<String, String>> additionalPdfs;
    /** Добавочные заголовки для запроса в sp-service */
    private Map<String, String> replacedHeaders;
    /** строка на которую меняется serviceId при вызове sp */
    private String serviceCustomId;
    /** общие параметры из ServiceDescriptor, которые влияют на отправку и отображение услуги */
    private Map<String, String> serviceParameters = new HashMap<>();

    private Boolean alwaysAttachServicePdf;

    /** Файлы для упаковки в PDF */
    private Set<PdfFilePackage> packageToPdf;
    /** Флаг отправки req_preview.pdf в SMEV*/
    @Deprecated
    private boolean regPreviewSendToSP = false;
    /** UIN переиспользования пошлины */
    private String reusePaymentUin;

    /** Генерируемые файлы. */
    private List<FileDescription> files;

    /** Структура полей услуги для генерации дефолтной PDF, если потребуется  */
    private DescriptorStructure descriptorStructure;

    public String getRequestGuid() {
        Object guid = additionalValues.get(SP_REQUEST_GUID);
        return (guid != null)? guid.toString() : null;
    }

    public String getRequestHash() {
        Object guid = additionalValues.get(SP_REQUEST_HASH);
        return (guid != null)? guid.toString() : null;
    }

}
