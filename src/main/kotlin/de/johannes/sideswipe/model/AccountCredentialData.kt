package de.johannes.sideswipe.model

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
    var password: String,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private val created: Date = Date(),

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private var lastModified: Date = Date()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private var accoundCredentialId: Long = 0

    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    private var user: UserData? = null
}
