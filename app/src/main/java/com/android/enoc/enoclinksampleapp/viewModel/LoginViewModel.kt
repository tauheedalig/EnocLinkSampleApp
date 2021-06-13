package com.android.enoc.enoclinksampleapp.viewModel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.enoc.enoclinksampleapp.R
import com.android.enoc.enoclinksampleapp.utils.CommonConstants
import com.android.enoc.enoclinksampleapp.api.ResponseListener
import com.android.enoc.enoclinksampleapp.repository.MasterRepository
import com.android.enoc.enoclinksampleapp.storage.Storage
import com.android.enoc.enoclinksampleapp.ui.main.data.model.LoggedInUser
import com.android.enoc.enoclinksampleapp.ui.main.ui.login.LoggedInUserView
import com.android.enoc.enoclinksampleapp.ui.main.ui.login.LoginFormState
import com.android.enoc.enoclinksampleapp.ui.main.ui.login.LoginResult
import com.android.enoc.enoclinksampleapp.utils.CreatekeyStore
import com.google.gson.Gson
import javax.inject.Inject


class LoginViewModel @Inject constructor(
    private val masterRepository: MasterRepository,
    private val storage: Storage
) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(userName: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = masterRepository.login(userName, password, object : ResponseListener {
            override fun onSuccess(response: String) {

                val loggedInUser = Gson().fromJson(response, LoggedInUser::class.java)
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(userData = loggedInUser))
                storage.setString(
                    CommonConstants.REGISTERED_USER_ID,
                    CreatekeyStore.encryptString(userName)
                )
                storage.setString(
                    CommonConstants.PASSWORD,
                    CreatekeyStore.encryptString(password)
                )
            }

            override fun onFailure(reponse: String) {

                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        })


    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            true
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}