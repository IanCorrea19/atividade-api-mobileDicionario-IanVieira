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
        val geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=${city.replace(" ", "+")}&count=5&language=pt"
        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, geoUrl, null,
            { response ->
                try {
                    if (response.has("results")) {
                        val resultsArray = response.getJSONArray("results")

                        // Se a API achou apenas 1 cidade, vai direto para o clima
                        if (resultsArray.length() == 1) {
                            val location = resultsArray.getJSONObject(0)
                            processarCidadeEscolhida(location)
                        } else {
                            // Se achou mais de 1, monta uma lista de opções para o Pop-up
                            val opcoesCidades = Array(resultsArray.length()) { "" }

                            for (i in 0 until resultsArray.length()) {
                                val loc = resultsArray.getJSONObject(i)
                                val name = loc.getString("name")
                                val state = loc.optString("admin1", "")
                                val country = loc.optString("country", "")

                                opcoesCidades[i] = if (state.isNotEmpty()) "$name, $state, $country" else "$name, $country"
                            }

                            // Cria e mostra o Pop-up nativo do Android
                            val builder = android.app.AlertDialog.Builder(this)
                            builder.setTitle("Qual destas cidades?")
                            builder.setItems(opcoesCidades) { _, which ->
                                val locationEscolhida = resultsArray.getJSONObject(which)
                                processarCidadeEscolhida(locationEscolhida)
                            }
                            builder.show()
                        }
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

    // Função Auxiliar
    private fun processarCidadeEscolhida(location: org.json.JSONObject) {
        val lat = location.getDouble("latitude")
        val lon = location.getDouble("longitude")
        val timezone = location.getString("timezone")

        val name = location.getString("name")
        val state = location.optString("admin1", "")
        val country = location.optString("country", "")

        val fullLocation = if (state.isNotEmpty()) "$name, $state, $country" else "$name, $country"

        buscarClimaExato(lat, lon, timezone, fullLocation)
    }

    // Função 2: Pega o clima baseado nas coordenadas
    private fun buscarClimaExato(lat: Double, lon: Double, timezone: String, fullLocation: String) {
        val weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true"
        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, weatherUrl, null,
            { response ->
                try {
                    val currentWeather = response.getJSONObject("current_weather")

                    val temperature = currentWeather.getDouble("temperature")
                    val windSpeed = currentWeather.getDouble("windspeed")

                    val weatherEmoji = when {
                        temperature >= 30 -> "☀️🥵"
                        temperature in 20.0..29.9 -> "⛅😎"
                        temperature in 10.0..19.9 -> "☁️🧥"
                        else -> "❄️🥶"
                    }

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