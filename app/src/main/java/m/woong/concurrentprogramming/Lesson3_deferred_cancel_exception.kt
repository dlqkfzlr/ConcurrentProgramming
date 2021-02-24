package m.woong.concurrentprogramming

import kotlinx.coroutines.*

fun main()  = runBlocking<Unit> {
    val deferred = GlobalScope.async {
        TODO("Not implemented yet!")
    }
    try {
        deferred.await()
    } catch (t: Throwable){
        println("Deferred cancelled due to ${t.message}")
    }
}