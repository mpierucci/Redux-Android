package com.mpierucci.androidredux

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.compose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class Store<State, Action>(
    initialState: State,
    middlewares: List<Middleware<State, Action>>,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> get() = _state.asStateFlow()

    private val chain = middlewares
        .map { middleware -> middleware(this) }
        .reduceOrNull { function, acc -> acc compose function }

    abstract suspend fun reduce(previous: State, action: Action): State


    fun dispatch(action: Action) {
        viewModelScope.launch {
            val previousState = _state.value
            val interceptedAction = chain?.invoke(action) ?: action
            val newState = reduce(previousState, interceptedAction)

            if (newState != previousState) _state.emit(newState)
        }
    }
}

