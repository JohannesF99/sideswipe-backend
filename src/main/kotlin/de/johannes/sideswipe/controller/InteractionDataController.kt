package de.johannes.sideswipe.controller

import de.johannes.sideswipe.model.InteractionData
import de.johannes.sideswipe.repositories.ContentDataRepository
import de.johannes.sideswipe.repositories.InteractionDataRepository
import de.johannes.sideswipe.repositories.UserDataRepository
import org.apache.logging.log4j.LogManager
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/user/{username}/interaction")
class InteractionDataController(
    private val interactionDataRepository: InteractionDataRepository,
    private val userDataRepository: UserDataRepository,
    private val contentDataRepository: ContentDataRepository
) {
    private val logger = LogManager.getLogger()

    @GetMapping("/{contendId}")
    fun getAllInteractionsForContent(@PathVariable contendId: Int, @PathVariable username: String): List<Map<String, Boolean>> {
        val content = contentDataRepository.findByContentId(contendId.toLong())
        return interactionDataRepository.findAllByContentData(content)
            .sortedBy { it.isLike }
            .map { mapOf(Pair(it.userData!!.username, it.isLike)) }
    }

    @GetMapping("/all")
    fun getAllInteractionsForUser(@PathVariable username: String): List<Map<Long, Boolean>> {
        val user = userDataRepository.findByUsername(username)
        return interactionDataRepository.findAllByUserData(user)
            .sortedBy { it.isLike }
            .map { mapOf(Pair(it.contentData!!.contentId, it.isLike)) }
    }

    @PostMapping("/{contentId}/like")
    fun addLikeInteraction(@PathVariable contentId: Int, @PathVariable username: String): InteractionData {
        val content = contentDataRepository.findByContentId(contentId.toLong())
        val user = userDataRepository.findByUsername(username)
        val interactionData = interactionDataRepository.findByUserDataAndContentData(user, content) ?: InteractionData(true, user, content)
        interactionData.isLike = true
        logger.info("User \"$username\" liked the Post with Content ID $contentId!")
        return interactionDataRepository.save(interactionData)
    }

    @PostMapping("/{contentId}/dislike")
    fun addDislikeInteraction(@PathVariable contentId: Int, @PathVariable username: String): InteractionData {
        val content = contentDataRepository.findByContentId(contentId.toLong())
        val user = userDataRepository.findByUsername(username)
        val interactionData = interactionDataRepository.findByUserDataAndContentData(user, content) ?: InteractionData(false, user, content)
        interactionData.isLike = false
        logger.info("User \"$username\" disliked the Post with Content ID $contentId!")
        return interactionDataRepository.save(interactionData)
    }

    @DeleteMapping("/{contentId}/remove")
    fun removeInteraction(@PathVariable contentId: Int, @PathVariable username: String) {
        val content = contentDataRepository.findByContentId(contentId.toLong())
        val user = userDataRepository.findByUsername(username)
        logger.info("User \"$username\" removed his interaction of Post with Content ID $contentId!")
        return interactionDataRepository.deleteByUserDataAndContentData(user, content)
    }
}
