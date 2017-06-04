package com.github.xsavikx.zipbuilder;

import java.io.*;
import java.util.Properties;

/**
 * Created by User on 17/05/24.
 */
public class Configuration {
    private static final String CONFIGURATION_FILE_NAME = "./app.ini";
    private static final String LAST_FOLDER_PROPERTY = "lastFolder";
    private final Properties properties;

    private Configuration() {
        properties = initProperties();
    }

    private Properties initProperties() {
        final Properties properties = new Properties();
        loadProperties(properties, CONFIGURATION_FILE_NAME);
        return properties;
    }

    private void loadProperties(final Properties properties, final String propertiesLocation) {
        final File propertiesFile = new File(propertiesLocation);
        if (propertiesFile.exists()) {
            try (final FileInputStream fis = new FileInputStream(propertiesFile)) {
                properties.load(fis);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getLastFolder() {
        return properties.getProperty(LAST_FOLDER_PROPERTY, null);
    }

    public void setLastFolder(String lastFolder) {
        properties.put(LAST_FOLDER_PROPERTY, lastFolder);
    }

    private void writeProperties(Properties properties, String propertiesLocation) {
        try (OutputStream outputStream = new FileOutputStream(propertiesLocation)) {
            properties.store(outputStream, "zip-builder properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Configuration getInstance() {
        return SingletonHolder.INSTANCE.value;
    }

    private enum SingletonHolder {
        INSTANCE;

        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final Configuration value = new Configuration();
    }
}
