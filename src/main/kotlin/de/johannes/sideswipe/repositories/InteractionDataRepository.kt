package de.johannes.sideswipe.repositories

import de.johannes.sideswipe.model.ContentData
import de.johannes.sideswipe.model.InteractionData
import de.johannes.sideswipe.model.UserData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

interface InteractionDataRepository: JpaRepository<InteractionData, Long> {
    fun save(interactionData: InteractionData): InteractionData
    fun findAllByContentData(contentData: ContentData): List<InteractionData>
    fun findAllByUserData(userData: UserData): List<InteractionData>
    fun findByUserDataAndContentData(userData: UserData, contentData: ContentData): InteractionData?
    @Transactional
    fun deleteByUserDataAndContentData(userData: UserData, contentData: ContentData)
}
