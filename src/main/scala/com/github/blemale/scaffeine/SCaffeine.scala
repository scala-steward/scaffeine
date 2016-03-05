package com.github.blemale.scaffeine

import java.util.concurrent.{CompletableFuture, Executor, TimeUnit}

import com.github.benmanes.caffeine.cache._
import com.github.benmanes.caffeine.cache.stats.StatsCounter

import scala.compat.java8.FunctionConverters._
import scala.compat.java8.FutureConverters._
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

object SCaffeine {
  def apply(): SCaffeine[Any, Any] =
    SCaffeine(Caffeine.newBuilder().asInstanceOf[Caffeine[Any, Any]])

  def apply(spec: CaffeineSpec): SCaffeine[Any, Any] =
    SCaffeine(Caffeine.from(spec).asInstanceOf[Caffeine[Any, Any]])

  def apply(spec: String): SCaffeine[Any, Any] =
    SCaffeine(Caffeine.from(spec).asInstanceOf[Caffeine[Any, Any]])
}

case class SCaffeine[K, V](underlying: Caffeine[K, V]) {

  def initialCapacity(initialCapacity: Int): SCaffeine[K, V] =
    SCaffeine(underlying.initialCapacity(initialCapacity))

  def executor(executionContext: ExecutionContext): SCaffeine[K, V] =
    SCaffeine(underlying.executor(executionContext.asInstanceOf[ExecutionContextExecutor]))

  def maximumSize(maximumSize: Long): SCaffeine[K, V] =
    SCaffeine(underlying.maximumSize(maximumSize))

  def maximumWeight(maximumWeight: Long): SCaffeine[K, V] =
    SCaffeine(underlying.maximumWeight(maximumWeight))

  def weigher[K1 <: K, V1 <: V](weigher: (K1, V1) => Int) =
    SCaffeine(underlying.weigher(new Weigher[K1, V1] {
      override def weigh(key: K1, value: V1): Int = weigher(key, value)
    }))

  def weakKeys(): SCaffeine[K, V] =
    SCaffeine(underlying.weakKeys())

  def weakValues(): SCaffeine[K, V] =
    SCaffeine(underlying.weakValues())

  def softValues(): SCaffeine[K, V] =
    SCaffeine(underlying.softValues())

  def expireAfterWrite(duration: Duration): SCaffeine[K, V] =
    SCaffeine(underlying.expireAfterWrite(duration.toNanos, TimeUnit.NANOSECONDS))

  def expireAfterAccess(duration: Duration): SCaffeine[K, V] =
    SCaffeine(underlying.expireAfterAccess(duration.toNanos, TimeUnit.NANOSECONDS))

  def refreshAfterWrite(duration: Duration): SCaffeine[K, V] =
    SCaffeine(underlying.refreshAfterWrite(duration.toNanos, TimeUnit.NANOSECONDS))

  def ticker(ticker: Ticker): SCaffeine[K, V] =
    SCaffeine(underlying.ticker(ticker))

  def removalListener[K1 <: K, V1 <: V](removalListener: (K1, V1, RemovalCause) => Unit): SCaffeine[K1, V1] =
    SCaffeine(underlying.removalListener(new RemovalListener[K1, V1] {
      override def onRemoval(key: K1, value: V1, cause: RemovalCause): Unit = removalListener(key, value, cause)
    }))

  def writer[K1 <: K, V1 <: V](writer: CacheWriter[K1, V1]): SCaffeine[K1, V1] =
    SCaffeine(underlying.writer(writer))

  def recordStats(): SCaffeine[K, V] =
    SCaffeine(underlying.recordStats())

  def recordStat[C <: StatsCounter](statsCounterSupplier: () => C) =
    SCaffeine(underlying.recordStats(asJavaSupplier(statsCounterSupplier)))

  def build[K1 <: K, V1 <: V](): SCache[K1, V1] =
    SCache(underlying.build())

  def build[K1 <: K, V1 <: V](loader: K1 => V1): SLoadingCache[K1, V1] =
    SLoadingCache(underlying.build(new CacheLoader[K1, V1] {
      override def load(key: K1): V1 = loader(key)
    }))

  def buildAsync[K1 <: K, V1 <: V](loader: K1 => V1): AsyncLoadingCache[K1, V1] =
    underlying.buildAsync[K1, V1](new CacheLoader[K1, V1] {
      override def load(key: K1): V1 = loader(key)
    })

  def buildAsyncFuture[K1 <: K, V1 <: V](asyncLoader: K1 => Future[V1]): AsyncLoadingCache[K1, V1] =
    underlying.buildAsync[K1, V1](new AsyncCacheLoader[K1, V1] {
      override def asyncLoad(key: K1, executor: Executor): CompletableFuture[V1] =
        asyncLoader(key).toJava.toCompletableFuture
    })
}
