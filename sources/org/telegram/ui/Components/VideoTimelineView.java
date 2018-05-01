package org.telegram.ui.Components;

import android.annotation.TargetApi;
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
import org.telegram.ui.ActionBar.Theme;

@TargetApi(10)
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
    private float minProgressDiff = null;
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

    /* renamed from: org.telegram.ui.Components.VideoTimelineView$1 */
    class C13341 extends AsyncTask<Integer, Integer, Bitmap> {
        private int frameNum = null;

        C13341() {
        }

        protected Bitmap doInBackground(Integer... numArr) {
            Throwable e;
            this.frameNum = numArr[0].intValue();
            if (isCancelled() != null) {
                return null;
            }
            try {
                numArr = VideoTimelineView.this.mediaMetadataRetriever.getFrameAtTime((VideoTimelineView.this.frameTimeOffset * ((long) this.frameNum)) * 1000, 2);
                try {
                    if (isCancelled()) {
                        return null;
                    }
                    if (numArr != null) {
                        Bitmap createBitmap = Bitmap.createBitmap(VideoTimelineView.this.frameWidth, VideoTimelineView.this.frameHeight, numArr.getConfig());
                        Canvas canvas = new Canvas(createBitmap);
                        float access$200 = ((float) VideoTimelineView.this.frameWidth) / ((float) numArr.getWidth());
                        float access$300 = ((float) VideoTimelineView.this.frameHeight) / ((float) numArr.getHeight());
                        if (access$200 <= access$300) {
                            access$200 = access$300;
                        }
                        int width = (int) (((float) numArr.getWidth()) * access$200);
                        int height = (int) (((float) numArr.getHeight()) * access$200);
                        canvas.drawBitmap(numArr, new Rect(0, 0, numArr.getWidth(), numArr.getHeight()), new Rect((VideoTimelineView.this.frameWidth - width) / 2, (VideoTimelineView.this.frameHeight - height) / 2, width, height), null);
                        numArr.recycle();
                        numArr = createBitmap;
                    }
                    return numArr;
                } catch (Exception e2) {
                    e = e2;
                    FileLog.m3e(e);
                    return numArr;
                }
            } catch (Exception e3) {
                e = e3;
                numArr = null;
                FileLog.m3e(e);
                return numArr;
            }
        }

        protected void onPostExecute(Bitmap bitmap) {
            if (!isCancelled()) {
                VideoTimelineView.this.frames.add(bitmap);
                VideoTimelineView.this.invalidate();
                if (this.frameNum < VideoTimelineView.this.framesToLoad) {
                    VideoTimelineView.this.reloadFrames(this.frameNum + 1);
                }
            }
        }
    }

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
        this.paint2.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
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
        if (this.progressRight - this.progressLeft > this.maxProgressDiff) {
            this.progressRight = this.progressLeft + this.maxProgressDiff;
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
            motionEvent = AndroidUtilities.dp(12.0f);
            if (((float) (dp - motionEvent)) <= x && x <= ((float) (dp + motionEvent)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                if (this.delegate != null) {
                    this.delegate.didStartDragging();
                }
                this.pressedLeft = true;
                this.pressDx = (float) ((int) (x - ((float) dp)));
                invalidate();
                return true;
            } else if (((float) (dp2 - motionEvent)) <= x && x <= ((float) (motionEvent + dp2)) && y >= 0.0f && y <= ((float) getMeasuredHeight())) {
                if (this.delegate != null) {
                    this.delegate.didStartDragging();
                }
                this.pressedRight = true;
                this.pressDx = (float) ((int) (x - ((float) dp2)));
                invalidate();
                return true;
            }
        }
        if (motionEvent.getAction() != 1) {
            if (motionEvent.getAction() != 3) {
                if (motionEvent.getAction() == 2) {
                    if (this.pressedLeft != null) {
                        motionEvent = (int) (x - this.pressDx);
                        if (motionEvent < AndroidUtilities.dp(16.0f)) {
                            dp2 = AndroidUtilities.dp(16.0f);
                        } else if (motionEvent <= dp2) {
                            dp2 = motionEvent;
                        }
                        this.progressLeft = ((float) (dp2 - AndroidUtilities.dp(16.0f))) / f;
                        if (this.progressRight - this.progressLeft > this.maxProgressDiff) {
                            this.progressRight = this.progressLeft + this.maxProgressDiff;
                        } else if (this.minProgressDiff != null && this.progressRight - this.progressLeft < this.minProgressDiff) {
                            this.progressLeft = this.progressRight - this.minProgressDiff;
                            if (this.progressLeft < null) {
                                this.progressLeft = 0.0f;
                            }
                        }
                        if (this.delegate != null) {
                            this.delegate.onLeftProgressChanged(this.progressLeft);
                        }
                        invalidate();
                        return true;
                    } else if (this.pressedRight != null) {
                        motionEvent = (int) (x - this.pressDx);
                        if (motionEvent >= dp) {
                            dp = motionEvent > AndroidUtilities.dp(16.0f) + measuredWidth ? measuredWidth + AndroidUtilities.dp(16.0f) : motionEvent;
                        }
                        this.progressRight = ((float) (dp - AndroidUtilities.dp(16.0f))) / f;
                        if (this.progressRight - this.progressLeft > this.maxProgressDiff) {
                            this.progressLeft = this.progressRight - this.maxProgressDiff;
                        } else if (this.minProgressDiff != null && this.progressRight - this.progressLeft < this.minProgressDiff) {
                            this.progressRight = this.progressLeft + this.minProgressDiff;
                            if (this.progressRight > NUM) {
                                this.progressRight = 1.0f;
                            }
                        }
                        if (this.delegate != null) {
                            this.delegate.onRightProgressChanged(this.progressRight);
                        }
                        invalidate();
                        return true;
                    }
                }
            }
        }
        if (this.pressedLeft != null) {
            if (this.delegate != null) {
                this.delegate.didStopDragging();
            }
            this.pressedLeft = false;
            return true;
        } else if (this.pressedRight != null) {
            if (this.delegate != null) {
                this.delegate.didStopDragging();
            }
            this.pressedRight = false;
            return true;
        }
        return false;
    }

    public void setColor(int i) {
        this.paint.setColor(i);
    }

    public void setVideoPath(String str) {
        destroy();
        this.mediaMetadataRetriever = new MediaMetadataRetriever();
        this.progressLeft = 0.0f;
        this.progressRight = 1.0f;
        try {
            this.mediaMetadataRetriever.setDataSource(str);
            this.videoLength = Long.parseLong(this.mediaMetadataRetriever.extractMetadata(9));
        } catch (Throwable e) {
            FileLog.m3e(e);
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
            this.currentTask = new C13341();
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
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
        for (int i = 0; i < this.frames.size(); i++) {
            Bitmap bitmap = (Bitmap) this.frames.get(i);
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        this.frames.clear();
        if (this.currentTask != null) {
            this.currentTask.cancel(true);
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
        if (this.currentTask != null) {
            this.currentTask.cancel(true);
            this.currentTask = null;
        }
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(36.0f);
        float f = (float) measuredWidth;
        int dp = ((int) (this.progressLeft * f)) + AndroidUtilities.dp(16.0f);
        int dp2 = ((int) (f * this.progressRight)) + AndroidUtilities.dp(16.0f);
        canvas.save();
        int i = 0;
        canvas2.clipRect(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(20.0f) + measuredWidth, getMeasuredHeight());
        if (this.frames.isEmpty() && r0.currentTask == null) {
            reloadFrames(0);
        } else {
            int i2 = 0;
            while (i < r0.frames.size()) {
                Bitmap bitmap = (Bitmap) r0.frames.get(i);
                if (bitmap != null) {
                    int dp3 = AndroidUtilities.dp(16.0f) + ((r0.isRoundFrames ? r0.frameWidth / 2 : r0.frameWidth) * i2);
                    int dp4 = AndroidUtilities.dp(2.0f);
                    if (r0.isRoundFrames) {
                        r0.rect2.set(dp3, dp4, AndroidUtilities.dp(28.0f) + dp3, AndroidUtilities.dp(28.0f) + dp4);
                        canvas2.drawBitmap(bitmap, r0.rect1, r0.rect2, null);
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
        canvas2.drawRect((float) AndroidUtilities.dp(16.0f), f2, f3, (float) (getMeasuredHeight() - dp5), r0.paint2);
        float f4 = 4.0f;
        canvas2.drawRect((float) (AndroidUtilities.dp(4.0f) + dp2), f2, (float) ((AndroidUtilities.dp(16.0f) + measuredWidth) + AndroidUtilities.dp(4.0f)), (float) (getMeasuredHeight() - dp5), r0.paint2);
        canvas2.drawRect(f3, 0.0f, (float) (AndroidUtilities.dp(2.0f) + dp), (float) getMeasuredHeight(), r0.paint);
        canvas2.drawRect((float) (AndroidUtilities.dp(2.0f) + dp2), 0.0f, (float) (AndroidUtilities.dp(f4) + dp2), (float) getMeasuredHeight(), r0.paint);
        canvas2.drawRect((float) (AndroidUtilities.dp(2.0f) + dp), 0.0f, (float) (AndroidUtilities.dp(f4) + dp2), f2, r0.paint);
        canvas2.drawRect((float) (dp + AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - dp5), (float) (AndroidUtilities.dp(f4) + dp2), (float) getMeasuredHeight(), r0.paint);
        canvas.restore();
        canvas2.drawCircle(f3, (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(7.0f), r0.paint);
        canvas2.drawCircle((float) (dp2 + AndroidUtilities.dp(f4)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(7.0f), r0.paint);
    }
}
