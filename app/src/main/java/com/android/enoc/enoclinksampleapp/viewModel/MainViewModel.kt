package com.android.enoc.enoclinksampleapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.enoc.enoclinksampleapp.api.ResponseListener
import com.android.enoc.enoclinksampleapp.repository.MasterRepository
import com.android.enoc.enoclinksampleapp.storage.Storage
import com.android.enoc.enoclinksampleapp.ui.main.data.model.LoggedInUser
import com.google.gson.Gson
import javax.inject.Inject

class MainViewModel @Inject constructor(private val masterRepository: MasterRepository,  private val storage: Storage) : ViewModel() {
    // TODO: Implement the ViewModel
    val loginUser = MutableLiveData<LoggedInUser>()
    val loginFailed = MutableLiveData<Boolean>()
    fun login(userName: String, password: String) {
        // can be launched in a separate asynchronous job
        masterRepository.login(userName, password, object : ResponseListener {
            override fun onSuccess(response: String) {

                val loggedInUser = Gson().fromJson(response, LoggedInUser::class.java)
                loginUser.value = loggedInUser
            }

            override fun onFailure(reponse: String) {
                loginFailed.value = true

            }
        })


    }
}