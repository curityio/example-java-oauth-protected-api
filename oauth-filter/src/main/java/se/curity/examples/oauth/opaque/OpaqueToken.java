package se.curity.examples.oauth.opaque;


import java.time.Instant;

public class OpaqueToken implements Expirable
{
    private final Instant _expiresAt;
    private final String _scope;
    private final String _subject;

    public OpaqueToken(String subject, long expiresAt, String scope)
    {
        _subject = subject;
        _scope = scope;
        _expiresAt = Instant.ofEpochSecond(expiresAt);
    }

    public String getScope()
    {
        return _scope;
    }

    public String getSubject()
    {
        return _subject;
    }

    @Override
    public Instant getExpiresAt()
    {
        return _expiresAt;
    }
}
