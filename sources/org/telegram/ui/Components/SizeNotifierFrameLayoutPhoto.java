package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.AdjustPanFrameLayout;

public class SizeNotifierFrameLayoutPhoto extends AdjustPanFrameLayout {
    private SizeNotifierFrameLayoutPhotoDelegate delegate;
    private int keyboardHeight;
    private Rect rect = new Rect();
    private boolean useSmoothKeyboard;
    private boolean withoutWindow;

    public interface SizeNotifierFrameLayoutPhotoDelegate {
        void onSizeChanged(int i, boolean z);
    }

    public SizeNotifierFrameLayoutPhoto(Context context, boolean z) {
        super(context);
        this.useSmoothKeyboard = z;
    }

    public void setDelegate(SizeNotifierFrameLayoutPhotoDelegate sizeNotifierFrameLayoutPhotoDelegate) {
        this.delegate = sizeNotifierFrameLayoutPhotoDelegate;
    }

    public void setWithoutWindow(boolean z) {
        this.withoutWindow = z;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        notifyHeightChanged();
    }

    public int getKeyboardHeight() {
        int i;
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        int i2 = 0;
        if (this.withoutWindow) {
            int height = rootView.getHeight();
            if (this.rect.top != 0) {
                i2 = AndroidUtilities.statusBarHeight;
            }
            int viewInset = (height - i2) - AndroidUtilities.getViewInset(rootView);
            Rect rect2 = this.rect;
            return viewInset - (rect2.bottom - rect2.top);
        }
        int height2 = rootView.getHeight() - AndroidUtilities.getViewInset(rootView);
        Rect rect3 = this.rect;
        int i3 = rect3.top;
        if (this.useSmoothKeyboard) {
            i = Math.max(0, height2 - (rect3.bottom - i3));
        } else {
            i = (AndroidUtilities.displaySize.y - i3) - height2;
        }
        if (i <= Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.statusBarHeight)) {
            return 0;
        }
        return i;
    }

    public void notifyHeightChanged() {
        if (this.delegate != null) {
            this.keyboardHeight = getKeyboardHeight();
            Point point = AndroidUtilities.displaySize;
            post(new Runnable(point.x > point.y) {
                private final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SizeNotifierFrameLayoutPhoto.this.lambda$notifyHeightChanged$0$SizeNotifierFrameLayoutPhoto(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$notifyHeightChanged$0$SizeNotifierFrameLayoutPhoto(boolean z) {
        SizeNotifierFrameLayoutPhotoDelegate sizeNotifierFrameLayoutPhotoDelegate = this.delegate;
        if (sizeNotifierFrameLayoutPhotoDelegate != null) {
            sizeNotifierFrameLayoutPhotoDelegate.onSizeChanged(this.keyboardHeight, z);
        }
    }
}
