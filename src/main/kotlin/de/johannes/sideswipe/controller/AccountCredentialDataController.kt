package de.johannes.sideswipe.controller

import de.johannes.sideswipe.model.AccountCredentialData
import de.johannes.sideswipe.repositories.AccountCredentialDataRepository
import org.apache.logging.log4j.LogManager
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/account")
class AccountCredentialDataController(
    private val accountCredentialDataRepository: AccountCredentialDataRepository
) {
    private val logger = LogManager.getLogger()

    @DeleteMapping
    fun deleteAccountCredentials(@RequestBody username: String){
        logger.info("Account with Username '$username' got deleted!")
        accountCredentialDataRepository.deleteByUsername(username)
    }

    @GetMapping("/all")
    fun getAllAccounts(): Set<AccountCredentialData>{
        return accountCredentialDataRepository.findAll().toSet()
    }
}
