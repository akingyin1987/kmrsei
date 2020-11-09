package com.akingyin.fitter.typeview

import android.content.Context
import android.util.AttributeSet

import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.akingyin.base.ext.verifyLiveDataNotEmpty
import com.akingyin.fitter.R
import com.akingyin.fitter.adapter.SimpleTextAdapter



/**
 * Created by baiiu on 15/12/17.
 * 双列ListView
 */
class DoubleListView<LEFTD, RIGHTD> @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr){



    lateinit var mLeftAdapter: SimpleTextAdapter<LEFTD>
    lateinit var mRightAdapter: SimpleTextAdapter<RIGHTD>

    /**左边被选中项 */
    private  var  leftSelectPostion = MutableLiveData(0)

    /** 右边被选中项 */
    private  var  rightSelectPostion = MutableLiveData(0)
     var leftListView: RecyclerView
     var rightListView: RecyclerView




    init {
        orientation = VERTICAL
        inflate(context, R.layout.merge_filter_list, this)
        leftListView = findViewById(R.id.lv_left)
        rightListView = findViewById(R.id.lv_right)

    }

    /**
     *
     * @param leftAdapter SimpleTextAdapter<LEFTD>
     * @return DoubleListView<LEFTD, RIGHTD>
     */
    fun leftAdapter(leftAdapter: SimpleTextAdapter<LEFTD>): DoubleListView<LEFTD, RIGHTD> {
        mLeftAdapter = leftAdapter
        leftListView.adapter = leftAdapter
        leftAdapter.setOnItemClickListener { _, _, position ->
            leftSelectPostion.value = position
            mOnLeftItemClickListener?.let {
                val item: LEFTD = mLeftAdapter.getItem(position)
                val rightds = it.provideRightList(item, position)
                mRightAdapter.setList(rightds)
                if(rightds.isEmpty()){
                    leftSelectPostion.value = -1
                }

            }

            leftAdapter.setItemChecked(position)
        }
        return this
    }

    fun rightAdapter(rightAdapter: SimpleTextAdapter<RIGHTD>): DoubleListView<LEFTD, RIGHTD> {
        mRightAdapter = rightAdapter
        rightListView.adapter = rightAdapter
        mRightAdapter.setOnItemClickListener { _, _, position ->
            rightSelectPostion.value = position
            rightAdapter.setItemChecked(position)
            mOnRightItemClickListener?.onRightItemClick(mLeftAdapter.getItem(leftSelectPostion.verifyLiveDataNotEmpty()),mRightAdapter.getItem(position))
        }
        return this
    }

    fun setLeftList(list: List<LEFTD>, checkedPosition: Int) {
        mLeftAdapter.setList(list)
        leftSelectPostion.value = checkedPosition
        if (checkedPosition != -1) {

            leftListView.scrollToPosition(checkedPosition)
            mLeftAdapter.setItemChecked(checkedPosition)
        }
    }

    fun setRightList(list: List<RIGHTD>, checkedPosition: Int) {
        mRightAdapter.setList(list)
        rightSelectPostion.value = checkedPosition
        if (checkedPosition != -1) {
            rightListView.scrollToPosition(checkedPosition)
            mRightAdapter.setItemChecked(checkedPosition)
        }
    }

    private var mOnLeftItemClickListener: OnLeftItemClickListener<LEFTD, RIGHTD>? = null
    private var mOnRightItemClickListener: OnRightItemClickListener<LEFTD, RIGHTD>? = null

    interface OnLeftItemClickListener<LEFTD, RIGHTD> {
        fun provideRightList(leftAdapter: LEFTD, position: Int): List<RIGHTD>
    }

    interface OnRightItemClickListener<LEFTD, RIGHTD> {
        fun onRightItemClick(item: LEFTD, childItem: RIGHTD)
    }

    fun onLeftItemClickListener(onLeftItemClickListener: OnLeftItemClickListener<LEFTD, RIGHTD>): DoubleListView<LEFTD, RIGHTD> {
        mOnLeftItemClickListener = onLeftItemClickListener
        return this
    }

    fun onRightItemClickListener(onRightItemClickListener: OnRightItemClickListener<LEFTD, RIGHTD>): DoubleListView<LEFTD, RIGHTD> {
        mOnRightItemClickListener = onRightItemClickListener
        return this
    }



}