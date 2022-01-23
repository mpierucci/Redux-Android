package com.mpierucci.androidredux


/**
 * A middleware is a higher-order function that composes a dispatch function to return a new dispatch function.
 */
typealias Middleware<State, Action> = (Store<State, Action>) -> (Action) -> Action