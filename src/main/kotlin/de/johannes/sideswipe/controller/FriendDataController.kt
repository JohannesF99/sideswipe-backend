package de.johannes.sideswipe.controller

import de.johannes.sideswipe.model.FriendData
import de.johannes.sideswipe.repositories.FriendDataRepository
import de.johannes.sideswipe.repositories.UserDataRepository
import org.apache.logging.log4j.LogManager
import org.springframework.dao.EmptyResultDataAccessException
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
            user.doesTokenMatch()
            if (!friendDataRepository.existsByUserIdAndFriendData(user.userId, friend)) {
                friendDataRepository.save(FriendData(user, friend))
                logger.info("User '$username' added '$friendName' to his friends!")
            }
        } catch (e: EmptyResultDataAccessException){
            logger.warn("Friend-Request unsuccessful, because User-ID '$username' or Friend '$friendName' could not be found!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend-Request unsuccessful, because User-ID '$username' or Friend '$friendName' could not be found!", e)
        } catch (e: SecurityException) {
            logger.warn("Authorization-Token does not match Username!")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization-Token does not match Username!", e)
        }
    }

    @DeleteMapping
    fun deleteFriend(@PathVariable username: String, @RequestBody friendName: String){
        try {
            val user = userDataRepository.findByUsername(username)
            val friend = userDataRepository.findByUsername(friendName)
            user.doesTokenMatch()
            logger.info("User '$username' removed '$friendName' from his friends!")
            return friendDataRepository.deleteByUserIdAndFriendData(user.userId, friend)
        } catch (e: EmptyResultDataAccessException){
            logger.warn("Removal of Friend-Request unsuccessful, because User-ID '$username' or Friend '$friendName' could not be found!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Removal of Friend-Request unsuccessful, because User-ID '$username' or Friend '$friendName' could not be found!", e)
        } catch (e: SecurityException) {
            logger.warn("Authorization-Token does not match Username!")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization-Token does not match Username!", e)
        }
    }

    @GetMapping("/all")
    fun getFriendsForUserData(@PathVariable username: String): List<String> {
        try {
            val user = userDataRepository.findByUsername(username)
            user.doesTokenMatch()
            logger.info("Fetched Friends-List for Username '$username'!")
            return friendDataRepository.findAllByUserId(user.userId)
                .map { it.friendData!!.username }
        } catch (e: EmptyResultDataAccessException){
            logger.warn("Could not fetch Friends-List for unknown Username '$username'!")
            throw  ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not fetch Friends-List for unknown Username '$username'!", e)
        } catch (e: SecurityException) {
            logger.warn("Authorization-Token does not match Username!")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization-Token does not match Username!", e)
        }
    }
}
