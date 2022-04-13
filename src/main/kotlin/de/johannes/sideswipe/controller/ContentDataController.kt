package de.johannes.sideswipe.controller

import de.johannes.sideswipe.model.ContentData
import de.johannes.sideswipe.repositories.ContentDataRepository
import de.johannes.sideswipe.repositories.FriendDataRepository
import de.johannes.sideswipe.repositories.UserDataRepository
import org.apache.logging.log4j.LogManager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*

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
        val user = userDataRepository.findByUsername(username)
        val content = contentDataRepository.save(ContentData(contentData, user))
        logger.info("User \"$username\" created new Content with ID ${content.contentId}")
        return content
    }

    @GetMapping("/all")
    fun getAllContent(@PathVariable username: String): Set<ContentData> {
        val user = userDataRepository.findByUsername(username)
        return contentDataRepository.findAllByUserData(user)
    }

    @GetMapping("/{contentId}")
    fun getContent(@PathVariable username: String, @PathVariable contentId: Int): ContentData? {
        return contentDataRepository.findByIdOrNull(contentId.toLong())
    }

    @PutMapping("/{contentId}")
    fun changeContent(@PathVariable username: String, @PathVariable contentId: Int, @RequestBody newCaption: String): ContentData {
        val content = contentDataRepository.findByContentId(contentId.toLong())
        content.caption = newCaption
        logger.info("Caption of Content ID ${content.contentId} got changed!")
        return contentDataRepository.save(content)
    }

    @DeleteMapping("/{contentId}")
    fun deleteContent(@PathVariable username: String, @PathVariable contentId: Int){
        logger.info("Post with Content ID $contentId got deleted!")
        contentDataRepository.deleteById(contentId.toLong())
    }

    @GetMapping("/friends")
    fun getAllFriendsContent(@PathVariable username: String): List<ContentData> {
        val user = userDataRepository.findByUsername(username)
        return friendDataRepository.findAllByUserId(user.userId)
            .asSequence()
            .map { contentDataRepository.findAllByUserData(it.friendData!!) }
            .map { it.take(10) }
            .flatten()
            .sortedBy { it.lastModified }
            .toList()
    }
}
