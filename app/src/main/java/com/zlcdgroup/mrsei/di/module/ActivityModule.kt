package com.zlcdgroup.mrsei.di.module

import android.app.Activity
import android.content.Context
import com.zlcdgroup.mrsei.di.qualifier.ActivityContext
import com.zlcdgroup.mrsei.di.scope.PerActivity
import com.zlcdgroup.mrsei.presenter.modules.*
import com.zlcdgroup.mrsei.ui.*
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector


/**
 * @ Description:
 * @author king
 * @ Date 2018/9/3 18:15
 * @version V1.0
 *
 * dagger2 里 如果@Model 注解的是抽象类时 @provides 标注的必须是静态方法
A @Module may not contain both non-static @Provides methods and abstract @Binds or @Multibinds declarations

java 方式这样写没有问题 而我们知道Kotlin 是没有static 标记显示是静态方法 需要写companion object 来包含静态成员

暂时能做到的方式就是避免 @Provides 不要写在抽象类里 把@Binds标记的抽象方法 写在另一个类里 然后 再用非抽象类 include 进来 如下
 */

@Module
abstract class ActivityModule {



     @Binds
     @ActivityContext
     abstract fun bindActivityContext(activity: Activity): Context



    @ContributesAndroidInjector(modules = arrayOf(UserModule::class))
    @PerActivity
    abstract   fun contributeUserActivitytInjector():UserListActivity

    @ContributesAndroidInjector(modules = arrayOf(UserModule::class))
    @PerActivity
    abstract   fun  contributeBindUserActivitytInjector():UserListDataBindActivity

    @ContributesAndroidInjector(modules = arrayOf(LoginModule::class))
    @PerActivity
    abstract   fun  contributeLoginActivityInjector():LoginActivity

    @ContributesAndroidInjector(modules = arrayOf(StepModule::class, StepModule.StepModuleFragmentManagerModule::class))
    @PerActivity
    abstract   fun contributeStepActivitytInjector():SteperActivity

    @ContributesAndroidInjector(modules = arrayOf(AuthModule::class))
    @PerActivity
    abstract  fun contributeAuthActivitytInjector():AuthActivity


    @ContributesAndroidInjector(modules = arrayOf(CoroutinedDemoModule::class))
    @PerActivity
    abstract fun contributeCoroutinesActivityInjector():CoroutinesDemo
//    @ContributesAndroidInjector
//    @PerActivity
//    abstract fun contributeCoroutinesActivityInjector():CoroutinesDemo

//    @Module(includes = arrayOf(ActivityModule::class))
//     class   ActivityContextModule{
//        @Provides
//        @ActivityContext
//        fun    getActivityContext(activity: Activity):Context{
//            return   activity
//        }
//     }

//    @Module(includes = arrayOf(ActivityModule ::class))
//    class SupportFragmentManagerModule {
//
//        @Provides
//        @PerActivity
//        fun provideFragmentManager(activity: AppCompatActivity):FragmentManager{
//            return activity.supportFragmentManager
//        }
//    }


//    companion object {
//
//     @Provides
//     @PerActivity
//     fun activityFragmentManager(activity: AppCompatActivity): FragmentManager {
//        return activity.supportFragmentManager
//     }
//    }


//    @Provides
//    @PerActivity
//    fun activityFragmentManager(activity: AppCompatActivity): FragmentManager {
//        return activity.supportFragmentManager
//    }
}