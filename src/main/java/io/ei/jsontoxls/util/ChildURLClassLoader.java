package io.ei.jsontoxls.util;

import java.net.URL;
import java.net.URLClassLoader;

public class ChildURLClassLoader extends URLClassLoader {

    public ChildURLClassLoader(URL[] urls) {
        super(urls);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }
}
