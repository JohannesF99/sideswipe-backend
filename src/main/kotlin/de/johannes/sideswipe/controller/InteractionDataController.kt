package de.johannes.sideswipe.controller

import de.johannes.sideswipe.model.InteractionData
import de.johannes.sideswipe.repositories.ContentDataRepository
import de.johannes.sideswipe.repositories.InteractionDataRepository
import de.johannes.sideswipe.repositories.UserDataRepository
import org.apache.logging.log4j.LogManager
import org.springframework.dao.EmptyResultDataAccessException
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
    fun getAllInteractionsForContent(@PathVariable contentId: Int, @PathVariable username: String): MutableMap<String, Any> {
        try {
            val user = userDataRepository.findByUsername(username)
            val content = contentDataRepository.findByContentId(contentId.toLong())
            user.doesTokenMatch()
            user.doesContentBelong(content)
            val map = mutableMapOf<String, Any>()
            logger.info("Interaction-Data requested for Content-ID '$contentId'")
            map["likes"] = interactionDataRepository.findAllByContentData(content).count { it.isLike }
            map["dislikes"] = interactionDataRepository.findAllByContentData(content).count { !it.isLike }
            map["userInteraction"] = interactionDataRepository.findByUserDataAndContentData(user, content)?.isLike ?: "None"
            return map
        } catch (e: EmptyResultDataAccessException){
            logger.warn("Interaction-Data requested for unknown Content-ID '$contentId' or unknown Username '$username'!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,"Interaction-Data requested for unknown Content-ID '$contentId'", e)
        } catch (e: SecurityException) {
            logger.warn("Authorization-Token does not match Username!")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization-Token does not match Username!", e)
        } catch (e: IllegalAccessException) {
            logger.warn("Content does not belong to User, so Interactions cannot be fetched!")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Content does not belong to User, so Interactions cannot be fetched!", e)
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
            user.doesTokenMatch()
            val interactionData = interactionDataRepository.findByUserDataAndContentData(user, content) ?: InteractionData(true, user, content)
            interactionData.isLike = true
            logger.info("User '$username' liked the Post with Content ID $contentId!")
            return interactionDataRepository.save(interactionData)
        } catch (e: EmptyResultDataAccessException){
            logger.warn("Like-Interaction unavailable, because Content-ID '$contentId' or Username '$username' could not be found!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,"Like-Interaction unavailable, because Content-ID '$contentId' or Username '$username' could not be found!", e)
        } catch (e: SecurityException) {
            logger.warn("Authorization-Token does not match Username!")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization-Token does not match Username!", e)
        }
    }

    @PostMapping("/{contentId}/dislike")
    fun addDislikeInteraction(@PathVariable contentId: Int, @PathVariable username: String): InteractionData {
        try {
            val content = contentDataRepository.findByContentId(contentId.toLong())
            val user = userDataRepository.findByUsername(username)
            user.doesTokenMatch()
            val interactionData = interactionDataRepository.findByUserDataAndContentData(user, content) ?: InteractionData(false, user, content)
            interactionData.isLike = false
            logger.info("User '$username' disliked the Post with Content ID $contentId!")
            return interactionDataRepository.save(interactionData)
        } catch (e: EmptyResultDataAccessException){
            logger.warn("Dislike-Interaction unavailable, because Content-ID '$contentId' or Username '$username' could not be found!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,"Dislike-Interaction unavailable, because Content-ID '$contentId' or Username '$username' could not be found!", e)
        } catch (e: SecurityException) {
            logger.warn("Authorization-Token does not match Username!")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization-Token does not match Username!", e)
        }
    }

    @DeleteMapping("/{contentId}/remove")
    fun removeInteraction(@PathVariable contentId: Int, @PathVariable username: String) {
        try {
            val content = contentDataRepository.findByContentId(contentId.toLong())
            val user = userDataRepository.findByUsername(username)
            user.doesTokenMatch()
            logger.info("User '$username' removed his interaction of Post with Content ID $contentId!")
            return interactionDataRepository.deleteByUserDataAndContentData(user, content)
        } catch (e: EmptyResultDataAccessException){
            logger.warn("Interaction could not be removed, because Content-ID '$contentId' or Username '$username' could not be found!")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,"Interaction could not be removed, because Content-ID '$contentId' or Username '$username' could not be found!", e)
        } catch (e: SecurityException) {
            logger.warn("Authorization-Token does not match Username!")
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization-Token does not match Username!", e)
        }
    }
}
