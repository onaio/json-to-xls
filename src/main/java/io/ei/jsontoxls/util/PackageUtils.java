package io.ei.jsontoxls.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static java.text.MessageFormat.format;

public class PackageUtils {
    private Logger logger = LoggerFactory.getLogger(PackageUtils.class);
    private String outputDirectory = "";

    public PackageUtils(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void cleanup(String packageName) {
        File packageDirectory = new File(this.outputDirectory + "/" 
                + packageName.replace(".", "/"));
        try {
            if (packageDirectory.exists()) {
                logger.info(format("Attempting to delete directory: {0}",
                        packageDirectory.getPath()));
                FileUtils.forceDelete(packageDirectory);
            }
        } catch (IOException e) {
            logger.error(format("Cleanup of package failed. Exception Message: "
                    + "{0}. Stack trace: {1}", e.getMessage(),
                    ExceptionUtils.getStackTrace(e)));
        }
    }
}