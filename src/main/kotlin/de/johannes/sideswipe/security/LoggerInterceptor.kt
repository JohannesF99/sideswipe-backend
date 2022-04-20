package de.johannes.sideswipe.security

import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class LoggerInterceptor: HandlerInterceptor {
    private val logger = LogManager.getRootLogger()

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any): Boolean {
        logger.info("URL: ${request.requestURI}, IP: ${getRemoteAddr(request)}")
        return true
    }

    private fun getRemoteAddr(request: HttpServletRequest): String {
        val ipFromHeader = request.getHeader("X-FORWARDED-FOR")
        if (ipFromHeader != null && ipFromHeader.isNotEmpty()) {
            return ipFromHeader
        }
        return request.remoteAddr
    }

}
