package com.urepair.utilities

import kotlinx.html.span
import kotlinx.html.stream.createHTML
fun sanitize(input: String): String {
    val sanitizedHtml = createHTML().span {
        text(input)
    }.toString()
    val withoutHtmlTags = sanitizedHtml.replace(Regex("<[^>]*>"), "")
    return withoutHtmlTags.trim()
}
