package com.sanjodeundiak.highload_ass1;

import com.sanjodeundiak.highload_ass1.configs.Settings;
import com.sanjodeundiak.highload_ass1.controllers.IController;
import com.sanjodeundiak.highload_ass1.utils.Utils;
import com.sun.javafx.binding.StringFormatter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sanjo on 09.10.2016.
 */
public class Router {
    private static Router instance;

    private HashMap<String, String> map;
    private Config cfg;

    private Router() {
        map  = new HashMap<>();

        cfg = ConfigFactory.parseResources("routes.conf");

        for (Config c: cfg.getConfigList("routes")) {
            String pattern = c.getString("pattern");
            String controller = c.getString("controller");

            map.put(pattern, controller);
        }
    }

    private static synchronized Router getInstance() {
        if (instance == null)
            instance = new Router();

        return instance;
    }

    static IController getControllerForRoute(String route) {
        Router router = getInstance();
        String className = router.map.get(route);

        if (className == null)
            return null;

        className =  String.format("%s.%s", router.cfg.getString("controllers_baseclassname"), className);

        return Utils.instantiate(className, IController.class);
    }
}
