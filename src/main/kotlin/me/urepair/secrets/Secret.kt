package me.urepair.secrets

import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class Secret(val keyStoreSecret: String, val keyStoreAlias: String)

fun getSecret(secretName: String): Secret {
    val client = AWSSecretsManagerClientBuilder.defaultClient()
    val getSecretValueRequest = GetSecretValueRequest().withSecretId(secretName)
    val getSecretValueResult = client.getSecretValue(getSecretValueRequest)
    val secretValue = getSecretValueResult.secretString
    return Json.decodeFromString(secretValue)
}
