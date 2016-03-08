package com.github.blemale.scaffeine

import com.github.benmanes.caffeine.cache.stats.CacheStats
import com.github.benmanes.caffeine.cache.{ Cache => CaffeineCache, Policy }

import scala.collection.JavaConverters._
import scala.compat.java8.FunctionConverters._

object Cache {
  def apply[K, V](cache: CaffeineCache[K, V]) =
    new Cache(cache)
}

class Cache[K, V](val underlying: CaffeineCache[K, V]) {

  def getIfPresent(key: K): Option[V] =
    Option(underlying.getIfPresent(key))

  def get(key: K, mappingFunction: K => V): V =
    underlying.get(key, mappingFunction.asJava)

  def getAllPresent(keys: Iterable[K]): Map[K, V] =
    underlying.getAllPresent(keys.asJava).asScala.toMap

  def put(key: K, value: V): Unit =
    underlying.put(key, value)

  def putAll(map: Map[K, V]): Unit =
    underlying.putAll(map.asJava)

  def invalidate(key: K): Unit =
    underlying.invalidate(key)

  def invalidateAll(keys: Iterable[K]): Unit =
    underlying.invalidateAll(keys.asJava)

  def invalidateAll(): Unit =
    underlying.invalidateAll()

  def estimateSize(): Long =
    underlying.estimatedSize()

  def stats(): CacheStats =
    underlying.stats()

  def asMap(): collection.concurrent.Map[K, V] =
    underlying.asMap().asScala

  def cleanUp(): Unit =
    underlying.cleanUp()

  def policy(): Policy[K, V] =
    underlying.policy()

  override def toString = s"Cache($underlying)"
}
