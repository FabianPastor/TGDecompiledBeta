package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
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
        setImage(ImageLocation.getForSecureDocument(secureDocument), str, (ImageLocation) null, (String) null, (Drawable) null, (Bitmap) null, (String) null, 0, (Object) null);
    }

    public void setImage(ImageLocation imageLocation, String str, String str2, Drawable drawable, Object obj) {
        setImage(imageLocation, str, (ImageLocation) null, (String) null, drawable, (Bitmap) null, str2, 0, obj);
    }

    public void setImage(ImageLocation imageLocation, String str, Drawable drawable, Object obj) {
        setImage(imageLocation, str, (ImageLocation) null, (String) null, drawable, (Bitmap) null, (String) null, 0, obj);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, Drawable drawable, Object obj) {
        this.imageReceiver.setImage(imageLocation, str, imageLocation2, str2, (ImageLocation) null, (String) null, drawable, 0, (String) null, obj, 1);
    }

    public void setImage(ImageLocation imageLocation, String str, Drawable drawable, int i, Object obj) {
        setImage(imageLocation, str, (ImageLocation) null, (String) null, drawable, (Bitmap) null, (String) null, i, obj);
    }

    public void setImageMedia(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, Bitmap bitmap, int i, int i2, Object obj) {
        BitmapDrawable bitmapDrawable;
        BackupImageView backupImageView;
        Bitmap bitmap2 = bitmap;
        if (bitmap2 != null) {
            BitmapDrawable bitmapDrawable2 = new BitmapDrawable((Resources) null, bitmap2);
            backupImageView = this;
            bitmapDrawable = bitmapDrawable2;
        } else {
            backupImageView = this;
            bitmapDrawable = null;
        }
        backupImageView.imageReceiver.setImage(imageLocation, str, imageLocation2, str2, (ImageLocation) null, (String) null, bitmapDrawable, i, (String) null, obj, i2);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, int i, Object obj) {
        setImage(imageLocation, str, imageLocation2, str2, (Drawable) null, (Bitmap) null, (String) null, i, obj);
    }

    public void setImage(String str, String str2, Drawable drawable) {
        setImage(ImageLocation.getForPath(str), str2, (ImageLocation) null, (String) null, drawable, (Bitmap) null, (String) null, 0, (Object) null);
    }

    public void setImage(String str, String str2, String str3, String str4) {
        setImage(ImageLocation.getForPath(str), str2, ImageLocation.getForPath(str3), str4, (Drawable) null, (Bitmap) null, (String) null, 0, (Object) null);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, Drawable drawable, Bitmap bitmap, String str3, int i, Object obj) {
        BitmapDrawable bitmapDrawable;
        BackupImageView backupImageView;
        Bitmap bitmap2 = bitmap;
        if (bitmap2 != null) {
            BitmapDrawable bitmapDrawable2 = new BitmapDrawable((Resources) null, bitmap2);
            backupImageView = this;
            bitmapDrawable = bitmapDrawable2;
        } else {
            backupImageView = this;
            bitmapDrawable = drawable;
        }
        backupImageView.imageReceiver.setImage(imageLocation, str, imageLocation2, str2, bitmapDrawable, i, str3, obj, 0);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, String str3, int i, int i2, Object obj) {
        this.imageReceiver.setImage(imageLocation, str, imageLocation2, str2, (Drawable) null, i, str3, obj, i2);
    }

    public void setImageMedia(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, ImageLocation imageLocation3, String str3, String str4, int i, int i2, Object obj) {
        this.imageReceiver.setImage(imageLocation, str, imageLocation2, str2, imageLocation3, str3, (Drawable) null, i, str4, obj, i2);
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
            drawable.setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
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

    public int[] getRoundRadius() {
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

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.imageReceiver.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageReceiver.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.width == -1 || this.height == -1) {
            this.imageReceiver.setImageCoords(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
        } else {
            int height2 = getHeight();
            int i = this.height;
            this.imageReceiver.setImageCoords((float) ((getWidth() - this.width) / 2), (float) ((height2 - i) / 2), (float) this.width, (float) i);
        }
        this.imageReceiver.draw(canvas);
    }
}
