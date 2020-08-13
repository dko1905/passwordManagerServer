package io.github.dko1905.passwordmanagerserver.domain

import java.util.UUID

class Token(val USERID: Long?, val UUID: UUID, val EXP: Long){
    override fun toString(): String {
        return "userid: ${USERID.toString()}, uuid: ${UUID.toString()}, exp: ${EXP.toString()}"
    }
}