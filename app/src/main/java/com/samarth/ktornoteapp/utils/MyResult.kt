package com.samarth.ktornoteapp.utils

import kotlin.reflect.KProperty

/**
 * Created By Eslam Ghazy on 8/6/2022
 * this is wrapper class
 */
open class MyResult<T>(val data: T? = null, val errorMessage: String? = null) {

    class Success<T>(data: T, errorMessage: String? = null) : MyResult<T>(data, errorMessage)
    class Error<T>(errorMessage: String, data: T? = null) : MyResult<T>(data, errorMessage)
    class Loading<T> : MyResult<T>()

    override fun toString(): String {
        return "MyResult(data=$data, errorMessage=$errorMessage)"
    }

}