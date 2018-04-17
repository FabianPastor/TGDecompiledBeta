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

        public BitmapHolder(Bitmap b, String k) {
            this.bitmap = b;
            this.key = k;
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
            boolean canDelete = ImageLoader.getInstance().decrementUseCount(this.key);
            if (!ImageLoader.getInstance().isInCache(this.key) && canDelete) {
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

    public void setForceLoading(boolean value) {
        this.forceLoding = value;
    }

    public boolean isForceLoding() {
        return this.forceLoding;
    }

    public void setImage(TLObject path, String filter, Drawable thumb, String ext, int cacheType) {
        setImage(path, null, filter, thumb, null, null, 0, ext, cacheType);
    }

    public void setImage(TLObject path, String filter, Drawable thumb, int size, String ext, int cacheType) {
        setImage(path, null, filter, thumb, null, null, size, ext, cacheType);
    }

    public void setImage(String httpUrl, String filter, Drawable thumb, String ext, int size) {
        setImage(null, httpUrl, filter, thumb, null, null, size, ext, 1);
    }

    public void setImage(TLObject fileLocation, String filter, FileLocation thumbLocation, String thumbFilter, String ext, int cacheType) {
        setImage(fileLocation, null, filter, null, thumbLocation, thumbFilter, 0, ext, cacheType);
    }

    public void setImage(TLObject fileLocation, String filter, FileLocation thumbLocation, String thumbFilter, int size, String ext, int cacheType) {
        setImage(fileLocation, null, filter, null, thumbLocation, thumbFilter, size, ext, cacheType);
    }

    public void setImage(TLObject fileLocation, String httpUrl, String filter, Drawable thumb, FileLocation thumbLocation, String thumbFilter, int size, String ext, int cacheType) {
        TLObject fileLocation2 = fileLocation;
        String str = httpUrl;
        String str2 = filter;
        Drawable drawable = thumb;
        FileLocation thumbLocation2 = thumbLocation;
        String str3 = thumbFilter;
        String str4 = ext;
        if (this.setImageBackup != null) {
            r0.setImageBackup.fileLocation = null;
            r0.setImageBackup.httpUrl = null;
            r0.setImageBackup.thumbLocation = null;
            r0.setImageBackup.thumb = null;
        }
        boolean z = true;
        ImageReceiverDelegate imageReceiverDelegate;
        boolean z2;
        if (!(fileLocation2 == null && str == null && thumbLocation2 == null) && (fileLocation2 == null || (fileLocation2 instanceof TL_fileLocation) || (fileLocation2 instanceof TL_fileEncryptedLocation) || (fileLocation2 instanceof TL_document) || (fileLocation2 instanceof TL_webDocument) || (fileLocation2 instanceof TL_documentEncrypted))) {
            String key;
            boolean z3;
            if (!((thumbLocation2 instanceof TL_fileLocation) || (thumbLocation2 instanceof TL_fileEncryptedLocation))) {
                thumbLocation2 = null;
            }
            String key2;
            if (fileLocation2 == null) {
                key2 = null;
                if (str != null) {
                    key = Utilities.MD5(httpUrl);
                } else {
                    key = key2;
                }
            } else if (fileLocation2 instanceof FileLocation) {
                FileLocation location = (FileLocation) fileLocation2;
                StringBuilder stringBuilder = new StringBuilder();
                key2 = null;
                stringBuilder.append(location.volume_id);
                stringBuilder.append("_");
                stringBuilder.append(location.local_id);
                key = stringBuilder.toString();
            } else {
                key2 = null;
                if (fileLocation2 instanceof TL_webDocument) {
                    key = Utilities.MD5(((TL_webDocument) fileLocation2).url);
                } else {
                    Document location2 = (Document) fileLocation2;
                    if (location2.dc_id == 0) {
                        fileLocation2 = null;
                        key = key2;
                    } else if (location2.version == 0) {
                        r12 = new StringBuilder();
                        r12.append(location2.dc_id);
                        r12.append("_");
                        r12.append(location2.id);
                        key = r12.toString();
                    } else {
                        r12 = new StringBuilder();
                        r12.append(location2.dc_id);
                        r12.append("_");
                        r12.append(location2.id);
                        r12.append("_");
                        r12.append(location2.version);
                        key = r12.toString();
                    }
                }
            }
            if (!(key == null || str2 == null)) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(key);
                stringBuilder2.append("@");
                stringBuilder2.append(str2);
                key = stringBuilder2.toString();
            }
            if (!(r0.currentKey == null || key == null || !r0.currentKey.equals(key))) {
                if (r0.delegate != null) {
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
            String thumbKey = null;
            if (thumbLocation2 != null) {
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(thumbLocation2.volume_id);
                stringBuilder3.append("_");
                stringBuilder3.append(thumbLocation2.local_id);
                thumbKey = stringBuilder3.toString();
                if (str3 != null) {
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(thumbKey);
                    stringBuilder3.append("@");
                    stringBuilder3.append(str3);
                    thumbKey = stringBuilder3.toString();
                }
            }
            if (r0.crossfadeWithOldImage) {
                if (r0.currentImage != null) {
                    recycleBitmap(thumbKey, 1);
                    recycleBitmap(null, 2);
                    r0.crossfadeShader = r0.bitmapShader;
                    r0.crossfadeImage = r0.currentImage;
                    r0.crossfadeKey = r0.currentKey;
                    r0.currentImage = null;
                    r0.currentKey = null;
                } else if (r0.currentThumb != null) {
                    recycleBitmap(key, 0);
                    recycleBitmap(null, 2);
                    r0.crossfadeShader = r0.bitmapShaderThumb;
                    r0.crossfadeImage = r0.currentThumb;
                    r0.crossfadeKey = r0.currentThumbKey;
                    r0.currentThumb = null;
                    r0.currentThumbKey = null;
                } else {
                    z3 = false;
                    recycleBitmap(key, 0);
                    recycleBitmap(thumbKey, 1);
                    recycleBitmap(null, 2);
                    r0.crossfadeShader = null;
                }
                z3 = false;
            } else {
                z3 = false;
                recycleBitmap(key, 0);
                recycleBitmap(thumbKey, 1);
                recycleBitmap(null, 2);
                r0.crossfadeShader = null;
            }
            r0.currentThumbKey = thumbKey;
            r0.currentKey = key;
            r0.currentExt = str4;
            r0.currentImageLocation = fileLocation2;
            r0.currentHttpUrl = str;
            r0.currentFilter = str2;
            r0.currentThumbFilter = str3;
            r0.currentSize = size;
            r0.currentCacheType = cacheType;
            r0.currentThumbLocation = thumbLocation2;
            r0.staticThumb = drawable;
            r0.bitmapShader = null;
            r0.bitmapShaderThumb = null;
            r0.currentAlpha = 1.0f;
            if (r0.delegate != null) {
                imageReceiverDelegate = r0.delegate;
                if (r0.currentImage == null && r0.currentThumb == null) {
                    if (r0.staticThumb == null) {
                        z2 = z3;
                        if (r0.currentImage == null) {
                            z3 = true;
                        }
                        imageReceiverDelegate.didSetImage(r0, z2, z3);
                    }
                }
                z2 = true;
                if (r0.currentImage == null) {
                    z3 = true;
                }
                imageReceiverDelegate.didSetImage(r0, z2, z3);
            }
            ImageLoader.getInstance().loadImageForImageReceiver(r0);
            if (r0.parentView == null) {
            } else if (r0.invalidateAll) {
                r0.parentView.invalidate();
                TLObject tLObject = fileLocation2;
            } else {
                r0.parentView.invalidate(r0.imageX, r0.imageY, r0.imageX + r0.imageW, r0.imageY + r0.imageH);
            }
            return;
        }
        for (int a = 0; a < 3; a++) {
            recycleBitmap(null, a);
        }
        r0.currentKey = null;
        r0.currentExt = str4;
        r0.currentThumbKey = null;
        r0.currentThumbFilter = null;
        r0.currentImageLocation = null;
        r0.currentHttpUrl = null;
        r0.currentFilter = null;
        r0.currentCacheType = 0;
        r0.staticThumb = drawable;
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

    public void setColorFilter(ColorFilter filter) {
        this.colorFilter = filter;
    }

    public void setDelegate(ImageReceiverDelegate delegate) {
        this.delegate = delegate;
    }

    public void setPressed(int value) {
        this.isPressed = value;
    }

    public boolean getPressed() {
        return this.isPressed != 0;
    }

    public void setOrientation(int angle, boolean center) {
        while (angle < 0) {
            angle += 360;
        }
        while (angle > 360) {
            angle -= 360;
        }
        this.orientation = angle;
        this.centerRotation = center;
    }

    public void setInvalidateAll(boolean value) {
        this.invalidateAll = value;
    }

    public Drawable getStaticThumb() {
        return this.staticThumb;
    }

    public int getAnimatedOrientation() {
        if (this.currentImage instanceof AnimatedFileDrawable) {
            return ((AnimatedFileDrawable) this.currentImage).getOrientation();
        }
        if (this.staticThumb instanceof AnimatedFileDrawable) {
            return ((AnimatedFileDrawable) this.staticThumb).getOrientation();
        }
        return 0;
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

    public void setImageBitmap(Drawable bitmap) {
        boolean z = false;
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
        for (int a = 0; a < 3; a++) {
            recycleBitmap(null, a);
        }
        this.staticThumb = bitmap;
        if (this.roundRadius == 0 || !(bitmap instanceof BitmapDrawable)) {
            this.bitmapShaderThumb = null;
        } else {
            this.bitmapShaderThumb = new BitmapShader(((BitmapDrawable) bitmap).getBitmap(), TileMode.CLAMP, TileMode.CLAMP);
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
            ImageReceiverDelegate imageReceiverDelegate = this.delegate;
            if (this.currentThumb == null) {
                if (this.staticThumb == null) {
                    imageReceiverDelegate.didSetImage(this, z, true);
                }
            }
            z = true;
            imageReceiverDelegate.didSetImage(this, z, true);
        }
        if (this.parentView == null) {
            return;
        }
        if (this.invalidateAll) {
            this.parentView.invalidate();
        } else {
            this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
        }
    }

    public void clearImage() {
        for (int a = 0; a < 3; a++) {
            recycleBitmap(null, a);
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

    private void drawDrawable(Canvas canvas, Drawable drawable, int alpha, BitmapShader shader) {
        Throwable bitmapW;
        ImageReceiver imageReceiver = this;
        Canvas canvas2 = canvas;
        Drawable drawable2 = drawable;
        int i = alpha;
        Shader shader2 = shader;
        if (drawable2 instanceof BitmapDrawable) {
            Paint paint;
            int bitmapW2;
            int bitmapH;
            Drawable bitmapDrawable = (BitmapDrawable) drawable2;
            if (shader2 != null) {
                paint = imageReceiver.roundPaint;
            } else {
                paint = bitmapDrawable.getPaint();
            }
            Paint paint2 = paint;
            boolean z = (paint2 == null || paint2.getColorFilter() == null) ? false : true;
            boolean hasFilter = z;
            if (hasFilter && imageReceiver.isPressed == 0) {
                if (shader2 != null) {
                    imageReceiver.roundPaint.setColorFilter(null);
                } else if (imageReceiver.staticThumb != drawable2) {
                    bitmapDrawable.setColorFilter(null);
                }
            } else if (!(hasFilter || imageReceiver.isPressed == 0)) {
                if (imageReceiver.isPressed == 1) {
                    if (shader2 != null) {
                        imageReceiver.roundPaint.setColorFilter(selectedColorFilter);
                    } else {
                        bitmapDrawable.setColorFilter(selectedColorFilter);
                    }
                } else if (shader2 != null) {
                    imageReceiver.roundPaint.setColorFilter(selectedGroupColorFilter);
                } else {
                    bitmapDrawable.setColorFilter(selectedGroupColorFilter);
                }
            }
            if (imageReceiver.colorFilter != null) {
                if (shader2 != null) {
                    imageReceiver.roundPaint.setColorFilter(imageReceiver.colorFilter);
                } else {
                    bitmapDrawable.setColorFilter(imageReceiver.colorFilter);
                }
            }
            if (bitmapDrawable instanceof AnimatedFileDrawable) {
                if (imageReceiver.orientation % 360 != 90) {
                    if (imageReceiver.orientation % 360 != 270) {
                        bitmapW2 = bitmapDrawable.getIntrinsicWidth();
                        bitmapH = bitmapDrawable.getIntrinsicHeight();
                    }
                }
                bitmapW2 = bitmapDrawable.getIntrinsicHeight();
                bitmapH = bitmapDrawable.getIntrinsicWidth();
            } else {
                if (imageReceiver.orientation % 360 != 90) {
                    if (imageReceiver.orientation % 360 != 270) {
                        bitmapW2 = bitmapDrawable.getBitmap().getWidth();
                        bitmapH = bitmapDrawable.getBitmap().getHeight();
                    }
                }
                bitmapW2 = bitmapDrawable.getBitmap().getHeight();
                bitmapH = bitmapDrawable.getBitmap().getWidth();
            }
            int bitmapW3 = bitmapW2;
            int bitmapH2 = bitmapH;
            float scaleW = ((float) bitmapW3) / ((float) imageReceiver.imageW);
            float scaleH = ((float) bitmapH2) / ((float) imageReceiver.imageH);
            int h;
            if (shader2 != null) {
                imageReceiver.roundPaint.setShader(shader2);
                float scale = Math.min(scaleW, scaleH);
                imageReceiver.roundRect.set((float) imageReceiver.imageX, (float) imageReceiver.imageY, (float) (imageReceiver.imageX + imageReceiver.imageW), (float) (imageReceiver.imageY + imageReceiver.imageH));
                imageReceiver.shaderMatrix.reset();
                if (Math.abs(scaleW - scaleH) <= 1.0E-5f) {
                    imageReceiver.drawRegion.set(imageReceiver.imageX, imageReceiver.imageY, imageReceiver.imageX + imageReceiver.imageW, imageReceiver.imageY + imageReceiver.imageH);
                } else if (((float) bitmapW3) / scaleH > ((float) imageReceiver.imageW)) {
                    imageReceiver.drawRegion.set(imageReceiver.imageX - ((((int) (((float) bitmapW3) / scaleH)) - imageReceiver.imageW) / 2), imageReceiver.imageY, imageReceiver.imageX + ((((int) (((float) bitmapW3) / scaleH)) + imageReceiver.imageW) / 2), imageReceiver.imageY + imageReceiver.imageH);
                } else {
                    imageReceiver.drawRegion.set(imageReceiver.imageX, imageReceiver.imageY - ((((int) (((float) bitmapH2) / scaleW)) - imageReceiver.imageH) / 2), imageReceiver.imageX + imageReceiver.imageW, imageReceiver.imageY + ((((int) (((float) bitmapH2) / scaleW)) + imageReceiver.imageH) / 2));
                }
                if (imageReceiver.isVisible) {
                    if (Math.abs(scaleW - scaleH) > 1.0E-5f) {
                        bitmapW2 = (int) Math.floor((double) (((float) imageReceiver.imageW) * scale));
                        h = (int) Math.floor((double) (((float) imageReceiver.imageH) * scale));
                        imageReceiver.bitmapRect.set((float) ((bitmapW3 - bitmapW2) / 2), (float) ((bitmapH2 - h) / 2), (float) ((bitmapW3 + bitmapW2) / 2), (float) ((bitmapH2 + h) / 2));
                        imageReceiver.shaderMatrix.setRectToRect(imageReceiver.bitmapRect, imageReceiver.roundRect, ScaleToFit.START);
                    } else {
                        imageReceiver.bitmapRect.set(0.0f, 0.0f, (float) bitmapW3, (float) bitmapH2);
                        imageReceiver.shaderMatrix.setRectToRect(imageReceiver.bitmapRect, imageReceiver.roundRect, ScaleToFit.FILL);
                    }
                    shader2.setLocalMatrix(imageReceiver.shaderMatrix);
                    imageReceiver.roundPaint.setAlpha(i);
                    canvas.drawRoundRect(imageReceiver.roundRect, (float) imageReceiver.roundRadius, (float) imageReceiver.roundRadius, imageReceiver.roundPaint);
                } else {
                    canvas2 = canvas;
                }
            } else {
                Drawable bitmapDrawable2 = bitmapDrawable;
                float f;
                float f2;
                Drawable drawable3;
                int i2;
                if (imageReceiver.isAspectFit) {
                    float scale2 = Math.max(scaleW, scaleH);
                    canvas.save();
                    int bitmapW4 = (int) (((float) bitmapW3) / scale2);
                    int bitmapH3 = (int) (((float) bitmapH2) / scale2);
                    imageReceiver.drawRegion.set(imageReceiver.imageX + ((imageReceiver.imageW - bitmapW4) / 2), imageReceiver.imageY + ((imageReceiver.imageH - bitmapH3) / 2), imageReceiver.imageX + ((imageReceiver.imageW + bitmapW4) / 2), imageReceiver.imageY + ((imageReceiver.imageH + bitmapH3) / 2));
                    Drawable bitmapDrawable3 = bitmapDrawable2;
                    bitmapDrawable3.setBounds(imageReceiver.drawRegion);
                    try {
                        bitmapDrawable3.setAlpha(i);
                        bitmapDrawable3.draw(canvas2);
                        f = scaleH;
                        f2 = scaleW;
                        drawable3 = bitmapDrawable3;
                        i2 = bitmapH3;
                        int i3 = bitmapW4;
                        float f3 = scale2;
                    } catch (Throwable e) {
                        Throwable e2 = e;
                        if (bitmapDrawable3 == imageReceiver.currentImage && imageReceiver.currentKey != null) {
                            ImageLoader.getInstance().removeImage(imageReceiver.currentKey);
                            imageReceiver.currentKey = null;
                        } else if (bitmapDrawable3 == imageReceiver.currentThumb && imageReceiver.currentThumbKey != null) {
                            ImageLoader.getInstance().removeImage(imageReceiver.currentThumbKey);
                            imageReceiver.currentThumbKey = null;
                        }
                        BitmapDrawable bitmapDrawable4 = scaleH;
                        bitmapW = e2;
                        setImage(imageReceiver.currentImageLocation, imageReceiver.currentHttpUrl, imageReceiver.currentFilter, imageReceiver.currentThumb, imageReceiver.currentThumbLocation, imageReceiver.currentThumbFilter, imageReceiver.currentSize, imageReceiver.currentExt, imageReceiver.currentCacheType);
                        FileLog.m3e(bitmapW);
                    }
                    canvas.restore();
                } else {
                    f2 = scaleW;
                    drawable3 = bitmapDrawable2;
                    f = scaleH;
                    int height;
                    int centerY;
                    if (Math.abs(f2 - f) > 1.0E-5f) {
                        int i4;
                        canvas.save();
                        canvas2.clipRect(imageReceiver.imageX, imageReceiver.imageY, imageReceiver.imageX + imageReceiver.imageW, imageReceiver.imageY + imageReceiver.imageH);
                        if (imageReceiver.orientation % 360 != 0) {
                            if (imageReceiver.centerRotation) {
                                canvas2.rotate((float) imageReceiver.orientation, (float) (imageReceiver.imageW / 2), (float) (imageReceiver.imageH / 2));
                            } else {
                                canvas2.rotate((float) imageReceiver.orientation, 0.0f, 0.0f);
                            }
                        }
                        if (((float) bitmapW3) / f > ((float) imageReceiver.imageW)) {
                            bitmapW3 = (int) (((float) bitmapW3) / f);
                            imageReceiver.drawRegion.set(imageReceiver.imageX - ((bitmapW3 - imageReceiver.imageW) / 2), imageReceiver.imageY, imageReceiver.imageX + ((imageReceiver.imageW + bitmapW3) / 2), imageReceiver.imageY + imageReceiver.imageH);
                        } else {
                            bitmapH2 = (int) (((float) bitmapH2) / f2);
                            imageReceiver.drawRegion.set(imageReceiver.imageX, imageReceiver.imageY - ((bitmapH2 - imageReceiver.imageH) / 2), imageReceiver.imageX + imageReceiver.imageW, imageReceiver.imageY + ((imageReceiver.imageH + bitmapH2) / 2));
                        }
                        int bitmapW5 = bitmapW3;
                        if (drawable3 instanceof AnimatedFileDrawable) {
                            ((AnimatedFileDrawable) drawable3).setActualDrawRect(imageReceiver.imageX, imageReceiver.imageY, imageReceiver.imageW, imageReceiver.imageH);
                        }
                        if (imageReceiver.orientation % 360 != 90) {
                            if (imageReceiver.orientation % 360 != 270) {
                                drawable3.setBounds(imageReceiver.drawRegion);
                                if (imageReceiver.isVisible) {
                                } else {
                                    try {
                                        drawable3.setAlpha(i);
                                        drawable3.draw(canvas2);
                                        i4 = bitmapW5;
                                    } catch (Throwable e3) {
                                        Throwable e4 = e3;
                                        if (drawable3 == imageReceiver.currentImage && imageReceiver.currentKey != null) {
                                            ImageLoader.getInstance().removeImage(imageReceiver.currentKey);
                                            imageReceiver.currentKey = null;
                                        } else if (drawable3 == imageReceiver.currentThumb && imageReceiver.currentThumbKey != null) {
                                            ImageLoader.getInstance().removeImage(imageReceiver.currentThumbKey);
                                            imageReceiver.currentThumbKey = null;
                                        }
                                        bitmapW = e4;
                                        setImage(imageReceiver.currentImageLocation, imageReceiver.currentHttpUrl, imageReceiver.currentFilter, imageReceiver.currentThumb, imageReceiver.currentThumbLocation, imageReceiver.currentThumbFilter, imageReceiver.currentSize, imageReceiver.currentExt, imageReceiver.currentCacheType);
                                        FileLog.m3e(bitmapW);
                                    }
                                }
                                canvas.restore();
                            }
                        }
                        bitmapW2 = (imageReceiver.drawRegion.right - imageReceiver.drawRegion.left) / 2;
                        height = (imageReceiver.drawRegion.bottom - imageReceiver.drawRegion.top) / 2;
                        h = (imageReceiver.drawRegion.right + imageReceiver.drawRegion.left) / 2;
                        centerY = (imageReceiver.drawRegion.top + imageReceiver.drawRegion.bottom) / 2;
                        drawable3.setBounds(h - height, centerY - bitmapW2, h + height, centerY + bitmapW2);
                        if (imageReceiver.isVisible) {
                        } else {
                            drawable3.setAlpha(i);
                            drawable3.draw(canvas2);
                            i4 = bitmapW5;
                        }
                        canvas.restore();
                    } else {
                        int i5;
                        Drawable drawable4;
                        canvas.save();
                        if (imageReceiver.orientation % 360 != 0) {
                            if (imageReceiver.centerRotation) {
                                canvas2.rotate((float) imageReceiver.orientation, (float) (imageReceiver.imageW / 2), (float) (imageReceiver.imageH / 2));
                            } else {
                                canvas2.rotate((float) imageReceiver.orientation, 0.0f, 0.0f);
                            }
                        }
                        imageReceiver.drawRegion.set(imageReceiver.imageX, imageReceiver.imageY, imageReceiver.imageX + imageReceiver.imageW, imageReceiver.imageY + imageReceiver.imageH);
                        if (drawable3 instanceof AnimatedFileDrawable) {
                            ((AnimatedFileDrawable) drawable3).setActualDrawRect(imageReceiver.imageX, imageReceiver.imageY, imageReceiver.imageW, imageReceiver.imageH);
                        }
                        if (imageReceiver.orientation % 360 != 90) {
                            if (imageReceiver.orientation % 360 != 270) {
                                drawable3.setBounds(imageReceiver.drawRegion);
                                if (imageReceiver.isVisible) {
                                    i5 = bitmapW3;
                                    drawable4 = drawable3;
                                } else {
                                    try {
                                        drawable3.setAlpha(i);
                                        drawable3.draw(canvas2);
                                        i2 = bitmapH2;
                                        i5 = bitmapW3;
                                        drawable4 = drawable3;
                                    } catch (Throwable e32) {
                                        bitmapW = e32;
                                        if (drawable3 == imageReceiver.currentImage && imageReceiver.currentKey != null) {
                                            ImageLoader.getInstance().removeImage(imageReceiver.currentKey);
                                            imageReceiver.currentKey = null;
                                        } else if (drawable3 == imageReceiver.currentThumb && imageReceiver.currentThumbKey != null) {
                                            ImageLoader.getInstance().removeImage(imageReceiver.currentThumbKey);
                                            imageReceiver.currentThumbKey = null;
                                        }
                                        i2 = bitmapH2;
                                        setImage(imageReceiver.currentImageLocation, imageReceiver.currentHttpUrl, imageReceiver.currentFilter, imageReceiver.currentThumb, imageReceiver.currentThumbLocation, imageReceiver.currentThumbFilter, imageReceiver.currentSize, imageReceiver.currentExt, imageReceiver.currentCacheType);
                                        FileLog.m3e(bitmapW);
                                    }
                                }
                                canvas.restore();
                            }
                        }
                        bitmapW2 = (imageReceiver.drawRegion.right - imageReceiver.drawRegion.left) / 2;
                        height = (imageReceiver.drawRegion.bottom - imageReceiver.drawRegion.top) / 2;
                        h = (imageReceiver.drawRegion.right + imageReceiver.drawRegion.left) / 2;
                        centerY = (imageReceiver.drawRegion.top + imageReceiver.drawRegion.bottom) / 2;
                        drawable3.setBounds(h - height, centerY - bitmapW2, h + height, centerY + bitmapW2);
                        if (imageReceiver.isVisible) {
                            i5 = bitmapW3;
                            drawable4 = drawable3;
                        } else {
                            drawable3.setAlpha(i);
                            drawable3.draw(canvas2);
                            i2 = bitmapH2;
                            i5 = bitmapW3;
                            drawable4 = drawable3;
                        }
                        canvas.restore();
                    }
                }
            }
            Drawable drawable5 = drawable;
            return;
        }
        imageReceiver.drawRegion.set(imageReceiver.imageX, imageReceiver.imageY, imageReceiver.imageX + imageReceiver.imageW, imageReceiver.imageY + imageReceiver.imageH);
        drawable5 = drawable;
        drawable5.setBounds(imageReceiver.drawRegion);
        if (imageReceiver.isVisible) {
            try {
                drawable.setAlpha(alpha);
                drawable5.draw(canvas2);
            } catch (Throwable e322) {
                FileLog.m3e(e322);
            }
        }
    }

    private void checkAlphaAnimation(boolean skip) {
        if (!(this.manualAlphaAnimator || this.currentAlpha == 1.0f)) {
            if (!skip) {
                long dt = System.currentTimeMillis() - this.lastUpdateAlphaTime;
                if (dt > 18) {
                    dt = 18;
                }
                this.currentAlpha += ((float) dt) / 150.0f;
                if (this.currentAlpha > 1.0f) {
                    this.currentAlpha = 1.0f;
                    if (this.crossfadeImage != null) {
                        recycleBitmap(null, 2);
                        this.crossfadeShader = null;
                    }
                }
            }
            this.lastUpdateAlphaTime = System.currentTimeMillis();
            if (this.parentView != null) {
                if (this.invalidateAll) {
                    this.parentView.invalidate();
                } else {
                    this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                }
            }
        }
    }

    public boolean draw(Canvas canvas) {
        Drawable drawable = null;
        try {
            boolean animationNotReady = (this.currentImage instanceof AnimatedFileDrawable) && !((AnimatedFileDrawable) this.currentImage).hasBitmap();
            boolean isThumb = false;
            BitmapShader customShader = null;
            if (!this.forcePreview && this.currentImage != null && !animationNotReady) {
                drawable = this.currentImage;
            } else if (this.crossfadeImage != null) {
                drawable = this.crossfadeImage;
                customShader = this.crossfadeShader;
            } else if (this.staticThumb instanceof BitmapDrawable) {
                drawable = this.staticThumb;
                isThumb = true;
            } else if (this.currentThumb != null) {
                drawable = this.currentThumb;
                isThumb = true;
            }
            if (drawable != null) {
                int i;
                BitmapShader bitmapShader;
                if (this.crossfadeAlpha == (byte) 0) {
                    i = (int) (this.overrideAlpha * 255.0f);
                    bitmapShader = customShader != null ? customShader : isThumb ? this.bitmapShaderThumb : this.bitmapShader;
                    drawDrawable(canvas, drawable, i, bitmapShader);
                } else if (this.crossfadeWithThumb && animationNotReady) {
                    drawDrawable(canvas, drawable, (int) (this.overrideAlpha * 255.0f), this.bitmapShaderThumb);
                } else {
                    if (this.crossfadeWithThumb && this.currentAlpha != 1.0f) {
                        Drawable thumbDrawable = null;
                        BitmapShader customThumbShader = null;
                        if (drawable == this.currentImage) {
                            if (this.crossfadeImage != null) {
                                thumbDrawable = this.crossfadeImage;
                                customThumbShader = this.crossfadeShader;
                            } else if (this.staticThumb != null) {
                                thumbDrawable = this.staticThumb;
                            } else if (this.currentThumb != null) {
                                thumbDrawable = this.currentThumb;
                            }
                        } else if (drawable == this.currentThumb && this.staticThumb != null) {
                            thumbDrawable = this.staticThumb;
                        }
                        if (thumbDrawable != null) {
                            drawDrawable(canvas, thumbDrawable, (int) (this.overrideAlpha * 255.0f), customThumbShader != null ? customThumbShader : this.bitmapShaderThumb);
                        }
                    }
                    i = (int) ((this.overrideAlpha * this.currentAlpha) * 255.0f);
                    bitmapShader = customShader != null ? customShader : isThumb ? this.bitmapShaderThumb : this.bitmapShader;
                    drawDrawable(canvas, drawable, i, bitmapShader);
                }
                boolean z = animationNotReady && this.crossfadeWithThumb;
                checkAlphaAnimation(z);
                return true;
            } else if (this.staticThumb != null) {
                drawDrawable(canvas, this.staticThumb, 255, null);
                checkAlphaAnimation(animationNotReady);
                return true;
            } else {
                checkAlphaAnimation(animationNotReady);
                return false;
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public void setManualAlphaAnimator(boolean value) {
        this.manualAlphaAnimator = value;
    }

    public float getCurrentAlpha() {
        return this.currentAlpha;
    }

    public void setCurrentAlpha(float value) {
        this.currentAlpha = value;
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
        if (this.staticThumb instanceof BitmapDrawable) {
            return ((BitmapDrawable) this.staticThumb).getBitmap();
        }
        return null;
    }

    public BitmapHolder getBitmapSafe() {
        Bitmap bitmap = null;
        String key = null;
        if (this.currentImage instanceof AnimatedFileDrawable) {
            bitmap = ((AnimatedFileDrawable) this.currentImage).getAnimatedBitmap();
        } else if (this.staticThumb instanceof AnimatedFileDrawable) {
            bitmap = ((AnimatedFileDrawable) this.staticThumb).getAnimatedBitmap();
        } else if (this.currentImage instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) this.currentImage).getBitmap();
            key = this.currentKey;
        } else if (this.currentThumb instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) this.currentThumb).getBitmap();
            key = this.currentThumbKey;
        } else if (this.staticThumb instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) this.staticThumb).getBitmap();
        }
        if (bitmap != null) {
            return new BitmapHolder(bitmap, key);
        }
        return null;
    }

    public Bitmap getThumbBitmap() {
        if (this.currentThumb instanceof BitmapDrawable) {
            return ((BitmapDrawable) this.currentThumb).getBitmap();
        }
        if (this.staticThumb instanceof BitmapDrawable) {
            return ((BitmapDrawable) this.staticThumb).getBitmap();
        }
        return null;
    }

    public BitmapHolder getThumbBitmapSafe() {
        Bitmap bitmap = null;
        String key = null;
        if (this.currentThumb instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) this.currentThumb).getBitmap();
            key = this.currentThumbKey;
        } else if (this.staticThumb instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) this.staticThumb).getBitmap();
        }
        if (bitmap != null) {
            return new BitmapHolder(bitmap, key);
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
            if (bitmap != null) {
                int height;
                if (this.orientation % 360 != 0) {
                    if (this.orientation % 360 != 180) {
                        height = bitmap.getHeight();
                        return height;
                    }
                }
                height = bitmap.getWidth();
                return height;
            } else if (this.staticThumb != null) {
                return this.staticThumb.getIntrinsicWidth();
            } else {
                return 1;
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
            if (bitmap != null) {
                int width;
                if (this.orientation % 360 != 0) {
                    if (this.orientation % 360 != 180) {
                        width = bitmap.getWidth();
                        return width;
                    }
                }
                width = bitmap.getHeight();
                return width;
            } else if (this.staticThumb != null) {
                return this.staticThumb.getIntrinsicHeight();
            } else {
                return 1;
            }
        }
    }

    public void setVisible(boolean value, boolean invalidate) {
        if (this.isVisible != value) {
            this.isVisible = value;
            if (invalidate && this.parentView != null) {
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

    public void setAlpha(float value) {
        this.overrideAlpha = value;
    }

    public void setCrossfadeAlpha(byte value) {
        this.crossfadeAlpha = value;
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

    public void setAspectFit(boolean value) {
        this.isAspectFit = value;
    }

    public void setParentView(View view) {
        this.parentView = view;
        if (this.currentImage instanceof AnimatedFileDrawable) {
            this.currentImage.setParentView(this.parentView);
        }
    }

    public void setImageX(int x) {
        this.imageX = x;
    }

    public void setImageY(int y) {
        this.imageY = y;
    }

    public void setImageWidth(int width) {
        this.imageW = width;
    }

    public void setImageCoords(int x, int y, int width, int height) {
        this.imageX = x;
        this.imageY = y;
        this.imageW = width;
        this.imageH = height;
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

    public boolean isInsideImage(float x, float y) {
        return x >= ((float) this.imageX) && x <= ((float) (this.imageX + this.imageW)) && y >= ((float) this.imageY) && y <= ((float) (this.imageY + this.imageH));
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

    public void setForcePreview(boolean value) {
        this.forcePreview = value;
    }

    public void setForceCrossfade(boolean value) {
        this.forceCrossfade = value;
    }

    public boolean isForcePreview() {
        return this.forcePreview;
    }

    public void setRoundRadius(int value) {
        this.roundRadius = value;
    }

    public void setCurrentAccount(int value) {
        this.currentAccount = value;
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

    public void setNeedsQualityThumb(boolean value) {
        this.needsQualityThumb = value;
        if (this.needsQualityThumb) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.messageThumbGenerated);
        } else {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.messageThumbGenerated);
        }
    }

    public void setCrossfadeWithOldImage(boolean value) {
        this.crossfadeWithOldImage = value;
    }

    public boolean isNeedsQualityThumb() {
        return this.needsQualityThumb;
    }

    public int getcurrentAccount() {
        return this.currentAccount;
    }

    public void setShouldGenerateQualityThumb(boolean value) {
        this.shouldGenerateQualityThumb = value;
    }

    public boolean isShouldGenerateQualityThumb() {
        return this.shouldGenerateQualityThumb;
    }

    public void setAllowStartAnimation(boolean value) {
        this.allowStartAnimation = value;
    }

    public void setAllowDecodeSingleFrame(boolean value) {
        this.allowDecodeSingleFrame = value;
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

    protected int getTag(boolean thumb) {
        if (thumb) {
            return this.thumbTag;
        }
        return this.tag;
    }

    protected void setTag(int value, boolean thumb) {
        if (thumb) {
            this.thumbTag = value;
        } else {
            this.tag = value;
        }
    }

    public void setParam(int value) {
        this.param = value;
    }

    public int getParam() {
        return this.param;
    }

    protected boolean setImageBitmapByKey(BitmapDrawable bitmap, String key, boolean thumb, boolean memCache) {
        boolean z = false;
        if (bitmap != null) {
            if (key != null) {
                boolean z2;
                if (!thumb) {
                    if (this.currentKey != null) {
                        if (key.equals(this.currentKey)) {
                            if (!(bitmap instanceof AnimatedFileDrawable)) {
                                ImageLoader.getInstance().incrementUseCount(this.currentKey);
                            }
                            this.currentImage = bitmap;
                            if (this.roundRadius == 0 || !(bitmap instanceof BitmapDrawable)) {
                                this.bitmapShader = null;
                            } else if (bitmap instanceof AnimatedFileDrawable) {
                                ((AnimatedFileDrawable) bitmap).setRoundRadius(this.roundRadius);
                            } else {
                                this.bitmapShader = new BitmapShader(bitmap.getBitmap(), TileMode.CLAMP, TileMode.CLAMP);
                            }
                            if ((memCache || this.forcePreview) && !this.forceCrossfade) {
                                this.currentAlpha = 1.0f;
                            } else if ((this.currentThumb == null && this.staticThumb == null) || this.currentAlpha == 1.0f || this.forceCrossfade) {
                                this.currentAlpha = 0.0f;
                                this.lastUpdateAlphaTime = System.currentTimeMillis();
                                if (this.currentThumb == null) {
                                    if (this.staticThumb == null) {
                                        z2 = false;
                                        this.crossfadeWithThumb = z2;
                                    }
                                }
                                z2 = true;
                                this.crossfadeWithThumb = z2;
                            }
                            if (bitmap instanceof AnimatedFileDrawable) {
                                AnimatedFileDrawable fileDrawable = (AnimatedFileDrawable) bitmap;
                                fileDrawable.setParentView(this.parentView);
                                if (this.allowStartAnimation) {
                                    fileDrawable.start();
                                } else {
                                    fileDrawable.setAllowDecodeSingleFrame(this.allowDecodeSingleFrame);
                                }
                            }
                            if (this.parentView != null) {
                                if (this.invalidateAll) {
                                    this.parentView.invalidate();
                                } else {
                                    this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                                }
                            }
                        }
                    }
                    return false;
                } else if (this.currentThumb == null && (this.currentImage == null || (((this.currentImage instanceof AnimatedFileDrawable) && !((AnimatedFileDrawable) this.currentImage).hasBitmap()) || this.forcePreview))) {
                    if (this.currentThumbKey != null) {
                        if (key.equals(this.currentThumbKey)) {
                            ImageLoader.getInstance().incrementUseCount(this.currentThumbKey);
                            this.currentThumb = bitmap;
                            if (this.roundRadius == 0 || !(bitmap instanceof BitmapDrawable)) {
                                this.bitmapShaderThumb = null;
                            } else if (bitmap instanceof AnimatedFileDrawable) {
                                ((AnimatedFileDrawable) bitmap).setRoundRadius(this.roundRadius);
                            } else {
                                this.bitmapShaderThumb = new BitmapShader(bitmap.getBitmap(), TileMode.CLAMP, TileMode.CLAMP);
                            }
                            if (memCache || this.crossfadeAlpha == (byte) 2) {
                                this.currentAlpha = 1.0f;
                            } else if (this.parentMessageObject != null && this.parentMessageObject.isRoundVideo() && this.parentMessageObject.isSending()) {
                                this.currentAlpha = 1.0f;
                            } else {
                                this.currentAlpha = 0.0f;
                                this.lastUpdateAlphaTime = System.currentTimeMillis();
                                z2 = this.staticThumb != null && this.currentKey == null;
                                this.crossfadeWithThumb = z2;
                            }
                            if (!((this.staticThumb instanceof BitmapDrawable) || this.parentView == null)) {
                                if (this.invalidateAll) {
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
                    boolean z3;
                    ImageReceiverDelegate imageReceiverDelegate = this.delegate;
                    if (this.currentImage == null && this.currentThumb == null) {
                        if (this.staticThumb == null) {
                            z3 = false;
                            if (this.currentImage == null) {
                                z = true;
                            }
                            imageReceiverDelegate.didSetImage(this, z3, z);
                        }
                    }
                    z3 = true;
                    if (this.currentImage == null) {
                        z = true;
                    }
                    imageReceiverDelegate.didSetImage(this, z3, z);
                }
                return true;
            }
        }
        return false;
    }

    private void recycleBitmap(String newKey, int type) {
        String key;
        Drawable image;
        Bitmap bitmap;
        if (type == 2) {
            key = this.crossfadeKey;
            image = this.crossfadeImage;
        } else if (type == 1) {
            key = this.currentThumbKey;
            image = this.currentThumb;
        } else {
            key = this.currentKey;
            image = this.currentImage;
            if (key != null && ((newKey == null || !newKey.equals(key)) && image != null)) {
                if (image instanceof AnimatedFileDrawable) {
                    ((AnimatedFileDrawable) image).recycle();
                } else if (image instanceof BitmapDrawable) {
                    bitmap = ((BitmapDrawable) image).getBitmap();
                    boolean canDelete = ImageLoader.getInstance().decrementUseCount(key);
                    if (!ImageLoader.getInstance().isInCache(key) && canDelete) {
                        bitmap.recycle();
                    }
                }
            }
            if (type == 2) {
                this.crossfadeKey = null;
                this.crossfadeImage = null;
            } else if (type != 1) {
                this.currentThumb = null;
                this.currentThumbKey = null;
            } else {
                this.currentImage = null;
                this.currentKey = null;
            }
        }
        if (image instanceof AnimatedFileDrawable) {
            ((AnimatedFileDrawable) image).recycle();
        } else if (image instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) image).getBitmap();
            boolean canDelete2 = ImageLoader.getInstance().decrementUseCount(key);
            bitmap.recycle();
        }
        if (type == 2) {
            this.crossfadeKey = null;
            this.crossfadeImage = null;
        } else if (type != 1) {
            this.currentImage = null;
            this.currentKey = null;
        } else {
            this.currentThumb = null;
            this.currentThumbKey = null;
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        String key;
        if (id == NotificationCenter.messageThumbGenerated) {
            key = args[1];
            if (this.currentThumbKey != null && this.currentThumbKey.equals(key)) {
                if (this.currentThumb == null) {
                    ImageLoader.getInstance().incrementUseCount(this.currentThumbKey);
                }
                this.currentThumb = (BitmapDrawable) args[0];
                if (this.roundRadius == 0 || this.currentImage != null || !(this.currentThumb instanceof BitmapDrawable) || (this.currentThumb instanceof AnimatedFileDrawable)) {
                    this.bitmapShaderThumb = null;
                } else {
                    this.bitmapShaderThumb = new BitmapShader(((BitmapDrawable) this.currentThumb).getBitmap(), TileMode.CLAMP, TileMode.CLAMP);
                }
                if (this.staticThumb instanceof BitmapDrawable) {
                    this.staticThumb = null;
                }
                if (this.parentView != null) {
                    if (this.invalidateAll) {
                        this.parentView.invalidate();
                    } else {
                        this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                    }
                }
            }
        } else if (id == NotificationCenter.didReplacedPhotoInMemCache) {
            key = args[0];
            if (this.currentKey != null && this.currentKey.equals(key)) {
                this.currentKey = (String) args[1];
                this.currentImageLocation = (FileLocation) args[2];
            }
            if (this.currentThumbKey != null && this.currentThumbKey.equals(key)) {
                this.currentThumbKey = (String) args[1];
                this.currentThumbLocation = (FileLocation) args[2];
            }
            if (this.setImageBackup != null) {
                if (this.currentKey != null && this.currentKey.equals(key)) {
                    this.currentKey = (String) args[1];
                    this.currentImageLocation = (FileLocation) args[2];
                }
                if (this.currentThumbKey != null && this.currentThumbKey.equals(key)) {
                    this.currentThumbKey = (String) args[1];
                    this.currentThumbLocation = (FileLocation) args[2];
                }
            }
        }
    }
}
