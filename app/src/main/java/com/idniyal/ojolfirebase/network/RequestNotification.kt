package com.idniyal.ojolfirebase.network

import com.google.gson.annotations.SerializedName
import com.idniyal.ojolfirebase.model.Booking

class RequestNotification{

    @SerializedName("to")
    var token: String? = null

    @SerializedName("data")
    var sendNotificationModel: Booking? = null
}