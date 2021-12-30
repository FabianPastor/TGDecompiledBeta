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

    public SpoilersClickDetector(View view, List<SpoilerEffect> list, OnSpoilerClickedListener onSpoilerClickedListener) {
        this(view, list, true, onSpoilerClickedListener);
    }

    public SpoilersClickDetector(View view, List<SpoilerEffect> list, boolean z, OnSpoilerClickedListener onSpoilerClickedListener) {
        final View view2 = view;
        final boolean z2 = z;
        final List<SpoilerEffect> list2 = list;
        final OnSpoilerClickedListener onSpoilerClickedListener2 = onSpoilerClickedListener;
        this.gestureDetector = new GestureDetectorCompat(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
            public boolean onDown(MotionEvent motionEvent) {
                int x = (int) motionEvent.getX();
                int y = ((int) motionEvent.getY()) + view2.getScrollY();
                if (z2) {
                    x -= view2.getPaddingLeft();
                    y -= view2.getPaddingTop();
                }
                for (SpoilerEffect bounds : list2) {
                    if (bounds.getBounds().contains(x, y)) {
                        boolean unused = SpoilersClickDetector.this.trackingTap = true;
                        return true;
                    }
                }
                return false;
            }

            public boolean onSingleTapUp(MotionEvent motionEvent) {
                if (SpoilersClickDetector.this.trackingTap) {
                    view2.playSoundEffect(0);
                    boolean unused = SpoilersClickDetector.this.trackingTap = false;
                    int x = (int) motionEvent.getX();
                    int y = ((int) motionEvent.getY()) + view2.getScrollY();
                    if (z2) {
                        x -= view2.getPaddingLeft();
                        y -= view2.getPaddingTop();
                    }
                    for (SpoilerEffect spoilerEffect : list2) {
                        if (spoilerEffect.getBounds().contains(x, y)) {
                            onSpoilerClickedListener2.onSpoilerClicked(spoilerEffect, (float) x, (float) y);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.gestureDetector.onTouchEvent(motionEvent);
    }
}
