package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.Theme;

public class VideoTimelineView extends View {
    private static final Object sync = new Object();
    private Paint backgroundGrayPaint;
    private AsyncTask<Integer, Integer, Bitmap> currentTask;
    private VideoTimelineViewDelegate delegate;
    /* access modifiers changed from: private */
    public int frameHeight;
    /* access modifiers changed from: private */
    public long frameTimeOffset;
    /* access modifiers changed from: private */
    public int frameWidth;
    /* access modifiers changed from: private */
    public ArrayList<Bitmap> frames = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean framesLoaded;
    /* access modifiers changed from: private */
    public int framesToLoad;
    private boolean isRoundFrames;
    private ArrayList<Bitmap> keyframes = new ArrayList<>();
    private float maxProgressDiff = 1.0f;
    /* access modifiers changed from: private */
    public MediaMetadataRetriever mediaMetadataRetriever;
    private float minProgressDiff = 0.0f;
    private Paint paint;
    private Paint paint2;
    private float pressDx;
    private boolean pressedLeft;
    private boolean pressedRight;
    private float progressLeft;
    private float progressRight = 1.0f;
    private Rect rect1;
    private Rect rect2;
    private Bitmap roundCornerBitmap;
    private int roundCornersSize;
    Paint thumbPaint = new Paint(1);
    Paint thumbRipplePaint = new Paint(1);
    private TimeHintView timeHintView;
    private long videoLength;

    public interface VideoTimelineViewDelegate {
        void didStartDragging();

        void didStopDragging();

        void onLeftProgressChanged(float f);

        void onRightProgressChanged(float f);
    }

    public void setKeyframes(ArrayList<Bitmap> arrayList) {
        this.keyframes.clear();
        this.keyframes.addAll(arrayList);
    }

    public VideoTimelineView(Context context) {
        super(context);
        Paint paint3 = new Paint(1);
        this.paint = paint3;
        paint3.setColor(-1);
        Paint paint4 = new Paint();
        this.paint2 = paint4;
        paint4.setColor(NUM);
        this.backgroundGrayPaint = new Paint();
        this.thumbPaint.setColor(-1);
        this.thumbPaint.setStrokeWidth(AndroidUtilities.dpf2(2.0f));
        this.thumbPaint.setStyle(Paint.Style.STROKE);
        this.thumbPaint.setStrokeCap(Paint.Cap.ROUND);
        updateColors();
    }

    public void updateColors() {
        this.backgroundGrayPaint.setColor(Theme.getColor("windowBackgroundGray"));
        this.thumbRipplePaint.setColor(Theme.getColor("key_chat_recordedVoiceHighlight"));
        this.roundCornersSize = 0;
        TimeHintView timeHintView2 = this.timeHintView;
        if (timeHintView2 != null) {
            timeHintView2.updateColors();
        }
    }

    public float getLeftProgress() {
        return this.progressLeft;
    }

    public float getRightProgress() {
        return this.progressRight;
    }

    public void setMinProgressDiff(float f) {
        this.minProgressDiff = f;
    }

    public void setMaxProgressDiff(float f) {
        this.maxProgressDiff = f;
        float f2 = this.progressRight;
        float f3 = this.progressLeft;
        if (f2 - f3 > f) {
            this.progressRight = f3 + f;
            invalidate();
        }
    }

