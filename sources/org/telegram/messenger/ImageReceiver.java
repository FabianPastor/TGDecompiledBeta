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
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentEncrypted;
import org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.RecyclableDrawable;

public class ImageReceiver implements NotificationCenterDelegate {
    private static final int TYPE_CROSSFDADE = 2;
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_MEDIA = 3;
    public static final int TYPE_THUMB = 1;
    private static PorterDuffColorFilter selectedColorFilter = new PorterDuffColorFilter(-2236963, Mode.MULTIPLY);
    private static PorterDuffColorFilter selectedGroupColorFilter = new PorterDuffColorFilter(-4473925, Mode.MULTIPLY);
    private boolean allowDecodeSingleFrame;
    private boolean allowStartAnimation;
    private RectF bitmapRect;
    private boolean canceledLoading;
    private boolean centerRotation;
    private ColorFilter colorFilter;
    private byte crossfadeAlpha;
    private Drawable crossfadeImage;
    private String crossfadeKey;
    private BitmapShader crossfadeShader;
    private boolean crossfadeWithOldImage;
    private boolean crossfadeWithThumb;
    private boolean crossfadingWithThumb;
    private int currentAccount;
    private float currentAlpha;
    private int currentCacheType;
    private String currentExt;
    private Drawable currentImageDrawable;
    private String currentImageFilter;
    private String currentImageKey;
    private Object currentImageLocation;
    private boolean currentKeyQuality;
    private Drawable currentMediaDrawable;
    private String currentMediaFilter;
    private String currentMediaKey;
    private Object currentMediaLocation;
    private Object currentParentObject;
    private int currentSize;
    private Drawable currentThumbDrawable;
    private String currentThumbFilter;
    private String currentThumbKey;
    private Object currentThumbLocation;
    private ImageReceiverDelegate delegate;
    private Rect drawRegion;
    private boolean forceCrossfade;
    private boolean forceLoding;
    private boolean forcePreview;
    private int imageH;
    private int imageOrientation;
    private BitmapShader imageShader;
    private int imageTag;
    private int imageW;
    private int imageX;
    private int imageY;
    private boolean invalidateAll;
    private boolean isAspectFit;
    private int isPressed;
    private boolean isVisible;
    private long lastUpdateAlphaTime;
    private boolean manualAlphaAnimator;
    private BitmapShader mediaShader;
    private int mediaTag;
    private boolean needsQualityThumb;
    private float overrideAlpha;
    private int param;
    private View parentView;
    private Document qulityThumbDocument;
    private Paint roundPaint;
    private int roundRadius;
    private RectF roundRect;
    private SetImageBackup setImageBackup;
    private Matrix shaderMatrix;
    private boolean shouldGenerateQualityThumb;
    private Drawable staticThumbDrawable;
    private int thumbOrientation;
    private BitmapShader thumbShader;
    private int thumbTag;

    public static class BitmapHolder {
        public Bitmap bitmap;
        private String key;
        private boolean recycleOnRelease;

        public BitmapHolder(Bitmap b, String k) {
            this.bitmap = b;
            this.key = k;
            if (this.key != null) {
                ImageLoader.getInstance().incrementUseCount(this.key);
            }
        }

        public BitmapHolder(Bitmap b) {
            this.bitmap = b;
            this.recycleOnRelease = true;
        }

        public int getWidth() {
            return this.bitmap != null ? this.bitmap.getWidth() : 0;
        }

        public int getHeight() {
            return this.bitmap != null ? this.bitmap.getHeight() : 0;
        }

        public boolean isRecycled() {
            return this.bitmap == null || this.bitmap.isRecycled();
        }

