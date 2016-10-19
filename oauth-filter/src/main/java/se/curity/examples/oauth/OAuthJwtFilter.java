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

package se.curity.examples.oauth;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.io.Closeables;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.examples.oauth.jwt.JwtValidator;
import se.curity.examples.oauth.jwt.JwtValidatorWithJwk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import static se.curity.examples.oauth.FilterHelper.getInitParamValue;
import static se.curity.examples.oauth.FilterHelper.getOptionalInitParamValue;
import static se.curity.examples.oauth.FilterHelper.initParamsMapFrom;

public class OAuthJwtFilter extends OAuthFilter
{
    private static Logger _logger = LoggerFactory.getLogger(OAuthJwtFilter.class);

    private String _oauthHost = null;
    private String[] _scopes = null;
    private long _minKidReloadTimeInSeconds = 3600;

    private JwtValidator _jwtValidator = null;
    private final HttpClient _httpClient = ExternalResourceLoader.getInstance().loadJwkHttpClient();

    interface InitParams
    {
        String OAUTH_HOST = "oauthHost";
        String OAUTH_PORT = "oauthPort";
        String SCOPE = "scope";
        String JSON_WEB_KEYS_PATH = "jsonWebKeysPath";
        String MIN_KID_RELOAD_TIME = "_minKidReloadTimeInSeconds";
    }


    public void init(FilterConfig filterConfig) throws ServletException
    {
        ImmutableMultimap<String, String> initParams = initParamsMapFrom(filterConfig);

        _oauthHost = getInitParamValue(InitParams.OAUTH_HOST, initParams);
        int oauthPort = getInitParamValue(InitParams.OAUTH_PORT, initParams, Integer::parseInt);

        String webKeysPath = getInitParamValue(InitParams.JSON_WEB_KEYS_PATH, initParams);

        String scope = getInitParamValue(InitParams.SCOPE, initParams);
        _scopes = scope.split("\\s+");

        Optional<Long> minKidReloadTime = getOptionalInitParamValue(
                InitParams.MIN_KID_RELOAD_TIME,
                initParams, Long::parseLong);
        _minKidReloadTimeInSeconds = minKidReloadTime.orElse(_minKidReloadTimeInSeconds);

        synchronized (this)
        {
            if (_jwtValidator == null)
            {
                try
                {
                    URI webKeysURI = new URI("https", null, _oauthHost, oauthPort, webKeysPath, null, null);
                    _jwtValidator = new JwtValidatorWithJwk(
                            webKeysURI,
                            _minKidReloadTimeInSeconds,
                            _httpClient);
                }
                catch (URISyntaxException e)
                {
                    throw new ServletException("Invalid parameters", e);
                }
                _logger.info("{} successfully initialized", OAuthFilter.class.getSimpleName());
            }
            else
            {
                _logger.warn("Attempted to set webkey URI more than once! Ignoring further attempts.");
            }
        }
    }

    @Override
    protected String getOAuthHost()
    {
        if (_oauthHost == null)
        {
            throw new IllegalStateException("Filter not initialized");
        }
        return _oauthHost;
    }

    @Override
    protected @Nonnull String[] getScopes()
    {
        if (_scopes == null)
        {
            throw new IllegalStateException("Filter not initialized");
        }
        return _scopes;
    }

    @Nullable
    @Override
    protected AuthenticatedUser authenticate(String token)
            throws IOException, ServletException
    {
        try
        {
            @Nullable Map<String, Object> validationResult = _jwtValidator.validate(token);
            if (validationResult != null)
            {
                return AuthenticatedUser.fromMap(validationResult);
            }
        }
        catch (Exception e)
        {
            _logger.debug("Failed to validate incoming token due to: {}", e.getMessage());
        }
        return null;
    }


    @Override
    public void destroy()
    {
        _logger.info("Destroying OAuthFilter");
        try
        {
            Closeables.close(_jwtValidator, true);
        }
        catch (IOException e)
        {
            _logger.warn("Problem closing jwk client", e);
        }
    }
}
