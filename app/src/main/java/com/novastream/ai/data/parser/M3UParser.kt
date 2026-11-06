package com.novastream.ai.data.parser

import com.novastream.ai.data.model.Channel
import com.novastream.ai.data.model.ContentType
import java.io.BufferedReader
import java.io.StringReader
import java.util.regex.Pattern

object M3UParser {

    private val TVG_ID_PATTERN = Pattern.compile("tvg-id=\"([^\"]*)\"")
    private val TVG_NAME_PATTERN = Pattern.compile("tvg-name=\"([^\"]*)\"")
    private val TVG_LOGO_PATTERN = Pattern.compile("tvg-logo=\"([^\"]*)\"")
    private val GROUP_TITLE_PATTERN = Pattern.compile("group-title=\"([^\"]*)\"")

    fun parse(content: String, playlistId: Long = 0): List<Channel> {
        val channels = mutableListOf<Channel>()
        val reader = BufferedReader(StringReader(content))

        var currentName = ""
        var currentCategory = "General"
        var currentLogo: String? = null
        var currentEpgId: String? = null
        var currentType = ContentType.LIVE

        var line: String? = reader.readLine()
        while (line != null) {
            val trimmed = line.trim()

            if (trimmed.startsWith("#EXTINF:", ignoreCase = true)) {
                // Extract group-title
                val groupMatcher = GROUP_TITLE_PATTERN.matcher(trimmed)
                currentCategory = if (groupMatcher.find()) {
                    groupMatcher.group(1)?.ifBlank { "General" } ?: "General"
                } else {
                    "General"
                }

                // Extract tvg-logo
                val logoMatcher = TVG_LOGO_PATTERN.matcher(trimmed)
                currentLogo = if (logoMatcher.find()) logoMatcher.group(1) else null

                // Extract tvg-id
                val idMatcher = TVG_ID_PATTERN.matcher(trimmed)
                currentEpgId = if (idMatcher.find()) idMatcher.group(1) else null

                // Extract channel title after comma
                val commaIndex = trimmed.lastIndexOf(',')
                currentName = if (commaIndex != -1 && commaIndex < trimmed.length - 1) {
                    trimmed.substring(commaIndex + 1).trim()
                } else {
                    val nameMatcher = TVG_NAME_PATTERN.matcher(trimmed)
                    if (nameMatcher.find()) nameMatcher.group(1) ?: "Unnamed Stream" else "Unnamed Stream"
                }
            } else if (trimmed.isNotBlank() && !trimmed.startsWith("#")) {
                // This is a stream URL line
                val streamUrl = trimmed
                if (currentName.isBlank()) {
                    currentName = inferNameFromUrl(streamUrl)
                }

                currentType = classifyContentType(streamUrl, currentCategory)

                channels.add(
                    Channel(
                        id = 0,
                        playlistId = playlistId,
                        name = currentName,
                        streamUrl = streamUrl,
                        category = currentCategory,
                        logoUrl = currentLogo,
                        epgId = currentEpgId,
                        contentType = currentType,
                        isFavorite = false,
                        isWorking = true
                    )
                )

                // Reset for next entry
                currentName = ""
                currentCategory = "General"
                currentLogo = null
                currentEpgId = null
                currentType = ContentType.LIVE
            }

            line = reader.readLine()
        }

        return channels
    }

    private fun inferNameFromUrl(url: String): String {
        return try {
            val lastSegment = url.substringAfterLast('/').substringBefore('?')
            if (lastSegment.isNotBlank()) lastSegment.substringBeforeLast('.') else "Stream Channel"
        } catch (e: Exception) {
            "Stream Channel"
        }
    }

    private fun classifyContentType(url: String, category: String): ContentType {
        val lowerUrl = url.lowercase()
        val lowerCat = category.lowercase()

        return when {
            lowerUrl.contains("/series/") || lowerCat.contains("series") || lowerCat.contains("season") -> ContentType.SERIES
            lowerUrl.endsWith(".mp4") || lowerUrl.endsWith(".mkv") || lowerUrl.contains("/movie/") ||
                    lowerCat.contains("movie") || lowerCat.contains("vod") || lowerCat.contains("film") -> ContentType.VOD
            lowerUrl.endsWith(".mp3") || lowerCat.contains("radio") -> ContentType.RADIO
            else -> ContentType.LIVE
        }
    }

    /**
     * Fixes combo lines, removes invalid characters, normalizes delimiters,
     * and removes duplicate entries (inspired by iptvhit Combo Editor).
     */
    fun fixCombolist(rawText: String): Pair<List<String>, Int> {
        val validCombos = mutableListOf<String>()
        val seen = mutableSetOf<String>()
        var errorsFixed = 0

        val lines = rawText.lines()
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isBlank() || trimmed.startsWith("#") || trimmed.startsWith("//")) continue

            // Normalize separators: replace tabs, semicolons, or multiple spaces with colon
            var normalized = trimmed
                .replace("\t", ":")
                .replace(";", ":")
                .replace("|", ":")

            // Remove spaces around colons
            normalized = normalized.split(":").map { it.trim() }.filter { it.isNotEmpty() }.joinToString(":")

            if (normalized.contains(":") && normalized.length >= 3) {
                if (normalized !in seen) {
                    seen.add(normalized)
                    validCombos.add(normalized)
                } else {
                    errorsFixed++ // duplicate fixed
                }
            } else {
                errorsFixed++ // invalid line skipped/fixed
            }
        }

        return Pair(validCombos, errorsFixed)
    }
}
