package ru.gosuslugi.pgu.service.holder.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.gosuslugi.pgu.service.holder.FrontendConfigHolder;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class FrontendConfigHolderImpl implements FrontendConfigHolder {

    @Value("${defaultConfig}")
    private String config;

    @Value("${defaultKey}")
    private String configKey;

    @Value("${initConfig}")
    private String initConfig;

    private Map<String,Object> configHolder = new HashMap<>();

    @Override
    public Object getConfigByServiceId(String serviceId) {
        return configHolder.get(serviceId);
    }

    @Override
    public void setConfigByServiceId(String serviceId, Object config) {
        configHolder.put(serviceId,config);
    }


    @PostConstruct
    private void afterInit(){
        if(!Strings.isEmpty(config) && !Strings.isEmpty(configKey)){
            this.configHolder.put(configKey,config);
        }

        if(!Strings.isEmpty(initConfig)){
            try{
                ObjectMapper om = new ObjectMapper();
                Map<String,Object> initConfigMap = om.readValue(initConfig, new TypeReference<>() {
                });
                configHolder.putAll(initConfigMap);
            } catch (JsonProcessingException e) {
                log.error("Ошибка чтения initConfig",e);
            }
        }
    }
}
