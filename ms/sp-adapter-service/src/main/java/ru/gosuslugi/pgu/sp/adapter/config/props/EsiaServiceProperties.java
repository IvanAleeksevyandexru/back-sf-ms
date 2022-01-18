package ru.gosuslugi.pgu.sp.adapter.config.props;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "esia")
public class EsiaServiceProperties {
    @Value("${esia.keystore.alias}")
    private String keyStoreAlias;
    private String crt;
    private String keystore;
    @Value("${esia.keystore.passwd}")
    private String keystorePasswd;

    private String url;
    @Value("${esia.pd.proxy.url:#{null}}")
    private String proxyUrl;
    @Value("${esia.redirect.url}")
    private String redirectUrl;
}
