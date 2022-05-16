package de.johannes.sideswipe.repositories

import de.johannes.sideswipe.model.ContentData
import de.johannes.sideswipe.model.UserData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface ContentDataRepository: JpaRepository<ContentData, Long> {
    fun save(contentData: ContentData): ContentData
    fun findByContentId(contentId: Long): ContentData
    fun findAllByUserData(userData: UserData): Set<ContentData>
    @Transactional
    fun deleteByContentId(contentId: Long)
}
