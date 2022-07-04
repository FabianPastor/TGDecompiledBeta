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
    public static final int MODE_AVATAR = 1;
    public static final int MODE_VIDEO = 0;
    public static int TYPE_LEFT = 0;
    public static int TYPE_PROGRESS = 2;
    public static int TYPE_RIGHT = 1;
    private static final Object sync = new Object();
    Paint bitmapPaint = new Paint();
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
    public ArrayList<BitmapFrame> frames = new ArrayList<>();
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

    public void setMinProgressDiff(float value) {
        this.minProgressDiff = value;
    }

    public void setMode(int mode) {
        if (this.currentMode != mode) {
            this.currentMode = mode;
            invalidate();
        }
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

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (Build.VERSION.SDK_INT >= 29) {
            this.exclustionRect.set(left, 0, right, getMeasuredHeight());
            setSystemGestureExclusionRects(this.exclusionRects);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        int width = getMeasuredWidth() - AndroidUtilities.dp(32.0f);
        int startX = ((int) (((float) width) * this.progressLeft)) + AndroidUtilities.dp(16.0f);
        int playX = ((int) (((float) width) * this.playProgress)) + AndroidUtilities.dp(16.0f);
        int endX = ((int) (((float) width) * this.progressRight)) + AndroidUtilities.dp(16.0f);
        if (event.getAction() == 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (this.mediaMetadataRetriever == null) {
                return false;
            }
            int additionWidth = AndroidUtilities.dp(16.0f);
            int additionWidthPlay = AndroidUtilities.dp(8.0f);
            if (endX != startX && ((float) (playX - additionWidthPlay)) <= x && x <= ((float) (playX + additionWidthPlay)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                VideoTimelineViewDelegate videoTimelineViewDelegate = this.delegate;
                if (videoTimelineViewDelegate != null) {
                    videoTimelineViewDelegate.didStartDragging(TYPE_PROGRESS);
                }
                this.pressedPlay = true;
                this.pressDx = (float) ((int) (x - ((float) playX)));
                invalidate();
                return true;
            } else if (((float) (startX - additionWidth)) <= x && x <= ((float) Math.min(startX + additionWidth, endX)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                VideoTimelineViewDelegate videoTimelineViewDelegate2 = this.delegate;
                if (videoTimelineViewDelegate2 != null) {
                    videoTimelineViewDelegate2.didStartDragging(TYPE_LEFT);
                }
                this.pressedLeft = true;
                this.pressDx = (float) ((int) (x - ((float) startX)));
                invalidate();
                return true;
            } else if (((float) (endX - additionWidth)) <= x && x <= ((float) (endX + additionWidth)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                VideoTimelineViewDelegate videoTimelineViewDelegate3 = this.delegate;
                if (videoTimelineViewDelegate3 != null) {
                    videoTimelineViewDelegate3.didStartDragging(TYPE_RIGHT);
                }
                this.pressedRight = true;
                this.pressDx = (float) ((int) (x - ((float) endX)));
                invalidate();
                return true;
            } else if (((float) startX) <= x && x <= ((float) endX) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                VideoTimelineViewDelegate videoTimelineViewDelegate4 = this.delegate;
                if (videoTimelineViewDelegate4 != null) {
                    videoTimelineViewDelegate4.didStartDragging(TYPE_PROGRESS);
                }
                this.pressedPlay = true;
                float dp = (x - ((float) AndroidUtilities.dp(16.0f))) / ((float) width);
                this.playProgress = dp;
                VideoTimelineViewDelegate videoTimelineViewDelegate5 = this.delegate;
                if (videoTimelineViewDelegate5 != null) {
                    videoTimelineViewDelegate5.onPlayProgressChanged(dp);
                }
                this.pressDx = 0.0f;
                invalidate();
                return true;
            }
        } else if (event.getAction() == 1 || event.getAction() == 3) {
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
                return true;
            }
        } else if (event.getAction() == 2) {
            if (this.pressedPlay) {
                float dp2 = ((float) (((int) (x - this.pressDx)) - AndroidUtilities.dp(16.0f))) / ((float) width);
                this.playProgress = dp2;
                float f = this.progressLeft;
                if (dp2 < f) {
                    this.playProgress = f;
                } else {
                    float f2 = this.progressRight;
                    if (dp2 > f2) {
                        this.playProgress = f2;
                    }
                }
                VideoTimelineViewDelegate videoTimelineViewDelegate9 = this.delegate;
                if (videoTimelineViewDelegate9 != null) {
                    videoTimelineViewDelegate9.onPlayProgressChanged(this.playProgress);
                }
                invalidate();
                return true;
            } else if (this.pressedLeft != 0) {
                int startX2 = (int) (x - this.pressDx);
                if (startX2 < AndroidUtilities.dp(16.0f)) {
                    startX2 = AndroidUtilities.dp(16.0f);
                } else if (startX2 > endX) {
                    startX2 = endX;
                }
                float dp3 = ((float) (startX2 - AndroidUtilities.dp(16.0f))) / ((float) width);
                this.progressLeft = dp3;
                float f3 = this.progressRight;
                float f4 = this.maxProgressDiff;
                if (f3 - dp3 > f4) {
                    this.progressRight = dp3 + f4;
                } else {
                    float f5 = this.minProgressDiff;
                    if (f5 != 0.0f && f3 - dp3 < f5) {
                        float f6 = f3 - f5;
                        this.progressLeft = f6;
                        if (f6 < 0.0f) {
                            this.progressLeft = 0.0f;
                        }
                    }
                }
                float f7 = this.progressLeft;
                float f8 = this.playProgress;
                if (f7 > f8) {
                    this.playProgress = f7;
                } else {
                    float f9 = this.progressRight;
                    if (f9 < f8) {
                        this.playProgress = f9;
                    }
                }
                VideoTimelineViewDelegate videoTimelineViewDelegate10 = this.delegate;
                if (videoTimelineViewDelegate10 != null) {
                    videoTimelineViewDelegate10.onLeftProgressChanged(f7);
                }
                invalidate();
                return true;
            } else if (this.pressedRight != 0) {
                int endX2 = (int) (x - this.pressDx);
                if (endX2 < startX) {
                    endX2 = startX;
                } else if (endX2 > AndroidUtilities.dp(16.0f) + width) {
                    endX2 = width + AndroidUtilities.dp(16.0f);
                }
                float dp4 = ((float) (endX2 - AndroidUtilities.dp(16.0f))) / ((float) width);
                this.progressRight = dp4;
                float var_ = this.progressLeft;
                float var_ = this.maxProgressDiff;
                if (dp4 - var_ > var_) {
                    this.progressLeft = dp4 - var_;
                } else {
                    float var_ = this.minProgressDiff;
                    if (var_ != 0.0f && dp4 - var_ < var_) {
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

    public void setVideoPath(String path, float left, float right) {
        destroy();
        MediaMetadataRetriever mediaMetadataRetriever2 = new MediaMetadataRetriever();
        this.mediaMetadataRetriever = mediaMetadataRetriever2;
        this.progressLeft = left;
        this.progressRight = right;
        try {
            mediaMetadataRetriever2.setDataSource(path);
            this.videoLength = Long.parseLong(this.mediaMetadataRetriever.extractMetadata(9));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        invalidate();
    }

    public void setRightProgress(float value) {
        this.progressRight = value;
        VideoTimelineViewDelegate videoTimelineViewDelegate = this.delegate;
        if (videoTimelineViewDelegate != null) {
            videoTimelineViewDelegate.didStartDragging(TYPE_RIGHT);
        }
        VideoTimelineViewDelegate videoTimelineViewDelegate2 = this.delegate;
        if (videoTimelineViewDelegate2 != null) {
            videoTimelineViewDelegate2.onRightProgressChanged(this.progressRight);
        }
        VideoTimelineViewDelegate videoTimelineViewDelegate3 = this.delegate;
        if (videoTimelineViewDelegate3 != null) {
            videoTimelineViewDelegate3.didStopDragging(TYPE_RIGHT);
        }
        invalidate();
    }

    public void setDelegate(VideoTimelineViewDelegate delegate2) {
        this.delegate = delegate2;
    }

    /* access modifiers changed from: private */
    public void reloadFrames(int frameNum) {
        if (this.mediaMetadataRetriever != null) {
            if (frameNum == 0) {
                this.frameHeight = AndroidUtilities.dp(40.0f);
                this.framesToLoad = Math.max(1, (getMeasuredWidth() - AndroidUtilities.dp(16.0f)) / this.frameHeight);
                this.frameWidth = (int) Math.ceil((double) (((float) (getMeasuredWidth() - AndroidUtilities.dp(16.0f))) / ((float) this.framesToLoad)));
                this.frameTimeOffset = this.videoLength / ((long) this.framesToLoad);
            }
            AnonymousClass1 r1 = new AsyncTask<Integer, Integer, Bitmap>() {
                private int frameNum = 0;

                /* access modifiers changed from: protected */
                public Bitmap doInBackground(Integer... objects) {
                    this.frameNum = objects[0].intValue();
                    if (isCancelled()) {
                        return null;
                    }
                    try {
                        Bitmap bitmap = VideoTimelinePlayView.this.mediaMetadataRetriever.getFrameAtTime(VideoTimelinePlayView.this.frameTimeOffset * ((long) this.frameNum) * 1000, 2);
                        if (isCancelled()) {
                            return null;
                        }
                        if (bitmap == null) {
                            return bitmap;
                        }
                        Bitmap result = Bitmap.createBitmap(VideoTimelinePlayView.this.frameWidth, VideoTimelinePlayView.this.frameHeight, bitmap.getConfig());
                        Canvas canvas = new Canvas(result);
                        float scale = Math.max(((float) VideoTimelinePlayView.this.frameWidth) / ((float) bitmap.getWidth()), ((float) VideoTimelinePlayView.this.frameHeight) / ((float) bitmap.getHeight()));
                        int w = (int) (((float) bitmap.getWidth()) * scale);
                        int h = (int) (((float) bitmap.getHeight()) * scale);
                        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect((VideoTimelinePlayView.this.frameWidth - w) / 2, (VideoTimelinePlayView.this.frameHeight - h) / 2, w, h), (Paint) null);
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
                        VideoTimelinePlayView.this.frames.add(new BitmapFrame(bitmap));
                        VideoTimelinePlayView.this.invalidate();
                        if (this.frameNum < VideoTimelinePlayView.this.framesToLoad) {
                            VideoTimelinePlayView.this.reloadFrames(this.frameNum + 1);
                        }
                    }
                }
            };
            this.currentTask = r1;
            r1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Integer[]{Integer.valueOf(frameNum), null, null});
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
        for (int a = 0; a < this.frames.size(); a++) {
            BitmapFrame bitmap = this.frames.get(a);
            if (!(bitmap == null || bitmap.bitmap == null)) {
                bitmap.bitmap.recycle();
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

    public void setProgress(float value) {
        this.playProgress = value;
        invalidate();
    }

    public void clearFrames() {
        for (int a = 0; a < this.frames.size(); a++) {
            BitmapFrame frame = this.frames.get(a);
            if (frame != null) {
                frame.bitmap.recycle();
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        if (this.lastWidth != widthSize) {
            clearFrames();
            this.lastWidth = widthSize;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        int width = getMeasuredWidth() - AndroidUtilities.dp(32.0f);
        float f = 16.0f;
        int startX = ((int) (((float) width) * this.progressLeft)) + AndroidUtilities.dp(16.0f);
        int endX = ((int) (((float) width) * this.progressRight)) + AndroidUtilities.dp(16.0f);
        canvas.save();
        canvas2.clipRect(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(20.0f) + width, AndroidUtilities.dp(48.0f));
        float f2 = 1.0f;
        if (!this.frames.isEmpty() || this.currentTask != null) {
            int offset = 0;
            int a = 0;
            while (a < this.frames.size()) {
                BitmapFrame bitmap = this.frames.get(a);
                if (bitmap.bitmap != null) {
                    int x = AndroidUtilities.dp(f) + (this.frameWidth * offset);
                    int y = AndroidUtilities.dp(6.0f);
                    if (bitmap.alpha != f2) {
                        bitmap.alpha += 0.16f;
                        if (bitmap.alpha > f2) {
                            bitmap.alpha = f2;
                        } else {
                            invalidate();
                        }
                        this.bitmapPaint.setAlpha((int) (bitmap.alpha * 255.0f));
                        canvas2.drawBitmap(bitmap.bitmap, (float) x, (float) y, this.bitmapPaint);
                    } else {
                        canvas2.drawBitmap(bitmap.bitmap, (float) x, (float) y, (Paint) null);
                    }
                }
                offset++;
                a++;
                f = 16.0f;
                f2 = 1.0f;
            }
        } else {
            reloadFrames(0);
        }
        int top = AndroidUtilities.dp(6.0f);
        int end = AndroidUtilities.dp(48.0f);
        canvas.drawRect((float) AndroidUtilities.dp(16.0f), (float) top, (float) startX, (float) AndroidUtilities.dp(46.0f), this.paint2);
        canvas.drawRect((float) (AndroidUtilities.dp(4.0f) + endX), (float) top, (float) (AndroidUtilities.dp(16.0f) + width + AndroidUtilities.dp(4.0f)), (float) AndroidUtilities.dp(46.0f), this.paint2);
        canvas.drawRect((float) startX, (float) AndroidUtilities.dp(4.0f), (float) (AndroidUtilities.dp(2.0f) + startX), (float) end, this.paint);
        canvas.drawRect((float) (AndroidUtilities.dp(2.0f) + endX), (float) AndroidUtilities.dp(4.0f), (float) (AndroidUtilities.dp(4.0f) + endX), (float) end, this.paint);
        canvas.drawRect((float) (AndroidUtilities.dp(2.0f) + startX), (float) AndroidUtilities.dp(4.0f), (float) (AndroidUtilities.dp(4.0f) + endX), (float) top, this.paint);
        canvas.drawRect((float) (AndroidUtilities.dp(2.0f) + startX), (float) (end - AndroidUtilities.dp(2.0f)), (float) (AndroidUtilities.dp(4.0f) + endX), (float) end, this.paint);
        canvas.restore();
        this.rect3.set((float) (startX - AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(4.0f), (float) (AndroidUtilities.dp(2.0f) + startX), (float) end);
        canvas2.drawRoundRect(this.rect3, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), this.paint);
        this.drawableLeft.setBounds(startX - AndroidUtilities.dp(8.0f), AndroidUtilities.dp(4.0f) + ((AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(18.0f)) / 2), AndroidUtilities.dp(2.0f) + startX, ((AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(18.0f)) / 2) + AndroidUtilities.dp(22.0f));
        this.drawableLeft.draw(canvas2);
        this.rect3.set((float) (AndroidUtilities.dp(2.0f) + endX), (float) AndroidUtilities.dp(4.0f), (float) (AndroidUtilities.dp(12.0f) + endX), (float) end);
        canvas2.drawRoundRect(this.rect3, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), this.paint);
        this.drawableRight.setBounds(AndroidUtilities.dp(2.0f) + endX, AndroidUtilities.dp(4.0f) + ((AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(18.0f)) / 2), AndroidUtilities.dp(12.0f) + endX, ((AndroidUtilities.dp(44.0f) - AndroidUtilities.dp(18.0f)) / 2) + AndroidUtilities.dp(22.0f));
        this.drawableRight.draw(canvas2);
        float cx = ((float) AndroidUtilities.dp(18.0f)) + (((float) width) * this.playProgress);
        this.rect3.set(cx - ((float) AndroidUtilities.dp(1.5f)), (float) AndroidUtilities.dp(2.0f), ((float) AndroidUtilities.dp(1.5f)) + cx, (float) AndroidUtilities.dp(50.0f));
        canvas2.drawRoundRect(this.rect3, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), this.paint2);
        canvas2.drawCircle(cx, (float) AndroidUtilities.dp(52.0f), (float) AndroidUtilities.dp(3.5f), this.paint2);
        this.rect3.set(cx - ((float) AndroidUtilities.dp(1.0f)), (float) AndroidUtilities.dp(2.0f), ((float) AndroidUtilities.dp(1.0f)) + cx, (float) AndroidUtilities.dp(50.0f));
        canvas2.drawRoundRect(this.rect3, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), this.paint);
        canvas2.drawCircle(cx, (float) AndroidUtilities.dp(52.0f), (float) AndroidUtilities.dp(3.0f), this.paint);
    }

    private static class BitmapFrame {
        float alpha;
        Bitmap bitmap;

        public BitmapFrame(Bitmap bitmap2) {
            this.bitmap = bitmap2;
        }
    }
}
