package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Cells.StickerSetGroupInfoCell;
import org.telegram.ui.Cells.StickerSetNameCell;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.PagerSlidingTabStrip;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ContentPreviewViewer;

public class EmojiView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public static final ViewTreeObserver.OnScrollChangedListener NOP = $$Lambda$EmojiView$PYmfpY1F_JSo9ExUvlHDRxz4bQ.INSTANCE;
    /* access modifiers changed from: private */
    public static final Field superListenerField;
    /* access modifiers changed from: private */
    public ImageView backspaceButton;
    private AnimatorSet backspaceButtonAnimation;
    /* access modifiers changed from: private */
    public boolean backspaceOnce;
    /* access modifiers changed from: private */
    public boolean backspacePressed;
    private FrameLayout bottomTabContainer;
    private AnimatorSet bottomTabContainerAnimation;
    private View bottomTabContainerBackground;
    /* access modifiers changed from: private */
    public ContentPreviewViewer.ContentPreviewViewerDelegate contentPreviewViewerDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() {
        public /* synthetic */ boolean needOpen() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needOpen(this);
        }

        public boolean needSend() {
            return true;
        }

        public void sendSticker(TLRPC.Document document, Object obj, boolean z, int i) {
            EmojiView.this.delegate.onStickerSelected((View) null, document, obj, z, i);
        }

        public boolean canSchedule() {
            return EmojiView.this.delegate.canSchedule();
        }

        public boolean isInScheduleMode() {
            return EmojiView.this.delegate.isInScheduleMode();
        }

        public void openSet(TLRPC.InputStickerSet inputStickerSet, boolean z) {
            if (inputStickerSet != null) {
                EmojiView.this.delegate.onShowStickerSet((TLRPC.StickerSet) null, inputStickerSet);
            }
        }

        public void sendGif(Object obj, boolean z, int i) {
            if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter) {
                EmojiView.this.delegate.onGifSelected((View) null, obj, "gif", z, i);
            } else if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter) {
                EmojiView.this.delegate.onGifSelected((View) null, obj, EmojiView.this.gifSearchAdapter.bot, z, i);
            }
        }

        public void gifAddedOrDeleted() {
            EmojiView emojiView = EmojiView.this;
            ArrayList unused = emojiView.recentGifs = MediaDataController.getInstance(emojiView.currentAccount).getRecentGifs();
            if (EmojiView.this.gifAdapter != null) {
                EmojiView.this.gifAdapter.notifyDataSetChanged();
            }
        }

        public long getDialogId() {
            return EmojiView.this.delegate.getDialogId();
        }
    };
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    private int currentBackgroundType = -1;
    /* access modifiers changed from: private */
    public int currentChatId;
    private int currentPage;
    /* access modifiers changed from: private */
    public EmojiViewDelegate delegate;
    /* access modifiers changed from: private */
    public Paint dotPaint;
    /* access modifiers changed from: private */
    public DragListener dragListener;
    /* access modifiers changed from: private */
    public EmojiGridAdapter emojiAdapter;
    private FrameLayout emojiContainer;
    /* access modifiers changed from: private */
    public RecyclerListView emojiGridView;
    private Drawable[] emojiIcons;
    /* access modifiers changed from: private */
    public float emojiLastX;
    /* access modifiers changed from: private */
    public float emojiLastY;
    /* access modifiers changed from: private */
    public GridLayoutManager emojiLayoutManager;
    private int emojiMinusDy;
    /* access modifiers changed from: private */
    public EmojiSearchAdapter emojiSearchAdapter;
    /* access modifiers changed from: private */
    public SearchField emojiSearchField;
    /* access modifiers changed from: private */
    public int emojiSize;
    /* access modifiers changed from: private */
    public AnimatorSet emojiTabShadowAnimator;
    /* access modifiers changed from: private */
    public ScrollSlidingTabStrip emojiTabs;
    private View emojiTabsShadow;
    /* access modifiers changed from: private */
    public String[] emojiTitles;
    /* access modifiers changed from: private */
    public ImageViewEmoji emojiTouchedView;
    /* access modifiers changed from: private */
    public float emojiTouchedX;
    /* access modifiers changed from: private */
    public float emojiTouchedY;
    /* access modifiers changed from: private */
    public int favTabBum = -2;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Document> favouriteStickers = new ArrayList<>();
    /* access modifiers changed from: private */
    public int featuredStickersHash;
    /* access modifiers changed from: private */
    public boolean firstEmojiAttach = true;
    /* access modifiers changed from: private */
    public boolean firstGifAttach = true;
    /* access modifiers changed from: private */
    public boolean firstStickersAttach = true;
    private ImageView floatingButton;
    private boolean forseMultiwindowLayout;
    /* access modifiers changed from: private */
    public GifAdapter gifAdapter;
    private FrameLayout gifContainer;
    /* access modifiers changed from: private */
    public RecyclerListView gifGridView;
    /* access modifiers changed from: private */
    public ExtendedGridLayoutManager gifLayoutManager;
    private RecyclerListView.OnItemClickListener gifOnItemClickListener;
    /* access modifiers changed from: private */
    public GifSearchAdapter gifSearchAdapter;
    /* access modifiers changed from: private */
    public SearchField gifSearchField;
    /* access modifiers changed from: private */
    public int groupStickerPackNum;
    /* access modifiers changed from: private */
    public int groupStickerPackPosition;
    /* access modifiers changed from: private */
    public TLRPC.TL_messages_stickerSet groupStickerSet;
    /* access modifiers changed from: private */
    public boolean groupStickersHidden;
    private int hasRecentEmoji = -1;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull info;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC.StickerSetCovered> installingStickerSets = new LongSparseArray<>();
    private boolean isLayout;
    private float lastBottomScrollDy;
    private int lastNotifyHeight;
    private int lastNotifyHeight2;
    private int lastNotifyWidth;
    /* access modifiers changed from: private */
    public String[] lastSearchKeyboardLanguage;
    /* access modifiers changed from: private */
    public int[] location = new int[2];
    /* access modifiers changed from: private */
    public TextView mediaBanTooltip;
    /* access modifiers changed from: private */
    public boolean needEmojiSearch;
    private Object outlineProvider;
    /* access modifiers changed from: private */
    public ViewPager pager;
    /* access modifiers changed from: private */
    public EmojiColorPickerView pickerView;
    /* access modifiers changed from: private */
    public EmojiPopupWindow pickerViewPopup;
    /* access modifiers changed from: private */
    public int popupHeight;
    /* access modifiers changed from: private */
    public int popupWidth;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Document> recentGifs = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Document> recentStickers = new ArrayList<>();
    /* access modifiers changed from: private */
    public int recentTabBum = -2;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC.StickerSetCovered> removingStickerSets = new LongSparseArray<>();
    private int scrolledToTrending;
    /* access modifiers changed from: private */
    public AnimatorSet searchAnimation;
    private ImageView searchButton;
    /* access modifiers changed from: private */
    public int searchFieldHeight;
    private View shadowLine;
    private boolean showGifs;
    private Drawable[] stickerIcons;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = new ArrayList<>();
    /* access modifiers changed from: private */
    public ImageView stickerSettingsButton;
    private AnimatorSet stickersButtonAnimation;
    private FrameLayout stickersContainer;
    private TextView stickersCounter;
    /* access modifiers changed from: private */
    public StickersGridAdapter stickersGridAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView stickersGridView;
    /* access modifiers changed from: private */
    public GridLayoutManager stickersLayoutManager;
    private int stickersMinusDy;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    /* access modifiers changed from: private */
    public SearchField stickersSearchField;
    /* access modifiers changed from: private */
    public StickersSearchGridAdapter stickersSearchGridAdapter;
    /* access modifiers changed from: private */
    public ScrollSlidingTabStrip stickersTab;
    /* access modifiers changed from: private */
    public int stickersTabOffset;
    /* access modifiers changed from: private */
    public Drawable[] tabIcons;
    private View topShadow;
    /* access modifiers changed from: private */
    public TrendingGridAdapter trendingGridAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView trendingGridView;
    /* access modifiers changed from: private */
    public GridLayoutManager trendingLayoutManager;
    /* access modifiers changed from: private */
    public boolean trendingLoaded;
    private int trendingTabNum = -2;
    private PagerSlidingTabStrip typeTabs;
    /* access modifiers changed from: private */
    public ArrayList<View> views = new ArrayList<>();

    public interface DragListener {
        void onDrag(int i);

        void onDragCancel();

        void onDragEnd(float f);

        void onDragStart();
    }

    public interface EmojiViewDelegate {

        /* renamed from: org.telegram.ui.Components.EmojiView$EmojiViewDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static boolean $default$canSchedule(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static long $default$getDialogId(EmojiViewDelegate emojiViewDelegate) {
                return 0;
            }

            public static boolean $default$isExpanded(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static boolean $default$isInScheduleMode(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static boolean $default$isSearchOpened(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static boolean $default$onBackspace(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static void $default$onClearEmojiRecent(EmojiViewDelegate emojiViewDelegate) {
            }

            public static void $default$onEmojiSelected(EmojiViewDelegate emojiViewDelegate, String str) {
            }

            public static void $default$onGifSelected(EmojiViewDelegate emojiViewDelegate, View view, Object obj, Object obj2, boolean z, int i) {
            }

            public static void $default$onSearchOpenClose(EmojiViewDelegate emojiViewDelegate, int i) {
            }

            public static void $default$onShowStickerSet(EmojiViewDelegate emojiViewDelegate, TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet) {
            }

            public static void $default$onStickerSelected(EmojiViewDelegate emojiViewDelegate, View view, TLRPC.Document document, Object obj, boolean z, int i) {
            }

            public static void $default$onStickerSetAdd(EmojiViewDelegate emojiViewDelegate, TLRPC.StickerSetCovered stickerSetCovered) {
            }

            public static void $default$onStickerSetRemove(EmojiViewDelegate emojiViewDelegate, TLRPC.StickerSetCovered stickerSetCovered) {
            }

            public static void $default$onStickersGroupClick(EmojiViewDelegate emojiViewDelegate, int i) {
            }

            public static void $default$onStickersSettingsClick(EmojiViewDelegate emojiViewDelegate) {
            }

            public static void $default$onTabOpened(EmojiViewDelegate emojiViewDelegate, int i) {
            }
        }

        boolean canSchedule();

        long getDialogId();

        boolean isExpanded();

        boolean isInScheduleMode();

        boolean isSearchOpened();

        boolean onBackspace();

        void onClearEmojiRecent();

        void onEmojiSelected(String str);

        void onGifSelected(View view, Object obj, Object obj2, boolean z, int i);

        void onSearchOpenClose(int i);

        void onShowStickerSet(TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet);

        void onStickerSelected(View view, TLRPC.Document document, Object obj, boolean z, int i);

        void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered);

        void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered);

        void onStickersGroupClick(int i);

        void onStickersSettingsClick();

        void onTabOpened(int i);
    }

    static /* synthetic */ void lambda$static$0() {
    }

    static {
        Field field = null;
        try {
            field = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
            field.setAccessible(true);
        } catch (NoSuchFieldException unused) {
        }
        superListenerField = field;
    }

    private class SearchField extends FrameLayout {
        /* access modifiers changed from: private */
        public View backgroundView;
        /* access modifiers changed from: private */
        public ImageView clearSearchImageView;
        /* access modifiers changed from: private */
        public CloseProgressDrawable2 progressDrawable;
        /* access modifiers changed from: private */
        public View searchBackground;
        /* access modifiers changed from: private */
        public EditTextBoldCursor searchEditText;
        /* access modifiers changed from: private */
        public ImageView searchIconImageView;
        /* access modifiers changed from: private */
        public AnimatorSet shadowAnimator;
        /* access modifiers changed from: private */
        public View shadowView;

        public SearchField(Context context, final int i) {
            super(context);
            this.shadowView = new View(context);
            this.shadowView.setAlpha(0.0f);
            this.shadowView.setTag(1);
            this.shadowView.setBackgroundColor(Theme.getColor("chat_emojiPanelShadowLine"));
            addView(this.shadowView, new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83));
            this.backgroundView = new View(context);
            this.backgroundView.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
            addView(this.backgroundView, new FrameLayout.LayoutParams(-1, EmojiView.this.searchFieldHeight));
            this.searchBackground = new View(context);
            this.searchBackground.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor("chat_emojiSearchBackground")));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 14.0f, 14.0f, 0.0f));
            this.searchIconImageView = new ImageView(context);
            this.searchIconImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.searchIconImageView.setImageResource(NUM);
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiSearchIcon"), PorterDuff.Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 14.0f, 0.0f, 0.0f));
            this.clearSearchImageView = new ImageView(context);
            this.clearSearchImageView.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView = this.clearSearchImageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
            this.progressDrawable = closeProgressDrawable2;
            imageView.setImageDrawable(closeProgressDrawable2);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            this.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiSearchIcon"), PorterDuff.Mode.MULTIPLY));
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 14.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    EmojiView.SearchField.this.lambda$new$0$EmojiView$SearchField(view);
                }
            });
            this.searchEditText = new EditTextBoldCursor(context, EmojiView.this) {
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getAction() == 0) {
                        if (!EmojiView.this.delegate.isSearchOpened()) {
                            SearchField searchField = SearchField.this;
                            EmojiView.this.openSearch(searchField);
                        }
                        EmojiViewDelegate access$000 = EmojiView.this.delegate;
                        int i = 1;
                        if (i == 1) {
                            i = 2;
                        }
                        access$000.onSearchOpenClose(i);
                        SearchField.this.searchEditText.requestFocus();
                        AndroidUtilities.showKeyboard(SearchField.this.searchEditText);
                        if (EmojiView.this.trendingGridView != null && EmojiView.this.trendingGridView.getVisibility() == 0) {
                            EmojiView.this.showTrendingTab(false);
                        }
                    }
                    return super.onTouchEvent(motionEvent);
                }
            };
            this.searchEditText.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(Theme.getColor("chat_emojiSearchIcon"));
            this.searchEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.searchEditText.setBackgroundDrawable((Drawable) null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(NUM);
            if (i == 0) {
                this.searchEditText.setHint(LocaleController.getString("SearchStickersHint", NUM));
            } else if (i == 1) {
                this.searchEditText.setHint(LocaleController.getString("SearchEmojiHint", NUM));
            } else if (i == 2) {
                this.searchEditText.setHint(LocaleController.getString("SearchGifsTitle", NUM));
            }
            this.searchEditText.setCursorColor(Theme.getColor("featuredStickers_addedIcon"));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 12.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher(EmojiView.this) {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    boolean z = false;
                    boolean z2 = SearchField.this.searchEditText.length() > 0;
                    float f = 0.0f;
                    if (SearchField.this.clearSearchImageView.getAlpha() != 0.0f) {
                        z = true;
                    }
                    if (z2 != z) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (z2) {
                            f = 1.0f;
                        }
                        ViewPropertyAnimator scaleX = animate.alpha(f).setDuration(150).scaleX(z2 ? 1.0f : 0.1f);
                        if (!z2) {
                            f2 = 0.1f;
                        }
                        scaleX.scaleY(f2).start();
                    }
                    int i = i;
                    if (i == 0) {
                        EmojiView.this.stickersSearchGridAdapter.search(SearchField.this.searchEditText.getText().toString());
                    } else if (i == 1) {
                        EmojiView.this.emojiSearchAdapter.search(SearchField.this.searchEditText.getText().toString());
                    } else if (i == 2) {
                        EmojiView.this.gifSearchAdapter.search(SearchField.this.searchEditText.getText().toString());
                    }
                }
            });
        }

        public /* synthetic */ void lambda$new$0$EmojiView$SearchField(View view) {
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
                    this.shadowAnimator = new AnimatorSet();
                    AnimatorSet animatorSet2 = this.shadowAnimator;
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

    private class ImageViewEmoji extends ImageView {
        /* access modifiers changed from: private */
        public boolean isRecent;

        public ImageViewEmoji(Context context) {
            super(context);
            setScaleType(ImageView.ScaleType.CENTER);
        }

        /* access modifiers changed from: private */
        public void sendEmoji(String str) {
            String str2;
            String str3;
            EmojiView.this.showBottomTab(true, true);
            if (str != null) {
                str2 = str;
            } else {
                str2 = (String) getTag();
            }
            new SpannableStringBuilder().append(str2);
            if (str == null) {
                if (!this.isRecent && (str3 = Emoji.emojiColor.get(str2)) != null) {
                    str2 = EmojiView.addColorToCode(str2, str3);
                }
                EmojiView.this.addEmojiToRecent(str2);
                if (EmojiView.this.delegate != null) {
                    EmojiView.this.delegate.onEmojiSelected(Emoji.fixEmoji(str2));
                }
            } else if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.onEmojiSelected(Emoji.fixEmoji(str));
            }
        }

        public void setImageDrawable(Drawable drawable, boolean z) {
            super.setImageDrawable(drawable);
            this.isRecent = z;
        }

        public void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i));
        }
    }

    private class EmojiPopupWindow extends PopupWindow {
        private ViewTreeObserver.OnScrollChangedListener mSuperScrollListener;
        private ViewTreeObserver mViewTreeObserver;

        public EmojiPopupWindow() {
            init();
        }

        public EmojiPopupWindow(Context context) {
            super(context);
            init();
        }

        public EmojiPopupWindow(int i, int i2) {
            super(i, i2);
            init();
        }

        public EmojiPopupWindow(View view) {
            super(view);
            init();
        }

        public EmojiPopupWindow(View view, int i, int i2, boolean z) {
            super(view, i, i2, z);
            init();
        }

        public EmojiPopupWindow(View view, int i, int i2) {
            super(view, i, i2);
            init();
        }

        private void init() {
            if (EmojiView.superListenerField != null) {
                try {
                    this.mSuperScrollListener = (ViewTreeObserver.OnScrollChangedListener) EmojiView.superListenerField.get(this);
                    EmojiView.superListenerField.set(this, EmojiView.NOP);
                } catch (Exception unused) {
                    this.mSuperScrollListener = null;
                }
            }
        }

        private void unregisterListener() {
            ViewTreeObserver viewTreeObserver;
            if (this.mSuperScrollListener != null && (viewTreeObserver = this.mViewTreeObserver) != null) {
                if (viewTreeObserver.isAlive()) {
                    this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                }
                this.mViewTreeObserver = null;
            }
        }

        private void registerListener(View view) {
            if (this.mSuperScrollListener != null) {
                ViewTreeObserver viewTreeObserver = view.getWindowToken() != null ? view.getViewTreeObserver() : null;
                ViewTreeObserver viewTreeObserver2 = this.mViewTreeObserver;
                if (viewTreeObserver != viewTreeObserver2) {
                    if (viewTreeObserver2 != null && viewTreeObserver2.isAlive()) {
                        this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                    }
                    this.mViewTreeObserver = viewTreeObserver;
                    if (viewTreeObserver != null) {
                        viewTreeObserver.addOnScrollChangedListener(this.mSuperScrollListener);
                    }
                }
            }
        }

        public void showAsDropDown(View view, int i, int i2) {
            try {
                super.showAsDropDown(view, i, i2);
                registerListener(view);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void update(View view, int i, int i2, int i3, int i4) {
            super.update(view, i, i2, i3, i4);
            registerListener(view);
        }

        public void update(View view, int i, int i2) {
            super.update(view, i, i2);
            registerListener(view);
        }

        public void showAtLocation(View view, int i, int i2, int i3) {
            super.showAtLocation(view, i, i2, i3);
            unregisterListener();
        }

        public void dismiss() {
            setFocusable(false);
            try {
                super.dismiss();
            } catch (Exception unused) {
            }
            unregisterListener();
        }
    }

    private class EmojiColorPickerView extends View {
        /* access modifiers changed from: private */
        public Drawable arrowDrawable = getResources().getDrawable(NUM);
        private int arrowX;
        /* access modifiers changed from: private */
        public Drawable backgroundDrawable = getResources().getDrawable(NUM);
        private String currentEmoji;
        private RectF rect = new RectF();
        private Paint rectPaint = new Paint(1);
        private int selection;

        public void setEmoji(String str, int i) {
            this.currentEmoji = str;
            this.arrowX = i;
            this.rectPaint.setColor(NUM);
            invalidate();
        }

        public String getEmoji() {
            return this.currentEmoji;
        }

        public void setSelection(int i) {
            if (this.selection != i) {
                this.selection = i;
                invalidate();
            }
        }

        public int getSelection() {
            return this.selection;
        }

        public EmojiColorPickerView(Context context) {
            super(context);
            Theme.setDrawableColor(this.backgroundDrawable, Theme.getColor("dialogBackground"));
            Theme.setDrawableColor(this.arrowDrawable, Theme.getColor("dialogBackground"));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int i = 0;
            this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(AndroidUtilities.isTablet() ? 60.0f : 52.0f));
            this.backgroundDrawable.draw(canvas);
            Drawable drawable = this.arrowDrawable;
            int dp = this.arrowX - AndroidUtilities.dp(9.0f);
            float f = 55.5f;
            int dp2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 55.5f : 47.5f);
            int dp3 = this.arrowX + AndroidUtilities.dp(9.0f);
            if (!AndroidUtilities.isTablet()) {
                f = 47.5f;
            }
            drawable.setBounds(dp, dp2, dp3, AndroidUtilities.dp(f + 8.0f));
            this.arrowDrawable.draw(canvas);
            if (this.currentEmoji != null) {
                while (i < 6) {
                    int access$2000 = (EmojiView.this.emojiSize * i) + AndroidUtilities.dp((float) ((i * 4) + 5));
                    int dp4 = AndroidUtilities.dp(9.0f);
                    if (this.selection == i) {
                        this.rect.set((float) access$2000, (float) (dp4 - ((int) AndroidUtilities.dpf2(3.5f))), (float) (EmojiView.this.emojiSize + access$2000), (float) (EmojiView.this.emojiSize + dp4 + AndroidUtilities.dp(3.0f)));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.rectPaint);
                    }
                    String str = this.currentEmoji;
                    if (i != 0) {
                        str = EmojiView.addColorToCode(str, i != 1 ? i != 2 ? i != 3 ? i != 4 ? i != 5 ? "" : "🏿" : "🏾" : "🏽" : "🏼" : "🏻");
                    }
                    Drawable emojiBigDrawable = Emoji.getEmojiBigDrawable(str);
                    if (emojiBigDrawable != null) {
                        emojiBigDrawable.setBounds(access$2000, dp4, EmojiView.this.emojiSize + access$2000, EmojiView.this.emojiSize + dp4);
                        emojiBigDrawable.draw(canvas);
                    }
                    i++;
                }
            }
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public EmojiView(boolean r28, boolean r29, android.content.Context r30, boolean r31, org.telegram.tgnet.TLRPC.ChatFull r32) {
        /*
            r27 = this;
            r0 = r27
            r1 = r29
            r2 = r30
            r3 = r31
            r0.<init>(r2)
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r0.views = r4
            r4 = 1
            r0.firstEmojiAttach = r4
            r5 = -1
            r0.hasRecentEmoji = r5
            r0.firstGifAttach = r4
            r0.firstStickersAttach = r4
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            r0.currentAccount = r6
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r0.stickerSets = r6
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r0.recentGifs = r6
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r0.recentStickers = r6
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r0.favouriteStickers = r6
            android.util.LongSparseArray r6 = new android.util.LongSparseArray
            r6.<init>()
            r0.installingStickerSets = r6
            android.util.LongSparseArray r6 = new android.util.LongSparseArray
            r6.<init>()
            r0.removingStickerSets = r6
            r6 = 2
            int[] r7 = new int[r6]
            r0.location = r7
            r7 = -2
            r0.recentTabBum = r7
            r0.favTabBum = r7
            r0.trendingTabNum = r7
            r0.currentBackgroundType = r5
            org.telegram.ui.Components.EmojiView$1 r7 = new org.telegram.ui.Components.EmojiView$1
            r7.<init>()
            r0.contentPreviewViewerDelegate = r7
            java.lang.String r7 = "chat_emojiBottomPanelIcon"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            int r9 = android.graphics.Color.red(r8)
            int r10 = android.graphics.Color.green(r8)
            int r8 = android.graphics.Color.blue(r8)
            r11 = 30
            int r8 = android.graphics.Color.argb(r11, r9, r10, r8)
            r9 = 1115684864(0x42800000, float:64.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r0.searchFieldHeight = r9
            r0.needEmojiSearch = r3
            r9 = 3
            android.graphics.drawable.Drawable[] r10 = new android.graphics.drawable.Drawable[r9]
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            java.lang.String r12 = "chat_emojiPanelIconSelected"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r14 = 2131165883(0x7var_bb, float:1.7945996E38)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r14, r11, r13)
            r13 = 0
            r10[r13] = r11
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r15 = 2131165880(0x7var_b8, float:1.794599E38)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r11, r14)
            r10[r4] = r11
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r15 = 2131165884(0x7var_bc, float:1.7945998E38)
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r11, r14)
            r10[r6] = r11
            r0.tabIcons = r10
            r10 = 9
            android.graphics.drawable.Drawable[] r10 = new android.graphics.drawable.Drawable[r10]
            java.lang.String r11 = "chat_emojiPanelIcon"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r5 = 2131165874(0x7var_b2, float:1.7945977E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r5, r14, r15)
            r10[r13] = r5
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r15 = 2131165875(0x7var_b3, float:1.794598E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r5, r14)
            r10[r4] = r5
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r15 = 2131165867(0x7var_ab, float:1.7945963E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r5, r14)
            r10[r6] = r5
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r15 = 2131165870(0x7var_ae, float:1.794597E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r5, r14)
            r10[r9] = r5
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r15 = 2131165866(0x7var_aa, float:1.7945961E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r5, r14)
            r14 = 4
            r10[r14] = r5
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r15 = 2131165876(0x7var_b4, float:1.7945981E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r5, r14)
            r14 = 5
            r10[r14] = r5
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r14 = 2131165871(0x7var_af, float:1.7945971E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r14, r5, r15)
            r14 = 6
            r10[r14] = r5
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r15 = 2131165872(0x7var_b0, float:1.7945973E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r5, r14)
            r14 = 7
            r10[r14] = r5
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r14 = 2131165869(0x7var_ad, float:1.7945967E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r14, r5, r11)
            r11 = 8
            r10[r11] = r5
            r0.emojiIcons = r10
            android.graphics.drawable.Drawable[] r5 = new android.graphics.drawable.Drawable[r9]
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r15 = 2131165874(0x7var_b2, float:1.7945977E38)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r10, r14)
            r5[r13] = r10
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r15 = 2131165868(0x7var_ac, float:1.7945965E38)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r15, r10, r14)
            r5[r4] = r10
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r12 = 2131165877(0x7var_b5, float:1.7945984E38)
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r2, r12, r7, r10)
            r5[r6] = r7
            r0.stickerIcons = r5
            java.lang.String[] r5 = new java.lang.String[r11]
            java.lang.String r7 = "Emoji1"
            r10 = 2131624989(0x7f0e041d, float:1.8877173E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)
            r5[r13] = r7
            java.lang.String r7 = "Emoji2"
            r10 = 2131624990(0x7f0e041e, float:1.8877175E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)
            r5[r4] = r7
            java.lang.String r7 = "Emoji3"
            r10 = 2131624991(0x7f0e041f, float:1.8877177E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)
            r5[r6] = r7
            java.lang.String r7 = "Emoji4"
            r10 = 2131624992(0x7f0e0420, float:1.887718E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)
            r5[r9] = r7
            java.lang.String r7 = "Emoji5"
            r10 = 2131624993(0x7f0e0421, float:1.8877181E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)
            r10 = 4
            r5[r10] = r7
            java.lang.String r7 = "Emoji6"
            r10 = 2131624994(0x7f0e0422, float:1.8877183E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)
            r10 = 5
            r5[r10] = r7
            java.lang.String r7 = "Emoji7"
            r10 = 2131624995(0x7f0e0423, float:1.8877185E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)
            r10 = 6
            r5[r10] = r7
            java.lang.String r7 = "Emoji8"
            r10 = 2131624996(0x7f0e0424, float:1.8877187E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)
            r10 = 7
            r5[r10] = r7
            r0.emojiTitles = r5
            r0.showGifs = r1
            r5 = r32
            r0.info = r5
            android.graphics.Paint r5 = new android.graphics.Paint
            r5.<init>(r4)
            r0.dotPaint = r5
            android.graphics.Paint r5 = r0.dotPaint
            java.lang.String r7 = "chat_emojiPanelNewTrending"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r5.setColor(r7)
            int r5 = android.os.Build.VERSION.SDK_INT
            r7 = 21
            if (r5 < r7) goto L_0x021f
            org.telegram.ui.Components.EmojiView$2 r5 = new org.telegram.ui.Components.EmojiView$2
            r5.<init>()
            r0.outlineProvider = r5
        L_0x021f:
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            r5.<init>(r2)
            r0.emojiContainer = r5
            java.util.ArrayList<android.view.View> r5 = r0.views
            android.widget.FrameLayout r10 = r0.emojiContainer
            r5.add(r10)
            org.telegram.ui.Components.EmojiView$3 r5 = new org.telegram.ui.Components.EmojiView$3
            r5.<init>(r2)
            r0.emojiGridView = r5
            org.telegram.ui.Components.RecyclerListView r5 = r0.emojiGridView
            r5.setInstantClick(r4)
            org.telegram.ui.Components.RecyclerListView r5 = r0.emojiGridView
            androidx.recyclerview.widget.GridLayoutManager r10 = new androidx.recyclerview.widget.GridLayoutManager
            r10.<init>(r2, r11)
            r0.emojiLayoutManager = r10
            r5.setLayoutManager(r10)
            org.telegram.ui.Components.RecyclerListView r5 = r0.emojiGridView
            r10 = 1108869120(0x42180000, float:38.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r5.setTopGlowOffset(r10)
            org.telegram.ui.Components.RecyclerListView r5 = r0.emojiGridView
            r10 = 1111490560(0x42400000, float:48.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r5.setBottomGlowOffset(r10)
            org.telegram.ui.Components.RecyclerListView r5 = r0.emojiGridView
            r10 = 1108869120(0x42180000, float:38.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r12 = 1110441984(0x42300000, float:44.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r5.setPadding(r13, r10, r13, r14)
            org.telegram.ui.Components.RecyclerListView r5 = r0.emojiGridView
            java.lang.String r10 = "chat_emojiPanelBackground"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r5.setGlowColor(r14)
            org.telegram.ui.Components.RecyclerListView r5 = r0.emojiGridView
            r5.setClipToPadding(r13)
            androidx.recyclerview.widget.GridLayoutManager r5 = r0.emojiLayoutManager
            org.telegram.ui.Components.EmojiView$4 r14 = new org.telegram.ui.Components.EmojiView$4
            r14.<init>()
            r5.setSpanSizeLookup(r14)
            org.telegram.ui.Components.RecyclerListView r5 = r0.emojiGridView
            org.telegram.ui.Components.EmojiView$EmojiGridAdapter r14 = new org.telegram.ui.Components.EmojiView$EmojiGridAdapter
            r15 = 0
            r14.<init>()
            r0.emojiAdapter = r14
            r5.setAdapter(r14)
            org.telegram.ui.Components.EmojiView$EmojiSearchAdapter r5 = new org.telegram.ui.Components.EmojiView$EmojiSearchAdapter
            r5.<init>()
            r0.emojiSearchAdapter = r5
            android.widget.FrameLayout r5 = r0.emojiContainer
            org.telegram.ui.Components.RecyclerListView r14 = r0.emojiGridView
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            r7 = -1
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r9)
            r5.addView(r14, r9)
            org.telegram.ui.Components.RecyclerListView r5 = r0.emojiGridView
            org.telegram.ui.Components.EmojiView$5 r7 = new org.telegram.ui.Components.EmojiView$5
            r7.<init>()
            r5.setOnScrollListener(r7)
            org.telegram.ui.Components.RecyclerListView r5 = r0.emojiGridView
            org.telegram.ui.Components.EmojiView$6 r7 = new org.telegram.ui.Components.EmojiView$6
            r7.<init>()
            r5.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r7)
            org.telegram.ui.Components.RecyclerListView r5 = r0.emojiGridView
            org.telegram.ui.Components.EmojiView$7 r7 = new org.telegram.ui.Components.EmojiView$7
            r7.<init>()
            r5.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r7)
            org.telegram.ui.Components.ScrollSlidingTabStrip r5 = new org.telegram.ui.Components.ScrollSlidingTabStrip
            r5.<init>(r2)
            r0.emojiTabs = r5
            if (r3 == 0) goto L_0x02f9
            org.telegram.ui.Components.EmojiView$SearchField r5 = new org.telegram.ui.Components.EmojiView$SearchField
            r5.<init>(r2, r4)
            r0.emojiSearchField = r5
            android.widget.FrameLayout r5 = r0.emojiContainer
            org.telegram.ui.Components.EmojiView$SearchField r7 = r0.emojiSearchField
            android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
            int r14 = r0.searchFieldHeight
            int r19 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r14 = r14 + r19
            r11 = -1
            r9.<init>(r11, r14)
            r5.addView(r7, r9)
            org.telegram.ui.Components.EmojiView$SearchField r5 = r0.emojiSearchField
            org.telegram.ui.Components.EditTextBoldCursor r5 = r5.searchEditText
            org.telegram.ui.Components.EmojiView$8 r7 = new org.telegram.ui.Components.EmojiView$8
            r7.<init>()
            r5.setOnFocusChangeListener(r7)
        L_0x02f9:
            org.telegram.ui.Components.ScrollSlidingTabStrip r5 = r0.emojiTabs
            r5.setShouldExpand(r4)
            org.telegram.ui.Components.ScrollSlidingTabStrip r5 = r0.emojiTabs
            r7 = -1
            r5.setIndicatorHeight(r7)
            org.telegram.ui.Components.ScrollSlidingTabStrip r5 = r0.emojiTabs
            r5.setUnderlineHeight(r7)
            org.telegram.ui.Components.ScrollSlidingTabStrip r5 = r0.emojiTabs
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r5.setBackgroundColor(r9)
            android.widget.FrameLayout r5 = r0.emojiContainer
            org.telegram.ui.Components.ScrollSlidingTabStrip r9 = r0.emojiTabs
            r11 = 1108869120(0x42180000, float:38.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r11)
            r5.addView(r9, r11)
            org.telegram.ui.Components.ScrollSlidingTabStrip r5 = r0.emojiTabs
            org.telegram.ui.Components.EmojiView$9 r7 = new org.telegram.ui.Components.EmojiView$9
            r7.<init>()
            r5.setDelegate(r7)
            android.view.View r5 = new android.view.View
            r5.<init>(r2)
            r0.emojiTabsShadow = r5
            android.view.View r5 = r0.emojiTabsShadow
            r7 = 0
            r5.setAlpha(r7)
            android.view.View r5 = r0.emojiTabsShadow
            java.lang.Integer r7 = java.lang.Integer.valueOf(r4)
            r5.setTag(r7)
            android.view.View r5 = r0.emojiTabsShadow
            java.lang.String r7 = "chat_emojiPanelShadowLine"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r5.setBackgroundColor(r7)
            android.widget.FrameLayout$LayoutParams r5 = new android.widget.FrameLayout$LayoutParams
            int r7 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r9 = 51
            r11 = -1
            r5.<init>(r11, r7, r9)
            r7 = 1108869120(0x42180000, float:38.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r5.topMargin = r7
            android.widget.FrameLayout r7 = r0.emojiContainer
            android.view.View r9 = r0.emojiTabsShadow
            r7.addView(r9, r5)
            if (r28 == 0) goto L_0x0592
            if (r1 == 0) goto L_0x0410
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r2)
            r0.gifContainer = r1
            java.util.ArrayList<android.view.View> r1 = r0.views
            android.widget.FrameLayout r5 = r0.gifContainer
            r1.add(r5)
            org.telegram.ui.Components.EmojiView$10 r1 = new org.telegram.ui.Components.EmojiView$10
            r1.<init>(r2)
            r0.gifGridView = r1
            org.telegram.ui.Components.RecyclerListView r1 = r0.gifGridView
            r1.setClipToPadding(r13)
            org.telegram.ui.Components.RecyclerListView r1 = r0.gifGridView
            org.telegram.ui.Components.EmojiView$11 r5 = new org.telegram.ui.Components.EmojiView$11
            r7 = 100
            r5.<init>(r2, r7)
            r0.gifLayoutManager = r5
            r1.setLayoutManager(r5)
            org.telegram.ui.Components.ExtendedGridLayoutManager r1 = r0.gifLayoutManager
            org.telegram.ui.Components.EmojiView$12 r5 = new org.telegram.ui.Components.EmojiView$12
            r5.<init>()
            r1.setSpanSizeLookup(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r0.gifGridView
            org.telegram.ui.Components.EmojiView$13 r5 = new org.telegram.ui.Components.EmojiView$13
            r5.<init>()
            r1.addItemDecoration(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r0.gifGridView
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.setPadding(r13, r13, r13, r5)
            org.telegram.ui.Components.RecyclerListView r1 = r0.gifGridView
            r1.setOverScrollMode(r6)
            org.telegram.ui.Components.RecyclerListView r1 = r0.gifGridView
            org.telegram.ui.Components.EmojiView$GifAdapter r5 = new org.telegram.ui.Components.EmojiView$GifAdapter
            r5.<init>(r2)
            r0.gifAdapter = r5
            r1.setAdapter(r5)
            org.telegram.ui.Components.EmojiView$GifSearchAdapter r1 = new org.telegram.ui.Components.EmojiView$GifSearchAdapter
            r1.<init>(r2)
            r0.gifSearchAdapter = r1
            org.telegram.ui.Components.RecyclerListView r1 = r0.gifGridView
            org.telegram.ui.Components.EmojiView$14 r5 = new org.telegram.ui.Components.EmojiView$14
            r5.<init>()
            r1.setOnScrollListener(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r0.gifGridView
            org.telegram.ui.Components.-$$Lambda$EmojiView$l7-k5UkFPpeHpKVTvoFojtQIyNg r5 = new org.telegram.ui.Components.-$$Lambda$EmojiView$l7-k5UkFPpeHpKVTvoFojtQIyNg
            r5.<init>()
            r1.setOnTouchListener(r5)
            org.telegram.ui.Components.-$$Lambda$EmojiView$qDPcvu2cn8NHi3uzHeRPO6CJgew r1 = new org.telegram.ui.Components.-$$Lambda$EmojiView$qDPcvu2cn8NHi3uzHeRPO6CJgew
            r1.<init>()
            r0.gifOnItemClickListener = r1
            org.telegram.ui.Components.RecyclerListView r1 = r0.gifGridView
            org.telegram.ui.Components.RecyclerListView$OnItemClickListener r5 = r0.gifOnItemClickListener
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r5)
            android.widget.FrameLayout r1 = r0.gifContainer
            org.telegram.ui.Components.RecyclerListView r5 = r0.gifGridView
            r7 = -1082130432(0xffffffffbvar_, float:-1.0)
            r9 = -1
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r7)
            r1.addView(r5, r7)
            org.telegram.ui.Components.EmojiView$SearchField r1 = new org.telegram.ui.Components.EmojiView$SearchField
            r1.<init>(r2, r6)
            r0.gifSearchField = r1
            android.widget.FrameLayout r1 = r0.gifContainer
            org.telegram.ui.Components.EmojiView$SearchField r5 = r0.gifSearchField
            android.widget.FrameLayout$LayoutParams r7 = new android.widget.FrameLayout$LayoutParams
            int r11 = r0.searchFieldHeight
            int r14 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r11 = r11 + r14
            r7.<init>(r9, r11)
            r1.addView(r5, r7)
        L_0x0410:
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            r1.<init>(r2)
            r0.stickersContainer = r1
            int r1 = r0.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            r1.checkStickers(r13)
            int r1 = r0.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            r1.checkFeaturedStickers()
            org.telegram.ui.Components.EmojiView$15 r1 = new org.telegram.ui.Components.EmojiView$15
            r1.<init>(r2)
            r0.stickersGridView = r1
            org.telegram.ui.Components.RecyclerListView r1 = r0.stickersGridView
            androidx.recyclerview.widget.GridLayoutManager r5 = new androidx.recyclerview.widget.GridLayoutManager
            r7 = 5
            r5.<init>(r2, r7)
            r0.stickersLayoutManager = r5
            r1.setLayoutManager(r5)
            androidx.recyclerview.widget.GridLayoutManager r1 = r0.stickersLayoutManager
            org.telegram.ui.Components.EmojiView$16 r5 = new org.telegram.ui.Components.EmojiView$16
            r5.<init>()
            r1.setSpanSizeLookup(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r0.stickersGridView
            r5 = 1112539136(0x42500000, float:52.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.setPadding(r13, r5, r13, r7)
            org.telegram.ui.Components.RecyclerListView r1 = r0.stickersGridView
            r1.setClipToPadding(r13)
            java.util.ArrayList<android.view.View> r1 = r0.views
            android.widget.FrameLayout r5 = r0.stickersContainer
            r1.add(r5)
            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r1 = new org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter
            r1.<init>(r2)
            r0.stickersSearchGridAdapter = r1
            org.telegram.ui.Components.RecyclerListView r1 = r0.stickersGridView
            org.telegram.ui.Components.EmojiView$StickersGridAdapter r5 = new org.telegram.ui.Components.EmojiView$StickersGridAdapter
            r5.<init>(r2)
            r0.stickersGridAdapter = r5
            r1.setAdapter(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r0.stickersGridView
            org.telegram.ui.Components.-$$Lambda$EmojiView$qCmH2CuXJBuotfYIBsa5LNpa8lQ r5 = new org.telegram.ui.Components.-$$Lambda$EmojiView$qCmH2CuXJBuotfYIBsa5LNpa8lQ
            r5.<init>()
            r1.setOnTouchListener(r5)
            org.telegram.ui.Components.-$$Lambda$EmojiView$sBpUanL5xQpXEXKoK_PK5yrP_k0 r1 = new org.telegram.ui.Components.-$$Lambda$EmojiView$sBpUanL5xQpXEXKoK_PK5yrP_k0
            r1.<init>()
            r0.stickersOnItemClickListener = r1
            org.telegram.ui.Components.RecyclerListView r1 = r0.stickersGridView
            org.telegram.ui.Components.RecyclerListView$OnItemClickListener r5 = r0.stickersOnItemClickListener
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r5)
            org.telegram.ui.Components.RecyclerListView r1 = r0.stickersGridView
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r1.setGlowColor(r5)
            android.widget.FrameLayout r1 = r0.stickersContainer
            org.telegram.ui.Components.RecyclerListView r5 = r0.stickersGridView
            r1.addView(r5)
            org.telegram.ui.Components.EmojiView$17 r1 = new org.telegram.ui.Components.EmojiView$17
            r1.<init>(r2)
            r0.stickersTab = r1
            org.telegram.ui.Components.EmojiView$SearchField r1 = new org.telegram.ui.Components.EmojiView$SearchField
            r1.<init>(r2, r13)
            r0.stickersSearchField = r1
            android.widget.FrameLayout r1 = r0.stickersContainer
            org.telegram.ui.Components.EmojiView$SearchField r5 = r0.stickersSearchField
            android.widget.FrameLayout$LayoutParams r7 = new android.widget.FrameLayout$LayoutParams
            int r9 = r0.searchFieldHeight
            int r11 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r9 = r9 + r11
            r11 = -1
            r7.<init>(r11, r9)
            r1.addView(r5, r7)
            org.telegram.ui.Components.RecyclerListView r1 = new org.telegram.ui.Components.RecyclerListView
            r1.<init>(r2)
            r0.trendingGridView = r1
            org.telegram.ui.Components.RecyclerListView r1 = r0.trendingGridView
            r1.setItemAnimator(r15)
            org.telegram.ui.Components.RecyclerListView r1 = r0.trendingGridView
            r1.setLayoutAnimation(r15)
            org.telegram.ui.Components.RecyclerListView r1 = r0.trendingGridView
            org.telegram.ui.Components.EmojiView$18 r5 = new org.telegram.ui.Components.EmojiView$18
            r7 = 5
            r5.<init>(r2, r7)
            r0.trendingLayoutManager = r5
            r1.setLayoutManager(r5)
            androidx.recyclerview.widget.GridLayoutManager r1 = r0.trendingLayoutManager
            org.telegram.ui.Components.EmojiView$19 r5 = new org.telegram.ui.Components.EmojiView$19
            r5.<init>()
            r1.setSpanSizeLookup(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r0.trendingGridView
            org.telegram.ui.Components.EmojiView$20 r5 = new org.telegram.ui.Components.EmojiView$20
            r5.<init>()
            r1.setOnScrollListener(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r0.trendingGridView
            r1.setClipToPadding(r13)
            org.telegram.ui.Components.RecyclerListView r1 = r0.trendingGridView
            r5 = 1111490560(0x42400000, float:48.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r1.setPadding(r13, r5, r13, r9)
            org.telegram.ui.Components.RecyclerListView r1 = r0.trendingGridView
            org.telegram.ui.Components.EmojiView$TrendingGridAdapter r5 = new org.telegram.ui.Components.EmojiView$TrendingGridAdapter
            r5.<init>(r2)
            r0.trendingGridAdapter = r5
            r1.setAdapter(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r0.trendingGridView
            org.telegram.ui.Components.-$$Lambda$EmojiView$dYtQT1qlPrE3oYWHySyt2vGHEgc r5 = new org.telegram.ui.Components.-$$Lambda$EmojiView$dYtQT1qlPrE3oYWHySyt2vGHEgc
            r5.<init>()
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r5)
            org.telegram.ui.Components.EmojiView$TrendingGridAdapter r1 = r0.trendingGridAdapter
            r1.notifyDataSetChanged()
            org.telegram.ui.Components.RecyclerListView r1 = r0.trendingGridView
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r1.setGlowColor(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r0.trendingGridView
            r5 = 8
            r1.setVisibility(r5)
            android.widget.FrameLayout r1 = r0.stickersContainer
            org.telegram.ui.Components.RecyclerListView r5 = r0.trendingGridView
            r1.addView(r5)
            org.telegram.ui.Components.ScrollSlidingTabStrip r1 = r0.stickersTab
            int r5 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r1.setUnderlineHeight(r5)
            org.telegram.ui.Components.ScrollSlidingTabStrip r1 = r0.stickersTab
            r5 = 1073741824(0x40000000, float:2.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.setIndicatorHeight(r5)
            org.telegram.ui.Components.ScrollSlidingTabStrip r1 = r0.stickersTab
            java.lang.String r5 = "chat_emojiPanelStickerPackSelectorLine"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r1.setIndicatorColor(r5)
            org.telegram.ui.Components.ScrollSlidingTabStrip r1 = r0.stickersTab
            java.lang.String r5 = "chat_emojiPanelShadowLine"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r1.setUnderlineColor(r5)
            org.telegram.ui.Components.ScrollSlidingTabStrip r1 = r0.stickersTab
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r1.setBackgroundColor(r5)
            android.widget.FrameLayout r1 = r0.stickersContainer
            org.telegram.ui.Components.ScrollSlidingTabStrip r5 = r0.stickersTab
            r9 = 48
            r11 = 51
            r14 = -1
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r14, (int) r9, (int) r11)
            r1.addView(r5, r9)
            r27.updateStickerTabs()
            org.telegram.ui.Components.ScrollSlidingTabStrip r1 = r0.stickersTab
            org.telegram.ui.Components.-$$Lambda$EmojiView$EeKK6r6yP0jttMlGjDS2ja6SA2o r5 = new org.telegram.ui.Components.-$$Lambda$EmojiView$EeKK6r6yP0jttMlGjDS2ja6SA2o
            r5.<init>()
            r1.setDelegate(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r0.stickersGridView
            org.telegram.ui.Components.EmojiView$21 r5 = new org.telegram.ui.Components.EmojiView$21
            r5.<init>()
            r1.setOnScrollListener(r5)
            goto L_0x0593
        L_0x0592:
            r7 = 5
        L_0x0593:
            org.telegram.ui.Components.EmojiView$22 r1 = new org.telegram.ui.Components.EmojiView$22
            r1.<init>(r2)
            r0.pager = r1
            androidx.viewpager.widget.ViewPager r1 = r0.pager
            org.telegram.ui.Components.EmojiView$EmojiPagesAdapter r5 = new org.telegram.ui.Components.EmojiView$EmojiPagesAdapter
            r5.<init>()
            r1.setAdapter(r5)
            android.view.View r1 = new android.view.View
            r1.<init>(r2)
            r0.topShadow = r1
            android.view.View r1 = r0.topShadow
            r5 = 2131165408(0x7var_e0, float:1.7945032E38)
            r9 = -1907225(0xffffffffffe2e5e7, float:NaN)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r5, (int) r9)
            r1.setBackgroundDrawable(r5)
            android.view.View r1 = r0.topShadow
            r5 = 1086324736(0x40CLASSNAME, float:6.0)
            r9 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r5)
            r0.addView(r1, r5)
            org.telegram.ui.Components.EmojiView$23 r1 = new org.telegram.ui.Components.EmojiView$23
            r1.<init>(r2)
            r0.backspaceButton = r1
            android.widget.ImageView r1 = r0.backspaceButton
            r5 = 2131165879(0x7var_b7, float:1.7945988E38)
            r1.setImageResource(r5)
            android.widget.ImageView r1 = r0.backspaceButton
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            java.lang.String r9 = "chat_emojiPanelBackspace"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r9, r11)
            r1.setColorFilter(r5)
            android.widget.ImageView r1 = r0.backspaceButton
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER
            r1.setScaleType(r5)
            android.widget.ImageView r1 = r0.backspaceButton
            r5 = 2131623951(0x7f0e000f, float:1.8875068E38)
            java.lang.String r9 = "AccDescrBackspace"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r1.setContentDescription(r5)
            android.widget.ImageView r1 = r0.backspaceButton
            r1.setFocusable(r4)
            android.widget.ImageView r1 = r0.backspaceButton
            org.telegram.ui.Components.EmojiView$24 r5 = new org.telegram.ui.Components.EmojiView$24
            r5.<init>()
            r1.setOnClickListener(r5)
            org.telegram.ui.Components.EmojiView$25 r1 = new org.telegram.ui.Components.EmojiView$25
            r1.<init>(r2)
            r0.bottomTabContainer = r1
            android.view.View r1 = new android.view.View
            r1.<init>(r2)
            r0.shadowLine = r1
            android.view.View r1 = r0.shadowLine
            java.lang.String r5 = "chat_emojiPanelShadowLine"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r1.setBackgroundColor(r5)
            android.widget.FrameLayout r1 = r0.bottomTabContainer
            android.view.View r5 = r0.shadowLine
            android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
            int r11 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r14 = -1
            r9.<init>(r14, r11)
            r1.addView(r5, r9)
            android.view.View r1 = new android.view.View
            r1.<init>(r2)
            r0.bottomTabContainerBackground = r1
            android.widget.FrameLayout r1 = r0.bottomTabContainer
            android.view.View r5 = r0.bottomTabContainerBackground
            android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r15 = 83
            r9.<init>(r14, r11, r15)
            r1.addView(r5, r9)
            r1 = 44
            if (r3 == 0) goto L_0x078d
            android.widget.FrameLayout r3 = r0.bottomTabContainer
            android.widget.FrameLayout$LayoutParams r5 = new android.widget.FrameLayout$LayoutParams
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r9 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r7 = r7 + r9
            r9 = 83
            r5.<init>(r14, r7, r9)
            r0.addView(r3, r5)
            android.widget.FrameLayout r3 = r0.bottomTabContainer
            android.widget.ImageView r5 = r0.backspaceButton
            r7 = 52
            r9 = 85
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r1, (int) r9)
            r3.addView(r5, r7)
            int r3 = android.os.Build.VERSION.SDK_INT
            r5 = 21
            if (r3 < r5) goto L_0x0685
            android.widget.ImageView r3 = r0.backspaceButton
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r8)
            r3.setBackground(r5)
        L_0x0685:
            android.widget.ImageView r3 = new android.widget.ImageView
            r3.<init>(r2)
            r0.stickerSettingsButton = r3
            android.widget.ImageView r3 = r0.stickerSettingsButton
            r5 = 2131165882(0x7var_ba, float:1.7945994E38)
            r3.setImageResource(r5)
            android.widget.ImageView r3 = r0.stickerSettingsButton
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            java.lang.String r7 = "chat_emojiPanelBackspace"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r7, r9)
            r3.setColorFilter(r5)
            android.widget.ImageView r3 = r0.stickerSettingsButton
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER
            r3.setScaleType(r5)
            android.widget.ImageView r3 = r0.stickerSettingsButton
            r3.setFocusable(r4)
            int r3 = android.os.Build.VERSION.SDK_INT
            r5 = 21
            if (r3 < r5) goto L_0x06c1
            android.widget.ImageView r3 = r0.stickerSettingsButton
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r8)
            r3.setBackground(r5)
        L_0x06c1:
            android.widget.ImageView r3 = r0.stickerSettingsButton
            r5 = 2131626573(0x7f0e0a4d, float:1.8880386E38)
            java.lang.String r7 = "Settings"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r3.setContentDescription(r5)
            android.widget.FrameLayout r3 = r0.bottomTabContainer
            android.widget.ImageView r5 = r0.stickerSettingsButton
            r7 = 52
            r9 = 85
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r1, (int) r9)
            r3.addView(r5, r7)
            android.widget.ImageView r3 = r0.stickerSettingsButton
            org.telegram.ui.Components.EmojiView$26 r5 = new org.telegram.ui.Components.EmojiView$26
            r5.<init>()
            r3.setOnClickListener(r5)
            org.telegram.ui.Components.PagerSlidingTabStrip r3 = new org.telegram.ui.Components.PagerSlidingTabStrip
            r3.<init>(r2)
            r0.typeTabs = r3
            org.telegram.ui.Components.PagerSlidingTabStrip r3 = r0.typeTabs
            androidx.viewpager.widget.ViewPager r5 = r0.pager
            r3.setViewPager(r5)
            org.telegram.ui.Components.PagerSlidingTabStrip r3 = r0.typeTabs
            r3.setShouldExpand(r13)
            org.telegram.ui.Components.PagerSlidingTabStrip r3 = r0.typeTabs
            r3.setIndicatorHeight(r13)
            org.telegram.ui.Components.PagerSlidingTabStrip r3 = r0.typeTabs
            r3.setUnderlineHeight(r13)
            org.telegram.ui.Components.PagerSlidingTabStrip r3 = r0.typeTabs
            r5 = 1092616192(0x41200000, float:10.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r3.setTabPaddingLeftRight(r5)
            android.widget.FrameLayout r3 = r0.bottomTabContainer
            org.telegram.ui.Components.PagerSlidingTabStrip r5 = r0.typeTabs
            r7 = -2
            r9 = 81
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r1, (int) r9)
            r3.addView(r5, r7)
            org.telegram.ui.Components.PagerSlidingTabStrip r3 = r0.typeTabs
            org.telegram.ui.Components.EmojiView$27 r5 = new org.telegram.ui.Components.EmojiView$27
            r5.<init>()
            r3.setOnPageChangeListener(r5)
            android.widget.ImageView r3 = new android.widget.ImageView
            r3.<init>(r2)
            r0.searchButton = r3
            android.widget.ImageView r3 = r0.searchButton
            r5 = 2131165881(0x7var_b9, float:1.7945992E38)
            r3.setImageResource(r5)
            android.widget.ImageView r3 = r0.searchButton
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            java.lang.String r7 = "chat_emojiPanelBackspace"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r7, r9)
            r3.setColorFilter(r5)
            android.widget.ImageView r3 = r0.searchButton
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER
            r3.setScaleType(r5)
            android.widget.ImageView r3 = r0.searchButton
            r5 = 2131626457(0x7f0e09d9, float:1.888015E38)
            java.lang.String r7 = "Search"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r3.setContentDescription(r5)
            android.widget.ImageView r3 = r0.searchButton
            r3.setFocusable(r4)
            int r3 = android.os.Build.VERSION.SDK_INT
            r5 = 21
            if (r3 < r5) goto L_0x0772
            android.widget.ImageView r3 = r0.searchButton
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r8)
            r3.setBackground(r5)
        L_0x0772:
            android.widget.FrameLayout r3 = r0.bottomTabContainer
            android.widget.ImageView r5 = r0.searchButton
            r7 = 52
            r8 = 83
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r1, (int) r8)
            r3.addView(r5, r1)
            android.widget.ImageView r1 = r0.searchButton
            org.telegram.ui.Components.EmojiView$28 r3 = new org.telegram.ui.Components.EmojiView$28
            r3.<init>()
            r1.setOnClickListener(r3)
            goto L_0x08ce
        L_0x078d:
            android.widget.FrameLayout r3 = r0.bottomTabContainer
            int r5 = android.os.Build.VERSION.SDK_INT
            r8 = 21
            if (r5 < r8) goto L_0x0798
            r5 = 40
            goto L_0x079a
        L_0x0798:
            r5 = 44
        L_0x079a:
            int r20 = r5 + 20
            int r5 = android.os.Build.VERSION.SDK_INT
            if (r5 < r8) goto L_0x07a3
            r5 = 40
            goto L_0x07a5
        L_0x07a3:
            r5 = 44
        L_0x07a5:
            int r5 = r5 + 12
            float r5 = (float) r5
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x07af
            r18 = 3
            goto L_0x07b1
        L_0x07af:
            r18 = 5
        L_0x07b1:
            r22 = r18 | 80
            r23 = 0
            r24 = 0
            r25 = 1073741824(0x40000000, float:2.0)
            r26 = 0
            r21 = r5
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r0.addView(r3, r5)
            r3 = 1113587712(0x42600000, float:56.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r3, r5, r7)
            int r5 = android.os.Build.VERSION.SDK_INT
            r7 = 21
            if (r5 >= r7) goto L_0x080d
            android.content.res.Resources r5 = r30.getResources()
            r7 = 2131165390(0x7var_ce, float:1.7944996E38)
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r7)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            r8 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r9)
            r5.setColorFilter(r7)
            org.telegram.ui.Components.CombinedDrawable r7 = new org.telegram.ui.Components.CombinedDrawable
            r7.<init>(r5, r3, r13, r13)
            r3 = 1109393408(0x42200000, float:40.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r5 = 1109393408(0x42200000, float:40.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r7.setIconSize(r3, r5)
            r3 = r7
            goto L_0x0874
        L_0x080d:
            android.animation.StateListAnimator r5 = new android.animation.StateListAnimator
            r5.<init>()
            int[] r7 = new int[r4]
            r8 = 16842919(0x10100a7, float:2.3694026E-38)
            r7[r13] = r8
            android.widget.ImageView r8 = r0.floatingButton
            android.util.Property r9 = android.view.View.TRANSLATION_Z
            float[] r10 = new float[r6]
            r11 = 1073741824(0x40000000, float:2.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r10[r13] = r11
            r11 = 1082130432(0x40800000, float:4.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r10[r4] = r11
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r9, r10)
            r9 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r8 = r8.setDuration(r9)
            r5.addState(r7, r8)
            int[] r7 = new int[r13]
            android.widget.ImageView r8 = r0.floatingButton
            android.util.Property r9 = android.view.View.TRANSLATION_Z
            float[] r10 = new float[r6]
            r11 = 1082130432(0x40800000, float:4.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r10[r13] = r11
            r11 = 1073741824(0x40000000, float:2.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r10[r4] = r11
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r9, r10)
            r9 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r8 = r8.setDuration(r9)
            r5.addState(r7, r8)
            android.widget.ImageView r7 = r0.backspaceButton
            r7.setStateListAnimator(r5)
            android.widget.ImageView r5 = r0.backspaceButton
            org.telegram.ui.Components.EmojiView$29 r7 = new org.telegram.ui.Components.EmojiView$29
            r7.<init>()
            r5.setOutlineProvider(r7)
        L_0x0874:
            android.widget.ImageView r5 = r0.backspaceButton
            r7 = 1073741824(0x40000000, float:2.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r5.setPadding(r13, r13, r7, r13)
            android.widget.ImageView r5 = r0.backspaceButton
            r5.setBackground(r3)
            android.widget.ImageView r3 = r0.backspaceButton
            r5 = 2131623951(0x7f0e000f, float:1.8875068E38)
            java.lang.String r7 = "AccDescrBackspace"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r3.setContentDescription(r5)
            android.widget.ImageView r3 = r0.backspaceButton
            r3.setFocusable(r4)
            android.widget.FrameLayout r3 = r0.bottomTabContainer
            android.widget.ImageView r5 = r0.backspaceButton
            int r7 = android.os.Build.VERSION.SDK_INT
            r8 = 21
            if (r7 < r8) goto L_0x08a6
            r7 = 40
            r20 = 40
            goto L_0x08a8
        L_0x08a6:
            r20 = 44
        L_0x08a8:
            int r7 = android.os.Build.VERSION.SDK_INT
            if (r7 < r8) goto L_0x08ae
            r1 = 40
        L_0x08ae:
            float r1 = (float) r1
            r22 = 51
            r23 = 1092616192(0x41200000, float:10.0)
            r24 = 0
            r25 = 1092616192(0x41200000, float:10.0)
            r26 = 0
            r21 = r1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r3.addView(r5, r1)
            android.view.View r1 = r0.shadowLine
            r3 = 8
            r1.setVisibility(r3)
            android.view.View r1 = r0.bottomTabContainerBackground
            r1.setVisibility(r3)
        L_0x08ce:
            androidx.viewpager.widget.ViewPager r1 = r0.pager
            r3 = 51
            r5 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r5, (int) r3)
            r0.addView(r1, r13, r3)
            org.telegram.ui.Components.CorrectlyMeasuringTextView r1 = new org.telegram.ui.Components.CorrectlyMeasuringTextView
            r1.<init>(r2)
            r0.mediaBanTooltip = r1
            android.widget.TextView r1 = r0.mediaBanTooltip
            r3 = 1077936128(0x40400000, float:3.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            java.lang.String r5 = "chat_gifSaveHintBackground"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r3, r5)
            r1.setBackgroundDrawable(r3)
            android.widget.TextView r1 = r0.mediaBanTooltip
            java.lang.String r3 = "chat_gifSaveHintText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r1.setTextColor(r3)
            android.widget.TextView r1 = r0.mediaBanTooltip
            r3 = 1090519040(0x41000000, float:8.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r5 = 1088421888(0x40e00000, float:7.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r7 = 1090519040(0x41000000, float:8.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r8 = 1088421888(0x40e00000, float:7.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.setPadding(r3, r5, r7, r8)
            android.widget.TextView r1 = r0.mediaBanTooltip
            r3 = 16
            r1.setGravity(r3)
            android.widget.TextView r1 = r0.mediaBanTooltip
            r3 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r4, r3)
            android.widget.TextView r1 = r0.mediaBanTooltip
            r3 = 4
            r1.setVisibility(r3)
            android.widget.TextView r1 = r0.mediaBanTooltip
            r14 = -2
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r16 = 81
            r17 = 1084227584(0x40a00000, float:5.0)
            r18 = 0
            r19 = 1084227584(0x40a00000, float:5.0)
            r20 = 1112801280(0x42540000, float:53.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r0.addView(r1, r3)
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r1 == 0) goto L_0x0951
            r1 = 1109393408(0x42200000, float:40.0)
            goto L_0x0953
        L_0x0951:
            r1 = 1107296256(0x42000000, float:32.0)
        L_0x0953:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.emojiSize = r1
            org.telegram.ui.Components.EmojiView$EmojiColorPickerView r1 = new org.telegram.ui.Components.EmojiView$EmojiColorPickerView
            r1.<init>(r2)
            r0.pickerView = r1
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = new org.telegram.ui.Components.EmojiView$EmojiPopupWindow
            org.telegram.ui.Components.EmojiView$EmojiColorPickerView r2 = r0.pickerView
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r3 == 0) goto L_0x096d
            r3 = 40
            goto L_0x096f
        L_0x096d:
            r3 = 32
        L_0x096f:
            int r3 = r3 * 6
            int r3 = r3 + 10
            int r3 = r3 + 20
            float r3 = (float) r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r0.popupWidth = r3
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 == 0) goto L_0x0985
            r5 = 1115684864(0x42800000, float:64.0)
            goto L_0x0987
        L_0x0985:
            r5 = 1113587712(0x42600000, float:56.0)
        L_0x0987:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r0.popupHeight = r5
            r1.<init>(r2, r3, r5)
            r0.pickerViewPopup = r1
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = r0.pickerViewPopup
            r1.setOutsideTouchable(r4)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = r0.pickerViewPopup
            r1.setClippingEnabled(r4)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = r0.pickerViewPopup
            r1.setInputMethodMode(r6)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = r0.pickerViewPopup
            r1.setSoftInputMode(r13)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = r0.pickerViewPopup
            android.view.View r1 = r1.getContentView()
            r1.setFocusableInTouchMode(r4)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = r0.pickerViewPopup
            android.view.View r1 = r1.getContentView()
            org.telegram.ui.Components.-$$Lambda$EmojiView$YBM1z0ObV6f6L9y0M-Yz0CakSrc r2 = new org.telegram.ui.Components.-$$Lambda$EmojiView$YBM1z0ObV6f6L9y0M-Yz0CakSrc
            r2.<init>()
            r1.setOnKeyListener(r2)
            android.content.SharedPreferences r1 = org.telegram.messenger.MessagesController.getGlobalEmojiSettings()
            java.lang.String r2 = "selected_page"
            int r1 = r1.getInt(r2, r13)
            r0.currentPage = r1
            org.telegram.messenger.Emoji.loadRecentEmoji()
            org.telegram.ui.Components.EmojiView$EmojiGridAdapter r1 = r0.emojiAdapter
            r1.notifyDataSetChanged()
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r0.typeTabs
            if (r1 == 0) goto L_0x0a01
            java.util.ArrayList<android.view.View> r1 = r0.views
            int r1 = r1.size()
            if (r1 != r4) goto L_0x09ec
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r0.typeTabs
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x09ec
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r0.typeTabs
            r2 = 4
            r1.setVisibility(r2)
            goto L_0x0a01
        L_0x09ec:
            java.util.ArrayList<android.view.View> r1 = r0.views
            int r1 = r1.size()
            if (r1 == r4) goto L_0x0a01
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r0.typeTabs
            int r1 = r1.getVisibility()
            if (r1 == 0) goto L_0x0a01
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r0.typeTabs
            r1.setVisibility(r13)
        L_0x0a01:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.<init>(boolean, boolean, android.content.Context, boolean, org.telegram.tgnet.TLRPC$ChatFull):void");
    }

    public /* synthetic */ boolean lambda$new$1$EmojiView(View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.gifGridView, 0, this.gifOnItemClickListener, this.contentPreviewViewerDelegate);
    }

    public /* synthetic */ void lambda$new$2$EmojiView(View view, int i) {
        if (this.delegate != null) {
            int i2 = i - 1;
            if (this.gifGridView.getAdapter() != this.gifAdapter) {
                RecyclerView.Adapter adapter = this.gifGridView.getAdapter();
                GifSearchAdapter gifSearchAdapter2 = this.gifSearchAdapter;
                if (adapter == gifSearchAdapter2 && i2 >= 0 && i2 < gifSearchAdapter2.results.size()) {
                    this.delegate.onGifSelected(view, this.gifSearchAdapter.results.get(i2), this.gifSearchAdapter.bot, true, 0);
                    this.recentGifs = MediaDataController.getInstance(this.currentAccount).getRecentGifs();
                    GifAdapter gifAdapter2 = this.gifAdapter;
                    if (gifAdapter2 != null) {
                        gifAdapter2.notifyDataSetChanged();
                    }
                }
            } else if (i2 >= 0 && i2 < this.recentGifs.size()) {
                this.delegate.onGifSelected(view, this.recentGifs.get(i2), "gif", true, 0);
            }
        }
    }

    public /* synthetic */ boolean lambda$new$3$EmojiView(View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.stickersGridView, getMeasuredHeight(), this.stickersOnItemClickListener, this.contentPreviewViewerDelegate);
    }

    public /* synthetic */ void lambda$new$4$EmojiView(View view, int i) {
        TLRPC.StickerSetCovered stickerSetCovered;
        RecyclerView.Adapter adapter = this.stickersGridView.getAdapter();
        StickersSearchGridAdapter stickersSearchGridAdapter2 = this.stickersSearchGridAdapter;
        if (adapter == stickersSearchGridAdapter2 && (stickerSetCovered = (TLRPC.StickerSetCovered) stickersSearchGridAdapter2.positionsToSets.get(i)) != null) {
            this.delegate.onShowStickerSet(stickerSetCovered.set, (TLRPC.InputStickerSet) null);
        } else if (view instanceof StickerEmojiCell) {
            ContentPreviewViewer.getInstance().reset();
            StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
            if (!stickerEmojiCell.isDisabled()) {
                stickerEmojiCell.disable();
                this.delegate.onStickerSelected(stickerEmojiCell, stickerEmojiCell.getSticker(), stickerEmojiCell.getParentObject(), true, 0);
            }
        }
    }

    public /* synthetic */ void lambda$new$5$EmojiView(View view, int i) {
        TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) this.trendingGridAdapter.positionsToSets.get(i);
        if (stickerSetCovered != null) {
            this.delegate.onShowStickerSet(stickerSetCovered.set, (TLRPC.InputStickerSet) null);
        }
    }

    public /* synthetic */ void lambda$new$6$EmojiView(int i) {
        if (i == this.trendingTabNum) {
            if (this.trendingGridView.getVisibility() != 0) {
                showTrendingTab(true);
            }
        } else if (this.trendingGridView.getVisibility() == 0) {
            showTrendingTab(false);
            saveNewPage();
        }
        if (i != this.trendingTabNum) {
            if (i == this.recentTabBum) {
                this.stickersGridView.stopScroll();
                this.stickersLayoutManager.scrollToPositionWithOffset(this.stickersGridAdapter.getPositionForPack("recent"), 0);
                checkStickersTabY((View) null, 0);
                ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
                int i2 = this.recentTabBum;
                scrollSlidingTabStrip.onPageScrolled(i2, i2 > 0 ? i2 : this.stickersTabOffset);
            } else if (i == this.favTabBum) {
                this.stickersGridView.stopScroll();
                this.stickersLayoutManager.scrollToPositionWithOffset(this.stickersGridAdapter.getPositionForPack("fav"), 0);
                checkStickersTabY((View) null, 0);
                ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
                int i3 = this.favTabBum;
                scrollSlidingTabStrip2.onPageScrolled(i3, i3 > 0 ? i3 : this.stickersTabOffset);
            } else {
                int i4 = i - this.stickersTabOffset;
                if (i4 < this.stickerSets.size()) {
                    if (i4 >= this.stickerSets.size()) {
                        i4 = this.stickerSets.size() - 1;
                    }
                    this.firstStickersAttach = false;
                    this.stickersGridView.stopScroll();
                    this.stickersLayoutManager.scrollToPositionWithOffset(this.stickersGridAdapter.getPositionForPack(this.stickerSets.get(i4)), 0);
                    checkStickersTabY((View) null, 0);
                    checkScroll();
                }
            }
        }
    }

    public /* synthetic */ boolean lambda$new$7$EmojiView(View view, int i, KeyEvent keyEvent) {
        EmojiPopupWindow emojiPopupWindow;
        if (i != 82 || keyEvent.getRepeatCount() != 0 || keyEvent.getAction() != 1 || (emojiPopupWindow = this.pickerViewPopup) == null || !emojiPopupWindow.isShowing()) {
            return false;
        }
        this.pickerViewPopup.dismiss();
        return true;
    }

    /* access modifiers changed from: private */
    public static String addColorToCode(String str, String str2) {
        String str3;
        int length = str.length();
        if (length > 2 && str.charAt(str.length() - 2) == 8205) {
            str3 = str.substring(str.length() - 2);
            str = str.substring(0, str.length() - 2);
        } else if (length <= 3 || str.charAt(str.length() - 3) != 8205) {
            str3 = null;
        } else {
            str3 = str.substring(str.length() - 3);
            str = str.substring(0, str.length() - 3);
        }
        String str4 = str + str2;
        if (str3 == null) {
            return str4;
        }
        return str4 + str3;
    }

    public void setTranslationY(float f) {
        View view;
        super.setTranslationY(f);
        if (this.bottomTabContainer.getTag() == null) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if ((emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) && (view = (View) getParent()) != null) {
                this.bottomTabContainer.setTranslationY(-((getY() + ((float) getMeasuredHeight())) - ((float) view.getHeight())));
            }
        }
    }

    /* access modifiers changed from: private */
    public void startStopVisibleGifs(boolean z) {
        RecyclerListView recyclerListView = this.gifGridView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.gifGridView.getChildAt(i);
                if (childAt instanceof ContextLinkCell) {
                    ImageReceiver photoImage = ((ContextLinkCell) childAt).getPhotoImage();
                    if (z) {
                        photoImage.setAllowStartAnimation(true);
                        photoImage.startAnimation();
                    } else {
                        photoImage.setAllowStartAnimation(false);
                        photoImage.stopAnimation();
                    }
                }
            }
        }
    }

    public void addEmojiToRecent(String str) {
        if (Emoji.isValidEmoji(str)) {
            Emoji.recentEmoji.size();
            Emoji.addRecentEmoji(str);
            if (!(getVisibility() == 0 && this.pager.getCurrentItem() == 0)) {
                Emoji.sortEmoji();
                this.emojiAdapter.notifyDataSetChanged();
            }
            Emoji.saveRecentEmoji();
        }
    }

    public void showSearchField(boolean z) {
        ScrollSlidingTabStrip scrollSlidingTabStrip;
        LinearLayoutManager linearLayoutManager;
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                linearLayoutManager = this.emojiLayoutManager;
                scrollSlidingTabStrip = this.emojiTabs;
            } else if (i == 1) {
                linearLayoutManager = this.gifLayoutManager;
                scrollSlidingTabStrip = null;
            } else {
                linearLayoutManager = this.stickersLayoutManager;
                scrollSlidingTabStrip = this.stickersTab;
            }
            if (linearLayoutManager != null) {
                int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (z) {
                    if (findFirstVisibleItemPosition == 1 || findFirstVisibleItemPosition == 2) {
                        linearLayoutManager.scrollToPosition(0);
                        if (scrollSlidingTabStrip != null) {
                            scrollSlidingTabStrip.setTranslationY(0.0f);
                        }
                    }
                } else if (findFirstVisibleItemPosition == 0) {
                    linearLayoutManager.scrollToPositionWithOffset(1, 0);
                }
            }
        }
    }

    public void hideSearchKeyboard() {
        SearchField searchField = this.stickersSearchField;
        if (searchField != null) {
            searchField.hideKeyboard();
        }
        SearchField searchField2 = this.gifSearchField;
        if (searchField2 != null) {
            searchField2.hideKeyboard();
        }
        SearchField searchField3 = this.emojiSearchField;
        if (searchField3 != null) {
            searchField3.hideKeyboard();
        }
    }

    /* access modifiers changed from: private */
    public void openSearch(SearchField searchField) {
        LinearLayoutManager linearLayoutManager;
        ScrollSlidingTabStrip scrollSlidingTabStrip;
        final RecyclerListView recyclerListView;
        SearchField searchField2;
        EmojiViewDelegate emojiViewDelegate;
        AnimatorSet animatorSet = this.searchAnimation;
        ScrollSlidingTabStrip scrollSlidingTabStrip2 = null;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        this.firstStickersAttach = false;
        this.firstGifAttach = false;
        this.firstEmojiAttach = false;
        int i = 0;
        while (i < 3) {
            if (i == 0) {
                searchField2 = this.emojiSearchField;
                recyclerListView = this.emojiGridView;
                scrollSlidingTabStrip = this.emojiTabs;
                linearLayoutManager = this.emojiLayoutManager;
            } else if (i == 1) {
                searchField2 = this.gifSearchField;
                recyclerListView = this.gifGridView;
                linearLayoutManager = this.gifLayoutManager;
                scrollSlidingTabStrip = scrollSlidingTabStrip2;
            } else {
                searchField2 = this.stickersSearchField;
                recyclerListView = this.stickersGridView;
                scrollSlidingTabStrip = this.stickersTab;
                linearLayoutManager = this.stickersLayoutManager;
            }
            if (searchField2 == null) {
                SearchField searchField3 = searchField;
            } else {
                if (searchField2 == this.gifSearchField) {
                    SearchField searchField4 = searchField;
                } else if (searchField == searchField2 && (emojiViewDelegate = this.delegate) != null && emojiViewDelegate.isExpanded()) {
                    this.searchAnimation = new AnimatorSet();
                    if (scrollSlidingTabStrip != null) {
                        this.searchAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(scrollSlidingTabStrip, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))}), ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))}), ObjectAnimator.ofFloat(searchField2, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(0.0f)})});
                    } else {
                        this.searchAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))}), ObjectAnimator.ofFloat(searchField2, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(0.0f)})});
                    }
                    this.searchAnimation.setDuration(200);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                recyclerListView.setTranslationY(0.0f);
                                if (recyclerListView == EmojiView.this.stickersGridView) {
                                    recyclerListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                                } else if (recyclerListView == EmojiView.this.emojiGridView) {
                                    recyclerListView.setPadding(0, 0, 0, 0);
                                }
                                AnimatorSet unused = EmojiView.this.searchAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                AnimatorSet unused = EmojiView.this.searchAnimation = null;
                            }
                        }
                    });
                    this.searchAnimation.start();
                }
                searchField2.setTranslationY((float) AndroidUtilities.dp(0.0f));
                if (scrollSlidingTabStrip != null) {
                    scrollSlidingTabStrip.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
                }
                if (recyclerListView == this.stickersGridView) {
                    recyclerListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                } else if (recyclerListView == this.emojiGridView) {
                    recyclerListView.setPadding(0, 0, 0, 0);
                }
                linearLayoutManager.scrollToPositionWithOffset(0, 0);
            }
            i++;
            scrollSlidingTabStrip2 = null;
        }
    }

    private void showEmojiShadow(boolean z, boolean z2) {
        if (z && this.emojiTabsShadow.getTag() == null) {
            return;
        }
        if (z || this.emojiTabsShadow.getTag() == null) {
            AnimatorSet animatorSet = this.emojiTabShadowAnimator;
            int i = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.emojiTabShadowAnimator = null;
            }
            View view = this.emojiTabsShadow;
            if (!z) {
                i = 1;
            }
            view.setTag(i);
            float f = 1.0f;
            if (z2) {
                this.emojiTabShadowAnimator = new AnimatorSet();
                AnimatorSet animatorSet2 = this.emojiTabShadowAnimator;
                Animator[] animatorArr = new Animator[1];
                View view2 = this.emojiTabsShadow;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                if (!z) {
                    f = 0.0f;
                }
                fArr[0] = f;
                animatorArr[0] = ObjectAnimator.ofFloat(view2, property, fArr);
                animatorSet2.playTogether(animatorArr);
                this.emojiTabShadowAnimator.setDuration(200);
                this.emojiTabShadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.emojiTabShadowAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet unused = EmojiView.this.emojiTabShadowAnimator = null;
                    }
                });
                this.emojiTabShadowAnimator.start();
                return;
            }
            View view3 = this.emojiTabsShadow;
            if (!z) {
                f = 0.0f;
            }
            view3.setAlpha(f);
        }
    }

    public void closeSearch(boolean z) {
        closeSearch(z, -1);
    }

    public void closeSearch(boolean z, long j) {
        ScrollSlidingTabStrip scrollSlidingTabStrip;
        final GridLayoutManager gridLayoutManager;
        final RecyclerListView recyclerListView;
        SearchField searchField;
        TLRPC.TL_messages_stickerSet stickerSetById;
        int positionForPack;
        boolean z2 = z;
        long j2 = j;
        AnimatorSet animatorSet = this.searchAnimation;
        ScrollSlidingTabStrip scrollSlidingTabStrip2 = null;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        int currentItem = this.pager.getCurrentItem();
        int i = 2;
        if (currentItem == 2 && j2 != -1 && (stickerSetById = MediaDataController.getInstance(this.currentAccount).getStickerSetById(j2)) != null && (positionForPack = this.stickersGridAdapter.getPositionForPack(stickerSetById)) >= 0) {
            this.stickersLayoutManager.scrollToPositionWithOffset(positionForPack, AndroidUtilities.dp(60.0f));
        }
        int i2 = 0;
        while (i2 < 3) {
            if (i2 == 0) {
                searchField = this.emojiSearchField;
                recyclerListView = this.emojiGridView;
                gridLayoutManager = this.emojiLayoutManager;
                scrollSlidingTabStrip = this.emojiTabs;
            } else if (i2 == 1) {
                searchField = this.gifSearchField;
                recyclerListView = this.gifGridView;
                gridLayoutManager = this.gifLayoutManager;
                scrollSlidingTabStrip = scrollSlidingTabStrip2;
            } else {
                searchField = this.stickersSearchField;
                recyclerListView = this.stickersGridView;
                gridLayoutManager = this.stickersLayoutManager;
                scrollSlidingTabStrip = this.stickersTab;
            }
            if (searchField != null) {
                searchField.searchEditText.setText("");
                if (i2 != currentItem || !z2) {
                    gridLayoutManager.scrollToPositionWithOffset(1, 0);
                    searchField.setTranslationY((float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight));
                    if (scrollSlidingTabStrip != null) {
                        scrollSlidingTabStrip.setTranslationY(0.0f);
                    }
                    if (recyclerListView == this.stickersGridView) {
                        recyclerListView.setPadding(0, AndroidUtilities.dp(52.0f), 0, AndroidUtilities.dp(44.0f));
                    } else if (recyclerListView == this.emojiGridView) {
                        recyclerListView.setPadding(0, AndroidUtilities.dp(38.0f), 0, AndroidUtilities.dp(44.0f));
                    }
                } else {
                    this.searchAnimation = new AnimatorSet();
                    if (scrollSlidingTabStrip != null) {
                        AnimatorSet animatorSet2 = this.searchAnimation;
                        Property property = View.TRANSLATION_Y;
                        float[] fArr = {(float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight)};
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(searchField, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight)});
                        i = 2;
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(scrollSlidingTabStrip, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(recyclerListView, property, fArr), ofFloat});
                    } else {
                        AnimatorSet animatorSet3 = this.searchAnimation;
                        Animator[] animatorArr = new Animator[i];
                        animatorArr[0] = ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, new float[]{(float) (-this.searchFieldHeight)});
                        animatorArr[1] = ObjectAnimator.ofFloat(searchField, View.TRANSLATION_Y, new float[]{(float) (-this.searchFieldHeight)});
                        animatorSet3.playTogether(animatorArr);
                    }
                    this.searchAnimation.setDuration(200);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                gridLayoutManager.findFirstVisibleItemPosition();
                                int findFirstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                                int top = findFirstVisibleItemPosition != -1 ? (int) (((float) gridLayoutManager.findViewByPosition(findFirstVisibleItemPosition).getTop()) + recyclerListView.getTranslationY()) : 0;
                                recyclerListView.setTranslationY(0.0f);
                                if (recyclerListView == EmojiView.this.stickersGridView) {
                                    recyclerListView.setPadding(0, AndroidUtilities.dp(52.0f), 0, AndroidUtilities.dp(44.0f));
                                } else if (recyclerListView == EmojiView.this.emojiGridView) {
                                    recyclerListView.setPadding(0, AndroidUtilities.dp(38.0f), 0, AndroidUtilities.dp(44.0f));
                                }
                                if (recyclerListView == EmojiView.this.gifGridView) {
                                    gridLayoutManager.scrollToPositionWithOffset(1, 0);
                                } else if (findFirstVisibleItemPosition != -1) {
                                    gridLayoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, top - recyclerListView.getPaddingTop());
                                }
                                AnimatorSet unused = EmojiView.this.searchAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                AnimatorSet unused = EmojiView.this.searchAnimation = null;
                            }
                        }
                    });
                    this.searchAnimation.start();
                }
            }
            i2++;
            scrollSlidingTabStrip2 = null;
        }
        if (!z2) {
            this.delegate.onSearchOpenClose(0);
        }
        showBottomTab(true, z2);
    }

    /* access modifiers changed from: private */
    public void checkStickersSearchFieldScroll(boolean z) {
        RecyclerListView recyclerListView;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        boolean z2 = false;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.stickersGridView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition == null) {
                this.stickersSearchField.showShadow(true, !z);
                return;
            }
            SearchField searchField = this.stickersSearchField;
            if (findViewHolderForAdapterPosition.itemView.getTop() < this.stickersGridView.getPaddingTop()) {
                z2 = true;
            }
            searchField.showShadow(z2, !z);
        } else if (this.stickersSearchField != null && (recyclerListView = this.stickersGridView) != null) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition2 != null) {
                this.stickersSearchField.setTranslationY((float) findViewHolderForAdapterPosition2.itemView.getTop());
            } else {
                this.stickersSearchField.setTranslationY((float) (-this.searchFieldHeight));
            }
            this.stickersSearchField.showShadow(false, !z);
        }
    }

    /* access modifiers changed from: private */
    public void checkBottomTabScroll(float f) {
        int i;
        this.lastBottomScrollDy += f;
        if (this.pager.getCurrentItem() == 0) {
            i = AndroidUtilities.dp(38.0f);
        } else {
            i = AndroidUtilities.dp(48.0f);
        }
        float f2 = this.lastBottomScrollDy;
        if (f2 >= ((float) i)) {
            showBottomTab(false, true);
        } else if (f2 <= ((float) (-i))) {
            showBottomTab(true, true);
        } else if ((this.bottomTabContainer.getTag() == null && this.lastBottomScrollDy < 0.0f) || (this.bottomTabContainer.getTag() != null && this.lastBottomScrollDy > 0.0f)) {
            this.lastBottomScrollDy = 0.0f;
        }
    }

    /* access modifiers changed from: private */
    public void showBackspaceButton(final boolean z, boolean z2) {
        if (z && this.backspaceButton.getTag() == null) {
            return;
        }
        if (z || this.backspaceButton.getTag() == null) {
            AnimatorSet animatorSet = this.backspaceButtonAnimation;
            int i = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.backspaceButtonAnimation = null;
            }
            ImageView imageView = this.backspaceButton;
            if (!z) {
                i = 1;
            }
            imageView.setTag(i);
            int i2 = 0;
            float f = 1.0f;
            if (z2) {
                if (z) {
                    this.backspaceButton.setVisibility(0);
                }
                this.backspaceButtonAnimation = new AnimatorSet();
                AnimatorSet animatorSet2 = this.backspaceButtonAnimation;
                Animator[] animatorArr = new Animator[3];
                ImageView imageView2 = this.backspaceButton;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(imageView2, property, fArr);
                ImageView imageView3 = this.backspaceButton;
                Property property2 = View.SCALE_X;
                float[] fArr2 = new float[1];
                fArr2[0] = z ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView3, property2, fArr2);
                ImageView imageView4 = this.backspaceButton;
                Property property3 = View.SCALE_Y;
                float[] fArr3 = new float[1];
                if (!z) {
                    f = 0.0f;
                }
                fArr3[0] = f;
                animatorArr[2] = ObjectAnimator.ofFloat(imageView4, property3, fArr3);
                animatorSet2.playTogether(animatorArr);
                this.backspaceButtonAnimation.setDuration(200);
                this.backspaceButtonAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.backspaceButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (!z) {
                            EmojiView.this.backspaceButton.setVisibility(4);
                        }
                    }
                });
                this.backspaceButtonAnimation.start();
                return;
            }
            this.backspaceButton.setAlpha(z ? 1.0f : 0.0f);
            this.backspaceButton.setScaleX(z ? 1.0f : 0.0f);
            ImageView imageView5 = this.backspaceButton;
            if (!z) {
                f = 0.0f;
            }
            imageView5.setScaleY(f);
            ImageView imageView6 = this.backspaceButton;
            if (!z) {
                i2 = 4;
            }
            imageView6.setVisibility(i2);
        }
    }

    /* access modifiers changed from: private */
    public void showStickerSettingsButton(final boolean z, boolean z2) {
        ImageView imageView = this.stickerSettingsButton;
        if (imageView != null) {
            if (z && imageView.getTag() == null) {
                return;
            }
            if (z || this.stickerSettingsButton.getTag() == null) {
                AnimatorSet animatorSet = this.stickersButtonAnimation;
                int i = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.stickersButtonAnimation = null;
                }
                ImageView imageView2 = this.stickerSettingsButton;
                if (!z) {
                    i = 1;
                }
                imageView2.setTag(i);
                int i2 = 0;
                float f = 1.0f;
                if (z2) {
                    if (z) {
                        this.stickerSettingsButton.setVisibility(0);
                    }
                    this.stickersButtonAnimation = new AnimatorSet();
                    AnimatorSet animatorSet2 = this.stickersButtonAnimation;
                    Animator[] animatorArr = new Animator[3];
                    ImageView imageView3 = this.stickerSettingsButton;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    fArr[0] = z ? 1.0f : 0.0f;
                    animatorArr[0] = ObjectAnimator.ofFloat(imageView3, property, fArr);
                    ImageView imageView4 = this.stickerSettingsButton;
                    Property property2 = View.SCALE_X;
                    float[] fArr2 = new float[1];
                    fArr2[0] = z ? 1.0f : 0.0f;
                    animatorArr[1] = ObjectAnimator.ofFloat(imageView4, property2, fArr2);
                    ImageView imageView5 = this.stickerSettingsButton;
                    Property property3 = View.SCALE_Y;
                    float[] fArr3 = new float[1];
                    if (!z) {
                        f = 0.0f;
                    }
                    fArr3[0] = f;
                    animatorArr[2] = ObjectAnimator.ofFloat(imageView5, property3, fArr3);
                    animatorSet2.playTogether(animatorArr);
                    this.stickersButtonAnimation.setDuration(200);
                    this.stickersButtonAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.stickersButtonAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (!z) {
                                EmojiView.this.stickerSettingsButton.setVisibility(4);
                            }
                        }
                    });
                    this.stickersButtonAnimation.start();
                    return;
                }
                this.stickerSettingsButton.setAlpha(z ? 1.0f : 0.0f);
                this.stickerSettingsButton.setScaleX(z ? 1.0f : 0.0f);
                ImageView imageView6 = this.stickerSettingsButton;
                if (!z) {
                    f = 0.0f;
                }
                imageView6.setScaleY(f);
                ImageView imageView7 = this.stickerSettingsButton;
                if (!z) {
                    i2 = 4;
                }
                imageView7.setVisibility(i2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void showBottomTab(boolean z, boolean z2) {
        float f;
        float f2;
        float f3 = 0.0f;
        this.lastBottomScrollDy = 0.0f;
        if (z && this.bottomTabContainer.getTag() == null) {
            return;
        }
        if (z || this.bottomTabContainer.getTag() == null) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
                AnimatorSet animatorSet = this.bottomTabContainerAnimation;
                int i = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.bottomTabContainerAnimation = null;
                }
                FrameLayout frameLayout = this.bottomTabContainer;
                if (!z) {
                    i = 1;
                }
                frameLayout.setTag(i);
                float f4 = 54.0f;
                if (z2) {
                    this.bottomTabContainerAnimation = new AnimatorSet();
                    AnimatorSet animatorSet2 = this.bottomTabContainerAnimation;
                    Animator[] animatorArr = new Animator[2];
                    FrameLayout frameLayout2 = this.bottomTabContainer;
                    Property property = View.TRANSLATION_Y;
                    float[] fArr = new float[1];
                    if (z) {
                        f2 = 0.0f;
                    } else {
                        if (this.needEmojiSearch) {
                            f4 = 49.0f;
                        }
                        f2 = (float) AndroidUtilities.dp(f4);
                    }
                    fArr[0] = f2;
                    animatorArr[0] = ObjectAnimator.ofFloat(frameLayout2, property, fArr);
                    View view = this.shadowLine;
                    Property property2 = View.TRANSLATION_Y;
                    float[] fArr2 = new float[1];
                    if (!z) {
                        f3 = (float) AndroidUtilities.dp(49.0f);
                    }
                    fArr2[0] = f3;
                    animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
                    animatorSet2.playTogether(animatorArr);
                    this.bottomTabContainerAnimation.setDuration(200);
                    this.bottomTabContainerAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.bottomTabContainerAnimation.start();
                    return;
                }
                FrameLayout frameLayout3 = this.bottomTabContainer;
                if (z) {
                    f = 0.0f;
                } else {
                    if (this.needEmojiSearch) {
                        f4 = 49.0f;
                    }
                    f = (float) AndroidUtilities.dp(f4);
                }
                frameLayout3.setTranslationY(f);
                View view2 = this.shadowLine;
                if (!z) {
                    f3 = (float) AndroidUtilities.dp(49.0f);
                }
                view2.setTranslationY(f3);
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkStickersTabY(View view, int i) {
        RecyclerListView recyclerListView;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (view == null) {
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            this.stickersMinusDy = 0;
            scrollSlidingTabStrip.setTranslationY((float) 0);
        } else if (view.getVisibility() == 0) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                return;
            }
            if (i <= 0 || (recyclerListView = this.stickersGridView) == null || recyclerListView.getVisibility() != 0 || (findViewHolderForAdapterPosition = this.stickersGridView.findViewHolderForAdapterPosition(0)) == null || findViewHolderForAdapterPosition.itemView.getTop() + this.searchFieldHeight < this.stickersGridView.getPaddingTop()) {
                this.stickersMinusDy -= i;
                int i2 = this.stickersMinusDy;
                if (i2 > 0) {
                    this.stickersMinusDy = 0;
                } else if (i2 < (-AndroidUtilities.dp(288.0f))) {
                    this.stickersMinusDy = -AndroidUtilities.dp(288.0f);
                }
                this.stickersTab.setTranslationY((float) Math.max(-AndroidUtilities.dp(48.0f), this.stickersMinusDy));
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkEmojiSearchFieldScroll(boolean z) {
        RecyclerListView recyclerListView;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        boolean z2 = false;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.emojiGridView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition == null) {
                this.emojiSearchField.showShadow(true, !z);
            } else {
                this.emojiSearchField.showShadow(findViewHolderForAdapterPosition.itemView.getTop() < this.emojiGridView.getPaddingTop(), !z);
            }
            showEmojiShadow(false, !z);
        } else if (this.emojiSearchField != null && (recyclerListView = this.emojiGridView) != null) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition2 != null) {
                this.emojiSearchField.setTranslationY((float) findViewHolderForAdapterPosition2.itemView.getTop());
            } else {
                this.emojiSearchField.setTranslationY((float) (-this.searchFieldHeight));
            }
            this.emojiSearchField.showShadow(false, !z);
            if (findViewHolderForAdapterPosition2 == null || ((float) findViewHolderForAdapterPosition2.itemView.getTop()) < ((float) (AndroidUtilities.dp(38.0f) - this.searchFieldHeight)) + this.emojiTabs.getTranslationY()) {
                z2 = true;
            }
            showEmojiShadow(z2, !z);
        }
    }

    /* access modifiers changed from: private */
    public void checkEmojiTabY(View view, int i) {
        RecyclerListView recyclerListView;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (view == null) {
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.emojiTabs;
            this.emojiMinusDy = 0;
            scrollSlidingTabStrip.setTranslationY((float) 0);
            this.emojiTabsShadow.setTranslationY((float) this.emojiMinusDy);
        } else if (view.getVisibility() == 0) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
                if (i > 0 && (recyclerListView = this.emojiGridView) != null && recyclerListView.getVisibility() == 0 && (findViewHolderForAdapterPosition = this.emojiGridView.findViewHolderForAdapterPosition(0)) != null) {
                    if (findViewHolderForAdapterPosition.itemView.getTop() + (this.needEmojiSearch ? this.searchFieldHeight : 0) >= this.emojiGridView.getPaddingTop()) {
                        return;
                    }
                }
                this.emojiMinusDy -= i;
                int i2 = this.emojiMinusDy;
                if (i2 > 0) {
                    this.emojiMinusDy = 0;
                } else if (i2 < (-AndroidUtilities.dp(288.0f))) {
                    this.emojiMinusDy = -AndroidUtilities.dp(288.0f);
                }
                this.emojiTabs.setTranslationY((float) Math.max(-AndroidUtilities.dp(38.0f), this.emojiMinusDy));
                this.emojiTabsShadow.setTranslationY(this.emojiTabs.getTranslationY());
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkGifSearchFieldScroll(boolean z) {
        RecyclerListView recyclerListView;
        GifSearchAdapter gifSearchAdapter2;
        int findLastVisibleItemPosition;
        RecyclerListView recyclerListView2 = this.gifGridView;
        if (recyclerListView2 != null && recyclerListView2.getAdapter() == (gifSearchAdapter2 = this.gifSearchAdapter) && !gifSearchAdapter2.searchEndReached && this.gifSearchAdapter.reqId == 0 && !this.gifSearchAdapter.results.isEmpty() && (findLastVisibleItemPosition = this.gifLayoutManager.findLastVisibleItemPosition()) != -1 && findLastVisibleItemPosition > this.gifLayoutManager.getItemCount() - 5) {
            GifSearchAdapter gifSearchAdapter3 = this.gifSearchAdapter;
            gifSearchAdapter3.search(gifSearchAdapter3.lastSearchImageString, this.gifSearchAdapter.nextSearchOffset, true);
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        boolean z2 = false;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.gifGridView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition == null) {
                this.gifSearchField.showShadow(true, !z);
                return;
            }
            SearchField searchField = this.gifSearchField;
            if (findViewHolderForAdapterPosition.itemView.getTop() < this.gifGridView.getPaddingTop()) {
                z2 = true;
            }
            searchField.showShadow(z2, !z);
        } else if (this.gifSearchField != null && (recyclerListView = this.gifGridView) != null) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = recyclerListView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition2 != null) {
                this.gifSearchField.setTranslationY((float) findViewHolderForAdapterPosition2.itemView.getTop());
            } else {
                this.gifSearchField.setTranslationY((float) (-this.searchFieldHeight));
            }
            this.gifSearchField.showShadow(false, !z);
        }
    }

    /* access modifiers changed from: private */
    public void checkScroll() {
        int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (findFirstVisibleItemPosition != -1 && this.stickersGridView != null) {
            int i = this.favTabBum;
            if (i <= 0 && (i = this.recentTabBum) <= 0) {
                i = this.stickersTabOffset;
            }
            this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition), i);
        }
    }

    /* access modifiers changed from: private */
    public void saveNewPage() {
        ViewPager viewPager = this.pager;
        if (viewPager != null) {
            int currentItem = viewPager.getCurrentItem();
            int i = 1;
            if (currentItem != 2) {
                i = currentItem == 1 ? 2 : 0;
            }
            if (this.currentPage != i) {
                this.currentPage = i;
                MessagesController.getGlobalEmojiSettings().edit().putInt("selected_page", i).commit();
            }
        }
    }

    public void clearRecentEmoji() {
        Emoji.clearRecentEmoji();
        this.emojiAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public void showTrendingTab(boolean z) {
        if (z) {
            this.trendingGridView.setVisibility(0);
            this.stickersGridView.setVisibility(8);
            this.stickersSearchField.setVisibility(8);
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            int i = this.trendingTabNum;
            int i2 = this.recentTabBum;
            if (i2 <= 0) {
                i2 = this.stickersTabOffset;
            }
            scrollSlidingTabStrip.onPageScrolled(i, i2);
            saveNewPage();
            return;
        }
        this.trendingGridView.setVisibility(8);
        this.stickersGridView.setVisibility(0);
        this.stickersSearchField.setVisibility(0);
    }

    /* access modifiers changed from: private */
    public void onPageScrolled(int i, int i2, int i3) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null) {
            int i4 = 0;
            if (i == 1) {
                if (i3 != 0) {
                    i4 = 2;
                }
                emojiViewDelegate.onTabOpened(i4);
            } else if (i == 2) {
                emojiViewDelegate.onTabOpened(3);
            } else {
                emojiViewDelegate.onTabOpened(0);
            }
        }
    }

    /* access modifiers changed from: private */
    public void postBackspaceRunnable(int i) {
        AndroidUtilities.runOnUIThread(new Runnable(i) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                EmojiView.this.lambda$postBackspaceRunnable$8$EmojiView(this.f$1);
            }
        }, (long) i);
    }

    public /* synthetic */ void lambda$postBackspaceRunnable$8$EmojiView(int i) {
        if (this.backspacePressed) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate != null && emojiViewDelegate.onBackspace()) {
                this.backspaceButton.performHapticFeedback(3);
            }
            this.backspaceOnce = true;
            postBackspaceRunnable(Math.max(50, i - 100));
        }
    }

    public void switchToGifRecent() {
        showBackspaceButton(false, false);
        showStickerSettingsButton(false, false);
        this.pager.setCurrentItem(1, false);
    }

    /* access modifiers changed from: private */
    public void updateEmojiTabs() {
        boolean z = !Emoji.recentEmoji.isEmpty();
        int i = this.hasRecentEmoji;
        if (i == -1 || i != z) {
            this.hasRecentEmoji = z ? 1 : 0;
            this.emojiTabs.removeTabs();
            String[] strArr = {LocaleController.getString("RecentStickers", NUM), LocaleController.getString("Emoji1", NUM), LocaleController.getString("Emoji2", NUM), LocaleController.getString("Emoji3", NUM), LocaleController.getString("Emoji4", NUM), LocaleController.getString("Emoji5", NUM), LocaleController.getString("Emoji6", NUM), LocaleController.getString("Emoji7", NUM), LocaleController.getString("Emoji8", NUM)};
            for (int i2 = 0; i2 < this.emojiIcons.length; i2++) {
                if (i2 != 0 || !Emoji.recentEmoji.isEmpty()) {
                    this.emojiTabs.addIconTab(i2, this.emojiIcons[i2]).setContentDescription(strArr[i2]);
                }
            }
            this.emojiTabs.updateTabStyles();
        }
    }

    /* access modifiers changed from: private */
    public void updateStickerTabs() {
        boolean z;
        ArrayList<TLRPC.Document> arrayList;
        ArrayList<TLRPC.Document> arrayList2;
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
        if (scrollSlidingTabStrip != null) {
            this.recentTabBum = -2;
            this.favTabBum = -2;
            this.trendingTabNum = -2;
            this.stickersTabOffset = 0;
            int currentPosition = scrollSlidingTabStrip.getCurrentPosition();
            int i = 1;
            this.stickersTab.beginUpdate((getParent() == null || getVisibility() != 0 || (this.installingStickerSets.size() == 0 && this.removingStickerSets.size() == 0)) ? false : true);
            ArrayList<Long> unreadStickerSets = MediaDataController.getInstance(this.currentAccount).getUnreadStickerSets();
            TrendingGridAdapter trendingGridAdapter2 = this.trendingGridAdapter;
            if (!(trendingGridAdapter2 == null || trendingGridAdapter2.getItemCount() == 0 || unreadStickerSets.isEmpty())) {
                this.stickersCounter = this.stickersTab.addIconTabWithCounter(3, this.stickerIcons[2]);
                int i2 = this.stickersTabOffset;
                this.trendingTabNum = i2;
                this.stickersTabOffset = i2 + 1;
                this.stickersCounter.setText(String.format("%d", new Object[]{Integer.valueOf(unreadStickerSets.size())}));
            }
            if (!this.favouriteStickers.isEmpty()) {
                int i3 = this.stickersTabOffset;
                this.favTabBum = i3;
                this.stickersTabOffset = i3 + 1;
                this.stickersTab.addIconTab(1, this.stickerIcons[1]).setContentDescription(LocaleController.getString("FavoriteStickers", NUM));
                z = true;
            } else {
                z = false;
            }
            if (!this.recentStickers.isEmpty()) {
                int i4 = this.stickersTabOffset;
                this.recentTabBum = i4;
                this.stickersTabOffset = i4 + 1;
                this.stickersTab.addIconTab(0, this.stickerIcons[0]).setContentDescription(LocaleController.getString("RecentStickers", NUM));
                z = true;
            }
            this.stickerSets.clear();
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = null;
            this.groupStickerSet = null;
            this.groupStickerPackPosition = -1;
            this.groupStickerPackNum = -10;
            ArrayList<TLRPC.TL_messages_stickerSet> stickerSets2 = MediaDataController.getInstance(this.currentAccount).getStickerSets(0);
            boolean z2 = z;
            for (int i5 = 0; i5 < stickerSets2.size(); i5++) {
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet2 = stickerSets2.get(i5);
                if (!tL_messages_stickerSet2.set.archived && (arrayList2 = tL_messages_stickerSet2.documents) != null && !arrayList2.isEmpty()) {
                    this.stickerSets.add(tL_messages_stickerSet2);
                    z2 = true;
                }
            }
            if (this.info != null) {
                long j = MessagesController.getEmojiSettings(this.currentAccount).getLong("group_hide_stickers_" + this.info.id, -1);
                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.info.id));
                if (chat == null || this.info.stickerset == null || !ChatObject.hasAdminRights(chat)) {
                    this.groupStickersHidden = j != -1;
                } else {
                    TLRPC.StickerSet stickerSet = this.info.stickerset;
                    if (stickerSet != null) {
                        this.groupStickersHidden = j == stickerSet.id;
                    }
                }
                TLRPC.ChatFull chatFull = this.info;
                if (chatFull.stickerset != null) {
                    TLRPC.TL_messages_stickerSet groupStickerSetById = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(this.info.stickerset);
                    if (!(groupStickerSetById == null || (arrayList = groupStickerSetById.documents) == null || arrayList.isEmpty() || groupStickerSetById.set == null)) {
                        TLRPC.TL_messages_stickerSet tL_messages_stickerSet3 = new TLRPC.TL_messages_stickerSet();
                        tL_messages_stickerSet3.documents = groupStickerSetById.documents;
                        tL_messages_stickerSet3.packs = groupStickerSetById.packs;
                        tL_messages_stickerSet3.set = groupStickerSetById.set;
                        if (this.groupStickersHidden) {
                            this.groupStickerPackNum = this.stickerSets.size();
                            this.stickerSets.add(tL_messages_stickerSet3);
                        } else {
                            this.groupStickerPackNum = 0;
                            this.stickerSets.add(0, tL_messages_stickerSet3);
                        }
                        if (this.info.can_set_stickers) {
                            tL_messages_stickerSet = tL_messages_stickerSet3;
                        }
                        this.groupStickerSet = tL_messages_stickerSet;
                    }
                } else if (chatFull.can_set_stickers) {
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet4 = new TLRPC.TL_messages_stickerSet();
                    if (this.groupStickersHidden) {
                        this.groupStickerPackNum = this.stickerSets.size();
                        this.stickerSets.add(tL_messages_stickerSet4);
                    } else {
                        this.groupStickerPackNum = 0;
                        this.stickerSets.add(0, tL_messages_stickerSet4);
                    }
                }
            }
            int i6 = 0;
            while (i6 < this.stickerSets.size()) {
                if (i6 == this.groupStickerPackNum) {
                    TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.info.id));
                    if (chat2 == null) {
                        this.stickerSets.remove(0);
                        i6--;
                        i6++;
                    } else {
                        this.stickersTab.addStickerTab(chat2);
                    }
                } else {
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet5 = this.stickerSets.get(i6);
                    TLRPC.Document document = tL_messages_stickerSet5.documents.get(0);
                    TLObject tLObject = tL_messages_stickerSet5.set.thumb;
                    if (!(tLObject instanceof TLRPC.TL_photoSize)) {
                        tLObject = document;
                    }
                    this.stickersTab.addStickerTab(tLObject, document, tL_messages_stickerSet5).setContentDescription(tL_messages_stickerSet5.set.title + ", " + LocaleController.getString("AccDescrStickerSet", NUM));
                }
                z2 = true;
                i6++;
            }
            TrendingGridAdapter trendingGridAdapter3 = this.trendingGridAdapter;
            if (!(trendingGridAdapter3 == null || trendingGridAdapter3.getItemCount() == 0 || !unreadStickerSets.isEmpty())) {
                this.trendingTabNum = this.stickersTabOffset + this.stickerSets.size();
                this.stickersTab.addIconTab(2, this.stickerIcons[2]).setContentDescription(LocaleController.getString("FeaturedStickers", NUM));
            }
            this.stickersTab.commitUpdate();
            this.stickersTab.updateTabStyles();
            if (currentPosition != 0) {
                this.stickersTab.onPageScrolled(currentPosition, currentPosition);
            }
            checkPanels();
            if ((!z2 || (this.trendingTabNum == 0 && MediaDataController.getInstance(this.currentAccount).areAllTrendingStickerSetsUnread())) && this.trendingTabNum >= 0) {
                if (this.scrolledToTrending == 0) {
                    showTrendingTab(true);
                    if (z2) {
                        i = 2;
                    }
                    this.scrolledToTrending = i;
                }
            } else if (this.scrolledToTrending == 1) {
                showTrendingTab(false);
                checkScroll();
                this.stickersTab.cancelPositionAnimation();
            }
        }
    }

    private void checkPanels() {
        RecyclerListView recyclerListView;
        if (this.stickersTab != null) {
            if (this.trendingTabNum == -2 && (recyclerListView = this.trendingGridView) != null && recyclerListView.getVisibility() == 0) {
                this.trendingGridView.setVisibility(8);
                this.stickersGridView.setVisibility(0);
                this.stickersSearchField.setVisibility(0);
            }
            RecyclerListView recyclerListView2 = this.trendingGridView;
            if (recyclerListView2 == null || recyclerListView2.getVisibility() != 0) {
                int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
                if (findFirstVisibleItemPosition != -1) {
                    int i = this.favTabBum;
                    if (i <= 0 && (i = this.recentTabBum) <= 0) {
                        i = this.stickersTabOffset;
                    }
                    this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition), i);
                    return;
                }
                return;
            }
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            int i2 = this.trendingTabNum;
            int i3 = this.recentTabBum;
            if (i3 <= 0) {
                i3 = this.stickersTabOffset;
            }
            scrollSlidingTabStrip.onPageScrolled(i2, i3);
        }
    }

    public void addRecentSticker(TLRPC.Document document) {
        if (document != null) {
            MediaDataController.getInstance(this.currentAccount).addRecentSticker(0, (Object) null, document, (int) (System.currentTimeMillis() / 1000), false);
            boolean isEmpty = this.recentStickers.isEmpty();
            this.recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(0);
            StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
            if (stickersGridAdapter2 != null) {
                stickersGridAdapter2.notifyDataSetChanged();
            }
            if (isEmpty) {
                updateStickerTabs();
            }
        }
    }

    public void addRecentGif(TLRPC.Document document) {
        if (document != null) {
            boolean isEmpty = this.recentGifs.isEmpty();
            this.recentGifs = MediaDataController.getInstance(this.currentAccount).getRecentGifs();
            GifAdapter gifAdapter2 = this.gifAdapter;
            if (gifAdapter2 != null) {
                gifAdapter2.notifyDataSetChanged();
            }
            if (isEmpty) {
                updateStickerTabs();
            }
        }
    }

    public void requestLayout() {
        if (!this.isLayout) {
            super.requestLayout();
        }
    }

    public void updateColors() {
        SearchField searchField;
        if (AndroidUtilities.isInMultiwindow || this.forseMultiwindowLayout) {
            Drawable background = getBackground();
            if (background != null) {
                background.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelBackground"), PorterDuff.Mode.MULTIPLY));
            }
        } else {
            setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
            if (this.needEmojiSearch) {
                this.bottomTabContainerBackground.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
            }
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.emojiTabs;
        if (scrollSlidingTabStrip != null) {
            scrollSlidingTabStrip.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
            this.emojiTabsShadow.setBackgroundColor(Theme.getColor("chat_emojiPanelShadowLine"));
        }
        EmojiColorPickerView emojiColorPickerView = this.pickerView;
        if (emojiColorPickerView != null) {
            Theme.setDrawableColor(emojiColorPickerView.backgroundDrawable, Theme.getColor("dialogBackground"));
            Theme.setDrawableColor(this.pickerView.arrowDrawable, Theme.getColor("dialogBackground"));
        }
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                searchField = this.stickersSearchField;
            } else if (i == 1) {
                searchField = this.emojiSearchField;
            } else {
                searchField = this.gifSearchField;
            }
            if (searchField != null) {
                searchField.backgroundView.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
                searchField.shadowView.setBackgroundColor(Theme.getColor("chat_emojiPanelShadowLine"));
                searchField.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiSearchIcon"), PorterDuff.Mode.MULTIPLY));
                searchField.searchIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiSearchIcon"), PorterDuff.Mode.MULTIPLY));
                Theme.setDrawableColorByKey(searchField.searchBackground.getBackground(), "chat_emojiSearchBackground");
                searchField.searchBackground.invalidate();
                searchField.searchEditText.setHintTextColor(Theme.getColor("chat_emojiSearchIcon"));
                searchField.searchEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            }
        }
        Paint paint = this.dotPaint;
        if (paint != null) {
            paint.setColor(Theme.getColor("chat_emojiPanelNewTrending"));
        }
        RecyclerListView recyclerListView = this.emojiGridView;
        if (recyclerListView != null) {
            recyclerListView.setGlowColor(Theme.getColor("chat_emojiPanelBackground"));
        }
        RecyclerListView recyclerListView2 = this.stickersGridView;
        if (recyclerListView2 != null) {
            recyclerListView2.setGlowColor(Theme.getColor("chat_emojiPanelBackground"));
        }
        RecyclerListView recyclerListView3 = this.trendingGridView;
        if (recyclerListView3 != null) {
            recyclerListView3.setGlowColor(Theme.getColor("chat_emojiPanelBackground"));
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
        if (scrollSlidingTabStrip2 != null) {
            scrollSlidingTabStrip2.setIndicatorColor(Theme.getColor("chat_emojiPanelStickerPackSelectorLine"));
            this.stickersTab.setUnderlineColor(Theme.getColor("chat_emojiPanelShadowLine"));
            this.stickersTab.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
        }
        ImageView imageView = this.backspaceButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelBackspace"), PorterDuff.Mode.MULTIPLY));
            if (this.emojiSearchField == null) {
                Theme.setSelectorDrawableColor(this.backspaceButton.getBackground(), Theme.getColor("chat_emojiPanelBackground"), false);
                Theme.setSelectorDrawableColor(this.backspaceButton.getBackground(), Theme.getColor("chat_emojiPanelBackground"), true);
            }
        }
        ImageView imageView2 = this.stickerSettingsButton;
        if (imageView2 != null) {
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelBackspace"), PorterDuff.Mode.MULTIPLY));
        }
        ImageView imageView3 = this.searchButton;
        if (imageView3 != null) {
            imageView3.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelBackspace"), PorterDuff.Mode.MULTIPLY));
        }
        View view = this.shadowLine;
        if (view != null) {
            view.setBackgroundColor(Theme.getColor("chat_emojiPanelShadowLine"));
        }
        TextView textView = this.mediaBanTooltip;
        if (textView != null) {
            ((ShapeDrawable) textView.getBackground()).getPaint().setColor(Theme.getColor("chat_gifSaveHintBackground"));
            this.mediaBanTooltip.setTextColor(Theme.getColor("chat_gifSaveHintText"));
        }
        TextView textView2 = this.stickersCounter;
        if (textView2 != null) {
            textView2.setTextColor(Theme.getColor("chat_emojiPanelBadgeText"));
            Theme.setDrawableColor(this.stickersCounter.getBackground(), Theme.getColor("chat_emojiPanelBadgeBackground"));
            this.stickersCounter.invalidate();
        }
        int i2 = 0;
        while (true) {
            Drawable[] drawableArr = this.tabIcons;
            if (i2 >= drawableArr.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr[i2], Theme.getColor("chat_emojiBottomPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.tabIcons[i2], Theme.getColor("chat_emojiPanelIconSelected"), true);
            i2++;
        }
        int i3 = 0;
        while (true) {
            Drawable[] drawableArr2 = this.emojiIcons;
            if (i3 >= drawableArr2.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr2[i3], Theme.getColor("chat_emojiPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.emojiIcons[i3], Theme.getColor("chat_emojiPanelIconSelected"), true);
            i3++;
        }
        int i4 = 0;
        while (true) {
            Drawable[] drawableArr3 = this.stickerIcons;
            if (i4 < drawableArr3.length) {
                Theme.setEmojiDrawableColor(drawableArr3[i4], Theme.getColor("chat_emojiPanelIcon"), false);
                Theme.setEmojiDrawableColor(this.stickerIcons[i4], Theme.getColor("chat_emojiPanelIconSelected"), true);
                i4++;
            } else {
                return;
            }
        }
    }

    public void onMeasure(int i, int i2) {
        this.isLayout = true;
        if (AndroidUtilities.isInMultiwindow || this.forseMultiwindowLayout) {
            if (this.currentBackgroundType != 1) {
                if (Build.VERSION.SDK_INT >= 21) {
                    setOutlineProvider((ViewOutlineProvider) this.outlineProvider);
                    setClipToOutline(true);
                    setElevation((float) AndroidUtilities.dp(2.0f));
                }
                setBackgroundResource(NUM);
                getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelBackground"), PorterDuff.Mode.MULTIPLY));
                if (this.needEmojiSearch) {
                    this.bottomTabContainerBackground.setBackgroundDrawable((Drawable) null);
                }
                this.currentBackgroundType = 1;
            }
        } else if (this.currentBackgroundType != 0) {
            if (Build.VERSION.SDK_INT >= 21) {
                setOutlineProvider((ViewOutlineProvider) null);
                setClipToOutline(false);
                setElevation(0.0f);
            }
            setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
            if (this.needEmojiSearch) {
                this.bottomTabContainerBackground.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
            }
            this.currentBackgroundType = 0;
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), NUM));
        this.isLayout = false;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        if (this.lastNotifyWidth != i5) {
            this.lastNotifyWidth = i5;
            reloadStickersAdapter();
        }
        View view = (View) getParent();
        if (view != null) {
            int i6 = i4 - i2;
            int height = view.getHeight();
            if (!(this.lastNotifyHeight == i6 && this.lastNotifyHeight2 == height)) {
                EmojiViewDelegate emojiViewDelegate = this.delegate;
                if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                    this.bottomTabContainer.setTranslationY((float) AndroidUtilities.dp(49.0f));
                } else if (this.bottomTabContainer.getTag() == null) {
                    if (i6 < this.lastNotifyHeight) {
                        this.bottomTabContainer.setTranslationY(0.0f);
                    } else {
                        this.bottomTabContainer.setTranslationY(-((getY() + ((float) getMeasuredHeight())) - ((float) view.getHeight())));
                    }
                }
                this.lastNotifyHeight = i6;
                this.lastNotifyHeight2 = height;
            }
        }
        super.onLayout(z, i, i2, i3, i4);
    }

    private void reloadStickersAdapter() {
        StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
        if (stickersGridAdapter2 != null) {
            stickersGridAdapter2.notifyDataSetChanged();
        }
        TrendingGridAdapter trendingGridAdapter2 = this.trendingGridAdapter;
        if (trendingGridAdapter2 != null) {
            trendingGridAdapter2.notifyDataSetChanged();
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

    public void setDelegate(EmojiViewDelegate emojiViewDelegate) {
        this.delegate = emojiViewDelegate;
    }

    public void setDragListener(DragListener dragListener2) {
        this.dragListener = dragListener2;
    }

    public void setChatInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        updateStickerTabs();
    }

    public void invalidateViews() {
        this.emojiGridView.invalidateViews();
    }

    public void setForseMultiwindowLayout(boolean z) {
        this.forseMultiwindowLayout = z;
    }

    public void onOpen(boolean z) {
        if (!(this.currentPage == 0 || this.currentChatId == 0)) {
            this.currentPage = 0;
        }
        if (this.currentPage == 0 || z || this.views.size() == 1) {
            showBackspaceButton(true, false);
            showStickerSettingsButton(false, false);
            if (this.pager.getCurrentItem() != 0) {
                this.pager.setCurrentItem(0, !z);
                return;
            }
            return;
        }
        int i = this.currentPage;
        if (i == 1) {
            showBackspaceButton(false, false);
            showStickerSettingsButton(true, false);
            if (this.pager.getCurrentItem() != 2) {
                this.pager.setCurrentItem(2, false);
            }
            if (this.stickersTab == null) {
                return;
            }
            if (this.trendingTabNum != 0 || !MediaDataController.getInstance(this.currentAccount).areAllTrendingStickerSetsUnread()) {
                int i2 = this.recentTabBum;
                if (i2 >= 0) {
                    this.stickersTab.selectTab(i2);
                    return;
                }
                int i3 = this.favTabBum;
                if (i3 >= 0) {
                    this.stickersTab.selectTab(i3);
                } else {
                    this.stickersTab.selectTab(this.stickersTabOffset);
                }
            } else {
                showTrendingTab(true);
            }
        } else if (i == 2) {
            showBackspaceButton(false, false);
            showStickerSettingsButton(false, false);
            if (this.pager.getCurrentItem() != 1) {
                this.pager.setCurrentItem(1, false);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    EmojiView.this.lambda$onAttachedToWindow$9$EmojiView();
                }
            });
        }
    }

    public /* synthetic */ void lambda$onAttachedToWindow$9$EmojiView() {
        updateStickerTabs();
        reloadStickersAdapter();
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i != 8) {
            Emoji.sortEmoji();
            this.emojiAdapter.notifyDataSetChanged();
            if (this.stickersGridAdapter != null) {
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
                updateStickerTabs();
                reloadStickersAdapter();
            }
            TrendingGridAdapter trendingGridAdapter2 = this.trendingGridAdapter;
            if (trendingGridAdapter2 != null) {
                this.trendingLoaded = false;
                trendingGridAdapter2.notifyDataSetChanged();
            }
            checkDocuments(true);
            checkDocuments(false);
            MediaDataController.getInstance(this.currentAccount).loadRecents(0, true, true, false);
            MediaDataController.getInstance(this.currentAccount).loadRecents(0, false, true, false);
            MediaDataController.getInstance(this.currentAccount).loadRecents(2, false, true, false);
        }
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void onDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentDocumentsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EmojiPopupWindow emojiPopupWindow = this.pickerViewPopup;
        if (emojiPopupWindow != null && emojiPopupWindow.isShowing()) {
            this.pickerViewPopup.dismiss();
        }
    }

    private void checkDocuments(boolean z) {
        if (z) {
            this.recentGifs = MediaDataController.getInstance(this.currentAccount).getRecentGifs();
            GifAdapter gifAdapter2 = this.gifAdapter;
            if (gifAdapter2 != null) {
                gifAdapter2.notifyDataSetChanged();
                return;
            }
            return;
        }
        int size = this.recentStickers.size();
        int size2 = this.favouriteStickers.size();
        this.recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(0);
        this.favouriteStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(2);
        for (int i = 0; i < this.favouriteStickers.size(); i++) {
            TLRPC.Document document = this.favouriteStickers.get(i);
            int i2 = 0;
            while (true) {
                if (i2 >= this.recentStickers.size()) {
                    break;
                }
                TLRPC.Document document2 = this.recentStickers.get(i2);
                if (document2.dc_id == document.dc_id && document2.id == document.id) {
                    this.recentStickers.remove(i2);
                    break;
                }
                i2++;
            }
        }
        if (!(size == this.recentStickers.size() && size2 == this.favouriteStickers.size())) {
            updateStickerTabs();
        }
        StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
        if (stickersGridAdapter2 != null) {
            stickersGridAdapter2.notifyDataSetChanged();
        }
        checkPanels();
    }

    public void setStickersBanned(boolean z, int i) {
        if (this.typeTabs != null) {
            if (z) {
                this.currentChatId = i;
            } else {
                this.currentChatId = 0;
            }
            View tab = this.typeTabs.getTab(2);
            if (tab != null) {
                tab.setAlpha(this.currentChatId != 0 ? 0.5f : 1.0f);
                if (this.currentChatId != 0 && this.pager.getCurrentItem() != 0) {
                    showBackspaceButton(true, true);
                    showStickerSettingsButton(false, true);
                    this.pager.setCurrentItem(0, false);
                }
            }
        }
    }

    public void showStickerBanHint(boolean z) {
        TLRPC.Chat chat;
        TLRPC.TL_chatBannedRights tL_chatBannedRights;
        if (this.mediaBanTooltip.getVisibility() != 0 && (chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.currentChatId))) != null) {
            if (ChatObject.hasAdminRights(chat) || (tL_chatBannedRights = chat.default_banned_rights) == null || !tL_chatBannedRights.send_stickers) {
                TLRPC.TL_chatBannedRights tL_chatBannedRights2 = chat.banned_rights;
                if (tL_chatBannedRights2 != null) {
                    if (AndroidUtilities.isBannedForever(tL_chatBannedRights2)) {
                        if (z) {
                            this.mediaBanTooltip.setText(LocaleController.getString("AttachGifRestrictedForever", NUM));
                        } else {
                            this.mediaBanTooltip.setText(LocaleController.getString("AttachStickersRestrictedForever", NUM));
                        }
                    } else if (z) {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachGifRestricted", NUM, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
                    } else {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachStickersRestricted", NUM, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
                    }
                } else {
                    return;
                }
            } else if (z) {
                this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachGifRestricted", NUM));
            } else {
                this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachStickersRestricted", NUM));
            }
            this.mediaBanTooltip.setVisibility(0);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mediaBanTooltip, View.ALPHA, new float[]{0.0f, 1.0f})});
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            EmojiView.AnonymousClass35.this.lambda$onAnimationEnd$0$EmojiView$35();
                        }
                    }, 5000);
                }

                public /* synthetic */ void lambda$onAnimationEnd$0$EmojiView$35() {
                    if (EmojiView.this.mediaBanTooltip != null) {
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(EmojiView.this.mediaBanTooltip, View.ALPHA, new float[]{0.0f})});
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (EmojiView.this.mediaBanTooltip != null) {
                                    EmojiView.this.mediaBanTooltip.setVisibility(4);
                                }
                            }
                        });
                        animatorSet.setDuration(300);
                        animatorSet.start();
                    }
                }
            });
            animatorSet.setDuration(300);
            animatorSet.start();
        }
    }

    private void updateVisibleTrendingSets() {
        RecyclerListView recyclerListView;
        TrendingGridAdapter trendingGridAdapter2 = this.trendingGridAdapter;
        if (trendingGridAdapter2 != null && trendingGridAdapter2 != null) {
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    try {
                        recyclerListView = this.trendingGridView;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        return;
                    }
                } else {
                    recyclerListView = this.stickersGridView;
                }
                int childCount = recyclerListView.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = recyclerListView.getChildAt(i2);
                    if (childAt instanceof FeaturedStickerSetInfoCell) {
                        if (((RecyclerListView.Holder) recyclerListView.getChildViewHolder(childAt)) != null) {
                            FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) childAt;
                            ArrayList<Long> unreadStickerSets = MediaDataController.getInstance(this.currentAccount).getUnreadStickerSets();
                            TLRPC.StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
                            boolean z = true;
                            boolean z2 = unreadStickerSets != null && unreadStickerSets.contains(Long.valueOf(stickerSet.set.id));
                            featuredStickerSetInfoCell.setStickerSet(stickerSet, z2, true);
                            if (z2) {
                                MediaDataController.getInstance(this.currentAccount).markFaturedStickersByIdAsRead(stickerSet.set.id);
                            }
                            boolean z3 = this.installingStickerSets.indexOfKey(stickerSet.set.id) >= 0;
                            if (this.removingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
                                z = false;
                            }
                            if (z3 || z) {
                                if (z3 && featuredStickerSetInfoCell.isInstalled()) {
                                    this.installingStickerSets.remove(stickerSet.set.id);
                                } else if (z && !featuredStickerSetInfoCell.isInstalled()) {
                                    this.removingStickerSets.remove(stickerSet.set.id);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean areThereAnyStickers() {
        StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
        return stickersGridAdapter2 != null && stickersGridAdapter2.getItemCount() > 0;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC.StickerSet stickerSet;
        int i3 = 0;
        if (i == NotificationCenter.stickersDidLoad) {
            if (objArr[0].intValue() == 0) {
                updateStickerTabs();
                TrendingGridAdapter trendingGridAdapter2 = this.trendingGridAdapter;
                if (trendingGridAdapter2 != null) {
                    if (this.trendingLoaded) {
                        updateVisibleTrendingSets();
                    } else {
                        trendingGridAdapter2.notifyDataSetChanged();
                    }
                }
                reloadStickersAdapter();
                checkPanels();
            }
        } else if (i == NotificationCenter.recentDocumentsDidLoad) {
            boolean booleanValue = objArr[0].booleanValue();
            int intValue = objArr[1].intValue();
            if (booleanValue || intValue == 0 || intValue == 2) {
                checkDocuments(booleanValue);
            }
        } else if (i == NotificationCenter.featuredStickersDidLoad) {
            if (this.trendingGridAdapter != null) {
                if (this.featuredStickersHash != MediaDataController.getInstance(this.currentAccount).getFeaturesStickersHashWithoutUnread()) {
                    this.trendingLoaded = false;
                }
                if (this.trendingLoaded) {
                    updateVisibleTrendingSets();
                } else {
                    this.trendingGridAdapter.notifyDataSetChanged();
                }
            }
            PagerSlidingTabStrip pagerSlidingTabStrip = this.typeTabs;
            if (pagerSlidingTabStrip != null) {
                int childCount = pagerSlidingTabStrip.getChildCount();
                while (i3 < childCount) {
                    this.typeTabs.getChildAt(i3).invalidate();
                    i3++;
                }
            }
            updateStickerTabs();
        } else if (i == NotificationCenter.groupStickersDidLoad) {
            TLRPC.ChatFull chatFull = this.info;
            if (chatFull != null && (stickerSet = chatFull.stickerset) != null && stickerSet.id == objArr[0].longValue()) {
                updateStickerTabs();
            }
        } else if (i == NotificationCenter.emojiDidLoad) {
            RecyclerListView recyclerListView = this.stickersGridView;
            if (recyclerListView != null) {
                int childCount2 = recyclerListView.getChildCount();
                while (i3 < childCount2) {
                    View childAt = this.stickersGridView.getChildAt(i3);
                    if ((childAt instanceof StickerSetNameCell) || (childAt instanceof StickerEmojiCell)) {
                        childAt.invalidate();
                    }
                    i3++;
                }
            }
            EmojiColorPickerView emojiColorPickerView = this.pickerView;
            if (emojiColorPickerView != null) {
                emojiColorPickerView.invalidate();
            }
        } else if (i == NotificationCenter.newEmojiSuggestionsAvailable && this.emojiGridView != null && this.needEmojiSearch) {
            if ((this.emojiSearchField.progressDrawable.isAnimating() || this.emojiGridView.getAdapter() == this.emojiSearchAdapter) && !TextUtils.isEmpty(this.emojiSearchAdapter.lastSearchEmojiString)) {
                EmojiSearchAdapter emojiSearchAdapter2 = this.emojiSearchAdapter;
                emojiSearchAdapter2.search(emojiSearchAdapter2.lastSearchEmojiString);
            }
        }
    }

    private class TrendingGridAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public SparseArray<Object> cache = new SparseArray<>();
        private Context context;
        /* access modifiers changed from: private */
        public SparseArray<TLRPC.StickerSetCovered> positionsToSets = new SparseArray<>();
        private ArrayList<TLRPC.StickerSetCovered> sets = new ArrayList<>();
        /* access modifiers changed from: private */
        public int stickersPerRow;
        /* access modifiers changed from: private */
        public int totalItems;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public TrendingGridAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            return this.totalItems;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public int getItemViewType(int i) {
            Object obj = this.cache.get(i);
            if (obj != null) {
                return obj instanceof TLRPC.Document ? 0 : 2;
            }
            return 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FeaturedStickerSetInfoCell featuredStickerSetInfoCell;
            if (i == 0) {
                featuredStickerSetInfoCell = new StickerEmojiCell(this.context) {
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                    }
                };
            } else if (i == 1) {
                featuredStickerSetInfoCell = new EmptyCell(this.context);
            } else if (i != 2) {
                featuredStickerSetInfoCell = null;
            } else {
                FeaturedStickerSetInfoCell featuredStickerSetInfoCell2 = new FeaturedStickerSetInfoCell(this.context, 17);
                featuredStickerSetInfoCell2.setAddOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        EmojiView.TrendingGridAdapter.this.lambda$onCreateViewHolder$0$EmojiView$TrendingGridAdapter(view);
                    }
                });
                featuredStickerSetInfoCell = featuredStickerSetInfoCell2;
            }
            return new RecyclerListView.Holder(featuredStickerSetInfoCell);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$EmojiView$TrendingGridAdapter(View view) {
            FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
            TLRPC.StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
            if (EmojiView.this.installingStickerSets.indexOfKey(stickerSet.set.id) < 0 && EmojiView.this.removingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
                if (featuredStickerSetInfoCell.isInstalled()) {
                    EmojiView.this.removingStickerSets.put(stickerSet.set.id, stickerSet);
                    EmojiView.this.delegate.onStickerSetRemove(featuredStickerSetInfoCell.getStickerSet());
                    return;
                }
                EmojiView.this.installingStickerSets.put(stickerSet.set.id, stickerSet);
                EmojiView.this.delegate.onStickerSetAdd(featuredStickerSetInfoCell.getStickerSet());
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                ((StickerEmojiCell) viewHolder.itemView).setSticker((TLRPC.Document) this.cache.get(i), this.positionsToSets.get(i), false);
            } else if (itemViewType == 1) {
                ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(82.0f));
            } else if (itemViewType == 2) {
                ArrayList<Long> unreadStickerSets = MediaDataController.getInstance(EmojiView.this.currentAccount).getUnreadStickerSets();
                TLRPC.StickerSetCovered stickerSetCovered = this.sets.get(((Integer) this.cache.get(i)).intValue());
                boolean z2 = unreadStickerSets != null && unreadStickerSets.contains(Long.valueOf(stickerSetCovered.set.id));
                FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) viewHolder.itemView;
                featuredStickerSetInfoCell.setStickerSet(stickerSetCovered, z2);
                if (z2) {
                    MediaDataController.getInstance(EmojiView.this.currentAccount).markFaturedStickersByIdAsRead(stickerSetCovered.set.id);
                }
                boolean z3 = EmojiView.this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                if (EmojiView.this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0) {
                    z = true;
                }
                if (!z3 && !z) {
                    return;
                }
                if (z3 && featuredStickerSetInfoCell.isInstalled()) {
                    EmojiView.this.installingStickerSets.remove(stickerSetCovered.set.id);
                } else if (z && !featuredStickerSetInfoCell.isInstalled()) {
                    EmojiView.this.removingStickerSets.remove(stickerSetCovered.set.id);
                }
            }
        }

        public void notifyDataSetChanged() {
            int i;
            int measuredWidth = EmojiView.this.getMeasuredWidth();
            if (measuredWidth == 0) {
                if (AndroidUtilities.isTablet()) {
                    int i2 = AndroidUtilities.displaySize.x;
                    int i3 = (i2 * 35) / 100;
                    if (i3 < AndroidUtilities.dp(320.0f)) {
                        i3 = AndroidUtilities.dp(320.0f);
                    }
                    measuredWidth = i2 - i3;
                } else {
                    measuredWidth = AndroidUtilities.displaySize.x;
                }
                if (measuredWidth == 0) {
                    measuredWidth = 1080;
                }
            }
            this.stickersPerRow = Math.max(5, measuredWidth / AndroidUtilities.dp(72.0f));
            EmojiView.this.trendingLayoutManager.setSpanCount(this.stickersPerRow);
            if (!EmojiView.this.trendingLoaded) {
                this.cache.clear();
                this.positionsToSets.clear();
                this.sets.clear();
                this.totalItems = 0;
                ArrayList<TLRPC.StickerSetCovered> featuredStickerSets = MediaDataController.getInstance(EmojiView.this.currentAccount).getFeaturedStickerSets();
                int i4 = 0;
                int i5 = 0;
                while (true) {
                    int i6 = 1;
                    if (i4 >= featuredStickerSets.size()) {
                        break;
                    }
                    TLRPC.StickerSetCovered stickerSetCovered = featuredStickerSets.get(i4);
                    if (!MediaDataController.getInstance(EmojiView.this.currentAccount).isStickerPackInstalled(stickerSetCovered.set.id) && (!stickerSetCovered.covers.isEmpty() || stickerSetCovered.cover != null)) {
                        this.sets.add(stickerSetCovered);
                        this.positionsToSets.put(this.totalItems, stickerSetCovered);
                        SparseArray<Object> sparseArray = this.cache;
                        int i7 = this.totalItems;
                        this.totalItems = i7 + 1;
                        int i8 = i5 + 1;
                        sparseArray.put(i7, Integer.valueOf(i5));
                        int i9 = this.totalItems / this.stickersPerRow;
                        if (!stickerSetCovered.covers.isEmpty()) {
                            i6 = (int) Math.ceil((double) (((float) stickerSetCovered.covers.size()) / ((float) this.stickersPerRow)));
                            for (int i10 = 0; i10 < stickerSetCovered.covers.size(); i10++) {
                                this.cache.put(this.totalItems + i10, stickerSetCovered.covers.get(i10));
                            }
                        } else {
                            this.cache.put(this.totalItems, stickerSetCovered.cover);
                        }
                        int i11 = 0;
                        while (true) {
                            i = this.stickersPerRow;
                            if (i11 >= i6 * i) {
                                break;
                            }
                            this.positionsToSets.put(this.totalItems + i11, stickerSetCovered);
                            i11++;
                        }
                        this.totalItems += i6 * i;
                        i5 = i8;
                    }
                    i4++;
                }
                if (this.totalItems != 0) {
                    boolean unused = EmojiView.this.trendingLoaded = true;
                    EmojiView emojiView = EmojiView.this;
                    int unused2 = emojiView.featuredStickersHash = MediaDataController.getInstance(emojiView.currentAccount).getFeaturesStickersHashWithoutUnread();
                }
                super.notifyDataSetChanged();
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

        public Object getItem(int i) {
            return this.cache.get(i);
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
            if (obj == null) {
                return 1;
            }
            if (obj instanceof TLRPC.Document) {
                return 0;
            }
            return obj instanceof String ? 3 : 2;
        }

        public int getTabForPosition(int i) {
            if (i == 0) {
                i = 1;
            }
            if (this.stickersPerRow == 0) {
                int measuredWidth = EmojiView.this.getMeasuredWidth();
                if (measuredWidth == 0) {
                    measuredWidth = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
            }
            int i2 = this.positionToRow.get(i, Integer.MIN_VALUE);
            if (i2 == Integer.MIN_VALUE) {
                return (EmojiView.this.stickerSets.size() - 1) + EmojiView.this.stickersTabOffset;
            }
            Object obj = this.rowStartPack.get(i2);
            if (!(obj instanceof String)) {
                return EmojiView.this.stickerSets.indexOf((TLRPC.TL_messages_stickerSet) obj) + EmojiView.this.stickersTabOffset;
            } else if ("recent".equals(obj)) {
                return EmojiView.this.recentTabBum;
            } else {
                return EmojiView.this.favTabBum;
            }
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$EmojiView$StickersGridAdapter(View view) {
            if (EmojiView.this.groupStickerSet == null) {
                SharedPreferences.Editor edit = MessagesController.getEmojiSettings(EmojiView.this.currentAccount).edit();
                edit.putLong("group_hide_stickers_" + EmojiView.this.info.id, EmojiView.this.info.stickerset != null ? EmojiView.this.info.stickerset.id : 0).commit();
                EmojiView.this.updateStickerTabs();
                if (EmojiView.this.stickersGridAdapter != null) {
                    EmojiView.this.stickersGridAdapter.notifyDataSetChanged();
                }
            } else if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.onStickersGroupClick(EmojiView.this.info.id);
            }
        }

        public /* synthetic */ void lambda$onCreateViewHolder$1$EmojiView$StickersGridAdapter(View view) {
            if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.onStickersGroupClick(EmojiView.this.info.id);
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            StickerSetGroupInfoCell stickerSetGroupInfoCell;
            if (i == 0) {
                stickerSetGroupInfoCell = new StickerEmojiCell(this.context) {
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                    }
                };
            } else if (i == 1) {
                stickerSetGroupInfoCell = new EmptyCell(this.context);
            } else if (i == 2) {
                StickerSetNameCell stickerSetNameCell = new StickerSetNameCell(this.context, false);
                stickerSetNameCell.setOnIconClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$0$EmojiView$StickersGridAdapter(view);
                    }
                });
                stickerSetGroupInfoCell = stickerSetNameCell;
            } else if (i == 3) {
                StickerSetGroupInfoCell stickerSetGroupInfoCell2 = new StickerSetGroupInfoCell(this.context);
                stickerSetGroupInfoCell2.setAddOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$1$EmojiView$StickersGridAdapter(view);
                    }
                });
                stickerSetGroupInfoCell2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                stickerSetGroupInfoCell = stickerSetGroupInfoCell2;
            } else if (i != 4) {
                stickerSetGroupInfoCell = null;
            } else {
                View view = new View(this.context);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                stickerSetGroupInfoCell = view;
            }
            return new RecyclerListView.Holder(stickerSetGroupInfoCell);
        }

        /* JADX WARNING: type inference failed for: r3v1 */
        /* JADX WARNING: type inference failed for: r3v2, types: [java.util.ArrayList] */
        /* JADX WARNING: type inference failed for: r3v10, types: [org.telegram.tgnet.TLRPC$Chat] */
        /* JADX WARNING: type inference failed for: r3v19 */
        /* JADX WARNING: type inference failed for: r3v20 */
        /* JADX WARNING: type inference failed for: r3v21 */
        /* JADX WARNING: type inference failed for: r3v22 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r6, int r7) {
            /*
                r5 = this;
                int r0 = r6.getItemViewType()
                r1 = 0
                r2 = 1
                if (r0 == 0) goto L_0x0156
                r3 = 0
                if (r0 == r2) goto L_0x00cd
                r4 = 2
                if (r0 == r4) goto L_0x0022
                r3 = 3
                if (r0 == r3) goto L_0x0013
                goto L_0x0187
            L_0x0013:
                android.view.View r6 = r6.itemView
                org.telegram.ui.Cells.StickerSetGroupInfoCell r6 = (org.telegram.ui.Cells.StickerSetGroupInfoCell) r6
                int r0 = r5.totalItems
                int r0 = r0 - r2
                if (r7 != r0) goto L_0x001d
                r1 = 1
            L_0x001d:
                r6.setIsLast(r1)
                goto L_0x0187
            L_0x0022:
                android.view.View r6 = r6.itemView
                org.telegram.ui.Cells.StickerSetNameCell r6 = (org.telegram.ui.Cells.StickerSetNameCell) r6
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                int r0 = r0.groupStickerPackPosition
                if (r7 != r0) goto L_0x008a
                org.telegram.ui.Components.EmojiView r7 = org.telegram.ui.Components.EmojiView.this
                boolean r7 = r7.groupStickersHidden
                if (r7 == 0) goto L_0x0040
                org.telegram.ui.Components.EmojiView r7 = org.telegram.ui.Components.EmojiView.this
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r7 = r7.groupStickerSet
                if (r7 != 0) goto L_0x0040
                r7 = 0
                goto L_0x004f
            L_0x0040:
                org.telegram.ui.Components.EmojiView r7 = org.telegram.ui.Components.EmojiView.this
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r7 = r7.groupStickerSet
                if (r7 == 0) goto L_0x004c
                r7 = 2131165895(0x7var_c7, float:1.794602E38)
                goto L_0x004f
            L_0x004c:
                r7 = 2131165896(0x7var_c8, float:1.7946022E38)
            L_0x004f:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                org.telegram.tgnet.TLRPC$ChatFull r0 = r0.info
                if (r0 == 0) goto L_0x0071
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                int r0 = r0.currentAccount
                org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
                org.telegram.ui.Components.EmojiView r3 = org.telegram.ui.Components.EmojiView.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.info
                int r3 = r3.id
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                org.telegram.tgnet.TLRPC$Chat r3 = r0.getChat(r3)
            L_0x0071:
                r0 = 2131624796(0x7f0e035c, float:1.8876782E38)
                java.lang.Object[] r2 = new java.lang.Object[r2]
                if (r3 == 0) goto L_0x007b
                java.lang.String r3 = r3.title
                goto L_0x007d
            L_0x007b:
                java.lang.String r3 = "Group Stickers"
            L_0x007d:
                r2[r1] = r3
                java.lang.String r1 = "CurrentGroupStickers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
                r6.setText(r0, r7)
                goto L_0x0187
            L_0x008a:
                android.util.SparseArray<java.lang.Object> r0 = r5.cache
                java.lang.Object r7 = r0.get(r7)
                boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messages_stickerSet
                if (r0 == 0) goto L_0x00a1
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r7 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r7
                org.telegram.tgnet.TLRPC$StickerSet r7 = r7.set
                if (r7 == 0) goto L_0x0187
                java.lang.String r7 = r7.title
                r6.setText(r7, r1)
                goto L_0x0187
            L_0x00a1:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r0 = r0.recentStickers
                if (r7 != r0) goto L_0x00b7
                r7 = 2131626336(0x7f0e0960, float:1.8879905E38)
                java.lang.String r0 = "RecentStickers"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7, r1)
                goto L_0x0187
            L_0x00b7:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r0 = r0.favouriteStickers
                if (r7 != r0) goto L_0x0187
                r7 = 2131625145(0x7f0e04b9, float:1.887749E38)
                java.lang.String r0 = "FavoriteStickers"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
                r6.setText(r7, r1)
                goto L_0x0187
            L_0x00cd:
                android.view.View r6 = r6.itemView
                org.telegram.ui.Cells.EmptyCell r6 = (org.telegram.ui.Cells.EmptyCell) r6
                int r0 = r5.totalItems
                r1 = 1118044160(0x42a40000, float:82.0)
                if (r7 != r0) goto L_0x014e
                android.util.SparseIntArray r0 = r5.positionToRow
                int r7 = r7 - r2
                r4 = -2147483648(0xfffffffvar_, float:-0.0)
                int r7 = r0.get(r7, r4)
                if (r7 != r4) goto L_0x00e7
                r6.setHeight(r2)
                goto L_0x0187
            L_0x00e7:
                android.util.SparseArray<java.lang.Object> r0 = r5.rowStartPack
                java.lang.Object r7 = r0.get(r7)
                boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messages_stickerSet
                if (r0 == 0) goto L_0x00f6
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r7 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r7
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r3 = r7.documents
                goto L_0x010f
            L_0x00f6:
                boolean r0 = r7 instanceof java.lang.String
                if (r0 == 0) goto L_0x010f
                java.lang.String r0 = "recent"
                boolean r7 = r0.equals(r7)
                if (r7 == 0) goto L_0x0109
                org.telegram.ui.Components.EmojiView r7 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r3 = r7.recentStickers
                goto L_0x010f
            L_0x0109:
                org.telegram.ui.Components.EmojiView r7 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r3 = r7.favouriteStickers
            L_0x010f:
                if (r3 != 0) goto L_0x0116
                r6.setHeight(r2)
                goto L_0x0187
            L_0x0116:
                boolean r7 = r3.isEmpty()
                if (r7 == 0) goto L_0x0126
                r7 = 1090519040(0x41000000, float:8.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r6.setHeight(r7)
                goto L_0x0187
            L_0x0126:
                org.telegram.ui.Components.EmojiView r7 = org.telegram.ui.Components.EmojiView.this
                androidx.viewpager.widget.ViewPager r7 = r7.pager
                int r7 = r7.getHeight()
                int r0 = r3.size()
                float r0 = (float) r0
                int r3 = r5.stickersPerRow
                float r3 = (float) r3
                float r0 = r0 / r3
                double r3 = (double) r0
                double r3 = java.lang.Math.ceil(r3)
                int r0 = (int) r3
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r0 = r0 * r1
                int r7 = r7 - r0
                if (r7 <= 0) goto L_0x0149
                goto L_0x014a
            L_0x0149:
                r7 = 1
            L_0x014a:
                r6.setHeight(r7)
                goto L_0x0187
            L_0x014e:
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r6.setHeight(r7)
                goto L_0x0187
            L_0x0156:
                android.util.SparseArray<java.lang.Object> r0 = r5.cache
                java.lang.Object r0 = r0.get(r7)
                org.telegram.tgnet.TLRPC$Document r0 = (org.telegram.tgnet.TLRPC.Document) r0
                android.view.View r6 = r6.itemView
                org.telegram.ui.Cells.StickerEmojiCell r6 = (org.telegram.ui.Cells.StickerEmojiCell) r6
                android.util.SparseArray<java.lang.Object> r3 = r5.cacheParents
                java.lang.Object r7 = r3.get(r7)
                r6.setSticker(r0, r7, r1)
                org.telegram.ui.Components.EmojiView r7 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r7 = r7.recentStickers
                boolean r7 = r7.contains(r0)
                if (r7 != 0) goto L_0x0183
                org.telegram.ui.Components.EmojiView r7 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r7 = r7.favouriteStickers
                boolean r7 = r7.contains(r0)
                if (r7 == 0) goto L_0x0184
            L_0x0183:
                r1 = 1
            L_0x0184:
                r6.setRecent(r1)
            L_0x0187:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersGridAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public void notifyDataSetChanged() {
            Object obj;
            ArrayList<TLRPC.Document> arrayList;
            int i;
            int measuredWidth = EmojiView.this.getMeasuredWidth();
            if (measuredWidth == 0) {
                measuredWidth = AndroidUtilities.displaySize.x;
            }
            this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
            EmojiView.this.stickersLayoutManager.setSpanCount(this.stickersPerRow);
            this.rowStartPack.clear();
            this.packStartPosition.clear();
            this.positionToRow.clear();
            this.cache.clear();
            this.totalItems = 0;
            ArrayList access$10900 = EmojiView.this.stickerSets;
            int i2 = -3;
            int i3 = -3;
            int i4 = 0;
            while (i3 < access$10900.size()) {
                if (i3 == i2) {
                    SparseArray<Object> sparseArray = this.cache;
                    int i5 = this.totalItems;
                    this.totalItems = i5 + 1;
                    sparseArray.put(i5, "search");
                    i4++;
                } else {
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet = null;
                    if (i3 == -2) {
                        arrayList = EmojiView.this.favouriteStickers;
                        this.packStartPosition.put("fav", Integer.valueOf(this.totalItems));
                        obj = "fav";
                    } else if (i3 == -1) {
                        arrayList = EmojiView.this.recentStickers;
                        this.packStartPosition.put("recent", Integer.valueOf(this.totalItems));
                        obj = "recent";
                    } else {
                        TLRPC.TL_messages_stickerSet tL_messages_stickerSet2 = (TLRPC.TL_messages_stickerSet) access$10900.get(i3);
                        ArrayList<TLRPC.Document> arrayList2 = tL_messages_stickerSet2.documents;
                        this.packStartPosition.put(tL_messages_stickerSet2, Integer.valueOf(this.totalItems));
                        tL_messages_stickerSet = tL_messages_stickerSet2;
                        arrayList = arrayList2;
                        obj = null;
                    }
                    if (i3 == EmojiView.this.groupStickerPackNum) {
                        int unused = EmojiView.this.groupStickerPackPosition = this.totalItems;
                        if (arrayList.isEmpty()) {
                            this.rowStartPack.put(i4, tL_messages_stickerSet);
                            int i6 = i4 + 1;
                            this.positionToRow.put(this.totalItems, i4);
                            this.rowStartPack.put(i6, tL_messages_stickerSet);
                            this.positionToRow.put(this.totalItems + 1, i6);
                            SparseArray<Object> sparseArray2 = this.cache;
                            int i7 = this.totalItems;
                            this.totalItems = i7 + 1;
                            sparseArray2.put(i7, tL_messages_stickerSet);
                            SparseArray<Object> sparseArray3 = this.cache;
                            int i8 = this.totalItems;
                            this.totalItems = i8 + 1;
                            sparseArray3.put(i8, "group");
                            i4 = i6 + 1;
                        }
                    }
                    if (!arrayList.isEmpty()) {
                        int ceil = (int) Math.ceil((double) (((float) arrayList.size()) / ((float) this.stickersPerRow)));
                        if (tL_messages_stickerSet != null) {
                            this.cache.put(this.totalItems, tL_messages_stickerSet);
                        } else {
                            this.cache.put(this.totalItems, arrayList);
                        }
                        this.positionToRow.put(this.totalItems, i4);
                        int i9 = 0;
                        while (i9 < arrayList.size()) {
                            int i10 = i9 + 1;
                            int i11 = this.totalItems + i10;
                            this.cache.put(i11, arrayList.get(i9));
                            if (tL_messages_stickerSet != null) {
                                this.cacheParents.put(i11, tL_messages_stickerSet);
                            } else {
                                this.cacheParents.put(i11, obj);
                            }
                            this.positionToRow.put(this.totalItems + i10, i4 + 1 + (i9 / this.stickersPerRow));
                            i9 = i10;
                        }
                        int i12 = 0;
                        while (true) {
                            i = ceil + 1;
                            if (i12 >= i) {
                                break;
                            }
                            if (tL_messages_stickerSet != null) {
                                this.rowStartPack.put(i4 + i12, tL_messages_stickerSet);
                            } else {
                                this.rowStartPack.put(i4 + i12, i3 == -1 ? "recent" : "fav");
                            }
                            i12++;
                        }
                        this.totalItems += (ceil * this.stickersPerRow) + 1;
                        i4 += i;
                    }
                }
                i3++;
                i2 = -3;
            }
            super.notifyDataSetChanged();
        }
    }

    private class EmojiGridAdapter extends RecyclerListView.SelectionAdapter {
        private int itemCount;
        /* access modifiers changed from: private */
        public SparseIntArray positionToSection;
        /* access modifiers changed from: private */
        public SparseIntArray sectionToPosition;

        public long getItemId(int i) {
            return (long) i;
        }

        private EmojiGridAdapter() {
            this.positionToSection = new SparseIntArray();
            this.sectionToPosition = new SparseIntArray();
        }

        public int getItemCount() {
            return this.itemCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                EmojiView emojiView = EmojiView.this;
                view = new ImageViewEmoji(emojiView.getContext());
            } else if (i != 1) {
                view = new View(EmojiView.this.getContext());
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
            } else {
                view = new StickerSetNameCell(EmojiView.this.getContext(), true);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            String str2;
            String str3;
            String str4;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                ImageViewEmoji imageViewEmoji = (ImageViewEmoji) viewHolder.itemView;
                if (EmojiView.this.needEmojiSearch) {
                    i--;
                }
                int size = Emoji.recentEmoji.size();
                if (i < size) {
                    str = Emoji.recentEmoji.get(i);
                    str2 = str;
                    z = true;
                } else {
                    int i2 = size;
                    int i3 = 0;
                    while (true) {
                        String[][] strArr = EmojiData.dataColored;
                        if (i3 >= strArr.length) {
                            str3 = null;
                            break;
                        }
                        int length = strArr[i3].length + 1 + i2;
                        if (i < length) {
                            str3 = strArr[i3][(i - i2) - 1];
                            String str5 = Emoji.emojiColor.get(str3);
                            if (str5 != null) {
                                str4 = EmojiView.addColorToCode(str3, str5);
                            }
                        } else {
                            i3++;
                            i2 = length;
                        }
                    }
                    str4 = str3;
                    String str6 = str4;
                    str2 = str3;
                    str = str6;
                }
                imageViewEmoji.setImageDrawable(Emoji.getEmojiBigDrawable(str), z);
                imageViewEmoji.setTag(str2);
                imageViewEmoji.setContentDescription(str);
            } else if (itemViewType == 1) {
                ((StickerSetNameCell) viewHolder.itemView).setText(EmojiView.this.emojiTitles[this.positionToSection.get(i)], 0);
            }
        }

        public int getItemViewType(int i) {
            if (!EmojiView.this.needEmojiSearch || i != 0) {
                return this.positionToSection.indexOfKey(i) >= 0 ? 1 : 0;
            }
            return 2;
        }

        public void notifyDataSetChanged() {
            this.positionToSection.clear();
            this.itemCount = Emoji.recentEmoji.size() + (EmojiView.this.needEmojiSearch ? 1 : 0);
            for (int i = 0; i < EmojiData.dataColored.length; i++) {
                this.positionToSection.put(this.itemCount, i);
                this.sectionToPosition.put(i, this.itemCount);
                this.itemCount += EmojiData.dataColored[i].length + 1;
            }
            EmojiView.this.updateEmojiTabs();
            super.notifyDataSetChanged();
        }
    }

    private class EmojiSearchAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public String lastSearchAlias;
        /* access modifiers changed from: private */
        public String lastSearchEmojiString;
        /* access modifiers changed from: private */
        public ArrayList<MediaDataController.KeywordResult> result;
        private Runnable searchRunnable;
        /* access modifiers changed from: private */
        public boolean searchWas;

        private EmojiSearchAdapter() {
            this.result = new ArrayList<>();
        }

        public int getItemCount() {
            int size;
            if (this.result.isEmpty() && !this.searchWas) {
                size = Emoji.recentEmoji.size();
            } else if (this.result.isEmpty()) {
                return 2;
            } else {
                size = this.result.size();
            }
            return size + 1;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            AnonymousClass1 r12;
            if (i == 0) {
                EmojiView emojiView = EmojiView.this;
                r12 = new ImageViewEmoji(emojiView.getContext());
            } else if (i != 1) {
                AnonymousClass1 r122 = new FrameLayout(EmojiView.this.getContext()) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        int i3;
                        View view = (View) EmojiView.this.getParent();
                        if (view != null) {
                            i3 = (int) (((float) view.getMeasuredHeight()) - EmojiView.this.getY());
                        } else {
                            i3 = AndroidUtilities.dp(120.0f);
                        }
                        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(i3 - EmojiView.this.searchFieldHeight, NUM));
                    }
                };
                TextView textView = new TextView(EmojiView.this.getContext());
                textView.setText(LocaleController.getString("NoEmojiFound", NUM));
                textView.setTextSize(1, 16.0f);
                textView.setTextColor(Theme.getColor("chat_emojiPanelEmptyText"));
                r122.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 10.0f, 0.0f, 0.0f));
                ImageView imageView = new ImageView(EmojiView.this.getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                imageView.setImageResource(NUM);
                imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
                r122.addView(imageView, LayoutHelper.createFrame(48, 48, 85));
                imageView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        final boolean[] zArr = new boolean[1];
                        final BottomSheet.Builder builder = new BottomSheet.Builder(EmojiView.this.getContext());
                        LinearLayout linearLayout = new LinearLayout(EmojiView.this.getContext());
                        linearLayout.setOrientation(1);
                        linearLayout.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                        ImageView imageView = new ImageView(EmojiView.this.getContext());
                        imageView.setImageResource(NUM);
                        linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 15, 0, 0));
                        TextView textView = new TextView(EmojiView.this.getContext());
                        textView.setText(LocaleController.getString("EmojiSuggestions", NUM));
                        textView.setTextSize(1, 15.0f);
                        textView.setTextColor(Theme.getColor("dialogTextBlue2"));
                        int i = 5;
                        textView.setGravity(LocaleController.isRTL ? 5 : 3);
                        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 51, 0, 24, 0, 0));
                        TextView textView2 = new TextView(EmojiView.this.getContext());
                        textView2.setText(AndroidUtilities.replaceTags(LocaleController.getString("EmojiSuggestionsInfo", NUM)));
                        textView2.setTextSize(1, 15.0f);
                        textView2.setTextColor(Theme.getColor("dialogTextBlack"));
                        textView2.setGravity(LocaleController.isRTL ? 5 : 3);
                        linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 51, 0, 11, 0, 0));
                        TextView textView3 = new TextView(EmojiView.this.getContext());
                        Object[] objArr = new Object[1];
                        objArr[0] = EmojiSearchAdapter.this.lastSearchAlias != null ? EmojiSearchAdapter.this.lastSearchAlias : EmojiView.this.lastSearchKeyboardLanguage;
                        textView3.setText(LocaleController.formatString("EmojiSuggestionsUrl", NUM, objArr));
                        textView3.setTextSize(1, 15.0f);
                        textView3.setTextColor(Theme.getColor("dialogTextLink"));
                        if (!LocaleController.isRTL) {
                            i = 3;
                        }
                        textView3.setGravity(i);
                        linearLayout.addView(textView3, LayoutHelper.createLinear(-2, -2, 51, 0, 18, 0, 16));
                        textView3.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                boolean[] zArr = zArr;
                                if (!zArr[0]) {
                                    zArr[0] = true;
                                    AlertDialog[] alertDialogArr = {new AlertDialog(EmojiView.this.getContext(), 3)};
                                    TLRPC.TL_messages_getEmojiURL tL_messages_getEmojiURL = new TLRPC.TL_messages_getEmojiURL();
                                    tL_messages_getEmojiURL.lang_code = EmojiSearchAdapter.this.lastSearchAlias != null ? EmojiSearchAdapter.this.lastSearchAlias : EmojiView.this.lastSearchKeyboardLanguage[0];
                                    AndroidUtilities.runOnUIThread(
                                    /*  JADX ERROR: Method code generation error
                                        jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0065: INVOKE  
                                          (wrap: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$TwKjymEFVh1Ja7xLl-9KmhIF6eg : 0x0060: CONSTRUCTOR  (r1v4 org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$TwKjymEFVh1Ja7xLl-9KmhIF6eg) = 
                                          (r4v0 'this' org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 A[THIS])
                                          (r5v2 'alertDialogArr' org.telegram.ui.ActionBar.AlertDialog[])
                                          (wrap: int : 0x005a: INVOKE  (r0v7 int) = 
                                          (wrap: org.telegram.tgnet.ConnectionsManager : 0x004f: INVOKE  (r0v6 org.telegram.tgnet.ConnectionsManager) = 
                                          (wrap: int : 0x004b: INVOKE  (r0v5 int) = 
                                          (wrap: org.telegram.ui.Components.EmojiView : 0x0049: IGET  (r0v4 org.telegram.ui.Components.EmojiView) = 
                                          (wrap: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter : 0x0047: IGET  (r0v3 org.telegram.ui.Components.EmojiView$EmojiSearchAdapter) = 
                                          (wrap: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2 : 0x0045: IGET  (r0v2 org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2) = 
                                          (r4v0 'this' org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 A[THIS])
                                         org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.1.this$2 org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2)
                                         org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.this$1 org.telegram.ui.Components.EmojiView$EmojiSearchAdapter)
                                         org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.this$0 org.telegram.ui.Components.EmojiView)
                                         org.telegram.ui.Components.EmojiView.access$600(org.telegram.ui.Components.EmojiView):int type: STATIC)
                                         org.telegram.tgnet.ConnectionsManager.getInstance(int):org.telegram.tgnet.ConnectionsManager type: STATIC)
                                          (r1v3 'tL_messages_getEmojiURL' org.telegram.tgnet.TLRPC$TL_messages_getEmojiURL)
                                          (wrap: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$vESTLZoOoKr-laMmjTYW6gGhW9U : 0x0057: CONSTRUCTOR  (r3v1 org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$vESTLZoOoKr-laMmjTYW6gGhW9U) = 
                                          (r4v0 'this' org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 A[THIS])
                                          (r5v2 'alertDialogArr' org.telegram.ui.ActionBar.AlertDialog[])
                                          (wrap: org.telegram.ui.ActionBar.BottomSheet$Builder : 0x0053: IGET  (r2v7 org.telegram.ui.ActionBar.BottomSheet$Builder) = 
                                          (r4v0 'this' org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 A[THIS])
                                         org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.1.val$builder org.telegram.ui.ActionBar.BottomSheet$Builder)
                                         call: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$vESTLZoOoKr-laMmjTYW6gGhW9U.<init>(org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1, org.telegram.ui.ActionBar.AlertDialog[], org.telegram.ui.ActionBar.BottomSheet$Builder):void type: CONSTRUCTOR)
                                         org.telegram.tgnet.ConnectionsManager.sendRequest(org.telegram.tgnet.TLObject, org.telegram.tgnet.RequestDelegate):int type: VIRTUAL)
                                         call: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$TwKjymEFVh1Ja7xLl-9KmhIF6eg.<init>(org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1, org.telegram.ui.ActionBar.AlertDialog[], int):void type: CONSTRUCTOR)
                                          (1000 long)
                                         org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable, long):void type: STATIC in method: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.1.onClick(android.view.View):void, dex: classes.dex
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                        	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0060: CONSTRUCTOR  (r1v4 org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$TwKjymEFVh1Ja7xLl-9KmhIF6eg) = 
                                          (r4v0 'this' org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 A[THIS])
                                          (r5v2 'alertDialogArr' org.telegram.ui.ActionBar.AlertDialog[])
                                          (wrap: int : 0x005a: INVOKE  (r0v7 int) = 
                                          (wrap: org.telegram.tgnet.ConnectionsManager : 0x004f: INVOKE  (r0v6 org.telegram.tgnet.ConnectionsManager) = 
                                          (wrap: int : 0x004b: INVOKE  (r0v5 int) = 
                                          (wrap: org.telegram.ui.Components.EmojiView : 0x0049: IGET  (r0v4 org.telegram.ui.Components.EmojiView) = 
                                          (wrap: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter : 0x0047: IGET  (r0v3 org.telegram.ui.Components.EmojiView$EmojiSearchAdapter) = 
                                          (wrap: org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2 : 0x0045: IGET  (r0v2 org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2) = 
                                          (r4v0 'this' org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 A[THIS])
                                         org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.1.this$2 org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2)
                                         org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.this$1 org.telegram.ui.Components.EmojiView$EmojiSearchAdapter)
                                         org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.this$0 org.telegram.ui.Components.EmojiView)
                                         org.telegram.ui.Components.EmojiView.access$600(org.telegram.ui.Components.EmojiView):int type: STATIC)
                                         org.telegram.tgnet.ConnectionsManager.getInstance(int):org.telegram.tgnet.ConnectionsManager type: STATIC)
                                          (r1v3 'tL_messages_getEmojiURL' org.telegram.tgnet.TLRPC$TL_messages_getEmojiURL)
                                          (wrap: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$vESTLZoOoKr-laMmjTYW6gGhW9U : 0x0057: CONSTRUCTOR  (r3v1 org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$vESTLZoOoKr-laMmjTYW6gGhW9U) = 
                                          (r4v0 'this' org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 A[THIS])
                                          (r5v2 'alertDialogArr' org.telegram.ui.ActionBar.AlertDialog[])
                                          (wrap: org.telegram.ui.ActionBar.BottomSheet$Builder : 0x0053: IGET  (r2v7 org.telegram.ui.ActionBar.BottomSheet$Builder) = 
                                          (r4v0 'this' org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 A[THIS])
                                         org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.1.val$builder org.telegram.ui.ActionBar.BottomSheet$Builder)
                                         call: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$vESTLZoOoKr-laMmjTYW6gGhW9U.<init>(org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1, org.telegram.ui.ActionBar.AlertDialog[], org.telegram.ui.ActionBar.BottomSheet$Builder):void type: CONSTRUCTOR)
                                         org.telegram.tgnet.ConnectionsManager.sendRequest(org.telegram.tgnet.TLObject, org.telegram.tgnet.RequestDelegate):int type: VIRTUAL)
                                         call: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$TwKjymEFVh1Ja7xLl-9KmhIF6eg.<init>(org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1, org.telegram.ui.ActionBar.AlertDialog[], int):void type: CONSTRUCTOR in method: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.1.onClick(android.view.View):void, dex: classes.dex
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                        	... 122 more
                                        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$TwKjymEFVh1Ja7xLl-9KmhIF6eg, state: NOT_LOADED
                                        	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                        	... 128 more
                                        */
                                    /*
                                        this = this;
                                        boolean[] r5 = r2
                                        r0 = 0
                                        boolean r1 = r5[r0]
                                        if (r1 == 0) goto L_0x0008
                                        return
                                    L_0x0008:
                                        r1 = 1
                                        r5[r0] = r1
                                        org.telegram.ui.ActionBar.AlertDialog[] r5 = new org.telegram.ui.ActionBar.AlertDialog[r1]
                                        org.telegram.ui.ActionBar.AlertDialog r1 = new org.telegram.ui.ActionBar.AlertDialog
                                        org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2 r2 = org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.AnonymousClass2.this
                                        org.telegram.ui.Components.EmojiView$EmojiSearchAdapter r2 = org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.this
                                        org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                                        android.content.Context r2 = r2.getContext()
                                        r3 = 3
                                        r1.<init>(r2, r3)
                                        r5[r0] = r1
                                        org.telegram.tgnet.TLRPC$TL_messages_getEmojiURL r1 = new org.telegram.tgnet.TLRPC$TL_messages_getEmojiURL
                                        r1.<init>()
                                        org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2 r2 = org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.AnonymousClass2.this
                                        org.telegram.ui.Components.EmojiView$EmojiSearchAdapter r2 = org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.this
                                        java.lang.String r2 = r2.lastSearchAlias
                                        if (r2 == 0) goto L_0x0037
                                        org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2 r0 = org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.AnonymousClass2.this
                                        org.telegram.ui.Components.EmojiView$EmojiSearchAdapter r0 = org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.this
                                        java.lang.String r0 = r0.lastSearchAlias
                                        goto L_0x0043
                                    L_0x0037:
                                        org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2 r2 = org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.AnonymousClass2.this
                                        org.telegram.ui.Components.EmojiView$EmojiSearchAdapter r2 = org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.this
                                        org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                                        java.lang.String[] r2 = r2.lastSearchKeyboardLanguage
                                        r0 = r2[r0]
                                    L_0x0043:
                                        r1.lang_code = r0
                                        org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2 r0 = org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.AnonymousClass2.this
                                        org.telegram.ui.Components.EmojiView$EmojiSearchAdapter r0 = org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.this
                                        org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                                        int r0 = r0.currentAccount
                                        org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
                                        org.telegram.ui.ActionBar.BottomSheet$Builder r2 = r3
                                        org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$vESTLZoOoKr-laMmjTYW6gGhW9U r3 = new org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$vESTLZoOoKr-laMmjTYW6gGhW9U
                                        r3.<init>(r4, r5, r2)
                                        int r0 = r0.sendRequest(r1, r3)
                                        org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$TwKjymEFVh1Ja7xLl-9KmhIF6eg r1 = new org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$TwKjymEFVh1Ja7xLl-9KmhIF6eg
                                        r1.<init>(r4, r5, r0)
                                        r2 = 1000(0x3e8, double:4.94E-321)
                                        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r2)
                                        return
                                    */
                                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.onClick(android.view.View):void");
                                }

                                public /* synthetic */ void lambda$onClick$1$EmojiView$EmojiSearchAdapter$2$1(AlertDialog[] alertDialogArr, BottomSheet.Builder builder, TLObject tLObject, TLRPC.TL_error tL_error) {
                                    AndroidUtilities.runOnUIThread(
                                    /*  JADX ERROR: Method code generation error
                                        jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0005: INVOKE  
                                          (wrap: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$k1LSrqBP6ptq3f6NASRqH9-nzpY : 0x0002: CONSTRUCTOR  (r4v1 org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$k1LSrqBP6ptq3f6NASRqH9-nzpY) = 
                                          (r0v0 'this' org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 A[THIS])
                                          (r1v0 'alertDialogArr' org.telegram.ui.ActionBar.AlertDialog[])
                                          (r3v0 'tLObject' org.telegram.tgnet.TLObject)
                                          (r2v0 'builder' org.telegram.ui.ActionBar.BottomSheet$Builder)
                                         call: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$k1LSrqBP6ptq3f6NASRqH9-nzpY.<init>(org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1, org.telegram.ui.ActionBar.AlertDialog[], org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.BottomSheet$Builder):void type: CONSTRUCTOR)
                                         org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.1.lambda$onClick$1$EmojiView$EmojiSearchAdapter$2$1(org.telegram.ui.ActionBar.AlertDialog[], org.telegram.ui.ActionBar.BottomSheet$Builder, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void, dex: classes.dex
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                        	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                        	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0002: CONSTRUCTOR  (r4v1 org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$k1LSrqBP6ptq3f6NASRqH9-nzpY) = 
                                          (r0v0 'this' org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 A[THIS])
                                          (r1v0 'alertDialogArr' org.telegram.ui.ActionBar.AlertDialog[])
                                          (r3v0 'tLObject' org.telegram.tgnet.TLObject)
                                          (r2v0 'builder' org.telegram.ui.ActionBar.BottomSheet$Builder)
                                         call: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$k1LSrqBP6ptq3f6NASRqH9-nzpY.<init>(org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1, org.telegram.ui.ActionBar.AlertDialog[], org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.BottomSheet$Builder):void type: CONSTRUCTOR in method: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.1.lambda$onClick$1$EmojiView$EmojiSearchAdapter$2$1(org.telegram.ui.ActionBar.AlertDialog[], org.telegram.ui.ActionBar.BottomSheet$Builder, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void, dex: classes.dex
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                        	... 115 more
                                        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$k1LSrqBP6ptq3f6NASRqH9-nzpY, state: NOT_LOADED
                                        	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                        	... 121 more
                                        */
                                    /*
                                        this = this;
                                        org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$k1LSrqBP6ptq3f6NASRqH9-nzpY r4 = new org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$k1LSrqBP6ptq3f6NASRqH9-nzpY
                                        r4.<init>(r0, r1, r3, r2)
                                        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
                                        return
                                    */
                                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.lambda$onClick$1$EmojiView$EmojiSearchAdapter$2$1(org.telegram.ui.ActionBar.AlertDialog[], org.telegram.ui.ActionBar.BottomSheet$Builder, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
                                }

                                public /* synthetic */ void lambda$null$0$EmojiView$EmojiSearchAdapter$2$1(AlertDialog[] alertDialogArr, TLObject tLObject, BottomSheet.Builder builder) {
                                    try {
                                        alertDialogArr[0].dismiss();
                                    } catch (Throwable unused) {
                                    }
                                    alertDialogArr[0] = null;
                                    if (tLObject instanceof TLRPC.TL_emojiURL) {
                                        Browser.openUrl(EmojiView.this.getContext(), ((TLRPC.TL_emojiURL) tLObject).url);
                                        builder.getDismissRunnable().run();
                                    }
                                }

                                public /* synthetic */ void lambda$onClick$3$EmojiView$EmojiSearchAdapter$2$1(AlertDialog[] alertDialogArr, int i) {
                                    if (alertDialogArr[0] != null) {
                                        alertDialogArr[0].setOnCancelListener(
                                        /*  JADX ERROR: Method code generation error
                                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000d: INVOKE  
                                              (wrap: org.telegram.ui.ActionBar.AlertDialog : 0x0006: AGET  (r1v1 org.telegram.ui.ActionBar.AlertDialog) = 
                                              (r4v0 'alertDialogArr' org.telegram.ui.ActionBar.AlertDialog[])
                                              (0 ?[int, short, byte, char])
                                            )
                                              (wrap: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$BvShOFTdaqiZ1q6_dGuWuV6cZ2o : 0x000a: CONSTRUCTOR  (r2v0 org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$BvShOFTdaqiZ1q6_dGuWuV6cZ2o) = 
                                              (r3v0 'this' org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 A[THIS])
                                              (r5v0 'i' int)
                                             call: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$BvShOFTdaqiZ1q6_dGuWuV6cZ2o.<init>(org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1, int):void type: CONSTRUCTOR)
                                             org.telegram.ui.ActionBar.AlertDialog.setOnCancelListener(android.content.DialogInterface$OnCancelListener):void type: VIRTUAL in method: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.1.lambda$onClick$3$EmojiView$EmojiSearchAdapter$2$1(org.telegram.ui.ActionBar.AlertDialog[], int):void, dex: classes.dex
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                            	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                            	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                            	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                            	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                            	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000a: CONSTRUCTOR  (r2v0 org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$BvShOFTdaqiZ1q6_dGuWuV6cZ2o) = 
                                              (r3v0 'this' org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 A[THIS])
                                              (r5v0 'i' int)
                                             call: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$BvShOFTdaqiZ1q6_dGuWuV6cZ2o.<init>(org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1, int):void type: CONSTRUCTOR in method: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.1.lambda$onClick$3$EmojiView$EmojiSearchAdapter$2$1(org.telegram.ui.ActionBar.AlertDialog[], int):void, dex: classes.dex
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                            	... 122 more
                                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$BvShOFTdaqiZ1q6_dGuWuV6cZ2o, state: NOT_LOADED
                                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                            	... 128 more
                                            */
                                        /*
                                            this = this;
                                            r0 = 0
                                            r1 = r4[r0]
                                            if (r1 != 0) goto L_0x0006
                                            return
                                        L_0x0006:
                                            r1 = r4[r0]
                                            org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$BvShOFTdaqiZ1q6_dGuWuV6cZ2o r2 = new org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$BvShOFTdaqiZ1q6_dGuWuV6cZ2o
                                            r2.<init>(r3, r5)
                                            r1.setOnCancelListener(r2)
                                            r4 = r4[r0]
                                            r4.show()
                                            return
                                        */
                                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.lambda$onClick$3$EmojiView$EmojiSearchAdapter$2$1(org.telegram.ui.ActionBar.AlertDialog[], int):void");
                                    }

                                    public /* synthetic */ void lambda$null$2$EmojiView$EmojiSearchAdapter$2$1(int i, DialogInterface dialogInterface) {
                                        ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(i, true);
                                    }
                                });
                                builder.setCustomView(linearLayout);
                                builder.show();
                            }
                        });
                        r122.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        r12 = r122;
                    } else {
                        View view = new View(EmojiView.this.getContext());
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                        r12 = view;
                    }
                    return new RecyclerListView.Holder(r12);
                }

                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                    String str;
                    boolean z;
                    if (viewHolder.getItemViewType() == 0) {
                        ImageViewEmoji imageViewEmoji = (ImageViewEmoji) viewHolder.itemView;
                        int i2 = i - 1;
                        if (!this.result.isEmpty() || this.searchWas) {
                            str = this.result.get(i2).emoji;
                            z = false;
                        } else {
                            str = Emoji.recentEmoji.get(i2);
                            z = true;
                        }
                        imageViewEmoji.setImageDrawable(Emoji.getEmojiBigDrawable(str), z);
                        imageViewEmoji.setTag(str);
                    }
                }

                public int getItemViewType(int i) {
                    if (i == 0) {
                        return 1;
                    }
                    return (i != 1 || !this.searchWas || !this.result.isEmpty()) ? 0 : 2;
                }

                public void search(String str) {
                    if (TextUtils.isEmpty(str)) {
                        this.lastSearchEmojiString = null;
                        if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiAdapter) {
                            EmojiView.this.emojiGridView.setAdapter(EmojiView.this.emojiAdapter);
                            this.searchWas = false;
                        }
                        notifyDataSetChanged();
                    } else {
                        this.lastSearchEmojiString = str.toLowerCase();
                    }
                    Runnable runnable = this.searchRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                    }
                    if (!TextUtils.isEmpty(this.lastSearchEmojiString)) {
                        AnonymousClass3 r3 = new Runnable() {
                            public void run() {
                                EmojiView.this.emojiSearchField.progressDrawable.startAnimation();
                                final String access$10300 = EmojiSearchAdapter.this.lastSearchEmojiString;
                                String[] currentKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                                if (!Arrays.equals(EmojiView.this.lastSearchKeyboardLanguage, currentKeyboardLanguage)) {
                                    MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(currentKeyboardLanguage);
                                }
                                String[] unused = EmojiView.this.lastSearchKeyboardLanguage = currentKeyboardLanguage;
                                MediaDataController.getInstance(EmojiView.this.currentAccount).getEmojiSuggestions(EmojiView.this.lastSearchKeyboardLanguage, EmojiSearchAdapter.this.lastSearchEmojiString, false, new MediaDataController.KeywordResultCallback() {
                                    public void run(ArrayList<MediaDataController.KeywordResult> arrayList, String str) {
                                        if (access$10300.equals(EmojiSearchAdapter.this.lastSearchEmojiString)) {
                                            String unused = EmojiSearchAdapter.this.lastSearchAlias = str;
                                            EmojiView.this.emojiSearchField.progressDrawable.stopAnimation();
                                            boolean unused2 = EmojiSearchAdapter.this.searchWas = true;
                                            if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiSearchAdapter) {
                                                EmojiView.this.emojiGridView.setAdapter(EmojiView.this.emojiSearchAdapter);
                                            }
                                            ArrayList unused3 = EmojiSearchAdapter.this.result = arrayList;
                                            EmojiSearchAdapter.this.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        };
                        this.searchRunnable = r3;
                        AndroidUtilities.runOnUIThread(r3, 300);
                    }
                }
            }

            private class EmojiPagesAdapter extends PagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
                public boolean isViewFromObject(View view, Object obj) {
                    return view == obj;
                }

                private EmojiPagesAdapter() {
                }

                public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
                    viewGroup.removeView((View) EmojiView.this.views.get(i));
                }

                public boolean canScrollToTab(int i) {
                    boolean z = true;
                    if ((i != 1 && i != 2) || EmojiView.this.currentChatId == 0) {
                        return true;
                    }
                    EmojiView emojiView = EmojiView.this;
                    if (i != 1) {
                        z = false;
                    }
                    emojiView.showStickerBanHint(z);
                    return false;
                }

                public int getCount() {
                    return EmojiView.this.views.size();
                }

                public Drawable getPageIconDrawable(int i) {
                    return EmojiView.this.tabIcons[i];
                }

                public CharSequence getPageTitle(int i) {
                    if (i == 0) {
                        return LocaleController.getString("Emoji", NUM);
                    }
                    if (i == 1) {
                        return LocaleController.getString("AccDescrGIFs", NUM);
                    }
                    if (i != 2) {
                        return null;
                    }
                    return LocaleController.getString("AccDescrStickers", NUM);
                }

                public void customOnDraw(Canvas canvas, int i) {
                    if (i == 2 && !MediaDataController.getInstance(EmojiView.this.currentAccount).getUnreadStickerSets().isEmpty() && EmojiView.this.dotPaint != null) {
                        canvas.drawCircle((float) ((canvas.getWidth() / 2) + AndroidUtilities.dp(9.0f)), (float) ((canvas.getHeight() / 2) - AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(5.0f), EmojiView.this.dotPaint);
                    }
                }

                public Object instantiateItem(ViewGroup viewGroup, int i) {
                    View view = (View) EmojiView.this.views.get(i);
                    viewGroup.addView(view);
                    return view;
                }

                public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
                    if (dataSetObserver != null) {
                        super.unregisterDataSetObserver(dataSetObserver);
                    }
                }
            }

            private class GifAdapter extends RecyclerListView.SelectionAdapter {
                private Context mContext;

                public long getItemId(int i) {
                    return (long) i;
                }

                public int getItemViewType(int i) {
                    return i == 0 ? 1 : 0;
                }

                public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                    return false;
                }

                public GifAdapter(Context context) {
                    this.mContext = context;
                }

                public int getItemCount() {
                    return EmojiView.this.recentGifs.size() + 1;
                }

                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    ContextLinkCell contextLinkCell;
                    if (i != 0) {
                        View view = new View(EmojiView.this.getContext());
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                        contextLinkCell = view;
                    } else {
                        ContextLinkCell contextLinkCell2 = new ContextLinkCell(this.mContext);
                        contextLinkCell2.setContentDescription(LocaleController.getString("AttachGif", NUM));
                        contextLinkCell2.setCanPreviewGif(true);
                        contextLinkCell = contextLinkCell2;
                    }
                    return new RecyclerListView.Holder(contextLinkCell);
                }

                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                    TLRPC.Document document;
                    if (viewHolder.getItemViewType() == 0 && (document = (TLRPC.Document) EmojiView.this.recentGifs.get(i - 1)) != null) {
                        ((ContextLinkCell) viewHolder.itemView).setGif(document, false);
                    }
                }

                public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
                    View view = viewHolder.itemView;
                    if (view instanceof ContextLinkCell) {
                        ImageReceiver photoImage = ((ContextLinkCell) view).getPhotoImage();
                        if (EmojiView.this.pager.getCurrentItem() == 1) {
                            photoImage.setAllowStartAnimation(true);
                            photoImage.startAnimation();
                            return;
                        }
                        photoImage.setAllowStartAnimation(false);
                        photoImage.stopAnimation();
                    }
                }
            }

            private class GifSearchAdapter extends RecyclerListView.SelectionAdapter {
                /* access modifiers changed from: private */
                public TLRPC.User bot;
                /* access modifiers changed from: private */
                public String lastSearchImageString;
                private Context mContext;
                /* access modifiers changed from: private */
                public String nextSearchOffset;
                /* access modifiers changed from: private */
                public int reqId;
                /* access modifiers changed from: private */
                public ArrayList<TLRPC.BotInlineResult> results = new ArrayList<>();
                private HashMap<String, TLRPC.BotInlineResult> resultsMap = new HashMap<>();
                /* access modifiers changed from: private */
                public boolean searchEndReached;
                private Runnable searchRunnable;
                private boolean searchingUser;

                public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                    return false;
                }

                public GifSearchAdapter(Context context) {
                    this.mContext = context;
                }

                public int getItemCount() {
                    return (this.results.isEmpty() ? 1 : this.results.size()) + 1;
                }

                public int getItemViewType(int i) {
                    if (i == 0) {
                        return 1;
                    }
                    return this.results.isEmpty() ? 2 : 0;
                }

                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    AnonymousClass1 r15;
                    if (i == 0) {
                        ContextLinkCell contextLinkCell = new ContextLinkCell(this.mContext);
                        contextLinkCell.setContentDescription(LocaleController.getString("AttachGif", NUM));
                        contextLinkCell.setCanPreviewGif(true);
                        r15 = contextLinkCell;
                    } else if (i != 1) {
                        AnonymousClass1 r152 = new FrameLayout(EmojiView.this.getContext()) {
                            /* access modifiers changed from: protected */
                            public void onMeasure(int i, int i2) {
                                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec((int) (((float) (((EmojiView.this.gifGridView.getMeasuredHeight() - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3)) * 1.7f), NUM));
                            }
                        };
                        ImageView imageView = new ImageView(EmojiView.this.getContext());
                        imageView.setScaleType(ImageView.ScaleType.CENTER);
                        imageView.setImageResource(NUM);
                        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
                        r152.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 59.0f));
                        TextView textView = new TextView(EmojiView.this.getContext());
                        textView.setText(LocaleController.getString("NoGIFsFound", NUM));
                        textView.setTextSize(1, 16.0f);
                        textView.setTextColor(Theme.getColor("chat_emojiPanelEmptyText"));
                        r152.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 9.0f));
                        r152.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        r15 = r152;
                    } else {
                        View view = new View(EmojiView.this.getContext());
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                        r15 = view;
                    }
                    return new RecyclerListView.Holder(r15);
                }

                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                    if (viewHolder.getItemViewType() == 0) {
                        ((ContextLinkCell) viewHolder.itemView).setLink(this.results.get(i - 1), true, false, false);
                    }
                }

                public void search(final String str) {
                    if (this.reqId != 0) {
                        ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                        this.reqId = 0;
                    }
                    if (TextUtils.isEmpty(str)) {
                        this.lastSearchImageString = null;
                        if (EmojiView.this.gifGridView.getAdapter() != EmojiView.this.gifAdapter) {
                            EmojiView.this.gifGridView.setAdapter(EmojiView.this.gifAdapter);
                        }
                        notifyDataSetChanged();
                    } else {
                        this.lastSearchImageString = str.toLowerCase();
                    }
                    Runnable runnable = this.searchRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                    }
                    if (!TextUtils.isEmpty(this.lastSearchImageString)) {
                        AnonymousClass2 r0 = new Runnable() {
                            public void run() {
                                GifSearchAdapter.this.search(str, "", true);
                            }
                        };
                        this.searchRunnable = r0;
                        AndroidUtilities.runOnUIThread(r0, 300);
                    }
                }

                private void searchBotUser() {
                    if (!this.searchingUser) {
                        this.searchingUser = true;
                        TLRPC.TL_contacts_resolveUsername tL_contacts_resolveUsername = new TLRPC.TL_contacts_resolveUsername();
                        tL_contacts_resolveUsername.username = MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot;
                        ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tL_contacts_resolveUsername, new RequestDelegate() {
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                EmojiView.GifSearchAdapter.this.lambda$searchBotUser$1$EmojiView$GifSearchAdapter(tLObject, tL_error);
                            }
                        });
                    }
                }

                public /* synthetic */ void lambda$searchBotUser$1$EmojiView$GifSearchAdapter(TLObject tLObject, TLRPC.TL_error tL_error) {
                    if (tLObject != null) {
                        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
                            private final /* synthetic */ TLObject f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                EmojiView.GifSearchAdapter.this.lambda$null$0$EmojiView$GifSearchAdapter(this.f$1);
                            }
                        });
                    }
                }

                public /* synthetic */ void lambda$null$0$EmojiView$GifSearchAdapter(TLObject tLObject) {
                    TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer) tLObject;
                    MessagesController.getInstance(EmojiView.this.currentAccount).putUsers(tL_contacts_resolvedPeer.users, false);
                    MessagesController.getInstance(EmojiView.this.currentAccount).putChats(tL_contacts_resolvedPeer.chats, false);
                    MessagesStorage.getInstance(EmojiView.this.currentAccount).putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, true, true);
                    String str = this.lastSearchImageString;
                    this.lastSearchImageString = null;
                    search(str, "", false);
                }

                /* access modifiers changed from: private */
                public void search(String str, String str2, boolean z) {
                    if (this.reqId != 0) {
                        ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                        this.reqId = 0;
                    }
                    this.lastSearchImageString = str;
                    TLObject userOrChat = MessagesController.getInstance(EmojiView.this.currentAccount).getUserOrChat(MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot);
                    if (userOrChat instanceof TLRPC.User) {
                        if (TextUtils.isEmpty(str2)) {
                            EmojiView.this.gifSearchField.progressDrawable.startAnimation();
                        }
                        this.bot = (TLRPC.User) userOrChat;
                        TLRPC.TL_messages_getInlineBotResults tL_messages_getInlineBotResults = new TLRPC.TL_messages_getInlineBotResults();
                        if (str == null) {
                            str = "";
                        }
                        tL_messages_getInlineBotResults.query = str;
                        tL_messages_getInlineBotResults.bot = MessagesController.getInstance(EmojiView.this.currentAccount).getInputUser(this.bot);
                        tL_messages_getInlineBotResults.offset = str2;
                        tL_messages_getInlineBotResults.peer = new TLRPC.TL_inputPeerEmpty();
                        this.reqId = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tL_messages_getInlineBotResults, new RequestDelegate(tL_messages_getInlineBotResults, str2) {
                            private final /* synthetic */ TLRPC.TL_messages_getInlineBotResults f$1;
                            private final /* synthetic */ String f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                EmojiView.GifSearchAdapter.this.lambda$search$3$EmojiView$GifSearchAdapter(this.f$1, this.f$2, tLObject, tL_error);
                            }
                        }, 2);
                    } else if (z) {
                        searchBotUser();
                        EmojiView.this.gifSearchField.progressDrawable.startAnimation();
                    }
                }

                public /* synthetic */ void lambda$search$3$EmojiView$GifSearchAdapter(TLRPC.TL_messages_getInlineBotResults tL_messages_getInlineBotResults, String str, TLObject tLObject, TLRPC.TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(tL_messages_getInlineBotResults, str, tLObject) {
                        private final /* synthetic */ TLRPC.TL_messages_getInlineBotResults f$1;
                        private final /* synthetic */ String f$2;
                        private final /* synthetic */ TLObject f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                        }

                        public final void run() {
                            EmojiView.GifSearchAdapter.this.lambda$null$2$EmojiView$GifSearchAdapter(this.f$1, this.f$2, this.f$3);
                        }
                    });
                }

                public /* synthetic */ void lambda$null$2$EmojiView$GifSearchAdapter(TLRPC.TL_messages_getInlineBotResults tL_messages_getInlineBotResults, String str, TLObject tLObject) {
                    if (tL_messages_getInlineBotResults.query.equals(this.lastSearchImageString)) {
                        if (EmojiView.this.gifGridView.getAdapter() != EmojiView.this.gifSearchAdapter) {
                            EmojiView.this.gifGridView.setAdapter(EmojiView.this.gifSearchAdapter);
                        }
                        if (TextUtils.isEmpty(str)) {
                            this.results.clear();
                            this.resultsMap.clear();
                            EmojiView.this.gifSearchField.progressDrawable.stopAnimation();
                        }
                        boolean z = false;
                        this.reqId = 0;
                        if (tLObject instanceof TLRPC.messages_BotResults) {
                            int size = this.results.size();
                            TLRPC.messages_BotResults messages_botresults = (TLRPC.messages_BotResults) tLObject;
                            this.nextSearchOffset = messages_botresults.next_offset;
                            int i = 0;
                            for (int i2 = 0; i2 < messages_botresults.results.size(); i2++) {
                                TLRPC.BotInlineResult botInlineResult = messages_botresults.results.get(i2);
                                if (!this.resultsMap.containsKey(botInlineResult.id)) {
                                    botInlineResult.query_id = messages_botresults.query_id;
                                    this.results.add(botInlineResult);
                                    this.resultsMap.put(botInlineResult.id, botInlineResult);
                                    i++;
                                }
                            }
                            if (size == this.results.size() || TextUtils.isEmpty(this.nextSearchOffset)) {
                                z = true;
                            }
                            this.searchEndReached = z;
                            if (i != 0) {
                                if (size != 0) {
                                    notifyItemChanged(size);
                                }
                                notifyItemRangeInserted(size + 1, i);
                            } else if (this.results.isEmpty()) {
                                notifyDataSetChanged();
                            }
                        } else {
                            notifyDataSetChanged();
                        }
                    }
                }

                public void notifyDataSetChanged() {
                    super.notifyDataSetChanged();
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
                public SparseArray<TLRPC.StickerSetCovered> positionsToSets = new SparseArray<>();
                /* access modifiers changed from: private */
                public int reqId;
                /* access modifiers changed from: private */
                public int reqId2;
                private SparseArray<Object> rowStartPack = new SparseArray<>();
                /* access modifiers changed from: private */
                public String searchQuery;
                private Runnable searchRunnable = new Runnable() {
                    /* access modifiers changed from: private */
                    public void clear() {
                        StickersSearchGridAdapter stickersSearchGridAdapter = StickersSearchGridAdapter.this;
                        if (!stickersSearchGridAdapter.cleared) {
                            stickersSearchGridAdapter.cleared = true;
                            stickersSearchGridAdapter.emojiStickers.clear();
                            StickersSearchGridAdapter.this.emojiArrays.clear();
                            StickersSearchGridAdapter.this.localPacks.clear();
                            StickersSearchGridAdapter.this.serverPacks.clear();
                            StickersSearchGridAdapter.this.localPacksByShortName.clear();
                            StickersSearchGridAdapter.this.localPacksByName.clear();
                        }
                    }

                    /* JADX WARNING: Code restructure failed: missing block: B:14:0x007c, code lost:
                        if (r8.charAt(r9) <= 57343) goto L_0x0098;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0096, code lost:
                        if (r8.charAt(r9) != 9794) goto L_0x00b3;
                     */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        /*
                            r13 = this;
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r0 = r0.searchQuery
                            boolean r0 = android.text.TextUtils.isEmpty(r0)
                            if (r0 == 0) goto L_0x000d
                            return
                        L_0x000d:
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.EmojiView$SearchField r0 = r0.stickersSearchField
                            org.telegram.ui.Components.CloseProgressDrawable2 r0 = r0.progressDrawable
                            r0.startAnimation()
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            r1 = 0
                            r0.cleared = r1
                            int r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.access$13504(r0)
                            java.util.ArrayList r2 = new java.util.ArrayList
                            r2.<init>(r1)
                            android.util.LongSparseArray r3 = new android.util.LongSparseArray
                            r3.<init>(r1)
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r4 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                            int r4 = r4.currentAccount
                            org.telegram.messenger.MediaDataController r4 = org.telegram.messenger.MediaDataController.getInstance(r4)
                            java.util.HashMap r4 = r4.getAllStickers()
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r5 = r5.searchQuery
                            int r5 = r5.length()
                            r6 = 14
                            r7 = 1
                            if (r5 > r6) goto L_0x0123
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r5 = r5.searchQuery
                            int r6 = r5.length()
                            r8 = r5
                            r5 = 0
                        L_0x005a:
                            if (r5 >= r6) goto L_0x00da
                            int r9 = r6 + -1
                            r10 = 2
                            if (r5 >= r9) goto L_0x00b3
                            char r9 = r8.charAt(r5)
                            r11 = 55356(0xd83c, float:7.757E-41)
                            if (r9 != r11) goto L_0x007e
                            int r9 = r5 + 1
                            char r11 = r8.charAt(r9)
                            r12 = 57339(0xdffb, float:8.0349E-41)
                            if (r11 < r12) goto L_0x007e
                            char r9 = r8.charAt(r9)
                            r11 = 57343(0xdfff, float:8.0355E-41)
                            if (r9 <= r11) goto L_0x0098
                        L_0x007e:
                            char r9 = r8.charAt(r5)
                            r11 = 8205(0x200d, float:1.1498E-41)
                            if (r9 != r11) goto L_0x00b3
                            int r9 = r5 + 1
                            char r11 = r8.charAt(r9)
                            r12 = 9792(0x2640, float:1.3722E-41)
                            if (r11 == r12) goto L_0x0098
                            char r9 = r8.charAt(r9)
                            r11 = 9794(0x2642, float:1.3724E-41)
                            if (r9 != r11) goto L_0x00b3
                        L_0x0098:
                            java.lang.CharSequence[] r9 = new java.lang.CharSequence[r10]
                            java.lang.CharSequence r10 = r8.subSequence(r1, r5)
                            r9[r1] = r10
                            int r10 = r5 + 2
                            int r11 = r8.length()
                            java.lang.CharSequence r8 = r8.subSequence(r10, r11)
                            r9[r7] = r8
                            java.lang.CharSequence r8 = android.text.TextUtils.concat(r9)
                            int r6 = r6 + -2
                            goto L_0x00d6
                        L_0x00b3:
                            char r9 = r8.charAt(r5)
                            r11 = 65039(0xfe0f, float:9.1139E-41)
                            if (r9 != r11) goto L_0x00d8
                            java.lang.CharSequence[] r9 = new java.lang.CharSequence[r10]
                            java.lang.CharSequence r10 = r8.subSequence(r1, r5)
                            r9[r1] = r10
                            int r10 = r5 + 1
                            int r11 = r8.length()
                            java.lang.CharSequence r8 = r8.subSequence(r10, r11)
                            r9[r7] = r8
                            java.lang.CharSequence r8 = android.text.TextUtils.concat(r9)
                            int r6 = r6 + -1
                        L_0x00d6:
                            int r5 = r5 + -1
                        L_0x00d8:
                            int r5 = r5 + r7
                            goto L_0x005a
                        L_0x00da:
                            if (r4 == 0) goto L_0x00e7
                            java.lang.String r5 = r8.toString()
                            java.lang.Object r5 = r4.get(r5)
                            java.util.ArrayList r5 = (java.util.ArrayList) r5
                            goto L_0x00e8
                        L_0x00e7:
                            r5 = 0
                        L_0x00e8:
                            if (r5 == 0) goto L_0x0123
                            boolean r6 = r5.isEmpty()
                            if (r6 != 0) goto L_0x0123
                            r13.clear()
                            r2.addAll(r5)
                            int r6 = r5.size()
                            r8 = 0
                        L_0x00fb:
                            if (r8 >= r6) goto L_0x010b
                            java.lang.Object r9 = r5.get(r8)
                            org.telegram.tgnet.TLRPC$Document r9 = (org.telegram.tgnet.TLRPC.Document) r9
                            long r10 = r9.id
                            r3.put(r10, r9)
                            int r8 = r8 + 1
                            goto L_0x00fb
                        L_0x010b:
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.HashMap r5 = r5.emojiStickers
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r6 = r6.searchQuery
                            r5.put(r2, r6)
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.ArrayList r5 = r5.emojiArrays
                            r5.add(r2)
                        L_0x0123:
                            if (r4 == 0) goto L_0x0181
                            boolean r5 = r4.isEmpty()
                            if (r5 != 0) goto L_0x0181
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r5 = r5.searchQuery
                            int r5 = r5.length()
                            if (r5 <= r7) goto L_0x0181
                            java.lang.String[] r5 = org.telegram.messenger.AndroidUtilities.getCurrentKeyboardLanguage()
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                            java.lang.String[] r6 = r6.lastSearchKeyboardLanguage
                            boolean r6 = java.util.Arrays.equals(r6, r5)
                            if (r6 != 0) goto L_0x0158
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                            int r6 = r6.currentAccount
                            org.telegram.messenger.MediaDataController r6 = org.telegram.messenger.MediaDataController.getInstance(r6)
                            r6.fetchNewEmojiKeywords(r5)
                        L_0x0158:
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                            java.lang.String[] unused = r6.lastSearchKeyboardLanguage = r5
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r5 = org.telegram.ui.Components.EmojiView.this
                            int r5 = r5.currentAccount
                            org.telegram.messenger.MediaDataController r5 = org.telegram.messenger.MediaDataController.getInstance(r5)
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                            java.lang.String[] r6 = r6.lastSearchKeyboardLanguage
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r8 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r8 = r8.searchQuery
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$1 r9 = new org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$1
                            r9.<init>(r0, r4)
                            r5.getEmojiSuggestions(r6, r8, r1, r9)
                        L_0x0181:
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                            int r0 = r0.currentAccount
                            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
                            java.util.ArrayList r0 = r0.getStickerSets(r1)
                            int r4 = r0.size()
                            r5 = 0
                        L_0x0196:
                            r6 = 32
                            if (r5 >= r4) goto L_0x0214
                            java.lang.Object r8 = r0.get(r5)
                            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r8
                            org.telegram.tgnet.TLRPC$StickerSet r9 = r8.set
                            java.lang.String r9 = r9.title
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r10 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r10 = r10.searchQuery
                            int r9 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r9, r10)
                            if (r9 < 0) goto L_0x01d8
                            if (r9 == 0) goto L_0x01be
                            org.telegram.tgnet.TLRPC$StickerSet r10 = r8.set
                            java.lang.String r10 = r10.title
                            int r11 = r9 + -1
                            char r10 = r10.charAt(r11)
                            if (r10 != r6) goto L_0x0211
                        L_0x01be:
                            r13.clear()
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.ArrayList r6 = r6.localPacks
                            r6.add(r8)
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.HashMap r6 = r6.localPacksByName
                            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
                            r6.put(r8, r9)
                            goto L_0x0211
                        L_0x01d8:
                            org.telegram.tgnet.TLRPC$StickerSet r9 = r8.set
                            java.lang.String r9 = r9.short_name
                            if (r9 == 0) goto L_0x0211
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r10 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r10 = r10.searchQuery
                            int r9 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r9, r10)
                            if (r9 < 0) goto L_0x0211
                            if (r9 == 0) goto L_0x01f8
                            org.telegram.tgnet.TLRPC$StickerSet r10 = r8.set
                            java.lang.String r10 = r10.short_name
                            int r9 = r9 + -1
                            char r9 = r10.charAt(r9)
                            if (r9 != r6) goto L_0x0211
                        L_0x01f8:
                            r13.clear()
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.ArrayList r6 = r6.localPacks
                            r6.add(r8)
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.HashMap r6 = r6.localPacksByShortName
                            java.lang.Boolean r9 = java.lang.Boolean.valueOf(r7)
                            r6.put(r8, r9)
                        L_0x0211:
                            int r5 = r5 + 1
                            goto L_0x0196
                        L_0x0214:
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                            int r0 = r0.currentAccount
                            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
                            r4 = 3
                            java.util.ArrayList r0 = r0.getStickerSets(r4)
                            int r4 = r0.size()
                            r5 = 0
                        L_0x022a:
                            if (r5 >= r4) goto L_0x02a6
                            java.lang.Object r8 = r0.get(r5)
                            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r8
                            org.telegram.tgnet.TLRPC$StickerSet r9 = r8.set
                            java.lang.String r9 = r9.title
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r10 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r10 = r10.searchQuery
                            int r9 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r9, r10)
                            if (r9 < 0) goto L_0x026a
                            if (r9 == 0) goto L_0x0250
                            org.telegram.tgnet.TLRPC$StickerSet r10 = r8.set
                            java.lang.String r10 = r10.title
                            int r11 = r9 + -1
                            char r10 = r10.charAt(r11)
                            if (r10 != r6) goto L_0x02a3
                        L_0x0250:
                            r13.clear()
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r10 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.ArrayList r10 = r10.localPacks
                            r10.add(r8)
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r10 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.HashMap r10 = r10.localPacksByName
                            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
                            r10.put(r8, r9)
                            goto L_0x02a3
                        L_0x026a:
                            org.telegram.tgnet.TLRPC$StickerSet r9 = r8.set
                            java.lang.String r9 = r9.short_name
                            if (r9 == 0) goto L_0x02a3
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r10 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r10 = r10.searchQuery
                            int r9 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r9, r10)
                            if (r9 < 0) goto L_0x02a3
                            if (r9 == 0) goto L_0x028a
                            org.telegram.tgnet.TLRPC$StickerSet r10 = r8.set
                            java.lang.String r10 = r10.short_name
                            int r9 = r9 + -1
                            char r9 = r10.charAt(r9)
                            if (r9 != r6) goto L_0x02a3
                        L_0x028a:
                            r13.clear()
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r9 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.ArrayList r9 = r9.localPacks
                            r9.add(r8)
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r9 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.HashMap r9 = r9.localPacksByShortName
                            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r7)
                            r9.put(r8, r10)
                        L_0x02a3:
                            int r5 = r5 + 1
                            goto L_0x022a
                        L_0x02a6:
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.ArrayList r0 = r0.localPacks
                            boolean r0 = r0.isEmpty()
                            if (r0 == 0) goto L_0x02be
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.HashMap r0 = r0.emojiStickers
                            boolean r0 = r0.isEmpty()
                            if (r0 != 0) goto L_0x02e7
                        L_0x02be:
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.RecyclerListView r0 = r0.stickersGridView
                            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r4 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r4 = r4.stickersSearchGridAdapter
                            if (r0 == r4) goto L_0x02e7
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.RecyclerListView r0 = r0.stickersGridView
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r4 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r4 = r4.stickersSearchGridAdapter
                            r0.setAdapter(r4)
                        L_0x02e7:
                            org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets r0 = new org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets
                            r0.<init>()
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r4 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r4 = r4.searchQuery
                            r0.q = r4
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r4 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r5 = org.telegram.ui.Components.EmojiView.this
                            int r5 = r5.currentAccount
                            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r5)
                            org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$PMXaBA9vcwjG4TQSEF8AuHh_dEU r6 = new org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$PMXaBA9vcwjG4TQSEF8AuHh_dEU
                            r6.<init>(r13, r0)
                            int r0 = r5.sendRequest(r0, r6)
                            int unused = r4.reqId = r0
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r0 = r0.searchQuery
                            boolean r0 = org.telegram.messenger.Emoji.isValidEmoji(r0)
                            if (r0 == 0) goto L_0x033f
                            org.telegram.tgnet.TLRPC$TL_messages_getStickers r0 = new org.telegram.tgnet.TLRPC$TL_messages_getStickers
                            r0.<init>()
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r4 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r4 = r4.searchQuery
                            r0.emoticon = r4
                            r0.hash = r1
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r1 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                            int r4 = r4.currentAccount
                            org.telegram.tgnet.ConnectionsManager r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
                            org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$D2YaPWN88kYmZJFvtUqCfq785Bw r5 = new org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$D2YaPWN88kYmZJFvtUqCfq785Bw
                            r5.<init>(r13, r0, r2, r3)
                            int r0 = r4.sendRequest(r0, r5)
                            int unused = r1.reqId2 = r0
                        L_0x033f:
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            r0.notifyDataSetChanged()
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.AnonymousClass1.run():void");
                    }

                    public /* synthetic */ void lambda$run$1$EmojiView$StickersSearchGridAdapter$1(TLRPC.TL_messages_searchStickerSets tL_messages_searchStickerSets, TLObject tLObject, TLRPC.TL_error tL_error) {
                        if (tLObject instanceof TLRPC.TL_messages_foundStickerSets) {
                            AndroidUtilities.runOnUIThread(
                            /*  JADX ERROR: Method code generation error
                                jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0009: INVOKE  
                                  (wrap: org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$jguHwpSdjaICAoSBsd6Lf5EhaUQ : 0x0006: CONSTRUCTOR  (r3v2 org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$jguHwpSdjaICAoSBsd6Lf5EhaUQ) = 
                                  (r0v0 'this' org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1 A[THIS])
                                  (r1v0 'tL_messages_searchStickerSets' org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets)
                                  (r2v0 'tLObject' org.telegram.tgnet.TLObject)
                                 call: org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$jguHwpSdjaICAoSBsd6Lf5EhaUQ.<init>(org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1, org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets, org.telegram.tgnet.TLObject):void type: CONSTRUCTOR)
                                 org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.1.lambda$run$1$EmojiView$StickersSearchGridAdapter$1(org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void, dex: classes.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
                                	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
                                	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
                                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0006: CONSTRUCTOR  (r3v2 org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$jguHwpSdjaICAoSBsd6Lf5EhaUQ) = 
                                  (r0v0 'this' org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1 A[THIS])
                                  (r1v0 'tL_messages_searchStickerSets' org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets)
                                  (r2v0 'tLObject' org.telegram.tgnet.TLObject)
                                 call: org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$jguHwpSdjaICAoSBsd6Lf5EhaUQ.<init>(org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1, org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets, org.telegram.tgnet.TLObject):void type: CONSTRUCTOR in method: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.1.lambda$run$1$EmojiView$StickersSearchGridAdapter$1(org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void, dex: classes.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                	... 64 more
                                Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$jguHwpSdjaICAoSBsd6Lf5EhaUQ, state: NOT_LOADED
                                	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	... 70 more
                                */
                            /*
                                this = this;
                                boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_foundStickerSets
                                if (r3 == 0) goto L_0x000c
                                org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$jguHwpSdjaICAoSBsd6Lf5EhaUQ r3 = new org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$jguHwpSdjaICAoSBsd6Lf5EhaUQ
                                r3.<init>(r0, r1, r2)
                                org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)
                            L_0x000c:
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.AnonymousClass1.lambda$run$1$EmojiView$StickersSearchGridAdapter$1(org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
                        }

                        public /* synthetic */ void lambda$null$0$EmojiView$StickersSearchGridAdapter$1(TLRPC.TL_messages_searchStickerSets tL_messages_searchStickerSets, TLObject tLObject) {
                            if (tL_messages_searchStickerSets.q.equals(StickersSearchGridAdapter.this.searchQuery)) {
                                clear();
                                EmojiView.this.stickersSearchField.progressDrawable.stopAnimation();
                                int unused = StickersSearchGridAdapter.this.reqId = 0;
                                if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersSearchGridAdapter) {
                                    EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersSearchGridAdapter);
                                }
                                StickersSearchGridAdapter.this.serverPacks.addAll(((TLRPC.TL_messages_foundStickerSets) tLObject).sets);
                                StickersSearchGridAdapter.this.notifyDataSetChanged();
                            }
                        }

                        public /* synthetic */ void lambda$run$3$EmojiView$StickersSearchGridAdapter$1(TLRPC.TL_messages_getStickers tL_messages_getStickers, ArrayList arrayList, LongSparseArray longSparseArray, TLObject tLObject, TLRPC.TL_error tL_error) {
                            AndroidUtilities.runOnUIThread(
                            /*  JADX ERROR: Method code generation error
                                jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000b: INVOKE  
                                  (wrap: org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$0O6pCOi0jii8pwBAvqjd7az_Jp8 : 0x0008: CONSTRUCTOR  (r0v0 org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$0O6pCOi0jii8pwBAvqjd7az_Jp8) = 
                                  (r6v0 'this' org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1 A[THIS])
                                  (r7v0 'tL_messages_getStickers' org.telegram.tgnet.TLRPC$TL_messages_getStickers)
                                  (r10v0 'tLObject' org.telegram.tgnet.TLObject)
                                  (r8v0 'arrayList' java.util.ArrayList)
                                  (r9v0 'longSparseArray' android.util.LongSparseArray)
                                 call: org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$0O6pCOi0jii8pwBAvqjd7az_Jp8.<init>(org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1, org.telegram.tgnet.TLRPC$TL_messages_getStickers, org.telegram.tgnet.TLObject, java.util.ArrayList, android.util.LongSparseArray):void type: CONSTRUCTOR)
                                 org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.1.lambda$run$3$EmojiView$StickersSearchGridAdapter$1(org.telegram.tgnet.TLRPC$TL_messages_getStickers, java.util.ArrayList, android.util.LongSparseArray, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void, dex: classes.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
                                	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
                                	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
                                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                	at java.util.ArrayList.forEach(ArrayList.java:1257)
                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0008: CONSTRUCTOR  (r0v0 org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$0O6pCOi0jii8pwBAvqjd7az_Jp8) = 
                                  (r6v0 'this' org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1 A[THIS])
                                  (r7v0 'tL_messages_getStickers' org.telegram.tgnet.TLRPC$TL_messages_getStickers)
                                  (r10v0 'tLObject' org.telegram.tgnet.TLObject)
                                  (r8v0 'arrayList' java.util.ArrayList)
                                  (r9v0 'longSparseArray' android.util.LongSparseArray)
                                 call: org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$0O6pCOi0jii8pwBAvqjd7az_Jp8.<init>(org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1, org.telegram.tgnet.TLRPC$TL_messages_getStickers, org.telegram.tgnet.TLObject, java.util.ArrayList, android.util.LongSparseArray):void type: CONSTRUCTOR in method: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.1.lambda$run$3$EmojiView$StickersSearchGridAdapter$1(org.telegram.tgnet.TLRPC$TL_messages_getStickers, java.util.ArrayList, android.util.LongSparseArray, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void, dex: classes.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                	... 57 more
                                Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$0O6pCOi0jii8pwBAvqjd7az_Jp8, state: NOT_LOADED
                                	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	... 63 more
                                */
                            /*
                                this = this;
                                org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$0O6pCOi0jii8pwBAvqjd7az_Jp8 r11 = new org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$0O6pCOi0jii8pwBAvqjd7az_Jp8
                                r0 = r11
                                r1 = r6
                                r2 = r7
                                r3 = r10
                                r4 = r8
                                r5 = r9
                                r0.<init>(r1, r2, r3, r4, r5)
                                org.telegram.messenger.AndroidUtilities.runOnUIThread(r11)
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.AnonymousClass1.lambda$run$3$EmojiView$StickersSearchGridAdapter$1(org.telegram.tgnet.TLRPC$TL_messages_getStickers, java.util.ArrayList, android.util.LongSparseArray, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
                        }

                        public /* synthetic */ void lambda$null$2$EmojiView$StickersSearchGridAdapter$1(TLRPC.TL_messages_getStickers tL_messages_getStickers, TLObject tLObject, ArrayList arrayList, LongSparseArray longSparseArray) {
                            if (tL_messages_getStickers.emoticon.equals(StickersSearchGridAdapter.this.searchQuery)) {
                                int unused = StickersSearchGridAdapter.this.reqId2 = 0;
                                if (tLObject instanceof TLRPC.TL_messages_stickers) {
                                    TLRPC.TL_messages_stickers tL_messages_stickers = (TLRPC.TL_messages_stickers) tLObject;
                                    int size = arrayList.size();
                                    int size2 = tL_messages_stickers.stickers.size();
                                    for (int i = 0; i < size2; i++) {
                                        TLRPC.Document document = tL_messages_stickers.stickers.get(i);
                                        if (longSparseArray.indexOfKey(document.id) < 0) {
                                            arrayList.add(document);
                                        }
                                    }
                                    if (size != arrayList.size()) {
                                        StickersSearchGridAdapter.this.emojiStickers.put(arrayList, StickersSearchGridAdapter.this.searchQuery);
                                        if (size == 0) {
                                            StickersSearchGridAdapter.this.emojiArrays.add(arrayList);
                                        }
                                        StickersSearchGridAdapter.this.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    };
                    /* access modifiers changed from: private */
                    public ArrayList<TLRPC.StickerSetCovered> serverPacks = new ArrayList<>();
                    /* access modifiers changed from: private */
                    public int totalItems;

                    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                        return false;
                    }

                    static /* synthetic */ int access$13504(StickersSearchGridAdapter stickersSearchGridAdapter) {
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

                    public Object getItem(int i) {
                        return this.cache.get(i);
                    }

                    public void search(String str) {
                        if (this.reqId != 0) {
                            ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                            this.reqId = 0;
                        }
                        if (this.reqId2 != 0) {
                            ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId2, true);
                            this.reqId2 = 0;
                        }
                        if (TextUtils.isEmpty(str)) {
                            this.searchQuery = null;
                            this.localPacks.clear();
                            this.emojiStickers.clear();
                            this.serverPacks.clear();
                            if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersGridAdapter) {
                                EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersGridAdapter);
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
                        if (obj == null) {
                            return 1;
                        }
                        if (obj instanceof TLRPC.Document) {
                            return 0;
                        }
                        return obj instanceof TLRPC.StickerSetCovered ? 3 : 2;
                    }

                    public /* synthetic */ void lambda$onCreateViewHolder$0$EmojiView$StickersSearchGridAdapter(View view) {
                        FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
                        TLRPC.StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
                        if (EmojiView.this.installingStickerSets.indexOfKey(stickerSet.set.id) < 0 && EmojiView.this.removingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
                            if (featuredStickerSetInfoCell.isInstalled()) {
                                EmojiView.this.removingStickerSets.put(stickerSet.set.id, stickerSet);
                                EmojiView.this.delegate.onStickerSetRemove(featuredStickerSetInfoCell.getStickerSet());
                                return;
                            }
                            EmojiView.this.installingStickerSets.put(stickerSet.set.id, stickerSet);
                            EmojiView.this.delegate.onStickerSetAdd(featuredStickerSetInfoCell.getStickerSet());
                        }
                    }

                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v1, resolved type: org.telegram.ui.Cells.FeaturedStickerSetInfoCell} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v11, resolved type: org.telegram.ui.Cells.FeaturedStickerSetInfoCell} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v12, resolved type: org.telegram.ui.Cells.FeaturedStickerSetInfoCell} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v13, resolved type: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$2} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v14, resolved type: org.telegram.ui.Cells.EmptyCell} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v15, resolved type: org.telegram.ui.Cells.StickerSetNameCell} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v16, resolved type: org.telegram.ui.Cells.FeaturedStickerSetInfoCell} */
                    /* JADX WARNING: type inference failed for: r15v9, types: [android.widget.FrameLayout, android.view.View, org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$3] */
                    /* JADX WARNING: Multi-variable type inference failed */
                    /* JADX WARNING: Unknown variable types count: 1 */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r14, int r15) {
                        /*
                            r13 = this;
                            if (r15 == 0) goto L_0x00c3
                            r14 = 1
                            if (r15 == r14) goto L_0x00bb
                            r0 = 2
                            if (r15 == r0) goto L_0x00b2
                            r0 = 3
                            if (r15 == r0) goto L_0x00a0
                            r0 = 4
                            r1 = -1
                            if (r15 == r0) goto L_0x008a
                            r0 = 5
                            if (r15 == r0) goto L_0x0015
                            r14 = 0
                            goto L_0x00ca
                        L_0x0015:
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$3 r15 = new org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$3
                            android.content.Context r0 = r13.context
                            r15.<init>(r0)
                            android.widget.ImageView r0 = new android.widget.ImageView
                            android.content.Context r2 = r13.context
                            r0.<init>(r2)
                            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
                            r0.setScaleType(r2)
                            r2 = 2131165894(0x7var_c6, float:1.7946018E38)
                            r0.setImageResource(r2)
                            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
                            java.lang.String r3 = "chat_emojiPanelEmptyText"
                            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
                            r2.<init>(r4, r5)
                            r0.setColorFilter(r2)
                            r6 = -2
                            r7 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                            r8 = 17
                            r9 = 0
                            r10 = 0
                            r11 = 0
                            r12 = 1114374144(0x426CLASSNAME, float:59.0)
                            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
                            r15.addView(r0, r2)
                            android.widget.TextView r0 = new android.widget.TextView
                            android.content.Context r2 = r13.context
                            r0.<init>(r2)
                            r2 = 2131625688(0x7f0e06d8, float:1.8878591E38)
                            java.lang.String r4 = "NoStickersFound"
                            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
                            r0.setText(r2)
                            r2 = 1098907648(0x41800000, float:16.0)
                            r0.setTextSize(r14, r2)
                            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                            r0.setTextColor(r14)
                            r2 = -2
                            r3 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                            r4 = 17
                            r5 = 0
                            r6 = 0
                            r7 = 0
                            r8 = 1091567616(0x41100000, float:9.0)
                            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8)
                            r15.addView(r0, r14)
                            androidx.recyclerview.widget.RecyclerView$LayoutParams r14 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                            r0 = -2
                            r14.<init>((int) r1, (int) r0)
                            r15.setLayoutParams(r14)
                            r14 = r15
                            goto L_0x00ca
                        L_0x008a:
                            android.view.View r14 = new android.view.View
                            android.content.Context r15 = r13.context
                            r14.<init>(r15)
                            androidx.recyclerview.widget.RecyclerView$LayoutParams r15 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                            org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                            int r0 = r0.searchFieldHeight
                            r15.<init>((int) r1, (int) r0)
                            r14.setLayoutParams(r15)
                            goto L_0x00ca
                        L_0x00a0:
                            org.telegram.ui.Cells.FeaturedStickerSetInfoCell r14 = new org.telegram.ui.Cells.FeaturedStickerSetInfoCell
                            android.content.Context r15 = r13.context
                            r0 = 17
                            r14.<init>(r15, r0)
                            org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$An1o7aFGx9Hb6YuxBcvVH4f8K-M r15 = new org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$An1o7aFGx9Hb6YuxBcvVH4f8K-M
                            r15.<init>()
                            r14.setAddOnClickListener(r15)
                            goto L_0x00ca
                        L_0x00b2:
                            org.telegram.ui.Cells.StickerSetNameCell r14 = new org.telegram.ui.Cells.StickerSetNameCell
                            android.content.Context r15 = r13.context
                            r0 = 0
                            r14.<init>(r15, r0)
                            goto L_0x00ca
                        L_0x00bb:
                            org.telegram.ui.Cells.EmptyCell r14 = new org.telegram.ui.Cells.EmptyCell
                            android.content.Context r15 = r13.context
                            r14.<init>(r15)
                            goto L_0x00ca
                        L_0x00c3:
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$2 r14 = new org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$2
                            android.content.Context r15 = r13.context
                            r14.<init>(r15)
                        L_0x00ca:
                            org.telegram.ui.Components.RecyclerListView$Holder r15 = new org.telegram.ui.Components.RecyclerListView$Holder
                            r15.<init>(r14)
                            return r15
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
                    }

                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v12, resolved type: java.lang.Object} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: java.lang.Integer} */
                    /* JADX WARNING: Multi-variable type inference failed */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r10, int r11) {
                        /*
                            r9 = this;
                            int r0 = r10.getItemViewType()
                            r1 = 1
                            r2 = 0
                            if (r0 == 0) goto L_0x01ac
                            r3 = 0
                            if (r0 == r1) goto L_0x0124
                            r4 = 2
                            if (r0 == r4) goto L_0x00c0
                            r3 = 3
                            if (r0 == r3) goto L_0x0013
                            goto L_0x01e6
                        L_0x0013:
                            android.util.SparseArray<java.lang.Object> r0 = r9.cache
                            java.lang.Object r11 = r0.get(r11)
                            r4 = r11
                            org.telegram.tgnet.TLRPC$StickerSetCovered r4 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r4
                            android.view.View r10 = r10.itemView
                            r3 = r10
                            org.telegram.ui.Cells.FeaturedStickerSetInfoCell r3 = (org.telegram.ui.Cells.FeaturedStickerSetInfoCell) r3
                            org.telegram.ui.Components.EmojiView r10 = org.telegram.ui.Components.EmojiView.this
                            android.util.LongSparseArray r10 = r10.installingStickerSets
                            org.telegram.tgnet.TLRPC$StickerSet r11 = r4.set
                            long r5 = r11.id
                            int r10 = r10.indexOfKey(r5)
                            if (r10 < 0) goto L_0x0033
                            r10 = 1
                            goto L_0x0034
                        L_0x0033:
                            r10 = 0
                        L_0x0034:
                            org.telegram.ui.Components.EmojiView r11 = org.telegram.ui.Components.EmojiView.this
                            android.util.LongSparseArray r11 = r11.removingStickerSets
                            org.telegram.tgnet.TLRPC$StickerSet r0 = r4.set
                            long r5 = r0.id
                            int r11 = r11.indexOfKey(r5)
                            if (r11 < 0) goto L_0x0045
                            goto L_0x0046
                        L_0x0045:
                            r1 = 0
                        L_0x0046:
                            if (r10 != 0) goto L_0x004a
                            if (r1 == 0) goto L_0x0075
                        L_0x004a:
                            if (r10 == 0) goto L_0x0060
                            boolean r10 = r3.isInstalled()
                            if (r10 == 0) goto L_0x0060
                            org.telegram.ui.Components.EmojiView r10 = org.telegram.ui.Components.EmojiView.this
                            android.util.LongSparseArray r10 = r10.installingStickerSets
                            org.telegram.tgnet.TLRPC$StickerSet r11 = r4.set
                            long r0 = r11.id
                            r10.remove(r0)
                            goto L_0x0075
                        L_0x0060:
                            if (r1 == 0) goto L_0x0075
                            boolean r10 = r3.isInstalled()
                            if (r10 != 0) goto L_0x0075
                            org.telegram.ui.Components.EmojiView r10 = org.telegram.ui.Components.EmojiView.this
                            android.util.LongSparseArray r10 = r10.removingStickerSets
                            org.telegram.tgnet.TLRPC$StickerSet r11 = r4.set
                            long r0 = r11.id
                            r10.remove(r0)
                        L_0x0075:
                            java.lang.String r10 = r9.searchQuery
                            boolean r10 = android.text.TextUtils.isEmpty(r10)
                            if (r10 == 0) goto L_0x0080
                            r10 = -1
                            r7 = -1
                            goto L_0x008b
                        L_0x0080:
                            org.telegram.tgnet.TLRPC$StickerSet r10 = r4.set
                            java.lang.String r10 = r10.title
                            java.lang.String r11 = r9.searchQuery
                            int r10 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r10, r11)
                            r7 = r10
                        L_0x008b:
                            if (r7 < 0) goto L_0x009a
                            r5 = 0
                            r6 = 0
                            java.lang.String r10 = r9.searchQuery
                            int r8 = r10.length()
                            r3.setStickerSet(r4, r5, r6, r7, r8)
                            goto L_0x01e6
                        L_0x009a:
                            r3.setStickerSet(r4, r2)
                            java.lang.String r10 = r9.searchQuery
                            boolean r10 = android.text.TextUtils.isEmpty(r10)
                            if (r10 != 0) goto L_0x01e6
                            org.telegram.tgnet.TLRPC$StickerSet r10 = r4.set
                            java.lang.String r10 = r10.short_name
                            java.lang.String r11 = r9.searchQuery
                            int r10 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r10, r11)
                            if (r10 != 0) goto L_0x01e6
                            org.telegram.tgnet.TLRPC$StickerSet r10 = r4.set
                            java.lang.String r10 = r10.short_name
                            java.lang.String r11 = r9.searchQuery
                            int r11 = r11.length()
                            r3.setUrl(r10, r11)
                            goto L_0x01e6
                        L_0x00c0:
                            android.view.View r10 = r10.itemView
                            org.telegram.ui.Cells.StickerSetNameCell r10 = (org.telegram.ui.Cells.StickerSetNameCell) r10
                            android.util.SparseArray<java.lang.Object> r0 = r9.cache
                            java.lang.Object r11 = r0.get(r11)
                            boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messages_stickerSet
                            if (r0 == 0) goto L_0x01e6
                            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r11 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r11
                            java.lang.String r0 = r9.searchQuery
                            boolean r0 = android.text.TextUtils.isEmpty(r0)
                            if (r0 != 0) goto L_0x00f8
                            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_messages_stickerSet, java.lang.Boolean> r0 = r9.localPacksByShortName
                            boolean r0 = r0.containsKey(r11)
                            if (r0 == 0) goto L_0x00f8
                            org.telegram.tgnet.TLRPC$StickerSet r0 = r11.set
                            if (r0 == 0) goto L_0x00e9
                            java.lang.String r0 = r0.title
                            r10.setText(r0, r2)
                        L_0x00e9:
                            org.telegram.tgnet.TLRPC$StickerSet r11 = r11.set
                            java.lang.String r11 = r11.short_name
                            java.lang.String r0 = r9.searchQuery
                            int r0 = r0.length()
                            r10.setUrl(r11, r0)
                            goto L_0x01e6
                        L_0x00f8:
                            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_messages_stickerSet, java.lang.Integer> r0 = r9.localPacksByName
                            java.lang.Object r0 = r0.get(r11)
                            java.lang.Integer r0 = (java.lang.Integer) r0
                            org.telegram.tgnet.TLRPC$StickerSet r11 = r11.set
                            if (r11 == 0) goto L_0x011f
                            if (r0 == 0) goto L_0x011f
                            java.lang.String r11 = r11.title
                            int r0 = r0.intValue()
                            java.lang.String r1 = r9.searchQuery
                            boolean r1 = android.text.TextUtils.isEmpty(r1)
                            if (r1 != 0) goto L_0x011b
                            java.lang.String r1 = r9.searchQuery
                            int r1 = r1.length()
                            goto L_0x011c
                        L_0x011b:
                            r1 = 0
                        L_0x011c:
                            r10.setText(r11, r2, r0, r1)
                        L_0x011f:
                            r10.setUrl(r3, r2)
                            goto L_0x01e6
                        L_0x0124:
                            android.view.View r10 = r10.itemView
                            org.telegram.ui.Cells.EmptyCell r10 = (org.telegram.ui.Cells.EmptyCell) r10
                            int r0 = r9.totalItems
                            r2 = 1118044160(0x42a40000, float:82.0)
                            if (r11 != r0) goto L_0x01a4
                            android.util.SparseIntArray r0 = r9.positionToRow
                            int r11 = r11 - r1
                            r4 = -2147483648(0xfffffffvar_, float:-0.0)
                            int r11 = r0.get(r11, r4)
                            if (r11 != r4) goto L_0x013e
                            r10.setHeight(r1)
                            goto L_0x01e6
                        L_0x013e:
                            android.util.SparseArray<java.lang.Object> r0 = r9.rowStartPack
                            java.lang.Object r11 = r0.get(r11)
                            boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messages_stickerSet
                            if (r0 == 0) goto L_0x0155
                            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r11 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r11
                            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r11 = r11.documents
                            int r11 = r11.size()
                            java.lang.Integer r3 = java.lang.Integer.valueOf(r11)
                            goto L_0x015c
                        L_0x0155:
                            boolean r0 = r11 instanceof java.lang.Integer
                            if (r0 == 0) goto L_0x015c
                            r3 = r11
                            java.lang.Integer r3 = (java.lang.Integer) r3
                        L_0x015c:
                            if (r3 != 0) goto L_0x0163
                            r10.setHeight(r1)
                            goto L_0x01e6
                        L_0x0163:
                            int r11 = r3.intValue()
                            if (r11 != 0) goto L_0x0174
                            r11 = 1090519040(0x41000000, float:8.0)
                            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
                            r10.setHeight(r11)
                            goto L_0x01e6
                        L_0x0174:
                            org.telegram.ui.Components.EmojiView r11 = org.telegram.ui.Components.EmojiView.this
                            androidx.viewpager.widget.ViewPager r11 = r11.pager
                            int r11 = r11.getHeight()
                            int r0 = r3.intValue()
                            float r0 = (float) r0
                            org.telegram.ui.Components.EmojiView r3 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.EmojiView$StickersGridAdapter r3 = r3.stickersGridAdapter
                            int r3 = r3.stickersPerRow
                            float r3 = (float) r3
                            float r0 = r0 / r3
                            double r3 = (double) r0
                            double r3 = java.lang.Math.ceil(r3)
                            int r0 = (int) r3
                            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                            int r0 = r0 * r2
                            int r11 = r11 - r0
                            if (r11 <= 0) goto L_0x019f
                            goto L_0x01a0
                        L_0x019f:
                            r11 = 1
                        L_0x01a0:
                            r10.setHeight(r11)
                            goto L_0x01e6
                        L_0x01a4:
                            int r11 = org.telegram.messenger.AndroidUtilities.dp(r2)
                            r10.setHeight(r11)
                            goto L_0x01e6
                        L_0x01ac:
                            android.util.SparseArray<java.lang.Object> r0 = r9.cache
                            java.lang.Object r0 = r0.get(r11)
                            org.telegram.tgnet.TLRPC$Document r0 = (org.telegram.tgnet.TLRPC.Document) r0
                            android.view.View r10 = r10.itemView
                            org.telegram.ui.Cells.StickerEmojiCell r10 = (org.telegram.ui.Cells.StickerEmojiCell) r10
                            android.util.SparseArray<java.lang.Object> r3 = r9.cacheParent
                            java.lang.Object r3 = r3.get(r11)
                            android.util.SparseArray<java.lang.String> r4 = r9.positionToEmoji
                            java.lang.Object r11 = r4.get(r11)
                            java.lang.String r11 = (java.lang.String) r11
                            r10.setSticker(r0, r3, r11, r2)
                            org.telegram.ui.Components.EmojiView r11 = org.telegram.ui.Components.EmojiView.this
                            java.util.ArrayList r11 = r11.recentStickers
                            boolean r11 = r11.contains(r0)
                            if (r11 != 0) goto L_0x01e3
                            org.telegram.ui.Components.EmojiView r11 = org.telegram.ui.Components.EmojiView.this
                            java.util.ArrayList r11 = r11.favouriteStickers
                            boolean r11 = r11.contains(r0)
                            if (r11 == 0) goto L_0x01e2
                            goto L_0x01e3
                        L_0x01e2:
                            r1 = 0
                        L_0x01e3:
                            r10.setRecent(r1)
                        L_0x01e6:
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
                    }

                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: org.telegram.tgnet.TLRPC$StickerSetCovered} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v15, resolved type: org.telegram.tgnet.TLRPC$TL_messages_stickerSet} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v17, resolved type: org.telegram.tgnet.TLRPC$StickerSetCovered} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: org.telegram.tgnet.TLRPC$StickerSetCovered} */
                    /* JADX WARNING: Multi-variable type inference failed */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void notifyDataSetChanged() {
                        /*
                            r19 = this;
                            r0 = r19
                            android.util.SparseArray<java.lang.Object> r1 = r0.rowStartPack
                            r1.clear()
                            android.util.SparseIntArray r1 = r0.positionToRow
                            r1.clear()
                            android.util.SparseArray<java.lang.Object> r1 = r0.cache
                            r1.clear()
                            android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r1 = r0.positionsToSets
                            r1.clear()
                            android.util.SparseArray<java.lang.String> r1 = r0.positionToEmoji
                            r1.clear()
                            r1 = 0
                            r0.totalItems = r1
                            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r2 = r0.serverPacks
                            int r2 = r2.size()
                            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r3 = r0.localPacks
                            int r3 = r3.size()
                            java.util.ArrayList<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r4 = r0.emojiArrays
                            boolean r4 = r4.isEmpty()
                            r4 = r4 ^ 1
                            r5 = -1
                            r6 = -1
                            r7 = 0
                        L_0x0035:
                            int r8 = r2 + r3
                            int r8 = r8 + r4
                            if (r6 >= r8) goto L_0x01e4
                            if (r6 != r5) goto L_0x004f
                            android.util.SparseArray<java.lang.Object> r8 = r0.cache
                            int r9 = r0.totalItems
                            int r10 = r9 + 1
                            r0.totalItems = r10
                            java.lang.String r10 = "search"
                            r8.put(r9, r10)
                            int r7 = r7 + 1
                            r16 = r2
                            goto L_0x01dc
                        L_0x004f:
                            if (r6 >= r3) goto L_0x005f
                            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r8 = r0.localPacks
                            java.lang.Object r8 = r8.get(r6)
                            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r8
                            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r8.documents
                            r16 = r2
                            goto L_0x013a
                        L_0x005f:
                            int r8 = r6 - r3
                            if (r8 >= r4) goto L_0x012c
                            java.util.ArrayList<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r8 = r0.emojiArrays
                            int r8 = r8.size()
                            java.lang.String r9 = ""
                            r11 = r9
                            r9 = 0
                            r10 = 0
                        L_0x006e:
                            if (r9 >= r8) goto L_0x00f2
                            java.util.ArrayList<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r12 = r0.emojiArrays
                            java.lang.Object r12 = r12.get(r9)
                            java.util.ArrayList r12 = (java.util.ArrayList) r12
                            java.util.HashMap<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>, java.lang.String> r13 = r0.emojiStickers
                            java.lang.Object r13 = r13.get(r12)
                            java.lang.String r13 = (java.lang.String) r13
                            if (r13 == 0) goto L_0x0091
                            boolean r14 = r11.equals(r13)
                            if (r14 != 0) goto L_0x0091
                            android.util.SparseArray<java.lang.String> r11 = r0.positionToEmoji
                            int r14 = r0.totalItems
                            int r14 = r14 + r10
                            r11.put(r14, r13)
                            r11 = r13
                        L_0x0091:
                            int r13 = r12.size()
                            r14 = r10
                            r10 = 0
                        L_0x0097:
                            if (r10 >= r13) goto L_0x00e7
                            int r15 = r0.totalItems
                            int r15 = r15 + r14
                            org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.EmojiView$StickersGridAdapter r1 = r1.stickersGridAdapter
                            int r1 = r1.stickersPerRow
                            int r1 = r14 / r1
                            int r1 = r1 + r7
                            java.lang.Object r16 = r12.get(r10)
                            r5 = r16
                            org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC.Document) r5
                            r16 = r2
                            android.util.SparseArray<java.lang.Object> r2 = r0.cache
                            r2.put(r15, r5)
                            org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                            int r2 = r2.currentAccount
                            org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.getInstance(r2)
                            r18 = r11
                            r17 = r12
                            long r11 = org.telegram.messenger.MediaDataController.getStickerSetId(r5)
                            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = r2.getStickerSetById(r11)
                            if (r2 == 0) goto L_0x00d5
                            android.util.SparseArray<java.lang.Object> r5 = r0.cacheParent
                            r5.put(r15, r2)
                        L_0x00d5:
                            android.util.SparseIntArray r2 = r0.positionToRow
                            r2.put(r15, r1)
                            int r14 = r14 + 1
                            int r10 = r10 + 1
                            r2 = r16
                            r12 = r17
                            r11 = r18
                            r1 = 0
                            r5 = -1
                            goto L_0x0097
                        L_0x00e7:
                            r16 = r2
                            r18 = r11
                            int r9 = r9 + 1
                            r10 = r14
                            r1 = 0
                            r5 = -1
                            goto L_0x006e
                        L_0x00f2:
                            r16 = r2
                            float r1 = (float) r10
                            org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.EmojiView$StickersGridAdapter r2 = r2.stickersGridAdapter
                            int r2 = r2.stickersPerRow
                            float r2 = (float) r2
                            float r1 = r1 / r2
                            double r1 = (double) r1
                            double r1 = java.lang.Math.ceil(r1)
                            int r1 = (int) r1
                            r2 = 0
                        L_0x0108:
                            if (r2 >= r1) goto L_0x0118
                            android.util.SparseArray<java.lang.Object> r5 = r0.rowStartPack
                            int r8 = r7 + r2
                            java.lang.Integer r9 = java.lang.Integer.valueOf(r10)
                            r5.put(r8, r9)
                            int r2 = r2 + 1
                            goto L_0x0108
                        L_0x0118:
                            int r2 = r0.totalItems
                            org.telegram.ui.Components.EmojiView r5 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.EmojiView$StickersGridAdapter r5 = r5.stickersGridAdapter
                            int r5 = r5.stickersPerRow
                            int r5 = r5 * r1
                            int r2 = r2 + r5
                            r0.totalItems = r2
                            int r7 = r7 + r1
                            goto L_0x01dc
                        L_0x012c:
                            r16 = r2
                            int r8 = r8 - r4
                            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r1 = r0.serverPacks
                            java.lang.Object r1 = r1.get(r8)
                            r8 = r1
                            org.telegram.tgnet.TLRPC$StickerSetCovered r8 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r8
                            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r8.covers
                        L_0x013a:
                            boolean r1 = r9.isEmpty()
                            if (r1 == 0) goto L_0x0142
                            goto L_0x01dc
                        L_0x0142:
                            int r1 = r9.size()
                            float r1 = (float) r1
                            org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.EmojiView$StickersGridAdapter r2 = r2.stickersGridAdapter
                            int r2 = r2.stickersPerRow
                            float r2 = (float) r2
                            float r1 = r1 / r2
                            double r1 = (double) r1
                            double r1 = java.lang.Math.ceil(r1)
                            int r1 = (int) r1
                            android.util.SparseArray<java.lang.Object> r2 = r0.cache
                            int r5 = r0.totalItems
                            r2.put(r5, r8)
                            if (r6 < r3) goto L_0x0170
                            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC.StickerSetCovered
                            if (r2 == 0) goto L_0x0170
                            android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r2 = r0.positionsToSets
                            int r5 = r0.totalItems
                            r10 = r8
                            org.telegram.tgnet.TLRPC$StickerSetCovered r10 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r10
                            r2.put(r5, r10)
                        L_0x0170:
                            android.util.SparseIntArray r2 = r0.positionToRow
                            int r5 = r0.totalItems
                            r2.put(r5, r7)
                            int r2 = r9.size()
                            r5 = 0
                        L_0x017c:
                            if (r5 >= r2) goto L_0x01b9
                            int r10 = r5 + 1
                            int r11 = r0.totalItems
                            int r11 = r11 + r10
                            int r12 = r7 + 1
                            org.telegram.ui.Components.EmojiView r13 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.EmojiView$StickersGridAdapter r13 = r13.stickersGridAdapter
                            int r13 = r13.stickersPerRow
                            int r13 = r5 / r13
                            int r12 = r12 + r13
                            java.lang.Object r5 = r9.get(r5)
                            org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC.Document) r5
                            android.util.SparseArray<java.lang.Object> r13 = r0.cache
                            r13.put(r11, r5)
                            if (r8 == 0) goto L_0x01a4
                            android.util.SparseArray<java.lang.Object> r5 = r0.cacheParent
                            r5.put(r11, r8)
                        L_0x01a4:
                            android.util.SparseIntArray r5 = r0.positionToRow
                            r5.put(r11, r12)
                            if (r6 < r3) goto L_0x01b7
                            boolean r5 = r8 instanceof org.telegram.tgnet.TLRPC.StickerSetCovered
                            if (r5 == 0) goto L_0x01b7
                            android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r5 = r0.positionsToSets
                            r12 = r8
                            org.telegram.tgnet.TLRPC$StickerSetCovered r12 = (org.telegram.tgnet.TLRPC.StickerSetCovered) r12
                            r5.put(r11, r12)
                        L_0x01b7:
                            r5 = r10
                            goto L_0x017c
                        L_0x01b9:
                            int r2 = r1 + 1
                            r5 = 0
                        L_0x01bc:
                            if (r5 >= r2) goto L_0x01c8
                            android.util.SparseArray<java.lang.Object> r9 = r0.rowStartPack
                            int r10 = r7 + r5
                            r9.put(r10, r8)
                            int r5 = r5 + 1
                            goto L_0x01bc
                        L_0x01c8:
                            int r5 = r0.totalItems
                            org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.EmojiView$StickersGridAdapter r8 = r8.stickersGridAdapter
                            int r8 = r8.stickersPerRow
                            int r1 = r1 * r8
                            int r1 = r1 + 1
                            int r5 = r5 + r1
                            r0.totalItems = r5
                            int r7 = r7 + r2
                        L_0x01dc:
                            int r6 = r6 + 1
                            r2 = r16
                            r1 = 0
                            r5 = -1
                            goto L_0x0035
                        L_0x01e4:
                            super.notifyDataSetChanged()
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.notifyDataSetChanged():void");
                    }
                }
            }
