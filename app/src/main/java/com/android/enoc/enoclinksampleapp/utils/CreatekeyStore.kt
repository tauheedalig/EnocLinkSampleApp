package com.android.enoc.enoclinksampleapp.utils

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.security.auth.x500.X500Principal


internal class CreatekeyStore {

companion object{
    var alias = "mykeystore"
    val androidStore="AndroidKeyStore"
    val algorithm="RSA"
    val trassformPath="RSA/ECB/PKCS1Padding"


    lateinit var keyStore: KeyStore
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.M)
    open  fun createNewKeys() {
        try {
            // Create new key if needed
            keyStore = KeyStore.getInstance(androidStore)
            keyStore.let { keyStore.load(null) }
            if (!keyStore.containsAlias(alias)) {
                val start = Calendar.getInstance()
                val end = Calendar.getInstance()
                end.add(Calendar.YEAR, 2)

                val spec = KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .setKeySize(2048)
                    .setRandomizedEncryptionRequired(false)
                    .setCertificateSubject(X500Principal("CN=Sample Name"))
                    .setCertificateNotBefore(start.time)
                    .setCertificateNotAfter(end.time)
                    .setKeyValidityStart(start.time)
                    .setKeyValidityEnd(end.time)
                    .setCertificateSerialNumber(BigInteger.ONE)
                    .build()
                val generator = KeyPairGenerator.getInstance(algorithm, androidStore)
                generator.initialize(spec)
                generator.generateKeyPair()
            }
        } catch (e: Exception) {
        }
    }
    @JvmStatic
    fun encryptString(text: String): String {
        try {
            val privateKeyEntry = keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry
            val publicKey = privateKeyEntry.certificate.publicKey
            val input = Cipher.getInstance(trassformPath)
            input.init(Cipher.ENCRYPT_MODE, publicKey)
            val outputStream = ByteArrayOutputStream()
            val cipherOutputStream = CipherOutputStream(
                outputStream, input
            )
            cipherOutputStream.write(text.toByteArray(Charsets.UTF_8))
            cipherOutputStream.close()
            val vals = outputStream.toByteArray()
            return Base64.encodeToString(vals, Base64.DEFAULT)
        } catch (e: Exception) {
        }
        return text
    }
    @JvmStatic
    fun decryptString(text: String): String {
        try {
            val privateKeyEntry = keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry
            val privateKey = privateKeyEntry.privateKey
            val output = Cipher.getInstance(trassformPath)
            output.init(Cipher.DECRYPT_MODE, privateKey)
            val cipherInputStream = CipherInputStream(
                ByteArrayInputStream(Base64.decode(text, Base64.DEFAULT)), output
            )
            val values = ArrayList<Byte>()
            var nextByte: Int
            while (cipherInputStream.read().also { nextByte = it } != -1) {
                values.add(nextByte.toByte())
            }
            val bytes = ByteArray(values.size)
            for (i in bytes.indices) {
                bytes[i] = values[i]
            }
            return String(bytes, 0, bytes.size, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace();
        }
        return text
    }
}

}