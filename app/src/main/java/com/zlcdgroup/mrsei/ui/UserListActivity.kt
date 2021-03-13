package com.zlcdgroup.mrsei.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import com.akingyin.base.BaseDaggerActivity
import com.akingyin.base.ext.click
import com.akingyin.base.ext.goActivity
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.databinding.ActivityUserlistBinding
import com.zlcdgroup.mrsei.presenter.UserListContract
import com.zlcdgroup.mrsei.presenter.impl.UserListPresenterImpl
import com.zlcdgroup.mrsei.ui.adapter.UserListAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject





/**
 * @ Description:
 * @author king
 * @ Date 2018/9/4 10:52
 * @version V1.0
 */
@Route(path = "/user/list")
@AndroidEntryPoint
class UserListActivity  : BaseDaggerActivity(),UserListContract.View {

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



    override fun getLayoutId(): Int = R.layout.activity_userlist

    lateinit var bindView:ActivityUserlistBinding

    override fun useViewBind()=true

    override fun initViewBind() {
        super.initViewBind()
        bindView = ActivityUserlistBinding.inflate(layoutInflater)
        setContentView(bindView.root)
    }

    override fun initializationData(savedInstanceState: Bundle?) {
        setToolBar(bindView.topBar.toolbar,"测试")
        println("name=$name")
        userListPresenterImpl.attachView(this)
        bindView.recycle.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        bindView.recycle.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        bindView.recycle.adapter = userListAdapter
        userListAdapter.setNewInstance(userListPresenterImpl.getUserList()?.toMutableList())
        userListAdapter.setOnItemClickListener { _, _, position ->
            showSucces(userListAdapter.getItem(position).name)
        }

        bindView.fab.setOnClickListener {
                            val random  = Random()
                            val userEntity = UserEntity()
                            userEntity.name="name1"+random.nextInt(100)
                            userEntity.age = random.nextInt(100)
                            userListPresenterImpl.addUser(userEntity)



        }


        bindView.fabShare.click {
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
        userListAdapter.setNewInstance(userEntitys?.toMutableList())
    }

    override fun showAddUser(userEntity: UserEntity) {
       // userListAdapter.addData(userEntity)
        val intent  = Intent()
        intent.setClass(this,SteperActivity::class.java)
        startActivity(intent)

    }

    override fun showDelect(userEntity: UserEntity, postion: Int) {

    }







    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    override fun notifyModifyUser(userEntity: UserEntity, postion: Int) {
    }

    override fun initInjection() {
        super.initInjection()
        ARouter.getInstance().inject(this)
    }
}