package com.app.structure.model

import android.content.SharedPreferences
import com.app.structure.extension.prefBoolean
import com.app.structure.extension.prefInt
import com.app.structure.extension.prefString


class UserHolder(preference: SharedPreferences) {
    var mAuthToken by preference.prefString("")
    var mFirstName by preference.prefString("")
    var mLastName by preference.prefString("")
    var mEmail by preference.prefString("")
    var mPhoneNumber by preference.prefString("")
    var mCountryCode by preference.prefString("")
    var mUserId by preference.prefString("")
    var isEmailVerified by preference.prefBoolean(false)
    var isMobileVerified by preference.prefBoolean(false)
    var mCompleteOnBoardingScreenNum by preference.prefInt(0)
    var connectedDeviceList by preference.prefString("")
    var notificationToken by preference.prefString("")

    fun setUserData(
        firstName: String,
        lastName: String,
        authToken: String,
        email: String,
        phoneNumber: String,
        countryCode: String,
        userId: String,
        isVerified: Boolean,
        isMobileVerified: Boolean,
        completedOnbordingScreen: Int
    ) {
        this.mFirstName = firstName
        this.mLastName = lastName
        this.mAuthToken = authToken
        this.mEmail = email
        this.mPhoneNumber = phoneNumber
        this.mCountryCode = countryCode
        this.mUserId = userId
        this.isEmailVerified = isVerified
        this.isMobileVerified = isMobileVerified
        this.mCompleteOnBoardingScreenNum = completedOnbordingScreen
    }

    fun setVerificationStatus(isVerified: Boolean) {
        this.isEmailVerified = isVerified
    }

    fun setMobileVerificationStatus(isVerified: Boolean) {
        this.isMobileVerified = isVerified
    }

    fun setDeviceToken(token: String) {
        this.notificationToken = token
    }

    fun setEmail(email: String) {
        this.mEmail = email
    }

    fun updateOnBoarding(completedOnbordingScreen: Int) {
        this.mCompleteOnBoardingScreenNum = completedOnbordingScreen
    }

    fun clear() {
        this.mFirstName = ""
        this.mLastName = ""
        this.mAuthToken = ""
        this.mEmail = ""
        this.mPhoneNumber = ""
        this.mCountryCode = ""
        this.mUserId = ""
        this.isEmailVerified = false
        this.isMobileVerified = false
        this.mCompleteOnBoardingScreenNum = 0
        this.connectedDeviceList = ""
    }
}