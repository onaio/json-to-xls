package io.ei.jsontoxls.util;

import com.astav.jsontojava.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;

import static io.ei.jsontoxls.util.UUIDUtils.newUUID;

public class JsonPojoConverter {
    private Logger logger = LoggerFactory.getLogger(JsonPojoConverter.class);
    private String className;
    private String packageName;
    private String outputDirectory;

    public JsonPojoConverter(String packageName, String className, String outputDirectory) {
        this.className = className;
        this.packageName = packageName;
        this.outputDirectory = outputDirectory;
    }

    public String generateJavaClasses(String jsonString) throws IOException, ClassNotFoundException {
        String generatedPackageName = this.packageName + newUUID();
        Generator generator = new Generator(this.outputDirectory, generatedPackageName, null, null);
        generator.generateClasses(className, jsonString);
        logger.info(MessageFormat.format("Generated package with name {0}", generatedPackageName));
        return generatedPackageName;
    }
}

