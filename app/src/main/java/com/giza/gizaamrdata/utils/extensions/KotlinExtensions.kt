package com.giza.gizaamrdata.utils.extensions

/**
 * @author hossam.
 */
/**
 * Returns an enum entry with the specified name or `null` if no such entry was found.
 */
inline fun <reified T : Enum<T>> enumValueOfOrNull(name: String): T? {
    return enumValues<T>().find { it.name == name }
}