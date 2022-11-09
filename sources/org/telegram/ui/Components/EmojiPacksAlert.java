package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSetInstallResultArchive;
import org.telegram.tgnet.TLRPC$TL_stickerPack;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.EmojiPacksAlert;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.ProfileActivity;
/* loaded from: classes3.dex */
public class EmojiPacksAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private static Pattern urlPattern;
    private TextView addButtonView;
    private LongSparseArray<AnimatedEmojiDrawable> animatedEmojiDrawables;
    private FrameLayout buttonsView;
    private EmojiPacksLoader customEmojiPacks;
    private BaseFragment fragment;
    private Float fromY;
    private GridLayoutManager gridLayoutManager;
    private boolean hasDescription;
    private float lastY;
    private RecyclerListView listView;
    private ValueAnimator loadAnimator;
    private float loadT;
    boolean loaded;
    private View paddingView;
    private ActionBarPopupWindow popupWindow;
    private long premiumButtonClicked;
    private PremiumButtonView premiumButtonView;
    private CircularProgressDrawable progressDrawable;
    private TextView removeButtonView;
    private View shadowView;
    private boolean shown;

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    protected void onButtonClicked(boolean z) {
    }

    protected void onCloseByLink() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void access$5500(EmojiPacksAlert emojiPacksAlert, int i) {
        emojiPacksAlert.onSubItemClick(i);
    }

    public EmojiPacksAlert(final BaseFragment baseFragment, final Context context, final Theme.ResourcesProvider resourcesProvider, final ArrayList<TLRPC$InputStickerSet> arrayList) {
        super(context, false, resourcesProvider);
        this.shown = false;
        this.loaded = true;
        arrayList.size();
        this.fragment = baseFragment;
        fixNavigationBar();
        this.customEmojiPacks = new EmojiPacksLoader(this.currentAccount, arrayList) { // from class: org.telegram.ui.Components.EmojiPacksAlert.1
            @Override // org.telegram.ui.Components.EmojiPacksAlert.EmojiPacksLoader
            protected void onUpdate() {
                EmojiPacksAlert.this.updateButton();
                if (EmojiPacksAlert.this.listView == null || EmojiPacksAlert.this.listView.getAdapter() == null) {
                    return;
                }
                EmojiPacksAlert.this.listView.getAdapter().notifyDataSetChanged();
            }
        };
        this.progressDrawable = new CircularProgressDrawable(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(3.5f), getThemedColor("featuredStickers_addButton"));
        final PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(ColorUtils.setAlphaComponent(getThemedColor("windowBackgroundWhiteLinkText"), 178), PorterDuff.Mode.MULTIPLY);
        this.containerView = new FrameLayout(context) { // from class: org.telegram.ui.Components.EmojiPacksAlert.2
            boolean attached;
            private Paint paint = new Paint();
            private Path path = new Path();
            private Boolean lastOpen = null;
            SparseArray<ArrayList<EmojiImageView>> viewsGroupedByLines = new SparseArray<>();
            ArrayList<DrawingInBackgroundLine> lineDrawables = new ArrayList<>();
            ArrayList<DrawingInBackgroundLine> lineDrawablesTmp = new ArrayList<>();
            ArrayList<ArrayList<EmojiImageView>> unusedArrays = new ArrayList<>();
            ArrayList<DrawingInBackgroundLine> unusedLineDrawables = new ArrayList<>();

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                EmojiPacksAlert emojiPacksAlert;
                float f;
                if (!this.attached) {
                    return;
                }
                this.paint.setColor(EmojiPacksAlert.this.getThemedColor("dialogBackground"));
                this.paint.setShadowLayer(AndroidUtilities.dp(2.0f), 0.0f, AndroidUtilities.dp(-0.66f), NUM);
                this.path.reset();
                float f2 = EmojiPacksAlert.this.lastY = emojiPacksAlert.getListTop();
                if (EmojiPacksAlert.this.fromY != null) {
                    float lerp = AndroidUtilities.lerp(EmojiPacksAlert.this.fromY.floatValue(), ((BottomSheet) EmojiPacksAlert.this).containerView.getY() + f2, EmojiPacksAlert.this.loadT) - ((BottomSheet) EmojiPacksAlert.this).containerView.getY();
                    f = lerp - f2;
                    f2 = lerp;
                } else {
                    f = 0.0f;
                }
                float clamp = 1.0f - MathUtils.clamp((f2 - ((BottomSheet) EmojiPacksAlert.this).containerView.getPaddingTop()) / AndroidUtilities.dp(32.0f), 0.0f, 1.0f);
                float paddingTop = f2 - (((BottomSheet) EmojiPacksAlert.this).containerView.getPaddingTop() * clamp);
                float dp = AndroidUtilities.dp((1.0f - clamp) * 14.0f);
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(getPaddingLeft(), paddingTop, getWidth() - getPaddingRight(), getBottom() + dp);
                this.path.addRoundRect(rectF, dp, dp, Path.Direction.CW);
                canvas.drawPath(this.path, this.paint);
                boolean z = clamp > 0.75f;
                Boolean bool = this.lastOpen;
                if (bool == null || z != bool.booleanValue()) {
                    EmojiPacksAlert emojiPacksAlert2 = EmojiPacksAlert.this;
                    Boolean valueOf = Boolean.valueOf(z);
                    this.lastOpen = valueOf;
                    emojiPacksAlert2.updateLightStatusBar(valueOf.booleanValue());
                }
                Theme.dialogs_onlineCirclePaint.setColor(EmojiPacksAlert.this.getThemedColor("key_sheet_scrollUp"));
                Theme.dialogs_onlineCirclePaint.setAlpha((int) (MathUtils.clamp(paddingTop / AndroidUtilities.dp(20.0f), 0.0f, 1.0f) * Theme.dialogs_onlineCirclePaint.getAlpha()));
                int dp2 = AndroidUtilities.dp(36.0f);
                float dp3 = paddingTop + AndroidUtilities.dp(10.0f);
                rectF.set((getMeasuredWidth() - dp2) / 2, dp3, (getMeasuredWidth() + dp2) / 2, AndroidUtilities.dp(4.0f) + dp3);
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                EmojiPacksAlert.this.shadowView.setVisibility((EmojiPacksAlert.this.listView.canScrollVertically(1) || EmojiPacksAlert.this.removeButtonView.getVisibility() == 0) ? 0 : 4);
                if (EmojiPacksAlert.this.listView != null) {
                    canvas.save();
                    canvas.translate(EmojiPacksAlert.this.listView.getLeft(), EmojiPacksAlert.this.listView.getTop() + f);
                    canvas.clipRect(0, 0, EmojiPacksAlert.this.listView.getWidth(), EmojiPacksAlert.this.listView.getHeight());
                    canvas.saveLayerAlpha(0.0f, 0.0f, EmojiPacksAlert.this.listView.getWidth(), EmojiPacksAlert.this.listView.getHeight(), (int) (EmojiPacksAlert.this.listView.getAlpha() * 255.0f), 31);
                    for (int i = 0; i < this.viewsGroupedByLines.size(); i++) {
                        ArrayList<EmojiImageView> valueAt = this.viewsGroupedByLines.valueAt(i);
                        valueAt.clear();
                        this.unusedArrays.add(valueAt);
                    }
                    this.viewsGroupedByLines.clear();
                    for (int i2 = 0; i2 < EmojiPacksAlert.this.listView.getChildCount(); i2++) {
                        View childAt = EmojiPacksAlert.this.listView.getChildAt(i2);
                        if (childAt instanceof EmojiImageView) {
                            EmojiImageView emojiImageView = (EmojiImageView) childAt;
                            emojiImageView.updatePressedProgress();
                            if (EmojiPacksAlert.this.animatedEmojiDrawables == null) {
                                EmojiPacksAlert.this.animatedEmojiDrawables = new LongSparseArray();
                            }
                            AnimatedEmojiSpan animatedEmojiSpan = emojiImageView.span;
                            if (animatedEmojiSpan != null) {
                                long documentId = animatedEmojiSpan.getDocumentId();
                                AnimatedEmojiDrawable animatedEmojiDrawable = (AnimatedEmojiDrawable) EmojiPacksAlert.this.animatedEmojiDrawables.get(documentId);
                                if (animatedEmojiDrawable == null) {
                                    LongSparseArray longSparseArray = EmojiPacksAlert.this.animatedEmojiDrawables;
                                    AnimatedEmojiDrawable make = AnimatedEmojiDrawable.make(((BottomSheet) EmojiPacksAlert.this).currentAccount, 3, documentId);
                                    longSparseArray.put(documentId, make);
                                    animatedEmojiDrawable = make;
                                }
                                animatedEmojiDrawable.setColorFilter(porterDuffColorFilter);
                                animatedEmojiDrawable.addView(this);
                                ArrayList<EmojiImageView> arrayList2 = this.viewsGroupedByLines.get(childAt.getTop());
                                if (arrayList2 == null) {
                                    if (!this.unusedArrays.isEmpty()) {
                                        ArrayList<ArrayList<EmojiImageView>> arrayList3 = this.unusedArrays;
                                        arrayList2 = arrayList3.remove(arrayList3.size() - 1);
                                    } else {
                                        arrayList2 = new ArrayList<>();
                                    }
                                    this.viewsGroupedByLines.put(childAt.getTop(), arrayList2);
                                }
                                arrayList2.add((EmojiImageView) childAt);
                            }
                        } else {
                            canvas.save();
                            canvas.translate(childAt.getLeft(), childAt.getTop());
                            childAt.draw(canvas);
                            canvas.restore();
                        }
                    }
                    this.lineDrawablesTmp.clear();
                    this.lineDrawablesTmp.addAll(this.lineDrawables);
                    this.lineDrawables.clear();
                    long currentTimeMillis = System.currentTimeMillis();
                    int i3 = 0;
                    while (true) {
                        DrawingInBackgroundLine drawingInBackgroundLine = null;
                        if (i3 >= this.viewsGroupedByLines.size()) {
                            break;
                        }
                        ArrayList<EmojiImageView> valueAt2 = this.viewsGroupedByLines.valueAt(i3);
                        EmojiImageView emojiImageView2 = valueAt2.get(0);
                        int childAdapterPosition = EmojiPacksAlert.this.listView.getChildAdapterPosition(emojiImageView2);
                        int i4 = 0;
                        while (true) {
                            if (i4 >= this.lineDrawablesTmp.size()) {
                                break;
                            } else if (this.lineDrawablesTmp.get(i4).position == childAdapterPosition) {
                                drawingInBackgroundLine = this.lineDrawablesTmp.get(i4);
                                this.lineDrawablesTmp.remove(i4);
                                break;
                            } else {
                                i4++;
                            }
                        }
                        if (drawingInBackgroundLine == null) {
                            if (!this.unusedLineDrawables.isEmpty()) {
                                ArrayList<DrawingInBackgroundLine> arrayList4 = this.unusedLineDrawables;
                                drawingInBackgroundLine = arrayList4.remove(arrayList4.size() - 1);
                            } else {
                                drawingInBackgroundLine = new DrawingInBackgroundLine();
                                drawingInBackgroundLine.currentLayerNum = 7;
                            }
                            drawingInBackgroundLine.position = childAdapterPosition;
                            drawingInBackgroundLine.onAttachToWindow();
                        }
                        this.lineDrawables.add(drawingInBackgroundLine);
                        drawingInBackgroundLine.imageViewEmojis = valueAt2;
                        canvas.save();
                        canvas.translate(0.0f, emojiImageView2.getY() + emojiImageView2.getPaddingTop());
                        drawingInBackgroundLine.draw(canvas, currentTimeMillis, getMeasuredWidth(), emojiImageView2.getMeasuredHeight() - emojiImageView2.getPaddingBottom(), 1.0f);
                        canvas.restore();
                        i3++;
                    }
                    for (int i5 = 0; i5 < this.lineDrawablesTmp.size(); i5++) {
                        if (this.unusedLineDrawables.size() < 3) {
                            this.unusedLineDrawables.add(this.lineDrawablesTmp.get(i5));
                            this.lineDrawablesTmp.get(i5).imageViewEmojis = null;
                            this.lineDrawablesTmp.get(i5).reset();
                        } else {
                            this.lineDrawablesTmp.get(i5).onDetachFromWindow();
                        }
                    }
                    this.lineDrawablesTmp.clear();
                    canvas.restore();
                    canvas.restore();
                    if (EmojiPacksAlert.this.listView.getAlpha() < 1.0f) {
                        int width = getWidth() / 2;
                        int height = (((int) dp3) + getHeight()) / 2;
                        int dp4 = AndroidUtilities.dp(16.0f);
                        EmojiPacksAlert.this.progressDrawable.setAlpha((int) ((1.0f - EmojiPacksAlert.this.listView.getAlpha()) * 255.0f));
                        EmojiPacksAlert.this.progressDrawable.setBounds(width - dp4, height - dp4, width + dp4, height + dp4);
                        EmojiPacksAlert.this.progressDrawable.draw(canvas);
                        invalidate();
                    }
                }
                super.dispatchDraw(canvas);
            }

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && motionEvent.getY() < EmojiPacksAlert.this.getListTop() - AndroidUtilities.dp(6.0f)) {
                    EmojiPacksAlert.this.dismiss();
                }
                return super.dispatchTouchEvent(motionEvent);
            }

            /* renamed from: org.telegram.ui.Components.EmojiPacksAlert$2$DrawingInBackgroundLine */
            /* loaded from: classes3.dex */
            class DrawingInBackgroundLine extends DrawingInBackgroundThreadDrawable {
                ArrayList<EmojiImageView> drawInBackgroundViews = new ArrayList<>();
                ArrayList<EmojiImageView> imageViewEmojis;
                public int position;

                DrawingInBackgroundLine() {
                }

                @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                public void prepareDraw(long j) {
                    AnimatedEmojiDrawable animatedEmojiDrawable;
                    this.drawInBackgroundViews.clear();
                    for (int i = 0; i < this.imageViewEmojis.size(); i++) {
                        EmojiImageView emojiImageView = this.imageViewEmojis.get(i);
                        if (emojiImageView.span != null && (animatedEmojiDrawable = (AnimatedEmojiDrawable) EmojiPacksAlert.this.animatedEmojiDrawables.get(emojiImageView.span.getDocumentId())) != null && animatedEmojiDrawable.getImageReceiver() != null) {
                            animatedEmojiDrawable.update(j);
                            ImageReceiver.BackgroundThreadDrawHolder drawInBackgroundThread = animatedEmojiDrawable.getImageReceiver().setDrawInBackgroundThread(emojiImageView.backgroundThreadDrawHolder);
                            emojiImageView.backgroundThreadDrawHolder = drawInBackgroundThread;
                            drawInBackgroundThread.time = j;
                            animatedEmojiDrawable.setAlpha(255);
                            android.graphics.Rect rect = AndroidUtilities.rectTmp2;
                            rect.set(emojiImageView.getLeft() + emojiImageView.getPaddingLeft(), emojiImageView.getPaddingTop(), emojiImageView.getRight() - emojiImageView.getPaddingRight(), emojiImageView.getMeasuredHeight() - emojiImageView.getPaddingBottom());
                            emojiImageView.backgroundThreadDrawHolder.setBounds(rect);
                            emojiImageView.imageReceiver = animatedEmojiDrawable.getImageReceiver();
                            this.drawInBackgroundViews.add(emojiImageView);
                        }
                    }
                }

                /* JADX WARN: Code restructure failed: missing block: B:29:0x0058, code lost:
                    prepareDraw(java.lang.System.currentTimeMillis());
                    drawInUiThread(r7, r12);
                    reset();
                 */
                /* JADX WARN: Code restructure failed: missing block: B:38:?, code lost:
                    return;
                 */
                @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct add '--show-bad-code' argument
                */
                public void draw(android.graphics.Canvas r7, long r8, int r10, int r11, float r12) {
                    /*
                        r6 = this;
                        java.util.ArrayList<org.telegram.ui.Components.EmojiPacksAlert$EmojiImageView> r0 = r6.imageViewEmojis
                        if (r0 != 0) goto L5
                        return
                    L5:
                        int r0 = r0.size()
                        r1 = 3
                        r2 = 0
                        r3 = 1
                        if (r0 <= r1) goto L17
                        int r0 = org.telegram.messenger.SharedConfig.getDevicePerformanceClass()
                        if (r0 != 0) goto L15
                        goto L17
                    L15:
                        r0 = 0
                        goto L18
                    L17:
                        r0 = 1
                    L18:
                        if (r0 != 0) goto L55
                    L1a:
                        java.util.ArrayList<org.telegram.ui.Components.EmojiPacksAlert$EmojiImageView> r1 = r6.imageViewEmojis
                        int r1 = r1.size()
                        if (r2 >= r1) goto L55
                        java.util.ArrayList<org.telegram.ui.Components.EmojiPacksAlert$EmojiImageView> r1 = r6.imageViewEmojis
                        java.lang.Object r1 = r1.get(r2)
                        org.telegram.ui.Components.EmojiPacksAlert$EmojiImageView r1 = (org.telegram.ui.Components.EmojiPacksAlert.EmojiImageView) r1
                        float r4 = org.telegram.ui.Components.EmojiPacksAlert.EmojiImageView.access$1800(r1)
                        r5 = 0
                        int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
                        if (r4 != 0) goto L56
                        android.animation.ValueAnimator r4 = r1.backAnimator
                        if (r4 != 0) goto L56
                        float r4 = r1.getTranslationX()
                        int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
                        if (r4 != 0) goto L56
                        float r4 = r1.getTranslationY()
                        int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
                        if (r4 != 0) goto L56
                        float r1 = r1.getAlpha()
                        r4 = 1065353216(0x3var_, float:1.0)
                        int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                        if (r1 == 0) goto L52
                        goto L56
                    L52:
                        int r2 = r2 + 1
                        goto L1a
                    L55:
                        r3 = r0
                    L56:
                        if (r3 == 0) goto L66
                        long r8 = java.lang.System.currentTimeMillis()
                        r6.prepareDraw(r8)
                        r6.drawInUiThread(r7, r12)
                        r6.reset()
                        goto L69
                    L66:
                        super.draw(r7, r8, r10, r11, r12)
                    L69:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiPacksAlert.AnonymousClass2.DrawingInBackgroundLine.draw(android.graphics.Canvas, long, int, int, float):void");
                }

                @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                public void drawInBackground(Canvas canvas) {
                    for (int i = 0; i < this.drawInBackgroundViews.size(); i++) {
                        EmojiImageView emojiImageView = this.drawInBackgroundViews.get(i);
                        emojiImageView.imageReceiver.draw(canvas, emojiImageView.backgroundThreadDrawHolder);
                    }
                }

                @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                protected void drawInUiThread(Canvas canvas, float f) {
                    AnimatedEmojiDrawable animatedEmojiDrawable;
                    if (this.imageViewEmojis != null) {
                        for (int i = 0; i < this.imageViewEmojis.size(); i++) {
                            EmojiImageView emojiImageView = this.imageViewEmojis.get(i);
                            if (emojiImageView.span != null && (animatedEmojiDrawable = (AnimatedEmojiDrawable) EmojiPacksAlert.this.animatedEmojiDrawables.get(emojiImageView.span.getDocumentId())) != null && animatedEmojiDrawable.getImageReceiver() != null && emojiImageView.imageReceiver != null) {
                                animatedEmojiDrawable.setAlpha((int) (255.0f * f * emojiImageView.getAlpha()));
                                float width = ((emojiImageView.getWidth() - emojiImageView.getPaddingLeft()) - emojiImageView.getPaddingRight()) / 2.0f;
                                float height = ((emojiImageView.getHeight() - emojiImageView.getPaddingTop()) - emojiImageView.getPaddingBottom()) / 2.0f;
                                float left = (emojiImageView.getLeft() + emojiImageView.getRight()) / 2.0f;
                                float paddingTop = emojiImageView.getPaddingTop() + height;
                                float f2 = 1.0f;
                                if (emojiImageView.pressedProgress != 0.0f) {
                                    f2 = 1.0f * (((1.0f - emojiImageView.pressedProgress) * 0.2f) + 0.8f);
                                }
                                animatedEmojiDrawable.setBounds((int) (left - ((emojiImageView.getScaleX() * width) * f2)), (int) (paddingTop - ((emojiImageView.getScaleY() * height) * f2)), (int) (left + (width * emojiImageView.getScaleX() * f2)), (int) (paddingTop + (height * emojiImageView.getScaleY() * f2)));
                                animatedEmojiDrawable.draw(canvas, false);
                            }
                        }
                    }
                }

                @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                public void onFrameReady() {
                    super.onFrameReady();
                    for (int i = 0; i < this.drawInBackgroundViews.size(); i++) {
                        this.drawInBackgroundViews.get(i).backgroundThreadDrawHolder.release();
                    }
                    ((BottomSheet) EmojiPacksAlert.this).containerView.invalidate();
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                this.attached = true;
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                this.attached = false;
                for (int i = 0; i < this.lineDrawables.size(); i++) {
                    this.lineDrawables.get(i).onDetachFromWindow();
                }
                for (int i2 = 0; i2 < this.unusedLineDrawables.size(); i2++) {
                    this.unusedLineDrawables.get(i2).onDetachFromWindow();
                }
                this.lineDrawables.clear();
            }
        };
        this.paddingView = new View(this, context) { // from class: org.telegram.ui.Components.EmojiPacksAlert.3
            @Override // android.view.View
            protected void onMeasure(int i, int i2) {
                android.graphics.Point point = AndroidUtilities.displaySize;
                int i3 = point.x;
                int i4 = point.y;
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec((int) (i4 * (i3 < i4 ? 0.56f : 0.3f)), NUM));
            }
        };
        this.listView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.EmojiPacksAlert.4
            @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View view, long j) {
                return false;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public void onMeasure(int i, int i2) {
                EmojiPacksAlert.this.gridLayoutManager.setSpanCount(Math.max(1, View.MeasureSpec.getSize(i) / AndroidUtilities.dp(AndroidUtilities.isTablet() ? 60.0f : 45.0f)));
                super.onMeasure(i, i2);
            }

            @Override // androidx.recyclerview.widget.RecyclerView
            public void onScrolled(int i, int i2) {
                super.onScrolled(i, i2);
                ((BottomSheet) EmojiPacksAlert.this).containerView.invalidate();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                AnimatedEmojiSpan.release(((BottomSheet) EmojiPacksAlert.this).containerView, EmojiPacksAlert.this.animatedEmojiDrawables);
            }
        };
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, AndroidUtilities.statusBarHeight, i, 0);
        this.containerView.setClipChildren(false);
        this.containerView.setClipToPadding(false);
        this.containerView.setWillNotDraw(false);
        this.listView.setSelectorRadius(AndroidUtilities.dp(6.0f));
        this.listView.setSelectorDrawableColor(Theme.getColor("listSelectorSDK21", resourcesProvider));
        this.listView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(68.0f));
        this.listView.setAdapter(new Adapter());
        RecyclerListView recyclerListView = this.listView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 8);
        this.gridLayoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.listView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.Components.EmojiPacksAlert.5
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                if (view instanceof SeparatorView) {
                    rect.left = -EmojiPacksAlert.this.listView.getPaddingLeft();
                    rect.right = -EmojiPacksAlert.this.listView.getPaddingRight();
                } else if (EmojiPacksAlert.this.listView.getChildAdapterPosition(view) != 1) {
                } else {
                    rect.top = AndroidUtilities.dp(14.0f);
                }
            }
        });
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda9
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                EmojiPacksAlert.this.lambda$new$0(arrayList, baseFragment, resourcesProvider, view, i2);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda10
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i2) {
                boolean lambda$new$2;
                lambda$new$2 = EmojiPacksAlert.this.lambda$new$2(context, view, i2);
                return lambda$new$2;
            }
        });
        this.gridLayoutManager.setReverseLayout(false);
        this.gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.EmojiPacksAlert.7
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int i2) {
                if (EmojiPacksAlert.this.listView.getAdapter() == null || EmojiPacksAlert.this.listView.getAdapter().getItemViewType(i2) == 1) {
                    return 1;
                }
                return EmojiPacksAlert.this.gridLayoutManager.getSpanCount();
            }
        });
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        View view = new View(context);
        this.shadowView = view;
        view.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.containerView.addView(this.shadowView, LayoutHelper.createFrame(-1.0f, 1.0f / AndroidUtilities.density, 80));
        this.shadowView.setTranslationY(-AndroidUtilities.dp(68.0f));
        FrameLayout frameLayout = new FrameLayout(context);
        this.buttonsView = frameLayout;
        frameLayout.setBackgroundColor(getThemedColor("dialogBackground"));
        this.containerView.addView(this.buttonsView, LayoutHelper.createFrame(-1, 68, 87));
        TextView textView = new TextView(context);
        this.addButtonView = textView;
        textView.setVisibility(8);
        this.addButtonView.setBackground(Theme.AdaptiveRipple.filledRect(getThemedColor("featuredStickers_addButton"), 6.0f));
        this.addButtonView.setTextColor(getThemedColor("featuredStickers_buttonText"));
        this.addButtonView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.addButtonView.setGravity(17);
        this.buttonsView.addView(this.addButtonView, LayoutHelper.createFrame(-1, 48.0f, 80, 12.0f, 10.0f, 12.0f, 10.0f));
        TextView textView2 = new TextView(context);
        this.removeButtonView = textView2;
        textView2.setVisibility(8);
        this.removeButtonView.setBackground(Theme.createRadSelectorDrawable(NUM & getThemedColor("dialogTextRed"), 0, 0));
        this.removeButtonView.setTextColor(getThemedColor("dialogTextRed"));
        this.removeButtonView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.removeButtonView.setGravity(17);
        this.removeButtonView.setClickable(true);
        this.buttonsView.addView(this.removeButtonView, LayoutHelper.createFrame(-1, -1.0f, 80, 0.0f, 0.0f, 0.0f, 19.0f));
        PremiumButtonView premiumButtonView = new PremiumButtonView(context, false);
        this.premiumButtonView = premiumButtonView;
        premiumButtonView.setButton(LocaleController.getString("UnlockPremiumEmoji", R.string.UnlockPremiumEmoji), new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                EmojiPacksAlert.this.lambda$new$3(view2);
            }
        });
        this.premiumButtonView.setIcon(R.raw.unlock_icon);
        this.premiumButtonView.buttonLayout.setClickable(true);
        this.buttonsView.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 80, 12.0f, 10.0f, 12.0f, 10.0f));
        updateButton();
        MediaDataController.getInstance(baseFragment.getCurrentAccount()).checkStickers(5);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ArrayList arrayList, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider, View view, int i) {
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = null;
        int i2 = 0;
        if (arrayList == null || arrayList.size() <= 1) {
            ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
                this.popupWindow = null;
            } else if (!(baseFragment instanceof ChatActivity) || ((ChatActivity) baseFragment).getChatActivityEnterView().getVisibility() != 0 || !(view instanceof EmojiImageView)) {
            } else {
                AnimatedEmojiSpan animatedEmojiSpan = ((EmojiImageView) view).span;
                try {
                    TLRPC$Document tLRPC$Document = animatedEmojiSpan.document;
                    if (tLRPC$Document == null) {
                        tLRPC$Document = AnimatedEmojiDrawable.findDocument(this.currentAccount, animatedEmojiSpan.getDocumentId());
                    }
                    SpannableString spannableString = new SpannableString(MessageObject.findAnimatedEmojiEmoticon(tLRPC$Document));
                    spannableString.setSpan(animatedEmojiSpan, 0, spannableString.length(), 33);
                    ((ChatActivity) baseFragment).getChatActivityEnterView().messageEditText.getText().append((CharSequence) spannableString);
                    ((ChatActivity) baseFragment).showEmojiHint();
                    onCloseByLink();
                    dismiss();
                } catch (Exception unused) {
                }
                try {
                    view.performHapticFeedback(3, 1);
                } catch (Exception unused2) {
                }
            }
        } else if (SystemClock.elapsedRealtime() - this.premiumButtonClicked < 250) {
        } else {
            int i3 = 0;
            while (true) {
                ArrayList<EmojiView.CustomEmoji>[] arrayListArr = this.customEmojiPacks.data;
                if (i2 >= arrayListArr.length) {
                    break;
                }
                int size = arrayListArr[i2].size();
                if (this.customEmojiPacks.data.length > 1) {
                    size = Math.min(this.gridLayoutManager.getSpanCount() * 2, size);
                }
                i3 += size + 1 + 1;
                if (i < i3) {
                    break;
                }
                i2++;
            }
            ArrayList<TLRPC$TL_messages_stickerSet> arrayList2 = this.customEmojiPacks.stickerSets;
            if (arrayList2 != null && i2 < arrayList2.size()) {
                tLRPC$TL_messages_stickerSet = this.customEmojiPacks.stickerSets.get(i2);
            }
            if (tLRPC$TL_messages_stickerSet == null || tLRPC$TL_messages_stickerSet.set == null) {
                return;
            }
            ArrayList arrayList3 = new ArrayList();
            TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
            TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
            tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
            tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
            arrayList3.add(tLRPC$TL_inputStickerSetID);
            new EmojiPacksAlert(baseFragment, getContext(), resourcesProvider, arrayList3) { // from class: org.telegram.ui.Components.EmojiPacksAlert.6
                @Override // org.telegram.ui.Components.EmojiPacksAlert
                protected void onCloseByLink() {
                    EmojiPacksAlert.this.dismiss();
                }
            }.show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$2(Context context, View view, int i) {
        final AnimatedEmojiSpan animatedEmojiSpan;
        if (!(view instanceof EmojiImageView) || (animatedEmojiSpan = ((EmojiImageView) view).span) == null) {
            return false;
        }
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), true, true);
        actionBarMenuSubItem.setItemHeight(48);
        actionBarMenuSubItem.setPadding(AndroidUtilities.dp(26.0f), 0, AndroidUtilities.dp(26.0f), 0);
        actionBarMenuSubItem.setText(LocaleController.getString("Copy", R.string.Copy));
        actionBarMenuSubItem.getTextView().setTextSize(1, 14.4f);
        actionBarMenuSubItem.getTextView().setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                EmojiPacksAlert.this.lambda$new$1(animatedEmojiSpan, view2);
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        Drawable mutate = ContextCompat.getDrawable(getContext(), R.drawable.popup_fixed_alert).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor("actionBarDefaultSubmenuBackground"), PorterDuff.Mode.MULTIPLY));
        linearLayout.setBackground(mutate);
        linearLayout.addView(actionBarMenuSubItem);
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(linearLayout, -2, -2);
        this.popupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setClippingEnabled(true);
        this.popupWindow.setLayoutInScreen(true);
        this.popupWindow.setInputMethodMode(2);
        this.popupWindow.setSoftInputMode(0);
        this.popupWindow.setOutsideTouchable(true);
        this.popupWindow.setAnimationStyle(R.style.PopupAnimation);
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        this.popupWindow.showAtLocation(view, 51, (iArr[0] - AndroidUtilities.dp(49.0f)) + (view.getMeasuredWidth() / 2), iArr[1] - AndroidUtilities.dp(52.0f));
        try {
            view.performHapticFeedback(0, 1);
        } catch (Exception unused) {
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(AnimatedEmojiSpan animatedEmojiSpan, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null) {
            return;
        }
        actionBarPopupWindow.dismiss();
        this.popupWindow = null;
        SpannableString spannableString = new SpannableString(MessageObject.findAnimatedEmojiEmoticon(AnimatedEmojiDrawable.findDocument(this.currentAccount, animatedEmojiSpan.getDocumentId())));
        spannableString.setSpan(animatedEmojiSpan, 0, spannableString.length(), 33);
        if (!AndroidUtilities.addToClipboard(spannableString)) {
            return;
        }
        BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider).createCopyBulletin(LocaleController.getString("EmojiCopied", R.string.EmojiCopied)).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view) {
        showPremiumAlert();
    }

    private void updateShowButton(boolean z) {
        int i = 0;
        boolean z2 = !this.shown && z;
        if (this.removeButtonView.getVisibility() == 0) {
            i = AndroidUtilities.dp(19.0f);
        }
        float f = i;
        float f2 = 1.0f;
        float f3 = 0.0f;
        if (z2) {
            ViewPropertyAnimator duration = this.buttonsView.animate().translationY(z ? f : AndroidUtilities.dp(16.0f)).alpha(z ? 1.0f : 0.0f).setDuration(250L);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            duration.setInterpolator(cubicBezierInterpolator).start();
            ViewPropertyAnimator translationY = this.shadowView.animate().translationY(z ? -(AndroidUtilities.dp(68.0f) - f) : 0.0f);
            if (!z) {
                f2 = 0.0f;
            }
            translationY.alpha(f2).setDuration(250L).setInterpolator(cubicBezierInterpolator).start();
            ViewPropertyAnimator animate = this.listView.animate();
            if (!z) {
                f3 = AndroidUtilities.dp(68.0f) - f;
            }
            animate.translationY(f3).setDuration(250L).setInterpolator(cubicBezierInterpolator).start();
        } else {
            this.buttonsView.setAlpha(z ? 1.0f : 0.0f);
            this.buttonsView.setTranslationY(z ? f : AndroidUtilities.dp(16.0f));
            View view = this.shadowView;
            if (!z) {
                f2 = 0.0f;
            }
            view.setAlpha(f2);
            this.shadowView.setTranslationY(z ? -(AndroidUtilities.dp(68.0f) - f) : 0.0f);
            RecyclerListView recyclerListView = this.listView;
            if (!z) {
                f3 = AndroidUtilities.dp(68.0f) - f;
            }
            recyclerListView.setTranslationY(f3);
        }
        this.shown = z;
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoad) {
            updateInstallment();
        }
    }

    public void showPremiumAlert() {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null) {
            new PremiumFeatureBottomSheet(baseFragment, 11, false).show();
        } else if (!(getContext() instanceof LaunchActivity)) {
        } else {
            ((LaunchActivity) getContext()).lambda$runLinkRequest$65(new PremiumPreviewFragment(null));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLightStatusBar(boolean z) {
        boolean z2 = true;
        boolean z3 = AndroidUtilities.computePerceivedBrightness(getThemedColor("dialogBackground")) > 0.721f;
        if (AndroidUtilities.computePerceivedBrightness(Theme.blendOver(getThemedColor("actionBarDefault"), NUM)) <= 0.721f) {
            z2 = false;
        }
        if (!z) {
            z3 = z2;
        }
        AndroidUtilities.setLightStatusBar(getWindow(), z3);
    }

    public void updateInstallment() {
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof EmojiPackHeader) {
                EmojiPackHeader emojiPackHeader = (EmojiPackHeader) childAt;
                if (emojiPackHeader.set != null && emojiPackHeader.set.set != null) {
                    emojiPackHeader.toggle(MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(emojiPackHeader.set.set.id), true);
                }
            }
        }
        updateButton();
    }

    public static void installSet(BaseFragment baseFragment, TLObject tLObject, boolean z) {
        installSet(baseFragment, tLObject, z, null, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0031 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0032  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void installSet(final org.telegram.ui.ActionBar.BaseFragment r11, org.telegram.tgnet.TLObject r12, final boolean r13, final org.telegram.messenger.Utilities.Callback<java.lang.Boolean> r14, final java.lang.Runnable r15) {
        /*
            if (r11 != 0) goto L5
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            goto L9
        L5:
            int r0 = r11.getCurrentAccount()
        L9:
            r7 = r0
            r0 = 0
            if (r11 != 0) goto Lf
            r4 = r0
            goto L14
        Lf:
            android.view.View r1 = r11.getFragmentView()
            r4 = r1
        L14:
            if (r12 != 0) goto L17
            return
        L17:
            boolean r1 = r12 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
            if (r1 == 0) goto L20
            r1 = r12
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r1
            r6 = r1
            goto L21
        L20:
            r6 = r0
        L21:
            if (r6 == 0) goto L27
            org.telegram.tgnet.TLRPC$StickerSet r12 = r6.set
        L25:
            r2 = r12
            goto L2f
        L27:
            boolean r1 = r12 instanceof org.telegram.tgnet.TLRPC$StickerSet
            if (r1 == 0) goto L2e
            org.telegram.tgnet.TLRPC$StickerSet r12 = (org.telegram.tgnet.TLRPC$StickerSet) r12
            goto L25
        L2e:
            r2 = r0
        L2f:
            if (r2 != 0) goto L32
            return
        L32:
            org.telegram.messenger.MediaDataController r12 = org.telegram.messenger.MediaDataController.getInstance(r7)
            long r0 = r2.id
            boolean r12 = r12.cancelRemovingStickerSet(r0)
            if (r12 == 0) goto L46
            if (r14 == 0) goto L45
            java.lang.Boolean r11 = java.lang.Boolean.TRUE
            r14.run(r11)
        L45:
            return
        L46:
            org.telegram.tgnet.TLRPC$TL_messages_installStickerSet r12 = new org.telegram.tgnet.TLRPC$TL_messages_installStickerSet
            r12.<init>()
            org.telegram.tgnet.TLRPC$TL_inputStickerSetID r0 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetID
            r0.<init>()
            r12.stickerset = r0
            long r8 = r2.id
            r0.id = r8
            long r8 = r2.access_hash
            r0.access_hash = r8
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r7)
            org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda8 r10 = new org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda8
            r1 = r10
            r3 = r13
            r5 = r11
            r8 = r14
            r9 = r15
            r1.<init>()
            r0.sendRequest(r12, r10)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiPacksAlert.installSet(org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLObject, boolean, org.telegram.messenger.Utilities$Callback, java.lang.Runnable):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$installSet$6(final TLRPC$StickerSet tLRPC$StickerSet, final boolean z, final View view, final BaseFragment baseFragment, final TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, final int i, final Utilities.Callback callback, final Runnable runnable, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                EmojiPacksAlert.lambda$installSet$5(TLRPC$StickerSet.this, tLRPC$TL_error, z, view, baseFragment, tLRPC$TL_messages_stickerSet, tLObject, i, callback, runnable);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ void lambda$installSet$5(TLRPC$StickerSet tLRPC$StickerSet, TLRPC$TL_error tLRPC$TL_error, boolean z, View view, BaseFragment baseFragment, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, TLObject tLObject, int i, Utilities.Callback callback, final Runnable runnable) {
        int i2;
        if (tLRPC$StickerSet.masks) {
            i2 = 1;
        } else {
            i2 = tLRPC$StickerSet.emojis ? 5 : 0;
        }
        try {
            if (tLRPC$TL_error == null) {
                if (z && view != null) {
                    Bulletin.make(baseFragment, new StickerSetBulletinLayout(baseFragment.getFragmentView().getContext(), tLRPC$TL_messages_stickerSet == 0 ? tLRPC$StickerSet : tLRPC$TL_messages_stickerSet, 2, null, baseFragment.getResourceProvider()), 1500).show();
                }
                if (tLObject instanceof TLRPC$TL_messages_stickerSetInstallResultArchive) {
                    MediaDataController.getInstance(i).processStickerSetInstallResultArchive(baseFragment, true, i2, (TLRPC$TL_messages_stickerSetInstallResultArchive) tLObject);
                }
                if (callback != null) {
                    callback.run(Boolean.TRUE);
                }
            } else if (view != null) {
                Toast.makeText(baseFragment.getFragmentView().getContext(), LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred), 0).show();
                if (callback != null) {
                    callback.run(Boolean.FALSE);
                }
            } else if (callback != null) {
                callback.run(Boolean.FALSE);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        MediaDataController.getInstance(i).loadStickers(i2, false, true, false, new Utilities.Callback() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda6
            @Override // org.telegram.messenger.Utilities.Callback
            public final void run(Object obj) {
                EmojiPacksAlert.lambda$installSet$4(runnable, (ArrayList) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$installSet$4(Runnable runnable, ArrayList arrayList) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void uninstallSet(BaseFragment baseFragment, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, boolean z, Runnable runnable) {
        if (baseFragment == null || tLRPC$TL_messages_stickerSet == null || baseFragment.getFragmentView() == null) {
            return;
        }
        MediaDataController.getInstance(baseFragment.getCurrentAccount()).toggleStickerSet(baseFragment.getFragmentView().getContext(), tLRPC$TL_messages_stickerSet, 0, baseFragment, true, z, runnable);
    }

    private void loadAnimation() {
        if (this.loadAnimator != null) {
            return;
        }
        this.loadAnimator = ValueAnimator.ofFloat(this.loadT, 1.0f);
        this.fromY = Float.valueOf(this.lastY + this.containerView.getY());
        this.loadAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                EmojiPacksAlert.this.lambda$loadAnimation$7(valueAnimator);
            }
        });
        this.loadAnimator.setDuration(250L);
        this.loadAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.loadAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAnimation$7(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.loadT = floatValue;
        this.listView.setAlpha(floatValue);
        this.addButtonView.setAlpha(this.loadT);
        this.removeButtonView.setAlpha(this.loadT);
        this.containerView.invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateButton() {
        TLRPC$StickerSet tLRPC$StickerSet;
        if (this.buttonsView == null) {
            return;
        }
        ArrayList arrayList = this.customEmojiPacks.stickerSets == null ? new ArrayList() : new ArrayList(this.customEmojiPacks.stickerSets);
        int i = 0;
        while (i < arrayList.size()) {
            if (arrayList.get(i) == null) {
                arrayList.remove(i);
                i--;
            }
            i++;
        }
        MediaDataController mediaDataController = MediaDataController.getInstance(this.currentAccount);
        final ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) arrayList.get(i2);
            if (tLRPC$TL_messages_stickerSet != null && (tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set) != null) {
                if (!mediaDataController.isStickerPackInstalled(tLRPC$StickerSet.id)) {
                    arrayList3.add(tLRPC$TL_messages_stickerSet);
                } else {
                    arrayList2.add(tLRPC$TL_messages_stickerSet);
                }
            }
        }
        boolean isPremium = UserConfig.getInstance(this.currentAccount).isPremium();
        final ArrayList arrayList4 = new ArrayList(arrayList3);
        int i3 = 0;
        while (i3 < arrayList4.size()) {
            if (MessageObject.isPremiumEmojiPack((TLRPC$TL_messages_stickerSet) arrayList4.get(i3)) && !isPremium) {
                arrayList4.remove(i3);
                i3--;
            }
            i3++;
        }
        boolean z = this.customEmojiPacks.inputStickerSets != null && arrayList.size() == this.customEmojiPacks.inputStickerSets.size();
        if (!this.loaded && z) {
            loadAnimation();
        }
        this.loaded = z;
        if (!z) {
            this.listView.setAlpha(0.0f);
        }
        if (!this.loaded) {
            this.premiumButtonView.setVisibility(8);
            this.addButtonView.setVisibility(8);
            this.removeButtonView.setVisibility(8);
            updateShowButton(false);
        } else if ((arrayList4.size() <= 0 && arrayList3.size() >= 0 && !isPremium) || !this.loaded) {
            this.premiumButtonView.setVisibility(0);
            this.addButtonView.setVisibility(8);
            this.removeButtonView.setVisibility(8);
            updateShowButton(true);
        } else {
            this.premiumButtonView.setVisibility(4);
            if (arrayList4.size() > 0) {
                this.addButtonView.setVisibility(0);
                this.removeButtonView.setVisibility(8);
                if (arrayList4.size() == 1) {
                    this.addButtonView.setText(LocaleController.formatString("AddStickersCount", R.string.AddStickersCount, LocaleController.formatPluralString("EmojiCountButton", ((TLRPC$TL_messages_stickerSet) arrayList4.get(0)).documents.size(), new Object[0])));
                } else {
                    this.addButtonView.setText(LocaleController.formatString("AddStickersCount", R.string.AddStickersCount, LocaleController.formatPluralString("EmojiPackCount", arrayList4.size(), new Object[0])));
                }
                this.addButtonView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda3
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        EmojiPacksAlert.this.lambda$updateButton$9(arrayList4, view);
                    }
                });
                updateShowButton(true);
            } else if (arrayList2.size() > 0) {
                this.addButtonView.setVisibility(8);
                this.removeButtonView.setVisibility(0);
                if (arrayList2.size() == 1) {
                    this.removeButtonView.setText(LocaleController.formatString("RemoveStickersCount", R.string.RemoveStickersCount, LocaleController.formatPluralString("EmojiCountButton", ((TLRPC$TL_messages_stickerSet) arrayList2.get(0)).documents.size(), new Object[0])));
                } else {
                    this.removeButtonView.setText(LocaleController.formatString("RemoveStickersCount", R.string.RemoveStickersCount, LocaleController.formatPluralString("EmojiPackCount", arrayList2.size(), new Object[0])));
                }
                this.removeButtonView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda2
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        EmojiPacksAlert.this.lambda$updateButton$10(arrayList2, view);
                    }
                });
                updateShowButton(true);
            } else {
                this.addButtonView.setVisibility(8);
                this.removeButtonView.setVisibility(8);
                updateShowButton(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButton$9(final ArrayList arrayList, View view) {
        final int size = arrayList.size();
        final int[] iArr = new int[2];
        for (int i = 0; i < arrayList.size(); i++) {
            installSet(this.fragment, (TLObject) arrayList.get(i), size == 1, size > 1 ? new Utilities.Callback() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda7
                @Override // org.telegram.messenger.Utilities.Callback
                public final void run(Object obj) {
                    EmojiPacksAlert.this.lambda$updateButton$8(iArr, size, arrayList, (Boolean) obj);
                }
            } : null, null);
        }
        onButtonClicked(true);
        if (size <= 1) {
            dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButton$8(int[] iArr, int i, ArrayList arrayList, Boolean bool) {
        iArr[0] = iArr[0] + 1;
        if (bool.booleanValue()) {
            iArr[1] = iArr[1] + 1;
        }
        if (iArr[0] != i || iArr[1] <= 0) {
            return;
        }
        dismiss();
        Bulletin.make(this.fragment, new StickerSetBulletinLayout(this.fragment.getFragmentView().getContext(), (TLObject) arrayList.get(0), iArr[1], 2, null, this.fragment.getResourceProvider()), 1500).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateButton$10(ArrayList arrayList, View view) {
        dismiss();
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null) {
            MediaDataController.getInstance(baseFragment.getCurrentAccount()).removeMultipleStickerSets(this.fragment.getFragmentView().getContext(), this.fragment, arrayList);
        } else {
            int i = 0;
            while (i < arrayList.size()) {
                uninstallSet(this.fragment, (TLRPC$TL_messages_stickerSet) arrayList.get(i), i == 0, null);
                i++;
            }
        }
        onButtonClicked(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getListTop() {
        if (this.containerView == null) {
            return 0;
        }
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null || recyclerListView.getChildCount() < 1) {
            return this.containerView.getPaddingTop();
        }
        View childAt = this.listView.getChildAt(0);
        View view = this.paddingView;
        if (childAt != view) {
            return this.containerView.getPaddingTop();
        }
        return view.getBottom() + this.containerView.getPaddingTop();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public int getContainerViewHeight() {
        RecyclerListView recyclerListView = this.listView;
        int i = 0;
        int measuredHeight = (recyclerListView == null ? 0 : recyclerListView.getMeasuredHeight()) - getListTop();
        ViewGroup viewGroup = this.containerView;
        if (viewGroup != null) {
            i = viewGroup.getPaddingTop();
        }
        return measuredHeight + i + AndroidUtilities.navigationBarHeight + AndroidUtilities.dp(8.0f);
    }

    /* loaded from: classes3.dex */
    class SeparatorView extends View {
        public SeparatorView(EmojiPacksAlert emojiPacksAlert, Context context) {
            super(context);
            setBackgroundColor(emojiPacksAlert.getThemedColor("chat_emojiPanelShadowLine"));
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-1, AndroidUtilities.getShadowHeight());
            ((ViewGroup.MarginLayoutParams) layoutParams).topMargin = AndroidUtilities.dp(14.0f);
            setLayoutParams(layoutParams);
        }
    }

    /* loaded from: classes3.dex */
    private class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1822onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = EmojiPacksAlert.this.paddingView;
            } else {
                boolean z = true;
                if (i == 1) {
                    EmojiPacksAlert emojiPacksAlert = EmojiPacksAlert.this;
                    view = new EmojiImageView(emojiPacksAlert.getContext());
                } else if (i == 2) {
                    EmojiPacksAlert emojiPacksAlert2 = EmojiPacksAlert.this;
                    Context context = emojiPacksAlert2.getContext();
                    if (EmojiPacksAlert.this.customEmojiPacks.data.length > 1) {
                        z = false;
                    }
                    view = new EmojiPackHeader(context, z);
                } else if (i == 3) {
                    view = new TextView(EmojiPacksAlert.this.getContext());
                } else if (i == 4) {
                    EmojiPacksAlert emojiPacksAlert3 = EmojiPacksAlert.this;
                    view = new SeparatorView(emojiPacksAlert3, emojiPacksAlert3.getContext());
                } else {
                    view = null;
                }
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            EmojiView.CustomEmoji customEmoji;
            ArrayList<TLRPC$Document> arrayList;
            int i2 = i - 1;
            int itemViewType = viewHolder.getItemViewType();
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = null;
            int i3 = 0;
            boolean z = true;
            if (itemViewType == 1) {
                if (EmojiPacksAlert.this.hasDescription) {
                    i2--;
                }
                EmojiImageView emojiImageView = (EmojiImageView) viewHolder.itemView;
                int i4 = 0;
                while (true) {
                    if (i3 >= EmojiPacksAlert.this.customEmojiPacks.data.length) {
                        customEmoji = null;
                        break;
                    }
                    int size = EmojiPacksAlert.this.customEmojiPacks.data[i3].size();
                    if (EmojiPacksAlert.this.customEmojiPacks.data.length > 1) {
                        size = Math.min(EmojiPacksAlert.this.gridLayoutManager.getSpanCount() * 2, size);
                    }
                    if (i2 > i4 && i2 <= i4 + size) {
                        customEmoji = EmojiPacksAlert.this.customEmojiPacks.data[i3].get((i2 - i4) - 1);
                        break;
                    } else {
                        i4 += size + 1 + 1;
                        i3++;
                    }
                }
                AnimatedEmojiSpan animatedEmojiSpan = emojiImageView.span;
                if ((animatedEmojiSpan != null || customEmoji == null) && ((customEmoji != null || animatedEmojiSpan == null) && (customEmoji == null || animatedEmojiSpan.documentId == customEmoji.documentId))) {
                    return;
                }
                if (customEmoji == null) {
                    emojiImageView.span = null;
                    return;
                }
                TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                TLRPC$StickerSet tLRPC$StickerSet = customEmoji.stickerSet.set;
                tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
                tLRPC$TL_inputStickerSetID.short_name = tLRPC$StickerSet.short_name;
                tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
                emojiImageView.span = new AnimatedEmojiSpan(customEmoji.documentId, (Paint.FontMetricsInt) null);
            } else if (itemViewType == 2) {
                if (EmojiPacksAlert.this.hasDescription && i2 > 0) {
                    i2--;
                }
                int i5 = 0;
                int i6 = 0;
                while (i5 < EmojiPacksAlert.this.customEmojiPacks.data.length) {
                    int size2 = EmojiPacksAlert.this.customEmojiPacks.data[i5].size();
                    if (EmojiPacksAlert.this.customEmojiPacks.data.length > 1) {
                        size2 = Math.min(EmojiPacksAlert.this.gridLayoutManager.getSpanCount() * 2, size2);
                    }
                    if (i2 == i6) {
                        break;
                    }
                    i6 += size2 + 1 + 1;
                    i5++;
                }
                if (EmojiPacksAlert.this.customEmojiPacks.stickerSets != null && i5 < EmojiPacksAlert.this.customEmojiPacks.stickerSets.size()) {
                    tLRPC$TL_messages_stickerSet = EmojiPacksAlert.this.customEmojiPacks.stickerSets.get(i5);
                }
                if (tLRPC$TL_messages_stickerSet != null && tLRPC$TL_messages_stickerSet.documents != null) {
                    for (int i7 = 0; i7 < tLRPC$TL_messages_stickerSet.documents.size(); i7++) {
                        if (!MessageObject.isFreeEmoji(tLRPC$TL_messages_stickerSet.documents.get(i7))) {
                            break;
                        }
                    }
                }
                z = false;
                if (i5 >= EmojiPacksAlert.this.customEmojiPacks.data.length) {
                    return;
                }
                EmojiPackHeader emojiPackHeader = (EmojiPackHeader) viewHolder.itemView;
                if (tLRPC$TL_messages_stickerSet != null && (arrayList = tLRPC$TL_messages_stickerSet.documents) != null) {
                    i3 = arrayList.size();
                }
                emojiPackHeader.set(tLRPC$TL_messages_stickerSet, i3, z);
            } else {
                if (itemViewType != 3) {
                    return;
                }
                TextView textView = (TextView) viewHolder.itemView;
                textView.setTextSize(1, 13.0f);
                textView.setTextColor(EmojiPacksAlert.this.getThemedColor("chat_emojiPanelTrendingDescription"));
                textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("PremiumPreviewEmojiPack", R.string.PremiumPreviewEmojiPack)));
                textView.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(30.0f), AndroidUtilities.dp(14.0f));
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == 0) {
                return 0;
            }
            int i2 = i - 1;
            if (EmojiPacksAlert.this.hasDescription) {
                if (i2 == 1) {
                    return 3;
                }
                if (i2 > 0) {
                    i2--;
                }
            }
            int i3 = 0;
            for (int i4 = 0; i4 < EmojiPacksAlert.this.customEmojiPacks.data.length; i4++) {
                if (i2 == i3) {
                    return 2;
                }
                int size = EmojiPacksAlert.this.customEmojiPacks.data[i4].size();
                if (EmojiPacksAlert.this.customEmojiPacks.data.length > 1) {
                    size = Math.min(EmojiPacksAlert.this.gridLayoutManager.getSpanCount() * 2, size);
                }
                int i5 = i3 + size + 1;
                if (i2 == i5) {
                    return 4;
                }
                i3 = i5 + 1;
            }
            return 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            EmojiPacksAlert emojiPacksAlert = EmojiPacksAlert.this;
            emojiPacksAlert.hasDescription = !UserConfig.getInstance(((BottomSheet) emojiPacksAlert).currentAccount).isPremium() && EmojiPacksAlert.this.customEmojiPacks.stickerSets != null && EmojiPacksAlert.this.customEmojiPacks.stickerSets.size() == 1 && MessageObject.isPremiumEmojiPack(EmojiPacksAlert.this.customEmojiPacks.stickerSets.get(0));
            return (EmojiPacksAlert.this.hasDescription ? 1 : 0) + 1 + EmojiPacksAlert.this.customEmojiPacks.getItemsCount() + Math.max(0, EmojiPacksAlert.this.customEmojiPacks.data.length - 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSubItemClick(int i) {
        ArrayList<TLRPC$TL_messages_stickerSet> arrayList;
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet;
        String str;
        EmojiPacksLoader emojiPacksLoader = this.customEmojiPacks;
        if (emojiPacksLoader == null || (arrayList = emojiPacksLoader.stickerSets) == null || arrayList.isEmpty()) {
            return;
        }
        TLRPC$StickerSet tLRPC$StickerSet = this.customEmojiPacks.stickerSets.get(0).set;
        if (tLRPC$StickerSet != null && tLRPC$StickerSet.emojis) {
            str = "https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/addemoji/" + tLRPC$TL_messages_stickerSet.set.short_name;
        } else {
            str = "https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/addstickers/" + tLRPC$TL_messages_stickerSet.set.short_name;
        }
        String str2 = str;
        if (i != 1) {
            if (i != 2) {
                return;
            }
            try {
                AndroidUtilities.addToClipboard(str2);
                BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider).createCopyLinkBulletin().show();
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        Context context = null;
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null) {
            context = baseFragment.getParentActivity();
        }
        if (context == null) {
            context = getContext();
        }
        AnonymousClass8 anonymousClass8 = new AnonymousClass8(context, null, str2, false, str2, false, this.resourcesProvider);
        BaseFragment baseFragment2 = this.fragment;
        if (baseFragment2 != null) {
            baseFragment2.showDialog(anonymousClass8);
        } else {
            anonymousClass8.show();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.Components.EmojiPacksAlert$8  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass8 extends ShareAlert {
        AnonymousClass8(Context context, ArrayList arrayList, String str, boolean z, String str2, boolean z2, Theme.ResourcesProvider resourcesProvider) {
            super(context, arrayList, str, z, str2, z2, resourcesProvider);
        }

        @Override // org.telegram.ui.Components.ShareAlert
        protected void onSend(final androidx.collection.LongSparseArray<TLRPC$Dialog> longSparseArray, final int i, TLRPC$TL_forumTopic tLRPC$TL_forumTopic) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiPacksAlert$8$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiPacksAlert.AnonymousClass8.this.lambda$onSend$0(longSparseArray, i);
                }
            }, 100L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSend$0(androidx.collection.LongSparseArray longSparseArray, int i) {
            UndoView undoView = EmojiPacksAlert.this.fragment instanceof ChatActivity ? ((ChatActivity) EmojiPacksAlert.this.fragment).getUndoView() : EmojiPacksAlert.this.fragment instanceof ProfileActivity ? ((ProfileActivity) EmojiPacksAlert.this.fragment).getUndoView() : null;
            if (undoView != null) {
                if (longSparseArray.size() == 1) {
                    undoView.showWithAction(((TLRPC$Dialog) longSparseArray.valueAt(0)).id, 53, Integer.valueOf(i));
                } else {
                    undoView.showWithAction(0L, 53, Integer.valueOf(i), Integer.valueOf(longSparseArray.size()), (Runnable) null, (Runnable) null);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class EmojiImageView extends View {
        ValueAnimator backAnimator;
        public ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder;
        public ImageReceiver imageReceiver;
        private float pressedProgress;
        public AnimatedEmojiSpan span;

        public EmojiImageView(Context context) {
            super(context);
        }

        @Override // android.view.View
        protected void onMeasure(int i, int i2) {
            setPadding(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM));
        }

        @Override // android.view.View
        public void setPressed(boolean z) {
            ValueAnimator valueAnimator;
            if (isPressed() != z) {
                super.setPressed(z);
                invalidate();
                if (z && (valueAnimator = this.backAnimator) != null) {
                    valueAnimator.removeAllListeners();
                    this.backAnimator.cancel();
                }
                if (z) {
                    return;
                }
                float f = this.pressedProgress;
                if (f == 0.0f) {
                    return;
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(f, 0.0f);
                this.backAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiImageView$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        EmojiPacksAlert.EmojiImageView.this.lambda$setPressed$0(valueAnimator2);
                    }
                });
                this.backAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.EmojiPacksAlert.EmojiImageView.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        super.onAnimationEnd(animator);
                        EmojiImageView.this.backAnimator = null;
                    }
                });
                this.backAnimator.setInterpolator(new OvershootInterpolator(5.0f));
                this.backAnimator.setDuration(350L);
                this.backAnimator.start();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setPressed$0(ValueAnimator valueAnimator) {
            this.pressedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            ((BottomSheet) EmojiPacksAlert.this).containerView.invalidate();
        }

        public void updatePressedProgress() {
            if (isPressed()) {
                float f = this.pressedProgress;
                if (f == 1.0f) {
                    return;
                }
                this.pressedProgress = Utilities.clamp(f + 0.16f, 1.0f, 0.0f);
                invalidate();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class EmojiPackHeader extends FrameLayout {
        public TextView addButtonView;
        private ValueAnimator animator;
        public BaseFragment dummyFragment;
        public ActionBarMenuItem optionsButton;
        public TextView removeButtonView;
        private TLRPC$TL_messages_stickerSet set;
        private boolean single;
        public TextView subtitleView;
        public LinkSpanDrawable.LinksTextView titleView;
        private float toggleT;
        private boolean toggled;
        public PremiumButtonView unlockButtonView;

        public EmojiPackHeader(Context context, boolean z) {
            super(context);
            float f;
            float f2;
            this.dummyFragment = new BaseFragment() { // from class: org.telegram.ui.Components.EmojiPacksAlert.EmojiPackHeader.1
                @Override // org.telegram.ui.ActionBar.BaseFragment
                public int getCurrentAccount() {
                    return this.currentAccount;
                }

                @Override // org.telegram.ui.ActionBar.BaseFragment
                public View getFragmentView() {
                    return ((BottomSheet) EmojiPacksAlert.this).containerView;
                }

                @Override // org.telegram.ui.ActionBar.BaseFragment
                public FrameLayout getLayoutContainer() {
                    return (FrameLayout) ((BottomSheet) EmojiPacksAlert.this).containerView;
                }

                @Override // org.telegram.ui.ActionBar.BaseFragment
                public Theme.ResourcesProvider getResourceProvider() {
                    return ((BottomSheet) EmojiPacksAlert.this).resourcesProvider;
                }
            };
            this.toggled = false;
            this.toggleT = 0.0f;
            this.single = z;
            if (!z) {
                if (!UserConfig.getInstance(((BottomSheet) EmojiPacksAlert.this).currentAccount).isPremium()) {
                    PremiumButtonView premiumButtonView = new PremiumButtonView(context, AndroidUtilities.dp(4.0f), false);
                    this.unlockButtonView = premiumButtonView;
                    premiumButtonView.setButton(LocaleController.getString("Unlock", R.string.Unlock), new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda2
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            EmojiPacksAlert.EmojiPackHeader.this.lambda$new$0(view);
                        }
                    });
                    this.unlockButtonView.setIcon(R.raw.unlock_icon);
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.unlockButtonView.getIconView().getLayoutParams();
                    marginLayoutParams.leftMargin = AndroidUtilities.dp(1.0f);
                    marginLayoutParams.topMargin = AndroidUtilities.dp(1.0f);
                    int dp = AndroidUtilities.dp(20.0f);
                    marginLayoutParams.height = dp;
                    marginLayoutParams.width = dp;
                    ((ViewGroup.MarginLayoutParams) this.unlockButtonView.getTextView().getLayoutParams()).leftMargin = AndroidUtilities.dp(3.0f);
                    this.unlockButtonView.getChildAt(0).setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
                    addView(this.unlockButtonView, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 15.66f, 5.66f, 0.0f));
                    this.unlockButtonView.measure(View.MeasureSpec.makeMeasureSpec(99999, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(28.0f), NUM));
                    f2 = (this.unlockButtonView.getMeasuredWidth() + AndroidUtilities.dp(16.0f)) / AndroidUtilities.density;
                } else {
                    f2 = 8.0f;
                }
                TextView textView = new TextView(context);
                this.addButtonView = textView;
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.addButtonView.setTextColor(EmojiPacksAlert.this.getThemedColor("featuredStickers_buttonText"));
                this.addButtonView.setBackground(Theme.AdaptiveRipple.filledRect(EmojiPacksAlert.this.getThemedColor("featuredStickers_addButton"), 4.0f));
                this.addButtonView.setText(LocaleController.getString("Add", R.string.Add));
                this.addButtonView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
                this.addButtonView.setGravity(17);
                this.addButtonView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda4
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        EmojiPacksAlert.EmojiPackHeader.this.lambda$new$1(view);
                    }
                });
                addView(this.addButtonView, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 15.66f, 5.66f, 0.0f));
                this.addButtonView.measure(View.MeasureSpec.makeMeasureSpec(99999, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(28.0f), NUM));
                float max = Math.max(f2, (this.addButtonView.getMeasuredWidth() + AndroidUtilities.dp(16.0f)) / AndroidUtilities.density);
                TextView textView2 = new TextView(context);
                this.removeButtonView = textView2;
                textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.removeButtonView.setTextColor(EmojiPacksAlert.this.getThemedColor("featuredStickers_addButton"));
                this.removeButtonView.setBackground(Theme.createRadSelectorDrawable(NUM & EmojiPacksAlert.this.getThemedColor("featuredStickers_addButton"), 4, 4));
                this.removeButtonView.setText(LocaleController.getString("StickersRemove", R.string.StickersRemove));
                this.removeButtonView.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
                this.removeButtonView.setGravity(17);
                this.removeButtonView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        EmojiPacksAlert.EmojiPackHeader.this.lambda$new$3(view);
                    }
                });
                this.removeButtonView.setClickable(false);
                addView(this.removeButtonView, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 15.66f, 5.66f, 0.0f));
                this.removeButtonView.setScaleX(0.0f);
                this.removeButtonView.setScaleY(0.0f);
                this.removeButtonView.setAlpha(0.0f);
                this.removeButtonView.measure(View.MeasureSpec.makeMeasureSpec(99999, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(28.0f), NUM));
                f = Math.max(max, (this.removeButtonView.getMeasuredWidth() + AndroidUtilities.dp(16.0f)) / AndroidUtilities.density);
            } else {
                f = 32.0f;
            }
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, ((BottomSheet) EmojiPacksAlert.this).resourcesProvider);
            this.titleView = linksTextView;
            linksTextView.setPadding(AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(2.0f), 0);
            this.titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titleView.setEllipsize(TextUtils.TruncateAt.END);
            this.titleView.setSingleLine(true);
            this.titleView.setLines(1);
            this.titleView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText", ((BottomSheet) EmojiPacksAlert.this).resourcesProvider));
            this.titleView.setTextColor(EmojiPacksAlert.this.getThemedColor("dialogTextBlack"));
            if (z) {
                this.titleView.setTextSize(1, 20.0f);
                addView(this.titleView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388659, 12.0f, 11.0f, f, 0.0f));
            } else {
                this.titleView.setTextSize(1, 17.0f);
                addView(this.titleView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388659, 6.0f, 10.0f, f, 0.0f));
            }
            if (!z) {
                TextView textView3 = new TextView(context);
                this.subtitleView = textView3;
                textView3.setTextSize(1, 13.0f);
                this.subtitleView.setTextColor(EmojiPacksAlert.this.getThemedColor("dialogTextGray2"));
                this.subtitleView.setEllipsize(TextUtils.TruncateAt.END);
                this.subtitleView.setSingleLine(true);
                this.subtitleView.setLines(1);
                addView(this.subtitleView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388659, 8.0f, 31.66f, f, 0.0f));
            }
            if (z) {
                ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, (ActionBarMenu) null, 0, EmojiPacksAlert.this.getThemedColor("key_sheet_other"), ((BottomSheet) EmojiPacksAlert.this).resourcesProvider);
                this.optionsButton = actionBarMenuItem;
                actionBarMenuItem.setLongClickEnabled(false);
                this.optionsButton.setSubMenuOpenSide(2);
                this.optionsButton.setIcon(R.drawable.ic_ab_other);
                this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(EmojiPacksAlert.this.getThemedColor("player_actionBarSelector"), 1));
                addView(this.optionsButton, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, 5.0f, 5.0f - (((BottomSheet) EmojiPacksAlert.this).backgroundPaddingLeft / AndroidUtilities.density), 0.0f));
                this.optionsButton.addSubItem(1, R.drawable.msg_share, LocaleController.getString("StickersShare", R.string.StickersShare));
                this.optionsButton.addSubItem(2, R.drawable.msg_link, LocaleController.getString("CopyLink", R.string.CopyLink));
                this.optionsButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda3
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        EmojiPacksAlert.EmojiPackHeader.this.lambda$new$4(view);
                    }
                });
                this.optionsButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda6
                    @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate
                    public final void onItemClick(int i) {
                        EmojiPacksAlert.access$5500(EmojiPacksAlert.this, i);
                    }
                });
                this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            EmojiPacksAlert.this.premiumButtonClicked = SystemClock.elapsedRealtime();
            EmojiPacksAlert.this.showPremiumAlert();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view) {
            EmojiPacksAlert.installSet(this.dummyFragment, this.set, true);
            toggle(true, true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3(View view) {
            EmojiPacksAlert.uninstallSet(this.dummyFragment, this.set, true, new Runnable() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    EmojiPacksAlert.EmojiPackHeader.this.lambda$new$2();
                }
            });
            toggle(false, true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2() {
            toggle(true, true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4(View view) {
            this.optionsButton.toggleSubMenu();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void toggle(boolean z, boolean z2) {
            if (this.toggled == z) {
                return;
            }
            this.toggled = z;
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.animator = null;
            }
            TextView textView = this.addButtonView;
            if (textView == null || this.removeButtonView == null) {
                return;
            }
            textView.setClickable(!z);
            this.removeButtonView.setClickable(z);
            float f = 1.0f;
            if (z2) {
                float[] fArr = new float[2];
                fArr[0] = this.toggleT;
                if (!z) {
                    f = 0.0f;
                }
                fArr[1] = f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.animator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPackHeader$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        EmojiPacksAlert.EmojiPackHeader.this.lambda$toggle$6(valueAnimator2);
                    }
                });
                this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.animator.setDuration(250L);
                this.animator.start();
                return;
            }
            this.toggleT = z ? 1.0f : 0.0f;
            this.addButtonView.setScaleX(z ? 0.0f : 1.0f);
            this.addButtonView.setScaleY(z ? 0.0f : 1.0f);
            this.addButtonView.setAlpha(z ? 0.0f : 1.0f);
            this.removeButtonView.setScaleX(z ? 1.0f : 0.0f);
            this.removeButtonView.setScaleY(z ? 1.0f : 0.0f);
            TextView textView2 = this.removeButtonView;
            if (!z) {
                f = 0.0f;
            }
            textView2.setAlpha(f);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$toggle$6(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.toggleT = floatValue;
            this.addButtonView.setScaleX(1.0f - floatValue);
            this.addButtonView.setScaleY(1.0f - this.toggleT);
            this.addButtonView.setAlpha(1.0f - this.toggleT);
            this.removeButtonView.setScaleX(this.toggleT);
            this.removeButtonView.setScaleY(this.toggleT);
            this.removeButtonView.setAlpha(this.toggleT);
        }

        /* JADX WARN: Removed duplicated region for block: B:31:0x0080  */
        /* JADX WARN: Removed duplicated region for block: B:36:0x0091  */
        /* JADX WARN: Removed duplicated region for block: B:51:0x00cc  */
        /* JADX WARN: Removed duplicated region for block: B:54:0x00d3  */
        /* JADX WARN: Removed duplicated region for block: B:57:0x00da  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void set(org.telegram.tgnet.TLRPC$TL_messages_stickerSet r10, int r11, boolean r12) {
            /*
                Method dump skipped, instructions count: 250
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiPacksAlert.EmojiPackHeader.set(org.telegram.tgnet.TLRPC$TL_messages_stickerSet, int, boolean):void");
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.single ? 42.0f : 56.0f), NUM));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        @Override // android.text.method.LinkMovementMethod, android.text.method.ScrollingMovementMethod, android.text.method.BaseMovementMethod, android.text.method.MovementMethod
        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                boolean onTouchEvent = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    Selection.removeSelection(spannable);
                }
                return onTouchEvent;
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public class EmojiPacksLoader implements NotificationCenter.NotificationCenterDelegate {
        private int currentAccount;
        public ArrayList<EmojiView.CustomEmoji>[] data;
        public ArrayList<TLRPC$InputStickerSet> inputStickerSets;
        public ArrayList<TLRPC$TL_messages_stickerSet> stickerSets;

        protected void onUpdate() {
            throw null;
        }

        public EmojiPacksLoader(int i, ArrayList<TLRPC$InputStickerSet> arrayList) {
            this.currentAccount = i;
            this.inputStickerSets = arrayList == null ? new ArrayList<>() : arrayList;
            init();
        }

        private void init() {
            TLRPC$StickerSet tLRPC$StickerSet;
            this.stickerSets = new ArrayList<>(this.inputStickerSets.size());
            this.data = new ArrayList[this.inputStickerSets.size()];
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
            final boolean[] zArr = new boolean[1];
            for (int i = 0; i < this.data.length; i++) {
                TLRPC$TL_messages_stickerSet stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSet(this.inputStickerSets.get(i), false, new Runnable() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPacksLoader$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        EmojiPacksAlert.EmojiPacksLoader.this.lambda$init$1(zArr);
                    }
                });
                if (this.data.length == 1 && stickerSet != null && (tLRPC$StickerSet = stickerSet.set) != null && !tLRPC$StickerSet.emojis) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPacksLoader$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            EmojiPacksAlert.EmojiPacksLoader.this.lambda$init$2();
                        }
                    });
                    new StickersAlert(EmojiPacksAlert.this.getContext(), EmojiPacksAlert.this.fragment, this.inputStickerSets.get(i), null, EmojiPacksAlert.this.fragment instanceof ChatActivity ? ((ChatActivity) EmojiPacksAlert.this.fragment).getChatActivityEnterView() : null, ((BottomSheet) EmojiPacksAlert.this).resourcesProvider).show();
                    return;
                }
                this.stickerSets.add(stickerSet);
                putStickerSet(i, stickerSet);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$init$1(boolean[] zArr) {
            if (!zArr[0]) {
                zArr[0] = true;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPacksLoader$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EmojiPacksAlert.EmojiPacksLoader.this.lambda$init$0();
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$init$0() {
            EmojiPacksAlert.this.dismiss();
            if (EmojiPacksAlert.this.fragment == null || EmojiPacksAlert.this.fragment.getParentActivity() == null) {
                return;
            }
            BulletinFactory.of(EmojiPacksAlert.this.fragment).createErrorBulletin(LocaleController.getString("AddEmojiNotFound", R.string.AddEmojiNotFound)).show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$init$2() {
            EmojiPacksAlert.this.dismiss();
        }

        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            TLRPC$StickerSet tLRPC$StickerSet;
            if (i == NotificationCenter.groupStickersDidLoad) {
                for (int i3 = 0; i3 < this.stickerSets.size(); i3++) {
                    if (this.stickerSets.get(i3) == null) {
                        TLRPC$TL_messages_stickerSet stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSet(this.inputStickerSets.get(i3), true);
                        if (this.stickerSets.size() == 1 && stickerSet != null && (tLRPC$StickerSet = stickerSet.set) != null && !tLRPC$StickerSet.emojis) {
                            EmojiPacksAlert.this.dismiss();
                            new StickersAlert(EmojiPacksAlert.this.getContext(), EmojiPacksAlert.this.fragment, this.inputStickerSets.get(i3), null, EmojiPacksAlert.this.fragment instanceof ChatActivity ? ((ChatActivity) EmojiPacksAlert.this.fragment).getChatActivityEnterView() : null, ((BottomSheet) EmojiPacksAlert.this).resourcesProvider).show();
                            return;
                        }
                        this.stickerSets.set(i3, stickerSet);
                        if (stickerSet != null) {
                            putStickerSet(i3, stickerSet);
                        }
                    }
                }
                onUpdate();
            }
        }

        private void putStickerSet(int i, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
            if (i >= 0) {
                ArrayList<EmojiView.CustomEmoji>[] arrayListArr = this.data;
                if (i >= arrayListArr.length) {
                    return;
                }
                int i2 = 0;
                if (tLRPC$TL_messages_stickerSet == null || tLRPC$TL_messages_stickerSet.documents == null) {
                    arrayListArr[i] = new ArrayList<>(12);
                    while (i2 < 12) {
                        this.data[i].add(null);
                        i2++;
                    }
                    return;
                }
                arrayListArr[i] = new ArrayList<>();
                while (i2 < tLRPC$TL_messages_stickerSet.documents.size()) {
                    TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i2);
                    if (tLRPC$Document == null) {
                        this.data[i].add(null);
                    } else {
                        EmojiView.CustomEmoji customEmoji = new EmojiView.CustomEmoji();
                        findEmoticon(tLRPC$TL_messages_stickerSet, tLRPC$Document.id);
                        customEmoji.stickerSet = tLRPC$TL_messages_stickerSet;
                        customEmoji.documentId = tLRPC$Document.id;
                        this.data[i].add(customEmoji);
                    }
                    i2++;
                }
            }
        }

        public int getItemsCount() {
            int min;
            int i = 0;
            if (this.data == null) {
                return 0;
            }
            int i2 = 0;
            while (true) {
                ArrayList<EmojiView.CustomEmoji>[] arrayListArr = this.data;
                if (i >= arrayListArr.length) {
                    return i2;
                }
                if (arrayListArr[i] != null) {
                    if (arrayListArr.length != 1) {
                        min = Math.min(EmojiPacksAlert.this.gridLayoutManager.getSpanCount() * 2, this.data[i].size());
                    } else {
                        min = arrayListArr[i].size();
                    }
                    i2 = i2 + min + 1;
                }
                i++;
            }
        }

        public String findEmoticon(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, long j) {
            if (tLRPC$TL_messages_stickerSet == null) {
                return null;
            }
            for (int i = 0; i < tLRPC$TL_messages_stickerSet.packs.size(); i++) {
                TLRPC$TL_stickerPack tLRPC$TL_stickerPack = tLRPC$TL_messages_stickerSet.packs.get(i);
                ArrayList<Long> arrayList = tLRPC$TL_stickerPack.documents;
                if (arrayList != null && arrayList.contains(Long.valueOf(j))) {
                    return tLRPC$TL_stickerPack.emoticon;
                }
            }
            return null;
        }
    }
}
