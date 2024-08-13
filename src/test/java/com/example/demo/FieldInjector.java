package com.example.demo;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FieldInjector  {

    private static final Logger LOGGER = Logger.getLogger(FieldInjector.class.getName());

    public static void injectObjects(Object target, String fieldName, Object objectToInject) {
        if (target == null || fieldName == null || objectToInject == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }

        boolean wasPrivate = false;

        try {
            Field field = target.getClass().getDeclaredField(fieldName);

            if (!field.isAccessible()) {
                field.setAccessible(true);
                wasPrivate = true;
            }

            field.set(target, objectToInject);

            if (wasPrivate) {
                field.setAccessible(false);
            }
        } catch (NoSuchFieldException e) {
            LOGGER.log(Level.SEVERE, "Field not found: " + fieldName, e);
        } catch (IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, "Unable to access field: " + fieldName, e);
        }
    }
}
