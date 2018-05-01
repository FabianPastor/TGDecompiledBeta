package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentEncrypted;
import org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.ui.Components.AnimatedFileDrawable;

public class ImageReceiver implements NotificationCenterDelegate {
    private static PorterDuffColorFilter selectedColorFilter = new PorterDuffColorFilter(-2236963, Mode.MULTIPLY);
    private static PorterDuffColorFilter selectedGroupColorFilter = new PorterDuffColorFilter(-4473925, Mode.MULTIPLY);
    private boolean allowDecodeSingleFrame;
    private boolean allowStartAnimation;
    private RectF bitmapRect;
    private BitmapShader bitmapShader;
    private BitmapShader bitmapShaderThumb;
    private boolean canceledLoading;
    private boolean centerRotation;
    private ColorFilter colorFilter;
    private byte crossfadeAlpha;
    private Drawable crossfadeImage;
    private String crossfadeKey;
    private BitmapShader crossfadeShader;
    private boolean crossfadeWithOldImage;
    private boolean crossfadeWithThumb;
    private int currentAccount;
    private float currentAlpha;
    private int currentCacheType;
    private String currentExt;
    private String currentFilter;
    private String currentHttpUrl;
    private Drawable currentImage;
    private TLObject currentImageLocation;
    private String currentKey;
    private int currentSize;
    private Drawable currentThumb;
    private String currentThumbFilter;
    private String currentThumbKey;
    private FileLocation currentThumbLocation;
    private ImageReceiverDelegate delegate;
    private Rect drawRegion;
    private boolean forceCrossfade;
    private boolean forceLoding;
    private boolean forcePreview;
    private int imageH;
    private int imageW;
    private int imageX;
    private int imageY;
    private boolean invalidateAll;
    private boolean isAspectFit;
    private int isPressed;
    private boolean isVisible;
    private long lastUpdateAlphaTime;
    private boolean manualAlphaAnimator;
    private boolean needsQualityThumb;
    private int orientation;
    private float overrideAlpha;
    private int param;
    private MessageObject parentMessageObject;
    private View parentView;
    private Paint roundPaint;
    private int roundRadius;
    private RectF roundRect;
    private SetImageBackup setImageBackup;
    private Matrix shaderMatrix;
    private boolean shouldGenerateQualityThumb;
    private Drawable staticThumb;
    private int tag;
    private int thumbTag;

    public static class BitmapHolder {
        public Bitmap bitmap;
        private String key;

        public BitmapHolder(Bitmap bitmap, String str) {
            this.bitmap = bitmap;
            this.key = str;
            if (this.key != null) {
                ImageLoader.getInstance().incrementUseCount(this.key);
            }
        }

        public int getWidth() {
            return this.bitmap != null ? this.bitmap.getWidth() : 0;
        }

        public int getHeight() {
            return this.bitmap != null ? this.bitmap.getHeight() : 0;
        }

        public boolean isRecycled() {
            if (this.bitmap != null) {
                if (!this.bitmap.isRecycled()) {
                    return false;
                }
            }
            return true;
        }

        public void release() {
            if (this.key == null) {
                this.bitmap = null;
                return;
            }
            boolean decrementUseCount = ImageLoader.getInstance().decrementUseCount(this.key);
            if (!ImageLoader.getInstance().isInCache(this.key) && decrementUseCount) {
                this.bitmap.recycle();
            }
            this.key = null;
            this.bitmap = null;
        }
    }

