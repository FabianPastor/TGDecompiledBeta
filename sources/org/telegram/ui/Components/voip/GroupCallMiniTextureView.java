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
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BlobDrawable;
import org.telegram.ui.Components.CrossOutDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.GroupCallFullscreenAdapter;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
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
    ImageView blurredFlippingStub;
    ChatObject.Call call;
    /* access modifiers changed from: private */
    public Drawable castingScreenDrawable;
    /* access modifiers changed from: private */
    public boolean checkScale;
    int collapseSize;
    ValueAnimator colorAnimator;
    int currentAccount;
    public boolean drawFirst;
    ValueAnimator flipAnimator;
    boolean flipHalfReached;
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
    int lastSpeakingFrameColor;
    private final RLottieImageView micIconView;
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
    private Rect rect = new Rect();
    private final ImageView screencastIcon;
    public GroupCallFullscreenAdapter.GroupCallUserCell secondaryView;
    /* access modifiers changed from: private */
    public boolean showingAsScrimView;
    public boolean showingInFullscreen;
    float spanCount;
    Paint speakingPaint = new Paint(1);
    /* access modifiers changed from: private */
    public GroupCallStatusIcon statusIcon;
    /* access modifiers changed from: private */
    public TextView stopSharingTextView;
    private boolean swipeToBack;
    /* access modifiers changed from: private */
    public float swipeToBackDy;
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

    static /* synthetic */ float access$616(GroupCallMiniTextureView groupCallMiniTextureView, float f) {
        float f2 = groupCallMiniTextureView.videoIsPausedProgress + f;
        groupCallMiniTextureView.videoIsPausedProgress = f2;
        return f2;
    }

    static /* synthetic */ float access$624(GroupCallMiniTextureView groupCallMiniTextureView, float f) {
        float f2 = groupCallMiniTextureView.videoIsPausedProgress - f;
        groupCallMiniTextureView.videoIsPausedProgress = f2;
        return f2;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GroupCallMiniTextureView(GroupCallRenderersContainer groupCallRenderersContainer, ArrayList<GroupCallMiniTextureView> arrayList, ChatObject.Call call2, GroupCallActivity groupCallActivity) {
        super(groupCallRenderersContainer.getContext());
        ChatObject.Call call3 = call2;
        final GroupCallRenderersContainer groupCallRenderersContainer2 = groupCallRenderersContainer;
        final ChatObject.Call call4 = call2;
        final GroupCallActivity groupCallActivity2 = groupCallActivity;
        this.call = call3;
        this.currentAccount = groupCallActivity.getCurrentAccount();
        CrossOutDrawable crossOutDrawable = new CrossOutDrawable(groupCallRenderersContainer.getContext(), NUM, (String) null);
        this.pausedVideoDrawable = crossOutDrawable;
        crossOutDrawable.setCrossOut(true, false);
        this.pausedVideoDrawable.setOffsets((float) (-AndroidUtilities.dp(4.0f)), (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f));
        this.pausedVideoDrawable.setStrokeWidth(AndroidUtilities.dpf2(3.4f));
        this.castingScreenDrawable = groupCallRenderersContainer.getContext().getResources().getDrawable(NUM).mutate();
        final TextPaint textPaint = r2;
        TextPaint textPaint2 = new TextPaint(1);
        textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textPaint2.setTextSize((float) AndroidUtilities.dp(13.0f));
        textPaint2.setColor(-1);
        final TextPaint textPaint3 = r13;
        TextPaint textPaint4 = new TextPaint(1);
        textPaint4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textPaint4.setTextSize((float) AndroidUtilities.dp(15.0f));
        textPaint4.setColor(-1);
        String string = LocaleController.getString("VoipVideoOnPause", NUM);
        final String str = string;
        final StaticLayout staticLayout = r19;
        TextPaint textPaint5 = textPaint2;
        StaticLayout staticLayout2 = new StaticLayout(LocaleController.getString("VoipVideoScreenSharingTwoLines", NUM), textPaint5, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(call3.chatId));
        String str2 = string;
        final StaticLayout staticLayout3 = r19;
        StaticLayout staticLayout4 = new StaticLayout(LocaleController.formatString("VoipVideoNotAvailable", NUM, LocaleController.formatPluralString("Participants", MessagesController.getInstance(this.currentAccount).groipCallVideoMaxParticipants)), textPaint5, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        String string2 = LocaleController.getString("VoipVideoScreenSharing", NUM);
        String str3 = str2;
        final String str4 = string2;
        final float measureText = textPaint2.measureText(str3);
        final float measureText2 = textPaint4.measureText(string2);
        AnonymousClass1 r28 = r0;
        AnonymousClass1 r0 = new VoIPTextureView(this, groupCallRenderersContainer.getContext(), false, false, true, true) {
            float overlayIconAlphaFrom;
            final /* synthetic */ GroupCallMiniTextureView this$0;

            {
                this.this$0 = r3;
            }

            public void animateToLayout() {
                super.animateToLayout();
                this.overlayIconAlphaFrom = this.this$0.overlayIconAlpha;
            }

            /* access modifiers changed from: protected */
            public void updateRendererSize() {
                super.updateRendererSize();
                ImageView imageView = this.this$0.blurredFlippingStub;
                if (imageView != null && imageView.getParent() != null) {
                    this.this$0.blurredFlippingStub.getLayoutParams().width = this.this$0.textureView.renderer.getMeasuredWidth();
                    this.this$0.blurredFlippingStub.getLayoutParams().height = this.this$0.textureView.renderer.getMeasuredHeight();
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                float f;
                float f2;
                float f3;
                float f4;
                Canvas canvas2 = canvas;
                if (!this.renderer.isFirstFrameRendered() || (!(this.renderer.getAlpha() == 1.0f || this.blurRenderer.getAlpha() == 1.0f) || this.this$0.videoIsPaused)) {
                    if (this.this$0.progressToBackground != 1.0f) {
                        GroupCallMiniTextureView.access$116(this.this$0, 0.10666667f);
                        if (this.this$0.progressToBackground > 1.0f) {
                            float unused = this.this$0.progressToBackground = 1.0f;
                        } else {
                            invalidate();
                        }
                    }
                    GroupCallMiniTextureView groupCallMiniTextureView = this.this$0;
                    if (groupCallMiniTextureView.thumb != null) {
                        canvas.save();
                        float f5 = this.currentThumbScale;
                        canvas2.scale(f5, f5, ((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f);
                        GroupCallMiniTextureView groupCallMiniTextureView2 = this.this$0;
                        if (groupCallMiniTextureView2.thumbPaint == null) {
                            groupCallMiniTextureView2.thumbPaint = new Paint(1);
                            this.this$0.thumbPaint.setFilterBitmap(true);
                        }
                        canvas2.drawBitmap(this.this$0.thumb, ((float) (getMeasuredWidth() - this.this$0.thumb.getWidth())) / 2.0f, ((float) (getMeasuredHeight() - this.this$0.thumb.getHeight())) / 2.0f, this.this$0.thumbPaint);
                        canvas.restore();
                    } else {
                        groupCallMiniTextureView.imageReceiver.setImageCoords(this.currentClipHorizontal, this.currentClipVertical, ((float) getMeasuredWidth()) - (this.currentClipHorizontal * 2.0f), ((float) getMeasuredHeight()) - (this.currentClipVertical * 2.0f));
                        GroupCallMiniTextureView groupCallMiniTextureView3 = this.this$0;
                        groupCallMiniTextureView3.imageReceiver.setAlpha(groupCallMiniTextureView3.progressToBackground);
                        this.this$0.imageReceiver.draw(canvas2);
                    }
                    GroupCallMiniTextureView groupCallMiniTextureView4 = this.this$0;
                    ChatObject.VideoParticipant videoParticipant = groupCallMiniTextureView4.participant;
                    if (videoParticipant == call4.videoNotAvailableParticipant) {
                        if (groupCallMiniTextureView4.showingInFullscreen || !groupCallRenderersContainer2.inFullscreenMode) {
                            float dp = (float) AndroidUtilities.dp(48.0f);
                            textPaint.setAlpha(255);
                            canvas.save();
                            canvas2.translate((((((float) getMeasuredWidth()) - dp) / 2.0f) - (((float) AndroidUtilities.dp(400.0f)) / 2.0f)) + (dp / 2.0f), (((float) (getMeasuredHeight() / 2)) - dp) + dp + ((float) AndroidUtilities.dp(10.0f)));
                            staticLayout3.draw(canvas2);
                            canvas.restore();
                        }
                        if (this.this$0.stopSharingTextView.getVisibility() != 4) {
                            this.this$0.stopSharingTextView.setVisibility(4);
                        }
                    } else if (!videoParticipant.presentation || !videoParticipant.participant.self) {
                        if (groupCallMiniTextureView4.stopSharingTextView.getVisibility() != 4) {
                            this.this$0.stopSharingTextView.setVisibility(4);
                        }
                        groupCallActivity2.cellFlickerDrawable.draw(canvas2, this.this$0);
                    } else {
                        if (groupCallMiniTextureView4.stopSharingTextView.getVisibility() != 0) {
                            this.this$0.stopSharingTextView.setVisibility(0);
                            this.this$0.stopSharingTextView.setScaleX(1.0f);
                            this.this$0.stopSharingTextView.setScaleY(1.0f);
                        }
                        float f6 = this.this$0.drawFirst ? 0.0f : groupCallRenderersContainer2.progressToFullscreenMode;
                        int dp2 = AndroidUtilities.dp(33.0f);
                        GroupCallMiniTextureView groupCallMiniTextureView5 = this.this$0;
                        if (groupCallMiniTextureView5.animateToFullscreen || groupCallMiniTextureView5.showingInFullscreen) {
                            f3 = (float) dp2;
                            f2 = ((float) AndroidUtilities.dp(10.0f)) + (((float) AndroidUtilities.dp(39.0f)) * groupCallRenderersContainer2.progressToFullscreenMode);
                        } else {
                            f3 = (float) dp2;
                            f2 = ((float) AndroidUtilities.dp(10.0f)) * Math.max(1.0f - groupCallRenderersContainer2.progressToFullscreenMode, (this.this$0.showingAsScrimView || this.this$0.animateToScrimView) ? groupCallRenderersContainer2.progressToScrimView : 0.0f);
                        }
                        int i = (int) (f3 + f2);
                        int measuredWidth = (getMeasuredWidth() - i) / 2;
                        float f7 = (this.this$0.showingAsScrimView || this.this$0.animateToScrimView) ? groupCallRenderersContainer2.progressToScrimView : 0.0f;
                        GroupCallMiniTextureView groupCallMiniTextureView6 = this.this$0;
                        if (groupCallMiniTextureView6.showingInFullscreen) {
                            f4 = f6;
                        } else {
                            f6 = groupCallMiniTextureView6.animateToFullscreen ? groupCallRenderersContainer2.progressToFullscreenMode : f7;
                            f4 = (groupCallMiniTextureView6.showingAsScrimView || this.this$0.animateToScrimView) ? groupCallRenderersContainer2.progressToScrimView : groupCallRenderersContainer2.progressToFullscreenMode;
                        }
                        float measuredHeight = (float) (((getMeasuredHeight() - i) / 2) - AndroidUtilities.dp(28.0f));
                        float dp3 = (float) AndroidUtilities.dp(17.0f);
                        float dp4 = (float) AndroidUtilities.dp(74.0f);
                        GroupCallMiniTextureView groupCallMiniTextureView7 = this.this$0;
                        int dp5 = (int) ((measuredHeight - ((dp3 + (dp4 * ((groupCallMiniTextureView7.showingInFullscreen || groupCallMiniTextureView7.animateToFullscreen) ? groupCallRenderersContainer2.progressToFullscreenMode : 0.0f))) * f6)) + (((float) AndroidUtilities.dp(17.0f)) * f4));
                        int i2 = dp5 + i;
                        this.this$0.castingScreenDrawable.setBounds(measuredWidth, dp5, measuredWidth + i, i2);
                        this.this$0.castingScreenDrawable.draw(canvas2);
                        float f8 = groupCallRenderersContainer2.progressToFullscreenMode;
                        if (f8 > 0.0f || f7 > 0.0f) {
                            float max = Math.max(f8, f7) * f6;
                            textPaint3.setAlpha((int) (max * 255.0f));
                            GroupCallMiniTextureView groupCallMiniTextureView8 = this.this$0;
                            if (groupCallMiniTextureView8.animateToFullscreen || groupCallMiniTextureView8.showingInFullscreen) {
                                groupCallMiniTextureView8.stopSharingTextView.setAlpha(max * (1.0f - f7));
                            } else {
                                groupCallMiniTextureView8.stopSharingTextView.setAlpha(0.0f);
                            }
                            canvas2.drawText(str4, (((float) measuredWidth) - (measureText2 / 2.0f)) + (((float) i) / 2.0f), (float) (AndroidUtilities.dp(32.0f) + i2), textPaint3);
                        } else {
                            this.this$0.stopSharingTextView.setAlpha(0.0f);
                        }
                        this.this$0.stopSharingTextView.setTranslationY((((float) (AndroidUtilities.dp(72.0f) + i2)) + this.this$0.swipeToBackDy) - this.currentClipVertical);
                        this.this$0.stopSharingTextView.setTranslationX(((float) ((getMeasuredWidth() - this.this$0.stopSharingTextView.getMeasuredWidth()) / 2)) - this.currentClipHorizontal);
                        float f9 = groupCallRenderersContainer2.progressToFullscreenMode;
                        if (f9 < 1.0f && f7 < 1.0f) {
                            TextPaint textPaint = textPaint;
                            double max2 = (double) Math.max(f9, f7);
                            Double.isNaN(max2);
                            textPaint.setAlpha((int) ((1.0d - max2) * 255.0d));
                            canvas.save();
                            canvas2.translate((((float) measuredWidth) - (((float) AndroidUtilities.dp(400.0f)) / 2.0f)) + (((float) i) / 2.0f), (float) (i2 + AndroidUtilities.dp(10.0f)));
                            staticLayout.draw(canvas2);
                            canvas.restore();
                        }
                    }
                    invalidate();
                }
                ImageView imageView = this.this$0.blurredFlippingStub;
                if (!(imageView == null || imageView.getParent() == null)) {
                    GroupCallMiniTextureView groupCallMiniTextureView9 = this.this$0;
                    groupCallMiniTextureView9.blurredFlippingStub.setScaleX(groupCallMiniTextureView9.textureView.renderer.getScaleX());
                    GroupCallMiniTextureView groupCallMiniTextureView10 = this.this$0;
                    groupCallMiniTextureView10.blurredFlippingStub.setScaleY(groupCallMiniTextureView10.textureView.renderer.getScaleY());
                }
                super.dispatchDraw(canvas);
                float measuredHeight2 = (((float) getMeasuredHeight()) - this.currentClipVertical) - ((float) AndroidUtilities.dp(80.0f));
                if (this.this$0.participant != call4.videoNotAvailableParticipant) {
                    canvas.save();
                    GroupCallMiniTextureView groupCallMiniTextureView11 = this.this$0;
                    if ((groupCallMiniTextureView11.showingInFullscreen || groupCallMiniTextureView11.animateToFullscreen) && !GroupCallActivity.isLandscapeMode && !GroupCallActivity.isTabletMode) {
                        GroupCallRenderersContainer groupCallRenderersContainer = groupCallRenderersContainer2;
                        measuredHeight2 -= (((float) AndroidUtilities.dp(90.0f)) * groupCallRenderersContainer.progressToFullscreenMode) * (1.0f - groupCallRenderersContainer.progressToHideUi);
                    }
                    canvas2.translate(0.0f, measuredHeight2);
                    canvas2.drawPaint(this.this$0.gradientPaint);
                    canvas.restore();
                }
                if (this.this$0.videoIsPaused || this.this$0.videoIsPausedProgress != 0.0f) {
                    if (this.this$0.videoIsPaused && this.this$0.videoIsPausedProgress != 1.0f) {
                        GroupCallMiniTextureView.access$616(this.this$0, 0.064f);
                        if (this.this$0.videoIsPausedProgress > 1.0f) {
                            float unused2 = this.this$0.videoIsPausedProgress = 1.0f;
                        } else {
                            invalidate();
                        }
                    } else if (!this.this$0.videoIsPaused && this.this$0.videoIsPausedProgress != 0.0f) {
                        GroupCallMiniTextureView.access$624(this.this$0, 0.064f);
                        if (this.this$0.videoIsPausedProgress < 0.0f) {
                            float unused3 = this.this$0.videoIsPausedProgress = 0.0f;
                        } else {
                            invalidate();
                        }
                    }
                    float access$600 = this.this$0.videoIsPausedProgress;
                    if (isInAnimation()) {
                        float var_ = this.overlayIconAlphaFrom;
                        float var_ = this.animationProgress;
                        f = (var_ * (1.0f - var_)) + (this.this$0.overlayIconAlpha * var_);
                    } else {
                        f = this.this$0.overlayIconAlpha;
                    }
                    float var_ = access$600 * f;
                    if (var_ > 0.0f) {
                        float dp6 = (float) AndroidUtilities.dp(48.0f);
                        float measuredWidth2 = (((float) getMeasuredWidth()) - dp6) / 2.0f;
                        float measuredHeight3 = (((float) getMeasuredHeight()) - dp6) / 2.0f;
                        if (this.this$0.participant == call4.videoNotAvailableParticipant) {
                            measuredHeight3 -= dp6 / 2.5f;
                        }
                        RectF rectF = AndroidUtilities.rectTmp;
                        float var_ = measuredHeight3 + dp6;
                        rectF.set((float) ((int) measuredWidth2), (float) ((int) measuredHeight3), (float) ((int) (measuredWidth2 + dp6)), (float) ((int) var_));
                        if (var_ != 1.0f) {
                            canvas2.saveLayerAlpha(rectF, (int) (var_ * 255.0f), 31);
                        } else {
                            canvas.save();
                        }
                        this.this$0.pausedVideoDrawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                        this.this$0.pausedVideoDrawable.draw(canvas2);
                        canvas.restore();
                        float var_ = var_ * groupCallRenderersContainer2.progressToFullscreenMode;
                        if (var_ > 0.0f && this.this$0.participant != call4.videoNotAvailableParticipant) {
                            textPaint.setAlpha((int) (var_ * 255.0f));
                            canvas2.drawText(str, (measuredWidth2 - (measureText / 2.0f)) + (dp6 / 2.0f), var_ + ((float) AndroidUtilities.dp(16.0f)), textPaint);
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                GroupCallMiniTextureView groupCallMiniTextureView = this.this$0;
                if (!groupCallMiniTextureView.inPinchToZoom || view != groupCallMiniTextureView.textureView.renderer) {
                    return super.drawChild(canvas, view, j);
                }
                canvas.save();
                GroupCallMiniTextureView groupCallMiniTextureView2 = this.this$0;
                float f = groupCallMiniTextureView2.pinchScale;
                canvas.scale(f, f, groupCallMiniTextureView2.pinchCenterX, groupCallMiniTextureView2.pinchCenterY);
                GroupCallMiniTextureView groupCallMiniTextureView3 = this.this$0;
                canvas.translate(groupCallMiniTextureView3.pinchTranslationX, groupCallMiniTextureView3.pinchTranslationY);
                boolean drawChild = super.drawChild(canvas, view, j);
                canvas.restore();
                return drawChild;
            }

            public void invalidate() {
                super.invalidate();
                boolean unused = this.this$0.invalidateFromChild = true;
                this.this$0.invalidate();
                boolean unused2 = this.this$0.invalidateFromChild = false;
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int i5;
                ChatObject.VideoParticipant videoParticipant;
                GroupCallMiniTextureView groupCallMiniTextureView = this.this$0;
                if (groupCallMiniTextureView.attached && groupCallMiniTextureView.checkScale) {
                    TextureViewRenderer textureViewRenderer = this.renderer;
                    if (!(textureViewRenderer.rotatedFrameHeight == 0 || textureViewRenderer.rotatedFrameWidth == 0)) {
                        if (this.this$0.showingAsScrimView) {
                            this.this$0.textureView.scaleType = VoIPTextureView.SCALE_TYPE_FIT;
                        } else {
                            GroupCallMiniTextureView groupCallMiniTextureView2 = this.this$0;
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
                        boolean unused = this.this$0.checkScale = false;
                    }
                }
                super.onLayout(z, i, i2, i3, i4);
                TextureViewRenderer textureViewRenderer2 = this.renderer;
                int i6 = textureViewRenderer2.rotatedFrameHeight;
                if (i6 != 0 && (i5 = textureViewRenderer2.rotatedFrameWidth) != 0 && (videoParticipant = this.this$0.participant) != null) {
                    videoParticipant.setAspectRatio(((float) i5) / ((float) i6), call4);
                }
            }

            public void requestLayout() {
                this.this$0.requestLayout();
                super.requestLayout();
            }

            /* access modifiers changed from: protected */
            public void onFirstFrameRendered() {
                invalidate();
                if (!this.this$0.videoIsPaused && this.renderer.getAlpha() != 1.0f) {
                    this.renderer.animate().setDuration(300).alpha(1.0f);
                }
                TextureView textureView = this.blurRenderer;
                if (!(textureView == null || textureView.getAlpha() == 1.0f)) {
                    this.blurRenderer.animate().setDuration(300).alpha(1.0f);
                }
                ImageView imageView = this.this$0.blurredFlippingStub;
                if (imageView != null && imageView.getParent() != null) {
                    if (this.this$0.blurredFlippingStub.getAlpha() == 1.0f) {
                        this.this$0.blurredFlippingStub.animate().alpha(0.0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (AnonymousClass1.this.this$0.blurredFlippingStub.getParent() != null) {
                                    GroupCallMiniTextureView groupCallMiniTextureView = AnonymousClass1.this.this$0;
                                    groupCallMiniTextureView.textureView.removeView(groupCallMiniTextureView.blurredFlippingStub);
                                }
                            }
                        }).start();
                    } else if (this.this$0.blurredFlippingStub.getParent() != null) {
                        GroupCallMiniTextureView groupCallMiniTextureView = this.this$0;
                        groupCallMiniTextureView.textureView.removeView(groupCallMiniTextureView.blurredFlippingStub);
                    }
                }
            }
        };
        AnonymousClass1 r1 = r28;
        this.textureView = r1;
        r1.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        this.parentContainer = groupCallRenderersContainer;
        this.attachedRenderers = arrayList;
        this.activity = groupCallActivity;
        r1.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
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
        r1.attachBackgroundRenderer();
        setClipChildren(false);
        r1.renderer.setAlpha(0.0f);
        addView(r1);
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
        Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(19.0f), 0, ColorUtils.setAlphaComponent(-1, 100));
        AnonymousClass3 r6 = new TextView(groupCallRenderersContainer.getContext()) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (Math.abs(GroupCallMiniTextureView.this.stopSharingTextView.getAlpha() - 1.0f) > 0.001f) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.stopSharingTextView = r6;
        r6.setText(LocaleController.getString("VoipVideoScreenStopSharing", NUM));
        this.stopSharingTextView.setTextSize(1, 15.0f);
        this.stopSharingTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.stopSharingTextView.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
        this.stopSharingTextView.setTextColor(-1);
        this.stopSharingTextView.setBackground(createSimpleSelectorRoundRectDrawable);
        this.stopSharingTextView.setGravity(17);
        this.stopSharingTextView.setOnClickListener(new GroupCallMiniTextureView$$ExternalSyntheticLambda3(this));
        addView(this.stopSharingTextView, LayoutHelper.createFrame(-2, 38, 51));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().stopScreenCapture();
        }
        this.stopSharingTextView.animate().alpha(0.0f).scaleX(0.0f).scaleY(0.0f).setDuration(180).start();
    }

    public boolean isInsideStopScreenButton(float f, float f2) {
        this.stopSharingTextView.getHitRect(this.rect);
        return this.rect.contains((int) f, (int) f2);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.attached) {
            float y = (((this.textureView.getY() + ((float) this.textureView.getMeasuredHeight())) - this.textureView.currentClipVertical) - ((float) this.infoContainer.getMeasuredHeight())) + this.swipeToBackDy;
            if (this.showingAsScrimView || this.animateToScrimView) {
                this.infoContainer.setAlpha(1.0f - this.parentContainer.progressToScrimView);
                this.micIconView.setAlpha(1.0f - this.parentContainer.progressToScrimView);
            } else if (this.showingInFullscreen || this.animateToFullscreen) {
                if (!GroupCallActivity.isLandscapeMode && !GroupCallActivity.isTabletMode) {
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
            this.infoContainer.setTranslationX(this.drawFirst ? 0.0f : ((float) AndroidUtilities.dp(6.0f)) * this.parentContainer.progressToFullscreenMode);
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
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0169  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x017a  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x019e  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x01a8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r14, int r15) {
        /*
            r13 = this;
            android.widget.FrameLayout r0 = r13.infoContainer
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r0 = (android.widget.FrameLayout.LayoutParams) r0
            int r1 = r0.leftMargin
            boolean r1 = r13.lastLandscapeMode
            boolean r2 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r1 == r2) goto L_0x0015
            r1 = 1
            r13.checkScale = r1
            r13.lastLandscapeMode = r2
        L_0x0015:
            r1 = 1073741824(0x40000000, float:2.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.rightMargin = r2
            r0.leftMargin = r2
            boolean r0 = r13.updateNextLayoutAnimated
            r2 = 1065353216(0x3var_, float:1.0)
            if (r0 == 0) goto L_0x0048
            org.telegram.ui.ActionBar.SimpleTextView r0 = r13.nameView
            android.view.ViewPropertyAnimator r0 = r0.animate()
            android.view.ViewPropertyAnimator r0 = r0.scaleX(r2)
            android.view.ViewPropertyAnimator r0 = r0.scaleY(r2)
            r0.start()
            org.telegram.ui.Components.RLottieImageView r0 = r13.micIconView
            android.view.ViewPropertyAnimator r0 = r0.animate()
            android.view.ViewPropertyAnimator r0 = r0.scaleX(r2)
            android.view.ViewPropertyAnimator r0 = r0.scaleY(r2)
            r0.start()
            goto L_0x0077
        L_0x0048:
            org.telegram.ui.ActionBar.SimpleTextView r0 = r13.nameView
            android.view.ViewPropertyAnimator r0 = r0.animate()
            r0.cancel()
            org.telegram.ui.ActionBar.SimpleTextView r0 = r13.nameView
            r0.setScaleX(r2)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r13.nameView
            r0.setScaleY(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r13.micIconView
            android.view.ViewPropertyAnimator r0 = r0.animate()
            r0.cancel()
            org.telegram.ui.Components.RLottieImageView r0 = r13.micIconView
            r0.setScaleX(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r13.micIconView
            r0.setScaleY(r2)
            android.widget.FrameLayout r0 = r13.infoContainer
            android.view.ViewPropertyAnimator r0 = r0.animate()
            r0.cancel()
        L_0x0077:
            r0 = 0
            r13.updateNextLayoutAnimated = r0
            boolean r3 = r13.showingInFullscreen
            r4 = 0
            r5 = 1082130432(0x40800000, float:4.0)
            r6 = 1073741824(0x40000000, float:2.0)
            if (r3 == 0) goto L_0x00e7
            r13.updateSize(r0)
            r13.overlayIconAlpha = r2
            boolean r0 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r0 == 0) goto L_0x00ad
            int r0 = android.view.View.MeasureSpec.getSize(r14)
            r2 = 1134821376(0x43a40000, float:328.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            int r2 = android.view.View.MeasureSpec.getSize(r15)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r2 = r2 - r3
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6)
            int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r6)
            super.onMeasure(r0, r2)
            goto L_0x01c9
        L_0x00ad:
            boolean r0 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            r2 = 1119354880(0x42b80000, float:92.0)
            if (r0 != 0) goto L_0x00cd
            int r0 = android.view.View.MeasureSpec.getSize(r15)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            int r2 = android.view.View.MeasureSpec.getSize(r14)
            int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r6)
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6)
            super.onMeasure(r2, r0)
            goto L_0x01c9
        L_0x00cd:
            int r0 = android.view.View.MeasureSpec.getSize(r14)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6)
            int r2 = android.view.View.MeasureSpec.getSize(r15)
            int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r6)
            super.onMeasure(r0, r2)
            goto L_0x01c9
        L_0x00e7:
            boolean r3 = r13.showingAsScrimView
            r7 = 1096810496(0x41600000, float:14.0)
            r8 = 2
            if (r3 == 0) goto L_0x0115
            r13.overlayIconAlpha = r2
            int r0 = android.view.View.MeasureSpec.getSize(r14)
            int r2 = android.view.View.MeasureSpec.getSize(r15)
            int r0 = java.lang.Math.min(r0, r2)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r2 = r2 * 2
            int r0 = r0 - r2
            int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6)
            int r3 = r13.getPaddingBottom()
            int r0 = r0 + r3
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6)
            super.onMeasure(r2, r0)
            goto L_0x01c9
        L_0x0115:
            boolean r3 = r13.useSpanSize
            if (r3 == 0) goto L_0x01c4
            r13.overlayIconAlpha = r2
            boolean r2 = org.telegram.ui.GroupCallActivity.isTabletMode
            r3 = 6
            if (r2 == 0) goto L_0x0125
            org.telegram.ui.Components.voip.GroupCallGridCell r2 = r13.tabletGridView
            if (r2 == 0) goto L_0x0125
            goto L_0x012b
        L_0x0125:
            boolean r2 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r2 == 0) goto L_0x012a
            goto L_0x012b
        L_0x012a:
            r3 = 2
        L_0x012b:
            org.telegram.ui.Components.voip.GroupCallGridCell r2 = r13.tabletGridView
            if (r2 == 0) goto L_0x013c
            int r0 = android.view.View.MeasureSpec.getSize(r14)
            r2 = 1135345664(0x43aCLASSNAME, float:344.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
        L_0x013a:
            float r0 = (float) r0
            goto L_0x015f
        L_0x013c:
            boolean r2 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r2 == 0) goto L_0x0147
            r0 = 1134559232(0x43a00000, float:320.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            goto L_0x013a
        L_0x0147:
            int r2 = android.view.View.MeasureSpec.getSize(r14)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 * 2
            int r2 = r2 - r7
            boolean r7 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r7 == 0) goto L_0x015d
            r0 = 1119092736(0x42b40000, float:90.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = -r0
        L_0x015d:
            int r2 = r2 + r0
            float r0 = (float) r2
        L_0x015f:
            float r2 = r13.spanCount
            float r3 = (float) r3
            float r2 = r2 / r3
            float r2 = r2 * r0
            org.telegram.ui.Components.voip.GroupCallGridCell r3 = r13.tabletGridView
            if (r3 == 0) goto L_0x017a
            float r0 = r3.getItemHeight()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r3 = (float) r3
            float r0 = r0 - r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
        L_0x0177:
            float r3 = (float) r3
            float r2 = r2 - r3
            goto L_0x018e
        L_0x017a:
            boolean r3 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r3 == 0) goto L_0x0180
            float r0 = r0 / r1
            goto L_0x0189
        L_0x0180:
            boolean r3 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r3 == 0) goto L_0x0186
            r3 = 3
            goto L_0x0187
        L_0x0186:
            r3 = 2
        L_0x0187:
            float r3 = (float) r3
            float r0 = r0 / r3
        L_0x0189:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            goto L_0x0177
        L_0x018e:
            android.widget.FrameLayout r3 = r13.infoContainer
            android.view.ViewGroup$LayoutParams r3 = r3.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            android.widget.ImageView r5 = r13.screencastIcon
            int r5 = r5.getVisibility()
            if (r5 != 0) goto L_0x01a8
            r5 = 1105199104(0x41e00000, float:28.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r5 = r2 - r5
            goto L_0x01a9
        L_0x01a8:
            r5 = r2
        L_0x01a9:
            int r7 = (int) r5
            r13.updateSize(r7)
            int r7 = r3.leftMargin
            int r7 = r7 * 2
            float r7 = (float) r7
            float r5 = r5 - r7
            int r5 = (int) r5
            r3.width = r5
            int r2 = (int) r2
            int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r6)
            int r0 = (int) r0
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r6)
            super.onMeasure(r2, r0)
            goto L_0x01c9
        L_0x01c4:
            r13.overlayIconAlpha = r4
            super.onMeasure(r14, r15)
        L_0x01c9:
            int r15 = android.view.View.MeasureSpec.getSize(r15)
            int r14 = android.view.View.MeasureSpec.getSize(r14)
            int r14 = r14 << 16
            int r15 = r15 + r14
            int r14 = r13.lastSize
            if (r14 == r15) goto L_0x01fc
            r13.lastSize = r15
            android.graphics.LinearGradient r14 = new android.graphics.LinearGradient
            r6 = 0
            r7 = 0
            r8 = 0
            r15 = 1123024896(0x42var_, float:120.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r9 = (float) r15
            r10 = 0
            r15 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r0 = 120(0x78, float:1.68E-43)
            int r11 = androidx.core.graphics.ColorUtils.setAlphaComponent(r15, r0)
            android.graphics.Shader$TileMode r12 = android.graphics.Shader.TileMode.CLAMP
            r5 = r14
            r5.<init>(r6, r7, r8, r9, r10, r11, r12)
            r13.gradientShader = r14
            android.graphics.Paint r15 = r13.gradientPaint
            r15.setShader(r14)
        L_0x01fc:
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.nameView
            r14.setPivotX(r4)
            org.telegram.ui.ActionBar.SimpleTextView r14 = r13.nameView
            int r15 = r14.getMeasuredHeight()
            float r15 = (float) r15
            float r15 = r15 / r1
            r14.setPivotY(r15)
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v55, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v61, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v95, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v96, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x0174, code lost:
        if (r1 != r11.videoNotAvailableParticipant) goto L_0x016a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:102:0x017c, code lost:
        if (org.telegram.messenger.ChatObject.Call.videoIsActive(r10, r1.presentation, r11) == false) goto L_0x016a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x01a3, code lost:
        if (r1 != false) goto L_0x01a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x006f, code lost:
        if (r0.participant != r10.videoNotAvailableParticipant) goto L_0x0071;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x0166, code lost:
        if (org.telegram.messenger.voip.VoIPService.getSharedInstance().getVideoState(r0.participant.presentation) == 2) goto L_0x0168;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0183  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x01a9  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x01db  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x01ed  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x0209  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x023e  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0272  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0286  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0288  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x029b  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x02af  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x02c5  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x0368  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0373  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x037b  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x03a2  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x03c0  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x03e2  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x03ec  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0401  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x0426  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x0448  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x047e  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x0481  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x04ca  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x04d4  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0518  */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x053d  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0570  */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x0595  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x05bf  */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x0614  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00c3  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00df  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x010b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateAttachState(boolean r24) {
        /*
            r23 = this;
            r0 = r23
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
            r3 = 2
            r4 = 1056964608(0x3var_, float:0.5)
            r5 = 0
            r6 = 0
            r7 = 1065353216(0x3var_, float:1.0)
            r8 = 1
            r9 = 0
            if (r1 == 0) goto L_0x0115
            boolean r10 = r0.showingInFullscreen
            if (r10 != 0) goto L_0x0115
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 != 0) goto L_0x004c
            r1 = 1
            goto L_0x004d
        L_0x004c:
            r1 = 0
        L_0x004d:
            boolean r10 = org.telegram.ui.GroupCallActivity.paused
            if (r10 != 0) goto L_0x0071
            org.telegram.messenger.ChatObject$VideoParticipant r10 = r0.participant
            if (r10 == 0) goto L_0x0071
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r11 = r0.secondaryView
            if (r11 != 0) goto L_0x0072
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11 = r10.participant
            boolean r10 = r10.presentation
            org.telegram.messenger.ChatObject$Call r12 = r0.call
            boolean r10 = org.telegram.messenger.ChatObject.Call.videoIsActive(r11, r10, r12)
            if (r10 == 0) goto L_0x0071
            org.telegram.messenger.ChatObject$Call r10 = r0.call
            boolean r11 = r10.canStreamVideo
            if (r11 != 0) goto L_0x0072
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r0.participant
            org.telegram.messenger.ChatObject$VideoParticipant r10 = r10.videoNotAvailableParticipant
            if (r11 == r10) goto L_0x0072
        L_0x0071:
            r1 = 1
        L_0x0072:
            if (r1 != 0) goto L_0x0088
            org.telegram.ui.Components.voip.GroupCallGridCell r10 = r0.primaryView
            if (r10 != 0) goto L_0x0290
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r10 = r0.secondaryView
            if (r10 != 0) goto L_0x0290
            org.telegram.ui.Components.voip.GroupCallGridCell r10 = r0.tabletGridView
            if (r10 != 0) goto L_0x0290
            boolean r10 = r0.showingAsScrimView
            if (r10 != 0) goto L_0x0290
            boolean r10 = r0.animateToScrimView
            if (r10 != 0) goto L_0x0290
        L_0x0088:
            r0.attached = r9
            r23.saveThumb()
            org.telegram.ui.Components.voip.VoIPTextureView r10 = r0.textureView
            android.animation.ValueAnimator r10 = r10.currentAnimation
            if (r10 != 0) goto L_0x00bd
            if (r1 == 0) goto L_0x00bd
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r1 = r0.parentContainer
            r1.detach(r0)
            android.view.ViewPropertyAnimator r1 = r23.animate()
            android.view.ViewPropertyAnimator r1 = r1.scaleX(r4)
            android.view.ViewPropertyAnimator r1 = r1.scaleY(r4)
            android.view.ViewPropertyAnimator r1 = r1.alpha(r6)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$4 r4 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$4
            r4.<init>(r0)
            android.view.ViewPropertyAnimator r1 = r1.setListener(r4)
            r10 = 150(0x96, double:7.4E-322)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r10)
            r1.start()
            goto L_0x00d7
        L_0x00bd:
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r1 = r0.parentContainer
            boolean r4 = r1.inLayout
            if (r4 == 0) goto L_0x00cc
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda6 r1 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda6
            r1.<init>(r0, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            goto L_0x00cf
        L_0x00cc:
            r1.removeView(r0)
        L_0x00cf:
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r1 = r0.parentContainer
            r1.detach(r0)
            r23.release()
        L_0x00d7:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r1.participant
            boolean r1 = r1.self
            if (r1 == 0) goto L_0x00f1
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x0104
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r4 = r0.participant
            boolean r4 = r4.presentation
            r1.setLocalSink(r5, r4)
            goto L_0x0104
        L_0x00f1:
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x0104
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r4 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r10 = r4.participant
            boolean r4 = r4.presentation
            r1.removeRemoteSink(r10, r4)
        L_0x0104:
            r23.invalidate()
            android.animation.ValueAnimator r1 = r0.noVideoStubAnimator
            if (r1 == 0) goto L_0x0290
            r1.removeAllListeners()
            android.animation.ValueAnimator r1 = r0.noVideoStubAnimator
            r1.cancel()
            goto L_0x0290
        L_0x0115:
            if (r1 != 0) goto L_0x0290
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 != 0) goto L_0x011e
            return
        L_0x011e:
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r0.primaryView
            if (r1 != 0) goto L_0x012e
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r10 = r0.secondaryView
            if (r10 != 0) goto L_0x012e
            org.telegram.ui.Components.voip.GroupCallGridCell r10 = r0.tabletGridView
            if (r10 != 0) goto L_0x012e
            boolean r10 = r0.showingInFullscreen
            if (r10 == 0) goto L_0x0290
        L_0x012e:
            if (r1 == 0) goto L_0x0137
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.getParticipant()
            r0.participant = r1
            goto L_0x014c
        L_0x0137:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r1 = r0.secondaryView
            if (r1 == 0) goto L_0x0142
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.getVideoParticipant()
            r0.participant = r1
            goto L_0x014c
        L_0x0142:
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r0.tabletGridView
            if (r1 == 0) goto L_0x014c
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.getParticipant()
            r0.participant = r1
        L_0x014c:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r10 = r1.participant
            boolean r11 = r10.self
            if (r11 == 0) goto L_0x016c
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x016a
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r10 = r0.participant
            boolean r10 = r10.presentation
            int r1 = r1.getVideoState(r10)
            if (r1 != r3) goto L_0x016a
        L_0x0168:
            r1 = 1
            goto L_0x017f
        L_0x016a:
            r1 = 0
            goto L_0x017f
        L_0x016c:
            org.telegram.messenger.ChatObject$Call r11 = r0.call
            boolean r12 = r11.canStreamVideo
            if (r12 != 0) goto L_0x0176
            org.telegram.messenger.ChatObject$VideoParticipant r12 = r11.videoNotAvailableParticipant
            if (r1 != r12) goto L_0x016a
        L_0x0176:
            boolean r1 = r1.presentation
            boolean r1 = org.telegram.messenger.ChatObject.Call.videoIsActive(r10, r1, r11)
            if (r1 == 0) goto L_0x016a
            goto L_0x0168
        L_0x017f:
            boolean r10 = r0.showingInFullscreen
            if (r10 != 0) goto L_0x01a5
            org.telegram.messenger.voip.VoIPService r10 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r11.participant
            boolean r11 = r11.presentation
            boolean r10 = r10.isFullscreen(r12, r11)
            if (r10 != 0) goto L_0x0290
            org.telegram.messenger.voip.VoIPService r10 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r11.participant
            boolean r11 = r11.presentation
            boolean r10 = r10.isFullscreen(r12, r11)
            if (r10 != 0) goto L_0x0290
            if (r1 == 0) goto L_0x0290
        L_0x01a5:
            boolean r1 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r1 == 0) goto L_0x01cf
            r1 = 0
        L_0x01aa:
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r10 = r0.attachedRenderers
            int r10 = r10.size()
            if (r1 >= r10) goto L_0x01cf
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r10 = r0.attachedRenderers
            java.lang.Object r10 = r10.get(r1)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r10 = (org.telegram.ui.Components.voip.GroupCallMiniTextureView) r10
            org.telegram.messenger.ChatObject$VideoParticipant r10 = r10.participant
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r0.participant
            boolean r10 = r10.equals(r11)
            if (r10 != 0) goto L_0x01c7
            int r1 = r1 + 1
            goto L_0x01aa
        L_0x01c7:
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            java.lang.String r2 = "try add two same renderers"
            r1.<init>(r2)
            throw r1
        L_0x01cf:
            r0.attached = r8
            org.telegram.ui.GroupCallActivity r1 = r0.activity
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallStatusIcon> r1 = r1.statusIconPool
            int r1 = r1.size()
            if (r1 <= 0) goto L_0x01ed
            org.telegram.ui.GroupCallActivity r1 = r0.activity
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallStatusIcon> r1 = r1.statusIconPool
            int r10 = r1.size()
            int r10 = r10 - r8
            java.lang.Object r1 = r1.remove(r10)
            org.telegram.ui.Components.voip.GroupCallStatusIcon r1 = (org.telegram.ui.Components.voip.GroupCallStatusIcon) r1
            r0.statusIcon = r1
            goto L_0x01f4
        L_0x01ed:
            org.telegram.ui.Components.voip.GroupCallStatusIcon r1 = new org.telegram.ui.Components.voip.GroupCallStatusIcon
            r1.<init>()
            r0.statusIcon = r1
        L_0x01f4:
            org.telegram.ui.Components.voip.GroupCallStatusIcon r1 = r0.statusIcon
            r1.setCallback(r0)
            org.telegram.ui.Components.voip.GroupCallStatusIcon r1 = r0.statusIcon
            org.telegram.ui.Components.RLottieImageView r10 = r0.micIconView
            r1.setImageView(r10)
            r0.updateIconColor(r9)
            android.view.ViewParent r1 = r23.getParent()
            if (r1 != 0) goto L_0x021b
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r1 = r0.parentContainer
            r10 = 51
            r11 = 46
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r11, r10)
            r1.addView(r0, r10)
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r1 = r0.parentContainer
            r1.attach(r0)
        L_0x021b:
            r0.checkScale = r8
            r0.animateEnter = r9
            android.view.ViewPropertyAnimator r1 = r23.animate()
            android.view.ViewPropertyAnimator r1 = r1.setListener(r5)
            r1.cancel()
            org.telegram.ui.Components.voip.VoIPTextureView r1 = r0.textureView
            android.animation.ValueAnimator r1 = r1.currentAnimation
            if (r1 != 0) goto L_0x0272
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r1 = r0.secondaryView
            if (r1 == 0) goto L_0x0272
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r0.primaryView
            if (r1 != 0) goto L_0x0272
            boolean r1 = r23.hasImage()
            if (r1 != 0) goto L_0x0272
            r0.setScaleX(r4)
            r0.setScaleY(r4)
            r0.setAlpha(r6)
            r0.animateEnter = r8
            r23.invalidate()
            android.view.ViewPropertyAnimator r1 = r23.animate()
            android.view.ViewPropertyAnimator r1 = r1.scaleX(r7)
            android.view.ViewPropertyAnimator r1 = r1.scaleY(r7)
            android.view.ViewPropertyAnimator r1 = r1.alpha(r7)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$5 r4 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$5
            r4.<init>()
            android.view.ViewPropertyAnimator r1 = r1.setListener(r4)
            r10 = 100
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r10)
            r1.start()
            r23.invalidate()
            goto L_0x027b
        L_0x0272:
            r0.setScaleY(r7)
            r0.setScaleX(r7)
            r0.setAlpha(r7)
        L_0x027b:
            r23.loadThumb()
            android.widget.ImageView r1 = r0.screencastIcon
            org.telegram.messenger.ChatObject$VideoParticipant r4 = r0.participant
            boolean r4 = r4.presentation
            if (r4 == 0) goto L_0x0288
            r4 = 0
            goto L_0x028a
        L_0x0288:
            r4 = 8
        L_0x028a:
            r1.setVisibility(r4)
            r1 = 0
            r4 = 1
            goto L_0x0293
        L_0x0290:
            r1 = r24
            r4 = 0
        L_0x0293:
            org.telegram.messenger.ChatObject$VideoParticipant r10 = r0.participant
            org.telegram.messenger.ChatObject$Call r11 = r0.call
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r11.videoNotAvailableParticipant
            if (r10 != r11) goto L_0x02af
            org.telegram.ui.ActionBar.SimpleTextView r10 = r0.nameView
            int r10 = r10.getVisibility()
            r11 = 4
            if (r10 == r11) goto L_0x02c1
            org.telegram.ui.ActionBar.SimpleTextView r10 = r0.nameView
            r10.setVisibility(r11)
            org.telegram.ui.Components.RLottieImageView r10 = r0.micIconView
            r10.setVisibility(r11)
            goto L_0x02c1
        L_0x02af:
            org.telegram.ui.ActionBar.SimpleTextView r10 = r0.nameView
            int r10 = r10.getVisibility()
            if (r10 == 0) goto L_0x02c1
            org.telegram.ui.ActionBar.SimpleTextView r10 = r0.nameView
            r10.setVisibility(r9)
            org.telegram.ui.Components.RLottieImageView r10 = r0.micIconView
            r10.setVisibility(r9)
        L_0x02c1:
            boolean r10 = r0.attached
            if (r10 == 0) goto L_0x0677
            boolean r10 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r10 == 0) goto L_0x02d9
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r10 = r0.parentContainer
            boolean r10 = r10.inFullscreenMode
            if (r10 == 0) goto L_0x02d7
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r10 = r0.secondaryView
            if (r10 != 0) goto L_0x02d9
            org.telegram.ui.Components.voip.GroupCallGridCell r10 = r0.primaryView
            if (r10 != 0) goto L_0x02d9
        L_0x02d7:
            r10 = 1
            goto L_0x02da
        L_0x02d9:
            r10 = 0
        L_0x02da:
            boolean r11 = r0.showingInFullscreen
            r12 = -1
            if (r11 == 0) goto L_0x02e6
        L_0x02df:
            r10 = -1
        L_0x02e0:
            r11 = 1065353216(0x3var_, float:1.0)
            r13 = 0
            r14 = 0
            goto L_0x0345
        L_0x02e6:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r11 = r0.secondaryView
            if (r11 == 0) goto L_0x02f6
            org.telegram.ui.Components.voip.GroupCallGridCell r13 = r0.primaryView
            if (r13 != 0) goto L_0x02f6
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r13 = r0.parentContainer
            boolean r13 = r13.inFullscreenMode
            if (r13 != 0) goto L_0x02f6
        L_0x02f4:
            r10 = 0
            goto L_0x02e0
        L_0x02f6:
            boolean r13 = r0.showingAsScrimView
            if (r13 == 0) goto L_0x02fb
            goto L_0x02df
        L_0x02fb:
            r13 = 1117782016(0x42a00000, float:80.0)
            if (r11 == 0) goto L_0x0308
            org.telegram.ui.Components.voip.GroupCallGridCell r14 = r0.primaryView
            if (r14 != 0) goto L_0x0308
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r13)
            goto L_0x02e0
        L_0x0308:
            org.telegram.ui.Components.voip.GroupCallGridCell r14 = r0.tabletGridView
            r15 = 1110966272(0x42380000, float:46.0)
            if (r14 == 0) goto L_0x0325
            if (r10 == 0) goto L_0x0325
            if (r14 == 0) goto L_0x0320
            int r10 = r14.spanCount
            float r10 = (float) r10
            org.telegram.ui.GroupCallTabletGridAdapter r11 = r14.gridAdapter
            int r11 = r11.getItemCount()
            r13 = r11
            r14 = 1
            r11 = r10
            r10 = -1
            goto L_0x0345
        L_0x0320:
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r15)
            goto L_0x02e0
        L_0x0325:
            org.telegram.ui.Components.voip.GroupCallGridCell r10 = r0.primaryView
            if (r10 == 0) goto L_0x032b
            if (r11 == 0) goto L_0x032f
        L_0x032b:
            boolean r11 = r0.isFullscreenMode
            if (r11 != 0) goto L_0x033e
        L_0x032f:
            if (r10 == 0) goto L_0x0339
            int r10 = r10.spanCount
            float r10 = (float) r10
            r11 = r10
            r10 = -1
            r13 = 0
            r14 = 1
            goto L_0x0345
        L_0x0339:
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r15)
            goto L_0x02e0
        L_0x033e:
            if (r10 == 0) goto L_0x02f4
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r13)
            goto L_0x02e0
        L_0x0345:
            android.view.ViewGroup$LayoutParams r15 = r23.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r15 = (android.view.ViewGroup.MarginLayoutParams) r15
            if (r10 == 0) goto L_0x0390
            int r2 = r15.height
            if (r2 != r10) goto L_0x0363
            if (r4 != 0) goto L_0x0363
            boolean r2 = r0.useSpanSize
            if (r2 != r14) goto L_0x0363
            if (r14 == 0) goto L_0x035f
            float r2 = r0.spanCount
            int r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r2 != 0) goto L_0x0363
        L_0x035f:
            int r2 = r0.gridItemsCount
            if (r2 == r13) goto L_0x0390
        L_0x0363:
            r15.height = r10
            if (r14 == 0) goto L_0x0368
            goto L_0x0369
        L_0x0368:
            r12 = r10
        L_0x0369:
            r15.width = r12
            r0.useSpanSize = r14
            r0.spanCount = r11
            r0.checkScale = r8
            if (r1 == 0) goto L_0x037b
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            r2.animateToLayout()
            r0.updateNextLayoutAnimated = r8
            goto L_0x0380
        L_0x037b:
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            r2.requestLayout()
        L_0x0380:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda4 r2 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda4
            r2.<init>(r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r2 = r0.parentContainer
            r2.requestLayout()
            r23.invalidate()
        L_0x0390:
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r4 = r2.participant
            boolean r4 = r4.self
            if (r4 == 0) goto L_0x03c0
            boolean r2 = r2.presentation
            if (r2 != 0) goto L_0x03c0
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r2 == 0) goto L_0x03c0
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            org.telegram.messenger.voip.VoIPService r4 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r4 = r4.isFrontFaceCamera()
            r2.setMirror(r4)
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            r2.setRotateTextureWitchScreen(r8)
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            r2.setUseCameraRotation(r8)
            goto L_0x03d5
        L_0x03c0:
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            r2.setMirror(r9)
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            r2.setRotateTextureWitchScreen(r8)
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            r2.setUseCameraRotation(r9)
        L_0x03d5:
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            r2.updateRotation()
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            boolean r2 = r2.self
            if (r2 == 0) goto L_0x03ec
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            r4 = 720(0x2d0, float:1.009E-42)
            r2.setMaxTextureSize(r4)
            goto L_0x03f3
        L_0x03ec:
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            r2.setMaxTextureSize(r9)
        L_0x03f3:
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r4 = r2.participant
            boolean r2 = r2.presentation
            org.telegram.messenger.ChatObject$Call r10 = r0.call
            boolean r2 = org.telegram.messenger.ChatObject.Call.videoIsActive(r4, r2, r10)
            if (r2 == 0) goto L_0x0411
            org.telegram.messenger.ChatObject$Call r2 = r0.call
            boolean r4 = r2.canStreamVideo
            if (r4 != 0) goto L_0x040e
            org.telegram.messenger.ChatObject$VideoParticipant r4 = r0.participant
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r2.videoNotAvailableParticipant
            if (r4 == r2) goto L_0x040e
            goto L_0x0411
        L_0x040e:
            r2 = 1
            goto L_0x04af
        L_0x0411:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            org.telegram.messenger.ImageReceiver r2 = r2.avatarImageReceiver
            int r4 = r0.currentAccount
            r2.setCurrentAccount(r4)
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer
            int r2 = org.telegram.messenger.MessageObject.getPeerId(r2)
            if (r2 <= 0) goto L_0x0448
            int r4 = r0.currentAccount
            org.telegram.messenger.AccountInstance r4 = org.telegram.messenger.AccountInstance.getInstance(r4)
            org.telegram.messenger.MessagesController r4 = r4.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r2 = r4.getUser(r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r4 = r0.noVideoStubLayout
            org.telegram.ui.Components.AvatarDrawable r4 = r4.avatarDrawable
            r4.setInfo((org.telegram.tgnet.TLRPC$User) r2)
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForUser(r2, r9)
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForUser(r2, r8)
            goto L_0x046a
        L_0x0448:
            int r4 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.AccountInstance r4 = org.telegram.messenger.AccountInstance.getInstance(r4)
            org.telegram.messenger.MessagesController r4 = r4.getMessagesController()
            int r2 = -r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r4.getChat(r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r4 = r0.noVideoStubLayout
            org.telegram.ui.Components.AvatarDrawable r4 = r4.avatarDrawable
            r4.setInfo((org.telegram.tgnet.TLRPC$Chat) r2)
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForChat(r2, r9)
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForChat(r2, r8)
        L_0x046a:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r11 = r0.noVideoStubLayout
            org.telegram.ui.Components.AvatarDrawable r11 = r11.avatarDrawable
            if (r10 == 0) goto L_0x0481
            org.telegram.messenger.ImageLoader r12 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r10 = r10.location
            java.lang.String r13 = "50_50"
            android.graphics.drawable.BitmapDrawable r10 = r12.getImageFromMemory(r10, r5, r13)
            if (r10 == 0) goto L_0x0481
            r19 = r10
            goto L_0x0483
        L_0x0481:
            r19 = r11
        L_0x0483:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r10 = r0.noVideoStubLayout
            org.telegram.messenger.ImageReceiver r10 = r10.avatarImageReceiver
            r18 = 0
            r20 = 0
            r22 = 0
            r16 = r10
            r17 = r4
            r21 = r2
            r16.setImage(r17, r18, r19, r20, r21, r22)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r10 = r0.noVideoStubLayout
            org.telegram.messenger.ImageReceiver r10 = r10.backgroundImageReceiver
            android.graphics.drawable.ColorDrawable r11 = new android.graphics.drawable.ColorDrawable
            java.lang.String r12 = "voipgroup_listViewBackground"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r11.<init>(r12)
            java.lang.String r18 = "50_50_b"
            r16 = r10
            r19 = r11
            r16.setImage(r17, r18, r19, r20, r21, r22)
            r2 = 0
        L_0x04af:
            if (r1 == 0) goto L_0x04bd
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r4 = r0.secondaryView
            if (r4 == 0) goto L_0x04bd
            boolean r4 = r0.showingInFullscreen
            if (r4 != 0) goto L_0x04bd
            if (r2 != 0) goto L_0x04bd
            r4 = 1
            goto L_0x04be
        L_0x04bd:
            r4 = 0
        L_0x04be:
            boolean r10 = r0.hasVideo
            if (r2 == r10) goto L_0x0542
            if (r4 != 0) goto L_0x0542
            r0.hasVideo = r2
            android.animation.ValueAnimator r2 = r0.noVideoStubAnimator
            if (r2 == 0) goto L_0x04d2
            r2.removeAllListeners()
            android.animation.ValueAnimator r2 = r0.noVideoStubAnimator
            r2.cancel()
        L_0x04d2:
            if (r1 == 0) goto L_0x0518
            boolean r2 = r0.hasVideo
            if (r2 != 0) goto L_0x04ea
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            int r2 = r2.getVisibility()
            if (r2 == 0) goto L_0x04ea
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            r2.setVisibility(r9)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            r2.setAlpha(r6)
        L_0x04ea:
            float[] r2 = new float[r3]
            float r3 = r0.progressToNoVideoStub
            r2[r9] = r3
            boolean r3 = r0.hasVideo
            if (r3 == 0) goto L_0x04f6
            r3 = 0
            goto L_0x04f8
        L_0x04f6:
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x04f8:
            r2[r8] = r3
            android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r2)
            r0.noVideoStubAnimator = r2
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda0 r3 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda0
            r3.<init>(r0)
            r2.addUpdateListener(r3)
            android.animation.ValueAnimator r2 = r0.noVideoStubAnimator
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$6 r3 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$6
            r3.<init>()
            r2.addListener(r3)
            android.animation.ValueAnimator r2 = r0.noVideoStubAnimator
            r2.start()
            goto L_0x0539
        L_0x0518:
            boolean r2 = r0.hasVideo
            if (r2 == 0) goto L_0x051e
            r3 = 0
            goto L_0x0520
        L_0x051e:
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x0520:
            r0.progressToNoVideoStub = r3
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r3 = r0.noVideoStubLayout
            if (r2 == 0) goto L_0x0529
            r2 = 8
            goto L_0x052a
        L_0x0529:
            r2 = 0
        L_0x052a:
            r3.setVisibility(r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            float r3 = r0.progressToNoVideoStub
            r2.setAlpha(r3)
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            r2.invalidate()
        L_0x0539:
            boolean r2 = r0.hasVideo
            if (r2 == 0) goto L_0x0542
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            r2.updateMuteButtonState(r9)
        L_0x0542:
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            boolean r2 = r2.self
            if (r2 == 0) goto L_0x055f
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r2 == 0) goto L_0x055f
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.ui.Components.voip.VoIPTextureView r3 = r0.textureView
            org.webrtc.TextureViewRenderer r3 = r3.renderer
            org.telegram.messenger.ChatObject$VideoParticipant r4 = r0.participant
            boolean r4 = r4.presentation
            r2.setLocalSink(r3, r4)
        L_0x055f:
            org.telegram.ui.Components.voip.GroupCallStatusIcon r2 = r0.statusIcon
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            r2.setParticipant(r3, r1)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r1 = r0.noVideoStubLayout
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0575
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r1 = r0.noVideoStubLayout
            r1.updateMuteButtonState(r8)
        L_0x0575:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.participant
            boolean r2 = r1.presentation
            if (r2 == 0) goto L_0x0586
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r1.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r1 = r1.presentation
            if (r1 == 0) goto L_0x0591
            boolean r1 = r1.paused
            if (r1 == 0) goto L_0x0591
            goto L_0x0590
        L_0x0586:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r1.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r1 = r1.video
            if (r1 == 0) goto L_0x0591
            boolean r1 = r1.paused
            if (r1 == 0) goto L_0x0591
        L_0x0590:
            r9 = 1
        L_0x0591:
            boolean r1 = r0.videoIsPaused
            if (r1 == r9) goto L_0x05b6
            r0.videoIsPaused = r9
            org.telegram.ui.Components.voip.VoIPTextureView r1 = r0.textureView
            org.webrtc.TextureViewRenderer r1 = r1.renderer
            android.view.ViewPropertyAnimator r1 = r1.animate()
            boolean r2 = r0.videoIsPaused
            if (r2 == 0) goto L_0x05a4
            r7 = 0
        L_0x05a4:
            android.view.ViewPropertyAnimator r1 = r1.alpha(r7)
            r2 = 250(0xfa, double:1.235E-321)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r2)
            r1.start()
            org.telegram.ui.Components.voip.VoIPTextureView r1 = r0.textureView
            r1.invalidate()
        L_0x05b6:
            boolean r1 = org.telegram.ui.GroupCallActivity.paused
            if (r1 != 0) goto L_0x0614
            boolean r1 = r0.hasVideo
            if (r1 != 0) goto L_0x05bf
            goto L_0x0614
        L_0x05bf:
            org.telegram.ui.Components.voip.VoIPTextureView r1 = r0.textureView
            org.webrtc.TextureViewRenderer r1 = r1.renderer
            boolean r1 = r1.isFirstFrameRendered()
            if (r1 != 0) goto L_0x05cc
            r23.loadThumb()
        L_0x05cc:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r1.participant
            boolean r1 = r1.self
            if (r1 == 0) goto L_0x05eb
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x0674
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            org.webrtc.TextureViewRenderer r2 = r2.renderer
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            boolean r3 = r3.presentation
            r1.setLocalSink(r2, r3)
            goto L_0x0674
        L_0x05eb:
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x0674
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r2.participant
            boolean r2 = r2.presentation
            org.telegram.ui.Components.voip.VoIPTextureView r4 = r0.textureView
            org.webrtc.TextureViewRenderer r4 = r4.renderer
            r1.addRemoteSink(r3, r2, r4, r5)
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r2.participant
            boolean r2 = r2.presentation
            org.telegram.ui.Components.voip.VoIPTextureView r4 = r0.textureView
            org.webrtc.TextureViewRenderer r4 = r4.renderer
            r1.addRemoteSink(r3, r2, r4, r5)
            goto L_0x0674
        L_0x0614:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r1.participant
            boolean r1 = r1.self
            if (r1 == 0) goto L_0x062e
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x064e
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            boolean r2 = r2.presentation
            r1.setLocalSink(r5, r2)
            goto L_0x064e
        L_0x062e:
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x064e
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r2.participant
            boolean r2 = r2.presentation
            r1.removeRemoteSink(r3, r2)
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r2.participant
            boolean r2 = r2.presentation
            r1.removeRemoteSink(r3, r2)
        L_0x064e:
            boolean r1 = org.telegram.ui.GroupCallActivity.paused
            if (r1 == 0) goto L_0x0674
            org.telegram.ui.Components.voip.VoIPTextureView r1 = r0.textureView
            org.webrtc.TextureViewRenderer r1 = r1.renderer
            boolean r1 = r1.isFirstFrameRendered()
            if (r1 == 0) goto L_0x0674
            r23.saveThumb()
            org.telegram.ui.Components.voip.VoIPTextureView r1 = r0.textureView
            org.webrtc.TextureViewRenderer r1 = r1.renderer
            r1.clearFirstFrame()
            org.telegram.ui.Components.voip.VoIPTextureView r1 = r0.textureView
            org.webrtc.TextureViewRenderer r1 = r1.renderer
            r1.setAlpha(r6)
            org.telegram.ui.Components.voip.VoIPTextureView r1 = r0.textureView
            android.view.TextureView r1 = r1.blurRenderer
            r1.setAlpha(r6)
        L_0x0674:
            r0.updateIconColor(r8)
        L_0x0677:
            r23.updateInfo()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallMiniTextureView.updateAttachState(boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateAttachState$1(View view) {
        this.parentContainer.removeView(view);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateAttachState$2(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.progressToNoVideoStub = floatValue;
        this.noVideoStubLayout.setAlpha(floatValue);
        this.textureView.invalidate();
    }

    private void loadThumb() {
        if (this.thumb == null) {
            HashMap<String, Bitmap> hashMap = this.call.thumbs;
            ChatObject.VideoParticipant videoParticipant = this.participant;
            boolean z = videoParticipant.presentation;
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = videoParticipant.participant;
            Bitmap bitmap = hashMap.get(z ? tLRPC$TL_groupCallParticipant.presentationEndpoint : tLRPC$TL_groupCallParticipant.videoEndpoint);
            this.thumb = bitmap;
            this.textureView.setThumb(bitmap);
            if (this.thumb == null) {
                int peerId = MessageObject.getPeerId(this.participant.participant.peer);
                ChatObject.VideoParticipant videoParticipant2 = this.participant;
                if (videoParticipant2.participant.self && videoParticipant2.presentation) {
                    this.imageReceiver.setImageBitmap((Drawable) new MotionBackgroundDrawable(-14602694, -13935795, -14395293, -14203560, true));
                } else if (peerId > 0) {
                    TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(peerId));
                    ImageLocation forUser = ImageLocation.getForUser(user, 1);
                    int colorForId = user != null ? AvatarDrawable.getColorForId(user.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
                    this.imageReceiver.setImage(forUser, "50_50_b", new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(colorForId, -16777216, 0.2f), ColorUtils.blendARGB(colorForId, -16777216, 0.4f)}), (String) null, user, 0);
                } else {
                    TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-peerId));
                    ImageLocation forChat = ImageLocation.getForChat(chat, 1);
                    int colorForId2 = chat != null ? AvatarDrawable.getColorForId(chat.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
                    this.imageReceiver.setImage(forChat, "50_50_b", new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(colorForId2, -16777216, 0.2f), ColorUtils.blendARGB(colorForId2, -16777216, 0.4f)}), (String) null, chat, 0);
                }
            }
        }
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

    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00c4, code lost:
        if (r0 != false) goto L_0x00c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00cb, code lost:
        if (r2 != null) goto L_0x00c6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updatePosition(android.view.ViewGroup r9, android.view.ViewGroup r10, org.telegram.ui.Components.RecyclerListView r11, org.telegram.ui.Components.voip.GroupCallRenderersContainer r12) {
        /*
            r8 = this;
            boolean r0 = r8.showingAsScrimView
            if (r0 != 0) goto L_0x01bc
            boolean r0 = r8.animateToScrimView
            if (r0 != 0) goto L_0x01bc
            boolean r0 = r8.forceDetached
            if (r0 == 0) goto L_0x000e
            goto L_0x01bc
        L_0x000e:
            r0 = 0
            r8.drawFirst = r0
            float r1 = r12.progressToFullscreenMode
            boolean r2 = r8.animateToFullscreen
            r3 = 1073741824(0x40000000, float:2.0)
            r4 = 1090519040(0x41000000, float:8.0)
            r5 = 1065353216(0x3var_, float:1.0)
            if (r2 != 0) goto L_0x013b
            boolean r2 = r8.showingInFullscreen
            if (r2 == 0) goto L_0x0023
            goto L_0x013b
        L_0x0023:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r2 = r8.secondaryView
            if (r2 == 0) goto L_0x00a7
            boolean r9 = r2.isRemoving(r11)
            if (r9 == 0) goto L_0x0037
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r9 = r8.secondaryView
            float r9 = r9.getAlpha()
            r8.setAlpha(r9)
            goto L_0x005e
        L_0x0037:
            org.telegram.ui.Components.voip.GroupCallGridCell r9 = r8.primaryView
            if (r9 != 0) goto L_0x004e
            boolean r9 = r8.attached
            if (r9 == 0) goto L_0x0046
            boolean r9 = r8.animateEnter
            if (r9 != 0) goto L_0x0046
            r8.setAlpha(r1)
        L_0x0046:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r9 = r8.secondaryView
            r9.setAlpha(r1)
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x005e
        L_0x004e:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r9 = r8.secondaryView
            r9.setAlpha(r5)
            boolean r9 = r8.attached
            if (r9 == 0) goto L_0x005e
            boolean r9 = r8.animateEnter
            if (r9 != 0) goto L_0x005e
            r8.setAlpha(r5)
        L_0x005e:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r9 = r8.secondaryView
            float r9 = r9.getX()
            float r10 = r11.getX()
            float r9 = r9 + r10
            int r10 = r8.getLeft()
            float r10 = (float) r10
            float r9 = r9 - r10
            r8.setTranslationX(r9)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r9 = (float) r9
            float r5 = r5 - r1
            float r9 = r9 * r5
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r10 = r8.secondaryView
            float r10 = r10.getY()
            float r9 = r9 + r10
            float r10 = r11.getY()
            float r9 = r9 + r10
            int r10 = r8.getTop()
            float r10 = (float) r10
            float r9 = r9 - r10
            r8.setTranslationY(r9)
            org.telegram.ui.Components.voip.VoIPTextureView r9 = r8.textureView
            r10 = 1095761920(0x41500000, float:13.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r10 = r10 * r1
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r11 = (float) r11
            float r11 = r11 * r5
            float r10 = r10 + r11
            r9.setRoundCorners(r10)
            goto L_0x01bc
        L_0x00a7:
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r8.primaryView
            if (r11 != 0) goto L_0x00af
            org.telegram.ui.Components.voip.GroupCallGridCell r2 = r8.tabletGridView
            if (r2 == 0) goto L_0x01bc
        L_0x00af:
            org.telegram.ui.Components.voip.GroupCallGridCell r2 = r8.tabletGridView
            r6 = 1
            if (r2 == 0) goto L_0x00c8
            if (r11 == 0) goto L_0x00c8
            boolean r7 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r7 == 0) goto L_0x00c1
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r7 = r8.parentContainer
            boolean r7 = r7.inFullscreenMode
            if (r7 != 0) goto L_0x00c1
            r0 = 1
        L_0x00c1:
            if (r0 == 0) goto L_0x00c4
            r11 = r2
        L_0x00c4:
            if (r0 == 0) goto L_0x00ce
        L_0x00c6:
            r9 = r10
            goto L_0x00ce
        L_0x00c8:
            if (r2 == 0) goto L_0x00cb
            r11 = r2
        L_0x00cb:
            if (r2 == 0) goto L_0x00ce
            goto L_0x00c6
        L_0x00ce:
            float r10 = r11.getX()
            float r0 = r9.getX()
            float r10 = r10 + r0
            int r0 = r8.getLeft()
            float r0 = (float) r0
            float r10 = r10 - r0
            int r0 = r12.getLeft()
            float r0 = (float) r0
            float r10 = r10 - r0
            r8.setTranslationX(r10)
            float r10 = r11.getY()
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r0 = (float) r0
            float r10 = r10 + r0
            float r9 = r9.getY()
            float r10 = r10 + r9
            int r9 = r8.getTop()
            float r9 = (float) r9
            float r10 = r10 - r9
            int r9 = r12.getTop()
            float r9 = (float) r9
            float r10 = r10 - r9
            r8.setTranslationY(r10)
            org.telegram.ui.Components.voip.VoIPTextureView r9 = r8.textureView
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r10 = (float) r10
            r9.setRoundCorners(r10)
            boolean r9 = r8.attached
            if (r9 == 0) goto L_0x01bc
            boolean r9 = r8.animateEnter
            if (r9 != 0) goto L_0x01bc
            boolean r9 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r9 != 0) goto L_0x0128
            r8.drawFirst = r6
            float r5 = r5 - r1
            float r9 = r11.getAlpha()
            float r5 = r5 * r9
            r8.setAlpha(r5)
            goto L_0x01bc
        L_0x0128:
            org.telegram.ui.Components.voip.GroupCallGridCell r9 = r8.primaryView
            if (r9 == 0) goto L_0x01bc
            org.telegram.ui.Components.voip.GroupCallGridCell r9 = r8.tabletGridView
            if (r9 != 0) goto L_0x01bc
            float r9 = r11.getAlpha()
            float r1 = r1 * r9
            r8.setAlpha(r1)
            goto L_0x01bc
        L_0x013b:
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r8.primaryView
            r0 = 0
            if (r11 != 0) goto L_0x014c
            org.telegram.ui.Components.voip.GroupCallGridCell r2 = r8.tabletGridView
            if (r2 == 0) goto L_0x0145
            goto L_0x014c
        L_0x0145:
            r8.setTranslationX(r0)
            r8.setTranslationY(r0)
            goto L_0x0194
        L_0x014c:
            org.telegram.ui.Components.voip.GroupCallGridCell r2 = r8.tabletGridView
            if (r2 == 0) goto L_0x0151
            r11 = r2
        L_0x0151:
            if (r2 == 0) goto L_0x0154
            r9 = r10
        L_0x0154:
            float r10 = r11.getX()
            float r2 = r9.getX()
            float r10 = r10 + r2
            int r2 = r8.getLeft()
            float r2 = (float) r2
            float r10 = r10 - r2
            int r2 = r12.getLeft()
            float r2 = (float) r2
            float r10 = r10 - r2
            float r11 = r11.getY()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r2 = (float) r2
            float r11 = r11 + r2
            float r9 = r9.getY()
            float r11 = r11 + r9
            int r9 = r8.getTop()
            float r9 = (float) r9
            float r11 = r11 - r9
            int r9 = r12.getTop()
            float r9 = (float) r9
            float r11 = r11 - r9
            float r9 = r5 - r1
            float r10 = r10 * r9
            float r0 = r0 * r1
            float r10 = r10 + r0
            r8.setTranslationX(r10)
            float r11 = r11 * r9
            float r11 = r11 + r0
            r8.setTranslationY(r11)
        L_0x0194:
            org.telegram.ui.Components.voip.VoIPTextureView r9 = r8.textureView
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r10 = (float) r10
            r9.setRoundCorners(r10)
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r9 = r8.secondaryView
            if (r9 == 0) goto L_0x01a5
            r9.setAlpha(r1)
        L_0x01a5:
            boolean r9 = r8.showingInFullscreen
            if (r9 != 0) goto L_0x01b5
            org.telegram.ui.Components.voip.GroupCallGridCell r9 = r8.primaryView
            if (r9 != 0) goto L_0x01b5
            org.telegram.ui.Components.voip.GroupCallGridCell r9 = r8.tabletGridView
            if (r9 != 0) goto L_0x01b5
            r8.setAlpha(r1)
            goto L_0x01bc
        L_0x01b5:
            boolean r9 = r8.animateEnter
            if (r9 != 0) goto L_0x01bc
            r8.setAlpha(r5)
        L_0x01bc:
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
            getRenderBufferBitmap(new GroupCallMiniTextureView$$ExternalSyntheticLambda7(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveThumb$4(Bitmap bitmap, int i) {
        if (bitmap != null && bitmap.getPixel(0, 0) != 0) {
            Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(bitmap.getWidth(), bitmap.getHeight()) / 180));
            AndroidUtilities.runOnUIThread(new GroupCallMiniTextureView$$ExternalSyntheticLambda5(this, bitmap));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveThumb$3(Bitmap bitmap) {
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

    /* JADX WARNING: Removed duplicated region for block: B:13:0x002d A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x002e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateIconColor(boolean r10) {
        /*
            r9 = this;
            org.telegram.ui.Components.voip.GroupCallStatusIcon r0 = r9.statusIcon
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            boolean r0 = r0.isMutedByMe()
            if (r0 == 0) goto L_0x0013
            java.lang.String r0 = "voipgroup_mutedByAdminIcon"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
        L_0x0011:
            r1 = r0
            goto L_0x0029
        L_0x0013:
            org.telegram.ui.Components.voip.GroupCallStatusIcon r0 = r9.statusIcon
            boolean r0 = r0.isSpeaking()
            java.lang.String r1 = "voipgroup_speakingText"
            if (r0 == 0) goto L_0x0022
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            goto L_0x0011
        L_0x0022:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r1 = -1
            r1 = r0
            r0 = -1
        L_0x0029:
            int r2 = r9.animateToColor
            if (r2 != r0) goto L_0x002e
            return
        L_0x002e:
            android.animation.ValueAnimator r2 = r9.colorAnimator
            if (r2 == 0) goto L_0x003a
            r2.removeAllListeners()
            android.animation.ValueAnimator r2 = r9.colorAnimator
            r2.cancel()
        L_0x003a:
            if (r10 != 0) goto L_0x0044
            android.graphics.Paint r10 = r9.speakingPaint
            r9.lastSpeakingFrameColor = r1
            r10.setColor(r1)
            goto L_0x0071
        L_0x0044:
            int r4 = r9.lastIconColor
            int r6 = r9.lastSpeakingFrameColor
            r9.animateToColor = r0
            r10 = 2
            float[] r10 = new float[r10]
            r10 = {0, NUM} // fill-array
            android.animation.ValueAnimator r10 = android.animation.ValueAnimator.ofFloat(r10)
            r9.colorAnimator = r10
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda2 r8 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda2
            r2 = r8
            r3 = r9
            r5 = r0
            r7 = r1
            r2.<init>(r3, r4, r5, r6, r7)
            r10.addUpdateListener(r8)
            android.animation.ValueAnimator r10 = r9.colorAnimator
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$7 r2 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$7
            r2.<init>(r0, r1)
            r10.addListener(r2)
            android.animation.ValueAnimator r10 = r9.colorAnimator
            r10.start()
        L_0x0071:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallMiniTextureView.updateIconColor(boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateIconColor$5(int i, int i2, int i3, int i4, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.lastIconColor = ColorUtils.blendARGB(i, i2, floatValue);
        int blendARGB = ColorUtils.blendARGB(i3, i4, floatValue);
        this.lastSpeakingFrameColor = blendARGB;
        this.speakingPaint.setColor(blendARGB);
        if (this.progressToSpeaking > 0.0f) {
            invalidate();
        }
    }

    public void runDelayedAnimations() {
        for (int i = 0; i < this.onFirstFrameRunnables.size(); i++) {
            this.onFirstFrameRunnables.get(i).run();
        }
        this.onFirstFrameRunnables.clear();
    }

    public void updateSize(int i) {
        int measuredWidth = this.parentContainer.getMeasuredWidth() - AndroidUtilities.dp(6.0f);
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

    public void startFlipAnimation() {
        if (this.flipAnimator == null) {
            this.flipHalfReached = false;
            ImageView imageView = this.blurredFlippingStub;
            if (imageView == null) {
                this.blurredFlippingStub = new ImageView(getContext());
            } else {
                imageView.animate().cancel();
            }
            if (this.textureView.renderer.isFirstFrameRendered()) {
                Bitmap bitmap = this.textureView.blurRenderer.getBitmap(100, 100);
                if (bitmap != null) {
                    Utilities.blurBitmap(bitmap, 3, 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                    this.blurredFlippingStub.setBackground(new BitmapDrawable(bitmap));
                }
                this.blurredFlippingStub.setAlpha(0.0f);
            } else {
                this.blurredFlippingStub.setAlpha(1.0f);
            }
            if (this.blurredFlippingStub.getParent() == null) {
                this.textureView.addView(this.blurredFlippingStub);
            }
            ((FrameLayout.LayoutParams) this.blurredFlippingStub.getLayoutParams()).gravity = 17;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.flipAnimator = ofFloat;
            ofFloat.addUpdateListener(new GroupCallMiniTextureView$$ExternalSyntheticLambda1(this));
            this.flipAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                    groupCallMiniTextureView.flipAnimator = null;
                    groupCallMiniTextureView.textureView.setRotationY(0.0f);
                    GroupCallMiniTextureView groupCallMiniTextureView2 = GroupCallMiniTextureView.this;
                    if (!groupCallMiniTextureView2.flipHalfReached) {
                        groupCallMiniTextureView2.textureView.renderer.clearImage();
                    }
                }
            });
            this.flipAnimator.setDuration(400);
            this.flipAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.flipAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startFlipAnimation$6(ValueAnimator valueAnimator) {
        boolean z;
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        if (floatValue < 0.5f) {
            z = false;
        } else {
            floatValue -= 1.0f;
            z = true;
        }
        if (z && !this.flipHalfReached) {
            this.blurredFlippingStub.setAlpha(1.0f);
            this.flipHalfReached = true;
            this.textureView.renderer.clearImage();
        }
        float f = floatValue * 180.0f;
        this.blurredFlippingStub.setRotationY(f);
        this.textureView.renderer.setRotationY(f);
    }
}
