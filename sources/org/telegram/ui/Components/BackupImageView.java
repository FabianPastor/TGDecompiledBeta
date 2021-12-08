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
import org.telegram.tgnet.TLObject;

public class BackupImageView extends View {
    protected int height = -1;
    protected ImageReceiver imageReceiver = new ImageReceiver(this);
    protected int width = -1;

    public BackupImageView(Context context) {
        super(context);
    }

    public void setOrientation(int angle, boolean center) {
        this.imageReceiver.setOrientation(angle, center);
    }

    public void setImage(SecureDocument secureDocument, String filter) {
        setImage(ImageLocation.getForSecureDocument(secureDocument), filter, (ImageLocation) null, (String) null, (Drawable) null, (Bitmap) null, (String) null, 0, (Object) null);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, String ext, Drawable thumb, Object parentObject) {
        setImage(imageLocation, imageFilter, (ImageLocation) null, (String) null, thumb, (Bitmap) null, ext, 0, parentObject);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, Drawable thumb, Object parentObject) {
        setImage(imageLocation, imageFilter, (ImageLocation) null, (String) null, thumb, (Bitmap) null, (String) null, 0, parentObject);
    }

    public void setImage(ImageLocation mediaLocation, String mediaFilter, ImageLocation imageLocation, String imageFilter, Drawable thumb, Object parentObject) {
        this.imageReceiver.setImage(mediaLocation, mediaFilter, imageLocation, imageFilter, (ImageLocation) null, (String) null, thumb, 0, (String) null, parentObject, 1);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, Bitmap thumb, Object parentObject) {
        setImage(imageLocation, imageFilter, (ImageLocation) null, (String) null, (Drawable) null, thumb, (String) null, 0, parentObject);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, Drawable thumb, int size, Object parentObject) {
        setImage(imageLocation, imageFilter, (ImageLocation) null, (String) null, thumb, (Bitmap) null, (String) null, size, parentObject);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, Bitmap thumbBitmap, int size, int cacheType, Object parentObject) {
        Bitmap bitmap = thumbBitmap;
        Drawable thumb = null;
        if (bitmap != null) {
            thumb = new BitmapDrawable((Resources) null, bitmap);
        }
        this.imageReceiver.setImage(imageLocation, imageFilter, (ImageLocation) null, (String) null, thumb, size, (String) null, parentObject, cacheType);
    }

    public void setForUserOrChat(TLObject object, AvatarDrawable avatarDrawable) {
        this.imageReceiver.setForUserOrChat(object, avatarDrawable);
    }

    public void setForUserOrChat(TLObject object, AvatarDrawable avatarDrawable, Object parent) {
        this.imageReceiver.setForUserOrChat(object, avatarDrawable, parent);
    }

    public void setImageMedia(ImageLocation mediaLocation, String mediaFilter, ImageLocation imageLocation, String imageFilter, Bitmap thumbBitmap, int size, int cacheType, Object parentObject) {
        Bitmap bitmap = thumbBitmap;
        Drawable thumb = null;
        if (bitmap != null) {
            thumb = new BitmapDrawable((Resources) null, bitmap);
        }
        this.imageReceiver.setImage(mediaLocation, mediaFilter, imageLocation, imageFilter, (ImageLocation) null, (String) null, thumb, size, (String) null, parentObject, cacheType);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, ImageLocation thumbLocation, String thumbFilter, int size, Object parentObject) {
        setImage(imageLocation, imageFilter, thumbLocation, thumbFilter, (Drawable) null, (Bitmap) null, (String) null, size, parentObject);
    }

    public void setImage(String path, String filter, Drawable thumb) {
        setImage(ImageLocation.getForPath(path), filter, (ImageLocation) null, (String) null, thumb, (Bitmap) null, (String) null, 0, (Object) null);
    }

    public void setImage(String path, String filter, String thumbPath, String thumbFilter) {
        setImage(ImageLocation.getForPath(path), filter, ImageLocation.getForPath(thumbPath), thumbFilter, (Drawable) null, (Bitmap) null, (String) null, 0, (Object) null);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, ImageLocation thumbLocation, String thumbFilter, Drawable thumb, Bitmap thumbBitmap, String ext, int size, Object parentObject) {
        Drawable thumb2;
        Bitmap bitmap = thumbBitmap;
        if (bitmap != null) {
            thumb2 = new BitmapDrawable((Resources) null, bitmap);
        } else {
            thumb2 = thumb;
        }
        this.imageReceiver.setImage(imageLocation, imageFilter, thumbLocation, thumbFilter, thumb2, size, ext, parentObject, 0);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, ImageLocation thumbLocation, String thumbFilter, String ext, int size, int cacheType, Object parentObject) {
        this.imageReceiver.setImage(imageLocation, imageFilter, thumbLocation, thumbFilter, (Drawable) null, size, ext, parentObject, cacheType);
    }

    public void setImageMedia(ImageLocation mediaLocation, String mediaFilter, ImageLocation imageLocation, String imageFilter, ImageLocation thumbLocation, String thumbFilter, String ext, int size, int cacheType, Object parentObject) {
        this.imageReceiver.setImage(mediaLocation, mediaFilter, imageLocation, imageFilter, thumbLocation, thumbFilter, (Drawable) null, size, ext, parentObject, cacheType);
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
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }
        this.imageReceiver.setImageBitmap(drawable);
        invalidate();
    }

    public void setImageDrawable(Drawable drawable) {
        this.imageReceiver.setImageBitmap(drawable);
    }

    public void setLayerNum(int value) {
        this.imageReceiver.setLayerNum(value);
    }

    public void setRoundRadius(int value) {
        this.imageReceiver.setRoundRadius(value);
        invalidate();
    }

    public void setRoundRadius(int tl, int tr, int bl, int br) {
        this.imageReceiver.setRoundRadius(tl, tr, bl, br);
        invalidate();
    }

    public int[] getRoundRadius() {
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
