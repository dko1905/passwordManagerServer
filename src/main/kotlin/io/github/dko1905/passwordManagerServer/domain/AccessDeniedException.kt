package io.github.dko1905.passwordManagerServer.domain

class AccessDeniedException : RuntimeException {
	constructor(msg: String) : super(msg)
	constructor(msg: String, t: Throwable) : super(msg, t)
}
