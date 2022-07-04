package org.telegram.ui.Components.spoilers;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.view.GestureDetectorCompat;
import java.util.List;

public class SpoilersClickDetector {
    private GestureDetectorCompat gestureDetector;
    /* access modifiers changed from: private */
    public boolean trackingTap;

    public interface OnSpoilerClickedListener {
        void onSpoilerClicked(SpoilerEffect spoilerEffect, float f, float f2);
    }

    public SpoilersClickDetector(View v, List<SpoilerEffect> spoilers, OnSpoilerClickedListener clickedListener) {
        this(v, spoilers, true, clickedListener);
    }

    public SpoilersClickDetector(View v, List<SpoilerEffect> spoilers, boolean offsetPadding, OnSpoilerClickedListener clickedListener) {
        final View view = v;
        final boolean z = offsetPadding;
        final List<SpoilerEffect> list = spoilers;
        final OnSpoilerClickedListener onSpoilerClickedListener = clickedListener;
        this.gestureDetector = new GestureDetectorCompat(v.getContext(), new GestureDetector.SimpleOnGestureListener() {
            public boolean onDown(MotionEvent e) {
                int x = (int) e.getX();
                int y = ((int) e.getY()) + view.getScrollY();
                if (z) {
                    x -= view.getPaddingLeft();
                    y -= view.getPaddingTop();
                }
                for (SpoilerEffect eff : list) {
                    if (eff.getBounds().contains(x, y)) {
                        boolean unused = SpoilersClickDetector.this.trackingTap = true;
                        return true;
                    }
                }
                return false;
            }

            public boolean onSingleTapUp(MotionEvent e) {
                if (SpoilersClickDetector.this.trackingTap) {
                    view.playSoundEffect(0);
                    boolean unused = SpoilersClickDetector.this.trackingTap = false;
                    int x = (int) e.getX();
                    int y = ((int) e.getY()) + view.getScrollY();
                    if (z) {
                        x -= view.getPaddingLeft();
                        y -= view.getPaddingTop();
                    }
                    for (SpoilerEffect eff : list) {
                        if (eff.getBounds().contains(x, y)) {
                            onSpoilerClickedListener.onSpoilerClicked(eff, (float) x, (float) y);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return this.gestureDetector.onTouchEvent(ev);
    }
}
