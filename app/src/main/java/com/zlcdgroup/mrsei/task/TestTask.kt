package com.zlcdgroup.mrsei.task

import com.akingyin.base.taskmanager.AbsTaskRunner
import com.akingyin.base.taskmanager.enums.TaskStatusEnum

/**
 * @ Description:
 * @author king
 * @ Date 2019/12/20 11:43
 * @version V1.0
 */
class TestTask(var tagInfo:String) : AbsTaskRunner() {




    override fun onBefore(): TaskStatusEnum {
        println("onBefore=${tagInfo}")
        return  TaskStatusEnum.SUCCESS
    }

    override fun onToDo() {
        println("start to Do $tagInfo")
        Thread.sleep(10)
        println("end to Do $tagInfo")
        TaskOnSuccess()
    }
}