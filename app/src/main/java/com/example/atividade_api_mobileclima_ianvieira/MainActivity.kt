package com.example.atividade_api_mobileclima_ianvieira

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var etCityName: TextInputEditText
    private lateinit var btnSearchWeather: Button
    private lateinit var tvLocationFull: TextView
    private lateinit var tvWeatherIcon: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvWind: TextView
    private lateinit var tvTimezone: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCityName = findViewById(R.id.etCityName)
        btnSearchWeather = findViewById(R.id.btnSearchWeather)
        tvLocationFull = findViewById(R.id.tvLocationFull)
        tvWeatherIcon = findViewById(R.id.tvWeatherIcon)
        tvTemperature = findViewById(R.id.tvTemperature)
        tvWind = findViewById(R.id.tvWind)
        tvTimezone = findViewById(R.id.tvTimezone)

        btnSearchWeather.setOnClickListener {
            val city = etCityName.text.toString().trim()

            if (city.isEmpty()) {
                Toast.makeText(this, "Por favor, digite o nome de uma cidade!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Buscando o clima...", Toast.LENGTH_SHORT).show()
                buscarCoordenadasDaCidade(city)
            }
        }
    }

    private fun buscarCoordenadasDaCidade(city: String) {
        val geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=${city.replace(" ", "+")}&count=1&language=pt"
        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, geoUrl, null,
            { response ->
                try {
                    if (response.has("results")) {
                        val location = response.getJSONArray("results").getJSONObject(0)

                        val lat = location.getDouble("latitude")
                        val lon = location.getDouble("longitude")
                        val timezone = location.getString("timezone")

                        // Extraindo os detalhes para formatar (Ex: Itaperuna, Rio de Janeiro, Brasil)
                        val name = location.getString("name")
                        val state = location.optString("admin1", "")
                        val country = location.optString("country", "")

                        val fullLocation = if (state.isNotEmpty()) "$name, $state, $country" else "$name, $country"

                        // Com as coordenadas e o nome formatado em mãos, busca o clima!
                        buscarClimaExato(lat, lon, timezone, fullLocation)
                    } else {
                        Toast.makeText(this, "Cidade não encontrada.", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Erro ao ler dados da cidade.", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(this, "Erro de rede ao buscar a cidade.", Toast.LENGTH_LONG).show()
            }
        )
        queue.add(request)
    }

    private fun buscarClimaExato(lat: Double, lon: Double, timezone: String, fullLocation: String) {
        val weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true"
        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, weatherUrl, null,
            { response ->
                try {
                    val currentWeather = response.getJSONObject("current_weather")

                    val temperature = currentWeather.getDouble("temperature")
                    val windSpeed = currentWeather.getDouble("windspeed")

                    // Lógica para mudar o ícone dependendo da temperatura!
                    val weatherEmoji = when {
                        temperature >= 30 -> "☀️🥵"
                        temperature in 20.0..29.9 -> "⛅😎"
                        temperature in 10.0..19.9 -> "☁️🧥"
                        else -> "❄️🥶"
                    }

                    // Atualiza a tela com as informações
                    tvLocationFull.text = fullLocation
                    tvWeatherIcon.text = weatherEmoji
                    tvTemperature.text = "Temperatura: $temperature °C"
                    tvWind.text = "Vento: $windSpeed km/h"
                    tvTimezone.text = "Fuso Horário: $timezone"

                } catch (e: Exception) {
                    Toast.makeText(this, "Erro ao processar o clima.", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(this, "Erro de rede ao buscar o clima.", Toast.LENGTH_LONG).show()
            }
        )
        queue.add(request)
    }
}