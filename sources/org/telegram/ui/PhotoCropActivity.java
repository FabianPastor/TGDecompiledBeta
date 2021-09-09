package org.telegram.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;

public class PhotoCropActivity extends BaseFragment {
    private String bitmapKey;
    /* access modifiers changed from: private */
    public PhotoEditActivityDelegate delegate = null;
    /* access modifiers changed from: private */
    public boolean doneButtonPressed = false;
    /* access modifiers changed from: private */
    public BitmapDrawable drawable;
    /* access modifiers changed from: private */
    public Bitmap imageToCrop;
    /* access modifiers changed from: private */
    public boolean sameBitmap = false;
    /* access modifiers changed from: private */
    public PhotoCropView view;

    public interface PhotoEditActivityDelegate {
        void didFinishEdit(Bitmap bitmap);
    }

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return false;
    }

    private class PhotoCropView extends FrameLayout {
        int bitmapHeight;
        int bitmapWidth;
        int bitmapX;
        int bitmapY;
        Paint circlePaint = null;
        int draggingState = 0;
        boolean freeform;
        Paint halfPaint = null;
        float oldX = 0.0f;
        float oldY = 0.0f;
        Paint rectPaint = null;
        float rectSizeX = 600.0f;
        float rectSizeY = 600.0f;
        float rectX = -1.0f;
        float rectY = -1.0f;
        int viewHeight;
        int viewWidth;

        public PhotoCropView(Context context) {
            super(context);
            init();
        }

        private void init() {
            Paint paint = new Paint();
            this.rectPaint = paint;
            paint.setColor(NUM);
            this.rectPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            this.rectPaint.setStyle(Paint.Style.STROKE);
            Paint paint2 = new Paint();
            this.circlePaint = paint2;
            paint2.setColor(-1);
            Paint paint3 = new Paint();
            this.halfPaint = paint3;
            paint3.setColor(-NUM);
            setBackgroundColor(-13421773);
            setOnTouchListener(new PhotoCropActivity$PhotoCropView$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x00bb  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ boolean lambda$init$0(android.view.View r13, android.view.MotionEvent r14) {
            /*
                r12 = this;
                float r13 = r14.getX()
                float r0 = r14.getY()
                r1 = 1096810496(0x41600000, float:14.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r2 = r14.getAction()
                r3 = 4
                r4 = 3
                r5 = 5
                r6 = 0
                r7 = 2
                r8 = 1
                if (r2 != 0) goto L_0x00c4
                float r14 = r12.rectX
                float r1 = (float) r1
                float r2 = r14 - r1
                int r2 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
                if (r2 >= 0) goto L_0x003a
                float r2 = r14 + r1
                int r2 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
                if (r2 <= 0) goto L_0x003a
                float r2 = r12.rectY
                float r9 = r2 - r1
                int r9 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
                if (r9 >= 0) goto L_0x003a
                float r2 = r2 + r1
                int r2 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                if (r2 <= 0) goto L_0x003a
                r12.draggingState = r8
                goto L_0x00b7
            L_0x003a:
                float r2 = r14 - r1
                float r9 = r12.rectSizeX
                float r2 = r2 + r9
                int r2 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
                if (r2 >= 0) goto L_0x005a
                float r2 = r14 + r1
                float r2 = r2 + r9
                int r2 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
                if (r2 <= 0) goto L_0x005a
                float r2 = r12.rectY
                float r10 = r2 - r1
                int r10 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
                if (r10 >= 0) goto L_0x005a
                float r2 = r2 + r1
                int r2 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                if (r2 <= 0) goto L_0x005a
                r12.draggingState = r7
                goto L_0x00b7
            L_0x005a:
                float r2 = r14 - r1
                int r2 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
                if (r2 >= 0) goto L_0x007a
                float r2 = r14 + r1
                int r2 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
                if (r2 <= 0) goto L_0x007a
                float r2 = r12.rectY
                float r7 = r2 - r1
                float r10 = r12.rectSizeY
                float r7 = r7 + r10
                int r7 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
                if (r7 >= 0) goto L_0x007a
                float r2 = r2 + r1
                float r2 = r2 + r10
                int r2 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                if (r2 <= 0) goto L_0x007a
                r12.draggingState = r4
                goto L_0x00b7
            L_0x007a:
                float r2 = r14 - r1
                float r2 = r2 + r9
                int r2 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
                if (r2 >= 0) goto L_0x009c
                float r2 = r14 + r1
                float r2 = r2 + r9
                int r2 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
                if (r2 <= 0) goto L_0x009c
                float r2 = r12.rectY
                float r4 = r2 - r1
                float r7 = r12.rectSizeY
                float r4 = r4 + r7
                int r4 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
                if (r4 >= 0) goto L_0x009c
                float r2 = r2 + r1
                float r2 = r2 + r7
                int r1 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                if (r1 <= 0) goto L_0x009c
                r12.draggingState = r3
                goto L_0x00b7
            L_0x009c:
                int r1 = (r14 > r13 ? 1 : (r14 == r13 ? 0 : -1))
                if (r1 >= 0) goto L_0x00b5
                float r14 = r14 + r9
                int r14 = (r14 > r13 ? 1 : (r14 == r13 ? 0 : -1))
                if (r14 <= 0) goto L_0x00b5
                float r14 = r12.rectY
                int r1 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1))
                if (r1 >= 0) goto L_0x00b5
                float r1 = r12.rectSizeY
                float r14 = r14 + r1
                int r14 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1))
                if (r14 <= 0) goto L_0x00b5
                r12.draggingState = r5
                goto L_0x00b7
            L_0x00b5:
                r12.draggingState = r6
            L_0x00b7:
                int r14 = r12.draggingState
                if (r14 == 0) goto L_0x00be
                r12.requestDisallowInterceptTouchEvent(r8)
            L_0x00be:
                r12.oldX = r13
                r12.oldY = r0
                goto L_0x02bc
            L_0x00c4:
                int r1 = r14.getAction()
                if (r1 != r8) goto L_0x00ce
                r12.draggingState = r6
                goto L_0x02bc
            L_0x00ce:
                int r14 = r14.getAction()
                if (r14 != r7) goto L_0x02bc
                int r14 = r12.draggingState
                if (r14 == 0) goto L_0x02bc
                float r1 = r12.oldX
                float r1 = r13 - r1
                float r2 = r12.oldY
                float r2 = r0 - r2
                if (r14 != r5) goto L_0x0127
                float r14 = r12.rectX
                float r14 = r14 + r1
                r12.rectX = r14
                float r1 = r12.rectY
                float r1 = r1 + r2
                r12.rectY = r1
                int r2 = r12.bitmapX
                float r3 = (float) r2
                int r3 = (r14 > r3 ? 1 : (r14 == r3 ? 0 : -1))
                if (r3 >= 0) goto L_0x00f7
                float r14 = (float) r2
                r12.rectX = r14
                goto L_0x0108
            L_0x00f7:
                float r3 = r12.rectSizeX
                float r14 = r14 + r3
                int r4 = r12.bitmapWidth
                int r5 = r2 + r4
                float r5 = (float) r5
                int r14 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1))
                if (r14 <= 0) goto L_0x0108
                int r2 = r2 + r4
                float r14 = (float) r2
                float r14 = r14 - r3
                r12.rectX = r14
            L_0x0108:
                int r14 = r12.bitmapY
                float r2 = (float) r14
                int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r2 >= 0) goto L_0x0114
                float r14 = (float) r14
                r12.rectY = r14
                goto L_0x02b5
            L_0x0114:
                float r2 = r12.rectSizeY
                float r1 = r1 + r2
                int r3 = r12.bitmapHeight
                int r4 = r14 + r3
                float r4 = (float) r4
                int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r1 <= 0) goto L_0x02b5
                int r14 = r14 + r3
                float r14 = (float) r14
                float r14 = r14 - r2
                r12.rectY = r14
                goto L_0x02b5
            L_0x0127:
                r5 = 1126170624(0x43200000, float:160.0)
                if (r14 != r8) goto L_0x0188
                float r14 = r12.rectSizeX
                float r3 = r14 - r1
                int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r3 >= 0) goto L_0x0135
                float r1 = r14 - r5
            L_0x0135:
                float r3 = r12.rectX
                float r4 = r3 + r1
                int r6 = r12.bitmapX
                float r7 = (float) r6
                int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
                if (r4 >= 0) goto L_0x0142
                float r1 = (float) r6
                float r1 = r1 - r3
            L_0x0142:
                boolean r4 = r12.freeform
                if (r4 != 0) goto L_0x0163
                float r2 = r12.rectY
                float r4 = r2 + r1
                int r5 = r12.bitmapY
                float r6 = (float) r5
                int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r4 >= 0) goto L_0x0153
                float r1 = (float) r5
                float r1 = r1 - r2
            L_0x0153:
                float r3 = r3 + r1
                r12.rectX = r3
                float r2 = r2 + r1
                r12.rectY = r2
                float r14 = r14 - r1
                r12.rectSizeX = r14
                float r14 = r12.rectSizeY
                float r14 = r14 - r1
                r12.rectSizeY = r14
                goto L_0x02b5
            L_0x0163:
                float r4 = r12.rectSizeY
                float r6 = r4 - r2
                int r6 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
                if (r6 >= 0) goto L_0x016d
                float r2 = r4 - r5
            L_0x016d:
                float r5 = r12.rectY
                float r6 = r5 + r2
                int r7 = r12.bitmapY
                float r9 = (float) r7
                int r6 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
                if (r6 >= 0) goto L_0x017a
                float r2 = (float) r7
                float r2 = r2 - r5
            L_0x017a:
                float r3 = r3 + r1
                r12.rectX = r3
                float r5 = r5 + r2
                r12.rectY = r5
                float r14 = r14 - r1
                r12.rectSizeX = r14
                float r4 = r4 - r2
                r12.rectSizeY = r4
                goto L_0x02b5
            L_0x0188:
                if (r14 != r7) goto L_0x01ea
                float r14 = r12.rectSizeX
                float r3 = r14 + r1
                int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r3 >= 0) goto L_0x0195
                float r1 = r14 - r5
                float r1 = -r1
            L_0x0195:
                float r3 = r12.rectX
                float r4 = r3 + r14
                float r4 = r4 + r1
                int r6 = r12.bitmapX
                int r7 = r12.bitmapWidth
                int r9 = r6 + r7
                float r9 = (float) r9
                int r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
                if (r4 <= 0) goto L_0x01a9
                int r6 = r6 + r7
                float r1 = (float) r6
                float r1 = r1 - r3
                float r1 = r1 - r14
            L_0x01a9:
                boolean r3 = r12.freeform
                if (r3 != 0) goto L_0x01c8
                float r2 = r12.rectY
                float r3 = r2 - r1
                int r4 = r12.bitmapY
                float r5 = (float) r4
                int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r3 >= 0) goto L_0x01bb
                float r1 = (float) r4
                float r1 = r2 - r1
            L_0x01bb:
                float r2 = r2 - r1
                r12.rectY = r2
                float r14 = r14 + r1
                r12.rectSizeX = r14
                float r14 = r12.rectSizeY
                float r14 = r14 + r1
                r12.rectSizeY = r14
                goto L_0x02b5
            L_0x01c8:
                float r3 = r12.rectSizeY
                float r4 = r3 - r2
                int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
                if (r4 >= 0) goto L_0x01d2
                float r2 = r3 - r5
            L_0x01d2:
                float r4 = r12.rectY
                float r5 = r4 + r2
                int r6 = r12.bitmapY
                float r7 = (float) r6
                int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r5 >= 0) goto L_0x01df
                float r2 = (float) r6
                float r2 = r2 - r4
            L_0x01df:
                float r4 = r4 + r2
                r12.rectY = r4
                float r14 = r14 + r1
                r12.rectSizeX = r14
                float r3 = r3 - r2
                r12.rectSizeY = r3
                goto L_0x02b5
            L_0x01ea:
                if (r14 != r4) goto L_0x0250
                float r14 = r12.rectSizeX
                float r3 = r14 - r1
                int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r3 >= 0) goto L_0x01f6
                float r1 = r14 - r5
            L_0x01f6:
                float r3 = r12.rectX
                float r4 = r3 + r1
                int r6 = r12.bitmapX
                float r7 = (float) r6
                int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
                if (r4 >= 0) goto L_0x0203
                float r1 = (float) r6
                float r1 = r1 - r3
            L_0x0203:
                boolean r4 = r12.freeform
                if (r4 != 0) goto L_0x022a
                float r2 = r12.rectY
                float r4 = r2 + r14
                float r4 = r4 - r1
                int r5 = r12.bitmapY
                int r6 = r12.bitmapHeight
                int r7 = r5 + r6
                float r7 = (float) r7
                int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
                if (r4 <= 0) goto L_0x021d
                float r2 = r2 + r14
                float r1 = (float) r5
                float r2 = r2 - r1
                float r1 = (float) r6
                float r1 = r2 - r1
            L_0x021d:
                float r3 = r3 + r1
                r12.rectX = r3
                float r14 = r14 - r1
                r12.rectSizeX = r14
                float r14 = r12.rectSizeY
                float r14 = r14 - r1
                r12.rectSizeY = r14
                goto L_0x02b5
            L_0x022a:
                float r4 = r12.rectY
                float r6 = r12.rectSizeY
                float r7 = r4 + r6
                float r7 = r7 + r2
                int r9 = r12.bitmapY
                int r10 = r12.bitmapHeight
                int r11 = r9 + r10
                float r11 = (float) r11
                int r7 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
                if (r7 <= 0) goto L_0x0240
                int r9 = r9 + r10
                float r2 = (float) r9
                float r2 = r2 - r4
                float r2 = r2 - r6
            L_0x0240:
                float r3 = r3 + r1
                r12.rectX = r3
                float r14 = r14 - r1
                r12.rectSizeX = r14
                float r6 = r6 + r2
                r12.rectSizeY = r6
                int r14 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
                if (r14 >= 0) goto L_0x02b5
                r12.rectSizeY = r5
                goto L_0x02b5
            L_0x0250:
                if (r14 != r3) goto L_0x02b5
                float r14 = r12.rectX
                float r3 = r12.rectSizeX
                float r4 = r14 + r3
                float r4 = r4 + r1
                int r6 = r12.bitmapX
                int r7 = r12.bitmapWidth
                int r9 = r6 + r7
                float r9 = (float) r9
                int r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
                if (r4 <= 0) goto L_0x0268
                int r6 = r6 + r7
                float r1 = (float) r6
                float r1 = r1 - r14
                float r1 = r1 - r3
            L_0x0268:
                boolean r14 = r12.freeform
                if (r14 != 0) goto L_0x0289
                float r14 = r12.rectY
                float r2 = r14 + r3
                float r2 = r2 + r1
                int r4 = r12.bitmapY
                int r6 = r12.bitmapHeight
                int r7 = r4 + r6
                float r7 = (float) r7
                int r2 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
                if (r2 <= 0) goto L_0x0280
                int r4 = r4 + r6
                float r1 = (float) r4
                float r1 = r1 - r14
                float r1 = r1 - r3
            L_0x0280:
                float r3 = r3 + r1
                r12.rectSizeX = r3
                float r14 = r12.rectSizeY
                float r14 = r14 + r1
                r12.rectSizeY = r14
                goto L_0x02a5
            L_0x0289:
                float r14 = r12.rectY
                float r4 = r12.rectSizeY
                float r6 = r14 + r4
                float r6 = r6 + r2
                int r7 = r12.bitmapY
                int r9 = r12.bitmapHeight
                int r10 = r7 + r9
                float r10 = (float) r10
                int r6 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
                if (r6 <= 0) goto L_0x029f
                int r7 = r7 + r9
                float r2 = (float) r7
                float r2 = r2 - r14
                float r2 = r2 - r4
            L_0x029f:
                float r3 = r3 + r1
                r12.rectSizeX = r3
                float r4 = r4 + r2
                r12.rectSizeY = r4
            L_0x02a5:
                float r14 = r12.rectSizeX
                int r14 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1))
                if (r14 >= 0) goto L_0x02ad
                r12.rectSizeX = r5
            L_0x02ad:
                float r14 = r12.rectSizeY
                int r14 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1))
                if (r14 >= 0) goto L_0x02b5
                r12.rectSizeY = r5
            L_0x02b5:
                r12.oldX = r13
                r12.oldY = r0
                r12.invalidate()
            L_0x02bc:
                return r8
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoCropActivity.PhotoCropView.lambda$init$0(android.view.View, android.view.MotionEvent):boolean");
        }

        private void updateBitmapSize() {
            if (this.viewWidth != 0 && this.viewHeight != 0 && PhotoCropActivity.this.imageToCrop != null) {
                float f = this.rectX - ((float) this.bitmapX);
                int i = this.bitmapWidth;
                float f2 = f / ((float) i);
                float f3 = this.rectY - ((float) this.bitmapY);
                int i2 = this.bitmapHeight;
                float f4 = f3 / ((float) i2);
                float f5 = this.rectSizeX / ((float) i);
                float f6 = this.rectSizeY / ((float) i2);
                float width = (float) PhotoCropActivity.this.imageToCrop.getWidth();
                float height = (float) PhotoCropActivity.this.imageToCrop.getHeight();
                int i3 = this.viewWidth;
                float f7 = ((float) i3) / width;
                int i4 = this.viewHeight;
                float f8 = ((float) i4) / height;
                if (f7 > f8) {
                    this.bitmapHeight = i4;
                    this.bitmapWidth = (int) Math.ceil((double) (width * f8));
                } else {
                    this.bitmapWidth = i3;
                    this.bitmapHeight = (int) Math.ceil((double) (height * f7));
                }
                this.bitmapX = ((this.viewWidth - this.bitmapWidth) / 2) + AndroidUtilities.dp(14.0f);
                int dp = ((this.viewHeight - this.bitmapHeight) / 2) + AndroidUtilities.dp(14.0f);
                this.bitmapY = dp;
                if (this.rectX != -1.0f || this.rectY != -1.0f) {
                    int i5 = this.bitmapWidth;
                    this.rectX = (f2 * ((float) i5)) + ((float) this.bitmapX);
                    int i6 = this.bitmapHeight;
                    this.rectY = (f4 * ((float) i6)) + ((float) dp);
                    this.rectSizeX = f5 * ((float) i5);
                    this.rectSizeY = f6 * ((float) i6);
                } else if (this.freeform) {
                    this.rectY = (float) dp;
                    this.rectX = (float) this.bitmapX;
                    this.rectSizeX = (float) this.bitmapWidth;
                    this.rectSizeY = (float) this.bitmapHeight;
                } else {
                    int i7 = this.bitmapWidth;
                    int i8 = this.bitmapHeight;
                    if (i7 > i8) {
                        this.rectY = (float) dp;
                        this.rectX = (float) (((this.viewWidth - i8) / 2) + AndroidUtilities.dp(14.0f));
                        int i9 = this.bitmapHeight;
                        this.rectSizeX = (float) i9;
                        this.rectSizeY = (float) i9;
                    } else {
                        this.rectX = (float) this.bitmapX;
                        this.rectY = (float) (((this.viewHeight - i7) / 2) + AndroidUtilities.dp(14.0f));
                        int i10 = this.bitmapWidth;
                        this.rectSizeX = (float) i10;
                        this.rectSizeY = (float) i10;
                    }
                }
                invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            this.viewWidth = (i3 - i) - AndroidUtilities.dp(28.0f);
            this.viewHeight = (i4 - i2) - AndroidUtilities.dp(28.0f);
            updateBitmapSize();
        }

        public Bitmap getBitmap() {
            float f = this.rectX - ((float) this.bitmapX);
            int i = this.bitmapWidth;
            float f2 = (this.rectY - ((float) this.bitmapY)) / ((float) this.bitmapHeight);
            float f3 = this.rectSizeX / ((float) i);
            float f4 = this.rectSizeY / ((float) i);
            int width = (int) ((f / ((float) i)) * ((float) PhotoCropActivity.this.imageToCrop.getWidth()));
            int height = (int) (f2 * ((float) PhotoCropActivity.this.imageToCrop.getHeight()));
            int width2 = (int) (f3 * ((float) PhotoCropActivity.this.imageToCrop.getWidth()));
            int width3 = (int) (f4 * ((float) PhotoCropActivity.this.imageToCrop.getWidth()));
            if (width < 0) {
                width = 0;
            }
            if (height < 0) {
                height = 0;
            }
            if (width + width2 > PhotoCropActivity.this.imageToCrop.getWidth()) {
                width2 = PhotoCropActivity.this.imageToCrop.getWidth() - width;
            }
            if (height + width3 > PhotoCropActivity.this.imageToCrop.getHeight()) {
                width3 = PhotoCropActivity.this.imageToCrop.getHeight() - height;
            }
            try {
                return Bitmaps.createBitmap(PhotoCropActivity.this.imageToCrop, width, height, width2, width3);
            } catch (Throwable th) {
                FileLog.e(th);
                return null;
            }
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:14:0x01b4 A[LOOP:0: B:13:0x01b2->B:14:0x01b4, LOOP_END] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r16) {
            /*
                r15 = this;
                r1 = r15
                org.telegram.ui.PhotoCropActivity r0 = org.telegram.ui.PhotoCropActivity.this
                android.graphics.drawable.BitmapDrawable r0 = r0.drawable
                if (r0 == 0) goto L_0x0031
                org.telegram.ui.PhotoCropActivity r0 = org.telegram.ui.PhotoCropActivity.this     // Catch:{ all -> 0x002a }
                android.graphics.drawable.BitmapDrawable r0 = r0.drawable     // Catch:{ all -> 0x002a }
                int r2 = r1.bitmapX     // Catch:{ all -> 0x002a }
                int r3 = r1.bitmapY     // Catch:{ all -> 0x002a }
                int r4 = r1.bitmapWidth     // Catch:{ all -> 0x002a }
                int r4 = r4 + r2
                int r5 = r1.bitmapHeight     // Catch:{ all -> 0x002a }
                int r5 = r5 + r3
                r0.setBounds(r2, r3, r4, r5)     // Catch:{ all -> 0x002a }
                org.telegram.ui.PhotoCropActivity r0 = org.telegram.ui.PhotoCropActivity.this     // Catch:{ all -> 0x002a }
                android.graphics.drawable.BitmapDrawable r0 = r0.drawable     // Catch:{ all -> 0x002a }
                r8 = r16
                r0.draw(r8)     // Catch:{ all -> 0x0028 }
                goto L_0x0033
            L_0x0028:
                r0 = move-exception
                goto L_0x002d
            L_0x002a:
                r0 = move-exception
                r8 = r16
            L_0x002d:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x0033
            L_0x0031:
                r8 = r16
            L_0x0033:
                int r0 = r1.bitmapX
                float r3 = (float) r0
                int r2 = r1.bitmapY
                float r4 = (float) r2
                int r2 = r1.bitmapWidth
                int r0 = r0 + r2
                float r5 = (float) r0
                float r6 = r1.rectY
                android.graphics.Paint r7 = r1.halfPaint
                r2 = r16
                r2.drawRect(r3, r4, r5, r6, r7)
                int r0 = r1.bitmapX
                float r3 = (float) r0
                float r4 = r1.rectY
                float r5 = r1.rectX
                float r0 = r1.rectSizeY
                float r6 = r4 + r0
                android.graphics.Paint r7 = r1.halfPaint
                r2.drawRect(r3, r4, r5, r6, r7)
                float r0 = r1.rectX
                float r2 = r1.rectSizeX
                float r3 = r0 + r2
                float r4 = r1.rectY
                int r0 = r1.bitmapX
                int r2 = r1.bitmapWidth
                int r0 = r0 + r2
                float r5 = (float) r0
                float r0 = r1.rectSizeY
                float r6 = r4 + r0
                android.graphics.Paint r7 = r1.halfPaint
                r2 = r16
                r2.drawRect(r3, r4, r5, r6, r7)
                int r0 = r1.bitmapX
                float r3 = (float) r0
                float r2 = r1.rectY
                float r4 = r1.rectSizeY
                float r4 = r4 + r2
                int r2 = r1.bitmapWidth
                int r0 = r0 + r2
                float r5 = (float) r0
                int r0 = r1.bitmapY
                int r2 = r1.bitmapHeight
                int r0 = r0 + r2
                float r6 = (float) r0
                android.graphics.Paint r7 = r1.halfPaint
                r2 = r16
                r2.drawRect(r3, r4, r5, r6, r7)
                float r3 = r1.rectX
                float r4 = r1.rectY
                float r0 = r1.rectSizeX
                float r5 = r3 + r0
                float r0 = r1.rectSizeY
                float r6 = r4 + r0
                android.graphics.Paint r7 = r1.rectPaint
                r2.drawRect(r3, r4, r5, r6, r7)
                r0 = 1065353216(0x3var_, float:1.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                float r2 = r1.rectX
                float r9 = (float) r0
                float r3 = r2 + r9
                float r4 = r1.rectY
                float r4 = r4 + r9
                float r2 = r2 + r9
                r10 = 1101004800(0x41a00000, float:20.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r5 = (float) r5
                float r5 = r5 + r2
                float r2 = r1.rectY
                r11 = 3
                int r0 = r0 * 3
                float r0 = (float) r0
                float r6 = r2 + r0
                android.graphics.Paint r7 = r1.circlePaint
                r2 = r16
                r2.drawRect(r3, r4, r5, r6, r7)
                float r2 = r1.rectX
                float r3 = r2 + r9
                float r4 = r1.rectY
                float r5 = r4 + r9
                float r6 = r2 + r0
                float r4 = r4 + r9
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r2 = (float) r2
                float r7 = r4 + r2
                android.graphics.Paint r12 = r1.circlePaint
                r2 = r16
                r4 = r5
                r5 = r6
                r6 = r7
                r7 = r12
                r2.drawRect(r3, r4, r5, r6, r7)
                float r2 = r1.rectX
                float r3 = r1.rectSizeX
                float r2 = r2 + r3
                float r2 = r2 - r9
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r3 = (float) r3
                float r3 = r2 - r3
                float r2 = r1.rectY
                float r4 = r2 + r9
                float r5 = r1.rectX
                float r6 = r1.rectSizeX
                float r5 = r5 + r6
                float r5 = r5 - r9
                float r6 = r2 + r0
                android.graphics.Paint r7 = r1.circlePaint
                r2 = r16
                r2.drawRect(r3, r4, r5, r6, r7)
                float r2 = r1.rectX
                float r3 = r1.rectSizeX
                float r4 = r2 + r3
                float r4 = r4 - r0
                float r5 = r1.rectY
                float r6 = r5 + r9
                float r2 = r2 + r3
                float r7 = r2 - r9
                float r5 = r5 + r9
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r2 = (float) r2
                float r12 = r5 + r2
                android.graphics.Paint r13 = r1.circlePaint
                r2 = r16
                r3 = r4
                r4 = r6
                r5 = r7
                r6 = r12
                r7 = r13
                r2.drawRect(r3, r4, r5, r6, r7)
                float r2 = r1.rectX
                float r3 = r2 + r9
                float r2 = r1.rectY
                float r4 = r1.rectSizeY
                float r2 = r2 + r4
                float r2 = r2 - r9
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r4 = (float) r4
                float r4 = r2 - r4
                float r2 = r1.rectX
                float r5 = r2 + r0
                float r2 = r1.rectY
                float r6 = r1.rectSizeY
                float r2 = r2 + r6
                float r6 = r2 - r9
                android.graphics.Paint r7 = r1.circlePaint
                r2 = r16
                r2.drawRect(r3, r4, r5, r6, r7)
                float r2 = r1.rectX
                float r3 = r2 + r9
                float r4 = r1.rectY
                float r5 = r1.rectSizeY
                float r4 = r4 + r5
                float r4 = r4 - r0
                float r2 = r2 + r9
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r5 = (float) r5
                float r5 = r5 + r2
                float r2 = r1.rectY
                float r6 = r1.rectSizeY
                float r2 = r2 + r6
                float r6 = r2 - r9
                android.graphics.Paint r7 = r1.circlePaint
                r2 = r16
                r2.drawRect(r3, r4, r5, r6, r7)
                float r2 = r1.rectX
                float r3 = r1.rectSizeX
                float r2 = r2 + r3
                float r2 = r2 - r9
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r3 = (float) r3
                float r3 = r2 - r3
                float r2 = r1.rectY
                float r4 = r1.rectSizeY
                float r5 = r2 + r4
                float r5 = r5 - r0
                float r6 = r1.rectX
                float r7 = r1.rectSizeX
                float r6 = r6 + r7
                float r6 = r6 - r9
                float r2 = r2 + r4
                float r7 = r2 - r9
                android.graphics.Paint r12 = r1.circlePaint
                r2 = r16
                r4 = r5
                r5 = r6
                r6 = r7
                r7 = r12
                r2.drawRect(r3, r4, r5, r6, r7)
                float r2 = r1.rectX
                float r3 = r1.rectSizeX
                float r2 = r2 + r3
                float r3 = r2 - r0
                float r0 = r1.rectY
                float r2 = r1.rectSizeY
                float r0 = r0 + r2
                float r0 = r0 - r9
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r2 = (float) r2
                float r4 = r0 - r2
                float r0 = r1.rectX
                float r2 = r1.rectSizeX
                float r0 = r0 + r2
                float r5 = r0 - r9
                float r0 = r1.rectY
                float r2 = r1.rectSizeY
                float r0 = r0 + r2
                float r6 = r0 - r9
                android.graphics.Paint r7 = r1.circlePaint
                r2 = r16
                r2.drawRect(r3, r4, r5, r6, r7)
                r0 = 1
            L_0x01b2:
                if (r0 >= r11) goto L_0x0200
                float r2 = r1.rectX
                float r3 = r1.rectSizeX
                r10 = 1077936128(0x40400000, float:3.0)
                float r4 = r3 / r10
                float r12 = (float) r0
                float r4 = r4 * r12
                float r4 = r4 + r2
                float r5 = r1.rectY
                float r6 = r5 + r9
                float r2 = r2 + r9
                float r3 = r3 / r10
                float r3 = r3 * r12
                float r7 = r2 + r3
                float r2 = r1.rectSizeY
                float r5 = r5 + r2
                float r13 = r5 - r9
                android.graphics.Paint r14 = r1.circlePaint
                r2 = r16
                r3 = r4
                r4 = r6
                r5 = r7
                r6 = r13
                r7 = r14
                r2.drawRect(r3, r4, r5, r6, r7)
                float r2 = r1.rectX
                float r3 = r2 + r9
                float r4 = r1.rectY
                float r5 = r1.rectSizeY
                float r6 = r5 / r10
                float r6 = r6 * r12
                float r6 = r6 + r4
                float r2 = r2 - r9
                float r7 = r1.rectSizeX
                float r7 = r7 + r2
                float r5 = r5 / r10
                float r5 = r5 * r12
                float r4 = r4 + r5
                float r10 = r4 + r9
                android.graphics.Paint r12 = r1.circlePaint
                r2 = r16
                r4 = r6
                r5 = r7
                r6 = r10
                r7 = r12
                r2.drawRect(r3, r4, r5, r6, r7)
                int r0 = r0 + 1
                goto L_0x01b2
            L_0x0200:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoCropActivity.PhotoCropView.onDraw(android.graphics.Canvas):void");
        }
    }

    public PhotoCropActivity(Bundle bundle) {
        super(bundle);
    }

    public boolean onFragmentCreate() {
        int i;
        if (this.imageToCrop == null) {
            String string = getArguments().getString("photoPath");
            Uri uri = (Uri) getArguments().getParcelable("photoUri");
            if (string == null && uri == null) {
                return false;
            }
            if (string != null && !new File(string).exists()) {
                return false;
            }
            if (AndroidUtilities.isTablet()) {
                i = AndroidUtilities.dp(520.0f);
            } else {
                Point point = AndroidUtilities.displaySize;
                i = Math.max(point.x, point.y);
            }
            float f = (float) i;
            Bitmap loadBitmap = ImageLoader.loadBitmap(string, uri, f, f, true);
            this.imageToCrop = loadBitmap;
            if (loadBitmap == null) {
                return false;
            }
        }
        this.drawable = new BitmapDrawable(this.imageToCrop);
        super.onFragmentCreate();
        return true;
    }

    public void onFragmentDestroy() {
        Bitmap bitmap;
        super.onFragmentDestroy();
        if (this.bitmapKey != null && ImageLoader.getInstance().decrementUseCount(this.bitmapKey) && !ImageLoader.getInstance().isInMemCache(this.bitmapKey, false)) {
            this.bitmapKey = null;
        }
        if (this.bitmapKey == null && (bitmap = this.imageToCrop) != null && !this.sameBitmap) {
            bitmap.recycle();
            this.imageToCrop = null;
        }
        this.drawable = null;
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(-13421773);
        this.actionBar.setItemsBackgroundColor(-12763843, false);
        this.actionBar.setTitleColor(-1);
        this.actionBar.setItemsColor(-1, false);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("CropImage", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PhotoCropActivity.this.finishFragment();
                } else if (i == 1) {
                    if (PhotoCropActivity.this.delegate != null && !PhotoCropActivity.this.doneButtonPressed) {
                        Bitmap bitmap = PhotoCropActivity.this.view.getBitmap();
                        if (bitmap == PhotoCropActivity.this.imageToCrop) {
                            boolean unused = PhotoCropActivity.this.sameBitmap = true;
                        }
                        PhotoCropActivity.this.delegate.didFinishEdit(bitmap);
                        boolean unused2 = PhotoCropActivity.this.doneButtonPressed = true;
                    }
                    PhotoCropActivity.this.finishFragment();
                }
            }
        });
        this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
        PhotoCropView photoCropView = new PhotoCropView(context);
        this.view = photoCropView;
        this.fragmentView = photoCropView;
        photoCropView.freeform = getArguments().getBoolean("freeform", false);
        this.fragmentView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        return this.fragmentView;
    }

    public void setDelegate(PhotoEditActivityDelegate photoEditActivityDelegate) {
        this.delegate = photoEditActivityDelegate;
    }
}
