package com.example.forecastly


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forecastly.api.Constant
import com.example.forecastly.api.NetworkResponse
import com.example.forecastly.api.RetrofitInstance
import com.example.forecastly.api.WeatherModel
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val weatherApi = RetrofitInstance.weatherApi

    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult // ✅ public getter

    fun getData(city: String) {
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constant.apiKey, city)
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        _weatherResult.value = NetworkResponse.Success(data)
                    } else {
                        _weatherResult.value = NetworkResponse.Error("No data received")
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Error("Failed to load data: ${response.code()}")
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("Exception: ${e.message}")
                Log.e("WeatherViewModel", "Error fetching weather", e)
            }
        }
    }
}
