package io.github.dko1905.passwordManagerServer.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.dko1905.passwordManagerServer.domain.Account
import io.github.dko1905.passwordManagerServer.domain.Token
import io.github.dko1905.passwordManagerServer.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.nio.charset.StandardCharsets
import io.github.dko1905.passwordManagerServer.domain.AccessDeniedException
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList

@RestController
@RequestMapping("/api")
class AuthController(@Autowired private val authService: AuthService) {
	private val logger = Logger.getLogger(AuthController::class.java.name)
	private val mapper = ObjectMapper()

	private fun decodeToken(str: String): Token?{
		try{
			val jObj = mapper.readValue<Map<String, String>>(str)

			if(!jObj.containsKey("userid") && !jObj.containsKey("exp") && !jObj.containsKey("uuid")){
				return null
			}
			val userid: Long? = jObj.getValue("userid").toLong()
			val exp = jObj.getValue("exp").toLong()
			val uuid = UUID.fromString(jObj.getValue("uuid"))
			return Token(userid, uuid, exp)
		} catch (e: Exception){
			throw e
		}
	}

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
			logger.info("Get /login UNAUTHORIZED")
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
		} else{
			try{
				val authInfo = decodeAuth(authHeader)
				val username = authInfo!!.first
				val password = authInfo.second

				val token = authService.login(username, password)

				if(token == null){
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null)
				} else{
					return ResponseEntity.ok(token)
				}
			} catch (ade: AccessDeniedException){
				logger.info("GET /login UNAUTHORIZED")
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
			} catch (e: Exception){
				logger.warning("GET /login INTERNAL_SERVER_ERROR: ${e.message}")
				e.printStackTrace()
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
			}
		}
	}

	@PostMapping("/account", produces = ["application/json"])
	fun addAccount(
			@RequestHeader("X-Auth-Token", required=true) headerToken: String,
			@RequestBody account: Account
	): ResponseEntity<String?>{
		try{
			var token: Token
			try{
				if(headerToken != ""){
					token = decodeToken(headerToken)!!
				} else {
					logger.info("POST /account FORBIDDEN: No token passed")
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
				}
			} catch (e: Exception){
				logger.info("POST /account BAD_REQUEST: ${e.message}")
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
			}

			authService.addAccount(token, account)

			return ResponseEntity.ok("OK")
		} catch (ade: AccessDeniedException){
			logger.info("POST /account UNAUTHORIZED")
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null)
		} catch (e: Exception){
			logger.warning("POST /account INTERNAL_SERVER_ERROR: ${e.message}")
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
		}
	}

	@DeleteMapping("/account/{id}", produces = ["application/json"])
	fun deleteAccount(
			@RequestHeader("X-Auth-Token", required = true) headerToken: String?,
			@PathVariable("id", required = true) id: Long
	): ResponseEntity<String>{
		try{
			var token: Token
			try{
				if(headerToken != null && headerToken != ""){
					token = decodeToken(headerToken)!!
				} else {
					logger.info("DELETE /account/$id FORBIDDEN: No token passed")
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
				}
			} catch (e: Exception){
				logger.info("DELETE /account/$id BAD_REQUEST: ${e.message}")
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
			}

			authService.deleteAccount(token, id)

			return ResponseEntity.ok("OK")
		} catch (ade: AccessDeniedException){
			if(ade.message == "NOT_FOUND"){
				logger.info("DELETE /account/$id NOT_FOUND")
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
			} else{
				logger.info("DELETE /account/$id UNAUTHORIZED")
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null)
			}
		} catch (e: Exception){
			logger.warning("DELETE /account/$id INTERNAL_SERVER_ERROR: ${e.message}")
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
		}
	}

	@GetMapping("/account", produces = ["application/json"])
	fun getAccounts(@RequestHeader("X-Auth-Token", required = true) headerToken: String?): ResponseEntity<ArrayList<Account>>{
		try{
			var token: Token
			try{
				if(headerToken != null && headerToken != ""){
					token = decodeToken(headerToken)!!
				} else {
					logger.info("GET /account FORBIDDEN: No token passed")
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
				}
			} catch (e: Exception){
				logger.info("GET /account BAD_REQUEST: ${e.message}")
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
			}

			return ResponseEntity.ok(authService.getAccounts(token))
		} catch (ade: AccessDeniedException){
			logger.info("GET /account UNAUTHORIZED")
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null)
		} catch (e: Exception){
			logger.warning("GET /account INTERNAL_SERVER_ERROR: ${e.message}")
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
		}
	}
}