package com.aokp.romcontrol.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;

public class Lockscreens extends AOKPPreferenceFragment {

    private static final String PREF_VOLUME_ROCKER_WAKE = "volume_rocker_wake";
    private static final String PREF_VOLUME_MUSIC = "volume_music_controls";
    private static final String PREF_QUICK_UNLOCK = "lockscreen_quick_unlock_control";
    private static final String PREF_LOCKSCREEN_AUTO_ROTATE = "lockscreen_auto_rotate";
    private static final String PREF_LOCKSCREEN_BATTERY = "lockscreen_battery";
    private static final String PREF_LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS = "lockscreen_hide_initial_page_hints";
    private static final String PREF_LOCKSCREEN_MINIMIZE_CHALLENGE = "lockscreen_minimize_challenge";
    private static final String PREF_LOCKSCREEN_USE_CAROUSEL = "lockscreen_use_widget_container_carousel";
    private static final String PREF_LOCKSCREEN_LONGPRESS_CHALLENGE = "lockscreen_longpress_challenge";

    public static final int REQUEST_PICK_CUSTOM_ICON = 200;
    public static final int SELECT_ACTIVITY = 2;

    Preference mLockscreenTargets;

    CheckBoxPreference mVolumeMusic;
    CheckBoxPreference mVolumeRockerWake;
    CheckBoxPreference mQuickUnlock;
    CheckBoxPreference mLockscreenBattery;
    CheckBoxPreference mLockscreenAutoRotate;
    CheckBoxPreference mLockscreenHideInitialPageHints;
    CheckBoxPreference mLockscreenMinChallenge;
    CheckBoxPreference mLockscreenUseCarousel;
    CheckBoxPreference mLockscreenLongpressChallenge;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_lockscreens);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_lockscreens);

        mVolumeRockerWake = (CheckBoxPreference) findPreference(PREF_VOLUME_ROCKER_WAKE);
        mVolumeRockerWake.setChecked(Settings.System.getBoolean(mContext
                .getContentResolver(), Settings.System.VOLUME_WAKE_SCREEN, false));

        mVolumeMusic = (CheckBoxPreference) findPreference(PREF_VOLUME_MUSIC);
        mVolumeMusic.setChecked(Settings.System.getBoolean(mContext.getContentResolver(),
                Settings.System.VOLUME_MUSIC_CONTROLS, false));

        mQuickUnlock = (CheckBoxPreference) findPreference(PREF_QUICK_UNLOCK);
        mQuickUnlock.setChecked(Settings.System.getBoolean(mContext.getContentResolver(),
                Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL, false));

        mLockscreenAutoRotate = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_AUTO_ROTATE);
        mLockscreenAutoRotate.setChecked(Settings.System.getBoolean(mContext
                .getContentResolver(), Settings.System.LOCKSCREEN_AUTO_ROTATE, false));

        mLockscreenBattery = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_BATTERY);
        mLockscreenBattery.setChecked(Settings.System.getBoolean(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_BATTERY, false));

        mLockscreenHideInitialPageHints = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS);
        mLockscreenHideInitialPageHints.setChecked(Settings.System.getBoolean(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS, false));

        mLockscreenMinChallenge = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_MINIMIZE_CHALLENGE);
        mLockscreenMinChallenge.setChecked(Settings.System.getBoolean(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_MINIMIZE_LOCKSCREEN_CHALLENGE, false));

        mLockscreenUseCarousel = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_USE_CAROUSEL);
        mLockscreenUseCarousel.setChecked(Settings.System.getBoolean(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_USE_WIDGET_CONTAINER_CAROUSEL, false));

        mLockscreenLongpressChallenge = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_LONGPRESS_CHALLENGE);
        mLockscreenLongpressChallenge.setChecked(Settings.System.getBoolean(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_LONGPRESS_CHALLENGE, false));

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mVolumeRockerWake) {
            Settings.System.putBoolean(mContext.getContentResolver(),
                    Settings.System.VOLUME_WAKE_SCREEN,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mVolumeMusic) {
            Settings.System.putBoolean(mContext.getContentResolver(),
                    Settings.System.VOLUME_MUSIC_CONTROLS,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mQuickUnlock) {
            Settings.System.putBoolean(mContext.getContentResolver(),
                    Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mLockscreenBattery) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_BATTERY,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mLockscreenMinChallenge) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_MINIMIZE_LOCKSCREEN_CHALLENGE,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mLockscreenLongpressChallenge) {
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_LONGPRESS_CHALLENGE,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        } else if (preference == mLockscreenAutoRotate) {
            Settings.System.putBoolean(mContext.getContentResolver(),
                    Settings.System.LOCKSCREEN_AUTO_ROTATE,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mLockscreenHideInitialPageHints) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mLockscreenUseCarousel) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_USE_WIDGET_CONTAINER_CAROUSEL,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

}

