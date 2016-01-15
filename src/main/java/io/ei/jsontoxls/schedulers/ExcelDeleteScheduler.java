package io.ei.jsontoxls.schedulers;


//import de.spinscale.dropwizard.jobs.Job;
//import de.spinscale.dropwizard.jobs.annotations.On;
import io.ei.jsontoxls.Context;
import io.ei.jsontoxls.repository.ExcelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

//@On("59 59 23 * * ?")
public class ExcelDeleteScheduler {//extends Job {
    Logger logger = LoggerFactory.getLogger(ExcelDeleteScheduler.class);
    private Context context;

    public ExcelDeleteScheduler() {
        context = Context.getInstance();
    }

    //@Override
    public void doJob() {
        logger.info("Scheduled job started....");
        try {
            ExcelRepository excelRepository = context.excelRepository();
            excelRepository.deleteAllExcelsCreatedADayBefore();
        } catch (ClassNotFoundException e) {
            logger.error(MessageFormat.format("Failed to delete excels aged more than one day. Exception: {0}",e.getMessage()));
        }
    }
}
