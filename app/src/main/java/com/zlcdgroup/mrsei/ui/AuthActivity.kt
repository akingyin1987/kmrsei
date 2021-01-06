package com.zlcdgroup.mrsei.ui

import android.content.Intent
import android.os.Bundle

import com.akingyin.base.BaseDaggerActivity

import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.shareboard.SnsPlatform
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.presenter.UserAuthContract
import com.zlcdgroup.mrsei.ui.adapter.AuthAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.include_toolbar.*
import javax.inject.Inject


/**
 * @ Description:
 * @author king
 * @ Date 2019/5/10 13:45
 * @version V1.0
 */

@AndroidEntryPoint
class AuthActivity :BaseDaggerActivity(),UserAuthContract.View{

    override fun getLayoutId() = R.layout.activity_auth

    override fun initializationData(savedInstanceState: Bundle?) {
    }

    override fun onSaveInstanceData(outState: Bundle?) {
        UMShareAPI.get(this).onSaveInstanceState(outState)
    }

   val list = arrayOf( SHARE_MEDIA.QQ,SHARE_MEDIA.WEIXIN)

    @Inject
    lateinit var  authAdapter :AuthAdapter
    var platforms = ArrayList<SnsPlatform>()

    override fun initView() {
        setToolBar(toolbar,"权限")


        platforms.clear()
        list.forEach {
            if(!it.toString().equals(SHARE_MEDIA.GENERIC.toString())){
                platforms.add(it.toSnsPlatform())
            }
        }

        authAdapter.mutableList = platforms
        recycle.adapter = authAdapter
    }

    override fun startRequest() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data)
    }

    override fun onDestroy() {
        UMShareAPI.get(this).release()
        super.onDestroy()
    }
}