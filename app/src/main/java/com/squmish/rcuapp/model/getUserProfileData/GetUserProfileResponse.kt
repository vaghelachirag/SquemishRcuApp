package com.squmish.rcuapp.model.getUserProfileData

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squmish.rcuapp.model.getUserProfileData.GetUserProfileData


class GetUserProfileResponse {

    @SerializedName("status_Code")
    @Expose
    private var statusCode: Int? = null

    @SerializedName("status")
    @Expose
    private var status: String? = null

    @SerializedName("message")
    @Expose
    private var message: Any? = null

    @SerializedName("returnUrl")
    @Expose
    private var returnUrl: Any? = null

    @SerializedName("data")
    @Expose
    private var data: GetUserProfileData? = null

    @SerializedName("textData")
    @Expose
    private var textData: Any? = null

    fun getStatusCode(): Int? {
        return statusCode
    }

    fun setStatusCode(statusCode: Int?) {
        this.statusCode = statusCode
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getMessage(): Any? {
        return message
    }

    fun setMessage(message: Any?) {
        this.message = message
    }

    fun getReturnUrl(): Any? {
        return returnUrl
    }

    fun setReturnUrl(returnUrl: Any?) {
        this.returnUrl = returnUrl
    }

    fun getData(): GetUserProfileData? {
        return data
    }

    fun setData(data: GetUserProfileData?) {
        this.data = data
    }

    fun getTextData(): Any? {
        return textData
    }

    fun setTextData(textData: Any?) {
        this.textData = textData
    }

}