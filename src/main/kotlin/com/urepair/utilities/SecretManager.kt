package com.urepair.utilities

import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

fun getSecret(secretName: String): JsonObject {
    val client = AWSSecretsManagerClientBuilder.defaultClient()
    val getSecretValueRequest = GetSecretValueRequest().withSecretId(secretName)
    val getSecretValueResult = client.getSecretValue(getSecretValueRequest)
    val secretValue = getSecretValueResult.secretString
    return Json.parseToJsonElement(secretValue) as JsonObject
}
