package com.github.blemale.scaffeine

import java.util.concurrent.Executor

import com.github.benmanes.caffeine.cache.{AsyncLoadingCache => CaffeineAsyncLoadingCache}

import scala.collection.JavaConverters._
import scala.compat.java8.FunctionConverters._
import scala.compat.java8.FutureConverters._
import scala.concurrent.{ExecutionContext, Future}

object AsyncLoadingCache {
  def apply[K, V](asyncLoadingCache: CaffeineAsyncLoadingCache[K, V]): AsyncLoadingCache[K, V] =
    new AsyncLoadingCache(asyncLoadingCache)
}

class AsyncLoadingCache[K, V](val underlying: CaffeineAsyncLoadingCache[K, V]) {

  def getIfPresent(key: K)(implicit ec: ExecutionContext): Future[V] =
    underlying.getIfPresent(key).toScala

  def get(key: K, mappingFunction: K => V)(implicit ec: ExecutionContext): Future[V] =
    underlying.get(key, asJavaFunction(mappingFunction)).toScala

  def getFuture(key: K, mappingFunction: K => Future[V])(implicit ec: ExecutionContext): Future[V] =
    underlying.get(
      key,
      asJavaBiFunction((k: K, _: Executor) => mappingFunction(k).toJava.toCompletableFuture)
    ).toScala

  def get(key: K)(implicit ec: ExecutionContext): Future[V] =
    underlying.get(key).toScala

  def getAll(keys: Iterable[K])(implicit ec: ExecutionContext): Future[Map[K, V]] =
    underlying.getAll(keys.asJava).toScala.map(_.asScala.toMap)

  def put(key: K, valueFuture: Future[V])(implicit ec: ExecutionContext): Unit =
    underlying.put(key, valueFuture.toJava.toCompletableFuture)

  def synchronous(): LoadingCache[K, V] =
    LoadingCache(underlying.synchronous())

  override def toString = s"AsyncLoadingCache($underlying)"
}
