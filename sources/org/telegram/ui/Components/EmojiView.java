package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build.VERSION;
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
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.DataQuery.KeywordResult;
import org.telegram.messenger.DataQuery.KeywordResultCallback;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_emojiURL;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_foundStickerSets;
import org.telegram.tgnet.TLRPC.TL_messages_getEmojiURL;
import org.telegram.tgnet.TLRPC.TL_messages_getInlineBotResults;
import org.telegram.tgnet.TLRPC.TL_messages_getStickers;
import org.telegram.tgnet.TLRPC.TL_messages_searchStickerSets;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_stickers;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebDocument;
import org.telegram.tgnet.TLRPC.messages_BotResults;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Cells.StickerSetGroupInfoCell;
import org.telegram.ui.Cells.StickerSetNameCell;
import org.telegram.ui.Components.PagerSlidingTabStrip.IconTabProvider;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate;
import org.telegram.ui.ContentPreviewViewer;
import org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate;
import org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate.-CC;

public class EmojiView extends FrameLayout implements NotificationCenterDelegate {
    private static final OnScrollChangedListener NOP = -$$Lambda$EmojiView$PY-mfpY1F_JSo9ExUvlHDRxz4bQ.INSTANCE;
    private static final Field superListenerField;
    private ImageView backspaceButton;
    private AnimatorSet backspaceButtonAnimation;
    private boolean backspaceOnce;
    private boolean backspacePressed;
    private FrameLayout bottomTabContainer;
    private AnimatorSet bottomTabContainerAnimation;
    private View bottomTabContainerBackground;
    private ContentPreviewViewerDelegate contentPreviewViewerDelegate = new ContentPreviewViewerDelegate() {
        public /* synthetic */ boolean needOpen() {
            return -CC.$default$needOpen(this);
        }

        public boolean needSend() {
            return true;
        }

        public void sendSticker(Document document, Object obj) {
            EmojiView.this.delegate.onStickerSelected(document, obj);
        }

        public void openSet(InputStickerSet inputStickerSet, boolean z) {
            if (inputStickerSet != null) {
                EmojiView.this.delegate.onShowStickerSet(null, inputStickerSet);
            }
        }

        public void sendGif(Document document) {
            if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter) {
                EmojiView.this.delegate.onGifSelected(document, "gif");
            } else if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter) {
                EmojiView.this.delegate.onGifSelected(document, EmojiView.this.gifSearchAdapter.bot);
            }
        }

        public void gifAddedOrDeleted() {
            EmojiView emojiView = EmojiView.this;
            emojiView.recentGifs = DataQuery.getInstance(emojiView.currentAccount).getRecentGifs();
            if (EmojiView.this.gifAdapter != null) {
                EmojiView.this.gifAdapter.notifyDataSetChanged();
            }
        }
    };
    private int currentAccount = UserConfig.selectedAccount;
    private int currentBackgroundType = -1;
    private int currentChatId;
    private int currentPage;
    private EmojiViewDelegate delegate;
    private Paint dotPaint;
    private DragListener dragListener;
    private EmojiGridAdapter emojiAdapter;
    private FrameLayout emojiContainer;
    private RecyclerListView emojiGridView;
    private Drawable[] emojiIcons;
    private float emojiLastX;
    private float emojiLastY;
    private GridLayoutManager emojiLayoutManager;
    private int emojiMinusDy;
    private EmojiSearchAdapter emojiSearchAdapter;
    private SearchField emojiSearchField;
    private int emojiSize;
    private AnimatorSet emojiTabShadowAnimator;
    private ScrollSlidingTabStrip emojiTabs;
    private View emojiTabsShadow;
    private String[] emojiTitles;
    private ImageViewEmoji emojiTouchedView;
    private float emojiTouchedX;
    private float emojiTouchedY;
    private int favTabBum = -2;
    private ArrayList<Document> favouriteStickers = new ArrayList();
    private int featuredStickersHash;
    private boolean firstEmojiAttach = true;
    private boolean firstGifAttach = true;
    private boolean firstStickersAttach = true;
    private ImageView floatingButton;
    private boolean forseMultiwindowLayout;
    private GifAdapter gifAdapter;
    private FrameLayout gifContainer;
    private RecyclerListView gifGridView;
    private ExtendedGridLayoutManager gifLayoutManager;
    private OnItemClickListener gifOnItemClickListener;
    private GifSearchAdapter gifSearchAdapter;
    private SearchField gifSearchField;
    private int groupStickerPackNum;
    private int groupStickerPackPosition;
    private TL_messages_stickerSet groupStickerSet;
    private boolean groupStickersHidden;
    private int hasRecentEmoji = -1;
    private ChatFull info;
    private LongSparseArray<StickerSetCovered> installingStickerSets = new LongSparseArray();
    private boolean isLayout;
    private float lastBottomScrollDy;
    private int lastNotifyHeight;
    private int lastNotifyHeight2;
    private int lastNotifyWidth;
    private String[] lastSearchKeyboardLanguage;
    private int[] location = new int[2];
    private TextView mediaBanTooltip;
    private boolean needEmojiSearch;
    private Object outlineProvider;
    private ViewPager pager;
    private EmojiColorPickerView pickerView;
    private EmojiPopupWindow pickerViewPopup;
    private int popupHeight;
    private int popupWidth;
    private ArrayList<Document> recentGifs = new ArrayList();
    private ArrayList<Document> recentStickers = new ArrayList();
    private int recentTabBum = -2;
    private LongSparseArray<StickerSetCovered> removingStickerSets = new LongSparseArray();
    private int scrolledToTrending;
    private AnimatorSet searchAnimation;
    private ImageView searchButton;
    private int searchFieldHeight = AndroidUtilities.dp(64.0f);
    private View shadowLine;
    private boolean showGifs;
    private Drawable[] stickerIcons;
    private ArrayList<TL_messages_stickerSet> stickerSets = new ArrayList();
    private ImageView stickerSettingsButton;
    private AnimatorSet stickersButtonAnimation;
    private FrameLayout stickersContainer;
    private TextView stickersCounter;
    private StickersGridAdapter stickersGridAdapter;
    private RecyclerListView stickersGridView;
    private GridLayoutManager stickersLayoutManager;
    private int stickersMinusDy;
    private OnItemClickListener stickersOnItemClickListener;
    private SearchField stickersSearchField;
    private StickersSearchGridAdapter stickersSearchGridAdapter;
    private ScrollSlidingTabStrip stickersTab;
    private int stickersTabOffset;
    private Drawable[] tabIcons;
    private View topShadow;
    private TrendingGridAdapter trendingGridAdapter;
    private RecyclerListView trendingGridView;
    private GridLayoutManager trendingLayoutManager;
    private boolean trendingLoaded;
    private int trendingTabNum = -2;
    private PagerSlidingTabStrip typeTabs;
    private ArrayList<View> views = new ArrayList();

    public interface DragListener {
        void onDrag(int i);

        void onDragCancel();

        void onDragEnd(float f);

        void onDragStart();
    }

    private class EmojiColorPickerView extends View {
        private Drawable arrowDrawable = getResources().getDrawable(NUM);
        private int arrowX;
        private Drawable backgroundDrawable = getResources().getDrawable(NUM);
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
        }

        /* Access modifiers changed, original: protected */
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
                    dp = AndroidUtilities.dp(9.0f);
                    if (this.selection == i) {
                        this.rect.set((float) access$2000, (float) (dp - ((int) AndroidUtilities.dpf2(3.5f))), (float) (EmojiView.this.emojiSize + access$2000), (float) ((EmojiView.this.emojiSize + dp) + AndroidUtilities.dp(3.0f)));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.rectPaint);
                    }
                    String str = this.currentEmoji;
                    if (i != 0) {
                        String str2 = i != 1 ? i != 2 ? i != 3 ? i != 4 ? i != 5 ? "" : "🏿" : "🏾" : "🏽" : "🏼" : "🏻";
                        str = EmojiView.addColorToCode(str, str2);
                    }
                    Drawable emojiBigDrawable = Emoji.getEmojiBigDrawable(str);
                    if (emojiBigDrawable != null) {
                        emojiBigDrawable.setBounds(access$2000, dp, EmojiView.this.emojiSize + access$2000, EmojiView.this.emojiSize + dp);
                        emojiBigDrawable.draw(canvas);
                    }
                    i++;
                }
            }
        }
    }

    private class EmojiPopupWindow extends PopupWindow {
        private OnScrollChangedListener mSuperScrollListener;
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
                    this.mSuperScrollListener = (OnScrollChangedListener) EmojiView.superListenerField.get(this);
                    EmojiView.superListenerField.set(this, EmojiView.NOP);
                } catch (Exception unused) {
                    this.mSuperScrollListener = null;
                }
            }
        }

        private void unregisterListener() {
            if (this.mSuperScrollListener != null) {
                ViewTreeObserver viewTreeObserver = this.mViewTreeObserver;
                if (viewTreeObserver != null) {
                    if (viewTreeObserver.isAlive()) {
                        this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                    }
                    this.mViewTreeObserver = null;
                }
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
                FileLog.e(e);
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

    public interface EmojiViewDelegate {

        public final /* synthetic */ class -CC {
            public static boolean $default$isExpanded(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static boolean $default$isSearchOpened(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static void $default$onClearEmojiRecent(EmojiViewDelegate emojiViewDelegate) {
            }

            public static void $default$onGifSelected(EmojiViewDelegate emojiViewDelegate, Object obj, Object obj2) {
            }

            public static void $default$onSearchOpenClose(EmojiViewDelegate emojiViewDelegate, int i) {
            }

            public static void $default$onShowStickerSet(EmojiViewDelegate emojiViewDelegate, StickerSet stickerSet, InputStickerSet inputStickerSet) {
            }

            public static void $default$onStickerSelected(EmojiViewDelegate emojiViewDelegate, Document document, Object obj) {
            }

            public static void $default$onStickerSetAdd(EmojiViewDelegate emojiViewDelegate, StickerSetCovered stickerSetCovered) {
            }

            public static void $default$onStickerSetRemove(EmojiViewDelegate emojiViewDelegate, StickerSetCovered stickerSetCovered) {
            }

            public static void $default$onStickersGroupClick(EmojiViewDelegate emojiViewDelegate, int i) {
            }

            public static void $default$onStickersSettingsClick(EmojiViewDelegate emojiViewDelegate) {
            }

            public static void $default$onTabOpened(EmojiViewDelegate emojiViewDelegate, int i) {
            }
        }

        boolean isExpanded();

        boolean isSearchOpened();

        boolean onBackspace();

        void onClearEmojiRecent();

        void onEmojiSelected(String str);

        void onGifSelected(Object obj, Object obj2);

        void onSearchOpenClose(int i);

        void onShowStickerSet(StickerSet stickerSet, InputStickerSet inputStickerSet);

        void onStickerSelected(Document document, Object obj);

        void onStickerSetAdd(StickerSetCovered stickerSetCovered);

        void onStickerSetRemove(StickerSetCovered stickerSetCovered);

        void onStickersGroupClick(int i);

        void onStickersSettingsClick();

        void onTabOpened(int i);
    }

    private class ImageViewEmoji extends ImageView {
        private boolean isRecent;

        public ImageViewEmoji(Context context) {
            super(context);
            setScaleType(ScaleType.CENTER);
        }

        private void sendEmoji(String str) {
            String str2;
            EmojiView.this.showBottomTab(true, true);
            if (str != null) {
                str2 = str;
            } else {
                str2 = (String) getTag();
            }
            new SpannableStringBuilder().append(str2);
            if (str == null) {
                if (!this.isRecent) {
                    str = (String) Emoji.emojiColor.get(str2);
                    if (str != null) {
                        str2 = EmojiView.addColorToCode(str2, str);
                    }
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
            setMeasuredDimension(MeasureSpec.getSize(i), MeasureSpec.getSize(i));
        }
    }

    private class SearchField extends FrameLayout {
        private View backgroundView;
        private ImageView clearSearchImageView;
        private CloseProgressDrawable2 progressDrawable;
        private View searchBackground;
        private EditTextBoldCursor searchEditText;
        private ImageView searchIconImageView;
        private AnimatorSet shadowAnimator;
        private View shadowView;

        public SearchField(Context context, final int i) {
            super(context);
            this.shadowView = new View(context);
            this.shadowView.setAlpha(0.0f);
            this.shadowView.setTag(Integer.valueOf(1));
            this.shadowView.setBackgroundColor(Theme.getColor("chat_emojiPanelShadowLine"));
            addView(this.shadowView, new LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83));
            this.backgroundView = new View(context);
            this.backgroundView.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
            addView(this.backgroundView, new LayoutParams(-1, EmojiView.this.searchFieldHeight));
            this.searchBackground = new View(context);
            this.searchBackground.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor("chat_emojiSearchBackground")));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 14.0f, 14.0f, 0.0f));
            this.searchIconImageView = new ImageView(context);
            this.searchIconImageView.setScaleType(ScaleType.CENTER);
            this.searchIconImageView.setImageResource(NUM);
            String str = "chat_emojiSearchIcon";
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 14.0f, 0.0f, 0.0f));
            this.clearSearchImageView = new ImageView(context);
            this.clearSearchImageView.setScaleType(ScaleType.CENTER);
            ImageView imageView = this.clearSearchImageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
            this.progressDrawable = closeProgressDrawable2;
            imageView.setImageDrawable(closeProgressDrawable2);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            this.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 14.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new -$$Lambda$EmojiView$SearchField$04evSuS5X_E-yJiP7OOqbVezHUE(this));
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
            this.searchEditText.setHintTextColor(Theme.getColor(str));
            this.searchEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.searchEditText.setBackgroundDrawable(null);
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
                    Object obj = null;
                    Object obj2 = SearchField.this.searchEditText.length() > 0 ? 1 : null;
                    float f = 0.0f;
                    if (SearchField.this.clearSearchImageView.getAlpha() != 0.0f) {
                        obj = 1;
                    }
                    if (obj2 != obj) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (obj2 != null) {
                            f = 1.0f;
                        }
                        animate = animate.alpha(f).setDuration(150).scaleX(obj2 != null ? 1.0f : 0.1f);
                        if (obj2 == null) {
                            f2 = 0.1f;
                        }
                        animate.scaleY(f2).start();
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

        private void showShadow(boolean z, boolean z2) {
            if (!(z && this.shadowView.getTag() == null) && (z || this.shadowView.getTag() == null)) {
                AnimatorSet animatorSet = this.shadowAnimator;
                Object obj = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.shadowAnimator = null;
                }
                View view = this.shadowView;
                if (!z) {
                    obj = Integer.valueOf(1);
                }
                view.setTag(obj);
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
                            SearchField.this.shadowAnimator = null;
                        }
                    });
                    this.shadowAnimator.start();
                } else {
                    View view3 = this.shadowView;
                    if (!z) {
                        f = 0.0f;
                    }
                    view3.setAlpha(f);
                }
            }
        }
    }

    private class EmojiPagesAdapter extends PagerAdapter implements IconTabProvider {
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        private EmojiPagesAdapter() {
        }

        /* synthetic */ EmojiPagesAdapter(EmojiView emojiView, AnonymousClass1 anonymousClass1) {
            this();
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
            if (i != 1) {
                return i != 2 ? null : LocaleController.getString("AccDescrStickers", NUM);
            } else {
                return LocaleController.getString("AccDescrGIFs", NUM);
            }
        }

        public void customOnDraw(Canvas canvas, int i) {
            if (i == 2 && !DataQuery.getInstance(EmojiView.this.currentAccount).getUnreadStickerSets().isEmpty() && EmojiView.this.dotPaint != null) {
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

    private class EmojiGridAdapter extends SelectionAdapter {
        private int itemCount;
        private SparseIntArray positionToSection;
        private SparseIntArray sectionToPosition;

        public long getItemId(int i) {
            return (long) i;
        }

        private EmojiGridAdapter() {
            this.positionToSection = new SparseIntArray();
            this.sectionToPosition = new SparseIntArray();
        }

        /* synthetic */ EmojiGridAdapter(EmojiView emojiView, AnonymousClass1 anonymousClass1) {
            this();
        }

        public int getItemCount() {
            return this.itemCount;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View imageViewEmoji;
            if (i == 0) {
                EmojiView emojiView = EmojiView.this;
                imageViewEmoji = new ImageViewEmoji(emojiView.getContext());
            } else if (i != 1) {
                imageViewEmoji = new View(EmojiView.this.getContext());
                imageViewEmoji.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
            } else {
                imageViewEmoji = new StickerSetNameCell(EmojiView.this.getContext(), true);
            }
            return new Holder(imageViewEmoji);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                String str;
                Object obj;
                ImageViewEmoji imageViewEmoji = (ImageViewEmoji) viewHolder.itemView;
                if (EmojiView.this.needEmojiSearch) {
                    i--;
                }
                itemViewType = Emoji.recentEmoji.size();
                if (i < itemViewType) {
                    str = (String) Emoji.recentEmoji.get(i);
                    obj = str;
                    z = true;
                } else {
                    Object obj2;
                    String str2;
                    int i2 = itemViewType;
                    itemViewType = 0;
                    while (true) {
                        String[][] strArr = EmojiData.dataColored;
                        if (itemViewType >= strArr.length) {
                            obj2 = null;
                            break;
                        }
                        int length = (strArr[itemViewType].length + 1) + i2;
                        if (i < length) {
                            obj2 = strArr[itemViewType][(i - i2) - 1];
                            str2 = (String) Emoji.emojiColor.get(obj2);
                            if (str2 != null) {
                                str2 = EmojiView.addColorToCode(obj2, str2);
                            }
                        } else {
                            itemViewType++;
                            i2 = length;
                        }
                    }
                    str2 = obj2;
                    String str3 = str2;
                    obj = obj2;
                    str = str3;
                }
                imageViewEmoji.setImageDrawable(Emoji.getEmojiBigDrawable(str), z);
                imageViewEmoji.setTag(obj);
                imageViewEmoji.setContentDescription(str);
            } else if (itemViewType == 1) {
                ((StickerSetNameCell) viewHolder.itemView).setText(EmojiView.this.emojiTitles[this.positionToSection.get(i)], 0);
            }
        }

        public int getItemViewType(int i) {
            if (EmojiView.this.needEmojiSearch && i == 0) {
                return 2;
            }
            return this.positionToSection.indexOfKey(i) >= 0 ? 1 : 0;
        }

        public void notifyDataSetChanged() {
            this.positionToSection.clear();
            this.itemCount = Emoji.recentEmoji.size() + EmojiView.this.needEmojiSearch;
            for (int i = 0; i < EmojiData.dataColored.length; i++) {
                this.positionToSection.put(this.itemCount, i);
                this.sectionToPosition.put(i, this.itemCount);
                this.itemCount += EmojiData.dataColored[i].length + 1;
            }
            EmojiView.this.updateEmojiTabs();
            super.notifyDataSetChanged();
        }
    }

    private class EmojiSearchAdapter extends SelectionAdapter {
        private String lastSearchAlias;
        private String lastSearchEmojiString;
        private ArrayList<KeywordResult> result;
        private Runnable searchRunnable;
        private boolean searchWas;

        private EmojiSearchAdapter() {
            this.result = new ArrayList();
        }

        /* synthetic */ EmojiSearchAdapter(EmojiView emojiView, AnonymousClass1 anonymousClass1) {
            this();
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

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View imageViewEmoji;
            if (i == 0) {
                EmojiView emojiView = EmojiView.this;
                imageViewEmoji = new ImageViewEmoji(emojiView.getContext());
            } else if (i != 1) {
                imageViewEmoji = new FrameLayout(EmojiView.this.getContext()) {
                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        View view = (View) EmojiView.this.getParent();
                        if (view != null) {
                            i2 = (int) (((float) view.getMeasuredHeight()) - EmojiView.this.getY());
                        } else {
                            i2 = AndroidUtilities.dp(120.0f);
                        }
                        super.onMeasure(i, MeasureSpec.makeMeasureSpec(i2 - EmojiView.this.searchFieldHeight, NUM));
                    }
                };
                TextView textView = new TextView(EmojiView.this.getContext());
                textView.setText(LocaleController.getString("NoEmojiFound", NUM));
                textView.setTextSize(1, 16.0f);
                String str = "chat_emojiPanelEmptyText";
                textView.setTextColor(Theme.getColor(str));
                imageViewEmoji.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 10.0f, 0.0f, 0.0f));
                ImageView imageView = new ImageView(EmojiView.this.getContext());
                imageView.setScaleType(ScaleType.CENTER);
                imageView.setImageResource(NUM);
                imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
                imageViewEmoji.addView(imageView, LayoutHelper.createFrame(48, 48, 85));
                imageView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        final boolean[] zArr = new boolean[1];
                        final Builder builder = new Builder(EmojiView.this.getContext());
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
                        textView = new TextView(EmojiView.this.getContext());
                        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EmojiSuggestionsInfo", NUM)));
                        textView.setTextSize(1, 15.0f);
                        textView.setTextColor(Theme.getColor("dialogTextBlack"));
                        textView.setGravity(LocaleController.isRTL ? 5 : 3);
                        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 51, 0, 11, 0, 0));
                        textView = new TextView(EmojiView.this.getContext());
                        Object[] objArr = new Object[1];
                        objArr[0] = EmojiSearchAdapter.this.lastSearchAlias != null ? EmojiSearchAdapter.this.lastSearchAlias : EmojiView.this.lastSearchKeyboardLanguage;
                        textView.setText(LocaleController.formatString("EmojiSuggestionsUrl", NUM, objArr));
                        textView.setTextSize(1, 15.0f);
                        textView.setTextColor(Theme.getColor("dialogTextLink"));
                        if (!LocaleController.isRTL) {
                            i = 3;
                        }
                        textView.setGravity(i);
                        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 51, 0, 18, 0, 16));
                        textView.setOnClickListener(new OnClickListener() {
                            public void onClick(View view) {
                                boolean[] zArr = zArr;
                                if (!zArr[0]) {
                                    zArr[0] = true;
                                    AlertDialog[] alertDialogArr = new AlertDialog[]{new AlertDialog(EmojiView.this.getContext(), 3)};
                                    TL_messages_getEmojiURL tL_messages_getEmojiURL = new TL_messages_getEmojiURL();
                                    tL_messages_getEmojiURL.lang_code = EmojiSearchAdapter.this.lastSearchAlias != null ? EmojiSearchAdapter.this.lastSearchAlias : EmojiView.this.lastSearchKeyboardLanguage[0];
                                    AndroidUtilities.runOnUIThread(new -$$Lambda$EmojiView$EmojiSearchAdapter$2$1$TwKjymEFVh1Ja7xLl-9KmhIF6eg(this, alertDialogArr, ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tL_messages_getEmojiURL, new -$$Lambda$EmojiView$EmojiSearchAdapter$2$1$vESTLZoOoKr-laMmjTYW6gGhW9U(this, alertDialogArr, builder))), 1000);
                                }
                            }

                            public /* synthetic */ void lambda$onClick$1$EmojiView$EmojiSearchAdapter$2$1(AlertDialog[] alertDialogArr, Builder builder, TLObject tLObject, TL_error tL_error) {
                                AndroidUtilities.runOnUIThread(new -$$Lambda$EmojiView$EmojiSearchAdapter$2$1$k1LSrqBP6ptq3f6NASRqH9-nzpY(this, alertDialogArr, tLObject, builder));
                            }

                            public /* synthetic */ void lambda$null$0$EmojiView$EmojiSearchAdapter$2$1(AlertDialog[] alertDialogArr, TLObject tLObject, Builder builder) {
                                try {
                                    alertDialogArr[0].dismiss();
                                } catch (Throwable unused) {
                                }
                                alertDialogArr[0] = null;
                                if (tLObject instanceof TL_emojiURL) {
                                    Browser.openUrl(EmojiView.this.getContext(), ((TL_emojiURL) tLObject).url);
                                    builder.getDismissRunnable().run();
                                }
                            }

                            public /* synthetic */ void lambda$onClick$3$EmojiView$EmojiSearchAdapter$2$1(AlertDialog[] alertDialogArr, int i) {
                                if (alertDialogArr[0] != null) {
                                    alertDialogArr[0].setOnCancelListener(new -$$Lambda$EmojiView$EmojiSearchAdapter$2$1$BvShOFTdaqiZ1q6_dGuWuV6cZ2o(this, i));
                                    alertDialogArr[0].show();
                                }
                            }

                            public /* synthetic */ void lambda$null$2$EmojiView$EmojiSearchAdapter$2$1(int i, DialogInterface dialogInterface) {
                                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(i, true);
                            }
                        });
                        builder.setCustomView(linearLayout);
                        builder.show();
                    }
                });
                imageViewEmoji.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            } else {
                imageViewEmoji = new View(EmojiView.this.getContext());
                imageViewEmoji.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
            }
            return new Holder(imageViewEmoji);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                String str;
                boolean z;
                ImageViewEmoji imageViewEmoji = (ImageViewEmoji) viewHolder.itemView;
                i--;
                if (!this.result.isEmpty() || this.searchWas) {
                    str = ((KeywordResult) this.result.get(i)).emoji;
                    z = false;
                } else {
                    str = (String) Emoji.recentEmoji.get(i);
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
            return (i == 1 && this.searchWas && this.result.isEmpty()) ? 2 : 0;
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
                AnonymousClass3 anonymousClass3 = new Runnable() {
                    public void run() {
                        EmojiView.this.emojiSearchField.progressDrawable.startAnimation();
                        final String access$10100 = EmojiSearchAdapter.this.lastSearchEmojiString;
                        String[] currentKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                        if (!Arrays.equals(EmojiView.this.lastSearchKeyboardLanguage, currentKeyboardLanguage)) {
                            DataQuery.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(currentKeyboardLanguage);
                        }
                        EmojiView.this.lastSearchKeyboardLanguage = currentKeyboardLanguage;
                        DataQuery.getInstance(EmojiView.this.currentAccount).getEmojiSuggestions(EmojiView.this.lastSearchKeyboardLanguage, EmojiSearchAdapter.this.lastSearchEmojiString, false, new KeywordResultCallback() {
                            public void run(ArrayList<KeywordResult> arrayList, String str) {
                                if (access$10100.equals(EmojiSearchAdapter.this.lastSearchEmojiString)) {
                                    EmojiSearchAdapter.this.lastSearchAlias = str;
                                    EmojiView.this.emojiSearchField.progressDrawable.stopAnimation();
                                    EmojiSearchAdapter.this.searchWas = true;
                                    if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiSearchAdapter) {
                                        EmojiView.this.emojiGridView.setAdapter(EmojiView.this.emojiSearchAdapter);
                                    }
                                    EmojiSearchAdapter.this.result = arrayList;
                                    EmojiSearchAdapter.this.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                };
                this.searchRunnable = anonymousClass3;
                AndroidUtilities.runOnUIThread(anonymousClass3, 300);
            }
        }
    }

    private class GifAdapter extends SelectionAdapter {
        private Context mContext;

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return i == 0 ? 1 : 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public GifAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return EmojiView.this.recentGifs.size() + 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new View(EmojiView.this.getContext());
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
            } else {
                view = new ContextLinkCell(this.mContext);
                view.setContentDescription(LocaleController.getString("AttachGif", NUM));
                view.setCanPreviewGif(true);
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                Document document = (Document) EmojiView.this.recentGifs.get(i - 1);
                if (document != null) {
                    ((ContextLinkCell) viewHolder.itemView).setGif(document, false);
                }
            }
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
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

    private class GifSearchAdapter extends SelectionAdapter {
        private User bot;
        private String lastSearchImageString;
        private Context mContext;
        private String nextSearchOffset;
        private int reqId;
        private ArrayList<BotInlineResult> results = new ArrayList();
        private HashMap<String, BotInlineResult> resultsMap = new HashMap();
        private boolean searchEndReached;
        private Runnable searchRunnable;
        private boolean searchingUser;

        public boolean isEnabled(ViewHolder viewHolder) {
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

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View contextLinkCell;
            if (i == 0) {
                contextLinkCell = new ContextLinkCell(this.mContext);
                contextLinkCell.setContentDescription(LocaleController.getString("AttachGif", NUM));
                contextLinkCell.setCanPreviewGif(true);
            } else if (i != 1) {
                contextLinkCell = new FrameLayout(EmojiView.this.getContext()) {
                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, MeasureSpec.makeMeasureSpec((int) (((float) (((EmojiView.this.gifGridView.getMeasuredHeight() - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3)) * 1.7f), NUM));
                    }
                };
                ImageView imageView = new ImageView(EmojiView.this.getContext());
                imageView.setScaleType(ScaleType.CENTER);
                imageView.setImageResource(NUM);
                String str = "chat_emojiPanelEmptyText";
                imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
                contextLinkCell.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 59.0f));
                TextView textView = new TextView(EmojiView.this.getContext());
                textView.setText(LocaleController.getString("NoGIFsFound", NUM));
                textView.setTextSize(1, 16.0f);
                textView.setTextColor(Theme.getColor(str));
                contextLinkCell.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 9.0f));
                contextLinkCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            } else {
                contextLinkCell = new View(EmojiView.this.getContext());
                contextLinkCell.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
            }
            return new Holder(contextLinkCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ((ContextLinkCell) viewHolder.itemView).setLink((BotInlineResult) this.results.get(i - 1), true, false, false);
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
                AnonymousClass2 anonymousClass2 = new Runnable() {
                    public void run() {
                        GifSearchAdapter.this.search(str, "", true);
                    }
                };
                this.searchRunnable = anonymousClass2;
                AndroidUtilities.runOnUIThread(anonymousClass2, 300);
            }
        }

        private void searchBotUser() {
            if (!this.searchingUser) {
                this.searchingUser = true;
                TL_contacts_resolveUsername tL_contacts_resolveUsername = new TL_contacts_resolveUsername();
                tL_contacts_resolveUsername.username = MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot;
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tL_contacts_resolveUsername, new -$$Lambda$EmojiView$GifSearchAdapter$rB6iGH2IIIWpfXKf_CfIBRZyOE8(this));
            }
        }

        public /* synthetic */ void lambda$searchBotUser$1$EmojiView$GifSearchAdapter(TLObject tLObject, TL_error tL_error) {
            if (tLObject != null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$EmojiView$GifSearchAdapter$XYVthzC0iAXb1tv95YkceAYL3QE(this, tLObject));
            }
        }

        public /* synthetic */ void lambda$null$0$EmojiView$GifSearchAdapter(TLObject tLObject) {
            TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TL_contacts_resolvedPeer) tLObject;
            MessagesController.getInstance(EmojiView.this.currentAccount).putUsers(tL_contacts_resolvedPeer.users, false);
            MessagesController.getInstance(EmojiView.this.currentAccount).putChats(tL_contacts_resolvedPeer.chats, false);
            MessagesStorage.getInstance(EmojiView.this.currentAccount).putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, true, true);
            String str = this.lastSearchImageString;
            this.lastSearchImageString = null;
            search(str, "", false);
        }

        private void search(String str, String str2, boolean z) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            this.lastSearchImageString = str;
            TLObject userOrChat = MessagesController.getInstance(EmojiView.this.currentAccount).getUserOrChat(MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot);
            if (userOrChat instanceof User) {
                if (TextUtils.isEmpty(str2)) {
                    EmojiView.this.gifSearchField.progressDrawable.startAnimation();
                }
                this.bot = (User) userOrChat;
                TL_messages_getInlineBotResults tL_messages_getInlineBotResults = new TL_messages_getInlineBotResults();
                if (str == null) {
                    str = "";
                }
                tL_messages_getInlineBotResults.query = str;
                tL_messages_getInlineBotResults.bot = MessagesController.getInstance(EmojiView.this.currentAccount).getInputUser(this.bot);
                tL_messages_getInlineBotResults.offset = str2;
                tL_messages_getInlineBotResults.peer = new TL_inputPeerEmpty();
                this.reqId = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tL_messages_getInlineBotResults, new -$$Lambda$EmojiView$GifSearchAdapter$rHXwwVWZa9pK-feyWosndOy6a0o(this, tL_messages_getInlineBotResults, str2), 2);
                return;
            }
            if (z) {
                searchBotUser();
                EmojiView.this.gifSearchField.progressDrawable.startAnimation();
            }
        }

        public /* synthetic */ void lambda$search$3$EmojiView$GifSearchAdapter(TL_messages_getInlineBotResults tL_messages_getInlineBotResults, String str, TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$EmojiView$GifSearchAdapter$T00z6XPCmLZsEfaqGzAOZW0Q-C8(this, tL_messages_getInlineBotResults, str, tLObject));
        }

        public /* synthetic */ void lambda$null$2$EmojiView$GifSearchAdapter(TL_messages_getInlineBotResults tL_messages_getInlineBotResults, String str, TLObject tLObject) {
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
                if (tLObject instanceof messages_BotResults) {
                    int size = this.results.size();
                    messages_BotResults messages_botresults = (messages_BotResults) tLObject;
                    this.nextSearchOffset = messages_botresults.next_offset;
                    int i = 0;
                    for (int i2 = 0; i2 < messages_botresults.results.size(); i2++) {
                        BotInlineResult botInlineResult = (BotInlineResult) messages_botresults.results.get(i2);
                        if (!this.resultsMap.containsKey(botInlineResult.id)) {
                            botInlineResult.query_id = messages_botresults.query_id;
                            this.results.add(botInlineResult);
                            this.resultsMap.put(botInlineResult.id, botInlineResult);
                            i++;
                        }
                    }
                    if (size == this.results.size()) {
                        z = true;
                    }
                    this.searchEndReached = z;
                    if (i != 0) {
                        if (size != 0) {
                            notifyItemChanged(size);
                        }
                        notifyItemRangeInserted(size + 1, i);
                        return;
                    } else if (this.results.isEmpty()) {
                        notifyDataSetChanged();
                        return;
                    } else {
                        return;
                    }
                }
                notifyDataSetChanged();
            }
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }

    private class StickersGridAdapter extends SelectionAdapter {
        private SparseArray<Object> cache = new SparseArray();
        private SparseArray<Object> cacheParents = new SparseArray();
        private Context context;
        private HashMap<Object, Integer> packStartPosition = new HashMap();
        private SparseIntArray positionToRow = new SparseIntArray();
        private SparseArray<Object> rowStartPack = new SparseArray();
        private int stickersPerRow;
        private int totalItems;

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public StickersGridAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            int i = this.totalItems;
            return i != 0 ? i + 1 : 0;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public int getPositionForPack(Object obj) {
            Integer num = (Integer) this.packStartPosition.get(obj);
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
            if (obj instanceof Document) {
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
            i = this.positionToRow.get(i, Integer.MIN_VALUE);
            if (i == Integer.MIN_VALUE) {
                return (EmojiView.this.stickerSets.size() - 1) + EmojiView.this.stickersTabOffset;
            }
            Object obj = this.rowStartPack.get(i);
            if (!(obj instanceof String)) {
                return EmojiView.this.stickerSets.indexOf((TL_messages_stickerSet) obj) + EmojiView.this.stickersTabOffset;
            } else if ("recent".equals(obj)) {
                return EmojiView.this.recentTabBum;
            } else {
                return EmojiView.this.favTabBum;
            }
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$EmojiView$StickersGridAdapter(View view) {
            if (EmojiView.this.groupStickerSet == null) {
                Editor edit = MessagesController.getEmojiSettings(EmojiView.this.currentAccount).edit();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("group_hide_stickers_");
                stringBuilder.append(EmojiView.this.info.id);
                edit.putLong(stringBuilder.toString(), EmojiView.this.info.stickerset != null ? EmojiView.this.info.stickerset.id : 0).commit();
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

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View anonymousClass1;
            if (i == 0) {
                anonymousClass1 = new StickerEmojiCell(this.context) {
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                    }
                };
            } else if (i == 1) {
                anonymousClass1 = new EmptyCell(this.context);
            } else if (i == 2) {
                anonymousClass1 = new StickerSetNameCell(this.context, false);
                anonymousClass1.setOnIconClickListener(new -$$Lambda$EmojiView$StickersGridAdapter$LJAPCN9WKCLASSNAMEC6I4z-4Z05VncJg(this));
            } else if (i == 3) {
                anonymousClass1 = new StickerSetGroupInfoCell(this.context);
                anonymousClass1.setAddOnClickListener(new -$$Lambda$EmojiView$StickersGridAdapter$rEbSNlpXftcal36it1dYPmUfJQ8(this));
                anonymousClass1.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            } else if (i != 4) {
                anonymousClass1 = null;
            } else {
                anonymousClass1 = new View(this.context);
                anonymousClass1.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
            }
            return new Holder(anonymousClass1);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType != 0) {
                ArrayList arrayList = null;
                if (itemViewType == 1) {
                    EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
                    if (i == this.totalItems) {
                        i = this.positionToRow.get(i - 1, Integer.MIN_VALUE);
                        if (i == Integer.MIN_VALUE) {
                            emptyCell.setHeight(1);
                            return;
                        }
                        Object obj = this.rowStartPack.get(i);
                        if (obj instanceof TL_messages_stickerSet) {
                            arrayList = ((TL_messages_stickerSet) obj).documents;
                        } else if (obj instanceof String) {
                            if ("recent".equals(obj)) {
                                arrayList = EmojiView.this.recentStickers;
                            } else {
                                arrayList = EmojiView.this.favouriteStickers;
                            }
                        }
                        if (arrayList == null) {
                            emptyCell.setHeight(1);
                            return;
                        } else if (arrayList.isEmpty()) {
                            emptyCell.setHeight(AndroidUtilities.dp(8.0f));
                            return;
                        } else {
                            i = EmojiView.this.pager.getHeight() - (((int) Math.ceil((double) (((float) arrayList.size()) / ((float) this.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                            if (i <= 0) {
                                i = 1;
                            }
                            emptyCell.setHeight(i);
                            return;
                        }
                    }
                    emptyCell.setHeight(AndroidUtilities.dp(82.0f));
                    return;
                } else if (itemViewType == 2) {
                    StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                    if (i == EmojiView.this.groupStickerPackPosition) {
                        Chat chat;
                        i = (EmojiView.this.groupStickersHidden && EmojiView.this.groupStickerSet == null) ? 0 : EmojiView.this.groupStickerSet != null ? NUM : NUM;
                        if (EmojiView.this.info != null) {
                            chat = MessagesController.getInstance(EmojiView.this.currentAccount).getChat(Integer.valueOf(EmojiView.this.info.id));
                        }
                        Object[] objArr = new Object[1];
                        objArr[0] = chat != null ? chat.title : "Group Stickers";
                        stickerSetNameCell.setText(LocaleController.formatString("CurrentGroupStickers", NUM, objArr), i);
                        return;
                    }
                    ArrayList arrayList2 = this.cache.get(i);
                    if (arrayList2 instanceof TL_messages_stickerSet) {
                        StickerSet stickerSet = ((TL_messages_stickerSet) arrayList2).set;
                        if (stickerSet != null) {
                            stickerSetNameCell.setText(stickerSet.title, 0);
                            return;
                        }
                        return;
                    } else if (arrayList2 == EmojiView.this.recentStickers) {
                        stickerSetNameCell.setText(LocaleController.getString("RecentStickers", NUM), 0);
                        return;
                    } else if (arrayList2 == EmojiView.this.favouriteStickers) {
                        stickerSetNameCell.setText(LocaleController.getString("FavoriteStickers", NUM), 0);
                        return;
                    } else {
                        return;
                    }
                } else if (itemViewType == 3) {
                    StickerSetGroupInfoCell stickerSetGroupInfoCell = (StickerSetGroupInfoCell) viewHolder.itemView;
                    if (i == this.totalItems - 1) {
                        z = true;
                    }
                    stickerSetGroupInfoCell.setIsLast(z);
                    return;
                } else {
                    return;
                }
            }
            Document document = (Document) this.cache.get(i);
            StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) viewHolder.itemView;
            stickerEmojiCell.setSticker(document, this.cacheParents.get(i), false);
            if (EmojiView.this.recentStickers.contains(document) || EmojiView.this.favouriteStickers.contains(document)) {
                z = true;
            }
            stickerEmojiCell.setRecent(z);
        }

        public void notifyDataSetChanged() {
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
            ArrayList access$10700 = EmojiView.this.stickerSets;
            int i = -3;
            int i2 = -3;
            int i3 = 0;
            while (i2 < access$10700.size()) {
                int i4;
                if (i2 == i) {
                    SparseArray sparseArray = this.cache;
                    i4 = this.totalItems;
                    this.totalItems = i4 + 1;
                    sparseArray.put(i4, "search");
                    i3++;
                } else {
                    ArrayList access$11200;
                    Object obj;
                    String str = "recent";
                    String str2 = "fav";
                    Object obj2 = null;
                    if (i2 == -2) {
                        access$11200 = EmojiView.this.favouriteStickers;
                        this.packStartPosition.put(str2, Integer.valueOf(this.totalItems));
                        obj = str2;
                    } else if (i2 == -1) {
                        access$11200 = EmojiView.this.recentStickers;
                        this.packStartPosition.put(str, Integer.valueOf(this.totalItems));
                        obj = str;
                    } else {
                        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) access$10700.get(i2);
                        ArrayList arrayList = tL_messages_stickerSet.documents;
                        this.packStartPosition.put(tL_messages_stickerSet, Integer.valueOf(this.totalItems));
                        obj2 = tL_messages_stickerSet;
                        access$11200 = arrayList;
                        obj = null;
                    }
                    if (i2 == EmojiView.this.groupStickerPackNum) {
                        EmojiView.this.groupStickerPackPosition = this.totalItems;
                        if (access$11200.isEmpty()) {
                            this.rowStartPack.put(i3, obj2);
                            int i5 = i3 + 1;
                            this.positionToRow.put(this.totalItems, i3);
                            this.rowStartPack.put(i5, obj2);
                            i4 = i5 + 1;
                            this.positionToRow.put(this.totalItems + 1, i5);
                            SparseArray sparseArray2 = this.cache;
                            int i6 = this.totalItems;
                            this.totalItems = i6 + 1;
                            sparseArray2.put(i6, obj2);
                            sparseArray2 = this.cache;
                            i6 = this.totalItems;
                            this.totalItems = i6 + 1;
                            sparseArray2.put(i6, "group");
                            i3 = i4;
                        }
                    }
                    if (!access$11200.isEmpty()) {
                        int ceil = (int) Math.ceil((double) (((float) access$11200.size()) / ((float) this.stickersPerRow)));
                        if (obj2 != null) {
                            this.cache.put(this.totalItems, obj2);
                        } else {
                            this.cache.put(this.totalItems, access$11200);
                        }
                        this.positionToRow.put(this.totalItems, i3);
                        int i7 = 0;
                        while (i7 < access$11200.size()) {
                            int i8 = i7 + 1;
                            int i9 = this.totalItems + i8;
                            this.cache.put(i9, access$11200.get(i7));
                            if (obj2 != null) {
                                this.cacheParents.put(i9, obj2);
                            } else {
                                this.cacheParents.put(i9, obj);
                            }
                            this.positionToRow.put(this.totalItems + i8, (i3 + 1) + (i7 / this.stickersPerRow));
                            i7 = i8;
                        }
                        measuredWidth = 0;
                        while (true) {
                            i = ceil + 1;
                            if (measuredWidth >= i) {
                                break;
                            }
                            if (obj2 != null) {
                                this.rowStartPack.put(i3 + measuredWidth, obj2);
                            } else {
                                this.rowStartPack.put(i3 + measuredWidth, i2 == -1 ? str : str2);
                            }
                            measuredWidth++;
                        }
                        this.totalItems += (ceil * this.stickersPerRow) + 1;
                        i3 += i;
                    }
                }
                i2++;
                i = -3;
            }
            super.notifyDataSetChanged();
        }
    }

    private class StickersSearchGridAdapter extends SelectionAdapter {
        private SparseArray<Object> cache = new SparseArray();
        private SparseArray<Object> cacheParent = new SparseArray();
        boolean cleared;
        private Context context;
        private ArrayList<ArrayList<Document>> emojiArrays = new ArrayList();
        private int emojiSearchId;
        private HashMap<ArrayList<Document>, String> emojiStickers = new HashMap();
        private ArrayList<TL_messages_stickerSet> localPacks = new ArrayList();
        private HashMap<TL_messages_stickerSet, Integer> localPacksByName = new HashMap();
        private HashMap<TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap();
        private SparseArray<String> positionToEmoji = new SparseArray();
        private SparseIntArray positionToRow = new SparseIntArray();
        private SparseArray<StickerSetCovered> positionsToSets = new SparseArray();
        private int reqId;
        private int reqId2;
        private SparseArray<Object> rowStartPack = new SparseArray();
        private String searchQuery;
        private Runnable searchRunnable = new Runnable() {
            private void clear() {
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

            /* JADX WARNING: Missing block: B:14:0x007c, code skipped:
            if (r8.charAt(r9) <= 57343) goto L_0x0098;
     */
            /* JADX WARNING: Missing block: B:20:0x0096, code skipped:
            if (r8.charAt(r9) != 9794) goto L_0x00b3;
     */
            public void run() {
                /*
                r13 = this;
                r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r0 = r0.searchQuery;
                r0 = android.text.TextUtils.isEmpty(r0);
                if (r0 == 0) goto L_0x000d;
            L_0x000c:
                return;
            L_0x000d:
                r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r0 = org.telegram.ui.Components.EmojiView.this;
                r0 = r0.stickersSearchField;
                r0 = r0.progressDrawable;
                r0.startAnimation();
                r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r1 = 0;
                r0.cleared = r1;
                r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.access$13304(r0);
                r2 = new java.util.ArrayList;
                r2.<init>(r1);
                r3 = new android.util.LongSparseArray;
                r3.<init>(r1);
                r4 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r4 = org.telegram.ui.Components.EmojiView.this;
                r4 = r4.currentAccount;
                r4 = org.telegram.messenger.DataQuery.getInstance(r4);
                r4 = r4.getAllStickers();
                r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r5 = r5.searchQuery;
                r5 = r5.length();
                r6 = 14;
                r7 = 1;
                if (r5 > r6) goto L_0x0123;
            L_0x004e:
                r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r5 = r5.searchQuery;
                r6 = r5.length();
                r8 = r5;
                r5 = 0;
            L_0x005a:
                if (r5 >= r6) goto L_0x00da;
            L_0x005c:
                r9 = r6 + -1;
                r10 = 2;
                if (r5 >= r9) goto L_0x00b3;
            L_0x0061:
                r9 = r8.charAt(r5);
                r11 = 55356; // 0xd83c float:7.757E-41 double:2.73495E-319;
                if (r9 != r11) goto L_0x007e;
            L_0x006a:
                r9 = r5 + 1;
                r11 = r8.charAt(r9);
                r12 = 57339; // 0xdffb float:8.0349E-41 double:2.8329E-319;
                if (r11 < r12) goto L_0x007e;
            L_0x0075:
                r9 = r8.charAt(r9);
                r11 = 57343; // 0xdfff float:8.0355E-41 double:2.8331E-319;
                if (r9 <= r11) goto L_0x0098;
            L_0x007e:
                r9 = r8.charAt(r5);
                r11 = 8205; // 0x200d float:1.1498E-41 double:4.054E-320;
                if (r9 != r11) goto L_0x00b3;
            L_0x0086:
                r9 = r5 + 1;
                r11 = r8.charAt(r9);
                r12 = 9792; // 0x2640 float:1.3722E-41 double:4.838E-320;
                if (r11 == r12) goto L_0x0098;
            L_0x0090:
                r9 = r8.charAt(r9);
                r11 = 9794; // 0x2642 float:1.3724E-41 double:4.839E-320;
                if (r9 != r11) goto L_0x00b3;
            L_0x0098:
                r9 = new java.lang.CharSequence[r10];
                r10 = r8.subSequence(r1, r5);
                r9[r1] = r10;
                r10 = r5 + 2;
                r11 = r8.length();
                r8 = r8.subSequence(r10, r11);
                r9[r7] = r8;
                r8 = android.text.TextUtils.concat(r9);
                r6 = r6 + -2;
                goto L_0x00d6;
            L_0x00b3:
                r9 = r8.charAt(r5);
                r11 = 65039; // 0xfe0f float:9.1139E-41 double:3.21335E-319;
                if (r9 != r11) goto L_0x00d8;
            L_0x00bc:
                r9 = new java.lang.CharSequence[r10];
                r10 = r8.subSequence(r1, r5);
                r9[r1] = r10;
                r10 = r5 + 1;
                r11 = r8.length();
                r8 = r8.subSequence(r10, r11);
                r9[r7] = r8;
                r8 = android.text.TextUtils.concat(r9);
                r6 = r6 + -1;
            L_0x00d6:
                r5 = r5 + -1;
            L_0x00d8:
                r5 = r5 + r7;
                goto L_0x005a;
            L_0x00da:
                if (r4 == 0) goto L_0x00e7;
            L_0x00dc:
                r5 = r8.toString();
                r5 = r4.get(r5);
                r5 = (java.util.ArrayList) r5;
                goto L_0x00e8;
            L_0x00e7:
                r5 = 0;
            L_0x00e8:
                if (r5 == 0) goto L_0x0123;
            L_0x00ea:
                r6 = r5.isEmpty();
                if (r6 != 0) goto L_0x0123;
            L_0x00f0:
                r13.clear();
                r2.addAll(r5);
                r6 = r5.size();
                r8 = 0;
            L_0x00fb:
                if (r8 >= r6) goto L_0x010b;
            L_0x00fd:
                r9 = r5.get(r8);
                r9 = (org.telegram.tgnet.TLRPC.Document) r9;
                r10 = r9.id;
                r3.put(r10, r9);
                r8 = r8 + 1;
                goto L_0x00fb;
            L_0x010b:
                r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r5 = r5.emojiStickers;
                r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r6 = r6.searchQuery;
                r5.put(r2, r6);
                r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r5 = r5.emojiArrays;
                r5.add(r2);
            L_0x0123:
                if (r4 == 0) goto L_0x0181;
            L_0x0125:
                r5 = r4.isEmpty();
                if (r5 != 0) goto L_0x0181;
            L_0x012b:
                r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r5 = r5.searchQuery;
                r5 = r5.length();
                if (r5 <= r7) goto L_0x0181;
            L_0x0137:
                r5 = org.telegram.messenger.AndroidUtilities.getCurrentKeyboardLanguage();
                r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r6 = org.telegram.ui.Components.EmojiView.this;
                r6 = r6.lastSearchKeyboardLanguage;
                r6 = java.util.Arrays.equals(r6, r5);
                if (r6 != 0) goto L_0x0158;
            L_0x0149:
                r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r6 = org.telegram.ui.Components.EmojiView.this;
                r6 = r6.currentAccount;
                r6 = org.telegram.messenger.DataQuery.getInstance(r6);
                r6.fetchNewEmojiKeywords(r5);
            L_0x0158:
                r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r6 = org.telegram.ui.Components.EmojiView.this;
                r6.lastSearchKeyboardLanguage = r5;
                r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r5 = org.telegram.ui.Components.EmojiView.this;
                r5 = r5.currentAccount;
                r5 = org.telegram.messenger.DataQuery.getInstance(r5);
                r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r6 = org.telegram.ui.Components.EmojiView.this;
                r6 = r6.lastSearchKeyboardLanguage;
                r8 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r8 = r8.searchQuery;
                r9 = new org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$1;
                r9.<init>(r0, r4);
                r5.getEmojiSuggestions(r6, r8, r1, r9);
            L_0x0181:
                r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r0 = org.telegram.ui.Components.EmojiView.this;
                r0 = r0.currentAccount;
                r0 = org.telegram.messenger.DataQuery.getInstance(r0);
                r0 = r0.getStickerSets(r1);
                r4 = r0.size();
                r5 = 0;
            L_0x0196:
                r6 = 32;
                if (r5 >= r4) goto L_0x021d;
            L_0x019a:
                r8 = r0.get(r5);
                r8 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r8;
                r9 = r8.set;
                r9 = r9.title;
                r9 = r9.toLowerCase();
                r10 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r10 = r10.searchQuery;
                r9 = r9.indexOf(r10);
                if (r9 < 0) goto L_0x01dc;
            L_0x01b4:
                if (r9 == 0) goto L_0x01c2;
            L_0x01b6:
                r10 = r8.set;
                r10 = r10.title;
                r11 = r9 + -1;
                r10 = r10.charAt(r11);
                if (r10 != r6) goto L_0x0219;
            L_0x01c2:
                r13.clear();
                r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r6 = r6.localPacks;
                r6.add(r8);
                r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r6 = r6.localPacksByName;
                r9 = java.lang.Integer.valueOf(r9);
                r6.put(r8, r9);
                goto L_0x0219;
            L_0x01dc:
                r9 = r8.set;
                r9 = r9.short_name;
                if (r9 == 0) goto L_0x0219;
            L_0x01e2:
                r9 = r9.toLowerCase();
                r10 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r10 = r10.searchQuery;
                r9 = r9.indexOf(r10);
                if (r9 < 0) goto L_0x0219;
            L_0x01f2:
                if (r9 == 0) goto L_0x0200;
            L_0x01f4:
                r10 = r8.set;
                r10 = r10.short_name;
                r9 = r9 + -1;
                r9 = r10.charAt(r9);
                if (r9 != r6) goto L_0x0219;
            L_0x0200:
                r13.clear();
                r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r6 = r6.localPacks;
                r6.add(r8);
                r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r6 = r6.localPacksByShortName;
                r9 = java.lang.Boolean.valueOf(r7);
                r6.put(r8, r9);
            L_0x0219:
                r5 = r5 + 1;
                goto L_0x0196;
            L_0x021d:
                r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r0 = org.telegram.ui.Components.EmojiView.this;
                r0 = r0.currentAccount;
                r0 = org.telegram.messenger.DataQuery.getInstance(r0);
                r4 = 3;
                r0 = r0.getStickerSets(r4);
                r4 = r0.size();
                r5 = 0;
            L_0x0233:
                if (r5 >= r4) goto L_0x02b8;
            L_0x0235:
                r8 = r0.get(r5);
                r8 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r8;
                r9 = r8.set;
                r9 = r9.title;
                r9 = r9.toLowerCase();
                r10 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r10 = r10.searchQuery;
                r9 = r9.indexOf(r10);
                if (r9 < 0) goto L_0x0277;
            L_0x024f:
                if (r9 == 0) goto L_0x025d;
            L_0x0251:
                r10 = r8.set;
                r10 = r10.title;
                r11 = r9 + -1;
                r10 = r10.charAt(r11);
                if (r10 != r6) goto L_0x02b4;
            L_0x025d:
                r13.clear();
                r10 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r10 = r10.localPacks;
                r10.add(r8);
                r10 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r10 = r10.localPacksByName;
                r9 = java.lang.Integer.valueOf(r9);
                r10.put(r8, r9);
                goto L_0x02b4;
            L_0x0277:
                r9 = r8.set;
                r9 = r9.short_name;
                if (r9 == 0) goto L_0x02b4;
            L_0x027d:
                r9 = r9.toLowerCase();
                r10 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r10 = r10.searchQuery;
                r9 = r9.indexOf(r10);
                if (r9 < 0) goto L_0x02b4;
            L_0x028d:
                if (r9 == 0) goto L_0x029b;
            L_0x028f:
                r10 = r8.set;
                r10 = r10.short_name;
                r9 = r9 + -1;
                r9 = r10.charAt(r9);
                if (r9 != r6) goto L_0x02b4;
            L_0x029b:
                r13.clear();
                r9 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r9 = r9.localPacks;
                r9.add(r8);
                r9 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r9 = r9.localPacksByShortName;
                r10 = java.lang.Boolean.valueOf(r7);
                r9.put(r8, r10);
            L_0x02b4:
                r5 = r5 + 1;
                goto L_0x0233;
            L_0x02b8:
                r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r0 = r0.localPacks;
                r0 = r0.isEmpty();
                if (r0 == 0) goto L_0x02d0;
            L_0x02c4:
                r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r0 = r0.emojiStickers;
                r0 = r0.isEmpty();
                if (r0 != 0) goto L_0x02f9;
            L_0x02d0:
                r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r0 = org.telegram.ui.Components.EmojiView.this;
                r0 = r0.stickersGridView;
                r0 = r0.getAdapter();
                r4 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r4 = org.telegram.ui.Components.EmojiView.this;
                r4 = r4.stickersSearchGridAdapter;
                if (r0 == r4) goto L_0x02f9;
            L_0x02e6:
                r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r0 = org.telegram.ui.Components.EmojiView.this;
                r0 = r0.stickersGridView;
                r4 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r4 = org.telegram.ui.Components.EmojiView.this;
                r4 = r4.stickersSearchGridAdapter;
                r0.setAdapter(r4);
            L_0x02f9:
                r0 = new org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets;
                r0.<init>();
                r4 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r4 = r4.searchQuery;
                r0.q = r4;
                r4 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r5 = org.telegram.ui.Components.EmojiView.this;
                r5 = r5.currentAccount;
                r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r5);
                r6 = new org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$PMXaBA9vcwjG4TQSEF8AuHh_dEU;
                r6.<init>(r13, r0);
                r0 = r5.sendRequest(r0, r6);
                r4.reqId = r0;
                r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r0 = r0.searchQuery;
                r0 = org.telegram.messenger.Emoji.isValidEmoji(r0);
                if (r0 == 0) goto L_0x0351;
            L_0x032a:
                r0 = new org.telegram.tgnet.TLRPC$TL_messages_getStickers;
                r0.<init>();
                r4 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r4 = r4.searchQuery;
                r0.emoticon = r4;
                r0.hash = r1;
                r1 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r4 = org.telegram.ui.Components.EmojiView.this;
                r4 = r4.currentAccount;
                r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4);
                r5 = new org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$D2YaPWN88kYmZJFvtUqCfq785Bw;
                r5.<init>(r13, r0, r2, r3);
                r0 = r4.sendRequest(r0, r5);
                r1.reqId2 = r0;
            L_0x0351:
                r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this;
                r0.notifyDataSetChanged();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$AnonymousClass1.run():void");
            }

            public /* synthetic */ void lambda$run$1$EmojiView$StickersSearchGridAdapter$1(TL_messages_searchStickerSets tL_messages_searchStickerSets, TLObject tLObject, TL_error tL_error) {
                if (tLObject instanceof TL_messages_foundStickerSets) {
                    AndroidUtilities.runOnUIThread(new -$$Lambda$EmojiView$StickersSearchGridAdapter$1$jguHwpSdjaICAoSBsd6Lf5EhaUQ(this, tL_messages_searchStickerSets, tLObject));
                }
            }

            public /* synthetic */ void lambda$null$0$EmojiView$StickersSearchGridAdapter$1(TL_messages_searchStickerSets tL_messages_searchStickerSets, TLObject tLObject) {
                if (tL_messages_searchStickerSets.q.equals(StickersSearchGridAdapter.this.searchQuery)) {
                    clear();
                    EmojiView.this.stickersSearchField.progressDrawable.stopAnimation();
                    StickersSearchGridAdapter.this.reqId = 0;
                    if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersSearchGridAdapter) {
                        EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersSearchGridAdapter);
                    }
                    StickersSearchGridAdapter.this.serverPacks.addAll(((TL_messages_foundStickerSets) tLObject).sets);
                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                }
            }

            public /* synthetic */ void lambda$run$3$EmojiView$StickersSearchGridAdapter$1(TL_messages_getStickers tL_messages_getStickers, ArrayList arrayList, LongSparseArray longSparseArray, TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$EmojiView$StickersSearchGridAdapter$1$0O6pCOi0jii8pwBAvqjd7az_Jp8(this, tL_messages_getStickers, tLObject, arrayList, longSparseArray));
            }

            public /* synthetic */ void lambda$null$2$EmojiView$StickersSearchGridAdapter$1(TL_messages_getStickers tL_messages_getStickers, TLObject tLObject, ArrayList arrayList, LongSparseArray longSparseArray) {
                if (tL_messages_getStickers.emoticon.equals(StickersSearchGridAdapter.this.searchQuery)) {
                    int i = 0;
                    StickersSearchGridAdapter.this.reqId2 = 0;
                    if (tLObject instanceof TL_messages_stickers) {
                        TL_messages_stickers tL_messages_stickers = (TL_messages_stickers) tLObject;
                        int size = arrayList.size();
                        int size2 = tL_messages_stickers.stickers.size();
                        while (i < size2) {
                            Document document = (Document) tL_messages_stickers.stickers.get(i);
                            if (longSparseArray.indexOfKey(document.id) < 0) {
                                arrayList.add(document);
                            }
                            i++;
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
        private ArrayList<StickerSetCovered> serverPacks = new ArrayList();
        private int totalItems;

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        static /* synthetic */ int access$13304(StickersSearchGridAdapter stickersSearchGridAdapter) {
            int i = stickersSearchGridAdapter.emojiSearchId + 1;
            stickersSearchGridAdapter.emojiSearchId = i;
            return i;
        }

        public StickersSearchGridAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            int i = this.totalItems;
            return i != 1 ? i + 1 : 2;
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
            if (obj instanceof Document) {
                return 0;
            }
            return obj instanceof StickerSetCovered ? 3 : 2;
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$EmojiView$StickersSearchGridAdapter(View view) {
            FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
            StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
            if (EmojiView.this.installingStickerSets.indexOfKey(stickerSet.set.id) < 0 && EmojiView.this.removingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
                if (featuredStickerSetInfoCell.isInstalled()) {
                    EmojiView.this.removingStickerSets.put(stickerSet.set.id, stickerSet);
                    EmojiView.this.delegate.onStickerSetRemove(featuredStickerSetInfoCell.getStickerSet());
                } else {
                    EmojiView.this.installingStickerSets.put(stickerSet.set.id, stickerSet);
                    EmojiView.this.delegate.onStickerSetAdd(featuredStickerSetInfoCell.getStickerSet());
                }
                featuredStickerSetInfoCell.setDrawProgress(true);
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View anonymousClass2;
            if (i == 0) {
                anonymousClass2 = new StickerEmojiCell(this.context) {
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                    }
                };
            } else if (i == 1) {
                anonymousClass2 = new EmptyCell(this.context);
            } else if (i == 2) {
                anonymousClass2 = new StickerSetNameCell(this.context, false);
            } else if (i == 3) {
                anonymousClass2 = new FeaturedStickerSetInfoCell(this.context, 17);
                anonymousClass2.setAddOnClickListener(new -$$Lambda$EmojiView$StickersSearchGridAdapter$An1o7aFGx9Hb6YuxBcvVH4f8K-M(this));
            } else if (i == 4) {
                anonymousClass2 = new View(this.context);
                anonymousClass2.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
            } else if (i != 5) {
                anonymousClass2 = null;
            } else {
                View anonymousClass3 = new FrameLayout(this.context) {
                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, MeasureSpec.makeMeasureSpec((int) (((float) (((EmojiView.this.stickersGridView.getMeasuredHeight() - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3)) * 1.7f), NUM));
                    }
                };
                ImageView imageView = new ImageView(this.context);
                imageView.setScaleType(ScaleType.CENTER);
                imageView.setImageResource(NUM);
                String str = "chat_emojiPanelEmptyText";
                imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
                anonymousClass3.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 59.0f));
                TextView textView = new TextView(this.context);
                textView.setText(LocaleController.getString("NoStickersFound", NUM));
                textView.setTextSize(1, 16.0f);
                textView.setTextColor(Theme.getColor(str));
                anonymousClass3.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 9.0f));
                anonymousClass3.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                anonymousClass2 = anonymousClass3;
            }
            return new Holder(anonymousClass2);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            if (itemViewType != 0) {
                Integer num = null;
                Object obj;
                if (itemViewType == 1) {
                    EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
                    if (i == this.totalItems) {
                        i = this.positionToRow.get(i - 1, Integer.MIN_VALUE);
                        if (i == Integer.MIN_VALUE) {
                            emptyCell.setHeight(1);
                            return;
                        }
                        obj = this.rowStartPack.get(i);
                        if (obj instanceof TL_messages_stickerSet) {
                            num = Integer.valueOf(((TL_messages_stickerSet) obj).documents.size());
                        } else if (obj instanceof Integer) {
                            num = (Integer) obj;
                        }
                        if (num == null) {
                            emptyCell.setHeight(1);
                            return;
                        } else if (num.intValue() == 0) {
                            emptyCell.setHeight(AndroidUtilities.dp(8.0f));
                            return;
                        } else {
                            i = EmojiView.this.pager.getHeight() - (((int) Math.ceil((double) (((float) num.intValue()) / ((float) EmojiView.this.stickersGridAdapter.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                            if (i <= 0) {
                                i = 1;
                            }
                            emptyCell.setHeight(i);
                            return;
                        }
                    }
                    emptyCell.setHeight(AndroidUtilities.dp(82.0f));
                    return;
                } else if (itemViewType == 2) {
                    StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                    obj = this.cache.get(i);
                    if (obj instanceof TL_messages_stickerSet) {
                        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) obj;
                        if (TextUtils.isEmpty(this.searchQuery) || !this.localPacksByShortName.containsKey(tL_messages_stickerSet)) {
                            Integer num2 = (Integer) this.localPacksByName.get(tL_messages_stickerSet);
                            StickerSet stickerSet = tL_messages_stickerSet.set;
                            if (!(stickerSet == null || num2 == null)) {
                                stickerSetNameCell.setText(stickerSet.title, 0, num2.intValue(), !TextUtils.isEmpty(this.searchQuery) ? this.searchQuery.length() : 0);
                            }
                            stickerSetNameCell.setUrl(null, 0);
                            return;
                        }
                        StickerSet stickerSet2 = tL_messages_stickerSet.set;
                        if (stickerSet2 != null) {
                            stickerSetNameCell.setText(stickerSet2.title, 0);
                        }
                        stickerSetNameCell.setUrl(tL_messages_stickerSet.set.short_name, this.searchQuery.length());
                        return;
                    }
                    return;
                } else if (itemViewType == 3) {
                    StickerSetCovered stickerSetCovered = (StickerSetCovered) this.cache.get(i);
                    FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) viewHolder.itemView;
                    Object obj2 = EmojiView.this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0 ? 1 : null;
                    Object obj3 = EmojiView.this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0 ? 1 : null;
                    if (!(obj2 == null && obj3 == null)) {
                        if (obj2 != null && featuredStickerSetInfoCell.isInstalled()) {
                            EmojiView.this.installingStickerSets.remove(stickerSetCovered.set.id);
                            obj2 = null;
                        } else if (!(obj3 == null || featuredStickerSetInfoCell.isInstalled())) {
                            EmojiView.this.removingStickerSets.remove(stickerSetCovered.set.id);
                            obj3 = null;
                        }
                    }
                    if (obj2 == null && obj3 == null) {
                        z = false;
                    }
                    featuredStickerSetInfoCell.setDrawProgress(z);
                    itemViewType = TextUtils.isEmpty(this.searchQuery) ? -1 : stickerSetCovered.set.title.toLowerCase().indexOf(this.searchQuery);
                    if (itemViewType >= 0) {
                        featuredStickerSetInfoCell.setStickerSet(stickerSetCovered, false, itemViewType, this.searchQuery.length());
                        return;
                    }
                    featuredStickerSetInfoCell.setStickerSet(stickerSetCovered, false);
                    if (!TextUtils.isEmpty(this.searchQuery) && stickerSetCovered.set.short_name.toLowerCase().startsWith(this.searchQuery)) {
                        featuredStickerSetInfoCell.setUrl(stickerSetCovered.set.short_name, this.searchQuery.length());
                        return;
                    }
                    return;
                } else {
                    return;
                }
            }
            Document document = (Document) this.cache.get(i);
            StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) viewHolder.itemView;
            stickerEmojiCell.setSticker(document, this.cacheParent.get(i), (String) this.positionToEmoji.get(i), false);
            if (!(EmojiView.this.recentStickers.contains(document) || EmojiView.this.favouriteStickers.contains(document))) {
                z = false;
            }
            stickerEmojiCell.setRecent(z);
        }

        public void notifyDataSetChanged() {
            this.rowStartPack.clear();
            this.positionToRow.clear();
            this.cache.clear();
            this.positionsToSets.clear();
            this.positionToEmoji.clear();
            this.totalItems = 0;
            int size = this.serverPacks.size();
            int size2 = this.localPacks.size();
            int isEmpty = this.emojiArrays.isEmpty() ^ 1;
            int i = -1;
            int i2 = -1;
            int i3 = 0;
            while (i2 < (size + size2) + isEmpty) {
                int i4;
                int i5;
                if (i2 == i) {
                    SparseArray sparseArray = this.cache;
                    i5 = this.totalItems;
                    this.totalItems = i5 + 1;
                    sparseArray.put(i5, "search");
                    i3++;
                    i4 = size;
                } else {
                    Object obj;
                    ArrayList arrayList;
                    int i6;
                    int access$6200;
                    if (i2 < size2) {
                        obj = (TL_messages_stickerSet) this.localPacks.get(i2);
                        arrayList = obj.documents;
                        i4 = size;
                    } else {
                        int i7 = i2 - size2;
                        if (i7 < isEmpty) {
                            i7 = this.emojiArrays.size();
                            String str = "";
                            i5 = 0;
                            i6 = 0;
                            while (i5 < i7) {
                                String str2;
                                ArrayList arrayList2 = (ArrayList) this.emojiArrays.get(i5);
                                String str3 = (String) this.emojiStickers.get(arrayList2);
                                if (!(str3 == null || r11.equals(str3))) {
                                    this.positionToEmoji.put(this.totalItems + i6, str3);
                                    str = str3;
                                }
                                int size3 = arrayList2.size();
                                int i8 = i6;
                                i6 = 0;
                                while (i6 < size3) {
                                    int i9 = this.totalItems + i8;
                                    access$6200 = (i8 / EmojiView.this.stickersGridAdapter.stickersPerRow) + i3;
                                    Document document = (Document) arrayList2.get(i6);
                                    i4 = size;
                                    this.cache.put(i9, document);
                                    str2 = str;
                                    ArrayList arrayList3 = arrayList2;
                                    TL_messages_stickerSet stickerSetById = DataQuery.getInstance(EmojiView.this.currentAccount).getStickerSetById(DataQuery.getStickerSetId(document));
                                    if (stickerSetById != null) {
                                        this.cacheParent.put(i9, stickerSetById);
                                    }
                                    this.positionToRow.put(i9, access$6200);
                                    i8++;
                                    i6++;
                                    size = i4;
                                    arrayList2 = arrayList3;
                                    str = str2;
                                }
                                i4 = size;
                                str2 = str;
                                i5++;
                                i6 = i8;
                            }
                            i4 = size;
                            access$6200 = (int) Math.ceil((double) (((float) i6) / ((float) EmojiView.this.stickersGridAdapter.stickersPerRow)));
                            for (size = 0; size < access$6200; size++) {
                                this.rowStartPack.put(i3 + size, Integer.valueOf(i6));
                            }
                            this.totalItems += EmojiView.this.stickersGridAdapter.stickersPerRow * access$6200;
                            i3 += access$6200;
                        } else {
                            i4 = size;
                            obj = (StickerSetCovered) this.serverPacks.get(i7 - isEmpty);
                            arrayList = obj.covers;
                        }
                    }
                    if (!arrayList.isEmpty()) {
                        access$6200 = (int) Math.ceil((double) (((float) arrayList.size()) / ((float) EmojiView.this.stickersGridAdapter.stickersPerRow)));
                        this.cache.put(this.totalItems, obj);
                        if (i2 >= size2 && (obj instanceof StickerSetCovered)) {
                            this.positionsToSets.put(this.totalItems, (StickerSetCovered) obj);
                        }
                        this.positionToRow.put(this.totalItems, i3);
                        size = arrayList.size();
                        i = 0;
                        while (i < size) {
                            i6 = i + 1;
                            int i10 = this.totalItems + i6;
                            int access$62002 = (i3 + 1) + (i / EmojiView.this.stickersGridAdapter.stickersPerRow);
                            this.cache.put(i10, (Document) arrayList.get(i));
                            if (obj != null) {
                                this.cacheParent.put(i10, obj);
                            }
                            this.positionToRow.put(i10, access$62002);
                            if (i2 >= size2 && (obj instanceof StickerSetCovered)) {
                                this.positionsToSets.put(i10, (StickerSetCovered) obj);
                            }
                            i = i6;
                        }
                        size = access$6200 + 1;
                        for (i = 0; i < size; i++) {
                            this.rowStartPack.put(i3 + i, obj);
                        }
                        this.totalItems += (access$6200 * EmojiView.this.stickersGridAdapter.stickersPerRow) + 1;
                        i3 += size;
                    }
                }
                i2++;
                size = i4;
                i = -1;
            }
            super.notifyDataSetChanged();
        }
    }

    private class TrendingGridAdapter extends SelectionAdapter {
        private SparseArray<Object> cache = new SparseArray();
        private Context context;
        private SparseArray<StickerSetCovered> positionsToSets = new SparseArray();
        private ArrayList<StickerSetCovered> sets = new ArrayList();
        private int stickersPerRow;
        private int totalItems;

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public TrendingGridAdapter(Context context) {
            this.context = context;
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
                return obj instanceof Document ? 0 : 2;
            } else {
                return 1;
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View anonymousClass1;
            if (i == 0) {
                anonymousClass1 = new StickerEmojiCell(this.context) {
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                    }
                };
            } else if (i == 1) {
                anonymousClass1 = new EmptyCell(this.context);
            } else if (i != 2) {
                anonymousClass1 = null;
            } else {
                anonymousClass1 = new FeaturedStickerSetInfoCell(this.context, 17);
                anonymousClass1.setAddOnClickListener(new -$$Lambda$EmojiView$TrendingGridAdapter$KqfE7v9vPbOMyNkHB4d4a59Ij7c(this));
            }
            return new Holder(anonymousClass1);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$EmojiView$TrendingGridAdapter(View view) {
            FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
            StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
            if (EmojiView.this.installingStickerSets.indexOfKey(stickerSet.set.id) < 0 && EmojiView.this.removingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
                if (featuredStickerSetInfoCell.isInstalled()) {
                    EmojiView.this.removingStickerSets.put(stickerSet.set.id, stickerSet);
                    EmojiView.this.delegate.onStickerSetRemove(featuredStickerSetInfoCell.getStickerSet());
                } else {
                    EmojiView.this.installingStickerSets.put(stickerSet.set.id, stickerSet);
                    EmojiView.this.delegate.onStickerSetAdd(featuredStickerSetInfoCell.getStickerSet());
                }
                featuredStickerSetInfoCell.setDrawProgress(true);
            }
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                ((StickerEmojiCell) viewHolder.itemView).setSticker((Document) this.cache.get(i), this.positionsToSets.get(i), false);
            } else if (itemViewType == 1) {
                ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(82.0f));
            } else if (itemViewType == 2) {
                ArrayList unreadStickerSets = DataQuery.getInstance(EmojiView.this.currentAccount).getUnreadStickerSets();
                StickerSetCovered stickerSetCovered = (StickerSetCovered) this.sets.get(((Integer) this.cache.get(i)).intValue());
                boolean z2 = unreadStickerSets != null && unreadStickerSets.contains(Long.valueOf(stickerSetCovered.set.id));
                FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) viewHolder.itemView;
                featuredStickerSetInfoCell.setStickerSet(stickerSetCovered, z2);
                if (z2) {
                    DataQuery.getInstance(EmojiView.this.currentAccount).markFaturedStickersByIdAsRead(stickerSetCovered.set.id);
                }
                Object obj = EmojiView.this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0 ? 1 : null;
                Object obj2 = EmojiView.this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0 ? 1 : null;
                if (!(obj == null && obj2 == null)) {
                    if (obj != null && featuredStickerSetInfoCell.isInstalled()) {
                        EmojiView.this.installingStickerSets.remove(stickerSetCovered.set.id);
                        obj = null;
                    } else if (!(obj2 == null || featuredStickerSetInfoCell.isInstalled())) {
                        EmojiView.this.removingStickerSets.remove(stickerSetCovered.set.id);
                        obj2 = null;
                    }
                }
                if (!(obj == null && obj2 == null)) {
                    z = true;
                }
                featuredStickerSetInfoCell.setDrawProgress(z);
            }
        }

        public void notifyDataSetChanged() {
            int measuredWidth = EmojiView.this.getMeasuredWidth();
            if (measuredWidth == 0) {
                if (AndroidUtilities.isTablet()) {
                    measuredWidth = AndroidUtilities.displaySize.x;
                    int i = (measuredWidth * 35) / 100;
                    if (i < AndroidUtilities.dp(320.0f)) {
                        i = AndroidUtilities.dp(320.0f);
                    }
                    measuredWidth -= i;
                } else {
                    measuredWidth = AndroidUtilities.displaySize.x;
                }
                if (measuredWidth == 0) {
                    measuredWidth = 1080;
                }
            }
            this.stickersPerRow = Math.max(1, measuredWidth / AndroidUtilities.dp(72.0f));
            EmojiView.this.trendingLayoutManager.setSpanCount(this.stickersPerRow);
            if (!EmojiView.this.trendingLoaded) {
                this.cache.clear();
                this.positionsToSets.clear();
                this.sets.clear();
                this.totalItems = 0;
                ArrayList featuredStickerSets = DataQuery.getInstance(EmojiView.this.currentAccount).getFeaturedStickerSets();
                int i2 = 0;
                for (int i3 = 0; i3 < featuredStickerSets.size(); i3++) {
                    StickerSetCovered stickerSetCovered = (StickerSetCovered) featuredStickerSets.get(i3);
                    if (!(DataQuery.getInstance(EmojiView.this.currentAccount).isStickerPackInstalled(stickerSetCovered.set.id) || (stickerSetCovered.covers.isEmpty() && stickerSetCovered.cover == null))) {
                        int i4;
                        this.sets.add(stickerSetCovered);
                        this.positionsToSets.put(this.totalItems, stickerSetCovered);
                        SparseArray sparseArray = this.cache;
                        int i5 = this.totalItems;
                        this.totalItems = i5 + 1;
                        int i6 = i2 + 1;
                        sparseArray.put(i5, Integer.valueOf(i2));
                        i2 = this.totalItems / this.stickersPerRow;
                        if (stickerSetCovered.covers.isEmpty()) {
                            this.cache.put(this.totalItems, stickerSetCovered.cover);
                            i2 = 1;
                        } else {
                            i2 = (int) Math.ceil((double) (((float) stickerSetCovered.covers.size()) / ((float) this.stickersPerRow)));
                            for (i4 = 0; i4 < stickerSetCovered.covers.size(); i4++) {
                                this.cache.put(this.totalItems + i4, stickerSetCovered.covers.get(i4));
                            }
                        }
                        i4 = 0;
                        while (true) {
                            i5 = this.stickersPerRow;
                            if (i4 >= i2 * i5) {
                                break;
                            }
                            this.positionsToSets.put(this.totalItems + i4, stickerSetCovered);
                            i4++;
                        }
                        this.totalItems += i2 * i5;
                        i2 = i6;
                    }
                }
                if (this.totalItems != 0) {
                    EmojiView.this.trendingLoaded = true;
                    EmojiView emojiView = EmojiView.this;
                    emojiView.featuredStickersHash = DataQuery.getInstance(emojiView.currentAccount).getFeaturesStickersHashWithoutUnread();
                }
                super.notifyDataSetChanged();
            }
        }
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

    public EmojiView(boolean z, boolean z2, Context context, boolean z3, ChatFull chatFull) {
        boolean z4 = z2;
        Context context2 = context;
        boolean z5 = z3;
        super(context2);
        this.needEmojiSearch = z5;
        r8 = new Drawable[3];
        String str = "chat_emojiBottomPanelIcon";
        String str2 = "chat_emojiPanelIconSelected";
        r8[0] = Theme.createEmojiIconSelectorDrawable(context2, NUM, Theme.getColor(str), Theme.getColor(str2));
        r8[1] = Theme.createEmojiIconSelectorDrawable(context2, NUM, Theme.getColor(str), Theme.getColor(str2));
        r8[2] = Theme.createEmojiIconSelectorDrawable(context2, NUM, Theme.getColor(str), Theme.getColor(str2));
        this.tabIcons = r8;
        r8 = new Drawable[9];
        String str3 = "chat_emojiPanelIcon";
        r8[0] = Theme.createEmojiIconSelectorDrawable(context2, NUM, Theme.getColor(str3), Theme.getColor(str2));
        r8[1] = Theme.createEmojiIconSelectorDrawable(context2, NUM, Theme.getColor(str3), Theme.getColor(str2));
        r8[2] = Theme.createEmojiIconSelectorDrawable(context2, NUM, Theme.getColor(str3), Theme.getColor(str2));
        r8[3] = Theme.createEmojiIconSelectorDrawable(context2, NUM, Theme.getColor(str3), Theme.getColor(str2));
        r8[4] = Theme.createEmojiIconSelectorDrawable(context2, NUM, Theme.getColor(str3), Theme.getColor(str2));
        int i = 5;
        r8[5] = Theme.createEmojiIconSelectorDrawable(context2, NUM, Theme.getColor(str3), Theme.getColor(str2));
        r8[6] = Theme.createEmojiIconSelectorDrawable(context2, NUM, Theme.getColor(str3), Theme.getColor(str2));
        r8[7] = Theme.createEmojiIconSelectorDrawable(context2, NUM, Theme.getColor(str3), Theme.getColor(str2));
        r8[8] = Theme.createEmojiIconSelectorDrawable(context2, NUM, Theme.getColor(str3), Theme.getColor(str2));
        this.emojiIcons = r8;
        this.stickerIcons = new Drawable[]{Theme.createEmojiIconSelectorDrawable(context2, NUM, Theme.getColor(str), Theme.getColor(str2)), Theme.createEmojiIconSelectorDrawable(context2, NUM, Theme.getColor(str), Theme.getColor(str2)), Theme.createEmojiIconSelectorDrawable(context2, NUM, Theme.getColor(str), Theme.getColor(str2))};
        this.emojiTitles = new String[]{LocaleController.getString("Emoji1", NUM), LocaleController.getString("Emoji2", NUM), LocaleController.getString("Emoji3", NUM), LocaleController.getString("Emoji4", NUM), LocaleController.getString("Emoji5", NUM), LocaleController.getString("Emoji6", NUM), LocaleController.getString("Emoji7", NUM), LocaleController.getString("Emoji8", NUM)};
        this.showGifs = z4;
        this.info = chatFull;
        this.dotPaint = new Paint(1);
        this.dotPaint.setColor(Theme.getColor("chat_emojiPanelNewTrending"));
        if (VERSION.SDK_INT >= 21) {
            this.outlineProvider = new ViewOutlineProvider() {
                @TargetApi(21)
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(view.getPaddingLeft(), view.getPaddingTop(), view.getMeasuredWidth() - view.getPaddingRight(), view.getMeasuredHeight() - view.getPaddingBottom(), (float) AndroidUtilities.dp(6.0f));
                }
            };
        }
        this.emojiContainer = new FrameLayout(context2);
        this.views.add(this.emojiContainer);
        this.emojiGridView = new RecyclerListView(context2) {
            private boolean ignoreLayout;

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                this.ignoreLayout = true;
                EmojiView.this.emojiLayoutManager.setSpanCount(MeasureSpec.getSize(i) / AndroidUtilities.dp(AndroidUtilities.isTablet() ? 60.0f : 45.0f));
                this.ignoreLayout = false;
                super.onMeasure(i, i2);
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                if (EmojiView.this.needEmojiSearch && EmojiView.this.firstEmojiAttach) {
                    this.ignoreLayout = true;
                    EmojiView.this.emojiLayoutManager.scrollToPositionWithOffset(1, 0);
                    EmojiView.this.firstEmojiAttach = false;
                    this.ignoreLayout = false;
                }
                super.onLayout(z, i, i2, i3, i4);
                EmojiView.this.checkEmojiSearchFieldScroll(true);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* JADX WARNING: Removed duplicated region for block: B:18:0x006f  */
            public boolean onTouchEvent(android.view.MotionEvent r12) {
                /*
                r11 = this;
                r0 = org.telegram.ui.Components.EmojiView.this;
                r0 = r0.emojiTouchedView;
                if (r0 == 0) goto L_0x01a1;
            L_0x0008:
                r0 = r12.getAction();
                r1 = 2;
                r2 = 3;
                r3 = 5;
                r4 = -NUM; // 0xffffffffCLASSNAMECLASSNAME float:-10000.0 double:NaN;
                r5 = 1;
                if (r0 == r5) goto L_0x00c8;
            L_0x0015:
                r0 = r12.getAction();
                if (r0 != r2) goto L_0x001d;
            L_0x001b:
                goto L_0x00c8;
            L_0x001d:
                r0 = r12.getAction();
                if (r0 != r1) goto L_0x01a0;
            L_0x0023:
                r0 = org.telegram.ui.Components.EmojiView.this;
                r0 = r0.emojiTouchedX;
                r1 = 0;
                r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
                if (r0 == 0) goto L_0x006c;
            L_0x002e:
                r0 = org.telegram.ui.Components.EmojiView.this;
                r0 = r0.emojiTouchedX;
                r2 = r12.getX();
                r0 = r0 - r2;
                r0 = java.lang.Math.abs(r0);
                r2 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
                r6 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r2, r5);
                r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1));
                if (r0 > 0) goto L_0x0062;
            L_0x0048:
                r0 = org.telegram.ui.Components.EmojiView.this;
                r0 = r0.emojiTouchedY;
                r6 = r12.getY();
                r0 = r0 - r6;
                r0 = java.lang.Math.abs(r0);
                r2 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r2, r1);
                r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
                if (r0 <= 0) goto L_0x0060;
            L_0x005f:
                goto L_0x0062;
            L_0x0060:
                r0 = 1;
                goto L_0x006d;
            L_0x0062:
                r0 = org.telegram.ui.Components.EmojiView.this;
                r0.emojiTouchedX = r4;
                r0 = org.telegram.ui.Components.EmojiView.this;
                r0.emojiTouchedY = r4;
            L_0x006c:
                r0 = 0;
            L_0x006d:
                if (r0 != 0) goto L_0x01a0;
            L_0x006f:
                r0 = org.telegram.ui.Components.EmojiView.this;
                r0 = r0.location;
                r11.getLocationOnScreen(r0);
                r0 = org.telegram.ui.Components.EmojiView.this;
                r0 = r0.location;
                r0 = r0[r1];
                r0 = (float) r0;
                r12 = r12.getX();
                r0 = r0 + r12;
                r12 = org.telegram.ui.Components.EmojiView.this;
                r12 = r12.pickerView;
                r2 = org.telegram.ui.Components.EmojiView.this;
                r2 = r2.location;
                r12.getLocationOnScreen(r2);
                r12 = org.telegram.ui.Components.EmojiView.this;
                r12 = r12.location;
                r12 = r12[r1];
                r2 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
                r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
                r12 = r12 + r2;
                r12 = (float) r12;
                r0 = r0 - r12;
                r12 = org.telegram.ui.Components.EmojiView.this;
                r12 = r12.emojiSize;
                r2 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
                r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
                r12 = r12 + r2;
                r12 = (float) r12;
                r0 = r0 / r12;
                r12 = (int) r0;
                if (r12 >= 0) goto L_0x00ba;
            L_0x00b8:
                r12 = 0;
                goto L_0x00bd;
            L_0x00ba:
                if (r12 <= r3) goto L_0x00bd;
            L_0x00bc:
                r12 = 5;
            L_0x00bd:
                r0 = org.telegram.ui.Components.EmojiView.this;
                r0 = r0.pickerView;
                r0.setSelection(r12);
                goto L_0x01a0;
            L_0x00c8:
                r12 = org.telegram.ui.Components.EmojiView.this;
                r12 = r12.pickerViewPopup;
                r0 = 0;
                if (r12 == 0) goto L_0x0191;
            L_0x00d1:
                r12 = org.telegram.ui.Components.EmojiView.this;
                r12 = r12.pickerViewPopup;
                r12 = r12.isShowing();
                if (r12 == 0) goto L_0x0191;
            L_0x00dd:
                r12 = org.telegram.ui.Components.EmojiView.this;
                r12 = r12.pickerViewPopup;
                r12.dismiss();
                r12 = org.telegram.ui.Components.EmojiView.this;
                r12 = r12.pickerView;
                r12 = r12.getSelection();
                r6 = "🏿";
                r7 = "🏾";
                r8 = "🏽";
                r9 = "🏼";
                r10 = "🏻";
                if (r12 == r5) goto L_0x0114;
            L_0x0101:
                if (r12 == r1) goto L_0x0112;
            L_0x0103:
                if (r12 == r2) goto L_0x0110;
            L_0x0105:
                r1 = 4;
                if (r12 == r1) goto L_0x010e;
            L_0x0108:
                if (r12 == r3) goto L_0x010c;
            L_0x010a:
                r12 = r0;
                goto L_0x0115;
            L_0x010c:
                r12 = r6;
                goto L_0x0115;
            L_0x010e:
                r12 = r7;
                goto L_0x0115;
            L_0x0110:
                r12 = r8;
                goto L_0x0115;
            L_0x0112:
                r12 = r9;
                goto L_0x0115;
            L_0x0114:
                r12 = r10;
            L_0x0115:
                r1 = org.telegram.ui.Components.EmojiView.this;
                r1 = r1.emojiTouchedView;
                r1 = r1.getTag();
                r1 = (java.lang.String) r1;
                r2 = org.telegram.ui.Components.EmojiView.this;
                r2 = r2.emojiTouchedView;
                r2 = r2.isRecent;
                if (r2 != 0) goto L_0x0162;
            L_0x012d:
                if (r12 == 0) goto L_0x0139;
            L_0x012f:
                r2 = org.telegram.messenger.Emoji.emojiColor;
                r2.put(r1, r12);
                r1 = org.telegram.ui.Components.EmojiView.addColorToCode(r1, r12);
                goto L_0x013e;
            L_0x0139:
                r12 = org.telegram.messenger.Emoji.emojiColor;
                r12.remove(r1);
            L_0x013e:
                r12 = org.telegram.ui.Components.EmojiView.this;
                r12 = r12.emojiTouchedView;
                r1 = org.telegram.messenger.Emoji.getEmojiBigDrawable(r1);
                r2 = org.telegram.ui.Components.EmojiView.this;
                r2 = r2.emojiTouchedView;
                r2 = r2.isRecent;
                r12.setImageDrawable(r1, r2);
                r12 = org.telegram.ui.Components.EmojiView.this;
                r12 = r12.emojiTouchedView;
                r12.sendEmoji(r0);
                org.telegram.messenger.Emoji.saveEmojiColors();
                goto L_0x0191;
            L_0x0162:
                r2 = "";
                r1 = r1.replace(r10, r2);
                r1 = r1.replace(r9, r2);
                r1 = r1.replace(r8, r2);
                r1 = r1.replace(r7, r2);
                r1 = r1.replace(r6, r2);
                if (r12 == 0) goto L_0x0188;
            L_0x017a:
                r2 = org.telegram.ui.Components.EmojiView.this;
                r2 = r2.emojiTouchedView;
                r12 = org.telegram.ui.Components.EmojiView.addColorToCode(r1, r12);
                r2.sendEmoji(r12);
                goto L_0x0191;
            L_0x0188:
                r12 = org.telegram.ui.Components.EmojiView.this;
                r12 = r12.emojiTouchedView;
                r12.sendEmoji(r1);
            L_0x0191:
                r12 = org.telegram.ui.Components.EmojiView.this;
                r12.emojiTouchedView = r0;
                r12 = org.telegram.ui.Components.EmojiView.this;
                r12.emojiTouchedX = r4;
                r12 = org.telegram.ui.Components.EmojiView.this;
                r12.emojiTouchedY = r4;
            L_0x01a0:
                return r5;
            L_0x01a1:
                r0 = org.telegram.ui.Components.EmojiView.this;
                r1 = r12.getX();
                r0.emojiLastX = r1;
                r0 = org.telegram.ui.Components.EmojiView.this;
                r1 = r12.getY();
                r0.emojiLastY = r1;
                r12 = super.onTouchEvent(r12);
                return r12;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView$AnonymousClass3.onTouchEvent(android.view.MotionEvent):boolean");
            }
        };
        this.emojiGridView.setInstantClick(true);
        RecyclerListView recyclerListView = this.emojiGridView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context2, 8);
        this.emojiLayoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.emojiGridView.setTopGlowOffset(AndroidUtilities.dp(38.0f));
        this.emojiGridView.setBottomGlowOffset(AndroidUtilities.dp(48.0f));
        this.emojiGridView.setPadding(0, AndroidUtilities.dp(38.0f), 0, 0);
        str = "chat_emojiPanelBackground";
        this.emojiGridView.setGlowColor(Theme.getColor(str));
        this.emojiGridView.setClipToPadding(false);
        this.emojiLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            public int getSpanSize(int i) {
                if (EmojiView.this.emojiGridView.getAdapter() == EmojiView.this.emojiSearchAdapter) {
                    if (i == 0 || (i == 1 && EmojiView.this.emojiSearchAdapter.searchWas && EmojiView.this.emojiSearchAdapter.result.isEmpty())) {
                        return EmojiView.this.emojiLayoutManager.getSpanCount();
                    }
                } else if ((EmojiView.this.needEmojiSearch && i == 0) || EmojiView.this.emojiAdapter.positionToSection.indexOfKey(i) >= 0) {
                    return EmojiView.this.emojiLayoutManager.getSpanCount();
                }
                return 1;
            }
        });
        recyclerListView = this.emojiGridView;
        EmojiGridAdapter emojiGridAdapter = new EmojiGridAdapter(this, null);
        this.emojiAdapter = emojiGridAdapter;
        recyclerListView.setAdapter(emojiGridAdapter);
        this.emojiSearchAdapter = new EmojiSearchAdapter(this, null);
        this.emojiContainer.addView(this.emojiGridView, LayoutHelper.createFrame(-1, -1.0f));
        this.emojiGridView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1 && EmojiView.this.emojiSearchField != null) {
                    EmojiView.this.emojiSearchField.hideKeyboard();
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                i = EmojiView.this.emojiLayoutManager.findFirstVisibleItemPosition();
                if (i != -1) {
                    int size = Emoji.recentEmoji.size() + EmojiView.this.needEmojiSearch;
                    if (i >= size) {
                        int i3 = size;
                        size = 0;
                        while (true) {
                            String[][] strArr = EmojiData.dataColored;
                            if (size >= strArr.length) {
                                break;
                            }
                            i3 += strArr[size].length + 1;
                            if (i < i3) {
                                i = (Emoji.recentEmoji.isEmpty() ^ 1) + size;
                                break;
                            }
                            size++;
                        }
                        EmojiView.this.emojiTabs.onPageScrolled(i, 0);
                    }
                    i = 0;
                    EmojiView.this.emojiTabs.onPageScrolled(i, 0);
                }
                EmojiView.this.checkEmojiTabY(recyclerView, i2);
                EmojiView.this.checkEmojiSearchFieldScroll(false);
                EmojiView.this.checkBottomTabScroll((float) i2);
            }
        });
        this.emojiGridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int i) {
                if (view instanceof ImageViewEmoji) {
                    ((ImageViewEmoji) view).sendEmoji(null);
                }
            }
        });
        this.emojiGridView.setOnItemLongClickListener(new OnItemLongClickListener() {
            /* JADX WARNING: Removed duplicated region for block: B:75:0x0191  */
            /* JADX WARNING: Removed duplicated region for block: B:79:0x01a4  */
            /* JADX WARNING: Removed duplicated region for block: B:78:0x01a1  */
            public boolean onItemClick(android.view.View r18, int r19) {
                /*
                r17 = this;
                r0 = r17;
                r1 = r18;
                r2 = r1 instanceof org.telegram.ui.Components.EmojiView.ImageViewEmoji;
                r3 = 0;
                if (r2 == 0) goto L_0x021d;
            L_0x0009:
                r2 = r1;
                r2 = (org.telegram.ui.Components.EmojiView.ImageViewEmoji) r2;
                r4 = r2.getTag();
                r4 = (java.lang.String) r4;
                r5 = 0;
                r6 = "🏻";
                r7 = "";
                r8 = r4.replace(r6, r7);
                if (r8 == r4) goto L_0x001f;
            L_0x001e:
                r5 = r6;
            L_0x001f:
                r9 = "🏼";
                if (r5 != 0) goto L_0x002b;
            L_0x0024:
                r8 = r4.replace(r9, r7);
                if (r8 == r4) goto L_0x002b;
            L_0x002a:
                r5 = r9;
            L_0x002b:
                r10 = "🏽";
                if (r5 != 0) goto L_0x0037;
            L_0x0030:
                r8 = r4.replace(r10, r7);
                if (r8 == r4) goto L_0x0037;
            L_0x0036:
                r5 = r10;
            L_0x0037:
                r11 = "🏾";
                if (r5 != 0) goto L_0x0043;
            L_0x003c:
                r8 = r4.replace(r11, r7);
                if (r8 == r4) goto L_0x0043;
            L_0x0042:
                r5 = r11;
            L_0x0043:
                r12 = "🏿";
                if (r5 != 0) goto L_0x004f;
            L_0x0048:
                r8 = r4.replace(r12, r7);
                if (r8 == r4) goto L_0x004f;
            L_0x004e:
                r5 = r12;
            L_0x004f:
                r4 = org.telegram.messenger.EmojiData.emojiColoredMap;
                r4 = r4.containsKey(r8);
                r7 = 1;
                if (r4 == 0) goto L_0x01f5;
            L_0x0058:
                r4 = org.telegram.ui.Components.EmojiView.this;
                r4.emojiTouchedView = r2;
                r4 = org.telegram.ui.Components.EmojiView.this;
                r13 = r4.emojiLastX;
                r4.emojiTouchedX = r13;
                r4 = org.telegram.ui.Components.EmojiView.this;
                r13 = r4.emojiLastY;
                r4.emojiTouchedY = r13;
                if (r5 != 0) goto L_0x0080;
            L_0x0071:
                r4 = r2.isRecent;
                if (r4 != 0) goto L_0x0080;
            L_0x0077:
                r4 = org.telegram.messenger.Emoji.emojiColor;
                r4 = r4.get(r8);
                r5 = r4;
                r5 = (java.lang.String) r5;
            L_0x0080:
                r4 = 5;
                r13 = 2;
                r14 = 4;
                if (r5 == 0) goto L_0x00f4;
            L_0x0085:
                r16 = r5.hashCode();
                r15 = 3;
                switch(r16) {
                    case 1773375: goto L_0x00ae;
                    case 1773376: goto L_0x00a6;
                    case 1773377: goto L_0x009e;
                    case 1773378: goto L_0x0096;
                    case 1773379: goto L_0x008e;
                    default: goto L_0x008d;
                };
            L_0x008d:
                goto L_0x00b6;
            L_0x008e:
                r5 = r5.equals(r12);
                if (r5 == 0) goto L_0x00b6;
            L_0x0094:
                r5 = 4;
                goto L_0x00b7;
            L_0x0096:
                r5 = r5.equals(r11);
                if (r5 == 0) goto L_0x00b6;
            L_0x009c:
                r5 = 3;
                goto L_0x00b7;
            L_0x009e:
                r5 = r5.equals(r10);
                if (r5 == 0) goto L_0x00b6;
            L_0x00a4:
                r5 = 2;
                goto L_0x00b7;
            L_0x00a6:
                r5 = r5.equals(r9);
                if (r5 == 0) goto L_0x00b6;
            L_0x00ac:
                r5 = 1;
                goto L_0x00b7;
            L_0x00ae:
                r5 = r5.equals(r6);
                if (r5 == 0) goto L_0x00b6;
            L_0x00b4:
                r5 = 0;
                goto L_0x00b7;
            L_0x00b6:
                r5 = -1;
            L_0x00b7:
                if (r5 == 0) goto L_0x00ea;
            L_0x00b9:
                if (r5 == r7) goto L_0x00e0;
            L_0x00bb:
                if (r5 == r13) goto L_0x00d6;
            L_0x00bd:
                if (r5 == r15) goto L_0x00cc;
            L_0x00bf:
                if (r5 == r14) goto L_0x00c2;
            L_0x00c1:
                goto L_0x00fd;
            L_0x00c2:
                r5 = org.telegram.ui.Components.EmojiView.this;
                r5 = r5.pickerView;
                r5.setSelection(r4);
                goto L_0x00fd;
            L_0x00cc:
                r5 = org.telegram.ui.Components.EmojiView.this;
                r5 = r5.pickerView;
                r5.setSelection(r14);
                goto L_0x00fd;
            L_0x00d6:
                r5 = org.telegram.ui.Components.EmojiView.this;
                r5 = r5.pickerView;
                r5.setSelection(r15);
                goto L_0x00fd;
            L_0x00e0:
                r5 = org.telegram.ui.Components.EmojiView.this;
                r5 = r5.pickerView;
                r5.setSelection(r13);
                goto L_0x00fd;
            L_0x00ea:
                r5 = org.telegram.ui.Components.EmojiView.this;
                r5 = r5.pickerView;
                r5.setSelection(r7);
                goto L_0x00fd;
            L_0x00f4:
                r5 = org.telegram.ui.Components.EmojiView.this;
                r5 = r5.pickerView;
                r5.setSelection(r3);
            L_0x00fd:
                r5 = org.telegram.ui.Components.EmojiView.this;
                r5 = r5.location;
                r2.getLocationOnScreen(r5);
                r5 = org.telegram.ui.Components.EmojiView.this;
                r5 = r5.emojiSize;
                r6 = org.telegram.ui.Components.EmojiView.this;
                r6 = r6.pickerView;
                r6 = r6.getSelection();
                r5 = r5 * r6;
                r6 = org.telegram.ui.Components.EmojiView.this;
                r6 = r6.pickerView;
                r6 = r6.getSelection();
                r6 = r6 * 4;
                r9 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r9 == 0) goto L_0x012b;
            L_0x012a:
                goto L_0x012c;
            L_0x012b:
                r4 = 1;
            L_0x012c:
                r6 = r6 - r4;
                r4 = (float) r6;
                r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                r5 = r5 + r4;
                r4 = org.telegram.ui.Components.EmojiView.this;
                r4 = r4.location;
                r4 = r4[r3];
                r4 = r4 - r5;
                r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
                r9 = org.telegram.messenger.AndroidUtilities.dp(r6);
                if (r4 >= r9) goto L_0x0154;
            L_0x0144:
                r4 = org.telegram.ui.Components.EmojiView.this;
                r4 = r4.location;
                r4 = r4[r3];
                r4 = r4 - r5;
                r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                r4 = r4 - r6;
            L_0x0152:
                r5 = r5 + r4;
                goto L_0x018a;
            L_0x0154:
                r4 = org.telegram.ui.Components.EmojiView.this;
                r4 = r4.location;
                r4 = r4[r3];
                r4 = r4 - r5;
                r9 = org.telegram.ui.Components.EmojiView.this;
                r9 = r9.popupWidth;
                r4 = r4 + r9;
                r9 = org.telegram.messenger.AndroidUtilities.displaySize;
                r9 = r9.x;
                r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
                r9 = r9 - r10;
                if (r4 <= r9) goto L_0x018a;
            L_0x016f:
                r4 = org.telegram.ui.Components.EmojiView.this;
                r4 = r4.location;
                r4 = r4[r3];
                r4 = r4 - r5;
                r9 = org.telegram.ui.Components.EmojiView.this;
                r9 = r9.popupWidth;
                r4 = r4 + r9;
                r9 = org.telegram.messenger.AndroidUtilities.displaySize;
                r9 = r9.x;
                r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                r9 = r9 - r6;
                r4 = r4 - r9;
                goto L_0x0152;
            L_0x018a:
                r4 = -r5;
                r5 = r2.getTop();
                if (r5 >= 0) goto L_0x0195;
            L_0x0191:
                r3 = r2.getTop();
            L_0x0195:
                r2 = org.telegram.ui.Components.EmojiView.this;
                r2 = r2.pickerView;
                r5 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r5 == 0) goto L_0x01a4;
            L_0x01a1:
                r5 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
                goto L_0x01a6;
            L_0x01a4:
                r5 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
            L_0x01a6:
                r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
                r5 = r5 - r4;
                r6 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
                r6 = org.telegram.messenger.AndroidUtilities.dpf2(r6);
                r6 = (int) r6;
                r5 = r5 + r6;
                r2.setEmoji(r8, r5);
                r2 = org.telegram.ui.Components.EmojiView.this;
                r2 = r2.pickerViewPopup;
                r2.setFocusable(r7);
                r2 = org.telegram.ui.Components.EmojiView.this;
                r2 = r2.pickerViewPopup;
                r5 = r18.getMeasuredHeight();
                r5 = -r5;
                r6 = org.telegram.ui.Components.EmojiView.this;
                r6 = r6.popupHeight;
                r5 = r5 - r6;
                r6 = r18.getMeasuredHeight();
                r8 = org.telegram.ui.Components.EmojiView.this;
                r8 = r8.emojiSize;
                r6 = r6 - r8;
                r6 = r6 / r13;
                r5 = r5 + r6;
                r5 = r5 - r3;
                r2.showAsDropDown(r1, r4, r5);
                r1 = org.telegram.ui.Components.EmojiView.this;
                r1 = r1.pager;
                r1.requestDisallowInterceptTouchEvent(r7);
                r1 = org.telegram.ui.Components.EmojiView.this;
                r1 = r1.emojiGridView;
                r1.hideSelector();
                return r7;
            L_0x01f5:
                r2 = r2.isRecent;
                if (r2 == 0) goto L_0x021d;
            L_0x01fb:
                r2 = org.telegram.ui.Components.EmojiView.this;
                r2 = r2.emojiGridView;
                r1 = r2.findContainingViewHolder(r1);
                if (r1 == 0) goto L_0x021c;
            L_0x0207:
                r1 = r1.getAdapterPosition();
                r2 = org.telegram.messenger.Emoji.recentEmoji;
                r2 = r2.size();
                if (r1 > r2) goto L_0x021c;
            L_0x0213:
                r1 = org.telegram.ui.Components.EmojiView.this;
                r1 = r1.delegate;
                r1.onClearEmojiRecent();
            L_0x021c:
                return r7;
            L_0x021d:
                return r3;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView$AnonymousClass7.onItemClick(android.view.View, int):boolean");
            }
        });
        this.emojiTabs = new ScrollSlidingTabStrip(context2);
        if (z5) {
            this.emojiSearchField = new SearchField(context2, 1);
            this.emojiContainer.addView(this.emojiSearchField, new LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
            this.emojiSearchField.searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View view, boolean z) {
                    if (z) {
                        EmojiView.this.lastSearchKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                        DataQuery.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(EmojiView.this.lastSearchKeyboardLanguage);
                    }
                }
            });
        }
        this.emojiTabs.setShouldExpand(true);
        this.emojiTabs.setIndicatorHeight(-1);
        this.emojiTabs.setUnderlineHeight(-1);
        this.emojiTabs.setBackgroundColor(Theme.getColor(str));
        this.emojiContainer.addView(this.emojiTabs, LayoutHelper.createFrame(-1, 38.0f));
        this.emojiTabs.setDelegate(new ScrollSlidingTabStripDelegate() {
            public void onPageSelected(int i) {
                if (!Emoji.recentEmoji.isEmpty()) {
                    if (i == 0) {
                        EmojiView.this.emojiLayoutManager.scrollToPositionWithOffset(EmojiView.this.needEmojiSearch, 0);
                        return;
                    }
                    i--;
                }
                EmojiView.this.emojiGridView.stopScroll();
                EmojiView.this.emojiLayoutManager.scrollToPositionWithOffset(EmojiView.this.emojiAdapter.sectionToPosition.get(i), 0);
                EmojiView.this.checkEmojiTabY(null, 0);
            }
        });
        this.emojiTabsShadow = new View(context2);
        this.emojiTabsShadow.setAlpha(0.0f);
        this.emojiTabsShadow.setTag(Integer.valueOf(1));
        this.emojiTabsShadow.setBackgroundColor(Theme.getColor("chat_emojiPanelShadowLine"));
        LayoutParams layoutParams = new LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(38.0f);
        this.emojiContainer.addView(this.emojiTabsShadow, layoutParams);
        if (z) {
            RecyclerListView recyclerListView2;
            if (z4) {
                this.gifContainer = new FrameLayout(context2);
                this.views.add(this.gifContainer);
                this.gifGridView = new RecyclerListView(context2) {
                    private boolean ignoreLayout;

                    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                        boolean onInterceptTouchEvent = ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, EmojiView.this.gifGridView, 0, EmojiView.this.contentPreviewViewerDelegate);
                        if (super.onInterceptTouchEvent(motionEvent) || onInterceptTouchEvent) {
                            return true;
                        }
                        return false;
                    }

                    /* Access modifiers changed, original: protected */
                    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                        if (EmojiView.this.firstGifAttach && EmojiView.this.gifAdapter.getItemCount() > 1) {
                            this.ignoreLayout = true;
                            EmojiView.this.gifLayoutManager.scrollToPositionWithOffset(1, 0);
                            EmojiView.this.firstGifAttach = false;
                            this.ignoreLayout = false;
                        }
                        super.onLayout(z, i, i2, i3, i4);
                        EmojiView.this.checkGifSearchFieldScroll(true);
                    }

                    public void requestLayout() {
                        if (!this.ignoreLayout) {
                            super.requestLayout();
                        }
                    }
                };
                this.gifGridView.setClipToPadding(false);
                recyclerListView2 = this.gifGridView;
                AnonymousClass11 anonymousClass11 = new ExtendedGridLayoutManager(context2, 100) {
                    private Size size = new Size();

                    /* Access modifiers changed, original: protected */
                    public Size getSizeForItem(int i) {
                        ArrayList arrayList;
                        int i2;
                        Document document = null;
                        if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter) {
                            document = (Document) EmojiView.this.recentGifs.get(i);
                            arrayList = document.attributes;
                        } else if (EmojiView.this.gifSearchAdapter.results.isEmpty()) {
                            arrayList = null;
                        } else {
                            BotInlineResult botInlineResult = (BotInlineResult) EmojiView.this.gifSearchAdapter.results.get(i);
                            Document document2 = botInlineResult.document;
                            if (document2 != null) {
                                arrayList = document2.attributes;
                            } else {
                                WebDocument webDocument = botInlineResult.content;
                                if (webDocument != null) {
                                    arrayList = webDocument.attributes;
                                } else {
                                    WebDocument webDocument2 = botInlineResult.thumb;
                                    if (webDocument2 != null) {
                                        arrayList = webDocument2.attributes;
                                    }
                                    arrayList = document;
                                    document = document2;
                                }
                            }
                            document = arrayList;
                            arrayList = document;
                            document = document2;
                        }
                        Size size = this.size;
                        size.height = 100.0f;
                        size.width = 100.0f;
                        if (document != null) {
                            PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                            if (closestPhotoSizeWithSize != null) {
                                int i3 = closestPhotoSizeWithSize.w;
                                if (i3 != 0) {
                                    i2 = closestPhotoSizeWithSize.h;
                                    if (i2 != 0) {
                                        Size size2 = this.size;
                                        size2.width = (float) i3;
                                        size2.height = (float) i2;
                                    }
                                }
                            }
                        }
                        if (arrayList != null) {
                            for (i2 = 0; i2 < arrayList.size(); i2++) {
                                DocumentAttribute documentAttribute = (DocumentAttribute) arrayList.get(i2);
                                if ((documentAttribute instanceof TL_documentAttributeImageSize) || (documentAttribute instanceof TL_documentAttributeVideo)) {
                                    Size size3 = this.size;
                                    size3.width = (float) documentAttribute.w;
                                    size3.height = (float) documentAttribute.h;
                                    break;
                                }
                            }
                        }
                        return this.size;
                    }

                    /* Access modifiers changed, original: protected */
                    public int getFlowItemCount() {
                        if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter && EmojiView.this.gifSearchAdapter.results.isEmpty()) {
                            return 0;
                        }
                        return getItemCount() - 1;
                    }
                };
                this.gifLayoutManager = anonymousClass11;
                recyclerListView2.setLayoutManager(anonymousClass11);
                this.gifLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
                    public int getSpanSize(int i) {
                        if (i == 0 || (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter && EmojiView.this.gifSearchAdapter.results.isEmpty())) {
                            return EmojiView.this.gifLayoutManager.getSpanCount();
                        }
                        return EmojiView.this.gifLayoutManager.getSpanSizeForItem(i - 1);
                    }
                });
                this.gifGridView.addItemDecoration(new ItemDecoration() {
                    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
                        int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                        int i = 0;
                        if (childAdapterPosition != 0) {
                            rect.left = 0;
                            rect.bottom = 0;
                            childAdapterPosition--;
                            if (EmojiView.this.gifLayoutManager.isFirstRow(childAdapterPosition)) {
                                rect.top = 0;
                            } else {
                                rect.top = AndroidUtilities.dp(2.0f);
                            }
                            if (!EmojiView.this.gifLayoutManager.isLastInRow(childAdapterPosition)) {
                                i = AndroidUtilities.dp(2.0f);
                            }
                            rect.right = i;
                            return;
                        }
                        rect.left = 0;
                        rect.top = 0;
                        rect.bottom = 0;
                        rect.right = 0;
                    }
                });
                this.gifGridView.setOverScrollMode(2);
                recyclerListView2 = this.gifGridView;
                GifAdapter gifAdapter = new GifAdapter(context2);
                this.gifAdapter = gifAdapter;
                recyclerListView2.setAdapter(gifAdapter);
                this.gifSearchAdapter = new GifSearchAdapter(context2);
                this.gifGridView.setOnScrollListener(new OnScrollListener() {
                    public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                        if (i == 1) {
                            EmojiView.this.gifSearchField.hideKeyboard();
                        }
                    }

                    public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                        EmojiView.this.checkGifSearchFieldScroll(false);
                        EmojiView.this.checkBottomTabScroll((float) i2);
                    }
                });
                this.gifGridView.setOnTouchListener(new -$$Lambda$EmojiView$l7-k5UkFPpeHpKVTvoFojtQIyNg(this));
                this.gifOnItemClickListener = new -$$Lambda$EmojiView$qDPcvu2cn8NHi3uzHeRPO6CJgew(this);
                this.gifGridView.setOnItemClickListener(this.gifOnItemClickListener);
                this.gifContainer.addView(this.gifGridView, LayoutHelper.createFrame(-1, -1.0f));
                this.gifSearchField = new SearchField(context2, 2);
                this.gifContainer.addView(this.gifSearchField, new LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
            }
            this.stickersContainer = new FrameLayout(context2);
            DataQuery.getInstance(this.currentAccount).checkStickers(0);
            DataQuery.getInstance(this.currentAccount).checkFeaturedStickers();
            this.stickersGridView = new RecyclerListView(context2) {
                boolean ignoreLayout;

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.contentPreviewViewerDelegate);
                }

                public void setVisibility(int i) {
                    if (EmojiView.this.trendingGridView == null || EmojiView.this.trendingGridView.getVisibility() != 0) {
                        super.setVisibility(i);
                    } else {
                        super.setVisibility(8);
                    }
                }

                /* Access modifiers changed, original: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    if (EmojiView.this.firstStickersAttach && EmojiView.this.stickersGridAdapter.getItemCount() > 0) {
                        this.ignoreLayout = true;
                        EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(1, 0);
                        EmojiView.this.firstStickersAttach = false;
                        this.ignoreLayout = false;
                    }
                    super.onLayout(z, i, i2, i3, i4);
                    EmojiView.this.checkStickersSearchFieldScroll(true);
                }

                public void requestLayout() {
                    if (!this.ignoreLayout) {
                        super.requestLayout();
                    }
                }
            };
            recyclerListView2 = this.stickersGridView;
            GridLayoutManager gridLayoutManager2 = new GridLayoutManager(context2, 5);
            this.stickersLayoutManager = gridLayoutManager2;
            recyclerListView2.setLayoutManager(gridLayoutManager2);
            this.stickersLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
                public int getSpanSize(int i) {
                    if (EmojiView.this.stickersGridView.getAdapter() == EmojiView.this.stickersGridAdapter) {
                        if (i == 0) {
                            return EmojiView.this.stickersGridAdapter.stickersPerRow;
                        }
                        if (i == EmojiView.this.stickersGridAdapter.totalItems || (EmojiView.this.stickersGridAdapter.cache.get(i) != null && !(EmojiView.this.stickersGridAdapter.cache.get(i) instanceof Document))) {
                            return EmojiView.this.stickersGridAdapter.stickersPerRow;
                        }
                        return 1;
                    } else if (i == EmojiView.this.stickersSearchGridAdapter.totalItems || (EmojiView.this.stickersSearchGridAdapter.cache.get(i) != null && !(EmojiView.this.stickersSearchGridAdapter.cache.get(i) instanceof Document))) {
                        return EmojiView.this.stickersGridAdapter.stickersPerRow;
                    } else {
                        return 1;
                    }
                }
            });
            this.stickersGridView.setPadding(0, AndroidUtilities.dp(52.0f), 0, 0);
            this.stickersGridView.setClipToPadding(false);
            this.views.add(this.stickersContainer);
            this.stickersSearchGridAdapter = new StickersSearchGridAdapter(context2);
            recyclerListView2 = this.stickersGridView;
            StickersGridAdapter stickersGridAdapter = new StickersGridAdapter(context2);
            this.stickersGridAdapter = stickersGridAdapter;
            recyclerListView2.setAdapter(stickersGridAdapter);
            this.stickersGridView.setOnTouchListener(new -$$Lambda$EmojiView$qCmH2CuXJBuotfYIBsa5LNpa8lQ(this));
            this.stickersOnItemClickListener = new -$$Lambda$EmojiView$sBpUanL5xQpXEXKoK_PK5yrP_k0(this);
            this.stickersGridView.setOnItemClickListener(this.stickersOnItemClickListener);
            this.stickersGridView.setGlowColor(Theme.getColor(str));
            this.stickersContainer.addView(this.stickersGridView);
            this.stickersTab = new ScrollSlidingTabStrip(context2) {
                float downX;
                float downY;
                boolean draggingHorizontally;
                boolean draggingVertically;
                boolean first = true;
                float lastTranslateX;
                float lastX;
                boolean startedScroll;
                final int touchslop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                VelocityTracker vTracker;

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    if (motionEvent.getAction() == 0) {
                        this.draggingHorizontally = false;
                        this.draggingVertically = false;
                        this.downX = motionEvent.getRawX();
                        this.downY = motionEvent.getRawY();
                    } else if (!(this.draggingVertically || this.draggingHorizontally || EmojiView.this.dragListener == null || Math.abs(motionEvent.getRawY() - this.downY) < ((float) this.touchslop))) {
                        this.draggingVertically = true;
                        this.downY = motionEvent.getRawY();
                        EmojiView.this.dragListener.onDragStart();
                        if (this.startedScroll) {
                            EmojiView.this.pager.endFakeDrag();
                            this.startedScroll = false;
                        }
                        return true;
                    }
                    return super.onInterceptTouchEvent(motionEvent);
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    boolean z = false;
                    if (this.first) {
                        this.first = false;
                        this.lastX = motionEvent.getX();
                    }
                    if (motionEvent.getAction() == 0) {
                        this.draggingHorizontally = false;
                        this.draggingVertically = false;
                        this.downX = motionEvent.getRawX();
                        this.downY = motionEvent.getRawY();
                    } else if (!(this.draggingVertically || this.draggingHorizontally || EmojiView.this.dragListener == null)) {
                        if (Math.abs(motionEvent.getRawX() - this.downX) >= ((float) this.touchslop)) {
                            this.draggingHorizontally = true;
                        } else if (Math.abs(motionEvent.getRawY() - this.downY) >= ((float) this.touchslop)) {
                            this.draggingVertically = true;
                            this.downY = motionEvent.getRawY();
                            EmojiView.this.dragListener.onDragStart();
                            if (this.startedScroll) {
                                EmojiView.this.pager.endFakeDrag();
                                this.startedScroll = false;
                            }
                        }
                    }
                    float yVelocity;
                    if (this.draggingVertically) {
                        if (this.vTracker == null) {
                            this.vTracker = VelocityTracker.obtain();
                        }
                        this.vTracker.addMovement(motionEvent);
                        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                            this.vTracker.computeCurrentVelocity(1000);
                            yVelocity = this.vTracker.getYVelocity();
                            this.vTracker.recycle();
                            this.vTracker = null;
                            if (motionEvent.getAction() == 1) {
                                EmojiView.this.dragListener.onDragEnd(yVelocity);
                            } else {
                                EmojiView.this.dragListener.onDragCancel();
                            }
                            this.first = true;
                            this.draggingHorizontally = false;
                            this.draggingVertically = false;
                        } else {
                            EmojiView.this.dragListener.onDrag(Math.round(motionEvent.getRawY() - this.downY));
                        }
                        return true;
                    }
                    yVelocity = EmojiView.this.stickersTab.getTranslationX();
                    if (EmojiView.this.stickersTab.getScrollX() == 0 && yVelocity == 0.0f) {
                        if (this.startedScroll || this.lastX - motionEvent.getX() >= 0.0f) {
                            if (this.startedScroll && this.lastX - motionEvent.getX() > 0.0f && EmojiView.this.pager.isFakeDragging()) {
                                EmojiView.this.pager.endFakeDrag();
                                this.startedScroll = false;
                            }
                        } else if (EmojiView.this.pager.beginFakeDrag()) {
                            this.startedScroll = true;
                            this.lastTranslateX = EmojiView.this.stickersTab.getTranslationX();
                        }
                    }
                    if (this.startedScroll) {
                        motionEvent.getX();
                        float f = this.lastX;
                        f = this.lastTranslateX;
                        try {
                            this.lastTranslateX = yVelocity;
                        } catch (Exception e) {
                            try {
                                EmojiView.this.pager.endFakeDrag();
                            } catch (Exception unused) {
                            }
                            this.startedScroll = false;
                            FileLog.e(e);
                        }
                    }
                    this.lastX = motionEvent.getX();
                    if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
                        this.first = true;
                        this.draggingHorizontally = false;
                        this.draggingVertically = false;
                        if (this.startedScroll) {
                            EmojiView.this.pager.endFakeDrag();
                            this.startedScroll = false;
                        }
                    }
                    if (this.startedScroll || super.onTouchEvent(motionEvent)) {
                        z = true;
                    }
                    return z;
                }
            };
            this.stickersSearchField = new SearchField(context2, 0);
            this.stickersContainer.addView(this.stickersSearchField, new LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
            this.trendingGridView = new RecyclerListView(context2);
            this.trendingGridView.setItemAnimator(null);
            this.trendingGridView.setLayoutAnimation(null);
            recyclerListView2 = this.trendingGridView;
            AnonymousClass18 anonymousClass18 = new GridLayoutManager(context2, 5) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            this.trendingLayoutManager = anonymousClass18;
            recyclerListView2.setLayoutManager(anonymousClass18);
            this.trendingLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
                public int getSpanSize(int i) {
                    if ((EmojiView.this.trendingGridAdapter.cache.get(i) instanceof Integer) || i == EmojiView.this.trendingGridAdapter.totalItems) {
                        return EmojiView.this.trendingGridAdapter.stickersPerRow;
                    }
                    return 1;
                }
            });
            this.trendingGridView.setOnScrollListener(new OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    EmojiView.this.checkStickersTabY(recyclerView, i2);
                    EmojiView.this.checkBottomTabScroll((float) i2);
                }
            });
            this.trendingGridView.setClipToPadding(false);
            this.trendingGridView.setPadding(0, AndroidUtilities.dp(48.0f), 0, 0);
            recyclerListView2 = this.trendingGridView;
            TrendingGridAdapter trendingGridAdapter = new TrendingGridAdapter(context2);
            this.trendingGridAdapter = trendingGridAdapter;
            recyclerListView2.setAdapter(trendingGridAdapter);
            this.trendingGridView.setOnItemClickListener(new -$$Lambda$EmojiView$dYtQT1qlPrE3oYWHySyt2vGHEgc(this));
            this.trendingGridAdapter.notifyDataSetChanged();
            this.trendingGridView.setGlowColor(Theme.getColor(str));
            this.trendingGridView.setVisibility(8);
            this.stickersContainer.addView(this.trendingGridView);
            this.stickersTab.setUnderlineHeight(AndroidUtilities.getShadowHeight());
            this.stickersTab.setIndicatorHeight(AndroidUtilities.dp(2.0f));
            this.stickersTab.setIndicatorColor(Theme.getColor("chat_emojiPanelStickerPackSelectorLine"));
            this.stickersTab.setUnderlineColor(Theme.getColor("chat_emojiPanelShadowLine"));
            this.stickersTab.setBackgroundColor(Theme.getColor(str));
            this.stickersContainer.addView(this.stickersTab, LayoutHelper.createFrame(-1, 48, 51));
            updateStickerTabs();
            this.stickersTab.setDelegate(new -$$Lambda$EmojiView$EeKK6r6yP0jttMlGjDS2ja6SA2o(this));
            this.stickersGridView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    if (i == 1) {
                        EmojiView.this.stickersSearchField.hideKeyboard();
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    EmojiView.this.checkScroll();
                    EmojiView.this.checkStickersTabY(recyclerView, i2);
                    EmojiView.this.checkStickersSearchFieldScroll(false);
                    EmojiView.this.checkBottomTabScroll((float) i2);
                }
            });
        }
        this.pager = new ViewPager(context2) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(motionEvent);
            }

            public void setCurrentItem(int i, boolean z) {
                EmojiView.this.startStopVisibleGifs(i == 1);
                if (i == getCurrentItem()) {
                    if (i == 0) {
                        EmojiView.this.emojiGridView.smoothScrollToPosition(EmojiView.this.needEmojiSearch);
                    } else if (i == 1) {
                        EmojiView.this.gifGridView.smoothScrollToPosition(1);
                    } else {
                        EmojiView.this.stickersGridView.smoothScrollToPosition(1);
                    }
                    return;
                }
                super.setCurrentItem(i, z);
            }
        };
        this.pager.setAdapter(new EmojiPagesAdapter(this, null));
        this.topShadow = new View(context2);
        this.topShadow.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, -1907225));
        addView(this.topShadow, LayoutHelper.createFrame(-1, 6.0f));
        this.backspaceButton = new ImageView(context2) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    EmojiView.this.backspacePressed = true;
                    EmojiView.this.backspaceOnce = false;
                    EmojiView.this.postBackspaceRunnable(350);
                } else if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
                    EmojiView.this.backspacePressed = false;
                    if (!(EmojiView.this.backspaceOnce || EmojiView.this.delegate == null || !EmojiView.this.delegate.onBackspace())) {
                        EmojiView.this.backspaceButton.performHapticFeedback(3);
                    }
                }
                super.onTouchEvent(motionEvent);
                return true;
            }
        };
        this.backspaceButton.setImageResource(NUM);
        this.backspaceButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelBackspace"), Mode.MULTIPLY));
        this.backspaceButton.setScaleType(ScaleType.CENTER);
        this.backspaceButton.setContentDescription(LocaleController.getString("AccDescrBackspace", NUM));
        this.backspaceButton.setFocusable(true);
        this.bottomTabContainer = new FrameLayout(context2) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(motionEvent);
            }
        };
        this.shadowLine = new View(context2);
        this.shadowLine.setBackgroundColor(Theme.getColor("chat_emojiPanelShadowLine"));
        this.bottomTabContainer.addView(this.shadowLine, new LayoutParams(-1, AndroidUtilities.getShadowHeight()));
        this.bottomTabContainerBackground = new View(context2);
        this.bottomTabContainer.addView(this.bottomTabContainerBackground, new LayoutParams(-1, AndroidUtilities.dp(44.0f), 83));
        int i2 = 40;
        int i3 = 44;
        if (z5) {
            addView(this.bottomTabContainer, new LayoutParams(-1, AndroidUtilities.dp(44.0f) + AndroidUtilities.getShadowHeight(), 83));
            this.bottomTabContainer.addView(this.backspaceButton, LayoutHelper.createFrame(52, 44, 85));
            this.stickerSettingsButton = new ImageView(context2);
            this.stickerSettingsButton.setImageResource(NUM);
            this.stickerSettingsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelBackspace"), Mode.MULTIPLY));
            this.stickerSettingsButton.setScaleType(ScaleType.CENTER);
            this.stickerSettingsButton.setFocusable(true);
            this.stickerSettingsButton.setContentDescription(LocaleController.getString("Settings", NUM));
            this.bottomTabContainer.addView(this.stickerSettingsButton, LayoutHelper.createFrame(52, 44, 85));
            this.stickerSettingsButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (EmojiView.this.delegate != null) {
                        EmojiView.this.delegate.onStickersSettingsClick();
                    }
                }
            });
            this.typeTabs = new PagerSlidingTabStrip(context2);
            this.typeTabs.setViewPager(this.pager);
            this.typeTabs.setShouldExpand(false);
            this.typeTabs.setIndicatorHeight(0);
            this.typeTabs.setUnderlineHeight(0);
            this.typeTabs.setTabPaddingLeftRight(AndroidUtilities.dp(10.0f));
            this.bottomTabContainer.addView(this.typeTabs, LayoutHelper.createFrame(-2, 44, 81));
            this.typeTabs.setOnPageChangeListener(new OnPageChangeListener() {
                public void onPageScrollStateChanged(int i) {
                }

                public void onPageScrolled(int i, float f, int i2) {
                    SearchField access$4200;
                    EmojiView emojiView = EmojiView.this;
                    emojiView.onPageScrolled(i, (emojiView.getMeasuredWidth() - EmojiView.this.getPaddingLeft()) - EmojiView.this.getPaddingRight(), i2);
                    EmojiView.this.showBottomTab(true, true);
                    i = EmojiView.this.pager.getCurrentItem();
                    if (i == 0) {
                        access$4200 = EmojiView.this.emojiSearchField;
                    } else if (i == 1) {
                        access$4200 = EmojiView.this.gifSearchField;
                    } else {
                        access$4200 = EmojiView.this.stickersSearchField;
                    }
                    String obj = access$4200.searchEditText.getText().toString();
                    for (int i3 = 0; i3 < 3; i3++) {
                        SearchField access$42002;
                        if (i3 == 0) {
                            access$42002 = EmojiView.this.emojiSearchField;
                        } else if (i3 == 1) {
                            access$42002 = EmojiView.this.gifSearchField;
                        } else {
                            access$42002 = EmojiView.this.stickersSearchField;
                        }
                        if (!(access$42002 == null || access$42002 == access$4200 || access$42002.searchEditText == null || access$42002.searchEditText.getText().toString().equals(obj))) {
                            access$42002.searchEditText.setText(obj);
                            access$42002.searchEditText.setSelection(obj.length());
                        }
                    }
                }

                public void onPageSelected(int i) {
                    EmojiView.this.saveNewPage();
                    boolean z = false;
                    EmojiView.this.showBackspaceButton(i == 0, true);
                    EmojiView emojiView = EmojiView.this;
                    if (i == 2) {
                        z = true;
                    }
                    emojiView.showStickerSettingsButton(z, true);
                    if (!EmojiView.this.delegate.isSearchOpened()) {
                        return;
                    }
                    if (i == 0) {
                        if (EmojiView.this.emojiSearchField != null) {
                            EmojiView.this.emojiSearchField.searchEditText.requestFocus();
                        }
                    } else if (i == 1) {
                        if (EmojiView.this.gifSearchField != null) {
                            EmojiView.this.gifSearchField.searchEditText.requestFocus();
                        }
                    } else if (EmojiView.this.stickersSearchField != null) {
                        EmojiView.this.stickersSearchField.searchEditText.requestFocus();
                    }
                }
            });
            this.searchButton = new ImageView(context2);
            this.searchButton.setImageResource(NUM);
            this.searchButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelBackspace"), Mode.MULTIPLY));
            this.searchButton.setScaleType(ScaleType.CENTER);
            this.searchButton.setContentDescription(LocaleController.getString("Search", NUM));
            this.searchButton.setFocusable(true);
            this.bottomTabContainer.addView(this.searchButton, LayoutHelper.createFrame(52, 44, 83));
            this.searchButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    SearchField access$4200;
                    int currentItem = EmojiView.this.pager.getCurrentItem();
                    if (currentItem == 0) {
                        access$4200 = EmojiView.this.emojiSearchField;
                    } else if (currentItem == 1) {
                        access$4200 = EmojiView.this.gifSearchField;
                    } else {
                        access$4200 = EmojiView.this.stickersSearchField;
                    }
                    if (access$4200 != null) {
                        access$4200.searchEditText.requestFocus();
                        MotionEvent obtain = MotionEvent.obtain(0, 0, 0, 0.0f, 0.0f, 0);
                        access$4200.searchEditText.onTouchEvent(obtain);
                        obtain.recycle();
                        obtain = MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0);
                        access$4200.searchEditText.onTouchEvent(obtain);
                        obtain.recycle();
                    }
                }
            });
        } else {
            FrameLayout frameLayout = this.bottomTabContainer;
            int i4 = (VERSION.SDK_INT >= 21 ? 40 : 44) + 20;
            float f = (float) ((VERSION.SDK_INT >= 21 ? 40 : 44) + 12);
            if (LocaleController.isRTL) {
                i = 3;
            }
            addView(frameLayout, LayoutHelper.createFrame(i4, f, i | 80, 0.0f, 0.0f, 2.0f, 0.0f));
            Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(str), Theme.getColor(str));
            if (VERSION.SDK_INT < 21) {
                Drawable mutate = context.getResources().getDrawable(NUM).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
                Drawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                createSimpleSelectorCircleDrawable = combinedDrawable;
            } else {
                StateListAnimator stateListAnimator = new StateListAnimator();
                stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                this.backspaceButton.setStateListAnimator(stateListAnimator);
                this.backspaceButton.setOutlineProvider(new ViewOutlineProvider() {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                    }
                });
            }
            this.backspaceButton.setPadding(0, 0, AndroidUtilities.dp(2.0f), 0);
            this.backspaceButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
            this.backspaceButton.setContentDescription(LocaleController.getString("AccDescrBackspace", NUM));
            this.backspaceButton.setFocusable(true);
            frameLayout = this.bottomTabContainer;
            ImageView imageView = this.backspaceButton;
            int i5 = VERSION.SDK_INT >= 21 ? 40 : 44;
            if (VERSION.SDK_INT >= 21) {
                i3 = 40;
            }
            frameLayout.addView(imageView, LayoutHelper.createFrame(i5, (float) i3, 51, 10.0f, 0.0f, 10.0f, 0.0f));
            this.shadowLine.setVisibility(8);
            this.bottomTabContainerBackground.setVisibility(8);
        }
        addView(this.pager, 0, LayoutHelper.createFrame(-1, -1, 51));
        this.mediaBanTooltip = new CorrectlyMeasuringTextView(context2);
        this.mediaBanTooltip.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor("chat_gifSaveHintBackground")));
        this.mediaBanTooltip.setTextColor(Theme.getColor("chat_gifSaveHintText"));
        this.mediaBanTooltip.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f));
        this.mediaBanTooltip.setGravity(16);
        this.mediaBanTooltip.setTextSize(1, 14.0f);
        this.mediaBanTooltip.setVisibility(4);
        addView(this.mediaBanTooltip, LayoutHelper.createFrame(-2, -2.0f, 81, 5.0f, 0.0f, 5.0f, 53.0f));
        this.emojiSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 32.0f);
        this.pickerView = new EmojiColorPickerView(context2);
        EmojiColorPickerView emojiColorPickerView = this.pickerView;
        if (!AndroidUtilities.isTablet()) {
            i2 = 32;
        }
        i2 = AndroidUtilities.dp((float) (((i2 * 6) + 10) + 20));
        this.popupWidth = i2;
        i3 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 64.0f : 56.0f);
        this.popupHeight = i3;
        this.pickerViewPopup = new EmojiPopupWindow(emojiColorPickerView, i2, i3);
        this.pickerViewPopup.setOutsideTouchable(true);
        this.pickerViewPopup.setClippingEnabled(true);
        this.pickerViewPopup.setInputMethodMode(2);
        this.pickerViewPopup.setSoftInputMode(0);
        this.pickerViewPopup.getContentView().setFocusableInTouchMode(true);
        this.pickerViewPopup.getContentView().setOnKeyListener(new -$$Lambda$EmojiView$YBM1z0ObV6f6L9y0M-Yz0CakSrc(this));
        this.currentPage = MessagesController.getGlobalEmojiSettings().getInt("selected_page", 0);
        Emoji.loadRecentEmoji();
        this.emojiAdapter.notifyDataSetChanged();
        if (this.typeTabs == null) {
            return;
        }
        if (this.views.size() == 1 && this.typeTabs.getVisibility() == 0) {
            this.typeTabs.setVisibility(4);
        } else if (this.views.size() != 1 && this.typeTabs.getVisibility() != 0) {
            this.typeTabs.setVisibility(0);
        }
    }

    public /* synthetic */ boolean lambda$new$1$EmojiView(View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.gifGridView, 0, this.gifOnItemClickListener, this.contentPreviewViewerDelegate);
    }

    public /* synthetic */ void lambda$new$2$EmojiView(View view, int i) {
        if (this.delegate != null) {
            i--;
            if (this.gifGridView.getAdapter() != this.gifAdapter) {
                Adapter adapter = this.gifGridView.getAdapter();
                Adapter adapter2 = this.gifSearchAdapter;
                if (adapter == adapter2 && i >= 0 && i < adapter2.results.size()) {
                    this.delegate.onGifSelected(this.gifSearchAdapter.results.get(i), this.gifSearchAdapter.bot);
                    this.recentGifs = DataQuery.getInstance(this.currentAccount).getRecentGifs();
                    GifAdapter gifAdapter = this.gifAdapter;
                    if (gifAdapter != null) {
                        gifAdapter.notifyDataSetChanged();
                    }
                }
            } else if (i >= 0 && i < this.recentGifs.size()) {
                this.delegate.onGifSelected(this.recentGifs.get(i), "gif");
            }
        }
    }

    public /* synthetic */ boolean lambda$new$3$EmojiView(View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.stickersGridView, getMeasuredHeight(), this.stickersOnItemClickListener, this.contentPreviewViewerDelegate);
    }

    public /* synthetic */ void lambda$new$4$EmojiView(View view, int i) {
        Adapter adapter = this.stickersGridView.getAdapter();
        Adapter adapter2 = this.stickersSearchGridAdapter;
        if (adapter == adapter2) {
            StickerSetCovered stickerSetCovered = (StickerSetCovered) adapter2.positionsToSets.get(i);
            if (stickerSetCovered != null) {
                this.delegate.onShowStickerSet(stickerSetCovered.set, null);
                return;
            }
        }
        if (view instanceof StickerEmojiCell) {
            ContentPreviewViewer.getInstance().reset();
            StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
            if (!stickerEmojiCell.isDisabled()) {
                stickerEmojiCell.disable();
                this.delegate.onStickerSelected(stickerEmojiCell.getSticker(), stickerEmojiCell.getParentObject());
            }
        }
    }

    public /* synthetic */ void lambda$new$5$EmojiView(View view, int i) {
        StickerSetCovered stickerSetCovered = (StickerSetCovered) this.trendingGridAdapter.positionsToSets.get(i);
        if (stickerSetCovered != null) {
            this.delegate.onShowStickerSet(stickerSetCovered.set, null);
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
            ScrollSlidingTabStrip scrollSlidingTabStrip;
            int i2;
            if (i == this.recentTabBum) {
                this.stickersGridView.stopScroll();
                this.stickersLayoutManager.scrollToPositionWithOffset(this.stickersGridAdapter.getPositionForPack("recent"), 0);
                checkStickersTabY(null, 0);
                scrollSlidingTabStrip = this.stickersTab;
                i2 = this.recentTabBum;
                scrollSlidingTabStrip.onPageScrolled(i2, i2 > 0 ? i2 : this.stickersTabOffset);
            } else if (i == this.favTabBum) {
                this.stickersGridView.stopScroll();
                this.stickersLayoutManager.scrollToPositionWithOffset(this.stickersGridAdapter.getPositionForPack("fav"), 0);
                checkStickersTabY(null, 0);
                scrollSlidingTabStrip = this.stickersTab;
                i2 = this.favTabBum;
                scrollSlidingTabStrip.onPageScrolled(i2, i2 > 0 ? i2 : this.stickersTabOffset);
            } else {
                i -= this.stickersTabOffset;
                if (i < this.stickerSets.size()) {
                    if (i >= this.stickerSets.size()) {
                        i = this.stickerSets.size() - 1;
                    }
                    this.firstStickersAttach = false;
                    this.stickersGridView.stopScroll();
                    this.stickersLayoutManager.scrollToPositionWithOffset(this.stickersGridAdapter.getPositionForPack(this.stickerSets.get(i)), 0);
                    checkStickersTabY(null, 0);
                    checkScroll();
                }
            }
        }
    }

    public /* synthetic */ boolean lambda$new$7$EmojiView(View view, int i, KeyEvent keyEvent) {
        if (i == 82 && keyEvent.getRepeatCount() == 0 && keyEvent.getAction() == 1) {
            EmojiPopupWindow emojiPopupWindow = this.pickerViewPopup;
            if (emojiPopupWindow != null && emojiPopupWindow.isShowing()) {
                this.pickerViewPopup.dismiss();
                return true;
            }
        }
        return false;
    }

    private static String addColorToCode(String str, String str2) {
        String substring;
        int length = str.length();
        if (length > 2 && str.charAt(str.length() - 2) == 8205) {
            substring = str.substring(str.length() - 2);
            str = str.substring(0, str.length() - 2);
        } else if (length <= 3 || str.charAt(str.length() - 3) != 8205) {
            substring = null;
        } else {
            substring = str.substring(str.length() - 3);
            str = str.substring(0, str.length() - 3);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(str2);
        str = stringBuilder.toString();
        if (substring == null) {
            return str;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append(substring);
        return stringBuilder2.toString();
    }

    public void setTranslationY(float f) {
        super.setTranslationY(f);
        if (this.bottomTabContainer.getTag() == null) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
                View view = (View) getParent();
                if (view != null) {
                    this.bottomTabContainer.setTranslationY(-((getY() + ((float) getMeasuredHeight())) - ((float) view.getHeight())));
                }
            }
        }
    }

    private void startStopVisibleGifs(boolean z) {
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
        for (int i = 0; i < 3; i++) {
            LinearLayoutManager linearLayoutManager;
            HorizontalScrollView horizontalScrollView;
            if (i == 0) {
                linearLayoutManager = this.emojiLayoutManager;
                horizontalScrollView = this.emojiTabs;
            } else if (i == 1) {
                linearLayoutManager = this.gifLayoutManager;
                horizontalScrollView = null;
            } else {
                linearLayoutManager = this.stickersLayoutManager;
                horizontalScrollView = this.stickersTab;
            }
            if (linearLayoutManager != null) {
                int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (z) {
                    if (findFirstVisibleItemPosition == 1 || findFirstVisibleItemPosition == 2) {
                        linearLayoutManager.scrollToPosition(0);
                        if (horizontalScrollView != null) {
                            horizontalScrollView.setTranslationY(0.0f);
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
        searchField = this.gifSearchField;
        if (searchField != null) {
            searchField.hideKeyboard();
        }
        searchField = this.emojiSearchField;
        if (searchField != null) {
            searchField.hideKeyboard();
        }
    }

    private void openSearch(SearchField searchField) {
        AnimatorSet animatorSet = this.searchAnimation;
        AnimatorSet animatorSet2 = null;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        this.firstStickersAttach = false;
        this.firstGifAttach = false;
        this.firstEmojiAttach = false;
        int i = 0;
        while (i < 3) {
            SearchField searchField2;
            RecyclerListView recyclerListView;
            Object obj;
            LinearLayoutManager linearLayoutManager;
            if (i == 0) {
                searchField2 = this.emojiSearchField;
                recyclerListView = this.emojiGridView;
                obj = this.emojiTabs;
                linearLayoutManager = this.emojiLayoutManager;
            } else if (i == 1) {
                searchField2 = this.gifSearchField;
                recyclerListView = this.gifGridView;
                linearLayoutManager = this.gifLayoutManager;
                obj = animatorSet2;
            } else {
                searchField2 = this.stickersSearchField;
                recyclerListView = this.stickersGridView;
                obj = this.stickersTab;
                linearLayoutManager = this.stickersLayoutManager;
            }
            SearchField searchField3;
            if (searchField2 == null) {
                searchField3 = searchField;
            } else {
                if (searchField2 == this.gifSearchField) {
                    searchField3 = searchField;
                } else if (searchField == searchField2) {
                    EmojiViewDelegate emojiViewDelegate = this.delegate;
                    if (emojiViewDelegate != null && emojiViewDelegate.isExpanded()) {
                        this.searchAnimation = new AnimatorSet();
                        Animator[] animatorArr;
                        if (obj != null) {
                            AnimatorSet animatorSet3 = this.searchAnimation;
                            animatorArr = new Animator[3];
                            animatorArr[0] = ObjectAnimator.ofFloat(obj, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))});
                            animatorArr[1] = ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))});
                            animatorArr[2] = ObjectAnimator.ofFloat(searchField2, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(0.0f)});
                            animatorSet3.playTogether(animatorArr);
                        } else {
                            animatorSet2 = this.searchAnimation;
                            animatorArr = new Animator[2];
                            animatorArr[0] = ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))});
                            animatorArr[1] = ObjectAnimator.ofFloat(searchField2, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(0.0f)});
                            animatorSet2.playTogether(animatorArr);
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
                                    EmojiView.this.searchAnimation = null;
                                }
                            }

                            public void onAnimationCancel(Animator animator) {
                                if (animator.equals(EmojiView.this.searchAnimation)) {
                                    EmojiView.this.searchAnimation = null;
                                }
                            }
                        });
                        this.searchAnimation.start();
                    }
                }
                searchField2.setTranslationY((float) AndroidUtilities.dp(0.0f));
                if (obj != null) {
                    obj.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
                }
                if (recyclerListView == this.stickersGridView) {
                    recyclerListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                } else if (recyclerListView == this.emojiGridView) {
                    recyclerListView.setPadding(0, 0, 0, 0);
                }
                linearLayoutManager.scrollToPositionWithOffset(0, 0);
            }
            i++;
            animatorSet2 = null;
        }
    }

    private void showEmojiShadow(boolean z, boolean z2) {
        if (!(z && this.emojiTabsShadow.getTag() == null) && (z || this.emojiTabsShadow.getTag() == null)) {
            AnimatorSet animatorSet = this.emojiTabShadowAnimator;
            Object obj = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.emojiTabShadowAnimator = null;
            }
            View view = this.emojiTabsShadow;
            if (!z) {
                obj = Integer.valueOf(1);
            }
            view.setTag(obj);
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
                        EmojiView.this.emojiTabShadowAnimator = null;
                    }
                });
                this.emojiTabShadowAnimator.start();
            } else {
                View view3 = this.emojiTabsShadow;
                if (!z) {
                    f = 0.0f;
                }
                view3.setAlpha(f);
            }
        }
    }

    public void closeSearch(boolean z) {
        closeSearch(z, -1);
    }

    public void closeSearch(boolean z, long j) {
        boolean z2 = z;
        long j2 = j;
        AnimatorSet animatorSet = this.searchAnimation;
        AnimatorSet animatorSet2 = null;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        int currentItem = this.pager.getCurrentItem();
        int i = 2;
        if (currentItem == 2 && j2 != -1) {
            TL_messages_stickerSet stickerSetById = DataQuery.getInstance(this.currentAccount).getStickerSetById(j2);
            if (stickerSetById != null) {
                int positionForPack = this.stickersGridAdapter.getPositionForPack(stickerSetById);
                if (positionForPack >= 0) {
                    this.stickersLayoutManager.scrollToPositionWithOffset(positionForPack, AndroidUtilities.dp(60.0f));
                }
            }
        }
        int i2 = 0;
        while (i2 < 3) {
            SearchField searchField;
            RecyclerListView recyclerListView;
            GridLayoutManager gridLayoutManager;
            Object obj;
            if (i2 == 0) {
                searchField = this.emojiSearchField;
                recyclerListView = this.emojiGridView;
                gridLayoutManager = this.emojiLayoutManager;
                obj = this.emojiTabs;
            } else if (i2 == 1) {
                searchField = this.gifSearchField;
                recyclerListView = this.gifGridView;
                gridLayoutManager = this.gifLayoutManager;
                obj = animatorSet2;
            } else {
                searchField = this.stickersSearchField;
                recyclerListView = this.stickersGridView;
                gridLayoutManager = this.stickersLayoutManager;
                obj = this.stickersTab;
            }
            if (searchField != null) {
                searchField.searchEditText.setText("");
                if (i2 == currentItem && z2) {
                    this.searchAnimation = new AnimatorSet();
                    Animator[] animatorArr;
                    if (obj != null) {
                        AnimatorSet animatorSet3 = this.searchAnimation;
                        animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofFloat(obj, View.TRANSLATION_Y, new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight)});
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(searchField, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight)});
                        i = 2;
                        animatorArr[2] = ofFloat;
                        animatorSet3.playTogether(animatorArr);
                    } else {
                        animatorSet2 = this.searchAnimation;
                        animatorArr = new Animator[i];
                        animatorArr[0] = ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, new float[]{(float) (-this.searchFieldHeight)});
                        animatorArr[1] = ObjectAnimator.ofFloat(searchField, View.TRANSLATION_Y, new float[]{(float) (-this.searchFieldHeight)});
                        animatorSet2.playTogether(animatorArr);
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
                                    recyclerListView.setPadding(0, AndroidUtilities.dp(52.0f), 0, 0);
                                } else if (recyclerListView == EmojiView.this.emojiGridView) {
                                    recyclerListView.setPadding(0, AndroidUtilities.dp(38.0f), 0, 0);
                                }
                                if (recyclerListView == EmojiView.this.gifGridView) {
                                    gridLayoutManager.scrollToPositionWithOffset(1, 0);
                                } else if (findFirstVisibleItemPosition != -1) {
                                    gridLayoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, top - recyclerListView.getPaddingTop());
                                }
                                EmojiView.this.searchAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                EmojiView.this.searchAnimation = null;
                            }
                        }
                    });
                    this.searchAnimation.start();
                } else {
                    gridLayoutManager.scrollToPositionWithOffset(1, 0);
                    searchField.setTranslationY((float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight));
                    if (obj != null) {
                        obj.setTranslationY(0.0f);
                    }
                    if (recyclerListView == this.stickersGridView) {
                        recyclerListView.setPadding(0, AndroidUtilities.dp(52.0f), 0, 0);
                    } else if (recyclerListView == this.emojiGridView) {
                        recyclerListView.setPadding(0, AndroidUtilities.dp(38.0f), 0, 0);
                    }
                }
            }
            i2++;
            animatorSet2 = null;
        }
        if (!z2) {
            this.delegate.onSearchOpenClose(0);
        }
        showBottomTab(true, z2);
    }

    private void checkStickersSearchFieldScroll(boolean z) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        boolean z2 = false;
        ViewHolder findViewHolderForAdapterPosition;
        if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
            if (this.stickersSearchField != null) {
                RecyclerListView recyclerListView = this.stickersGridView;
                if (recyclerListView != null) {
                    findViewHolderForAdapterPosition = recyclerListView.findViewHolderForAdapterPosition(0);
                    if (findViewHolderForAdapterPosition != null) {
                        this.stickersSearchField.setTranslationY((float) findViewHolderForAdapterPosition.itemView.getTop());
                    } else {
                        this.stickersSearchField.setTranslationY((float) (-this.searchFieldHeight));
                    }
                    this.stickersSearchField.showShadow(false, z ^ 1);
                }
            }
            return;
        }
        findViewHolderForAdapterPosition = this.stickersGridView.findViewHolderForAdapterPosition(0);
        if (findViewHolderForAdapterPosition == null) {
            this.stickersSearchField.showShadow(true, z ^ 1);
        } else {
            SearchField searchField = this.stickersSearchField;
            if (findViewHolderForAdapterPosition.itemView.getTop() < this.stickersGridView.getPaddingTop()) {
                z2 = true;
            }
            searchField.showShadow(z2, z ^ 1);
        }
    }

    private void checkBottomTabScroll(float f) {
        int dp;
        this.lastBottomScrollDy += f;
        if (this.pager.getCurrentItem() == 0) {
            dp = AndroidUtilities.dp(38.0f);
        } else {
            dp = AndroidUtilities.dp(48.0f);
        }
        float f2 = this.lastBottomScrollDy;
        if (f2 >= ((float) dp)) {
            showBottomTab(false, true);
        } else if (f2 <= ((float) (-dp))) {
            showBottomTab(true, true);
        } else if ((this.bottomTabContainer.getTag() == null && this.lastBottomScrollDy < 0.0f) || (this.bottomTabContainer.getTag() != null && this.lastBottomScrollDy > 0.0f)) {
            this.lastBottomScrollDy = 0.0f;
        }
    }

    private void showBackspaceButton(final boolean z, boolean z2) {
        if (!(z && this.backspaceButton.getTag() == null) && (z || this.backspaceButton.getTag() == null)) {
            AnimatorSet animatorSet = this.backspaceButtonAnimation;
            Object obj = null;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.backspaceButtonAnimation = null;
            }
            ImageView imageView = this.backspaceButton;
            if (!z) {
                obj = Integer.valueOf(1);
            }
            imageView.setTag(obj);
            int i = 0;
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
                imageView2 = this.backspaceButton;
                property = View.SCALE_X;
                fArr = new float[1];
                fArr[0] = z ? 1.0f : 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView2, property, fArr);
                ImageView imageView3 = this.backspaceButton;
                Property property2 = View.SCALE_Y;
                float[] fArr2 = new float[1];
                if (!z) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                animatorArr[2] = ObjectAnimator.ofFloat(imageView3, property2, fArr2);
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
            } else {
                this.backspaceButton.setAlpha(z ? 1.0f : 0.0f);
                this.backspaceButton.setScaleX(z ? 1.0f : 0.0f);
                ImageView imageView4 = this.backspaceButton;
                if (!z) {
                    f = 0.0f;
                }
                imageView4.setScaleY(f);
                imageView4 = this.backspaceButton;
                if (!z) {
                    i = 4;
                }
                imageView4.setVisibility(i);
            }
        }
    }

    private void showStickerSettingsButton(final boolean z, boolean z2) {
        ImageView imageView = this.stickerSettingsButton;
        if (imageView != null) {
            if (!(z && imageView.getTag() == null) && (z || this.stickerSettingsButton.getTag() == null)) {
                AnimatorSet animatorSet = this.stickersButtonAnimation;
                Object obj = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.stickersButtonAnimation = null;
                }
                imageView = this.stickerSettingsButton;
                if (!z) {
                    obj = Integer.valueOf(1);
                }
                imageView.setTag(obj);
                int i = 0;
                float f = 1.0f;
                if (z2) {
                    if (z) {
                        this.stickerSettingsButton.setVisibility(0);
                    }
                    this.stickersButtonAnimation = new AnimatorSet();
                    AnimatorSet animatorSet2 = this.stickersButtonAnimation;
                    Animator[] animatorArr = new Animator[3];
                    ImageView imageView2 = this.stickerSettingsButton;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    fArr[0] = z ? 1.0f : 0.0f;
                    animatorArr[0] = ObjectAnimator.ofFloat(imageView2, property, fArr);
                    imageView2 = this.stickerSettingsButton;
                    property = View.SCALE_X;
                    fArr = new float[1];
                    fArr[0] = z ? 1.0f : 0.0f;
                    animatorArr[1] = ObjectAnimator.ofFloat(imageView2, property, fArr);
                    ImageView imageView3 = this.stickerSettingsButton;
                    Property property2 = View.SCALE_Y;
                    float[] fArr2 = new float[1];
                    if (!z) {
                        f = 0.0f;
                    }
                    fArr2[0] = f;
                    animatorArr[2] = ObjectAnimator.ofFloat(imageView3, property2, fArr2);
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
                } else {
                    this.stickerSettingsButton.setAlpha(z ? 1.0f : 0.0f);
                    this.stickerSettingsButton.setScaleX(z ? 1.0f : 0.0f);
                    ImageView imageView4 = this.stickerSettingsButton;
                    if (!z) {
                        f = 0.0f;
                    }
                    imageView4.setScaleY(f);
                    imageView4 = this.stickerSettingsButton;
                    if (!z) {
                        i = 4;
                    }
                    imageView4.setVisibility(i);
                }
            }
        }
    }

    private void showBottomTab(boolean z, boolean z2) {
        float f = 0.0f;
        this.lastBottomScrollDy = 0.0f;
        if (!(z && this.bottomTabContainer.getTag() == null) && (z || this.bottomTabContainer.getTag() == null)) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
                AnimatorSet animatorSet = this.bottomTabContainerAnimation;
                Object obj = null;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.bottomTabContainerAnimation = null;
                }
                FrameLayout frameLayout = this.bottomTabContainer;
                if (!z) {
                    obj = Integer.valueOf(1);
                }
                frameLayout.setTag(obj);
                float f2 = 54.0f;
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
                            f2 = 49.0f;
                        }
                        f2 = (float) AndroidUtilities.dp(f2);
                    }
                    fArr[0] = f2;
                    animatorArr[0] = ObjectAnimator.ofFloat(frameLayout2, property, fArr);
                    View view = this.shadowLine;
                    Property property2 = View.TRANSLATION_Y;
                    float[] fArr2 = new float[1];
                    if (!z) {
                        f = (float) AndroidUtilities.dp(49.0f);
                    }
                    fArr2[0] = f;
                    animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
                    animatorSet2.playTogether(animatorArr);
                    this.bottomTabContainerAnimation.setDuration(200);
                    this.bottomTabContainerAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.bottomTabContainerAnimation.start();
                } else {
                    FrameLayout frameLayout3 = this.bottomTabContainer;
                    if (z) {
                        f2 = 0.0f;
                    } else {
                        if (this.needEmojiSearch) {
                            f2 = 49.0f;
                        }
                        f2 = (float) AndroidUtilities.dp(f2);
                    }
                    frameLayout3.setTranslationY(f2);
                    View view2 = this.shadowLine;
                    if (!z) {
                        f = (float) AndroidUtilities.dp(49.0f);
                    }
                    view2.setTranslationY(f);
                }
            }
        }
    }

    private void checkStickersTabY(View view, int i) {
        if (view == null) {
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            this.stickersMinusDy = 0;
            scrollSlidingTabStrip.setTranslationY((float) null);
        } else if (view.getVisibility() == 0) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
                if (i > 0) {
                    RecyclerListView recyclerListView = this.stickersGridView;
                    if (recyclerListView != null && recyclerListView.getVisibility() == 0) {
                        ViewHolder findViewHolderForAdapterPosition = this.stickersGridView.findViewHolderForAdapterPosition(0);
                        if (findViewHolderForAdapterPosition != null && findViewHolderForAdapterPosition.itemView.getTop() + this.searchFieldHeight >= this.stickersGridView.getPaddingTop()) {
                            return;
                        }
                    }
                }
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

    private void checkEmojiSearchFieldScroll(boolean z) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        boolean z2 = false;
        ViewHolder findViewHolderForAdapterPosition;
        if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
            if (this.emojiSearchField != null) {
                RecyclerListView recyclerListView = this.emojiGridView;
                if (recyclerListView != null) {
                    findViewHolderForAdapterPosition = recyclerListView.findViewHolderForAdapterPosition(0);
                    if (findViewHolderForAdapterPosition != null) {
                        this.emojiSearchField.setTranslationY((float) findViewHolderForAdapterPosition.itemView.getTop());
                    } else {
                        this.emojiSearchField.setTranslationY((float) (-this.searchFieldHeight));
                    }
                    this.emojiSearchField.showShadow(false, z ^ 1);
                    if (findViewHolderForAdapterPosition == null || ((float) findViewHolderForAdapterPosition.itemView.getTop()) < ((float) (AndroidUtilities.dp(38.0f) - this.searchFieldHeight)) + this.emojiTabs.getTranslationY()) {
                        z2 = true;
                    }
                    showEmojiShadow(z2, z ^ 1);
                }
            }
            return;
        }
        findViewHolderForAdapterPosition = this.emojiGridView.findViewHolderForAdapterPosition(0);
        if (findViewHolderForAdapterPosition == null) {
            this.emojiSearchField.showShadow(true, z ^ 1);
        } else {
            this.emojiSearchField.showShadow(findViewHolderForAdapterPosition.itemView.getTop() < this.emojiGridView.getPaddingTop(), z ^ 1);
        }
        showEmojiShadow(false, z ^ 1);
    }

    private void checkEmojiTabY(View view, int i) {
        if (view == null) {
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.emojiTabs;
            this.emojiMinusDy = 0;
            scrollSlidingTabStrip.setTranslationY((float) null);
            this.emojiTabsShadow.setTranslationY((float) this.emojiMinusDy);
        } else if (view.getVisibility() == 0) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
                if (i > 0) {
                    RecyclerListView recyclerListView = this.emojiGridView;
                    if (recyclerListView != null && recyclerListView.getVisibility() == 0) {
                        ViewHolder findViewHolderForAdapterPosition = this.emojiGridView.findViewHolderForAdapterPosition(0);
                        if (findViewHolderForAdapterPosition != null) {
                            if (findViewHolderForAdapterPosition.itemView.getTop() + (this.needEmojiSearch ? this.searchFieldHeight : 0) >= this.emojiGridView.getPaddingTop()) {
                                return;
                            }
                        }
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

    private void checkGifSearchFieldScroll(boolean z) {
        RecyclerListView recyclerListView = this.gifGridView;
        if (recyclerListView != null) {
            Adapter adapter = recyclerListView.getAdapter();
            Adapter adapter2 = this.gifSearchAdapter;
            if (adapter == adapter2 && !adapter2.searchEndReached && this.gifSearchAdapter.reqId == 0 && !this.gifSearchAdapter.results.isEmpty()) {
                int findLastVisibleItemPosition = this.gifLayoutManager.findLastVisibleItemPosition();
                if (findLastVisibleItemPosition != -1 && findLastVisibleItemPosition > this.gifLayoutManager.getItemCount() - 5) {
                    GifSearchAdapter gifSearchAdapter = this.gifSearchAdapter;
                    gifSearchAdapter.search(gifSearchAdapter.lastSearchImageString, this.gifSearchAdapter.nextSearchOffset, true);
                }
            }
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        boolean z2 = false;
        ViewHolder findViewHolderForAdapterPosition;
        if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
            if (this.gifSearchField != null) {
                recyclerListView = this.gifGridView;
                if (recyclerListView != null) {
                    findViewHolderForAdapterPosition = recyclerListView.findViewHolderForAdapterPosition(0);
                    if (findViewHolderForAdapterPosition != null) {
                        this.gifSearchField.setTranslationY((float) findViewHolderForAdapterPosition.itemView.getTop());
                    } else {
                        this.gifSearchField.setTranslationY((float) (-this.searchFieldHeight));
                    }
                    this.gifSearchField.showShadow(false, z ^ 1);
                }
            }
            return;
        }
        findViewHolderForAdapterPosition = this.gifGridView.findViewHolderForAdapterPosition(0);
        if (findViewHolderForAdapterPosition == null) {
            this.gifSearchField.showShadow(true, z ^ 1);
        } else {
            SearchField searchField = this.gifSearchField;
            if (findViewHolderForAdapterPosition.itemView.getTop() < this.gifGridView.getPaddingTop()) {
                z2 = true;
            }
            searchField.showShadow(z2, z ^ 1);
        }
    }

    private void checkScroll() {
        int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (findFirstVisibleItemPosition != -1 && this.stickersGridView != null) {
            int i = this.favTabBum;
            if (i <= 0) {
                i = this.recentTabBum;
                if (i <= 0) {
                    i = this.stickersTabOffset;
                }
            }
            this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition), i);
        }
    }

    private void saveNewPage() {
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

    private void showTrendingTab(boolean z) {
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

    private void onPageScrolled(int i, int i2, int i3) {
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

    private void postBackspaceRunnable(int i) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$EmojiView$Jrw9DaGNjc5-52tMJtvct_h0u6A(this, i), (long) i);
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

    private void updateEmojiTabs() {
        int isEmpty = Emoji.recentEmoji.isEmpty() ^ 1;
        int i = this.hasRecentEmoji;
        if (i == -1 || i != isEmpty) {
            this.hasRecentEmoji = isEmpty;
            this.emojiTabs.removeTabs();
            r0 = new String[9];
            int i2 = 0;
            r0[0] = LocaleController.getString("RecentStickers", NUM);
            r0[1] = LocaleController.getString("Emoji1", NUM);
            r0[2] = LocaleController.getString("Emoji2", NUM);
            r0[3] = LocaleController.getString("Emoji3", NUM);
            r0[4] = LocaleController.getString("Emoji4", NUM);
            r0[5] = LocaleController.getString("Emoji5", NUM);
            r0[6] = LocaleController.getString("Emoji6", NUM);
            r0[7] = LocaleController.getString("Emoji7", NUM);
            r0[8] = LocaleController.getString("Emoji8", NUM);
            while (i2 < this.emojiIcons.length) {
                if (i2 != 0 || !Emoji.recentEmoji.isEmpty()) {
                    this.emojiTabs.addIconTab(this.emojiIcons[i2]).setContentDescription(r0[i2]);
                }
                i2++;
            }
            this.emojiTabs.updateTabStyles();
        }
    }

    private void updateStickerTabs() {
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
        if (scrollSlidingTabStrip != null) {
            int i;
            Object obj;
            this.recentTabBum = -2;
            this.favTabBum = -2;
            this.trendingTabNum = -2;
            this.stickersTabOffset = 0;
            int currentPosition = scrollSlidingTabStrip.getCurrentPosition();
            this.stickersTab.removeTabs();
            ArrayList unreadStickerSets = DataQuery.getInstance(this.currentAccount).getUnreadStickerSets();
            TrendingGridAdapter trendingGridAdapter = this.trendingGridAdapter;
            int i2 = 2;
            if (!(trendingGridAdapter == null || trendingGridAdapter.getItemCount() == 0 || unreadStickerSets.isEmpty())) {
                this.stickersCounter = this.stickersTab.addIconTabWithCounter(this.stickerIcons[2]);
                i = this.stickersTabOffset;
                this.trendingTabNum = i;
                this.stickersTabOffset = i + 1;
                this.stickersCounter.setText(String.format("%d", new Object[]{Integer.valueOf(unreadStickerSets.size())}));
            }
            if (this.favouriteStickers.isEmpty()) {
                obj = null;
            } else {
                i = this.stickersTabOffset;
                this.favTabBum = i;
                this.stickersTabOffset = i + 1;
                this.stickersTab.addIconTab(this.stickerIcons[1]).setContentDescription(LocaleController.getString("FavoriteStickers", NUM));
                obj = 1;
            }
            if (!this.recentStickers.isEmpty()) {
                i = this.stickersTabOffset;
                this.recentTabBum = i;
                this.stickersTabOffset = i + 1;
                this.stickersTab.addIconTab(this.stickerIcons[0]).setContentDescription(LocaleController.getString("RecentStickers", NUM));
                obj = 1;
            }
            this.stickerSets.clear();
            TL_messages_stickerSet tL_messages_stickerSet = null;
            this.groupStickerSet = null;
            this.groupStickerPackPosition = -1;
            this.groupStickerPackNum = -10;
            ArrayList stickerSets = DataQuery.getInstance(this.currentAccount).getStickerSets(0);
            Object obj2 = obj;
            for (i = 0; i < stickerSets.size(); i++) {
                TL_messages_stickerSet tL_messages_stickerSet2 = (TL_messages_stickerSet) stickerSets.get(i);
                if (!tL_messages_stickerSet2.set.archived) {
                    ArrayList arrayList = tL_messages_stickerSet2.documents;
                    if (!(arrayList == null || arrayList.isEmpty())) {
                        this.stickerSets.add(tL_messages_stickerSet2);
                        obj2 = 1;
                    }
                }
            }
            if (this.info != null) {
                SharedPreferences emojiSettings = MessagesController.getEmojiSettings(this.currentAccount);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("group_hide_stickers_");
                stringBuilder.append(this.info.id);
                long j = emojiSettings.getLong(stringBuilder.toString(), -1);
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.info.id));
                if (chat == null || this.info.stickerset == null || !ChatObject.hasAdminRights(chat)) {
                    this.groupStickersHidden = j != -1;
                } else {
                    StickerSet stickerSet = this.info.stickerset;
                    if (stickerSet != null) {
                        this.groupStickersHidden = j == stickerSet.id;
                    }
                }
                ChatFull chatFull = this.info;
                TL_messages_stickerSet groupStickerSetById;
                if (chatFull.stickerset != null) {
                    groupStickerSetById = DataQuery.getInstance(this.currentAccount).getGroupStickerSetById(this.info.stickerset);
                    if (groupStickerSetById != null) {
                        stickerSets = groupStickerSetById.documents;
                        if (!(stickerSets == null || stickerSets.isEmpty() || groupStickerSetById.set == null)) {
                            TL_messages_stickerSet tL_messages_stickerSet3 = new TL_messages_stickerSet();
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
                    }
                } else if (chatFull.can_set_stickers) {
                    groupStickerSetById = new TL_messages_stickerSet();
                    if (this.groupStickersHidden) {
                        this.groupStickerPackNum = this.stickerSets.size();
                        this.stickerSets.add(groupStickerSetById);
                    } else {
                        this.groupStickerPackNum = 0;
                        this.stickerSets.add(0, groupStickerSetById);
                    }
                }
            }
            i = 0;
            while (i < this.stickerSets.size()) {
                if (i == this.groupStickerPackNum) {
                    Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.info.id));
                    if (chat2 == null) {
                        this.stickerSets.remove(0);
                        i--;
                        i++;
                    } else {
                        this.stickersTab.addStickerTab(chat2);
                    }
                } else {
                    tL_messages_stickerSet = (TL_messages_stickerSet) this.stickerSets.get(i);
                    TLObject tLObject = (Document) tL_messages_stickerSet.documents.get(0);
                    TLObject tLObject2 = tL_messages_stickerSet.set.thumb;
                    if (!(tLObject2 instanceof TL_photoSize)) {
                        tLObject2 = tLObject;
                    }
                    View addStickerTab = this.stickersTab.addStickerTab(tLObject2, tLObject, tL_messages_stickerSet);
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(tL_messages_stickerSet.set.title);
                    stringBuilder2.append(", ");
                    stringBuilder2.append(LocaleController.getString("AccDescrStickerSet", NUM));
                    addStickerTab.setContentDescription(stringBuilder2.toString());
                }
                obj2 = 1;
                i++;
            }
            trendingGridAdapter = this.trendingGridAdapter;
            if (!(trendingGridAdapter == null || trendingGridAdapter.getItemCount() == 0 || !unreadStickerSets.isEmpty())) {
                this.trendingTabNum = this.stickersTabOffset + this.stickerSets.size();
                this.stickersTab.addIconTab(this.stickerIcons[2]).setContentDescription(LocaleController.getString("FeaturedStickers", NUM));
            }
            this.stickersTab.updateTabStyles();
            if (currentPosition != 0) {
                this.stickersTab.onPageScrolled(currentPosition, currentPosition);
            }
            checkPanels();
            if ((obj2 == null || (this.trendingTabNum == 0 && DataQuery.getInstance(this.currentAccount).areAllTrendingStickerSetsUnread())) && this.trendingTabNum >= 0) {
                if (this.scrolledToTrending == 0) {
                    showTrendingTab(true);
                    if (obj2 == null) {
                        i2 = 1;
                    }
                    this.scrolledToTrending = i2;
                }
            } else if (this.scrolledToTrending == 1) {
                showTrendingTab(false);
                checkScroll();
                this.stickersTab.cancelPositionAnimation();
            }
        }
    }

    private void checkPanels() {
        if (this.stickersTab != null) {
            RecyclerListView recyclerListView;
            if (this.trendingTabNum == -2) {
                recyclerListView = this.trendingGridView;
                if (recyclerListView != null && recyclerListView.getVisibility() == 0) {
                    this.trendingGridView.setVisibility(8);
                    this.stickersGridView.setVisibility(0);
                    this.stickersSearchField.setVisibility(0);
                }
            }
            recyclerListView = this.trendingGridView;
            int i;
            if (recyclerListView == null || recyclerListView.getVisibility() != 0) {
                int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
                if (findFirstVisibleItemPosition != -1) {
                    i = this.favTabBum;
                    if (i <= 0) {
                        i = this.recentTabBum;
                        if (i <= 0) {
                            i = this.stickersTabOffset;
                        }
                    }
                    this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition), i);
                }
            } else {
                ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
                i = this.trendingTabNum;
                int i2 = this.recentTabBum;
                if (i2 <= 0) {
                    i2 = this.stickersTabOffset;
                }
                scrollSlidingTabStrip.onPageScrolled(i, i2);
            }
        }
    }

    public void addRecentSticker(Document document) {
        if (document != null) {
            DataQuery.getInstance(this.currentAccount).addRecentSticker(0, null, document, (int) (System.currentTimeMillis() / 1000), false);
            boolean isEmpty = this.recentStickers.isEmpty();
            this.recentStickers = DataQuery.getInstance(this.currentAccount).getRecentStickers(0);
            StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
            if (stickersGridAdapter != null) {
                stickersGridAdapter.notifyDataSetChanged();
            }
            if (isEmpty) {
                updateStickerTabs();
            }
        }
    }

    public void addRecentGif(Document document) {
        if (document != null) {
            boolean isEmpty = this.recentGifs.isEmpty();
            this.recentGifs = DataQuery.getInstance(this.currentAccount).getRecentGifs();
            GifAdapter gifAdapter = this.gifAdapter;
            if (gifAdapter != null) {
                gifAdapter.notifyDataSetChanged();
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

    public void updateUIColors() {
        Drawable[] drawableArr;
        String str;
        String str2;
        String str3 = "chat_emojiPanelBackground";
        if (AndroidUtilities.isInMultiwindow || this.forseMultiwindowLayout) {
            Drawable background = getBackground();
            if (background != null) {
                background.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str3), Mode.MULTIPLY));
            }
        } else {
            setBackgroundColor(Theme.getColor(str3));
            if (this.needEmojiSearch) {
                this.bottomTabContainerBackground.setBackgroundColor(Theme.getColor(str3));
            }
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.emojiTabs;
        String str4 = "chat_emojiPanelShadowLine";
        if (scrollSlidingTabStrip != null) {
            scrollSlidingTabStrip.setBackgroundColor(Theme.getColor(str3));
            this.emojiTabsShadow.setBackgroundColor(Theme.getColor(str4));
        }
        for (int i = 0; i < 3; i++) {
            SearchField searchField;
            if (i == 0) {
                searchField = this.stickersSearchField;
            } else if (i == 1) {
                searchField = this.emojiSearchField;
            } else {
                searchField = this.gifSearchField;
            }
            if (searchField != null) {
                searchField.backgroundView.setBackgroundColor(Theme.getColor(str3));
                searchField.shadowView.setBackgroundColor(Theme.getColor(str4));
                String str5 = "chat_emojiSearchIcon";
                searchField.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str5), Mode.MULTIPLY));
                searchField.searchIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str5), Mode.MULTIPLY));
                Theme.setDrawableColorByKey(searchField.searchBackground.getBackground(), "chat_emojiSearchBackground");
                searchField.searchBackground.invalidate();
                searchField.searchEditText.setHintTextColor(Theme.getColor(str5));
                searchField.searchEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            }
        }
        Paint paint = this.dotPaint;
        if (paint != null) {
            paint.setColor(Theme.getColor("chat_emojiPanelNewTrending"));
        }
        RecyclerListView recyclerListView = this.emojiGridView;
        if (recyclerListView != null) {
            recyclerListView.setGlowColor(Theme.getColor(str3));
        }
        recyclerListView = this.stickersGridView;
        if (recyclerListView != null) {
            recyclerListView.setGlowColor(Theme.getColor(str3));
        }
        recyclerListView = this.trendingGridView;
        if (recyclerListView != null) {
            recyclerListView.setGlowColor(Theme.getColor(str3));
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
        if (scrollSlidingTabStrip2 != null) {
            scrollSlidingTabStrip2.setIndicatorColor(Theme.getColor("chat_emojiPanelStickerPackSelectorLine"));
            this.stickersTab.setUnderlineColor(Theme.getColor(str4));
            this.stickersTab.setBackgroundColor(Theme.getColor(str3));
        }
        ImageView imageView = this.backspaceButton;
        String str6 = "chat_emojiPanelBackspace";
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str6), Mode.MULTIPLY));
        }
        imageView = this.stickerSettingsButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str6), Mode.MULTIPLY));
        }
        imageView = this.searchButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str6), Mode.MULTIPLY));
        }
        View view = this.shadowLine;
        if (view != null) {
            view.setBackgroundColor(Theme.getColor(str4));
        }
        TextView textView = this.mediaBanTooltip;
        if (textView != null) {
            ((ShapeDrawable) textView.getBackground()).getPaint().setColor(Theme.getColor("chat_gifSaveHintBackground"));
            this.mediaBanTooltip.setTextColor(Theme.getColor("chat_gifSaveHintText"));
        }
        textView = this.stickersCounter;
        if (textView != null) {
            textView.setTextColor(Theme.getColor("chat_emojiPanelBadgeText"));
            Theme.setDrawableColor(this.stickersCounter.getBackground(), Theme.getColor("chat_emojiPanelBadgeBackground"));
            this.stickersCounter.invalidate();
        }
        int i2 = 0;
        while (true) {
            drawableArr = this.tabIcons;
            str = "chat_emojiPanelIconSelected";
            if (i2 >= drawableArr.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr[i2], Theme.getColor("chat_emojiBottomPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.tabIcons[i2], Theme.getColor(str), true);
            i2++;
        }
        i2 = 0;
        while (true) {
            drawableArr = this.emojiIcons;
            str2 = "chat_emojiPanelIcon";
            if (i2 >= drawableArr.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr[i2], Theme.getColor(str2), false);
            Theme.setEmojiDrawableColor(this.emojiIcons[i2], Theme.getColor(str), true);
            i2++;
        }
        i2 = 0;
        while (true) {
            drawableArr = this.stickerIcons;
            if (i2 < drawableArr.length) {
                Theme.setEmojiDrawableColor(drawableArr[i2], Theme.getColor(str2), false);
                Theme.setEmojiDrawableColor(this.stickerIcons[i2], Theme.getColor(str), true);
                i2++;
            } else {
                return;
            }
        }
    }

    public void onMeasure(int i, int i2) {
        this.isLayout = true;
        String str = "chat_emojiPanelBackground";
        if (AndroidUtilities.isInMultiwindow || this.forseMultiwindowLayout) {
            if (this.currentBackgroundType != 1) {
                if (VERSION.SDK_INT >= 21) {
                    setOutlineProvider((ViewOutlineProvider) this.outlineProvider);
                    setClipToOutline(true);
                    setElevation((float) AndroidUtilities.dp(2.0f));
                }
                setBackgroundResource(NUM);
                getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
                if (this.needEmojiSearch) {
                    this.bottomTabContainerBackground.setBackgroundDrawable(null);
                }
                this.currentBackgroundType = 1;
            }
        } else if (this.currentBackgroundType != 0) {
            if (VERSION.SDK_INT >= 21) {
                setOutlineProvider(null);
                setClipToOutline(false);
                setElevation(0.0f);
            }
            setBackgroundColor(Theme.getColor(str));
            if (this.needEmojiSearch) {
                this.bottomTabContainerBackground.setBackgroundColor(Theme.getColor(str));
            }
            this.currentBackgroundType = 0;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i2), NUM));
        this.isLayout = false;
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        if (this.lastNotifyWidth != i5) {
            this.lastNotifyWidth = i5;
            reloadStickersAdapter();
        }
        View view = (View) getParent();
        if (view != null) {
            i5 = i4 - i2;
            int height = view.getHeight();
            if (!(this.lastNotifyHeight == i5 && this.lastNotifyHeight2 == height)) {
                EmojiViewDelegate emojiViewDelegate = this.delegate;
                if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                    this.bottomTabContainer.setTranslationY((float) AndroidUtilities.dp(49.0f));
                } else if (this.bottomTabContainer.getTag() == null) {
                    if (i5 < this.lastNotifyHeight) {
                        this.bottomTabContainer.setTranslationY(0.0f);
                    } else {
                        this.bottomTabContainer.setTranslationY(-((getY() + ((float) getMeasuredHeight())) - ((float) view.getHeight())));
                    }
                }
                this.lastNotifyHeight = i5;
                this.lastNotifyHeight2 = height;
            }
        }
        super.onLayout(z, i, i2, i3, i4);
    }

    private void reloadStickersAdapter() {
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
        }
        TrendingGridAdapter trendingGridAdapter = this.trendingGridAdapter;
        if (trendingGridAdapter != null) {
            trendingGridAdapter.notifyDataSetChanged();
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

    public void setDelegate(EmojiViewDelegate emojiViewDelegate) {
        this.delegate = emojiViewDelegate;
    }

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    public void setChatInfo(ChatFull chatFull) {
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
        int i = this.currentPage;
        if (i == 0 || z) {
            showBackspaceButton(true, false);
            showStickerSettingsButton(false, false);
            if (this.pager.getCurrentItem() != 0) {
                this.pager.setCurrentItem(0, z ^ 1);
            }
        } else if (i == 1) {
            showBackspaceButton(false, false);
            showStickerSettingsButton(true, false);
            if (this.pager.getCurrentItem() != 2) {
                this.pager.setCurrentItem(2, false);
            }
            if (this.stickersTab == null) {
                return;
            }
            if (this.trendingTabNum == 0 && DataQuery.getInstance(this.currentAccount).areAllTrendingStickerSetsUnread()) {
                showTrendingTab(true);
                return;
            }
            int i2 = this.recentTabBum;
            if (i2 >= 0) {
                this.stickersTab.selectTab(i2);
                return;
            }
            i2 = this.favTabBum;
            if (i2 >= 0) {
                this.stickersTab.selectTab(i2);
            } else {
                this.stickersTab.selectTab(this.stickersTabOffset);
            }
        } else if (i == 2) {
            showBackspaceButton(false, false);
            showStickerSettingsButton(false, false);
            if (this.pager.getCurrentItem() != 1) {
                this.pager.setCurrentItem(1, false);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentImagesDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
            AndroidUtilities.runOnUIThread(new -$$Lambda$EmojiView$nydI8RFLQx0Boo3VLUW9Gt2FzYg(this));
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
            TrendingGridAdapter trendingGridAdapter = this.trendingGridAdapter;
            if (trendingGridAdapter != null) {
                this.trendingLoaded = false;
                trendingGridAdapter.notifyDataSetChanged();
            }
            checkDocuments(true);
            checkDocuments(false);
            DataQuery.getInstance(this.currentAccount).loadRecents(0, true, true, false);
            DataQuery.getInstance(this.currentAccount).loadRecents(0, false, true, false);
            DataQuery.getInstance(this.currentAccount).loadRecents(2, false, true, false);
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

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EmojiPopupWindow emojiPopupWindow = this.pickerViewPopup;
        if (emojiPopupWindow != null && emojiPopupWindow.isShowing()) {
            this.pickerViewPopup.dismiss();
        }
    }

    private void checkDocuments(boolean z) {
        if (z) {
            this.recentGifs = DataQuery.getInstance(this.currentAccount).getRecentGifs();
            GifAdapter gifAdapter = this.gifAdapter;
            if (gifAdapter != null) {
                gifAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        int size = this.recentStickers.size();
        int size2 = this.favouriteStickers.size();
        this.recentStickers = DataQuery.getInstance(this.currentAccount).getRecentStickers(0);
        this.favouriteStickers = DataQuery.getInstance(this.currentAccount).getRecentStickers(2);
        for (int i = 0; i < this.favouriteStickers.size(); i++) {
            Document document = (Document) this.favouriteStickers.get(i);
            for (int i2 = 0; i2 < this.recentStickers.size(); i2++) {
                Document document2 = (Document) this.recentStickers.get(i2);
                if (document2.dc_id == document.dc_id && document2.id == document.id) {
                    this.recentStickers.remove(i2);
                    break;
                }
            }
        }
        if (!(size == this.recentStickers.size() && size2 == this.favouriteStickers.size())) {
            updateStickerTabs();
        }
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
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
                if (!(this.currentChatId == 0 || this.pager.getCurrentItem() == 0)) {
                    showBackspaceButton(true, true);
                    showStickerSettingsButton(false, true);
                    this.pager.setCurrentItem(0, false);
                }
            }
        }
    }

    public void showStickerBanHint(boolean z) {
        if (this.mediaBanTooltip.getVisibility() != 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.currentChatId));
            if (chat != null) {
                TL_chatBannedRights tL_chatBannedRights;
                AnimatorSet animatorSet;
                if (!ChatObject.hasAdminRights(chat)) {
                    tL_chatBannedRights = chat.default_banned_rights;
                    if (tL_chatBannedRights != null && tL_chatBannedRights.send_stickers) {
                        if (z) {
                            this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachGifRestricted", NUM));
                        } else {
                            this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachStickersRestricted", NUM));
                        }
                        this.mediaBanTooltip.setVisibility(0);
                        animatorSet = new AnimatorSet();
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mediaBanTooltip, View.ALPHA, new float[]{0.0f, 1.0f})});
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AndroidUtilities.runOnUIThread(new -$$Lambda$EmojiView$34$syxP2tKTMRS9T48q548gWNuUVOM(this), 5000);
                            }

                            public /* synthetic */ void lambda$onAnimationEnd$0$EmojiView$34() {
                                if (EmojiView.this.mediaBanTooltip != null) {
                                    AnimatorSet animatorSet = new AnimatorSet();
                                    Animator[] animatorArr = new Animator[1];
                                    animatorArr[0] = ObjectAnimator.ofFloat(EmojiView.this.mediaBanTooltip, View.ALPHA, new float[]{0.0f});
                                    animatorSet.playTogether(animatorArr);
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
                tL_chatBannedRights = chat.banned_rights;
                if (tL_chatBannedRights != null) {
                    if (AndroidUtilities.isBannedForever(tL_chatBannedRights)) {
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
                    this.mediaBanTooltip.setVisibility(0);
                    animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mediaBanTooltip, View.ALPHA, new float[]{0.0f, 1.0f})});
                    animatorSet.addListener(/* anonymous class already generated */);
                    animatorSet.setDuration(300);
                    animatorSet.start();
                }
            }
        }
    }

    private void updateVisibleTrendingSets() {
        TrendingGridAdapter trendingGridAdapter = this.trendingGridAdapter;
        if (trendingGridAdapter != null && trendingGridAdapter != null) {
            for (int i = 0; i < 2; i++) {
                ViewGroup viewGroup;
                if (i == 0) {
                    try {
                        viewGroup = this.trendingGridView;
                    } catch (Exception e) {
                        FileLog.e(e);
                        return;
                    }
                }
                viewGroup = this.stickersGridView;
                int childCount = viewGroup.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = viewGroup.getChildAt(i2);
                    if (childAt instanceof FeaturedStickerSetInfoCell) {
                        if (((Holder) viewGroup.getChildViewHolder(childAt)) != null) {
                            FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) childAt;
                            ArrayList unreadStickerSets = DataQuery.getInstance(this.currentAccount).getUnreadStickerSets();
                            StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
                            boolean z = true;
                            boolean z2 = unreadStickerSets != null && unreadStickerSets.contains(Long.valueOf(stickerSet.set.id));
                            featuredStickerSetInfoCell.setStickerSet(stickerSet, z2);
                            if (z2) {
                                DataQuery.getInstance(this.currentAccount).markFaturedStickersByIdAsRead(stickerSet.set.id);
                            }
                            Object obj = this.installingStickerSets.indexOfKey(stickerSet.set.id) >= 0 ? 1 : null;
                            Object obj2 = this.removingStickerSets.indexOfKey(stickerSet.set.id) >= 0 ? 1 : null;
                            if (!(obj == null && obj2 == null)) {
                                if (obj != null && featuredStickerSetInfoCell.isInstalled()) {
                                    this.installingStickerSets.remove(stickerSet.set.id);
                                    obj = null;
                                } else if (!(obj2 == null || featuredStickerSetInfoCell.isInstalled())) {
                                    this.removingStickerSets.remove(stickerSet.set.id);
                                    obj2 = null;
                                }
                            }
                            if (obj == null) {
                                if (obj2 == null) {
                                    z = false;
                                }
                            }
                            featuredStickerSetInfoCell.setDrawProgress(z);
                        }
                    }
                }
            }
        }
    }

    public boolean areThereAnyStickers() {
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        return stickersGridAdapter != null && stickersGridAdapter.getItemCount() > 0;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = 0;
        if (i == NotificationCenter.stickersDidLoad) {
            if (((Integer) objArr[0]).intValue() == 0) {
                TrendingGridAdapter trendingGridAdapter = this.trendingGridAdapter;
                if (trendingGridAdapter != null) {
                    if (this.trendingLoaded) {
                        updateVisibleTrendingSets();
                    } else {
                        trendingGridAdapter.notifyDataSetChanged();
                    }
                }
                updateStickerTabs();
                reloadStickersAdapter();
                checkPanels();
            }
        } else if (i == NotificationCenter.recentDocumentsDidLoad) {
            boolean booleanValue = ((Boolean) objArr[0]).booleanValue();
            i2 = ((Integer) objArr[1]).intValue();
            if (booleanValue || i2 == 0 || i2 == 2) {
                checkDocuments(booleanValue);
            }
        } else if (i == NotificationCenter.featuredStickersDidLoad) {
            if (this.trendingGridAdapter != null) {
                if (this.featuredStickersHash != DataQuery.getInstance(this.currentAccount).getFeaturesStickersHashWithoutUnread()) {
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
                i = pagerSlidingTabStrip.getChildCount();
                while (i3 < i) {
                    this.typeTabs.getChildAt(i3).invalidate();
                    i3++;
                }
            }
            updateStickerTabs();
        } else if (i == NotificationCenter.groupStickersDidLoad) {
            ChatFull chatFull = this.info;
            if (chatFull != null) {
                StickerSet stickerSet = chatFull.stickerset;
                if (stickerSet != null && stickerSet.id == ((Long) objArr[0]).longValue()) {
                    updateStickerTabs();
                }
            }
        } else if (i == NotificationCenter.emojiDidLoad) {
            RecyclerListView recyclerListView = this.stickersGridView;
            if (recyclerListView != null) {
                i = recyclerListView.getChildCount();
                while (i3 < i) {
                    View childAt = this.stickersGridView.getChildAt(i3);
                    if ((childAt instanceof StickerSetNameCell) || (childAt instanceof StickerEmojiCell)) {
                        childAt.invalidate();
                    }
                    i3++;
                }
            }
        } else if (i != NotificationCenter.newEmojiSuggestionsAvailable || this.emojiGridView == null || !this.needEmojiSearch) {
        } else {
            if ((this.emojiSearchField.progressDrawable.isAnimating() || this.emojiGridView.getAdapter() == this.emojiSearchAdapter) && !TextUtils.isEmpty(this.emojiSearchAdapter.lastSearchEmojiString)) {
                EmojiSearchAdapter emojiSearchAdapter = this.emojiSearchAdapter;
                emojiSearchAdapter.search(emojiSearchAdapter.lastSearchEmojiString);
            }
        }
    }
}
