package com.github.blemale.scaffeine

import java.util.concurrent.Executor

import com.github.benmanes.caffeine.cache.{ AsyncCache => CaffeineAsyncCache }

import scala.compat.java8.FunctionConverters._
import scala.compat.java8.FutureConverters._
import scala.concurrent.Future

object AsyncCache {
  def apply[K, V](asyncCache: CaffeineAsyncCache[K, V]): AsyncCache[K, V] =
    new AsyncCache(asyncCache)
}

class AsyncCache[K, V](val underlying: CaffeineAsyncCache[K, V]) {

  /**
   * Returns the future associated with `key` in this cache, or `None` if there is no
   * cached future for `key`.
   *
   * @param key key whose associated value is to be returned
   * @return an option containing the current (existing or computed) future value to which the
   *         specified key is mapped, or `None` if this map contains no mapping for the key
   */
  def getIfPresent(key: K): Option[Future[V]] =
    Option(underlying.getIfPresent(key)).map(_.toScala)

  /**
   * Returns the future associated with `key` in this cache, obtaining that value from
   * `mappingFunction` if necessary. This method provides a simple substitute for the
   * conventional "if cached, return; otherwise create, cache and return" pattern.
   *
   * @param key             key with which the specified value is to be associated
   * @param mappingFunction the function to asynchronously compute a value
   * @return the current (existing or computed) future value associated with the specified key
   */
  def get(key: K, mappingFunction: K => V): Future[V] =
    underlying.get(key, asJavaFunction(mappingFunction)).toScala

  /**
   * Returns the future associated with `key` in this cache, obtaining that value from
   * `mappingFunction` if necessary. This method provides a simple substitute for the
   * conventional "if cached, return; otherwise create, cache and return" pattern.
   *
   * @param key             key with which the specified value is to be associated
   * @param mappingFunction the function to asynchronously compute a value
   * @return the current (existing or computed) future value associated with the specified key
   * @throws java.lang.RuntimeException     or Error if the mappingFunction does when constructing the future,
   *                              in which case the mapping is left unestablished
   */
  def getFuture(key: K, mappingFunction: K => Future[V]): Future[V] =
    underlying.get(
      key,
      asJavaBiFunction((k: K, _: Executor) => mappingFunction(k).toJava.toCompletableFuture)
    ).toScala

  /**
   * Associates `value` with `key` in this cache. If the cache previously contained a
   * value associated with `key`, the old value is replaced by `value`. If the
   * asynchronous computation fails, the entry will be automatically removed.
   *
   * @param key         key with which the specified value is to be associated
   * @param valueFuture value to be associated with the specified key
   */
  def put(key: K, valueFuture: Future[V]): Unit =
    underlying.put(key, valueFuture.toJava.toCompletableFuture)

  /**
   * Returns a view of the entries stored in this cache as a synchronous [[Cache]]. A
   * mapping is not present if the value is currently being loaded. Modifications made to the
   * synchronous cache directly affect the asynchronous cache. If a modification is made to a
   * mapping that is currently loading, the operation blocks until the computation completes.
   *
   * @return a thread-safe synchronous view of this cache
   */
  def synchronous(): Cache[K, V] =
    Cache(underlying.synchronous())

}
