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
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
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
@SuppressLint({"ViewConstructor"})
/* loaded from: classes3.dex */
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
    private Drawable castingScreenDrawable;
    private boolean checkScale;
    int collapseSize;
    ValueAnimator colorAnimator;
    int currentAccount;
    public boolean drawFirst;
    ValueAnimator flipAnimator;
    boolean flipHalfReached;
    public boolean forceDetached;
    int fullSize;
    Paint gradientPaint;
    LinearGradient gradientShader;
    int gridItemsCount;
    public boolean hasVideo;
    ImageReceiver imageReceiver;
    boolean inPinchToZoom;
    FrameLayout infoContainer;
    private boolean invalidateFromChild;
    boolean isFullscreenMode;
    int lastIconColor;
    private boolean lastLandscapeMode;
    private int lastSize;
    int lastSpeakingFrameColor;
    private final RLottieImageView micIconView;
    private final SimpleTextView nameView;
    private Runnable noRtmpStreamCallback;
    private TextView noRtmpStreamTextView;
    ValueAnimator noVideoStubAnimator;
    private NoVideoStubLayout noVideoStubLayout;
    ArrayList<Runnable> onFirstFrameRunnables;
    float overlayIconAlpha;
    GroupCallRenderersContainer parentContainer;
    public ChatObject.VideoParticipant participant;
    private CrossOutDrawable pausedVideoDrawable;
    float pinchCenterX;
    float pinchCenterY;
    float pinchScale;
    float pinchTranslationX;
    float pinchTranslationY;
    private boolean postedNoRtmpStreamCallback;
    public GroupCallGridCell primaryView;
    private float progressToBackground;
    public float progressToNoVideoStub;
    float progressToSpeaking;
    private Rect rect;
    private final ImageView screencastIcon;
    public GroupCallFullscreenAdapter.GroupCallUserCell secondaryView;
    private boolean showingAsScrimView;
    public boolean showingInFullscreen;
    float spanCount;
    Paint speakingPaint;
    private GroupCallStatusIcon statusIcon;
    private TextView stopSharingTextView;
    private boolean swipeToBack;
    private float swipeToBackDy;
    public GroupCallGridCell tabletGridView;
    public VoIPTextureView textureView;
    Bitmap thumb;
    Paint thumbPaint;
    private boolean updateNextLayoutAnimated;
    boolean useSpanSize;
    private boolean videoIsPaused;
    private float videoIsPausedProgress;

    static /* synthetic */ float access$116(GroupCallMiniTextureView groupCallMiniTextureView, float f) {
        float f2 = groupCallMiniTextureView.progressToBackground + f;
        groupCallMiniTextureView.progressToBackground = f2;
        return f2;
    }

    static /* synthetic */ float access$716(GroupCallMiniTextureView groupCallMiniTextureView, float f) {
        float f2 = groupCallMiniTextureView.videoIsPausedProgress + f;
        groupCallMiniTextureView.videoIsPausedProgress = f2;
        return f2;
    }

    static /* synthetic */ float access$724(GroupCallMiniTextureView groupCallMiniTextureView, float f) {
        float f2 = groupCallMiniTextureView.videoIsPausedProgress - f;
        groupCallMiniTextureView.videoIsPausedProgress = f2;
        return f2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (this.textureView.renderer.isFirstFrameRendered()) {
            return;
        }
        this.textureView.animate().cancel();
        this.textureView.animate().alpha(0.0f).setDuration(150L).start();
        this.noRtmpStreamTextView.animate().cancel();
        this.noRtmpStreamTextView.animate().alpha(1.0f).setDuration(150L).start();
    }

    public GroupCallMiniTextureView(final GroupCallRenderersContainer groupCallRenderersContainer, ArrayList<GroupCallMiniTextureView> arrayList, final ChatObject.Call call, final GroupCallActivity groupCallActivity) {
        super(groupCallRenderersContainer.getContext());
        this.gradientPaint = new Paint(1);
        this.speakingPaint = new Paint(1);
        this.progressToNoVideoStub = 1.0f;
        this.imageReceiver = new ImageReceiver();
        this.onFirstFrameRunnables = new ArrayList<>();
        this.noRtmpStreamCallback = new Runnable() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                GroupCallMiniTextureView.this.lambda$new$0();
            }
        };
        this.rect = new Rect();
        this.call = call;
        this.currentAccount = groupCallActivity.getCurrentAccount();
        CrossOutDrawable crossOutDrawable = new CrossOutDrawable(groupCallRenderersContainer.getContext(), R.drawable.calls_video, null);
        this.pausedVideoDrawable = crossOutDrawable;
        crossOutDrawable.setCrossOut(true, false);
        this.pausedVideoDrawable.setOffsets(-AndroidUtilities.dp(4.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f));
        this.pausedVideoDrawable.setStrokeWidth(AndroidUtilities.dpf2(3.4f));
        this.castingScreenDrawable = groupCallRenderersContainer.getContext().getResources().getDrawable(R.drawable.screencast_big).mutate();
        final TextPaint textPaint = new TextPaint(1);
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textPaint.setTextSize(AndroidUtilities.dp(13.0f));
        textPaint.setColor(-1);
        final TextPaint textPaint2 = new TextPaint(1);
        textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textPaint2.setTextSize(AndroidUtilities.dp(15.0f));
        textPaint2.setColor(-1);
        final String string = LocaleController.getString("VoipVideoOnPause", R.string.VoipVideoOnPause);
        final StaticLayout staticLayout = new StaticLayout(LocaleController.getString("VoipVideoScreenSharingTwoLines", R.string.VoipVideoScreenSharingTwoLines), textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(call.chatId));
        final StaticLayout staticLayout2 = new StaticLayout(LocaleController.formatString("VoipVideoNotAvailable", R.string.VoipVideoNotAvailable, LocaleController.formatPluralString("Participants", MessagesController.getInstance(this.currentAccount).groupCallVideoMaxParticipants, new Object[0])), textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        final String string2 = LocaleController.getString("VoipVideoScreenSharing", R.string.VoipVideoScreenSharing);
        final float measureText = textPaint.measureText(string);
        final float measureText2 = textPaint2.measureText(string2);
        VoIPTextureView voIPTextureView = new VoIPTextureView(groupCallRenderersContainer.getContext(), false, false, true, true) { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView.1
            float overlayIconAlphaFrom;

            @Override // org.telegram.ui.Components.voip.VoIPTextureView
            public void animateToLayout() {
                super.animateToLayout();
                this.overlayIconAlphaFrom = GroupCallMiniTextureView.this.overlayIconAlpha;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.voip.VoIPTextureView
            public void updateRendererSize() {
                super.updateRendererSize();
                ImageView imageView = GroupCallMiniTextureView.this.blurredFlippingStub;
                if (imageView == null || imageView.getParent() == null) {
                    return;
                }
                GroupCallMiniTextureView.this.blurredFlippingStub.getLayoutParams().width = GroupCallMiniTextureView.this.textureView.renderer.getMeasuredWidth();
                GroupCallMiniTextureView.this.blurredFlippingStub.getLayoutParams().height = GroupCallMiniTextureView.this.textureView.renderer.getMeasuredHeight();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.voip.VoIPTextureView, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                float f;
                float dp;
                float f2;
                int i;
                float f3;
                if (!this.renderer.isFirstFrameRendered() || ((this.renderer.getAlpha() != 1.0f && this.blurRenderer.getAlpha() != 1.0f) || GroupCallMiniTextureView.this.videoIsPaused)) {
                    if (GroupCallMiniTextureView.this.progressToBackground != 1.0f) {
                        GroupCallMiniTextureView.access$116(GroupCallMiniTextureView.this, 0.10666667f);
                        if (GroupCallMiniTextureView.this.progressToBackground > 1.0f) {
                            GroupCallMiniTextureView.this.progressToBackground = 1.0f;
                        } else {
                            invalidate();
                        }
                    }
                    GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                    if (groupCallMiniTextureView.thumb != null) {
                        canvas.save();
                        float f4 = this.currentThumbScale;
                        canvas.scale(f4, f4, getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f);
                        GroupCallMiniTextureView groupCallMiniTextureView2 = GroupCallMiniTextureView.this;
                        if (groupCallMiniTextureView2.thumbPaint == null) {
                            groupCallMiniTextureView2.thumbPaint = new Paint(1);
                            GroupCallMiniTextureView.this.thumbPaint.setFilterBitmap(true);
                        }
                        canvas.drawBitmap(GroupCallMiniTextureView.this.thumb, (getMeasuredWidth() - GroupCallMiniTextureView.this.thumb.getWidth()) / 2.0f, (getMeasuredHeight() - GroupCallMiniTextureView.this.thumb.getHeight()) / 2.0f, GroupCallMiniTextureView.this.thumbPaint);
                        canvas.restore();
                    } else {
                        groupCallMiniTextureView.imageReceiver.setImageCoords(this.currentClipHorizontal, this.currentClipVertical, getMeasuredWidth() - (this.currentClipHorizontal * 2.0f), getMeasuredHeight() - (this.currentClipVertical * 2.0f));
                        GroupCallMiniTextureView groupCallMiniTextureView3 = GroupCallMiniTextureView.this;
                        groupCallMiniTextureView3.imageReceiver.setAlpha(groupCallMiniTextureView3.progressToBackground);
                        GroupCallMiniTextureView.this.imageReceiver.draw(canvas);
                    }
                    GroupCallMiniTextureView groupCallMiniTextureView4 = GroupCallMiniTextureView.this;
                    ChatObject.VideoParticipant videoParticipant = groupCallMiniTextureView4.participant;
                    if (videoParticipant == call.videoNotAvailableParticipant) {
                        if (groupCallMiniTextureView4.showingInFullscreen || !groupCallRenderersContainer.inFullscreenMode) {
                            float dp2 = AndroidUtilities.dp(48.0f);
                            textPaint.setAlpha(255);
                            canvas.save();
                            canvas.translate((((getMeasuredWidth() - dp2) / 2.0f) - (AndroidUtilities.dp(400.0f) / 2.0f)) + (dp2 / 2.0f), ((getMeasuredHeight() / 2) - dp2) + dp2 + AndroidUtilities.dp(10.0f));
                            staticLayout2.draw(canvas);
                            canvas.restore();
                        }
                        if (GroupCallMiniTextureView.this.stopSharingTextView.getVisibility() != 4) {
                            GroupCallMiniTextureView.this.stopSharingTextView.setVisibility(4);
                        }
                    } else if (!videoParticipant.presentation || !videoParticipant.participant.self) {
                        if (groupCallMiniTextureView4.stopSharingTextView.getVisibility() != 4) {
                            GroupCallMiniTextureView.this.stopSharingTextView.setVisibility(4);
                        }
                        groupCallActivity.cellFlickerDrawable.draw(canvas, GroupCallMiniTextureView.this);
                    } else {
                        if (groupCallMiniTextureView4.stopSharingTextView.getVisibility() != 0) {
                            GroupCallMiniTextureView.this.stopSharingTextView.setVisibility(0);
                            GroupCallMiniTextureView.this.stopSharingTextView.setScaleX(1.0f);
                            GroupCallMiniTextureView.this.stopSharingTextView.setScaleY(1.0f);
                        }
                        float f5 = GroupCallMiniTextureView.this.drawFirst ? 0.0f : groupCallRenderersContainer.progressToFullscreenMode;
                        int dp3 = AndroidUtilities.dp(33.0f);
                        GroupCallMiniTextureView groupCallMiniTextureView5 = GroupCallMiniTextureView.this;
                        if (groupCallMiniTextureView5.animateToFullscreen || groupCallMiniTextureView5.showingInFullscreen) {
                            f = dp3;
                            dp = AndroidUtilities.dp(10.0f) + (AndroidUtilities.dp(39.0f) * groupCallRenderersContainer.progressToFullscreenMode);
                        } else {
                            f = dp3;
                            dp = AndroidUtilities.dp(10.0f) * Math.max(1.0f - groupCallRenderersContainer.progressToFullscreenMode, (GroupCallMiniTextureView.this.showingAsScrimView || GroupCallMiniTextureView.this.animateToScrimView) ? groupCallRenderersContainer.progressToScrimView : 0.0f);
                        }
                        int i2 = (int) (f + dp);
                        int measuredWidth = (getMeasuredWidth() - i2) / 2;
                        float f6 = (GroupCallMiniTextureView.this.showingAsScrimView || GroupCallMiniTextureView.this.animateToScrimView) ? groupCallRenderersContainer.progressToScrimView : 0.0f;
                        GroupCallMiniTextureView groupCallMiniTextureView6 = GroupCallMiniTextureView.this;
                        if (groupCallMiniTextureView6.showingInFullscreen) {
                            f2 = f5;
                        } else {
                            f5 = groupCallMiniTextureView6.animateToFullscreen ? groupCallRenderersContainer.progressToFullscreenMode : f6;
                            f2 = (groupCallMiniTextureView6.showingAsScrimView || GroupCallMiniTextureView.this.animateToScrimView) ? groupCallRenderersContainer.progressToScrimView : groupCallRenderersContainer.progressToFullscreenMode;
                        }
                        float measuredHeight = ((getMeasuredHeight() - i2) / 2) - AndroidUtilities.dp(28.0f);
                        float dp4 = AndroidUtilities.dp(17.0f);
                        float dp5 = AndroidUtilities.dp(74.0f);
                        GroupCallMiniTextureView groupCallMiniTextureView7 = GroupCallMiniTextureView.this;
                        int dp6 = (int) ((measuredHeight - ((dp4 + (dp5 * ((groupCallMiniTextureView7.showingInFullscreen || groupCallMiniTextureView7.animateToFullscreen) ? groupCallRenderersContainer.progressToFullscreenMode : 0.0f))) * f5)) + (AndroidUtilities.dp(17.0f) * f2));
                        GroupCallMiniTextureView.this.castingScreenDrawable.setBounds(measuredWidth, dp6, measuredWidth + i2, dp6 + i2);
                        GroupCallMiniTextureView.this.castingScreenDrawable.draw(canvas);
                        float f7 = groupCallRenderersContainer.progressToFullscreenMode;
                        if (f7 <= 0.0f && f6 <= 0.0f) {
                            GroupCallMiniTextureView.this.stopSharingTextView.setAlpha(0.0f);
                        } else {
                            float max = Math.max(f7, f6) * f5;
                            textPaint2.setAlpha((int) (max * 255.0f));
                            GroupCallMiniTextureView groupCallMiniTextureView8 = GroupCallMiniTextureView.this;
                            if (groupCallMiniTextureView8.animateToFullscreen || groupCallMiniTextureView8.showingInFullscreen) {
                                groupCallMiniTextureView8.stopSharingTextView.setAlpha(max * (1.0f - f6));
                            } else {
                                groupCallMiniTextureView8.stopSharingTextView.setAlpha(0.0f);
                            }
                            canvas.drawText(string2, (measuredWidth - (measureText2 / 2.0f)) + (i2 / 2.0f), AndroidUtilities.dp(32.0f) + i, textPaint2);
                        }
                        GroupCallMiniTextureView.this.stopSharingTextView.setTranslationY(((AndroidUtilities.dp(72.0f) + i) + GroupCallMiniTextureView.this.swipeToBackDy) - this.currentClipVertical);
                        GroupCallMiniTextureView.this.stopSharingTextView.setTranslationX(((getMeasuredWidth() - GroupCallMiniTextureView.this.stopSharingTextView.getMeasuredWidth()) / 2.0f) - this.currentClipHorizontal);
                        float f8 = groupCallRenderersContainer.progressToFullscreenMode;
                        if (f8 < 1.0f && f6 < 1.0f) {
                            TextPaint textPaint3 = textPaint;
                            double max2 = Math.max(f8, f6);
                            Double.isNaN(max2);
                            textPaint3.setAlpha((int) ((1.0d - max2) * 255.0d));
                            canvas.save();
                            canvas.translate((measuredWidth - (AndroidUtilities.dp(400.0f) / 2.0f)) + (i2 / 2.0f), i + AndroidUtilities.dp(10.0f));
                            staticLayout.draw(canvas);
                            canvas.restore();
                        }
                    }
                    invalidate();
                }
                GroupCallMiniTextureView.this.noRtmpStreamTextView.setTranslationY((((getMeasuredHeight() - GroupCallMiniTextureView.this.noRtmpStreamTextView.getMeasuredHeight()) / 2.0f) + GroupCallMiniTextureView.this.swipeToBackDy) - this.currentClipVertical);
                GroupCallMiniTextureView.this.noRtmpStreamTextView.setTranslationX(((getMeasuredWidth() - GroupCallMiniTextureView.this.noRtmpStreamTextView.getMeasuredWidth()) / 2.0f) - this.currentClipHorizontal);
                ImageView imageView = GroupCallMiniTextureView.this.blurredFlippingStub;
                if (imageView != null && imageView.getParent() != null) {
                    GroupCallMiniTextureView groupCallMiniTextureView9 = GroupCallMiniTextureView.this;
                    groupCallMiniTextureView9.blurredFlippingStub.setScaleX(groupCallMiniTextureView9.textureView.renderer.getScaleX());
                    GroupCallMiniTextureView groupCallMiniTextureView10 = GroupCallMiniTextureView.this;
                    groupCallMiniTextureView10.blurredFlippingStub.setScaleY(groupCallMiniTextureView10.textureView.renderer.getScaleY());
                }
                super.dispatchDraw(canvas);
                float measuredHeight2 = (getMeasuredHeight() - this.currentClipVertical) - AndroidUtilities.dp(80.0f);
                if (GroupCallMiniTextureView.this.participant != call.videoNotAvailableParticipant) {
                    canvas.save();
                    GroupCallMiniTextureView groupCallMiniTextureView11 = GroupCallMiniTextureView.this;
                    if ((groupCallMiniTextureView11.showingInFullscreen || groupCallMiniTextureView11.animateToFullscreen) && !GroupCallActivity.isLandscapeMode && !GroupCallActivity.isTabletMode) {
                        GroupCallRenderersContainer groupCallRenderersContainer2 = groupCallRenderersContainer;
                        measuredHeight2 -= (AndroidUtilities.dp(90.0f) * groupCallRenderersContainer2.progressToFullscreenMode) * (1.0f - groupCallRenderersContainer2.progressToHideUi);
                    }
                    canvas.translate(0.0f, measuredHeight2);
                    canvas.drawPaint(GroupCallMiniTextureView.this.gradientPaint);
                    canvas.restore();
                }
                if (GroupCallMiniTextureView.this.videoIsPaused || GroupCallMiniTextureView.this.videoIsPausedProgress != 0.0f) {
                    if (!GroupCallMiniTextureView.this.videoIsPaused || GroupCallMiniTextureView.this.videoIsPausedProgress == 1.0f) {
                        if (!GroupCallMiniTextureView.this.videoIsPaused && GroupCallMiniTextureView.this.videoIsPausedProgress != 0.0f) {
                            GroupCallMiniTextureView.access$724(GroupCallMiniTextureView.this, 0.064f);
                            if (GroupCallMiniTextureView.this.videoIsPausedProgress < 0.0f) {
                                GroupCallMiniTextureView.this.videoIsPausedProgress = 0.0f;
                            } else {
                                invalidate();
                            }
                        }
                    } else {
                        GroupCallMiniTextureView.access$716(GroupCallMiniTextureView.this, 0.064f);
                        if (GroupCallMiniTextureView.this.videoIsPausedProgress > 1.0f) {
                            GroupCallMiniTextureView.this.videoIsPausedProgress = 1.0f;
                        } else {
                            invalidate();
                        }
                    }
                    float f9 = GroupCallMiniTextureView.this.videoIsPausedProgress;
                    if (isInAnimation()) {
                        float var_ = this.overlayIconAlphaFrom;
                        float var_ = this.animationProgress;
                        f3 = (var_ * (1.0f - var_)) + (GroupCallMiniTextureView.this.overlayIconAlpha * var_);
                    } else {
                        f3 = GroupCallMiniTextureView.this.overlayIconAlpha;
                    }
                    float var_ = f9 * f3;
                    if (var_ <= 0.0f) {
                        return;
                    }
                    float dp7 = AndroidUtilities.dp(48.0f);
                    float measuredWidth2 = (getMeasuredWidth() - dp7) / 2.0f;
                    float measuredHeight3 = (getMeasuredHeight() - dp7) / 2.0f;
                    if (GroupCallMiniTextureView.this.participant == call.videoNotAvailableParticipant) {
                        measuredHeight3 -= dp7 / 2.5f;
                    }
                    RectF rectF = AndroidUtilities.rectTmp;
                    float var_ = measuredHeight3 + dp7;
                    rectF.set((int) measuredWidth2, (int) measuredHeight3, (int) (measuredWidth2 + dp7), (int) var_);
                    if (var_ != 1.0f) {
                        canvas.saveLayerAlpha(rectF, (int) (var_ * 255.0f), 31);
                    } else {
                        canvas.save();
                    }
                    GroupCallMiniTextureView.this.pausedVideoDrawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                    GroupCallMiniTextureView.this.pausedVideoDrawable.draw(canvas);
                    canvas.restore();
                    float var_ = var_ * groupCallRenderersContainer.progressToFullscreenMode;
                    if (var_ <= 0.0f || GroupCallMiniTextureView.this.participant == call.videoNotAvailableParticipant) {
                        return;
                    }
                    textPaint.setAlpha((int) (var_ * 255.0f));
                    canvas.drawText(string, (measuredWidth2 - (measureText / 2.0f)) + (dp7 / 2.0f), var_ + AndroidUtilities.dp(16.0f), textPaint);
                }
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View view, long j) {
                GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                if (groupCallMiniTextureView.inPinchToZoom && view == groupCallMiniTextureView.textureView.renderer) {
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
                return super.drawChild(canvas, view, j);
            }

            @Override // android.view.View
            public void invalidate() {
                super.invalidate();
                GroupCallMiniTextureView.this.invalidateFromChild = true;
                GroupCallMiniTextureView.this.invalidate();
                GroupCallMiniTextureView.this.invalidateFromChild = false;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.voip.VoIPTextureView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int i5;
                ChatObject.VideoParticipant videoParticipant;
                GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                if (groupCallMiniTextureView.attached && groupCallMiniTextureView.checkScale) {
                    TextureViewRenderer textureViewRenderer = this.renderer;
                    if (textureViewRenderer.rotatedFrameHeight != 0 && textureViewRenderer.rotatedFrameWidth != 0) {
                        if (GroupCallMiniTextureView.this.showingAsScrimView) {
                            GroupCallMiniTextureView.this.textureView.scaleType = VoIPTextureView.SCALE_TYPE_FIT;
                        } else {
                            GroupCallMiniTextureView groupCallMiniTextureView2 = GroupCallMiniTextureView.this;
                            if (groupCallMiniTextureView2.showingInFullscreen) {
                                groupCallMiniTextureView2.textureView.scaleType = VoIPTextureView.SCALE_TYPE_FIT;
                            } else if (groupCallRenderersContainer.inFullscreenMode) {
                                groupCallMiniTextureView2.textureView.scaleType = VoIPTextureView.SCALE_TYPE_FILL;
                            } else if (groupCallMiniTextureView2.participant.presentation) {
                                groupCallMiniTextureView2.textureView.scaleType = VoIPTextureView.SCALE_TYPE_FIT;
                            } else {
                                groupCallMiniTextureView2.textureView.scaleType = VoIPTextureView.SCALE_TYPE_ADAPTIVE;
                            }
                        }
                        GroupCallMiniTextureView.this.checkScale = false;
                    }
                }
                super.onLayout(z, i, i2, i3, i4);
                TextureViewRenderer textureViewRenderer2 = this.renderer;
                int i6 = textureViewRenderer2.rotatedFrameHeight;
                if (i6 == 0 || (i5 = textureViewRenderer2.rotatedFrameWidth) == 0 || (videoParticipant = GroupCallMiniTextureView.this.participant) == null) {
                    return;
                }
                videoParticipant.setAspectRatio(i5, i6, call);
            }

            @Override // org.telegram.ui.Components.voip.VoIPTextureView, android.view.View, android.view.ViewParent
            public void requestLayout() {
                GroupCallMiniTextureView.this.requestLayout();
                super.requestLayout();
            }

            @Override // org.telegram.ui.Components.voip.VoIPTextureView
            protected void onFirstFrameRendered() {
                int i;
                ChatObject.VideoParticipant videoParticipant;
                invalidate();
                ChatObject.Call call2 = call;
                if (call2 != null && call2.call.rtmp_stream && GroupCallMiniTextureView.this.postedNoRtmpStreamCallback) {
                    AndroidUtilities.cancelRunOnUIThread(GroupCallMiniTextureView.this.noRtmpStreamCallback);
                    GroupCallMiniTextureView.this.postedNoRtmpStreamCallback = false;
                    GroupCallMiniTextureView.this.noRtmpStreamTextView.animate().cancel();
                    GroupCallMiniTextureView.this.noRtmpStreamTextView.animate().alpha(0.0f).setDuration(150L).start();
                    GroupCallMiniTextureView.this.textureView.animate().cancel();
                    GroupCallMiniTextureView.this.textureView.animate().alpha(1.0f).setDuration(150L).start();
                }
                if (!GroupCallMiniTextureView.this.videoIsPaused && this.renderer.getAlpha() != 1.0f) {
                    this.renderer.animate().setDuration(300L).alpha(1.0f);
                }
                TextureView textureView = this.blurRenderer;
                if (textureView != null && textureView.getAlpha() != 1.0f) {
                    this.blurRenderer.animate().setDuration(300L).alpha(1.0f);
                }
                ImageView imageView = GroupCallMiniTextureView.this.blurredFlippingStub;
                if (imageView != null && imageView.getParent() != null) {
                    if (GroupCallMiniTextureView.this.blurredFlippingStub.getAlpha() == 1.0f) {
                        GroupCallMiniTextureView.this.blurredFlippingStub.animate().alpha(0.0f).setDuration(300L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView.1.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                if (GroupCallMiniTextureView.this.blurredFlippingStub.getParent() != null) {
                                    GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                                    groupCallMiniTextureView.textureView.removeView(groupCallMiniTextureView.blurredFlippingStub);
                                }
                            }
                        }).start();
                    } else if (GroupCallMiniTextureView.this.blurredFlippingStub.getParent() != null) {
                        GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                        groupCallMiniTextureView.textureView.removeView(groupCallMiniTextureView.blurredFlippingStub);
                    }
                }
                TextureViewRenderer textureViewRenderer = this.renderer;
                int i2 = textureViewRenderer.rotatedFrameHeight;
                if (i2 == 0 || (i = textureViewRenderer.rotatedFrameWidth) == 0 || (videoParticipant = GroupCallMiniTextureView.this.participant) == null) {
                    return;
                }
                videoParticipant.setAspectRatio(i, i2, call);
            }
        };
        this.textureView = voIPTextureView;
        voIPTextureView.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        this.parentContainer = groupCallRenderersContainer;
        this.attachedRenderers = arrayList;
        this.activity = groupCallActivity;
        this.textureView.renderer.init(VideoCapturerDevice.getEglBase().mo2426getEglBaseContext(), new RendererCommon.RendererEvents() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView.2
            @Override // org.webrtc.RendererCommon.RendererEvents
            public void onFrameResolutionChanged(int i, int i2, int i3) {
            }

            @Override // org.webrtc.RendererCommon.RendererEvents
            public void onFirstFrameRendered() {
                for (int i = 0; i < GroupCallMiniTextureView.this.onFirstFrameRunnables.size(); i++) {
                    AndroidUtilities.cancelRunOnUIThread(GroupCallMiniTextureView.this.onFirstFrameRunnables.get(i));
                    GroupCallMiniTextureView.this.onFirstFrameRunnables.get(i).run();
                }
                GroupCallMiniTextureView.this.onFirstFrameRunnables.clear();
            }
        });
        this.textureView.attachBackgroundRenderer();
        setClipChildren(false);
        this.textureView.renderer.setAlpha(0.0f);
        addView(this.textureView);
        NoVideoStubLayout noVideoStubLayout = new NoVideoStubLayout(getContext());
        this.noVideoStubLayout = noVideoStubLayout;
        addView(noVideoStubLayout);
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
        this.speakingPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.speakingPaint.setColor(Theme.getColor("voipgroup_speakingText"));
        this.infoContainer.setClipChildren(false);
        RLottieImageView rLottieImageView = new RLottieImageView(groupCallRenderersContainer.getContext());
        this.micIconView = rLottieImageView;
        addView(rLottieImageView, LayoutHelper.createFrame(24, 24.0f, 0, 4.0f, 6.0f, 4.0f, 0.0f));
        ImageView imageView = new ImageView(groupCallRenderersContainer.getContext());
        this.screencastIcon = imageView;
        addView(imageView, LayoutHelper.createFrame(24, 24.0f, 0, 4.0f, 6.0f, 4.0f, 0.0f));
        imageView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
        imageView.setImageDrawable(ContextCompat.getDrawable(groupCallRenderersContainer.getContext(), R.drawable.voicechat_screencast));
        imageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(19.0f), 0, ColorUtils.setAlphaComponent(-1, 100));
        TextView textView = new TextView(groupCallRenderersContainer.getContext()) { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView.3
            @Override // android.widget.TextView, android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (Math.abs(GroupCallMiniTextureView.this.stopSharingTextView.getAlpha() - 1.0f) > 0.001f) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.stopSharingTextView = textView;
        textView.setText(LocaleController.getString("VoipVideoScreenStopSharing", R.string.VoipVideoScreenStopSharing));
        this.stopSharingTextView.setTextSize(1, 15.0f);
        this.stopSharingTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.stopSharingTextView.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
        this.stopSharingTextView.setTextColor(-1);
        this.stopSharingTextView.setBackground(createSimpleSelectorRoundRectDrawable);
        this.stopSharingTextView.setGravity(17);
        this.stopSharingTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GroupCallMiniTextureView.this.lambda$new$1(view);
            }
        });
        addView(this.stopSharingTextView, LayoutHelper.createFrame(-2, 38, 51));
        TextView textView2 = new TextView(groupCallRenderersContainer.getContext());
        this.noRtmpStreamTextView = textView2;
        textView2.setTextSize(1, 15.0f);
        this.noRtmpStreamTextView.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
        this.noRtmpStreamTextView.setTextColor(Theme.getColor("voipgroup_lastSeenText"));
        this.noRtmpStreamTextView.setBackground(createSimpleSelectorRoundRectDrawable);
        this.noRtmpStreamTextView.setGravity(17);
        this.noRtmpStreamTextView.setAlpha(0.0f);
        if (ChatObject.canManageCalls(chat)) {
            this.noRtmpStreamTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.NoRtmpStreamFromAppOwner)));
        } else {
            this.noRtmpStreamTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoRtmpStreamFromAppViewer", R.string.NoRtmpStreamFromAppViewer, chat.title)));
        }
        addView(this.noRtmpStreamTextView, LayoutHelper.createFrame(-2, -2, 51));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().stopScreenCapture();
        }
        this.stopSharingTextView.animate().alpha(0.0f).scaleX(0.0f).scaleY(0.0f).setDuration(180L).start();
    }

    public boolean isInsideStopScreenButton(float f, float f2) {
        this.stopSharingTextView.getHitRect(this.rect);
        return this.rect.contains((int) f, (int) f2);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.attached) {
            float y = (((this.textureView.getY() + this.textureView.getMeasuredHeight()) - this.textureView.currentClipVertical) - this.infoContainer.getMeasuredHeight()) + this.swipeToBackDy;
            if (this.showingAsScrimView || this.animateToScrimView) {
                this.infoContainer.setAlpha(1.0f - this.parentContainer.progressToScrimView);
                this.micIconView.setAlpha(1.0f - this.parentContainer.progressToScrimView);
            } else if (this.showingInFullscreen || this.animateToFullscreen) {
                if (!GroupCallActivity.isLandscapeMode && !GroupCallActivity.isTabletMode) {
                    GroupCallRenderersContainer groupCallRenderersContainer = this.parentContainer;
                    y -= (AndroidUtilities.dp(90.0f) * groupCallRenderersContainer.progressToFullscreenMode) * (1.0f - groupCallRenderersContainer.progressToHideUi);
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
            this.micIconView.setTranslationY(y - AndroidUtilities.dp(2.0f));
            if (this.screencastIcon.getVisibility() == 0) {
                this.screencastIcon.setTranslationX((this.textureView.getMeasuredWidth() - (this.textureView.currentClipHorizontal * 2.0f)) - AndroidUtilities.dp(32.0f));
                this.screencastIcon.setTranslationY(y - AndroidUtilities.dp(2.0f));
                ImageView imageView = this.screencastIcon;
                GroupCallRenderersContainer groupCallRenderersContainer2 = this.parentContainer;
                imageView.setAlpha(Math.min(1.0f - groupCallRenderersContainer2.progressToFullscreenMode, 1.0f - groupCallRenderersContainer2.progressToScrimView));
            }
            this.infoContainer.setTranslationY(y);
            this.infoContainer.setTranslationX(this.drawFirst ? 0.0f : AndroidUtilities.dp(6.0f) * this.parentContainer.progressToFullscreenMode);
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
            if (f5 <= 0.0f) {
                return;
            }
            this.speakingPaint.setAlpha((int) (f6 * 255.0f));
            float max = (Math.max(0.0f, 1.0f - (Math.abs(this.swipeToBackDy) / AndroidUtilities.dp(300.0f))) * 0.1f) + 0.9f;
            canvas.save();
            RectF rectF = AndroidUtilities.rectTmp;
            float x = this.textureView.getX();
            VoIPTextureView voIPTextureView = this.textureView;
            float f7 = x + voIPTextureView.currentClipHorizontal;
            float y2 = voIPTextureView.getY();
            VoIPTextureView voIPTextureView2 = this.textureView;
            float f8 = y2 + voIPTextureView2.currentClipVertical;
            float x2 = voIPTextureView2.getX() + this.textureView.getMeasuredWidth();
            VoIPTextureView voIPTextureView3 = this.textureView;
            rectF.set(f7, f8, x2 - voIPTextureView3.currentClipHorizontal, (voIPTextureView3.getY() + this.textureView.getMeasuredHeight()) - this.textureView.currentClipVertical);
            canvas.scale(max, max, rectF.centerX(), rectF.centerY());
            canvas.translate(0.0f, this.swipeToBackDy);
            float f9 = this.textureView.roundRadius;
            canvas.drawRoundRect(rectF, f9, f9, this.speakingPaint);
            canvas.restore();
        }
    }

    public void getRenderBufferBitmap(GlGenericDrawer.TextureCallback textureCallback) {
        this.textureView.renderer.getRenderBufferBitmap(textureCallback);
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View view, long j) {
        if (this.swipeToBack && (view == this.textureView || view == this.noVideoStubLayout)) {
            float max = (Math.max(0.0f, 1.0f - (Math.abs(this.swipeToBackDy) / AndroidUtilities.dp(300.0f))) * 0.1f) + 0.9f;
            canvas.save();
            canvas.scale(max, max, view.getX() + (view.getMeasuredWidth() / 2.0f), view.getY() + (view.getMeasuredHeight() / 2.0f));
            canvas.translate(0.0f, this.swipeToBackDy);
            boolean drawChild = super.drawChild(canvas, view, j);
            canvas.restore();
            return drawChild;
        }
        return super.drawChild(canvas, view, j);
    }

    /* JADX WARN: Removed duplicated region for block: B:55:0x0185  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x0196  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x01ba  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x01c4  */
    @Override // android.widget.FrameLayout, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onMeasure(int r14, int r15) {
        /*
            Method dump skipped, instructions count: 553
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallMiniTextureView.onMeasure(int, int):void");
    }

    public static GroupCallMiniTextureView getOrCreate(ArrayList<GroupCallMiniTextureView> arrayList, GroupCallRenderersContainer groupCallRenderersContainer, GroupCallGridCell groupCallGridCell, GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell, GroupCallGridCell groupCallGridCell2, ChatObject.VideoParticipant videoParticipant, ChatObject.Call call, GroupCallActivity groupCallActivity) {
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
            groupCallMiniTextureView = new GroupCallMiniTextureView(groupCallRenderersContainer, arrayList, call, groupCallActivity);
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

    /* JADX WARN: Code restructure failed: missing block: B:103:0x0180, code lost:
        if (org.telegram.messenger.voip.VoIPService.getSharedInstance().getVideoState(r23.participant.presentation) == 2) goto L304;
     */
    /* JADX WARN: Code restructure failed: missing block: B:104:0x0182, code lost:
        r1 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:109:0x018e, code lost:
        if (r1 != r11.videoNotAvailableParticipant) goto L348;
     */
    /* JADX WARN: Code restructure failed: missing block: B:111:0x0196, code lost:
        if (org.telegram.messenger.ChatObject.Call.videoIsActive(r10, r1.presentation, r11) == false) goto L348;
     */
    /* JADX WARN: Code restructure failed: missing block: B:119:0x01bd, code lost:
        if (r1 != false) goto L312;
     */
    /* JADX WARN: Code restructure failed: missing block: B:45:0x0089, code lost:
        if (r23.participant != r10.videoNotAvailableParticipant) goto L282;
     */
    /* JADX WARN: Removed duplicated region for block: B:157:0x02bd  */
    /* JADX WARN: Removed duplicated region for block: B:160:0x02d1  */
    /* JADX WARN: Removed duplicated region for block: B:165:0x02e7  */
    /* JADX WARN: Removed duplicated region for block: B:212:0x036f  */
    /* JADX WARN: Removed duplicated region for block: B:225:0x038a  */
    /* JADX WARN: Removed duplicated region for block: B:228:0x0395  */
    /* JADX WARN: Removed duplicated region for block: B:229:0x039d  */
    /* JADX WARN: Removed duplicated region for block: B:233:0x03ba  */
    /* JADX WARN: Removed duplicated region for block: B:241:0x0404  */
    /* JADX WARN: Removed duplicated region for block: B:242:0x040e  */
    /* JADX WARN: Removed duplicated region for block: B:245:0x0423  */
    /* JADX WARN: Removed duplicated region for block: B:253:0x044c  */
    /* JADX WARN: Removed duplicated region for block: B:254:0x046e  */
    /* JADX WARN: Removed duplicated region for block: B:257:0x0496  */
    /* JADX WARN: Removed duplicated region for block: B:263:0x04d7  */
    /* JADX WARN: Removed duplicated region for block: B:272:0x04e8 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:275:0x04f0  */
    /* JADX WARN: Removed duplicated region for block: B:277:0x04fa  */
    /* JADX WARN: Removed duplicated region for block: B:287:0x053e  */
    /* JADX WARN: Removed duplicated region for block: B:298:0x0563  */
    /* JADX WARN: Removed duplicated region for block: B:301:0x0570  */
    /* JADX WARN: Removed duplicated region for block: B:306:0x0596  */
    /* JADX WARN: Removed duplicated region for block: B:309:0x05a1  */
    /* JADX WARN: Removed duplicated region for block: B:314:0x05ac  */
    /* JADX WARN: Removed duplicated region for block: B:321:0x05bb  */
    /* JADX WARN: Removed duplicated region for block: B:327:0x05e0  */
    /* JADX WARN: Removed duplicated region for block: B:351:0x0664  */
    /* JADX WARN: Removed duplicated region for block: B:354:0x0676  */
    /* JADX WARN: Removed duplicated region for block: B:359:0x069a  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x00dd  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x00e6  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x00f9  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x010b  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0125  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateAttachState(boolean r24) {
        /*
            Method dump skipped, instructions count: 1731
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallMiniTextureView.updateAttachState(boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateAttachState$2(View view) {
        this.parentContainer.removeView(view);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateAttachState$3(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.progressToNoVideoStub = floatValue;
        this.noVideoStubLayout.setAlpha(floatValue);
        this.textureView.invalidate();
    }

    private void loadThumb() {
        if (this.thumb != null) {
            return;
        }
        HashMap<String, Bitmap> hashMap = this.call.thumbs;
        ChatObject.VideoParticipant videoParticipant = this.participant;
        boolean z = videoParticipant.presentation;
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = videoParticipant.participant;
        Bitmap bitmap = hashMap.get(z ? tLRPC$TL_groupCallParticipant.presentationEndpoint : tLRPC$TL_groupCallParticipant.videoEndpoint);
        this.thumb = bitmap;
        this.textureView.setThumb(bitmap);
        if (this.thumb != null) {
            return;
        }
        long peerId = MessageObject.getPeerId(this.participant.participant.peer);
        ChatObject.VideoParticipant videoParticipant2 = this.participant;
        if (videoParticipant2.participant.self && videoParticipant2.presentation) {
            this.imageReceiver.setImageBitmap(new MotionBackgroundDrawable(-14602694, -13935795, -14395293, -14203560, true));
        } else if (peerId > 0) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(peerId));
            ImageLocation forUser = ImageLocation.getForUser(user, 1);
            int colorForId = user != null ? AvatarDrawable.getColorForId(user.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
            this.imageReceiver.setImage(forUser, "50_50_b", new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(colorForId, -16777216, 0.2f), ColorUtils.blendARGB(colorForId, -16777216, 0.4f)}), null, user, 0);
        } else {
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-peerId));
            ImageLocation forChat = ImageLocation.getForChat(chat, 1);
            int colorForId2 = chat != null ? AvatarDrawable.getColorForId(chat.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
            this.imageReceiver.setImage(forChat, "50_50_b", new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(colorForId2, -16777216, 0.2f), ColorUtils.blendARGB(colorForId2, -16777216, 0.4f)}), null, chat, 0);
        }
    }

    public void updateInfo() {
        if (!this.attached) {
            return;
        }
        String str = null;
        long peerId = MessageObject.getPeerId(this.participant.participant.peer);
        if (DialogObject.isUserDialog(peerId)) {
            str = UserObject.getUserName(AccountInstance.getInstance(this.currentAccount).getMessagesController().getUser(Long.valueOf(peerId)));
        } else {
            TLRPC$Chat chat = AccountInstance.getInstance(this.currentAccount).getMessagesController().getChat(Long.valueOf(-peerId));
            if (chat != null) {
                str = chat.title;
            }
        }
        this.nameView.setText(str);
    }

    public boolean hasImage() {
        return this.textureView.stubVisibleProgress == 1.0f;
    }

    /* JADX WARN: Code restructure failed: missing block: B:100:?, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00c4, code lost:
        if (r0 != false) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x00c6, code lost:
        r9 = r10;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x00cb, code lost:
        if (r2 != null) goto L48;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x00ce, code lost:
        setTranslationX(((r11.getX() + r9.getX()) - getLeft()) - r12.getLeft());
        setTranslationY((((r11.getY() + org.telegram.messenger.AndroidUtilities.dp(2.0f)) + r9.getY()) - getTop()) - r12.getTop());
        r8.textureView.setRoundCorners(org.telegram.messenger.AndroidUtilities.dp(8.0f));
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0110, code lost:
        if (r8.attached == false) goto L66;
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x0114, code lost:
        if (r8.animateEnter != false) goto L65;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x0118, code lost:
        if (org.telegram.ui.GroupCallActivity.isTabletMode != false) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:59:0x011a, code lost:
        r8.drawFirst = true;
        setAlpha((1.0f - r1) * r11.getAlpha());
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x012a, code lost:
        if (r8.primaryView == null) goto L64;
     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x012e, code lost:
        if (r8.tabletGridView != null) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x0130, code lost:
        setAlpha(r1 * r11.getAlpha());
     */
    /* JADX WARN: Code restructure failed: missing block: B:95:?, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:96:?, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:97:?, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:98:?, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:99:?, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updatePosition(android.view.ViewGroup r9, android.view.ViewGroup r10, org.telegram.ui.Components.RecyclerListView r11, org.telegram.ui.Components.voip.GroupCallRenderersContainer r12) {
        /*
            Method dump skipped, instructions count: 445
            To view this dump add '--comments-level debug' option
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
            this.statusIcon.setCallback(null);
            this.statusIcon.setImageView(null);
        }
        this.statusIcon = null;
    }

    public boolean isFullyVisible() {
        return !this.showingInFullscreen && !this.animateToFullscreen && this.attached && this.textureView.renderer.isFirstFrameRendered() && getAlpha() == 1.0f;
    }

    @Override // android.view.View
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
                    VoIPService.getSharedInstance().setLocalSink(null, this.participant.presentation);
                }
            } else if (VoIPService.getSharedInstance() != null && !RTMPStreamPipOverlay.isVisible()) {
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
        if (this.participant == null || this.textureView.renderer.getMeasuredHeight() == 0 || this.textureView.renderer.getMeasuredWidth() == 0) {
            return;
        }
        getRenderBufferBitmap(new GlGenericDrawer.TextureCallback() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda8
            @Override // org.webrtc.GlGenericDrawer.TextureCallback
            public final void run(Bitmap bitmap, int i) {
                GroupCallMiniTextureView.this.lambda$saveThumb$5(bitmap, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveThumb$5(final Bitmap bitmap, int i) {
        if (bitmap == null || bitmap.getPixel(0, 0) == 0) {
            return;
        }
        Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(bitmap.getWidth(), bitmap.getHeight()) / 180));
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                GroupCallMiniTextureView.this.lambda$saveThumb$4(bitmap);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveThumb$4(Bitmap bitmap) {
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
        if (this.pinchScale == f && this.pinchCenterX == f2 && this.pinchCenterY == f3 && this.pinchTranslationX == f4 && this.pinchTranslationY == f5) {
            return;
        }
        this.inPinchToZoom = z;
        this.pinchScale = f;
        this.pinchCenterX = f2;
        this.pinchCenterY = f3;
        this.pinchTranslationX = f4;
        this.pinchTranslationY = f5;
        this.textureView.invalidate();
    }

    public void setSwipeToBack(boolean z, float f) {
        if (this.swipeToBack == z && this.swipeToBackDy == f) {
            return;
        }
        this.swipeToBack = z;
        this.swipeToBackDy = f;
        this.textureView.invalidate();
        invalidate();
    }

    public void runOnFrameRendered(Runnable runnable) {
        if (this.textureView.renderer.isFirstFrameRendered()) {
            runnable.run();
            return;
        }
        AndroidUtilities.runOnUIThread(runnable, 250L);
        this.onFirstFrameRunnables.add(runnable);
    }

    @Override // org.telegram.ui.Components.voip.GroupCallStatusIcon.Callback
    public void onStatusChanged() {
        invalidate();
        updateIconColor(true);
        if (this.noVideoStubLayout.getVisibility() == 0) {
            this.noVideoStubLayout.updateMuteButtonState(true);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x002d A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:16:0x002e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateIconColor(boolean r10) {
        /*
            r9 = this;
            org.telegram.ui.Components.voip.GroupCallStatusIcon r0 = r9.statusIcon
            if (r0 != 0) goto L5
            return
        L5:
            boolean r0 = r0.isMutedByMe()
            if (r0 == 0) goto L13
            java.lang.String r0 = "voipgroup_mutedByAdminIcon"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
        L11:
            r1 = r0
            goto L29
        L13:
            org.telegram.ui.Components.voip.GroupCallStatusIcon r0 = r9.statusIcon
            boolean r0 = r0.isSpeaking()
            java.lang.String r1 = "voipgroup_speakingText"
            if (r0 == 0) goto L22
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            goto L11
        L22:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r1 = -1
            r1 = r0
            r0 = -1
        L29:
            int r2 = r9.animateToColor
            if (r2 != r0) goto L2e
            return
        L2e:
            android.animation.ValueAnimator r2 = r9.colorAnimator
            if (r2 == 0) goto L3a
            r2.removeAllListeners()
            android.animation.ValueAnimator r2 = r9.colorAnimator
            r2.cancel()
        L3a:
            if (r10 != 0) goto L44
            android.graphics.Paint r10 = r9.speakingPaint
            r9.lastSpeakingFrameColor = r1
            r10.setColor(r1)
            goto L71
        L44:
            int r4 = r9.lastIconColor
            int r6 = r9.lastSpeakingFrameColor
            r9.animateToColor = r0
            r10 = 2
            float[] r10 = new float[r10]
            r10 = {x0072: FILL_ARRAY_DATA  , data: [0, NUM} // fill-array
            android.animation.ValueAnimator r10 = android.animation.ValueAnimator.ofFloat(r10)
            r9.colorAnimator = r10
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda2 r8 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda2
            r2 = r8
            r3 = r9
            r5 = r0
            r7 = r1
            r2.<init>()
            r10.addUpdateListener(r8)
            android.animation.ValueAnimator r10 = r9.colorAnimator
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$7 r2 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$7
            r2.<init>()
            r10.addListener(r2)
            android.animation.ValueAnimator r10 = r9.colorAnimator
            r10.start()
        L71:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallMiniTextureView.updateIconColor(boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateIconColor$6(int i, int i2, int i3, int i4, ValueAnimator valueAnimator) {
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
        if ((this.collapseSize == i || i <= 0) && (this.fullSize == measuredWidth || measuredWidth <= 0)) {
            return;
        }
        if (i != 0) {
            this.collapseSize = i;
        }
        if (measuredWidth != 0) {
            this.fullSize = measuredWidth;
        }
        this.nameView.setFullLayoutAdditionalWidth(measuredWidth - i, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class NoVideoStubLayout extends View {
        float amplitude;
        float animateAmplitudeDiff;
        float animateToAmplitude;
        AvatarDrawable avatarDrawable;
        public ImageReceiver avatarImageReceiver;
        public ImageReceiver backgroundImageReceiver;
        Paint backgroundPaint;
        BlobDrawable bigWaveDrawable;
        private GroupCallActivity.WeavingState currentState;
        float cx;
        float cy;
        int muteButtonState;
        Paint paint;
        private GroupCallActivity.WeavingState prevState;
        float speakingProgress;
        private GroupCallActivity.WeavingState[] states;
        float switchProgress;
        BlobDrawable tinyWaveDrawable;

        public NoVideoStubLayout(Context context) {
            super(context);
            this.avatarImageReceiver = new ImageReceiver();
            this.backgroundImageReceiver = new ImageReceiver();
            this.avatarDrawable = new AvatarDrawable();
            this.paint = new Paint(1);
            this.backgroundPaint = new Paint(1);
            this.states = new GroupCallActivity.WeavingState[3];
            this.muteButtonState = -1;
            this.switchProgress = 1.0f;
            this.tinyWaveDrawable = new BlobDrawable(9);
            this.bigWaveDrawable = new BlobDrawable(12);
            this.tinyWaveDrawable.minRadius = AndroidUtilities.dp(76.0f);
            this.tinyWaveDrawable.maxRadius = AndroidUtilities.dp(92.0f);
            this.tinyWaveDrawable.generateBlob();
            this.bigWaveDrawable.minRadius = AndroidUtilities.dp(80.0f);
            this.bigWaveDrawable.maxRadius = AndroidUtilities.dp(95.0f);
            this.bigWaveDrawable.generateBlob();
            this.paint.setColor(ColorUtils.blendARGB(Theme.getColor("voipgroup_listeningText"), Theme.getColor("voipgroup_speakingText"), this.speakingProgress));
            this.paint.setAlpha(102);
            this.backgroundPaint.setColor(ColorUtils.setAlphaComponent(-16777216, 127));
        }

        @Override // android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            float dp = AndroidUtilities.dp(157.0f);
            this.cx = getMeasuredWidth() >> 1;
            this.cy = (getMeasuredHeight() >> 1) + (GroupCallActivity.isLandscapeMode ? 0.0f : (-getMeasuredHeight()) * 0.12f);
            float f = dp / 2.0f;
            this.avatarImageReceiver.setRoundRadius((int) f);
            this.avatarImageReceiver.setImageCoords(this.cx - f, this.cy - f, dp, dp);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            GroupCallActivity.WeavingState weavingState;
            float f;
            GroupCallActivity.WeavingState weavingState2;
            super.onDraw(canvas);
            RectF rectF = AndroidUtilities.rectTmp;
            float x = GroupCallMiniTextureView.this.textureView.getX();
            VoIPTextureView voIPTextureView = GroupCallMiniTextureView.this.textureView;
            float f2 = x + voIPTextureView.currentClipHorizontal;
            float y = voIPTextureView.getY();
            VoIPTextureView voIPTextureView2 = GroupCallMiniTextureView.this.textureView;
            float f3 = y + voIPTextureView2.currentClipVertical;
            float x2 = voIPTextureView2.getX() + GroupCallMiniTextureView.this.textureView.getMeasuredWidth();
            VoIPTextureView voIPTextureView3 = GroupCallMiniTextureView.this.textureView;
            rectF.set(f2, f3, x2 - voIPTextureView3.currentClipHorizontal, voIPTextureView3.getY() + GroupCallMiniTextureView.this.textureView.getMeasuredHeight() + GroupCallMiniTextureView.this.textureView.currentClipVertical);
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
                weavingState3.update((int) (this.cy - AndroidUtilities.dp(100.0f)), (int) (this.cx - AndroidUtilities.dp(100.0f)), AndroidUtilities.dp(200.0f), 16L, this.amplitude);
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

        /* JADX INFO: Access modifiers changed from: private */
        public void updateMuteButtonState(boolean z) {
            int i;
            if (GroupCallMiniTextureView.this.statusIcon.isMutedByMe() || GroupCallMiniTextureView.this.statusIcon.isMutedByAdmin()) {
                i = 2;
            } else {
                i = GroupCallMiniTextureView.this.statusIcon.isSpeaking() ? 1 : 0;
            }
            if (i == this.muteButtonState) {
                return;
            }
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

        @Override // android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.avatarImageReceiver.onAttachedToWindow();
            this.backgroundImageReceiver.onAttachedToWindow();
        }

        @Override // android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.avatarImageReceiver.onDetachedFromWindow();
            this.backgroundImageReceiver.onDetachedFromWindow();
        }
    }

    public String getName() {
        long peerId = MessageObject.getPeerId(this.participant.participant.peer);
        if (DialogObject.isUserDialog(peerId)) {
            return UserObject.getUserName(AccountInstance.getInstance(UserConfig.selectedAccount).getMessagesController().getUser(Long.valueOf(peerId)));
        }
        return AccountInstance.getInstance(UserConfig.selectedAccount).getMessagesController().getChat(Long.valueOf(-peerId)).title;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.imageReceiver.onDetachedFromWindow();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageReceiver.onAttachedToWindow();
    }

    public void startFlipAnimation() {
        if (this.flipAnimator != null) {
            return;
        }
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
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.flipAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                GroupCallMiniTextureView.this.lambda$startFlipAnimation$7(valueAnimator);
            }
        });
        this.flipAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView.8
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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
        this.flipAnimator.setDuration(400L);
        this.flipAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.flipAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startFlipAnimation$7(ValueAnimator valueAnimator) {
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
