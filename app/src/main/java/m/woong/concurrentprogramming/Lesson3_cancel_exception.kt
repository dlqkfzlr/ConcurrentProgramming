package m.woong.concurrentprogramming

import kotlinx.coroutines.*

@InternalCoroutinesApi
fun main(args: Array<String>) = runBlocking {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("1. Job cancelled due to ${throwable.message}")
    }
    GlobalScope.launch(exceptionHandler) {
        TODO("1. Not implemented yet!")
    }

    GlobalScope.launch {
        TODO("2. Not implemented yet!")
    }.invokeOnCompletion { cause ->
        cause?.let {
            println("2. Job cancelled due to ${it.message}")
        }
    }
    delay(2000)
}