package com.github.blemale.scaffeine

import com.github.benmanes.caffeine.cache.{LoadingCache => CaffeineLoadingCache}

import scala.collection.JavaConverters._

object LoadingCache {
  def apply[K, V](loadingCache: CaffeineLoadingCache[K, V]): LoadingCache[K, V] =
    new LoadingCache(loadingCache)
}

class LoadingCache[K, V](override val underlying: CaffeineLoadingCache[K, V]) extends Cache(underlying) {

  def get(key: K): V =
    underlying.get(key)

  def getAll(keys: Iterable[K]): Map[K, V] =
    underlying.getAll(keys.asJava).asScala.toMap

  def refresh(key: K): Unit =
    underlying.refresh(key)

  override def toString = s"LoadingCache($underlying)"
}
