package com.wafflestudio.snuttev.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class SnuttUserFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val snuttUserId = request.getHeader("Snutt-User-Id")
        if (snuttUserId?.isNotBlank() == true) {
            request.setAttribute("UserId", snuttUserId)
            filterChain.doFilter(request, response)
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        val excludePath = listOf("/v1/lectures/snutt-summary", "/v1/lectures/ids")
        return excludePath.contains(path)
    }
}
