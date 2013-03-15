
package com.aokp.romcontrol.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.util.CMDProcessor;
import com.aokp.romcontrol.util.Helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class UserInterface extends AOKPPreferenceFragment implements OnPreferenceChangeListener {
    public final String TAG = getClass().getSimpleName();
    private static final boolean DEBUG = false;

    private static final CharSequence PREF_180 = "rotate_180";
    private static final CharSequence PREF_STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";
    private static final CharSequence PREF_CUSTOM_CARRIER_LABEL = "custom_carrier_label";
    private static final CharSequence PREF_SHOW_OVERFLOW = "show_overflow";
    private static final CharSequence PREF_VIBRATE_NOTIF_EXPAND = "vibrate_notif_expand";
    private static final CharSequence PREF_RAM_USAGE_BAR = "ram_usage_bar";
    private static final CharSequence PREF_IME_SWITCHER = "ime_switcher";
    private static final CharSequence PREF_STATUSBAR_BRIGHTNESS = "statusbar_brightness_slider";
    private static final CharSequence PREF_USER_MODE_UI = "user_mode_ui";
    private static final CharSequence PREF_HIDE_EXTRAS = "hide_extras";
    private static final CharSequence PREF_WAKEUP_WHEN_PLUGGED_UNPLUGGED = "wakeup_when_plugged_unplugged";
    private static final CharSequence PREF_NOTIFICATION_VIBRATE = "notification";
    private static final CharSequence PREF_MISC = "misc";

    private static final int REQUEST_PICK_WALLPAPER = 201;
    //private static final int REQUEST_PICK_CUSTOM_ICON = 202; //unused

    private static final String WALLPAPER_NAME = "notification_wallpaper.jpg";

    CheckBoxPreference mAllow180Rotation;
    CheckBoxPreference mStatusBarNotifCount;
    Preference mCustomLabel;
    ImageView mView;
    TextView mError;
    CheckBoxPreference mShowActionOverflow;
    CheckBoxPreference mVibrateOnExpand;
    CheckBoxPreference mRamBar;
    CheckBoxPreference mShowImeSwitcher;
    CheckBoxPreference mStatusbarSliderPreference;
    AlertDialog mCustomBootAnimationDialog;
    ListPreference mUserModeUI;
    CheckBoxPreference mHideExtras;
    CheckBoxPreference mWakeUpWhenPluggedOrUnplugged;

    private CMDProcessor mCMDProcessor = new CMDProcessor();
    private static ContentResolver mContentResolver;

    String mCustomLabelText = null;
    int mUserRotationAngles = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_ui);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_ui);

        //debug?
        mCMDProcessor.setLogcatDebugging(DEBUG);

        mContentResolver = getContentResolver();

        mAllow180Rotation = (CheckBoxPreference) findPreference(PREF_180);
        mUserRotationAngles = Settings.System.getInt(mContentResolver,
                Settings.System.ACCELEROMETER_ROTATION_ANGLES, -1);
        if (mUserRotationAngles < 0) {
            // Not set by user so use these defaults
            boolean mAllowAllRotations = mContext.getResources().getBoolean(
                            com.android.internal.R.bool.config_allowAllRotations) ? true : false;
            mUserRotationAngles = mAllowAllRotations  ?
                (1 | 2 | 4 | 8) : // All angles
                (1 | 2 | 8); // All except 180
        }
        mAllow180Rotation.setChecked(mUserRotationAngles == (1 | 2 | 4 | 8));

        mStatusBarNotifCount = (CheckBoxPreference) findPreference(PREF_STATUS_BAR_NOTIF_COUNT);
        mStatusBarNotifCount.setChecked(Settings.System.getBoolean(mContentResolver,
                Settings.System.STATUSBAR_NOTIF_COUNT, false));

        mCustomLabel = findPreference(PREF_CUSTOM_CARRIER_LABEL);
        updateCustomLabelTextSummary();

        mShowImeSwitcher = (CheckBoxPreference) findPreference(PREF_IME_SWITCHER);
        mShowImeSwitcher.setChecked(Settings.System.getBoolean(mContentResolver,
                Settings.System.SHOW_STATUSBAR_IME_SWITCHER, true));

        mStatusbarSliderPreference = (CheckBoxPreference) findPreference(PREF_STATUSBAR_BRIGHTNESS);
        mStatusbarSliderPreference.setChecked(Settings.System.getBoolean(mContentResolver,
                Settings.System.STATUSBAR_BRIGHTNESS_SLIDER, true));

        mVibrateOnExpand = (CheckBoxPreference) findPreference(PREF_VIBRATE_NOTIF_EXPAND);
        mVibrateOnExpand.setChecked(Settings.System.getBoolean(mContentResolver,
                Settings.System.VIBRATE_NOTIF_EXPAND, true));
        if (!hasVibration) {
            ((PreferenceGroup)findPreference(PREF_NOTIFICATION_VIBRATE)).removePreference(mVibrateOnExpand);
        }

        mRamBar = (CheckBoxPreference) findPreference(PREF_RAM_USAGE_BAR);
        mRamBar.setChecked(Settings.System.getBoolean(mContentResolver,
                Settings.System.RAM_USAGE_BAR, false));

        mHideExtras = (CheckBoxPreference) findPreference(PREF_HIDE_EXTRAS);
        mHideExtras.setChecked(Settings.System.getBoolean(mContentResolver,
                        Settings.System.HIDE_EXTRAS_SYSTEM_BAR, false));

        mShowActionOverflow = (CheckBoxPreference) findPreference(PREF_SHOW_OVERFLOW);
        mShowActionOverflow.setChecked(Settings.System.getBoolean(mContentResolver,
                        Settings.System.UI_FORCE_OVERFLOW_BUTTON, false));

        mUserModeUI = (ListPreference) findPreference(PREF_USER_MODE_UI);
        int uiMode = Settings.System.getInt(mContentResolver,
                Settings.System.CURRENT_UI_MODE, 0);
        mUserModeUI.setValue(Integer.toString(Settings.System.getInt(mContentResolver,
                Settings.System.USER_UI_MODE, uiMode)));
        mUserModeUI.setOnPreferenceChangeListener(this);

        mWakeUpWhenPluggedOrUnplugged = (CheckBoxPreference) findPreference(PREF_WAKEUP_WHEN_PLUGGED_UNPLUGGED);
        mWakeUpWhenPluggedOrUnplugged.setChecked(Settings.System.getBoolean(mContentResolver,
                        Settings.System.WAKEUP_WHEN_PLUGGED_UNPLUGGED, true));

        // hide option if device is already set to never wake up
        if(!mContext.getResources().getBoolean(
                com.android.internal.R.bool.config_unplugTurnsOnScreen)) {
            ((PreferenceGroup) findPreference(PREF_MISC)).removePreference(mWakeUpWhenPluggedOrUnplugged);
        }

        setHasOptionsMenu(true);
    }

    private void updateCustomLabelTextSummary() {
        mCustomLabelText = Settings.System.getString(mContentResolver,
                Settings.System.CUSTOM_CARRIER_LABEL);
        if (mCustomLabelText == null || mCustomLabelText.isEmpty()) {
            mCustomLabel.setSummary(R.string.custom_carrier_label_notset);
        } else {
            mCustomLabel.setSummary(mCustomLabelText);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mAllow180Rotation) {
            boolean checked = ((TwoStatePreference) preference).isChecked();
            Settings.System.putInt(mContentResolver,
                    Settings.System.ACCELEROMETER_ROTATION_ANGLES,
                    checked ? (1 | 2 | 4 | 8) : (1 | 2 | 8));
            return true;
        } else if (preference == mStatusBarNotifCount) {
            Settings.System.putBoolean(mContentResolver,
                    Settings.System.STATUSBAR_NOTIF_COUNT,
                    ((TwoStatePreference) preference).isChecked());
            return true;
        } else if (preference == mHideExtras) {
            Settings.System.putBoolean(mContentResolver,
                    Settings.System.HIDE_EXTRAS_SYSTEM_BAR,
                    ((TwoStatePreference) preference).isChecked());
            return true;
        } else if (preference == mShowActionOverflow) {
            boolean enabled = mShowActionOverflow.isChecked();
            Settings.System.putBoolean(mContentResolver, Settings.System.UI_FORCE_OVERFLOW_BUTTON,
                    enabled);
            // Show toast appropriately
            if (enabled) {
                Toast.makeText(getActivity(), R.string.show_overflow_toast_enable,
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), R.string.show_overflow_toast_disable,
                        Toast.LENGTH_LONG).show();
            }
            return true;
        } else if (preference == mShowImeSwitcher) {
            Settings.System.putBoolean(mContentResolver,
                    Settings.System.SHOW_STATUSBAR_IME_SWITCHER,
                    isCheckBoxPrefernceChecked(preference));
            return true;
        } else if (preference == mStatusbarSliderPreference) {
            Settings.System.putBoolean(mContentResolver,
                    Settings.System.STATUSBAR_BRIGHTNESS_SLIDER,
                    isCheckBoxPrefernceChecked(preference));
            return true;
        } else if (preference == mCustomLabel) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.custom_carrier_label_title);
            alert.setMessage(R.string.custom_carrier_label_explain);

            // Set an EditText mView to get user input
            final EditText input = new EditText(getActivity());
            input.setText(mCustomLabelText != null ? mCustomLabelText : "");
            alert.setView(input);
            alert.setPositiveButton(getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = input.getText().toString();
                    Settings.System.putString(mContentResolver,
                            Settings.System.CUSTOM_CARRIER_LABEL, value);
                    updateCustomLabelTextSummary();
                    Intent i = new Intent();
                    i.setAction("com.aokp.romcontrol.LABEL_CHANGED");
                    mContext.sendBroadcast(i);
                }
            });
            alert.setNegativeButton(getResources().getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });
            alert.show();
        } else if (preference == mVibrateOnExpand) {
            Settings.System.putBoolean(mContentResolver,
                    Settings.System.VIBRATE_NOTIF_EXPAND,
                    ((TwoStatePreference) preference).isChecked());
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mRamBar) {
            boolean checked = ((TwoStatePreference) preference).isChecked();
            Settings.System.putBoolean(mContentResolver,
                    Settings.System.RAM_USAGE_BAR, checked);
            return true;
        } else if (preference == mWakeUpWhenPluggedOrUnplugged) {
            Settings.System.putBoolean(mContentResolver,
                    Settings.System.WAKEUP_WHEN_PLUGGED_UNPLUGGED,
                    ((TwoStatePreference) preference).isChecked());
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user_interface, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_wallpaper:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mContext.deleteFile(WALLPAPER_NAME);
                        Helpers.restartSystemUI();
                    }
                }).start();
                return true;
            default:
                // call to super is implicit
                return onContextItemSelected(item);
        }
    }

    private Uri getNotificationExternalUri() {
        File dir = mContext.getExternalCacheDir();
        File wallpaper = new File(dir, WALLPAPER_NAME);
        return Uri.fromFile(wallpaper);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_WALLPAPER) {
                FileOutputStream wallpaperStream = null;
                try {
                    wallpaperStream = mContext.openFileOutput(WALLPAPER_NAME,
                            Context.MODE_WORLD_READABLE);
                    Uri selectedImageUri = getNotificationExternalUri();
                    Bitmap bitmap = BitmapFactory.decodeFile(
                            selectedImageUri.getPath());
                    bitmap.compress(Bitmap.CompressFormat.PNG,
                                    100,
                                    wallpaperStream);
                } catch (FileNotFoundException e) {
                    return; // NOOOOO
                } finally {
                    try {
                        if (wallpaperStream != null)
                            wallpaperStream.close();
                    } catch (IOException e) {
                        // let it go
                    }
                }
                Helpers.restartSystemUI();
            }
        }
    }

    public void copy(File src, File dst) throws IOException {
        // use file channels for faster byte transfers
        FileChannel inChannel = new
                FileInputStream(src).getChannel();
        FileChannel outChannel = new
                FileOutputStream(dst).getChannel();
        try {
            // move the bytes from in to out
            inChannel.transferTo(0,
                    inChannel.size(),
                    outChannel);
        } finally {
            // ensure closure
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
       if (preference == mUserModeUI) {
            Settings.System.putInt(mContentResolver,
                    Settings.System.USER_UI_MODE, Integer.parseInt((String) newValue));
            Helpers.restartSystemUI();
            return true;
        }
        return false;
    }
}
