package zidoo.tarot.kernel.input;

import android.view.MotionEvent;

public class GestureEvent {
    private int action = 0;
    private float dx = 0.0f;
    private float dy = 0.0f;
    private long eventTime = 0;
    private float x = 0.0f;
    private float y = 0.0f;

    public void obtain(MotionEvent e) {
        this.action = e.getAction();
        this.eventTime = e.getEventTime();
        this.x = e.getX();
        this.y = e.getY();
    }

    public int getAction() {
        return this.action;
    }

    public long getEventTime() {
        return this.eventTime;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void setLastMoveDistanceX(float dx) {
        this.dx = dx;
    }

    public void setLastMoveDistanceY(float dy) {
        this.dy = dy;
    }

    public float getLastMoveDistanceX() {
        return this.dx;
    }

    public float getLastMoveDistanceY() {
        return this.dy;
    }
}
