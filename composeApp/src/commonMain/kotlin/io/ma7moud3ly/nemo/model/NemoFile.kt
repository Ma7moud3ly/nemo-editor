package io.ma7moud3ly.nemo.model

import io.github.vinceglb.filekit.PlatformFile
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

data class NemoFile(
    val name: String,
    val path: String = "",
    val isDirectory: Boolean = false,
    val extension: String = "",
    val size: Long = 0L,
    val lastModified: Long = 0L,
    val isHidden: Boolean = false,
    val isReadOnly: Boolean = false,
    val platformFile: PlatformFile? = null
) {
    val nameWithoutExt: String get() = name.substringBeforeLast(".")
}

/**
 * Format file size from bytes to human-readable string
 */
fun NemoFile.formatSize(): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    val value = size / 1024.0.pow(digitGroups.toDouble())
    // Round to 1 decimal place
    val rounded = (round(value * 10) / 10)
    return "$rounded ${units[digitGroups]}"
}

/**
 * Format last modified timestamp to human-readable string
 */
@OptIn(ExperimentalTime::class)
fun NemoFile.formatLastModified(): String {
    if (lastModified <= 0) return "Unknown"

    return try {
        val instant = Clock.System.now() - (Clock.System.now()
            .toEpochMilliseconds() - lastModified).milliseconds
        val now = Clock.System.now()
        val duration = now - instant

        when {
            duration.inWholeMinutes < 1 -> "Just now"
            duration.inWholeMinutes < 60 -> "${duration.inWholeMinutes}m ago"
            duration.inWholeHours < 24 -> "${duration.inWholeHours}h ago"
            duration.inWholeDays < 7 -> "${duration.inWholeDays}d ago"
            duration.inWholeDays < 30 -> "${duration.inWholeDays / 7}w ago"
            else -> {
                val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                val month = localDateTime.month.name.take(3)
                val day = localDateTime.day
                val year = localDateTime.year
                "$month $day, $year"
            }
        }
    } catch (e: Exception) {
        "Unknown"
    }
}