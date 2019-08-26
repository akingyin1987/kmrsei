package com.zlcdgroup.mrsei.ui

import android.os.Bundle
import com.akingyin.base.BaseActivity
import com.akingyin.base.ext.click
import com.akingyin.base.ext.currentTimeMillis
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.db.dao.NoticeDao
import com.zlcdgroup.mrsei.data.entity.NoticeEntity
import com.zlcdgroup.mrsei.utils.RetrofitConfig
import kotlinx.android.synthetic.main.activity_coroutlines.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2019/7/4 11:11
 * @version V1.0
 */
class CoroutinesDemo :BaseActivity() {

     @Inject
    lateinit var noticeDao :NoticeDao

    override fun getLayoutId() = R.layout.activity_coroutlines



    override fun initializationData(savedInstanceState: Bundle?) {
    }

    override fun onSaveInstanceData(outState: Bundle?) {
    }

    override fun initView() {
        setToolBar(toolbar,"协程测试")


        GlobalScope.launch (IO){
            var  noticeEntity = NoticeEntity().apply {
                name="name"
                demo="demo"
                time= currentTimeMillis
            }
            noticeDao.insertNotice(noticeEntity)
        }
        btn_test.click {
            showLoading()

         GlobalScope.launch(IO) {
             try {
                 RetrofitConfig.getDefaultCoroutineServer().loginK("test","test")
                         .await().apply {
                             println("onResult")
                         }
             }catch (e:Exception){
                 e.printStackTrace()
             }


         }


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


    fun    testCoroutines(){

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