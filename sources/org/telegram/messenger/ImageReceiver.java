package org.telegram.messenger;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import androidx.annotation.Keep;
import java.util.ArrayList;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclableDrawable;

public class ImageReceiver implements NotificationCenter.NotificationCenterDelegate {
    public static final int DEFAULT_CROSSFADE_DURATION = 150;
    private static final int TYPE_CROSSFDADE = 2;
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_MEDIA = 3;
    public static final int TYPE_THUMB = 1;
    private static float[] radii = new float[8];
    private static PorterDuffColorFilter selectedColorFilter = new PorterDuffColorFilter(-2236963, PorterDuff.Mode.MULTIPLY);
    private static PorterDuffColorFilter selectedGroupColorFilter = new PorterDuffColorFilter(-4473925, PorterDuff.Mode.MULTIPLY);
    private boolean allowDecodeSingleFrame;
    private boolean allowStartAnimation;
    private boolean allowStartLottieAnimation;
    private int animateFromIsPressed;
    private boolean animationReadySent;
    private boolean attachedToWindow;
    private int autoRepeat;
    private RectF bitmapRect;
    private Object blendMode;
    private boolean canceledLoading;
    private boolean centerRotation;
    private ColorFilter colorFilter;
    private ComposeShader composeShader;
    private byte crossfadeAlpha;
    private int crossfadeDuration;
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
    private long endTime;
    private boolean forceCrossfade;
    private boolean forceLoding;
    private boolean forcePreview;
    private Bitmap gradientBitmap;
    private BitmapShader gradientShader;
    private boolean ignoreImageSet;
    private float imageH;
    private int imageOrientation;
    private BitmapShader imageShader;
    private int imageTag;
    private float imageW;
    private float imageX;
    private float imageY;
    private boolean invalidateAll;
    private boolean isAspectFit;
    private int isPressed;
    private boolean isRoundRect;
    private boolean isRoundVideo;
    private boolean isVisible;
    private long lastUpdateAlphaTime;
    private Bitmap legacyBitmap;
    private Canvas legacyCanvas;
    private Paint legacyPaint;
    private BitmapShader legacyShader;
    private ArrayList<Runnable> loadingOperations;
    private boolean manualAlphaAnimator;
    private BitmapShader mediaShader;
    private int mediaTag;
    private boolean needsQualityThumb;
    private float overrideAlpha;
    private int param;
    private View parentView;
    private float pressedProgress;
    private float previousAlpha;
    private TLRPC$Document qulityThumbDocument;
    private Paint roundPaint;
    private Path roundPath;
    private int[] roundRadius;
    private RectF roundRect;
    private SetImageBackup setImageBackup;
    private Matrix shaderMatrix;
    private boolean shouldGenerateQualityThumb;
    private float sideClip;
    private long startTime;
    private Drawable staticThumbDrawable;
    private ImageLocation strippedLocation;
    private int thumbOrientation;
    private BitmapShader thumbShader;
    private int thumbTag;
    private String uniqKeyPrefix;
    private boolean useRoundForThumb;
    private boolean useSharedAnimationQueue;

    public interface ImageReceiverDelegate {

