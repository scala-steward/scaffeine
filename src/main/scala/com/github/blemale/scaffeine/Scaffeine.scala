package com.github.blemale.scaffeine

import java.util.concurrent.{CompletableFuture, Executor, TimeUnit}

import com.github.benmanes.caffeine.cache._
import com.github.benmanes.caffeine.cache.stats.StatsCounter

import scala.compat.java8.FunctionConverters._
import scala.compat.java8.FutureConverters._
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

object Scaffeine {
  def apply(): Scaffeine[Any, Any] =
    Scaffeine(Caffeine.newBuilder().asInstanceOf[Caffeine[Any, Any]])

  def apply(spec: CaffeineSpec): Scaffeine[Any, Any] =
    Scaffeine(Caffeine.from(spec).asInstanceOf[Caffeine[Any, Any]])

  def apply(spec: String): Scaffeine[Any, Any] =
    Scaffeine(Caffeine.from(spec).asInstanceOf[Caffeine[Any, Any]])
}

case class Scaffeine[K, V](underlying: Caffeine[K, V]) {

  def initialCapacity(initialCapacity: Int): Scaffeine[K, V] =
    Scaffeine(underlying.initialCapacity(initialCapacity))

  def executor(executionContext: ExecutionContext): Scaffeine[K, V] =
    Scaffeine(underlying.executor(executionContext.asInstanceOf[ExecutionContextExecutor]))

  def maximumSize(maximumSize: Long): Scaffeine[K, V] =
    Scaffeine(underlying.maximumSize(maximumSize))

  def maximumWeight(maximumWeight: Long): Scaffeine[K, V] =
    Scaffeine(underlying.maximumWeight(maximumWeight))

  def weigher[K1 <: K, V1 <: V](weigher: (K1, V1) => Int) =
    Scaffeine(underlying.weigher(new Weigher[K1, V1] {
      override def weigh(key: K1, value: V1): Int = weigher(key, value)
    }))

  def weakKeys(): Scaffeine[K, V] =
    Scaffeine(underlying.weakKeys())

  def weakValues(): Scaffeine[K, V] =
    Scaffeine(underlying.weakValues())

  def softValues(): Scaffeine[K, V] =
    Scaffeine(underlying.softValues())

  def expireAfterWrite(duration: Duration): Scaffeine[K, V] =
    Scaffeine(underlying.expireAfterWrite(duration.toNanos, TimeUnit.NANOSECONDS))

  def expireAfterAccess(duration: Duration): Scaffeine[K, V] =
    Scaffeine(underlying.expireAfterAccess(duration.toNanos, TimeUnit.NANOSECONDS))

  def refreshAfterWrite(duration: Duration): Scaffeine[K, V] =
    Scaffeine(underlying.refreshAfterWrite(duration.toNanos, TimeUnit.NANOSECONDS))

  def ticker(ticker: Ticker): Scaffeine[K, V] =
    Scaffeine(underlying.ticker(ticker))

  def removalListener[K1 <: K, V1 <: V](removalListener: (K1, V1, RemovalCause) => Unit): Scaffeine[K1, V1] =
    Scaffeine(underlying.removalListener(new RemovalListener[K1, V1] {
      override def onRemoval(key: K1, value: V1, cause: RemovalCause): Unit = removalListener(key, value, cause)
    }))

  def writer[K1 <: K, V1 <: V](writer: CacheWriter[K1, V1]): Scaffeine[K1, V1] =
    Scaffeine(underlying.writer(writer))

  def recordStats(): Scaffeine[K, V] =
    Scaffeine(underlying.recordStats())

  def recordStat[C <: StatsCounter](statsCounterSupplier: () => C) =
    Scaffeine(underlying.recordStats(asJavaSupplier(statsCounterSupplier)))

  def build[K1 <: K, V1 <: V](): Cache[K1, V1] =
    Cache(underlying.build())

  def build[K1 <: K, V1 <: V](loader: K1 => V1): LoadingCache[K1, V1] =
    LoadingCache(underlying.build(new CacheLoader[K1, V1] {
      override def load(key: K1): V1 = loader(key)
    }))

  def buildAsync[K1 <: K, V1 <: V](loader: K1 => V1): AsyncLoadingCache[K1, V1] =
    AsyncLoadingCache(underlying.buildAsync[K1, V1](new CacheLoader[K1, V1] {
      override def load(key: K1): V1 = loader(key)
    }))

  def buildAsyncFuture[K1 <: K, V1 <: V](asyncLoader: K1 => Future[V1]): AsyncLoadingCache[K1, V1] =
    AsyncLoadingCache(underlying.buildAsync[K1, V1](new AsyncCacheLoader[K1, V1] {
      override def asyncLoad(key: K1, executor: Executor): CompletableFuture[V1] =
        asyncLoader(key).toJava.toCompletableFuture
    }))
}
