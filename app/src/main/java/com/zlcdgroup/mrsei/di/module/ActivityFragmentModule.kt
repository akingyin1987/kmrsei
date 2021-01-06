import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped


//package com.zlcdgroup.mrsei.di.module
//
//import android.app.Activity
//import android.content.Context
//import com.zlcdgroup.mrsei.di.qualifier.ActivityContext
//import com.zlcdgroup.mrsei.di.scope.PerActivity
//import com.zlcdgroup.mrsei.presenter.modules.*
//import com.zlcdgroup.mrsei.ui.*
//import dagger.Binds
//import dagger.Module
//import dagger.android.ContributesAndroidInjector
//
//
///**
// * @ Description:
// * @author king
// * @ Date 2018/9/3 18:15
// * @version V1.0
// *
// * dagger2 里 如果@Model 注解的是抽象类时 @provides 标注的必须是静态方法
//A @Module may not contain both non-static @Provides methods and abstract @Binds or @Multibinds declarations
//
//java 方式这样写没有问题 而我们知道Kotlin 是没有static 标记显示是静态方法 需要写companion object 来包含静态成员
//
//暂时能做到的方式就是避免 @Provides 不要写在抽象类里 把@Binds标记的抽象方法 写在另一个类里 然后 再用非抽象类 include 进来 如下
// */
//
//@Module
//@InstallIn(ActivityComponent::class)
// abstract class ActivityFragmentModule {
//
//    @Provides
//    @ActivityScoped
//    fun provideActivity(@ActivityContext context:Context):Activity{
//        return context as Activity
//    }
//
//
//    @Provides
//    @ActivityContext
//    fun provideFragmentManager(activity: Activity): FragmentManager {
//        return (activity as FragmentActivity).supportFragmentManager
//    }
//
//
//
//}