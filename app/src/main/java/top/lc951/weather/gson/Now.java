package top.lc951.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author lichong_951@163.com
 * @version V1.0
 * @Description: TODO
 * @date 2017/3/4  17:52
 * @powered by lichong
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
