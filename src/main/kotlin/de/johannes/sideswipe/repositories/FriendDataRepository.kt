package de.johannes.sideswipe.repositories

import de.johannes.sideswipe.model.FriendData
import de.johannes.sideswipe.model.UserData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface FriendDataRepository: JpaRepository<FriendData, Long> {
    fun save(friendData: FriendData): FriendData
    fun findAllByUserId(userId: Long): Set<FriendData>
    fun existsByUserIdAndFriendData(userId: Long, friendData: UserData): Boolean
    @Transactional
    fun deleteByUserIdAndFriendData(userId: Long, friendData: UserData)
}
