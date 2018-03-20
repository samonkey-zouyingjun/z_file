package zidoo.tarot.kernel.input;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import zidoo.tarot.GLContext;

@SuppressLint({"HandlerLeak"})
public abstract class DefaultGestureRecogniser implements GestureRecogniser {
    public static final int LONG_PRESS = 7002;
    public static final int LONG_PRESS_TIME = 500;
    public static final int SHOW_PRESS = 7001;
    public static final int SHOW_PRESS_TIME = 180;
    private GLContext glContext;
    private float mDownX;
    private float mDownY;
    private EventHanlder mHanlder = new EventHanlder(Looper.getMainLooper());
    private int mIgnoreMoveDistance = 5;
    private GestureEvent mLastEvent = new GestureEvent();
    private boolean mLastIsMove = false;
    private int mMaximumFlingVelocity;
    private int mMinimumFlingVelocity;
    private int mState = 0;
    VelocityTracker mVelocityTracker = null;

    private class EventHanlder extends Handler {
        EventHanlder(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            DefaultGestureRecogniser defaultGestureRecogniser;
            switch (msg.what) {
                case DefaultGestureRecogniser.SHOW_PRESS /*7001*/:
                    if (!DefaultGestureRecogniser.this.hasMove()) {
                        defaultGestureRecogniser = DefaultGestureRecogniser.this;
                        defaultGestureRecogniser.mState = defaultGestureRecogniser.mState | 256;
                        DefaultGestureRecogniser.this.onShowPress((MotionEvent) msg.obj);
                        break;
                    }
                    break;
                case DefaultGestureRecogniser.LONG_PRESS /*7002*/:
                    if (!DefaultGestureRecogniser.this.hasMove()) {
                        defaultGestureRecogniser = DefaultGestureRecogniser.this;
                        defaultGestureRecogniser.mState = defaultGestureRecogniser.mState | 69632;
                        DefaultGestureRecogniser.this.onLongPress((MotionEvent) msg.obj);
                        break;
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public DefaultGestureRecogniser(GLContext glContext) {
        this.glContext = glContext;
        ViewConfiguration configuration = ViewConfiguration.get(glContext.getBaseContext());
        this.mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    public boolean onTouch(MotionEvent e) {
        int action = e.getAction();
        if (isOver() && action != 0) {
            return false;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(e);
        boolean consume = false;
        boolean move = false;
        switch (action) {
            case 0:
                this.mState = 1;
                consume = onDown(e);
                this.mDownX = e.getX();
                this.mDownY = e.getY();
                this.mHanlder.sendMessageDelayed(this.mHanlder.obtainMessage(SHOW_PRESS, e), 180);
                this.mHanlder.sendMessageDelayed(this.mHanlder.obtainMessage(LONG_PRESS, e), 500);
                break;
            case 1:
                if (!hasLongPress()) {
                    if (!hasMove()) {
                        consume = onSingleTapUp(e);
                    } else if (this.mLastIsMove) {
                        VelocityTracker velocityTracker = this.mVelocityTracker;
                        int pointerId = e.getPointerId(0);
                        velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumFlingVelocity);
                        float velocityY = velocityTracker.getYVelocity(pointerId);
                        float velocityX = velocityTracker.getXVelocity(pointerId);
                        if (Math.abs(velocityY) > ((float) this.mMinimumFlingVelocity) || Math.abs(velocityX) > ((float) this.mMinimumFlingVelocity)) {
                            onFling(this.mLastEvent, e, velocityX, velocityY);
                        }
                    }
                }
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                }
                this.mHanlder.removeMessages(SHOW_PRESS);
                this.mHanlder.removeMessages(LONG_PRESS);
                this.mState |= 65536;
                break;
            case 2:
                float dx = e.getX() - this.mDownX;
                float dy = e.getY() - this.mDownY;
                if (!hasMove() && Math.pow((double) dx, 2.0d) + Math.pow((double) dy, 2.0d) > ((double) (this.mIgnoreMoveDistance * this.mIgnoreMoveDistance))) {
                    this.mState |= 16;
                }
                if (hasMove()) {
                    move = true;
                    consume = onScroll(e, this.mLastEvent);
                    this.mLastEvent.setLastMoveDistanceX(dx);
                    this.mLastEvent.setLastMoveDistanceY(dy);
                    break;
                }
                break;
        }
        this.mLastEvent.obtain(e);
        this.mLastIsMove = move;
        return consume;
    }

    public void setIgnoreMoveDistance(int ignoreMoveDistance) {
        this.mIgnoreMoveDistance = (int) (this.glContext.getConfig().getDisplay().sRatioX * ((float) ignoreMoveDistance));
    }

    public boolean hasDown() {
        return (this.mState & 1) != 0;
    }

    public boolean hasMove() {
        return (this.mState & 16) != 0;
    }

    public boolean hasPress() {
        return (this.mState & 256) != 0;
    }

    public boolean hasLongPress() {
        return (this.mState & 4096) != 0;
    }

    private boolean isOver() {
        return (this.mState & 65536) != 0;
    }
}
