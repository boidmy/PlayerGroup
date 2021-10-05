package com.example.playergroup.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.util.Log
import com.example.playergroup.ui.main.MainActivity
import com.example.playergroup.R
import com.example.playergroup.data.*
import com.example.playergroup.ui.board.boardList.BoardListActivity
import com.example.playergroup.ui.club.ClubActivity
import com.example.playergroup.ui.club.create.CreateClubActivity
import com.example.playergroup.ui.login.InitLoginScreenActivity
import com.example.playergroup.ui.mypage.MyPageActivity
import com.example.playergroup.ui.search.SearchActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

object LandingRouter {
    fun move(context: Context, event: RouterEvent) {
        try {
            when (event.type) {
                Landing.MAIN -> gotoMain(context, event)
                Landing.LOGIN -> gotoLogin(context, event)
                Landing.GOOGLE_LOGIN -> gotoGoogleLogin(context, event)
                Landing.MY_PAGE -> gotoMyPage(context, event)
                Landing.GALLERY -> checkPermission(context, event)
                Landing.CREATE_CLUB -> gotoCreateClub(context, event)
                Landing.CLUB_MAIN -> gotoClubMain(context, event)
                Landing.SEARCH -> gotoSearch(context, event)
                Landing.NOTICEBOARD -> gotoBoardList(context)
            }
        } catch (e: Exception) {
            Log.e("####", "${event.type} -> ${e.localizedMessage}")
        }
    }

    private fun gotoSearch(context: Context, event: RouterEvent) {
        context.startActivity(Intent(context, SearchActivity::class.java).also {
            //todo Parameter?
        })
    }

    private fun gotoClubMain(context: Context, event: RouterEvent) {
        context.startActivity(Intent(context, ClubActivity::class.java).also { intent ->
            intent.putExtra(INTENT_EXTRA_STRING_PARAM, event.paramString)
            intent.putExtra(INTENT_EXTRA_URI_TO_STRING_PARAM, event.paramUriToString)
            intent.putExtra(INTENT_EXTRA_PRIMARY_KEY, event.primaryKey)
        }, event.options?.toBundle())
    }

    private fun gotoCreateClub(context: Context, event: RouterEvent) {
        context.startActivity(Intent(context, CreateClubActivity::class.java).also {
            //todo Parameter?
        })
    }

    private fun gotoMain(context: Context, event: RouterEvent) {
        context.startActivity(Intent(context, MainActivity::class.java).also { intent ->
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK)
            })
    }

    private fun gotoLogin(context: Context, event: RouterEvent) {
        context.startActivity(Intent(context, InitLoginScreenActivity::class.java).also { intent ->
            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK)
        })
    }

    private fun gotoBoardList(context: Context) {
        context.startActivity(Intent(context, BoardListActivity::class.java))
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

    private fun gotoMyPage(context: Context, event: RouterEvent) {
        context.startActivity(Intent(context, MyPageActivity::class.java).also { intent ->
            intent.putExtra(INTENT_EXTRA_PARAM, event.paramBoolean)
        })
    }

    private fun gotoGallery(context: Context, event: RouterEvent) {
        val intent = Intent(Intent.ACTION_PICK).also {
            it.setDataAndType(EXTERNAL_CONTENT_URI, "image/*")
        }
        event.activityResult?.launch(intent)
    }

    private fun checkPermission(context: Context, event: RouterEvent) {
        TedPermission.with(context)
            .setPermissions(*setPermission(event.type))
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() { gotoTypeMove(context, event) }
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Log.d("####","${setPermission(event.type)} onPermissionDenied")
                }
            })
            .check()
    }

    private fun setPermission(type: Landing): Array<String> {
        return arrayOf(
            when (type) {
                Landing.GALLERY -> Manifest.permission.READ_EXTERNAL_STORAGE
                else -> ""
            }
        )
    }

    private fun gotoTypeMove(context: Context, event: RouterEvent) {
        when (event.type) {
            Landing.GALLERY -> gotoGallery(context, event)
        }
    }
}