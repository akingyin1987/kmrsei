/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.map.ui

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.akingyin.base.ext.inflate
import com.akingyin.map.R
import kotlinx.android.synthetic.*

/**
 * @ Description:
 * @author king
 * @ Date 2020/5/30 11:38
 * @version V1.0
 */
class MapSettingFragment : PreferenceFragmentCompat() {

    companion object{
        fun   newInstance(sharedPreferencesName:String):MapSettingFragment{
            return MapSettingFragment().apply {
                arguments = Bundle().apply {
                    putString("sharedPreferencesName",sharedPreferencesName)
                }
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        println("key=${rootKey}")
        arguments?.let {
            if(!it.getString("sharedPreferencesName").isNullOrEmpty()){
                preferenceManager.sharedPreferencesName=it.getString("sharedPreferencesName")
            }
        }
        setPreferencesFromResource(R.xml.map_preferences_fragment, rootKey)


        findPreference<EditTextPreference>("map_path_min_time")?.let {
            it.summary = preferenceManager.sharedPreferences.getString("map_path_min_time","3")+"(分)"
            it.setOnBindEditTextListener {
                editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER

            }
        }
        findPreference<EditTextPreference>("map_path_min_dis")?.let {
            it.summary = preferenceManager.sharedPreferences.getString("map_path_min_dis","50")+"(米)"
            it.setOnBindEditTextListener {
                  editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
            it.setOnPreferenceChangeListener { _, newValue ->
                 it.summary = "$newValue(米)"
                true
            }
        }
        findPreference<Preference>("icon")?.let {
            it.setOnPreferenceClickListener {
                 showToast("setOnPreferenceClickListener")
                return@setOnPreferenceClickListener true
            }
        }

        findPreference<EditTextPreference>("myname")?.let {
            it.summary = preferenceManager.sharedPreferences.getString("myname","")
            it.setOnBindEditTextListener {
               editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
            it.setOnPreferenceChangeListener { _, newValue ->
                it.summary = newValue.toString()
                true
            }
        }

        findPreference<ListPreference>("mycity")?.let {
            it.setOnPreferenceChangeListener { _, newValue ->
                it.summary = newValue.toString()
                true
            }
        }
    }


    fun   showToast(msg: String){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()

    }
}