package de.johannes.sideswipe.model

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "INTERACTION_DATA")
data class InteractionData(
    var isLike: Boolean,
){
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private var interactionId: Long = 0

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private val created: Date = Date()

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private var lastModified: Date = Date()

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interacting_user_id", nullable = false)
    var userData: UserData? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_id", nullable = false)
    var contentData: ContentData? = null

    constructor(isLike: Boolean, userData: UserData, contentData: ContentData) : this(isLike) {
        this.userData = userData
        this.contentData = contentData
    }
}
