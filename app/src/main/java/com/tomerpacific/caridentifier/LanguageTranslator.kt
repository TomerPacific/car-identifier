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
const val FAILED_TO_TRANSLATE_MSG = "Failed to translate"

private val TAG = LanguageTranslator::class.simpleName

class LanguageTranslator {

    private val translator: Translator

    private var isLanguageModelDownloaded = false

    val currentLocale = Locale.current.language

    init {
            val translatorOptions = buildTranslatorOptions(currentLocale)
            translator = Translation.getClient(translatorOptions)


            val downloadConditions = DownloadConditions.Builder()
                .build()
            translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener {
                    isLanguageModelDownloaded = true
                }
                .addOnFailureListener { error ->
                    Log.e(TAG, "Failed to download language model: ${error.message}")
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
                val translatedText = translator.translate(t).await()
                results.add(translatedText)
            } catch (e: Exception) {
                Log.e(TAG, "Translation failed: ${e.message}")
            }
        }

        return when (results.isEmpty()) {
            true -> Result.failure(Exception("Failed to translate text"))
            false -> Result.success(results)
        }
    }

    fun translateOwnership(ownership: String): String {
        return when (ownership) {
            "פרטי" -> "Private"
            "חברה" -> "Company"
            "ליסינג" -> "Lease"
            else -> ownership
        }
    }

    fun translateFuelType(fuelType: String): String {
        return when (fuelType) {
            "בנזין" -> "Gasoline"
            "דיזל" -> "Diesel"
            "חשמלי" -> "Electric"
            else -> fuelType
        }
    }

    fun getSectionHeaderTitle(sectionHeader: SectionHeader): String {
        return when (sectionHeader) {
            SectionHeader.PROS -> {
                return when (currentLocale) {
                    HEBREW_LANGUAGE_CODE -> PROS_SECTION_HEBREW
                    else -> PROS_SECTION_ENGLISH
                }
            }

            SectionHeader.CONS -> when (currentLocale) {
                HEBREW_LANGUAGE_CODE -> CONS_SECTION_HEBREW
                else -> CONS_SECTION_ENGLISH
            }
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

    fun clear() {
        translator.close()
    }
}

enum class SectionHeader {
    PROS,
    CONS
}