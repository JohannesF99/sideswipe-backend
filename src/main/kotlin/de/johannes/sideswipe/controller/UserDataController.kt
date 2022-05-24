package de.johannes.sideswipe.controller

import de.johannes.sideswipe.model.UserData
import de.johannes.sideswipe.repositories.UserDataRepository
import org.apache.logging.log4j.LogManager
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api/v1/user")
class UserDataController(private val userDataRepository: UserDataRepository) {

    private val logger = LogManager.getLogger()

    @GetMapping("/{username}")
    fun getUserData(@PathVariable username: String): UserData{
        try {
            val user = userDataRepository.findByUsername(username)
            //Enable if User should only fetch their own UserData
            //user.doesTokenMatch()
            logger.info("User-Data requested for Username '$username'!")
            return user
        } catch (e: EmptyResultDataAccessException){
            logger.warn("User-Data requested for unknown Username '$username'")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User-Data requested for unknown Username '$username'", e)
        } catch (e: SecurityException) {
            logger.warn("Authorization-Token does not match Username!")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization-Token does not match Username!")
        }
    }

    @GetMapping("/all")
    fun getAllUser(): ArrayList<UserData>{
        return userDataRepository.findAll() as ArrayList<UserData>
    }

    @PutMapping("/{username}")
    fun updateUserDataName(@RequestBody newUserData: UserData, @PathVariable username: String): UserData{
        try {
            val user = userDataRepository.findByUsername(username)
            user.doesTokenMatch()
            logger.info("User '$username' changed his properties!")
            user.updateFrom(newUserData)
            return userDataRepository.save(user)
        } catch (e: EmptyResultDataAccessException){
            logger.warn("Property change requested for unknown Username '$username'")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Property change requested for unknown Username '$username'", e)
        } catch (e: SecurityException) {
            logger.warn("Authorization-Token does not match Username!")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization-Token does not match Username!")
        }
    }
}
