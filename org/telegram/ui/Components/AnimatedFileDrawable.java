package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class AnimatedFileDrawable extends BitmapDrawable implements Animatable {
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2, new DiscardPolicy());
    private static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private RectF actualDrawRect = new RectF();
    private boolean applyTransformation;
    private Bitmap backgroundBitmap;
    private BitmapShader backgroundShader;
    private RectF bitmapRect = new RectF();
    private boolean decodeSingleFrame;
    private boolean decoderCreated;
    private boolean destroyWhenDone;
    private final Rect dstRect = new Rect();
    private int invalidateAfter = 50;
    private volatile boolean isRecycled;
    private volatile boolean isRunning;
    private long lastFrameDecodeTime;
    private long lastFrameTime;
    private int lastTimeStamp;
    private Runnable loadFrameRunnable = new Runnable() {
        public void run() {
            if (!AnimatedFileDrawable.this.isRecycled) {
                if (!AnimatedFileDrawable.this.decoderCreated && AnimatedFileDrawable.this.nativePtr == 0) {
                    AnimatedFileDrawable.this.nativePtr = AnimatedFileDrawable.createDecoder(AnimatedFileDrawable.this.path.getAbsolutePath(), AnimatedFileDrawable.this.metaData);
                    AnimatedFileDrawable.this.decoderCreated = true;
                }
                try {
                    if (AnimatedFileDrawable.this.backgroundBitmap == null) {
                        AnimatedFileDrawable.this.backgroundBitmap = Bitmap.createBitmap(AnimatedFileDrawable.this.metaData[0], AnimatedFileDrawable.this.metaData[1], Config.ARGB_8888);
                        if (!(AnimatedFileDrawable.this.backgroundShader != null || AnimatedFileDrawable.this.backgroundBitmap == null || AnimatedFileDrawable.this.roundRadius == 0)) {
                            AnimatedFileDrawable.this.backgroundShader = new BitmapShader(AnimatedFileDrawable.this.backgroundBitmap, TileMode.CLAMP, TileMode.CLAMP);
                        }
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
                if (AnimatedFileDrawable.this.backgroundBitmap != null) {
                    AnimatedFileDrawable.this.lastFrameDecodeTime = System.currentTimeMillis();
                    AnimatedFileDrawable.getVideoFrame(AnimatedFileDrawable.this.nativePtr, AnimatedFileDrawable.this.backgroundBitmap, AnimatedFileDrawable.this.metaData);
                }
            }
            AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnable);
        }
    };
    private Runnable loadFrameTask;
    protected final Runnable mInvalidateTask = new Runnable() {
        public void run() {
            if (AnimatedFileDrawable.this.secondParentView != null) {
                AnimatedFileDrawable.this.secondParentView.invalidate();
            } else if (AnimatedFileDrawable.this.parentView != null) {
                AnimatedFileDrawable.this.parentView.invalidate();
            }
        }
    };
    private final Runnable mStartTask = new Runnable() {
        public void run() {
            if (AnimatedFileDrawable.this.secondParentView != null) {
                AnimatedFileDrawable.this.secondParentView.invalidate();
            } else if (AnimatedFileDrawable.this.parentView != null) {
                AnimatedFileDrawable.this.parentView.invalidate();
            }
        }
    };
    private final int[] metaData = new int[4];
    private volatile int nativePtr;
    private Bitmap nextRenderingBitmap;
    private BitmapShader nextRenderingShader;
    private View parentView = null;
    private File path;
    private boolean recycleWithSecond;
    private Bitmap renderingBitmap;
    private BitmapShader renderingShader;
    private int roundRadius;
    private RectF roundRect = new RectF();
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private View secondParentView = null;
    private Matrix shaderMatrix = new Matrix();
    private boolean singleFrameDecoded;
    private Runnable uiRunnable = new Runnable() {
        public void run() {
            if (AnimatedFileDrawable.this.destroyWhenDone && AnimatedFileDrawable.this.nativePtr != 0) {
                AnimatedFileDrawable.destroyDecoder(AnimatedFileDrawable.this.nativePtr);
                AnimatedFileDrawable.this.nativePtr = 0;
            }
            if (AnimatedFileDrawable.this.nativePtr == 0) {
                if (AnimatedFileDrawable.this.renderingBitmap != null) {
                    AnimatedFileDrawable.this.renderingBitmap.recycle();
                    AnimatedFileDrawable.this.renderingBitmap = null;
                }
                if (AnimatedFileDrawable.this.backgroundBitmap != null) {
                    AnimatedFileDrawable.this.backgroundBitmap.recycle();
                    AnimatedFileDrawable.this.backgroundBitmap = null;
                    return;
                }
                return;
            }
            AnimatedFileDrawable.this.singleFrameDecoded = true;
            AnimatedFileDrawable.this.loadFrameTask = null;
            AnimatedFileDrawable.this.nextRenderingBitmap = AnimatedFileDrawable.this.backgroundBitmap;
            AnimatedFileDrawable.this.nextRenderingShader = AnimatedFileDrawable.this.backgroundShader;
            if (AnimatedFileDrawable.this.metaData[3] < AnimatedFileDrawable.this.lastTimeStamp) {
                AnimatedFileDrawable.this.lastTimeStamp = 0;
            }
            if (AnimatedFileDrawable.this.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp != 0) {
                AnimatedFileDrawable.this.invalidateAfter = AnimatedFileDrawable.this.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp;
            }
            AnimatedFileDrawable.this.lastTimeStamp = AnimatedFileDrawable.this.metaData[3];
            if (AnimatedFileDrawable.this.secondParentView != null) {
                AnimatedFileDrawable.this.secondParentView.invalidate();
            } else if (AnimatedFileDrawable.this.parentView != null) {
                AnimatedFileDrawable.this.parentView.invalidate();
            }
            AnimatedFileDrawable.this.scheduleNextGetFrame();
        }
    };

    private static native int createDecoder(String str, int[] iArr);

    private static native void destroyDecoder(int i);

    private static native int getVideoFrame(int i, Bitmap bitmap, int[] iArr);

    public AnimatedFileDrawable(File file, boolean createDecoder) {
        this.path = file;
        if (createDecoder) {
            this.nativePtr = createDecoder(file.getAbsolutePath(), this.metaData);
            this.decoderCreated = true;
        }
    }

    public void setParentView(View view) {
        this.parentView = view;
    }

    public void setSecondParentView(View view) {
        this.secondParentView = view;
        if (view == null && this.recycleWithSecond) {
            recycle();
        }
    }

    public void setAllowDecodeSingleFrame(boolean value) {
        this.decodeSingleFrame = value;
        if (this.decodeSingleFrame) {
            scheduleNextGetFrame();
        }
    }

    public void recycle() {
        if (this.secondParentView != null) {
            this.recycleWithSecond = true;
            return;
        }
        this.isRunning = false;
        this.isRecycled = true;
        if (this.loadFrameTask == null) {
            if (this.nativePtr != 0) {
                destroyDecoder(this.nativePtr);
                this.nativePtr = 0;
            }
            if (this.renderingBitmap != null) {
                this.renderingBitmap.recycle();
                this.renderingBitmap = null;
            }
            if (this.nextRenderingBitmap != null) {
                this.nextRenderingBitmap.recycle();
                this.nextRenderingBitmap = null;
                return;
            }
            return;
        }
        this.destroyWhenDone = true;
    }

    protected static void runOnUiThread(Runnable task) {
        if (Looper.myLooper() == uiHandler.getLooper()) {
            task.run();
        } else {
            uiHandler.post(task);
        }
    }

    protected void finalize() throws Throwable {
        try {
            recycle();
        } finally {
            super.finalize();
        }
    }

    public int getOpacity() {
        return -2;
    }

    public void start() {
        if (!this.isRunning) {
            this.isRunning = true;
            scheduleNextGetFrame();
            runOnUiThread(this.mStartTask);
        }
    }

    private void scheduleNextGetFrame() {
        if (this.loadFrameTask != null) {
            return;
        }
        if ((this.nativePtr != 0 || !this.decoderCreated) && !this.destroyWhenDone) {
            if (!this.isRunning) {
                if (!this.decodeSingleFrame) {
                    return;
                }
                if (this.decodeSingleFrame && this.singleFrameDecoded) {
                    return;
                }
            }
            long ms = 0;
            if (this.lastFrameDecodeTime != 0) {
                ms = Math.min((long) this.invalidateAfter, Math.max(0, ((long) this.invalidateAfter) - (System.currentTimeMillis() - this.lastFrameDecodeTime)));
            }
            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = executor;
            Runnable runnable = this.loadFrameRunnable;
            this.loadFrameTask = runnable;
            scheduledThreadPoolExecutor.schedule(runnable, ms, TimeUnit.MILLISECONDS);
        }
    }

    public void stop() {
        this.isRunning = false;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public int getIntrinsicHeight() {
        if (this.decoderCreated) {
            return (this.metaData[2] == 90 || this.metaData[2] == 270) ? this.metaData[0] : this.metaData[1];
        } else {
            return AndroidUtilities.dp(100.0f);
        }
    }

    public int getIntrinsicWidth() {
        if (this.decoderCreated) {
            return (this.metaData[2] == 90 || this.metaData[2] == 270) ? this.metaData[1] : this.metaData[0];
        } else {
            return AndroidUtilities.dp(100.0f);
        }
    }

    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.applyTransformation = true;
    }

    public void draw(Canvas canvas) {
        if ((this.nativePtr != 0 || !this.decoderCreated) && !this.destroyWhenDone) {
            long now = System.currentTimeMillis();
            if (this.isRunning) {
                if (this.renderingBitmap == null && this.nextRenderingBitmap == null) {
                    scheduleNextGetFrame();
                } else if (Math.abs(now - this.lastFrameTime) >= ((long) this.invalidateAfter) && this.nextRenderingBitmap != null) {
                    this.renderingBitmap = this.nextRenderingBitmap;
                    this.renderingShader = this.nextRenderingShader;
                    this.nextRenderingBitmap = null;
                    this.nextRenderingShader = null;
                    this.lastFrameTime = now;
                }
            } else if (!this.isRunning && this.decodeSingleFrame && Math.abs(now - this.lastFrameTime) >= ((long) this.invalidateAfter) && this.nextRenderingBitmap != null) {
                this.renderingBitmap = this.nextRenderingBitmap;
                this.renderingShader = this.nextRenderingShader;
                this.nextRenderingBitmap = null;
                this.nextRenderingShader = null;
                this.lastFrameTime = now;
            }
            if (this.renderingBitmap != null) {
                if (this.applyTransformation) {
                    int bitmapW = this.renderingBitmap.getWidth();
                    int bitmapH = this.renderingBitmap.getHeight();
                    if (this.metaData[2] == 90 || this.metaData[2] == 270) {
                        int temp = bitmapW;
                        bitmapW = bitmapH;
                        bitmapH = temp;
                    }
                    this.dstRect.set(getBounds());
                    this.scaleX = ((float) this.dstRect.width()) / ((float) bitmapW);
                    this.scaleY = ((float) this.dstRect.height()) / ((float) bitmapH);
                    this.applyTransformation = false;
                }
                if (this.roundRadius != 0) {
                    float scale = Math.max(this.scaleX, this.scaleY);
                    if (this.renderingShader == null) {
                        this.renderingShader = new BitmapShader(this.backgroundBitmap, TileMode.CLAMP, TileMode.CLAMP);
                    }
                    getPaint().setShader(this.renderingShader);
                    this.roundRect.set(this.dstRect);
                    this.shaderMatrix.reset();
                    if (Math.abs(this.scaleX - this.scaleY) > 1.0E-5f) {
                        int w;
                        int h;
                        if (this.metaData[2] == 90 || this.metaData[2] == 270) {
                            w = (int) Math.floor((double) (((float) this.dstRect.height()) / scale));
                            h = (int) Math.floor((double) (((float) this.dstRect.width()) / scale));
                        } else {
                            w = (int) Math.floor((double) (((float) this.dstRect.width()) / scale));
                            h = (int) Math.floor((double) (((float) this.dstRect.height()) / scale));
                        }
                        this.bitmapRect.set((float) ((this.renderingBitmap.getWidth() - w) / 2), (float) ((this.renderingBitmap.getHeight() - h) / 2), (float) w, (float) h);
                        AndroidUtilities.setRectToRect(this.shaderMatrix, this.bitmapRect, this.roundRect, this.metaData[2], ScaleToFit.START);
                    } else {
                        this.bitmapRect.set(0.0f, 0.0f, (float) this.renderingBitmap.getWidth(), (float) this.renderingBitmap.getHeight());
                        AndroidUtilities.setRectToRect(this.shaderMatrix, this.bitmapRect, this.roundRect, this.metaData[2], ScaleToFit.FILL);
                    }
                    this.renderingShader.setLocalMatrix(this.shaderMatrix);
                    canvas.drawRoundRect(this.actualDrawRect, (float) this.roundRadius, (float) this.roundRadius, getPaint());
                } else {
                    canvas.translate((float) this.dstRect.left, (float) this.dstRect.top);
                    if (this.metaData[2] == 90) {
                        canvas.rotate(90.0f);
                        canvas.translate(0.0f, (float) (-this.dstRect.width()));
                    } else if (this.metaData[2] == 180) {
                        canvas.rotate(BitmapDescriptorFactory.HUE_CYAN);
                        canvas.translate((float) (-this.dstRect.width()), (float) (-this.dstRect.height()));
                    } else if (this.metaData[2] == 270) {
                        canvas.rotate(BitmapDescriptorFactory.HUE_VIOLET);
                        canvas.translate((float) (-this.dstRect.height()), 0.0f);
                    }
                    canvas.scale(this.scaleX, this.scaleY);
                    canvas.drawBitmap(this.renderingBitmap, 0.0f, 0.0f, getPaint());
                }
                if (this.isRunning) {
                    long timeToNextFrame = Math.max(1, (((long) this.invalidateAfter) - (now - this.lastFrameTime)) - 17);
                    uiHandler.removeCallbacks(this.mInvalidateTask);
                    uiHandler.postDelayed(this.mInvalidateTask, Math.min(timeToNextFrame, (long) this.invalidateAfter));
                }
            }
        }
    }

    public int getMinimumHeight() {
        if (this.decoderCreated) {
            return (this.metaData[2] == 90 || this.metaData[2] == 270) ? this.metaData[0] : this.metaData[1];
        } else {
            return AndroidUtilities.dp(100.0f);
        }
    }

    public int getMinimumWidth() {
        if (this.decoderCreated) {
            return (this.metaData[2] == 90 || this.metaData[2] == 270) ? this.metaData[1] : this.metaData[0];
        } else {
            return AndroidUtilities.dp(100.0f);
        }
    }

    public Bitmap getAnimatedBitmap() {
        if (this.renderingBitmap != null) {
            return this.renderingBitmap;
        }
        if (this.nextRenderingBitmap != null) {
            return this.nextRenderingBitmap;
        }
        return null;
    }

    public void setActualDrawRect(int x, int y, int width, int height) {
        this.actualDrawRect.set((float) x, (float) y, (float) (x + width), (float) (y + height));
    }

    public void setRoundRadius(int value) {
        this.roundRadius = value;
        getPaint().setFlags(1);
    }

    public boolean hasBitmap() {
        return (this.nativePtr == 0 || (this.renderingBitmap == null && this.nextRenderingBitmap == null)) ? false : true;
    }

    public int getOrientation() {
        return this.metaData[2];
    }

    public AnimatedFileDrawable makeCopy() {
        AnimatedFileDrawable drawable = new AnimatedFileDrawable(this.path, false);
        drawable.metaData[0] = this.metaData[0];
        drawable.metaData[1] = this.metaData[1];
        return drawable;
    }
}
