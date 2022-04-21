package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
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
import org.telegram.tgnet.TLRPC;
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
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.GroupCallStatusIcon;
import org.telegram.ui.GroupCallActivity;
import org.webrtc.GlGenericDrawer;
import org.webrtc.RendererCommon;
import org.webrtc.VideoSink;

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
    /* access modifiers changed from: private */
    public Runnable noRtmpStreamCallback = new GroupCallMiniTextureView$$ExternalSyntheticLambda5(this);
    /* access modifiers changed from: private */
    public TextView noRtmpStreamTextView;
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
    /* access modifiers changed from: private */
    public boolean postedNoRtmpStreamCallback;
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
    public VoIPTextureView textureView;
    Bitmap thumb;
    Paint thumbPaint;
    private boolean updateNextLayoutAnimated;
    boolean useSpanSize;
    /* access modifiers changed from: private */
    public boolean videoIsPaused;
    /* access modifiers changed from: private */
    public float videoIsPausedProgress;

    static /* synthetic */ float access$116(GroupCallMiniTextureView x0, float x1) {
        float f = x0.progressToBackground + x1;
        x0.progressToBackground = f;
        return f;
    }

    static /* synthetic */ float access$716(GroupCallMiniTextureView x0, float x1) {
        float f = x0.videoIsPausedProgress + x1;
        x0.videoIsPausedProgress = f;
        return f;
    }

    static /* synthetic */ float access$724(GroupCallMiniTextureView x0, float x1) {
        float f = x0.videoIsPausedProgress - x1;
        x0.videoIsPausedProgress = f;
        return f;
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-voip-GroupCallMiniTextureView  reason: not valid java name */
    public /* synthetic */ void m4546xCLASSNAMEd612d() {
        if (!this.textureView.renderer.isFirstFrameRendered()) {
            this.textureView.animate().cancel();
            this.textureView.animate().alpha(0.0f).setDuration(150).start();
            this.noRtmpStreamTextView.animate().cancel();
            this.noRtmpStreamTextView.animate().alpha(1.0f).setDuration(150).start();
        }
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GroupCallMiniTextureView(GroupCallRenderersContainer parentContainer2, ArrayList<GroupCallMiniTextureView> attachedRenderers2, ChatObject.Call call2, GroupCallActivity activity2) {
        super(parentContainer2.getContext());
        ChatObject.Call call3 = call2;
        final GroupCallRenderersContainer groupCallRenderersContainer = parentContainer2;
        final ChatObject.Call call4 = call2;
        final GroupCallActivity groupCallActivity = activity2;
        this.call = call3;
        this.currentAccount = activity2.getCurrentAccount();
        CrossOutDrawable crossOutDrawable = new CrossOutDrawable(parentContainer2.getContext(), NUM, (String) null);
        this.pausedVideoDrawable = crossOutDrawable;
        crossOutDrawable.setCrossOut(true, false);
        this.pausedVideoDrawable.setOffsets((float) (-AndroidUtilities.dp(4.0f)), (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f));
        this.pausedVideoDrawable.setStrokeWidth(AndroidUtilities.dpf2(3.4f));
        this.castingScreenDrawable = parentContainer2.getContext().getResources().getDrawable(NUM).mutate();
        TextPaint textPaint = new TextPaint(1);
        final TextPaint textPaint2 = textPaint;
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        textPaint.setColor(-1);
        TextPaint textPaint22 = new TextPaint(1);
        final TextPaint textPaint3 = textPaint22;
        textPaint22.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textPaint22.setTextSize((float) AndroidUtilities.dp(15.0f));
        textPaint22.setColor(-1);
        String videoOnPauseString = LocaleController.getString("VoipVideoOnPause", NUM);
        final String str = videoOnPauseString;
        TextPaint textPaint4 = textPaint;
        final StaticLayout staticLayout = new StaticLayout(LocaleController.getString("VoipVideoScreenSharingTwoLines", NUM), textPaint4, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(call3.chatId));
        String videoOnPauseString2 = videoOnPauseString;
        final StaticLayout noVideoLayout = new StaticLayout(LocaleController.formatString("VoipVideoNotAvailable", NUM, LocaleController.formatPluralString("Participants", MessagesController.getInstance(this.currentAccount).groupCallVideoMaxParticipants)), textPaint4, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        String sharingScreenString = LocaleController.getString("VoipVideoScreenSharing", NUM);
        final String str2 = sharingScreenString;
        final float measureText = textPaint.measureText(videoOnPauseString2);
        TextPaint textPaint5 = textPaint22;
        final float measureText2 = textPaint22.measureText(sharingScreenString);
        TextPaint textPaint6 = textPaint;
        String str3 = sharingScreenString;
        String str4 = videoOnPauseString2;
        AnonymousClass1 r33 = r0;
        TLRPC.Chat chat2 = chat;
        AnonymousClass1 r0 = new VoIPTextureView(this, parentContainer2.getContext(), false, false, true, true) {
            float overlayIconAlphaFrom;
            final /* synthetic */ GroupCallMiniTextureView this$0;

            {
                this.this$0 = this$0;
            }

            public void animateToLayout() {
                super.animateToLayout();
                this.overlayIconAlphaFrom = this.this$0.overlayIconAlpha;
            }

            /* access modifiers changed from: protected */
            public void updateRendererSize() {
                super.updateRendererSize();
                if (this.this$0.blurredFlippingStub != null && this.this$0.blurredFlippingStub.getParent() != null) {
                    this.this$0.blurredFlippingStub.getLayoutParams().width = this.this$0.textureView.renderer.getMeasuredWidth();
                    this.this$0.blurredFlippingStub.getLayoutParams().height = this.this$0.textureView.renderer.getMeasuredHeight();
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                float y;
                int size;
                float smallProgress;
                float smallProgress2;
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
                    if (this.this$0.thumb != null) {
                        canvas.save();
                        canvas2.scale(this.currentThumbScale, this.currentThumbScale, ((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f);
                        if (this.this$0.thumbPaint == null) {
                            this.this$0.thumbPaint = new Paint(1);
                            this.this$0.thumbPaint.setFilterBitmap(true);
                        }
                        canvas2.drawBitmap(this.this$0.thumb, ((float) (getMeasuredWidth() - this.this$0.thumb.getWidth())) / 2.0f, ((float) (getMeasuredHeight() - this.this$0.thumb.getHeight())) / 2.0f, this.this$0.thumbPaint);
                        canvas.restore();
                    } else {
                        this.this$0.imageReceiver.setImageCoords(this.currentClipHorizontal, this.currentClipVertical, ((float) getMeasuredWidth()) - (this.currentClipHorizontal * 2.0f), ((float) getMeasuredHeight()) - (this.currentClipVertical * 2.0f));
                        this.this$0.imageReceiver.setAlpha(this.this$0.progressToBackground);
                        this.this$0.imageReceiver.draw(canvas2);
                    }
                    if (this.this$0.participant == call4.videoNotAvailableParticipant) {
                        if (this.this$0.showingInFullscreen || !groupCallRenderersContainer.inFullscreenMode) {
                            float iconSize = (float) AndroidUtilities.dp(48.0f);
                            textPaint2.setAlpha(255);
                            canvas.save();
                            canvas2.translate((((((float) getMeasuredWidth()) - iconSize) / 2.0f) - (((float) AndroidUtilities.dp(400.0f)) / 2.0f)) + (iconSize / 2.0f), (((float) (getMeasuredHeight() / 2)) - iconSize) + iconSize + ((float) AndroidUtilities.dp(10.0f)));
                            noVideoLayout.draw(canvas2);
                            canvas.restore();
                        }
                        if (this.this$0.stopSharingTextView.getVisibility() != 4) {
                            this.this$0.stopSharingTextView.setVisibility(4);
                        }
                    } else if (!this.this$0.participant.presentation || !this.this$0.participant.participant.self) {
                        if (this.this$0.stopSharingTextView.getVisibility() != 4) {
                            this.this$0.stopSharingTextView.setVisibility(4);
                        }
                        groupCallActivity.cellFlickerDrawable.draw(canvas2, this.this$0);
                    } else {
                        if (this.this$0.stopSharingTextView.getVisibility() != 0) {
                            this.this$0.stopSharingTextView.setVisibility(0);
                            this.this$0.stopSharingTextView.setScaleX(1.0f);
                            this.this$0.stopSharingTextView.setScaleY(1.0f);
                        }
                        float progressToFullscreen = this.this$0.drawFirst ? 0.0f : groupCallRenderersContainer.progressToFullscreenMode;
                        int size2 = AndroidUtilities.dp(33.0f);
                        if (this.this$0.animateToFullscreen || this.this$0.showingInFullscreen) {
                            size = (int) (((float) size2) + ((float) AndroidUtilities.dp(10.0f)) + (((float) AndroidUtilities.dp(39.0f)) * groupCallRenderersContainer.progressToFullscreenMode));
                        } else {
                            size = (int) (((float) size2) + (((float) AndroidUtilities.dp(10.0f)) * Math.max(1.0f - groupCallRenderersContainer.progressToFullscreenMode, (this.this$0.showingAsScrimView || this.this$0.animateToScrimView) ? groupCallRenderersContainer.progressToScrimView : 0.0f)));
                        }
                        int x = (getMeasuredWidth() - size) / 2;
                        float scrimProgress = (this.this$0.showingAsScrimView || this.this$0.animateToScrimView) ? groupCallRenderersContainer.progressToScrimView : 0.0f;
                        if (this.this$0.showingInFullscreen) {
                            smallProgress2 = progressToFullscreen;
                            smallProgress = progressToFullscreen;
                        } else {
                            smallProgress = this.this$0.animateToFullscreen ? groupCallRenderersContainer.progressToFullscreenMode : scrimProgress;
                            smallProgress2 = (this.this$0.showingAsScrimView || this.this$0.animateToScrimView) ? groupCallRenderersContainer.progressToScrimView : groupCallRenderersContainer.progressToFullscreenMode;
                        }
                        int y2 = (int) ((((float) (((getMeasuredHeight() - size) / 2) - AndroidUtilities.dp(28.0f))) - ((((float) AndroidUtilities.dp(17.0f)) + (((float) AndroidUtilities.dp(74.0f)) * ((this.this$0.showingInFullscreen || this.this$0.animateToFullscreen) ? groupCallRenderersContainer.progressToFullscreenMode : 0.0f))) * smallProgress)) + (((float) AndroidUtilities.dp(17.0f)) * smallProgress2));
                        this.this$0.castingScreenDrawable.setBounds(x, y2, x + size, y2 + size);
                        this.this$0.castingScreenDrawable.draw(canvas2);
                        if (groupCallRenderersContainer.progressToFullscreenMode > 0.0f || scrimProgress > 0.0f) {
                            float alpha = Math.max(groupCallRenderersContainer.progressToFullscreenMode, scrimProgress) * smallProgress;
                            textPaint3.setAlpha((int) (alpha * 255.0f));
                            if (this.this$0.animateToFullscreen || this.this$0.showingInFullscreen) {
                                this.this$0.stopSharingTextView.setAlpha((1.0f - scrimProgress) * alpha);
                            } else {
                                this.this$0.stopSharingTextView.setAlpha(0.0f);
                            }
                            canvas2.drawText(str2, (((float) x) - (measureText2 / 2.0f)) + (((float) size) / 2.0f), (float) (y2 + size + AndroidUtilities.dp(32.0f)), textPaint3);
                        } else {
                            this.this$0.stopSharingTextView.setAlpha(0.0f);
                        }
                        this.this$0.stopSharingTextView.setTranslationY((((float) ((y2 + size) + AndroidUtilities.dp(72.0f))) + this.this$0.swipeToBackDy) - this.currentClipVertical);
                        this.this$0.stopSharingTextView.setTranslationX((((float) (getMeasuredWidth() - this.this$0.stopSharingTextView.getMeasuredWidth())) / 2.0f) - this.currentClipHorizontal);
                        if (groupCallRenderersContainer.progressToFullscreenMode < 1.0f && scrimProgress < 1.0f) {
                            TextPaint textPaint = textPaint2;
                            double max = (double) Math.max(groupCallRenderersContainer.progressToFullscreenMode, scrimProgress);
                            Double.isNaN(max);
                            textPaint.setAlpha((int) ((1.0d - max) * 255.0d));
                            canvas.save();
                            canvas2.translate((((float) x) - (((float) AndroidUtilities.dp(400.0f)) / 2.0f)) + (((float) size) / 2.0f), (float) (y2 + size + AndroidUtilities.dp(10.0f)));
                            staticLayout.draw(canvas2);
                            canvas.restore();
                        }
                    }
                    invalidate();
                }
                this.this$0.noRtmpStreamTextView.setTranslationY(((((float) (getMeasuredHeight() - this.this$0.noRtmpStreamTextView.getMeasuredHeight())) / 2.0f) + this.this$0.swipeToBackDy) - this.currentClipVertical);
                this.this$0.noRtmpStreamTextView.setTranslationX((((float) (getMeasuredWidth() - this.this$0.noRtmpStreamTextView.getMeasuredWidth())) / 2.0f) - this.currentClipHorizontal);
                if (!(this.this$0.blurredFlippingStub == null || this.this$0.blurredFlippingStub.getParent() == null)) {
                    this.this$0.blurredFlippingStub.setScaleX(this.this$0.textureView.renderer.getScaleX());
                    this.this$0.blurredFlippingStub.setScaleY(this.this$0.textureView.renderer.getScaleY());
                }
                super.dispatchDraw(canvas);
                float y3 = (((float) getMeasuredHeight()) - this.currentClipVertical) - ((float) AndroidUtilities.dp(80.0f));
                if (this.this$0.participant != call4.videoNotAvailableParticipant) {
                    canvas.save();
                    if ((this.this$0.showingInFullscreen || this.this$0.animateToFullscreen) && !GroupCallActivity.isLandscapeMode && !GroupCallActivity.isTabletMode) {
                        y3 -= (((float) AndroidUtilities.dp(90.0f)) * groupCallRenderersContainer.progressToFullscreenMode) * (1.0f - groupCallRenderersContainer.progressToHideUi);
                    }
                    canvas2.translate(0.0f, y3);
                    canvas2.drawPaint(this.this$0.gradientPaint);
                    canvas.restore();
                }
                if (this.this$0.videoIsPaused || this.this$0.videoIsPausedProgress != 0.0f) {
                    if (this.this$0.videoIsPaused && this.this$0.videoIsPausedProgress != 1.0f) {
                        GroupCallMiniTextureView.access$716(this.this$0, 0.064f);
                        if (this.this$0.videoIsPausedProgress > 1.0f) {
                            float unused2 = this.this$0.videoIsPausedProgress = 1.0f;
                        } else {
                            invalidate();
                        }
                    } else if (!this.this$0.videoIsPaused && this.this$0.videoIsPausedProgress != 0.0f) {
                        GroupCallMiniTextureView.access$724(this.this$0, 0.064f);
                        if (this.this$0.videoIsPausedProgress < 0.0f) {
                            float unused3 = this.this$0.videoIsPausedProgress = 0.0f;
                        } else {
                            invalidate();
                        }
                    }
                    float a = this.this$0.videoIsPausedProgress * (isInAnimation() ? (this.overlayIconAlphaFrom * (1.0f - this.animationProgress)) + (this.this$0.overlayIconAlpha * this.animationProgress) : this.this$0.overlayIconAlpha);
                    if (a > 0.0f) {
                        float iconSize2 = (float) AndroidUtilities.dp(48.0f);
                        float x2 = (((float) getMeasuredWidth()) - iconSize2) / 2.0f;
                        float y4 = (((float) getMeasuredHeight()) - iconSize2) / 2.0f;
                        if (this.this$0.participant == call4.videoNotAvailableParticipant) {
                            y = y4 - (iconSize2 / 2.5f);
                        } else {
                            y = y4;
                        }
                        AndroidUtilities.rectTmp.set((float) ((int) x2), (float) ((int) y), (float) ((int) (x2 + iconSize2)), (float) ((int) (y + iconSize2)));
                        if (a != 1.0f) {
                            canvas2.saveLayerAlpha(AndroidUtilities.rectTmp, (int) (a * 255.0f), 31);
                        } else {
                            canvas.save();
                        }
                        this.this$0.pausedVideoDrawable.setBounds((int) AndroidUtilities.rectTmp.left, (int) AndroidUtilities.rectTmp.top, (int) AndroidUtilities.rectTmp.right, (int) AndroidUtilities.rectTmp.bottom);
                        this.this$0.pausedVideoDrawable.draw(canvas2);
                        canvas.restore();
                        float a2 = a * groupCallRenderersContainer.progressToFullscreenMode;
                        if (a2 > 0.0f && this.this$0.participant != call4.videoNotAvailableParticipant) {
                            textPaint2.setAlpha((int) (255.0f * a2));
                            canvas2.drawText(str, (x2 - (measureText / 2.0f)) + (iconSize2 / 2.0f), y + iconSize2 + ((float) AndroidUtilities.dp(16.0f)), textPaint2);
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (!this.this$0.inPinchToZoom || child != this.this$0.textureView.renderer) {
                    return super.drawChild(canvas, child, drawingTime);
                }
                canvas.save();
                canvas.scale(this.this$0.pinchScale, this.this$0.pinchScale, this.this$0.pinchCenterX, this.this$0.pinchCenterY);
                canvas.translate(this.this$0.pinchTranslationX, this.this$0.pinchTranslationY);
                boolean b = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return b;
            }

            public void invalidate() {
                super.invalidate();
                boolean unused = this.this$0.invalidateFromChild = true;
                this.this$0.invalidate();
                boolean unused2 = this.this$0.invalidateFromChild = false;
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                if (this.this$0.attached && this.this$0.checkScale && this.renderer.rotatedFrameHeight != 0 && this.renderer.rotatedFrameWidth != 0) {
                    if (this.this$0.showingAsScrimView) {
                        this.this$0.textureView.scaleType = SCALE_TYPE_FIT;
                    } else if (this.this$0.showingInFullscreen) {
                        this.this$0.textureView.scaleType = SCALE_TYPE_FIT;
                    } else if (groupCallRenderersContainer.inFullscreenMode) {
                        this.this$0.textureView.scaleType = SCALE_TYPE_FILL;
                    } else if (this.this$0.participant.presentation) {
                        this.this$0.textureView.scaleType = SCALE_TYPE_FIT;
                    } else {
                        this.this$0.textureView.scaleType = SCALE_TYPE_ADAPTIVE;
                    }
                    boolean unused = this.this$0.checkScale = false;
                }
                super.onLayout(changed, left, top, right, bottom);
                if (this.renderer.rotatedFrameHeight != 0 && this.renderer.rotatedFrameWidth != 0 && this.this$0.participant != null) {
                    this.this$0.participant.setAspectRatio(this.renderer.rotatedFrameWidth, this.renderer.rotatedFrameHeight, call4);
                }
            }

            public void requestLayout() {
                this.this$0.requestLayout();
                super.requestLayout();
            }

            /* access modifiers changed from: protected */
            public void onFirstFrameRendered() {
                invalidate();
                ChatObject.Call call = call4;
                if (call != null && call.call.rtmp_stream && this.this$0.postedNoRtmpStreamCallback) {
                    AndroidUtilities.cancelRunOnUIThread(this.this$0.noRtmpStreamCallback);
                    boolean unused = this.this$0.postedNoRtmpStreamCallback = false;
                    this.this$0.noRtmpStreamTextView.animate().cancel();
                    this.this$0.noRtmpStreamTextView.animate().alpha(0.0f).setDuration(150).start();
                    this.this$0.textureView.animate().cancel();
                    this.this$0.textureView.animate().alpha(1.0f).setDuration(150).start();
                }
                if (!this.this$0.videoIsPaused && this.renderer.getAlpha() != 1.0f) {
                    this.renderer.animate().setDuration(300).alpha(1.0f);
                }
                if (!(this.blurRenderer == null || this.blurRenderer.getAlpha() == 1.0f)) {
                    this.blurRenderer.animate().setDuration(300).alpha(1.0f);
                }
                if (!(this.this$0.blurredFlippingStub == null || this.this$0.blurredFlippingStub.getParent() == null)) {
                    if (this.this$0.blurredFlippingStub.getAlpha() == 1.0f) {
                        this.this$0.blurredFlippingStub.animate().alpha(0.0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (AnonymousClass1.this.this$0.blurredFlippingStub.getParent() != null) {
                                    AnonymousClass1.this.this$0.textureView.removeView(AnonymousClass1.this.this$0.blurredFlippingStub);
                                }
                            }
                        }).start();
                    } else if (this.this$0.blurredFlippingStub.getParent() != null) {
                        this.this$0.textureView.removeView(this.this$0.blurredFlippingStub);
                    }
                }
                if (this.renderer.rotatedFrameHeight != 0 && this.renderer.rotatedFrameWidth != 0 && this.this$0.participant != null) {
                    this.this$0.participant.setAspectRatio(this.renderer.rotatedFrameWidth, this.renderer.rotatedFrameHeight, call4);
                }
            }
        };
        AnonymousClass1 r1 = r33;
        this.textureView = r1;
        r1.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        this.parentContainer = parentContainer2;
        this.attachedRenderers = attachedRenderers2;
        this.activity = activity2;
        this.textureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
            public void onFirstFrameRendered() {
                for (int i = 0; i < GroupCallMiniTextureView.this.onFirstFrameRunnables.size(); i++) {
                    AndroidUtilities.cancelRunOnUIThread(GroupCallMiniTextureView.this.onFirstFrameRunnables.get(i));
                    GroupCallMiniTextureView.this.onFirstFrameRunnables.get(i).run();
                }
                GroupCallMiniTextureView.this.onFirstFrameRunnables.clear();
            }

            public void onFrameResolutionChanged(int videoWidth, int videoHeight, int rotation) {
            }
        });
        this.textureView.attachBackgroundRenderer();
        setClipChildren(false);
        this.textureView.renderer.setAlpha(0.0f);
        addView(this.textureView);
        NoVideoStubLayout noVideoStubLayout2 = new NoVideoStubLayout(getContext());
        this.noVideoStubLayout = noVideoStubLayout2;
        addView(noVideoStubLayout2);
        SimpleTextView simpleTextView = new SimpleTextView(parentContainer2.getContext());
        this.nameView = simpleTextView;
        simpleTextView.setTextSize(13);
        simpleTextView.setTextColor(ColorUtils.setAlphaComponent(-1, 229));
        simpleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        simpleTextView.setFullTextMaxLines(1);
        simpleTextView.setBuildFullLayout(true);
        FrameLayout frameLayout = new FrameLayout(parentContainer2.getContext());
        this.infoContainer = frameLayout;
        frameLayout.addView(simpleTextView, LayoutHelper.createFrame(-1, -2.0f, 19, 32.0f, 0.0f, 8.0f, 0.0f));
        addView(this.infoContainer, LayoutHelper.createFrame(-1, 32.0f));
        this.speakingPaint.setStyle(Paint.Style.STROKE);
        this.speakingPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.speakingPaint.setColor(Theme.getColor("voipgroup_speakingText"));
        this.infoContainer.setClipChildren(false);
        RLottieImageView rLottieImageView = new RLottieImageView(parentContainer2.getContext());
        this.micIconView = rLottieImageView;
        addView(rLottieImageView, LayoutHelper.createFrame(24, 24.0f, 0, 4.0f, 6.0f, 4.0f, 0.0f));
        ImageView imageView = new ImageView(parentContainer2.getContext());
        this.screencastIcon = imageView;
        addView(imageView, LayoutHelper.createFrame(24, 24.0f, 0, 4.0f, 6.0f, 4.0f, 0.0f));
        imageView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
        imageView.setImageDrawable(ContextCompat.getDrawable(parentContainer2.getContext(), NUM));
        imageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        Drawable rippleDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(19.0f), 0, ColorUtils.setAlphaComponent(-1, 100));
        AnonymousClass3 r9 = new TextView(parentContainer2.getContext()) {
            public boolean onTouchEvent(MotionEvent event) {
                if (Math.abs(GroupCallMiniTextureView.this.stopSharingTextView.getAlpha() - 1.0f) > 0.001f) {
                    return false;
                }
                return super.onTouchEvent(event);
            }
        };
        this.stopSharingTextView = r9;
        r9.setText(LocaleController.getString("VoipVideoScreenStopSharing", NUM));
        this.stopSharingTextView.setTextSize(1, 15.0f);
        this.stopSharingTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.stopSharingTextView.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
        this.stopSharingTextView.setTextColor(-1);
        this.stopSharingTextView.setBackground(rippleDrawable);
        this.stopSharingTextView.setGravity(17);
        this.stopSharingTextView.setOnClickListener(new GroupCallMiniTextureView$$ExternalSyntheticLambda3(this));
        addView(this.stopSharingTextView, LayoutHelper.createFrame(-2, 38, 51));
        TextView textView = new TextView(parentContainer2.getContext());
        this.noRtmpStreamTextView = textView;
        textView.setTextSize(1, 15.0f);
        this.noRtmpStreamTextView.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
        this.noRtmpStreamTextView.setTextColor(Theme.getColor("voipgroup_lastSeenText"));
        this.noRtmpStreamTextView.setBackground(rippleDrawable);
        this.noRtmpStreamTextView.setGravity(17);
        this.noRtmpStreamTextView.setAlpha(0.0f);
        if (ChatObject.canManageCalls(chat2)) {
            this.noRtmpStreamTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString(NUM)));
            TLRPC.Chat chat3 = chat2;
        } else {
            this.noRtmpStreamTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoRtmpStreamFromAppViewer", NUM, chat2.title)));
        }
        addView(this.noRtmpStreamTextView, LayoutHelper.createFrame(-2, -2, 51));
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-voip-GroupCallMiniTextureView  reason: not valid java name */
    public /* synthetic */ void m4547x550a784c(View v) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().stopScreenCapture();
        }
        this.stopSharingTextView.animate().alpha(0.0f).scaleX(0.0f).scaleY(0.0f).setDuration(180).start();
    }

    public boolean isInsideStopScreenButton(float x, float y) {
        this.stopSharingTextView.getHitRect(this.rect);
        return this.rect.contains((int) x, (int) y);
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
                    y -= (((float) AndroidUtilities.dp(90.0f)) * this.parentContainer.progressToFullscreenMode) * (1.0f - this.parentContainer.progressToHideUi);
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
                this.screencastIcon.setAlpha(Math.min(1.0f - this.parentContainer.progressToFullscreenMode, 1.0f - this.parentContainer.progressToScrimView));
            }
            this.infoContainer.setTranslationY(y);
            this.infoContainer.setTranslationX(this.drawFirst ? 0.0f : ((float) AndroidUtilities.dp(6.0f)) * this.parentContainer.progressToFullscreenMode);
        }
        super.dispatchDraw(canvas);
        if (this.attached) {
            GroupCallStatusIcon groupCallStatusIcon = this.statusIcon;
            if (groupCallStatusIcon != null) {
                if (groupCallStatusIcon.isSpeaking) {
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
                if (!this.statusIcon.isSpeaking) {
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
            float selectionProgress = this.progressToSpeaking * (1.0f - this.parentContainer.progressToFullscreenMode) * (1.0f - this.parentContainer.progressToScrimView);
            if (this.progressToSpeaking > 0.0f) {
                this.speakingPaint.setAlpha((int) (255.0f * selectionProgress));
                float scale = (Math.max(0.0f, 1.0f - (Math.abs(this.swipeToBackDy) / ((float) AndroidUtilities.dp(300.0f)))) * 0.1f) + 0.9f;
                canvas.save();
                AndroidUtilities.rectTmp.set(this.textureView.getX() + this.textureView.currentClipHorizontal, this.textureView.getY() + this.textureView.currentClipVertical, (this.textureView.getX() + ((float) this.textureView.getMeasuredWidth())) - this.textureView.currentClipHorizontal, (this.textureView.getY() + ((float) this.textureView.getMeasuredHeight())) - this.textureView.currentClipVertical);
                canvas.scale(scale, scale, AndroidUtilities.rectTmp.centerX(), AndroidUtilities.rectTmp.centerY());
                canvas.translate(0.0f, this.swipeToBackDy);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, this.textureView.roundRadius, this.textureView.roundRadius, this.speakingPaint);
                canvas.restore();
            }
        }
    }

    public void getRenderBufferBitmap(GlGenericDrawer.TextureCallback callback) {
        this.textureView.renderer.getRenderBufferBitmap(callback);
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (!this.swipeToBack || (child != this.textureView && child != this.noVideoStubLayout)) {
            return super.drawChild(canvas, child, drawingTime);
        }
        float scale = (Math.max(0.0f, 1.0f - (Math.abs(this.swipeToBackDy) / ((float) AndroidUtilities.dp(300.0f)))) * 0.1f) + 0.9f;
        canvas.save();
        canvas.scale(scale, scale, child.getX() + (((float) child.getMeasuredWidth()) / 2.0f), child.getY() + (((float) child.getMeasuredHeight()) / 2.0f));
        canvas.translate(0.0f, this.swipeToBackDy);
        boolean b = super.drawChild(canvas, child, drawingTime);
        canvas.restore();
        return b;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int spanCountTotal;
        float listSize;
        float h;
        float w;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.infoContainer.getLayoutParams();
        int i = layoutParams.leftMargin;
        float nameScale = 1.0f;
        if (this.call.call.rtmp_stream) {
            nameScale = 0.0f;
        }
        if (this.lastLandscapeMode != GroupCallActivity.isLandscapeMode) {
            this.checkScale = true;
            this.lastLandscapeMode = GroupCallActivity.isLandscapeMode;
        }
        int dp = AndroidUtilities.dp(2.0f);
        layoutParams.rightMargin = dp;
        layoutParams.leftMargin = dp;
        if (this.updateNextLayoutAnimated) {
            this.nameView.animate().scaleX(nameScale).scaleY(nameScale).start();
            this.micIconView.animate().scaleX(nameScale).scaleY(nameScale).start();
        } else {
            this.nameView.animate().cancel();
            this.nameView.setScaleX(nameScale);
            this.nameView.setScaleY(nameScale);
            this.micIconView.animate().cancel();
            this.micIconView.setScaleX(nameScale);
            this.micIconView.setScaleY(nameScale);
            this.infoContainer.animate().cancel();
        }
        int i2 = 0;
        this.updateNextLayoutAnimated = false;
        if (this.showingInFullscreen) {
            updateSize(0);
            this.overlayIconAlpha = 1.0f;
            if (GroupCallActivity.isTabletMode) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(328.0f), NUM), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec) - AndroidUtilities.dp(4.0f), NUM));
            } else if (!GroupCallActivity.isLandscapeMode) {
                int h2 = View.MeasureSpec.getSize(heightMeasureSpec);
                if (!this.call.call.rtmp_stream) {
                    h2 -= AndroidUtilities.dp(92.0f);
                }
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(h2, NUM));
            } else {
                int w2 = View.MeasureSpec.getSize(widthMeasureSpec);
                if (!this.call.call.rtmp_stream) {
                    w2 -= AndroidUtilities.dp(92.0f);
                }
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(w2, NUM), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), NUM));
            }
        } else if (this.showingAsScrimView) {
            this.overlayIconAlpha = 1.0f;
            int size = Math.min(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec)) - (AndroidUtilities.dp(14.0f) * 2);
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(getPaddingBottom() + size, NUM));
        } else if (this.useSpanSize) {
            this.overlayIconAlpha = 1.0f;
            if (!GroupCallActivity.isTabletMode || this.tabletGridView == null) {
                spanCountTotal = GroupCallActivity.isLandscapeMode != 0 ? 6 : 2;
            } else {
                spanCountTotal = 6;
            }
            if (this.tabletGridView != null) {
                listSize = (float) (View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(344.0f));
            } else if (GroupCallActivity.isTabletMode) {
                listSize = (float) AndroidUtilities.dp(320.0f);
            } else {
                int size2 = View.MeasureSpec.getSize(widthMeasureSpec) - (AndroidUtilities.dp(14.0f) * 2);
                if (GroupCallActivity.isLandscapeMode) {
                    i2 = -AndroidUtilities.dp(90.0f);
                }
                listSize = (float) (size2 + i2);
            }
            float w3 = (this.spanCount / ((float) spanCountTotal)) * listSize;
            GroupCallGridCell groupCallGridCell = this.tabletGridView;
            if (groupCallGridCell != null) {
                h = groupCallGridCell.getItemHeight() - ((float) AndroidUtilities.dp(4.0f));
                w = w3 - ((float) AndroidUtilities.dp(4.0f));
            } else {
                if (GroupCallActivity.isTabletMode) {
                    h = listSize / 2.0f;
                } else {
                    h = listSize / ((float) (GroupCallActivity.isLandscapeMode ? 3 : 2));
                }
                w = w3 - ((float) AndroidUtilities.dp(2.0f));
            }
            float layoutContainerW = w;
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.infoContainer.getLayoutParams();
            if (this.screencastIcon.getVisibility() == 0) {
                layoutContainerW -= (float) AndroidUtilities.dp(28.0f);
            }
            updateSize((int) layoutContainerW);
            layoutParams2.width = (int) (layoutContainerW - ((float) (layoutParams2.leftMargin * 2)));
            super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) w, NUM), View.MeasureSpec.makeMeasureSpec((int) h, NUM));
        } else {
            this.overlayIconAlpha = 0.0f;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        int size3 = View.MeasureSpec.getSize(heightMeasureSpec) + (View.MeasureSpec.getSize(widthMeasureSpec) << 16);
        if (this.lastSize != size3) {
            this.lastSize = size3;
            LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, (float) AndroidUtilities.dp(120.0f), 0, ColorUtils.setAlphaComponent(-16777216, 120), Shader.TileMode.CLAMP);
            this.gradientShader = linearGradient;
            this.gradientPaint.setShader(linearGradient);
        }
        this.nameView.setPivotX(0.0f);
        SimpleTextView simpleTextView = this.nameView;
        simpleTextView.setPivotY(((float) simpleTextView.getMeasuredHeight()) / 2.0f);
    }

    public static GroupCallMiniTextureView getOrCreate(ArrayList<GroupCallMiniTextureView> attachedRenderers2, GroupCallRenderersContainer renderersContainer, GroupCallGridCell primaryView2, GroupCallFullscreenAdapter.GroupCallUserCell secondaryView2, GroupCallGridCell tabletGridView2, ChatObject.VideoParticipant participant2, ChatObject.Call call2, GroupCallActivity activity2) {
        GroupCallMiniTextureView renderer = null;
        int i = 0;
        while (true) {
            if (i >= attachedRenderers2.size()) {
                break;
            } else if (participant2.equals(attachedRenderers2.get(i).participant)) {
                renderer = attachedRenderers2.get(i);
                break;
            } else {
                i++;
            }
        }
        if (renderer == null) {
            renderer = new GroupCallMiniTextureView(renderersContainer, attachedRenderers2, call2, activity2);
        }
        if (primaryView2 != null) {
            renderer.setPrimaryView(primaryView2);
        }
        if (secondaryView2 != null) {
            renderer.setSecondaryView(secondaryView2);
        }
        if (tabletGridView2 != null) {
            renderer.setTabletGridView(tabletGridView2);
        }
        return renderer;
    }

    public void setTabletGridView(GroupCallGridCell tabletGridView2) {
        if (this.tabletGridView != tabletGridView2) {
            this.tabletGridView = tabletGridView2;
            updateAttachState(true);
        }
    }

    public void setPrimaryView(GroupCallGridCell primaryView2) {
        if (this.primaryView != primaryView2) {
            this.primaryView = primaryView2;
            this.checkScale = true;
            updateAttachState(true);
        }
    }

    public void setSecondaryView(GroupCallFullscreenAdapter.GroupCallUserCell secondaryView2) {
        if (this.secondaryView != secondaryView2) {
            this.secondaryView = secondaryView2;
            this.checkScale = true;
            updateAttachState(true);
        }
    }

    public void setShowingAsScrimView(boolean showing, boolean animated) {
        this.showingAsScrimView = showing;
        updateAttachState(animated);
    }

    public void setShowingInFullscreen(boolean showing, boolean animated) {
        if (this.showingInFullscreen != showing) {
            this.showingInFullscreen = showing;
            this.checkScale = true;
            updateAttachState(animated);
        }
    }

    public void setFullscreenMode(boolean fullscreenMode, boolean animated) {
        if (this.isFullscreenMode != fullscreenMode) {
            this.isFullscreenMode = fullscreenMode;
            updateAttachState(!(this.primaryView == null && this.tabletGridView == null) && animated);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v21, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v29, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v33, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v34, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x02e0  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x02f4  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x030a  */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x0735  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateAttachState(boolean r30) {
        /*
            r29 = this;
            r0 = r29
            boolean r1 = r0.forceDetached
            if (r1 == 0) goto L_0x0007
            return
        L_0x0007:
            org.telegram.messenger.ChatObject$Call r1 = r0.call
            org.telegram.tgnet.TLRPC$GroupCall r1 = r1.call
            boolean r1 = r1.rtmp_stream
            r2 = 0
            if (r1 == 0) goto L_0x0022
            boolean r1 = r0.showingInFullscreen
            if (r1 == 0) goto L_0x0017
            r1 = 1108344832(0x42100000, float:36.0)
            goto L_0x0019
        L_0x0017:
            r1 = 1101529088(0x41a80000, float:21.0)
        L_0x0019:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            android.widget.TextView r3 = r0.noRtmpStreamTextView
            r3.setPadding(r1, r2, r1, r2)
        L_0x0022:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.participant
            if (r1 != 0) goto L_0x004e
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r0.primaryView
            if (r1 != 0) goto L_0x0032
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r3 = r0.secondaryView
            if (r3 != 0) goto L_0x0032
            org.telegram.ui.Components.voip.GroupCallGridCell r3 = r0.tabletGridView
            if (r3 == 0) goto L_0x004e
        L_0x0032:
            if (r1 == 0) goto L_0x003b
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.getParticipant()
            r0.participant = r1
            goto L_0x004e
        L_0x003b:
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r0.tabletGridView
            if (r1 == 0) goto L_0x0046
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.getParticipant()
            r0.participant = r1
            goto L_0x004e
        L_0x0046:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r1 = r0.secondaryView
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.getVideoParticipant()
            r0.participant = r1
        L_0x004e:
            r1 = 0
            boolean r3 = r0.attached
            r5 = 2
            r6 = 1056964608(0x3var_, float:0.5)
            r7 = 0
            r8 = 0
            r9 = 1065353216(0x3var_, float:1.0)
            r10 = 1
            if (r3 == 0) goto L_0x013c
            boolean r11 = r0.showingInFullscreen
            if (r11 != 0) goto L_0x013c
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r3 != 0) goto L_0x0067
            r3 = 1
            goto L_0x0068
        L_0x0067:
            r3 = 0
        L_0x0068:
            boolean r11 = org.telegram.ui.GroupCallActivity.paused
            if (r11 != 0) goto L_0x0090
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r0.participant
            if (r11 == 0) goto L_0x0090
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r12 = r0.secondaryView
            if (r12 != 0) goto L_0x0091
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11 = r11.participant
            org.telegram.messenger.ChatObject$VideoParticipant r12 = r0.participant
            boolean r12 = r12.presentation
            org.telegram.messenger.ChatObject$Call r13 = r0.call
            boolean r11 = org.telegram.messenger.ChatObject.Call.videoIsActive(r11, r12, r13)
            if (r11 == 0) goto L_0x0090
            org.telegram.messenger.ChatObject$Call r11 = r0.call
            boolean r11 = r11.canStreamVideo
            if (r11 != 0) goto L_0x0091
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r0.participant
            org.telegram.messenger.ChatObject$Call r12 = r0.call
            org.telegram.messenger.ChatObject$VideoParticipant r12 = r12.videoNotAvailableParticipant
            if (r11 == r12) goto L_0x0091
        L_0x0090:
            r3 = 1
        L_0x0091:
            if (r3 != 0) goto L_0x00a7
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r0.primaryView
            if (r11 != 0) goto L_0x02d5
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r11 = r0.secondaryView
            if (r11 != 0) goto L_0x02d5
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r0.tabletGridView
            if (r11 != 0) goto L_0x02d5
            boolean r11 = r0.showingAsScrimView
            if (r11 != 0) goto L_0x02d5
            boolean r11 = r0.animateToScrimView
            if (r11 != 0) goto L_0x02d5
        L_0x00a7:
            r0.attached = r2
            r29.saveThumb()
            org.telegram.ui.Components.voip.VoIPTextureView r11 = r0.textureView
            android.animation.ValueAnimator r11 = r11.currentAnimation
            if (r11 != 0) goto L_0x00de
            if (r3 == 0) goto L_0x00de
            r11 = r29
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r12 = r0.parentContainer
            r12.detach(r11)
            android.view.ViewPropertyAnimator r12 = r29.animate()
            android.view.ViewPropertyAnimator r12 = r12.scaleX(r6)
            android.view.ViewPropertyAnimator r6 = r12.scaleY(r6)
            android.view.ViewPropertyAnimator r6 = r6.alpha(r8)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$4 r12 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$4
            r12.<init>(r11)
            android.view.ViewPropertyAnimator r6 = r6.setListener(r12)
            r12 = 150(0x96, double:7.4E-322)
            android.view.ViewPropertyAnimator r6 = r6.setDuration(r12)
            r6.start()
            goto L_0x00fc
        L_0x00de:
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r6 = r0.parentContainer
            boolean r6 = r6.inLayout
            if (r6 == 0) goto L_0x00ef
            r6 = r29
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda7 r11 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda7
            r11.<init>(r0, r6)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r11)
            goto L_0x00f4
        L_0x00ef:
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r6 = r0.parentContainer
            r6.removeView(r0)
        L_0x00f4:
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r6 = r0.parentContainer
            r6.detach(r0)
            r29.release()
        L_0x00fc:
            org.telegram.messenger.ChatObject$VideoParticipant r6 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r6 = r6.participant
            boolean r6 = r6.self
            if (r6 == 0) goto L_0x0116
            org.telegram.messenger.voip.VoIPService r6 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r6 == 0) goto L_0x012b
            org.telegram.messenger.voip.VoIPService r6 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r0.participant
            boolean r11 = r11.presentation
            r6.setLocalSink(r7, r11)
            goto L_0x012b
        L_0x0116:
            org.telegram.messenger.voip.VoIPService r6 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r6 == 0) goto L_0x012b
            org.telegram.messenger.voip.VoIPService r6 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11 = r11.participant
            org.telegram.messenger.ChatObject$VideoParticipant r12 = r0.participant
            boolean r12 = r12.presentation
            r6.removeRemoteSink(r11, r12)
        L_0x012b:
            r29.invalidate()
            android.animation.ValueAnimator r6 = r0.noVideoStubAnimator
            if (r6 == 0) goto L_0x02d5
            r6.removeAllListeners()
            android.animation.ValueAnimator r6 = r0.noVideoStubAnimator
            r6.cancel()
            goto L_0x02d5
        L_0x013c:
            if (r3 != 0) goto L_0x02d5
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r3 != 0) goto L_0x0145
            return
        L_0x0145:
            org.telegram.ui.Components.voip.GroupCallGridCell r3 = r0.primaryView
            if (r3 != 0) goto L_0x0155
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r11 = r0.secondaryView
            if (r11 != 0) goto L_0x0155
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r0.tabletGridView
            if (r11 != 0) goto L_0x0155
            boolean r11 = r0.showingInFullscreen
            if (r11 == 0) goto L_0x02d6
        L_0x0155:
            if (r3 == 0) goto L_0x015e
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r3.getParticipant()
            r0.participant = r3
            goto L_0x0173
        L_0x015e:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r3 = r0.secondaryView
            if (r3 == 0) goto L_0x0169
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r3.getVideoParticipant()
            r0.participant = r3
            goto L_0x0173
        L_0x0169:
            org.telegram.ui.Components.voip.GroupCallGridCell r3 = r0.tabletGridView
            if (r3 == 0) goto L_0x0173
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r3.getParticipant()
            r0.participant = r3
        L_0x0173:
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            boolean r3 = r3.self
            if (r3 == 0) goto L_0x0193
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r3 == 0) goto L_0x0191
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r0.participant
            boolean r11 = r11.presentation
            int r3 = r3.getVideoState(r11)
            if (r3 != r5) goto L_0x0191
            r3 = 1
            goto L_0x0192
        L_0x0191:
            r3 = 0
        L_0x0192:
            goto L_0x01b4
        L_0x0193:
            org.telegram.messenger.ChatObject$Call r3 = r0.call
            boolean r3 = r3.canStreamVideo
            if (r3 != 0) goto L_0x01a1
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.messenger.ChatObject$Call r11 = r0.call
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r11.videoNotAvailableParticipant
            if (r3 != r11) goto L_0x01b3
        L_0x01a1:
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r0.participant
            boolean r11 = r11.presentation
            org.telegram.messenger.ChatObject$Call r12 = r0.call
            boolean r3 = org.telegram.messenger.ChatObject.Call.videoIsActive(r3, r11, r12)
            if (r3 == 0) goto L_0x01b3
            r3 = 1
            goto L_0x01b4
        L_0x01b3:
            r3 = 0
        L_0x01b4:
            boolean r11 = r0.showingInFullscreen
            if (r11 != 0) goto L_0x01de
            org.telegram.messenger.voip.VoIPService r11 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r12 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r12.participant
            org.telegram.messenger.ChatObject$VideoParticipant r13 = r0.participant
            boolean r13 = r13.presentation
            boolean r11 = r11.isFullscreen(r12, r13)
            if (r11 != 0) goto L_0x02d6
            org.telegram.messenger.voip.VoIPService r11 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r12 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r12.participant
            org.telegram.messenger.ChatObject$VideoParticipant r13 = r0.participant
            boolean r13 = r13.presentation
            boolean r11 = r11.isFullscreen(r12, r13)
            if (r11 != 0) goto L_0x02d6
            if (r3 == 0) goto L_0x02d6
        L_0x01de:
            boolean r11 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r11 == 0) goto L_0x0208
            r11 = 0
        L_0x01e3:
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r12 = r0.attachedRenderers
            int r12 = r12.size()
            if (r11 >= r12) goto L_0x0208
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r12 = r0.attachedRenderers
            java.lang.Object r12 = r12.get(r11)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r12 = (org.telegram.ui.Components.voip.GroupCallMiniTextureView) r12
            org.telegram.messenger.ChatObject$VideoParticipant r12 = r12.participant
            org.telegram.messenger.ChatObject$VideoParticipant r13 = r0.participant
            boolean r12 = r12.equals(r13)
            if (r12 != 0) goto L_0x0200
            int r11 = r11 + 1
            goto L_0x01e3
        L_0x0200:
            java.lang.RuntimeException r2 = new java.lang.RuntimeException
            java.lang.String r4 = "try add two same renderers"
            r2.<init>(r4)
            throw r2
        L_0x0208:
            r1 = 1
            r0.attached = r10
            org.telegram.ui.GroupCallActivity r11 = r0.activity
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallStatusIcon> r11 = r11.statusIconPool
            int r11 = r11.size()
            if (r11 <= 0) goto L_0x022b
            org.telegram.ui.GroupCallActivity r11 = r0.activity
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallStatusIcon> r11 = r11.statusIconPool
            org.telegram.ui.GroupCallActivity r12 = r0.activity
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallStatusIcon> r12 = r12.statusIconPool
            int r12 = r12.size()
            int r12 = r12 - r10
            java.lang.Object r11 = r11.remove(r12)
            org.telegram.ui.Components.voip.GroupCallStatusIcon r11 = (org.telegram.ui.Components.voip.GroupCallStatusIcon) r11
            r0.statusIcon = r11
            goto L_0x0232
        L_0x022b:
            org.telegram.ui.Components.voip.GroupCallStatusIcon r11 = new org.telegram.ui.Components.voip.GroupCallStatusIcon
            r11.<init>()
            r0.statusIcon = r11
        L_0x0232:
            org.telegram.ui.Components.voip.GroupCallStatusIcon r11 = r0.statusIcon
            r11.setCallback(r0)
            org.telegram.ui.Components.voip.GroupCallStatusIcon r11 = r0.statusIcon
            org.telegram.ui.Components.RLottieImageView r12 = r0.micIconView
            r11.setImageView(r12)
            r0.updateIconColor(r2)
            android.view.ViewParent r11 = r29.getParent()
            if (r11 != 0) goto L_0x0259
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r11 = r0.parentContainer
            r12 = 51
            r13 = 46
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r13, (int) r12)
            r11.addView(r0, r12)
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r11 = r0.parentContainer
            r11.attach(r0)
        L_0x0259:
            r0.checkScale = r10
            r0.animateEnter = r2
            android.view.ViewPropertyAnimator r11 = r29.animate()
            android.view.ViewPropertyAnimator r11 = r11.setListener(r7)
            r11.cancel()
            org.telegram.ui.Components.voip.VoIPTextureView r11 = r0.textureView
            android.animation.ValueAnimator r11 = r11.currentAnimation
            if (r11 != 0) goto L_0x02b0
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r11 = r0.secondaryView
            if (r11 == 0) goto L_0x02b0
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r0.primaryView
            if (r11 != 0) goto L_0x02b0
            boolean r11 = r29.hasImage()
            if (r11 != 0) goto L_0x02b0
            r0.setScaleX(r6)
            r0.setScaleY(r6)
            r0.setAlpha(r8)
            r0.animateEnter = r10
            r29.invalidate()
            android.view.ViewPropertyAnimator r6 = r29.animate()
            android.view.ViewPropertyAnimator r6 = r6.scaleX(r9)
            android.view.ViewPropertyAnimator r6 = r6.scaleY(r9)
            android.view.ViewPropertyAnimator r6 = r6.alpha(r9)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$5 r11 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$5
            r11.<init>()
            android.view.ViewPropertyAnimator r6 = r6.setListener(r11)
            r11 = 100
            android.view.ViewPropertyAnimator r6 = r6.setDuration(r11)
            r6.start()
            r29.invalidate()
            goto L_0x02b9
        L_0x02b0:
            r0.setScaleY(r9)
            r0.setScaleX(r9)
            r0.setAlpha(r9)
        L_0x02b9:
            r6 = 0
            r29.loadThumb()
            android.widget.ImageView r11 = r0.screencastIcon
            org.telegram.messenger.ChatObject$VideoParticipant r12 = r0.participant
            boolean r12 = r12.presentation
            if (r12 == 0) goto L_0x02cf
            org.telegram.messenger.ChatObject$Call r12 = r0.call
            org.telegram.tgnet.TLRPC$GroupCall r12 = r12.call
            boolean r12 = r12.rtmp_stream
            if (r12 != 0) goto L_0x02cf
            r12 = 0
            goto L_0x02d1
        L_0x02cf:
            r12 = 8
        L_0x02d1:
            r11.setVisibility(r12)
            goto L_0x02d8
        L_0x02d5:
        L_0x02d6:
            r6 = r30
        L_0x02d8:
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.messenger.ChatObject$Call r11 = r0.call
            org.telegram.messenger.ChatObject$VideoParticipant r11 = r11.videoNotAvailableParticipant
            if (r3 != r11) goto L_0x02f4
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.nameView
            int r3 = r3.getVisibility()
            r11 = 4
            if (r3 == r11) goto L_0x0306
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.nameView
            r3.setVisibility(r11)
            org.telegram.ui.Components.RLottieImageView r3 = r0.micIconView
            r3.setVisibility(r11)
            goto L_0x0306
        L_0x02f4:
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.nameView
            int r3 = r3.getVisibility()
            if (r3 == 0) goto L_0x0306
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.nameView
            r3.setVisibility(r2)
            org.telegram.ui.Components.RLottieImageView r3 = r0.micIconView
            r3.setVisibility(r2)
        L_0x0306:
            boolean r3 = r0.attached
            if (r3 == 0) goto L_0x0735
            r3 = 1065353216(0x3var_, float:1.0)
            r11 = 0
            r12 = 0
            boolean r13 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r13 == 0) goto L_0x0322
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r13 = r0.parentContainer
            boolean r13 = r13.inFullscreenMode
            if (r13 == 0) goto L_0x0320
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r13 = r0.secondaryView
            if (r13 != 0) goto L_0x0322
            org.telegram.ui.Components.voip.GroupCallGridCell r13 = r0.primaryView
            if (r13 != 0) goto L_0x0322
        L_0x0320:
            r13 = 1
            goto L_0x0323
        L_0x0322:
            r13 = 0
        L_0x0323:
            boolean r14 = r0.showingInFullscreen
            if (r14 == 0) goto L_0x032a
            r14 = -1
            goto L_0x038b
        L_0x032a:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r14 = r0.secondaryView
            if (r14 == 0) goto L_0x033a
            org.telegram.ui.Components.voip.GroupCallGridCell r14 = r0.primaryView
            if (r14 != 0) goto L_0x033a
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r14 = r0.parentContainer
            boolean r14 = r14.inFullscreenMode
            if (r14 != 0) goto L_0x033a
            r14 = 0
            goto L_0x038b
        L_0x033a:
            boolean r14 = r0.showingAsScrimView
            if (r14 == 0) goto L_0x0340
            r14 = -1
            goto L_0x038b
        L_0x0340:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r14 = r0.secondaryView
            r15 = 1117782016(0x42a00000, float:80.0)
            if (r14 == 0) goto L_0x034f
            org.telegram.ui.Components.voip.GroupCallGridCell r4 = r0.primaryView
            if (r4 != 0) goto L_0x034f
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r15)
            goto L_0x038b
        L_0x034f:
            org.telegram.ui.Components.voip.GroupCallGridCell r4 = r0.tabletGridView
            r16 = 1110966272(0x42380000, float:46.0)
            if (r4 == 0) goto L_0x036c
            if (r13 == 0) goto L_0x036c
            if (r4 == 0) goto L_0x0367
            r11 = 1
            r14 = -1
            int r4 = r4.spanCount
            float r3 = (float) r4
            org.telegram.ui.Components.voip.GroupCallGridCell r4 = r0.tabletGridView
            org.telegram.ui.GroupCallTabletGridAdapter r4 = r4.gridAdapter
            int r12 = r4.getItemCount()
            goto L_0x038b
        L_0x0367:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r16)
            goto L_0x038b
        L_0x036c:
            org.telegram.ui.Components.voip.GroupCallGridCell r4 = r0.primaryView
            if (r4 == 0) goto L_0x0372
            if (r14 == 0) goto L_0x0376
        L_0x0372:
            boolean r14 = r0.isFullscreenMode
            if (r14 != 0) goto L_0x0383
        L_0x0376:
            if (r4 == 0) goto L_0x037e
            r11 = 1
            r14 = -1
            int r4 = r4.spanCount
            float r3 = (float) r4
            goto L_0x038b
        L_0x037e:
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r16)
            goto L_0x038b
        L_0x0383:
            if (r4 == 0) goto L_0x038a
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r15)
            goto L_0x038b
        L_0x038a:
            r14 = 0
        L_0x038b:
            android.view.ViewGroup$LayoutParams r4 = r29.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r4 = (android.view.ViewGroup.MarginLayoutParams) r4
            if (r14 == 0) goto L_0x03d7
            int r15 = r4.height
            if (r15 != r14) goto L_0x03a9
            if (r1 != 0) goto L_0x03a9
            boolean r15 = r0.useSpanSize
            if (r15 != r11) goto L_0x03a9
            if (r11 == 0) goto L_0x03a5
            float r15 = r0.spanCount
            int r15 = (r15 > r3 ? 1 : (r15 == r3 ? 0 : -1))
            if (r15 != 0) goto L_0x03a9
        L_0x03a5:
            int r15 = r0.gridItemsCount
            if (r15 == r12) goto L_0x03d7
        L_0x03a9:
            r4.height = r14
            if (r11 == 0) goto L_0x03af
            r15 = -1
            goto L_0x03b0
        L_0x03af:
            r15 = r14
        L_0x03b0:
            r4.width = r15
            r0.useSpanSize = r11
            r0.spanCount = r3
            r0.checkScale = r10
            if (r6 == 0) goto L_0x03c2
            org.telegram.ui.Components.voip.VoIPTextureView r15 = r0.textureView
            r15.animateToLayout()
            r0.updateNextLayoutAnimated = r10
            goto L_0x03c7
        L_0x03c2:
            org.telegram.ui.Components.voip.VoIPTextureView r15 = r0.textureView
            r15.requestLayout()
        L_0x03c7:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda4 r15 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda4
            r15.<init>(r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r15)
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r15 = r0.parentContainer
            r15.requestLayout()
            r29.invalidate()
        L_0x03d7:
            org.telegram.messenger.ChatObject$VideoParticipant r15 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r15 = r15.participant
            boolean r15 = r15.self
            if (r15 == 0) goto L_0x0409
            org.telegram.messenger.ChatObject$VideoParticipant r15 = r0.participant
            boolean r15 = r15.presentation
            if (r15 != 0) goto L_0x0409
            org.telegram.messenger.voip.VoIPService r15 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r15 == 0) goto L_0x0409
            org.telegram.ui.Components.voip.VoIPTextureView r15 = r0.textureView
            org.webrtc.TextureViewRenderer r15 = r15.renderer
            org.telegram.messenger.voip.VoIPService r16 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            boolean r9 = r16.isFrontFaceCamera()
            r15.setMirror(r9)
            org.telegram.ui.Components.voip.VoIPTextureView r9 = r0.textureView
            org.webrtc.TextureViewRenderer r9 = r9.renderer
            r9.setRotateTextureWithScreen(r10)
            org.telegram.ui.Components.voip.VoIPTextureView r9 = r0.textureView
            org.webrtc.TextureViewRenderer r9 = r9.renderer
            r9.setUseCameraRotation(r10)
            goto L_0x041e
        L_0x0409:
            org.telegram.ui.Components.voip.VoIPTextureView r9 = r0.textureView
            org.webrtc.TextureViewRenderer r9 = r9.renderer
            r9.setMirror(r2)
            org.telegram.ui.Components.voip.VoIPTextureView r9 = r0.textureView
            org.webrtc.TextureViewRenderer r9 = r9.renderer
            r9.setRotateTextureWithScreen(r10)
            org.telegram.ui.Components.voip.VoIPTextureView r9 = r0.textureView
            org.webrtc.TextureViewRenderer r9 = r9.renderer
            r9.setUseCameraRotation(r2)
        L_0x041e:
            org.telegram.ui.Components.voip.VoIPTextureView r9 = r0.textureView
            r9.updateRotation()
            org.telegram.messenger.ChatObject$VideoParticipant r9 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r9 = r9.participant
            boolean r9 = r9.self
            if (r9 == 0) goto L_0x0435
            org.telegram.ui.Components.voip.VoIPTextureView r9 = r0.textureView
            org.webrtc.TextureViewRenderer r9 = r9.renderer
            r15 = 720(0x2d0, float:1.009E-42)
            r9.setMaxTextureSize(r15)
            goto L_0x043c
        L_0x0435:
            org.telegram.ui.Components.voip.VoIPTextureView r9 = r0.textureView
            org.webrtc.TextureViewRenderer r9 = r9.renderer
            r9.setMaxTextureSize(r2)
        L_0x043c:
            r9 = 1
            org.telegram.messenger.ChatObject$VideoParticipant r15 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r15 = r15.participant
            org.telegram.messenger.ChatObject$VideoParticipant r5 = r0.participant
            boolean r5 = r5.presentation
            org.telegram.messenger.ChatObject$Call r8 = r0.call
            boolean r5 = org.telegram.messenger.ChatObject.Call.videoIsActive(r15, r5, r8)
            if (r5 == 0) goto L_0x0466
            org.telegram.messenger.ChatObject$Call r5 = r0.call
            boolean r5 = r5.canStreamVideo
            if (r5 != 0) goto L_0x045c
            org.telegram.messenger.ChatObject$VideoParticipant r5 = r0.participant
            org.telegram.messenger.ChatObject$Call r8 = r0.call
            org.telegram.messenger.ChatObject$VideoParticipant r8 = r8.videoNotAvailableParticipant
            if (r5 == r8) goto L_0x045c
            goto L_0x0466
        L_0x045c:
            r26 = r1
            r27 = r3
            r28 = r4
            r18 = r11
            goto L_0x0521
        L_0x0466:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r5 = r0.noVideoStubLayout
            org.telegram.messenger.ImageReceiver r5 = r5.avatarImageReceiver
            int r8 = r0.currentAccount
            r5.setCurrentAccount(r8)
            org.telegram.messenger.ChatObject$VideoParticipant r5 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r5 = r5.participant
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer
            long r7 = org.telegram.messenger.MessageObject.getPeerId(r5)
            boolean r5 = org.telegram.messenger.DialogObject.isUserDialog(r7)
            if (r5 == 0) goto L_0x04a7
            int r5 = r0.currentAccount
            org.telegram.messenger.AccountInstance r5 = org.telegram.messenger.AccountInstance.getInstance(r5)
            org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
            java.lang.Long r15 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r15)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r15 = r0.noVideoStubLayout
            org.telegram.ui.Components.AvatarDrawable r15 = r15.avatarDrawable
            r15.setInfo((org.telegram.tgnet.TLRPC.User) r5)
            org.telegram.messenger.ImageLocation r15 = org.telegram.messenger.ImageLocation.getForUser(r5, r2)
            org.telegram.messenger.ImageLocation r17 = org.telegram.messenger.ImageLocation.getForUser(r5, r10)
            r18 = r11
            r10 = r15
            r11 = r17
            goto L_0x04ce
        L_0x04a7:
            int r5 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.AccountInstance r5 = org.telegram.messenger.AccountInstance.getInstance(r5)
            org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
            r18 = r11
            long r10 = -r7
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r10)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r10 = r0.noVideoStubLayout
            org.telegram.ui.Components.AvatarDrawable r10 = r10.avatarDrawable
            r10.setInfo((org.telegram.tgnet.TLRPC.Chat) r5)
            org.telegram.messenger.ImageLocation r15 = org.telegram.messenger.ImageLocation.getForChat(r5, r2)
            r10 = 1
            org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForChat(r5, r10)
            r10 = r5
            r10 = r15
        L_0x04ce:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r15 = r0.noVideoStubLayout
            org.telegram.ui.Components.AvatarDrawable r15 = r15.avatarDrawable
            if (r11 == 0) goto L_0x04ec
            org.telegram.messenger.ImageLoader r2 = org.telegram.messenger.ImageLoader.getInstance()
            r26 = r1
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r11.location
            r27 = r3
            java.lang.String r3 = "50_50"
            r28 = r4
            r4 = 0
            android.graphics.drawable.BitmapDrawable r1 = r2.getImageFromMemory(r1, r4, r3)
            r2 = r15
            if (r1 == 0) goto L_0x04f3
            r2 = r1
            goto L_0x04f3
        L_0x04ec:
            r26 = r1
            r27 = r3
            r28 = r4
            r2 = r15
        L_0x04f3:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r1 = r0.noVideoStubLayout
            org.telegram.messenger.ImageReceiver r1 = r1.avatarImageReceiver
            r21 = 0
            r23 = 0
            r25 = 0
            r19 = r1
            r20 = r10
            r22 = r2
            r24 = r5
            r19.setImage(r20, r21, r22, r23, r24, r25)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r1 = r0.noVideoStubLayout
            org.telegram.messenger.ImageReceiver r1 = r1.backgroundImageReceiver
            android.graphics.drawable.ColorDrawable r3 = new android.graphics.drawable.ColorDrawable
            java.lang.String r4 = "voipgroup_listViewBackground"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.<init>(r4)
            java.lang.String r21 = "50_50_b"
            r19 = r1
            r22 = r3
            r19.setImage(r20, r21, r22, r23, r24, r25)
            r9 = 0
        L_0x0521:
            if (r6 == 0) goto L_0x052f
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r1 = r0.secondaryView
            if (r1 == 0) goto L_0x052f
            boolean r1 = r0.showingInFullscreen
            if (r1 != 0) goto L_0x052f
            if (r9 != 0) goto L_0x052f
            r1 = 1
            goto L_0x0530
        L_0x052f:
            r1 = 0
        L_0x0530:
            boolean r2 = r0.hasVideo
            if (r9 == r2) goto L_0x05ba
            if (r1 != 0) goto L_0x05ba
            r0.hasVideo = r9
            android.animation.ValueAnimator r2 = r0.noVideoStubAnimator
            if (r2 == 0) goto L_0x0544
            r2.removeAllListeners()
            android.animation.ValueAnimator r2 = r0.noVideoStubAnimator
            r2.cancel()
        L_0x0544:
            if (r6 == 0) goto L_0x058f
            boolean r2 = r0.hasVideo
            if (r2 != 0) goto L_0x055e
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            int r2 = r2.getVisibility()
            if (r2 == 0) goto L_0x055e
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            r3 = 0
            r2.setVisibility(r3)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            r3 = 0
            r2.setAlpha(r3)
        L_0x055e:
            r2 = 2
            float[] r2 = new float[r2]
            float r3 = r0.progressToNoVideoStub
            r4 = 0
            r2[r4] = r3
            boolean r3 = r0.hasVideo
            if (r3 == 0) goto L_0x056c
            r3 = 0
            goto L_0x056e
        L_0x056c:
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x056e:
            r4 = 1
            r2[r4] = r3
            android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r2)
            r0.noVideoStubAnimator = r2
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda1 r3 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda1
            r3.<init>(r0)
            r2.addUpdateListener(r3)
            android.animation.ValueAnimator r2 = r0.noVideoStubAnimator
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$6 r3 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView$6
            r3.<init>()
            r2.addListener(r3)
            android.animation.ValueAnimator r2 = r0.noVideoStubAnimator
            r2.start()
            goto L_0x05b0
        L_0x058f:
            boolean r2 = r0.hasVideo
            if (r2 == 0) goto L_0x0595
            r3 = 0
            goto L_0x0597
        L_0x0595:
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x0597:
            r0.progressToNoVideoStub = r3
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r3 = r0.noVideoStubLayout
            if (r2 == 0) goto L_0x05a0
            r4 = 8
            goto L_0x05a1
        L_0x05a0:
            r4 = 0
        L_0x05a1:
            r3.setVisibility(r4)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            float r3 = r0.progressToNoVideoStub
            r2.setAlpha(r3)
            org.telegram.ui.Components.voip.VoIPTextureView r2 = r0.textureView
            r2.invalidate()
        L_0x05b0:
            boolean r2 = r0.hasVideo
            if (r2 == 0) goto L_0x05ba
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            r3 = 0
            r2.updateMuteButtonState(r3)
        L_0x05ba:
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            boolean r2 = r2.self
            if (r2 == 0) goto L_0x05d7
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r2 == 0) goto L_0x05d7
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.ui.Components.voip.VoIPTextureView r3 = r0.textureView
            org.webrtc.TextureViewRenderer r3 = r3.renderer
            org.telegram.messenger.ChatObject$VideoParticipant r4 = r0.participant
            boolean r4 = r4.presentation
            r2.setLocalSink(r3, r4)
        L_0x05d7:
            org.telegram.ui.Components.voip.GroupCallStatusIcon r2 = r0.statusIcon
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            r2.setParticipant(r3, r6)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            int r2 = r2.getVisibility()
            if (r2 != 0) goto L_0x05ee
            org.telegram.ui.Components.voip.GroupCallMiniTextureView$NoVideoStubLayout r2 = r0.noVideoStubLayout
            r3 = 1
            r2.updateMuteButtonState(r3)
        L_0x05ee:
            r2 = 0
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            boolean r3 = r3.presentation
            if (r3 == 0) goto L_0x0609
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r3 = r3.presentation
            if (r3 == 0) goto L_0x061c
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r3 = r3.presentation
            boolean r3 = r3.paused
            if (r3 == 0) goto L_0x061c
            r2 = 1
            goto L_0x061c
        L_0x0609:
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r3 = r3.video
            if (r3 == 0) goto L_0x061c
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipantVideo r3 = r3.video
            boolean r3 = r3.paused
            if (r3 == 0) goto L_0x061c
            r2 = 1
        L_0x061c:
            boolean r3 = r0.videoIsPaused
            if (r3 == r2) goto L_0x0644
            r0.videoIsPaused = r2
            org.telegram.ui.Components.voip.VoIPTextureView r3 = r0.textureView
            org.webrtc.TextureViewRenderer r3 = r3.renderer
            android.view.ViewPropertyAnimator r3 = r3.animate()
            boolean r4 = r0.videoIsPaused
            if (r4 == 0) goto L_0x0630
            r4 = 0
            goto L_0x0632
        L_0x0630:
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x0632:
            android.view.ViewPropertyAnimator r3 = r3.alpha(r4)
            r4 = 250(0xfa, double:1.235E-321)
            android.view.ViewPropertyAnimator r3 = r3.setDuration(r4)
            r3.start()
            org.telegram.ui.Components.voip.VoIPTextureView r3 = r0.textureView
            r3.invalidate()
        L_0x0644:
            boolean r3 = org.telegram.ui.GroupCallActivity.paused
            if (r3 != 0) goto L_0x06ca
            boolean r3 = r0.hasVideo
            if (r3 != 0) goto L_0x064e
            goto L_0x06ca
        L_0x064e:
            org.telegram.ui.Components.voip.VoIPTextureView r3 = r0.textureView
            org.webrtc.TextureViewRenderer r3 = r3.renderer
            boolean r3 = r3.isFirstFrameRendered()
            if (r3 != 0) goto L_0x065b
            r29.loadThumb()
        L_0x065b:
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            boolean r3 = r3.self
            if (r3 == 0) goto L_0x067a
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r3 == 0) goto L_0x0730
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.ui.Components.voip.VoIPTextureView r4 = r0.textureView
            org.webrtc.TextureViewRenderer r4 = r4.renderer
            org.telegram.messenger.ChatObject$VideoParticipant r5 = r0.participant
            boolean r5 = r5.presentation
            r3.setLocalSink(r4, r5)
            goto L_0x0730
        L_0x067a:
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r3 == 0) goto L_0x0730
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r4 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r4 = r4.participant
            org.telegram.messenger.ChatObject$VideoParticipant r5 = r0.participant
            boolean r5 = r5.presentation
            org.telegram.ui.Components.voip.VoIPTextureView r7 = r0.textureView
            org.webrtc.TextureViewRenderer r7 = r7.renderer
            r8 = 0
            r3.addRemoteSink(r4, r5, r7, r8)
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r4 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r4 = r4.participant
            org.telegram.messenger.ChatObject$VideoParticipant r5 = r0.participant
            boolean r5 = r5.presentation
            org.telegram.ui.Components.voip.VoIPTextureView r7 = r0.textureView
            org.webrtc.TextureViewRenderer r7 = r7.renderer
            r3.addRemoteSink(r4, r5, r7, r8)
            org.telegram.messenger.ChatObject$Call r3 = r0.call
            if (r3 == 0) goto L_0x0730
            org.telegram.tgnet.TLRPC$GroupCall r3 = r3.call
            boolean r3 = r3.rtmp_stream
            if (r3 == 0) goto L_0x0730
            org.telegram.ui.Components.voip.VoIPTextureView r3 = r0.textureView
            org.webrtc.TextureViewRenderer r3 = r3.renderer
            boolean r3 = r3.isFirstFrameRendered()
            if (r3 != 0) goto L_0x0730
            boolean r3 = r0.postedNoRtmpStreamCallback
            if (r3 != 0) goto L_0x0730
            java.lang.Runnable r3 = r0.noRtmpStreamCallback
            r4 = 15000(0x3a98, double:7.411E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3, r4)
            r3 = 1
            r0.postedNoRtmpStreamCallback = r3
            goto L_0x0730
        L_0x06ca:
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r3.participant
            boolean r3 = r3.self
            if (r3 == 0) goto L_0x06e5
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r3 == 0) goto L_0x0709
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r4 = r0.participant
            boolean r4 = r4.presentation
            r5 = 0
            r3.setLocalSink(r5, r4)
            goto L_0x0709
        L_0x06e5:
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r3 == 0) goto L_0x0709
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r4 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r4 = r4.participant
            org.telegram.messenger.ChatObject$VideoParticipant r5 = r0.participant
            boolean r5 = r5.presentation
            r3.removeRemoteSink(r4, r5)
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.messenger.ChatObject$VideoParticipant r4 = r0.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r4 = r4.participant
            org.telegram.messenger.ChatObject$VideoParticipant r5 = r0.participant
            boolean r5 = r5.presentation
            r3.removeRemoteSink(r4, r5)
        L_0x0709:
            boolean r3 = org.telegram.ui.GroupCallActivity.paused
            if (r3 == 0) goto L_0x0730
            org.telegram.ui.Components.voip.VoIPTextureView r3 = r0.textureView
            org.webrtc.TextureViewRenderer r3 = r3.renderer
            boolean r3 = r3.isFirstFrameRendered()
            if (r3 == 0) goto L_0x0730
            r29.saveThumb()
            org.telegram.ui.Components.voip.VoIPTextureView r3 = r0.textureView
            org.webrtc.TextureViewRenderer r3 = r3.renderer
            r3.clearFirstFrame()
            org.telegram.ui.Components.voip.VoIPTextureView r3 = r0.textureView
            org.webrtc.TextureViewRenderer r3 = r3.renderer
            r4 = 0
            r3.setAlpha(r4)
            org.telegram.ui.Components.voip.VoIPTextureView r3 = r0.textureView
            android.view.TextureView r3 = r3.blurRenderer
            r3.setAlpha(r4)
        L_0x0730:
            r3 = 1
            r0.updateIconColor(r3)
            goto L_0x0737
        L_0x0735:
            r26 = r1
        L_0x0737:
            r29.updateInfo()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallMiniTextureView.updateAttachState(boolean):void");
    }

    /* renamed from: lambda$updateAttachState$2$org-telegram-ui-Components-voip-GroupCallMiniTextureView  reason: not valid java name */
    public /* synthetic */ void m4551x7CLASSNAMEa8(View viewToRemove) {
        this.parentContainer.removeView(viewToRemove);
    }

    /* renamed from: lambda$updateAttachState$3$org-telegram-ui-Components-voip-GroupCallMiniTextureView  reason: not valid java name */
    public /* synthetic */ void m4552x9064ec7(ValueAnimator valueAnimator1) {
        float floatValue = ((Float) valueAnimator1.getAnimatedValue()).floatValue();
        this.progressToNoVideoStub = floatValue;
        this.noVideoStubLayout.setAlpha(floatValue);
        this.textureView.invalidate();
    }

    private void loadThumb() {
        if (this.thumb == null) {
            Bitmap bitmap = this.call.thumbs.get(this.participant.presentation ? this.participant.participant.presentationEndpoint : this.participant.participant.videoEndpoint);
            this.thumb = bitmap;
            this.textureView.setThumb(bitmap);
            if (this.thumb == null) {
                long peerId = MessageObject.getPeerId(this.participant.participant.peer);
                if (this.participant.participant.self && this.participant.presentation) {
                    this.imageReceiver.setImageBitmap((Drawable) new MotionBackgroundDrawable(-14602694, -13935795, -14395293, -14203560, true));
                } else if (peerId > 0) {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(peerId));
                    ImageLocation imageLocation = ImageLocation.getForUser(user, 1);
                    int color = user != null ? AvatarDrawable.getColorForId(user.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
                    this.imageReceiver.setImage(imageLocation, "50_50_b", new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(color, -16777216, 0.2f), ColorUtils.blendARGB(color, -16777216, 0.4f)}), (String) null, user, 0);
                } else {
                    TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-peerId));
                    ImageLocation imageLocation2 = ImageLocation.getForChat(chat, 1);
                    int color2 = chat != null ? AvatarDrawable.getColorForId(chat.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
                    this.imageReceiver.setImage(imageLocation2, "50_50_b", new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(color2, -16777216, 0.2f), ColorUtils.blendARGB(color2, -16777216, 0.4f)}), (String) null, chat, 0);
                }
            }
        }
    }

    public void updateInfo() {
        if (this.attached) {
            String name = null;
            long peerId = MessageObject.getPeerId(this.participant.participant.peer);
            if (DialogObject.isUserDialog(peerId)) {
                name = UserObject.getUserName(AccountInstance.getInstance(this.currentAccount).getMessagesController().getUser(Long.valueOf(peerId)));
            } else {
                TLRPC.Chat currentChat = AccountInstance.getInstance(this.currentAccount).getMessagesController().getChat(Long.valueOf(-peerId));
                if (currentChat != null) {
                    name = currentChat.title;
                }
            }
            this.nameView.setText(name);
        }
    }

    public boolean hasImage() {
        return this.textureView.stubVisibleProgress == 1.0f;
    }

    public void updatePosition(ViewGroup listView, ViewGroup tabletGridListView, RecyclerListView fullscreenListView, GroupCallRenderersContainer renderersContainer) {
        GroupCallGridCell callUserCell;
        ViewGroup fromListView;
        if (!this.showingAsScrimView && !this.animateToScrimView && !this.forceDetached) {
            boolean useTablet = false;
            this.drawFirst = false;
            float progressToFullscreen = renderersContainer.progressToFullscreenMode;
            if (this.animateToFullscreen || this.showingInFullscreen) {
                GroupCallGridCell callUserCell2 = this.primaryView;
                if (callUserCell2 == null && this.tabletGridView == null) {
                    setTranslationX(0.0f);
                    setTranslationY(0.0f);
                } else {
                    GroupCallGridCell groupCallGridCell = this.tabletGridView;
                    if (groupCallGridCell != null) {
                        callUserCell2 = groupCallGridCell;
                    }
                    ViewGroup fromListView2 = groupCallGridCell != null ? tabletGridListView : listView;
                    setTranslationX(((1.0f - progressToFullscreen) * (((callUserCell2.getX() + fromListView2.getX()) - ((float) getLeft())) - ((float) renderersContainer.getLeft()))) + (0.0f * progressToFullscreen));
                    setTranslationY(((1.0f - progressToFullscreen) * ((((callUserCell2.getY() + ((float) AndroidUtilities.dp(2.0f))) + fromListView2.getY()) - ((float) getTop())) - ((float) renderersContainer.getTop()))) + (0.0f * progressToFullscreen));
                }
                this.textureView.setRoundCorners((float) AndroidUtilities.dp(8.0f));
                GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = this.secondaryView;
                if (groupCallUserCell != null) {
                    groupCallUserCell.setAlpha(progressToFullscreen);
                }
                if (!this.showingInFullscreen && this.primaryView == null && this.tabletGridView == null) {
                    setAlpha(progressToFullscreen);
                } else if (!this.animateEnter) {
                    setAlpha(1.0f);
                }
            } else {
                GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell2 = this.secondaryView;
                if (groupCallUserCell2 != null) {
                    if (groupCallUserCell2.isRemoving(fullscreenListView)) {
                        setAlpha(this.secondaryView.getAlpha());
                    } else if (this.primaryView == null) {
                        if (this.attached && !this.animateEnter) {
                            setAlpha(progressToFullscreen);
                        }
                        this.secondaryView.setAlpha(progressToFullscreen);
                        progressToFullscreen = 1.0f;
                    } else {
                        this.secondaryView.setAlpha(1.0f);
                        if (this.attached && !this.animateEnter) {
                            setAlpha(1.0f);
                        }
                    }
                    setTranslationX((this.secondaryView.getX() + fullscreenListView.getX()) - ((float) getLeft()));
                    setTranslationY((((((float) AndroidUtilities.dp(2.0f)) * (1.0f - progressToFullscreen)) + this.secondaryView.getY()) + fullscreenListView.getY()) - ((float) getTop()));
                    this.textureView.setRoundCorners((((float) AndroidUtilities.dp(13.0f)) * progressToFullscreen) + (((float) AndroidUtilities.dp(8.0f)) * (1.0f - progressToFullscreen)));
                    return;
                }
                GroupCallGridCell callUserCell3 = this.primaryView;
                if (callUserCell3 != null || this.tabletGridView != null) {
                    GroupCallGridCell groupCallGridCell2 = this.tabletGridView;
                    if (groupCallGridCell2 == null || callUserCell3 == null) {
                        if (groupCallGridCell2 != null) {
                            callUserCell3 = groupCallGridCell2;
                        }
                        fromListView = groupCallGridCell2 != null ? tabletGridListView : listView;
                    } else {
                        if (GroupCallActivity.isTabletMode && !this.parentContainer.inFullscreenMode) {
                            useTablet = true;
                        }
                        callUserCell = useTablet ? this.tabletGridView : this.primaryView;
                        fromListView = useTablet ? tabletGridListView : listView;
                    }
                    setTranslationX(((callUserCell.getX() + fromListView.getX()) - ((float) getLeft())) - ((float) renderersContainer.getLeft()));
                    setTranslationY((((callUserCell.getY() + ((float) AndroidUtilities.dp(2.0f))) + fromListView.getY()) - ((float) getTop())) - ((float) renderersContainer.getTop()));
                    this.textureView.setRoundCorners((float) AndroidUtilities.dp(8.0f));
                    if (this.attached && !this.animateEnter) {
                        if (!GroupCallActivity.isTabletMode) {
                            this.drawFirst = true;
                            setAlpha((1.0f - progressToFullscreen) * callUserCell.getAlpha());
                        } else if (this.primaryView != null && this.tabletGridView == null) {
                            setAlpha(callUserCell.getAlpha() * progressToFullscreen);
                        }
                    }
                }
            }
        }
    }

    public boolean isAttached() {
        return this.attached;
    }

    public void release() {
        this.textureView.renderer.release();
        if (this.statusIcon != null) {
            this.activity.statusIconPool.add(this.statusIcon);
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

    public boolean isVisible() {
        if (this.showingInFullscreen || this.animateToFullscreen || !this.attached || !this.textureView.renderer.isFirstFrameRendered()) {
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

    public void forceDetach(boolean removeSink) {
        this.forceDetached = true;
        this.attached = false;
        this.parentContainer.detach(this);
        if (removeSink) {
            if (this.participant.participant.self) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().setLocalSink((VideoSink) null, this.participant.presentation);
                }
            } else if (VoIPService.getSharedInstance() != null && !RTMPStreamPipOverlay.isVisible()) {
                VoIPService.getSharedInstance().removeRemoteSink(this.participant.participant, this.participant.presentation);
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
            getRenderBufferBitmap(new GroupCallMiniTextureView$$ExternalSyntheticLambda8(this));
        }
    }

    /* renamed from: lambda$saveThumb$5$org-telegram-ui-Components-voip-GroupCallMiniTextureView  reason: not valid java name */
    public /* synthetic */ void m4549x5d3c7eaf(Bitmap bitmap, int rotation1) {
        if (bitmap != null && bitmap.getPixel(0, 0) != 0) {
            Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(bitmap.getWidth(), bitmap.getHeight()) / 180));
            AndroidUtilities.runOnUIThread(new GroupCallMiniTextureView$$ExternalSyntheticLambda6(this, bitmap));
        }
    }

    /* renamed from: lambda$saveThumb$4$org-telegram-ui-Components-voip-GroupCallMiniTextureView  reason: not valid java name */
    public /* synthetic */ void m4548xd04var_(Bitmap bitmap) {
        this.call.thumbs.put(this.participant.presentation ? this.participant.participant.presentationEndpoint : this.participant.participant.videoEndpoint, bitmap);
    }

    public void setViews(GroupCallGridCell primaryView2, GroupCallFullscreenAdapter.GroupCallUserCell secondaryView2, GroupCallGridCell tabletGrid) {
        this.primaryView = primaryView2;
        this.secondaryView = secondaryView2;
        this.tabletGridView = tabletGrid;
    }

    public void setAmplitude(double value) {
        this.statusIcon.setAmplitude(value);
        this.noVideoStubLayout.setAmplitude(value);
    }

    public void setZoom(boolean inPinchToZoom2, float pinchScale2, float pinchCenterX2, float pinchCenterY2, float pinchTranslationX2, float pinchTranslationY2) {
        if (this.pinchScale != pinchScale2 || this.pinchCenterX != pinchCenterX2 || this.pinchCenterY != pinchCenterY2 || this.pinchTranslationX != pinchTranslationX2 || this.pinchTranslationY != pinchTranslationY2) {
            this.inPinchToZoom = inPinchToZoom2;
            this.pinchScale = pinchScale2;
            this.pinchCenterX = pinchCenterX2;
            this.pinchCenterY = pinchCenterY2;
            this.pinchTranslationX = pinchTranslationX2;
            this.pinchTranslationY = pinchTranslationY2;
            this.textureView.invalidate();
        }
    }

    public void setSwipeToBack(boolean swipeToBack2, float swipeToBackDy2) {
        if (this.swipeToBack != swipeToBack2 || this.swipeToBackDy != swipeToBackDy2) {
            this.swipeToBack = swipeToBack2;
            this.swipeToBackDy = swipeToBackDy2;
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

    private void updateIconColor(boolean animated) {
        final int newColor;
        final int newSpeakingFrameColor;
        GroupCallStatusIcon groupCallStatusIcon = this.statusIcon;
        if (groupCallStatusIcon != null) {
            if (groupCallStatusIcon.isMutedByMe()) {
                newSpeakingFrameColor = Theme.getColor("voipgroup_mutedByAdminIcon");
                newColor = newSpeakingFrameColor;
            } else if (this.statusIcon.isSpeaking()) {
                newSpeakingFrameColor = Theme.getColor("voipgroup_speakingText");
                newColor = newSpeakingFrameColor;
            } else {
                newSpeakingFrameColor = Theme.getColor("voipgroup_speakingText");
                newColor = -1;
            }
            if (this.animateToColor != newColor) {
                ValueAnimator valueAnimator = this.colorAnimator;
                if (valueAnimator != null) {
                    valueAnimator.removeAllListeners();
                    this.colorAnimator.cancel();
                }
                if (!animated) {
                    Paint paint = this.speakingPaint;
                    this.lastSpeakingFrameColor = newSpeakingFrameColor;
                    paint.setColor(newSpeakingFrameColor);
                    return;
                }
                int colorFrom = this.lastIconColor;
                int colorFromSpeaking = this.lastSpeakingFrameColor;
                this.animateToColor = newColor;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.colorAnimator = ofFloat;
                ofFloat.addUpdateListener(new GroupCallMiniTextureView$$ExternalSyntheticLambda2(this, colorFrom, newColor, colorFromSpeaking, newSpeakingFrameColor));
                this.colorAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                        int i = newColor;
                        groupCallMiniTextureView.lastIconColor = i;
                        groupCallMiniTextureView.animateToColor = i;
                        GroupCallMiniTextureView.this.lastSpeakingFrameColor = newSpeakingFrameColor;
                        GroupCallMiniTextureView.this.speakingPaint.setColor(GroupCallMiniTextureView.this.lastSpeakingFrameColor);
                        if (GroupCallMiniTextureView.this.progressToSpeaking > 0.0f) {
                            GroupCallMiniTextureView.this.invalidate();
                        }
                    }
                });
                this.colorAnimator.start();
            }
        }
    }

    /* renamed from: lambda$updateIconColor$6$org-telegram-ui-Components-voip-GroupCallMiniTextureView  reason: not valid java name */
    public /* synthetic */ void m4553x2cb02dc6(int colorFrom, int newColor, int colorFromSpeaking, int newSpeakingFrameColor, ValueAnimator valueAnimator) {
        float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.lastIconColor = ColorUtils.blendARGB(colorFrom, newColor, v);
        int blendARGB = ColorUtils.blendARGB(colorFromSpeaking, newSpeakingFrameColor, v);
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

    public void updateSize(int collapseSize2) {
        int fullSize2 = this.parentContainer.getMeasuredWidth() - AndroidUtilities.dp(6.0f);
        if ((this.collapseSize != collapseSize2 && collapseSize2 > 0) || (this.fullSize != fullSize2 && fullSize2 > 0)) {
            if (collapseSize2 != 0) {
                this.collapseSize = collapseSize2;
            }
            if (fullSize2 != 0) {
                this.fullSize = fullSize2;
            }
            this.nameView.setFullLayoutAdditionalWidth(fullSize2 - collapseSize2, 0);
        }
    }

    private class NoVideoStubLayout extends View {
        private static final int MUTED_BY_ADMIN = 2;
        private static final int MUTE_BUTTON_STATE_MUTE = 1;
        private static final int MUTE_BUTTON_STATE_UNMUTE = 0;
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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            float size = (float) AndroidUtilities.dp(157.0f);
            this.cx = (float) (getMeasuredWidth() >> 1);
            this.cy = ((float) (getMeasuredHeight() >> 1)) + (GroupCallActivity.isLandscapeMode ? 0.0f : ((float) (-getMeasuredHeight())) * 0.12f);
            this.avatarImageReceiver.setRoundRadius((int) (size / 2.0f));
            this.avatarImageReceiver.setImageCoords(this.cx - (size / 2.0f), this.cy - (size / 2.0f), size, size);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float alpha;
            GroupCallActivity.WeavingState weavingState;
            GroupCallActivity.WeavingState weavingState2;
            super.onDraw(canvas);
            AndroidUtilities.rectTmp.set(GroupCallMiniTextureView.this.textureView.getX() + GroupCallMiniTextureView.this.textureView.currentClipHorizontal, GroupCallMiniTextureView.this.textureView.getY() + GroupCallMiniTextureView.this.textureView.currentClipVertical, (GroupCallMiniTextureView.this.textureView.getX() + ((float) GroupCallMiniTextureView.this.textureView.getMeasuredWidth())) - GroupCallMiniTextureView.this.textureView.currentClipHorizontal, GroupCallMiniTextureView.this.textureView.getY() + ((float) GroupCallMiniTextureView.this.textureView.getMeasuredHeight()) + GroupCallMiniTextureView.this.textureView.currentClipVertical);
            this.backgroundImageReceiver.setImageCoords(AndroidUtilities.rectTmp.left, AndroidUtilities.rectTmp.top, AndroidUtilities.rectTmp.width(), AndroidUtilities.rectTmp.height());
            this.backgroundImageReceiver.setRoundRadius((int) GroupCallMiniTextureView.this.textureView.roundRadius);
            this.backgroundImageReceiver.draw(canvas);
            canvas.drawRoundRect(AndroidUtilities.rectTmp, GroupCallMiniTextureView.this.textureView.roundRadius, GroupCallMiniTextureView.this.textureView.roundRadius, this.backgroundPaint);
            float f = this.animateToAmplitude;
            float f2 = this.amplitude;
            if (f != f2) {
                float f3 = this.animateAmplitudeDiff;
                float f4 = f2 + (16.0f * f3);
                this.amplitude = f4;
                if (f3 > 0.0f) {
                    if (f4 > f) {
                        this.amplitude = f;
                    }
                } else if (f4 < f) {
                    this.amplitude = f;
                }
            }
            float f5 = this.switchProgress;
            if (f5 != 1.0f) {
                if (this.prevState != null) {
                    this.switchProgress = f5 + 0.07272727f;
                }
                if (this.switchProgress >= 1.0f) {
                    this.switchProgress = 1.0f;
                    this.prevState = null;
                }
            }
            float scale = (this.amplitude * 0.8f) + 1.0f;
            canvas.save();
            canvas.scale(scale, scale, this.cx, this.cy);
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
                        alpha = this.switchProgress;
                    }
                } else {
                    this.paint.setShader(weavingState2.shader);
                    alpha = 1.0f - this.switchProgress;
                }
                this.paint.setAlpha((int) (76.0f * alpha));
                this.bigWaveDrawable.draw(this.cx, this.cy, canvas, this.paint);
                this.tinyWaveDrawable.draw(this.cx, this.cy, canvas, this.paint);
            }
            canvas.restore();
            float scale2 = (this.amplitude * 0.2f) + 1.0f;
            canvas.save();
            canvas.scale(scale2, scale2, this.cx, this.cy);
            this.avatarImageReceiver.draw(canvas);
            canvas.restore();
            invalidate();
        }

        /* access modifiers changed from: private */
        public void updateMuteButtonState(boolean animated) {
            int newButtonState;
            if (GroupCallMiniTextureView.this.statusIcon.isMutedByMe() || GroupCallMiniTextureView.this.statusIcon.isMutedByAdmin()) {
                newButtonState = 2;
            } else if (GroupCallMiniTextureView.this.statusIcon.isSpeaking()) {
                newButtonState = 1;
            } else {
                newButtonState = 0;
            }
            if (newButtonState != this.muteButtonState) {
                this.muteButtonState = newButtonState;
                GroupCallActivity.WeavingState[] weavingStateArr = this.states;
                if (weavingStateArr[newButtonState] == null) {
                    weavingStateArr[newButtonState] = new GroupCallActivity.WeavingState(this.muteButtonState);
                    int i = this.muteButtonState;
                    if (i == 2) {
                        this.states[i].shader = new LinearGradient(0.0f, 400.0f, 400.0f, 0.0f, new int[]{Theme.getColor("voipgroup_mutedByAdminGradient"), Theme.getColor("voipgroup_mutedByAdminGradient3"), Theme.getColor("voipgroup_mutedByAdminGradient2")}, (float[]) null, Shader.TileMode.CLAMP);
                    } else if (i == 1) {
                        this.states[i].shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor("voipgroup_muteButton"), Theme.getColor("voipgroup_muteButton3")}, (float[]) null, Shader.TileMode.CLAMP);
                    } else {
                        this.states[i].shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor("voipgroup_unmuteButton2"), Theme.getColor("voipgroup_unmuteButton")}, (float[]) null, Shader.TileMode.CLAMP);
                    }
                }
                GroupCallActivity.WeavingState[] weavingStateArr2 = this.states;
                int i2 = this.muteButtonState;
                GroupCallActivity.WeavingState weavingState = weavingStateArr2[i2];
                GroupCallActivity.WeavingState weavingState2 = this.currentState;
                if (weavingState != weavingState2) {
                    this.prevState = weavingState2;
                    this.currentState = weavingStateArr2[i2];
                    if (weavingState2 == null || !animated) {
                        this.switchProgress = 1.0f;
                        this.prevState = null;
                    } else {
                        this.switchProgress = 0.0f;
                    }
                }
                invalidate();
            }
        }

        public void setAmplitude(double value) {
            float amplitude2 = ((float) value) / 80.0f;
            if (amplitude2 > 1.0f) {
                amplitude2 = 1.0f;
            } else if (amplitude2 < 0.0f) {
                amplitude2 = 0.0f;
            }
            this.animateToAmplitude = amplitude2;
            this.animateAmplitudeDiff = (amplitude2 - this.amplitude) / 200.0f;
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
        long peerId = MessageObject.getPeerId(this.participant.participant.peer);
        if (DialogObject.isUserDialog(peerId)) {
            return UserObject.getUserName(AccountInstance.getInstance(UserConfig.selectedAccount).getMessagesController().getUser(Long.valueOf(peerId)));
        }
        return AccountInstance.getInstance(UserConfig.selectedAccount).getMessagesController().getChat(Long.valueOf(-peerId)).title;
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
            ofFloat.addUpdateListener(new GroupCallMiniTextureView$$ExternalSyntheticLambda0(this));
            this.flipAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    GroupCallMiniTextureView.this.flipAnimator = null;
                    GroupCallMiniTextureView.this.textureView.setRotationY(0.0f);
                    if (!GroupCallMiniTextureView.this.flipHalfReached) {
                        GroupCallMiniTextureView.this.textureView.renderer.clearImage();
                    }
                }
            });
            this.flipAnimator.setDuration(400);
            this.flipAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.flipAnimator.start();
        }
    }

    /* renamed from: lambda$startFlipAnimation$7$org-telegram-ui-Components-voip-GroupCallMiniTextureView  reason: not valid java name */
    public /* synthetic */ void m4550xdd41bCLASSNAME(ValueAnimator valueAnimator) {
        float rotation;
        float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        boolean halfReached = false;
        if (v < 0.5f) {
            rotation = v;
        } else {
            halfReached = true;
            rotation = v - 1.0f;
        }
        if (halfReached && !this.flipHalfReached) {
            this.blurredFlippingStub.setAlpha(1.0f);
            this.flipHalfReached = true;
            this.textureView.renderer.clearImage();
        }
        float rotation2 = rotation * 180.0f;
        this.blurredFlippingStub.setRotationY(rotation2);
        this.textureView.renderer.setRotationY(rotation2);
    }
}
