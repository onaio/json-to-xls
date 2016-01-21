package io.ei.jsontoxls;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Main configuration file for json to xls application
 * @author denniswambus
 * @email dwambua@ona.io
 */
public class JsonToXlsConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty
     private DataSourceFactory database = new DataSourceFactory();

    @Valid
    @NotEmpty
    @JsonProperty
    private String apiDetailsFile;

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    public String apiDetailsFile() {
        return apiDetailsFile;
    }
}
