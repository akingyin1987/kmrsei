package com.zlcdgroup.mrsei.ui


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.zlcdgroup.mrsei.R
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/5/14 16:26
 */

@RunWith(AndroidJUnit4::class)
class SteperActivityTest {

    @Rule
    @JvmField
    var activityRule = ActivityTestRule<SteperActivity>(SteperActivity::class.java)


    @Before
    fun setUp() {

    }


    @Test
    fun   addUser(){
      onView(allOf(withId(R.id.fab), isDisplayed())).perform(ViewActions.click())
      onView(withId(R.id.edit_name)).perform(ViewActions.typeText(""))
      onView(withId(R.id.edit_age)).perform(ViewActions.typeText("1"))
      onView(withText("确定")).perform(ViewActions.click())
     // onView(withResourceName("确定")).check(matches(withText("保存成功")))
      //onView(withText("确定")).check(matches(withText("确定")))
    }
}