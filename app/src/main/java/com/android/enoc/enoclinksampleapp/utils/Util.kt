package com.android.enoc.enoclinksampleapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.*
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*


object Util {
    private const val MEGABYTE = 1024.0 * 1024.0
    private const val MAX_ALLOWED_FILE_SIZE = 10.0
    fun readFileFromAssets(ctx: Context, fileName: String?): String {
        val builder = StringBuilder()
        val `is`: InputStream
        try {
            `is` = ctx.assets.open(fileName!!)
            val bufferedReader = BufferedReader(InputStreamReader(`is`, "UTF-8"))
            var str: String?
            while (bufferedReader.readLine().also { str = it } != null) {
                builder.append(str)
            }
            bufferedReader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return builder.toString()
    }

    fun readEncryptedFileFromAssets(context: Context, filename: String?): ByteArray? {
        var bytes: ByteArray? = null
        try {
            val `is` = context.assets.open(filename!!)
            bytes = ByteArray(`is`.available())
            `is`.read(bytes)
            `is`.close()
        } catch (e: IOException) {
        }
        return bytes
    }

    fun saveBitmap(context: Context, b: Bitmap, picName: String?) {
        val fos: FileOutputStream
        try {
            fos = context.openFileOutput(picName, Context.MODE_PRIVATE)
            b.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()
        } catch (ignored: IOException) {
        }
    }

    fun deleteBitmap(context: Context, picName: String?): Boolean {
        return context.deleteFile(picName)
    }

    fun deleteFileByPath(filePath: String): Boolean {
        val file = File(filePath)
        return if (file.exists()) {
            file.delete()
        } else false
    }

    fun getFilePath(context: Context, picName: String?): String {
        val file  = context.getFileStreamPath(picName)
  return file.absolutePath
    }

    fun loadBitmap(context: Context, picName: String?): Bitmap? {
        var bitmap: Bitmap? = null
        val fis: FileInputStream
        try {
            fis = context.openFileInput(picName)
            bitmap = BitmapFactory.decodeStream(fis)
            fis.close()
        } catch (ignored: IOException) {
        }
        return bitmap
    }

    fun isBitmapPresent(context: Context, picName: String?): Boolean {
        return loadBitmap(context, picName) != null
    }

    fun bytesToChars(bytes: ByteArray?): CharArray {
        val charBuffer = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes))
        return Arrays.copyOf(charBuffer.array(), charBuffer.limit())
    }

    fun isLessThanMaxSize(filePath: String?): Boolean {
        val file = File(filePath)
        val fileSize = file.length() / MEGABYTE
        return fileSize <= MAX_ALLOWED_FILE_SIZE
    }
    fun convertBitmapToByteArray(bitmap: Bitmap, byteSizeLimit: Int): ByteArray? {
        try {
            ByteArrayOutputStream().use { outputStream ->
                var quality = 100
                do {
                    outputStream.reset()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                    quality -= 10
                } while (outputStream.toByteArray().size > byteSizeLimit)
                return outputStream.toByteArray()
            }
        } catch (e: IOException) {

        }
        return ByteArray(0)
    }
}