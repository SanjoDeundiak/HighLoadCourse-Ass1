package com.sanjodeundiak.highload_ass1.controllers;

import com.sanjodeundiak.highload_ass1.utils.HttpRequest;
import com.sanjodeundiak.highload_ass1.utils.HttpResponseWriter;

import java.io.OutputStream;

/**
 * Created by sanjo on 09.10.2016.
 */
public interface IController {
    public void handleQuery(HttpRequest request, HttpResponseWriter responseWriter);
}
