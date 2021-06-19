package com.programmers.kmooc.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

object ImageLoader {
    fun loadImage(url: String, completed: (Bitmap?) -> Unit) {
        //TODO: String -> Bitmap 을 구현하세요

        val httpClient = HttpClient("http://www.kmooc.kr")
        httpClient.getData(
            url,
        ) { result ->
            result.onSuccess {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                completed(bitmap)
            }
        }
    }
}