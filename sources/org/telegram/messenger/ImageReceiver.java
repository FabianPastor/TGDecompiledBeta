package org.telegram.messenger;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclableDrawable;

public class ImageReceiver implements NotificationCenter.NotificationCenterDelegate {
    private static final int TYPE_CROSSFDADE = 2;
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_MEDIA = 3;
    public static final int TYPE_THUMB = 1;
    private static PorterDuffColorFilter selectedColorFilter = new PorterDuffColorFilter(-2236963, PorterDuff.Mode.MULTIPLY);
    private static PorterDuffColorFilter selectedGroupColorFilter = new PorterDuffColorFilter(-4473925, PorterDuff.Mode.MULTIPLY);
    private boolean allowDecodeSingleFrame;
    private boolean allowStartAnimation;
    private boolean animationReadySent;
    private int autoRepeat;
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
    private int currentGuid;
    private Drawable currentImageDrawable;
    private String currentImageFilter;
    private String currentImageKey;
    private ImageLocation currentImageLocation;
    private boolean currentKeyQuality;
    private int currentLayerNum;
    private Drawable currentMediaDrawable;
    private String currentMediaFilter;
    private String currentMediaKey;
    private ImageLocation currentMediaLocation;
    private int currentOpenedLayerFlags;
    private Object currentParentObject;
    private int currentSize;
    private Drawable currentThumbDrawable;
    private String currentThumbFilter;
    private String currentThumbKey;
    private ImageLocation currentThumbLocation;
    private ImageReceiverDelegate delegate;
    private RectF drawRegion;
    private boolean forceCrossfade;
    private boolean forceLoding;
    private boolean forcePreview;
    private boolean ignoreImageSet;
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
    private boolean isRoundVideo;
    private boolean isVisible;
    private long lastUpdateAlphaTime;
    private boolean manualAlphaAnimator;
    private BitmapShader mediaShader;
    private int mediaTag;
    private boolean needsQualityThumb;
    private float overrideAlpha;
    private int param;
    private View parentView;
    private TLRPC.Document qulityThumbDocument;
    private Paint roundPaint;
    private int roundRadius;
    private RectF roundRect;
    private SetImageBackup setImageBackup;
    private Matrix shaderMatrix;
    private boolean shouldGenerateQualityThumb;
    private float sideClip;
    private Drawable staticThumbDrawable;
    private ImageLocation strippedLocation;
    private int thumbOrientation;
    private BitmapShader thumbShader;
    private int thumbTag;
    private boolean useSharedAnimationQueue;

    public interface ImageReceiverDelegate {