    public void setRoundFrames(boolean z) {
        this.isRoundFrames = z;
        if (z) {
            this.rect1 = new Rect(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            this.rect2 = new Rect();
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent == null) {
            return false;
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(24.0f);
        float f = (float) measuredWidth;
        int dp = ((int) (this.progressLeft * f)) + AndroidUtilities.dp(12.0f);
        int dp2 = ((int) (this.progressRight * f)) + AndroidUtilities.dp(12.0f);
        if (motionEvent.getAction() == 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (this.mediaMetadataRetriever == null) {
                return false;
            }
            int dp3 = AndroidUtilities.dp(24.0f);
            if (((float) (dp - dp3)) <= x && x <= ((float) (dp + dp3)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                VideoTimelineViewDelegate videoTimelineViewDelegate = this.delegate;
                if (videoTimelineViewDelegate != null) {
                    videoTimelineViewDelegate.didStartDragging();
                }
                this.pressedLeft = true;
                this.pressDx = (float) ((int) (x - ((float) dp)));
                this.timeHintView.setTime((int) ((((float) this.videoLength) / 1000.0f) * this.progressLeft));
                this.timeHintView.setCx((float) (dp + getLeft() + AndroidUtilities.dp(4.0f)));
                this.timeHintView.show(true);
                invalidate();
                return true;
            } else if (((float) (dp2 - dp3)) > x || x > ((float) (dp3 + dp2)) || y < 0.0f || y > ((float) getMeasuredHeight())) {
                this.timeHintView.show(false);
            } else {
                VideoTimelineViewDelegate videoTimelineViewDelegate2 = this.delegate;
                if (videoTimelineViewDelegate2 != null) {
                    videoTimelineViewDelegate2.didStartDragging();
                }
                this.pressedRight = true;
                this.pressDx = (float) ((int) (x - ((float) dp2)));
                this.timeHintView.setTime((int) ((((float) this.videoLength) / 1000.0f) * this.progressRight));
                this.timeHintView.setCx((float) ((dp2 + getLeft()) - AndroidUtilities.dp(4.0f)));
                this.timeHintView.show(true);
                invalidate();
                return true;
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (this.pressedLeft) {
                VideoTimelineViewDelegate videoTimelineViewDelegate3 = this.delegate;
                if (videoTimelineViewDelegate3 != null) {
                    videoTimelineViewDelegate3.didStopDragging();
                }
                this.pressedLeft = false;
                invalidate();
                this.timeHintView.show(false);
                return true;
            } else if (this.pressedRight) {
                VideoTimelineViewDelegate videoTimelineViewDelegate4 = this.delegate;
                if (videoTimelineViewDelegate4 != null) {
                    videoTimelineViewDelegate4.didStopDragging();
                }
                this.pressedRight = false;
                invalidate();
                this.timeHintView.show(false);
                return true;
            }
        } else if (motionEvent.getAction() == 2) {
            if (this.pressedLeft) {
                int i = (int) (x - this.pressDx);
                if (i < AndroidUtilities.dp(16.0f)) {
                    dp2 = AndroidUtilities.dp(16.0f);
                } else if (i <= dp2) {
                    dp2 = i;
                }
                float dp4 = ((float) (dp2 - AndroidUtilities.dp(16.0f))) / f;
                this.progressLeft = dp4;
                float f2 = this.progressRight;
                float f3 = this.maxProgressDiff;
                if (f2 - dp4 > f3) {
                    this.progressRight = dp4 + f3;
                } else {
                    float f4 = this.minProgressDiff;
                    if (f4 != 0.0f && f2 - dp4 < f4) {
                        float f5 = f2 - f4;
                        this.progressLeft = f5;
                        if (f5 < 0.0f) {
                            this.progressLeft = 0.0f;
                        }
                    }
                }
                this.timeHintView.setCx((((f * this.progressLeft) + AndroidUtilities.dpf2(12.0f)) + ((float) getLeft())) - ((float) AndroidUtilities.dp(4.0f)));
                this.timeHintView.setTime((int) ((((float) this.videoLength) / 1000.0f) * this.progressLeft));
                this.timeHintView.show(true);
                VideoTimelineViewDelegate videoTimelineViewDelegate5 = this.delegate;
                if (videoTimelineViewDelegate5 != null) {
                    videoTimelineViewDelegate5.onLeftProgressChanged(this.progressLeft);
                }
                invalidate();
                return true;
            } else if (this.pressedRight) {
                int i2 = (int) (x - this.pressDx);
                if (i2 >= dp) {
                    dp = i2 > AndroidUtilities.dp(16.0f) + measuredWidth ? measuredWidth + AndroidUtilities.dp(16.0f) : i2;
                }
                float dp5 = ((float) (dp - AndroidUtilities.dp(16.0f))) / f;
                this.progressRight = dp5;
                float f6 = this.progressLeft;
                float f7 = this.maxProgressDiff;
                if (dp5 - f6 > f7) {
                    this.progressLeft = dp5 - f7;
                } else {
                    float f8 = this.minProgressDiff;
                    if (f8 != 0.0f && dp5 - f6 < f8) {
                        float f9 = f6 + f8;
                        this.progressRight = f9;
                        if (f9 > 1.0f) {
                            this.progressRight = 1.0f;
                        }
                    }
                }
                this.timeHintView.setCx((f * this.progressRight) + AndroidUtilities.dpf2(12.0f) + ((float) getLeft()) + ((float) AndroidUtilities.dp(4.0f)));
                this.timeHintView.show(true);
                this.timeHintView.setTime((int) ((((float) this.videoLength) / 1000.0f) * this.progressRight));
                VideoTimelineViewDelegate videoTimelineViewDelegate6 = this.delegate;
                if (videoTimelineViewDelegate6 != null) {
                    videoTimelineViewDelegate6.onRightProgressChanged(this.progressRight);
                }
                invalidate();
                return true;
            }
        }
        return false;
    }

    public void setColor(int i) {
        this.paint.setColor(i);
        invalidate();
    }

    public void setVideoPath(String str) {
        destroy();
        MediaMetadataRetriever mediaMetadataRetriever2 = new MediaMetadataRetriever();
        this.mediaMetadataRetriever = mediaMetadataRetriever2;
        this.progressLeft = 0.0f;
        this.progressRight = 1.0f;
        try {
            mediaMetadataRetriever2.setDataSource(str);
            this.videoLength = Long.parseLong(this.mediaMetadataRetriever.extractMetadata(9));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        invalidate();
    }

    public void setDelegate(VideoTimelineViewDelegate videoTimelineViewDelegate) {
        this.delegate = videoTimelineViewDelegate;
    }

    /* access modifiers changed from: private */
    public void reloadFrames(int i) {
        if (this.mediaMetadataRetriever != null) {
            if (i == 0) {
                if (this.isRoundFrames) {
                    int dp = AndroidUtilities.dp(56.0f);
                    this.frameWidth = dp;
                    this.frameHeight = dp;
                    this.framesToLoad = Math.max(1, (int) Math.ceil((double) (((float) (getMeasuredWidth() - AndroidUtilities.dp(16.0f))) / (((float) this.frameHeight) / 2.0f))));
                } else {
                    this.frameHeight = AndroidUtilities.dp(40.0f);
                    this.framesToLoad = Math.max(1, (getMeasuredWidth() - AndroidUtilities.dp(16.0f)) / this.frameHeight);
                    this.frameWidth = (int) Math.ceil((double) (((float) (getMeasuredWidth() - AndroidUtilities.dp(16.0f))) / ((float) this.framesToLoad)));
                }
                this.frameTimeOffset = this.videoLength / ((long) this.framesToLoad);
                if (!this.keyframes.isEmpty()) {
                    float size = ((float) this.keyframes.size()) / ((float) this.framesToLoad);
                    float f = 0.0f;
                    for (int i2 = 0; i2 < this.framesToLoad; i2++) {
                        this.frames.add(this.keyframes.get((int) f));
                        f += size;
                    }
                    return;
                }
            }
            this.framesLoaded = false;
            AnonymousClass1 r2 = new AsyncTask<Integer, Integer, Bitmap>() {
                private int frameNum = 0;

                /* access modifiers changed from: protected */
                public Bitmap doInBackground(Integer... numArr) {
                    this.frameNum = numArr[0].intValue();
                    Bitmap bitmap = null;
                    if (isCancelled()) {
                        return null;
                    }
                    try {
                        Bitmap frameAtTime = VideoTimelineView.this.mediaMetadataRetriever.getFrameAtTime(VideoTimelineView.this.frameTimeOffset * ((long) this.frameNum) * 1000, 2);
                        try {
                            if (isCancelled()) {
                                return null;
                            }
                            if (frameAtTime == null) {
                                return frameAtTime;
                            }
                            Bitmap createBitmap = Bitmap.createBitmap(VideoTimelineView.this.frameWidth, VideoTimelineView.this.frameHeight, frameAtTime.getConfig());
                            Canvas canvas = new Canvas(createBitmap);
                            float max = Math.max(((float) VideoTimelineView.this.frameWidth) / ((float) frameAtTime.getWidth()), ((float) VideoTimelineView.this.frameHeight) / ((float) frameAtTime.getHeight()));
                            int width = (int) (((float) frameAtTime.getWidth()) * max);
                            int height = (int) (((float) frameAtTime.getHeight()) * max);
                            canvas.drawBitmap(frameAtTime, new Rect(0, 0, frameAtTime.getWidth(), frameAtTime.getHeight()), new Rect((VideoTimelineView.this.frameWidth - width) / 2, (VideoTimelineView.this.frameHeight - height) / 2, width, height), (Paint) null);
                            frameAtTime.recycle();
                            return createBitmap;
                        } catch (Exception e) {
                            e = e;
                            bitmap = frameAtTime;
                            FileLog.e((Throwable) e);
                            return bitmap;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.e((Throwable) e);
                        return bitmap;
                    }
                }

                /* access modifiers changed from: protected */
                public void onPostExecute(Bitmap bitmap) {
                    if (!isCancelled()) {
                        VideoTimelineView.this.frames.add(bitmap);
                        VideoTimelineView.this.invalidate();
                        if (this.frameNum < VideoTimelineView.this.framesToLoad) {
                            VideoTimelineView.this.reloadFrames(this.frameNum + 1);
                        } else {
                            boolean unused = VideoTimelineView.this.framesLoaded = true;
                        }
                    }
                }
            };
            this.currentTask = r2;
            r2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Integer[]{Integer.valueOf(i), null, null});
        }
    }

    public void destroy() {
        synchronized (sync) {
            try {
                if (this.mediaMetadataRetriever != null) {
                    this.mediaMetadataRetriever.release();
                    this.mediaMetadataRetriever = null;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        int i = 0;
        if (!this.keyframes.isEmpty()) {
            while (i < this.keyframes.size()) {
                Bitmap bitmap = this.keyframes.get(i);
                if (bitmap != null) {
                    bitmap.recycle();
                }
                i++;
            }
        } else {
            while (i < this.frames.size()) {
                Bitmap bitmap2 = this.frames.get(i);
                if (bitmap2 != null) {
                    bitmap2.recycle();
                }
                i++;
            }
        }
        this.keyframes.clear();
        this.frames.clear();
        AsyncTask<Integer, Integer, Bitmap> asyncTask = this.currentTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.currentTask = null;
        }
    }

    public void clearFrames() {
        if (this.keyframes.isEmpty()) {
            for (int i = 0; i < this.frames.size(); i++) {
                Bitmap bitmap = this.frames.get(i);
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        }
        this.frames.clear();
        AsyncTask<Integer, Integer, Bitmap> asyncTask = this.currentTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.currentTask = null;
        }
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float measuredWidth = (float) (getMeasuredWidth() - AndroidUtilities.dp(24.0f));
        int dp = ((int) (this.progressLeft * measuredWidth)) + AndroidUtilities.dp(12.0f);
        int dp2 = ((int) (measuredWidth * this.progressRight)) + AndroidUtilities.dp(12.0f);
        int measuredHeight = (getMeasuredHeight() - AndroidUtilities.dp(32.0f)) >> 1;
        if (this.frames.isEmpty() && this.currentTask == null) {
            reloadFrames(0);
        }
        if (!this.frames.isEmpty()) {
            if (!this.framesLoaded) {
                canvas.drawRect(0.0f, (float) measuredHeight, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - measuredHeight), this.backgroundGrayPaint);
            }
            int i = 0;
            for (int i2 = 0; i2 < this.frames.size(); i2++) {
                Bitmap bitmap = this.frames.get(i2);
                if (bitmap != null) {
                    int i3 = (this.isRoundFrames ? this.frameWidth / 2 : this.frameWidth) * i;
                    if (this.isRoundFrames) {
                        this.rect2.set(i3, measuredHeight, AndroidUtilities.dp(28.0f) + i3, AndroidUtilities.dp(32.0f) + measuredHeight);
                        canvas.drawBitmap(bitmap, this.rect1, this.rect2, (Paint) null);
                    } else {
                        canvas.drawBitmap(bitmap, (float) i3, (float) measuredHeight, (Paint) null);
                    }
                }
                i++;
            }
            float f = (float) measuredHeight;
            canvas.drawRect(0.0f, f, (float) dp, (float) (getMeasuredHeight() - measuredHeight), this.paint2);
            canvas.drawRect((float) dp2, f, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - measuredHeight), this.paint2);
            canvas.drawLine((float) (dp - AndroidUtilities.dp(4.0f)), (float) (AndroidUtilities.dp(10.0f) + measuredHeight), (float) (dp - AndroidUtilities.dp(4.0f)), (float) ((getMeasuredHeight() - AndroidUtilities.dp(10.0f)) - measuredHeight), this.thumbPaint);
            canvas.drawLine((float) (AndroidUtilities.dp(4.0f) + dp2), (float) (AndroidUtilities.dp(10.0f) + measuredHeight), (float) (dp2 + AndroidUtilities.dp(4.0f)), (float) ((getMeasuredHeight() - AndroidUtilities.dp(10.0f)) - measuredHeight), this.thumbPaint);
            drawCorners(canvas, getMeasuredHeight() - (measuredHeight * 2), getMeasuredWidth(), 0, measuredHeight);
        }
    }

    private void drawCorners(Canvas canvas, int i, int i2, int i3, int i4) {
        if (AndroidUtilities.dp(6.0f) != this.roundCornersSize) {
            this.roundCornersSize = AndroidUtilities.dp(6.0f);
            this.roundCornerBitmap = Bitmap.createBitmap(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Bitmap.Config.ARGB_8888);
            Canvas canvas2 = new Canvas(this.roundCornerBitmap);
            Paint paint3 = new Paint(1);
            paint3.setColor(0);
            paint3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas2.drawColor(Theme.getColor("chat_messagePanelBackground"));
            int i5 = this.roundCornersSize;
            canvas2.drawCircle((float) i5, (float) i5, (float) i5, paint3);
        }
        int i6 = this.roundCornersSize >> 1;
        canvas.save();
        float f = (float) i3;
        float f2 = (float) i4;
        canvas.drawBitmap(this.roundCornerBitmap, f, f2, (Paint) null);
        int i7 = i + i4;
        float f3 = (float) (i7 - i6);
        canvas.rotate(-90.0f, (float) (i3 + i6), f3);
        canvas.drawBitmap(this.roundCornerBitmap, f, (float) (i7 - this.roundCornersSize), (Paint) null);
        canvas.restore();
        canvas.save();
        int i8 = i3 + i2;
        float f4 = (float) (i8 - i6);
        canvas.rotate(180.0f, f4, f3);
        Bitmap bitmap = this.roundCornerBitmap;
        int i9 = this.roundCornersSize;
        canvas.drawBitmap(bitmap, (float) (i8 - i9), (float) (i7 - i9), (Paint) null);
        canvas.restore();
        canvas.save();
        canvas.rotate(90.0f, f4, (float) (i4 + i6));
        canvas.drawBitmap(this.roundCornerBitmap, (float) (i8 - this.roundCornersSize), f2, (Paint) null);
        canvas.restore();
    }

    public void setTimeHintView(TimeHintView timeHintView2) {
        this.timeHintView = timeHintView2;
    }

    public static class TimeHintView extends View {
        private float cx;
        private long lastTime = -1;
        private float scale;
        private boolean show;
        private Drawable tooltipBackground;
        private Drawable tooltipBackgroundArrow;
        private StaticLayout tooltipLayout;
        private TextPaint tooltipPaint;

        public TimeHintView(Context context) {
            super(context);
            TextPaint textPaint = new TextPaint(1);
            this.tooltipPaint = textPaint;
            textPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
            this.tooltipBackgroundArrow = ContextCompat.getDrawable(context, NUM);
            this.tooltipBackground = Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), Theme.getColor("chat_gifSaveHintBackground"));
            updateColors();
            setTime(0);
        }

        public void setTime(int i) {
            long j = (long) i;
            if (j != this.lastTime) {
                this.lastTime = j;
                String formatShortDuration = AndroidUtilities.formatShortDuration(i);
                TextPaint textPaint = this.tooltipPaint;
                this.tooltipLayout = new StaticLayout(formatShortDuration, textPaint, (int) textPaint.measureText(formatShortDuration), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(this.tooltipLayout.getHeight() + AndroidUtilities.dp(4.0f) + this.tooltipBackgroundArrow.getIntrinsicHeight(), NUM));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.tooltipLayout != null) {
                if (this.show) {
                    float f = this.scale;
                    if (f != 1.0f) {
                        float f2 = f + 0.12f;
                        this.scale = f2;
                        if (f2 > 1.0f) {
                            this.scale = 1.0f;
                        }
                        invalidate();
                    }
                } else {
                    float f3 = this.scale;
                    if (f3 != 0.0f) {
                        float f4 = f3 - 0.12f;
                        this.scale = f4;
                        if (f4 < 0.0f) {
                            this.scale = 0.0f;
                        }
                        invalidate();
                    }
                    if (this.scale == 0.0f) {
                        return;
                    }
                }
                float f5 = this.scale;
                int i = (int) ((f5 > 0.5f ? 1.0f : f5 / 0.5f) * 255.0f);
                canvas.save();
                float f6 = this.scale;
                canvas.scale(f6, f6, this.cx, (float) getMeasuredHeight());
                canvas.translate(this.cx - (((float) this.tooltipLayout.getWidth()) / 2.0f), 0.0f);
                this.tooltipBackground.setBounds(-AndroidUtilities.dp(8.0f), 0, this.tooltipLayout.getWidth() + AndroidUtilities.dp(8.0f), (int) (((float) this.tooltipLayout.getHeight()) + AndroidUtilities.dpf2(4.0f)));
                this.tooltipBackgroundArrow.setBounds((this.tooltipLayout.getWidth() / 2) - (this.tooltipBackgroundArrow.getIntrinsicWidth() / 2), (int) (((float) this.tooltipLayout.getHeight()) + AndroidUtilities.dpf2(4.0f)), (this.tooltipLayout.getWidth() / 2) + (this.tooltipBackgroundArrow.getIntrinsicWidth() / 2), ((int) (((float) this.tooltipLayout.getHeight()) + AndroidUtilities.dpf2(4.0f))) + this.tooltipBackgroundArrow.getIntrinsicHeight());
                this.tooltipBackgroundArrow.setAlpha(i);
                this.tooltipBackground.setAlpha(i);
                this.tooltipPaint.setAlpha(i);
                this.tooltipBackgroundArrow.draw(canvas);
                this.tooltipBackground.draw(canvas);
                canvas.translate(0.0f, AndroidUtilities.dpf2(1.0f));
                this.tooltipLayout.draw(canvas);
                canvas.restore();
            }
        }

        public void updateColors() {
            this.tooltipPaint.setColor(Theme.getColor("chat_gifSaveHintText"));
            this.tooltipBackground = Theme.createRoundRectDrawable(AndroidUtilities.dp(5.0f), Theme.getColor("chat_gifSaveHintBackground"));
            this.tooltipBackgroundArrow.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_gifSaveHintBackground"), PorterDuff.Mode.MULTIPLY));
        }

        public void setCx(float f) {
            this.cx = f;
            invalidate();
        }

        public void show(boolean z) {
            this.show = z;
            invalidate();
        }
    }
}
