/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.img.multimedia;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.zlcdgroup.imagebundle.R;


public class MulitimediaSettingFragment extends PreferenceFragment {

    ListPreference   camera_key,video_key,audio_key;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("mulitimedia_setting");
        addPreferencesFromResource(R.xml.mulitimedia_setting);

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

            }
        });
        camera_key = (ListPreference) getPreferenceScreen().findPreference("camera_key");
        camera_key.setSummary(camera_key.getEntry());
        audio_key = (ListPreference)getPreferenceScreen().findPreference("audio_key");
        audio_key.setSummary(audio_key.getEntry());
        audio_key.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int index = audio_key.findIndexOfValue(newValue.toString());
                //
                //if(index>=0){
                //    audio_key.setValueIndex(index);
                //}
                CharSequence[] entries=audio_key.getEntries();

                audio_key.setSummary(entries[index]);

                return true;
            }
        });
        video_key = (ListPreference) getPreferenceScreen().findPreference("video_key");
        video_key.setSummary(video_key.getEntry());
        video_key.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int index = video_key.findIndexOfValue(newValue.toString());
                //
                //if(index>=0){
                //    video_key.setValueIndex(index);
                //}
                CharSequence[] entries=video_key.getEntries();
                video_key.setSummary(entries[index]);
                return true;
            }
        });
        camera_key.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override public boolean onPreferenceChange(Preference preference, Object o) {

                int index = camera_key.findIndexOfValue(o.toString());
                CharSequence[] entries=camera_key.getEntries();
                //if(index>=0){
                //    camera_key.setValueIndex(index);
                //}

                camera_key.setSummary(entries[index]);
                return true;
            }
        });

    }

}
