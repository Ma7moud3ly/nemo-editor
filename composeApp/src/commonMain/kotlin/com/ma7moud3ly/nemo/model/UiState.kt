package com.ma7moud3ly.nemo.model

import androidx.compose.runtime.mutableStateOf

class UiState {
    val showFindAndReplace = mutableStateOf(false)
    val showAnimatedLogo = mutableStateOf(true)
    val sidebarExpanded = mutableStateOf(false)


    fun toggleAnimatedLogo() {
        showAnimatedLogo.value = !showAnimatedLogo.value
    }

    fun showSidebar(show: Boolean) {
        sidebarExpanded.value = show
    }

    fun toggleSidebar() {
        sidebarExpanded.value = !sidebarExpanded.value
    }

    fun toggleFindAndReplace() {
        showFindAndReplace.value = !showFindAndReplace.value
    }
}