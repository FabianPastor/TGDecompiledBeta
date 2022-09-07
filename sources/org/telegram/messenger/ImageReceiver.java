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
    private boolean allowLoadingOnAttachedOnly;
    private boolean allowLottieVibration;
    private boolean allowStartAnimation;
    private boolean allowStartLottieAnimation;
    private int animateFromIsPressed;
    public int animatedFileDrawableRepeatMaxCount;
    private boolean animationReadySent;
    private boolean attachedToWindow;
    private int autoRepeat;
    private int autoRepeatCount;
    private long autoRepeatTimeout;
    private RectF bitmapRect;
    private Object blendMode;
    private boolean canceledLoading;
    private boolean centerRotation;
    public boolean clip;
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
    private long currentSize;
    private Drawable currentThumbDrawable;
    private String currentThumbFilter;
    private String currentThumbKey;
    private ImageLocation currentThumbLocation;
    private long currentTime;
    private ImageReceiverDelegate delegate;
    private RectF drawRegion;
    private long endTime;
    private int fileLoadingPriority;
    private boolean forceCrossfade;
    private boolean forceLoding;
    private boolean forcePreview;
    private Bitmap gradientBitmap;
    private BitmapShader gradientShader;
    private boolean ignoreImageSet;
    private float imageH;
    protected int imageOrientation;
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
    private boolean shouldLoadOnAttach;
    private float sideClip;
    private boolean skipUpdateFrame;
    private long startTime;
    private Drawable staticThumbDrawable;
    private ImageLocation strippedLocation;
    private int thumbOrientation;
    private BitmapShader thumbShader;
    private int thumbTag;
    private String uniqKeyPrefix;
    private boolean useRoundForThumb;
    private boolean useSharedAnimationQueue;
    private boolean videoThumbIsSame;

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

    /* access modifiers changed from: protected */
    public boolean customDraw(Canvas canvas, AnimatedFileDrawable animatedFileDrawable, RLottieDrawable rLottieDrawable, Drawable drawable, BitmapShader bitmapShader, Drawable drawable2, BitmapShader bitmapShader2, Drawable drawable3, BitmapShader bitmapShader3, boolean z, boolean z2, Drawable drawable4, BitmapShader bitmapShader4, Drawable drawable5, float f, float f2, float f3, int[] iArr, BackgroundThreadDrawHolder backgroundThreadDrawHolder) {
        return false;
    }

    public void skipDraw() {
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
        public long size;
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
        this.fileLoadingPriority = 1;
        this.useRoundForThumb = true;
        this.allowLottieVibration = true;
        this.allowStartAnimation = true;
        this.allowStartLottieAnimation = true;
        this.autoRepeat = 1;
        this.autoRepeatCount = -1;
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
        this.clip = true;
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

    public void setImage(ImageLocation imageLocation, String str, Drawable drawable, long j, String str2, Object obj, int i) {
        setImage(imageLocation, str, (ImageLocation) null, (String) null, drawable, j, str2, obj, i);
    }

    public void setImage(String str, String str2, Drawable drawable, String str3, long j) {
        setImage(ImageLocation.getForPath(str), str2, (ImageLocation) null, (String) null, drawable, j, str3, (Object) null, 1);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, String str3, Object obj, int i) {
        setImage(imageLocation, str, imageLocation2, str2, (Drawable) null, 0, str3, obj, i);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, long j, String str3, Object obj, int i) {
        setImage(imageLocation, str, imageLocation2, str2, (Drawable) null, j, str3, obj, i);
    }

    public void setForUserOrChat(TLObject tLObject, Drawable drawable) {
        setForUserOrChat(tLObject, drawable, (Object) null);
    }

    public void setForUserOrChat(TLObject tLObject, Drawable drawable, Object obj) {
        setForUserOrChat(tLObject, drawable, (Object) null, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: org.telegram.tgnet.TLObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v0, resolved type: android.graphics.drawable.BitmapDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: android.graphics.drawable.BitmapDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: android.graphics.drawable.BitmapDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: org.telegram.tgnet.TLObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: org.telegram.tgnet.TLObject} */
    /* JADX WARNING: type inference failed for: r7v5 */
    /* JADX WARNING: type inference failed for: r6v5, types: [android.graphics.drawable.BitmapDrawable] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setForUserOrChat(org.telegram.tgnet.TLObject r17, android.graphics.drawable.Drawable r18, java.lang.Object r19, boolean r20) {
        /*
            r16 = this;
            r13 = r16
            r0 = r17
            if (r19 != 0) goto L_0x0008
            r11 = r0
            goto L_0x000a
        L_0x0008:
            r11 = r19
        L_0x000a:
            r1 = 1
            r13.setUseRoundForThumbDrawable(r1)
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$User
            r3 = 0
            r4 = 0
            if (r2 == 0) goto L_0x00a4
            r2 = r0
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC$User) r2
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r2.photo
            if (r5 == 0) goto L_0x00a0
            android.graphics.drawable.BitmapDrawable r6 = r5.strippedBitmap
            byte[] r5 = r5.stripped_thumb
            if (r5 == 0) goto L_0x0023
            r5 = 1
            goto L_0x0024
        L_0x0023:
            r5 = 0
        L_0x0024:
            if (r20 == 0) goto L_0x009c
            int r7 = r13.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            boolean r7 = r7.isPremiumUser(r2)
            if (r7 == 0) goto L_0x009c
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r2.photo
            boolean r7 = r7.has_video
            if (r7 == 0) goto L_0x009c
            int r7 = r13.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            long r8 = r2.id
            org.telegram.tgnet.TLRPC$UserFull r7 = r7.getUserFull(r8)
            if (r7 != 0) goto L_0x0052
            int r7 = r13.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            int r8 = r13.currentGuid
            r7.loadFullUser(r2, r8, r4)
            goto L_0x009c
        L_0x0052:
            org.telegram.tgnet.TLRPC$Photo r2 = r7.profile_photo
            if (r2 == 0) goto L_0x009c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r2 = r2.video_sizes
            if (r2 == 0) goto L_0x009c
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x009c
            org.telegram.tgnet.TLRPC$Photo r2 = r7.profile_photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r2 = r2.video_sizes
            java.lang.Object r2 = r2.get(r4)
            org.telegram.tgnet.TLRPC$VideoSize r2 = (org.telegram.tgnet.TLRPC$VideoSize) r2
        L_0x006a:
            org.telegram.tgnet.TLRPC$Photo r3 = r7.profile_photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r3 = r3.video_sizes
            int r3 = r3.size()
            if (r4 >= r3) goto L_0x0096
            org.telegram.tgnet.TLRPC$Photo r3 = r7.profile_photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r3 = r3.video_sizes
            java.lang.Object r3 = r3.get(r4)
            org.telegram.tgnet.TLRPC$VideoSize r3 = (org.telegram.tgnet.TLRPC$VideoSize) r3
            java.lang.String r3 = r3.type
            java.lang.String r8 = "p"
            boolean r3 = r8.equals(r3)
            if (r3 == 0) goto L_0x0093
            org.telegram.tgnet.TLRPC$Photo r2 = r7.profile_photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r2 = r2.video_sizes
            java.lang.Object r2 = r2.get(r4)
            org.telegram.tgnet.TLRPC$VideoSize r2 = (org.telegram.tgnet.TLRPC$VideoSize) r2
            goto L_0x0096
        L_0x0093:
            int r4 = r4 + 1
            goto L_0x006a
        L_0x0096:
            org.telegram.tgnet.TLRPC$Photo r3 = r7.profile_photo
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$VideoSize) r2, (org.telegram.tgnet.TLRPC$Photo) r3)
        L_0x009c:
            r2 = r3
            r4 = r5
            r3 = r6
            goto L_0x00a1
        L_0x00a0:
            r2 = r3
        L_0x00a1:
            r7 = r3
            r3 = r2
            goto L_0x00b9
        L_0x00a4:
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r2 == 0) goto L_0x00b8
            r2 = r0
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            org.telegram.tgnet.TLRPC$ChatPhoto r2 = r2.photo
            if (r2 == 0) goto L_0x00b8
            android.graphics.drawable.BitmapDrawable r5 = r2.strippedBitmap
            byte[] r2 = r2.stripped_thumb
            if (r2 == 0) goto L_0x00b6
            r4 = 1
        L_0x00b6:
            r7 = r5
            goto L_0x00b9
        L_0x00b8:
            r7 = r3
        L_0x00b9:
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r1)
            java.lang.String r6 = "50_50"
            if (r3 == 0) goto L_0x00d8
            r8 = 0
            r9 = 0
            r14 = 0
            r10 = 0
            r12 = 0
            java.lang.String r2 = "avatar"
            r0 = r16
            r1 = r3
            r3 = r5
            r4 = r6
            r5 = r8
            r6 = r9
            r8 = r14
            r0.setImage(r1, r2, r3, r4, r5, r6, r7, r8, r10, r11, r12)
            r0 = 3
            r13.animatedFileDrawableRepeatMaxCount = r0
            goto L_0x0109
        L_0x00d8:
            if (r7 == 0) goto L_0x00e7
            r4 = 0
            r8 = 0
            r0 = r16
            r1 = r5
            r2 = r6
            r3 = r7
            r5 = r11
            r6 = r8
            r0.setImage(r1, r2, r3, r4, r5, r6)
            goto L_0x0109
        L_0x00e7:
            if (r4 == 0) goto L_0x00fc
            r1 = 2
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r1)
            r7 = 0
            java.lang.String r4 = "50_50_b"
            r0 = r16
            r1 = r5
            r2 = r6
            r5 = r18
            r6 = r11
            r0.setImage((org.telegram.messenger.ImageLocation) r1, (java.lang.String) r2, (org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (android.graphics.drawable.Drawable) r5, (java.lang.Object) r6, (int) r7)
            goto L_0x0109
        L_0x00fc:
            r4 = 0
            r7 = 0
            r0 = r16
            r1 = r5
            r2 = r6
            r3 = r18
            r5 = r11
            r6 = r7
            r0.setImage(r1, r2, r3, r4, r5, r6)
        L_0x0109:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageReceiver.setForUserOrChat(org.telegram.tgnet.TLObject, android.graphics.drawable.Drawable, java.lang.Object, boolean):void");
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, Drawable drawable, Object obj, int i) {
        setImage((ImageLocation) null, (String) null, imageLocation, str, imageLocation2, str2, drawable, 0, (String) null, obj, i);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, Drawable drawable, long j, String str3, Object obj, int i) {
        setImage((ImageLocation) null, (String) null, imageLocation, str, imageLocation2, str2, drawable, j, str3, obj, i);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, ImageLocation imageLocation3, String str3, Drawable drawable, long j, String str4, Object obj, int i) {
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
        long j2 = j;
        String str10 = str4;
        Object obj2 = obj;
        int i2 = i;
        if (this.allowLoadingOnAttachedOnly && !this.attachedToWindow) {
            if (this.setImageBackup == null) {
                this.setImageBackup = new SetImageBackup();
            }
            SetImageBackup setImageBackup3 = this.setImageBackup;
            setImageBackup3.mediaLocation = imageLocation4;
            setImageBackup3.mediaFilter = str7;
            setImageBackup3.imageLocation = imageLocation5;
            setImageBackup3.imageFilter = str8;
            setImageBackup3.thumbLocation = imageLocation6;
            setImageBackup3.thumbFilter = str9;
            setImageBackup3.thumb = drawable2;
            setImageBackup3.size = j2;
            setImageBackup3.ext = str10;
            setImageBackup3.cacheType = i2;
            setImageBackup3.parentObject = obj2;
        } else if (!this.ignoreImageSet) {
            if (this.crossfadeWithOldImage && (setImageBackup2 = this.setImageBackup) != null && setImageBackup2.isWebfileSet()) {
                setBackupImage();
            }
            SetImageBackup setImageBackup4 = this.setImageBackup;
            if (setImageBackup4 != null) {
                setImageBackup4.clear();
            }
            boolean z = true;
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
                invalidate();
                ImageReceiverDelegate imageReceiverDelegate = this.delegate;
                if (imageReceiverDelegate != null) {
                    Drawable drawable3 = this.currentImageDrawable;
                    boolean z2 = (drawable3 == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true;
                    if (!(drawable3 == null && this.currentMediaDrawable == null)) {
                        z = false;
                    }
                    imageReceiverDelegate.didSetImage(this, z2, z, false);
                    return;
                }
                return;
            }
            String key = imageLocation5 != null ? imageLocation5.getKey(obj2, (Object) null, false) : null;
            if (key == null && imageLocation5 != null) {
                imageLocation5 = null;
            }
            this.animatedFileDrawableRepeatMaxCount = Math.max(this.autoRepeatCount, 0);
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
            String str11 = key;
            if (!(str11 == null || str8 == null)) {
                str11 = str11 + "@" + str8;
            }
            if (this.uniqKeyPrefix != null) {
                str11 = this.uniqKeyPrefix + str11;
            }
            String key2 = imageLocation4 != null ? imageLocation4.getKey(obj2, (Object) null, false) : null;
            if (key2 == null && imageLocation4 != null) {
                imageLocation4 = null;
            }
            if (!(key2 == null || str7 == null)) {
                key2 = key2 + "@" + str7;
            }
            if (this.uniqKeyPrefix != null) {
                key2 = this.uniqKeyPrefix + key2;
            }
            if ((key2 == null && (str6 = this.currentImageKey) != null && str6.equals(str11)) || ((str5 = this.currentMediaKey) != null && str5.equals(key2))) {
                ImageReceiverDelegate imageReceiverDelegate2 = this.delegate;
                if (imageReceiverDelegate2 != null) {
                    Drawable drawable4 = this.currentImageDrawable;
                    imageReceiverDelegate2.didSetImage(this, (drawable4 == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true, drawable4 == null && this.currentMediaDrawable == null, false);
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
                        ((AnimatedFileDrawable) this.currentMediaDrawable).removeParent(this);
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
                    recycleBitmap(str11, 0);
                    recycleBitmap((String) null, 2);
                    recycleBitmap(key2, 3);
                    this.crossfadeShader = this.thumbShader;
                    this.crossfadeImage = this.currentThumbDrawable;
                    this.crossfadeKey = this.currentThumbKey;
                    this.crossfadingWithThumb = false;
                    this.currentThumbDrawable = null;
                    this.currentThumbKey = null;
                } else if (this.staticThumbDrawable != null) {
                    recycleBitmap(str11, 0);
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
                    recycleBitmap(str11, 0);
                    recycleBitmap(key3, 1);
                    recycleBitmap((String) null, 2);
                    recycleBitmap(key2, 3);
                    this.crossfadeShader = null;
                }
            } else {
                recycleBitmap(str11, 0);
                recycleBitmap(key3, 1);
                recycleBitmap((String) null, 2);
                recycleBitmap(key2, 3);
                this.crossfadeShader = null;
            }
            this.currentImageLocation = imageLocation5;
            this.currentImageFilter = str8;
            this.currentImageKey = str11;
            this.currentMediaLocation = imageLocation4;
            this.currentMediaFilter = str7;
            this.currentMediaKey = key2;
            this.currentThumbLocation = imageLocation6;
            this.currentThumbFilter = str9;
            this.currentThumbKey = key3;
            this.currentParentObject = obj2;
            this.currentExt = str10;
            this.currentSize = j;
            this.currentCacheType = i;
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
                imageReceiverDelegate3.didSetImage(this, (drawable7 == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true, drawable7 == null && this.currentMediaDrawable == null, false);
            }
            loadImage();
            this.isRoundVideo = (obj2 instanceof MessageObject) && ((MessageObject) obj2).isRoundVideo();
        }
    }

    private void loadImage() {
        ImageLoader.getInstance().loadImageForImageReceiver(this);
        invalidate();
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
                animatedFileDrawable.addParent(this);
            }
            animatedFileDrawable.setUseSharedQueue(this.useSharedAnimationQueue || animatedFileDrawable.isWebmSticker);
            if (this.allowStartAnimation && this.currentOpenedLayerFlags == 0) {
                animatedFileDrawable.checkRepeat();
            }
            animatedFileDrawable.setAllowDecodeSingleFrame(this.allowDecodeSingleFrame);
        } else if (drawable instanceof RLottieDrawable) {
            RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
            if (this.attachedToWindow) {
                rLottieDrawable.addParentView(this);
            }
            if (rLottieDrawable != null) {
                rLottieDrawable.setAllowVibration(this.allowLottieVibration);
            }
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
        Drawable drawable3 = this.currentMediaDrawable;
        if (drawable3 instanceof AnimatedFileDrawable) {
            ((AnimatedFileDrawable) drawable3).removeParent(this);
        }
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
        invalidate();
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
                        Shader.TileMode tileMode = Shader.TileMode.REPEAT;
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
            animation.removeParent(this);
        }
        RLottieDrawable lottieAnimation = getLottieAnimation();
        if (lottieAnimation != null) {
            lottieAnimation.removeParentView(this);
        }
    }

    public boolean setBackupImage() {
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
        if (lottieAnimation != null) {
            lottieAnimation.setAllowVibration(this.allowLottieVibration);
        }
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
        if (lottieAnimation != null) {
            lottieAnimation.addParentView(this);
            lottieAnimation.setAllowVibration(this.allowLottieVibration);
        }
        if (lottieAnimation != null && this.allowStartLottieAnimation && (!lottieAnimation.isHeavyDrawable() || this.currentOpenedLayerFlags == 0)) {
            lottieAnimation.start();
        }
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            animation.addParent(this);
        }
        if (animation != null && this.allowStartAnimation && this.currentOpenedLayerFlags == 0) {
            animation.checkRepeat();
            invalidate();
        }
        if (NotificationCenter.getGlobalInstance().isAnimationInProgress()) {
            didReceivedNotification(i, this.currentAccount, 512);
        }
        return false;
    }

    private void drawDrawable(Canvas canvas, Drawable drawable, int i, BitmapShader bitmapShader, int i2, BackgroundThreadDrawHolder backgroundThreadDrawHolder) {
        if (this.isPressed == 0) {
            float f = this.pressedProgress;
            if (f != 0.0f) {
                float f2 = f - 0.10666667f;
                this.pressedProgress = f2;
                if (f2 < 0.0f) {
                    this.pressedProgress = 0.0f;
                }
                invalidate();
            }
        }
        int i3 = this.isPressed;
        if (i3 != 0) {
            this.pressedProgress = 1.0f;
            this.animateFromIsPressed = i3;
        }
        float f3 = this.pressedProgress;
        if (f3 == 0.0f || f3 == 1.0f) {
            drawDrawable(canvas, drawable, i, bitmapShader, i2, i3, backgroundThreadDrawHolder);
            return;
        }
        Drawable drawable2 = drawable;
        BitmapShader bitmapShader2 = bitmapShader;
        int i4 = i2;
        BackgroundThreadDrawHolder backgroundThreadDrawHolder2 = backgroundThreadDrawHolder;
        drawDrawable(canvas, drawable2, i, bitmapShader2, i4, i3, backgroundThreadDrawHolder2);
        drawDrawable(canvas, drawable2, (int) (((float) i) * this.pressedProgress), bitmapShader2, i4, this.animateFromIsPressed, backgroundThreadDrawHolder2);
    }

    public void setUseRoundForThumbDrawable(boolean z) {
        this.useRoundForThumb = z;
    }

    /* access modifiers changed from: protected */
    public void drawDrawable(Canvas canvas, Drawable drawable, int i, BitmapShader bitmapShader, int i2, int i3, BackgroundThreadDrawHolder backgroundThreadDrawHolder) {
        int[] iArr;
        ColorFilter colorFilter2;
        RectF rectF;
        float f;
        float f2;
        float f3;
        float f4;
        int[] iArr2;
        boolean z;
        Paint paint;
        int i4;
        int i5;
        BitmapDrawable bitmapDrawable;
        int i6;
        Canvas canvas2 = canvas;
        Drawable drawable2 = drawable;
        int i7 = i;
        BitmapShader bitmapShader2 = bitmapShader;
        int i8 = i2;
        int i9 = i3;
        BackgroundThreadDrawHolder backgroundThreadDrawHolder2 = backgroundThreadDrawHolder;
        if (backgroundThreadDrawHolder2 != null) {
            f4 = backgroundThreadDrawHolder2.imageX;
            f3 = backgroundThreadDrawHolder2.imageY;
            f2 = backgroundThreadDrawHolder2.imageH;
            f = backgroundThreadDrawHolder2.imageW;
            rectF = backgroundThreadDrawHolder2.drawRegion;
            colorFilter2 = backgroundThreadDrawHolder2.colorFilter;
            iArr = backgroundThreadDrawHolder.roundRadius;
        } else {
            f4 = this.imageX;
            f3 = this.imageY;
            f2 = this.imageH;
            f = this.imageW;
            rectF = this.drawRegion;
            colorFilter2 = this.colorFilter;
            iArr = this.roundRadius;
        }
        if (drawable2 instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable2 = (BitmapDrawable) drawable2;
            boolean z2 = drawable2 instanceof RLottieDrawable;
            if (z2) {
                z = z2;
                iArr2 = iArr;
                ((RLottieDrawable) drawable2).skipFrameUpdate = this.skipUpdateFrame;
            } else {
                z = z2;
                iArr2 = iArr;
                if (drawable2 instanceof AnimatedFileDrawable) {
                    ((AnimatedFileDrawable) drawable2).skipFrameUpdate = this.skipUpdateFrame;
                }
            }
            if (bitmapShader2 != null) {
                paint = this.roundPaint;
            } else {
                paint = bitmapDrawable2.getPaint();
            }
            int i10 = Build.VERSION.SDK_INT;
            RectF rectF2 = rectF;
            if (i10 >= 29) {
                Object obj = this.blendMode;
                if (obj == null || this.gradientShader != null) {
                    paint.setBlendMode((BlendMode) null);
                } else {
                    paint.setBlendMode((BlendMode) obj);
                }
            }
            boolean z3 = (paint == null || paint.getColorFilter() == null) ? false : true;
            if (!z3 || i9 != 0) {
                if (!z3 && i9 != 0) {
                    if (i9 == 1) {
                        if (bitmapShader2 != null) {
                            this.roundPaint.setColorFilter(selectedColorFilter);
                        } else {
                            bitmapDrawable2.setColorFilter(selectedColorFilter);
                        }
                    } else if (bitmapShader2 != null) {
                        this.roundPaint.setColorFilter(selectedGroupColorFilter);
                    } else {
                        bitmapDrawable2.setColorFilter(selectedGroupColorFilter);
                    }
                }
            } else if (bitmapShader2 != null) {
                this.roundPaint.setColorFilter((ColorFilter) null);
            } else if (this.staticThumbDrawable != drawable2) {
                bitmapDrawable2.setColorFilter((ColorFilter) null);
            }
            if (colorFilter2 != null && this.gradientShader == null) {
                if (bitmapShader2 != null) {
                    this.roundPaint.setColorFilter(colorFilter2);
                } else {
                    bitmapDrawable2.setColorFilter(colorFilter2);
                }
            }
            boolean z4 = bitmapDrawable2 instanceof AnimatedFileDrawable;
            if (z4 || (bitmapDrawable2 instanceof RLottieDrawable)) {
                int i11 = i8 % 360;
                if (i11 == 90 || i11 == 270) {
                    i4 = bitmapDrawable2.getIntrinsicHeight();
                    i5 = bitmapDrawable2.getIntrinsicWidth();
                } else {
                    i4 = bitmapDrawable2.getIntrinsicWidth();
                    i5 = bitmapDrawable2.getIntrinsicHeight();
                }
            } else {
                Bitmap bitmap = bitmapDrawable2.getBitmap();
                if (bitmap == null || !bitmap.isRecycled()) {
                    int i12 = i8 % 360;
                    if (i12 == 90 || i12 == 270) {
                        i4 = bitmap.getHeight();
                        i5 = bitmap.getWidth();
                    } else {
                        i4 = bitmap.getWidth();
                        i5 = bitmap.getHeight();
                    }
                } else {
                    return;
                }
            }
            float f5 = this.sideClip;
            float f6 = f - (f5 * 2.0f);
            float f7 = f2 - (f5 * 2.0f);
            float f8 = f == 0.0f ? 1.0f : ((float) i4) / f6;
            float f9 = f2 == 0.0f ? 1.0f : ((float) i5) / f7;
            if (bitmapShader2 == null || backgroundThreadDrawHolder2 != null) {
                BackgroundThreadDrawHolder backgroundThreadDrawHolder3 = backgroundThreadDrawHolder2;
                float var_ = f;
                BitmapDrawable bitmapDrawable3 = bitmapDrawable2;
                int[] iArr3 = iArr2;
                Canvas canvas3 = canvas;
                int i13 = i8;
                int i14 = i10;
                RectF rectF3 = rectF2;
                int i15 = i5;
                int i16 = i;
                boolean z5 = z4;
                if (this.isAspectFit) {
                    float max = Math.max(f8, f9);
                    canvas.save();
                    int i17 = (int) (((float) i4) / max);
                    int i18 = (int) (((float) i15) / max);
                    if (backgroundThreadDrawHolder3 == null) {
                        float var_ = (float) i17;
                        float var_ = (float) i18;
                        rectF3.set(((var_ - var_) / 2.0f) + f4, ((f2 - var_) / 2.0f) + f3, ((var_ + var_) / 2.0f) + f4, ((var_ + f2) / 2.0f) + f3);
                        bitmapDrawable3.setBounds((int) rectF3.left, (int) rectF3.top, (int) rectF3.right, (int) rectF3.bottom);
                        if (bitmapDrawable3 instanceof AnimatedFileDrawable) {
                            ((AnimatedFileDrawable) bitmapDrawable3).setActualDrawRect(rectF3.left, rectF3.top, rectF3.width(), rectF3.height());
                        }
                    }
                    if (!(backgroundThreadDrawHolder3 == null || iArr3 == null || iArr3[0] <= 0)) {
                        canvas.save();
                        Path access$502 = backgroundThreadDrawHolder.roundPath == null ? backgroundThreadDrawHolder3.roundPath = new Path() : backgroundThreadDrawHolder.roundPath;
                        access$502.rewind();
                        RectF rectF4 = AndroidUtilities.rectTmp;
                        rectF4.set(f4, f3, f4 + var_, f2 + f3);
                        access$502.addRoundRect(rectF4, (float) iArr3[0], (float) iArr3[2], Path.Direction.CW);
                        canvas3.clipPath(access$502);
                    }
                    if (this.isVisible) {
                        try {
                            bitmapDrawable3.setAlpha(i16);
                            drawBitmapDrawable(canvas3, bitmapDrawable3, backgroundThreadDrawHolder3, i16);
                        } catch (Exception e) {
                            if (backgroundThreadDrawHolder3 == null) {
                                onBitmapException(bitmapDrawable3);
                            }
                            FileLog.e((Throwable) e);
                        }
                    }
                    canvas.restore();
                    if (!(backgroundThreadDrawHolder3 == null || iArr3 == null || iArr3[0] <= 0)) {
                        canvas.restore();
                    }
                } else if (Math.abs(f8 - f9) > 1.0E-5f) {
                    canvas.save();
                    if (this.clip) {
                        canvas3.clipRect(f4, f3, f4 + var_, f3 + f2);
                    }
                    int i19 = i13 % 360;
                    if (i19 != 0) {
                        if (this.centerRotation) {
                            canvas3.rotate((float) i13, var_ / 2.0f, f2 / 2.0f);
                        } else {
                            canvas3.rotate((float) i13, 0.0f, 0.0f);
                        }
                    }
                    float var_ = ((float) i4) / f9;
                    if (var_ > var_) {
                        float var_ = (float) ((int) var_);
                        rectF3.set(f4 - ((var_ - var_) / 2.0f), f3, ((var_ + var_) / 2.0f) + f4, f3 + f2);
                    } else {
                        float var_ = (float) ((int) (((float) i15) / f8));
                        rectF3.set(f4, f3 - ((var_ - f2) / 2.0f), f4 + var_, ((var_ + f2) / 2.0f) + f3);
                    }
                    if (z5) {
                        ((AnimatedFileDrawable) bitmapDrawable3).setActualDrawRect(f4, f3, var_, f2);
                    }
                    if (backgroundThreadDrawHolder3 == null) {
                        if (i19 == 90 || i19 == 270) {
                            float width = rectF3.width() / 2.0f;
                            float height = rectF3.height() / 2.0f;
                            float centerX = rectF3.centerX();
                            float centerY = rectF3.centerY();
                            bitmapDrawable3.setBounds((int) (centerX - height), (int) (centerY - width), (int) (centerX + height), (int) (centerY + width));
                        } else {
                            bitmapDrawable3.setBounds((int) rectF3.left, (int) rectF3.top, (int) rectF3.right, (int) rectF3.bottom);
                        }
                    }
                    if (this.isVisible) {
                        if (i14 >= 29) {
                            try {
                                if (this.blendMode != null) {
                                    bitmapDrawable3.getPaint().setBlendMode((BlendMode) this.blendMode);
                                } else {
                                    bitmapDrawable3.getPaint().setBlendMode((BlendMode) null);
                                }
                            } catch (Exception e2) {
                                if (backgroundThreadDrawHolder3 == null) {
                                    onBitmapException(bitmapDrawable3);
                                }
                                FileLog.e((Throwable) e2);
                            }
                        }
                        drawBitmapDrawable(canvas3, bitmapDrawable3, backgroundThreadDrawHolder3, i);
                    }
                    canvas.restore();
                } else {
                    int i20 = i16;
                    int i21 = i14;
                    float var_ = var_;
                    canvas.save();
                    int i22 = i13 % 360;
                    if (i22 != 0) {
                        if (this.centerRotation) {
                            canvas3.rotate((float) i13, var_ / 2.0f, f2 / 2.0f);
                        } else {
                            canvas3.rotate((float) i13, 0.0f, 0.0f);
                        }
                    }
                    rectF3.set(f4, f3, f4 + var_, f3 + f2);
                    if (this.isRoundVideo) {
                        int i23 = AndroidUtilities.roundMessageInset;
                        rectF3.inset((float) (-i23), (float) (-i23));
                    }
                    if (z5) {
                        ((AnimatedFileDrawable) bitmapDrawable3).setActualDrawRect(f4, f3, var_, f2);
                    }
                    if (backgroundThreadDrawHolder3 == null) {
                        if (i22 == 90 || i22 == 270) {
                            float width2 = rectF3.width() / 2.0f;
                            float height2 = rectF3.height() / 2.0f;
                            float centerX2 = rectF3.centerX();
                            float centerY2 = rectF3.centerY();
                            bitmapDrawable3.setBounds((int) (centerX2 - height2), (int) (centerY2 - width2), (int) (centerX2 + height2), (int) (centerY2 + width2));
                        } else {
                            bitmapDrawable3.setBounds((int) rectF3.left, (int) rectF3.top, (int) rectF3.right, (int) rectF3.bottom);
                        }
                    }
                    if (this.isVisible) {
                        if (i21 >= 29) {
                            try {
                                if (this.blendMode != null) {
                                    bitmapDrawable3.getPaint().setBlendMode((BlendMode) this.blendMode);
                                } else {
                                    bitmapDrawable3.getPaint().setBlendMode((BlendMode) null);
                                }
                            } catch (Exception e3) {
                                onBitmapException(bitmapDrawable3);
                                FileLog.e((Throwable) e3);
                            }
                        }
                        drawBitmapDrawable(canvas3, bitmapDrawable3, backgroundThreadDrawHolder3, i20);
                    }
                    canvas.restore();
                }
            } else if (this.isAspectFit) {
                float max2 = Math.max(f8, f9);
                float var_ = (float) ((int) (((float) i4) / max2));
                float var_ = (float) ((int) (((float) i5) / max2));
                RectF rectF5 = rectF2;
                rectF5.set(((f - var_) / 2.0f) + f4, ((f2 - var_) / 2.0f) + f3, f4 + ((f + var_) / 2.0f), f3 + ((f2 + var_) / 2.0f));
                if (this.isVisible) {
                    this.shaderMatrix.reset();
                    this.shaderMatrix.setTranslate((float) ((int) rectF5.left), (float) ((int) rectF5.top));
                    float var_ = 1.0f / max2;
                    this.shaderMatrix.preScale(var_, var_);
                    bitmapShader2.setLocalMatrix(this.shaderMatrix);
                    this.roundPaint.setShader(bitmapShader2);
                    this.roundPaint.setAlpha(i);
                    this.roundRect.set(rectF5);
                    if (this.isRoundRect) {
                        try {
                            if (iArr2[0] == 0) {
                                canvas.drawRect(this.roundRect, this.roundPaint);
                            } else {
                                canvas.drawRoundRect(this.roundRect, (float) iArr2[0], (float) iArr2[0], this.roundPaint);
                            }
                        } catch (Exception e4) {
                            onBitmapException(bitmapDrawable2);
                            FileLog.e((Throwable) e4);
                        }
                    } else {
                        Canvas canvas4 = canvas;
                        int[] iArr4 = iArr2;
                        for (int i24 = 0; i24 < iArr4.length; i24++) {
                            float[] fArr = radii;
                            int i25 = i24 * 2;
                            fArr[i25] = (float) iArr4[i24];
                            fArr[i25 + 1] = (float) iArr4[i24];
                        }
                        this.roundPath.reset();
                        this.roundPath.addRoundRect(this.roundRect, radii, Path.Direction.CW);
                        this.roundPath.close();
                        canvas4.drawPath(this.roundPath, this.roundPaint);
                    }
                }
            } else {
                int i26 = i;
                RectF rectF6 = rectF2;
                if (this.legacyCanvas != null) {
                    i6 = i5;
                    this.roundRect.set(0.0f, 0.0f, (float) this.legacyBitmap.getWidth(), (float) this.legacyBitmap.getHeight());
                    this.legacyCanvas.drawBitmap(this.gradientBitmap, (Rect) null, this.roundRect, (Paint) null);
                    bitmapDrawable = bitmapDrawable2;
                    this.legacyCanvas.drawBitmap(bitmapDrawable2.getBitmap(), (Rect) null, this.roundRect, this.legacyPaint);
                } else {
                    i6 = i5;
                    bitmapDrawable = bitmapDrawable2;
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
                float min = 1.0f / Math.min(f8, f9);
                RectF rectF7 = this.roundRect;
                float var_ = this.sideClip;
                float var_ = f;
                rectF7.set(f4 + var_, f3 + var_, (f4 + f) - var_, (f3 + f2) - var_);
                if (Math.abs(f8 - f9) > 5.0E-4f) {
                    float var_ = ((float) i4) / f9;
                    if (var_ > f6) {
                        float var_ = (float) ((int) var_);
                        rectF6.set(f4 - ((var_ - f6) / 2.0f), f3, ((var_ + f6) / 2.0f) + f4, f3 + f7);
                    } else {
                        float var_ = (float) ((int) (((float) i6) / f8));
                        rectF6.set(f4, f3 - ((var_ - f7) / 2.0f), f4 + f6, ((var_ + f7) / 2.0f) + f3);
                    }
                } else {
                    rectF6.set(f4, f3, f4 + f6, f3 + f7);
                }
                if (this.isVisible) {
                    this.shaderMatrix.reset();
                    Matrix matrix = this.shaderMatrix;
                    float var_ = rectF6.left;
                    float var_ = this.sideClip;
                    matrix.setTranslate((float) ((int) (var_ + var_)), (float) ((int) (rectF6.top + var_)));
                    int i27 = i2;
                    if (i27 == 90) {
                        this.shaderMatrix.preRotate(90.0f);
                        this.shaderMatrix.preTranslate(0.0f, -rectF6.width());
                    } else if (i27 == 180) {
                        this.shaderMatrix.preRotate(180.0f);
                        this.shaderMatrix.preTranslate(-rectF6.width(), -rectF6.height());
                    } else if (i27 == 270) {
                        this.shaderMatrix.preRotate(270.0f);
                        this.shaderMatrix.preTranslate(-rectF6.height(), 0.0f);
                    }
                    this.shaderMatrix.preScale(min, min);
                    if (this.isRoundVideo) {
                        float var_ = (f6 + ((float) (AndroidUtilities.roundMessageInset * 2))) / f6;
                        this.shaderMatrix.postScale(var_, var_, rectF6.centerX(), rectF6.centerY());
                    }
                    BitmapShader bitmapShader3 = this.legacyShader;
                    if (bitmapShader3 != null) {
                        bitmapShader3.setLocalMatrix(this.shaderMatrix);
                    }
                    bitmapShader2.setLocalMatrix(this.shaderMatrix);
                    if (this.composeShader != null) {
                        int width3 = this.gradientBitmap.getWidth();
                        int height3 = this.gradientBitmap.getHeight();
                        float var_ = var_ == 0.0f ? 1.0f : ((float) width3) / f6;
                        float var_ = f2 == 0.0f ? 1.0f : ((float) height3) / f7;
                        if (Math.abs(var_ - var_) > 5.0E-4f) {
                            float var_ = ((float) width3) / var_;
                            if (var_ > f6) {
                                width3 = (int) var_;
                                float var_ = (float) width3;
                                rectF6.set(f4 - ((var_ - f6) / 2.0f), f3, f4 + ((var_ + f6) / 2.0f), f3 + f7);
                            } else {
                                height3 = (int) (((float) height3) / var_);
                                float var_ = (float) height3;
                                rectF6.set(f4, f3 - ((var_ - f7) / 2.0f), f4 + f6, f3 + ((var_ + f7) / 2.0f));
                            }
                        } else {
                            rectF6.set(f4, f3, f4 + f6, f3 + f7);
                        }
                        float min2 = 1.0f / Math.min(var_ == 0.0f ? 1.0f : ((float) width3) / f6, f2 == 0.0f ? 1.0f : ((float) height3) / f7);
                        this.shaderMatrix.reset();
                        Matrix matrix2 = this.shaderMatrix;
                        float var_ = rectF6.left;
                        float var_ = this.sideClip;
                        matrix2.setTranslate(var_ + var_, rectF6.top + var_);
                        this.shaderMatrix.preScale(min2, min2);
                        this.gradientShader.setLocalMatrix(this.shaderMatrix);
                    }
                    this.roundPaint.setAlpha(i);
                    if (this.isRoundRect) {
                        try {
                            if (iArr2[0] == 0) {
                                canvas.drawRect(this.roundRect, this.roundPaint);
                            } else {
                                int[] iArr5 = iArr2;
                                canvas.drawRoundRect(this.roundRect, (float) iArr5[0], (float) iArr5[0], this.roundPaint);
                            }
                        } catch (Exception e5) {
                            if (backgroundThreadDrawHolder == null) {
                                onBitmapException(bitmapDrawable);
                            }
                            FileLog.e((Throwable) e5);
                        }
                    } else {
                        Canvas canvas5 = canvas;
                        int[] iArr6 = iArr2;
                        for (int i28 = 0; i28 < iArr6.length; i28++) {
                            float[] fArr2 = radii;
                            int i29 = i28 * 2;
                            fArr2[i29] = (float) iArr6[i28];
                            fArr2[i29 + 1] = (float) iArr6[i28];
                        }
                        this.roundPath.reset();
                        this.roundPath.addRoundRect(this.roundRect, radii, Path.Direction.CW);
                        this.roundPath.close();
                        canvas5.drawPath(this.roundPath, this.roundPaint);
                    }
                }
            }
            if (z) {
                ((RLottieDrawable) drawable).skipFrameUpdate = false;
                return;
            }
            Drawable drawable3 = drawable;
            if (drawable3 instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) drawable3).skipFrameUpdate = false;
                return;
            }
            return;
        }
        Canvas canvas6 = canvas;
        int i30 = i7;
        Drawable drawable4 = drawable2;
        BackgroundThreadDrawHolder backgroundThreadDrawHolder4 = backgroundThreadDrawHolder2;
        float var_ = f;
        RectF rectF8 = rectF;
        if (backgroundThreadDrawHolder4 == null) {
            if (this.isAspectFit) {
                int intrinsicWidth = drawable.getIntrinsicWidth();
                int intrinsicHeight = drawable.getIntrinsicHeight();
                float var_ = this.sideClip;
                float max3 = Math.max(var_ == 0.0f ? 1.0f : ((float) intrinsicWidth) / (var_ - (var_ * 2.0f)), f2 == 0.0f ? 1.0f : ((float) intrinsicHeight) / (f2 - (var_ * 2.0f)));
                float var_ = (float) ((int) (((float) intrinsicWidth) / max3));
                float var_ = (float) ((int) (((float) intrinsicHeight) / max3));
                rectF8.set(((var_ - var_) / 2.0f) + f4, ((f2 - var_) / 2.0f) + f3, f4 + ((var_ + var_) / 2.0f), f3 + ((f2 + var_) / 2.0f));
            } else {
                rectF8.set(f4, f3, f4 + var_, f2 + f3);
            }
            drawable4.setBounds((int) rectF8.left, (int) rectF8.top, (int) rectF8.right, (int) rectF8.bottom);
        }
        if (this.isVisible) {
            try {
                drawable.setAlpha(i);
                if (backgroundThreadDrawHolder4 == null) {
                    drawable4.draw(canvas6);
                } else if (drawable4 instanceof SvgHelper.SvgDrawable) {
                    long j = backgroundThreadDrawHolder4.time;
                    if (j == 0) {
                        j = System.currentTimeMillis();
                    }
                    ((SvgHelper.SvgDrawable) drawable4).drawInternal(canvas, true, j, backgroundThreadDrawHolder4.imageX, backgroundThreadDrawHolder4.imageY, backgroundThreadDrawHolder4.imageW, backgroundThreadDrawHolder4.imageH);
                } else {
                    drawable4.draw(canvas6);
                }
            } catch (Exception e6) {
                FileLog.e((Throwable) e6);
            }
        }
    }

    private void drawBitmapDrawable(Canvas canvas, BitmapDrawable bitmapDrawable, BackgroundThreadDrawHolder backgroundThreadDrawHolder, int i) {
        if (backgroundThreadDrawHolder == null) {
            bitmapDrawable.setAlpha(i);
            if (bitmapDrawable instanceof RLottieDrawable) {
                ((RLottieDrawable) bitmapDrawable).drawInternal(canvas, false, this.currentTime);
            } else if (bitmapDrawable instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) bitmapDrawable).drawInternal(canvas, false, this.currentTime);
            } else {
                bitmapDrawable.draw(canvas);
            }
        } else if (bitmapDrawable instanceof RLottieDrawable) {
            ((RLottieDrawable) bitmapDrawable).drawInBackground(canvas, backgroundThreadDrawHolder.imageX, backgroundThreadDrawHolder.imageY, backgroundThreadDrawHolder.imageW, backgroundThreadDrawHolder.imageH, i, backgroundThreadDrawHolder.colorFilter);
        } else if (bitmapDrawable instanceof AnimatedFileDrawable) {
            ((AnimatedFileDrawable) bitmapDrawable).drawInBackground(canvas, backgroundThreadDrawHolder.imageX, backgroundThreadDrawHolder.imageY, backgroundThreadDrawHolder.imageW, backgroundThreadDrawHolder.imageH, i, backgroundThreadDrawHolder.colorFilter);
        } else {
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null) {
                if (backgroundThreadDrawHolder.paint == null) {
                    backgroundThreadDrawHolder.paint = new Paint(1);
                }
                backgroundThreadDrawHolder.paint.setAlpha(i);
                backgroundThreadDrawHolder.paint.setColorFilter(backgroundThreadDrawHolder.colorFilter);
                canvas.save();
                canvas.translate(backgroundThreadDrawHolder.imageX, backgroundThreadDrawHolder.imageY);
                canvas.scale(backgroundThreadDrawHolder.imageW / ((float) bitmap.getWidth()), backgroundThreadDrawHolder.imageH / ((float) bitmap.getHeight()));
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, backgroundThreadDrawHolder.paint);
                canvas.restore();
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

    private void checkAlphaAnimation(boolean z, BackgroundThreadDrawHolder backgroundThreadDrawHolder) {
        if (!this.manualAlphaAnimator) {
            float f = this.currentAlpha;
            if (f != 1.0f) {
                if (!z) {
                    if (backgroundThreadDrawHolder != null) {
                        long currentTimeMillis = System.currentTimeMillis();
                        long j = this.lastUpdateAlphaTime;
                        long j2 = currentTimeMillis - j;
                        if (j == 0) {
                            j2 = 16;
                        }
                        if (j2 > 30 && AndroidUtilities.screenRefreshRate > 60.0f) {
                            j2 = 30;
                        }
                        this.currentAlpha += ((float) j2) / ((float) this.crossfadeDuration);
                    } else {
                        this.currentAlpha = f + (16.0f / ((float) this.crossfadeDuration));
                    }
                    if (this.currentAlpha > 1.0f) {
                        this.currentAlpha = 1.0f;
                        this.previousAlpha = 1.0f;
                        if (this.crossfadeImage != null) {
                            recycleBitmap((String) null, 2);
                            this.crossfadeShader = null;
                        }
                    }
                }
                if (backgroundThreadDrawHolder != null) {
                    AndroidUtilities.runOnUIThread(new ImageReceiver$$ExternalSyntheticLambda0(this));
                } else {
                    invalidate();
                }
            }
        }
    }

    public boolean draw(Canvas canvas) {
        return draw(canvas, (BackgroundThreadDrawHolder) null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:122:0x027d A[Catch:{ Exception -> 0x014a }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean draw(android.graphics.Canvas r41, org.telegram.messenger.ImageReceiver.BackgroundThreadDrawHolder r42) {
        /*
            r40 = this;
            r15 = r40
            r14 = r41
            r0 = r42
            android.graphics.Bitmap r1 = r15.gradientBitmap
            if (r1 == 0) goto L_0x0023
            java.lang.String r1 = r15.currentImageKey
            if (r1 == 0) goto L_0x0023
            r41.save()
            float r1 = r15.imageX
            float r2 = r15.imageY
            float r3 = r15.imageW
            float r3 = r3 + r1
            float r4 = r15.imageH
            float r4 = r4 + r2
            r14.clipRect(r1, r2, r3, r4)
            r1 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r14.drawColor(r1)
        L_0x0023:
            r21 = 0
            if (r0 == 0) goto L_0x002a
            r22 = 1
            goto L_0x002c
        L_0x002a:
            r22 = 0
        L_0x002c:
            if (r22 == 0) goto L_0x0094
            org.telegram.ui.Components.AnimatedFileDrawable r1 = r42.animation     // Catch:{ Exception -> 0x0309 }
            org.telegram.ui.Components.RLottieDrawable r2 = r42.lottieDrawable     // Catch:{ Exception -> 0x0309 }
            int[] r3 = r42.roundRadius     // Catch:{ Exception -> 0x0309 }
            android.graphics.drawable.Drawable r4 = r42.mediaDrawable     // Catch:{ Exception -> 0x0309 }
            android.graphics.BitmapShader r5 = r42.mediaShader     // Catch:{ Exception -> 0x0309 }
            android.graphics.drawable.Drawable r6 = r42.imageDrawable     // Catch:{ Exception -> 0x0309 }
            android.graphics.BitmapShader r7 = r42.imageShader     // Catch:{ Exception -> 0x0309 }
            android.graphics.BitmapShader r8 = r42.thumbShader     // Catch:{ Exception -> 0x0309 }
            android.graphics.drawable.Drawable r9 = r42.crossfadeImage     // Catch:{ Exception -> 0x0309 }
            boolean r10 = r42.crossfadeWithOldImage     // Catch:{ Exception -> 0x0309 }
            boolean r11 = r42.crossfadingWithThumb     // Catch:{ Exception -> 0x0309 }
            android.graphics.drawable.Drawable r12 = r42.thumbDrawable     // Catch:{ Exception -> 0x0309 }
            android.graphics.drawable.Drawable r16 = r42.staticThumbDrawable     // Catch:{ Exception -> 0x0309 }
            float r17 = r42.currentAlpha     // Catch:{ Exception -> 0x0309 }
            float r18 = r42.previousAlpha     // Catch:{ Exception -> 0x0309 }
            android.graphics.BitmapShader r19 = r42.crossfadeShader     // Catch:{ Exception -> 0x0309 }
            boolean r13 = r0.animationNotReady     // Catch:{ Exception -> 0x0309 }
            r23 = r1
            float r1 = r0.overrideAlpha     // Catch:{ Exception -> 0x0309 }
            r24 = r5
            r25 = r7
            r26 = r8
            r7 = r9
            r27 = r11
            r28 = r13
            r11 = r16
            r29 = r17
            r30 = r18
            r31 = r19
            r13 = r23
            r23 = r1
            r9 = r4
            r8 = r6
            r16 = r10
            r6 = r12
            r12 = r2
            r10 = r3
            goto L_0x00f4
        L_0x0094:
            org.telegram.ui.Components.AnimatedFileDrawable r1 = r40.getAnimation()     // Catch:{ Exception -> 0x0309 }
            org.telegram.ui.Components.RLottieDrawable r2 = r40.getLottieAnimation()     // Catch:{ Exception -> 0x0309 }
            int[] r3 = r15.roundRadius     // Catch:{ Exception -> 0x0309 }
            android.graphics.drawable.Drawable r4 = r15.currentMediaDrawable     // Catch:{ Exception -> 0x0309 }
            android.graphics.BitmapShader r5 = r15.mediaShader     // Catch:{ Exception -> 0x0309 }
            android.graphics.drawable.Drawable r6 = r15.currentImageDrawable     // Catch:{ Exception -> 0x0309 }
            android.graphics.BitmapShader r7 = r15.imageShader     // Catch:{ Exception -> 0x0309 }
            android.graphics.drawable.Drawable r12 = r15.currentThumbDrawable     // Catch:{ Exception -> 0x0309 }
            android.graphics.BitmapShader r8 = r15.thumbShader     // Catch:{ Exception -> 0x0309 }
            boolean r9 = r15.crossfadeWithOldImage     // Catch:{ Exception -> 0x0309 }
            boolean r11 = r15.crossfadingWithThumb     // Catch:{ Exception -> 0x0309 }
            android.graphics.drawable.Drawable r10 = r15.crossfadeImage     // Catch:{ Exception -> 0x0309 }
            android.graphics.drawable.Drawable r13 = r15.staticThumbDrawable     // Catch:{ Exception -> 0x0309 }
            r16 = r3
            float r3 = r15.currentAlpha     // Catch:{ Exception -> 0x0309 }
            r17 = r3
            float r3 = r15.previousAlpha     // Catch:{ Exception -> 0x0309 }
            r18 = r3
            android.graphics.BitmapShader r3 = r15.crossfadeShader     // Catch:{ Exception -> 0x0309 }
            r19 = r3
            float r3 = r15.overrideAlpha     // Catch:{ Exception -> 0x0309 }
            if (r1 == 0) goto L_0x00ca
            boolean r23 = r1.hasBitmap()     // Catch:{ Exception -> 0x0309 }
            if (r23 == 0) goto L_0x00d2
        L_0x00ca:
            if (r2 == 0) goto L_0x00d5
            boolean r23 = r2.hasBitmap()     // Catch:{ Exception -> 0x0309 }
            if (r23 != 0) goto L_0x00d5
        L_0x00d2:
            r23 = 1
            goto L_0x00d7
        L_0x00d5:
            r23 = 0
        L_0x00d7:
            r24 = r5
            r25 = r7
            r26 = r8
            r7 = r10
            r27 = r11
            r11 = r13
            r10 = r16
            r29 = r17
            r30 = r18
            r31 = r19
            r28 = r23
            r13 = r1
            r23 = r3
            r8 = r6
            r16 = r9
            r6 = r12
            r12 = r2
            r9 = r4
        L_0x00f4:
            r1 = r40
            r2 = r41
            r3 = r13
            r4 = r12
            r5 = r9
            r32 = r6
            r6 = r24
            r33 = r7
            r7 = r8
            r0 = r8
            r8 = r25
            r34 = r0
            r0 = r9
            r9 = r32
            r35 = r10
            r10 = r26
            r36 = r11
            r11 = r16
            r37 = r0
            r0 = r12
            r12 = r27
            r38 = r0
            r39 = r13
            r0 = 1
            r13 = r33
            r14 = r31
            r15 = r36
            r16 = r29
            r17 = r30
            r18 = r23
            r19 = r35
            r20 = r42
            boolean r1 = r1.customDraw(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)     // Catch:{ Exception -> 0x0305 }
            if (r1 == 0) goto L_0x0133
            return r0
        L_0x0133:
            r1 = r39
            if (r1 == 0) goto L_0x013c
            r3 = r35
            r1.setRoundRadius(r3)     // Catch:{ Exception -> 0x0305 }
        L_0x013c:
            if (r38 == 0) goto L_0x014d
            if (r22 != 0) goto L_0x014d
            r8 = r40
            android.view.View r2 = r8.parentView     // Catch:{ Exception -> 0x014a }
            r3 = r38
            r3.setCurrentParentView(r2)     // Catch:{ Exception -> 0x014a }
            goto L_0x0151
        L_0x014a:
            r0 = move-exception
            goto L_0x030b
        L_0x014d:
            r8 = r40
            r3 = r38
        L_0x0151:
            if (r1 != 0) goto L_0x0155
            if (r3 == 0) goto L_0x0166
        L_0x0155:
            if (r28 != 0) goto L_0x0166
            boolean r1 = r8.animationReadySent     // Catch:{ Exception -> 0x014a }
            if (r1 != 0) goto L_0x0166
            if (r22 != 0) goto L_0x0166
            r8.animationReadySent = r0     // Catch:{ Exception -> 0x014a }
            org.telegram.messenger.ImageReceiver$ImageReceiverDelegate r1 = r8.delegate     // Catch:{ Exception -> 0x014a }
            if (r1 == 0) goto L_0x0166
            r1.onAnimationReady(r8)     // Catch:{ Exception -> 0x014a }
        L_0x0166:
            boolean r1 = r8.forcePreview     // Catch:{ Exception -> 0x014a }
            if (r1 != 0) goto L_0x0181
            if (r37 == 0) goto L_0x0181
            if (r28 != 0) goto L_0x0181
            int r1 = r8.imageOrientation     // Catch:{ Exception -> 0x014a }
            r10 = r1
            r1 = r26
            r15 = r28
            r12 = r32
            r9 = r33
            r13 = r36
            r14 = r37
            r26 = r24
            goto L_0x01e2
        L_0x0181:
            if (r1 != 0) goto L_0x019a
            if (r34 == 0) goto L_0x019a
            if (r28 == 0) goto L_0x0189
            if (r37 == 0) goto L_0x019a
        L_0x0189:
            int r1 = r8.imageOrientation     // Catch:{ Exception -> 0x014a }
            r10 = r1
            r1 = r26
            r12 = r32
            r9 = r33
            r14 = r34
            r13 = r36
            r15 = 0
            r26 = r25
            goto L_0x01e2
        L_0x019a:
            r9 = r33
            if (r9 == 0) goto L_0x01af
            if (r27 != 0) goto L_0x01af
            int r1 = r8.imageOrientation     // Catch:{ Exception -> 0x014a }
            r10 = r1
            r14 = r9
            r1 = r26
            r15 = r28
            r26 = r31
            r12 = r32
            r13 = r36
            goto L_0x01e2
        L_0x01af:
            r13 = r36
            boolean r1 = r13 instanceof android.graphics.drawable.BitmapDrawable     // Catch:{ Exception -> 0x014a }
            if (r1 == 0) goto L_0x01cd
            boolean r1 = r8.useRoundForThumb     // Catch:{ Exception -> 0x014a }
            if (r1 == 0) goto L_0x01c2
            if (r26 != 0) goto L_0x01c2
            r8.updateDrawableRadius(r13)     // Catch:{ Exception -> 0x014a }
            android.graphics.BitmapShader r1 = r8.thumbShader     // Catch:{ Exception -> 0x014a }
            r26 = r1
        L_0x01c2:
            int r1 = r8.thumbOrientation     // Catch:{ Exception -> 0x014a }
            r10 = r1
            r14 = r13
            r1 = r26
            r15 = r28
            r12 = r32
            goto L_0x01e2
        L_0x01cd:
            r12 = r32
            if (r12 == 0) goto L_0x01da
            int r1 = r8.thumbOrientation     // Catch:{ Exception -> 0x014a }
            r10 = r1
            r14 = r12
            r1 = r26
            r15 = r28
            goto L_0x01e2
        L_0x01da:
            r1 = r26
            r15 = r28
            r10 = 0
            r14 = 0
            r26 = 0
        L_0x01e2:
            r16 = 1132396544(0x437var_, float:255.0)
            if (r14 == 0) goto L_0x02de
            byte r2 = r8.crossfadeAlpha     // Catch:{ Exception -> 0x014a }
            if (r2 == 0) goto L_0x02bd
            r17 = 1065353216(0x3var_, float:1.0)
            int r2 = (r30 > r17 ? 1 : (r30 == r17 ? 0 : -1))
            if (r2 == 0) goto L_0x021f
            r7 = r34
            r6 = r37
            if (r14 == r7) goto L_0x01f8
            if (r14 != r6) goto L_0x021c
        L_0x01f8:
            if (r13 == 0) goto L_0x021c
            boolean r2 = r8.useRoundForThumb     // Catch:{ Exception -> 0x014a }
            if (r2 == 0) goto L_0x0205
            if (r1 != 0) goto L_0x0205
            r8.updateDrawableRadius(r13)     // Catch:{ Exception -> 0x014a }
            android.graphics.BitmapShader r1 = r8.thumbShader     // Catch:{ Exception -> 0x014a }
        L_0x0205:
            r18 = r1
            float r1 = r23 * r16
            int r4 = (int) r1     // Catch:{ Exception -> 0x014a }
            r1 = r40
            r2 = r41
            r3 = r13
            r5 = r18
            r0 = r6
            r6 = r10
            r11 = r7
            r7 = r42
            r1.drawDrawable(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x014a }
            r1 = r18
            goto L_0x0223
        L_0x021c:
            r0 = r6
            r11 = r7
            goto L_0x0223
        L_0x021f:
            r11 = r34
            r0 = r37
        L_0x0223:
            boolean r2 = r8.crossfadeWithThumb     // Catch:{ Exception -> 0x014a }
            if (r2 == 0) goto L_0x023b
            if (r15 == 0) goto L_0x023b
            float r0 = r23 * r16
            int r4 = (int) r0     // Catch:{ Exception -> 0x014a }
            r1 = r40
            r2 = r41
            r3 = r14
            r5 = r26
            r6 = r10
            r7 = r42
            r1.drawDrawable(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x014a }
            goto L_0x02cd
        L_0x023b:
            if (r2 == 0) goto L_0x02aa
            int r2 = (r29 > r17 ? 1 : (r29 == r17 ? 0 : -1))
            if (r2 == 0) goto L_0x02aa
            if (r14 == r11) goto L_0x025e
            if (r14 != r0) goto L_0x0246
            goto L_0x025e
        L_0x0246:
            if (r14 == r12) goto L_0x0250
            if (r14 != r9) goto L_0x024b
            goto L_0x0250
        L_0x024b:
            if (r14 != r13) goto L_0x0279
            if (r9 == 0) goto L_0x0279
            goto L_0x0260
        L_0x0250:
            if (r13 == 0) goto L_0x0279
            boolean r0 = r8.useRoundForThumb     // Catch:{ Exception -> 0x014a }
            if (r0 == 0) goto L_0x0276
            if (r1 != 0) goto L_0x0276
            r8.updateDrawableRadius(r13)     // Catch:{ Exception -> 0x014a }
            android.graphics.BitmapShader r1 = r8.thumbShader     // Catch:{ Exception -> 0x014a }
            goto L_0x0276
        L_0x025e:
            if (r9 == 0) goto L_0x0264
        L_0x0260:
            r11 = r9
            r5 = r31
            goto L_0x027b
        L_0x0264:
            if (r12 == 0) goto L_0x0269
            r5 = r1
            r11 = r12
            goto L_0x027b
        L_0x0269:
            if (r13 == 0) goto L_0x0279
            boolean r0 = r8.useRoundForThumb     // Catch:{ Exception -> 0x014a }
            if (r0 == 0) goto L_0x0276
            if (r1 != 0) goto L_0x0276
            r8.updateDrawableRadius(r13)     // Catch:{ Exception -> 0x014a }
            android.graphics.BitmapShader r1 = r8.thumbShader     // Catch:{ Exception -> 0x014a }
        L_0x0276:
            r5 = r1
            r11 = r13
            goto L_0x027b
        L_0x0279:
            r5 = 0
            r11 = 0
        L_0x027b:
            if (r11 == 0) goto L_0x02aa
            boolean r0 = r11 instanceof org.telegram.messenger.SvgHelper.SvgDrawable     // Catch:{ Exception -> 0x014a }
            if (r0 != 0) goto L_0x028b
            boolean r0 = r11 instanceof org.telegram.messenger.Emoji.EmojiDrawable     // Catch:{ Exception -> 0x014a }
            if (r0 == 0) goto L_0x0286
            goto L_0x028b
        L_0x0286:
            float r30 = r30 * r23
            float r0 = r30 * r16
            goto L_0x0291
        L_0x028b:
            float r0 = r23 * r16
            float r17 = r17 - r29
            float r0 = r0 * r17
        L_0x0291:
            int r0 = (int) r0     // Catch:{ Exception -> 0x014a }
            int r6 = r8.thumbOrientation     // Catch:{ Exception -> 0x014a }
            r1 = r40
            r2 = r41
            r3 = r11
            r4 = r0
            r7 = r42
            r1.drawDrawable(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x014a }
            r1 = 255(0xff, float:3.57E-43)
            if (r0 == r1) goto L_0x02aa
            boolean r0 = r11 instanceof org.telegram.messenger.Emoji.EmojiDrawable     // Catch:{ Exception -> 0x014a }
            if (r0 == 0) goto L_0x02aa
            r11.setAlpha(r1)     // Catch:{ Exception -> 0x014a }
        L_0x02aa:
            float r23 = r23 * r29
            float r0 = r23 * r16
            int r4 = (int) r0     // Catch:{ Exception -> 0x014a }
            r1 = r40
            r2 = r41
            r3 = r14
            r5 = r26
            r6 = r10
            r7 = r42
            r1.drawDrawable(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x014a }
            goto L_0x02cd
        L_0x02bd:
            float r0 = r23 * r16
            int r4 = (int) r0     // Catch:{ Exception -> 0x014a }
            r1 = r40
            r2 = r41
            r3 = r14
            r5 = r26
            r6 = r10
            r7 = r42
            r1.drawDrawable(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x014a }
        L_0x02cd:
            if (r15 == 0) goto L_0x02d7
            boolean r0 = r8.crossfadeWithThumb     // Catch:{ Exception -> 0x014a }
            if (r0 == 0) goto L_0x02d7
            r0 = r42
            r13 = 1
            goto L_0x02da
        L_0x02d7:
            r0 = r42
            r13 = 0
        L_0x02da:
            r8.checkAlphaAnimation(r13, r0)     // Catch:{ Exception -> 0x014a }
            goto L_0x02f5
        L_0x02de:
            r0 = r42
            if (r13 == 0) goto L_0x02f8
            float r1 = r23 * r16
            int r4 = (int) r1     // Catch:{ Exception -> 0x014a }
            r5 = 0
            int r6 = r8.thumbOrientation     // Catch:{ Exception -> 0x014a }
            r1 = r40
            r2 = r41
            r3 = r13
            r7 = r42
            r1.drawDrawable(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x014a }
            r8.checkAlphaAnimation(r15, r0)     // Catch:{ Exception -> 0x014a }
        L_0x02f5:
            r21 = 1
            goto L_0x02fb
        L_0x02f8:
            r8.checkAlphaAnimation(r15, r0)     // Catch:{ Exception -> 0x014a }
        L_0x02fb:
            if (r14 != 0) goto L_0x030e
            if (r15 == 0) goto L_0x030e
            if (r22 != 0) goto L_0x030e
            r40.invalidate()     // Catch:{ Exception -> 0x014a }
            goto L_0x030e
        L_0x0305:
            r0 = move-exception
            r8 = r40
            goto L_0x030b
        L_0x0309:
            r0 = move-exception
            r8 = r15
        L_0x030b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x030e:
            android.graphics.Bitmap r0 = r8.gradientBitmap
            if (r0 == 0) goto L_0x0319
            java.lang.String r0 = r8.currentImageKey
            if (r0 == 0) goto L_0x0319
            r41.restore()
        L_0x0319:
            return r21
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageReceiver.draw(android.graphics.Canvas, org.telegram.messenger.ImageReceiver$BackgroundThreadDrawHolder):boolean");
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
        RLottieDrawable lottieAnimation = getLottieAnimation();
        if (lottieAnimation != null && lottieAnimation.hasBitmap()) {
            return lottieAnimation.getAnimatedBitmap();
        }
        AnimatedFileDrawable animation = getAnimation();
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
        if (animation != null && this.attachedToWindow) {
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

    public void setImageCoords(Rect rect) {
        if (rect != null) {
            this.imageX = (float) rect.left;
            this.imageY = (float) rect.top;
            this.imageW = (float) rect.width();
            this.imageH = (float) rect.height();
        }
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

    public long getSize() {
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

    public TLRPC$Document getQualityThumbDocument() {
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

    public void setAllowLottieVibration(boolean z) {
        this.allowLottieVibration = z;
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

    public void setAutoRepeatCount(int i) {
        this.autoRepeatCount = i;
        if (getLottieAnimation() != null) {
            getLottieAnimation().setAutoRepeatCount(i);
            return;
        }
        this.animatedFileDrawableRepeatMaxCount = i;
        if (getAnimation() != null) {
            getAnimation().repeatCount = 0;
        }
    }

    public void setAutoRepeatTimeout(long j) {
        this.autoRepeatTimeout = j;
        RLottieDrawable lottieAnimation = getLottieAnimation();
        if (lottieAnimation != null) {
            lottieAnimation.setAutoRepeatTimeout(this.autoRepeatTimeout);
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
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00b1, code lost:
        if ((r9 instanceof org.telegram.messenger.Emoji.EmojiDrawable) == false) goto L_0x008e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00c8  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00cd  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00e4  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x00e6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean setImageBitmapByKey(android.graphics.drawable.Drawable r8, java.lang.String r9, int r10, boolean r11, int r12) {
        /*
            r7 = this;
            r0 = 0
            if (r8 == 0) goto L_0x02a4
            if (r9 == 0) goto L_0x02a4
            int r1 = r7.currentGuid
            if (r1 == r12) goto L_0x000b
            goto L_0x02a4
        L_0x000b:
            r12 = 0
            r1 = 1065353216(0x3var_, float:1.0)
            r2 = 1
            if (r10 != 0) goto L_0x00f1
            java.lang.String r10 = r7.currentImageKey
            boolean r9 = r9.equals(r10)
            if (r9 != 0) goto L_0x001a
            return r0
        L_0x001a:
            boolean r9 = r8 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r9 != 0) goto L_0x0038
            org.telegram.messenger.ImageLoader r9 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r10 = r7.currentImageKey
            r9.incrementUseCount(r10)
            boolean r9 = r7.videoThumbIsSame
            if (r9 == 0) goto L_0x0059
            android.graphics.drawable.Drawable r9 = r7.currentImageDrawable
            if (r8 == r9) goto L_0x0036
            float r9 = r7.currentAlpha
            int r9 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r9 < 0) goto L_0x0036
            goto L_0x0059
        L_0x0036:
            r9 = 0
            goto L_0x005a
        L_0x0038:
            r9 = r8
            org.telegram.ui.Components.AnimatedFileDrawable r9 = (org.telegram.ui.Components.AnimatedFileDrawable) r9
            long r3 = r7.startTime
            long r5 = r7.endTime
            r9.setStartEndTime(r3, r5)
            boolean r10 = r9.isWebmSticker
            if (r10 == 0) goto L_0x004f
            org.telegram.messenger.ImageLoader r10 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r3 = r7.currentImageKey
            r10.incrementUseCount(r3)
        L_0x004f:
            boolean r10 = r7.videoThumbIsSame
            if (r10 == 0) goto L_0x0059
            boolean r9 = r9.hasBitmap()
            r9 = r9 ^ r2
            goto L_0x005a
        L_0x0059:
            r9 = 1
        L_0x005a:
            r7.currentImageDrawable = r8
            boolean r10 = r8 instanceof org.telegram.messenger.ExtendedBitmapDrawable
            if (r10 == 0) goto L_0x0069
            r10 = r8
            org.telegram.messenger.ExtendedBitmapDrawable r10 = (org.telegram.messenger.ExtendedBitmapDrawable) r10
            int r10 = r10.getOrientation()
            r7.imageOrientation = r10
        L_0x0069:
            r7.updateDrawableRadius(r8)
            if (r9 == 0) goto L_0x00eb
            boolean r9 = r7.isVisible
            if (r9 == 0) goto L_0x00eb
            if (r11 != 0) goto L_0x0078
            boolean r9 = r7.forcePreview
            if (r9 == 0) goto L_0x007c
        L_0x0078:
            boolean r9 = r7.forceCrossfade
            if (r9 == 0) goto L_0x00eb
        L_0x007c:
            int r9 = r7.crossfadeDuration
            if (r9 == 0) goto L_0x00eb
            android.graphics.drawable.Drawable r9 = r7.currentMediaDrawable
            boolean r10 = r9 instanceof org.telegram.ui.Components.RLottieDrawable
            if (r10 == 0) goto L_0x0090
            org.telegram.ui.Components.RLottieDrawable r9 = (org.telegram.ui.Components.RLottieDrawable) r9
            boolean r9 = r9.hasBitmap()
            if (r9 == 0) goto L_0x0090
        L_0x008e:
            r9 = 0
            goto L_0x00b4
        L_0x0090:
            android.graphics.drawable.Drawable r9 = r7.currentMediaDrawable
            boolean r10 = r9 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r10 == 0) goto L_0x009f
            org.telegram.ui.Components.AnimatedFileDrawable r9 = (org.telegram.ui.Components.AnimatedFileDrawable) r9
            boolean r9 = r9.hasBitmap()
            if (r9 == 0) goto L_0x009f
            goto L_0x008e
        L_0x009f:
            android.graphics.drawable.Drawable r9 = r7.currentImageDrawable
            boolean r9 = r9 instanceof org.telegram.ui.Components.RLottieDrawable
            if (r9 == 0) goto L_0x00b3
            android.graphics.drawable.Drawable r9 = r7.staticThumbDrawable
            boolean r10 = r9 instanceof org.telegram.ui.Components.LoadingStickerDrawable
            if (r10 != 0) goto L_0x00b3
            boolean r10 = r9 instanceof org.telegram.messenger.SvgHelper.SvgDrawable
            if (r10 != 0) goto L_0x00b3
            boolean r9 = r9 instanceof org.telegram.messenger.Emoji.EmojiDrawable
            if (r9 == 0) goto L_0x008e
        L_0x00b3:
            r9 = 1
        L_0x00b4:
            if (r9 == 0) goto L_0x021e
            android.graphics.drawable.Drawable r9 = r7.currentThumbDrawable
            if (r9 != 0) goto L_0x00c2
            android.graphics.drawable.Drawable r10 = r7.staticThumbDrawable
            if (r10 != 0) goto L_0x00c2
            boolean r10 = r7.forceCrossfade
            if (r10 == 0) goto L_0x021e
        L_0x00c2:
            if (r9 == 0) goto L_0x00cd
            android.graphics.drawable.Drawable r9 = r7.staticThumbDrawable
            if (r9 == 0) goto L_0x00cd
            float r9 = r7.currentAlpha
            r7.previousAlpha = r9
            goto L_0x00cf
        L_0x00cd:
            r7.previousAlpha = r1
        L_0x00cf:
            r7.currentAlpha = r12
            long r9 = java.lang.System.currentTimeMillis()
            r7.lastUpdateAlphaTime = r9
            android.graphics.drawable.Drawable r9 = r7.crossfadeImage
            if (r9 != 0) goto L_0x00e6
            android.graphics.drawable.Drawable r9 = r7.currentThumbDrawable
            if (r9 != 0) goto L_0x00e6
            android.graphics.drawable.Drawable r9 = r7.staticThumbDrawable
            if (r9 == 0) goto L_0x00e4
            goto L_0x00e6
        L_0x00e4:
            r9 = 0
            goto L_0x00e7
        L_0x00e6:
            r9 = 1
        L_0x00e7:
            r7.crossfadeWithThumb = r9
            goto L_0x021e
        L_0x00eb:
            r7.currentAlpha = r1
            r7.previousAlpha = r1
            goto L_0x021e
        L_0x00f1:
            r3 = 3
            if (r10 != r3) goto L_0x0195
            java.lang.String r10 = r7.currentMediaKey
            boolean r9 = r9.equals(r10)
            if (r9 != 0) goto L_0x00fd
            return r0
        L_0x00fd:
            boolean r9 = r8 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r9 != 0) goto L_0x010b
            org.telegram.messenger.ImageLoader r9 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r10 = r7.currentMediaKey
            r9.incrementUseCount(r10)
            goto L_0x0141
        L_0x010b:
            r9 = r8
            org.telegram.ui.Components.AnimatedFileDrawable r9 = (org.telegram.ui.Components.AnimatedFileDrawable) r9
            long r3 = r7.startTime
            long r5 = r7.endTime
            r9.setStartEndTime(r3, r5)
            boolean r10 = r9.isWebmSticker
            if (r10 == 0) goto L_0x0122
            org.telegram.messenger.ImageLoader r10 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r3 = r7.currentMediaKey
            r10.incrementUseCount(r3)
        L_0x0122:
            boolean r10 = r7.videoThumbIsSame
            if (r10 == 0) goto L_0x0141
            android.graphics.drawable.Drawable r10 = r7.currentThumbDrawable
            boolean r3 = r10 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r3 != 0) goto L_0x0132
            android.graphics.drawable.Drawable r3 = r7.currentImageDrawable
            boolean r3 = r3 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r3 == 0) goto L_0x0141
        L_0x0132:
            r3 = 0
            boolean r5 = r10 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r5 == 0) goto L_0x013e
            org.telegram.ui.Components.AnimatedFileDrawable r10 = (org.telegram.ui.Components.AnimatedFileDrawable) r10
            long r3 = r10.getLastFrameTimestamp()
        L_0x013e:
            r9.seekTo(r3, r2, r2)
        L_0x0141:
            r7.currentMediaDrawable = r8
            r7.updateDrawableRadius(r8)
            android.graphics.drawable.Drawable r9 = r7.currentImageDrawable
            if (r9 != 0) goto L_0x021e
            if (r11 != 0) goto L_0x0150
            boolean r9 = r7.forcePreview
            if (r9 == 0) goto L_0x0154
        L_0x0150:
            boolean r9 = r7.forceCrossfade
            if (r9 == 0) goto L_0x018f
        L_0x0154:
            android.graphics.drawable.Drawable r9 = r7.currentThumbDrawable
            if (r9 != 0) goto L_0x015c
            android.graphics.drawable.Drawable r10 = r7.staticThumbDrawable
            if (r10 == 0) goto L_0x0166
        L_0x015c:
            float r10 = r7.currentAlpha
            int r10 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r10 == 0) goto L_0x0166
            boolean r10 = r7.forceCrossfade
            if (r10 == 0) goto L_0x021e
        L_0x0166:
            if (r9 == 0) goto L_0x0171
            android.graphics.drawable.Drawable r9 = r7.staticThumbDrawable
            if (r9 == 0) goto L_0x0171
            float r9 = r7.currentAlpha
            r7.previousAlpha = r9
            goto L_0x0173
        L_0x0171:
            r7.previousAlpha = r1
        L_0x0173:
            r7.currentAlpha = r12
            long r9 = java.lang.System.currentTimeMillis()
            r7.lastUpdateAlphaTime = r9
            android.graphics.drawable.Drawable r9 = r7.crossfadeImage
            if (r9 != 0) goto L_0x018a
            android.graphics.drawable.Drawable r9 = r7.currentThumbDrawable
            if (r9 != 0) goto L_0x018a
            android.graphics.drawable.Drawable r9 = r7.staticThumbDrawable
            if (r9 == 0) goto L_0x0188
            goto L_0x018a
        L_0x0188:
            r9 = 0
            goto L_0x018b
        L_0x018a:
            r9 = 1
        L_0x018b:
            r7.crossfadeWithThumb = r9
            goto L_0x021e
        L_0x018f:
            r7.currentAlpha = r1
            r7.previousAlpha = r1
            goto L_0x021e
        L_0x0195:
            if (r10 != r2) goto L_0x021e
            android.graphics.drawable.Drawable r10 = r7.currentThumbDrawable
            if (r10 == 0) goto L_0x019c
            return r0
        L_0x019c:
            boolean r10 = r7.forcePreview
            if (r10 != 0) goto L_0x01be
            org.telegram.ui.Components.AnimatedFileDrawable r10 = r7.getAnimation()
            if (r10 == 0) goto L_0x01ad
            boolean r10 = r10.hasBitmap()
            if (r10 == 0) goto L_0x01ad
            return r0
        L_0x01ad:
            android.graphics.drawable.Drawable r10 = r7.currentImageDrawable
            if (r10 == 0) goto L_0x01b5
            boolean r10 = r10 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r10 == 0) goto L_0x01bd
        L_0x01b5:
            android.graphics.drawable.Drawable r10 = r7.currentMediaDrawable
            if (r10 == 0) goto L_0x01be
            boolean r10 = r10 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r10 != 0) goto L_0x01be
        L_0x01bd:
            return r0
        L_0x01be:
            java.lang.String r10 = r7.currentThumbKey
            boolean r9 = r9.equals(r10)
            if (r9 != 0) goto L_0x01c7
            return r0
        L_0x01c7:
            org.telegram.messenger.ImageLoader r9 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r10 = r7.currentThumbKey
            r9.incrementUseCount(r10)
            r7.currentThumbDrawable = r8
            boolean r9 = r8 instanceof org.telegram.messenger.ExtendedBitmapDrawable
            if (r9 == 0) goto L_0x01df
            r9 = r8
            org.telegram.messenger.ExtendedBitmapDrawable r9 = (org.telegram.messenger.ExtendedBitmapDrawable) r9
            int r9 = r9.getOrientation()
            r7.thumbOrientation = r9
        L_0x01df:
            r7.updateDrawableRadius(r8)
            if (r11 != 0) goto L_0x021a
            byte r9 = r7.crossfadeAlpha
            r10 = 2
            if (r9 == r10) goto L_0x021a
            java.lang.Object r9 = r7.currentParentObject
            boolean r10 = r9 instanceof org.telegram.messenger.MessageObject
            if (r10 == 0) goto L_0x0206
            org.telegram.messenger.MessageObject r9 = (org.telegram.messenger.MessageObject) r9
            boolean r9 = r9.isRoundVideo()
            if (r9 == 0) goto L_0x0206
            java.lang.Object r9 = r7.currentParentObject
            org.telegram.messenger.MessageObject r9 = (org.telegram.messenger.MessageObject) r9
            boolean r9 = r9.isSending()
            if (r9 == 0) goto L_0x0206
            r7.currentAlpha = r1
            r7.previousAlpha = r1
            goto L_0x021e
        L_0x0206:
            r7.currentAlpha = r12
            r7.previousAlpha = r1
            long r9 = java.lang.System.currentTimeMillis()
            r7.lastUpdateAlphaTime = r9
            android.graphics.drawable.Drawable r9 = r7.staticThumbDrawable
            if (r9 == 0) goto L_0x0216
            r9 = 1
            goto L_0x0217
        L_0x0216:
            r9 = 0
        L_0x0217:
            r7.crossfadeWithThumb = r9
            goto L_0x021e
        L_0x021a:
            r7.currentAlpha = r1
            r7.previousAlpha = r1
        L_0x021e:
            org.telegram.messenger.ImageReceiver$ImageReceiverDelegate r9 = r7.delegate
            if (r9 == 0) goto L_0x0242
            android.graphics.drawable.Drawable r10 = r7.currentImageDrawable
            if (r10 != 0) goto L_0x0235
            android.graphics.drawable.Drawable r12 = r7.currentThumbDrawable
            if (r12 != 0) goto L_0x0235
            android.graphics.drawable.Drawable r12 = r7.staticThumbDrawable
            if (r12 != 0) goto L_0x0235
            android.graphics.drawable.Drawable r12 = r7.currentMediaDrawable
            if (r12 == 0) goto L_0x0233
            goto L_0x0235
        L_0x0233:
            r12 = 0
            goto L_0x0236
        L_0x0235:
            r12 = 1
        L_0x0236:
            if (r10 != 0) goto L_0x023e
            android.graphics.drawable.Drawable r10 = r7.currentMediaDrawable
            if (r10 != 0) goto L_0x023e
            r10 = 1
            goto L_0x023f
        L_0x023e:
            r10 = 0
        L_0x023f:
            r9.didSetImage(r7, r12, r10, r11)
        L_0x0242:
            boolean r9 = r8 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r9 == 0) goto L_0x026e
            org.telegram.ui.Components.AnimatedFileDrawable r8 = (org.telegram.ui.Components.AnimatedFileDrawable) r8
            boolean r9 = r7.useSharedAnimationQueue
            r8.setUseSharedQueue(r9)
            boolean r9 = r7.attachedToWindow
            if (r9 == 0) goto L_0x0254
            r8.addParent(r7)
        L_0x0254:
            boolean r9 = r7.allowStartAnimation
            if (r9 == 0) goto L_0x025f
            int r9 = r7.currentOpenedLayerFlags
            if (r9 != 0) goto L_0x025f
            r8.checkRepeat()
        L_0x025f:
            boolean r9 = r7.allowDecodeSingleFrame
            r8.setAllowDecodeSingleFrame(r9)
            r7.animationReadySent = r0
            android.view.View r8 = r7.parentView
            if (r8 == 0) goto L_0x02a0
            r8.invalidate()
            goto L_0x02a0
        L_0x026e:
            boolean r9 = r8 instanceof org.telegram.ui.Components.RLottieDrawable
            if (r9 == 0) goto L_0x02a0
            org.telegram.ui.Components.RLottieDrawable r8 = (org.telegram.ui.Components.RLottieDrawable) r8
            boolean r9 = r7.attachedToWindow
            if (r9 == 0) goto L_0x027b
            r8.addParentView(r7)
        L_0x027b:
            boolean r9 = r7.allowStartLottieAnimation
            if (r9 == 0) goto L_0x028c
            boolean r9 = r8.isHeavyDrawable()
            if (r9 == 0) goto L_0x0289
            int r9 = r7.currentOpenedLayerFlags
            if (r9 != 0) goto L_0x028c
        L_0x0289:
            r8.start()
        L_0x028c:
            r8.setAllowDecodeSingleFrame(r2)
            int r9 = r7.autoRepeat
            r8.setAutoRepeat(r9)
            int r9 = r7.autoRepeatCount
            r8.setAutoRepeatCount(r9)
            long r9 = r7.autoRepeatTimeout
            r8.setAutoRepeatTimeout(r9)
            r7.animationReadySent = r0
        L_0x02a0:
            r7.invalidate()
            return r2
        L_0x02a4:
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
            ((RLottieDrawable) obj).removeParentView(this);
        }
        if (obj instanceof AnimatedFileDrawable) {
            ((AnimatedFileDrawable) obj).removeParent(this);
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
                } else if (animatedFileDrawable.getParents().isEmpty()) {
                    animatedFileDrawable.recycle();
                }
            } else if (obj instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) obj).getBitmap();
                boolean decrementUseCount3 = ImageLoader.getInstance().decrementUseCount(str2);
                if (!ImageLoader.getInstance().isInMemCache(str2, false) && decrementUseCount3) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(bitmap);
                    AndroidUtilities.recycleBitmaps(arrayList);
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
                    if (lottieAnimation2 != null) {
                        lottieAnimation2.setAllowVibration(this.allowLottieVibration);
                    }
                    if (this.allowStartLottieAnimation && lottieAnimation2 != null && lottieAnimation2.isHeavyDrawable()) {
                        lottieAnimation2.start();
                    }
                    AnimatedFileDrawable animation2 = getAnimation();
                    if (this.allowStartAnimation && animation2 != null) {
                        animation2.checkRepeat();
                        invalidate();
                    }
                }
            }
        }
    }

    public void startCrossfadeFromStaticThumb(Bitmap bitmap) {
        startCrossfadeFromStaticThumb((Drawable) new BitmapDrawable((Resources) null, bitmap));
    }

    public void startCrossfadeFromStaticThumb(Drawable drawable) {
        this.currentThumbKey = null;
        this.currentThumbDrawable = null;
        this.thumbShader = null;
        this.roundPaint.setShader((Shader) null);
        this.staticThumbDrawable = drawable;
        this.crossfadeWithThumb = true;
        this.currentAlpha = 0.0f;
        updateDrawableRadius(drawable);
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

    public void moveLottieToFront() {
        BitmapDrawable bitmapDrawable;
        BitmapDrawable bitmapDrawable2;
        String str;
        Drawable drawable = this.currentMediaDrawable;
        String str2 = null;
        if (drawable instanceof RLottieDrawable) {
            bitmapDrawable2 = (BitmapDrawable) drawable;
            str = this.currentMediaKey;
        } else {
            Drawable drawable2 = this.currentImageDrawable;
            if (drawable2 instanceof RLottieDrawable) {
                bitmapDrawable2 = (BitmapDrawable) drawable2;
                str = this.currentImageKey;
            } else {
                bitmapDrawable = null;
                if (str2 != null && bitmapDrawable != null) {
                    ImageLoader.getInstance().moveToFront(str2);
                    if (!ImageLoader.getInstance().isInMemCache(str2, true)) {
                        ImageLoader.getInstance().getLottieMemCahce().put(str2, bitmapDrawable);
                        return;
                    }
                    return;
                }
            }
        }
        BitmapDrawable bitmapDrawable3 = bitmapDrawable2;
        str2 = str;
        bitmapDrawable = bitmapDrawable3;
        if (str2 != null) {
        }
    }

    public View getParentView() {
        return this.parentView;
    }

    public boolean isAttachedToWindow() {
        return this.attachedToWindow;
    }

    public void setVideoThumbIsSame(boolean z) {
        this.videoThumbIsSame = z;
    }

    public void setAllowLoadingOnAttachedOnly(boolean z) {
        this.allowLoadingOnAttachedOnly = z;
    }

    public void setSkipUpdateFrame(boolean z) {
        this.skipUpdateFrame = z;
    }

    public void setCurrentTime(long j) {
        this.currentTime = j;
    }

    public void setFileLoadingPriority(int i) {
        this.fileLoadingPriority = i;
    }

    public int getFileLoadingPriority() {
        return this.fileLoadingPriority;
    }

    public BackgroundThreadDrawHolder setDrawInBackgroundThread(BackgroundThreadDrawHolder backgroundThreadDrawHolder) {
        if (backgroundThreadDrawHolder == null) {
            backgroundThreadDrawHolder = new BackgroundThreadDrawHolder();
        }
        AnimatedFileDrawable unused = backgroundThreadDrawHolder.animation = getAnimation();
        RLottieDrawable unused2 = backgroundThreadDrawHolder.lottieDrawable = getLottieAnimation();
        boolean z = false;
        for (int i = 0; i < 4; i++) {
            backgroundThreadDrawHolder.roundRadius[i] = this.roundRadius[i];
        }
        Drawable unused3 = backgroundThreadDrawHolder.mediaDrawable = this.currentMediaDrawable;
        BitmapShader unused4 = backgroundThreadDrawHolder.mediaShader = this.mediaShader;
        Drawable unused5 = backgroundThreadDrawHolder.imageDrawable = this.currentImageDrawable;
        BitmapShader unused6 = backgroundThreadDrawHolder.imageShader = this.imageShader;
        Drawable unused7 = backgroundThreadDrawHolder.thumbDrawable = this.currentThumbDrawable;
        BitmapShader unused8 = backgroundThreadDrawHolder.thumbShader = this.thumbShader;
        Drawable unused9 = backgroundThreadDrawHolder.staticThumbDrawable = this.staticThumbDrawable;
        Drawable unused10 = backgroundThreadDrawHolder.crossfadeImage = this.crossfadeImage;
        backgroundThreadDrawHolder.colorFilter = this.colorFilter;
        boolean unused11 = backgroundThreadDrawHolder.crossfadingWithThumb = this.crossfadingWithThumb;
        boolean unused12 = backgroundThreadDrawHolder.crossfadeWithOldImage = this.crossfadeWithOldImage;
        float unused13 = backgroundThreadDrawHolder.currentAlpha = this.currentAlpha;
        float unused14 = backgroundThreadDrawHolder.previousAlpha = this.previousAlpha;
        BitmapShader unused15 = backgroundThreadDrawHolder.crossfadeShader = this.crossfadeShader;
        if ((backgroundThreadDrawHolder.animation != null && !backgroundThreadDrawHolder.animation.hasBitmap()) || (backgroundThreadDrawHolder.lottieDrawable != null && !backgroundThreadDrawHolder.lottieDrawable.hasBitmap())) {
            z = true;
        }
        backgroundThreadDrawHolder.animationNotReady = z;
        backgroundThreadDrawHolder.imageX = this.imageX;
        backgroundThreadDrawHolder.imageY = this.imageY;
        backgroundThreadDrawHolder.imageW = this.imageW;
        backgroundThreadDrawHolder.imageH = this.imageH;
        backgroundThreadDrawHolder.overrideAlpha = this.overrideAlpha;
        return backgroundThreadDrawHolder;
    }

    public static class BackgroundThreadDrawHolder {
        /* access modifiers changed from: private */
        public AnimatedFileDrawable animation;
        public boolean animationNotReady;
        public ColorFilter colorFilter;
        /* access modifiers changed from: private */
        public Drawable crossfadeImage;
        /* access modifiers changed from: private */
        public BitmapShader crossfadeShader;
        /* access modifiers changed from: private */
        public boolean crossfadeWithOldImage;
        /* access modifiers changed from: private */
        public boolean crossfadingWithThumb;
        /* access modifiers changed from: private */
        public float currentAlpha;
        public RectF drawRegion = new RectF();
        /* access modifiers changed from: private */
        public Drawable imageDrawable;
        public float imageH;
        /* access modifiers changed from: private */
        public BitmapShader imageShader;
        public float imageW;
        public float imageX;
        public float imageY;
        /* access modifiers changed from: private */
        public RLottieDrawable lottieDrawable;
        /* access modifiers changed from: private */
        public Drawable mediaDrawable;
        /* access modifiers changed from: private */
        public BitmapShader mediaShader;
        public float overrideAlpha;
        Paint paint;
        /* access modifiers changed from: private */
        public float previousAlpha;
        /* access modifiers changed from: private */
        public Path roundPath;
        /* access modifiers changed from: private */
        public int[] roundRadius = new int[4];
        /* access modifiers changed from: private */
        public Drawable staticThumbDrawable;
        /* access modifiers changed from: private */
        public Drawable thumbDrawable;
        /* access modifiers changed from: private */
        public BitmapShader thumbShader;
        public long time;

        public void release() {
            this.animation = null;
            this.lottieDrawable = null;
            for (int i = 0; i < 4; i++) {
                int[] iArr = this.roundRadius;
                iArr[i] = iArr[i];
            }
            this.mediaDrawable = null;
            this.mediaShader = null;
            this.imageDrawable = null;
            this.imageShader = null;
            this.thumbDrawable = null;
            this.thumbShader = null;
            this.staticThumbDrawable = null;
            this.crossfadeImage = null;
            this.colorFilter = null;
        }

        public void setBounds(Rect rect) {
            if (rect != null) {
                this.imageX = (float) rect.left;
                this.imageY = (float) rect.top;
                this.imageW = (float) rect.width();
                this.imageH = (float) rect.height();
            }
        }

        public void getBounds(RectF rectF) {
            if (rectF != null) {
                float f = this.imageX;
                rectF.left = f;
                float f2 = this.imageY;
                rectF.top = f2;
                rectF.right = f + this.imageW;
                rectF.bottom = f2 + this.imageH;
            }
        }

        public void getBounds(Rect rect) {
            if (rect != null) {
                int i = (int) this.imageX;
                rect.left = i;
                int i2 = (int) this.imageY;
                rect.top = i2;
                rect.right = (int) (((float) i) + this.imageW);
                rect.bottom = (int) (((float) i2) + this.imageH);
            }
        }
    }
}
