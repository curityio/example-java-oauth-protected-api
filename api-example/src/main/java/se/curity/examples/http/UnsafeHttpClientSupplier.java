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

package se.curity.examples.http;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * WARNING: This httpClient supplier is NOT meant to be used in production environments.
 * <p>
 * It disables SSL Certificate checks, making HTTPS communication completely unsafe.
 * <p>
 * Delete the file src/main/resources/META-INF/OAuthFilter.properties or set
 * a safe HttpClient supplier to use.
 */
public class UnsafeHttpClientSupplier implements Supplier<HttpClient>
{
    private static final Logger _logger = LoggerFactory.getLogger(UnsafeHttpClientSupplier.class);

    private static HttpClient create()
    {
        try
        {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                    builder.build(), NoopHostnameVerifier.INSTANCE);
            return HttpClients
                    .custom()
                    .disableAuthCaching()
                    .disableAutomaticRetries()
                    .disableRedirectHandling()
                    .setSSLSocketFactory(sslSocketFactory)
                    .build();
        }
        catch (Exception e)
        {
            _logger.error("Unable to create Unsafe HTTP client supplier", e);
            throw new RuntimeException("Unable to initialize httpClient", e);
        }
    }

    @Override
    public HttpClient get()
    {
        _logger.info("Creating {}", getClass().getName());
        return create();
    }
}
