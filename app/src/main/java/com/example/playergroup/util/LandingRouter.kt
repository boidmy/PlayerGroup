package com.example.playergroup.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.provider.Settings
import android.util.Log
import com.example.playergroup.PlayerGroupApplication
import com.example.playergroup.R
import com.example.playergroup.data.*
import com.example.playergroup.ui.board.boardDetail.BoardDetailActivity
import com.example.playergroup.ui.board.boardList.BoardListActivity
import com.example.playergroup.ui.base.BaseActivity
import com.example.playergroup.ui.club.ClubActivity
import com.example.playergroup.ui.club.create.CreateClubActivity
import com.example.playergroup.ui.dialog.adjust.AdjustBottomSheet
import com.example.playergroup.ui.dialog.dropout.DropOutBottomSheet
import com.example.playergroup.ui.login.InitLoginScreenActivity
import com.example.playergroup.ui.login.LoginType
import com.example.playergroup.ui.login.fragments.BottomSheetLoginFragment
import com.example.playergroup.ui.main.MainActivity
import com.example.playergroup.ui.mypage.MyPageActivity
import com.example.playergroup.ui.search.SearchActivity
import com.example.playergroup.ui.setting.SettingActivity
import com.example.playergroup.ui.dialog.themeselector.ThemeSelectorBottomSheet
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

object LandingRouter {

    private val pgApplication by lazy { PlayerGroupApplication.instance }

    fun move(context: Context, event: RouterEvent) {
        try {
            when (event.type) {
                Landing.MAIN -> gotoMain(context, event)
                Landing.START_LOGIN_SCREEN -> gotoStartLoginScreen(context, event)
                Landing.LOGIN -> goToLogin(context, event)
                Landing.LOGOUT -> goToLogOut(context, event)
                Landing.GOOGLE_LOGIN -> gotoGoogleLogin(context, event)
                Landing.MY_PAGE -> gotoMyPage(context, event)
                Landing.GALLERY -> checkPermission(context, event)
                Landing.CREATE_CLUB -> gotoCreateClub(context, event)
                Landing.CLUB_MAIN -> gotoClubMain(context, event)
                Landing.SEARCH -> gotoSearch(context, event)
                Landing.BOARD -> gotoBoardList(context)
                Landing.BOARD_DETAIL -> gotoBoardDetail(context, event)
                Landing.DROP_OUT -> goToDropOut(context, event)
                Landing.THEME_SELECTOR -> goToThemeSelector(context, event)
                Landing.SETTING -> goToSetting(context, event)
                Landing.APP_PERMISSION_SETTING -> gotoAppSettings(context, event)
                Landing.ADJUST_LIST -> goToAdjustList(context, event)
            }
        } catch (e: Exception) {
            Log.e("####", "${event.type} -> ${e.localizedMessage}")
        }
    }

    private fun goToAdjustList(context: Context, event: RouterEvent) {
        (context as? BaseActivity<*>)?.let { activity ->
            val newInstance = AdjustBottomSheet.newInstance {
                activity.onReload()
            }
            if (newInstance.isVisible) return
            newInstance.show(activity.supportFragmentManager, newInstance.tag)
        }
    }

    private fun gotoAppSettings(context: Context, event: RouterEvent) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri: Uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }

    private fun goToThemeSelector(context: Context, event: RouterEvent) {
        (context as? BaseActivity<*>)?.let { activity ->
            val newInstance = ThemeSelectorBottomSheet.newInstance()
            if (newInstance.isVisible) return
            newInstance.show(activity.supportFragmentManager, newInstance.tag)
        }
    }

    private fun goToLogOut(context: Context, event: RouterEvent) {
        FirebaseAuth.getInstance().signOut()
        if (event.paramBoolean) {
            RxBus.publish(LoginStateChange(FirebaseAuth.getInstance().currentUser != null))
        }
    }

    private fun goToSetting(context: Context, event: RouterEvent) {
        context.startActivity(Intent(context, SettingActivity::class.java)).also {
            //todo Parameter?
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
        if (pgApplication.isLogin()) {
            context.startActivity(Intent(context, CreateClubActivity::class.java).also {
                //todo Parameter?
            })
        } else {
            goToLogin(context, event.apply { paramInt = LoginType.LOGIN.value })
        }
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

    private fun gotoStartLoginScreen(context: Context, event: RouterEvent) {
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

    private fun goToLogin(context: Context, event: RouterEvent) {
        (context as? BaseActivity<*>)?.let { activity ->
            val newInstance = BottomSheetLoginFragment.newInstance(event.paramInt) {
                RxBus.publish(LoginStateChange(FirebaseAuth.getInstance().currentUser != null))
            }
            if (newInstance.isVisible) return
            newInstance.show(activity.supportFragmentManager, newInstance.tag)
        }
    }

    private fun goToDropOut(context: Context, event: RouterEvent) {
        (context as? BaseActivity<*>)?.let { activity ->
            val newInstance = DropOutBottomSheet.newInstance {
                FirebaseAuth.getInstance().signOut()
                RxBus.publish(LoginStateChange(FirebaseAuth.getInstance().currentUser != null))
            }
            if (newInstance.isVisible) return
            newInstance.show(activity.supportFragmentManager, newInstance.tag)
        }
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
        if (pgApplication.isLogin()) {
            context.startActivity(Intent(context, MyPageActivity::class.java).also { intent ->
                intent.putExtra(INTENT_EXTRA_PARAM, event.paramBoolean)
            })
        } else {
            goToLogin(context, event.apply { paramInt = LoginType.LOGIN.value })
        }
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

    private fun gotoBoardDetail(context: Context, event: RouterEvent) {
        Intent(context, BoardDetailActivity::class.java).apply {
            putExtra(INTENT_BUNDLE, event.bundle)
        }.run {
            context.startActivity(this)
        }
    }
}