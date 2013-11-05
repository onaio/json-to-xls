package io.ei.jsontoxls;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class JsonToXlsConfiguration extends Configuration {

    @NotEmpty
    @JsonProperty
    private String xlsTemplate;

    public String getXlsTemplate() {
        return xlsTemplate;
    }
}
