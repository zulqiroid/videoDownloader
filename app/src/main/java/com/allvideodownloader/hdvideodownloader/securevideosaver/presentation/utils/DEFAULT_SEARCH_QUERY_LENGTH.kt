package com.allvideodownloader.hdvideodownloader.securevideosaver.presentation.utils

private const val DEFAULT_SEARCH_QUERY_LENGTH = 80

fun String.toProfessionalSearchQuery(
    maxLength: Int = DEFAULT_SEARCH_QUERY_LENGTH,
): String {
    return trimStart()
        .replace(Regex("\\s+"), " ")
        .take(maxLength)
}

fun <T> List<T>.searchByQuery(
    query: String,
    vararg selectors: (T) -> String?,
): List<T> {
    val normalizedQuery = query.trim()

    if (normalizedQuery.isBlank()) return this

    return filter { item ->
        selectors.any { selector ->
            selector(item)
                .orEmpty()
                .contains(
                    other = normalizedQuery,
                    ignoreCase = true
                )
        }
    }
}