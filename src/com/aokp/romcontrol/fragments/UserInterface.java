
package com.aokp.romcontrol.fragments;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;

public class UserInterface extends AOKPPreferenceFragment {
    public final String TAG = getClass().getSimpleName();

    private static final CharSequence PREF_STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";
    private static final CharSequence PREF_CUSTOM_CARRIER_LABEL = "custom_carrier_label";

    CheckBoxPreference mStatusBarNotifCount;
    Preference mCustomLabel;
    ImageView mView;
    TextView mError;
    

    private static ContentResolver mContentResolver;

    String mCustomLabelText = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_ui);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_ui);

        mContentResolver = getContentResolver();

        mStatusBarNotifCount = (CheckBoxPreference) findPreference(PREF_STATUS_BAR_NOTIF_COUNT);
        mStatusBarNotifCount.setChecked(Settings.System.getBoolean(mContentResolver,
                Settings.System.STATUSBAR_NOTIF_COUNT, false));

        mCustomLabel = findPreference(PREF_CUSTOM_CARRIER_LABEL);
        updateCustomLabelTextSummary();

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
        if (preference == mStatusBarNotifCount) {
            Settings.System.putBoolean(mContentResolver,
                    Settings.System.STATUSBAR_NOTIF_COUNT,
                    ((TwoStatePreference) preference).isChecked());
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
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

}
