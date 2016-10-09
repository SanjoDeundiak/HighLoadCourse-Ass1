package com.sanjodeundiak.highload_ass1.configs;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by sanjo on 09.10.2016.
 */
public class FreeMarkerConfig {
    private static final FreeMarkerConfig instance = new FreeMarkerConfig();

    private static Configuration cfg;

    static public Configuration getConfig() {
        synchronized (instance) {
            if (cfg == null)
                cfg = initCfg();

            return cfg;
        }
    }

    private FreeMarkerConfig() { }

    private static Configuration initCfg() {
        Logger logger = LoggerFactory.getLogger(FreeMarkerConfig.class);
        logger.info("Initializing FreeMarker");
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);

        String templatesPath = Settings.getConfig().getString("freemarker.templates_path");
        try {
            cfg.setDirectoryForTemplateLoading(new File(templatesPath));
        }
        catch (IOException ex) {
            logger.error("Error initializing free market templates config: {}", ex);
        }

        cfg.setDefaultEncoding("UTF-8");

        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        cfg.setLogTemplateExceptions(false);

        logger.info("FreeMarker initialized");

        return cfg;
    }
}
