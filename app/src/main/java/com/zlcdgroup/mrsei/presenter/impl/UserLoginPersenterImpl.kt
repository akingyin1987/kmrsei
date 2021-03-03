package com.zlcdgroup.mrsei.presenter.impl


import android.hardware.biometrics.BiometricPrompt
import android.os.CancellationSignal
import com.akingyin.base.BasePresenter
import com.akingyin.base.ext.spGetString
import com.akingyin.base.ext.spSetString
import com.akingyin.base.taskmanager.ApiTaskCallBack
import com.akingyin.base.taskmanager.MultiTaskManager
import com.akingyin.base.taskmanager.enums.TaskManagerStatusEnum
import com.akingyin.base.taskmanager.enums.ThreadTypeEnum
import com.akingyin.base.utils.MainExecutor
import com.zlcdgroup.mrsei.data.entity.PersonEntity
import com.zlcdgroup.mrsei.data.source.PersonRepository
import com.zlcdgroup.mrsei.presenter.UserLoginContract
import com.zlcdgroup.mrsei.task.TestTask
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/10/8 14:38
 * @version V1.0
 */
class UserLoginPersenterImpl @Inject constructor(var personRepository: PersonRepository): BasePresenter<UserLoginContract.View>(), UserLoginContract.Presenter {



    override fun initialization() {
        val taskManager = MultiTaskManager.createPool(2)
         for ( i in 1..5000){
             taskManager.addTask(TestTask(i.toString()))
         }
         taskManager.threadTypeEnum = ThreadTypeEnum.MainThread
         taskManager.callBack = object : ApiTaskCallBack{
             override fun onCallBack(total: Int, progress: Int, error: Int) {
                 println("onCallBack=$total  progress=$progress  error=$error")
             }

             override fun onComplete() {
             }

             override fun onError(message: String, statusEnum: TaskManagerStatusEnum) {

             }


         }
       // taskManager.executeTask()
        // mRootView?.setAppTheme(getTheme())
    }

//    @Inject
//    lateinit var okHttpClient: OkHttpClient
//
//    @Inject
//    lateinit var retrofit : Retrofit

    override fun getListPersons(): List<PersonEntity> {
        return  personRepository.getAllPersons()
    }

    override fun delectOutTowMothsPersons() {
        personRepository.delectOutTowMonth()
    }

    override fun getLastPerson(): PersonEntity? {
        return  personRepository.getLastPerson()
    }

    override fun login(name: String, pass: String) {



        if(name.isEmpty()){
            mRootView!!.showError("用户名不可为空！")
            return
        }
        if(pass.isEmpty()){
            mRootView!!.showError("密码不可为空！")
            return
        }
      //  mRootView?.showLoadDialog("登录中..")
        mRootView?.goToMainActivity()
//       personRepository.login(name,pass, object : ApiCallBack<LoginResultModel> {
//           override fun call(resultModel: LoginResultModel) {
//               mRootView?.hideLoadDialog()
//               mRootView?.goToMainActivity()
//           }
//
//           override fun onError(msg: String) {
//                mRootView?.let {
//                    it.hideLoadDialog()
//                    it.showError(msg)
//                    it.goToMainActivity()
//                }
//
//
//
//           }
//       })
    }

    override fun getTheme(): String {
        return  spGetString("themePref")
    }

    override fun saveAppTheme(theme: String) {
        spSetString("themePref",theme)
        mRootView?.setAppTheme(theme)
    }

    override fun cancelSubscribe() {
        personRepository.cancelSubscribe()
    }

    override fun fingerprintLogin(callBack: (result: Boolean, error: String?) -> Unit) {
        val cancelSignal = CancellationSignal().apply {
            setOnCancelListener {
                println("取消验证")
            }
        }
        val authCallback = object : BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                println("errorCode=$errorCode,errString=$errString")
                callBack.invoke(false,errString?.toString())
            }

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                super.onAuthenticationHelp(helpCode, helpString)
                callBack.invoke(false,helpString?.toString())
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                callBack.invoke(true,"")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                callBack.invoke(false,"")
            }
        }
        mRootView?.showFingerprintDialog()?.authenticate(cancelSignal,MainExecutor(),authCallback)
    }
}