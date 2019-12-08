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
    private int frameHeight;
    private long frameTimeOffset;
    private int frameWidth;
    private ArrayList<Bitmap> frames = new ArrayList();
    private int framesToLoad;
    private boolean isRoundFrames;
    private float maxProgressDiff = 1.0f;
    private MediaMetadataRetriever mediaMetadataRetriever;
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
        f = this.progressRight;
        float f2 = this.progressLeft;
        f -= f2;
        float f3 = this.maxProgressDiff;
        if (f > f3) {
            this.progressRight = f2 + f3;
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
        int dp3;
        VideoTimelineViewDelegate videoTimelineViewDelegate;
        if (motionEvent.getAction() == 0) {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (this.mediaMetadataRetriever == null) {
                return false;
            }
            dp3 = AndroidUtilities.dp(12.0f);
            if (((float) (dp - dp3)) <= x && x <= ((float) (dp + dp3)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                videoTimelineViewDelegate = this.delegate;
                if (videoTimelineViewDelegate != null) {
                    videoTimelineViewDelegate.didStartDragging();
                }
                this.pressedLeft = true;
                this.pressDx = (float) ((int) (x - ((float) dp)));
                invalidate();
                return true;
            } else if (((float) (dp2 - dp3)) <= x && x <= ((float) (dp3 + dp2)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                videoTimelineViewDelegate = this.delegate;
                if (videoTimelineViewDelegate != null) {
                    videoTimelineViewDelegate.didStartDragging();
                }
                this.pressedRight = true;
                this.pressDx = (float) ((int) (x - ((float) dp2)));
                invalidate();
                return true;
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (this.pressedLeft) {
                videoTimelineViewDelegate = this.delegate;
                if (videoTimelineViewDelegate != null) {
                    videoTimelineViewDelegate.didStopDragging();
                }
                this.pressedLeft = false;
                return true;
            } else if (this.pressedRight) {
                videoTimelineViewDelegate = this.delegate;
                if (videoTimelineViewDelegate != null) {
                    videoTimelineViewDelegate.didStopDragging();
                }
                this.pressedRight = false;
                return true;
            }
        } else if (motionEvent.getAction() == 2) {
            float f2;
            float f3;
            if (this.pressedLeft) {
                dp3 = (int) (x - this.pressDx);
                if (dp3 < AndroidUtilities.dp(16.0f)) {
                    dp2 = AndroidUtilities.dp(16.0f);
                } else if (dp3 <= dp2) {
                    dp2 = dp3;
                }
                this.progressLeft = ((float) (dp2 - AndroidUtilities.dp(16.0f))) / f;
                f2 = this.progressRight;
                f3 = this.progressLeft;
                x = f2 - f3;
                y = this.maxProgressDiff;
                if (x > y) {
                    this.progressRight = f3 + y;
                } else {
                    x = this.minProgressDiff;
                    if (x != 0.0f && f2 - f3 < x) {
                        this.progressLeft = f2 - x;
                        if (this.progressLeft < 0.0f) {
                            this.progressLeft = 0.0f;
                        }
                    }
                }
                videoTimelineViewDelegate = this.delegate;
                if (videoTimelineViewDelegate != null) {
                    videoTimelineViewDelegate.onLeftProgressChanged(this.progressLeft);
                }
                invalidate();
                return true;
            } else if (this.pressedRight) {
                dp3 = (int) (x - this.pressDx);
                if (dp3 >= dp) {
                    dp = dp3 > AndroidUtilities.dp(16.0f) + measuredWidth ? measuredWidth + AndroidUtilities.dp(16.0f) : dp3;
                }
                this.progressRight = ((float) (dp - AndroidUtilities.dp(16.0f))) / f;
                f2 = this.progressRight;
                f3 = this.progressLeft;
                x = f2 - f3;
                y = this.maxProgressDiff;
                if (x > y) {
                    this.progressLeft = f2 - y;
                } else {
                    x = this.minProgressDiff;
                    if (x != 0.0f && f2 - f3 < x) {
                        this.progressRight = f3 + x;
                        if (this.progressRight > 1.0f) {
                            this.progressRight = 1.0f;
                        }
                    }
                }
                videoTimelineViewDelegate = this.delegate;
                if (videoTimelineViewDelegate != null) {
                    videoTimelineViewDelegate.onRightProgressChanged(this.progressRight);
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
            FileLog.e(e);
        }
        invalidate();
    }

    public void setDelegate(VideoTimelineViewDelegate videoTimelineViewDelegate) {
        this.delegate = videoTimelineViewDelegate;
    }

    private void reloadFrames(int i) {
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

                /* Access modifiers changed, original: protected|varargs */
                public Bitmap doInBackground(Integer... numArr) {
                    Throwable e;
                    this.frameNum = numArr[0].intValue();
                    if (isCancelled()) {
                        return null;
                    }
                    Bitmap frameAtTime;
                    try {
                        frameAtTime = VideoTimelineView.this.mediaMetadataRetriever.getFrameAtTime((VideoTimelineView.this.frameTimeOffset * ((long) this.frameNum)) * 1000, 2);
                        try {
                            if (isCancelled()) {
                                return null;
                            }
                            if (frameAtTime != null) {
                                Bitmap createBitmap = Bitmap.createBitmap(VideoTimelineView.this.frameWidth, VideoTimelineView.this.frameHeight, frameAtTime.getConfig());
                                Canvas canvas = new Canvas(createBitmap);
                                float access$200 = ((float) VideoTimelineView.this.frameWidth) / ((float) frameAtTime.getWidth());
                                float access$300 = ((float) VideoTimelineView.this.frameHeight) / ((float) frameAtTime.getHeight());
                                if (access$200 <= access$300) {
                                    access$200 = access$300;
                                }
                                int width = (int) (((float) frameAtTime.getWidth()) * access$200);
                                int height = (int) (((float) frameAtTime.getHeight()) * access$200);
                                canvas.drawBitmap(frameAtTime, new Rect(0, 0, frameAtTime.getWidth(), frameAtTime.getHeight()), new Rect((VideoTimelineView.this.frameWidth - width) / 2, (VideoTimelineView.this.frameHeight - height) / 2, width, height), null);
                                frameAtTime.recycle();
                                frameAtTime = createBitmap;
                            }
                            return frameAtTime;
                        } catch (Exception e2) {
                            e = e2;
                            FileLog.e(e);
                            return frameAtTime;
                        }
                    } catch (Exception e3) {
                        e = e3;
                        frameAtTime = null;
                        FileLog.e(e);
                        return frameAtTime;
                    }
                }

                /* Access modifiers changed, original: protected */
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
                FileLog.e(e);
            }
        }
        for (int i = 0; i < this.frames.size(); i++) {
            Bitmap bitmap = (Bitmap) this.frames.get(i);
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        this.frames.clear();
        AsyncTask asyncTask = this.currentTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.currentTask = null;
        }
    }

    public void clearFrames() {
        for (int i = 0; i < this.frames.size(); i++) {
            Bitmap bitmap = (Bitmap) this.frames.get(i);
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        this.frames.clear();
        AsyncTask asyncTask = this.currentTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.currentTask = null;
        }
        invalidate();
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(36.0f);
        float f = (float) measuredWidth;
        int dp = ((int) (this.progressLeft * f)) + AndroidUtilities.dp(16.0f);
        int dp2 = ((int) (f * this.progressRight)) + AndroidUtilities.dp(16.0f);
        canvas.save();
        int i = 0;
        canvas2.clipRect(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(20.0f) + measuredWidth, getMeasuredHeight());
        if (this.frames.isEmpty() && this.currentTask == null) {
            reloadFrames(0);
        } else {
            int i2 = 0;
            while (i < this.frames.size()) {
                Bitmap bitmap = (Bitmap) this.frames.get(i);
                if (bitmap != null) {
                    int dp3 = AndroidUtilities.dp(16.0f) + ((this.isRoundFrames ? this.frameWidth / 2 : this.frameWidth) * i2);
                    int dp4 = AndroidUtilities.dp(2.0f);
                    if (this.isRoundFrames) {
                        this.rect2.set(dp3, dp4, AndroidUtilities.dp(28.0f) + dp3, AndroidUtilities.dp(28.0f) + dp4);
                        canvas2.drawBitmap(bitmap, this.rect1, this.rect2, null);
                    } else {
                        canvas2.drawBitmap(bitmap, (float) dp3, (float) dp4, null);
                    }
                }
                i2++;
                i++;
            }
        }
        int dp5 = AndroidUtilities.dp(2.0f);
        float f2 = (float) dp5;
        float f3 = (float) dp;
        canvas.drawRect((float) AndroidUtilities.dp(16.0f), f2, f3, (float) (getMeasuredHeight() - dp5), this.paint2);
        canvas.drawRect((float) (AndroidUtilities.dp(4.0f) + dp2), f2, (float) ((AndroidUtilities.dp(16.0f) + measuredWidth) + AndroidUtilities.dp(4.0f)), (float) (getMeasuredHeight() - dp5), this.paint2);
        canvas.drawRect(f3, 0.0f, (float) (AndroidUtilities.dp(2.0f) + dp), (float) getMeasuredHeight(), this.paint);
        canvas.drawRect((float) (AndroidUtilities.dp(2.0f) + dp2), 0.0f, (float) (AndroidUtilities.dp(4.0f) + dp2), (float) getMeasuredHeight(), this.paint);
        canvas.drawRect((float) (AndroidUtilities.dp(2.0f) + dp), 0.0f, (float) (AndroidUtilities.dp(4.0f) + dp2), f2, this.paint);
        canvas.drawRect((float) (dp + AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - dp5), (float) (AndroidUtilities.dp(4.0f) + dp2), (float) getMeasuredHeight(), this.paint);
        canvas.restore();
        canvas2.drawCircle(f3, (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(7.0f), this.paint);
        canvas2.drawCircle((float) (dp2 + AndroidUtilities.dp(4.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(7.0f), this.paint);
    }
}
