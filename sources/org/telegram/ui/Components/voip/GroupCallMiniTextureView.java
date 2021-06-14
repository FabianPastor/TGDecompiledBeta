package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextPaint;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BlobDrawable;
import org.telegram.ui.Components.CrossOutDrawable;
import org.telegram.ui.Components.GroupCallFullscreenAdapter;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.voip.GroupCallStatusIcon;
import org.telegram.ui.GroupCallActivity;
import org.webrtc.GlGenericDrawer;
import org.webrtc.RendererCommon;
import org.webrtc.TextureViewRenderer;
import org.webrtc.VideoSink;

@SuppressLint({"ViewConstructor"})
public class GroupCallMiniTextureView extends FrameLayout implements GroupCallStatusIcon.Callback {
    GroupCallActivity activity;
    boolean animateEnter;
    int animateToColor;
    public boolean animateToFullscreen;
    public boolean animateToScrimView;
    boolean attached;
    ArrayList<GroupCallMiniTextureView> attachedRenderers;
    ChatObject.Call call;
    /* access modifiers changed from: private */
    public boolean checkScale;
    int collapseSize;
    ValueAnimator colorAnimator;
    int currentAccount;
    public boolean forceDetached;
    int fullSize;
    Paint gradientPaint = new Paint(1);
    LinearGradient gradientShader;
    int gridItemsCount;
    public boolean hasVideo;
    ImageReceiver imageReceiver = new ImageReceiver();
    boolean inPinchToZoom;
    FrameLayout infoContainer;
    /* access modifiers changed from: private */
    public boolean invalidateFromChild;
    boolean isFullscreenMode;
    int lastIconColor;
    private boolean lastLandscapeMode;
    private int lastSize;
    /* access modifiers changed from: private */
    public final RLottieImageView micIconView;
    private final SimpleTextView nameView;
    ValueAnimator noVideoStubAnimator;
    /* access modifiers changed from: private */
    public NoVideoStubLayout noVideoStubLayout;
    ArrayList<Runnable> onFirstFrameRunnables = new ArrayList<>();
    float overlayIconAlpha;
    GroupCallRenderersContainer parentContainer;
    public ChatObject.VideoParticipant participant;
    /* access modifiers changed from: private */
    public CrossOutDrawable pausedVideoDrawable;
    float pinchCenterX;
    float pinchCenterY;
    float pinchScale;
    float pinchTranslationX;
    float pinchTranslationY;
    public GroupCallGridCell primaryView;
    /* access modifiers changed from: private */
    public float progressToBackground;
    public float progressToNoVideoStub = 1.0f;
    float progressToSpeaking;
    private final ImageView screencastIcon;
    public GroupCallFullscreenAdapter.GroupCallUserCell secondaryView;
    public boolean showBackground;
    Runnable showBackgroundRunnable = new Runnable() {
        public void run() {
            if (!GroupCallMiniTextureView.this.textureView.renderer.isFirstFrameRendered()) {
                GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                groupCallMiniTextureView.showBackground = true;
                groupCallMiniTextureView.textureView.invalidate();
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean showingAsScrimView;
    public boolean showingInFullscreen;
    float spanCount;
    Paint speakingPaint = new Paint(1);
    /* access modifiers changed from: private */
    public GroupCallStatusIcon statusIcon;
    private boolean swipeToBack;
    private float swipeToBackDy;
    public GroupCallGridCell tabletGridView;
    public final VoIPTextureView textureView;
    Bitmap thumb;
    Paint thumbPaint;
    private boolean updateNextLayoutAnimated;
    boolean useSpanSize;
    /* access modifiers changed from: private */
    public boolean videoIsPaused;
    /* access modifiers changed from: private */
    public float videoIsPausedProgress;

    static /* synthetic */ float access$116(GroupCallMiniTextureView groupCallMiniTextureView, float f) {
        float f2 = groupCallMiniTextureView.progressToBackground + f;
        groupCallMiniTextureView.progressToBackground = f2;
        return f2;
    }

    static /* synthetic */ float access$216(GroupCallMiniTextureView groupCallMiniTextureView, float f) {
        float f2 = groupCallMiniTextureView.videoIsPausedProgress + f;
        groupCallMiniTextureView.videoIsPausedProgress = f2;
        return f2;
    }

    static /* synthetic */ float access$224(GroupCallMiniTextureView groupCallMiniTextureView, float f) {
        float f2 = groupCallMiniTextureView.videoIsPausedProgress - f;
        groupCallMiniTextureView.videoIsPausedProgress = f2;
        return f2;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GroupCallMiniTextureView(GroupCallRenderersContainer groupCallRenderersContainer, ArrayList<GroupCallMiniTextureView> arrayList, ChatObject.Call call2, GroupCallActivity groupCallActivity) {
        super(groupCallRenderersContainer.getContext());
        final ChatObject.Call call3 = call2;
        this.call = call3;
        this.currentAccount = groupCallActivity.getCurrentAccount();
        CrossOutDrawable crossOutDrawable = new CrossOutDrawable(groupCallRenderersContainer.getContext(), NUM, (String) null);
        this.pausedVideoDrawable = crossOutDrawable;
        crossOutDrawable.setCrossOut(true, false);
        this.pausedVideoDrawable.setOffsets((float) (-AndroidUtilities.dp(4.0f)), (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f));
        this.pausedVideoDrawable.setStrokeWidth(AndroidUtilities.dpf2(3.4f));
        final TextPaint textPaint = new TextPaint(1);
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        textPaint.setColor(-1);
        final String string = LocaleController.getString("VoipVideoOnPause", NUM);
        final GroupCallActivity groupCallActivity2 = groupCallActivity;
        AnonymousClass2 r14 = r0;
        final GroupCallRenderersContainer groupCallRenderersContainer2 = groupCallRenderersContainer;
        final float measureText = textPaint.measureText(string);
        AnonymousClass2 r0 = new VoIPTextureView(groupCallRenderersContainer.getContext(), false, false, true, true) {
            float overlayIconAlphaFrom;

            public void animateToLayout() {
                super.animateToLayout();
                this.overlayIconAlphaFrom = GroupCallMiniTextureView.this.overlayIconAlpha;
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                float f;
                if (GroupCallMiniTextureView.this.showBackground && (!this.renderer.isFirstFrameRendered() || this.renderer.getAlpha() != 1.0f || GroupCallMiniTextureView.this.videoIsPaused)) {
                    if (GroupCallMiniTextureView.this.progressToBackground != 1.0f) {
                        GroupCallMiniTextureView.access$116(GroupCallMiniTextureView.this, 0.10666667f);
                        if (GroupCallMiniTextureView.this.progressToBackground > 1.0f) {
                            float unused = GroupCallMiniTextureView.this.progressToBackground = 1.0f;
                        } else {
                            invalidate();
                        }
                    }
                    GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                    if (groupCallMiniTextureView.thumb != null) {
                        canvas.save();
                        float f2 = this.currentThumbScale;
                        canvas.scale(f2, f2, ((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f);
                        GroupCallMiniTextureView groupCallMiniTextureView2 = GroupCallMiniTextureView.this;
                        if (groupCallMiniTextureView2.thumbPaint == null) {
                            groupCallMiniTextureView2.thumbPaint = new Paint(1);
                            GroupCallMiniTextureView.this.thumbPaint.setFilterBitmap(true);
                        }
                        canvas.drawBitmap(GroupCallMiniTextureView.this.thumb, ((float) (getMeasuredWidth() - GroupCallMiniTextureView.this.thumb.getWidth())) / 2.0f, ((float) (getMeasuredHeight() - GroupCallMiniTextureView.this.thumb.getHeight())) / 2.0f, GroupCallMiniTextureView.this.thumbPaint);
                        canvas.restore();
                    } else {
                        groupCallMiniTextureView.imageReceiver.setImageCoords(this.currentClipHorizontal, this.currentClipVertical, ((float) getMeasuredWidth()) - (this.currentClipHorizontal * 2.0f), ((float) getMeasuredHeight()) - (this.currentClipVertical * 2.0f));
                        GroupCallMiniTextureView groupCallMiniTextureView3 = GroupCallMiniTextureView.this;
                        groupCallMiniTextureView3.imageReceiver.setAlpha(groupCallMiniTextureView3.progressToBackground);
                        GroupCallMiniTextureView.this.imageReceiver.draw(canvas);
                    }
                    groupCallActivity2.cellFlickerDrawable.draw(canvas, GroupCallMiniTextureView.this);
                    invalidate();
                }
                super.dispatchDraw(canvas);
                canvas.save();
                float measuredHeight = (((float) getMeasuredHeight()) - this.currentClipVertical) - ((float) AndroidUtilities.dp(80.0f));
                GroupCallMiniTextureView groupCallMiniTextureView4 = GroupCallMiniTextureView.this;
                if ((groupCallMiniTextureView4.showingInFullscreen || groupCallMiniTextureView4.animateToFullscreen) && !GroupCallActivity.isLandscapeMode) {
                    GroupCallRenderersContainer groupCallRenderersContainer = groupCallRenderersContainer2;
                    measuredHeight -= (((float) AndroidUtilities.dp(90.0f)) * groupCallRenderersContainer.progressToFullscreenMode) * (1.0f - groupCallRenderersContainer.progressToHideUi);
                }
                canvas.translate(0.0f, measuredHeight);
                canvas.drawPaint(GroupCallMiniTextureView.this.gradientPaint);
                canvas.restore();
                if (GroupCallMiniTextureView.this.videoIsPaused || GroupCallMiniTextureView.this.videoIsPausedProgress != 1.0f) {
                    if (GroupCallMiniTextureView.this.videoIsPaused && GroupCallMiniTextureView.this.videoIsPausedProgress != 1.0f) {
                        GroupCallMiniTextureView.access$216(GroupCallMiniTextureView.this, 0.064f);
                        if (GroupCallMiniTextureView.this.videoIsPausedProgress > 1.0f) {
                            float unused2 = GroupCallMiniTextureView.this.videoIsPausedProgress = 1.0f;
                        } else {
                            invalidate();
                        }
                    } else if (!GroupCallMiniTextureView.this.videoIsPaused && GroupCallMiniTextureView.this.videoIsPausedProgress != 0.0f) {
                        GroupCallMiniTextureView.access$224(GroupCallMiniTextureView.this, 0.064f);
                        if (GroupCallMiniTextureView.this.videoIsPausedProgress < 0.0f) {
                            float unused3 = GroupCallMiniTextureView.this.videoIsPausedProgress = 0.0f;
                        } else {
                            invalidate();
                        }
                    }
                    float access$200 = GroupCallMiniTextureView.this.videoIsPausedProgress;
                    if (isInAnimation()) {
                        float f3 = this.overlayIconAlphaFrom;
                        float f4 = this.animationProgress;
                        f = (f3 * (1.0f - f4)) + (GroupCallMiniTextureView.this.overlayIconAlpha * f4);
                    } else {
                        f = GroupCallMiniTextureView.this.overlayIconAlpha;
                    }
                    float f5 = access$200 * f;
                    if (f5 > 0.0f) {
                        float dp = (float) AndroidUtilities.dp(48.0f);
                        float measuredWidth = (((float) getMeasuredWidth()) - dp) / 2.0f;
                        float measuredHeight2 = (((float) getMeasuredHeight()) - dp) / 2.0f;
                        RectF rectF = AndroidUtilities.rectTmp;
                        float f6 = measuredHeight2 + dp;
                        rectF.set((float) ((int) measuredWidth), (float) ((int) measuredHeight2), (float) ((int) (measuredWidth + dp)), (float) ((int) f6));
                        if (f5 != 1.0f) {
                            canvas.saveLayerAlpha(rectF, (int) (f5 * 255.0f), 31);
                        } else {
                            canvas.save();
                        }
                        GroupCallMiniTextureView.this.pausedVideoDrawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                        GroupCallMiniTextureView.this.pausedVideoDrawable.draw(canvas);
                        canvas.restore();
                        float f7 = f5 * groupCallRenderersContainer2.progressToFullscreenMode;
                        if (f7 > 0.0f) {
                            textPaint.setAlpha((int) (f7 * 255.0f));
                            canvas.drawText(string, (measuredWidth - (measureText / 2.0f)) + (dp / 2.0f), f6 + ((float) AndroidUtilities.dp(16.0f)), textPaint);
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                if (!groupCallMiniTextureView.inPinchToZoom || view != groupCallMiniTextureView.textureView.renderer) {
                    return super.drawChild(canvas, view, j);
                }
                canvas.save();
                GroupCallMiniTextureView groupCallMiniTextureView2 = GroupCallMiniTextureView.this;
                float f = groupCallMiniTextureView2.pinchScale;
                canvas.scale(f, f, groupCallMiniTextureView2.pinchCenterX, groupCallMiniTextureView2.pinchCenterY);
                GroupCallMiniTextureView groupCallMiniTextureView3 = GroupCallMiniTextureView.this;
                canvas.translate(groupCallMiniTextureView3.pinchTranslationX, groupCallMiniTextureView3.pinchTranslationY);
                boolean drawChild = super.drawChild(canvas, view, j);
                canvas.restore();
                return drawChild;
            }

            public void invalidate() {
                super.invalidate();
                boolean unused = GroupCallMiniTextureView.this.invalidateFromChild = true;
                GroupCallMiniTextureView.this.invalidate();
                boolean unused2 = GroupCallMiniTextureView.this.invalidateFromChild = false;
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int i5;
                ChatObject.VideoParticipant videoParticipant;
                GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                if (groupCallMiniTextureView.attached && groupCallMiniTextureView.checkScale) {
                    TextureViewRenderer textureViewRenderer = this.renderer;
                    if (!(textureViewRenderer.rotatedFrameHeight == 0 || textureViewRenderer.rotatedFrameWidth == 0)) {
                        if (GroupCallMiniTextureView.this.showingAsScrimView) {
                            GroupCallMiniTextureView.this.textureView.scaleType = VoIPTextureView.SCALE_TYPE_FIT;
                        } else {
                            GroupCallMiniTextureView groupCallMiniTextureView2 = GroupCallMiniTextureView.this;
                            boolean z2 = groupCallMiniTextureView2.showingInFullscreen;
                            if (z2) {
                                groupCallMiniTextureView2.textureView.scaleType = VoIPTextureView.SCALE_TYPE_FIT;
                            } else {
                                boolean z3 = groupCallRenderersContainer2.inFullscreenMode;
                                if (z3 && !z2) {
                                    groupCallMiniTextureView2.textureView.scaleType = VoIPTextureView.SCALE_TYPE_FILL;
                                } else if (!z3) {
                                    groupCallMiniTextureView2.textureView.scaleType = groupCallMiniTextureView2.participant.presentation ? VoIPTextureView.SCALE_TYPE_FIT : VoIPTextureView.SCALE_TYPE_ADAPTIVE;
                                } else {
                                    groupCallMiniTextureView2.textureView.scaleType = VoIPTextureView.SCALE_TYPE_FIT;
                                }
                            }
                        }
                        boolean unused = GroupCallMiniTextureView.this.checkScale = false;
                    }
                }
                super.onLayout(z, i, i2, i3, i4);
                TextureViewRenderer textureViewRenderer2 = this.renderer;
                int i6 = textureViewRenderer2.rotatedFrameHeight;
                if (i6 != 0 && (i5 = textureViewRenderer2.rotatedFrameWidth) != 0 && (videoParticipant = GroupCallMiniTextureView.this.participant) != null) {
                    videoParticipant.setAspectRatio(((float) i5) / ((float) i6), call3);
                }
            }

            public void requestLayout() {
                GroupCallMiniTextureView.this.requestLayout();
                super.requestLayout();
            }

            /* access modifiers changed from: protected */
            public void onFirstFrameRendered() {
                invalidate();
                if (!GroupCallMiniTextureView.this.videoIsPaused && this.renderer.getAlpha() != 1.0f) {
                    this.renderer.animate().setDuration(300).alpha(1.0f);
                }
                TextureView textureView = this.blurRenderer;
                if (textureView != null && textureView.getAlpha() != 1.0f) {
                    this.blurRenderer.animate().setDuration(300).alpha(1.0f);
                }
            }
        };
        this.textureView = r14;
        r14.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        this.parentContainer = groupCallRenderersContainer;
        this.attachedRenderers = arrayList;
        this.activity = groupCallActivity;
        r14.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
            public void onFrameResolutionChanged(int i, int i2, int i3) {
            }

            public void onFirstFrameRendered() {
                for (int i = 0; i < GroupCallMiniTextureView.this.onFirstFrameRunnables.size(); i++) {
                    AndroidUtilities.cancelRunOnUIThread(GroupCallMiniTextureView.this.onFirstFrameRunnables.get(i));
                    GroupCallMiniTextureView.this.onFirstFrameRunnables.get(i).run();
                }
                GroupCallMiniTextureView.this.onFirstFrameRunnables.clear();
            }
        });
        r14.attachBackgroundRenderer();
        AndroidUtilities.runOnUIThread(this.showBackgroundRunnable, 250);
        setClipChildren(false);
        r14.renderer.setAlpha(0.0f);
        addView(r14);
        NoVideoStubLayout noVideoStubLayout2 = new NoVideoStubLayout(getContext());
        this.noVideoStubLayout = noVideoStubLayout2;
        addView(noVideoStubLayout2);
        SimpleTextView simpleTextView = new SimpleTextView(groupCallRenderersContainer.getContext());
        this.nameView = simpleTextView;
        simpleTextView.setTextSize(13);
        simpleTextView.setTextColor(ColorUtils.setAlphaComponent(-1, 229));
        simpleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        simpleTextView.setFullTextMaxLines(1);
        simpleTextView.setBuildFullLayout(true);
        FrameLayout frameLayout = new FrameLayout(groupCallRenderersContainer.getContext());
        this.infoContainer = frameLayout;
        frameLayout.addView(simpleTextView, LayoutHelper.createFrame(-1, -2.0f, 19, 32.0f, 0.0f, 8.0f, 0.0f));
        addView(this.infoContainer, LayoutHelper.createFrame(-1, 32.0f));
        this.speakingPaint.setStyle(Paint.Style.STROKE);
        this.speakingPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.speakingPaint.setColor(Theme.getColor("voipgroup_speakingText"));
        this.infoContainer.setClipChildren(false);
        RLottieImageView rLottieImageView = new RLottieImageView(groupCallRenderersContainer.getContext());
        this.micIconView = rLottieImageView;
        addView(rLottieImageView, LayoutHelper.createFrame(24, 24.0f, 0, 4.0f, 6.0f, 4.0f, 0.0f));
        ImageView imageView = new ImageView(groupCallRenderersContainer.getContext());
        this.screencastIcon = imageView;
        addView(imageView, LayoutHelper.createFrame(24, 24.0f, 0, 4.0f, 6.0f, 4.0f, 0.0f));
        imageView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
        imageView.setImageDrawable(ContextCompat.getDrawable(groupCallRenderersContainer.getContext(), NUM));
        imageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.attached) {
            float y = (((this.textureView.getY() + ((float) this.textureView.getMeasuredHeight())) - this.textureView.currentClipVertical) - ((float) this.infoContainer.getMeasuredHeight())) + this.swipeToBackDy;
            if (this.showingAsScrimView || this.animateToScrimView) {
                this.infoContainer.setAlpha(1.0f - this.parentContainer.progressToScrimView);
                this.micIconView.setAlpha(1.0f - this.parentContainer.progressToScrimView);
            } else if (this.showingInFullscreen || this.animateToFullscreen) {
                if (!GroupCallActivity.isLandscapeMode) {
                    GroupCallRenderersContainer groupCallRenderersContainer = this.parentContainer;
                    y -= (((float) AndroidUtilities.dp(90.0f)) * groupCallRenderersContainer.progressToFullscreenMode) * (1.0f - groupCallRenderersContainer.progressToHideUi);
                }
                this.infoContainer.setAlpha(1.0f);
                this.micIconView.setAlpha(1.0f);
            } else if (this.secondaryView != null) {
                this.infoContainer.setAlpha(1.0f - this.parentContainer.progressToFullscreenMode);
                this.micIconView.setAlpha(1.0f - this.parentContainer.progressToFullscreenMode);
            } else {
                this.infoContainer.setAlpha(1.0f);
                this.micIconView.setAlpha(1.0f);
            }
            if (this.showingInFullscreen || this.animateToFullscreen) {
                this.nameView.setFullAlpha(this.parentContainer.progressToFullscreenMode);
            } else {
                this.nameView.setFullAlpha(0.0f);
            }
            this.micIconView.setTranslationX(this.infoContainer.getX());
            this.micIconView.setTranslationY(y - ((float) AndroidUtilities.dp(2.0f)));
            if (this.screencastIcon.getVisibility() == 0) {
                this.screencastIcon.setTranslationX((((float) this.textureView.getMeasuredWidth()) - (this.textureView.currentClipHorizontal * 2.0f)) - ((float) AndroidUtilities.dp(32.0f)));
                this.screencastIcon.setTranslationY(y - ((float) AndroidUtilities.dp(2.0f)));
                ImageView imageView = this.screencastIcon;
                GroupCallRenderersContainer groupCallRenderersContainer2 = this.parentContainer;
                imageView.setAlpha(Math.min(1.0f - groupCallRenderersContainer2.progressToFullscreenMode, 1.0f - groupCallRenderersContainer2.progressToScrimView));
            }
            this.infoContainer.setTranslationY(y);
        }
        super.dispatchDraw(canvas);
        if (this.attached) {
            GroupCallStatusIcon groupCallStatusIcon = this.statusIcon;
            if (groupCallStatusIcon != null) {
                boolean z = groupCallStatusIcon.isSpeaking;
                if (z) {
                    float f = this.progressToSpeaking;
                    if (f != 1.0f) {
                        float f2 = f + 0.053333335f;
                        this.progressToSpeaking = f2;
                        if (f2 > 1.0f) {
                            this.progressToSpeaking = 1.0f;
                        } else {
                            invalidate();
                        }
                    }
                }
                if (!z) {
                    float f3 = this.progressToSpeaking;
                    if (f3 != 0.0f) {
                        float f4 = f3 - 0.053333335f;
                        this.progressToSpeaking = f4;
                        if (f4 < 0.0f) {
                            this.progressToSpeaking = 0.0f;
                        } else {
                            invalidate();
                        }
                    }
                }
            }
            float f5 = this.progressToSpeaking;
            GroupCallRenderersContainer groupCallRenderersContainer3 = this.parentContainer;
            float f6 = (1.0f - groupCallRenderersContainer3.progressToFullscreenMode) * f5 * (1.0f - groupCallRenderersContainer3.progressToScrimView);
            if (f5 > 0.0f) {
                this.speakingPaint.setAlpha((int) (f6 * 255.0f));
                float max = (Math.max(0.0f, 1.0f - (Math.abs(this.swipeToBackDy) / ((float) AndroidUtilities.dp(300.0f)))) * 0.1f) + 0.9f;
                canvas.save();
                RectF rectF = AndroidUtilities.rectTmp;
                float x = this.textureView.getX();
                VoIPTextureView voIPTextureView = this.textureView;
                float f7 = x + voIPTextureView.currentClipHorizontal;
                float y2 = voIPTextureView.getY();
                VoIPTextureView voIPTextureView2 = this.textureView;
                float f8 = y2 + voIPTextureView2.currentClipVertical;
                float x2 = voIPTextureView2.getX() + ((float) this.textureView.getMeasuredWidth());
                VoIPTextureView voIPTextureView3 = this.textureView;
                rectF.set(f7, f8, x2 - voIPTextureView3.currentClipHorizontal, (voIPTextureView3.getY() + ((float) this.textureView.getMeasuredHeight())) - this.textureView.currentClipVertical);
                canvas.scale(max, max, rectF.centerX(), rectF.centerY());
                canvas.translate(0.0f, this.swipeToBackDy);
                float f9 = this.textureView.roundRadius;
                canvas.drawRoundRect(rectF, f9, f9, this.speakingPaint);
                canvas.restore();
            }
        }
    }

    public void getRenderBufferBitmap(GlGenericDrawer.TextureCallback textureCallback) {
        this.textureView.renderer.getRenderBufferBitmap(textureCallback);
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        if (!this.swipeToBack || (view != this.textureView && view != this.noVideoStubLayout)) {
            return super.drawChild(canvas, view, j);
        }
        float max = (Math.max(0.0f, 1.0f - (Math.abs(this.swipeToBackDy) / ((float) AndroidUtilities.dp(300.0f)))) * 0.1f) + 0.9f;
        canvas.save();
        canvas.scale(max, max, view.getX() + (((float) view.getMeasuredWidth()) / 2.0f), view.getY() + (((float) view.getMeasuredHeight()) / 2.0f));
        canvas.translate(0.0f, this.swipeToBackDy);
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restore();
        return drawChild;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0195  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x01a6  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x01c2  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x01cc  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r13, int r14) {
        /*
            r12 = this;
            android.widget.FrameLayout r0 = r12.infoContainer
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            int r1 = r0.leftMargin
            boolean r2 = r12.lastLandscapeMode
            boolean r3 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r2 == r3) goto L_0x0015
            r2 = 1
            r12.checkScale = r2
            r12.lastLandscapeMode = r3
        L_0x0015:
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.rightMargin = r2
            r0.leftMargin = r2
            boolean r2 = r12.updateNextLayoutAnimated
            r3 = 0
            r4 = 1065353216(0x3var_, float:1.0)
            if (r2 == 0) goto L_0x006d
            org.telegram.ui.ActionBar.SimpleTextView r2 = r12.nameView
            android.view.ViewPropertyAnimator r2 = r2.animate()
            android.view.ViewPropertyAnimator r2 = r2.scaleX(r4)
            android.view.ViewPropertyAnimator r2 = r2.scaleY(r4)
            r2.start()
            org.telegram.ui.Components.RLottieImageView r2 = r12.micIconView
            android.view.ViewPropertyAnimator r2 = r2.animate()
            android.view.ViewPropertyAnimator r2 = r2.scaleX(r4)
            android.view.ViewPropertyAnimator r2 = r2.scaleY(r4)
            r2.start()
            int r0 = r0.leftMargin
            if (r0 == r1) goto L_0x00a1
            android.widget.FrameLayout r2 = r12.infoContainer
            int r1 = r1 - r0
            float r0 = (float) r1
            r2.setTranslationX(r0)
            android.widget.FrameLayout r0 = r12.infoContainer
            android.view.ViewPropertyAnimator r0 = r0.animate()
            android.view.ViewPropertyAnimator r0 = r0.translationX(r3)
            r1 = 350(0x15e, double:1.73E-321)
            android.view.ViewPropertyAnimator r0 = r0.setDuration(r1)
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r0 = r0.setInterpolator(r1)
            r0.start()
            goto L_0x00a1
        L_0x006d:
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.nameView
            android.view.ViewPropertyAnimator r0 = r0.animate()
            r0.cancel()
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.nameView
            r0.setScaleX(r4)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r12.nameView
            r0.setScaleY(r4)
            org.telegram.ui.Components.RLottieImageView r0 = r12.micIconView
            android.view.ViewPropertyAnimator r0 = r0.animate()
            r0.cancel()
            org.telegram.ui.Components.RLottieImageView r0 = r12.micIconView
            r0.setScaleX(r4)
            org.telegram.ui.Components.RLottieImageView r0 = r12.micIconView
            r0.setScaleY(r4)
            android.widget.FrameLayout r0 = r12.infoContainer
            android.view.ViewPropertyAnimator r0 = r0.animate()
            r0.cancel()
            android.widget.FrameLayout r0 = r12.infoContainer
            r0.setTranslationX(r3)
        L_0x00a1:
            r0 = 0
            r12.updateNextLayoutAnimated = r0
            boolean r1 = r12.showingInFullscreen
            r2 = 1073741824(0x40000000, float:2.0)
            r5 = 1082130432(0x40800000, float:4.0)
            r6 = 1073741824(0x40000000, float:2.0)
            if (r1 == 0) goto L_0x0112
            r12.updateSize(r0)
            r12.overlayIconAlpha = r4
            boolean r0 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r0 == 0) goto L_0x00d8
            int r0 = android.view.View.MeasureSpec.getSize(r13)
            r1 = 1134821376(0x43a40000, float:328.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            int r1 = android.view.View.MeasureSpec.getSize(r14)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r1 = r1 - r4
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r6)
            super.onMeasure(r0, r1)
            goto L_0x01ed
        L_0x00d8:
            boolean r0 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            r1 = 1119354880(0x42b80000, float:92.0)
            if (r0 != 0) goto L_0x00f8
            int r0 = android.view.View.MeasureSpec.getSize(r14)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            int r1 = android.view.View.MeasureSpec.getSize(r13)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r6)
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6)
            super.onMeasure(r1, r0)
            goto L_0x01ed
        L_0x00f8:
            int r0 = android.view.View.MeasureSpec.getSize(r13)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6)
            int r1 = android.view.View.MeasureSpec.getSize(r14)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r6)
            super.onMeasure(r0, r1)
            goto L_0x01ed
        L_0x0112:
            boolean r1 = r12.showingAsScrimView
            r7 = 1096810496(0x41600000, float:14.0)
            r8 = 2
            if (r1 == 0) goto L_0x0140
            r12.overlayIconAlpha = r4
            int r0 = android.view.View.MeasureSpec.getSize(r13)
            int r1 = android.view.View.MeasureSpec.getSize(r14)
            int r0 = java.lang.Math.min(r0, r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r1 = r1 * 2
            int r0 = r0 - r1
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6)
            int r4 = r12.getPaddingBottom()
            int r0 = r0 + r4
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6)
            super.onMeasure(r1, r0)
            goto L_0x01ed
        L_0x0140:
            boolean r1 = r12.useSpanSize
            if (r1 == 0) goto L_0x01e8
            r12.overlayIconAlpha = r4
            boolean r1 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r1 == 0) goto L_0x0150
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r12.tabletGridView
            if (r1 == 0) goto L_0x0150
            r1 = 6
            goto L_0x0157
        L_0x0150:
            boolean r1 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r1 == 0) goto L_0x0156
            r1 = 3
            goto L_0x0157
        L_0x0156:
            r1 = 2
        L_0x0157:
            org.telegram.ui.Components.voip.GroupCallGridCell r4 = r12.tabletGridView
            if (r4 == 0) goto L_0x0168
            int r0 = android.view.View.MeasureSpec.getSize(r13)
            r4 = 1135345664(0x43aCLASSNAME, float:344.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
        L_0x0166:
            float r0 = (float) r0
            goto L_0x018b
        L_0x0168:
            boolean r4 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r4 == 0) goto L_0x0173
            r0 = 1134559232(0x43a00000, float:320.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x0166
        L_0x0173:
            int r4 = android.view.View.MeasureSpec.getSize(r13)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 * 2
            int r4 = r4 - r7
            boolean r7 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r7 == 0) goto L_0x0189
            r0 = 1119092736(0x42b40000, float:90.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = -r0
        L_0x0189:
            int r4 = r4 + r0
            float r0 = (float) r4
        L_0x018b:
            float r4 = r12.spanCount
            float r1 = (float) r1
            float r4 = r4 / r1
            float r4 = r4 * r0
            org.telegram.ui.Components.voip.GroupCallGridCell r7 = r12.tabletGridView
            if (r7 == 0) goto L_0x01a6
            float r0 = r7.getItemHeight()
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r1 = (float) r1
            float r0 = r0 - r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r5)
        L_0x01a3:
            float r1 = (float) r1
            float r4 = r4 - r1
            goto L_0x01b2
        L_0x01a6:
            boolean r5 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r5 == 0) goto L_0x01ac
            float r0 = r0 / r2
            goto L_0x01ad
        L_0x01ac:
            float r0 = r0 / r1
        L_0x01ad:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            goto L_0x01a3
        L_0x01b2:
            android.widget.FrameLayout r1 = r12.infoContainer
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            android.widget.ImageView r5 = r12.screencastIcon
            int r5 = r5.getVisibility()
            if (r5 != 0) goto L_0x01cc
            r5 = 1105199104(0x41e00000, float:28.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r5 = r4 - r5
            goto L_0x01cd
        L_0x01cc:
            r5 = r4
        L_0x01cd:
            int r7 = (int) r5
            r12.updateSize(r7)
            int r7 = r1.leftMargin
            int r7 = r7 * 2
            float r7 = (float) r7
            float r5 = r5 - r7
            int r5 = (int) r5
            r1.width = r5
            int r1 = (int) r4
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r6)
            int r0 = (int) r0
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6)
            super.onMeasure(r1, r0)
            goto L_0x01ed
        L_0x01e8:
            r12.overlayIconAlpha = r3
            super.onMeasure(r13, r14)
        L_0x01ed:
            int r14 = android.view.View.MeasureSpec.getSize(r14)
            int r13 = android.view.View.MeasureSpec.getSize(r13)
            int r13 = r13 << 16
            int r14 = r14 + r13
            int r13 = r12.lastSize
            if (r13 == r14) goto L_0x0220
            r12.lastSize = r14
            android.graphics.LinearGradient r13 = new android.graphics.LinearGradient
            r5 = 0
            r6 = 0
            r7 = 0
            r14 = 1123024896(0x42var_, float:120.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r8 = (float) r14
            r9 = 0
            r14 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r0 = 120(0x78, float:1.68E-43)
            int r10 = androidx.core.graphics.ColorUtils.setAlphaComponent(r14, r0)
            android.graphics.Shader$TileMode r11 = android.graphics.Shader.TileMode.CLAMP
            r4 = r13
            r4.<init>(r5, r6, r7, r8, r9, r10, r11)
            r12.gradientShader = r13
            android.graphics.Paint r14 = r12.gradientPaint
            r14.setShader(r13)
        L_0x0220:
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.nameView
            r13.setPivotX(r3)
            org.telegram.ui.ActionBar.SimpleTextView r13 = r12.nameView
            int r14 = r13.getMeasuredHeight()
            float r14 = (float) r14
            float r14 = r14 / r2
            r13.setPivotY(r14)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallMiniTextureView.onMeasure(int, int):void");
    }

    public static GroupCallMiniTextureView getOrCreate(ArrayList<GroupCallMiniTextureView> arrayList, GroupCallRenderersContainer groupCallRenderersContainer, GroupCallGridCell groupCallGridCell, GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell, GroupCallGridCell groupCallGridCell2, ChatObject.VideoParticipant videoParticipant, ChatObject.Call call2, GroupCallActivity groupCallActivity) {
        GroupCallMiniTextureView groupCallMiniTextureView;
        int i = 0;
        while (true) {
            if (i >= arrayList.size()) {
                groupCallMiniTextureView = null;
                break;
            } else if (videoParticipant.equals(arrayList.get(i).participant)) {
                groupCallMiniTextureView = arrayList.get(i);
                break;
            } else {
                i++;
            }
        }
        if (groupCallMiniTextureView == null) {
            groupCallMiniTextureView = new GroupCallMiniTextureView(groupCallRenderersContainer, arrayList, call2, groupCallActivity);
        }
        if (groupCallGridCell != null) {
            groupCallMiniTextureView.setPrimaryView(groupCallGridCell);
        }
        if (groupCallUserCell != null) {
            groupCallMiniTextureView.setSecondaryView(groupCallUserCell);
        }
        if (groupCallGridCell2 != null) {
            groupCallMiniTextureView.setTabletGridView(groupCallGridCell2);
        }
        return groupCallMiniTextureView;
    }

    public void setTabletGridView(GroupCallGridCell groupCallGridCell) {
        if (this.tabletGridView != groupCallGridCell) {
            this.tabletGridView = groupCallGridCell;
            updateAttachState(true);
        }
    }

    public void setPrimaryView(GroupCallGridCell groupCallGridCell) {
        if (this.primaryView != groupCallGridCell) {
            this.primaryView = groupCallGridCell;
            this.checkScale = true;
            updateAttachState(true);
        }
    }

    public void setSecondaryView(GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell) {
        if (this.secondaryView != groupCallUserCell) {
            this.secondaryView = groupCallUserCell;
            this.checkScale = true;
            updateAttachState(true);
        }
    }

    public void setShowingAsScrimView(boolean z, boolean z2) {
        this.showingAsScrimView = z;
        updateAttachState(z2);
    }

    public void setShowingInFullscreen(boolean z, boolean z2) {
        if (this.showingInFullscreen != z) {
            this.showingInFullscreen = z;
            this.checkScale = true;
            updateAttachState(z2);
        }
    }

    public void setFullscreenMode(boolean z, boolean z2) {
        if (this.isFullscreenMode != z) {
            this.isFullscreenMode = z;
            updateAttachState(!(this.primaryView == null && this.tabletGridView == null) && z2);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v50, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v56, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v104, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v105, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX WARNING: Code restructure failed: missing block: B:103:0x0194, code lost:
        if (r1 != false) goto L_0x0196;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x038b  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x042d  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x0438  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0440  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x0467  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x047e  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x0494  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x049e  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x04b3  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0547  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0563  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x056d  */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x05b1  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x05d6  */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x063e  */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x0655  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateAttachState(boolean r22) {
        /*
            r21 = this;
            r0 = r21
            boolean r1 = r0.forceDetached
            if (r1 == 0) goto L_0x0007
            return
        L_0x0007:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.participant
            if (r1 != 0) goto L_0x0033
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r0.primaryView
            if (r1 != 0) goto L_0x0017
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r2 = r0.secondaryView
            if (r2 != 0) goto L_0x0017
            org.telegram.ui.Components.voip.GroupCallGridCell r2 = r0.tabletGridView
            if (r2 == 0) goto L_0x0033
        L_0x0017:
            if (r1 == 0) goto L_0x0020
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.getParticipant()
            r0.participant = r1
            goto L_0x0033
        L_0x0020:
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r0.tabletGridView
            if (r1 == 0) goto L_0x002b
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.getParticipant()
            r0.participant = r1
            goto L_0x0033
        L_0x002b:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r1 = r0.secondaryView
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.getVideoParticipant()
            r0.participant = r1
        L_0x0033:
            boolean r1 = r0.attached
            r3 = 1056964608(0x3var_, float:0.5)
            r4 = 2
            r5 = 0
            r6 = 0
            r7 = -1
            r8 = 1065353216(0x3var_, float:1.0)
            r9 = 1
            r10 = 0
            if (r1 == 0) goto L_0x0106
            boolean r11 = r0.showingInFullscreen
            if (r11 != 0) goto L_0x0106
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 != 0) goto L_0x004d
            r1 = 1
            goto L_0x004e
        L_0x004d:
            r1 = 0
        L_0x004e:
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r0.participant
            if (r11 == 0) goto L_0x0062
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r12 = r0.secondaryView
            if (r12 != 0) goto L_0x0063
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r11.participant
            boolean r11 = r11.presentation
            org.telegram.messenger.ChatObject$Call r13 = r0.call
            boolean r11 = org.telegram.ui.GroupCallActivity.videoIsActive(r12, r11, r13)
            if (r11 != 0) goto L_0x0063
        L_0x0062:
            r1 = 1
        L_0x0063:
            if (r1 != 0) goto L_0x0079
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r0.primaryView
            if (r11 != 0) goto L_0x0384
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r11 = r0.secondaryView
            if (r11 != 0) goto L_0x0384
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r0.tabletGridView
            if (r11 != 0) goto L_0x0384
            boolean r11 = r0.showingAsScrimView
            if (r11 != 0) goto L_0x0384
            boolean r11 = r0.animateToScrimView
            if (r11 != 0) goto L_0x0384
        L_0x0079:
            r0.attached = r10
            r21.saveThumb()
            org.telegram.ui.Components.voip.VoIPTextureView r11 = r0.textureView
            android.animation.ValueAnimator r11 = r11.currentAnimation
            if (r11 != 0) goto L_0x00ae
            if (r1 == 0) goto L_0x00ae
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r1 = r0.parentContainer
            r1.detach(r0)
            android.view.ViewPropertyAnimator r1 = r21.animate()
            android.view.ViewPropertyAnimator r1 = r1.scaleX(r3)
            android.view.ViewPropertyAnimator r1 = r1.scaleY(r3)
            android.view.ViewPropertyAnimator r1 = r1.alpha(r6)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$4 r3 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$4
            r3.<init>(r0)
            android.view.ViewPropertyAnimator r1 = r1.setListener(r3)
            r11 = 150(0x96, double:7.4E-322)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r11)
            r1.start()
            goto L_0x00c8
        L_0x00ae:
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r1 = r0.parentContainer
            boolean r3 = r1.inLayout
            if (r3 == 0) goto L_0x00bd
            org.telegram.ui.Components.voip.-$$Lambda$GroupCallMiniTextureView$AXdOlW2ejCqMgoPFkTgBrW-4bBY r1 = new org.telegram.ui.Components.voip.-$$Lambda$GroupCallMiniTextureView$AXdOlW2ejCqMgoPFkTgBrW-4bBY
            r1.<init>(r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            goto L_0x00c0
        L_0x00bd:
            r1.removeView(r0)
        L_0x00c0:
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r1 = r0.parentContainer
            r1.detach(r0)
            r21.release()
        L_0x00c8:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r1.participant
            boolean r1 = r1.self
            if (r1 == 0) goto L_0x00e2
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x00f5
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            boolean r3 = r3.presentation
            r1.setLocalSink(r5, r3)
            goto L_0x00f5
        L_0x00e2:
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x00f5
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11 = r3.participant
            boolean r3 = r3.presentation
            r1.removeRemoteSink(r11, r3)
        L_0x00f5:
            r21.invalidate()
            android.animation.ValueAnimator r1 = r0.noVideoStubAnimator
            if (r1 == 0) goto L_0x0384
            r1.removeAllListeners()
            android.animation.ValueAnimator r1 = r0.noVideoStubAnimator
            r1.cancel()
            goto L_0x0384
        L_0x0106:
            if (r1 != 0) goto L_0x0384
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 != 0) goto L_0x010f
            return
        L_0x010f:
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r0.primaryView
            if (r1 != 0) goto L_0x011f
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r11 = r0.secondaryView
            if (r11 != 0) goto L_0x011f
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r0.tabletGridView
            if (r11 != 0) goto L_0x011f
            boolean r11 = r0.showingInFullscreen
            if (r11 == 0) goto L_0x0384
        L_0x011f:
            if (r1 == 0) goto L_0x0128
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.getParticipant()
            r0.participant = r1
            goto L_0x013d
        L_0x0128:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r1 = r0.secondaryView
            if (r1 == 0) goto L_0x0133
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.getVideoParticipant()
            r0.participant = r1
            goto L_0x013d
        L_0x0133:
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r0.tabletGridView
            if (r1 == 0) goto L_0x013d
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.getParticipant()
            r0.participant = r1
        L_0x013d:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11 = r1.participant
            boolean r12 = r11.self
            if (r12 == 0) goto L_0x0168
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x015b
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r0.participant
            boolean r11 = r11.presentation
            int r1 = r1.getVideoState(r11)
            if (r1 != r4) goto L_0x015b
            r1 = 1
            goto L_0x015c
        L_0x015b:
            r1 = 0
        L_0x015c:
            org.telegram.messenger.ChatObject$Call r11 = r0.call
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11 = r11.fullscreenParticipant
            if (r11 == 0) goto L_0x0170
            boolean r11 = r11.self
            if (r11 == 0) goto L_0x0170
            r1 = 0
            goto L_0x0170
        L_0x0168:
            boolean r1 = r1.presentation
            org.telegram.messenger.ChatObject$Call r12 = r0.call
            boolean r1 = org.telegram.ui.GroupCallActivity.videoIsActive(r11, r1, r12)
        L_0x0170:
            boolean r11 = r0.showingInFullscreen
            if (r11 != 0) goto L_0x0196
            org.telegram.messenger.voip.VoIPService r11 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r12 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r13 = r12.participant
            boolean r12 = r12.presentation
            boolean r11 = r11.isFullscreen(r13, r12)
            if (r11 != 0) goto L_0x0384
            org.telegram.messenger.voip.VoIPService r11 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r12 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r13 = r12.participant
            boolean r12 = r12.presentation
            boolean r11 = r11.isFullscreen(r13, r12)
            if (r11 != 0) goto L_0x0384
            if (r1 == 0) goto L_0x0384
        L_0x0196:
            boolean r1 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r1 == 0) goto L_0x01c0
            r1 = 0
        L_0x019b:
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r11 = r0.attachedRenderers
            int r11 = r11.size()
            if (r1 >= r11) goto L_0x01c0
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r11 = r0.attachedRenderers
            java.lang.Object r11 = r11.get(r1)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r11 = (org.telegram.ui.Components.voip.GroupCallMiniTextureView) r11
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r11.participant
            org.telegram.messenger.ChatObject$VideoParticipant r12 = r0.participant
            boolean r11 = r11.equals(r12)
            if (r11 != 0) goto L_0x01b8
            int r1 = r1 + 1
            goto L_0x019b
        L_0x01b8:
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            java.lang.String r2 = "try add two same renderers"
            r1.<init>(r2)
            throw r1
        L_0x01c0:
            r0.attached = r9
            org.telegram.ui.GroupCallActivity r1 = r0.activity
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallStatusIcon> r1 = r1.statusIconPool
            int r1 = r1.size()
            if (r1 <= 0) goto L_0x01de
            org.telegram.ui.GroupCallActivity r1 = r0.activity
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallStatusIcon> r1 = r1.statusIconPool
            int r11 = r1.size()
            int r11 = r11 - r9
            java.lang.Object r1 = r1.remove(r11)
            org.telegram.ui.Components.voip.GroupCallStatusIcon r1 = (org.telegram.ui.Components.voip.GroupCallStatusIcon) r1
            r0.statusIcon = r1
            goto L_0x01e5
        L_0x01de:
            org.telegram.ui.Components.voip.GroupCallStatusIcon r1 = new org.telegram.ui.Components.voip.GroupCallStatusIcon
            r1.<init>()
            r0.statusIcon = r1
        L_0x01e5:
            org.telegram.ui.Components.voip.GroupCallStatusIcon r1 = r0.statusIcon
            r1.setCallback(r0)
            org.telegram.ui.Components.voip.GroupCallStatusIcon r1 = r0.statusIcon
            org.telegram.ui.Components.RLottieImageView r11 = r0.micIconView
            r1.setImageView(r11)
            r0.updateIconColor(r10)
            android.view.ViewParent r1 = r21.getParent()
            if (r1 != 0) goto L_0x020c
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r1 = r0.parentContainer
            r11 = 51
            r12 = 46
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r12, r11)
            r1.addView(r0, r11)
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r1 = r0.parentContainer
            r1.attach(r0)
        L_0x020c:
            r0.checkScale = r9
            r0.animateEnter = r10
            android.view.ViewPropertyAnimator r1 = r21.animate()
            android.view.ViewPropertyAnimator r1 = r1.setListener(r5)
            r1.cancel()
            org.telegram.ui.Components.voip.VoIPTextureView r1 = r0.textureView
            android.animation.ValueAnimator r1 = r1.currentAnimation
            if (r1 != 0) goto L_0x0263
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r1 = r0.secondaryView
            if (r1 == 0) goto L_0x0263
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r0.primaryView
            if (r1 != 0) goto L_0x0263
            boolean r1 = r21.hasImage()
            if (r1 != 0) goto L_0x0263
            r0.setScaleX(r3)
            r0.setScaleY(r3)
            r0.setAlpha(r6)
            r0.animateEnter = r9
            r21.invalidate()
            android.view.ViewPropertyAnimator r1 = r21.animate()
            android.view.ViewPropertyAnimator r1 = r1.scaleX(r8)
            android.view.ViewPropertyAnimator r1 = r1.scaleY(r8)
            android.view.ViewPropertyAnimator r1 = r1.alpha(r8)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$5 r3 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$5
            r3.<init>()
            android.view.ViewPropertyAnimator r1 = r1.setListener(r3)
            r11 = 100
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r11)
            r1.start()
            r21.invalidate()
            goto L_0x026c
        L_0x0263:
            r0.setScaleY(r8)
            r0.setScaleX(r8)
            r0.setAlpha(r8)
        L_0x026c:
            org.telegram.messenger.ChatObject$Call r1 = r0.call
            java.util.HashMap<java.lang.String, android.graphics.Bitmap> r1 = r1.thumbs
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            boolean r11 = r3.presentation
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            if (r11 == 0) goto L_0x027b
            java.lang.String r3 = r3.presentationEndpoint
            goto L_0x027d
        L_0x027b:
            java.lang.String r3 = r3.videoEndpoint
        L_0x027d:
            java.lang.Object r1 = r1.get(r3)
            android.graphics.Bitmap r1 = (android.graphics.Bitmap) r1
            r0.thumb = r1
            org.telegram.ui.Components.voip.VoIPTextureView r3 = r0.textureView
            r3.setThumb(r1)
            android.graphics.Bitmap r1 = r0.thumb
            if (r1 != 0) goto L_0x032c
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r1.participant
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer
            int r1 = org.telegram.messenger.MessageObject.getPeerId(r1)
            r3 = 1053609165(0x3ecccccd, float:0.4)
            r11 = 1045220557(0x3e4ccccd, float:0.2)
            r12 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            if (r1 <= 0) goto L_0x02e8
            int r13 = r0.currentAccount
            org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r13.getUser(r1)
            org.telegram.messenger.ImageLocation r15 = org.telegram.messenger.ImageLocation.getForUser(r1, r9)
            if (r1 == 0) goto L_0x02bd
            int r13 = r1.id
            int r13 = org.telegram.ui.Components.AvatarDrawable.getColorForId(r13)
            goto L_0x02c1
        L_0x02bd:
            int r13 = androidx.core.graphics.ColorUtils.blendARGB(r12, r7, r11)
        L_0x02c1:
            android.graphics.drawable.GradientDrawable r14 = new android.graphics.drawable.GradientDrawable
            android.graphics.drawable.GradientDrawable$Orientation r2 = android.graphics.drawable.GradientDrawable.Orientation.BOTTOM_TOP
            int[] r8 = new int[r4]
            int r11 = androidx.core.graphics.ColorUtils.blendARGB(r13, r12, r11)
            r8[r10] = r11
            int r3 = androidx.core.graphics.ColorUtils.blendARGB(r13, r12, r3)
            r8[r9] = r3
            r14.<init>(r2, r8)
            org.telegram.messenger.ImageReceiver r2 = r0.imageReceiver
            r18 = 0
            r20 = 0
            java.lang.String r16 = "50_50_b"
            r3 = r14
            r14 = r2
            r17 = r3
            r19 = r1
            r14.setImage(r15, r16, r17, r18, r19, r20)
            goto L_0x032c
        L_0x02e8:
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r1 = -r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r2.getChat(r1)
            org.telegram.messenger.ImageLocation r14 = org.telegram.messenger.ImageLocation.getForChat(r1, r9)
            if (r1 == 0) goto L_0x0304
            int r2 = r1.id
            int r2 = org.telegram.ui.Components.AvatarDrawable.getColorForId(r2)
            goto L_0x0308
        L_0x0304:
            int r2 = androidx.core.graphics.ColorUtils.blendARGB(r12, r7, r11)
        L_0x0308:
            android.graphics.drawable.GradientDrawable r8 = new android.graphics.drawable.GradientDrawable
            android.graphics.drawable.GradientDrawable$Orientation r13 = android.graphics.drawable.GradientDrawable.Orientation.BOTTOM_TOP
            int[] r15 = new int[r4]
            int r11 = androidx.core.graphics.ColorUtils.blendARGB(r2, r12, r11)
            r15[r10] = r11
            int r2 = androidx.core.graphics.ColorUtils.blendARGB(r2, r12, r3)
            r15[r9] = r2
            r8.<init>(r13, r15)
            org.telegram.messenger.ImageReceiver r13 = r0.imageReceiver
            r17 = 0
            r19 = 0
            java.lang.String r15 = "50_50_b"
            r16 = r8
            r18 = r1
            r13.setImage(r14, r15, r16, r17, r18, r19)
        L_0x032c:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r1.participant
            boolean r1 = r1.self
            if (r1 == 0) goto L_0x034a
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x0372
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            boolean r3 = r3.presentation
            r1.setLocalSink(r2, r3)
            goto L_0x0372
        L_0x034a:
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x0372
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r2.participant
            boolean r2 = r2.presentation
            org.telegram.ui.Components.voip.VoIPTextureView r8 = r0.textureView
            org.webrtc.TextureViewRenderer r8 = r8.renderer
            r1.addRemoteSink(r3, r2, r8, r5)
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r2.participant
            boolean r2 = r2.presentation
            org.telegram.ui.Components.voip.VoIPTextureView r8 = r0.textureView
            org.webrtc.TextureViewRenderer r8 = r8.renderer
            r1.addRemoteSink(r3, r2, r8, r5)
        L_0x0372:
            android.widget.ImageView r1 = r0.screencastIcon
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            boolean r2 = r2.presentation
            if (r2 == 0) goto L_0x037c
            r2 = 0
            goto L_0x037e
        L_0x037c:
            r2 = 8
        L_0x037e:
            r1.setVisibility(r2)
            r1 = 0
            r2 = 1
            goto L_0x0387
        L_0x0384:
            r1 = r22
            r2 = 0
        L_0x0387:
            boolean r3 = r0.attached
            if (r3 == 0) goto L_0x0678
            boolean r3 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r3 == 0) goto L_0x039f
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r3 = r0.parentContainer
            boolean r3 = r3.inFullscreenMode
            if (r3 == 0) goto L_0x039d
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r3 = r0.secondaryView
            if (r3 != 0) goto L_0x039f
            org.telegram.ui.Components.voip.GroupCallGridCell r3 = r0.primaryView
            if (r3 != 0) goto L_0x039f
        L_0x039d:
            r3 = 1
            goto L_0x03a0
        L_0x039f:
            r3 = 0
        L_0x03a0:
            boolean r8 = r0.showingInFullscreen
            if (r8 == 0) goto L_0x03ab
        L_0x03a4:
            r3 = -1
        L_0x03a5:
            r8 = 1065353216(0x3var_, float:1.0)
            r11 = 0
            r12 = 0
            goto L_0x040a
        L_0x03ab:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r8 = r0.secondaryView
            if (r8 == 0) goto L_0x03bb
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r0.primaryView
            if (r11 != 0) goto L_0x03bb
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r11 = r0.parentContainer
            boolean r11 = r11.inFullscreenMode
            if (r11 != 0) goto L_0x03bb
        L_0x03b9:
            r3 = 0
            goto L_0x03a5
        L_0x03bb:
            boolean r11 = r0.showingAsScrimView
            if (r11 == 0) goto L_0x03c0
            goto L_0x03a4
        L_0x03c0:
            r11 = 1117782016(0x42a00000, float:80.0)
            if (r8 == 0) goto L_0x03cd
            org.telegram.ui.Components.voip.GroupCallGridCell r12 = r0.primaryView
            if (r12 != 0) goto L_0x03cd
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r11)
            goto L_0x03a5
        L_0x03cd:
            org.telegram.ui.Components.voip.GroupCallGridCell r12 = r0.tabletGridView
            r13 = 1110966272(0x42380000, float:46.0)
            if (r12 == 0) goto L_0x03ea
            if (r3 == 0) goto L_0x03ea
            if (r12 == 0) goto L_0x03e5
            int r3 = r12.spanCount
            float r3 = (float) r3
            org.telegram.ui.GroupCallTabletGridAdapter r8 = r12.gridAdapter
            int r8 = r8.getItemCount()
            r11 = r8
            r12 = 1
            r8 = r3
            r3 = -1
            goto L_0x040a
        L_0x03e5:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            goto L_0x03a5
        L_0x03ea:
            org.telegram.ui.Components.voip.GroupCallGridCell r3 = r0.primaryView
            if (r3 == 0) goto L_0x03f0
            if (r8 == 0) goto L_0x03f4
        L_0x03f0:
            boolean r8 = r0.isFullscreenMode
            if (r8 != 0) goto L_0x0403
        L_0x03f4:
            if (r3 == 0) goto L_0x03fe
            int r3 = r3.spanCount
            float r3 = (float) r3
            r8 = r3
            r3 = -1
            r11 = 0
            r12 = 1
            goto L_0x040a
        L_0x03fe:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            goto L_0x03a5
        L_0x0403:
            if (r3 == 0) goto L_0x03b9
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r11)
            goto L_0x03a5
        L_0x040a:
            android.view.ViewGroup$LayoutParams r13 = r21.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r13 = (android.view.ViewGroup.MarginLayoutParams) r13
            if (r3 == 0) goto L_0x0455
            int r14 = r13.height
            if (r14 != r3) goto L_0x0428
            if (r2 != 0) goto L_0x0428
            boolean r2 = r0.useSpanSize
            if (r2 != r12) goto L_0x0428
            if (r12 == 0) goto L_0x0424
            float r2 = r0.spanCount
            int r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r2 != 0) goto L_0x0428
        L_0x0424:
            int r2 = r0.gridItemsCount
            if (r2 == r11) goto L_0x0455
        L_0x0428:
            r13.height = r3
            if (r12 == 0) goto L_0x042d
            goto L_0x042e
        L_0x042d:
            r7 = r3
        L_0x042e:
            r13.width = r7
            r0.useSpanSize = r12
            r0.spanCount = r8
            r0.checkScale = r9
            if (r1 == 0) goto L_0x0440
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            r2.animateToLayout()
            r0.updateNextLayoutAnimated = r9
            goto L_0x0445
        L_0x0440:
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            r2.requestLayout()
        L_0x0445:
            org.telegram.ui.Components.voip.-$$Lambda$qlWeM_Ffarc-WFXvdj67MUDb5-A r2 = new org.telegram.ui.Components.voip.-$$Lambda$qlWeM_Ffarc-WFXvdj67MUDb5-A
            r2.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r2 = r0.parentContainer
            r2.requestLayout()
            r21.invalidate()
        L_0x0455:
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r2.participant
            boolean r3 = r3.self
            if (r3 == 0) goto L_0x047e
            boolean r2 = r2.presentation
            if (r2 != 0) goto L_0x047e
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r2 == 0) goto L_0x047e
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r3 = r3.isFrontFaceCamera()
            r2.setMirror(r3)
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            r2.setRotateTextureWitchScreen(r9)
            goto L_0x048c
        L_0x047e:
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            r2.setMirror(r10)
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            r2.setRotateTextureWitchScreen(r10)
        L_0x048c:
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            boolean r2 = r2.self
            if (r2 == 0) goto L_0x049e
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            r3 = 720(0x2d0, float:1.009E-42)
            r2.setMaxTextureSize(r3)
            goto L_0x04a5
        L_0x049e:
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            r2.setMaxTextureSize(r10)
        L_0x04a5:
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r2.participant
            boolean r2 = r2.presentation
            org.telegram.messenger.ChatObject$Call r7 = r0.call
            boolean r2 = org.telegram.ui.GroupCallActivity.videoIsActive(r3, r2, r7)
            if (r2 != 0) goto L_0x0547
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            org.telegram.messenger.ImageReceiver r2 = r2.avatarImageReceiver
            int r3 = r0.currentAccount
            r2.setCurrentAccount(r3)
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer
            int r2 = org.telegram.messenger.MessageObject.getPeerId(r2)
            if (r2 <= 0) goto L_0x04ea
            int r3 = r0.currentAccount
            org.telegram.messenger.AccountInstance r3 = org.telegram.messenger.AccountInstance.getInstance(r3)
            org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r2 = r3.getUser(r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r3 = r0.noVideoStubLayout
            org.telegram.ui.Components.AvatarDrawable r3 = r3.avatarDrawable
            r3.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForUser(r2, r10)
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForUser(r2, r9)
            goto L_0x050c
        L_0x04ea:
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.AccountInstance r3 = org.telegram.messenger.AccountInstance.getInstance(r3)
            org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
            int r2 = -r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r3.getChat(r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r3 = r0.noVideoStubLayout
            org.telegram.ui.Components.AvatarDrawable r3 = r3.avatarDrawable
            r3.setInfo((org.telegram.tgnet.TLRPC$Chat) r2)
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForChat(r2, r10)
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForChat(r2, r9)
        L_0x050c:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r8 = r0.noVideoStubLayout
            org.telegram.ui.Components.AvatarDrawable r8 = r8.avatarDrawable
            if (r7 == 0) goto L_0x0522
            org.telegram.messenger.ImageLoader r11 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r7 = r7.location
            java.lang.String r12 = "50_50"
            android.graphics.drawable.BitmapDrawable r7 = r11.getImageFromMemory(r7, r5, r12)
            if (r7 == 0) goto L_0x0522
            r14 = r7
            goto L_0x0523
        L_0x0522:
            r14 = r8
        L_0x0523:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r7 = r0.noVideoStubLayout
            org.telegram.messenger.ImageReceiver r11 = r7.avatarImageReceiver
            r13 = 0
            r15 = 0
            r17 = 0
            r12 = r3
            r16 = r2
            r11.setImage(r12, r13, r14, r15, r16, r17)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r7 = r0.noVideoStubLayout
            org.telegram.messenger.ImageReceiver r11 = r7.backgroundImageReceiver
            android.graphics.drawable.ColorDrawable r14 = new android.graphics.drawable.ColorDrawable
            java.lang.String r7 = "voipgroup_listViewBackground"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r14.<init>(r7)
            java.lang.String r13 = "50_50_b"
            r11.setImage(r12, r13, r14, r15, r16, r17)
            r2 = 0
            goto L_0x0548
        L_0x0547:
            r2 = 1
        L_0x0548:
            if (r1 == 0) goto L_0x0556
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r3 = r0.secondaryView
            if (r3 == 0) goto L_0x0556
            boolean r3 = r0.showingInFullscreen
            if (r3 != 0) goto L_0x0556
            if (r2 != 0) goto L_0x0556
            r3 = 1
            goto L_0x0557
        L_0x0556:
            r3 = 0
        L_0x0557:
            boolean r7 = r0.hasVideo
            if (r2 == r7) goto L_0x0610
            if (r3 != 0) goto L_0x0610
            r0.hasVideo = r2
            android.animation.ValueAnimator r2 = r0.noVideoStubAnimator
            if (r2 == 0) goto L_0x056b
            r2.removeAllListeners()
            android.animation.ValueAnimator r2 = r0.noVideoStubAnimator
            r2.cancel()
        L_0x056b:
            if (r1 == 0) goto L_0x05b1
            boolean r2 = r0.hasVideo
            if (r2 != 0) goto L_0x0583
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            int r2 = r2.getVisibility()
            if (r2 == 0) goto L_0x0583
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            r2.setVisibility(r10)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            r2.setAlpha(r6)
        L_0x0583:
            float[] r2 = new float[r4]
            float r3 = r0.progressToNoVideoStub
            r2[r10] = r3
            boolean r3 = r0.hasVideo
            if (r3 == 0) goto L_0x058f
            r3 = 0
            goto L_0x0591
        L_0x058f:
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x0591:
            r2[r9] = r3
            android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r2)
            r0.noVideoStubAnimator = r2
            org.telegram.ui.Components.voip.-$$Lambda$GroupCallMiniTextureView$CtHItnslcnEQ5eIKwOvfCm_XweA r3 = new org.telegram.ui.Components.voip.-$$Lambda$GroupCallMiniTextureView$CtHItnslcnEQ5eIKwOvfCm_XweA
            r3.<init>()
            r2.addUpdateListener(r3)
            android.animation.ValueAnimator r2 = r0.noVideoStubAnimator
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$6 r3 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$6
            r3.<init>()
            r2.addListener(r3)
            android.animation.ValueAnimator r2 = r0.noVideoStubAnimator
            r2.start()
            goto L_0x05d2
        L_0x05b1:
            boolean r2 = r0.hasVideo
            if (r2 == 0) goto L_0x05b7
            r3 = 0
            goto L_0x05b9
        L_0x05b7:
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x05b9:
            r0.progressToNoVideoStub = r3
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r3 = r0.noVideoStubLayout
            if (r2 == 0) goto L_0x05c2
            r2 = 8
            goto L_0x05c3
        L_0x05c2:
            r2 = 0
        L_0x05c3:
            r3.setVisibility(r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            float r3 = r0.progressToNoVideoStub
            r2.setAlpha(r3)
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            r2.invalidate()
        L_0x05d2:
            boolean r2 = r0.hasVideo
            if (r2 == 0) goto L_0x0610
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            r2.updateMuteButtonState(r10)
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            boolean r2 = r2.self
            if (r2 == 0) goto L_0x05f9
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r2 == 0) goto L_0x0610
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.ui.Components.voip.VoIPTextureView r3 = r0.textureView
            org.webrtc.TextureViewRenderer r3 = r3.renderer
            org.telegram.messenger.ChatObject$VideoParticipant r4 = r0.participant
            boolean r4 = r4.presentation
            r2.setLocalSink(r3, r4)
            goto L_0x0610
        L_0x05f9:
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r2 == 0) goto L_0x0610
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r4 = r3.participant
            boolean r3 = r3.presentation
            org.telegram.ui.Components.voip.VoIPTextureView r7 = r0.textureView
            org.webrtc.TextureViewRenderer r7 = r7.renderer
            r2.addRemoteSink(r4, r3, r7, r5)
        L_0x0610:
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            boolean r2 = r2.self
            if (r2 == 0) goto L_0x062d
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r2 == 0) goto L_0x062d
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.ui.Components.voip.VoIPTextureView r3 = r0.textureView
            org.webrtc.TextureViewRenderer r3 = r3.renderer
            org.telegram.messenger.ChatObject$VideoParticipant r4 = r0.participant
            boolean r4 = r4.presentation
            r2.setLocalSink(r3, r4)
        L_0x062d:
            org.telegram.ui.Components.voip.GroupCallStatusIcon r2 = r0.statusIcon
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            r2.setParticipant(r3, r1)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r1 = r0.noVideoStubLayout
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0643
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r1 = r0.noVideoStubLayout
            r1.updateMuteButtonState(r9)
        L_0x0643:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r1.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r1 = r1.video
            if (r1 == 0) goto L_0x0650
            boolean r1 = r1.paused
            if (r1 == 0) goto L_0x0650
            goto L_0x0651
        L_0x0650:
            r9 = 0
        L_0x0651:
            boolean r1 = r0.videoIsPaused
            if (r1 == r9) goto L_0x0678
            r0.videoIsPaused = r9
            org.telegram.ui.Components.voip.VoIPTextureView r1 = r0.textureView
            org.webrtc.TextureViewRenderer r1 = r1.renderer
            android.view.ViewPropertyAnimator r1 = r1.animate()
            boolean r2 = r0.videoIsPaused
            if (r2 == 0) goto L_0x0664
            goto L_0x0666
        L_0x0664:
            r6 = 1065353216(0x3var_, float:1.0)
        L_0x0666:
            android.view.ViewPropertyAnimator r1 = r1.alpha(r6)
            r2 = 250(0xfa, double:1.235E-321)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r2)
            r1.start()
            org.telegram.ui.Components.voip.VoIPTextureView r1 = r0.textureView
            r1.invalidate()
        L_0x0678:
            r21.updateInfo()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallMiniTextureView.updateAttachState(boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateAttachState$0 */
    public /* synthetic */ void lambda$updateAttachState$0$GroupCallMiniTextureView(View view) {
        this.parentContainer.removeView(view);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateAttachState$1 */
    public /* synthetic */ void lambda$updateAttachState$1$GroupCallMiniTextureView(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.progressToNoVideoStub = floatValue;
        this.noVideoStubLayout.setAlpha(floatValue);
        this.textureView.invalidate();
    }

    public void updateInfo() {
        if (this.attached) {
            String str = null;
            int peerId = MessageObject.getPeerId(this.participant.participant.peer);
            if (peerId > 0) {
                str = UserObject.getUserName(AccountInstance.getInstance(this.currentAccount).getMessagesController().getUser(Integer.valueOf(peerId)));
            } else {
                TLRPC$Chat chat = AccountInstance.getInstance(this.currentAccount).getMessagesController().getChat(Integer.valueOf(-peerId));
                if (chat != null) {
                    str = chat.title;
                }
            }
            this.nameView.setText(str);
        }
    }

    public boolean hasImage() {
        return this.textureView.stubVisibleProgress == 1.0f;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00c2, code lost:
        if (r5 != false) goto L_0x00c4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00c9, code lost:
        if (r1 != null) goto L_0x00c4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updatePosition(android.view.ViewGroup r7, android.view.ViewGroup r8, org.telegram.ui.Components.RecyclerListView r9, org.telegram.ui.Components.voip.GroupCallRenderersContainer r10) {
        /*
            r6 = this;
            boolean r0 = r6.showingAsScrimView
            if (r0 != 0) goto L_0x01b8
            boolean r0 = r6.animateToScrimView
            if (r0 != 0) goto L_0x01b8
            boolean r0 = r6.forceDetached
            if (r0 == 0) goto L_0x000e
            goto L_0x01b8
        L_0x000e:
            float r0 = r10.progressToFullscreenMode
            boolean r1 = r6.animateToFullscreen
            r2 = 1090519040(0x41000000, float:8.0)
            r3 = 1073741824(0x40000000, float:2.0)
            r4 = 1065353216(0x3var_, float:1.0)
            if (r1 != 0) goto L_0x0137
            boolean r1 = r6.showingInFullscreen
            if (r1 == 0) goto L_0x0020
            goto L_0x0137
        L_0x0020:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r1 = r6.secondaryView
            if (r1 == 0) goto L_0x00a4
            boolean r7 = r1.isRemoving(r9)
            if (r7 == 0) goto L_0x0034
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r7 = r6.secondaryView
            float r7 = r7.getAlpha()
            r6.setAlpha(r7)
            goto L_0x005b
        L_0x0034:
            org.telegram.ui.Components.voip.GroupCallGridCell r7 = r6.primaryView
            if (r7 != 0) goto L_0x004b
            boolean r7 = r6.attached
            if (r7 == 0) goto L_0x0043
            boolean r7 = r6.animateEnter
            if (r7 != 0) goto L_0x0043
            r6.setAlpha(r0)
        L_0x0043:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r7 = r6.secondaryView
            r7.setAlpha(r0)
            r0 = 1065353216(0x3var_, float:1.0)
            goto L_0x005b
        L_0x004b:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r7 = r6.secondaryView
            r7.setAlpha(r4)
            boolean r7 = r6.attached
            if (r7 == 0) goto L_0x005b
            boolean r7 = r6.animateEnter
            if (r7 != 0) goto L_0x005b
            r6.setAlpha(r4)
        L_0x005b:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r7 = r6.secondaryView
            float r7 = r7.getX()
            float r8 = r9.getX()
            float r7 = r7 + r8
            int r8 = r6.getLeft()
            float r8 = (float) r8
            float r7 = r7 - r8
            r6.setTranslationX(r7)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r7 = (float) r7
            float r4 = r4 - r0
            float r7 = r7 * r4
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r8 = r6.secondaryView
            float r8 = r8.getY()
            float r7 = r7 + r8
            float r8 = r9.getY()
            float r7 = r7 + r8
            int r8 = r6.getTop()
            float r8 = (float) r8
            float r7 = r7 - r8
            r6.setTranslationY(r7)
            org.telegram.ui.Components.voip.VoIPTextureView r7 = r6.textureView
            r8 = 1095761920(0x41500000, float:13.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            float r8 = r8 * r0
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r9 = (float) r9
            float r9 = r9 * r4
            float r8 = r8 + r9
            r7.setRoundCorners(r8)
            goto L_0x01b8
        L_0x00a4:
            org.telegram.ui.Components.voip.GroupCallGridCell r9 = r6.primaryView
            if (r9 != 0) goto L_0x00ac
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r6.tabletGridView
            if (r1 == 0) goto L_0x01b8
        L_0x00ac:
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r6.tabletGridView
            if (r1 == 0) goto L_0x00c6
            if (r9 == 0) goto L_0x00c6
            boolean r5 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r5 == 0) goto L_0x00be
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r5 = r6.parentContainer
            boolean r5 = r5.inFullscreenMode
            if (r5 != 0) goto L_0x00be
            r5 = 1
            goto L_0x00bf
        L_0x00be:
            r5 = 0
        L_0x00bf:
            if (r5 == 0) goto L_0x00c2
            r9 = r1
        L_0x00c2:
            if (r5 == 0) goto L_0x00cc
        L_0x00c4:
            r7 = r8
            goto L_0x00cc
        L_0x00c6:
            if (r1 == 0) goto L_0x00c9
            r9 = r1
        L_0x00c9:
            if (r1 == 0) goto L_0x00cc
            goto L_0x00c4
        L_0x00cc:
            float r8 = r9.getX()
            float r1 = r7.getX()
            float r8 = r8 + r1
            int r1 = r6.getLeft()
            float r1 = (float) r1
            float r8 = r8 - r1
            int r1 = r10.getLeft()
            float r1 = (float) r1
            float r8 = r8 - r1
            r6.setTranslationX(r8)
            float r8 = r9.getY()
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r1 = (float) r1
            float r8 = r8 + r1
            float r7 = r7.getY()
            float r8 = r8 + r7
            int r7 = r6.getTop()
            float r7 = (float) r7
            float r8 = r8 - r7
            int r7 = r10.getTop()
            float r7 = (float) r7
            float r8 = r8 - r7
            r6.setTranslationY(r8)
            org.telegram.ui.Components.voip.VoIPTextureView r7 = r6.textureView
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r8 = (float) r8
            r7.setRoundCorners(r8)
            boolean r7 = r6.attached
            if (r7 == 0) goto L_0x01b8
            boolean r7 = r6.animateEnter
            if (r7 != 0) goto L_0x01b8
            boolean r7 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r7 != 0) goto L_0x0124
            float r4 = r4 - r0
            float r7 = r9.getAlpha()
            float r4 = r4 * r7
            r6.setAlpha(r4)
            goto L_0x01b8
        L_0x0124:
            org.telegram.ui.Components.voip.GroupCallGridCell r7 = r6.primaryView
            if (r7 == 0) goto L_0x01b8
            org.telegram.ui.Components.voip.GroupCallGridCell r7 = r6.tabletGridView
            if (r7 != 0) goto L_0x01b8
            float r7 = r9.getAlpha()
            float r0 = r0 * r7
            r6.setAlpha(r0)
            goto L_0x01b8
        L_0x0137:
            org.telegram.ui.Components.voip.GroupCallGridCell r9 = r6.primaryView
            r1 = 0
            if (r9 != 0) goto L_0x0148
            org.telegram.ui.Components.voip.GroupCallGridCell r5 = r6.tabletGridView
            if (r5 == 0) goto L_0x0141
            goto L_0x0148
        L_0x0141:
            r6.setTranslationX(r1)
            r6.setTranslationY(r1)
            goto L_0x019a
        L_0x0148:
            org.telegram.ui.Components.voip.GroupCallGridCell r5 = r6.tabletGridView
            if (r5 == 0) goto L_0x014d
            r9 = r5
        L_0x014d:
            if (r5 == 0) goto L_0x0150
            r7 = r8
        L_0x0150:
            float r8 = r9.getX()
            float r5 = r7.getX()
            float r8 = r8 + r5
            int r5 = r6.getLeft()
            float r5 = (float) r5
            float r8 = r8 - r5
            int r5 = r10.getLeft()
            float r5 = (float) r5
            float r8 = r8 - r5
            float r9 = r9.getY()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r9 = r9 + r3
            float r7 = r7.getY()
            float r9 = r9 + r7
            int r7 = r6.getTop()
            float r7 = (float) r7
            float r9 = r9 - r7
            int r7 = r10.getTop()
            float r7 = (float) r7
            float r9 = r9 - r7
            float r7 = r4 - r0
            float r8 = r8 * r7
            float r1 = r1 * r0
            float r8 = r8 + r1
            r6.setTranslationX(r8)
            float r9 = r9 * r7
            float r9 = r9 + r1
            r6.setTranslationY(r9)
            org.telegram.ui.Components.voip.VoIPTextureView r7 = r6.textureView
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r8 = (float) r8
            r7.setRoundCorners(r8)
        L_0x019a:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r7 = r6.secondaryView
            if (r7 == 0) goto L_0x01a1
            r7.setAlpha(r0)
        L_0x01a1:
            boolean r7 = r6.showingInFullscreen
            if (r7 != 0) goto L_0x01b1
            org.telegram.ui.Components.voip.GroupCallGridCell r7 = r6.primaryView
            if (r7 != 0) goto L_0x01b1
            org.telegram.ui.Components.voip.GroupCallGridCell r7 = r6.tabletGridView
            if (r7 != 0) goto L_0x01b1
            r6.setAlpha(r0)
            goto L_0x01b8
        L_0x01b1:
            boolean r7 = r6.animateEnter
            if (r7 != 0) goto L_0x01b8
            r6.setAlpha(r4)
        L_0x01b8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallMiniTextureView.updatePosition(android.view.ViewGroup, android.view.ViewGroup, org.telegram.ui.Components.RecyclerListView, org.telegram.ui.Components.voip.GroupCallRenderersContainer):void");
    }

    public boolean isAttached() {
        return this.attached;
    }

    public void release() {
        this.textureView.renderer.release();
        GroupCallStatusIcon groupCallStatusIcon = this.statusIcon;
        if (groupCallStatusIcon != null) {
            this.activity.statusIconPool.add(groupCallStatusIcon);
            this.statusIcon.setCallback((GroupCallStatusIcon.Callback) null);
            this.statusIcon.setImageView((RLottieImageView) null);
        }
        this.statusIcon = null;
    }

    public boolean isFullyVisible() {
        if (this.showingInFullscreen || this.animateToFullscreen || !this.attached || !this.textureView.renderer.isFirstFrameRendered() || getAlpha() != 1.0f) {
            return false;
        }
        return true;
    }

    public void invalidate() {
        super.invalidate();
        if (!this.invalidateFromChild) {
            this.textureView.invalidate();
        }
        GroupCallGridCell groupCallGridCell = this.primaryView;
        if (groupCallGridCell != null) {
            groupCallGridCell.invalidate();
            if (this.activity.getScrimView() == this.primaryView) {
                this.activity.getContainerView().invalidate();
            }
        }
        GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = this.secondaryView;
        if (groupCallUserCell != null) {
            groupCallUserCell.invalidate();
            if (this.secondaryView.getParent() != null) {
                ((View) this.secondaryView.getParent()).invalidate();
            }
        }
        if (getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    public void forceDetach(boolean z) {
        this.forceDetached = true;
        this.attached = false;
        this.parentContainer.detach(this);
        if (z) {
            if (this.participant.participant.self) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().setLocalSink((VideoSink) null, this.participant.presentation);
                }
            } else if (VoIPService.getSharedInstance() != null) {
                VoIPService sharedInstance = VoIPService.getSharedInstance();
                ChatObject.VideoParticipant videoParticipant = this.participant;
                sharedInstance.removeRemoteSink(videoParticipant.participant, videoParticipant.presentation);
            }
        }
        saveThumb();
        ValueAnimator valueAnimator = this.noVideoStubAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.noVideoStubAnimator.cancel();
        }
        this.textureView.renderer.release();
    }

    public void saveThumb() {
        if (this.participant != null && this.textureView.renderer.getMeasuredHeight() != 0 && this.textureView.renderer.getMeasuredWidth() != 0) {
            getRenderBufferBitmap(new GlGenericDrawer.TextureCallback() {
                public final void run(Bitmap bitmap, int i) {
                    GroupCallMiniTextureView.this.lambda$saveThumb$3$GroupCallMiniTextureView(bitmap, i);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$saveThumb$3 */
    public /* synthetic */ void lambda$saveThumb$3$GroupCallMiniTextureView(Bitmap bitmap, int i) {
        if (bitmap != null && bitmap.getPixel(0, 0) != 0) {
            Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(bitmap.getWidth(), bitmap.getHeight()) / 180));
            AndroidUtilities.runOnUIThread(new Runnable(bitmap) {
                public final /* synthetic */ Bitmap f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    GroupCallMiniTextureView.this.lambda$saveThumb$2$GroupCallMiniTextureView(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$saveThumb$2 */
    public /* synthetic */ void lambda$saveThumb$2$GroupCallMiniTextureView(Bitmap bitmap) {
        HashMap<String, Bitmap> hashMap = this.call.thumbs;
        ChatObject.VideoParticipant videoParticipant = this.participant;
        boolean z = videoParticipant.presentation;
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = videoParticipant.participant;
        hashMap.put(z ? tLRPC$TL_groupCallParticipant.presentationEndpoint : tLRPC$TL_groupCallParticipant.videoEndpoint, bitmap);
    }

    public void setViews(GroupCallGridCell groupCallGridCell, GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell, GroupCallGridCell groupCallGridCell2) {
        this.primaryView = groupCallGridCell;
        this.secondaryView = groupCallUserCell;
        this.tabletGridView = groupCallGridCell2;
    }

    public void setAmplitude(double d) {
        this.statusIcon.setAmplitude(d);
        this.noVideoStubLayout.setAmplitude(d);
    }

    public void setZoom(boolean z, float f, float f2, float f3, float f4, float f5) {
        if (this.pinchScale != f || this.pinchCenterX != f2 || this.pinchCenterY != f3 || this.pinchTranslationX != f4 || this.pinchTranslationY != f5) {
            this.inPinchToZoom = z;
            this.pinchScale = f;
            this.pinchCenterX = f2;
            this.pinchCenterY = f3;
            this.pinchTranslationX = f4;
            this.pinchTranslationY = f5;
            this.textureView.invalidate();
        }
    }

    public void setSwipeToBack(boolean z, float f) {
        if (this.swipeToBack != z || this.swipeToBackDy != f) {
            this.swipeToBack = z;
            this.swipeToBackDy = f;
            this.textureView.invalidate();
            invalidate();
        }
    }

    public void runOnFrameRendered(Runnable runnable) {
        if (this.textureView.renderer.isFirstFrameRendered()) {
            runnable.run();
            return;
        }
        AndroidUtilities.runOnUIThread(runnable, 250);
        this.onFirstFrameRunnables.add(runnable);
    }

    public void onStatusChanged() {
        invalidate();
        updateIconColor(true);
        if (this.noVideoStubLayout.getVisibility() == 0) {
            this.noVideoStubLayout.updateMuteButtonState(true);
        }
    }

    private void updateIconColor(boolean z) {
        final int i;
        GroupCallStatusIcon groupCallStatusIcon = this.statusIcon;
        if (groupCallStatusIcon != null) {
            if (groupCallStatusIcon.isMutedByMe()) {
                i = Theme.getColor("voipgroup_mutedByAdminIcon");
            } else {
                i = this.statusIcon.isSpeaking() ? Theme.getColor("voipgroup_speakingText") : -1;
            }
            if (this.animateToColor != i) {
                ValueAnimator valueAnimator = this.colorAnimator;
                if (valueAnimator != null) {
                    valueAnimator.removeAllListeners();
                    this.colorAnimator.cancel();
                }
                if (!z) {
                    RLottieImageView rLottieImageView = this.micIconView;
                    this.lastIconColor = i;
                    this.animateToColor = i;
                    rLottieImageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                    return;
                }
                final int i2 = this.lastIconColor;
                this.animateToColor = i;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.colorAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                        GroupCallMiniTextureView.this.lastIconColor = ColorUtils.blendARGB(i2, i, floatValue);
                        GroupCallMiniTextureView.this.micIconView.setColorFilter(new PorterDuffColorFilter(GroupCallMiniTextureView.this.lastIconColor, PorterDuff.Mode.MULTIPLY));
                    }
                });
                this.colorAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                        int i = i;
                        groupCallMiniTextureView.lastIconColor = i;
                        groupCallMiniTextureView.animateToColor = i;
                        groupCallMiniTextureView.micIconView.setColorFilter(new PorterDuffColorFilter(GroupCallMiniTextureView.this.lastIconColor, PorterDuff.Mode.MULTIPLY));
                    }
                });
                this.colorAnimator.start();
            }
        }
    }

    public void runDelayedAnimations() {
        for (int i = 0; i < this.onFirstFrameRunnables.size(); i++) {
            this.onFirstFrameRunnables.get(i).run();
        }
        this.onFirstFrameRunnables.clear();
    }

    public void updateSize(int i) {
        int measuredWidth = this.parentContainer.getMeasuredWidth();
        if ((this.collapseSize != i && i > 0) || (this.fullSize != measuredWidth && measuredWidth > 0)) {
            if (i != 0) {
                this.collapseSize = i;
            }
            if (measuredWidth != 0) {
                this.fullSize = measuredWidth;
            }
            this.nameView.setFullLayoutAdditionalWidth(measuredWidth - i, 0);
        }
    }

    private class NoVideoStubLayout extends View {
        float amplitude;
        float animateAmplitudeDiff;
        float animateToAmplitude;
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        public ImageReceiver avatarImageReceiver = new ImageReceiver();
        public ImageReceiver backgroundImageReceiver = new ImageReceiver();
        Paint backgroundPaint = new Paint(1);
        BlobDrawable bigWaveDrawable = new BlobDrawable(12);
        private GroupCallActivity.WeavingState currentState;
        float cx;
        float cy;
        int muteButtonState = -1;
        Paint paint = new Paint(1);
        private GroupCallActivity.WeavingState prevState;
        float speakingProgress;
        private GroupCallActivity.WeavingState[] states = new GroupCallActivity.WeavingState[3];
        float switchProgress = 1.0f;
        BlobDrawable tinyWaveDrawable = new BlobDrawable(9);
        float wavesEnter = 0.0f;

        public NoVideoStubLayout(Context context) {
            super(context);
            this.tinyWaveDrawable.minRadius = (float) AndroidUtilities.dp(76.0f);
            this.tinyWaveDrawable.maxRadius = (float) AndroidUtilities.dp(92.0f);
            this.tinyWaveDrawable.generateBlob();
            this.bigWaveDrawable.minRadius = (float) AndroidUtilities.dp(80.0f);
            this.bigWaveDrawable.maxRadius = (float) AndroidUtilities.dp(95.0f);
            this.bigWaveDrawable.generateBlob();
            this.paint.setColor(ColorUtils.blendARGB(Theme.getColor("voipgroup_listeningText"), Theme.getColor("voipgroup_speakingText"), this.speakingProgress));
            this.paint.setAlpha(102);
            this.backgroundPaint.setColor(ColorUtils.setAlphaComponent(-16777216, 127));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            float dp = (float) AndroidUtilities.dp(157.0f);
            this.cx = (float) (getMeasuredWidth() >> 1);
            this.cy = ((float) (getMeasuredHeight() >> 1)) + (GroupCallActivity.isLandscapeMode ? 0.0f : ((float) (-getMeasuredHeight())) * 0.12f);
            float f = dp / 2.0f;
            this.avatarImageReceiver.setRoundRadius((int) f);
            this.avatarImageReceiver.setImageCoords(this.cx - f, this.cy - f, dp, dp);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float f;
            GroupCallActivity.WeavingState weavingState;
            GroupCallActivity.WeavingState weavingState2;
            super.onDraw(canvas);
            RectF rectF = AndroidUtilities.rectTmp;
            float x = GroupCallMiniTextureView.this.textureView.getX();
            VoIPTextureView voIPTextureView = GroupCallMiniTextureView.this.textureView;
            float f2 = x + voIPTextureView.currentClipHorizontal;
            float y = voIPTextureView.getY();
            VoIPTextureView voIPTextureView2 = GroupCallMiniTextureView.this.textureView;
            float f3 = y + voIPTextureView2.currentClipVertical;
            float x2 = voIPTextureView2.getX() + ((float) GroupCallMiniTextureView.this.textureView.getMeasuredWidth());
            VoIPTextureView voIPTextureView3 = GroupCallMiniTextureView.this.textureView;
            rectF.set(f2, f3, x2 - voIPTextureView3.currentClipHorizontal, voIPTextureView3.getY() + ((float) GroupCallMiniTextureView.this.textureView.getMeasuredHeight()) + GroupCallMiniTextureView.this.textureView.currentClipVertical);
            this.backgroundImageReceiver.setImageCoords(rectF.left, rectF.top, rectF.width(), rectF.height());
            this.backgroundImageReceiver.setRoundRadius((int) GroupCallMiniTextureView.this.textureView.roundRadius);
            this.backgroundImageReceiver.draw(canvas);
            float f4 = GroupCallMiniTextureView.this.textureView.roundRadius;
            canvas.drawRoundRect(rectF, f4, f4, this.backgroundPaint);
            float f5 = this.animateToAmplitude;
            float f6 = this.amplitude;
            if (f5 != f6) {
                float f7 = this.animateAmplitudeDiff;
                float f8 = f6 + (16.0f * f7);
                this.amplitude = f8;
                if (f7 > 0.0f) {
                    if (f8 > f5) {
                        this.amplitude = f5;
                    }
                } else if (f8 < f5) {
                    this.amplitude = f5;
                }
            }
            float f9 = this.switchProgress;
            if (f9 != 1.0f) {
                if (this.prevState != null) {
                    this.switchProgress = f9 + 0.07272727f;
                }
                if (this.switchProgress >= 1.0f) {
                    this.switchProgress = 1.0f;
                    this.prevState = null;
                }
            }
            float var_ = (this.amplitude * 0.8f) + 1.0f;
            canvas.save();
            canvas.scale(var_, var_, this.cx, this.cy);
            GroupCallActivity.WeavingState weavingState3 = this.currentState;
            if (weavingState3 != null) {
                weavingState3.update((int) (this.cy - ((float) AndroidUtilities.dp(100.0f))), (int) (this.cx - ((float) AndroidUtilities.dp(100.0f))), AndroidUtilities.dp(200.0f), 16, this.amplitude);
            }
            this.bigWaveDrawable.update(this.amplitude, 1.0f);
            this.tinyWaveDrawable.update(this.amplitude, 1.0f);
            for (int i = 0; i < 2; i++) {
                if (i != 0 || (weavingState2 = this.prevState) == null) {
                    if (i == 1 && (weavingState = this.currentState) != null) {
                        this.paint.setShader(weavingState.shader);
                        f = this.switchProgress;
                    }
                } else {
                    this.paint.setShader(weavingState2.shader);
                    f = 1.0f - this.switchProgress;
                }
                this.paint.setAlpha((int) (f * 76.0f));
                this.bigWaveDrawable.draw(this.cx, this.cy, canvas, this.paint);
                this.tinyWaveDrawable.draw(this.cx, this.cy, canvas, this.paint);
            }
            canvas.restore();
            float var_ = (this.amplitude * 0.2f) + 1.0f;
            canvas.save();
            canvas.scale(var_, var_, this.cx, this.cy);
            this.avatarImageReceiver.draw(canvas);
            canvas.restore();
            invalidate();
        }

        /* access modifiers changed from: private */
        public void updateMuteButtonState(boolean z) {
            int i = (GroupCallMiniTextureView.this.statusIcon.isMutedByMe() || GroupCallMiniTextureView.this.statusIcon.isMutedByAdmin()) ? 2 : GroupCallMiniTextureView.this.statusIcon.isSpeaking() ? 1 : 0;
            if (i != this.muteButtonState) {
                this.muteButtonState = i;
                GroupCallActivity.WeavingState[] weavingStateArr = this.states;
                if (weavingStateArr[i] == null) {
                    weavingStateArr[i] = new GroupCallActivity.WeavingState(i);
                    int i2 = this.muteButtonState;
                    if (i2 == 2) {
                        this.states[i2].shader = new LinearGradient(0.0f, 400.0f, 400.0f, 0.0f, new int[]{Theme.getColor("voipgroup_mutedByAdminGradient"), Theme.getColor("voipgroup_mutedByAdminGradient3"), Theme.getColor("voipgroup_mutedByAdminGradient2")}, (float[]) null, Shader.TileMode.CLAMP);
                    } else if (i2 == 1) {
                        this.states[i2].shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor("voipgroup_muteButton"), Theme.getColor("voipgroup_muteButton3")}, (float[]) null, Shader.TileMode.CLAMP);
                    } else {
                        this.states[i2].shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor("voipgroup_unmuteButton2"), Theme.getColor("voipgroup_unmuteButton")}, (float[]) null, Shader.TileMode.CLAMP);
                    }
                }
                GroupCallActivity.WeavingState[] weavingStateArr2 = this.states;
                int i3 = this.muteButtonState;
                GroupCallActivity.WeavingState weavingState = weavingStateArr2[i3];
                GroupCallActivity.WeavingState weavingState2 = this.currentState;
                if (weavingState != weavingState2) {
                    this.prevState = weavingState2;
                    this.currentState = weavingStateArr2[i3];
                    if (weavingState2 == null || !z) {
                        this.switchProgress = 1.0f;
                        this.prevState = null;
                    } else {
                        this.switchProgress = 0.0f;
                    }
                }
                invalidate();
            }
        }

        public void setAmplitude(double d) {
            float f = ((float) d) / 80.0f;
            if (f > 1.0f) {
                f = 1.0f;
            } else if (f < 0.0f) {
                f = 0.0f;
            }
            this.animateToAmplitude = f;
            this.animateAmplitudeDiff = (f - this.amplitude) / 200.0f;
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.avatarImageReceiver.onAttachedToWindow();
            this.backgroundImageReceiver.onAttachedToWindow();
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.avatarImageReceiver.onDetachedFromWindow();
            this.backgroundImageReceiver.onDetachedFromWindow();
        }
    }

    public String getName() {
        int peerId = MessageObject.getPeerId(this.participant.participant.peer);
        if (peerId > 0) {
            return UserObject.getUserName(AccountInstance.getInstance(UserConfig.selectedAccount).getMessagesController().getUser(Integer.valueOf(peerId)));
        }
        return AccountInstance.getInstance(UserConfig.selectedAccount).getMessagesController().getChat(Integer.valueOf(-peerId)).title;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.imageReceiver.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageReceiver.onAttachedToWindow();
    }
}
