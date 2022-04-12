package de.johannes.sideswipe.security

import de.johannes.sideswipe.repositories.AccountCredentialDataRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

@Component
class AuthenticationProvider(private val accountCredentialDataRepository: AccountCredentialDataRepository):
    AbstractUserDetailsAuthenticationProvider() {

    override fun additionalAuthenticationChecks(
        userDetails: UserDetails?,
        authentication: UsernamePasswordAuthenticationToken?
    ) {}

    override fun retrieveUser(username: String, authentication: UsernamePasswordAuthenticationToken): UserDetails? {
        val token = authentication.credentials to UUID::class
        val account = accountCredentialDataRepository.findByLoginToken(token.second.objectInstance)
        return User(account.username,account.password, true, true, true, true,
            AuthorityUtils.createAuthorityList("USER"))
    }
}
