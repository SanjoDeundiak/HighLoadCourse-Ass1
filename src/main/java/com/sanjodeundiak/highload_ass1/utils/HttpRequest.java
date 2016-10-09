package com.sanjodeundiak.highload_ass1.utils;

/**
 * Created by sanjo on 09.10.2016.
 */
public class HttpRequest {
    private String method;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public HttpRequest(String method, String query) {
        this.method = method;
        this.query = query;
    }
}
