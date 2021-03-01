package m.woong.concurrentprogramming

import kotlinx.coroutines.runBlocking
import m.woong.concurrentprogramming.repo.ProfileServiceClient
import m.woong.concurrentprogramming.repo.ProfileServiceRepository

fun main() = runBlocking {
    val client: ProfileServiceRepository = ProfileServiceClient()
    val profile = /*client.asyncFetchById(12).await()*/
        client.fetchById(12)

    println(profile)
}