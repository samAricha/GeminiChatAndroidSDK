package com.teka.geminichatsdk.spacee_gemini

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.teka.geminichatsdk.spacee_gemini.components.ConversationArea
import com.spongycode.spaceegemini.components.TypingArea
import com.teka.geminichatsdk.gemini_chat.data.ApiType

@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MultiTurnScreen(
    viewModel: MainViewModel,
) {

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
                ConversationArea(viewModel, apiType = ApiType.MULTI_CHAT)
            }
            TypingArea(
                viewModel = viewModel,
                apiType = ApiType.MULTI_CHAT,
                bitmaps = null,
                galleryLauncher = null,
                permissionLauncher = null
            )
        }
    }

}
