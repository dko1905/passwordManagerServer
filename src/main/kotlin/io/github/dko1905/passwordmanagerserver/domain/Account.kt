package io.github.dko1905.passwordmanagerserver.domain

class Account(var ID: Long?, val USERNAME: String, val HASH: String, val ACCOUNTROLE: AccountRole)