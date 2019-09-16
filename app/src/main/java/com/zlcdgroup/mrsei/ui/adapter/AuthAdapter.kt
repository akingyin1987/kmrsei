package com.zlcdgroup.mrsei.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.common.ResContainer
import com.umeng.socialize.shareboard.SnsPlatform
import com.umeng.socialize.utils.SocializeUtils
import com.zlcdgroup.mrsei.R
import javax.inject.Inject


/**
 * @ Description:
 * @author king
 * @ Date 2019/5/10 13:57
 * @version V1.0
 */
class AuthAdapter @Inject constructor(var context: Activity) :BaseAdapter(){

    var mutableList: MutableList<SnsPlatform> = mutableListOf()

    var   dialog: MaterialDialog = MaterialDialog.Builder(context)
             .progress(false, 0).build()

    override fun getView(position: Int, convertView1: View?, parent: ViewGroup?): View {
        var   convertView = convertView1
        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.app_authadapter_share, null)
        }
        val isauth = UMShareAPI.get(context).isAuthorize(context, mutableList[position].mPlatform)

        val img = convertView?.findViewById(R.id.adapter_image) as ImageView
        img.setImageResource(ResContainer.getResourceId(context, "drawable", mutableList[position].mIcon))
        val tv = convertView.findViewById(R.id.name) as TextView
        tv.text = mutableList[position].mShowWord
        val authBtn = convertView.findViewById(R.id.auth_button) as TextView
        if (isauth) {
            authBtn.text = "删除授权"
        } else {
            authBtn.text = "授权"
        }
        authBtn.setOnClickListener {
            if (isauth) {
                UMShareAPI.get(context).deleteOauth(context, mutableList[position].mPlatform, authListener)
            } else {
                UMShareAPI.get(context).doOauthVerify(context, mutableList[position].mPlatform, authListener)
            }
        }

        if (position == mutableList.size - 1) {

            convertView.findViewById<View>(R.id.divider).visibility = View.GONE
        } else {
            convertView.findViewById<View>(R.id.divider).visibility = View.VISIBLE
        }
//
        return convertView
    }

    override fun getItem(position: Int): Any {
        return mutableList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return  mutableList.size
    }

    var authListener: UMAuthListener = object : UMAuthListener {
        override fun onStart(platform: SHARE_MEDIA) {
            SocializeUtils.safeShowDialog(dialog)
        }

        override fun onComplete(platform: SHARE_MEDIA, action: Int, data: Map<String, String>) {
            SocializeUtils.safeCloseDialog(dialog)
            Toast.makeText(context, "成功了", Toast.LENGTH_LONG).show()
            notifyDataSetChanged()
        }

        override fun onError(platform: SHARE_MEDIA, action: Int, t: Throwable) {
            SocializeUtils.safeCloseDialog(dialog)
            Toast.makeText(context, "失败：" + t.message, Toast.LENGTH_LONG).show()
        }

        override fun onCancel(platform: SHARE_MEDIA, action: Int) {
            SocializeUtils.safeCloseDialog(dialog)
            Toast.makeText(context, "取消了", Toast.LENGTH_LONG).show()
        }
    }
}