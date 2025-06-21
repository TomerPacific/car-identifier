package com.tomerpacific.caridentifier

import android.util.Log
import androidx.compose.ui.text.intl.Locale
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

const val HEBREW_LANGUAGE_CODE_UPDATED = "he"
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
                .requireWifi()
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

    suspend fun translate(vararg text: String): Result<List<String>> = coroutineScope {
        if (!isLanguageModelDownloaded) {
            return@coroutineScope Result.failure(Exception("Failed to download language model"))
        }

        val deferredTranslations = text.map { t ->
            async {
                try {
                    translator.translate(t).await()
                } catch (e: Exception) {
                    Log.e(TAG, "Translation failed: ${e.message}")
                    null
                }
            }
        }

        val results = deferredTranslations.awaitAll().filterNotNull()

        return@coroutineScope when {
            results.isEmpty() -> Result.failure(Exception("Failed to translate text"))
            else -> Result.success(results)
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
                when (isHebrewLanguage(currentLocale)) {
                    true -> PROS_SECTION_HEBREW
                    else -> PROS_SECTION_ENGLISH
                }
            }

            SectionHeader.CONS -> when (isHebrewLanguage(currentLocale)) {
                true -> CONS_SECTION_HEBREW
                else -> CONS_SECTION_ENGLISH
            }
        }
    }

    private fun buildTranslatorOptions(locale: String): TranslatorOptions {
        return when(isHebrewLanguage(locale)) {
            true -> TranslatorOptions.Builder()
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

    fun isHebrewLanguage(locale: String = currentLocale): Boolean {
        return locale == HEBREW_LANGUAGE_CODE || locale == HEBREW_LANGUAGE_CODE_UPDATED
    }
}

enum class SectionHeader {
    PROS,
    CONS
}