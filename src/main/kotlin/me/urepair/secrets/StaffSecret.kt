package me.urepair.secrets

import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest
import com.amazonaws.services.secretsmanager.model.PutSecretValueRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class StaffSecret(val staffSecret: String, val staffEmail: String)

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
