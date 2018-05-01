package org.telegram.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

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
        int draggingState = null;
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

        /* renamed from: org.telegram.ui.PhotoCropActivity$PhotoCropView$1 */
        class C15831 implements OnTouchListener {
            C15831() {
            }

            public boolean onTouch(View view, MotionEvent motionEvent) {
                view = motionEvent.getX();
                float y = motionEvent.getY();
                int dp = AndroidUtilities.dp(14.0f);
                float f;
                if (motionEvent.getAction() == 0) {
                    f = (float) dp;
                    if (PhotoCropView.this.rectX - f < view && PhotoCropView.this.rectX + f > view && PhotoCropView.this.rectY - f < y && PhotoCropView.this.rectY + f > y) {
                        PhotoCropView.this.draggingState = 1;
                    } else if ((PhotoCropView.this.rectX - f) + PhotoCropView.this.rectSizeX < view && (PhotoCropView.this.rectX + f) + PhotoCropView.this.rectSizeX > view && PhotoCropView.this.rectY - f < y && PhotoCropView.this.rectY + f > y) {
                        PhotoCropView.this.draggingState = 2;
                    } else if (PhotoCropView.this.rectX - f < view && PhotoCropView.this.rectX + f > view && (PhotoCropView.this.rectY - f) + PhotoCropView.this.rectSizeY < y && (PhotoCropView.this.rectY + f) + PhotoCropView.this.rectSizeY > y) {
                        PhotoCropView.this.draggingState = 3;
                    } else if ((PhotoCropView.this.rectX - f) + PhotoCropView.this.rectSizeX < view && (PhotoCropView.this.rectX + f) + PhotoCropView.this.rectSizeX > view && (PhotoCropView.this.rectY - f) + PhotoCropView.this.rectSizeY < y && (PhotoCropView.this.rectY + f) + PhotoCropView.this.rectSizeY > y) {
                        PhotoCropView.this.draggingState = 4;
                    } else if (PhotoCropView.this.rectX >= view || PhotoCropView.this.rectX + PhotoCropView.this.rectSizeX <= view || PhotoCropView.this.rectY >= y || PhotoCropView.this.rectY + PhotoCropView.this.rectSizeY <= y) {
                        PhotoCropView.this.draggingState = 0;
                    } else {
                        PhotoCropView.this.draggingState = 5;
                    }
                    if (PhotoCropView.this.draggingState != null) {
                        PhotoCropView.this.requestDisallowInterceptTouchEvent(true);
                    }
                    PhotoCropView.this.oldX = view;
                    PhotoCropView.this.oldY = y;
                } else if (motionEvent.getAction() == 1) {
                    PhotoCropView.this.draggingState = 0;
                } else if (motionEvent.getAction() == 2 && PhotoCropView.this.draggingState != null) {
                    motionEvent = view - PhotoCropView.this.oldX;
                    f = y - PhotoCropView.this.oldY;
                    PhotoCropView photoCropView;
                    if (PhotoCropView.this.draggingState == 5) {
                        photoCropView = PhotoCropView.this;
                        photoCropView.rectX += motionEvent;
                        motionEvent = PhotoCropView.this;
                        motionEvent.rectY += f;
                        if (PhotoCropView.this.rectX < ((float) PhotoCropView.this.bitmapX)) {
                            PhotoCropView.this.rectX = (float) PhotoCropView.this.bitmapX;
                        } else if (PhotoCropView.this.rectX + PhotoCropView.this.rectSizeX > ((float) (PhotoCropView.this.bitmapX + PhotoCropView.this.bitmapWidth))) {
                            PhotoCropView.this.rectX = ((float) (PhotoCropView.this.bitmapX + PhotoCropView.this.bitmapWidth)) - PhotoCropView.this.rectSizeX;
                        }
                        if (PhotoCropView.this.rectY < ((float) PhotoCropView.this.bitmapY)) {
                            PhotoCropView.this.rectY = (float) PhotoCropView.this.bitmapY;
                        } else if (PhotoCropView.this.rectY + PhotoCropView.this.rectSizeY > ((float) (PhotoCropView.this.bitmapY + PhotoCropView.this.bitmapHeight))) {
                            PhotoCropView.this.rectY = ((float) (PhotoCropView.this.bitmapY + PhotoCropView.this.bitmapHeight)) - PhotoCropView.this.rectSizeY;
                        }
                    } else if (PhotoCropView.this.draggingState == 1) {
                        if (PhotoCropView.this.rectSizeX - motionEvent < 160.0f) {
                            motionEvent = PhotoCropView.this.rectSizeX - NUM;
                        }
                        if (PhotoCropView.this.rectX + motionEvent < ((float) PhotoCropView.this.bitmapX)) {
                            motionEvent = ((float) PhotoCropView.this.bitmapX) - PhotoCropView.this.rectX;
                        }
                        if (PhotoCropView.this.freeform) {
                            if (PhotoCropView.this.rectSizeY - f < 160.0f) {
                                f = PhotoCropView.this.rectSizeY - 160.0f;
                            }
                            if (PhotoCropView.this.rectY + f < ((float) PhotoCropView.this.bitmapY)) {
                                f = ((float) PhotoCropView.this.bitmapY) - PhotoCropView.this.rectY;
                            }
                            photoCropView = PhotoCropView.this;
                            photoCropView.rectX += motionEvent;
                            photoCropView = PhotoCropView.this;
                            photoCropView.rectY += f;
                            photoCropView = PhotoCropView.this;
                            photoCropView.rectSizeX -= motionEvent;
                            motionEvent = PhotoCropView.this;
                            motionEvent.rectSizeY -= f;
                        } else {
                            if (PhotoCropView.this.rectY + motionEvent < ((float) PhotoCropView.this.bitmapY)) {
                                motionEvent = ((float) PhotoCropView.this.bitmapY) - PhotoCropView.this.rectY;
                            }
                            r1 = PhotoCropView.this;
                            r1.rectX += motionEvent;
                            r1 = PhotoCropView.this;
                            r1.rectY += motionEvent;
                            r1 = PhotoCropView.this;
                            r1.rectSizeX -= motionEvent;
                            r1 = PhotoCropView.this;
                            r1.rectSizeY -= motionEvent;
                        }
                    } else if (PhotoCropView.this.draggingState == 2) {
                        if (PhotoCropView.this.rectSizeX + motionEvent < 160.0f) {
                            motionEvent = -(PhotoCropView.this.rectSizeX - NUM);
                        }
                        if ((PhotoCropView.this.rectX + PhotoCropView.this.rectSizeX) + motionEvent > ((float) (PhotoCropView.this.bitmapX + PhotoCropView.this.bitmapWidth))) {
                            motionEvent = (((float) (PhotoCropView.this.bitmapX + PhotoCropView.this.bitmapWidth)) - PhotoCropView.this.rectX) - PhotoCropView.this.rectSizeX;
                        }
                        if (PhotoCropView.this.freeform) {
                            if (PhotoCropView.this.rectSizeY - f < 160.0f) {
                                f = PhotoCropView.this.rectSizeY - 160.0f;
                            }
                            if (PhotoCropView.this.rectY + f < ((float) PhotoCropView.this.bitmapY)) {
                                f = ((float) PhotoCropView.this.bitmapY) - PhotoCropView.this.rectY;
                            }
                            photoCropView = PhotoCropView.this;
                            photoCropView.rectY += f;
                            photoCropView = PhotoCropView.this;
                            photoCropView.rectSizeX += motionEvent;
                            motionEvent = PhotoCropView.this;
                            motionEvent.rectSizeY -= f;
                        } else {
                            if (PhotoCropView.this.rectY - motionEvent < ((float) PhotoCropView.this.bitmapY)) {
                                motionEvent = PhotoCropView.this.rectY - ((float) PhotoCropView.this.bitmapY);
                            }
                            r1 = PhotoCropView.this;
                            r1.rectY -= motionEvent;
                            r1 = PhotoCropView.this;
                            r1.rectSizeX += motionEvent;
                            r1 = PhotoCropView.this;
                            r1.rectSizeY += motionEvent;
                        }
                    } else if (PhotoCropView.this.draggingState == 3) {
                        if (PhotoCropView.this.rectSizeX - motionEvent < 160.0f) {
                            motionEvent = PhotoCropView.this.rectSizeX - NUM;
                        }
                        if (PhotoCropView.this.rectX + motionEvent < ((float) PhotoCropView.this.bitmapX)) {
                            motionEvent = ((float) PhotoCropView.this.bitmapX) - PhotoCropView.this.rectX;
                        }
                        if (PhotoCropView.this.freeform) {
                            if ((PhotoCropView.this.rectY + PhotoCropView.this.rectSizeY) + f > ((float) (PhotoCropView.this.bitmapY + PhotoCropView.this.bitmapHeight))) {
                                f = (((float) (PhotoCropView.this.bitmapY + PhotoCropView.this.bitmapHeight)) - PhotoCropView.this.rectY) - PhotoCropView.this.rectSizeY;
                            }
                            photoCropView = PhotoCropView.this;
                            photoCropView.rectX += motionEvent;
                            photoCropView = PhotoCropView.this;
                            photoCropView.rectSizeX -= motionEvent;
                            motionEvent = PhotoCropView.this;
                            motionEvent.rectSizeY += f;
                            if (PhotoCropView.this.rectSizeY < NUM) {
                                PhotoCropView.this.rectSizeY = 160.0f;
                            }
                        } else {
                            if ((PhotoCropView.this.rectY + PhotoCropView.this.rectSizeX) - motionEvent > ((float) (PhotoCropView.this.bitmapY + PhotoCropView.this.bitmapHeight))) {
                                motionEvent = ((PhotoCropView.this.rectY + PhotoCropView.this.rectSizeX) - ((float) PhotoCropView.this.bitmapY)) - ((float) PhotoCropView.this.bitmapHeight);
                            }
                            r1 = PhotoCropView.this;
                            r1.rectX += motionEvent;
                            r1 = PhotoCropView.this;
                            r1.rectSizeX -= motionEvent;
                            r1 = PhotoCropView.this;
                            r1.rectSizeY -= motionEvent;
                        }
                    } else if (PhotoCropView.this.draggingState == 4) {
                        if ((PhotoCropView.this.rectX + PhotoCropView.this.rectSizeX) + motionEvent > ((float) (PhotoCropView.this.bitmapX + PhotoCropView.this.bitmapWidth))) {
                            motionEvent = (((float) (PhotoCropView.this.bitmapX + PhotoCropView.this.bitmapWidth)) - PhotoCropView.this.rectX) - PhotoCropView.this.rectSizeX;
                        }
                        if (PhotoCropView.this.freeform) {
                            if ((PhotoCropView.this.rectY + PhotoCropView.this.rectSizeY) + f > ((float) (PhotoCropView.this.bitmapY + PhotoCropView.this.bitmapHeight))) {
                                f = (((float) (PhotoCropView.this.bitmapY + PhotoCropView.this.bitmapHeight)) - PhotoCropView.this.rectY) - PhotoCropView.this.rectSizeY;
                            }
                            photoCropView = PhotoCropView.this;
                            photoCropView.rectSizeX += motionEvent;
                            motionEvent = PhotoCropView.this;
                            motionEvent.rectSizeY += f;
                        } else {
                            if ((PhotoCropView.this.rectY + PhotoCropView.this.rectSizeX) + motionEvent > ((float) (PhotoCropView.this.bitmapY + PhotoCropView.this.bitmapHeight))) {
                                motionEvent = (((float) (PhotoCropView.this.bitmapY + PhotoCropView.this.bitmapHeight)) - PhotoCropView.this.rectY) - PhotoCropView.this.rectSizeX;
                            }
                            r1 = PhotoCropView.this;
                            r1.rectSizeX += motionEvent;
                            r1 = PhotoCropView.this;
                            r1.rectSizeY += motionEvent;
                        }
                        if (PhotoCropView.this.rectSizeX < NUM) {
                            PhotoCropView.this.rectSizeX = 160.0f;
                        }
                        if (PhotoCropView.this.rectSizeY < NUM) {
                            PhotoCropView.this.rectSizeY = 160.0f;
                        }
                    }
                    PhotoCropView.this.oldX = view;
                    PhotoCropView.this.oldY = y;
                    PhotoCropView.this.invalidate();
                }
                return true;
            }
        }

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
            this.halfPaint.setColor(-939524096);
            setBackgroundColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
            setOnTouchListener(new C15831());
        }

        private void updateBitmapSize() {
            if (!(this.viewWidth == 0 || this.viewHeight == 0)) {
                if (PhotoCropActivity.this.imageToCrop != null) {
                    float f = (this.rectX - ((float) this.bitmapX)) / ((float) this.bitmapWidth);
                    float f2 = (this.rectY - ((float) this.bitmapY)) / ((float) this.bitmapHeight);
                    float f3 = this.rectSizeX / ((float) this.bitmapWidth);
                    float f4 = this.rectSizeY / ((float) this.bitmapHeight);
                    float width = (float) PhotoCropActivity.this.imageToCrop.getWidth();
                    float height = (float) PhotoCropActivity.this.imageToCrop.getHeight();
                    float f5 = ((float) this.viewWidth) / width;
                    float f6 = ((float) this.viewHeight) / height;
                    if (f5 > f6) {
                        this.bitmapHeight = this.viewHeight;
                        this.bitmapWidth = (int) Math.ceil((double) (width * f6));
                    } else {
                        this.bitmapWidth = this.viewWidth;
                        this.bitmapHeight = (int) Math.ceil((double) (height * f5));
                    }
                    this.bitmapX = ((this.viewWidth - this.bitmapWidth) / 2) + AndroidUtilities.dp(14.0f);
                    this.bitmapY = ((this.viewHeight - this.bitmapHeight) / 2) + AndroidUtilities.dp(14.0f);
                    if (this.rectX != -1.0f || this.rectY != -1.0f) {
                        this.rectX = (f * ((float) this.bitmapWidth)) + ((float) this.bitmapX);
                        this.rectY = (f2 * ((float) this.bitmapHeight)) + ((float) this.bitmapY);
                        this.rectSizeX = f3 * ((float) this.bitmapWidth);
                        this.rectSizeY = f4 * ((float) this.bitmapHeight);
                    } else if (this.freeform) {
                        this.rectY = (float) this.bitmapY;
                        this.rectX = (float) this.bitmapX;
                        this.rectSizeX = (float) this.bitmapWidth;
                        this.rectSizeY = (float) this.bitmapHeight;
                    } else if (this.bitmapWidth > this.bitmapHeight) {
                        this.rectY = (float) this.bitmapY;
                        this.rectX = (float) (((this.viewWidth - this.bitmapHeight) / 2) + AndroidUtilities.dp(14.0f));
                        this.rectSizeX = (float) this.bitmapHeight;
                        this.rectSizeY = (float) this.bitmapHeight;
                    } else {
                        this.rectX = (float) this.bitmapX;
                        this.rectY = (float) (((this.viewHeight - this.bitmapWidth) / 2) + AndroidUtilities.dp(14.0f));
                        this.rectSizeX = (float) this.bitmapWidth;
                        this.rectSizeY = (float) this.bitmapWidth;
                    }
                    invalidate();
                }
            }
        }

        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            this.viewWidth = (i3 - i) - AndroidUtilities.dp(28.0f);
            this.viewHeight = (i4 - i2) - AndroidUtilities.dp(28.0f);
            updateBitmapSize();
        }

        public Bitmap getBitmap() {
            int width = (int) (((this.rectX - ((float) this.bitmapX)) / ((float) this.bitmapWidth)) * ((float) PhotoCropActivity.this.imageToCrop.getWidth()));
            int height = (int) (((this.rectY - ((float) this.bitmapY)) / ((float) this.bitmapHeight)) * ((float) PhotoCropActivity.this.imageToCrop.getHeight()));
            int width2 = (int) ((this.rectSizeX / ((float) this.bitmapWidth)) * ((float) PhotoCropActivity.this.imageToCrop.getWidth()));
            int width3 = (int) ((this.rectSizeY / ((float) this.bitmapWidth)) * ((float) PhotoCropActivity.this.imageToCrop.getWidth()));
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
                FileLog.m3e(th);
                return null;
            }
        }

        protected void onDraw(Canvas canvas) {
            if (PhotoCropActivity.this.drawable != null) {
                try {
                    PhotoCropActivity.this.drawable.setBounds(this.bitmapX, this.bitmapY, this.bitmapX + this.bitmapWidth, this.bitmapY + this.bitmapHeight);
                    PhotoCropActivity.this.drawable.draw(canvas);
                } catch (Throwable th) {
                    FileLog.m3e(th);
                }
            }
            canvas.drawRect((float) this.bitmapX, (float) this.bitmapY, (float) (this.bitmapX + this.bitmapWidth), this.rectY, this.halfPaint);
            canvas.drawRect((float) this.bitmapX, this.rectY, this.rectX, this.rectY + this.rectSizeY, this.halfPaint);
            canvas.drawRect(this.rectX + this.rectSizeX, this.rectY, (float) (this.bitmapX + this.bitmapWidth), this.rectY + this.rectSizeY, this.halfPaint);
            canvas.drawRect((float) this.bitmapX, this.rectY + this.rectSizeY, (float) (this.bitmapX + this.bitmapWidth), (float) (this.bitmapY + this.bitmapHeight), this.halfPaint);
            canvas.drawRect(this.rectX, this.rectY, this.rectX + this.rectSizeX, this.rectY + this.rectSizeY, this.rectPaint);
            int dp = AndroidUtilities.dp(1.0f);
            float f = (float) dp;
            float f2 = (float) (dp * 3);
            Canvas canvas2 = canvas;
            canvas2.drawRect(this.rectX + f, this.rectY + f, ((float) AndroidUtilities.dp(20.0f)) + (this.rectX + f), this.rectY + f2, this.circlePaint);
            canvas2 = canvas;
            canvas2.drawRect(this.rectX + f, this.rectY + f, this.rectX + f2, ((float) AndroidUtilities.dp(20.0f)) + (this.rectY + f), this.circlePaint);
            canvas.drawRect(((this.rectX + this.rectSizeX) - f) - ((float) AndroidUtilities.dp(20.0f)), this.rectY + f, (this.rectX + this.rectSizeX) - f, this.rectY + f2, this.circlePaint);
            canvas2 = canvas;
            canvas2.drawRect((this.rectX + this.rectSizeX) - f2, this.rectY + f, (this.rectX + this.rectSizeX) - f, ((float) AndroidUtilities.dp(20.0f)) + (this.rectY + f), this.circlePaint);
            canvas.drawRect(this.rectX + f, ((this.rectY + this.rectSizeY) - f) - ((float) AndroidUtilities.dp(20.0f)), this.rectX + f2, (this.rectY + this.rectSizeY) - f, this.circlePaint);
            canvas2 = canvas;
            canvas2.drawRect(this.rectX + f, (this.rectY + this.rectSizeY) - f2, ((float) AndroidUtilities.dp(20.0f)) + (this.rectX + f), (this.rectY + this.rectSizeY) - f, this.circlePaint);
            canvas.drawRect(((this.rectX + this.rectSizeX) - f) - ((float) AndroidUtilities.dp(20.0f)), (this.rectY + this.rectSizeY) - f2, (this.rectX + this.rectSizeX) - f, (this.rectY + this.rectSizeY) - f, this.circlePaint);
            canvas.drawRect((this.rectX + this.rectSizeX) - f2, ((this.rectY + this.rectSizeY) - f) - ((float) AndroidUtilities.dp(20.0f)), (this.rectX + this.rectSizeX) - f, (this.rectY + this.rectSizeY) - f, this.circlePaint);
            for (dp = 1; dp < 3; dp++) {
                float f3 = (float) dp;
                canvas2 = canvas;
                canvas2.drawRect(((this.rectSizeX / 3.0f) * f3) + this.rectX, this.rectY + f, ((this.rectSizeX / 3.0f) * f3) + (this.rectX + f), (this.rectY + this.rectSizeY) - f, this.circlePaint);
                canvas2 = canvas;
                canvas2.drawRect(this.rectX + f, ((this.rectSizeY / 3.0f) * f3) + this.rectY, this.rectSizeX + (this.rectX - f), (this.rectY + ((this.rectSizeY / 3.0f) * f3)) + f, this.circlePaint);
            }
        }
    }

    public interface PhotoEditActivityDelegate {
        void didFinishEdit(Bitmap bitmap);
    }

    /* renamed from: org.telegram.ui.PhotoCropActivity$1 */
    class C22261 extends ActionBarMenuOnItemClick {
        C22261() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                PhotoCropActivity.this.finishFragment();
            } else if (i == 1) {
                if (PhotoCropActivity.this.delegate != 0 && PhotoCropActivity.this.doneButtonPressed == 0) {
                    i = PhotoCropActivity.this.view.getBitmap();
                    if (i == PhotoCropActivity.this.imageToCrop) {
                        PhotoCropActivity.this.sameBitmap = true;
                    }
                    PhotoCropActivity.this.delegate.didFinishEdit(i);
                    PhotoCropActivity.this.doneButtonPressed = true;
                }
                PhotoCropActivity.this.finishFragment();
            }
        }
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
                dp = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
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
        if (!(this.bitmapKey == null || !ImageLoader.getInstance().decrementUseCount(this.bitmapKey) || ImageLoader.getInstance().isInCache(this.bitmapKey))) {
            this.bitmapKey = null;
        }
        if (!(this.bitmapKey != null || this.imageToCrop == null || this.sameBitmap)) {
            this.imageToCrop.recycle();
            this.imageToCrop = null;
        }
        this.drawable = null;
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, false);
        this.actionBar.setTitleColor(-1);
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("CropImage", C0446R.string.CropImage));
        this.actionBar.setActionBarMenuOnItemClick(new C22261());
        this.actionBar.createMenu().addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        View photoCropView = new PhotoCropView(context);
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
