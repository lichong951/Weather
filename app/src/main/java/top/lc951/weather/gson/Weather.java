package top.lc951.weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author lichong_951@163.com
 * @version V1.0
 * @Description: TODO
 * @date 2017/3/4  17:58
 * @powered by lichong
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

}
