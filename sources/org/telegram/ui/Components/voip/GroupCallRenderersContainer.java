package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarsImageView;
import org.telegram.ui.Components.CrossOutDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.GroupCallActivity;
@SuppressLint({"ViewConstructor"})
/* loaded from: classes3.dex */
public class GroupCallRenderersContainer extends FrameLayout {
    int animationIndex;
    private LongSparseIntArray attachedPeerIds;
    private final ArrayList<GroupCallMiniTextureView> attachedRenderers;
    private final ImageView backButton;
    ChatObject.Call call;
    private boolean canZoomGesture;
    private boolean drawFirst;
    private boolean drawRenderesOnly;
    ValueAnimator fullscreenAnimator;
    private final RecyclerView fullscreenListView;
    public ChatObject.VideoParticipant fullscreenParticipant;
    public long fullscreenPeerId;
    public GroupCallMiniTextureView fullscreenTextureView;
    GroupCallActivity groupCallActivity;
    public boolean hasPinnedVideo;
    Runnable hideUiRunnable;
    boolean hideUiRunnableIsScheduled;
    public boolean inFullscreenMode;
    public boolean inLayout;
    private boolean isInPinchToZoomTouchMode;
    private boolean isTablet;
    public long lastUpdateTime;
    long lastUpdateTooltipTime;
    private final RecyclerView listView;
    public int listWidth;
    boolean maybeSwipeToBackGesture;
    private boolean notDrawRenderes;
    private GroupCallMiniTextureView outFullscreenTextureView;
    private final ImageView pinButton;
    View pinContainer;
    CrossOutDrawable pinDrawable;
    TextView pinTextView;
    private float pinchCenterX;
    private float pinchCenterY;
    float pinchScale;
    private float pinchStartCenterX;
    private float pinchStartCenterY;
    private float pinchStartDistance;
    private float pinchTranslationX;
    private float pinchTranslationY;
    public ImageView pipView;
    private int pointerId1;
    private int pointerId2;
    public float progressToFullscreenMode;
    float progressToHideUi;
    public float progressToScrimView;
    ValueAnimator replaceFullscreenViewAnimator;
    Drawable rightShadowDrawable;
    private final View rightShadowView;
    private boolean showSpeakingMembersToast;
    private float showSpeakingMembersToastProgress;
    private final AvatarsImageView speakingMembersAvatars;
    private final TextView speakingMembersText;
    private final FrameLayout speakingMembersToast;
    private float speakingMembersToastChangeProgress;
    private float speakingMembersToastFromLeft;
    private float speakingMembersToastFromRight;
    private float speakingMembersToastFromTextLeft;
    private long speakingToastPeerId;
    ValueAnimator swipeToBackAnimator;
    float swipeToBackDy;
    boolean swipeToBackGesture;
    public boolean swipedBack;
    boolean tapGesture;
    long tapTime;
    float tapX;
    float tapY;
    Drawable topShadowDrawable;
    private final View topShadowView;
    private final int touchSlop;
    boolean uiVisible;
    public UndoView[] undoView;
    TextView unpinTextView;
    Runnable updateTooltipRunnbale;
    ValueAnimator zoomBackAnimator;
    private boolean zoomStarted;

    protected void onBackPressed() {
    }

    protected void onFullScreenModeChanged(boolean z) {
    }

    protected void onUiVisibilityChanged() {
    }

