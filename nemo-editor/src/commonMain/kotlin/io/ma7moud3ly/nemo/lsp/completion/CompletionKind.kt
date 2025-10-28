package io.ma7moud3ly.nemo.lsp.completion

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import io.ma7moud3ly.nemo.model.EditorTheme

enum class CompletionKind {
    TEXT, METHOD, FUNCTION, CONSTRUCTOR, FIELD, VARIABLE, CLASS, INTERFACE,
    MODULE, PROPERTY, UNIT, VALUE, ENUM, KEYWORD, SNIPPET
}

internal fun CompletionKind.getIcon(): ImageVector {
    return when (this) {
        CompletionKind.FUNCTION, CompletionKind.METHOD -> Icons.Default.AccountBox
        CompletionKind.CLASS, CompletionKind.INTERFACE -> Icons.Default.Create
        CompletionKind.VARIABLE, CompletionKind.FIELD -> Icons.Default.Info
        CompletionKind.KEYWORD -> Icons.Default.Star
        CompletionKind.PROPERTY -> Icons.Default.Settings
        CompletionKind.CONSTRUCTOR -> Icons.Default.Build
        else -> Icons.AutoMirrored.Default.KeyboardArrowRight
    }
}

internal fun CompletionKind.getColor(theme: EditorTheme): Color {
    return when (this) {
        CompletionKind.FUNCTION, CompletionKind.METHOD -> Color(theme.syntax.function)
        CompletionKind.CLASS, CompletionKind.INTERFACE -> Color(theme.syntax.type)
        CompletionKind.VARIABLE, CompletionKind.FIELD -> Color(theme.syntax.variable)
        CompletionKind.KEYWORD -> Color(theme.syntax.keyword)
        CompletionKind.PROPERTY -> Color(theme.syntax.variable)
        CompletionKind.CONSTRUCTOR -> Color(theme.syntax.type)
        else -> Color(theme.foreground)
    }
}