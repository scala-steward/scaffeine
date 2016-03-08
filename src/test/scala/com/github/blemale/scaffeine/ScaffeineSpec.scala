package com.github.blemale.scaffeine

import java.util.concurrent.Executor

import com.github.benmanes.caffeine.cache.stats.StatsCounter
import com.github.benmanes.caffeine.cache.{ RemovalCause, CacheWriter, RemovalListener, Ticker }
import com.sun.org.apache.xml.internal.security.encryption.ReferenceList
import org.scalatest.{ PrivateMethodTester, ShouldMatchers, WordSpec }
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext

class ScaffeineSpec
    extends WordSpec
    with ShouldMatchers
    with PrivateMethodTester {

  "Scaffeine" should {
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
  }

}
