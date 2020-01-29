package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class VideoTimelineView extends View {
    private static final Object sync = new Object();
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
    public int framesToLoad;
    private boolean isRoundFrames;
    private float maxProgressDiff = 1.0f;
    /* access modifiers changed from: private */
    public MediaMetadataRetriever mediaMetadataRetriever;
    private float minProgressDiff = 0.0f;
    private Paint paint = new Paint(1);
    private Paint paint2;
    private float pressDx;
    private boolean pressedLeft;
    private boolean pressedRight;
    private float progressLeft;
    private float progressRight = 1.0f;
    private Rect rect1;
    private Rect rect2;
    private long videoLength;

    public interface VideoTimelineViewDelegate {
        void didStartDragging();

        void didStopDragging();

        void onLeftProgressChanged(float f);

        void onRightProgressChanged(float f);
    }

    public VideoTimelineView(Context context) {
        super(context);
        this.paint.setColor(-1);
        this.paint2 = new Paint();
        this.paint2.setColor(NUM);
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
        float f4 = this.maxProgressDiff;
        if (f2 - f3 > f4) {
            this.progressRight = f3 + f4;
            invalidate();
        }
    }

    public void setRoundFrames(boolean z) {
        this.isRoundFrames = z;
        if (this.isRoundFrames) {
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
        int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(32.0f);
        float f = (float) measuredWidth;
        int dp = ((int) (this.progressLeft * f)) + AndroidUtilities.dp(16.0f);
        int dp2 = ((int) (this.progressRight * f)) + AndroidUtilities.dp(16.0f);
        if (motionEvent.getAction() == 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (this.mediaMetadataRetriever == null) {
                return false;
            }
            int dp3 = AndroidUtilities.dp(12.0f);
            if (((float) (dp - dp3)) <= x && x <= ((float) (dp + dp3)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                VideoTimelineViewDelegate videoTimelineViewDelegate = this.delegate;
                if (videoTimelineViewDelegate != null) {
                    videoTimelineViewDelegate.didStartDragging();
                }
                this.pressedLeft = true;
                this.pressDx = (float) ((int) (x - ((float) dp)));
                invalidate();
                return true;
            } else if (((float) (dp2 - dp3)) <= x && x <= ((float) (dp3 + dp2)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                VideoTimelineViewDelegate videoTimelineViewDelegate2 = this.delegate;
                if (videoTimelineViewDelegate2 != null) {
                    videoTimelineViewDelegate2.didStartDragging();
                }
                this.pressedRight = true;
                this.pressDx = (float) ((int) (x - ((float) dp2)));
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
                return true;
            } else if (this.pressedRight) {
                VideoTimelineViewDelegate videoTimelineViewDelegate4 = this.delegate;
                if (videoTimelineViewDelegate4 != null) {
                    videoTimelineViewDelegate4.didStopDragging();
                }
                this.pressedRight = false;
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
                this.progressLeft = ((float) (dp2 - AndroidUtilities.dp(16.0f))) / f;
                float f2 = this.progressRight;
                float f3 = this.progressLeft;
                float f4 = this.maxProgressDiff;
                if (f2 - f3 > f4) {
                    this.progressRight = f3 + f4;
                } else {
                    float f5 = this.minProgressDiff;
                    if (f5 != 0.0f && f2 - f3 < f5) {
                        this.progressLeft = f2 - f5;
                        if (this.progressLeft < 0.0f) {
                            this.progressLeft = 0.0f;
                        }
                    }
                }
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
                this.progressRight = ((float) (dp - AndroidUtilities.dp(16.0f))) / f;
                float f6 = this.progressRight;
                float f7 = this.progressLeft;
                float f8 = this.maxProgressDiff;
                if (f6 - f7 > f8) {
                    this.progressLeft = f6 - f8;
                } else {
                    float f9 = this.minProgressDiff;
                    if (f9 != 0.0f && f6 - f7 < f9) {
                        this.progressRight = f7 + f9;
                        if (this.progressRight > 1.0f) {
                            this.progressRight = 1.0f;
                        }
                    }
                }
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
        this.mediaMetadataRetriever = new MediaMetadataRetriever();
        this.progressLeft = 0.0f;
        this.progressRight = 1.0f;
        try {
            this.mediaMetadataRetriever.setDataSource(str);
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
                    this.framesToLoad = (int) Math.ceil((double) (((float) (getMeasuredWidth() - AndroidUtilities.dp(16.0f))) / (((float) this.frameHeight) / 2.0f)));
                } else {
                    this.frameHeight = AndroidUtilities.dp(40.0f);
                    this.framesToLoad = (getMeasuredWidth() - AndroidUtilities.dp(16.0f)) / this.frameHeight;
                    this.frameWidth = (int) Math.ceil((double) (((float) (getMeasuredWidth() - AndroidUtilities.dp(16.0f))) / ((float) this.framesToLoad)));
                }
                this.frameTimeOffset = this.videoLength / ((long) this.framesToLoad);
            }
            this.currentTask = new AsyncTask<Integer, Integer, Bitmap>() {
                private int frameNum = 0;

                /* access modifiers changed from: protected */
                public Bitmap doInBackground(Integer... numArr) {
                    Bitmap bitmap;
                    this.frameNum = numArr[0].intValue();
                    if (isCancelled()) {
                        return null;
                    }
                    try {
                        bitmap = VideoTimelineView.this.mediaMetadataRetriever.getFrameAtTime(VideoTimelineView.this.frameTimeOffset * ((long) this.frameNum) * 1000, 2);
                        try {
                            if (isCancelled()) {
                                return null;
                            }
                            if (bitmap == null) {
                                return bitmap;
                            }
                            Bitmap createBitmap = Bitmap.createBitmap(VideoTimelineView.this.frameWidth, VideoTimelineView.this.frameHeight, bitmap.getConfig());
                            Canvas canvas = new Canvas(createBitmap);
                            float access$200 = ((float) VideoTimelineView.this.frameWidth) / ((float) bitmap.getWidth());
                            float access$300 = ((float) VideoTimelineView.this.frameHeight) / ((float) bitmap.getHeight());
                            if (access$200 <= access$300) {
                                access$200 = access$300;
                            }
                            int width = (int) (((float) bitmap.getWidth()) * access$200);
                            int height = (int) (((float) bitmap.getHeight()) * access$200);
                            canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect((VideoTimelineView.this.frameWidth - width) / 2, (VideoTimelineView.this.frameHeight - height) / 2, width, height), (Paint) null);
                            bitmap.recycle();
                            return createBitmap;
                        } catch (Exception e) {
                            e = e;
                            FileLog.e((Throwable) e);
                            return bitmap;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        bitmap = null;
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
                        }
                    }
                }
            };
            this.currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Integer[]{Integer.valueOf(i), null, null});
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
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(36.0f);
        float f = (float) measuredWidth;
        int dp = ((int) (this.progressLeft * f)) + AndroidUtilities.dp(16.0f);
        int dp2 = ((int) (f * this.progressRight)) + AndroidUtilities.dp(16.0f);
        canvas.save();
        canvas2.clipRect(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(20.0f) + measuredWidth, getMeasuredHeight());
        if (!this.frames.isEmpty() || this.currentTask != null) {
            int i = 0;
            for (int i2 = 0; i2 < this.frames.size(); i2++) {
                Bitmap bitmap = this.frames.get(i2);
                if (bitmap != null) {
                    int dp3 = AndroidUtilities.dp(16.0f) + ((this.isRoundFrames ? this.frameWidth / 2 : this.frameWidth) * i);
                    int dp4 = AndroidUtilities.dp(2.0f);
                    if (this.isRoundFrames) {
                        this.rect2.set(dp3, dp4, AndroidUtilities.dp(28.0f) + dp3, AndroidUtilities.dp(28.0f) + dp4);
                        canvas2.drawBitmap(bitmap, this.rect1, this.rect2, (Paint) null);
                    } else {
                        canvas2.drawBitmap(bitmap, (float) dp3, (float) dp4, (Paint) null);
                    }
                }
                i++;
            }
        } else {
            reloadFrames(0);
        }
        int dp5 = AndroidUtilities.dp(2.0f);
        float f2 = (float) dp5;
        float f3 = (float) dp;
        canvas.drawRect((float) AndroidUtilities.dp(16.0f), f2, f3, (float) (getMeasuredHeight() - dp5), this.paint2);
        canvas.drawRect((float) (AndroidUtilities.dp(4.0f) + dp2), f2, (float) (AndroidUtilities.dp(16.0f) + measuredWidth + AndroidUtilities.dp(4.0f)), (float) (getMeasuredHeight() - dp5), this.paint2);
        canvas.drawRect(f3, 0.0f, (float) (AndroidUtilities.dp(2.0f) + dp), (float) getMeasuredHeight(), this.paint);
        canvas.drawRect((float) (AndroidUtilities.dp(2.0f) + dp2), 0.0f, (float) (AndroidUtilities.dp(4.0f) + dp2), (float) getMeasuredHeight(), this.paint);
        canvas.drawRect((float) (AndroidUtilities.dp(2.0f) + dp), 0.0f, (float) (AndroidUtilities.dp(4.0f) + dp2), f2, this.paint);
        canvas.drawRect((float) (dp + AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - dp5), (float) (AndroidUtilities.dp(4.0f) + dp2), (float) getMeasuredHeight(), this.paint);
        canvas.restore();
        canvas2.drawCircle(f3, (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(7.0f), this.paint);
        canvas2.drawCircle((float) (dp2 + AndroidUtilities.dp(4.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(7.0f), this.paint);
    }
}
