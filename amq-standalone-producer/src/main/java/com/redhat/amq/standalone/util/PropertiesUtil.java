package com.redhat.amq.standalone.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    public static Properties loadPropertiesFromFile(String filename) throws IOException {
        InputStream is = new FileInputStream(filename);

        Properties properties = new Properties();
        properties.load(is);

        return properties;
    }

    public static Properties loadPropertiesFromResources(String filename) throws IOException {
        InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(filename);

        Properties properties = new Properties();
        properties.load(is);

        return properties;
    }
}
