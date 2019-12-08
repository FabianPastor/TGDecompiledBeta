package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.SecureDocument;

public class BackupImageView extends View {
    protected int height = -1;
    protected ImageReceiver imageReceiver = new ImageReceiver(this);
    protected int width = -1;

    public BackupImageView(Context context) {
        super(context);
    }

    public void setOrientation(int i, boolean z) {
        this.imageReceiver.setOrientation(i, z);
    }

    public void setImage(SecureDocument secureDocument, String str) {
        setImage(ImageLocation.getForSecureDocument(secureDocument), str, null, null, null, null, null, 0, null);
    }

    public void setImage(ImageLocation imageLocation, String str, String str2, Drawable drawable, Object obj) {
        setImage(imageLocation, str, null, null, drawable, null, str2, 0, obj);
    }

    public void setImage(ImageLocation imageLocation, String str, Drawable drawable, Object obj) {
        setImage(imageLocation, str, null, null, drawable, null, null, 0, obj);
    }

    public void setImage(ImageLocation imageLocation, String str, Bitmap bitmap, Object obj) {
        setImage(imageLocation, str, null, null, null, bitmap, null, 0, obj);
    }

    public void setImage(ImageLocation imageLocation, String str, Drawable drawable, int i, Object obj) {
        setImage(imageLocation, str, null, null, drawable, null, null, i, obj);
    }

    public void setImage(ImageLocation imageLocation, String str, Bitmap bitmap, int i, Object obj) {
        setImage(imageLocation, str, null, null, null, bitmap, null, i, obj);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, int i, Object obj) {
        setImage(imageLocation, str, imageLocation2, str2, null, null, null, i, obj);
    }

    public void setImage(String str, String str2, Drawable drawable) {
        setImage(ImageLocation.getForPath(str), str2, null, null, drawable, null, null, 0, null);
    }

    public void setImage(String str, String str2, String str3, String str4) {
        setImage(ImageLocation.getForPath(str), str2, ImageLocation.getForPath(str3), str4, null, null, null, 0, null);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, Drawable drawable, Bitmap bitmap, String str3, int i, Object obj) {
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
        backupImageView.imageReceiver.setImage(imageLocation, str, imageLocation2, str2, drawable2, i, str3, obj, 0);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, String str3, int i, int i2, Object obj) {
        this.imageReceiver.setImage(imageLocation, str, imageLocation2, str2, null, i, str3, obj, i2);
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
        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(i2, Mode.MULTIPLY));
        }
        this.imageReceiver.setImageBitmap(drawable);
        invalidate();
    }

    public void setImageDrawable(Drawable drawable) {
        this.imageReceiver.setImageBitmap(drawable);
    }

    public void setLayerNum(int i) {
        this.imageReceiver.setLayerNum(i);
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

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.imageReceiver.onDetachedFromWindow();
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageReceiver.onAttachedToWindow();
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.width == -1 || this.height == -1) {
            this.imageReceiver.setImageCoords(0, 0, getWidth(), getHeight());
        } else {
            ImageReceiver imageReceiver = this.imageReceiver;
            int width = (getWidth() - this.width) / 2;
            int height = getHeight();
            int i = this.height;
            imageReceiver.setImageCoords(width, (height - i) / 2, this.width, i);
        }
        this.imageReceiver.draw(canvas);
    }
}
