package de.johannes.sideswipe.repositories

import de.johannes.sideswipe.model.ContentData
import de.johannes.sideswipe.model.UserData
import org.springframework.data.jpa.repository.JpaRepository

interface ContentDataRepository: JpaRepository<ContentData, Long> {
    fun save(contentData: ContentData): ContentData
    fun findByContentId(contentId: Long): ContentData
    fun findAllByUserData(userData: UserData): Set<ContentData>
}
