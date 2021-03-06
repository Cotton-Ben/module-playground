/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 */

package org.ehcache.internal;

import org.ehcache.AtomicCache;
import org.ehcache.Cache;
import org.ehcache.internal.cachingtier.CachingTier;
import org.ehcache.internal.cachingtier.CachingTierProvider;
import org.ehcache.internal.cachingtier.TieredAtomicCache;
import org.ehcache.internal.cachingtier.TieredCache;
import org.ehcache.internal.cachingtier.ClockEvictingHeapCachingTier;
import org.ehcache.spi.ServiceConfiguration;
import org.ehcache.spi.ServiceProvider;
import org.ehcache.internal.util.ServiceUtil;

import java.util.concurrent.Future;

import static org.ehcache.spi.ServiceProvider.findAmongst;
import static org.ehcache.spi.ServiceProvider.findSingletonAmongst;

/**
 * @author Alex Snaps
 */
public class HeapCachingTierResource implements CachingTierProvider {

  public static <K, V> AtomicCache<K, V> wrapCacheWithCachingTierIfConfigured(final AtomicCache<K, V> cache, final Class<K> keyClazz, final Class<V> valueClazz, final ServiceProvider serviceProvider, final ServiceConfiguration<?>[] config) {
    if(findAmongst(HeapResourceCacheConfiguration.class, config).size() > 0) {
        return new TieredAtomicCache<>(cache, keyClazz, valueClazz, serviceProvider, config);
    } else {
      return cache;
    }
  }

  public static <K, V> Cache<K, V> wrapCacheWithCachingTierIfConfigured(final Cache<K, V> cache, final Class<K> keyClazz, final Class<V> valueClazz, final ServiceProvider serviceProvider, final ServiceConfiguration<?>[] config) {
    if(findAmongst(HeapResourceCacheConfiguration.class, config).size() > 0) {
      return new TieredCache<>(cache, keyClazz, valueClazz, serviceProvider, config);
    } else {
      return cache;
    }
  }

  @Override
  public <K, V> CachingTier<K> createCachingTier(Class<K> keyClazz, Class<V> valueClazz, ServiceProvider serviceProvider, ServiceConfiguration<?>... configs) {
    final HeapResourceCacheConfiguration config = findSingletonAmongst(HeapResourceCacheConfiguration.class, configs);
    return new ClockEvictingHeapCachingTier<>(config.getMaxOnHeapEntryCount());
  }

  @Override
  public void releaseCachingTier(CachingTier<?> resource) {
    //no-op
  }

  @Override
  public Future<?> start() {
    return ServiceUtil.completeFuture();
  }

  @Override
  public Future<?> stop() {
    return ServiceUtil.completeFuture();
  }
}
