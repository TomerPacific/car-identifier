package com.tomerpacific.caridentifier

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

class LanguageTranslator {

    private val englishHebrewTranslator: Translator
    private var didDownloadLanguageModel: Boolean = false

    init {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.HEBREW)
            .build()
        englishHebrewTranslator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        englishHebrewTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                didDownloadLanguageModel = true
            }
    }

    suspend fun translate(text: String): Result<String> {

        if (!didDownloadLanguageModel) {
            return Result.failure(Exception("Failed to download language model"))
        }

        val translatedText = englishHebrewTranslator.translate(text).await()
        return Result.success(translatedText)
    }
}