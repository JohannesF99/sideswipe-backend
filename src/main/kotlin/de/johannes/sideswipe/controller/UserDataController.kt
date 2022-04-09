package de.johannes.sideswipe.controller

import de.johannes.sideswipe.enums.Gender
import de.johannes.sideswipe.model.UserData
import de.johannes.sideswipe.repositories.UserDataRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/user")
class UserDataController(private val userDataRepository: UserDataRepository) {

    @GetMapping("/{username}")
    fun getUserData(@PathVariable username: String): UserData?{
        return userDataRepository.findByUsername(username)
    }

    @GetMapping("/all")
    fun getAllUser(): ArrayList<UserData>{
        return userDataRepository.findAll() as ArrayList<UserData>
    }

    @PutMapping("/{username}/name")
    fun updateUserDataName(@RequestBody name: String, @PathVariable username: String): UserData{
        val user = userDataRepository.findByUsername(username)
        user.name = name
        return userDataRepository.save(user)
    }

    @PutMapping("/{username}/vorname")
    fun updateUserDataVorname(@RequestBody vorname: String, @PathVariable username: String): UserData{
        val user = userDataRepository.findByUsername(username)
        user.vorname = vorname
        return userDataRepository.save(user)
    }

    @PutMapping("/{username}/gender")
    fun updateUserDataGender(@RequestBody gender: Gender, @PathVariable username: String): UserData{
        val user = userDataRepository.findByUsername(username)
        user.gender = gender
        return userDataRepository.save(user)
    }

    @PutMapping("/{username}/birthday")
    fun updateUserDataBirthday(@RequestBody birthday: String, @PathVariable username: String): UserData{
        val user = userDataRepository.findByUsername(username)
        user.birthday = birthday
        return userDataRepository.save(user)
    }
}
