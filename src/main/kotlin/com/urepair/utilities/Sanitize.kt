package com.urepair.utilities

import kotlinx.html.span
import kotlinx.html.stream.createHTML
fun sanitize(input: String): String {
    return createHTML().span {
        text(input)
    }.toString()
}
