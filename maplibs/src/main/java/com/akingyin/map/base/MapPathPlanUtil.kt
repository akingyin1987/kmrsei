/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.map.base

import com.akingyin.map.IMarker
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.utils.DistanceUtil
import java.util.*

/**
 * @ Description:
 * @author king
 * @ Date 2020/5/26 17:37
 * @version V1.0
 */
object MapPathPlanUtil {

    const val MIN_MARKER_COUNT = 3

    const val MIN_CALCULATION_COUNT = 5

    const val MAX_MARKER_COUNT = 20

    /** 最短距离 */
    const val  MIN_SHORTEST_DIS=2.0

    /**
     * 通过当前位置计算最优路径
     */
    fun<T: IMarker> getOptimalPathPlan(iMarkerModels: MutableList<T>, currentLatlnt: LatLng): List<T> {
        try {
            val iterable = iMarkerModels.iterator()
            val tempcompleteMarkers: MutableList<T> = ArrayList()
            while (iterable.hasNext()) {
                val planModel = iterable.next()
                //去掉已完成的
                if (planModel.isComplete()) {
                    iterable.remove()
                    tempcompleteMarkers.add(planModel)
                }
            }
            val pathPlanModelList = getCalculatDistance(iMarkerModels, currentLatlnt)

            //当前数，小于指定量则不提供建议路径
            if (iMarkerModels.size <= MIN_MARKER_COUNT) {
//                val iMarkerModelList: MutableList<T> = LinkedList()
//                for (planModel in pathPlanModelList) {
//                    planModel.iMarkerModel?.let {
//                        iMarkerModelList.add(it)
//                    }
//
//                }
//                iMarkerModelList.addAll(0, tempcompleteMarkers)
                return iMarkerModels
            }
//            if (pathPlanModelList.size > MAX_MARKER_COUNT) {
//               pathPlanModelList = pathPlanModelList.toMutableList().subList(0, MAX_MARKER_COUNT).toList()
//            }
            //待计算的点
            val pathPlanModels: MutableList<PathPlanModel<T>> = LinkedList()
            //已算好的点
            val sortPathPlanModels: MutableList<PathPlanModel<T>> = LinkedList()
            pathPlanModels.addAll(pathPlanModelList)
            sortPathPlanModels.add(pathPlanModelList[0])
            pathPlanModels.removeAt(0)
            //以最近的一个为启点
            while (pathPlanModels.size > 0 &&  sortPathPlanModels.size< MAX_MARKER_COUNT) {
                //每次以计算好的最后一个点为起点
                val fristpPlanModel = sortPathPlanModels[sortPathPlanModels.size - 1]
                val nextPlanModel = getLatelyDistance(fristpPlanModel, pathPlanModels)

                nextPlanModel?.let {
                    sortPathPlanModels.add(it)
                    pathPlanModels.remove(it)
                }


                ////第一次确定启点
                //List<PathPlanModel>   pathPlanModels1 = getOptimumNext(fristpPlanModel, pathPlanModels);
                //
                //if (pathPlanModels1.size() > 0) {
                //  pathPlanModels1.remove(0);
                //}
                //if (pathPlanModels1.size() == 0) {
                //  System.out.println("break");
                //  break;
                //}
                //if (pathPlanModels1.size() > 0) {
                //  sortPathPlanModels.addAll(pathPlanModels1);
                //  pathPlanModels.removeAll(pathPlanModels1);
                //}
            }
           return  sortPathPlanModels.map {
                 println("返回结果->>>${DistanceUtil.getDistance(currentLatlnt,it.latLng)}  uuid=${it.iMarkerModel?.uuid}")

                it.iMarkerModel!!
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return iMarkerModels
    }


    /**
     * 计算当前点与所有marker距离并按重近到远排序
     */
    fun<T : IMarker> getCalculatDistance(iMarkerModels: List<T>, latLng: LatLng): List<PathPlanModel<T>> {

        return iMarkerModels.map {
            val d = DistanceUtil.getDistance(latLng, LatLng(it.getLat(), it.getLng())).toFloat()

            PathPlanModel(it,d)
        }.sortedBy {
            it.distance
        }
    }

    /**
     * 获取最近距离点 且最小距离超过 MIN
     */
    fun<T:IMarker> getLatelyDistance(pathPlanModel: PathPlanModel<T>,
                          pathPlanModelList: List<PathPlanModel<T>>): PathPlanModel<T>? {
        var dis = 0f
        var planModel: PathPlanModel<T>? = null
        val latLng = pathPlanModel.latLng
        for (model in pathPlanModelList) {
            if (pathPlanModel !== model) {
                val d = DistanceUtil.getDistance(latLng, model.latLng).toFloat()
                if(d > MIN_SHORTEST_DIS){
                    if (dis == 0f ) {
                        dis = d
                        planModel = model
                    }
                    if (d < dis) {
                        dis = d
                        planModel = model
                    }
                }

            }
        }
        if (null == planModel) {
            return null
        }

        planModel.accumulateDis = dis
        return planModel
    }

    class PathPlanModel<T :IMarker> {
        var iMarkerModel: T? = null
        var distance = 0f

        /** 累积距离  */
        var accumulateDis = 0f
        var sort = 0
        var latLng: LatLng
        var planModels: List<PathPlanModel<T>>? = null



        constructor(IMarkerModel: T, distance: Float) {
            iMarkerModel = IMarkerModel
            this.distance = distance
            this.latLng = LatLng(IMarkerModel.getLat(),IMarkerModel.getLng())
        }

        constructor(latLng: LatLng) {
            this.latLng = latLng
        }

    }
}