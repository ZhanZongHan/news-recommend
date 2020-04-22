package com.zzh.tools;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtils {
    private static volatile Properties properties;
    private PropertiesUtils() {}

    public static void init(String filename) {
        if (properties == null) {
            synchronized (PropertiesUtils.class) {
                if (properties == null) {
                    properties = new Properties();
                    try {
                        properties.load(PropertiesUtils.class.getClassLoader().getResourceAsStream(filename));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public static String getValue(String key) {
        return properties.getProperty(key);
    }
}
