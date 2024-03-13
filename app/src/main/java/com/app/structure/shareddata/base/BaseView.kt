package com.app.structure.shareddata.base

interface BaseView {

    fun internalServer()

    fun onUnknownError(error: String?)

    fun onTimeout()

    fun onNetworkError()

    fun onConnectionError()

    fun generalErrorAction()

    fun onServerDown()
}