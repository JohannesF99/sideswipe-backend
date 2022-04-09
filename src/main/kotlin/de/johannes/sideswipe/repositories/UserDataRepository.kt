package de.johannes.sideswipe.repositories

import de.johannes.sideswipe.model.UserData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserDataRepository: JpaRepository<UserData, Long> {
    fun save(user: UserData): UserData
    fun findByUsername(username: String): UserData
}
