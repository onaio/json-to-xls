package io.ei.jsontoxls;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.db.DatabaseConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class JsonToXlsConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
    private DatabaseConfiguration database = new DatabaseConfiguration();

    @NotNull
    @JsonProperty
    private String apiDetailsFile;

    public DatabaseConfiguration getDatabaseConfiguration() {
        return database;
    }

    public String apiDetailsFile() {
        return apiDetailsFile;
    }
}
