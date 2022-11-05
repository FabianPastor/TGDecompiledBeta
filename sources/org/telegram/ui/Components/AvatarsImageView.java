package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import org.telegram.tgnet.TLObject;
/* loaded from: classes3.dex */
public class AvatarsImageView extends View {
    public final AvatarsDarawable avatarsDarawable;

    public AvatarsImageView(Context context, boolean z) {
        super(context);
        this.avatarsDarawable = new AvatarsDarawable(this, z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.avatarsDarawable.width = getMeasuredWidth();
        this.avatarsDarawable.height = getMeasuredHeight();
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarsDarawable.onAttachedToWindow();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.avatarsDarawable.onDraw(canvas);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarsDarawable.onDetachedFromWindow();
    }

    public void setStyle(int i) {
        this.avatarsDarawable.setStyle(i);
    }

    public void setDelegate(Runnable runnable) {
        this.avatarsDarawable.setDelegate(runnable);
    }

    public void setObject(int i, int i2, TLObject tLObject) {
        this.avatarsDarawable.setObject(i, i2, tLObject);
    }

    public void setAvatarsTextSize(int i) {
        this.avatarsDarawable.setAvatarsTextSize(i);
    }

    public void reset() {
        this.avatarsDarawable.reset();
    }

    public void setCount(int i) {
        this.avatarsDarawable.setCount(i);
    }

    public void commitTransition(boolean z) {
        this.avatarsDarawable.commitTransition(z);
    }

    public void updateAfterTransitionEnd() {
        this.avatarsDarawable.updateAfterTransitionEnd();
    }

    public void setCentered(boolean z) {
        this.avatarsDarawable.setCentered(z);
    }
}
