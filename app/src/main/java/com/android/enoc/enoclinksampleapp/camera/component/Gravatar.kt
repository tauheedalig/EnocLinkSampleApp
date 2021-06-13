package com.android.enoc.enoclinksampleapp.camera.component

import com.android.enoc.enoclinksampleapp.camera.component.Gravatar.Builder



/**
 * Easy Gravatar query building.
 *
 *
 * Use [.init] for the global singleton instance or construct your
 * own instance with [Builder].
 */
class Gravatar private constructor(val ssl: Boolean, val extension: Boolean) {
    /**
     * Start a Gravatar URL building request using the specified email address.
     */
    fun with(email: String?): RequestBuilder {
        return RequestBuilder(this, email)
    }

    /**
     * Fluent API for creating [Gravatar] instances.
     */
    // Public API.
    class Builder {
        private var ssl = false
        private var extension = false

        /**
         * Specify that the secure Gravatar endpoint should be used by default.
         */
        fun ssl(): Builder {
            ssl = true
            return this
        }

        /**
         * Specify that file extension (.jpg) will be displayed at the end of the generated URL.
         */
        fun fileExtension(): Builder {
            this.extension = true
            return this
        }

        /**
         * Create the [Gravatar] instance.
         */
        fun build(): Gravatar {
            return Gravatar(ssl, extension)
        }
    }

    object DefaultImage {
        const val MYSTERY_MAN = 0
        const val IDENTICON = 1
        const val MONSTER = 2
        const val WAVATAR = 3
        const val RETRO = 4
        const val BLANK = 5
    }

    object Rating {
        const val g = 0
        const val pg = 1
        const val r = 2
        const val x = 3
    }

    companion object {
        const val MIN_IMAGE_SIZE_PIXEL = 1
        const val MAX_IMAGE_SIZE_PIXEL = 2048
        private var singleton: Gravatar? = null

        /**
         * The global default [Gravatar] instance.
         *
         *
         * This instance is automatically initialized with defaults that are suitable to most
         * implementations.
         *
         *  * SSL turned off by default
         *  * Image extension (.jpg) not displayed.
         *
         *
         *
         * If these settings do not meet the requirements of your application you can construct your own
         * instance with full control over the configuration by using [Gravatar.Builder].
         */
        fun init(): Gravatar? {
            if (singleton == null) {
                singleton = Builder().build()
            }
            return singleton
        }
    }
}