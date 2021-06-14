package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.SparseIntArray;
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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarsImageView;
import org.telegram.ui.Components.CrossOutDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.GroupCallFullscreenAdapter;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.GroupCallActivity;
import org.webrtc.TextureViewRenderer;

@SuppressLint({"ViewConstructor"})
public class GroupCallRenderersContainer extends FrameLayout {
    private boolean animateSpeakingOnNextDraw = true;
    int animationIndex;
    private SparseIntArray attachedPeerIds = new SparseIntArray();
    private final ArrayList<GroupCallMiniTextureView> attachedRenderers;
    /* access modifiers changed from: private */
    public final ImageView backButton;
    ChatObject.Call call;
    private boolean canZoomGesture;
    private boolean drawRenderesOnly;
    ValueAnimator fullscreenAnimator;
    private final RecyclerView fullscreenListView;
    public ChatObject.VideoParticipant fullscreenParticipant;
    public int fullscreenPeerId;
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
    private int pointerId1;
    private int pointerId2;
    public float progressToFullscreenMode;
    float progressToHideUi;
    public float progressToScrimView;
    ValueAnimator replaceFullscreenViewAnimator;
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
        this.listView = recyclerView;
        this.fullscreenListView = recyclerView2;
        this.attachedRenderers = arrayList;
        this.call = call2;
        this.groupCallActivity = groupCallActivity2;
        AnonymousClass2 r5 = new ImageView(context2) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(ActionBar.getCurrentActionBarHeight(), NUM));
            }
        };
        this.backButton = r5;
        BackDrawable backDrawable = new BackDrawable(false);
        backDrawable.setColor(-1);
        r5.setImageDrawable(backDrawable);
        r5.setScaleType(ImageView.ScaleType.FIT_CENTER);
        r5.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        r5.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 55)));
        View view = new View(context2);
        this.topShadowView = view;
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0, ColorUtils.setAlphaComponent(-16777216, 114)});
        this.topShadowDrawable = gradientDrawable;
        view.setBackgroundDrawable(gradientDrawable);
        addView(view, LayoutHelper.createFrame(-1, 120.0f));
        addView(r5, LayoutHelper.createFrame(56, -1, 51));
        r5.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupCallRenderersContainer.this.lambda$new$0$GroupCallRenderersContainer(view);
            }
        });
        AnonymousClass3 r52 = new ImageView(context2) {
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
        this.pinButton = r52;
        final Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(20.0f), 0, ColorUtils.setAlphaComponent(-1, 100));
        AnonymousClass4 r12 = new View(context2) {
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
        this.pinContainer = r12;
        r12.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupCallRenderersContainer.this.lambda$new$1$GroupCallRenderersContainer(view);
            }
        });
        createSimpleSelectorRoundRectDrawable.setCallback(this.pinContainer);
        addView(this.pinContainer);
        CrossOutDrawable crossOutDrawable = new CrossOutDrawable(context2, NUM, (String) null);
        this.pinDrawable = crossOutDrawable;
        crossOutDrawable.setOffsets((float) (-AndroidUtilities.dp(1.0f)), (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(1.0f));
        r52.setImageDrawable(this.pinDrawable);
        r52.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        addView(r52, LayoutHelper.createFrame(56, -1, 51));
        TextView textView = new TextView(context2);
        this.pinTextView = textView;
        textView.setTextColor(-1);
        this.pinTextView.setTextSize(1, 15.0f);
        this.pinTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.pinTextView.setText(LocaleController.getString("CallViedeoPin", NUM));
        TextView textView2 = new TextView(context2);
        this.unpinTextView = textView2;
        textView2.setTextColor(-1);
        this.unpinTextView.setTextSize(1, 15.0f);
        this.unpinTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.unpinTextView.setText(LocaleController.getString("CallViedeoUnpin", NUM));
        addView(this.pinTextView, LayoutHelper.createFrame(-2, -2, 51));
        addView(this.unpinTextView, LayoutHelper.createFrame(-2, -2, 51));
        final Drawable createRoundRectDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_listViewBackground"), 204));
        AnonymousClass5 r6 = new FrameLayout(context2) {
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
        this.speakingMembersToast = r6;
        AvatarsImageView avatarsImageView = new AvatarsImageView(context2);
        this.speakingMembersAvatars = avatarsImageView;
        avatarsImageView.setStyle(10);
        r6.setClipChildren(false);
        r6.setClipToPadding(false);
        r6.addView(avatarsImageView, LayoutHelper.createFrame(100, 32.0f, 16, 0.0f, 0.0f, 0.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.speakingMembersText = textView3;
        textView3.setTextSize(1, 14.0f);
        textView3.setTextColor(-1);
        textView3.setLines(1);
        textView3.setEllipsize(TextUtils.TruncateAt.END);
        r6.addView(textView3, LayoutHelper.createFrame(-2, -2, 16));
        addView(r6, LayoutHelper.createFrame(-2, 36.0f, 1, 0.0f, 0.0f, 0.0f, 0.0f));
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
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$GroupCallRenderersContainer(View view) {
        onBackPressed();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$GroupCallRenderersContainer(View view) {
        if (this.inFullscreenMode && this.uiVisible) {
            boolean z = !this.hasPinnedVideo;
            this.hasPinnedVideo = z;
            this.pinDrawable.setCrossOut(z, true);
            requestLayout();
        }
    }

    public void setIsTablet(boolean z) {
        if (this.isTablet != z) {
            this.isTablet = z;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.backButton.getLayoutParams();
            layoutParams.gravity = z ? 85 : 51;
            layoutParams.rightMargin = z ? AndroidUtilities.dp(328.0f) : 0;
            layoutParams.bottomMargin = z ? -AndroidUtilities.dp(8.0f) : 0;
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
    public boolean drawChild(Canvas canvas, View view, long j) {
        UndoView[] undoViewArr = this.undoView;
        if (view == undoViewArr[0] || view == undoViewArr[1]) {
            return true;
        }
        if (view instanceof GroupCallMiniTextureView) {
            GroupCallMiniTextureView groupCallMiniTextureView = (GroupCallMiniTextureView) view;
            if (groupCallMiniTextureView == this.fullscreenTextureView || groupCallMiniTextureView == this.outFullscreenTextureView || this.notDrawRenderes) {
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
                super.drawChild(canvas, view, j);
                canvas.restore();
            }
        }
        if (this.drawRenderesOnly) {
            return true;
        }
        return super.drawChild(canvas, view, j);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x03ad  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x03ba  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x03fe  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x0411 A[LOOP:1: B:134:0x0411->B:141:0x046a, LOOP_START, PHI: r10 
      PHI: (r10v1 int) = (r10v0 int), (r10v2 int) binds: [B:133:0x040f, B:141:0x046a] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARNING: Removed duplicated region for block: B:149:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
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
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.outFullscreenTextureView
            r11 = 1119092736(0x42b40000, float:90.0)
            r12 = 1132396544(0x437var_, float:255.0)
            r13 = 0
            r14 = 1065353216(0x3var_, float:1.0)
            if (r1 != 0) goto L_0x0020
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            if (r1 == 0) goto L_0x0157
        L_0x0020:
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
            if (r4 != 0) goto L_0x0067
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r0.fullscreenTextureView
            if (r5 == 0) goto L_0x0067
            boolean r6 = r5.forceDetached
            if (r6 != 0) goto L_0x0067
            org.telegram.ui.Components.voip.GroupCallGridCell r5 = r5.primaryView
            if (r5 == 0) goto L_0x0067
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
            goto L_0x0074
        L_0x0067:
            if (r4 == 0) goto L_0x0074
            int r1 = r22.getMeasuredWidth()
            int r2 = r22.getMeasuredHeight()
            r8.clipRect(r10, r10, r1, r2)
        L_0x0074:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.outFullscreenTextureView
            if (r1 == 0) goto L_0x0098
            android.view.ViewParent r1 = r1.getParent()
            if (r1 == 0) goto L_0x0098
            r23.save()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.outFullscreenTextureView
            float r1 = r1.getX()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r2 = r0.outFullscreenTextureView
            float r2 = r2.getY()
            r8.translate(r1, r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.outFullscreenTextureView
            r1.draw(r8)
            r23.restore()
        L_0x0098:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            if (r1 == 0) goto L_0x0154
            android.view.ViewParent r1 = r1.getParent()
            if (r1 == 0) goto L_0x0154
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            float r1 = r1.getAlpha()
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 == 0) goto L_0x00e8
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
            goto L_0x00eb
        L_0x00e8:
            r23.save()
        L_0x00eb:
            boolean r1 = r0.swipeToBackGesture
            if (r1 != 0) goto L_0x00f6
            android.animation.ValueAnimator r1 = r0.swipeToBackAnimator
            if (r1 == 0) goto L_0x00f4
            goto L_0x00f6
        L_0x00f4:
            r1 = 0
            goto L_0x00f7
        L_0x00f6:
            r1 = 1
        L_0x00f7:
            if (r1 == 0) goto L_0x010f
            int r2 = r22.getMeasuredWidth()
            int r3 = r22.getMeasuredHeight()
            boolean r4 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r4 == 0) goto L_0x0107
            r4 = 0
            goto L_0x010b
        L_0x0107:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
        L_0x010b:
            int r3 = r3 - r4
            r8.clipRect(r10, r10, r2, r3)
        L_0x010f:
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
            if (r1 != 0) goto L_0x0133
            android.animation.ValueAnimator r1 = r0.zoomBackAnimator
            if (r1 == 0) goto L_0x0130
            goto L_0x0133
        L_0x0130:
            r16 = 0
            goto L_0x0135
        L_0x0133:
            r16 = 1
        L_0x0135:
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
        L_0x0154:
            r23.restore()
        L_0x0157:
            r15 = 0
        L_0x0158:
            r1 = 2
            r2 = 1090519040(0x41000000, float:8.0)
            r16 = 1073741824(0x40000000, float:2.0)
            if (r15 >= r1) goto L_0x023d
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r15]
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0239
            r23.save()
            boolean r1 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r1 == 0) goto L_0x0172
            r1 = 0
            goto L_0x017e
        L_0x0172:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r1 = -r1
            float r1 = (float) r1
            float r3 = r0.progressToHideUi
            float r3 = r14 - r3
            float r1 = r1 * r3
        L_0x017e:
            int r3 = r22.getMeasuredWidth()
            float r3 = (float) r3
            int r4 = r22.getMeasuredHeight()
            boolean r5 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r5 == 0) goto L_0x018d
            r5 = 0
            goto L_0x0191
        L_0x018d:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
        L_0x0191:
            int r4 = r4 - r5
            float r4 = (float) r4
            float r4 = r4 + r1
            r5 = 1099956224(0x41900000, float:18.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r4 = r4 - r5
            r8.clipRect(r13, r13, r3, r4)
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
            if (r4 == 0) goto L_0x01bb
            r4 = 0
            goto L_0x01bf
        L_0x01bb:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
        L_0x01bf:
            float r4 = (float) r4
            float r2 = r2 - r4
            float r2 = r2 + r1
            r1 = 1104150528(0x41d00000, float:26.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r2 = r2 - r1
            r8.translate(r3, r2)
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r15]
            float r1 = r1.getAlpha()
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 == 0) goto L_0x0200
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
            goto L_0x0203
        L_0x0200:
            r23.save()
        L_0x0203:
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
        L_0x0239:
            int r15 = r15 + 1
            goto L_0x0158
        L_0x023d:
            float r1 = r0.progressToFullscreenMode
            float r3 = r0.progressToHideUi
            float r3 = r14 - r3
            float r1 = r1 * r3
            android.animation.ValueAnimator r3 = r0.replaceFullscreenViewAnimator
            if (r3 == 0) goto L_0x0275
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.outFullscreenTextureView
            if (r3 == 0) goto L_0x0275
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            if (r4 == 0) goto L_0x0275
            boolean r3 = r3.hasVideo
            boolean r5 = r4.hasVideo
            if (r3 == r5) goto L_0x0267
            if (r5 != 0) goto L_0x0260
            float r3 = r4.getAlpha()
            float r3 = r14 - r3
            goto L_0x0264
        L_0x0260:
            float r3 = r4.getAlpha()
        L_0x0264:
            float r3 = r3 * r1
            goto L_0x026c
        L_0x0267:
            if (r5 != 0) goto L_0x026b
            r3 = 0
            goto L_0x026c
        L_0x026b:
            r3 = r1
        L_0x026c:
            android.graphics.drawable.Drawable r4 = r0.topShadowDrawable
            float r3 = r3 * r12
            int r3 = (int) r3
            r4.setAlpha(r3)
            goto L_0x0290
        L_0x0275:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.fullscreenTextureView
            if (r3 == 0) goto L_0x0288
            android.graphics.drawable.Drawable r4 = r0.topShadowDrawable
            float r12 = r12 * r1
            float r3 = r3.progressToNoVideoStub
            float r3 = r14 - r3
            float r12 = r12 * r3
            int r3 = (int) r12
            r4.setAlpha(r3)
            goto L_0x0290
        L_0x0288:
            android.graphics.drawable.Drawable r3 = r0.topShadowDrawable
            float r12 = r12 * r1
            int r4 = (int) r12
            r3.setAlpha(r4)
        L_0x0290:
            android.widget.ImageView r3 = r0.backButton
            r3.setAlpha(r1)
            android.widget.ImageView r3 = r0.pinButton
            r3.setAlpha(r1)
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
            if (r3 == 0) goto L_0x02ee
            r3 = 1134821376(0x43a40000, float:328.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
        L_0x02eb:
            float r3 = (float) r3
            float r4 = r4 - r3
            goto L_0x02fb
        L_0x02ee:
            boolean r3 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r3 == 0) goto L_0x02f9
            r3 = 1127481344(0x43340000, float:180.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            goto L_0x02eb
        L_0x02f9:
            r3 = 0
            goto L_0x02eb
        L_0x02fb:
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
            float r1 = r0.speakingMembersToastChangeProgress
            int r3 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r3 == 0) goto L_0x0379
            r3 = 1033171465(0x3d94var_, float:0.07272727)
            float r1 = r1 + r3
            r0.speakingMembersToastChangeProgress = r1
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 <= 0) goto L_0x0371
            r0.speakingMembersToastChangeProgress = r14
            goto L_0x0374
        L_0x0371:
            r22.invalidate()
        L_0x0374:
            android.widget.FrameLayout r1 = r0.speakingMembersToast
            r1.invalidate()
        L_0x0379:
            boolean r1 = r0.showSpeakingMembersToast
            r3 = 1037726734(0x3dda740e, float:0.10666667)
            if (r1 == 0) goto L_0x0394
            float r4 = r0.showSpeakingMembersToastProgress
            int r5 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r5 == 0) goto L_0x0394
            float r4 = r4 + r3
            r0.showSpeakingMembersToastProgress = r4
            int r1 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r1 <= 0) goto L_0x0390
            r0.showSpeakingMembersToastProgress = r14
            goto L_0x03a9
        L_0x0390:
            r22.invalidate()
            goto L_0x03a9
        L_0x0394:
            if (r1 != 0) goto L_0x03a9
            float r1 = r0.showSpeakingMembersToastProgress
            int r4 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r4 == 0) goto L_0x03a9
            float r1 = r1 - r3
            r0.showSpeakingMembersToastProgress = r1
            int r1 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x03a6
            r0.showSpeakingMembersToastProgress = r13
            goto L_0x03a9
        L_0x03a6:
            r22.invalidate()
        L_0x03a9:
            boolean r1 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r1 == 0) goto L_0x03ba
            android.widget.FrameLayout r1 = r0.speakingMembersToast
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r1.setTranslationY(r2)
            goto L_0x03d9
        L_0x03ba:
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
        L_0x03d9:
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
            if (r1 == 0) goto L_0x0406
            r0.notDrawRenderes = r9
            super.dispatchDraw(r23)
            r0.notDrawRenderes = r10
            goto L_0x0409
        L_0x0406:
            super.dispatchDraw(r23)
        L_0x0409:
            androidx.recyclerview.widget.RecyclerView r1 = r0.fullscreenListView
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x046d
        L_0x0411:
            androidx.recyclerview.widget.RecyclerView r1 = r0.fullscreenListView
            int r1 = r1.getChildCount()
            if (r10 >= r1) goto L_0x046d
            androidx.recyclerview.widget.RecyclerView r1 = r0.fullscreenListView
            android.view.View r1 = r1.getChildAt(r10)
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r1 = (org.telegram.ui.Components.GroupCallFullscreenAdapter.GroupCallUserCell) r1
            int r2 = r1.getVisibility()
            if (r2 != 0) goto L_0x046a
            float r2 = r1.getAlpha()
            int r2 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r2 == 0) goto L_0x046a
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
        L_0x046a:
            int r10 = r10 + 1
            goto L_0x0411
        L_0x046d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallRenderersContainer.dispatchDraw(android.graphics.Canvas):void");
    }

    public void requestFullscreen(ChatObject.VideoParticipant videoParticipant) {
        int i;
        final GroupCallMiniTextureView groupCallMiniTextureView;
        GroupCallMiniTextureView groupCallMiniTextureView2;
        ChatObject.VideoParticipant videoParticipant2 = videoParticipant;
        if (videoParticipant2 != null || this.fullscreenParticipant != null) {
            if (videoParticipant2 == null || !videoParticipant2.equals(this.fullscreenParticipant)) {
                boolean z = false;
                if (videoParticipant2 == null) {
                    i = 0;
                } else {
                    i = MessageObject.getPeerId(videoParticipant2.participant.peer);
                }
                GroupCallMiniTextureView groupCallMiniTextureView3 = this.fullscreenTextureView;
                if (groupCallMiniTextureView3 != null) {
                    groupCallMiniTextureView3.runDelayedAnimations();
                }
                ValueAnimator valueAnimator = this.replaceFullscreenViewAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.fullscreenParticipant = videoParticipant2;
                this.fullscreenPeerId = i;
                boolean z2 = this.inFullscreenMode;
                this.lastUpdateTime = System.currentTimeMillis();
                float f = 1.0f;
                if (videoParticipant2 == null) {
                    if (this.inFullscreenMode) {
                        ValueAnimator valueAnimator2 = this.fullscreenAnimator;
                        if (valueAnimator2 != null) {
                            valueAnimator2.cancel();
                        }
                        this.inFullscreenMode = false;
                        GroupCallMiniTextureView groupCallMiniTextureView4 = this.fullscreenTextureView;
                        if (!(groupCallMiniTextureView4.primaryView == null && groupCallMiniTextureView4.secondaryView == null && groupCallMiniTextureView4.tabletGridView == null)) {
                            ChatObject.VideoParticipant videoParticipant3 = groupCallMiniTextureView4.participant;
                            if (GroupCallActivity.videoIsActive(videoParticipant3.participant, videoParticipant3.presentation, this.call)) {
                                this.fullscreenTextureView.setShowingInFullscreen(false, true);
                            }
                        }
                        this.fullscreenTextureView.forceDetach(true);
                        GroupCallGridCell groupCallGridCell = this.fullscreenTextureView.primaryView;
                        if (groupCallGridCell != null) {
                            groupCallGridCell.setRenderer((GroupCallMiniTextureView) null);
                        }
                        GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = this.fullscreenTextureView.secondaryView;
                        if (groupCallUserCell != null) {
                            groupCallUserCell.setRenderer((GroupCallMiniTextureView) null);
                        }
                        GroupCallGridCell groupCallGridCell2 = this.fullscreenTextureView.tabletGridView;
                        if (groupCallGridCell2 != null) {
                            groupCallGridCell2.setRenderer((GroupCallMiniTextureView) null);
                        }
                        final GroupCallMiniTextureView groupCallMiniTextureView5 = this.fullscreenTextureView;
                        groupCallMiniTextureView5.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (groupCallMiniTextureView5.getParent() != null) {
                                    GroupCallRenderersContainer.this.removeView(groupCallMiniTextureView5);
                                    groupCallMiniTextureView5.release();
                                }
                            }
                        }).setDuration(350).start();
                    }
                    this.backButton.setEnabled(false);
                    this.hasPinnedVideo = false;
                } else {
                    int i2 = 0;
                    while (true) {
                        if (i2 >= this.attachedRenderers.size()) {
                            groupCallMiniTextureView = null;
                            break;
                        } else if (this.attachedRenderers.get(i2).participant.equals(videoParticipant2)) {
                            groupCallMiniTextureView = this.attachedRenderers.get(i2);
                            break;
                        } else {
                            i2++;
                        }
                    }
                    if (groupCallMiniTextureView != null) {
                        ValueAnimator valueAnimator3 = this.fullscreenAnimator;
                        if (valueAnimator3 != null) {
                            valueAnimator3.cancel();
                        }
                        if (!this.inFullscreenMode) {
                            this.inFullscreenMode = true;
                            this.fullscreenTextureView = groupCallMiniTextureView;
                            groupCallMiniTextureView.setShowingInFullscreen(true, true);
                            invalidate();
                            this.pinDrawable.setCrossOut(this.hasPinnedVideo, false);
                        } else {
                            this.hasPinnedVideo = false;
                            this.pinDrawable.setCrossOut(false, false);
                            this.fullscreenTextureView.forceDetach(false);
                            groupCallMiniTextureView.forceDetach(false);
                            GroupCallMiniTextureView groupCallMiniTextureView6 = this.fullscreenTextureView;
                            if (groupCallMiniTextureView6.primaryView == null && groupCallMiniTextureView6.secondaryView == null && groupCallMiniTextureView6.tabletGridView == null) {
                                groupCallMiniTextureView2 = null;
                            } else {
                                groupCallMiniTextureView2 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                                GroupCallMiniTextureView groupCallMiniTextureView7 = this.fullscreenTextureView;
                                groupCallMiniTextureView2.setViews(groupCallMiniTextureView7.primaryView, groupCallMiniTextureView7.secondaryView, groupCallMiniTextureView7.tabletGridView);
                                groupCallMiniTextureView2.setFullscreenMode(this.inFullscreenMode, false);
                                groupCallMiniTextureView2.updateAttachState(false);
                                GroupCallGridCell groupCallGridCell3 = this.fullscreenTextureView.primaryView;
                                if (groupCallGridCell3 != null) {
                                    groupCallGridCell3.setRenderer(groupCallMiniTextureView2);
                                }
                                GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell2 = this.fullscreenTextureView.secondaryView;
                                if (groupCallUserCell2 != null) {
                                    groupCallUserCell2.setRenderer(groupCallMiniTextureView2);
                                }
                                GroupCallGridCell groupCallGridCell4 = this.fullscreenTextureView.tabletGridView;
                                if (groupCallGridCell4 != null) {
                                    groupCallGridCell4.setRenderer(groupCallMiniTextureView2);
                                }
                            }
                            final GroupCallMiniTextureView groupCallMiniTextureView8 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                            groupCallMiniTextureView8.participant = groupCallMiniTextureView.participant;
                            groupCallMiniTextureView8.setViews(groupCallMiniTextureView.primaryView, groupCallMiniTextureView.secondaryView, groupCallMiniTextureView.tabletGridView);
                            groupCallMiniTextureView8.setFullscreenMode(this.inFullscreenMode, false);
                            groupCallMiniTextureView8.updateAttachState(false);
                            groupCallMiniTextureView8.showBackground = true;
                            groupCallMiniTextureView8.textureView.renderer.setAlpha(1.0f);
                            groupCallMiniTextureView8.textureView.blurRenderer.setAlpha(1.0f);
                            GroupCallGridCell groupCallGridCell5 = groupCallMiniTextureView.primaryView;
                            if (groupCallGridCell5 != null) {
                                groupCallGridCell5.setRenderer(groupCallMiniTextureView8);
                            }
                            GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell3 = groupCallMiniTextureView.secondaryView;
                            if (groupCallUserCell3 != null) {
                                groupCallUserCell3.setRenderer(groupCallMiniTextureView8);
                            }
                            GroupCallGridCell groupCallGridCell6 = groupCallMiniTextureView.tabletGridView;
                            if (groupCallGridCell6 != null) {
                                groupCallGridCell6.setRenderer(groupCallMiniTextureView8);
                            }
                            groupCallMiniTextureView8.animateEnter = true;
                            groupCallMiniTextureView8.setAlpha(0.0f);
                            this.outFullscreenTextureView = this.fullscreenTextureView;
                            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(groupCallMiniTextureView8, View.ALPHA, new float[]{0.0f, 1.0f});
                            this.replaceFullscreenViewAnimator = ofFloat;
                            ofFloat.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                                    groupCallRenderersContainer.replaceFullscreenViewAnimator = null;
                                    groupCallMiniTextureView8.animateEnter = false;
                                    if (groupCallRenderersContainer.outFullscreenTextureView != null) {
                                        if (GroupCallRenderersContainer.this.outFullscreenTextureView.getParent() != null) {
                                            GroupCallRenderersContainer groupCallRenderersContainer2 = GroupCallRenderersContainer.this;
                                            groupCallRenderersContainer2.removeView(groupCallRenderersContainer2.outFullscreenTextureView);
                                            groupCallMiniTextureView.release();
                                        }
                                        GroupCallMiniTextureView unused = GroupCallRenderersContainer.this.outFullscreenTextureView = null;
                                    }
                                }
                            });
                            if (groupCallMiniTextureView2 != null) {
                                groupCallMiniTextureView2.setAlpha(0.0f);
                                groupCallMiniTextureView2.setScaleX(0.5f);
                                groupCallMiniTextureView2.setScaleY(0.5f);
                                groupCallMiniTextureView2.animateEnter = true;
                            }
                            groupCallMiniTextureView8.runOnFrameRendered(new Runnable(groupCallMiniTextureView, groupCallMiniTextureView2) {
                                public final /* synthetic */ GroupCallMiniTextureView f$1;
                                public final /* synthetic */ GroupCallMiniTextureView f$2;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                }

                                public final void run() {
                                    GroupCallRenderersContainer.this.lambda$requestFullscreen$2$GroupCallRenderersContainer(this.f$1, this.f$2);
                                }
                            });
                            this.fullscreenTextureView = groupCallMiniTextureView8;
                            groupCallMiniTextureView8.setShowingInFullscreen(true, false);
                            update();
                        }
                    } else if (this.inFullscreenMode) {
                        GroupCallMiniTextureView groupCallMiniTextureView9 = this.fullscreenTextureView;
                        if (groupCallMiniTextureView9.primaryView == null) {
                            if (!(groupCallMiniTextureView9.secondaryView != null) && !(groupCallMiniTextureView9.tabletGridView != null)) {
                                groupCallMiniTextureView9.forceDetach(true);
                                final GroupCallMiniTextureView groupCallMiniTextureView10 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                                groupCallMiniTextureView10.participant = videoParticipant2;
                                groupCallMiniTextureView10.setFullscreenMode(this.inFullscreenMode, false);
                                groupCallMiniTextureView10.setShowingInFullscreen(true, false);
                                groupCallMiniTextureView10.animateEnter = true;
                                groupCallMiniTextureView10.setAlpha(0.0f);
                                this.outFullscreenTextureView = this.fullscreenTextureView;
                                ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                                this.replaceFullscreenViewAnimator = ofFloat2;
                                ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(groupCallMiniTextureView10) {
                                    public final /* synthetic */ GroupCallMiniTextureView f$1;

                                    {
                                        this.f$1 = r2;
                                    }

                                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        GroupCallRenderersContainer.this.lambda$requestFullscreen$4$GroupCallRenderersContainer(this.f$1, valueAnimator);
                                    }
                                });
                                this.replaceFullscreenViewAnimator.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                                        groupCallRenderersContainer.replaceFullscreenViewAnimator = null;
                                        groupCallMiniTextureView10.animateEnter = false;
                                        if (groupCallRenderersContainer.outFullscreenTextureView != null) {
                                            if (GroupCallRenderersContainer.this.outFullscreenTextureView.getParent() != null) {
                                                GroupCallRenderersContainer groupCallRenderersContainer2 = GroupCallRenderersContainer.this;
                                                groupCallRenderersContainer2.removeView(groupCallRenderersContainer2.outFullscreenTextureView);
                                                GroupCallRenderersContainer.this.outFullscreenTextureView.release();
                                            }
                                            GroupCallMiniTextureView unused = GroupCallRenderersContainer.this.outFullscreenTextureView = null;
                                        }
                                    }
                                });
                                this.replaceFullscreenViewAnimator.start();
                                this.fullscreenTextureView = groupCallMiniTextureView10;
                                groupCallMiniTextureView10.setShowingInFullscreen(true, false);
                                this.fullscreenTextureView.updateAttachState(false);
                                update();
                            }
                        }
                        groupCallMiniTextureView9.forceDetach(false);
                        GroupCallMiniTextureView groupCallMiniTextureView11 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                        GroupCallMiniTextureView groupCallMiniTextureView12 = this.fullscreenTextureView;
                        groupCallMiniTextureView11.setViews(groupCallMiniTextureView12.primaryView, groupCallMiniTextureView12.secondaryView, groupCallMiniTextureView12.tabletGridView);
                        groupCallMiniTextureView11.setFullscreenMode(this.inFullscreenMode, false);
                        groupCallMiniTextureView11.updateAttachState(false);
                        GroupCallGridCell groupCallGridCell7 = this.fullscreenTextureView.primaryView;
                        if (groupCallGridCell7 != null) {
                            groupCallGridCell7.setRenderer(groupCallMiniTextureView11);
                        }
                        GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell4 = this.fullscreenTextureView.secondaryView;
                        if (groupCallUserCell4 != null) {
                            groupCallUserCell4.setRenderer(groupCallMiniTextureView11);
                        }
                        GroupCallGridCell groupCallGridCell8 = this.fullscreenTextureView.tabletGridView;
                        if (groupCallGridCell8 != null) {
                            groupCallGridCell8.setRenderer(groupCallMiniTextureView11);
                        }
                        groupCallMiniTextureView11.setAlpha(0.0f);
                        groupCallMiniTextureView11.setScaleX(0.5f);
                        groupCallMiniTextureView11.setScaleY(0.5f);
                        groupCallMiniTextureView11.animateEnter = true;
                        groupCallMiniTextureView11.runOnFrameRendered(new Runnable(groupCallMiniTextureView11) {
                            public final /* synthetic */ GroupCallMiniTextureView f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                GroupCallRenderersContainer.this.lambda$requestFullscreen$3$GroupCallRenderersContainer(this.f$1);
                            }
                        });
                        final GroupCallMiniTextureView groupCallMiniTextureView102 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                        groupCallMiniTextureView102.participant = videoParticipant2;
                        groupCallMiniTextureView102.setFullscreenMode(this.inFullscreenMode, false);
                        groupCallMiniTextureView102.setShowingInFullscreen(true, false);
                        groupCallMiniTextureView102.animateEnter = true;
                        groupCallMiniTextureView102.setAlpha(0.0f);
                        this.outFullscreenTextureView = this.fullscreenTextureView;
                        ValueAnimator ofFloat22 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        this.replaceFullscreenViewAnimator = ofFloat22;
                        ofFloat22.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(groupCallMiniTextureView102) {
                            public final /* synthetic */ GroupCallMiniTextureView f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                GroupCallRenderersContainer.this.lambda$requestFullscreen$4$GroupCallRenderersContainer(this.f$1, valueAnimator);
                            }
                        });
                        this.replaceFullscreenViewAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                                groupCallRenderersContainer.replaceFullscreenViewAnimator = null;
                                groupCallMiniTextureView102.animateEnter = false;
                                if (groupCallRenderersContainer.outFullscreenTextureView != null) {
                                    if (GroupCallRenderersContainer.this.outFullscreenTextureView.getParent() != null) {
                                        GroupCallRenderersContainer groupCallRenderersContainer2 = GroupCallRenderersContainer.this;
                                        groupCallRenderersContainer2.removeView(groupCallRenderersContainer2.outFullscreenTextureView);
                                        GroupCallRenderersContainer.this.outFullscreenTextureView.release();
                                    }
                                    GroupCallMiniTextureView unused = GroupCallRenderersContainer.this.outFullscreenTextureView = null;
                                }
                            }
                        });
                        this.replaceFullscreenViewAnimator.start();
                        this.fullscreenTextureView = groupCallMiniTextureView102;
                        groupCallMiniTextureView102.setShowingInFullscreen(true, false);
                        this.fullscreenTextureView.updateAttachState(false);
                        update();
                    } else {
                        this.inFullscreenMode = true;
                        GroupCallMiniTextureView groupCallMiniTextureView13 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                        this.fullscreenTextureView = groupCallMiniTextureView13;
                        groupCallMiniTextureView13.participant = videoParticipant2;
                        groupCallMiniTextureView13.setFullscreenMode(this.inFullscreenMode, false);
                        this.fullscreenTextureView.setShowingInFullscreen(true, false);
                        this.fullscreenTextureView.setShowingInFullscreen(true, false);
                        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.fullscreenTextureView, View.ALPHA, new float[]{0.0f, 1.0f});
                        this.replaceFullscreenViewAnimator = ofFloat3;
                        ofFloat3.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                                groupCallRenderersContainer.replaceFullscreenViewAnimator = null;
                                groupCallRenderersContainer.fullscreenTextureView.animateEnter = false;
                                if (groupCallRenderersContainer.outFullscreenTextureView != null) {
                                    if (GroupCallRenderersContainer.this.outFullscreenTextureView.getParent() != null) {
                                        GroupCallRenderersContainer groupCallRenderersContainer2 = GroupCallRenderersContainer.this;
                                        groupCallRenderersContainer2.removeView(groupCallRenderersContainer2.outFullscreenTextureView);
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
                boolean z3 = this.inFullscreenMode;
                if (z2 != z3) {
                    if (!z3) {
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
                    ofFloat4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            GroupCallRenderersContainer.this.lambda$requestFullscreen$5$GroupCallRenderersContainer(valueAnimator);
                        }
                    });
                    final GroupCallMiniTextureView groupCallMiniTextureView14 = this.fullscreenTextureView;
                    groupCallMiniTextureView14.animateToFullscreen = true;
                    final int currentAccount = this.groupCallActivity.getCurrentAccount();
                    this.swipedBack = this.swipeToBackGesture;
                    this.animationIndex = NotificationCenter.getInstance(currentAccount).setAnimationInProgress(this.animationIndex, (int[]) null);
                    this.fullscreenAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            NotificationCenter.getInstance(currentAccount).onAnimationFinish(GroupCallRenderersContainer.this.animationIndex);
                            GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                            groupCallRenderersContainer.fullscreenAnimator = null;
                            groupCallMiniTextureView14.animateToFullscreen = false;
                            boolean z = groupCallRenderersContainer.inFullscreenMode;
                            if (!z) {
                                groupCallRenderersContainer.fullscreenTextureView = null;
                                groupCallRenderersContainer.fullscreenPeerId = 0;
                            }
                            groupCallRenderersContainer.progressToFullscreenMode = z ? 1.0f : 0.0f;
                            groupCallRenderersContainer.update();
                            GroupCallRenderersContainer.this.onFullScreenModeChanged(false);
                            GroupCallRenderersContainer groupCallRenderersContainer2 = GroupCallRenderersContainer.this;
                            if (!groupCallRenderersContainer2.inFullscreenMode) {
                                groupCallRenderersContainer2.backButton.setVisibility(8);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$requestFullscreen$2 */
    public /* synthetic */ void lambda$requestFullscreen$2$GroupCallRenderersContainer(final GroupCallMiniTextureView groupCallMiniTextureView, final GroupCallMiniTextureView groupCallMiniTextureView2) {
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
        }).setDuration(150).start();
        if (groupCallMiniTextureView2 != null) {
            groupCallMiniTextureView2.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    groupCallMiniTextureView2.animateEnter = false;
                }
            }).start();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$requestFullscreen$3 */
    public /* synthetic */ void lambda$requestFullscreen$3$GroupCallRenderersContainer(final GroupCallMiniTextureView groupCallMiniTextureView) {
        groupCallMiniTextureView.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                groupCallMiniTextureView.animateEnter = false;
            }
        }).setDuration(150).start();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$requestFullscreen$4 */
    public /* synthetic */ void lambda$requestFullscreen$4$GroupCallRenderersContainer(GroupCallMiniTextureView groupCallMiniTextureView, ValueAnimator valueAnimator) {
        groupCallMiniTextureView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
        invalidate();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$requestFullscreen$5 */
    public /* synthetic */ void lambda$requestFullscreen$5$GroupCallRenderersContainer(ValueAnimator valueAnimator) {
        this.progressToFullscreenMode = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        update();
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

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.inFullscreenMode || ((!this.maybeSwipeToBackGesture && !this.swipeToBackGesture && !this.tapGesture && !this.canZoomGesture && !this.isInPinchToZoomTouchMode && !this.zoomStarted && motionEvent.getActionMasked() != 0) || this.fullscreenTextureView == null)) {
            finishZoom();
            return false;
        }
        if (motionEvent.getActionMasked() == 0 && this.swipeToBackAnimator != null) {
            this.maybeSwipeToBackGesture = false;
            this.swipeToBackGesture = true;
            this.tapY = motionEvent.getY() - this.swipeToBackDy;
            this.swipeToBackAnimator.removeAllListeners();
            this.swipeToBackAnimator.cancel();
            this.swipeToBackAnimator = null;
        } else if (this.swipeToBackAnimator != null) {
            finishZoom();
            return false;
        }
        if (motionEvent.getActionMasked() == 0 && !this.swipeToBackGesture) {
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, (float) ActionBar.getCurrentActionBarHeight(), (float) (this.fullscreenTextureView.getMeasuredWidth() + ((!GroupCallActivity.isLandscapeMode || !this.uiVisible) ? 0 : -AndroidUtilities.dp(90.0f))), (float) (this.fullscreenTextureView.getMeasuredHeight() + ((GroupCallActivity.isLandscapeMode || !this.uiVisible) ? 0 : -AndroidUtilities.dp(90.0f))));
            if (rectF.contains(motionEvent.getX(), motionEvent.getY())) {
                this.tapTime = System.currentTimeMillis();
                this.tapGesture = true;
                this.maybeSwipeToBackGesture = true;
                this.tapX = motionEvent.getX();
                this.tapY = motionEvent.getY();
            }
        } else if ((this.maybeSwipeToBackGesture || this.swipeToBackGesture || this.tapGesture) && motionEvent.getActionMasked() == 2) {
            if (Math.abs(this.tapX - motionEvent.getX()) > ((float) this.touchSlop) || Math.abs(this.tapY - motionEvent.getY()) > ((float) this.touchSlop)) {
                this.tapGesture = false;
            }
            if (this.maybeSwipeToBackGesture && Math.abs(this.tapX - motionEvent.getX()) > ((float) this.touchSlop)) {
                this.maybeSwipeToBackGesture = false;
            }
            if (this.maybeSwipeToBackGesture && !this.zoomStarted && Math.abs(this.tapY - motionEvent.getY()) > ((float) (this.touchSlop * 2))) {
                this.tapY = motionEvent.getY();
                this.maybeSwipeToBackGesture = false;
                this.swipeToBackGesture = true;
            } else if (this.swipeToBackGesture) {
                this.swipeToBackDy = motionEvent.getY() - this.tapY;
                invalidate();
            }
        }
        if (this.tapGesture && motionEvent.getActionMasked() == 1 && System.currentTimeMillis() - this.tapTime < 200) {
            setUiVisible(!this.uiVisible);
            this.swipeToBackDy = 0.0f;
            invalidate();
        }
        if (((this.maybeSwipeToBackGesture || this.swipeToBackGesture) && motionEvent.getActionMasked() == 1) || motionEvent.getActionMasked() == 3) {
            this.maybeSwipeToBackGesture = false;
            if (this.swipeToBackGesture) {
                if (motionEvent.getActionMasked() != 1 || Math.abs(this.swipeToBackDy) <= ((float) AndroidUtilities.dp(120.0f))) {
                    animateSwipeToBack(false);
                } else {
                    this.groupCallActivity.fullscreenFor((ChatObject.VideoParticipant) null);
                }
            }
            invalidate();
        }
        if (!this.fullscreenTextureView.hasVideo || this.swipeToBackGesture) {
            finishZoom();
            if (this.tapGesture || this.swipeToBackGesture || this.maybeSwipeToBackGesture) {
                return true;
            }
            return false;
        }
        if (motionEvent.getActionMasked() == 0 || motionEvent.getActionMasked() == 5) {
            if (motionEvent.getActionMasked() == 0) {
                TextureViewRenderer textureViewRenderer = this.fullscreenTextureView.textureView.renderer;
                RectF rectF2 = AndroidUtilities.rectTmp;
                rectF2.set(textureViewRenderer.getX(), textureViewRenderer.getY(), textureViewRenderer.getX() + ((float) textureViewRenderer.getMeasuredWidth()), textureViewRenderer.getY() + ((float) textureViewRenderer.getMeasuredHeight()));
                rectF2.inset(((((float) textureViewRenderer.getMeasuredHeight()) * this.fullscreenTextureView.textureView.scaleTextureToFill) - ((float) textureViewRenderer.getMeasuredHeight())) / 2.0f, ((((float) textureViewRenderer.getMeasuredWidth()) * this.fullscreenTextureView.textureView.scaleTextureToFill) - ((float) textureViewRenderer.getMeasuredWidth())) / 2.0f);
                if (!GroupCallActivity.isLandscapeMode) {
                    rectF2.top = Math.max(rectF2.top, (float) ActionBar.getCurrentActionBarHeight());
                    rectF2.bottom = Math.min(rectF2.bottom, (float) (this.fullscreenTextureView.getMeasuredHeight() - AndroidUtilities.dp(90.0f)));
                } else {
                    rectF2.top = Math.max(rectF2.top, (float) ActionBar.getCurrentActionBarHeight());
                    rectF2.right = Math.min(rectF2.right, (float) (this.fullscreenTextureView.getMeasuredWidth() - AndroidUtilities.dp(90.0f)));
                }
                boolean contains = rectF2.contains(motionEvent.getX(), motionEvent.getY());
                this.canZoomGesture = contains;
                if (!contains) {
                    finishZoom();
                    return this.maybeSwipeToBackGesture;
                }
            }
            if (!this.isInPinchToZoomTouchMode && motionEvent.getPointerCount() == 2) {
                this.pinchStartDistance = (float) Math.hypot((double) (motionEvent.getX(1) - motionEvent.getX(0)), (double) (motionEvent.getY(1) - motionEvent.getY(0)));
                float x = (motionEvent.getX(0) + motionEvent.getX(1)) / 2.0f;
                this.pinchCenterX = x;
                this.pinchStartCenterX = x;
                float y = (motionEvent.getY(0) + motionEvent.getY(1)) / 2.0f;
                this.pinchCenterY = y;
                this.pinchStartCenterY = y;
                this.pinchScale = 1.0f;
                this.pointerId1 = motionEvent.getPointerId(0);
                this.pointerId2 = motionEvent.getPointerId(1);
                this.isInPinchToZoomTouchMode = true;
            }
        } else if (motionEvent.getActionMasked() == 2 && this.isInPinchToZoomTouchMode) {
            int i = -1;
            int i2 = -1;
            for (int i3 = 0; i3 < motionEvent.getPointerCount(); i3++) {
                if (this.pointerId1 == motionEvent.getPointerId(i3)) {
                    i = i3;
                }
                if (this.pointerId2 == motionEvent.getPointerId(i3)) {
                    i2 = i3;
                }
            }
            if (i == -1 || i2 == -1) {
                getParent().requestDisallowInterceptTouchEvent(false);
                finishZoom();
                return this.maybeSwipeToBackGesture;
            }
            float hypot = ((float) Math.hypot((double) (motionEvent.getX(i2) - motionEvent.getX(i)), (double) (motionEvent.getY(i2) - motionEvent.getY(i)))) / this.pinchStartDistance;
            this.pinchScale = hypot;
            if (hypot > 1.005f && !this.zoomStarted) {
                this.pinchStartDistance = (float) Math.hypot((double) (motionEvent.getX(i2) - motionEvent.getX(i)), (double) (motionEvent.getY(i2) - motionEvent.getY(i)));
                float x2 = (motionEvent.getX(i) + motionEvent.getX(i2)) / 2.0f;
                this.pinchCenterX = x2;
                this.pinchStartCenterX = x2;
                float y2 = (motionEvent.getY(i) + motionEvent.getY(i2)) / 2.0f;
                this.pinchCenterY = y2;
                this.pinchStartCenterY = y2;
                this.pinchScale = 1.0f;
                this.pinchTranslationX = 0.0f;
                this.pinchTranslationY = 0.0f;
                getParent().requestDisallowInterceptTouchEvent(true);
                this.zoomStarted = true;
                this.isInPinchToZoomTouchMode = true;
            }
            float x3 = this.pinchStartCenterX - ((motionEvent.getX(i) + motionEvent.getX(i2)) / 2.0f);
            float y3 = this.pinchStartCenterY - ((motionEvent.getY(i) + motionEvent.getY(i2)) / 2.0f);
            float f = this.pinchScale;
            this.pinchTranslationX = (-x3) / f;
            this.pinchTranslationY = (-y3) / f;
            invalidate();
        } else if (motionEvent.getActionMasked() == 1 || ((motionEvent.getActionMasked() == 6 && checkPointerIds(motionEvent)) || motionEvent.getActionMasked() == 3)) {
            getParent().requestDisallowInterceptTouchEvent(false);
            finishZoom();
        }
        if (this.canZoomGesture || this.tapGesture || this.maybeSwipeToBackGesture) {
            return true;
        }
        return false;
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
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    GroupCallRenderersContainer.this.lambda$animateSwipeToBack$6$GroupCallRenderersContainer(valueAnimator);
                }
            });
            this.swipeToBackAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                    groupCallRenderersContainer.swipeToBackAnimator = null;
                    groupCallRenderersContainer.swipeToBackDy = 0.0f;
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
    /* renamed from: lambda$animateSwipeToBack$6 */
    public /* synthetic */ void lambda$animateSwipeToBack$6$GroupCallRenderersContainer(ValueAnimator valueAnimator) {
        this.swipeToBackDy = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    private void finishZoom() {
        if (this.zoomStarted) {
            this.zoomStarted = false;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
            this.zoomBackAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(this.pinchScale, this.pinchTranslationX, this.pinchTranslationY) {
                public final /* synthetic */ float f$1;
                public final /* synthetic */ float f$2;
                public final /* synthetic */ float f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    GroupCallRenderersContainer.this.lambda$finishZoom$7$GroupCallRenderersContainer(this.f$1, this.f$2, this.f$3, valueAnimator);
                }
            });
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
    /* renamed from: lambda$finishZoom$7 */
    public /* synthetic */ void lambda$finishZoom$7$GroupCallRenderersContainer(float f, float f2, float f3, ValueAnimator valueAnimator) {
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
            AndroidUtilities.runOnUIThread(this.hideUiRunnable, 3000);
        }
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
        setVisibleParticipant(true);
    }

    public boolean isAnimating() {
        return this.fullscreenAnimator != null;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (GroupCallActivity.isTabletMode) {
            ((ViewGroup.MarginLayoutParams) this.topShadowView.getLayoutParams()).rightMargin = AndroidUtilities.dp(328.0f);
        } else if (GroupCallActivity.isLandscapeMode) {
            ((ViewGroup.MarginLayoutParams) this.topShadowView.getLayoutParams()).rightMargin = AndroidUtilities.dp(90.0f);
        } else {
            ((ViewGroup.MarginLayoutParams) this.topShadowView.getLayoutParams()).rightMargin = 0;
        }
        this.pinContainer.getLayoutParams().height = AndroidUtilities.dp(40.0f);
        this.pinTextView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 0), i2);
        this.unpinTextView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 0), i2);
        this.pinContainer.getLayoutParams().width = AndroidUtilities.dp(46.0f) + (!this.hasPinnedVideo ? this.pinTextView : this.unpinTextView).getMeasuredWidth();
        ((ViewGroup.MarginLayoutParams) this.speakingMembersToast.getLayoutParams()).rightMargin = GroupCallActivity.isLandscapeMode ? AndroidUtilities.dp(45.0f) : 0;
        for (int i3 = 0; i3 < 2; i3++) {
            ((ViewGroup.MarginLayoutParams) this.undoView[i3].getLayoutParams()).rightMargin = GroupCallActivity.isLandscapeMode ? AndroidUtilities.dp(180.0f) : 0;
        }
        super.onMeasure(i, i2);
    }

    public boolean autoPinEnabled() {
        return !this.hasPinnedVideo && System.currentTimeMillis() - this.lastUpdateTime > 2000 && !this.swipeToBackGesture && !this.isInPinchToZoomTouchMode;
    }

    public void setVisibleParticipant(boolean z) {
        boolean z2;
        int i;
        if (this.inFullscreenMode && !this.isTablet && this.fullscreenParticipant != null && this.fullscreenAnimator == null) {
            int currentAccount = this.groupCallActivity.getCurrentAccount();
            if (System.currentTimeMillis() - this.lastUpdateTooltipTime >= 500) {
                this.lastUpdateTooltipTime = System.currentTimeMillis();
                ArrayList<TLRPC$TL_groupCallParticipant> arrayList = this.call.sortedParticipants;
                SpannableStringBuilder spannableStringBuilder = null;
                int i2 = 0;
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = arrayList.get(i3);
                    if (!tLRPC$TL_groupCallParticipant.self && !tLRPC$TL_groupCallParticipant.muted_by_you && MessageObject.getPeerId(this.fullscreenParticipant.participant.peer) != MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer)) {
                        int peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
                        if (!(SystemClock.uptimeMillis() - tLRPC$TL_groupCallParticipant.lastSpeakTime < 500)) {
                            continue;
                        } else {
                            if (spannableStringBuilder == null) {
                                spannableStringBuilder = new SpannableStringBuilder();
                            }
                            if (i2 < 3) {
                                TLRPC$User user = peerId > 0 ? MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(peerId)) : null;
                                TLRPC$Chat chat = peerId <= 0 ? MessagesController.getInstance(currentAccount).getChat(Integer.valueOf(peerId)) : null;
                                if (user != null || chat != null) {
                                    this.speakingMembersAvatars.setObject(i2, currentAccount, tLRPC$TL_groupCallParticipant);
                                    if (i2 != 0) {
                                        spannableStringBuilder.append(", ");
                                    }
                                    if (user != null) {
                                        if (Build.VERSION.SDK_INT >= 21) {
                                            spannableStringBuilder.append(user.first_name, new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0);
                                        } else {
                                            spannableStringBuilder.append(user.first_name);
                                        }
                                    } else if (Build.VERSION.SDK_INT >= 21) {
                                        spannableStringBuilder.append(chat.title, new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0);
                                    } else {
                                        spannableStringBuilder.append(chat.title);
                                    }
                                }
                            }
                            i2++;
                            if (i2 == 3) {
                                break;
                            }
                        }
                    }
                }
                boolean z3 = i2 != 0;
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
                String pluralString = LocaleController.getPluralString("MembersAreSpeakingToast", i2);
                int indexOf = pluralString.indexOf("un1");
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(pluralString);
                spannableStringBuilder2.replace(indexOf, indexOf + 3, spannableStringBuilder);
                this.speakingMembersText.setText(spannableStringBuilder2);
                if (i2 == 0) {
                    i = 0;
                } else if (i2 == 1) {
                    i = AndroidUtilities.dp(40.0f);
                } else if (i2 == 2) {
                    i = AndroidUtilities.dp(64.0f);
                } else {
                    i = AndroidUtilities.dp(88.0f);
                }
                ((FrameLayout.LayoutParams) this.speakingMembersText.getLayoutParams()).leftMargin = i;
                ((FrameLayout.LayoutParams) this.speakingMembersText.getLayoutParams()).rightMargin = AndroidUtilities.dp(16.0f);
                this.showSpeakingMembersToast = z3;
                invalidate();
                while (i2 < 3) {
                    this.speakingMembersAvatars.setObject(i2, currentAccount, (TLObject) null);
                    i2++;
                }
                this.speakingMembersAvatars.commitTransition(z2);
            } else if (this.updateTooltipRunnbale == null) {
                $$Lambda$GroupCallRenderersContainer$_Y0FYNsyJ9FACJZ4bH6rsU3t4H8 r1 = new Runnable() {
                    public final void run() {
                        GroupCallRenderersContainer.this.lambda$setVisibleParticipant$8$GroupCallRenderersContainer();
                    }
                };
                this.updateTooltipRunnbale = r1;
                AndroidUtilities.runOnUIThread(r1, (System.currentTimeMillis() - this.lastUpdateTooltipTime) + 50);
            }
        } else if (this.showSpeakingMembersToast) {
            this.showSpeakingMembersToast = false;
            this.showSpeakingMembersToastProgress = 0.0f;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setVisibleParticipant$8 */
    public /* synthetic */ void lambda$setVisibleParticipant$8$GroupCallRenderersContainer() {
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
        int peerId = MessageObject.getPeerId(groupCallMiniTextureView.participant.participant.peer);
        SparseIntArray sparseIntArray = this.attachedPeerIds;
        sparseIntArray.put(peerId, sparseIntArray.get(peerId, 0) + 1);
    }

    public void detach(GroupCallMiniTextureView groupCallMiniTextureView) {
        this.attachedRenderers.remove(groupCallMiniTextureView);
        int peerId = MessageObject.getPeerId(groupCallMiniTextureView.participant.participant.peer);
        SparseIntArray sparseIntArray = this.attachedPeerIds;
        sparseIntArray.put(peerId, sparseIntArray.get(peerId, 0) - 1);
    }
}
