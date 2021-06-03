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
import android.view.View;
import android.view.ViewGroup;
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
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.GroupCallFullscreenAdapter;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
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
    GroupCallRenderersContainer parentContainer;
    public ChatObject.VideoParticipant participant;
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
    public final VoIPTextureView textureView;
    Bitmap thumb;
    Paint thumbPaint;
    private boolean updateNextLayoutAnimated;
    boolean useSpanSize;

    static /* synthetic */ float access$016(GroupCallMiniTextureView groupCallMiniTextureView, float f) {
        float f2 = groupCallMiniTextureView.progressToBackground + f;
        groupCallMiniTextureView.progressToBackground = f2;
        return f2;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GroupCallMiniTextureView(GroupCallRenderersContainer groupCallRenderersContainer, ArrayList<GroupCallMiniTextureView> arrayList, ChatObject.Call call2, GroupCallActivity groupCallActivity) {
        super(groupCallRenderersContainer.getContext());
        final ChatObject.Call call3 = call2;
        this.call = call3;
        this.currentAccount = groupCallActivity.getCurrentAccount();
        final GroupCallActivity groupCallActivity2 = groupCallActivity;
        final GroupCallRenderersContainer groupCallRenderersContainer2 = groupCallRenderersContainer;
        AnonymousClass2 r0 = new VoIPTextureView(groupCallRenderersContainer.getContext(), false, false, true, true) {
            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                if ((GroupCallMiniTextureView.this.showBackground && !this.renderer.isFirstFrameRendered()) || this.renderer.getAlpha() != 1.0f) {
                    if (GroupCallMiniTextureView.this.progressToBackground != 1.0f) {
                        GroupCallMiniTextureView.access$016(GroupCallMiniTextureView.this, 0.10666667f);
                        if (GroupCallMiniTextureView.this.progressToBackground > 1.0f) {
                            float unused = GroupCallMiniTextureView.this.progressToBackground = 1.0f;
                        } else {
                            invalidate();
                        }
                    }
                    GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                    if (groupCallMiniTextureView.thumb != null) {
                        canvas.save();
                        float f = this.currentThumbScale;
                        canvas.scale(f, f, ((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f);
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
        };
        this.textureView = r0;
        r0.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        this.parentContainer = groupCallRenderersContainer;
        this.attachedRenderers = arrayList;
        this.activity = groupCallActivity;
        r0.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
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
        r0.attachBackgroundRenderer();
        AndroidUtilities.runOnUIThread(this.showBackgroundRunnable, 250);
        setClipChildren(false);
        r0.renderer.setAlpha(0.0f);
        addView(r0);
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
    public void onMeasure(int i, int i2) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.infoContainer.getLayoutParams();
        int i3 = layoutParams.leftMargin;
        boolean z = this.lastLandscapeMode;
        boolean z2 = GroupCallActivity.isLandscapeMode;
        if (z != z2) {
            this.checkScale = true;
            this.lastLandscapeMode = z2;
        }
        int dp = AndroidUtilities.dp(8.0f);
        layoutParams.rightMargin = dp;
        layoutParams.leftMargin = dp;
        if (this.updateNextLayoutAnimated) {
            this.nameView.animate().scaleX(1.0f).scaleY(1.0f).start();
            this.micIconView.animate().scaleX(1.0f).scaleY(1.0f).start();
            int i4 = layoutParams.leftMargin;
            if (i4 != i3) {
                this.infoContainer.setTranslationX((float) (i3 - i4));
                this.infoContainer.animate().translationX(0.0f).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            }
        } else {
            this.nameView.animate().cancel();
            this.nameView.setScaleX(1.0f);
            this.nameView.setScaleY(1.0f);
            this.micIconView.animate().cancel();
            this.micIconView.setScaleX(1.0f);
            this.micIconView.setScaleY(1.0f);
            this.infoContainer.animate().cancel();
            this.infoContainer.setTranslationX(0.0f);
        }
        int i5 = 0;
        this.updateNextLayoutAnimated = false;
        if (this.showingInFullscreen) {
            updateSize(0);
            if (!GroupCallActivity.isLandscapeMode) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2) - AndroidUtilities.dp(92.0f), NUM));
            } else {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i) - AndroidUtilities.dp(92.0f), NUM), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), NUM));
            }
        } else if (this.showingAsScrimView) {
            int min = Math.min(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2)) - (AndroidUtilities.dp(14.0f) * 2);
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(min, NUM), View.MeasureSpec.makeMeasureSpec(min + getPaddingBottom(), NUM));
        } else if (this.useSpanSize) {
            int i6 = GroupCallActivity.isLandscapeMode ? 3 : 2;
            int size = View.MeasureSpec.getSize(i) - (AndroidUtilities.dp(14.0f) * 2);
            if (GroupCallActivity.isLandscapeMode) {
                i5 = -AndroidUtilities.dp(90.0f);
            }
            float f = (float) (size + i5);
            float f2 = (float) i6;
            float dp2 = ((this.spanCount / f2) * f) - ((float) AndroidUtilities.dp(2.0f));
            float f3 = f / f2;
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.infoContainer.getLayoutParams();
            float dp3 = this.screencastIcon.getVisibility() == 0 ? dp2 - ((float) AndroidUtilities.dp(28.0f)) : dp2;
            updateSize((int) dp3);
            layoutParams2.width = (int) (dp3 - ((float) (layoutParams2.leftMargin * 2)));
            super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) dp2, NUM), View.MeasureSpec.makeMeasureSpec((int) f3, NUM));
        } else {
            super.onMeasure(i, i2);
        }
        int measuredHeight = getMeasuredHeight() + (getMeasuredWidth() << 16);
        if (this.lastSize != measuredHeight) {
            this.lastSize = measuredHeight;
            LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, (float) AndroidUtilities.dp(120.0f), 0, ColorUtils.setAlphaComponent(-16777216, 120), Shader.TileMode.CLAMP);
            this.gradientShader = linearGradient;
            this.gradientPaint.setShader(linearGradient);
        }
        this.nameView.setPivotX(0.0f);
        SimpleTextView simpleTextView = this.nameView;
        simpleTextView.setPivotY(((float) simpleTextView.getMeasuredHeight()) / 2.0f);
    }

    public static GroupCallMiniTextureView getOrCreate(ArrayList<GroupCallMiniTextureView> arrayList, GroupCallRenderersContainer groupCallRenderersContainer, GroupCallGridCell groupCallGridCell, GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell, ChatObject.VideoParticipant videoParticipant, ChatObject.Call call2, GroupCallActivity groupCallActivity) {
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
        return groupCallMiniTextureView;
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
            updateAttachState(this.primaryView != null && z2);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v43, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v49, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v95, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v96, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0165, code lost:
        if (r1 != false) goto L_0x0167;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x035c  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x03b5  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x03c0  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x03c8  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x03ef  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x041c  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x0426  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x043b  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x04cf  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x04eb  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x04f5  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x0538  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x055c  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x05a7  */
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
            if (r1 != 0) goto L_0x0024
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r0.primaryView
            if (r1 != 0) goto L_0x0013
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r2 = r0.secondaryView
            if (r2 == 0) goto L_0x0024
        L_0x0013:
            if (r1 == 0) goto L_0x001c
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.getParticipant()
            r0.participant = r1
            goto L_0x0024
        L_0x001c:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r1 = r0.secondaryView
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.getVideoParticipant()
            r0.participant = r1
        L_0x0024:
            boolean r1 = r0.attached
            r3 = 1056964608(0x3var_, float:0.5)
            r4 = 2
            r5 = 0
            r6 = 0
            r7 = -1
            r8 = 1065353216(0x3var_, float:1.0)
            r9 = 1
            r10 = 0
            if (r1 == 0) goto L_0x00e6
            boolean r11 = r0.showingInFullscreen
            if (r11 != 0) goto L_0x00e6
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 != 0) goto L_0x003e
            r1 = 1
            goto L_0x003f
        L_0x003e:
            r1 = 0
        L_0x003f:
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r0.participant
            if (r11 == 0) goto L_0x0053
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r12 = r0.secondaryView
            if (r12 != 0) goto L_0x0054
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r11.participant
            boolean r11 = r11.presentation
            org.telegram.messenger.ChatObject$Call r13 = r0.call
            boolean r11 = org.telegram.ui.GroupCallActivity.videoIsActive(r12, r11, r13)
            if (r11 != 0) goto L_0x0054
        L_0x0053:
            r1 = 1
        L_0x0054:
            if (r1 != 0) goto L_0x0066
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r0.primaryView
            if (r11 != 0) goto L_0x0355
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r11 = r0.secondaryView
            if (r11 != 0) goto L_0x0355
            boolean r11 = r0.showingAsScrimView
            if (r11 != 0) goto L_0x0355
            boolean r11 = r0.animateToScrimView
            if (r11 != 0) goto L_0x0355
        L_0x0066:
            r0.attached = r10
            r21.saveThumb()
            org.telegram.ui.Components.voip.VoIPTextureView r11 = r0.textureView
            android.animation.ValueAnimator r11 = r11.currentAnimation
            if (r11 != 0) goto L_0x009b
            if (r1 == 0) goto L_0x009b
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r1 = r0.attachedRenderers
            r1.remove(r0)
            android.view.ViewPropertyAnimator r1 = r21.animate()
            android.view.ViewPropertyAnimator r1 = r1.scaleX(r3)
            android.view.ViewPropertyAnimator r1 = r1.scaleY(r3)
            android.view.ViewPropertyAnimator r1 = r1.alpha(r5)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$4 r3 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$4
            r3.<init>(r0)
            android.view.ViewPropertyAnimator r1 = r1.setListener(r3)
            r11 = 150(0x96, double:7.4E-322)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r11)
            r1.start()
            goto L_0x00a8
        L_0x009b:
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r1 = r0.parentContainer
            r1.removeView(r0)
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r1 = r0.attachedRenderers
            r1.remove(r0)
            r21.release()
        L_0x00a8:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r1.participant
            boolean r1 = r1.self
            if (r1 == 0) goto L_0x00c2
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x00d5
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            boolean r3 = r3.presentation
            r1.setLocalSink(r6, r3)
            goto L_0x00d5
        L_0x00c2:
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x00d5
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11 = r3.participant
            boolean r3 = r3.presentation
            r1.removeRemoteSink(r11, r3)
        L_0x00d5:
            r21.invalidate()
            android.animation.ValueAnimator r1 = r0.noVideoStubAnimator
            if (r1 == 0) goto L_0x0355
            r1.removeAllListeners()
            android.animation.ValueAnimator r1 = r0.noVideoStubAnimator
            r1.cancel()
            goto L_0x0355
        L_0x00e6:
            if (r1 != 0) goto L_0x0355
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 != 0) goto L_0x00ef
            return
        L_0x00ef:
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r0.primaryView
            if (r1 != 0) goto L_0x00fb
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r11 = r0.secondaryView
            if (r11 != 0) goto L_0x00fb
            boolean r11 = r0.showingInFullscreen
            if (r11 == 0) goto L_0x0355
        L_0x00fb:
            if (r1 == 0) goto L_0x0104
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.getParticipant()
            r0.participant = r1
            goto L_0x010e
        L_0x0104:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r1 = r0.secondaryView
            if (r1 == 0) goto L_0x010e
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.getVideoParticipant()
            r0.participant = r1
        L_0x010e:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11 = r1.participant
            boolean r12 = r11.self
            if (r12 == 0) goto L_0x0139
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x012c
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r0.participant
            boolean r11 = r11.presentation
            int r1 = r1.getVideoState(r11)
            if (r1 != r4) goto L_0x012c
            r1 = 1
            goto L_0x012d
        L_0x012c:
            r1 = 0
        L_0x012d:
            org.telegram.messenger.ChatObject$Call r11 = r0.call
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11 = r11.fullscreenParticipant
            if (r11 == 0) goto L_0x0141
            boolean r11 = r11.self
            if (r11 == 0) goto L_0x0141
            r1 = 0
            goto L_0x0141
        L_0x0139:
            boolean r1 = r1.presentation
            org.telegram.messenger.ChatObject$Call r12 = r0.call
            boolean r1 = org.telegram.ui.GroupCallActivity.videoIsActive(r11, r1, r12)
        L_0x0141:
            boolean r11 = r0.showingInFullscreen
            if (r11 != 0) goto L_0x0167
            org.telegram.messenger.voip.VoIPService r11 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r12 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r13 = r12.participant
            boolean r12 = r12.presentation
            boolean r11 = r11.isFullscreen(r13, r12)
            if (r11 != 0) goto L_0x0355
            org.telegram.messenger.voip.VoIPService r11 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r12 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r13 = r12.participant
            boolean r12 = r12.presentation
            boolean r11 = r11.isFullscreen(r13, r12)
            if (r11 != 0) goto L_0x0355
            if (r1 == 0) goto L_0x0355
        L_0x0167:
            boolean r1 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r1 == 0) goto L_0x0191
            r1 = 0
        L_0x016c:
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r11 = r0.attachedRenderers
            int r11 = r11.size()
            if (r1 >= r11) goto L_0x0191
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r11 = r0.attachedRenderers
            java.lang.Object r11 = r11.get(r1)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r11 = (org.telegram.ui.Components.voip.GroupCallMiniTextureView) r11
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r11.participant
            org.telegram.messenger.ChatObject$VideoParticipant r12 = r0.participant
            boolean r11 = r11.equals(r12)
            if (r11 != 0) goto L_0x0189
            int r1 = r1 + 1
            goto L_0x016c
        L_0x0189:
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            java.lang.String r2 = "try add two same renderers"
            r1.<init>(r2)
            throw r1
        L_0x0191:
            r0.attached = r9
            org.telegram.ui.GroupCallActivity r1 = r0.activity
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallStatusIcon> r1 = r1.statusIconPool
            int r1 = r1.size()
            if (r1 <= 0) goto L_0x01af
            org.telegram.ui.GroupCallActivity r1 = r0.activity
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallStatusIcon> r1 = r1.statusIconPool
            int r11 = r1.size()
            int r11 = r11 - r9
            java.lang.Object r1 = r1.remove(r11)
            org.telegram.ui.Components.voip.GroupCallStatusIcon r1 = (org.telegram.ui.Components.voip.GroupCallStatusIcon) r1
            r0.statusIcon = r1
            goto L_0x01b6
        L_0x01af:
            org.telegram.ui.Components.voip.GroupCallStatusIcon r1 = new org.telegram.ui.Components.voip.GroupCallStatusIcon
            r1.<init>()
            r0.statusIcon = r1
        L_0x01b6:
            org.telegram.ui.Components.voip.GroupCallStatusIcon r1 = r0.statusIcon
            r1.setCallback(r0)
            org.telegram.ui.Components.voip.GroupCallStatusIcon r1 = r0.statusIcon
            org.telegram.ui.Components.RLottieImageView r11 = r0.micIconView
            r1.setImageView(r11)
            r0.updateIconColor(r10)
            android.view.ViewParent r1 = r21.getParent()
            if (r1 != 0) goto L_0x01dd
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r1 = r0.parentContainer
            r11 = 51
            r12 = 46
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r12, r11)
            r1.addView(r0, r11)
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r1 = r0.attachedRenderers
            r1.add(r0)
        L_0x01dd:
            r0.checkScale = r9
            r0.animateEnter = r10
            android.view.ViewPropertyAnimator r1 = r21.animate()
            android.view.ViewPropertyAnimator r1 = r1.setListener(r6)
            r1.cancel()
            org.telegram.ui.Components.voip.VoIPTextureView r1 = r0.textureView
            android.animation.ValueAnimator r1 = r1.currentAnimation
            if (r1 != 0) goto L_0x0234
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r1 = r0.secondaryView
            if (r1 == 0) goto L_0x0234
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r0.primaryView
            if (r1 != 0) goto L_0x0234
            boolean r1 = r21.hasImage()
            if (r1 != 0) goto L_0x0234
            r0.setScaleX(r3)
            r0.setScaleY(r3)
            r0.setAlpha(r5)
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
            goto L_0x023d
        L_0x0234:
            r0.setScaleY(r8)
            r0.setScaleX(r8)
            r0.setAlpha(r8)
        L_0x023d:
            org.telegram.messenger.ChatObject$Call r1 = r0.call
            java.util.HashMap<java.lang.String, android.graphics.Bitmap> r1 = r1.thumbs
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            boolean r11 = r3.presentation
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            if (r11 == 0) goto L_0x024c
            java.lang.String r3 = r3.presentationEndpoint
            goto L_0x024e
        L_0x024c:
            java.lang.String r3 = r3.videoEndpoint
        L_0x024e:
            java.lang.Object r1 = r1.get(r3)
            android.graphics.Bitmap r1 = (android.graphics.Bitmap) r1
            r0.thumb = r1
            org.telegram.ui.Components.voip.VoIPTextureView r3 = r0.textureView
            r3.setThumb(r1)
            android.graphics.Bitmap r1 = r0.thumb
            if (r1 != 0) goto L_0x02fd
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r1.participant
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer
            int r1 = org.telegram.messenger.MessageObject.getPeerId(r1)
            r3 = 1053609165(0x3ecccccd, float:0.4)
            r11 = 1045220557(0x3e4ccccd, float:0.2)
            r12 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            if (r1 <= 0) goto L_0x02b9
            int r13 = r0.currentAccount
            org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r13.getUser(r1)
            org.telegram.messenger.ImageLocation r15 = org.telegram.messenger.ImageLocation.getForUser(r1, r9)
            if (r1 == 0) goto L_0x028e
            int r13 = r1.id
            int r13 = org.telegram.ui.Components.AvatarDrawable.getColorForId(r13)
            goto L_0x0292
        L_0x028e:
            int r13 = androidx.core.graphics.ColorUtils.blendARGB(r12, r7, r11)
        L_0x0292:
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
            goto L_0x02fd
        L_0x02b9:
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r1 = -r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r2.getChat(r1)
            org.telegram.messenger.ImageLocation r14 = org.telegram.messenger.ImageLocation.getForChat(r1, r9)
            if (r1 == 0) goto L_0x02d5
            int r2 = r1.id
            int r2 = org.telegram.ui.Components.AvatarDrawable.getColorForId(r2)
            goto L_0x02d9
        L_0x02d5:
            int r2 = androidx.core.graphics.ColorUtils.blendARGB(r12, r7, r11)
        L_0x02d9:
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
        L_0x02fd:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r1.participant
            boolean r1 = r1.self
            if (r1 == 0) goto L_0x031b
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x0343
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            boolean r3 = r3.presentation
            r1.setLocalSink(r2, r3)
            goto L_0x0343
        L_0x031b:
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x0343
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r2.participant
            boolean r2 = r2.presentation
            org.telegram.ui.Components.voip.VoIPTextureView r8 = r0.textureView
            org.webrtc.TextureViewRenderer r8 = r8.renderer
            r1.addRemoteSink(r3, r2, r8, r6)
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r2.participant
            boolean r2 = r2.presentation
            org.telegram.ui.Components.voip.VoIPTextureView r8 = r0.textureView
            org.webrtc.TextureViewRenderer r8 = r8.renderer
            r1.addRemoteSink(r3, r2, r8, r6)
        L_0x0343:
            android.widget.ImageView r1 = r0.screencastIcon
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            boolean r2 = r2.presentation
            if (r2 == 0) goto L_0x034d
            r2 = 0
            goto L_0x034f
        L_0x034d:
            r2 = 8
        L_0x034f:
            r1.setVisibility(r2)
            r1 = 0
            r2 = 1
            goto L_0x0358
        L_0x0355:
            r1 = r22
            r2 = 0
        L_0x0358:
            boolean r3 = r0.attached
            if (r3 == 0) goto L_0x05ac
            boolean r3 = r0.showingInFullscreen
            if (r3 == 0) goto L_0x0365
        L_0x0360:
            r3 = -1
        L_0x0361:
            r8 = 1065353216(0x3var_, float:1.0)
            r11 = 0
            goto L_0x0398
        L_0x0365:
            boolean r3 = r0.showingAsScrimView
            if (r3 == 0) goto L_0x036a
            goto L_0x0360
        L_0x036a:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r3 = r0.secondaryView
            r8 = 1117782016(0x42a00000, float:80.0)
            if (r3 == 0) goto L_0x0379
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r0.primaryView
            if (r11 != 0) goto L_0x0379
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            goto L_0x0361
        L_0x0379:
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r0.primaryView
            if (r11 == 0) goto L_0x037f
            if (r3 == 0) goto L_0x0383
        L_0x037f:
            boolean r3 = r0.isFullscreenMode
            if (r3 != 0) goto L_0x0393
        L_0x0383:
            if (r11 == 0) goto L_0x038c
            int r3 = r11.spanCount
            float r3 = (float) r3
            r8 = r3
            r3 = -1
            r11 = 1
            goto L_0x0398
        L_0x038c:
            r3 = 1110966272(0x42380000, float:46.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            goto L_0x0361
        L_0x0393:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            goto L_0x0361
        L_0x0398:
            android.view.ViewGroup$LayoutParams r12 = r21.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r12 = (android.view.ViewGroup.MarginLayoutParams) r12
            int r13 = r12.height
            if (r13 != r3) goto L_0x03b0
            if (r2 != 0) goto L_0x03b0
            boolean r2 = r0.useSpanSize
            if (r2 != r11) goto L_0x03b0
            if (r11 == 0) goto L_0x03dd
            float r2 = r0.spanCount
            int r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r2 == 0) goto L_0x03dd
        L_0x03b0:
            r12.height = r3
            if (r11 == 0) goto L_0x03b5
            goto L_0x03b6
        L_0x03b5:
            r7 = r3
        L_0x03b6:
            r12.width = r7
            r0.useSpanSize = r11
            r0.spanCount = r8
            r0.checkScale = r9
            if (r1 == 0) goto L_0x03c8
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            r2.animateToLayout()
            r0.updateNextLayoutAnimated = r9
            goto L_0x03cd
        L_0x03c8:
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            r2.requestLayout()
        L_0x03cd:
            org.telegram.ui.Components.voip.-$$Lambda$qlWeM_Ffarc-WFXvdj67MUDb5-A r2 = new org.telegram.ui.Components.voip.-$$Lambda$qlWeM_Ffarc-WFXvdj67MUDb5-A
            r2.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r2 = r0.parentContainer
            r2.requestLayout()
            r21.invalidate()
        L_0x03dd:
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r2.participant
            boolean r3 = r3.self
            if (r3 == 0) goto L_0x0406
            boolean r2 = r2.presentation
            if (r2 != 0) goto L_0x0406
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r2 == 0) goto L_0x0406
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r3 = r3.isFrontFaceCamera()
            r2.setMirror(r3)
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            r2.setRotateTextureWitchScreen(r9)
            goto L_0x0414
        L_0x0406:
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            r2.setMirror(r10)
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            r2.setRotateTextureWitchScreen(r10)
        L_0x0414:
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            boolean r2 = r2.self
            if (r2 == 0) goto L_0x0426
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            r3 = 720(0x2d0, float:1.009E-42)
            r2.setMaxTextureSize(r3)
            goto L_0x042d
        L_0x0426:
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            r2.setMaxTextureSize(r10)
        L_0x042d:
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r2.participant
            boolean r2 = r2.presentation
            org.telegram.messenger.ChatObject$Call r7 = r0.call
            boolean r2 = org.telegram.ui.GroupCallActivity.videoIsActive(r3, r2, r7)
            if (r2 != 0) goto L_0x04cf
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            org.telegram.messenger.ImageReceiver r2 = r2.avatarImageReceiver
            int r3 = r0.currentAccount
            r2.setCurrentAccount(r3)
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer
            int r2 = org.telegram.messenger.MessageObject.getPeerId(r2)
            if (r2 <= 0) goto L_0x0472
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
            goto L_0x0494
        L_0x0472:
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
        L_0x0494:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r8 = r0.noVideoStubLayout
            org.telegram.ui.Components.AvatarDrawable r8 = r8.avatarDrawable
            if (r7 == 0) goto L_0x04aa
            org.telegram.messenger.ImageLoader r11 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r7 = r7.location
            java.lang.String r12 = "50_50"
            android.graphics.drawable.BitmapDrawable r7 = r11.getImageFromMemory(r7, r6, r12)
            if (r7 == 0) goto L_0x04aa
            r14 = r7
            goto L_0x04ab
        L_0x04aa:
            r14 = r8
        L_0x04ab:
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
            goto L_0x04d0
        L_0x04cf:
            r2 = 1
        L_0x04d0:
            if (r1 == 0) goto L_0x04de
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r3 = r0.secondaryView
            if (r3 == 0) goto L_0x04de
            boolean r3 = r0.showingInFullscreen
            if (r3 != 0) goto L_0x04de
            if (r2 != 0) goto L_0x04de
            r3 = 1
            goto L_0x04df
        L_0x04de:
            r3 = 0
        L_0x04df:
            boolean r7 = r0.hasVideo
            if (r2 == r7) goto L_0x0596
            if (r3 != 0) goto L_0x0596
            r0.hasVideo = r2
            android.animation.ValueAnimator r2 = r0.noVideoStubAnimator
            if (r2 == 0) goto L_0x04f3
            r2.removeAllListeners()
            android.animation.ValueAnimator r2 = r0.noVideoStubAnimator
            r2.cancel()
        L_0x04f3:
            if (r1 == 0) goto L_0x0538
            boolean r2 = r0.hasVideo
            if (r2 != 0) goto L_0x050b
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            int r2 = r2.getVisibility()
            if (r2 == 0) goto L_0x050b
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            r2.setVisibility(r10)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            r2.setAlpha(r5)
        L_0x050b:
            float[] r2 = new float[r4]
            float r3 = r0.progressToNoVideoStub
            r2[r10] = r3
            boolean r3 = r0.hasVideo
            if (r3 == 0) goto L_0x0516
            goto L_0x0518
        L_0x0516:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x0518:
            r2[r9] = r5
            android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r2)
            r0.noVideoStubAnimator = r2
            org.telegram.ui.Components.voip.-$$Lambda$GroupCallMiniTextureView$3I091EEowAT7AjcKorY2CTAKl1E r3 = new org.telegram.ui.Components.voip.-$$Lambda$GroupCallMiniTextureView$3I091EEowAT7AjcKorY2CTAKl1E
            r3.<init>()
            r2.addUpdateListener(r3)
            android.animation.ValueAnimator r2 = r0.noVideoStubAnimator
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$6 r3 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$6
            r3.<init>()
            r2.addListener(r3)
            android.animation.ValueAnimator r2 = r0.noVideoStubAnimator
            r2.start()
            goto L_0x0558
        L_0x0538:
            boolean r2 = r0.hasVideo
            if (r2 == 0) goto L_0x053d
            goto L_0x053f
        L_0x053d:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x053f:
            r0.progressToNoVideoStub = r5
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r3 = r0.noVideoStubLayout
            if (r2 == 0) goto L_0x0548
            r2 = 8
            goto L_0x0549
        L_0x0548:
            r2 = 0
        L_0x0549:
            r3.setVisibility(r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            float r3 = r0.progressToNoVideoStub
            r2.setAlpha(r3)
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            r2.invalidate()
        L_0x0558:
            boolean r2 = r0.hasVideo
            if (r2 == 0) goto L_0x0596
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            r2.updateMuteButtonState(r10)
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            boolean r2 = r2.self
            if (r2 == 0) goto L_0x057f
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r2 == 0) goto L_0x0596
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.ui.Components.voip.VoIPTextureView r3 = r0.textureView
            org.webrtc.TextureViewRenderer r3 = r3.renderer
            org.telegram.messenger.ChatObject$VideoParticipant r4 = r0.participant
            boolean r4 = r4.presentation
            r2.setLocalSink(r3, r4)
            goto L_0x0596
        L_0x057f:
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r2 == 0) goto L_0x0596
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r4 = r3.participant
            boolean r3 = r3.presentation
            org.telegram.ui.Components.voip.VoIPTextureView r5 = r0.textureView
            org.webrtc.TextureViewRenderer r5 = r5.renderer
            r2.addRemoteSink(r4, r3, r5, r6)
        L_0x0596:
            org.telegram.ui.Components.voip.GroupCallStatusIcon r2 = r0.statusIcon
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            r2.setParticipant(r3, r1)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r1 = r0.noVideoStubLayout
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x05ac
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r1 = r0.noVideoStubLayout
            r1.updateMuteButtonState(r9)
        L_0x05ac:
            r21.updateInfo()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallMiniTextureView.updateAttachState(boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateAttachState$0 */
    public /* synthetic */ void lambda$updateAttachState$0$GroupCallMiniTextureView(ValueAnimator valueAnimator) {
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

    public void updatePosition(ViewGroup viewGroup, RecyclerListView recyclerListView, GroupCallRenderersContainer groupCallRenderersContainer) {
        if (!this.showingAsScrimView && !this.animateToScrimView && !this.forceDetached) {
            float f = groupCallRenderersContainer.progressToFullscreenMode;
            if (this.animateToFullscreen || this.showingInFullscreen) {
                GroupCallGridCell groupCallGridCell = this.primaryView;
                if (groupCallGridCell != null) {
                    float y = (((groupCallGridCell.getY() + ((float) AndroidUtilities.dp(2.0f))) + viewGroup.getY()) - ((float) getTop())) - ((float) groupCallRenderersContainer.getTop());
                    float f2 = 1.0f - f;
                    float f3 = 0.0f * f;
                    setTranslationX(((((groupCallGridCell.getX() + viewGroup.getX()) - ((float) getLeft())) - ((float) groupCallRenderersContainer.getLeft())) * f2) + f3);
                    setTranslationY((y * f2) + f3);
                    this.textureView.setRoundCorners((float) AndroidUtilities.dp(8.0f));
                } else {
                    setTranslationX(0.0f);
                    setTranslationY(0.0f);
                }
                GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = this.secondaryView;
                if (groupCallUserCell != null) {
                    groupCallUserCell.setAlpha(f);
                }
                if (!this.animateEnter) {
                    setAlpha(1.0f);
                    return;
                }
                return;
            }
            GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell2 = this.secondaryView;
            if (groupCallUserCell2 != null) {
                if (groupCallUserCell2.isRemoving(recyclerListView)) {
                    setAlpha(this.secondaryView.getAlpha());
                } else if (this.primaryView == null) {
                    if (this.attached && !this.animateEnter) {
                        setAlpha(f);
                    }
                    this.secondaryView.setAlpha(f);
                    f = 1.0f;
                } else {
                    this.secondaryView.setAlpha(1.0f);
                    if (this.attached && !this.animateEnter) {
                        setAlpha(1.0f);
                    }
                }
                setTranslationX((this.secondaryView.getX() + recyclerListView.getX()) - ((float) getLeft()));
                float f4 = 1.0f - f;
                setTranslationY((((((float) AndroidUtilities.dp(2.0f)) * f4) + this.secondaryView.getY()) + recyclerListView.getY()) - ((float) getTop()));
                this.textureView.setRoundCorners((((float) AndroidUtilities.dp(13.0f)) * f) + (((float) AndroidUtilities.dp(8.0f)) * f4));
                return;
            }
            GroupCallGridCell groupCallGridCell2 = this.primaryView;
            if (groupCallGridCell2 != null) {
                if (groupCallGridCell2 instanceof GroupCallGridCell) {
                    setTranslationX(((groupCallGridCell2.getX() + viewGroup.getX()) - ((float) getLeft())) - ((float) groupCallRenderersContainer.getLeft()));
                    setTranslationY((((groupCallGridCell2.getY() + ((float) AndroidUtilities.dp(2.0f))) + viewGroup.getY()) - ((float) getTop())) - ((float) groupCallRenderersContainer.getTop()));
                    this.textureView.setRoundCorners((float) AndroidUtilities.dp(8.0f));
                }
                if (this.attached && !this.animateEnter) {
                    setAlpha((1.0f - f) * this.primaryView.getAlpha());
                }
            }
        }
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
        this.attachedRenderers.remove(this);
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
                    GroupCallMiniTextureView.this.lambda$saveThumb$2$GroupCallMiniTextureView(bitmap, i);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$saveThumb$2 */
    public /* synthetic */ void lambda$saveThumb$2$GroupCallMiniTextureView(Bitmap bitmap, int i) {
        if (bitmap != null && bitmap.getPixel(0, 0) != 0) {
            Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(bitmap.getWidth(), bitmap.getHeight()) / 180));
            AndroidUtilities.runOnUIThread(new Runnable(bitmap) {
                public final /* synthetic */ Bitmap f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    GroupCallMiniTextureView.this.lambda$saveThumb$1$GroupCallMiniTextureView(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$saveThumb$1 */
    public /* synthetic */ void lambda$saveThumb$1$GroupCallMiniTextureView(Bitmap bitmap) {
        HashMap<String, Bitmap> hashMap = this.call.thumbs;
        ChatObject.VideoParticipant videoParticipant = this.participant;
        boolean z = videoParticipant.presentation;
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = videoParticipant.participant;
        hashMap.put(z ? tLRPC$TL_groupCallParticipant.presentationEndpoint : tLRPC$TL_groupCallParticipant.videoEndpoint, bitmap);
    }

    public void setViews(GroupCallGridCell groupCallGridCell, GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell) {
        this.primaryView = groupCallGridCell;
        this.secondaryView = groupCallUserCell;
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
