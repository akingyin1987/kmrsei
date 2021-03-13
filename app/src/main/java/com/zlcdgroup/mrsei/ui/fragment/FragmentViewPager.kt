package com.zlcdgroup.mrsei.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.akingyin.base.SimpleFragment
import com.google.android.material.tabs.TabLayout
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.databinding.FragmentTabViewpagerBinding


/**
 * @ Description:
 * @author king
 * @ Date 2020/5/8 14:51
 * @version V1.0
 */
class FragmentViewPager : SimpleFragment(){

    lateinit var bindView:FragmentTabViewpagerBinding

    override fun useViewBind()=true

    override fun initViewBind(inflater: LayoutInflater, container: ViewGroup?): View {
        return FragmentTabViewpagerBinding.inflate(inflater,container,false).also {
            bindView = it
        }.root
    }

    override fun initView() {
        val  data = mutableListOf<Fragment>()

        for (i in 1..8){
            data.add(FragmentLayzTest.newInstance("ViewPager2-$i"))
            bindView.tabLayout.addTab(TabLayout.Tab().apply {
                text="page$i"
            })
        }
        bindView.viewpager.adapter = MyFragmentStateAdapter(this,data)
        bindView.viewpager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bindView.tabLayout.selectTab(bindView.tabLayout.getTabAt(position))
            }
        })
        bindView.tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    bindView.viewpager.currentItem = it.position
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