package com.app.structure.ui.login

import android.app.Activity
import android.content.Intent
import com.app.structure.R
import com.app.structure.databinding.ActivityLoginBinding
import com.app.structure.extension.startActivityWithAnimation
import com.app.structure.shareddata.base.BaseActivity

class LoginActivity: BaseActivity<ActivityLoginBinding>() {

    override fun getResource() = R.layout.activity_login

    companion object {
        fun start(activity: Activity) {
            var intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun initView() {
    }

    override fun initObserver() {
    }

    override fun handleListener() {
    }

    override fun displayMessage(message: String) {
    }
}