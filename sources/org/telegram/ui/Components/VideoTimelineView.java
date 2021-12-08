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

    public void setKeyframes(ArrayList<Bitmap> keyframes2) {
        this.keyframes.clear();
        this.keyframes.addAll(keyframes2);
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

    public void setMinProgressDiff(float value) {
        this.minProgressDiff = value;
    }

    public void setMaxProgressDiff(float value) {
        this.maxProgressDiff = value;
        float f = this.progressRight;
        float f2 = this.progressLeft;
        if (f - f2 > value) {
            this.progressRight = f2 + value;
            invalidate();
        }
    }

    public void setRoundFrames(boolean value) {
        this.isRoundFrames = value;
        if (value) {
            this.rect1 = new Rect(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            this.rect2 = new Rect();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        int width = getMeasuredWidth() - AndroidUtilities.dp(24.0f);
        int startX = ((int) (((float) width) * this.progressLeft)) + AndroidUtilities.dp(12.0f);
        int endX = ((int) (((float) width) * this.progressRight)) + AndroidUtilities.dp(12.0f);
        if (event.getAction() == 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (this.mediaMetadataRetriever == null) {
                return false;
            }
            int additionWidth = AndroidUtilities.dp(24.0f);
            if (((float) (startX - additionWidth)) <= x && x <= ((float) (startX + additionWidth)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                VideoTimelineViewDelegate videoTimelineViewDelegate = this.delegate;
                if (videoTimelineViewDelegate != null) {
                    videoTimelineViewDelegate.didStartDragging();
                }
                this.pressedLeft = true;
                this.pressDx = (float) ((int) (x - ((float) startX)));
                this.timeHintView.setTime((int) ((((float) this.videoLength) / 1000.0f) * this.progressLeft));
                this.timeHintView.setCx((float) (getLeft() + startX + AndroidUtilities.dp(4.0f)));
                this.timeHintView.show(true);
                invalidate();
                return true;
            } else if (((float) (endX - additionWidth)) > x || x > ((float) (endX + additionWidth)) || y < 0.0f || y > ((float) getMeasuredHeight())) {
                this.timeHintView.show(false);
            } else {
                VideoTimelineViewDelegate videoTimelineViewDelegate2 = this.delegate;
                if (videoTimelineViewDelegate2 != null) {
                    videoTimelineViewDelegate2.didStartDragging();
                }
                this.pressedRight = true;
                this.pressDx = (float) ((int) (x - ((float) endX)));
                this.timeHintView.setTime((int) ((((float) this.videoLength) / 1000.0f) * this.progressRight));
                this.timeHintView.setCx((float) ((getLeft() + endX) - AndroidUtilities.dp(4.0f)));
                this.timeHintView.show(true);
                invalidate();
                return true;
            }
        } else if (event.getAction() == 1 || event.getAction() == 3) {
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
        } else if (event.getAction() == 2) {
            if (this.pressedLeft) {
                int startX2 = (int) (x - this.pressDx);
                if (startX2 < AndroidUtilities.dp(16.0f)) {
                    startX2 = AndroidUtilities.dp(16.0f);
                } else if (startX2 > endX) {
                    startX2 = endX;
                }
                float dp = ((float) (startX2 - AndroidUtilities.dp(16.0f))) / ((float) width);
                this.progressLeft = dp;
                float f = this.progressRight;
                float f2 = this.maxProgressDiff;
                if (f - dp > f2) {
                    this.progressRight = dp + f2;
                } else {
                    float f3 = this.minProgressDiff;
                    if (f3 != 0.0f && f - dp < f3) {
                        float f4 = f - f3;
                        this.progressLeft = f4;
                        if (f4 < 0.0f) {
                            this.progressLeft = 0.0f;
                        }
                    }
                }
                this.timeHintView.setCx((((((float) width) * this.progressLeft) + AndroidUtilities.dpf2(12.0f)) + ((float) getLeft())) - ((float) AndroidUtilities.dp(4.0f)));
                this.timeHintView.setTime((int) ((((float) this.videoLength) / 1000.0f) * this.progressLeft));
                this.timeHintView.show(true);
                VideoTimelineViewDelegate videoTimelineViewDelegate5 = this.delegate;
                if (videoTimelineViewDelegate5 != null) {
                    videoTimelineViewDelegate5.onLeftProgressChanged(this.progressLeft);
                }
                invalidate();
                return true;
            } else if (this.pressedRight) {
                int endX2 = (int) (x - this.pressDx);
                if (endX2 < startX) {
                    endX2 = startX;
                } else if (endX2 > AndroidUtilities.dp(16.0f) + width) {
                    endX2 = width + AndroidUtilities.dp(16.0f);
                }
                float dp2 = ((float) (endX2 - AndroidUtilities.dp(16.0f))) / ((float) width);
                this.progressRight = dp2;
                float f5 = this.progressLeft;
                float f6 = this.maxProgressDiff;
                if (dp2 - f5 > f6) {
                    this.progressLeft = dp2 - f6;
                } else {
                    float f7 = this.minProgressDiff;
                    if (f7 != 0.0f && dp2 - f5 < f7) {
                        float f8 = f5 + f7;
                        this.progressRight = f8;
                        if (f8 > 1.0f) {
                            this.progressRight = 1.0f;
                        }
                    }
                }
                this.timeHintView.setCx((((float) width) * this.progressRight) + AndroidUtilities.dpf2(12.0f) + ((float) getLeft()) + ((float) AndroidUtilities.dp(4.0f)));
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

    public void setColor(int color) {
        this.paint.setColor(color);
        invalidate();
    }

    public void setVideoPath(String path) {
        destroy();
        MediaMetadataRetriever mediaMetadataRetriever2 = new MediaMetadataRetriever();
        this.mediaMetadataRetriever = mediaMetadataRetriever2;
        this.progressLeft = 0.0f;
        this.progressRight = 1.0f;
        try {
            mediaMetadataRetriever2.setDataSource(path);
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
    public void reloadFrames(int frameNum) {
        if (this.mediaMetadataRetriever != null) {
            if (frameNum == 0) {
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
                    float step = ((float) this.keyframes.size()) / ((float) this.framesToLoad);
                    float currentP = 0.0f;
                    for (int i = 0; i < this.framesToLoad; i++) {
                        this.frames.add(this.keyframes.get((int) currentP));
                        currentP += step;
                    }
                    return;
                }
            }
            this.framesLoaded = false;
            AnonymousClass1 r2 = new AsyncTask<Integer, Integer, Bitmap>() {
                private int frameNum = 0;

                /* access modifiers changed from: protected */
                public Bitmap doInBackground(Integer... objects) {
                    this.frameNum = objects[0].intValue();
                    if (isCancelled()) {
                        return null;
                    }
                    try {
                        Bitmap bitmap = VideoTimelineView.this.mediaMetadataRetriever.getFrameAtTime(VideoTimelineView.this.frameTimeOffset * ((long) this.frameNum) * 1000, 2);
                        if (isCancelled()) {
                            return null;
                        }
                        if (bitmap == null) {
                            return bitmap;
                        }
                        Bitmap result = Bitmap.createBitmap(VideoTimelineView.this.frameWidth, VideoTimelineView.this.frameHeight, bitmap.getConfig());
                        Canvas canvas = new Canvas(result);
                        float scale = Math.max(((float) VideoTimelineView.this.frameWidth) / ((float) bitmap.getWidth()), ((float) VideoTimelineView.this.frameHeight) / ((float) bitmap.getHeight()));
                        int w = (int) (((float) bitmap.getWidth()) * scale);
                        int h = (int) (((float) bitmap.getHeight()) * scale);
                        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect((VideoTimelineView.this.frameWidth - w) / 2, (VideoTimelineView.this.frameHeight - h) / 2, w, h), (Paint) null);
                        bitmap.recycle();
                        return result;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        return null;
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
            r2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Integer[]{Integer.valueOf(frameNum), null, null});
        }
    }

    public void destroy() {
        synchronized (sync) {
            try {
                MediaMetadataRetriever mediaMetadataRetriever2 = this.mediaMetadataRetriever;
                if (mediaMetadataRetriever2 != null) {
                    mediaMetadataRetriever2.release();
                    this.mediaMetadataRetriever = null;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        if (!this.keyframes.isEmpty()) {
            for (int a = 0; a < this.keyframes.size(); a++) {
                Bitmap bitmap = this.keyframes.get(a);
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        } else {
            for (int a2 = 0; a2 < this.frames.size(); a2++) {
                Bitmap bitmap2 = this.frames.get(a2);
                if (bitmap2 != null) {
                    bitmap2.recycle();
                }
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
            for (int a = 0; a < this.frames.size(); a++) {
                Bitmap bitmap = this.frames.get(a);
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
        Canvas canvas2 = canvas;
        int width = getMeasuredWidth() - AndroidUtilities.dp(24.0f);
        int startX = ((int) (((float) width) * this.progressLeft)) + AndroidUtilities.dp(12.0f);
        int endX = ((int) (((float) width) * this.progressRight)) + AndroidUtilities.dp(12.0f);
        int topOffset = (getMeasuredHeight() - AndroidUtilities.dp(32.0f)) >> 1;
        if (this.frames.isEmpty() && this.currentTask == null) {
            reloadFrames(0);
        }
        if (!this.frames.isEmpty()) {
            if (!this.framesLoaded) {
                canvas.drawRect(0.0f, (float) topOffset, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - topOffset), this.backgroundGrayPaint);
            }
            int offset = 0;
            for (int a = 0; a < this.frames.size(); a++) {
                Bitmap bitmap = this.frames.get(a);
                if (bitmap != null) {
                    boolean z = this.isRoundFrames;
                    int i = this.frameWidth;
                    if (z) {
                        i /= 2;
                    }
                    int x = i * offset;
                    if (z) {
                        this.rect2.set(x, topOffset, AndroidUtilities.dp(28.0f) + x, AndroidUtilities.dp(32.0f) + topOffset);
                        canvas2.drawBitmap(bitmap, this.rect1, this.rect2, (Paint) null);
                    } else {
                        canvas2.drawBitmap(bitmap, (float) x, (float) topOffset, (Paint) null);
                    }
                }
                offset++;
            }
            canvas.drawRect(0.0f, (float) topOffset, (float) startX, (float) (getMeasuredHeight() - topOffset), this.paint2);
            canvas.drawRect((float) endX, (float) topOffset, (float) getMeasuredWidth(), (float) (getMeasuredHeight() - topOffset), this.paint2);
            canvas.drawLine((float) (startX - AndroidUtilities.dp(4.0f)), (float) (AndroidUtilities.dp(10.0f) + topOffset), (float) (startX - AndroidUtilities.dp(4.0f)), (float) ((getMeasuredHeight() - AndroidUtilities.dp(10.0f)) - topOffset), this.thumbPaint);
            canvas.drawLine((float) (AndroidUtilities.dp(4.0f) + endX), (float) (AndroidUtilities.dp(10.0f) + topOffset), (float) (AndroidUtilities.dp(4.0f) + endX), (float) ((getMeasuredHeight() - AndroidUtilities.dp(10.0f)) - topOffset), this.thumbPaint);
            drawCorners(canvas, getMeasuredHeight() - (topOffset * 2), getMeasuredWidth(), 0, topOffset);
        }
    }

    private void drawCorners(Canvas canvas, int height, int width, int left, int top) {
        if (AndroidUtilities.dp(6.0f) != this.roundCornersSize) {
            this.roundCornersSize = AndroidUtilities.dp(6.0f);
            this.roundCornerBitmap = Bitmap.createBitmap(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Bitmap.Config.ARGB_8888);
            Canvas bitmapCanvas = new Canvas(this.roundCornerBitmap);
            Paint xRefP = new Paint(1);
            xRefP.setColor(0);
            xRefP.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            bitmapCanvas.drawColor(Theme.getColor("chat_messagePanelBackground"));
            int i = this.roundCornersSize;
            bitmapCanvas.drawCircle((float) i, (float) i, (float) i, xRefP);
        }
        int sizeHalf = this.roundCornersSize >> 1;
        canvas.save();
        canvas.drawBitmap(this.roundCornerBitmap, (float) left, (float) top, (Paint) null);
        canvas.rotate(-90.0f, (float) (left + sizeHalf), (float) ((top + height) - sizeHalf));
        canvas.drawBitmap(this.roundCornerBitmap, (float) left, (float) ((top + height) - this.roundCornersSize), (Paint) null);
        canvas.restore();
        canvas.save();
        canvas.rotate(180.0f, (float) ((left + width) - sizeHalf), (float) ((top + height) - sizeHalf));
        Bitmap bitmap = this.roundCornerBitmap;
        int i2 = this.roundCornersSize;
        canvas.drawBitmap(bitmap, (float) ((left + width) - i2), (float) ((top + height) - i2), (Paint) null);
        canvas.restore();
        canvas.save();
        canvas.rotate(90.0f, (float) ((left + width) - sizeHalf), (float) (top + sizeHalf));
        canvas.drawBitmap(this.roundCornerBitmap, (float) ((left + width) - this.roundCornersSize), (float) top, (Paint) null);
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
        private boolean showTooltip;
        private long showTooltipStartTime;
        private float tooltipAlpha;
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

        public void setTime(int timeInSeconds) {
            if (((long) timeInSeconds) != this.lastTime) {
                this.lastTime = (long) timeInSeconds;
                String s = AndroidUtilities.formatShortDuration(timeInSeconds);
                TextPaint textPaint = this.tooltipPaint;
                this.tooltipLayout = new StaticLayout(s, textPaint, (int) textPaint.measureText(s), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(this.tooltipLayout.getHeight() + AndroidUtilities.dp(4.0f) + this.tooltipBackgroundArrow.getIntrinsicHeight(), NUM));
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
                int alpha = (int) ((f5 > 0.5f ? 1.0f : f5 / 0.5f) * 255.0f);
                canvas.save();
                float f6 = this.scale;
                canvas.scale(f6, f6, this.cx, (float) getMeasuredHeight());
                canvas.translate(this.cx - (((float) this.tooltipLayout.getWidth()) / 2.0f), 0.0f);
                this.tooltipBackground.setBounds(-AndroidUtilities.dp(8.0f), 0, this.tooltipLayout.getWidth() + AndroidUtilities.dp(8.0f), (int) (((float) this.tooltipLayout.getHeight()) + AndroidUtilities.dpf2(4.0f)));
                this.tooltipBackgroundArrow.setBounds((this.tooltipLayout.getWidth() / 2) - (this.tooltipBackgroundArrow.getIntrinsicWidth() / 2), (int) (((float) this.tooltipLayout.getHeight()) + AndroidUtilities.dpf2(4.0f)), (this.tooltipLayout.getWidth() / 2) + (this.tooltipBackgroundArrow.getIntrinsicWidth() / 2), ((int) (((float) this.tooltipLayout.getHeight()) + AndroidUtilities.dpf2(4.0f))) + this.tooltipBackgroundArrow.getIntrinsicHeight());
                this.tooltipBackgroundArrow.setAlpha(alpha);
                this.tooltipBackground.setAlpha(alpha);
                this.tooltipPaint.setAlpha(alpha);
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

        public void setCx(float v) {
            this.cx = v;
            invalidate();
        }

        public void show(boolean s) {
            this.show = s;
            invalidate();
        }
    }
}
