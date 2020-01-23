package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
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
    private static float[] radii = new float[8];
    private static PorterDuffColorFilter selectedColorFilter = new PorterDuffColorFilter(-2236963, Mode.MULTIPLY);
    private static PorterDuffColorFilter selectedGroupColorFilter = new PorterDuffColorFilter(-4473925, Mode.MULTIPLY);
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
    private Document qulityThumbDocument;
    private Paint roundPaint;
    private Path roundPath;
    private int[] roundRadius;
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

        public final /* synthetic */ class -CC {
            public static void $default$onAnimationReady(ImageReceiverDelegate imageReceiverDelegate, ImageReceiver imageReceiver) {
            }
        }

        void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2);

        void onAnimationReady(ImageReceiver imageReceiver);
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

        private boolean isSet() {
            return (this.imageLocation == null && this.thumbLocation == null && this.mediaLocation == null && this.thumb == null) ? false : true;
        }

        private boolean isWebfileSet() {
            ImageLocation imageLocation = this.imageLocation;
            if (imageLocation == null || (imageLocation.webFile == null && imageLocation.path == null)) {
                imageLocation = this.thumbLocation;
                if (imageLocation == null || (imageLocation.webFile == null && imageLocation.path == null)) {
                    imageLocation = this.mediaLocation;
                    if (imageLocation == null || (imageLocation.webFile == null && imageLocation.path == null)) {
                        return false;
                    }
                }
            }
            return true;
        }

        private void clear() {
            this.imageLocation = null;
            this.thumbLocation = null;
            this.mediaLocation = null;
            this.thumb = null;
        }
    }

    private boolean hasRoundRadius() {
        return true;
    }

    public ImageReceiver() {
        this(null);
    }

    public ImageReceiver(View view) {
        this.allowStartAnimation = true;
        this.autoRepeat = 1;
        this.drawRegion = new RectF();
        this.isVisible = true;
        this.roundRadius = new int[4];
        this.roundRect = new RectF();
        this.bitmapRect = new RectF();
        this.shaderMatrix = new Matrix();
        this.roundPath = new Path();
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

    public void setIgnoreImageSet(boolean z) {
        this.ignoreImageSet = z;
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

    /* JADX WARNING: Missing block: B:86:0x0153, code skipped:
            if (r13.equals(r14) != false) goto L_0x015f;
     */
    /* JADX WARNING: Missing block: B:90:0x015d, code skipped:
            if (r13.equals(r15) != false) goto L_0x015f;
     */
    /* JADX WARNING: Missing block: B:91:0x015f, code skipped:
            r13 = r0.delegate;
     */
    /* JADX WARNING: Missing block: B:92:0x0161, code skipped:
            if (r13 == null) goto L_0x0185;
     */
    /* JADX WARNING: Missing block: B:94:0x0165, code skipped:
            if (r0.currentImageDrawable != null) goto L_0x0176;
     */
    /* JADX WARNING: Missing block: B:96:0x0169, code skipped:
            if (r0.currentThumbDrawable != null) goto L_0x0176;
     */
    /* JADX WARNING: Missing block: B:98:0x016d, code skipped:
            if (r0.staticThumbDrawable != null) goto L_0x0176;
     */
    /* JADX WARNING: Missing block: B:100:0x0171, code skipped:
            if (r0.currentMediaDrawable == null) goto L_0x0174;
     */
    /* JADX WARNING: Missing block: B:101:0x0174, code skipped:
            r10 = false;
     */
    /* JADX WARNING: Missing block: B:102:0x0176, code skipped:
            r10 = true;
     */
    /* JADX WARNING: Missing block: B:104:0x0179, code skipped:
            if (r0.currentImageDrawable != null) goto L_0x0181;
     */
    /* JADX WARNING: Missing block: B:106:0x017d, code skipped:
            if (r0.currentMediaDrawable != null) goto L_0x0181;
     */
    /* JADX WARNING: Missing block: B:107:0x017f, code skipped:
            r11 = true;
     */
    /* JADX WARNING: Missing block: B:108:0x0181, code skipped:
            r11 = false;
     */
    /* JADX WARNING: Missing block: B:109:0x0182, code skipped:
            r13.didSetImage(r0, r10, r11);
     */
    /* JADX WARNING: Missing block: B:111:0x0187, code skipped:
            if (r0.canceledLoading != false) goto L_0x018e;
     */
    /* JADX WARNING: Missing block: B:113:0x018b, code skipped:
            if (r0.forcePreview != false) goto L_0x018e;
     */
    /* JADX WARNING: Missing block: B:114:0x018d, code skipped:
            return;
     */
    public void setImage(org.telegram.messenger.ImageLocation r20, java.lang.String r21, org.telegram.messenger.ImageLocation r22, java.lang.String r23, org.telegram.messenger.ImageLocation r24, java.lang.String r25, android.graphics.drawable.Drawable r26, int r27, java.lang.String r28, java.lang.Object r29, int r30) {
        /*
        r19 = this;
        r0 = r19;
        r1 = r20;
        r2 = r21;
        r3 = r22;
        r4 = r23;
        r5 = r24;
        r6 = r25;
        r7 = r26;
        r8 = r28;
        r9 = r29;
        r10 = r0.ignoreImageSet;
        if (r10 == 0) goto L_0x0019;
    L_0x0018:
        return;
    L_0x0019:
        r10 = r0.crossfadeWithOldImage;
        if (r10 == 0) goto L_0x002a;
    L_0x001d:
        r10 = r0.setImageBackup;
        if (r10 == 0) goto L_0x002a;
    L_0x0021:
        r10 = r10.isWebfileSet();
        if (r10 == 0) goto L_0x002a;
    L_0x0027:
        r19.setBackupImage();
    L_0x002a:
        r10 = r0.setImageBackup;
        if (r10 == 0) goto L_0x0031;
    L_0x002e:
        r10.clear();
    L_0x0031:
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r11 = 1;
        r12 = 0;
        r13 = 0;
        if (r3 != 0) goto L_0x00b6;
    L_0x0038:
        if (r5 != 0) goto L_0x00b6;
    L_0x003a:
        if (r1 != 0) goto L_0x00b6;
    L_0x003c:
        r1 = 0;
    L_0x003d:
        r2 = 4;
        if (r1 >= r2) goto L_0x0046;
    L_0x0040:
        r0.recycleBitmap(r13, r1);
        r1 = r1 + 1;
        goto L_0x003d;
    L_0x0046:
        r0.currentImageLocation = r13;
        r0.currentImageFilter = r13;
        r0.currentImageKey = r13;
        r0.currentMediaLocation = r13;
        r0.currentMediaFilter = r13;
        r0.currentMediaKey = r13;
        r0.currentThumbLocation = r13;
        r0.currentThumbFilter = r13;
        r0.currentThumbKey = r13;
        r0.currentMediaDrawable = r13;
        r0.mediaShader = r13;
        r0.currentImageDrawable = r13;
        r0.imageShader = r13;
        r0.thumbShader = r13;
        r0.crossfadeShader = r13;
        r0.currentExt = r8;
        r0.currentParentObject = r13;
        r0.currentCacheType = r12;
        r0.staticThumbDrawable = r7;
        r0.currentAlpha = r10;
        r0.currentSize = r12;
        r1 = org.telegram.messenger.ImageLoader.getInstance();
        r1.cancelLoadingForImageReceiver(r0, r11);
        r1 = r0.parentView;
        if (r1 == 0) goto L_0x0090;
    L_0x007b:
        r2 = r0.invalidateAll;
        if (r2 == 0) goto L_0x0083;
    L_0x007f:
        r1.invalidate();
        goto L_0x0090;
    L_0x0083:
        r2 = r0.imageX;
        r3 = r0.imageY;
        r4 = r0.imageW;
        r4 = r4 + r2;
        r5 = r0.imageH;
        r5 = r5 + r3;
        r1.invalidate(r2, r3, r4, r5);
    L_0x0090:
        r1 = r0.delegate;
        if (r1 == 0) goto L_0x00b5;
    L_0x0094:
        r2 = r0.currentImageDrawable;
        if (r2 != 0) goto L_0x00a7;
    L_0x0098:
        r2 = r0.currentThumbDrawable;
        if (r2 != 0) goto L_0x00a7;
    L_0x009c:
        r2 = r0.staticThumbDrawable;
        if (r2 != 0) goto L_0x00a7;
    L_0x00a0:
        r2 = r0.currentMediaDrawable;
        if (r2 == 0) goto L_0x00a5;
    L_0x00a4:
        goto L_0x00a7;
    L_0x00a5:
        r2 = 0;
        goto L_0x00a8;
    L_0x00a7:
        r2 = 1;
    L_0x00a8:
        r3 = r0.currentImageDrawable;
        if (r3 != 0) goto L_0x00b1;
    L_0x00ac:
        r3 = r0.currentMediaDrawable;
        if (r3 != 0) goto L_0x00b1;
    L_0x00b0:
        goto L_0x00b2;
    L_0x00b1:
        r11 = 0;
    L_0x00b2:
        r1.didSetImage(r0, r2, r11);
    L_0x00b5:
        return;
    L_0x00b6:
        if (r3 == 0) goto L_0x00bd;
    L_0x00b8:
        r14 = r3.getKey(r9, r13, r12);
        goto L_0x00be;
    L_0x00bd:
        r14 = r13;
    L_0x00be:
        if (r14 != 0) goto L_0x00c3;
    L_0x00c0:
        if (r3 == 0) goto L_0x00c3;
    L_0x00c2:
        r3 = r13;
    L_0x00c3:
        r0.currentKeyQuality = r12;
        if (r14 != 0) goto L_0x010c;
    L_0x00c7:
        r15 = r0.needsQualityThumb;
        if (r15 == 0) goto L_0x010c;
    L_0x00cb:
        r15 = r9 instanceof org.telegram.messenger.MessageObject;
        if (r15 != 0) goto L_0x00d3;
    L_0x00cf:
        r15 = r0.qulityThumbDocument;
        if (r15 == 0) goto L_0x010c;
    L_0x00d3:
        r15 = r0.qulityThumbDocument;
        if (r15 == 0) goto L_0x00d8;
    L_0x00d7:
        goto L_0x00df;
    L_0x00d8:
        r15 = r9;
        r15 = (org.telegram.messenger.MessageObject) r15;
        r15 = r15.getDocument();
    L_0x00df:
        if (r15 == 0) goto L_0x010c;
    L_0x00e1:
        r10 = r15.dc_id;
        if (r10 == 0) goto L_0x010c;
    L_0x00e5:
        r12 = r15.id;
        r16 = 0;
        r18 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1));
        if (r18 == 0) goto L_0x010c;
    L_0x00ed:
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r13 = "q_";
        r12.append(r13);
        r13 = r15.dc_id;
        r12.append(r13);
        r13 = "_";
        r12.append(r13);
        r13 = r15.id;
        r12.append(r13);
        r14 = r12.toString();
        r0.currentKeyQuality = r11;
    L_0x010c:
        r12 = "@";
        if (r14 == 0) goto L_0x0124;
    L_0x0110:
        if (r4 == 0) goto L_0x0124;
    L_0x0112:
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r13.append(r14);
        r13.append(r12);
        r13.append(r4);
        r14 = r13.toString();
    L_0x0124:
        if (r1 == 0) goto L_0x012d;
    L_0x0126:
        r10 = 0;
        r13 = 0;
        r15 = r1.getKey(r9, r13, r10);
        goto L_0x012e;
    L_0x012d:
        r15 = 0;
    L_0x012e:
        if (r15 != 0) goto L_0x0133;
    L_0x0130:
        if (r1 == 0) goto L_0x0133;
    L_0x0132:
        r1 = 0;
    L_0x0133:
        if (r15 == 0) goto L_0x0149;
    L_0x0135:
        if (r2 == 0) goto L_0x0149;
    L_0x0137:
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r13.append(r15);
        r13.append(r12);
        r13.append(r2);
        r15 = r13.toString();
    L_0x0149:
        if (r15 != 0) goto L_0x0155;
    L_0x014b:
        r13 = r0.currentImageKey;
        if (r13 == 0) goto L_0x0155;
    L_0x014f:
        r13 = r13.equals(r14);
        if (r13 != 0) goto L_0x015f;
    L_0x0155:
        r13 = r0.currentMediaKey;
        if (r13 == 0) goto L_0x018e;
    L_0x0159:
        r13 = r13.equals(r15);
        if (r13 == 0) goto L_0x018e;
    L_0x015f:
        r13 = r0.delegate;
        if (r13 == 0) goto L_0x0185;
    L_0x0163:
        r10 = r0.currentImageDrawable;
        if (r10 != 0) goto L_0x0176;
    L_0x0167:
        r10 = r0.currentThumbDrawable;
        if (r10 != 0) goto L_0x0176;
    L_0x016b:
        r10 = r0.staticThumbDrawable;
        if (r10 != 0) goto L_0x0176;
    L_0x016f:
        r10 = r0.currentMediaDrawable;
        if (r10 == 0) goto L_0x0174;
    L_0x0173:
        goto L_0x0176;
    L_0x0174:
        r10 = 0;
        goto L_0x0177;
    L_0x0176:
        r10 = 1;
    L_0x0177:
        r11 = r0.currentImageDrawable;
        if (r11 != 0) goto L_0x0181;
    L_0x017b:
        r11 = r0.currentMediaDrawable;
        if (r11 != 0) goto L_0x0181;
    L_0x017f:
        r11 = 1;
        goto L_0x0182;
    L_0x0181:
        r11 = 0;
    L_0x0182:
        r13.didSetImage(r0, r10, r11);
    L_0x0185:
        r10 = r0.canceledLoading;
        if (r10 != 0) goto L_0x018e;
    L_0x0189:
        r10 = r0.forcePreview;
        if (r10 != 0) goto L_0x018e;
    L_0x018d:
        return;
    L_0x018e:
        r10 = r0.strippedLocation;
        if (r10 == 0) goto L_0x0193;
    L_0x0192:
        goto L_0x0198;
    L_0x0193:
        if (r1 == 0) goto L_0x0197;
    L_0x0195:
        r10 = r1;
        goto L_0x0198;
    L_0x0197:
        r10 = r3;
    L_0x0198:
        if (r5 == 0) goto L_0x01a0;
    L_0x019a:
        r11 = 0;
        r13 = r5.getKey(r9, r10, r11);
        goto L_0x01a1;
    L_0x01a0:
        r13 = 0;
    L_0x01a1:
        if (r13 == 0) goto L_0x01b7;
    L_0x01a3:
        if (r6 == 0) goto L_0x01b7;
    L_0x01a5:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r13);
        r10.append(r12);
        r10.append(r6);
        r13 = r10.toString();
    L_0x01b7:
        r10 = r0.crossfadeWithOldImage;
        r11 = 3;
        r12 = 2;
        if (r10 == 0) goto L_0x023a;
    L_0x01bd:
        r10 = r0.currentImageDrawable;
        if (r10 == 0) goto L_0x01e1;
    L_0x01c1:
        r10 = 1;
        r0.recycleBitmap(r13, r10);
        r10 = 0;
        r0.recycleBitmap(r10, r12);
        r0.recycleBitmap(r15, r11);
        r11 = r0.imageShader;
        r0.crossfadeShader = r11;
        r11 = r0.currentImageDrawable;
        r0.crossfadeImage = r11;
        r11 = r0.currentImageKey;
        r0.crossfadeKey = r11;
        r11 = 0;
        r0.crossfadingWithThumb = r11;
        r0.currentImageDrawable = r10;
        r0.currentImageKey = r10;
    L_0x01df:
        r11 = 1;
        goto L_0x024c;
    L_0x01e1:
        r11 = 0;
        r10 = r0.currentThumbDrawable;
        if (r10 == 0) goto L_0x0204;
    L_0x01e6:
        r0.recycleBitmap(r14, r11);
        r10 = 0;
        r0.recycleBitmap(r10, r12);
        r12 = 3;
        r0.recycleBitmap(r15, r12);
        r12 = r0.thumbShader;
        r0.crossfadeShader = r12;
        r12 = r0.currentThumbDrawable;
        r0.crossfadeImage = r12;
        r12 = r0.currentThumbKey;
        r0.crossfadeKey = r12;
        r0.crossfadingWithThumb = r11;
        r0.currentThumbDrawable = r10;
        r0.currentThumbKey = r10;
        goto L_0x01df;
    L_0x0204:
        r10 = r0.staticThumbDrawable;
        if (r10 == 0) goto L_0x0228;
    L_0x0208:
        r0.recycleBitmap(r14, r11);
        r10 = 1;
        r0.recycleBitmap(r13, r10);
        r10 = 0;
        r0.recycleBitmap(r10, r12);
        r12 = 3;
        r0.recycleBitmap(r15, r12);
        r12 = r0.thumbShader;
        r0.crossfadeShader = r12;
        r12 = r0.staticThumbDrawable;
        r0.crossfadeImage = r12;
        r0.crossfadingWithThumb = r11;
        r0.crossfadeKey = r10;
        r0.currentThumbDrawable = r10;
        r0.currentThumbKey = r10;
        goto L_0x01df;
    L_0x0228:
        r10 = 0;
        r0.recycleBitmap(r14, r11);
        r11 = 1;
        r0.recycleBitmap(r13, r11);
        r0.recycleBitmap(r10, r12);
        r12 = 3;
        r0.recycleBitmap(r15, r12);
        r0.crossfadeShader = r10;
        goto L_0x024c;
    L_0x023a:
        r10 = 0;
        r11 = 1;
        r0.recycleBitmap(r14, r10);
        r0.recycleBitmap(r13, r11);
        r10 = 0;
        r0.recycleBitmap(r10, r12);
        r12 = 3;
        r0.recycleBitmap(r15, r12);
        r0.crossfadeShader = r10;
    L_0x024c:
        r0.currentImageLocation = r3;
        r0.currentImageFilter = r4;
        r0.currentImageKey = r14;
        r0.currentMediaLocation = r1;
        r0.currentMediaFilter = r2;
        r0.currentMediaKey = r15;
        r0.currentThumbLocation = r5;
        r0.currentThumbFilter = r6;
        r0.currentThumbKey = r13;
        r0.currentParentObject = r9;
        r0.currentExt = r8;
        r1 = r27;
        r0.currentSize = r1;
        r1 = r30;
        r0.currentCacheType = r1;
        r0.staticThumbDrawable = r7;
        r1 = 0;
        r0.imageShader = r1;
        r0.thumbShader = r1;
        r0.mediaShader = r1;
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0.currentAlpha = r1;
        r1 = r0.delegate;
        if (r1 == 0) goto L_0x029d;
    L_0x027b:
        r2 = r0.currentImageDrawable;
        if (r2 != 0) goto L_0x028e;
    L_0x027f:
        r2 = r0.currentThumbDrawable;
        if (r2 != 0) goto L_0x028e;
    L_0x0283:
        r2 = r0.staticThumbDrawable;
        if (r2 != 0) goto L_0x028e;
    L_0x0287:
        r2 = r0.currentMediaDrawable;
        if (r2 == 0) goto L_0x028c;
    L_0x028b:
        goto L_0x028e;
    L_0x028c:
        r2 = 0;
        goto L_0x028f;
    L_0x028e:
        r2 = 1;
    L_0x028f:
        r3 = r0.currentImageDrawable;
        if (r3 != 0) goto L_0x0299;
    L_0x0293:
        r3 = r0.currentMediaDrawable;
        if (r3 != 0) goto L_0x0299;
    L_0x0297:
        r3 = 1;
        goto L_0x029a;
    L_0x0299:
        r3 = 0;
    L_0x029a:
        r1.didSetImage(r0, r2, r3);
    L_0x029d:
        r1 = org.telegram.messenger.ImageLoader.getInstance();
        r1.loadImageForImageReceiver(r0);
        r1 = r0.parentView;
        if (r1 == 0) goto L_0x02bd;
    L_0x02a8:
        r2 = r0.invalidateAll;
        if (r2 == 0) goto L_0x02b0;
    L_0x02ac:
        r1.invalidate();
        goto L_0x02bd;
    L_0x02b0:
        r2 = r0.imageX;
        r3 = r0.imageY;
        r4 = r0.imageW;
        r4 = r4 + r2;
        r5 = r0.imageH;
        r5 = r5 + r3;
        r1.invalidate(r2, r3, r4, r5);
    L_0x02bd:
        r1 = r9 instanceof org.telegram.messenger.MessageObject;
        if (r1 == 0) goto L_0x02cb;
    L_0x02c1:
        r1 = r9;
        r1 = (org.telegram.messenger.MessageObject) r1;
        r1 = r1.isRoundVideo();
        if (r1 == 0) goto L_0x02cb;
    L_0x02ca:
        goto L_0x02cc;
    L_0x02cb:
        r11 = 0;
    L_0x02cc:
        r0.isRoundVideo = r11;
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
        SetImageBackup setImageBackup = this.setImageBackup;
        if (setImageBackup != null) {
            setImageBackup.clear();
        }
        ImageReceiverDelegate imageReceiverDelegate = this.delegate;
        if (imageReceiverDelegate != null) {
            boolean z2 = (this.currentThumbDrawable == null && this.staticThumbDrawable == null) ? false : true;
            imageReceiverDelegate.didSetImage(this, z2, true);
        }
        View view = this.parentView;
        if (view != null) {
            if (this.invalidateAll) {
                view.invalidate();
            } else {
                i = this.imageX;
                int i2 = this.imageY;
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
        if (!hasRoundRadius() || !(drawable instanceof BitmapDrawable)) {
            setDrawableShader(drawable, null);
        } else if (!(drawable instanceof RLottieDrawable)) {
            if (drawable instanceof AnimatedFileDrawable) {
                ((AnimatedFileDrawable) drawable).setRoundRadius(this.roundRadius);
                return;
            }
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            TileMode tileMode = TileMode.CLAMP;
            setDrawableShader(drawable, new BitmapShader(bitmap, tileMode, tileMode));
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
        if (this.staticThumbDrawable != null) {
            this.staticThumbDrawable = null;
            this.thumbShader = null;
        }
        clearImage();
    }

    private boolean setBackupImage() {
        SetImageBackup setImageBackup = this.setImageBackup;
        if (setImageBackup == null || !setImageBackup.isSet()) {
            return false;
        }
        setImageBackup = this.setImageBackup;
        this.setImageBackup = null;
        setImage(setImageBackup.mediaLocation, setImageBackup.mediaFilter, setImageBackup.imageLocation, setImageBackup.imageFilter, setImageBackup.thumbLocation, setImageBackup.thumbFilter, setImageBackup.thumb, setImageBackup.size, setImageBackup.ext, setImageBackup.parentObject, setImageBackup.cacheType);
        setImageBackup.clear();
        this.setImageBackup = setImageBackup;
        if (this.currentOpenedLayerFlags == 0) {
            RLottieDrawable lottieAnimation = getLottieAnimation();
            if (lottieAnimation != null) {
                lottieAnimation.start();
            }
        }
        return true;
    }

    public boolean onAttachedToWindow() {
        this.currentOpenedLayerFlags = NotificationCenter.getGlobalInstance().getCurrentHeavyOperationFlags();
        this.currentOpenedLayerFlags &= this.currentLayerNum ^ -1;
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopAllHeavyOperations);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.startAllHeavyOperations);
        if (setBackupImage()) {
            return true;
        }
        if (this.currentOpenedLayerFlags == 0) {
            RLottieDrawable lottieAnimation = getLottieAnimation();
            if (lottieAnimation != null) {
                lottieAnimation.start();
            }
        }
        return false;
    }

    private void drawDrawable(Canvas canvas, Drawable drawable, int i, BitmapShader bitmapShader, int i2) {
        Canvas canvas2 = canvas;
        Drawable drawable2 = drawable;
        int i3 = i;
        Shader shader = bitmapShader;
        int i4 = i2;
        int i5;
        Canvas canvas3;
        int i6;
        RectF rectF;
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
            int i11;
            RectF rectF2;
            int i12;
            float f6;
            int i13;
            float f7;
            float width;
            RectF rectF3;
            int i14;
            if (shader == null) {
                i5 = i3;
                canvas3 = canvas2;
                i6 = i5;
                float f8;
                float f9;
                int i15;
                if (this.isAspectFit) {
                    max = Math.max(f4, f5);
                    canvas.save();
                    i4 = (int) (((float) i9) / max);
                    i11 = (int) (((float) intrinsicWidth) / max);
                    rectF2 = this.drawRegion;
                    i12 = this.imageX;
                    f6 = (float) i12;
                    i13 = this.imageW;
                    f6 += ((float) (i13 - i4)) / 2.0f;
                    i9 = this.imageY;
                    f8 = (float) i9;
                    i10 = this.imageH;
                    rectF2.set(f6, f8 + (((float) (i10 - i11)) / 2.0f), ((float) i12) + (((float) (i13 + i4)) / 2.0f), ((float) i9) + (((float) (i10 + i11)) / 2.0f));
                    rectF = this.drawRegion;
                    bitmapDrawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                    if (z) {
                        AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) bitmapDrawable;
                        rectF = this.drawRegion;
                        animatedFileDrawable.setActualDrawRect(rectF.left, rectF.top, rectF.width(), this.drawRegion.height());
                    }
                    if (this.isVisible) {
                        try {
                            bitmapDrawable.setAlpha(i6);
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
                    i11 = this.imageX;
                    i12 = this.imageY;
                    canvas3.clipRect(i11, i12, this.imageW + i11, this.imageH + i12);
                    i11 = i4 % 360;
                    if (i11 != 0) {
                        if (this.centerRotation) {
                            canvas3.rotate((float) i4, (float) (this.imageW / 2), (float) (this.imageH / 2));
                        } else {
                            canvas3.rotate((float) i4, 0.0f, 0.0f);
                        }
                    }
                    f9 = ((float) i9) / f5;
                    i7 = this.imageW;
                    RectF rectF4;
                    if (f9 > ((float) i7)) {
                        i4 = (int) f9;
                        rectF4 = this.drawRegion;
                        i15 = this.imageX;
                        f7 = ((float) i15) - (((float) (i4 - i7)) / 2.0f);
                        i9 = this.imageY;
                        rectF4.set(f7, (float) i9, ((float) i15) + (((float) (i4 + i7)) / 2.0f), (float) (i9 + this.imageH));
                    } else {
                        i4 = (int) (((float) intrinsicWidth) / f4);
                        rectF4 = this.drawRegion;
                        i15 = this.imageX;
                        f7 = (float) i15;
                        i9 = this.imageY;
                        f8 = (float) i9;
                        i10 = this.imageH;
                        rectF4.set(f7, f8 - (((float) (i4 - i10)) / 2.0f), (float) (i15 + i7), ((float) i9) + (((float) (i4 + i10)) / 2.0f));
                    }
                    if (z) {
                        ((AnimatedFileDrawable) bitmapDrawable).setActualDrawRect((float) this.imageX, (float) this.imageY, (float) this.imageW, (float) this.imageH);
                    }
                    if (i11 == 90 || i11 == 270) {
                        width = this.drawRegion.width() / 2.0f;
                        max = this.drawRegion.height() / 2.0f;
                        f9 = this.drawRegion.centerX();
                        f5 = this.drawRegion.centerY();
                        bitmapDrawable.setBounds((int) (f9 - max), (int) (f5 - width), (int) (f9 + max), (int) (f5 + width));
                    } else {
                        rectF3 = this.drawRegion;
                        bitmapDrawable.setBounds((int) rectF3.left, (int) rectF3.top, (int) rectF3.right, (int) rectF3.bottom);
                    }
                    if (this.isVisible) {
                        try {
                            bitmapDrawable.setAlpha(i6);
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
                    i11 = i4 % 360;
                    if (i11 != 0) {
                        if (this.centerRotation) {
                            canvas3.rotate((float) i4, (float) (this.imageW / 2), (float) (this.imageH / 2));
                        } else {
                            canvas3.rotate((float) i4, 0.0f, 0.0f);
                        }
                    }
                    RectF rectF5 = this.drawRegion;
                    i7 = this.imageX;
                    f3 = (float) i7;
                    i15 = this.imageY;
                    rectF5.set(f3, (float) i15, (float) (i7 + this.imageW), (float) (i15 + this.imageH));
                    if (this.isRoundVideo) {
                        rectF5 = this.drawRegion;
                        i7 = AndroidUtilities.roundMessageInset;
                        rectF5.inset((float) (-i7), (float) (-i7));
                    }
                    if (z) {
                        ((AnimatedFileDrawable) bitmapDrawable).setActualDrawRect((float) this.imageX, (float) this.imageY, (float) this.imageW, (float) this.imageH);
                    }
                    if (i11 == 90 || i11 == 270) {
                        width = this.drawRegion.width() / 2.0f;
                        max = this.drawRegion.height() / 2.0f;
                        f9 = this.drawRegion.centerX();
                        f5 = this.drawRegion.centerY();
                        bitmapDrawable.setBounds((int) (f9 - max), (int) (f5 - width), (int) (f9 + max), (int) (f5 + width));
                    } else {
                        rectF3 = this.drawRegion;
                        bitmapDrawable.setBounds((int) rectF3.left, (int) rectF3.top, (int) rectF3.right, (int) rectF3.bottom);
                    }
                    if (this.isVisible) {
                        try {
                            bitmapDrawable.setAlpha(i6);
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
                i14 = (int) (((float) intrinsicWidth) / width);
                rectF2 = this.drawRegion;
                i12 = this.imageX;
                i13 = this.imageW;
                float var_ = (float) (((i13 - i4) / 2) + i12);
                intrinsicWidth = this.imageY;
                i10 = this.imageH;
                rectF2.set(var_, (float) (((i10 - i14) / 2) + intrinsicWidth), (float) (i12 + ((i13 + i4) / 2)), (float) (intrinsicWidth + ((i10 + i14) / 2)));
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
                    i8 = 0;
                    while (true) {
                        int[] iArr = this.roundRadius;
                        if (i8 < iArr.length) {
                            float[] fArr = radii;
                            i4 = i8 * 2;
                            fArr[i4] = (float) iArr[i8];
                            fArr[i4 + 1] = (float) iArr[i8];
                            i8++;
                        } else {
                            this.roundPath.reset();
                            this.roundPath.addRoundRect(this.roundRect, radii, Direction.CW);
                            this.roundPath.close();
                            canvas2.drawPath(this.roundPath, this.roundPaint);
                            return;
                        }
                    }
                }
                return;
            } else {
                RectF rectF7;
                float var_;
                this.roundPaint.setShader(shader);
                f2 = 1.0f / Math.min(f4, f5);
                rectF3 = this.roundRect;
                i14 = this.imageX;
                f6 = (float) i14;
                f7 = this.sideClip;
                f6 += f7;
                i6 = this.imageY;
                rectF3.set(f6, ((float) i6) + f7, ((float) (i14 + this.imageW)) - f7, ((float) (i6 + this.imageH)) - f7);
                this.shaderMatrix.reset();
                if (Math.abs(f4 - f5) > 5.0E-4f) {
                    width = ((float) i9) / f5;
                    if (width > f) {
                        i8 = (int) width;
                        rectF7 = this.drawRegion;
                        i3 = this.imageX;
                        width = (float) i8;
                        max = ((float) i3) - ((width - f) / 2.0f);
                        i14 = this.imageY;
                        rectF7.set(max, (float) i14, ((float) i3) + ((width + f) / 2.0f), ((float) i14) + f3);
                    } else {
                        i8 = (int) (((float) intrinsicWidth) / f4);
                        rectF7 = this.drawRegion;
                        i3 = this.imageX;
                        max = (float) i3;
                        i14 = this.imageY;
                        width = (float) i8;
                        rectF7.set(max, ((float) i14) - ((width - f3) / 2.0f), ((float) i3) + f, ((float) i14) + ((width + f3) / 2.0f));
                    }
                } else {
                    rectF3 = this.drawRegion;
                    i6 = this.imageX;
                    var_ = (float) i6;
                    i11 = this.imageY;
                    rectF3.set(var_, (float) i11, ((float) i6) + f, ((float) i11) + f3);
                }
                if (this.isVisible) {
                    this.shaderMatrix.reset();
                    Matrix matrix2 = this.shaderMatrix;
                    rectF7 = this.drawRegion;
                    var_ = rectF7.left;
                    max = this.sideClip;
                    matrix2.setTranslate(var_ + max, rectF7.top + max);
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
                    if (this.isRoundVideo) {
                        width = (((float) (AndroidUtilities.roundMessageInset * 2)) + f) / f;
                        this.shaderMatrix.postScale(width, width, this.drawRegion.centerX(), this.drawRegion.centerY());
                    }
                    bitmapShader.setLocalMatrix(this.shaderMatrix);
                    this.roundPaint.setAlpha(i);
                    i8 = 0;
                    while (true) {
                        int[] iArr2 = this.roundRadius;
                        if (i8 < iArr2.length) {
                            float[] fArr2 = radii;
                            i11 = i8 * 2;
                            fArr2[i11] = (float) iArr2[i8];
                            fArr2[i11 + 1] = (float) iArr2[i8];
                            i8++;
                        } else {
                            this.roundPath.reset();
                            this.roundPath.addRoundRect(this.roundRect, radii, Direction.CW);
                            this.roundPath.close();
                            canvas.drawPath(this.roundPath, this.roundPaint);
                            return;
                        }
                    }
                }
                return;
            }
        }
        i5 = i3;
        canvas3 = canvas2;
        i6 = i5;
        rectF = this.drawRegion;
        i4 = this.imageX;
        float var_ = (float) i4;
        i7 = this.imageY;
        rectF.set(var_, (float) i7, (float) (i4 + this.imageW), (float) (i7 + this.imageH));
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

    /* JADX WARNING: Removed duplicated region for block: B:90:0x0103 A:{Catch:{ Exception -> 0x0156 }} */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0103 A:{Catch:{ Exception -> 0x0156 }} */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0103 A:{Catch:{ Exception -> 0x0156 }} */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0103 A:{Catch:{ Exception -> 0x0156 }} */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0103 A:{Catch:{ Exception -> 0x0156 }} */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x013b A:{Catch:{ Exception -> 0x0156 }} */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0095 A:{Catch:{ Exception -> 0x0156 }} */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0095 A:{Catch:{ Exception -> 0x0156 }} */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x013b A:{Catch:{ Exception -> 0x0156 }} */
    public boolean draw(android.graphics.Canvas r14) {
        /*
        r13 = this;
        r7 = 0;
        r0 = r13.getAnimation();	 Catch:{ Exception -> 0x0156 }
        r1 = r13.getLottieAnimation();	 Catch:{ Exception -> 0x0156 }
        r8 = 1;
        if (r0 == 0) goto L_0x0012;
    L_0x000c:
        r2 = r0.hasBitmap();	 Catch:{ Exception -> 0x0156 }
        if (r2 == 0) goto L_0x001a;
    L_0x0012:
        if (r1 == 0) goto L_0x001c;
    L_0x0014:
        r2 = r1.hasBitmap();	 Catch:{ Exception -> 0x0156 }
        if (r2 != 0) goto L_0x001c;
    L_0x001a:
        r2 = 1;
        goto L_0x001d;
    L_0x001c:
        r2 = 0;
    L_0x001d:
        if (r1 == 0) goto L_0x0024;
    L_0x001f:
        r3 = r13.parentView;	 Catch:{ Exception -> 0x0156 }
        r1.setCurrentParentView(r3);	 Catch:{ Exception -> 0x0156 }
    L_0x0024:
        if (r0 != 0) goto L_0x0028;
    L_0x0026:
        if (r1 == 0) goto L_0x0039;
    L_0x0028:
        if (r2 != 0) goto L_0x0039;
    L_0x002a:
        r0 = r13.animationReadySent;	 Catch:{ Exception -> 0x0156 }
        if (r0 != 0) goto L_0x0039;
    L_0x002e:
        r13.animationReadySent = r8;	 Catch:{ Exception -> 0x0156 }
        r0 = r13.delegate;	 Catch:{ Exception -> 0x0156 }
        if (r0 == 0) goto L_0x0039;
    L_0x0034:
        r0 = r13.delegate;	 Catch:{ Exception -> 0x0156 }
        r0.onAnimationReady(r13);	 Catch:{ Exception -> 0x0156 }
    L_0x0039:
        r0 = r13.forcePreview;	 Catch:{ Exception -> 0x0156 }
        r1 = 0;
        if (r0 != 0) goto L_0x004e;
    L_0x003e:
        r0 = r13.currentMediaDrawable;	 Catch:{ Exception -> 0x0156 }
        if (r0 == 0) goto L_0x004e;
    L_0x0042:
        if (r2 != 0) goto L_0x004e;
    L_0x0044:
        r0 = r13.currentMediaDrawable;	 Catch:{ Exception -> 0x0156 }
        r3 = r13.mediaShader;	 Catch:{ Exception -> 0x0156 }
        r4 = r13.imageOrientation;	 Catch:{ Exception -> 0x0156 }
    L_0x004a:
        r9 = r2;
        r10 = r3;
        r11 = r4;
        goto L_0x0091;
    L_0x004e:
        r0 = r13.forcePreview;	 Catch:{ Exception -> 0x0156 }
        if (r0 != 0) goto L_0x0066;
    L_0x0052:
        r0 = r13.currentImageDrawable;	 Catch:{ Exception -> 0x0156 }
        if (r0 == 0) goto L_0x0066;
    L_0x0056:
        if (r2 == 0) goto L_0x005c;
    L_0x0058:
        r0 = r13.currentMediaDrawable;	 Catch:{ Exception -> 0x0156 }
        if (r0 == 0) goto L_0x0066;
    L_0x005c:
        r0 = r13.currentImageDrawable;	 Catch:{ Exception -> 0x0156 }
        r2 = r13.imageShader;	 Catch:{ Exception -> 0x0156 }
        r3 = r13.imageOrientation;	 Catch:{ Exception -> 0x0156 }
        r10 = r2;
        r11 = r3;
        r9 = 0;
        goto L_0x0091;
    L_0x0066:
        r0 = r13.crossfadeImage;	 Catch:{ Exception -> 0x0156 }
        if (r0 == 0) goto L_0x0075;
    L_0x006a:
        r0 = r13.crossfadingWithThumb;	 Catch:{ Exception -> 0x0156 }
        if (r0 != 0) goto L_0x0075;
    L_0x006e:
        r0 = r13.crossfadeImage;	 Catch:{ Exception -> 0x0156 }
        r3 = r13.crossfadeShader;	 Catch:{ Exception -> 0x0156 }
        r4 = r13.imageOrientation;	 Catch:{ Exception -> 0x0156 }
        goto L_0x004a;
    L_0x0075:
        r0 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0156 }
        r0 = r0 instanceof android.graphics.drawable.BitmapDrawable;	 Catch:{ Exception -> 0x0156 }
        if (r0 == 0) goto L_0x0082;
    L_0x007b:
        r0 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0156 }
        r3 = r13.thumbShader;	 Catch:{ Exception -> 0x0156 }
        r4 = r13.thumbOrientation;	 Catch:{ Exception -> 0x0156 }
        goto L_0x004a;
    L_0x0082:
        r0 = r13.currentThumbDrawable;	 Catch:{ Exception -> 0x0156 }
        if (r0 == 0) goto L_0x008d;
    L_0x0086:
        r0 = r13.currentThumbDrawable;	 Catch:{ Exception -> 0x0156 }
        r3 = r13.thumbShader;	 Catch:{ Exception -> 0x0156 }
        r4 = r13.thumbOrientation;	 Catch:{ Exception -> 0x0156 }
        goto L_0x004a;
    L_0x008d:
        r0 = r1;
        r10 = r0;
        r9 = r2;
        r11 = 0;
    L_0x0091:
        r12 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        if (r0 == 0) goto L_0x013b;
    L_0x0095:
        r2 = r13.crossfadeAlpha;	 Catch:{ Exception -> 0x0156 }
        if (r2 == 0) goto L_0x0121;
    L_0x0099:
        r2 = r13.crossfadeWithThumb;	 Catch:{ Exception -> 0x0156 }
        if (r2 == 0) goto L_0x00ae;
    L_0x009d:
        if (r9 == 0) goto L_0x00ae;
    L_0x009f:
        r1 = r13.overrideAlpha;	 Catch:{ Exception -> 0x0156 }
        r1 = r1 * r12;
        r4 = (int) r1;	 Catch:{ Exception -> 0x0156 }
        r1 = r13;
        r2 = r14;
        r3 = r0;
        r5 = r10;
        r6 = r11;
        r1.drawDrawable(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x0156 }
        goto L_0x012e;
    L_0x00ae:
        r2 = r13.crossfadeWithThumb;	 Catch:{ Exception -> 0x0156 }
        if (r2 == 0) goto L_0x010f;
    L_0x00b2:
        r2 = r13.currentAlpha;	 Catch:{ Exception -> 0x0156 }
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r2 == 0) goto L_0x010f;
    L_0x00ba:
        r2 = r13.currentImageDrawable;	 Catch:{ Exception -> 0x0156 }
        if (r0 == r2) goto L_0x00e2;
    L_0x00be:
        r2 = r13.currentMediaDrawable;	 Catch:{ Exception -> 0x0156 }
        if (r0 != r2) goto L_0x00c3;
    L_0x00c2:
        goto L_0x00e2;
    L_0x00c3:
        r2 = r13.currentThumbDrawable;	 Catch:{ Exception -> 0x0156 }
        if (r0 == r2) goto L_0x00d9;
    L_0x00c7:
        r2 = r13.crossfadeImage;	 Catch:{ Exception -> 0x0156 }
        if (r0 != r2) goto L_0x00cc;
    L_0x00cb:
        goto L_0x00d9;
    L_0x00cc:
        r2 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0156 }
        if (r0 != r2) goto L_0x00ff;
    L_0x00d0:
        r2 = r13.crossfadeImage;	 Catch:{ Exception -> 0x0156 }
        if (r2 == 0) goto L_0x00ff;
    L_0x00d4:
        r1 = r13.crossfadeImage;	 Catch:{ Exception -> 0x0156 }
        r2 = r13.crossfadeShader;	 Catch:{ Exception -> 0x0156 }
        goto L_0x00ea;
    L_0x00d9:
        r2 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0156 }
        if (r2 == 0) goto L_0x00ff;
    L_0x00dd:
        r1 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0156 }
        r2 = r13.thumbShader;	 Catch:{ Exception -> 0x0156 }
        goto L_0x00ea;
    L_0x00e2:
        r2 = r13.crossfadeImage;	 Catch:{ Exception -> 0x0156 }
        if (r2 == 0) goto L_0x00ed;
    L_0x00e6:
        r1 = r13.crossfadeImage;	 Catch:{ Exception -> 0x0156 }
        r2 = r13.crossfadeShader;	 Catch:{ Exception -> 0x0156 }
    L_0x00ea:
        r3 = r1;
        r5 = r2;
        goto L_0x0101;
    L_0x00ed:
        r2 = r13.currentThumbDrawable;	 Catch:{ Exception -> 0x0156 }
        if (r2 == 0) goto L_0x00f6;
    L_0x00f1:
        r1 = r13.currentThumbDrawable;	 Catch:{ Exception -> 0x0156 }
        r2 = r13.thumbShader;	 Catch:{ Exception -> 0x0156 }
        goto L_0x00ea;
    L_0x00f6:
        r2 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0156 }
        if (r2 == 0) goto L_0x00ff;
    L_0x00fa:
        r1 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0156 }
        r2 = r13.thumbShader;	 Catch:{ Exception -> 0x0156 }
        goto L_0x00ea;
    L_0x00ff:
        r3 = r1;
        r5 = r3;
    L_0x0101:
        if (r3 == 0) goto L_0x010f;
    L_0x0103:
        r1 = r13.overrideAlpha;	 Catch:{ Exception -> 0x0156 }
        r1 = r1 * r12;
        r4 = (int) r1;	 Catch:{ Exception -> 0x0156 }
        r6 = r13.thumbOrientation;	 Catch:{ Exception -> 0x0156 }
        r1 = r13;
        r2 = r14;
        r1.drawDrawable(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x0156 }
    L_0x010f:
        r1 = r13.overrideAlpha;	 Catch:{ Exception -> 0x0156 }
        r2 = r13.currentAlpha;	 Catch:{ Exception -> 0x0156 }
        r1 = r1 * r2;
        r1 = r1 * r12;
        r4 = (int) r1;	 Catch:{ Exception -> 0x0156 }
        r1 = r13;
        r2 = r14;
        r3 = r0;
        r5 = r10;
        r6 = r11;
        r1.drawDrawable(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x0156 }
        goto L_0x012e;
    L_0x0121:
        r1 = r13.overrideAlpha;	 Catch:{ Exception -> 0x0156 }
        r1 = r1 * r12;
        r4 = (int) r1;	 Catch:{ Exception -> 0x0156 }
        r1 = r13;
        r2 = r14;
        r3 = r0;
        r5 = r10;
        r6 = r11;
        r1.drawDrawable(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x0156 }
    L_0x012e:
        if (r9 == 0) goto L_0x0136;
    L_0x0130:
        r0 = r13.crossfadeWithThumb;	 Catch:{ Exception -> 0x0156 }
        if (r0 == 0) goto L_0x0136;
    L_0x0134:
        r0 = 1;
        goto L_0x0137;
    L_0x0136:
        r0 = 0;
    L_0x0137:
        r13.checkAlphaAnimation(r0);	 Catch:{ Exception -> 0x0156 }
        return r8;
    L_0x013b:
        r0 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0156 }
        if (r0 == 0) goto L_0x0152;
    L_0x013f:
        r3 = r13.staticThumbDrawable;	 Catch:{ Exception -> 0x0156 }
        r0 = r13.overrideAlpha;	 Catch:{ Exception -> 0x0156 }
        r0 = r0 * r12;
        r4 = (int) r0;	 Catch:{ Exception -> 0x0156 }
        r5 = 0;
        r6 = r13.thumbOrientation;	 Catch:{ Exception -> 0x0156 }
        r1 = r13;
        r2 = r14;
        r1.drawDrawable(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x0156 }
        r13.checkAlphaAnimation(r9);	 Catch:{ Exception -> 0x0156 }
        return r8;
    L_0x0152:
        r13.checkAlphaAnimation(r9);	 Catch:{ Exception -> 0x0156 }
        goto L_0x015a;
    L_0x0156:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x015a:
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
        setRoundRadius(new int[]{i, i, i, i});
    }

    public void setRoundRadius(int i, int i2, int i3, int i4) {
        setRoundRadius(new int[]{i, i2, i3, i4});
    }

    public void setRoundRadius(int[] iArr) {
        Object obj;
        int i = 0;
        while (true) {
            int[] iArr2 = this.roundRadius;
            if (i >= iArr2.length) {
                obj = null;
                break;
            } else if (iArr2[i] != iArr[i]) {
                obj = 1;
                break;
            } else {
                i++;
            }
        }
        if (obj != null) {
            int[] iArr3 = this.roundRadius;
            System.arraycopy(iArr, 0, iArr3, 0, iArr3.length);
            Drawable drawable = this.currentImageDrawable;
            if (drawable != null && this.imageShader == null) {
                updateDrawableRadius(drawable);
            }
            drawable = this.currentMediaDrawable;
            if (drawable != null && this.mediaShader == null) {
                updateDrawableRadius(drawable);
            }
            if (this.thumbShader == null) {
                drawable = this.currentThumbDrawable;
                if (drawable != null) {
                    updateDrawableRadius(drawable);
                    return;
                }
                drawable = this.staticThumbDrawable;
                if (drawable != null) {
                    updateDrawableRadius(drawable);
                }
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
    /* JADX WARNING: Missing block: B:100:0x0113, code skipped:
            if ((r7 instanceof org.telegram.ui.Components.AnimatedFileDrawable) == false) goto L_0x0115;
     */
    public boolean setImageBitmapByKey(android.graphics.drawable.Drawable r5, java.lang.String r6, int r7, boolean r8, int r9) {
        /*
        r4 = this;
        r0 = 0;
        if (r5 == 0) goto L_0x01f1;
    L_0x0003:
        if (r6 == 0) goto L_0x01f1;
    L_0x0005:
        r1 = r4.currentGuid;
        if (r1 == r9) goto L_0x000b;
    L_0x0009:
        goto L_0x01f1;
    L_0x000b:
        r9 = 0;
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = 1;
        if (r7 != 0) goto L_0x008f;
    L_0x0011:
        r7 = r4.currentImageKey;
        r6 = r6.equals(r7);
        if (r6 != 0) goto L_0x001a;
    L_0x0019:
        return r0;
    L_0x001a:
        r6 = r5 instanceof org.telegram.ui.Components.AnimatedFileDrawable;
        if (r6 != 0) goto L_0x0027;
    L_0x001e:
        r6 = org.telegram.messenger.ImageLoader.getInstance();
        r7 = r4.currentImageKey;
        r6.incrementUseCount(r7);
    L_0x0027:
        r4.currentImageDrawable = r5;
        r6 = r5 instanceof org.telegram.messenger.ExtendedBitmapDrawable;
        if (r6 == 0) goto L_0x0036;
    L_0x002d:
        r6 = r5;
        r6 = (org.telegram.messenger.ExtendedBitmapDrawable) r6;
        r6 = r6.getOrientation();
        r4.imageOrientation = r6;
    L_0x0036:
        r4.updateDrawableRadius(r5);
        if (r8 != 0) goto L_0x003f;
    L_0x003b:
        r6 = r4.forcePreview;
        if (r6 == 0) goto L_0x0043;
    L_0x003f:
        r6 = r4.forceCrossfade;
        if (r6 == 0) goto L_0x008b;
    L_0x0043:
        r6 = r4.currentMediaDrawable;
        r7 = r6 instanceof org.telegram.ui.Components.AnimatedFileDrawable;
        if (r7 == 0) goto L_0x0052;
    L_0x0049:
        r6 = (org.telegram.ui.Components.AnimatedFileDrawable) r6;
        r6 = r6.hasBitmap();
        if (r6 == 0) goto L_0x0052;
    L_0x0051:
        goto L_0x0058;
    L_0x0052:
        r6 = r4.currentImageDrawable;
        r6 = r6 instanceof org.telegram.ui.Components.RLottieDrawable;
        if (r6 == 0) goto L_0x005a;
    L_0x0058:
        r6 = 0;
        goto L_0x005b;
    L_0x005a:
        r6 = 1;
    L_0x005b:
        if (r6 == 0) goto L_0x0178;
    L_0x005d:
        r6 = r4.currentThumbDrawable;
        if (r6 != 0) goto L_0x0065;
    L_0x0061:
        r6 = r4.staticThumbDrawable;
        if (r6 == 0) goto L_0x006f;
    L_0x0065:
        r6 = r4.currentAlpha;
        r6 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1));
        if (r6 == 0) goto L_0x006f;
    L_0x006b:
        r6 = r4.forceCrossfade;
        if (r6 == 0) goto L_0x0178;
    L_0x006f:
        r4.currentAlpha = r9;
        r6 = java.lang.System.currentTimeMillis();
        r4.lastUpdateAlphaTime = r6;
        r6 = r4.crossfadeImage;
        if (r6 != 0) goto L_0x0086;
    L_0x007b:
        r6 = r4.currentThumbDrawable;
        if (r6 != 0) goto L_0x0086;
    L_0x007f:
        r6 = r4.staticThumbDrawable;
        if (r6 == 0) goto L_0x0084;
    L_0x0083:
        goto L_0x0086;
    L_0x0084:
        r6 = 0;
        goto L_0x0087;
    L_0x0086:
        r6 = 1;
    L_0x0087:
        r4.crossfadeWithThumb = r6;
        goto L_0x0178;
    L_0x008b:
        r4.currentAlpha = r1;
        goto L_0x0178;
    L_0x008f:
        r3 = 3;
        if (r7 != r3) goto L_0x00ed;
    L_0x0092:
        r7 = r4.currentMediaKey;
        r6 = r6.equals(r7);
        if (r6 != 0) goto L_0x009b;
    L_0x009a:
        return r0;
    L_0x009b:
        r6 = r5 instanceof org.telegram.ui.Components.AnimatedFileDrawable;
        if (r6 != 0) goto L_0x00a8;
    L_0x009f:
        r6 = org.telegram.messenger.ImageLoader.getInstance();
        r7 = r4.currentMediaKey;
        r6.incrementUseCount(r7);
    L_0x00a8:
        r4.currentMediaDrawable = r5;
        r4.updateDrawableRadius(r5);
        r6 = r4.currentImageDrawable;
        if (r6 != 0) goto L_0x0178;
    L_0x00b1:
        if (r8 != 0) goto L_0x00b7;
    L_0x00b3:
        r6 = r4.forcePreview;
        if (r6 == 0) goto L_0x00bb;
    L_0x00b7:
        r6 = r4.forceCrossfade;
        if (r6 == 0) goto L_0x00e9;
    L_0x00bb:
        r6 = r4.currentThumbDrawable;
        if (r6 != 0) goto L_0x00c3;
    L_0x00bf:
        r6 = r4.staticThumbDrawable;
        if (r6 == 0) goto L_0x00cd;
    L_0x00c3:
        r6 = r4.currentAlpha;
        r6 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1));
        if (r6 == 0) goto L_0x00cd;
    L_0x00c9:
        r6 = r4.forceCrossfade;
        if (r6 == 0) goto L_0x0178;
    L_0x00cd:
        r4.currentAlpha = r9;
        r6 = java.lang.System.currentTimeMillis();
        r4.lastUpdateAlphaTime = r6;
        r6 = r4.crossfadeImage;
        if (r6 != 0) goto L_0x00e4;
    L_0x00d9:
        r6 = r4.currentThumbDrawable;
        if (r6 != 0) goto L_0x00e4;
    L_0x00dd:
        r6 = r4.staticThumbDrawable;
        if (r6 == 0) goto L_0x00e2;
    L_0x00e1:
        goto L_0x00e4;
    L_0x00e2:
        r6 = 0;
        goto L_0x00e5;
    L_0x00e4:
        r6 = 1;
    L_0x00e5:
        r4.crossfadeWithThumb = r6;
        goto L_0x0178;
    L_0x00e9:
        r4.currentAlpha = r1;
        goto L_0x0178;
    L_0x00ed:
        if (r7 != r2) goto L_0x0178;
    L_0x00ef:
        r7 = r4.currentThumbDrawable;
        if (r7 == 0) goto L_0x00f4;
    L_0x00f3:
        return r0;
    L_0x00f4:
        r7 = r4.forcePreview;
        if (r7 != 0) goto L_0x0116;
    L_0x00f8:
        r7 = r4.getAnimation();
        if (r7 == 0) goto L_0x0105;
    L_0x00fe:
        r7 = r7.hasBitmap();
        if (r7 == 0) goto L_0x0105;
    L_0x0104:
        return r0;
    L_0x0105:
        r7 = r4.currentImageDrawable;
        if (r7 == 0) goto L_0x010d;
    L_0x0109:
        r7 = r7 instanceof org.telegram.ui.Components.AnimatedFileDrawable;
        if (r7 == 0) goto L_0x0115;
    L_0x010d:
        r7 = r4.currentMediaDrawable;
        if (r7 == 0) goto L_0x0116;
    L_0x0111:
        r7 = r7 instanceof org.telegram.ui.Components.AnimatedFileDrawable;
        if (r7 != 0) goto L_0x0116;
    L_0x0115:
        return r0;
    L_0x0116:
        r7 = r4.currentThumbKey;
        r6 = r6.equals(r7);
        if (r6 != 0) goto L_0x011f;
    L_0x011e:
        return r0;
    L_0x011f:
        r6 = org.telegram.messenger.ImageLoader.getInstance();
        r7 = r4.currentThumbKey;
        r6.incrementUseCount(r7);
        r4.currentThumbDrawable = r5;
        r6 = r5 instanceof org.telegram.messenger.ExtendedBitmapDrawable;
        if (r6 == 0) goto L_0x0137;
    L_0x012e:
        r6 = r5;
        r6 = (org.telegram.messenger.ExtendedBitmapDrawable) r6;
        r6 = r6.getOrientation();
        r4.thumbOrientation = r6;
    L_0x0137:
        r4.updateDrawableRadius(r5);
        if (r8 != 0) goto L_0x0176;
    L_0x013c:
        r6 = r4.crossfadeAlpha;
        r7 = 2;
        if (r6 == r7) goto L_0x0176;
    L_0x0141:
        r6 = r4.currentParentObject;
        r7 = r6 instanceof org.telegram.messenger.MessageObject;
        if (r7 == 0) goto L_0x015c;
    L_0x0147:
        r6 = (org.telegram.messenger.MessageObject) r6;
        r6 = r6.isRoundVideo();
        if (r6 == 0) goto L_0x015c;
    L_0x014f:
        r6 = r4.currentParentObject;
        r6 = (org.telegram.messenger.MessageObject) r6;
        r6 = r6.isSending();
        if (r6 == 0) goto L_0x015c;
    L_0x0159:
        r4.currentAlpha = r1;
        goto L_0x0178;
    L_0x015c:
        r4.currentAlpha = r9;
        r6 = java.lang.System.currentTimeMillis();
        r4.lastUpdateAlphaTime = r6;
        r6 = r4.staticThumbDrawable;
        if (r6 == 0) goto L_0x0172;
    L_0x0168:
        r6 = r4.currentImageKey;
        if (r6 != 0) goto L_0x0172;
    L_0x016c:
        r6 = r4.currentMediaKey;
        if (r6 != 0) goto L_0x0172;
    L_0x0170:
        r6 = 1;
        goto L_0x0173;
    L_0x0172:
        r6 = 0;
    L_0x0173:
        r4.crossfadeWithThumb = r6;
        goto L_0x0178;
    L_0x0176:
        r4.currentAlpha = r1;
    L_0x0178:
        r6 = r5 instanceof org.telegram.ui.Components.AnimatedFileDrawable;
        if (r6 == 0) goto L_0x0197;
    L_0x017c:
        r5 = (org.telegram.ui.Components.AnimatedFileDrawable) r5;
        r6 = r4.parentView;
        r5.setParentView(r6);
        r6 = r4.useSharedAnimationQueue;
        r5.setUseSharedQueue(r6);
        r6 = r4.allowStartAnimation;
        if (r6 == 0) goto L_0x018f;
    L_0x018c:
        r5.start();
    L_0x018f:
        r6 = r4.allowDecodeSingleFrame;
        r5.setAllowDecodeSingleFrame(r6);
        r4.animationReadySent = r0;
        goto L_0x01b3;
    L_0x0197:
        r6 = r5 instanceof org.telegram.ui.Components.RLottieDrawable;
        if (r6 == 0) goto L_0x01b3;
    L_0x019b:
        r5 = (org.telegram.ui.Components.RLottieDrawable) r5;
        r6 = r4.parentView;
        r5.addParentView(r6);
        r6 = r4.currentOpenedLayerFlags;
        if (r6 != 0) goto L_0x01a9;
    L_0x01a6:
        r5.start();
    L_0x01a9:
        r5.setAllowDecodeSingleFrame(r2);
        r6 = r4.autoRepeat;
        r5.setAutoRepeat(r6);
        r4.animationReadySent = r0;
    L_0x01b3:
        r5 = r4.parentView;
        if (r5 == 0) goto L_0x01cc;
    L_0x01b7:
        r6 = r4.invalidateAll;
        if (r6 == 0) goto L_0x01bf;
    L_0x01bb:
        r5.invalidate();
        goto L_0x01cc;
    L_0x01bf:
        r6 = r4.imageX;
        r7 = r4.imageY;
        r8 = r4.imageW;
        r8 = r8 + r6;
        r9 = r4.imageH;
        r9 = r9 + r7;
        r5.invalidate(r6, r7, r8, r9);
    L_0x01cc:
        r5 = r4.delegate;
        if (r5 == 0) goto L_0x01f0;
    L_0x01d0:
        r6 = r4.currentImageDrawable;
        if (r6 != 0) goto L_0x01e3;
    L_0x01d4:
        r6 = r4.currentThumbDrawable;
        if (r6 != 0) goto L_0x01e3;
    L_0x01d8:
        r6 = r4.staticThumbDrawable;
        if (r6 != 0) goto L_0x01e3;
    L_0x01dc:
        r6 = r4.currentMediaDrawable;
        if (r6 == 0) goto L_0x01e1;
    L_0x01e0:
        goto L_0x01e3;
    L_0x01e1:
        r6 = 0;
        goto L_0x01e4;
    L_0x01e3:
        r6 = 1;
    L_0x01e4:
        r7 = r4.currentImageDrawable;
        if (r7 != 0) goto L_0x01ed;
    L_0x01e8:
        r7 = r4.currentMediaDrawable;
        if (r7 != 0) goto L_0x01ed;
    L_0x01ec:
        r0 = 1;
    L_0x01ed:
        r5.didSetImage(r4, r6, r0);
    L_0x01f0:
        return r2;
    L_0x01f1:
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
