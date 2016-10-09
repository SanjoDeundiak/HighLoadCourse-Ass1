package com.sanjodeundiak.highload_ass1.configs;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by sanjo on 10/9/16.
 */
public class Settings {
    static private Config config;
    static private final Settings instance = new Settings();

    private Settings() { }

    static public Config getConfig() {
        synchronized (instance) {
            if (config == null)
                config = ConfigFactory.load();

            return config;
        }
    }
}
