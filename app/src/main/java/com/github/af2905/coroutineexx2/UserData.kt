package com.github.af2905.coroutineexx2

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

data class UserData(
    val userId: Int = 1,
    val userName: String = "UserName",
    val userAge: Int = 20
) : AbstractCoroutineContextElement(UserData) {

    companion object Key : CoroutineContext.Key<UserData>

}