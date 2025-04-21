package com.app.gmao_machines.ui.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.app.gmao_machines.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun signOut(context: Context, onSignOutComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                // Sign out from Firebase
                auth.signOut()

                // Sign out from Google
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(R.string.default_web_client_id.toString()) // This should match your Firebase config
                    .requestEmail()
                    .build()

                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                googleSignInClient.signOut().await()

                onSignOutComplete()
            } catch (e: Exception) {
                // Handle sign out error if needed
                e.printStackTrace()
                onSignOutComplete()
            }
        }
    }
} 