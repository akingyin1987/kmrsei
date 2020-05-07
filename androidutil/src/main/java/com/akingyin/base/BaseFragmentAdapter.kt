package com.akingyin.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


/**
 * @ Description:
 * @author king
 * @ Date 2018/8/6 10:20
 * 该类内的每一个生成的 Fragment 都将保存在内存之中，
 * 因此适用于那些相对静态的页，数量也比较少的那种；
 * 如果需要处理有很多页，并且数据动态性较大、占用内存较多的情况，
 * 应该使用FragmentStatePagerAdapter。
 * @version V1.0
 */
class BaseFragmentAdapter   : FragmentPagerAdapter {


    private   var  fragmentlist:List<Fragment>


    private   var  mTitles:List<String>?=null

    constructor(fm: FragmentManager, fragmentlist: List<Fragment>) : super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT ) {
        this.fragmentlist = fragmentlist
    }

    constructor(fm: FragmentManager, fragmentlist: List<Fragment>, mTitles: List<String>?) : super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT ) {
        this.fragmentlist = fragmentlist
        this.mTitles = mTitles
    }

    //刷新fragment

    private fun setFragments(fm:FragmentManager, fragments: List<Fragment>, mTitles: List<String>) {
        this.mTitles = mTitles
        val ft = fm.beginTransaction()
        fragmentlist.forEach {
            ft.remove(it)
        }
        ft.commitAllowingStateLoss()
        fm.executePendingTransactions()
        this.fragmentlist = fragments
        notifyDataSetChanged()
    }
    override fun getItem(p0: Int): Fragment {

        return  fragmentlist[p0]
    }

    override fun getCount(): Int {
      return   fragmentlist.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
       return  if(null == mTitles)"" else mTitles!![position]
    }



}