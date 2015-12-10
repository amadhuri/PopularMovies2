package com.example.android.popularmovies2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class UserSettingActivity extends Activity {

   private static final String TAG = UserSettingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getBoolean(R.bool.landscape_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setContentView(R.layout.activity_user_setting);
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREF_NAME,0);
        String value = sharedPreferences.getString(MainActivity.SHARED_PREF_KEY,getString(R.string.most_popular_value));
        RadioGroup settingsRadioGroup = (RadioGroup)findViewById(R.id.settings_radio_group);

        if( value.compareTo(getString(R.string.favorites_value)) == 0 ) {
           settingsRadioGroup.check(R.id.favorites);
        } else if (value.compareTo(getString(R.string.highest_rated_value)) == 0) {
            settingsRadioGroup.check(R.id.highest_rated);
        } else {
           settingsRadioGroup.check(R.id.most_popular);
        }
    }

    public void onRadioButtonClicked(View v) {
        boolean checked = ((RadioButton) v).isChecked();
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREF_NAME,0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String value = sharedPreferences.getString(MainActivity.SHARED_PREF_KEY,getString(R.string.most_popular_value));
        Log.e(TAG,"sharedPref value:"+value );

        //noinspection SimplifiableIfStatement
        switch(v.getId()){
            case R.id.highest_rated:
                value = getString(R.string.highest_rated_value);
                break;
            case R.id.most_popular:
                value = getString(R.string.most_popular_value);
                break;
            case R.id.favorites:
                value = getString(R.string.favorites_value);
                break;
        }

        editor.putString(MainActivity.SHARED_PREF_KEY,value);
        editor.commit();
        Log.e(TAG,"sharedPref setting value:"+value );
        finish();
        return;
    }
}





