package com.tomerpacific.caridentifier

import android.util.Log
import androidx.compose.ui.text.intl.Locale
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.tomerpacific.caridentifier.model.CarDetails
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

const val HEBREW_LANGUAGE_CODE_UPDATED = "he"
const val HEBREW_LANGUAGE_CODE = "iw"

const val FAILED_TO_TRANSLATE_MSG = "Failed to translate"

private val tag = LanguageTranslator::class.simpleName

data class TranslationResult(
    val carDetails: CarDetails,
    val searchTerm: String
)

class LanguageTranslator {
    private val translator: Translator
    private val modelDownloadTask: Task<Void>

    val currentLocale = Locale.current.language

    init {
        val translatorOptions = buildTranslatorOptions(currentLocale)
        translator = Translation.getClient(translatorOptions)

        val downloadConditions =
            DownloadConditions.Builder()
                .requireWifi()
                .build()
        modelDownloadTask = translator.downloadModelIfNeeded(downloadConditions)
    }

    suspend fun translate(vararg text: String): Result<List<String>> =
        coroutineScope {
            try {
                modelDownloadTask.await()
            } catch (e: Exception) {
                Log.e(tag, "Failed to download language model: ${e.message}")
                return@coroutineScope Result.failure(e)
            }

            val deferredTranslations =
                text.map { t ->
                    async {
                        try {
                            translator.translate(t).await()
                        } catch (e: Exception) {
                            Log.e(tag, "Translation failed: ${e.message}")
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

    suspend fun translateCarDetails(carDetails: CarDetails): TranslationResult {
        return if (isHebrewLanguage()) {
            val carMakeAndModel = concatenateCarMakeAndModel(carDetails)
            val translationResult = translate(carMakeAndModel)
            val translatedText = translationResult.getOrNull()
            val searchTerm = if (translationResult.isSuccess && !translatedText.isNullOrEmpty()) {
                "$REVIEW_HEBREW${translatedText.first()}"
            } else {
                carMakeAndModel + REVIEW_ENGLISH
            }
            TranslationResult(carDetails, searchTerm)
        } else {
            val translationResult = translate(carDetails.color)
            val translatedColor = translationResult.getOrNull()
            val color = when {
                translationResult.isSuccess && !translatedColor.isNullOrEmpty() ->
                    translatedColor.first()
                translationResult.isFailure -> FAILED_TO_TRANSLATE_MSG
                else -> carDetails.color
            }

            val updatedCarDetails = carDetails.copy(
                color = color,
                ownership = translateOwnership(carDetails.ownership),
                fuelType = translateFuelType(carDetails.fuelType)
            )
            val searchTerm = concatenateCarMakeAndModel(updatedCarDetails) + REVIEW_ENGLISH
            TranslationResult(updatedCarDetails, searchTerm)
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

            SectionHeader.CONS ->
                when (isHebrewLanguage(currentLocale)) {
                    true -> CONS_SECTION_HEBREW
                    else -> CONS_SECTION_ENGLISH
                }
        }
    }

    private fun buildTranslatorOptions(locale: String): TranslatorOptions {
        return when (isHebrewLanguage(locale)) {
            true ->
                TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.HEBREW)
                    .build()
            else ->
                TranslatorOptions.Builder()
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
    CONS,
}
