package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getStickers;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickers;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Cells.StickerSetNameCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTabStrip;
import org.telegram.ui.ContentPreviewViewer;

public class StickerMasksAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private FrameLayout bottomTabContainer;
    /* access modifiers changed from: private */
    public ContentPreviewViewer.ContentPreviewViewerDelegate contentPreviewViewerDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() {
        public /* synthetic */ boolean can() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$can(this);
        }

        public boolean canSchedule() {
            return false;
        }

        public long getDialogId() {
            return 0;
        }

        public /* synthetic */ String getQuery(boolean z) {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$getQuery(this, z);
        }

        public /* synthetic */ void gifAddedOrDeleted() {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$gifAddedOrDeleted(this);
        }

        public boolean isInScheduleMode() {
            return false;
        }

        public boolean needMenu() {
            return false;
        }

        public /* synthetic */ boolean needOpen() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needOpen(this);
        }

        public /* synthetic */ boolean needRemove() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needRemove(this);
        }

        public boolean needSend() {
            return false;
        }

        public void openSet(TLRPC$InputStickerSet tLRPC$InputStickerSet, boolean z) {
        }

        public /* synthetic */ void remove(SendMessagesHelper.ImportingSticker importingSticker) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$remove(this, importingSticker);
        }

        public /* synthetic */ void sendGif(Object obj, Object obj2, boolean z, int i) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendGif(this, obj, obj2, z, i);
        }

        public void sendSticker(TLRPC$Document tLRPC$Document, String str, Object obj, boolean z, int i) {
            StickerMasksAlert.this.delegate.onStickerSelected(obj, tLRPC$Document);
        }
    };
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public StickerMasksAlertDelegate delegate;
    /* access modifiers changed from: private */
    public int favTabBum = -2;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$Document> favouriteStickers = new ArrayList<>();
    /* access modifiers changed from: private */
    public RecyclerListView gridView;
    /* access modifiers changed from: private */
    public String[] lastSearchKeyboardLanguage;
    private ImageView masksButton;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$Document>[] recentStickers = {new ArrayList<>(), new ArrayList<>()};
    /* access modifiers changed from: private */
    public int recentTabBum = -2;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public int searchFieldHeight;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private View shadowLine;
    private Drawable[] stickerIcons;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$TL_messages_stickerSet>[] stickerSets = {new ArrayList<>(), new ArrayList<>()};
    private ImageView stickersButton;
    /* access modifiers changed from: private */
    public StickersGridAdapter stickersGridAdapter;
    /* access modifiers changed from: private */
    public GridLayoutManager stickersLayoutManager;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    /* access modifiers changed from: private */
    public SearchField stickersSearchField;
    /* access modifiers changed from: private */
    public StickersSearchGridAdapter stickersSearchGridAdapter;
    private ScrollSlidingTabStrip stickersTab;
    /* access modifiers changed from: private */
    public int stickersTabOffset;

    public interface StickerMasksAlertDelegate {
        void onStickerSelected(Object obj, TLRPC$Document tLRPC$Document);
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    private class SearchField extends FrameLayout {
        /* access modifiers changed from: private */
        public ImageView clearSearchImageView;
        /* access modifiers changed from: private */
        public CloseProgressDrawable2 progressDrawable;
        /* access modifiers changed from: private */
        public EditTextBoldCursor searchEditText;
        /* access modifiers changed from: private */
        public AnimatorSet shadowAnimator;
        private View shadowView;
        final /* synthetic */ StickerMasksAlert this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public SearchField(org.telegram.ui.Components.StickerMasksAlert r19, android.content.Context r20, int r21) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                r2 = r20
                r3 = r21
                r0.this$0 = r1
                r0.<init>(r2)
                android.view.View r4 = new android.view.View
                r4.<init>(r2)
                r0.shadowView = r4
                r5 = 0
                r4.setAlpha(r5)
                android.view.View r4 = r0.shadowView
                r6 = 1
                java.lang.Integer r7 = java.lang.Integer.valueOf(r6)
                r4.setTag(r7)
                android.view.View r4 = r0.shadowView
                r7 = 301989888(0x12000000, float:4.0389678E-28)
                r4.setBackgroundColor(r7)
                android.view.View r4 = r0.shadowView
                android.widget.FrameLayout$LayoutParams r7 = new android.widget.FrameLayout$LayoutParams
                int r8 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
                r9 = -1
                r10 = 83
                r7.<init>(r9, r8, r10)
                r0.addView(r4, r7)
                android.view.View r4 = new android.view.View
                r4.<init>(r2)
                r7 = -14342875(0xfffffffffvar_, float:-2.1951548E38)
                r4.setBackgroundColor(r7)
                android.widget.FrameLayout$LayoutParams r7 = new android.widget.FrameLayout$LayoutParams
                int r8 = r19.searchFieldHeight
                r7.<init>(r9, r8)
                r0.addView(r4, r7)
                android.view.View r4 = new android.view.View
                r4.<init>(r2)
                r7 = 1099956224(0x41900000, float:18.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r8 = -13224394(0xfffffffffvar_, float:-2.4220097E38)
                android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r7, r8)
                r4.setBackgroundDrawable(r7)
                r10 = -1
                r11 = 1108344832(0x42100000, float:36.0)
                r12 = 51
                r13 = 1096810496(0x41600000, float:14.0)
                r14 = 1096810496(0x41600000, float:14.0)
                r15 = 1096810496(0x41600000, float:14.0)
                r16 = 0
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r0.addView(r4, r7)
                android.widget.ImageView r4 = new android.widget.ImageView
                r4.<init>(r2)
                android.widget.ImageView$ScaleType r7 = android.widget.ImageView.ScaleType.CENTER
                r4.setScaleType(r7)
                r7 = 2131166150(0x7var_c6, float:1.7946537E38)
                r4.setImageResource(r7)
                android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
                android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.MULTIPLY
                r10 = -8947849(0xfffffffffvar_, float:-3.2893961E38)
                r7.<init>(r10, r8)
                r4.setColorFilter(r7)
                r11 = 36
                r12 = 1108344832(0x42100000, float:36.0)
                r13 = 51
                r14 = 1098907648(0x41800000, float:16.0)
                r17 = 0
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
                r0.addView(r4, r7)
                android.widget.ImageView r4 = new android.widget.ImageView
                r4.<init>(r2)
                r0.clearSearchImageView = r4
                android.widget.ImageView$ScaleType r7 = android.widget.ImageView.ScaleType.CENTER
                r4.setScaleType(r7)
                android.widget.ImageView r4 = r0.clearSearchImageView
                org.telegram.ui.Components.StickerMasksAlert$SearchField$1 r7 = new org.telegram.ui.Components.StickerMasksAlert$SearchField$1
                r7.<init>(r0, r1)
                r0.progressDrawable = r7
                r4.setImageDrawable(r7)
                org.telegram.ui.Components.CloseProgressDrawable2 r4 = r0.progressDrawable
                r7 = 1088421888(0x40e00000, float:7.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r4.setSide(r7)
                android.widget.ImageView r4 = r0.clearSearchImageView
                r7 = 1036831949(0x3dcccccd, float:0.1)
                r4.setScaleX(r7)
                android.widget.ImageView r4 = r0.clearSearchImageView
                r4.setScaleY(r7)
                android.widget.ImageView r4 = r0.clearSearchImageView
                r4.setAlpha(r5)
                android.widget.ImageView r4 = r0.clearSearchImageView
                r13 = 53
                r14 = 1096810496(0x41600000, float:14.0)
                r16 = 1096810496(0x41600000, float:14.0)
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
                r0.addView(r4, r5)
                android.widget.ImageView r4 = r0.clearSearchImageView
                org.telegram.ui.Components.StickerMasksAlert$SearchField$$ExternalSyntheticLambda0 r5 = new org.telegram.ui.Components.StickerMasksAlert$SearchField$$ExternalSyntheticLambda0
                r5.<init>(r0)
                r4.setOnClickListener(r5)
                org.telegram.ui.Components.StickerMasksAlert$SearchField$2 r4 = new org.telegram.ui.Components.StickerMasksAlert$SearchField$2
                r4.<init>(r2, r1)
                r0.searchEditText = r4
                r2 = 1098907648(0x41800000, float:16.0)
                r4.setTextSize(r6, r2)
                org.telegram.ui.Components.EditTextBoldCursor r2 = r0.searchEditText
                r2.setHintTextColor(r10)
                org.telegram.ui.Components.EditTextBoldCursor r2 = r0.searchEditText
                r2.setTextColor(r9)
                org.telegram.ui.Components.EditTextBoldCursor r2 = r0.searchEditText
                r4 = 0
                r2.setBackgroundDrawable(r4)
                org.telegram.ui.Components.EditTextBoldCursor r2 = r0.searchEditText
                r4 = 0
                r2.setPadding(r4, r4, r4, r4)
                org.telegram.ui.Components.EditTextBoldCursor r2 = r0.searchEditText
                r2.setMaxLines(r6)
                org.telegram.ui.Components.EditTextBoldCursor r2 = r0.searchEditText
                r2.setLines(r6)
                org.telegram.ui.Components.EditTextBoldCursor r2 = r0.searchEditText
                r2.setSingleLine(r6)
                org.telegram.ui.Components.EditTextBoldCursor r2 = r0.searchEditText
                r4 = 268435459(0x10000003, float:2.5243558E-29)
                r2.setImeOptions(r4)
                if (r3 != 0) goto L_0x0140
                org.telegram.ui.Components.EditTextBoldCursor r2 = r0.searchEditText
                r3 = 2131628184(0x7f0e1098, float:1.8883654E38)
                java.lang.String r4 = "SearchStickersHint"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r2.setHint(r3)
                goto L_0x0162
            L_0x0140:
                if (r3 != r6) goto L_0x0151
                org.telegram.ui.Components.EditTextBoldCursor r2 = r0.searchEditText
                r3 = 2131628158(0x7f0e107e, float:1.88836E38)
                java.lang.String r4 = "SearchEmojiHint"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r2.setHint(r3)
                goto L_0x0162
            L_0x0151:
                r2 = 2
                if (r3 != r2) goto L_0x0162
                org.telegram.ui.Components.EditTextBoldCursor r2 = r0.searchEditText
                r3 = 2131628174(0x7f0e108e, float:1.8883633E38)
                java.lang.String r4 = "SearchGifsTitle"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r2.setHint(r3)
            L_0x0162:
                org.telegram.ui.Components.EditTextBoldCursor r2 = r0.searchEditText
                r2.setCursorColor(r9)
                org.telegram.ui.Components.EditTextBoldCursor r2 = r0.searchEditText
                r3 = 1101004800(0x41a00000, float:20.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r2.setCursorSize(r3)
                org.telegram.ui.Components.EditTextBoldCursor r2 = r0.searchEditText
                r3 = 1069547520(0x3fCLASSNAME, float:1.5)
                r2.setCursorWidth(r3)
                org.telegram.ui.Components.EditTextBoldCursor r2 = r0.searchEditText
                r3 = -1
                r4 = 1109393408(0x42200000, float:40.0)
                r5 = 51
                r6 = 1113063424(0x42580000, float:54.0)
                r7 = 1094713344(0x41400000, float:12.0)
                r8 = 1110966272(0x42380000, float:46.0)
                r9 = 0
                android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r5, r6, r7, r8, r9)
                r0.addView(r2, r3)
                org.telegram.ui.Components.EditTextBoldCursor r2 = r0.searchEditText
                org.telegram.ui.Components.StickerMasksAlert$SearchField$3 r3 = new org.telegram.ui.Components.StickerMasksAlert$SearchField$3
                r3.<init>(r1)
                r2.addTextChangedListener(r3)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerMasksAlert.SearchField.<init>(org.telegram.ui.Components.StickerMasksAlert, android.content.Context, int):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }

        /* access modifiers changed from: private */
        public void showShadow(boolean z, boolean z2) {
            if (z && this.shadowView.getTag() == null) {
                return;
            }
            if (z || this.shadowView.getTag() == null) {
                AnimatorSet animatorSet = this.shadowAnimator;
                int i = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.shadowAnimator = null;
                }
                View view = this.shadowView;
                if (!z) {
                    i = 1;
                }
                view.setTag(i);
                float f = 1.0f;
                if (z2) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.shadowAnimator = animatorSet2;
                    Animator[] animatorArr = new Animator[1];
                    View view2 = this.shadowView;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    if (!z) {
                        f = 0.0f;
                    }
                    fArr[0] = f;
                    animatorArr[0] = ObjectAnimator.ofFloat(view2, property, fArr);
                    animatorSet2.playTogether(animatorArr);
                    this.shadowAnimator.setDuration(200);
                    this.shadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.shadowAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            AnimatorSet unused = SearchField.this.shadowAnimator = null;
                        }
                    });
                    this.shadowAnimator.start();
                    return;
                }
                View view3 = this.shadowView;
                if (!z) {
                    f = 0.0f;
                }
                view3.setAlpha(f);
            }
        }
    }

    public StickerMasksAlert(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
        super(context, true, resourcesProvider);
        this.behindKeyboardColorKey = null;
        this.behindKeyboardColor = -14342875;
        this.useLightStatusBar = false;
        fixNavigationBar(-14342875);
        this.currentType = 0;
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
        MediaDataController.getInstance(this.currentAccount).loadRecents(0, false, true, false);
        MediaDataController.getInstance(this.currentAccount).loadRecents(1, false, true, false);
        MediaDataController.getInstance(this.currentAccount).loadRecents(2, false, true, false);
        Drawable mutate = context.getResources().getDrawable(NUM).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(-14342875, PorterDuff.Mode.MULTIPLY));
        AnonymousClass2 r5 = new SizeNotifierFrameLayout(context) {
            private boolean ignoreLayout = false;
            private long lastUpdateTime;
            private RectF rect = new RectF();
            private float statusBarProgress;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int i3;
                int size = View.MeasureSpec.getSize(i2);
                if (Build.VERSION.SDK_INT >= 21 && !StickerMasksAlert.this.isFullscreen) {
                    this.ignoreLayout = true;
                    setPadding(StickerMasksAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, StickerMasksAlert.this.backgroundPaddingLeft, 0);
                    this.ignoreLayout = false;
                }
                int paddingTop = size - getPaddingTop();
                if (measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                    this.statusBarProgress = 1.0f;
                    i3 = 0;
                } else {
                    i3 = (paddingTop - ((paddingTop / 5) * 3)) + AndroidUtilities.dp(8.0f);
                }
                if (StickerMasksAlert.this.gridView.getPaddingTop() != i3) {
                    this.ignoreLayout = true;
                    StickerMasksAlert.this.gridView.setPinnedSectionOffsetY(-i3);
                    StickerMasksAlert.this.gridView.setPadding(0, i3, 0, AndroidUtilities.dp(48.0f));
                    this.ignoreLayout = false;
                }
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(size, NUM));
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                StickerMasksAlert.this.updateLayout(false);
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || StickerMasksAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) (StickerMasksAlert.this.scrollOffsetY + AndroidUtilities.dp(12.0f)))) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                StickerMasksAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return !StickerMasksAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                float f;
                int dp = AndroidUtilities.dp(13.0f);
                int access$1100 = (StickerMasksAlert.this.scrollOffsetY - StickerMasksAlert.this.backgroundPaddingTop) - dp;
                if (StickerMasksAlert.this.currentSheetAnimationType == 1) {
                    access$1100 = (int) (((float) access$1100) + StickerMasksAlert.this.gridView.getTranslationY());
                }
                int dp2 = AndroidUtilities.dp(20.0f) + access$1100;
                int measuredHeight = getMeasuredHeight() + AndroidUtilities.dp(15.0f) + StickerMasksAlert.this.backgroundPaddingTop;
                int dp3 = AndroidUtilities.dp(12.0f);
                if (StickerMasksAlert.this.backgroundPaddingTop + access$1100 < dp3) {
                    float dp4 = (float) (dp + AndroidUtilities.dp(4.0f));
                    float min = Math.min(1.0f, ((float) ((dp3 - access$1100) - StickerMasksAlert.this.backgroundPaddingTop)) / dp4);
                    int i = (int) ((((float) dp3) - dp4) * min);
                    access$1100 -= i;
                    dp2 -= i;
                    measuredHeight += i;
                    f = 1.0f - min;
                } else {
                    f = 1.0f;
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    int i2 = AndroidUtilities.statusBarHeight;
                    access$1100 += i2;
                    dp2 += i2;
                }
                StickerMasksAlert.this.shadowDrawable.setBounds(0, access$1100, getMeasuredWidth(), measuredHeight);
                StickerMasksAlert.this.shadowDrawable.draw(canvas);
                if (f != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(-14342875);
                    this.rect.set((float) StickerMasksAlert.this.backgroundPaddingLeft, (float) (StickerMasksAlert.this.backgroundPaddingTop + access$1100), (float) (getMeasuredWidth() - StickerMasksAlert.this.backgroundPaddingLeft), (float) (StickerMasksAlert.this.backgroundPaddingTop + access$1100 + AndroidUtilities.dp(24.0f)));
                    canvas.drawRoundRect(this.rect, ((float) AndroidUtilities.dp(12.0f)) * f, ((float) AndroidUtilities.dp(12.0f)) * f, Theme.dialogs_onlineCirclePaint);
                }
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long j = elapsedRealtime - this.lastUpdateTime;
                if (j > 18) {
                    j = 18;
                }
                this.lastUpdateTime = elapsedRealtime;
                if (f > 0.0f) {
                    int dp5 = AndroidUtilities.dp(36.0f);
                    this.rect.set((float) ((getMeasuredWidth() - dp5) / 2), (float) dp2, (float) ((getMeasuredWidth() + dp5) / 2), (float) (dp2 + AndroidUtilities.dp(4.0f)));
                    int alpha = Color.alpha(-11842741);
                    Theme.dialogs_onlineCirclePaint.setColor(-11842741);
                    Theme.dialogs_onlineCirclePaint.setAlpha((int) (((float) alpha) * 1.0f * f));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                    float f2 = this.statusBarProgress;
                    if (f2 > 0.0f) {
                        float f3 = f2 - (((float) j) / 180.0f);
                        this.statusBarProgress = f3;
                        if (f3 < 0.0f) {
                            this.statusBarProgress = 0.0f;
                        } else {
                            invalidate();
                        }
                    }
                } else {
                    float f4 = this.statusBarProgress;
                    if (f4 < 1.0f) {
                        float f5 = f4 + (((float) j) / 180.0f);
                        this.statusBarProgress = f5;
                        if (f5 > 1.0f) {
                            this.statusBarProgress = 1.0f;
                        } else {
                            invalidate();
                        }
                    }
                }
                Theme.dialogs_onlineCirclePaint.setColor(Color.argb((int) (this.statusBarProgress * 255.0f), (int) (((float) Color.red(-14342875)) * 0.8f), (int) (((float) Color.green(-14342875)) * 0.8f), (int) (((float) Color.blue(-14342875)) * 0.8f)));
                canvas.drawRect((float) StickerMasksAlert.this.backgroundPaddingLeft, 0.0f, (float) (getMeasuredWidth() - StickerMasksAlert.this.backgroundPaddingLeft), (float) AndroidUtilities.statusBarHeight, Theme.dialogs_onlineCirclePaint);
            }
        };
        this.containerView = r5;
        r5.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, 0, i, 0);
        this.searchFieldHeight = AndroidUtilities.dp(64.0f);
        this.stickerIcons = new Drawable[]{Theme.createEmojiIconSelectorDrawable(context, NUM, -11842741, -9520403), Theme.createEmojiIconSelectorDrawable(context, NUM, -11842741, -9520403)};
        MediaDataController.getInstance(this.currentAccount).checkStickers(0);
        MediaDataController.getInstance(this.currentAccount).checkStickers(1);
        MediaDataController.getInstance(this.currentAccount).checkFeaturedStickers();
        AnonymousClass3 r1 = new RecyclerListView(context) {
            /* access modifiers changed from: protected */
            public boolean allowSelectChildAtPosition(float f, float f2) {
                return f2 >= ((float) (StickerMasksAlert.this.scrollOffsetY + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, StickerMasksAlert.this.gridView, StickerMasksAlert.this.containerView.getMeasuredHeight(), StickerMasksAlert.this.contentPreviewViewerDelegate, this.resourcesProvider);
            }
        };
        this.gridView = r1;
        AnonymousClass4 r52 = new GridLayoutManager(context, 5) {
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                AnonymousClass1 r2 = new LinearSmoothScroller(recyclerView.getContext()) {
                    public int calculateDyToMakeVisible(View view, int i) {
                        return super.calculateDyToMakeVisible(view, i) - (StickerMasksAlert.this.gridView.getPaddingTop() - AndroidUtilities.dp(7.0f));
                    }

                    /* access modifiers changed from: protected */
                    public int calculateTimeForDeceleration(int i) {
                        return super.calculateTimeForDeceleration(i) * 4;
                    }
                };
                r2.setTargetPosition(i);
                startSmoothScroll(r2);
            }
        };
        this.stickersLayoutManager = r52;
        r1.setLayoutManager(r52);
        this.stickersLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int i) {
                if (StickerMasksAlert.this.gridView.getAdapter() == StickerMasksAlert.this.stickersGridAdapter) {
                    if (i == 0) {
                        return StickerMasksAlert.this.stickersGridAdapter.stickersPerRow;
                    }
                    if (i == StickerMasksAlert.this.stickersGridAdapter.totalItems || (StickerMasksAlert.this.stickersGridAdapter.cache.get(i) != null && !(StickerMasksAlert.this.stickersGridAdapter.cache.get(i) instanceof TLRPC$Document))) {
                        return StickerMasksAlert.this.stickersGridAdapter.stickersPerRow;
                    }
                    return 1;
                } else if (i == StickerMasksAlert.this.stickersSearchGridAdapter.totalItems || (StickerMasksAlert.this.stickersSearchGridAdapter.cache.get(i) != null && !(StickerMasksAlert.this.stickersSearchGridAdapter.cache.get(i) instanceof TLRPC$Document))) {
                    return StickerMasksAlert.this.stickersGridAdapter.stickersPerRow;
                } else {
                    return 1;
                }
            }
        });
        this.gridView.setPadding(0, AndroidUtilities.dp(52.0f), 0, AndroidUtilities.dp(48.0f));
        this.gridView.setClipToPadding(false);
        this.gridView.setHorizontalScrollBarEnabled(false);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.setGlowColor(-14342875);
        this.stickersSearchGridAdapter = new StickersSearchGridAdapter(context);
        RecyclerListView recyclerListView = this.gridView;
        StickersGridAdapter stickersGridAdapter2 = new StickersGridAdapter(context);
        this.stickersGridAdapter = stickersGridAdapter2;
        recyclerListView.setAdapter(stickersGridAdapter2);
        this.gridView.setOnTouchListener(new StickerMasksAlert$$ExternalSyntheticLambda2(this, resourcesProvider));
        StickerMasksAlert$$ExternalSyntheticLambda3 stickerMasksAlert$$ExternalSyntheticLambda3 = new StickerMasksAlert$$ExternalSyntheticLambda3(this);
        this.stickersOnItemClickListener = stickerMasksAlert$$ExternalSyntheticLambda3;
        this.gridView.setOnItemClickListener((RecyclerListView.OnItemClickListener) stickerMasksAlert$$ExternalSyntheticLambda3);
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f));
        this.stickersTab = new ScrollSlidingTabStrip(this, context, resourcesProvider) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(motionEvent);
            }
        };
        SearchField searchField = new SearchField(this, context, 0);
        this.stickersSearchField = searchField;
        this.containerView.addView(searchField, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
        this.stickersTab.setType(ScrollSlidingTabStrip.Type.TAB);
        this.stickersTab.setUnderlineHeight(AndroidUtilities.getShadowHeight());
        this.stickersTab.setIndicatorColor(-9520403);
        this.stickersTab.setUnderlineColor(-16053493);
        this.stickersTab.setBackgroundColor(-14342875);
        this.containerView.addView(this.stickersTab, LayoutHelper.createFrame(-1, 36, 51));
        this.stickersTab.setDelegate(new StickerMasksAlert$$ExternalSyntheticLambda4(this));
        this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    StickerMasksAlert.this.stickersSearchField.hideKeyboard();
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                StickerMasksAlert.this.updateLayout(true);
            }
        });
        View view = new View(context);
        view.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, -1907225));
        this.containerView.addView(view, LayoutHelper.createFrame(-1, 6.0f));
        if (!z) {
            this.bottomTabContainer = new FrameLayout(this, context) {
                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    return super.onInterceptTouchEvent(motionEvent);
                }
            };
            View view2 = new View(context);
            this.shadowLine = view2;
            view2.setBackgroundColor(NUM);
            this.bottomTabContainer.addView(this.shadowLine, new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight()));
            View view3 = new View(context);
            view3.setBackgroundColor(-14342875);
            this.bottomTabContainer.addView(view3, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(48.0f), 83));
            this.containerView.addView(this.bottomTabContainer, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(48.0f) + AndroidUtilities.getShadowHeight(), 83));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            this.bottomTabContainer.addView(linearLayout, LayoutHelper.createFrame(-2, 48, 81));
            AnonymousClass9 r14 = new ImageView(this, context) {
                public void setSelected(boolean z) {
                    super.setSelected(z);
                    Drawable background = getBackground();
                    if (Build.VERSION.SDK_INT >= 21 && background != null) {
                        int i = z ? -9520403 : NUM;
                        Theme.setSelectorDrawableColor(background, Color.argb(30, Color.red(i), Color.green(i), Color.blue(i)), true);
                    }
                }
            };
            this.stickersButton = r14;
            r14.setScaleType(ImageView.ScaleType.CENTER);
            this.stickersButton.setImageDrawable(Theme.createEmojiIconSelectorDrawable(context, NUM, -1, -9520403));
            int i2 = Build.VERSION.SDK_INT;
            if (i2 >= 21) {
                RippleDrawable rippleDrawable = (RippleDrawable) Theme.createSelectorDrawable(NUM);
                Theme.setRippleDrawableForceSoftware(rippleDrawable);
                this.stickersButton.setBackground(rippleDrawable);
            }
            linearLayout.addView(this.stickersButton, LayoutHelper.createLinear(70, 48));
            this.stickersButton.setOnClickListener(new StickerMasksAlert$$ExternalSyntheticLambda1(this));
            AnonymousClass10 r4 = new ImageView(this, context) {
                public void setSelected(boolean z) {
                    super.setSelected(z);
                    Drawable background = getBackground();
                    if (Build.VERSION.SDK_INT >= 21 && background != null) {
                        int i = z ? -9520403 : NUM;
                        Theme.setSelectorDrawableColor(background, Color.argb(30, Color.red(i), Color.green(i), Color.blue(i)), true);
                    }
                }
            };
            this.masksButton = r4;
            r4.setScaleType(ImageView.ScaleType.CENTER);
            this.masksButton.setImageDrawable(Theme.createEmojiIconSelectorDrawable(context, NUM, -1, -9520403));
            if (i2 >= 21) {
                RippleDrawable rippleDrawable2 = (RippleDrawable) Theme.createSelectorDrawable(NUM);
                Theme.setRippleDrawableForceSoftware(rippleDrawable2);
                this.masksButton.setBackground(rippleDrawable2);
            }
            linearLayout.addView(this.masksButton, LayoutHelper.createLinear(70, 48));
            this.masksButton.setOnClickListener(new StickerMasksAlert$$ExternalSyntheticLambda0(this));
        }
        checkDocuments(true);
        reloadStickersAdapter();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$0(Theme.ResourcesProvider resourcesProvider, View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.gridView, this.containerView.getMeasuredHeight(), this.stickersOnItemClickListener, this.contentPreviewViewerDelegate, resourcesProvider);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view, int i) {
        if (view instanceof StickerEmojiCell) {
            ContentPreviewViewer.getInstance().reset();
            StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
            this.delegate.onStickerSelected(stickerEmojiCell.getParentObject(), stickerEmojiCell.getSticker());
            dismiss();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(int i) {
        int i2;
        if (i == this.recentTabBum) {
            i2 = this.stickersGridAdapter.getPositionForPack("recent");
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            int i3 = this.recentTabBum;
            scrollSlidingTabStrip.onPageScrolled(i3, i3 > 0 ? i3 : this.stickersTabOffset);
        } else if (i == this.favTabBum) {
            i2 = this.stickersGridAdapter.getPositionForPack("fav");
            ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
            int i4 = this.favTabBum;
            scrollSlidingTabStrip2.onPageScrolled(i4, i4 > 0 ? i4 : this.stickersTabOffset);
        } else {
            int i5 = i - this.stickersTabOffset;
            if (i5 < this.stickerSets[this.currentType].size()) {
                if (i5 >= this.stickerSets[this.currentType].size()) {
                    i5 = this.stickerSets[this.currentType].size() - 1;
                }
                i2 = this.stickersGridAdapter.getPositionForPack(this.stickerSets[this.currentType].get(i5));
            } else {
                return;
            }
        }
        if (this.stickersLayoutManager.findFirstVisibleItemPosition() != i2) {
            this.stickersLayoutManager.scrollToPositionWithOffset(i2, (-this.gridView.getPaddingTop()) + this.searchFieldHeight + AndroidUtilities.dp(48.0f));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view) {
        if (this.currentType != 0) {
            this.currentType = 0;
            updateType();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(View view) {
        if (this.currentType != 1) {
            this.currentType = 1;
            updateType();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
        r0 = r3.gridView.getChildAt(0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateType() {
        /*
            r3 = this;
            org.telegram.ui.Components.RecyclerListView r0 = r3.gridView
            int r0 = r0.getChildCount()
            if (r0 <= 0) goto L_0x0036
            org.telegram.ui.Components.RecyclerListView r0 = r3.gridView
            r1 = 0
            android.view.View r0 = r0.getChildAt(r1)
            org.telegram.ui.Components.RecyclerListView r2 = r3.gridView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r2.findContainingViewHolder(r0)
            if (r2 == 0) goto L_0x0036
            int r2 = r2.getAdapterPosition()
            if (r2 == 0) goto L_0x0025
            org.telegram.ui.Components.RecyclerListView r0 = r3.gridView
            int r0 = r0.getPaddingTop()
            int r0 = -r0
            goto L_0x0031
        L_0x0025:
            org.telegram.ui.Components.RecyclerListView r2 = r3.gridView
            int r2 = r2.getPaddingTop()
            int r2 = -r2
            int r0 = r0.getTop()
            int r0 = r0 + r2
        L_0x0031:
            androidx.recyclerview.widget.GridLayoutManager r2 = r3.stickersLayoutManager
            r2.scrollToPositionWithOffset(r1, r0)
        L_0x0036:
            r0 = 1
            r3.checkDocuments(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerMasksAlert.updateType():void");
    }

    public void setDelegate(StickerMasksAlertDelegate stickerMasksAlertDelegate) {
        this.delegate = stickerMasksAlertDelegate;
    }

    /* access modifiers changed from: private */
    public void updateLayout(boolean z) {
        RecyclerListView.Holder holder;
        if (this.gridView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.gridView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        View childAt = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder2 = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(childAt);
        int top = childAt.getTop();
        int dp = AndroidUtilities.dp(7.0f);
        if (top < AndroidUtilities.dp(7.0f) || holder2 == null || holder2.getAdapterPosition() != 0) {
            top = dp;
        }
        int i = top + (-AndroidUtilities.dp(11.0f));
        if (this.scrollOffsetY != i) {
            RecyclerListView recyclerListView2 = this.gridView;
            this.scrollOffsetY = i;
            recyclerListView2.setTopGlowOffset(i);
            this.stickersTab.setTranslationY((float) i);
            this.stickersSearchField.setTranslationY((float) (i + AndroidUtilities.dp(48.0f)));
            this.containerView.invalidate();
        }
        RecyclerListView.Holder holder3 = (RecyclerListView.Holder) this.gridView.findViewHolderForAdapterPosition(0);
        if (holder3 == null) {
            this.stickersSearchField.showShadow(true, z);
        } else {
            this.stickersSearchField.showShadow(holder3.itemView.getTop() < this.gridView.getPaddingTop(), z);
        }
        RecyclerView.Adapter adapter = this.gridView.getAdapter();
        StickersSearchGridAdapter stickersSearchGridAdapter2 = this.stickersSearchGridAdapter;
        if (adapter == stickersSearchGridAdapter2 && (holder = (RecyclerListView.Holder) this.gridView.findViewHolderForAdapterPosition(stickersSearchGridAdapter2.getItemCount() - 1)) != null && holder.getItemViewType() == 5) {
            FrameLayout frameLayout = (FrameLayout) holder.itemView;
            int childCount = frameLayout.getChildCount();
            float f = (float) ((-((frameLayout.getTop() - this.searchFieldHeight) - AndroidUtilities.dp(48.0f))) / 2);
            for (int i2 = 0; i2 < childCount; i2++) {
                frameLayout.getChildAt(i2).setTranslationY(f);
            }
        }
        checkPanels();
    }

    private void updateStickerTabs() {
        ArrayList<TLRPC$Document> arrayList;
        if (this.stickersTab != null) {
            ImageView imageView = this.stickersButton;
            if (imageView != null) {
                if (this.currentType == 0) {
                    imageView.setSelected(true);
                    this.masksButton.setSelected(false);
                } else {
                    imageView.setSelected(false);
                    this.masksButton.setSelected(true);
                }
            }
            this.recentTabBum = -2;
            this.favTabBum = -2;
            this.stickersTabOffset = 0;
            int currentPosition = this.stickersTab.getCurrentPosition();
            this.stickersTab.beginUpdate(false);
            if (this.currentType == 0 && !this.favouriteStickers.isEmpty()) {
                int i = this.stickersTabOffset;
                this.favTabBum = i;
                this.stickersTabOffset = i + 1;
                this.stickersTab.addIconTab(1, this.stickerIcons[1]).setContentDescription(LocaleController.getString("FavoriteStickers", NUM));
            }
            if (!this.recentStickers[this.currentType].isEmpty()) {
                int i2 = this.stickersTabOffset;
                this.recentTabBum = i2;
                this.stickersTabOffset = i2 + 1;
                this.stickersTab.addIconTab(0, this.stickerIcons[0]).setContentDescription(LocaleController.getString("RecentStickers", NUM));
            }
            this.stickerSets[this.currentType].clear();
            ArrayList<TLRPC$TL_messages_stickerSet> stickerSets2 = MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType);
            for (int i3 = 0; i3 < stickerSets2.size(); i3++) {
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = stickerSets2.get(i3);
                if (!tLRPC$TL_messages_stickerSet.set.archived && (arrayList = tLRPC$TL_messages_stickerSet.documents) != null && !arrayList.isEmpty()) {
                    this.stickerSets[this.currentType].add(tLRPC$TL_messages_stickerSet);
                }
            }
            for (int i4 = 0; i4 < this.stickerSets[this.currentType].size(); i4++) {
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = this.stickerSets[this.currentType].get(i4);
                TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet2.documents.get(0);
                TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_messages_stickerSet2.set.thumbs, 90);
                if (closestPhotoSizeWithSize == null) {
                    closestPhotoSizeWithSize = tLRPC$Document;
                }
                View addStickerTab = this.stickersTab.addStickerTab(closestPhotoSizeWithSize, tLRPC$Document, tLRPC$TL_messages_stickerSet2);
                addStickerTab.setContentDescription(tLRPC$TL_messages_stickerSet2.set.title + ", " + LocaleController.getString("AccDescrStickerSet", NUM));
            }
            this.stickersTab.commitUpdate();
            this.stickersTab.updateTabStyles();
            if (currentPosition != 0) {
                this.stickersTab.onPageScrolled(currentPosition, currentPosition);
            }
            checkPanels();
        }
    }

    private void checkPanels() {
        if (this.stickersTab != null) {
            int childCount = this.gridView.getChildCount();
            View view = null;
            for (int i = 0; i < childCount; i++) {
                view = this.gridView.getChildAt(i);
                if (view.getBottom() > this.searchFieldHeight + AndroidUtilities.dp(48.0f)) {
                    break;
                }
            }
            if (view != null) {
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(view);
                int adapterPosition = holder != null ? holder.getAdapterPosition() : -1;
                if (adapterPosition != -1) {
                    int i2 = this.favTabBum;
                    if (i2 <= 0 && (i2 = this.recentTabBum) <= 0) {
                        i2 = this.stickersTabOffset;
                    }
                    this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(adapterPosition), i2);
                }
            }
        }
    }

    private void reloadStickersAdapter() {
        StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
        if (stickersGridAdapter2 != null) {
            stickersGridAdapter2.notifyDataSetChanged();
        }
        StickersSearchGridAdapter stickersSearchGridAdapter2 = this.stickersSearchGridAdapter;
        if (stickersSearchGridAdapter2 != null) {
            stickersSearchGridAdapter2.notifyDataSetChanged();
        }
        if (ContentPreviewViewer.getInstance().isVisible()) {
            ContentPreviewViewer.getInstance().close();
        }
        ContentPreviewViewer.getInstance().reset();
    }

    public void dismissInternal() {
        super.dismissInternal();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentDocumentsDidLoad);
    }

    private void checkDocuments(boolean z) {
        int size = this.recentStickers[this.currentType].size();
        int size2 = this.favouriteStickers.size();
        this.recentStickers[this.currentType] = MediaDataController.getInstance(this.currentAccount).getRecentStickers(this.currentType);
        this.favouriteStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(2);
        if (this.currentType == 0) {
            for (int i = 0; i < this.favouriteStickers.size(); i++) {
                TLRPC$Document tLRPC$Document = this.favouriteStickers.get(i);
                int i2 = 0;
                while (true) {
                    if (i2 >= this.recentStickers[this.currentType].size()) {
                        break;
                    }
                    TLRPC$Document tLRPC$Document2 = this.recentStickers[this.currentType].get(i2);
                    if (tLRPC$Document2.dc_id == tLRPC$Document.dc_id && tLRPC$Document2.id == tLRPC$Document.id) {
                        this.recentStickers[this.currentType].remove(i2);
                        break;
                    }
                    i2++;
                }
            }
        }
        if (!(!z && size == this.recentStickers[this.currentType].size() && size2 == this.favouriteStickers.size())) {
            updateStickerTabs();
        }
        StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
        if (stickersGridAdapter2 != null) {
            stickersGridAdapter2.notifyDataSetChanged();
        }
        if (!z) {
            checkPanels();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        RecyclerListView recyclerListView;
        if (i == NotificationCenter.stickersDidLoad) {
            if (objArr[0].intValue() == this.currentType) {
                updateStickerTabs();
                reloadStickersAdapter();
                checkPanels();
            }
        } else if (i == NotificationCenter.recentDocumentsDidLoad) {
            boolean booleanValue = objArr[0].booleanValue();
            int intValue = objArr[1].intValue();
            if (booleanValue) {
                return;
            }
            if (intValue == this.currentType || intValue == 2) {
                checkDocuments(false);
            }
        } else if (i == NotificationCenter.emojiLoaded && (recyclerListView = this.gridView) != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = this.gridView.getChildAt(i3);
                if ((childAt instanceof StickerSetNameCell) || (childAt instanceof StickerEmojiCell)) {
                    childAt.invalidate();
                }
            }
        }
    }

    private class StickersGridAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public SparseArray<Object> cache = new SparseArray<>();
        private SparseArray<Object> cacheParents = new SparseArray<>();
        private Context context;
        private HashMap<Object, Integer> packStartPosition = new HashMap<>();
        private SparseIntArray positionToRow = new SparseIntArray();
        private SparseArray<Object> rowStartPack = new SparseArray<>();
        /* access modifiers changed from: private */
        public int stickersPerRow;
        /* access modifiers changed from: private */
        public int totalItems;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public StickersGridAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            int i = this.totalItems;
            if (i != 0) {
                return i + 1;
            }
            return 0;
        }

        public int getPositionForPack(Object obj) {
            Integer num = this.packStartPosition.get(obj);
            if (num == null) {
                return -1;
            }
            return num.intValue();
        }

        public int getItemViewType(int i) {
            if (i == 0) {
                return 4;
            }
            Object obj = this.cache.get(i);
            if (obj != null) {
                return obj instanceof TLRPC$Document ? 0 : 2;
            }
            return 1;
        }

        public int getTabForPosition(int i) {
            if (i == 0) {
                i = 1;
            }
            if (this.stickersPerRow == 0) {
                int measuredWidth = StickerMasksAlert.this.gridView.getMeasuredWidth();
                if (measuredWidth == 0) {
                    measuredWidth = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
            }
            int i2 = this.positionToRow.get(i, Integer.MIN_VALUE);
            if (i2 == Integer.MIN_VALUE) {
                return (StickerMasksAlert.this.stickerSets[StickerMasksAlert.this.currentType].size() - 1) + StickerMasksAlert.this.stickersTabOffset;
            }
            Object obj = this.rowStartPack.get(i2);
            if (!(obj instanceof String)) {
                return StickerMasksAlert.this.stickerSets[StickerMasksAlert.this.currentType].indexOf((TLRPC$TL_messages_stickerSet) obj) + StickerMasksAlert.this.stickersTabOffset;
            } else if ("recent".equals(obj)) {
                return StickerMasksAlert.this.recentTabBum;
            } else {
                return StickerMasksAlert.this.favTabBum;
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: org.telegram.ui.Components.StickerMasksAlert$StickersGridAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v8, resolved type: org.telegram.ui.Components.StickerMasksAlert$StickersGridAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v9, resolved type: org.telegram.ui.Components.StickerMasksAlert$StickersGridAdapter$1} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r4, int r5) {
            /*
                r3 = this;
                r4 = 0
                if (r5 == 0) goto L_0x0048
                r0 = 1
                if (r5 == r0) goto L_0x0040
                r0 = 2
                if (r5 == r0) goto L_0x002c
                r4 = 4
                if (r5 == r4) goto L_0x000e
                r4 = 0
                goto L_0x0050
            L_0x000e:
                android.view.View r4 = new android.view.View
                android.content.Context r5 = r3.context
                r4.<init>(r5)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r5 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                org.telegram.ui.Components.StickerMasksAlert r1 = org.telegram.ui.Components.StickerMasksAlert.this
                int r1 = r1.searchFieldHeight
                r2 = 1111490560(0x42400000, float:48.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r1 = r1 + r2
                r5.<init>((int) r0, (int) r1)
                r4.setLayoutParams(r5)
                goto L_0x0050
            L_0x002c:
                org.telegram.ui.Cells.StickerSetNameCell r5 = new org.telegram.ui.Cells.StickerSetNameCell
                android.content.Context r0 = r3.context
                org.telegram.ui.Components.StickerMasksAlert r1 = org.telegram.ui.Components.StickerMasksAlert.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r1.resourcesProvider
                r5.<init>(r0, r4, r1)
                r4 = -7829368(0xfffffffffvar_, float:NaN)
                r5.setTitleColor(r4)
                goto L_0x004f
            L_0x0040:
                org.telegram.ui.Cells.EmptyCell r4 = new org.telegram.ui.Cells.EmptyCell
                android.content.Context r5 = r3.context
                r4.<init>(r5)
                goto L_0x0050
            L_0x0048:
                org.telegram.ui.Components.StickerMasksAlert$StickersGridAdapter$1 r5 = new org.telegram.ui.Components.StickerMasksAlert$StickersGridAdapter$1
                android.content.Context r0 = r3.context
                r5.<init>(r3, r0, r4)
            L_0x004f:
                r4 = r5
            L_0x0050:
                org.telegram.ui.Components.RecyclerListView$Holder r5 = new org.telegram.ui.Components.RecyclerListView$Holder
                r5.<init>(r4)
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerMasksAlert.StickersGridAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ArrayList<TLRPC$Document> arrayList;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                int i2 = 1;
                if (itemViewType == 1) {
                    EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
                    if (i == this.totalItems) {
                        int i3 = this.positionToRow.get(i - 1, Integer.MIN_VALUE);
                        if (i3 == Integer.MIN_VALUE) {
                            emptyCell.setHeight(1);
                            return;
                        }
                        Object obj = this.rowStartPack.get(i3);
                        if (obj instanceof TLRPC$TL_messages_stickerSet) {
                            arrayList = ((TLRPC$TL_messages_stickerSet) obj).documents;
                        } else if (obj instanceof String) {
                            arrayList = "recent".equals(obj) ? StickerMasksAlert.this.recentStickers[StickerMasksAlert.this.currentType] : StickerMasksAlert.this.favouriteStickers;
                        } else {
                            arrayList = null;
                        }
                        if (arrayList == null) {
                            emptyCell.setHeight(1);
                        } else if (arrayList.isEmpty()) {
                            emptyCell.setHeight(AndroidUtilities.dp(8.0f));
                        } else {
                            int height = StickerMasksAlert.this.gridView.getHeight() - (((int) Math.ceil((double) (((float) arrayList.size()) / ((float) this.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                            if (height > 0) {
                                i2 = height;
                            }
                            emptyCell.setHeight(i2);
                        }
                    } else {
                        emptyCell.setHeight(AndroidUtilities.dp(82.0f));
                    }
                } else if (itemViewType == 2) {
                    StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                    Object obj2 = this.cache.get(i);
                    if (obj2 instanceof TLRPC$TL_messages_stickerSet) {
                        TLRPC$StickerSet tLRPC$StickerSet = ((TLRPC$TL_messages_stickerSet) obj2).set;
                        if (tLRPC$StickerSet != null) {
                            stickerSetNameCell.setText(tLRPC$StickerSet.title, 0);
                        }
                    } else if (obj2 == StickerMasksAlert.this.recentStickers[StickerMasksAlert.this.currentType]) {
                        stickerSetNameCell.setText(LocaleController.getString("RecentStickers", NUM), 0);
                    } else if (obj2 == StickerMasksAlert.this.favouriteStickers) {
                        stickerSetNameCell.setText(LocaleController.getString("FavoriteStickers", NUM), 0);
                    }
                }
            } else {
                TLRPC$Document tLRPC$Document = (TLRPC$Document) this.cache.get(i);
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) viewHolder.itemView;
                stickerEmojiCell.setSticker(tLRPC$Document, this.cacheParents.get(i), false);
                stickerEmojiCell.setRecent(StickerMasksAlert.this.recentStickers[StickerMasksAlert.this.currentType].contains(tLRPC$Document));
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:23:0x00e3  */
        /* JADX WARNING: Removed duplicated region for block: B:24:0x00eb  */
        /* JADX WARNING: Removed duplicated region for block: B:28:0x0100  */
        /* JADX WARNING: Removed duplicated region for block: B:36:0x0133  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x014e A[EDGE_INSN: B:56:0x014e->B:44:0x014e ?: BREAK  , SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void notifyDataSetChanged() {
            /*
                r17 = this;
                r0 = r17
                org.telegram.ui.Components.StickerMasksAlert r1 = org.telegram.ui.Components.StickerMasksAlert.this
                org.telegram.ui.Components.RecyclerListView r1 = r1.gridView
                int r1 = r1.getMeasuredWidth()
                if (r1 != 0) goto L_0x0012
                android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
                int r1 = r1.x
            L_0x0012:
                r2 = 1116733440(0x42900000, float:72.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r1 = r1 / r2
                r0.stickersPerRow = r1
                org.telegram.ui.Components.StickerMasksAlert r1 = org.telegram.ui.Components.StickerMasksAlert.this
                androidx.recyclerview.widget.GridLayoutManager r1 = r1.stickersLayoutManager
                int r2 = r0.stickersPerRow
                r1.setSpanCount(r2)
                android.util.SparseArray<java.lang.Object> r1 = r0.rowStartPack
                r1.clear()
                java.util.HashMap<java.lang.Object, java.lang.Integer> r1 = r0.packStartPosition
                r1.clear()
                android.util.SparseIntArray r1 = r0.positionToRow
                r1.clear()
                android.util.SparseArray<java.lang.Object> r1 = r0.cache
                r1.clear()
                r1 = 0
                r0.totalItems = r1
                org.telegram.ui.Components.StickerMasksAlert r2 = org.telegram.ui.Components.StickerMasksAlert.this
                java.util.ArrayList[] r2 = r2.stickerSets
                org.telegram.ui.Components.StickerMasksAlert r3 = org.telegram.ui.Components.StickerMasksAlert.this
                int r3 = r3.currentType
                r2 = r2[r3]
                r3 = -3
                r4 = -3
                r5 = 0
            L_0x004e:
                int r6 = r2.size()
                if (r4 >= r6) goto L_0x0160
                if (r4 != r3) goto L_0x0067
                android.util.SparseArray<java.lang.Object> r6 = r0.cache
                int r7 = r0.totalItems
                int r8 = r7 + 1
                r0.totalItems = r8
                java.lang.String r8 = "search"
                r6.put(r7, r8)
                int r5 = r5 + 1
                goto L_0x015a
            L_0x0067:
                r6 = -2
                java.lang.String r7 = "fav"
                java.lang.String r8 = "recent"
                r9 = -1
                r10 = 0
                if (r4 != r6) goto L_0x008e
                org.telegram.ui.Components.StickerMasksAlert r6 = org.telegram.ui.Components.StickerMasksAlert.this
                int r6 = r6.currentType
                if (r6 != 0) goto L_0x008b
                org.telegram.ui.Components.StickerMasksAlert r6 = org.telegram.ui.Components.StickerMasksAlert.this
                java.util.ArrayList r6 = r6.favouriteStickers
                java.util.HashMap<java.lang.Object, java.lang.Integer> r11 = r0.packStartPosition
                int r12 = r0.totalItems
                java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
                r11.put(r7, r12)
                r11 = r7
                goto L_0x00aa
            L_0x008b:
                r6 = r10
                r11 = r6
                goto L_0x00c8
            L_0x008e:
                if (r4 != r9) goto L_0x00b0
                org.telegram.ui.Components.StickerMasksAlert r6 = org.telegram.ui.Components.StickerMasksAlert.this
                java.util.ArrayList[] r6 = r6.recentStickers
                org.telegram.ui.Components.StickerMasksAlert r11 = org.telegram.ui.Components.StickerMasksAlert.this
                int r11 = r11.currentType
                r6 = r6[r11]
                java.util.HashMap<java.lang.Object, java.lang.Integer> r11 = r0.packStartPosition
                int r12 = r0.totalItems
                java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
                r11.put(r8, r12)
                r11 = r8
            L_0x00aa:
                r16 = r10
                r10 = r6
                r6 = r16
                goto L_0x00c8
            L_0x00b0:
                java.lang.Object r6 = r2.get(r4)
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r6 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r6
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r11 = r6.documents
                java.util.HashMap<java.lang.Object, java.lang.Integer> r12 = r0.packStartPosition
                int r13 = r0.totalItems
                java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
                r12.put(r6, r13)
                r16 = r11
                r11 = r10
                r10 = r16
            L_0x00c8:
                if (r10 == 0) goto L_0x015a
                boolean r12 = r10.isEmpty()
                if (r12 == 0) goto L_0x00d2
                goto L_0x015a
            L_0x00d2:
                int r12 = r10.size()
                float r12 = (float) r12
                int r13 = r0.stickersPerRow
                float r13 = (float) r13
                float r12 = r12 / r13
                double r12 = (double) r12
                double r12 = java.lang.Math.ceil(r12)
                int r12 = (int) r12
                if (r6 == 0) goto L_0x00eb
                android.util.SparseArray<java.lang.Object> r13 = r0.cache
                int r14 = r0.totalItems
                r13.put(r14, r6)
                goto L_0x00f2
            L_0x00eb:
                android.util.SparseArray<java.lang.Object> r13 = r0.cache
                int r14 = r0.totalItems
                r13.put(r14, r10)
            L_0x00f2:
                android.util.SparseIntArray r13 = r0.positionToRow
                int r14 = r0.totalItems
                r13.put(r14, r5)
                r13 = 0
            L_0x00fa:
                int r14 = r10.size()
                if (r13 >= r14) goto L_0x012e
                int r14 = r13 + 1
                int r15 = r0.totalItems
                int r15 = r15 + r14
                android.util.SparseArray<java.lang.Object> r1 = r0.cache
                java.lang.Object r3 = r10.get(r13)
                r1.put(r15, r3)
                if (r6 == 0) goto L_0x0116
                android.util.SparseArray<java.lang.Object> r1 = r0.cacheParents
                r1.put(r15, r6)
                goto L_0x011b
            L_0x0116:
                android.util.SparseArray<java.lang.Object> r1 = r0.cacheParents
                r1.put(r15, r11)
            L_0x011b:
                android.util.SparseIntArray r1 = r0.positionToRow
                int r3 = r0.totalItems
                int r3 = r3 + r14
                int r15 = r5 + 1
                int r9 = r0.stickersPerRow
                int r13 = r13 / r9
                int r15 = r15 + r13
                r1.put(r3, r15)
                r13 = r14
                r1 = 0
                r3 = -3
                r9 = -1
                goto L_0x00fa
            L_0x012e:
                r1 = 0
            L_0x012f:
                int r3 = r12 + 1
                if (r1 >= r3) goto L_0x014e
                if (r6 == 0) goto L_0x013e
                android.util.SparseArray<java.lang.Object> r3 = r0.rowStartPack
                int r9 = r5 + r1
                r3.put(r9, r6)
                r10 = -1
                goto L_0x014b
            L_0x013e:
                android.util.SparseArray<java.lang.Object> r3 = r0.rowStartPack
                int r9 = r5 + r1
                r10 = -1
                if (r4 != r10) goto L_0x0147
                r11 = r8
                goto L_0x0148
            L_0x0147:
                r11 = r7
            L_0x0148:
                r3.put(r9, r11)
            L_0x014b:
                int r1 = r1 + 1
                goto L_0x012f
            L_0x014e:
                int r1 = r0.totalItems
                int r6 = r0.stickersPerRow
                int r12 = r12 * r6
                int r12 = r12 + 1
                int r1 = r1 + r12
                r0.totalItems = r1
                int r5 = r5 + r3
            L_0x015a:
                int r4 = r4 + 1
                r1 = 0
                r3 = -3
                goto L_0x004e
            L_0x0160:
                super.notifyDataSetChanged()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerMasksAlert.StickersGridAdapter.notifyDataSetChanged():void");
        }
    }

    private class StickersSearchGridAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public SparseArray<Object> cache = new SparseArray<>();
        private SparseArray<Object> cacheParent = new SparseArray<>();
        boolean cleared;
        private Context context;
        /* access modifiers changed from: private */
        public ArrayList<ArrayList<TLRPC$Document>> emojiArrays = new ArrayList<>();
        /* access modifiers changed from: private */
        public int emojiSearchId;
        /* access modifiers changed from: private */
        public HashMap<ArrayList<TLRPC$Document>, String> emojiStickers = new HashMap<>();
        /* access modifiers changed from: private */
        public ArrayList<TLRPC$TL_messages_stickerSet> localPacks = new ArrayList<>();
        /* access modifiers changed from: private */
        public HashMap<TLRPC$TL_messages_stickerSet, Integer> localPacksByName = new HashMap<>();
        /* access modifiers changed from: private */
        public HashMap<TLRPC$TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap<>();
        private SparseArray<String> positionToEmoji = new SparseArray<>();
        private SparseIntArray positionToRow = new SparseIntArray();
        /* access modifiers changed from: private */
        public int reqId2;
        private SparseArray<Object> rowStartPack = new SparseArray<>();
        /* access modifiers changed from: private */
        public String searchQuery;
        private Runnable searchRunnable = new Runnable() {
            private void clear() {
                StickersSearchGridAdapter stickersSearchGridAdapter = StickersSearchGridAdapter.this;
                if (!stickersSearchGridAdapter.cleared) {
                    stickersSearchGridAdapter.cleared = true;
                    stickersSearchGridAdapter.emojiStickers.clear();
                    StickersSearchGridAdapter.this.emojiArrays.clear();
                    StickersSearchGridAdapter.this.localPacks.clear();
                    StickersSearchGridAdapter.this.localPacksByShortName.clear();
                    StickersSearchGridAdapter.this.localPacksByName.clear();
                }
            }

            /* JADX WARNING: Code restructure failed: missing block: B:14:0x006c, code lost:
                if (r5.charAt(r9) <= 57343) goto L_0x0088;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:20:0x0086, code lost:
                if (r5.charAt(r9) != 9794) goto L_0x00a3;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r13 = this;
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r0 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.lang.String r0 = r0.searchQuery
                    boolean r0 = android.text.TextUtils.isEmpty(r0)
                    if (r0 == 0) goto L_0x000d
                    return
                L_0x000d:
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r0 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    r1 = 0
                    r0.cleared = r1
                    int r0 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.access$4904(r0)
                    java.util.ArrayList r2 = new java.util.ArrayList
                    r2.<init>(r1)
                    android.util.LongSparseArray r3 = new android.util.LongSparseArray
                    r3.<init>(r1)
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r4 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.StickerMasksAlert r4 = org.telegram.ui.Components.StickerMasksAlert.this
                    int r4 = r4.currentAccount
                    org.telegram.messenger.MediaDataController r4 = org.telegram.messenger.MediaDataController.getInstance(r4)
                    java.util.HashMap r4 = r4.getAllStickers()
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r5 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.lang.String r5 = r5.searchQuery
                    int r5 = r5.length()
                    r6 = 14
                    r7 = 1
                    if (r5 > r6) goto L_0x0113
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r5 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.lang.String r5 = r5.searchQuery
                    int r6 = r5.length()
                    r8 = 0
                L_0x004a:
                    if (r8 >= r6) goto L_0x00ca
                    int r9 = r6 + -1
                    r10 = 2
                    if (r8 >= r9) goto L_0x00a3
                    char r9 = r5.charAt(r8)
                    r11 = 55356(0xd83c, float:7.757E-41)
                    if (r9 != r11) goto L_0x006e
                    int r9 = r8 + 1
                    char r11 = r5.charAt(r9)
                    r12 = 57339(0xdffb, float:8.0349E-41)
                    if (r11 < r12) goto L_0x006e
                    char r9 = r5.charAt(r9)
                    r11 = 57343(0xdfff, float:8.0355E-41)
                    if (r9 <= r11) goto L_0x0088
                L_0x006e:
                    char r9 = r5.charAt(r8)
                    r11 = 8205(0x200d, float:1.1498E-41)
                    if (r9 != r11) goto L_0x00a3
                    int r9 = r8 + 1
                    char r11 = r5.charAt(r9)
                    r12 = 9792(0x2640, float:1.3722E-41)
                    if (r11 == r12) goto L_0x0088
                    char r9 = r5.charAt(r9)
                    r11 = 9794(0x2642, float:1.3724E-41)
                    if (r9 != r11) goto L_0x00a3
                L_0x0088:
                    java.lang.CharSequence[] r9 = new java.lang.CharSequence[r10]
                    java.lang.CharSequence r10 = r5.subSequence(r1, r8)
                    r9[r1] = r10
                    int r10 = r8 + 2
                    int r11 = r5.length()
                    java.lang.CharSequence r5 = r5.subSequence(r10, r11)
                    r9[r7] = r5
                    java.lang.CharSequence r5 = android.text.TextUtils.concat(r9)
                    int r6 = r6 + -2
                    goto L_0x00c6
                L_0x00a3:
                    char r9 = r5.charAt(r8)
                    r11 = 65039(0xfe0f, float:9.1139E-41)
                    if (r9 != r11) goto L_0x00c8
                    java.lang.CharSequence[] r9 = new java.lang.CharSequence[r10]
                    java.lang.CharSequence r10 = r5.subSequence(r1, r8)
                    r9[r1] = r10
                    int r10 = r8 + 1
                    int r11 = r5.length()
                    java.lang.CharSequence r5 = r5.subSequence(r10, r11)
                    r9[r7] = r5
                    java.lang.CharSequence r5 = android.text.TextUtils.concat(r9)
                    int r6 = r6 + -1
                L_0x00c6:
                    int r8 = r8 + -1
                L_0x00c8:
                    int r8 = r8 + r7
                    goto L_0x004a
                L_0x00ca:
                    if (r4 == 0) goto L_0x00d7
                    java.lang.String r5 = r5.toString()
                    java.lang.Object r5 = r4.get(r5)
                    java.util.ArrayList r5 = (java.util.ArrayList) r5
                    goto L_0x00d8
                L_0x00d7:
                    r5 = 0
                L_0x00d8:
                    if (r5 == 0) goto L_0x0113
                    boolean r6 = r5.isEmpty()
                    if (r6 != 0) goto L_0x0113
                    r13.clear()
                    r2.addAll(r5)
                    int r6 = r5.size()
                    r8 = 0
                L_0x00eb:
                    if (r8 >= r6) goto L_0x00fb
                    java.lang.Object r9 = r5.get(r8)
                    org.telegram.tgnet.TLRPC$Document r9 = (org.telegram.tgnet.TLRPC$Document) r9
                    long r10 = r9.id
                    r3.put(r10, r9)
                    int r8 = r8 + 1
                    goto L_0x00eb
                L_0x00fb:
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r5 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.util.HashMap r5 = r5.emojiStickers
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r6 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.lang.String r6 = r6.searchQuery
                    r5.put(r2, r6)
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r5 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.util.ArrayList r5 = r5.emojiArrays
                    r5.add(r2)
                L_0x0113:
                    if (r4 == 0) goto L_0x0173
                    boolean r5 = r4.isEmpty()
                    if (r5 != 0) goto L_0x0173
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r5 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.lang.String r5 = r5.searchQuery
                    int r5 = r5.length()
                    if (r5 <= r7) goto L_0x0173
                    java.lang.String[] r5 = org.telegram.messenger.AndroidUtilities.getCurrentKeyboardLanguage()
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r6 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.StickerMasksAlert r6 = org.telegram.ui.Components.StickerMasksAlert.this
                    java.lang.String[] r6 = r6.lastSearchKeyboardLanguage
                    boolean r6 = java.util.Arrays.equals(r6, r5)
                    if (r6 != 0) goto L_0x0148
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r6 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.StickerMasksAlert r6 = org.telegram.ui.Components.StickerMasksAlert.this
                    int r6 = r6.currentAccount
                    org.telegram.messenger.MediaDataController r6 = org.telegram.messenger.MediaDataController.getInstance(r6)
                    r6.fetchNewEmojiKeywords(r5)
                L_0x0148:
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r6 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.StickerMasksAlert r6 = org.telegram.ui.Components.StickerMasksAlert.this
                    java.lang.String[] unused = r6.lastSearchKeyboardLanguage = r5
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r5 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.StickerMasksAlert r5 = org.telegram.ui.Components.StickerMasksAlert.this
                    int r5 = r5.currentAccount
                    org.telegram.messenger.MediaDataController r6 = org.telegram.messenger.MediaDataController.getInstance(r5)
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r5 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.StickerMasksAlert r5 = org.telegram.ui.Components.StickerMasksAlert.this
                    java.lang.String[] r7 = r5.lastSearchKeyboardLanguage
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r5 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.lang.String r8 = r5.searchQuery
                    r9 = 0
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter$1$$ExternalSyntheticLambda1 r10 = new org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter$1$$ExternalSyntheticLambda1
                    r10.<init>(r13, r0, r4)
                    r11 = 0
                    r6.getEmojiSuggestions(r7, r8, r9, r10, r11)
                L_0x0173:
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r0 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.StickerMasksAlert r0 = org.telegram.ui.Components.StickerMasksAlert.this
                    int r0 = r0.currentAccount
                    org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r4 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.StickerMasksAlert r4 = org.telegram.ui.Components.StickerMasksAlert.this
                    int r4 = r4.currentType
                    java.util.ArrayList r0 = r0.getStickerSets(r4)
                    int r4 = r0.size()
                    r5 = 0
                L_0x0190:
                    r6 = 32
                    if (r5 >= r4) goto L_0x020c
                    java.lang.Object r7 = r0.get(r5)
                    org.telegram.tgnet.TLRPC$TL_messages_stickerSet r7 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r7
                    org.telegram.tgnet.TLRPC$StickerSet r8 = r7.set
                    java.lang.String r8 = r8.title
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r9 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.lang.String r9 = r9.searchQuery
                    int r8 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r8, r9)
                    if (r8 < 0) goto L_0x01d2
                    if (r8 == 0) goto L_0x01b8
                    org.telegram.tgnet.TLRPC$StickerSet r9 = r7.set
                    java.lang.String r9 = r9.title
                    int r10 = r8 + -1
                    char r9 = r9.charAt(r10)
                    if (r9 != r6) goto L_0x0209
                L_0x01b8:
                    r13.clear()
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r6 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.util.ArrayList r6 = r6.localPacks
                    r6.add(r7)
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r6 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.util.HashMap r6 = r6.localPacksByName
                    java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
                    r6.put(r7, r8)
                    goto L_0x0209
                L_0x01d2:
                    org.telegram.tgnet.TLRPC$StickerSet r8 = r7.set
                    java.lang.String r8 = r8.short_name
                    if (r8 == 0) goto L_0x0209
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r9 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.lang.String r9 = r9.searchQuery
                    int r8 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r8, r9)
                    if (r8 < 0) goto L_0x0209
                    if (r8 == 0) goto L_0x01f2
                    org.telegram.tgnet.TLRPC$StickerSet r9 = r7.set
                    java.lang.String r9 = r9.short_name
                    int r8 = r8 + -1
                    char r8 = r9.charAt(r8)
                    if (r8 != r6) goto L_0x0209
                L_0x01f2:
                    r13.clear()
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r6 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.util.ArrayList r6 = r6.localPacks
                    r6.add(r7)
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r6 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.util.HashMap r6 = r6.localPacksByShortName
                    java.lang.Boolean r8 = java.lang.Boolean.TRUE
                    r6.put(r7, r8)
                L_0x0209:
                    int r5 = r5 + 1
                    goto L_0x0190
                L_0x020c:
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r0 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.StickerMasksAlert r0 = org.telegram.ui.Components.StickerMasksAlert.this
                    int r0 = r0.currentAccount
                    org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
                    r4 = 3
                    java.util.ArrayList r0 = r0.getStickerSets(r4)
                    int r4 = r0.size()
                L_0x0221:
                    if (r1 >= r4) goto L_0x029b
                    java.lang.Object r5 = r0.get(r1)
                    org.telegram.tgnet.TLRPC$TL_messages_stickerSet r5 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r5
                    org.telegram.tgnet.TLRPC$StickerSet r7 = r5.set
                    java.lang.String r7 = r7.title
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r8 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.lang.String r8 = r8.searchQuery
                    int r7 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r7, r8)
                    if (r7 < 0) goto L_0x0261
                    if (r7 == 0) goto L_0x0247
                    org.telegram.tgnet.TLRPC$StickerSet r8 = r5.set
                    java.lang.String r8 = r8.title
                    int r9 = r7 + -1
                    char r8 = r8.charAt(r9)
                    if (r8 != r6) goto L_0x0298
                L_0x0247:
                    r13.clear()
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r8 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.util.ArrayList r8 = r8.localPacks
                    r8.add(r5)
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r8 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.util.HashMap r8 = r8.localPacksByName
                    java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
                    r8.put(r5, r7)
                    goto L_0x0298
                L_0x0261:
                    org.telegram.tgnet.TLRPC$StickerSet r7 = r5.set
                    java.lang.String r7 = r7.short_name
                    if (r7 == 0) goto L_0x0298
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r8 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.lang.String r8 = r8.searchQuery
                    int r7 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r7, r8)
                    if (r7 < 0) goto L_0x0298
                    if (r7 == 0) goto L_0x0281
                    org.telegram.tgnet.TLRPC$StickerSet r8 = r5.set
                    java.lang.String r8 = r8.short_name
                    int r7 = r7 + -1
                    char r7 = r8.charAt(r7)
                    if (r7 != r6) goto L_0x0298
                L_0x0281:
                    r13.clear()
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r7 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.util.ArrayList r7 = r7.localPacks
                    r7.add(r5)
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r7 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.util.HashMap r7 = r7.localPacksByShortName
                    java.lang.Boolean r8 = java.lang.Boolean.TRUE
                    r7.put(r5, r8)
                L_0x0298:
                    int r1 = r1 + 1
                    goto L_0x0221
                L_0x029b:
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r0 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.lang.String r0 = r0.searchQuery
                    boolean r0 = org.telegram.messenger.Emoji.isValidEmoji(r0)
                    if (r0 == 0) goto L_0x02df
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r1 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.StickerMasksAlert r1 = org.telegram.ui.Components.StickerMasksAlert.this
                    org.telegram.ui.Components.StickerMasksAlert$SearchField r1 = r1.stickersSearchField
                    org.telegram.ui.Components.CloseProgressDrawable2 r1 = r1.progressDrawable
                    r1.startAnimation()
                    org.telegram.tgnet.TLRPC$TL_messages_getStickers r1 = new org.telegram.tgnet.TLRPC$TL_messages_getStickers
                    r1.<init>()
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r4 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.lang.String r4 = r4.searchQuery
                    r1.emoticon = r4
                    r4 = 0
                    r1.hash = r4
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r4 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.StickerMasksAlert r5 = org.telegram.ui.Components.StickerMasksAlert.this
                    int r5 = r5.currentAccount
                    org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r5)
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter$1$$ExternalSyntheticLambda2 r6 = new org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter$1$$ExternalSyntheticLambda2
                    r6.<init>(r13, r1, r2, r3)
                    int r1 = r5.sendRequest(r1, r6)
                    int unused = r4.reqId2 = r1
                L_0x02df:
                    if (r0 == 0) goto L_0x02f9
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r0 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.util.ArrayList r0 = r0.localPacks
                    boolean r0 = r0.isEmpty()
                    if (r0 == 0) goto L_0x02f9
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r0 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    java.util.HashMap r0 = r0.emojiStickers
                    boolean r0 = r0.isEmpty()
                    if (r0 != 0) goto L_0x0322
                L_0x02f9:
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r0 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.StickerMasksAlert r0 = org.telegram.ui.Components.StickerMasksAlert.this
                    org.telegram.ui.Components.RecyclerListView r0 = r0.gridView
                    androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r1 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.StickerMasksAlert r1 = org.telegram.ui.Components.StickerMasksAlert.this
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r1 = r1.stickersSearchGridAdapter
                    if (r0 == r1) goto L_0x0322
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r0 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.StickerMasksAlert r0 = org.telegram.ui.Components.StickerMasksAlert.this
                    org.telegram.ui.Components.RecyclerListView r0 = r0.gridView
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r1 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    org.telegram.ui.Components.StickerMasksAlert r1 = org.telegram.ui.Components.StickerMasksAlert.this
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r1 = r1.stickersSearchGridAdapter
                    r0.setAdapter(r1)
                L_0x0322:
                    org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r0 = org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.this
                    r0.notifyDataSetChanged()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.AnonymousClass1.run():void");
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$run$0(int i, HashMap hashMap, ArrayList arrayList, String str) {
                if (i == StickersSearchGridAdapter.this.emojiSearchId) {
                    int size = arrayList.size();
                    boolean z = false;
                    for (int i2 = 0; i2 < size; i2++) {
                        String str2 = ((MediaDataController.KeywordResult) arrayList.get(i2)).emoji;
                        ArrayList arrayList2 = hashMap != null ? (ArrayList) hashMap.get(str2) : null;
                        if (arrayList2 != null && !arrayList2.isEmpty()) {
                            clear();
                            if (!StickersSearchGridAdapter.this.emojiStickers.containsKey(arrayList2)) {
                                StickersSearchGridAdapter.this.emojiStickers.put(arrayList2, str2);
                                StickersSearchGridAdapter.this.emojiArrays.add(arrayList2);
                                z = true;
                            }
                        }
                    }
                    if (z) {
                        StickersSearchGridAdapter.this.notifyDataSetChanged();
                    } else if (StickersSearchGridAdapter.this.reqId2 == 0) {
                        clear();
                        StickersSearchGridAdapter.this.notifyDataSetChanged();
                    }
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$run$2(TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers, ArrayList arrayList, LongSparseArray longSparseArray, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                AndroidUtilities.runOnUIThread(new StickerMasksAlert$StickersSearchGridAdapter$1$$ExternalSyntheticLambda0(this, tLRPC$TL_messages_getStickers, tLObject, arrayList, longSparseArray));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$run$1(TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers, TLObject tLObject, ArrayList arrayList, LongSparseArray longSparseArray) {
                if (tLRPC$TL_messages_getStickers.emoticon.equals(StickersSearchGridAdapter.this.searchQuery)) {
                    StickerMasksAlert.this.stickersSearchField.progressDrawable.stopAnimation();
                    int unused = StickersSearchGridAdapter.this.reqId2 = 0;
                    if (tLObject instanceof TLRPC$TL_messages_stickers) {
                        TLRPC$TL_messages_stickers tLRPC$TL_messages_stickers = (TLRPC$TL_messages_stickers) tLObject;
                        int size = arrayList.size();
                        int size2 = tLRPC$TL_messages_stickers.stickers.size();
                        for (int i = 0; i < size2; i++) {
                            TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickers.stickers.get(i);
                            if (longSparseArray.indexOfKey(tLRPC$Document.id) < 0) {
                                arrayList.add(tLRPC$Document);
                            }
                        }
                        if (size != arrayList.size()) {
                            StickersSearchGridAdapter.this.emojiStickers.put(arrayList, StickersSearchGridAdapter.this.searchQuery);
                            if (size == 0) {
                                StickersSearchGridAdapter.this.emojiArrays.add(arrayList);
                            }
                            StickersSearchGridAdapter.this.notifyDataSetChanged();
                        }
                        if (StickerMasksAlert.this.gridView.getAdapter() != StickerMasksAlert.this.stickersSearchGridAdapter) {
                            StickerMasksAlert.this.gridView.setAdapter(StickerMasksAlert.this.stickersSearchGridAdapter);
                        }
                    }
                }
            }
        };
        /* access modifiers changed from: private */
        public int totalItems;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        static /* synthetic */ int access$4904(StickersSearchGridAdapter stickersSearchGridAdapter) {
            int i = stickersSearchGridAdapter.emojiSearchId + 1;
            stickersSearchGridAdapter.emojiSearchId = i;
            return i;
        }

        public StickersSearchGridAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            int i = this.totalItems;
            if (i != 1) {
                return i + 1;
            }
            return 2;
        }

        public void search(String str) {
            if (this.reqId2 != 0) {
                ConnectionsManager.getInstance(StickerMasksAlert.this.currentAccount).cancelRequest(this.reqId2, true);
                this.reqId2 = 0;
            }
            if (TextUtils.isEmpty(str)) {
                this.searchQuery = null;
                this.localPacks.clear();
                this.emojiStickers.clear();
                if (StickerMasksAlert.this.gridView.getAdapter() != StickerMasksAlert.this.stickersGridAdapter) {
                    StickerMasksAlert.this.gridView.setAdapter(StickerMasksAlert.this.stickersGridAdapter);
                }
                notifyDataSetChanged();
            } else {
                this.searchQuery = str.toLowerCase();
            }
            AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
            AndroidUtilities.runOnUIThread(this.searchRunnable, 300);
        }

        public int getItemViewType(int i) {
            if (i == 0) {
                return 4;
            }
            if (i == 1 && this.totalItems == 1) {
                return 5;
            }
            Object obj = this.cache.get(i);
            if (obj != null) {
                return obj instanceof TLRPC$Document ? 0 : 2;
            }
            return 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            AnonymousClass3 r13;
            View view;
            if (i != 0) {
                if (i == 1) {
                    r13 = new EmptyCell(this.context);
                } else if (i == 2) {
                    view = new StickerSetNameCell(this.context, false, StickerMasksAlert.this.resourcesProvider);
                } else if (i == 4) {
                    View view2 = new View(this.context);
                    view2.setLayoutParams(new RecyclerView.LayoutParams(-1, StickerMasksAlert.this.searchFieldHeight + AndroidUtilities.dp(48.0f)));
                    r13 = view2;
                } else if (i != 5) {
                    r13 = null;
                } else {
                    AnonymousClass3 r132 = new FrameLayout(this.context) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int i, int i2) {
                            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(((StickerMasksAlert.this.gridView.getMeasuredHeight() - StickerMasksAlert.this.searchFieldHeight) - AndroidUtilities.dp(48.0f)) - AndroidUtilities.dp(48.0f), NUM));
                        }
                    };
                    ImageView imageView = new ImageView(this.context);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    imageView.setImageResource(NUM);
                    imageView.setColorFilter(new PorterDuffColorFilter(-7038047, PorterDuff.Mode.MULTIPLY));
                    r132.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 50.0f));
                    TextView textView = new TextView(this.context);
                    textView.setText(LocaleController.getString("NoStickersFound", NUM));
                    textView.setTextSize(1, 16.0f);
                    textView.setTextColor(-7038047);
                    r132.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
                    r132.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    r13 = r132;
                }
                return new RecyclerListView.Holder(r13);
            }
            view = new StickerEmojiCell(this, this.context, false) {
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                }
            };
            r13 = view;
            return new RecyclerListView.Holder(r13);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v12, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v13, resolved type: java.lang.Integer} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r10, int r11) {
            /*
                r9 = this;
                int r0 = r10.getItemViewType()
                r1 = 0
                r2 = 1
                if (r0 == 0) goto L_0x00fb
                r3 = 0
                if (r0 == r2) goto L_0x0074
                r2 = 2
                if (r0 == r2) goto L_0x0010
                goto L_0x0141
            L_0x0010:
                android.view.View r10 = r10.itemView
                org.telegram.ui.Cells.StickerSetNameCell r10 = (org.telegram.ui.Cells.StickerSetNameCell) r10
                android.util.SparseArray<java.lang.Object> r0 = r9.cache
                java.lang.Object r11 = r0.get(r11)
                boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
                if (r0 == 0) goto L_0x0141
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r11 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r11
                java.lang.String r0 = r9.searchQuery
                boolean r0 = android.text.TextUtils.isEmpty(r0)
                if (r0 != 0) goto L_0x0048
                java.util.HashMap<org.telegram.tgnet.TLRPC$TL_messages_stickerSet, java.lang.Boolean> r0 = r9.localPacksByShortName
                boolean r0 = r0.containsKey(r11)
                if (r0 == 0) goto L_0x0048
                org.telegram.tgnet.TLRPC$StickerSet r0 = r11.set
                if (r0 == 0) goto L_0x0039
                java.lang.String r0 = r0.title
                r10.setText(r0, r1)
            L_0x0039:
                org.telegram.tgnet.TLRPC$StickerSet r11 = r11.set
                java.lang.String r11 = r11.short_name
                java.lang.String r0 = r9.searchQuery
                int r0 = r0.length()
                r10.setUrl(r11, r0)
                goto L_0x0141
            L_0x0048:
                java.util.HashMap<org.telegram.tgnet.TLRPC$TL_messages_stickerSet, java.lang.Integer> r0 = r9.localPacksByName
                java.lang.Object r0 = r0.get(r11)
                java.lang.Integer r0 = (java.lang.Integer) r0
                org.telegram.tgnet.TLRPC$StickerSet r11 = r11.set
                if (r11 == 0) goto L_0x006f
                if (r0 == 0) goto L_0x006f
                java.lang.String r11 = r11.title
                int r0 = r0.intValue()
                java.lang.String r2 = r9.searchQuery
                boolean r2 = android.text.TextUtils.isEmpty(r2)
                if (r2 != 0) goto L_0x006b
                java.lang.String r2 = r9.searchQuery
                int r2 = r2.length()
                goto L_0x006c
            L_0x006b:
                r2 = 0
            L_0x006c:
                r10.setText(r11, r1, r0, r2)
            L_0x006f:
                r10.setUrl(r3, r1)
                goto L_0x0141
            L_0x0074:
                android.view.View r10 = r10.itemView
                org.telegram.ui.Cells.EmptyCell r10 = (org.telegram.ui.Cells.EmptyCell) r10
                int r0 = r9.totalItems
                r1 = 1118044160(0x42a40000, float:82.0)
                if (r11 != r0) goto L_0x00f3
                android.util.SparseIntArray r0 = r9.positionToRow
                int r11 = r11 - r2
                r4 = -2147483648(0xfffffffvar_, float:-0.0)
                int r11 = r0.get(r11, r4)
                if (r11 != r4) goto L_0x008e
                r10.setHeight(r2)
                goto L_0x0141
            L_0x008e:
                android.util.SparseArray<java.lang.Object> r0 = r9.rowStartPack
                java.lang.Object r11 = r0.get(r11)
                boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
                if (r0 == 0) goto L_0x00a5
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r11 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r11
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r11 = r11.documents
                int r11 = r11.size()
                java.lang.Integer r3 = java.lang.Integer.valueOf(r11)
                goto L_0x00ac
            L_0x00a5:
                boolean r0 = r11 instanceof java.lang.Integer
                if (r0 == 0) goto L_0x00ac
                r3 = r11
                java.lang.Integer r3 = (java.lang.Integer) r3
            L_0x00ac:
                if (r3 != 0) goto L_0x00b3
                r10.setHeight(r2)
                goto L_0x0141
            L_0x00b3:
                int r11 = r3.intValue()
                if (r11 != 0) goto L_0x00c4
                r11 = 1090519040(0x41000000, float:8.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r10.setHeight(r11)
                goto L_0x0141
            L_0x00c4:
                org.telegram.ui.Components.StickerMasksAlert r11 = org.telegram.ui.Components.StickerMasksAlert.this
                org.telegram.ui.Components.RecyclerListView r11 = r11.gridView
                int r11 = r11.getHeight()
                int r0 = r3.intValue()
                float r0 = (float) r0
                org.telegram.ui.Components.StickerMasksAlert r3 = org.telegram.ui.Components.StickerMasksAlert.this
                org.telegram.ui.Components.StickerMasksAlert$StickersGridAdapter r3 = r3.stickersGridAdapter
                int r3 = r3.stickersPerRow
                float r3 = (float) r3
                float r0 = r0 / r3
                double r3 = (double) r0
                double r3 = java.lang.Math.ceil(r3)
                int r0 = (int) r3
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r0 = r0 * r1
                int r11 = r11 - r0
                if (r11 <= 0) goto L_0x00ef
                r2 = r11
            L_0x00ef:
                r10.setHeight(r2)
                goto L_0x0141
            L_0x00f3:
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r10.setHeight(r11)
                goto L_0x0141
            L_0x00fb:
                android.util.SparseArray<java.lang.Object> r0 = r9.cache
                java.lang.Object r0 = r0.get(r11)
                org.telegram.tgnet.TLRPC$Document r0 = (org.telegram.tgnet.TLRPC$Document) r0
                android.view.View r10 = r10.itemView
                org.telegram.ui.Cells.StickerEmojiCell r10 = (org.telegram.ui.Cells.StickerEmojiCell) r10
                r5 = 0
                android.util.SparseArray<java.lang.Object> r3 = r9.cacheParent
                java.lang.Object r6 = r3.get(r11)
                android.util.SparseArray<java.lang.String> r3 = r9.positionToEmoji
                java.lang.Object r11 = r3.get(r11)
                r7 = r11
                java.lang.String r7 = (java.lang.String) r7
                r8 = 0
                r3 = r10
                r4 = r0
                r3.setSticker(r4, r5, r6, r7, r8)
                org.telegram.ui.Components.StickerMasksAlert r11 = org.telegram.ui.Components.StickerMasksAlert.this
                java.util.ArrayList[] r11 = r11.recentStickers
                org.telegram.ui.Components.StickerMasksAlert r3 = org.telegram.ui.Components.StickerMasksAlert.this
                int r3 = r3.currentType
                r11 = r11[r3]
                boolean r11 = r11.contains(r0)
                if (r11 != 0) goto L_0x013d
                org.telegram.ui.Components.StickerMasksAlert r11 = org.telegram.ui.Components.StickerMasksAlert.this
                java.util.ArrayList r11 = r11.favouriteStickers
                boolean r11 = r11.contains(r0)
                if (r11 == 0) goto L_0x013e
            L_0x013d:
                r1 = 1
            L_0x013e:
                r10.setRecent(r1)
            L_0x0141:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public void notifyDataSetChanged() {
            int i;
            this.rowStartPack.clear();
            this.positionToRow.clear();
            this.cache.clear();
            this.positionToEmoji.clear();
            this.totalItems = 0;
            int size = this.localPacks.size();
            boolean z = !this.emojiArrays.isEmpty();
            int i2 = -1;
            int i3 = -1;
            int i4 = 0;
            while (i3 < size + (z ? 1 : 0)) {
                if (i3 == i2) {
                    SparseArray<Object> sparseArray = this.cache;
                    int i5 = this.totalItems;
                    this.totalItems = i5 + 1;
                    sparseArray.put(i5, "search");
                    i4++;
                } else if (i3 < size) {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.localPacks.get(i3);
                    ArrayList<TLRPC$Document> arrayList = tLRPC$TL_messages_stickerSet.documents;
                    if (!arrayList.isEmpty()) {
                        int ceil = (int) Math.ceil((double) (((float) arrayList.size()) / ((float) StickerMasksAlert.this.stickersGridAdapter.stickersPerRow)));
                        this.cache.put(this.totalItems, tLRPC$TL_messages_stickerSet);
                        this.positionToRow.put(this.totalItems, i4);
                        int size2 = arrayList.size();
                        int i6 = 0;
                        while (i6 < size2) {
                            int i7 = i6 + 1;
                            int i8 = this.totalItems + i7;
                            int access$2700 = i4 + 1 + (i6 / StickerMasksAlert.this.stickersGridAdapter.stickersPerRow);
                            this.cache.put(i8, arrayList.get(i6));
                            this.cacheParent.put(i8, tLRPC$TL_messages_stickerSet);
                            this.positionToRow.put(i8, access$2700);
                            i6 = i7;
                        }
                        int i9 = ceil + 1;
                        for (int i10 = 0; i10 < i9; i10++) {
                            this.rowStartPack.put(i4 + i10, tLRPC$TL_messages_stickerSet);
                        }
                        this.totalItems += (ceil * StickerMasksAlert.this.stickersGridAdapter.stickersPerRow) + 1;
                        i4 += i9;
                    }
                } else {
                    int size3 = this.emojiArrays.size();
                    String str = "";
                    int i11 = 0;
                    for (int i12 = 0; i12 < size3; i12++) {
                        ArrayList arrayList2 = this.emojiArrays.get(i12);
                        String str2 = this.emojiStickers.get(arrayList2);
                        if (str2 != null && !str.equals(str2)) {
                            this.positionToEmoji.put(this.totalItems + i11, str2);
                            str = str2;
                        }
                        int size4 = arrayList2.size();
                        int i13 = 0;
                        while (i13 < size4) {
                            int i14 = this.totalItems + i11;
                            int access$27002 = (i11 / StickerMasksAlert.this.stickersGridAdapter.stickersPerRow) + i4;
                            TLRPC$Document tLRPC$Document = (TLRPC$Document) arrayList2.get(i13);
                            this.cache.put(i14, tLRPC$Document);
                            int i15 = size;
                            TLRPC$TL_messages_stickerSet stickerSetById = MediaDataController.getInstance(StickerMasksAlert.this.currentAccount).getStickerSetById(MediaDataController.getStickerSetId(tLRPC$Document));
                            if (stickerSetById != null) {
                                this.cacheParent.put(i14, stickerSetById);
                            }
                            this.positionToRow.put(i14, access$27002);
                            i11++;
                            i13++;
                            size = i15;
                        }
                        int i16 = size;
                    }
                    i = size;
                    int ceil2 = (int) Math.ceil((double) (((float) i11) / ((float) StickerMasksAlert.this.stickersGridAdapter.stickersPerRow)));
                    for (int i17 = 0; i17 < ceil2; i17++) {
                        this.rowStartPack.put(i4 + i17, Integer.valueOf(i11));
                    }
                    this.totalItems += StickerMasksAlert.this.stickersGridAdapter.stickersPerRow * ceil2;
                    i4 += ceil2;
                    i3++;
                    size = i;
                    i2 = -1;
                }
                i = size;
                i3++;
                size = i;
                i2 = -1;
            }
            super.notifyDataSetChanged();
        }
    }
}
