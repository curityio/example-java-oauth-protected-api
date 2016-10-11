/*
 * Copyright (C) 2016 Curity AB. All rights reserved.
 *
 * The contents of this file are the property of Curity AB.
 * You may not copy or use this file, in either source code
 * or executable form, except in compliance with terms
 * set by Curity AB.
 *
 * For further information, please contact Curity AB.
 */

package se.curity.examples.oauth;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;
import java.util.function.Supplier;

final class ExternalResourceLoader
{
    private static final Logger _logger = LoggerFactory.getLogger(ExternalResourceLoader.class);

    static final String PROPS_FILE_NAME = "OAuthFilter.properties";
    static final String PROPERTIES_LOCATION = "/META-INF/services/" + PROPS_FILE_NAME;
    static final String HTTP_CLIENT_PROPERTY = "openid.httpClientSupplier.className";

    private static ExternalResourceLoader instance;

    private final Properties _properties;

    public static synchronized ExternalResourceLoader getInstance()
    {
        if (instance == null)
        {
            instance = new ExternalResourceLoader();
        }
        return instance;
    }

    private ExternalResourceLoader()
    {
        _properties = new Properties(defaultProperties());

        InputStream stream = getClass().getResourceAsStream(PROPERTIES_LOCATION);
        if (stream == null)
        {
            try
            {
                stream = new BufferedInputStream(new FileInputStream(PROPS_FILE_NAME));
                _logger.info("Found properties file in the working directory");
            }
            catch (FileNotFoundException e)
            {
                // not a problem
            }
        }
        if (stream == null)
        {
            _logger.info("No external config found, using defaults");
        }
        else
        {
            try (InputStream input = stream)
            {
                _properties.load(input);
            }
            catch (IOException e)
            {
                _logger.warn("Problem loading properties at " + PROPERTIES_LOCATION, e);
            }
        }
    }

    private static Properties defaultProperties()
    {
        Properties properties = new Properties();
        properties.put(HTTP_CLIENT_PROPERTY, DefaultJwkHttpClientSupplier.class.getName());
        return properties;
    }

    HttpClient loadJwkHttpClient()
    {
        String httpClientSupplierClassName = _properties.getProperty(HTTP_CLIENT_PROPERTY);
        try
        {
            Class<? extends Supplier> supplierType = Class.forName(httpClientSupplierClassName)
                    .asSubclass(Supplier.class);
            _logger.info("Using HttpClientSupplier of type {}", supplierType.getName());
            Supplier<?> supplier = supplierType.newInstance();
            Object httpClient = supplier.get();
            return HttpClient.class.cast(httpClient);
        }
        catch (Exception e)
        {
            _logger.warn("Unable to load httpClientSupplier from " + PROPERTIES_LOCATION +
                    "\nWill fallback to the default HTTP Client", e);
            return new DefaultJwkHttpClientSupplier().get();
        }
    }

}
