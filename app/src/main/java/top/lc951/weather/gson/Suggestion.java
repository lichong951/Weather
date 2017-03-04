package top.lc951.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author lichong_951@163.com
 * @version V1.0
 * @Description: TODO
 * @date 2017/3/4  17:54
 * @powered by lichong
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort{
        @SerializedName("txt")
        public String info;
    }

    public class CarWash{
        @SerializedName("txt")
        public String info;
    }

    public class Sport{
        @SerializedName("txt")
        public String info;
    }
}
