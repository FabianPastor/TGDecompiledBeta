package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;

public class SizeNotifierFrameLayoutPhoto extends FrameLayout {
    /* access modifiers changed from: private */
    public SizeNotifierFrameLayoutPhotoDelegate delegate;
    /* access modifiers changed from: private */
    public int keyboardHeight;
    private Rect rect = new Rect();
    private WindowManager windowManager;
    private boolean withoutWindow;

    public interface SizeNotifierFrameLayoutPhotoDelegate {
        void onSizeChanged(int i, boolean z);
    }

    public SizeNotifierFrameLayoutPhoto(Context context) {
        super(context);
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
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        int i = 0;
        if (this.withoutWindow) {
            int height = rootView.getHeight();
            if (this.rect.top != 0) {
                i = AndroidUtilities.statusBarHeight;
            }
            int viewInset = (height - i) - AndroidUtilities.getViewInset(rootView);
            Rect rect2 = this.rect;
            return viewInset - (rect2.bottom - rect2.top);
        }
        int height2 = (AndroidUtilities.displaySize.y - this.rect.top) - (rootView.getHeight() - AndroidUtilities.getViewInset(rootView));
        if (height2 <= Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.statusBarHeight)) {
            return 0;
        }
        return height2;
    }

    public void notifyHeightChanged() {
        if (this.delegate != null) {
            this.keyboardHeight = getKeyboardHeight();
            Point point = AndroidUtilities.displaySize;
            final boolean z = point.x > point.y;
            post(new Runnable() {
                public void run() {
                    if (SizeNotifierFrameLayoutPhoto.this.delegate != null) {
                        SizeNotifierFrameLayoutPhoto.this.delegate.onSizeChanged(SizeNotifierFrameLayoutPhoto.this.keyboardHeight, z);
                    }
                }
            });
        }
    }
}
