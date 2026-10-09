package io.ma7moud3ly.nemo.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.ma7moud3ly.nemo.managers.FindAndReplaceManager
import io.ma7moud3ly.nemo.model.CodeState

/**
 * Runs the search again whenever the search text, one of its options, or the
 * code in [state] changes.
 */
@Composable
internal fun SearchOnChange(state: CodeState, manager: FindAndReplaceManager) {
    LaunchedEffect(
        manager.findText,
        manager.caseSensitive,
        manager.wholeWord,
        manager.useRegex,
        state.code
    ) {
        manager.updateSearch()
    }
}
