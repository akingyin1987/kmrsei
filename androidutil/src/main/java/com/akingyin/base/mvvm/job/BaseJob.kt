package com.akingyin.base.mvvm.job

import com.akingyin.base.net.Result


abstract class BaseJob<T : Any>  {

  abstract suspend fun  run():Result<T>
}