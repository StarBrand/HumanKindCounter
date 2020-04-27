package cl.humankind.humankindcounter;

import android.os.Bundle;
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
    }

}
