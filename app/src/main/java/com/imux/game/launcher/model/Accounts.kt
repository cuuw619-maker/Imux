package com.imux.game.launcher.model

data class ImuxAccount(
    val id: String,
    val displayName: String,
    val avatarUri: String? = null,
    val identity: String? = null,
    val authenticationProvider: String? = null
)

interface AccountRepository {
    fun current(): ImuxAccount?
    fun list(): List<ImuxAccount>
}
