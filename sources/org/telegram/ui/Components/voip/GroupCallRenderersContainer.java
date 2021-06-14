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
import org.telegram.messenger.UserObject;
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
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.GroupCallActivity;

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
    private boolean drawFirst;
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
    private int speakingToastPeerId;
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
        if (this.inFullscreenMode) {
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
    /* JADX WARNING: Removed duplicated region for block: B:132:0x03dd  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x03ea  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x042e  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0436  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0441 A[LOOP:1: B:140:0x0441->B:147:0x049a, LOOP_START, PHI: r10 
      PHI: (r10v1 int) = (r10v0 int), (r10v2 int) binds: [B:139:0x043f, B:147:0x049a] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARNING: Removed duplicated region for block: B:155:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
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
            if (r1 == 0) goto L_0x0163
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
            if (r1 == 0) goto L_0x0160
            android.view.ViewParent r1 = r1.getParent()
            if (r1 == 0) goto L_0x0160
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
            if (r1 == 0) goto L_0x011b
            int r2 = r22.getMeasuredWidth()
            int r3 = r22.getMeasuredHeight()
            boolean r4 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r4 != 0) goto L_0x0116
            boolean r4 = org.telegram.ui.GroupCallActivity.isTabletMode
            if (r4 == 0) goto L_0x0111
            goto L_0x0116
        L_0x0111:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            goto L_0x0117
        L_0x0116:
            r4 = 0
        L_0x0117:
            int r3 = r3 - r4
            r8.clipRect(r10, r10, r2, r3)
        L_0x011b:
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
            if (r1 != 0) goto L_0x013f
            android.animation.ValueAnimator r1 = r0.zoomBackAnimator
            if (r1 == 0) goto L_0x013c
            goto L_0x013f
        L_0x013c:
            r16 = 0
            goto L_0x0141
        L_0x013f:
            r16 = 1
        L_0x0141:
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
        L_0x0160:
            r23.restore()
        L_0x0163:
            r15 = 0
        L_0x0164:
            r1 = 2
            r2 = 1090519040(0x41000000, float:8.0)
            r16 = 1073741824(0x40000000, float:2.0)
            if (r15 >= r1) goto L_0x026d
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r15]
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0269
            r23.save()
            boolean r1 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r1 == 0) goto L_0x017e
            r1 = 0
            goto L_0x018a
        L_0x017e:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r1 = -r1
            float r1 = (float) r1
            float r3 = r0.progressToHideUi
            float r3 = r14 - r3
            float r1 = r1 * r3
        L_0x018a:
            int r3 = r22.getMeasuredWidth()
            float r3 = (float) r3
            int r4 = r22.getMeasuredHeight()
            boolean r5 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r5 == 0) goto L_0x0199
            r5 = 0
            goto L_0x019d
        L_0x0199:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
        L_0x019d:
            int r4 = r4 - r5
            float r4 = (float) r4
            float r4 = r4 + r1
            r5 = 1099956224(0x41900000, float:18.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r4 = r4 - r5
            r8.clipRect(r13, r13, r3, r4)
            boolean r3 = r0.isTablet
            if (r3 == 0) goto L_0x01cf
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
            goto L_0x01fd
        L_0x01cf:
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
            if (r4 == 0) goto L_0x01eb
            r4 = 0
            goto L_0x01ef
        L_0x01eb:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
        L_0x01ef:
            float r4 = (float) r4
            float r2 = r2 - r4
            float r2 = r2 + r1
            r1 = 1104150528(0x41d00000, float:26.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r2 = r2 - r1
            r8.translate(r3, r2)
        L_0x01fd:
            org.telegram.ui.Components.UndoView[] r1 = r0.undoView
            r1 = r1[r15]
            float r1 = r1.getAlpha()
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 == 0) goto L_0x0230
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
            goto L_0x0233
        L_0x0230:
            r23.save()
        L_0x0233:
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
        L_0x0269:
            int r15 = r15 + 1
            goto L_0x0164
        L_0x026d:
            float r1 = r0.progressToFullscreenMode
            float r3 = r0.progressToHideUi
            float r3 = r14 - r3
            float r1 = r1 * r3
            android.animation.ValueAnimator r3 = r0.replaceFullscreenViewAnimator
            if (r3 == 0) goto L_0x02a5
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.outFullscreenTextureView
            if (r3 == 0) goto L_0x02a5
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            if (r4 == 0) goto L_0x02a5
            boolean r3 = r3.hasVideo
            boolean r5 = r4.hasVideo
            if (r3 == r5) goto L_0x0297
            if (r5 != 0) goto L_0x0290
            float r3 = r4.getAlpha()
            float r3 = r14 - r3
            goto L_0x0294
        L_0x0290:
            float r3 = r4.getAlpha()
        L_0x0294:
            float r3 = r3 * r1
            goto L_0x029c
        L_0x0297:
            if (r5 != 0) goto L_0x029b
            r3 = 0
            goto L_0x029c
        L_0x029b:
            r3 = r1
        L_0x029c:
            android.graphics.drawable.Drawable r4 = r0.topShadowDrawable
            float r3 = r3 * r12
            int r3 = (int) r3
            r4.setAlpha(r3)
            goto L_0x02c0
        L_0x02a5:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r3 = r0.fullscreenTextureView
            if (r3 == 0) goto L_0x02b8
            android.graphics.drawable.Drawable r4 = r0.topShadowDrawable
            float r12 = r12 * r1
            float r3 = r3.progressToNoVideoStub
            float r3 = r14 - r3
            float r12 = r12 * r3
            int r3 = (int) r12
            r4.setAlpha(r3)
            goto L_0x02c0
        L_0x02b8:
            android.graphics.drawable.Drawable r3 = r0.topShadowDrawable
            float r12 = r12 * r1
            int r4 = (int) r12
            r3.setAlpha(r4)
        L_0x02c0:
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
            if (r3 == 0) goto L_0x031e
            r3 = 1134821376(0x43a40000, float:328.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
        L_0x031b:
            float r3 = (float) r3
            float r4 = r4 - r3
            goto L_0x032b
        L_0x031e:
            boolean r3 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r3 == 0) goto L_0x0329
            r3 = 1127481344(0x43340000, float:180.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            goto L_0x031b
        L_0x0329:
            r3 = 0
            goto L_0x031b
        L_0x032b:
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
            if (r3 == 0) goto L_0x03a9
            r3 = 1033171465(0x3d94var_, float:0.07272727)
            float r1 = r1 + r3
            r0.speakingMembersToastChangeProgress = r1
            int r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r1 <= 0) goto L_0x03a1
            r0.speakingMembersToastChangeProgress = r14
            goto L_0x03a4
        L_0x03a1:
            r22.invalidate()
        L_0x03a4:
            android.widget.FrameLayout r1 = r0.speakingMembersToast
            r1.invalidate()
        L_0x03a9:
            boolean r1 = r0.showSpeakingMembersToast
            r3 = 1037726734(0x3dda740e, float:0.10666667)
            if (r1 == 0) goto L_0x03c4
            float r4 = r0.showSpeakingMembersToastProgress
            int r5 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r5 == 0) goto L_0x03c4
            float r4 = r4 + r3
            r0.showSpeakingMembersToastProgress = r4
            int r1 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r1 <= 0) goto L_0x03c0
            r0.showSpeakingMembersToastProgress = r14
            goto L_0x03d9
        L_0x03c0:
            r22.invalidate()
            goto L_0x03d9
        L_0x03c4:
            if (r1 != 0) goto L_0x03d9
            float r1 = r0.showSpeakingMembersToastProgress
            int r4 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r4 == 0) goto L_0x03d9
            float r1 = r1 - r3
            r0.showSpeakingMembersToastProgress = r1
            int r1 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x03d6
            r0.showSpeakingMembersToastProgress = r13
            goto L_0x03d9
        L_0x03d6:
            r22.invalidate()
        L_0x03d9:
            boolean r1 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r1 == 0) goto L_0x03ea
            android.widget.FrameLayout r1 = r0.speakingMembersToast
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r1.setTranslationY(r2)
            goto L_0x0409
        L_0x03ea:
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
        L_0x0409:
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
            if (r1 == 0) goto L_0x0436
            r0.notDrawRenderes = r9
            super.dispatchDraw(r23)
            r0.notDrawRenderes = r10
            goto L_0x0439
        L_0x0436:
            super.dispatchDraw(r23)
        L_0x0439:
            androidx.recyclerview.widget.RecyclerView r1 = r0.fullscreenListView
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x049d
        L_0x0441:
            androidx.recyclerview.widget.RecyclerView r1 = r0.fullscreenListView
            int r1 = r1.getChildCount()
            if (r10 >= r1) goto L_0x049d
            androidx.recyclerview.widget.RecyclerView r1 = r0.fullscreenListView
            android.view.View r1 = r1.getChildAt(r10)
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r1 = (org.telegram.ui.Components.GroupCallFullscreenAdapter.GroupCallUserCell) r1
            int r2 = r1.getVisibility()
            if (r2 != 0) goto L_0x049a
            float r2 = r1.getAlpha()
            int r2 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r2 == 0) goto L_0x049a
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
        L_0x049a:
            int r10 = r10 + 1
            goto L_0x0441
        L_0x049d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallRenderersContainer.dispatchDraw(android.graphics.Canvas):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:80:0x018e  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0195  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x019c  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x01bf  */
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
            r2 = 0
            if (r1 != 0) goto L_0x001a
            r3 = 0
            goto L_0x0022
        L_0x001a:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r3 = r1.participant
            org.telegram.tgnet.TLRPC$Peer r3 = r3.peer
            int r3 = org.telegram.messenger.MessageObject.getPeerId(r3)
        L_0x0022:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            if (r4 == 0) goto L_0x0029
            r4.runDelayedAnimations()
        L_0x0029:
            android.animation.ValueAnimator r4 = r0.replaceFullscreenViewAnimator
            if (r4 == 0) goto L_0x0030
            r4.cancel()
        L_0x0030:
            r0.fullscreenParticipant = r1
            r0.fullscreenPeerId = r3
            boolean r3 = r0.inFullscreenMode
            long r4 = java.lang.System.currentTimeMillis()
            r0.lastUpdateTime = r4
            r4 = 350(0x15e, double:1.73E-321)
            r6 = 1065353216(0x3var_, float:1.0)
            r7 = 2
            r8 = 0
            r9 = 0
            r10 = 1
            if (r1 != 0) goto L_0x00b8
            boolean r1 = r0.inFullscreenMode
            if (r1 == 0) goto L_0x00af
            android.animation.ValueAnimator r1 = r0.fullscreenAnimator
            if (r1 == 0) goto L_0x0051
            r1.cancel()
        L_0x0051:
            r0.inFullscreenMode = r2
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r1.primaryView
            if (r11 != 0) goto L_0x0061
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r11 = r1.secondaryView
            if (r11 != 0) goto L_0x0061
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r1.tabletGridView
            if (r11 == 0) goto L_0x006f
        L_0x0061:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.participant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r11 = r1.participant
            boolean r1 = r1.presentation
            org.telegram.messenger.ChatObject$Call r12 = r0.call
            boolean r1 = org.telegram.ui.GroupCallActivity.videoIsActive(r11, r1, r12)
            if (r1 != 0) goto L_0x00aa
        L_0x006f:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r1.forceDetach(r10)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r1.primaryView
            if (r1 == 0) goto L_0x007d
            r1.setRenderer(r9)
        L_0x007d:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r1 = r1.secondaryView
            if (r1 == 0) goto L_0x0086
            r1.setRenderer(r9)
        L_0x0086:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r1.tabletGridView
            if (r1 == 0) goto L_0x008f
            r1.setRenderer(r9)
        L_0x008f:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            android.view.ViewPropertyAnimator r11 = r1.animate()
            android.view.ViewPropertyAnimator r11 = r11.alpha(r8)
            org.telegram.ui.Components.voip.GroupCallRenderersContainer$7 r12 = new org.telegram.ui.Components.voip.GroupCallRenderersContainer$7
            r12.<init>(r1)
            android.view.ViewPropertyAnimator r1 = r11.setListener(r12)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r4)
            r1.start()
            goto L_0x00af
        L_0x00aa:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r1.setShowingInFullscreen(r2, r10)
        L_0x00af:
            android.widget.ImageView r1 = r0.backButton
            r1.setEnabled(r2)
            r0.hasPinnedVideo = r2
            goto L_0x02e4
        L_0x00b8:
            r11 = 0
        L_0x00b9:
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r12 = r0.attachedRenderers
            int r12 = r12.size()
            if (r11 >= r12) goto L_0x00dd
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r12 = r0.attachedRenderers
            java.lang.Object r12 = r12.get(r11)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r12 = (org.telegram.ui.Components.voip.GroupCallMiniTextureView) r12
            org.telegram.messenger.ChatObject$VideoParticipant r12 = r12.participant
            boolean r12 = r12.equals(r1)
            if (r12 == 0) goto L_0x00da
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r12 = r0.attachedRenderers
            java.lang.Object r11 = r12.get(r11)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r11 = (org.telegram.ui.Components.voip.GroupCallMiniTextureView) r11
            goto L_0x00de
        L_0x00da:
            int r11 = r11 + 1
            goto L_0x00b9
        L_0x00dd:
            r11 = r9
        L_0x00de:
            r12 = 1056964608(0x3var_, float:0.5)
            if (r11 == 0) goto L_0x01dc
            android.animation.ValueAnimator r1 = r0.fullscreenAnimator
            if (r1 == 0) goto L_0x00e9
            r1.cancel()
        L_0x00e9:
            boolean r1 = r0.inFullscreenMode
            if (r1 != 0) goto L_0x0100
            r0.inFullscreenMode = r10
            r0.fullscreenTextureView = r11
            r11.setShowingInFullscreen(r10, r10)
            r16.invalidate()
            org.telegram.ui.Components.CrossOutDrawable r1 = r0.pinDrawable
            boolean r11 = r0.hasPinnedVideo
            r1.setCrossOut(r11, r2)
            goto L_0x02df
        L_0x0100:
            r0.hasPinnedVideo = r2
            org.telegram.ui.Components.CrossOutDrawable r1 = r0.pinDrawable
            r1.setCrossOut(r2, r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r1.forceDetach(r2)
            r11.forceDetach(r2)
            boolean r1 = r0.isTablet
            if (r1 != 0) goto L_0x015b
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r13 = r1.primaryView
            if (r13 != 0) goto L_0x0121
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r13 = r1.secondaryView
            if (r13 != 0) goto L_0x0121
            org.telegram.ui.Components.voip.GroupCallGridCell r1 = r1.tabletGridView
            if (r1 == 0) goto L_0x015b
        L_0x0121:
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
            r1.setFullscreenMode(r13, r2)
            r1.updateAttachState(r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r13 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r13 = r13.primaryView
            if (r13 == 0) goto L_0x0148
            r13.setRenderer(r1)
        L_0x0148:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r13 = r0.fullscreenTextureView
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r13 = r13.secondaryView
            if (r13 == 0) goto L_0x0151
            r13.setRenderer(r1)
        L_0x0151:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r13 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r13 = r13.tabletGridView
            if (r13 == 0) goto L_0x015c
            r13.setRenderer(r1)
            goto L_0x015c
        L_0x015b:
            r1 = r9
        L_0x015c:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r13 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r14 = r0.attachedRenderers
            org.telegram.messenger.ChatObject$Call r15 = r0.call
            org.telegram.ui.GroupCallActivity r4 = r0.groupCallActivity
            r13.<init>(r0, r14, r15, r4)
            org.telegram.messenger.ChatObject$VideoParticipant r4 = r11.participant
            r13.participant = r4
            org.telegram.ui.Components.voip.GroupCallGridCell r4 = r11.primaryView
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r5 = r11.secondaryView
            org.telegram.ui.Components.voip.GroupCallGridCell r14 = r11.tabletGridView
            r13.setViews(r4, r5, r14)
            boolean r4 = r0.inFullscreenMode
            r13.setFullscreenMode(r4, r2)
            r13.updateAttachState(r2)
            org.telegram.ui.Components.voip.VoIPTextureView r4 = r13.textureView
            org.webrtc.TextureViewRenderer r4 = r4.renderer
            r4.setAlpha(r6)
            org.telegram.ui.Components.voip.VoIPTextureView r4 = r13.textureView
            android.view.TextureView r4 = r4.blurRenderer
            r4.setAlpha(r6)
            org.telegram.ui.Components.voip.GroupCallGridCell r4 = r11.primaryView
            if (r4 == 0) goto L_0x0191
            r4.setRenderer(r13)
        L_0x0191:
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r4 = r11.secondaryView
            if (r4 == 0) goto L_0x0198
            r4.setRenderer(r13)
        L_0x0198:
            org.telegram.ui.Components.voip.GroupCallGridCell r4 = r11.tabletGridView
            if (r4 == 0) goto L_0x019f
            r4.setRenderer(r13)
        L_0x019f:
            r13.animateEnter = r10
            r13.setAlpha(r8)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            r0.outFullscreenTextureView = r4
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r7]
            r5 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r13, r4, r5)
            r0.replaceFullscreenViewAnimator = r4
            org.telegram.ui.Components.voip.GroupCallRenderersContainer$8 r5 = new org.telegram.ui.Components.voip.GroupCallRenderersContainer$8
            r5.<init>(r13, r11)
            r4.addListener(r5)
            if (r1 == 0) goto L_0x01ca
            r1.setAlpha(r8)
            r1.setScaleX(r12)
            r1.setScaleY(r12)
            r1.animateEnter = r10
        L_0x01ca:
            org.telegram.ui.Components.voip.-$$Lambda$GroupCallRenderersContainer$9NlWBW3dhhuxw-ncKdlwogwYeHA r4 = new org.telegram.ui.Components.voip.-$$Lambda$GroupCallRenderersContainer$9NlWBW3dhhuxw-ncKdlwogwYeHA
            r4.<init>(r11, r1)
            r13.runOnFrameRendered(r4)
            r0.fullscreenTextureView = r13
            r13.setShowingInFullscreen(r10, r2)
            r16.update()
            goto L_0x02df
        L_0x01dc:
            boolean r4 = r0.inFullscreenMode
            if (r4 == 0) goto L_0x0299
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r5 = r4.primaryView
            if (r5 != 0) goto L_0x01fc
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r5 = r4.secondaryView
            if (r5 == 0) goto L_0x01ec
            r5 = 1
            goto L_0x01ed
        L_0x01ec:
            r5 = 0
        L_0x01ed:
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r4.tabletGridView
            if (r11 == 0) goto L_0x01f3
            r11 = 1
            goto L_0x01f4
        L_0x01f3:
            r11 = 0
        L_0x01f4:
            r5 = r5 | r11
            if (r5 == 0) goto L_0x01f8
            goto L_0x01fc
        L_0x01f8:
            r4.forceDetach(r10)
            goto L_0x024b
        L_0x01fc:
            r4.forceDetach(r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r5 = r0.attachedRenderers
            org.telegram.messenger.ChatObject$Call r11 = r0.call
            org.telegram.ui.GroupCallActivity r13 = r0.groupCallActivity
            r4.<init>(r0, r5, r11, r13)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r11 = r5.primaryView
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r13 = r5.secondaryView
            org.telegram.ui.Components.voip.GroupCallGridCell r5 = r5.tabletGridView
            r4.setViews(r11, r13, r5)
            boolean r5 = r0.inFullscreenMode
            r4.setFullscreenMode(r5, r2)
            r4.updateAttachState(r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r5 = r5.primaryView
            if (r5 == 0) goto L_0x0226
            r5.setRenderer(r4)
        L_0x0226:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r0.fullscreenTextureView
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r5 = r5.secondaryView
            if (r5 == 0) goto L_0x022f
            r5.setRenderer(r4)
        L_0x022f:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r5 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.GroupCallGridCell r5 = r5.tabletGridView
            if (r5 == 0) goto L_0x0238
            r5.setRenderer(r4)
        L_0x0238:
            r4.setAlpha(r8)
            r4.setScaleX(r12)
            r4.setScaleY(r12)
            r4.animateEnter = r10
            org.telegram.ui.Components.voip.-$$Lambda$GroupCallRenderersContainer$KXeEaD1-nec5x50G8V2Rxw_pZx4 r5 = new org.telegram.ui.Components.voip.-$$Lambda$GroupCallRenderersContainer$KXeEaD1-nec5x50G8V2Rxw_pZx4
            r5.<init>(r4)
            r4.runOnFrameRendered(r5)
        L_0x024b:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r5 = r0.attachedRenderers
            org.telegram.messenger.ChatObject$Call r11 = r0.call
            org.telegram.ui.GroupCallActivity r12 = r0.groupCallActivity
            r4.<init>(r0, r5, r11, r12)
            r4.participant = r1
            boolean r1 = r0.inFullscreenMode
            r4.setFullscreenMode(r1, r2)
            r4.setShowingInFullscreen(r10, r2)
            r4.animateEnter = r10
            r4.setAlpha(r8)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r0.outFullscreenTextureView = r1
            float[] r1 = new float[r7]
            r1 = {0, NUM} // fill-array
            android.animation.ValueAnimator r1 = android.animation.ValueAnimator.ofFloat(r1)
            r0.replaceFullscreenViewAnimator = r1
            org.telegram.ui.Components.voip.-$$Lambda$GroupCallRenderersContainer$89hD6AwIwR24tybDizgDzRTjUnY r5 = new org.telegram.ui.Components.voip.-$$Lambda$GroupCallRenderersContainer$89hD6AwIwR24tybDizgDzRTjUnY
            r5.<init>(r4)
            r1.addUpdateListener(r5)
            android.animation.ValueAnimator r1 = r0.replaceFullscreenViewAnimator
            org.telegram.ui.Components.voip.GroupCallRenderersContainer$12 r5 = new org.telegram.ui.Components.voip.GroupCallRenderersContainer$12
            r5.<init>(r4)
            r1.addListener(r5)
            android.animation.ValueAnimator r1 = r0.replaceFullscreenViewAnimator
            r1.start()
            r0.fullscreenTextureView = r4
            r4.setShowingInFullscreen(r10, r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r1.updateAttachState(r2)
            r16.update()
            goto L_0x02df
        L_0x0299:
            r0.inFullscreenMode = r10
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r4 = new org.telegram.ui.Components.voip.GroupCallMiniTextureView
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r5 = r0.attachedRenderers
            org.telegram.messenger.ChatObject$Call r11 = r0.call
            org.telegram.ui.GroupCallActivity r12 = r0.groupCallActivity
            r4.<init>(r0, r5, r11, r12)
            r0.fullscreenTextureView = r4
            r4.participant = r1
            boolean r1 = r0.inFullscreenMode
            r4.setFullscreenMode(r1, r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r1.setShowingInFullscreen(r10, r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r1.setShowingInFullscreen(r10, r2)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            android.util.Property r4 = android.view.View.ALPHA
            float[] r5 = new float[r7]
            r5 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofFloat(r1, r4, r5)
            r0.replaceFullscreenViewAnimator = r1
            org.telegram.ui.Components.voip.GroupCallRenderersContainer$13 r4 = new org.telegram.ui.Components.voip.GroupCallRenderersContainer$13
            r4.<init>()
            r1.addListener(r4)
            android.animation.ValueAnimator r1 = r0.replaceFullscreenViewAnimator
            r1.start()
            r16.invalidate()
            org.telegram.ui.Components.CrossOutDrawable r1 = r0.pinDrawable
            boolean r4 = r0.hasPinnedVideo
            r1.setCrossOut(r4, r2)
        L_0x02df:
            android.widget.ImageView r1 = r0.backButton
            r1.setEnabled(r10)
        L_0x02e4:
            boolean r1 = r0.inFullscreenMode
            if (r3 == r1) goto L_0x0367
            if (r1 != 0) goto L_0x02f9
            r0.setUiVisible(r10)
            boolean r1 = r0.hideUiRunnableIsScheduled
            if (r1 == 0) goto L_0x030d
            r0.hideUiRunnableIsScheduled = r2
            java.lang.Runnable r1 = r0.hideUiRunnable
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1)
            goto L_0x030d
        L_0x02f9:
            android.widget.ImageView r1 = r0.backButton
            r1.setVisibility(r2)
            android.widget.ImageView r1 = r0.pinButton
            r1.setVisibility(r2)
            android.widget.TextView r1 = r0.unpinTextView
            r1.setVisibility(r2)
            android.view.View r1 = r0.pinContainer
            r1.setVisibility(r2)
        L_0x030d:
            r0.onFullScreenModeChanged(r10)
            float[] r1 = new float[r7]
            float r3 = r0.progressToFullscreenMode
            r1[r2] = r3
            boolean r3 = r0.inFullscreenMode
            if (r3 == 0) goto L_0x031b
            goto L_0x031c
        L_0x031b:
            r6 = 0
        L_0x031c:
            r1[r10] = r6
            android.animation.ValueAnimator r1 = android.animation.ValueAnimator.ofFloat(r1)
            r0.fullscreenAnimator = r1
            org.telegram.ui.Components.voip.-$$Lambda$GroupCallRenderersContainer$JUury30Nbkwb3zQadlyD3CLASSNAMEITE r3 = new org.telegram.ui.Components.voip.-$$Lambda$GroupCallRenderersContainer$JUury30Nbkwb3zQadlyD3CLASSNAMEITE
            r3.<init>()
            r1.addUpdateListener(r3)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            r1.animateToFullscreen = r10
            org.telegram.ui.GroupCallActivity r3 = r0.groupCallActivity
            int r3 = r3.getCurrentAccount()
            boolean r4 = r0.swipeToBackGesture
            r0.swipedBack = r4
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getInstance(r3)
            int r5 = r0.animationIndex
            int r4 = r4.setAnimationInProgress(r5, r9)
            r0.animationIndex = r4
            android.animation.ValueAnimator r4 = r0.fullscreenAnimator
            org.telegram.ui.Components.voip.GroupCallRenderersContainer$14 r5 = new org.telegram.ui.Components.voip.GroupCallRenderersContainer$14
            r5.<init>(r3, r1)
            r4.addListener(r5)
            android.animation.ValueAnimator r1 = r0.fullscreenAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r1.setInterpolator(r3)
            android.animation.ValueAnimator r1 = r0.fullscreenAnimator
            r3 = 350(0x15e, double:1.73E-321)
            r1.setDuration(r3)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = r0.fullscreenTextureView
            org.telegram.ui.Components.voip.VoIPTextureView r1 = r1.textureView
            android.animation.ValueAnimator r3 = r0.fullscreenAnimator
            r1.synchOrRunAnimation(r3)
        L_0x0367:
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r0.fullscreenParticipant
            if (r1 != 0) goto L_0x036c
            r2 = 1
        L_0x036c:
            r0.animateSwipeToBack(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallRenderersContainer.requestFullscreen(org.telegram.messenger.ChatObject$VideoParticipant):void");
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
        }).setDuration(100).start();
        if (groupCallMiniTextureView2 != null) {
            groupCallMiniTextureView2.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(100).setListener(new AnimatorListenerAdapter() {
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

    /* JADX WARNING: Removed duplicated region for block: B:103:0x021b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r14) {
        /*
            r13 = this;
            boolean r0 = r13.inFullscreenMode
            r1 = 0
            if (r0 == 0) goto L_0x04a3
            boolean r0 = r13.maybeSwipeToBackGesture
            if (r0 != 0) goto L_0x0023
            boolean r0 = r13.swipeToBackGesture
            if (r0 != 0) goto L_0x0023
            boolean r0 = r13.tapGesture
            if (r0 != 0) goto L_0x0023
            boolean r0 = r13.canZoomGesture
            if (r0 != 0) goto L_0x0023
            boolean r0 = r13.isInPinchToZoomTouchMode
            if (r0 != 0) goto L_0x0023
            boolean r0 = r13.zoomStarted
            if (r0 != 0) goto L_0x0023
            int r0 = r14.getActionMasked()
            if (r0 != 0) goto L_0x04a3
        L_0x0023:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r0 = r13.fullscreenTextureView
            if (r0 != 0) goto L_0x0029
            goto L_0x04a3
        L_0x0029:
            int r0 = r14.getActionMasked()
            if (r0 != 0) goto L_0x0039
            r13.maybeSwipeToBackGesture = r1
            r13.swipeToBackGesture = r1
            r13.canZoomGesture = r1
            r13.isInPinchToZoomTouchMode = r1
            r13.zoomStarted = r1
        L_0x0039:
            int r0 = r14.getActionMasked()
            r2 = 0
            r3 = 1
            if (r0 != 0) goto L_0x005f
            android.animation.ValueAnimator r0 = r13.swipeToBackAnimator
            if (r0 == 0) goto L_0x005f
            r13.maybeSwipeToBackGesture = r1
            r13.swipeToBackGesture = r3
            float r0 = r14.getY()
            float r4 = r13.swipeToBackDy
            float r0 = r0 - r4
            r13.tapY = r0
            android.animation.ValueAnimator r0 = r13.swipeToBackAnimator
            r0.removeAllListeners()
            android.animation.ValueAnimator r0 = r13.swipeToBackAnimator
            r0.cancel()
            r13.swipeToBackAnimator = r2
            goto L_0x0067
        L_0x005f:
            android.animation.ValueAnimator r0 = r13.swipeToBackAnimator
            if (r0 == 0) goto L_0x0067
            r13.finishZoom()
            return r1
        L_0x0067:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r0 = r13.fullscreenTextureView
            float r4 = r14.getX()
            float r5 = r14.getY()
            boolean r0 = r0.isInsideStopScreenButton(r4, r5)
            if (r0 == 0) goto L_0x0078
            return r1
        L_0x0078:
            int r0 = r14.getActionMasked()
            r4 = 1119092736(0x42b40000, float:90.0)
            r5 = 0
            r6 = 2
            if (r0 != 0) goto L_0x00e4
            boolean r0 = r13.swipeToBackGesture
            if (r0 != 0) goto L_0x00e4
            android.graphics.RectF r0 = org.telegram.messenger.AndroidUtilities.rectTmp
            int r7 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            float r7 = (float) r7
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r8 = r13.fullscreenTextureView
            int r8 = r8.getMeasuredWidth()
            boolean r9 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r9 == 0) goto L_0x00a1
            boolean r9 = r13.uiVisible
            if (r9 == 0) goto L_0x00a1
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r9 = -r9
            goto L_0x00a2
        L_0x00a1:
            r9 = 0
        L_0x00a2:
            int r8 = r8 + r9
            float r8 = (float) r8
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r9 = r13.fullscreenTextureView
            int r9 = r9.getMeasuredHeight()
            boolean r10 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r10 != 0) goto L_0x00b8
            boolean r10 = r13.uiVisible
            if (r10 == 0) goto L_0x00b8
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r10 = -r10
            goto L_0x00b9
        L_0x00b8:
            r10 = 0
        L_0x00b9:
            int r9 = r9 + r10
            float r9 = (float) r9
            r0.set(r5, r7, r8, r9)
            float r7 = r14.getX()
            float r8 = r14.getY()
            boolean r0 = r0.contains(r7, r8)
            if (r0 == 0) goto L_0x016d
            long r7 = java.lang.System.currentTimeMillis()
            r13.tapTime = r7
            r13.tapGesture = r3
            r13.maybeSwipeToBackGesture = r3
            float r0 = r14.getX()
            r13.tapX = r0
            float r0 = r14.getY()
            r13.tapY = r0
            goto L_0x016d
        L_0x00e4:
            boolean r0 = r13.maybeSwipeToBackGesture
            if (r0 != 0) goto L_0x00f0
            boolean r0 = r13.swipeToBackGesture
            if (r0 != 0) goto L_0x00f0
            boolean r0 = r13.tapGesture
            if (r0 == 0) goto L_0x016d
        L_0x00f0:
            int r0 = r14.getActionMasked()
            if (r0 != r6) goto L_0x016d
            float r0 = r13.tapX
            float r7 = r14.getX()
            float r0 = r0 - r7
            float r0 = java.lang.Math.abs(r0)
            int r7 = r13.touchSlop
            float r7 = (float) r7
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 > 0) goto L_0x011a
            float r0 = r13.tapY
            float r7 = r14.getY()
            float r0 = r0 - r7
            float r0 = java.lang.Math.abs(r0)
            int r7 = r13.touchSlop
            float r7 = (float) r7
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 <= 0) goto L_0x011c
        L_0x011a:
            r13.tapGesture = r1
        L_0x011c:
            boolean r0 = r13.maybeSwipeToBackGesture
            if (r0 == 0) goto L_0x0143
            boolean r0 = r13.zoomStarted
            if (r0 != 0) goto L_0x0143
            float r0 = r13.tapY
            float r7 = r14.getY()
            float r0 = r0 - r7
            float r0 = java.lang.Math.abs(r0)
            int r7 = r13.touchSlop
            int r7 = r7 * 2
            float r7 = (float) r7
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 <= 0) goto L_0x0143
            float r0 = r14.getY()
            r13.tapY = r0
            r13.maybeSwipeToBackGesture = r1
            r13.swipeToBackGesture = r3
            goto L_0x0153
        L_0x0143:
            boolean r0 = r13.swipeToBackGesture
            if (r0 == 0) goto L_0x0153
            float r0 = r14.getY()
            float r7 = r13.tapY
            float r0 = r0 - r7
            r13.swipeToBackDy = r0
            r13.invalidate()
        L_0x0153:
            boolean r0 = r13.maybeSwipeToBackGesture
            if (r0 == 0) goto L_0x016d
            float r0 = r13.tapX
            float r7 = r14.getX()
            float r0 = r0 - r7
            float r0 = java.lang.Math.abs(r0)
            int r7 = r13.touchSlop
            int r7 = r7 * 4
            float r7 = (float) r7
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 <= 0) goto L_0x016d
            r13.maybeSwipeToBackGesture = r1
        L_0x016d:
            boolean r0 = r13.tapGesture
            if (r0 == 0) goto L_0x0226
            int r0 = r14.getActionMasked()
            if (r0 != r3) goto L_0x0226
            long r7 = java.lang.System.currentTimeMillis()
            long r9 = r13.tapTime
            long r7 = r7 - r9
            r9 = 200(0xc8, double:9.9E-322)
            int r0 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r0 >= 0) goto L_0x0226
            boolean r0 = r13.showSpeakingMembersToast
            if (r0 == 0) goto L_0x0218
            android.graphics.RectF r0 = org.telegram.messenger.AndroidUtilities.rectTmp
            android.widget.FrameLayout r7 = r13.speakingMembersToast
            float r7 = r7.getX()
            android.widget.FrameLayout r8 = r13.speakingMembersToast
            float r8 = r8.getY()
            android.widget.FrameLayout r9 = r13.speakingMembersToast
            float r9 = r9.getX()
            android.widget.FrameLayout r10 = r13.speakingMembersToast
            int r10 = r10.getWidth()
            float r10 = (float) r10
            float r9 = r9 + r10
            android.widget.FrameLayout r10 = r13.speakingMembersToast
            float r10 = r10.getY()
            android.widget.FrameLayout r11 = r13.speakingMembersToast
            int r11 = r11.getHeight()
            float r11 = (float) r11
            float r10 = r10 + r11
            r0.set(r7, r8, r9, r10)
            org.telegram.messenger.ChatObject$Call r7 = r13.call
            if (r7 == 0) goto L_0x0218
            float r7 = r14.getX()
            float r8 = r14.getY()
            boolean r0 = r0.contains(r7, r8)
            if (r0 == 0) goto L_0x0218
            r0 = 0
            r7 = 0
            r8 = 0
        L_0x01ca:
            org.telegram.messenger.ChatObject$Call r9 = r13.call
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r9 = r9.visibleVideoParticipants
            int r9 = r9.size()
            if (r0 >= r9) goto L_0x01fe
            int r9 = r13.speakingToastPeerId
            org.telegram.messenger.ChatObject$Call r10 = r13.call
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r10 = r10.visibleVideoParticipants
            java.lang.Object r10 = r10.get(r0)
            org.telegram.messenger.ChatObject$VideoParticipant r10 = (org.telegram.messenger.ChatObject.VideoParticipant) r10
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r10 = r10.participant
            org.telegram.tgnet.TLRPC$Peer r10 = r10.peer
            int r10 = org.telegram.messenger.MessageObject.getPeerId(r10)
            if (r9 != r10) goto L_0x01fb
            org.telegram.ui.GroupCallActivity r7 = r13.groupCallActivity
            org.telegram.messenger.ChatObject$Call r8 = r13.call
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r8 = r8.visibleVideoParticipants
            java.lang.Object r8 = r8.get(r0)
            org.telegram.messenger.ChatObject$VideoParticipant r8 = (org.telegram.messenger.ChatObject.VideoParticipant) r8
            r7.fullscreenFor(r8)
            r7 = 1
            r8 = 1
        L_0x01fb:
            int r0 = r0 + 1
            goto L_0x01ca
        L_0x01fe:
            if (r7 != 0) goto L_0x0219
            org.telegram.messenger.ChatObject$Call r0 = r13.call
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r0 = r0.participants
            int r7 = r13.speakingToastPeerId
            java.lang.Object r0 = r0.get(r7)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r0 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r0
            org.telegram.ui.GroupCallActivity r7 = r13.groupCallActivity
            org.telegram.messenger.ChatObject$VideoParticipant r8 = new org.telegram.messenger.ChatObject$VideoParticipant
            r8.<init>(r0, r1, r1)
            r7.fullscreenFor(r8)
            r8 = 1
            goto L_0x0219
        L_0x0218:
            r8 = 0
        L_0x0219:
            if (r8 != 0) goto L_0x0221
            boolean r0 = r13.uiVisible
            r0 = r0 ^ r3
            r13.setUiVisible(r0)
        L_0x0221:
            r13.swipeToBackDy = r5
            r13.invalidate()
        L_0x0226:
            boolean r0 = r13.maybeSwipeToBackGesture
            r7 = 3
            if (r0 != 0) goto L_0x022f
            boolean r0 = r13.swipeToBackGesture
            if (r0 == 0) goto L_0x0235
        L_0x022f:
            int r0 = r14.getActionMasked()
            if (r0 == r3) goto L_0x023b
        L_0x0235:
            int r0 = r14.getActionMasked()
            if (r0 != r7) goto L_0x0264
        L_0x023b:
            r13.maybeSwipeToBackGesture = r1
            boolean r0 = r13.swipeToBackGesture
            if (r0 == 0) goto L_0x0261
            int r0 = r14.getActionMasked()
            if (r0 != r3) goto L_0x025e
            float r0 = r13.swipeToBackDy
            float r0 = java.lang.Math.abs(r0)
            r8 = 1123024896(0x42var_, float:120.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            int r0 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
            if (r0 <= 0) goto L_0x025e
            org.telegram.ui.GroupCallActivity r0 = r13.groupCallActivity
            r0.fullscreenFor(r2)
            goto L_0x0261
        L_0x025e:
            r13.animateSwipeToBack(r1)
        L_0x0261:
            r13.invalidate()
        L_0x0264:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r0 = r13.fullscreenTextureView
            boolean r0 = r0.hasVideo
            if (r0 == 0) goto L_0x0492
            boolean r0 = r13.swipeToBackGesture
            if (r0 == 0) goto L_0x0270
            goto L_0x0492
        L_0x0270:
            int r0 = r14.getActionMasked()
            r2 = 1065353216(0x3var_, float:1.0)
            r8 = 1073741824(0x40000000, float:2.0)
            if (r0 == 0) goto L_0x037e
            int r0 = r14.getActionMasked()
            r9 = 5
            if (r0 != r9) goto L_0x0283
            goto L_0x037e
        L_0x0283:
            int r0 = r14.getActionMasked()
            if (r0 != r6) goto L_0x0359
            boolean r0 = r13.isInPinchToZoomTouchMode
            if (r0 == 0) goto L_0x0359
            r0 = -1
            r4 = 0
            r6 = -1
            r7 = -1
        L_0x0291:
            int r9 = r14.getPointerCount()
            if (r4 >= r9) goto L_0x02ac
            int r9 = r13.pointerId1
            int r10 = r14.getPointerId(r4)
            if (r9 != r10) goto L_0x02a0
            r6 = r4
        L_0x02a0:
            int r9 = r13.pointerId2
            int r10 = r14.getPointerId(r4)
            if (r9 != r10) goto L_0x02a9
            r7 = r4
        L_0x02a9:
            int r4 = r4 + 1
            goto L_0x0291
        L_0x02ac:
            if (r6 == r0) goto L_0x034c
            if (r7 != r0) goto L_0x02b2
            goto L_0x034c
        L_0x02b2:
            float r0 = r14.getX(r7)
            float r4 = r14.getX(r6)
            float r0 = r0 - r4
            double r9 = (double) r0
            float r0 = r14.getY(r7)
            float r4 = r14.getY(r6)
            float r0 = r0 - r4
            double r11 = (double) r0
            double r9 = java.lang.Math.hypot(r9, r11)
            float r0 = (float) r9
            float r4 = r13.pinchStartDistance
            float r0 = r0 / r4
            r13.pinchScale = r0
            r4 = 1065395159(0x3var_a3d7, float:1.005)
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x0323
            boolean r0 = r13.zoomStarted
            if (r0 != 0) goto L_0x0323
            float r0 = r14.getX(r7)
            float r4 = r14.getX(r6)
            float r0 = r0 - r4
            double r9 = (double) r0
            float r0 = r14.getY(r7)
            float r4 = r14.getY(r6)
            float r0 = r0 - r4
            double r11 = (double) r0
            double r9 = java.lang.Math.hypot(r9, r11)
            float r0 = (float) r9
            r13.pinchStartDistance = r0
            float r0 = r14.getX(r6)
            float r4 = r14.getX(r7)
            float r0 = r0 + r4
            float r0 = r0 / r8
            r13.pinchCenterX = r0
            r13.pinchStartCenterX = r0
            float r0 = r14.getY(r6)
            float r4 = r14.getY(r7)
            float r0 = r0 + r4
            float r0 = r0 / r8
            r13.pinchCenterY = r0
            r13.pinchStartCenterY = r0
            r13.pinchScale = r2
            r13.pinchTranslationX = r5
            r13.pinchTranslationY = r5
            android.view.ViewParent r0 = r13.getParent()
            r0.requestDisallowInterceptTouchEvent(r3)
            r13.zoomStarted = r3
            r13.isInPinchToZoomTouchMode = r3
        L_0x0323:
            float r0 = r14.getX(r6)
            float r2 = r14.getX(r7)
            float r0 = r0 + r2
            float r0 = r0 / r8
            float r2 = r14.getY(r6)
            float r14 = r14.getY(r7)
            float r2 = r2 + r14
            float r2 = r2 / r8
            float r14 = r13.pinchStartCenterX
            float r14 = r14 - r0
            float r0 = r13.pinchStartCenterY
            float r0 = r0 - r2
            float r14 = -r14
            float r2 = r13.pinchScale
            float r14 = r14 / r2
            r13.pinchTranslationX = r14
            float r14 = -r0
            float r14 = r14 / r2
            r13.pinchTranslationY = r14
            r13.invalidate()
            goto L_0x0484
        L_0x034c:
            android.view.ViewParent r14 = r13.getParent()
            r14.requestDisallowInterceptTouchEvent(r1)
            r13.finishZoom()
            boolean r14 = r13.maybeSwipeToBackGesture
            return r14
        L_0x0359:
            int r0 = r14.getActionMasked()
            if (r0 == r3) goto L_0x0372
            int r0 = r14.getActionMasked()
            r2 = 6
            if (r0 != r2) goto L_0x036c
            boolean r0 = r13.checkPointerIds(r14)
            if (r0 != 0) goto L_0x0372
        L_0x036c:
            int r14 = r14.getActionMasked()
            if (r14 != r7) goto L_0x0484
        L_0x0372:
            android.view.ViewParent r14 = r13.getParent()
            r14.requestDisallowInterceptTouchEvent(r1)
            r13.finishZoom()
            goto L_0x0484
        L_0x037e:
            int r0 = r14.getActionMasked()
            if (r0 != 0) goto L_0x0433
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r0 = r13.fullscreenTextureView
            org.telegram.ui.Components.voip.VoIPTextureView r0 = r0.textureView
            org.webrtc.TextureViewRenderer r0 = r0.renderer
            android.graphics.RectF r5 = org.telegram.messenger.AndroidUtilities.rectTmp
            float r7 = r0.getX()
            float r9 = r0.getY()
            float r10 = r0.getX()
            int r11 = r0.getMeasuredWidth()
            float r11 = (float) r11
            float r10 = r10 + r11
            float r11 = r0.getY()
            int r12 = r0.getMeasuredHeight()
            float r12 = (float) r12
            float r11 = r11 + r12
            r5.set(r7, r9, r10, r11)
            int r7 = r0.getMeasuredHeight()
            float r7 = (float) r7
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r9 = r13.fullscreenTextureView
            org.telegram.ui.Components.voip.VoIPTextureView r9 = r9.textureView
            float r9 = r9.scaleTextureToFill
            float r7 = r7 * r9
            int r9 = r0.getMeasuredHeight()
            float r9 = (float) r9
            float r7 = r7 - r9
            float r7 = r7 / r8
            int r9 = r0.getMeasuredWidth()
            float r9 = (float) r9
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r10 = r13.fullscreenTextureView
            org.telegram.ui.Components.voip.VoIPTextureView r10 = r10.textureView
            float r10 = r10.scaleTextureToFill
            float r9 = r9 * r10
            int r0 = r0.getMeasuredWidth()
            float r0 = (float) r0
            float r9 = r9 - r0
            float r9 = r9 / r8
            r5.inset(r7, r9)
            boolean r0 = org.telegram.ui.GroupCallActivity.isLandscapeMode
            if (r0 != 0) goto L_0x03fc
            float r0 = r5.top
            int r7 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            float r7 = (float) r7
            float r0 = java.lang.Math.max(r0, r7)
            r5.top = r0
            float r0 = r5.bottom
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r7 = r13.fullscreenTextureView
            int r7 = r7.getMeasuredHeight()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r7 = r7 - r4
            float r4 = (float) r7
            float r0 = java.lang.Math.min(r0, r4)
            r5.bottom = r0
            goto L_0x041d
        L_0x03fc:
            float r0 = r5.top
            int r7 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            float r7 = (float) r7
            float r0 = java.lang.Math.max(r0, r7)
            r5.top = r0
            float r0 = r5.right
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r7 = r13.fullscreenTextureView
            int r7 = r7.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r7 = r7 - r4
            float r4 = (float) r7
            float r0 = java.lang.Math.min(r0, r4)
            r5.right = r0
        L_0x041d:
            float r0 = r14.getX()
            float r4 = r14.getY()
            boolean r0 = r5.contains(r0, r4)
            r13.canZoomGesture = r0
            if (r0 != 0) goto L_0x0433
            r13.finishZoom()
            boolean r14 = r13.maybeSwipeToBackGesture
            return r14
        L_0x0433:
            boolean r0 = r13.isInPinchToZoomTouchMode
            if (r0 != 0) goto L_0x0484
            int r0 = r14.getPointerCount()
            if (r0 != r6) goto L_0x0484
            float r0 = r14.getX(r3)
            float r4 = r14.getX(r1)
            float r0 = r0 - r4
            double r4 = (double) r0
            float r0 = r14.getY(r3)
            float r6 = r14.getY(r1)
            float r0 = r0 - r6
            double r6 = (double) r0
            double r4 = java.lang.Math.hypot(r4, r6)
            float r0 = (float) r4
            r13.pinchStartDistance = r0
            float r0 = r14.getX(r1)
            float r4 = r14.getX(r3)
            float r0 = r0 + r4
            float r0 = r0 / r8
            r13.pinchCenterX = r0
            r13.pinchStartCenterX = r0
            float r0 = r14.getY(r1)
            float r4 = r14.getY(r3)
            float r0 = r0 + r4
            float r0 = r0 / r8
            r13.pinchCenterY = r0
            r13.pinchStartCenterY = r0
            r13.pinchScale = r2
            int r0 = r14.getPointerId(r1)
            r13.pointerId1 = r0
            int r14 = r14.getPointerId(r3)
            r13.pointerId2 = r14
            r13.isInPinchToZoomTouchMode = r3
        L_0x0484:
            boolean r14 = r13.canZoomGesture
            if (r14 != 0) goto L_0x0490
            boolean r14 = r13.tapGesture
            if (r14 != 0) goto L_0x0490
            boolean r14 = r13.maybeSwipeToBackGesture
            if (r14 == 0) goto L_0x0491
        L_0x0490:
            r1 = 1
        L_0x0491:
            return r1
        L_0x0492:
            r13.finishZoom()
            boolean r14 = r13.tapGesture
            if (r14 != 0) goto L_0x04a1
            boolean r14 = r13.swipeToBackGesture
            if (r14 != 0) goto L_0x04a1
            boolean r14 = r13.maybeSwipeToBackGesture
            if (r14 == 0) goto L_0x04a2
        L_0x04a1:
            r1 = 1
        L_0x04a2:
            return r1
        L_0x04a3:
            r13.finishZoom()
            return r1
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
                        int peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
                        i = i3;
                        if (!(SystemClock.uptimeMillis() - tLRPC$TL_groupCallParticipant.lastSpeakTime < 500)) {
                            continue;
                        } else {
                            if (spannableStringBuilder == null) {
                                spannableStringBuilder = new SpannableStringBuilder();
                            }
                            if (i4 == 0) {
                                this.speakingToastPeerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
                            }
                            if (i4 < 3) {
                                TLRPC$User user = peerId > 0 ? MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(peerId)) : null;
                                TLRPC$Chat chat = peerId <= 0 ? MessagesController.getInstance(currentAccount).getChat(Integer.valueOf(peerId)) : null;
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

    public void setGroupCall(ChatObject.Call call2) {
        this.call = call2;
    }
}
