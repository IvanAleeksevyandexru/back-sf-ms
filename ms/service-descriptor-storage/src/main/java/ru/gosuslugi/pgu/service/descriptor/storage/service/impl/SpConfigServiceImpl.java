package ru.gosuslugi.pgu.service.descriptor.storage.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.service.descriptor.storage.service.ServiceDescriptorService;
import ru.gosuslugi.pgu.service.descriptor.storage.service.SpConfigService;

@RequiredArgsConstructor
@Service
@Slf4j
public class SpConfigServiceImpl implements SpConfigService {

    private final ServiceDescriptorService service;

    @Override
    public String get(String serviceId){
        try {
            JSONObject jsonObject = new JSONObject(service.get(serviceId));
            return jsonObject.getString("spConfig");
        } catch(JSONException e){
            log.info("Cannot parse spConfig for serviceId = " + serviceId + ": " + e.getMessage(), e);
            return null;
        }
    }
}
