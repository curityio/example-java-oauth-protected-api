package se.curity.examples.oauth.opaque;


public class OAuthIntrospectResponse
{
    private boolean active;
    private String sub;
    private String scope;
    private long exp;

    public boolean getActive()
    {
        return active;
    }

    public String getSub()
    {
        return sub;
    }

    public String getScope()
    {
        return scope;
    }

    public long getExp()
    {
        return exp;
    }
}
