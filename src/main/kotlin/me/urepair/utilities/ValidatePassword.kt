package me.urepair.utilities

fun validatePassword(password: String): String {
    val digitRegex = ".*\\d.*".toRegex()
    val lowerCaseRegex = ".*[a-z].*".toRegex()
    val upperCaseRegex = ".*[A-Z].*".toRegex()
    val specialCharRegex = ".*[@$!%*?&].*".toRegex()

    return when {
        password.length < 8 -> "Password must be at least 8 characters long."
        !digitRegex.matches(password) -> "Password must contain at least one digit."
        !lowerCaseRegex.matches(password) -> "Password must contain at least one lowercase letter."
        !upperCaseRegex.matches(password) -> "Password must contain at least one uppercase letter."
        !specialCharRegex.matches(password) -> "Password must contain at least one special character (@, $, !, %, *, ?, &)."
        else -> "Valid password."
    }
}
