package io.ei.jsontoxls.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.io.File;
import java.util.List;
import java.net.URL;

public class ObjectDeserializer {
    private String outputDirectory;
    private String className;

    public ObjectDeserializer(String outputDirectory, String className) {
        this.outputDirectory = outputDirectory;
        this.className = className;
    }

    public Object makeJsonObject(String generatedPackageName, String jsonString) throws Exception {
        File file = new File(this.outputDirectory + "/");

        URL url = file.toURI().toURL();
        URL[] urls = new URL[]{url};

        ClassLoader defaultClassLoader = Thread.currentThread().getContextClassLoader();

        ClassLoader classLoader = new ChildURLClassLoader(urls);

        Thread.currentThread().setContextClassLoader(classLoader);
        Class aClass = classLoader.loadClass(generatedPackageName + "." + this.className);
        Object object = new Gson().fromJson(jsonString, aClass);
        Thread.currentThread().setContextClassLoader(defaultClassLoader);
        return object;
    }

    public Object makeJsonList(String generatedPackageName, String jsonString) throws Exception {
        File file = new File(this.outputDirectory + "/");

        URL url = file.toURI().toURL();
        URL[] urls = new URL[]{url};

        ClassLoader defaultClassLoader = Thread.currentThread().getContextClassLoader();

        ClassLoader classLoader = new ChildURLClassLoader(urls);

        Thread.currentThread().setContextClassLoader(classLoader);
        Class aClass = classLoader.loadClass(generatedPackageName + "." + this.className);
        Type listType = new TypeToken<List<Object>>(){}.getType();
        Object object = new Gson().fromJson(jsonString, listType);
        Thread.currentThread().setContextClassLoader(defaultClassLoader);
        return object;
    }
}