package io.ei.jsontoxls;

import com.yammer.dropwizard.config.Environment;
import io.ei.jsontoxls.filter.CorsFilter;
import io.ei.jsontoxls.repository.ExcelRepository;
import io.ei.jsontoxls.repository.TemplateRepository;
import org.skife.jdbi.v2.DBI;

public class Context {
    private static Context context;
    private Environment environment;
    private JsonToXlsConfiguration jsonToXlsConfiguration;
    private TemplateRepository templateRepository;
    private ExcelRepository excelRepository;
    private DBI dbInterface;

    protected Context() {
    }

    public static Context getInstance() {
        if (context == null) {
            context = new Context();
        }
        return context;
    }

    public Context updateJSONToXlsConfiguration(JsonToXlsConfiguration jsonToXlsConfiguration) {
        this.jsonToXlsConfiguration = jsonToXlsConfiguration;
        return this;
    }

    public Context updateEnvironment(Environment environment) {
        this.environment = environment;
        this.environment.addFilter(CorsFilter.class, "/*");
        return this;
    }

    public ExcelRepository excelRepository() throws ClassNotFoundException {

        if (excelRepository == null) {
            excelRepository = dbInterface.onDemand(ExcelRepository.class);
        }
        return excelRepository;
    }

    public Context updateDBInterface(DBI dbInterface) {
        this.dbInterface = dbInterface;
        return this;
    }

    public TemplateRepository templateRepository() throws ClassNotFoundException {

        if (templateRepository == null) {
            templateRepository = dbInterface.onDemand(TemplateRepository.class);
        }
        return templateRepository;
    }


}
