package com.example.travel_translator

import LanguageHelper
import TranslateTask
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.example.travel_translator.Spinnerhelper.Spinnerhelper
import com.example.travel_translator.databinding.ActivityTranslatorScreenBinding
import java.util.Locale

class TranslatorScreen : ComponentActivity(), OnInitListener {
    lateinit var binding: ActivityTranslatorScreenBinding
    private val REQUEST_CODE_SPEECH_INPUT = 100
    private var recognizedText: String = ""
    private var fromlan: String = ""
    private var tolan: String = ""
    private var tts: TextToSpeech? = null

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslatorScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize TextToSpeech
        tts = TextToSpeech(this, this)

        val spinner1: Spinner = findViewById(R.id.sp1)
        val spinner2: Spinner = findViewById(R.id.sp2)
        val languages = LanguageHelper().getAllLanguages()

        // Create an ArrayAdapter using the languages array
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.adapter = adapter
        spinner2.adapter = adapter

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                fromlan = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional: Handle the case where no item is selected
            }
        }

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                tolan = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional: Handle the case where no item is selected
            }
        }

        binding.mic.setOnClickListener {
            startSpeechToText()
        }

        binding.swlan.setOnClickListener {
            Spinnerhelper().swapSelectedItems<String>(spinner1, spinner2)
        }

        binding.button.setOnClickListener {
            val inputText = binding.input.text.toString()
            val fromlancode = LanguageHelper().getLanguageCode(fromlan)
            val tolancode = LanguageHelper().getLanguageCode(tolan)
            if (inputText.isEmpty()) {
                Toast.makeText(this, "Input can't be blank", Toast.LENGTH_SHORT).show()
            } else {
                TranslateTask(fromlancode!!, tolancode!!, inputText) { translatedText ->
                    if (translatedText != null) {
                        binding.output.setText(translatedText)
                    } else {
                        Toast.makeText(this, "Translation failed", Toast.LENGTH_SHORT).show()
                    }
                }.execute()
            }
        }

        binding.speak.setOnClickListener {
            val textToSpeak = binding.output.text.toString()
            if (textToSpeak.isEmpty()) {
                Toast.makeText(this, "No Translated Text To Speak", Toast.LENGTH_SHORT).show()
            } else {
                speakText(textToSpeak)
            }
        }
    }

    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        }

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Speech-to-text is not supported on this device.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {
            data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let {
                recognizedText = it[0]
                binding.input.setText(recognizedText)
            }
        }
    }

    private fun speakText(text: String) {
        tts?.let {
            it.language = Locale.forLanguageTag(LanguageHelper().getLanguageCode(tolan)!!)
            it.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            Log.d("TranslatorScreen", "TextToSpeech initialized successfully")
        } else {
            Log.e("TranslatorScreen", "Failed to initialize TextToSpeech")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.stop()
        tts?.shutdown()
    }
}
