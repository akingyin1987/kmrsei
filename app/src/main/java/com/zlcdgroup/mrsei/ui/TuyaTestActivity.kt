package com.zlcdgroup.mrsei.ui

import com.akingyin.base.ext.appServerTime
import com.akingyin.tuya.BaseTuYaActivity
import java.io.File

/**
 * @ Description:
 * @author king
 * @ Date 2019/11/20 17:37
 * @version V1.0
 */
class TuyaTestActivity  : BaseTuYaActivity() {

    override fun saveTuYaBitmapSuccess(outFile: File?) {
    }

    override fun getNowTime() = appServerTime
}