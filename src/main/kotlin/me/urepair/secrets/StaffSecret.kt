package me.urepair.secrets

import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest
import com.amazonaws.services.secretsmanager.model.PutSecretValueRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.urepair.utilities.isValidEmail
import me.urepair.utilities.validatePassword

@Serializable
data class StaffSecret(val staffSecret: String, val staffEmail: String) { init {
    staffEmail.let {
        require(it.length <= 255) { "Email cannot exceed 255 characters" }
        require(isValidEmail(it)) { "Invalid email address" }
    }
    staffSecret.let {
        require(it.length <= 50) { "Password cannot exceed 50 characters" }
        val passwordValidationResult = validatePassword(it)
        require(passwordValidationResult == "Valid password.") { passwordValidationResult }
    }
} }

fun getStaffSecret(secretName: String): StaffSecret {
    val client = AWSSecretsManagerClientBuilder.defaultClient()
    val getSecretValueRequest = GetSecretValueRequest().withSecretId(secretName)
    val getSecretValueResult = client.getSecretValue(getSecretValueRequest)
    val secretValue = getSecretValueResult.secretString
    return Json.decodeFromString(secretValue)
}

fun updateStaffSecret(secretName: String, newSecret: StaffSecret) {
    val client = AWSSecretsManagerClientBuilder.defaultClient()
    val jsonSecret = Json.encodeToString(newSecret)
    val putSecretValueRequest = PutSecretValueRequest().apply {
        secretId = secretName
        secretString = jsonSecret
    }
    client.putSecretValue(putSecretValueRequest)
}
