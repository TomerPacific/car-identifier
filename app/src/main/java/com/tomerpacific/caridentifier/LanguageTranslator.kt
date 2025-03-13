package com.tomerpacific.caridentifier

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

class LanguageTranslator {

    private val englishHebrewTranslator: Translator

    private var isLanguageModelDownloaded = false

    init {
        val translatorOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.HEBREW)
            .build()
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
}