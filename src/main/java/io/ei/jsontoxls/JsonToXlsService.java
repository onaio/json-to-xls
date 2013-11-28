package io.ei.jsontoxls;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.jdbi.DBIFactory;
import com.yammer.dropwizard.migrations.MigrationsBundle;
import io.ei.jsontoxls.repository.TemplateRepository;
import io.ei.jsontoxls.resources.TemplateResource;
import io.ei.jsontoxls.resources.XlsResource;
import io.ei.jsontoxls.util.ExcelUtils;
import io.ei.jsontoxls.util.JsonPojoConverter;
import io.ei.jsontoxls.util.ObjectDeserializer;
import io.ei.jsontoxls.util.PackageUtils;
import org.skife.jdbi.v2.DBI;

import static io.ei.jsontoxls.AllConstants.*;

public class JsonToXlsService extends Service<JsonToXlsConfiguration> {

    public static void main(String[] args) throws Exception {
        new JsonToXlsService().run(args);
    }

    @Override
    public void initialize(Bootstrap<JsonToXlsConfiguration> bootstrap) {
        bootstrap.setName(MICRO_SERVICE_NAME);
        bootstrap.addBundle(new MigrationsBundle<JsonToXlsConfiguration>() {
            @Override
            public DatabaseConfiguration getDatabaseConfiguration(JsonToXlsConfiguration configuration) {
                return configuration.getDatabaseConfiguration();
            }
        });
    }

    @Override
    public void run(JsonToXlsConfiguration configuration, Environment environment) throws Exception {
        JsonPojoConverter converter = new JsonPojoConverter(DOMAIN_PACKAGE, ROOT_CLASS_NAME, GENERATED_CLASSES_OUTPUT_DIRECTORY);
        ObjectDeserializer objectDeserializer = new ObjectDeserializer(GENERATED_CLASSES_OUTPUT_DIRECTORY, ROOT_CLASS_NAME);
        PackageUtils packageUtil = new PackageUtils(GENERATED_CLASSES_OUTPUT_DIRECTORY);
        ExcelUtils excelUtils = new ExcelUtils();
        DBIFactory factory = new DBIFactory();
        DBI dbInterface = factory.build(environment, configuration.getDatabaseConfiguration(), MICRO_SERVICE_NAME);
        TemplateRepository templateRepository = dbInterface.onDemand(TemplateRepository.class);

        XlsResource xlsResource = new XlsResource(converter, objectDeserializer, packageUtil, excelUtils,
                templateRepository);
        TemplateResource templateResource = new TemplateResource(templateRepository, excelUtils);
        environment.addResource(xlsResource);
        environment.addResource(templateResource);
    }
}
