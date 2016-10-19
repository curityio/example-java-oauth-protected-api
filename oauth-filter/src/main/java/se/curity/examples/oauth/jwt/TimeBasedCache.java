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

package se.curity.examples.oauth.jwt;

import com.google.common.collect.ImmutableMap;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A Cache which attempts to reload its entries, limited to a minimum reloading time,
 * when a value is requested for a key which does not exist in the backing map.
 * <p>
 * The cache will not attempt to reload more often than once every minTimeBetweenReloads,
 * which is a parameter provided in the constructor.
 * <p>
 * This cache is Thread-safe.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class TimeBasedCache<K, V>
{

    private final Object _cacheLock = new Object();
    private volatile ImmutableMap<K, V> _cache;

    private volatile Instant _nextLoadingEarliestTime = Instant.MIN;
    private volatile Optional<Instant> _lastLoading = Optional.empty();

    private final Duration _minTimeBetweenReloads;
    private final Supplier<ImmutableMap<K, V>> _valuesSupplier;
    private final Clock _clock;

    public TimeBasedCache(Duration minTimeBetweenReloads, Supplier<ImmutableMap<K, V>> valuesSupplier)
    {
        this(minTimeBetweenReloads, valuesSupplier, ImmutableMap.of(), Clock.systemDefaultZone());
    }

    public TimeBasedCache(Duration minTimeBetweenReloads,
                          Supplier<ImmutableMap<K, V>> valuesSupplier,
                          ImmutableMap<K, V> initialCache,
                          Clock clock)
    {
        _minTimeBetweenReloads = minTimeBetweenReloads;
        _valuesSupplier = valuesSupplier;
        _cache = initialCache;
        _clock = clock;
    }

    private boolean mayReload()
    {
        return Instant.now(_clock).isAfter(_nextLoadingEarliestTime);
    }

    public V get(K key)
    {
        // optimistically try to get the key without locking
        V value = _cache.get(key);
        if (value == null && mayReload())
        {
            reloadCache();
            value = _cache.get(key);
        }

        return value;
    }

    public void clear()
    {
        synchronized (_cacheLock)
        {
            _cache = ImmutableMap.of();
        }
    }

    private void reloadCache()
    {
        final Instant earliestTime = _nextLoadingEarliestTime;
        synchronized (_cacheLock)
        {
            // if the field was updated while we locked, we should NOT reload the cache
            // as it must have just happened
            if (earliestTime == _nextLoadingEarliestTime)
            {
                _cache = _valuesSupplier.get();
                _lastLoading = Optional.of(Instant.now(_clock));
                _nextLoadingEarliestTime = _lastLoading.get().plus(_minTimeBetweenReloads);
            }
        }
    }

    public Optional<Instant> getLastReloadInstant()
    {
        return _lastLoading;
    }

    public Duration getMinTimeBetweenReloads()
    {
        return _minTimeBetweenReloads;
    }
}
