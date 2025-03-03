package com.example.gemaichatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemaichatbot.ui.theme.ColorModelMessage
import com.example.gemaichatbot.ui.theme.ColorUserMessage
import com.example.gemaichatbot.ui.theme.Purple80

@Composable
fun ChatPage(
    viewModel: ChatViewModel,
    onNavigateToResult: () -> Unit,
    modifier: Modifier = Modifier // Define the modifier parameter
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp) // Add padding to ensure no overlap with navigation buttons
    ) {
        AppHeader()
        MessageList(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp), // Add padding to the sides
            messageList = viewModel.messageList
        )
        MessageInput(
            viewModel = viewModel,
            onNavigateToResult = onNavigateToResult,
            modifier = Modifier.padding(horizontal = 8.dp) // Pass the modifier
        )
    }
}




@Composable
fun MessageList(modifier: Modifier = Modifier, messageList: List<MessageModel>) {
    if(messageList.isEmpty()){
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(60.dp),
                painter = painterResource(id = R.drawable.baseline_question_answer_24),
                contentDescription = "Icon",
                tint = Purple80,
            )
            Text(text = "Ask me anything", fontSize = 22.sp)
        }

    }else {
        LazyColumn (
            modifier = modifier,
            reverseLayout = true
        ) {
            items(messageList.reversed()){
                MessageRow(messageModel = it)
                //Text(text = it.message)
            }
        }

    }


}

@Composable
fun MessageRow(messageModel: MessageModel) {
    val isModel = messageModel.role == "model"

    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.align(if(isModel) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(
                        start = if(isModel) 8.dp else 70.dp,
                        end = if(isModel) 70.dp else 8.dp,
                        top = 8.dp,
                        bottom = 8.dp,
                    )
                    .clip(RoundedCornerShape(48f))
                    .background(if(isModel) ColorModelMessage else ColorUserMessage)
                    .padding(16.dp)
                )
            {
                Text(
                    text = messageModel.message,
                    fontWeight = FontWeight.W500,
                    color = Color.White
                )
            }


        }

    }

}

@Composable
fun MessageInput(
    viewModel: ChatViewModel,
    onNavigateToResult: () -> Unit,
    modifier: Modifier = Modifier // Define the modifier parameter
) {
    var message by remember {
        mutableStateOf("")
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Add vertical padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = message,
            onValueChange = { message = it },
            placeholder = { Text(text = "Type your answer here...") }
        )
        IconButton(onClick = {
            if (message.isNotEmpty()) {
                viewModel.messageList.add(MessageModel(message, "user"))
                viewModel.handleResponse(message, onNavigateToResult)
                message = ""
            }
        }) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send"
            )
        }
    }
}




@Composable
fun AppHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    )   {
        Text(
             modifier = Modifier.padding(16.dp),
             text = "GemaBot",
             color = Color.White,
             fontSize = 22.sp
        )
    }

}

