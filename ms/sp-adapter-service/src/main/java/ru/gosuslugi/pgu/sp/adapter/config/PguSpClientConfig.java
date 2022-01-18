package ru.gosuslugi.pgu.sp.adapter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.remoting.jaxws.JaxWsPortProxyFactoryBean;
import org.springframework.web.client.RestTemplate;
import org.uddi.v3_service.UDDIInquiryPortType;
import ru.atc.carcass.common.spring.AppContextUtil;
import ru.atc.carcass.common.ws.JaxWSClientFactoryImpl;
import ru.gosuslugi.pgu.common.core.attachments.AttachmentService;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.common.core.interceptor.creator.RestTemplateCreator;
import ru.gosuslugi.pgu.common.esia.search.service.UddiService;
import ru.gosuslugi.pgu.common.esia.search.service.impl.CacheImpl;
import ru.gosuslugi.pgu.common.logging.service.SpanService;
import ru.gosuslugi.pgu.sp.adapter.properties.PguClientProperties;
import ru.gosuslugi.pgu.sp.adapter.types.PackageProcessingStatus;
import ru.gosuslugi.pgu.terrabyte.client.TerrabyteClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration class for service-processing client integration
 * Beans are taken from xml-beans from pgu projects configurations (sf, delirium)
 * Used by ru.gosuslugi.pgu.sp.adapter.service.ServiceProcessingClient
 */
@Configuration
@EnableConfigurationProperties(PguClientProperties.class)
public class PguSpClientConfig {

    @Bean
    public Map<String, PackageProcessingStatus> packageProcessingStatusMap() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public AppContextUtil appContextUtil(ApplicationContext context) {
        var util = new AppContextUtil();
        util.setApplicationContext(context);
        return util;
    }

    @Bean
    public RestTemplate restTemplate(ConfigurableEnvironment env, RestTemplateCustomizer... customizers) {
        return RestTemplateCreator.create("rest-client", objectMapper(), env, customizers);
    }

    @Bean
    public JaxWsPortProxyFactoryBean jaxWsPortProxyFactoryBean(PguClientProperties properties) throws MalformedURLException {
        JaxWsPortProxyFactoryBean proxyFactoryBean = new JaxWsPortProxyFactoryBean();
        proxyFactoryBean.setServiceInterface(org.uddi.v3_service.UDDIInquiryPortType.class);
        proxyFactoryBean.setServiceName("UDDIInquiryService");
        proxyFactoryBean.setLookupServiceOnStartup(false);

        proxyFactoryBean.setWsdlDocumentUrl(
                new URL(properties.getUddiUrl()));
        return proxyFactoryBean;
    }

    @Bean
    public JaxWSClientFactoryImpl jaxWsClientFactory(AppContextUtil appContextUtil, JaxWsPortProxyFactoryBean uddi) {
        JaxWSClientFactoryImpl jaxWSClientFactory = new JaxWSClientFactoryImpl();
        CacheImpl cache = new CacheImpl();
        jaxWSClientFactory.setCacheControlService(cache);
        jaxWSClientFactory.setUddiService((UDDIInquiryPortType)uddi.getObject());
        return jaxWSClientFactory;
    }

    @Bean
    public UddiService uddiService(JaxWSClientFactoryImpl jaxWsClientFactory, SpanService spanService) {
        return new UddiService(jaxWsClientFactory, spanService);
    }

    @Bean
    public AttachmentService attachmentService(TerrabyteClient terrabyteClient) {
        return new AttachmentService(terrabyteClient);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonProcessingUtil.getObjectMapper();
    }

}
