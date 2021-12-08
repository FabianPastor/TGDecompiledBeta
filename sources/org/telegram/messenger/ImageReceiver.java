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
import java.util.ArrayList;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.LoadingStickerDrawable;
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
    private TLRPC.Document qulityThumbDocument;
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
        void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3);

        void onAnimationReady(ImageReceiver imageReceiver);

        /* renamed from: org.telegram.messenger.ImageReceiver$ImageReceiverDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onAnimationReady(ImageReceiverDelegate _this, ImageReceiver imageReceiver) {
            }
        }
    }

    public static class BitmapHolder {
        public Bitmap bitmap;
        public Drawable drawable;
        private String key;
        public int orientation;
        private boolean recycleOnRelease;

        public BitmapHolder(Bitmap b, String k, int o) {
            this.bitmap = b;
            this.key = k;
            this.orientation = o;
            if (k != null) {
                ImageLoader.getInstance().incrementUseCount(this.key);
            }
        }

        public BitmapHolder(Drawable d, String k, int o) {
            this.drawable = d;
            this.key = k;
            this.orientation = o;
            if (k != null) {
                ImageLoader.getInstance().incrementUseCount(this.key);
            }
        }

        public BitmapHolder(Bitmap b) {
            this.bitmap = b;
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
            boolean canDelete = ImageLoader.getInstance().decrementUseCount(this.key);
            if (!ImageLoader.getInstance().isInMemCache(this.key, false) && canDelete) {
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
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x001c, code lost:
            r0 = r1.mediaLocation;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x000e, code lost:
            r0 = r1.thumbLocation;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean isWebfileSet() {
            /*
                r1 = this;
                org.telegram.messenger.ImageLocation r0 = r1.imageLocation
                if (r0 == 0) goto L_0x000e
                org.telegram.messenger.WebFile r0 = r0.webFile
                if (r0 != 0) goto L_0x002a
                org.telegram.messenger.ImageLocation r0 = r1.imageLocation
                java.lang.String r0 = r0.path
                if (r0 != 0) goto L_0x002a
            L_0x000e:
                org.telegram.messenger.ImageLocation r0 = r1.thumbLocation
                if (r0 == 0) goto L_0x001c
                org.telegram.messenger.WebFile r0 = r0.webFile
                if (r0 != 0) goto L_0x002a
                org.telegram.messenger.ImageLocation r0 = r1.thumbLocation
                java.lang.String r0 = r0.path
                if (r0 != 0) goto L_0x002a
            L_0x001c:
                org.telegram.messenger.ImageLocation r0 = r1.mediaLocation
                if (r0 == 0) goto L_0x002c
                org.telegram.messenger.WebFile r0 = r0.webFile
                if (r0 != 0) goto L_0x002a
                org.telegram.messenger.ImageLocation r0 = r1.mediaLocation
                java.lang.String r0 = r0.path
                if (r0 == 0) goto L_0x002c
            L_0x002a:
                r0 = 1
                goto L_0x002d
            L_0x002c:
                r0 = 0
            L_0x002d:
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

    public void setForceLoading(boolean value) {
        this.forceLoding = value;
    }

    public boolean isForceLoding() {
        return this.forceLoding;
    }

    public void setStrippedLocation(ImageLocation location) {
        this.strippedLocation = location;
    }

    public void setIgnoreImageSet(boolean value) {
        this.ignoreImageSet = value;
    }

    public ImageLocation getStrippedLocation() {
        return this.strippedLocation;
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, Drawable thumb, String ext, Object parentObject, int cacheType) {
        setImage(imageLocation, imageFilter, (ImageLocation) null, (String) null, thumb, 0, ext, parentObject, cacheType);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, Drawable thumb, int size, String ext, Object parentObject, int cacheType) {
        setImage(imageLocation, imageFilter, (ImageLocation) null, (String) null, thumb, size, ext, parentObject, cacheType);
    }

    public void setImage(String imagePath, String imageFilter, Drawable thumb, String ext, int size) {
        setImage(ImageLocation.getForPath(imagePath), imageFilter, (ImageLocation) null, (String) null, thumb, size, ext, (Object) null, 1);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, ImageLocation thumbLocation, String thumbFilter, String ext, Object parentObject, int cacheType) {
        setImage(imageLocation, imageFilter, thumbLocation, thumbFilter, (Drawable) null, 0, ext, parentObject, cacheType);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, ImageLocation thumbLocation, String thumbFilter, int size, String ext, Object parentObject, int cacheType) {
        setImage(imageLocation, imageFilter, thumbLocation, thumbFilter, (Drawable) null, size, ext, parentObject, cacheType);
    }

    public void setForUserOrChat(TLObject object, Drawable avatarDrawable) {
        setForUserOrChat(object, avatarDrawable, (Object) null);
    }

    public void setForUserOrChat(TLObject object, Drawable avatarDrawable, Object parentObject) {
        boolean hasStripped;
        BitmapDrawable strippedBitmap;
        if (parentObject == null) {
            parentObject = object;
        }
        setUseRoundForThumbDrawable(true);
        BitmapDrawable strippedBitmap2 = null;
        boolean hasStripped2 = false;
        boolean hasStripped3 = false;
        if (object instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) object;
            if (user.photo != null) {
                strippedBitmap2 = user.photo.strippedBitmap;
                if (user.photo.stripped_thumb != null) {
                    hasStripped3 = true;
                }
                hasStripped2 = hasStripped3;
            }
            strippedBitmap = strippedBitmap2;
            hasStripped = hasStripped2;
        } else {
            if (object instanceof TLRPC.Chat) {
                TLRPC.Chat chat = (TLRPC.Chat) object;
                if (chat.photo != null) {
                    BitmapDrawable strippedBitmap3 = chat.photo.strippedBitmap;
                    if (chat.photo.stripped_thumb != null) {
                        hasStripped3 = true;
                    }
                    strippedBitmap = strippedBitmap3;
                    hasStripped = hasStripped3;
                }
            }
            strippedBitmap = null;
            hasStripped = false;
        }
        if (strippedBitmap != null) {
            setImage(ImageLocation.getForUserOrChat(object, 1), "50_50", strippedBitmap, (String) null, parentObject, 0);
        } else if (hasStripped) {
            setImage(ImageLocation.getForUserOrChat(object, 1), "50_50", ImageLocation.getForUserOrChat(object, 2), "50_50", avatarDrawable, parentObject, 0);
        } else {
            setImage(ImageLocation.getForUserOrChat(object, 1), "50_50", avatarDrawable, (String) null, parentObject, 0);
        }
    }

    public void setImage(ImageLocation fileLocation, String fileFilter, ImageLocation thumbLocation, String thumbFilter, Drawable thumb, Object parentObject, int cacheType) {
        setImage((ImageLocation) null, (String) null, fileLocation, fileFilter, thumbLocation, thumbFilter, thumb, 0, (String) null, parentObject, cacheType);
    }

    public void setImage(ImageLocation fileLocation, String fileFilter, ImageLocation thumbLocation, String thumbFilter, Drawable thumb, int size, String ext, Object parentObject, int cacheType) {
        setImage((ImageLocation) null, (String) null, fileLocation, fileFilter, thumbLocation, thumbFilter, thumb, size, ext, parentObject, cacheType);
    }

    /* JADX WARNING: Removed duplicated region for block: B:107:0x01a8  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x01cd A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x01d2  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x01d5  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x01dc  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x01e0  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01e6  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0203  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x02ba  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x0302  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x031d  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x0326  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x0344  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x0346  */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x034c  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x035a  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x0380  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0382  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0158  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x016b  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0171  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setImage(org.telegram.messenger.ImageLocation r20, java.lang.String r21, org.telegram.messenger.ImageLocation r22, java.lang.String r23, org.telegram.messenger.ImageLocation r24, java.lang.String r25, android.graphics.drawable.Drawable r26, int r27, java.lang.String r28, java.lang.Object r29, int r30) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r22
            r4 = r23
            r5 = r24
            r6 = r25
            r7 = r26
            r8 = r28
            r9 = r29
            boolean r10 = r0.ignoreImageSet
            if (r10 == 0) goto L_0x0019
            return
        L_0x0019:
            boolean r10 = r0.crossfadeWithOldImage
            if (r10 == 0) goto L_0x002a
            org.telegram.messenger.ImageReceiver$SetImageBackup r10 = r0.setImageBackup
            if (r10 == 0) goto L_0x002a
            boolean r10 = r10.isWebfileSet()
            if (r10 == 0) goto L_0x002a
            r19.setBackupImage()
        L_0x002a:
            org.telegram.messenger.ImageReceiver$SetImageBackup r10 = r0.setImageBackup
            if (r10 == 0) goto L_0x0031
            r10.clear()
        L_0x0031:
            r10 = 1065353216(0x3var_, float:1.0)
            r11 = 1
            r12 = 0
            r13 = 0
            if (r3 != 0) goto L_0x00d3
            if (r5 != 0) goto L_0x00d3
            if (r1 != 0) goto L_0x00d3
            r14 = 0
        L_0x003d:
            r15 = 4
            if (r14 >= r15) goto L_0x0046
            r0.recycleBitmap(r13, r14)
            int r14 = r14 + 1
            goto L_0x003d
        L_0x0046:
            r0.currentImageLocation = r13
            r0.currentImageFilter = r13
            r0.currentImageKey = r13
            r0.currentMediaLocation = r13
            r0.currentMediaFilter = r13
            r0.currentMediaKey = r13
            r0.currentThumbLocation = r13
            r0.currentThumbFilter = r13
            r0.currentThumbKey = r13
            r0.currentMediaDrawable = r13
            r0.mediaShader = r13
            r0.currentImageDrawable = r13
            r0.imageShader = r13
            r0.composeShader = r13
            r0.thumbShader = r13
            r0.crossfadeShader = r13
            r0.legacyShader = r13
            r0.legacyCanvas = r13
            android.graphics.Bitmap r14 = r0.legacyBitmap
            if (r14 == 0) goto L_0x0073
            r14.recycle()
            r0.legacyBitmap = r13
        L_0x0073:
            r0.currentExt = r8
            r0.currentParentObject = r13
            r0.currentCacheType = r12
            r0.staticThumbDrawable = r7
            r0.currentAlpha = r10
            r0.currentSize = r12
            boolean r10 = r7 instanceof org.telegram.messenger.SvgHelper.SvgDrawable
            if (r10 == 0) goto L_0x0089
            r10 = r7
            org.telegram.messenger.SvgHelper$SvgDrawable r10 = (org.telegram.messenger.SvgHelper.SvgDrawable) r10
            r10.setParent(r0)
        L_0x0089:
            org.telegram.messenger.ImageLoader r10 = org.telegram.messenger.ImageLoader.getInstance()
            r10.cancelLoadingForImageReceiver(r0, r11)
            android.view.View r10 = r0.parentView
            if (r10 == 0) goto L_0x00ad
            boolean r13 = r0.invalidateAll
            if (r13 == 0) goto L_0x009c
            r10.invalidate()
            goto L_0x00ad
        L_0x009c:
            float r13 = r0.imageX
            int r14 = (int) r13
            float r15 = r0.imageY
            int r11 = (int) r15
            float r12 = r0.imageW
            float r13 = r13 + r12
            int r12 = (int) r13
            float r13 = r0.imageH
            float r15 = r15 + r13
            int r13 = (int) r15
            r10.invalidate(r14, r11, r12, r13)
        L_0x00ad:
            org.telegram.messenger.ImageReceiver$ImageReceiverDelegate r10 = r0.delegate
            if (r10 == 0) goto L_0x00d2
            android.graphics.drawable.Drawable r11 = r0.currentImageDrawable
            if (r11 != 0) goto L_0x00c4
            android.graphics.drawable.Drawable r12 = r0.currentThumbDrawable
            if (r12 != 0) goto L_0x00c4
            android.graphics.drawable.Drawable r12 = r0.staticThumbDrawable
            if (r12 != 0) goto L_0x00c4
            android.graphics.drawable.Drawable r12 = r0.currentMediaDrawable
            if (r12 == 0) goto L_0x00c2
            goto L_0x00c4
        L_0x00c2:
            r12 = 0
            goto L_0x00c5
        L_0x00c4:
            r12 = 1
        L_0x00c5:
            if (r11 != 0) goto L_0x00cd
            android.graphics.drawable.Drawable r11 = r0.currentMediaDrawable
            if (r11 != 0) goto L_0x00cd
            r11 = 1
            goto L_0x00ce
        L_0x00cd:
            r11 = 0
        L_0x00ce:
            r13 = 0
            r10.didSetImage(r0, r12, r11, r13)
        L_0x00d2:
            return
        L_0x00d3:
            if (r3 == 0) goto L_0x00db
            r11 = 0
            java.lang.String r12 = r3.getKey(r9, r13, r11)
            goto L_0x00dc
        L_0x00db:
            r12 = r13
        L_0x00dc:
            r11 = r12
            if (r11 != 0) goto L_0x00e2
            if (r3 == 0) goto L_0x00e2
            r3 = 0
        L_0x00e2:
            r12 = 0
            r0.currentKeyQuality = r12
            if (r11 != 0) goto L_0x0138
            boolean r12 = r0.needsQualityThumb
            if (r12 == 0) goto L_0x0138
            boolean r12 = r9 instanceof org.telegram.messenger.MessageObject
            if (r12 != 0) goto L_0x00f7
            org.telegram.tgnet.TLRPC$Document r12 = r0.qulityThumbDocument
            if (r12 == 0) goto L_0x00f4
            goto L_0x00f7
        L_0x00f4:
            r16 = r11
            goto L_0x013a
        L_0x00f7:
            org.telegram.tgnet.TLRPC$Document r12 = r0.qulityThumbDocument
            if (r12 == 0) goto L_0x00fc
            goto L_0x0103
        L_0x00fc:
            r12 = r9
            org.telegram.messenger.MessageObject r12 = (org.telegram.messenger.MessageObject) r12
            org.telegram.tgnet.TLRPC$Document r12 = r12.getDocument()
        L_0x0103:
            if (r12 == 0) goto L_0x0135
            int r14 = r12.dc_id
            if (r14 == 0) goto L_0x0135
            long r14 = r12.id
            r16 = 0
            int r18 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r18 == 0) goto L_0x0135
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r15 = "q_"
            r14.append(r15)
            int r15 = r12.dc_id
            r14.append(r15)
            java.lang.String r15 = "_"
            r14.append(r15)
            r16 = r11
            long r10 = r12.id
            r14.append(r10)
            java.lang.String r11 = r14.toString()
            r10 = 1
            r0.currentKeyQuality = r10
            goto L_0x013c
        L_0x0135:
            r16 = r11
            goto L_0x013a
        L_0x0138:
            r16 = r11
        L_0x013a:
            r11 = r16
        L_0x013c:
            java.lang.String r10 = "@"
            if (r11 == 0) goto L_0x0154
            if (r4 == 0) goto L_0x0154
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r11)
            r12.append(r10)
            r12.append(r4)
            java.lang.String r11 = r12.toString()
        L_0x0154:
            java.lang.String r12 = r0.uniqKeyPrefix
            if (r12 == 0) goto L_0x0169
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r14 = r0.uniqKeyPrefix
            r12.append(r14)
            r12.append(r11)
            java.lang.String r11 = r12.toString()
        L_0x0169:
            if (r1 == 0) goto L_0x0171
            r12 = 0
            java.lang.String r14 = r1.getKey(r9, r13, r12)
            goto L_0x0172
        L_0x0171:
            r14 = r13
        L_0x0172:
            r12 = r14
            if (r12 != 0) goto L_0x0178
            if (r1 == 0) goto L_0x0178
            r1 = 0
        L_0x0178:
            if (r12 == 0) goto L_0x018e
            if (r2 == 0) goto L_0x018e
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r12)
            r14.append(r10)
            r14.append(r2)
            java.lang.String r12 = r14.toString()
        L_0x018e:
            if (r12 != 0) goto L_0x019a
            java.lang.String r14 = r0.currentImageKey
            if (r14 == 0) goto L_0x019a
            boolean r14 = r14.equals(r11)
            if (r14 != 0) goto L_0x01a4
        L_0x019a:
            java.lang.String r14 = r0.currentMediaKey
            if (r14 == 0) goto L_0x01ce
            boolean r14 = r14.equals(r12)
            if (r14 == 0) goto L_0x01ce
        L_0x01a4:
            org.telegram.messenger.ImageReceiver$ImageReceiverDelegate r14 = r0.delegate
            if (r14 == 0) goto L_0x01c9
            android.graphics.drawable.Drawable r15 = r0.currentImageDrawable
            if (r15 != 0) goto L_0x01bb
            android.graphics.drawable.Drawable r13 = r0.currentThumbDrawable
            if (r13 != 0) goto L_0x01bb
            android.graphics.drawable.Drawable r13 = r0.staticThumbDrawable
            if (r13 != 0) goto L_0x01bb
            android.graphics.drawable.Drawable r13 = r0.currentMediaDrawable
            if (r13 == 0) goto L_0x01b9
            goto L_0x01bb
        L_0x01b9:
            r13 = 0
            goto L_0x01bc
        L_0x01bb:
            r13 = 1
        L_0x01bc:
            if (r15 != 0) goto L_0x01c4
            android.graphics.drawable.Drawable r15 = r0.currentMediaDrawable
            if (r15 != 0) goto L_0x01c4
            r15 = 1
            goto L_0x01c5
        L_0x01c4:
            r15 = 0
        L_0x01c5:
            r7 = 0
            r14.didSetImage(r0, r13, r15, r7)
        L_0x01c9:
            boolean r7 = r0.canceledLoading
            if (r7 != 0) goto L_0x01ce
            return
        L_0x01ce:
            org.telegram.messenger.ImageLocation r7 = r0.strippedLocation
            if (r7 == 0) goto L_0x01d5
            org.telegram.messenger.ImageLocation r7 = r0.strippedLocation
            goto L_0x01da
        L_0x01d5:
            if (r1 == 0) goto L_0x01d9
            r7 = r1
            goto L_0x01da
        L_0x01d9:
            r7 = r3
        L_0x01da:
            if (r7 != 0) goto L_0x01de
            r7 = r24
        L_0x01de:
            if (r5 == 0) goto L_0x01e6
            r13 = 0
            java.lang.String r14 = r5.getKey(r9, r7, r13)
            goto L_0x01e7
        L_0x01e6:
            r14 = 0
        L_0x01e7:
            r13 = r14
            if (r13 == 0) goto L_0x01fe
            if (r6 == 0) goto L_0x01fe
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r13)
            r14.append(r10)
            r14.append(r6)
            java.lang.String r13 = r14.toString()
        L_0x01fe:
            boolean r10 = r0.crossfadeWithOldImage
            r15 = 2
            if (r10 == 0) goto L_0x02ba
            android.graphics.drawable.Drawable r10 = r0.currentMediaDrawable
            if (r10 == 0) goto L_0x0232
            boolean r14 = r10 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r14 == 0) goto L_0x0210
            org.telegram.ui.Components.AnimatedFileDrawable r10 = (org.telegram.ui.Components.AnimatedFileDrawable) r10
            r10.stop()
        L_0x0210:
            r10 = 1
            r0.recycleBitmap(r13, r10)
            r10 = 0
            r0.recycleBitmap(r10, r15)
            r10 = 0
            r0.recycleBitmap(r12, r10)
            android.graphics.drawable.Drawable r14 = r0.currentMediaDrawable
            r0.crossfadeImage = r14
            android.graphics.BitmapShader r14 = r0.mediaShader
            r0.crossfadeShader = r14
            java.lang.String r14 = r0.currentImageKey
            r0.crossfadeKey = r14
            r0.crossfadingWithThumb = r10
            r10 = 0
            r0.currentMediaDrawable = r10
            r0.currentMediaKey = r10
            r10 = 1
            goto L_0x02cd
        L_0x0232:
            r10 = 0
            android.graphics.drawable.Drawable r14 = r0.currentImageDrawable
            if (r14 == 0) goto L_0x0259
            r14 = 1
            r0.recycleBitmap(r13, r14)
            r0.recycleBitmap(r10, r15)
            r10 = 3
            r0.recycleBitmap(r12, r10)
            android.graphics.BitmapShader r10 = r0.imageShader
            r0.crossfadeShader = r10
            android.graphics.drawable.Drawable r10 = r0.currentImageDrawable
            r0.crossfadeImage = r10
            java.lang.String r10 = r0.currentImageKey
            r0.crossfadeKey = r10
            r10 = 0
            r0.crossfadingWithThumb = r10
            r14 = 0
            r0.currentImageDrawable = r14
            r0.currentImageKey = r14
            r10 = 1
            goto L_0x02cd
        L_0x0259:
            r14 = r10
            r10 = 0
            android.graphics.drawable.Drawable r14 = r0.currentThumbDrawable
            if (r14 == 0) goto L_0x0280
            r0.recycleBitmap(r11, r10)
            r10 = 0
            r0.recycleBitmap(r10, r15)
            r10 = 3
            r0.recycleBitmap(r12, r10)
            android.graphics.BitmapShader r10 = r0.thumbShader
            r0.crossfadeShader = r10
            android.graphics.drawable.Drawable r10 = r0.currentThumbDrawable
            r0.crossfadeImage = r10
            java.lang.String r10 = r0.currentThumbKey
            r0.crossfadeKey = r10
            r10 = 0
            r0.crossfadingWithThumb = r10
            r14 = 0
            r0.currentThumbDrawable = r14
            r0.currentThumbKey = r14
            r10 = 1
            goto L_0x02cd
        L_0x0280:
            r14 = 0
            android.graphics.drawable.Drawable r14 = r0.staticThumbDrawable
            if (r14 == 0) goto L_0x02a8
            r0.recycleBitmap(r11, r10)
            r10 = 1
            r0.recycleBitmap(r13, r10)
            r10 = 0
            r0.recycleBitmap(r10, r15)
            r10 = 3
            r0.recycleBitmap(r12, r10)
            android.graphics.BitmapShader r10 = r0.thumbShader
            r0.crossfadeShader = r10
            android.graphics.drawable.Drawable r10 = r0.staticThumbDrawable
            r0.crossfadeImage = r10
            r10 = 0
            r0.crossfadingWithThumb = r10
            r14 = 0
            r0.crossfadeKey = r14
            r0.currentThumbDrawable = r14
            r0.currentThumbKey = r14
            r10 = 1
            goto L_0x02cd
        L_0x02a8:
            r14 = 0
            r0.recycleBitmap(r11, r10)
            r10 = 1
            r0.recycleBitmap(r13, r10)
            r0.recycleBitmap(r14, r15)
            r15 = 3
            r0.recycleBitmap(r12, r15)
            r0.crossfadeShader = r14
            goto L_0x02cd
        L_0x02ba:
            r10 = 1
            r14 = 0
            r14 = 0
            r0.recycleBitmap(r11, r14)
            r0.recycleBitmap(r13, r10)
            r14 = 0
            r0.recycleBitmap(r14, r15)
            r15 = 3
            r0.recycleBitmap(r12, r15)
            r0.crossfadeShader = r14
        L_0x02cd:
            r0.currentImageLocation = r3
            r0.currentImageFilter = r4
            r0.currentImageKey = r11
            r0.currentMediaLocation = r1
            r0.currentMediaFilter = r2
            r0.currentMediaKey = r12
            r0.currentThumbLocation = r5
            r0.currentThumbFilter = r6
            r0.currentThumbKey = r13
            r0.currentParentObject = r9
            r0.currentExt = r8
            r14 = r27
            r0.currentSize = r14
            r15 = r30
            r0.currentCacheType = r15
            r10 = r26
            r0.staticThumbDrawable = r10
            r20 = r1
            r1 = 0
            r0.imageShader = r1
            r0.composeShader = r1
            r0.thumbShader = r1
            r0.mediaShader = r1
            r0.legacyShader = r1
            r0.legacyCanvas = r1
            android.graphics.Bitmap r1 = r0.legacyBitmap
            if (r1 == 0) goto L_0x0308
            r1.recycle()
            r1 = 0
            r0.legacyBitmap = r1
        L_0x0308:
            boolean r1 = r0.useRoundForThumb
            if (r1 == 0) goto L_0x0313
            android.graphics.drawable.Drawable r1 = r0.staticThumbDrawable
            if (r1 == 0) goto L_0x0313
            r0.updateDrawableRadius(r1)
        L_0x0313:
            r1 = 1065353216(0x3var_, float:1.0)
            r0.currentAlpha = r1
            android.graphics.drawable.Drawable r1 = r0.staticThumbDrawable
            boolean r2 = r1 instanceof org.telegram.messenger.SvgHelper.SvgDrawable
            if (r2 == 0) goto L_0x0322
            org.telegram.messenger.SvgHelper$SvgDrawable r1 = (org.telegram.messenger.SvgHelper.SvgDrawable) r1
            r1.setParent(r0)
        L_0x0322:
            org.telegram.messenger.ImageReceiver$ImageReceiverDelegate r1 = r0.delegate
            if (r1 == 0) goto L_0x034c
            android.graphics.drawable.Drawable r2 = r0.currentImageDrawable
            if (r2 != 0) goto L_0x033b
            r16 = r3
            android.graphics.drawable.Drawable r3 = r0.currentThumbDrawable
            if (r3 != 0) goto L_0x033d
            android.graphics.drawable.Drawable r3 = r0.staticThumbDrawable
            if (r3 != 0) goto L_0x033d
            android.graphics.drawable.Drawable r3 = r0.currentMediaDrawable
            if (r3 == 0) goto L_0x0339
            goto L_0x033d
        L_0x0339:
            r3 = 0
            goto L_0x033e
        L_0x033b:
            r16 = r3
        L_0x033d:
            r3 = 1
        L_0x033e:
            if (r2 != 0) goto L_0x0346
            android.graphics.drawable.Drawable r2 = r0.currentMediaDrawable
            if (r2 != 0) goto L_0x0346
            r2 = 1
            goto L_0x0347
        L_0x0346:
            r2 = 0
        L_0x0347:
            r4 = 0
            r1.didSetImage(r0, r3, r2, r4)
            goto L_0x034f
        L_0x034c:
            r16 = r3
            r4 = 0
        L_0x034f:
            org.telegram.messenger.ImageLoader r1 = org.telegram.messenger.ImageLoader.getInstance()
            r1.loadImageForImageReceiver(r0)
            android.view.View r1 = r0.parentView
            if (r1 == 0) goto L_0x0373
            boolean r2 = r0.invalidateAll
            if (r2 == 0) goto L_0x0362
            r1.invalidate()
            goto L_0x0373
        L_0x0362:
            float r2 = r0.imageX
            int r3 = (int) r2
            float r4 = r0.imageY
            int r5 = (int) r4
            float r6 = r0.imageW
            float r2 = r2 + r6
            int r2 = (int) r2
            float r6 = r0.imageH
            float r4 = r4 + r6
            int r4 = (int) r4
            r1.invalidate(r3, r5, r2, r4)
        L_0x0373:
            boolean r1 = r9 instanceof org.telegram.messenger.MessageObject
            if (r1 == 0) goto L_0x0382
            r1 = r9
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            boolean r1 = r1.isRoundVideo()
            if (r1 == 0) goto L_0x0382
            r1 = 1
            goto L_0x0383
        L_0x0382:
            r1 = 0
        L_0x0383:
            r0.isRoundVideo = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageReceiver.setImage(org.telegram.messenger.ImageLocation, java.lang.String, org.telegram.messenger.ImageLocation, java.lang.String, org.telegram.messenger.ImageLocation, java.lang.String, android.graphics.drawable.Drawable, int, java.lang.String, java.lang.Object, int):void");
    }

    public boolean canInvertBitmap() {
        return (this.currentMediaDrawable instanceof ExtendedBitmapDrawable) || (this.currentImageDrawable instanceof ExtendedBitmapDrawable) || (this.currentThumbDrawable instanceof ExtendedBitmapDrawable) || (this.staticThumbDrawable instanceof ExtendedBitmapDrawable);
    }

    public void setColorFilter(ColorFilter filter) {
        this.colorFilter = filter;
    }

    public void setDelegate(ImageReceiverDelegate delegate2) {
        this.delegate = delegate2;
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
        if (animation != null) {
            return animation.getOrientation();
        }
        return 0;
    }

    public int getOrientation() {
        return this.imageOrientation;
    }

    public void setLayerNum(int value) {
        this.currentLayerNum = value;
    }

    public void setImageBitmap(Bitmap bitmap) {
        BitmapDrawable bitmapDrawable = null;
        if (bitmap != null) {
            bitmapDrawable = new BitmapDrawable((Resources) null, bitmap);
        }
        setImageBitmap((Drawable) bitmapDrawable);
    }

    public void setImageBitmap(Drawable bitmap) {
        boolean z = true;
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
        if (!this.crossfadeWithOldImage) {
            for (int a = 0; a < 4; a++) {
                recycleBitmap((String) null, a);
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
            for (int a2 = 0; a2 < 4; a2++) {
                recycleBitmap((String) null, a2);
            }
            this.crossfadeShader = null;
        }
        Drawable drawable = this.staticThumbDrawable;
        if (drawable instanceof RecyclableDrawable) {
            ((RecyclableDrawable) drawable).recycle();
        }
        if (bitmap instanceof AnimatedFileDrawable) {
            AnimatedFileDrawable fileDrawable = (AnimatedFileDrawable) bitmap;
            fileDrawable.setParentView(this.parentView);
            fileDrawable.setUseSharedQueue(this.useSharedAnimationQueue);
            if (this.allowStartAnimation && this.currentOpenedLayerFlags == 0) {
                fileDrawable.start();
            }
            fileDrawable.setAllowDecodeSingleFrame(this.allowDecodeSingleFrame);
        } else if (bitmap instanceof RLottieDrawable) {
            RLottieDrawable fileDrawable2 = (RLottieDrawable) bitmap;
            fileDrawable2.addParentView(this.parentView);
            if (this.allowStartLottieAnimation && (!fileDrawable2.isHeavyDrawable() || this.currentOpenedLayerFlags == 0)) {
                fileDrawable2.start();
            }
            fileDrawable2.setAllowDecodeSingleFrame(true);
        }
        this.staticThumbDrawable = bitmap;
        updateDrawableRadius(bitmap);
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
        Bitmap bitmap2 = this.legacyBitmap;
        if (bitmap2 != null) {
            bitmap2.recycle();
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

    private void setDrawableShader(Drawable drawable, BitmapShader shader) {
        if (drawable == this.currentThumbDrawable || drawable == this.staticThumbDrawable) {
            this.thumbShader = shader;
        } else if (drawable == this.currentMediaDrawable) {
            this.mediaShader = shader;
        } else if (drawable == this.currentImageDrawable) {
            this.imageShader = shader;
            if (this.gradientShader != null && (drawable instanceof BitmapDrawable)) {
                if (Build.VERSION.SDK_INT >= 28) {
                    this.composeShader = new ComposeShader(this.gradientShader, this.imageShader, PorterDuff.Mode.DST_IN);
                    return;
                }
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                int w = bitmapDrawable.getBitmap().getWidth();
                int h = bitmapDrawable.getBitmap().getHeight();
                Bitmap bitmap = this.legacyBitmap;
                if (bitmap == null || bitmap.getWidth() != w || this.legacyBitmap.getHeight() != h) {
                    Bitmap bitmap2 = this.legacyBitmap;
                    if (bitmap2 != null) {
                        bitmap2.recycle();
                    }
                    this.legacyBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    this.legacyCanvas = new Canvas(this.legacyBitmap);
                    this.legacyShader = new BitmapShader(this.legacyBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    if (this.legacyPaint == null) {
                        Paint paint = new Paint();
                        this.legacyPaint = paint;
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                    }
                }
            }
        }
    }

    private boolean hasRoundRadius() {
        return true;
    }

    private void updateDrawableRadius(Drawable drawable) {
        if ((!hasRoundRadius() && this.gradientShader == null) || !(drawable instanceof BitmapDrawable)) {
            setDrawableShader(drawable, (BitmapShader) null);
        } else if (!(drawable instanceof RLottieDrawable)) {
            if (drawable instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) drawable).setRoundRadius(this.roundRadius);
            } else {
                setDrawableShader(drawable, new BitmapShader(((BitmapDrawable) drawable).getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            }
        }
    }

    public void clearImage() {
        for (int a = 0; a < 4; a++) {
            recycleBitmap((String) null, a);
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
            this.setImageBackup.imageLocation = this.currentImageLocation;
            this.setImageBackup.imageFilter = this.currentImageFilter;
            this.setImageBackup.thumbLocation = this.currentThumbLocation;
            this.setImageBackup.thumbFilter = this.currentThumbFilter;
            this.setImageBackup.thumb = this.staticThumbDrawable;
            this.setImageBackup.size = this.currentSize;
            this.setImageBackup.ext = this.currentExt;
            this.setImageBackup.cacheType = this.currentCacheType;
            this.setImageBackup.parentObject = this.currentParentObject;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopAllHeavyOperations);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.startAllHeavyOperations);
        if (this.staticThumbDrawable != null) {
            this.staticThumbDrawable = null;
            this.thumbShader = null;
        }
        clearImage();
        if (this.isPressed == 0) {
            this.pressedProgress = 0.0f;
        }
    }

    private boolean setBackupImage() {
        SetImageBackup setImageBackup2 = this.setImageBackup;
        if (setImageBackup2 == null || !setImageBackup2.isSet()) {
            return false;
        }
        SetImageBackup temp = this.setImageBackup;
        this.setImageBackup = null;
        setImage(temp.mediaLocation, temp.mediaFilter, temp.imageLocation, temp.imageFilter, temp.thumbLocation, temp.thumbFilter, temp.thumb, temp.size, temp.ext, temp.parentObject, temp.cacheType);
        temp.clear();
        this.setImageBackup = temp;
        RLottieDrawable lottieDrawable = getLottieAnimation();
        if (lottieDrawable == null || !this.allowStartLottieAnimation) {
            return true;
        }
        if (lottieDrawable.isHeavyDrawable() && this.currentOpenedLayerFlags != 0) {
            return true;
        }
        lottieDrawable.start();
        return true;
    }

    public boolean onAttachedToWindow() {
        int currentHeavyOperationFlags = NotificationCenter.getGlobalInstance().getCurrentHeavyOperationFlags();
        this.currentOpenedLayerFlags = currentHeavyOperationFlags;
        this.currentOpenedLayerFlags = currentHeavyOperationFlags & (this.currentLayerNum ^ -1);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopAllHeavyOperations);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.startAllHeavyOperations);
        if (setBackupImage()) {
            return true;
        }
        RLottieDrawable lottieDrawable = getLottieAnimation();
        if (lottieDrawable != null && this.allowStartLottieAnimation && (!lottieDrawable.isHeavyDrawable() || this.currentOpenedLayerFlags == 0)) {
            lottieDrawable.start();
        }
        AnimatedFileDrawable animatedFileDrawable = getAnimation();
        if (animatedFileDrawable != null && this.allowStartAnimation && this.currentOpenedLayerFlags == 0) {
            animatedFileDrawable.stop();
        }
        if (NotificationCenter.getGlobalInstance().isAnimationInProgress()) {
            didReceivedNotification(NotificationCenter.stopAllHeavyOperations, this.currentAccount, 512);
        }
        return false;
    }

    private void drawDrawable(Canvas canvas, Drawable drawable, int alpha, BitmapShader shader, int orientation) {
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
        int i = this.isPressed;
        if (i != 0) {
            this.pressedProgress = 1.0f;
            this.animateFromIsPressed = i;
        }
        float f3 = this.pressedProgress;
        if (f3 == 0.0f || f3 == 1.0f) {
            drawDrawable(canvas, drawable, alpha, shader, orientation, i);
            return;
        }
        drawDrawable(canvas, drawable, alpha, shader, orientation, i);
        drawDrawable(canvas, drawable, (int) (((float) alpha) * this.pressedProgress), shader, orientation, this.animateFromIsPressed);
    }

    public void setUseRoundForThumbDrawable(boolean value) {
        this.useRoundForThumb = value;
    }

    private void drawDrawable(Canvas canvas, Drawable drawable, int alpha, BitmapShader shader, int orientation, int isPressed2) {
        Paint paint;
        int bitmapW;
        int bitmapH;
        int bitmapW2;
        Canvas canvas2 = canvas;
        Drawable drawable2 = drawable;
        int i = alpha;
        BitmapShader bitmapShader = shader;
        int i2 = orientation;
        int i3 = isPressed2;
        if (drawable2 instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable2;
            if (bitmapShader != null) {
                paint = this.roundPaint;
            } else {
                paint = bitmapDrawable.getPaint();
            }
            if (Build.VERSION.SDK_INT >= 29) {
                Object obj = this.blendMode;
                if (obj == null || this.gradientShader != null) {
                    paint.setBlendMode((BlendMode) null);
                } else {
                    paint.setBlendMode((BlendMode) obj);
                }
            }
            boolean hasFilter = (paint == null || paint.getColorFilter() == null) ? false : true;
            if (!hasFilter || i3 != 0) {
                if (!hasFilter && i3 != 0) {
                    if (i3 == 1) {
                        if (bitmapShader != null) {
                            this.roundPaint.setColorFilter(selectedColorFilter);
                        } else {
                            bitmapDrawable.setColorFilter(selectedColorFilter);
                        }
                    } else if (bitmapShader != null) {
                        this.roundPaint.setColorFilter(selectedGroupColorFilter);
                    } else {
                        bitmapDrawable.setColorFilter(selectedGroupColorFilter);
                    }
                }
            } else if (bitmapShader != null) {
                this.roundPaint.setColorFilter((ColorFilter) null);
            } else if (this.staticThumbDrawable != drawable2) {
                bitmapDrawable.setColorFilter((ColorFilter) null);
            }
            ColorFilter colorFilter2 = this.colorFilter;
            if (colorFilter2 != null && this.gradientShader == null) {
                if (bitmapShader != null) {
                    this.roundPaint.setColorFilter(colorFilter2);
                } else {
                    bitmapDrawable.setColorFilter(colorFilter2);
                }
            }
            if (!(bitmapDrawable instanceof AnimatedFileDrawable) && !(bitmapDrawable instanceof RLottieDrawable)) {
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap != null && bitmap.isRecycled()) {
                    return;
                }
                if (i2 % 360 == 90 || i2 % 360 == 270) {
                    bitmapW = bitmap.getHeight();
                    bitmapH = bitmap.getWidth();
                } else {
                    bitmapW = bitmap.getWidth();
                    bitmapH = bitmap.getHeight();
                }
            } else if (i2 % 360 == 90 || i2 % 360 == 270) {
                bitmapW = bitmapDrawable.getIntrinsicHeight();
                bitmapH = bitmapDrawable.getIntrinsicWidth();
            } else {
                bitmapW = bitmapDrawable.getIntrinsicWidth();
                bitmapH = bitmapDrawable.getIntrinsicHeight();
            }
            float f = this.imageW;
            float f2 = this.sideClip;
            float realImageW = f - (f2 * 2.0f);
            float f3 = this.imageH;
            float realImageH = f3 - (f2 * 2.0f);
            float scaleW = f == 0.0f ? 1.0f : ((float) bitmapW) / realImageW;
            float scaleH = f3 == 0.0f ? 1.0f : ((float) bitmapH) / realImageH;
            if (bitmapShader == null) {
                int i4 = i2;
                float scaleH2 = scaleH;
                BitmapDrawable bitmapDrawable2 = bitmapDrawable;
                Paint paint2 = paint;
                float scaleW2 = scaleW;
                if (this.isAspectFit) {
                    float scale = Math.max(scaleW2, scaleH2);
                    canvas.save();
                    int bitmapW3 = (int) (((float) bitmapW) / scale);
                    int bitmapH2 = (int) (((float) bitmapH) / scale);
                    RectF rectF = this.drawRegion;
                    float f4 = this.imageX;
                    float f5 = this.imageW;
                    float f6 = this.imageY;
                    float f7 = this.imageH;
                    float f8 = scale;
                    rectF.set(((f5 - ((float) bitmapW3)) / 2.0f) + f4, ((f7 - ((float) bitmapH2)) / 2.0f) + f6, f4 + ((f5 + ((float) bitmapW3)) / 2.0f), f6 + ((f7 + ((float) bitmapH2)) / 2.0f));
                    bitmapDrawable2.setBounds((int) this.drawRegion.left, (int) this.drawRegion.top, (int) this.drawRegion.right, (int) this.drawRegion.bottom);
                    if (bitmapDrawable2 instanceof AnimatedFileDrawable) {
                        ((AnimatedFileDrawable) bitmapDrawable2).setActualDrawRect(this.drawRegion.left, this.drawRegion.top, this.drawRegion.width(), this.drawRegion.height());
                    }
                    if (this.isVisible) {
                        try {
                            bitmapDrawable2.setAlpha(i);
                            bitmapDrawable2.draw(canvas2);
                        } catch (Exception e) {
                            onBitmapException(bitmapDrawable2);
                            FileLog.e((Throwable) e);
                        }
                    }
                    canvas.restore();
                    int i5 = orientation;
                } else if (Math.abs(scaleW2 - scaleH2) > 1.0E-5f) {
                    canvas.save();
                    float f9 = this.imageX;
                    float var_ = this.imageY;
                    canvas2.clipRect(f9, var_, this.imageW + f9, this.imageH + var_);
                    int i6 = orientation;
                    if (i6 % 360 != 0) {
                        if (this.centerRotation) {
                            canvas2.rotate((float) i6, this.imageW / 2.0f, this.imageH / 2.0f);
                        } else {
                            canvas2.rotate((float) i6, 0.0f, 0.0f);
                        }
                    }
                    float var_ = this.imageW;
                    if (((float) bitmapW) / scaleH2 > var_) {
                        int bitmapW4 = (int) (((float) bitmapW) / scaleH2);
                        RectF rectF2 = this.drawRegion;
                        float var_ = this.imageX;
                        float var_ = this.imageY;
                        rectF2.set(var_ - ((((float) bitmapW4) - var_) / 2.0f), var_, var_ + ((((float) bitmapW4) + var_) / 2.0f), this.imageH + var_);
                        float var_ = scaleW2;
                    } else {
                        int bitmapH3 = (int) (((float) bitmapH) / scaleW2);
                        RectF rectF3 = this.drawRegion;
                        float var_ = this.imageX;
                        float var_ = this.imageY;
                        float var_ = this.imageH;
                        float var_ = scaleW2;
                        rectF3.set(var_, var_ - ((((float) bitmapH3) - var_) / 2.0f), var_ + var_, var_ + ((((float) bitmapH3) + var_) / 2.0f));
                    }
                    if (bitmapDrawable2 instanceof AnimatedFileDrawable) {
                        ((AnimatedFileDrawable) bitmapDrawable2).setActualDrawRect(this.imageX, this.imageY, this.imageW, this.imageH);
                    }
                    if (i6 % 360 == 90 || i6 % 360 == 270) {
                        float width = this.drawRegion.width() / 2.0f;
                        float height = this.drawRegion.height() / 2.0f;
                        float centerX = this.drawRegion.centerX();
                        float centerY = this.drawRegion.centerY();
                        bitmapDrawable2.setBounds((int) (centerX - height), (int) (centerY - width), (int) (centerX + height), (int) (centerY + width));
                    } else {
                        bitmapDrawable2.setBounds((int) this.drawRegion.left, (int) this.drawRegion.top, (int) this.drawRegion.right, (int) this.drawRegion.bottom);
                    }
                    if (this.isVisible) {
                        try {
                            if (Build.VERSION.SDK_INT >= 29) {
                                if (this.blendMode != null) {
                                    bitmapDrawable2.getPaint().setBlendMode((BlendMode) this.blendMode);
                                } else {
                                    bitmapDrawable2.getPaint().setBlendMode((BlendMode) null);
                                }
                            }
                            bitmapDrawable2.setAlpha(i);
                            bitmapDrawable2.draw(canvas2);
                        } catch (Exception e2) {
                            onBitmapException(bitmapDrawable2);
                            FileLog.e((Throwable) e2);
                        }
                    }
                    canvas.restore();
                } else {
                    int i7 = orientation;
                    float var_ = scaleW2;
                    canvas.save();
                    if (i7 % 360 != 0) {
                        if (this.centerRotation) {
                            canvas2.rotate((float) i7, this.imageW / 2.0f, this.imageH / 2.0f);
                        } else {
                            canvas2.rotate((float) i7, 0.0f, 0.0f);
                        }
                    }
                    RectF rectF4 = this.drawRegion;
                    float var_ = this.imageX;
                    float var_ = this.imageY;
                    rectF4.set(var_, var_, this.imageW + var_, this.imageH + var_);
                    if (this.isRoundVideo) {
                        this.drawRegion.inset((float) (-AndroidUtilities.roundMessageInset), (float) (-AndroidUtilities.roundMessageInset));
                    }
                    if (bitmapDrawable2 instanceof AnimatedFileDrawable) {
                        ((AnimatedFileDrawable) bitmapDrawable2).setActualDrawRect(this.imageX, this.imageY, this.imageW, this.imageH);
                    }
                    if (i7 % 360 == 90 || i7 % 360 == 270) {
                        float width2 = this.drawRegion.width() / 2.0f;
                        float height2 = this.drawRegion.height() / 2.0f;
                        float centerX2 = this.drawRegion.centerX();
                        float centerY2 = this.drawRegion.centerY();
                        bitmapDrawable2.setBounds((int) (centerX2 - height2), (int) (centerY2 - width2), (int) (centerX2 + height2), (int) (centerY2 + width2));
                    } else {
                        bitmapDrawable2.setBounds((int) this.drawRegion.left, (int) this.drawRegion.top, (int) this.drawRegion.right, (int) this.drawRegion.bottom);
                    }
                    if (this.isVisible) {
                        try {
                            if (Build.VERSION.SDK_INT >= 29) {
                                if (this.blendMode != null) {
                                    bitmapDrawable2.getPaint().setBlendMode((BlendMode) this.blendMode);
                                } else {
                                    bitmapDrawable2.getPaint().setBlendMode((BlendMode) null);
                                }
                            }
                            bitmapDrawable2.setAlpha(i);
                            bitmapDrawable2.draw(canvas2);
                        } catch (Exception e3) {
                            onBitmapException(bitmapDrawable2);
                            FileLog.e((Throwable) e3);
                        }
                    }
                    canvas.restore();
                }
            } else if (this.isAspectFit) {
                float scale2 = Math.max(scaleW, scaleH);
                int bitmapW5 = (int) (((float) bitmapW) / scale2);
                int bitmapH4 = (int) (((float) bitmapH) / scale2);
                RectF rectF5 = this.drawRegion;
                float var_ = this.imageX;
                float var_ = this.imageW;
                Paint paint3 = paint;
                float var_ = this.imageY;
                float var_ = this.imageH;
                float var_ = scaleH;
                float var_ = scaleW;
                rectF5.set(((var_ - ((float) bitmapW5)) / 2.0f) + var_, ((var_ - ((float) bitmapH4)) / 2.0f) + var_, var_ + ((var_ + ((float) bitmapW5)) / 2.0f), var_ + ((var_ + ((float) bitmapH4)) / 2.0f));
                if (this.isVisible) {
                    this.roundPaint.setShader(bitmapShader);
                    this.shaderMatrix.reset();
                    this.shaderMatrix.setTranslate(this.drawRegion.left, this.drawRegion.top);
                    this.shaderMatrix.preScale(1.0f / scale2, 1.0f / scale2);
                    bitmapShader.setLocalMatrix(this.shaderMatrix);
                    this.roundPaint.setAlpha(i);
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
                        int a = 0;
                        while (true) {
                            int[] iArr2 = this.roundRadius;
                            if (a >= iArr2.length) {
                                break;
                            }
                            float[] fArr = radii;
                            fArr[a * 2] = (float) iArr2[a];
                            fArr[(a * 2) + 1] = (float) iArr2[a];
                            a++;
                        }
                        this.roundPath.reset();
                        this.roundPath.addRoundRect(this.roundRect, radii, Path.Direction.CW);
                        this.roundPath.close();
                        canvas2.drawPath(this.roundPath, this.roundPaint);
                    }
                }
                int i8 = orientation;
            } else {
                float scaleH3 = scaleH;
                Paint paint4 = paint;
                float scaleW3 = scaleW;
                if (this.legacyCanvas != null) {
                    this.roundRect.set(0.0f, 0.0f, (float) this.legacyBitmap.getWidth(), (float) this.legacyBitmap.getHeight());
                    this.legacyCanvas.drawBitmap(this.gradientBitmap, (Rect) null, this.roundRect, (Paint) null);
                    this.legacyCanvas.drawBitmap(bitmapDrawable.getBitmap(), (Rect) null, this.roundRect, this.legacyPaint);
                }
                if (bitmapShader != this.imageShader || this.gradientShader == null) {
                    this.roundPaint.setShader(bitmapShader);
                } else {
                    ComposeShader composeShader2 = this.composeShader;
                    if (composeShader2 != null) {
                        this.roundPaint.setShader(composeShader2);
                    } else {
                        this.roundPaint.setShader(this.legacyShader);
                    }
                }
                float scaleH4 = scaleH3;
                float scaleW4 = scaleW3;
                float scale3 = 1.0f / Math.min(scaleW4, scaleH4);
                RectF rectF6 = this.roundRect;
                float var_ = this.imageX;
                float var_ = this.sideClip;
                float var_ = this.imageY;
                BitmapDrawable bitmapDrawable3 = bitmapDrawable;
                rectF6.set(var_ + var_, var_ + var_, (var_ + this.imageW) - var_, (var_ + this.imageH) - var_);
                if (Math.abs(scaleW4 - scaleH4) <= 5.0E-4f) {
                    RectF rectF7 = this.drawRegion;
                    float var_ = this.imageX;
                    float var_ = this.imageY;
                    rectF7.set(var_, var_, var_ + realImageW, var_ + realImageH);
                } else if (((float) bitmapW) / scaleH4 > realImageW) {
                    int bitmapW6 = (int) (((float) bitmapW) / scaleH4);
                    RectF rectF8 = this.drawRegion;
                    float var_ = this.imageX;
                    float var_ = this.imageY;
                    rectF8.set(var_ - ((((float) bitmapW6) - realImageW) / 2.0f), var_, var_ + ((((float) bitmapW6) + realImageW) / 2.0f), var_ + realImageH);
                } else {
                    int bitmapH5 = (int) (((float) bitmapH) / scaleW4);
                    RectF rectF9 = this.drawRegion;
                    float var_ = this.imageX;
                    float var_ = this.imageY;
                    rectF9.set(var_, var_ - ((((float) bitmapH5) - realImageH) / 2.0f), var_ + realImageW, var_ + ((((float) bitmapH5) + realImageH) / 2.0f));
                }
                if (this.isVisible) {
                    this.shaderMatrix.reset();
                    this.shaderMatrix.setTranslate(this.drawRegion.left + this.sideClip, this.drawRegion.top + this.sideClip);
                    int i9 = orientation;
                    if (i9 == 90) {
                        this.shaderMatrix.preRotate(90.0f);
                        this.shaderMatrix.preTranslate(0.0f, -this.drawRegion.width());
                    } else if (i9 == 180) {
                        this.shaderMatrix.preRotate(180.0f);
                        this.shaderMatrix.preTranslate(-this.drawRegion.width(), -this.drawRegion.height());
                    } else if (i9 == 270) {
                        this.shaderMatrix.preRotate(270.0f);
                        this.shaderMatrix.preTranslate(-this.drawRegion.height(), 0.0f);
                    }
                    this.shaderMatrix.preScale(scale3, scale3);
                    if (this.isRoundVideo) {
                        float postScale = (realImageW + ((float) (AndroidUtilities.roundMessageInset * 2))) / realImageW;
                        this.shaderMatrix.postScale(postScale, postScale, this.drawRegion.centerX(), this.drawRegion.centerY());
                    }
                    BitmapShader bitmapShader2 = this.legacyShader;
                    if (bitmapShader2 != null) {
                        bitmapShader2.setLocalMatrix(this.shaderMatrix);
                    }
                    bitmapShader.setLocalMatrix(this.shaderMatrix);
                    if (this.composeShader != null) {
                        int bitmapW22 = this.gradientBitmap.getWidth();
                        int bitmapH22 = this.gradientBitmap.getHeight();
                        float scaleW22 = this.imageW == 0.0f ? 1.0f : ((float) bitmapW22) / realImageW;
                        float scaleH22 = this.imageH == 0.0f ? 1.0f : ((float) bitmapH22) / realImageH;
                        if (Math.abs(scaleW22 - scaleH22) <= 5.0E-4f) {
                            int bitmapW23 = bitmapW22;
                            float var_ = scale3;
                            float var_ = scaleH22;
                            RectF rectvar_ = this.drawRegion;
                            float var_ = this.imageX;
                            float var_ = this.imageY;
                            rectvar_.set(var_, var_, var_ + realImageW, var_ + realImageH);
                            bitmapW2 = bitmapW23;
                        } else if (((float) bitmapW22) / scaleH22 > realImageW) {
                            bitmapW2 = (int) (((float) bitmapW22) / scaleH22);
                            RectF rectvar_ = this.drawRegion;
                            float var_ = this.imageX;
                            float var_ = scale3;
                            float scale4 = this.imageY;
                            float var_ = scaleH22;
                            rectvar_.set(var_ - ((((float) bitmapW2) - realImageW) / 2.0f), scale4, var_ + ((((float) bitmapW2) + realImageW) / 2.0f), scale4 + realImageH);
                        } else {
                            float var_ = scaleH22;
                            bitmapH22 = (int) (((float) bitmapH22) / scaleW22);
                            RectF rectvar_ = this.drawRegion;
                            float var_ = this.imageX;
                            float var_ = this.imageY;
                            rectvar_.set(var_, var_ - ((((float) bitmapH22) - realImageH) / 2.0f), var_ + realImageW, var_ + ((((float) bitmapH22) + realImageH) / 2.0f));
                            bitmapW2 = bitmapW22;
                        }
                        float scale5 = 1.0f / Math.min(this.imageW == 0.0f ? 1.0f : ((float) bitmapW2) / realImageW, this.imageH == 0.0f ? 1.0f : ((float) bitmapH22) / realImageH);
                        this.shaderMatrix.reset();
                        this.shaderMatrix.setTranslate(this.drawRegion.left + this.sideClip, this.drawRegion.top + this.sideClip);
                        this.shaderMatrix.preScale(scale5, scale5);
                        this.gradientShader.setLocalMatrix(this.shaderMatrix);
                    } else {
                        float var_ = scale3;
                    }
                    this.roundPaint.setAlpha(i);
                    if (this.isRoundRect) {
                        try {
                            int[] iArr3 = this.roundRadius;
                            if (iArr3[0] == 0) {
                                canvas2.drawRect(this.roundRect, this.roundPaint);
                            } else {
                                canvas2.drawRoundRect(this.roundRect, (float) iArr3[0], (float) iArr3[0], this.roundPaint);
                            }
                            BitmapDrawable bitmapDrawable4 = bitmapDrawable3;
                        } catch (Exception e5) {
                            onBitmapException(bitmapDrawable3);
                            FileLog.e((Throwable) e5);
                        }
                    } else {
                        int a2 = 0;
                        while (true) {
                            int[] iArr4 = this.roundRadius;
                            if (a2 >= iArr4.length) {
                                break;
                            }
                            float[] fArr2 = radii;
                            fArr2[a2 * 2] = (float) iArr4[a2];
                            fArr2[(a2 * 2) + 1] = (float) iArr4[a2];
                            a2++;
                        }
                        this.roundPath.reset();
                        this.roundPath.addRoundRect(this.roundRect, radii, Path.Direction.CW);
                        this.roundPath.close();
                        canvas2.drawPath(this.roundPath, this.roundPaint);
                    }
                } else {
                    int i10 = orientation;
                    float var_ = scale3;
                    BitmapDrawable bitmapDrawable5 = bitmapDrawable3;
                }
            }
            Drawable drawable3 = drawable;
            return;
        }
        int i11 = i2;
        float scaleH5 = 1.0f;
        if (this.isAspectFit) {
            int bitmapW7 = drawable.getIntrinsicWidth();
            int bitmapH6 = drawable.getIntrinsicHeight();
            float var_ = this.imageW;
            float var_ = this.sideClip;
            float realImageW2 = var_ - (var_ * 2.0f);
            float var_ = this.imageH;
            float realImageH2 = var_ - (var_ * 2.0f);
            float scaleW5 = var_ == 0.0f ? 1.0f : ((float) bitmapW7) / realImageW2;
            if (var_ != 0.0f) {
                scaleH5 = ((float) bitmapH6) / realImageH2;
            }
            float scale6 = Math.max(scaleW5, scaleH5);
            int bitmapW8 = (int) (((float) bitmapW7) / scale6);
            int bitmapH7 = (int) (((float) bitmapH6) / scale6);
            RectF rectvar_ = this.drawRegion;
            float var_ = this.imageX;
            float var_ = this.imageW;
            float var_ = this.imageY;
            float var_ = this.imageH;
            float var_ = scaleW5;
            float var_ = realImageH2;
            rectvar_.set(((var_ - ((float) bitmapW8)) / 2.0f) + var_, ((var_ - ((float) bitmapH7)) / 2.0f) + var_, var_ + ((var_ + ((float) bitmapW8)) / 2.0f), var_ + ((var_ + ((float) bitmapH7)) / 2.0f));
        } else {
            RectF rectvar_ = this.drawRegion;
            float var_ = this.imageX;
            float var_ = this.imageY;
            rectvar_.set(var_, var_, this.imageW + var_, this.imageH + var_);
        }
        Drawable drawable4 = drawable;
        drawable4.setBounds((int) this.drawRegion.left, (int) this.drawRegion.top, (int) this.drawRegion.right, (int) this.drawRegion.bottom);
        if (this.isVisible) {
            try {
                drawable.setAlpha(alpha);
                drawable4.draw(canvas2);
            } catch (Exception e6) {
                FileLog.e((Throwable) e6);
            }
        }
    }

    public void setBlendMode(Object mode) {
        this.blendMode = mode;
        invalidate();
    }

    public void setGradientBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            if (this.gradientShader == null || this.gradientBitmap != bitmap) {
                this.gradientShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
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
        setImage(this.currentMediaLocation, this.currentMediaFilter, this.currentImageLocation, this.currentImageFilter, this.currentThumbLocation, this.currentThumbFilter, this.currentThumbDrawable, this.currentSize, this.currentExt, this.currentParentObject, this.currentCacheType);
    }

    private void checkAlphaAnimation(boolean skip) {
        if (!this.manualAlphaAnimator && this.currentAlpha != 1.0f) {
            if (!skip) {
                long dt = System.currentTimeMillis() - this.lastUpdateAlphaTime;
                if (dt > 18) {
                    dt = 18;
                }
                float f = this.currentAlpha + (((float) dt) / ((float) this.crossfadeDuration));
                this.currentAlpha = f;
                if (f > 1.0f) {
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
            float f2 = this.imageX;
            float f3 = this.imageY;
            view.invalidate((int) f2, (int) f3, (int) (f2 + this.imageW), (int) (f3 + this.imageH));
        }
    }

    public void skipDraw() {
        RLottieDrawable lottieDrawable = getLottieAnimation();
        if (lottieDrawable != null) {
            lottieDrawable.setCurrentParentView(this.parentView);
            lottieDrawable.updateCurrentFrame();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:104:0x0187 A[Catch:{ Exception -> 0x01d7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x014f A[Catch:{ Exception -> 0x01d7 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean draw(android.graphics.Canvas r21) {
        /*
            r20 = this;
            r7 = r20
            r8 = r21
            r9 = 0
            android.graphics.Bitmap r0 = r7.gradientBitmap
            if (r0 == 0) goto L_0x0022
            java.lang.String r0 = r7.currentImageKey
            if (r0 == 0) goto L_0x0022
            r21.save()
            float r0 = r7.imageX
            float r1 = r7.imageY
            float r2 = r7.imageW
            float r2 = r2 + r0
            float r3 = r7.imageH
            float r3 = r3 + r1
            r8.clipRect(r0, r1, r2, r3)
            r0 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r8.drawColor(r0)
        L_0x0022:
            r0 = 0
            org.telegram.ui.Components.AnimatedFileDrawable r1 = r20.getAnimation()     // Catch:{ Exception -> 0x01d7 }
            r10 = r1
            org.telegram.ui.Components.RLottieDrawable r1 = r20.getLottieAnimation()     // Catch:{ Exception -> 0x01d7 }
            r11 = r1
            r13 = 1
            if (r10 == 0) goto L_0x0036
            boolean r1 = r10.hasBitmap()     // Catch:{ Exception -> 0x01d7 }
            if (r1 == 0) goto L_0x003e
        L_0x0036:
            if (r11 == 0) goto L_0x0040
            boolean r1 = r11.hasBitmap()     // Catch:{ Exception -> 0x01d7 }
            if (r1 != 0) goto L_0x0040
        L_0x003e:
            r1 = 1
            goto L_0x0041
        L_0x0040:
            r1 = 0
        L_0x0041:
            if (r10 == 0) goto L_0x0048
            int[] r2 = r7.roundRadius     // Catch:{ Exception -> 0x01d7 }
            r10.setRoundRadius(r2)     // Catch:{ Exception -> 0x01d7 }
        L_0x0048:
            if (r11 == 0) goto L_0x004f
            android.view.View r2 = r7.parentView     // Catch:{ Exception -> 0x01d7 }
            r11.setCurrentParentView(r2)     // Catch:{ Exception -> 0x01d7 }
        L_0x004f:
            if (r10 != 0) goto L_0x0053
            if (r11 == 0) goto L_0x0062
        L_0x0053:
            if (r1 != 0) goto L_0x0062
            boolean r2 = r7.animationReadySent     // Catch:{ Exception -> 0x01d7 }
            if (r2 != 0) goto L_0x0062
            r7.animationReadySent = r13     // Catch:{ Exception -> 0x01d7 }
            org.telegram.messenger.ImageReceiver$ImageReceiverDelegate r2 = r7.delegate     // Catch:{ Exception -> 0x01d7 }
            if (r2 == 0) goto L_0x0062
            r2.onAnimationReady(r7)     // Catch:{ Exception -> 0x01d7 }
        L_0x0062:
            r2 = 0
            r3 = 0
            boolean r4 = r7.forcePreview     // Catch:{ Exception -> 0x01d7 }
            if (r4 != 0) goto L_0x007b
            android.graphics.drawable.Drawable r5 = r7.currentMediaDrawable     // Catch:{ Exception -> 0x01d7 }
            if (r5 == 0) goto L_0x007b
            if (r1 != 0) goto L_0x007b
            r0 = r5
            android.graphics.BitmapShader r4 = r7.mediaShader     // Catch:{ Exception -> 0x01d7 }
            r3 = r4
            int r4 = r7.imageOrientation     // Catch:{ Exception -> 0x01d7 }
            r2 = r4
            r14 = r1
            r15 = r2
            r16 = r3
            goto L_0x00ce
        L_0x007b:
            if (r4 != 0) goto L_0x0094
            android.graphics.drawable.Drawable r4 = r7.currentImageDrawable     // Catch:{ Exception -> 0x01d7 }
            if (r4 == 0) goto L_0x0094
            if (r1 == 0) goto L_0x0087
            android.graphics.drawable.Drawable r5 = r7.currentMediaDrawable     // Catch:{ Exception -> 0x01d7 }
            if (r5 == 0) goto L_0x0094
        L_0x0087:
            r0 = r4
            android.graphics.BitmapShader r4 = r7.imageShader     // Catch:{ Exception -> 0x01d7 }
            r3 = r4
            int r4 = r7.imageOrientation     // Catch:{ Exception -> 0x01d7 }
            r2 = r4
            r1 = 0
            r14 = r1
            r15 = r2
            r16 = r3
            goto L_0x00ce
        L_0x0094:
            android.graphics.drawable.Drawable r4 = r7.crossfadeImage     // Catch:{ Exception -> 0x01d7 }
            if (r4 == 0) goto L_0x00a8
            boolean r5 = r7.crossfadingWithThumb     // Catch:{ Exception -> 0x01d7 }
            if (r5 != 0) goto L_0x00a8
            r0 = r4
            android.graphics.BitmapShader r4 = r7.crossfadeShader     // Catch:{ Exception -> 0x01d7 }
            r3 = r4
            int r4 = r7.imageOrientation     // Catch:{ Exception -> 0x01d7 }
            r2 = r4
            r14 = r1
            r15 = r2
            r16 = r3
            goto L_0x00ce
        L_0x00a8:
            android.graphics.drawable.Drawable r4 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01d7 }
            boolean r5 = r4 instanceof android.graphics.drawable.BitmapDrawable     // Catch:{ Exception -> 0x01d7 }
            if (r5 == 0) goto L_0x00ba
            r0 = r4
            android.graphics.BitmapShader r4 = r7.thumbShader     // Catch:{ Exception -> 0x01d7 }
            r3 = r4
            int r4 = r7.thumbOrientation     // Catch:{ Exception -> 0x01d7 }
            r2 = r4
            r14 = r1
            r15 = r2
            r16 = r3
            goto L_0x00ce
        L_0x00ba:
            android.graphics.drawable.Drawable r4 = r7.currentThumbDrawable     // Catch:{ Exception -> 0x01d7 }
            if (r4 == 0) goto L_0x00ca
            r0 = r4
            android.graphics.BitmapShader r4 = r7.thumbShader     // Catch:{ Exception -> 0x01d7 }
            r3 = r4
            int r4 = r7.thumbOrientation     // Catch:{ Exception -> 0x01d7 }
            r2 = r4
            r14 = r1
            r15 = r2
            r16 = r3
            goto L_0x00ce
        L_0x00ca:
            r14 = r1
            r15 = r2
            r16 = r3
        L_0x00ce:
            r17 = 1132396544(0x437var_, float:255.0)
            if (r0 == 0) goto L_0x01bb
            byte r1 = r7.crossfadeAlpha     // Catch:{ Exception -> 0x01d7 }
            if (r1 == 0) goto L_0x019d
            boolean r1 = r7.crossfadeWithThumb     // Catch:{ Exception -> 0x01d7 }
            if (r1 == 0) goto L_0x00ee
            if (r14 == 0) goto L_0x00ee
            float r1 = r7.overrideAlpha     // Catch:{ Exception -> 0x01d7 }
            float r1 = r1 * r17
            int r4 = (int) r1     // Catch:{ Exception -> 0x01d7 }
            r1 = r20
            r2 = r21
            r3 = r0
            r5 = r16
            r6 = r15
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x01d7 }
            goto L_0x01ad
        L_0x00ee:
            if (r1 == 0) goto L_0x0188
            float r1 = r7.currentAlpha     // Catch:{ Exception -> 0x01d7 }
            r2 = 1065353216(0x3var_, float:1.0)
            int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r3 == 0) goto L_0x0188
            r3 = 0
            r4 = 0
            android.graphics.drawable.Drawable r5 = r7.currentImageDrawable     // Catch:{ Exception -> 0x01d7 }
            if (r0 == r5) goto L_0x0126
            android.graphics.drawable.Drawable r5 = r7.currentMediaDrawable     // Catch:{ Exception -> 0x01d7 }
            if (r0 != r5) goto L_0x0103
            goto L_0x0126
        L_0x0103:
            android.graphics.drawable.Drawable r5 = r7.currentThumbDrawable     // Catch:{ Exception -> 0x01d7 }
            if (r0 == r5) goto L_0x011a
            android.graphics.drawable.Drawable r5 = r7.crossfadeImage     // Catch:{ Exception -> 0x01d7 }
            if (r0 != r5) goto L_0x010c
            goto L_0x011a
        L_0x010c:
            android.graphics.drawable.Drawable r6 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01d7 }
            if (r0 != r6) goto L_0x014a
            if (r5 == 0) goto L_0x014a
            r3 = r5
            android.graphics.BitmapShader r5 = r7.crossfadeShader     // Catch:{ Exception -> 0x01d7 }
            r4 = r5
            r6 = r3
            r18 = r4
            goto L_0x014d
        L_0x011a:
            android.graphics.drawable.Drawable r5 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01d7 }
            if (r5 == 0) goto L_0x014a
            r3 = r5
            android.graphics.BitmapShader r5 = r7.thumbShader     // Catch:{ Exception -> 0x01d7 }
            r4 = r5
            r6 = r3
            r18 = r4
            goto L_0x014d
        L_0x0126:
            android.graphics.drawable.Drawable r5 = r7.crossfadeImage     // Catch:{ Exception -> 0x01d7 }
            if (r5 == 0) goto L_0x0132
            r3 = r5
            android.graphics.BitmapShader r5 = r7.crossfadeShader     // Catch:{ Exception -> 0x01d7 }
            r4 = r5
            r6 = r3
            r18 = r4
            goto L_0x014d
        L_0x0132:
            android.graphics.drawable.Drawable r5 = r7.currentThumbDrawable     // Catch:{ Exception -> 0x01d7 }
            if (r5 == 0) goto L_0x013e
            r3 = r5
            android.graphics.BitmapShader r5 = r7.thumbShader     // Catch:{ Exception -> 0x01d7 }
            r4 = r5
            r6 = r3
            r18 = r4
            goto L_0x014d
        L_0x013e:
            android.graphics.drawable.Drawable r5 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01d7 }
            if (r5 == 0) goto L_0x014a
            r3 = r5
            android.graphics.BitmapShader r5 = r7.thumbShader     // Catch:{ Exception -> 0x01d7 }
            r4 = r5
            r6 = r3
            r18 = r4
            goto L_0x014d
        L_0x014a:
            r6 = r3
            r18 = r4
        L_0x014d:
            if (r6 == 0) goto L_0x0187
            boolean r3 = r6 instanceof org.telegram.messenger.SvgHelper.SvgDrawable     // Catch:{ Exception -> 0x01d7 }
            if (r3 != 0) goto L_0x015f
            boolean r3 = r6 instanceof org.telegram.messenger.Emoji.EmojiDrawable     // Catch:{ Exception -> 0x01d7 }
            if (r3 == 0) goto L_0x0158
            goto L_0x015f
        L_0x0158:
            float r1 = r7.overrideAlpha     // Catch:{ Exception -> 0x01d7 }
            float r1 = r1 * r17
            int r1 = (int) r1     // Catch:{ Exception -> 0x01d7 }
            r5 = r1
            goto L_0x0168
        L_0x015f:
            float r3 = r7.overrideAlpha     // Catch:{ Exception -> 0x01d7 }
            float r3 = r3 * r17
            float r2 = r2 - r1
            float r3 = r3 * r2
            int r1 = (int) r3     // Catch:{ Exception -> 0x01d7 }
            r5 = r1
        L_0x0168:
            int r4 = r7.thumbOrientation     // Catch:{ Exception -> 0x01d7 }
            r1 = r20
            r2 = r21
            r3 = r6
            r19 = r4
            r4 = r5
            r12 = r5
            r5 = r18
            r13 = r6
            r6 = r19
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x01d7 }
            r1 = 255(0xff, float:3.57E-43)
            if (r12 == r1) goto L_0x0188
            boolean r2 = r13 instanceof org.telegram.messenger.Emoji.EmojiDrawable     // Catch:{ Exception -> 0x01d7 }
            if (r2 == 0) goto L_0x0188
            r13.setAlpha(r1)     // Catch:{ Exception -> 0x01d7 }
            goto L_0x0188
        L_0x0187:
            r13 = r6
        L_0x0188:
            float r1 = r7.overrideAlpha     // Catch:{ Exception -> 0x01d7 }
            float r2 = r7.currentAlpha     // Catch:{ Exception -> 0x01d7 }
            float r1 = r1 * r2
            float r1 = r1 * r17
            int r4 = (int) r1     // Catch:{ Exception -> 0x01d7 }
            r1 = r20
            r2 = r21
            r3 = r0
            r5 = r16
            r6 = r15
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x01d7 }
            goto L_0x01ad
        L_0x019d:
            float r1 = r7.overrideAlpha     // Catch:{ Exception -> 0x01d7 }
            float r1 = r1 * r17
            int r4 = (int) r1     // Catch:{ Exception -> 0x01d7 }
            r1 = r20
            r2 = r21
            r3 = r0
            r5 = r16
            r6 = r15
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x01d7 }
        L_0x01ad:
            if (r14 == 0) goto L_0x01b5
            boolean r1 = r7.crossfadeWithThumb     // Catch:{ Exception -> 0x01d7 }
            if (r1 == 0) goto L_0x01b5
            r12 = 1
            goto L_0x01b6
        L_0x01b5:
            r12 = 0
        L_0x01b6:
            r7.checkAlphaAnimation(r12)     // Catch:{ Exception -> 0x01d7 }
            r9 = 1
            goto L_0x01d6
        L_0x01bb:
            android.graphics.drawable.Drawable r3 = r7.staticThumbDrawable     // Catch:{ Exception -> 0x01d7 }
            if (r3 == 0) goto L_0x01d3
            float r1 = r7.overrideAlpha     // Catch:{ Exception -> 0x01d7 }
            float r1 = r1 * r17
            int r4 = (int) r1     // Catch:{ Exception -> 0x01d7 }
            r5 = 0
            int r6 = r7.thumbOrientation     // Catch:{ Exception -> 0x01d7 }
            r1 = r20
            r2 = r21
            r1.drawDrawable(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x01d7 }
            r7.checkAlphaAnimation(r14)     // Catch:{ Exception -> 0x01d7 }
            r9 = 1
            goto L_0x01d6
        L_0x01d3:
            r7.checkAlphaAnimation(r14)     // Catch:{ Exception -> 0x01d7 }
        L_0x01d6:
            goto L_0x01db
        L_0x01d7:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01db:
            android.graphics.Bitmap r0 = r7.gradientBitmap
            if (r0 == 0) goto L_0x01e6
            java.lang.String r0 = r7.currentImageKey
            if (r0 == 0) goto L_0x01e6
            r21.restore()
        L_0x01e6:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageReceiver.draw(android.graphics.Canvas):boolean");
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
        RLottieDrawable lottieDrawable = getLottieAnimation();
        if (lottieDrawable != null && lottieDrawable.hasBitmap()) {
            return lottieDrawable.getAnimatedBitmap();
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

    public BitmapHolder getBitmapSafe() {
        Bitmap bitmap = null;
        String key = null;
        AnimatedFileDrawable animation = getAnimation();
        RLottieDrawable lottieDrawable = getLottieAnimation();
        int orientation = 0;
        if (lottieDrawable != null && lottieDrawable.hasBitmap()) {
            bitmap = lottieDrawable.getAnimatedBitmap();
        } else if (animation == null || !animation.hasBitmap()) {
            Drawable drawable = this.currentMediaDrawable;
            if (!(drawable instanceof BitmapDrawable) || (drawable instanceof AnimatedFileDrawable) || (drawable instanceof RLottieDrawable)) {
                Drawable drawable2 = this.currentImageDrawable;
                if (!(drawable2 instanceof BitmapDrawable) || (drawable2 instanceof AnimatedFileDrawable) || (drawable instanceof RLottieDrawable)) {
                    Drawable drawable3 = this.currentThumbDrawable;
                    if (!(drawable3 instanceof BitmapDrawable) || (drawable3 instanceof AnimatedFileDrawable) || (drawable instanceof RLottieDrawable)) {
                        Drawable drawable4 = this.staticThumbDrawable;
                        if (drawable4 instanceof BitmapDrawable) {
                            bitmap = ((BitmapDrawable) drawable4).getBitmap();
                        }
                    } else {
                        bitmap = ((BitmapDrawable) drawable3).getBitmap();
                        key = this.currentThumbKey;
                    }
                } else {
                    bitmap = ((BitmapDrawable) drawable2).getBitmap();
                    key = this.currentImageKey;
                }
            } else {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
                key = this.currentMediaKey;
            }
        } else {
            bitmap = animation.getAnimatedBitmap();
            orientation = animation.getOrientation();
            if (orientation != 0) {
                return new BitmapHolder(Bitmap.createBitmap(bitmap), (String) null, orientation);
            }
        }
        if (bitmap != null) {
            return new BitmapHolder(bitmap, key, orientation);
        }
        return null;
    }

    public BitmapHolder getDrawableSafe() {
        Drawable drawable = null;
        String key = null;
        Drawable drawable2 = this.currentMediaDrawable;
        if (!(drawable2 instanceof BitmapDrawable) || (drawable2 instanceof AnimatedFileDrawable) || (drawable2 instanceof RLottieDrawable)) {
            Drawable drawable3 = this.currentImageDrawable;
            if (!(drawable3 instanceof BitmapDrawable) || (drawable3 instanceof AnimatedFileDrawable) || (drawable2 instanceof RLottieDrawable)) {
                Drawable drawable4 = this.currentThumbDrawable;
                if ((drawable4 instanceof BitmapDrawable) && !(drawable4 instanceof AnimatedFileDrawable) && !(drawable2 instanceof RLottieDrawable)) {
                    drawable = this.currentThumbDrawable;
                    key = this.currentThumbKey;
                } else if (this.staticThumbDrawable instanceof BitmapDrawable) {
                    drawable = this.staticThumbDrawable;
                }
            } else {
                drawable = this.currentImageDrawable;
                key = this.currentImageKey;
            }
        } else {
            drawable = this.currentMediaDrawable;
            key = this.currentMediaKey;
        }
        if (drawable != null) {
            return new BitmapHolder(drawable, key, 0);
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
        Bitmap bitmap = null;
        String key = null;
        Drawable drawable = this.currentThumbDrawable;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            key = this.currentThumbKey;
        } else {
            Drawable drawable2 = this.staticThumbDrawable;
            if (drawable2 instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable2).getBitmap();
            }
        }
        if (bitmap != null) {
            return new BitmapHolder(bitmap, key, 0);
        }
        return null;
    }

    public int getBitmapWidth() {
        Drawable drawable = getDrawable();
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            int i = this.imageOrientation;
            return (i % 360 == 0 || i % 360 == 180) ? animation.getIntrinsicWidth() : animation.getIntrinsicHeight();
        }
        RLottieDrawable lottieDrawable = getLottieAnimation();
        if (lottieDrawable != null) {
            return lottieDrawable.getIntrinsicWidth();
        }
        Bitmap bitmap = getBitmap();
        if (bitmap == null) {
            Drawable drawable2 = this.staticThumbDrawable;
            if (drawable2 != null) {
                return drawable2.getIntrinsicWidth();
            }
            return 1;
        }
        int i2 = this.imageOrientation;
        return (i2 % 360 == 0 || i2 % 360 == 180) ? bitmap.getWidth() : bitmap.getHeight();
    }

    public int getBitmapHeight() {
        Drawable drawable = getDrawable();
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            int i = this.imageOrientation;
            return (i % 360 == 0 || i % 360 == 180) ? animation.getIntrinsicHeight() : animation.getIntrinsicWidth();
        }
        RLottieDrawable lottieDrawable = getLottieAnimation();
        if (lottieDrawable != null) {
            return lottieDrawable.getIntrinsicHeight();
        }
        Bitmap bitmap = getBitmap();
        if (bitmap == null) {
            Drawable drawable2 = this.staticThumbDrawable;
            if (drawable2 != null) {
                return drawable2.getIntrinsicHeight();
            }
            return 1;
        }
        int i2 = this.imageOrientation;
        return (i2 % 360 == 0 || i2 % 360 == 180) ? bitmap.getHeight() : bitmap.getWidth();
    }

    public void setVisible(boolean value, boolean invalidate) {
        if (this.isVisible != value) {
            this.isVisible = value;
            if (invalidate) {
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

    public void getParentPosition(int[] position) {
        View view = this.parentView;
        if (view != null) {
            view.getLocationInWindow(position);
        }
    }

    public boolean getVisible() {
        return this.isVisible;
    }

    public void setAlpha(float value) {
        this.overrideAlpha = value;
    }

    public float getAlpha() {
        return this.overrideAlpha;
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
        this.imageX = (float) x;
    }

    public void setImageY(float y) {
        this.imageY = y;
    }

    public void setImageWidth(int width) {
        this.imageW = (float) width;
    }

    public void setImageCoords(float x, float y, float width, float height) {
        this.imageX = x;
        this.imageY = y;
        this.imageW = width;
        this.imageH = height;
    }

    public void setSideClip(float value) {
        this.sideClip = value;
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

    public boolean isInsideImage(float x, float y) {
        float f = this.imageX;
        if (x >= f && x <= f + this.imageW) {
            float f2 = this.imageY;
            return y >= f2 && y <= f2 + this.imageH;
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
        setRoundRadius(new int[]{value, value, value, value});
    }

    public void setRoundRadius(int tl, int tr, int bl, int br) {
        setRoundRadius(new int[]{tl, tr, bl, br});
    }

    public void setRoundRadius(int[] value) {
        boolean changed = false;
        int firstValue = value[0];
        this.isRoundRect = true;
        int a = 0;
        while (true) {
            int[] iArr = this.roundRadius;
            if (a >= iArr.length) {
                break;
            }
            if (iArr[a] != value[a]) {
                changed = true;
            }
            if (firstValue != value[a]) {
                this.isRoundRect = false;
            }
            iArr[a] = value[a];
            a++;
        }
        if (changed) {
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

    public void setCurrentAccount(int value) {
        this.currentAccount = value;
    }

    public int[] getRoundRadius() {
        return this.roundRadius;
    }

    public Object getParentObject() {
        return this.currentParentObject;
    }

    public void setNeedsQualityThumb(boolean value) {
        this.needsQualityThumb = value;
    }

    public void setQualityThumbDocument(TLRPC.Document document) {
        this.qulityThumbDocument = document;
    }

    public TLRPC.Document getQulityThumbDocument() {
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

    public boolean getAllowStartAnimation() {
        return this.allowStartAnimation;
    }

    public void setAllowStartLottieAnimation(boolean value) {
        this.allowStartLottieAnimation = value;
    }

    public void setAllowDecodeSingleFrame(boolean value) {
        this.allowDecodeSingleFrame = value;
    }

    public void setAutoRepeat(int value) {
        this.autoRepeat = value;
        RLottieDrawable drawable = getLottieAnimation();
        if (drawable != null) {
            drawable.setAutoRepeat(value);
        }
    }

    public void setUseSharedAnimationQueue(boolean value) {
        this.useSharedAnimationQueue = value;
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
        RLottieDrawable rLottieDrawable = getLottieAnimation();
        if (rLottieDrawable != null && !rLottieDrawable.isRunning()) {
            rLottieDrawable.restart();
        }
    }

    public void stopAnimation() {
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            animation.stop();
            return;
        }
        RLottieDrawable rLottieDrawable = getLottieAnimation();
        if (rLottieDrawable != null && !rLottieDrawable.isRunning()) {
            rLottieDrawable.stop();
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
    public int getTag(int type) {
        if (type == 1) {
            return this.thumbTag;
        }
        if (type == 3) {
            return this.mediaTag;
        }
        return this.imageTag;
    }

    /* access modifiers changed from: protected */
    public void setTag(int value, int type) {
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

    /* access modifiers changed from: protected */
    public boolean setImageBitmapByKey(Drawable drawable, String key, int type, boolean memCache, int guid) {
        Drawable drawable2;
        if (drawable == null || key == null || this.currentGuid != guid) {
            return false;
        }
        if (type == 0) {
            if (!key.equals(this.currentImageKey)) {
                return false;
            }
            if (!(drawable instanceof AnimatedFileDrawable)) {
                ImageLoader.getInstance().incrementUseCount(this.currentImageKey);
            } else {
                ((AnimatedFileDrawable) drawable).setStartEndTime(this.startTime, this.endTime);
            }
            this.currentImageDrawable = drawable;
            if (drawable instanceof ExtendedBitmapDrawable) {
                this.imageOrientation = ((ExtendedBitmapDrawable) drawable).getOrientation();
            }
            updateDrawableRadius(drawable);
            if (!this.isVisible || (((memCache || this.forcePreview) && !this.forceCrossfade) || this.crossfadeDuration == 0)) {
                this.currentAlpha = 1.0f;
            } else {
                boolean allowCorssfade = true;
                Drawable drawable3 = this.currentMediaDrawable;
                if ((drawable3 instanceof AnimatedFileDrawable) && ((AnimatedFileDrawable) drawable3).hasBitmap()) {
                    allowCorssfade = false;
                } else if (this.currentImageDrawable instanceof RLottieDrawable) {
                    Drawable drawable4 = this.staticThumbDrawable;
                    allowCorssfade = (drawable4 instanceof LoadingStickerDrawable) || (drawable4 instanceof SvgHelper.SvgDrawable) || (drawable4 instanceof Emoji.EmojiDrawable);
                }
                if (allowCorssfade && ((this.currentThumbDrawable == null && this.staticThumbDrawable == null) || this.currentAlpha == 1.0f || this.forceCrossfade)) {
                    this.currentAlpha = 0.0f;
                    this.lastUpdateAlphaTime = System.currentTimeMillis();
                    this.crossfadeWithThumb = (this.crossfadeImage == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null) ? false : true;
                }
            }
        } else if (type == 3) {
            if (!key.equals(this.currentMediaKey)) {
                return false;
            }
            if (!(drawable instanceof AnimatedFileDrawable)) {
                ImageLoader.getInstance().incrementUseCount(this.currentMediaKey);
            } else {
                ((AnimatedFileDrawable) drawable).setStartEndTime(this.startTime, this.endTime);
            }
            this.currentMediaDrawable = drawable;
            updateDrawableRadius(drawable);
            if (this.currentImageDrawable == null) {
                if ((memCache || this.forcePreview) && !this.forceCrossfade) {
                    this.currentAlpha = 1.0f;
                } else if ((this.currentThumbDrawable == null && this.staticThumbDrawable == null) || this.currentAlpha == 1.0f || this.forceCrossfade) {
                    this.currentAlpha = 0.0f;
                    this.lastUpdateAlphaTime = System.currentTimeMillis();
                    this.crossfadeWithThumb = (this.crossfadeImage == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null) ? false : true;
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
                Drawable drawable5 = this.currentImageDrawable;
                if ((drawable5 != null && !(drawable5 instanceof AnimatedFileDrawable)) || ((drawable2 = this.currentMediaDrawable) != null && !(drawable2 instanceof AnimatedFileDrawable))) {
                    return false;
                }
            }
            if (!key.equals(this.currentThumbKey)) {
                return false;
            }
            ImageLoader.getInstance().incrementUseCount(this.currentThumbKey);
            this.currentThumbDrawable = drawable;
            if (drawable instanceof ExtendedBitmapDrawable) {
                this.thumbOrientation = ((ExtendedBitmapDrawable) drawable).getOrientation();
            }
            updateDrawableRadius(drawable);
            if (memCache || this.crossfadeAlpha == 2) {
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
        ImageReceiverDelegate imageReceiverDelegate = this.delegate;
        if (imageReceiverDelegate != null) {
            Drawable drawable6 = this.currentImageDrawable;
            imageReceiverDelegate.didSetImage(this, (drawable6 == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true, drawable6 == null && this.currentMediaDrawable == null, memCache);
        }
        if (drawable instanceof AnimatedFileDrawable) {
            AnimatedFileDrawable fileDrawable = (AnimatedFileDrawable) drawable;
            fileDrawable.setParentView(this.parentView);
            fileDrawable.setUseSharedQueue(this.useSharedAnimationQueue);
            if (this.allowStartAnimation && this.currentOpenedLayerFlags == 0) {
                fileDrawable.start();
            }
            fileDrawable.setAllowDecodeSingleFrame(this.allowDecodeSingleFrame);
            this.animationReadySent = false;
        } else if (drawable instanceof RLottieDrawable) {
            RLottieDrawable fileDrawable2 = (RLottieDrawable) drawable;
            fileDrawable2.addParentView(this.parentView);
            if (this.allowStartLottieAnimation && (!fileDrawable2.isHeavyDrawable() || this.currentOpenedLayerFlags == 0)) {
                fileDrawable2.start();
            }
            fileDrawable2.setAllowDecodeSingleFrame(true);
            fileDrawable2.setAutoRepeat(this.autoRepeat);
            this.animationReadySent = false;
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
        return true;
    }

    public void setMediaStartEndTime(long startTime2, long endTime2) {
        this.startTime = startTime2;
        this.endTime = endTime2;
        Drawable drawable = this.currentMediaDrawable;
        if (drawable instanceof AnimatedFileDrawable) {
            ((AnimatedFileDrawable) drawable).setStartEndTime(startTime2, endTime2);
        }
    }

    private void recycleBitmap(String newKey, int type) {
        Drawable image;
        String key;
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
        if (key != null && ((key.startsWith("-") || key.startsWith("strippedmessage-")) && (replacedKey = ImageLoader.getInstance().getReplacedKey(key)) != null)) {
            key = replacedKey;
        }
        if (image instanceof RLottieDrawable) {
            ((RLottieDrawable) image).removeParentView(this.parentView);
        }
        if (key != null && ((newKey == null || !newKey.equals(key)) && image != null)) {
            if (image instanceof RLottieDrawable) {
                RLottieDrawable fileDrawable = (RLottieDrawable) image;
                boolean canDelete = ImageLoader.getInstance().decrementUseCount(key);
                if (!ImageLoader.getInstance().isInMemCache(key, true) && canDelete) {
                    fileDrawable.recycle();
                }
            } else if (image instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) image).recycle();
            } else if (image instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
                boolean canDelete2 = ImageLoader.getInstance().decrementUseCount(key);
                if (!ImageLoader.getInstance().isInMemCache(key, false) && canDelete2) {
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

    public void setCrossfadeDuration(int duration) {
        this.crossfadeDuration = duration;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int i;
        if (id == NotificationCenter.didReplacedPhotoInMemCache) {
            String oldKey = args[0];
            String str = this.currentMediaKey;
            if (str != null && str.equals(oldKey)) {
                this.currentMediaKey = args[1];
                this.currentMediaLocation = args[2];
                SetImageBackup setImageBackup2 = this.setImageBackup;
                if (setImageBackup2 != null) {
                    setImageBackup2.mediaLocation = args[2];
                }
            }
            String str2 = this.currentImageKey;
            if (str2 != null && str2.equals(oldKey)) {
                this.currentImageKey = args[1];
                this.currentImageLocation = args[2];
                SetImageBackup setImageBackup3 = this.setImageBackup;
                if (setImageBackup3 != null) {
                    setImageBackup3.imageLocation = args[2];
                }
            }
            String str3 = this.currentThumbKey;
            if (str3 != null && str3.equals(oldKey)) {
                this.currentThumbKey = args[1];
                this.currentThumbLocation = args[2];
                SetImageBackup setImageBackup4 = this.setImageBackup;
                if (setImageBackup4 != null) {
                    setImageBackup4.thumbLocation = args[2];
                }
            }
        } else if (id == NotificationCenter.stopAllHeavyOperations) {
            Integer layer = args[0];
            if (this.currentLayerNum < layer.intValue()) {
                int intValue = this.currentOpenedLayerFlags | layer.intValue();
                this.currentOpenedLayerFlags = intValue;
                if (intValue != 0) {
                    RLottieDrawable lottieDrawable = getLottieAnimation();
                    if (lottieDrawable != null && lottieDrawable.isHeavyDrawable()) {
                        lottieDrawable.stop();
                    }
                    AnimatedFileDrawable animatedFileDrawable = getAnimation();
                    if (animatedFileDrawable != null) {
                        animatedFileDrawable.stop();
                    }
                }
            }
        } else if (id == NotificationCenter.startAllHeavyOperations) {
            Integer layer2 = args[0];
            if (this.currentLayerNum < layer2.intValue() && (i = this.currentOpenedLayerFlags) != 0) {
                int intValue2 = i & (layer2.intValue() ^ -1);
                this.currentOpenedLayerFlags = intValue2;
                if (intValue2 == 0) {
                    RLottieDrawable lottieDrawable2 = getLottieAnimation();
                    if (this.allowStartLottieAnimation && lottieDrawable2 != null && lottieDrawable2.isHeavyDrawable()) {
                        lottieDrawable2.start();
                    }
                    AnimatedFileDrawable animatedFileDrawable2 = getAnimation();
                    if (this.allowStartAnimation && animatedFileDrawable2 != null) {
                        animatedFileDrawable2.start();
                    }
                }
            }
        }
    }

    public void startCrossfadeFromStaticThumb(Bitmap thumb) {
        this.currentThumbKey = null;
        this.currentThumbDrawable = null;
        this.thumbShader = null;
        BitmapDrawable bitmapDrawable = new BitmapDrawable((Resources) null, thumb);
        this.staticThumbDrawable = bitmapDrawable;
        this.crossfadeWithThumb = true;
        this.currentAlpha = 0.0f;
        updateDrawableRadius(bitmapDrawable);
    }

    public void setUniqKeyPrefix(String prefix) {
        this.uniqKeyPrefix = prefix;
    }

    public String getUniqKeyPrefix() {
        return this.uniqKeyPrefix;
    }

    public void addLoadingImageRunnable(Runnable loadOperationRunnable) {
        this.loadingOperations.add(loadOperationRunnable);
    }

    public ArrayList<Runnable> getLoadingOperations() {
        return this.loadingOperations;
    }

    public void moveImageToFront() {
        ImageLoader.getInstance().moveToFront(this.currentImageKey);
        ImageLoader.getInstance().moveToFront(this.currentThumbKey);
    }
}
