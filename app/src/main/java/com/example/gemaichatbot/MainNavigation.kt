package com.example.gemaichatbot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun MainNavigation(viewModel: ChatViewModel) {
    var currentPage by remember { mutableStateOf("chat") }

    when (currentPage) {
        "chat" -> ChatPage(
            viewModel = viewModel,
            onNavigateToResult = { currentPage = "result" } // Navigate to result page
        )
        "result" -> ResultPage(
            viewModel = viewModel,
            onNavigateBack = {
                currentPage = "chat" // Navigate back to chat page
                viewModel.showResultPage.value = false
                viewModel.currentQuestionIndex = 0 // Reset question index for a new session
            }
        )
    }
}
