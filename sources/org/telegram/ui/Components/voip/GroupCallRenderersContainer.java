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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
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
    private final ArrayList<GroupCallMiniTextureView> attachedRenderers;
    /* access modifiers changed from: private */
    public final ImageView backButton;
    ChatObject.Call call;
    private boolean canZoomGesture;
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
    private boolean isInPinchToZoomTouchMode;
    public long lastUpdateTime;
    long lastUpdateTooltipTime;
    private final RecyclerView listView;
    public int listWidth;
    boolean maybeSwipeToBackGesture;
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
            if (Build.VERSION.SDK_INT >= 21) {
                this.undoView[i].setTranslationZ((float) AndroidUtilities.dp(5.0f));
            }
            addView(this.undoView[i], LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        }
        this.pinContainer.setVisibility(8);
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

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        UndoView[] undoViewArr = this.undoView;
        if (view == undoViewArr[0] || view == undoViewArr[1]) {
            return true;
        }
        if (!(view instanceof GroupCallMiniTextureView)) {
            return super.drawChild(canvas, view, j);
        }
        GroupCallMiniTextureView groupCallMiniTextureView = (GroupCallMiniTextureView) view;
        if (groupCallMiniTextureView == this.fullscreenTextureView || groupCallMiniTextureView == this.outFullscreenTextureView) {
            return true;
        }
        if (groupCallMiniTextureView.primaryView == null) {
            return super.drawChild(canvas, view, j);
        }
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
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0373  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0380  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x03cd A[LOOP:1: B:117:0x03cd->B:124:0x0424, LOOP_START, PHI: r12 
      PHI: (r12v1 int) = (r12v0 int), (r12v2 int) binds: [B:116:0x03cb, B:124:0x0424] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARNING: Removed duplicated region for block: B:132:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dispatchDraw(android.graphics.Canvas r22) {
        /*
            r21 = this;
            r0 = r21
            r8 = r22
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.outFullscreenTextureView
            r9 = 1119092736(0x42b40000, float:90.0)
            r10 = 1132396544(0x437var_, float:255.0)
            r11 = 0
            r12 = 0
            r13 = 1065353216(0x3var_, float:1.0)
            if (r1 != 0) goto L_0x0014
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            if (r1 == 0) goto L_0x0134
        L_0x0014:
            androidx.recyclerview.widget.RecyclerView r1 = r0.listView
            float r1 = r1.getY()
            int r2 = r21.getTop()
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
            r22.save()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            if (r4 == 0) goto L_0x0052
            boolean r4 = r4.forceDetached
            if (r4 != 0) goto L_0x0052
            float r4 = r13 - r3
            float r1 = r1 * r4
            int r5 = r21.getMeasuredWidth()
            float r5 = (float) r5
            float r2 = r2 * r4
            int r4 = r21.getMeasuredHeight()
            float r4 = (float) r4
            float r4 = r4 * r3
            float r2 = r2 + r4
            r8.clipRect(r11, r1, r5, r2)
        L_0x0052:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.outFullscreenTextureView
            if (r1 == 0) goto L_0x0076
            android.view.ViewParent r1 = r1.getParent()
            if (r1 == 0) goto L_0x0076
            r22.save()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.outFullscreenTextureView
            float r1 = r1.getX()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r2 = r0.outFullscreenTextureView
            float r2 = r2.getY()
            r8.translate(r1, r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.outFullscreenTextureView
            r1.draw(r8)
            r22.restore()
        L_0x0076:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            if (r1 == 0) goto L_0x0131
            android.view.ViewParent r1 = r1.getParent()
            if (r1 == 0) goto L_0x0131
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            float r1 = r1.getAlpha()
            int r1 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r1 == 0) goto L_0x00c6
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
            float r2 = r2 * r10
            int r2 = (int) r2
            r3 = 31
            r8.saveLayerAlpha(r1, r2, r3)
            goto L_0x00c9
        L_0x00c6:
            r22.save()
        L_0x00c9:
            boolean r1 = r0.swipeToBackGesture
            r2 = 1
            if (r1 != 0) goto L_0x00d5
            android.animation.ValueAnimator r1 = r0.swipeToBackAnimator
            if (r1 == 0) goto L_0x00d3
            goto L_0x00d5
        L_0x00d3:
            r1 = 0
            goto L_0x00d6
        L_0x00d5:
            r1 = 1
        L_0x00d6:
            if (r1 == 0) goto L_0x00ee
            int r3 = r21.getMeasuredWidth()
            int r4 = r21.getMeasuredHeight()
            boolean r5 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r5 == 0) goto L_0x00e6
            r5 = 0
            goto L_0x00ea
        L_0x00e6:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r9)
        L_0x00ea:
            int r4 = r4 - r5
            r8.clipRect(r12, r12, r3, r4)
        L_0x00ee:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.fullscreenTextureView
            float r3 = r3.getX()
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            float r4 = r4.getY()
            r8.translate(r3, r4)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.fullscreenTextureView
            float r4 = r0.swipeToBackDy
            r3.setSwipeToBack(r1, r4)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r14 = r0.fullscreenTextureView
            boolean r1 = r0.zoomStarted
            if (r1 != 0) goto L_0x0111
            android.animation.ValueAnimator r1 = r0.zoomBackAnimator
            if (r1 == 0) goto L_0x010f
            goto L_0x0111
        L_0x010f:
            r15 = 0
            goto L_0x0112
        L_0x0111:
            r15 = 1
        L_0x0112:
            float r1 = r0.pinchScale
            float r2 = r0.pinchCenterX
            float r3 = r0.pinchCenterY
            float r4 = r0.pinchTranslationX
            float r5 = r0.pinchTranslationY
            r16 = r1
            r17 = r2
            r18 = r3
            r19 = r4
            r20 = r5
            r14.setZoom(r15, r16, r17, r18, r19, r20)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r1.draw(r8)
            r22.restore()
        L_0x0131:
            r22.restore()
        L_0x0134:
            r14 = 0
        L_0x0135:
            r1 = 2
            r15 = 1073741824(0x40000000, float:2.0)
            if (r14 >= r1) goto L_0x0210
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r14]
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x020c
            r22.save()
            boolean r1 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r1 == 0) goto L_0x014d
            r1 = 0
            goto L_0x0159
        L_0x014d:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r1 = -r1
            float r1 = (float) r1
            float r2 = r0.progressToHideUi
            float r2 = r13 - r2
            float r1 = r1 * r2
        L_0x0159:
            int r2 = r21.getMeasuredWidth()
            float r2 = (float) r2
            int r3 = r21.getMeasuredHeight()
            boolean r4 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r4 == 0) goto L_0x0168
            r4 = 0
            goto L_0x016c
        L_0x0168:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r9)
        L_0x016c:
            int r3 = r3 - r4
            float r3 = (float) r3
            float r3 = r3 + r1
            r4 = 1099956224(0x41900000, float:18.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r3 = r3 - r4
            r8.clipRect(r11, r11, r2, r3)
            org.telegram.ui.Components.UndoView[] r2 = r0.undoView
            r2 = r2[r14]
            float r2 = r2.getX()
            org.telegram.ui.Components.UndoView[] r3 = r0.undoView
            r3 = r3[r14]
            float r3 = r3.getY()
            boolean r4 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r4 == 0) goto L_0x0190
            r4 = 0
            goto L_0x0194
        L_0x0190:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r9)
        L_0x0194:
            float r4 = (float) r4
            float r3 = r3 - r4
            float r3 = r3 + r1
            r1 = 1104150528(0x41d00000, float:26.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r3 = r3 - r1
            r8.translate(r2, r3)
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r14]
            float r1 = r1.getAlpha()
            int r1 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r1 == 0) goto L_0x01d5
            r2 = 0
            r3 = 0
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r14]
            int r1 = r1.getMeasuredWidth()
            float r4 = (float) r1
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r14]
            int r1 = r1.getMeasuredHeight()
            float r5 = (float) r1
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r14]
            float r1 = r1.getAlpha()
            float r1 = r1 * r10
            int r6 = (int) r1
            r7 = 31
            r1 = r22
            r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
            goto L_0x01d8
        L_0x01d5:
            r22.save()
        L_0x01d8:
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r14]
            float r1 = r1.getScaleX()
            org.telegram.ui.Components.UndoView[] r2 = r0.undoView
            r2 = r2[r14]
            float r2 = r2.getScaleY()
            org.telegram.ui.Components.UndoView[] r3 = r0.undoView
            r3 = r3[r14]
            int r3 = r3.getMeasuredWidth()
            float r3 = (float) r3
            float r3 = r3 / r15
            org.telegram.ui.Components.UndoView[] r4 = r0.undoView
            r4 = r4[r14]
            int r4 = r4.getMeasuredHeight()
            float r4 = (float) r4
            float r4 = r4 / r15
            r8.scale(r1, r2, r3, r4)
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r14]
            r1.draw(r8)
            r22.restore()
            r22.restore()
        L_0x020c:
            int r14 = r14 + 1
            goto L_0x0135
        L_0x0210:
            float r1 = r0.progressToFullscreenMode
            float r2 = r0.progressToHideUi
            float r2 = r13 - r2
            float r1 = r1 * r2
            android.animation.ValueAnimator r2 = r0.replaceFullscreenViewAnimator
            if (r2 == 0) goto L_0x0248
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r2 = r0.outFullscreenTextureView
            if (r2 == 0) goto L_0x0248
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.fullscreenTextureView
            if (r3 == 0) goto L_0x0248
            boolean r2 = r2.hasVideo
            boolean r4 = r3.hasVideo
            if (r2 == r4) goto L_0x023a
            if (r4 != 0) goto L_0x0233
            float r2 = r3.getAlpha()
            float r2 = r13 - r2
            goto L_0x0237
        L_0x0233:
            float r2 = r3.getAlpha()
        L_0x0237:
            float r2 = r2 * r1
            goto L_0x023f
        L_0x023a:
            if (r4 != 0) goto L_0x023e
            r2 = 0
            goto L_0x023f
        L_0x023e:
            r2 = r1
        L_0x023f:
            android.graphics.drawable.Drawable r3 = r0.topShadowDrawable
            float r2 = r2 * r10
            int r2 = (int) r2
            r3.setAlpha(r2)
            goto L_0x0263
        L_0x0248:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r2 = r0.fullscreenTextureView
            if (r2 == 0) goto L_0x025b
            android.graphics.drawable.Drawable r3 = r0.topShadowDrawable
            float r10 = r10 * r1
            float r2 = r2.progressToNoVideoStub
            float r2 = r13 - r2
            float r10 = r10 * r2
            int r2 = (int) r10
            r3.setAlpha(r2)
            goto L_0x0263
        L_0x025b:
            android.graphics.drawable.Drawable r2 = r0.topShadowDrawable
            float r10 = r10 * r1
            int r3 = (int) r10
            r2.setAlpha(r3)
        L_0x0263:
            android.widget.ImageView r2 = r0.backButton
            r2.setAlpha(r1)
            android.widget.ImageView r2 = r0.pinButton
            r2.setAlpha(r1)
            int r2 = r21.getMeasuredWidth()
            android.widget.TextView r3 = r0.pinTextView
            int r3 = r3.getMeasuredWidth()
            int r2 = r2 - r3
            float r2 = (float) r2
            int r3 = r21.getMeasuredWidth()
            android.widget.TextView r4 = r0.unpinTextView
            int r4 = r4.getMeasuredWidth()
            int r3 = r3 - r4
            float r3 = (float) r3
            int r4 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            android.widget.TextView r5 = r0.pinTextView
            int r5 = r5.getMeasuredHeight()
            int r4 = r4 - r5
            float r4 = (float) r4
            float r4 = r4 / r15
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r5 = (float) r5
            float r4 = r4 - r5
            org.telegram.ui.Components.CrossOutDrawable r5 = r0.pinDrawable
            float r5 = r5.getProgress()
            float r3 = r3 * r5
            org.telegram.ui.Components.CrossOutDrawable r5 = r0.pinDrawable
            float r5 = r5.getProgress()
            float r5 = r13 - r5
            float r2 = r2 * r5
            float r3 = r3 + r2
            r2 = 1101529088(0x41a80000, float:21.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r3 = r3 - r2
            boolean r2 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r2 == 0) goto L_0x02bf
            r2 = 1127481344(0x43340000, float:180.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = -r2
            goto L_0x02c0
        L_0x02bf:
            r2 = 0
        L_0x02c0:
            float r2 = (float) r2
            float r3 = r3 + r2
            android.widget.TextView r2 = r0.pinTextView
            r2.setTranslationX(r3)
            android.widget.TextView r2 = r0.unpinTextView
            r2.setTranslationX(r3)
            android.widget.TextView r2 = r0.pinTextView
            r2.setTranslationY(r4)
            android.widget.TextView r2 = r0.unpinTextView
            r2.setTranslationY(r4)
            android.view.View r2 = r0.pinContainer
            r4 = 1108344832(0x42100000, float:36.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r4 = r3 - r4
            r2.setTranslationX(r4)
            android.view.View r2 = r0.pinContainer
            int r4 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            android.view.View r5 = r0.pinContainer
            int r5 = r5.getMeasuredHeight()
            int r4 = r4 - r5
            float r4 = (float) r4
            float r4 = r4 / r15
            r2.setTranslationY(r4)
            android.widget.ImageView r2 = r0.pinButton
            r4 = 1110441984(0x42300000, float:44.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r3 = r3 - r4
            r2.setTranslationX(r3)
            android.widget.TextView r2 = r0.pinTextView
            org.telegram.ui.Components.CrossOutDrawable r3 = r0.pinDrawable
            float r3 = r3.getProgress()
            float r3 = r13 - r3
            float r3 = r3 * r1
            r2.setAlpha(r3)
            android.widget.TextView r2 = r0.unpinTextView
            org.telegram.ui.Components.CrossOutDrawable r3 = r0.pinDrawable
            float r3 = r3.getProgress()
            float r3 = r3 * r1
            r2.setAlpha(r3)
            android.view.View r2 = r0.pinContainer
            r2.setAlpha(r1)
            float r1 = r0.speakingMembersToastChangeProgress
            int r2 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r2 == 0) goto L_0x033f
            r2 = 1033171465(0x3d94var_, float:0.07272727)
            float r1 = r1 + r2
            r0.speakingMembersToastChangeProgress = r1
            int r1 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r1 <= 0) goto L_0x0337
            r0.speakingMembersToastChangeProgress = r13
            goto L_0x033a
        L_0x0337:
            r21.invalidate()
        L_0x033a:
            android.widget.FrameLayout r1 = r0.speakingMembersToast
            r1.invalidate()
        L_0x033f:
            boolean r1 = r0.showSpeakingMembersToast
            r2 = 1037726734(0x3dda740e, float:0.10666667)
            if (r1 == 0) goto L_0x035a
            float r3 = r0.showSpeakingMembersToastProgress
            int r4 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r4 == 0) goto L_0x035a
            float r3 = r3 + r2
            r0.showSpeakingMembersToastProgress = r3
            int r1 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r1 <= 0) goto L_0x0356
            r0.showSpeakingMembersToastProgress = r13
            goto L_0x036f
        L_0x0356:
            r21.invalidate()
            goto L_0x036f
        L_0x035a:
            if (r1 != 0) goto L_0x036f
            float r1 = r0.showSpeakingMembersToastProgress
            int r3 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r3 == 0) goto L_0x036f
            float r1 = r1 - r2
            r0.showSpeakingMembersToastProgress = r1
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 >= 0) goto L_0x036c
            r0.showSpeakingMembersToastProgress = r11
            goto L_0x036f
        L_0x036c:
            r21.invalidate()
        L_0x036f:
            boolean r1 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r1 == 0) goto L_0x0380
            android.widget.FrameLayout r1 = r0.speakingMembersToast
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r1.setTranslationY(r2)
            goto L_0x03a1
        L_0x0380:
            android.widget.FrameLayout r1 = r0.speakingMembersToast
            int r2 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            float r2 = (float) r2
            float r3 = r0.progressToHideUi
            float r13 = r13 - r3
            float r2 = r2 * r13
            r3 = 1090519040(0x41000000, float:8.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r4 = (float) r4
            float r2 = r2 + r4
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r4 = r0.progressToHideUi
            float r3 = r3 * r4
            float r2 = r2 + r3
            r1.setTranslationY(r2)
        L_0x03a1:
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
            super.dispatchDraw(r22)
            androidx.recyclerview.widget.RecyclerView r1 = r0.fullscreenListView
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0427
        L_0x03cd:
            androidx.recyclerview.widget.RecyclerView r1 = r0.fullscreenListView
            int r1 = r1.getChildCount()
            if (r12 >= r1) goto L_0x0427
            androidx.recyclerview.widget.RecyclerView r1 = r0.fullscreenListView
            android.view.View r1 = r1.getChildAt(r12)
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r1 = (org.telegram.ui.Components.GroupCallFullscreenAdapter.GroupCallUserCell) r1
            int r2 = r1.getVisibility()
            if (r2 != 0) goto L_0x0424
            float r2 = r1.getAlpha()
            int r2 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r2 == 0) goto L_0x0424
            r22.save()
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
            float r4 = r4 / r15
            int r5 = r1.getMeasuredHeight()
            float r5 = (float) r5
            float r5 = r5 / r15
            r8.scale(r2, r3, r4, r5)
            r1.drawOverlays(r8)
            r22.restore()
        L_0x0424:
            int r12 = r12 + 1
            goto L_0x03cd
        L_0x0427:
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
                        if (!(groupCallMiniTextureView4.primaryView == null && groupCallMiniTextureView4.secondaryView == null)) {
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
                            if (groupCallMiniTextureView6.primaryView == null && groupCallMiniTextureView6.secondaryView == null) {
                                groupCallMiniTextureView2 = null;
                            } else {
                                groupCallMiniTextureView2 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                                GroupCallMiniTextureView groupCallMiniTextureView7 = this.fullscreenTextureView;
                                groupCallMiniTextureView2.setViews(groupCallMiniTextureView7.primaryView, groupCallMiniTextureView7.secondaryView);
                                groupCallMiniTextureView2.setFullscreenMode(this.inFullscreenMode, false);
                                groupCallMiniTextureView2.updateAttachState(false);
                                GroupCallGridCell groupCallGridCell2 = this.fullscreenTextureView.primaryView;
                                if (groupCallGridCell2 != null) {
                                    groupCallGridCell2.setRenderer(groupCallMiniTextureView2);
                                }
                                GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell2 = this.fullscreenTextureView.secondaryView;
                                if (groupCallUserCell2 != null) {
                                    groupCallUserCell2.setRenderer(groupCallMiniTextureView2);
                                }
                            }
                            final GroupCallMiniTextureView groupCallMiniTextureView8 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                            groupCallMiniTextureView8.participant = groupCallMiniTextureView.participant;
                            groupCallMiniTextureView8.setViews(groupCallMiniTextureView.primaryView, groupCallMiniTextureView.secondaryView);
                            groupCallMiniTextureView8.setFullscreenMode(this.inFullscreenMode, false);
                            groupCallMiniTextureView8.updateAttachState(false);
                            groupCallMiniTextureView8.showBackground = true;
                            groupCallMiniTextureView8.textureView.renderer.setAlpha(1.0f);
                            groupCallMiniTextureView8.textureView.blurRenderer.setAlpha(1.0f);
                            GroupCallGridCell groupCallGridCell3 = groupCallMiniTextureView.primaryView;
                            if (groupCallGridCell3 != null) {
                                groupCallGridCell3.setRenderer(groupCallMiniTextureView8);
                            }
                            GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell3 = groupCallMiniTextureView.secondaryView;
                            if (groupCallUserCell3 != null) {
                                groupCallUserCell3.setRenderer(groupCallMiniTextureView8);
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
                        if (groupCallMiniTextureView9.primaryView == null && groupCallMiniTextureView9.secondaryView == null) {
                            groupCallMiniTextureView9.forceDetach(true);
                        } else {
                            groupCallMiniTextureView9.forceDetach(false);
                            GroupCallMiniTextureView groupCallMiniTextureView10 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                            GroupCallMiniTextureView groupCallMiniTextureView11 = this.fullscreenTextureView;
                            groupCallMiniTextureView10.setViews(groupCallMiniTextureView11.primaryView, groupCallMiniTextureView11.secondaryView);
                            groupCallMiniTextureView10.setFullscreenMode(this.inFullscreenMode, false);
                            groupCallMiniTextureView10.updateAttachState(false);
                            GroupCallGridCell groupCallGridCell4 = this.fullscreenTextureView.primaryView;
                            if (groupCallGridCell4 != null) {
                                groupCallGridCell4.setRenderer(groupCallMiniTextureView10);
                            }
                            GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell4 = this.fullscreenTextureView.secondaryView;
                            if (groupCallUserCell4 != null) {
                                groupCallUserCell4.setRenderer(groupCallMiniTextureView10);
                            }
                            groupCallMiniTextureView10.setAlpha(0.0f);
                            groupCallMiniTextureView10.setScaleX(0.5f);
                            groupCallMiniTextureView10.setScaleY(0.5f);
                            groupCallMiniTextureView10.animateEnter = true;
                            groupCallMiniTextureView10.runOnFrameRendered(new Runnable(groupCallMiniTextureView10) {
                                public final /* synthetic */ GroupCallMiniTextureView f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    GroupCallRenderersContainer.this.lambda$requestFullscreen$3$GroupCallRenderersContainer(this.f$1);
                                }
                            });
                        }
                        final GroupCallMiniTextureView groupCallMiniTextureView12 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                        groupCallMiniTextureView12.participant = videoParticipant2;
                        groupCallMiniTextureView12.setFullscreenMode(this.inFullscreenMode, false);
                        groupCallMiniTextureView12.setShowingInFullscreen(true, false);
                        groupCallMiniTextureView12.animateEnter = true;
                        groupCallMiniTextureView12.setAlpha(0.0f);
                        this.outFullscreenTextureView = this.fullscreenTextureView;
                        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        this.replaceFullscreenViewAnimator = ofFloat2;
                        ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(groupCallMiniTextureView12) {
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
                                groupCallMiniTextureView12.animateEnter = false;
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
                        this.fullscreenTextureView = groupCallMiniTextureView12;
                        groupCallMiniTextureView12.setShowingInFullscreen(true, false);
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
        ((ViewGroup.MarginLayoutParams) this.topShadowView.getLayoutParams()).rightMargin = GroupCallActivity.isLandscapeMode ? AndroidUtilities.dp(90.0f) : 0;
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
        int i2;
        if (this.inFullscreenMode && this.fullscreenParticipant != null && this.fullscreenAnimator == null) {
            int currentAccount = this.groupCallActivity.getCurrentAccount();
            if (System.currentTimeMillis() - this.lastUpdateTooltipTime >= 500) {
                this.lastUpdateTooltipTime = System.currentTimeMillis();
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                ArrayList<TLRPC$TL_groupCallParticipant> arrayList = this.call.sortedParticipants;
                int i3 = 0;
                int i4 = 0;
                while (true) {
                    if (i3 >= arrayList.size()) {
                        break;
                    }
                    TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = arrayList.get(i3);
                    if (tLRPC$TL_groupCallParticipant.self || MessageObject.getPeerId(this.fullscreenParticipant.participant.peer) == MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer)) {
                        i2 = i3;
                    } else {
                        int peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
                        i2 = i3;
                        if (SystemClock.uptimeMillis() - tLRPC$TL_groupCallParticipant.lastSpeakTime < 500) {
                            if (i4 < 3) {
                                TLRPC$User user = peerId > 0 ? MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(peerId)) : null;
                                TLRPC$Chat chat = peerId <= 0 ? MessagesController.getInstance(currentAccount).getChat(Integer.valueOf(peerId)) : null;
                                if (!(user == null && chat == null)) {
                                    this.speakingMembersAvatars.setObject(i4, currentAccount, tLRPC$TL_groupCallParticipant);
                                    if (i4 != 0) {
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
                            i4++;
                            if (i4 == 3) {
                                break;
                            }
                            i3 = i2 + 1;
                        }
                    }
                    i3 = i2 + 1;
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
                String pluralString = LocaleController.getPluralString("MembersAreSpeakingToast", i4);
                int indexOf = pluralString.indexOf("un1");
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(pluralString);
                spannableStringBuilder2.replace(indexOf, indexOf + 3, spannableStringBuilder);
                this.speakingMembersText.setText(spannableStringBuilder2);
                if (i4 == 0) {
                    i = 0;
                } else if (i4 == 1) {
                    i = AndroidUtilities.dp(40.0f);
                } else if (i4 == 2) {
                    i = AndroidUtilities.dp(64.0f);
                } else {
                    i = AndroidUtilities.dp(88.0f);
                }
                ((FrameLayout.LayoutParams) this.speakingMembersText.getLayoutParams()).leftMargin = i;
                ((FrameLayout.LayoutParams) this.speakingMembersText.getLayoutParams()).rightMargin = AndroidUtilities.dp(16.0f);
                this.showSpeakingMembersToast = z3;
                invalidate();
                while (i4 < 3) {
                    this.speakingMembersAvatars.setObject(i4, currentAccount, (TLObject) null);
                    i4++;
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
}
