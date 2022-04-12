package de.johannes.sideswipe.repositories

import de.johannes.sideswipe.model.AccountCredentialData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface AccountCredentialDataRepository: JpaRepository<AccountCredentialData, Long> {
    fun save(accountCredentialData: AccountCredentialData): AccountCredentialData
    fun findByUsernameAndPassword(username: String, password: String): AccountCredentialData?
    fun findByLoginToken(token: UUID?): AccountCredentialData
    @Transactional
    fun deleteByUsername(username: String)
}
