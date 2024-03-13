package com.app.structure.shareddata.repo

import com.app.structure.model.UserHolder
import com.app.structure.shareddata.endpoint.ApiEndPoint


/*
* implement all your userRepo methods here
* */
class UserRepository(
    private val mApiEndPoint: ApiEndPoint,
    private val mUserHolder: UserHolder
) : UserRepo