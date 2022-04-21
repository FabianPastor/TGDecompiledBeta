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

    public PaintingOverlay(Context context) {
        super(context);
    }

    public void setData(String paintPath, ArrayList<VideoEditedInfo.MediaEntity> entities, boolean isVideo, boolean startAfterSet) {
        if (paintPath != null) {
            this.paintBitmap = BitmapFactory.decodeFile(paintPath);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(this.paintBitmap);
            this.backgroundDrawable = bitmapDrawable;
            setBackground(bitmapDrawable);
        } else {
            this.paintBitmap = null;
            this.backgroundDrawable = null;
            setBackground((Drawable) null);
        }
        setEntities(entities, isVideo, startAfterSet);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.ignoreLayout = true;
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
        if (this.mediaEntities != null) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            int N = this.mediaEntities.size();
            for (int a = 0; a < N; a++) {
                VideoEditedInfo.MediaEntity entity = this.mediaEntities.get(a);
                if (entity.view != null) {
                    if (entity.view instanceof EditTextOutline) {
                        entity.view.measure(View.MeasureSpec.makeMeasureSpec(entity.viewWidth, NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
                        float sc = (entity.textViewWidth * ((float) width)) / ((float) entity.viewWidth);
                        entity.view.setScaleX(entity.scale * sc);
                        entity.view.setScaleY(entity.scale * sc);
                    } else {
                        entity.view.measure(View.MeasureSpec.makeMeasureSpec((int) (((float) width) * entity.width), NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) height) * entity.height), NUM));
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
        if (this.mediaEntities != null) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            int N = this.mediaEntities.size();
            for (int a = 0; a < N; a++) {
                VideoEditedInfo.MediaEntity entity = this.mediaEntities.get(a);
                if (entity.view != null) {
                    if (entity.view instanceof EditTextOutline) {
                        x = ((int) (((float) width) * entity.textViewX)) - (entity.view.getMeasuredWidth() / 2);
                        y = ((int) (((float) height) * entity.textViewY)) - (entity.view.getMeasuredHeight() / 2);
                    } else {
                        x = (int) (((float) width) * entity.x);
                        y = (int) (((float) height) * entity.y);
                    }
                    entity.view.layout(x, y, entity.view.getMeasuredWidth() + x, entity.view.getMeasuredHeight() + y);
                }
            }
        }
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

    public void setEntities(ArrayList<VideoEditedInfo.MediaEntity> entities, boolean isVideo, boolean startAfterSet) {
        ArrayList<VideoEditedInfo.MediaEntity> arrayList = entities;
        this.mediaEntities = arrayList;
        removeAllViews();
        if (arrayList != null && !entities.isEmpty()) {
            int N = this.mediaEntities.size();
            for (int a = 0; a < N; a++) {
                VideoEditedInfo.MediaEntity entity = this.mediaEntities.get(a);
                if (entity.type == 0) {
                    BackupImageView imageView = new BackupImageView(getContext());
                    imageView.setAspectFit(true);
                    ImageReceiver imageReceiver = imageView.getImageReceiver();
                    if (isVideo) {
                        imageReceiver.setAllowDecodeSingleFrame(true);
                        imageReceiver.setAllowStartLottieAnimation(false);
                        if (startAfterSet) {
                            imageReceiver.setDelegate(PaintingOverlay$$ExternalSyntheticLambda0.INSTANCE);
                        }
                    }
                    ImageReceiver imageReceiver2 = imageReceiver;
                    ImageReceiver imageReceiver3 = imageReceiver;
                    imageReceiver2.setImage(ImageLocation.getForDocument(entity.document), (String) null, ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(entity.document.thumbs, 90), entity.document), (String) null, "webp", entity.parentObject, 1);
                    if ((entity.subType & 2) != 0) {
                        imageView.setScaleX(-1.0f);
                    }
                    entity.view = imageView;
                } else if (entity.type == 1) {
                    EditTextOutline editText = new EditTextOutline(getContext()) {
                        public boolean dispatchTouchEvent(MotionEvent event) {
                            return false;
                        }

                        public boolean onTouchEvent(MotionEvent event) {
                            return false;
                        }
                    };
                    editText.setBackgroundColor(0);
                    editText.setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(7.0f));
                    editText.setTextSize(0, (float) entity.fontSize);
                    editText.setText(entity.text);
                    editText.setTypeface((Typeface) null, 1);
                    editText.setGravity(17);
                    editText.setHorizontallyScrolling(false);
                    editText.setImeOptions(NUM);
                    editText.setFocusableInTouchMode(true);
                    editText.setEnabled(false);
                    editText.setInputType(editText.getInputType() | 16384);
                    if (Build.VERSION.SDK_INT >= 23) {
                        editText.setBreakStrategy(0);
                    }
                    if ((1 & entity.subType) != 0) {
                        editText.setTextColor(-1);
                        editText.setStrokeColor(entity.color);
                        editText.setFrameColor(0);
                        editText.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                    } else if ((entity.subType & 4) != 0) {
                        editText.setTextColor(-16777216);
                        editText.setStrokeColor(0);
                        editText.setFrameColor(entity.color);
                        editText.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                    } else {
                        editText.setTextColor(entity.color);
                        editText.setStrokeColor(0);
                        editText.setFrameColor(0);
                        editText.setShadowLayer(5.0f, 0.0f, 1.0f, NUM);
                    }
                    entity.view = editText;
                }
                addView(entity.view);
                View view = entity.view;
                double d = (double) (-entity.rotation);
                Double.isNaN(d);
                view.setRotation((float) ((d / 3.141592653589793d) * 180.0d));
            }
        }
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
