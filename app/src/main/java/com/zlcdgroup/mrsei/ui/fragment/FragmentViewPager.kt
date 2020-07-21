package com.zlcdgroup.mrsei.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.akingyin.base.SimpleFragment
import com.google.android.material.tabs.TabLayout
import com.zlcdgroup.mrsei.R
import kotlinx.android.synthetic.main.fragment_tab_viewpager.*

/**
 * @ Description:
 * @author king
 * @ Date 2020/5/8 14:51
 * @version V1.0
 */
class FragmentViewPager : SimpleFragment(){


    override fun initView() {
        val  data = mutableListOf<Fragment>()
        println("null==${null == tab_layout}")
        for (i in 1..8){
            data.add(FragmentLayzTest.newInstance("ViewPager2-$i"))
            tab_layout.addTab(TabLayout.Tab().apply {
                text="page$i"
            })
        }
        viewpager.adapter = MyFragmentStateAdapter(this,data)
        viewpager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tab_layout.selectTab(tab_layout.getTabAt(position))
            }
        })
        tab_layout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewpager.currentItem = it.position
                }
            }
        })

    }

    override fun injection() {

    }

    override fun lazyLoad() {
        println("lazyLoad=")
    }

    override fun getLayoutId()= R.layout.fragment_tab_viewpager


    companion object{
        fun   newInstance(str:String):FragmentViewPager{
            return FragmentViewPager().apply {
                arguments = Bundle().apply {
                    putString("data",str)
                }
    }
        }
    }

    var   str :String=""
    override fun initEventAndData() {

       str= arguments?.getString("data","") ?:""
    }

    class MyFragmentStateAdapter constructor(fragment:Fragment,var data:List<Fragment>) :FragmentStateAdapter(fragment){

        override fun getItemCount()= data.size

        override fun createFragment(position: Int)=data[position]
    }

}