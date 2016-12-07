package com.github.blemale.scaffeine;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.concurrent.Future;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static scala.compat.java8.FutureConverters.toJava;

class AsyncCacheLoaderAdapter<K, V> implements AsyncCacheLoader<K, V> {

  private final Function1<K, Future<V>> loader;
  private final Option<Function2<K, V, Future<V>>> reloadLoader;

  AsyncCacheLoaderAdapter(Function1<K, Future<V>> loader, Option<Function2<K, V, Future<V>>> reloadLoader) {
    this.loader = loader;
    this.reloadLoader = reloadLoader;
  }

  @Nonnull
  @Override
  public CompletableFuture<V> asyncLoad(@Nonnull K key, @Nonnull Executor executor) {
    return toJava(loader.apply(key)).toCompletableFuture();
  }

  @Nonnull
  @Override
  public CompletableFuture<V> asyncReload(@Nonnull K key, @Nonnull V oldValue, @Nonnull Executor executor) {
    if (reloadLoader.isEmpty()) {
      return AsyncCacheLoader.super.asyncReload(key, oldValue, executor);
    } else {
      return toJava(reloadLoader.get().apply(key, oldValue)).toCompletableFuture();
    }
  }
}
