package com.example.gemaichatbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.gemaichatbot.ui.theme.GemaichatbotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        setContent {
            GemaichatbotTheme {
                var currentPage by remember { mutableStateOf("chat") }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentPage) {
                        "chat" -> ChatPage(
                            viewModel = chatViewModel,
                            onNavigateToResult = { currentPage = "result" }
                        )
                        "result" -> ResultPage(
                            viewModel = chatViewModel,
                            onNavigateBack = {
                                currentPage = "chat"
                                chatViewModel.showResultPage.value = false
                                chatViewModel.currentQuestionIndex = 0 // Reset for new session
                            }
                        )
                    }
                }
            }
        }
    }
}


