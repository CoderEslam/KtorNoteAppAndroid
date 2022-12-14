package com.samarth.ktornoteapp.ui.account

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.samarth.ktornoteapp.R
import com.samarth.ktornoteapp.databinding.FragmentUserInfoBinding
import com.samarth.ktornoteapp.utils.MyResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created By Eslam Ghazy on 8/6/2022
 */

@AndroidEntryPoint
class UserInfoFragment : Fragment(R.layout.fragment_user_info) {

    private var _binding: FragmentUserInfoBinding? = null
    val binding: FragmentUserInfoBinding?
        get() = _binding


    /*
    * get it by activityViewModels
    * -> because we want single instance of this view model in all the fragment on our single activity
    * */
    private val userViewModel: UserViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUserInfoBinding.bind(view)


        binding?.createAccountBtn?.setOnClickListener {
            findNavController().navigate(R.id.action_userInfoFragment_to_createAccountFragment)
        }

        binding?.loginBtn?.setOnClickListener {
            findNavController().navigate(R.id.action_userInfoFragment_to_loginFragment)
        }

        binding?.logoutBtn?.setOnClickListener {
            userViewModel.logout()
        }
        subscribeToCurrentUserEvents()

    }

    override fun onStart() {
        super.onStart()
        userViewModel.getCurrentUser()
    }


    private fun subscribeToCurrentUserEvents() = lifecycleScope.launch {
        userViewModel.currentUserState.collect { MyResult ->
            when (MyResult) {
                is MyResult.Success -> {
                    userLoggedIn()
                    binding?.userTxt?.text = MyResult.data?.name ?: "NO Name"
                    binding?.userEmail?.text = MyResult.data?.email ?: "No Email"
                }
                is MyResult.Error -> {
                    userNotLoggedIn()
                    binding?.userTxt?.text = "Not Logged In!"
                }
                is MyResult.Loading -> {
                    binding?.userProgressBar?.isVisible = true
                }
            }

        }

    }


    private fun userLoggedIn() {
        binding?.userProgressBar?.isVisible = false
        binding?.loginBtn?.isVisible = false
        binding?.createAccountBtn?.isVisible = false
        binding?.logoutBtn?.isVisible = true
        binding?.userEmail?.isVisible = true
    }

    private fun userNotLoggedIn() {
        binding?.userProgressBar?.isVisible = false
        binding?.loginBtn?.isVisible = true
        binding?.createAccountBtn?.isVisible = true
        binding?.logoutBtn?.isVisible = false
        binding?.userEmail?.isVisible = false
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}