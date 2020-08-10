package com.akingyin.media.audio

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore



/**
 *
 * Name: AudioUtil
 * Author: akingyin
 * Email:
 * Comment: //TODO
 * Date: 2019-12-01 21:16
 *
 */
object AudioUtil {

    fun getMediaDuration(localpath: String?): Int {
        var duration = 0
        try {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(localpath)
            mediaPlayer.prepare()
            duration = mediaPlayer.duration
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: Error) {
            e.printStackTrace()
        }
        return duration
    }


    fun getSoundDuration(context: Context, localPath: String): Long {
        var duration: Long = 0
        try {
            val contentUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            val mResolver: ContentResolver = context.contentResolver
            val selection = MediaStore.Audio.Media.DATA + "=?"
            val selectionArgs = arrayOf(localPath)
            val cursor: Cursor? = mResolver.query(contentUri, null, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))

            }
            println("di=$duration")
            cursor?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return duration
    }


}