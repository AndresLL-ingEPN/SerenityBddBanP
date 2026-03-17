package co.com.screenplay.project.util;



import co.com.screenplay.project.util.constants.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertiesReader {

    Logger logger = Logger.getLogger(this.getClass().getName());

    public Optional<Properties> getPropValues(String nameFileProperties) {
        Properties properties = new Properties();
        String propFileName = co.com.screenplay.project.util.constants.Paths.propertiesPath() + nameFileProperties;

        try (InputStream inputStream = Files.newInputStream(Paths.get(propFileName))) {
            properties.load(inputStream);
            return Optional.of(properties);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public String getProperty(String property, String nameFileProperties) {
        InputStream imputStream = null;
        Properties properties = new Properties();
        try {
            imputStream = PropertiesReader.class.getClassLoader().getResourceAsStream(Constants.PROPERTIES + File.separator + nameFileProperties);
            properties.load(imputStream);
        } catch (IOException e) {
            logger.log(Level.SEVERE, String.format(Messages.ERROR_READ_PROP, property, nameFileProperties), e);
        } finally {
            if (imputStream != null) {
                try {
                    imputStream.close();
                } catch (IOException e) {
                    logger.log(Level.WARNING, Messages.ERROR, e);
                }
            }
        }
        return properties.getProperty(property);
    }
}
