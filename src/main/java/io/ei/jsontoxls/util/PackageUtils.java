package io.ei.jsontoxls.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

public class PackageUtils {
    private Logger logger = LoggerFactory.getLogger(PackageUtils.class);
    private String outputDirectory = "";

    public PackageUtils(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void cleanup(String packageName) {
        File packageDirectory = new File(this.outputDirectory + "/" + packageName.replace(".", "/"));
        try {
            if (packageDirectory.exists()) {
                FileUtils.forceDelete(packageDirectory);
            }
        } catch (IOException e) {
            logger.error(MessageFormat.format("Cleanup of package failed. Exception Message: {0}. Stack trace: {1}", e.getMessage(),
                    ExceptionUtils.getFullStackTrace(e)));
        }
    }
}