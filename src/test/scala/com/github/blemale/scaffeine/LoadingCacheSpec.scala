package com.github.blemale.scaffeine

import org.scalatest._

import scala.concurrent.ExecutionContext

class LoadingCacheSpec
    extends WordSpec
    with ShouldMatchers
    with OptionValues {

  "LoadingCache" should {
    "be a cache" in {
      val cache = Scaffeine().build[String, String]((key: String) => "computed")

      cache shouldBe a[Cache[_, _]]
    }

    "get or load value" in {
      val cache = Scaffeine().build[String, String]((key: String) => "computed")
      cache.put("foo", "present")

      val fooValue = cache.get("foo")
      val barValue = cache.get("bar")

      fooValue should be("present")
      barValue should be("computed")
    }

    "get or load all given values" in {
      val cache =
        Scaffeine()
          .build[String, String](
            loader = (key: String) => "computed",
            allLoader = Some((keys: Iterable[String]) => keys.map(_ -> "computed").toMap)
          )
      cache.put("foo", "present")

      val values = cache.getAll(List("foo", "bar"))

      values should contain only ("foo" -> "present", "bar" -> "computed")
    }

    "refresh value" in {
      val cache =
        Scaffeine()
          .executor(ExecutionContext.fromExecutor(DirectExecutor))
          .build[String, String](
            loader = (key: String) => "computed",
            reloadLoader = Some((key: String, old: String) => "reload")
          )

      cache.put("foo", "present")
      cache.refresh("foo")
      val fooValue = cache.get("foo")

      fooValue should be("reload")
    }
  }

}
