package io.github.dko1905.passwordmanagerserver.repository

import io.github.dko1905.passwordmanagerserver.domain.Credential
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.platform.commons.logging.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CredentialRepositoryTest(@Autowired private val credentialRepository: CredentialRepository) {
    private val idsAdded: ArrayList<Long> = ArrayList()

    @Test
    fun `Add and then remove it`(){
        val credential = Credential(null, "123", "456", "789", "6969")

        credential.ACCOUNTID = credentialRepository.addCredential(credential)

        Assertions.assertNotNull(credential.ACCOUNTID)

        idsAdded.add(credential.ACCOUNTID!!)

        credentialRepository.removeCredential(credential.ACCOUNTID!!)

        idsAdded.remove(credential.ACCOUNTID!!)
    }

    @Test
    fun `Add and then get the credential`(){
        val credential = Credential(null, "123", "456", "789", "6969")

        credential.ACCOUNTID = credentialRepository.addCredential(credential)

        Assertions.assertNotNull(credential.ACCOUNTID)

        idsAdded.add(credential.ACCOUNTID!!)

        val credential2 = credentialRepository.getCredential(credential.ACCOUNTID!!)

        Assertions.assertNotNull(credential2)
        Assertions.assertNotNull(credential2!!.ACCOUNTID)

        Assertions.assertEquals(credential.ACCOUNTID!!, credential2.ACCOUNTID!!)
        Assertions.assertEquals(credential.EXTRA, credential2.EXTRA)
        Assertions.assertEquals(credential.PASSWORD, credential2.PASSWORD)
        Assertions.assertEquals(credential.URL, credential2.URL)
        Assertions.assertEquals(credential.USERNAME, credential2.USERNAME)
    }

    @Test
    fun `Add and then replace a thing and then get it and check it`(){
        val credential = Credential(null, "123", "456", "789", "6969")

        credential.ACCOUNTID = credentialRepository.addCredential(credential)

        Assertions.assertNotNull(credential.ACCOUNTID)

        idsAdded.add(credential.ACCOUNTID!!)

        val credential2 = Credential(credential.ACCOUNTID, credential.URL, "USER", credential.PASSWORD, credential.EXTRA)

        credentialRepository.replaceCredential(credential2)

        val credential3 = credentialRepository.getCredential(credential.ACCOUNTID!!)

        Assertions.assertNotNull(credential3)
        Assertions.assertNotNull(credential3!!.ACCOUNTID)

        Assertions.assertEquals(credential2.ACCOUNTID!!, credential3.ACCOUNTID!!)
        Assertions.assertEquals(credential2.EXTRA, credential3.EXTRA)
        Assertions.assertEquals(credential2.PASSWORD, credential3.PASSWORD)
        Assertions.assertEquals(credential2.URL, credential3.URL)
        Assertions.assertEquals(credential2.USERNAME, credential3.USERNAME)
    }

    @Test
    fun `Add credential and then check if it is returned in array`(){
        val credential = Credential(null, "123", "456", "789", "6969")

        credential.ACCOUNTID = credentialRepository.addCredential(credential)

        Assertions.assertNotNull(credential.ACCOUNTID)

        idsAdded.add(credential.ACCOUNTID!!)

        val creds = credentialRepository.getCredentials()

        var inside = false
        for(cred in creds){
            if(
                cred.ACCOUNTID == credential.ACCOUNTID &&
                cred.URL == credential.URL &&
                cred.USERNAME == credential.USERNAME &&
                cred.PASSWORD == credential.PASSWORD &&
                cred.EXTRA == credential.EXTRA
            ){
                inside = true
            }
        }
        if(!inside){
            throw Exception("Credential not found in array")
        }
    }

    @AfterAll
    fun clearDatabase(){
        for(id in idsAdded){
            credentialRepository.removeCredential(id)
        }
    }
}