package co.simonkenny.luasforecaster.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.simonkenny.luasforecaster.api.DIRECTION_INBOUND
import co.simonkenny.luasforecaster.api.DIRECTION_OUTBOUND
import co.simonkenny.luasforecaster.api.PublicInfoDisplaySource
import co.simonkenny.luasforecaster.api.Tram
import co.simonkenny.luasforecaster.util.SingleLiveEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE

private const val STOP_ABRV_EARLY = "mar"
private const val STOP_ABRV_LATE = "sti"

private enum class Period { EARLY, LATE }

data class StopInfoRequestWrapper(
        val stopAbrv: String,
        val direction: String,
        val messagePrefix: String
)

class MainViewModel(
        private val publicInfoDisplaySource: PublicInfoDisplaySource,
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _tramsDataList = MutableLiveData<List<Tram>>()
    val tramsDataList: LiveData<List<Tram>> = _tramsDataList

    private val _footerMainText = MutableLiveData<String>()
    val footerMainText: LiveData<String> = _footerMainText

    private val _footerSubText = MutableLiveData<String>()
    val footerSubText: LiveData<String> = _footerSubText

    private val _alertText = SingleLiveEvent<String>()
    val alertText: LiveData<String> = _alertText

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val stopInfoRequests = mapOf(
            Period.EARLY to StopInfoRequestWrapper(STOP_ABRV_EARLY, DIRECTION_OUTBOUND, "☀️ "),
            Period.LATE to StopInfoRequestWrapper(STOP_ABRV_LATE, DIRECTION_INBOUND, "🌙 ")
    )

    fun fetchStopInfo() {
        _isLoading.postValue(true)
        viewModelScope.launch(dispatcher) {
            try {
                with(requireNotNull(stopInfoRequests[currentDayPeriod()])) {
                    with(withContext(Dispatchers.IO) { publicInfoDisplaySource.fetchStopInfoAsync(stopAbrv).await() }) {
                        Log.d(this@MainViewModel::class.java.simpleName, "StopInfo: $this")
                        directions?.find { it.name == direction }
                            ?.trams
                            ?.filter { it.dueMins?.isNotBlank() == true }
                            ?.let {
                                _tramsDataList.postValue(it)
                            } ?: run {
                                _alertText.postValue("Error fetching data")
                                _tramsDataList.postValue(emptyList())
                            }
                        _footerMainText.postValue("$messagePrefix$stop")
                        _footerSubText.postValue(direction.capitalize())
                        _isLoading.postValue(false)
                    }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                _alertText.postValue(t.message ?: "Error fetching data")
                _tramsDataList.postValue(emptyList())
                _isLoading.postValue(false)
            }
        }
    }

    private fun currentDayPeriod() =
            with (Calendar.getInstance()) {
                if (get(HOUR_OF_DAY) < 12 || (get(HOUR_OF_DAY) == 12 && get(MINUTE) == 0)) {
                    Period.EARLY
                } else {
                    Period.LATE
                }
            }
}