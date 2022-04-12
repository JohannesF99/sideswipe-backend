package de.johannes.sideswipe.controller

import de.johannes.sideswipe.model.AccountCredentialData
import de.johannes.sideswipe.model.UserData
import de.johannes.sideswipe.repositories.AccountCredentialDataRepository
import de.johannes.sideswipe.repositories.UserDataRepository
import de.johannes.sideswipe.service.PasswordEncoderService.Companion.toSHA256
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public/api/v1/account")
class LoginTokenController(
    private val accountCredentialDataRepository: AccountCredentialDataRepository,
    private val userDataRepository: UserDataRepository
) {
    @PostMapping
    fun addAccountCredentials(@RequestBody accountCredentialData: AccountCredentialData){
        accountCredentialData.password = accountCredentialData.password.toSHA256()
        accountCredentialDataRepository.save(accountCredentialData)
        userDataRepository.save(UserData(accountCredentialData))
    }

    @PostMapping("/login")
    fun loginAccount(@RequestBody account: AccountCredentialData): ResponseEntity<*> {
        val accountData = accountCredentialDataRepository.findByUsernameAndPassword(account.username, account.password.toSHA256())
        if (accountData != null) {
            accountData.hasLoginToken() ?: accountData.createLoginToken()
            accountCredentialDataRepository.save(accountData)
            return ResponseEntity.ok().body(accountData.hasLoginToken())
        }
        return ResponseEntity.badRequest().body("No User with given Credentials found!")
    }

    @PostMapping("/logout")
    fun logoutAccount(@RequestBody account: AccountCredentialData) {
        val accountData = accountCredentialDataRepository.findByUsernameAndPassword(account.username, account.password.toSHA256())
        if (accountData != null){
            accountData.removeLoginToken()
            accountCredentialDataRepository.save(accountData)
        }
    }
}
