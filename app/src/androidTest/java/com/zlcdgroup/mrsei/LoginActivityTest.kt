package com.zlcdgroup.mrsei

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.zlcdgroup.mrsei.ui.LoginActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @ Description:
 * @author king
 * @ Date 2019/5/10 10:42
 * @version V1.0
 */

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @Rule
    @JvmField
    var activityRule = ActivityTestRule<LoginActivity>(LoginActivity::class.java)


    private val username = "chike"
    private val password = "password"

    @Test
    fun clickLoginButton_opensLoginUi() {
        onView(withId(R.id.et_mobile)).perform(ViewActions.typeText(username))
        onView(withId(R.id.et_password)).perform(ViewActions.typeText(password))

        onView(withId(R.id.btn_login)).perform( ViewActions.click())

//        Espresso.onView(withId(R.id.btn_login))
//                .check(matches(withText("Success")))

    }
}