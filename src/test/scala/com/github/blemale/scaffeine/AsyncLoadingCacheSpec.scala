package com.github.blemale.scaffeine

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ OptionValues, ShouldMatchers, WordSpec }

import scala.concurrent.Future

class AsyncLoadingCacheSpec
    extends WordSpec
    with ShouldMatchers
    with ScalaFutures
    with OptionValues {

  "AsyncLoadingCache" should {
    "get value if present" in {
      import scala.concurrent.ExecutionContext.Implicits.global
      val cache = Scaffeine().buildAsync[String, String]((key: String) => "loaded")

      cache.put("foo", Future.successful("present"))
      val fooValue = cache.getIfPresent("foo")
      val barValue = cache.getIfPresent("bar")

      fooValue.futureValue.value should be("present")
      barValue.futureValue should be(None)
    }

    "get or load value" in {
      import scala.concurrent.ExecutionContext.Implicits.global
      val cache = Scaffeine().buildAsync[String, String]((key: String) => "loaded")

      cache.put("foo", Future.successful("present"))
      val fooValue = cache.get("foo")
      val barValue = cache.get("bar")

      fooValue.futureValue should be("present")
      barValue.futureValue should be("loaded")
    }

    "get or compute value" in {
      import scala.concurrent.ExecutionContext.Implicits.global
      val cache = Scaffeine().buildAsync[String, String]((key: String) => "loaded")

      cache.put("foo", Future.successful("present"))
      val fooValue = cache.get("foo", k => "computed")
      val barValue = cache.get("bar", k => "computed")

      fooValue.futureValue should be("present")
      barValue.futureValue should be("computed")
    }

    "get or compute async value" in {
      import scala.concurrent.ExecutionContext.Implicits.global
      val cache = Scaffeine().buildAsync[String, String]((key: String) => "loaded")

      cache.put("foo", Future.successful("present"))
      val fooValue = cache.getFuture("foo", k => Future.successful("computed"))
      val barValue = cache.getFuture("bar", k => Future.successful("computed"))

      fooValue.futureValue should be("present")
      barValue.futureValue should be("computed")
    }

    "get or load all given values" in {
      import scala.concurrent.ExecutionContext.Implicits.global
      val cache = Scaffeine().buildAsync[String, String]((key: String) => "loaded")

      cache.put("foo", Future.successful("present"))
      val values = cache.getAll(List("foo", "bar"))

      values.futureValue should contain only ("foo" -> "present", "bar" -> "loaded")
    }

    "get or bulk load all given values" in {
      import scala.concurrent.ExecutionContext.Implicits.global
      val cache = Scaffeine().buildAsync[String, String](
        (key: String) => "loaded",
        allLoader = Some((keys: Iterable[String]) => keys.map(_ -> "bulked").toMap)
      )

      cache.put("foo", Future.successful("present"))
      val values = cache.getAll(List("foo", "bar"))

      values.futureValue should contain only ("foo" -> "present", "bar" -> "bulked")
    }

    "put value" in {
      import scala.concurrent.ExecutionContext.Implicits.global
      val cache = Scaffeine().buildAsync[String, String]((key: String) => "loaded")

      cache.put("foo", Future.successful("present"))
      val fooValue = cache.getIfPresent("foo")

      fooValue.futureValue.value should be("present")
    }

    "expose a synchronous view of itself" in {
      val cache = Scaffeine().buildAsync[String, String]((key: String) => "loaded")

      val synchronousCache = cache.synchronous()

      synchronousCache shouldBe a[LoadingCache[_, _]]
    }
  }
}
