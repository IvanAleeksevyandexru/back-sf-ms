package ru.gosuslugi.pgu.voskhod.adapter.service.esep.impl;

import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfstring;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.CreateUIToSingRequest;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.CreateUIToSingResponse;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.Credentials;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.ErrorDataItem;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.FileCertificateUserInfo;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.GetFileCertificatesUserInfoRequest;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.GetFileCertificatesUserInfoResponse;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.RequestResult;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts_esepservice.ClientSignatureFormat;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts_esepservice.ClientSigningMode;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts_esepservice.ServerSignatureFormat;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts_esepservice.SignatureKind;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.tempuri.EsepService;
import org.tempuri.IEsepService;
import ru.atc.carcass.common.exception.FaultException;
import ru.atc.idecs.config.util.ConfigUtil;
import ru.atc.idecs.config.ws.ConfigService;
import ru.gosuslugi.pgu.common.core.exception.ExternalServiceException;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.common.logging.service.SpanService;
import ru.gosuslugi.pgu.voskhod.adapter.service.esep.EsepServiceHelper;
import ru.gosuslugi.pgu.voskhod.adapter.service.esep.impl.model.IEsepServiceMethod;
import ru.nvg.idecs.storageservice.ws.types.EsepFile;
import ru.nvg.idecs.uddi.ServiceClientProxy;
import ru.nvg.idecs.uddi.UDDINames;
import ru.nvg.idecs.uddi.ws.client.impl.JuddiServiceLocator;

import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.util.List;
import java.util.Map;

