package de.johannes.sideswipe.controller

import de.johannes.sideswipe.model.ContentData
import de.johannes.sideswipe.repositories.ContentDataRepository
import de.johannes.sideswipe.repositories.FriendDataRepository
import de.johannes.sideswipe.repositories.UserDataRepository
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api/v1/user/{username}/content")
class ContentDataController(
    private val contentDataRepository: ContentDataRepository,
    private val userDataRepository: UserDataRepository,
    private val friendDataRepository: FriendDataRepository
) {
    private val logger = LogManager.getLogger()

    @PostMapping
    fun saveContent(@RequestBody contentData: ContentData, @PathVariable username: String): ContentData {
        try {
            val user = userDataRepository.findByUsername(username)
            val content = contentDataRepository.save(ContentData(contentData, user))
            logger.info("User \"$username\" created new Content with ID ${content.contentId}")
            return content
        } catch (e: Exception) {
            logger.warn("Could not create new Content, because of unknown Username \"$username\"!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not create new Content, because of unknown Username \"$username\"!", e)
        }
    }

    @GetMapping("/all")
    fun getAllContent(@PathVariable username: String): Set<ContentData> {
        try {
            val user = userDataRepository.findByUsername(username)
            logger.info("Fetched Content-List for Username \"$username\"!")
            return contentDataRepository.findAllByUserData(user)
        } catch (e : Exception) {
            logger.warn("Could not fetch Content-List, because of unknown Username \"$username\"!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not fetch Content-List, because of unknown Username \"$username\"!", e)
        }
    }

    @GetMapping("/{contentId}")
    fun getContent(@PathVariable username: String, @PathVariable contentId: Int): ContentData {
        try {
            val content = contentDataRepository.findByContentId(contentId.toLong())
            logger.info("Fetched Content for Content-ID \"$contentId\"!")
            return content
        } catch (e: Exception) {
            logger.warn("Could not fetch Content for unknown Content-ID \"$contentId\"!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not fetch Content for unknown Content-ID \"$contentId\"!", e)
        }
    }

    @PutMapping("/{contentId}")
    fun changeContent(@PathVariable username: String, @PathVariable contentId: Int, @RequestBody newCaption: String): ContentData {
        try {
            val content = contentDataRepository.findByContentId(contentId.toLong())
            content.caption = newCaption
            logger.info("Caption of Content-ID ${content.contentId} got changed!")
            return contentDataRepository.save(content)
        } catch (e: Exception) {
            logger.warn("Could not change Content-Caption for unknown Content-ID \"$contentId\"!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not change Content-Caption for unknown Content-ID \"$contentId\"!", e)
        }
    }

    @DeleteMapping("/{contentId}")
    fun deleteContent(@PathVariable username: String, @PathVariable contentId: Int){
        try {
            logger.info("Post with Content ID $contentId got deleted!")
            contentDataRepository.deleteById(contentId.toLong())
        } catch (e: Exception) {
            logger.warn("Could not delete Content for unknown Content-ID \"$contentId\"!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not delete Content for unknown Content-ID \"$contentId\"!", e)
        }
    }

    @GetMapping("/friends")
    fun getAllFriendsContent(@PathVariable username: String): List<ContentData> {
        try {
            val user = userDataRepository.findByUsername(username)
            logger.warn("Fetched Content of Friends for Username \"$username\"!")
            return friendDataRepository.findAllByUserId(user.userId)
                .asSequence()
                .map { contentDataRepository.findAllByUserData(it.friendData!!) }
                .map { it.take(10) }
                .flatten()
                .sortedBy { it.lastModified }
                .toList()
        } catch (e: Exception) {
            logger.warn("Could not fetch Content of Friends for unknown Username \"$username\"!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not fetch Content of Friends for unknown Username \"$username\"!", e)
        }
    }
}
