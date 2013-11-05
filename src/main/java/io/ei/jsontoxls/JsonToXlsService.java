package io.ei.jsontoxls;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import io.ei.jsontoxls.health.JsonToXLSHealthCheck;
import io.ei.jsontoxls.resources.JsonToXlsResource;

public class JsonToXlsService extends Service<JsonToXlsConfiguration> {
    public static void main(String[] args) throws Exception {
        new JsonToXlsService().run(args);
    }

    @Override
    public void initialize(Bootstrap<JsonToXlsConfiguration> bootstrap) {
        bootstrap.setName("json-to-excel");
    }

    @Override
    public void run(JsonToXlsConfiguration configuration, Environment environment) throws Exception {
        String xlsTemplate = configuration.getXlsTemplate();
        JsonToXlsResource resource = new JsonToXlsResource(xlsTemplate);
        environment.addResource(resource);
        environment.addHealthCheck(new JsonToXLSHealthCheck(resource));
    }
}
