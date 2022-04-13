package de.johannes.sideswipe.controller

import de.johannes.sideswipe.enums.Gender
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
            user.doesTokenMatch()
            logger.info("User-Data requested for Username '$username'")
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

    @PutMapping("/{username}/name")
    fun updateUserDataName(@RequestBody name: String, @PathVariable username: String): UserData{
        try {
            val user = userDataRepository.findByUsername(username)
            user.doesTokenMatch()
            logger.info("User '$username' changed his last name from '${user.name}' to '$name'!")
            user.name = name
            return userDataRepository.save(user)
        } catch (e: EmptyResultDataAccessException){
            logger.warn("Last Name change for User-Data requested for unknown Username '$username'")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Last Name change for User-Data requested for unknown Username '$username'", e)
        } catch (e: SecurityException) {
            logger.warn("Authorization-Token does not match Username!")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization-Token does not match Username!")
        }
    }

    @PutMapping("/{username}/vorname")
    fun updateUserDataVorname(@RequestBody vorname: String, @PathVariable username: String): UserData{
        try {
            val user = userDataRepository.findByUsername(username)
            user.doesTokenMatch()
            logger.info("User '$username' changed his first name from '${user.vorname}' to '$vorname'!")
            user.vorname = vorname
            return userDataRepository.save(user)
        } catch (e: EmptyResultDataAccessException){
            logger.warn("First Name change for User-Data requested for unknown Username '$username'")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "First Name change for User-Data requested for unknown Username '$username'", e)
        } catch (e: SecurityException) {
            logger.warn("Authorization-Token does not match Username!")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization-Token does not match Username!")
        }
    }

    @PutMapping("/{username}/gender")
    fun updateUserDataGender(@RequestBody gender: Gender, @PathVariable username: String): UserData{
        try {
            val user = userDataRepository.findByUsername(username)
            user.doesTokenMatch()
            logger.info("User '$username' changed his gender from '${user.gender}' to '$gender'!")
            user.gender = gender
            return userDataRepository.save(user)
        } catch (e: EmptyResultDataAccessException){
            logger.warn("Gender change for User-Data requested for unknown Username '$username'")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Gender change for User-Data requested for unknown Username '$username'", e)
        } catch (e: SecurityException) {
            logger.warn("Authorization-Token does not match Username!")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization-Token does not match Username!")
        }
    }

    @PutMapping("/{username}/birthday")
    fun updateUserDataBirthday(@RequestBody birthday: String, @PathVariable username: String): UserData{
        try {
            val user = userDataRepository.findByUsername(username)
            user.doesTokenMatch()
            logger.info("User '$username' changed his date of birth from '${user.birthday}' to '$birthday'!")
            user.birthday = birthday
            return userDataRepository.save(user)
        } catch (e: EmptyResultDataAccessException){
            logger.warn("Birthday change for User-Data requested for unknown Username '$username'")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Birthday change for User-Data requested for unknown Username '$username'", e)
        } catch (e: SecurityException) {
            logger.warn("Authorization-Token does not match Username!")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization-Token does not match Username!")
        }
    }
}
