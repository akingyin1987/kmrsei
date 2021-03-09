package com.zlcdgroup.mrsei.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.akingyin.base.SimpleActivity
import com.google.android.material.tabs.TabLayout
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.databinding.ActivityFragmentsBinding
import com.zlcdgroup.mrsei.ui.fragment.FragmentLayzTest
import com.zlcdgroup.mrsei.ui.fragment.FragmentViewPager

/**
 * 测试 Fragment+Viewpager 懒加载问题
 * @ Description:
 * @author king
 * @ Date 2020/1/10 15:04
 * @version V1.0
 */
class FragmentTestActivity : SimpleActivity() {

    override fun initInjection() {
    }

    override fun getLayoutId()= R.layout.activity_fragments

    lateinit var viewBinding : ActivityFragmentsBinding

    override fun useViewBind()=true

    override fun initViewBind() {
        super.initViewBind()
        viewBinding = ActivityFragmentsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }

    override fun initializationData(savedInstanceState: Bundle?) {
    }

    override fun onSaveInstanceData(outState: Bundle?) {
    }

    override fun initView() {
        val  fragments = mutableListOf<Fragment>()
        val  titles = mutableListOf<String>()
        fragments.add(FragmentViewPager.newInstance("abc"))
        titles.add("abc")
        for (i in 1..10){
            titles.add("test$i")
            fragments.add(FragmentLayzTest.newInstance("frag-data$i"))
        }

        viewBinding.viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(viewBinding.tabLayout))
        viewBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewBinding.viewpager.currentItem = it.position
                }
            }
        })
        viewBinding.viewpager.adapter = FragmentListAdapter(fragments,titles,supportFragmentManager)
        viewBinding.tabLayout.setupWithViewPager(viewBinding.viewpager)


    }

    override fun startRequest() {
    }

    class   FragmentListAdapter @JvmOverloads constructor(var fragments:List<Fragment>,var titles : List<String>,fm: FragmentManager,mBehavior:Int=BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)  : FragmentPagerAdapter(fm,mBehavior) {



        override fun getItem(position: Int): Fragment {

            return  fragments[position]
        }

        override fun getCount(): Int {
            return  fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }
}