        public void release() {
            if (this.key == null) {
                if (this.recycleOnRelease && this.bitmap != null) {
                    this.bitmap.recycle();
                }
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
        public Object fileLocation;
        public String filter;
        public String mediaFilter;
        public Object mediaLocation;
        public Object parentObject;
        public int size;
        public Drawable thumb;
        public String thumbFilter;
        public Object thumbLocation;

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
        this.roundPaint = new Paint(3);
        this.currentAccount = UserConfig.selectedAccount;
    }

    public void cancelLoadImage() {
        this.forceLoding = false;
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
        this.canceledLoading = true;
    }

    public void setForceLoading(boolean value) {
        this.forceLoding = value;
    }

    public boolean isForceLoding() {
        return this.forceLoding;
    }

    public void setImage(TLObject path, String filter, Drawable thumb, String ext, Object parentObject, int cacheType) {
        setImage(path, filter, thumb, null, null, 0, ext, parentObject, cacheType);
    }

    public void setImage(TLObject path, String filter, Drawable thumb, int size, String ext, Object parentObject, int cacheType) {
        setImage(path, filter, thumb, null, null, size, ext, parentObject, cacheType);
    }

    public void setImage(String httpUrl, String filter, Drawable thumb, String ext, int size) {
        setImage(httpUrl, filter, thumb, null, null, size, ext, null, 1);
    }

    public void setImage(TLObject fileLocation, String filter, TLObject thumbLocation, String thumbFilter, String ext, Object parentObject, int cacheType) {
        setImage(fileLocation, filter, null, thumbLocation, thumbFilter, 0, ext, parentObject, cacheType);
    }

    public void setImage(Object fileLocation, String filter, TLObject thumbLocation, String thumbFilter, int size, String ext, Object parentObject, int cacheType) {
        setImage(fileLocation, filter, null, thumbLocation, thumbFilter, size, ext, parentObject, cacheType);
    }

    public void setImage(Object fileLocation, String filter, Drawable thumb, Object thumbLocation, String thumbFilter, int size, String ext, Object parentObject, int cacheType) {
        setImage(null, null, fileLocation, filter, thumb, thumbLocation, thumbFilter, size, ext, parentObject, cacheType);
    }

    private String getLocationKey(Object fileLocation, Object parentObject) {
        if (fileLocation instanceof SecureDocument) {
            SecureDocument document = (SecureDocument) fileLocation;
            return document.secureFile.dc_id + "_" + document.secureFile.id;
        } else if (fileLocation instanceof FileLocation) {
            FileLocation location = (FileLocation) fileLocation;
            return location.volume_id + "_" + location.local_id;
        } else {
            if (fileLocation instanceof TL_photoStrippedSize) {
                if (((TL_photoStrippedSize) fileLocation).bytes.length > 0) {
                    return "stripped" + FileRefController.getKeyForParentObject(parentObject);
                }
            } else if ((fileLocation instanceof TL_photoSize) || (fileLocation instanceof TL_photoCachedSize)) {
                PhotoSize photoSize = (PhotoSize) fileLocation;
                return photoSize.location.volume_id + "_" + photoSize.location.local_id;
            } else if (fileLocation instanceof WebFile) {
                return Utilities.MD5(((WebFile) fileLocation).url);
            } else {
                if (fileLocation instanceof Document) {
                    Document location2 = (Document) fileLocation;
                    if (location2.dc_id != 0) {
                        return location2.dc_id + "_" + location2.id;
                    }
                } else if (fileLocation instanceof String) {
                    return Utilities.MD5((String) fileLocation);
                }
            }
            return null;
        }
    }

    private boolean isInvalidLocation(Object fileLocation) {
        return (fileLocation == null || (fileLocation instanceof TL_fileLocation) || (fileLocation instanceof TL_fileEncryptedLocation) || (fileLocation instanceof TL_document) || (fileLocation instanceof WebFile) || (fileLocation instanceof TL_documentEncrypted) || (fileLocation instanceof PhotoSize) || (fileLocation instanceof SecureDocument) || (fileLocation instanceof String)) ? false : true;
    }

    public void setImage(Object mediaLocation, String mediaFilter, Object fileLocation, String imageFilter, Drawable thumb, Object thumbLocation, String thumbFilter, int size, String ext, Object parentObject, int cacheType) {
        if (this.setImageBackup != null) {
            this.setImageBackup.fileLocation = null;
            this.setImageBackup.thumbLocation = null;
            this.setImageBackup.mediaLocation = null;
            this.setImageBackup.thumb = null;
        }
        ImageReceiverDelegate imageReceiverDelegate;
        boolean z;
        boolean z2;
        if ((fileLocation == null && thumbLocation == null && mediaLocation == null) || isInvalidLocation(fileLocation) || isInvalidLocation(mediaLocation)) {
            for (int a = 0; a < 4; a++) {
                recycleBitmap(null, a);
            }
            this.currentImageLocation = null;
            this.currentImageKey = null;
            this.currentImageFilter = null;
            this.currentMediaLocation = null;
            this.currentMediaKey = null;
            this.currentMediaFilter = null;
            this.currentThumbLocation = null;
            this.currentThumbKey = null;
            this.currentThumbFilter = null;
            this.currentMediaDrawable = null;
            this.mediaShader = null;
            this.currentImageDrawable = null;
            this.imageShader = null;
            this.thumbShader = null;
            this.crossfadeShader = null;
            this.currentExt = ext;
            this.currentParentObject = null;
            this.currentCacheType = 0;
            this.staticThumbDrawable = thumb;
            this.currentAlpha = 1.0f;
            this.currentSize = 0;
            ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
            if (this.parentView != null) {
                if (this.invalidateAll) {
                    this.parentView.invalidate();
                } else {
                    this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                }
            }
            if (this.delegate != null) {
                imageReceiverDelegate = this.delegate;
                if (this.currentImageDrawable == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) {
                    z = false;
                } else {
                    z = true;
                }
                if (this.currentImageDrawable == null && this.currentMediaDrawable == null) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                imageReceiverDelegate.didSetImage(this, z, z2);
                return;
            }
            return;
        }
        if (isInvalidLocation(thumbLocation)) {
            thumbLocation = null;
        }
        String imageKey = getLocationKey(fileLocation, parentObject);
        if (imageKey == null && fileLocation != null) {
            fileLocation = null;
        }
        this.currentKeyQuality = false;
        if (imageKey == null && this.needsQualityThumb && ((parentObject instanceof MessageObject) || this.qulityThumbDocument != null)) {
            Document document;
            if (this.qulityThumbDocument != null) {
                document = this.qulityThumbDocument;
            } else {
                document = ((MessageObject) parentObject).getDocument();
            }
            if (!(document == null || document.dc_id == 0 || document.id == 0)) {
                imageKey = "q_" + document.dc_id + "_" + document.id;
                this.currentKeyQuality = true;
            }
        }
        if (!(imageKey == null || imageFilter == null)) {
            imageKey = imageKey + "@" + imageFilter;
        }
        String mediaKey = getLocationKey(mediaLocation, parentObject);
        if (mediaKey == null && mediaLocation != null) {
            mediaLocation = null;
        }
        if (!(mediaKey == null || mediaFilter == null)) {
            mediaKey = mediaKey + "@" + mediaFilter;
        }
        if ((mediaKey == null && this.currentImageKey != null && this.currentImageKey.equals(imageKey)) || (this.currentMediaKey != null && this.currentMediaKey.equals(mediaKey))) {
            if (this.delegate != null) {
                imageReceiverDelegate = this.delegate;
                z = (this.currentImageDrawable == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true;
                if (this.currentImageDrawable == null && this.currentMediaDrawable == null) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                imageReceiverDelegate.didSetImage(this, z, z2);
            }
            if (!(this.canceledLoading || this.forcePreview)) {
                return;
            }
        }
        String thumbKey = getLocationKey(thumbLocation, parentObject);
        if (!(thumbKey == null || thumbFilter == null)) {
            thumbKey = thumbKey + "@" + thumbFilter;
        }
        if (!this.crossfadeWithOldImage) {
            recycleBitmap(imageKey, 0);
            recycleBitmap(thumbKey, 1);
            recycleBitmap(null, 2);
            recycleBitmap(mediaKey, 3);
            this.crossfadeShader = null;
        } else if (this.currentImageDrawable != null) {
            recycleBitmap(thumbKey, 1);
            recycleBitmap(null, 2);
            recycleBitmap(mediaKey, 3);
            this.crossfadeShader = this.imageShader;
            this.crossfadeImage = this.currentImageDrawable;
            this.crossfadeKey = this.currentImageKey;
            this.crossfadingWithThumb = false;
            this.currentImageDrawable = null;
            this.currentImageKey = null;
        } else if (this.currentThumbDrawable != null) {
            recycleBitmap(imageKey, 0);
            recycleBitmap(null, 2);
            recycleBitmap(mediaKey, 3);
            this.crossfadeShader = this.thumbShader;
            this.crossfadeImage = this.currentThumbDrawable;
            this.crossfadeKey = this.currentThumbKey;
            this.crossfadingWithThumb = false;
            this.currentThumbDrawable = null;
            this.currentThumbKey = null;
        } else if (this.staticThumbDrawable != null) {
            recycleBitmap(imageKey, 0);
            recycleBitmap(thumbKey, 1);
            recycleBitmap(null, 2);
            recycleBitmap(mediaKey, 3);
            this.crossfadeShader = this.thumbShader;
            this.crossfadeImage = this.staticThumbDrawable;
            this.crossfadingWithThumb = false;
            this.crossfadeKey = null;
            this.currentThumbDrawable = null;
            this.currentThumbKey = null;
        } else {
            recycleBitmap(imageKey, 0);
            recycleBitmap(thumbKey, 1);
            recycleBitmap(null, 2);
            recycleBitmap(mediaKey, 3);
            this.crossfadeShader = null;
        }
        this.currentImageLocation = fileLocation;
        this.currentImageKey = imageKey;
        this.currentImageFilter = imageFilter;
        this.currentMediaLocation = mediaLocation;
        this.currentMediaKey = mediaKey;
        this.currentMediaFilter = mediaFilter;
        this.currentThumbLocation = thumbLocation;
        this.currentThumbKey = thumbKey;
        this.currentThumbFilter = thumbFilter;
        this.currentParentObject = parentObject;
        this.currentExt = ext;
        this.currentSize = size;
        this.currentCacheType = cacheType;
        this.staticThumbDrawable = thumb;
        this.imageShader = null;
        this.thumbShader = null;
        this.mediaShader = null;
        this.currentAlpha = 1.0f;
        if (this.delegate != null) {
            imageReceiverDelegate = this.delegate;
            z = (this.currentImageDrawable == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true;
            z2 = this.currentImageDrawable == null && this.currentMediaDrawable == null;
            imageReceiverDelegate.didSetImage(this, z, z2);
        }
        ImageLoader.getInstance().loadImageForImageReceiver(this);
        if (this.parentView == null) {
            return;
        }
        if (this.invalidateAll) {
            this.parentView.invalidate();
        } else {
            this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
        }
    }

    public boolean canInvertBitmap() {
        return (this.currentMediaDrawable instanceof ExtendedBitmapDrawable) || (this.currentImageDrawable instanceof ExtendedBitmapDrawable) || (this.currentThumbDrawable instanceof ExtendedBitmapDrawable) || (this.staticThumbDrawable instanceof ExtendedBitmapDrawable);
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
        this.thumbOrientation = angle;
        this.imageOrientation = angle;
        this.centerRotation = center;
    }

    public void setInvalidateAll(boolean value) {
        this.invalidateAll = value;
    }

    public Drawable getStaticThumb() {
        return this.staticThumbDrawable;
    }

    public int getAnimatedOrientation() {
        AnimatedFileDrawable animation = getAnimation();
        return animation != null ? animation.getOrientation() : 0;
    }

    public int getOrientation() {
        return this.imageOrientation;
    }

    public void setImageBitmap(Bitmap bitmap) {
        Drawable bitmapDrawable;
        if (bitmap != null) {
            bitmapDrawable = new BitmapDrawable(null, bitmap);
        } else {
            bitmapDrawable = null;
        }
        setImageBitmap(bitmapDrawable);
    }

    public void setImageBitmap(Drawable bitmap) {
        boolean z = false;
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
        int a;
        if (!this.crossfadeWithOldImage) {
            for (a = 0; a < 4; a++) {
                recycleBitmap(null, a);
            }
        } else if (this.currentImageDrawable != null) {
            recycleBitmap(null, 1);
            recycleBitmap(null, 2);
            recycleBitmap(null, 3);
            this.crossfadeShader = this.imageShader;
            this.crossfadeImage = this.currentImageDrawable;
            this.crossfadeKey = this.currentImageKey;
            this.crossfadingWithThumb = true;
        } else if (this.currentThumbDrawable != null) {
            recycleBitmap(null, 0);
            recycleBitmap(null, 2);
            recycleBitmap(null, 3);
            this.crossfadeShader = this.thumbShader;
            this.crossfadeImage = this.currentThumbDrawable;
            this.crossfadeKey = this.currentThumbKey;
            this.crossfadingWithThumb = true;
        } else if (this.staticThumbDrawable != null) {
            recycleBitmap(null, 0);
            recycleBitmap(null, 1);
            recycleBitmap(null, 2);
            recycleBitmap(null, 3);
            this.crossfadeShader = this.thumbShader;
            this.crossfadeImage = this.staticThumbDrawable;
            this.crossfadingWithThumb = true;
            this.crossfadeKey = null;
        } else {
            for (a = 0; a < 4; a++) {
                recycleBitmap(null, a);
            }
            this.crossfadeShader = null;
        }
        if (this.staticThumbDrawable instanceof RecyclableDrawable) {
            this.staticThumbDrawable.recycle();
        }
        if (bitmap instanceof AnimatedFileDrawable) {
            AnimatedFileDrawable fileDrawable = (AnimatedFileDrawable) bitmap;
            fileDrawable.setParentView(this.parentView);
            if (this.allowStartAnimation) {
                fileDrawable.start();
            }
            fileDrawable.setAllowDecodeSingleFrame(this.allowDecodeSingleFrame);
        }
        this.staticThumbDrawable = bitmap;
        if (this.roundRadius == 0 || !(bitmap instanceof BitmapDrawable)) {
            this.thumbShader = null;
        } else if (bitmap instanceof AnimatedFileDrawable) {
            ((AnimatedFileDrawable) bitmap).setRoundRadius(this.roundRadius);
        } else {
            this.thumbShader = new BitmapShader(((BitmapDrawable) bitmap).getBitmap(), TileMode.CLAMP, TileMode.CLAMP);
        }
        this.currentMediaLocation = null;
        this.currentMediaDrawable = null;
        this.currentMediaKey = null;
        this.currentMediaFilter = null;
        this.mediaShader = null;
        this.currentImageLocation = null;
        this.currentImageDrawable = null;
        this.currentImageKey = null;
        this.currentImageFilter = null;
        this.imageShader = null;
        this.currentThumbLocation = null;
        this.currentThumbKey = null;
        this.currentThumbFilter = null;
        this.currentKeyQuality = false;
        this.currentExt = null;
        this.currentSize = 0;
        this.currentCacheType = 0;
        this.currentAlpha = 1.0f;
        if (this.setImageBackup != null) {
            this.setImageBackup.fileLocation = null;
            this.setImageBackup.thumbLocation = null;
            this.setImageBackup.mediaLocation = null;
            this.setImageBackup.thumb = null;
        }
        if (this.delegate != null) {
            boolean z2;
            ImageReceiverDelegate imageReceiverDelegate = this.delegate;
            if (this.currentThumbDrawable == null && this.staticThumbDrawable == null) {
                z2 = false;
            } else {
                z2 = true;
            }
            imageReceiverDelegate.didSetImage(this, z2, true);
        }
        if (this.parentView != null) {
            if (this.invalidateAll) {
                this.parentView.invalidate();
            } else {
                this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
            }
        }
        if (this.forceCrossfade && this.crossfadeWithOldImage && this.crossfadeImage != null) {
            this.currentAlpha = 0.0f;
            this.lastUpdateAlphaTime = System.currentTimeMillis();
            if (!(this.currentThumbDrawable == null && this.staticThumbDrawable == null)) {
                z = true;
            }
            this.crossfadeWithThumb = z;
        }
    }

    public void clearImage() {
        for (int a = 0; a < 4; a++) {
            recycleBitmap(null, a);
        }
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
    }

    public void onDetachedFromWindow() {
        if (!(this.currentImageLocation == null && this.currentMediaLocation == null && this.currentThumbLocation == null && this.staticThumbDrawable == null)) {
            if (this.setImageBackup == null) {
                this.setImageBackup = new SetImageBackup();
            }
            this.setImageBackup.mediaLocation = this.currentMediaLocation;
            this.setImageBackup.mediaFilter = this.currentMediaFilter;
            this.setImageBackup.fileLocation = this.currentImageLocation;
            this.setImageBackup.filter = this.currentImageFilter;
            this.setImageBackup.thumb = this.staticThumbDrawable;
            this.setImageBackup.thumbLocation = this.currentThumbLocation;
            this.setImageBackup.thumbFilter = this.currentThumbFilter;
            this.setImageBackup.size = this.currentSize;
            this.setImageBackup.ext = this.currentExt;
            this.setImageBackup.cacheType = this.currentCacheType;
            this.setImageBackup.parentObject = this.currentParentObject;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        clearImage();
    }

    public boolean onAttachedToWindow() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        if (this.setImageBackup == null || (this.setImageBackup.fileLocation == null && this.setImageBackup.thumbLocation == null && this.setImageBackup.mediaLocation == null && this.setImageBackup.thumb == null)) {
            return false;
        }
        setImage(this.setImageBackup.mediaLocation, this.setImageBackup.mediaFilter, this.setImageBackup.fileLocation, this.setImageBackup.filter, this.setImageBackup.thumb, this.setImageBackup.thumbLocation, this.setImageBackup.thumbFilter, this.setImageBackup.size, this.setImageBackup.ext, this.setImageBackup.parentObject, this.setImageBackup.cacheType);
        return true;
    }

    private void drawDrawable(Canvas canvas, Drawable drawable, int alpha, BitmapShader shader, int orientation) {
        if (drawable instanceof BitmapDrawable) {
            Paint paint;
            int bitmapW;
            int bitmapH;
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (shader != null) {
                paint = this.roundPaint;
            } else {
                paint = bitmapDrawable.getPaint();
            }
            boolean hasFilter = (paint == null || paint.getColorFilter() == null) ? false : true;
            if (hasFilter && this.isPressed == 0) {
                if (shader != null) {
                    this.roundPaint.setColorFilter(null);
                } else if (this.staticThumbDrawable != drawable) {
                    bitmapDrawable.setColorFilter(null);
                }
            } else if (!(hasFilter || this.isPressed == 0)) {
                if (this.isPressed == 1) {
                    if (shader != null) {
                        this.roundPaint.setColorFilter(selectedColorFilter);
                    } else {
                        bitmapDrawable.setColorFilter(selectedColorFilter);
                    }
                } else if (shader != null) {
                    this.roundPaint.setColorFilter(selectedGroupColorFilter);
                } else {
                    bitmapDrawable.setColorFilter(selectedGroupColorFilter);
                }
            }
            if (this.colorFilter != null) {
                if (shader != null) {
                    this.roundPaint.setColorFilter(this.colorFilter);
                } else {
                    bitmapDrawable.setColorFilter(this.colorFilter);
                }
            }
            if (bitmapDrawable instanceof AnimatedFileDrawable) {
                if (orientation % 360 == 90 || orientation % 360 == 270) {
                    bitmapW = bitmapDrawable.getIntrinsicHeight();
                    bitmapH = bitmapDrawable.getIntrinsicWidth();
                } else {
                    bitmapW = bitmapDrawable.getIntrinsicWidth();
                    bitmapH = bitmapDrawable.getIntrinsicHeight();
                }
            } else if (orientation % 360 == 90 || orientation % 360 == 270) {
                bitmapW = bitmapDrawable.getBitmap().getHeight();
                bitmapH = bitmapDrawable.getBitmap().getWidth();
            } else {
                bitmapW = bitmapDrawable.getBitmap().getWidth();
                bitmapH = bitmapDrawable.getBitmap().getHeight();
            }
            float scaleW = ((float) bitmapW) / ((float) this.imageW);
            float scaleH = ((float) bitmapH) / ((float) this.imageH);
            float scale;
            int width;
            int height;
            int centerX;
            int centerY;
            if (shader != null) {
                this.roundPaint.setShader(shader);
                scale = Math.min(scaleW, scaleH);
                this.roundRect.set((float) this.imageX, (float) this.imageY, (float) (this.imageX + this.imageW), (float) (this.imageY + this.imageH));
                this.shaderMatrix.reset();
                if (Math.abs(scaleW - scaleH) <= 5.0E-4f) {
                    this.drawRegion.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                } else if (((float) bitmapW) / scaleH > ((float) this.imageW)) {
                    this.drawRegion.set(this.imageX - ((((int) (((float) bitmapW) / scaleH)) - this.imageW) / 2), this.imageY, this.imageX + ((((int) (((float) bitmapW) / scaleH)) + this.imageW) / 2), this.imageY + this.imageH);
                } else {
                    this.drawRegion.set(this.imageX, this.imageY - ((((int) (((float) bitmapH) / scaleW)) - this.imageH) / 2), this.imageX + this.imageW, this.imageY + ((((int) (((float) bitmapH) / scaleW)) + this.imageH) / 2));
                }
                if (this.isVisible) {
                    if (Math.abs(scaleW - scaleH) > 5.0E-4f) {
                        int w = (int) Math.ceil((double) (((float) this.imageW) * scale));
                        int h = (int) Math.ceil((double) (((float) this.imageH) * scale));
                        this.bitmapRect.set((float) ((bitmapW - w) / 2), (float) ((bitmapH - h) / 2), (float) ((bitmapW + w) / 2), (float) ((bitmapH + h) / 2));
                        this.shaderMatrix.setRectToRect(this.bitmapRect, this.roundRect, ScaleToFit.START);
                    } else {
                        this.bitmapRect.set(0.0f, 0.0f, (float) bitmapW, (float) bitmapH);
                        this.shaderMatrix.setRectToRect(this.bitmapRect, this.roundRect, ScaleToFit.FILL);
                    }
                    shader.setLocalMatrix(this.shaderMatrix);
                    this.roundPaint.setAlpha(alpha);
                    canvas.drawRoundRect(this.roundRect, (float) this.roundRadius, (float) this.roundRadius, this.roundPaint);
                    return;
                }
                return;
            } else if (this.isAspectFit) {
                scale = Math.max(scaleW, scaleH);
                canvas.save();
                bitmapW = (int) (((float) bitmapW) / scale);
                bitmapH = (int) (((float) bitmapH) / scale);
                this.drawRegion.set(this.imageX + ((this.imageW - bitmapW) / 2), this.imageY + ((this.imageH - bitmapH) / 2), this.imageX + ((this.imageW + bitmapW) / 2), this.imageY + ((this.imageH + bitmapH) / 2));
                bitmapDrawable.setBounds(this.drawRegion);
                if (this.isVisible) {
                    try {
                        bitmapDrawable.setAlpha(alpha);
                        bitmapDrawable.draw(canvas);
                    } catch (Throwable e) {
                        onBitmapException(bitmapDrawable);
                        FileLog.e(e);
                    }
                }
                canvas.restore();
                return;
            } else if (Math.abs(scaleW - scaleH) > 1.0E-5f) {
                canvas.save();
                canvas.clipRect(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                if (orientation % 360 != 0) {
                    if (this.centerRotation) {
                        canvas.rotate((float) orientation, (float) (this.imageW / 2), (float) (this.imageH / 2));
                    } else {
                        canvas.rotate((float) orientation, 0.0f, 0.0f);
                    }
                }
                if (((float) bitmapW) / scaleH > ((float) this.imageW)) {
                    bitmapW = (int) (((float) bitmapW) / scaleH);
                    this.drawRegion.set(this.imageX - ((bitmapW - this.imageW) / 2), this.imageY, this.imageX + ((this.imageW + bitmapW) / 2), this.imageY + this.imageH);
                } else {
                    bitmapH = (int) (((float) bitmapH) / scaleW);
                    this.drawRegion.set(this.imageX, this.imageY - ((bitmapH - this.imageH) / 2), this.imageX + this.imageW, this.imageY + ((this.imageH + bitmapH) / 2));
                }
                if (bitmapDrawable instanceof AnimatedFileDrawable) {
                    ((AnimatedFileDrawable) bitmapDrawable).setActualDrawRect(this.imageX, this.imageY, this.imageW, this.imageH);
                }
                if (orientation % 360 == 90 || orientation % 360 == 270) {
                    width = (this.drawRegion.right - this.drawRegion.left) / 2;
                    height = (this.drawRegion.bottom - this.drawRegion.top) / 2;
                    centerX = (this.drawRegion.right + this.drawRegion.left) / 2;
                    centerY = (this.drawRegion.top + this.drawRegion.bottom) / 2;
                    bitmapDrawable.setBounds(centerX - height, centerY - width, centerX + height, centerY + width);
                } else {
                    bitmapDrawable.setBounds(this.drawRegion);
                }
                if (this.isVisible) {
                    try {
                        bitmapDrawable.setAlpha(alpha);
                        bitmapDrawable.draw(canvas);
                    } catch (Throwable e2) {
                        onBitmapException(bitmapDrawable);
                        FileLog.e(e2);
                    }
                }
                canvas.restore();
                return;
            } else {
                canvas.save();
                if (orientation % 360 != 0) {
                    if (this.centerRotation) {
                        canvas.rotate((float) orientation, (float) (this.imageW / 2), (float) (this.imageH / 2));
                    } else {
                        canvas.rotate((float) orientation, 0.0f, 0.0f);
                    }
                }
                this.drawRegion.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
                if (bitmapDrawable instanceof AnimatedFileDrawable) {
                    ((AnimatedFileDrawable) bitmapDrawable).setActualDrawRect(this.imageX, this.imageY, this.imageW, this.imageH);
                }
                if (orientation % 360 == 90 || orientation % 360 == 270) {
                    width = (this.drawRegion.right - this.drawRegion.left) / 2;
                    height = (this.drawRegion.bottom - this.drawRegion.top) / 2;
                    centerX = (this.drawRegion.right + this.drawRegion.left) / 2;
                    centerY = (this.drawRegion.top + this.drawRegion.bottom) / 2;
                    bitmapDrawable.setBounds(centerX - height, centerY - width, centerX + height, centerY + width);
                } else {
                    bitmapDrawable.setBounds(this.drawRegion);
                }
                if (this.isVisible) {
                    try {
                        bitmapDrawable.setAlpha(alpha);
                        bitmapDrawable.draw(canvas);
                    } catch (Throwable e22) {
                        onBitmapException(bitmapDrawable);
                        FileLog.e(e22);
                    }
                }
                canvas.restore();
                return;
            }
        }
        this.drawRegion.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
        drawable.setBounds(this.drawRegion);
        if (this.isVisible) {
            try {
                drawable.setAlpha(alpha);
                drawable.draw(canvas);
            } catch (Throwable e222) {
                FileLog.e(e222);
            }
        }
    }

    private void onBitmapException(Drawable bitmapDrawable) {
        if (bitmapDrawable == this.currentMediaDrawable && this.currentMediaKey != null) {
            ImageLoader.getInstance().removeImage(this.currentMediaKey);
            this.currentMediaKey = null;
        } else if (bitmapDrawable == this.currentImageDrawable && this.currentImageKey != null) {
            ImageLoader.getInstance().removeImage(this.currentImageKey);
            this.currentImageKey = null;
        } else if (bitmapDrawable == this.currentThumbDrawable && this.currentThumbKey != null) {
            ImageLoader.getInstance().removeImage(this.currentThumbKey);
            this.currentThumbKey = null;
        }
        setImage(this.currentMediaLocation, this.currentMediaFilter, this.currentImageLocation, this.currentImageFilter, this.currentThumbDrawable, this.currentThumbLocation, this.currentThumbFilter, this.currentSize, this.currentExt, this.currentParentObject, this.currentCacheType);
    }

    private void checkAlphaAnimation(boolean skip) {
        if (!this.manualAlphaAnimator && this.currentAlpha != 1.0f) {
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
            if (this.parentView == null) {
                return;
            }
            if (this.invalidateAll) {
                this.parentView.invalidate();
            } else {
                this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
            }
        }
    }

    public boolean draw(Canvas canvas) {
        Drawable drawable = null;
        try {
            AnimatedFileDrawable animation = getAnimation();
            boolean animationNotReady = (animation == null || animation.hasBitmap()) ? false : true;
            int orientation = 0;
            BitmapShader shaderToUse = null;
            if (!this.forcePreview && this.currentMediaDrawable != null && !animationNotReady) {
                drawable = this.currentMediaDrawable;
                shaderToUse = this.mediaShader;
                orientation = this.imageOrientation;
            } else if (!this.forcePreview && this.currentImageDrawable != null && (!animationNotReady || this.currentMediaDrawable != null)) {
                drawable = this.currentImageDrawable;
                shaderToUse = this.imageShader;
                orientation = this.imageOrientation;
                animationNotReady = false;
            } else if (this.crossfadeImage != null && !this.crossfadingWithThumb) {
                drawable = this.crossfadeImage;
                shaderToUse = this.crossfadeShader;
                orientation = this.imageOrientation;
            } else if (this.staticThumbDrawable instanceof BitmapDrawable) {
                drawable = this.staticThumbDrawable;
                shaderToUse = this.thumbShader;
                orientation = this.thumbOrientation;
            } else if (this.currentThumbDrawable != null) {
                drawable = this.currentThumbDrawable;
                shaderToUse = this.thumbShader;
                orientation = this.thumbOrientation;
            }
            if (drawable != null) {
                if (this.crossfadeAlpha == (byte) 0) {
                    drawDrawable(canvas, drawable, (int) (this.overrideAlpha * 255.0f), shaderToUse, orientation);
                } else if (this.crossfadeWithThumb && animationNotReady) {
                    drawDrawable(canvas, drawable, (int) (this.overrideAlpha * 255.0f), shaderToUse, orientation);
                } else {
                    if (this.crossfadeWithThumb && this.currentAlpha != 1.0f) {
                        Drawable thumbDrawable = null;
                        BitmapShader thumbShaderToUse = null;
                        if (drawable == this.currentImageDrawable || drawable == this.currentMediaDrawable) {
                            if (this.crossfadeImage != null) {
                                thumbDrawable = this.crossfadeImage;
                                thumbShaderToUse = this.crossfadeShader;
                            } else if (this.staticThumbDrawable != null) {
                                thumbDrawable = this.staticThumbDrawable;
                                thumbShaderToUse = this.thumbShader;
                            } else if (this.currentThumbDrawable != null) {
                                thumbDrawable = this.currentThumbDrawable;
                                thumbShaderToUse = this.thumbShader;
                            }
                        } else if (drawable == this.currentThumbDrawable || drawable == this.crossfadeImage) {
                            if (this.staticThumbDrawable != null) {
                                thumbDrawable = this.staticThumbDrawable;
                                thumbShaderToUse = this.thumbShader;
                            }
                        } else if (drawable == this.staticThumbDrawable && this.crossfadeImage != null) {
                            thumbDrawable = this.crossfadeImage;
                            thumbShaderToUse = this.crossfadeShader;
                        }
                        if (thumbDrawable != null) {
                            drawDrawable(canvas, thumbDrawable, (int) (this.overrideAlpha * 255.0f), thumbShaderToUse, this.thumbOrientation);
                        }
                    }
                    drawDrawable(canvas, drawable, (int) ((this.overrideAlpha * this.currentAlpha) * 255.0f), shaderToUse, orientation);
                }
                boolean z = animationNotReady && this.crossfadeWithThumb;
                checkAlphaAnimation(z);
                return true;
            } else if (this.staticThumbDrawable != null) {
                drawDrawable(canvas, this.staticThumbDrawable, (int) (this.overrideAlpha * 255.0f), null, this.thumbOrientation);
                checkAlphaAnimation(animationNotReady);
                return true;
            } else {
                checkAlphaAnimation(animationNotReady);
                return false;
            }
        } catch (Throwable e) {
            FileLog.e(e);
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

    public Drawable getDrawable() {
        if (this.currentMediaDrawable != null) {
            return this.currentMediaDrawable;
        }
        if (this.currentImageDrawable != null) {
            return this.currentImageDrawable;
        }
        if (this.currentThumbDrawable != null) {
            return this.currentThumbDrawable;
        }
        if (this.staticThumbDrawable != null) {
            return this.staticThumbDrawable;
        }
        return null;
    }

    public Bitmap getBitmap() {
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null && animation.hasBitmap()) {
            return animation.getAnimatedBitmap();
        }
        if ((this.currentMediaDrawable instanceof BitmapDrawable) && !(this.currentMediaDrawable instanceof AnimatedFileDrawable)) {
            return ((BitmapDrawable) this.currentMediaDrawable).getBitmap();
        }
        if ((this.currentImageDrawable instanceof BitmapDrawable) && !(this.currentImageDrawable instanceof AnimatedFileDrawable)) {
            return ((BitmapDrawable) this.currentImageDrawable).getBitmap();
        }
        if ((this.currentThumbDrawable instanceof BitmapDrawable) && !(this.currentThumbDrawable instanceof AnimatedFileDrawable)) {
            return ((BitmapDrawable) this.currentThumbDrawable).getBitmap();
        }
        if (this.staticThumbDrawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) this.staticThumbDrawable).getBitmap();
        }
        return null;
    }

    public BitmapHolder getBitmapSafe() {
        Bitmap bitmap = null;
        String key = null;
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null && animation.hasBitmap()) {
            bitmap = animation.getAnimatedBitmap();
        } else if ((this.currentMediaDrawable instanceof BitmapDrawable) && !(this.currentMediaDrawable instanceof AnimatedFileDrawable)) {
            bitmap = ((BitmapDrawable) this.currentMediaDrawable).getBitmap();
            key = this.currentMediaKey;
        } else if ((this.currentImageDrawable instanceof BitmapDrawable) && !(this.currentImageDrawable instanceof AnimatedFileDrawable)) {
            bitmap = ((BitmapDrawable) this.currentImageDrawable).getBitmap();
            key = this.currentImageKey;
        } else if ((this.currentThumbDrawable instanceof BitmapDrawable) && !(this.currentThumbDrawable instanceof AnimatedFileDrawable)) {
            bitmap = ((BitmapDrawable) this.currentThumbDrawable).getBitmap();
            key = this.currentThumbKey;
        } else if (this.staticThumbDrawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) this.staticThumbDrawable).getBitmap();
        }
        if (bitmap != null) {
            return new BitmapHolder(bitmap, key);
        }
        return null;
    }

