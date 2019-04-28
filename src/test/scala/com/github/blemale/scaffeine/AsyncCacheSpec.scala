package com.github.blemale.scaffeine

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ Matchers, OptionValues, WordSpec }

import scala.concurrent.Future

class AsyncCacheSpec
  extends WordSpec
  with Matchers
  with ScalaFutures
  with OptionValues {

  "AsyncCache" should {
    "get value if present" in {
      val cache = Scaffeine().buildAsync[String, String]()

      cache.put("foo", Future.successful("present"))
      val fooValue = cache.getIfPresent("foo")
      val barValue = cache.getIfPresent("bar")

      fooValue.value.futureValue should be("present")
      barValue should be(None)
    }

    "get or compute value" in {
      val cache = Scaffeine().buildAsync[String, String]()

      cache.put("foo", Future.successful("present"))
      val fooValue = cache.get("foo", _ => "computed")
      val barValue = cache.get("bar", _ => "computed")

      fooValue.futureValue should be("present")
      barValue.futureValue should be("computed")
    }

    "get or compute async value" in {
      val cache = Scaffeine().buildAsync[String, String]()

      cache.put("foo", Future.successful("present"))
      val fooValue = cache.getFuture("foo", _ => Future.successful("computed"))
      val barValue = cache.getFuture("bar", _ => Future.successful("computed"))

      fooValue.futureValue should be("present")
      barValue.futureValue should be("computed")
    }

    "put value" in {
      val cache = Scaffeine().buildAsync[String, String]()

      cache.put("foo", Future.successful("present"))
      val fooValue = cache.getIfPresent("foo")

      fooValue.value.futureValue should be("present")
    }

    "expose a synchronous view of itself" in {
      val cache = Scaffeine().buildAsync[String, String]()

      val synchronousCache = cache.synchronous()

      synchronousCache shouldBe a[Cache[_, _]]
    }
  }
}
