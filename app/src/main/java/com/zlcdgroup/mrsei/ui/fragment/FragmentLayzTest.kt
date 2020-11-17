package com.zlcdgroup.mrsei.ui.fragment

import android.os.Bundle
import com.akingyin.base.SimpleFragment
import com.akingyin.base.ext.currentTimeMillis
import com.akingyin.base.utils.DateUtil
import com.akingyin.base.utils.RandomUtil
import com.zlcdgroup.mrsei.R
import kotlinx.android.synthetic.main.fragment_test.*

/**
 *
 * 在androidX 中fragment 只需要在onResume 做懒加载即可
 * @ Description:
 * @author king
 * @ Date 2020/1/10 14:43
 * @version V1.0
 */
class FragmentLayzTest : SimpleFragment() {

    override fun getLayoutId()= R.layout.fragment_test
    override fun initView() {

    }

    companion object{
        fun   newInstance(str:String):FragmentLayzTest{
            return FragmentLayzTest().apply {
                arguments = Bundle().apply {
                    putString("data",str)
                }
            }
        }
    }

    override fun initEventAndData() {
        tv_info.text = arguments?.getString("data")?:TAG
        println("initEventAndData->${arguments?.getString("data")}")
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("onCreate=${TAG}")

    }

    override fun onStart() {
        super.onStart()
        println("onStart=${TAG}")
    }

    override fun onResume() {
        super.onResume()

        println("onResume=${TAG}")
    }


    override fun injection() {

    }

    override fun lazyLoad() {
        println("第一次懒加载=${TAG}  time=${DateUtil.millis2String(currentTimeMillis)}")    }

    override fun onPause() {
        super.onPause()
        println("onPause=${TAG}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("onDestroyView=${TAG}")
    }

    override fun onStop() {
        super.onStop()
        println("onStop=${TAG}")
    }

    fun   isComplete() = RandomUtil.getRandomNum(1,10)/2 == 0

}