package top.lc951.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import top.lc951.weather.gson.Forecast;
import top.lc951.weather.gson.Weather;
import top.lc951.weather.service.AutoupdateService;
import top.lc951.weather.util.HttpUtil;
import top.lc951.weather.util.Utility;

public class WeatherActivity extends AppCompatActivity {

    @InjectView(R.id.title_city)
    TextView titleCity;
    @InjectView(R.id.title_update_time)
    TextView titleUpdateTime;
    @InjectView(R.id.degree_text)
    TextView degreeText;
    @InjectView(R.id.weather_info_txt)
    TextView weatherInfoTxt;
    @InjectView(R.id.forecast_layout)
    LinearLayout forecastLayout;
    @InjectView(R.id.aqi_text)
    TextView aqiText;
    @InjectView(R.id.pm25_text)
    TextView pm25Text;
    @InjectView(R.id.comfort_text)
    TextView comfortText;
    @InjectView(R.id.car_wash_text)
    TextView carWashText;
    @InjectView(R.id.sport_text)
    TextView sportText;
    @InjectView(R.id.weather_layout)
    ScrollView weatherLayout;
    @InjectView(R.id.activity_weather)
    FrameLayout activityWeather;
    public static final String WEATHER = "weather";
    @InjectView(R.id.bing_pic_img)
    ImageView bingPicImg;
    @InjectView(R.id.swip_refresh)
    SwipeRefreshLayout swipRefresh;
    @InjectView(R.id.nav_button)
    Button navButton;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    public static void actionStartForWeatherId(Context context, String weatherId) {
        Intent intent = new Intent(context, WeatherActivity.class);
        intent.putExtra("weather_id", weatherId);
        context.startActivity(intent);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, WeatherActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_weather);
        ButterKnife.inject(this);

        bindViews();
        querySharedPreferences();

    }

    private void bindViews() {
        swipRefresh.setColorSchemeResources(R.color.colorPrimary);

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }


    private void querySharedPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString(WEATHER, null);
        final String weatherId;
        if (!TextUtils.isEmpty(weatherString)) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        setSwipRefreshListener(weatherId);

        loadBingPicImg(prefs);
    }

    private void setSwipRefreshListener(final String weatherId) {
        swipRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
    }

    private void loadBingPicImg(SharedPreferences prefs) {
        if (prefs == null) {
            return;
        }
        String bingPic = prefs.getString("bing_pic", null);
        if (!TextUtils.isEmpty(bingPic)) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
    }

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" +
                weatherId + "&key=ef2811dad501453784bdde41e45789e6";

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipRefresh.setRefreshing(false);

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString(WEATHER, responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }

    private void showWeatherInfo(Weather weather) {
        if(weather!=null&&"ok".equals(weather.status)) {
            AutoupdateService.actionService(this);
        }else {
            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();

        }

        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "°C";
        String weatherInfo = weather.now.more.info;

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoTxt.setText(weatherInfo);

        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }

        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String spprt = "运动建议：" + weather.suggestion.sport.info;

        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(spprt);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.pm25_text)
    public void onClick() {
    }
}