        /* renamed from: org.telegram.messenger.ImageReceiver$ImageReceiverDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onAnimationReady(ImageReceiverDelegate imageReceiverDelegate, ImageReceiver imageReceiver) {
            }
        }

        void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2);

        void onAnimationReady(ImageReceiver imageReceiver);
    }

    public static class BitmapHolder {
        public Bitmap bitmap;
        private String key;
        private boolean recycleOnRelease;

        public BitmapHolder(Bitmap bitmap2, String str) {
            this.bitmap = bitmap2;
            this.key = str;
            if (this.key != null) {
                ImageLoader.getInstance().incrementUseCount(this.key);
            }
        }

        public BitmapHolder(Bitmap bitmap2) {
            this.bitmap = bitmap2;
            this.recycleOnRelease = true;
        }

        public int getWidth() {
            Bitmap bitmap2 = this.bitmap;
            if (bitmap2 != null) {
                return bitmap2.getWidth();
            }
            return 0;
        }

        public int getHeight() {
            Bitmap bitmap2 = this.bitmap;
            if (bitmap2 != null) {
                return bitmap2.getHeight();
            }
            return 0;
        }

        public boolean isRecycled() {
            Bitmap bitmap2 = this.bitmap;
            return bitmap2 == null || bitmap2.isRecycled();
        }

        public void release() {
            Bitmap bitmap2;
            if (this.key == null) {
                if (this.recycleOnRelease && (bitmap2 = this.bitmap) != null) {
                    bitmap2.recycle();
                }
                this.bitmap = null;
                return;
            }
            boolean decrementUseCount = ImageLoader.getInstance().decrementUseCount(this.key);
            if (!ImageLoader.getInstance().isInMemCache(this.key, false) && decrementUseCount) {
                this.bitmap.recycle();
            }
            this.key = null;
            this.bitmap = null;
        }
    }

    private class SetImageBackup {
        public int cacheType;
        public String ext;
        public String imageFilter;
        public ImageLocation imageLocation;
        public String mediaFilter;
        public ImageLocation mediaLocation;
        public Object parentObject;
        public int size;
        public Drawable thumb;
        public String thumbFilter;
        public ImageLocation thumbLocation;

        private SetImageBackup() {
        }

        /* access modifiers changed from: private */
        public boolean isSet() {
            return (this.imageLocation == null && this.thumbLocation == null && this.mediaLocation == null && this.thumb == null) ? false : true;
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0018, code lost:
            r0 = r2.mediaLocation;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x000c, code lost:
            r0 = r2.thumbLocation;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean isWebfileSet() {
            /*
                r2 = this;
                org.telegram.messenger.ImageLocation r0 = r2.imageLocation
                if (r0 == 0) goto L_0x000c
                org.telegram.messenger.WebFile r1 = r0.webFile
                if (r1 != 0) goto L_0x0024
                java.lang.String r0 = r0.path
                if (r0 != 0) goto L_0x0024
            L_0x000c:
                org.telegram.messenger.ImageLocation r0 = r2.thumbLocation
                if (r0 == 0) goto L_0x0018
                org.telegram.messenger.WebFile r1 = r0.webFile
                if (r1 != 0) goto L_0x0024
                java.lang.String r0 = r0.path
                if (r0 != 0) goto L_0x0024
            L_0x0018:
                org.telegram.messenger.ImageLocation r0 = r2.mediaLocation
                if (r0 == 0) goto L_0x0026
                org.telegram.messenger.WebFile r1 = r0.webFile
                if (r1 != 0) goto L_0x0024
                java.lang.String r0 = r0.path
                if (r0 == 0) goto L_0x0026
            L_0x0024:
                r0 = 1
                goto L_0x0027
            L_0x0026:
                r0 = 0
            L_0x0027:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageReceiver.SetImageBackup.isWebfileSet():boolean");
        }

        /* access modifiers changed from: private */
        public void clear() {
            this.imageLocation = null;
            this.thumbLocation = null;
            this.mediaLocation = null;
            this.thumb = null;
        }
    }

    public ImageReceiver() {
        this((View) null);
    }

    public ImageReceiver(View view) {
        this.allowStartAnimation = true;
        this.autoRepeat = 1;
        this.drawRegion = new RectF();
        this.isVisible = true;
        this.roundRect = new RectF();
        this.bitmapRect = new RectF();
        this.shaderMatrix = new Matrix();
        this.overrideAlpha = 1.0f;
        this.crossfadeAlpha = 1;
        this.parentView = view;
        this.roundPaint = new Paint(3);
        this.currentAccount = UserConfig.selectedAccount;
    }

    public void cancelLoadImage() {
        this.forceLoding = false;
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
        this.canceledLoading = true;
    }

    public void setForceLoading(boolean z) {
        this.forceLoding = z;
    }

    public boolean isForceLoding() {
        return this.forceLoding;
    }

    public void setStrippedLocation(ImageLocation imageLocation) {
        this.strippedLocation = imageLocation;
    }

    public void setIgnoreImageSet(boolean z) {
        this.ignoreImageSet = z;
    }

    public ImageLocation getStrippedLocation() {
        return this.strippedLocation;
    }

    public void setImage(ImageLocation imageLocation, String str, Drawable drawable, String str2, Object obj, int i) {
        setImage(imageLocation, str, (ImageLocation) null, (String) null, drawable, 0, str2, obj, i);
    }

    public void setImage(ImageLocation imageLocation, String str, Drawable drawable, int i, String str2, Object obj, int i2) {
        setImage(imageLocation, str, (ImageLocation) null, (String) null, drawable, i, str2, obj, i2);
    }

    public void setImage(String str, String str2, Drawable drawable, String str3, int i) {
        setImage(ImageLocation.getForPath(str), str2, (ImageLocation) null, (String) null, drawable, i, str3, (Object) null, 1);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, String str3, Object obj, int i) {
        setImage(imageLocation, str, imageLocation2, str2, (Drawable) null, 0, str3, obj, i);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, int i, String str3, Object obj, int i2) {
        setImage(imageLocation, str, imageLocation2, str2, (Drawable) null, i, str3, obj, i2);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, Drawable drawable, int i, String str3, Object obj, int i2) {
        setImage((ImageLocation) null, (String) null, imageLocation, str, imageLocation2, str2, drawable, i, str3, obj, i2);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, ImageLocation imageLocation3, String str3, Drawable drawable, int i, String str4, Object obj, int i2) {
        boolean z;
        String str5;
        String str6;
        SetImageBackup setImageBackup2;
        ImageLocation imageLocation4 = imageLocation;
        String str7 = str;
        ImageLocation imageLocation5 = imageLocation2;
        String str8 = str2;
        ImageLocation imageLocation6 = imageLocation3;
        String str9 = str3;
        Drawable drawable2 = drawable;
        String str10 = str4;
        Object obj2 = obj;
        if (!this.ignoreImageSet) {
            if (this.crossfadeWithOldImage && (setImageBackup2 = this.setImageBackup) != null && setImageBackup2.isWebfileSet()) {
                setBackupImage();
            }
            SetImageBackup setImageBackup3 = this.setImageBackup;
            if (setImageBackup3 != null) {
                setImageBackup3.clear();
            }
            boolean z2 = true;
            if (imageLocation5 == null && imageLocation6 == null && imageLocation4 == null) {
                for (int i3 = 0; i3 < 4; i3++) {
                    recycleBitmap((String) null, i3);
                }
                this.currentImageLocation = null;
                this.currentImageFilter = null;
                this.currentImageKey = null;
                this.currentMediaLocation = null;
                this.currentMediaFilter = null;
                this.currentMediaKey = null;
                this.currentThumbLocation = null;
                this.currentThumbFilter = null;
                this.currentThumbKey = null;
                this.currentMediaDrawable = null;
                this.mediaShader = null;
                this.currentImageDrawable = null;
                this.imageShader = null;
                this.thumbShader = null;
                this.crossfadeShader = null;
                this.currentExt = str10;
                this.currentParentObject = null;
                this.currentCacheType = 0;
                this.staticThumbDrawable = drawable2;
                this.currentAlpha = 1.0f;
                this.currentSize = 0;
                ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
                View view = this.parentView;
                if (view != null) {
                    if (this.invalidateAll) {
                        view.invalidate();
                    } else {
                        int i4 = this.imageX;
                        int i5 = this.imageY;
                        view.invalidate(i4, i5, this.imageW + i4, this.imageH + i5);
                    }
                }
                ImageReceiverDelegate imageReceiverDelegate = this.delegate;
                if (imageReceiverDelegate != null) {
                    boolean z3 = (this.currentImageDrawable == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true;
                    if (!(this.currentImageDrawable == null && this.currentMediaDrawable == null)) {
                        z2 = false;
                    }
                    imageReceiverDelegate.didSetImage(this, z3, z2);
                    return;
                }
                return;
            }
            String key = imageLocation5 != null ? imageLocation5.getKey(obj2, (Object) null, false) : null;
            if (key == null && imageLocation5 != null) {
                imageLocation5 = null;
            }
            this.currentKeyQuality = false;
            if (key == null && this.needsQualityThumb && ((obj2 instanceof MessageObject) || this.qulityThumbDocument != null)) {
                TLRPC.Document document = this.qulityThumbDocument;
                if (document == null) {
                    document = ((MessageObject) obj2).getDocument();
                }
                if (!(document == null || document.dc_id == 0 || document.id == 0)) {
                    key = "q_" + document.dc_id + "_" + document.id;
                    this.currentKeyQuality = true;
                }
            }
            if (!(key == null || str8 == null)) {
                key = key + "@" + str8;
            }
            String key2 = imageLocation4 != null ? imageLocation4.getKey(obj2, (Object) null, false) : null;
            if (key2 == null && imageLocation4 != null) {
                imageLocation4 = null;
            }
            if (!(key2 == null || str7 == null)) {
                key2 = key2 + "@" + str7;
            }
            if ((key2 == null && (str6 = this.currentImageKey) != null && str6.equals(key)) || ((str5 = this.currentMediaKey) != null && str5.equals(key2))) {
                ImageReceiverDelegate imageReceiverDelegate2 = this.delegate;
                if (imageReceiverDelegate2 != null) {
                    imageReceiverDelegate2.didSetImage(this, (this.currentImageDrawable == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true, this.currentImageDrawable == null && this.currentMediaDrawable == null);
                }
                if (!this.canceledLoading && !this.forcePreview) {
                    return;
                }
            }
            ImageLocation imageLocation7 = this.strippedLocation;
            if (imageLocation7 == null) {
                imageLocation7 = imageLocation4 != null ? imageLocation4 : imageLocation5;
            }
            String key3 = imageLocation6 != null ? imageLocation6.getKey(obj2, imageLocation7, false) : null;
            if (!(key3 == null || str9 == null)) {
                key3 = key3 + "@" + str9;
            }
            if (this.crossfadeWithOldImage) {
                if (this.currentImageDrawable != null) {
                    recycleBitmap(key3, 1);
                    recycleBitmap((String) null, 2);
                    recycleBitmap(key2, 3);
                    this.crossfadeShader = this.imageShader;
                    this.crossfadeImage = this.currentImageDrawable;
                    this.crossfadeKey = this.currentImageKey;
                    this.crossfadingWithThumb = false;
                    this.currentImageDrawable = null;
                    this.currentImageKey = null;
                } else if (this.currentThumbDrawable != null) {
                    recycleBitmap(key, 0);
                    recycleBitmap((String) null, 2);
                    recycleBitmap(key2, 3);
                    this.crossfadeShader = this.thumbShader;
                    this.crossfadeImage = this.currentThumbDrawable;
                    this.crossfadeKey = this.currentThumbKey;
                    this.crossfadingWithThumb = false;
                    this.currentThumbDrawable = null;
                    this.currentThumbKey = null;
                } else if (this.staticThumbDrawable != null) {
                    recycleBitmap(key, 0);
                    recycleBitmap(key3, 1);
                    recycleBitmap((String) null, 2);
                    recycleBitmap(key2, 3);
                    this.crossfadeShader = this.thumbShader;
                    this.crossfadeImage = this.staticThumbDrawable;
                    this.crossfadingWithThumb = false;
                    this.crossfadeKey = null;
                    this.currentThumbDrawable = null;
                    this.currentThumbKey = null;
                } else {
                    recycleBitmap(key, 0);
                    z = true;
                    recycleBitmap(key3, 1);
                    recycleBitmap((String) null, 2);
                    recycleBitmap(key2, 3);
                    this.crossfadeShader = null;
                }
                z = true;
            } else {
                z = true;
                recycleBitmap(key, 0);
                recycleBitmap(key3, 1);
                recycleBitmap((String) null, 2);
                recycleBitmap(key2, 3);
                this.crossfadeShader = null;
            }
            this.currentImageLocation = imageLocation5;
            this.currentImageFilter = str8;
            this.currentImageKey = key;
            this.currentMediaLocation = imageLocation4;
            this.currentMediaFilter = str7;
            this.currentMediaKey = key2;
            this.currentThumbLocation = imageLocation6;
            this.currentThumbFilter = str9;
            this.currentThumbKey = key3;
            this.currentParentObject = obj2;
            this.currentExt = str10;
            this.currentSize = i;
            this.currentCacheType = i2;
            this.staticThumbDrawable = drawable2;
            this.imageShader = null;
            this.thumbShader = null;
            this.mediaShader = null;
            this.currentAlpha = 1.0f;
            ImageReceiverDelegate imageReceiverDelegate3 = this.delegate;
            if (imageReceiverDelegate3 != null) {
                imageReceiverDelegate3.didSetImage(this, (this.currentImageDrawable == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true, this.currentImageDrawable == null && this.currentMediaDrawable == null);
            }
            ImageLoader.getInstance().loadImageForImageReceiver(this);
            View view2 = this.parentView;
            if (view2 != null) {
                if (this.invalidateAll) {
                    view2.invalidate();
                } else {
                    int i6 = this.imageX;
                    int i7 = this.imageY;
                    view2.invalidate(i6, i7, this.imageW + i6, this.imageH + i7);
                }
            }
            if (!(obj2 instanceof MessageObject) || !((MessageObject) obj2).isRoundVideo()) {
                z = false;
            }
            this.isRoundVideo = z;
        }
    }

    public boolean canInvertBitmap() {
        return (this.currentMediaDrawable instanceof ExtendedBitmapDrawable) || (this.currentImageDrawable instanceof ExtendedBitmapDrawable) || (this.currentThumbDrawable instanceof ExtendedBitmapDrawable) || (this.staticThumbDrawable instanceof ExtendedBitmapDrawable);
    }

    public void setColorFilter(ColorFilter colorFilter2) {
        this.colorFilter = colorFilter2;
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
        this.thumbOrientation = i;
        this.imageOrientation = i;
        this.centerRotation = z;
    }

    public void setInvalidateAll(boolean z) {
        this.invalidateAll = z;
    }

    public Drawable getStaticThumb() {
        return this.staticThumbDrawable;
    }

    public int getAnimatedOrientation() {
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            return animation.getOrientation();
        }
        return 0;
    }

    public int getOrientation() {
        return this.imageOrientation;
    }

    public void setLayerNum(int i) {
        this.currentLayerNum = i;
    }

    public void setImageBitmap(Bitmap bitmap) {
        BitmapDrawable bitmapDrawable = null;
        if (bitmap != null) {
            bitmapDrawable = new BitmapDrawable((Resources) null, bitmap);
        }
        setImageBitmap((Drawable) bitmapDrawable);
    }

    public void setImageBitmap(Drawable drawable) {
        boolean z = true;
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
        if (!this.crossfadeWithOldImage) {
            for (int i = 0; i < 4; i++) {
                recycleBitmap((String) null, i);
            }
        } else if (this.currentImageDrawable != null) {
            recycleBitmap((String) null, 1);
            recycleBitmap((String) null, 2);
            recycleBitmap((String) null, 3);
            this.crossfadeShader = this.imageShader;
            this.crossfadeImage = this.currentImageDrawable;
            this.crossfadeKey = this.currentImageKey;
            this.crossfadingWithThumb = true;
        } else if (this.currentThumbDrawable != null) {
            recycleBitmap((String) null, 0);
            recycleBitmap((String) null, 2);
            recycleBitmap((String) null, 3);
            this.crossfadeShader = this.thumbShader;
            this.crossfadeImage = this.currentThumbDrawable;
            this.crossfadeKey = this.currentThumbKey;
            this.crossfadingWithThumb = true;
        } else if (this.staticThumbDrawable != null) {
            recycleBitmap((String) null, 0);
            recycleBitmap((String) null, 1);
            recycleBitmap((String) null, 2);
            recycleBitmap((String) null, 3);
            this.crossfadeShader = this.thumbShader;
            this.crossfadeImage = this.staticThumbDrawable;
            this.crossfadingWithThumb = true;
            this.crossfadeKey = null;
        } else {
            for (int i2 = 0; i2 < 4; i2++) {
                recycleBitmap((String) null, i2);
            }
            this.crossfadeShader = null;
        }
        Drawable drawable2 = this.staticThumbDrawable;
        if (drawable2 instanceof RecyclableDrawable) {
            ((RecyclableDrawable) drawable2).recycle();
        }
        if (drawable instanceof AnimatedFileDrawable) {
            AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) drawable;
            animatedFileDrawable.setParentView(this.parentView);
            animatedFileDrawable.setUseSharedQueue(this.useSharedAnimationQueue);
            if (this.allowStartAnimation) {
                animatedFileDrawable.start();
            }
            animatedFileDrawable.setAllowDecodeSingleFrame(this.allowDecodeSingleFrame);
        } else if (drawable instanceof RLottieDrawable) {
            RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
            rLottieDrawable.addParentView(this.parentView);
            if (this.currentOpenedLayerFlags == 0) {
                rLottieDrawable.start();
            }
            rLottieDrawable.setAllowDecodeSingleFrame(true);
        }
        this.staticThumbDrawable = drawable;
        updateDrawableRadius(drawable);
        this.currentMediaLocation = null;
        this.currentMediaFilter = null;
        this.currentMediaDrawable = null;
        this.currentMediaKey = null;
        this.mediaShader = null;
        this.currentImageLocation = null;
        this.currentImageFilter = null;
        this.currentImageDrawable = null;
        this.currentImageKey = null;
        this.imageShader = null;
        this.currentThumbLocation = null;
        this.currentThumbFilter = null;
        this.currentThumbKey = null;
        this.currentKeyQuality = false;
        this.currentExt = null;
        this.currentSize = 0;
        this.currentCacheType = 0;
        this.currentAlpha = 1.0f;
        SetImageBackup setImageBackup2 = this.setImageBackup;
        if (setImageBackup2 != null) {
            setImageBackup2.clear();
        }
        ImageReceiverDelegate imageReceiverDelegate = this.delegate;
        if (imageReceiverDelegate != null) {
            imageReceiverDelegate.didSetImage(this, (this.currentThumbDrawable == null && this.staticThumbDrawable == null) ? false : true, true);
        }
        View view = this.parentView;
        if (view != null) {
            if (this.invalidateAll) {
                view.invalidate();
            } else {
                int i3 = this.imageX;
                int i4 = this.imageY;
                view.invalidate(i3, i4, this.imageW + i3, this.imageH + i4);
            }
        }
        if (this.forceCrossfade && this.crossfadeWithOldImage && this.crossfadeImage != null) {
            this.currentAlpha = 0.0f;
            this.lastUpdateAlphaTime = System.currentTimeMillis();
            if (this.currentThumbDrawable == null && this.staticThumbDrawable == null) {
                z = false;
            }
            this.crossfadeWithThumb = z;
        }
    }

    private void setDrawableShader(Drawable drawable, BitmapShader bitmapShader) {
        if (drawable == this.currentThumbDrawable || drawable == this.staticThumbDrawable) {
            this.thumbShader = bitmapShader;
        } else if (drawable == this.currentMediaDrawable) {
            this.mediaShader = bitmapShader;
        } else if (drawable == this.currentImageDrawable) {
            this.imageShader = bitmapShader;
        }
    }

    private void updateDrawableRadius(Drawable drawable) {
        int i = this.roundRadius;
        if (i == 0 || !(drawable instanceof BitmapDrawable)) {
            setDrawableShader(drawable, (BitmapShader) null);
        } else if (!(drawable instanceof RLottieDrawable)) {
            if (drawable instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) drawable).setRoundRadius(i);
                return;
            }
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            setDrawableShader(drawable, new BitmapShader(bitmap, tileMode, tileMode));
        }
    }

