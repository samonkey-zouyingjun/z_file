package zidoo.tarot;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import zidoo.tarot.kernel.GameObject;
import zidoo.tarot.kernel.Vector3;

public class Utils {
    public static char[] trimToChars(String inputString, int length) {
        if (length <= 0) {
            return null;
        }
        char[] result = new char[length];
        if (!(inputString == null || inputString.isEmpty())) {
            int orgStringLength = inputString.length();
            char[] inputStringChars = inputString.toCharArray();
            int i = 0;
            int j = 0;
            while (i < length && j < orgStringLength) {
                result[i] = inputStringChars[j];
                i++;
                j++;
            }
        }
        return result;
    }

    public static byte[] trimToCPPCharBytes(String inputString, int length) {
        if (length <= 0) {
            return null;
        }
        byte[] result = new byte[length];
        if (!(inputString == null || inputString.isEmpty())) {
            byte[] inputStringBytes = inputString.getBytes(Charset.forName("utf8"));
            int inputStringLength = inputStringBytes.length;
            int i = 0;
            int j = 0;
            while (i < length && j < inputStringLength) {
                result[i] = inputStringBytes[j];
                i++;
                j++;
            }
        }
        return result;
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

    public static Drawable mosaicNumberDrawable(Drawable number) {
        return mosaicNumberDrawable(number, null);
    }

    public static Drawable mosaicNumberDrawable(Drawable left, Drawable right) {
        int imageWidth = 0;
        int imageHeight = 0;
        int leftWidth = 0;
        int leftHeight = 0;
        int rightHeight = 0;
        if (left != null) {
            leftWidth = left.getIntrinsicWidth();
            imageWidth = leftWidth;
            leftHeight = left.getIntrinsicHeight();
            imageHeight = leftHeight;
            left.setBounds(0, 0, leftWidth, leftHeight);
        }
        if (right != null) {
            int rightWidth = right.getIntrinsicWidth();
            imageWidth += rightWidth;
            if (imageHeight <= right.getIntrinsicHeight()) {
                rightHeight = right.getIntrinsicHeight();
                imageHeight = rightHeight;
            }
            right.setBounds(0, 0, rightWidth, rightHeight);
        }
        Bitmap bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if (left != null) {
            canvas.save();
            canvas.translate(0.0f, ((float) (imageHeight - leftHeight)) / 2.0f);
            left.draw(canvas);
            canvas.restore();
        }
        if (right != null) {
            canvas.save();
            canvas.translate((float) leftWidth, ((float) (imageHeight - rightHeight)) / 2.0f);
            right.draw(canvas);
            canvas.restore();
        }
        return new BitmapDrawable(bitmap);
    }

    public static ArrayList<Integer> makePrimeNumbers(ArrayList<Integer> list, int fromNumber, int toNumber) {
        list.clear();
        for (int i = fromNumber; i <= toNumber; i++) {
            int theta = (int) Math.sqrt((double) (i + 1));
            boolean isPrime = true;
            for (int j = 2; j <= theta; j++) {
                if (i % j == 0) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime) {
                list.add(Integer.valueOf(i));
            }
        }
        return list;
    }

    public static ArrayList<Integer> makeRandCompositeNumbers(ArrayList<Integer> list, int fromNumber, int toNumber) {
        int i;
        list.clear();
        for (i = fromNumber; i <= toNumber; i++) {
            int theta = (int) Math.sqrt((double) (i + 1));
            for (int j = 2; j <= theta; j++) {
                if (i % j == 0) {
                    list.add(Integer.valueOf(i));
                    break;
                }
            }
        }
        int[] randSerial = randomSerial(list.size());
        ArrayList<Integer> randList = new ArrayList(list);
        int size = randSerial.length;
        for (i = 0; i < size; i++) {
            randList.set(randSerial[i], (Integer) list.get(i));
        }
        list.clear();
        return randList;
    }

    public static int[] factor(int factoredNumber) {
        boolean keepRunning = true;
        List<Integer> factors = new ArrayList();
        while (keepRunning) {
            int i = 2;
            while (i < (factoredNumber / 2) + 1) {
                if (factoredNumber % i == 0) {
                    factors.add(Integer.valueOf(i));
                    factoredNumber /= i;
                    if (isPrimeNumber(factoredNumber)) {
                        keepRunning = false;
                        factors.add(Integer.valueOf(factoredNumber));
                    }
                } else {
                    i++;
                }
            }
        }
        int loopSize = factors.size();
        int[] result = new int[loopSize];
        for (i = 0; i < loopSize; i++) {
            result[i] = ((Integer) factors.get(i)).intValue();
        }
        return result;
    }

    public static boolean isPrimeNumber(int number) {
        for (int i = 2; i < number; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static int[] randomSerial(int limit) {
        int i;
        int[] result = new int[limit];
        for (i = 0; i < limit; i++) {
            result[i] = i;
        }
        Random rand = new Random();
        for (i = limit - 1; i > 0; i--) {
            int w = rand.nextInt(i);
            int t = result[i];
            result[i] = result[w];
            result[w] = t;
        }
        return result;
    }

    public static Bitmap generateReflectedImage(Bitmap bitmap, int alpha) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1.0f, -1.0f);
        Bitmap reflectionBitmap = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);
        Bitmap withReflectionBitmap = Bitmap.createBitmap(width, ((height / 2) + height) + 4, Config.ARGB_8888);
        Canvas canvas = new Canvas(withReflectionBitmap);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
        canvas.drawRect(0.0f, (float) height, (float) width, (float) (height + 4), new Paint());
        canvas.drawBitmap(reflectionBitmap, 0.0f, (float) (height + 4), null);
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0.0f, (float) bitmap.getHeight(), 0.0f, (float) withReflectionBitmap.getHeight(), ViewCompat.MEASURED_SIZE_MASK | (alpha << 24), ViewCompat.MEASURED_SIZE_MASK, TileMode.MIRROR));
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0.0f, (float) height, (float) width, (float) withReflectionBitmap.getHeight(), paint);
        return withReflectionBitmap;
    }

    public static Bitmap generateReflectionImage(Bitmap bitmap, int[] colors, float[] positions, float clippingPercent) {
        int i;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int reflectionWidth = width;
        int reflectionHeight = (int) (((float) height) * clippingPercent);
        Paint paint = new Paint();
        Paint reflectionPaint = new Paint();
        Matrix matrix = new Matrix();
        int[] colorArray = new int[colors.length];
        for (i = 0; i < colorArray.length; i++) {
            colorArray[i] = colors[i];
        }
        float[] positionArray = new float[positions.length];
        for (i = 0; i < positionArray.length; i++) {
            positionArray[i] = positions[i];
        }
        matrix.setScale(1.0f, -1.0f);
        matrix.postTranslate(0.0f, (float) height);
        Bitmap reflectionBitmap = Bitmap.createBitmap(reflectionWidth, reflectionHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(reflectionBitmap);
        canvas.drawBitmap(bitmap, matrix, paint);
        reflectionPaint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) reflectionHeight, colorArray, positionArray, TileMode.MIRROR));
        reflectionPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0.0f, 0.0f, (float) reflectionWidth, (float) reflectionHeight, reflectionPaint);
        return reflectionBitmap;
    }

    public static float roundTo2Pow(float src) {
        int pow = 1;
        while (true) {
            int pow2 = pow + 1;
            float result = (float) Math.pow(2.0d, (double) pow);
            if (result >= src) {
                return result;
            }
            pow = pow2;
        }
    }

    public static void sortByDepth(GameObject[] actors, Vector3 cameraPosition) {
        float[] distanceList = new float[actors.length];
        for (int i = 0; i < distanceList.length; i++) {
            distanceList[i] = Vector3.distance(cameraPosition, actors[i].Position);
        }
        qsort(distanceList, actors, 0, distanceList.length - 1, cameraPosition);
    }

    private static void qsort(float[] distanceList, GameObject[] actors, int low, int high, Vector3 cameraPosition) {
        if (low < high) {
            int privot = qsortPartion(distanceList, actors, low, high, cameraPosition);
            qsort(distanceList, actors, low, privot - 1, cameraPosition);
            qsort(distanceList, actors, privot + 1, high, cameraPosition);
        }
    }

    private static int qsortPartion(float[] distanceList, GameObject[] actors, int low, int high, Vector3 cameraPosition) {
        int key = low;
        float keyValue = distanceList[key];
        GameObject keyObject = actors[key];
        distanceList[key] = distanceList[high];
        actors[key] = actors[high];
        distanceList[high] = keyValue;
        actors[high] = keyObject;
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (distanceList[j] <= distanceList[high]) {
                keyValue = distanceList[i + 1];
                keyObject = actors[i + 1];
                distanceList[i + 1] = distanceList[j];
                actors[i + 1] = actors[j];
                distanceList[j] = keyValue;
                actors[j] = keyObject;
                i++;
            }
        }
        keyValue = distanceList[high];
        keyObject = actors[high];
        distanceList[high] = distanceList[i + 1];
        actors[high] = actors[i + 1];
        distanceList[i + 1] = keyValue;
        actors[i + 1] = keyObject;
        return i + 1;
    }
}
