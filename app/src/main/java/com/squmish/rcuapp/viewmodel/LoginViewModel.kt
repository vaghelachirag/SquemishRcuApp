package com.squmish.rcuapp.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.squmish.rcuapp.uttils.Utils
import com.squmish.rcuapp.BuildConfig
import com.squmish.rcuapp.view.menu.DashboardActivity
import com.squmish.rcuapp.R
import com.squmish.rcuapp.databinding.LoginscreenBinding
import com.squmish.rcuapp.model.LoginModel
import com.squmish.rcuapp.model.base.BaseViewModel
import com.squmish.rcuapp.model.login.GetLoginResponseModel
import com.squmish.rcuapp.network.CallbackObserver
import com.squmish.rcuapp.network.Networking
import com.squmish.rcuapp.uttils.Session
import com.squmish.rcuapp.uttils.Utility
import com.squmish.rcuapp.view.LoginFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LoginViewModel(
    private val context: Context,
    private val loginFragment: LoginFragment,
    private val binding: LoginscreenBinding
) : BaseViewModel(){

    // Login Params
     var email : ObservableField<String> = ObservableField()
     var password : ObservableField<String> = ObservableField()
     private var confirmPassword : ObservableField<String> = ObservableField()
     private var signInMutableLiveData: MutableLiveData<LoginModel> = MutableLiveData()

    fun onSignInClicked() {
        val model = LoginModel()
        model.email = email.get()
        model.password = password.get()
        model.confirmPassword = confirmPassword.get()

        signInMutableLiveData.value = model

        if (model.email == null){
            // Utils().showToast(context,"Please Enter Email Address")
             Utils().showSnackBar(context,"Please Enter Employee Code",binding.constraintLayout)
        }
        else if (model.password == null ){
            //Utils().showToast(context,"Please Enter Your Password")
            Utils().showSnackBar(context,"Please Enter Your Password",binding.constraintLayout)
        }
        else{
            var session = Session(context)

            if (binding.chkRememberPassword.isChecked) {
                session.storeDataByKey(Session.KEY_USER_EMAIL, binding.edtEmailAddress.text.toString(),)
                session.storeDataByKey(Session.KEY_USER_PASSWORD, binding.edtPassword.text.toString(),)
                session.storeDataByKey(Session.KEY_USER_REMEMBER, binding.chkRememberPassword.isChecked,)

            } else {
                session.storeDataByKey(Session.KEY_USER_EMAIL, "")
                session.storeDataByKey(Session.KEY_USER_PASSWORD, "")
                session.storeDataByKey(Session.KEY_USER_REMEMBER, false)
                session.storeDataByKey(Session.KEY_USER_NAME,"")
            }

            callLoginAPI()
        }
    }

    @SuppressLint("HardwareIds")
    private fun callLoginAPI() {
        var session = Session(context)

        Log.e("Token", session.getStoreTokenByKey().toString())
        val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val manufacturer = Build.MANUFACTURER
        val androidModel = Build.MODEL
        val deviceToken = session.getStoreTokenByKey().toString()


        val params = HashMap<String,Any>()
        params["EmployeeCode"] = email.get().toString()
        params["Password"] = password.get().toString()
        params["AppId"] = "BCF97D6D0DB4C5E83107TR11"
        params["DeviceId"] = deviceId
        params["firebasetoken"] = deviceToken
        params["AppVersion"] = BuildConfig.VERSION_NAME.toString()

        if (Utility.isNetworkConnected(context)){
            isLoading.postValue(true)
            Networking.with(context)
                .getServices()
                .login(Networking.wrapParams(params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<GetLoginResponseModel>() {
                    override fun onSuccess(response: GetLoginResponseModel) {
                        isLoading.postValue(false)
                        redirectToHome()
                    }

                    override fun onFailed(code: Int, message: String) {
                        isLoading.postValue(false)
                    }

                    override fun onNext(t: GetLoginResponseModel) {
                        Log.e("Status",t.getStatusCode().toString())
                        isLoading.postValue(false)
                        if(t.getStatusCode() == 200){
                            var session = Session(context)
                            session.isLoggedIn = true
                            session.user = t.getData()
                            t.getData()!!.getProfilePicture()?.let { session.storeUserProfileImageKey(it) }
                            t.getData()!!.getName()?.let { session.storeUserNameKey(it) }
                            redirectToHome()
                        }else{
                          //  Utils().showToast(context,t.getMessage().toString())
                            Utils().showSnackBar(context,t.getMessage().toString(),binding.constraintLayout)
                        }
                        Log.e("StatusCode",t.getStatus().toString())
                    }

                })
        }else{
            Utils().showToast(context,context.getString(R.string.nointernetconnection).toString())
        }
    }


    /** Returns the consumer friendly device name  */
    private fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else capitalize(manufacturer) + " " + model
    }

    private fun capitalize(str: String): String {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        val arr = str.toCharArray()
        var capitalizeNext = true
        val phrase = StringBuilder()
        for (c in arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c))
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase.append(c)
        }
        return phrase.toString()
    }
    fun redirectToSignup(){
        loginFragment.findNavController().navigate(R.id.action_LoginFragment_to_SignUpFragment)
    }

    private fun redirectToHome(){
      //  loginFragment.findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)
        var intentLogin = Intent(context, DashboardActivity::class.java)
        context.startActivity(intentLogin)
    }

}