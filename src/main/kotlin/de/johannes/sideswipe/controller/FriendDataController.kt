package de.johannes.sideswipe.controller

import de.johannes.sideswipe.model.FriendData
import de.johannes.sideswipe.repositories.FriendDataRepository
import de.johannes.sideswipe.repositories.UserDataRepository
import org.apache.logging.log4j.LogManager
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/user/{username}/friend")
class FriendDataController(
    private val friendDataRepository: FriendDataRepository,
    private val userDataRepository: UserDataRepository) {

    private val logger = LogManager.getLogger()

    @PostMapping
    fun changeFriend(@PathVariable username: String, @RequestBody friendName: String) {
        val user = userDataRepository.findByUsername(username)
        val friend = userDataRepository.findByUsername(friendName)
        if (!friendDataRepository.existsByUserIdAndFriendData(user.userId, friend)) {
            friendDataRepository.save(FriendData(user, friend))
            logger.info("User \"$username\" added \"$friendName\" to his friends!")
        }
    }

    @DeleteMapping
    fun deleteFriend(@PathVariable username: String, @RequestBody friendName: String){
        val user = userDataRepository.findByUsername(username)
        val friend = userDataRepository.findByUsername(friendName)
        logger.info("User \"$username\" removed \"$friendName\" from his friends!")
        return friendDataRepository.deleteByUserIdAndFriendData(user.userId, friend)
    }

    @GetMapping("/all")
    fun getFriendsForUserData(@PathVariable username: String): Set<FriendData> {
        val user = userDataRepository.findByUsername(username)
        return friendDataRepository.findAllByUserId(user.userId)
    }
}
