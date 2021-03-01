package m.woong.concurrentprogramming.repo

import kotlinx.coroutines.Deferred
import m.woong.concurrentprogramming.model.Profile

interface ProfileServiceRepository {
    fun asyncFetchByName(name: String): Deferred<Profile>
    fun asyncFetchById(id: Long): Deferred<Profile>

    suspend fun fetchByName(name: String): Profile
    suspend fun fetchById(id: Long): Profile
}