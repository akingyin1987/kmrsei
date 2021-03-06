package com.zlcdgroup.mrsei.ui

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import autodispose2.ScopeProvider
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import autodispose2.autoDispose
import com.akingyin.base.BaseDaggerActivity
import com.akingyin.base.dialog.MaterialDialogUtil
import com.akingyin.base.ext.*
import com.akingyin.base.utils.DateUtil
import com.akingyin.base.utils.FileUtils
import com.akingyin.base.utils.StringUtils
import com.akingyin.tuya.BaseTuYaActivity
import com.alibaba.fastjson.JSONObject
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.utils.AreaUtil
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.db.dao.UserEntityDao
import com.zlcdgroup.mrsei.databinding.ActivityLoginBinding
import com.zlcdgroup.mrsei.di.module.ViewModelModule
import com.zlcdgroup.mrsei.presenter.UserLoginContract
import com.zlcdgroup.mrsei.presenter.impl.UserLoginPersenterImpl
import com.zlcdgroup.mrsei.ui.mvvm.LoginViewModel
import com.zlcdgroup.mrsei.utils.ThemeHelper
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import permissions.dispatcher.ktx.constructPermissionsRequest
import java.io.File
import java.nio.charset.Charset
import java.util.*
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/10/8 12:16
 * @version V1.0
 */

@AndroidEntryPoint
class LoginActivity  : BaseDaggerActivity() ,UserLoginContract.View{

    @Inject
    lateinit var userLoginPersenterImpl: UserLoginPersenterImpl

    //注释模式
    @Inject
    lateinit var  viewModel :LoginViewModel

    @Inject
    lateinit var  userEntityDao: UserEntityDao



     // 默认创建工厂为无参构造函数
    //ViewModelProvider.Factory getDefaultViewModelProviderFactory()
   //  val  viewModel2 :LoginViewModel by  viewModels()

    //自定义 工厂模式
     val viewModel3 : LoginViewModel by viewModels {
         ViewModelModule.LoginViewModelFactory(userEntityDao)
     }

    lateinit var  bindView:ActivityLoginBinding

    override fun getLayoutId(): Int = R.layout.activity_login


    override fun useViewBind()=true

