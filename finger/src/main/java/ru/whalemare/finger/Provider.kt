package ru.whalemare.finger

/**
 * @since 2018
 * @author Anton Vlasov - whalemare
 */
interface Provider<Data> {
    fun provide(): Data
}