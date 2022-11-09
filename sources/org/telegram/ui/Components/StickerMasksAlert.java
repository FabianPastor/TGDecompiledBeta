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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
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
import org.telegram.messenger.R;
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
import org.telegram.ui.Components.StickerMasksAlert;
import org.telegram.ui.ContentPreviewViewer;
/* loaded from: classes3.dex */
public class StickerMasksAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private FrameLayout bottomTabContainer;
    private ContentPreviewViewer.ContentPreviewViewerDelegate contentPreviewViewerDelegate;
    private int currentAccount;
    private int currentType;
    private StickerMasksAlertDelegate delegate;
    private int favTabBum;
    private ArrayList<TLRPC$Document> favouriteStickers;
    private RecyclerListView gridView;
    private String[] lastSearchKeyboardLanguage;
    private ImageView masksButton;
    private ArrayList<TLRPC$Document>[] recentStickers;
    private int recentTabBum;
    private int scrollOffsetY;
    private int searchFieldHeight;
    private Drawable shadowDrawable;
    private View shadowLine;
    private Drawable[] stickerIcons;
    private ArrayList<TLRPC$TL_messages_stickerSet>[] stickerSets;
    private ImageView stickersButton;
    private StickersGridAdapter stickersGridAdapter;
    private GridLayoutManager stickersLayoutManager;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    private SearchField stickersSearchField;
    private StickersSearchGridAdapter stickersSearchGridAdapter;
    private ScrollSlidingTabStrip stickersTab;
    private int stickersTabOffset;

    /* loaded from: classes3.dex */
    public interface StickerMasksAlertDelegate {
        void onStickerSelected(Object obj, TLRPC$Document tLRPC$Document);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class SearchField extends FrameLayout {
        private ImageView clearSearchImageView;
        private CloseProgressDrawable2 progressDrawable;
        private EditTextBoldCursor searchEditText;
        private AnimatorSet shadowAnimator;
        private View shadowView;

        public SearchField(Context context, int i) {
            super(context);
            View view = new View(context);
            this.shadowView = view;
            view.setAlpha(0.0f);
            this.shadowView.setTag(1);
            this.shadowView.setBackgroundColor(NUM);
            addView(this.shadowView, new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83));
            View view2 = new View(context);
            view2.setBackgroundColor(-14342875);
            addView(view2, new FrameLayout.LayoutParams(-1, StickerMasksAlert.this.searchFieldHeight));
            View view3 = new View(context);
            view3.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), -13224394));
            addView(view3, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 14.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setImageResource(R.drawable.smiles_inputsearch);
            imageView.setColorFilter(new PorterDuffColorFilter(-8947849, PorterDuff.Mode.MULTIPLY));
            addView(imageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 14.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView3 = this.clearSearchImageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2(this, StickerMasksAlert.this) { // from class: org.telegram.ui.Components.StickerMasksAlert.SearchField.1
                @Override // org.telegram.ui.Components.CloseProgressDrawable2
                public int getCurrentColor() {
                    return -8947849;
                }
            };
            this.progressDrawable = closeProgressDrawable2;
            imageView3.setImageDrawable(closeProgressDrawable2);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 14.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.StickerMasksAlert$SearchField$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view4) {
                    StickerMasksAlert.SearchField.this.lambda$new$0(view4);
                }
            });
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context, StickerMasksAlert.this) { // from class: org.telegram.ui.Components.StickerMasksAlert.SearchField.2
                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getAction() == 0) {
                        SearchField.this.searchEditText.requestFocus();
                        AndroidUtilities.showKeyboard(SearchField.this.searchEditText);
                    }
                    return super.onTouchEvent(motionEvent);
                }
            };
            this.searchEditText = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(-8947849);
            this.searchEditText.setTextColor(-1);
            this.searchEditText.setBackgroundDrawable(null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(NUM);
            if (i == 0) {
                this.searchEditText.setHint(LocaleController.getString("SearchStickersHint", R.string.SearchStickersHint));
            } else if (i == 1) {
                this.searchEditText.setHint(LocaleController.getString("SearchEmojiHint", R.string.SearchEmojiHint));
            } else if (i == 2) {
                this.searchEditText.setHint(LocaleController.getString("SearchGifsTitle", R.string.SearchGifsTitle));
            }
            this.searchEditText.setCursorColor(-1);
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 12.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher(StickerMasksAlert.this) { // from class: org.telegram.ui.Components.StickerMasksAlert.SearchField.3
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                    boolean z = true;
                    boolean z2 = SearchField.this.searchEditText.length() > 0;
                    float f = 0.0f;
                    if (SearchField.this.clearSearchImageView.getAlpha() == 0.0f) {
                        z = false;
                    }
                    if (z2 != z) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (z2) {
                            f = 1.0f;
                        }
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150L).scaleX(z2 ? 1.0f : 0.1f);
                        if (!z2) {
                            f2 = 0.1f;
                        }
                        scaleX.scaleY(f2).start();
                    }
                    StickerMasksAlert.this.stickersSearchGridAdapter.search(SearchField.this.searchEditText.getText().toString());
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void showShadow(boolean z, boolean z2) {
            if (!z || this.shadowView.getTag() != null) {
                if (!z && this.shadowView.getTag() != null) {
                    return;
                }
                AnimatorSet animatorSet = this.shadowAnimator;
                Integer num = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.shadowAnimator = null;
                }
                View view = this.shadowView;
                if (!z) {
                    num = 1;
                }
                view.setTag(num);
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
                    this.shadowAnimator.setDuration(200L);
                    this.shadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.shadowAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.StickerMasksAlert.SearchField.4
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            SearchField.this.shadowAnimator = null;
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

    public StickerMasksAlert(Context context, boolean z, final Theme.ResourcesProvider resourcesProvider) {
        super(context, true, resourcesProvider);
        this.currentAccount = UserConfig.selectedAccount;
        this.stickerSets = new ArrayList[]{new ArrayList<>(), new ArrayList<>()};
        this.recentStickers = new ArrayList[]{new ArrayList<>(), new ArrayList<>()};
        this.favouriteStickers = new ArrayList<>();
        this.recentTabBum = -2;
        this.favTabBum = -2;
        this.contentPreviewViewerDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() { // from class: org.telegram.ui.Components.StickerMasksAlert.1
            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ boolean can() {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$can(this);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean canSchedule() {
                return false;
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public long getDialogId() {
                return 0L;
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ String getQuery(boolean z2) {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$getQuery(this, z2);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ void gifAddedOrDeleted() {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$gifAddedOrDeleted(this);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean isInScheduleMode() {
                return false;
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean needMenu() {
                return false;
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ boolean needOpen() {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needOpen(this);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ boolean needRemove() {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needRemove(this);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean needSend() {
                return false;
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public void openSet(TLRPC$InputStickerSet tLRPC$InputStickerSet, boolean z2) {
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ void remove(SendMessagesHelper.ImportingSticker importingSticker) {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$remove(this, importingSticker);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ void sendGif(Object obj, Object obj2, boolean z2, int i) {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendGif(this, obj, obj2, z2, i);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public void sendSticker(TLRPC$Document tLRPC$Document, String str, Object obj, boolean z2, int i) {
                StickerMasksAlert.this.delegate.onStickerSelected(obj, tLRPC$Document);
            }
        };
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
        Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(-14342875, PorterDuff.Mode.MULTIPLY));
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) { // from class: org.telegram.ui.Components.StickerMasksAlert.2
            private long lastUpdateTime;
            private float statusBarProgress;
            private boolean ignoreLayout = false;
            private RectF rect = new RectF();

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int i, int i2) {
                int dp;
                int size = View.MeasureSpec.getSize(i2);
                if (Build.VERSION.SDK_INT >= 21 && !((BottomSheet) StickerMasksAlert.this).isFullscreen) {
                    this.ignoreLayout = true;
                    setPadding(((BottomSheet) StickerMasksAlert.this).backgroundPaddingLeft, AndroidUtilities.statusBarHeight, ((BottomSheet) StickerMasksAlert.this).backgroundPaddingLeft, 0);
                    this.ignoreLayout = false;
                }
                int paddingTop = size - getPaddingTop();
                if (measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                    this.statusBarProgress = 1.0f;
                    dp = 0;
                } else {
                    dp = (paddingTop - ((paddingTop / 5) * 3)) + AndroidUtilities.dp(8.0f);
                }
                if (StickerMasksAlert.this.gridView.getPaddingTop() != dp) {
                    this.ignoreLayout = true;
                    StickerMasksAlert.this.gridView.setPinnedSectionOffsetY(-dp);
                    StickerMasksAlert.this.gridView.setPadding(0, dp, 0, AndroidUtilities.dp(48.0f));
                    this.ignoreLayout = false;
                }
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(size, NUM));
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            public void onLayout(boolean z2, int i, int i2, int i3, int i4) {
                super.onLayout(z2, i, i2, i3, i4);
                StickerMasksAlert.this.updateLayout(false);
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && StickerMasksAlert.this.scrollOffsetY != 0 && motionEvent.getY() < StickerMasksAlert.this.scrollOffsetY + AndroidUtilities.dp(12.0f)) {
                    StickerMasksAlert.this.dismiss();
                    return true;
                }
                return super.onInterceptTouchEvent(motionEvent);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return !StickerMasksAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                float f;
                int dp = AndroidUtilities.dp(13.0f);
                int i = (StickerMasksAlert.this.scrollOffsetY - ((BottomSheet) StickerMasksAlert.this).backgroundPaddingTop) - dp;
                if (((BottomSheet) StickerMasksAlert.this).currentSheetAnimationType == 1) {
                    i = (int) (i + StickerMasksAlert.this.gridView.getTranslationY());
                }
                int dp2 = AndroidUtilities.dp(20.0f) + i;
                int measuredHeight = getMeasuredHeight() + AndroidUtilities.dp(15.0f) + ((BottomSheet) StickerMasksAlert.this).backgroundPaddingTop;
                int dp3 = AndroidUtilities.dp(12.0f);
                if (((BottomSheet) StickerMasksAlert.this).backgroundPaddingTop + i < dp3) {
                    float dp4 = dp + AndroidUtilities.dp(4.0f);
                    float min = Math.min(1.0f, ((dp3 - i) - ((BottomSheet) StickerMasksAlert.this).backgroundPaddingTop) / dp4);
                    int i2 = (int) ((dp3 - dp4) * min);
                    i -= i2;
                    dp2 -= i2;
                    measuredHeight += i2;
                    f = 1.0f - min;
                } else {
                    f = 1.0f;
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    int i3 = AndroidUtilities.statusBarHeight;
                    i += i3;
                    dp2 += i3;
                }
                StickerMasksAlert.this.shadowDrawable.setBounds(0, i, getMeasuredWidth(), measuredHeight);
                StickerMasksAlert.this.shadowDrawable.draw(canvas);
                if (f != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(-14342875);
                    this.rect.set(((BottomSheet) StickerMasksAlert.this).backgroundPaddingLeft, ((BottomSheet) StickerMasksAlert.this).backgroundPaddingTop + i, getMeasuredWidth() - ((BottomSheet) StickerMasksAlert.this).backgroundPaddingLeft, ((BottomSheet) StickerMasksAlert.this).backgroundPaddingTop + i + AndroidUtilities.dp(24.0f));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(12.0f) * f, AndroidUtilities.dp(12.0f) * f, Theme.dialogs_onlineCirclePaint);
                }
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long j = elapsedRealtime - this.lastUpdateTime;
                if (j > 18) {
                    j = 18;
                }
                this.lastUpdateTime = elapsedRealtime;
                if (f > 0.0f) {
                    int dp5 = AndroidUtilities.dp(36.0f);
                    this.rect.set((getMeasuredWidth() - dp5) / 2, dp2, (getMeasuredWidth() + dp5) / 2, dp2 + AndroidUtilities.dp(4.0f));
                    int alpha = Color.alpha(-11842741);
                    Theme.dialogs_onlineCirclePaint.setColor(-11842741);
                    Theme.dialogs_onlineCirclePaint.setAlpha((int) (alpha * 1.0f * f));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
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
                Theme.dialogs_onlineCirclePaint.setColor(Color.argb((int) (this.statusBarProgress * 255.0f), (int) (Color.red(-14342875) * 0.8f), (int) (Color.green(-14342875) * 0.8f), (int) (Color.blue(-14342875) * 0.8f)));
                canvas.drawRect(((BottomSheet) StickerMasksAlert.this).backgroundPaddingLeft, 0.0f, getMeasuredWidth() - ((BottomSheet) StickerMasksAlert.this).backgroundPaddingLeft, AndroidUtilities.statusBarHeight, Theme.dialogs_onlineCirclePaint);
            }
        };
        this.containerView = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, 0, i, 0);
        this.searchFieldHeight = AndroidUtilities.dp(64.0f);
        this.stickerIcons = new Drawable[]{Theme.createEmojiIconSelectorDrawable(context, R.drawable.stickers_recent, -11842741, -9520403), Theme.createEmojiIconSelectorDrawable(context, R.drawable.stickers_favorites, -11842741, -9520403)};
        MediaDataController.getInstance(this.currentAccount).checkStickers(0);
        MediaDataController.getInstance(this.currentAccount).checkStickers(1);
        MediaDataController.getInstance(this.currentAccount).checkFeaturedStickers();
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.StickerMasksAlert.3
            @Override // org.telegram.ui.Components.RecyclerListView
            protected boolean allowSelectChildAtPosition(float f, float f2) {
                return f2 >= ((float) (StickerMasksAlert.this.scrollOffsetY + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, StickerMasksAlert.this.gridView, ((BottomSheet) StickerMasksAlert.this).containerView.getMeasuredHeight(), StickerMasksAlert.this.contentPreviewViewerDelegate, this.resourcesProvider);
            }
        };
        this.gridView = recyclerListView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 5) { // from class: org.telegram.ui.Components.StickerMasksAlert.4
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i2) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) { // from class: org.telegram.ui.Components.StickerMasksAlert.4.1
                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateDyToMakeVisible(View view, int i3) {
                        return super.calculateDyToMakeVisible(view, i3) - (StickerMasksAlert.this.gridView.getPaddingTop() - AndroidUtilities.dp(7.0f));
                    }

                    /* JADX INFO: Access modifiers changed from: protected */
                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateTimeForDeceleration(int i3) {
                        return super.calculateTimeForDeceleration(i3) * 4;
                    }
                };
                linearSmoothScroller.setTargetPosition(i2);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        this.stickersLayoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.stickersLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.StickerMasksAlert.5
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int i2) {
                if (StickerMasksAlert.this.gridView.getAdapter() != StickerMasksAlert.this.stickersGridAdapter) {
                    if (i2 != StickerMasksAlert.this.stickersSearchGridAdapter.totalItems && (StickerMasksAlert.this.stickersSearchGridAdapter.cache.get(i2) == null || (StickerMasksAlert.this.stickersSearchGridAdapter.cache.get(i2) instanceof TLRPC$Document))) {
                        return 1;
                    }
                    return StickerMasksAlert.this.stickersGridAdapter.stickersPerRow;
                } else if (i2 == 0) {
                    return StickerMasksAlert.this.stickersGridAdapter.stickersPerRow;
                } else {
                    if (i2 != StickerMasksAlert.this.stickersGridAdapter.totalItems && (StickerMasksAlert.this.stickersGridAdapter.cache.get(i2) == null || (StickerMasksAlert.this.stickersGridAdapter.cache.get(i2) instanceof TLRPC$Document))) {
                        return 1;
                    }
                    return StickerMasksAlert.this.stickersGridAdapter.stickersPerRow;
                }
            }
        });
        this.gridView.setPadding(0, AndroidUtilities.dp(52.0f), 0, AndroidUtilities.dp(48.0f));
        this.gridView.setClipToPadding(false);
        this.gridView.setHorizontalScrollBarEnabled(false);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.setGlowColor(-14342875);
        this.stickersSearchGridAdapter = new StickersSearchGridAdapter(context);
        RecyclerListView recyclerListView2 = this.gridView;
        StickersGridAdapter stickersGridAdapter = new StickersGridAdapter(context);
        this.stickersGridAdapter = stickersGridAdapter;
        recyclerListView2.setAdapter(stickersGridAdapter);
        this.gridView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda2
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$new$0;
                lambda$new$0 = StickerMasksAlert.this.lambda$new$0(resourcesProvider, view, motionEvent);
                return lambda$new$0;
            }
        });
        RecyclerListView.OnItemClickListener onItemClickListener = new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                StickerMasksAlert.this.lambda$new$1(view, i2);
            }
        };
        this.stickersOnItemClickListener = onItemClickListener;
        this.gridView.setOnItemClickListener(onItemClickListener);
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f));
        this.stickersTab = new ScrollSlidingTabStrip(this, context, resourcesProvider) { // from class: org.telegram.ui.Components.StickerMasksAlert.6
            @Override // org.telegram.ui.Components.ScrollSlidingTabStrip, android.widget.HorizontalScrollView, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(motionEvent);
            }
        };
        SearchField searchField = new SearchField(context, 0);
        this.stickersSearchField = searchField;
        this.containerView.addView(searchField, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
        this.stickersTab.setType(ScrollSlidingTabStrip.Type.TAB);
        this.stickersTab.setUnderlineHeight(AndroidUtilities.getShadowHeight());
        this.stickersTab.setIndicatorColor(-9520403);
        this.stickersTab.setUnderlineColor(-16053493);
        this.stickersTab.setBackgroundColor(-14342875);
        this.containerView.addView(this.stickersTab, LayoutHelper.createFrame(-1, 36, 51));
        this.stickersTab.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate() { // from class: org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate
            public final void onPageSelected(int i2) {
                StickerMasksAlert.this.lambda$new$2(i2);
            }
        });
        this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.StickerMasksAlert.7
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i2) {
                if (i2 == 1) {
                    StickerMasksAlert.this.stickersSearchField.hideKeyboard();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i2, int i3) {
                StickerMasksAlert.this.updateLayout(true);
            }
        });
        View view = new View(context);
        view.setBackgroundDrawable(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, -1907225));
        this.containerView.addView(view, LayoutHelper.createFrame(-1, 6.0f));
        if (!z) {
            this.bottomTabContainer = new FrameLayout(this, context) { // from class: org.telegram.ui.Components.StickerMasksAlert.8
                @Override // android.view.ViewGroup
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
            ImageView imageView = new ImageView(this, context) { // from class: org.telegram.ui.Components.StickerMasksAlert.9
                @Override // android.widget.ImageView, android.view.View
                public void setSelected(boolean z2) {
                    super.setSelected(z2);
                    Drawable background = getBackground();
                    if (Build.VERSION.SDK_INT < 21 || background == null) {
                        return;
                    }
                    int i2 = z2 ? -9520403 : NUM;
                    Theme.setSelectorDrawableColor(background, Color.argb(30, Color.red(i2), Color.green(i2), Color.blue(i2)), true);
                }
            };
            this.stickersButton = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.stickersButton.setImageDrawable(Theme.createEmojiIconSelectorDrawable(context, R.drawable.smiles_tab_stickers, -1, -9520403));
            int i2 = Build.VERSION.SDK_INT;
            if (i2 >= 21) {
                RippleDrawable rippleDrawable = (RippleDrawable) Theme.createSelectorDrawable(NUM);
                Theme.setRippleDrawableForceSoftware(rippleDrawable);
                this.stickersButton.setBackground(rippleDrawable);
            }
            linearLayout.addView(this.stickersButton, LayoutHelper.createLinear(70, 48));
            this.stickersButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view4) {
                    StickerMasksAlert.this.lambda$new$3(view4);
                }
            });
            ImageView imageView2 = new ImageView(this, context) { // from class: org.telegram.ui.Components.StickerMasksAlert.10
                @Override // android.widget.ImageView, android.view.View
                public void setSelected(boolean z2) {
                    super.setSelected(z2);
                    Drawable background = getBackground();
                    if (Build.VERSION.SDK_INT < 21 || background == null) {
                        return;
                    }
                    int i3 = z2 ? -9520403 : NUM;
                    Theme.setSelectorDrawableColor(background, Color.argb(30, Color.red(i3), Color.green(i3), Color.blue(i3)), true);
                }
            };
            this.masksButton = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.masksButton.setImageDrawable(Theme.createEmojiIconSelectorDrawable(context, R.drawable.ic_masks_msk1, -1, -9520403));
            if (i2 >= 21) {
                RippleDrawable rippleDrawable2 = (RippleDrawable) Theme.createSelectorDrawable(NUM);
                Theme.setRippleDrawableForceSoftware(rippleDrawable2);
                this.masksButton.setBackground(rippleDrawable2);
            }
            linearLayout.addView(this.masksButton, LayoutHelper.createLinear(70, 48));
            this.masksButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.StickerMasksAlert$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view4) {
                    StickerMasksAlert.this.lambda$new$4(view4);
                }
            });
        }
        checkDocuments(true);
        reloadStickersAdapter();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$0(Theme.ResourcesProvider resourcesProvider, View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.gridView, this.containerView.getMeasuredHeight(), this.stickersOnItemClickListener, this.contentPreviewViewerDelegate, resourcesProvider);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view, int i) {
        if (!(view instanceof StickerEmojiCell)) {
            return;
        }
        ContentPreviewViewer.getInstance().reset();
        StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
        this.delegate.onStickerSelected(stickerEmojiCell.getParentObject(), stickerEmojiCell.getSticker());
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(int i) {
        int positionForPack;
        if (i == this.recentTabBum) {
            positionForPack = this.stickersGridAdapter.getPositionForPack("recent");
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            int i2 = this.recentTabBum;
            scrollSlidingTabStrip.onPageScrolled(i2, i2 > 0 ? i2 : this.stickersTabOffset);
        } else if (i == this.favTabBum) {
            positionForPack = this.stickersGridAdapter.getPositionForPack("fav");
            ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
            int i3 = this.favTabBum;
            scrollSlidingTabStrip2.onPageScrolled(i3, i3 > 0 ? i3 : this.stickersTabOffset);
        } else {
            int i4 = i - this.stickersTabOffset;
            if (i4 >= this.stickerSets[this.currentType].size()) {
                return;
            }
            if (i4 >= this.stickerSets[this.currentType].size()) {
                i4 = this.stickerSets[this.currentType].size() - 1;
            }
            positionForPack = this.stickersGridAdapter.getPositionForPack(this.stickerSets[this.currentType].get(i4));
        }
        if (this.stickersLayoutManager.findFirstVisibleItemPosition() == positionForPack) {
            return;
        }
        this.stickersLayoutManager.scrollToPositionWithOffset(positionForPack, (-this.gridView.getPaddingTop()) + this.searchFieldHeight + AndroidUtilities.dp(48.0f));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view) {
        if (this.currentType == 0) {
            return;
        }
        this.currentType = 0;
        updateType();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(View view) {
        if (this.currentType == 1) {
            return;
        }
        this.currentType = 1;
        updateType();
    }

    private void updateType() {
        View childAt;
        RecyclerView.ViewHolder findContainingViewHolder;
        int top;
        if (this.gridView.getChildCount() > 0 && (findContainingViewHolder = this.gridView.findContainingViewHolder((childAt = this.gridView.getChildAt(0)))) != null) {
            if (findContainingViewHolder.getAdapterPosition() != 0) {
                top = -this.gridView.getPaddingTop();
            } else {
                top = childAt.getTop() + (-this.gridView.getPaddingTop());
            }
            this.stickersLayoutManager.scrollToPositionWithOffset(0, top);
        }
        checkDocuments(true);
    }

    public void setDelegate(StickerMasksAlertDelegate stickerMasksAlertDelegate) {
        this.delegate = stickerMasksAlertDelegate;
    }

    /* JADX INFO: Access modifiers changed from: private */
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
            this.stickersTab.setTranslationY(i);
            this.stickersSearchField.setTranslationY(i + AndroidUtilities.dp(48.0f));
            this.containerView.invalidate();
        }
        RecyclerListView.Holder holder3 = (RecyclerListView.Holder) this.gridView.findViewHolderForAdapterPosition(0);
        if (holder3 == null) {
            this.stickersSearchField.showShadow(true, z);
        } else {
            this.stickersSearchField.showShadow(holder3.itemView.getTop() < this.gridView.getPaddingTop(), z);
        }
        RecyclerView.Adapter adapter = this.gridView.getAdapter();
        StickersSearchGridAdapter stickersSearchGridAdapter = this.stickersSearchGridAdapter;
        if (adapter == stickersSearchGridAdapter && (holder = (RecyclerListView.Holder) this.gridView.findViewHolderForAdapterPosition(stickersSearchGridAdapter.getItemCount() - 1)) != null && holder.getItemViewType() == 5) {
            FrameLayout frameLayout = (FrameLayout) holder.itemView;
            int childCount = frameLayout.getChildCount();
            float f = (-((frameLayout.getTop() - this.searchFieldHeight) - AndroidUtilities.dp(48.0f))) / 2;
            for (int i2 = 0; i2 < childCount; i2++) {
                frameLayout.getChildAt(i2).setTranslationY(f);
            }
        }
        checkPanels();
    }

    private void updateStickerTabs() {
        ArrayList<TLRPC$Document> arrayList;
        if (this.stickersTab == null) {
            return;
        }
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
            this.stickersTab.addIconTab(1, this.stickerIcons[1]).setContentDescription(LocaleController.getString("FavoriteStickers", R.string.FavoriteStickers));
        }
        if (!this.recentStickers[this.currentType].isEmpty()) {
            int i2 = this.stickersTabOffset;
            this.recentTabBum = i2;
            this.stickersTabOffset = i2 + 1;
            this.stickersTab.addIconTab(0, this.stickerIcons[0]).setContentDescription(LocaleController.getString("RecentStickers", R.string.RecentStickers));
        }
        this.stickerSets[this.currentType].clear();
        ArrayList<TLRPC$TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType);
        for (int i3 = 0; i3 < stickerSets.size(); i3++) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = stickerSets.get(i3);
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
            addStickerTab.setContentDescription(tLRPC$TL_messages_stickerSet2.set.title + ", " + LocaleController.getString("AccDescrStickerSet", R.string.AccDescrStickerSet));
        }
        this.stickersTab.commitUpdate();
        this.stickersTab.updateTabStyles();
        if (currentPosition != 0) {
            this.stickersTab.onPageScrolled(currentPosition, currentPosition);
        }
        checkPanels();
    }

    private void checkPanels() {
        if (this.stickersTab == null) {
            return;
        }
        int childCount = this.gridView.getChildCount();
        View view = null;
        for (int i = 0; i < childCount; i++) {
            view = this.gridView.getChildAt(i);
            if (view.getBottom() > this.searchFieldHeight + AndroidUtilities.dp(48.0f)) {
                break;
            }
        }
        if (view == null) {
            return;
        }
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(view);
        int adapterPosition = holder != null ? holder.getAdapterPosition() : -1;
        if (adapterPosition == -1) {
            return;
        }
        int i2 = this.favTabBum;
        if (i2 <= 0 && (i2 = this.recentTabBum) <= 0) {
            i2 = this.stickersTabOffset;
        }
        this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(adapterPosition), i2);
    }

    private void reloadStickersAdapter() {
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
        }
        StickersSearchGridAdapter stickersSearchGridAdapter = this.stickersSearchGridAdapter;
        if (stickersSearchGridAdapter != null) {
            stickersSearchGridAdapter.notifyDataSetChanged();
        }
        if (ContentPreviewViewer.getInstance().isVisible()) {
            ContentPreviewViewer.getInstance().close();
        }
        ContentPreviewViewer.getInstance().reset();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
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
                    if (i2 < this.recentStickers[this.currentType].size()) {
                        TLRPC$Document tLRPC$Document2 = this.recentStickers[this.currentType].get(i2);
                        if (tLRPC$Document2.dc_id == tLRPC$Document.dc_id && tLRPC$Document2.id == tLRPC$Document.id) {
                            this.recentStickers[this.currentType].remove(i2);
                            break;
                        }
                        i2++;
                    }
                }
            }
        }
        if (z || size != this.recentStickers[this.currentType].size() || size2 != this.favouriteStickers.size()) {
            updateStickerTabs();
        }
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
        }
        if (!z) {
            checkPanels();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        RecyclerListView recyclerListView;
        if (i == NotificationCenter.stickersDidLoad) {
            if (((Integer) objArr[0]).intValue() != this.currentType) {
                return;
            }
            updateStickerTabs();
            reloadStickersAdapter();
            checkPanels();
        } else if (i == NotificationCenter.recentDocumentsDidLoad) {
            boolean booleanValue = ((Boolean) objArr[0]).booleanValue();
            int intValue = ((Integer) objArr[1]).intValue();
            if (booleanValue) {
                return;
            }
            if (intValue != this.currentType && intValue != 2) {
                return;
            }
            checkDocuments(false);
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

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class StickersGridAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private int stickersPerRow;
        private int totalItems;
        private SparseArray<Object> rowStartPack = new SparseArray<>();
        private HashMap<Object, Integer> packStartPosition = new HashMap<>();
        private SparseArray<Object> cache = new SparseArray<>();
        private SparseArray<Object> cacheParents = new SparseArray<>();
        private SparseIntArray positionToRow = new SparseIntArray();

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public StickersGridAdapter(Context context) {
            this.context = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
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

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == 0) {
                return 4;
            }
            Object obj = this.cache.get(i);
            if (obj == null) {
                return 1;
            }
            return obj instanceof TLRPC$Document ? 0 : 2;
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
            if (obj instanceof String) {
                return "recent".equals(obj) ? StickerMasksAlert.this.recentTabBum : StickerMasksAlert.this.favTabBum;
            }
            return StickerMasksAlert.this.stickerSets[StickerMasksAlert.this.currentType].indexOf((TLRPC$TL_messages_stickerSet) obj) + StickerMasksAlert.this.stickersTabOffset;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1822onCreateViewHolder(ViewGroup viewGroup, int i) {
            StickerEmojiCell stickerEmojiCell;
            View view;
            if (i == 0) {
                stickerEmojiCell = new StickerEmojiCell(this, this.context, false) { // from class: org.telegram.ui.Components.StickerMasksAlert.StickersGridAdapter.1
                    @Override // android.widget.FrameLayout, android.view.View
                    public void onMeasure(int i2, int i3) {
                        super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                    }
                };
            } else {
                if (i == 1) {
                    view = new EmptyCell(this.context);
                } else if (i == 2) {
                    StickerSetNameCell stickerSetNameCell = new StickerSetNameCell(this.context, false, ((BottomSheet) StickerMasksAlert.this).resourcesProvider);
                    stickerSetNameCell.setTitleColor(-7829368);
                    stickerEmojiCell = stickerSetNameCell;
                } else if (i != 4) {
                    view = null;
                } else {
                    view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, StickerMasksAlert.this.searchFieldHeight + AndroidUtilities.dp(48.0f)));
                }
                return new RecyclerListView.Holder(view);
            }
            view = stickerEmojiCell;
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ArrayList<TLRPC$Document> arrayList;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                TLRPC$Document tLRPC$Document = (TLRPC$Document) this.cache.get(i);
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) viewHolder.itemView;
                stickerEmojiCell.setSticker(tLRPC$Document, this.cacheParents.get(i), false);
                stickerEmojiCell.setRecent(StickerMasksAlert.this.recentStickers[StickerMasksAlert.this.currentType].contains(tLRPC$Document));
                return;
            }
            int i2 = 1;
            if (itemViewType != 1) {
                if (itemViewType != 2) {
                    return;
                }
                StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                Object obj = this.cache.get(i);
                if (!(obj instanceof TLRPC$TL_messages_stickerSet)) {
                    if (obj != StickerMasksAlert.this.recentStickers[StickerMasksAlert.this.currentType]) {
                        if (obj != StickerMasksAlert.this.favouriteStickers) {
                            return;
                        }
                        stickerSetNameCell.setText(LocaleController.getString("FavoriteStickers", R.string.FavoriteStickers), 0);
                        return;
                    }
                    stickerSetNameCell.setText(LocaleController.getString("RecentStickers", R.string.RecentStickers), 0);
                    return;
                }
                TLRPC$StickerSet tLRPC$StickerSet = ((TLRPC$TL_messages_stickerSet) obj).set;
                if (tLRPC$StickerSet == null) {
                    return;
                }
                stickerSetNameCell.setText(tLRPC$StickerSet.title, 0);
                return;
            }
            EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
            if (i == this.totalItems) {
                int i3 = this.positionToRow.get(i - 1, Integer.MIN_VALUE);
                if (i3 == Integer.MIN_VALUE) {
                    emptyCell.setHeight(1);
                    return;
                }
                Object obj2 = this.rowStartPack.get(i3);
                if (obj2 instanceof TLRPC$TL_messages_stickerSet) {
                    arrayList = ((TLRPC$TL_messages_stickerSet) obj2).documents;
                } else if (obj2 instanceof String) {
                    arrayList = "recent".equals(obj2) ? StickerMasksAlert.this.recentStickers[StickerMasksAlert.this.currentType] : StickerMasksAlert.this.favouriteStickers;
                } else {
                    arrayList = null;
                }
                if (arrayList == null) {
                    emptyCell.setHeight(1);
                    return;
                } else if (!arrayList.isEmpty()) {
                    int height = StickerMasksAlert.this.gridView.getHeight() - (((int) Math.ceil(arrayList.size() / this.stickersPerRow)) * AndroidUtilities.dp(82.0f));
                    if (height > 0) {
                        i2 = height;
                    }
                    emptyCell.setHeight(i2);
                    return;
                } else {
                    emptyCell.setHeight(AndroidUtilities.dp(8.0f));
                    return;
                }
            }
            emptyCell.setHeight(AndroidUtilities.dp(82.0f));
        }

        /* JADX WARN: Removed duplicated region for block: B:21:0x00ca  */
        /* JADX WARN: Removed duplicated region for block: B:26:0x00e3  */
        /* JADX WARN: Removed duplicated region for block: B:27:0x00eb  */
        /* JADX WARN: Removed duplicated region for block: B:31:0x0100  */
        /* JADX WARN: Removed duplicated region for block: B:39:0x0133  */
        /* JADX WARN: Removed duplicated region for block: B:54:0x015a A[ADDED_TO_REGION, SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:59:0x014e A[EDGE_INSN: B:59:0x014e->B:47:0x014e ?: BREAK  , SYNTHETIC] */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void notifyDataSetChanged() {
            /*
                Method dump skipped, instructions count: 356
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerMasksAlert.StickersGridAdapter.notifyDataSetChanged():void");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class StickersSearchGridAdapter extends RecyclerListView.SelectionAdapter {
        boolean cleared;
        private Context context;
        private int emojiSearchId;
        private int reqId2;
        private String searchQuery;
        private int totalItems;
        private SparseArray<Object> rowStartPack = new SparseArray<>();
        private SparseArray<Object> cache = new SparseArray<>();
        private SparseArray<Object> cacheParent = new SparseArray<>();
        private SparseIntArray positionToRow = new SparseIntArray();
        private SparseArray<String> positionToEmoji = new SparseArray<>();
        private ArrayList<TLRPC$TL_messages_stickerSet> localPacks = new ArrayList<>();
        private HashMap<TLRPC$TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap<>();
        private HashMap<TLRPC$TL_messages_stickerSet, Integer> localPacksByName = new HashMap<>();
        private HashMap<ArrayList<TLRPC$Document>, String> emojiStickers = new HashMap<>();
        private ArrayList<ArrayList<TLRPC$Document>> emojiArrays = new ArrayList<>();
        private Runnable searchRunnable = new AnonymousClass1();

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        static /* synthetic */ int access$4904(StickersSearchGridAdapter stickersSearchGridAdapter) {
            int i = stickersSearchGridAdapter.emojiSearchId + 1;
            stickersSearchGridAdapter.emojiSearchId = i;
            return i;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter$1  reason: invalid class name */
        /* loaded from: classes3.dex */
        public class AnonymousClass1 implements Runnable {
            AnonymousClass1() {
            }

            private void clear() {
                StickersSearchGridAdapter stickersSearchGridAdapter = StickersSearchGridAdapter.this;
                if (stickersSearchGridAdapter.cleared) {
                    return;
                }
                stickersSearchGridAdapter.cleared = true;
                stickersSearchGridAdapter.emojiStickers.clear();
                StickersSearchGridAdapter.this.emojiArrays.clear();
                StickersSearchGridAdapter.this.localPacks.clear();
                StickersSearchGridAdapter.this.localPacksByShortName.clear();
                StickersSearchGridAdapter.this.localPacksByName.clear();
            }

            /* JADX WARN: Code restructure failed: missing block: B:16:0x006c, code lost:
                if (r5.charAt(r9) <= 57343) goto L17;
             */
            /* JADX WARN: Code restructure failed: missing block: B:22:0x0086, code lost:
                if (r5.charAt(r9) != 9794) goto L27;
             */
            @Override // java.lang.Runnable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void run() {
                /*
                    Method dump skipped, instructions count: 808
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.AnonymousClass1.run():void");
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$run$0(int i, HashMap hashMap, ArrayList arrayList, String str) {
                if (i != StickersSearchGridAdapter.this.emojiSearchId) {
                    return;
                }
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
                if (!z) {
                    if (StickersSearchGridAdapter.this.reqId2 != 0) {
                        return;
                    }
                    clear();
                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                    return;
                }
                StickersSearchGridAdapter.this.notifyDataSetChanged();
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$run$2(final TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers, final ArrayList arrayList, final LongSparseArray longSparseArray, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.StickerMasksAlert$StickersSearchGridAdapter$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        StickerMasksAlert.StickersSearchGridAdapter.AnonymousClass1.this.lambda$run$1(tLRPC$TL_messages_getStickers, tLObject, arrayList, longSparseArray);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$run$1(TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers, TLObject tLObject, ArrayList arrayList, LongSparseArray longSparseArray) {
                if (tLRPC$TL_messages_getStickers.emoticon.equals(StickersSearchGridAdapter.this.searchQuery)) {
                    StickerMasksAlert.this.stickersSearchField.progressDrawable.stopAnimation();
                    StickersSearchGridAdapter.this.reqId2 = 0;
                    if (!(tLObject instanceof TLRPC$TL_messages_stickers)) {
                        return;
                    }
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
                    if (StickerMasksAlert.this.gridView.getAdapter() == StickerMasksAlert.this.stickersSearchGridAdapter) {
                        return;
                    }
                    StickerMasksAlert.this.gridView.setAdapter(StickerMasksAlert.this.stickersSearchGridAdapter);
                }
            }
        }

        public StickersSearchGridAdapter(Context context) {
            this.context = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
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
            AndroidUtilities.runOnUIThread(this.searchRunnable, 300L);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == 0) {
                return 4;
            }
            if (i == 1 && this.totalItems == 1) {
                return 5;
            }
            Object obj = this.cache.get(i);
            if (obj == null) {
                return 1;
            }
            return obj instanceof TLRPC$Document ? 0 : 2;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1822onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            FrameLayout frameLayout2;
            if (i == 0) {
                frameLayout = new StickerEmojiCell(this, this.context, false) { // from class: org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.2
                    @Override // android.widget.FrameLayout, android.view.View
                    public void onMeasure(int i2, int i3) {
                        super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                    }
                };
            } else {
                if (i == 1) {
                    frameLayout2 = new EmptyCell(this.context);
                } else if (i == 2) {
                    frameLayout = new StickerSetNameCell(this.context, false, ((BottomSheet) StickerMasksAlert.this).resourcesProvider);
                } else if (i == 4) {
                    View view = new View(this.context);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, StickerMasksAlert.this.searchFieldHeight + AndroidUtilities.dp(48.0f)));
                    frameLayout2 = view;
                } else if (i != 5) {
                    frameLayout2 = null;
                } else {
                    FrameLayout frameLayout3 = new FrameLayout(this.context) { // from class: org.telegram.ui.Components.StickerMasksAlert.StickersSearchGridAdapter.3
                        @Override // android.widget.FrameLayout, android.view.View
                        protected void onMeasure(int i2, int i3) {
                            super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(((StickerMasksAlert.this.gridView.getMeasuredHeight() - StickerMasksAlert.this.searchFieldHeight) - AndroidUtilities.dp(48.0f)) - AndroidUtilities.dp(48.0f), NUM));
                        }
                    };
                    ImageView imageView = new ImageView(this.context);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    imageView.setImageResource(R.drawable.stickers_empty);
                    imageView.setColorFilter(new PorterDuffColorFilter(-7038047, PorterDuff.Mode.MULTIPLY));
                    frameLayout3.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 50.0f));
                    TextView textView = new TextView(this.context);
                    textView.setText(LocaleController.getString("NoStickersFound", R.string.NoStickersFound));
                    textView.setTextSize(1, 16.0f);
                    textView.setTextColor(-7038047);
                    frameLayout3.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
                    frameLayout3.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    frameLayout2 = frameLayout3;
                }
                return new RecyclerListView.Holder(frameLayout2);
            }
            frameLayout2 = frameLayout;
            return new RecyclerListView.Holder(frameLayout2);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            int i2 = 1;
            if (itemViewType == 0) {
                TLRPC$Document tLRPC$Document = (TLRPC$Document) this.cache.get(i);
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) viewHolder.itemView;
                stickerEmojiCell.setSticker(tLRPC$Document, null, this.cacheParent.get(i), this.positionToEmoji.get(i), false);
                if (StickerMasksAlert.this.recentStickers[StickerMasksAlert.this.currentType].contains(tLRPC$Document) || StickerMasksAlert.this.favouriteStickers.contains(tLRPC$Document)) {
                    z = true;
                }
                stickerEmojiCell.setRecent(z);
                return;
            }
            Integer num = null;
            if (itemViewType != 1) {
                if (itemViewType != 2) {
                    return;
                }
                StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                Object obj = this.cache.get(i);
                if (!(obj instanceof TLRPC$TL_messages_stickerSet)) {
                    return;
                }
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) obj;
                if (!TextUtils.isEmpty(this.searchQuery) && this.localPacksByShortName.containsKey(tLRPC$TL_messages_stickerSet)) {
                    TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
                    if (tLRPC$StickerSet != null) {
                        stickerSetNameCell.setText(tLRPC$StickerSet.title, 0);
                    }
                    stickerSetNameCell.setUrl(tLRPC$TL_messages_stickerSet.set.short_name, this.searchQuery.length());
                    return;
                }
                Integer num2 = this.localPacksByName.get(tLRPC$TL_messages_stickerSet);
                TLRPC$StickerSet tLRPC$StickerSet2 = tLRPC$TL_messages_stickerSet.set;
                if (tLRPC$StickerSet2 != null && num2 != null) {
                    stickerSetNameCell.setText(tLRPC$StickerSet2.title, 0, num2.intValue(), !TextUtils.isEmpty(this.searchQuery) ? this.searchQuery.length() : 0);
                }
                stickerSetNameCell.setUrl(null, 0);
                return;
            }
            EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
            if (i == this.totalItems) {
                int i3 = this.positionToRow.get(i - 1, Integer.MIN_VALUE);
                if (i3 == Integer.MIN_VALUE) {
                    emptyCell.setHeight(1);
                    return;
                }
                Object obj2 = this.rowStartPack.get(i3);
                if (obj2 instanceof TLRPC$TL_messages_stickerSet) {
                    num = Integer.valueOf(((TLRPC$TL_messages_stickerSet) obj2).documents.size());
                } else if (obj2 instanceof Integer) {
                    num = (Integer) obj2;
                }
                if (num == null) {
                    emptyCell.setHeight(1);
                    return;
                } else if (num.intValue() != 0) {
                    int height = StickerMasksAlert.this.gridView.getHeight() - (((int) Math.ceil(num.intValue() / StickerMasksAlert.this.stickersGridAdapter.stickersPerRow)) * AndroidUtilities.dp(82.0f));
                    if (height > 0) {
                        i2 = height;
                    }
                    emptyCell.setHeight(i2);
                    return;
                } else {
                    emptyCell.setHeight(AndroidUtilities.dp(8.0f));
                    return;
                }
            }
            emptyCell.setHeight(AndroidUtilities.dp(82.0f));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            int i;
            this.rowStartPack.clear();
            this.positionToRow.clear();
            this.cache.clear();
            this.positionToEmoji.clear();
            this.totalItems = 0;
            int size = this.localPacks.size();
            int i2 = !this.emojiArrays.isEmpty() ? 1 : 0;
            int i3 = -1;
            int i4 = -1;
            int i5 = 0;
            while (i4 < size + i2) {
                if (i4 == i3) {
                    SparseArray<Object> sparseArray = this.cache;
                    int i6 = this.totalItems;
                    this.totalItems = i6 + 1;
                    sparseArray.put(i6, "search");
                    i5++;
                } else if (i4 < size) {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.localPacks.get(i4);
                    ArrayList<TLRPC$Document> arrayList = tLRPC$TL_messages_stickerSet.documents;
                    if (!arrayList.isEmpty()) {
                        int ceil = (int) Math.ceil(arrayList.size() / StickerMasksAlert.this.stickersGridAdapter.stickersPerRow);
                        this.cache.put(this.totalItems, tLRPC$TL_messages_stickerSet);
                        this.positionToRow.put(this.totalItems, i5);
                        int size2 = arrayList.size();
                        int i7 = 0;
                        while (i7 < size2) {
                            int i8 = i7 + 1;
                            int i9 = this.totalItems + i8;
                            int i10 = i5 + 1 + (i7 / StickerMasksAlert.this.stickersGridAdapter.stickersPerRow);
                            this.cache.put(i9, arrayList.get(i7));
                            this.cacheParent.put(i9, tLRPC$TL_messages_stickerSet);
                            this.positionToRow.put(i9, i10);
                            i7 = i8;
                        }
                        int i11 = ceil + 1;
                        for (int i12 = 0; i12 < i11; i12++) {
                            this.rowStartPack.put(i5 + i12, tLRPC$TL_messages_stickerSet);
                        }
                        this.totalItems += (ceil * StickerMasksAlert.this.stickersGridAdapter.stickersPerRow) + 1;
                        i5 += i11;
                    }
                } else {
                    int size3 = this.emojiArrays.size();
                    String str = "";
                    int i13 = 0;
                    for (int i14 = 0; i14 < size3; i14++) {
                        ArrayList<TLRPC$Document> arrayList2 = this.emojiArrays.get(i14);
                        String str2 = this.emojiStickers.get(arrayList2);
                        if (str2 != null && !str.equals(str2)) {
                            this.positionToEmoji.put(this.totalItems + i13, str2);
                            str = str2;
                        }
                        int size4 = arrayList2.size();
                        int i15 = 0;
                        while (i15 < size4) {
                            int i16 = this.totalItems + i13;
                            int i17 = (i13 / StickerMasksAlert.this.stickersGridAdapter.stickersPerRow) + i5;
                            TLRPC$Document tLRPC$Document = arrayList2.get(i15);
                            this.cache.put(i16, tLRPC$Document);
                            int i18 = size;
                            TLRPC$TL_messages_stickerSet stickerSetById = MediaDataController.getInstance(StickerMasksAlert.this.currentAccount).getStickerSetById(MediaDataController.getStickerSetId(tLRPC$Document));
                            if (stickerSetById != null) {
                                this.cacheParent.put(i16, stickerSetById);
                            }
                            this.positionToRow.put(i16, i17);
                            i13++;
                            i15++;
                            size = i18;
                        }
                    }
                    i = size;
                    int ceil2 = (int) Math.ceil(i13 / StickerMasksAlert.this.stickersGridAdapter.stickersPerRow);
                    for (int i19 = 0; i19 < ceil2; i19++) {
                        this.rowStartPack.put(i5 + i19, Integer.valueOf(i13));
                    }
                    this.totalItems += StickerMasksAlert.this.stickersGridAdapter.stickersPerRow * ceil2;
                    i5 += ceil2;
                    i4++;
                    size = i;
                    i3 = -1;
                }
                i = size;
                i4++;
                size = i;
                i3 = -1;
            }
            super.notifyDataSetChanged();
        }
    }
}
