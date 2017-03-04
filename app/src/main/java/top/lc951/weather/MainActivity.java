package top.lc951.weather;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSharedPreferences();
    }

    private void initSharedPreferences() {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString(WeatherActivity.WEATHER,null)!=null
                ) {
            WeatherActivity.actionStart(this);
            finish();
        }
    }
}
