package ru.whalemare.finger

interface Storage<Data> {
    fun store(key: String, value: Data)

    fun retrieve(key: String): Data?

    fun isExists(key: String): Boolean
}
