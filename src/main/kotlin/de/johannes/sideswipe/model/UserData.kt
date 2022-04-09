package de.johannes.sideswipe.model

import de.johannes.sideswipe.enums.Gender
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "USER_DATA")
data class UserData(
    @Column(unique = true)
    var username: String,

    @Column(unique = true)
    var email: String,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private var created: Date = Date(),

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private var lastModified: Date = Date(),

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private var account: AccountCredentialData? = null,

    var name: String?,
    var vorname: String?,
    var gender: Gender,
    var birthday: String?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var userId: Long = 0

    @OneToMany(mappedBy = "userData", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    private val contentData: Set<ContentData> = mutableSetOf()

    @OneToMany(mappedBy = "userData", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    private val interactionData: Set<InteractionData> = mutableSetOf()

    @OneToMany(mappedBy = "friendData", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    private val friendData: Set<FriendData> = mutableSetOf()

    constructor(account: AccountCredentialData):
            this(account.username,
                account.email,
                Date(),
                Date(),
                account,
                null,
                null,
                Gender.Unkown,
                null)
}
