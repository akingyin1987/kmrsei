package com.akingyin.base

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentFilter.MalformedMimeTypeException
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.zlcdgroup.nfcsdk.ConStatus
import com.zlcdgroup.nfcsdk.RfidConnectorInterface
import com.zlcdgroup.nfcsdk.RfidInterface


/**
 * @ Description:
 * @author king
 * @ Date 2018/9/3 15:30
 * @version V1.0
 */
 abstract class BaseNfcTagActivity  :BaseActivity(),RfidConnectorInterface {

  var mAdapter: NfcAdapter? = null
  var mPendingIntent: PendingIntent? = null
  var mFilters: Array<IntentFilter>? = null
  var mTechLists: Array<Array<String>>? = null
  var mf: MifareClassic? = null
  var tagFromIntent: Tag? = null
  var openNfc = 0
 var isSupportBle = true

 var openBle = false

 var mNotificationManager: NotificationManager? = null


 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  mAdapter = NfcAdapter.getDefaultAdapter(this)
  mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
  if(packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
    isSupportBle = false
  }
  if(null == mAdapter){
   showWarning("当前终端不支持NFC")
  }else{
     mPendingIntent = PendingIntent.getActivity(this,0, Intent(this,javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0)
   val ndef = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
   try {
    ndef.addDataType("*/*")
    mFilters = arrayOf(ndef)
    mTechLists = arrayOf(
            arrayOf(MifareClassic::class.java.name),
            arrayOf(NfcA::class.java.name),
            arrayOf(NfcB::class.java.name),
            arrayOf(NfcF::class.java.name),
            arrayOf(NfcV::class.java.name))

   } catch (e: MalformedMimeTypeException) {
    // TODO Auto-generated catch block
    e.printStackTrace()
   }
  }
 }


 override fun onStart() {
  super.onStart()
  if (null != mAdapter && !mAdapter!!.isEnabled) {
   showMessage("请打开NFC")
   openNfc++
   if (openNfc >= 3) {
    openNfc = 3
    return
   }
   startActivity( Intent("android.settings.NFC_SETTINGS"))

  }
 }

 override fun onResume() {
  if(null != mAdapter){
    mAdapter!!.enableForegroundDispatch(this,mPendingIntent,mFilters,mTechLists)
  }
  super.onResume()

 }

 override fun onPause() {
  if(null != mAdapter){
   mAdapter!!.disableForegroundDispatch(this)
  }
  super.onPause()
 }


 override fun onNewIntent(intent: Intent?) {
  super.onNewIntent(intent)

  if (null != intent && NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
   tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
   mf = MifareClassic.get(tagFromIntent)
  }
 }


 var mainHandler: Handler = object : Handler(Looper.getMainLooper()) {
  override fun  handleMessage(msg: Message) {
   super.handleMessage(msg)
   if (msg.what == 1) {

    handTag(msg.obj.toString(), null)
   }
  }
 }

 override fun onNewRfid(data: ByteArray?, p1: RfidInterface?) {

 }

 override fun onConnectStatus(p0: ConStatus?) {
 }

 override fun onElectricity(p0: Int) {
  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
 }

 abstract   fun    handTag( rfid:String?,rfidInterface: RfidInterface?)
}