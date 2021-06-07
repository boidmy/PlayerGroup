package com.example.playergroup.util

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.playergroup.ui.main.MainActivity
import com.example.playergroup.R
import com.example.playergroup.data.Landing
import com.example.playergroup.data.RouterEvent
import com.example.playergroup.ui.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

object LandingRouter {

    fun move(context: Context, event: RouterEvent) {
        try {
            when (event.type) {
                Landing.MAIN -> gotoMain(context, event)
                Landing.LOGIN -> gotoLogin(context, event)
                Landing.GOOGLE_LOGIN -> gotoGoogleLogin(context, event)
            }
        } catch (e: Exception) {
            Log.e("####", "${event.type} -> ${e.localizedMessage}")
        }
    }

    private fun gotoMain(context: Context, event: RouterEvent) {
        context.startActivity(Intent(context, MainActivity::class.java).also { intent ->
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        //(context as? AppCompatActivity)?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun gotoLogin(context: Context, event: RouterEvent) {
        context.startActivity(Intent(context, LoginActivity::class.java))
    }

    private fun gotoGoogleLogin(context: Context, event: RouterEvent) {
        val mGoogleSignInClient = GoogleSignIn.getClient(context,
            GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )
        event.activityResult?.launch(mGoogleSignInClient.signInIntent)
    }
}