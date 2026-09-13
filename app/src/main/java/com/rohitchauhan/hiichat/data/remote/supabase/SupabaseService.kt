package com.rohitchauhan.hiichat.data.remote.supabase

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.traceEventEnd
import com.rohitchauhan.hiichat.data.remote.firebase.FirebaseService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class SupabaseService @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val firebaseService: FirebaseService
) {

    suspend fun uploadImage(
        context: Context,
        uri: Uri,
        bucketName: String,
        path: String
    ): String = withContext(Dispatchers.IO) {
        //first method
//        val bytes = context.contentResolver.openInputStream(uri)?.use {
//            it.readBytes()
//        } ?: throw Exception("Could not read bytes from Uri")
//
//        val bucket = supabaseClient.storage.from(bucketName)
//        bucket.upload(path, bytes, upsert = true)
//
//        // Return the public URL
//        supabaseClient.storage.from(bucketName).publicUrl(path)

        //second method
        val tempFile = File(context.cacheDir,"upload_temp${System.currentTimeMillis()}.jpeg")

        context.contentResolver.openInputStream(uri)?.use{inputStream ->
            tempFile.outputStream().use{outputStream ->
                inputStream.copyTo(outputStream)
            }
        }?:throw Exception("Could not open Uri stream")
        try {
            val bucket = supabaseClient.storage.from(bucketName)
            bucket.upload(path,tempFile, upsert = true)
            supabaseClient.storage.from(bucketName).publicUrl(path)
        }finally {
            tempFile.delete()
        }
    }
}
