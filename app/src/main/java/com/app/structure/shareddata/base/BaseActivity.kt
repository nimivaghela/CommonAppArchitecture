package com.app.structure.shareddata.base

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.app.structure.R
import com.app.structure.extension.hideKeyboard
import com.app.structure.extension.resToast
import com.app.structure.extension.snackWithColor
import com.app.structure.model.UserHolder
import com.app.structure.ui.login.LoginActivity
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.SocketInternetObservingStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import org.jetbrains.annotations.NotNull
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.regex.Pattern


abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity(), BaseView {
    @LayoutRes
    abstract fun getResource(): Int

    open lateinit var mDisposable: CompositeDisposable
    lateinit var mBinding: VB
    abstract fun initView()
    abstract fun initObserver()
    abstract fun handleListener()
    abstract fun displayMessage(message: String)

    var isInternetConnected: Boolean = true
    val mUserHolder: UserHolder by inject()

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDisposable()
        initConnectivity()
        setView(getResource())
    }

    private fun setView(@LayoutRes layoutId: Int) {
        try {
            mBinding = DataBindingUtil.setContentView(this, layoutId)

            initView()
            initObserver()
            handleListener()
        } catch (e: Exception) {
            resToast(e.localizedMessage)
        }
    }

    private lateinit var mToolbar: Toolbar

    @SuppressLint("RestrictedApi")
    protected fun setToolbar(
        @NotNull toolbar: Toolbar, @NotNull title: String,
        isBackEnabled: Boolean = false, isLeft1Enabled: Boolean = false,
        isLeft2Enabled: Boolean = false, backgroundColor: Int = R.color.transparent
    ) {
        this.mToolbar = toolbar
        super.setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(ContextCompat.getColor(this, backgroundColor))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        /*
        * Add your toolbar here
        * */
    }

    fun setNavigationIcon(navigationIconResId: Int) {
        if (::mToolbar.isInitialized) {
            mToolbar.setNavigationIcon(navigationIconResId)
        }
    }

    private fun initConnectivity() {
        val settings = InternetObservingSettings.builder()
            .host("www.google.com")
            .strategy(SocketInternetObservingStrategy())
            .interval(3000)
            .build()

        ReactiveNetwork
            .observeInternetConnectivity(settings)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isConnectedToHost ->
                isInternetConnected = isConnectedToHost
            }.addTo(mDisposable)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun initDisposable() {
        mDisposable = CompositeDisposable()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposable.clear()
        mDisposable.dispose()
    }

    fun changeFragment(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        layout: Int,
        addToBackStack: Boolean = false
    ) {
        fragmentManager.beginTransaction().replace(layout, fragment, fragment::class.java.name)
            .commit()
    }

    fun checkFragmentVisible(fragmentManager: FragmentManager, fragmentName: String): Boolean {
        return fragmentManager.findFragmentByTag(fragmentName)?.isVisible ?: false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        hideKeyboard()
    }

    override fun onUnknownError(error: String?) {
        error?.let {
            Timber.d("Base Activity Unknown error $error")
            displayErrorMessage(error)
        }
        generalErrorAction()
    }

    override fun internalServer() {
        Timber.d("Base Activity API Internal server")
        displayErrorMessage(getString(R.string.text_error_internal_server))
        generalErrorAction()
    }

    override fun onTimeout() {
        Timber.d("Base Activity API Timeout")
        displayErrorMessage(getString(R.string.text_error_timeout))
        generalErrorAction()
    }

    override fun onNetworkError() {
        Timber.d("Base Activity network error")
        displayErrorMessage(getString(R.string.text_error_network))
        generalErrorAction()
    }

    override fun onConnectionError() {
        Timber.d("Base Activity internet issue")
        displayErrorMessage(getString(R.string.text_error_connection))
        generalErrorAction()
    }

    override fun onServerDown() {
        Timber.d("Base Activity Server Connection issue")
        displayErrorMessage(getString(R.string.text_server_connection))
        generalErrorAction()
    }

    override fun generalErrorAction() {
        Timber.d("This method will use in child class for performing common task for all error")
    }

    //this method needs to be call only when you get response from the logout API
    fun autoLogout() {
        mUserHolder.clear()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        )
        startActivity(intent)
    }

    fun showLoadingIndicator(progressBar: View, isShow: Boolean) {
        progressBar.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    /**
     * validates website using regex
     */
    fun isWebsiteInvalid(website: String): Boolean {
        val regex =
            "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(website)
        return !matcher.matches()
    }

    private fun displayErrorMessage(message: String) {
        mBinding.root.snackWithColor(message)
    }
}