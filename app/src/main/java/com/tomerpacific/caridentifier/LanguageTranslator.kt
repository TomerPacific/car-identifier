package com.tomerpacific.caridentifier

import androidx.compose.ui.text.intl.Locale
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

private const val HEBREW_LANGUAGE_CODE = "iw"

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

    suspend fun translate(text: String): Result<String> {

        if (!isLanguageModelDownloaded) {
            return Result.failure(Exception("Failed to download language model"))
        }

        return try {
            val translatedText = englishHebrewTranslator.translate(text).await()
            Result.success(translatedText)
        } catch (e: Exception) {
            Result.failure(Exception("Translation failed: ${e.message}", e))
        }
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