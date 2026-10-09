package io.ma7moud3ly.nemo

/**
 * Marks declarations that the Nemo libraries use between themselves.
 * They are not part of the public API and may change in any release.
 */
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This is internal Nemo API. It may change without notice."
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY
)
annotation class InternalNemoApi