    public void clearImage() {
        for (int i = 0; i < 4; i++) {
            recycleBitmap((String) null, i);
        }
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
    }

    public void onDetachedFromWindow() {
        if (!(this.currentImageLocation == null && this.currentMediaLocation == null && this.currentThumbLocation == null && this.staticThumbDrawable == null)) {
            if (this.setImageBackup == null) {
                this.setImageBackup = new SetImageBackup();
            }
            SetImageBackup setImageBackup2 = this.setImageBackup;
            setImageBackup2.mediaLocation = this.currentMediaLocation;
            setImageBackup2.mediaFilter = this.currentMediaFilter;
            setImageBackup2.imageLocation = this.currentImageLocation;
            setImageBackup2.imageFilter = this.currentImageFilter;
            setImageBackup2.thumbLocation = this.currentThumbLocation;
            setImageBackup2.thumbFilter = this.currentThumbFilter;
            setImageBackup2.thumb = this.staticThumbDrawable;
            setImageBackup2.size = this.currentSize;
            setImageBackup2.ext = this.currentExt;
            setImageBackup2.cacheType = this.currentCacheType;
            setImageBackup2.parentObject = this.currentParentObject;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopAllHeavyOperations);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.startAllHeavyOperations);
        if (this.staticThumbDrawable != null) {
            this.staticThumbDrawable = null;
            this.thumbShader = null;
        }
        clearImage();
    }

    private boolean setBackupImage() {
        RLottieDrawable lottieAnimation;
        SetImageBackup setImageBackup2 = this.setImageBackup;
        if (setImageBackup2 == null || !setImageBackup2.isSet()) {
            return false;
        }
        SetImageBackup setImageBackup3 = this.setImageBackup;
        this.setImageBackup = null;
        setImage(setImageBackup3.mediaLocation, setImageBackup3.mediaFilter, setImageBackup3.imageLocation, setImageBackup3.imageFilter, setImageBackup3.thumbLocation, setImageBackup3.thumbFilter, setImageBackup3.thumb, setImageBackup3.size, setImageBackup3.ext, setImageBackup3.parentObject, setImageBackup3.cacheType);
        setImageBackup3.clear();
        this.setImageBackup = setImageBackup3;
        if (this.currentOpenedLayerFlags != 0 || (lottieAnimation = getLottieAnimation()) == null) {
            return true;
        }
        lottieAnimation.start();
        return true;
    }