    public interface ImageReceiverDelegate {
        void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2);
    }

    private class SetImageBackup {
        public int cacheType;
        public String ext;
        public TLObject fileLocation;
        public String filter;
        public String httpUrl;
        public int size;
        public Drawable thumb;
        public String thumbFilter;
        public FileLocation thumbLocation;

        private SetImageBackup() {
        }
    }

    public ImageReceiver() {
        this(null);
    }

    public ImageReceiver(View view) {
        this.allowStartAnimation = true;
        this.drawRegion = new Rect();
        this.isVisible = true;
        this.roundRect = new RectF();
        this.bitmapRect = new RectF();
        this.shaderMatrix = new Matrix();
        this.overrideAlpha = 1.0f;
        this.crossfadeAlpha = (byte) 1;
        this.parentView = view;
        this.roundPaint = new Paint(1);
        this.currentAccount = UserConfig.selectedAccount;
    }

    public void cancelLoadImage() {
        this.forceLoding = false;
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
        this.canceledLoading = true;
    }

    public void setForceLoading(boolean z) {
        this.forceLoding = z;
    }

    public boolean isForceLoding() {
        return this.forceLoding;
    }

    public void setImage(TLObject tLObject, String str, Drawable drawable, String str2, int i) {
        setImage(tLObject, null, str, drawable, null, null, 0, str2, i);
    }

    public void setImage(TLObject tLObject, String str, Drawable drawable, int i, String str2, int i2) {
        setImage(tLObject, null, str, drawable, null, null, i, str2, i2);
    }

    public void setImage(String str, String str2, Drawable drawable, String str3, int i) {
        setImage(null, str, str2, drawable, null, null, i, str3, 1);
    }

    public void setImage(TLObject tLObject, String str, FileLocation fileLocation, String str2, String str3, int i) {
        setImage(tLObject, null, str, null, fileLocation, str2, 0, str3, i);
    }

    public void setImage(TLObject tLObject, String str, FileLocation fileLocation, String str2, int i, String str3, int i2) {
        setImage(tLObject, null, str, null, fileLocation, str2, i, str3, i2);
    }

    public void setImage(TLObject tLObject, String str, String str2, Drawable drawable, FileLocation fileLocation, String str3, int i, String str4, int i2) {
        TLObject tLObject2 = tLObject;
        String str5 = str;
        String str6 = str2;
        Drawable drawable2 = drawable;
        FileLocation fileLocation2 = fileLocation;
        String str7 = str3;
        String str8 = str4;
        if (this.setImageBackup != null) {
            r0.setImageBackup.fileLocation = null;
            r0.setImageBackup.httpUrl = null;
            r0.setImageBackup.thumbLocation = null;
            r0.setImageBackup.thumb = null;
        }
        boolean z = true;
        ImageReceiverDelegate imageReceiverDelegate;
        boolean z2;
        if (!(tLObject2 == null && str5 == null && fileLocation2 == null) && (tLObject2 == null || (tLObject2 instanceof TL_fileLocation) || (tLObject2 instanceof TL_fileEncryptedLocation) || (tLObject2 instanceof TL_document) || (tLObject2 instanceof TL_webDocument) || (tLObject2 instanceof TL_documentEncrypted))) {
            String MD5;
            StringBuilder stringBuilder;
            String stringBuilder2;
            if (!((fileLocation2 instanceof TL_fileLocation) || (fileLocation2 instanceof TL_fileEncryptedLocation))) {
                fileLocation2 = null;
            }
            if (tLObject2 == null) {
                MD5 = str5 != null ? Utilities.MD5(str) : null;
            } else if (tLObject2 instanceof FileLocation) {
                FileLocation fileLocation3 = (FileLocation) tLObject2;
                stringBuilder = new StringBuilder();
                stringBuilder.append(fileLocation3.volume_id);
                stringBuilder.append("_");
                stringBuilder.append(fileLocation3.local_id);
                MD5 = stringBuilder.toString();
            } else if (tLObject2 instanceof TL_webDocument) {
                MD5 = Utilities.MD5(((TL_webDocument) tLObject2).url);
            } else {
                Document document = (Document) tLObject2;
                if (document.dc_id == 0) {
                    tLObject2 = null;
                    MD5 = tLObject2;
                } else if (document.version == 0) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(document.dc_id);
                    stringBuilder.append("_");
                    stringBuilder.append(document.id);
                    MD5 = stringBuilder.toString();
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(document.dc_id);
                    stringBuilder.append("_");
                    stringBuilder.append(document.id);
                    stringBuilder.append("_");
                    stringBuilder.append(document.version);
                    MD5 = stringBuilder.toString();
                }
            }
            if (!(MD5 == null || str6 == null)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(MD5);
                stringBuilder.append("@");
                stringBuilder.append(str6);
                MD5 = stringBuilder.toString();
            }
            if (!(r0.currentKey == null || MD5 == null || !r0.currentKey.equals(MD5))) {
                if (r0.delegate != null) {
                    boolean z3;
                    ImageReceiverDelegate imageReceiverDelegate2 = r0.delegate;
                    if (r0.currentImage == null && r0.currentThumb == null) {
                        if (r0.staticThumb == null) {
                            z3 = false;
                            imageReceiverDelegate2.didSetImage(r0, z3, r0.currentImage != null);
                        }
                    }
                    z3 = true;
                    if (r0.currentImage != null) {
                    }
                    imageReceiverDelegate2.didSetImage(r0, z3, r0.currentImage != null);
                }
                if (!(r0.canceledLoading || r0.forcePreview)) {
                    return;
                }
            }
            if (fileLocation2 != null) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(fileLocation2.volume_id);
                stringBuilder.append("_");
                stringBuilder.append(fileLocation2.local_id);
                stringBuilder2 = stringBuilder.toString();
                if (str7 != null) {
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(stringBuilder2);
                    stringBuilder3.append("@");
                    stringBuilder3.append(str7);
                    stringBuilder2 = stringBuilder3.toString();
                }
            } else {
                stringBuilder2 = null;
            }
            if (!r0.crossfadeWithOldImage) {
                recycleBitmap(MD5, 0);
                recycleBitmap(stringBuilder2, 1);
                recycleBitmap(null, 2);
                r0.crossfadeShader = null;
            } else if (r0.currentImage != null) {
                recycleBitmap(stringBuilder2, 1);
                recycleBitmap(null, 2);
                r0.crossfadeShader = r0.bitmapShader;
                r0.crossfadeImage = r0.currentImage;
                r0.crossfadeKey = r0.currentKey;
                r0.currentImage = null;
                r0.currentKey = null;
            } else if (r0.currentThumb != null) {
                recycleBitmap(MD5, 0);
                recycleBitmap(null, 2);
                r0.crossfadeShader = r0.bitmapShaderThumb;
                r0.crossfadeImage = r0.currentThumb;
                r0.crossfadeKey = r0.currentThumbKey;
                r0.currentThumb = null;
                r0.currentThumbKey = null;
            } else {
                recycleBitmap(MD5, 0);
                recycleBitmap(stringBuilder2, 1);
                recycleBitmap(null, 2);
                r0.crossfadeShader = null;
            }
            r0.currentThumbKey = stringBuilder2;
            r0.currentKey = MD5;
            r0.currentExt = str8;
            r0.currentImageLocation = tLObject2;
            r0.currentHttpUrl = str5;
            r0.currentFilter = str6;
            r0.currentThumbFilter = str7;
            r0.currentSize = i;
            r0.currentCacheType = i2;
            r0.currentThumbLocation = fileLocation2;
            r0.staticThumb = drawable2;
            r0.bitmapShader = null;
            r0.bitmapShaderThumb = null;
            r0.currentAlpha = 1.0f;
            if (r0.delegate != null) {
                imageReceiverDelegate = r0.delegate;
                if (r0.currentImage == null && r0.currentThumb == null) {
                    if (r0.staticThumb == null) {
                        z2 = false;
                        if (r0.currentImage == null) {
                            z = false;
                        }
                        imageReceiverDelegate.didSetImage(r0, z2, z);
                    }
                }
                z2 = true;
                if (r0.currentImage == null) {
                    z = false;
                }
                imageReceiverDelegate.didSetImage(r0, z2, z);
            }
            ImageLoader.getInstance().loadImageForImageReceiver(r0);
            if (r0.parentView != null) {
                if (r0.invalidateAll) {
                    r0.parentView.invalidate();
                } else {
                    r0.parentView.invalidate(r0.imageX, r0.imageY, r0.imageX + r0.imageW, r0.imageY + r0.imageH);
                }
            }
            return;
        }
        for (int i3 = 0; i3 < 3; i3++) {
            recycleBitmap(null, i3);
        }
        r0.currentKey = null;
        r0.currentExt = str8;
        r0.currentThumbKey = null;
        r0.currentThumbFilter = null;
        r0.currentImageLocation = null;
        r0.currentHttpUrl = null;
        r0.currentFilter = null;
        r0.currentCacheType = 0;
        r0.staticThumb = drawable2;
        r0.currentAlpha = 1.0f;
        r0.currentThumbLocation = null;
        r0.currentSize = 0;
        r0.currentImage = null;
        r0.bitmapShader = null;
        r0.bitmapShaderThumb = null;
        r0.crossfadeShader = null;
        ImageLoader.getInstance().cancelLoadingForImageReceiver(r0, 0);
        if (r0.parentView != null) {
            if (r0.invalidateAll) {
                r0.parentView.invalidate();
            } else {
                r0.parentView.invalidate(r0.imageX, r0.imageY, r0.imageX + r0.imageW, r0.imageY + r0.imageH);
            }
        }
        if (r0.delegate != null) {
            imageReceiverDelegate = r0.delegate;
            if (r0.currentImage == null && r0.currentThumb == null) {
                if (r0.staticThumb == null) {
                    z2 = false;
                    if (r0.currentImage == null) {
                        z = false;
                    }
                    imageReceiverDelegate.didSetImage(r0, z2, z);
                }
            }
            z2 = true;
            if (r0.currentImage == null) {
                z = false;
            }
            imageReceiverDelegate.didSetImage(r0, z2, z);
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.colorFilter = colorFilter;
    }

    public void setDelegate(ImageReceiverDelegate imageReceiverDelegate) {
        this.delegate = imageReceiverDelegate;
    }

    public void setPressed(int i) {
        this.isPressed = i;
    }

    public boolean getPressed() {
        return this.isPressed != 0;
    }

    public void setOrientation(int i, boolean z) {
        while (i < 0) {
            i += 360;
        }
        while (i > 360) {
            i -= 360;
        }
        this.orientation = i;
        this.centerRotation = z;
    }

    public void setInvalidateAll(boolean z) {
        this.invalidateAll = z;
    }

    public Drawable getStaticThumb() {
        return this.staticThumb;
    }

    public int getAnimatedOrientation() {
        if (this.currentImage instanceof AnimatedFileDrawable) {
            return ((AnimatedFileDrawable) this.currentImage).getOrientation();
        }
        return this.staticThumb instanceof AnimatedFileDrawable ? ((AnimatedFileDrawable) this.staticThumb).getOrientation() : 0;
    }

    public int getOrientation() {
        return this.orientation;
    }

    public void setImageBitmap(Bitmap bitmap) {
        Drawable drawable = null;
        if (bitmap != null) {
            drawable = new BitmapDrawable(null, bitmap);
        }
        setImageBitmap(drawable);
    }

    public void setImageBitmap(Drawable drawable) {
        boolean z = false;
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
        for (int i = 0; i < 3; i++) {
            recycleBitmap(null, i);
        }
        this.staticThumb = drawable;
        if (this.roundRadius == 0 || !(drawable instanceof BitmapDrawable)) {
            this.bitmapShaderThumb = null;
        } else {
            this.bitmapShaderThumb = new BitmapShader(((BitmapDrawable) drawable).getBitmap(), TileMode.CLAMP, TileMode.CLAMP);
        }
        this.currentThumbLocation = null;
        this.currentKey = null;
        this.currentExt = null;
        this.currentThumbKey = null;
        this.currentImage = null;
        this.currentThumbFilter = null;
        this.currentImageLocation = null;
        this.currentHttpUrl = null;
        this.currentFilter = null;
        this.currentSize = 0;
        this.currentCacheType = 0;
        this.bitmapShader = null;
        this.crossfadeShader = null;
        if (this.setImageBackup != null) {
            this.setImageBackup.fileLocation = null;
            this.setImageBackup.httpUrl = null;
            this.setImageBackup.thumbLocation = null;
            this.setImageBackup.thumb = null;
        }
        this.currentAlpha = 1.0f;
        if (this.delegate != null) {
            drawable = this.delegate;
            if (!(this.currentThumb == null && this.staticThumb == null)) {
                z = true;
            }
            drawable.didSetImage(this, z, true);
        }
        if (this.parentView == null) {
            return;
        }
        if (this.invalidateAll != null) {
            this.parentView.invalidate();
        } else {
            this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
        }
    }

    public void clearImage() {
        for (int i = 0; i < 3; i++) {
            recycleBitmap(null, i);
        }
        if (this.needsQualityThumb) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.messageThumbGenerated);
        }
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
    }

    public void onDetachedFromWindow() {
        if (!(this.currentImageLocation == null && this.currentHttpUrl == null && this.currentThumbLocation == null && this.staticThumb == null)) {
            if (this.setImageBackup == null) {
                this.setImageBackup = new SetImageBackup();
            }
            this.setImageBackup.fileLocation = this.currentImageLocation;
            this.setImageBackup.httpUrl = this.currentHttpUrl;
            this.setImageBackup.filter = this.currentFilter;
            this.setImageBackup.thumb = this.staticThumb;
            this.setImageBackup.thumbLocation = this.currentThumbLocation;
            this.setImageBackup.thumbFilter = this.currentThumbFilter;
            this.setImageBackup.size = this.currentSize;
            this.setImageBackup.ext = this.currentExt;
            this.setImageBackup.cacheType = this.currentCacheType;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        clearImage();
    }

    public boolean onAttachedToWindow() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        if (this.needsQualityThumb) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.messageThumbGenerated);
        }
        if (this.setImageBackup == null || (this.setImageBackup.fileLocation == null && this.setImageBackup.httpUrl == null && this.setImageBackup.thumbLocation == null && this.setImageBackup.thumb == null)) {
            return false;
        }
        setImage(this.setImageBackup.fileLocation, this.setImageBackup.httpUrl, this.setImageBackup.filter, this.setImageBackup.thumb, this.setImageBackup.thumbLocation, this.setImageBackup.thumbFilter, this.setImageBackup.size, this.setImageBackup.ext, this.setImageBackup.cacheType);
        return true;
    }

    private void drawDrawable(Canvas canvas, Drawable drawable, int i, BitmapShader bitmapShader) {
        Throwable th;
        ImageReceiver imageReceiver = this;
        Canvas canvas2 = canvas;
        Drawable drawable2 = drawable;
        int i2 = i;
        Shader shader = bitmapShader;
        if (drawable2 instanceof BitmapDrawable) {
            Paint paint;
            int intrinsicWidth;
            int intrinsicHeight;
            Drawable drawable3 = (BitmapDrawable) drawable2;
            if (shader != null) {
                paint = imageReceiver.roundPaint;
            } else {
                paint = drawable3.getPaint();
            }
            Object obj = (paint == null || paint.getColorFilter() == null) ? null : 1;
            if (obj == null || imageReceiver.isPressed != 0) {
                if (obj == null && imageReceiver.isPressed != 0) {
                    if (imageReceiver.isPressed == 1) {
                        if (shader != null) {
                            imageReceiver.roundPaint.setColorFilter(selectedColorFilter);
                        } else {
                            drawable3.setColorFilter(selectedColorFilter);
                        }
                    } else if (shader != null) {
                        imageReceiver.roundPaint.setColorFilter(selectedGroupColorFilter);
                    } else {
                        drawable3.setColorFilter(selectedGroupColorFilter);
                    }
                }
            } else if (shader != null) {
                imageReceiver.roundPaint.setColorFilter(null);
            } else if (imageReceiver.staticThumb != drawable2) {
                drawable3.setColorFilter(null);
            }
            if (imageReceiver.colorFilter != null) {
                if (shader != null) {
                    imageReceiver.roundPaint.setColorFilter(imageReceiver.colorFilter);
                } else {
                    drawable3.setColorFilter(imageReceiver.colorFilter);
                }
            }
            boolean z = drawable3 instanceof AnimatedFileDrawable;
            if (z) {
                if (imageReceiver.orientation % 360 != 90) {
                    if (imageReceiver.orientation % 360 != 270) {
                        intrinsicWidth = drawable3.getIntrinsicWidth();
                        intrinsicHeight = drawable3.getIntrinsicHeight();
                    }
                }
                intrinsicWidth = drawable3.getIntrinsicHeight();
                intrinsicHeight = drawable3.getIntrinsicWidth();
            } else {
                if (imageReceiver.orientation % 360 != 90) {
                    if (imageReceiver.orientation % 360 != 270) {
                        intrinsicWidth = drawable3.getBitmap().getWidth();
                        intrinsicHeight = drawable3.getBitmap().getHeight();
                    }
                }
                intrinsicWidth = drawable3.getBitmap().getHeight();
                intrinsicHeight = drawable3.getBitmap().getWidth();
            }
            float f = (float) intrinsicWidth;
            float f2 = f / ((float) imageReceiver.imageW);
            float f3 = (float) intrinsicHeight;
            float f4 = f3 / ((float) imageReceiver.imageH);
            float min;
            int floor;
            if (shader != null) {
                imageReceiver.roundPaint.setShader(shader);
                min = Math.min(f2, f4);
                imageReceiver.roundRect.set((float) imageReceiver.imageX, (float) imageReceiver.imageY, (float) (imageReceiver.imageX + imageReceiver.imageW), (float) (imageReceiver.imageY + imageReceiver.imageH));
                imageReceiver.shaderMatrix.reset();
                float f5 = f2 - f4;
                if (Math.abs(f5) > 1.0E-5f) {
                    float f6 = f / f4;
                    if (f6 > ((float) imageReceiver.imageW)) {
                        int i3 = (int) f6;
                        imageReceiver.drawRegion.set(imageReceiver.imageX - ((i3 - imageReceiver.imageW) / 2), imageReceiver.imageY, imageReceiver.imageX + ((i3 + imageReceiver.imageW) / 2), imageReceiver.imageY + imageReceiver.imageH);
                    } else {
                        int i4 = (int) (f3 / f2);
                        imageReceiver.drawRegion.set(imageReceiver.imageX, imageReceiver.imageY - ((i4 - imageReceiver.imageH) / 2), imageReceiver.imageX + imageReceiver.imageW, imageReceiver.imageY + ((i4 + imageReceiver.imageH) / 2));
                    }
                } else {
                    imageReceiver.drawRegion.set(imageReceiver.imageX, imageReceiver.imageY, imageReceiver.imageX + imageReceiver.imageW, imageReceiver.imageY + imageReceiver.imageH);
                }
                if (imageReceiver.isVisible) {
                    if (Math.abs(f5) > 1.0E-5f) {
                        i2 = (int) Math.floor((double) (((float) imageReceiver.imageW) * min));
                        floor = (int) Math.floor((double) (((float) imageReceiver.imageH) * min));
                        imageReceiver.bitmapRect.set((float) ((intrinsicWidth - i2) / 2), (float) ((intrinsicHeight - floor) / 2), (float) ((intrinsicWidth + i2) / 2), (float) ((intrinsicHeight + floor) / 2));
                        imageReceiver.shaderMatrix.setRectToRect(imageReceiver.bitmapRect, imageReceiver.roundRect, ScaleToFit.START);
                    } else {
                        imageReceiver.bitmapRect.set(0.0f, 0.0f, f, f3);
                        imageReceiver.shaderMatrix.setRectToRect(imageReceiver.bitmapRect, imageReceiver.roundRect, ScaleToFit.FILL);
                    }
                    shader.setLocalMatrix(imageReceiver.shaderMatrix);
                    imageReceiver.roundPaint.setAlpha(i);
                    canvas.drawRoundRect(imageReceiver.roundRect, (float) imageReceiver.roundRadius, (float) imageReceiver.roundRadius, imageReceiver.roundPaint);
                    return;
                }
                return;
            } else if (imageReceiver.isAspectFit) {
                min = Math.max(f2, f4);
                canvas.save();
                r3 = (int) (f / min);
                floor = (int) (f3 / min);
                imageReceiver.drawRegion.set(imageReceiver.imageX + ((imageReceiver.imageW - r3) / 2), imageReceiver.imageY + ((imageReceiver.imageH - floor) / 2), imageReceiver.imageX + ((imageReceiver.imageW + r3) / 2), imageReceiver.imageY + ((imageReceiver.imageH + floor) / 2));
                drawable3.setBounds(imageReceiver.drawRegion);
                try {
                    drawable3.setAlpha(i2);
                    drawable3.draw(canvas2);
                } catch (Throwable e) {
                    th = e;
                    if (drawable3 == imageReceiver.currentImage && imageReceiver.currentKey != null) {
                        ImageLoader.getInstance().removeImage(imageReceiver.currentKey);
                        imageReceiver.currentKey = null;
                    } else if (drawable3 == imageReceiver.currentThumb && imageReceiver.currentThumbKey != null) {
                        ImageLoader.getInstance().removeImage(imageReceiver.currentThumbKey);
                        imageReceiver.currentThumbKey = null;
                    }
                    setImage(imageReceiver.currentImageLocation, imageReceiver.currentHttpUrl, imageReceiver.currentFilter, imageReceiver.currentThumb, imageReceiver.currentThumbLocation, imageReceiver.currentThumbFilter, imageReceiver.currentSize, imageReceiver.currentExt, imageReceiver.currentCacheType);
                    FileLog.m3e(th);
                }
                canvas.restore();
                return;
            } else if (Math.abs(f2 - f4) > 1.0E-5f) {
                canvas.save();
                canvas2.clipRect(imageReceiver.imageX, imageReceiver.imageY, imageReceiver.imageX + imageReceiver.imageW, imageReceiver.imageY + imageReceiver.imageH);
                if (imageReceiver.orientation % 360 != 0) {
                    if (imageReceiver.centerRotation) {
                        canvas2.rotate((float) imageReceiver.orientation, (float) (imageReceiver.imageW / 2), (float) (imageReceiver.imageH / 2));
                    } else {
                        canvas2.rotate((float) imageReceiver.orientation, 0.0f, 0.0f);
                    }
                }
                f /= f4;
                if (f > ((float) imageReceiver.imageW)) {
                    r3 = (int) f;
                    imageReceiver.drawRegion.set(imageReceiver.imageX - ((r3 - imageReceiver.imageW) / 2), imageReceiver.imageY, imageReceiver.imageX + ((r3 + imageReceiver.imageW) / 2), imageReceiver.imageY + imageReceiver.imageH);
                } else {
                    r3 = (int) (f3 / f2);
                    imageReceiver.drawRegion.set(imageReceiver.imageX, imageReceiver.imageY - ((r3 - imageReceiver.imageH) / 2), imageReceiver.imageX + imageReceiver.imageW, imageReceiver.imageY + ((r3 + imageReceiver.imageH) / 2));
                }
                if (z) {
                    ((AnimatedFileDrawable) drawable3).setActualDrawRect(imageReceiver.imageX, imageReceiver.imageY, imageReceiver.imageW, imageReceiver.imageH);
                }
                if (imageReceiver.orientation % 360 != 90) {
                    if (imageReceiver.orientation % 360 != 270) {
                        drawable3.setBounds(imageReceiver.drawRegion);
                        if (imageReceiver.isVisible) {
                            try {
                                drawable3.setAlpha(i2);
                                drawable3.draw(canvas2);
                            } catch (Throwable e2) {
                                th = e2;
                                if (drawable3 == imageReceiver.currentImage && imageReceiver.currentKey != null) {
                                    ImageLoader.getInstance().removeImage(imageReceiver.currentKey);
                                    imageReceiver.currentKey = null;
                                } else if (drawable3 == imageReceiver.currentThumb && imageReceiver.currentThumbKey != null) {
                                    ImageLoader.getInstance().removeImage(imageReceiver.currentThumbKey);
                                    imageReceiver.currentThumbKey = null;
                                }
                                setImage(imageReceiver.currentImageLocation, imageReceiver.currentHttpUrl, imageReceiver.currentFilter, imageReceiver.currentThumb, imageReceiver.currentThumbLocation, imageReceiver.currentThumbFilter, imageReceiver.currentSize, imageReceiver.currentExt, imageReceiver.currentCacheType);
                                FileLog.m3e(th);
                            }
                        }
                        canvas.restore();
                        return;
                    }
                }
                floor = (imageReceiver.drawRegion.right - imageReceiver.drawRegion.left) / 2;
                r3 = (imageReceiver.drawRegion.bottom - imageReceiver.drawRegion.top) / 2;
                r5 = (imageReceiver.drawRegion.right + imageReceiver.drawRegion.left) / 2;
                r6 = (imageReceiver.drawRegion.top + imageReceiver.drawRegion.bottom) / 2;
                drawable3.setBounds(r5 - r3, r6 - floor, r5 + r3, r6 + floor);
                if (imageReceiver.isVisible) {
                    drawable3.setAlpha(i2);
                    drawable3.draw(canvas2);
                }
                canvas.restore();
                return;
            } else {
                canvas.save();
                if (imageReceiver.orientation % 360 != 0) {
                    if (imageReceiver.centerRotation) {
                        canvas2.rotate((float) imageReceiver.orientation, (float) (imageReceiver.imageW / 2), (float) (imageReceiver.imageH / 2));
                    } else {
                        canvas2.rotate((float) imageReceiver.orientation, 0.0f, 0.0f);
                    }
                }
                imageReceiver.drawRegion.set(imageReceiver.imageX, imageReceiver.imageY, imageReceiver.imageX + imageReceiver.imageW, imageReceiver.imageY + imageReceiver.imageH);
                if (z) {
                    ((AnimatedFileDrawable) drawable3).setActualDrawRect(imageReceiver.imageX, imageReceiver.imageY, imageReceiver.imageW, imageReceiver.imageH);
                }
                if (imageReceiver.orientation % 360 != 90) {
                    if (imageReceiver.orientation % 360 != 270) {
                        drawable3.setBounds(imageReceiver.drawRegion);
                        if (imageReceiver.isVisible) {
                            try {
                                drawable3.setAlpha(i2);
                                drawable3.draw(canvas2);
                            } catch (Throwable e22) {
                                th = e22;
                                if (drawable3 == imageReceiver.currentImage && imageReceiver.currentKey != null) {
                                    ImageLoader.getInstance().removeImage(imageReceiver.currentKey);
                                    imageReceiver.currentKey = null;
                                } else if (drawable3 == imageReceiver.currentThumb && imageReceiver.currentThumbKey != null) {
                                    ImageLoader.getInstance().removeImage(imageReceiver.currentThumbKey);
                                    imageReceiver.currentThumbKey = null;
                                }
                                setImage(imageReceiver.currentImageLocation, imageReceiver.currentHttpUrl, imageReceiver.currentFilter, imageReceiver.currentThumb, imageReceiver.currentThumbLocation, imageReceiver.currentThumbFilter, imageReceiver.currentSize, imageReceiver.currentExt, imageReceiver.currentCacheType);
                                FileLog.m3e(th);
                            }
                        }
                        canvas.restore();
                        return;
                    }
                }
                floor = (imageReceiver.drawRegion.right - imageReceiver.drawRegion.left) / 2;
                r3 = (imageReceiver.drawRegion.bottom - imageReceiver.drawRegion.top) / 2;
                r5 = (imageReceiver.drawRegion.right + imageReceiver.drawRegion.left) / 2;
                r6 = (imageReceiver.drawRegion.top + imageReceiver.drawRegion.bottom) / 2;
                drawable3.setBounds(r5 - r3, r6 - floor, r5 + r3, r6 + floor);
                if (imageReceiver.isVisible) {
                    drawable3.setAlpha(i2);
                    drawable3.draw(canvas2);
                }
                canvas.restore();
                return;
            }
        }
        imageReceiver.drawRegion.set(imageReceiver.imageX, imageReceiver.imageY, imageReceiver.imageX + imageReceiver.imageW, imageReceiver.imageY + imageReceiver.imageH);
        drawable2.setBounds(imageReceiver.drawRegion);
        if (imageReceiver.isVisible) {
            try {
                drawable.setAlpha(i);
                drawable2.draw(canvas2);
            } catch (Throwable e222) {
                FileLog.m3e(e222);
            }
        }
    }

    private void checkAlphaAnimation(boolean z) {
        if (!(this.manualAlphaAnimator || this.currentAlpha == 1.0f)) {
            if (!z) {
                long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateAlphaTime;
                long j = 18;
                if (currentTimeMillis <= 18) {
                    j = currentTimeMillis;
                }
                this.currentAlpha += ((float) j) / 150.0f;
                if (this.currentAlpha > true) {
                    this.currentAlpha = 1.0f;
                    if (this.crossfadeImage) {
                        recycleBitmap(null, true);
                        this.crossfadeShader = null;
                    }
                }
            }
            this.lastUpdateAlphaTime = System.currentTimeMillis();
            if (this.parentView) {
                if (this.invalidateAll) {
                    this.parentView.invalidate();
                } else {
                    this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                }
            }
        }
    }

    public boolean draw(Canvas canvas) {
        try {
            Drawable drawable;
            BitmapShader bitmapShader;
            boolean z;
            int i;
            Drawable drawable2;
            int i2;
            boolean z2 = (this.currentImage instanceof AnimatedFileDrawable) && !((AnimatedFileDrawable) this.currentImage).hasBitmap();
            BitmapShader bitmapShader2 = null;
            if (this.forcePreview || this.currentImage == null || z2) {
                if (this.crossfadeImage != null) {
                    drawable = this.crossfadeImage;
                    bitmapShader = this.crossfadeShader;
                    z = false;
                } else {
                    if (this.staticThumb instanceof BitmapDrawable) {
                        drawable = this.staticThumb;
                    } else if (this.currentThumb != null) {
                        drawable = this.currentThumb;
                    } else {
                        z = false;
                        drawable = null;
                        bitmapShader = drawable;
                    }
                    z = true;
                }
                if (drawable != null) {
                    if (this.crossfadeAlpha != (byte) 0) {
                        i = (int) (this.overrideAlpha * 255.0f);
                        if (bitmapShader != null) {
                            bitmapShader = z ? this.bitmapShaderThumb : this.bitmapShader;
                        }
                        drawDrawable(canvas, drawable, i, bitmapShader);
                    } else if (this.crossfadeWithThumb || !z2) {
                        if (this.crossfadeWithThumb && this.currentAlpha != 1.0f) {
                            if (drawable != this.currentImage) {
                                if (this.crossfadeImage != null) {
                                    drawable2 = this.crossfadeImage;
                                    bitmapShader2 = this.crossfadeShader;
                                } else if (this.staticThumb != null) {
                                    drawable2 = this.staticThumb;
                                } else if (this.currentThumb != null) {
                                    drawable2 = this.currentThumb;
                                }
                                if (drawable2 != null) {
                                    i2 = (int) (this.overrideAlpha * 255.0f);
                                    if (bitmapShader2 != null) {
                                        bitmapShader2 = this.bitmapShaderThumb;
                                    }
                                    drawDrawable(canvas, drawable2, i2, bitmapShader2);
                                }
                            } else if (drawable == this.currentThumb && this.staticThumb != null) {
                                drawable2 = this.staticThumb;
                                if (drawable2 != null) {
                                    i2 = (int) (this.overrideAlpha * 255.0f);
                                    if (bitmapShader2 != null) {
                                        bitmapShader2 = this.bitmapShaderThumb;
                                    }
                                    drawDrawable(canvas, drawable2, i2, bitmapShader2);
                                }
                            }
                            drawable2 = null;
                            if (drawable2 != null) {
                                i2 = (int) (this.overrideAlpha * 255.0f);
                                if (bitmapShader2 != null) {
                                    bitmapShader2 = this.bitmapShaderThumb;
                                }
                                drawDrawable(canvas, drawable2, i2, bitmapShader2);
                            }
                        }
                        i = (int) ((this.overrideAlpha * this.currentAlpha) * 255.0f);
                        if (bitmapShader == null) {
                            bitmapShader = z ? this.bitmapShaderThumb : this.bitmapShader;
                        }
                        drawDrawable(canvas, drawable, i, bitmapShader);
                    } else {
                        drawDrawable(canvas, drawable, (int) (this.overrideAlpha * 255.0f), this.bitmapShaderThumb);
                    }
                    canvas = (z2 || this.crossfadeWithThumb == null) ? null : 1;
                    checkAlphaAnimation(canvas);
                    return true;
                } else if (this.staticThumb == null) {
                    drawDrawable(canvas, this.staticThumb, 255, null);
                    checkAlphaAnimation(z2);
                    return true;
                } else {
                    checkAlphaAnimation(z2);
                    return false;
                }
            }
            drawable = this.currentImage;
            z = false;
            bitmapShader = null;
            if (drawable != null) {
                if (this.crossfadeAlpha != (byte) 0) {
                    i = (int) (this.overrideAlpha * 255.0f);
                    if (bitmapShader != null) {
                        if (z) {
                        }
                    }
                    drawDrawable(canvas, drawable, i, bitmapShader);
                } else {
                    if (this.crossfadeWithThumb) {
                    }
                    if (drawable != this.currentImage) {
                        drawable2 = this.staticThumb;
                        if (drawable2 != null) {
                            i2 = (int) (this.overrideAlpha * 255.0f);
                            if (bitmapShader2 != null) {
                                bitmapShader2 = this.bitmapShaderThumb;
                            }
                            drawDrawable(canvas, drawable2, i2, bitmapShader2);
                        }
                        i = (int) ((this.overrideAlpha * this.currentAlpha) * 255.0f);
                        if (bitmapShader == null) {
                            if (z) {
                            }
                        }
                        drawDrawable(canvas, drawable, i, bitmapShader);
                    } else {
                        if (this.crossfadeImage != null) {
                            drawable2 = this.crossfadeImage;
                            bitmapShader2 = this.crossfadeShader;
                        } else if (this.staticThumb != null) {
                            drawable2 = this.staticThumb;
                        } else if (this.currentThumb != null) {
                            drawable2 = this.currentThumb;
                        }
                        if (drawable2 != null) {
                            i2 = (int) (this.overrideAlpha * 255.0f);
                            if (bitmapShader2 != null) {
                                bitmapShader2 = this.bitmapShaderThumb;
                            }
                            drawDrawable(canvas, drawable2, i2, bitmapShader2);
                        }
                        i = (int) ((this.overrideAlpha * this.currentAlpha) * 255.0f);
                        if (bitmapShader == null) {
                            if (z) {
                            }
                        }
                        drawDrawable(canvas, drawable, i, bitmapShader);
                    }
                    drawable2 = null;
                    if (drawable2 != null) {
                        i2 = (int) (this.overrideAlpha * 255.0f);
                        if (bitmapShader2 != null) {
                            bitmapShader2 = this.bitmapShaderThumb;
                        }
                        drawDrawable(canvas, drawable2, i2, bitmapShader2);
                    }
                    i = (int) ((this.overrideAlpha * this.currentAlpha) * 255.0f);
                    if (bitmapShader == null) {
                        if (z) {
                        }
                    }
                    drawDrawable(canvas, drawable, i, bitmapShader);
                }
                if (z2) {
                }
                checkAlphaAnimation(canvas);
                return true;
            } else if (this.staticThumb == null) {
                checkAlphaAnimation(z2);
                return false;
            } else {
                drawDrawable(canvas, this.staticThumb, 255, null);
                checkAlphaAnimation(z2);
                return true;
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public void setManualAlphaAnimator(boolean z) {
        this.manualAlphaAnimator = z;
    }

    public float getCurrentAlpha() {
        return this.currentAlpha;
    }

    public void setCurrentAlpha(float f) {
        this.currentAlpha = f;
    }

    public Bitmap getBitmap() {
        if (this.currentImage instanceof AnimatedFileDrawable) {
            return ((AnimatedFileDrawable) this.currentImage).getAnimatedBitmap();
        }
        if (this.staticThumb instanceof AnimatedFileDrawable) {
            return ((AnimatedFileDrawable) this.staticThumb).getAnimatedBitmap();
        }
        if (this.currentImage instanceof BitmapDrawable) {
            return ((BitmapDrawable) this.currentImage).getBitmap();
        }
        if (this.currentThumb instanceof BitmapDrawable) {
            return ((BitmapDrawable) this.currentThumb).getBitmap();
        }
        return this.staticThumb instanceof BitmapDrawable ? ((BitmapDrawable) this.staticThumb).getBitmap() : null;
    }

    public BitmapHolder getBitmapSafe() {
        Bitmap animatedBitmap;
        String str;
        if (this.currentImage instanceof AnimatedFileDrawable) {
            animatedBitmap = ((AnimatedFileDrawable) this.currentImage).getAnimatedBitmap();
        } else if (this.staticThumb instanceof AnimatedFileDrawable) {
            animatedBitmap = ((AnimatedFileDrawable) this.staticThumb).getAnimatedBitmap();
        } else {
            if (this.currentImage instanceof BitmapDrawable) {
                animatedBitmap = ((BitmapDrawable) this.currentImage).getBitmap();
                str = this.currentKey;
            } else if (this.currentThumb instanceof BitmapDrawable) {
                animatedBitmap = ((BitmapDrawable) this.currentThumb).getBitmap();
                str = this.currentThumbKey;
            } else if (this.staticThumb instanceof BitmapDrawable) {
                animatedBitmap = ((BitmapDrawable) this.staticThumb).getBitmap();
            } else {
                animatedBitmap = null;
                str = animatedBitmap;
            }
            if (animatedBitmap == null) {
                return new BitmapHolder(animatedBitmap, str);
            }
            return null;
        }
        str = null;
        if (animatedBitmap == null) {
            return null;
        }
        return new BitmapHolder(animatedBitmap, str);
    }

    public Bitmap getThumbBitmap() {
        if (this.currentThumb instanceof BitmapDrawable) {
            return ((BitmapDrawable) this.currentThumb).getBitmap();
        }
        return this.staticThumb instanceof BitmapDrawable ? ((BitmapDrawable) this.staticThumb).getBitmap() : null;
    }

    public BitmapHolder getThumbBitmapSafe() {
        Bitmap bitmap;
        String str;
        if (this.currentThumb instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) this.currentThumb).getBitmap();
            str = this.currentThumbKey;
        } else if (this.staticThumb instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) this.staticThumb).getBitmap();
            str = null;
        } else {
            bitmap = null;
            str = bitmap;
        }
        if (bitmap != null) {
            return new BitmapHolder(bitmap, str);
        }
        return null;
    }

    public int getBitmapWidth() {
        int intrinsicHeight;
        if (this.currentImage instanceof AnimatedFileDrawable) {
            if (this.orientation % 360 != 0) {
                if (this.orientation % 360 != 180) {
                    intrinsicHeight = this.currentImage.getIntrinsicHeight();
                    return intrinsicHeight;
                }
            }
            intrinsicHeight = this.currentImage.getIntrinsicWidth();
            return intrinsicHeight;
        } else if (this.staticThumb instanceof AnimatedFileDrawable) {
            if (this.orientation % 360 != 0) {
                if (this.orientation % 360 != 180) {
                    intrinsicHeight = this.staticThumb.getIntrinsicHeight();
                    return intrinsicHeight;
                }
            }
            intrinsicHeight = this.staticThumb.getIntrinsicWidth();
            return intrinsicHeight;
        } else {
            Bitmap bitmap = getBitmap();
            if (bitmap == null) {
                return this.staticThumb != null ? this.staticThumb.getIntrinsicWidth() : 1;
            } else {
                if (this.orientation % 360 != 0) {
                    if (this.orientation % 360 != 180) {
                        intrinsicHeight = bitmap.getHeight();
                        return intrinsicHeight;
                    }
                }
                intrinsicHeight = bitmap.getWidth();
                return intrinsicHeight;
            }
        }
    }

    public int getBitmapHeight() {
        int intrinsicWidth;
        if (this.currentImage instanceof AnimatedFileDrawable) {
            if (this.orientation % 360 != 0) {
                if (this.orientation % 360 != 180) {
                    intrinsicWidth = this.currentImage.getIntrinsicWidth();
                    return intrinsicWidth;
                }
            }
            intrinsicWidth = this.currentImage.getIntrinsicHeight();
            return intrinsicWidth;
        } else if (this.staticThumb instanceof AnimatedFileDrawable) {
            if (this.orientation % 360 != 0) {
                if (this.orientation % 360 != 180) {
                    intrinsicWidth = this.staticThumb.getIntrinsicWidth();
                    return intrinsicWidth;
                }
            }
            intrinsicWidth = this.staticThumb.getIntrinsicHeight();
            return intrinsicWidth;
        } else {
            Bitmap bitmap = getBitmap();
            if (bitmap == null) {
                return this.staticThumb != null ? this.staticThumb.getIntrinsicHeight() : 1;
            } else {
                if (this.orientation % 360 != 0) {
                    if (this.orientation % 360 != 180) {
                        intrinsicWidth = bitmap.getWidth();
                        return intrinsicWidth;
                    }
                }
                intrinsicWidth = bitmap.getHeight();
                return intrinsicWidth;
            }
        }
    }

    public void setVisible(boolean z, boolean z2) {
        if (this.isVisible != z) {
            this.isVisible = z;
            if (z2 && this.parentView) {
                if (this.invalidateAll) {
                    this.parentView.invalidate();
                } else {
                    this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                }
            }
        }
    }

    public boolean getVisible() {
        return this.isVisible;
    }

    public void setAlpha(float f) {
        this.overrideAlpha = f;
    }

    public void setCrossfadeAlpha(byte b) {
        this.crossfadeAlpha = b;
    }

    public boolean hasImage() {
        if (this.currentImage == null && this.currentThumb == null && this.currentKey == null && this.currentHttpUrl == null) {
            if (this.staticThumb == null) {
                return false;
            }
        }
        return true;
    }

    public boolean hasBitmapImage() {
        if (this.currentImage == null && this.currentThumb == null) {
            if (this.staticThumb == null) {
                return false;
            }
        }
        return true;
    }

    public void setAspectFit(boolean z) {
        this.isAspectFit = z;
    }

    public void setParentView(View view) {
        this.parentView = view;
        if ((this.currentImage instanceof AnimatedFileDrawable) != null) {
            ((AnimatedFileDrawable) this.currentImage).setParentView(this.parentView);
        }
    }

    public void setImageX(int i) {
        this.imageX = i;
    }

    public void setImageY(int i) {
        this.imageY = i;
    }

    public void setImageWidth(int i) {
        this.imageW = i;
    }

    public void setImageCoords(int i, int i2, int i3, int i4) {
        this.imageX = i;
        this.imageY = i2;
        this.imageW = i3;
        this.imageH = i4;
    }

    public float getCenterX() {
        return ((float) this.imageX) + (((float) this.imageW) / 2.0f);
    }

    public float getCenterY() {
        return ((float) this.imageY) + (((float) this.imageH) / 2.0f);
    }

    public int getImageX() {
        return this.imageX;
    }

    public int getImageX2() {
        return this.imageX + this.imageW;
    }

    public int getImageY() {
        return this.imageY;
    }

    public int getImageY2() {
        return this.imageY + this.imageH;
    }

    public int getImageWidth() {
        return this.imageW;
    }

    public int getImageHeight() {
        return this.imageH;
    }

    public String getExt() {
        return this.currentExt;
    }

    public boolean isInsideImage(float f, float f2) {
        return f >= ((float) this.imageX) && f <= ((float) (this.imageX + this.imageW)) && f2 >= ((float) this.imageY) && f2 <= ((float) (this.imageY + this.imageH));
    }

    public Rect getDrawRegion() {
        return this.drawRegion;
    }

    public String getFilter() {
        return this.currentFilter;
    }

    public String getThumbFilter() {
        return this.currentThumbFilter;
    }

    public String getKey() {
        return this.currentKey;
    }

    public String getThumbKey() {
        return this.currentThumbKey;
    }

    public int getSize() {
        return this.currentSize;
    }

    public TLObject getImageLocation() {
        return this.currentImageLocation;
    }

    public FileLocation getThumbLocation() {
        return this.currentThumbLocation;
    }

    public String getHttpImageLocation() {
        return this.currentHttpUrl;
    }

    public int getCacheType() {
        return this.currentCacheType;
    }

    public void setForcePreview(boolean z) {
        this.forcePreview = z;
    }

    public void setForceCrossfade(boolean z) {
        this.forceCrossfade = z;
    }

    public boolean isForcePreview() {
        return this.forcePreview;
    }

    public void setRoundRadius(int i) {
        this.roundRadius = i;
    }

    public void setCurrentAccount(int i) {
        this.currentAccount = i;
    }

    public int getRoundRadius() {
        return this.roundRadius;
    }

    public void setParentMessageObject(MessageObject messageObject) {
        this.parentMessageObject = messageObject;
    }

    public MessageObject getParentMessageObject() {
        return this.parentMessageObject;
    }

    public void setNeedsQualityThumb(boolean z) {
        this.needsQualityThumb = z;
        if (this.needsQualityThumb) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.messageThumbGenerated);
        } else {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.messageThumbGenerated);
        }
    }

    public void setCrossfadeWithOldImage(boolean z) {
        this.crossfadeWithOldImage = z;
    }

    public boolean isNeedsQualityThumb() {
        return this.needsQualityThumb;
    }

    public int getcurrentAccount() {
        return this.currentAccount;
    }

    public void setShouldGenerateQualityThumb(boolean z) {
        this.shouldGenerateQualityThumb = z;
    }

    public boolean isShouldGenerateQualityThumb() {
        return this.shouldGenerateQualityThumb;
    }

    public void setAllowStartAnimation(boolean z) {
        this.allowStartAnimation = z;
    }

    public void setAllowDecodeSingleFrame(boolean z) {
        this.allowDecodeSingleFrame = z;
    }

    public boolean isAllowStartAnimation() {
        return this.allowStartAnimation;
    }

    public void startAnimation() {
        if (this.currentImage instanceof AnimatedFileDrawable) {
            ((AnimatedFileDrawable) this.currentImage).start();
        }
    }

    public void stopAnimation() {
        if (this.currentImage instanceof AnimatedFileDrawable) {
            ((AnimatedFileDrawable) this.currentImage).stop();
        }
    }

    public boolean isAnimationRunning() {
        return (this.currentImage instanceof AnimatedFileDrawable) && ((AnimatedFileDrawable) this.currentImage).isRunning();
    }

    public AnimatedFileDrawable getAnimation() {
        return this.currentImage instanceof AnimatedFileDrawable ? (AnimatedFileDrawable) this.currentImage : null;
    }

    protected int getTag(boolean z) {
        if (z) {
            return this.thumbTag;
        }
        return this.tag;
    }

    protected void setTag(int i, boolean z) {
        if (z) {
            this.thumbTag = i;
        } else {
            this.tag = i;
        }
    }

    public void setParam(int i) {
        this.param = i;
    }

    public int getParam() {
        return this.param;
    }

    protected boolean setImageBitmapByKey(BitmapDrawable bitmapDrawable, String str, boolean z, boolean z2) {
        boolean z3 = false;
        if (bitmapDrawable != null) {
            if (str != null) {
                if (!z) {
                    if (this.currentKey) {
                        if (str.equals(this.currentKey) != null) {
                            str = bitmapDrawable instanceof AnimatedFileDrawable;
                            if (str == null) {
                                ImageLoader.getInstance().incrementUseCount(this.currentKey);
                            }
                            this.currentImage = bitmapDrawable;
                            if (!this.roundRadius || !(bitmapDrawable instanceof BitmapDrawable)) {
                                this.bitmapShader = null;
                            } else if (str != null) {
                                ((AnimatedFileDrawable) bitmapDrawable).setRoundRadius(this.roundRadius);
                            } else {
                                this.bitmapShader = new BitmapShader(bitmapDrawable.getBitmap(), TileMode.CLAMP, TileMode.CLAMP);
                            }
                            if ((z2 || this.forcePreview) && !this.forceCrossfade) {
                                this.currentAlpha = 1.0f;
                            } else if (!(this.currentThumb || this.staticThumb) || this.currentAlpha || this.forceCrossfade) {
                                this.currentAlpha = 0.0f;
                                this.lastUpdateAlphaTime = System.currentTimeMillis();
                                if (!this.currentThumb) {
                                    if (!this.staticThumb) {
                                        z = false;
                                        this.crossfadeWithThumb = z;
                                    }
                                }
                                z = true;
                                this.crossfadeWithThumb = z;
                            }
                            if (str != null) {
                                AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) bitmapDrawable;
                                animatedFileDrawable.setParentView(this.parentView);
                                if (this.allowStartAnimation != null) {
                                    animatedFileDrawable.start();
                                } else {
                                    animatedFileDrawable.setAllowDecodeSingleFrame(this.allowDecodeSingleFrame);
                                }
                            }
                            if (this.parentView != null) {
                                if (this.invalidateAll != null) {
                                    this.parentView.invalidate();
                                } else {
                                    this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                                }
                            }
                        }
                    }
                    return false;
                } else if (!this.currentThumb && (!this.currentImage || (((this.currentImage instanceof AnimatedFileDrawable) && !((AnimatedFileDrawable) this.currentImage).hasBitmap()) || this.forcePreview))) {
                    if (this.currentThumbKey) {
                        if (str.equals(this.currentThumbKey) != null) {
                            ImageLoader.getInstance().incrementUseCount(this.currentThumbKey);
                            this.currentThumb = bitmapDrawable;
                            if (this.roundRadius == null || (bitmapDrawable instanceof BitmapDrawable) == null) {
                                this.bitmapShaderThumb = null;
                            } else if ((bitmapDrawable instanceof AnimatedFileDrawable) != null) {
                                ((AnimatedFileDrawable) bitmapDrawable).setRoundRadius(this.roundRadius);
                            } else {
                                this.bitmapShaderThumb = new BitmapShader(bitmapDrawable.getBitmap(), TileMode.CLAMP, TileMode.CLAMP);
                            }
                            if (z2 || this.crossfadeAlpha == 2) {
                                this.currentAlpha = 1.0f;
                            } else if (this.parentMessageObject == null || this.parentMessageObject.isRoundVideo() == null || this.parentMessageObject.isSending() == null) {
                                this.currentAlpha = 0.0f;
                                this.lastUpdateAlphaTime = System.currentTimeMillis();
                                bitmapDrawable = (this.staticThumb == null || this.currentKey != null) ? null : 1;
                                this.crossfadeWithThumb = bitmapDrawable;
                            } else {
                                this.currentAlpha = 1.0f;
                            }
                            if ((this.staticThumb instanceof BitmapDrawable) == null && this.parentView != null) {
                                if (this.invalidateAll != null) {
                                    this.parentView.invalidate();
                                } else {
                                    this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                                }
                            }
                        }
                    }
                    return false;
                }
                if (this.delegate != null) {
                    bitmapDrawable = this.delegate;
                    if (this.currentImage == null && this.currentThumb == null) {
                        if (this.staticThumb == null) {
                            str = null;
                            if (!this.currentImage) {
                                z3 = true;
                            }
                            bitmapDrawable.didSetImage(this, str, z3);
                        }
                    }
                    str = 1;
                    if (this.currentImage) {
                        z3 = true;
                    }
                    bitmapDrawable.didSetImage(this, str, z3);
                }
                return true;
            }
        }
        return false;
    }

    private void recycleBitmap(String str, int i) {
        String str2;
        Drawable drawable;
        if (i == 2) {
            str2 = this.crossfadeKey;
            drawable = this.crossfadeImage;
        } else if (i == 1) {
            str2 = this.currentThumbKey;
            drawable = this.currentThumb;
        } else {
            str2 = this.currentKey;
            drawable = this.currentImage;
        }
        if (str2 != null && ((str == null || str.equals(str2) == null) && drawable != null)) {
            if ((drawable instanceof AnimatedFileDrawable) != null) {
                ((AnimatedFileDrawable) drawable).recycle();
            } else if ((drawable instanceof BitmapDrawable) != null) {
                str = ((BitmapDrawable) drawable).getBitmap();
                boolean decrementUseCount = ImageLoader.getInstance().decrementUseCount(str2);
                if (!ImageLoader.getInstance().isInCache(str2) && decrementUseCount) {
                    str.recycle();
                }
            }
        }
        if (i == 2) {
            this.crossfadeKey = null;
            this.crossfadeImage = null;
        } else if (i == 1) {
            this.currentThumb = null;
            this.currentThumbKey = null;
        } else {
            this.currentImage = null;
            this.currentKey = null;
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        if (i == NotificationCenter.messageThumbGenerated) {
            str = (String) objArr[1];
            if (this.currentThumbKey != 0 && this.currentThumbKey.equals(str) != 0) {
                if (this.currentThumb == 0) {
                    ImageLoader.getInstance().incrementUseCount(this.currentThumbKey);
                }
                this.currentThumb = (BitmapDrawable) objArr[0];
                if (this.roundRadius == 0 || this.currentImage != 0 || (this.currentThumb instanceof BitmapDrawable) == 0 || (this.currentThumb instanceof AnimatedFileDrawable) != 0) {
                    this.bitmapShaderThumb = null;
                } else {
                    this.bitmapShaderThumb = new BitmapShader(((BitmapDrawable) this.currentThumb).getBitmap(), TileMode.CLAMP, TileMode.CLAMP);
                }
                if ((this.staticThumb instanceof BitmapDrawable) != 0) {
                    this.staticThumb = null;
                }
                if (this.parentView == 0) {
                    return;
                }
                if (this.invalidateAll != 0) {
                    this.parentView.invalidate();
                } else {
                    this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                }
            }
        } else if (i == NotificationCenter.didReplacedPhotoInMemCache) {
            str = (String) objArr[0];
            if (!(this.currentKey == 0 || this.currentKey.equals(str) == 0)) {
                this.currentKey = (String) objArr[1];
                this.currentImageLocation = (FileLocation) objArr[2];
            }
            if (!(this.currentThumbKey == 0 || this.currentThumbKey.equals(str) == 0)) {
                this.currentThumbKey = (String) objArr[1];
                this.currentThumbLocation = (FileLocation) objArr[2];
            }
            if (this.setImageBackup != 0) {
                if (!(this.currentKey == 0 || this.currentKey.equals(str) == 0)) {
                    this.currentKey = (String) objArr[1];
                    this.currentImageLocation = (FileLocation) objArr[2];
                }
                if (this.currentThumbKey != 0 && this.currentThumbKey.equals(str) != 0) {
                    this.currentThumbKey = (String) objArr[1];
                    this.currentThumbLocation = (FileLocation) objArr[2];
                }
            }
        }
    }
}
