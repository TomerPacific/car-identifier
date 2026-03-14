package com.tomerpacific.caridentifier

import android.content.SharedPreferences
import com.tomerpacific.caridentifier.model.DID_REQUEST_CAMERA_PERMISSION_KEY
import com.tomerpacific.caridentifier.model.PermissionViewModel
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class PermissionViewModelTest {

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var viewModel: PermissionViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        `when`(editor.putBoolean(anyString(), anyBoolean())).thenReturn(editor)
        `when`(sharedPreferences.getBoolean(DID_REQUEST_CAMERA_PERMISSION_KEY, false)).thenReturn(false)
        
        viewModel = PermissionViewModel(sharedPreferences)
    }

    @Test
    fun `initial state reflects shared preferences`() {
        assertFalse(viewModel.didRequestCameraPermission.value)
    }

    @Test
    fun `initial state reflects shared preferences when true`() {
        `when`(sharedPreferences.getBoolean(DID_REQUEST_CAMERA_PERMISSION_KEY, false)).thenReturn(true)
        val viewModelWithTrue = PermissionViewModel(sharedPreferences)
        assertTrue(viewModelWithTrue.didRequestCameraPermission.value)
    }

    @Test
    fun `setDidRequestCameraPermission updates state and persists when true`() {
        viewModel.setDidRequestCameraPermission(true)
        
        assertTrue(viewModel.didRequestCameraPermission.value)
        verify(editor).putBoolean(DID_REQUEST_CAMERA_PERMISSION_KEY, true)
        verify(editor).apply()
    }

    @Test
    fun `setDidRequestCameraPermission does not update or persist if already true`() {
        `when`(sharedPreferences.getBoolean(DID_REQUEST_CAMERA_PERMISSION_KEY, false)).thenReturn(true)
        val viewModelWithTrue = PermissionViewModel(sharedPreferences)
        
        viewModelWithTrue.setDidRequestCameraPermission(true)
        
        verify(editor, never()).putBoolean(anyString(), anyBoolean())
    }

    @Test
    fun `setShouldShowRationale updates state`() {
        assertFalse(viewModel.shouldShowRationale.value)
        
        viewModel.setShouldShowRationale(true)
        assertTrue(viewModel.shouldShowRationale.value)
        
        viewModel.setShouldShowRationale(false)
        assertFalse(viewModel.shouldShowRationale.value)
    }
}
