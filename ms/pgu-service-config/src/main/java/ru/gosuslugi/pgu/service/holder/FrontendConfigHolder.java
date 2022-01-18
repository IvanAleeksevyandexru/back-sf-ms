package ru.gosuslugi.pgu.service.holder;

import lombok.Data;

public interface FrontendConfigHolder {

    Object getConfigByServiceId(String serviceId);

    void setConfigByServiceId(String serviceId, Object config);

}
