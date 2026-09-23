package com.rohitchauhan.hiichat.data.remote.supabase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class SupabaseService @Inject constructor(
    private val supabaseClient: SupabaseClient
) {

    suspend fun uploadImage(
        context: Context,
        uri: Uri,
        bucketName: String,
        path: String
    ): String = withContext(Dispatchers.IO) {
        val tempFile = File(context.cacheDir, "upload_temp_${System.currentTimeMillis()}.jpeg")

        try {
            // 1. Open input stream from content resolver ONCE and copy to tempFile
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: throw Exception("Could not open Uri stream")

            // 2. Decode bounds to check dimensions from tempFile
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(tempFile.absolutePath, options)

            // 3. Calculate inSampleSize for reasonable max dimensions (e.g., max 1024px)
            val maxSize = 1024
            var inSampleSize = 1
            if (options.outHeight > maxSize || options.outWidth > maxSize) {
                val halfHeight = options.outHeight / 2
                val halfWidth = options.outWidth / 2
                while ((halfHeight / inSampleSize) >= maxSize && (halfWidth / inSampleSize) >= maxSize) {
                    inSampleSize *= 2
                }
            }

            // 4. Decode actual bitmap with sample size from tempFile
            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
                inJustDecodeBounds = false
            }
            val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath, decodeOptions)
                ?: throw Exception("Could not decode image from file")

            // 5. Compress bitmap into tempFile (JPEG, 80% quality)
            tempFile.outputStream().use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            }
            bitmap.recycle()

            // 6. Upload compressed tempFile to Supabase Storage
            val bucket = supabaseClient.storage.from(bucketName)
            bucket.upload(path, tempFile, upsert = true)

            // Return the public URL
            supabaseClient.storage.from(bucketName).publicUrl(path)
        } finally {
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }
    }
}
