package com.example.gemaichatbot

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }

    val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = Constants.apiKey
    )

    private val questions = listOf(
        "Did you apply for asylum within one year of arriving in the United States?",
        "Have there been any changed circumstances in your country of nationality that affect your asylum eligibility?",
        "Have you experienced changes in circumstances outside your country that make you at risk of persecution?",
        "Were you an unaccompanied child when you arrived in the United States?",
        "Did you file your asylum application before the expiration of the 1-year deadline?",
        "Was your asylum application rejected, but you re-filed it within a reasonable time?",
        "Have you been convicted of a serious crime, such as an aggravated felony?",
        "Have you been involved in activities that threaten the security of the United States?",
        "Have you ordered, incited, assisted, or participated in the persecution of anyone on account of race, religion, nationality, membership in a particular social group, or political opinion?"
    )

    var currentQuestionIndex = 0
    private val responses = mutableListOf<Boolean>()

    var showResultPage = mutableStateOf(false)
    var resultMessage = mutableStateOf("")
    var recommendation = mutableStateOf("")

    init {
        startRuleBasedConversation()
    }

    private fun startRuleBasedConversation() {
        if (currentQuestionIndex < questions.size) {
            val initialMessage = questions[currentQuestionIndex]
            messageList.add(MessageModel(initialMessage, "model"))
        }
    }

    fun handleResponse(answer: String, onComplete: () -> Unit) {
        val isAffirmative = answer.equals("yes", ignoreCase = true)
        responses.add(isAffirmative)

        if (currentQuestionIndex < 6 && !isAffirmative) {
            fetchRecommendation(questions[currentQuestionIndex], onComplete)
            return
        }

        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            messageList.add(MessageModel(questions[currentQuestionIndex], "model"))
        } else {
            finalizeResults(onComplete)
        }
    }

    private fun fetchRecommendation(question: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role) { text(it.message) }
                    }.toList()
                )
                val response = chat.sendMessage("What happens if: $question was answered no?")
                recommendation.value = response.text.toString()
                resultMessage.value = "Your response to: \"$question\" may disqualify you."
                showResultPage.value = true
                onComplete()
            } catch (e: Exception) {
                recommendation.value = "Error generating recommendation: ${e.message}"
                resultMessage.value = "An error occurred. Please try again."
                showResultPage.value = true
                onComplete()
            }
        }
    }

    private fun finalizeResults(onComplete: () -> Unit) {
        val ineligibleDueToNoAnswers = responses.take(6).any { !it }
        if (ineligibleDueToNoAnswers) {
            // Already handled in fetchRecommendation
            return
        }

        resultMessage.value = "All responses are correct. An attorney will contact you."
        showResultPage.value = true
        onComplete()
    }
}
