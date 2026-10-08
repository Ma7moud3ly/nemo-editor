package io.ma7moud3ly.nemo.model

import kotlinx.serialization.Serializable

@Serializable
data class SavedTabData(
    val id: String,
    val filePath: String,
    val fileName: String,
    val fileExtension: String,
    val tempContent: String,
    val cursorPosition: Int,
    val isDirty: Boolean
)


@Serializable
data class SettingsData(
    val themeName: String,
    val fontSize: Int,
    val tabSize: Int,
    val useTabs: Boolean,
    val showLineNumbers: Boolean,
    val enableAutoIndent: Boolean,
    val enableAutocomplete: Boolean,
    val readOnly: Boolean,
    val sidebarExpanded: Boolean,
    val animatedLogo: Boolean
)