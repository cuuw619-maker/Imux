package com.imux.gamecore

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class VersionInfo(
    val id: String,
    val name: String,
    val tagName: String,
    val repository: String,
    val isCurrent: Boolean = false
)

@Serializable
private data class GitHubRelease(
    val tag_name: String,
    val name: String? = null,
    val prerelease: Boolean = false,
    val draft: Boolean = false
)

/** Repository-backed version catalog owned entirely by game-core. */
object VersionManager {
    private const val CURRENT_REPOSITORY = "cuuw619-maker/Imux"
    private val archiveRepositories = emptyList<String>()

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun fetchLatest(): VersionInfo {
        val release = client.get("https://api.github.com/repos/$CURRENT_REPOSITORY/releases/latest") {
            header(HttpHeaders.Accept, "application/vnd.github+json")
        }.body<GitHubRelease>()

        return VersionInfo(
            id = release.tag_name,
            name = release.name?.takeIf { it.isNotBlank() } ?: release.tag_name,
            tagName = release.tag_name,
            repository = CURRENT_REPOSITORY,
            isCurrent = true
        )
    }

    suspend fun fetchArchives(): List<VersionInfo> = archiveRepositories.flatMap { repository ->
        runCatching {
            client.get("https://api.github.com/repos/$repository/releases") {
                header(HttpHeaders.Accept, "application/vnd.github+json")
            }.body<List<GitHubRelease>>()
                .filterNot { it.draft }
                .map { release ->
                    VersionInfo(
                        id = "${repository}:${release.tag_name}",
                        name = release.name?.takeIf { it.isNotBlank() } ?: release.tag_name,
                        tagName = release.tag_name,
                        repository = repository
                    )
                }
        }.getOrDefault(emptyList())
    }

    fun close() = client.close()
}
