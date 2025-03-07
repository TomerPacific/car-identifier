package com.tomerpacific.caridentifier

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class LanguageTranslator {

    private val englishHebrewTranslator: Translator

    private val _didDownloadLanguageModel = MutableStateFlow(false)
    val didDownloadLanguageModel: StateFlow<Boolean> get() = _didDownloadLanguageModel

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
                _didDownloadLanguageModel.value = true
            }
    }

    suspend fun translate(text: String): Result<String> {

        if (!_didDownloadLanguageModel.value) {
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