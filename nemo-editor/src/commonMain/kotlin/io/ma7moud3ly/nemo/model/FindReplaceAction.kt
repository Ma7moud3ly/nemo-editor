package io.ma7moud3ly.nemo.model

sealed class FindReplaceAction {
    data class Insert(val position: Int, val text: String) : FindReplaceAction()
    data class Delete(val position: Int, val text: String) : FindReplaceAction()
    data class Replace(
        val start: Int,
        val end: Int,
        val oldText: String,
        val newText: String
    ) : FindReplaceAction()
}