/**
 * Сервис для взаимодействия с EsepService
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "esep-service.enabled", matchIfMissing = true)
@RequiredArgsConstructor
public class EsepServiceHelperImpl implements EsepServiceHelper {

    @Value("${ru.nvg.idecs.uddi.inquiryUrl}")
    private String inquiryUrl;

    @Value("${config-service.enabled:true}")
    private Boolean configServiceEnabled;

    @Value("${esep.login}")
    private String esepLogin;

    @Value("${esep.password}")
    private String esepPassword;

    private final ConfigService configService;
    private final SpanService spanService;

    private static final String ESEP_SERVICE_WSDL_PATH = "/META-INF/wsdl/esep/EsepService.wsdl";
    private static final String GET_CERTIFICATES_USER_INFO = "http://schemas.granit.ru/esep/IEsepService/GetFileCertificatesUserInfo";
    private static final String CREATE_UI_TO_SIGN_EX = "http://schemas.granit.ru/esep/IEsepService/CreateUIToSingEx";

    private static final String ESEP_CONFIG_PREFIX = "esep";
    private static final String ESEP_CONFIG_LOGIN = ESEP_CONFIG_PREFIX + ".login";
    private static final String ESEP_CONFIG_PASSWORD = ESEP_CONFIG_PREFIX + ".password";

    private EsepService serviceInstance;
    private static final ThreadLocal<IEsepService> threadLocalCreateUIToSignEx = new ThreadLocal<>();
    private static final ThreadLocal<IEsepService> threadLocalGetCertificatesUserInfo = new ThreadLocal<>();

    @PostConstruct
    public void init() {
        System.setProperty(JuddiServiceLocator.UDDI_INQUIRY_WSDL_URL_PROPERTY, inquiryUrl);
        serviceInstance = new EsepService(getClass().getResource(ESEP_SERVICE_WSDL_PATH),
            new QName("http://tempuri.org/", "EsepService"));
    }

    @Override
    public CreateUIToSingResponse createUIToSingEx(String returnUrl, List<EsepFile> fileAccessCodes) {
        final CreateUIToSingRequest signRequest = createUIToSingRequest(returnUrl, fileAccessCodes);
        final IEsepService esepService = createIEsepService(IEsepServiceMethod.createUIToSignEx);
        final CreateUIToSingResponse signResponse = spanService.runExternalService(
                "esepService.createUIToSingEx",
                "esepService.createUIToSingEx",
                () -> esepService.createUIToSingEx(signRequest),
                Map.of("returnUrl", returnUrl),
                JsonProcessingUtil.toJson(signRequest));
        final RequestResult requestResult = signResponse.getRequestResult();
        if (requestResult == null || requestResult.isWasSuccessful() == null || !requestResult.isWasSuccessful()) {
            StringBuilder messageBuilder = new StringBuilder("Can't initialise ESEP sign process");
            if (requestResult != null) {
                messageBuilder.append(": ").append(requestResult.getErrorCode())
                        .append(": ").append(requestResult.getErrorMessage()).append(": ");
                if (requestResult.getErrorData() != null) {
                    for (ErrorDataItem errorData : requestResult.getErrorData().getErrorDataItem()) {
                        messageBuilder.append(errorData.getKey()).append("=").append(errorData.getValue()).append(", ");
                    }
                    messageBuilder.setLength(messageBuilder.length() - 2);
                }
            }
            throw new ExternalServiceException(messageBuilder.toString());
        }
        return signResponse;
    }

    @Override
    public List<FileCertificateUserInfo> getFileCertificateUserInfos(List<String> fileAccessCodes) {
        Credentials credentials = createCredentials();

        final GetFileCertificatesUserInfoRequest request = new GetFileCertificatesUserInfoRequest();
        request.setCredentials(credentials);
        request.setFileAccessCodes(new ArrayOfstring());
        request.getFileAccessCodes().getString().addAll(fileAccessCodes);
        final IEsepService esepService = createIEsepService(IEsepServiceMethod.getFileCertificatesUserInfo);

        final GetFileCertificatesUserInfoResponse response = spanService.runExternalService(
                "esepService.createUIToSingEx",
                "esepService.createUIToSingEx",
                () -> esepService.getFileCertificatesUserInfo(request),
                Map.of(),
                JsonProcessingUtil.toJson(request));

        if (!response.getRequestResult().isWasSuccessful()) {
            throw new ExternalServiceException(String.format("Error code: %s, message: %s",
                    response.getRequestResult().getErrorCode(), response.getRequestResult().getErrorMessage()));
        }

        return response.getFileCertificateUserInfos().getFileCertificateUserInfo();
    }


    private IEsepService createIEsepService(IEsepServiceMethod method) {
        ThreadLocal<IEsepService> threadLocal = (method == IEsepServiceMethod.createUIToSignEx ?
            threadLocalCreateUIToSignEx : threadLocalGetCertificatesUserInfo);
        final IEsepService iEsepService = threadLocal.get();

        if (iEsepService == null) {
            String methodAction = (method == IEsepServiceMethod.createUIToSignEx ? CREATE_UI_TO_SIGN_EX : GET_CERTIFICATES_USER_INFO);
            final IEsepService esepService = serviceInstance.getBasicHttpBindingIEsepService();
            ServiceClientProxy.setEndpoint(UDDINames.NAME_ESEP_SIGN_SERVICE, esepService);

            ((BindingProvider) esepService).getRequestContext().putAll(
                Map.of(
                    BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE,
                    BindingProvider.SOAPACTION_URI_PROPERTY, methodAction,
                    "org.jboss.ws.timeout", 2000,
                    "com.sun.xml.internal.ws.request.timeout", 2000,
                    "com.sun.xml.internal.ws.connect.timeout", 2000
                ));

            threadLocal.set(esepService);
        }

        return threadLocal.get();
    }

    private Credentials createCredentials() {
        if (configServiceEnabled) {
            String login = spanService.runExternalService(
                    "configService.getStrParameter request ESEP login",
                    "configService.getStrParameter",
                    () -> ConfigUtil.getStrParameter(configService, ESEP_CONFIG_LOGIN, null),
                    Map.of("key", ESEP_CONFIG_LOGIN));
            String password = spanService.runExternalService(
                    "configService.getStrParameter request ESEP password",
                    "configService.getStrParameter",
                    () -> ConfigUtil.getStrParameter(configService, ESEP_CONFIG_PASSWORD, null),
                    Map.of("key", ESEP_CONFIG_PASSWORD));
            if (StringUtils.hasText(login) && StringUtils.hasText(password)) {
                Credentials credentials = new Credentials();
                credentials.setLogin(login);
                credentials.setPassword(password);
                return credentials;
            }

            throw new FaultException("Can't get login/password from esep config service");
        }

        Credentials credentials = new Credentials();
        credentials.setLogin(esepLogin);
        credentials.setPassword(esepPassword);
        return credentials;
    }

    public CreateUIToSingRequest createUIToSingRequest(String returnUrl, List<EsepFile> fileAccessCodes) {
        final CreateUIToSingRequest signRequest = new CreateUIToSingRequest();
        Credentials credentials = createCredentials();

        signRequest.setCredentials(credentials);
        signRequest.setClienSignatureFormat(ClientSignatureFormat.CMS);
        signRequest.setClientSignatureKind(SignatureKind.DETACHED);
        signRequest.setClientSigningMode(ClientSigningMode.BATCH);
        signRequest.setServerSignRequired(false);
        signRequest.setServerSignatureFormat(ServerSignatureFormat.CMS);
        signRequest.setReturnUrl(returnUrl);
        signRequest.setFileAccessCodes(new ArrayOfstring());

        for (EsepFile esepFile : fileAccessCodes) {
            signRequest.getFileAccessCodes().getString().add(esepFile.getFileAccessCode());
        }
        return signRequest;
    }
}