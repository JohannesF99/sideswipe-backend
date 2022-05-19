package de.johannes.sideswipe.controller

import de.johannes.sideswipe.model.AccountCredentialData
import de.johannes.sideswipe.repositories.AccountCredentialDataRepository
import de.johannes.sideswipe.repositories.ContentDataRepository
import de.johannes.sideswipe.repositories.UserDataRepository
import de.johannes.sideswipe.service.PasswordEncoderService.Companion.toSHA256
import org.apache.logging.log4j.LogManager
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api/v1/account")
class AccountCredentialDataController(
    private val accountCredentialDataRepository: AccountCredentialDataRepository,
    private val userDataRepository: UserDataRepository,
    private val contentDataRepository: ContentDataRepository
) {
    private val logger = LogManager.getLogger()

    @DeleteMapping
    fun deleteAccountCredentials(@RequestBody accountCredentialData: AccountCredentialData){
        try {
            accountCredentialDataRepository.findByUsernameAndPassword(
                accountCredentialData.username,
                accountCredentialData.password.toSHA256()
            ) ?: throw IllegalAccessException("Combination of Username and Password does not match!")
            val user = userDataRepository.findByUsername(accountCredentialData.username)
            user.doesTokenMatch()
            logger.info("Account with Username '${accountCredentialData.username}' got deleted!")
            contentDataRepository.deleteAllByUserData(user)
            accountCredentialDataRepository.deleteByUsername(accountCredentialData.username)
        } catch (e: EmptyResultDataAccessException){
            logger.warn("Could not delete User for unknown Username '${accountCredentialData.username}'")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not delete User for unknown Username '${accountCredentialData.username}'", e)
        } catch (e: SecurityException) {
            logger.warn("Authorization-Token does not match Username!")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization-Token does not match Username!")
        } catch (e: IllegalAccessException) {
            logger.warn(e.message)
            throw ResponseStatusException(HttpStatus.CONFLICT, e.message)
        }
    }

    @GetMapping("/all")
    fun getAllAccounts(): Set<AccountCredentialData>{
        return accountCredentialDataRepository.findAll().toSet()
    }
}
