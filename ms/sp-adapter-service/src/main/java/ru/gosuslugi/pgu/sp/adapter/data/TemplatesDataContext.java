package ru.gosuslugi.pgu.sp.adapter.data;

import org.springframework.util.StringUtils;
import ru.atc.carcass.security.rest.model.orgs.OrgType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import ru.gosuslugi.pgu.dto.ApplicantRole;
import ru.gosuslugi.pgu.dto.PdfFilePackage;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.gosuslugi.pgu.sp.adapter.service.impl.TemplatesDataContextServiceImpl.*;

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
    private Boolean skip17Status;
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

    public String getRequestGuid() {
        Object guid = additionalValues.get(SP_REQUEST_GUID);
        return (guid != null)? guid.toString() : null;
    }

    public String getRequestHash() {
        Object guid = additionalValues.get(SP_REQUEST_HASH);
        return (guid != null)? guid.toString() : null;
    }

    public void setRequestGuid(String guid) {
        additionalValues.put(SP_REQUEST_GUID, guid);
    }

    public void setRequestHash(String hash) {
        additionalValues.put(SP_REQUEST_HASH, hash);
    }

    public String getSystemAuthority() {
        Object systemAuthority = additionalValues.get(SYSTEM_AUTHORITY_ATTR_NAME);
        return (systemAuthority != null) ? systemAuthority.toString() : null;
    }

    public boolean isAlwaysAttachServicePdf() {
        return alwaysAttachServicePdf != null && alwaysAttachServicePdf;
    }

    /** Возвращает название типа организации
     * @see ru.atc.carcass.security.rest.model.orgs.OrgType
     * @return тип регистрации огранизации (АО, ИП, ОО...)
     */
    public OrgType getOrgType() {
        val orgTypeStr = (String) additionalValues.get(ORG_TYPE_ATTR_NAME);
        return orgTypeStr == null ? null : OrgType.valueOf(orgTypeStr);
    }

    public Set<String> getEmpowerments() {
        HashSet<String> empowerments = new HashSet<>();
        if (additionalValues.containsKey(ORG_EMPOWERMENTS_ATTR_NAME)) {
            empowerments = new HashSet<>(Arrays.asList(
                    additionalValues.get(ORG_EMPOWERMENTS_ATTR_NAME)
                            .toString().split(",")
            ));
        }
        return empowerments;
    }
    public List<String> getRequiredEmpowerments() {
        String empowerments = serviceParameters.get(EMPOWERMENT_ID_PARAM);
        if (StringUtils.hasText(empowerments)) {
            return Arrays.stream(empowerments.split(",")).map(String::trim).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
