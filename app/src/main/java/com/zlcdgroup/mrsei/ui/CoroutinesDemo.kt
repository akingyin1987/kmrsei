package com.zlcdgroup.mrsei.ui

import android.os.Bundle
import com.akingyin.base.BaseDaggerActivity
import com.akingyin.base.ext.click
import com.akingyin.base.ext.currentTimeMillis
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.db.dao.NoticeDao
import com.zlcdgroup.mrsei.data.entity.NoticeEntity
import com.zlcdgroup.mrsei.databinding.ActivityCoroutlinesBinding
import com.zlcdgroup.mrsei.utils.RetrofitConfig
import dagger.hilt.android.AndroidEntryPoint

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

@AndroidEntryPoint
class CoroutinesDemo :BaseDaggerActivity() {

     @Inject
    lateinit var noticeDao :NoticeDao

    lateinit var bindView: ActivityCoroutlinesBinding

    override fun getLayoutId() = R.layout.activity_coroutlines

    override fun useViewBind()=true

    override fun initViewBind() {
        super.initViewBind()
        bindView = ActivityCoroutlinesBinding.inflate(layoutInflater)
        setContentView(bindView.root)
    }

    override fun initializationData(savedInstanceState: Bundle?) {
    }

    override fun onSaveInstanceData(outState: Bundle?) {
    }

    override fun initView() {

        setToolBar(toolbar,"协程测试")


        GlobalScope.launch (IO){
            val noticeEntity = NoticeEntity().apply {
                name="name"
                demo="demo"
                time= currentTimeMillis
            }
            noticeDao.insertNotice(noticeEntity)
        }
        bindView.btnTest.click {
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