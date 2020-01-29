package org.telegram.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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
            this.rectPaint = new Paint();
            this.rectPaint.setColor(NUM);
            this.rectPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
            this.rectPaint.setStyle(Paint.Style.STROKE);
            this.circlePaint = new Paint();
            this.circlePaint.setColor(-1);
            this.halfPaint = new Paint();
            this.halfPaint.setColor(-NUM);
            setBackgroundColor(-13421773);
            setOnTouchListener(new View.OnTouchListener() {
                /* JADX WARNING: Removed duplicated region for block: B:50:0x00d2  */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public boolean onTouch(android.view.View r12, android.view.MotionEvent r13) {
                    /*
                        r11 = this;
                        float r12 = r13.getX()
                        float r0 = r13.getY()
                        r1 = 1096810496(0x41600000, float:14.0)
                        int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                        int r2 = r13.getAction()
                        r3 = 4
                        r4 = 3
                        r5 = 5
                        r6 = 0
                        r7 = 2
                        r8 = 1
                        if (r2 != 0) goto L_0x00dd
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r2 = r13.rectX
                        float r1 = (float) r1
                        float r9 = r2 - r1
                        int r9 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1))
                        if (r9 >= 0) goto L_0x003b
                        float r2 = r2 + r1
                        int r2 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
                        if (r2 <= 0) goto L_0x003b
                        float r2 = r13.rectY
                        float r9 = r2 - r1
                        int r9 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
                        if (r9 >= 0) goto L_0x003b
                        float r2 = r2 + r1
                        int r2 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                        if (r2 <= 0) goto L_0x003b
                        r13.draggingState = r8
                        goto L_0x00cc
                    L_0x003b:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r2 = r13.rectX
                        float r9 = r2 - r1
                        float r10 = r13.rectSizeX
                        float r9 = r9 + r10
                        int r9 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1))
                        if (r9 >= 0) goto L_0x005f
                        float r2 = r2 + r1
                        float r2 = r2 + r10
                        int r2 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
                        if (r2 <= 0) goto L_0x005f
                        float r2 = r13.rectY
                        float r9 = r2 - r1
                        int r9 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
                        if (r9 >= 0) goto L_0x005f
                        float r2 = r2 + r1
                        int r2 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                        if (r2 <= 0) goto L_0x005f
                        r13.draggingState = r7
                        goto L_0x00cc
                    L_0x005f:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r2 = r13.rectX
                        float r7 = r2 - r1
                        int r7 = (r7 > r12 ? 1 : (r7 == r12 ? 0 : -1))
                        if (r7 >= 0) goto L_0x0082
                        float r2 = r2 + r1
                        int r2 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
                        if (r2 <= 0) goto L_0x0082
                        float r2 = r13.rectY
                        float r7 = r2 - r1
                        float r9 = r13.rectSizeY
                        float r7 = r7 + r9
                        int r7 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1))
                        if (r7 >= 0) goto L_0x0082
                        float r2 = r2 + r1
                        float r2 = r2 + r9
                        int r2 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                        if (r2 <= 0) goto L_0x0082
                        r13.draggingState = r4
                        goto L_0x00cc
                    L_0x0082:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r2 = r13.rectX
                        float r4 = r2 - r1
                        float r7 = r13.rectSizeX
                        float r4 = r4 + r7
                        int r4 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
                        if (r4 >= 0) goto L_0x00a9
                        float r2 = r2 + r1
                        float r2 = r2 + r7
                        int r2 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
                        if (r2 <= 0) goto L_0x00a9
                        float r2 = r13.rectY
                        float r4 = r2 - r1
                        float r7 = r13.rectSizeY
                        float r4 = r4 + r7
                        int r4 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
                        if (r4 >= 0) goto L_0x00a9
                        float r2 = r2 + r1
                        float r2 = r2 + r7
                        int r1 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                        if (r1 <= 0) goto L_0x00a9
                        r13.draggingState = r3
                        goto L_0x00cc
                    L_0x00a9:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r1 = r13.rectX
                        int r2 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
                        if (r2 >= 0) goto L_0x00c8
                        float r2 = r13.rectSizeX
                        float r1 = r1 + r2
                        int r1 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
                        if (r1 <= 0) goto L_0x00c8
                        float r1 = r13.rectY
                        int r2 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1))
                        if (r2 >= 0) goto L_0x00c8
                        float r2 = r13.rectSizeY
                        float r1 = r1 + r2
                        int r1 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1))
                        if (r1 <= 0) goto L_0x00c8
                        r13.draggingState = r5
                        goto L_0x00cc
                    L_0x00c8:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        r13.draggingState = r6
                    L_0x00cc:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        int r1 = r13.draggingState
                        if (r1 == 0) goto L_0x00d5
                        r13.requestDisallowInterceptTouchEvent(r8)
                    L_0x00d5:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        r13.oldX = r12
                        r13.oldY = r0
                        goto L_0x0341
                    L_0x00dd:
                        int r1 = r13.getAction()
                        if (r1 != r8) goto L_0x00e9
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r12 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        r12.draggingState = r6
                        goto L_0x0341
                    L_0x00e9:
                        int r13 = r13.getAction()
                        if (r13 != r7) goto L_0x0341
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        int r1 = r13.draggingState
                        if (r1 == 0) goto L_0x0341
                        float r2 = r13.oldX
                        float r2 = r12 - r2
                        float r6 = r13.oldY
                        float r6 = r0 - r6
                        if (r1 != r5) goto L_0x014a
                        float r1 = r13.rectX
                        float r1 = r1 + r2
                        r13.rectX = r1
                        float r1 = r13.rectY
                        float r1 = r1 + r6
                        r13.rectY = r1
                        float r1 = r13.rectX
                        int r2 = r13.bitmapX
                        float r3 = (float) r2
                        int r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
                        if (r3 >= 0) goto L_0x0116
                        float r1 = (float) r2
                        r13.rectX = r1
                        goto L_0x0127
                    L_0x0116:
                        float r3 = r13.rectSizeX
                        float r1 = r1 + r3
                        int r4 = r13.bitmapWidth
                        int r5 = r2 + r4
                        float r5 = (float) r5
                        int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                        if (r1 <= 0) goto L_0x0127
                        int r2 = r2 + r4
                        float r1 = (float) r2
                        float r1 = r1 - r3
                        r13.rectX = r1
                    L_0x0127:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r1 = r13.rectY
                        int r2 = r13.bitmapY
                        float r3 = (float) r2
                        int r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
                        if (r3 >= 0) goto L_0x0137
                        float r1 = (float) r2
                        r13.rectY = r1
                        goto L_0x0338
                    L_0x0137:
                        float r3 = r13.rectSizeY
                        float r1 = r1 + r3
                        int r4 = r13.bitmapHeight
                        int r5 = r2 + r4
                        float r5 = (float) r5
                        int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                        if (r1 <= 0) goto L_0x0338
                        int r2 = r2 + r4
                        float r1 = (float) r2
                        float r1 = r1 - r3
                        r13.rectY = r1
                        goto L_0x0338
                    L_0x014a:
                        r5 = 1126170624(0x43200000, float:160.0)
                        if (r1 != r8) goto L_0x01c6
                        float r13 = r13.rectSizeX
                        float r1 = r13 - r2
                        int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                        if (r1 >= 0) goto L_0x0158
                        float r2 = r13 - r5
                    L_0x0158:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r1 = r13.rectX
                        float r3 = r1 + r2
                        int r13 = r13.bitmapX
                        float r4 = (float) r13
                        int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                        if (r3 >= 0) goto L_0x0168
                        float r13 = (float) r13
                        float r2 = r13 - r1
                    L_0x0168:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        boolean r1 = r13.freeform
                        if (r1 != 0) goto L_0x0194
                        float r1 = r13.rectY
                        float r3 = r1 + r2
                        int r13 = r13.bitmapY
                        float r4 = (float) r13
                        int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                        if (r3 >= 0) goto L_0x017c
                        float r13 = (float) r13
                        float r2 = r13 - r1
                    L_0x017c:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r1 = r13.rectX
                        float r1 = r1 + r2
                        r13.rectX = r1
                        float r1 = r13.rectY
                        float r1 = r1 + r2
                        r13.rectY = r1
                        float r1 = r13.rectSizeX
                        float r1 = r1 - r2
                        r13.rectSizeX = r1
                        float r1 = r13.rectSizeY
                        float r1 = r1 - r2
                        r13.rectSizeY = r1
                        goto L_0x0338
                    L_0x0194:
                        float r13 = r13.rectSizeY
                        float r1 = r13 - r6
                        int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                        if (r1 >= 0) goto L_0x019e
                        float r6 = r13 - r5
                    L_0x019e:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r1 = r13.rectY
                        float r3 = r1 + r6
                        int r13 = r13.bitmapY
                        float r4 = (float) r13
                        int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                        if (r3 >= 0) goto L_0x01ae
                        float r13 = (float) r13
                        float r6 = r13 - r1
                    L_0x01ae:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r1 = r13.rectX
                        float r1 = r1 + r2
                        r13.rectX = r1
                        float r1 = r13.rectY
                        float r1 = r1 + r6
                        r13.rectY = r1
                        float r1 = r13.rectSizeX
                        float r1 = r1 - r2
                        r13.rectSizeX = r1
                        float r1 = r13.rectSizeY
                        float r1 = r1 - r6
                        r13.rectSizeY = r1
                        goto L_0x0338
                    L_0x01c6:
                        if (r1 != r7) goto L_0x023f
                        float r13 = r13.rectSizeX
                        float r1 = r13 + r2
                        int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                        if (r1 >= 0) goto L_0x01d2
                        float r13 = r13 - r5
                        float r2 = -r13
                    L_0x01d2:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r1 = r13.rectX
                        float r3 = r13.rectSizeX
                        float r4 = r1 + r3
                        float r4 = r4 + r2
                        int r7 = r13.bitmapX
                        int r13 = r13.bitmapWidth
                        int r9 = r7 + r13
                        float r9 = (float) r9
                        int r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
                        if (r4 <= 0) goto L_0x01eb
                        int r7 = r7 + r13
                        float r13 = (float) r7
                        float r13 = r13 - r1
                        float r2 = r13 - r3
                    L_0x01eb:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        boolean r1 = r13.freeform
                        if (r1 != 0) goto L_0x0212
                        float r1 = r13.rectY
                        float r3 = r1 - r2
                        int r13 = r13.bitmapY
                        float r4 = (float) r13
                        int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                        if (r3 >= 0) goto L_0x01ff
                        float r13 = (float) r13
                        float r2 = r1 - r13
                    L_0x01ff:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r1 = r13.rectY
                        float r1 = r1 - r2
                        r13.rectY = r1
                        float r1 = r13.rectSizeX
                        float r1 = r1 + r2
                        r13.rectSizeX = r1
                        float r1 = r13.rectSizeY
                        float r1 = r1 + r2
                        r13.rectSizeY = r1
                        goto L_0x0338
                    L_0x0212:
                        float r13 = r13.rectSizeY
                        float r1 = r13 - r6
                        int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                        if (r1 >= 0) goto L_0x021c
                        float r6 = r13 - r5
                    L_0x021c:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r1 = r13.rectY
                        float r3 = r1 + r6
                        int r13 = r13.bitmapY
                        float r4 = (float) r13
                        int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                        if (r3 >= 0) goto L_0x022c
                        float r13 = (float) r13
                        float r6 = r13 - r1
                    L_0x022c:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r1 = r13.rectY
                        float r1 = r1 + r6
                        r13.rectY = r1
                        float r1 = r13.rectSizeX
                        float r1 = r1 + r2
                        r13.rectSizeX = r1
                        float r1 = r13.rectSizeY
                        float r1 = r1 - r6
                        r13.rectSizeY = r1
                        goto L_0x0338
                    L_0x023f:
                        if (r1 != r4) goto L_0x02be
                        float r13 = r13.rectSizeX
                        float r1 = r13 - r2
                        int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                        if (r1 >= 0) goto L_0x024b
                        float r2 = r13 - r5
                    L_0x024b:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r1 = r13.rectX
                        float r3 = r1 + r2
                        int r13 = r13.bitmapX
                        float r4 = (float) r13
                        int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                        if (r3 >= 0) goto L_0x025b
                        float r13 = (float) r13
                        float r2 = r13 - r1
                    L_0x025b:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        boolean r1 = r13.freeform
                        if (r1 != 0) goto L_0x028c
                        float r1 = r13.rectY
                        float r3 = r13.rectSizeX
                        float r4 = r1 + r3
                        float r4 = r4 - r2
                        int r5 = r13.bitmapY
                        int r13 = r13.bitmapHeight
                        int r6 = r5 + r13
                        float r6 = (float) r6
                        int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                        if (r4 <= 0) goto L_0x0279
                        float r1 = r1 + r3
                        float r2 = (float) r5
                        float r1 = r1 - r2
                        float r13 = (float) r13
                        float r2 = r1 - r13
                    L_0x0279:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r1 = r13.rectX
                        float r1 = r1 + r2
                        r13.rectX = r1
                        float r1 = r13.rectSizeX
                        float r1 = r1 - r2
                        r13.rectSizeX = r1
                        float r1 = r13.rectSizeY
                        float r1 = r1 - r2
                        r13.rectSizeY = r1
                        goto L_0x0338
                    L_0x028c:
                        float r1 = r13.rectY
                        float r3 = r13.rectSizeY
                        float r4 = r1 + r3
                        float r4 = r4 + r6
                        int r7 = r13.bitmapY
                        int r13 = r13.bitmapHeight
                        int r9 = r7 + r13
                        float r9 = (float) r9
                        int r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
                        if (r4 <= 0) goto L_0x02a3
                        int r7 = r7 + r13
                        float r13 = (float) r7
                        float r13 = r13 - r1
                        float r6 = r13 - r3
                    L_0x02a3:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r1 = r13.rectX
                        float r1 = r1 + r2
                        r13.rectX = r1
                        float r1 = r13.rectSizeX
                        float r1 = r1 - r2
                        r13.rectSizeX = r1
                        float r1 = r13.rectSizeY
                        float r1 = r1 + r6
                        r13.rectSizeY = r1
                        float r1 = r13.rectSizeY
                        int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                        if (r1 >= 0) goto L_0x0338
                        r13.rectSizeY = r5
                        goto L_0x0338
                    L_0x02be:
                        if (r1 != r3) goto L_0x0338
                        float r1 = r13.rectX
                        float r3 = r13.rectSizeX
                        float r4 = r1 + r3
                        float r4 = r4 + r2
                        int r7 = r13.bitmapX
                        int r13 = r13.bitmapWidth
                        int r9 = r7 + r13
                        float r9 = (float) r9
                        int r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
                        if (r4 <= 0) goto L_0x02d7
                        int r7 = r7 + r13
                        float r13 = (float) r7
                        float r13 = r13 - r1
                        float r2 = r13 - r3
                    L_0x02d7:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        boolean r1 = r13.freeform
                        if (r1 != 0) goto L_0x0301
                        float r1 = r13.rectY
                        float r3 = r13.rectSizeX
                        float r4 = r1 + r3
                        float r4 = r4 + r2
                        int r6 = r13.bitmapY
                        int r13 = r13.bitmapHeight
                        int r7 = r6 + r13
                        float r7 = (float) r7
                        int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
                        if (r4 <= 0) goto L_0x02f4
                        int r6 = r6 + r13
                        float r13 = (float) r6
                        float r13 = r13 - r1
                        float r2 = r13 - r3
                    L_0x02f4:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r1 = r13.rectSizeX
                        float r1 = r1 + r2
                        r13.rectSizeX = r1
                        float r1 = r13.rectSizeY
                        float r1 = r1 + r2
                        r13.rectSizeY = r1
                        goto L_0x0324
                    L_0x0301:
                        float r1 = r13.rectY
                        float r3 = r13.rectSizeY
                        float r4 = r1 + r3
                        float r4 = r4 + r6
                        int r7 = r13.bitmapY
                        int r13 = r13.bitmapHeight
                        int r9 = r7 + r13
                        float r9 = (float) r9
                        int r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
                        if (r4 <= 0) goto L_0x0318
                        int r7 = r7 + r13
                        float r13 = (float) r7
                        float r13 = r13 - r1
                        float r6 = r13 - r3
                    L_0x0318:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r1 = r13.rectSizeX
                        float r1 = r1 + r2
                        r13.rectSizeX = r1
                        float r1 = r13.rectSizeY
                        float r1 = r1 + r6
                        r13.rectSizeY = r1
                    L_0x0324:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r1 = r13.rectSizeX
                        int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                        if (r1 >= 0) goto L_0x032e
                        r13.rectSizeX = r5
                    L_0x032e:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        float r1 = r13.rectSizeY
                        int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                        if (r1 >= 0) goto L_0x0338
                        r13.rectSizeY = r5
                    L_0x0338:
                        org.telegram.ui.PhotoCropActivity$PhotoCropView r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this
                        r13.oldX = r12
                        r13.oldY = r0
                        r13.invalidate()
                    L_0x0341:
                        return r8
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoCropActivity.PhotoCropView.AnonymousClass1.onTouch(android.view.View, android.view.MotionEvent):boolean");
                }
            });
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
                this.bitmapY = ((this.viewHeight - this.bitmapHeight) / 2) + AndroidUtilities.dp(14.0f);
                if (this.rectX != -1.0f || this.rectY != -1.0f) {
                    int i5 = this.bitmapWidth;
                    this.rectX = (f2 * ((float) i5)) + ((float) this.bitmapX);
                    int i6 = this.bitmapHeight;
                    this.rectY = (f4 * ((float) i6)) + ((float) this.bitmapY);
                    this.rectSizeX = f5 * ((float) i5);
                    this.rectSizeY = f6 * ((float) i6);
                } else if (this.freeform) {
                    this.rectY = (float) this.bitmapY;
                    this.rectX = (float) this.bitmapX;
                    this.rectSizeX = (float) this.bitmapWidth;
                    this.rectSizeY = (float) this.bitmapHeight;
                } else {
                    int i7 = this.bitmapWidth;
                    int i8 = this.bitmapHeight;
                    if (i7 > i8) {
                        this.rectY = (float) this.bitmapY;
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
        /* JADX WARNING: Removed duplicated region for block: B:14:0x01b8 A[LOOP:0: B:13:0x01b6->B:14:0x01b8, LOOP_END] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r16) {
            /*
                r15 = this;
                r1 = r15
                org.telegram.ui.PhotoCropActivity r0 = org.telegram.ui.PhotoCropActivity.this
                android.graphics.drawable.BitmapDrawable r0 = r0.drawable
                if (r0 == 0) goto L_0x0035
                org.telegram.ui.PhotoCropActivity r0 = org.telegram.ui.PhotoCropActivity.this     // Catch:{ all -> 0x002e }
                android.graphics.drawable.BitmapDrawable r0 = r0.drawable     // Catch:{ all -> 0x002e }
                int r2 = r1.bitmapX     // Catch:{ all -> 0x002e }
                int r3 = r1.bitmapY     // Catch:{ all -> 0x002e }
                int r4 = r1.bitmapX     // Catch:{ all -> 0x002e }
                int r5 = r1.bitmapWidth     // Catch:{ all -> 0x002e }
                int r4 = r4 + r5
                int r5 = r1.bitmapY     // Catch:{ all -> 0x002e }
                int r6 = r1.bitmapHeight     // Catch:{ all -> 0x002e }
                int r5 = r5 + r6
                r0.setBounds(r2, r3, r4, r5)     // Catch:{ all -> 0x002e }
                org.telegram.ui.PhotoCropActivity r0 = org.telegram.ui.PhotoCropActivity.this     // Catch:{ all -> 0x002e }
                android.graphics.drawable.BitmapDrawable r0 = r0.drawable     // Catch:{ all -> 0x002e }
                r8 = r16
                r0.draw(r8)     // Catch:{ all -> 0x002c }
                goto L_0x0037
            L_0x002c:
                r0 = move-exception
                goto L_0x0031
            L_0x002e:
                r0 = move-exception
                r8 = r16
            L_0x0031:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x0037
            L_0x0035:
                r8 = r16
            L_0x0037:
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
            L_0x01b6:
                if (r0 >= r11) goto L_0x0204
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
                goto L_0x01b6
            L_0x0204:
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
        this.swipeBackEnabled = false;
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
            this.imageToCrop = ImageLoader.loadBitmap(string, uri, f, f, true);
            if (this.imageToCrop == null) {
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
        this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        PhotoCropView photoCropView = new PhotoCropView(context);
        this.view = photoCropView;
        this.fragmentView = photoCropView;
        ((PhotoCropView) this.fragmentView).freeform = getArguments().getBoolean("freeform", false);
        this.fragmentView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        return this.fragmentView;
    }

    public void setDelegate(PhotoEditActivityDelegate photoEditActivityDelegate) {
        this.delegate = photoEditActivityDelegate;
    }
}
