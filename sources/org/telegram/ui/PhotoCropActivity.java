package org.telegram.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;

public class PhotoCropActivity extends BaseFragment {
    private static final int done_button = 1;
    private String bitmapKey;
    private PhotoEditActivityDelegate delegate = null;
    private boolean doneButtonPressed = false;
    private BitmapDrawable drawable;
    private Bitmap imageToCrop;
    private boolean sameBitmap = false;
    private PhotoCropView view;

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
            this.rectPaint.setStyle(Style.STROKE);
            this.circlePaint = new Paint();
            this.circlePaint.setColor(-1);
            this.halfPaint = new Paint();
            this.halfPaint.setColor(-NUM);
            setBackgroundColor(-13421773);
            setOnTouchListener(new OnTouchListener() {
                /* JADX WARNING: Removed duplicated region for block: B:50:0x00d2  */
                /* JADX WARNING: Removed duplicated region for block: B:50:0x00d2  */
                /* JADX WARNING: Removed duplicated region for block: B:50:0x00d2  */
                /* JADX WARNING: Removed duplicated region for block: B:50:0x00d2  */
                /* JADX WARNING: Removed duplicated region for block: B:50:0x00d2  */
                public boolean onTouch(android.view.View r12, android.view.MotionEvent r13) {
                    /*
                    r11 = this;
                    r12 = r13.getX();
                    r0 = r13.getY();
                    r1 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
                    r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                    r2 = r13.getAction();
                    r3 = 4;
                    r4 = 3;
                    r5 = 5;
                    r6 = 0;
                    r7 = 2;
                    r8 = 1;
                    if (r2 != 0) goto L_0x00dd;
                L_0x001a:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r2 = r13.rectX;
                    r1 = (float) r1;
                    r9 = r2 - r1;
                    r9 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1));
                    if (r9 >= 0) goto L_0x003b;
                L_0x0025:
                    r2 = r2 + r1;
                    r2 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1));
                    if (r2 <= 0) goto L_0x003b;
                L_0x002a:
                    r2 = r13.rectY;
                    r9 = r2 - r1;
                    r9 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1));
                    if (r9 >= 0) goto L_0x003b;
                L_0x0032:
                    r2 = r2 + r1;
                    r2 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
                    if (r2 <= 0) goto L_0x003b;
                L_0x0037:
                    r13.draggingState = r8;
                    goto L_0x00cc;
                L_0x003b:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r2 = r13.rectX;
                    r9 = r2 - r1;
                    r10 = r13.rectSizeX;
                    r9 = r9 + r10;
                    r9 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1));
                    if (r9 >= 0) goto L_0x005f;
                L_0x0048:
                    r2 = r2 + r1;
                    r2 = r2 + r10;
                    r2 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1));
                    if (r2 <= 0) goto L_0x005f;
                L_0x004e:
                    r2 = r13.rectY;
                    r9 = r2 - r1;
                    r9 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1));
                    if (r9 >= 0) goto L_0x005f;
                L_0x0056:
                    r2 = r2 + r1;
                    r2 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
                    if (r2 <= 0) goto L_0x005f;
                L_0x005b:
                    r13.draggingState = r7;
                    goto L_0x00cc;
                L_0x005f:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r2 = r13.rectX;
                    r7 = r2 - r1;
                    r7 = (r7 > r12 ? 1 : (r7 == r12 ? 0 : -1));
                    if (r7 >= 0) goto L_0x0082;
                L_0x0069:
                    r2 = r2 + r1;
                    r2 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1));
                    if (r2 <= 0) goto L_0x0082;
                L_0x006e:
                    r2 = r13.rectY;
                    r7 = r2 - r1;
                    r9 = r13.rectSizeY;
                    r7 = r7 + r9;
                    r7 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1));
                    if (r7 >= 0) goto L_0x0082;
                L_0x0079:
                    r2 = r2 + r1;
                    r2 = r2 + r9;
                    r2 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
                    if (r2 <= 0) goto L_0x0082;
                L_0x007f:
                    r13.draggingState = r4;
                    goto L_0x00cc;
                L_0x0082:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r2 = r13.rectX;
                    r4 = r2 - r1;
                    r7 = r13.rectSizeX;
                    r4 = r4 + r7;
                    r4 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1));
                    if (r4 >= 0) goto L_0x00a9;
                L_0x008f:
                    r2 = r2 + r1;
                    r2 = r2 + r7;
                    r2 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1));
                    if (r2 <= 0) goto L_0x00a9;
                L_0x0095:
                    r2 = r13.rectY;
                    r4 = r2 - r1;
                    r7 = r13.rectSizeY;
                    r4 = r4 + r7;
                    r4 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
                    if (r4 >= 0) goto L_0x00a9;
                L_0x00a0:
                    r2 = r2 + r1;
                    r2 = r2 + r7;
                    r1 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
                    if (r1 <= 0) goto L_0x00a9;
                L_0x00a6:
                    r13.draggingState = r3;
                    goto L_0x00cc;
                L_0x00a9:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.rectX;
                    r2 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1));
                    if (r2 >= 0) goto L_0x00c8;
                L_0x00b1:
                    r2 = r13.rectSizeX;
                    r1 = r1 + r2;
                    r1 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1));
                    if (r1 <= 0) goto L_0x00c8;
                L_0x00b8:
                    r1 = r13.rectY;
                    r2 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1));
                    if (r2 >= 0) goto L_0x00c8;
                L_0x00be:
                    r2 = r13.rectSizeY;
                    r1 = r1 + r2;
                    r1 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1));
                    if (r1 <= 0) goto L_0x00c8;
                L_0x00c5:
                    r13.draggingState = r5;
                    goto L_0x00cc;
                L_0x00c8:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r13.draggingState = r6;
                L_0x00cc:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.draggingState;
                    if (r1 == 0) goto L_0x00d5;
                L_0x00d2:
                    r13.requestDisallowInterceptTouchEvent(r8);
                L_0x00d5:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r13.oldX = r12;
                    r13.oldY = r0;
                    goto L_0x0341;
                L_0x00dd:
                    r1 = r13.getAction();
                    if (r1 != r8) goto L_0x00e9;
                L_0x00e3:
                    r12 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r12.draggingState = r6;
                    goto L_0x0341;
                L_0x00e9:
                    r13 = r13.getAction();
                    if (r13 != r7) goto L_0x0341;
                L_0x00ef:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.draggingState;
                    if (r1 == 0) goto L_0x0341;
                L_0x00f5:
                    r2 = r13.oldX;
                    r2 = r12 - r2;
                    r6 = r13.oldY;
                    r6 = r0 - r6;
                    if (r1 != r5) goto L_0x014a;
                L_0x00ff:
                    r1 = r13.rectX;
                    r1 = r1 + r2;
                    r13.rectX = r1;
                    r1 = r13.rectY;
                    r1 = r1 + r6;
                    r13.rectY = r1;
                    r1 = r13.rectX;
                    r2 = r13.bitmapX;
                    r3 = (float) r2;
                    r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
                    if (r3 >= 0) goto L_0x0116;
                L_0x0112:
                    r1 = (float) r2;
                    r13.rectX = r1;
                    goto L_0x0127;
                L_0x0116:
                    r3 = r13.rectSizeX;
                    r1 = r1 + r3;
                    r4 = r13.bitmapWidth;
                    r5 = r2 + r4;
                    r5 = (float) r5;
                    r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
                    if (r1 <= 0) goto L_0x0127;
                L_0x0122:
                    r2 = r2 + r4;
                    r1 = (float) r2;
                    r1 = r1 - r3;
                    r13.rectX = r1;
                L_0x0127:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.rectY;
                    r2 = r13.bitmapY;
                    r3 = (float) r2;
                    r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
                    if (r3 >= 0) goto L_0x0137;
                L_0x0132:
                    r1 = (float) r2;
                    r13.rectY = r1;
                    goto L_0x0338;
                L_0x0137:
                    r3 = r13.rectSizeY;
                    r1 = r1 + r3;
                    r4 = r13.bitmapHeight;
                    r5 = r2 + r4;
                    r5 = (float) r5;
                    r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
                    if (r1 <= 0) goto L_0x0338;
                L_0x0143:
                    r2 = r2 + r4;
                    r1 = (float) r2;
                    r1 = r1 - r3;
                    r13.rectY = r1;
                    goto L_0x0338;
                L_0x014a:
                    r5 = NUM; // 0x43200000 float:160.0 double:5.564022167E-315;
                    if (r1 != r8) goto L_0x01c6;
                L_0x014e:
                    r13 = r13.rectSizeX;
                    r1 = r13 - r2;
                    r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
                    if (r1 >= 0) goto L_0x0158;
                L_0x0156:
                    r2 = r13 - r5;
                L_0x0158:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.rectX;
                    r3 = r1 + r2;
                    r13 = r13.bitmapX;
                    r4 = (float) r13;
                    r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1));
                    if (r3 >= 0) goto L_0x0168;
                L_0x0165:
                    r13 = (float) r13;
                    r2 = r13 - r1;
                L_0x0168:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.freeform;
                    if (r1 != 0) goto L_0x0194;
                L_0x016e:
                    r1 = r13.rectY;
                    r3 = r1 + r2;
                    r13 = r13.bitmapY;
                    r4 = (float) r13;
                    r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1));
                    if (r3 >= 0) goto L_0x017c;
                L_0x0179:
                    r13 = (float) r13;
                    r2 = r13 - r1;
                L_0x017c:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.rectX;
                    r1 = r1 + r2;
                    r13.rectX = r1;
                    r1 = r13.rectY;
                    r1 = r1 + r2;
                    r13.rectY = r1;
                    r1 = r13.rectSizeX;
                    r1 = r1 - r2;
                    r13.rectSizeX = r1;
                    r1 = r13.rectSizeY;
                    r1 = r1 - r2;
                    r13.rectSizeY = r1;
                    goto L_0x0338;
                L_0x0194:
                    r13 = r13.rectSizeY;
                    r1 = r13 - r6;
                    r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
                    if (r1 >= 0) goto L_0x019e;
                L_0x019c:
                    r6 = r13 - r5;
                L_0x019e:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.rectY;
                    r3 = r1 + r6;
                    r13 = r13.bitmapY;
                    r4 = (float) r13;
                    r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1));
                    if (r3 >= 0) goto L_0x01ae;
                L_0x01ab:
                    r13 = (float) r13;
                    r6 = r13 - r1;
                L_0x01ae:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.rectX;
                    r1 = r1 + r2;
                    r13.rectX = r1;
                    r1 = r13.rectY;
                    r1 = r1 + r6;
                    r13.rectY = r1;
                    r1 = r13.rectSizeX;
                    r1 = r1 - r2;
                    r13.rectSizeX = r1;
                    r1 = r13.rectSizeY;
                    r1 = r1 - r6;
                    r13.rectSizeY = r1;
                    goto L_0x0338;
                L_0x01c6:
                    if (r1 != r7) goto L_0x023f;
                L_0x01c8:
                    r13 = r13.rectSizeX;
                    r1 = r13 + r2;
                    r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
                    if (r1 >= 0) goto L_0x01d2;
                L_0x01d0:
                    r13 = r13 - r5;
                    r2 = -r13;
                L_0x01d2:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.rectX;
                    r3 = r13.rectSizeX;
                    r4 = r1 + r3;
                    r4 = r4 + r2;
                    r7 = r13.bitmapX;
                    r13 = r13.bitmapWidth;
                    r9 = r7 + r13;
                    r9 = (float) r9;
                    r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1));
                    if (r4 <= 0) goto L_0x01eb;
                L_0x01e6:
                    r7 = r7 + r13;
                    r13 = (float) r7;
                    r13 = r13 - r1;
                    r2 = r13 - r3;
                L_0x01eb:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.freeform;
                    if (r1 != 0) goto L_0x0212;
                L_0x01f1:
                    r1 = r13.rectY;
                    r3 = r1 - r2;
                    r13 = r13.bitmapY;
                    r4 = (float) r13;
                    r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1));
                    if (r3 >= 0) goto L_0x01ff;
                L_0x01fc:
                    r13 = (float) r13;
                    r2 = r1 - r13;
                L_0x01ff:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.rectY;
                    r1 = r1 - r2;
                    r13.rectY = r1;
                    r1 = r13.rectSizeX;
                    r1 = r1 + r2;
                    r13.rectSizeX = r1;
                    r1 = r13.rectSizeY;
                    r1 = r1 + r2;
                    r13.rectSizeY = r1;
                    goto L_0x0338;
                L_0x0212:
                    r13 = r13.rectSizeY;
                    r1 = r13 - r6;
                    r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
                    if (r1 >= 0) goto L_0x021c;
                L_0x021a:
                    r6 = r13 - r5;
                L_0x021c:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.rectY;
                    r3 = r1 + r6;
                    r13 = r13.bitmapY;
                    r4 = (float) r13;
                    r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1));
                    if (r3 >= 0) goto L_0x022c;
                L_0x0229:
                    r13 = (float) r13;
                    r6 = r13 - r1;
                L_0x022c:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.rectY;
                    r1 = r1 + r6;
                    r13.rectY = r1;
                    r1 = r13.rectSizeX;
                    r1 = r1 + r2;
                    r13.rectSizeX = r1;
                    r1 = r13.rectSizeY;
                    r1 = r1 - r6;
                    r13.rectSizeY = r1;
                    goto L_0x0338;
                L_0x023f:
                    if (r1 != r4) goto L_0x02be;
                L_0x0241:
                    r13 = r13.rectSizeX;
                    r1 = r13 - r2;
                    r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
                    if (r1 >= 0) goto L_0x024b;
                L_0x0249:
                    r2 = r13 - r5;
                L_0x024b:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.rectX;
                    r3 = r1 + r2;
                    r13 = r13.bitmapX;
                    r4 = (float) r13;
                    r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1));
                    if (r3 >= 0) goto L_0x025b;
                L_0x0258:
                    r13 = (float) r13;
                    r2 = r13 - r1;
                L_0x025b:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.freeform;
                    if (r1 != 0) goto L_0x028c;
                L_0x0261:
                    r1 = r13.rectY;
                    r3 = r13.rectSizeX;
                    r4 = r1 + r3;
                    r4 = r4 - r2;
                    r5 = r13.bitmapY;
                    r13 = r13.bitmapHeight;
                    r6 = r5 + r13;
                    r6 = (float) r6;
                    r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
                    if (r4 <= 0) goto L_0x0279;
                L_0x0273:
                    r1 = r1 + r3;
                    r2 = (float) r5;
                    r1 = r1 - r2;
                    r13 = (float) r13;
                    r2 = r1 - r13;
                L_0x0279:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.rectX;
                    r1 = r1 + r2;
                    r13.rectX = r1;
                    r1 = r13.rectSizeX;
                    r1 = r1 - r2;
                    r13.rectSizeX = r1;
                    r1 = r13.rectSizeY;
                    r1 = r1 - r2;
                    r13.rectSizeY = r1;
                    goto L_0x0338;
                L_0x028c:
                    r1 = r13.rectY;
                    r3 = r13.rectSizeY;
                    r4 = r1 + r3;
                    r4 = r4 + r6;
                    r7 = r13.bitmapY;
                    r13 = r13.bitmapHeight;
                    r9 = r7 + r13;
                    r9 = (float) r9;
                    r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1));
                    if (r4 <= 0) goto L_0x02a3;
                L_0x029e:
                    r7 = r7 + r13;
                    r13 = (float) r7;
                    r13 = r13 - r1;
                    r6 = r13 - r3;
                L_0x02a3:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.rectX;
                    r1 = r1 + r2;
                    r13.rectX = r1;
                    r1 = r13.rectSizeX;
                    r1 = r1 - r2;
                    r13.rectSizeX = r1;
                    r1 = r13.rectSizeY;
                    r1 = r1 + r6;
                    r13.rectSizeY = r1;
                    r1 = r13.rectSizeY;
                    r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
                    if (r1 >= 0) goto L_0x0338;
                L_0x02ba:
                    r13.rectSizeY = r5;
                    goto L_0x0338;
                L_0x02be:
                    if (r1 != r3) goto L_0x0338;
                L_0x02c0:
                    r1 = r13.rectX;
                    r3 = r13.rectSizeX;
                    r4 = r1 + r3;
                    r4 = r4 + r2;
                    r7 = r13.bitmapX;
                    r13 = r13.bitmapWidth;
                    r9 = r7 + r13;
                    r9 = (float) r9;
                    r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1));
                    if (r4 <= 0) goto L_0x02d7;
                L_0x02d2:
                    r7 = r7 + r13;
                    r13 = (float) r7;
                    r13 = r13 - r1;
                    r2 = r13 - r3;
                L_0x02d7:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.freeform;
                    if (r1 != 0) goto L_0x0301;
                L_0x02dd:
                    r1 = r13.rectY;
                    r3 = r13.rectSizeX;
                    r4 = r1 + r3;
                    r4 = r4 + r2;
                    r6 = r13.bitmapY;
                    r13 = r13.bitmapHeight;
                    r7 = r6 + r13;
                    r7 = (float) r7;
                    r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1));
                    if (r4 <= 0) goto L_0x02f4;
                L_0x02ef:
                    r6 = r6 + r13;
                    r13 = (float) r6;
                    r13 = r13 - r1;
                    r2 = r13 - r3;
                L_0x02f4:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.rectSizeX;
                    r1 = r1 + r2;
                    r13.rectSizeX = r1;
                    r1 = r13.rectSizeY;
                    r1 = r1 + r2;
                    r13.rectSizeY = r1;
                    goto L_0x0324;
                L_0x0301:
                    r1 = r13.rectY;
                    r3 = r13.rectSizeY;
                    r4 = r1 + r3;
                    r4 = r4 + r6;
                    r7 = r13.bitmapY;
                    r13 = r13.bitmapHeight;
                    r9 = r7 + r13;
                    r9 = (float) r9;
                    r4 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1));
                    if (r4 <= 0) goto L_0x0318;
                L_0x0313:
                    r7 = r7 + r13;
                    r13 = (float) r7;
                    r13 = r13 - r1;
                    r6 = r13 - r3;
                L_0x0318:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.rectSizeX;
                    r1 = r1 + r2;
                    r13.rectSizeX = r1;
                    r1 = r13.rectSizeY;
                    r1 = r1 + r6;
                    r13.rectSizeY = r1;
                L_0x0324:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.rectSizeX;
                    r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
                    if (r1 >= 0) goto L_0x032e;
                L_0x032c:
                    r13.rectSizeX = r5;
                L_0x032e:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r1 = r13.rectSizeY;
                    r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
                    if (r1 >= 0) goto L_0x0338;
                L_0x0336:
                    r13.rectSizeY = r5;
                L_0x0338:
                    r13 = org.telegram.ui.PhotoCropActivity.PhotoCropView.this;
                    r13.oldX = r12;
                    r13.oldY = r0;
                    r13.invalidate();
                L_0x0341:
                    return r8;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoCropActivity$PhotoCropView$AnonymousClass1.onTouch(android.view.View, android.view.MotionEvent):boolean");
                }
            });
        }

        private void updateBitmapSize() {
            if (this.viewWidth != 0 && this.viewHeight != 0 && PhotoCropActivity.this.imageToCrop != null) {
                float f = this.rectX - ((float) this.bitmapX);
                int i = this.bitmapWidth;
                f /= (float) i;
                float f2 = this.rectY - ((float) this.bitmapY);
                int i2 = this.bitmapHeight;
                f2 /= (float) i2;
                float f3 = this.rectSizeX / ((float) i);
                float f4 = this.rectSizeY / ((float) i2);
                float width = (float) PhotoCropActivity.this.imageToCrop.getWidth();
                float height = (float) PhotoCropActivity.this.imageToCrop.getHeight();
                int i3 = this.viewWidth;
                float f5 = ((float) i3) / width;
                int i4 = this.viewHeight;
                float f6 = ((float) i4) / height;
                if (f5 > f6) {
                    this.bitmapHeight = i4;
                    this.bitmapWidth = (int) Math.ceil((double) (width * f6));
                } else {
                    this.bitmapWidth = i3;
                    this.bitmapHeight = (int) Math.ceil((double) (height * f5));
                }
                this.bitmapX = ((this.viewWidth - this.bitmapWidth) / 2) + AndroidUtilities.dp(14.0f);
                this.bitmapY = ((this.viewHeight - this.bitmapHeight) / 2) + AndroidUtilities.dp(14.0f);
                int i5;
                if (this.rectX != -1.0f || this.rectY != -1.0f) {
                    i2 = this.bitmapWidth;
                    this.rectX = (f * ((float) i2)) + ((float) this.bitmapX);
                    i5 = this.bitmapHeight;
                    this.rectY = (f2 * ((float) i5)) + ((float) this.bitmapY);
                    this.rectSizeX = f3 * ((float) i2);
                    this.rectSizeY = f4 * ((float) i5);
                } else if (this.freeform) {
                    this.rectY = (float) this.bitmapY;
                    this.rectX = (float) this.bitmapX;
                    this.rectSizeX = (float) this.bitmapWidth;
                    this.rectSizeY = (float) this.bitmapHeight;
                } else {
                    i5 = this.bitmapWidth;
                    i = this.bitmapHeight;
                    if (i5 > i) {
                        this.rectY = (float) this.bitmapY;
                        this.rectX = (float) (((this.viewWidth - i) / 2) + AndroidUtilities.dp(14.0f));
                        i5 = this.bitmapHeight;
                        this.rectSizeX = (float) i5;
                        this.rectSizeY = (float) i5;
                    } else {
                        this.rectX = (float) this.bitmapX;
                        this.rectY = (float) (((this.viewHeight - i5) / 2) + AndroidUtilities.dp(14.0f));
                        i5 = this.bitmapWidth;
                        this.rectSizeX = (float) i5;
                        this.rectSizeY = (float) i5;
                    }
                }
                invalidate();
            }
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            this.viewWidth = (i3 - i) - AndroidUtilities.dp(28.0f);
            this.viewHeight = (i4 - i2) - AndroidUtilities.dp(28.0f);
            updateBitmapSize();
        }

        public Bitmap getBitmap() {
            float f = this.rectX - ((float) this.bitmapX);
            int i = this.bitmapWidth;
            float f2 = this.rectSizeX / ((float) i);
            float f3 = this.rectSizeY / ((float) i);
            int width = (int) ((f / ((float) i)) * ((float) PhotoCropActivity.this.imageToCrop.getWidth()));
            i = (int) (((this.rectY - ((float) this.bitmapY)) / ((float) this.bitmapHeight)) * ((float) PhotoCropActivity.this.imageToCrop.getHeight()));
            int width2 = (int) (f2 * ((float) PhotoCropActivity.this.imageToCrop.getWidth()));
            int width3 = (int) (f3 * ((float) PhotoCropActivity.this.imageToCrop.getWidth()));
            if (width < 0) {
                width = 0;
            }
            if (i < 0) {
                i = 0;
            }
            if (width + width2 > PhotoCropActivity.this.imageToCrop.getWidth()) {
                width2 = PhotoCropActivity.this.imageToCrop.getWidth() - width;
            }
            if (i + width3 > PhotoCropActivity.this.imageToCrop.getHeight()) {
                width3 = PhotoCropActivity.this.imageToCrop.getHeight() - i;
            }
            try {
                width = Bitmaps.createBitmap(PhotoCropActivity.this.imageToCrop, width, i, width2, width3);
                return width;
            } catch (Throwable th) {
                FileLog.e(th);
                return null;
            }
        }

        /* Access modifiers changed, original: protected */
        /* JADX WARNING: Removed duplicated region for block: B:14:0x01b8 A:{LOOP_END, LOOP:0: B:13:0x01b6->B:14:0x01b8} */
        public void onDraw(android.graphics.Canvas r16) {
            /*
            r15 = this;
            r1 = r15;
            r0 = org.telegram.ui.PhotoCropActivity.this;
            r0 = r0.drawable;
            if (r0 == 0) goto L_0x0035;
        L_0x0009:
            r0 = org.telegram.ui.PhotoCropActivity.this;	 Catch:{ Throwable -> 0x002e }
            r0 = r0.drawable;	 Catch:{ Throwable -> 0x002e }
            r2 = r1.bitmapX;	 Catch:{ Throwable -> 0x002e }
            r3 = r1.bitmapY;	 Catch:{ Throwable -> 0x002e }
            r4 = r1.bitmapX;	 Catch:{ Throwable -> 0x002e }
            r5 = r1.bitmapWidth;	 Catch:{ Throwable -> 0x002e }
            r4 = r4 + r5;
            r5 = r1.bitmapY;	 Catch:{ Throwable -> 0x002e }
            r6 = r1.bitmapHeight;	 Catch:{ Throwable -> 0x002e }
            r5 = r5 + r6;
            r0.setBounds(r2, r3, r4, r5);	 Catch:{ Throwable -> 0x002e }
            r0 = org.telegram.ui.PhotoCropActivity.this;	 Catch:{ Throwable -> 0x002e }
            r0 = r0.drawable;	 Catch:{ Throwable -> 0x002e }
            r8 = r16;
            r0.draw(r8);	 Catch:{ Throwable -> 0x002c }
            goto L_0x0037;
        L_0x002c:
            r0 = move-exception;
            goto L_0x0031;
        L_0x002e:
            r0 = move-exception;
            r8 = r16;
        L_0x0031:
            org.telegram.messenger.FileLog.e(r0);
            goto L_0x0037;
        L_0x0035:
            r8 = r16;
        L_0x0037:
            r0 = r1.bitmapX;
            r3 = (float) r0;
            r2 = r1.bitmapY;
            r4 = (float) r2;
            r2 = r1.bitmapWidth;
            r0 = r0 + r2;
            r5 = (float) r0;
            r6 = r1.rectY;
            r7 = r1.halfPaint;
            r2 = r16;
            r2.drawRect(r3, r4, r5, r6, r7);
            r0 = r1.bitmapX;
            r3 = (float) r0;
            r4 = r1.rectY;
            r5 = r1.rectX;
            r0 = r1.rectSizeY;
            r6 = r4 + r0;
            r7 = r1.halfPaint;
            r2.drawRect(r3, r4, r5, r6, r7);
            r0 = r1.rectX;
            r2 = r1.rectSizeX;
            r3 = r0 + r2;
            r4 = r1.rectY;
            r0 = r1.bitmapX;
            r2 = r1.bitmapWidth;
            r0 = r0 + r2;
            r5 = (float) r0;
            r0 = r1.rectSizeY;
            r6 = r4 + r0;
            r7 = r1.halfPaint;
            r2 = r16;
            r2.drawRect(r3, r4, r5, r6, r7);
            r0 = r1.bitmapX;
            r3 = (float) r0;
            r2 = r1.rectY;
            r4 = r1.rectSizeY;
            r4 = r4 + r2;
            r2 = r1.bitmapWidth;
            r0 = r0 + r2;
            r5 = (float) r0;
            r0 = r1.bitmapY;
            r2 = r1.bitmapHeight;
            r0 = r0 + r2;
            r6 = (float) r0;
            r7 = r1.halfPaint;
            r2 = r16;
            r2.drawRect(r3, r4, r5, r6, r7);
            r3 = r1.rectX;
            r4 = r1.rectY;
            r0 = r1.rectSizeX;
            r5 = r3 + r0;
            r0 = r1.rectSizeY;
            r6 = r4 + r0;
            r7 = r1.rectPaint;
            r2.drawRect(r3, r4, r5, r6, r7);
            r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r2 = r1.rectX;
            r9 = (float) r0;
            r3 = r2 + r9;
            r4 = r1.rectY;
            r4 = r4 + r9;
            r2 = r2 + r9;
            r10 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
            r5 = org.telegram.messenger.AndroidUtilities.dp(r10);
            r5 = (float) r5;
            r5 = r5 + r2;
            r2 = r1.rectY;
            r11 = 3;
            r0 = r0 * 3;
            r0 = (float) r0;
            r6 = r2 + r0;
            r7 = r1.circlePaint;
            r2 = r16;
            r2.drawRect(r3, r4, r5, r6, r7);
            r2 = r1.rectX;
            r3 = r2 + r9;
            r4 = r1.rectY;
            r5 = r4 + r9;
            r6 = r2 + r0;
            r4 = r4 + r9;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r10);
            r2 = (float) r2;
            r7 = r4 + r2;
            r12 = r1.circlePaint;
            r2 = r16;
            r4 = r5;
            r5 = r6;
            r6 = r7;
            r7 = r12;
            r2.drawRect(r3, r4, r5, r6, r7);
            r2 = r1.rectX;
            r3 = r1.rectSizeX;
            r2 = r2 + r3;
            r2 = r2 - r9;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r10);
            r3 = (float) r3;
            r3 = r2 - r3;
            r2 = r1.rectY;
            r4 = r2 + r9;
            r5 = r1.rectX;
            r6 = r1.rectSizeX;
            r5 = r5 + r6;
            r5 = r5 - r9;
            r6 = r2 + r0;
            r7 = r1.circlePaint;
            r2 = r16;
            r2.drawRect(r3, r4, r5, r6, r7);
            r2 = r1.rectX;
            r3 = r1.rectSizeX;
            r4 = r2 + r3;
            r4 = r4 - r0;
            r5 = r1.rectY;
            r6 = r5 + r9;
            r2 = r2 + r3;
            r7 = r2 - r9;
            r5 = r5 + r9;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r10);
            r2 = (float) r2;
            r12 = r5 + r2;
            r13 = r1.circlePaint;
            r2 = r16;
            r3 = r4;
            r4 = r6;
            r5 = r7;
            r6 = r12;
            r7 = r13;
            r2.drawRect(r3, r4, r5, r6, r7);
            r2 = r1.rectX;
            r3 = r2 + r9;
            r2 = r1.rectY;
            r4 = r1.rectSizeY;
            r2 = r2 + r4;
            r2 = r2 - r9;
            r4 = org.telegram.messenger.AndroidUtilities.dp(r10);
            r4 = (float) r4;
            r4 = r2 - r4;
            r2 = r1.rectX;
            r5 = r2 + r0;
            r2 = r1.rectY;
            r6 = r1.rectSizeY;
            r2 = r2 + r6;
            r6 = r2 - r9;
            r7 = r1.circlePaint;
            r2 = r16;
            r2.drawRect(r3, r4, r5, r6, r7);
            r2 = r1.rectX;
            r3 = r2 + r9;
            r4 = r1.rectY;
            r5 = r1.rectSizeY;
            r4 = r4 + r5;
            r4 = r4 - r0;
            r2 = r2 + r9;
            r5 = org.telegram.messenger.AndroidUtilities.dp(r10);
            r5 = (float) r5;
            r5 = r5 + r2;
            r2 = r1.rectY;
            r6 = r1.rectSizeY;
            r2 = r2 + r6;
            r6 = r2 - r9;
            r7 = r1.circlePaint;
            r2 = r16;
            r2.drawRect(r3, r4, r5, r6, r7);
            r2 = r1.rectX;
            r3 = r1.rectSizeX;
            r2 = r2 + r3;
            r2 = r2 - r9;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r10);
            r3 = (float) r3;
            r3 = r2 - r3;
            r2 = r1.rectY;
            r4 = r1.rectSizeY;
            r5 = r2 + r4;
            r5 = r5 - r0;
            r6 = r1.rectX;
            r7 = r1.rectSizeX;
            r6 = r6 + r7;
            r6 = r6 - r9;
            r2 = r2 + r4;
            r7 = r2 - r9;
            r12 = r1.circlePaint;
            r2 = r16;
            r4 = r5;
            r5 = r6;
            r6 = r7;
            r7 = r12;
            r2.drawRect(r3, r4, r5, r6, r7);
            r2 = r1.rectX;
            r3 = r1.rectSizeX;
            r2 = r2 + r3;
            r3 = r2 - r0;
            r0 = r1.rectY;
            r2 = r1.rectSizeY;
            r0 = r0 + r2;
            r0 = r0 - r9;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r10);
            r2 = (float) r2;
            r4 = r0 - r2;
            r0 = r1.rectX;
            r2 = r1.rectSizeX;
            r0 = r0 + r2;
            r5 = r0 - r9;
            r0 = r1.rectY;
            r2 = r1.rectSizeY;
            r0 = r0 + r2;
            r6 = r0 - r9;
            r7 = r1.circlePaint;
            r2 = r16;
            r2.drawRect(r3, r4, r5, r6, r7);
            r0 = 1;
        L_0x01b6:
            if (r0 >= r11) goto L_0x0204;
        L_0x01b8:
            r2 = r1.rectX;
            r3 = r1.rectSizeX;
            r10 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
            r4 = r3 / r10;
            r12 = (float) r0;
            r4 = r4 * r12;
            r4 = r4 + r2;
            r5 = r1.rectY;
            r6 = r5 + r9;
            r2 = r2 + r9;
            r3 = r3 / r10;
            r3 = r3 * r12;
            r7 = r2 + r3;
            r2 = r1.rectSizeY;
            r5 = r5 + r2;
            r13 = r5 - r9;
            r14 = r1.circlePaint;
            r2 = r16;
            r3 = r4;
            r4 = r6;
            r5 = r7;
            r6 = r13;
            r7 = r14;
            r2.drawRect(r3, r4, r5, r6, r7);
            r2 = r1.rectX;
            r3 = r2 + r9;
            r4 = r1.rectY;
            r5 = r1.rectSizeY;
            r6 = r5 / r10;
            r6 = r6 * r12;
            r6 = r6 + r4;
            r2 = r2 - r9;
            r7 = r1.rectSizeX;
            r7 = r7 + r2;
            r5 = r5 / r10;
            r5 = r5 * r12;
            r4 = r4 + r5;
            r10 = r4 + r9;
            r12 = r1.circlePaint;
            r2 = r16;
            r4 = r6;
            r5 = r7;
            r6 = r10;
            r7 = r12;
            r2.drawRect(r3, r4, r5, r6, r7);
            r0 = r0 + 1;
            goto L_0x01b6;
        L_0x0204:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhotoCropActivity$PhotoCropView.onDraw(android.graphics.Canvas):void");
        }
    }

    public interface PhotoEditActivityDelegate {
        void didFinishEdit(Bitmap bitmap);
    }

    public PhotoCropActivity(Bundle bundle) {
        super(bundle);
    }

    public boolean onFragmentCreate() {
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
            int dp;
            if (AndroidUtilities.isTablet()) {
                dp = AndroidUtilities.dp(520.0f);
            } else {
                Point point = AndroidUtilities.displaySize;
                dp = Math.max(point.x, point.y);
            }
            float f = (float) dp;
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
        super.onFragmentDestroy();
        if (!(this.bitmapKey == null || !ImageLoader.getInstance().decrementUseCount(this.bitmapKey) || ImageLoader.getInstance().isInMemCache(this.bitmapKey, false))) {
            this.bitmapKey = null;
        }
        if (this.bitmapKey == null) {
            Bitmap bitmap = this.imageToCrop;
            if (!(bitmap == null || this.sameBitmap)) {
                bitmap.recycle();
                this.imageToCrop = null;
            }
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
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PhotoCropActivity.this.finishFragment();
                } else if (i == 1) {
                    if (!(PhotoCropActivity.this.delegate == null || PhotoCropActivity.this.doneButtonPressed)) {
                        Bitmap bitmap = PhotoCropActivity.this.view.getBitmap();
                        if (bitmap == PhotoCropActivity.this.imageToCrop) {
                            PhotoCropActivity.this.sameBitmap = true;
                        }
                        PhotoCropActivity.this.delegate.didFinishEdit(bitmap);
                        PhotoCropActivity.this.doneButtonPressed = true;
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
        this.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        return this.fragmentView;
    }

    public void setDelegate(PhotoEditActivityDelegate photoEditActivityDelegate) {
        this.delegate = photoEditActivityDelegate;
    }
}
