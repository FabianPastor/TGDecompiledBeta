package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_help_premiumPromo;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.PremiumPreviewFragment;

public class VideoScreenPreview extends FrameLayout implements PagerHeaderView, NotificationCenter.NotificationCenterDelegate {
    private static final float[] speedScaleVideoTimestamps = {0.02f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.02f};
    boolean allowPlay;
    float aspectRatio;
    AspectRatioFrameLayout aspectRatioFrameLayout;
    String attachFileName;
    boolean attached;
    CellFlickerDrawable.DrawableInterface cellFlickerDrawable;
    int currentAccount;
    File file;
    boolean firstFrameRendered;
    boolean fromTop = false;
    ImageReceiver imageReceiver = new ImageReceiver(this);
    long lastFrameTime;
    private MatrixParticlesDrawable matrixParticlesDrawable;
    Paint phoneFrame1 = new Paint(1);
    Paint phoneFrame2 = new Paint(1);
    boolean play;
    float progress;
    /* access modifiers changed from: private */
    public float roundRadius;
    RoundedBitmapDrawable roundedBitmapDrawable;
    int size;
    SpeedLineParticles$Drawable speedLinesDrawable;
    StarParticlesView.Drawable starDrawable;
    private final SvgHelper.SvgDrawable svgIcon;
    TextureView textureView;
    int type;
    VideoPlayer videoPlayer;
    boolean visible;

    private void checkVideo() {
        File file2 = this.file;
        if (file2 != null && file2.exists()) {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(ApplicationLoader.applicationContext, Uri.fromFile(this.file));
            int intValue = Integer.valueOf(mediaMetadataRetriever.extractMetadata(18)).intValue();
            int intValue2 = Integer.valueOf(mediaMetadataRetriever.extractMetadata(19)).intValue();
            mediaMetadataRetriever.release();
            this.aspectRatio = ((float) intValue) / ((float) intValue2);
            if (this.allowPlay) {
                runVideoPlayer();
            }
        }
    }

