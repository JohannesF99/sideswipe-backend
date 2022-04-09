package de.johannes.sideswipe.model

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "CONTENT_DATA")
data class ContentData(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var contentId: Long = 0,
    var caption: String,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private val created: Date = Date(),

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    var lastModified: Date = Date()
) {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private var userData: UserData? = null

    @OneToMany(mappedBy = "contentData", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    private val interactionData: Set<InteractionData> = mutableSetOf()

    constructor(contentData: ContentData, userData: UserData)
            : this(contentData.contentId, contentData.caption) {
        this.userData = userData
    }
}
