package io.github.dko1905.passwordmanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.dko1905.passwordmanagerserver.domain.Account
import io.github.dko1905.passwordmanagerserver.domain.Token
import io.github.dko1905.passwordmanagerserver.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.*
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.logging.Logger
import kotlin.math.log
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

@RestController
@RequestMapping("/api")
class AuthController(@Autowired private val authService: AuthService) {
	private val logger = Logger.getLogger(AuthController::class.java.name)
	private val mapper = ObjectMapper()

	@GetMapping("/login", produces = ["application/json"])
	fun login(@RequestHeader("Authorization") authHeader: String?): ResponseEntity<Token> {
		fun decodeAuth(authorization: String): Pair<String, String>? {
			if (authorization.toLowerCase().startsWith("basic")) {
				// Authorization: Basic base64credentials
				val base64Credentials: String = authorization.substring("Basic".length).trim()
				val credDecoded: ByteArray = Base64.getDecoder().decode(base64Credentials)
				val credentials = String(credDecoded, StandardCharsets.UTF_8)
				// credentials = username:password
				val values = credentials.split(":".toRegex(), 2).toTypedArray()
				return Pair(values[0], values[1])
			}
			return null
		}

		if(authHeader == null ||  !authHeader.startsWith("Basic")){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
		} else{
			try{
				val authInfo = decodeAuth(authHeader)
				val username = authInfo!!.first
				val password = authInfo.second

				logger.info("Username: $username, Password len: ${password.length}")


				val token = authService.login(username, password)

				return ResponseEntity.ok(token!!)
			} catch (ade: AccessDeniedException){
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
			} catch (e: Exception){
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
			}
		}
	}

	@GetMapping("/account", produces = ["application/json"])
	fun getAccounts(
			@RequestHeader("X-Auth-Token") headerToken: String?,
			@CookieValue("SESSIONTOKEN") cookieToken: String?
	): ResponseEntity<String>{
		fun decodeToken(str: String): Token?{
			try{
				val jObj = mapper.readValue<Map<String, Object>>(str)

				if(!jObj.containsKey("userid") && !jObj.containsKey("exp") && !jObj.containsKey("uuid")){
					return null
				}
				val userid = jObj.getValue("userid") as Long?
				val exp = jObj.getValue("exp") as Long
				val uuid = UUID.fromString(jObj.getValue("uuid") as String)
				return Token(userid, uuid, exp)
			} catch (e: Exception){
				throw e
			}
		}

		try{
			logger.info("String is $headerToken")

			var token: Token? = null
			token = if(headerToken != null && headerToken != ""){
				decodeToken(headerToken)
			} else if(cookieToken != null && cookieToken != ""){
				decodeToken(cookieToken)
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null)
			}

			logger.info("Token is $token")
		} catch (ade: AccessDeniedException){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
		} catch (e: Exception){
			logger.warning("Error in GET /account: ${e.message}")
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
		}

		return ResponseEntity.status(HttpStatus.OK).body("Hello")
	}
}