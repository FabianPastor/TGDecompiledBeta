package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.RLottieDrawable;
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
    private float sideClip;
    private Drawable staticThumbDrawable;
    private ImageLocation strippedLocation;
    private int thumbOrientation;
    private BitmapShader thumbShader;
    private int thumbTag;
    private boolean useSharedAnimationQueue;

    public static class BitmapHolder {
        public Bitmap bitmap;
        private String key;
        private boolean recycleOnRelease;

        public BitmapHolder(Bitmap bitmap, String str) {
            this.bitmap = bitmap;
            this.key = str;
            if (this.key != null) {
                ImageLoader.getInstance().incrementUseCount(this.key);
            }
        }

        public BitmapHolder(Bitmap bitmap) {
            this.bitmap = bitmap;
            this.recycleOnRelease = true;
        }

        public int getWidth() {
            Bitmap bitmap = this.bitmap;
            return bitmap != null ? bitmap.getWidth() : 0;
        }

        public int getHeight() {
            Bitmap bitmap = this.bitmap;
            return bitmap != null ? bitmap.getHeight() : 0;
        }

        public boolean isRecycled() {
            Bitmap bitmap = this.bitmap;
            return bitmap == null || bitmap.isRecycled();
        }

        public void release() {
            if (this.key == null) {
                if (this.recycleOnRelease) {
                    Bitmap bitmap = this.bitmap;
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
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

    public interface ImageReceiverDelegate {
        void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2);
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
    }

    public ImageReceiver() {
        this(null);
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

    public void setForceLoading(boolean z) {
        this.forceLoding = z;
    }

    public boolean isForceLoding() {
        return this.forceLoding;
    }

    public void setStrippedLocation(ImageLocation imageLocation) {
        this.strippedLocation = imageLocation;
    }

    public ImageLocation getStrippedLocation() {
        return this.strippedLocation;
    }

    public void setImage(ImageLocation imageLocation, String str, Drawable drawable, String str2, Object obj, int i) {
        setImage(imageLocation, str, null, null, drawable, 0, str2, obj, i);
    }

    public void setImage(ImageLocation imageLocation, String str, Drawable drawable, int i, String str2, Object obj, int i2) {
        setImage(imageLocation, str, null, null, drawable, i, str2, obj, i2);
    }

    public void setImage(String str, String str2, Drawable drawable, String str3, int i) {
        setImage(ImageLocation.getForPath(str), str2, null, null, drawable, i, str3, null, 1);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, String str3, Object obj, int i) {
        setImage(imageLocation, str, imageLocation2, str2, null, 0, str3, obj, i);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, int i, String str3, Object obj, int i2) {
        setImage(imageLocation, str, imageLocation2, str2, null, i, str3, obj, i2);
    }

    public void setImage(ImageLocation imageLocation, String str, ImageLocation imageLocation2, String str2, Drawable drawable, int i, String str3, Object obj, int i2) {
        setImage(null, null, imageLocation, str, imageLocation2, str2, drawable, i, str3, obj, i2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:67:0x0121  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x011c  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x013f  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x014d  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x0187  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0193  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x018e  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x022d  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x01b0  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x026e  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x029a  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x011c  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0121  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x013f  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x014d  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x0187  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x018e  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0193  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x01b0  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x022d  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x026e  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x029a  */
    /* JADX WARNING: Missing block: B:78:0x0147, code skipped:
            if (r10.equals(r14) != false) goto L_0x0153;
     */
    /* JADX WARNING: Missing block: B:82:0x0151, code skipped:
            if (r10.equals(r15) != false) goto L_0x0153;
     */
    /* JADX WARNING: Missing block: B:83:0x0153, code skipped:
            r10 = r0.delegate;
     */
    /* JADX WARNING: Missing block: B:84:0x0155, code skipped:
            if (r10 == null) goto L_0x0179;
     */
    /* JADX WARNING: Missing block: B:86:0x0159, code skipped:
            if (r0.currentImageDrawable != null) goto L_0x016a;
     */
    /* JADX WARNING: Missing block: B:88:0x015d, code skipped:
            if (r0.currentThumbDrawable != null) goto L_0x016a;
     */
    /* JADX WARNING: Missing block: B:90:0x0161, code skipped:
            if (r0.staticThumbDrawable != null) goto L_0x016a;
     */
    /* JADX WARNING: Missing block: B:92:0x0165, code skipped:
            if (r0.currentMediaDrawable == null) goto L_0x0168;
     */
    /* JADX WARNING: Missing block: B:93:0x0168, code skipped:
            r11 = false;
     */
    /* JADX WARNING: Missing block: B:94:0x016a, code skipped:
            r11 = true;
     */
    /* JADX WARNING: Missing block: B:96:0x016d, code skipped:
            if (r0.currentImageDrawable != null) goto L_0x0175;
     */
    /* JADX WARNING: Missing block: B:98:0x0171, code skipped:
            if (r0.currentMediaDrawable != null) goto L_0x0175;
     */
    /* JADX WARNING: Missing block: B:99:0x0173, code skipped:
            r12 = true;
     */
    /* JADX WARNING: Missing block: B:100:0x0175, code skipped:
            r12 = false;
     */
    /* JADX WARNING: Missing block: B:101:0x0176, code skipped:
            r10.didSetImage(r0, r11, r12);
     */
    /* JADX WARNING: Missing block: B:103:0x017b, code skipped:
            if (r0.canceledLoading != false) goto L_0x0182;
     */
    /* JADX WARNING: Missing block: B:105:0x017f, code skipped:
            if (r0.forcePreview != false) goto L_0x0182;
     */
    /* JADX WARNING: Missing block: B:106:0x0181, code skipped:
            return;
     */
    public void setImage(org.telegram.messenger.ImageLocation r21, java.lang.String r22, org.telegram.messenger.ImageLocation r23, java.lang.String r24, org.telegram.messenger.ImageLocation r25, java.lang.String r26, android.graphics.drawable.Drawable r27, int r28, java.lang.String r29, java.lang.Object r30, int r31) {
        /*
        r20 = this;
        r0 = r20;
        r1 = r21;
        r2 = r22;
        r3 = r23;
        r4 = r24;
        r5 = r25;
        r6 = r26;
        r7 = r27;
        r8 = r29;
        r9 = r30;
        r10 = r0.setImageBackup;
        r11 = 0;
        if (r10 == 0) goto L_0x0021;
    L_0x0019:
        r10.imageLocation = r11;
        r10.thumbLocation = r11;
        r10.mediaLocation = r11;
        r10.thumb = r11;
    L_0x0021:
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r12 = 1;
        r13 = 0;
        if (r3 != 0) goto L_0x00a5;
    L_0x0027:
        if (r5 != 0) goto L_0x00a5;
    L_0x0029:
        if (r1 != 0) goto L_0x00a5;
    L_0x002b:
        r1 = 0;
    L_0x002c:
        r2 = 4;
        if (r1 >= r2) goto L_0x0035;
    L_0x002f:
        r0.recycleBitmap(r11, r1);
        r1 = r1 + 1;
        goto L_0x002c;
    L_0x0035:
        r0.currentImageLocation = r11;
        r0.currentImageFilter = r11;
        r0.currentImageKey = r11;
        r0.currentMediaLocation = r11;
        r0.currentMediaFilter = r11;
        r0.currentMediaKey = r11;
        r0.currentThumbLocation = r11;
        r0.currentThumbFilter = r11;
        r0.currentThumbKey = r11;
        r0.currentMediaDrawable = r11;
        r0.mediaShader = r11;
        r0.currentImageDrawable = r11;
        r0.imageShader = r11;
        r0.thumbShader = r11;
        r0.crossfadeShader = r11;
        r0.currentExt = r8;
        r0.currentParentObject = r11;
        r0.currentCacheType = r13;
        r0.staticThumbDrawable = r7;
        r0.currentAlpha = r10;
        r0.currentSize = r13;
        r1 = org.telegram.messenger.ImageLoader.getInstance();
        r1.cancelLoadingForImageReceiver(r0, r12);
        r1 = r0.parentView;
        if (r1 == 0) goto L_0x007f;
    L_0x006a:
        r2 = r0.invalidateAll;
        if (r2 == 0) goto L_0x0072;
    L_0x006e:
        r1.invalidate();
        goto L_0x007f;
    L_0x0072:
        r2 = r0.imageX;
        r3 = r0.imageY;
        r4 = r0.imageW;
        r4 = r4 + r2;
        r5 = r0.imageH;
        r5 = r5 + r3;
        r1.invalidate(r2, r3, r4, r5);
    L_0x007f:
        r1 = r0.delegate;
        if (r1 == 0) goto L_0x00a4;
    L_0x0083:
        r2 = r0.currentImageDrawable;
        if (r2 != 0) goto L_0x0096;
    L_0x0087:
        r2 = r0.currentThumbDrawable;
        if (r2 != 0) goto L_0x0096;
    L_0x008b:
        r2 = r0.staticThumbDrawable;
        if (r2 != 0) goto L_0x0096;
    L_0x008f:
        r2 = r0.currentMediaDrawable;
        if (r2 == 0) goto L_0x0094;
    L_0x0093:
        goto L_0x0096;
    L_0x0094:
        r2 = 0;
        goto L_0x0097;
    L_0x0096:
        r2 = 1;
    L_0x0097:
        r3 = r0.currentImageDrawable;
        if (r3 != 0) goto L_0x00a0;
    L_0x009b:
        r3 = r0.currentMediaDrawable;
        if (r3 != 0) goto L_0x00a0;
    L_0x009f:
        goto L_0x00a1;
    L_0x00a0:
        r12 = 0;
    L_0x00a1:
        r1.didSetImage(r0, r2, r12);
    L_0x00a4:
        return;
    L_0x00a5:
        if (r3 == 0) goto L_0x00ac;
    L_0x00a7:
        r14 = r3.getKey(r9, r11);
        goto L_0x00ad;
    L_0x00ac:
        r14 = r11;
    L_0x00ad:
        if (r14 != 0) goto L_0x00b2;
    L_0x00af:
        if (r3 == 0) goto L_0x00b2;
    L_0x00b1:
        r3 = r11;
    L_0x00b2:
        r0.currentKeyQuality = r13;
        if (r14 != 0) goto L_0x00fe;
    L_0x00b6:
        r15 = r0.needsQualityThumb;
        if (r15 == 0) goto L_0x00fe;
    L_0x00ba:
        r15 = r9 instanceof org.telegram.messenger.MessageObject;
        if (r15 != 0) goto L_0x00c2;
    L_0x00be:
        r15 = r0.qulityThumbDocument;
        if (r15 == 0) goto L_0x00fe;
    L_0x00c2:
        r15 = r0.qulityThumbDocument;
        if (r15 == 0) goto L_0x00c7;
    L_0x00c6:
        goto L_0x00ce;
    L_0x00c7:
        r15 = r9;
        r15 = (org.telegram.messenger.MessageObject) r15;
        r15 = r15.getDocument();
    L_0x00ce:
        if (r15 == 0) goto L_0x00fe;
    L_0x00d0:
        r10 = r15.dc_id;
        if (r10 == 0) goto L_0x00fe;
    L_0x00d4:
        r16 = r14;
        r13 = r15.id;
        r17 = 0;
        r19 = (r13 > r17 ? 1 : (r13 == r17 ? 0 : -1));
        if (r19 == 0) goto L_0x0100;
    L_0x00de:
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r14 = "q_";
        r13.append(r14);
        r14 = r15.dc_id;
        r13.append(r14);
        r14 = "_";
        r13.append(r14);
        r14 = r15.id;
        r13.append(r14);
        r14 = r13.toString();
        r0.currentKeyQuality = r12;
        goto L_0x0102;
    L_0x00fe:
        r16 = r14;
    L_0x0100:
        r14 = r16;
    L_0x0102:
        r13 = "@";
        if (r14 == 0) goto L_0x011a;
    L_0x0106:
        if (r4 == 0) goto L_0x011a;
    L_0x0108:
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r15.append(r14);
        r15.append(r13);
        r15.append(r4);
        r14 = r15.toString();
    L_0x011a:
        if (r1 == 0) goto L_0x0121;
    L_0x011c:
        r15 = r1.getKey(r9, r11);
        goto L_0x0122;
    L_0x0121:
        r15 = r11;
    L_0x0122:
        if (r15 != 0) goto L_0x0127;
    L_0x0124:
        if (r1 == 0) goto L_0x0127;
    L_0x0126:
        r1 = r11;
    L_0x0127:
        if (r15 == 0) goto L_0x013d;
    L_0x0129:
        if (r2 == 0) goto L_0x013d;
    L_0x012b:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r15);
        r10.append(r13);
        r10.append(r2);
        r15 = r10.toString();
    L_0x013d:
        if (r15 != 0) goto L_0x0149;
    L_0x013f:
        r10 = r0.currentImageKey;
        if (r10 == 0) goto L_0x0149;
    L_0x0143:
        r10 = r10.equals(r14);
        if (r10 != 0) goto L_0x0153;
    L_0x0149:
        r10 = r0.currentMediaKey;
        if (r10 == 0) goto L_0x0182;
    L_0x014d:
        r10 = r10.equals(r15);
        if (r10 == 0) goto L_0x0182;
    L_0x0153:
        r10 = r0.delegate;
        if (r10 == 0) goto L_0x0179;
    L_0x0157:
        r11 = r0.currentImageDrawable;
        if (r11 != 0) goto L_0x016a;
    L_0x015b:
        r11 = r0.currentThumbDrawable;
        if (r11 != 0) goto L_0x016a;
    L_0x015f:
        r11 = r0.staticThumbDrawable;
        if (r11 != 0) goto L_0x016a;
    L_0x0163:
        r11 = r0.currentMediaDrawable;
        if (r11 == 0) goto L_0x0168;
    L_0x0167:
        goto L_0x016a;
    L_0x0168:
        r11 = 0;
        goto L_0x016b;
    L_0x016a:
        r11 = 1;
    L_0x016b:
        r12 = r0.currentImageDrawable;
        if (r12 != 0) goto L_0x0175;
    L_0x016f:
        r12 = r0.currentMediaDrawable;
        if (r12 != 0) goto L_0x0175;
    L_0x0173:
        r12 = 1;
        goto L_0x0176;
    L_0x0175:
        r12 = 0;
    L_0x0176:
        r10.didSetImage(r0, r11, r12);
    L_0x0179:
        r10 = r0.canceledLoading;
        if (r10 != 0) goto L_0x0182;
    L_0x017d:
        r10 = r0.forcePreview;
        if (r10 != 0) goto L_0x0182;
    L_0x0181:
        return;
    L_0x0182:
        r10 = r0.strippedLocation;
        if (r10 == 0) goto L_0x0187;
    L_0x0186:
        goto L_0x018c;
    L_0x0187:
        if (r1 == 0) goto L_0x018b;
    L_0x0189:
        r10 = r1;
        goto L_0x018c;
    L_0x018b:
        r10 = r3;
    L_0x018c:
        if (r5 == 0) goto L_0x0193;
    L_0x018e:
        r11 = r5.getKey(r9, r10);
        goto L_0x0194;
    L_0x0193:
        r11 = 0;
    L_0x0194:
        if (r11 == 0) goto L_0x01aa;
    L_0x0196:
        if (r6 == 0) goto L_0x01aa;
    L_0x0198:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r11);
        r10.append(r13);
        r10.append(r6);
        r11 = r10.toString();
    L_0x01aa:
        r10 = r0.crossfadeWithOldImage;
        r12 = 3;
        r13 = 2;
        if (r10 == 0) goto L_0x022d;
    L_0x01b0:
        r10 = r0.currentImageDrawable;
        if (r10 == 0) goto L_0x01d4;
    L_0x01b4:
        r10 = 1;
        r0.recycleBitmap(r11, r10);
        r10 = 0;
        r0.recycleBitmap(r10, r13);
        r0.recycleBitmap(r15, r12);
        r12 = r0.imageShader;
        r0.crossfadeShader = r12;
        r12 = r0.currentImageDrawable;
        r0.crossfadeImage = r12;
        r12 = r0.currentImageKey;
        r0.crossfadeKey = r12;
        r12 = 0;
        r0.crossfadingWithThumb = r12;
        r0.currentImageDrawable = r10;
        r0.currentImageKey = r10;
    L_0x01d2:
        r12 = 1;
        goto L_0x023f;
    L_0x01d4:
        r12 = 0;
        r10 = r0.currentThumbDrawable;
        if (r10 == 0) goto L_0x01f7;
    L_0x01d9:
        r0.recycleBitmap(r14, r12);
        r10 = 0;
        r0.recycleBitmap(r10, r13);
        r13 = 3;
        r0.recycleBitmap(r15, r13);
        r13 = r0.thumbShader;
        r0.crossfadeShader = r13;
        r13 = r0.currentThumbDrawable;
        r0.crossfadeImage = r13;
        r13 = r0.currentThumbKey;
        r0.crossfadeKey = r13;
        r0.crossfadingWithThumb = r12;
        r0.currentThumbDrawable = r10;
        r0.currentThumbKey = r10;
        goto L_0x01d2;
    L_0x01f7:
        r10 = r0.staticThumbDrawable;
        if (r10 == 0) goto L_0x021b;
    L_0x01fb:
        r0.recycleBitmap(r14, r12);
        r10 = 1;
        r0.recycleBitmap(r11, r10);
        r10 = 0;
        r0.recycleBitmap(r10, r13);
        r13 = 3;
        r0.recycleBitmap(r15, r13);
        r13 = r0.thumbShader;
        r0.crossfadeShader = r13;
        r13 = r0.staticThumbDrawable;
        r0.crossfadeImage = r13;
        r0.crossfadingWithThumb = r12;
        r0.crossfadeKey = r10;
        r0.currentThumbDrawable = r10;
        r0.currentThumbKey = r10;
        goto L_0x01d2;
    L_0x021b:
        r10 = 0;
        r0.recycleBitmap(r14, r12);
        r12 = 1;
        r0.recycleBitmap(r11, r12);
        r0.recycleBitmap(r10, r13);
        r13 = 3;
        r0.recycleBitmap(r15, r13);
        r0.crossfadeShader = r10;
        goto L_0x023f;
    L_0x022d:
        r10 = 0;
        r12 = 1;
        r0.recycleBitmap(r14, r10);
        r0.recycleBitmap(r11, r12);
        r10 = 0;
        r0.recycleBitmap(r10, r13);
        r13 = 3;
        r0.recycleBitmap(r15, r13);
        r0.crossfadeShader = r10;
    L_0x023f:
        r0.currentImageLocation = r3;
        r0.currentImageFilter = r4;
        r0.currentImageKey = r14;
        r0.currentMediaLocation = r1;
        r0.currentMediaFilter = r2;
        r0.currentMediaKey = r15;
        r0.currentThumbLocation = r5;
        r0.currentThumbFilter = r6;
        r0.currentThumbKey = r11;
        r0.currentParentObject = r9;
        r0.currentExt = r8;
        r1 = r28;
        r0.currentSize = r1;
        r1 = r31;
        r0.currentCacheType = r1;
        r0.staticThumbDrawable = r7;
        r1 = 0;
        r0.imageShader = r1;
        r0.thumbShader = r1;
        r0.mediaShader = r1;
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0.currentAlpha = r1;
        r1 = r0.delegate;
        if (r1 == 0) goto L_0x028f;
    L_0x026e:
        r2 = r0.currentImageDrawable;
        if (r2 != 0) goto L_0x0281;
    L_0x0272:
        r2 = r0.currentThumbDrawable;
        if (r2 != 0) goto L_0x0281;
    L_0x0276:
        r2 = r0.staticThumbDrawable;
        if (r2 != 0) goto L_0x0281;
    L_0x027a:
        r2 = r0.currentMediaDrawable;
        if (r2 == 0) goto L_0x027f;
    L_0x027e:
        goto L_0x0281;
    L_0x027f:
        r2 = 0;
        goto L_0x0282;
    L_0x0281:
        r2 = 1;
    L_0x0282:
        r3 = r0.currentImageDrawable;
        if (r3 != 0) goto L_0x028b;
    L_0x0286:
        r3 = r0.currentMediaDrawable;
        if (r3 != 0) goto L_0x028b;
    L_0x028a:
        goto L_0x028c;
    L_0x028b:
        r12 = 0;
    L_0x028c:
        r1.didSetImage(r0, r2, r12);
    L_0x028f:
        r1 = org.telegram.messenger.ImageLoader.getInstance();
        r1.loadImageForImageReceiver(r0);
        r1 = r0.parentView;
        if (r1 == 0) goto L_0x02af;
    L_0x029a:
        r2 = r0.invalidateAll;
        if (r2 == 0) goto L_0x02a2;
    L_0x029e:
        r1.invalidate();
        goto L_0x02af;
    L_0x02a2:
        r2 = r0.imageX;
        r3 = r0.imageY;
        r4 = r0.imageW;
        r4 = r4 + r2;
        r5 = r0.imageH;
        r5 = r5 + r3;
        r1.invalidate(r2, r3, r4, r5);
    L_0x02af:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageReceiver.setImage(org.telegram.messenger.ImageLocation, java.lang.String, org.telegram.messenger.ImageLocation, java.lang.String, org.telegram.messenger.ImageLocation, java.lang.String, android.graphics.drawable.Drawable, int, java.lang.String, java.lang.Object, int):void");
    }

    public boolean canInvertBitmap() {
        return (this.currentMediaDrawable instanceof ExtendedBitmapDrawable) || (this.currentImageDrawable instanceof ExtendedBitmapDrawable) || (this.currentThumbDrawable instanceof ExtendedBitmapDrawable) || (this.staticThumbDrawable instanceof ExtendedBitmapDrawable);
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
        return animation != null ? animation.getOrientation() : 0;
    }

    public int getOrientation() {
        return this.imageOrientation;
    }

    public void setLayerNum(int i) {
        this.currentLayerNum = i;
    }

    public void setImageBitmap(Bitmap bitmap) {
        Drawable drawable = null;
        if (bitmap != null) {
            drawable = new BitmapDrawable(null, bitmap);
        }
        setImageBitmap(drawable);
    }

    public void setImageBitmap(Drawable drawable) {
        int i;
        boolean z = true;
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
        if (!this.crossfadeWithOldImage) {
            for (i = 0; i < 4; i++) {
                recycleBitmap(null, i);
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
            for (i = 0; i < 4; i++) {
                recycleBitmap(null, i);
            }
            this.crossfadeShader = null;
        }
        Drawable drawable2 = this.staticThumbDrawable;
        if (drawable2 instanceof RecyclableDrawable) {
            ((RecyclableDrawable) drawable2).recycle();
        }
        boolean z2 = drawable instanceof AnimatedFileDrawable;
        if (z2) {
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
        int i2 = this.roundRadius;
        if (i2 == 0 || !(drawable instanceof BitmapDrawable)) {
            this.thumbShader = null;
        } else if (!(drawable instanceof RLottieDrawable)) {
            if (z2) {
                ((AnimatedFileDrawable) drawable).setRoundRadius(i2);
            } else {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                TileMode tileMode = TileMode.CLAMP;
                this.thumbShader = new BitmapShader(bitmap, tileMode, tileMode);
            }
        }
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
        SetImageBackup setImageBackup = this.setImageBackup;
        if (setImageBackup != null) {
            setImageBackup.imageLocation = null;
            setImageBackup.thumbLocation = null;
            setImageBackup.mediaLocation = null;
            setImageBackup.thumb = null;
        }
        ImageReceiverDelegate imageReceiverDelegate = this.delegate;
        if (imageReceiverDelegate != null) {
            z2 = (this.currentThumbDrawable == null && this.staticThumbDrawable == null) ? false : true;
            imageReceiverDelegate.didSetImage(this, z2, true);
        }
        View view = this.parentView;
        if (view != null) {
            if (this.invalidateAll) {
                view.invalidate();
            } else {
                i = this.imageX;
                i2 = this.imageY;
                view.invalidate(i, i2, this.imageW + i, this.imageH + i2);
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

    public void clearImage() {
        for (int i = 0; i < 4; i++) {
            recycleBitmap(null, i);
        }
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
    }

    public void onDetachedFromWindow() {
        if (!(this.currentImageLocation == null && this.currentMediaLocation == null && this.currentThumbLocation == null && this.staticThumbDrawable == null)) {
            if (this.setImageBackup == null) {
                this.setImageBackup = new SetImageBackup();
            }
            SetImageBackup setImageBackup = this.setImageBackup;
            setImageBackup.mediaLocation = this.currentMediaLocation;
            setImageBackup.mediaFilter = this.currentMediaFilter;
            setImageBackup.imageLocation = this.currentImageLocation;
            setImageBackup.imageFilter = this.currentImageFilter;
            setImageBackup.thumbLocation = this.currentThumbLocation;
            setImageBackup.thumbFilter = this.currentThumbFilter;
            setImageBackup.thumb = this.staticThumbDrawable;
            setImageBackup.size = this.currentSize;
            setImageBackup.ext = this.currentExt;
            setImageBackup.cacheType = this.currentCacheType;
            setImageBackup.parentObject = this.currentParentObject;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopAllHeavyOperations);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.startAllHeavyOperations);
        clearImage();
    }

    public boolean onAttachedToWindow() {
        this.currentOpenedLayerFlags = NotificationCenter.getGlobalInstance().getCurrentHeavyOperationFlags();
        this.currentOpenedLayerFlags &= this.currentLayerNum ^ -1;
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopAllHeavyOperations);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.startAllHeavyOperations);
        SetImageBackup setImageBackup = this.setImageBackup;
        RLottieDrawable lottieAnimation;
        if (setImageBackup == null || (setImageBackup.imageLocation == null && setImageBackup.thumbLocation == null && setImageBackup.mediaLocation == null && setImageBackup.thumb == null)) {
            if (this.currentOpenedLayerFlags == 0) {
                lottieAnimation = getLottieAnimation();
                if (lottieAnimation != null) {
                    lottieAnimation.start();
                }
            }
            return false;
        }
        setImageBackup = this.setImageBackup;
        setImage(setImageBackup.mediaLocation, setImageBackup.mediaFilter, setImageBackup.imageLocation, setImageBackup.imageFilter, setImageBackup.thumbLocation, setImageBackup.thumbFilter, setImageBackup.thumb, setImageBackup.size, setImageBackup.ext, setImageBackup.parentObject, setImageBackup.cacheType);
        if (this.currentOpenedLayerFlags == 0) {
            lottieAnimation = getLottieAnimation();
            if (lottieAnimation != null) {
                lottieAnimation.start();
            }
        }
        return true;
    }

    private void drawDrawable(Canvas canvas, Drawable drawable, int i, BitmapShader bitmapShader, int i2) {
        Canvas canvas2 = canvas;
        Drawable drawable2 = drawable;
        int i3 = i;
        Shader shader = bitmapShader;
        int i4 = i2;
        Canvas canvas3;
        int i5;
        int i6;
        RectF rectF;
        float centerY;
        int i7;
        if (drawable2 instanceof BitmapDrawable) {
            Paint paint;
            int i8;
            int i9;
            int intrinsicWidth;
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable2;
            if (shader != null) {
                paint = this.roundPaint;
            } else {
                paint = bitmapDrawable.getPaint();
            }
            Object obj = (paint == null || paint.getColorFilter() == null) ? null : 1;
            if (obj == null || this.isPressed != 0) {
                if (obj == null) {
                    i8 = this.isPressed;
                    if (i8 != 0) {
                        if (i8 == 1) {
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
                }
            } else if (shader != null) {
                this.roundPaint.setColorFilter(null);
            } else if (this.staticThumbDrawable != drawable2) {
                bitmapDrawable.setColorFilter(null);
            }
            ColorFilter colorFilter = this.colorFilter;
            if (colorFilter != null) {
                if (shader != null) {
                    this.roundPaint.setColorFilter(colorFilter);
                } else {
                    bitmapDrawable.setColorFilter(colorFilter);
                }
            }
            boolean z = bitmapDrawable instanceof AnimatedFileDrawable;
            if (z || (bitmapDrawable instanceof RLottieDrawable)) {
                i9 = i4 % 360;
                if (i9 == 90 || i9 == 270) {
                    i9 = bitmapDrawable.getIntrinsicHeight();
                    intrinsicWidth = bitmapDrawable.getIntrinsicWidth();
                } else {
                    i9 = bitmapDrawable.getIntrinsicWidth();
                    intrinsicWidth = bitmapDrawable.getIntrinsicHeight();
                }
            } else {
                i9 = i4 % 360;
                if (i9 == 90 || i9 == 270) {
                    i9 = bitmapDrawable.getBitmap().getHeight();
                    intrinsicWidth = bitmapDrawable.getBitmap().getWidth();
                } else {
                    i9 = bitmapDrawable.getBitmap().getWidth();
                    intrinsicWidth = bitmapDrawable.getBitmap().getHeight();
                }
            }
            int i10 = this.imageW;
            float f = (float) i10;
            float f2 = this.sideClip;
            f -= f2 * 2.0f;
            float f3 = ((float) this.imageH) - (f2 * 2.0f);
            float f4 = i10 == 0 ? 1.0f : ((float) i9) / f;
            float f5 = this.imageH == 0 ? 1.0f : ((float) intrinsicWidth) / f3;
            float max;
            float f6;
            RectF rectF2;
            float f7;
            float width;
            RectF rectF3;
            if (shader == null) {
                canvas3 = canvas2;
                i5 = i3;
                if (this.isAspectFit) {
                    max = Math.max(f4, f5);
                    canvas.save();
                    i6 = (int) (((float) i9) / max);
                    i3 = (int) (((float) intrinsicWidth) / max);
                    RectF rectF4 = this.drawRegion;
                    int i11 = this.imageX;
                    float f8 = (float) i11;
                    intrinsicWidth = this.imageW;
                    f8 += ((float) (intrinsicWidth - i6)) / 2.0f;
                    i10 = this.imageY;
                    f2 = (float) i10;
                    int i12 = this.imageH;
                    rectF4.set(f8, f2 + (((float) (i12 - i3)) / 2.0f), ((float) i11) + (((float) (intrinsicWidth + i6)) / 2.0f), ((float) i10) + (((float) (i12 + i3)) / 2.0f));
                    rectF = this.drawRegion;
                    bitmapDrawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                    if (z) {
                        AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) bitmapDrawable;
                        rectF = this.drawRegion;
                        animatedFileDrawable.setActualDrawRect(rectF.left, rectF.top, rectF.width(), this.drawRegion.height());
                    }
                    if (this.isVisible) {
                        try {
                            bitmapDrawable.setAlpha(i5);
                            bitmapDrawable.draw(canvas3);
                        } catch (Exception e) {
                            onBitmapException(bitmapDrawable);
                            FileLog.e(e);
                        }
                    }
                    canvas.restore();
                    return;
                } else if (Math.abs(f4 - f5) > 1.0E-5f) {
                    canvas.save();
                    i3 = this.imageX;
                    i6 = this.imageY;
                    canvas3.clipRect(i3, i6, this.imageW + i3, this.imageH + i6);
                    i3 = i4 % 360;
                    if (i3 != 0) {
                        if (this.centerRotation) {
                            canvas3.rotate((float) i4, (float) (this.imageW / 2), (float) (this.imageH / 2));
                        } else {
                            canvas3.rotate((float) i4, 0.0f, 0.0f);
                        }
                    }
                    f6 = ((float) i9) / f5;
                    i4 = this.imageW;
                    if (f6 > ((float) i4)) {
                        i6 = (int) f6;
                        rectF2 = this.drawRegion;
                        i9 = this.imageX;
                        f7 = ((float) i9) - (((float) (i6 - i4)) / 2.0f);
                        i10 = this.imageY;
                        rectF2.set(f7, (float) i10, ((float) i9) + (((float) (i6 + i4)) / 2.0f), (float) (i10 + this.imageH));
                    } else {
                        i6 = (int) (((float) intrinsicWidth) / f4);
                        rectF2 = this.drawRegion;
                        i9 = this.imageX;
                        f7 = (float) i9;
                        i10 = this.imageY;
                        f = (float) i10;
                        int i13 = this.imageH;
                        rectF2.set(f7, f - (((float) (i6 - i13)) / 2.0f), (float) (i9 + i4), ((float) i10) + (((float) (i6 + i13)) / 2.0f));
                    }
                    if (z) {
                        ((AnimatedFileDrawable) bitmapDrawable).setActualDrawRect((float) this.imageX, (float) this.imageY, (float) this.imageW, (float) this.imageH);
                    }
                    if (i3 == 90 || i3 == 270) {
                        width = this.drawRegion.width() / 2.0f;
                        f6 = this.drawRegion.height() / 2.0f;
                        max = this.drawRegion.centerX();
                        centerY = this.drawRegion.centerY();
                        bitmapDrawable.setBounds((int) (max - f6), (int) (centerY - width), (int) (max + f6), (int) (centerY + width));
                    } else {
                        rectF3 = this.drawRegion;
                        bitmapDrawable.setBounds((int) rectF3.left, (int) rectF3.top, (int) rectF3.right, (int) rectF3.bottom);
                    }
                    if (this.isVisible) {
                        try {
                            bitmapDrawable.setAlpha(i5);
                            bitmapDrawable.draw(canvas3);
                        } catch (Exception e2) {
                            onBitmapException(bitmapDrawable);
                            FileLog.e(e2);
                        }
                    }
                    canvas.restore();
                    return;
                } else {
                    canvas.save();
                    i3 = i4 % 360;
                    if (i3 != 0) {
                        if (this.centerRotation) {
                            canvas3.rotate((float) i4, (float) (this.imageW / 2), (float) (this.imageH / 2));
                        } else {
                            canvas3.rotate((float) i4, 0.0f, 0.0f);
                        }
                    }
                    RectF rectF5 = this.drawRegion;
                    i4 = this.imageX;
                    f5 = (float) i4;
                    i9 = this.imageY;
                    rectF5.set(f5, (float) i9, (float) (i4 + this.imageW), (float) (i9 + this.imageH));
                    if (z) {
                        ((AnimatedFileDrawable) bitmapDrawable).setActualDrawRect((float) this.imageX, (float) this.imageY, (float) this.imageW, (float) this.imageH);
                    }
                    if (i3 == 90 || i3 == 270) {
                        width = this.drawRegion.width() / 2.0f;
                        f6 = this.drawRegion.height() / 2.0f;
                        max = this.drawRegion.centerX();
                        centerY = this.drawRegion.centerY();
                        bitmapDrawable.setBounds((int) (max - f6), (int) (centerY - width), (int) (max + f6), (int) (centerY + width));
                    } else {
                        rectF3 = this.drawRegion;
                        bitmapDrawable.setBounds((int) rectF3.left, (int) rectF3.top, (int) rectF3.right, (int) rectF3.bottom);
                    }
                    if (this.isVisible) {
                        try {
                            bitmapDrawable.setAlpha(i5);
                            bitmapDrawable.draw(canvas3);
                        } catch (Exception e22) {
                            onBitmapException(bitmapDrawable);
                            FileLog.e(e22);
                        }
                    }
                    canvas.restore();
                    return;
                }
            } else if (this.isAspectFit) {
                width = Math.max(f4, f5);
                i4 = (int) (((float) i9) / width);
                i7 = (int) (((float) intrinsicWidth) / width);
                rectF2 = this.drawRegion;
                int i14 = this.imageX;
                i9 = this.imageW;
                f7 = (float) (((i9 - i4) / 2) + i14);
                i10 = this.imageY;
                int i15 = this.imageH;
                rectF2.set(f7, (float) (((i15 - i7) / 2) + i10), (float) (i14 + ((i9 + i4) / 2)), (float) (i10 + ((i15 + i7) / 2)));
                if (this.isVisible) {
                    this.roundPaint.setShader(shader);
                    this.shaderMatrix.reset();
                    Matrix matrix = this.shaderMatrix;
                    RectF rectF6 = this.drawRegion;
                    matrix.setTranslate(rectF6.left, rectF6.top);
                    f2 = 1.0f / width;
                    this.shaderMatrix.preScale(f2, f2);
                    shader.setLocalMatrix(this.shaderMatrix);
                    this.roundPaint.setAlpha(i3);
                    this.roundRect.set(this.drawRegion);
                    rectF3 = this.roundRect;
                    i3 = this.roundRadius;
                    canvas2.drawRoundRect(rectF3, (float) i3, (float) i3, this.roundPaint);
                    return;
                }
                return;
            } else {
                RectF rectF7;
                this.roundPaint.setShader(shader);
                f2 = 1.0f / Math.min(f4, f5);
                rectF3 = this.roundRect;
                i7 = this.imageX;
                float f9 = (float) i7;
                float var_ = this.sideClip;
                f9 += var_;
                i5 = this.imageY;
                rectF3.set(f9, ((float) i5) + var_, ((float) (i7 + this.imageW)) - var_, ((float) (i5 + this.imageH)) - var_);
                this.shaderMatrix.reset();
                if (Math.abs(f4 - f5) > 5.0E-4f) {
                    width = ((float) i9) / f5;
                    if (width > f) {
                        i8 = (int) width;
                        rectF7 = this.drawRegion;
                        i3 = this.imageX;
                        width = (float) i8;
                        f6 = ((float) i3) - ((width - f) / 2.0f);
                        i7 = this.imageY;
                        rectF7.set(f6, (float) i7, ((float) i3) + ((width + f) / 2.0f), ((float) i7) + f3);
                    } else {
                        i8 = (int) (((float) intrinsicWidth) / f4);
                        rectF7 = this.drawRegion;
                        i3 = this.imageX;
                        f6 = (float) i3;
                        i7 = this.imageY;
                        width = (float) i8;
                        rectF7.set(f6, ((float) i7) - ((width - f3) / 2.0f), ((float) i3) + f, ((float) i7) + ((width + f3) / 2.0f));
                    }
                } else {
                    rectF3 = this.drawRegion;
                    i5 = this.imageX;
                    max = (float) i5;
                    i6 = this.imageY;
                    rectF3.set(max, (float) i6, ((float) i5) + f, ((float) i6) + f3);
                }
                if (this.isVisible) {
                    this.shaderMatrix.reset();
                    Matrix matrix2 = this.shaderMatrix;
                    rectF7 = this.drawRegion;
                    max = rectF7.left;
                    f6 = this.sideClip;
                    matrix2.setTranslate(max + f6, rectF7.top + f6);
                    if (i4 == 90) {
                        this.shaderMatrix.preRotate(90.0f);
                        this.shaderMatrix.preTranslate(0.0f, -this.drawRegion.width());
                    } else if (i4 == 180) {
                        this.shaderMatrix.preRotate(180.0f);
                        this.shaderMatrix.preTranslate(-this.drawRegion.width(), -this.drawRegion.height());
                    } else if (i4 == 270) {
                        this.shaderMatrix.preRotate(270.0f);
                        this.shaderMatrix.preTranslate(-this.drawRegion.height(), 0.0f);
                    }
                    this.shaderMatrix.preScale(f2, f2);
                    bitmapShader.setLocalMatrix(this.shaderMatrix);
                    this.roundPaint.setAlpha(i);
                    rectF3 = this.roundRect;
                    i5 = this.roundRadius;
                    canvas.drawRoundRect(rectF3, (float) i5, (float) i5, this.roundPaint);
                    return;
                }
                return;
            }
        }
        canvas3 = canvas2;
        i5 = i3;
        rectF = this.drawRegion;
        i6 = this.imageX;
        centerY = (float) i6;
        i7 = this.imageY;
        rectF.set(centerY, (float) i7, (float) (i6 + this.imageW), (float) (i7 + this.imageH));
        rectF = this.drawRegion;
        drawable2.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
        if (this.isVisible) {
            try {
                drawable.setAlpha(i);
                drawable2.draw(canvas3);
            } catch (Exception e222) {
                FileLog.e(e222);
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
        if (!(this.manualAlphaAnimator || this.currentAlpha == 1.0f)) {
            if (!z) {
                long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateAlphaTime;
                if (currentTimeMillis > 18) {
                    currentTimeMillis = 18;
                }
                this.currentAlpha += ((float) currentTimeMillis) / 150.0f;
                if (this.currentAlpha > 1.0f) {
                    this.currentAlpha = 1.0f;
                    if (this.crossfadeImage != null) {
                        recycleBitmap(null, 2);
                        this.crossfadeShader = null;
                    }
                }
            }
            this.lastUpdateAlphaTime = System.currentTimeMillis();
            View view = this.parentView;
            if (view != null) {
                if (this.invalidateAll) {
                    view.invalidate();
                } else {
                    int i = this.imageX;
                    int i2 = this.imageY;
                    view.invalidate(i, i2, this.imageW + i, this.imageH + i2);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:82:0x00ef A:{Catch:{ Exception -> 0x0142 }} */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x00ef A:{Catch:{ Exception -> 0x0142 }} */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x00ef A:{Catch:{ Exception -> 0x0142 }} */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x00ef A:{Catch:{ Exception -> 0x0142 }} */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x00ef A:{Catch:{ Exception -> 0x0142 }} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0127 A:{Catch:{ Exception -> 0x0142 }} */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0081 A:{Catch:{ Exception -> 0x0142 }} */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0081 A:{Catch:{ Exception -> 0x0142 }} */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0127 A:{Catch:{ Exception -> 0x0142 }} */
    public boolean draw(android.graphics.Canvas r14) {
        /*
        r13 = this;
        r7 = 0;
        r0 = r13.getAnimation();	 Catch:{ Exception -> 0x0142 }
        r1 = r13.getLottieAnimation();	 Catch:{ Exception -> 0x0142 }
        r8 = 1;
        if (r0 == 0) goto L_0x0012;
    L_0x000c:
        r0 = r0.hasBitmap();	 Catch:{ Exception -> 0x0142 }
        if (r0 == 0) goto L_0x001a;
    L_0x0012:
        if (r1 == 0) goto L_0x001c;
    L_0x0014:
        r0 = r1.hasBitmap();	 Catch:{ Exception -> 0x0142 }
        if (r0 != 0) goto L_0x001c;
    L_0x001a:
        r0 = 1;
        goto L_0x001d;
    L_0x001c:
        r0 = 0;
    L_0x001d:
        if (r1 == 0) goto L_0x0024;
    L_0x001f:
        r2 = r13.parentView;	 Catch:{ Exception -> 0x0142 }
        r1.setCurrentParentView(r2);	 Catch:{ Exception -> 0x0142 }
    L_0x0024:
        r1 = r13.forcePreview;	 Catch:{ Exception -> 0x0142 }
        r2 = 0;
        if (r1 != 0) goto L_0x003a;
    L_0x0029:
        r1 = r13.currentMediaDrawable;	 Catch:{ Exception -> 0x0142 }
        if (r1 == 0) goto L_0x003a;
    L_0x002d:
        if (r0 != 0) goto L_0x003a;
    L_0x002f:
        r1 = r13.currentMediaDrawable;	 Catch:{ Exception -> 0x0142 }
        r3 = r13.mediaShader;	 Catch:{ Exception -> 0x0142 }
        r4 = r13.imageOrientation;	 Catch:{ Exception -> 0x0142 }
    L_0x0035:
        r9 = r0;
        r0 = r1;
        r10 = r3;
        r11 = r4;
        goto L_0x007d;
    L_0x003a:
        r1 = r13.forcePreview;	 Catch:{ Exception -> 0x0142 }
        if (r1 != 0) goto L_0x0052;
    L_0x003e:
        r1 = r13.currentImageDrawable;	 Catch:{ Exception -> 0x0142 }
        if (r1 == 0) goto L_0x0052;
    L_0x0042:
        if (r0 == 0) goto L_0x0048;
    L_0x0044:
        r1 = r13.currentMediaDrawable;	 Catch:{ Exception -> 0x0142 }
        if (r1 == 0) goto L_0x0052;
    L_0x0048:
        r0 = r13.currentImageDrawable;	 Catch:{ Exception -> 0x0142 }
        r1 = r13.imageShader;	 Catch:{ Exception -> 0x0142 }
        r3 = r13.imageOrientation;	 Catch:{ Exception -> 0x0142 }
        r10 = r1;
        r11 = r3;
        r9 = 0;
        goto L_0x007d;
    L_0x0052:
        r1 = r13.crossfadeImage;	 Catch:{ Exception -> 0x0142 }
        if (r1 == 0) goto L_0x0061;
    L_0x0056:
        r1 = r13.crossfadingWithThumb;	 Catch:{ Exception -> 0x0142 }
        if (r1 != 0) goto L_0x0061;
    L_0x005a:
        r1 = r13.crossfadeImage;	 Catch:{ Exception -> 0x0142 }
        r3 = r13.crossfadeShader;	 Catch:{ Exception -> 0x0142 }
        r4 = r13.imageOrientation;	 Catch:{ Exception -> 0x0142 }
        goto L_0x0035;
    L_0x0061:
        r1 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0142 }
        r1 = r1 instanceof android.graphics.drawable.BitmapDrawable;	 Catch:{ Exception -> 0x0142 }
        if (r1 == 0) goto L_0x006e;
    L_0x0067:
        r1 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0142 }
        r3 = r13.thumbShader;	 Catch:{ Exception -> 0x0142 }
        r4 = r13.thumbOrientation;	 Catch:{ Exception -> 0x0142 }
        goto L_0x0035;
    L_0x006e:
        r1 = r13.currentThumbDrawable;	 Catch:{ Exception -> 0x0142 }
        if (r1 == 0) goto L_0x0079;
    L_0x0072:
        r1 = r13.currentThumbDrawable;	 Catch:{ Exception -> 0x0142 }
        r3 = r13.thumbShader;	 Catch:{ Exception -> 0x0142 }
        r4 = r13.thumbOrientation;	 Catch:{ Exception -> 0x0142 }
        goto L_0x0035;
    L_0x0079:
        r9 = r0;
        r0 = r2;
        r10 = r0;
        r11 = 0;
    L_0x007d:
        r12 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        if (r0 == 0) goto L_0x0127;
    L_0x0081:
        r1 = r13.crossfadeAlpha;	 Catch:{ Exception -> 0x0142 }
        if (r1 == 0) goto L_0x010d;
    L_0x0085:
        r1 = r13.crossfadeWithThumb;	 Catch:{ Exception -> 0x0142 }
        if (r1 == 0) goto L_0x009a;
    L_0x0089:
        if (r9 == 0) goto L_0x009a;
    L_0x008b:
        r1 = r13.overrideAlpha;	 Catch:{ Exception -> 0x0142 }
        r1 = r1 * r12;
        r4 = (int) r1;	 Catch:{ Exception -> 0x0142 }
        r1 = r13;
        r2 = r14;
        r3 = r0;
        r5 = r10;
        r6 = r11;
        r1.drawDrawable(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x0142 }
        goto L_0x011a;
    L_0x009a:
        r1 = r13.crossfadeWithThumb;	 Catch:{ Exception -> 0x0142 }
        if (r1 == 0) goto L_0x00fb;
    L_0x009e:
        r1 = r13.currentAlpha;	 Catch:{ Exception -> 0x0142 }
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r1 == 0) goto L_0x00fb;
    L_0x00a6:
        r1 = r13.currentImageDrawable;	 Catch:{ Exception -> 0x0142 }
        if (r0 == r1) goto L_0x00ce;
    L_0x00aa:
        r1 = r13.currentMediaDrawable;	 Catch:{ Exception -> 0x0142 }
        if (r0 != r1) goto L_0x00af;
    L_0x00ae:
        goto L_0x00ce;
    L_0x00af:
        r1 = r13.currentThumbDrawable;	 Catch:{ Exception -> 0x0142 }
        if (r0 == r1) goto L_0x00c5;
    L_0x00b3:
        r1 = r13.crossfadeImage;	 Catch:{ Exception -> 0x0142 }
        if (r0 != r1) goto L_0x00b8;
    L_0x00b7:
        goto L_0x00c5;
    L_0x00b8:
        r1 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0142 }
        if (r0 != r1) goto L_0x00eb;
    L_0x00bc:
        r1 = r13.crossfadeImage;	 Catch:{ Exception -> 0x0142 }
        if (r1 == 0) goto L_0x00eb;
    L_0x00c0:
        r1 = r13.crossfadeImage;	 Catch:{ Exception -> 0x0142 }
        r2 = r13.crossfadeShader;	 Catch:{ Exception -> 0x0142 }
        goto L_0x00d6;
    L_0x00c5:
        r1 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0142 }
        if (r1 == 0) goto L_0x00eb;
    L_0x00c9:
        r1 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0142 }
        r2 = r13.thumbShader;	 Catch:{ Exception -> 0x0142 }
        goto L_0x00d6;
    L_0x00ce:
        r1 = r13.crossfadeImage;	 Catch:{ Exception -> 0x0142 }
        if (r1 == 0) goto L_0x00d9;
    L_0x00d2:
        r1 = r13.crossfadeImage;	 Catch:{ Exception -> 0x0142 }
        r2 = r13.crossfadeShader;	 Catch:{ Exception -> 0x0142 }
    L_0x00d6:
        r3 = r1;
        r5 = r2;
        goto L_0x00ed;
    L_0x00d9:
        r1 = r13.currentThumbDrawable;	 Catch:{ Exception -> 0x0142 }
        if (r1 == 0) goto L_0x00e2;
    L_0x00dd:
        r1 = r13.currentThumbDrawable;	 Catch:{ Exception -> 0x0142 }
        r2 = r13.thumbShader;	 Catch:{ Exception -> 0x0142 }
        goto L_0x00d6;
    L_0x00e2:
        r1 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0142 }
        if (r1 == 0) goto L_0x00eb;
    L_0x00e6:
        r1 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0142 }
        r2 = r13.thumbShader;	 Catch:{ Exception -> 0x0142 }
        goto L_0x00d6;
    L_0x00eb:
        r3 = r2;
        r5 = r3;
    L_0x00ed:
        if (r3 == 0) goto L_0x00fb;
    L_0x00ef:
        r1 = r13.overrideAlpha;	 Catch:{ Exception -> 0x0142 }
        r1 = r1 * r12;
        r4 = (int) r1;	 Catch:{ Exception -> 0x0142 }
        r6 = r13.thumbOrientation;	 Catch:{ Exception -> 0x0142 }
        r1 = r13;
        r2 = r14;
        r1.drawDrawable(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x0142 }
    L_0x00fb:
        r1 = r13.overrideAlpha;	 Catch:{ Exception -> 0x0142 }
        r2 = r13.currentAlpha;	 Catch:{ Exception -> 0x0142 }
        r1 = r1 * r2;
        r1 = r1 * r12;
        r4 = (int) r1;	 Catch:{ Exception -> 0x0142 }
        r1 = r13;
        r2 = r14;
        r3 = r0;
        r5 = r10;
        r6 = r11;
        r1.drawDrawable(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x0142 }
        goto L_0x011a;
    L_0x010d:
        r1 = r13.overrideAlpha;	 Catch:{ Exception -> 0x0142 }
        r1 = r1 * r12;
        r4 = (int) r1;	 Catch:{ Exception -> 0x0142 }
        r1 = r13;
        r2 = r14;
        r3 = r0;
        r5 = r10;
        r6 = r11;
        r1.drawDrawable(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x0142 }
    L_0x011a:
        if (r9 == 0) goto L_0x0122;
    L_0x011c:
        r0 = r13.crossfadeWithThumb;	 Catch:{ Exception -> 0x0142 }
        if (r0 == 0) goto L_0x0122;
    L_0x0120:
        r0 = 1;
        goto L_0x0123;
    L_0x0122:
        r0 = 0;
    L_0x0123:
        r13.checkAlphaAnimation(r0);	 Catch:{ Exception -> 0x0142 }
        return r8;
    L_0x0127:
        r0 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0142 }
        if (r0 == 0) goto L_0x013e;
    L_0x012b:
        r3 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0142 }
        r0 = r13.overrideAlpha;	 Catch:{ Exception -> 0x0142 }
        r0 = r0 * r12;
        r4 = (int) r0;	 Catch:{ Exception -> 0x0142 }
        r5 = 0;
        r6 = r13.thumbOrientation;	 Catch:{ Exception -> 0x0142 }
        r1 = r13;
        r2 = r14;
        r1.drawDrawable(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x0142 }
        r13.checkAlphaAnimation(r9);	 Catch:{ Exception -> 0x0142 }
        return r8;
    L_0x013e:
        r13.checkAlphaAnimation(r9);	 Catch:{ Exception -> 0x0142 }
        goto L_0x0146;
    L_0x0142:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0146:
        return r7;
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
        drawable = this.currentImageDrawable;
        if (drawable != null) {
            return drawable;
        }
        drawable = this.currentThumbDrawable;
        if (drawable != null) {
            return drawable;
        }
        drawable = this.staticThumbDrawable;
        return drawable != null ? drawable : null;
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
        drawable = this.currentImageDrawable;
        if ((drawable instanceof BitmapDrawable) && !(drawable instanceof AnimatedFileDrawable) && !(this.currentMediaDrawable instanceof RLottieDrawable)) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        drawable = this.currentThumbDrawable;
        if ((drawable instanceof BitmapDrawable) && !(drawable instanceof AnimatedFileDrawable) && !(this.currentMediaDrawable instanceof RLottieDrawable)) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        drawable = this.staticThumbDrawable;
        return drawable instanceof BitmapDrawable ? ((BitmapDrawable) drawable).getBitmap() : null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x007f  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x007f  */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x007f  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x007f  */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A:{SYNTHETIC, RETURN} */
    public org.telegram.messenger.ImageReceiver.BitmapHolder getBitmapSafe() {
        /*
        r4 = this;
        r0 = r4.getAnimation();
        r1 = r4.getLottieAnimation();
        r2 = 0;
        if (r1 == 0) goto L_0x0018;
    L_0x000b:
        r3 = r1.hasBitmap();
        if (r3 == 0) goto L_0x0018;
    L_0x0011:
        r0 = r1.getAnimatedBitmap();
    L_0x0015:
        r1 = r2;
        goto L_0x007d;
    L_0x0018:
        if (r0 == 0) goto L_0x0025;
    L_0x001a:
        r1 = r0.hasBitmap();
        if (r1 == 0) goto L_0x0025;
    L_0x0020:
        r0 = r0.getAnimatedBitmap();
        goto L_0x0015;
    L_0x0025:
        r0 = r4.currentMediaDrawable;
        r1 = r0 instanceof android.graphics.drawable.BitmapDrawable;
        if (r1 == 0) goto L_0x003c;
    L_0x002b:
        r1 = r0 instanceof org.telegram.ui.Components.AnimatedFileDrawable;
        if (r1 != 0) goto L_0x003c;
    L_0x002f:
        r1 = r0 instanceof org.telegram.ui.Components.RLottieDrawable;
        if (r1 != 0) goto L_0x003c;
    L_0x0033:
        r0 = (android.graphics.drawable.BitmapDrawable) r0;
        r0 = r0.getBitmap();
        r1 = r4.currentMediaKey;
        goto L_0x007d;
    L_0x003c:
        r0 = r4.currentImageDrawable;
        r1 = r0 instanceof android.graphics.drawable.BitmapDrawable;
        if (r1 == 0) goto L_0x0055;
    L_0x0042:
        r1 = r0 instanceof org.telegram.ui.Components.AnimatedFileDrawable;
        if (r1 != 0) goto L_0x0055;
    L_0x0046:
        r1 = r4.currentMediaDrawable;
        r1 = r1 instanceof org.telegram.ui.Components.RLottieDrawable;
        if (r1 != 0) goto L_0x0055;
    L_0x004c:
        r0 = (android.graphics.drawable.BitmapDrawable) r0;
        r0 = r0.getBitmap();
        r1 = r4.currentImageKey;
        goto L_0x007d;
    L_0x0055:
        r0 = r4.currentThumbDrawable;
        r1 = r0 instanceof android.graphics.drawable.BitmapDrawable;
        if (r1 == 0) goto L_0x006e;
    L_0x005b:
        r1 = r0 instanceof org.telegram.ui.Components.AnimatedFileDrawable;
        if (r1 != 0) goto L_0x006e;
    L_0x005f:
        r1 = r4.currentMediaDrawable;
        r1 = r1 instanceof org.telegram.ui.Components.RLottieDrawable;
        if (r1 != 0) goto L_0x006e;
    L_0x0065:
        r0 = (android.graphics.drawable.BitmapDrawable) r0;
        r0 = r0.getBitmap();
        r1 = r4.currentThumbKey;
        goto L_0x007d;
    L_0x006e:
        r0 = r4.staticThumbDrawable;
        r1 = r0 instanceof android.graphics.drawable.BitmapDrawable;
        if (r1 == 0) goto L_0x007b;
    L_0x0074:
        r0 = (android.graphics.drawable.BitmapDrawable) r0;
        r0 = r0.getBitmap();
        goto L_0x0015;
    L_0x007b:
        r0 = r2;
        r1 = r0;
    L_0x007d:
        if (r0 == 0) goto L_0x0084;
    L_0x007f:
        r2 = new org.telegram.messenger.ImageReceiver$BitmapHolder;
        r2.<init>(r0, r1);
    L_0x0084:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageReceiver.getBitmapSafe():org.telegram.messenger.ImageReceiver$BitmapHolder");
    }

    public Bitmap getThumbBitmap() {
        Drawable drawable = this.currentThumbDrawable;
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        drawable = this.staticThumbDrawable;
        return drawable instanceof BitmapDrawable ? ((BitmapDrawable) drawable).getBitmap() : null;
    }

    public BitmapHolder getThumbBitmapSafe() {
        Bitmap bitmap;
        String str;
        Drawable drawable = this.currentThumbDrawable;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            str = this.currentThumbKey;
        } else {
            drawable = this.staticThumbDrawable;
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
                str = null;
            } else {
                bitmap = null;
                str = bitmap;
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
        int i;
        int intrinsicWidth;
        if (animation != null) {
            i = this.imageOrientation;
            intrinsicWidth = (i % 360 == 0 || i % 360 == 180) ? animation.getIntrinsicWidth() : animation.getIntrinsicHeight();
            return intrinsicWidth;
        }
        RLottieDrawable lottieAnimation = getLottieAnimation();
        if (lottieAnimation != null) {
            return lottieAnimation.getIntrinsicWidth();
        }
        Bitmap bitmap = getBitmap();
        if (bitmap == null) {
            Drawable drawable = this.staticThumbDrawable;
            return drawable != null ? drawable.getIntrinsicWidth() : 1;
        } else {
            i = this.imageOrientation;
            intrinsicWidth = (i % 360 == 0 || i % 360 == 180) ? bitmap.getWidth() : bitmap.getHeight();
            return intrinsicWidth;
        }
    }

    public int getBitmapHeight() {
        getDrawable();
        AnimatedFileDrawable animation = getAnimation();
        int i;
        int intrinsicHeight;
        if (animation != null) {
            i = this.imageOrientation;
            intrinsicHeight = (i % 360 == 0 || i % 360 == 180) ? animation.getIntrinsicHeight() : animation.getIntrinsicWidth();
            return intrinsicHeight;
        }
        RLottieDrawable lottieAnimation = getLottieAnimation();
        if (lottieAnimation != null) {
            return lottieAnimation.getIntrinsicHeight();
        }
        Bitmap bitmap = getBitmap();
        if (bitmap == null) {
            Drawable drawable = this.staticThumbDrawable;
            return drawable != null ? drawable.getIntrinsicHeight() : 1;
        } else {
            i = this.imageOrientation;
            intrinsicHeight = (i % 360 == 0 || i % 360 == 180) ? bitmap.getHeight() : bitmap.getWidth();
            return intrinsicHeight;
        }
    }

    public void setVisible(boolean z, boolean z2) {
        if (this.isVisible != z) {
            this.isVisible = z;
            if (z2) {
                View view = this.parentView;
                if (view != null) {
                    if (this.invalidateAll) {
                        view.invalidate();
                    } else {
                        int i = this.imageX;
                        int i2 = this.imageY;
                        view.invalidate(i, i2, this.imageW + i, this.imageH + i2);
                    }
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
        float height;
        float width;
        if (this.imageOrientation % 180 != 0) {
            height = this.drawRegion.height();
            width = this.drawRegion.width();
        } else {
            height = this.drawRegion.width();
            width = this.drawRegion.height();
        }
        return height / width;
    }

    public String getExt() {
        return this.currentExt;
    }

    public boolean isInsideImage(float f, float f2) {
        int i = this.imageX;
        if (f >= ((float) i) && f <= ((float) (i + this.imageW))) {
            int i2 = this.imageY;
            if (f2 >= ((float) i2) && f2 <= ((float) (i2 + this.imageH))) {
                return true;
            }
        }
        return false;
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
        this.roundRadius = i;
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

    public void setQualityThumbDocument(Document document) {
        this.qulityThumbDocument = document;
    }

    public Document getQulityThumbDocument() {
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
        drawable = this.currentImageDrawable;
        if (drawable instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) drawable;
        }
        drawable = this.currentThumbDrawable;
        if (drawable instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) drawable;
        }
        drawable = this.staticThumbDrawable;
        return drawable instanceof AnimatedFileDrawable ? (AnimatedFileDrawable) drawable : null;
    }

    public RLottieDrawable getLottieAnimation() {
        Drawable drawable = this.currentMediaDrawable;
        if (drawable instanceof RLottieDrawable) {
            return (RLottieDrawable) drawable;
        }
        drawable = this.currentImageDrawable;
        if (drawable instanceof RLottieDrawable) {
            return (RLottieDrawable) drawable;
        }
        drawable = this.currentThumbDrawable;
        if (drawable instanceof RLottieDrawable) {
            return (RLottieDrawable) drawable;
        }
        drawable = this.staticThumbDrawable;
        return drawable instanceof RLottieDrawable ? (RLottieDrawable) drawable : null;
    }

    /* Access modifiers changed, original: protected */
    public int getTag(int i) {
        if (i == 1) {
            return this.thumbTag;
        }
        if (i == 3) {
            return this.mediaTag;
        }
        return this.imageTag;
    }

    /* Access modifiers changed, original: protected */
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

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Missing block: B:119:0x0160, code skipped:
            if ((r8 instanceof org.telegram.ui.Components.AnimatedFileDrawable) == false) goto L_0x0162;
     */
    public boolean setImageBitmapByKey(android.graphics.drawable.Drawable r6, java.lang.String r7, int r8, boolean r9, int r10) {
        /*
        r5 = this;
        r0 = 0;
        if (r6 == 0) goto L_0x0262;
    L_0x0003:
        if (r7 == 0) goto L_0x0262;
    L_0x0005:
        r1 = r5.currentGuid;
        if (r1 == r10) goto L_0x000b;
    L_0x0009:
        goto L_0x0262;
    L_0x000b:
        r10 = 0;
        r1 = 0;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = 1;
        if (r8 != 0) goto L_0x00b6;
    L_0x0012:
        r8 = r5.currentImageKey;
        r7 = r7.equals(r8);
        if (r7 != 0) goto L_0x001b;
    L_0x001a:
        return r0;
    L_0x001b:
        r7 = r6 instanceof org.telegram.ui.Components.AnimatedFileDrawable;
        if (r7 != 0) goto L_0x0028;
    L_0x001f:
        r8 = org.telegram.messenger.ImageLoader.getInstance();
        r4 = r5.currentImageKey;
        r8.incrementUseCount(r4);
    L_0x0028:
        r5.currentImageDrawable = r6;
        r8 = r6 instanceof org.telegram.messenger.ExtendedBitmapDrawable;
        if (r8 == 0) goto L_0x0037;
    L_0x002e:
        r8 = r6;
        r8 = (org.telegram.messenger.ExtendedBitmapDrawable) r8;
        r8 = r8.getOrientation();
        r5.imageOrientation = r8;
    L_0x0037:
        r8 = r5.roundRadius;
        if (r8 == 0) goto L_0x005e;
    L_0x003b:
        r4 = r6 instanceof android.graphics.drawable.BitmapDrawable;
        if (r4 == 0) goto L_0x005e;
    L_0x003f:
        r1 = r6 instanceof org.telegram.ui.Components.RLottieDrawable;
        if (r1 == 0) goto L_0x0044;
    L_0x0043:
        goto L_0x0060;
    L_0x0044:
        if (r7 == 0) goto L_0x004d;
    L_0x0046:
        r7 = r6;
        r7 = (org.telegram.ui.Components.AnimatedFileDrawable) r7;
        r7.setRoundRadius(r8);
        goto L_0x0060;
    L_0x004d:
        r7 = r6;
        r7 = (android.graphics.drawable.BitmapDrawable) r7;
        r8 = new android.graphics.BitmapShader;
        r7 = r7.getBitmap();
        r1 = android.graphics.Shader.TileMode.CLAMP;
        r8.<init>(r7, r1, r1);
        r5.imageShader = r8;
        goto L_0x0060;
    L_0x005e:
        r5.imageShader = r1;
    L_0x0060:
        if (r9 != 0) goto L_0x0066;
    L_0x0062:
        r7 = r5.forcePreview;
        if (r7 == 0) goto L_0x006a;
    L_0x0066:
        r7 = r5.forceCrossfade;
        if (r7 == 0) goto L_0x00b2;
    L_0x006a:
        r7 = r5.currentMediaDrawable;
        r8 = r7 instanceof org.telegram.ui.Components.AnimatedFileDrawable;
        if (r8 == 0) goto L_0x0079;
    L_0x0070:
        r7 = (org.telegram.ui.Components.AnimatedFileDrawable) r7;
        r7 = r7.hasBitmap();
        if (r7 == 0) goto L_0x0079;
    L_0x0078:
        goto L_0x007f;
    L_0x0079:
        r7 = r5.currentImageDrawable;
        r7 = r7 instanceof org.telegram.ui.Components.RLottieDrawable;
        if (r7 == 0) goto L_0x0081;
    L_0x007f:
        r7 = 0;
        goto L_0x0082;
    L_0x0081:
        r7 = 1;
    L_0x0082:
        if (r7 == 0) goto L_0x01ed;
    L_0x0084:
        r7 = r5.currentThumbDrawable;
        if (r7 != 0) goto L_0x008c;
    L_0x0088:
        r7 = r5.staticThumbDrawable;
        if (r7 == 0) goto L_0x0096;
    L_0x008c:
        r7 = r5.currentAlpha;
        r7 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1));
        if (r7 == 0) goto L_0x0096;
    L_0x0092:
        r7 = r5.forceCrossfade;
        if (r7 == 0) goto L_0x01ed;
    L_0x0096:
        r5.currentAlpha = r10;
        r7 = java.lang.System.currentTimeMillis();
        r5.lastUpdateAlphaTime = r7;
        r7 = r5.crossfadeImage;
        if (r7 != 0) goto L_0x00ad;
    L_0x00a2:
        r7 = r5.currentThumbDrawable;
        if (r7 != 0) goto L_0x00ad;
    L_0x00a6:
        r7 = r5.staticThumbDrawable;
        if (r7 == 0) goto L_0x00ab;
    L_0x00aa:
        goto L_0x00ad;
    L_0x00ab:
        r7 = 0;
        goto L_0x00ae;
    L_0x00ad:
        r7 = 1;
    L_0x00ae:
        r5.crossfadeWithThumb = r7;
        goto L_0x01ed;
    L_0x00b2:
        r5.currentAlpha = r2;
        goto L_0x01ed;
    L_0x00b6:
        r4 = 3;
        if (r8 != r4) goto L_0x013a;
    L_0x00b9:
        r8 = r5.currentMediaKey;
        r7 = r7.equals(r8);
        if (r7 != 0) goto L_0x00c2;
    L_0x00c1:
        return r0;
    L_0x00c2:
        r7 = r6 instanceof org.telegram.ui.Components.AnimatedFileDrawable;
        if (r7 != 0) goto L_0x00cf;
    L_0x00c6:
        r8 = org.telegram.messenger.ImageLoader.getInstance();
        r4 = r5.currentMediaKey;
        r8.incrementUseCount(r4);
    L_0x00cf:
        r5.currentMediaDrawable = r6;
        r8 = r5.roundRadius;
        if (r8 == 0) goto L_0x00f8;
    L_0x00d5:
        r4 = r6 instanceof android.graphics.drawable.BitmapDrawable;
        if (r4 == 0) goto L_0x00f8;
    L_0x00d9:
        r1 = r6 instanceof org.telegram.ui.Components.RLottieDrawable;
        if (r1 == 0) goto L_0x00de;
    L_0x00dd:
        goto L_0x00fa;
    L_0x00de:
        if (r7 == 0) goto L_0x00e7;
    L_0x00e0:
        r7 = r6;
        r7 = (org.telegram.ui.Components.AnimatedFileDrawable) r7;
        r7.setRoundRadius(r8);
        goto L_0x00fa;
    L_0x00e7:
        r7 = r6;
        r7 = (android.graphics.drawable.BitmapDrawable) r7;
        r8 = new android.graphics.BitmapShader;
        r7 = r7.getBitmap();
        r1 = android.graphics.Shader.TileMode.CLAMP;
        r8.<init>(r7, r1, r1);
        r5.mediaShader = r8;
        goto L_0x00fa;
    L_0x00f8:
        r5.mediaShader = r1;
    L_0x00fa:
        r7 = r5.currentImageDrawable;
        if (r7 != 0) goto L_0x01ed;
    L_0x00fe:
        if (r9 != 0) goto L_0x0104;
    L_0x0100:
        r7 = r5.forcePreview;
        if (r7 == 0) goto L_0x0108;
    L_0x0104:
        r7 = r5.forceCrossfade;
        if (r7 == 0) goto L_0x0136;
    L_0x0108:
        r7 = r5.currentThumbDrawable;
        if (r7 != 0) goto L_0x0110;
    L_0x010c:
        r7 = r5.staticThumbDrawable;
        if (r7 == 0) goto L_0x011a;
    L_0x0110:
        r7 = r5.currentAlpha;
        r7 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1));
        if (r7 == 0) goto L_0x011a;
    L_0x0116:
        r7 = r5.forceCrossfade;
        if (r7 == 0) goto L_0x01ed;
    L_0x011a:
        r5.currentAlpha = r10;
        r7 = java.lang.System.currentTimeMillis();
        r5.lastUpdateAlphaTime = r7;
        r7 = r5.crossfadeImage;
        if (r7 != 0) goto L_0x0131;
    L_0x0126:
        r7 = r5.currentThumbDrawable;
        if (r7 != 0) goto L_0x0131;
    L_0x012a:
        r7 = r5.staticThumbDrawable;
        if (r7 == 0) goto L_0x012f;
    L_0x012e:
        goto L_0x0131;
    L_0x012f:
        r7 = 0;
        goto L_0x0132;
    L_0x0131:
        r7 = 1;
    L_0x0132:
        r5.crossfadeWithThumb = r7;
        goto L_0x01ed;
    L_0x0136:
        r5.currentAlpha = r2;
        goto L_0x01ed;
    L_0x013a:
        if (r8 != r3) goto L_0x01ed;
    L_0x013c:
        r8 = r5.currentThumbDrawable;
        if (r8 == 0) goto L_0x0141;
    L_0x0140:
        return r0;
    L_0x0141:
        r8 = r5.forcePreview;
        if (r8 != 0) goto L_0x0163;
    L_0x0145:
        r8 = r5.getAnimation();
        if (r8 == 0) goto L_0x0152;
    L_0x014b:
        r8 = r8.hasBitmap();
        if (r8 == 0) goto L_0x0152;
    L_0x0151:
        return r0;
    L_0x0152:
        r8 = r5.currentImageDrawable;
        if (r8 == 0) goto L_0x015a;
    L_0x0156:
        r8 = r8 instanceof org.telegram.ui.Components.AnimatedFileDrawable;
        if (r8 == 0) goto L_0x0162;
    L_0x015a:
        r8 = r5.currentMediaDrawable;
        if (r8 == 0) goto L_0x0163;
    L_0x015e:
        r8 = r8 instanceof org.telegram.ui.Components.AnimatedFileDrawable;
        if (r8 != 0) goto L_0x0163;
    L_0x0162:
        return r0;
    L_0x0163:
        r8 = r5.currentThumbKey;
        r7 = r7.equals(r8);
        if (r7 != 0) goto L_0x016c;
    L_0x016b:
        return r0;
    L_0x016c:
        r7 = org.telegram.messenger.ImageLoader.getInstance();
        r8 = r5.currentThumbKey;
        r7.incrementUseCount(r8);
        r5.currentThumbDrawable = r6;
        r7 = r6 instanceof org.telegram.messenger.ExtendedBitmapDrawable;
        if (r7 == 0) goto L_0x0184;
    L_0x017b:
        r7 = r6;
        r7 = (org.telegram.messenger.ExtendedBitmapDrawable) r7;
        r7 = r7.getOrientation();
        r5.thumbOrientation = r7;
    L_0x0184:
        r7 = r5.roundRadius;
        if (r7 == 0) goto L_0x01ad;
    L_0x0188:
        r8 = r6 instanceof android.graphics.drawable.BitmapDrawable;
        if (r8 == 0) goto L_0x01ad;
    L_0x018c:
        r8 = r6 instanceof org.telegram.ui.Components.RLottieDrawable;
        if (r8 == 0) goto L_0x0191;
    L_0x0190:
        goto L_0x01af;
    L_0x0191:
        r8 = r6 instanceof org.telegram.ui.Components.AnimatedFileDrawable;
        if (r8 == 0) goto L_0x019c;
    L_0x0195:
        r8 = r6;
        r8 = (org.telegram.ui.Components.AnimatedFileDrawable) r8;
        r8.setRoundRadius(r7);
        goto L_0x01af;
    L_0x019c:
        r7 = r6;
        r7 = (android.graphics.drawable.BitmapDrawable) r7;
        r8 = new android.graphics.BitmapShader;
        r7 = r7.getBitmap();
        r1 = android.graphics.Shader.TileMode.CLAMP;
        r8.<init>(r7, r1, r1);
        r5.thumbShader = r8;
        goto L_0x01af;
    L_0x01ad:
        r5.thumbShader = r1;
    L_0x01af:
        if (r9 != 0) goto L_0x01eb;
    L_0x01b1:
        r7 = r5.crossfadeAlpha;
        r8 = 2;
        if (r7 == r8) goto L_0x01eb;
    L_0x01b6:
        r7 = r5.currentParentObject;
        r8 = r7 instanceof org.telegram.messenger.MessageObject;
        if (r8 == 0) goto L_0x01d1;
    L_0x01bc:
        r7 = (org.telegram.messenger.MessageObject) r7;
        r7 = r7.isRoundVideo();
        if (r7 == 0) goto L_0x01d1;
    L_0x01c4:
        r7 = r5.currentParentObject;
        r7 = (org.telegram.messenger.MessageObject) r7;
        r7 = r7.isSending();
        if (r7 == 0) goto L_0x01d1;
    L_0x01ce:
        r5.currentAlpha = r2;
        goto L_0x01ed;
    L_0x01d1:
        r5.currentAlpha = r10;
        r7 = java.lang.System.currentTimeMillis();
        r5.lastUpdateAlphaTime = r7;
        r7 = r5.staticThumbDrawable;
        if (r7 == 0) goto L_0x01e7;
    L_0x01dd:
        r7 = r5.currentImageKey;
        if (r7 != 0) goto L_0x01e7;
    L_0x01e1:
        r7 = r5.currentMediaKey;
        if (r7 != 0) goto L_0x01e7;
    L_0x01e5:
        r7 = 1;
        goto L_0x01e8;
    L_0x01e7:
        r7 = 0;
    L_0x01e8:
        r5.crossfadeWithThumb = r7;
        goto L_0x01ed;
    L_0x01eb:
        r5.currentAlpha = r2;
    L_0x01ed:
        r7 = r6 instanceof org.telegram.ui.Components.AnimatedFileDrawable;
        if (r7 == 0) goto L_0x020a;
    L_0x01f1:
        r6 = (org.telegram.ui.Components.AnimatedFileDrawable) r6;
        r7 = r5.parentView;
        r6.setParentView(r7);
        r7 = r5.useSharedAnimationQueue;
        r6.setUseSharedQueue(r7);
        r7 = r5.allowStartAnimation;
        if (r7 == 0) goto L_0x0204;
    L_0x0201:
        r6.start();
    L_0x0204:
        r7 = r5.allowDecodeSingleFrame;
        r6.setAllowDecodeSingleFrame(r7);
        goto L_0x0224;
    L_0x020a:
        r7 = r6 instanceof org.telegram.ui.Components.RLottieDrawable;
        if (r7 == 0) goto L_0x0224;
    L_0x020e:
        r6 = (org.telegram.ui.Components.RLottieDrawable) r6;
        r7 = r5.parentView;
        r6.addParentView(r7);
        r7 = r5.currentOpenedLayerFlags;
        if (r7 != 0) goto L_0x021c;
    L_0x0219:
        r6.start();
    L_0x021c:
        r6.setAllowDecodeSingleFrame(r3);
        r7 = r5.autoRepeat;
        r6.setAutoRepeat(r7);
    L_0x0224:
        r6 = r5.parentView;
        if (r6 == 0) goto L_0x023d;
    L_0x0228:
        r7 = r5.invalidateAll;
        if (r7 == 0) goto L_0x0230;
    L_0x022c:
        r6.invalidate();
        goto L_0x023d;
    L_0x0230:
        r7 = r5.imageX;
        r8 = r5.imageY;
        r9 = r5.imageW;
        r9 = r9 + r7;
        r10 = r5.imageH;
        r10 = r10 + r8;
        r6.invalidate(r7, r8, r9, r10);
    L_0x023d:
        r6 = r5.delegate;
        if (r6 == 0) goto L_0x0261;
    L_0x0241:
        r7 = r5.currentImageDrawable;
        if (r7 != 0) goto L_0x0254;
    L_0x0245:
        r7 = r5.currentThumbDrawable;
        if (r7 != 0) goto L_0x0254;
    L_0x0249:
        r7 = r5.staticThumbDrawable;
        if (r7 != 0) goto L_0x0254;
    L_0x024d:
        r7 = r5.currentMediaDrawable;
        if (r7 == 0) goto L_0x0252;
    L_0x0251:
        goto L_0x0254;
    L_0x0252:
        r7 = 0;
        goto L_0x0255;
    L_0x0254:
        r7 = 1;
    L_0x0255:
        r8 = r5.currentImageDrawable;
        if (r8 != 0) goto L_0x025e;
    L_0x0259:
        r8 = r5.currentMediaDrawable;
        if (r8 != 0) goto L_0x025e;
    L_0x025d:
        r0 = 1;
    L_0x025e:
        r6.didSetImage(r5, r7, r0);
    L_0x0261:
        return r3;
    L_0x0262:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageReceiver.setImageBitmapByKey(android.graphics.drawable.Drawable, java.lang.String, int, boolean, int):boolean");
    }

    private void recycleBitmap(String str, int i) {
        String str2;
        Drawable drawable;
        if (i == 3) {
            str2 = this.currentMediaKey;
            drawable = this.currentMediaDrawable;
        } else if (i == 2) {
            str2 = this.crossfadeKey;
            drawable = this.crossfadeImage;
        } else if (i == 1) {
            str2 = this.currentThumbKey;
            drawable = this.currentThumbDrawable;
        } else {
            str2 = this.currentImageKey;
            drawable = this.currentImageDrawable;
        }
        if (str2 != null && str2.startsWith("-")) {
            String replacedKey = ImageLoader.getInstance().getReplacedKey(str2);
            if (replacedKey != null) {
                str2 = replacedKey;
            }
        }
        boolean z = drawable instanceof RLottieDrawable;
        if (z) {
            ((RLottieDrawable) drawable).removeParentView(this.parentView);
        }
        ImageLoader.getInstance().getReplacedKey(str2);
        if (str2 != null && ((str == null || !str.equals(str2)) && drawable != null)) {
            if (z) {
                RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
                boolean decrementUseCount = ImageLoader.getInstance().decrementUseCount(str2);
                if (!ImageLoader.getInstance().isInMemCache(str2, true) && decrementUseCount) {
                    rLottieDrawable.recycle();
                }
            } else if (drawable instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) drawable).recycle();
            } else if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
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
        Integer num;
        RLottieDrawable lottieAnimation;
        if (i == NotificationCenter.didReplacedPhotoInMemCache) {
            SetImageBackup setImageBackup;
            String str = (String) objArr[0];
            String str2 = this.currentMediaKey;
            if (str2 != null && str2.equals(str)) {
                this.currentMediaKey = (String) objArr[1];
                this.currentMediaLocation = (ImageLocation) objArr[2];
                setImageBackup = this.setImageBackup;
                if (setImageBackup != null) {
                    setImageBackup.mediaLocation = (ImageLocation) objArr[2];
                }
            }
            str2 = this.currentImageKey;
            if (str2 != null && str2.equals(str)) {
                this.currentImageKey = (String) objArr[1];
                this.currentImageLocation = (ImageLocation) objArr[2];
                setImageBackup = this.setImageBackup;
                if (setImageBackup != null) {
                    setImageBackup.imageLocation = (ImageLocation) objArr[2];
                }
            }
            str2 = this.currentThumbKey;
            if (str2 != null && str2.equals(str)) {
                this.currentThumbKey = (String) objArr[1];
                this.currentThumbLocation = (ImageLocation) objArr[2];
                SetImageBackup setImageBackup2 = this.setImageBackup;
                if (setImageBackup2 != null) {
                    setImageBackup2.thumbLocation = (ImageLocation) objArr[2];
                }
            }
        } else if (i == NotificationCenter.stopAllHeavyOperations) {
            num = (Integer) objArr[0];
            if (this.currentLayerNum < num.intValue()) {
                this.currentOpenedLayerFlags = num.intValue() | this.currentOpenedLayerFlags;
                if (this.currentOpenedLayerFlags != 0) {
                    lottieAnimation = getLottieAnimation();
                    if (lottieAnimation != null) {
                        lottieAnimation.stop();
                    }
                }
            }
        } else if (i == NotificationCenter.startAllHeavyOperations) {
            num = (Integer) objArr[0];
            if (this.currentLayerNum < num.intValue()) {
                i2 = this.currentOpenedLayerFlags;
                if (i2 != 0) {
                    this.currentOpenedLayerFlags = (num.intValue() ^ -1) & i2;
                    if (this.currentOpenedLayerFlags == 0) {
                        lottieAnimation = getLottieAnimation();
                        if (lottieAnimation != null) {
                            lottieAnimation.start();
                        }
                    }
                }
            }
        }
    }
}