    public Bitmap getThumbBitmap() {
        if (this.currentThumbDrawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) this.currentThumbDrawable).getBitmap();
        }
        if (this.staticThumbDrawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) this.staticThumbDrawable).getBitmap();
        }
        return null;
    }

    public BitmapHolder getThumbBitmapSafe() {
        Bitmap bitmap = null;
        String key = null;
        if (this.currentThumbDrawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) this.currentThumbDrawable).getBitmap();
            key = this.currentThumbKey;
        } else if (this.staticThumbDrawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) this.staticThumbDrawable).getBitmap();
        }
        if (bitmap != null) {
            return new BitmapHolder(bitmap, key);
        }
        return null;
    }

    public int getBitmapWidth() {
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            return (this.imageOrientation % 360 == 0 || this.imageOrientation % 360 == 180) ? animation.getIntrinsicWidth() : animation.getIntrinsicHeight();
        } else {
            Bitmap bitmap = getBitmap();
            if (bitmap != null) {
                return (this.imageOrientation % 360 == 0 || this.imageOrientation % 360 == 180) ? bitmap.getWidth() : bitmap.getHeight();
            } else {
                if (this.staticThumbDrawable != null) {
                    return this.staticThumbDrawable.getIntrinsicWidth();
                }
                return 1;
            }
        }
    }

    public int getBitmapHeight() {
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            return (this.imageOrientation % 360 == 0 || this.imageOrientation % 360 == 180) ? animation.getIntrinsicHeight() : animation.getIntrinsicWidth();
        } else {
            Bitmap bitmap = getBitmap();
            if (bitmap != null) {
                return (this.imageOrientation % 360 == 0 || this.imageOrientation % 360 == 180) ? bitmap.getHeight() : bitmap.getWidth();
            } else {
                if (this.staticThumbDrawable != null) {
                    return this.staticThumbDrawable.getIntrinsicHeight();
                }
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

    public boolean hasImageSet() {
        return (this.currentImageDrawable == null && this.currentMediaDrawable == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentImageKey == null && this.currentMediaKey == null) ? false : true;
    }

    public boolean hasBitmapImage() {
        return (this.currentImageDrawable == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true;
    }

    public boolean hasNotThumb() {
        return (this.currentImageDrawable == null && this.currentMediaDrawable == null) ? false : true;
    }

    public boolean hasStaticThumb() {
        return this.staticThumbDrawable != null;
    }

    public void setAspectFit(boolean value) {
        this.isAspectFit = value;
    }

    public boolean isAspectFit() {
        return this.isAspectFit;
    }

    public void setParentView(View view) {
        this.parentView = view;
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            animation.setParentView(this.parentView);
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

    public float getImageAspectRatio() {
        return this.imageOrientation % 180 != 0 ? ((float) this.drawRegion.height()) / ((float) this.drawRegion.width()) : ((float) this.drawRegion.width()) / ((float) this.drawRegion.height());
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

    public String getImageKey() {
        return this.currentImageKey;
    }

    public String getImageFilter() {
        return this.currentImageFilter;
    }

    public String getMediaKey() {
        return this.currentMediaKey;
    }

    public String getMediaFilter() {
        return this.currentMediaFilter;
    }

    public String getThumbKey() {
        return this.currentThumbKey;
    }

    public String getThumbFilter() {
        return this.currentThumbFilter;
    }

    public int getSize() {
        return this.currentSize;
    }

    public Object getMediaLocation() {
        return this.currentMediaLocation;
    }

    public Object getImageLocation() {
        return this.currentImageLocation;
    }

    public Object getThumbLocation() {
        return this.currentThumbLocation;
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

    public Object getParentObject() {
        return this.currentParentObject;
    }

    public void setNeedsQualityThumb(boolean value) {
        this.needsQualityThumb = value;
    }

    public void setQualityThumbDocument(Document document) {
        this.qulityThumbDocument = document;
    }

    public Document getQulityThumbDocument() {
        return this.qulityThumbDocument;
    }

    public void setCrossfadeWithOldImage(boolean value) {
        this.crossfadeWithOldImage = value;
    }

    public boolean isNeedsQualityThumb() {
        return this.needsQualityThumb;
    }

    public boolean isCurrentKeyQuality() {
        return this.currentKeyQuality;
    }

    public int getCurrentAccount() {
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
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            animation.start();
        }
    }

    public void stopAnimation() {
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            animation.stop();
        }
    }

    public boolean isAnimationRunning() {
        AnimatedFileDrawable animation = getAnimation();
        return animation != null && animation.isRunning();
    }

    public AnimatedFileDrawable getAnimation() {
        if (this.currentMediaDrawable instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) this.currentMediaDrawable;
        }
        if (this.currentImageDrawable instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) this.currentImageDrawable;
        }
        if (this.currentThumbDrawable instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) this.currentThumbDrawable;
        }
        if (this.staticThumbDrawable instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) this.staticThumbDrawable;
        }
        return null;
    }

    protected int getTag(int type) {
        if (type == 1) {
            return this.thumbTag;
        }
        if (type == 3) {
            return this.mediaTag;
        }
        return this.imageTag;
    }

    protected void setTag(int value, int type) {
        if (type == 1) {
            this.thumbTag = value;
        } else if (type == 3) {
            this.mediaTag = value;
        } else {
            this.imageTag = value;
        }
    }

    public void setParam(int value) {
        this.param = value;
    }

    public int getParam() {
        return this.param;
    }

    protected boolean setImageBitmapByKey(BitmapDrawable bitmap, String key, int type, boolean memCache) {
        if (bitmap == null || key == null) {
            return false;
        }
        boolean z;
        if (type == 0) {
            if (!key.equals(this.currentImageKey)) {
                return false;
            }
            if (!(bitmap instanceof AnimatedFileDrawable)) {
                ImageLoader.getInstance().incrementUseCount(this.currentImageKey);
            }
            this.currentImageDrawable = bitmap;
            if (bitmap instanceof ExtendedBitmapDrawable) {
                this.imageOrientation = ((ExtendedBitmapDrawable) bitmap).getOrientation();
            }
            if (this.roundRadius == 0 || !(bitmap instanceof BitmapDrawable)) {
                this.imageShader = null;
            } else if (bitmap instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) bitmap).setRoundRadius(this.roundRadius);
            } else {
                this.imageShader = new BitmapShader(bitmap.getBitmap(), TileMode.CLAMP, TileMode.CLAMP);
            }
            if ((memCache || this.forcePreview) && !this.forceCrossfade) {
                this.currentAlpha = 1.0f;
            } else {
                boolean allowCorssfade = true;
                if ((this.currentMediaDrawable instanceof AnimatedFileDrawable) && ((AnimatedFileDrawable) this.currentMediaDrawable).hasBitmap()) {
                    allowCorssfade = false;
                }
                if (allowCorssfade && ((this.currentThumbDrawable == null && this.staticThumbDrawable == null) || this.currentAlpha == 1.0f || this.forceCrossfade)) {
                    this.currentAlpha = 0.0f;
                    this.lastUpdateAlphaTime = System.currentTimeMillis();
                    if (this.crossfadeImage == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null) {
                        z = false;
                    } else {
                        z = true;
                    }
                    this.crossfadeWithThumb = z;
                }
            }
        } else if (type == 3) {
            if (!key.equals(this.currentMediaKey)) {
                return false;
            }
            if (!(bitmap instanceof AnimatedFileDrawable)) {
                ImageLoader.getInstance().incrementUseCount(this.currentMediaKey);
            }
            this.currentMediaDrawable = bitmap;
            if (this.roundRadius == 0 || !(bitmap instanceof BitmapDrawable)) {
                this.mediaShader = null;
            } else if (bitmap instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) bitmap).setRoundRadius(this.roundRadius);
            } else {
                this.mediaShader = new BitmapShader(bitmap.getBitmap(), TileMode.CLAMP, TileMode.CLAMP);
            }
            if (this.currentImageDrawable == null) {
                if ((memCache || this.forcePreview) && !this.forceCrossfade) {
                    this.currentAlpha = 1.0f;
                } else if ((this.currentThumbDrawable == null && this.staticThumbDrawable == null) || this.currentAlpha == 1.0f || this.forceCrossfade) {
                    this.currentAlpha = 0.0f;
                    this.lastUpdateAlphaTime = System.currentTimeMillis();
                    if (this.crossfadeImage == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null) {
                        z = false;
                    } else {
                        z = true;
                    }
                    this.crossfadeWithThumb = z;
                }
            }
        } else if (type == 1) {
            if (this.currentThumbDrawable != null) {
                return false;
            }
            if (!this.forcePreview) {
                AnimatedFileDrawable animation = getAnimation();
                if (animation != null && animation.hasBitmap()) {
                    return false;
                }
                if (!((this.currentImageDrawable == null || (this.currentImageDrawable instanceof AnimatedFileDrawable)) && (this.currentMediaDrawable == null || (this.currentMediaDrawable instanceof AnimatedFileDrawable)))) {
                    return false;
                }
            }
            if (!key.equals(this.currentThumbKey)) {
                return false;
            }
            ImageLoader.getInstance().incrementUseCount(this.currentThumbKey);
            this.currentThumbDrawable = bitmap;
            if (bitmap instanceof ExtendedBitmapDrawable) {
                this.thumbOrientation = ((ExtendedBitmapDrawable) bitmap).getOrientation();
            }
            if (this.roundRadius == 0 || !(bitmap instanceof BitmapDrawable)) {
                this.thumbShader = null;
            } else if (bitmap instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) bitmap).setRoundRadius(this.roundRadius);
            } else {
                this.thumbShader = new BitmapShader(bitmap.getBitmap(), TileMode.CLAMP, TileMode.CLAMP);
            }
            if (memCache || this.crossfadeAlpha == (byte) 2) {
                this.currentAlpha = 1.0f;
            } else if ((this.currentParentObject instanceof MessageObject) && ((MessageObject) this.currentParentObject).isRoundVideo() && ((MessageObject) this.currentParentObject).isSending()) {
                this.currentAlpha = 1.0f;
            } else {
                this.currentAlpha = 0.0f;
                this.lastUpdateAlphaTime = System.currentTimeMillis();
                z = this.staticThumbDrawable != null && this.currentImageKey == null && this.currentMediaKey == null;
                this.crossfadeWithThumb = z;
            }
        }
        if (bitmap instanceof AnimatedFileDrawable) {
            AnimatedFileDrawable fileDrawable = (AnimatedFileDrawable) bitmap;
            fileDrawable.setParentView(this.parentView);
            if (this.allowStartAnimation) {
                fileDrawable.start();
            }
            fileDrawable.setAllowDecodeSingleFrame(this.allowDecodeSingleFrame);
        }
        if (this.parentView != null) {
            if (this.invalidateAll) {
                this.parentView.invalidate();
            } else {
                this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
            }
        }
        if (this.delegate != null) {
            ImageReceiverDelegate imageReceiverDelegate = this.delegate;
            z = (this.currentImageDrawable == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true;
            boolean z2 = this.currentImageDrawable == null && this.currentMediaDrawable == null;
            imageReceiverDelegate.didSetImage(this, z, z2);
        }
        return true;
    }

    private void recycleBitmap(String newKey, int type) {
        String key;
        Drawable image;
        String replacedKey;
        if (type == 3) {
            key = this.currentMediaKey;
            image = this.currentMediaDrawable;
        } else if (type == 2) {
            key = this.crossfadeKey;
            image = this.crossfadeImage;
        } else if (type == 1) {
            key = this.currentThumbKey;
            image = this.currentThumbDrawable;
        } else {
            key = this.currentImageKey;
            image = this.currentImageDrawable;
        }
        if (key != null && key.startsWith("-")) {
            replacedKey = ImageLoader.getInstance().getReplacedKey(key);
            if (replacedKey != null) {
                key = replacedKey;
            }
        }
        replacedKey = ImageLoader.getInstance().getReplacedKey(key);
        if (key != null && ((newKey == null || !newKey.equals(key)) && image != null)) {
            if (image instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) image).recycle();
            } else if (image instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
                boolean canDelete = ImageLoader.getInstance().decrementUseCount(key);
                if (!ImageLoader.getInstance().isInCache(key) && canDelete) {
                    bitmap.recycle();
                }
            }
        }
        if (type == 3) {
            this.currentMediaKey = null;
            this.currentMediaDrawable = null;
        } else if (type == 2) {
            this.crossfadeKey = null;
            this.crossfadeImage = null;
        } else if (type == 1) {
            this.currentThumbDrawable = null;
            this.currentThumbKey = null;
        } else {
            this.currentImageDrawable = null;
            this.currentImageKey = null;
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didReplacedPhotoInMemCache) {
            String oldKey = args[0];
            if (this.currentMediaKey != null && this.currentMediaKey.equals(oldKey)) {
                this.currentMediaKey = (String) args[1];
                this.currentMediaLocation = args[2];
                if (this.setImageBackup != null) {
                    this.setImageBackup.mediaLocation = args[2];
                }
            }
            if (this.currentImageKey != null && this.currentImageKey.equals(oldKey)) {
                this.currentImageKey = (String) args[1];
                this.currentImageLocation = args[2];
                if (this.setImageBackup != null) {
                    this.setImageBackup.fileLocation = args[2];
                }
            }
            if (this.currentThumbKey != null && this.currentThumbKey.equals(oldKey)) {
                this.currentThumbKey = (String) args[1];
                this.currentThumbLocation = args[2];
                if (this.setImageBackup != null) {
                    this.setImageBackup.thumbLocation = args[2];
                }
            }
        }
    }
}
