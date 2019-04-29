package com.akingyin.base

import android.annotation.SuppressLint
import java.util.*

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
class BaseFragmentAdapter : androidx.fragment.app.FragmentPagerAdapter {


    private   var  fragmentlist:List<androidx.fragment.app.Fragment>?= ArrayList()

    private   var  mTitles:List<String>?=null

    constructor(fm: androidx.fragment.app.FragmentManager?, fragmentlist: List<androidx.fragment.app.Fragment>?) : super(fm) {
        this.fragmentlist = fragmentlist
    }

    constructor(fm: androidx.fragment.app.FragmentManager?, fragmentlist: List<androidx.fragment.app.Fragment>?, mTitles: List<String>?) : super(fm) {
        this.fragmentlist = fragmentlist
        this.mTitles = mTitles
    }

    //刷新fragment
    @SuppressLint("CommitTransaction")
    private fun setFragments(fm: androidx.fragment.app.FragmentManager, fragments: List<androidx.fragment.app.Fragment>, mTitles: List<String>) {
        this.mTitles = mTitles
        if (this.fragmentlist != null) {
            val ft = fm.beginTransaction()
            fragmentlist?.forEach {
                ft.remove(it)
            }
            ft.commitAllowingStateLoss()
            fm.executePendingTransactions()
        }
        this.fragmentlist = fragments
        notifyDataSetChanged()
    }
    override fun getItem(p0: Int): androidx.fragment.app.Fragment {
        return  fragmentlist!![p0]
    }

    override fun getCount(): Int {
      return   fragmentlist!!.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
       return  if(null == mTitles)"" else mTitles!![position]
    }
}