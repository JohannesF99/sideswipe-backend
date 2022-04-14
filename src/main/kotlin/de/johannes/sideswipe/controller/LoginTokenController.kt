package de.johannes.sideswipe.controller

import de.johannes.sideswipe.model.AccountCredentialData
import de.johannes.sideswipe.model.UserData
import de.johannes.sideswipe.repositories.AccountCredentialDataRepository
import de.johannes.sideswipe.repositories.UserDataRepository
import de.johannes.sideswipe.service.PasswordEncoderService.Companion.toSHA256
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/public/api/v1/account")
class LoginTokenController(
    private val accountCredentialDataRepository: AccountCredentialDataRepository,
    private val userDataRepository: UserDataRepository
) {
    private val logger = LogManager.getLogger()

    @PostMapping
    fun addAccountCredentials(@RequestBody accountCredentialData: AccountCredentialData){
        try {
            accountCredentialData.password = accountCredentialData.password.toSHA256()
            accountCredentialDataRepository.save(accountCredentialData)
            val user = UserData(accountCredentialData)
            userDataRepository.save(user)
            logger.info("New Account '${accountCredentialData.username}' created!")
            logger.info("Corresponding User with User-ID ${user.userId} created!")
        } catch (e: Exception){
            logger.warn("Account-Creation failed, because Username/E-Mail is already registered!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Account-Creation failed, because Username/E-Mail is already registered!", e)
        }
    }

    @PostMapping("/login")
    fun loginAccount(@RequestBody account: AccountCredentialData): UUID {
        val accountData = accountCredentialDataRepository.findByUsernameAndPassword(account.username, account.password.toSHA256())
        if (accountData != null) {
            accountData.hasLoginToken() ?: accountData.createLoginToken()
            if (accountData.isLoginTokenExpired()) {
                accountData.createLoginToken()
            }
            accountCredentialDataRepository.save(accountData)
            logger.info("Login from Account '${accountData.username}'!")
            return accountData.hasLoginToken()!!
        } else {
            logger.warn("No Account for given Credentials can be found!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "No Account for given Credentials can be found!")
        }
    }

    @PostMapping("/logout")
    fun logoutAccount(@RequestBody account: AccountCredentialData) {
        val accountData = accountCredentialDataRepository.findByUsernameAndPassword(account.username, account.password.toSHA256())
        if (accountData != null){
            accountData.removeLoginToken()
            accountCredentialDataRepository.save(accountData)
            logger.info("Logout from Account '${accountData.username}'!")
        } else {
            logger.warn("No Account for given Credentials can be found!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "No Account for given Credentials can be found!")
        }
    }
}
