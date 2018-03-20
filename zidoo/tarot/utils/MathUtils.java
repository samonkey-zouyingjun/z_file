package zidoo.tarot.utils;

public class MathUtils {
    public static float mappingRange(float src, float srcLowerLimit, float srcUpperLimit, float destLowerLimit, float destUpperLimit) {
        return (((0.5f * src) + ((srcUpperLimit - srcLowerLimit) * 0.5f)) * (destUpperLimit - destLowerLimit)) + destUpperLimit;
    }

    public static float clampAngle(float angle, float minAngle, float maxAngle) {
        angle %= 360.0f;
        if (angle < minAngle) {
            return minAngle;
        }
        if (angle > maxAngle) {
            return maxAngle;
        }
        return angle;
    }
}
