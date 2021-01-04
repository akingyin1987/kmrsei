package com.akingyin.base.mvvm.job

import com.akingyin.base.net.mode.Result

abstract class BaseJob<T : Any>  {

  abstract suspend fun  run():Result<T>
}