package com.urepair.utilities

import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
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
