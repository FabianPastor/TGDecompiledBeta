package org.telegram.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
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
    private static final int done_button = 1;
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

        /* JADX WARNING: Removed duplicated region for block: B:50:0x00c8  */
        /* renamed from: lambda$init$0$org-telegram-ui-PhotoCropActivity$PhotoCropView  reason: not valid java name */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ boolean m4216lambda$init$0$orgtelegramuiPhotoCropActivity$PhotoCropView(android.view.View r17, android.view.MotionEvent r18) {
            /*
                r16 = this;
                r0 = r16
                float r1 = r18.getX()
                float r2 = r18.getY()
                r3 = 1096810496(0x41600000, float:14.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r4 = r18.getAction()
                r5 = 4
                r6 = 3
                r7 = 5
                r8 = 0
                r9 = 2
                r10 = 1
                if (r4 != 0) goto L_0x00d1
                float r4 = r0.rectX
                float r11 = (float) r3
                float r11 = r4 - r11
                int r11 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1))
                if (r11 >= 0) goto L_0x003e
                float r11 = (float) r3
                float r11 = r11 + r4
                int r11 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1))
                if (r11 <= 0) goto L_0x003e
                float r11 = r0.rectY
                float r12 = (float) r3
                float r12 = r11 - r12
                int r12 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
                if (r12 >= 0) goto L_0x003e
                float r12 = (float) r3
                float r11 = r11 + r12
                int r11 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
                if (r11 <= 0) goto L_0x003e
                r0.draggingState = r10
                goto L_0x00c4
            L_0x003e:
                float r11 = (float) r3
                float r11 = r4 - r11
                float r12 = r0.rectSizeX
                float r11 = r11 + r12
                int r11 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1))
                if (r11 >= 0) goto L_0x0061
                float r11 = (float) r3
                float r11 = r11 + r4
                float r11 = r11 + r12
                int r11 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1))
                if (r11 <= 0) goto L_0x0061
                float r11 = r0.rectY
                float r13 = (float) r3
                float r13 = r11 - r13
                int r13 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
                if (r13 >= 0) goto L_0x0061
                float r13 = (float) r3
                float r11 = r11 + r13
                int r11 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
                if (r11 <= 0) goto L_0x0061
                r0.draggingState = r9
                goto L_0x00c4
            L_0x0061:
                float r9 = (float) r3
                float r9 = r4 - r9
                int r9 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
                if (r9 >= 0) goto L_0x0084
                float r9 = (float) r3
                float r9 = r9 + r4
                int r9 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
                if (r9 <= 0) goto L_0x0084
                float r9 = r0.rectY
                float r11 = (float) r3
                float r11 = r9 - r11
                float r13 = r0.rectSizeY
                float r11 = r11 + r13
                int r11 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
                if (r11 >= 0) goto L_0x0084
                float r11 = (float) r3
                float r9 = r9 + r11
                float r9 = r9 + r13
                int r9 = (r9 > r2 ? 1 : (r9 == r2 ? 0 : -1))
                if (r9 <= 0) goto L_0x0084
                r0.draggingState = r6
                goto L_0x00c4
            L_0x0084:
                float r6 = (float) r3
                float r6 = r4 - r6
                float r6 = r6 + r12
                int r6 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
                if (r6 >= 0) goto L_0x00a9
                float r6 = (float) r3
                float r6 = r6 + r4
                float r6 = r6 + r12
                int r6 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
                if (r6 <= 0) goto L_0x00a9
                float r6 = r0.rectY
                float r9 = (float) r3
                float r9 = r6 - r9
                float r11 = r0.rectSizeY
                float r9 = r9 + r11
                int r9 = (r9 > r2 ? 1 : (r9 == r2 ? 0 : -1))
                if (r9 >= 0) goto L_0x00a9
                float r9 = (float) r3
                float r6 = r6 + r9
                float r6 = r6 + r11
                int r6 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
                if (r6 <= 0) goto L_0x00a9
                r0.draggingState = r5
                goto L_0x00c4
            L_0x00a9:
                int r5 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
                if (r5 >= 0) goto L_0x00c2
                float r4 = r4 + r12
                int r4 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
                if (r4 <= 0) goto L_0x00c2
                float r4 = r0.rectY
                int r5 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
                if (r5 >= 0) goto L_0x00c2
                float r5 = r0.rectSizeY
                float r4 = r4 + r5
                int r4 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
                if (r4 <= 0) goto L_0x00c2
                r0.draggingState = r7
                goto L_0x00c4
            L_0x00c2:
                r0.draggingState = r8
            L_0x00c4:
                int r4 = r0.draggingState
                if (r4 == 0) goto L_0x00cb
                r0.requestDisallowInterceptTouchEvent(r10)
            L_0x00cb:
                r0.oldX = r1
                r0.oldY = r2
                goto L_0x02d4
            L_0x00d1:
                int r4 = r18.getAction()
                if (r4 != r10) goto L_0x00db
                r0.draggingState = r8
                goto L_0x02d4
            L_0x00db:
                int r4 = r18.getAction()
                if (r4 != r9) goto L_0x02d4
                int r4 = r0.draggingState
                if (r4 == 0) goto L_0x02d4
                float r8 = r0.oldX
                float r8 = r1 - r8
                float r11 = r0.oldY
                float r11 = r2 - r11
                if (r4 != r7) goto L_0x0134
                float r4 = r0.rectX
                float r4 = r4 + r8
                r0.rectX = r4
                float r5 = r0.rectY
                float r5 = r5 + r11
                r0.rectY = r5
                int r6 = r0.bitmapX
                float r7 = (float) r6
                int r7 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
                if (r7 >= 0) goto L_0x0104
                float r4 = (float) r6
                r0.rectX = r4
                goto L_0x0115
            L_0x0104:
                float r7 = r0.rectSizeX
                float r4 = r4 + r7
                int r9 = r0.bitmapWidth
                int r12 = r6 + r9
                float r12 = (float) r12
                int r4 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
                if (r4 <= 0) goto L_0x0115
                int r6 = r6 + r9
                float r4 = (float) r6
                float r4 = r4 - r7
                r0.rectX = r4
            L_0x0115:
                int r4 = r0.bitmapY
                float r6 = (float) r4
                int r6 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
                if (r6 >= 0) goto L_0x0121
                float r4 = (float) r4
                r0.rectY = r4
                goto L_0x02cd
            L_0x0121:
                float r6 = r0.rectSizeY
                float r5 = r5 + r6
                int r7 = r0.bitmapHeight
                int r9 = r4 + r7
                float r9 = (float) r9
                int r5 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
                if (r5 <= 0) goto L_0x02cd
                int r4 = r4 + r7
                float r4 = (float) r4
                float r4 = r4 - r6
                r0.rectY = r4
                goto L_0x02cd
            L_0x0134:
                r7 = 1126170624(0x43200000, float:160.0)
                if (r4 != r10) goto L_0x0198
                float r4 = r0.rectSizeX
                float r5 = r4 - r8
                int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r5 >= 0) goto L_0x0142
                float r8 = r4 - r7
            L_0x0142:
                float r5 = r0.rectX
                float r6 = r5 + r8
                int r9 = r0.bitmapX
                float r12 = (float) r9
                int r6 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
                if (r6 >= 0) goto L_0x0150
                float r6 = (float) r9
                float r6 = r6 - r5
                r8 = r6
            L_0x0150:
                boolean r6 = r0.freeform
                if (r6 != 0) goto L_0x0172
                float r6 = r0.rectY
                float r7 = r6 + r8
                int r9 = r0.bitmapY
                float r12 = (float) r9
                int r7 = (r7 > r12 ? 1 : (r7 == r12 ? 0 : -1))
                if (r7 >= 0) goto L_0x0162
                float r7 = (float) r9
                float r7 = r7 - r6
                r8 = r7
            L_0x0162:
                float r5 = r5 + r8
                r0.rectX = r5
                float r6 = r6 + r8
                r0.rectY = r6
                float r4 = r4 - r8
                r0.rectSizeX = r4
                float r4 = r0.rectSizeY
                float r4 = r4 - r8
                r0.rectSizeY = r4
                goto L_0x02cd
            L_0x0172:
                float r6 = r0.rectSizeY
                float r9 = r6 - r11
                int r9 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
                if (r9 >= 0) goto L_0x017c
                float r11 = r6 - r7
            L_0x017c:
                float r7 = r0.rectY
                float r9 = r7 + r11
                int r12 = r0.bitmapY
                float r13 = (float) r12
                int r9 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
                if (r9 >= 0) goto L_0x018a
                float r9 = (float) r12
                float r9 = r9 - r7
                r11 = r9
            L_0x018a:
                float r5 = r5 + r8
                r0.rectX = r5
                float r7 = r7 + r11
                r0.rectY = r7
                float r4 = r4 - r8
                r0.rectSizeX = r4
                float r6 = r6 - r11
                r0.rectSizeY = r6
                goto L_0x02cd
            L_0x0198:
                if (r4 != r9) goto L_0x01fd
                float r4 = r0.rectSizeX
                float r5 = r4 + r8
                int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r5 >= 0) goto L_0x01a5
                float r5 = r4 - r7
                float r8 = -r5
            L_0x01a5:
                float r5 = r0.rectX
                float r6 = r5 + r4
                float r6 = r6 + r8
                int r9 = r0.bitmapX
                int r12 = r0.bitmapWidth
                int r13 = r9 + r12
                float r13 = (float) r13
                int r6 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
                if (r6 <= 0) goto L_0x01ba
                int r9 = r9 + r12
                float r6 = (float) r9
                float r6 = r6 - r5
                float r6 = r6 - r4
                r8 = r6
            L_0x01ba:
                boolean r5 = r0.freeform
                if (r5 != 0) goto L_0x01da
                float r5 = r0.rectY
                float r6 = r5 - r8
                int r7 = r0.bitmapY
                float r9 = (float) r7
                int r6 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
                if (r6 >= 0) goto L_0x01cd
                float r6 = (float) r7
                float r6 = r5 - r6
                r8 = r6
            L_0x01cd:
                float r5 = r5 - r8
                r0.rectY = r5
                float r4 = r4 + r8
                r0.rectSizeX = r4
                float r4 = r0.rectSizeY
                float r4 = r4 + r8
                r0.rectSizeY = r4
                goto L_0x02cd
            L_0x01da:
                float r5 = r0.rectSizeY
                float r6 = r5 - r11
                int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
                if (r6 >= 0) goto L_0x01e4
                float r11 = r5 - r7
            L_0x01e4:
                float r6 = r0.rectY
                float r7 = r6 + r11
                int r9 = r0.bitmapY
                float r12 = (float) r9
                int r7 = (r7 > r12 ? 1 : (r7 == r12 ? 0 : -1))
                if (r7 >= 0) goto L_0x01f2
                float r7 = (float) r9
                float r7 = r7 - r6
                r11 = r7
            L_0x01f2:
                float r6 = r6 + r11
                r0.rectY = r6
                float r4 = r4 + r8
                r0.rectSizeX = r4
                float r5 = r5 - r11
                r0.rectSizeY = r5
                goto L_0x02cd
            L_0x01fd:
                if (r4 != r6) goto L_0x0265
                float r4 = r0.rectSizeX
                float r5 = r4 - r8
                int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r5 >= 0) goto L_0x0209
                float r8 = r4 - r7
            L_0x0209:
                float r5 = r0.rectX
                float r6 = r5 + r8
                int r9 = r0.bitmapX
                float r12 = (float) r9
                int r6 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
                if (r6 >= 0) goto L_0x0217
                float r6 = (float) r9
                float r6 = r6 - r5
                r8 = r6
            L_0x0217:
                boolean r6 = r0.freeform
                if (r6 != 0) goto L_0x023e
                float r6 = r0.rectY
                float r7 = r6 + r4
                float r7 = r7 - r8
                int r9 = r0.bitmapY
                int r12 = r0.bitmapHeight
                int r13 = r9 + r12
                float r13 = (float) r13
                int r7 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
                if (r7 <= 0) goto L_0x0231
                float r6 = r6 + r4
                float r7 = (float) r9
                float r6 = r6 - r7
                float r7 = (float) r12
                float r6 = r6 - r7
                r8 = r6
            L_0x0231:
                float r5 = r5 + r8
                r0.rectX = r5
                float r4 = r4 - r8
                r0.rectSizeX = r4
                float r4 = r0.rectSizeY
                float r4 = r4 - r8
                r0.rectSizeY = r4
                goto L_0x02cd
            L_0x023e:
                float r6 = r0.rectY
                float r9 = r0.rectSizeY
                float r12 = r6 + r9
                float r12 = r12 + r11
                int r13 = r0.bitmapY
                int r14 = r0.bitmapHeight
                int r15 = r13 + r14
                float r15 = (float) r15
                int r12 = (r12 > r15 ? 1 : (r12 == r15 ? 0 : -1))
                if (r12 <= 0) goto L_0x0255
                int r13 = r13 + r14
                float r12 = (float) r13
                float r12 = r12 - r6
                float r11 = r12 - r9
            L_0x0255:
                float r5 = r5 + r8
                r0.rectX = r5
                float r4 = r4 - r8
                r0.rectSizeX = r4
                float r9 = r9 + r11
                r0.rectSizeY = r9
                int r4 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
                if (r4 >= 0) goto L_0x02cd
                r0.rectSizeY = r7
                goto L_0x02cd
            L_0x0265:
                if (r4 != r5) goto L_0x02cd
                float r4 = r0.rectX
                float r5 = r0.rectSizeX
                float r6 = r4 + r5
                float r6 = r6 + r8
                int r9 = r0.bitmapX
                int r12 = r0.bitmapWidth
                int r13 = r9 + r12
                float r13 = (float) r13
                int r6 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
                if (r6 <= 0) goto L_0x027e
                int r9 = r9 + r12
                float r6 = (float) r9
                float r6 = r6 - r4
                float r8 = r6 - r5
            L_0x027e:
                boolean r4 = r0.freeform
                if (r4 != 0) goto L_0x02a0
                float r4 = r0.rectY
                float r6 = r4 + r5
                float r6 = r6 + r8
                int r9 = r0.bitmapY
                int r12 = r0.bitmapHeight
                int r13 = r9 + r12
                float r13 = (float) r13
                int r6 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
                if (r6 <= 0) goto L_0x0297
                int r9 = r9 + r12
                float r6 = (float) r9
                float r6 = r6 - r4
                float r8 = r6 - r5
            L_0x0297:
                float r5 = r5 + r8
                r0.rectSizeX = r5
                float r4 = r0.rectSizeY
                float r4 = r4 + r8
                r0.rectSizeY = r4
                goto L_0x02bd
            L_0x02a0:
                float r4 = r0.rectY
                float r6 = r0.rectSizeY
                float r9 = r4 + r6
                float r9 = r9 + r11
                int r12 = r0.bitmapY
                int r13 = r0.bitmapHeight
                int r14 = r12 + r13
                float r14 = (float) r14
                int r9 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1))
                if (r9 <= 0) goto L_0x02b7
                int r12 = r12 + r13
                float r9 = (float) r12
                float r9 = r9 - r4
                float r11 = r9 - r6
            L_0x02b7:
                float r5 = r5 + r8
                r0.rectSizeX = r5
                float r6 = r6 + r11
                r0.rectSizeY = r6
            L_0x02bd:
                float r4 = r0.rectSizeX
                int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
                if (r4 >= 0) goto L_0x02c5
                r0.rectSizeX = r7
            L_0x02c5:
                float r4 = r0.rectSizeY
                int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
                if (r4 >= 0) goto L_0x02cd
                r0.rectSizeY = r7
            L_0x02cd:
                r0.oldX = r1
                r0.oldY = r2
                r16.invalidate()
            L_0x02d4:
                return r10
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoCropActivity.PhotoCropView.m4216lambda$init$0$orgtelegramuiPhotoCropActivity$PhotoCropView(android.view.View, android.view.MotionEvent):boolean");
        }

        private void updateBitmapSize() {
            if (this.viewWidth != 0 && this.viewHeight != 0 && PhotoCropActivity.this.imageToCrop != null) {
                float f = this.rectX - ((float) this.bitmapX);
                int i = this.bitmapWidth;
                float percX = f / ((float) i);
                float f2 = this.rectY - ((float) this.bitmapY);
                int i2 = this.bitmapHeight;
                float percY = f2 / ((float) i2);
                float percSizeX = this.rectSizeX / ((float) i);
                float percSizeY = this.rectSizeY / ((float) i2);
                float w = (float) PhotoCropActivity.this.imageToCrop.getWidth();
                float h = (float) PhotoCropActivity.this.imageToCrop.getHeight();
                int i3 = this.viewWidth;
                float scaleX = ((float) i3) / w;
                int i4 = this.viewHeight;
                float scaleY = ((float) i4) / h;
                if (scaleX > scaleY) {
                    this.bitmapHeight = i4;
                    this.bitmapWidth = (int) Math.ceil((double) (w * scaleY));
                } else {
                    this.bitmapWidth = i3;
                    this.bitmapHeight = (int) Math.ceil((double) (h * scaleX));
                }
                this.bitmapX = ((this.viewWidth - this.bitmapWidth) / 2) + AndroidUtilities.dp(14.0f);
                int dp = ((this.viewHeight - this.bitmapHeight) / 2) + AndroidUtilities.dp(14.0f);
                this.bitmapY = dp;
                if (this.rectX != -1.0f || this.rectY != -1.0f) {
                    int i5 = this.bitmapWidth;
                    this.rectX = (((float) i5) * percX) + ((float) this.bitmapX);
                    int i6 = this.bitmapHeight;
                    this.rectY = (((float) i6) * percY) + ((float) dp);
                    this.rectSizeX = ((float) i5) * percSizeX;
                    this.rectSizeY = ((float) i6) * percSizeY;
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
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            this.viewWidth = (right - left) - AndroidUtilities.dp(28.0f);
            this.viewHeight = (bottom - top) - AndroidUtilities.dp(28.0f);
            updateBitmapSize();
        }

        public Bitmap getBitmap() {
            float f = this.rectX - ((float) this.bitmapX);
            int i = this.bitmapWidth;
            float percY = (this.rectY - ((float) this.bitmapY)) / ((float) this.bitmapHeight);
            float percSizeX = this.rectSizeX / ((float) i);
            float percSizeY = this.rectSizeY / ((float) i);
            int x = (int) (((float) PhotoCropActivity.this.imageToCrop.getWidth()) * (f / ((float) i)));
            int y = (int) (((float) PhotoCropActivity.this.imageToCrop.getHeight()) * percY);
            int sizeX = (int) (((float) PhotoCropActivity.this.imageToCrop.getWidth()) * percSizeX);
            int sizeY = (int) (((float) PhotoCropActivity.this.imageToCrop.getWidth()) * percSizeY);
            if (x < 0) {
                x = 0;
            }
            if (y < 0) {
                y = 0;
            }
            if (x + sizeX > PhotoCropActivity.this.imageToCrop.getWidth()) {
                sizeX = PhotoCropActivity.this.imageToCrop.getWidth() - x;
            }
            if (y + sizeY > PhotoCropActivity.this.imageToCrop.getHeight()) {
                sizeY = PhotoCropActivity.this.imageToCrop.getHeight() - y;
            }
            try {
                return Bitmaps.createBitmap(PhotoCropActivity.this.imageToCrop, x, y, sizeX, sizeY);
            } catch (Throwable e2) {
                FileLog.e(e2);
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (PhotoCropActivity.this.drawable != null) {
                try {
                    BitmapDrawable access$100 = PhotoCropActivity.this.drawable;
                    int i = this.bitmapX;
                    int i2 = this.bitmapY;
                    access$100.setBounds(i, i2, this.bitmapWidth + i, this.bitmapHeight + i2);
                    PhotoCropActivity.this.drawable.draw(canvas);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            int i3 = this.bitmapX;
            Canvas canvas2 = canvas;
            canvas2.drawRect((float) i3, (float) this.bitmapY, (float) (i3 + this.bitmapWidth), this.rectY, this.halfPaint);
            float f = this.rectY;
            canvas2.drawRect((float) this.bitmapX, f, this.rectX, f + this.rectSizeY, this.halfPaint);
            float f2 = this.rectY;
            canvas.drawRect(this.rectX + this.rectSizeX, f2, (float) (this.bitmapX + this.bitmapWidth), f2 + this.rectSizeY, this.halfPaint);
            int i4 = this.bitmapX;
            Canvas canvas3 = canvas;
            canvas3.drawRect((float) i4, this.rectSizeY + this.rectY, (float) (i4 + this.bitmapWidth), (float) (this.bitmapY + this.bitmapHeight), this.halfPaint);
            float f3 = this.rectX;
            float f4 = this.rectY;
            canvas.drawRect(f3, f4, f3 + this.rectSizeX, f4 + this.rectSizeY, this.rectPaint);
            int side = AndroidUtilities.dp(1.0f);
            float f5 = this.rectX;
            canvas.drawRect(f5 + ((float) side), this.rectY + ((float) side), f5 + ((float) side) + ((float) AndroidUtilities.dp(20.0f)), this.rectY + ((float) (side * 3)), this.circlePaint);
            float f6 = this.rectX;
            float f7 = this.rectY;
            Canvas canvas4 = canvas;
            canvas4.drawRect(f6 + ((float) side), f7 + ((float) side), f6 + ((float) (side * 3)), f7 + ((float) side) + ((float) AndroidUtilities.dp(20.0f)), this.circlePaint);
            float dp = ((this.rectX + this.rectSizeX) - ((float) side)) - ((float) AndroidUtilities.dp(20.0f));
            float f8 = this.rectY;
            canvas.drawRect(dp, f8 + ((float) side), (this.rectX + this.rectSizeX) - ((float) side), f8 + ((float) (side * 3)), this.circlePaint);
            float f9 = this.rectX;
            float var_ = this.rectSizeX;
            float var_ = this.rectY;
            Canvas canvas5 = canvas;
            canvas5.drawRect((f9 + var_) - ((float) (side * 3)), var_ + ((float) side), (f9 + var_) - ((float) side), var_ + ((float) side) + ((float) AndroidUtilities.dp(20.0f)), this.circlePaint);
            canvas.drawRect(this.rectX + ((float) side), ((this.rectY + this.rectSizeY) - ((float) side)) - ((float) AndroidUtilities.dp(20.0f)), this.rectX + ((float) (side * 3)), (this.rectY + this.rectSizeY) - ((float) side), this.circlePaint);
            float var_ = this.rectX;
            canvas.drawRect(var_ + ((float) side), (this.rectY + this.rectSizeY) - ((float) (side * 3)), var_ + ((float) side) + ((float) AndroidUtilities.dp(20.0f)), (this.rectY + this.rectSizeY) - ((float) side), this.circlePaint);
            float dp2 = ((this.rectX + this.rectSizeX) - ((float) side)) - ((float) AndroidUtilities.dp(20.0f));
            float var_ = this.rectY;
            float var_ = this.rectSizeY;
            Canvas canvas6 = canvas;
            canvas6.drawRect(dp2, (var_ + var_) - ((float) (side * 3)), (this.rectX + this.rectSizeX) - ((float) side), (var_ + var_) - ((float) side), this.circlePaint);
            canvas6.drawRect((this.rectX + this.rectSizeX) - ((float) (side * 3)), ((this.rectY + this.rectSizeY) - ((float) side)) - ((float) AndroidUtilities.dp(20.0f)), (this.rectX + this.rectSizeX) - ((float) side), (this.rectY + this.rectSizeY) - ((float) side), this.circlePaint);
            for (int a = 1; a < 3; a++) {
                float var_ = this.rectX;
                float var_ = this.rectSizeX;
                float var_ = this.rectY;
                Canvas canvas7 = canvas;
                canvas7.drawRect(var_ + ((var_ / 3.0f) * ((float) a)), var_ + ((float) side), var_ + ((float) side) + ((var_ / 3.0f) * ((float) a)), (var_ + this.rectSizeY) - ((float) side), this.circlePaint);
                float var_ = this.rectX;
                float var_ = this.rectY;
                float var_ = this.rectSizeY;
                Canvas canvas8 = canvas;
                canvas8.drawRect(var_ + ((float) side), ((var_ / 3.0f) * ((float) a)) + var_, this.rectSizeX + (var_ - ((float) side)), var_ + ((var_ / 3.0f) * ((float) a)) + ((float) side), this.circlePaint);
            }
        }
    }

    public PhotoCropActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        int size;
        if (this.imageToCrop == null) {
            String photoPath = getArguments().getString("photoPath");
            Uri photoUri = (Uri) getArguments().getParcelable("photoUri");
            if (photoPath == null && photoUri == null) {
                return false;
            }
            if (photoPath != null && !new File(photoPath).exists()) {
                return false;
            }
            if (AndroidUtilities.isTablet()) {
                size = AndroidUtilities.dp(520.0f);
            } else {
                size = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
            }
            Bitmap loadBitmap = ImageLoader.loadBitmap(photoPath, photoUri, (float) size, (float) size, true);
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
            public void onItemClick(int id) {
                if (id == -1) {
                    PhotoCropActivity.this.finishFragment();
                } else if (id == 1) {
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
        this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), (CharSequence) LocaleController.getString("Done", NUM));
        PhotoCropView photoCropView = new PhotoCropView(context);
        this.view = photoCropView;
        this.fragmentView = photoCropView;
        ((PhotoCropView) this.fragmentView).freeform = getArguments().getBoolean("freeform", false);
        this.fragmentView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        return this.fragmentView;
    }

    public boolean isSwipeBackEnabled(MotionEvent event) {
        return false;
    }

    public void setDelegate(PhotoEditActivityDelegate delegate2) {
        this.delegate = delegate2;
    }
}