    public VideoScreenPreview(Context context, SvgHelper.SvgDrawable svgDrawable, int i, int i2) {
        super(context);
        this.currentAccount = i;
        this.type = i2;
        this.svgIcon = svgDrawable;
        this.phoneFrame1.setColor(-16777216);
        this.phoneFrame2.setColor(ColorUtils.blendARGB(Theme.getColor("premiumGradient2"), -16777216, 0.5f));
        this.imageReceiver.setLayerNum(Integer.MAX_VALUE);
        setVideo();
        if (i2 == 1) {
            MatrixParticlesDrawable matrixParticlesDrawable2 = new MatrixParticlesDrawable();
            this.matrixParticlesDrawable = matrixParticlesDrawable2;
            matrixParticlesDrawable2.init();
        } else if (i2 == 6 || i2 == 9 || i2 == 3 || i2 == 7 || i2 == 11 || i2 == 4) {
            StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(40);
            this.starDrawable = drawable;
            drawable.speedScale = 3.0f;
            drawable.type = i2;
            if (i2 == 3) {
                drawable.size1 = 14;
                drawable.size2 = 18;
                drawable.size3 = 18;
            } else {
                drawable.size1 = 14;
                drawable.size2 = 16;
                drawable.size3 = 15;
            }
            drawable.k3 = 0.98f;
            drawable.k2 = 0.98f;
            drawable.k1 = 0.98f;
            drawable.speedScale = 4.0f;
            drawable.colorKey = "premiumStartSmallStarsColor2";
            drawable.init();
        } else if (i2 == 2) {
            SpeedLineParticles$Drawable speedLineParticles$Drawable = new SpeedLineParticles$Drawable(200);
            this.speedLinesDrawable = speedLineParticles$Drawable;
            speedLineParticles$Drawable.init();
        } else {
            int i3 = 100;
            if (SharedConfig.getDevicePerformanceClass() == 2) {
                i3 = 800;
            } else if (SharedConfig.getDevicePerformanceClass() == 1) {
                i3 = 400;
            }
            StarParticlesView.Drawable drawable2 = new StarParticlesView.Drawable(i3);
            this.starDrawable = drawable2;
            drawable2.colorKey = "premiumStartSmallStarsColor2";
            drawable2.size1 = 8;
            drawable2.size1 = 6;
            drawable2.size1 = 4;
            drawable2.k3 = 0.98f;
            drawable2.k2 = 0.98f;
            drawable2.k1 = 0.98f;
            drawable2.useRotate = true;
            drawable2.speedScale = 4.0f;
            drawable2.checkBounds = true;
            drawable2.checkTime = true;
            drawable2.useBlur = true;
            drawable2.roundEffect = false;
            drawable2.init();
        }
        if (i2 == 1 || i2 == 3 || i2 == 11) {
            this.fromTop = true;
        }
        AnonymousClass1 r12 = new AspectRatioFrameLayout(context) {
            Path clipPath = new Path();

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                this.clipPath.reset();
                VideoScreenPreview videoScreenPreview = VideoScreenPreview.this;
                if (videoScreenPreview.fromTop) {
                    AndroidUtilities.rectTmp.set(0.0f, -videoScreenPreview.roundRadius, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                } else {
                    AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) ((int) (((float) getMeasuredHeight()) + VideoScreenPreview.this.roundRadius)));
                }
                float access$000 = VideoScreenPreview.this.roundRadius - ((float) AndroidUtilities.dp(3.0f));
                this.clipPath.addRoundRect(AndroidUtilities.rectTmp, new float[]{access$000, access$000, access$000, access$000, access$000, access$000, access$000, access$000}, Path.Direction.CW);
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                canvas.save();
                canvas.clipPath(this.clipPath);
                super.dispatchDraw(canvas);
                canvas.restore();
            }
        };
        this.aspectRatioFrameLayout = r12;
        r12.setResizeMode(0);
        TextureView textureView2 = new TextureView(context);
        this.textureView = textureView2;
        this.aspectRatioFrameLayout.addView(textureView2);
        setWillNotDraw(false);
        addView(this.aspectRatioFrameLayout);
    }

    private void setVideo() {
        TLRPC$TL_help_premiumPromo premiumPromo = MediaDataController.getInstance(this.currentAccount).getPremiumPromo();
        String featureTypeToServerString = PremiumPreviewFragment.featureTypeToServerString(this.type);
        if (premiumPromo != null) {
            int i = -1;
            int i2 = 0;
            while (true) {
                if (i2 >= premiumPromo.video_sections.size()) {
                    break;
                } else if (premiumPromo.video_sections.get(i2).equals(featureTypeToServerString)) {
                    i = i2;
                    break;
                } else {
                    i2++;
                }
            }
            if (i >= 0) {
                TLRPC$Document tLRPC$Document = premiumPromo.videos.get(i);
                AnonymousClass2 r7 = null;
                for (int i3 = 0; i3 < tLRPC$Document.thumbs.size(); i3++) {
                    if (tLRPC$Document.thumbs.get(i3) instanceof TLRPC$TL_photoStrippedSize) {
                        this.roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), ImageLoader.getStrippedPhotoBitmap(tLRPC$Document.thumbs.get(i3).bytes, "b"));
                        CellFlickerDrawable cellFlickerDrawable2 = new CellFlickerDrawable();
                        cellFlickerDrawable2.repeatProgress = 4.0f;
                        cellFlickerDrawable2.progress = 3.5f;
                        cellFlickerDrawable2.frameInside = true;
                        this.cellFlickerDrawable = cellFlickerDrawable2.getDrawableInterface(this, this.svgIcon);
                        r7 = new CombinedDrawable(this.roundedBitmapDrawable, this.cellFlickerDrawable) {
                            public void setBounds(int i, int i2, int i3, int i4) {
                                VideoScreenPreview videoScreenPreview = VideoScreenPreview.this;
                                if (videoScreenPreview.fromTop) {
                                    super.setBounds(i, (int) (((float) i2) - videoScreenPreview.roundRadius), i3, i4);
                                } else {
                                    super.setBounds(i, i2, i3, (int) (((float) i4) + videoScreenPreview.roundRadius));
                                }
                            }
                        };
                        r7.setFullsize(true);
                    }
                }
                this.attachFileName = FileLoader.getAttachFileName(tLRPC$Document);
                this.imageReceiver.setImage((ImageLocation) null, (String) null, r7, (String) null, (Object) null, 1);
                FileLoader.getInstance(this.currentAccount).loadFile(tLRPC$Document, (Object) null, 1, 0);
                Utilities.globalQueue.postRunnable(new VideoScreenPreview$$ExternalSyntheticLambda1(this, tLRPC$Document));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setVideo$1(TLRPC$Document tLRPC$Document) {
        AndroidUtilities.runOnUIThread(new VideoScreenPreview$$ExternalSyntheticLambda0(this, FileLoader.getInstance(this.currentAccount).getPathToAttach(tLRPC$Document)));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setVideo$0(File file2) {
        this.file = file2;
        checkVideo();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size2 = View.MeasureSpec.getSize(i);
        int size3 = View.MeasureSpec.getSize(i2);
        float size4 = (float) ((int) (((float) View.MeasureSpec.getSize(i2)) * 0.9f));
        float f = (float) size2;
        float f2 = (f - (0.671f * size4)) / 2.0f;
        this.roundRadius = 0.0671f * size4;
        if (Build.VERSION.SDK_INT >= 21) {
            this.aspectRatioFrameLayout.invalidateOutline();
        }
        if (this.fromTop) {
            AndroidUtilities.rectTmp.set(f2, 0.0f, f - f2, size4);
        } else {
            float f3 = (float) size3;
            AndroidUtilities.rectTmp.set(f2, f3 - size4, f - f2, f3);
        }
        ViewGroup.LayoutParams layoutParams = this.aspectRatioFrameLayout.getLayoutParams();
        RectF rectF = AndroidUtilities.rectTmp;
        layoutParams.width = (int) rectF.width();
        this.aspectRatioFrameLayout.getLayoutParams().height = (int) rectF.height();
        ((ViewGroup.MarginLayoutParams) this.aspectRatioFrameLayout.getLayoutParams()).leftMargin = (int) rectF.left;
        ((ViewGroup.MarginLayoutParams) this.aspectRatioFrameLayout.getLayoutParams()).topMargin = (int) rectF.top;
        super.onMeasure(i, i2);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int measuredWidth = getMeasuredWidth() << (getMeasuredHeight() + 16);
        float measuredHeight = (float) ((int) (((float) getMeasuredHeight()) * 0.9f));
        float measuredWidth2 = (((float) getMeasuredWidth()) - (0.671f * measuredHeight)) / 2.0f;
        if (this.fromTop) {
            AndroidUtilities.rectTmp.set(measuredWidth2, -this.roundRadius, ((float) getMeasuredWidth()) - measuredWidth2, measuredHeight);
        } else {
            AndroidUtilities.rectTmp.set(measuredWidth2, ((float) getMeasuredHeight()) - measuredHeight, ((float) getMeasuredWidth()) - measuredWidth2, ((float) getMeasuredHeight()) + this.roundRadius);
        }
        if (this.size != measuredWidth) {
            this.size = measuredWidth;
            MatrixParticlesDrawable matrixParticlesDrawable2 = this.matrixParticlesDrawable;
            if (matrixParticlesDrawable2 != null) {
                matrixParticlesDrawable2.drawingRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.matrixParticlesDrawable.excludeRect.set(AndroidUtilities.rectTmp);
                this.matrixParticlesDrawable.excludeRect.inset((float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f));
            }
            StarParticlesView.Drawable drawable = this.starDrawable;
            if (drawable != null) {
                int i5 = this.type;
                if (i5 == 6 || i5 == 9 || i5 == 3 || i5 == 7 || i5 == 11 || i5 == 4) {
                    drawable.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                    this.starDrawable.rect.inset((float) AndroidUtilities.dp(30.0f), (float) AndroidUtilities.dp(30.0f));
                } else {
                    RectF rectF = AndroidUtilities.rectTmp;
                    float width = (float) ((int) (rectF.width() * 0.4f));
                    this.starDrawable.rect.set(rectF.centerX() - width, rectF.centerY() - width, rectF.centerX() + width, rectF.centerY() + width);
                    this.starDrawable.rect2.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                }
                this.starDrawable.resetPositions();
                this.starDrawable.excludeRect.set(AndroidUtilities.rectTmp);
                this.starDrawable.excludeRect.inset((float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f));
            }
            SpeedLineParticles$Drawable speedLineParticles$Drawable = this.speedLinesDrawable;
            if (speedLineParticles$Drawable != null) {
                speedLineParticles$Drawable.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                this.speedLinesDrawable.screenRect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                this.speedLinesDrawable.rect.inset((float) AndroidUtilities.dp(100.0f), (float) AndroidUtilities.dp(100.0f));
                this.speedLinesDrawable.rect.offset(0.0f, ((float) getMeasuredHeight()) * 0.1f);
                this.speedLinesDrawable.resetPositions();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (!(this.starDrawable == null && this.speedLinesDrawable == null && this.matrixParticlesDrawable == null)) {
            float f = this.progress;
            if (f < 0.5f) {
                float pow = (float) Math.pow((double) (1.0f - f), 2.0d);
                canvas.save();
                canvas.scale(pow, pow, ((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f);
                MatrixParticlesDrawable matrixParticlesDrawable2 = this.matrixParticlesDrawable;
                if (matrixParticlesDrawable2 != null) {
                    matrixParticlesDrawable2.onDraw(canvas);
                } else {
                    StarParticlesView.Drawable drawable = this.starDrawable;
                    if (drawable != null) {
                        drawable.onDraw(canvas);
                    } else if (this.speedLinesDrawable != null) {
                        float f2 = 0.2f;
                        VideoPlayer videoPlayer2 = this.videoPlayer;
                        if (videoPlayer2 != null) {
                            float clamp = Utilities.clamp(((float) videoPlayer2.getCurrentPosition()) / ((float) this.videoPlayer.getDuration()), 1.0f, 0.0f);
                            float[] fArr = speedScaleVideoTimestamps;
                            float length = 1.0f / ((float) (fArr.length - 1));
                            int i = (int) (clamp / length);
                            int i2 = i + 1;
                            float f3 = (clamp - (((float) i) * length)) / length;
                            if (i2 < fArr.length) {
                                f2 = (fArr[i] * (1.0f - f3)) + (fArr[i2] * f3);
                            } else {
                                f2 = fArr[i];
                            }
                        }
                        SpeedLineParticles$Drawable speedLineParticles$Drawable = this.speedLinesDrawable;
                        speedLineParticles$Drawable.speedScale = (((1.0f - Utilities.clamp(this.progress / 0.1f, 1.0f, 0.0f)) * 0.9f) + 0.1f) * 150.0f * f2;
                        speedLineParticles$Drawable.onDraw(canvas);
                    }
                }
                canvas.restore();
                invalidate();
            }
        }
        float measuredHeight = (float) ((int) (((float) getMeasuredHeight()) * 0.9f));
        float measuredWidth = (((float) getMeasuredWidth()) - (0.671f * measuredHeight)) / 2.0f;
        float f4 = 0.0671f * measuredHeight;
        this.roundRadius = f4;
        if (this.fromTop) {
            AndroidUtilities.rectTmp.set(measuredWidth, -f4, ((float) getMeasuredWidth()) - measuredWidth, measuredHeight);
        } else {
            AndroidUtilities.rectTmp.set(measuredWidth, ((float) getMeasuredHeight()) - measuredHeight, ((float) getMeasuredWidth()) - measuredWidth, ((float) getMeasuredHeight()) + this.roundRadius);
        }
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.inset((float) (-AndroidUtilities.dp(3.0f)), (float) (-AndroidUtilities.dp(3.0f)));
        rectF.inset((float) (-AndroidUtilities.dp(3.0f)), (float) (-AndroidUtilities.dp(3.0f)));
        canvas.drawRoundRect(rectF, this.roundRadius + ((float) AndroidUtilities.dp(3.0f)), this.roundRadius + ((float) AndroidUtilities.dp(3.0f)), this.phoneFrame2);
        rectF.inset((float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f));
        float f5 = this.roundRadius;
        canvas.drawRoundRect(rectF, f5, f5, this.phoneFrame1);
        if (this.fromTop) {
            rectF.set(measuredWidth, 0.0f, ((float) getMeasuredWidth()) - measuredWidth, measuredHeight);
        } else {
            rectF.set(measuredWidth, ((float) getMeasuredHeight()) - measuredHeight, ((float) getMeasuredWidth()) - measuredWidth, (float) getMeasuredHeight());
        }
        float dp = this.roundRadius - ((float) AndroidUtilities.dp(3.0f));
        this.roundRadius = dp;
        RoundedBitmapDrawable roundedBitmapDrawable2 = this.roundedBitmapDrawable;
        if (roundedBitmapDrawable2 != null) {
            roundedBitmapDrawable2.setCornerRadius(dp);
        }
        CellFlickerDrawable.DrawableInterface drawableInterface = this.cellFlickerDrawable;
        if (drawableInterface != null) {
            drawableInterface.radius = this.roundRadius;
        }
        if (this.fromTop) {
            ImageReceiver imageReceiver2 = this.imageReceiver;
            float f6 = this.roundRadius;
            imageReceiver2.setRoundRadius(0, 0, (int) f6, (int) f6);
        } else {
            ImageReceiver imageReceiver3 = this.imageReceiver;
            float f7 = this.roundRadius;
            imageReceiver3.setRoundRadius((int) f7, (int) f7, 0, 0);
        }
        if (!this.firstFrameRendered) {
            this.imageReceiver.setImageCoords(rectF.left, rectF.top, rectF.width(), rectF.height());
            this.imageReceiver.draw(canvas);
        }
        super.dispatchDraw(canvas);
        if (!this.fromTop) {
            canvas.drawCircle(this.imageReceiver.getCenterX(), this.imageReceiver.getImageY() + ((float) AndroidUtilities.dp(12.0f)), (float) AndroidUtilities.dp(6.0f), this.phoneFrame1);
        }
    }

    public void setOffset(float f) {
        boolean z;
        boolean z2 = true;
        if (f < 0.0f) {
            float measuredWidth = (-f) / ((float) getMeasuredWidth());
            setAlpha((Utilities.clamp(1.0f - measuredWidth, 1.0f, 0.0f) * 0.5f) + 0.5f);
            setRotationY(50.0f * measuredWidth);
            invalidate();
            if (this.fromTop) {
                setTranslationY(((float) (-getMeasuredHeight())) * 0.3f * measuredWidth);
            } else {
                setTranslationY(((float) getMeasuredHeight()) * 0.3f * measuredWidth);
            }
            this.progress = Math.abs(measuredWidth);
            z = measuredWidth < 1.0f;
            if (measuredWidth >= 0.1f) {
                z2 = false;
            }
        } else {
            float measuredWidth2 = (-f) / ((float) getMeasuredWidth());
            invalidate();
            setRotationY(50.0f * measuredWidth2);
            if (this.fromTop) {
                setTranslationY(((float) getMeasuredHeight()) * 0.3f * measuredWidth2);
            } else {
                setTranslationY(((float) (-getMeasuredHeight())) * 0.3f * measuredWidth2);
            }
            z = measuredWidth2 > -1.0f;
            if (measuredWidth2 <= -0.1f) {
                z2 = false;
            }
            this.progress = Math.abs(measuredWidth2);
        }
        if (z != this.visible) {
            this.visible = z;
            updateAttachState();
        }
        if (z2 != this.allowPlay) {
            this.allowPlay = z2;
            this.imageReceiver.setAllowStartAnimation(z2);
            if (this.allowPlay) {
                this.imageReceiver.startAnimation();
                runVideoPlayer();
                return;
            }
            stopVideoPlayer();
            this.imageReceiver.stopAnimation();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
        updateAttachState();
        if (!this.firstFrameRendered) {
            checkVideo();
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
        updateAttachState();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.fileLoaded) {
            String str = objArr[0];
            String str2 = this.attachFileName;
            if (str2 != null && str2.equals(str)) {
                this.file = objArr[1];
                checkVideo();
            }
        }
    }

    private void updateAttachState() {
        boolean z = this.visible && this.attached;
        if (this.play != z) {
            this.play = z;
            if (z) {
                this.imageReceiver.onAttachedToWindow();
            } else {
                this.imageReceiver.onDetachedFromWindow();
            }
        }
    }

    private void runVideoPlayer() {
        if (this.file != null && this.videoPlayer == null) {
            this.aspectRatioFrameLayout.setAspectRatio(this.aspectRatio, 0);
            VideoPlayer videoPlayer2 = new VideoPlayer();
            this.videoPlayer = videoPlayer2;
            videoPlayer2.setTextureView(this.textureView);
            this.videoPlayer.setDelegate(new VideoPlayer.VideoPlayerDelegate() {
                public void onError(VideoPlayer videoPlayer, Exception exc) {
                }

                public /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
                }

                public /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
                }

                public /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
                }

                public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                    return false;
                }

                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                }

                public void onVideoSizeChanged(int i, int i2, int i3, float f) {
                }

                public void onStateChanged(boolean z, int i) {
                    if (i == 4) {
                        VideoScreenPreview.this.videoPlayer.seekTo(0);
                        VideoScreenPreview.this.videoPlayer.play();
                    } else if (i == 1) {
                        VideoScreenPreview.this.videoPlayer.play();
                    }
                }

                public void onRenderedFirstFrame() {
                    VideoScreenPreview videoScreenPreview = VideoScreenPreview.this;
                    if (!videoScreenPreview.firstFrameRendered) {
                        videoScreenPreview.textureView.setAlpha(0.0f);
                        VideoScreenPreview.this.textureView.animate().alpha(1.0f).setListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                VideoScreenPreview videoScreenPreview = VideoScreenPreview.this;
                                videoScreenPreview.firstFrameRendered = true;
                                videoScreenPreview.invalidate();
                            }
                        }).setDuration(200);
                    }
                }
            });
            this.videoPlayer.preparePlayer(Uri.fromFile(this.file), "other");
            this.videoPlayer.setPlayWhenReady(true);
            if (!this.firstFrameRendered) {
                this.imageReceiver.stopAnimation();
                this.textureView.setAlpha(0.0f);
            }
            this.videoPlayer.seekTo(this.lastFrameTime + 60);
            this.videoPlayer.play();
        }
    }

    private void stopVideoPlayer() {
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            this.lastFrameTime = videoPlayer2.getCurrentPosition();
            this.videoPlayer.setTextureView((TextureView) null);
            this.videoPlayer.releasePlayer(true);
            this.videoPlayer = null;
        }
    }
}
