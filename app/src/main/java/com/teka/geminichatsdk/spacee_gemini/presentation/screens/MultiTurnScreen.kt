package com.teka.geminichatsdk.spacee_gemini.presentation.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.request.ImageRequest
import com.teka.geminichatsdk.spacee_gemini.components.ConversationArea
import com.teka.geminichatsdk.spacee_gemini.components.TypingArea
import com.teka.geminichatsdk.gemini_chat.data.ApiType
import com.teka.geminichatsdk.spacee_gemini.components.SelectedImageArea
import com.teka.geminichatsdk.spacee_gemini.presentation.MainViewModel
import com.teka.geminichatsdk.spacee_gemini.utils.ImageHelper
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MultiTurnScreen(
    viewModel: MainViewModel,
) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current


    val bitmaps: SnapshotStateList<Bitmap> = remember {
        mutableStateListOf()
    }

    val imageRequestBuilder = ImageRequest.Builder(context)
    val imageLoader = ImageLoader.Builder(context).build()



    //our various launchers
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) {
        if (it != null) {
            bitmaps.add(it)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
    ) {
        it.forEach { uri ->
            coroutineScope.launch {
                ImageHelper.scaleDownBitmap(uri, imageRequestBuilder, imageLoader)?.let { bitmap ->
                    bitmaps.add(bitmap)
                }
            }
        }
    }

    Scaffold(
        topBar = {

        }
    ) {
        Column(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .fillMaxSize()
                .fillMaxHeight(1f)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                ConversationArea(viewModel = viewModel, apiType = ApiType.MULTI_CHAT)
            }
            SelectedImageArea(bitmaps = bitmaps)
            TypingArea(
                viewModel = viewModel,
                apiType = ApiType.IMAGE_CHAT,
                bitmaps = bitmaps,
                galleryLauncher = galleryLauncher,
                permissionLauncher = permissionLauncher
            )
        }
    }

}