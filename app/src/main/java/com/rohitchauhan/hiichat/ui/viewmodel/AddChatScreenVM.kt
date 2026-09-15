package com.rohitchauhan.hiichat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitchauhan.hiichat.data.remote.firebase.dto.UserDto
import com.rohitchauhan.hiichat.domain.use_case.GetAllUserUC
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.util.Hash.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddChatScreenVM @Inject constructor(
    private val getAllUserUC: GetAllUserUC
) : ViewModel() {

    private val _getAllUsersState = MutableStateFlow<GetAllUsersState>(GetAllUsersState.Loading)
    val getAllUsersState = _getAllUsersState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            combine(_searchQuery, getAllUserUC()) { query, users ->
                if (query.isBlank()) {
                    users
                } else {
                    users.filter {
                        it.name.contains(query, ignoreCase = true) ||
                                it.email.contains(query, ignoreCase = true)
                    }
                }
            }.onStart {
                _getAllUsersState.value = GetAllUsersState.Loading
            }.catch {
                _getAllUsersState.value = GetAllUsersState.Error(it.message.toString())
            }.collect { filteredUsers ->
                _getAllUsersState.value = GetAllUsersState.Success(filteredUsers)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}

    sealed interface GetAllUsersState {
        object Loading : GetAllUsersState
        data class Success(val users: List<UserDto>) : GetAllUsersState
        data class Error(val message: String) : GetAllUsersState
    }