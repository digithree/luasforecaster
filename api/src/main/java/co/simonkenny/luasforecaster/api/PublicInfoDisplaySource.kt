package co.simonkenny.luasforecaster.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import retrofit2.Retrofit
import java.io.IOException
import java.lang.RuntimeException
import kotlin.jvm.Throws

/**
 * PublicInfoDisplaySource
 *
 * Data source for PublicInfoDisplay information from Luas Forecast API.
 *
 * @param retrofit      RetroFit instance with which to create API using interface spec.
 */
class PublicInfoDisplaySource(
    retrofit: Retrofit
) {

    // TODO : use DI instead of static references for basic singleton pattern here
    companion object {
        private val tikXml = TikXml.Builder()
            .exceptionOnUnreadXml(false)
            .build()

        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(TikXmlConverterFactory.create(tikXml))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        private val INSTANCE = PublicInfoDisplaySource(retrofit)

        fun getInstance() = INSTANCE
    }

    private val publicInfoDisplayApi = retrofit.create(PublicInfoDisplayApi::class.java)

    @Throws(IOException::class, RuntimeException::class)
    fun fetchStopInfoAsync(stopAbv: String) = publicInfoDisplayApi.fetchStopInfoAsync(stopAbv)
}