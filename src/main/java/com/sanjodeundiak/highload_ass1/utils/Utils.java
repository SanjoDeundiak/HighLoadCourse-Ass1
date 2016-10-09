package com.sanjodeundiak.highload_ass1.utils;

/**
 * Created by sanjo on 09.10.2016.
 */
public class Utils {
    public static  <T> T instantiate(final String className, final Class<T> type){
        try{
            return type.cast(Class.forName(className).newInstance());
        } catch(final InstantiationException e){
            throw new IllegalStateException(e);
        } catch(final IllegalAccessException e){
            throw new IllegalStateException(e);
        } catch(final ClassNotFoundException e){
            return null;
        }
    }
}
