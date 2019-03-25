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
import org.telegram.messenger.SecureDocument;
import org.telegram.tgnet.TLObject;

public class BackupImageView extends View {
    private int height = -1;
    private ImageReceiver imageReceiver;
    private int width = -1;

    public BackupImageView(Context context) {
        super(context);
        init();
    }

    public BackupImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BackupImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.imageReceiver = new ImageReceiver(this);
    }

    public void setImage(SecureDocument path, String filter) {
        setImage(path, filter, null, null, null, null, null, 0, null);
    }

    public void setImage(TLObject path, String filter, String ext, Drawable thumb, Object parentObject) {
        setImage(path, filter, thumb, null, null, null, ext, 0, parentObject);
    }

    public void setImage(TLObject path, String filter, Drawable thumb, Object parentObject) {
        setImage(path, filter, thumb, null, null, null, null, 0, parentObject);
    }

    public void setImage(TLObject path, String filter, Bitmap thumb, Object parentObject) {
        setImage(path, filter, null, thumb, null, null, null, 0, parentObject);
    }

    public void setImage(TLObject path, String filter, Drawable thumb, int size, Object parentObject) {
        setImage(path, filter, thumb, null, null, null, null, size, parentObject);
    }

    public void setImage(TLObject path, String filter, Bitmap thumb, int size, Object parentObject) {
        setImage(path, filter, null, thumb, null, null, null, size, parentObject);
    }

    public void setImage(TLObject path, String filter, TLObject thumb, int size, Object parentObject) {
        setImage(path, filter, null, null, thumb, null, null, size, parentObject);
    }

    public void setImage(String path, String filter, Drawable thumb) {
        setImage(path, filter, thumb, null, null, null, null, 0, null);
    }

    public void setImage(String path, String filter, String thumbPath, String thumbFilter) {
        setImage(path, filter, null, null, thumbPath, thumbFilter, null, 0, null);
    }

    public void setOrientation(int angle, boolean center) {
        this.imageReceiver.setOrientation(angle, center);
    }

    public void setImage(Object path, String filter, Drawable thumb, Bitmap thumbBitmap, Object thumbLocation, String thumbFilter, String ext, int size, Object parentObject) {
        if (thumbBitmap != null) {
            thumb = new BitmapDrawable(null, thumbBitmap);
        }
        this.imageReceiver.setImage(path, filter, thumb, thumbLocation, thumbFilter, size, ext, parentObject, 0);
    }

    public void setImage(TLObject path, String filter, TLObject thumbLocation, String thumbFilter, String ext, int size, int cacheType, Object parentObject) {
        this.imageReceiver.setImage(path, filter, null, thumbLocation, thumbFilter, size, ext, parentObject, cacheType);
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.imageReceiver.setImageBitmap(bitmap);
    }

    public void setImageResource(int resId) {
        this.imageReceiver.setImageBitmap(getResources().getDrawable(resId));
        invalidate();
    }

    public void setImageResource(int resId, int color) {
        Drawable drawable = getResources().getDrawable(resId);
        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(color, Mode.MULTIPLY));
        }
        this.imageReceiver.setImageBitmap(drawable);
        invalidate();
    }

    public void setImageDrawable(Drawable drawable) {
        this.imageReceiver.setImageBitmap(drawable);
    }

    public void setRoundRadius(int value) {
        this.imageReceiver.setRoundRadius(value);
        invalidate();
    }

    public int getRoundRadius() {
        return this.imageReceiver.getRoundRadius();
    }

    public void setAspectFit(boolean value) {
        this.imageReceiver.setAspectFit(value);
    }

    public ImageReceiver getImageReceiver() {
        return this.imageReceiver;
    }

    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
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
            this.imageReceiver.setImageCoords((getWidth() - this.width) / 2, (getHeight() - this.height) / 2, this.width, this.height);
        }
        this.imageReceiver.draw(canvas);
    }
}
