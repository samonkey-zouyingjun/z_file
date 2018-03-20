package zidoo.tarot.kernel.input;

import android.view.MotionEvent;

public interface GestureRecogniser {
    boolean onDown(MotionEvent motionEvent);

    boolean onFling(GestureEvent gestureEvent, MotionEvent motionEvent, float f, float f2);

    void onLongPress(MotionEvent motionEvent);

    boolean onScroll(MotionEvent motionEvent, GestureEvent gestureEvent);

    void onShowPress(MotionEvent motionEvent);

    boolean onSingleTapUp(MotionEvent motionEvent);
}
