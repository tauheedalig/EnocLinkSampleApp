package com.android.enoc.enoclinksampleapp.camera.component

import com.android.enoc.enoclinksampleapp.utils.Utils


/**
 * Fluent API for Gravatar request configuration.
 */
// Public API.
class RequestBuilder internal constructor(gravatar: Gravatar, email: String?) {
    private val builder: StringBuilder

    /**
     * Specifies the image dimensions (as the image is always squared) to be retrieved,
     * allowing to save both bandwidth and memory.
     *
     *
     *
     * @param sizeInPixels must be between `Gravatar.MIN_IMAGE_SIZE_PIXEL`
     * and `Gravatar.MAX_IMAGE_SIZE_PIXEL`.
     */
    fun size(sizeInPixels: Int): RequestBuilder {
        if (sizeInPixels >= Gravatar.MIN_IMAGE_SIZE_PIXEL && sizeInPixels <= Gravatar.MAX_IMAGE_SIZE_PIXEL) {
            builder.append("&size=").append(sizeInPixels)
            return this
        }
        throw IllegalArgumentException(
            "Requested image size must be between " + Gravatar.MIN_IMAGE_SIZE_PIXEL
                    + " and " + Gravatar.MAX_IMAGE_SIZE_PIXEL
        )
    }

    /**
     * The default image will always be returned.
     */
    fun forceDefault(): RequestBuilder {
        builder.append("&f=y")
        return this
    }

    /**
     * If the request image couldn't be found, a 404 HTTP error will be returned instead of the default image.
     */
    fun force404(): RequestBuilder {
        builder.append("&d=404")
        return this
    }

    /**
     * Sets the defaultImage to be one of Gravatar's built-in default images.
     *
     *
     *
     * @param gravatarDefaultImage must be either `Gravatar.DefaultImage.MYSTERY_MAN`,
     * `Gravatar.DefaultImage.IDENTICON`,
     * `Gravatar.DefaultImage.MONSTER`,
     * `Gravatar.DefaultImage.WAVATAR`,
     * `Gravatar.DefaultImage.RETRO` or
     * `Gravatar.DefaultImage.BLANK`.
     */
    fun defaultImage(gravatarDefaultImage: Int): RequestBuilder {
        when (gravatarDefaultImage) {
            Gravatar.DefaultImage.MYSTERY_MAN -> {
                builder.append("&d=mm")
                return this
            }
            Gravatar.DefaultImage.IDENTICON -> {
                builder.append("&d=identicon")
                return this
            }
            Gravatar.DefaultImage.MONSTER -> {
                builder.append("&d=monsterid")
                return this
            }
            Gravatar.DefaultImage.WAVATAR -> {
                builder.append("&d=wavatar")
                return this
            }
            Gravatar.DefaultImage.RETRO -> {
                builder.append("&d=retro")
                return this
            }
            Gravatar.DefaultImage.BLANK -> {
                builder.append("&d=blank")
                return this
            }
        }
        throw IllegalArgumentException(
            "The Gravatar default image must be one of the built-in " +
                    "default images MYSTERY_MAN, IDENTICON, MONSTER, WAVATAR, RETRO, BLANK or a custom URL image"
        )
    }

    /**
     * Sets the defaultImage to the custom location pointed by the [url][String].
     *
     *
     *
     * @param url the default image url, must a accessible over the Internet and will be automatically encoded.
     */
    fun defaultImage(url: String?): RequestBuilder {
        builder.append("&d=").append(Utils.encode(url!!))
        return this
    }

    /**
     * Specifies the rating policy for Gravatar image.
     *
     *
     *
     * @param rating must be either `Gravatar.Rating.g`,
     * `Gravatar.Rating.pg`,
     * `Gravatar.Rating.r` or
     * `Gravatar.Rating.x`.
     */
    fun rating(rating: Int): RequestBuilder {
        when (rating) {
            Gravatar.Rating.g -> {
                builder.append("&r=g")
                return this
            }
            Gravatar.Rating.pg -> {
                builder.append("&r=pg")
                return this
            }
            Gravatar.Rating.r -> {
                builder.append("&r=r")
                return this
            }
            Gravatar.Rating.x -> {
                builder.append("&r=x")
                return this
            }
        }
        throw IllegalArgumentException(
            "The Gravatar default image must be one of the built-in rating policy" +
                    "G, PG, R or X"
        )
    }

    /**
     * @return the fully built Gravatar URL.
     */
    fun build(): String {
        val size = builder.length - 1
        if (builder[size] == '?') {
            builder.deleteCharAt(size)
        }
        return builder.toString()
    }

    companion object {
        private const val GRAVATAR_ENDPOINT = "http://www.gravatar.com/avatar/"
        private const val GRAVATAR_SECURE_ENDPOINT = "https://secure.gravatar.com/avatar/"
    }

    init {
        val hash: String = Utils.convertEmailToHash(email!!)
        builder =
            if (gravatar.ssl) StringBuilder(GRAVATAR_SECURE_ENDPOINT.length + hash.length + 1).append(
                GRAVATAR_SECURE_ENDPOINT
            ) else StringBuilder(GRAVATAR_ENDPOINT.length + hash.length + 1).append(
                GRAVATAR_ENDPOINT
            )
        builder.append(hash)
        if (gravatar.extension) {
            builder.append(".jpg")
        }
        builder.append("?")
    }
}