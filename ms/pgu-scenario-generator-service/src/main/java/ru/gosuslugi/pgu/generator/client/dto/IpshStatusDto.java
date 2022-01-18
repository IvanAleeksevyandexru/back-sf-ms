package ru.gosuslugi.pgu.generator.client.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class IpshStatusDto extends IpshRequestDto {

    private Error detailedError;

}
