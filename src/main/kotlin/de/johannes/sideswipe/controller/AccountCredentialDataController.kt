package de.johannes.sideswipe.controller

import de.johannes.sideswipe.model.AccountCredentialData
import de.johannes.sideswipe.repositories.AccountCredentialDataRepository
import de.johannes.sideswipe.repositories.UserDataRepository
import org.apache.logging.log4j.LogManager
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api/v1/account")
class AccountCredentialDataController(
    private val accountCredentialDataRepository: AccountCredentialDataRepository,
    private val userDataRepository: UserDataRepository
) {
    private val logger = LogManager.getLogger()

    @DeleteMapping
    fun deleteAccountCredentials(@RequestBody username: String){
        try {
            val user = userDataRepository.findByUsername(username)
            user.doesTokenMatch()
            logger.info("Account with Username '$username' got deleted!")
            accountCredentialDataRepository.deleteByUsername(username)
        } catch (e: EmptyResultDataAccessException){
            logger.warn("Could not delete User for unknown Username '$username'")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not delete User for unknown Username '$username'", e)
        } catch (e: SecurityException) {
            logger.warn("Authorization-Token does not match Username!")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization-Token does not match Username!")
        }
    }

    @GetMapping("/all")
    fun getAllAccounts(): Set<AccountCredentialData>{
        return accountCredentialDataRepository.findAll().toSet()
    }
}
