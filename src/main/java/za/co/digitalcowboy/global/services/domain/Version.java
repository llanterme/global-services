package za.co.digitalcowboy.global.services.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Version {

    private String message;
    private String serviceVersion;
    private String service;
}
