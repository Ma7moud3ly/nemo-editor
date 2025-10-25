package com.ma7moud3ly.nemo.model

import kotlinx.serialization.Serializable

sealed interface AppRoutes {
    @Serializable
    data object HomeScreen : AppRoutes

    object Dialog {
        @Serializable
        data object About : AppRoutes

        @Serializable
        data object Themes : AppRoutes

        @Serializable
        data object Settings : AppRoutes

        @Serializable
        data object Shortcuts : AppRoutes

        @Serializable
        data object UnSavedChanged : AppRoutes

        object File {
            @Serializable
            data object Details : AppRoutes

            @Serializable
            data object Delete : AppRoutes

            @Serializable
            data object Rename : AppRoutes

            @Serializable
            data object Create : AppRoutes
        }

        object Folder {
            @Serializable
            data object Create : AppRoutes
        }
    }


}