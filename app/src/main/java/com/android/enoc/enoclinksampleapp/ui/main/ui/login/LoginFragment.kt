package com.android.enoc.enoclinksampleapp.ui.main.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.android.enoc.enoclinksampleapp.BR
import com.android.enoc.enoclinksampleapp.R
import com.android.enoc.enoclinksampleapp.databinding.FragmentLoginBinding
import com.android.enoc.enoclinksampleapp.viewModel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    @Inject
    lateinit var loginViewModel: LoginViewModel
    lateinit var dataBinding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()

            }
        }
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentLoginBinding.inflate(
            inflater,
            container,
            false
        )
        dataBinding.setLifecycleOwner(this);
        dataBinding.setVariable(BR.viewModel, loginViewModel);
        dataBinding.executePendingBindings();
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObserver()

        setTextChangeListener()

        setClickListener()
    }

    private fun setTextChangeListener(

    ) {
        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                    dataBinding.username.text.toString(),
                    dataBinding.password.text.toString()
                )
            }
        }
        dataBinding.username.addTextChangedListener(afterTextChangedListener)
        dataBinding.password.addTextChangedListener(afterTextChangedListener)
        dataBinding.password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                    dataBinding.username.text.toString(),
                    dataBinding.password.text.toString()
                )
            }
            false
        }
    }

    private fun setClickListener(

    ) {
        dataBinding.login.setOnClickListener {
            dataBinding.loading.visibility = View.VISIBLE
            loginViewModel.login(
                dataBinding.username.text.toString(),
                dataBinding.password.text.toString()
            )
        }
    }

    private fun setObserver(

    ) {
        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                dataBinding.login.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    dataBinding.username.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    dataBinding.password.error = getString(it)
                }
            })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                dataBinding.loading.visibility = View.GONE
                loginResult.error?.let {
                    showLoginFailed(it)
                }
                loginResult.success?.let {
                    updateUiWithUser(it)
                }
            })
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + model.userData.userId
        // TODO : initiate successful logged in experience
        val appContext = context?.applicationContext ?: return
        navigateToUserdetail(model)
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    private fun navigateToUserdetail(model: LoggedInUserView) {
        val direction =
            LoginFragmentDirections.actionLoginFragmentToDetailFragment(
                model.userData.userId,
                model.userData.token
            )
        findNavController().navigate(direction)
    }
}