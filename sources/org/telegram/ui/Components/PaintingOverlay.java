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

    public PaintingOverlay(Context context) {
        super(context);
    }

    public void setData(String paintPath, ArrayList<VideoEditedInfo.MediaEntity> entities, boolean isVideo, boolean startAfterSet) {
        setEntities(entities, isVideo, startAfterSet);
        if (paintPath != null) {
            this.paintBitmap = BitmapFactory.decodeFile(paintPath);
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.ignoreLayout = true;
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
        if (this.mediaEntityViews != null) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            int N = getChildCount();
            for (int a = 0; a < N; a++) {
                View child = getChildAt(a);
                VideoEditedInfo.MediaEntity entity = this.mediaEntityViews.get(child);
                if (entity != null) {
                    if (child instanceof EditTextOutline) {
                        child.measure(View.MeasureSpec.makeMeasureSpec(entity.viewWidth, NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
                        float sc = (entity.textViewWidth * ((float) width)) / ((float) entity.viewWidth);
                        child.setScaleX(entity.scale * sc);
                        child.setScaleY(entity.scale * sc);
                    } else {
                        child.measure(View.MeasureSpec.makeMeasureSpec((int) (((float) width) * entity.width), NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) height) * entity.height), NUM));
                    }
                }
            }
        }
        this.ignoreLayout = false;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int y;
        int x;
        if (this.mediaEntityViews != null) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            int N = getChildCount();
            for (int a = 0; a < N; a++) {
                View child = getChildAt(a);
                VideoEditedInfo.MediaEntity entity = this.mediaEntityViews.get(child);
                if (entity != null) {
                    if (child instanceof EditTextOutline) {
                        x = ((int) (((float) width) * entity.textViewX)) - (child.getMeasuredWidth() / 2);
                        y = ((int) (((float) height) * entity.textViewY)) - (child.getMeasuredHeight() / 2);
                    } else {
                        x = (int) (((float) width) * entity.x);
                        y = (int) (((float) height) * entity.y);
                    }
                    child.layout(x, y, child.getMeasuredWidth() + x, child.getMeasuredHeight() + y);
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
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            getChildAt(a).setVisibility(0);
        }
        setBackground(this.backgroundDrawable);
    }

    public void hideEntities() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            getChildAt(a).setVisibility(4);
        }
    }

    public void hideBitmap() {
        setBackground((Drawable) null);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v0, resolved type: org.telegram.ui.Components.PaintingOverlay$1} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: org.telegram.ui.Components.PaintingOverlay$1} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: org.telegram.ui.Components.PaintingOverlay$1} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v10, resolved type: org.telegram.ui.Components.BackupImageView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: org.telegram.ui.Components.PaintingOverlay$1} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setEntities(java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r19, boolean r20, boolean r21) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r18.reset()
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r0.mediaEntityViews = r2
            if (r1 == 0) goto L_0x0143
            boolean r2 = r19.isEmpty()
            if (r2 != 0) goto L_0x0143
            r2 = 0
            int r3 = r19.size()
        L_0x001b:
            if (r2 >= r3) goto L_0x0143
            java.lang.Object r4 = r1.get(r2)
            org.telegram.messenger.VideoEditedInfo$MediaEntity r4 = (org.telegram.messenger.VideoEditedInfo.MediaEntity) r4
            r5 = 0
            byte r6 = r4.type
            r7 = 1
            r8 = 0
            if (r6 != 0) goto L_0x007e
            org.telegram.ui.Components.BackupImageView r6 = new org.telegram.ui.Components.BackupImageView
            android.content.Context r9 = r18.getContext()
            r6.<init>(r9)
            r6.setAspectFit(r7)
            org.telegram.messenger.ImageReceiver r15 = r6.getImageReceiver()
            if (r20 == 0) goto L_0x0049
            r15.setAllowDecodeSingleFrame(r7)
            r15.setAllowStartLottieAnimation(r8)
            if (r21 == 0) goto L_0x0049
            org.telegram.ui.Components.PaintingOverlay$$ExternalSyntheticLambda0 r7 = org.telegram.ui.Components.PaintingOverlay$$ExternalSyntheticLambda0.INSTANCE
            r15.setDelegate(r7)
        L_0x0049:
            org.telegram.tgnet.TLRPC$Document r7 = r4.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r7.thumbs
            r8 = 90
            org.telegram.tgnet.TLRPC$PhotoSize r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8)
            org.telegram.tgnet.TLRPC$Document r8 = r4.document
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument(r8)
            r11 = 0
            org.telegram.tgnet.TLRPC$Document r8 = r4.document
            org.telegram.messenger.ImageLocation r12 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC.PhotoSize) r7, (org.telegram.tgnet.TLRPC.Document) r8)
            r13 = 0
            java.lang.Object r8 = r4.parentObject
            r16 = 1
            java.lang.String r14 = "webp"
            r9 = r15
            r17 = r15
            r15 = r8
            r9.setImage((org.telegram.messenger.ImageLocation) r10, (java.lang.String) r11, (org.telegram.messenger.ImageLocation) r12, (java.lang.String) r13, (java.lang.String) r14, (java.lang.Object) r15, (int) r16)
            byte r8 = r4.subType
            r8 = r8 & 2
            if (r8 == 0) goto L_0x0079
            r8 = -1082130432(0xffffffffbvar_, float:-1.0)
            r6.setScaleX(r8)
        L_0x0079:
            r5 = r6
            r4.view = r6
            goto L_0x011c
        L_0x007e:
            byte r6 = r4.type
            if (r6 != r7) goto L_0x011c
            org.telegram.ui.Components.PaintingOverlay$1 r6 = new org.telegram.ui.Components.PaintingOverlay$1
            android.content.Context r9 = r18.getContext()
            r6.<init>(r9)
            r6.setBackgroundColor(r8)
            r9 = 1088421888(0x40e00000, float:7.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r6.setPadding(r10, r11, r12, r9)
            int r9 = r4.fontSize
            float r9 = (float) r9
            r6.setTextSize(r8, r9)
            java.lang.String r9 = r4.text
            r6.setText(r9)
            r9 = 0
            r6.setTypeface(r9, r7)
            r9 = 17
            r6.setGravity(r9)
            r6.setHorizontallyScrolling(r8)
            r9 = 268435456(0x10000000, float:2.5243549E-29)
            r6.setImeOptions(r9)
            r6.setFocusableInTouchMode(r7)
            r6.setEnabled(r8)
            int r9 = r6.getInputType()
            r9 = r9 | 16384(0x4000, float:2.2959E-41)
            r6.setInputType(r9)
            int r9 = android.os.Build.VERSION.SDK_INT
            r10 = 23
            if (r9 < r10) goto L_0x00d7
            r6.setBreakStrategy(r8)
        L_0x00d7:
            byte r9 = r4.subType
            r7 = r7 & r9
            r9 = 0
            if (r7 == 0) goto L_0x00ed
            r7 = -1
            r6.setTextColor(r7)
            int r7 = r4.color
            r6.setStrokeColor(r7)
            r6.setFrameColor(r8)
            r6.setShadowLayer(r9, r9, r9, r8)
            goto L_0x0118
        L_0x00ed:
            byte r7 = r4.subType
            r7 = r7 & 4
            if (r7 == 0) goto L_0x0104
            r7 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r6.setTextColor(r7)
            r6.setStrokeColor(r8)
            int r7 = r4.color
            r6.setFrameColor(r7)
            r6.setShadowLayer(r9, r9, r9, r8)
            goto L_0x0118
        L_0x0104:
            int r7 = r4.color
            r6.setTextColor(r7)
            r6.setStrokeColor(r8)
            r6.setFrameColor(r8)
            r7 = 1084227584(0x40a00000, float:5.0)
            r8 = 1065353216(0x3var_, float:1.0)
            r10 = 1711276032(0x66000000, float:1.5111573E23)
            r6.setShadowLayer(r7, r9, r8, r10)
        L_0x0118:
            r5 = r6
            r4.view = r6
            goto L_0x011d
        L_0x011c:
        L_0x011d:
            if (r5 == 0) goto L_0x013f
            r0.addView(r5)
            float r6 = r4.rotation
            float r6 = -r6
            double r6 = (double) r6
            r8 = 4614256656552045848(0x400921fb54442d18, double:3.NUM)
            java.lang.Double.isNaN(r6)
            double r6 = r6 / r8
            r8 = 4640537203540230144(0xNUM, double:180.0)
            double r6 = r6 * r8
            float r6 = (float) r6
            r5.setRotation(r6)
            java.util.HashMap<android.view.View, org.telegram.messenger.VideoEditedInfo$MediaEntity> r6 = r0.mediaEntityViews
            r6.put(r5, r4)
        L_0x013f:
            int r2 = r2 + 1
            goto L_0x001b
        L_0x0143:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PaintingOverlay.setEntities(java.util.ArrayList, boolean, boolean):void");
    }

    static /* synthetic */ void lambda$setEntities$0(ImageReceiver imageReceiver1, boolean set, boolean thumb, boolean memCache) {
        RLottieDrawable drawable;
        if (set && !thumb && (drawable = imageReceiver1.getLottieAnimation()) != null) {
            drawable.start();
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

    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        Drawable drawable = this.backgroundDrawable;
        if (drawable != null) {
            drawable.setAlpha((int) (255.0f * alpha));
        }
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child != null && child.getParent() == this) {
                child.setAlpha(alpha);
            }
        }
    }

    public Bitmap getThumb() {
        float w = (float) getMeasuredWidth();
        float h = (float) getMeasuredHeight();
        float scale = Math.max(w / ((float) AndroidUtilities.dp(120.0f)), h / ((float) AndroidUtilities.dp(120.0f)));
        Bitmap bitmap = Bitmap.createBitmap((int) (w / scale), (int) (h / scale), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1.0f / scale, 1.0f / scale);
        draw(canvas);
        return bitmap;
    }
}