    public GroupCallRenderersContainer(Context context, RecyclerView recyclerView, RecyclerView recyclerView2, ArrayList<GroupCallMiniTextureView> arrayList, ChatObject.Call call, final GroupCallActivity groupCallActivity) {
        super(context);
        this.attachedPeerIds = new LongSparseIntArray();
        this.speakingMembersToastChangeProgress = 1.0f;
        this.uiVisible = true;
        this.hideUiRunnable = new Runnable() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.1
            @Override // java.lang.Runnable
            public void run() {
                if (!GroupCallRenderersContainer.this.canHideUI()) {
                    AndroidUtilities.runOnUIThread(GroupCallRenderersContainer.this.hideUiRunnable, 3000L);
                    return;
                }
                GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                groupCallRenderersContainer.hideUiRunnableIsScheduled = false;
                groupCallRenderersContainer.setUiVisible(false);
            }
        };
        this.pinchScale = 1.0f;
        this.undoView = new UndoView[2];
        this.listView = recyclerView;
        this.fullscreenListView = recyclerView2;
        this.attachedRenderers = arrayList;
        this.call = call;
        this.groupCallActivity = groupCallActivity;
        ImageView imageView = new ImageView(this, context) { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.2
            @Override // android.widget.ImageView, android.view.View
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(ActionBar.getCurrentActionBarHeight(), NUM));
            }
        };
        this.backButton = imageView;
        BackDrawable backDrawable = new BackDrawable(false);
        backDrawable.setColor(-1);
        imageView.setImageDrawable(backDrawable);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        imageView.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 55)));
        View view = new View(context);
        this.topShadowView = view;
        Drawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0, ColorUtils.setAlphaComponent(-16777216, 114)});
        this.topShadowDrawable = gradientDrawable;
        view.setBackground(gradientDrawable);
        addView(view, LayoutHelper.createFrame(-1, 120.0f));
        View view2 = new View(context);
        this.rightShadowView = view2;
        Drawable gradientDrawable2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0, ColorUtils.setAlphaComponent(-16777216, 114)});
        this.rightShadowDrawable = gradientDrawable2;
        view2.setBackground(gradientDrawable2);
        view2.setVisibility((call == null || !isRtmpStream()) ? 8 : 0);
        addView(view2, LayoutHelper.createFrame(160, -1, 5));
        addView(imageView, LayoutHelper.createFrame(56, -1, 51));
        imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                GroupCallRenderersContainer.this.lambda$new$0(view3);
            }
        });
        ImageView imageView2 = new ImageView(context) { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.3
            @Override // android.view.View
            public void invalidate() {
                super.invalidate();
                GroupCallRenderersContainer.this.pinContainer.invalidate();
                GroupCallRenderersContainer.this.invalidate();
            }

            @Override // android.widget.ImageView, android.view.View
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(ActionBar.getCurrentActionBarHeight(), NUM));
            }
        };
        this.pinButton = imageView2;
        final Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(20.0f), 0, ColorUtils.setAlphaComponent(-1, 100));
        View view3 = new View(context) { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.4
            @Override // android.view.View
            protected void drawableStateChanged() {
                super.drawableStateChanged();
                createSimpleSelectorRoundRectDrawable.setState(getDrawableState());
            }

            @Override // android.view.View
            public boolean verifyDrawable(Drawable drawable) {
                return createSimpleSelectorRoundRectDrawable == drawable || super.verifyDrawable(drawable);
            }

            @Override // android.view.View
            public void jumpDrawablesToCurrentState() {
                super.jumpDrawablesToCurrentState();
                createSimpleSelectorRoundRectDrawable.jumpToCurrentState();
            }

            @Override // android.view.View
            protected void dispatchDraw(Canvas canvas) {
                float measuredWidth = (GroupCallRenderersContainer.this.pinTextView.getMeasuredWidth() * (1.0f - GroupCallRenderersContainer.this.pinDrawable.getProgress())) + (GroupCallRenderersContainer.this.unpinTextView.getMeasuredWidth() * GroupCallRenderersContainer.this.pinDrawable.getProgress());
                canvas.save();
                createSimpleSelectorRoundRectDrawable.setBounds(0, 0, AndroidUtilities.dp(50.0f) + ((int) measuredWidth), getMeasuredHeight());
                createSimpleSelectorRoundRectDrawable.draw(canvas);
                super.dispatchDraw(canvas);
            }
        };
        this.pinContainer = view3;
        view3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view4) {
                GroupCallRenderersContainer.this.lambda$new$1(view4);
            }
        });
        createSimpleSelectorRoundRectDrawable.setCallback(this.pinContainer);
        addView(this.pinContainer);
        CrossOutDrawable crossOutDrawable = new CrossOutDrawable(context, R.drawable.msg_pin_filled, null);
        this.pinDrawable = crossOutDrawable;
        crossOutDrawable.setOffsets(-AndroidUtilities.dp(1.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(1.0f));
        imageView2.setImageDrawable(this.pinDrawable);
        imageView2.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        addView(imageView2, LayoutHelper.createFrame(56, -1, 51));
        TextView textView = new TextView(context);
        this.pinTextView = textView;
        textView.setTextColor(-1);
        this.pinTextView.setTextSize(1, 15.0f);
        this.pinTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.pinTextView.setText(LocaleController.getString("CallVideoPin", R.string.CallVideoPin));
        TextView textView2 = new TextView(context);
        this.unpinTextView = textView2;
        textView2.setTextColor(-1);
        this.unpinTextView.setTextSize(1, 15.0f);
        this.unpinTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.unpinTextView.setText(LocaleController.getString("CallVideoUnpin", R.string.CallVideoUnpin));
        addView(this.pinTextView, LayoutHelper.createFrame(-2, -2, 51));
        addView(this.unpinTextView, LayoutHelper.createFrame(-2, -2, 51));
        ImageView imageView3 = new ImageView(context);
        this.pipView = imageView3;
        imageView3.setVisibility(4);
        this.pipView.setAlpha(0.0f);
        this.pipView.setImageResource(R.drawable.ic_goinline);
        this.pipView.setContentDescription(LocaleController.getString(R.string.AccDescrPipMode));
        int dp = AndroidUtilities.dp(4.0f);
        this.pipView.setPadding(dp, dp, dp, dp);
        this.pipView.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 55)));
        this.pipView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view4) {
                GroupCallRenderersContainer.this.lambda$new$2(groupCallActivity, view4);
            }
        });
        addView(this.pipView, LayoutHelper.createFrame(32, 32.0f, 53, 12.0f, 12.0f, 12.0f, 12.0f));
        final Drawable createRoundRectDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_listViewBackground"), 204));
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.5
            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                if (GroupCallRenderersContainer.this.speakingMembersToastChangeProgress != 1.0f) {
                    float interpolation = 1.0f - CubicBezierInterpolator.DEFAULT.getInterpolation(GroupCallRenderersContainer.this.speakingMembersToastChangeProgress);
                    float left = (GroupCallRenderersContainer.this.speakingMembersToastFromLeft - getLeft()) * interpolation;
                    float left2 = (GroupCallRenderersContainer.this.speakingMembersToastFromTextLeft - GroupCallRenderersContainer.this.speakingMembersText.getLeft()) * interpolation;
                    createRoundRectDrawable.setBounds((int) left, 0, getMeasuredWidth() + ((int) ((GroupCallRenderersContainer.this.speakingMembersToastFromRight - getRight()) * interpolation)), getMeasuredHeight());
                    GroupCallRenderersContainer.this.speakingMembersAvatars.setTranslationX(left);
                    GroupCallRenderersContainer.this.speakingMembersText.setTranslationX(-left2);
                } else {
                    createRoundRectDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    GroupCallRenderersContainer.this.speakingMembersAvatars.setTranslationX(0.0f);
                    GroupCallRenderersContainer.this.speakingMembersText.setTranslationX(0.0f);
                }
                createRoundRectDrawable.draw(canvas);
                super.dispatchDraw(canvas);
            }
        };
        this.speakingMembersToast = frameLayout;
        AvatarsImageView avatarsImageView = new AvatarsImageView(context, true);
        this.speakingMembersAvatars = avatarsImageView;
        avatarsImageView.setStyle(10);
        frameLayout.setClipChildren(false);
        frameLayout.setClipToPadding(false);
        frameLayout.addView(avatarsImageView, LayoutHelper.createFrame(100, 32.0f, 16, 0.0f, 0.0f, 0.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.speakingMembersText = textView3;
        textView3.setTextSize(1, 14.0f);
        textView3.setTextColor(-1);
        textView3.setLines(1);
        textView3.setEllipsize(TextUtils.TruncateAt.END);
        frameLayout.addView(textView3, LayoutHelper.createFrame(-2, -2, 16));
        addView(frameLayout, LayoutHelper.createFrame(-2, 36.0f, 1, 0.0f, 0.0f, 0.0f, 0.0f));
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        for (int i = 0; i < 2; i++) {
            this.undoView[i] = new UndoView(context) { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.6
                @Override // org.telegram.ui.Components.UndoView, android.view.View
                public void invalidate() {
                    super.invalidate();
                    GroupCallRenderersContainer.this.invalidate();
                }
            };
            this.undoView[i].setHideAnimationType(2);
            this.undoView[i].setAdditionalTranslationY(AndroidUtilities.dp(10.0f));
            addView(this.undoView[i], LayoutHelper.createFrame(-1, -2.0f, 80, 16.0f, 0.0f, 0.0f, 8.0f));
        }
        this.pinContainer.setVisibility(8);
        setIsTablet(GroupCallActivity.isTabletMode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        onBackPressed();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        if (this.inFullscreenMode) {
            boolean z = !this.hasPinnedVideo;
            this.hasPinnedVideo = z;
            this.pinDrawable.setCrossOut(z, true);
            requestLayout();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(GroupCallActivity groupCallActivity, View view) {
        if (isRtmpStream()) {
            if (AndroidUtilities.checkInlinePermissions(groupCallActivity.getParentActivity())) {
                RTMPStreamPipOverlay.show();
                groupCallActivity.dismiss();
                return;
            }
            AlertsCreator.createDrawOverlayPermissionDialog(groupCallActivity.getParentActivity(), null).show();
        } else if (AndroidUtilities.checkInlinePermissions(groupCallActivity.getParentActivity())) {
            GroupCallPip.clearForce();
            groupCallActivity.dismiss();
        } else {
            AlertsCreator.createDrawOverlayGroupCallPermissionDialog(getContext()).show();
        }
    }

    private boolean isRtmpStream() {
        ChatObject.Call call = this.call;
        return call != null && call.call.rtmp_stream;
    }

    public void setIsTablet(boolean z) {
        if (this.isTablet != z) {
            this.isTablet = z;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.backButton.getLayoutParams();
            layoutParams.gravity = z ? 85 : 51;
            layoutParams.rightMargin = z ? AndroidUtilities.dp(328.0f) : 0;
            layoutParams.bottomMargin = z ? -AndroidUtilities.dp(8.0f) : 0;
            if (this.isTablet) {
                this.backButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.msg_calls_minimize));
                return;
            }
            BackDrawable backDrawable = new BackDrawable(false);
            backDrawable.setColor(-1);
            this.backButton.setImageDrawable(backDrawable);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View view, long j) {
        if (this.drawFirst) {
            if (!(view instanceof GroupCallMiniTextureView) || !((GroupCallMiniTextureView) view).drawFirst) {
                return true;
            }
            float y = this.listView.getY() - getTop();
            float measuredHeight = (this.listView.getMeasuredHeight() + y) - this.listView.getTranslationY();
            canvas.save();
            canvas.clipRect(0.0f, y, getMeasuredWidth(), measuredHeight);
            boolean drawChild = super.drawChild(canvas, view, j);
            canvas.restore();
            return drawChild;
        }
        UndoView[] undoViewArr = this.undoView;
        if (view == undoViewArr[0] || view == undoViewArr[1]) {
            return true;
        }
        if (view instanceof GroupCallMiniTextureView) {
            GroupCallMiniTextureView groupCallMiniTextureView = (GroupCallMiniTextureView) view;
            if (groupCallMiniTextureView == this.fullscreenTextureView || groupCallMiniTextureView == this.outFullscreenTextureView || this.notDrawRenderes || groupCallMiniTextureView.drawFirst) {
                return true;
            }
            if (groupCallMiniTextureView.primaryView != null) {
                float y2 = this.listView.getY() - getTop();
                float measuredHeight2 = (this.listView.getMeasuredHeight() + y2) - this.listView.getTranslationY();
                float f = this.progressToFullscreenMode;
                if (groupCallMiniTextureView.secondaryView == null) {
                    f = 0.0f;
                }
                canvas.save();
                float f2 = 1.0f - f;
                canvas.clipRect(0.0f, y2 * f2, getMeasuredWidth(), (measuredHeight2 * f2) + (getMeasuredHeight() * f));
                boolean drawChild2 = super.drawChild(canvas, view, j);
                canvas.restore();
                return drawChild2;
            } else if (GroupCallActivity.isTabletMode) {
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                boolean drawChild3 = super.drawChild(canvas, view, j);
                canvas.restore();
                return drawChild3;
            } else {
                return super.drawChild(canvas, view, j);
            }
        } else if (!this.drawRenderesOnly) {
            return super.drawChild(canvas, view, j);
        } else {
            return true;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:150:0x0459  */
    /* JADX WARN: Removed duplicated region for block: B:151:0x0466  */
    /* JADX WARN: Removed duplicated region for block: B:154:0x04aa  */
    /* JADX WARN: Removed duplicated region for block: B:155:0x04b2  */
    /* JADX WARN: Removed duplicated region for block: B:158:0x04bd A[LOOP:1: B:158:0x04bd->B:165:0x0516, LOOP_START, PHI: r10 
      PHI: (r10v1 int) = (r10v0 int), (r10v2 int) binds: [B:157:0x04bb, B:165:0x0516] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:166:0x0519 A[ORIG_RETURN, RETURN] */
    @Override // android.view.ViewGroup, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void dispatchDraw(android.graphics.Canvas r23) {
        /*
            Method dump skipped, instructions count: 1306
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallRenderersContainer.dispatchDraw(android.graphics.Canvas):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:100:0x01df  */
    /* JADX WARN: Removed duplicated region for block: B:91:0x01ae  */
    /* JADX WARN: Removed duplicated region for block: B:94:0x01b5  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x01bc  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void requestFullscreen(org.telegram.messenger.ChatObject.VideoParticipant r17) {
        /*
            Method dump skipped, instructions count: 946
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallRenderersContainer.requestFullscreen(org.telegram.messenger.ChatObject$VideoParticipant):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestFullscreen$3(final GroupCallMiniTextureView groupCallMiniTextureView, final GroupCallMiniTextureView groupCallMiniTextureView2) {
        ValueAnimator valueAnimator = this.replaceFullscreenViewAnimator;
        if (valueAnimator != null) {
            valueAnimator.start();
        }
        groupCallMiniTextureView.animate().scaleX(0.5f).scaleY(0.5f).alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.9
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (groupCallMiniTextureView.getParent() != null) {
                    GroupCallRenderersContainer.this.removeView(groupCallMiniTextureView);
                    groupCallMiniTextureView.release();
                }
            }
        }).setDuration(100L).start();
        if (groupCallMiniTextureView2 != null) {
            groupCallMiniTextureView2.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(100L).setListener(new AnimatorListenerAdapter(this) { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.10
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    groupCallMiniTextureView2.animateEnter = false;
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestFullscreen$4(final GroupCallMiniTextureView groupCallMiniTextureView) {
        groupCallMiniTextureView.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setListener(new AnimatorListenerAdapter(this) { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.11
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                groupCallMiniTextureView.animateEnter = false;
            }
        }).setDuration(150L).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestFullscreen$5(GroupCallMiniTextureView groupCallMiniTextureView, ValueAnimator valueAnimator) {
        groupCallMiniTextureView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestFullscreen$6(ValueAnimator valueAnimator) {
        this.progressToFullscreenMode = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.groupCallActivity.getMenuItemsContainer().setAlpha(1.0f - this.progressToFullscreenMode);
        this.groupCallActivity.invalidateActionBarAlpha();
        this.groupCallActivity.invalidateScrollOffsetY();
        update();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearCurrentFullscreenTextureView() {
        GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
        if (groupCallMiniTextureView != null) {
            groupCallMiniTextureView.setSwipeToBack(false, 0.0f);
            this.fullscreenTextureView.setZoom(false, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void update() {
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setUiVisible(boolean z) {
        if (this.uiVisible != z) {
            this.uiVisible = z;
            onUiVisibilityChanged();
            if (z && this.inFullscreenMode) {
                if (!this.hideUiRunnableIsScheduled) {
                    this.hideUiRunnableIsScheduled = true;
                    AndroidUtilities.runOnUIThread(this.hideUiRunnable, 3000L);
                }
            } else {
                this.hideUiRunnableIsScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(this.hideUiRunnable);
            }
            GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
            if (groupCallMiniTextureView == null) {
                return;
            }
            groupCallMiniTextureView.requestLayout();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean canHideUI() {
        return this.inFullscreenMode;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return onTouchEvent(motionEvent);
    }

    /* JADX WARN: Removed duplicated region for block: B:123:0x025d  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r15) {
        /*
            Method dump skipped, instructions count: 1197
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallRenderersContainer.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void animateSwipeToBack(boolean z) {
        ValueAnimator ofFloat;
        if (this.swipeToBackGesture) {
            this.swipeToBackGesture = false;
            float[] fArr = new float[2];
            float f = this.swipeToBackDy;
            if (z) {
                fArr[0] = f;
                fArr[1] = 0.0f;
                ofFloat = ValueAnimator.ofFloat(fArr);
            } else {
                fArr[0] = f;
                fArr[1] = 0.0f;
                ofFloat = ValueAnimator.ofFloat(fArr);
            }
            this.swipeToBackAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    GroupCallRenderersContainer.this.lambda$animateSwipeToBack$7(valueAnimator);
                }
            });
            this.swipeToBackAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.15
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                    groupCallRenderersContainer.swipeToBackAnimator = null;
                    groupCallRenderersContainer.swipeToBackDy = 0.0f;
                    groupCallRenderersContainer.invalidate();
                }
            });
            ValueAnimator valueAnimator = this.swipeToBackAnimator;
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            valueAnimator.setInterpolator(cubicBezierInterpolator);
            this.swipeToBackAnimator.setDuration(z ? 350L : 200L);
            this.swipeToBackAnimator.setInterpolator(cubicBezierInterpolator);
            GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
            if (groupCallMiniTextureView != null) {
                groupCallMiniTextureView.textureView.synchOrRunAnimation(this.swipeToBackAnimator);
            } else {
                this.swipeToBackAnimator.start();
            }
            this.lastUpdateTime = System.currentTimeMillis();
        }
        this.maybeSwipeToBackGesture = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$animateSwipeToBack$7(ValueAnimator valueAnimator) {
        this.swipeToBackDy = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    private void finishZoom() {
        if (this.zoomStarted) {
            this.zoomStarted = false;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
            this.zoomBackAnimator = ofFloat;
            final float f = this.pinchScale;
            final float f2 = this.pinchTranslationX;
            final float f3 = this.pinchTranslationY;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    GroupCallRenderersContainer.this.lambda$finishZoom$8(f, f2, f3, valueAnimator);
                }
            });
            this.zoomBackAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.16
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                    groupCallRenderersContainer.zoomBackAnimator = null;
                    groupCallRenderersContainer.pinchScale = 1.0f;
                    groupCallRenderersContainer.pinchTranslationX = 0.0f;
                    GroupCallRenderersContainer.this.pinchTranslationY = 0.0f;
                    GroupCallRenderersContainer.this.invalidate();
                }
            });
            this.zoomBackAnimator.setDuration(350L);
            this.zoomBackAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.zoomBackAnimator.start();
            this.lastUpdateTime = System.currentTimeMillis();
        }
        this.canZoomGesture = false;
        this.isInPinchToZoomTouchMode = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$finishZoom$8(float f, float f2, float f3, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.pinchScale = (f * floatValue) + ((1.0f - floatValue) * 1.0f);
        this.pinchTranslationX = f2 * floatValue;
        this.pinchTranslationY = f3 * floatValue;
        invalidate();
    }

    private boolean checkPointerIds(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() < 2) {
            return false;
        }
        if (this.pointerId1 == motionEvent.getPointerId(0) && this.pointerId2 == motionEvent.getPointerId(1)) {
            return true;
        }
        return this.pointerId1 == motionEvent.getPointerId(1) && this.pointerId2 == motionEvent.getPointerId(0);
    }

    public void delayHideUi() {
        if (this.hideUiRunnableIsScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUiRunnable);
        }
        AndroidUtilities.runOnUIThread(this.hideUiRunnable, 3000L);
        this.hideUiRunnableIsScheduled = true;
    }

    public boolean isUiVisible() {
        return this.uiVisible;
    }

    public void setProgressToHideUi(float f) {
        if (this.progressToHideUi != f) {
            this.progressToHideUi = f;
            invalidate();
            GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
            if (groupCallMiniTextureView == null) {
                return;
            }
            groupCallMiniTextureView.invalidate();
        }
    }

    public void setAmplitude(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, float f) {
        for (int i = 0; i < this.attachedRenderers.size(); i++) {
            if (MessageObject.getPeerId(this.attachedRenderers.get(i).participant.participant.peer) == MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer)) {
                this.attachedRenderers.get(i).setAmplitude(f);
            }
        }
    }

    public boolean isAnimating() {
        return this.fullscreenAnimator != null;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        if (GroupCallActivity.isTabletMode) {
            ((ViewGroup.MarginLayoutParams) this.topShadowView.getLayoutParams()).rightMargin = AndroidUtilities.dp(328.0f);
        } else if (GroupCallActivity.isLandscapeMode) {
            ((ViewGroup.MarginLayoutParams) this.topShadowView.getLayoutParams()).rightMargin = isRtmpStream() ? 0 : AndroidUtilities.dp(90.0f);
        } else {
            ((ViewGroup.MarginLayoutParams) this.topShadowView.getLayoutParams()).rightMargin = 0;
        }
        this.rightShadowView.setVisibility((!GroupCallActivity.isLandscapeMode || GroupCallActivity.isTabletMode) ? 8 : 0);
        this.pinContainer.getLayoutParams().height = AndroidUtilities.dp(40.0f);
        this.pinTextView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 0), i2);
        this.unpinTextView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 0), i2);
        this.pinContainer.getLayoutParams().width = AndroidUtilities.dp(46.0f) + (!this.hasPinnedVideo ? this.pinTextView : this.unpinTextView).getMeasuredWidth();
        ((ViewGroup.MarginLayoutParams) this.speakingMembersToast.getLayoutParams()).rightMargin = GroupCallActivity.isLandscapeMode ? AndroidUtilities.dp(45.0f) : 0;
        for (int i3 = 0; i3 < 2; i3++) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.undoView[i3].getLayoutParams();
            if (this.isTablet) {
                marginLayoutParams.rightMargin = AndroidUtilities.dp(344.0f);
            } else {
                marginLayoutParams.rightMargin = GroupCallActivity.isLandscapeMode ? AndroidUtilities.dp(180.0f) : 0;
            }
        }
        super.onMeasure(i, i2);
    }

    public boolean autoPinEnabled() {
        return !this.hasPinnedVideo && System.currentTimeMillis() - this.lastUpdateTime > 2000 && !this.swipeToBackGesture && !this.isInPinchToZoomTouchMode;
    }

    public void setVisibleParticipant(boolean z) {
        boolean z2;
        int i;
        int i2 = 0;
        if (!this.inFullscreenMode || this.isTablet || this.fullscreenParticipant == null || this.fullscreenAnimator != null || this.call == null) {
            if (!this.showSpeakingMembersToast) {
                return;
            }
            this.showSpeakingMembersToast = false;
            this.showSpeakingMembersToastProgress = 0.0f;
            return;
        }
        int currentAccount = this.groupCallActivity.getCurrentAccount();
        long j = 500;
        if (System.currentTimeMillis() - this.lastUpdateTooltipTime < 500) {
            if (this.updateTooltipRunnbale != null) {
                return;
            }
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    GroupCallRenderersContainer.this.lambda$setVisibleParticipant$9();
                }
            };
            this.updateTooltipRunnbale = runnable;
            AndroidUtilities.runOnUIThread(runnable, (System.currentTimeMillis() - this.lastUpdateTooltipTime) + 50);
            return;
        }
        this.lastUpdateTooltipTime = System.currentTimeMillis();
        int i3 = 0;
        SpannableStringBuilder spannableStringBuilder = null;
        int i4 = 0;
        while (i3 < this.call.currentSpeakingPeers.size()) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.call.currentSpeakingPeers.get(this.call.currentSpeakingPeers.keyAt(i3));
            if (tLRPC$TL_groupCallParticipant.self || tLRPC$TL_groupCallParticipant.muted_by_you || MessageObject.getPeerId(this.fullscreenParticipant.participant.peer) == MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer)) {
                i = i3;
            } else {
                long peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
                i = i3;
                if (!(SystemClock.uptimeMillis() - tLRPC$TL_groupCallParticipant.lastSpeakTime < j)) {
                    continue;
                } else {
                    if (spannableStringBuilder == null) {
                        spannableStringBuilder = new SpannableStringBuilder();
                    }
                    if (i4 == 0) {
                        this.speakingToastPeerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
                    }
                    if (i4 < 3) {
                        TLRPC$User user = peerId > 0 ? MessagesController.getInstance(currentAccount).getUser(Long.valueOf(peerId)) : null;
                        TLRPC$Chat chat = peerId <= 0 ? MessagesController.getInstance(currentAccount).getChat(Long.valueOf(peerId)) : null;
                        if (user != null || chat != null) {
                            this.speakingMembersAvatars.setObject(i4, currentAccount, tLRPC$TL_groupCallParticipant);
                            if (i4 != 0) {
                                spannableStringBuilder.append((CharSequence) ", ");
                            }
                            if (user != null) {
                                if (Build.VERSION.SDK_INT >= 21) {
                                    spannableStringBuilder.append(UserObject.getFirstName(user), new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0);
                                } else {
                                    spannableStringBuilder.append((CharSequence) UserObject.getFirstName(user));
                                }
                            } else if (Build.VERSION.SDK_INT >= 21) {
                                spannableStringBuilder.append(chat.title, new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0);
                            } else {
                                spannableStringBuilder.append((CharSequence) chat.title);
                            }
                        }
                    }
                    i4++;
                    if (i4 == 3) {
                        break;
                    }
                }
            }
            i3 = i + 1;
            j = 500;
        }
        boolean z3 = i4 != 0;
        boolean z4 = this.showSpeakingMembersToast;
        if (!z4 && z3) {
            z2 = false;
        } else if (!z3 && z4) {
            this.showSpeakingMembersToast = z3;
            invalidate();
            return;
        } else {
            if (z4 && z3) {
                this.speakingMembersToastFromLeft = this.speakingMembersToast.getLeft();
                this.speakingMembersToastFromRight = this.speakingMembersToast.getRight();
                this.speakingMembersToastFromTextLeft = this.speakingMembersText.getLeft();
                this.speakingMembersToastChangeProgress = 0.0f;
            }
            z2 = z;
        }
        if (!z3) {
            this.showSpeakingMembersToast = z3;
            invalidate();
            return;
        }
        String pluralString = LocaleController.getPluralString("MembersAreSpeakingToast", i4);
        int indexOf = pluralString.indexOf("un1");
        SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(pluralString);
        spannableStringBuilder2.replace(indexOf, indexOf + 3, (CharSequence) spannableStringBuilder);
        this.speakingMembersText.setText(spannableStringBuilder2);
        if (i4 != 0) {
            if (i4 == 1) {
                i2 = AndroidUtilities.dp(40.0f);
            } else if (i4 == 2) {
                i2 = AndroidUtilities.dp(64.0f);
            } else {
                i2 = AndroidUtilities.dp(88.0f);
            }
        }
        ((FrameLayout.LayoutParams) this.speakingMembersText.getLayoutParams()).leftMargin = i2;
        ((FrameLayout.LayoutParams) this.speakingMembersText.getLayoutParams()).rightMargin = AndroidUtilities.dp(16.0f);
        this.showSpeakingMembersToast = z3;
        invalidate();
        while (i4 < 3) {
            this.speakingMembersAvatars.setObject(i4, currentAccount, null);
            i4++;
        }
        this.speakingMembersAvatars.commitTransition(z2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setVisibleParticipant$9() {
        this.updateTooltipRunnbale = null;
        setVisibleParticipant(true);
    }

    public UndoView getUndoView() {
        if (this.undoView[0].getVisibility() == 0) {
            UndoView[] undoViewArr = this.undoView;
            UndoView undoView = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = undoView;
            undoView.hide(true, 2);
            removeView(this.undoView[0]);
            addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    public boolean isVisible(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant) {
        return this.attachedPeerIds.get(MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer)) > 0;
    }

    public void attach(GroupCallMiniTextureView groupCallMiniTextureView) {
        this.attachedRenderers.add(groupCallMiniTextureView);
        long peerId = MessageObject.getPeerId(groupCallMiniTextureView.participant.participant.peer);
        LongSparseIntArray longSparseIntArray = this.attachedPeerIds;
        longSparseIntArray.put(peerId, longSparseIntArray.get(peerId, 0) + 1);
    }

    public void detach(GroupCallMiniTextureView groupCallMiniTextureView) {
        this.attachedRenderers.remove(groupCallMiniTextureView);
        long peerId = MessageObject.getPeerId(groupCallMiniTextureView.participant.participant.peer);
        LongSparseIntArray longSparseIntArray = this.attachedPeerIds;
        longSparseIntArray.put(peerId, longSparseIntArray.get(peerId, 0) - 1);
    }

    public void setGroupCall(ChatObject.Call call) {
        this.call = call;
    }
}
