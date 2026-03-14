package com.tomerpacific.caridentifier

import com.tomerpacific.caridentifier.data.network.ConnectivityObserver
import com.tomerpacific.caridentifier.data.network.NO_INTERNET_CONNECTION_ERROR
import com.tomerpacific.caridentifier.data.repository.CarDetailsRepository
import com.tomerpacific.caridentifier.model.CarDetails
import com.tomerpacific.caridentifier.model.CarViewModel
import com.tomerpacific.caridentifier.model.TirePressure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class CarViewModelTest {

    @Mock
    private lateinit var connectivityObserver: ConnectivityObserver

    @Mock
    private lateinit var carDetailsRepository: CarDetailsRepository

    @Mock
    private lateinit var languageTranslator: LanguageTranslator

    private lateinit var viewModel: CarViewModel
    private val testDispatcher = UnconfinedTestDispatcher()
    private var closeable: AutoCloseable? = null

    @Before
    fun setup() {
        closeable = MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        whenever(languageTranslator.currentLocale).thenReturn("en")
        whenever(languageTranslator.isHebrewLanguage(any())).thenReturn(false)
        whenever(languageTranslator.isHebrewLanguage()).thenReturn(false)
        
        viewModel = CarViewModel(
            connectivityObserver,
            carDetailsRepository,
            languageTranslator,
            testDispatcher,
            testDispatcher
        )
    }

    @After
    fun tearDown() {
        closeable?.close()
        Dispatchers.resetMain()
    }

    @Test
    fun `getCarDetails should set error when not connected to network`() = runTest {
        whenever(connectivityObserver.isConnectedToNetwork()).thenReturn(false)

        viewModel.getCarDetails("1234567")

        assertFalse(viewModel.mainUiState.value.isLoading)
        assertEquals(NO_INTERNET_CONNECTION_ERROR, viewModel.mainUiState.value.errorMessage)
    }

    @Test
    fun `getCarDetails should update state on success`() = runTest {
        val licensePlate = "1234567"
        val carDetails = createCarDetails(1234567)

        whenever(connectivityObserver.isConnectedToNetwork()).thenReturn(true)
        whenever(carDetailsRepository.getCarDetails(any())).thenReturn(Result.success(carDetails))
        whenever(languageTranslator.translate(any())).thenReturn(Result.success(listOf("White")))
        whenever(languageTranslator.translateOwnership(any())).thenReturn("Private")
        whenever(languageTranslator.translateFuelType(any())).thenReturn("Gasoline")

        viewModel.getCarDetails(licensePlate)

        advanceUntilIdle()

        assertFalse(viewModel.mainUiState.value.isLoading)
        assertNull(viewModel.mainUiState.value.errorMessage)
        assertNotNull(viewModel.mainUiState.value.carDetails)
        assertEquals(carDetails, viewModel.mainUiState.value.carDetails)
    }

    @Test
    fun `getCarDetails should update state on failure`() = runTest {
        val licensePlate = "1234567"
        val errorMessage = "Not Found"

        whenever(connectivityObserver.isConnectedToNetwork()).thenReturn(true)
        whenever(carDetailsRepository.getCarDetails(any())).thenReturn(Result.failure(Exception(errorMessage)))

        viewModel.getCarDetails(licensePlate)

        advanceUntilIdle()

        assertFalse(viewModel.mainUiState.value.isLoading)
        assertEquals(errorMessage, viewModel.mainUiState.value.errorMessage)
        assertNull(viewModel.mainUiState.value.carDetails)
    }

    @Test
    fun `getCarReview should update state on success`() = runTest {
        val carReviewString = "Pros\\nReliable\\nCons\\nExpensive"
        
        whenever(connectivityObserver.isConnectedToNetwork()).thenReturn(true)
        whenever(carDetailsRepository.getCarReview(any(), any())).thenReturn(Result.success(carReviewString))
        
        viewModel.getCarReview()
        
        advanceUntilIdle()
        
        assertFalse(viewModel.mainUiState.value.isLoading)
        assertNotNull(viewModel.mainUiState.value.carReview)
        assertEquals(listOf("Reliable"), viewModel.mainUiState.value.carReview?.prosList)
        assertEquals(listOf("Expensive"), viewModel.mainUiState.value.carReview?.consList)
    }

    @Test
    fun `getTirePressure should update state on success`() = runTest {
        val tirePressure = TirePressure("Manual", 32.0, 30.0, "psi", "")
        
        whenever(connectivityObserver.isConnectedToNetwork()).thenReturn(true)
        whenever(carDetailsRepository.getTirePressure(any())).thenReturn(Result.success(tirePressure))
        
        viewModel.getTirePressure()
        
        advanceUntilIdle()
        
        assertFalse(viewModel.mainUiState.value.isLoading)
        assertEquals(tirePressure, viewModel.mainUiState.value.tirePressure)
    }

    @Test
    fun `resetData should clear UI state`() = runTest {
        viewModel.resetData()

        assertNull(viewModel.mainUiState.value.carDetails)
        assertNull(viewModel.mainUiState.value.errorMessage)
        assertFalse(viewModel.mainUiState.value.isLoading)
    }

    private fun createCarDetails(licensePlate: Int) = CarDetails(
        licensePlateNumber = licensePlate,
        manufacturerCountry = "Japan",
        trimLevel = "GLX",
        safetyFeatureLevel = 5,
        pollutionLevel = 3,
        yearOfProduction = 2020,
        lastTestDate = "2023-01-01",
        validDate = "2024-01-01",
        ownership = "Private",
        frameNumber = "ABC123456",
        color = "White",
        frontWheel = "205/55R16",
        rearWheel = "205/55R16",
        fuelType = "Gasoline",
        firstOnRoadDate = "2020-05-05",
        commercialName = "Corolla",
        manufacturerName = "טויוטה"
    )
}
