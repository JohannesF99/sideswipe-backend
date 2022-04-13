package de.johannes.sideswipe.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.Hibernate
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "ACCOUNT_CREDENTIAL_DATA")
data class AccountCredentialData(
    @Column(unique = true)
    var email: String,
    @Column(unique = true)
    var username: String,
    var password: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private var accoundCredentialId: Long = 0

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private val created: Date = Date()

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private var lastModified: Date = Date()

    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    private var user: UserData? = null

    private var loginToken: UUID? = null
    private var tokenExpireDate: Date? = null

    fun createLoginToken(): UUID{
        val newToken = UUID.randomUUID()
        this.loginToken = newToken
        val expireDate = Calendar.getInstance()
        expireDate.add(Calendar.HOUR_OF_DAY, 2)
        this.tokenExpireDate = expireDate.time
        return newToken
    }

    fun removeLoginToken(){
        this.loginToken = null
        this.tokenExpireDate = null
    }

    fun hasLoginToken(): UUID?{
        return this.loginToken
    }

    @JsonIgnore
    fun isLoginTokenExpired(): Boolean{
        return this.tokenExpireDate!!.before(Date())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as AccountCredentialData

        return accoundCredentialId == other.accoundCredentialId
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(accoundCredentialId = $accoundCredentialId , email = $email , username = $username , password = $password , created = $created , lastModified = $lastModified , loginToken = $loginToken , tokenExpireDate = $tokenExpireDate )"
    }
}
