package com.samarth.ktornoteapp.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.ktornoteapp.data.remote.Models.User
import com.samarth.ktornoteapp.repositoy.NoteRepo
import com.samarth.ktornoteapp.utils.Constants.MAXIMUM_PASSWORD_LENGTH
import com.samarth.ktornoteapp.utils.Constants.MINIMUM_PASSWORD_LENGTH
import com.samarth.ktornoteapp.utils.MyResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject


/**
 * Created By Eslam Ghazy on 8/6/2022
 */
@HiltViewModel
class UserViewModel @Inject constructor(val noteRepo: NoteRepo) : ViewModel() {

    /*
    * why we are not using state flow instead of this shared flow is
    * because state flow will emit everytime whenever
    * anything happens for example even if we will rotate our device then will again emit the state
    * but shared flow will handle that rotation or any other event happening in mobile
    * */
    private val _registerState = MutableSharedFlow<MyResult<String>>() /*as a MutableLiveData*/
    val registerState: SharedFlow<MyResult<String>>
        get() {
            return _registerState  /* as LiveData*/
        }

    private val _loginState = MutableSharedFlow<MyResult<String>>()
    val loginState: SharedFlow<MyResult<String>> = _loginState

    private val _currentUserState = MutableSharedFlow<MyResult<User>>()
    val currentUserState: SharedFlow<MyResult<User>> = _currentUserState

    /*
    * to create user
    * */
    fun createUser(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) = viewModelScope.launch {
        _registerState.emit(MyResult.Loading())

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || password != confirmPassword) {
            _registerState.emit(MyResult.Error("Some Fields are empty"))
            return@launch
        }

        if (!isEmailValid(email)) {
            _registerState.emit(MyResult.Error("Email is not Valid!"))
            return@launch
        }

        if (!isPasswordValid(password)) {
            _registerState.emit(MyResult.Error("Password should be between $MINIMUM_PASSWORD_LENGTH and $MAXIMUM_PASSWORD_LENGTH"))
            return@launch
        }

        val newUser = User(name, email, password)
        _registerState.emit(noteRepo.createUser(newUser))
    }

    fun loginUser(
        name: String,
        email: String,
        password: String
    ) = viewModelScope.launch {
        _loginState.emit(MyResult.Loading()) /* emit to get value*/

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _loginState.emit(MyResult.Error("Some Fields are empty"))
            return@launch
        }

        if (!isEmailValid(email)) {
            _loginState.emit(MyResult.Error("Email is not Valid!"))
            return@launch
        }

        if (!isPasswordValid(password)) {
            _loginState.emit(MyResult.Error("Password should be between $MINIMUM_PASSWORD_LENGTH and $MAXIMUM_PASSWORD_LENGTH"))
            return@launch
        }

        val newUser = User(
            name,
            email,
            password
        )
        _loginState.emit(noteRepo.login(newUser))
    }


    fun getCurrentUser() = viewModelScope.launch {
        _currentUserState.emit(MyResult.Loading())
        _currentUserState.emit(noteRepo.getUser())
    }

    fun logout() = viewModelScope.launch {
        val result = noteRepo.logout()
        if (result is MyResult.Success) {
            getCurrentUser()
        }
    }


    /*
    * is email is belong to this pattern(regular expression) or not
    * */
    private fun isEmailValid(email: String): Boolean {
        var regex =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
        val pattern = Pattern.compile(regex)
        return (email.isNotEmpty() && pattern.matcher(email).matches())
    }

    private fun isPasswordValid(password: String): Boolean {

        /*
        * MINIMUM_PASSWORD_LENGTH >= password.length >= MAXIMUM_PASSWORD_LENGTH
        * */
        return (password.length in MINIMUM_PASSWORD_LENGTH..MAXIMUM_PASSWORD_LENGTH)

    }

}