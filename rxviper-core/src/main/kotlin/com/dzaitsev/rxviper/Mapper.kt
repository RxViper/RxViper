package com.dzaitsev.rxviper

/** Converts one domain or presentation value without depending on an async backend. */
fun interface Mapper<in From, out To> {
    fun map(entity: From): To

    operator fun invoke(entity: From): To = map(entity)

    /** Maps in iteration order without modifying the input. */
    fun mapAll(entities: Iterable<From>): List<To> = entities.map(::map)
}
