package com.app.structure.shareddata.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.app.structure.R
import com.app.structure.extension.getWidthOfScreen
import com.app.structure.extension.snackWithColor
import com.app.structure.model.UserHolder
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.SocketInternetObservingStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import timber.log.Timber

abstract class BaseDialogFragment<VB : ViewDataBinding> : DialogFragment(), BaseView {

    /**
     * layout resource file
     */
    abstract fun getResource(): Int

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

    /**
     * to display error message
     */
    abstract fun displayMessage(message: String)

    var isInternetConnected: Boolean = true
    lateinit var mBinding: VB
    lateinit var mDisposable: CompositeDisposable
    val mUserHolder: UserHolder by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDisposable = CompositeDisposable()
        initConnectivity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposable.dispose()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        mBinding = DataBindingUtil
            .inflate(LayoutInflater.from(context), getResource(), null, false)
        dialog.setContentView(mBinding.root)
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        postInit()
        initObserver()
        handleListener()
    }

    override fun onResume() {
        super.onResume()
        /**
         * setup the width of dialog programmatically
         */
        val params = dialog?.window?.attributes
        params?.let { dialogParams ->
            context?.getWidthOfScreen()?.let {
                dialogParams.width = (it * 0.95).toInt()
                dialog?.window?.attributes = params
            }
        }
        /**
         * Prevent dialog dismiss on outside click
         */
        isCancelable = false
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
        displayMessage(getString(R.string.text_error_network))
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

    private fun displayErrorMessage(message: String) {
        mBinding.root.snackWithColor(message)
    }
}