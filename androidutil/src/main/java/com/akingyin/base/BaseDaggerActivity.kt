

package com.akingyin.base

import com.zlcdgroup.nfcsdk.RfidInterface





/**
 * 基础类
 * @ Description:
 * @author king
 * @ Date 2018/8/3 16:16
 * @version V1.0
 */
abstract  class BaseDaggerActivity : BaseNfcTagActivity() {

//    @Inject
//    lateinit var supportFragmentInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>
//    @Inject
//    lateinit var frameworkFragmentInjector: DispatchingAndroidInjector<android.app.Fragment>



    override fun initInjection() {



    }


//     fun fragmentInjector(): DispatchingAndroidInjector<Fragment>? {
//        return   frameworkFragmentInjector
//    }
//
//     fun supportFragmentInjector(): DispatchingAndroidInjector<androidx.fragment.app.Fragment>? {
//        return  supportFragmentInjector
//    }

    override fun handTag(rfid: String?, rfidInterface: RfidInterface?) {

    }
}