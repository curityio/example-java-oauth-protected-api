package se.curity.examples.oauth.opaque;


import javax.annotation.Nullable;
import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A cache that expires it's entries after a given timeout
 * @param <K> The key
 * @param <V extends Expirable> A value that can expire
 */
public class ExpirationBasedCache<K, V extends Expirable>
{
    private final ConcurrentHashMap<K, V> _cache;
    private final Clock _clock;

    public ExpirationBasedCache()
    {
        _cache = new ConcurrentHashMap<>();
        _clock = Clock.systemUTC();

        Timer timer = new Timer("cacheExpiration", true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                expireCacheEntries();
            }} , 60, 60);
    }

    public @Nullable V get(K key)
    {
        //optimistically get a value
        V value = _cache.get(key);
        //make sure it's not expired yet
        if (value != null && value.getExpiresAt().isAfter(Instant.now(_clock)))
        {
            return null;
        }
        return value;
    }

    public void put(K key, V value)
    {
        _cache.putIfAbsent(key, value);
    }

    private void expireCacheEntries()
    {
        Instant now = Instant.now(_clock);
        //This might miss the last entry if new are put in, but that's ok
        //it will be caught in the next expiration round instead.
        Set<K> keySet = new HashSet<>(_cache.keySet());

        Iterator<K> keys = keySet.iterator();
        while (keys.hasNext())
        {
            K key = keys.next();
            V entry = _cache.get(key);
            if (now.isAfter(entry.getExpiresAt()))
            {
                _cache.remove(key);
            }
        }
    }

    public void clear()
    {
        _cache.clear();
    }
}
