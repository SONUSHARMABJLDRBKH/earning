package com.example.ai

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private const val TAG = "GeminiAssistantVM"

data class AiChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String
)

sealed interface AiUiState {
    data object Idle : AiUiState
    data object Loading : AiUiState
    data class Streaming(val text: String) : AiUiState
    data class Success(val text: String) : AiUiState
    data class Error(val message: String) : AiUiState
}

class GeminiAssistantViewModel : ViewModel() {

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    "You are the official Royal Bullet 350 Rider & Concierge Assistant. " +
                        "Help riders in English or Hindi (matching the user's language) with questions about " +
                        "Royal Enfield motorcycles & scooters (Bullet 350 Black ₹700 - ₹120/day, Classic Scooty ₹1,500 - ₹240/day, " +
                        "Hunter 350 Blue ₹2,000 - ₹340/day, Meteor 350 Flagship ₹3,000 - ₹520/day), " +
                        "VIP Luxury Car plans (Thar ₹800, BMW M4 ₹1,200, Ferrari SF90 ₹1,500, Mercedes AMG ₹2,000, G-Wagon ₹3,000), " +
                        "wallet rules (₹120 signup Account Balance bonus, minimum withdrawal ₹150, 2% fee, ₹85 direct referral after verified orders), " +
                        "and motorcycle riding or maintenance tips. Be concise, accurate, and friendly."
                )
            }
        )
    }

    private val chatSession by lazy { model.startChat() }

    private val _uiState = MutableStateFlow<AiUiState>(AiUiState.Idle)
    val uiState: StateFlow<AiUiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<AiChatMessage>>(emptyList())
    val messages: StateFlow<List<AiChatMessage>> = _messages.asStateFlow()

    fun askAssistant(promptInput: String) {
        val prompt = promptInput.trim()
        if (prompt.isEmpty()) return

        val userMsg = AiChatMessage(
            id = "u_${System.nanoTime()}",
            isUser = true,
            text = prompt
        )
        _messages.value = _messages.value + userMsg
        _uiState.value = AiUiState.Loading

        viewModelScope.launch {
            val accumulatedText = StringBuilder()
            try {
                withTimeout(60_000L) {
                    chatSession.sendMessageStream(prompt).collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        if (chunkText.isNotEmpty()) {
                            accumulatedText.append(chunkText)
                            _uiState.value = AiUiState.Streaming(accumulatedText.toString())
                        }
                    }
                }
                val finalText = accumulatedText.toString().trim()
                if (finalText.isNotEmpty()) {
                    _messages.value = _messages.value + AiChatMessage(
                        id = "m_${System.nanoTime()}",
                        isUser = false,
                        text = finalText
                    )
                    _uiState.value = AiUiState.Success(finalText)
                } else {
                    _uiState.value = AiUiState.Error("Gemini returned an empty response. Please try again.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Gemini streaming timed out", e)
                _uiState.value = AiUiState.Error("Request timed out after 60 seconds. Please try again.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Firebase AI Logic request failed", e)
                _uiState.value = AiUiState.Error(
                    e.localizedMessage ?: "Gemini request failed. Check network or App Check setup."
                )
            }
        }
    }

    fun clearConversation() {
        _messages.value = emptyList()
        _uiState.value = AiUiState.Idle
    }
}
