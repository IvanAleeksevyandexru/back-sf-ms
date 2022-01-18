package ru.gosuslugi.pgu.sp.adapter.config;

import brave.Tracer;
import lombok.val;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import ru.atc.carcass.common.PKCS7SignService;
import ru.atc.carcass.security.service.impl.EsiaRestClientServiceImpl;
import ru.atc.carcass.security.service.impl.ThreadLocalTokensContainerManagerService;
import ru.gosuslugi.pgu.common.esia.search.dto.UserOrgData;
import ru.gosuslugi.pgu.common.esia.search.dto.UserPersonalData;
import ru.gosuslugi.pgu.common.logging.service.SpanService;
import ru.gosuslugi.pgu.sp.adapter.config.props.EsiaServiceProperties;

@Configuration
@EnableConfigurationProperties(EsiaServiceProperties.class)
public class PguLibConfig {
    @Bean
    public ThreadLocalTokensContainerManagerService threadLocalTokensContainerManagerService() {
        return new ThreadLocalTokensContainerManagerService();
    }

    @Bean
    public EsiaRestClientServiceImpl esiaRestClientService(EsiaServiceProperties properties) {
        EsiaRestClientServiceImpl esiaRestClientService = new EsiaRestClientServiceImpl();
        val pkcs7SignService = new PKCS7SignService(
                properties.getKeystorePasswd(),
                properties.getKeyStoreAlias(),
                properties.getKeystore(),
                properties.getCrt()
        );
        esiaRestClientService.setPkcs7SignService(pkcs7SignService);
        esiaRestClientService.setEsiaUrl(properties.getUrl());
        esiaRestClientService.setProxyUrl(properties.getProxyUrl());
        esiaRestClientService.setRedirectHost(properties.getRedirectUrl());
        esiaRestClientService.setTokensContainerManagerService(threadLocalTokensContainerManagerService());
        return esiaRestClientService;
    }

    @Bean
    @RequestScope
    UserPersonalData userPersonalData() {
        return new UserPersonalData();
    }

    @Bean
    @RequestScope
    UserOrgData userOrgData() {
        return new UserOrgData();
    }
}
