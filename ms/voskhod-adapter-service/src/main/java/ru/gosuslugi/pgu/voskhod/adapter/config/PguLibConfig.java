package ru.gosuslugi.pgu.voskhod.adapter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.remoting.jaxws.JaxWsPortProxyFactoryBean;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.uddi.v3_service.UDDIInquiryPortType;
import ru.atc.carcass.common.spring.AppContextUtil;
import ru.atc.carcass.common.ws.JaxWSClientFactoryImpl;
import ru.atc.carcass.common.ws.JaxWsClientFactory;
import ru.atc.carcass.common.ws.WsClientFactoryBean;
import ru.atc.idecs.config.ws.ConfigService;
import ru.atc.idecs.integration.ws.esep.internal.InternalESEPIntegrationService;
import ru.gosuslugi.pgu.common.core.exception.EntityNotFoundException;
import ru.gosuslugi.pgu.common.core.exception.ExternalServiceException;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.common.core.interceptor.creator.RestTemplateCreator;
import ru.gosuslugi.pgu.common.esia.search.service.impl.CacheImpl;
import ru.gosuslugi.pgu.voskhod.adapter.properties.PguProperties;
import ru.nvg.idecs.storageservice.ws.common.data.DataService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableConfigurationProperties(PguProperties.class)
public class PguLibConfig {

    @Bean
    public AppContextUtil appContextUtil(ApplicationContext context) {
        var util = new AppContextUtil();
        util.setApplicationContext(context);
        return util;
    }

    @Bean
    public RestTemplate restTemplate(ConfigurableEnvironment env, RestTemplateCustomizer... customizers) {
        RestTemplate restTemplate = RestTemplateCreator.create("rest-client", objectMapper(), env, customizers);
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return response.getStatusCode() != HttpStatus.OK;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                final HttpStatus status = response.getStatusCode();
                final String body = IOUtils.toString(response.getBody(), StandardCharsets.UTF_8);
                if (status == HttpStatus.NOT_FOUND) {
                    throw new EntityNotFoundException("External entity not found. Response code: 404 NOT_FOUND; body: " + body);
                }
                throw new ExternalServiceException("Error response from external service. Response code: "
                        + status + "; Body: " + body);
            }
        });
        return restTemplate;
    }

    @Bean
    @DependsOn("appContextUtil")
    public JaxWsPortProxyFactoryBean uddiService(PguProperties properties) throws MalformedURLException {
        JaxWsPortProxyFactoryBean proxyFactoryBean = new JaxWsPortProxyFactoryBean();
        proxyFactoryBean.setServiceInterface(org.uddi.v3_service.UDDIInquiryPortType.class);
        proxyFactoryBean.setServiceName("UDDIInquiryService");
        proxyFactoryBean.setLookupServiceOnStartup(false);

        proxyFactoryBean.setWsdlDocumentUrl(
            new URL(properties.getUddiUrl()));
        return proxyFactoryBean;
    }

    @Bean
    public JaxWSClientFactoryImpl jaxWsClientFactory(JaxWsPortProxyFactoryBean uddi) {
        JaxWSClientFactoryImpl jaxWSClientFactory = new JaxWSClientFactoryImpl();
        //TODO: default cache not сompatible with spring boot
        CacheImpl cache = new CacheImpl();
        jaxWSClientFactory.setCacheControlService(cache);
        jaxWSClientFactory.setUddiService((UDDIInquiryPortType)uddi.getObject());
        return jaxWSClientFactory;
    }

    @Bean
    public ConfigService configService(JaxWsClientFactory factory) throws Exception {
        WsClientFactoryBean factoryBean = new WsClientFactoryBean();
        factoryBean.setUddiKey("uddi:gosuslugi.ru:services:config/configservice");
        factoryBean.setTargetClass(ru.atc.idecs.config.ws.ConfigService.class);
        factoryBean.setJaxWsClientFactory(factory);
        factoryBean.setPortName("ConfigServiceSOAP");
        factoryBean.setWsdlDocumentUrl("classpath:META-INF/wsdl/ConfigService.wsdl");

        return (ConfigService) factoryBean.getObject();
    }

    @Bean
    public DataService dataService(JaxWsClientFactory factory) throws Exception {
        WsClientFactoryBean factoryBean = new WsClientFactoryBean();
        factoryBean.setUddiKey("uddi:gosuslugi.ru:services:storage/dataservice");
        factoryBean.setTargetClass(ru.nvg.idecs.storageservice.ws.common.data.DataService.class);
        factoryBean.setJaxWsClientFactory(factory);
        factoryBean.setPortName("DataServiceSOAP");
        factoryBean.setWsdlDocumentUrl("classpath:META-INF/wsdl/common/DataService.wsdl");

        return (DataService) factoryBean.getObject();
    }

    @Bean
    public InternalESEPIntegrationService internalESEPIntegrationService(JaxWsClientFactory factory) throws Exception {
        WsClientFactoryBean factoryBean = new WsClientFactoryBean();
        factoryBean.setUddiKey("uddi:gosuslugi.ru:services:esep/internal/esepservice");
        factoryBean.setTargetClass(ru.atc.idecs.integration.ws.esep.internal.InternalESEPIntegrationService.class);
        factoryBean.setJaxWsClientFactory(factory);
        factoryBean.setPortName("InternalESEPIntegrationServiceSOAP");
        factoryBean.setWsdlDocumentUrl("classpath:META-INF/wsdl/esep/InternalESEPIntegrationService.wsdl");

        return (InternalESEPIntegrationService) factoryBean.getObject();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonProcessingUtil.getObjectMapper();
    }

}
