package com.android.enoc.enoclinksampleapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.enoc.enoclinksampleapp.api.ResponseListener
import com.android.enoc.enoclinksampleapp.repository.MasterRepository
import com.android.enoc.enoclinksampleapp.storage.SharedPreferencesStorage
import com.android.enoc.enoclinksampleapp.ui.main.data.model.UserProfile
import com.android.enoc.enoclinksampleapp.ui.main.ui.userprofile.UserDetailsFragmentArgs
import com.android.enoc.enoclinksampleapp.utils.CommonConstants
import com.android.enoc.enoclinksampleapp.utils.CreatekeyStore
import com.google.gson.Gson
import javax.inject.Inject

class UserDetailsViewModel @Inject constructor(
    private val masterRepository: MasterRepository,
    private val storage: SharedPreferencesStorage
) : ViewModel() {
    // TODO: Implement the ViewModel
    val emailId = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val imageUpLoaded = MutableLiveData<Boolean>()
    fun init() {
        val encryptedPassword = storage.getString(CommonConstants.PASSWORD)
        password.value = CreatekeyStore.decryptString(encryptedPassword)
    }

    fun getUserInfo(args: UserDetailsFragmentArgs) {
        // can be launched in a separate asynchronous job
         masterRepository.getUserInfo(
            args.userId,
            args.token,
            object : ResponseListener {
                override fun onSuccess(response: String) {
                    val userProfile = Gson().fromJson(response, UserProfile::class.java)
                    imageUrl.value = userProfile.avatar_url
                    emailId.value = userProfile.email

                }

                override fun onFailure(reponse: String) {
                    imageUrl.value = ""
                    emailId.value =
                        CreatekeyStore.decryptString(storage.getString(CommonConstants.REGISTERED_USER_ID))
                }
            })


    }

    fun uploadImage(imageBytes: ByteArray, fileName: String,args: UserDetailsFragmentArgs) {
        masterRepository.uploadImage(imageBytes,fileName, args.token,  object : ResponseListener{
            override fun onSuccess(response: String) {
               imageUpLoaded.value=true
            }

            override fun onFailure(reponse: String) {
                imageUpLoaded.value=false
            }
        } )

    }
}