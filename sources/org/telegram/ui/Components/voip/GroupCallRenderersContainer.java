package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.TLObject;
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
public class GroupCallRenderersContainer extends FrameLayout {
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
            GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
            groupCallRenderersContainer.hideUiRunnableIsScheduled = false;
            groupCallRenderersContainer.setUiVisible(false);
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

    /* access modifiers changed from: protected */
    public void onBackPressed() {
    }

    /* access modifiers changed from: protected */
    public void onFullScreenModeChanged(boolean z) {
    }

    /* access modifiers changed from: protected */
    public void onUiVisibilityChanged() {
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GroupCallRenderersContainer(Context context, RecyclerView recyclerView, RecyclerView recyclerView2, ArrayList<GroupCallMiniTextureView> arrayList, ChatObject.Call call2, GroupCallActivity groupCallActivity2) {
        super(context);
        Context context2 = context;
        ChatObject.Call call3 = call2;
        GroupCallActivity groupCallActivity3 = groupCallActivity2;
        this.listView = recyclerView;
        this.fullscreenListView = recyclerView2;
        this.attachedRenderers = arrayList;
        this.call = call3;
        this.groupCallActivity = groupCallActivity3;
        AnonymousClass2 r7 = new ImageView(this, context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(ActionBar.getCurrentActionBarHeight(), NUM));
            }
        };
        this.backButton = r7;
        BackDrawable backDrawable = new BackDrawable(false);
        backDrawable.setColor(-1);
        r7.setImageDrawable(backDrawable);
        r7.setScaleType(ImageView.ScaleType.FIT_CENTER);
        r7.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        r7.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 55)));
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
        addView(r7, LayoutHelper.createFrame(56, -1, 51));
        r7.setOnClickListener(new GroupCallRenderersContainer$$ExternalSyntheticLambda5(this));
        AnonymousClass3 r72 = new ImageView(context2) {
            public void invalidate() {
                super.invalidate();
                GroupCallRenderersContainer.this.pinContainer.invalidate();
                GroupCallRenderersContainer.this.invalidate();
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(ActionBar.getCurrentActionBarHeight(), NUM));
            }
        };
        this.pinButton = r72;
        final Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(20.0f), 0, ColorUtils.setAlphaComponent(-1, 100));
        AnonymousClass4 r13 = new View(context2) {
            /* access modifiers changed from: protected */
            public void drawableStateChanged() {
                super.drawableStateChanged();
                createSimpleSelectorRoundRectDrawable.setState(getDrawableState());
            }

            public boolean verifyDrawable(Drawable drawable) {
                return createSimpleSelectorRoundRectDrawable == drawable || super.verifyDrawable(drawable);
            }

            public void jumpDrawablesToCurrentState() {
                super.jumpDrawablesToCurrentState();
                createSimpleSelectorRoundRectDrawable.jumpToCurrentState();
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                float measuredWidth = (((float) GroupCallRenderersContainer.this.pinTextView.getMeasuredWidth()) * (1.0f - GroupCallRenderersContainer.this.pinDrawable.getProgress())) + (((float) GroupCallRenderersContainer.this.unpinTextView.getMeasuredWidth()) * GroupCallRenderersContainer.this.pinDrawable.getProgress());
                canvas.save();
                createSimpleSelectorRoundRectDrawable.setBounds(0, 0, AndroidUtilities.dp(50.0f) + ((int) measuredWidth), getMeasuredHeight());
                createSimpleSelectorRoundRectDrawable.draw(canvas);
                super.dispatchDraw(canvas);
            }
        };
        this.pinContainer = r13;
        r13.setOnClickListener(new GroupCallRenderersContainer$$ExternalSyntheticLambda4(this));
        createSimpleSelectorRoundRectDrawable.setCallback(this.pinContainer);
        addView(this.pinContainer);
        CrossOutDrawable crossOutDrawable = new CrossOutDrawable(context2, R.drawable.msg_pin_filled, (String) null);
        this.pinDrawable = crossOutDrawable;
        crossOutDrawable.setOffsets((float) (-AndroidUtilities.dp(1.0f)), (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(1.0f));
        r72.setImageDrawable(this.pinDrawable);
        r72.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        addView(r72, LayoutHelper.createFrame(56, -1, 51));
        TextView textView = new TextView(context2);
        this.pinTextView = textView;
        textView.setTextColor(-1);
        this.pinTextView.setTextSize(1, 15.0f);
        this.pinTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.pinTextView.setText(LocaleController.getString("CallVideoPin", R.string.CallVideoPin));
        TextView textView2 = new TextView(context2);
        this.unpinTextView = textView2;
        textView2.setTextColor(-1);
        this.unpinTextView.setTextSize(1, 15.0f);
        this.unpinTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.unpinTextView.setText(LocaleController.getString("CallVideoUnpin", R.string.CallVideoUnpin));
        addView(this.pinTextView, LayoutHelper.createFrame(-2, -2, 51));
        addView(this.unpinTextView, LayoutHelper.createFrame(-2, -2, 51));
        ImageView imageView = new ImageView(context2);
        this.pipView = imageView;
        imageView.setVisibility(4);
        this.pipView.setAlpha(0.0f);
        this.pipView.setImageResource(R.drawable.ic_goinline);
        this.pipView.setContentDescription(LocaleController.getString(R.string.AccDescrPipMode));
        int dp = AndroidUtilities.dp(4.0f);
        this.pipView.setPadding(dp, dp, dp, dp);
        this.pipView.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 55)));
        this.pipView.setOnClickListener(new GroupCallRenderersContainer$$ExternalSyntheticLambda6(this, groupCallActivity3));
        addView(this.pipView, LayoutHelper.createFrame(32, 32.0f, 53, 12.0f, 12.0f, 12.0f, 12.0f));
        final Drawable createRoundRectDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_listViewBackground"), 204));
        AnonymousClass5 r3 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                if (GroupCallRenderersContainer.this.speakingMembersToastChangeProgress == 1.0f) {
                    createRoundRectDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    GroupCallRenderersContainer.this.speakingMembersAvatars.setTranslationX(0.0f);
                    GroupCallRenderersContainer.this.speakingMembersText.setTranslationX(0.0f);
                } else {
                    float interpolation = 1.0f - CubicBezierInterpolator.DEFAULT.getInterpolation(GroupCallRenderersContainer.this.speakingMembersToastChangeProgress);
                    float access$400 = (GroupCallRenderersContainer.this.speakingMembersToastFromLeft - ((float) getLeft())) * interpolation;
                    float access$500 = (GroupCallRenderersContainer.this.speakingMembersToastFromTextLeft - ((float) GroupCallRenderersContainer.this.speakingMembersText.getLeft())) * interpolation;
                    createRoundRectDrawable.setBounds((int) access$400, 0, getMeasuredWidth() + ((int) ((GroupCallRenderersContainer.this.speakingMembersToastFromRight - ((float) getRight())) * interpolation)), getMeasuredHeight());
                    GroupCallRenderersContainer.this.speakingMembersAvatars.setTranslationX(access$400);
                    GroupCallRenderersContainer.this.speakingMembersText.setTranslationX(-access$500);
                }
                createRoundRectDrawable.draw(canvas);
                super.dispatchDraw(canvas);
            }
        };
        this.speakingMembersToast = r3;
        AvatarsImageView avatarsImageView = new AvatarsImageView(context2, true);
        this.speakingMembersAvatars = avatarsImageView;
        avatarsImageView.setStyle(10);
        r3.setClipChildren(false);
        r3.setClipToPadding(false);
        r3.addView(avatarsImageView, LayoutHelper.createFrame(100, 32.0f, 16, 0.0f, 0.0f, 0.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.speakingMembersText = textView3;
        textView3.setTextSize(1, 14.0f);
        textView3.setTextColor(-1);
        textView3.setLines(1);
        textView3.setEllipsize(TextUtils.TruncateAt.END);
        r3.addView(textView3, LayoutHelper.createFrame(-2, -2, 16));
        addView(r3, LayoutHelper.createFrame(-2, 36.0f, 1, 0.0f, 0.0f, 0.0f, 0.0f));
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        for (int i = 0; i < 2; i++) {
            this.undoView[i] = new UndoView(context2) {
                public void invalidate() {
                    super.invalidate();
                    GroupCallRenderersContainer.this.invalidate();
                }
            };
            this.undoView[i].setHideAnimationType(2);
            this.undoView[i].setAdditionalTranslationY((float) AndroidUtilities.dp(10.0f));
            addView(this.undoView[i], LayoutHelper.createFrame(-1, -2.0f, 80, 16.0f, 0.0f, 0.0f, 8.0f));
        }
        this.pinContainer.setVisibility(8);
        setIsTablet(GroupCallActivity.isTabletMode);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        onBackPressed();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        if (this.inFullscreenMode) {
            boolean z = !this.hasPinnedVideo;
            this.hasPinnedVideo = z;
            this.pinDrawable.setCrossOut(z, true);
            requestLayout();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(GroupCallActivity groupCallActivity2, View view) {
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

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        if (!this.drawFirst) {
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
                    float y = this.listView.getY() - ((float) getTop());
                    float measuredHeight = (((float) this.listView.getMeasuredHeight()) + y) - this.listView.getTranslationY();
                    float f = this.progressToFullscreenMode;
                    if (groupCallMiniTextureView.secondaryView == null) {
                        f = 0.0f;
                    }
                    canvas.save();
                    float f2 = 1.0f - f;
                    canvas.clipRect(0.0f, y * f2, (float) getMeasuredWidth(), (measuredHeight * f2) + (((float) getMeasuredHeight()) * f));
                    boolean drawChild = super.drawChild(canvas, view, j);
                    canvas.restore();
                    return drawChild;
                } else if (!GroupCallActivity.isTabletMode) {
                    return super.drawChild(canvas, view, j);
                } else {
                    canvas.save();
                    canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    boolean drawChild2 = super.drawChild(canvas, view, j);
                    canvas.restore();
                    return drawChild2;
                }
            } else if (this.drawRenderesOnly) {
                return true;
            } else {
                return super.drawChild(canvas, view, j);
            }
        } else if (!(view instanceof GroupCallMiniTextureView) || !((GroupCallMiniTextureView) view).drawFirst) {
            return true;
        } else {
            float y2 = this.listView.getY() - ((float) getTop());
            float measuredHeight2 = (((float) this.listView.getMeasuredHeight()) + y2) - this.listView.getTranslationY();
            canvas.save();
            canvas.clipRect(0.0f, y2, (float) getMeasuredWidth(), measuredHeight2);
            boolean drawChild3 = super.drawChild(canvas, view, j);
            canvas.restore();
            return drawChild3;
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x0459  */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x0466  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x04aa  */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x04b2  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x04bd A[LOOP:1: B:153:0x04bd->B:160:0x0516, LOOP_START, PHI: r10 
      PHI: (r10v1 int) = (r10v0 int), (r10v2 int) binds: [B:152:0x04bb, B:160:0x0516] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARNING: Removed duplicated region for block: B:168:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dispatchDraw(android.graphics.Canvas r23) {
        /*
            r22 = this;
            r0 = r22
            r8 = r23
            boolean r1 = org.telegram.ui.GroupCallActivity.isTabletMode
            r9 = 1
            r10 = 0
            if (r1 == 0) goto L_0x0011
            r0.drawRenderesOnly = r9
            super.dispatchDraw(r23)
            r0.drawRenderesOnly = r10
        L_0x0011:
            r0.drawFirst = r9
            super.dispatchDraw(r23)
            r0.drawFirst = r10
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.outFullscreenTextureView
            r11 = 1119092736(0x42b40000, float:90.0)
            r12 = 1132396544(0x437var_, float:255.0)
            r13 = 0
            r14 = 1065353216(0x3var_, float:1.0)
            if (r1 != 0) goto L_0x0027
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            if (r1 == 0) goto L_0x0169
        L_0x0027:
            androidx.recyclerview.widget.RecyclerView r1 = r0.listView
            float r1 = r1.getY()
            int r2 = r22.getTop()
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
            r23.save()
            boolean r4 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r4 != 0) goto L_0x006e
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r0.fullscreenTextureView
            if (r5 == 0) goto L_0x006e
            boolean r6 = r5.forceDetached
            if (r6 != 0) goto L_0x006e
            org.telegram.ui.Components.voip.GroupCallGridCell r5 = r5.primaryView
            if (r5 == 0) goto L_0x006e
            float r4 = r14 - r3
            float r1 = r1 * r4
            int r5 = r22.getMeasuredWidth()
            float r5 = (float) r5
            float r2 = r2 * r4
            int r4 = r22.getMeasuredHeight()
            float r4 = (float) r4
            float r4 = r4 * r3
            float r2 = r2 + r4
            r8.clipRect(r13, r1, r5, r2)
            goto L_0x007b
        L_0x006e:
            if (r4 == 0) goto L_0x007b
            int r1 = r22.getMeasuredWidth()
            int r2 = r22.getMeasuredHeight()
            r8.clipRect(r10, r10, r1, r2)
        L_0x007b:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.outFullscreenTextureView
            if (r1 == 0) goto L_0x009f
            android.view.ViewParent r1 = r1.getParent()
            if (r1 == 0) goto L_0x009f
            r23.save()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.outFullscreenTextureView
            float r1 = r1.getX()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r2 = r0.outFullscreenTextureView
            float r2 = r2.getY()
            r8.translate(r1, r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.outFullscreenTextureView
            r1.draw(r8)
            r23.restore()
        L_0x009f:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            if (r1 == 0) goto L_0x0166
            android.view.ViewParent r1 = r1.getParent()
            if (r1 == 0) goto L_0x0166
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            float r1 = r1.getAlpha()
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 == 0) goto L_0x00ef
            android.graphics.RectF r1 = org.telegram.messenger.AndroidUtilities.rectTmp
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r2 = r0.fullscreenTextureView
            float r2 = r2.getX()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.fullscreenTextureView
            float r3 = r3.getY()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            float r4 = r4.getX()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r0.fullscreenTextureView
            int r5 = r5.getMeasuredWidth()
            float r5 = (float) r5
            float r4 = r4 + r5
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r0.fullscreenTextureView
            float r5 = r5.getY()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r6 = r0.fullscreenTextureView
            int r6 = r6.getMeasuredHeight()
            float r6 = (float) r6
            float r5 = r5 + r6
            r1.set(r2, r3, r4, r5)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r2 = r0.fullscreenTextureView
            float r2 = r2.getAlpha()
            float r2 = r2 * r12
            int r2 = (int) r2
            r3 = 31
            r8.saveLayerAlpha(r1, r2, r3)
            goto L_0x00f2
        L_0x00ef:
            r23.save()
        L_0x00f2:
            boolean r1 = r0.swipeToBackGesture
            if (r1 != 0) goto L_0x00fd
            android.animation.ValueAnimator r1 = r0.swipeToBackAnimator
            if (r1 == 0) goto L_0x00fb
            goto L_0x00fd
        L_0x00fb:
            r1 = 0
            goto L_0x00fe
        L_0x00fd:
            r1 = 1
        L_0x00fe:
            if (r1 == 0) goto L_0x0121
            boolean r2 = r22.isRtmpStream()
            if (r2 != 0) goto L_0x0121
            int r2 = r22.getMeasuredWidth()
            int r3 = r22.getMeasuredHeight()
            boolean r4 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r4 != 0) goto L_0x011c
            boolean r4 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r4 == 0) goto L_0x0117
            goto L_0x011c
        L_0x0117:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            goto L_0x011d
        L_0x011c:
            r4 = 0
        L_0x011d:
            int r3 = r3 - r4
            r8.clipRect(r10, r10, r2, r3)
        L_0x0121:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r2 = r0.fullscreenTextureView
            float r2 = r2.getX()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.fullscreenTextureView
            float r3 = r3.getY()
            r8.translate(r2, r3)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r2 = r0.fullscreenTextureView
            float r3 = r0.swipeToBackDy
            r2.setSwipeToBack(r1, r3)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r15 = r0.fullscreenTextureView
            boolean r1 = r0.zoomStarted
            if (r1 != 0) goto L_0x0145
            android.animation.ValueAnimator r1 = r0.zoomBackAnimator
            if (r1 == 0) goto L_0x0142
            goto L_0x0145
        L_0x0142:
            r16 = 0
            goto L_0x0147
        L_0x0145:
            r16 = 1
        L_0x0147:
            float r1 = r0.pinchScale
            float r2 = r0.pinchCenterX
            float r3 = r0.pinchCenterY
            float r4 = r0.pinchTranslationX
            float r5 = r0.pinchTranslationY
            r17 = r1
            r18 = r2
            r19 = r3
            r20 = r4
            r21 = r5
            r15.setZoom(r16, r17, r18, r19, r20, r21)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r1.draw(r8)
            r23.restore()
        L_0x0166:
            r23.restore()
        L_0x0169:
            r15 = 0
        L_0x016a:
            r1 = 2
            r2 = 1090519040(0x41000000, float:8.0)
            r16 = 1073741824(0x40000000, float:2.0)
            if (r15 >= r1) goto L_0x0273
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r15]
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x026f
            r23.save()
            boolean r1 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r1 == 0) goto L_0x0184
            r1 = 0
            goto L_0x0190
        L_0x0184:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r1 = -r1
            float r1 = (float) r1
            float r3 = r0.progressToHideUi
            float r3 = r14 - r3
            float r1 = r1 * r3
        L_0x0190:
            int r3 = r22.getMeasuredWidth()
            float r3 = (float) r3
            int r4 = r22.getMeasuredHeight()
            boolean r5 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r5 == 0) goto L_0x019f
            r5 = 0
            goto L_0x01a3
        L_0x019f:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
        L_0x01a3:
            int r4 = r4 - r5
            float r4 = (float) r4
            float r4 = r4 + r1
            r5 = 1099956224(0x41900000, float:18.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r4 = r4 - r5
            r8.clipRect(r13, r13, r3, r4)
            boolean r3 = r0.isTablet
            if (r3 == 0) goto L_0x01d5
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r15]
            float r1 = r1.getX()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r3 = (float) r3
            float r1 = r1 - r3
            org.telegram.ui.Components.UndoView[] r3 = r0.undoView
            r3 = r3[r15]
            float r3 = r3.getY()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r3 = r3 - r2
            r8.translate(r1, r3)
            goto L_0x0203
        L_0x01d5:
            org.telegram.ui.Components.UndoView[] r3 = r0.undoView
            r3 = r3[r15]
            float r3 = r3.getX()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r3 = r3 - r2
            org.telegram.ui.Components.UndoView[] r2 = r0.undoView
            r2 = r2[r15]
            float r2 = r2.getY()
            boolean r4 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r4 == 0) goto L_0x01f1
            r4 = 0
            goto L_0x01f5
        L_0x01f1:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
        L_0x01f5:
            float r4 = (float) r4
            float r2 = r2 - r4
            float r2 = r2 + r1
            r1 = 1104150528(0x41d00000, float:26.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r2 = r2 - r1
            r8.translate(r3, r2)
        L_0x0203:
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r15]
            float r1 = r1.getAlpha()
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 == 0) goto L_0x0236
            r2 = 0
            r3 = 0
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r15]
            int r1 = r1.getMeasuredWidth()
            float r4 = (float) r1
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r15]
            int r1 = r1.getMeasuredHeight()
            float r5 = (float) r1
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r15]
            float r1 = r1.getAlpha()
            float r1 = r1 * r12
            int r6 = (int) r1
            r7 = 31
            r1 = r23
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            goto L_0x0239
        L_0x0236:
            r23.save()
        L_0x0239:
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r15]
            float r1 = r1.getScaleX()
            org.telegram.ui.Components.UndoView[] r2 = r0.undoView
            r2 = r2[r15]
            float r2 = r2.getScaleY()
            org.telegram.ui.Components.UndoView[] r3 = r0.undoView
            r3 = r3[r15]
            int r3 = r3.getMeasuredWidth()
            float r3 = (float) r3
            float r3 = r3 / r16
            org.telegram.ui.Components.UndoView[] r4 = r0.undoView
            r4 = r4[r15]
            int r4 = r4.getMeasuredHeight()
            float r4 = (float) r4
            float r4 = r4 / r16
            r8.scale(r1, r2, r3, r4)
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r15]
            r1.draw(r8)
            r23.restore()
            r23.restore()
        L_0x026f:
            int r15 = r15 + 1
            goto L_0x016a
        L_0x0273:
            float r1 = r0.progressToFullscreenMode
            float r3 = r0.progressToHideUi
            float r3 = r14 - r3
            float r1 = r1 * r3
            android.animation.ValueAnimator r3 = r0.replaceFullscreenViewAnimator
            if (r3 == 0) goto L_0x02b0
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.outFullscreenTextureView
            if (r3 == 0) goto L_0x02b0
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            if (r4 == 0) goto L_0x02b0
            boolean r3 = r3.hasVideo
            boolean r5 = r4.hasVideo
            if (r3 == r5) goto L_0x029d
            if (r5 != 0) goto L_0x0296
            float r3 = r4.getAlpha()
            float r3 = r14 - r3
            goto L_0x029a
        L_0x0296:
            float r3 = r4.getAlpha()
        L_0x029a:
            float r3 = r3 * r1
            goto L_0x02a2
        L_0x029d:
            if (r5 != 0) goto L_0x02a1
            r3 = 0
            goto L_0x02a2
        L_0x02a1:
            r3 = r1
        L_0x02a2:
            android.graphics.drawable.Drawable r4 = r0.topShadowDrawable
            float r3 = r3 * r12
            int r3 = (int) r3
            r4.setAlpha(r3)
            android.graphics.drawable.Drawable r4 = r0.rightShadowDrawable
            r4.setAlpha(r3)
            goto L_0x02de
        L_0x02b0:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.fullscreenTextureView
            if (r3 == 0) goto L_0x02d1
            android.graphics.drawable.Drawable r4 = r0.topShadowDrawable
            float r12 = r12 * r1
            float r3 = r3.progressToNoVideoStub
            float r3 = r14 - r3
            float r3 = r3 * r12
            int r3 = (int) r3
            r4.setAlpha(r3)
            android.graphics.drawable.Drawable r3 = r0.rightShadowDrawable
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            float r4 = r4.progressToNoVideoStub
            float r4 = r14 - r4
            float r12 = r12 * r4
            int r4 = (int) r12
            r3.setAlpha(r4)
            goto L_0x02de
        L_0x02d1:
            android.graphics.drawable.Drawable r3 = r0.topShadowDrawable
            float r12 = r12 * r1
            int r4 = (int) r12
            r3.setAlpha(r4)
            android.graphics.drawable.Drawable r3 = r0.rightShadowDrawable
            r3.setAlpha(r4)
        L_0x02de:
            android.widget.ImageView r3 = r0.backButton
            r3.setAlpha(r1)
            boolean r3 = r22.isRtmpStream()
            r4 = 4
            if (r3 == 0) goto L_0x031c
            android.widget.ImageView r3 = r0.pinButton
            r3.setAlpha(r13)
            android.widget.ImageView r3 = r0.pinButton
            r3.setVisibility(r4)
            android.widget.ImageView r3 = r0.pipView
            r3.setAlpha(r1)
            android.widget.ImageView r3 = r0.pipView
            r3.setVisibility(r10)
            boolean r3 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r3 == 0) goto L_0x0316
            android.widget.ImageView r3 = r0.pipView
            r4 = 1116733440(0x42900000, float:72.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = -r4
            float r4 = (float) r4
            float r5 = r0.progressToHideUi
            float r5 = r14 - r5
            float r4 = r4 * r5
            r3.setTranslationX(r4)
            goto L_0x0330
        L_0x0316:
            android.widget.ImageView r3 = r0.pipView
            r3.setTranslationX(r13)
            goto L_0x0330
        L_0x031c:
            android.widget.ImageView r3 = r0.pinButton
            r3.setAlpha(r1)
            android.widget.ImageView r3 = r0.pinButton
            r3.setVisibility(r10)
            android.widget.ImageView r3 = r0.pipView
            r3.setAlpha(r13)
            android.widget.ImageView r3 = r0.pipView
            r3.setVisibility(r4)
        L_0x0330:
            int r3 = r22.getMeasuredWidth()
            android.widget.TextView r4 = r0.pinTextView
            int r4 = r4.getMeasuredWidth()
            int r3 = r3 - r4
            float r3 = (float) r3
            int r4 = r22.getMeasuredWidth()
            android.widget.TextView r5 = r0.unpinTextView
            int r5 = r5.getMeasuredWidth()
            int r4 = r4 - r5
            float r4 = (float) r4
            int r5 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            android.widget.TextView r6 = r0.pinTextView
            int r6 = r6.getMeasuredHeight()
            int r5 = r5 - r6
            float r5 = (float) r5
            float r5 = r5 / r16
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r6 = (float) r6
            float r5 = r5 - r6
            org.telegram.ui.Components.CrossOutDrawable r6 = r0.pinDrawable
            float r6 = r6.getProgress()
            float r4 = r4 * r6
            org.telegram.ui.Components.CrossOutDrawable r6 = r0.pinDrawable
            float r6 = r6.getProgress()
            float r6 = r14 - r6
            float r3 = r3 * r6
            float r4 = r4 + r3
            r3 = 1101529088(0x41a80000, float:21.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r4 - r3
            boolean r3 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r3 == 0) goto L_0x0384
            r3 = 1134821376(0x43a40000, float:328.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
        L_0x0381:
            float r3 = (float) r3
            float r4 = r4 - r3
            goto L_0x0391
        L_0x0384:
            boolean r3 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r3 == 0) goto L_0x038f
            r3 = 1127481344(0x43340000, float:180.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            goto L_0x0381
        L_0x038f:
            r3 = 0
            goto L_0x0381
        L_0x0391:
            android.widget.TextView r3 = r0.pinTextView
            r3.setTranslationX(r4)
            android.widget.TextView r3 = r0.unpinTextView
            r3.setTranslationX(r4)
            android.widget.TextView r3 = r0.pinTextView
            r3.setTranslationY(r5)
            android.widget.TextView r3 = r0.unpinTextView
            r3.setTranslationY(r5)
            android.view.View r3 = r0.pinContainer
            r5 = 1108344832(0x42100000, float:36.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r5 = r4 - r5
            r3.setTranslationX(r5)
            android.view.View r3 = r0.pinContainer
            int r5 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            android.view.View r6 = r0.pinContainer
            int r6 = r6.getMeasuredHeight()
            int r5 = r5 - r6
            float r5 = (float) r5
            float r5 = r5 / r16
            r3.setTranslationY(r5)
            android.widget.ImageView r3 = r0.pinButton
            r5 = 1110441984(0x42300000, float:44.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r4 = r4 - r5
            r3.setTranslationX(r4)
            boolean r3 = r22.isRtmpStream()
            if (r3 == 0) goto L_0x03e9
            android.widget.TextView r1 = r0.pinTextView
            r1.setAlpha(r13)
            android.widget.TextView r1 = r0.unpinTextView
            r1.setAlpha(r13)
            android.view.View r1 = r0.pinContainer
            r1.setAlpha(r13)
            goto L_0x040a
        L_0x03e9:
            android.widget.TextView r3 = r0.pinTextView
            org.telegram.ui.Components.CrossOutDrawable r4 = r0.pinDrawable
            float r4 = r4.getProgress()
            float r4 = r14 - r4
            float r4 = r4 * r1
            r3.setAlpha(r4)
            android.widget.TextView r3 = r0.unpinTextView
            org.telegram.ui.Components.CrossOutDrawable r4 = r0.pinDrawable
            float r4 = r4.getProgress()
            float r4 = r4 * r1
            r3.setAlpha(r4)
            android.view.View r3 = r0.pinContainer
            r3.setAlpha(r1)
        L_0x040a:
            float r1 = r0.speakingMembersToastChangeProgress
            int r3 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r3 == 0) goto L_0x0425
            r3 = 1033171465(0x3d94var_, float:0.07272727)
            float r1 = r1 + r3
            r0.speakingMembersToastChangeProgress = r1
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 <= 0) goto L_0x041d
            r0.speakingMembersToastChangeProgress = r14
            goto L_0x0420
        L_0x041d:
            r22.invalidate()
        L_0x0420:
            android.widget.FrameLayout r1 = r0.speakingMembersToast
            r1.invalidate()
        L_0x0425:
            boolean r1 = r0.showSpeakingMembersToast
            r3 = 1037726734(0x3dda740e, float:0.10666667)
            if (r1 == 0) goto L_0x0440
            float r4 = r0.showSpeakingMembersToastProgress
            int r5 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r5 == 0) goto L_0x0440
            float r4 = r4 + r3
            r0.showSpeakingMembersToastProgress = r4
            int r1 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r1 <= 0) goto L_0x043c
            r0.showSpeakingMembersToastProgress = r14
            goto L_0x0455
        L_0x043c:
            r22.invalidate()
            goto L_0x0455
        L_0x0440:
            if (r1 != 0) goto L_0x0455
            float r1 = r0.showSpeakingMembersToastProgress
            int r4 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r4 == 0) goto L_0x0455
            float r1 = r1 - r3
            r0.showSpeakingMembersToastProgress = r1
            int r1 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x0452
            r0.showSpeakingMembersToastProgress = r13
            goto L_0x0455
        L_0x0452:
            r22.invalidate()
        L_0x0455:
            boolean r1 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r1 == 0) goto L_0x0466
            android.widget.FrameLayout r1 = r0.speakingMembersToast
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r1.setTranslationY(r2)
            goto L_0x0485
        L_0x0466:
            android.widget.FrameLayout r1 = r0.speakingMembersToast
            int r3 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            float r3 = (float) r3
            float r4 = r0.progressToHideUi
            float r14 = r14 - r4
            float r3 = r3 * r14
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r4 = (float) r4
            float r3 = r3 + r4
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r4 = r0.progressToHideUi
            float r2 = r2 * r4
            float r3 = r3 + r2
            r1.setTranslationY(r3)
        L_0x0485:
            android.widget.FrameLayout r1 = r0.speakingMembersToast
            float r2 = r0.showSpeakingMembersToastProgress
            float r3 = r0.progressToFullscreenMode
            float r2 = r2 * r3
            r1.setAlpha(r2)
            android.widget.FrameLayout r1 = r0.speakingMembersToast
            float r2 = r0.showSpeakingMembersToastProgress
            r3 = 1056964608(0x3var_, float:0.5)
            float r2 = r2 * r3
            float r2 = r2 + r3
            r1.setScaleX(r2)
            android.widget.FrameLayout r1 = r0.speakingMembersToast
            float r2 = r0.showSpeakingMembersToastProgress
            float r2 = r2 * r3
            float r2 = r2 + r3
            r1.setScaleY(r2)
            boolean r1 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r1 == 0) goto L_0x04b2
            r0.notDrawRenderes = r9
            super.dispatchDraw(r23)
            r0.notDrawRenderes = r10
            goto L_0x04b5
        L_0x04b2:
            super.dispatchDraw(r23)
        L_0x04b5:
            androidx.recyclerview.widget.RecyclerView r1 = r0.fullscreenListView
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0519
        L_0x04bd:
            androidx.recyclerview.widget.RecyclerView r1 = r0.fullscreenListView
            int r1 = r1.getChildCount()
            if (r10 >= r1) goto L_0x0519
            androidx.recyclerview.widget.RecyclerView r1 = r0.fullscreenListView
            android.view.View r1 = r1.getChildAt(r10)
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r1 = (org.telegram.ui.Components.GroupCallFullscreenAdapter.GroupCallUserCell) r1
            int r2 = r1.getVisibility()
            if (r2 != 0) goto L_0x0516
            float r2 = r1.getAlpha()
            int r2 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r2 == 0) goto L_0x0516
            r23.save()
            float r2 = r1.getX()
            androidx.recyclerview.widget.RecyclerView r3 = r0.fullscreenListView
            float r3 = r3.getX()
            float r2 = r2 + r3
            float r3 = r1.getY()
            androidx.recyclerview.widget.RecyclerView r4 = r0.fullscreenListView
            float r4 = r4.getY()
            float r3 = r3 + r4
            r8.translate(r2, r3)
            float r2 = r1.getScaleX()
            float r3 = r1.getScaleY()
            int r4 = r1.getMeasuredWidth()
            float r4 = (float) r4
            float r4 = r4 / r16
            int r5 = r1.getMeasuredHeight()
            float r5 = (float) r5
            float r5 = r5 / r16
            r8.scale(r2, r3, r4, r5)
            r1.drawOverlays(r8)
            r23.restore()
        L_0x0516:
            int r10 = r10 + 1
            goto L_0x04bd
        L_0x0519:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallRenderersContainer.dispatchDraw(android.graphics.Canvas):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:88:0x01ae  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01bc  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01df  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void requestFullscreen(org.telegram.messenger.ChatObject.VideoParticipant r17) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            if (r1 != 0) goto L_0x000a
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.fullscreenParticipant
            if (r2 == 0) goto L_0x0014
        L_0x000a:
            if (r1 == 0) goto L_0x0015
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r0.fullscreenParticipant
            boolean r2 = r1.equals(r2)
            if (r2 == 0) goto L_0x0015
        L_0x0014:
            return
        L_0x0015:
            if (r1 != 0) goto L_0x001a
            r2 = 0
            goto L_0x0022
        L_0x001a:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r1.participant
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer
            long r2 = org.telegram.messenger.MessageObject.getPeerId(r2)
        L_0x0022:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            if (r4 == 0) goto L_0x0029
            r4.runDelayedAnimations()
        L_0x0029:
            android.animation.ValueAnimator r4 = r0.replaceFullscreenViewAnimator
            if (r4 == 0) goto L_0x0030
            r4.cancel()
        L_0x0030:
            org.telegram.messenger.voip.VoIPService r4 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            r5 = 0
            if (r4 == 0) goto L_0x0042
            org.telegram.messenger.ChatObject$VideoParticipant r6 = r0.fullscreenParticipant
            if (r6 == 0) goto L_0x0042
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r7 = r6.participant
            boolean r6 = r6.presentation
            r4.requestFullScreen(r7, r5, r6)
        L_0x0042:
            r0.fullscreenParticipant = r1
            r6 = 1
            if (r4 == 0) goto L_0x0050
            if (r1 == 0) goto L_0x0050
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r7 = r1.participant
            boolean r8 = r1.presentation
            r4.requestFullScreen(r7, r6, r8)
        L_0x0050:
            r0.fullscreenPeerId = r2
            boolean r2 = r0.inFullscreenMode
            long r3 = java.lang.System.currentTimeMillis()
            r0.lastUpdateTime = r3
            r3 = 350(0x15e, double:1.73E-321)
            r7 = 1065353216(0x3var_, float:1.0)
            r8 = 2
            r9 = 0
            r10 = 0
            if (r1 != 0) goto L_0x00d5
            boolean r1 = r0.inFullscreenMode
            if (r1 == 0) goto L_0x00cc
            android.animation.ValueAnimator r1 = r0.fullscreenAnimator
            if (r1 == 0) goto L_0x006e
            r1.cancel()
        L_0x006e:
            r0.inFullscreenMode = r5
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r1.primaryView
            if (r11 != 0) goto L_0x007e
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r11 = r1.secondaryView
            if (r11 != 0) goto L_0x007e
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r1.tabletGridView
            if (r11 == 0) goto L_0x008c
        L_0x007e:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11 = r1.participant
            boolean r1 = r1.presentation
            org.telegram.messenger.ChatObject$Call r12 = r0.call
            boolean r1 = org.telegram.messenger.ChatObject.Call.videoIsActive(r11, r1, r12)
            if (r1 != 0) goto L_0x00c7
        L_0x008c:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r1.forceDetach(r6)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r1.primaryView
            if (r1 == 0) goto L_0x009a
            r1.setRenderer(r10)
        L_0x009a:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r1 = r1.secondaryView
            if (r1 == 0) goto L_0x00a3
            r1.setRenderer(r10)
        L_0x00a3:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r1.tabletGridView
            if (r1 == 0) goto L_0x00ac
            r1.setRenderer(r10)
        L_0x00ac:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            android.view.ViewPropertyAnimator r11 = r1.animate()
            android.view.ViewPropertyAnimator r11 = r11.alpha(r9)
            org.telegram.ui.Components.voip.GroupCallRenderersContainer$7 r12 = new org.telegram.ui.Components.voip.GroupCallRenderersContainer$7
            r12.<init>(r1)
            android.view.ViewPropertyAnimator r1 = r11.setListener(r12)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r3)
            r1.start()
            goto L_0x00cc
        L_0x00c7:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r1.setShowingInFullscreen(r5, r6)
        L_0x00cc:
            android.widget.ImageView r1 = r0.backButton
            r1.setEnabled(r5)
            r0.hasPinnedVideo = r5
            goto L_0x030d
        L_0x00d5:
            r11 = 0
        L_0x00d6:
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r12 = r0.attachedRenderers
            int r12 = r12.size()
            if (r11 >= r12) goto L_0x00fa
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r12 = r0.attachedRenderers
            java.lang.Object r12 = r12.get(r11)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r12 = (org.telegram.ui.Components.voip.GroupCallMiniTextureView) r12
            org.telegram.messenger.ChatObject$VideoParticipant r12 = r12.participant
            boolean r12 = r12.equals(r1)
            if (r12 == 0) goto L_0x00f7
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r12 = r0.attachedRenderers
            java.lang.Object r11 = r12.get(r11)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r11 = (org.telegram.ui.Components.voip.GroupCallMiniTextureView) r11
            goto L_0x00fb
        L_0x00f7:
            int r11 = r11 + 1
            goto L_0x00d6
        L_0x00fa:
            r11 = r10
        L_0x00fb:
            r12 = 1056964608(0x3var_, float:0.5)
            if (r11 == 0) goto L_0x01ff
            android.animation.ValueAnimator r1 = r0.fullscreenAnimator
            if (r1 == 0) goto L_0x0106
            r1.cancel()
        L_0x0106:
            boolean r1 = r0.inFullscreenMode
            if (r1 != 0) goto L_0x0120
            r0.inFullscreenMode = r6
            r16.clearCurrentFullscreenTextureView()
            r0.fullscreenTextureView = r11
            r11.setShowingInFullscreen(r6, r6)
            r16.invalidate()
            org.telegram.ui.Components.CrossOutDrawable r1 = r0.pinDrawable
            boolean r11 = r0.hasPinnedVideo
            r1.setCrossOut(r11, r5)
            goto L_0x0308
        L_0x0120:
            r0.hasPinnedVideo = r5
            org.telegram.ui.Components.CrossOutDrawable r1 = r0.pinDrawable
            r1.setCrossOut(r5, r5)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r1.forceDetach(r5)
            r11.forceDetach(r5)
            boolean r1 = r0.isTablet
            if (r1 != 0) goto L_0x017b
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r13 = r1.primaryView
            if (r13 != 0) goto L_0x0141
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r13 = r1.secondaryView
            if (r13 != 0) goto L_0x0141
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r1.tabletGridView
            if (r1 == 0) goto L_0x017b
        L_0x0141:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r13 = r0.attachedRenderers
            org.telegram.messenger.ChatObject$Call r14 = r0.call
            org.telegram.ui.GroupCallActivity r15 = r0.groupCallActivity
            r1.<init>(r0, r13, r14, r15)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r13 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r14 = r13.primaryView
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r15 = r13.secondaryView
            org.telegram.ui.Components.voip.GroupCallGridCell r13 = r13.tabletGridView
            r1.setViews(r14, r15, r13)
            boolean r13 = r0.inFullscreenMode
            r1.setFullscreenMode(r13, r5)
            r1.updateAttachState(r5)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r13 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r13 = r13.primaryView
            if (r13 == 0) goto L_0x0168
            r13.setRenderer(r1)
        L_0x0168:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r13 = r0.fullscreenTextureView
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r13 = r13.secondaryView
            if (r13 == 0) goto L_0x0171
            r13.setRenderer(r1)
        L_0x0171:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r13 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r13 = r13.tabletGridView
            if (r13 == 0) goto L_0x017c
            r13.setRenderer(r1)
            goto L_0x017c
        L_0x017b:
            r1 = r10
        L_0x017c:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r13 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r14 = r0.attachedRenderers
            org.telegram.messenger.ChatObject$Call r15 = r0.call
            org.telegram.ui.GroupCallActivity r3 = r0.groupCallActivity
            r13.<init>(r0, r14, r15, r3)
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r11.participant
            r13.participant = r3
            org.telegram.ui.Components.voip.GroupCallGridCell r3 = r11.primaryView
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r4 = r11.secondaryView
            org.telegram.ui.Components.voip.GroupCallGridCell r14 = r11.tabletGridView
            r13.setViews(r3, r4, r14)
            boolean r3 = r0.inFullscreenMode
            r13.setFullscreenMode(r3, r5)
            r13.updateAttachState(r5)
            org.telegram.ui.Components.voip.VoIPTextureView r3 = r13.textureView
            org.webrtc.TextureViewRenderer r3 = r3.renderer
            r3.setAlpha(r7)
            org.telegram.ui.Components.voip.VoIPTextureView r3 = r13.textureView
            android.view.TextureView r3 = r3.blurRenderer
            r3.setAlpha(r7)
            org.telegram.ui.Components.voip.GroupCallGridCell r3 = r11.primaryView
            if (r3 == 0) goto L_0x01b1
            r3.setRenderer(r13)
        L_0x01b1:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r3 = r11.secondaryView
            if (r3 == 0) goto L_0x01b8
            r3.setRenderer(r13)
        L_0x01b8:
            org.telegram.ui.Components.voip.GroupCallGridCell r3 = r11.tabletGridView
            if (r3 == 0) goto L_0x01bf
            r3.setRenderer(r13)
        L_0x01bf:
            r13.animateEnter = r6
            r13.setAlpha(r9)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.fullscreenTextureView
            r0.outFullscreenTextureView = r3
            android.util.Property r3 = android.view.View.ALPHA
            float[] r4 = new float[r8]
            r4 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r13, r3, r4)
            r0.replaceFullscreenViewAnimator = r3
            org.telegram.ui.Components.voip.GroupCallRenderersContainer$8 r4 = new org.telegram.ui.Components.voip.GroupCallRenderersContainer$8
            r4.<init>(r13, r11)
            r3.addListener(r4)
            if (r1 == 0) goto L_0x01ea
            r1.setAlpha(r9)
            r1.setScaleX(r12)
            r1.setScaleY(r12)
            r1.animateEnter = r6
        L_0x01ea:
            org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda9 r3 = new org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda9
            r3.<init>(r0, r11, r1)
            r13.runOnFrameRendered(r3)
            r16.clearCurrentFullscreenTextureView()
            r0.fullscreenTextureView = r13
            r13.setShowingInFullscreen(r6, r5)
            r16.update()
            goto L_0x0308
        L_0x01ff:
            boolean r3 = r0.inFullscreenMode
            if (r3 == 0) goto L_0x02bf
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r4 = r3.primaryView
            if (r4 != 0) goto L_0x021f
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r4 = r3.secondaryView
            if (r4 == 0) goto L_0x020f
            r4 = 1
            goto L_0x0210
        L_0x020f:
            r4 = 0
        L_0x0210:
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r3.tabletGridView
            if (r11 == 0) goto L_0x0216
            r11 = 1
            goto L_0x0217
        L_0x0216:
            r11 = 0
        L_0x0217:
            r4 = r4 | r11
            if (r4 == 0) goto L_0x021b
            goto L_0x021f
        L_0x021b:
            r3.forceDetach(r6)
            goto L_0x026e
        L_0x021f:
            r3.forceDetach(r5)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r4 = r0.attachedRenderers
            org.telegram.messenger.ChatObject$Call r11 = r0.call
            org.telegram.ui.GroupCallActivity r13 = r0.groupCallActivity
            r3.<init>(r0, r4, r11, r13)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r4.primaryView
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r13 = r4.secondaryView
            org.telegram.ui.Components.voip.GroupCallGridCell r4 = r4.tabletGridView
            r3.setViews(r11, r13, r4)
            boolean r4 = r0.inFullscreenMode
            r3.setFullscreenMode(r4, r5)
            r3.updateAttachState(r5)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r4 = r4.primaryView
            if (r4 == 0) goto L_0x0249
            r4.setRenderer(r3)
        L_0x0249:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r4 = r4.secondaryView
            if (r4 == 0) goto L_0x0252
            r4.setRenderer(r3)
        L_0x0252:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r4 = r4.tabletGridView
            if (r4 == 0) goto L_0x025b
            r4.setRenderer(r3)
        L_0x025b:
            r3.setAlpha(r9)
            r3.setScaleX(r12)
            r3.setScaleY(r12)
            r3.animateEnter = r6
            org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda8 r4 = new org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda8
            r4.<init>(r0, r3)
            r3.runOnFrameRendered(r4)
        L_0x026e:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r4 = r0.attachedRenderers
            org.telegram.messenger.ChatObject$Call r11 = r0.call
            org.telegram.ui.GroupCallActivity r12 = r0.groupCallActivity
            r3.<init>(r0, r4, r11, r12)
            r3.participant = r1
            boolean r1 = r0.inFullscreenMode
            r3.setFullscreenMode(r1, r5)
            r3.setShowingInFullscreen(r6, r5)
            r3.animateEnter = r6
            r3.setAlpha(r9)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r0.outFullscreenTextureView = r1
            float[] r1 = new float[r8]
            r1 = {0, NUM} // fill-array
            android.animation.ValueAnimator r1 = android.animation.ValueAnimator.ofFloat(r1)
            r0.replaceFullscreenViewAnimator = r1
            org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda3 r4 = new org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda3
            r4.<init>(r0, r3)
            r1.addUpdateListener(r4)
            android.animation.ValueAnimator r1 = r0.replaceFullscreenViewAnimator
            org.telegram.ui.Components.voip.GroupCallRenderersContainer$12 r4 = new org.telegram.ui.Components.voip.GroupCallRenderersContainer$12
            r4.<init>(r3)
            r1.addListener(r4)
            android.animation.ValueAnimator r1 = r0.replaceFullscreenViewAnimator
            r1.start()
            r16.clearCurrentFullscreenTextureView()
            r0.fullscreenTextureView = r3
            r3.setShowingInFullscreen(r6, r5)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r1.updateAttachState(r5)
            r16.update()
            goto L_0x0308
        L_0x02bf:
            r0.inFullscreenMode = r6
            r16.clearCurrentFullscreenTextureView()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r4 = r0.attachedRenderers
            org.telegram.messenger.ChatObject$Call r11 = r0.call
            org.telegram.ui.GroupCallActivity r12 = r0.groupCallActivity
            r3.<init>(r0, r4, r11, r12)
            r0.fullscreenTextureView = r3
            r3.participant = r1
            boolean r1 = r0.inFullscreenMode
            r3.setFullscreenMode(r1, r5)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r1.setShowingInFullscreen(r6, r5)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r1.setShowingInFullscreen(r6, r5)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            android.util.Property r3 = android.view.View.ALPHA
            float[] r4 = new float[r8]
            r4 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r1, r3, r4)
            r0.replaceFullscreenViewAnimator = r1
            org.telegram.ui.Components.voip.GroupCallRenderersContainer$13 r3 = new org.telegram.ui.Components.voip.GroupCallRenderersContainer$13
            r3.<init>()
            r1.addListener(r3)
            android.animation.ValueAnimator r1 = r0.replaceFullscreenViewAnimator
            r1.start()
            r16.invalidate()
            org.telegram.ui.Components.CrossOutDrawable r1 = r0.pinDrawable
            boolean r3 = r0.hasPinnedVideo
            r1.setCrossOut(r3, r5)
        L_0x0308:
            android.widget.ImageView r1 = r0.backButton
            r1.setEnabled(r6)
        L_0x030d:
            boolean r1 = r0.inFullscreenMode
            if (r2 == r1) goto L_0x0390
            if (r1 != 0) goto L_0x0322
            r0.setUiVisible(r6)
            boolean r1 = r0.hideUiRunnableIsScheduled
            if (r1 == 0) goto L_0x0336
            r0.hideUiRunnableIsScheduled = r5
            java.lang.Runnable r1 = r0.hideUiRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1)
            goto L_0x0336
        L_0x0322:
            android.widget.ImageView r1 = r0.backButton
            r1.setVisibility(r5)
            android.widget.ImageView r1 = r0.pinButton
            r1.setVisibility(r5)
            android.widget.TextView r1 = r0.unpinTextView
            r1.setVisibility(r5)
            android.view.View r1 = r0.pinContainer
            r1.setVisibility(r5)
        L_0x0336:
            r0.onFullScreenModeChanged(r6)
            float[] r1 = new float[r8]
            float r2 = r0.progressToFullscreenMode
            r1[r5] = r2
            boolean r2 = r0.inFullscreenMode
            if (r2 == 0) goto L_0x0344
            goto L_0x0345
        L_0x0344:
            r7 = 0
        L_0x0345:
            r1[r6] = r7
            android.animation.ValueAnimator r1 = android.animation.ValueAnimator.ofFloat(r1)
            r0.fullscreenAnimator = r1
            org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda0
            r2.<init>(r0)
            r1.addUpdateListener(r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r1.animateToFullscreen = r6
            org.telegram.ui.GroupCallActivity r2 = r0.groupCallActivity
            int r2 = r2.getCurrentAccount()
            boolean r3 = r0.swipeToBackGesture
            r0.swipedBack = r3
            org.telegram.messenger.NotificationCenter r3 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r4 = r0.animationIndex
            int r3 = r3.setAnimationInProgress(r4, r10)
            r0.animationIndex = r3
            android.animation.ValueAnimator r3 = r0.fullscreenAnimator
            org.telegram.ui.Components.voip.GroupCallRenderersContainer$14 r4 = new org.telegram.ui.Components.voip.GroupCallRenderersContainer$14
            r4.<init>(r2, r1)
            r3.addListener(r4)
            android.animation.ValueAnimator r1 = r0.fullscreenAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r1.setInterpolator(r2)
            android.animation.ValueAnimator r1 = r0.fullscreenAnimator
            r2 = 350(0x15e, double:1.73E-321)
            r1.setDuration(r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.VoIPTextureView r1 = r1.textureView
            android.animation.ValueAnimator r2 = r0.fullscreenAnimator
            r1.synchOrRunAnimation(r2)
        L_0x0390:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.fullscreenParticipant
            if (r1 != 0) goto L_0x0395
            r5 = 1
        L_0x0395:
            r0.animateSwipeToBack(r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallRenderersContainer.requestFullscreen(org.telegram.messenger.ChatObject$VideoParticipant):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestFullscreen$3(final GroupCallMiniTextureView groupCallMiniTextureView, final GroupCallMiniTextureView groupCallMiniTextureView2) {
        ValueAnimator valueAnimator = this.replaceFullscreenViewAnimator;
        if (valueAnimator != null) {
            valueAnimator.start();
        }
        groupCallMiniTextureView.animate().scaleX(0.5f).scaleY(0.5f).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (groupCallMiniTextureView.getParent() != null) {
                    GroupCallRenderersContainer.this.removeView(groupCallMiniTextureView);
                    groupCallMiniTextureView.release();
                }
            }
        }).setDuration(100).start();
        if (groupCallMiniTextureView2 != null) {
            groupCallMiniTextureView2.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(100).setListener(new AnimatorListenerAdapter(this) {
                public void onAnimationEnd(Animator animator) {
                    groupCallMiniTextureView2.animateEnter = false;
                }
            }).start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestFullscreen$4(final GroupCallMiniTextureView groupCallMiniTextureView) {
        groupCallMiniTextureView.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setListener(new AnimatorListenerAdapter(this) {
            public void onAnimationEnd(Animator animator) {
                groupCallMiniTextureView.animateEnter = false;
            }
        }).setDuration(150).start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestFullscreen$5(GroupCallMiniTextureView groupCallMiniTextureView, ValueAnimator valueAnimator) {
        groupCallMiniTextureView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
        invalidate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestFullscreen$6(ValueAnimator valueAnimator) {
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

    /* access modifiers changed from: private */
    public void setUiVisible(boolean z) {
        if (this.uiVisible != z) {
            this.uiVisible = z;
            onUiVisibilityChanged();
            if (!z || !this.inFullscreenMode) {
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
    public boolean canHideUI() {
        return this.inFullscreenMode;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return onTouchEvent(motionEvent);
    }

    /* JADX WARNING: Removed duplicated region for block: B:120:0x025d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r15) {
        /*
            r14 = this;
            boolean r0 = r14.maybeSwipeToBackGesture
            r1 = 0
            r2 = 3
            r3 = 1
            r4 = 0
            if (r0 != 0) goto L_0x000c
            boolean r0 = r14.swipeToBackGesture
            if (r0 == 0) goto L_0x0041
        L_0x000c:
            int r0 = r15.getActionMasked()
            if (r0 == r3) goto L_0x0018
            int r0 = r15.getActionMasked()
            if (r0 != r2) goto L_0x0041
        L_0x0018:
            r14.maybeSwipeToBackGesture = r4
            boolean r0 = r14.swipeToBackGesture
            if (r0 == 0) goto L_0x003e
            int r0 = r15.getActionMasked()
            if (r0 != r3) goto L_0x003b
            float r0 = r14.swipeToBackDy
            float r0 = java.lang.Math.abs(r0)
            r5 = 1123024896(0x42var_, float:120.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x003b
            org.telegram.ui.GroupCallActivity r0 = r14.groupCallActivity
            r0.fullscreenFor(r1)
            goto L_0x003e
        L_0x003b:
            r14.animateSwipeToBack(r4)
        L_0x003e:
            r14.invalidate()
        L_0x0041:
            boolean r0 = r14.inFullscreenMode
            if (r0 == 0) goto L_0x04a9
            boolean r0 = r14.maybeSwipeToBackGesture
            if (r0 != 0) goto L_0x0063
            boolean r0 = r14.swipeToBackGesture
            if (r0 != 0) goto L_0x0063
            boolean r0 = r14.tapGesture
            if (r0 != 0) goto L_0x0063
            boolean r0 = r14.canZoomGesture
            if (r0 != 0) goto L_0x0063
            boolean r0 = r14.isInPinchToZoomTouchMode
            if (r0 != 0) goto L_0x0063
            boolean r0 = r14.zoomStarted
            if (r0 != 0) goto L_0x0063
            int r0 = r15.getActionMasked()
            if (r0 != 0) goto L_0x04a9
        L_0x0063:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r0 = r14.fullscreenTextureView
            if (r0 != 0) goto L_0x0069
            goto L_0x04a9
        L_0x0069:
            int r0 = r15.getActionMasked()
            if (r0 != 0) goto L_0x0079
            r14.maybeSwipeToBackGesture = r4
            r14.swipeToBackGesture = r4
            r14.canZoomGesture = r4
            r14.isInPinchToZoomTouchMode = r4
            r14.zoomStarted = r4
        L_0x0079:
            int r0 = r15.getActionMasked()
            if (r0 != 0) goto L_0x009d
            android.animation.ValueAnimator r0 = r14.swipeToBackAnimator
            if (r0 == 0) goto L_0x009d
            r14.maybeSwipeToBackGesture = r4
            r14.swipeToBackGesture = r3
            float r0 = r15.getY()
            float r5 = r14.swipeToBackDy
            float r0 = r0 - r5
            r14.tapY = r0
            android.animation.ValueAnimator r0 = r14.swipeToBackAnimator
            r0.removeAllListeners()
            android.animation.ValueAnimator r0 = r14.swipeToBackAnimator
            r0.cancel()
            r14.swipeToBackAnimator = r1
            goto L_0x00a5
        L_0x009d:
            android.animation.ValueAnimator r0 = r14.swipeToBackAnimator
            if (r0 == 0) goto L_0x00a5
            r14.finishZoom()
            return r4
        L_0x00a5:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r0 = r14.fullscreenTextureView
            float r1 = r15.getX()
            float r5 = r15.getY()
            boolean r0 = r0.isInsideStopScreenButton(r1, r5)
            if (r0 == 0) goto L_0x00b6
            return r4
        L_0x00b6:
            int r0 = r15.getActionMasked()
            r1 = 1119092736(0x42b40000, float:90.0)
            r5 = 0
            r6 = 2
            if (r0 != 0) goto L_0x0122
            boolean r0 = r14.swipeToBackGesture
            if (r0 != 0) goto L_0x0122
            android.graphics.RectF r0 = org.telegram.messenger.AndroidUtilities.rectTmp
            int r7 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            float r7 = (float) r7
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r8 = r14.fullscreenTextureView
            int r8 = r8.getMeasuredWidth()
            boolean r9 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r9 == 0) goto L_0x00df
            boolean r9 = r14.uiVisible
            if (r9 == 0) goto L_0x00df
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r9 = -r9
            goto L_0x00e0
        L_0x00df:
            r9 = 0
        L_0x00e0:
            int r8 = r8 + r9
            float r8 = (float) r8
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r9 = r14.fullscreenTextureView
            int r9 = r9.getMeasuredHeight()
            boolean r10 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r10 != 0) goto L_0x00f6
            boolean r10 = r14.uiVisible
            if (r10 == 0) goto L_0x00f6
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r10 = -r10
            goto L_0x00f7
        L_0x00f6:
            r10 = 0
        L_0x00f7:
            int r9 = r9 + r10
            float r9 = (float) r9
            r0.set(r5, r7, r8, r9)
            float r7 = r15.getX()
            float r8 = r15.getY()
            boolean r0 = r0.contains(r7, r8)
            if (r0 == 0) goto L_0x01ab
            long r7 = java.lang.System.currentTimeMillis()
            r14.tapTime = r7
            r14.tapGesture = r3
            r14.maybeSwipeToBackGesture = r3
            float r0 = r15.getX()
            r14.tapX = r0
            float r0 = r15.getY()
            r14.tapY = r0
            goto L_0x01ab
        L_0x0122:
            boolean r0 = r14.maybeSwipeToBackGesture
            if (r0 != 0) goto L_0x012e
            boolean r0 = r14.swipeToBackGesture
            if (r0 != 0) goto L_0x012e
            boolean r0 = r14.tapGesture
            if (r0 == 0) goto L_0x01ab
        L_0x012e:
            int r0 = r15.getActionMasked()
            if (r0 != r6) goto L_0x01ab
            float r0 = r14.tapX
            float r7 = r15.getX()
            float r0 = r0 - r7
            float r0 = java.lang.Math.abs(r0)
            int r7 = r14.touchSlop
            float r7 = (float) r7
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 > 0) goto L_0x0158
            float r0 = r14.tapY
            float r7 = r15.getY()
            float r0 = r0 - r7
            float r0 = java.lang.Math.abs(r0)
            int r7 = r14.touchSlop
            float r7 = (float) r7
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 <= 0) goto L_0x015a
        L_0x0158:
            r14.tapGesture = r4
        L_0x015a:
            boolean r0 = r14.maybeSwipeToBackGesture
            if (r0 == 0) goto L_0x0181
            boolean r0 = r14.zoomStarted
            if (r0 != 0) goto L_0x0181
            float r0 = r14.tapY
            float r7 = r15.getY()
            float r0 = r0 - r7
            float r0 = java.lang.Math.abs(r0)
            int r7 = r14.touchSlop
            int r7 = r7 * 2
            float r7 = (float) r7
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 <= 0) goto L_0x0181
            float r0 = r15.getY()
            r14.tapY = r0
            r14.maybeSwipeToBackGesture = r4
            r14.swipeToBackGesture = r3
            goto L_0x0191
        L_0x0181:
            boolean r0 = r14.swipeToBackGesture
            if (r0 == 0) goto L_0x0191
            float r0 = r15.getY()
            float r7 = r14.tapY
            float r0 = r0 - r7
            r14.swipeToBackDy = r0
            r14.invalidate()
        L_0x0191:
            boolean r0 = r14.maybeSwipeToBackGesture
            if (r0 == 0) goto L_0x01ab
            float r0 = r14.tapX
            float r7 = r15.getX()
            float r0 = r0 - r7
            float r0 = java.lang.Math.abs(r0)
            int r7 = r14.touchSlop
            int r7 = r7 * 4
            float r7 = (float) r7
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 <= 0) goto L_0x01ab
            r14.maybeSwipeToBackGesture = r4
        L_0x01ab:
            boolean r0 = r14.tapGesture
            if (r0 == 0) goto L_0x0268
            int r0 = r15.getActionMasked()
            if (r0 != r3) goto L_0x0268
            long r7 = java.lang.System.currentTimeMillis()
            long r9 = r14.tapTime
            long r7 = r7 - r9
            r9 = 200(0xc8, double:9.9E-322)
            int r0 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x0268
            r14.tapGesture = r4
            boolean r0 = r14.showSpeakingMembersToast
            if (r0 == 0) goto L_0x025a
            android.graphics.RectF r0 = org.telegram.messenger.AndroidUtilities.rectTmp
            android.widget.FrameLayout r7 = r14.speakingMembersToast
            float r7 = r7.getX()
            android.widget.FrameLayout r8 = r14.speakingMembersToast
            float r8 = r8.getY()
            android.widget.FrameLayout r9 = r14.speakingMembersToast
            float r9 = r9.getX()
            android.widget.FrameLayout r10 = r14.speakingMembersToast
            int r10 = r10.getWidth()
            float r10 = (float) r10
            float r9 = r9 + r10
            android.widget.FrameLayout r10 = r14.speakingMembersToast
            float r10 = r10.getY()
            android.widget.FrameLayout r11 = r14.speakingMembersToast
            int r11 = r11.getHeight()
            float r11 = (float) r11
            float r10 = r10 + r11
            r0.set(r7, r8, r9, r10)
            org.telegram.messenger.ChatObject$Call r7 = r14.call
            if (r7 == 0) goto L_0x025a
            float r7 = r15.getX()
            float r8 = r15.getY()
            boolean r0 = r0.contains(r7, r8)
            if (r0 == 0) goto L_0x025a
            r0 = 0
            r7 = 0
            r8 = 0
        L_0x020a:
            org.telegram.messenger.ChatObject$Call r9 = r14.call
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r9 = r9.visibleVideoParticipants
            int r9 = r9.size()
            if (r0 >= r9) goto L_0x0240
            long r9 = r14.speakingToastPeerId
            org.telegram.messenger.ChatObject$Call r11 = r14.call
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r11 = r11.visibleVideoParticipants
            java.lang.Object r11 = r11.get(r0)
            org.telegram.messenger.ChatObject$VideoParticipant r11 = (org.telegram.messenger.ChatObject.VideoParticipant) r11
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11 = r11.participant
            org.telegram.tgnet.TLRPC$Peer r11 = r11.peer
            long r11 = org.telegram.messenger.MessageObject.getPeerId(r11)
            int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r13 != 0) goto L_0x023d
            org.telegram.ui.GroupCallActivity r7 = r14.groupCallActivity
            org.telegram.messenger.ChatObject$Call r8 = r14.call
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r8 = r8.visibleVideoParticipants
            java.lang.Object r8 = r8.get(r0)
            org.telegram.messenger.ChatObject$VideoParticipant r8 = (org.telegram.messenger.ChatObject.VideoParticipant) r8
            r7.fullscreenFor(r8)
            r7 = 1
            r8 = 1
        L_0x023d:
            int r0 = r0 + 1
            goto L_0x020a
        L_0x0240:
            if (r7 != 0) goto L_0x025b
            org.telegram.messenger.ChatObject$Call r0 = r14.call
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r0 = r0.participants
            long r7 = r14.speakingToastPeerId
            java.lang.Object r0 = r0.get(r7)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r0 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r0
            org.telegram.ui.GroupCallActivity r7 = r14.groupCallActivity
            org.telegram.messenger.ChatObject$VideoParticipant r8 = new org.telegram.messenger.ChatObject$VideoParticipant
            r8.<init>(r0, r4, r4)
            r7.fullscreenFor(r8)
            r8 = 1
            goto L_0x025b
        L_0x025a:
            r8 = 0
        L_0x025b:
            if (r8 != 0) goto L_0x0263
            boolean r0 = r14.uiVisible
            r0 = r0 ^ r3
            r14.setUiVisible(r0)
        L_0x0263:
            r14.swipeToBackDy = r5
            r14.invalidate()
        L_0x0268:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r0 = r14.fullscreenTextureView
            boolean r0 = r0.hasVideo
            if (r0 == 0) goto L_0x0497
            boolean r0 = r14.swipeToBackGesture
            if (r0 == 0) goto L_0x0274
            goto L_0x0497
        L_0x0274:
            int r0 = r15.getActionMasked()
            r7 = 1065353216(0x3var_, float:1.0)
            r8 = 1073741824(0x40000000, float:2.0)
            if (r0 == 0) goto L_0x0382
            int r0 = r15.getActionMasked()
            r9 = 5
            if (r0 != r9) goto L_0x0287
            goto L_0x0382
        L_0x0287:
            int r0 = r15.getActionMasked()
            if (r0 != r6) goto L_0x035d
            boolean r0 = r14.isInPinchToZoomTouchMode
            if (r0 == 0) goto L_0x035d
            r0 = -1
            r1 = 0
            r2 = -1
            r6 = -1
        L_0x0295:
            int r9 = r15.getPointerCount()
            if (r1 >= r9) goto L_0x02b0
            int r9 = r14.pointerId1
            int r10 = r15.getPointerId(r1)
            if (r9 != r10) goto L_0x02a4
            r2 = r1
        L_0x02a4:
            int r9 = r14.pointerId2
            int r10 = r15.getPointerId(r1)
            if (r9 != r10) goto L_0x02ad
            r6 = r1
        L_0x02ad:
            int r1 = r1 + 1
            goto L_0x0295
        L_0x02b0:
            if (r2 == r0) goto L_0x0350
            if (r6 != r0) goto L_0x02b6
            goto L_0x0350
        L_0x02b6:
            float r0 = r15.getX(r6)
            float r1 = r15.getX(r2)
            float r0 = r0 - r1
            double r0 = (double) r0
            float r9 = r15.getY(r6)
            float r10 = r15.getY(r2)
            float r9 = r9 - r10
            double r9 = (double) r9
            double r0 = java.lang.Math.hypot(r0, r9)
            float r0 = (float) r0
            float r1 = r14.pinchStartDistance
            float r0 = r0 / r1
            r14.pinchScale = r0
            r1 = 1065395159(0x3var_a3d7, float:1.005)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x0327
            boolean r0 = r14.zoomStarted
            if (r0 != 0) goto L_0x0327
            float r0 = r15.getX(r6)
            float r1 = r15.getX(r2)
            float r0 = r0 - r1
            double r0 = (double) r0
            float r9 = r15.getY(r6)
            float r10 = r15.getY(r2)
            float r9 = r9 - r10
            double r9 = (double) r9
            double r0 = java.lang.Math.hypot(r0, r9)
            float r0 = (float) r0
            r14.pinchStartDistance = r0
            float r0 = r15.getX(r2)
            float r1 = r15.getX(r6)
            float r0 = r0 + r1
            float r0 = r0 / r8
            r14.pinchCenterX = r0
            r14.pinchStartCenterX = r0
            float r0 = r15.getY(r2)
            float r1 = r15.getY(r6)
            float r0 = r0 + r1
            float r0 = r0 / r8
            r14.pinchCenterY = r0
            r14.pinchStartCenterY = r0
            r14.pinchScale = r7
            r14.pinchTranslationX = r5
            r14.pinchTranslationY = r5
            android.view.ViewParent r0 = r14.getParent()
            r0.requestDisallowInterceptTouchEvent(r3)
            r14.zoomStarted = r3
            r14.isInPinchToZoomTouchMode = r3
        L_0x0327:
            float r0 = r15.getX(r2)
            float r1 = r15.getX(r6)
            float r0 = r0 + r1
            float r0 = r0 / r8
            float r1 = r15.getY(r2)
            float r15 = r15.getY(r6)
            float r1 = r1 + r15
            float r1 = r1 / r8
            float r15 = r14.pinchStartCenterX
            float r15 = r15 - r0
            float r0 = r14.pinchStartCenterY
            float r0 = r0 - r1
            float r15 = -r15
            float r1 = r14.pinchScale
            float r15 = r15 / r1
            r14.pinchTranslationX = r15
            float r15 = -r0
            float r15 = r15 / r1
            r14.pinchTranslationY = r15
            r14.invalidate()
            goto L_0x0488
        L_0x0350:
            android.view.ViewParent r15 = r14.getParent()
            r15.requestDisallowInterceptTouchEvent(r4)
            r14.finishZoom()
            boolean r15 = r14.maybeSwipeToBackGesture
            return r15
        L_0x035d:
            int r0 = r15.getActionMasked()
            if (r0 == r3) goto L_0x0376
            int r0 = r15.getActionMasked()
            r1 = 6
            if (r0 != r1) goto L_0x0370
            boolean r0 = r14.checkPointerIds(r15)
            if (r0 != 0) goto L_0x0376
        L_0x0370:
            int r15 = r15.getActionMasked()
            if (r15 != r2) goto L_0x0488
        L_0x0376:
            android.view.ViewParent r15 = r14.getParent()
            r15.requestDisallowInterceptTouchEvent(r4)
            r14.finishZoom()
            goto L_0x0488
        L_0x0382:
            int r0 = r15.getActionMasked()
            if (r0 != 0) goto L_0x0437
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r0 = r14.fullscreenTextureView
            org.telegram.ui.Components.voip.VoIPTextureView r0 = r0.textureView
            org.webrtc.TextureViewRenderer r0 = r0.renderer
            android.graphics.RectF r2 = org.telegram.messenger.AndroidUtilities.rectTmp
            float r5 = r0.getX()
            float r9 = r0.getY()
            float r10 = r0.getX()
            int r11 = r0.getMeasuredWidth()
            float r11 = (float) r11
            float r10 = r10 + r11
            float r11 = r0.getY()
            int r12 = r0.getMeasuredHeight()
            float r12 = (float) r12
            float r11 = r11 + r12
            r2.set(r5, r9, r10, r11)
            int r5 = r0.getMeasuredHeight()
            float r5 = (float) r5
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r9 = r14.fullscreenTextureView
            org.telegram.ui.Components.voip.VoIPTextureView r9 = r9.textureView
            float r9 = r9.scaleTextureToFill
            float r5 = r5 * r9
            int r9 = r0.getMeasuredHeight()
            float r9 = (float) r9
            float r5 = r5 - r9
            float r5 = r5 / r8
            int r9 = r0.getMeasuredWidth()
            float r9 = (float) r9
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r10 = r14.fullscreenTextureView
            org.telegram.ui.Components.voip.VoIPTextureView r10 = r10.textureView
            float r10 = r10.scaleTextureToFill
            float r9 = r9 * r10
            int r0 = r0.getMeasuredWidth()
            float r0 = (float) r0
            float r9 = r9 - r0
            float r9 = r9 / r8
            r2.inset(r5, r9)
            boolean r0 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r0 != 0) goto L_0x0400
            float r0 = r2.top
            int r5 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            float r5 = (float) r5
            float r0 = java.lang.Math.max(r0, r5)
            r2.top = r0
            float r0 = r2.bottom
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r14.fullscreenTextureView
            int r5 = r5.getMeasuredHeight()
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r5 = r5 - r1
            float r1 = (float) r5
            float r0 = java.lang.Math.min(r0, r1)
            r2.bottom = r0
            goto L_0x0421
        L_0x0400:
            float r0 = r2.top
            int r5 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            float r5 = (float) r5
            float r0 = java.lang.Math.max(r0, r5)
            r2.top = r0
            float r0 = r2.right
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r14.fullscreenTextureView
            int r5 = r5.getMeasuredWidth()
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r5 = r5 - r1
            float r1 = (float) r5
            float r0 = java.lang.Math.min(r0, r1)
            r2.right = r0
        L_0x0421:
            float r0 = r15.getX()
            float r1 = r15.getY()
            boolean r0 = r2.contains(r0, r1)
            r14.canZoomGesture = r0
            if (r0 != 0) goto L_0x0437
            r14.finishZoom()
            boolean r15 = r14.maybeSwipeToBackGesture
            return r15
        L_0x0437:
            boolean r0 = r14.isInPinchToZoomTouchMode
            if (r0 != 0) goto L_0x0488
            int r0 = r15.getPointerCount()
            if (r0 != r6) goto L_0x0488
            float r0 = r15.getX(r3)
            float r1 = r15.getX(r4)
            float r0 = r0 - r1
            double r0 = (double) r0
            float r2 = r15.getY(r3)
            float r5 = r15.getY(r4)
            float r2 = r2 - r5
            double r5 = (double) r2
            double r0 = java.lang.Math.hypot(r0, r5)
            float r0 = (float) r0
            r14.pinchStartDistance = r0
            float r0 = r15.getX(r4)
            float r1 = r15.getX(r3)
            float r0 = r0 + r1
            float r0 = r0 / r8
            r14.pinchCenterX = r0
            r14.pinchStartCenterX = r0
            float r0 = r15.getY(r4)
            float r1 = r15.getY(r3)
            float r0 = r0 + r1
            float r0 = r0 / r8
            r14.pinchCenterY = r0
            r14.pinchStartCenterY = r0
            r14.pinchScale = r7
            int r0 = r15.getPointerId(r4)
            r14.pointerId1 = r0
            int r15 = r15.getPointerId(r3)
            r14.pointerId2 = r15
            r14.isInPinchToZoomTouchMode = r3
        L_0x0488:
            boolean r15 = r14.canZoomGesture
            if (r15 != 0) goto L_0x0496
            boolean r15 = r14.tapGesture
            if (r15 != 0) goto L_0x0496
            boolean r15 = r14.maybeSwipeToBackGesture
            if (r15 == 0) goto L_0x0495
            goto L_0x0496
        L_0x0495:
            r3 = 0
        L_0x0496:
            return r3
        L_0x0497:
            r14.finishZoom()
            boolean r15 = r14.tapGesture
            if (r15 != 0) goto L_0x04a8
            boolean r15 = r14.swipeToBackGesture
            if (r15 != 0) goto L_0x04a8
            boolean r15 = r14.maybeSwipeToBackGesture
            if (r15 == 0) goto L_0x04a7
            goto L_0x04a8
        L_0x04a7:
            r3 = 0
        L_0x04a8:
            return r3
        L_0x04a9:
            r14.finishZoom()
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallRenderersContainer.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void animateSwipeToBack(boolean z) {
        ValueAnimator valueAnimator;
        if (this.swipeToBackGesture) {
            this.swipeToBackGesture = false;
            float[] fArr = new float[2];
            float f = this.swipeToBackDy;
            if (z) {
                fArr[0] = f;
                fArr[1] = 0.0f;
                valueAnimator = ValueAnimator.ofFloat(fArr);
            } else {
                fArr[0] = f;
                fArr[1] = 0.0f;
                valueAnimator = ValueAnimator.ofFloat(fArr);
            }
            this.swipeToBackAnimator = valueAnimator;
            valueAnimator.addUpdateListener(new GroupCallRenderersContainer$$ExternalSyntheticLambda1(this));
            this.swipeToBackAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                    groupCallRenderersContainer.swipeToBackAnimator = null;
                    groupCallRenderersContainer.swipeToBackDy = 0.0f;
                    groupCallRenderersContainer.invalidate();
                }
            });
            ValueAnimator valueAnimator2 = this.swipeToBackAnimator;
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            valueAnimator2.setInterpolator(cubicBezierInterpolator);
            this.swipeToBackAnimator.setDuration(z ? 350 : 200);
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateSwipeToBack$7(ValueAnimator valueAnimator) {
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
                public void onAnimationEnd(Animator animator) {
                    GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                    groupCallRenderersContainer.zoomBackAnimator = null;
                    groupCallRenderersContainer.pinchScale = 1.0f;
                    float unused = groupCallRenderersContainer.pinchTranslationX = 0.0f;
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

    /* access modifiers changed from: private */
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
        if (this.pointerId1 == motionEvent.getPointerId(1) && this.pointerId2 == motionEvent.getPointerId(0)) {
            return true;
        }
        return false;
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

    public void setProgressToHideUi(float f) {
        if (this.progressToHideUi != f) {
            this.progressToHideUi = f;
            invalidate();
            GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
            if (groupCallMiniTextureView != null) {
                groupCallMiniTextureView.invalidate();
            }
        }
    }

    public void setAmplitude(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, float f) {
        for (int i = 0; i < this.attachedRenderers.size(); i++) {
            if (MessageObject.getPeerId(this.attachedRenderers.get(i).participant.participant.peer) == MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer)) {
                this.attachedRenderers.get(i).setAmplitude((double) f);
            }
        }
    }

    public boolean isAnimating() {
        return this.fullscreenAnimator != null;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
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
        if (this.inFullscreenMode && !this.isTablet && this.fullscreenParticipant != null && this.fullscreenAnimator == null && this.call != null) {
            int currentAccount = this.groupCallActivity.getCurrentAccount();
            long j = 500;
            if (System.currentTimeMillis() - this.lastUpdateTooltipTime >= 500) {
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
                } else if (z3 || !z4) {
                    if (z4 && z3) {
                        this.speakingMembersToastFromLeft = (float) this.speakingMembersToast.getLeft();
                        this.speakingMembersToastFromRight = (float) this.speakingMembersToast.getRight();
                        this.speakingMembersToastFromTextLeft = (float) this.speakingMembersText.getLeft();
                        this.speakingMembersToastChangeProgress = 0.0f;
                    }
                    z2 = z;
                } else {
                    this.showSpeakingMembersToast = z3;
                    invalidate();
                    return;
                }
                if (!z3) {
                    this.showSpeakingMembersToast = z3;
                    invalidate();
                    return;
                }
                String pluralString = LocaleController.getPluralString("MembersAreSpeakingToast", i4);
                int indexOf = pluralString.indexOf("un1");
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(pluralString);
                spannableStringBuilder2.replace(indexOf, indexOf + 3, spannableStringBuilder);
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
                    this.speakingMembersAvatars.setObject(i4, currentAccount, (TLObject) null);
                    i4++;
                }
                this.speakingMembersAvatars.commitTransition(z2);
            } else if (this.updateTooltipRunnbale == null) {
                GroupCallRenderersContainer$$ExternalSyntheticLambda7 groupCallRenderersContainer$$ExternalSyntheticLambda7 = new GroupCallRenderersContainer$$ExternalSyntheticLambda7(this);
                this.updateTooltipRunnbale = groupCallRenderersContainer$$ExternalSyntheticLambda7;
                AndroidUtilities.runOnUIThread(groupCallRenderersContainer$$ExternalSyntheticLambda7, (System.currentTimeMillis() - this.lastUpdateTooltipTime) + 50);
            }
        } else if (this.showSpeakingMembersToast) {
            this.showSpeakingMembersToast = false;
            this.showSpeakingMembersToastProgress = 0.0f;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setVisibleParticipant$9() {
        this.updateTooltipRunnbale = null;
        setVisibleParticipant(true);
    }

    public UndoView getUndoView() {
        if (this.undoView[0].getVisibility() == 0) {
            UndoView[] undoViewArr = this.undoView;
            UndoView undoView2 = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = undoView2;
            undoView2.hide(true, 2);
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

    public void setGroupCall(ChatObject.Call call2) {
        this.call = call2;
    }
}
