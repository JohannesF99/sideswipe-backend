package de.johannes.sideswipe.controller

import de.johannes.sideswipe.model.AccountCredentialData
import de.johannes.sideswipe.repositories.AccountCredentialDataRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/account")
class AccountCredentialDataController(
    private val accountCredentialDataRepository: AccountCredentialDataRepository
) {
    @DeleteMapping
    fun deleteAccountCredentials(@RequestBody username: String){
        accountCredentialDataRepository.deleteByUsername(username)
    }

    @GetMapping("/all")
    fun getAllAccounts(): Set<AccountCredentialData>{
        return accountCredentialDataRepository.findAll().toSet()
    }
}
