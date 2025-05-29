package com.theoghn.mtgtreasury.ui.community

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoghn.mtgtreasury.models.AppUser
import com.theoghn.mtgtreasury.models.card.MtgCard
import com.theoghn.mtgtreasury.services.ExternalUserService
import com.theoghn.mtgtreasury.services.UserService
import com.theoghn.mtgtreasury.ui.cardslist.InventoryScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val userService: ExternalUserService
) : ViewModel() {
    var searchQuery = MutableStateFlow("")
    val uiState = MutableStateFlow<CommunityScreenUiState>(CommunityScreenUiState.Uninitialized)

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun onSearchQueryChange(){
        uiState.update { CommunityScreenUiState.Loading }
        viewModelScope.launch {

            val users = userService.getUsersByName(searchQuery.value)
            Log.d("CommunityViewModel", "Found ${users.size} users for query: ${searchQuery.value}")
            if (users.isEmpty()) {
                uiState.update { CommunityScreenUiState.Empty }
            } else {
                uiState.update {
                    CommunityScreenUiState.CommunityUi(users)
                }
            }

//                uiState.update { CommunityScreenUiState.Empty }

        }
    }
}

sealed class CommunityScreenUiState {
    data class CommunityUi(
        val users: List<AppUser>
    ) : CommunityScreenUiState()

    data object Loading : CommunityScreenUiState()
    data object Uninitialized : CommunityScreenUiState()
    data object Empty : CommunityScreenUiState()
}