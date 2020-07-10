package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class VideoTimelinePlayView extends View {
    public static int TYPE_LEFT = 0;
    public static int TYPE_PROGRESS = 2;
    public static int TYPE_RIGHT = 1;
    private static final Object sync = new Object();
    private int currentMode = 0;
    private AsyncTask<Integer, Integer, Bitmap> currentTask;
    private VideoTimelineViewDelegate delegate;
    private Drawable drawableLeft;
    private Drawable drawableRight;
    private ArrayList<Rect> exclusionRects = new ArrayList<>();
    private Rect exclustionRect = new Rect();
    /* access modifiers changed from: private */
    public int frameHeight;
    /* access modifiers changed from: private */
    public long frameTimeOffset;
    /* access modifiers changed from: private */
    public int frameWidth;
    /* access modifiers changed from: private */
    public ArrayList<Bitmap> frames = new ArrayList<>();
    /* access modifiers changed from: private */
    public int framesToLoad;
    private int lastWidth;
    private float maxProgressDiff = 1.0f;
    /* access modifiers changed from: private */
    public MediaMetadataRetriever mediaMetadataRetriever;
    private float minProgressDiff = 0.0f;
    private Paint paint;
    private Paint paint2;
    private float playProgress = 0.5f;
    private float pressDx;
    private boolean pressedLeft;
    private boolean pressedPlay;
    private boolean pressedRight;
    private float progressLeft;
    private float progressRight = 1.0f;
    private RectF rect3 = new RectF();
    private long videoLength;

    public interface VideoTimelineViewDelegate {
        void didStartDragging(int i);

        void didStopDragging(int i);

        void onLeftProgressChanged(float f);

        void onPlayProgressChanged(float f);

        void onRightProgressChanged(float f);
    }

    public VideoTimelinePlayView(Context context) {
        super(context);
        Paint paint3 = new Paint(1);
        this.paint = paint3;
        paint3.setColor(-1);
        Paint paint4 = new Paint();
        this.paint2 = paint4;
        paint4.setColor(NUM);
        Drawable drawable = context.getResources().getDrawable(NUM);
        this.drawableLeft = drawable;
        drawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        Drawable drawable2 = context.getResources().getDrawable(NUM);
        this.drawableRight = drawable2;
        drawable2.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        this.exclusionRects.add(this.exclustionRect);
    }

    public float getProgress() {
        return this.playProgress;
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

    public void setMode(int i) {
        if (this.currentMode != i) {
            this.currentMode = i;
            invalidate();
        }
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

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (Build.VERSION.SDK_INT >= 29) {
            this.exclustionRect.set(i, 0, i3, getMeasuredHeight());
            setSystemGestureExclusionRects(this.exclusionRects);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent == null) {
            return false;
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(32.0f);
        float f = (float) measuredWidth;
        int dp = ((int) (this.progressLeft * f)) + AndroidUtilities.dp(16.0f);
        int dp2 = ((int) (this.playProgress * f)) + AndroidUtilities.dp(16.0f);
        int dp3 = ((int) (this.progressRight * f)) + AndroidUtilities.dp(16.0f);
        if (motionEvent.getAction() == 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (this.mediaMetadataRetriever == null) {
                return false;
            }
            int dp4 = AndroidUtilities.dp(16.0f);
            int dp5 = AndroidUtilities.dp(8.0f);
            if (dp3 != dp && ((float) (dp2 - dp5)) <= x && x <= ((float) (dp5 + dp2)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                VideoTimelineViewDelegate videoTimelineViewDelegate = this.delegate;
                if (videoTimelineViewDelegate != null) {
                    videoTimelineViewDelegate.didStartDragging(TYPE_PROGRESS);
                }
                this.pressedPlay = true;
                this.pressDx = (float) ((int) (x - ((float) dp2)));
                invalidate();
                return true;
            } else if (((float) (dp - dp4)) <= x && x <= ((float) Math.min(dp + dp4, dp3)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                VideoTimelineViewDelegate videoTimelineViewDelegate2 = this.delegate;
                if (videoTimelineViewDelegate2 != null) {
                    videoTimelineViewDelegate2.didStartDragging(TYPE_LEFT);
                }
                this.pressedLeft = true;
                this.pressDx = (float) ((int) (x - ((float) dp)));
                invalidate();
                return true;
            } else if (((float) (dp3 - dp4)) <= x && x <= ((float) (dp4 + dp3)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                VideoTimelineViewDelegate videoTimelineViewDelegate3 = this.delegate;
                if (videoTimelineViewDelegate3 != null) {
                    videoTimelineViewDelegate3.didStartDragging(TYPE_RIGHT);
                }
                this.pressedRight = true;
                this.pressDx = (float) ((int) (x - ((float) dp3)));
                invalidate();
                return true;
            } else if (((float) dp) <= x && x <= ((float) dp3) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                VideoTimelineViewDelegate videoTimelineViewDelegate4 = this.delegate;
                if (videoTimelineViewDelegate4 != null) {
                    videoTimelineViewDelegate4.didStartDragging(TYPE_PROGRESS);
                }
                this.pressedPlay = true;
                float dp6 = (x - ((float) AndroidUtilities.dp(16.0f))) / f;
                this.playProgress = dp6;
                VideoTimelineViewDelegate videoTimelineViewDelegate5 = this.delegate;
                if (videoTimelineViewDelegate5 != null) {
                    videoTimelineViewDelegate5.onPlayProgressChanged(dp6);
                }
                this.pressDx = 0.0f;
                invalidate();
                return true;
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (this.pressedLeft) {
                VideoTimelineViewDelegate videoTimelineViewDelegate6 = this.delegate;
                if (videoTimelineViewDelegate6 != null) {
                    videoTimelineViewDelegate6.didStopDragging(TYPE_LEFT);
                }
                this.pressedLeft = false;
                return true;
            } else if (this.pressedRight) {
                VideoTimelineViewDelegate videoTimelineViewDelegate7 = this.delegate;
                if (videoTimelineViewDelegate7 != null) {
                    videoTimelineViewDelegate7.didStopDragging(TYPE_RIGHT);
                }
                this.pressedRight = false;
                return true;
            } else if (this.pressedPlay) {
                VideoTimelineViewDelegate videoTimelineViewDelegate8 = this.delegate;
                if (videoTimelineViewDelegate8 != null) {
                    videoTimelineViewDelegate8.didStopDragging(TYPE_PROGRESS);
                }
                this.pressedPlay = false;
            }
        } else if (motionEvent.getAction() == 2) {
            if (this.pressedPlay) {
                float dp7 = ((float) (((int) (x - this.pressDx)) - AndroidUtilities.dp(16.0f))) / f;
                this.playProgress = dp7;
                float f2 = this.progressLeft;
                if (dp7 < f2) {
                    this.playProgress = f2;
                } else {
                    float f3 = this.progressRight;
                    if (dp7 > f3) {
                        this.playProgress = f3;
                    }
                }
                VideoTimelineViewDelegate videoTimelineViewDelegate9 = this.delegate;
                if (videoTimelineViewDelegate9 != null) {
                    videoTimelineViewDelegate9.onPlayProgressChanged(this.playProgress);
                }
                invalidate();
                return true;
            } else if (this.pressedLeft) {
                int i = (int) (x - this.pressDx);
                if (i < AndroidUtilities.dp(16.0f)) {
                    dp3 = AndroidUtilities.dp(16.0f);
                } else if (i <= dp3) {
                    dp3 = i;
                }
                float dp8 = ((float) (dp3 - AndroidUtilities.dp(16.0f))) / f;
                this.progressLeft = dp8;
                float f4 = this.progressRight;
                float f5 = this.maxProgressDiff;
                if (f4 - dp8 > f5) {
                    this.progressRight = dp8 + f5;
                } else {
                    float f6 = this.minProgressDiff;
                    if (f6 != 0.0f && f4 - dp8 < f6) {
                        float f7 = f4 - f6;
                        this.progressLeft = f7;
                        if (f7 < 0.0f) {
                            this.progressLeft = 0.0f;
                        }
                    }
                }
                float f8 = this.progressLeft;
                float f9 = this.playProgress;
                if (f8 > f9) {
                    this.playProgress = f8;
                } else {
                    float var_ = this.progressRight;
                    if (var_ < f9) {
                        this.playProgress = var_;
                    }
                }
                VideoTimelineViewDelegate videoTimelineViewDelegate10 = this.delegate;
                if (videoTimelineViewDelegate10 != null) {
                    videoTimelineViewDelegate10.onLeftProgressChanged(this.progressLeft);
                }
                invalidate();
                return true;
            } else if (this.pressedRight) {
                int i2 = (int) (x - this.pressDx);
                if (i2 >= dp) {
                    dp = i2 > AndroidUtilities.dp(16.0f) + measuredWidth ? measuredWidth + AndroidUtilities.dp(16.0f) : i2;
                }
                float dp9 = ((float) (dp - AndroidUtilities.dp(16.0f))) / f;
                this.progressRight = dp9;
                float var_ = this.progressLeft;
                float var_ = this.maxProgressDiff;
                if (dp9 - var_ > var_) {
                    this.progressLeft = dp9 - var_;
                } else {
                    float var_ = this.minProgressDiff;
                    if (var_ != 0.0f && dp9 - var_ < var_) {
                        float var_ = var_ + var_;
                        this.progressRight = var_;
                        if (var_ > 1.0f) {
                            this.progressRight = 1.0f;
                        }
                    }
                }
                float var_ = this.progressLeft;
                float var_ = this.playProgress;
                if (var_ > var_) {
                    this.playProgress = var_;
                } else {
                    float var_ = this.progressRight;
                    if (var_ < var_) {
                        this.playProgress = var_;
                    }
                }
                VideoTimelineViewDelegate videoTimelineViewDelegate11 = this.delegate;
                if (videoTimelineViewDelegate11 != null) {
                    videoTimelineViewDelegate11.onRightProgressChanged(this.progressRight);
                }
                invalidate();
                return true;
            }
        }
        return true;
    }

    public void setVideoPath(String str, float f, float f2) {
        destroy();
        MediaMetadataRetriever mediaMetadataRetriever2 = new MediaMetadataRetriever();
        this.mediaMetadataRetriever = mediaMetadataRetriever2;
        this.progressLeft = f;
        this.progressRight = f2;
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
                this.frameHeight = AndroidUtilities.dp(40.0f);
                this.framesToLoad = (getMeasuredWidth() - AndroidUtilities.dp(16.0f)) / this.frameHeight;
                this.frameWidth = (int) Math.ceil((double) (((float) (getMeasuredWidth() - AndroidUtilities.dp(16.0f))) / ((float) this.framesToLoad)));
                this.frameTimeOffset = this.videoLength / ((long) this.framesToLoad);
            }
            AnonymousClass1 r0 = new AsyncTask<Integer, Integer, Bitmap>() {
                private int frameNum = 0;

                /* access modifiers changed from: protected */
                public Bitmap doInBackground(Integer... numArr) {
                    this.frameNum = numArr[0].intValue();
                    Bitmap bitmap = null;
                    if (isCancelled()) {
                        return null;
                    }
                    try {
                        Bitmap frameAtTime = VideoTimelinePlayView.this.mediaMetadataRetriever.getFrameAtTime(VideoTimelinePlayView.this.frameTimeOffset * ((long) this.frameNum) * 1000, 2);
                        try {
                            if (isCancelled()) {
                                return null;
                            }
                            if (frameAtTime == null) {
                                return frameAtTime;
                            }
                            Bitmap createBitmap = Bitmap.createBitmap(VideoTimelinePlayView.this.frameWidth, VideoTimelinePlayView.this.frameHeight, frameAtTime.getConfig());
                            Canvas canvas = new Canvas(createBitmap);
                            float max = Math.max(((float) VideoTimelinePlayView.this.frameWidth) / ((float) frameAtTime.getWidth()), ((float) VideoTimelinePlayView.this.frameHeight) / ((float) frameAtTime.getHeight()));
                            int width = (int) (((float) frameAtTime.getWidth()) * max);
                            int height = (int) (((float) frameAtTime.getHeight()) * max);
                            canvas.drawBitmap(frameAtTime, new Rect(0, 0, frameAtTime.getWidth(), frameAtTime.getHeight()), new Rect((VideoTimelinePlayView.this.frameWidth - width) / 2, (VideoTimelinePlayView.this.frameHeight - height) / 2, width, height), (Paint) null);
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
                        VideoTimelinePlayView.this.frames.add(bitmap);
                        VideoTimelinePlayView.this.invalidate();
                        if (this.frameNum < VideoTimelinePlayView.this.framesToLoad) {
                            VideoTimelinePlayView.this.reloadFrames(this.frameNum + 1);
                        }
                    }
                }
            };
            this.currentTask = r0;
            r0.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Integer[]{Integer.valueOf(i), null, null});
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
        for (int i = 0; i < this.frames.size(); i++) {
            Bitmap bitmap = this.frames.get(i);
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        this.frames.clear();
        AsyncTask<Integer, Integer, Bitmap> asyncTask = this.currentTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.currentTask = null;
        }
    }

    public boolean isDragging() {
        return this.pressedPlay;
    }

    public void setProgress(float f) {
        this.playProgress = f;
        invalidate();
    }

    public void clearFrames() {
        for (int i = 0; i < this.frames.size(); i++) {
            Bitmap bitmap = this.frames.get(i);
            if (bitmap != null) {
                bitmap.recycle();
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
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int size = View.MeasureSpec.getSize(i);
        if (this.lastWidth != size) {
            clearFrames();
            this.lastWidth = size;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(32.0f);
        float f = (float) measuredWidth;
        int dp = ((int) (this.progressLeft * f)) + AndroidUtilities.dp(16.0f);
        int dp2 = ((int) (this.progressRight * f)) + AndroidUtilities.dp(16.0f);
        canvas.save();
        canvas2.clipRect(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(20.0f) + measuredWidth, AndroidUtilities.dp(48.0f));
        if (!this.frames.isEmpty() || this.currentTask != null) {
            int i = 0;
            for (int i2 = 0; i2 < this.frames.size(); i2++) {
                Bitmap bitmap = this.frames.get(i2);
                if (bitmap != null) {
                    canvas2.drawBitmap(bitmap, (float) (AndroidUtilities.dp(16.0f) + (this.frameWidth * i)), (float) AndroidUtilities.dp(6.0f), (Paint) null);
                }
                i++;
            }
        } else {
            reloadFrames(0);
        }
        int dp3 = AndroidUtilities.dp(6.0f);
        int dp4 = AndroidUtilities.dp(48.0f);
        float f2 = (float) dp3;
        float f3 = (float) dp;
        Canvas canvas3 = canvas;
        canvas3.drawRect((float) AndroidUtilities.dp(16.0f), f2, f3, (float) AndroidUtilities.dp(46.0f), this.paint2);
        canvas.drawRect((float) (AndroidUtilities.dp(4.0f) + dp2), f2, (float) (AndroidUtilities.dp(16.0f) + measuredWidth + AndroidUtilities.dp(4.0f)), (float) AndroidUtilities.dp(46.0f), this.paint2);
        float f4 = (float) dp4;
        float f5 = f4;
        canvas.drawRect(f3, (float) AndroidUtilities.dp(4.0f), (float) (AndroidUtilities.dp(2.0f) + dp), f5, this.paint);
        canvas.drawRect((float) (AndroidUtilities.dp(2.0f) + dp2), (float) AndroidUtilities.dp(4.0f), (float) (AndroidUtilities.dp(4.0f) + dp2), f5, this.paint);
        canvas.drawRect((float) (AndroidUtilities.dp(2.0f) + dp), (float) AndroidUtilities.dp(4.0f), (float) (AndroidUtilities.dp(4.0f) + dp2), f2, this.paint);
        canvas.drawRect((float) (AndroidUtilities.dp(2.0f) + dp), (float) (dp4 - AndroidUtilities.dp(2.0f)), (float) (AndroidUtilities.dp(4.0f) + dp2), f4, this.paint);
        canvas.restore();
        this.rect3.set((float) (dp - AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(4.0f), (float) (AndroidUtilities.dp(2.0f) + dp), f4);
        canvas2.drawRoundRect(this.rect3, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), this.paint);
        this.drawableLeft.setBounds(dp - AndroidUtilities.dp(8.0f), AndroidUtilities.dp(4.0f) + ((AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(18.0f)) / 2), dp + AndroidUtilities.dp(2.0f), ((AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(18.0f)) / 2) + AndroidUtilities.dp(22.0f));
        this.drawableLeft.draw(canvas2);
        this.rect3.set((float) (AndroidUtilities.dp(2.0f) + dp2), (float) AndroidUtilities.dp(4.0f), (float) (AndroidUtilities.dp(12.0f) + dp2), f4);
        canvas2.drawRoundRect(this.rect3, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), this.paint);
        this.drawableRight.setBounds(AndroidUtilities.dp(2.0f) + dp2, AndroidUtilities.dp(4.0f) + ((AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(18.0f)) / 2), dp2 + AndroidUtilities.dp(12.0f), ((AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(18.0f)) / 2) + AndroidUtilities.dp(22.0f));
        this.drawableRight.draw(canvas2);
        float dp5 = ((float) AndroidUtilities.dp(18.0f)) + (f * this.playProgress);
        this.rect3.set(dp5 - ((float) AndroidUtilities.dp(1.5f)), (float) AndroidUtilities.dp(2.0f), ((float) AndroidUtilities.dp(1.5f)) + dp5, (float) AndroidUtilities.dp(50.0f));
        canvas2.drawRoundRect(this.rect3, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), this.paint2);
        canvas2.drawCircle(dp5, (float) AndroidUtilities.dp(52.0f), (float) AndroidUtilities.dp(3.5f), this.paint2);
        this.rect3.set(dp5 - ((float) AndroidUtilities.dp(1.0f)), (float) AndroidUtilities.dp(2.0f), ((float) AndroidUtilities.dp(1.0f)) + dp5, (float) AndroidUtilities.dp(50.0f));
        canvas2.drawRoundRect(this.rect3, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), this.paint);
        canvas2.drawCircle(dp5, (float) AndroidUtilities.dp(52.0f), (float) AndroidUtilities.dp(3.0f), this.paint);
    }
}
