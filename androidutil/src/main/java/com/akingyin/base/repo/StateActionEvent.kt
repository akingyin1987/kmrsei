package com.akingyin.base.repo

/**
 * @ Description:
 * @author king
 * @ Date 2020/5/15 16:05
 * @version V1.0
 */
sealed class StateActionEvent {

    object LoadState : StateActionEvent()

    object SuccessState : StateActionEvent()

    class  SuccessInfoState(val  message: String?):StateActionEvent()

    class ErrorState(val message: String?) : StateActionEvent()
}