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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.Premium.SpeedLineParticles;
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
    SpeedLineParticles.Drawable speedLinesDrawable;
    StarParticlesView.Drawable starDrawable;
    private final SvgHelper.SvgDrawable svgIcon;
    TextureView textureView;
    int type;
    VideoPlayer videoPlayer;
    boolean visible;

    private void checkVideo() {
        File file2 = this.file;
        if (file2 != null && file2.exists()) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(ApplicationLoader.applicationContext, Uri.fromFile(this.file));
            int width = Integer.valueOf(retriever.extractMetadata(18)).intValue();
            int height = Integer.valueOf(retriever.extractMetadata(19)).intValue();
            retriever.release();
            this.aspectRatio = ((float) width) / ((float) height);
            if (this.allowPlay) {
                runVideoPlayer();
            }
        }
    }

    public VideoScreenPreview(Context context, SvgHelper.SvgDrawable svgDrawable, int currentAccount2, int type2) {
        super(context);
        this.currentAccount = currentAccount2;
        this.type = type2;
        this.svgIcon = svgDrawable;
        this.phoneFrame1.setColor(-16777216);
        this.phoneFrame2.setColor(ColorUtils.blendARGB(Theme.getColor("premiumGradient2"), -16777216, 0.5f));
        this.imageReceiver.setLayerNum(Integer.MAX_VALUE);
        setVideo();
        if (type2 == 1) {
            MatrixParticlesDrawable matrixParticlesDrawable2 = new MatrixParticlesDrawable();
            this.matrixParticlesDrawable = matrixParticlesDrawable2;
            matrixParticlesDrawable2.init();
        } else if (type2 == 6 || type2 == 9 || type2 == 3 || type2 == 7) {
            StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(40);
            this.starDrawable = drawable;
            drawable.speedScale = 3.0f;
            this.starDrawable.type = type2;
            if (type2 == 3) {
                this.starDrawable.size1 = 14;
                this.starDrawable.size2 = 18;
                this.starDrawable.size3 = 18;
            } else {
                this.starDrawable.size1 = 14;
                this.starDrawable.size2 = 16;
                this.starDrawable.size3 = 15;
            }
            StarParticlesView.Drawable drawable2 = this.starDrawable;
            drawable2.k3 = 0.98f;
            drawable2.k2 = 0.98f;
            drawable2.k1 = 0.98f;
            this.starDrawable.speedScale = 4.0f;
            this.starDrawable.colorKey = "premiumStartSmallStarsColor2";
            this.starDrawable.init();
        } else if (type2 == 2) {
            SpeedLineParticles.Drawable drawable3 = new SpeedLineParticles.Drawable(200);
            this.speedLinesDrawable = drawable3;
            drawable3.init();
        } else {
            int particlesCount = 100;
            if (SharedConfig.getDevicePerformanceClass() == 2) {
                particlesCount = 800;
            } else if (SharedConfig.getDevicePerformanceClass() == 1) {
                particlesCount = 400;
            }
            StarParticlesView.Drawable drawable4 = new StarParticlesView.Drawable(particlesCount);
            this.starDrawable = drawable4;
            drawable4.colorKey = "premiumStartSmallStarsColor2";
            this.starDrawable.size1 = 8;
            this.starDrawable.size1 = 6;
            this.starDrawable.size1 = 4;
            StarParticlesView.Drawable drawable5 = this.starDrawable;
            drawable5.k3 = 0.98f;
            drawable5.k2 = 0.98f;
            drawable5.k1 = 0.98f;
            this.starDrawable.useRotate = true;
            this.starDrawable.speedScale = 4.0f;
            this.starDrawable.checkBounds = true;
            this.starDrawable.checkTime = true;
            this.starDrawable.useBlur = true;
            this.starDrawable.roundEffect = false;
            this.starDrawable.init();
        }
        if (type2 == 1 || type2 == 3) {
            this.fromTop = true;
        }
        AnonymousClass1 r1 = new AspectRatioFrameLayout(context) {
            Path clipPath = new Path();

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                this.clipPath.reset();
                if (VideoScreenPreview.this.fromTop) {
                    AndroidUtilities.rectTmp.set(0.0f, -VideoScreenPreview.this.roundRadius, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                } else {
                    AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) ((int) (((float) getMeasuredHeight()) + VideoScreenPreview.this.roundRadius)));
                }
                float rad = VideoScreenPreview.this.roundRadius - ((float) AndroidUtilities.dp(3.0f));
                this.clipPath.addRoundRect(AndroidUtilities.rectTmp, new float[]{rad, rad, rad, rad, rad, rad, rad, rad}, Path.Direction.CW);
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                canvas.save();
                canvas.clipPath(this.clipPath);
                super.dispatchDraw(canvas);
                canvas.restore();
            }
        };
        this.aspectRatioFrameLayout = r1;
        r1.setResizeMode(0);
        TextureView textureView2 = new TextureView(context);
        this.textureView = textureView2;
        this.aspectRatioFrameLayout.addView(textureView2);
        setWillNotDraw(false);
        addView(this.aspectRatioFrameLayout);
    }

    private void setVideo() {
        TLRPC.TL_help_premiumPromo premiumPromo = MediaDataController.getInstance(this.currentAccount).getPremiumPromo();
        String typeString = PremiumPreviewFragment.featureTypeToServerString(this.type);
        if (premiumPromo != null) {
            int index = -1;
            int i = 0;
            while (true) {
                if (i >= premiumPromo.video_sections.size()) {
                    break;
                } else if (premiumPromo.video_sections.get(i).equals(typeString)) {
                    index = i;
                    break;
                } else {
                    i++;
                }
            }
            if (index >= 0) {
                TLRPC.Document document = premiumPromo.videos.get(index);
                AnonymousClass2 r4 = null;
                for (int i2 = 0; i2 < document.thumbs.size(); i2++) {
                    if (document.thumbs.get(i2) instanceof TLRPC.TL_photoStrippedSize) {
                        this.roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), ImageLoader.getStrippedPhotoBitmap(document.thumbs.get(i2).bytes, "b"));
                        CellFlickerDrawable flickerDrawable = new CellFlickerDrawable();
                        flickerDrawable.repeatProgress = 4.0f;
                        flickerDrawable.progress = 3.5f;
                        flickerDrawable.frameInside = true;
                        this.cellFlickerDrawable = flickerDrawable.getDrawableInterface(this, this.svgIcon);
                        AnonymousClass2 r7 = new CombinedDrawable(this.roundedBitmapDrawable, this.cellFlickerDrawable) {
                            public void setBounds(int left, int top, int right, int bottom) {
                                if (VideoScreenPreview.this.fromTop) {
                                    super.setBounds(left, (int) (((float) top) - VideoScreenPreview.this.roundRadius), right, bottom);
                                } else {
                                    super.setBounds(left, top, right, (int) (((float) bottom) + VideoScreenPreview.this.roundRadius));
                                }
                            }
                        };
                        r7.setFullsize(true);
                        r4 = r7;
                    }
                }
                this.attachFileName = FileLoader.getAttachFileName(document);
                this.imageReceiver.setImage((ImageLocation) null, (String) null, r4, (String) null, (Object) null, 1);
                FileLoader.getInstance(this.currentAccount).loadFile(document, (Object) null, 1, 0);
                Utilities.globalQueue.postRunnable(new VideoScreenPreview$$ExternalSyntheticLambda1(this, document));
            }
        }
    }

    /* renamed from: lambda$setVideo$1$org-telegram-ui-Components-Premium-VideoScreenPreview  reason: not valid java name */
    public /* synthetic */ void m1259xfd64var_(TLRPC.Document document) {
        AndroidUtilities.runOnUIThread(new VideoScreenPreview$$ExternalSyntheticLambda0(this, FileLoader.getInstance(this.currentAccount).getPathToAttach(document)));
    }

    /* renamed from: lambda$setVideo$0$org-telegram-ui-Components-Premium-VideoScreenPreview  reason: not valid java name */
    public /* synthetic */ void m1258xd410a2f6(File file2) {
        this.file = file2;
        checkVideo();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        int size2 = (int) (((float) View.MeasureSpec.getSize(heightMeasureSpec)) * 0.9f);
        float h = (float) size2;
        float horizontalPadding = (((float) measuredWidth) - (((float) size2) * 0.671f)) / 2.0f;
        this.roundRadius = ((float) size2) * 0.0671f;
        if (Build.VERSION.SDK_INT >= 21) {
            this.aspectRatioFrameLayout.invalidateOutline();
        }
        if (this.fromTop) {
            AndroidUtilities.rectTmp.set(horizontalPadding, 0.0f, ((float) measuredWidth) - horizontalPadding, h);
        } else {
            AndroidUtilities.rectTmp.set(horizontalPadding, ((float) measuredHeight) - h, ((float) measuredWidth) - horizontalPadding, (float) measuredHeight);
        }
        this.aspectRatioFrameLayout.getLayoutParams().width = (int) AndroidUtilities.rectTmp.width();
        this.aspectRatioFrameLayout.getLayoutParams().height = (int) AndroidUtilities.rectTmp.height();
        ((ViewGroup.MarginLayoutParams) this.aspectRatioFrameLayout.getLayoutParams()).leftMargin = (int) AndroidUtilities.rectTmp.left;
        ((ViewGroup.MarginLayoutParams) this.aspectRatioFrameLayout.getLayoutParams()).topMargin = (int) AndroidUtilities.rectTmp.top;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int sizeInternal = getMeasuredWidth() << (getMeasuredHeight() + 16);
        int size2 = (int) (((float) getMeasuredHeight()) * 0.9f);
        float h = (float) size2;
        float horizontalPadding = (((float) getMeasuredWidth()) - (((float) size2) * 0.671f)) / 2.0f;
        if (this.fromTop) {
            AndroidUtilities.rectTmp.set(horizontalPadding, -this.roundRadius, ((float) getMeasuredWidth()) - horizontalPadding, h);
        } else {
            AndroidUtilities.rectTmp.set(horizontalPadding, ((float) getMeasuredHeight()) - h, ((float) getMeasuredWidth()) - horizontalPadding, ((float) getMeasuredHeight()) + this.roundRadius);
        }
        if (this.size != sizeInternal) {
            this.size = sizeInternal;
            MatrixParticlesDrawable matrixParticlesDrawable2 = this.matrixParticlesDrawable;
            if (matrixParticlesDrawable2 != null) {
                matrixParticlesDrawable2.drawingRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.matrixParticlesDrawable.excludeRect.set(AndroidUtilities.rectTmp);
                this.matrixParticlesDrawable.excludeRect.inset((float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f));
            }
            StarParticlesView.Drawable drawable = this.starDrawable;
            if (drawable != null) {
                int i = this.type;
                if (i == 6 || i == 9 || i == 3 || i == 7) {
                    drawable.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                    this.starDrawable.rect.inset((float) AndroidUtilities.dp(30.0f), (float) AndroidUtilities.dp(30.0f));
                } else {
                    int getParticlesWidth = (int) (AndroidUtilities.rectTmp.width() * 0.4f);
                    this.starDrawable.rect.set(AndroidUtilities.rectTmp.centerX() - ((float) getParticlesWidth), AndroidUtilities.rectTmp.centerY() - ((float) getParticlesWidth), AndroidUtilities.rectTmp.centerX() + ((float) getParticlesWidth), AndroidUtilities.rectTmp.centerY() + ((float) getParticlesWidth));
                    this.starDrawable.rect2.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                }
                this.starDrawable.resetPositions();
                this.starDrawable.excludeRect.set(AndroidUtilities.rectTmp);
                this.starDrawable.excludeRect.inset((float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f));
            }
            SpeedLineParticles.Drawable drawable2 = this.speedLinesDrawable;
            if (drawable2 != null) {
                drawable2.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
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
                float s = (float) Math.pow((double) (1.0f - f), 2.0d);
                canvas.save();
                canvas.scale(s, s, ((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f);
                MatrixParticlesDrawable matrixParticlesDrawable2 = this.matrixParticlesDrawable;
                if (matrixParticlesDrawable2 != null) {
                    matrixParticlesDrawable2.onDraw(canvas);
                } else {
                    StarParticlesView.Drawable drawable = this.starDrawable;
                    if (drawable != null) {
                        drawable.onDraw(canvas);
                    } else if (this.speedLinesDrawable != null) {
                        float videoSpeedScale = 0.2f;
                        VideoPlayer videoPlayer2 = this.videoPlayer;
                        if (videoPlayer2 != null) {
                            float p = Utilities.clamp(((float) videoPlayer2.getCurrentPosition()) / ((float) this.videoPlayer.getDuration()), 1.0f, 0.0f);
                            float[] fArr = speedScaleVideoTimestamps;
                            float step = 1.0f / ((float) (fArr.length - 1));
                            int fromIndex = (int) (p / step);
                            int toIndex = fromIndex + 1;
                            float localProgress = (p - (((float) fromIndex) * step)) / step;
                            if (toIndex < fArr.length) {
                                videoSpeedScale = (fArr[fromIndex] * (1.0f - localProgress)) + (fArr[toIndex] * localProgress);
                            } else {
                                videoSpeedScale = fArr[fromIndex];
                            }
                        }
                        this.speedLinesDrawable.speedScale = 150.0f * (((1.0f - Utilities.clamp(this.progress / 0.1f, 1.0f, 0.0f)) * 0.9f) + 0.1f) * videoSpeedScale;
                        this.speedLinesDrawable.onDraw(canvas);
                    }
                }
                canvas.restore();
                invalidate();
            }
        }
        int size2 = (int) (((float) getMeasuredHeight()) * 0.9f);
        float h = (float) size2;
        float horizontalPadding = (((float) getMeasuredWidth()) - (((float) size2) * 0.671f)) / 2.0f;
        this.roundRadius = ((float) size2) * 0.0671f;
        if (this.fromTop) {
            AndroidUtilities.rectTmp.set(horizontalPadding, -this.roundRadius, ((float) getMeasuredWidth()) - horizontalPadding, h);
        } else {
            AndroidUtilities.rectTmp.set(horizontalPadding, ((float) getMeasuredHeight()) - h, ((float) getMeasuredWidth()) - horizontalPadding, ((float) getMeasuredHeight()) + this.roundRadius);
        }
        AndroidUtilities.rectTmp.inset((float) (-AndroidUtilities.dp(3.0f)), (float) (-AndroidUtilities.dp(3.0f)));
        AndroidUtilities.rectTmp.inset((float) (-AndroidUtilities.dp(3.0f)), (float) (-AndroidUtilities.dp(3.0f)));
        canvas.drawRoundRect(AndroidUtilities.rectTmp, this.roundRadius + ((float) AndroidUtilities.dp(3.0f)), this.roundRadius + ((float) AndroidUtilities.dp(3.0f)), this.phoneFrame2);
        AndroidUtilities.rectTmp.inset((float) AndroidUtilities.dp(3.0f), (float) AndroidUtilities.dp(3.0f));
        RectF rectF = AndroidUtilities.rectTmp;
        float f2 = this.roundRadius;
        canvas.drawRoundRect(rectF, f2, f2, this.phoneFrame1);
        if (this.fromTop) {
            AndroidUtilities.rectTmp.set(horizontalPadding, 0.0f, ((float) getMeasuredWidth()) - horizontalPadding, h);
        } else {
            AndroidUtilities.rectTmp.set(horizontalPadding, ((float) getMeasuredHeight()) - h, ((float) getMeasuredWidth()) - horizontalPadding, (float) getMeasuredHeight());
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
            float f3 = this.roundRadius;
            imageReceiver2.setRoundRadius(0, 0, (int) f3, (int) f3);
        } else {
            ImageReceiver imageReceiver3 = this.imageReceiver;
            float f4 = this.roundRadius;
            imageReceiver3.setRoundRadius((int) f4, (int) f4, 0, 0);
        }
        if (!this.firstFrameRendered) {
            this.imageReceiver.setImageCoords(AndroidUtilities.rectTmp.left, AndroidUtilities.rectTmp.top, AndroidUtilities.rectTmp.width(), AndroidUtilities.rectTmp.height());
            this.imageReceiver.draw(canvas);
        }
        super.dispatchDraw(canvas);
        if (!this.fromTop) {
            canvas.drawCircle(this.imageReceiver.getCenterX(), this.imageReceiver.getImageY() + ((float) AndroidUtilities.dp(12.0f)), (float) AndroidUtilities.dp(6.0f), this.phoneFrame1);
        }
    }

    public void setOffset(float translationX) {
        boolean localAllowPlay;
        boolean localVisible;
        boolean localAllowPlay2 = true;
        if (translationX < 0.0f) {
            float p = (-translationX) / ((float) getMeasuredWidth());
            setAlpha((Utilities.clamp(1.0f - p, 1.0f, 0.0f) * 0.5f) + 0.5f);
            setRotationY(50.0f * p);
            invalidate();
            if (this.fromTop) {
                setTranslationY(((float) (-getMeasuredHeight())) * 0.3f * p);
            } else {
                setTranslationY(((float) getMeasuredHeight()) * 0.3f * p);
            }
            this.progress = Math.abs(p);
            localVisible = p < 1.0f;
            if (p >= 0.1f) {
                localAllowPlay2 = false;
            }
            localAllowPlay = localAllowPlay2;
        } else {
            float p2 = (-translationX) / ((float) getMeasuredWidth());
            invalidate();
            setRotationY(50.0f * p2);
            if (this.fromTop) {
                setTranslationY(((float) getMeasuredHeight()) * 0.3f * p2);
            } else {
                setTranslationY(((float) (-getMeasuredHeight())) * 0.3f * p2);
            }
            localVisible = p2 > -1.0f;
            if (p2 <= -0.1f) {
                localAllowPlay2 = false;
            }
            this.progress = Math.abs(p2);
            localAllowPlay = localAllowPlay2;
        }
        if (localVisible != this.visible) {
            this.visible = localVisible;
            updateAttachState();
        }
        if (localAllowPlay != this.allowPlay) {
            this.allowPlay = localAllowPlay;
            this.imageReceiver.setAllowStartAnimation(localAllowPlay);
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileLoaded) {
            String path = args[0];
            String str = this.attachFileName;
            if (str != null && str.equals(path)) {
                this.file = args[1];
                checkVideo();
            }
        }
    }

    private void updateAttachState() {
        boolean localPlay = this.visible && this.attached;
        if (this.play != localPlay) {
            this.play = localPlay;
            if (localPlay) {
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
                public /* synthetic */ void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
                }

                public /* synthetic */ void onSeekFinished(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
                }

                public /* synthetic */ void onSeekStarted(AnalyticsListener.EventTime eventTime) {
                    VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
                }

                public void onStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == 4) {
                        VideoScreenPreview.this.videoPlayer.seekTo(0);
                        VideoScreenPreview.this.videoPlayer.play();
                    } else if (playbackState == 1) {
                        VideoScreenPreview.this.videoPlayer.play();
                    }
                }

                public void onError(VideoPlayer player, Exception e) {
                }

                public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                }

                public void onRenderedFirstFrame() {
                    if (!VideoScreenPreview.this.firstFrameRendered) {
                        VideoScreenPreview.this.textureView.setAlpha(0.0f);
                        VideoScreenPreview.this.textureView.animate().alpha(1.0f).setListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                VideoScreenPreview.this.firstFrameRendered = true;
                                VideoScreenPreview.this.invalidate();
                            }
                        }).setDuration(200);
                    }
                }

                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                }

                public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                    return false;
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
