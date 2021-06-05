package com.hoc081098.flowext

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
public fun <T, R> Flow<T>.takeUntil(notifier: Flow<R>): Flow<T> = flow {
  try {
    coroutineScope {
      val job = launch {
        notifier.take(1).collect()
        throw ClosedException
      }

      collect { return@collect emit(it) }
      job.cancel()
    }
  } catch (e: ClosedException) {
    // no-op
  }
}

private object ClosedException : Exception()
