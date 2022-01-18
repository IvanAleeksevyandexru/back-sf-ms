package ru.gosuslugi.pgu.generator.client.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class IpshRequestDto extends BaseResponse {

    private Response response;

    @Data
    public static class Response {

        private String requestId;
        private String scenario;

    }

}
