package com.android.profkontur.Model

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.android.profkontur.ViewModel.TestsVIewModel

class QuestViewModelFactory(owner: SavedStateRegistryOwner, defaultArgs: Bundle? = null) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(TestsVIewModel::class.java)) {
            return TestsVIewModel(handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
