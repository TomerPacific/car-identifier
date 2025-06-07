package com.tomerpacific.caridentifier

import android.util.Log
import androidx.compose.ui.text.intl.Locale
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

const val HEBREW_LANGUAGE_CODE = "iw"
private val TAG = LanguageTranslator::class.simpleName

class LanguageTranslator {



    private val englishHebrewTranslator: Translator

    private var isLanguageModelDownloaded = false

    val currentLocal = Locale.current.language

    init {
            val translatorOptions = buildTranslatorOptions(currentLocal)
            englishHebrewTranslator = Translation.getClient(translatorOptions)

            val downloadConditions = DownloadConditions.Builder()
                .requireWifi()
                .build()
            englishHebrewTranslator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener {
                    isLanguageModelDownloaded = true
                }
                .addOnFailureListener {
                    isLanguageModelDownloaded = false
                }
    }

    suspend fun translate(vararg text: String): Result<List<String>> {

        if (!isLanguageModelDownloaded) {
            return Result.failure(Exception("Failed to download language model"))
        }

        val results = mutableListOf<String>()

        for (t in text) {
            try {
                val translatedText = englishHebrewTranslator.translate(t).await()
                results.add(translatedText)
            } catch (e: Exception) {
                Log.e(TAG, "Translation failed: ${e.message}")
            }
        }

        return Result.success(results)
    }

    private fun buildTranslatorOptions(locale: String): TranslatorOptions {
        return when(locale) {
            HEBREW_LANGUAGE_CODE -> TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.HEBREW)
                .build()
            else -> TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.HEBREW)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build()
        }
    }
}