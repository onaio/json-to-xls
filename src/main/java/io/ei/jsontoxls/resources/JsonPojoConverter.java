package io.ei.jsontoxls.resources;

import com.astav.jsontojava.Generator;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.UUID;

import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

public class JsonPojoConverter {
    private String className;

    private String packageName;
    private String outputDirectory;
    private Logger logger;

    public JsonPojoConverter(String packageName, String className, String outputDirectory, Logger logger) {
        this.className = className;
        this.packageName = packageName;
        this.outputDirectory = outputDirectory;
        this.logger = logger;
    }

    public Object makeJsonObject(String generatedPackageName, String jsonString) throws Exception {
        File file = new File(this.outputDirectory + "/");

        URL url = file.toURI().toURL();
        URL[] urls = new URL[]{url};

        ClassLoader defaultClassLoader = Thread.currentThread().getContextClassLoader();

        ClassLoader cl = new ChildURLClassLoader(urls);

        Thread.currentThread().setContextClassLoader(cl);
        Class cls = cl.loadClass(generatedPackageName + "." + className);
        Object object = new Gson().fromJson(jsonString, cls);
        Thread.currentThread().setContextClassLoader(defaultClassLoader);
        return object;
    }

    public String generateJavaClasses(String jsonString) throws IOException, ClassNotFoundException {
        String uuidString = UUID.randomUUID().toString().replace("-", "");
        String generatedPackageName = this.packageName + uuidString;
        Generator generator = null;

        generator = new Generator(this.outputDirectory, generatedPackageName, null, null);
        generator.generateClasses(className, jsonString);

        return generatedPackageName;
    }

    public void cleanup(String packageName)  {
        File outputDirectory = new File(this.outputDirectory + "/" + packageName.replace(".", "/"));
        try {
            if (outputDirectory.exists()) {
                FileUtils.forceDelete(outputDirectory);
            }
        } catch (IOException e) {
            logger.error(MessageFormat.format("Cleanup of package failed. Exception Message: {0}. Stack trace: {1}", e.getMessage(),
                    getFullStackTrace(e)));
        }
    }
}

