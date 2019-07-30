package com.zlcdgroup.mrsei.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import com.akingyin.base.BaseActivity
import com.akingyin.base.ext.click
import com.akingyin.base.ext.goActivity
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import com.umeng.socialize.shareboard.SnsPlatform
import com.umeng.socialize.utils.ShareBoardlistener
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.presenter.UserListContract
import com.zlcdgroup.mrsei.presenter.impl.UserListPresenterImpl
import com.zlcdgroup.mrsei.ui.adapter.UserListAdapter
import com.zlcdgroup.mrsei.utils.Defaultcontent
import kotlinx.android.synthetic.main.activity_userlist.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates




/**
 * @ Description:
 * @author king
 * @ Date 2018/9/4 10:52
 * @version V1.0
 */
@Route(path = "/user/list")
class UserListActivity  : BaseActivity(),UserListContract.View, UMShareListener {

    @Inject
    lateinit var userListPresenterImpl: UserListPresenterImpl

    @Inject
    lateinit var userListAdapter: UserListAdapter

    @Autowired
    @JvmField
    var name: String? = null
    @Autowired
    @JvmField
    var age: Int? = 0

    var   shareAction :ShareAction  by Delegates.notNull()

    override fun getLayoutId(): Int = R.layout.activity_userlist

    override fun initializationData(savedInstanceState: Bundle?) {
        setToolBar(toolbar,"测试")
        println("name=$name")
        userListPresenterImpl.attachView(this)
        recycle.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recycle.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        recycle.adapter = userListAdapter
        userListAdapter.setNewData(userListPresenterImpl.getUserList())
        userListAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener {
            _, _, position ->
            showSucces(userListAdapter.getItem(position)?.name)
        }
        fab.setOnClickListener {
                            var  random :Random = Random()
                            var   userEntity: UserEntity = UserEntity()
                            userEntity.name="name1"+random.nextInt(100)
                            userEntity.age = random.nextInt(100)
                            userListPresenterImpl.addUser(userEntity)



        }
        shareAction = ShareAction(this).setDisplayList( SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE,
                SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
                SHARE_MEDIA.ALIPAY, SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN,
                SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL, SHARE_MEDIA.YNOTE,
                SHARE_MEDIA.EVERNOTE, SHARE_MEDIA.LAIWANG, SHARE_MEDIA.LAIWANG_DYNAMIC,
                SHARE_MEDIA.LINKEDIN, SHARE_MEDIA.YIXIN, SHARE_MEDIA.YIXIN_CIRCLE,
                SHARE_MEDIA.TENCENT, SHARE_MEDIA.FACEBOOK, SHARE_MEDIA.TWITTER,
                SHARE_MEDIA.WHATSAPP, SHARE_MEDIA.GOOGLEPLUS, SHARE_MEDIA.LINE,
                SHARE_MEDIA.INSTAGRAM, SHARE_MEDIA.KAKAO, SHARE_MEDIA.PINTEREST,
                SHARE_MEDIA.POCKET, SHARE_MEDIA.TUMBLR, SHARE_MEDIA.FLICKR,
                SHARE_MEDIA.FOURSQUARE, SHARE_MEDIA.MORE)
                .addButton("复制文本", "复制文本", "umeng_socialize_copy", "umeng_socialize_copy")
                .addButton("复制链接", "复制链接", "umeng_socialize_copyurl", "umeng_socialize_copyurl")
                .setShareboardclickCallback(object :ShareBoardlistener{
                    override fun onclick(snsPlatform: SnsPlatform?, share_media: SHARE_MEDIA?) {
                        when {
                            snsPlatform?.mShowWord.equals("复制文本") -> Toast.makeText(this@UserListActivity, "复制文本按钮", Toast.LENGTH_LONG).show()
                            snsPlatform?.mShowWord.equals("复制链接") -> Toast.makeText(this@UserListActivity, "复制链接按钮", Toast.LENGTH_LONG).show()
                            else -> {
                                var web = UMWeb(Defaultcontent.url)
                                web.title = "来自分享面板标题"
                                web.description = "来自分享面板内容"
                                web.setThumb(UMImage(this@UserListActivity,R.drawable.ic_menu ))
                                ShareAction(this@UserListActivity).withMedia(web)
                                        .setCallback(this@UserListActivity)
                                        .setPlatform(share_media).share()

                            }
                        }
                    }
                })

        fab_share.click {
//           var config = ShareBoardConfig()
//            config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_CENTER)
//            config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR)
//
            goActivity<UserListDataBindActivity>()
        }

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    override fun initView() {
    }

    override fun startRequest() {

    }

    override fun showUserList(userEntitys: List<UserEntity>?) {
        userListAdapter.setNewData(userEntitys)
    }

    override fun showAddUser(userEntity: UserEntity) {
       // userListAdapter.addData(userEntity)
        var  intent :Intent = Intent()
        intent.setClass(this,SteperActivity::class.java)
        startActivity(intent)

    }

    override fun showDelect(userEntity: UserEntity, postion: Int) {

    }


    override fun onStart(p0: SHARE_MEDIA?) {
    }

    override fun onResult(platform: SHARE_MEDIA?) {
        platform?.let {
            if(it.name.equals("WEIXIN_FAVORITE")){
                Toast.makeText(this, platform.getName() + " 收藏成功啦", Toast.LENGTH_SHORT).show()
            }else{
                if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                        && platform != SHARE_MEDIA.EMAIL
                        && platform != SHARE_MEDIA.FLICKR
                        && platform != SHARE_MEDIA.FOURSQUARE
                        && platform != SHARE_MEDIA.TUMBLR
                        && platform != SHARE_MEDIA.POCKET
                        && platform != SHARE_MEDIA.PINTEREST

                        && platform != SHARE_MEDIA.INSTAGRAM
                        && platform != SHARE_MEDIA.GOOGLEPLUS
                        && platform != SHARE_MEDIA.YNOTE
                        && platform != SHARE_MEDIA.EVERNOTE) {
                    Toast.makeText(this, platform.getName() + " 分享成功啦", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onCancel(p0: SHARE_MEDIA?) {
    }

    override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        shareAction.close()
    }

    override fun notifyModifyUser(userEntity: UserEntity, postion: Int) {
    }

    override fun initInjection() {
        super.initInjection()
        ARouter.getInstance().inject(this)
    }
}