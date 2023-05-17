package com.wafflestudio.snuttev.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class SnuttUserFilter : Filter {
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val snuttUserId = httpRequest.getHeader("Snutt-User-Id")
        if (snuttUserId?.isNotBlank() == true) {
            request.setAttribute("UserId", snuttUserId)
            chain?.doFilter(request, response)
        } else {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED)
        }
    }
}
