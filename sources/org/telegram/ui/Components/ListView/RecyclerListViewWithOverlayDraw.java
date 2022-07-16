package org.telegram.ui.Components.ListView;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public class RecyclerListViewWithOverlayDraw extends RecyclerListView {
    boolean invalidated;

    public interface OverlayView {
        float getX();

        float getY();

        void preDraw(View view, Canvas canvas);
    }

    public RecyclerListViewWithOverlayDraw(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        this.invalidated = false;
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof OverlayView) {
                OverlayView overlayView = (OverlayView) getChildAt(i);
                canvas.save();
                canvas.translate(overlayView.getX(), overlayView.getY());
                overlayView.preDraw(this, canvas);
                canvas.restore();
            }
        }
        super.dispatchDraw(canvas);
    }

    public void invalidate() {
        if (!this.invalidated) {
            super.invalidate();
            this.invalidated = true;
        }
    }
}
