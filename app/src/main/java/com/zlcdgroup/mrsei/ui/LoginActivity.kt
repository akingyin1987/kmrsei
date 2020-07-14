package com.zlcdgroup.mrsei.ui

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
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
import com.zlcdgroup.mrsei.presenter.UserLoginContract
import com.zlcdgroup.mrsei.presenter.impl.UserLoginPersenterImpl
import com.zlcdgroup.mrsei.ui.mvvm.LoginViewModel
import com.zlcdgroup.mrsei.utils.ThemeHelper
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable.just
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.delay
import permissions.dispatcher.ktx.withPermissionsCheck
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


class LoginActivity  : BaseDaggerActivity() ,UserLoginContract.View{

    @Inject
    lateinit var userLoginPersenterImpl: UserLoginPersenterImpl

    lateinit var  viewModel :LoginViewModel


    override fun getLayoutId(): Int = R.layout.activity_login

    override fun initializationData(savedInstanceState: Bundle?) {
        userLoginPersenterImpl.attachView(this)
    }


    override fun onSaveInstanceData(outState: Bundle?) {
    }

    override fun initView() {
          viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(app).create(LoginViewModel::class.java)
          viewModel.Login("123","456")
        val person = userLoginPersenterImpl.getLastPerson()
        person?.let {

            et_mobile.setText(it.personAccount.isEmptyOrNull())
            et_password.setText(it.personPassword.isEmptyOrNull())
        }
       et_password.setOnEditorActionListener { _, actionId, _ ->
           if(actionId == EditorInfo.IME_ACTION_GO){
               userLoginPersenterImpl.login(et_mobile.text.toString(),et_password.text.toString())

               return@setOnEditorActionListener  true
           }
           return@setOnEditorActionListener false
       }
        println("btn_login2")

        btn_login.click {
            testCameraAuth()

            userLoginPersenterImpl.login(et_mobile.text.toString(),et_password.text.toString())
        }

        app_theme.click {
            app_theme.isChecked.yes {
                println("yes----------->>>>")
            }
            app_theme.isChecked.yes {
                userLoginPersenterImpl.saveAppTheme(ThemeHelper.DARK_MODE)
            }.no {
                userLoginPersenterImpl.saveAppTheme(ThemeHelper.LIGHT_MODE)
            }
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
        date_picker_actions.click {
            TimePickerDialog.newInstance({ view, hourOfDay, minute, second ->
                 val  calendar  = Calendar.getInstance()
                 calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
                 calendar.set(Calendar.MINUTE,minute)
                 calendar.set(Calendar.SECOND,second)
                  day_time = calendar.time.time
                  date_picker_actions.text = DateUtil.millis2String(day_time)
            },true).show(supportFragmentManager,"time-fragment")

        }
        btn_create_code.click {
            val  keywork = edit_keywork.text.toString()
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
      d3 =  Observable.just("2").autoDispose(AndroidLifecycleScopeProvider.from(this))
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

   private fun    testCameraAuth()=withPermissionsCheck(Manifest.permission.CAMERA,
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
    override fun goToMainActivity() =withPermissionsCheck(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION){

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
    }

    override fun setAppTheme(theme: String) {
        when(theme){
            ThemeHelper.LIGHT_MODE -> {
                app_theme.setText("Light")
                app_theme.isChecked = false
            }

            ThemeHelper.DARK_MODE ->{
                app_theme.setText("DARK")
                app_theme.isChecked = true
            }
            else ->{
                app_theme.setText("DEFAULT")
                app_theme.isChecked = false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
            val  imagePath = data?.getStringExtra("imgLocalPath")
            val  name = FileUtils.getFileName(imagePath)
            val  dir = FileUtils.getFolderName(imagePath)
             startActivity<TuyaTestActivity>(bundle = arrayOf(BaseTuYaActivity.KEY_PIC_NAME to name,BaseTuYaActivity.KEY_PIC_DIRECTORYPATH to dir))
        }
    }
}