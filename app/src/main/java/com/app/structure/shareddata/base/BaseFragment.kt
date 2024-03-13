package com.app.structure.shareddata.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.app.structure.R
import com.app.structure.extension.snackWithColor
import com.app.structure.model.UserHolder
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


abstract class BaseFragment<VB : ViewDataBinding> : Fragment(), BaseView {

    /**
     * to get Fragment resource file
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * to set fragment option menu
     */
    protected open fun hasOptionMenu(): Boolean = false

    /**
     * to display error message
     */
    abstract fun displayMessage(message: String)

    /**
     * to initialize variables
     */
    abstract fun initView()

    /**
     * to call API or bind adapter
     */
    abstract fun postInit()

    /**
     * initialize live data observer
     */
    abstract fun initObserver()

    /**
     * to define all listener
     */
    abstract fun handleListener()

    lateinit var mDisposable: CompositeDisposable
    lateinit var mBinding: VB
    var isInternetConnected: Boolean = true
    val mUserHolder: UserHolder by inject()


    override fun onDestroyView() {
        super.onDestroyView()
        mDisposable.clear()
        mDisposable.dispose()
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDisposable = CompositeDisposable()
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        setHasOptionsMenu(hasOptionMenu())
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        mDisposable = CompositeDisposable()
        initConnectivity()
        initView()
        initObserver()
        handleListener()
        postInit()
    }

    private lateinit var mToolbar: Toolbar

    @SuppressLint("RestrictedApi")
    protected fun setToolbar(
        @NotNull toolbar: Toolbar, @NotNull title: String,
        isBackEnabled: Boolean = false, isLeft1Enabled: Boolean = false,
        isLeft2Enabled: Boolean = false, backgroundColor: Int = R.color.transparent
    ) {
        this.mToolbar = toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(
            ContextCompat.getColor(
                (activity as AppCompatActivity).applicationContext,
                backgroundColor
            )
        )
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

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


    override fun onUnknownError(error: String?) {
        error?.let {
            Timber.d("Base Activity $error")
            displayMessage(error)
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

    override fun generalErrorAction() {
        Timber.d("This method will use in child class for performing common task for all error")
    }

    override fun onServerDown() {
        Timber.d("Base Activity Server Connection issue")
        displayErrorMessage(getString(R.string.text_server_connection))
        generalErrorAction()
    }

    //this method needs to be call only when you get response from the logout API
    fun autoLogout() {
        (activity as BaseActivity<*>).autoLogout()
    }

    fun showLoadingIndicator(progressBar: View, isShow: Boolean) {
        progressBar.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    private fun displayErrorMessage(message: String) {
        mBinding.root.snackWithColor(message)
    }
}