package ru.whalemare.finger.iv

import ru.whalemare.finger.Storage

/**
 * @since 2018
 * @author Anton Vlasov - whalemare
 */

typealias IV = ByteArray

class IvMemoryStorage: Storage<IV> {

    var cache = mutableMapOf<String, IV>()

    override fun isExists(key: String): Boolean {
        return cache.containsKey(key)
    }

    override fun retrieve(key: String): IV? {
        return cache[key]
    }


    override fun store(key: String, value: IV) {
        cache[key] = value
    }
}