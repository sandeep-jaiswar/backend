package com.apep.backend.utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
	
	public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

}
