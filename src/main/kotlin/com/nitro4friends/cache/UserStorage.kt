package com.nitro4friends.cache

import com.nitro4friends.model.ClientPublicDataModel
import com.nitro4friends.model.UserModel
import com.nitro4friends.model.toClientPublicDataModel
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

object UserStorage {

    private val cacheLock: ReadWriteLock = ReentrantReadWriteLock()
    private var cache: Map<String, Pair<UserModel, ClientPublicDataModel>> = mapOf()

    fun exists(uid: String): Boolean {
        cacheLock.readLock().lock()
        val exists = cache.containsKey(uid)
        cacheLock.readLock().unlock()
        return exists
    }

    fun getUser(uid: String): Pair<UserModel, ClientPublicDataModel>? {
        cacheLock.readLock().lock()
        val user = cache[uid]
        cacheLock.readLock().unlock()
        return user
    }

    fun addUser(uid: String, user: UserModel): ClientPublicDataModel {
        cacheLock.writeLock().lock()
        val clientPublicDataModel = user.toClientPublicDataModel()
        cache = cache + (uid to (user to clientPublicDataModel))
        cacheLock.writeLock().unlock()
        return clientPublicDataModel
    }

    fun removeUser(uid: String) {
        cacheLock.writeLock().lock()
        cache = cache - uid
        cacheLock.writeLock().unlock()
    }
}