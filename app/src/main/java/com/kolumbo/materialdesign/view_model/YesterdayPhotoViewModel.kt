package com.kolumbo.materialdesign.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kolumbo.materialdesign.BuildConfig.NASA_API_KEY
import com.kolumbo.materialdesign.model.PODRetrofitImpl
import com.kolumbo.materialdesign.model.PODServerResponseData
import com.kolumbo.materialdesign.model.PictureOfTheDayData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

private const val DAY_PATTERN = "yyyy-MM-dd"

class YesterdayPhotoViewModel(
    private val liveDataForViewToObserve: MutableLiveData<PictureOfTheDayData> = MutableLiveData(),
    private val retrofitImpl: PODRetrofitImpl = PODRetrofitImpl()
) :
    ViewModel() {


    fun getData(): LiveData<PictureOfTheDayData> {
        sendServerRequest(convertDateToString().also { Log.d("QWE", "getData: $it") })
        return liveDataForViewToObserve
    }


    private fun sendServerRequest(days: String) {
        liveDataForViewToObserve.value = PictureOfTheDayData.Loading(null)
        val apiKey = NASA_API_KEY
        if (apiKey.isBlank()) {
            PictureOfTheDayData.Error(Throwable("You need API key"))
        } else {
            retrofitImpl.getRetrofitImpl().getPictureOfTheDayYesterday(apiKey, days)
                .enqueue(object :
                    Callback<PODServerResponseData> {
                    override fun onResponse(
                        call: Call<PODServerResponseData>,
                        response: Response<PODServerResponseData>
                    ) {
                        if (response.body() != null && response.isSuccessful) {
                            liveDataForViewToObserve.value =
                                PictureOfTheDayData.Success(response.body()!!)
                        } else {
                            val message = response.message()
                            if (message.isNullOrEmpty()) {
                                liveDataForViewToObserve.value =
                                    PictureOfTheDayData.Error(Throwable("Unidentified error"))
                            } else {
                                liveDataForViewToObserve.value =
                                    PictureOfTheDayData.Error(Throwable(message))
                            }
                        }
                    }

                    override fun onFailure(call: Call<PODServerResponseData>, t: Throwable) {
                        liveDataForViewToObserve.value = PictureOfTheDayData.Error(t)
                    }
                })
        }
    }


    private fun convertDateToString(): String {

        Calendar.getInstance().apply {
            add(Calendar.DATE, -1)
            return time.getNormalDate()
        }

    }

    private fun Date.getNormalDate() =
        android.text.format.DateFormat.format(DAY_PATTERN, this).toString()

}
