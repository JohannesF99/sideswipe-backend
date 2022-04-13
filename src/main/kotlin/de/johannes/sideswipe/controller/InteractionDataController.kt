package de.johannes.sideswipe.controller

import de.johannes.sideswipe.model.InteractionData
import de.johannes.sideswipe.repositories.ContentDataRepository
import de.johannes.sideswipe.repositories.InteractionDataRepository
import de.johannes.sideswipe.repositories.UserDataRepository
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api/v1/user/{username}/interaction")
class InteractionDataController(
    private val interactionDataRepository: InteractionDataRepository,
    private val userDataRepository: UserDataRepository,
    private val contentDataRepository: ContentDataRepository
) {
    private val logger = LogManager.getLogger()

    @GetMapping("/{contentId}")
    fun getAllInteractionsForContent(@PathVariable contentId: Int, @PathVariable username: String): List<Map<String, Boolean>> {
        try {
            val content = contentDataRepository.findByContentId(contentId.toLong())
            logger.info("Interaction-Data requested for Content-ID '$contentId'")
            return interactionDataRepository.findAllByContentData(content)
                .sortedBy { it.isLike }
                .map { mapOf(Pair(it.userData!!.username, it.isLike)) }
        } catch (e: Exception){
            logger.warn("Interaction-Data requested for unknown Content-ID '$contentId'")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,"Interaction-Data requested for unknown Content-ID '$contentId'", e)
        }
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
        try {
            val content = contentDataRepository.findByContentId(contentId.toLong())
            val user = userDataRepository.findByUsername(username)
            val interactionData = interactionDataRepository.findByUserDataAndContentData(user, content) ?: InteractionData(true, user, content)
            interactionData.isLike = true
            logger.info("User '$username' liked the Post with Content ID $contentId!")
            return interactionDataRepository.save(interactionData)
        } catch (e: Exception){
            logger.warn("Like-Interaction unavailable, because Content-ID '$contentId' or Username '$username' could not be found!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,"Like-Interaction unavailable, because Content-ID '$contentId' or Username '$username' could not be found!", e)
        }
    }

    @PostMapping("/{contentId}/dislike")
    fun addDislikeInteraction(@PathVariable contentId: Int, @PathVariable username: String): InteractionData {
        try {
            val content = contentDataRepository.findByContentId(contentId.toLong())
            val user = userDataRepository.findByUsername(username)
            val interactionData = interactionDataRepository.findByUserDataAndContentData(user, content) ?: InteractionData(false, user, content)
            interactionData.isLike = false
            logger.info("User '$username' disliked the Post with Content ID $contentId!")
            return interactionDataRepository.save(interactionData)
        } catch (e: Exception){
            logger.warn("Dislike-Interaction unavailable, because Content-ID '$contentId' or Username '$username' could not be found!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,"Dislike-Interaction unavailable, because Content-ID '$contentId' or Username '$username' could not be found!", e)
        }
    }

    @DeleteMapping("/{contentId}/remove")
    fun removeInteraction(@PathVariable contentId: Int, @PathVariable username: String) {
        try {
            val content = contentDataRepository.findByContentId(contentId.toLong())
            val user = userDataRepository.findByUsername(username)
            logger.info("User '$username' removed his interaction of Post with Content ID $contentId!")
            return interactionDataRepository.deleteByUserDataAndContentData(user, content)
        } catch (e: Exception){
            logger.warn("Interaction could not be removed, because Content-ID '$contentId' or Username '$username' could not be found!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,"Interaction could not be removed, because Content-ID '$contentId' or Username '$username' could not be found!", e)
        }
    }
}
