package se.curity.examples.oauth.opaque;


import java.time.Instant;

public interface Expirable
{
    Instant getExpiresAt();
}
