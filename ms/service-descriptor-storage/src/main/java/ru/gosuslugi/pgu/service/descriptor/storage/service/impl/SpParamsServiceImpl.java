package ru.gosuslugi.pgu.service.descriptor.storage.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.service.descriptor.storage.service.ServiceDescriptorService;
import ru.gosuslugi.pgu.service.descriptor.storage.service.SpParamsService;

@Slf4j
@RequiredArgsConstructor
@Service
public class SpParamsServiceImpl implements SpParamsService {

    private final ServiceDescriptorService service;

    @Override
    public String get(String serviceId){
        try {
            JSONObject jsonObject = new JSONObject(service.get(serviceId));
            if(!jsonObject.has("parameters")){
                log.info("There are no SP parameters for serviceId = " + serviceId);
                return null;
            }
            return jsonObject.getString("parameters");
        } catch(JSONException e){
            log.info("Cannot parse SP parameters for serviceId = " + serviceId, e);
            return null;
        }
    }

}
