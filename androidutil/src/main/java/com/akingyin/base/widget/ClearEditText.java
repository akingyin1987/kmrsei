/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.base.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import androidx.appcompat.widget.AppCompatEditText;
import com.akingyin.base.R;

/**
 * 输入清空数据的小组件
 * @author king
 *
 */
public class ClearEditText extends AppCompatEditText implements
        OnFocusChangeListener, TextWatcher { 
	
    private Drawable mClearDrawable;

    private OnPasteCallback mOnPasteCallback;

  // 是否点击了粘贴
  private boolean isClickPaste;

  public OnPasteCallback getOnPasteCallback() {
    return mOnPasteCallback;
  }

  public void setOnPasteCallback(OnPasteCallback onPasteCallback) {
    mOnPasteCallback = onPasteCallback;
  }

  private boolean hasFoucs;
 
    public ClearEditText(Context context) { 
    	this(context, null); 
    } 
 
    public ClearEditText(Context context, AttributeSet attrs) { 
    	
    	this(context, attrs, android.R.attr.editTextStyle); 
    } 
    
    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    
    private void init() { 
    
    	mClearDrawable = getCompoundDrawables()[2]; 
        if (mClearDrawable == null) { 
//        	
        	mClearDrawable = getResources().getDrawable(R.drawable.delete_selector);
        } 
        
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight()); 
       
        setClearIconVisible(false); 
       
        setOnFocusChangeListener(this); 
       
        addTextChangedListener(this); 
    } 
 
 
   
    @SuppressLint("ClickableViewAccessibility")
    @Override 
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (getCompoundDrawables()[2] != null) {

				boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
						&& (event.getX() < ((getWidth() - getPaddingRight()))) && isEnabled();
				
				if (touchable) {
					this.setText("");
				}
			}
		}
      try {
        return super.onTouchEvent(event);
      } catch (IllegalArgumentException ex) {
        ex.printStackTrace();
      }
      return false;

	}
 
   
    @Override 
    public void onFocusChange(View v, boolean hasFocus) { 
    	this.hasFoucs = hasFocus;
        if (hasFocus) { 
            setClearIconVisible(getText().length() > 0 && isEnabled());
        } else { 
            setClearIconVisible(false); 
        } 
    } 
 
 
  
    protected void setClearIconVisible(boolean visible) { 
        Drawable right = visible ? mClearDrawable : null; 
        setCompoundDrawables(getCompoundDrawables()[0], 
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]); 
    } 
    
    
  
    @Override 
    public void onTextChanged(CharSequence s, int start, int count, 
            int after) { 
            	if(hasFoucs){
            		setClearIconVisible(s.length() > 0);
            	}
    } 
 
    @Override 
    public void beforeTextChanged(CharSequence s, int start, int count, 
            int after) { 
         
    } 
 
    @Override 
    public void afterTextChanged(Editable s) { 
         if(isClickPaste){
           isClickPaste = false;
           if(null != mOnPasteCallback){
             mOnPasteCallback.onPaste();
           }
         }
    } 
    
   
   
    public void setShakeAnimation(){
    	this.setAnimation(shakeAnimation(5));
    }
    
    
   
    public static Animation shakeAnimation(int counts){
    	Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
    	translateAnimation.setInterpolator(new CycleInterpolator(counts));
    	translateAnimation.setDuration(1000);
    	return translateAnimation;
    }

  @Override public boolean onTextContextMenuItem(int id) {
    switch (id) {
      case android.R.id.cut:
        // 剪切

        break;
      case android.R.id.copy:
        // 复制

        break;
      case android.R.id.paste:
        // 粘贴
        // 是否点击了粘贴
         isClickPaste = true;
         break;
         default:

    }
    return super.onTextContextMenuItem(id);
  }

  public interface OnPasteCallback {


    void onPaste();


  }

}
