package com.wafflestudio

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SnuttUserFilter : Filter {
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val snuttUserId = httpRequest.getHeader("Snutt-User-Id")
        if (snuttUserId?.toLongOrNull() == null) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED)
        } else {
            request.setAttribute("UserId", snuttUserId)
            chain?.doFilter(request, response)
        }
    }
}
