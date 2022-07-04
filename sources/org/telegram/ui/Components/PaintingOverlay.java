package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.ui.Components.Paint.Views.EditTextOutline;

public class PaintingOverlay extends FrameLayout {
    private Drawable backgroundDrawable;
    private boolean ignoreLayout;
    private HashMap<View, VideoEditedInfo.MediaEntity> mediaEntityViews;
    private Bitmap paintBitmap;

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public PaintingOverlay(Context context) {
        super(context);
    }

    public void setData(String str, ArrayList<VideoEditedInfo.MediaEntity> arrayList, boolean z, boolean z2) {
        setEntities(arrayList, z, z2);
        if (str != null) {
            this.paintBitmap = BitmapFactory.decodeFile(str);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(this.paintBitmap);
            this.backgroundDrawable = bitmapDrawable;
            setBackground(bitmapDrawable);
            return;
        }
        this.paintBitmap = null;
        this.backgroundDrawable = null;
        setBackground((Drawable) null);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        this.ignoreLayout = true;
        setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
        if (this.mediaEntityViews != null) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            int childCount = getChildCount();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                VideoEditedInfo.MediaEntity mediaEntity = this.mediaEntityViews.get(childAt);
                if (mediaEntity != null) {
                    if (childAt instanceof EditTextOutline) {
                        childAt.measure(View.MeasureSpec.makeMeasureSpec(mediaEntity.viewWidth, NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
                        float f = (mediaEntity.textViewWidth * ((float) measuredWidth)) / ((float) mediaEntity.viewWidth);
                        childAt.setScaleX(mediaEntity.scale * f);
                        childAt.setScaleY(mediaEntity.scale * f);
                    } else {
                        childAt.measure(View.MeasureSpec.makeMeasureSpec((int) (((float) measuredWidth) * mediaEntity.width), NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) measuredHeight) * mediaEntity.height), NUM));
                    }
                }
            }
        }
        this.ignoreLayout = false;
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        if (this.mediaEntityViews != null) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            int childCount = getChildCount();
            for (int i7 = 0; i7 < childCount; i7++) {
                View childAt = getChildAt(i7);
                VideoEditedInfo.MediaEntity mediaEntity = this.mediaEntityViews.get(childAt);
                if (mediaEntity != null) {
                    if (childAt instanceof EditTextOutline) {
                        i5 = ((int) (((float) measuredWidth) * mediaEntity.textViewX)) - (childAt.getMeasuredWidth() / 2);
                        i6 = ((int) (((float) measuredHeight) * mediaEntity.textViewY)) - (childAt.getMeasuredHeight() / 2);
                    } else {
                        i5 = (int) (((float) measuredWidth) * mediaEntity.x);
                        i6 = (int) (((float) measuredHeight) * mediaEntity.y);
                    }
                    childAt.layout(i5, i6, childAt.getMeasuredWidth() + i5, childAt.getMeasuredHeight() + i6);
                }
            }
        }
    }

    public void reset() {
        this.paintBitmap = null;
        this.backgroundDrawable = null;
        setBackground((Drawable) null);
        HashMap<View, VideoEditedInfo.MediaEntity> hashMap = this.mediaEntityViews;
        if (hashMap != null) {
            hashMap.clear();
        }
        removeAllViews();
    }

    public void showAll() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).setVisibility(0);
        }
        setBackground(this.backgroundDrawable);
    }

    public void hideEntities() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).setVisibility(4);
        }
    }

    public void hideBitmap() {
        setBackground((Drawable) null);
    }

    /* JADX WARNING: type inference failed for: r6v5, types: [org.telegram.ui.Components.PaintingOverlay$1, android.view.View, android.widget.EditText, org.telegram.ui.Components.Paint.Views.EditTextOutline] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setEntities(java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r18, boolean r19, boolean r20) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r17.reset()
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r0.mediaEntityViews = r2
            if (r1 == 0) goto L_0x0138
            boolean r2 = r18.isEmpty()
            if (r2 != 0) goto L_0x0138
            int r2 = r18.size()
            r3 = 0
            r4 = 0
        L_0x001c:
            if (r4 >= r2) goto L_0x0138
            java.lang.Object r5 = r1.get(r4)
            org.telegram.messenger.VideoEditedInfo$MediaEntity r5 = (org.telegram.messenger.VideoEditedInfo.MediaEntity) r5
            byte r6 = r5.type
            r7 = 0
            r8 = 1
            if (r6 != 0) goto L_0x0079
            org.telegram.ui.Components.BackupImageView r7 = new org.telegram.ui.Components.BackupImageView
            android.content.Context r6 = r17.getContext()
            r7.<init>(r6)
            r7.setAspectFit(r8)
            org.telegram.messenger.ImageReceiver r9 = r7.getImageReceiver()
            if (r19 == 0) goto L_0x0049
            r9.setAllowDecodeSingleFrame(r8)
            r9.setAllowStartLottieAnimation(r3)
            if (r20 == 0) goto L_0x0049
            org.telegram.ui.Components.PaintingOverlay$$ExternalSyntheticLambda0 r6 = org.telegram.ui.Components.PaintingOverlay$$ExternalSyntheticLambda0.INSTANCE
            r9.setDelegate(r6)
        L_0x0049:
            org.telegram.tgnet.TLRPC$Document r6 = r5.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r6.thumbs
            r8 = 90
            org.telegram.tgnet.TLRPC$PhotoSize r6 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r8)
            org.telegram.tgnet.TLRPC$Document r8 = r5.document
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument(r8)
            r11 = 0
            org.telegram.tgnet.TLRPC$Document r8 = r5.document
            org.telegram.messenger.ImageLocation r12 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r6, (org.telegram.tgnet.TLRPC$Document) r8)
            r13 = 0
            java.lang.Object r15 = r5.parentObject
            r16 = 1
            java.lang.String r14 = "webp"
            r9.setImage((org.telegram.messenger.ImageLocation) r10, (java.lang.String) r11, (org.telegram.messenger.ImageLocation) r12, (java.lang.String) r13, (java.lang.String) r14, (java.lang.Object) r15, (int) r16)
            byte r6 = r5.subType
            r6 = r6 & 2
            if (r6 == 0) goto L_0x0075
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            r7.setScaleX(r6)
        L_0x0075:
            r5.view = r7
            goto L_0x0112
        L_0x0079:
            if (r6 != r8) goto L_0x0112
            org.telegram.ui.Components.PaintingOverlay$1 r6 = new org.telegram.ui.Components.PaintingOverlay$1
            android.content.Context r9 = r17.getContext()
            r6.<init>(r0, r9)
            r6.setBackgroundColor(r3)
            r9 = 1088421888(0x40e00000, float:7.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r6.setPadding(r10, r11, r12, r9)
            int r9 = r5.fontSize
            float r9 = (float) r9
            r6.setTextSize(r3, r9)
            java.lang.String r9 = r5.text
            r6.setText(r9)
            r6.setTypeface(r7, r8)
            r7 = 17
            r6.setGravity(r7)
            r6.setHorizontallyScrolling(r3)
            r7 = 268435456(0x10000000, float:2.5243549E-29)
            r6.setImeOptions(r7)
            r6.setFocusableInTouchMode(r8)
            r6.setEnabled(r3)
            int r7 = r6.getInputType()
            r7 = r7 | 16384(0x4000, float:2.2959E-41)
            r6.setInputType(r7)
            int r7 = android.os.Build.VERSION.SDK_INT
            r8 = 23
            if (r7 < r8) goto L_0x00cf
            r6.setBreakStrategy(r3)
        L_0x00cf:
            byte r7 = r5.subType
            r8 = r7 & 1
            r9 = 0
            if (r8 == 0) goto L_0x00e6
            r7 = -1
            r6.setTextColor(r7)
            int r7 = r5.color
            r6.setStrokeColor(r7)
            r6.setFrameColor(r3)
            r6.setShadowLayer(r9, r9, r9, r3)
            goto L_0x010f
        L_0x00e6:
            r7 = r7 & 4
            if (r7 == 0) goto L_0x00fb
            r7 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r6.setTextColor(r7)
            r6.setStrokeColor(r3)
            int r7 = r5.color
            r6.setFrameColor(r7)
            r6.setShadowLayer(r9, r9, r9, r3)
            goto L_0x010f
        L_0x00fb:
            int r7 = r5.color
            r6.setTextColor(r7)
            r6.setStrokeColor(r3)
            r6.setFrameColor(r3)
            r7 = 1084227584(0x40a00000, float:5.0)
            r8 = 1065353216(0x3var_, float:1.0)
            r10 = 1711276032(0x66000000, float:1.5111573E23)
            r6.setShadowLayer(r7, r9, r8, r10)
        L_0x010f:
            r5.view = r6
            r7 = r6
        L_0x0112:
            if (r7 == 0) goto L_0x0134
            r0.addView(r7)
            float r6 = r5.rotation
            float r6 = -r6
            double r8 = (double) r6
            r10 = 4614256656552045848(0x400921fb54442d18, double:3.NUM)
            java.lang.Double.isNaN(r8)
            double r8 = r8 / r10
            r10 = 4640537203540230144(0xNUM, double:180.0)
            double r8 = r8 * r10
            float r6 = (float) r8
            r7.setRotation(r6)
            java.util.HashMap<android.view.View, org.telegram.messenger.VideoEditedInfo$MediaEntity> r6 = r0.mediaEntityViews
            r6.put(r7, r5)
        L_0x0134:
            int r4 = r4 + 1
            goto L_0x001c
        L_0x0138:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PaintingOverlay.setEntities(java.util.ArrayList, boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$setEntities$0(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        RLottieDrawable lottieAnimation;
        if (z && !z2 && (lottieAnimation = imageReceiver.getLottieAnimation()) != null) {
            lottieAnimation.start();
        }
    }

    public void setBitmap(Bitmap bitmap) {
        this.paintBitmap = bitmap;
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        this.backgroundDrawable = bitmapDrawable;
        setBackground(bitmapDrawable);
    }

    public Bitmap getBitmap() {
        return this.paintBitmap;
    }

    public void setAlpha(float f) {
        super.setAlpha(f);
        Drawable drawable = this.backgroundDrawable;
        if (drawable != null) {
            drawable.setAlpha((int) (255.0f * f));
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null && childAt.getParent() == this) {
                childAt.setAlpha(f);
            }
        }
    }

    public Bitmap getThumb() {
        float measuredWidth = (float) getMeasuredWidth();
        float measuredHeight = (float) getMeasuredHeight();
        float max = Math.max(measuredWidth / ((float) AndroidUtilities.dp(120.0f)), measuredHeight / ((float) AndroidUtilities.dp(120.0f)));
        Bitmap createBitmap = Bitmap.createBitmap((int) (measuredWidth / max), (int) (measuredHeight / max), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        float f = 1.0f / max;
        canvas.scale(f, f);
        draw(canvas);
        return createBitmap;
    }
}
