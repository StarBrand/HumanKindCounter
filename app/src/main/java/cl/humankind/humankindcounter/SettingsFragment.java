package cl.humankind.humankindcounter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat{

    public SettingsFragment(){
        // This constructor is needed
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        Preference numEd = findPreference("num_ed");
        if (numEd != null) numEd.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Toast.makeText(getContext(), R.string.no_implemented,
                                Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
        Preference facEd = findPreference("fac_ed");
        if (facEd != null) facEd.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Toast.makeText(getContext(), R.string.no_implemented,
                                Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
        Preference about = findPreference("about");
        if (about != null) about.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        LayoutInflater inflater;
                        Activity main = getActivity();
                        if (main == null) return false;
                        inflater = (LayoutInflater) main.getSystemService(
                                Context.LAYOUT_INFLATER_SERVICE);
                        View popup = inflater.inflate(R.layout.about_info, null);
                        if (popup == null) return false;
                        final PopupWindow  about_popup = new PopupWindow(
                                popup, LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT, true
                        );
                        about_popup.showAtLocation(popup, Gravity.CENTER, 0, 0);
                        Button ok_button = popup.findViewById(R.id.ok_button);
                        ok_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                about_popup.dismiss();
                            }
                        });
                        return true;
                    }
                });
    }

}
