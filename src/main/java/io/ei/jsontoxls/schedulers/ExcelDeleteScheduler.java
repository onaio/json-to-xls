package io.ei.jsontoxls.schedulers;


import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.On;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@On("0/15 * * * * ?")
public class ExcelDeleteScheduler extends Job {
    Logger logger = LoggerFactory.getLogger(ExcelDeleteScheduler.class);

    @Override
    public void doJob() {
        logger.info("Scheduled job started....");
    }
}
