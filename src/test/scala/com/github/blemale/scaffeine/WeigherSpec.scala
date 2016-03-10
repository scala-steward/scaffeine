package com.github.blemale.scaffeine

import org.scalatest._

import scala.concurrent.ExecutionContext

class WeigherSpec
    extends WordSpec
    with ShouldMatchers
    with OptionValues {

  "Cache" should {
    "use weigher for calculate size based eviction" in {
      val cache = Scaffeine()
        .executor(ExecutionContext.fromExecutor(DirectExecutor))
        .weigher[String, String]((key, value) => value.length)
        .maximumWeight(5)
        .build[String, String]()

      cache.put("foo", "word")
      cache.put("bar", "verylongword")

      cache.getIfPresent("foo") should be(None)
    }
  }

}