        /* renamed from: org.telegram.messenger.ImageReceiver$ImageReceiverDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onAnimationReady(ImageReceiverDelegate imageReceiverDelegate, ImageReceiver imageReceiver) {
            }
        }

        void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3);

        void onAnimationReady(ImageReceiver imageReceiver);
    }

    private boolean hasRoundRadius() {
        return true;
    }

    public static class BitmapHolder {
        public Bitmap bitmap;
        public Drawable drawable;
        private String key;
        public int orientation;
        private boolean recycleOnRelease;

        public BitmapHolder(Bitmap bitmap2, String str, int i) {
            this.bitmap = bitmap2;
            this.key = str;
            this.orientation = i;
            if (str != null) {
                ImageLoader.getInstance().incrementUseCount(this.key);
            }
        }

        public BitmapHolder(Drawable drawable2, String str, int i) {
            this.drawable = drawable2;
            this.key = str;
            this.orientation = i;
            if (str != null) {
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
                this.drawable = null;
                return;
            }
            boolean decrementUseCount = ImageLoader.getInstance().decrementUseCount(this.key);
            if (!ImageLoader.getInstance().isInMemCache(this.key, false) && decrementUseCount) {
                Bitmap bitmap3 = this.bitmap;
                if (bitmap3 != null) {
                    bitmap3.recycle();
                } else {
                    Drawable drawable2 = this.drawable;
                    if (drawable2 != null) {
                        if (drawable2 instanceof RLottieDrawable) {
                            ((RLottieDrawable) drawable2).recycle();
                        } else if (drawable2 instanceof AnimatedFileDrawable) {
                            ((AnimatedFileDrawable) drawable2).recycle();
                        } else if (drawable2 instanceof BitmapDrawable) {
                            ((BitmapDrawable) drawable2).getBitmap().recycle();
                        }
                    }
                }
            }
            this.key = null;
            this.bitmap = null;
            this.drawable = null;
        }
    }

    private static class SetImageBackup {
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
        this.useRoundForThumb = true;
        this.allowStartAnimation = true;
        this.allowStartLottieAnimation = true;
        this.autoRepeat = 1;
        this.drawRegion = new RectF();
        this.isVisible = true;
        this.roundRadius = new int[4];
        this.isRoundRect = true;
        this.roundRect = new RectF();
        this.bitmapRect = new RectF();
        this.shaderMatrix = new Matrix();
        this.roundPath = new Path();
        this.overrideAlpha = 1.0f;
        this.previousAlpha = 1.0f;
        this.crossfadeAlpha = 1;
        this.crossfadeDuration = 150;
        this.loadingOperations = new ArrayList<>();
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

    public void setForUserOrChat(TLObject tLObject, Drawable drawable) {
        setForUserOrChat(tLObject, drawable, (Object) null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002e, code lost:
        if (r2.stripped_thumb != null) goto L_0x001a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0018, code lost:
        if (r2.stripped_thumb != null) goto L_0x001a;
     */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0035  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0043  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setForUserOrChat(org.telegram.tgnet.TLObject r10, android.graphics.drawable.Drawable r11, java.lang.Object r12) {
        /*
            r9 = this;
            if (r12 != 0) goto L_0x0003
            r12 = r10
        L_0x0003:
            r0 = 1
            r9.setUseRoundForThumbDrawable(r0)
            r1 = 0
            boolean r2 = r10 instanceof org.telegram.tgnet.TLRPC$User
            r3 = 0
            if (r2 == 0) goto L_0x001f
            r2 = r10
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            org.telegram.tgnet.TLRPC$UserProfilePhoto r2 = r2.photo
            if (r2 == 0) goto L_0x001b
            android.graphics.drawable.BitmapDrawable r1 = r2.strippedBitmap
            byte[] r2 = r2.stripped_thumb
            if (r2 == 0) goto L_0x001b
        L_0x001a:
            r3 = 1
        L_0x001b:
            r8 = r3
            r3 = r1
            r1 = r8
            goto L_0x0033
        L_0x001f:
            boolean r2 = r10 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r2 == 0) goto L_0x0031
            r2 = r10
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            org.telegram.tgnet.TLRPC$ChatPhoto r2 = r2.photo
            if (r2 == 0) goto L_0x0031
            android.graphics.drawable.BitmapDrawable r1 = r2.strippedBitmap
            byte[] r2 = r2.stripped_thumb
            if (r2 == 0) goto L_0x001b
            goto L_0x001a
        L_0x0031:
            r3 = r1
            r1 = 0
        L_0x0033:
            if (r3 == 0) goto L_0x0043
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForUserOrChat(r10, r0)
            r4 = 0
            r6 = 0
            java.lang.String r2 = "50_50"
            r0 = r9
            r5 = r12
            r0.setImage(r1, r2, r3, r4, r5, r6)
            goto L_0x0068
        L_0x0043:
            if (r1 == 0) goto L_0x005a
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForUserOrChat(r10, r0)
            r0 = 2
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForUserOrChat(r10, r0)
            r7 = 0
            java.lang.String r2 = "50_50"
            java.lang.String r4 = "50_50_b"
            r0 = r9
            r5 = r11
            r6 = r12
            r0.setImage((org.telegram.messenger.ImageLocation) r1, (java.lang.String) r2, (org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6, (int) r7)
            goto L_0x0068
        L_0x005a:
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForUserOrChat(r10, r0)
            r4 = 0
            r6 = 0
            java.lang.String r2 = "50_50"
            r0 = r9
            r3 = r11
            r5 = r12
            r0.setImage(r1, r2, r3, r4, r5, r6)
        L_0x0068:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageReceiver.setForUserOrChat(org.telegram.tgnet.TLObject, android.graphics.drawable.Drawable, java.lang.Object):void");
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, Drawable drawable, Object obj, int i) {
        setImage((ImageLocation) null, (String) null, imageLocation, str, imageLocation2, str2, drawable, 0, (String) null, obj, i);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, Drawable drawable, int i, String str3, Object obj, int i2) {
        setImage((ImageLocation) null, (String) null, imageLocation, str, imageLocation2, str2, drawable, i, str3, obj, i2);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, ImageLocation imageLocation3, String str3, Drawable drawable, int i, String str4, Object obj, int i2) {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
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
            boolean z5 = true;
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
                this.composeShader = null;
                this.thumbShader = null;
                this.crossfadeShader = null;
                this.legacyShader = null;
                this.legacyCanvas = null;
                Bitmap bitmap = this.legacyBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                    this.legacyBitmap = null;
                }
                this.currentExt = str10;
                this.currentParentObject = null;
                this.currentCacheType = 0;
                this.roundPaint.setShader((Shader) null);
                this.staticThumbDrawable = drawable2;
                this.currentAlpha = 1.0f;
                this.previousAlpha = 1.0f;
                this.currentSize = 0;
                if (drawable2 instanceof SvgHelper.SvgDrawable) {
                    ((SvgHelper.SvgDrawable) drawable2).setParent(this);
                }
                ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
                View view = this.parentView;
                if (view != null) {
                    if (this.invalidateAll) {
                        view.invalidate();
                    } else {
                        float f = this.imageX;
                        float f2 = this.imageY;
                        view.invalidate((int) f, (int) f2, (int) (f + this.imageW), (int) (f2 + this.imageH));
                    }
                }
                ImageReceiverDelegate imageReceiverDelegate = this.delegate;
                if (imageReceiverDelegate != null) {
                    Drawable drawable3 = this.currentImageDrawable;
                    boolean z6 = (drawable3 == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true;
                    if (!(drawable3 == null && this.currentMediaDrawable == null)) {
                        z5 = false;
                    }
                    imageReceiverDelegate.didSetImage(this, z6, z5, false);
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
                TLRPC$Document tLRPC$Document = this.qulityThumbDocument;
                if (tLRPC$Document == null) {
                    tLRPC$Document = ((MessageObject) obj2).getDocument();
                }
                if (!(tLRPC$Document == null || tLRPC$Document.dc_id == 0 || tLRPC$Document.id == 0)) {
                    key = "q_" + tLRPC$Document.dc_id + "_" + tLRPC$Document.id;
                    this.currentKeyQuality = true;
                }
            }
            if (!(key == null || str8 == null)) {
                key = key + "@" + str8;
            }
            if (this.uniqKeyPrefix != null) {
                key = this.uniqKeyPrefix + key;
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
                    Drawable drawable4 = this.currentImageDrawable;
                    boolean z7 = (drawable4 == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true;
                    if (drawable4 == null && this.currentMediaDrawable == null) {
                        z4 = false;
                        z3 = true;
                    } else {
                        z4 = false;
                        z3 = false;
                    }
                    imageReceiverDelegate2.didSetImage(this, z7, z3, z4);
                }
                if (!this.canceledLoading) {
                    return;
                }
            }
            ImageLocation imageLocation7 = this.strippedLocation;
            if (imageLocation7 == null) {
                imageLocation7 = imageLocation4 != null ? imageLocation4 : imageLocation5;
            }
            if (imageLocation7 == null) {
                imageLocation7 = imageLocation6;
            }
            String key3 = imageLocation6 != null ? imageLocation6.getKey(obj2, imageLocation7, false) : null;
            if (!(key3 == null || str9 == null)) {
                key3 = key3 + "@" + str9;
            }
            if (this.crossfadeWithOldImage) {
                Drawable drawable5 = this.currentMediaDrawable;
                if (drawable5 != null) {
                    if (drawable5 instanceof AnimatedFileDrawable) {
                        ((AnimatedFileDrawable) drawable5).stop();
                    }
                    recycleBitmap(key3, 1);
                    recycleBitmap((String) null, 2);
                    recycleBitmap(key2, 0);
                    this.crossfadeImage = this.currentMediaDrawable;
                    this.crossfadeShader = this.mediaShader;
                    this.crossfadeKey = this.currentImageKey;
                    this.crossfadingWithThumb = false;
                    this.currentMediaDrawable = null;
                    this.currentMediaKey = null;
                } else if (this.currentImageDrawable != null) {
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
                    recycleBitmap(key3, 1);
                    recycleBitmap((String) null, 2);
                    recycleBitmap(key2, 3);
                    this.crossfadeShader = null;
                }
            } else {
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
            this.staticThumbDrawable = drawable;
            this.imageShader = null;
            this.composeShader = null;
            this.thumbShader = null;
            this.mediaShader = null;
            this.legacyShader = null;
            this.legacyCanvas = null;
            this.roundPaint.setShader((Shader) null);
            Bitmap bitmap2 = this.legacyBitmap;
            if (bitmap2 != null) {
                bitmap2.recycle();
                this.legacyBitmap = null;
            }
            this.currentAlpha = 1.0f;
            this.previousAlpha = 1.0f;
            Drawable drawable6 = this.staticThumbDrawable;
            if (drawable6 instanceof SvgHelper.SvgDrawable) {
                ((SvgHelper.SvgDrawable) drawable6).setParent(this);
            }
            updateDrawableRadius(this.staticThumbDrawable);
            ImageReceiverDelegate imageReceiverDelegate3 = this.delegate;
            if (imageReceiverDelegate3 != null) {
                Drawable drawable7 = this.currentImageDrawable;
                boolean z8 = (drawable7 == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true;
                if (drawable7 == null && this.currentMediaDrawable == null) {
                    z2 = false;
                    z = true;
                } else {
                    z2 = false;
                    z = false;
                }
                imageReceiverDelegate3.didSetImage(this, z8, z, z2);
            }
            ImageLoader.getInstance().loadImageForImageReceiver(this);
            View view2 = this.parentView;
            if (view2 != null) {
                if (this.invalidateAll) {
                    view2.invalidate();
                } else {
                    float f3 = this.imageX;
                    float f4 = this.imageY;
                    view2.invalidate((int) f3, (int) f4, (int) (f3 + this.imageW), (int) (f4 + this.imageH));
                }
            }
            this.isRoundVideo = (obj2 instanceof MessageObject) && ((MessageObject) obj2).isRoundVideo();
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
            if (this.attachedToWindow) {
                animatedFileDrawable.addParent(this.parentView);
            }
            animatedFileDrawable.setUseSharedQueue(this.useSharedAnimationQueue || animatedFileDrawable.isWebmSticker);
            if (this.allowStartAnimation && this.currentOpenedLayerFlags == 0) {
                animatedFileDrawable.start();
            }
            animatedFileDrawable.setAllowDecodeSingleFrame(this.allowDecodeSingleFrame);
        } else if (drawable instanceof RLottieDrawable) {
            RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
            rLottieDrawable.addParentView(this.parentView);
            if (this.allowStartLottieAnimation && (!rLottieDrawable.isHeavyDrawable() || this.currentOpenedLayerFlags == 0)) {
                rLottieDrawable.start();
            }
            rLottieDrawable.setAllowDecodeSingleFrame(true);
        }
        this.thumbShader = null;
        this.roundPaint.setShader((Shader) null);
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
        this.composeShader = null;
        this.legacyShader = null;
        this.legacyCanvas = null;
        Bitmap bitmap = this.legacyBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.legacyBitmap = null;
        }
        this.currentThumbLocation = null;
        this.currentThumbFilter = null;
        this.currentThumbKey = null;
        this.currentKeyQuality = false;
        this.currentExt = null;
        this.currentSize = 0;
        this.currentCacheType = 0;
        this.currentAlpha = 1.0f;
        this.previousAlpha = 1.0f;
        SetImageBackup setImageBackup2 = this.setImageBackup;
        if (setImageBackup2 != null) {
            setImageBackup2.clear();
        }
        ImageReceiverDelegate imageReceiverDelegate = this.delegate;
        if (imageReceiverDelegate != null) {
            imageReceiverDelegate.didSetImage(this, (this.currentThumbDrawable == null && this.staticThumbDrawable == null) ? false : true, true, false);
        }
        View view = this.parentView;
        if (view != null) {
            if (this.invalidateAll) {
                view.invalidate();
            } else {
                float f = this.imageX;
                float f2 = this.imageY;
                view.invalidate((int) f, (int) f2, (int) (f + this.imageW), (int) (f2 + this.imageH));
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
            if (this.gradientShader != null && (drawable instanceof BitmapDrawable)) {
                if (Build.VERSION.SDK_INT >= 28) {
                    this.composeShader = new ComposeShader(this.gradientShader, this.imageShader, PorterDuff.Mode.DST_IN);
                    return;
                }
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                int width = bitmapDrawable.getBitmap().getWidth();
                int height = bitmapDrawable.getBitmap().getHeight();
                Bitmap bitmap = this.legacyBitmap;
                if (bitmap == null || bitmap.getWidth() != width || this.legacyBitmap.getHeight() != height) {
                    Bitmap bitmap2 = this.legacyBitmap;
                    if (bitmap2 != null) {
                        bitmap2.recycle();
                    }
                    this.legacyBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    this.legacyCanvas = new Canvas(this.legacyBitmap);
                    Bitmap bitmap3 = this.legacyBitmap;
                    Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                    this.legacyShader = new BitmapShader(bitmap3, tileMode, tileMode);
                    if (this.legacyPaint == null) {
                        Paint paint = new Paint();
                        this.legacyPaint = paint;
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                    }
                }
            }
        }
    }

    private void updateDrawableRadius(Drawable drawable) {
        if (drawable != null) {
            if ((hasRoundRadius() || this.gradientShader != null) && (drawable instanceof BitmapDrawable)) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if (!(bitmapDrawable instanceof RLottieDrawable)) {
                    if (bitmapDrawable instanceof AnimatedFileDrawable) {
                        ((AnimatedFileDrawable) drawable).setRoundRadius(this.roundRadius);
                    } else if (bitmapDrawable.getBitmap() != null) {
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                        setDrawableShader(drawable, new BitmapShader(bitmap, tileMode, tileMode));
                    }
                }
            } else {
                setDrawableShader(drawable, (BitmapShader) null);
            }
        }
    }

    public void clearImage() {
        for (int i = 0; i < 4; i++) {
            recycleBitmap((String) null, i);
        }
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
    }

    public void onDetachedFromWindow() {
        this.attachedToWindow = false;
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
            this.roundPaint.setShader((Shader) null);
        }
        clearImage();
        if (this.isPressed == 0) {
            this.pressedProgress = 0.0f;
        }
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            animation.removeParent(this.parentView);
        }
    }

    private boolean setBackupImage() {
        SetImageBackup setImageBackup2 = this.setImageBackup;
        if (setImageBackup2 == null || !setImageBackup2.isSet()) {
            return false;
        }
        SetImageBackup setImageBackup3 = this.setImageBackup;
        this.setImageBackup = null;
        setImage(setImageBackup3.mediaLocation, setImageBackup3.mediaFilter, setImageBackup3.imageLocation, setImageBackup3.imageFilter, setImageBackup3.thumbLocation, setImageBackup3.thumbFilter, setImageBackup3.thumb, setImageBackup3.size, setImageBackup3.ext, setImageBackup3.parentObject, setImageBackup3.cacheType);
        setImageBackup3.clear();
        this.setImageBackup = setImageBackup3;
        RLottieDrawable lottieAnimation = getLottieAnimation();
        if (lottieAnimation == null || !this.allowStartLottieAnimation) {
            return true;
        }
        if (lottieAnimation.isHeavyDrawable() && this.currentOpenedLayerFlags != 0) {
            return true;
        }
        lottieAnimation.start();
        return true;
    }

    public boolean onAttachedToWindow() {
        View view;
        this.attachedToWindow = true;
        int currentHeavyOperationFlags = NotificationCenter.getGlobalInstance().getCurrentHeavyOperationFlags();
        this.currentOpenedLayerFlags = currentHeavyOperationFlags;
        this.currentOpenedLayerFlags = currentHeavyOperationFlags & (this.currentLayerNum ^ -1);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
        int i = NotificationCenter.stopAllHeavyOperations;
        globalInstance.addObserver(this, i);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.startAllHeavyOperations);
        if (setBackupImage()) {
            return true;
        }
        RLottieDrawable lottieAnimation = getLottieAnimation();
        if (lottieAnimation != null && this.allowStartLottieAnimation && (!lottieAnimation.isHeavyDrawable() || this.currentOpenedLayerFlags == 0)) {
            lottieAnimation.start();
        }
        AnimatedFileDrawable animation = getAnimation();
        if (!(animation == null || (view = this.parentView) == null)) {
            animation.addParent(view);
        }
        if (animation != null && this.allowStartAnimation && this.currentOpenedLayerFlags == 0) {
            animation.start();
            View view2 = this.parentView;
            if (view2 != null) {
                view2.invalidate();
            }
        }
        if (NotificationCenter.getGlobalInstance().isAnimationInProgress()) {
            didReceivedNotification(i, this.currentAccount, 512);
        }
        return false;
    }

    private void drawDrawable(Canvas canvas, Drawable drawable, int i, BitmapShader bitmapShader, int i2) {
        if (this.isPressed == 0) {
            float f = this.pressedProgress;
            if (f != 0.0f) {
                float f2 = f - 0.10666667f;
                this.pressedProgress = f2;
                if (f2 < 0.0f) {
                    this.pressedProgress = 0.0f;
                }
                View view = this.parentView;
                if (view != null) {
                    view.invalidate();
                }
            }
        }
        int i3 = this.isPressed;
        if (i3 != 0) {
            this.pressedProgress = 1.0f;
            this.animateFromIsPressed = i3;
        }
        float f3 = this.pressedProgress;
        if (f3 == 0.0f || f3 == 1.0f) {
            drawDrawable(canvas, drawable, i, bitmapShader, i2, i3);
            return;
        }
        drawDrawable(canvas, drawable, i, bitmapShader, i2, i3);
        drawDrawable(canvas, drawable, (int) (((float) i) * this.pressedProgress), bitmapShader, i2, this.animateFromIsPressed);
    }

    public void setUseRoundForThumbDrawable(boolean z) {
        this.useRoundForThumb = z;
    }

    private void drawDrawable(Canvas canvas, Drawable drawable, int i, BitmapShader bitmapShader, int i2, int i3) {
        Paint paint;
        int i4;
        int i5;
        Canvas canvas2 = canvas;
        Drawable drawable2 = drawable;
        int i6 = i;
        BitmapShader bitmapShader2 = bitmapShader;
        int i7 = i2;
        int i8 = i3;
        if (drawable2 instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable2;
            if (bitmapShader2 != null) {
                paint = this.roundPaint;
            } else {
                paint = bitmapDrawable.getPaint();
            }
            int i9 = Build.VERSION.SDK_INT;
            if (i9 >= 29) {
                Object obj = this.blendMode;
                if (obj == null || this.gradientShader != null) {
                    paint.setBlendMode((BlendMode) null);
                } else {
                    paint.setBlendMode((BlendMode) obj);
                }
            }
            boolean z = (paint == null || paint.getColorFilter() == null) ? false : true;
            if (!z || i8 != 0) {
                if (!z && i8 != 0) {
                    if (i8 == 1) {
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
            if (colorFilter2 != null && this.gradientShader == null) {
                if (bitmapShader2 != null) {
                    this.roundPaint.setColorFilter(colorFilter2);
                } else {
                    bitmapDrawable.setColorFilter(colorFilter2);
                }
            }
            boolean z2 = bitmapDrawable instanceof AnimatedFileDrawable;
            if (z2 || (bitmapDrawable instanceof RLottieDrawable)) {
                int i10 = i7 % 360;
                if (i10 == 90 || i10 == 270) {
                    i5 = bitmapDrawable.getIntrinsicHeight();
                    i4 = bitmapDrawable.getIntrinsicWidth();
                } else {
                    i5 = bitmapDrawable.getIntrinsicWidth();
                    i4 = bitmapDrawable.getIntrinsicHeight();
                }
            } else {
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap == null || !bitmap.isRecycled()) {
                    int i11 = i7 % 360;
                    if (i11 == 90 || i11 == 270) {
                        i5 = bitmap.getHeight();
                        i4 = bitmap.getWidth();
                    } else {
                        i5 = bitmap.getWidth();
                        i4 = bitmap.getHeight();
                    }
                } else {
                    return;
                }
            }
            float f = this.imageW;
            float f2 = this.sideClip;
            float f3 = f - (f2 * 2.0f);
            float f4 = this.imageH;
            float f5 = f4 - (f2 * 2.0f);
            float f6 = f == 0.0f ? 1.0f : ((float) i5) / f3;
            float f7 = f4 == 0.0f ? 1.0f : ((float) i4) / f5;
            if (bitmapShader2 == null) {
                BitmapDrawable bitmapDrawable2 = bitmapDrawable;
                int i12 = i6;
                Canvas canvas3 = canvas2;
                int i13 = i12;
                if (this.isAspectFit) {
                    float max = Math.max(f6, f7);
                    canvas.save();
                    RectF rectF = this.drawRegion;
                    float f8 = this.imageX;
                    float f9 = this.imageW;
                    float var_ = (float) ((int) (((float) i5) / max));
                    float var_ = this.imageY;
                    float var_ = this.imageH;
                    float var_ = (float) ((int) (((float) i4) / max));
                    rectF.set(((f9 - var_) / 2.0f) + f8, ((var_ - var_) / 2.0f) + var_, f8 + ((f9 + var_) / 2.0f), var_ + ((var_ + var_) / 2.0f));
                    RectF rectF2 = this.drawRegion;
                    bitmapDrawable2.setBounds((int) rectF2.left, (int) rectF2.top, (int) rectF2.right, (int) rectF2.bottom);
                    if (bitmapDrawable2 instanceof AnimatedFileDrawable) {
                        RectF rectF3 = this.drawRegion;
                        ((AnimatedFileDrawable) bitmapDrawable2).setActualDrawRect(rectF3.left, rectF3.top, rectF3.width(), this.drawRegion.height());
                    }
                    if (this.isVisible) {
                        try {
                            bitmapDrawable2.setAlpha(i13);
                            bitmapDrawable2.draw(canvas3);
                        } catch (Exception e) {
                            onBitmapException(bitmapDrawable2);
                            FileLog.e((Throwable) e);
                        }
                    }
                    canvas.restore();
                } else if (Math.abs(f6 - f7) > 1.0E-5f) {
                    canvas.save();
                    float var_ = this.imageX;
                    float var_ = this.imageY;
                    canvas3.clipRect(var_, var_, this.imageW + var_, this.imageH + var_);
                    int i14 = i7 % 360;
                    if (i14 != 0) {
                        if (this.centerRotation) {
                            canvas3.rotate((float) i7, this.imageW / 2.0f, this.imageH / 2.0f);
                        } else {
                            canvas3.rotate((float) i7, 0.0f, 0.0f);
                        }
                    }
                    float var_ = ((float) i5) / f7;
                    float var_ = this.imageW;
                    if (var_ > var_) {
                        RectF rectF4 = this.drawRegion;
                        float var_ = this.imageX;
                        float var_ = (float) ((int) var_);
                        float var_ = this.imageY;
                        rectF4.set(var_ - ((var_ - var_) / 2.0f), var_, var_ + ((var_ + var_) / 2.0f), this.imageH + var_);
                    } else {
                        RectF rectF5 = this.drawRegion;
                        float var_ = this.imageX;
                        float var_ = this.imageY;
                        float var_ = (float) ((int) (((float) i4) / f6));
                        float var_ = this.imageH;
                        rectF5.set(var_, var_ - ((var_ - var_) / 2.0f), var_ + var_, var_ + ((var_ + var_) / 2.0f));
                    }
                    if (z2) {
                        ((AnimatedFileDrawable) bitmapDrawable2).setActualDrawRect(this.imageX, this.imageY, this.imageW, this.imageH);
                    }
                    if (i14 == 90 || i14 == 270) {
                        float width = this.drawRegion.width() / 2.0f;
                        float height = this.drawRegion.height() / 2.0f;
                        float centerX = this.drawRegion.centerX();
                        float centerY = this.drawRegion.centerY();
                        bitmapDrawable2.setBounds((int) (centerX - height), (int) (centerY - width), (int) (centerX + height), (int) (centerY + width));
                    } else {
                        RectF rectF6 = this.drawRegion;
                        bitmapDrawable2.setBounds((int) rectF6.left, (int) rectF6.top, (int) rectF6.right, (int) rectF6.bottom);
                    }
                    if (this.isVisible) {
                        if (i9 >= 29) {
                            try {
                                if (this.blendMode != null) {
                                    bitmapDrawable2.getPaint().setBlendMode((BlendMode) this.blendMode);
                                } else {
                                    bitmapDrawable2.getPaint().setBlendMode((BlendMode) null);
                                }
                            } catch (Exception e2) {
                                onBitmapException(bitmapDrawable2);
                                FileLog.e((Throwable) e2);
                            }
                        }
                        bitmapDrawable2.setAlpha(i13);
                        bitmapDrawable2.draw(canvas3);
                    }
                    canvas.restore();
                } else {
                    canvas.save();
                    int i15 = i7 % 360;
                    if (i15 != 0) {
                        if (this.centerRotation) {
                            canvas3.rotate((float) i7, this.imageW / 2.0f, this.imageH / 2.0f);
                        } else {
                            canvas3.rotate((float) i7, 0.0f, 0.0f);
                        }
                    }
                    RectF rectF7 = this.drawRegion;
                    float var_ = this.imageX;
                    float var_ = this.imageY;
                    rectF7.set(var_, var_, this.imageW + var_, this.imageH + var_);
                    if (this.isRoundVideo) {
                        RectF rectF8 = this.drawRegion;
                        int i16 = AndroidUtilities.roundMessageInset;
                        rectF8.inset((float) (-i16), (float) (-i16));
                    }
                    if (z2) {
                        ((AnimatedFileDrawable) bitmapDrawable2).setActualDrawRect(this.imageX, this.imageY, this.imageW, this.imageH);
                    }
                    if (i15 == 90 || i15 == 270) {
                        float width2 = this.drawRegion.width() / 2.0f;
                        float height2 = this.drawRegion.height() / 2.0f;
                        float centerX2 = this.drawRegion.centerX();
                        float centerY2 = this.drawRegion.centerY();
                        bitmapDrawable2.setBounds((int) (centerX2 - height2), (int) (centerY2 - width2), (int) (centerX2 + height2), (int) (centerY2 + width2));
                    } else {
                        RectF rectF9 = this.drawRegion;
                        bitmapDrawable2.setBounds((int) rectF9.left, (int) rectF9.top, (int) rectF9.right, (int) rectF9.bottom);
                    }
                    if (this.isVisible) {
                        if (i9 >= 29) {
                            try {
                                if (this.blendMode != null) {
                                    bitmapDrawable2.getPaint().setBlendMode((BlendMode) this.blendMode);
                                } else {
                                    bitmapDrawable2.getPaint().setBlendMode((BlendMode) null);
                                }
                            } catch (Exception e3) {
                                onBitmapException(bitmapDrawable2);
                                FileLog.e((Throwable) e3);
                            }
                        }
                        bitmapDrawable2.setAlpha(i13);
                        bitmapDrawable2.draw(canvas3);
                    }
                    canvas.restore();
                }
            } else if (this.isAspectFit) {
                float max2 = Math.max(f6, f7);
                RectF rectvar_ = this.drawRegion;
                float var_ = this.imageX;
                float var_ = this.imageW;
                float var_ = (float) ((int) (((float) i5) / max2));
                float var_ = this.imageY;
                float var_ = this.imageH;
                float var_ = (float) ((int) (((float) i4) / max2));
                rectvar_.set(((var_ - var_) / 2.0f) + var_, var_ + ((var_ - var_) / 2.0f), var_ + ((var_ + var_) / 2.0f), var_ + ((var_ + var_) / 2.0f));
                if (this.isVisible) {
                    this.roundPaint.setShader(bitmapShader2);
                    this.shaderMatrix.reset();
                    Matrix matrix = this.shaderMatrix;
                    RectF rectvar_ = this.drawRegion;
                    matrix.setTranslate(rectvar_.left, rectvar_.top);
                    float var_ = 1.0f / max2;
                    this.shaderMatrix.preScale(var_, var_);
                    bitmapShader2.setLocalMatrix(this.shaderMatrix);
                    this.roundPaint.setAlpha(i6);
                    this.roundRect.set(this.drawRegion);
                    if (this.isRoundRect) {
                        try {
                            int[] iArr = this.roundRadius;
                            if (iArr[0] == 0) {
                                canvas2.drawRect(this.roundRect, this.roundPaint);
                            } else {
                                canvas2.drawRoundRect(this.roundRect, (float) iArr[0], (float) iArr[0], this.roundPaint);
                            }
                        } catch (Exception e4) {
                            onBitmapException(bitmapDrawable);
                            FileLog.e((Throwable) e4);
                        }
                    } else {
                        int i17 = 0;
                        while (true) {
                            int[] iArr2 = this.roundRadius;
                            if (i17 < iArr2.length) {
                                float[] fArr = radii;
                                int i18 = i17 * 2;
                                fArr[i18] = (float) iArr2[i17];
                                fArr[i18 + 1] = (float) iArr2[i17];
                                i17++;
                            } else {
                                this.roundPath.reset();
                                this.roundPath.addRoundRect(this.roundRect, radii, Path.Direction.CW);
                                this.roundPath.close();
                                canvas2.drawPath(this.roundPath, this.roundPaint);
                                return;
                            }
                        }
                    }
                }
            } else {
                if (this.legacyCanvas != null) {
                    this.roundRect.set(0.0f, 0.0f, (float) this.legacyBitmap.getWidth(), (float) this.legacyBitmap.getHeight());
                    this.legacyCanvas.drawBitmap(this.gradientBitmap, (Rect) null, this.roundRect, (Paint) null);
                    this.legacyCanvas.drawBitmap(bitmapDrawable.getBitmap(), (Rect) null, this.roundRect, this.legacyPaint);
                }
                if (bitmapShader2 != this.imageShader || this.gradientShader == null) {
                    this.roundPaint.setShader(bitmapShader2);
                } else {
                    ComposeShader composeShader2 = this.composeShader;
                    if (composeShader2 != null) {
                        this.roundPaint.setShader(composeShader2);
                    } else {
                        this.roundPaint.setShader(this.legacyShader);
                    }
                }
                float min = 1.0f / Math.min(f6, f7);
                RectF rectvar_ = this.roundRect;
                float var_ = this.imageX;
                float var_ = this.sideClip;
                BitmapDrawable bitmapDrawable3 = bitmapDrawable;
                float var_ = this.imageY;
                rectvar_.set(var_ + var_, var_ + var_, (var_ + this.imageW) - var_, (var_ + this.imageH) - var_);
                if (Math.abs(f6 - f7) > 5.0E-4f) {
                    float var_ = ((float) i5) / f7;
                    if (var_ > f3) {
                        RectF rectvar_ = this.drawRegion;
                        float var_ = this.imageX;
                        float var_ = (float) ((int) var_);
                        float var_ = this.imageY;
                        rectvar_.set(var_ - ((var_ - f3) / 2.0f), var_, var_ + ((var_ + f3) / 2.0f), var_ + f5);
                    } else {
                        RectF rectvar_ = this.drawRegion;
                        float var_ = this.imageX;
                        float var_ = this.imageY;
                        float var_ = (float) ((int) (((float) i4) / f6));
                        rectvar_.set(var_, var_ - ((var_ - f5) / 2.0f), var_ + f3, var_ + ((var_ + f5) / 2.0f));
                    }
                } else {
                    RectF rectvar_ = this.drawRegion;
                    float var_ = this.imageX;
                    float var_ = this.imageY;
                    rectvar_.set(var_, var_, var_ + f3, var_ + f5);
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
                        float var_ = (f3 + ((float) (AndroidUtilities.roundMessageInset * 2))) / f3;
                        this.shaderMatrix.postScale(var_, var_, this.drawRegion.centerX(), this.drawRegion.centerY());
                    }
                    BitmapShader bitmapShader3 = this.legacyShader;
                    if (bitmapShader3 != null) {
                        bitmapShader3.setLocalMatrix(this.shaderMatrix);
                    }
                    bitmapShader2.setLocalMatrix(this.shaderMatrix);
                    if (this.composeShader != null) {
                        int width3 = this.gradientBitmap.getWidth();
                        int height3 = this.gradientBitmap.getHeight();
                        float var_ = this.imageW == 0.0f ? 1.0f : ((float) width3) / f3;
                        float var_ = this.imageH == 0.0f ? 1.0f : ((float) height3) / f5;
                        if (Math.abs(var_ - var_) > 5.0E-4f) {
                            float var_ = ((float) width3) / var_;
                            if (var_ > f3) {
                                width3 = (int) var_;
                                RectF rectvar_ = this.drawRegion;
                                float var_ = this.imageX;
                                float var_ = (float) width3;
                                float var_ = this.imageY;
                                rectvar_.set(var_ - ((var_ - f3) / 2.0f), var_, var_ + ((var_ + f3) / 2.0f), var_ + f5);
                            } else {
                                height3 = (int) (((float) height3) / var_);
                                RectF rectvar_ = this.drawRegion;
                                float var_ = this.imageX;
                                float var_ = this.imageY;
                                float var_ = (float) height3;
                                rectvar_.set(var_, var_ - ((var_ - f5) / 2.0f), var_ + f3, var_ + ((var_ + f5) / 2.0f));
                            }
                        } else {
                            RectF rectvar_ = this.drawRegion;
                            float var_ = this.imageX;
                            float var_ = this.imageY;
                            rectvar_.set(var_, var_, var_ + f3, var_ + f5);
                        }
                        float min2 = 1.0f / Math.min(this.imageW == 0.0f ? 1.0f : ((float) width3) / f3, this.imageH == 0.0f ? 1.0f : ((float) height3) / f5);
                        this.shaderMatrix.reset();
                        Matrix matrix3 = this.shaderMatrix;
                        RectF rectvar_ = this.drawRegion;
                        float var_ = rectvar_.left;
                        float var_ = this.sideClip;
                        matrix3.setTranslate(var_ + var_, rectvar_.top + var_);
                        this.shaderMatrix.preScale(min2, min2);
                        this.gradientShader.setLocalMatrix(this.shaderMatrix);
                    }
                    this.roundPaint.setAlpha(i);
                    if (this.isRoundRect) {
                        try {
                            int[] iArr3 = this.roundRadius;
                            if (iArr3[0] == 0) {
                                canvas.drawRect(this.roundRect, this.roundPaint);
                            } else {
                                canvas.drawRoundRect(this.roundRect, (float) iArr3[0], (float) iArr3[0], this.roundPaint);
                            }
                        } catch (Exception e5) {
                            onBitmapException(bitmapDrawable3);
                            FileLog.e((Throwable) e5);
                        }
                    } else {
                        Canvas canvas4 = canvas;
                        int i19 = 0;
                        while (true) {
                            int[] iArr4 = this.roundRadius;
                            if (i19 < iArr4.length) {
                                float[] fArr2 = radii;
                                int i20 = i19 * 2;
                                fArr2[i20] = (float) iArr4[i19];
                                fArr2[i20 + 1] = (float) iArr4[i19];
                                i19++;
                            } else {
                                this.roundPath.reset();
                                this.roundPath.addRoundRect(this.roundRect, radii, Path.Direction.CW);
                                this.roundPath.close();
                                canvas4.drawPath(this.roundPath, this.roundPaint);
                                return;
                            }
                        }
                    }
                }
            }
        } else {
            int i21 = i6;
            Canvas canvas5 = canvas2;
            int i22 = i21;
            if (this.isAspectFit) {
                int intrinsicWidth = drawable.getIntrinsicWidth();
                int intrinsicHeight = drawable.getIntrinsicHeight();
                float var_ = this.imageW;
                float var_ = this.sideClip;
                float var_ = var_ - (var_ * 2.0f);
                float var_ = this.imageH;
                float max3 = Math.max(var_ == 0.0f ? 1.0f : ((float) intrinsicWidth) / var_, var_ == 0.0f ? 1.0f : ((float) intrinsicHeight) / (var_ - (var_ * 2.0f)));
                RectF rectvar_ = this.drawRegion;
                float var_ = this.imageX;
                float var_ = this.imageW;
                float var_ = (float) ((int) (((float) intrinsicWidth) / max3));
                float var_ = this.imageY;
                float var_ = this.imageH;
                float var_ = (float) ((int) (((float) intrinsicHeight) / max3));
                rectvar_.set(((var_ - var_) / 2.0f) + var_, ((var_ - var_) / 2.0f) + var_, var_ + ((var_ + var_) / 2.0f), var_ + ((var_ + var_) / 2.0f));
            } else {
                RectF rectvar_ = this.drawRegion;
                float var_ = this.imageX;
                float var_ = this.imageY;
                rectvar_.set(var_, var_, this.imageW + var_, this.imageH + var_);
            }
            RectF rectvar_ = this.drawRegion;
            drawable2.setBounds((int) rectvar_.left, (int) rectvar_.top, (int) rectvar_.right, (int) rectvar_.bottom);
            if (this.isVisible) {
                try {
                    drawable.setAlpha(i);
                    drawable2.draw(canvas5);
                } catch (Exception e6) {
                    FileLog.e((Throwable) e6);
                }
            }
        }
    }

    public void setBlendMode(Object obj) {
        this.blendMode = obj;
        invalidate();
    }

    public void setGradientBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            if (this.gradientShader == null || this.gradientBitmap != bitmap) {
                Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                this.gradientShader = new BitmapShader(bitmap, tileMode, tileMode);
                updateDrawableRadius(this.currentImageDrawable);
            }
            this.isRoundRect = true;
        } else {
            this.gradientShader = null;
            this.composeShader = null;
            this.legacyShader = null;
            this.legacyCanvas = null;
            Bitmap bitmap2 = this.legacyBitmap;
            if (bitmap2 != null) {
                bitmap2.recycle();
                this.legacyBitmap = null;
            }
        }
        this.gradientBitmap = bitmap;
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
                float f = this.currentAlpha + (((float) currentTimeMillis) / ((float) this.crossfadeDuration));
                this.currentAlpha = f;
                if (f > 1.0f) {
                    this.currentAlpha = 1.0f;
                    this.previousAlpha = 1.0f;
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
            float f2 = this.imageX;
            float f3 = this.imageY;
            view.invalidate((int) f2, (int) f3, (int) (f2 + this.imageW), (int) (f3 + this.imageH));
        }
    }

    public void skipDraw() {
        RLottieDrawable lottieAnimation = getLottieAnimation();
        if (lottieAnimation != null) {
            lottieAnimation.setCurrentParentView(this.parentView);
            lottieAnimation.updateCurrentFrame();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:122:0x0160 A[Catch:{ Exception -> 0x01f3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x01c9 A[Catch:{ Exception -> 0x01f3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x01ed A[Catch:{ Exception -> 0x01f1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00bb A[Catch:{ Exception -> 0x01f3 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean draw(android.graphics.Canvas r18) {
        /*
            r17 = this;
            r7 = r17
            r8 = r18
            android.graphics.Bitmap r0 = r7.gradientBitmap
            if (r0 == 0) goto L_0x0021
            java.lang.String r0 = r7.currentImageKey
            if (r0 == 0) goto L_0x0021
            r18.save()
            float r0 = r7.imageX
            float r1 = r7.imageY
            float r2 = r7.imageW
            float r2 = r2 + r0
            float r3 = r7.imageH
            float r3 = r3 + r1
            r8.clipRect(r0, r1, r2, r3)
            r0 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r8.drawColor(r0)
        L_0x0021:
            org.telegram.ui.Components.AnimatedFileDrawable r0 = r17.getAnimation()     // Catch:{ Exception -> 0x01f3 }
            org.telegram.ui.Components.RLottieDrawable r1 = r17.getLottieAnimation()     // Catch:{ Exception -> 0x01f3 }
            r10 = 1
            if (r0 == 0) goto L_0x0032
            boolean r2 = r0.hasBitmap()     // Catch:{ Exception -> 0x01f3 }
            if (r2 == 0) goto L_0x003a
        L_0x0032:
            if (r1 == 0) goto L_0x003c
            boolean r2 = r1.hasBitmap()     // Catch:{ Exception -> 0x01f3 }
            if (r2 != 0) goto L_0x003c
        L_0x003a:
            r2 = 1
            goto L_0x003d
        L_0x003c:
            r2 = 0
        L_0x003d:
            if (r0 == 0) goto L_0x0044
            int[] r3 = r7.roundRadius     // Catch:{ Exception -> 0x01f3 }
            r0.setRoundRadius(r3)     // Catch:{ Exception -> 0x01f3 }
        L_0x0044:
            if (r1 == 0) goto L_0x004b
            android.view.View r3 = r7.parentView     // Catch:{ Exception -> 0x01f3 }
            r1.setCurrentParentView(r3)     // Catch:{ Exception -> 0x01f3 }
        L_0x004b:
            if (r0 != 0) goto L_0x004f
            if (r1 == 0) goto L_0x005e
        L_0x004f:
            if (r2 != 0) goto L_0x005e
            boolean r0 = r7.animationReadySent     // Catch:{ Exception -> 0x01f3 }
            if (r0 != 0) goto L_0x005e
            r7.animationReadySent = r10     // Catch:{ Exception -> 0x01f3 }
            org.telegram.messenger.ImageReceiver$ImageReceiverDelegate r0 = r7.delegate     // Catch:{ Exception -> 0x01f3 }
            if (r0 == 0) goto L_0x005e
            r0.onAnimationReady(r7)     // Catch:{ Exception -> 0x01f3 }
        L_0x005e:
            boolean r0 = r7.forcePreview     // Catch:{ Exception -> 0x01f3 }
            r11 = 0
            if (r0 != 0) goto L_0x0072
            android.graphics.drawable.Drawable r1 = r7.currentMediaDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r1 == 0) goto L_0x0072
            if (r2 != 0) goto L_0x0072
            android.graphics.BitmapShader r0 = r7.mediaShader     // Catch:{ Exception -> 0x01f3 }
            int r3 = r7.imageOrientation     // Catch:{ Exception -> 0x01f3 }
        L_0x006d:
            r13 = r0
            r0 = r1
            r12 = r2
            r14 = r3
            goto L_0x00b7
        L_0x0072:
            if (r0 != 0) goto L_0x0087
            android.graphics.drawable.Drawable r1 = r7.currentImageDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r1 == 0) goto L_0x0087
            if (r2 == 0) goto L_0x007e
            android.graphics.drawable.Drawable r0 = r7.currentMediaDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r0 == 0) goto L_0x0087
        L_0x007e:
            android.graphics.BitmapShader r0 = r7.imageShader     // Catch:{ Exception -> 0x01f3 }
            int r2 = r7.imageOrientation     // Catch:{ Exception -> 0x01f3 }
            r13 = r0
            r0 = r1
            r14 = r2
            r12 = 0
            goto L_0x00b7
        L_0x0087:
            android.graphics.drawable.Drawable r1 = r7.crossfadeImage     // Catch:{ Exception -> 0x01f3 }
            if (r1 == 0) goto L_0x0094
            boolean r0 = r7.crossfadingWithThumb     // Catch:{ Exception -> 0x01f3 }
            if (r0 != 0) goto L_0x0094
            android.graphics.BitmapShader r0 = r7.crossfadeShader     // Catch:{ Exception -> 0x01f3 }
            int r3 = r7.imageOrientation     // Catch:{ Exception -> 0x01f3 }
            goto L_0x006d
        L_0x0094:
            android.graphics.drawable.Drawable r1 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01f3 }
            boolean r0 = r1 instanceof android.graphics.drawable.BitmapDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r0 == 0) goto L_0x00aa
            boolean r0 = r7.useRoundForThumb     // Catch:{ Exception -> 0x01f3 }
            if (r0 == 0) goto L_0x00a5
            android.graphics.BitmapShader r0 = r7.thumbShader     // Catch:{ Exception -> 0x01f3 }
            if (r0 != 0) goto L_0x00a5
            r7.updateDrawableRadius(r1)     // Catch:{ Exception -> 0x01f3 }
        L_0x00a5:
            android.graphics.BitmapShader r0 = r7.thumbShader     // Catch:{ Exception -> 0x01f3 }
            int r3 = r7.thumbOrientation     // Catch:{ Exception -> 0x01f3 }
            goto L_0x006d
        L_0x00aa:
            android.graphics.drawable.Drawable r1 = r7.currentThumbDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r1 == 0) goto L_0x00b3
            android.graphics.BitmapShader r0 = r7.thumbShader     // Catch:{ Exception -> 0x01f3 }
            int r3 = r7.thumbOrientation     // Catch:{ Exception -> 0x01f3 }
            goto L_0x006d
        L_0x00b3:
            r12 = r2
            r0 = r11
            r13 = r0
            r14 = 0
        L_0x00b7:
            r15 = 1132396544(0x437var_, float:255.0)
            if (r0 == 0) goto L_0x01c9
            byte r1 = r7.crossfadeAlpha     // Catch:{ Exception -> 0x01f3 }
            if (r1 == 0) goto L_0x01ad
            float r1 = r7.previousAlpha     // Catch:{ Exception -> 0x01f3 }
            r16 = 1065353216(0x3var_, float:1.0)
            int r1 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x00ef
            android.graphics.drawable.Drawable r1 = r7.currentImageDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r0 == r1) goto L_0x00cf
            android.graphics.drawable.Drawable r1 = r7.currentMediaDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r0 != r1) goto L_0x00ef
        L_0x00cf:
            android.graphics.drawable.Drawable r1 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r1 == 0) goto L_0x00ef
            boolean r2 = r7.useRoundForThumb     // Catch:{ Exception -> 0x01f3 }
            if (r2 == 0) goto L_0x00de
            android.graphics.BitmapShader r2 = r7.thumbShader     // Catch:{ Exception -> 0x01f3 }
            if (r2 != 0) goto L_0x00de
            r7.updateDrawableRadius(r1)     // Catch:{ Exception -> 0x01f3 }
        L_0x00de:
            android.graphics.drawable.Drawable r3 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01f3 }
            float r1 = r7.overrideAlpha     // Catch:{ Exception -> 0x01f3 }
            float r1 = r1 * r15
            int r4 = (int) r1     // Catch:{ Exception -> 0x01f3 }
            android.graphics.BitmapShader r5 = r7.thumbShader     // Catch:{ Exception -> 0x01f3 }
            r1 = r17
            r2 = r18
            r6 = r14
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x01f3 }
        L_0x00ef:
            boolean r1 = r7.crossfadeWithThumb     // Catch:{ Exception -> 0x01f3 }
            if (r1 == 0) goto L_0x0106
            if (r12 == 0) goto L_0x0106
            float r1 = r7.overrideAlpha     // Catch:{ Exception -> 0x01f3 }
            float r1 = r1 * r15
            int r4 = (int) r1     // Catch:{ Exception -> 0x01f3 }
            r1 = r17
            r2 = r18
            r3 = r0
            r5 = r13
            r6 = r14
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x01f3 }
            goto L_0x01bc
        L_0x0106:
            if (r1 == 0) goto L_0x0199
            float r1 = r7.currentAlpha     // Catch:{ Exception -> 0x01f3 }
            int r1 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x0199
            android.graphics.drawable.Drawable r1 = r7.currentImageDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r0 == r1) goto L_0x013b
            android.graphics.drawable.Drawable r1 = r7.currentMediaDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r0 != r1) goto L_0x0117
            goto L_0x013b
        L_0x0117:
            android.graphics.drawable.Drawable r1 = r7.currentThumbDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r0 == r1) goto L_0x0129
            android.graphics.drawable.Drawable r1 = r7.crossfadeImage     // Catch:{ Exception -> 0x01f3 }
            if (r0 != r1) goto L_0x0120
            goto L_0x0129
        L_0x0120:
            android.graphics.drawable.Drawable r2 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r0 != r2) goto L_0x015d
            if (r1 == 0) goto L_0x015d
            android.graphics.BitmapShader r2 = r7.crossfadeShader     // Catch:{ Exception -> 0x01f3 }
            goto L_0x0141
        L_0x0129:
            android.graphics.drawable.Drawable r1 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r1 == 0) goto L_0x015d
            boolean r2 = r7.useRoundForThumb     // Catch:{ Exception -> 0x01f3 }
            if (r2 == 0) goto L_0x0138
            android.graphics.BitmapShader r2 = r7.thumbShader     // Catch:{ Exception -> 0x01f3 }
            if (r2 != 0) goto L_0x0138
            r7.updateDrawableRadius(r1)     // Catch:{ Exception -> 0x01f3 }
        L_0x0138:
            android.graphics.BitmapShader r2 = r7.thumbShader     // Catch:{ Exception -> 0x01f3 }
            goto L_0x0141
        L_0x013b:
            android.graphics.drawable.Drawable r1 = r7.crossfadeImage     // Catch:{ Exception -> 0x01f3 }
            if (r1 == 0) goto L_0x0144
            android.graphics.BitmapShader r2 = r7.crossfadeShader     // Catch:{ Exception -> 0x01f3 }
        L_0x0141:
            r11 = r1
            r5 = r2
            goto L_0x015e
        L_0x0144:
            android.graphics.drawable.Drawable r1 = r7.currentThumbDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r1 == 0) goto L_0x014b
            android.graphics.BitmapShader r2 = r7.thumbShader     // Catch:{ Exception -> 0x01f3 }
            goto L_0x0141
        L_0x014b:
            android.graphics.drawable.Drawable r1 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r1 == 0) goto L_0x015d
            boolean r2 = r7.useRoundForThumb     // Catch:{ Exception -> 0x01f3 }
            if (r2 == 0) goto L_0x015a
            android.graphics.BitmapShader r2 = r7.thumbShader     // Catch:{ Exception -> 0x01f3 }
            if (r2 != 0) goto L_0x015a
            r7.updateDrawableRadius(r1)     // Catch:{ Exception -> 0x01f3 }
        L_0x015a:
            android.graphics.BitmapShader r2 = r7.thumbShader     // Catch:{ Exception -> 0x01f3 }
            goto L_0x0141
        L_0x015d:
            r5 = r11
        L_0x015e:
            if (r11 == 0) goto L_0x0199
            boolean r1 = r11 instanceof org.telegram.messenger.SvgHelper.SvgDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r1 != 0) goto L_0x0172
            boolean r1 = r11 instanceof org.telegram.messenger.Emoji.EmojiDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r1 == 0) goto L_0x0169
            goto L_0x0172
        L_0x0169:
            float r1 = r7.overrideAlpha     // Catch:{ Exception -> 0x01f3 }
            float r2 = r7.previousAlpha     // Catch:{ Exception -> 0x01f3 }
            float r1 = r1 * r2
            float r1 = r1 * r15
            goto L_0x017c
        L_0x0172:
            float r1 = r7.overrideAlpha     // Catch:{ Exception -> 0x01f3 }
            float r1 = r1 * r15
            float r2 = r7.currentAlpha     // Catch:{ Exception -> 0x01f3 }
            float r16 = r16 - r2
            float r1 = r1 * r16
        L_0x017c:
            int r1 = (int) r1     // Catch:{ Exception -> 0x01f3 }
            r6 = r1
            int r4 = r7.thumbOrientation     // Catch:{ Exception -> 0x01f3 }
            r1 = r17
            r2 = r18
            r3 = r11
            r16 = r4
            r4 = r6
            r9 = r6
            r6 = r16
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x01f3 }
            r1 = 255(0xff, float:3.57E-43)
            if (r9 == r1) goto L_0x0199
            boolean r2 = r11 instanceof org.telegram.messenger.Emoji.EmojiDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r2 == 0) goto L_0x0199
            r11.setAlpha(r1)     // Catch:{ Exception -> 0x01f3 }
        L_0x0199:
            float r1 = r7.overrideAlpha     // Catch:{ Exception -> 0x01f3 }
            float r2 = r7.currentAlpha     // Catch:{ Exception -> 0x01f3 }
            float r1 = r1 * r2
            float r1 = r1 * r15
            int r4 = (int) r1     // Catch:{ Exception -> 0x01f3 }
            r1 = r17
            r2 = r18
            r3 = r0
            r5 = r13
            r6 = r14
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x01f3 }
            goto L_0x01bc
        L_0x01ad:
            float r1 = r7.overrideAlpha     // Catch:{ Exception -> 0x01f3 }
            float r1 = r1 * r15
            int r4 = (int) r1     // Catch:{ Exception -> 0x01f3 }
            r1 = r17
            r2 = r18
            r3 = r0
            r5 = r13
            r6 = r14
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x01f3 }
        L_0x01bc:
            if (r12 == 0) goto L_0x01c4
            boolean r1 = r7.crossfadeWithThumb     // Catch:{ Exception -> 0x01f3 }
            if (r1 == 0) goto L_0x01c4
            r1 = 1
            goto L_0x01c5
        L_0x01c4:
            r1 = 0
        L_0x01c5:
            r7.checkAlphaAnimation(r1)     // Catch:{ Exception -> 0x01f3 }
            goto L_0x01df
        L_0x01c9:
            android.graphics.drawable.Drawable r3 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01f3 }
            if (r3 == 0) goto L_0x01e1
            float r1 = r7.overrideAlpha     // Catch:{ Exception -> 0x01f3 }
            float r1 = r1 * r15
            int r4 = (int) r1     // Catch:{ Exception -> 0x01f3 }
            r5 = 0
            int r6 = r7.thumbOrientation     // Catch:{ Exception -> 0x01f3 }
            r1 = r17
            r2 = r18
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x01f3 }
            r7.checkAlphaAnimation(r12)     // Catch:{ Exception -> 0x01f3 }
        L_0x01df:
            r9 = 1
            goto L_0x01e5
        L_0x01e1:
            r7.checkAlphaAnimation(r12)     // Catch:{ Exception -> 0x01f3 }
            r9 = 0
        L_0x01e5:
            if (r0 != 0) goto L_0x01f8
            if (r12 == 0) goto L_0x01f8
            android.view.View r0 = r7.parentView     // Catch:{ Exception -> 0x01f1 }
            if (r0 == 0) goto L_0x01f8
            r0.invalidate()     // Catch:{ Exception -> 0x01f1 }
            goto L_0x01f8
        L_0x01f1:
            r0 = move-exception
            goto L_0x01f5
        L_0x01f3:
            r0 = move-exception
            r9 = 0
        L_0x01f5:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01f8:
            android.graphics.Bitmap r0 = r7.gradientBitmap
            if (r0 == 0) goto L_0x0203
            java.lang.String r0 = r7.currentImageKey
            if (r0 == 0) goto L_0x0203
            r18.restore()
        L_0x0203:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageReceiver.draw(android.graphics.Canvas):boolean");
    }

    public void setManualAlphaAnimator(boolean z) {
        this.manualAlphaAnimator = z;
    }

    @Keep
    public float getCurrentAlpha() {
        return this.currentAlpha;
    }

    @Keep
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
        if ((drawable2 instanceof BitmapDrawable) && !(drawable2 instanceof AnimatedFileDrawable) && !(drawable instanceof RLottieDrawable)) {
            return ((BitmapDrawable) drawable2).getBitmap();
        }
        Drawable drawable3 = this.currentThumbDrawable;
        if ((drawable3 instanceof BitmapDrawable) && !(drawable3 instanceof AnimatedFileDrawable) && !(drawable instanceof RLottieDrawable)) {
            return ((BitmapDrawable) drawable3).getBitmap();
        }
        Drawable drawable4 = this.staticThumbDrawable;
        if (drawable4 instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable4).getBitmap();
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:42:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.telegram.messenger.ImageReceiver.BitmapHolder getBitmapSafe() {
        /*
            r5 = this;
            org.telegram.ui.Components.AnimatedFileDrawable r0 = r5.getAnimation()
            org.telegram.ui.Components.RLottieDrawable r1 = r5.getLottieAnimation()
            r2 = 0
            r3 = 0
            if (r1 == 0) goto L_0x0019
            boolean r4 = r1.hasBitmap()
            if (r4 == 0) goto L_0x0019
            android.graphics.Bitmap r0 = r1.getAnimatedBitmap()
        L_0x0016:
            r1 = r2
            goto L_0x008b
        L_0x0019:
            if (r0 == 0) goto L_0x0037
            boolean r1 = r0.hasBitmap()
            if (r1 == 0) goto L_0x0037
            android.graphics.Bitmap r1 = r0.getAnimatedBitmap()
            int r3 = r0.getOrientation()
            if (r3 == 0) goto L_0x0035
            org.telegram.messenger.ImageReceiver$BitmapHolder r0 = new org.telegram.messenger.ImageReceiver$BitmapHolder
            android.graphics.Bitmap r1 = android.graphics.Bitmap.createBitmap(r1)
            r0.<init>((android.graphics.Bitmap) r1, (java.lang.String) r2, (int) r3)
            return r0
        L_0x0035:
            r0 = r1
            goto L_0x0016
        L_0x0037:
            android.graphics.drawable.Drawable r0 = r5.currentMediaDrawable
            boolean r1 = r0 instanceof android.graphics.drawable.BitmapDrawable
            if (r1 == 0) goto L_0x004e
            boolean r1 = r0 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r1 != 0) goto L_0x004e
            boolean r1 = r0 instanceof org.telegram.ui.Components.RLottieDrawable
            if (r1 != 0) goto L_0x004e
            android.graphics.drawable.BitmapDrawable r0 = (android.graphics.drawable.BitmapDrawable) r0
            android.graphics.Bitmap r0 = r0.getBitmap()
            java.lang.String r1 = r5.currentMediaKey
            goto L_0x008b
        L_0x004e:
            android.graphics.drawable.Drawable r1 = r5.currentImageDrawable
            boolean r4 = r1 instanceof android.graphics.drawable.BitmapDrawable
            if (r4 == 0) goto L_0x0065
            boolean r4 = r1 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r4 != 0) goto L_0x0065
            boolean r4 = r0 instanceof org.telegram.ui.Components.RLottieDrawable
            if (r4 != 0) goto L_0x0065
            android.graphics.drawable.BitmapDrawable r1 = (android.graphics.drawable.BitmapDrawable) r1
            android.graphics.Bitmap r0 = r1.getBitmap()
            java.lang.String r1 = r5.currentImageKey
            goto L_0x008b
        L_0x0065:
            android.graphics.drawable.Drawable r1 = r5.currentThumbDrawable
            boolean r4 = r1 instanceof android.graphics.drawable.BitmapDrawable
            if (r4 == 0) goto L_0x007c
            boolean r4 = r1 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r4 != 0) goto L_0x007c
            boolean r0 = r0 instanceof org.telegram.ui.Components.RLottieDrawable
            if (r0 != 0) goto L_0x007c
            android.graphics.drawable.BitmapDrawable r1 = (android.graphics.drawable.BitmapDrawable) r1
            android.graphics.Bitmap r0 = r1.getBitmap()
            java.lang.String r1 = r5.currentThumbKey
            goto L_0x008b
        L_0x007c:
            android.graphics.drawable.Drawable r0 = r5.staticThumbDrawable
            boolean r1 = r0 instanceof android.graphics.drawable.BitmapDrawable
            if (r1 == 0) goto L_0x0089
            android.graphics.drawable.BitmapDrawable r0 = (android.graphics.drawable.BitmapDrawable) r0
            android.graphics.Bitmap r0 = r0.getBitmap()
            goto L_0x0016
        L_0x0089:
            r0 = r2
            r1 = r0
        L_0x008b:
            if (r0 == 0) goto L_0x0092
            org.telegram.messenger.ImageReceiver$BitmapHolder r2 = new org.telegram.messenger.ImageReceiver$BitmapHolder
            r2.<init>((android.graphics.Bitmap) r0, (java.lang.String) r1, (int) r3)
        L_0x0092:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageReceiver.getBitmapSafe():org.telegram.messenger.ImageReceiver$BitmapHolder");
    }

    public BitmapHolder getDrawableSafe() {
        String str;
        String str2;
        Drawable drawable = this.currentMediaDrawable;
        if (!(drawable instanceof BitmapDrawable) || (drawable instanceof AnimatedFileDrawable) || (drawable instanceof RLottieDrawable)) {
            Drawable drawable2 = this.currentImageDrawable;
            if (!(drawable2 instanceof BitmapDrawable) || (drawable2 instanceof AnimatedFileDrawable) || (drawable instanceof RLottieDrawable)) {
                drawable2 = this.currentThumbDrawable;
                if (!(drawable2 instanceof BitmapDrawable) || (drawable2 instanceof AnimatedFileDrawable) || (drawable instanceof RLottieDrawable)) {
                    drawable = this.staticThumbDrawable;
                    if (drawable instanceof BitmapDrawable) {
                        str = null;
                    } else {
                        drawable = null;
                        str = null;
                    }
                } else {
                    str2 = this.currentThumbKey;
                }
            } else {
                str2 = this.currentImageKey;
            }
            Drawable drawable3 = drawable2;
            str = str2;
            drawable = drawable3;
        } else {
            str = this.currentMediaKey;
        }
        if (drawable != null) {
            return new BitmapHolder(drawable, str, 0);
        }
        return null;
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
            return new BitmapHolder(bitmap, str, 0);
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
        if (this.isVisible != z) {
            this.isVisible = z;
            if (z2) {
                invalidate();
            }
        }
    }

    public void invalidate() {
        View view = this.parentView;
        if (view != null) {
            if (this.invalidateAll) {
                view.invalidate();
                return;
            }
            float f = this.imageX;
            float f2 = this.imageY;
            view.invalidate((int) f, (int) f2, (int) (f + this.imageW), (int) (f2 + this.imageH));
        }
    }

    public void getParentPosition(int[] iArr) {
        View view = this.parentView;
        if (view != null) {
            view.getLocationInWindow(iArr);
        }
    }

    public boolean getVisible() {
        return this.isVisible;
    }

    @Keep
    public void setAlpha(float f) {
        this.overrideAlpha = f;
    }

    @Keep
    public float getAlpha() {
        return this.overrideAlpha;
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
        this.imageX = (float) i;
    }

    public void setImageY(float f) {
        this.imageY = f;
    }

    public void setImageWidth(int i) {
        this.imageW = (float) i;
    }

    public void setImageCoords(float f, float f2, float f3, float f4) {
        this.imageX = f;
        this.imageY = f2;
        this.imageW = f3;
        this.imageH = f4;
    }

    public void setSideClip(float f) {
        this.sideClip = f;
    }

    public float getCenterX() {
        return this.imageX + (this.imageW / 2.0f);
    }

    public float getCenterY() {
        return this.imageY + (this.imageH / 2.0f);
    }

    public float getImageX() {
        return this.imageX;
    }

    public float getImageX2() {
        return this.imageX + this.imageW;
    }

    public float getImageY() {
        return this.imageY;
    }

    public float getImageY2() {
        return this.imageY + this.imageH;
    }

    public float getImageWidth() {
        return this.imageW;
    }

    public float getImageHeight() {
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
        float f3 = this.imageX;
        if (f >= f3 && f <= f3 + this.imageW) {
            float f4 = this.imageY;
            return f2 >= f4 && f2 <= f4 + this.imageH;
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
        setRoundRadius(new int[]{i, i, i, i});
    }

    public void setRoundRadius(int i, int i2, int i3, int i4) {
        setRoundRadius(new int[]{i, i2, i3, i4});
    }

    public void setRoundRadius(int[] iArr) {
        int i = iArr[0];
        this.isRoundRect = true;
        int i2 = 0;
        boolean z = false;
        while (true) {
            int[] iArr2 = this.roundRadius;
            if (i2 >= iArr2.length) {
                break;
            }
            if (iArr2[i2] != iArr[i2]) {
                z = true;
            }
            if (i != iArr[i2]) {
                this.isRoundRect = false;
            }
            iArr2[i2] = iArr[i2];
            i2++;
        }
        if (z) {
            Drawable drawable = this.currentImageDrawable;
            if (drawable != null && this.imageShader == null) {
                updateDrawableRadius(drawable);
            }
            Drawable drawable2 = this.currentMediaDrawable;
            if (drawable2 != null && this.mediaShader == null) {
                updateDrawableRadius(drawable2);
            }
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

    public void setCurrentAccount(int i) {
        this.currentAccount = i;
    }

    public int[] getRoundRadius() {
        return this.roundRadius;
    }

    public Object getParentObject() {
        return this.currentParentObject;
    }

    public void setNeedsQualityThumb(boolean z) {
        this.needsQualityThumb = z;
    }

    public void setQualityThumbDocument(TLRPC$Document tLRPC$Document) {
        this.qulityThumbDocument = tLRPC$Document;
    }

    public TLRPC$Document getQulityThumbDocument() {
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

    public boolean getAllowStartAnimation() {
        return this.allowStartAnimation;
    }

    public void setAllowStartLottieAnimation(boolean z) {
        this.allowStartLottieAnimation = z;
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
            return;
        }
        RLottieDrawable lottieAnimation = getLottieAnimation();
        if (lottieAnimation != null && !lottieAnimation.isRunning()) {
            lottieAnimation.restart();
        }
    }

    public void stopAnimation() {
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            animation.stop();
            return;
        }
        RLottieDrawable lottieAnimation = getLottieAnimation();
        if (lottieAnimation != null && !lottieAnimation.isRunning()) {
            lottieAnimation.stop();
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
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0085, code lost:
        if ((r9 instanceof org.telegram.messenger.Emoji.EmojiDrawable) == false) goto L_0x0071;
     */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x009c  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00a1  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00b8  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00ba  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean setImageBitmapByKey(android.graphics.drawable.Drawable r8, java.lang.String r9, int r10, boolean r11, int r12) {
        /*
            r7 = this;
            r0 = 0
            if (r8 == 0) goto L_0x0269
            if (r9 == 0) goto L_0x0269
            int r1 = r7.currentGuid
            if (r1 == r12) goto L_0x000b
            goto L_0x0269
        L_0x000b:
            r12 = 0
            r1 = 1
            r2 = 1065353216(0x3var_, float:1.0)
            if (r10 != 0) goto L_0x00c5
            java.lang.String r10 = r7.currentImageKey
            boolean r9 = r9.equals(r10)
            if (r9 != 0) goto L_0x001a
            return r0
        L_0x001a:
            boolean r9 = r8 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r9 != 0) goto L_0x0028
            org.telegram.messenger.ImageLoader r9 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r10 = r7.currentImageKey
            r9.incrementUseCount(r10)
            goto L_0x003f
        L_0x0028:
            r9 = r8
            org.telegram.ui.Components.AnimatedFileDrawable r9 = (org.telegram.ui.Components.AnimatedFileDrawable) r9
            long r3 = r7.startTime
            long r5 = r7.endTime
            r9.setStartEndTime(r3, r5)
            boolean r9 = r9.isWebmSticker
            if (r9 == 0) goto L_0x003f
            org.telegram.messenger.ImageLoader r9 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r10 = r7.currentImageKey
            r9.incrementUseCount(r10)
        L_0x003f:
            r7.currentImageDrawable = r8
            boolean r9 = r8 instanceof org.telegram.messenger.ExtendedBitmapDrawable
            if (r9 == 0) goto L_0x004e
            r9 = r8
            org.telegram.messenger.ExtendedBitmapDrawable r9 = (org.telegram.messenger.ExtendedBitmapDrawable) r9
            int r9 = r9.getOrientation()
            r7.imageOrientation = r9
        L_0x004e:
            r7.updateDrawableRadius(r8)
            boolean r9 = r7.isVisible
            if (r9 == 0) goto L_0x00bf
            if (r11 != 0) goto L_0x005b
            boolean r9 = r7.forcePreview
            if (r9 == 0) goto L_0x005f
        L_0x005b:
            boolean r9 = r7.forceCrossfade
            if (r9 == 0) goto L_0x00bf
        L_0x005f:
            int r9 = r7.crossfadeDuration
            if (r9 == 0) goto L_0x00bf
            android.graphics.drawable.Drawable r9 = r7.currentMediaDrawable
            boolean r10 = r9 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r10 == 0) goto L_0x0073
            org.telegram.ui.Components.AnimatedFileDrawable r9 = (org.telegram.ui.Components.AnimatedFileDrawable) r9
            boolean r9 = r9.hasBitmap()
            if (r9 == 0) goto L_0x0073
        L_0x0071:
            r9 = 0
            goto L_0x0088
        L_0x0073:
            android.graphics.drawable.Drawable r9 = r7.currentImageDrawable
            boolean r9 = r9 instanceof org.telegram.ui.Components.RLottieDrawable
            if (r9 == 0) goto L_0x0087
            android.graphics.drawable.Drawable r9 = r7.staticThumbDrawable
            boolean r10 = r9 instanceof org.telegram.ui.Components.LoadingStickerDrawable
            if (r10 != 0) goto L_0x0087
            boolean r10 = r9 instanceof org.telegram.messenger.SvgHelper.SvgDrawable
            if (r10 != 0) goto L_0x0087
            boolean r9 = r9 instanceof org.telegram.messenger.Emoji.EmojiDrawable
            if (r9 == 0) goto L_0x0071
        L_0x0087:
            r9 = 1
        L_0x0088:
            if (r9 == 0) goto L_0x01d3
            android.graphics.drawable.Drawable r9 = r7.currentThumbDrawable
            if (r9 != 0) goto L_0x0096
            android.graphics.drawable.Drawable r10 = r7.staticThumbDrawable
            if (r10 != 0) goto L_0x0096
            boolean r10 = r7.forceCrossfade
            if (r10 == 0) goto L_0x01d3
        L_0x0096:
            if (r9 == 0) goto L_0x00a1
            android.graphics.drawable.Drawable r9 = r7.staticThumbDrawable
            if (r9 == 0) goto L_0x00a1
            float r9 = r7.currentAlpha
            r7.previousAlpha = r9
            goto L_0x00a3
        L_0x00a1:
            r7.previousAlpha = r2
        L_0x00a3:
            r7.currentAlpha = r12
            long r9 = java.lang.System.currentTimeMillis()
            r7.lastUpdateAlphaTime = r9
            android.graphics.drawable.Drawable r9 = r7.crossfadeImage
            if (r9 != 0) goto L_0x00ba
            android.graphics.drawable.Drawable r9 = r7.currentThumbDrawable
            if (r9 != 0) goto L_0x00ba
            android.graphics.drawable.Drawable r9 = r7.staticThumbDrawable
            if (r9 == 0) goto L_0x00b8
            goto L_0x00ba
        L_0x00b8:
            r9 = 0
            goto L_0x00bb
        L_0x00ba:
            r9 = 1
        L_0x00bb:
            r7.crossfadeWithThumb = r9
            goto L_0x01d3
        L_0x00bf:
            r7.currentAlpha = r2
            r7.previousAlpha = r2
            goto L_0x01d3
        L_0x00c5:
            r3 = 3
            if (r10 != r3) goto L_0x014a
            java.lang.String r10 = r7.currentMediaKey
            boolean r9 = r9.equals(r10)
            if (r9 != 0) goto L_0x00d1
            return r0
        L_0x00d1:
            boolean r9 = r8 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r9 != 0) goto L_0x00df
            org.telegram.messenger.ImageLoader r9 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r10 = r7.currentMediaKey
            r9.incrementUseCount(r10)
            goto L_0x00f6
        L_0x00df:
            r9 = r8
            org.telegram.ui.Components.AnimatedFileDrawable r9 = (org.telegram.ui.Components.AnimatedFileDrawable) r9
            long r3 = r7.startTime
            long r5 = r7.endTime
            r9.setStartEndTime(r3, r5)
            boolean r9 = r9.isWebmSticker
            if (r9 == 0) goto L_0x00f6
            org.telegram.messenger.ImageLoader r9 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r10 = r7.currentImageKey
            r9.incrementUseCount(r10)
        L_0x00f6:
            r7.currentMediaDrawable = r8
            r7.updateDrawableRadius(r8)
            android.graphics.drawable.Drawable r9 = r7.currentImageDrawable
            if (r9 != 0) goto L_0x01d3
            if (r11 != 0) goto L_0x0105
            boolean r9 = r7.forcePreview
            if (r9 == 0) goto L_0x0109
        L_0x0105:
            boolean r9 = r7.forceCrossfade
            if (r9 == 0) goto L_0x0144
        L_0x0109:
            android.graphics.drawable.Drawable r9 = r7.currentThumbDrawable
            if (r9 != 0) goto L_0x0111
            android.graphics.drawable.Drawable r10 = r7.staticThumbDrawable
            if (r10 == 0) goto L_0x011b
        L_0x0111:
            float r10 = r7.currentAlpha
            int r10 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1))
            if (r10 == 0) goto L_0x011b
            boolean r10 = r7.forceCrossfade
            if (r10 == 0) goto L_0x01d3
        L_0x011b:
            if (r9 == 0) goto L_0x0126
            android.graphics.drawable.Drawable r9 = r7.staticThumbDrawable
            if (r9 == 0) goto L_0x0126
            float r9 = r7.currentAlpha
            r7.previousAlpha = r9
            goto L_0x0128
        L_0x0126:
            r7.previousAlpha = r2
        L_0x0128:
            r7.currentAlpha = r12
            long r9 = java.lang.System.currentTimeMillis()
            r7.lastUpdateAlphaTime = r9
            android.graphics.drawable.Drawable r9 = r7.crossfadeImage
            if (r9 != 0) goto L_0x013f
            android.graphics.drawable.Drawable r9 = r7.currentThumbDrawable
            if (r9 != 0) goto L_0x013f
            android.graphics.drawable.Drawable r9 = r7.staticThumbDrawable
            if (r9 == 0) goto L_0x013d
            goto L_0x013f
        L_0x013d:
            r9 = 0
            goto L_0x0140
        L_0x013f:
            r9 = 1
        L_0x0140:
            r7.crossfadeWithThumb = r9
            goto L_0x01d3
        L_0x0144:
            r7.currentAlpha = r2
            r7.previousAlpha = r2
            goto L_0x01d3
        L_0x014a:
            if (r10 != r1) goto L_0x01d3
            android.graphics.drawable.Drawable r10 = r7.currentThumbDrawable
            if (r10 == 0) goto L_0x0151
            return r0
        L_0x0151:
            boolean r10 = r7.forcePreview
            if (r10 != 0) goto L_0x0173
            org.telegram.ui.Components.AnimatedFileDrawable r10 = r7.getAnimation()
            if (r10 == 0) goto L_0x0162
            boolean r10 = r10.hasBitmap()
            if (r10 == 0) goto L_0x0162
            return r0
        L_0x0162:
            android.graphics.drawable.Drawable r10 = r7.currentImageDrawable
            if (r10 == 0) goto L_0x016a
            boolean r10 = r10 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r10 == 0) goto L_0x0172
        L_0x016a:
            android.graphics.drawable.Drawable r10 = r7.currentMediaDrawable
            if (r10 == 0) goto L_0x0173
            boolean r10 = r10 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r10 != 0) goto L_0x0173
        L_0x0172:
            return r0
        L_0x0173:
            java.lang.String r10 = r7.currentThumbKey
            boolean r9 = r9.equals(r10)
            if (r9 != 0) goto L_0x017c
            return r0
        L_0x017c:
            org.telegram.messenger.ImageLoader r9 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r10 = r7.currentThumbKey
            r9.incrementUseCount(r10)
            r7.currentThumbDrawable = r8
            boolean r9 = r8 instanceof org.telegram.messenger.ExtendedBitmapDrawable
            if (r9 == 0) goto L_0x0194
            r9 = r8
            org.telegram.messenger.ExtendedBitmapDrawable r9 = (org.telegram.messenger.ExtendedBitmapDrawable) r9
            int r9 = r9.getOrientation()
            r7.thumbOrientation = r9
        L_0x0194:
            r7.updateDrawableRadius(r8)
            if (r11 != 0) goto L_0x01cf
            byte r9 = r7.crossfadeAlpha
            r10 = 2
            if (r9 == r10) goto L_0x01cf
            java.lang.Object r9 = r7.currentParentObject
            boolean r10 = r9 instanceof org.telegram.messenger.MessageObject
            if (r10 == 0) goto L_0x01bb
            org.telegram.messenger.MessageObject r9 = (org.telegram.messenger.MessageObject) r9
            boolean r9 = r9.isRoundVideo()
            if (r9 == 0) goto L_0x01bb
            java.lang.Object r9 = r7.currentParentObject
            org.telegram.messenger.MessageObject r9 = (org.telegram.messenger.MessageObject) r9
            boolean r9 = r9.isSending()
            if (r9 == 0) goto L_0x01bb
            r7.currentAlpha = r2
            r7.previousAlpha = r2
            goto L_0x01d3
        L_0x01bb:
            r7.currentAlpha = r12
            r7.previousAlpha = r2
            long r9 = java.lang.System.currentTimeMillis()
            r7.lastUpdateAlphaTime = r9
            android.graphics.drawable.Drawable r9 = r7.staticThumbDrawable
            if (r9 == 0) goto L_0x01cb
            r9 = 1
            goto L_0x01cc
        L_0x01cb:
            r9 = 0
        L_0x01cc:
            r7.crossfadeWithThumb = r9
            goto L_0x01d3
        L_0x01cf:
            r7.currentAlpha = r2
            r7.previousAlpha = r2
        L_0x01d3:
            org.telegram.messenger.ImageReceiver$ImageReceiverDelegate r9 = r7.delegate
            if (r9 == 0) goto L_0x01f7
            android.graphics.drawable.Drawable r10 = r7.currentImageDrawable
            if (r10 != 0) goto L_0x01ea
            android.graphics.drawable.Drawable r12 = r7.currentThumbDrawable
            if (r12 != 0) goto L_0x01ea
            android.graphics.drawable.Drawable r12 = r7.staticThumbDrawable
            if (r12 != 0) goto L_0x01ea
            android.graphics.drawable.Drawable r12 = r7.currentMediaDrawable
            if (r12 == 0) goto L_0x01e8
            goto L_0x01ea
        L_0x01e8:
            r12 = 0
            goto L_0x01eb
        L_0x01ea:
            r12 = 1
        L_0x01eb:
            if (r10 != 0) goto L_0x01f3
            android.graphics.drawable.Drawable r10 = r7.currentMediaDrawable
            if (r10 != 0) goto L_0x01f3
            r10 = 1
            goto L_0x01f4
        L_0x01f3:
            r10 = 0
        L_0x01f4:
            r9.didSetImage(r7, r12, r10, r11)
        L_0x01f7:
            boolean r9 = r8 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r9 == 0) goto L_0x0225
            org.telegram.ui.Components.AnimatedFileDrawable r8 = (org.telegram.ui.Components.AnimatedFileDrawable) r8
            boolean r9 = r7.useSharedAnimationQueue
            r8.setUseSharedQueue(r9)
            boolean r9 = r7.attachedToWindow
            if (r9 == 0) goto L_0x020b
            android.view.View r9 = r7.parentView
            r8.addParent(r9)
        L_0x020b:
            boolean r9 = r7.allowStartAnimation
            if (r9 == 0) goto L_0x0216
            int r9 = r7.currentOpenedLayerFlags
            if (r9 != 0) goto L_0x0216
            r8.start()
        L_0x0216:
            boolean r9 = r7.allowDecodeSingleFrame
            r8.setAllowDecodeSingleFrame(r9)
            r7.animationReadySent = r0
            android.view.View r8 = r7.parentView
            if (r8 == 0) goto L_0x024b
            r8.invalidate()
            goto L_0x024b
        L_0x0225:
            boolean r9 = r8 instanceof org.telegram.ui.Components.RLottieDrawable
            if (r9 == 0) goto L_0x024b
            org.telegram.ui.Components.RLottieDrawable r8 = (org.telegram.ui.Components.RLottieDrawable) r8
            android.view.View r9 = r7.parentView
            r8.addParentView(r9)
            boolean r9 = r7.allowStartLottieAnimation
            if (r9 == 0) goto L_0x0241
            boolean r9 = r8.isHeavyDrawable()
            if (r9 == 0) goto L_0x023e
            int r9 = r7.currentOpenedLayerFlags
            if (r9 != 0) goto L_0x0241
        L_0x023e:
            r8.start()
        L_0x0241:
            r8.setAllowDecodeSingleFrame(r1)
            int r9 = r7.autoRepeat
            r8.setAutoRepeat(r9)
            r7.animationReadySent = r0
        L_0x024b:
            android.view.View r8 = r7.parentView
            if (r8 == 0) goto L_0x0268
            boolean r9 = r7.invalidateAll
            if (r9 == 0) goto L_0x0257
            r8.invalidate()
            goto L_0x0268
        L_0x0257:
            float r9 = r7.imageX
            int r10 = (int) r9
            float r11 = r7.imageY
            int r12 = (int) r11
            float r0 = r7.imageW
            float r9 = r9 + r0
            int r9 = (int) r9
            float r0 = r7.imageH
            float r11 = r11 + r0
            int r11 = (int) r11
            r8.invalidate(r10, r12, r9, r11)
        L_0x0268:
            return r1
        L_0x0269:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageReceiver.setImageBitmapByKey(android.graphics.drawable.Drawable, java.lang.String, int, boolean, int):boolean");
    }

    public void setMediaStartEndTime(long j, long j2) {
        this.startTime = j;
        this.endTime = j2;
        Drawable drawable = this.currentMediaDrawable;
        if (drawable instanceof AnimatedFileDrawable) {
            ((AnimatedFileDrawable) drawable).setStartEndTime(j, j2);
        }
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
        if (str2 != null && ((str2.startsWith("-") || str2.startsWith("strippedmessage-")) && (replacedKey = ImageLoader.getInstance().getReplacedKey(str2)) != null)) {
            str2 = replacedKey;
        }
        if (obj instanceof RLottieDrawable) {
            ((RLottieDrawable) obj).removeParentView(this.parentView);
        }
        if (obj instanceof AnimatedFileDrawable) {
            ((AnimatedFileDrawable) obj).removeParent(this.parentView);
        }
        if (str2 != null && ((str == null || !str.equals(str2)) && obj != null)) {
            if (obj instanceof RLottieDrawable) {
                RLottieDrawable rLottieDrawable = (RLottieDrawable) obj;
                boolean decrementUseCount = ImageLoader.getInstance().decrementUseCount(str2);
                if (!ImageLoader.getInstance().isInMemCache(str2, true) && decrementUseCount) {
                    rLottieDrawable.recycle();
                }
            } else if (obj instanceof AnimatedFileDrawable) {
                AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) obj;
                if (animatedFileDrawable.isWebmSticker) {
                    boolean decrementUseCount2 = ImageLoader.getInstance().decrementUseCount(str2);
                    if (!ImageLoader.getInstance().isInMemCache(str2, true)) {
                        if (decrementUseCount2) {
                            animatedFileDrawable.recycle();
                        }
                    } else if (decrementUseCount2) {
                        animatedFileDrawable.stop();
                    }
                } else {
                    animatedFileDrawable.recycle();
                }
            } else if (obj instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) obj).getBitmap();
                boolean decrementUseCount3 = ImageLoader.getInstance().decrementUseCount(str2);
                if (!ImageLoader.getInstance().isInMemCache(str2, false) && decrementUseCount3) {
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

    public void setCrossfadeDuration(int i) {
        this.crossfadeDuration = i;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3;
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
                int intValue = num.intValue() | this.currentOpenedLayerFlags;
                this.currentOpenedLayerFlags = intValue;
                if (intValue != 0) {
                    RLottieDrawable lottieAnimation = getLottieAnimation();
                    if (lottieAnimation != null && lottieAnimation.isHeavyDrawable()) {
                        lottieAnimation.stop();
                    }
                    AnimatedFileDrawable animation = getAnimation();
                    if (animation != null) {
                        animation.stop();
                    }
                }
            }
        } else if (i == NotificationCenter.startAllHeavyOperations) {
            Integer num2 = objArr[0];
            if (this.currentLayerNum < num2.intValue() && (i3 = this.currentOpenedLayerFlags) != 0) {
                int intValue2 = (num2.intValue() ^ -1) & i3;
                this.currentOpenedLayerFlags = intValue2;
                if (intValue2 == 0) {
                    RLottieDrawable lottieAnimation2 = getLottieAnimation();
                    if (this.allowStartLottieAnimation && lottieAnimation2 != null && lottieAnimation2.isHeavyDrawable()) {
                        lottieAnimation2.start();
                    }
                    AnimatedFileDrawable animation2 = getAnimation();
                    if (this.allowStartAnimation && animation2 != null) {
                        animation2.start();
                        View view = this.parentView;
                        if (view != null) {
                            view.invalidate();
                        }
                    }
                }
            }
        }
    }

    public void startCrossfadeFromStaticThumb(Bitmap bitmap) {
        this.currentThumbKey = null;
        this.currentThumbDrawable = null;
        this.thumbShader = null;
        this.roundPaint.setShader((Shader) null);
        BitmapDrawable bitmapDrawable = new BitmapDrawable((Resources) null, bitmap);
        this.staticThumbDrawable = bitmapDrawable;
        this.crossfadeWithThumb = true;
        this.currentAlpha = 0.0f;
        updateDrawableRadius(bitmapDrawable);
    }

    public void setUniqKeyPrefix(String str) {
        this.uniqKeyPrefix = str;
    }

    public String getUniqKeyPrefix() {
        return this.uniqKeyPrefix;
    }

    public void addLoadingImageRunnable(Runnable runnable) {
        this.loadingOperations.add(runnable);
    }

    public ArrayList<Runnable> getLoadingOperations() {
        return this.loadingOperations;
    }

    public void moveImageToFront() {
        ImageLoader.getInstance().moveToFront(this.currentImageKey);
        ImageLoader.getInstance().moveToFront(this.currentThumbKey);
    }
}
