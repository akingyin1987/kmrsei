package com.zlcdgroup.mrsei.ui

import android.os.Bundle
import com.akingyin.base.SimpleActivity
import com.akingyin.base.ext.click
import com.zlcdgroup.mrsei.R
import kotlinx.android.synthetic.main.activity_coroutlines.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @ Description:
 * @author king
 * @ Date 2019/7/4 11:11
 * @version V1.0
 */
class CoroutinesDemo :SimpleActivity() {

    override fun initInjection() {
    }

    override fun getLayoutId() = R.layout.activity_coroutlines

    override fun initializationData(savedInstanceState: Bundle?) {
    }

    override fun onSaveInstanceData(outState: Bundle?) {
    }

    override fun initView() {
        setToolBar(toolbar,"协程测试")
        btn_test.click {
            showLoading()

          var job =    GlobalScope.launch {
                val token = requestToken()
                val post = createPost(token, "item")
                processPost(post)
                withContext(Dispatchers.Main){
                    hideLoadDialog()
                }

            }




        }

    }

    override fun startRequest() {
    }


    suspend fun requestToken(): String {

        return  "token"
    }   // 挂起函数
    suspend fun createPost(token: String, item: String): String {
        return "post"
    }  // 挂起函数
    fun processPost(post: String) {
        println("process->"+post)
    }
}