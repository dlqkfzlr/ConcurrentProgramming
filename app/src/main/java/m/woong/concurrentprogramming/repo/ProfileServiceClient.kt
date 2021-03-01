package m.woong.concurrentprogramming.repo

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import m.woong.concurrentprogramming.model.Profile

class ProfileServiceClient : ProfileServiceRepository {
    override fun asyncFetchByName(name: String) = GlobalScope.async {
        Profile(1, name, 28)
    }

    override fun asyncFetchById(id: Long) = GlobalScope.async {
        Profile(id, "Susan", 28)
    }

    override suspend fun fetchByName(name: String): Profile {
        return  Profile(1, name, 28)
    }

    override suspend fun fetchById(id: Long): Profile {
        return Profile(id, "Susan", 28)
    }
}