    public boolean onAttachedToWindow() {
        RLottieDrawable lottieAnimation;
        this.currentOpenedLayerFlags = NotificationCenter.getGlobalInstance().getCurrentHeavyOperationFlags();
        this.currentOpenedLayerFlags &= this.currentLayerNum ^ -1;
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopAllHeavyOperations);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.startAllHeavyOperations);
        if (setBackupImage()) {
            return true;
        }
        if (this.currentOpenedLayerFlags != 0 || (lottieAnimation = getLottieAnimation()) == null) {
            return false;
        }
        lottieAnimation.start();
        return false;
    }

    private void drawDrawable(Canvas canvas, Drawable drawable, int i, BitmapShader bitmapShader, int i2) {
        Paint paint;
        int i3;
        int i4;
        int i5;
        Canvas canvas2 = canvas;
        Drawable drawable2 = drawable;
        int i6 = i;
        BitmapShader bitmapShader2 = bitmapShader;
        int i7 = i2;
        if (drawable2 instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable2;
            if (bitmapShader2 != null) {
                paint = this.roundPaint;
            } else {
                paint = bitmapDrawable.getPaint();
            }
            boolean z = (paint == null || paint.getColorFilter() == null) ? false : true;
            if (!z || this.isPressed != 0) {
                if (!z && (i5 = this.isPressed) != 0) {
                    if (i5 == 1) {
                        if (bitmapShader2 != null) {
                            this.roundPaint.setColorFilter(selectedColorFilter);
                        } else {
                            bitmapDrawable.setColorFilter(selectedColorFilter);
                        }
                    } else if (bitmapShader2 != null) {
                        this.roundPaint.setColorFilter(selectedGroupColorFilter);
                    } else {
                        bitmapDrawable.setColorFilter(selectedGroupColorFilter);
                    }
                }
            } else if (bitmapShader2 != null) {
                this.roundPaint.setColorFilter((ColorFilter) null);
            } else if (this.staticThumbDrawable != drawable2) {
                bitmapDrawable.setColorFilter((ColorFilter) null);
            }
            ColorFilter colorFilter2 = this.colorFilter;
            if (colorFilter2 != null) {
                if (bitmapShader2 != null) {
                    this.roundPaint.setColorFilter(colorFilter2);
                } else {
                    bitmapDrawable.setColorFilter(colorFilter2);
                }
            }
            boolean z2 = bitmapDrawable instanceof AnimatedFileDrawable;
            if (z2 || (bitmapDrawable instanceof RLottieDrawable)) {
                int i8 = i7 % 360;
                if (i8 == 90 || i8 == 270) {
                    i4 = bitmapDrawable.getIntrinsicHeight();
                    i3 = bitmapDrawable.getIntrinsicWidth();
                } else {
                    i4 = bitmapDrawable.getIntrinsicWidth();
                    i3 = bitmapDrawable.getIntrinsicHeight();
                }
            } else {
                int i9 = i7 % 360;
                if (i9 == 90 || i9 == 270) {
                    i4 = bitmapDrawable.getBitmap().getHeight();
                    i3 = bitmapDrawable.getBitmap().getWidth();
                } else {
                    i4 = bitmapDrawable.getBitmap().getWidth();
                    i3 = bitmapDrawable.getBitmap().getHeight();
                }
            }
            int i10 = this.imageW;
            float f = this.sideClip;
            float f2 = ((float) i10) - (f * 2.0f);
            float f3 = ((float) this.imageH) - (f * 2.0f);
            float f4 = i10 == 0 ? 1.0f : ((float) i4) / f2;
            float f5 = this.imageH == 0 ? 1.0f : ((float) i3) / f3;
            if (bitmapShader2 == null) {
                Canvas canvas3 = canvas2;
                int i11 = i6;
                if (this.isAspectFit) {
                    float max = Math.max(f4, f5);
                    canvas.save();
                    int i12 = (int) (((float) i4) / max);
                    int i13 = (int) (((float) i3) / max);
                    RectF rectF = this.drawRegion;
                    int i14 = this.imageX;
                    int i15 = this.imageW;
                    int i16 = this.imageY;
                    int i17 = this.imageH;
                    rectF.set(((float) i14) + (((float) (i15 - i12)) / 2.0f), ((float) i16) + (((float) (i17 - i13)) / 2.0f), ((float) i14) + (((float) (i15 + i12)) / 2.0f), ((float) i16) + (((float) (i17 + i13)) / 2.0f));
                    RectF rectF2 = this.drawRegion;
                    bitmapDrawable.setBounds((int) rectF2.left, (int) rectF2.top, (int) rectF2.right, (int) rectF2.bottom);
                    if (z2) {
                        RectF rectF3 = this.drawRegion;
                        ((AnimatedFileDrawable) bitmapDrawable).setActualDrawRect(rectF3.left, rectF3.top, rectF3.width(), this.drawRegion.height());
                    }
                    if (this.isVisible) {
                        try {
                            bitmapDrawable.setAlpha(i11);
                            bitmapDrawable.draw(canvas3);
                        } catch (Exception e) {
                            onBitmapException(bitmapDrawable);
                            FileLog.e((Throwable) e);
                        }
                    }
                    canvas.restore();
                } else if (Math.abs(f4 - f5) > 1.0E-5f) {
                    canvas.save();
                    int i18 = this.imageX;
                    int i19 = this.imageY;
                    canvas3.clipRect(i18, i19, this.imageW + i18, this.imageH + i19);
                    int i20 = i7 % 360;
                    if (i20 != 0) {
                        if (this.centerRotation) {
                            canvas3.rotate((float) i7, (float) (this.imageW / 2), (float) (this.imageH / 2));
                        } else {
                            canvas3.rotate((float) i7, 0.0f, 0.0f);
                        }
                    }
                    float f6 = ((float) i4) / f5;
                    int i21 = this.imageW;
                    if (f6 > ((float) i21)) {
                        int i22 = (int) f6;
                        RectF rectF4 = this.drawRegion;
                        int i23 = this.imageX;
                        int i24 = this.imageY;
                        rectF4.set(((float) i23) - (((float) (i22 - i21)) / 2.0f), (float) i24, ((float) i23) + (((float) (i22 + i21)) / 2.0f), (float) (i24 + this.imageH));
                    } else {
                        int i25 = (int) (((float) i3) / f4);
                        RectF rectF5 = this.drawRegion;
                        int i26 = this.imageX;
                        int i27 = this.imageY;
                        int i28 = this.imageH;
                        rectF5.set((float) i26, ((float) i27) - (((float) (i25 - i28)) / 2.0f), (float) (i26 + i21), ((float) i27) + (((float) (i25 + i28)) / 2.0f));
                    }
                    if (z2) {
                        ((AnimatedFileDrawable) bitmapDrawable).setActualDrawRect((float) this.imageX, (float) this.imageY, (float) this.imageW, (float) this.imageH);
                    }
                    if (i20 == 90 || i20 == 270) {
                        float width = this.drawRegion.width() / 2.0f;
                        float height = this.drawRegion.height() / 2.0f;
                        float centerX = this.drawRegion.centerX();
                        float centerY = this.drawRegion.centerY();
                        bitmapDrawable.setBounds((int) (centerX - height), (int) (centerY - width), (int) (centerX + height), (int) (centerY + width));
                    } else {
                        RectF rectF6 = this.drawRegion;
                        bitmapDrawable.setBounds((int) rectF6.left, (int) rectF6.top, (int) rectF6.right, (int) rectF6.bottom);
                    }
                    if (this.isVisible) {
                        try {
                            bitmapDrawable.setAlpha(i11);
                            bitmapDrawable.draw(canvas3);
                        } catch (Exception e2) {
                            onBitmapException(bitmapDrawable);
                            FileLog.e((Throwable) e2);
                        }
                    }
                    canvas.restore();
                } else {
                    canvas.save();
                    int i29 = i7 % 360;
                    if (i29 != 0) {
                        if (this.centerRotation) {
                            canvas3.rotate((float) i7, (float) (this.imageW / 2), (float) (this.imageH / 2));
                        } else {
                            canvas3.rotate((float) i7, 0.0f, 0.0f);
                        }
                    }
                    RectF rectF7 = this.drawRegion;
                    int i30 = this.imageX;
                    int i31 = this.imageY;
                    rectF7.set((float) i30, (float) i31, (float) (i30 + this.imageW), (float) (i31 + this.imageH));
                    if (this.isRoundVideo) {
                        RectF rectF8 = this.drawRegion;
                        int i32 = AndroidUtilities.roundMessageInset;
                        rectF8.inset((float) (-i32), (float) (-i32));
                    }
                    if (z2) {
                        ((AnimatedFileDrawable) bitmapDrawable).setActualDrawRect((float) this.imageX, (float) this.imageY, (float) this.imageW, (float) this.imageH);
                    }
                    if (i29 == 90 || i29 == 270) {
                        float width2 = this.drawRegion.width() / 2.0f;
                        float height2 = this.drawRegion.height() / 2.0f;
                        float centerX2 = this.drawRegion.centerX();
                        float centerY2 = this.drawRegion.centerY();
                        bitmapDrawable.setBounds((int) (centerX2 - height2), (int) (centerY2 - width2), (int) (centerX2 + height2), (int) (centerY2 + width2));
                    } else {
                        RectF rectF9 = this.drawRegion;
                        bitmapDrawable.setBounds((int) rectF9.left, (int) rectF9.top, (int) rectF9.right, (int) rectF9.bottom);
                    }
                    if (this.isVisible) {
                        try {
                            bitmapDrawable.setAlpha(i11);
                            bitmapDrawable.draw(canvas3);
                        } catch (Exception e3) {
                            onBitmapException(bitmapDrawable);
                            FileLog.e((Throwable) e3);
                        }
                    }
                    canvas.restore();
                }
            } else if (this.isAspectFit) {
                float max2 = Math.max(f4, f5);
                int i33 = (int) (((float) i4) / max2);
                int i34 = (int) (((float) i3) / max2);
                RectF rectvar_ = this.drawRegion;
                int i35 = this.imageX;
                int i36 = this.imageW;
                int i37 = this.imageY;
                int i38 = this.imageH;
                rectvar_.set((float) (((i36 - i33) / 2) + i35), (float) (((i38 - i34) / 2) + i37), (float) (i35 + ((i36 + i33) / 2)), (float) (i37 + ((i38 + i34) / 2)));
                if (this.isVisible) {
                    this.roundPaint.setShader(bitmapShader2);
                    this.shaderMatrix.reset();
                    Matrix matrix = this.shaderMatrix;
                    RectF rectvar_ = this.drawRegion;
                    matrix.setTranslate(rectvar_.left, rectvar_.top);
                    float f7 = 1.0f / max2;
                    this.shaderMatrix.preScale(f7, f7);
                    bitmapShader2.setLocalMatrix(this.shaderMatrix);
                    this.roundPaint.setAlpha(i6);
                    this.roundRect.set(this.drawRegion);
                    RectF rectvar_ = this.roundRect;
                    int i39 = this.roundRadius;
                    canvas2.drawRoundRect(rectvar_, (float) i39, (float) i39, this.roundPaint);
                }
            } else {
                this.roundPaint.setShader(bitmapShader2);
                float min = 1.0f / Math.min(f4, f5);
                RectF rectvar_ = this.roundRect;
                int i40 = this.imageX;
                float f8 = this.sideClip;
                int i41 = this.imageY;
                rectvar_.set(((float) i40) + f8, ((float) i41) + f8, ((float) (i40 + this.imageW)) - f8, ((float) (i41 + this.imageH)) - f8);
                this.shaderMatrix.reset();
                if (Math.abs(f4 - f5) > 5.0E-4f) {
                    float f9 = ((float) i4) / f5;
                    if (f9 > f2) {
                        RectF rectvar_ = this.drawRegion;
                        int i42 = this.imageX;
                        float var_ = (float) ((int) f9);
                        int i43 = this.imageY;
                        rectvar_.set(((float) i42) - ((var_ - f2) / 2.0f), (float) i43, ((float) i42) + ((var_ + f2) / 2.0f), ((float) i43) + f3);
                    } else {
                        RectF rectvar_ = this.drawRegion;
                        int i44 = this.imageX;
                        int i45 = this.imageY;
                        float var_ = (float) ((int) (((float) i3) / f4));
                        rectvar_.set((float) i44, ((float) i45) - ((var_ - f3) / 2.0f), ((float) i44) + f2, ((float) i45) + ((var_ + f3) / 2.0f));
                    }
                } else {
                    RectF rectvar_ = this.drawRegion;
                    int i46 = this.imageX;
                    int i47 = this.imageY;
                    rectvar_.set((float) i46, (float) i47, ((float) i46) + f2, ((float) i47) + f3);
                }
                if (this.isVisible) {
                    this.shaderMatrix.reset();
                    Matrix matrix2 = this.shaderMatrix;
                    RectF rectvar_ = this.drawRegion;
                    float var_ = rectvar_.left;
                    float var_ = this.sideClip;
                    matrix2.setTranslate(var_ + var_, rectvar_.top + var_);
                    if (i7 == 90) {
                        this.shaderMatrix.preRotate(90.0f);
                        this.shaderMatrix.preTranslate(0.0f, -this.drawRegion.width());
                    } else if (i7 == 180) {
                        this.shaderMatrix.preRotate(180.0f);
                        this.shaderMatrix.preTranslate(-this.drawRegion.width(), -this.drawRegion.height());
                    } else if (i7 == 270) {
                        this.shaderMatrix.preRotate(270.0f);
                        this.shaderMatrix.preTranslate(-this.drawRegion.height(), 0.0f);
                    }
                    this.shaderMatrix.preScale(min, min);
                    if (this.isRoundVideo) {
                        float var_ = (((float) (AndroidUtilities.roundMessageInset * 2)) + f2) / f2;
                        this.shaderMatrix.postScale(var_, var_, this.drawRegion.centerX(), this.drawRegion.centerY());
                    }
                    bitmapShader.setLocalMatrix(this.shaderMatrix);
                    this.roundPaint.setAlpha(i);
                    RectF rectvar_ = this.roundRect;
                    int i48 = this.roundRadius;
                    canvas.drawRoundRect(rectvar_, (float) i48, (float) i48, this.roundPaint);
                }
            }
        } else {
            Canvas canvas4 = canvas2;
            int i49 = i6;
            RectF rectvar_ = this.drawRegion;
            int i50 = this.imageX;
            int i51 = this.imageY;
            rectvar_.set((float) i50, (float) i51, (float) (i50 + this.imageW), (float) (i51 + this.imageH));
            RectF rectvar_ = this.drawRegion;
            drawable2.setBounds((int) rectvar_.left, (int) rectvar_.top, (int) rectvar_.right, (int) rectvar_.bottom);
            if (this.isVisible) {
                try {
                    drawable.setAlpha(i);
                    drawable2.draw(canvas4);
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
            }
        }
    }

    private void onBitmapException(Drawable drawable) {
        if (drawable == this.currentMediaDrawable && this.currentMediaKey != null) {
            ImageLoader.getInstance().removeImage(this.currentMediaKey);
            this.currentMediaKey = null;
        } else if (drawable == this.currentImageDrawable && this.currentImageKey != null) {
            ImageLoader.getInstance().removeImage(this.currentImageKey);
            this.currentImageKey = null;
        } else if (drawable == this.currentThumbDrawable && this.currentThumbKey != null) {
            ImageLoader.getInstance().removeImage(this.currentThumbKey);
            this.currentThumbKey = null;
        }
        setImage(this.currentMediaLocation, this.currentMediaFilter, this.currentImageLocation, this.currentImageFilter, this.currentThumbLocation, this.currentThumbFilter, this.currentThumbDrawable, this.currentSize, this.currentExt, this.currentParentObject, this.currentCacheType);
    }

    private void checkAlphaAnimation(boolean z) {
        if (!this.manualAlphaAnimator && this.currentAlpha != 1.0f) {
            if (!z) {
                long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateAlphaTime;
                if (currentTimeMillis > 18) {
                    currentTimeMillis = 18;
                }
                this.currentAlpha += ((float) currentTimeMillis) / 150.0f;
                if (this.currentAlpha > 1.0f) {
                    this.currentAlpha = 1.0f;
                    if (this.crossfadeImage != null) {
                        recycleBitmap((String) null, 2);
                        this.crossfadeShader = null;
                    }
                }
            }
            this.lastUpdateAlphaTime = System.currentTimeMillis();
            View view = this.parentView;
            if (view == null) {
                return;
            }
            if (this.invalidateAll) {
                view.invalidate();
                return;
            }
            int i = this.imageX;
            int i2 = this.imageY;
            view.invalidate(i, i2, this.imageW + i, this.imageH + i2);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:100:0x013b A[Catch:{ Exception -> 0x0156 }] */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0095 A[Catch:{ Exception -> 0x0156 }] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0103 A[Catch:{ Exception -> 0x0156 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean draw(android.graphics.Canvas r14) {
        /*
            r13 = this;
            r7 = 0
            org.telegram.ui.Components.AnimatedFileDrawable r0 = r13.getAnimation()     // Catch:{ Exception -> 0x0156 }
            org.telegram.ui.Components.RLottieDrawable r1 = r13.getLottieAnimation()     // Catch:{ Exception -> 0x0156 }
            r8 = 1
            if (r0 == 0) goto L_0x0012
            boolean r2 = r0.hasBitmap()     // Catch:{ Exception -> 0x0156 }
            if (r2 == 0) goto L_0x001a
        L_0x0012:
            if (r1 == 0) goto L_0x001c
            boolean r2 = r1.hasBitmap()     // Catch:{ Exception -> 0x0156 }
            if (r2 != 0) goto L_0x001c
        L_0x001a:
            r2 = 1
            goto L_0x001d
        L_0x001c:
            r2 = 0
        L_0x001d:
            if (r1 == 0) goto L_0x0024
            android.view.View r3 = r13.parentView     // Catch:{ Exception -> 0x0156 }
            r1.setCurrentParentView(r3)     // Catch:{ Exception -> 0x0156 }
        L_0x0024:
            if (r0 != 0) goto L_0x0028
            if (r1 == 0) goto L_0x0039
        L_0x0028:
            if (r2 != 0) goto L_0x0039
            boolean r0 = r13.animationReadySent     // Catch:{ Exception -> 0x0156 }
            if (r0 != 0) goto L_0x0039
            r13.animationReadySent = r8     // Catch:{ Exception -> 0x0156 }
            org.telegram.messenger.ImageReceiver$ImageReceiverDelegate r0 = r13.delegate     // Catch:{ Exception -> 0x0156 }
            if (r0 == 0) goto L_0x0039
            org.telegram.messenger.ImageReceiver$ImageReceiverDelegate r0 = r13.delegate     // Catch:{ Exception -> 0x0156 }
            r0.onAnimationReady(r13)     // Catch:{ Exception -> 0x0156 }
        L_0x0039:
            boolean r0 = r13.forcePreview     // Catch:{ Exception -> 0x0156 }
            r1 = 0
            if (r0 != 0) goto L_0x004e
            android.graphics.drawable.Drawable r0 = r13.currentMediaDrawable     // Catch:{ Exception -> 0x0156 }
            if (r0 == 0) goto L_0x004e
            if (r2 != 0) goto L_0x004e
            android.graphics.drawable.Drawable r0 = r13.currentMediaDrawable     // Catch:{ Exception -> 0x0156 }
            android.graphics.BitmapShader r3 = r13.mediaShader     // Catch:{ Exception -> 0x0156 }
            int r4 = r13.imageOrientation     // Catch:{ Exception -> 0x0156 }
        L_0x004a:
            r9 = r2
            r10 = r3
            r11 = r4
            goto L_0x0091
        L_0x004e:
            boolean r0 = r13.forcePreview     // Catch:{ Exception -> 0x0156 }
            if (r0 != 0) goto L_0x0066
            android.graphics.drawable.Drawable r0 = r13.currentImageDrawable     // Catch:{ Exception -> 0x0156 }
            if (r0 == 0) goto L_0x0066
            if (r2 == 0) goto L_0x005c
            android.graphics.drawable.Drawable r0 = r13.currentMediaDrawable     // Catch:{ Exception -> 0x0156 }
            if (r0 == 0) goto L_0x0066
        L_0x005c:
            android.graphics.drawable.Drawable r0 = r13.currentImageDrawable     // Catch:{ Exception -> 0x0156 }
            android.graphics.BitmapShader r2 = r13.imageShader     // Catch:{ Exception -> 0x0156 }
            int r3 = r13.imageOrientation     // Catch:{ Exception -> 0x0156 }
            r10 = r2
            r11 = r3
            r9 = 0
            goto L_0x0091
        L_0x0066:
            android.graphics.drawable.Drawable r0 = r13.crossfadeImage     // Catch:{ Exception -> 0x0156 }
            if (r0 == 0) goto L_0x0075
            boolean r0 = r13.crossfadingWithThumb     // Catch:{ Exception -> 0x0156 }
            if (r0 != 0) goto L_0x0075
            android.graphics.drawable.Drawable r0 = r13.crossfadeImage     // Catch:{ Exception -> 0x0156 }
            android.graphics.BitmapShader r3 = r13.crossfadeShader     // Catch:{ Exception -> 0x0156 }
            int r4 = r13.imageOrientation     // Catch:{ Exception -> 0x0156 }
            goto L_0x004a
        L_0x0075:
            android.graphics.drawable.Drawable r0 = r13.staticThumbDrawable     // Catch:{ Exception -> 0x0156 }
            boolean r0 = r0 instanceof android.graphics.drawable.BitmapDrawable     // Catch:{ Exception -> 0x0156 }
            if (r0 == 0) goto L_0x0082
            android.graphics.drawable.Drawable r0 = r13.staticThumbDrawable     // Catch:{ Exception -> 0x0156 }
            android.graphics.BitmapShader r3 = r13.thumbShader     // Catch:{ Exception -> 0x0156 }
            int r4 = r13.thumbOrientation     // Catch:{ Exception -> 0x0156 }
            goto L_0x004a
        L_0x0082:
            android.graphics.drawable.Drawable r0 = r13.currentThumbDrawable     // Catch:{ Exception -> 0x0156 }
            if (r0 == 0) goto L_0x008d
            android.graphics.drawable.Drawable r0 = r13.currentThumbDrawable     // Catch:{ Exception -> 0x0156 }
            android.graphics.BitmapShader r3 = r13.thumbShader     // Catch:{ Exception -> 0x0156 }
            int r4 = r13.thumbOrientation     // Catch:{ Exception -> 0x0156 }
            goto L_0x004a
        L_0x008d:
            r0 = r1
            r10 = r0
            r9 = r2
            r11 = 0
        L_0x0091:
            r12 = 1132396544(0x437var_, float:255.0)
            if (r0 == 0) goto L_0x013b
            byte r2 = r13.crossfadeAlpha     // Catch:{ Exception -> 0x0156 }
            if (r2 == 0) goto L_0x0121
            boolean r2 = r13.crossfadeWithThumb     // Catch:{ Exception -> 0x0156 }
            if (r2 == 0) goto L_0x00ae
            if (r9 == 0) goto L_0x00ae
            float r1 = r13.overrideAlpha     // Catch:{ Exception -> 0x0156 }
            float r1 = r1 * r12
            int r4 = (int) r1     // Catch:{ Exception -> 0x0156 }
            r1 = r13
            r2 = r14
            r3 = r0
            r5 = r10
            r6 = r11
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x0156 }
            goto L_0x012e
        L_0x00ae:
            boolean r2 = r13.crossfadeWithThumb     // Catch:{ Exception -> 0x0156 }
            if (r2 == 0) goto L_0x010f
            float r2 = r13.currentAlpha     // Catch:{ Exception -> 0x0156 }
            r3 = 1065353216(0x3var_, float:1.0)
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x010f
            android.graphics.drawable.Drawable r2 = r13.currentImageDrawable     // Catch:{ Exception -> 0x0156 }
            if (r0 == r2) goto L_0x00e2
            android.graphics.drawable.Drawable r2 = r13.currentMediaDrawable     // Catch:{ Exception -> 0x0156 }
            if (r0 != r2) goto L_0x00c3
            goto L_0x00e2
        L_0x00c3:
            android.graphics.drawable.Drawable r2 = r13.currentThumbDrawable     // Catch:{ Exception -> 0x0156 }
            if (r0 == r2) goto L_0x00d9
            android.graphics.drawable.Drawable r2 = r13.crossfadeImage     // Catch:{ Exception -> 0x0156 }
            if (r0 != r2) goto L_0x00cc
            goto L_0x00d9
        L_0x00cc:
            android.graphics.drawable.Drawable r2 = r13.staticThumbDrawable     // Catch:{ Exception -> 0x0156 }
            if (r0 != r2) goto L_0x00ff
            android.graphics.drawable.Drawable r2 = r13.crossfadeImage     // Catch:{ Exception -> 0x0156 }
            if (r2 == 0) goto L_0x00ff
            android.graphics.drawable.Drawable r1 = r13.crossfadeImage     // Catch:{ Exception -> 0x0156 }
            android.graphics.BitmapShader r2 = r13.crossfadeShader     // Catch:{ Exception -> 0x0156 }
            goto L_0x00ea
        L_0x00d9:
            android.graphics.drawable.Drawable r2 = r13.staticThumbDrawable     // Catch:{ Exception -> 0x0156 }
            if (r2 == 0) goto L_0x00ff
            android.graphics.drawable.Drawable r1 = r13.staticThumbDrawable     // Catch:{ Exception -> 0x0156 }
            android.graphics.BitmapShader r2 = r13.thumbShader     // Catch:{ Exception -> 0x0156 }
            goto L_0x00ea
        L_0x00e2:
            android.graphics.drawable.Drawable r2 = r13.crossfadeImage     // Catch:{ Exception -> 0x0156 }
            if (r2 == 0) goto L_0x00ed
            android.graphics.drawable.Drawable r1 = r13.crossfadeImage     // Catch:{ Exception -> 0x0156 }
            android.graphics.BitmapShader r2 = r13.crossfadeShader     // Catch:{ Exception -> 0x0156 }
        L_0x00ea:
            r3 = r1
            r5 = r2
            goto L_0x0101
        L_0x00ed:
            android.graphics.drawable.Drawable r2 = r13.currentThumbDrawable     // Catch:{ Exception -> 0x0156 }
            if (r2 == 0) goto L_0x00f6
            android.graphics.drawable.Drawable r1 = r13.currentThumbDrawable     // Catch:{ Exception -> 0x0156 }
            android.graphics.BitmapShader r2 = r13.thumbShader     // Catch:{ Exception -> 0x0156 }
            goto L_0x00ea
        L_0x00f6:
            android.graphics.drawable.Drawable r2 = r13.staticThumbDrawable     // Catch:{ Exception -> 0x0156 }
            if (r2 == 0) goto L_0x00ff
            android.graphics.drawable.Drawable r1 = r13.staticThumbDrawable     // Catch:{ Exception -> 0x0156 }
            android.graphics.BitmapShader r2 = r13.thumbShader     // Catch:{ Exception -> 0x0156 }
            goto L_0x00ea
        L_0x00ff:
            r3 = r1
            r5 = r3
        L_0x0101:
            if (r3 == 0) goto L_0x010f
            float r1 = r13.overrideAlpha     // Catch:{ Exception -> 0x0156 }
            float r1 = r1 * r12
            int r4 = (int) r1     // Catch:{ Exception -> 0x0156 }
            int r6 = r13.thumbOrientation     // Catch:{ Exception -> 0x0156 }
            r1 = r13
            r2 = r14
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x0156 }
        L_0x010f:
            float r1 = r13.overrideAlpha     // Catch:{ Exception -> 0x0156 }
            float r2 = r13.currentAlpha     // Catch:{ Exception -> 0x0156 }
            float r1 = r1 * r2
            float r1 = r1 * r12
            int r4 = (int) r1     // Catch:{ Exception -> 0x0156 }
            r1 = r13
            r2 = r14
            r3 = r0
            r5 = r10
            r6 = r11
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x0156 }
            goto L_0x012e
        L_0x0121:
            float r1 = r13.overrideAlpha     // Catch:{ Exception -> 0x0156 }
            float r1 = r1 * r12
            int r4 = (int) r1     // Catch:{ Exception -> 0x0156 }
            r1 = r13
            r2 = r14
            r3 = r0
            r5 = r10
            r6 = r11
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x0156 }
        L_0x012e:
            if (r9 == 0) goto L_0x0136
            boolean r0 = r13.crossfadeWithThumb     // Catch:{ Exception -> 0x0156 }
            if (r0 == 0) goto L_0x0136
            r0 = 1
            goto L_0x0137
        L_0x0136:
            r0 = 0
        L_0x0137:
            r13.checkAlphaAnimation(r0)     // Catch:{ Exception -> 0x0156 }
            return r8
        L_0x013b:
            android.graphics.drawable.Drawable r0 = r13.staticThumbDrawable     // Catch:{ Exception -> 0x0156 }
            if (r0 == 0) goto L_0x0152
            android.graphics.drawable.Drawable r3 = r13.staticThumbDrawable     // Catch:{ Exception -> 0x0156 }
            float r0 = r13.overrideAlpha     // Catch:{ Exception -> 0x0156 }
            float r0 = r0 * r12
            int r4 = (int) r0     // Catch:{ Exception -> 0x0156 }
            r5 = 0
            int r6 = r13.thumbOrientation     // Catch:{ Exception -> 0x0156 }
            r1 = r13
            r2 = r14
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x0156 }
            r13.checkAlphaAnimation(r9)     // Catch:{ Exception -> 0x0156 }
            return r8
        L_0x0152:
            r13.checkAlphaAnimation(r9)     // Catch:{ Exception -> 0x0156 }
            goto L_0x015a
        L_0x0156:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x015a:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageReceiver.draw(android.graphics.Canvas):boolean");
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

    public Drawable getDrawable() {
        Drawable drawable = this.currentMediaDrawable;
        if (drawable != null) {
            return drawable;
        }
        Drawable drawable2 = this.currentImageDrawable;
        if (drawable2 != null) {
            return drawable2;
        }
        Drawable drawable3 = this.currentThumbDrawable;
        if (drawable3 != null) {
            return drawable3;
        }
        Drawable drawable4 = this.staticThumbDrawable;
        if (drawable4 != null) {
            return drawable4;
        }
        return null;
    }

    public Bitmap getBitmap() {
        AnimatedFileDrawable animation = getAnimation();
        RLottieDrawable lottieAnimation = getLottieAnimation();
        if (lottieAnimation != null && lottieAnimation.hasBitmap()) {
            return lottieAnimation.getAnimatedBitmap();
        }
        if (animation != null && animation.hasBitmap()) {
            return animation.getAnimatedBitmap();
        }
        Drawable drawable = this.currentMediaDrawable;
        if ((drawable instanceof BitmapDrawable) && !(drawable instanceof AnimatedFileDrawable) && !(drawable instanceof RLottieDrawable)) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Drawable drawable2 = this.currentImageDrawable;
        if ((drawable2 instanceof BitmapDrawable) && !(drawable2 instanceof AnimatedFileDrawable) && !(this.currentMediaDrawable instanceof RLottieDrawable)) {
            return ((BitmapDrawable) drawable2).getBitmap();
        }
        Drawable drawable3 = this.currentThumbDrawable;
        if ((drawable3 instanceof BitmapDrawable) && !(drawable3 instanceof AnimatedFileDrawable) && !(this.currentMediaDrawable instanceof RLottieDrawable)) {
            return ((BitmapDrawable) drawable3).getBitmap();
        }
        Drawable drawable4 = this.staticThumbDrawable;
        if (drawable4 instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable4).getBitmap();
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x007f  */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.telegram.messenger.ImageReceiver.BitmapHolder getBitmapSafe() {
        /*
            r4 = this;
            org.telegram.ui.Components.AnimatedFileDrawable r0 = r4.getAnimation()
            org.telegram.ui.Components.RLottieDrawable r1 = r4.getLottieAnimation()
            r2 = 0
            if (r1 == 0) goto L_0x0018
            boolean r3 = r1.hasBitmap()
            if (r3 == 0) goto L_0x0018
            android.graphics.Bitmap r0 = r1.getAnimatedBitmap()
        L_0x0015:
            r1 = r2
            goto L_0x007d
        L_0x0018:
            if (r0 == 0) goto L_0x0025
            boolean r1 = r0.hasBitmap()
            if (r1 == 0) goto L_0x0025
            android.graphics.Bitmap r0 = r0.getAnimatedBitmap()
            goto L_0x0015
        L_0x0025:
            android.graphics.drawable.Drawable r0 = r4.currentMediaDrawable
            boolean r1 = r0 instanceof android.graphics.drawable.BitmapDrawable
            if (r1 == 0) goto L_0x003c
            boolean r1 = r0 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r1 != 0) goto L_0x003c
            boolean r1 = r0 instanceof org.telegram.ui.Components.RLottieDrawable
            if (r1 != 0) goto L_0x003c
            android.graphics.drawable.BitmapDrawable r0 = (android.graphics.drawable.BitmapDrawable) r0
            android.graphics.Bitmap r0 = r0.getBitmap()
            java.lang.String r1 = r4.currentMediaKey
            goto L_0x007d
        L_0x003c:
            android.graphics.drawable.Drawable r0 = r4.currentImageDrawable
            boolean r1 = r0 instanceof android.graphics.drawable.BitmapDrawable
            if (r1 == 0) goto L_0x0055
            boolean r1 = r0 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r1 != 0) goto L_0x0055
            android.graphics.drawable.Drawable r1 = r4.currentMediaDrawable
            boolean r1 = r1 instanceof org.telegram.ui.Components.RLottieDrawable
            if (r1 != 0) goto L_0x0055
            android.graphics.drawable.BitmapDrawable r0 = (android.graphics.drawable.BitmapDrawable) r0
            android.graphics.Bitmap r0 = r0.getBitmap()
            java.lang.String r1 = r4.currentImageKey
            goto L_0x007d
        L_0x0055:
            android.graphics.drawable.Drawable r0 = r4.currentThumbDrawable
            boolean r1 = r0 instanceof android.graphics.drawable.BitmapDrawable
            if (r1 == 0) goto L_0x006e
            boolean r1 = r0 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r1 != 0) goto L_0x006e
            android.graphics.drawable.Drawable r1 = r4.currentMediaDrawable
            boolean r1 = r1 instanceof org.telegram.ui.Components.RLottieDrawable
            if (r1 != 0) goto L_0x006e
            android.graphics.drawable.BitmapDrawable r0 = (android.graphics.drawable.BitmapDrawable) r0
            android.graphics.Bitmap r0 = r0.getBitmap()
            java.lang.String r1 = r4.currentThumbKey
            goto L_0x007d
        L_0x006e:
            android.graphics.drawable.Drawable r0 = r4.staticThumbDrawable
            boolean r1 = r0 instanceof android.graphics.drawable.BitmapDrawable
            if (r1 == 0) goto L_0x007b
            android.graphics.drawable.BitmapDrawable r0 = (android.graphics.drawable.BitmapDrawable) r0
            android.graphics.Bitmap r0 = r0.getBitmap()
            goto L_0x0015
        L_0x007b:
            r0 = r2
            r1 = r0
        L_0x007d:
            if (r0 == 0) goto L_0x0084
            org.telegram.messenger.ImageReceiver$BitmapHolder r2 = new org.telegram.messenger.ImageReceiver$BitmapHolder
            r2.<init>(r0, r1)
        L_0x0084:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageReceiver.getBitmapSafe():org.telegram.messenger.ImageReceiver$BitmapHolder");
    }

    public Bitmap getThumbBitmap() {
        Drawable drawable = this.currentThumbDrawable;
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Drawable drawable2 = this.staticThumbDrawable;
        if (drawable2 instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable2).getBitmap();
        }
        return null;
    }

    public BitmapHolder getThumbBitmapSafe() {
        String str;
        Bitmap bitmap;
        Drawable drawable = this.currentThumbDrawable;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            str = this.currentThumbKey;
        } else {
            Drawable drawable2 = this.staticThumbDrawable;
            if (drawable2 instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable2).getBitmap();
                str = null;
            } else {
                bitmap = null;
                str = null;
            }
        }
        if (bitmap != null) {
            return new BitmapHolder(bitmap, str);
        }
        return null;
    }

    public int getBitmapWidth() {
        getDrawable();
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            int i = this.imageOrientation;
            return (i % 360 == 0 || i % 360 == 180) ? animation.getIntrinsicWidth() : animation.getIntrinsicHeight();
        }
        RLottieDrawable lottieAnimation = getLottieAnimation();
        if (lottieAnimation != null) {
            return lottieAnimation.getIntrinsicWidth();
        }
        Bitmap bitmap = getBitmap();
        if (bitmap == null) {
            Drawable drawable = this.staticThumbDrawable;
            if (drawable != null) {
                return drawable.getIntrinsicWidth();
            }
            return 1;
        }
        int i2 = this.imageOrientation;
        return (i2 % 360 == 0 || i2 % 360 == 180) ? bitmap.getWidth() : bitmap.getHeight();
    }

    public int getBitmapHeight() {
        getDrawable();
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            int i = this.imageOrientation;
            return (i % 360 == 0 || i % 360 == 180) ? animation.getIntrinsicHeight() : animation.getIntrinsicWidth();
        }
        RLottieDrawable lottieAnimation = getLottieAnimation();
        if (lottieAnimation != null) {
            return lottieAnimation.getIntrinsicHeight();
        }
        Bitmap bitmap = getBitmap();
        if (bitmap == null) {
            Drawable drawable = this.staticThumbDrawable;
            if (drawable != null) {
                return drawable.getIntrinsicHeight();
            }
            return 1;
        }
        int i2 = this.imageOrientation;
        return (i2 % 360 == 0 || i2 % 360 == 180) ? bitmap.getHeight() : bitmap.getWidth();
    }

    public void setVisible(boolean z, boolean z2) {
        View view;
        if (this.isVisible != z) {
            this.isVisible = z;
            if (z2 && (view = this.parentView) != null) {
                if (this.invalidateAll) {
                    view.invalidate();
                    return;
                }
                int i = this.imageX;
                int i2 = this.imageY;
                view.invalidate(i, i2, this.imageW + i, this.imageH + i2);
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

    public void setAspectFit(boolean z) {
        this.isAspectFit = z;
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

    public void setSideClip(float f) {
        this.sideClip = f;
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
        float f;
        float f2;
        if (this.imageOrientation % 180 != 0) {
            f2 = this.drawRegion.height();
            f = this.drawRegion.width();
        } else {
            f2 = this.drawRegion.width();
            f = this.drawRegion.height();
        }
        return f2 / f;
    }

    public String getExt() {
        return this.currentExt;
    }

    public boolean isInsideImage(float f, float f2) {
        int i = this.imageX;
        if (f >= ((float) i) && f <= ((float) (i + this.imageW))) {
            int i2 = this.imageY;
            return f2 >= ((float) i2) && f2 <= ((float) (i2 + this.imageH));
        }
    }

    public RectF getDrawRegion() {
        return this.drawRegion;
    }

    public int getNewGuid() {
        int i = this.currentGuid + 1;
        this.currentGuid = i;
        return i;
    }

    public String getImageKey() {
        return this.currentImageKey;
    }

    public String getMediaKey() {
        return this.currentMediaKey;
    }

    public String getThumbKey() {
        return this.currentThumbKey;
    }

    public int getSize() {
        return this.currentSize;
    }

    public ImageLocation getMediaLocation() {
        return this.currentMediaLocation;
    }

    public ImageLocation getImageLocation() {
        return this.currentImageLocation;
    }

    public ImageLocation getThumbLocation() {
        return this.currentThumbLocation;
    }

    public String getMediaFilter() {
        return this.currentMediaFilter;
    }

    public String getImageFilter() {
        return this.currentImageFilter;
    }

    public String getThumbFilter() {
        return this.currentThumbFilter;
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
        if (this.roundRadius != i) {
            this.roundRadius = i;
            Drawable drawable = this.currentImageDrawable;
            if (drawable != null && this.imageShader == null) {
                updateDrawableRadius(drawable);
            }
            Drawable drawable2 = this.currentMediaDrawable;
            if (drawable2 != null && this.mediaShader == null) {
                updateDrawableRadius(drawable2);
            }
            if (this.thumbShader == null) {
                Drawable drawable3 = this.currentThumbDrawable;
                if (drawable3 != null) {
                    updateDrawableRadius(drawable3);
                    return;
                }
                Drawable drawable4 = this.staticThumbDrawable;
                if (drawable4 != null) {
                    updateDrawableRadius(drawable4);
                }
            }
        }
    }

    public void setCurrentAccount(int i) {
        this.currentAccount = i;
    }

    public int getRoundRadius() {
        return this.roundRadius;
    }

    public Object getParentObject() {
        return this.currentParentObject;
    }

    public void setNeedsQualityThumb(boolean z) {
        this.needsQualityThumb = z;
    }

    public void setQualityThumbDocument(TLRPC.Document document) {
        this.qulityThumbDocument = document;
    }

    public TLRPC.Document getQulityThumbDocument() {
        return this.qulityThumbDocument;
    }

    public void setCrossfadeWithOldImage(boolean z) {
        this.crossfadeWithOldImage = z;
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

    public void setAutoRepeat(int i) {
        this.autoRepeat = i;
        RLottieDrawable lottieAnimation = getLottieAnimation();
        if (lottieAnimation != null) {
            lottieAnimation.setAutoRepeat(i);
        }
    }

    public void setUseSharedAnimationQueue(boolean z) {
        this.useSharedAnimationQueue = z;
    }

    public boolean isAllowStartAnimation() {
        return this.allowStartAnimation;
    }

    public void startAnimation() {
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            animation.setUseSharedQueue(this.useSharedAnimationQueue);
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
        Drawable drawable = this.currentMediaDrawable;
        if (drawable instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) drawable;
        }
        Drawable drawable2 = this.currentImageDrawable;
        if (drawable2 instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) drawable2;
        }
        Drawable drawable3 = this.currentThumbDrawable;
        if (drawable3 instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) drawable3;
        }
        Drawable drawable4 = this.staticThumbDrawable;
        if (drawable4 instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) drawable4;
        }
        return null;
    }

    public RLottieDrawable getLottieAnimation() {
        Drawable drawable = this.currentMediaDrawable;
        if (drawable instanceof RLottieDrawable) {
            return (RLottieDrawable) drawable;
        }
        Drawable drawable2 = this.currentImageDrawable;
        if (drawable2 instanceof RLottieDrawable) {
            return (RLottieDrawable) drawable2;
        }
        Drawable drawable3 = this.currentThumbDrawable;
        if (drawable3 instanceof RLottieDrawable) {
            return (RLottieDrawable) drawable3;
        }
        Drawable drawable4 = this.staticThumbDrawable;
        if (drawable4 instanceof RLottieDrawable) {
            return (RLottieDrawable) drawable4;
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public int getTag(int i) {
        if (i == 1) {
            return this.thumbTag;
        }
        if (i == 3) {
            return this.mediaTag;
        }
        return this.imageTag;
    }

    /* access modifiers changed from: protected */
    public void setTag(int i, int i2) {
        if (i2 == 1) {
            this.thumbTag = i;
        } else if (i2 == 3) {
            this.mediaTag = i;
        } else {
            this.imageTag = i;
        }
    }

    public void setParam(int i) {
        this.param = i;
    }

    public int getParam() {
        return this.param;
    }

    /* access modifiers changed from: protected */
    public boolean setImageBitmapByKey(Drawable drawable, String str, int i, boolean z, int i2) {
        Drawable drawable2;
        boolean z2 = false;
        if (drawable == null || str == null || this.currentGuid != i2) {
            return false;
        }
        if (i == 0) {
            if (!str.equals(this.currentImageKey)) {
                return false;
            }
            if (!(drawable instanceof AnimatedFileDrawable)) {
                ImageLoader.getInstance().incrementUseCount(this.currentImageKey);
            }
            this.currentImageDrawable = drawable;
            if (drawable instanceof ExtendedBitmapDrawable) {
                this.imageOrientation = ((ExtendedBitmapDrawable) drawable).getOrientation();
            }
            updateDrawableRadius(drawable);
            if ((z || this.forcePreview) && !this.forceCrossfade) {
                this.currentAlpha = 1.0f;
            } else {
                Drawable drawable3 = this.currentMediaDrawable;
                if (((!(drawable3 instanceof AnimatedFileDrawable) || !((AnimatedFileDrawable) drawable3).hasBitmap()) && !(this.currentImageDrawable instanceof RLottieDrawable)) && ((this.currentThumbDrawable == null && this.staticThumbDrawable == null) || this.currentAlpha == 1.0f || this.forceCrossfade)) {
                    this.currentAlpha = 0.0f;
                    this.lastUpdateAlphaTime = System.currentTimeMillis();
                    this.crossfadeWithThumb = (this.crossfadeImage == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null) ? false : true;
                }
            }
        } else if (i == 3) {
            if (!str.equals(this.currentMediaKey)) {
                return false;
            }
            if (!(drawable instanceof AnimatedFileDrawable)) {
                ImageLoader.getInstance().incrementUseCount(this.currentMediaKey);
            }
            this.currentMediaDrawable = drawable;
            updateDrawableRadius(drawable);
            if (this.currentImageDrawable == null) {
                if ((z || this.forcePreview) && !this.forceCrossfade) {
                    this.currentAlpha = 1.0f;
                } else if ((this.currentThumbDrawable == null && this.staticThumbDrawable == null) || this.currentAlpha == 1.0f || this.forceCrossfade) {
                    this.currentAlpha = 0.0f;
                    this.lastUpdateAlphaTime = System.currentTimeMillis();
                    this.crossfadeWithThumb = (this.crossfadeImage == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null) ? false : true;
                }
            }
        } else if (i == 1) {
            if (this.currentThumbDrawable != null) {
                return false;
            }
            if (!this.forcePreview) {
                AnimatedFileDrawable animation = getAnimation();
                if (animation != null && animation.hasBitmap()) {
                    return false;
                }
                Drawable drawable4 = this.currentImageDrawable;
                if ((drawable4 != null && !(drawable4 instanceof AnimatedFileDrawable)) || ((drawable2 = this.currentMediaDrawable) != null && !(drawable2 instanceof AnimatedFileDrawable))) {
                    return false;
                }
            }
            if (!str.equals(this.currentThumbKey)) {
                return false;
            }
            ImageLoader.getInstance().incrementUseCount(this.currentThumbKey);
            this.currentThumbDrawable = drawable;
            if (drawable instanceof ExtendedBitmapDrawable) {
                this.thumbOrientation = ((ExtendedBitmapDrawable) drawable).getOrientation();
            }
            updateDrawableRadius(drawable);
            if (z || this.crossfadeAlpha == 2) {
                this.currentAlpha = 1.0f;
            } else {
                Object obj = this.currentParentObject;
                if (!(obj instanceof MessageObject) || !((MessageObject) obj).isRoundVideo() || !((MessageObject) this.currentParentObject).isSending()) {
                    this.currentAlpha = 0.0f;
                    this.lastUpdateAlphaTime = System.currentTimeMillis();
                    this.crossfadeWithThumb = this.staticThumbDrawable != null && this.currentImageKey == null && this.currentMediaKey == null;
                } else {
                    this.currentAlpha = 1.0f;
                }
            }
        }
        if (drawable instanceof AnimatedFileDrawable) {
            AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) drawable;
            animatedFileDrawable.setParentView(this.parentView);
            animatedFileDrawable.setUseSharedQueue(this.useSharedAnimationQueue);
            if (this.allowStartAnimation) {
                animatedFileDrawable.start();
            }
            animatedFileDrawable.setAllowDecodeSingleFrame(this.allowDecodeSingleFrame);
            this.animationReadySent = false;
        } else if (drawable instanceof RLottieDrawable) {
            RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
            rLottieDrawable.addParentView(this.parentView);
            if (this.currentOpenedLayerFlags == 0) {
                rLottieDrawable.start();
            }
            rLottieDrawable.setAllowDecodeSingleFrame(true);
            rLottieDrawable.setAutoRepeat(this.autoRepeat);
            this.animationReadySent = false;
        }
        View view = this.parentView;
        if (view != null) {
            if (this.invalidateAll) {
                view.invalidate();
            } else {
                int i3 = this.imageX;
                int i4 = this.imageY;
                view.invalidate(i3, i4, this.imageW + i3, this.imageH + i4);
            }
        }
        ImageReceiverDelegate imageReceiverDelegate = this.delegate;
        if (imageReceiverDelegate != null) {
            boolean z3 = (this.currentImageDrawable == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true;
            if (this.currentImageDrawable == null && this.currentMediaDrawable == null) {
                z2 = true;
            }
            imageReceiverDelegate.didSetImage(this, z3, z2);
        }
        return true;
    }

    private void recycleBitmap(String str, int i) {
        Object obj;
        String str2;
        String replacedKey;
        if (i == 3) {
            str2 = this.currentMediaKey;
            obj = this.currentMediaDrawable;
        } else if (i == 2) {
            str2 = this.crossfadeKey;
            obj = this.crossfadeImage;
        } else if (i == 1) {
            str2 = this.currentThumbKey;
            obj = this.currentThumbDrawable;
        } else {
            str2 = this.currentImageKey;
            obj = this.currentImageDrawable;
        }
        if (!(str2 == null || !str2.startsWith("-") || (replacedKey = ImageLoader.getInstance().getReplacedKey(str2)) == null)) {
            str2 = replacedKey;
        }
        boolean z = obj instanceof RLottieDrawable;
        if (z) {
            ((RLottieDrawable) obj).removeParentView(this.parentView);
        }
        ImageLoader.getInstance().getReplacedKey(str2);
        if (str2 != null && ((str == null || !str.equals(str2)) && obj != null)) {
            if (z) {
                RLottieDrawable rLottieDrawable = (RLottieDrawable) obj;
                boolean decrementUseCount = ImageLoader.getInstance().decrementUseCount(str2);
                if (!ImageLoader.getInstance().isInMemCache(str2, true) && decrementUseCount) {
                    rLottieDrawable.recycle();
                }
            } else if (obj instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) obj).recycle();
            } else if (obj instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) obj).getBitmap();
                boolean decrementUseCount2 = ImageLoader.getInstance().decrementUseCount(str2);
                if (!ImageLoader.getInstance().isInMemCache(str2, false) && decrementUseCount2) {
                    bitmap.recycle();
                }
            }
        }
        if (i == 3) {
            this.currentMediaKey = null;
            this.currentMediaDrawable = null;
        } else if (i == 2) {
            this.crossfadeKey = null;
            this.crossfadeImage = null;
        } else if (i == 1) {
            this.currentThumbDrawable = null;
            this.currentThumbKey = null;
        } else {
            this.currentImageDrawable = null;
            this.currentImageKey = null;
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3;
        RLottieDrawable lottieAnimation;
        RLottieDrawable lottieAnimation2;
        if (i == NotificationCenter.didReplacedPhotoInMemCache) {
            String str = objArr[0];
            String str2 = this.currentMediaKey;
            if (str2 != null && str2.equals(str)) {
                this.currentMediaKey = objArr[1];
                this.currentMediaLocation = objArr[2];
                SetImageBackup setImageBackup2 = this.setImageBackup;
                if (setImageBackup2 != null) {
                    setImageBackup2.mediaLocation = objArr[2];
                }
            }
            String str3 = this.currentImageKey;
            if (str3 != null && str3.equals(str)) {
                this.currentImageKey = objArr[1];
                this.currentImageLocation = objArr[2];
                SetImageBackup setImageBackup3 = this.setImageBackup;
                if (setImageBackup3 != null) {
                    setImageBackup3.imageLocation = objArr[2];
                }
            }
            String str4 = this.currentThumbKey;
            if (str4 != null && str4.equals(str)) {
                this.currentThumbKey = objArr[1];
                this.currentThumbLocation = objArr[2];
                SetImageBackup setImageBackup4 = this.setImageBackup;
                if (setImageBackup4 != null) {
                    setImageBackup4.thumbLocation = objArr[2];
                }
            }
        } else if (i == NotificationCenter.stopAllHeavyOperations) {
            Integer num = objArr[0];
            if (this.currentLayerNum < num.intValue()) {
                this.currentOpenedLayerFlags = num.intValue() | this.currentOpenedLayerFlags;
                if (this.currentOpenedLayerFlags != 0 && (lottieAnimation2 = getLottieAnimation()) != null) {
                    lottieAnimation2.stop();
                }
            }
        } else if (i == NotificationCenter.startAllHeavyOperations) {
            Integer num2 = objArr[0];
            if (this.currentLayerNum < num2.intValue() && (i3 = this.currentOpenedLayerFlags) != 0) {
                this.currentOpenedLayerFlags = (num2.intValue() ^ -1) & i3;
                if (this.currentOpenedLayerFlags == 0 && (lottieAnimation = getLottieAnimation()) != null) {
                    lottieAnimation.start();
                }
            }
        }
    }
}
