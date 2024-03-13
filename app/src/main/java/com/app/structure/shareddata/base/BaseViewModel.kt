package com.app.structure.shareddata.base

import androidx.lifecycle.ViewModel
import com.app.structure.shareddata.repo.UserRepo

/*
* add all common api calls here,
* add logout API call here
* */
class BaseViewModel(private val mUserRepository: UserRepo) : ViewModel() {
}