package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Cells.StickerSetNameCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ContentPreviewViewer;

public class StickerMasksAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private FrameLayout bottomTabContainer;
    private AnimatorSet bottomTabContainerAnimation;
    /* access modifiers changed from: private */
    public ContentPreviewViewer.ContentPreviewViewerDelegate contentPreviewViewerDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() {
        public /* synthetic */ String getQuery(boolean z) {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$getQuery(this, z);
        }

        public /* synthetic */ void gifAddedOrDeleted() {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$gifAddedOrDeleted(this);
        }

        public /* synthetic */ boolean needOpen() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needOpen(this);
        }

        public /* synthetic */ boolean needRemove() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needRemove(this);
        }

        public /* synthetic */ void remove(SendMessagesHelper.ImportingSticker importingSticker) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$remove(this, importingSticker);
        }

        public /* synthetic */ void sendGif(Object obj, Object obj2, boolean z, int i) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendGif(this, obj, obj2, z, i);
        }

        public void sendSticker(TLRPC.Document sticker, String query, Object parent, boolean notify, int scheduleDate) {
            StickerMasksAlert.this.delegate.onStickerSelected(parent, sticker);
        }

        public boolean needSend() {
            return false;
        }

        public boolean canSchedule() {
            return false;
        }

        public boolean isInScheduleMode() {
            return false;
        }

        public void openSet(TLRPC.InputStickerSet set, boolean clearsInputField) {
        }

        public long getDialogId() {
            return 0;
        }

        public boolean needMenu() {
            return false;
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
    public ArrayList<TLRPC.Document> favouriteStickers = new ArrayList<>();
    /* access modifiers changed from: private */
    public RecyclerListView gridView;
    private int lastNotifyHeight;
    private int lastNotifyHeight2;
    private int lastNotifyWidth;
    /* access modifiers changed from: private */
    public String[] lastSearchKeyboardLanguage;
    private ImageView masksButton;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Document>[] recentStickers = {new ArrayList<>(), new ArrayList<>()};
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
    public ArrayList<TLRPC.TL_messages_stickerSet>[] stickerSets = {new ArrayList<>(), new ArrayList<>()};
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
        void onStickerSelected(Object obj, TLRPC.Document document);
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
        public SearchField(org.telegram.ui.Components.StickerMasksAlert r21, android.content.Context r22, int r23) {
            /*
                r20 = this;
                r0 = r20
                r1 = r21
                r2 = r22
                r3 = r23
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
                int r8 = r21.searchFieldHeight
                r7.<init>(r9, r8)
                r0.addView(r4, r7)
                android.view.View r7 = new android.view.View
                r7.<init>(r2)
                r8 = 1099956224(0x41900000, float:18.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r10 = -13224394(0xfffffffffvar_, float:-2.4220097E38)
                android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r8, r10)
                r7.setBackgroundDrawable(r8)
                r10 = -1
                r11 = 1108344832(0x42100000, float:36.0)
                r12 = 51
                r13 = 1096810496(0x41600000, float:14.0)
                r14 = 1096810496(0x41600000, float:14.0)
                r15 = 1096810496(0x41600000, float:14.0)
                r16 = 0
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
                r0.addView(r7, r8)
                android.widget.ImageView r8 = new android.widget.ImageView
                r8.<init>(r2)
                android.widget.ImageView$ScaleType r10 = android.widget.ImageView.ScaleType.CENTER
                r8.setScaleType(r10)
                r10 = 2131166141(0x7var_bd, float:1.7946519E38)
                r8.setImageResource(r10)
                android.graphics.PorterDuffColorFilter r10 = new android.graphics.PorterDuffColorFilter
                android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
                r12 = -8947849(0xfffffffffvar_, float:-3.2893961E38)
                r10.<init>(r12, r11)
                r8.setColorFilter(r10)
                r13 = 36
                r14 = 1108344832(0x42100000, float:36.0)
                r15 = 51
                r16 = 1098907648(0x41800000, float:16.0)
                r17 = 1096810496(0x41600000, float:14.0)
                r18 = 0
                r19 = 0
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
                r0.addView(r8, r10)
                android.widget.ImageView r10 = new android.widget.ImageView
                r10.<init>(r2)
                r0.clearSearchImageView = r10
                android.widget.ImageView$ScaleType r11 = android.widget.ImageView.ScaleType.CENTER
                r10.setScaleType(r11)
                android.widget.ImageView r10 = r0.clearSearchImageView
                org.telegram.ui.Components.StickerMasksAlert$SearchField$1 r11 = new org.telegram.ui.Components.StickerMasksAlert$SearchField$1
                r11.<init>(r1)
                r0.progressDrawable = r11
                r10.setImageDrawable(r11)
                org.telegram.ui.Components.CloseProgressDrawable2 r10 = r0.progressDrawable
                r11 = 1088421888(0x40e00000, float:7.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r10.setSide(r11)
                android.widget.ImageView r10 = r0.clearSearchImageView
                r11 = 1036831949(0x3dcccccd, float:0.1)
                r10.setScaleX(r11)
                android.widget.ImageView r10 = r0.clearSearchImageView
                r10.setScaleY(r11)
                android.widget.ImageView r10 = r0.clearSearchImageView
                r10.setAlpha(r5)
                android.widget.ImageView r5 = r0.clearSearchImageView
                r15 = 53
                r16 = 1096810496(0x41600000, float:14.0)
                r18 = 1096810496(0x41600000, float:14.0)
                android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
                r0.addView(r5, r10)
                android.widget.ImageView r5 = r0.clearSearchImageView
                org.telegram.ui.Components.StickerMasksAlert$SearchField$$ExternalSyntheticLambda0 r10 = new org.telegram.ui.Components.StickerMasksAlert$SearchField$$ExternalSyntheticLambda0
                r10.<init>(r0)
                r5.setOnClickListener(r10)
                org.telegram.ui.Components.StickerMasksAlert$SearchField$2 r5 = new org.telegram.ui.Components.StickerMasksAlert$SearchField$2
                r5.<init>(r2, r1)
                r0.searchEditText = r5
                r10 = 1098907648(0x41800000, float:16.0)
                r5.setTextSize(r6, r10)
                org.telegram.ui.Components.EditTextBoldCursor r5 = r0.searchEditText
                r5.setHintTextColor(r12)
                org.telegram.ui.Components.EditTextBoldCursor r5 = r0.searchEditText
                r5.setTextColor(r9)
                org.telegram.ui.Components.EditTextBoldCursor r5 = r0.searchEditText
                r10 = 0
                r5.setBackgroundDrawable(r10)
                org.telegram.ui.Components.EditTextBoldCursor r5 = r0.searchEditText
                r10 = 0
                r5.setPadding(r10, r10, r10, r10)
                org.telegram.ui.Components.EditTextBoldCursor r5 = r0.searchEditText
                r5.setMaxLines(r6)
                org.telegram.ui.Components.EditTextBoldCursor r5 = r0.searchEditText
                r5.setLines(r6)
                org.telegram.ui.Components.EditTextBoldCursor r5 = r0.searchEditText
                r5.setSingleLine(r6)
                org.telegram.ui.Components.EditTextBoldCursor r5 = r0.searchEditText
                r10 = 268435459(0x10000003, float:2.5243558E-29)
                r5.setImeOptions(r10)
                if (r3 != 0) goto L_0x0144
                org.telegram.ui.Components.EditTextBoldCursor r5 = r0.searchEditText
                r6 = 2131627911(0x7f0e0var_, float:1.88831E38)
                java.lang.String r10 = "SearchStickersHint"
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r6)
                r5.setHint(r6)
                goto L_0x0166
            L_0x0144:
                if (r3 != r6) goto L_0x0155
                org.telegram.ui.Components.EditTextBoldCursor r5 = r0.searchEditText
                r6 = 2131627885(0x7f0e0f6d, float:1.8883047E38)
                java.lang.String r10 = "SearchEmojiHint"
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r6)
                r5.setHint(r6)
                goto L_0x0166
            L_0x0155:
                r5 = 2
                if (r3 != r5) goto L_0x0166
                org.telegram.ui.Components.EditTextBoldCursor r5 = r0.searchEditText
                r6 = 2131627901(0x7f0e0f7d, float:1.888308E38)
                java.lang.String r10 = "SearchGifsTitle"
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r6)
                r5.setHint(r6)
            L_0x0166:
                org.telegram.ui.Components.EditTextBoldCursor r5 = r0.searchEditText
                r5.setCursorColor(r9)
                org.telegram.ui.Components.EditTextBoldCursor r5 = r0.searchEditText
                r6 = 1101004800(0x41a00000, float:20.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                r5.setCursorSize(r6)
                org.telegram.ui.Components.EditTextBoldCursor r5 = r0.searchEditText
                r6 = 1069547520(0x3fCLASSNAME, float:1.5)
                r5.setCursorWidth(r6)
                org.telegram.ui.Components.EditTextBoldCursor r5 = r0.searchEditText
                r9 = -1
                r10 = 1109393408(0x42200000, float:40.0)
                r11 = 51
                r12 = 1113063424(0x42580000, float:54.0)
                r13 = 1094713344(0x41400000, float:12.0)
                r14 = 1110966272(0x42380000, float:46.0)
                r15 = 0
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
                r0.addView(r5, r6)
                org.telegram.ui.Components.EditTextBoldCursor r5 = r0.searchEditText
                org.telegram.ui.Components.StickerMasksAlert$SearchField$3 r6 = new org.telegram.ui.Components.StickerMasksAlert$SearchField$3
                r6.<init>(r1)
                r5.addTextChangedListener(r6)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerMasksAlert.SearchField.<init>(org.telegram.ui.Components.StickerMasksAlert, android.content.Context, int):void");
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-StickerMasksAlert$SearchField  reason: not valid java name */
        public /* synthetic */ void m4413xef7b9934(View v) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }

        /* access modifiers changed from: private */
        public void showShadow(boolean show, boolean animated) {
            if (show && this.shadowView.getTag() == null) {
                return;
            }
            if (show || this.shadowView.getTag() == null) {
                AnimatorSet animatorSet = this.shadowAnimator;
                int i = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.shadowAnimator = null;
                }
                View view = this.shadowView;
                if (!show) {
                    i = 1;
                }
                view.setTag(i);
                float f = 1.0f;
                if (animated) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.shadowAnimator = animatorSet2;
                    Animator[] animatorArr = new Animator[1];
                    View view2 = this.shadowView;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    if (!show) {
                        f = 0.0f;
                    }
                    fArr[0] = f;
                    animatorArr[0] = ObjectAnimator.ofFloat(view2, property, fArr);
                    animatorSet2.playTogether(animatorArr);
                    this.shadowAnimator.setDuration(200);
                    this.shadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.shadowAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            AnimatorSet unused = SearchField.this.shadowAnimator = null;
                        }
                    });
                    this.shadowAnimator.start();
                    return;
                }
                View view3 = this.shadowView;
                if (!show) {
                    f = 0.0f;
                }
                view3.setAlpha(f);
            }
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public StickerMasksAlert(android.content.Context r18, boolean r19, org.telegram.ui.ActionBar.Theme.ResourcesProvider r20) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = r20
            r3 = 1
            r0.<init>(r1, r3, r2)
            int r4 = org.telegram.messenger.UserConfig.selectedAccount
            r0.currentAccount = r4
            r4 = 2
            java.util.ArrayList[] r5 = new java.util.ArrayList[r4]
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r7 = 0
            r5[r7] = r6
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r5[r3] = r6
            r0.stickerSets = r5
            java.util.ArrayList[] r5 = new java.util.ArrayList[r4]
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r5[r7] = r6
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r5[r3] = r6
            r0.recentStickers = r5
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r0.favouriteStickers = r5
            r5 = -2
            r0.recentTabBum = r5
            r0.favTabBum = r5
            org.telegram.ui.Components.StickerMasksAlert$1 r6 = new org.telegram.ui.Components.StickerMasksAlert$1
            r6.<init>()
            r0.contentPreviewViewerDelegate = r6
            r6 = 0
            r0.behindKeyboardColorKey = r6
            r6 = -14342875(0xfffffffffvar_, float:-2.1951548E38)
            r0.behindKeyboardColor = r6
            r0.useLightStatusBar = r7
            r0.currentType = r7
            org.telegram.messenger.NotificationCenter r8 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r9 = org.telegram.messenger.NotificationCenter.emojiLoaded
            r8.addObserver(r0, r9)
            int r8 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r8 = org.telegram.messenger.NotificationCenter.getInstance(r8)
            int r9 = org.telegram.messenger.NotificationCenter.stickersDidLoad
            r8.addObserver(r0, r9)
            int r8 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r8 = org.telegram.messenger.NotificationCenter.getInstance(r8)
            int r9 = org.telegram.messenger.NotificationCenter.recentDocumentsDidLoad
            r8.addObserver(r0, r9)
            int r8 = r0.currentAccount
            org.telegram.messenger.MediaDataController r8 = org.telegram.messenger.MediaDataController.getInstance(r8)
            r8.loadRecents(r7, r7, r3, r7)
            int r8 = r0.currentAccount
            org.telegram.messenger.MediaDataController r8 = org.telegram.messenger.MediaDataController.getInstance(r8)
            r8.loadRecents(r3, r7, r3, r7)
            int r8 = r0.currentAccount
            org.telegram.messenger.MediaDataController r8 = org.telegram.messenger.MediaDataController.getInstance(r8)
            r8.loadRecents(r4, r7, r3, r7)
            android.content.res.Resources r8 = r18.getResources()
            r9 = 2131166129(0x7var_b1, float:1.7946495E38)
            android.graphics.drawable.Drawable r8 = r8.getDrawable(r9)
            android.graphics.drawable.Drawable r8 = r8.mutate()
            r0.shadowDrawable = r8
            android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.MULTIPLY
            r9.<init>(r6, r10)
            r8.setColorFilter(r9)
            org.telegram.ui.Components.StickerMasksAlert$2 r8 = new org.telegram.ui.Components.StickerMasksAlert$2
            r8.<init>(r1)
            r0.containerView = r8
            android.view.ViewGroup r8 = r0.containerView
            r8.setWillNotDraw(r7)
            android.view.ViewGroup r8 = r0.containerView
            int r9 = r0.backgroundPaddingLeft
            int r10 = r0.backgroundPaddingLeft
            r8.setPadding(r9, r7, r10, r7)
            r8 = 1115684864(0x42800000, float:64.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r0.searchFieldHeight = r8
            android.graphics.drawable.Drawable[] r4 = new android.graphics.drawable.Drawable[r4]
            r8 = 2131166174(0x7var_de, float:1.7946586E38)
            r9 = -11842741(0xffffffffff4b4b4b, float:-2.7022423E38)
            r10 = -9520403(0xffffffffff6ebaed, float:-3.1732684E38)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r8, r9, r10)
            r4[r7] = r8
            r8 = 2131166172(0x7var_dc, float:1.7946582E38)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r8, r9, r10)
            r4[r3] = r8
            r0.stickerIcons = r4
            int r4 = r0.currentAccount
            org.telegram.messenger.MediaDataController r4 = org.telegram.messenger.MediaDataController.getInstance(r4)
            r4.checkStickers(r7)
            int r4 = r0.currentAccount
            org.telegram.messenger.MediaDataController r4 = org.telegram.messenger.MediaDataController.getInstance(r4)
            r4.checkStickers(r3)
            int r4 = r0.currentAccount
            org.telegram.messenger.MediaDataController r4 = org.telegram.messenger.MediaDataController.getInstance(r4)
            r4.checkFeaturedStickers()
            org.telegram.ui.Components.StickerMasksAlert$3 r4 = new org.telegram.ui.Components.StickerMasksAlert$3
            r4.<init>(r1)
            r0.gridView = r4
            org.telegram.ui.Components.StickerMasksAlert$4 r8 = new org.telegram.ui.Components.StickerMasksAlert$4
            r9 = 5
            r8.<init>(r1, r9)
            r0.stickersLayoutManager = r8
            r4.setLayoutManager(r8)
            androidx.recyclerview.widget.GridLayoutManager r4 = r0.stickersLayoutManager
            org.telegram.ui.Components.StickerMasksAlert$5 r8 = new org.telegram.ui.Components.StickerMasksAlert$5
            r8.<init>()
            r4.setSpanSizeLookup(r8)
            org.telegram.ui.Components.RecyclerListView r4 = r0.gridView
            r8 = 1112539136(0x42500000, float:52.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r9 = 1111490560(0x42400000, float:48.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r4.setPadding(r7, r8, r7, r11)
            org.telegram.ui.Components.RecyclerListView r4 = r0.gridView
            r4.setClipToPadding(r7)
            org.telegram.ui.Components.RecyclerListView r4 = r0.gridView
            r4.setHorizontalScrollBarEnabled(r7)
            org.telegram.ui.Components.RecyclerListView r4 = r0.gridView
            r4.setVerticalScrollBarEnabled(r7)
            org.telegram.ui.Components.RecyclerListView r4 = r0.gridView
            r4.setGlowColor(r6)
            org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter r4 = new org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter
            r4.<init>(r1)
            r0.stickersSearchGridAdapter = r4
            org.telegram.ui.Components.RecyclerListView r4 = r0.gridView
            org.telegram.ui.Components.StickerMasksAlert$StickersGridAdapter r8 = new org.telegram.ui.Components.StickerMasksAlert$StickersGridAdapter
            r8.<init>(r1)
            r0.stickersGridAdapter = r8
            r4.setAdapter(r8)
            org.telegram.ui.Components.RecyclerListView r4 = r0.gridView
            org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda2 r8 = new org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda2
            r8.<init>(r0, r2)
            r4.setOnTouchListener(r8)
            org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda3 r4 = new org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda3
            r4.<init>(r0)
            r0.stickersOnItemClickListener = r4
            org.telegram.ui.Components.RecyclerListView r8 = r0.gridView
            r8.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r4)
            android.view.ViewGroup r4 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r8 = r0.gridView
            r11 = -1
            r12 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12)
            r4.addView(r8, r12)
            org.telegram.ui.Components.StickerMasksAlert$6 r4 = new org.telegram.ui.Components.StickerMasksAlert$6
            r4.<init>(r1, r2)
            r0.stickersTab = r4
            org.telegram.ui.Components.StickerMasksAlert$SearchField r4 = new org.telegram.ui.Components.StickerMasksAlert$SearchField
            r4.<init>(r0, r1, r7)
            r0.stickersSearchField = r4
            android.view.ViewGroup r4 = r0.containerView
            org.telegram.ui.Components.StickerMasksAlert$SearchField r8 = r0.stickersSearchField
            android.widget.FrameLayout$LayoutParams r12 = new android.widget.FrameLayout$LayoutParams
            int r13 = r0.searchFieldHeight
            int r14 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r13 = r13 + r14
            r12.<init>(r11, r13)
            r4.addView(r8, r12)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.stickersTab
            org.telegram.ui.Components.ScrollSlidingTabStrip$Type r8 = org.telegram.ui.Components.ScrollSlidingTabStrip.Type.TAB
            r4.setType(r8)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.stickersTab
            int r8 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r4.setUnderlineHeight(r8)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.stickersTab
            r4.setIndicatorColor(r10)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.stickersTab
            r8 = -16053493(0xffffffffff0b0b0b, float:-1.8482003E38)
            r4.setUnderlineColor(r8)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.stickersTab
            r4.setBackgroundColor(r6)
            android.view.ViewGroup r4 = r0.containerView
            org.telegram.ui.Components.ScrollSlidingTabStrip r8 = r0.stickersTab
            r12 = 48
            r13 = 51
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r11, (int) r12, (int) r13)
            r4.addView(r8, r13)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.stickersTab
            org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda4 r8 = new org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda4
            r8.<init>(r0)
            r4.setDelegate(r8)
            org.telegram.ui.Components.RecyclerListView r4 = r0.gridView
            org.telegram.ui.Components.StickerMasksAlert$7 r8 = new org.telegram.ui.Components.StickerMasksAlert$7
            r8.<init>()
            r4.setOnScrollListener(r8)
            android.view.View r4 = new android.view.View
            r4.<init>(r1)
            r8 = 2131165484(0x7var_c, float:1.7945186E38)
            r13 = -1907225(0xffffffffffe2e5e7, float:NaN)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r8, (int) r13)
            r4.setBackgroundDrawable(r8)
            android.view.ViewGroup r8 = r0.containerView
            r13 = 1086324736(0x40CLASSNAME, float:6.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r13)
            r8.addView(r4, r13)
            if (r19 != 0) goto L_0x02de
            org.telegram.ui.Components.StickerMasksAlert$8 r8 = new org.telegram.ui.Components.StickerMasksAlert$8
            r8.<init>(r1)
            r0.bottomTabContainer = r8
            android.view.View r8 = new android.view.View
            r8.<init>(r1)
            r0.shadowLine = r8
            r13 = 301989888(0x12000000, float:4.0389678E-28)
            r8.setBackgroundColor(r13)
            android.widget.FrameLayout r8 = r0.bottomTabContainer
            android.view.View r13 = r0.shadowLine
            android.widget.FrameLayout$LayoutParams r14 = new android.widget.FrameLayout$LayoutParams
            int r15 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r14.<init>(r11, r15)
            r8.addView(r13, r14)
            android.view.View r8 = new android.view.View
            r8.<init>(r1)
            r8.setBackgroundColor(r6)
            android.widget.FrameLayout r6 = r0.bottomTabContainer
            android.widget.FrameLayout$LayoutParams r13 = new android.widget.FrameLayout$LayoutParams
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r15 = 83
            r13.<init>(r11, r14, r15)
            r6.addView(r8, r13)
            android.view.ViewGroup r6 = r0.containerView
            android.widget.FrameLayout r13 = r0.bottomTabContainer
            android.widget.FrameLayout$LayoutParams r14 = new android.widget.FrameLayout$LayoutParams
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r16 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r9 = r9 + r16
            r14.<init>(r11, r9, r15)
            r6.addView(r13, r14)
            android.widget.LinearLayout r6 = new android.widget.LinearLayout
            r6.<init>(r1)
            r6.setOrientation(r7)
            android.widget.FrameLayout r7 = r0.bottomTabContainer
            r9 = 81
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r12, (int) r9)
            r7.addView(r6, r5)
            org.telegram.ui.Components.StickerMasksAlert$9 r5 = new org.telegram.ui.Components.StickerMasksAlert$9
            r5.<init>(r1)
            r0.stickersButton = r5
            android.widget.ImageView$ScaleType r7 = android.widget.ImageView.ScaleType.CENTER
            r5.setScaleType(r7)
            android.widget.ImageView r5 = r0.stickersButton
            r7 = 2131166159(0x7var_cf, float:1.7946555E38)
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r7, r11, r10)
            r5.setImageDrawable(r7)
            int r5 = android.os.Build.VERSION.SDK_INT
            r7 = 520093695(0x1effffff, float:2.7105053E-20)
            r9 = 21
            if (r5 < r9) goto L_0x028c
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r7)
            android.graphics.drawable.RippleDrawable r5 = (android.graphics.drawable.RippleDrawable) r5
            org.telegram.ui.ActionBar.Theme.setRippleDrawableForceSoftware(r5)
            android.widget.ImageView r13 = r0.stickersButton
            r13.setBackground(r5)
        L_0x028c:
            android.widget.ImageView r5 = r0.stickersButton
            r13 = 70
            android.widget.LinearLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r12)
            r6.addView(r5, r14)
            android.widget.ImageView r5 = r0.stickersButton
            org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda0 r14 = new org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda0
            r14.<init>(r0)
            r5.setOnClickListener(r14)
            org.telegram.ui.Components.StickerMasksAlert$10 r5 = new org.telegram.ui.Components.StickerMasksAlert$10
            r5.<init>(r1)
            r0.masksButton = r5
            android.widget.ImageView$ScaleType r14 = android.widget.ImageView.ScaleType.CENTER
            r5.setScaleType(r14)
            android.widget.ImageView r5 = r0.masksButton
            r14 = 2131165543(0x7var_, float:1.7945306E38)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r14, r11, r10)
            r5.setImageDrawable(r10)
            int r5 = android.os.Build.VERSION.SDK_INT
            if (r5 < r9) goto L_0x02cb
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r7)
            android.graphics.drawable.RippleDrawable r5 = (android.graphics.drawable.RippleDrawable) r5
            org.telegram.ui.ActionBar.Theme.setRippleDrawableForceSoftware(r5)
            android.widget.ImageView r7 = r0.masksButton
            r7.setBackground(r5)
        L_0x02cb:
            android.widget.ImageView r5 = r0.masksButton
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r12)
            r6.addView(r5, r7)
            android.widget.ImageView r5 = r0.masksButton
            org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda1 r7 = new org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda1
            r7.<init>(r0)
            r5.setOnClickListener(r7)
        L_0x02de:
            r0.checkDocuments(r3)
            r17.reloadStickersAdapter()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerMasksAlert.<init>(android.content.Context, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-StickerMasksAlert  reason: not valid java name */
    public /* synthetic */ boolean m4408lambda$new$0$orgtelegramuiComponentsStickerMasksAlert(Theme.ResourcesProvider resourcesProvider, View v, MotionEvent event) {
        return ContentPreviewViewer.getInstance().onTouch(event, this.gridView, this.containerView.getMeasuredHeight(), this.stickersOnItemClickListener, this.contentPreviewViewerDelegate, resourcesProvider);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-StickerMasksAlert  reason: not valid java name */
    public /* synthetic */ void m4409lambda$new$1$orgtelegramuiComponentsStickerMasksAlert(View view, int position) {
        if (view instanceof StickerEmojiCell) {
            ContentPreviewViewer.getInstance().reset();
            StickerEmojiCell cell = (StickerEmojiCell) view;
            this.delegate.onStickerSelected(cell.getParentObject(), cell.getSticker());
            dismiss();
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-StickerMasksAlert  reason: not valid java name */
    public /* synthetic */ void m4410lambda$new$2$orgtelegramuiComponentsStickerMasksAlert(int page) {
        int index;
        if (page == this.recentTabBum) {
            index = this.stickersGridAdapter.getPositionForPack("recent");
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            int i = this.recentTabBum;
            scrollSlidingTabStrip.onPageScrolled(i, i > 0 ? i : this.stickersTabOffset);
        } else if (page == this.favTabBum) {
            index = this.stickersGridAdapter.getPositionForPack("fav");
            ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
            int i2 = this.favTabBum;
            scrollSlidingTabStrip2.onPageScrolled(i2, i2 > 0 ? i2 : this.stickersTabOffset);
        } else {
            int index2 = page - this.stickersTabOffset;
            if (index2 < this.stickerSets[this.currentType].size()) {
                if (index2 >= this.stickerSets[this.currentType].size()) {
                    index2 = this.stickerSets[this.currentType].size() - 1;
                }
                index = this.stickersGridAdapter.getPositionForPack(this.stickerSets[this.currentType].get(index2));
            } else {
                return;
            }
        }
        if (this.stickersLayoutManager.findFirstVisibleItemPosition() != index) {
            this.stickersLayoutManager.scrollToPositionWithOffset(index, (-this.gridView.getPaddingTop()) + this.searchFieldHeight + AndroidUtilities.dp(48.0f));
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-StickerMasksAlert  reason: not valid java name */
    public /* synthetic */ void m4411lambda$new$3$orgtelegramuiComponentsStickerMasksAlert(View v) {
        if (this.currentType != 0) {
            this.currentType = 0;
            updateType();
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-StickerMasksAlert  reason: not valid java name */
    public /* synthetic */ void m4412lambda$new$4$orgtelegramuiComponentsStickerMasksAlert(View v) {
        if (this.currentType != 1) {
            this.currentType = 1;
            updateType();
        }
    }

    private int getCurrentTop() {
        if (this.gridView.getChildCount() == 0) {
            return -1000;
        }
        int i = 0;
        View child = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(child);
        if (holder == null) {
            return -1000;
        }
        int paddingTop = this.gridView.getPaddingTop();
        if (holder.getAdapterPosition() == 0 && child.getTop() >= 0) {
            i = child.getTop();
        }
        return paddingTop - i;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
        r0 = r5.gridView.getChildAt(0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateType() {
        /*
            r5 = this;
            org.telegram.ui.Components.RecyclerListView r0 = r5.gridView
            int r0 = r0.getChildCount()
            if (r0 <= 0) goto L_0x0036
            org.telegram.ui.Components.RecyclerListView r0 = r5.gridView
            r1 = 0
            android.view.View r0 = r0.getChildAt(r1)
            org.telegram.ui.Components.RecyclerListView r2 = r5.gridView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r2.findContainingViewHolder(r0)
            if (r2 == 0) goto L_0x0036
            int r3 = r2.getAdapterPosition()
            if (r3 == 0) goto L_0x0025
            org.telegram.ui.Components.RecyclerListView r3 = r5.gridView
            int r3 = r3.getPaddingTop()
            int r3 = -r3
            goto L_0x0031
        L_0x0025:
            org.telegram.ui.Components.RecyclerListView r3 = r5.gridView
            int r3 = r3.getPaddingTop()
            int r3 = -r3
            int r4 = r0.getTop()
            int r3 = r3 + r4
        L_0x0031:
            androidx.recyclerview.widget.GridLayoutManager r4 = r5.stickersLayoutManager
            r4.scrollToPositionWithOffset(r1, r3)
        L_0x0036:
            r0 = 1
            r5.checkDocuments(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerMasksAlert.updateType():void");
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public void setDelegate(StickerMasksAlertDelegate stickerMasksAlertDelegate) {
        this.delegate = stickerMasksAlertDelegate;
    }

    /* access modifiers changed from: private */
    public void updateLayout(boolean animated) {
        RecyclerListView.Holder holder;
        if (this.gridView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.gridView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        boolean z = false;
        View child = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder2 = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = AndroidUtilities.dp(7.0f);
        if (top >= AndroidUtilities.dp(7.0f) && holder2 != null && holder2.getAdapterPosition() == 0) {
            newOffset = top;
        }
        int newOffset2 = newOffset + (-AndroidUtilities.dp(11.0f));
        if (this.scrollOffsetY != newOffset2) {
            RecyclerListView recyclerListView2 = this.gridView;
            this.scrollOffsetY = newOffset2;
            recyclerListView2.setTopGlowOffset(newOffset2);
            this.stickersTab.setTranslationY((float) newOffset2);
            this.stickersSearchField.setTranslationY((float) (AndroidUtilities.dp(48.0f) + newOffset2));
            this.containerView.invalidate();
        }
        RecyclerListView.Holder holder3 = (RecyclerListView.Holder) this.gridView.findViewHolderForAdapterPosition(0);
        if (holder3 == null) {
            this.stickersSearchField.showShadow(true, animated);
        } else {
            SearchField searchField = this.stickersSearchField;
            if (holder3.itemView.getTop() < this.gridView.getPaddingTop()) {
                z = true;
            }
            searchField.showShadow(z, animated);
        }
        RecyclerView.Adapter adapter = this.gridView.getAdapter();
        StickersSearchGridAdapter stickersSearchGridAdapter2 = this.stickersSearchGridAdapter;
        if (adapter == stickersSearchGridAdapter2 && (holder = (RecyclerListView.Holder) this.gridView.findViewHolderForAdapterPosition(stickersSearchGridAdapter2.getItemCount() - 1)) != null && holder.getItemViewType() == 5) {
            FrameLayout layout = (FrameLayout) holder.itemView;
            int count = layout.getChildCount();
            float tr = (float) ((-((layout.getTop() - this.searchFieldHeight) - AndroidUtilities.dp(48.0f))) / 2);
            for (int a = 0; a < count; a++) {
                layout.getChildAt(a).setTranslationY(tr);
            }
        }
        checkPanels();
    }

    private void showBottomTab(boolean show, boolean animated) {
        if (show && this.bottomTabContainer.getTag() == null) {
            return;
        }
        if (show || this.bottomTabContainer.getTag() == null) {
            AnimatorSet animatorSet = this.bottomTabContainerAnimation;
            int i = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.bottomTabContainerAnimation = null;
            }
            FrameLayout frameLayout = this.bottomTabContainer;
            if (!show) {
                i = 1;
            }
            frameLayout.setTag(i);
            float f = 0.0f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.bottomTabContainerAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[2];
                FrameLayout frameLayout2 = this.bottomTabContainer;
                Property property = View.TRANSLATION_Y;
                float[] fArr = new float[1];
                fArr[0] = show ? 0.0f : (float) AndroidUtilities.dp(49.0f);
                animatorArr[0] = ObjectAnimator.ofFloat(frameLayout2, property, fArr);
                View view = this.shadowLine;
                Property property2 = View.TRANSLATION_Y;
                float[] fArr2 = new float[1];
                if (!show) {
                    f = (float) AndroidUtilities.dp(49.0f);
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
                animatorSet2.playTogether(animatorArr);
                this.bottomTabContainerAnimation.setDuration(200);
                this.bottomTabContainerAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.bottomTabContainerAnimation.start();
                return;
            }
            this.bottomTabContainer.setTranslationY(show ? 0.0f : (float) AndroidUtilities.dp(49.0f));
            View view2 = this.shadowLine;
            if (!show) {
                f = (float) AndroidUtilities.dp(49.0f);
            }
            view2.setTranslationY(f);
        }
    }

    private void updateStickerTabs() {
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
            int lastPosition = this.stickersTab.getCurrentPosition();
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
            ArrayList<TLRPC.TL_messages_stickerSet> packs = MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType);
            for (int a = 0; a < packs.size(); a++) {
                TLRPC.TL_messages_stickerSet pack = packs.get(a);
                if (!pack.set.archived && pack.documents != null && !pack.documents.isEmpty()) {
                    this.stickerSets[this.currentType].add(pack);
                }
            }
            for (int a2 = 0; a2 < this.stickerSets[this.currentType].size(); a2++) {
                TLRPC.TL_messages_stickerSet stickerSet = this.stickerSets[this.currentType].get(a2);
                TLRPC.Document document = (TLRPC.Document) stickerSet.documents.get(0);
                TLObject thumb = FileLoader.getClosestPhotoSizeWithSize(stickerSet.set.thumbs, 90);
                if (thumb == null) {
                    thumb = document;
                }
                View addStickerTab = this.stickersTab.addStickerTab(thumb, document, stickerSet);
                addStickerTab.setContentDescription(stickerSet.set.title + ", " + LocaleController.getString("AccDescrStickerSet", NUM));
            }
            this.stickersTab.commitUpdate();
            this.stickersTab.updateTabStyles();
            if (lastPosition != 0) {
                this.stickersTab.onPageScrolled(lastPosition, lastPosition);
            }
            checkPanels();
        }
    }

    private void checkPanels() {
        int firstTab;
        if (this.stickersTab != null) {
            int count = this.gridView.getChildCount();
            View child = null;
            for (int a = 0; a < count; a++) {
                child = this.gridView.getChildAt(a);
                if (child.getBottom() > this.searchFieldHeight + AndroidUtilities.dp(48.0f)) {
                    break;
                }
            }
            if (child != null) {
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(child);
                int position = holder != null ? holder.getAdapterPosition() : -1;
                if (position != -1) {
                    if (this.favTabBum > 0) {
                        firstTab = this.favTabBum;
                    } else if (this.recentTabBum > 0) {
                        firstTab = this.recentTabBum;
                    } else {
                        firstTab = this.stickersTabOffset;
                    }
                    this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(position), firstTab);
                }
            }
        }
    }

    public void addRecentSticker(TLRPC.Document document) {
        if (document != null) {
            MediaDataController.getInstance(this.currentAccount).addRecentSticker(this.currentType, (Object) null, document, (int) (System.currentTimeMillis() / 1000), false);
            boolean wasEmpty = this.recentStickers[this.currentType].isEmpty();
            this.recentStickers[this.currentType] = MediaDataController.getInstance(this.currentAccount).getRecentStickers(this.currentType);
            StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
            if (stickersGridAdapter2 != null) {
                stickersGridAdapter2.notifyDataSetChanged();
            }
            if (wasEmpty) {
                updateStickerTabs();
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

    private void checkDocuments(boolean force) {
        int previousCount = this.recentStickers[this.currentType].size();
        int previousCount2 = this.favouriteStickers.size();
        this.recentStickers[this.currentType] = MediaDataController.getInstance(this.currentAccount).getRecentStickers(this.currentType);
        this.favouriteStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(2);
        if (this.currentType == 0) {
            for (int a = 0; a < this.favouriteStickers.size(); a++) {
                TLRPC.Document favSticker = this.favouriteStickers.get(a);
                int b = 0;
                while (true) {
                    if (b >= this.recentStickers[this.currentType].size()) {
                        break;
                    }
                    TLRPC.Document recSticker = this.recentStickers[this.currentType].get(b);
                    if (recSticker.dc_id == favSticker.dc_id && recSticker.id == favSticker.id) {
                        this.recentStickers[this.currentType].remove(b);
                        break;
                    }
                    b++;
                }
            }
        }
        if (!(!force && previousCount == this.recentStickers[this.currentType].size() && previousCount2 == this.favouriteStickers.size())) {
            updateStickerTabs();
        }
        StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
        if (stickersGridAdapter2 != null) {
            stickersGridAdapter2.notifyDataSetChanged();
        }
        if (!force) {
            checkPanels();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        RecyclerListView recyclerListView;
        if (id == NotificationCenter.stickersDidLoad) {
            if (args[0].intValue() == this.currentType) {
                updateStickerTabs();
                reloadStickersAdapter();
                checkPanels();
            }
        } else if (id == NotificationCenter.recentDocumentsDidLoad) {
            boolean isGif = args[0].booleanValue();
            int type = args[1].intValue();
            if (isGif) {
                return;
            }
            if (type == this.currentType || type == 2) {
                checkDocuments(false);
            }
        } else if (id == NotificationCenter.emojiLoaded && (recyclerListView = this.gridView) != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.gridView.getChildAt(a);
                if ((child instanceof StickerSetNameCell) || (child instanceof StickerEmojiCell)) {
                    child.invalidate();
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

        public StickersGridAdapter(Context context2) {
            this.context = context2;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public int getItemCount() {
            int i = this.totalItems;
            if (i != 0) {
                return i + 1;
            }
            return 0;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public int getPositionForPack(Object pack) {
            Integer pos = this.packStartPosition.get(pack);
            if (pos == null) {
                return -1;
            }
            return pos.intValue();
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 4;
            }
            Object object = this.cache.get(position);
            if (object == null) {
                return 1;
            }
            if (object instanceof TLRPC.Document) {
                return 0;
            }
            return 2;
        }

        public int getTabForPosition(int position) {
            if (position == 0) {
                position = 1;
            }
            if (this.stickersPerRow == 0) {
                int width = StickerMasksAlert.this.gridView.getMeasuredWidth();
                if (width == 0) {
                    width = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
            }
            int row = this.positionToRow.get(position, Integer.MIN_VALUE);
            if (row == Integer.MIN_VALUE) {
                return (StickerMasksAlert.this.stickerSets[StickerMasksAlert.this.currentType].size() - 1) + StickerMasksAlert.this.stickersTabOffset;
            }
            Object pack = this.rowStartPack.get(row);
            if (!(pack instanceof String)) {
                return StickerMasksAlert.this.stickersTabOffset + StickerMasksAlert.this.stickerSets[StickerMasksAlert.this.currentType].indexOf((TLRPC.TL_messages_stickerSet) pack);
            } else if ("recent".equals(pack)) {
                return StickerMasksAlert.this.recentTabBum;
            } else {
                return StickerMasksAlert.this.favTabBum;
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new StickerEmojiCell(this.context, false) {
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                        }
                    };
                    break;
                case 1:
                    view = new EmptyCell(this.context);
                    break;
                case 2:
                    StickerSetNameCell cell = new StickerSetNameCell(this.context, false, StickerMasksAlert.this.resourcesProvider);
                    cell.setTitleColor(-7829368);
                    view = cell;
                    break;
                case 4:
                    view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, StickerMasksAlert.this.searchFieldHeight + AndroidUtilities.dp(48.0f)));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ArrayList<TLRPC.Document> documents;
            switch (holder.getItemViewType()) {
                case 0:
                    TLRPC.Document sticker = (TLRPC.Document) this.cache.get(position);
                    StickerEmojiCell cell = (StickerEmojiCell) holder.itemView;
                    cell.setSticker(sticker, this.cacheParents.get(position), false);
                    cell.setRecent(StickerMasksAlert.this.recentStickers[StickerMasksAlert.this.currentType].contains(sticker));
                    return;
                case 1:
                    EmptyCell cell2 = (EmptyCell) holder.itemView;
                    if (position == this.totalItems) {
                        int row = this.positionToRow.get(position - 1, Integer.MIN_VALUE);
                        int i = 1;
                        if (row == Integer.MIN_VALUE) {
                            cell2.setHeight(1);
                            return;
                        }
                        Object pack = this.rowStartPack.get(row);
                        if (pack instanceof TLRPC.TL_messages_stickerSet) {
                            documents = ((TLRPC.TL_messages_stickerSet) pack).documents;
                        } else if (!(pack instanceof String)) {
                            documents = null;
                        } else if ("recent".equals(pack)) {
                            documents = StickerMasksAlert.this.recentStickers[StickerMasksAlert.this.currentType];
                        } else {
                            documents = StickerMasksAlert.this.favouriteStickers;
                        }
                        if (documents == null) {
                            cell2.setHeight(1);
                            return;
                        } else if (documents.isEmpty()) {
                            cell2.setHeight(AndroidUtilities.dp(8.0f));
                            return;
                        } else {
                            int height = StickerMasksAlert.this.gridView.getHeight() - (((int) Math.ceil((double) (((float) documents.size()) / ((float) this.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                            if (height > 0) {
                                i = height;
                            }
                            cell2.setHeight(i);
                            return;
                        }
                    } else {
                        cell2.setHeight(AndroidUtilities.dp(82.0f));
                        return;
                    }
                case 2:
                    StickerSetNameCell cell3 = (StickerSetNameCell) holder.itemView;
                    Object object = this.cache.get(position);
                    if (object instanceof TLRPC.TL_messages_stickerSet) {
                        TLRPC.TL_messages_stickerSet set = (TLRPC.TL_messages_stickerSet) object;
                        if (set.set != null) {
                            cell3.setText(set.set.title, 0);
                            return;
                        }
                        return;
                    } else if (object == StickerMasksAlert.this.recentStickers[StickerMasksAlert.this.currentType]) {
                        cell3.setText(LocaleController.getString("RecentStickers", NUM), 0);
                        return;
                    } else if (object == StickerMasksAlert.this.favouriteStickers) {
                        cell3.setText(LocaleController.getString("FavoriteStickers", NUM), 0);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: org.telegram.tgnet.TLRPC$TL_messages_stickerSet} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void notifyDataSetChanged() {
            /*
                r17 = this;
                r0 = r17
                org.telegram.ui.Components.StickerMasksAlert r1 = org.telegram.ui.Components.StickerMasksAlert.this
                org.telegram.ui.Components.RecyclerListView r1 = r1.gridView
                int r1 = r1.getMeasuredWidth()
                if (r1 != 0) goto L_0x0012
                android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
                int r1 = r2.x
            L_0x0012:
                r2 = 1116733440(0x42900000, float:72.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = r1 / r2
                r0.stickersPerRow = r2
                org.telegram.ui.Components.StickerMasksAlert r2 = org.telegram.ui.Components.StickerMasksAlert.this
                androidx.recyclerview.widget.GridLayoutManager r2 = r2.stickersLayoutManager
                int r3 = r0.stickersPerRow
                r2.setSpanCount(r3)
                android.util.SparseArray<java.lang.Object> r2 = r0.rowStartPack
                r2.clear()
                java.util.HashMap<java.lang.Object, java.lang.Integer> r2 = r0.packStartPosition
                r2.clear()
                android.util.SparseIntArray r2 = r0.positionToRow
                r2.clear()
                android.util.SparseArray<java.lang.Object> r2 = r0.cache
                r2.clear()
                r2 = 0
                r0.totalItems = r2
                org.telegram.ui.Components.StickerMasksAlert r2 = org.telegram.ui.Components.StickerMasksAlert.this
                java.util.ArrayList[] r2 = r2.stickerSets
                org.telegram.ui.Components.StickerMasksAlert r3 = org.telegram.ui.Components.StickerMasksAlert.this
                int r3 = r3.currentType
                r2 = r2[r3]
                r3 = 0
                r4 = -3
            L_0x004e:
                int r5 = r2.size()
                if (r4 >= r5) goto L_0x016a
                r5 = 0
                r6 = -3
                if (r4 != r6) goto L_0x006b
                android.util.SparseArray<java.lang.Object> r6 = r0.cache
                int r7 = r0.totalItems
                int r8 = r7 + 1
                r0.totalItems = r8
                java.lang.String r8 = "search"
                r6.put(r7, r8)
                int r3 = r3 + 1
                r16 = r1
                goto L_0x0164
            L_0x006b:
                r6 = -2
                java.lang.String r7 = "fav"
                java.lang.String r8 = "recent"
                r9 = -1
                if (r4 != r6) goto L_0x0091
                org.telegram.ui.Components.StickerMasksAlert r6 = org.telegram.ui.Components.StickerMasksAlert.this
                int r6 = r6.currentType
                if (r6 != 0) goto L_0x008e
                org.telegram.ui.Components.StickerMasksAlert r6 = org.telegram.ui.Components.StickerMasksAlert.this
                java.util.ArrayList r6 = r6.favouriteStickers
                java.util.HashMap<java.lang.Object, java.lang.Integer> r10 = r0.packStartPosition
                r11 = r7
                int r12 = r0.totalItems
                java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
                r10.put(r7, r12)
                goto L_0x00c3
            L_0x008e:
                r6 = 0
                r11 = 0
                goto L_0x00c3
            L_0x0091:
                if (r4 != r9) goto L_0x00ae
                org.telegram.ui.Components.StickerMasksAlert r6 = org.telegram.ui.Components.StickerMasksAlert.this
                java.util.ArrayList[] r6 = r6.recentStickers
                org.telegram.ui.Components.StickerMasksAlert r10 = org.telegram.ui.Components.StickerMasksAlert.this
                int r10 = r10.currentType
                r6 = r6[r10]
                java.util.HashMap<java.lang.Object, java.lang.Integer> r10 = r0.packStartPosition
                r11 = r8
                int r12 = r0.totalItems
                java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
                r10.put(r8, r12)
                goto L_0x00c3
            L_0x00ae:
                r11 = 0
                java.lang.Object r6 = r2.get(r4)
                r5 = r6
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r5 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r5
                java.util.ArrayList r6 = r5.documents
                java.util.HashMap<java.lang.Object, java.lang.Integer> r10 = r0.packStartPosition
                int r12 = r0.totalItems
                java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
                r10.put(r5, r12)
            L_0x00c3:
                if (r6 == 0) goto L_0x0162
                boolean r10 = r6.isEmpty()
                if (r10 == 0) goto L_0x00cf
                r16 = r1
                goto L_0x0164
            L_0x00cf:
                int r10 = r6.size()
                float r10 = (float) r10
                int r12 = r0.stickersPerRow
                float r12 = (float) r12
                float r10 = r10 / r12
                double r12 = (double) r10
                double r12 = java.lang.Math.ceil(r12)
                int r10 = (int) r12
                if (r5 == 0) goto L_0x00e8
                android.util.SparseArray<java.lang.Object> r12 = r0.cache
                int r13 = r0.totalItems
                r12.put(r13, r5)
                goto L_0x00ef
            L_0x00e8:
                android.util.SparseArray<java.lang.Object> r12 = r0.cache
                int r13 = r0.totalItems
                r12.put(r13, r6)
            L_0x00ef:
                android.util.SparseIntArray r12 = r0.positionToRow
                int r13 = r0.totalItems
                r12.put(r13, r3)
                r12 = 0
            L_0x00f7:
                int r13 = r6.size()
                if (r12 >= r13) goto L_0x0131
                int r13 = r12 + 1
                int r14 = r0.totalItems
                int r13 = r13 + r14
                android.util.SparseArray<java.lang.Object> r14 = r0.cache
                java.lang.Object r15 = r6.get(r12)
                r14.put(r13, r15)
                if (r5 == 0) goto L_0x0113
                android.util.SparseArray<java.lang.Object> r14 = r0.cacheParents
                r14.put(r13, r5)
                goto L_0x0118
            L_0x0113:
                android.util.SparseArray<java.lang.Object> r14 = r0.cacheParents
                r14.put(r13, r11)
            L_0x0118:
                android.util.SparseIntArray r14 = r0.positionToRow
                int r15 = r12 + 1
                int r9 = r0.totalItems
                int r15 = r15 + r9
                int r9 = r3 + 1
                r16 = r1
                int r1 = r0.stickersPerRow
                int r1 = r12 / r1
                int r9 = r9 + r1
                r14.put(r15, r9)
                int r12 = r12 + 1
                r1 = r16
                r9 = -1
                goto L_0x00f7
            L_0x0131:
                r16 = r1
                r1 = 0
            L_0x0134:
                int r9 = r10 + 1
                if (r1 >= r9) goto L_0x0153
                if (r5 == 0) goto L_0x0143
                android.util.SparseArray<java.lang.Object> r9 = r0.rowStartPack
                int r12 = r3 + r1
                r9.put(r12, r5)
                r13 = -1
                goto L_0x0150
            L_0x0143:
                android.util.SparseArray<java.lang.Object> r9 = r0.rowStartPack
                int r12 = r3 + r1
                r13 = -1
                if (r4 != r13) goto L_0x014c
                r14 = r8
                goto L_0x014d
            L_0x014c:
                r14 = r7
            L_0x014d:
                r9.put(r12, r14)
            L_0x0150:
                int r1 = r1 + 1
                goto L_0x0134
            L_0x0153:
                int r1 = r0.totalItems
                int r7 = r0.stickersPerRow
                int r7 = r7 * r10
                int r7 = r7 + 1
                int r1 = r1 + r7
                r0.totalItems = r1
                int r1 = r10 + 1
                int r3 = r3 + r1
                goto L_0x0164
            L_0x0162:
                r16 = r1
            L_0x0164:
                int r4 = r4 + 1
                r1 = r16
                goto L_0x004e
            L_0x016a:
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
        public ArrayList<ArrayList<TLRPC.Document>> emojiArrays = new ArrayList<>();
        /* access modifiers changed from: private */
        public int emojiSearchId;
        /* access modifiers changed from: private */
        public HashMap<ArrayList<TLRPC.Document>, String> emojiStickers = new HashMap<>();
        /* access modifiers changed from: private */
        public ArrayList<TLRPC.TL_messages_stickerSet> localPacks = new ArrayList<>();
        /* access modifiers changed from: private */
        public HashMap<TLRPC.TL_messages_stickerSet, Integer> localPacksByName = new HashMap<>();
        /* access modifiers changed from: private */
        public HashMap<TLRPC.TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap<>();
        private SparseArray<String> positionToEmoji = new SparseArray<>();
        private SparseIntArray positionToRow = new SparseIntArray();
        /* access modifiers changed from: private */
        public int reqId2;
        private SparseArray<Object> rowStartPack = new SparseArray<>();
        /* access modifiers changed from: private */
        public String searchQuery;
        private Runnable searchRunnable = new Runnable() {
            private void clear() {
                if (!StickersSearchGridAdapter.this.cleared) {
                    StickersSearchGridAdapter.this.cleared = true;
                    StickersSearchGridAdapter.this.emojiStickers.clear();
                    StickersSearchGridAdapter.this.emojiArrays.clear();
                    StickersSearchGridAdapter.this.localPacks.clear();
                    StickersSearchGridAdapter.this.localPacksByShortName.clear();
                    StickersSearchGridAdapter.this.localPacksByName.clear();
                }
            }

            public void run() {
                if (!TextUtils.isEmpty(StickersSearchGridAdapter.this.searchQuery)) {
                    StickersSearchGridAdapter.this.cleared = false;
                    int lastId = StickersSearchGridAdapter.access$4904(StickersSearchGridAdapter.this);
                    ArrayList<TLRPC.Document> emojiStickersArray = new ArrayList<>(0);
                    LongSparseArray<TLRPC.Document> emojiStickersMap = new LongSparseArray<>(0);
                    HashMap<String, ArrayList<TLRPC.Document>> allStickers = MediaDataController.getInstance(StickerMasksAlert.this.currentAccount).getAllStickers();
                    if (StickersSearchGridAdapter.this.searchQuery.length() <= 14) {
                        CharSequence emoji = StickersSearchGridAdapter.this.searchQuery;
                        int length = emoji.length();
                        int a = 0;
                        while (a < length) {
                            if (a < length - 1 && ((emoji.charAt(a) == 55356 && emoji.charAt(a + 1) >= 57339 && emoji.charAt(a + 1) <= 57343) || (emoji.charAt(a) == 8205 && (emoji.charAt(a + 1) == 9792 || emoji.charAt(a + 1) == 9794)))) {
                                emoji = TextUtils.concat(new CharSequence[]{emoji.subSequence(0, a), emoji.subSequence(a + 2, emoji.length())});
                                length -= 2;
                                a--;
                            } else if (emoji.charAt(a) == 65039) {
                                emoji = TextUtils.concat(new CharSequence[]{emoji.subSequence(0, a), emoji.subSequence(a + 1, emoji.length())});
                                length--;
                                a--;
                            }
                            a++;
                        }
                        ArrayList<TLRPC.Document> newStickers = allStickers != null ? allStickers.get(emoji.toString()) : null;
                        if (newStickers != null && !newStickers.isEmpty()) {
                            clear();
                            emojiStickersArray.addAll(newStickers);
                            int size = newStickers.size();
                            for (int a2 = 0; a2 < size; a2++) {
                                TLRPC.Document document = newStickers.get(a2);
                                emojiStickersMap.put(document.id, document);
                            }
                            StickersSearchGridAdapter.this.emojiStickers.put(emojiStickersArray, StickersSearchGridAdapter.this.searchQuery);
                            StickersSearchGridAdapter.this.emojiArrays.add(emojiStickersArray);
                        }
                    }
                    if (allStickers != null && !allStickers.isEmpty() && StickersSearchGridAdapter.this.searchQuery.length() > 1) {
                        String[] newLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                        if (!Arrays.equals(StickerMasksAlert.this.lastSearchKeyboardLanguage, newLanguage)) {
                            MediaDataController.getInstance(StickerMasksAlert.this.currentAccount).fetchNewEmojiKeywords(newLanguage);
                        }
                        String[] unused = StickerMasksAlert.this.lastSearchKeyboardLanguage = newLanguage;
                        MediaDataController.getInstance(StickerMasksAlert.this.currentAccount).getEmojiSuggestions(StickerMasksAlert.this.lastSearchKeyboardLanguage, StickersSearchGridAdapter.this.searchQuery, false, new StickerMasksAlert$StickersSearchGridAdapter$1$$ExternalSyntheticLambda1(this, lastId, allStickers));
                    }
                    ArrayList<TLRPC.TL_messages_stickerSet> local = MediaDataController.getInstance(StickerMasksAlert.this.currentAccount).getStickerSets(StickerMasksAlert.this.currentType);
                    int size2 = local.size();
                    for (int a3 = 0; a3 < size2; a3++) {
                        TLRPC.TL_messages_stickerSet set = local.get(a3);
                        int indexOfIgnoreCase = AndroidUtilities.indexOfIgnoreCase(set.set.title, StickersSearchGridAdapter.this.searchQuery);
                        int index = indexOfIgnoreCase;
                        if (indexOfIgnoreCase >= 0) {
                            if (index == 0 || set.set.title.charAt(index - 1) == ' ') {
                                clear();
                                StickersSearchGridAdapter.this.localPacks.add(set);
                                StickersSearchGridAdapter.this.localPacksByName.put(set, Integer.valueOf(index));
                            }
                        } else if (set.set.short_name != null) {
                            int indexOfIgnoreCase2 = AndroidUtilities.indexOfIgnoreCase(set.set.short_name, StickersSearchGridAdapter.this.searchQuery);
                            int index2 = indexOfIgnoreCase2;
                            if (indexOfIgnoreCase2 >= 0 && (index2 == 0 || set.set.short_name.charAt(index2 - 1) == ' ')) {
                                clear();
                                StickersSearchGridAdapter.this.localPacks.add(set);
                                StickersSearchGridAdapter.this.localPacksByShortName.put(set, true);
                            }
                        }
                    }
                    ArrayList<TLRPC.TL_messages_stickerSet> local2 = MediaDataController.getInstance(StickerMasksAlert.this.currentAccount).getStickerSets(3);
                    int size3 = local2.size();
                    for (int a4 = 0; a4 < size3; a4++) {
                        TLRPC.TL_messages_stickerSet set2 = local2.get(a4);
                        int indexOfIgnoreCase3 = AndroidUtilities.indexOfIgnoreCase(set2.set.title, StickersSearchGridAdapter.this.searchQuery);
                        int index3 = indexOfIgnoreCase3;
                        if (indexOfIgnoreCase3 >= 0) {
                            if (index3 == 0 || set2.set.title.charAt(index3 - 1) == ' ') {
                                clear();
                                StickersSearchGridAdapter.this.localPacks.add(set2);
                                StickersSearchGridAdapter.this.localPacksByName.put(set2, Integer.valueOf(index3));
                            }
                        } else if (set2.set.short_name != null) {
                            int indexOfIgnoreCase4 = AndroidUtilities.indexOfIgnoreCase(set2.set.short_name, StickersSearchGridAdapter.this.searchQuery);
                            int index4 = indexOfIgnoreCase4;
                            if (indexOfIgnoreCase4 >= 0 && (index4 == 0 || set2.set.short_name.charAt(index4 - 1) == ' ')) {
                                clear();
                                StickersSearchGridAdapter.this.localPacks.add(set2);
                                StickersSearchGridAdapter.this.localPacksByShortName.put(set2, true);
                            }
                        }
                    }
                    boolean isValidEmoji = Emoji.isValidEmoji(StickersSearchGridAdapter.this.searchQuery);
                    boolean validEmoji = isValidEmoji;
                    if (isValidEmoji) {
                        StickerMasksAlert.this.stickersSearchField.progressDrawable.startAnimation();
                        TLRPC.TL_messages_getStickers req2 = new TLRPC.TL_messages_getStickers();
                        req2.emoticon = StickersSearchGridAdapter.this.searchQuery;
                        req2.hash = 0;
                        StickersSearchGridAdapter stickersSearchGridAdapter = StickersSearchGridAdapter.this;
                        int unused2 = stickersSearchGridAdapter.reqId2 = ConnectionsManager.getInstance(StickerMasksAlert.this.currentAccount).sendRequest(req2, new StickerMasksAlert$StickersSearchGridAdapter$1$$ExternalSyntheticLambda2(this, req2, emojiStickersArray, emojiStickersMap));
                    }
                    if ((!validEmoji || !StickersSearchGridAdapter.this.localPacks.isEmpty() || !StickersSearchGridAdapter.this.emojiStickers.isEmpty()) && StickerMasksAlert.this.gridView.getAdapter() != StickerMasksAlert.this.stickersSearchGridAdapter) {
                        StickerMasksAlert.this.gridView.setAdapter(StickerMasksAlert.this.stickersSearchGridAdapter);
                    }
                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                }
            }

            /* renamed from: lambda$run$0$org-telegram-ui-Components-StickerMasksAlert$StickersSearchGridAdapter$1  reason: not valid java name */
            public /* synthetic */ void m4414x56aca32f(int lastId, HashMap allStickers, ArrayList param, String alias) {
                if (lastId == StickersSearchGridAdapter.this.emojiSearchId) {
                    boolean added = false;
                    int size = param.size();
                    for (int a = 0; a < size; a++) {
                        String emoji = ((MediaDataController.KeywordResult) param.get(a)).emoji;
                        ArrayList<TLRPC.Document> newStickers = allStickers != null ? (ArrayList) allStickers.get(emoji) : null;
                        if (newStickers != null && !newStickers.isEmpty()) {
                            clear();
                            if (!StickersSearchGridAdapter.this.emojiStickers.containsKey(newStickers)) {
                                StickersSearchGridAdapter.this.emojiStickers.put(newStickers, emoji);
                                StickersSearchGridAdapter.this.emojiArrays.add(newStickers);
                                added = true;
                            }
                        }
                    }
                    if (added) {
                        StickersSearchGridAdapter.this.notifyDataSetChanged();
                    } else if (StickersSearchGridAdapter.this.reqId2 == 0) {
                        clear();
                        StickersSearchGridAdapter.this.notifyDataSetChanged();
                    }
                }
            }

            /* renamed from: lambda$run$2$org-telegram-ui-Components-StickerMasksAlert$StickersSearchGridAdapter$1  reason: not valid java name */
            public /* synthetic */ void m4416x8b8e956d(TLRPC.TL_messages_getStickers req2, ArrayList emojiStickersArray, LongSparseArray emojiStickersMap, TLObject response, TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new StickerMasksAlert$StickersSearchGridAdapter$1$$ExternalSyntheticLambda0(this, req2, response, emojiStickersArray, emojiStickersMap));
            }

            /* renamed from: lambda$run$1$org-telegram-ui-Components-StickerMasksAlert$StickersSearchGridAdapter$1  reason: not valid java name */
            public /* synthetic */ void m4415x711d9c4e(TLRPC.TL_messages_getStickers req2, TLObject response, ArrayList emojiStickersArray, LongSparseArray emojiStickersMap) {
                if (req2.emoticon.equals(StickersSearchGridAdapter.this.searchQuery)) {
                    StickerMasksAlert.this.stickersSearchField.progressDrawable.stopAnimation();
                    int unused = StickersSearchGridAdapter.this.reqId2 = 0;
                    if (response instanceof TLRPC.TL_messages_stickers) {
                        TLRPC.TL_messages_stickers res = (TLRPC.TL_messages_stickers) response;
                        int oldCount = emojiStickersArray.size();
                        int size = res.stickers.size();
                        for (int a = 0; a < size; a++) {
                            TLRPC.Document document = res.stickers.get(a);
                            if (emojiStickersMap.indexOfKey(document.id) < 0) {
                                emojiStickersArray.add(document);
                            }
                        }
                        if (oldCount != emojiStickersArray.size()) {
                            StickersSearchGridAdapter.this.emojiStickers.put(emojiStickersArray, StickersSearchGridAdapter.this.searchQuery);
                            if (oldCount == 0) {
                                StickersSearchGridAdapter.this.emojiArrays.add(emojiStickersArray);
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

        static /* synthetic */ int access$4904(StickersSearchGridAdapter x0) {
            int i = x0.emojiSearchId + 1;
            x0.emojiSearchId = i;
            return i;
        }

        public StickersSearchGridAdapter(Context context2) {
            this.context = context2;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public int getItemCount() {
            int i = this.totalItems;
            if (i != 1) {
                return i + 1;
            }
            return 2;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public void search(String text) {
            if (this.reqId2 != 0) {
                ConnectionsManager.getInstance(StickerMasksAlert.this.currentAccount).cancelRequest(this.reqId2, true);
                this.reqId2 = 0;
            }
            if (TextUtils.isEmpty(text)) {
                this.searchQuery = null;
                this.localPacks.clear();
                this.emojiStickers.clear();
                if (StickerMasksAlert.this.gridView.getAdapter() != StickerMasksAlert.this.stickersGridAdapter) {
                    StickerMasksAlert.this.gridView.setAdapter(StickerMasksAlert.this.stickersGridAdapter);
                }
                notifyDataSetChanged();
            } else {
                this.searchQuery = text.toLowerCase();
            }
            AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
            AndroidUtilities.runOnUIThread(this.searchRunnable, 300);
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 4;
            }
            if (position == 1 && this.totalItems == 1) {
                return 5;
            }
            Object object = this.cache.get(position);
            if (object == null) {
                return 1;
            }
            if (object instanceof TLRPC.Document) {
                return 0;
            }
            return 2;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new StickerEmojiCell(this.context, false) {
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                        }
                    };
                    break;
                case 1:
                    view = new EmptyCell(this.context);
                    break;
                case 2:
                    view = new StickerSetNameCell(this.context, false, StickerMasksAlert.this.resourcesProvider);
                    break;
                case 4:
                    view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, StickerMasksAlert.this.searchFieldHeight + AndroidUtilities.dp(48.0f)));
                    break;
                case 5:
                    FrameLayout frameLayout = new FrameLayout(this.context) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(((StickerMasksAlert.this.gridView.getMeasuredHeight() - StickerMasksAlert.this.searchFieldHeight) - AndroidUtilities.dp(48.0f)) - AndroidUtilities.dp(48.0f), NUM));
                        }
                    };
                    ImageView imageView = new ImageView(this.context);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    imageView.setImageResource(NUM);
                    imageView.setColorFilter(new PorterDuffColorFilter(-7038047, PorterDuff.Mode.MULTIPLY));
                    frameLayout.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 50.0f));
                    TextView textView = new TextView(this.context);
                    textView.setText(LocaleController.getString("NoStickersFound", NUM));
                    textView.setTextSize(1, 16.0f);
                    textView.setTextColor(-7038047);
                    frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
                    view = frameLayout;
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Integer count;
            boolean z = false;
            int i = 1;
            switch (holder.getItemViewType()) {
                case 0:
                    TLRPC.Document sticker = (TLRPC.Document) this.cache.get(position);
                    StickerEmojiCell cell = (StickerEmojiCell) holder.itemView;
                    cell.setSticker(sticker, (SendMessagesHelper.ImportingSticker) null, this.cacheParent.get(position), this.positionToEmoji.get(position), false);
                    if (StickerMasksAlert.this.recentStickers[StickerMasksAlert.this.currentType].contains(sticker) || StickerMasksAlert.this.favouriteStickers.contains(sticker)) {
                        z = true;
                    }
                    cell.setRecent(z);
                    return;
                case 1:
                    EmptyCell cell2 = (EmptyCell) holder.itemView;
                    if (position == this.totalItems) {
                        int row = this.positionToRow.get(position - 1, Integer.MIN_VALUE);
                        if (row == Integer.MIN_VALUE) {
                            cell2.setHeight(1);
                            return;
                        }
                        Object pack = this.rowStartPack.get(row);
                        if (pack instanceof TLRPC.TL_messages_stickerSet) {
                            count = Integer.valueOf(((TLRPC.TL_messages_stickerSet) pack).documents.size());
                        } else if (pack instanceof Integer) {
                            count = (Integer) pack;
                        } else {
                            count = null;
                        }
                        if (count == null) {
                            cell2.setHeight(1);
                            return;
                        } else if (count.intValue() == 0) {
                            cell2.setHeight(AndroidUtilities.dp(8.0f));
                            return;
                        } else {
                            int height = StickerMasksAlert.this.gridView.getHeight() - (((int) Math.ceil((double) (((float) count.intValue()) / ((float) StickerMasksAlert.this.stickersGridAdapter.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                            if (height > 0) {
                                i = height;
                            }
                            cell2.setHeight(i);
                            return;
                        }
                    } else {
                        cell2.setHeight(AndroidUtilities.dp(82.0f));
                        return;
                    }
                case 2:
                    StickerSetNameCell cell3 = (StickerSetNameCell) holder.itemView;
                    Object object = this.cache.get(position);
                    if (object instanceof TLRPC.TL_messages_stickerSet) {
                        TLRPC.TL_messages_stickerSet set = (TLRPC.TL_messages_stickerSet) object;
                        if (TextUtils.isEmpty(this.searchQuery) || !this.localPacksByShortName.containsKey(set)) {
                            Integer start = this.localPacksByName.get(set);
                            if (!(set.set == null || start == null)) {
                                cell3.setText(set.set.title, 0, start.intValue(), !TextUtils.isEmpty(this.searchQuery) ? this.searchQuery.length() : 0);
                            }
                            cell3.setUrl((CharSequence) null, 0);
                            return;
                        }
                        if (set.set != null) {
                            cell3.setText(set.set.title, 0);
                        }
                        cell3.setUrl(set.set.short_name, this.searchQuery.length());
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void notifyDataSetChanged() {
            int localCount;
            this.rowStartPack.clear();
            this.positionToRow.clear();
            this.cache.clear();
            this.positionToEmoji.clear();
            this.totalItems = 0;
            int startRow = 0;
            int a = -1;
            int localCount2 = this.localPacks.size();
            int emojiCount = this.emojiArrays.isEmpty() ^ 1;
            while (a < localCount2 + ((int) emojiCount)) {
                if (a == -1) {
                    SparseArray<Object> sparseArray = this.cache;
                    int i = this.totalItems;
                    this.totalItems = i + 1;
                    sparseArray.put(i, "search");
                    startRow++;
                    localCount = localCount2;
                } else if (a < localCount2) {
                    TLRPC.TL_messages_stickerSet set = this.localPacks.get(a);
                    ArrayList<TLRPC.Document> documents = set.documents;
                    if (documents.isEmpty()) {
                        localCount = localCount2;
                    } else {
                        int count = (int) Math.ceil((double) (((float) documents.size()) / ((float) StickerMasksAlert.this.stickersGridAdapter.stickersPerRow)));
                        this.cache.put(this.totalItems, set);
                        this.positionToRow.put(this.totalItems, startRow);
                        int size = documents.size();
                        for (int b = 0; b < size; b++) {
                            int num = b + 1 + this.totalItems;
                            int row = startRow + 1 + (b / StickerMasksAlert.this.stickersGridAdapter.stickersPerRow);
                            this.cache.put(num, documents.get(b));
                            if (set != null) {
                                this.cacheParent.put(num, set);
                            }
                            this.positionToRow.put(num, row);
                        }
                        int N = count + 1;
                        for (int b2 = 0; b2 < N; b2++) {
                            this.rowStartPack.put(startRow + b2, set);
                        }
                        this.totalItems += (StickerMasksAlert.this.stickersGridAdapter.stickersPerRow * count) + 1;
                        startRow += count + 1;
                        localCount = localCount2;
                    }
                } else {
                    int documentsCount = 0;
                    String lastEmoji = "";
                    int N2 = this.emojiArrays.size();
                    for (int i2 = 0; i2 < N2; i2++) {
                        ArrayList<TLRPC.Document> documents2 = this.emojiArrays.get(i2);
                        String emoji = this.emojiStickers.get(documents2);
                        if (emoji != null && !lastEmoji.equals(emoji)) {
                            lastEmoji = emoji;
                            this.positionToEmoji.put(this.totalItems + documentsCount, lastEmoji);
                        }
                        int b3 = 0;
                        int size2 = documents2.size();
                        while (b3 < size2) {
                            int num2 = this.totalItems + documentsCount;
                            int row2 = (documentsCount / StickerMasksAlert.this.stickersGridAdapter.stickersPerRow) + startRow;
                            TLRPC.Document document = documents2.get(b3);
                            int localCount3 = localCount2;
                            this.cache.put(num2, document);
                            int N3 = N2;
                            ArrayList<TLRPC.Document> documents3 = documents2;
                            Object parent = MediaDataController.getInstance(StickerMasksAlert.this.currentAccount).getStickerSetById(MediaDataController.getStickerSetId(document));
                            if (parent != null) {
                                this.cacheParent.put(num2, parent);
                            }
                            this.positionToRow.put(num2, row2);
                            documentsCount++;
                            b3++;
                            localCount2 = localCount3;
                            N2 = N3;
                            documents2 = documents3;
                        }
                        int i3 = N2;
                        ArrayList<TLRPC.Document> arrayList = documents2;
                    }
                    localCount = localCount2;
                    int i4 = N2;
                    int count2 = (int) Math.ceil((double) (((float) documentsCount) / ((float) StickerMasksAlert.this.stickersGridAdapter.stickersPerRow)));
                    for (int b4 = 0; b4 < count2; b4++) {
                        this.rowStartPack.put(startRow + b4, Integer.valueOf(documentsCount));
                    }
                    this.totalItems += StickerMasksAlert.this.stickersGridAdapter.stickersPerRow * count2;
                    startRow += count2;
                }
                a++;
                localCount2 = localCount;
            }
            super.notifyDataSetChanged();
        }
    }
}
