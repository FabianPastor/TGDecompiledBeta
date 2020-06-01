package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.ui.Components.Paint.Views.EditTextOutline;

public class PaintingOverlay extends FrameLayout {
    private Drawable backgroundDrawable;
    private boolean ignoreLayout;
    private ArrayList<VideoEditedInfo.MediaEntity> mediaEntities;
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
        if (str != null) {
            this.paintBitmap = BitmapFactory.decodeFile(str);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(this.paintBitmap);
            this.backgroundDrawable = bitmapDrawable;
            setBackground(bitmapDrawable);
        } else {
            this.paintBitmap = null;
            this.backgroundDrawable = null;
            setBackground((Drawable) null);
        }
        setEntities(arrayList, z, z2);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        this.ignoreLayout = true;
        setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
        if (this.mediaEntities != null) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            int size = this.mediaEntities.size();
            for (int i3 = 0; i3 < size; i3++) {
                VideoEditedInfo.MediaEntity mediaEntity = this.mediaEntities.get(i3);
                View view = mediaEntity.view;
                if (view != null) {
                    if (view instanceof EditTextOutline) {
                        view.measure(View.MeasureSpec.makeMeasureSpec(mediaEntity.viewWidth, NUM), View.MeasureSpec.makeMeasureSpec(mediaEntity.viewHeight, NUM));
                        float f = (mediaEntity.textViewWidth * ((float) measuredWidth)) / ((float) mediaEntity.viewWidth);
                        mediaEntity.view.setScaleX(mediaEntity.scale * f);
                        mediaEntity.view.setScaleY(mediaEntity.scale * f);
                    } else {
                        view.measure(View.MeasureSpec.makeMeasureSpec((int) (((float) measuredWidth) * mediaEntity.width), NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) measuredHeight) * mediaEntity.height), NUM));
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
        if (this.mediaEntities != null) {
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            int size = this.mediaEntities.size();
            for (int i7 = 0; i7 < size; i7++) {
                VideoEditedInfo.MediaEntity mediaEntity = this.mediaEntities.get(i7);
                View view = mediaEntity.view;
                if (view != null) {
                    if (view instanceof EditTextOutline) {
                        i5 = ((int) (((float) measuredWidth) * mediaEntity.textViewX)) - (view.getMeasuredWidth() / 2);
                        i6 = ((int) (((float) measuredHeight) * mediaEntity.textViewY)) - (mediaEntity.view.getMeasuredHeight() / 2);
                    } else {
                        i5 = (int) (((float) measuredWidth) * mediaEntity.x);
                        i6 = (int) (((float) measuredHeight) * mediaEntity.y);
                    }
                    View view2 = mediaEntity.view;
                    view2.layout(i5, i6, view2.getMeasuredWidth() + i5, mediaEntity.view.getMeasuredHeight() + i6);
                }
            }
        }
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

    public void setEntities(ArrayList<VideoEditedInfo.MediaEntity> arrayList, boolean z, boolean z2) {
        ArrayList<VideoEditedInfo.MediaEntity> arrayList2 = arrayList;
        this.mediaEntities = arrayList2;
        removeAllViews();
        if (arrayList2 != null && !arrayList.isEmpty()) {
            int size = this.mediaEntities.size();
            for (int i = 0; i < size; i++) {
                VideoEditedInfo.MediaEntity mediaEntity = this.mediaEntities.get(i);
                byte b = mediaEntity.type;
                if (b == 0) {
                    BackupImageView backupImageView = new BackupImageView(getContext());
                    backupImageView.setAspectFit(true);
                    ImageReceiver imageReceiver = backupImageView.getImageReceiver();
                    if (z) {
                        imageReceiver.setAllowDecodeSingleFrame(true);
                        imageReceiver.setAllowStartLottieAnimation(false);
                        if (z2) {
                            imageReceiver.setDelegate($$Lambda$PaintingOverlay$M1MbD6dSCe7rjuKTW4iqhjJx7E.INSTANCE);
                        }
                    }
                    imageReceiver.setImage(ImageLocation.getForDocument(mediaEntity.document), (String) null, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(mediaEntity.document.thumbs, 90), mediaEntity.document), (String) null, "webp", mediaEntity.parentObject, 1);
                    if ((mediaEntity.subType & 2) != 0) {
                        backupImageView.setScaleX(-1.0f);
                    }
                    mediaEntity.view = backupImageView;
                } else if (b == 1) {
                    AnonymousClass1 r5 = new EditTextOutline(this, getContext()) {
                        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                            return false;
                        }

                        public boolean onTouchEvent(MotionEvent motionEvent) {
                            return false;
                        }
                    };
                    r5.setBackgroundColor(0);
                    r5.setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f));
                    r5.setTextSize(0, (float) mediaEntity.fontSize);
                    r5.setText(mediaEntity.text);
                    r5.setTextColor(mediaEntity.color);
                    r5.setTypeface((Typeface) null, 1);
                    r5.setGravity(17);
                    r5.setHorizontallyScrolling(false);
                    r5.setImeOptions(NUM);
                    r5.setFocusableInTouchMode(true);
                    r5.setEnabled(false);
                    r5.setInputType(r5.getInputType() | 16384);
                    if (Build.VERSION.SDK_INT >= 23) {
                        r5.setBreakStrategy(0);
                    }
                    byte b2 = mediaEntity.subType;
                    if ((b2 & 1) != 0) {
                        r5.setTextColor(-1);
                        r5.setStrokeColor(mediaEntity.color);
                        r5.setFrameColor(0);
                        r5.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                    } else if ((b2 & 4) != 0) {
                        r5.setTextColor(-16777216);
                        r5.setStrokeColor(0);
                        r5.setFrameColor(mediaEntity.color);
                        r5.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                    } else {
                        r5.setTextColor(mediaEntity.color);
                        r5.setStrokeColor(0);
                        r5.setFrameColor(0);
                        r5.setShadowLayer(5.0f, 0.0f, 1.0f, NUM);
                    }
                    mediaEntity.view = r5;
                }
                addView(mediaEntity.view);
                View view = mediaEntity.view;
                double d = (double) (-mediaEntity.rotation);
                Double.isNaN(d);
                view.setRotation((float) ((d / 3.141592653589793d) * 180.0d));
            }
        }
    }

    static /* synthetic */ void lambda$setEntities$0(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
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
