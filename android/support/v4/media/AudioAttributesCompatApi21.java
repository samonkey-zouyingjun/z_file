package android.support.v4.media;

import android.media.AudioAttributes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(21)
class AudioAttributesCompatApi21 {
    private static final String TAG = "AudioAttributesCompat";
    private static Method sAudioAttributesToLegacyStreamType;

    static final class Wrapper {
        private AudioAttributes mWrapped;

        private Wrapper(AudioAttributes obj) {
            this.mWrapped = obj;
        }

        public static Wrapper wrap(@NonNull AudioAttributes obj) {
            if (obj != null) {
                return new Wrapper(obj);
            }
            throw new IllegalArgumentException("AudioAttributesApi21.Wrapper cannot wrap null");
        }

        public AudioAttributes unwrap() {
            return this.mWrapped;
        }
    }

    AudioAttributesCompatApi21() {
    }

    public static int toLegacyStreamType(Wrapper aaWrap) {
        Exception e;
        AudioAttributes aaObject = aaWrap.unwrap();
        try {
            if (sAudioAttributesToLegacyStreamType == null) {
                sAudioAttributesToLegacyStreamType = AudioAttributes.class.getMethod("toLegacyStreamType", new Class[]{AudioAttributes.class});
            }
            return ((Integer) sAudioAttributesToLegacyStreamType.invoke(null, new Object[]{aaObject})).intValue();
        } catch (NoSuchMethodException e2) {
            e = e2;
            Log.w(TAG, "getLegacyStreamType() failed on API21+", e);
            return -1;
        } catch (InvocationTargetException e3) {
            e = e3;
            Log.w(TAG, "getLegacyStreamType() failed on API21+", e);
            return -1;
        } catch (IllegalAccessException e4) {
            e = e4;
            Log.w(TAG, "getLegacyStreamType() failed on API21+", e);
            return -1;
        } catch (ClassCastException e5) {
            e = e5;
            Log.w(TAG, "getLegacyStreamType() failed on API21+", e);
            return -1;
        }
    }
}
