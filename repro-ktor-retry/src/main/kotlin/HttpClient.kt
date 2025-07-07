package com.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.Closeable
import java.io.IOException
import java.util.concurrent.CancellationException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


class HttpClient(
    val accept: ContentType = ContentType.Application.Json,
    val contentType: ContentType = ContentType.Application.Json,
    val baseUrl: String,
    val token: String? = null,
    val additionalHeaders: Map<String, String> = emptyMap(),
    val timeout: Duration = 3.seconds,
    val retries: Int = 3,
    val retryableStatusCodes: Set<HttpStatusCode> = setOf(
        HttpStatusCode.RequestTimeout,
        HttpStatusCode.TooEarly,
        HttpStatusCode.TooManyRequests,
        HttpStatusCode.InternalServerError,
        HttpStatusCode.BadGateway,
        HttpStatusCode.ServiceUnavailable,
        HttpStatusCode.GatewayTimeout
    )
) : Closeable {

    @PublishedApi
    internal val client = HttpClient(Java) {
        expectSuccess = true

        engine {
            protocolVersion = java.net.http.HttpClient.Version.HTTP_2
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }

        install(HttpTimeout) {
            connectTimeoutMillis = timeout.inWholeMilliseconds
            requestTimeoutMillis = timeout.inWholeMilliseconds
            socketTimeoutMillis = timeout.inWholeMilliseconds
        }

        install(HttpRequestRetry) {
            maxRetries = retries
            exponentialDelay(maxDelayMs = 2000)
            retryOnExceptionIf { _, cause ->
                when (cause) {
                    is CancellationException -> true
                    is ClientRequestException -> cause.response.status in retryableStatusCodes
                    is HttpRequestTimeoutException -> true
                    is IOException -> true
                    is ServerResponseException -> cause.response.status in retryableStatusCodes
                    else -> false
                }
            }

            modifyRequest {
                println("Retried...")
            }
        }
    }

    @PublishedApi
    internal fun HttpRequestBuilder.prepareRequest(path: String) {
        url {
            takeFrom(baseUrl)
            appendPathSegments(path.split('/').filter { it.isNotEmpty() })
        }
        accept(this@HttpClient.accept)
        token?.let { bearerAuth(it) }
        additionalHeaders.forEach { (key, value) -> header(key, value) }
    }

    suspend inline fun <reified T : Any, reified R : Any> post(path: String, requestBody: T): Result<R> {
        return runCatching {
            client.post {
                prepareRequest(path)
                contentType(this@HttpClient.contentType)
                when (this@HttpClient.contentType) {
                    ContentType.Application.Json -> setBody(requestBody)
                    ContentType.Application.FormUrlEncoded -> {
                        if (requestBody is Parameters) {
                            setBody(FormDataContent(requestBody))
                        } else {
                            throw IllegalArgumentException("Request body must be of type Parameters for FormUrlEncoded content type")
                        }
                    }

                    else -> throw IllegalArgumentException("Unsupported content type: ${this@HttpClient.contentType}")
                }
            }.body<R>()
        }
    }

    suspend inline fun <reified R : Any> get(path: String): Result<R> {
        return runCatching {
            client.get {
                prepareRequest(path)
            }.body<R>()
        }
    }

    override fun close() {
        client.close()
    }
}