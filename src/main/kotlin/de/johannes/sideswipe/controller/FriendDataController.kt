package de.johannes.sideswipe.controller

import de.johannes.sideswipe.model.FriendData
import de.johannes.sideswipe.repositories.FriendDataRepository
import de.johannes.sideswipe.repositories.UserDataRepository
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api/v1/user/{username}/friend")
class FriendDataController(
    private val friendDataRepository: FriendDataRepository,
    private val userDataRepository: UserDataRepository) {

    private val logger = LogManager.getLogger()

    @PostMapping
    fun changeFriend(@PathVariable username: String, @RequestBody friendName: String) {
        try {
            val user = userDataRepository.findByUsername(username)
            val friend = userDataRepository.findByUsername(friendName)
            if (!friendDataRepository.existsByUserIdAndFriendData(user.userId, friend)) {
                friendDataRepository.save(FriendData(user, friend))
                logger.info("User '$username' added '$friendName' to his friends!")
            }
        } catch (e: Exception){
            logger.warn("Friend-Request unsuccessful, because User-ID '$username' or Friend '$friendName' could not be found!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend-Request unsuccessful, because User-ID '$username' or Friend '$friendName' could not be found!", e)
        }
    }

    @DeleteMapping
    fun deleteFriend(@PathVariable username: String, @RequestBody friendName: String){
        try {
            val user = userDataRepository.findByUsername(username)
            val friend = userDataRepository.findByUsername(friendName)
            logger.info("User '$username' removed '$friendName' from his friends!")
            return friendDataRepository.deleteByUserIdAndFriendData(user.userId, friend)
        } catch (e: Exception){
            logger.warn("Removal of Friend-Request unsuccessful, because User-ID '$username' or Friend '$friendName' could not be found!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Removal of Friend-Request unsuccessful, because User-ID '$username' or Friend '$friendName' could not be found!", e)
        }
    }

    @GetMapping("/all")
    fun getFriendsForUserData(@PathVariable username: String): Set<FriendData> {
        try {
            val user = userDataRepository.findByUsername(username)
            logger.info("Fetched Friends-List for Username '$username'!")
            return friendDataRepository.findAllByUserId(user.userId)
        } catch (e: Exception){
            logger.warn("Could not fetch Friends-List for unknown Username '$username'!")
            throw  ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not fetch Friends-List for unknown Username '$username'!", e)
        }
    }
}
