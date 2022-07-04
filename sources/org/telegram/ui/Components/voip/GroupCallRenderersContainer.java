package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
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
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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

public class GroupCallRenderersContainer extends FrameLayout {
    private boolean animateSpeakingOnNextDraw = true;
    int animationIndex;
    private LongSparseIntArray attachedPeerIds = new LongSparseIntArray();
    private final ArrayList<GroupCallMiniTextureView> attachedRenderers;
    /* access modifiers changed from: private */
    public final ImageView backButton;
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
    Runnable hideUiRunnable = new Runnable() {
        public void run() {
            if (!GroupCallRenderersContainer.this.canHideUI()) {
                AndroidUtilities.runOnUIThread(GroupCallRenderersContainer.this.hideUiRunnable, 3000);
                return;
            }
            GroupCallRenderersContainer.this.hideUiRunnableIsScheduled = false;
            GroupCallRenderersContainer.this.setUiVisible(false);
        }
    };
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
    /* access modifiers changed from: private */
    public GroupCallMiniTextureView outFullscreenTextureView;
    /* access modifiers changed from: private */
    public final ImageView pinButton;
    View pinContainer;
    CrossOutDrawable pinDrawable;
    TextView pinTextView;
    private float pinchCenterX;
    private float pinchCenterY;
    float pinchScale = 1.0f;
    private float pinchStartCenterX;
    private float pinchStartCenterY;
    private float pinchStartDistance;
    /* access modifiers changed from: private */
    public float pinchTranslationX;
    /* access modifiers changed from: private */
    public float pinchTranslationY;
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
    /* access modifiers changed from: private */
    public final AvatarsImageView speakingMembersAvatars;
    /* access modifiers changed from: private */
    public final TextView speakingMembersText;
    private final FrameLayout speakingMembersToast;
    /* access modifiers changed from: private */
    public float speakingMembersToastChangeProgress = 1.0f;
    /* access modifiers changed from: private */
    public float speakingMembersToastFromLeft;
    /* access modifiers changed from: private */
    public float speakingMembersToastFromRight;
    /* access modifiers changed from: private */
    public float speakingMembersToastFromTextLeft;
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
    boolean uiVisible = true;
    public UndoView[] undoView = new UndoView[2];
    TextView unpinTextView;
    Runnable updateTooltipRunnbale;
    ValueAnimator zoomBackAnimator;
    private boolean zoomStarted;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GroupCallRenderersContainer(Context context, RecyclerView listView2, RecyclerView fullscreenListView2, ArrayList<GroupCallMiniTextureView> attachedRenderers2, ChatObject.Call call2, GroupCallActivity groupCallActivity2) {
        super(context);
        Context context2 = context;
        ChatObject.Call call3 = call2;
        GroupCallActivity groupCallActivity3 = groupCallActivity2;
        this.listView = listView2;
        this.fullscreenListView = fullscreenListView2;
        this.attachedRenderers = attachedRenderers2;
        this.call = call3;
        this.groupCallActivity = groupCallActivity3;
        AnonymousClass2 r10 = new ImageView(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(ActionBar.getCurrentActionBarHeight(), NUM));
            }
        };
        this.backButton = r10;
        BackDrawable backDrawable = new BackDrawable(false);
        backDrawable.setColor(-1);
        r10.setImageDrawable(backDrawable);
        r10.setScaleType(ImageView.ScaleType.FIT_CENTER);
        r10.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        r10.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 55)));
        View view = new View(context2);
        this.topShadowView = view;
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0, ColorUtils.setAlphaComponent(-16777216, 114)});
        this.topShadowDrawable = gradientDrawable;
        view.setBackground(gradientDrawable);
        addView(view, LayoutHelper.createFrame(-1, 120.0f));
        View view2 = new View(context2);
        this.rightShadowView = view2;
        GradientDrawable gradientDrawable2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0, ColorUtils.setAlphaComponent(-16777216, 114)});
        this.rightShadowDrawable = gradientDrawable2;
        view2.setBackground(gradientDrawable2);
        view2.setVisibility((call3 == null || !isRtmpStream()) ? 8 : 0);
        addView(view2, LayoutHelper.createFrame(160, -1, 5));
        addView(r10, LayoutHelper.createFrame(56, -1, 51));
        r10.setOnClickListener(new GroupCallRenderersContainer$$ExternalSyntheticLambda4(this));
        AnonymousClass3 r102 = new ImageView(context2) {
            public void invalidate() {
                super.invalidate();
                GroupCallRenderersContainer.this.pinContainer.invalidate();
                GroupCallRenderersContainer.this.invalidate();
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(ActionBar.getCurrentActionBarHeight(), NUM));
            }
        };
        this.pinButton = r102;
        final Drawable pinRippleDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(20.0f), 0, ColorUtils.setAlphaComponent(-1, 100));
        AnonymousClass4 r13 = new View(context2) {
            /* access modifiers changed from: protected */
            public void drawableStateChanged() {
                super.drawableStateChanged();
                pinRippleDrawable.setState(getDrawableState());
            }

            public boolean verifyDrawable(Drawable drawable) {
                return pinRippleDrawable == drawable || super.verifyDrawable(drawable);
            }

            public void jumpDrawablesToCurrentState() {
                super.jumpDrawablesToCurrentState();
                pinRippleDrawable.jumpToCurrentState();
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                float w = (((float) GroupCallRenderersContainer.this.pinTextView.getMeasuredWidth()) * (1.0f - GroupCallRenderersContainer.this.pinDrawable.getProgress())) + (((float) GroupCallRenderersContainer.this.unpinTextView.getMeasuredWidth()) * GroupCallRenderersContainer.this.pinDrawable.getProgress());
                canvas.save();
                pinRippleDrawable.setBounds(0, 0, AndroidUtilities.dp(50.0f) + ((int) w), getMeasuredHeight());
                pinRippleDrawable.draw(canvas);
                super.dispatchDraw(canvas);
            }
        };
        this.pinContainer = r13;
        r13.setOnClickListener(new GroupCallRenderersContainer$$ExternalSyntheticLambda5(this));
        pinRippleDrawable.setCallback(this.pinContainer);
        addView(this.pinContainer);
        CrossOutDrawable crossOutDrawable = new CrossOutDrawable(context2, NUM, (String) null);
        this.pinDrawable = crossOutDrawable;
        crossOutDrawable.setOffsets((float) (-AndroidUtilities.dp(1.0f)), (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(1.0f));
        r102.setImageDrawable(this.pinDrawable);
        r102.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        addView(r102, LayoutHelper.createFrame(56, -1, 51));
        TextView textView = new TextView(context2);
        this.pinTextView = textView;
        textView.setTextColor(-1);
        this.pinTextView.setTextSize(1, 15.0f);
        this.pinTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.pinTextView.setText(LocaleController.getString("CallVideoPin", NUM));
        TextView textView2 = new TextView(context2);
        this.unpinTextView = textView2;
        textView2.setTextColor(-1);
        this.unpinTextView.setTextSize(1, 15.0f);
        this.unpinTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.unpinTextView.setText(LocaleController.getString("CallVideoUnpin", NUM));
        addView(this.pinTextView, LayoutHelper.createFrame(-2, -2, 51));
        addView(this.unpinTextView, LayoutHelper.createFrame(-2, -2, 51));
        ImageView imageView = new ImageView(context2);
        this.pipView = imageView;
        imageView.setVisibility(4);
        this.pipView.setAlpha(0.0f);
        this.pipView.setImageResource(NUM);
        this.pipView.setContentDescription(LocaleController.getString(NUM));
        int padding = AndroidUtilities.dp(4.0f);
        this.pipView.setPadding(padding, padding, padding, padding);
        this.pipView.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 55)));
        this.pipView.setOnClickListener(new GroupCallRenderersContainer$$ExternalSyntheticLambda6(this, groupCallActivity3));
        addView(this.pipView, LayoutHelper.createFrame(32, 32.0f, 53, 12.0f, 12.0f, 12.0f, 12.0f));
        final Drawable toastBackgroundDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_listViewBackground"), 204));
        AnonymousClass5 r103 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                if (GroupCallRenderersContainer.this.speakingMembersToastChangeProgress == 1.0f) {
                    toastBackgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    GroupCallRenderersContainer.this.speakingMembersAvatars.setTranslationX(0.0f);
                    GroupCallRenderersContainer.this.speakingMembersText.setTranslationX(0.0f);
                } else {
                    float progress = CubicBezierInterpolator.DEFAULT.getInterpolation(GroupCallRenderersContainer.this.speakingMembersToastChangeProgress);
                    float offset = (GroupCallRenderersContainer.this.speakingMembersToastFromLeft - ((float) getLeft())) * (1.0f - progress);
                    float offsetText = (GroupCallRenderersContainer.this.speakingMembersToastFromTextLeft - ((float) GroupCallRenderersContainer.this.speakingMembersText.getLeft())) * (1.0f - progress);
                    toastBackgroundDrawable.setBounds((int) offset, 0, getMeasuredWidth() + ((int) ((GroupCallRenderersContainer.this.speakingMembersToastFromRight - ((float) getRight())) * (1.0f - progress))), getMeasuredHeight());
                    GroupCallRenderersContainer.this.speakingMembersAvatars.setTranslationX(offset);
                    GroupCallRenderersContainer.this.speakingMembersText.setTranslationX(-offsetText);
                }
                toastBackgroundDrawable.draw(canvas);
                super.dispatchDraw(canvas);
            }
        };
        this.speakingMembersToast = r103;
        AvatarsImageView avatarsImageView = new AvatarsImageView(context2, true);
        this.speakingMembersAvatars = avatarsImageView;
        avatarsImageView.setStyle(10);
        r103.setClipChildren(false);
        r103.setClipToPadding(false);
        r103.addView(avatarsImageView, LayoutHelper.createFrame(100, 32.0f, 16, 0.0f, 0.0f, 0.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.speakingMembersText = textView3;
        textView3.setTextSize(1, 14.0f);
        textView3.setTextColor(-1);
        textView3.setLines(1);
        textView3.setEllipsize(TextUtils.TruncateAt.END);
        r103.addView(textView3, LayoutHelper.createFrame(-2, -2, 16));
        addView(r103, LayoutHelper.createFrame(-2, 36.0f, 1, 0.0f, 0.0f, 0.0f, 0.0f));
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        for (int a = 0; a < 2; a++) {
            this.undoView[a] = new UndoView(context2) {
                public void invalidate() {
                    super.invalidate();
                    GroupCallRenderersContainer.this.invalidate();
                }
            };
            this.undoView[a].setHideAnimationType(2);
            this.undoView[a].setAdditionalTranslationY((float) AndroidUtilities.dp(10.0f));
            addView(this.undoView[a], LayoutHelper.createFrame(-1, -2.0f, 80, 16.0f, 0.0f, 0.0f, 8.0f));
        }
        this.pinContainer.setVisibility(8);
        setIsTablet(GroupCallActivity.isTabletMode);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-voip-GroupCallRenderersContainer  reason: not valid java name */
    public /* synthetic */ void m1589xbCLASSNAMEa82d(View view) {
        onBackPressed();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-voip-GroupCallRenderersContainer  reason: not valid java name */
    public /* synthetic */ void m1590x767d48ae(View view) {
        if (this.inFullscreenMode) {
            boolean z = !this.hasPinnedVideo;
            this.hasPinnedVideo = z;
            this.pinDrawable.setCrossOut(z, true);
            requestLayout();
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-voip-GroupCallRenderersContainer  reason: not valid java name */
    public /* synthetic */ void m1591x30f2e92f(GroupCallActivity groupCallActivity2, View v) {
        if (isRtmpStream()) {
            if (AndroidUtilities.checkInlinePermissions(groupCallActivity2.getParentActivity())) {
                RTMPStreamPipOverlay.show();
                groupCallActivity2.dismiss();
                return;
            }
            AlertsCreator.createDrawOverlayPermissionDialog(groupCallActivity2.getParentActivity(), (DialogInterface.OnClickListener) null).show();
        } else if (AndroidUtilities.checkInlinePermissions(groupCallActivity2.getParentActivity())) {
            GroupCallPip.clearForce();
            groupCallActivity2.dismiss();
        } else {
            AlertsCreator.createDrawOverlayGroupCallPermissionDialog(getContext()).show();
        }
    }

    private boolean isRtmpStream() {
        ChatObject.Call call2 = this.call;
        return call2 != null && call2.call.rtmp_stream;
    }

    /* access modifiers changed from: protected */
    public void onBackPressed() {
    }

    public void setIsTablet(boolean tablet) {
        if (this.isTablet != tablet) {
            this.isTablet = tablet;
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) this.backButton.getLayoutParams();
            lp.gravity = tablet ? 85 : 51;
            lp.rightMargin = tablet ? AndroidUtilities.dp(328.0f) : 0;
            lp.bottomMargin = tablet ? -AndroidUtilities.dp(8.0f) : 0;
            if (this.isTablet) {
                this.backButton.setImageDrawable(ContextCompat.getDrawable(getContext(), NUM));
                return;
            }
            BackDrawable backDrawable = new BackDrawable(false);
            backDrawable.setColor(-1);
            this.backButton.setImageDrawable(backDrawable);
        }
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (!this.drawFirst) {
            UndoView[] undoViewArr = this.undoView;
            if (child == undoViewArr[0] || child == undoViewArr[1]) {
                return true;
            }
            if (child instanceof GroupCallMiniTextureView) {
                GroupCallMiniTextureView textureView = (GroupCallMiniTextureView) child;
                if (textureView == this.fullscreenTextureView || textureView == this.outFullscreenTextureView || this.notDrawRenderes || textureView.drawFirst) {
                    return true;
                }
                if (textureView.primaryView != null) {
                    float listTop = this.listView.getY() - ((float) getTop());
                    float listBottom = (((float) this.listView.getMeasuredHeight()) + listTop) - this.listView.getTranslationY();
                    float progress = this.progressToFullscreenMode;
                    if (textureView.secondaryView == null) {
                        progress = 0.0f;
                    }
                    canvas.save();
                    canvas.clipRect(0.0f, (1.0f - progress) * listTop, (float) getMeasuredWidth(), ((1.0f - progress) * listBottom) + (((float) getMeasuredHeight()) * progress));
                    boolean r = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return r;
                } else if (!GroupCallActivity.isTabletMode) {
                    return super.drawChild(canvas, child, drawingTime);
                } else {
                    canvas.save();
                    canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    boolean r2 = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return r2;
                }
            } else if (this.drawRenderesOnly) {
                return true;
            } else {
                return super.drawChild(canvas, child, drawingTime);
            }
        } else if (!(child instanceof GroupCallMiniTextureView) || !((GroupCallMiniTextureView) child).drawFirst) {
            return true;
        } else {
            float listTop2 = this.listView.getY() - ((float) getTop());
            float listBottom2 = (((float) this.listView.getMeasuredHeight()) + listTop2) - this.listView.getTranslationY();
            canvas.save();
            canvas.clipRect(0.0f, listTop2, (float) getMeasuredWidth(), listBottom2);
            boolean r3 = super.drawChild(canvas, child, drawingTime);
            canvas.restore();
            return r3;
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x047c  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x0489  */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x04cf  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x04d9  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x04e4  */
    /* JADX WARNING: Removed duplicated region for block: B:170:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dispatchDraw(android.graphics.Canvas r24) {
        /*
            r23 = this;
            r0 = r23
            r8 = r24
            boolean r1 = org.telegram.ui.GroupCallActivity.isTabletMode
            r9 = 1
            r10 = 0
            if (r1 == 0) goto L_0x0011
            r0.drawRenderesOnly = r9
            super.dispatchDraw(r24)
            r0.drawRenderesOnly = r10
        L_0x0011:
            r0.drawFirst = r9
            super.dispatchDraw(r24)
            r0.drawFirst = r10
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.outFullscreenTextureView
            r11 = 1119092736(0x42b40000, float:90.0)
            r12 = 1132396544(0x437var_, float:255.0)
            r13 = 0
            r14 = 1065353216(0x3var_, float:1.0)
            if (r1 != 0) goto L_0x0027
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            if (r1 == 0) goto L_0x0173
        L_0x0027:
            androidx.recyclerview.widget.RecyclerView r1 = r0.listView
            float r1 = r1.getY()
            int r2 = r23.getTop()
            float r2 = (float) r2
            float r1 = r1 - r2
            androidx.recyclerview.widget.RecyclerView r2 = r0.listView
            int r2 = r2.getMeasuredHeight()
            float r2 = (float) r2
            float r2 = r2 + r1
            androidx.recyclerview.widget.RecyclerView r3 = r0.listView
            float r3 = r3.getTranslationY()
            float r2 = r2 - r3
            float r3 = r0.progressToFullscreenMode
            r24.save()
            boolean r4 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r4 != 0) goto L_0x0072
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            if (r4 == 0) goto L_0x0072
            boolean r4 = r4.forceDetached
            if (r4 != 0) goto L_0x0072
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r4 = r4.primaryView
            if (r4 == 0) goto L_0x0072
            float r4 = r14 - r3
            float r4 = r4 * r1
            int r5 = r23.getMeasuredWidth()
            float r5 = (float) r5
            float r6 = r14 - r3
            float r6 = r6 * r2
            int r7 = r23.getMeasuredHeight()
            float r7 = (float) r7
            float r7 = r7 * r3
            float r6 = r6 + r7
            r8.clipRect(r13, r4, r5, r6)
            goto L_0x0081
        L_0x0072:
            boolean r4 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r4 == 0) goto L_0x0081
            int r4 = r23.getMeasuredWidth()
            int r5 = r23.getMeasuredHeight()
            r8.clipRect(r10, r10, r4, r5)
        L_0x0081:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.outFullscreenTextureView
            if (r4 == 0) goto L_0x00a5
            android.view.ViewParent r4 = r4.getParent()
            if (r4 == 0) goto L_0x00a5
            r24.save()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.outFullscreenTextureView
            float r4 = r4.getX()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r0.outFullscreenTextureView
            float r5 = r5.getY()
            r8.translate(r4, r5)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.outFullscreenTextureView
            r4.draw(r8)
            r24.restore()
        L_0x00a5:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            if (r4 == 0) goto L_0x0170
            android.view.ViewParent r4 = r4.getParent()
            if (r4 == 0) goto L_0x0170
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            float r4 = r4.getAlpha()
            int r4 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r4 == 0) goto L_0x00f7
            android.graphics.RectF r4 = org.telegram.messenger.AndroidUtilities.rectTmp
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r0.fullscreenTextureView
            float r5 = r5.getX()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r6 = r0.fullscreenTextureView
            float r6 = r6.getY()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r7 = r0.fullscreenTextureView
            float r7 = r7.getX()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r15 = r0.fullscreenTextureView
            int r15 = r15.getMeasuredWidth()
            float r15 = (float) r15
            float r7 = r7 + r15
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r15 = r0.fullscreenTextureView
            float r15 = r15.getY()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r9 = r0.fullscreenTextureView
            int r9 = r9.getMeasuredHeight()
            float r9 = (float) r9
            float r15 = r15 + r9
            r4.set(r5, r6, r7, r15)
            android.graphics.RectF r4 = org.telegram.messenger.AndroidUtilities.rectTmp
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r0.fullscreenTextureView
            float r5 = r5.getAlpha()
            float r5 = r5 * r12
            int r5 = (int) r5
            r6 = 31
            r8.saveLayerAlpha(r4, r5, r6)
            goto L_0x00fa
        L_0x00f7:
            r24.save()
        L_0x00fa:
            boolean r4 = r0.swipeToBackGesture
            if (r4 != 0) goto L_0x0105
            android.animation.ValueAnimator r4 = r0.swipeToBackAnimator
            if (r4 == 0) goto L_0x0103
            goto L_0x0105
        L_0x0103:
            r4 = 0
            goto L_0x0106
        L_0x0105:
            r4 = 1
        L_0x0106:
            if (r4 == 0) goto L_0x0129
            boolean r5 = r23.isRtmpStream()
            if (r5 != 0) goto L_0x0129
            int r5 = r23.getMeasuredWidth()
            int r6 = r23.getMeasuredHeight()
            boolean r7 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r7 != 0) goto L_0x0124
            boolean r7 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r7 == 0) goto L_0x011f
            goto L_0x0124
        L_0x011f:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r11)
            goto L_0x0125
        L_0x0124:
            r7 = 0
        L_0x0125:
            int r6 = r6 - r7
            r8.clipRect(r10, r10, r5, r6)
        L_0x0129:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r0.fullscreenTextureView
            float r5 = r5.getX()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r6 = r0.fullscreenTextureView
            float r6 = r6.getY()
            r8.translate(r5, r6)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r0.fullscreenTextureView
            float r6 = r0.swipeToBackDy
            r5.setSwipeToBack(r4, r6)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r0.fullscreenTextureView
            boolean r6 = r0.zoomStarted
            if (r6 != 0) goto L_0x014d
            android.animation.ValueAnimator r6 = r0.zoomBackAnimator
            if (r6 == 0) goto L_0x014a
            goto L_0x014d
        L_0x014a:
            r17 = 0
            goto L_0x014f
        L_0x014d:
            r17 = 1
        L_0x014f:
            float r6 = r0.pinchScale
            float r7 = r0.pinchCenterX
            float r9 = r0.pinchCenterY
            float r15 = r0.pinchTranslationX
            float r10 = r0.pinchTranslationY
            r16 = r5
            r18 = r6
            r19 = r7
            r20 = r9
            r21 = r15
            r22 = r10
            r16.setZoom(r17, r18, r19, r20, r21, r22)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r0.fullscreenTextureView
            r5.draw(r8)
            r24.restore()
        L_0x0170:
            r24.restore()
        L_0x0173:
            r1 = 0
            r9 = r1
        L_0x0175:
            r1 = 2
            r2 = 1090519040(0x41000000, float:8.0)
            r10 = 1073741824(0x40000000, float:2.0)
            if (r9 >= r1) goto L_0x027d
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r9]
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0279
            r24.save()
            boolean r1 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r1 == 0) goto L_0x018f
            r1 = 0
            goto L_0x019b
        L_0x018f:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r1 = -r1
            float r1 = (float) r1
            float r3 = r0.progressToHideUi
            float r3 = r14 - r3
            float r1 = r1 * r3
        L_0x019b:
            r15 = r1
            int r1 = r23.getMeasuredWidth()
            float r1 = (float) r1
            int r3 = r23.getMeasuredHeight()
            boolean r4 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r4 == 0) goto L_0x01ab
            r4 = 0
            goto L_0x01af
        L_0x01ab:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
        L_0x01af:
            int r3 = r3 - r4
            float r3 = (float) r3
            float r3 = r3 + r15
            r4 = 1099956224(0x41900000, float:18.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r3 = r3 - r4
            r8.clipRect(r13, r13, r1, r3)
            boolean r1 = r0.isTablet
            if (r1 == 0) goto L_0x01e1
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r9]
            float r1 = r1.getX()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r3 = (float) r3
            float r1 = r1 - r3
            org.telegram.ui.Components.UndoView[] r3 = r0.undoView
            r3 = r3[r9]
            float r3 = r3.getY()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r3 = r3 - r2
            r8.translate(r1, r3)
            goto L_0x020f
        L_0x01e1:
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r9]
            float r1 = r1.getX()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r1 = r1 - r2
            org.telegram.ui.Components.UndoView[] r2 = r0.undoView
            r2 = r2[r9]
            float r2 = r2.getY()
            boolean r3 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r3 == 0) goto L_0x01fd
            r3 = 0
            goto L_0x0201
        L_0x01fd:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r11)
        L_0x0201:
            float r3 = (float) r3
            float r2 = r2 - r3
            float r2 = r2 + r15
            r3 = 1104150528(0x41d00000, float:26.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r2 = r2 - r3
            r8.translate(r1, r2)
        L_0x020f:
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r9]
            float r1 = r1.getAlpha()
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 == 0) goto L_0x0242
            r2 = 0
            r3 = 0
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r9]
            int r1 = r1.getMeasuredWidth()
            float r4 = (float) r1
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r9]
            int r1 = r1.getMeasuredHeight()
            float r5 = (float) r1
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r9]
            float r1 = r1.getAlpha()
            float r1 = r1 * r12
            int r6 = (int) r1
            r7 = 31
            r1 = r24
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            goto L_0x0245
        L_0x0242:
            r24.save()
        L_0x0245:
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r9]
            float r1 = r1.getScaleX()
            org.telegram.ui.Components.UndoView[] r2 = r0.undoView
            r2 = r2[r9]
            float r2 = r2.getScaleY()
            org.telegram.ui.Components.UndoView[] r3 = r0.undoView
            r3 = r3[r9]
            int r3 = r3.getMeasuredWidth()
            float r3 = (float) r3
            float r3 = r3 / r10
            org.telegram.ui.Components.UndoView[] r4 = r0.undoView
            r4 = r4[r9]
            int r4 = r4.getMeasuredHeight()
            float r4 = (float) r4
            float r4 = r4 / r10
            r8.scale(r1, r2, r3, r4)
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r9]
            r1.draw(r8)
            r24.restore()
            r24.restore()
        L_0x0279:
            int r9 = r9 + 1
            goto L_0x0175
        L_0x027d:
            float r1 = r0.progressToFullscreenMode
            float r3 = r0.progressToHideUi
            float r3 = r14 - r3
            float r1 = r1 * r3
            android.animation.ValueAnimator r3 = r0.replaceFullscreenViewAnimator
            if (r3 == 0) goto L_0x02cc
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.outFullscreenTextureView
            if (r3 == 0) goto L_0x02cc
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            if (r4 == 0) goto L_0x02cc
            r4 = r1
            boolean r3 = r3.hasVideo
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r0.fullscreenTextureView
            boolean r5 = r5.hasVideo
            if (r3 == r5) goto L_0x02b4
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.fullscreenTextureView
            boolean r3 = r3.hasVideo
            if (r3 != 0) goto L_0x02ab
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.fullscreenTextureView
            float r3 = r3.getAlpha()
            float r3 = r14 - r3
            float r4 = r4 * r3
            goto L_0x02bb
        L_0x02ab:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.fullscreenTextureView
            float r3 = r3.getAlpha()
            float r4 = r4 * r3
            goto L_0x02bb
        L_0x02b4:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.fullscreenTextureView
            boolean r3 = r3.hasVideo
            if (r3 != 0) goto L_0x02bb
            r4 = 0
        L_0x02bb:
            android.graphics.drawable.Drawable r3 = r0.topShadowDrawable
            float r5 = r4 * r12
            int r5 = (int) r5
            r3.setAlpha(r5)
            android.graphics.drawable.Drawable r3 = r0.rightShadowDrawable
            float r12 = r12 * r4
            int r5 = (int) r12
            r3.setAlpha(r5)
            goto L_0x02ff
        L_0x02cc:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.fullscreenTextureView
            if (r3 == 0) goto L_0x02ef
            android.graphics.drawable.Drawable r4 = r0.topShadowDrawable
            float r5 = r1 * r12
            float r3 = r3.progressToNoVideoStub
            float r3 = r14 - r3
            float r5 = r5 * r3
            int r3 = (int) r5
            r4.setAlpha(r3)
            android.graphics.drawable.Drawable r3 = r0.rightShadowDrawable
            float r12 = r12 * r1
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            float r4 = r4.progressToNoVideoStub
            float r4 = r14 - r4
            float r12 = r12 * r4
            int r4 = (int) r12
            r3.setAlpha(r4)
            goto L_0x02ff
        L_0x02ef:
            android.graphics.drawable.Drawable r3 = r0.topShadowDrawable
            float r4 = r1 * r12
            int r4 = (int) r4
            r3.setAlpha(r4)
            android.graphics.drawable.Drawable r3 = r0.rightShadowDrawable
            float r12 = r12 * r1
            int r4 = (int) r12
            r3.setAlpha(r4)
        L_0x02ff:
            android.widget.ImageView r3 = r0.backButton
            r3.setAlpha(r1)
            boolean r3 = r23.isRtmpStream()
            r4 = 4
            if (r3 == 0) goto L_0x033e
            android.widget.ImageView r3 = r0.pinButton
            r3.setAlpha(r13)
            android.widget.ImageView r3 = r0.pinButton
            r3.setVisibility(r4)
            android.widget.ImageView r3 = r0.pipView
            r3.setAlpha(r1)
            android.widget.ImageView r3 = r0.pipView
            r4 = 0
            r3.setVisibility(r4)
            boolean r3 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r3 == 0) goto L_0x0338
            android.widget.ImageView r3 = r0.pipView
            r4 = 1116733440(0x42900000, float:72.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = -r4
            float r4 = (float) r4
            float r5 = r0.progressToHideUi
            float r5 = r14 - r5
            float r4 = r4 * r5
            r3.setTranslationX(r4)
            goto L_0x0353
        L_0x0338:
            android.widget.ImageView r3 = r0.pipView
            r3.setTranslationX(r13)
            goto L_0x0353
        L_0x033e:
            android.widget.ImageView r3 = r0.pinButton
            r3.setAlpha(r1)
            android.widget.ImageView r3 = r0.pinButton
            r5 = 0
            r3.setVisibility(r5)
            android.widget.ImageView r3 = r0.pipView
            r3.setAlpha(r13)
            android.widget.ImageView r3 = r0.pipView
            r3.setVisibility(r4)
        L_0x0353:
            int r3 = r23.getMeasuredWidth()
            android.widget.TextView r4 = r0.pinTextView
            int r4 = r4.getMeasuredWidth()
            int r3 = r3 - r4
            float r3 = (float) r3
            int r4 = r23.getMeasuredWidth()
            android.widget.TextView r5 = r0.unpinTextView
            int r5 = r5.getMeasuredWidth()
            int r4 = r4 - r5
            float r4 = (float) r4
            int r5 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            android.widget.TextView r6 = r0.pinTextView
            int r6 = r6.getMeasuredHeight()
            int r5 = r5 - r6
            float r5 = (float) r5
            float r5 = r5 / r10
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r6 = (float) r6
            float r5 = r5 - r6
            org.telegram.ui.Components.CrossOutDrawable r6 = r0.pinDrawable
            float r6 = r6.getProgress()
            float r6 = r6 * r4
            org.telegram.ui.Components.CrossOutDrawable r7 = r0.pinDrawable
            float r7 = r7.getProgress()
            float r7 = r14 - r7
            float r7 = r7 * r3
            float r6 = r6 + r7
            r7 = 1101529088(0x41a80000, float:21.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r6 = r6 - r7
            boolean r7 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r7 == 0) goto L_0x03a6
            r7 = 1134821376(0x43a40000, float:328.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r6 = r6 - r7
            goto L_0x03b4
        L_0x03a6:
            boolean r7 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r7 == 0) goto L_0x03b1
            r7 = 1127481344(0x43340000, float:180.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            goto L_0x03b2
        L_0x03b1:
            r7 = 0
        L_0x03b2:
            float r7 = (float) r7
            float r6 = r6 - r7
        L_0x03b4:
            android.widget.TextView r7 = r0.pinTextView
            r7.setTranslationX(r6)
            android.widget.TextView r7 = r0.unpinTextView
            r7.setTranslationX(r6)
            android.widget.TextView r7 = r0.pinTextView
            r7.setTranslationY(r5)
            android.widget.TextView r7 = r0.unpinTextView
            r7.setTranslationY(r5)
            android.view.View r7 = r0.pinContainer
            r9 = 1108344832(0x42100000, float:36.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            float r9 = r6 - r9
            r7.setTranslationX(r9)
            android.view.View r7 = r0.pinContainer
            int r9 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            android.view.View r11 = r0.pinContainer
            int r11 = r11.getMeasuredHeight()
            int r9 = r9 - r11
            float r9 = (float) r9
            float r9 = r9 / r10
            r7.setTranslationY(r9)
            android.widget.ImageView r7 = r0.pinButton
            r9 = 1110441984(0x42300000, float:44.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            float r9 = r6 - r9
            r7.setTranslationX(r9)
            boolean r7 = r23.isRtmpStream()
            if (r7 == 0) goto L_0x040c
            android.widget.TextView r7 = r0.pinTextView
            r7.setAlpha(r13)
            android.widget.TextView r7 = r0.unpinTextView
            r7.setAlpha(r13)
            android.view.View r7 = r0.pinContainer
            r7.setAlpha(r13)
            goto L_0x042d
        L_0x040c:
            android.widget.TextView r7 = r0.pinTextView
            org.telegram.ui.Components.CrossOutDrawable r9 = r0.pinDrawable
            float r9 = r9.getProgress()
            float r9 = r14 - r9
            float r9 = r9 * r1
            r7.setAlpha(r9)
            android.widget.TextView r7 = r0.unpinTextView
            org.telegram.ui.Components.CrossOutDrawable r9 = r0.pinDrawable
            float r9 = r9.getProgress()
            float r9 = r9 * r1
            r7.setAlpha(r9)
            android.view.View r7 = r0.pinContainer
            r7.setAlpha(r1)
        L_0x042d:
            float r7 = r0.speakingMembersToastChangeProgress
            int r9 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1))
            if (r9 == 0) goto L_0x0448
            r9 = 1033171465(0x3d94var_, float:0.07272727)
            float r7 = r7 + r9
            r0.speakingMembersToastChangeProgress = r7
            int r7 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1))
            if (r7 <= 0) goto L_0x0440
            r0.speakingMembersToastChangeProgress = r14
            goto L_0x0443
        L_0x0440:
            r23.invalidate()
        L_0x0443:
            android.widget.FrameLayout r7 = r0.speakingMembersToast
            r7.invalidate()
        L_0x0448:
            boolean r7 = r0.showSpeakingMembersToast
            r9 = 1037726734(0x3dda740e, float:0.10666667)
            if (r7 == 0) goto L_0x0463
            float r11 = r0.showSpeakingMembersToastProgress
            int r12 = (r11 > r14 ? 1 : (r11 == r14 ? 0 : -1))
            if (r12 == 0) goto L_0x0463
            float r11 = r11 + r9
            r0.showSpeakingMembersToastProgress = r11
            int r7 = (r11 > r14 ? 1 : (r11 == r14 ? 0 : -1))
            if (r7 <= 0) goto L_0x045f
            r0.showSpeakingMembersToastProgress = r14
            goto L_0x0478
        L_0x045f:
            r23.invalidate()
            goto L_0x0478
        L_0x0463:
            if (r7 != 0) goto L_0x0478
            float r7 = r0.showSpeakingMembersToastProgress
            int r11 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
            if (r11 == 0) goto L_0x0478
            float r7 = r7 - r9
            r0.showSpeakingMembersToastProgress = r7
            int r7 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
            if (r7 >= 0) goto L_0x0475
            r0.showSpeakingMembersToastProgress = r13
            goto L_0x0478
        L_0x0475:
            r23.invalidate()
        L_0x0478:
            boolean r7 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r7 == 0) goto L_0x0489
            android.widget.FrameLayout r2 = r0.speakingMembersToast
            r7 = 1098907648(0x41800000, float:16.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            r2.setTranslationY(r7)
            goto L_0x04a8
        L_0x0489:
            android.widget.FrameLayout r7 = r0.speakingMembersToast
            int r9 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            float r9 = (float) r9
            float r11 = r0.progressToHideUi
            float r14 = r14 - r11
            float r9 = r9 * r14
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r11 = (float) r11
            float r9 = r9 + r11
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r11 = r0.progressToHideUi
            float r2 = r2 * r11
            float r9 = r9 + r2
            r7.setTranslationY(r9)
        L_0x04a8:
            android.widget.FrameLayout r2 = r0.speakingMembersToast
            float r7 = r0.showSpeakingMembersToastProgress
            float r9 = r0.progressToFullscreenMode
            float r7 = r7 * r9
            r2.setAlpha(r7)
            android.widget.FrameLayout r2 = r0.speakingMembersToast
            float r7 = r0.showSpeakingMembersToastProgress
            r9 = 1056964608(0x3var_, float:0.5)
            float r7 = r7 * r9
            float r7 = r7 + r9
            r2.setScaleX(r7)
            android.widget.FrameLayout r2 = r0.speakingMembersToast
            float r7 = r0.showSpeakingMembersToastProgress
            float r7 = r7 * r9
            float r7 = r7 + r9
            r2.setScaleY(r7)
            boolean r2 = org.telegram.ui.GroupCallActivity.isTabletMode
            boolean r7 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r7 == 0) goto L_0x04d9
            r7 = 1
            r0.notDrawRenderes = r7
            super.dispatchDraw(r24)
            r7 = 0
            r0.notDrawRenderes = r7
            goto L_0x04dc
        L_0x04d9:
            super.dispatchDraw(r24)
        L_0x04dc:
            androidx.recyclerview.widget.RecyclerView r7 = r0.fullscreenListView
            int r7 = r7.getVisibility()
            if (r7 != 0) goto L_0x053f
            r7 = 0
        L_0x04e5:
            androidx.recyclerview.widget.RecyclerView r9 = r0.fullscreenListView
            int r9 = r9.getChildCount()
            if (r7 >= r9) goto L_0x053f
            androidx.recyclerview.widget.RecyclerView r9 = r0.fullscreenListView
            android.view.View r9 = r9.getChildAt(r7)
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r9 = (org.telegram.ui.Components.GroupCallFullscreenAdapter.GroupCallUserCell) r9
            int r11 = r9.getVisibility()
            if (r11 != 0) goto L_0x053c
            float r11 = r9.getAlpha()
            int r11 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r11 == 0) goto L_0x053c
            r24.save()
            float r11 = r9.getX()
            androidx.recyclerview.widget.RecyclerView r12 = r0.fullscreenListView
            float r12 = r12.getX()
            float r11 = r11 + r12
            float r12 = r9.getY()
            androidx.recyclerview.widget.RecyclerView r14 = r0.fullscreenListView
            float r14 = r14.getY()
            float r12 = r12 + r14
            r8.translate(r11, r12)
            float r11 = r9.getScaleX()
            float r12 = r9.getScaleY()
            int r14 = r9.getMeasuredWidth()
            float r14 = (float) r14
            float r14 = r14 / r10
            int r15 = r9.getMeasuredHeight()
            float r15 = (float) r15
            float r15 = r15 / r10
            r8.scale(r11, r12, r14, r15)
            r9.drawOverlays(r8)
            r24.restore()
        L_0x053c:
            int r7 = r7 + 1
            goto L_0x04e5
        L_0x053f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallRenderersContainer.dispatchDraw(android.graphics.Canvas):void");
    }

    public void requestFullscreen(ChatObject.VideoParticipant videoParticipant) {
        GroupCallMiniTextureView newSmallTextureView;
        ChatObject.VideoParticipant videoParticipant2;
        ChatObject.VideoParticipant videoParticipant3 = videoParticipant;
        if (videoParticipant3 != null || this.fullscreenParticipant != null) {
            if (videoParticipant3 == null || !videoParticipant3.equals(this.fullscreenParticipant)) {
                long peerId = videoParticipant3 == null ? 0 : MessageObject.getPeerId(videoParticipant3.participant.peer);
                GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
                if (groupCallMiniTextureView != null) {
                    groupCallMiniTextureView.runDelayedAnimations();
                }
                ValueAnimator valueAnimator = this.replaceFullscreenViewAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                VoIPService service = VoIPService.getSharedInstance();
                boolean z = false;
                if (!(service == null || (videoParticipant2 = this.fullscreenParticipant) == null)) {
                    service.requestFullScreen(videoParticipant2.participant, false, this.fullscreenParticipant.presentation);
                }
                this.fullscreenParticipant = videoParticipant3;
                if (!(service == null || videoParticipant3 == null)) {
                    service.requestFullScreen(videoParticipant3.participant, true, this.fullscreenParticipant.presentation);
                }
                this.fullscreenPeerId = peerId;
                boolean oldInFullscreen = this.inFullscreenMode;
                this.lastUpdateTime = System.currentTimeMillis();
                float f = 1.0f;
                if (videoParticipant3 == null) {
                    if (this.inFullscreenMode) {
                        ValueAnimator valueAnimator2 = this.fullscreenAnimator;
                        if (valueAnimator2 != null) {
                            valueAnimator2.cancel();
                        }
                        this.inFullscreenMode = false;
                        if (!(this.fullscreenTextureView.primaryView == null && this.fullscreenTextureView.secondaryView == null && this.fullscreenTextureView.tabletGridView == null) && ChatObject.Call.videoIsActive(this.fullscreenTextureView.participant.participant, this.fullscreenTextureView.participant.presentation, this.call)) {
                            this.fullscreenTextureView.setShowingInFullscreen(false, true);
                        } else {
                            this.fullscreenTextureView.forceDetach(true);
                            if (this.fullscreenTextureView.primaryView != null) {
                                this.fullscreenTextureView.primaryView.setRenderer((GroupCallMiniTextureView) null);
                            }
                            if (this.fullscreenTextureView.secondaryView != null) {
                                this.fullscreenTextureView.secondaryView.setRenderer((GroupCallMiniTextureView) null);
                            }
                            if (this.fullscreenTextureView.tabletGridView != null) {
                                this.fullscreenTextureView.tabletGridView.setRenderer((GroupCallMiniTextureView) null);
                            }
                            final GroupCallMiniTextureView removingMiniView = this.fullscreenTextureView;
                            removingMiniView.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (removingMiniView.getParent() != null) {
                                        GroupCallRenderersContainer.this.removeView(removingMiniView);
                                        removingMiniView.release();
                                    }
                                }
                            }).setDuration(350).start();
                        }
                    }
                    this.backButton.setEnabled(false);
                    this.hasPinnedVideo = false;
                } else {
                    GroupCallMiniTextureView textureView = null;
                    int i = 0;
                    while (true) {
                        if (i >= this.attachedRenderers.size()) {
                            break;
                        } else if (this.attachedRenderers.get(i).participant.equals(videoParticipant3)) {
                            textureView = this.attachedRenderers.get(i);
                            break;
                        } else {
                            i++;
                        }
                    }
                    if (textureView != null) {
                        ValueAnimator valueAnimator3 = this.fullscreenAnimator;
                        if (valueAnimator3 != null) {
                            valueAnimator3.cancel();
                        }
                        if (!this.inFullscreenMode) {
                            this.inFullscreenMode = true;
                            clearCurrentFullscreenTextureView();
                            this.fullscreenTextureView = textureView;
                            textureView.setShowingInFullscreen(true, true);
                            invalidate();
                            this.pinDrawable.setCrossOut(this.hasPinnedVideo, false);
                        } else {
                            this.hasPinnedVideo = false;
                            this.pinDrawable.setCrossOut(false, false);
                            this.fullscreenTextureView.forceDetach(false);
                            textureView.forceDetach(false);
                            final GroupCallMiniTextureView removingMiniView2 = textureView;
                            if (this.isTablet || (this.fullscreenTextureView.primaryView == null && this.fullscreenTextureView.secondaryView == null && this.fullscreenTextureView.tabletGridView == null)) {
                                newSmallTextureView = null;
                            } else {
                                newSmallTextureView = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                                newSmallTextureView.setViews(this.fullscreenTextureView.primaryView, this.fullscreenTextureView.secondaryView, this.fullscreenTextureView.tabletGridView);
                                newSmallTextureView.setFullscreenMode(this.inFullscreenMode, false);
                                newSmallTextureView.updateAttachState(false);
                                if (this.fullscreenTextureView.primaryView != null) {
                                    this.fullscreenTextureView.primaryView.setRenderer(newSmallTextureView);
                                }
                                if (this.fullscreenTextureView.secondaryView != null) {
                                    this.fullscreenTextureView.secondaryView.setRenderer(newSmallTextureView);
                                }
                                if (this.fullscreenTextureView.tabletGridView != null) {
                                    this.fullscreenTextureView.tabletGridView.setRenderer(newSmallTextureView);
                                }
                            }
                            final GroupCallMiniTextureView newFullscreenTextureView = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                            newFullscreenTextureView.participant = textureView.participant;
                            newFullscreenTextureView.setViews(textureView.primaryView, textureView.secondaryView, textureView.tabletGridView);
                            newFullscreenTextureView.setFullscreenMode(this.inFullscreenMode, false);
                            newFullscreenTextureView.updateAttachState(false);
                            newFullscreenTextureView.textureView.renderer.setAlpha(1.0f);
                            newFullscreenTextureView.textureView.blurRenderer.setAlpha(1.0f);
                            if (textureView.primaryView != null) {
                                textureView.primaryView.setRenderer(newFullscreenTextureView);
                            }
                            if (textureView.secondaryView != null) {
                                textureView.secondaryView.setRenderer(newFullscreenTextureView);
                            }
                            if (textureView.tabletGridView != null) {
                                textureView.tabletGridView.setRenderer(newFullscreenTextureView);
                            }
                            newFullscreenTextureView.animateEnter = true;
                            newFullscreenTextureView.setAlpha(0.0f);
                            this.outFullscreenTextureView = this.fullscreenTextureView;
                            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(newFullscreenTextureView, View.ALPHA, new float[]{0.0f, 1.0f});
                            this.replaceFullscreenViewAnimator = ofFloat;
                            ofFloat.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    GroupCallRenderersContainer.this.replaceFullscreenViewAnimator = null;
                                    newFullscreenTextureView.animateEnter = false;
                                    if (GroupCallRenderersContainer.this.outFullscreenTextureView != null) {
                                        if (GroupCallRenderersContainer.this.outFullscreenTextureView.getParent() != null) {
                                            GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                                            groupCallRenderersContainer.removeView(groupCallRenderersContainer.outFullscreenTextureView);
                                            removingMiniView2.release();
                                        }
                                        GroupCallMiniTextureView unused = GroupCallRenderersContainer.this.outFullscreenTextureView = null;
                                    }
                                }
                            });
                            if (newSmallTextureView != null) {
                                newSmallTextureView.setAlpha(0.0f);
                                newSmallTextureView.setScaleX(0.5f);
                                newSmallTextureView.setScaleY(0.5f);
                                newSmallTextureView.animateEnter = true;
                            }
                            newFullscreenTextureView.runOnFrameRendered(new GroupCallRenderersContainer$$ExternalSyntheticLambda9(this, removingMiniView2, newSmallTextureView));
                            clearCurrentFullscreenTextureView();
                            this.fullscreenTextureView = newFullscreenTextureView;
                            newFullscreenTextureView.setShowingInFullscreen(true, false);
                            update();
                        }
                    } else if (this.inFullscreenMode) {
                        if (this.fullscreenTextureView.primaryView == null) {
                            if (!(this.fullscreenTextureView.secondaryView != null) && !(this.fullscreenTextureView.tabletGridView != null)) {
                                this.fullscreenTextureView.forceDetach(true);
                                final GroupCallMiniTextureView newFullscreenTextureView2 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                                newFullscreenTextureView2.participant = videoParticipant3;
                                newFullscreenTextureView2.setFullscreenMode(this.inFullscreenMode, false);
                                newFullscreenTextureView2.setShowingInFullscreen(true, false);
                                newFullscreenTextureView2.animateEnter = true;
                                newFullscreenTextureView2.setAlpha(0.0f);
                                this.outFullscreenTextureView = this.fullscreenTextureView;
                                ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                                this.replaceFullscreenViewAnimator = ofFloat2;
                                ofFloat2.addUpdateListener(new GroupCallRenderersContainer$$ExternalSyntheticLambda3(this, newFullscreenTextureView2));
                                this.replaceFullscreenViewAnimator.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animation) {
                                        GroupCallRenderersContainer.this.replaceFullscreenViewAnimator = null;
                                        newFullscreenTextureView2.animateEnter = false;
                                        if (GroupCallRenderersContainer.this.outFullscreenTextureView != null) {
                                            if (GroupCallRenderersContainer.this.outFullscreenTextureView.getParent() != null) {
                                                GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                                                groupCallRenderersContainer.removeView(groupCallRenderersContainer.outFullscreenTextureView);
                                                GroupCallRenderersContainer.this.outFullscreenTextureView.release();
                                            }
                                            GroupCallMiniTextureView unused = GroupCallRenderersContainer.this.outFullscreenTextureView = null;
                                        }
                                    }
                                });
                                this.replaceFullscreenViewAnimator.start();
                                clearCurrentFullscreenTextureView();
                                this.fullscreenTextureView = newFullscreenTextureView2;
                                newFullscreenTextureView2.setShowingInFullscreen(true, false);
                                this.fullscreenTextureView.updateAttachState(false);
                                update();
                            }
                        }
                        this.fullscreenTextureView.forceDetach(false);
                        GroupCallMiniTextureView newSmallTextureView2 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                        newSmallTextureView2.setViews(this.fullscreenTextureView.primaryView, this.fullscreenTextureView.secondaryView, this.fullscreenTextureView.tabletGridView);
                        newSmallTextureView2.setFullscreenMode(this.inFullscreenMode, false);
                        newSmallTextureView2.updateAttachState(false);
                        if (this.fullscreenTextureView.primaryView != null) {
                            this.fullscreenTextureView.primaryView.setRenderer(newSmallTextureView2);
                        }
                        if (this.fullscreenTextureView.secondaryView != null) {
                            this.fullscreenTextureView.secondaryView.setRenderer(newSmallTextureView2);
                        }
                        if (this.fullscreenTextureView.tabletGridView != null) {
                            this.fullscreenTextureView.tabletGridView.setRenderer(newSmallTextureView2);
                        }
                        newSmallTextureView2.setAlpha(0.0f);
                        newSmallTextureView2.setScaleX(0.5f);
                        newSmallTextureView2.setScaleY(0.5f);
                        newSmallTextureView2.animateEnter = true;
                        newSmallTextureView2.runOnFrameRendered(new GroupCallRenderersContainer$$ExternalSyntheticLambda8(this, newSmallTextureView2));
                        final GroupCallMiniTextureView newFullscreenTextureView22 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                        newFullscreenTextureView22.participant = videoParticipant3;
                        newFullscreenTextureView22.setFullscreenMode(this.inFullscreenMode, false);
                        newFullscreenTextureView22.setShowingInFullscreen(true, false);
                        newFullscreenTextureView22.animateEnter = true;
                        newFullscreenTextureView22.setAlpha(0.0f);
                        this.outFullscreenTextureView = this.fullscreenTextureView;
                        ValueAnimator ofFloat22 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        this.replaceFullscreenViewAnimator = ofFloat22;
                        ofFloat22.addUpdateListener(new GroupCallRenderersContainer$$ExternalSyntheticLambda3(this, newFullscreenTextureView22));
                        this.replaceFullscreenViewAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                GroupCallRenderersContainer.this.replaceFullscreenViewAnimator = null;
                                newFullscreenTextureView22.animateEnter = false;
                                if (GroupCallRenderersContainer.this.outFullscreenTextureView != null) {
                                    if (GroupCallRenderersContainer.this.outFullscreenTextureView.getParent() != null) {
                                        GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                                        groupCallRenderersContainer.removeView(groupCallRenderersContainer.outFullscreenTextureView);
                                        GroupCallRenderersContainer.this.outFullscreenTextureView.release();
                                    }
                                    GroupCallMiniTextureView unused = GroupCallRenderersContainer.this.outFullscreenTextureView = null;
                                }
                            }
                        });
                        this.replaceFullscreenViewAnimator.start();
                        clearCurrentFullscreenTextureView();
                        this.fullscreenTextureView = newFullscreenTextureView22;
                        newFullscreenTextureView22.setShowingInFullscreen(true, false);
                        this.fullscreenTextureView.updateAttachState(false);
                        update();
                    } else {
                        this.inFullscreenMode = true;
                        clearCurrentFullscreenTextureView();
                        GroupCallMiniTextureView groupCallMiniTextureView2 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                        this.fullscreenTextureView = groupCallMiniTextureView2;
                        groupCallMiniTextureView2.participant = videoParticipant3;
                        this.fullscreenTextureView.setFullscreenMode(this.inFullscreenMode, false);
                        this.fullscreenTextureView.setShowingInFullscreen(true, false);
                        this.fullscreenTextureView.setShowingInFullscreen(true, false);
                        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.fullscreenTextureView, View.ALPHA, new float[]{0.0f, 1.0f});
                        this.replaceFullscreenViewAnimator = ofFloat3;
                        ofFloat3.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                GroupCallRenderersContainer.this.replaceFullscreenViewAnimator = null;
                                GroupCallRenderersContainer.this.fullscreenTextureView.animateEnter = false;
                                if (GroupCallRenderersContainer.this.outFullscreenTextureView != null) {
                                    if (GroupCallRenderersContainer.this.outFullscreenTextureView.getParent() != null) {
                                        GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                                        groupCallRenderersContainer.removeView(groupCallRenderersContainer.outFullscreenTextureView);
                                        GroupCallRenderersContainer.this.outFullscreenTextureView.release();
                                    }
                                    GroupCallMiniTextureView unused = GroupCallRenderersContainer.this.outFullscreenTextureView = null;
                                }
                            }
                        });
                        this.replaceFullscreenViewAnimator.start();
                        invalidate();
                        this.pinDrawable.setCrossOut(this.hasPinnedVideo, false);
                    }
                    this.backButton.setEnabled(true);
                }
                boolean z2 = this.inFullscreenMode;
                if (oldInFullscreen != z2) {
                    if (!z2) {
                        setUiVisible(true);
                        if (this.hideUiRunnableIsScheduled) {
                            this.hideUiRunnableIsScheduled = false;
                            AndroidUtilities.cancelRunOnUIThread(this.hideUiRunnable);
                        }
                    } else {
                        this.backButton.setVisibility(0);
                        this.pinButton.setVisibility(0);
                        this.unpinTextView.setVisibility(0);
                        this.pinContainer.setVisibility(0);
                    }
                    onFullScreenModeChanged(true);
                    float[] fArr = new float[2];
                    fArr[0] = this.progressToFullscreenMode;
                    if (!this.inFullscreenMode) {
                        f = 0.0f;
                    }
                    fArr[1] = f;
                    ValueAnimator ofFloat4 = ValueAnimator.ofFloat(fArr);
                    this.fullscreenAnimator = ofFloat4;
                    ofFloat4.addUpdateListener(new GroupCallRenderersContainer$$ExternalSyntheticLambda1(this));
                    final GroupCallMiniTextureView textureViewFinal = this.fullscreenTextureView;
                    textureViewFinal.animateToFullscreen = true;
                    final int currentAccount = this.groupCallActivity.getCurrentAccount();
                    this.swipedBack = this.swipeToBackGesture;
                    this.animationIndex = NotificationCenter.getInstance(currentAccount).setAnimationInProgress(this.animationIndex, (int[]) null);
                    this.fullscreenAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            NotificationCenter.getInstance(currentAccount).onAnimationFinish(GroupCallRenderersContainer.this.animationIndex);
                            GroupCallRenderersContainer.this.fullscreenAnimator = null;
                            textureViewFinal.animateToFullscreen = false;
                            if (!GroupCallRenderersContainer.this.inFullscreenMode) {
                                GroupCallRenderersContainer.this.clearCurrentFullscreenTextureView();
                                GroupCallRenderersContainer.this.fullscreenTextureView = null;
                                GroupCallRenderersContainer.this.fullscreenPeerId = 0;
                            }
                            GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                            groupCallRenderersContainer.progressToFullscreenMode = groupCallRenderersContainer.inFullscreenMode ? 1.0f : 0.0f;
                            GroupCallRenderersContainer.this.update();
                            GroupCallRenderersContainer.this.onFullScreenModeChanged(false);
                            if (!GroupCallRenderersContainer.this.inFullscreenMode) {
                                GroupCallRenderersContainer.this.backButton.setVisibility(8);
                                GroupCallRenderersContainer.this.pinButton.setVisibility(8);
                                GroupCallRenderersContainer.this.unpinTextView.setVisibility(8);
                                GroupCallRenderersContainer.this.pinContainer.setVisibility(8);
                            }
                        }
                    });
                    this.fullscreenAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    this.fullscreenAnimator.setDuration(350);
                    this.fullscreenTextureView.textureView.synchOrRunAnimation(this.fullscreenAnimator);
                }
                if (this.fullscreenParticipant == null) {
                    z = true;
                }
                animateSwipeToBack(z);
            }
        }
    }

    /* renamed from: lambda$requestFullscreen$3$org-telegram-ui-Components-voip-GroupCallRenderersContainer  reason: not valid java name */
    public /* synthetic */ void m1592xe34ac4fa(final GroupCallMiniTextureView removingMiniView, final GroupCallMiniTextureView finalNewSmallTextureView) {
        ValueAnimator valueAnimator = this.replaceFullscreenViewAnimator;
        if (valueAnimator != null) {
            valueAnimator.start();
        }
        removingMiniView.animate().scaleX(0.5f).scaleY(0.5f).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (removingMiniView.getParent() != null) {
                    GroupCallRenderersContainer.this.removeView(removingMiniView);
                    removingMiniView.release();
                }
            }
        }).setDuration(100).start();
        if (finalNewSmallTextureView != null) {
            finalNewSmallTextureView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(100).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    finalNewSmallTextureView.animateEnter = false;
                }
            }).start();
        }
    }

    /* renamed from: lambda$requestFullscreen$4$org-telegram-ui-Components-voip-GroupCallRenderersContainer  reason: not valid java name */
    public /* synthetic */ void m1593x9dCLASSNAMEb(final GroupCallMiniTextureView newSmallTextureView) {
        newSmallTextureView.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                newSmallTextureView.animateEnter = false;
            }
        }).setDuration(150).start();
    }

    /* renamed from: lambda$requestFullscreen$5$org-telegram-ui-Components-voip-GroupCallRenderersContainer  reason: not valid java name */
    public /* synthetic */ void m1594x583605fc(GroupCallMiniTextureView newFullscreenTextureView, ValueAnimator valueAnimator) {
        newFullscreenTextureView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
        invalidate();
    }

    /* renamed from: lambda$requestFullscreen$6$org-telegram-ui-Components-voip-GroupCallRenderersContainer  reason: not valid java name */
    public /* synthetic */ void m1595x12aba67d(ValueAnimator valueAnimator) {
        this.progressToFullscreenMode = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.groupCallActivity.getMenuItemsContainer().setAlpha(1.0f - this.progressToFullscreenMode);
        this.groupCallActivity.invalidateActionBarAlpha();
        this.groupCallActivity.invalidateScrollOffsetY();
        update();
    }

    /* access modifiers changed from: private */
    public void clearCurrentFullscreenTextureView() {
        GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
        if (groupCallMiniTextureView != null) {
            groupCallMiniTextureView.setSwipeToBack(false, 0.0f);
            this.fullscreenTextureView.setZoom(false, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        }
    }

    /* access modifiers changed from: protected */
    public void update() {
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onFullScreenModeChanged(boolean startAnimaion) {
    }

    /* access modifiers changed from: private */
    public void setUiVisible(boolean uiVisible2) {
        if (this.uiVisible != uiVisible2) {
            this.uiVisible = uiVisible2;
            onUiVisibilityChanged();
            if (!uiVisible2 || !this.inFullscreenMode) {
                this.hideUiRunnableIsScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(this.hideUiRunnable);
            } else if (!this.hideUiRunnableIsScheduled) {
                this.hideUiRunnableIsScheduled = true;
                AndroidUtilities.runOnUIThread(this.hideUiRunnable, 3000);
            }
            GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
            if (groupCallMiniTextureView != null) {
                groupCallMiniTextureView.requestLayout();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onUiVisibilityChanged() {
    }

    /* access modifiers changed from: protected */
    public boolean canHideUI() {
        return this.inFullscreenMode;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return onTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if ((this.maybeSwipeToBackGesture || this.swipeToBackGesture) && (ev.getActionMasked() == 1 || ev.getActionMasked() == 3)) {
            this.maybeSwipeToBackGesture = false;
            if (this.swipeToBackGesture) {
                if (ev.getActionMasked() != 1 || Math.abs(this.swipeToBackDy) <= ((float) AndroidUtilities.dp(120.0f))) {
                    animateSwipeToBack(false);
                } else {
                    this.groupCallActivity.fullscreenFor((ChatObject.VideoParticipant) null);
                }
            }
            invalidate();
        }
        if (!this.inFullscreenMode || ((!this.maybeSwipeToBackGesture && !this.swipeToBackGesture && !this.tapGesture && !this.canZoomGesture && !this.isInPinchToZoomTouchMode && !this.zoomStarted && ev.getActionMasked() != 0) || this.fullscreenTextureView == null)) {
            finishZoom();
            return false;
        }
        if (ev.getActionMasked() == 0) {
            this.maybeSwipeToBackGesture = false;
            this.swipeToBackGesture = false;
            this.canZoomGesture = false;
            this.isInPinchToZoomTouchMode = false;
            this.zoomStarted = false;
        }
        if (ev.getActionMasked() == 0 && this.swipeToBackAnimator != null) {
            this.maybeSwipeToBackGesture = false;
            this.swipeToBackGesture = true;
            this.tapY = ev.getY() - this.swipeToBackDy;
            this.swipeToBackAnimator.removeAllListeners();
            this.swipeToBackAnimator.cancel();
            this.swipeToBackAnimator = null;
        } else if (this.swipeToBackAnimator != null) {
            finishZoom();
            return false;
        }
        if (this.fullscreenTextureView.isInsideStopScreenButton(ev.getX(), ev.getY())) {
            return false;
        }
        if (ev.getActionMasked() == 0 && !this.swipeToBackGesture) {
            AndroidUtilities.rectTmp.set(0.0f, (float) ActionBar.getCurrentActionBarHeight(), (float) (this.fullscreenTextureView.getMeasuredWidth() + ((!GroupCallActivity.isLandscapeMode || !this.uiVisible) ? 0 : -AndroidUtilities.dp(90.0f))), (float) (this.fullscreenTextureView.getMeasuredHeight() + ((GroupCallActivity.isLandscapeMode || !this.uiVisible) ? 0 : -AndroidUtilities.dp(90.0f))));
            if (AndroidUtilities.rectTmp.contains(ev.getX(), ev.getY())) {
                this.tapTime = System.currentTimeMillis();
                this.tapGesture = true;
                this.maybeSwipeToBackGesture = true;
                this.tapX = ev.getX();
                this.tapY = ev.getY();
            }
        } else if ((this.maybeSwipeToBackGesture || this.swipeToBackGesture || this.tapGesture) && ev.getActionMasked() == 2) {
            if (Math.abs(this.tapX - ev.getX()) > ((float) this.touchSlop) || Math.abs(this.tapY - ev.getY()) > ((float) this.touchSlop)) {
                this.tapGesture = false;
            }
            if (this.maybeSwipeToBackGesture && !this.zoomStarted && Math.abs(this.tapY - ev.getY()) > ((float) (this.touchSlop * 2))) {
                this.tapY = ev.getY();
                this.maybeSwipeToBackGesture = false;
                this.swipeToBackGesture = true;
            } else if (this.swipeToBackGesture) {
                this.swipeToBackDy = ev.getY() - this.tapY;
                invalidate();
            }
            if (this.maybeSwipeToBackGesture && Math.abs(this.tapX - ev.getX()) > ((float) (this.touchSlop * 4))) {
                this.maybeSwipeToBackGesture = false;
            }
        }
        if (this.tapGesture && ev.getActionMasked() == 1 && System.currentTimeMillis() - this.tapTime < 200) {
            boolean confirmAction = false;
            this.tapGesture = false;
            if (this.showSpeakingMembersToast) {
                AndroidUtilities.rectTmp.set(this.speakingMembersToast.getX(), this.speakingMembersToast.getY(), this.speakingMembersToast.getX() + ((float) this.speakingMembersToast.getWidth()), this.speakingMembersToast.getY() + ((float) this.speakingMembersToast.getHeight()));
                if (this.call != null && AndroidUtilities.rectTmp.contains(ev.getX(), ev.getY())) {
                    boolean found = false;
                    for (int i = 0; i < this.call.visibleVideoParticipants.size(); i++) {
                        if (this.speakingToastPeerId == MessageObject.getPeerId(this.call.visibleVideoParticipants.get(i).participant.peer)) {
                            found = true;
                            confirmAction = true;
                            this.groupCallActivity.fullscreenFor(this.call.visibleVideoParticipants.get(i));
                        }
                    }
                    if (!found) {
                        this.groupCallActivity.fullscreenFor(new ChatObject.VideoParticipant(this.call.participants.get(this.speakingToastPeerId), false, false));
                        confirmAction = true;
                    }
                }
            }
            if (!confirmAction) {
                setUiVisible(!this.uiVisible);
            }
            this.swipeToBackDy = 0.0f;
            invalidate();
        }
        if (!this.fullscreenTextureView.hasVideo || this.swipeToBackGesture) {
            finishZoom();
            if (this.tapGesture || this.swipeToBackGesture || this.maybeSwipeToBackGesture) {
                return true;
            }
            return false;
        }
        if (ev.getActionMasked() == 0 || ev.getActionMasked() == 5) {
            if (ev.getActionMasked() == 0) {
                View renderer = this.fullscreenTextureView.textureView.renderer;
                AndroidUtilities.rectTmp.set(renderer.getX(), renderer.getY(), renderer.getX() + ((float) renderer.getMeasuredWidth()), renderer.getY() + ((float) renderer.getMeasuredHeight()));
                AndroidUtilities.rectTmp.inset(((((float) renderer.getMeasuredHeight()) * this.fullscreenTextureView.textureView.scaleTextureToFill) - ((float) renderer.getMeasuredHeight())) / 2.0f, ((((float) renderer.getMeasuredWidth()) * this.fullscreenTextureView.textureView.scaleTextureToFill) - ((float) renderer.getMeasuredWidth())) / 2.0f);
                if (!GroupCallActivity.isLandscapeMode) {
                    AndroidUtilities.rectTmp.top = Math.max(AndroidUtilities.rectTmp.top, (float) ActionBar.getCurrentActionBarHeight());
                    AndroidUtilities.rectTmp.bottom = Math.min(AndroidUtilities.rectTmp.bottom, (float) (this.fullscreenTextureView.getMeasuredHeight() - AndroidUtilities.dp(90.0f)));
                } else {
                    AndroidUtilities.rectTmp.top = Math.max(AndroidUtilities.rectTmp.top, (float) ActionBar.getCurrentActionBarHeight());
                    AndroidUtilities.rectTmp.right = Math.min(AndroidUtilities.rectTmp.right, (float) (this.fullscreenTextureView.getMeasuredWidth() - AndroidUtilities.dp(90.0f)));
                }
                boolean contains = AndroidUtilities.rectTmp.contains(ev.getX(), ev.getY());
                this.canZoomGesture = contains;
                if (!contains) {
                    finishZoom();
                    return this.maybeSwipeToBackGesture;
                }
            }
            if (!this.isInPinchToZoomTouchMode && ev.getPointerCount() == 2) {
                this.pinchStartDistance = (float) Math.hypot((double) (ev.getX(1) - ev.getX(0)), (double) (ev.getY(1) - ev.getY(0)));
                float x = (ev.getX(0) + ev.getX(1)) / 2.0f;
                this.pinchCenterX = x;
                this.pinchStartCenterX = x;
                float y = (ev.getY(0) + ev.getY(1)) / 2.0f;
                this.pinchCenterY = y;
                this.pinchStartCenterY = y;
                this.pinchScale = 1.0f;
                this.pointerId1 = ev.getPointerId(0);
                this.pointerId2 = ev.getPointerId(1);
                this.isInPinchToZoomTouchMode = true;
            }
        } else if (ev.getActionMasked() == 2 && this.isInPinchToZoomTouchMode) {
            int index1 = -1;
            int index2 = -1;
            for (int i2 = 0; i2 < ev.getPointerCount(); i2++) {
                if (this.pointerId1 == ev.getPointerId(i2)) {
                    index1 = i2;
                }
                if (this.pointerId2 == ev.getPointerId(i2)) {
                    index2 = i2;
                }
            }
            if (index1 == -1 || index2 == -1) {
                getParent().requestDisallowInterceptTouchEvent(false);
                finishZoom();
                return this.maybeSwipeToBackGesture;
            }
            float hypot = ((float) Math.hypot((double) (ev.getX(index2) - ev.getX(index1)), (double) (ev.getY(index2) - ev.getY(index1)))) / this.pinchStartDistance;
            this.pinchScale = hypot;
            if (hypot > 1.005f && !this.zoomStarted) {
                this.pinchStartDistance = (float) Math.hypot((double) (ev.getX(index2) - ev.getX(index1)), (double) (ev.getY(index2) - ev.getY(index1)));
                float x2 = (ev.getX(index1) + ev.getX(index2)) / 2.0f;
                this.pinchCenterX = x2;
                this.pinchStartCenterX = x2;
                float y2 = (ev.getY(index1) + ev.getY(index2)) / 2.0f;
                this.pinchCenterY = y2;
                this.pinchStartCenterY = y2;
                this.pinchScale = 1.0f;
                this.pinchTranslationX = 0.0f;
                this.pinchTranslationY = 0.0f;
                getParent().requestDisallowInterceptTouchEvent(true);
                this.zoomStarted = true;
                this.isInPinchToZoomTouchMode = true;
            }
            float f = this.pinchStartCenterX;
            float f2 = this.pinchStartCenterY;
            float f3 = this.pinchScale;
            this.pinchTranslationX = (-(f - ((ev.getX(index1) + ev.getX(index2)) / 2.0f))) / f3;
            this.pinchTranslationY = (-(f2 - ((ev.getY(index1) + ev.getY(index2)) / 2.0f))) / f3;
            invalidate();
        } else if (ev.getActionMasked() == 1 || ((ev.getActionMasked() == 6 && checkPointerIds(ev)) || ev.getActionMasked() == 3)) {
            getParent().requestDisallowInterceptTouchEvent(false);
            finishZoom();
        }
        if (this.canZoomGesture || this.tapGesture || this.maybeSwipeToBackGesture) {
            return true;
        }
        return false;
    }

    private void animateSwipeToBack(boolean aplay) {
        ValueAnimator valueAnimator;
        if (this.swipeToBackGesture) {
            this.swipeToBackGesture = false;
            float[] fArr = new float[2];
            float f = this.swipeToBackDy;
            if (aplay) {
                fArr[0] = f;
                fArr[1] = 0.0f;
                valueAnimator = ValueAnimator.ofFloat(fArr);
            } else {
                fArr[0] = f;
                fArr[1] = 0.0f;
                valueAnimator = ValueAnimator.ofFloat(fArr);
            }
            this.swipeToBackAnimator = valueAnimator;
            valueAnimator.addUpdateListener(new GroupCallRenderersContainer$$ExternalSyntheticLambda0(this));
            this.swipeToBackAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    GroupCallRenderersContainer.this.swipeToBackAnimator = null;
                    GroupCallRenderersContainer.this.swipeToBackDy = 0.0f;
                    GroupCallRenderersContainer.this.invalidate();
                }
            });
            this.swipeToBackAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.swipeToBackAnimator.setDuration(aplay ? 350 : 200);
            this.swipeToBackAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
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

    /* renamed from: lambda$animateSwipeToBack$7$org-telegram-ui-Components-voip-GroupCallRenderersContainer  reason: not valid java name */
    public /* synthetic */ void m1587x200965e9(ValueAnimator valueAnimator) {
        this.swipeToBackDy = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    private void finishZoom() {
        if (this.zoomStarted) {
            this.zoomStarted = false;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
            this.zoomBackAnimator = ofFloat;
            ofFloat.addUpdateListener(new GroupCallRenderersContainer$$ExternalSyntheticLambda2(this, this.pinchScale, this.pinchTranslationX, this.pinchTranslationY));
            this.zoomBackAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    GroupCallRenderersContainer.this.zoomBackAnimator = null;
                    GroupCallRenderersContainer.this.pinchScale = 1.0f;
                    float unused = GroupCallRenderersContainer.this.pinchTranslationX = 0.0f;
                    float unused2 = GroupCallRenderersContainer.this.pinchTranslationY = 0.0f;
                    GroupCallRenderersContainer.this.invalidate();
                }
            });
            this.zoomBackAnimator.setDuration(350);
            this.zoomBackAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.zoomBackAnimator.start();
            this.lastUpdateTime = System.currentTimeMillis();
        }
        this.canZoomGesture = false;
        this.isInPinchToZoomTouchMode = false;
    }

    /* renamed from: lambda$finishZoom$8$org-telegram-ui-Components-voip-GroupCallRenderersContainer  reason: not valid java name */
    public /* synthetic */ void m1588x102cdcf5(float fromScale, float fromTranslateX, float fromTranslateY, ValueAnimator valueAnimator) {
        float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.pinchScale = (fromScale * v) + ((1.0f - v) * 1.0f);
        this.pinchTranslationX = fromTranslateX * v;
        this.pinchTranslationY = fromTranslateY * v;
        invalidate();
    }

    private boolean checkPointerIds(MotionEvent ev) {
        if (ev.getPointerCount() < 2) {
            return false;
        }
        if (this.pointerId1 == ev.getPointerId(0) && this.pointerId2 == ev.getPointerId(1)) {
            return true;
        }
        if (this.pointerId1 == ev.getPointerId(1) && this.pointerId2 == ev.getPointerId(0)) {
            return true;
        }
        return false;
    }

    public void hideUi() {
        if (canHideUI()) {
            if (this.hideUiRunnableIsScheduled) {
                AndroidUtilities.cancelRunOnUIThread(this.hideUiRunnable);
                this.hideUiRunnableIsScheduled = false;
            }
            setUiVisible(false);
        }
    }

    public void delayHideUi() {
        if (this.hideUiRunnableIsScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUiRunnable);
        }
        AndroidUtilities.runOnUIThread(this.hideUiRunnable, 3000);
        this.hideUiRunnableIsScheduled = true;
    }

    public boolean isUiVisible() {
        return this.uiVisible;
    }

    public void setProgressToHideUi(float progressToHideUi2) {
        if (this.progressToHideUi != progressToHideUi2) {
            this.progressToHideUi = progressToHideUi2;
            invalidate();
            GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
            if (groupCallMiniTextureView != null) {
                groupCallMiniTextureView.invalidate();
            }
        }
    }

    public void setAmplitude(TLRPC.TL_groupCallParticipant participant, float v) {
        for (int i = 0; i < this.attachedRenderers.size(); i++) {
            if (MessageObject.getPeerId(this.attachedRenderers.get(i).participant.participant.peer) == MessageObject.getPeerId(participant.peer)) {
                this.attachedRenderers.get(i).setAmplitude((double) v);
            }
        }
    }

    public boolean isAnimating() {
        return this.fullscreenAnimator != null;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (GroupCallActivity.isTabletMode) {
            ((ViewGroup.MarginLayoutParams) this.topShadowView.getLayoutParams()).rightMargin = AndroidUtilities.dp(328.0f);
        } else if (GroupCallActivity.isLandscapeMode) {
            ((ViewGroup.MarginLayoutParams) this.topShadowView.getLayoutParams()).rightMargin = isRtmpStream() ? 0 : AndroidUtilities.dp(90.0f);
        } else {
            ((ViewGroup.MarginLayoutParams) this.topShadowView.getLayoutParams()).rightMargin = 0;
        }
        this.rightShadowView.setVisibility((!GroupCallActivity.isLandscapeMode || GroupCallActivity.isTabletMode) ? 8 : 0);
        this.pinContainer.getLayoutParams().height = AndroidUtilities.dp(40.0f);
        this.pinTextView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 0), heightMeasureSpec);
        this.unpinTextView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 0), heightMeasureSpec);
        this.pinContainer.getLayoutParams().width = AndroidUtilities.dp(46.0f) + (!this.hasPinnedVideo ? this.pinTextView : this.unpinTextView).getMeasuredWidth();
        ((ViewGroup.MarginLayoutParams) this.speakingMembersToast.getLayoutParams()).rightMargin = GroupCallActivity.isLandscapeMode ? AndroidUtilities.dp(45.0f) : 0;
        for (int a = 0; a < 2; a++) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) this.undoView[a].getLayoutParams();
            if (this.isTablet) {
                lp.rightMargin = AndroidUtilities.dp(344.0f);
            } else {
                lp.rightMargin = GroupCallActivity.isLandscapeMode ? AndroidUtilities.dp(180.0f) : 0;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean autoPinEnabled() {
        return !this.hasPinnedVideo && System.currentTimeMillis() - this.lastUpdateTime > 2000 && !this.swipeToBackGesture && !this.isInPinchToZoomTouchMode;
    }

    public void setVisibleParticipant(boolean animated) {
        boolean show;
        boolean animated2;
        int leftMargin;
        if (this.inFullscreenMode && !this.isTablet && this.fullscreenParticipant != null && this.fullscreenAnimator == null && this.call != null) {
            int speakingIndex = 0;
            int currenAccount = this.groupCallActivity.getCurrentAccount();
            long j = 500;
            if (System.currentTimeMillis() - this.lastUpdateTooltipTime >= 500) {
                this.lastUpdateTooltipTime = System.currentTimeMillis();
                SpannableStringBuilder spannableStringBuilder = null;
                int i = 0;
                while (i < this.call.currentSpeakingPeers.size()) {
                    long key = this.call.currentSpeakingPeers.keyAt(i);
                    TLRPC.TL_groupCallParticipant participant = this.call.currentSpeakingPeers.get(key);
                    if (participant.self || participant.muted_by_you) {
                    } else if (MessageObject.getPeerId(this.fullscreenParticipant.participant.peer) == MessageObject.getPeerId(participant.peer)) {
                        continue;
                    } else {
                        long peerId = MessageObject.getPeerId(participant.peer);
                        long j2 = key;
                        if (!(SystemClock.uptimeMillis() - participant.lastSpeakTime < j)) {
                            continue;
                        } else {
                            if (spannableStringBuilder == null) {
                                spannableStringBuilder = new SpannableStringBuilder();
                            }
                            if (speakingIndex == 0) {
                                this.speakingToastPeerId = MessageObject.getPeerId(participant.peer);
                            }
                            if (speakingIndex < 3) {
                                TLRPC.User user = peerId > 0 ? MessagesController.getInstance(currenAccount).getUser(Long.valueOf(peerId)) : null;
                                TLRPC.Chat chat = peerId <= 0 ? MessagesController.getInstance(currenAccount).getChat(Long.valueOf(peerId)) : null;
                                if (user != null || chat != null) {
                                    this.speakingMembersAvatars.setObject(speakingIndex, currenAccount, participant);
                                    if (speakingIndex != 0) {
                                        spannableStringBuilder.append(", ");
                                    }
                                    if (user != null) {
                                        if (Build.VERSION.SDK_INT >= 21) {
                                            spannableStringBuilder.append(UserObject.getFirstName(user), new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0);
                                        } else {
                                            spannableStringBuilder.append(UserObject.getFirstName(user));
                                        }
                                    } else if (Build.VERSION.SDK_INT >= 21) {
                                        spannableStringBuilder.append(chat.title, new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0);
                                    } else {
                                        spannableStringBuilder.append(chat.title);
                                    }
                                }
                            }
                            speakingIndex++;
                            if (speakingIndex == 3) {
                                break;
                            }
                        }
                    }
                    i++;
                    j = 500;
                }
                if (speakingIndex == 0) {
                    show = false;
                } else {
                    show = true;
                }
                boolean z = this.showSpeakingMembersToast;
                if (!z && show) {
                    animated2 = false;
                } else if (show || !z) {
                    if (z && show) {
                        this.speakingMembersToastFromLeft = (float) this.speakingMembersToast.getLeft();
                        this.speakingMembersToastFromRight = (float) this.speakingMembersToast.getRight();
                        this.speakingMembersToastFromTextLeft = (float) this.speakingMembersText.getLeft();
                        this.speakingMembersToastChangeProgress = 0.0f;
                    }
                    animated2 = animated;
                } else {
                    this.showSpeakingMembersToast = show;
                    invalidate();
                    return;
                }
                if (!show) {
                    this.showSpeakingMembersToast = show;
                    invalidate();
                    return;
                }
                String s = LocaleController.getPluralString("MembersAreSpeakingToast", speakingIndex);
                int replaceIndex = s.indexOf("un1");
                SpannableStringBuilder spannableStringBuilder1 = new SpannableStringBuilder(s);
                spannableStringBuilder1.replace(replaceIndex, replaceIndex + 3, spannableStringBuilder);
                this.speakingMembersText.setText(spannableStringBuilder1);
                if (speakingIndex == 0) {
                    leftMargin = 0;
                } else if (speakingIndex == 1) {
                    leftMargin = AndroidUtilities.dp(40.0f);
                } else if (speakingIndex == 2) {
                    leftMargin = AndroidUtilities.dp(64.0f);
                } else {
                    leftMargin = AndroidUtilities.dp(88.0f);
                }
                ((FrameLayout.LayoutParams) this.speakingMembersText.getLayoutParams()).leftMargin = leftMargin;
                ((FrameLayout.LayoutParams) this.speakingMembersText.getLayoutParams()).rightMargin = AndroidUtilities.dp(16.0f);
                this.showSpeakingMembersToast = show;
                invalidate();
                while (speakingIndex < 3) {
                    this.speakingMembersAvatars.setObject(speakingIndex, currenAccount, (TLObject) null);
                    speakingIndex++;
                }
                this.speakingMembersAvatars.commitTransition(animated2);
            } else if (this.updateTooltipRunnbale == null) {
                GroupCallRenderersContainer$$ExternalSyntheticLambda7 groupCallRenderersContainer$$ExternalSyntheticLambda7 = new GroupCallRenderersContainer$$ExternalSyntheticLambda7(this);
                this.updateTooltipRunnbale = groupCallRenderersContainer$$ExternalSyntheticLambda7;
                AndroidUtilities.runOnUIThread(groupCallRenderersContainer$$ExternalSyntheticLambda7, (System.currentTimeMillis() - this.lastUpdateTooltipTime) + 50);
            }
        } else if (this.showSpeakingMembersToast != 0) {
            this.showSpeakingMembersToast = false;
            this.showSpeakingMembersToastProgress = 0.0f;
        }
    }

    /* renamed from: lambda$setVisibleParticipant$9$org-telegram-ui-Components-voip-GroupCallRenderersContainer  reason: not valid java name */
    public /* synthetic */ void m1596x9var_CLASSNAME() {
        this.updateTooltipRunnbale = null;
        setVisibleParticipant(true);
    }

    public UndoView getUndoView() {
        if (this.undoView[0].getVisibility() == 0) {
            UndoView[] undoViewArr = this.undoView;
            UndoView old = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = old;
            old.hide(true, 2);
            removeView(this.undoView[0]);
            addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    public boolean isVisible(TLRPC.TL_groupCallParticipant participant) {
        return this.attachedPeerIds.get(MessageObject.getPeerId(participant.peer)) > 0;
    }

    public void attach(GroupCallMiniTextureView view) {
        this.attachedRenderers.add(view);
        long peerId = MessageObject.getPeerId(view.participant.participant.peer);
        LongSparseIntArray longSparseIntArray = this.attachedPeerIds;
        longSparseIntArray.put(peerId, longSparseIntArray.get(peerId, 0) + 1);
    }

    public void detach(GroupCallMiniTextureView view) {
        this.attachedRenderers.remove(view);
        long peerId = MessageObject.getPeerId(view.participant.participant.peer);
        LongSparseIntArray longSparseIntArray = this.attachedPeerIds;
        longSparseIntArray.put(peerId, longSparseIntArray.get(peerId, 0) - 1);
    }

    public void setGroupCall(ChatObject.Call call2) {
        this.call = call2;
    }
}
