package me.urepair.secrets

import kotlinx.serialization.Serializable
import me.urepair.utilities.isValidEmail

@Serializable
data class HashedStaffSecret(val staffSecret: String, val staffEmail: String) { init {
    staffEmail.let {
        require(it.length <= 255) { "Email cannot exceed 255 characters" }
        require(isValidEmail(it)) { "Invalid email address" }
    }
} }
