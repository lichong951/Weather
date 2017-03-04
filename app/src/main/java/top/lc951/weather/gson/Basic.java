package top.lc951.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author lichong_951@163.com
 * @version V1.0
 * @Description: TODO
 * @date 2017/3/4  17:50
 * @powered by lichong
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
