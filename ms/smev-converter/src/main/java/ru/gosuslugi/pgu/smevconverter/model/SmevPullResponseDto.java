package ru.gosuslugi.pgu.smevconverter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmevPullResponseDto {
    private HttpStatus status;
    private Map<Object, Object> response;
}
