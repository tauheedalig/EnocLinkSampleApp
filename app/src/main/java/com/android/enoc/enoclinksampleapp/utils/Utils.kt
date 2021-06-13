package com.android.enoc.enoclinksampleapp.utils

import okhttp3.internal.and
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException



internal object Utils {
    private val sBuilder = StringBuilder()
    private const val SPECIAL_CHARS = " %$&+,/:;=?@<>#%"
    private fun hex(array: ByteArray): String {
        sBuilder.setLength(0)
        for (b in array) {
            sBuilder.append(Integer.toHexString(b and  0xFF or 0x100).substring(1, 3))
        }
        return sBuilder.toString()
    }

    fun convertEmailToHash(email: String): String {
        val messageDigest: MessageDigest
        return try {
            messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.reset()
            hex(messageDigest.digest(email.toByteArray(charset("UTF-8"))))
        } catch (e: NoSuchAlgorithmException) {
            email
        } catch (e: UnsupportedEncodingException) {
            email
        }
    }

    fun encode(input: String): String {
        sBuilder.setLength(0)
        for (ch in input.toCharArray()) {
            if (isUnsafe(ch)) {
                sBuilder.append('%')
                sBuilder.append(toHex(ch.toInt() / 16))
                sBuilder.append(toHex(ch.toInt() % 16))
            } else {
                sBuilder.append(ch)
            }
        }
        return sBuilder.toString()
    }

    private fun toHex(ch: Int): Char {
        return (if (ch < 10) '0'.toInt() + ch else 'A'.toInt() + ch - 10).toChar()
    }

    private fun isUnsafe(ch: Char): Boolean {
        return ch.toInt() > 128 || ch.toInt() < 0 || SPECIAL_CHARS.indexOf(ch) >= 0
    }
}
