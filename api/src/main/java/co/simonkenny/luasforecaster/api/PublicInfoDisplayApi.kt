package co.simonkenny.luasforecaster.api

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

internal const val BASE_URL = "https://luasforecasts.rpa.ie/xml/"

/**
 * PublicInfoDisplayApi
 *
 * Network resource interface spec for Luas Forecast API, for use with RetroFit2.
 */
interface PublicInfoDisplayApi {

    @GET("get.ashx")
    fun fetchStopInfoAsync(
        @Query("stop") stopAbv: String = "",
        @Query("action") action: String = "forecast",
        @Query("encrypt") encrypt: String = "false"
    ): Deferred<StopInfo>
}