/*
 * Copyright (C) 2016 Curity AB.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
