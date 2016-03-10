package com.github.blemale.scaffeine

import java.util.concurrent.Executor

import com.github.benmanes.caffeine.cache.stats.StatsCounter
import com.github.benmanes.caffeine.cache._
import org.scalatest.{ PrivateMethodTester, ShouldMatchers, WordSpec }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }

class ScaffeineSpec
    extends WordSpec
    with ShouldMatchers
    with PrivateMethodTester {

  "Scaffeine" should {
    "create builder" in {
      val scaffeine: Scaffeine[Any, Any] = Scaffeine()

      scaffeine shouldBe a[Scaffeine[_, _]]
    }

    "create builder from spec" in {
      val scaffeine: Scaffeine[Any, Any] = Scaffeine(CaffeineSpec.parse("initialCapacity=10"))

      scaffeine shouldBe a[Scaffeine[_, _]]
    }

    "create builder from spec as string" in {
      val scaffeine: Scaffeine[Any, Any] = Scaffeine("initialCapacity=10")

      scaffeine shouldBe a[Scaffeine[_, _]]
    }

    "set initial capacity" in {
      val scaffeine = Scaffeine().initialCapacity(99)

      val getInitialCapacity = PrivateMethod[Int]('getInitialCapacity)
      val initialCapacity = scaffeine.underlying invokePrivate getInitialCapacity()

      initialCapacity should be(99)
    }

    "set executor" in {
      val scaffeine = Scaffeine().executor(ExecutionContext.global)

      val getExecutor = PrivateMethod[Executor]('getExecutor)
      val executor = scaffeine.underlying invokePrivate getExecutor()

      executor should be(ExecutionContext.global)
    }

    "set maximum size" in {
      val scaffeine = Scaffeine().maximumSize(99)

      val getMaximumWeight = PrivateMethod[Long]('getMaximumWeight)
      val maximumSize = scaffeine.underlying invokePrivate getMaximumWeight()

      maximumSize should be(99L)
    }

    "set maximum weight" in {
      val scaffeine = Scaffeine().maximumWeight(99).weigher((_: Any, _: Any) => 1)

      val getMaximumWeight = PrivateMethod[Long]('getMaximumWeight)
      val maximumWeight = scaffeine.underlying invokePrivate getMaximumWeight()

      maximumWeight should be(99L)
    }

    "set weigher" in {
      val scaffeine = Scaffeine().weigher((_: Any, _: Any) => 1)

      val isWeighted = PrivateMethod[Boolean]('isWeighted)
      val weighted = scaffeine.underlying invokePrivate isWeighted()

      weighted should be(true)
    }

    "set weak keys" in {
      val scaffeine = Scaffeine().weakKeys()

      val isWeakKeys = PrivateMethod[Boolean]('isWeakKeys)
      val weakKeys = scaffeine.underlying invokePrivate isWeakKeys()

      weakKeys should be(true)
    }

    "set weak values" in {
      val scaffeine = Scaffeine().weakValues()

      val isWeakValues = PrivateMethod[Boolean]('isWeakValues)
      val weakValues = scaffeine.underlying invokePrivate isWeakValues()

      weakValues should be(true)
    }

    "set soft values" in {
      val scaffeine = Scaffeine().softValues()

      val isSoftValues = PrivateMethod[Boolean]('isSoftValues)
      val softValues = scaffeine.underlying invokePrivate isSoftValues()

      softValues should be(true)
    }

    "set expire after write" in {
      val scaffeine = Scaffeine().expireAfterWrite(10.minutes)

      val getExpiresAfterWriteNanos = PrivateMethod[Long]('getExpiresAfterWriteNanos)
      val expiresAfterWriteNanos = scaffeine.underlying invokePrivate getExpiresAfterWriteNanos()

      expiresAfterWriteNanos should be(10.minutes.toNanos)
    }

    "set expire after access" in {
      val scaffeine = Scaffeine().expireAfterAccess(10.minutes)

      val getExpiresAfterAccessNanos = PrivateMethod[Long]('getExpiresAfterAccessNanos)
      val expiresAfterAccessNanos = scaffeine.underlying invokePrivate getExpiresAfterAccessNanos()

      expiresAfterAccessNanos should be(10.minutes.toNanos)
    }

    "set refresh after write" in {
      val scaffeine = Scaffeine().refreshAfterWrite(10.minutes)

      val getRefreshAfterWriteNanos = PrivateMethod[Long]('getRefreshAfterWriteNanos)
      val refreshAfterWriteNanos = scaffeine.underlying invokePrivate getRefreshAfterWriteNanos()

      refreshAfterWriteNanos should be(10.minutes.toNanos)
    }

    "set ticker" in {
      val scaffeine = Scaffeine().ticker(Ticker.disabledTicker())

      val getTicker = PrivateMethod[Ticker]('getTicker)
      val ticker = scaffeine.underlying invokePrivate getTicker()

      ticker should be(Ticker.disabledTicker())
    }

    "set removal listener" in {
      val scaffeine = Scaffeine().removalListener((_: Any, _: Any, _) => println("removed"))

      val getRemovalListener = PrivateMethod[RemovalListener[Any, Any]]('getRemovalListener)
      val removalListener = scaffeine.underlying invokePrivate getRemovalListener(false)

      removalListener shouldNot be(null)
    }

    "set cache writer" in {
      val writer = new CacheWriter[Any, Any] {
        override def write(key: Any, value: Any): Unit = println("write")
        override def delete(key: Any, value: Any, cause: RemovalCause): Unit = println("delete")
      }

      val scaffeine = Scaffeine().writer(writer)

      val getCacheWriter = PrivateMethod[CacheWriter[Any, Any]]('getCacheWriter)
      val cacheWriter = scaffeine.underlying invokePrivate getCacheWriter()

      cacheWriter should be(writer)
    }

    "set record stats" in {
      val scaffeine = Scaffeine().recordStats()

      val isRecordingStats = PrivateMethod[Boolean]('isRecordingStats)
      val recordingStats = scaffeine.underlying invokePrivate isRecordingStats()

      recordingStats should be(true)
    }

    "set record stats supplier" in {
      val scaffeine = Scaffeine().recordStats(() => StatsCounter.disabledStatsCounter())

      val isRecordingStats = PrivateMethod[Boolean]('isRecordingStats)
      val recordingStats = scaffeine.underlying invokePrivate isRecordingStats()

      recordingStats should be(true)
    }

    "build cache" in {
      val cache = Scaffeine().build[Int, Int]()

      cache shouldBe a[Cache[_, _]]
    }

    "build loading cache from loading function" in {
      val cache = Scaffeine().build[Int, Int]((i: Int) => i + 1)

      cache shouldBe a[LoadingCache[_, _]]
    }

    "build loading cache from loading, all loading and reloading functions" in {
      val cache =
        Scaffeine()
          .build[Int, Int](
            loader = (key: Int) => key + 1,
            allLoader = Some((keys: Iterable[Int]) => keys.map(i => (i -> (i + 1))).toMap),
            reloadLoader = Some((key: Int, old: Int) => key + 1)
          )

      cache shouldBe a[LoadingCache[_, _]]
    }

    "build async loading cache from sync loading function " in {
      val cache = Scaffeine().buildAsync[Int, Int]((i: Int) => i + 1)

      cache shouldBe a[AsyncLoadingCache[_, _]]
    }

    "build async loading cache from sync loading, all loading and reloading functions" in {
      val cache =
        Scaffeine()
          .buildAsync[Int, Int](
            loader = (key: Int) => key + 1,
            allLoader = Some((keys: Iterable[Int]) => keys.map(i => (i -> (i + 1))).toMap),
            reloadLoader = Some((key: Int, old: Int) => key + 1)
          )

      cache shouldBe a[AsyncLoadingCache[_, _]]
    }

    "build async loading cache from async loading function" in {
      val cache = Scaffeine().buildAsyncFuture[Int, Int]((i: Int) => Future.successful(i + 1))

      cache shouldBe a[AsyncLoadingCache[_, _]]
    }

    "build async loading cache from async loading, all loading and reloading functions" in {
      val cache =
        Scaffeine()
          .buildAsyncFuture[Int, Int](
            loader = (key: Int) => Future.successful(key + 1),
            allLoader = Some((keys: Iterable[Int]) => Future.successful(keys.map(i => (i -> (i + 1))).toMap)),
            reloadLoader = Some((key: Int, old: Int) => Future.successful(key + 1))
          )

      cache shouldBe a[AsyncLoadingCache[_, _]]
    }

  }

}
