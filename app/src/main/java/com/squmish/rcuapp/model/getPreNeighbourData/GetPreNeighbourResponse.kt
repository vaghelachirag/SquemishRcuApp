package com.squmish.rcuapp.model.getPreNeighbourData

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squmish.rcuapp.model.getPreNeighbourData.GetPreNeighbourData


class GetPreNeighbourResponse {

    @SerializedName("status_Code")
    @Expose
    private var statusCode: Int? = null

    @SerializedName("status")
    @Expose
    private var status: String? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    @SerializedName("returnUrl")
    @Expose
    private var returnUrl: Any? = null

    @SerializedName("data")
    @Expose
    private var data: GetPreNeighbourData? = null

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

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getReturnUrl(): Any? {
        return returnUrl
    }

    fun setReturnUrl(returnUrl: Any?) {
        this.returnUrl = returnUrl
    }

    fun getData(): GetPreNeighbourData? {
        return data
    }

    fun setData(data: GetPreNeighbourData?) {
        this.data = data
    }

    fun getTextData(): Any? {
        return textData
    }

    fun setTextData(textData: Any?) {
        this.textData = textData
    }

}