package com.nitro4friends.cache

import com.nitro4friends.model.ClientPublicDataModel
import com.nitro4friends.model.UserModel
import com.nitro4friends.model.toClientPublicDataModel
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

object UserStorage {

    /**
     * The `cacheLock` property is a ReadWriteLock object used for thread synchronization when accessing the `cache` data structure.
     * It allows multiple threads to read from the cache concurrently, while ensuring exclusive write access for a single thread.
     *
     * @see ReentrantReadWriteLock
     */
    private val cacheLock: ReadWriteLock = ReentrantReadWriteLock()
    /**
     * Represents a cache variable that stores a mapping of user ids to pairs of UserModel and ClientPublicDataModel objects.
     * The cache variable is a private property and is initialized as an empty map.
     *
     * @property cache The cache map that stores the user id to UserModel and ClientPublicDataModel pairs.
     */
    private var cache: Map<String, Pair<UserModel, ClientPublicDataModel>> = mapOf()

    /**
     * Checks if a user with the given UID exists in the cache.
     *
     * @param uid The unique identifier of the user.
     * @return true if a user with the given UID exists in the cache, false otherwise.
     */
    fun exists(uid: String): Boolean {
        cacheLock.readLock().lock()
        val exists = cache.containsKey(uid)
        cacheLock.readLock().unlock()
        return exists
    }

    /**
     * Retrieves a user and their client public data from the cache based on the given unique identifier.
     *
     * @param uid The unique identifier of the user.
     * @return A pair containing the UserModel and ClientPublicDataModel if the user exists in the cache, otherwise null.
     */
    fun getUser(uid: String): Pair<UserModel, ClientPublicDataModel>? {
        cacheLock.readLock().lock()
        val user = cache[uid]
        cacheLock.readLock().unlock()
        return user
    }

    /**
     * Adds a user to the cache with the specified user ID and user model.
     *
     * @param uid The unique identifier of the user.
     * @param user The user model representing the user.
     * @return The client public data model after adding the user to the cache.
     */
    fun addUser(uid: String, user: UserModel): ClientPublicDataModel {
        cacheLock.writeLock().lock()
        val clientPublicDataModel = user.toClientPublicDataModel()
        cache = cache + (uid to (user to clientPublicDataModel))
        cacheLock.writeLock().unlock()
        return clientPublicDataModel
    }

    /**
     * Removes a user from the cache using the specified user ID.
     *
     * @param uid The ID of the user to be removed from the cache.
     */
    fun removeUser(uid: String) {
        cacheLock.writeLock().lock()
        cache = cache - uid
        cacheLock.writeLock().unlock()
    }
}