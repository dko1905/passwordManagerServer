package io.github.dko1905.passwordmanagerserver.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

class Token(
		@JsonProperty("userid")
		val USERID: Long?,
		@JsonProperty("uuid")
		val UUID: UUID,
		@JsonProperty("exp")
		val EXP: Long
){
    override fun toString(): String {
        return "userid: ${USERID.toString()}, uuid: $UUID, exp: $EXP"
    }
}