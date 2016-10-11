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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

final class DefaultJwkHttpClientSupplier implements Supplier<HttpClient>
{

    private final CloseableHttpClient _httpClient = HttpClients
            .custom()
            .disableAuthCaching()
            .disableAutomaticRetries()
            .disableRedirectHandling()
            .setConnectionTimeToLive(2, TimeUnit.SECONDS)
            .build();

    @Override
    public HttpClient get()
    {
        return _httpClient;
    }
}
