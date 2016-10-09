package com.sanjodeundiak.highload_ass1.controllers;

import com.sanjodeundiak.highload_ass1.utils.HttpRequest;
import com.sanjodeundiak.highload_ass1.utils.HttpResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sanjo on 09.10.2016.
 */
public class UserProfileController implements IController {
    public void handleQuery(HttpRequest request, HttpResponseWriter responseWriter) {
        Logger logger = LoggerFactory.getLogger(UserProfileController.class);
        logger.info("Received request");
    }
}
