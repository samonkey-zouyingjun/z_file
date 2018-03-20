package zidoo.tarot.kernel;

public class Radian {
    public static float ToDegreeFactor = 57.29578f;
    public static float ToRadianFactor = 0.017453292f;

    public static float toDegree(float radian) {
        return ToDegreeFactor * radian;
    }

    public static float toRadian(float degree) {
        return ToRadianFactor * degree;
    }
}