    override fun initViewBind() {
        super.initViewBind()
        bindView = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bindView.root)
    }

    override fun initializationData(savedInstanceState: Bundle?) {
        userLoginPersenterImpl.attachView(this)
    }


    override fun onSaveInstanceData(outState: Bundle?) {
    }

    @ObsoleteCoroutinesApi
    override fun initView() {

          viewModel.Login("123","456")

        val person = userLoginPersenterImpl.getLastPerson()
        person?.let {

            bindView.etMobile.setText(it.personAccount.isEmptyOrNull())
            bindView.etPassword.setText(it.personPassword.isEmptyOrNull())
        }
        bindView.etPassword.setOnEditorActionListener { _, actionId, _ ->
           if(actionId == EditorInfo.IME_ACTION_GO){
               userLoginPersenterImpl.login(bindView.etMobile.text.toString(),bindView.etPassword.text.toString())

               return@setOnEditorActionListener  true
           }
           return@setOnEditorActionListener false
       }
        println("btn_login2")

        bindView.btnLogin.click {
            testCameraAuth()

            userLoginPersenterImpl.login(bindView.etMobile.text.toString(),bindView.etPassword.text.toString())
        }



        load {
            println("--------1111----------${Thread.currentThread().name}")
            delay(200)


            AreaUtil.calculateArea(mutableListOf<LatLng>().apply {
                add(LatLng(113.601164,24.829919))
                add(LatLng(113.609397,24.838277))
                add(LatLng(113.614567,24.838638))
                add(LatLng(113.615178,24.84136))
                add(LatLng(113.62055,24.843893))
                add(LatLng(113.621942,24.837466))
                add(LatLng(113.621448,24.836076))
            })
        }.then {
            println(it)
            println(Thread.currentThread().name)

        }
        println("222222222${Thread.currentThread().name}")
        var  day_time = 0L

        bindView.datePickerActions.click {
            TimePickerDialog.newInstance({ view, hourOfDay, minute, second ->
                 val  calendar  = Calendar.getInstance()
                 calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
                 calendar.set(Calendar.MINUTE,minute)
                 calendar.set(Calendar.SECOND,second)
                  day_time = calendar.time.time
                bindView.datePickerActions.text = DateUtil.millis2String(day_time)
            },true).show(supportFragmentManager,"time-fragment")

        }

        bindView.btnCreateCode.click {
            val  keywork = bindView.editKeywork.text.toString()
            if(keywork.isEmpty() || day_time<= 0){
                showError("数据不正确")
                return@click
            }
            val json = JSONObject().apply {
                put("keyWord",keywork)
                put("overdueTime",day_time)
            }
            val  data = Base64.encode(json.toJSONString().toByteArray(),Base64.DEFAULT).toString(Charset.forName("utf-8"))
            MaterialDialogUtil.showConfigDialog( this,message = data,callback = {
                   if(it){
                     clipboardManager.setPrimaryClip(ClipData.newPlainText("验证码",data))
                     showSucces("已复制到剪切版里")
                   }
            })
        }

        bindView.fingerprint.clickDelay {
            userLoginPersenterImpl.fingerprintLogin { result, error ->
                println("result=$result,error=$error")
                if(!result){
                    showError(error)
                }else{
                    goToMainActivity()
                }
            }
        }
    }

    var  d1 :Disposable? = null
    var  d2 :Disposable? = null
    var  d3 :Disposable? = null
    override fun startRequest() {
     d1 = Completable.create {
            it.onComplete()
      }.autoDispose(Completable.complete()).subscribe {

      }
     d2 = Observable.just("1").autoDispose(ScopeProvider.UNBOUND)
              .subscribe {
                  println("it1=${it}")
              }
      d3 =  Observable.just("2").autoDispose(AndroidLifecycleScopeProvider.from(lifecycle))
                .subscribe {
                    println("it=${it}")
                }

        println("d1=${d1?.isDisposed}   d2=${d2?.isDisposed}   d3=${d3?.isDisposed}")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("ondestory ->d1=${d1?.isDisposed}   d2=${d2?.isDisposed}   d3=${d3?.isDisposed}")
    }

    override fun dismissLoading() {
        super.dismissLoading()
        userLoginPersenterImpl.cancelSubscribe()
    }

    override fun showConfigDialog(message: String) {
        MaterialDialogUtil.showConfigDialog(context = this,message = message) {
            aBoolean -> if(aBoolean){

        }

        }
    }

   private fun    testCameraAuth()=constructPermissionsRequest(Manifest.permission.CAMERA,
    onShowRationale = {
        it.proceed()
        println("onShowRationale")
    },onPermissionDenied = {
        println("onPermissionDenied")
    },onNeverAskAgain = {
       println("onNeverAskAgain")
    }){
        println("权限认证通过")
    }
    private fun onCameraNeverAskAgain() {

       showTips("不再询问")
    }
    override fun goToMainActivity() {
        println("goToMainActivity")
        constructPermissionsRequest(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION){
            println("goToMainActivity2")
//        ARouter.getInstance().build("/user/list").withString("name","nametest")
//                .withInt("age",2).navigation()
            //  goActivity<UserListActivity>()
            //   goActivity<CoroutinesDemo>()
            // goActivity<CameraXActivity>()
            var  localPath = mContext?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.apply {
                println("abs$absolutePath")
            }?.absolutePath+File.separator+StringUtils.getUUID()+".jpg"
            // startActivityForResult<SimpleCameraActivity>(bundle = arrayOf("imgLocalPath" to localPath,"cameraViewInfo" to "cameraViewInfo","cameraViewType" to "cameraViewType"),requestCode = 100)
            // startActivity<TestMarkerMapActivity>()
            //  startActivity<TestTuwenActivity>()
            println("message={0}".messageFormat("test"))
            startActivity<TestFunActivity>()
        }.launch()
    }

    override fun setAppTheme(theme: String) {
        when(theme){
            ThemeHelper.LIGHT_MODE -> {
                bindView.appTheme.text = "Light"
                bindView.appTheme.isChecked = false
            }

            ThemeHelper.DARK_MODE ->{
                bindView.appTheme.text = "DARK"
                bindView.appTheme.isChecked = true
            }
            else ->{
                bindView.appTheme.text = "DEFAULT"
                bindView.appTheme.isChecked = false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
            val  imagePath = data?.getStringExtra("imgLocalPath")?:""
            val  name = FileUtils.getFileName(imagePath)
            val  dir = FileUtils.getFolderName(imagePath)
             startActivity<TuyaTestActivity>(bundle = arrayOf(BaseTuYaActivity.KEY_PIC_NAME to name,BaseTuYaActivity.KEY_PIC_DIRECTORYPATH to dir))
        }
    }

    override fun showFingerprintDialog(): BiometricPrompt {
       return BiometricPrompt.Builder(this).setTitle("指纹识别")
               .setDescription("这是描述").setNegativeButton("取消",mainExecutor, { _, _ -> }).build()
    }

    override fun getContext(): Context {
        return this
    }
}