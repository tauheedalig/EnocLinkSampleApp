package com.android.enoc.enoclinksampleapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.android.enoc.enoclinksampleapp.R
import com.android.enoc.enoclinksampleapp.storage.Storage
import com.android.enoc.enoclinksampleapp.ui.main.data.model.LoggedInUser
import com.android.enoc.enoclinksampleapp.utils.CommonConstants
import com.android.enoc.enoclinksampleapp.utils.CreatekeyStore
import com.android.enoc.enoclinksampleapp.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {
    @Inject
    lateinit var storage: Storage

    @Inject
    lateinit var mainViewModel: MainViewModel
    lateinit var loadingProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingProgressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        loadingProgressBar.visibility=View.VISIBLE
        if (storage.getString(CommonConstants.REGISTERED_USER_ID).isNullOrBlank())
            navigateToLogin()
        else {
            setLoginObserver()
            autoLogin()
        }

    }

    private fun setLoginObserver() {
        mainViewModel.loginFailed.observe(viewLifecycleOwner, Observer { loginFailed ->
            loginFailed ?: return@Observer
            showLoginFailed()
        })

        mainViewModel.loginUser.observe(viewLifecycleOwner, Observer { loginUser ->
            loginUser ?: return@Observer
            mainViewModel.loginUser.value?.let { navigateToUserDetails(it)
                loadingProgressBar.visibility=View.GONE}
        })
    }

    private fun showLoginFailed() {
        loadingProgressBar.visibility=View.GONE
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, getString(R.string.login_failed), Toast.LENGTH_LONG).show()

    }

    private fun navigateToLogin() {
        val direction = MainFragmentDirections.actionMainFragmentToLoginFragment()
        findNavController().navigate(direction)
    }

    private fun autoLogin() {
        val userId =
            CreatekeyStore.decryptString(storage.getString(CommonConstants.REGISTERED_USER_ID))
        val password = CreatekeyStore.decryptString(storage.getString(CommonConstants.PASSWORD))
        mainViewModel.login(
            userId, password
        )
    }

    private fun navigateToUserDetails(loggedInUser: LoggedInUser) {

        val direction = MainFragmentDirections.actionMainFragmentToDetailFragment(loggedInUser.userId,loggedInUser.token)
        findNavController().navigate(direction)
        loadingProgressBar.visibility=View.GONE
    }
}