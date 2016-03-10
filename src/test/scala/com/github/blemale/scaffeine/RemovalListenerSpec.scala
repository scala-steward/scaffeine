package com.github.blemale.scaffeine

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

import com.github.benmanes.caffeine.cache.RemovalCause
import org.scalatest._

import scala.concurrent.ExecutionContext

class RemovalListenerSpec
    extends WordSpec
    with ShouldMatchers {

  class StubListener extends ((String, String, RemovalCause) => Unit) {
    val callCounter = new AtomicInteger

    override def apply(key: String, value: String, cause: RemovalCause): Unit =
      callCounter.incrementAndGet()
  }

  "Cache" should {
    "call removal listener on enties eviction" in {
      val listener = new StubListener
      val cache =
        Scaffeine()
          .executor(ExecutionContext.fromExecutor(DirectExecutor))
          .removalListener(listener)
          .build[String, String]()

      cache.put("foo", "bar")
      cache.invalidate("foo")

      listener.callCounter.get should be(1)
    }
  }

}
