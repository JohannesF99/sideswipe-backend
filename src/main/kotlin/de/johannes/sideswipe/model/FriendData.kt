package de.johannes.sideswipe.model

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "FRIEND_DATA")
class FriendData(userData: UserData, friendData: UserData) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private var friendDataId: Long = 0

    private var userId: Long

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private val created: Date = Date()

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private var lastModified: Date = Date()

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "friend_id", nullable = false)
    var friendData: UserData? = null

    init {
        this.userId = userData.userId
        this.friendDataId = userData.userId
        this.friendData = friendData
    }
}
