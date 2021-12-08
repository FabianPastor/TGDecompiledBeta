package org.telegram.ui.Components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;

public class SizeNotifierFrameLayoutPhoto extends FrameLayout {
    private SizeNotifierFrameLayoutPhotoDelegate delegate;
    private int keyboardHeight;
    private Rect rect = new Rect();
    private boolean useSmoothKeyboard;
    private WindowManager windowManager;
    private boolean withoutWindow;

    public interface SizeNotifierFrameLayoutPhotoDelegate {
        void onSizeChanged(int i, boolean z);
    }

    public SizeNotifierFrameLayoutPhoto(Context context, boolean smoothKeyboard) {
        super(context);
        this.useSmoothKeyboard = smoothKeyboard;
    }

    public void setDelegate(SizeNotifierFrameLayoutPhotoDelegate sizeNotifierFrameLayoutPhotoDelegate) {
        this.delegate = sizeNotifierFrameLayoutPhotoDelegate;
    }

    public void setWithoutWindow(boolean value) {
        this.withoutWindow = value;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        notifyHeightChanged();
    }

    public int getKeyboardHeight() {
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        if (this.withoutWindow) {
            return ((rootView.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(rootView)) - (this.rect.bottom - this.rect.top);
        }
        int size = (((Activity) rootView.getContext()).getWindow().getDecorView().getHeight() - AndroidUtilities.getViewInset(rootView)) - rootView.getBottom();
        if (size <= Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.statusBarHeight)) {
            return 0;
        }
        return size;
    }

    public void notifyHeightChanged() {
        if (this.delegate != null) {
            this.keyboardHeight = getKeyboardHeight();
            post(new SizeNotifierFrameLayoutPhoto$$ExternalSyntheticLambda0(this, AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y));
        }
    }

    /* renamed from: lambda$notifyHeightChanged$0$org-telegram-ui-Components-SizeNotifierFrameLayoutPhoto  reason: not valid java name */
    public /* synthetic */ void m2610xb9d79cf2(boolean isWidthGreater) {
        SizeNotifierFrameLayoutPhotoDelegate sizeNotifierFrameLayoutPhotoDelegate = this.delegate;
        if (sizeNotifierFrameLayoutPhotoDelegate != null) {
            sizeNotifierFrameLayoutPhotoDelegate.onSizeChanged(this.keyboardHeight, isWidthGreater);
        }
    }
}
