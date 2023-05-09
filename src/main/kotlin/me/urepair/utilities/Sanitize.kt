package me.urepair.utilities

fun sanitize(input: String): String {
    return input.filter { it.isLetterOrDigit() || it.isWhitespace() || it == '.' || it == ',' || it == '-' || it == '_' || it == '!' || it == '?' }
}
