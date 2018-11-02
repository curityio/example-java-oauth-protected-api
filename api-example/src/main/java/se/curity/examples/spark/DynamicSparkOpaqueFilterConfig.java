package se.curity.examples.spark;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import se.curity.examples.http.UnsafeHttpClientSupplier;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class DynamicSparkOpaqueFilterConfig implements FilterConfig
{
    private final Map<String, String> _params;

    public DynamicSparkOpaqueFilterConfig(String oauthHost, String oauthPort, String issuer, String clientId, String clientSecret, String scope)
    {
        _params = new HashMap<>();
        _params.put("oauthHost", oauthHost);
        _params.put("oauthPort", oauthPort);
        _params.put("issuer", issuer);
        _params.put("clientId", clientId);
        _params.put("clientSecret", clientSecret);
        _params.put("scope", scope);
    }

    public boolean autoconfigure(OpenIdConfig openIdConfig) throws IOException
    {
        boolean autoconfSuccess = false;
        URL introspectionEndpoint = new URL(openIdConfig.introspectionEndpoint);
        String introspectionEndpointPath = introspectionEndpoint.getPath();
        if (!introspectionEndpointPath.isEmpty()) { autoconfSuccess = true; }
        _params.put("introspectionPath", introspectionEndpointPath);
        return autoconfSuccess;
    }

    public boolean scopeSupportedByOpenidServer(OpenIdConfig openIdConfig)
    {
        boolean scopeIsSupported = false;
        for (String scope: openIdConfig.scopesSupported)
        {
            if (scope.equals(_params.get("scope")))
            {
                scopeIsSupported = true;
                break;
            }
        }
        return scopeIsSupported;
    }

    public OpenIdConfig getWellKnownOpenidConf() throws IOException {
        HttpClient httpClient = new UnsafeHttpClientSupplier().get();
        HttpGet httpGet = new HttpGet(_params.get("issuer") + ".well-known/openid-configuration");
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        DynamicSparkOpaqueFilterConfig.OpenIdConfig openIdConfig = gson.fromJson(bufferedReader, DynamicSparkOpaqueFilterConfig.OpenIdConfig.class);
        return openIdConfig;
    }

    @Override
    public String getFilterName() { return "DynamicSparkOpaquefilterConfig"; }

    @Override
    public ServletContext getServletContext() { return null; }

    @Override
    public String getInitParameter(String s) { return _params.get(s); }

    @Override
    public Enumeration<String> getInitParameterNames() { return new DynamicSparkOpaqueFilterConfig.EnumerationFromIteration(_params.keySet().iterator()); }

    protected class OpenIdConfig
    {
        String introspectionEndpoint;
        List<String> scopesSupported;
    }

    public class EnumerationFromIteration implements Enumeration
    {
        private Iterator iterator;

        EnumerationFromIteration(Iterator iterator)
        {
            this.iterator = iterator;
        }

        @Override
        public boolean hasMoreElements() { return iterator.hasNext(); }

        @Override
        public Object nextElement() { return iterator.next(); }
    }
}