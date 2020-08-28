package com.idniyal.ojolfirebase.network

import com.idniyal.ojolfirebase.model.ResultRoute
import io.reactivex.Flowable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("json")
    fun actionRoute(@Query("origin")origin: String,
                    @Query("destination") destination: String,
                    @Query("key")key: String):Flowable<ResultRoute>

    @Headers(
        "Authorization: key=AAAAkKralYs:APA91bENYCWMi7iSpj-3eX7AFl98mn7kJcv9Z2jVFJPRtdY7vyxpft7V6putK05aLK13FmTpAyGG_GQNZlCfXfsbwAa2cz9jNCocQtlZl0Ox4J9pXYQZZ6h6W6kqYJSDQRjsKhcsM_K7",
        "Content-Type:application/json"
    )
    @POST("fcm/send")
    fun sendChatNotification(@Body requestNotificaton: RequestNotification): Call<ResponseBody>

}