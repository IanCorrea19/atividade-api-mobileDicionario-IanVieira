package com.example.atividade_api_mobileclima_ianvieira

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var tvAppTitle: TextView
    private lateinit var btnToggleLanguage: Button
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var etWord: TextInputEditText
    private lateinit var btnSearch: Button
    private lateinit var tvWordResult: TextView
    private lateinit var tvPhonetic: TextView
    private lateinit var tvPartOfSpeech: TextView
    private lateinit var tvDefinition: TextView

    private var isEnglish = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvAppTitle = findViewById(R.id.tvAppTitle)
        btnToggleLanguage = findViewById(R.id.btnToggleLanguage)
        textInputLayout = findViewById(R.id.textInputLayout)
        etWord = findViewById(R.id.etWord)
        btnSearch = findViewById(R.id.btnSearch)
        tvWordResult = findViewById(R.id.tvWordResult)
        tvPhonetic = findViewById(R.id.tvPhonetic)
        tvPartOfSpeech = findViewById(R.id.tvPartOfSpeech)
        tvDefinition = findViewById(R.id.tvDefinition)


        btnToggleLanguage.setOnClickListener {
            isEnglish = !isEnglish
            atualizarTextosDaInterface()
        }

        btnSearch.setOnClickListener {
            val word = etWord.text.toString().trim()

            if (word.isEmpty()) {
                val msgVazio = if (isEnglish) "Please, type a word!" else "Por favor, digite uma palavra!"
                Toast.makeText(this, msgVazio, Toast.LENGTH_SHORT).show()
            } else {
                btnSearch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E53935"))
                btnSearch.text = if (isEnglish) "Searching..." else "Buscando..."
                btnSearch.isEnabled = false

                buscarPalavra(word)
            }
        }
    }

    private fun atualizarTextosDaInterface() {
        if (isEnglish) {
            btnToggleLanguage.text = "PT-BR"
            tvAppTitle.text = "English Dictionary"
            textInputLayout.hint = "Type a word in English (Ex: engineer)"
            btnSearch.text = "Search Word"

            if (tvPhonetic.text == "/.../") {
                tvWordResult.text = "Waiting for a word..."
                tvPartOfSpeech.text = "Part of Speech: -"
                tvDefinition.text = "Definition: -"
            }
        } else {
            btnToggleLanguage.text = "EN"
            tvAppTitle.text = "Dicionário de Inglês"
            textInputLayout.hint = "Digite uma palavra em inglês (Ex: engineer)"
            btnSearch.text = "Buscar Palavra"

            if (tvPhonetic.text == "/.../") {
                tvWordResult.text = "Aguardando uma palavra..."
                tvPartOfSpeech.text = "Classe Gramatical: -"
                tvDefinition.text = "Definição: -"
            }
        }
    }

    private fun restaurarBotao() {
        btnSearch.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#673AB7"))
        btnSearch.text = if (isEnglish) "Search Word" else "Buscar Palavra"
        btnSearch.isEnabled = true
    }

    private fun buscarPalavra(word: String) {
        val url = "https://api.dictionaryapi.dev/api/v2/entries/en/${word.lowercase()}"
        val queue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val firstEntry = response.getJSONObject(0)

                    val wordText = firstEntry.getString("word")
                    val phonetic = firstEntry.optString("phonetic", "Phonetic not available")

                    // Pega o primeiro grupo de significados (ex: Substantivo)
                    val meanings = firstEntry.getJSONArray("meanings")
                    val firstMeaning = meanings.getJSONObject(0)
                    val partOfSpeech = firstMeaning.getString("partOfSpeech")

                    // Lista de definições
                    val definitionsArray = firstMeaning.getJSONArray("definitions")

                    // Ferramenta do Kotlin para juntar vários textos de forma otimizada
                    val definicoesJuntas = StringBuilder()

                    // Lógica para pegar até 3 definições (para caber bonito na tela)
                    val limite = if (definitionsArray.length() > 3) 3 else definitionsArray.length()

                    for (i in 0 until limite) {
                        val defObj = definitionsArray.getJSONObject(i)
                        val textoDefinicao = defObj.getString("definition")

                        // Adiciona o número (1., 2., 3.) e o texto, pulando duas linhas no final
                        definicoesJuntas.append("${i + 1}. $textoDefinicao\n\n")
                    }

                    val labelPart = if (isEnglish) "Part of Speech: " else "Classe: "
                    val labelDef = if (isEnglish) "Definitions:\n" else "Definições:\n"

                    tvWordResult.text = wordText.replaceFirstChar { it.uppercase() }
                    tvPhonetic.text = phonetic
                    tvPartOfSpeech.text = "$labelPart${partOfSpeech.uppercase()}"

                    // Remove os espaços extras do final e exibe na tela
                    tvDefinition.text = "$labelDef${definicoesJuntas.toString().trim()}"

                    restaurarBotao()

                } catch (e: Exception) {
                    val msgErroJson = if (isEnglish) "Error reading data." else "Erro ao ler dados."
                    Toast.makeText(this, msgErroJson, Toast.LENGTH_SHORT).show()
                    restaurarBotao()
                }
            },
            { error ->
                val msgNaoEncontrada = if (isEnglish) "Word not found! Try another one." else "Palavra não encontrada! Tente outra."
                Toast.makeText(this, msgNaoEncontrada, Toast.LENGTH_LONG).show()

                val textoVazio = if (isEnglish) "Word not found" else "Palavra não encontrada"
                tvWordResult.text = textoVazio
                tvPhonetic.text = "/.../"
                tvPartOfSpeech.text = if (isEnglish) "Part of Speech: -" else "Classe Gramatical: -"
                tvDefinition.text = if (isEnglish) "Definitions: -" else "Definições: -"

                restaurarBotao()
            }
        )
        queue.add(request)
    }
}