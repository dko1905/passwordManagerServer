package io.github.dko1905.passwordManagerServer.domain

import com.fasterxml.jackson.annotation.JsonProperty

class Account(
		@JsonProperty("id")
		var ID: Long?,
		@JsonProperty("username")
		val USERNAME: String,
		@JsonProperty("hash")
		val HASH: String,
		@JsonProperty("accountrole")
		val ACCOUNTROLE: AccountRole
){
    override fun toString(): String {
        return "ID=${ID},USERNAME=${USERNAME},HASH=${HASH},ACCOUNTROLE=${ACCOUNTROLE}"
    }
}