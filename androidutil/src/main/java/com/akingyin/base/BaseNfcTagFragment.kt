package com.akingyin.base

import com.zlcdgroup.nfcsdk.RfidInterface

abstract class BaseNfcTagFragment : BaseFragment() {




    open   fun    handTag( rfid:String?,rfidInterface: RfidInterface?){}
}