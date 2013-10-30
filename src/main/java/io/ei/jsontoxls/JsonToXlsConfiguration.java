package io.ei.jsontoxls;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class JsonToXlsConfiguration extends Configuration {

    @NotEmpty
    @JsonProperty
    private String template;

    @NotEmpty
    @JsonProperty
    private String defaultName = "Stranger";

    @NotEmpty
    @JsonProperty
    private String xlsTemplate;

    public String getTemplate() {
        return template;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public String getXlsTemplate() {
        return xlsTemplate;
    }
}
