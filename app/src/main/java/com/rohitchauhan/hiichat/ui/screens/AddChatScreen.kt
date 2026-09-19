package com.rohitchauhan.hiichat.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohitchauhan.hiichat.R
import com.rohitchauhan.hiichat.components.UserItem
import com.rohitchauhan.hiichat.ui.viewmodel.AddChatScreenVM
import com.rohitchauhan.hiichat.ui.viewmodel.GetAllUsersState

@Composable
fun AddChatScreen(
    onUserClick: (chatId: String, otherUserId: String, otherUserName: String) -> Unit = { _, _, _ -> }
) {
    val viewModel: AddChatScreenVM = hiltViewModel()
    val getAllUsersState = viewModel.getAllUsersState.collectAsStateWithLifecycle().value
    val searchQuery = viewModel.searchQuery.collectAsState().value

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search users...") },
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.search),
                        contentDescription = null
                    )
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.size(16.dp))

            when (getAllUsersState) {
                is GetAllUsersState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is GetAllUsersState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = getAllUsersState.message, color = Color.Red)
                    }
                }

                is GetAllUsersState.Success -> {
                    LazyColumn {
                        items(getAllUsersState.users) { user ->
                            UserItem(
                                user = user,
                                onClick = {
                                    onUserClick(
                                        viewModel.getChatId(user.id),
                                        user.id,
                                        user.name
                                    )
                                }
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                thickness = 0.5.dp,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }

        }
    }
}
