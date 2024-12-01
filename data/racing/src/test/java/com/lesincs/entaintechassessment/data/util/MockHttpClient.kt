package com.lesincs.entaintechassessment.data.util

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.append
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun getMockHttpClient(
    path: String,
    success: Boolean,
    response: String = "",
) = HttpClient(MockEngine) {
    engine {
        addHandler { request ->
            if (path == request.url.encodedPathAndQuery && success) {
                respond(
                    response,
                    HttpStatusCode.OK,
                    headers {
                        append(HttpHeaders.ContentType, ContentType.Application.Json)
                    }
                )
            } else {
                error("Error response for ${request.url.encodedPath}")
            }
        }
    }
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    defaultRequest {
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
        url("https://api.neds.com.au/rest/v1/")
    }
}
