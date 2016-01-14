package io.ei.jsontoxls;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import de.spinscale.dropwizard.jobs.JobsBundle;
import io.ei.jsontoxls.filter.CorsFilter;
import io.ei.jsontoxls.resources.APIResource;
import io.ei.jsontoxls.resources.TemplateResource;
import io.ei.jsontoxls.resources.XlsResource;
import io.ei.jsontoxls.util.ExcelUtils;
import io.ei.jsontoxls.util.JsonPojoConverter;
import io.ei.jsontoxls.util.ObjectDeserializer;
import io.ei.jsontoxls.util.PackageUtils;
import org.skife.jdbi.v2.DBI;

import static io.ei.jsontoxls.AllConstants.*;

public class JsonToXlsService extends Application<JsonToXlsConfiguration> {

    private Context context;

    public static void main(String[] args) throws Exception {
        new JsonToXlsService().run(args);
    }

    @Override
    public void initialize(Bootstrap<JsonToXlsConfiguration> bootstrap) {
        //bootstrap.setName(MICRO_SERVICE_NAME);
        bootstrap.addBundle(new MigrationsBundle<JsonToXlsConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(JsonToXlsConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        //bootstrap.addBundle(new JobsBundle("io.ei.jsontoxls"));
    }

    @Override
    public void run(JsonToXlsConfiguration configuration, Environment environment) throws Exception {
        //environment.addFilter(CorsFilter.class, "/*");

        JsonPojoConverter converter = new JsonPojoConverter(DOMAIN_PACKAGE, ROOT_CLASS_NAME, GENERATED_CLASSES_OUTPUT_DIRECTORY);
        ObjectDeserializer objectDeserializer = new ObjectDeserializer(GENERATED_CLASSES_OUTPUT_DIRECTORY, ROOT_CLASS_NAME);
        PackageUtils packageUtil = new PackageUtils(GENERATED_CLASSES_OUTPUT_DIRECTORY);
        
        DBIFactory factory = new DBIFactory();
        DBI dbInterface = factory.build(environment, configuration.getDataSourceFactory(), MICRO_SERVICE_NAME);
        ExcelUtils excelUtils = new ExcelUtils();

        context = Context.getInstance()
                .updateEnvironment(environment)
                .updateJSONToXlsConfiguration(configuration)
                .updateDBInterface(dbInterface);

        APIResource apiResource = new APIResource(configuration.apiDetailsFile());
        TemplateResource templateResource = new TemplateResource(context.templateRepository(), excelUtils);
        XlsResource xlsResource = new XlsResource(converter, objectDeserializer, packageUtil, excelUtils,
                context.templateRepository(), context.excelRepository());
        environment.jersey().register(apiResource);
        //environment.jersey().register(templateResource);
        //environment.jersey().register(xlsResource);
    }
}
