package me.urepair.utilities

import jakarta.mail.internet.AddressException
import jakarta.mail.internet.InternetAddress

fun isValidEmail(email: String): Boolean {
    return try {
        val emailAddress = InternetAddress(email)
        emailAddress.validate()
        true
    } catch (e: AddressException) {
        false
    }
}
