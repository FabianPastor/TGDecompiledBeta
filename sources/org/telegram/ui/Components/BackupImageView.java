package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;

public class BackupImageView extends View {
    private int height = -1;
    private ImageReceiver imageReceiver;
    private int width = -1;

    public BackupImageView(Context context) {
        super(context);
        init();
    }

    public BackupImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public BackupImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        this.imageReceiver = new ImageReceiver(this);
    }

    public void setImage(TLObject tLObject, String str, String str2, Drawable drawable) {
        setImage(tLObject, null, str, drawable, null, null, null, str2, 0);
    }

    public void setImage(TLObject tLObject, String str, Drawable drawable) {
        setImage(tLObject, null, str, drawable, null, null, null, null, 0);
    }

    public void setImage(TLObject tLObject, String str, Bitmap bitmap) {
        setImage(tLObject, null, str, null, bitmap, null, null, null, 0);
    }

    public void setImage(TLObject tLObject, String str, Drawable drawable, int i) {
        setImage(tLObject, null, str, drawable, null, null, null, null, i);
    }

    public void setImage(TLObject tLObject, String str, Bitmap bitmap, int i) {
        setImage(tLObject, null, str, null, bitmap, null, null, null, i);
    }

    public void setImage(TLObject tLObject, String str, FileLocation fileLocation, int i) {
        setImage(tLObject, null, str, null, null, fileLocation, null, null, i);
    }

    public void setImage(String str, String str2, Drawable drawable) {
        setImage(null, str, str2, drawable, null, null, null, null, 0);
    }

    public void setOrientation(int i, boolean z) {
        this.imageReceiver.setOrientation(i, z);
    }

    public void setImage(TLObject tLObject, String str, String str2, Drawable drawable, Bitmap bitmap, FileLocation fileLocation, String str3, String str4, int i) {
        BackupImageView backupImageView;
        Drawable drawable2;
        Bitmap bitmap2 = bitmap;
        if (bitmap2 != null) {
            Drawable bitmapDrawable = new BitmapDrawable(null, bitmap2);
            backupImageView = this;
            drawable2 = bitmapDrawable;
        } else {
            backupImageView = this;
            drawable2 = drawable;
        }
        backupImageView.imageReceiver.setImage(tLObject, str, str2, drawable2, fileLocation, str3, i, str4, 0);
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.imageReceiver.setImageBitmap(bitmap);
    }

    public void setImageResource(int i) {
        this.imageReceiver.setImageBitmap(getResources().getDrawable(i));
        invalidate();
    }

    public void setImageResource(int i, int i2) {
        Drawable drawable = getResources().getDrawable(i);
        if (drawable != 0) {
            drawable.setColorFilter(new PorterDuffColorFilter(i2, Mode.MULTIPLY));
        }
        this.imageReceiver.setImageBitmap(drawable);
        invalidate();
    }

    public void setImageDrawable(Drawable drawable) {
        this.imageReceiver.setImageBitmap(drawable);
    }

    public void setRoundRadius(int i) {
        this.imageReceiver.setRoundRadius(i);
        invalidate();
    }

    public int getRoundRadius() {
        return this.imageReceiver.getRoundRadius();
    }

    public void setAspectFit(boolean z) {
        this.imageReceiver.setAspectFit(z);
    }

    public ImageReceiver getImageReceiver() {
        return this.imageReceiver;
    }

    public void setSize(int i, int i2) {
        this.width = i;
        this.height = i2;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.imageReceiver.onDetachedFromWindow();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageReceiver.onAttachedToWindow();
    }

    protected void onDraw(Canvas canvas) {
        if (this.width == -1 || this.height == -1) {
            this.imageReceiver.setImageCoords(0, 0, getWidth(), getHeight());
        } else {
            this.imageReceiver.setImageCoords((getWidth() - this.width) / 2, (getHeight() - this.height) / 2, this.width, this.height);
        }
        this.imageReceiver.draw(canvas);
    }
}
