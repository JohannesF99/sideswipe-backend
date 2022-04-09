package de.johannes.sideswipe.controller

import de.johannes.sideswipe.model.AccountCredentialData
import de.johannes.sideswipe.model.UserData
import de.johannes.sideswipe.repositories.AccountCredentialDataRepository
import de.johannes.sideswipe.repositories.UserDataRepository
import de.johannes.sideswipe.service.PasswordEncoderService.Companion.toSHA256
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/account")
class AccountCredentialDataController(
    private val accountCredentialDataRepository: AccountCredentialDataRepository,
    private val userDataRepository: UserDataRepository
) {
    @PostMapping
    fun addAccountCredentials(@RequestBody accountCredentialData: AccountCredentialData){
        accountCredentialData.password = accountCredentialData.password.toSHA256()
        accountCredentialDataRepository.save(accountCredentialData)
        userDataRepository.save(UserData(accountCredentialData))
    }

    @DeleteMapping
    fun deleteAccountCredentials(@RequestBody username: String){
        accountCredentialDataRepository.deleteByUsername(username)
    }

    @GetMapping("/all")
    fun getAllAccounts(): Set<AccountCredentialData>{
        return accountCredentialDataRepository.findAll().toSet()
    }
}
