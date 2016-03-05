package com.github.blemale.scaffeine

import com.github.benmanes.caffeine.cache.LoadingCache

import scala.collection.JavaConverters._

object SLoadingCache {
  def apply[K, V](loadingCache: LoadingCache[K, V]): SLoadingCache[K, V] =
    new SLoadingCache(loadingCache)
}

class SLoadingCache[K, V](override val underlying: LoadingCache[K, V]) extends SCache(underlying) {

  def get(key: K): V =
    underlying.get(key)

  def getAll(keys: Iterable[K]): Map[K, V] =
    underlying.getAll(keys.asJava).asScala.toMap

  def refresh(key: K): Unit =
    underlying.refresh(key)

  override def toString = s"SLoadingCache($underlying)"
}
