package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLoader;
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
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC$TL_emojiURL;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC$TL_messages_foundStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_getEmojiURL;
import org.telegram.tgnet.TLRPC$TL_messages_getInlineBotResults;
import org.telegram.tgnet.TLRPC$TL_messages_getStickers;
import org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickers;
import org.telegram.tgnet.TLRPC$TL_photoSize;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$messages_BotResults;
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
import org.telegram.ui.Components.TrendingStickersLayout;
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
        public /* synthetic */ boolean needMenu() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needMenu(this);
        }

        public /* synthetic */ boolean needOpen() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needOpen(this);
        }

        public boolean needSend() {
            return true;
        }

        public void sendSticker(TLRPC$Document tLRPC$Document, Object obj, boolean z, int i) {
            EmojiView.this.delegate.onStickerSelected((View) null, tLRPC$Document, obj, z, i);
        }

        public boolean canSchedule() {
            return EmojiView.this.delegate.canSchedule();
        }

        public boolean isInScheduleMode() {
            return EmojiView.this.delegate.isInScheduleMode();
        }

        public void openSet(TLRPC$InputStickerSet tLRPC$InputStickerSet, boolean z) {
            if (tLRPC$InputStickerSet != null) {
                EmojiView.this.delegate.onShowStickerSet((TLRPC$StickerSet) null, tLRPC$InputStickerSet);
            }
        }

        public void sendGif(Object obj, Object obj2, boolean z, int i) {
            if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter) {
                EmojiView.this.delegate.onGifSelected((View) null, obj, obj2, z, i);
            } else if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter) {
                EmojiView.this.delegate.onGifSelected((View) null, obj, obj2, z, i);
            }
        }

        public void gifAddedOrDeleted() {
            EmojiView.this.updateRecentGifs();
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
    /* access modifiers changed from: private */
    public View emojiTabsShadow;
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
    public ArrayList<TLRPC$Document> favouriteStickers = new ArrayList<>();
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
    /* access modifiers changed from: private */
    public final Map<String, TLRPC$messages_BotResults> gifCache = new HashMap();
    private FrameLayout gifContainer;
    /* access modifiers changed from: private */
    public int gifFirstEmojiTabNum = -2;
    /* access modifiers changed from: private */
    public RecyclerListView gifGridView;
    private Drawable[] gifIcons;
    /* access modifiers changed from: private */
    public GifLayoutManager gifLayoutManager;
    private RecyclerListView.OnItemClickListener gifOnItemClickListener;
    /* access modifiers changed from: private */
    public int gifRecentTabNum = -2;
    /* access modifiers changed from: private */
    public GifAdapter gifSearchAdapter;
    /* access modifiers changed from: private */
    public SearchField gifSearchField;
    /* access modifiers changed from: private */
    public GifSearchPreloader gifSearchPreloader = new GifSearchPreloader();
    /* access modifiers changed from: private */
    public ScrollSlidingTabStrip gifTabs;
    /* access modifiers changed from: private */
    public int gifTrendingTabNum = -2;
    /* access modifiers changed from: private */
    public int groupStickerPackNum;
    /* access modifiers changed from: private */
    public int groupStickerPackPosition;
    /* access modifiers changed from: private */
    public TLRPC$TL_messages_stickerSet groupStickerSet;
    /* access modifiers changed from: private */
    public boolean groupStickersHidden;
    private int hasRecentEmoji = -1;
    /* access modifiers changed from: private */
    public TLRPC$ChatFull info;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC$StickerSetCovered> installingStickerSets = new LongSparseArray<>();
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
    private TLRPC$StickerSetCovered[] primaryInstallingStickerSets = new TLRPC$StickerSetCovered[10];
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$Document> recentGifs = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$Document> recentStickers = new ArrayList<>();
    /* access modifiers changed from: private */
    public int recentTabBum = -2;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC$StickerSetCovered> removingStickerSets = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public AnimatorSet searchAnimation;
    private ImageView searchButton;
    /* access modifiers changed from: private */
    public int searchFieldHeight;
    private View shadowLine;
    private Drawable[] stickerIcons;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$TL_messages_stickerSet> stickerSets = new ArrayList<>();
    /* access modifiers changed from: private */
    public ImageView stickerSettingsButton;
    private AnimatorSet stickersButtonAnimation;
    private FrameLayout stickersContainer;
    /* access modifiers changed from: private */
    public StickersGridAdapter stickersGridAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView stickersGridView;
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
    /* access modifiers changed from: private */
    public Drawable[] tabIcons;
    private final int[] tabsMinusDy = new int[3];
    private ObjectAnimator[] tabsYAnimators = new ObjectAnimator[3];
    private View topShadow;
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

            public static void $default$onClearEmojiRecent(EmojiViewDelegate emojiViewDelegate) {
            }

            public static void $default$onGifSelected(EmojiViewDelegate emojiViewDelegate, View view, Object obj, Object obj2, boolean z, int i) {
            }

            public static void $default$onSearchOpenClose(EmojiViewDelegate emojiViewDelegate, int i) {
            }

            public static void $default$onShowStickerSet(EmojiViewDelegate emojiViewDelegate, TLRPC$StickerSet tLRPC$StickerSet, TLRPC$InputStickerSet tLRPC$InputStickerSet) {
            }

            public static void $default$onStickerSelected(EmojiViewDelegate emojiViewDelegate, View view, TLRPC$Document tLRPC$Document, Object obj, boolean z, int i) {
            }

            public static void $default$onStickerSetAdd(EmojiViewDelegate emojiViewDelegate, TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
            }

            public static void $default$onStickerSetRemove(EmojiViewDelegate emojiViewDelegate, TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
            }

            public static void $default$onStickersGroupClick(EmojiViewDelegate emojiViewDelegate, int i) {
            }

            public static void $default$onStickersSettingsClick(EmojiViewDelegate emojiViewDelegate) {
            }

            public static void $default$onTabOpened(EmojiViewDelegate emojiViewDelegate, int i) {
            }

            public static void $default$showTrendingStickersAlert(EmojiViewDelegate emojiViewDelegate, TrendingStickersLayout trendingStickersLayout) {
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

        void onShowStickerSet(TLRPC$StickerSet tLRPC$StickerSet, TLRPC$InputStickerSet tLRPC$InputStickerSet);

        void onStickerSelected(View view, TLRPC$Document tLRPC$Document, Object obj, boolean z, int i);

        void onStickerSetAdd(TLRPC$StickerSetCovered tLRPC$StickerSetCovered);

        void onStickerSetRemove(TLRPC$StickerSetCovered tLRPC$StickerSetCovered);

        void onStickersGroupClick(int i);

        void onStickersSettingsClick();

        void onTabOpened(int i);

        void showTrendingStickersAlert(TrendingStickersLayout trendingStickersLayout);
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

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.stickersSearchField.searchEditText.setEnabled(z);
        this.gifSearchField.searchEditText.setEnabled(z);
        this.emojiSearchField.searchEditText.setEnabled(z);
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

        public SearchField(Context context, int i) {
            super(context);
            View view = new View(context);
            this.shadowView = view;
            view.setAlpha(0.0f);
            this.shadowView.setTag(1);
            this.shadowView.setBackgroundColor(Theme.getColor("chat_emojiPanelShadowLine"));
            addView(this.shadowView, new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83));
            View view2 = new View(context);
            this.backgroundView = view2;
            view2.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
            addView(this.backgroundView, new FrameLayout.LayoutParams(-1, EmojiView.this.searchFieldHeight));
            View view3 = new View(context);
            this.searchBackground = view3;
            view3.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor("chat_emojiSearchBackground")));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 14.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.searchIconImageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.searchIconImageView.setImageResource(NUM);
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiSearchIcon"), PorterDuff.Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 14.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView3 = this.clearSearchImageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
            this.progressDrawable = closeProgressDrawable2;
            imageView3.setImageDrawable(closeProgressDrawable2);
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
            AnonymousClass1 r0 = new EditTextBoldCursor(context, EmojiView.this, i) {
                final /* synthetic */ int val$type;

                {
                    this.val$type = r4;
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (!SearchField.this.searchEditText.isEnabled()) {
                        return super.onTouchEvent(motionEvent);
                    }
                    if (motionEvent.getAction() == 0) {
                        if (!EmojiView.this.delegate.isSearchOpened()) {
                            SearchField searchField = SearchField.this;
                            EmojiView.this.openSearch(searchField);
                        }
                        EmojiViewDelegate access$100 = EmojiView.this.delegate;
                        int i = 1;
                        if (this.val$type == 1) {
                            i = 2;
                        }
                        access$100.onSearchOpenClose(i);
                        SearchField.this.searchEditText.requestFocus();
                        AndroidUtilities.showKeyboard(SearchField.this.searchEditText);
                    }
                    return super.onTouchEvent(motionEvent);
                }
            };
            this.searchEditText = r0;
            r0.setTextSize(1, 16.0f);
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
            this.searchEditText.addTextChangedListener(new TextWatcher(EmojiView.this, i) {
                final /* synthetic */ int val$type;

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                {
                    this.val$type = r3;
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
                    int i = this.val$type;
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

    private class TypedScrollListener extends RecyclerView.OnScrollListener {
        private boolean smoothScrolling;
        private final int type;

        public TypedScrollListener(int i) {
            this.type = i;
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (recyclerView.getLayoutManager().isSmoothScrolling()) {
                this.smoothScrolling = true;
            } else if (i == 0) {
                if (!this.smoothScrolling) {
                    EmojiView.this.animateTabsY(this.type);
                }
                this.smoothScrolling = false;
            } else {
                if (i == 1) {
                    SearchField access$1400 = EmojiView.this.getSearchFieldForType(this.type);
                    if (access$1400 != null) {
                        access$1400.hideKeyboard();
                    }
                    this.smoothScrolling = false;
                }
                if (!this.smoothScrolling) {
                    EmojiView.this.stopAnimatingTabsY(this.type);
                }
            }
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            EmojiView.this.checkScroll(this.type);
            EmojiView.this.checkTabsY(this.type, i2);
            checkSearchFieldScroll();
            if (!this.smoothScrolling) {
                EmojiView.this.checkBottomTabScroll((float) i2);
            }
        }

        private void checkSearchFieldScroll() {
            int i = this.type;
            if (i == 0) {
                EmojiView.this.checkStickersSearchFieldScroll(false);
            } else if (i == 1) {
                EmojiView.this.checkEmojiSearchFieldScroll(false);
            } else if (i == 2) {
                EmojiView.this.checkGifSearchFieldScroll(false);
            }
        }
    }

    private class DraggableScrollSlidingTabStrip extends ScrollSlidingTabStrip {
        private float downX;
        private float downY;
        private boolean draggingHorizontally;
        private boolean draggingVertically;
        private boolean first = true;
        private float lastTranslateX;
        private float lastX;
        private boolean startedScroll;
        private final int touchSlop;
        private VelocityTracker vTracker;

        public DraggableScrollSlidingTabStrip(Context context) {
            super(context);
            this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (motionEvent.getAction() == 0) {
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                this.downX = motionEvent.getRawX();
                this.downY = motionEvent.getRawY();
            } else if (!this.draggingVertically && !this.draggingHorizontally && EmojiView.this.dragListener != null && Math.abs(motionEvent.getRawY() - this.downY) >= ((float) this.touchSlop)) {
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
            if (this.first) {
                this.first = false;
                this.lastX = motionEvent.getX();
            }
            if (motionEvent.getAction() == 0) {
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                this.downX = motionEvent.getRawX();
                this.downY = motionEvent.getRawY();
            } else if (!this.draggingVertically && !this.draggingHorizontally && EmojiView.this.dragListener != null) {
                if (Math.abs(motionEvent.getRawX() - this.downX) >= ((float) this.touchSlop)) {
                    this.draggingHorizontally = true;
                } else if (Math.abs(motionEvent.getRawY() - this.downY) >= ((float) this.touchSlop)) {
                    this.draggingVertically = true;
                    this.downY = motionEvent.getRawY();
                    EmojiView.this.dragListener.onDragStart();
                    if (this.startedScroll) {
                        EmojiView.this.pager.endFakeDrag();
                        this.startedScroll = false;
                    }
                }
            }
            if (this.draggingVertically) {
                if (this.vTracker == null) {
                    this.vTracker = VelocityTracker.obtain();
                }
                this.vTracker.addMovement(motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    this.vTracker.computeCurrentVelocity(1000);
                    float yVelocity = this.vTracker.getYVelocity();
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
            float translationX = getTranslationX();
            if (getScrollX() == 0 && translationX == 0.0f) {
                if (this.startedScroll || this.lastX - motionEvent.getX() >= 0.0f) {
                    if (this.startedScroll && this.lastX - motionEvent.getX() > 0.0f && EmojiView.this.pager.isFakeDragging()) {
                        EmojiView.this.pager.endFakeDrag();
                        this.startedScroll = false;
                    }
                } else if (EmojiView.this.pager.beginFakeDrag()) {
                    this.startedScroll = true;
                    this.lastTranslateX = getTranslationX();
                }
            }
            if (this.startedScroll) {
                motionEvent.getX();
                try {
                    this.lastTranslateX = translationX;
                } catch (Exception e) {
                    try {
                        EmojiView.this.pager.endFakeDrag();
                    } catch (Exception unused) {
                    }
                    this.startedScroll = false;
                    FileLog.e((Throwable) e);
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
                return true;
            }
            return false;
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

        public EmojiPopupWindow(EmojiView emojiView, View view, int i, int i2) {
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
                    int access$2800 = (EmojiView.this.emojiSize * i) + AndroidUtilities.dp((float) ((i * 4) + 5));
                    int dp4 = AndroidUtilities.dp(9.0f);
                    if (this.selection == i) {
                        this.rect.set((float) access$2800, (float) (dp4 - ((int) AndroidUtilities.dpf2(3.5f))), (float) (EmojiView.this.emojiSize + access$2800), (float) (EmojiView.this.emojiSize + dp4 + AndroidUtilities.dp(3.0f)));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.rectPaint);
                    }
                    String str = this.currentEmoji;
                    if (i != 0) {
                        str = EmojiView.addColorToCode(str, i != 1 ? i != 2 ? i != 3 ? i != 4 ? i != 5 ? "" : "🏿" : "🏾" : "🏽" : "🏼" : "🏻");
                    }
                    Drawable emojiBigDrawable = Emoji.getEmojiBigDrawable(str);
                    if (emojiBigDrawable != null) {
                        emojiBigDrawable.setBounds(access$2800, dp4, EmojiView.this.emojiSize + access$2800, EmojiView.this.emojiSize + dp4);
                        emojiBigDrawable.draw(canvas);
                    }
                    i++;
                }
            }
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public EmojiView(boolean r27, boolean r28, android.content.Context r29, boolean r30, org.telegram.tgnet.TLRPC$ChatFull r31) {
        /*
            r26 = this;
            r0 = r26
            r1 = r29
            r2 = r30
            r0.<init>(r1)
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r0.views = r3
            r3 = 1
            r0.firstEmojiAttach = r3
            r4 = -1
            r0.hasRecentEmoji = r4
            org.telegram.ui.Components.EmojiView$GifSearchPreloader r5 = new org.telegram.ui.Components.EmojiView$GifSearchPreloader
            r6 = 0
            r5.<init>()
            r0.gifSearchPreloader = r5
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            r0.gifCache = r5
            r0.firstGifAttach = r3
            r5 = -2
            r0.gifRecentTabNum = r5
            r0.gifTrendingTabNum = r5
            r0.gifFirstEmojiTabNum = r5
            r0.firstStickersAttach = r3
            r7 = 3
            int[] r8 = new int[r7]
            r0.tabsMinusDy = r8
            android.animation.ObjectAnimator[] r8 = new android.animation.ObjectAnimator[r7]
            r0.tabsYAnimators = r8
            int r8 = org.telegram.messenger.UserConfig.selectedAccount
            r0.currentAccount = r8
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            r0.stickerSets = r8
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            r0.recentGifs = r8
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            r0.recentStickers = r8
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            r0.favouriteStickers = r8
            r8 = 10
            org.telegram.tgnet.TLRPC$StickerSetCovered[] r8 = new org.telegram.tgnet.TLRPC$StickerSetCovered[r8]
            r0.primaryInstallingStickerSets = r8
            android.util.LongSparseArray r8 = new android.util.LongSparseArray
            r8.<init>()
            r0.installingStickerSets = r8
            android.util.LongSparseArray r8 = new android.util.LongSparseArray
            r8.<init>()
            r0.removingStickerSets = r8
            r8 = 2
            int[] r9 = new int[r8]
            r0.location = r9
            r0.recentTabBum = r5
            r0.favTabBum = r5
            r0.trendingTabNum = r5
            r0.currentBackgroundType = r4
            org.telegram.ui.Components.EmojiView$1 r9 = new org.telegram.ui.Components.EmojiView$1
            r9.<init>()
            r0.contentPreviewViewerDelegate = r9
            java.lang.String r9 = "chat_emojiBottomPanelIcon"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            int r11 = android.graphics.Color.red(r10)
            int r12 = android.graphics.Color.green(r10)
            int r10 = android.graphics.Color.blue(r10)
            r13 = 30
            int r10 = android.graphics.Color.argb(r13, r11, r12, r10)
            r11 = 1115684864(0x42800000, float:64.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r0.searchFieldHeight = r11
            r0.needEmojiSearch = r2
            android.graphics.drawable.Drawable[] r11 = new android.graphics.drawable.Drawable[r7]
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            java.lang.String r13 = "chat_emojiPanelIconSelected"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r15 = 2131165934(0x7var_ee, float:1.79461E38)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r15, r12, r14)
            r14 = 0
            r11[r14] = r12
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r5 = 2131165931(0x7var_eb, float:1.7946093E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r5, r12, r15)
            r11[r3] = r5
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r15 = 2131165935(0x7var_ef, float:1.7946101E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r15, r5, r12)
            r11[r8] = r5
            r0.tabIcons = r11
            r5 = 9
            android.graphics.drawable.Drawable[] r5 = new android.graphics.drawable.Drawable[r5]
            java.lang.String r11 = "chat_emojiPanelIcon"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r4 = 2131165925(0x7var_e5, float:1.794608E38)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r4, r12, r15)
            r5[r14] = r4
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r15 = 2131165926(0x7var_e6, float:1.7946083E38)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r15, r4, r12)
            r5[r3] = r4
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r15 = 2131165919(0x7var_df, float:1.7946069E38)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r15, r4, r12)
            r5[r8] = r4
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r15 = 2131165921(0x7var_e1, float:1.7946073E38)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r15, r4, r12)
            r5[r7] = r4
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r15 = 2131165918(0x7var_de, float:1.7946067E38)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r15, r4, r12)
            r12 = 4
            r5[r12] = r4
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r6 = 2131165927(0x7var_e7, float:1.7946085E38)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r6, r4, r15)
            r6 = 5
            r5[r6] = r4
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r6 = 2131165922(0x7var_e2, float:1.7946075E38)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r6, r4, r15)
            r6 = 6
            r5[r6] = r4
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r15 = 2131165923(0x7var_e3, float:1.7946077E38)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r15, r4, r6)
            r6 = 7
            r5[r6] = r4
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r11 = 2131165920(0x7var_e0, float:1.794607E38)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r11, r4, r6)
            r6 = 8
            r5[r6] = r4
            r0.emojiIcons = r5
            android.graphics.drawable.Drawable[] r4 = new android.graphics.drawable.Drawable[r12]
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r15 = 2131165950(0x7var_fe, float:1.7946132E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r15, r5, r11)
            r4[r14] = r5
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r15 = 2131165948(0x7var_fc, float:1.7946128E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r15, r5, r11)
            r4[r3] = r5
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r15 = 2131165953(0x7var_, float:1.7946138E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r15, r5, r11)
            r4[r8] = r5
            android.graphics.drawable.LayerDrawable r5 = new android.graphics.drawable.LayerDrawable
            android.graphics.drawable.Drawable[] r11 = new android.graphics.drawable.Drawable[r8]
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r6 = 2131165951(0x7var_ff, float:1.7946134E38)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r6, r15, r12)
            r11[r14] = r6
            java.lang.String r6 = "chat_emojiPanelStickerPackSelectorLine"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r14 = 2131165952(0x7var_, float:1.7946136E38)
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r14, r12, r15)
            r11[r3] = r12
            r5.<init>(r11)
            r4[r7] = r5
            r0.stickerIcons = r4
            android.graphics.drawable.Drawable[] r4 = new android.graphics.drawable.Drawable[r8]
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r12 = 2131165950(0x7var_fe, float:1.7946132E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r12, r5, r11)
            r11 = 0
            r4[r11] = r5
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r12 = 2131165949(0x7var_fd, float:1.794613E38)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createEmojiIconSelectorDrawable(r1, r12, r5, r9)
            r4[r3] = r5
            r0.gifIcons = r4
            r4 = 8
            java.lang.String[] r5 = new java.lang.String[r4]
            java.lang.String r4 = "Emoji1"
            r9 = 2131625108(0x7f0e0494, float:1.8877415E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r9)
            r5[r11] = r4
            java.lang.String r4 = "Emoji2"
            r9 = 2131625109(0x7f0e0495, float:1.8877417E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r9)
            r5[r3] = r4
            java.lang.String r4 = "Emoji3"
            r9 = 2131625110(0x7f0e0496, float:1.8877419E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r9)
            r5[r8] = r4
            java.lang.String r4 = "Emoji4"
            r9 = 2131625111(0x7f0e0497, float:1.887742E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r9)
            r5[r7] = r4
            java.lang.String r4 = "Emoji5"
            r9 = 2131625112(0x7f0e0498, float:1.8877423E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r9)
            r9 = 4
            r5[r9] = r4
            java.lang.String r4 = "Emoji6"
            r9 = 2131625113(0x7f0e0499, float:1.8877425E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r9)
            r9 = 5
            r5[r9] = r4
            java.lang.String r4 = "Emoji7"
            r9 = 2131625114(0x7f0e049a, float:1.8877427E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r9)
            r9 = 6
            r5[r9] = r4
            java.lang.String r4 = "Emoji8"
            r9 = 2131625115(0x7f0e049b, float:1.8877429E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r9)
            r9 = 7
            r5[r9] = r4
            r0.emojiTitles = r5
            r4 = r31
            r0.info = r4
            android.graphics.Paint r4 = new android.graphics.Paint
            r4.<init>(r3)
            r0.dotPaint = r4
            java.lang.String r5 = "chat_emojiPanelNewTrending"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setColor(r5)
            int r4 = android.os.Build.VERSION.SDK_INT
            r5 = 21
            if (r4 < r5) goto L_0x0292
            org.telegram.ui.Components.EmojiView$2 r4 = new org.telegram.ui.Components.EmojiView$2
            r4.<init>(r0)
            r0.outlineProvider = r4
        L_0x0292:
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r1)
            r0.emojiContainer = r4
            java.util.ArrayList<android.view.View> r9 = r0.views
            r9.add(r4)
            org.telegram.ui.Components.EmojiView$3 r4 = new org.telegram.ui.Components.EmojiView$3
            r4.<init>(r1)
            r0.emojiGridView = r4
            r4.setInstantClick(r3)
            org.telegram.ui.Components.RecyclerListView r4 = r0.emojiGridView
            androidx.recyclerview.widget.GridLayoutManager r9 = new androidx.recyclerview.widget.GridLayoutManager
            r11 = 8
            r9.<init>(r1, r11)
            r0.emojiLayoutManager = r9
            r4.setLayoutManager(r9)
            org.telegram.ui.Components.RecyclerListView r4 = r0.emojiGridView
            r9 = 1108869120(0x42180000, float:38.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r4.setTopGlowOffset(r11)
            org.telegram.ui.Components.RecyclerListView r4 = r0.emojiGridView
            r11 = 1111490560(0x42400000, float:48.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r4.setBottomGlowOffset(r11)
            org.telegram.ui.Components.RecyclerListView r4 = r0.emojiGridView
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r12 = 1110441984(0x42300000, float:44.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r14 = 0
            r4.setPadding(r14, r11, r14, r13)
            org.telegram.ui.Components.RecyclerListView r4 = r0.emojiGridView
            java.lang.String r11 = "chat_emojiPanelBackground"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setGlowColor(r13)
            org.telegram.ui.Components.RecyclerListView r4 = r0.emojiGridView
            r4.setClipToPadding(r14)
            androidx.recyclerview.widget.GridLayoutManager r4 = r0.emojiLayoutManager
            org.telegram.ui.Components.EmojiView$4 r13 = new org.telegram.ui.Components.EmojiView$4
            r13.<init>()
            r4.setSpanSizeLookup(r13)
            org.telegram.ui.Components.RecyclerListView r4 = r0.emojiGridView
            org.telegram.ui.Components.EmojiView$EmojiGridAdapter r13 = new org.telegram.ui.Components.EmojiView$EmojiGridAdapter
            r14 = 0
            r13.<init>()
            r0.emojiAdapter = r13
            r4.setAdapter(r13)
            org.telegram.ui.Components.EmojiView$EmojiSearchAdapter r4 = new org.telegram.ui.Components.EmojiView$EmojiSearchAdapter
            r4.<init>()
            r0.emojiSearchAdapter = r4
            android.widget.FrameLayout r4 = r0.emojiContainer
            org.telegram.ui.Components.RecyclerListView r13 = r0.emojiGridView
            r14 = -1082130432(0xffffffffbvar_, float:-1.0)
            r15 = -1
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r14)
            r4.addView(r13, r14)
            org.telegram.ui.Components.RecyclerListView r4 = r0.emojiGridView
            org.telegram.ui.Components.EmojiView$5 r13 = new org.telegram.ui.Components.EmojiView$5
            r13.<init>(r3)
            r4.setOnScrollListener(r13)
            org.telegram.ui.Components.RecyclerListView r4 = r0.emojiGridView
            org.telegram.ui.Components.EmojiView$6 r13 = new org.telegram.ui.Components.EmojiView$6
            r13.<init>(r0)
            r4.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r13)
            org.telegram.ui.Components.RecyclerListView r4 = r0.emojiGridView
            org.telegram.ui.Components.EmojiView$7 r13 = new org.telegram.ui.Components.EmojiView$7
            r13.<init>()
            r4.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r13)
            org.telegram.ui.Components.EmojiView$8 r4 = new org.telegram.ui.Components.EmojiView$8
            r4.<init>(r1)
            r0.emojiTabs = r4
            if (r2 == 0) goto L_0x0367
            org.telegram.ui.Components.EmojiView$SearchField r4 = new org.telegram.ui.Components.EmojiView$SearchField
            r4.<init>(r1, r3)
            r0.emojiSearchField = r4
            android.widget.FrameLayout r13 = r0.emojiContainer
            android.widget.FrameLayout$LayoutParams r14 = new android.widget.FrameLayout$LayoutParams
            int r15 = r0.searchFieldHeight
            int r17 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r15 = r15 + r17
            r7 = -1
            r14.<init>(r7, r15)
            r13.addView(r4, r14)
            org.telegram.ui.Components.EmojiView$SearchField r4 = r0.emojiSearchField
            org.telegram.ui.Components.EditTextBoldCursor r4 = r4.searchEditText
            org.telegram.ui.Components.EmojiView$9 r7 = new org.telegram.ui.Components.EmojiView$9
            r7.<init>()
            r4.setOnFocusChangeListener(r7)
        L_0x0367:
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.emojiTabs
            r4.setShouldExpand(r3)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.emojiTabs
            r7 = -1
            r4.setIndicatorHeight(r7)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.emojiTabs
            r4.setUnderlineHeight(r7)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.emojiTabs
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setBackgroundColor(r13)
            android.widget.FrameLayout r4 = r0.emojiContainer
            org.telegram.ui.Components.ScrollSlidingTabStrip r13 = r0.emojiTabs
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r9)
            r4.addView(r13, r14)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.emojiTabs
            org.telegram.ui.Components.EmojiView$10 r7 = new org.telegram.ui.Components.EmojiView$10
            r7.<init>()
            r4.setDelegate(r7)
            android.view.View r4 = new android.view.View
            r4.<init>(r1)
            r0.emojiTabsShadow = r4
            r7 = 0
            r4.setAlpha(r7)
            android.view.View r4 = r0.emojiTabsShadow
            java.lang.Integer r7 = java.lang.Integer.valueOf(r3)
            r4.setTag(r7)
            android.view.View r4 = r0.emojiTabsShadow
            java.lang.String r7 = "chat_emojiPanelShadowLine"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r4.setBackgroundColor(r13)
            android.widget.FrameLayout$LayoutParams r4 = new android.widget.FrameLayout$LayoutParams
            int r13 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r14 = 51
            r15 = -1
            r4.<init>(r15, r13, r14)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r4.topMargin = r9
            android.widget.FrameLayout r9 = r0.emojiContainer
            android.view.View r13 = r0.emojiTabsShadow
            r9.addView(r13, r4)
            if (r27 == 0) goto L_0x05cb
            if (r28 == 0) goto L_0x04cf
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r1)
            r0.gifContainer = r4
            java.util.ArrayList<android.view.View> r9 = r0.views
            r9.add(r4)
            org.telegram.ui.Components.EmojiView$11 r4 = new org.telegram.ui.Components.EmojiView$11
            r4.<init>(r1)
            r0.gifGridView = r4
            r9 = 0
            r4.setClipToPadding(r9)
            org.telegram.ui.Components.RecyclerListView r4 = r0.gifGridView
            org.telegram.ui.Components.EmojiView$GifLayoutManager r9 = new org.telegram.ui.Components.EmojiView$GifLayoutManager
            r9.<init>(r1)
            r0.gifLayoutManager = r9
            r4.setLayoutManager(r9)
            org.telegram.ui.Components.RecyclerListView r4 = r0.gifGridView
            org.telegram.ui.Components.EmojiView$12 r9 = new org.telegram.ui.Components.EmojiView$12
            r9.<init>()
            r4.addItemDecoration(r9)
            org.telegram.ui.Components.RecyclerListView r4 = r0.gifGridView
            r9 = 1112539136(0x42500000, float:52.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r15 = 0
            r4.setPadding(r15, r9, r15, r13)
            org.telegram.ui.Components.RecyclerListView r4 = r0.gifGridView
            r4.setOverScrollMode(r8)
            org.telegram.ui.Components.RecyclerListView r4 = r0.gifGridView
            androidx.recyclerview.widget.RecyclerView$ItemAnimator r4 = r4.getItemAnimator()
            androidx.recyclerview.widget.SimpleItemAnimator r4 = (androidx.recyclerview.widget.SimpleItemAnimator) r4
            r4.setSupportsChangeAnimations(r15)
            org.telegram.ui.Components.RecyclerListView r4 = r0.gifGridView
            org.telegram.ui.Components.EmojiView$GifAdapter r9 = new org.telegram.ui.Components.EmojiView$GifAdapter
            r9.<init>(r0, r1, r3)
            r0.gifAdapter = r9
            r4.setAdapter(r9)
            org.telegram.ui.Components.EmojiView$GifAdapter r4 = new org.telegram.ui.Components.EmojiView$GifAdapter
            r4.<init>(r0, r1)
            r0.gifSearchAdapter = r4
            org.telegram.ui.Components.RecyclerListView r4 = r0.gifGridView
            org.telegram.ui.Components.EmojiView$TypedScrollListener r9 = new org.telegram.ui.Components.EmojiView$TypedScrollListener
            r9.<init>(r8)
            r4.setOnScrollListener(r9)
            org.telegram.ui.Components.RecyclerListView r4 = r0.gifGridView
            org.telegram.ui.Components.-$$Lambda$EmojiView$l7-k5UkFPpeHpKVTvoFojtQIyNg r9 = new org.telegram.ui.Components.-$$Lambda$EmojiView$l7-k5UkFPpeHpKVTvoFojtQIyNg
            r9.<init>()
            r4.setOnTouchListener(r9)
            org.telegram.ui.Components.-$$Lambda$EmojiView$qDPcvu2cn8NHi3uzHeRPO6CJgew r4 = new org.telegram.ui.Components.-$$Lambda$EmojiView$qDPcvu2cn8NHi3uzHeRPO6CJgew
            r4.<init>()
            r0.gifOnItemClickListener = r4
            org.telegram.ui.Components.RecyclerListView r9 = r0.gifGridView
            r9.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r4)
            android.widget.FrameLayout r4 = r0.gifContainer
            org.telegram.ui.Components.RecyclerListView r9 = r0.gifGridView
            r13 = -1082130432(0xffffffffbvar_, float:-1.0)
            r15 = -1
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r13)
            r4.addView(r9, r13)
            org.telegram.ui.Components.EmojiView$SearchField r4 = new org.telegram.ui.Components.EmojiView$SearchField
            r4.<init>(r1, r8)
            r0.gifSearchField = r4
            r9 = 4
            r4.setVisibility(r9)
            android.widget.FrameLayout r4 = r0.gifContainer
            org.telegram.ui.Components.EmojiView$SearchField r9 = r0.gifSearchField
            android.widget.FrameLayout$LayoutParams r13 = new android.widget.FrameLayout$LayoutParams
            int r15 = r0.searchFieldHeight
            int r18 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r15 = r15 + r18
            r8 = -1
            r13.<init>(r8, r15)
            r4.addView(r9, r13)
            org.telegram.ui.Components.EmojiView$DraggableScrollSlidingTabStrip r4 = new org.telegram.ui.Components.EmojiView$DraggableScrollSlidingTabStrip
            r4.<init>(r1)
            r0.gifTabs = r4
            org.telegram.ui.Components.ScrollSlidingTabStrip$Type r8 = org.telegram.ui.Components.ScrollSlidingTabStrip.Type.TAB
            r4.setType(r8)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.gifTabs
            int r8 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r4.setUnderlineHeight(r8)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.gifTabs
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r4.setIndicatorColor(r8)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.gifTabs
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r4.setUnderlineColor(r8)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.gifTabs
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setBackgroundColor(r8)
            android.widget.FrameLayout r4 = r0.gifContainer
            org.telegram.ui.Components.ScrollSlidingTabStrip r8 = r0.gifTabs
            r9 = 48
            r13 = -1
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r9, r14)
            r4.addView(r8, r9)
            r26.updateGifTabs()
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.gifTabs
            org.telegram.ui.Components.-$$Lambda$EmojiView$6x9qYgYwoGy_8lRtkTlUjdfCDCM r8 = new org.telegram.ui.Components.-$$Lambda$EmojiView$6x9qYgYwoGy_8lRtkTlUjdfCDCM
            r8.<init>()
            r4.setDelegate(r8)
            org.telegram.ui.Components.EmojiView$GifAdapter r4 = r0.gifAdapter
            r4.loadTrendingGifs()
        L_0x04cf:
            android.widget.FrameLayout r4 = new android.widget.FrameLayout
            r4.<init>(r1)
            r0.stickersContainer = r4
            int r4 = r0.currentAccount
            org.telegram.messenger.MediaDataController r4 = org.telegram.messenger.MediaDataController.getInstance(r4)
            r8 = 0
            r4.checkStickers(r8)
            int r4 = r0.currentAccount
            org.telegram.messenger.MediaDataController r4 = org.telegram.messenger.MediaDataController.getInstance(r4)
            r4.checkFeaturedStickers()
            org.telegram.ui.Components.EmojiView$13 r4 = new org.telegram.ui.Components.EmojiView$13
            r4.<init>(r1)
            r0.stickersGridView = r4
            androidx.recyclerview.widget.GridLayoutManager r8 = new androidx.recyclerview.widget.GridLayoutManager
            r9 = 5
            r8.<init>(r1, r9)
            r0.stickersLayoutManager = r8
            r4.setLayoutManager(r8)
            androidx.recyclerview.widget.GridLayoutManager r4 = r0.stickersLayoutManager
            org.telegram.ui.Components.EmojiView$14 r8 = new org.telegram.ui.Components.EmojiView$14
            r8.<init>()
            r4.setSpanSizeLookup(r8)
            org.telegram.ui.Components.RecyclerListView r4 = r0.stickersGridView
            r8 = 1112539136(0x42500000, float:52.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r15 = 0
            r4.setPadding(r15, r8, r15, r13)
            org.telegram.ui.Components.RecyclerListView r4 = r0.stickersGridView
            r4.setClipToPadding(r15)
            java.util.ArrayList<android.view.View> r4 = r0.views
            android.widget.FrameLayout r8 = r0.stickersContainer
            r4.add(r8)
            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r4 = new org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter
            r4.<init>(r1)
            r0.stickersSearchGridAdapter = r4
            org.telegram.ui.Components.RecyclerListView r4 = r0.stickersGridView
            org.telegram.ui.Components.EmojiView$StickersGridAdapter r8 = new org.telegram.ui.Components.EmojiView$StickersGridAdapter
            r8.<init>(r1)
            r0.stickersGridAdapter = r8
            r4.setAdapter(r8)
            org.telegram.ui.Components.RecyclerListView r4 = r0.stickersGridView
            org.telegram.ui.Components.-$$Lambda$EmojiView$NzPX_Qg1AGn78eWJsMMwGj2aiYI r8 = new org.telegram.ui.Components.-$$Lambda$EmojiView$NzPX_Qg1AGn78eWJsMMwGj2aiYI
            r8.<init>()
            r4.setOnTouchListener(r8)
            org.telegram.ui.Components.-$$Lambda$EmojiView$dYtQT1qlPrE3oYWHySyt2vGHEgc r4 = new org.telegram.ui.Components.-$$Lambda$EmojiView$dYtQT1qlPrE3oYWHySyt2vGHEgc
            r4.<init>()
            r0.stickersOnItemClickListener = r4
            org.telegram.ui.Components.RecyclerListView r8 = r0.stickersGridView
            r8.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r4)
            org.telegram.ui.Components.RecyclerListView r4 = r0.stickersGridView
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setGlowColor(r8)
            android.widget.FrameLayout r4 = r0.stickersContainer
            org.telegram.ui.Components.RecyclerListView r8 = r0.stickersGridView
            r4.addView(r8)
            org.telegram.ui.Components.EmojiView$SearchField r4 = new org.telegram.ui.Components.EmojiView$SearchField
            r8 = 0
            r4.<init>(r1, r8)
            r0.stickersSearchField = r4
            android.widget.FrameLayout r8 = r0.stickersContainer
            android.widget.FrameLayout$LayoutParams r13 = new android.widget.FrameLayout$LayoutParams
            int r15 = r0.searchFieldHeight
            int r16 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r15 = r15 + r16
            r9 = -1
            r13.<init>(r9, r15)
            r8.addView(r4, r13)
            org.telegram.ui.Components.EmojiView$DraggableScrollSlidingTabStrip r4 = new org.telegram.ui.Components.EmojiView$DraggableScrollSlidingTabStrip
            r4.<init>(r1)
            r0.stickersTab = r4
            org.telegram.ui.Components.ScrollSlidingTabStrip$Type r8 = org.telegram.ui.Components.ScrollSlidingTabStrip.Type.TAB
            r4.setType(r8)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.stickersTab
            int r8 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r4.setUnderlineHeight(r8)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.stickersTab
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r4.setIndicatorColor(r6)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.stickersTab
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r4.setUnderlineColor(r6)
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.stickersTab
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setBackgroundColor(r6)
            android.widget.FrameLayout r4 = r0.stickersContainer
            org.telegram.ui.Components.ScrollSlidingTabStrip r6 = r0.stickersTab
            r8 = 48
            r9 = -1
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r8, r14)
            r4.addView(r6, r8)
            r26.updateStickerTabs()
            org.telegram.ui.Components.ScrollSlidingTabStrip r4 = r0.stickersTab
            org.telegram.ui.Components.-$$Lambda$EmojiView$VZHoX8xwavMw3KjWLEhwlMNniRM r6 = new org.telegram.ui.Components.-$$Lambda$EmojiView$VZHoX8xwavMw3KjWLEhwlMNniRM
            r6.<init>(r1)
            r4.setDelegate(r6)
            org.telegram.ui.Components.RecyclerListView r4 = r0.stickersGridView
            org.telegram.ui.Components.EmojiView$TypedScrollListener r6 = new org.telegram.ui.Components.EmojiView$TypedScrollListener
            r8 = 0
            r6.<init>(r8)
            r4.setOnScrollListener(r6)
        L_0x05cb:
            org.telegram.ui.Components.EmojiView$16 r4 = new org.telegram.ui.Components.EmojiView$16
            r4.<init>(r1)
            r0.pager = r4
            org.telegram.ui.Components.EmojiView$EmojiPagesAdapter r6 = new org.telegram.ui.Components.EmojiView$EmojiPagesAdapter
            r8 = 0
            r6.<init>()
            r4.setAdapter(r6)
            android.view.View r4 = new android.view.View
            r4.<init>(r1)
            r0.topShadow = r4
            r6 = 2131165425(0x7var_f1, float:1.7945067E38)
            r8 = -1907225(0xffffffffffe2e5e7, float:NaN)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r6, (int) r8)
            r4.setBackgroundDrawable(r6)
            android.view.View r4 = r0.topShadow
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            r8 = -1
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r6)
            r0.addView(r4, r6)
            org.telegram.ui.Components.EmojiView$17 r4 = new org.telegram.ui.Components.EmojiView$17
            r4.<init>(r1)
            r0.backspaceButton = r4
            r6 = 2131165930(0x7var_ea, float:1.794609E38)
            r4.setImageResource(r6)
            android.widget.ImageView r4 = r0.backspaceButton
            android.graphics.PorterDuffColorFilter r6 = new android.graphics.PorterDuffColorFilter
            java.lang.String r8 = "chat_emojiPanelBackspace"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r6.<init>(r8, r9)
            r4.setColorFilter(r6)
            android.widget.ImageView r4 = r0.backspaceButton
            android.widget.ImageView$ScaleType r6 = android.widget.ImageView.ScaleType.CENTER
            r4.setScaleType(r6)
            android.widget.ImageView r4 = r0.backspaceButton
            r6 = 2131623952(0x7f0e0010, float:1.887507E38)
            java.lang.String r8 = "AccDescrBackspace"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            r4.setContentDescription(r6)
            android.widget.ImageView r4 = r0.backspaceButton
            r4.setFocusable(r3)
            android.widget.ImageView r4 = r0.backspaceButton
            org.telegram.ui.Components.EmojiView$18 r6 = new org.telegram.ui.Components.EmojiView$18
            r6.<init>(r0)
            r4.setOnClickListener(r6)
            org.telegram.ui.Components.EmojiView$19 r4 = new org.telegram.ui.Components.EmojiView$19
            r4.<init>(r0, r1)
            r0.bottomTabContainer = r4
            android.view.View r4 = new android.view.View
            r4.<init>(r1)
            r0.shadowLine = r4
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r4.setBackgroundColor(r6)
            android.widget.FrameLayout r4 = r0.bottomTabContainer
            android.view.View r6 = r0.shadowLine
            android.widget.FrameLayout$LayoutParams r7 = new android.widget.FrameLayout$LayoutParams
            int r8 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r9 = -1
            r7.<init>(r9, r8)
            r4.addView(r6, r7)
            android.view.View r4 = new android.view.View
            r4.<init>(r1)
            r0.bottomTabContainerBackground = r4
            android.widget.FrameLayout r6 = r0.bottomTabContainer
            android.widget.FrameLayout$LayoutParams r7 = new android.widget.FrameLayout$LayoutParams
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r13 = 83
            r7.<init>(r9, r8, r13)
            r6.addView(r4, r7)
            r4 = 40
            r6 = 44
            if (r2 == 0) goto L_0x07b1
            android.widget.FrameLayout r2 = r0.bottomTabContainer
            android.widget.FrameLayout$LayoutParams r7 = new android.widget.FrameLayout$LayoutParams
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r11 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            int r8 = r8 + r11
            r11 = 83
            r7.<init>(r9, r8, r11)
            r0.addView(r2, r7)
            android.widget.FrameLayout r2 = r0.bottomTabContainer
            android.widget.ImageView r7 = r0.backspaceButton
            r8 = 52
            r9 = 85
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r6, r9)
            r2.addView(r7, r8)
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r5) goto L_0x06b2
            android.widget.ImageView r2 = r0.backspaceButton
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r10)
            r2.setBackground(r7)
        L_0x06b2:
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            r0.stickerSettingsButton = r2
            r7 = 2131165933(0x7var_ed, float:1.7946097E38)
            r2.setImageResource(r7)
            android.widget.ImageView r2 = r0.stickerSettingsButton
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            java.lang.String r8 = "chat_emojiPanelBackspace"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r9)
            r2.setColorFilter(r7)
            android.widget.ImageView r2 = r0.stickerSettingsButton
            android.widget.ImageView$ScaleType r7 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r7)
            android.widget.ImageView r2 = r0.stickerSettingsButton
            r2.setFocusable(r3)
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r5) goto L_0x06ea
            android.widget.ImageView r2 = r0.stickerSettingsButton
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r10)
            r2.setBackground(r7)
        L_0x06ea:
            android.widget.ImageView r2 = r0.stickerSettingsButton
            r7 = 2131626901(0x7f0e0b95, float:1.8881051E38)
            java.lang.String r8 = "Settings"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r2.setContentDescription(r7)
            android.widget.FrameLayout r2 = r0.bottomTabContainer
            android.widget.ImageView r7 = r0.stickerSettingsButton
            r8 = 52
            r9 = 85
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r6, r9)
            r2.addView(r7, r8)
            android.widget.ImageView r2 = r0.stickerSettingsButton
            org.telegram.ui.Components.EmojiView$20 r7 = new org.telegram.ui.Components.EmojiView$20
            r7.<init>()
            r2.setOnClickListener(r7)
            org.telegram.ui.Components.PagerSlidingTabStrip r2 = new org.telegram.ui.Components.PagerSlidingTabStrip
            r2.<init>(r1)
            r0.typeTabs = r2
            androidx.viewpager.widget.ViewPager r7 = r0.pager
            r2.setViewPager(r7)
            org.telegram.ui.Components.PagerSlidingTabStrip r2 = r0.typeTabs
            r7 = 0
            r2.setShouldExpand(r7)
            org.telegram.ui.Components.PagerSlidingTabStrip r2 = r0.typeTabs
            r2.setIndicatorHeight(r7)
            org.telegram.ui.Components.PagerSlidingTabStrip r2 = r0.typeTabs
            r2.setUnderlineHeight(r7)
            org.telegram.ui.Components.PagerSlidingTabStrip r2 = r0.typeTabs
            r7 = 1092616192(0x41200000, float:10.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r2.setTabPaddingLeftRight(r7)
            android.widget.FrameLayout r2 = r0.bottomTabContainer
            org.telegram.ui.Components.PagerSlidingTabStrip r7 = r0.typeTabs
            r8 = 81
            r9 = -2
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r6, r8)
            r2.addView(r7, r8)
            org.telegram.ui.Components.PagerSlidingTabStrip r2 = r0.typeTabs
            org.telegram.ui.Components.EmojiView$21 r7 = new org.telegram.ui.Components.EmojiView$21
            r7.<init>()
            r2.setOnPageChangeListener(r7)
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r1)
            r0.searchButton = r2
            r7 = 2131165932(0x7var_ec, float:1.7946095E38)
            r2.setImageResource(r7)
            android.widget.ImageView r2 = r0.searchButton
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            java.lang.String r8 = "chat_emojiPanelBackspace"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r9)
            r2.setColorFilter(r7)
            android.widget.ImageView r2 = r0.searchButton
            android.widget.ImageView$ScaleType r7 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r7)
            android.widget.ImageView r2 = r0.searchButton
            r7 = 2131626774(0x7f0e0b16, float:1.8880794E38)
            java.lang.String r8 = "Search"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r2.setContentDescription(r7)
            android.widget.ImageView r2 = r0.searchButton
            r2.setFocusable(r3)
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 < r5) goto L_0x0796
            android.widget.ImageView r2 = r0.searchButton
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r10)
            r2.setBackground(r5)
        L_0x0796:
            android.widget.FrameLayout r2 = r0.bottomTabContainer
            android.widget.ImageView r5 = r0.searchButton
            r7 = 52
            r8 = 83
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r6, r8)
            r2.addView(r5, r6)
            android.widget.ImageView r2 = r0.searchButton
            org.telegram.ui.Components.EmojiView$22 r5 = new org.telegram.ui.Components.EmojiView$22
            r5.<init>()
            r2.setOnClickListener(r5)
            goto L_0x08ef
        L_0x07b1:
            android.widget.FrameLayout r2 = r0.bottomTabContainer
            int r7 = android.os.Build.VERSION.SDK_INT
            if (r7 < r5) goto L_0x07ba
            r7 = 40
            goto L_0x07bc
        L_0x07ba:
            r7 = 44
        L_0x07bc:
            int r19 = r7 + 20
            int r7 = android.os.Build.VERSION.SDK_INT
            if (r7 < r5) goto L_0x07c5
            r7 = 40
            goto L_0x07c7
        L_0x07c5:
            r7 = 44
        L_0x07c7:
            int r7 = r7 + 12
            float r7 = (float) r7
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x07d1
            r16 = 3
            goto L_0x07d3
        L_0x07d1:
            r16 = 5
        L_0x07d3:
            r21 = r16 | 80
            r22 = 0
            r23 = 0
            r24 = 1073741824(0x40000000, float:2.0)
            r25 = 0
            r20 = r7
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r2, r7)
            r2 = 1113587712(0x42600000, float:56.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r2, r7, r8)
            int r7 = android.os.Build.VERSION.SDK_INT
            if (r7 >= r5) goto L_0x082e
            android.content.res.Resources r7 = r29.getResources()
            r8 = 2131165399(0x7var_d7, float:1.7945014E38)
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r8)
            android.graphics.drawable.Drawable r7 = r7.mutate()
            android.graphics.PorterDuffColorFilter r8 = new android.graphics.PorterDuffColorFilter
            r9 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.MULTIPLY
            r8.<init>(r9, r10)
            r7.setColorFilter(r8)
            org.telegram.ui.Components.CombinedDrawable r8 = new org.telegram.ui.Components.CombinedDrawable
            r9 = 0
            r8.<init>(r7, r2, r9, r9)
            r2 = 1109393408(0x42200000, float:40.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r7 = 1109393408(0x42200000, float:40.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r8.setIconSize(r2, r7)
            r2 = r8
            goto L_0x0898
        L_0x082e:
            r9 = 0
            android.animation.StateListAnimator r7 = new android.animation.StateListAnimator
            r7.<init>()
            int[] r8 = new int[r3]
            r10 = 16842919(0x10100a7, float:2.3694026E-38)
            r8[r9] = r10
            android.widget.ImageView r10 = r0.floatingButton
            android.util.Property r11 = android.view.View.TRANSLATION_Z
            r12 = 2
            float[] r13 = new float[r12]
            r12 = 1073741824(0x40000000, float:2.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r13[r9] = r12
            r12 = 1082130432(0x40800000, float:4.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r13[r3] = r12
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r10, r11, r13)
            r11 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r10 = r10.setDuration(r11)
            r7.addState(r8, r10)
            int[] r8 = new int[r9]
            android.widget.ImageView r10 = r0.floatingButton
            android.util.Property r11 = android.view.View.TRANSLATION_Z
            r12 = 2
            float[] r13 = new float[r12]
            r12 = 1082130432(0x40800000, float:4.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r13[r9] = r12
            r9 = 1073741824(0x40000000, float:2.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            r13[r3] = r9
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r10, r11, r13)
            r10 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r9 = r9.setDuration(r10)
            r7.addState(r8, r9)
            android.widget.ImageView r8 = r0.backspaceButton
            r8.setStateListAnimator(r7)
            android.widget.ImageView r7 = r0.backspaceButton
            org.telegram.ui.Components.EmojiView$23 r8 = new org.telegram.ui.Components.EmojiView$23
            r8.<init>(r0)
            r7.setOutlineProvider(r8)
        L_0x0898:
            android.widget.ImageView r7 = r0.backspaceButton
            r8 = 1073741824(0x40000000, float:2.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r9 = 0
            r7.setPadding(r9, r9, r8, r9)
            android.widget.ImageView r7 = r0.backspaceButton
            r7.setBackground(r2)
            android.widget.ImageView r2 = r0.backspaceButton
            r7 = 2131623952(0x7f0e0010, float:1.887507E38)
            java.lang.String r8 = "AccDescrBackspace"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r2.setContentDescription(r7)
            android.widget.ImageView r2 = r0.backspaceButton
            r2.setFocusable(r3)
            android.widget.FrameLayout r2 = r0.bottomTabContainer
            android.widget.ImageView r7 = r0.backspaceButton
            int r8 = android.os.Build.VERSION.SDK_INT
            if (r8 < r5) goto L_0x08c7
            r19 = 40
            goto L_0x08c9
        L_0x08c7:
            r19 = 44
        L_0x08c9:
            int r8 = android.os.Build.VERSION.SDK_INT
            if (r8 < r5) goto L_0x08cf
            r6 = 40
        L_0x08cf:
            float r5 = (float) r6
            r21 = 51
            r22 = 1092616192(0x41200000, float:10.0)
            r23 = 0
            r24 = 1092616192(0x41200000, float:10.0)
            r25 = 0
            r20 = r5
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r2.addView(r7, r5)
            android.view.View r2 = r0.shadowLine
            r5 = 8
            r2.setVisibility(r5)
            android.view.View r2 = r0.bottomTabContainerBackground
            r2.setVisibility(r5)
        L_0x08ef:
            androidx.viewpager.widget.ViewPager r2 = r0.pager
            r5 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r5, r14)
            r6 = 0
            r0.addView(r2, r6, r5)
            org.telegram.ui.Components.CorrectlyMeasuringTextView r2 = new org.telegram.ui.Components.CorrectlyMeasuringTextView
            r2.<init>(r1)
            r0.mediaBanTooltip = r2
            r5 = 1077936128(0x40400000, float:3.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.String r6 = "chat_gifSaveHintBackground"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createRoundRectDrawable(r5, r6)
            r2.setBackgroundDrawable(r5)
            android.widget.TextView r2 = r0.mediaBanTooltip
            java.lang.String r5 = "chat_gifSaveHintText"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r2.setTextColor(r5)
            android.widget.TextView r2 = r0.mediaBanTooltip
            r5 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 1088421888(0x40e00000, float:7.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r7 = 1090519040(0x41000000, float:8.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r8 = 1088421888(0x40e00000, float:7.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r2.setPadding(r5, r6, r7, r8)
            android.widget.TextView r2 = r0.mediaBanTooltip
            r5 = 16
            r2.setGravity(r5)
            android.widget.TextView r2 = r0.mediaBanTooltip
            r5 = 1096810496(0x41600000, float:14.0)
            r2.setTextSize(r3, r5)
            android.widget.TextView r2 = r0.mediaBanTooltip
            r5 = 4
            r2.setVisibility(r5)
            android.widget.TextView r2 = r0.mediaBanTooltip
            r5 = -2
            r6 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r7 = 81
            r8 = 1084227584(0x40a00000, float:5.0)
            r9 = 0
            r10 = 1084227584(0x40a00000, float:5.0)
            r11 = 1112801280(0x42540000, float:53.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6, r7, r8, r9, r10, r11)
            r0.addView(r2, r5)
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x096e
            r2 = 1109393408(0x42200000, float:40.0)
            goto L_0x0970
        L_0x096e:
            r2 = 1107296256(0x42000000, float:32.0)
        L_0x0970:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.emojiSize = r2
            org.telegram.ui.Components.EmojiView$EmojiColorPickerView r2 = new org.telegram.ui.Components.EmojiView$EmojiColorPickerView
            r2.<init>(r1)
            r0.pickerView = r2
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = new org.telegram.ui.Components.EmojiView$EmojiPopupWindow
            org.telegram.ui.Components.EmojiView$EmojiColorPickerView r2 = r0.pickerView
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 == 0) goto L_0x0988
            goto L_0x098a
        L_0x0988:
            r4 = 32
        L_0x098a:
            int r4 = r4 * 6
            int r4 = r4 + 10
            int r4 = r4 + 20
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r0.popupWidth = r4
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 == 0) goto L_0x09a0
            r5 = 1115684864(0x42800000, float:64.0)
            goto L_0x09a2
        L_0x09a0:
            r5 = 1113587712(0x42600000, float:56.0)
        L_0x09a2:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r0.popupHeight = r5
            r1.<init>(r0, r2, r4, r5)
            r0.pickerViewPopup = r1
            r1.setOutsideTouchable(r3)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = r0.pickerViewPopup
            r1.setClippingEnabled(r3)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = r0.pickerViewPopup
            r2 = 2
            r1.setInputMethodMode(r2)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = r0.pickerViewPopup
            r2 = 0
            r1.setSoftInputMode(r2)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = r0.pickerViewPopup
            android.view.View r1 = r1.getContentView()
            r1.setFocusableInTouchMode(r3)
            org.telegram.ui.Components.EmojiView$EmojiPopupWindow r1 = r0.pickerViewPopup
            android.view.View r1 = r1.getContentView()
            org.telegram.ui.Components.-$$Lambda$EmojiView$YBM1z0ObV6f6L9y0M-Yz0CakSrc r4 = new org.telegram.ui.Components.-$$Lambda$EmojiView$YBM1z0ObV6f6L9y0M-Yz0CakSrc
            r4.<init>()
            r1.setOnKeyListener(r4)
            android.content.SharedPreferences r1 = org.telegram.messenger.MessagesController.getGlobalEmojiSettings()
            java.lang.String r4 = "selected_page"
            int r1 = r1.getInt(r4, r2)
            r0.currentPage = r1
            org.telegram.messenger.Emoji.loadRecentEmoji()
            org.telegram.ui.Components.EmojiView$EmojiGridAdapter r1 = r0.emojiAdapter
            r1.notifyDataSetChanged()
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r0.typeTabs
            if (r1 == 0) goto L_0x0a1d
            java.util.ArrayList<android.view.View> r1 = r0.views
            int r1 = r1.size()
            if (r1 != r3) goto L_0x0a07
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r0.typeTabs
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0a07
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r0.typeTabs
            r2 = 4
            r1.setVisibility(r2)
            goto L_0x0a1d
        L_0x0a07:
            java.util.ArrayList<android.view.View> r1 = r0.views
            int r1 = r1.size()
            if (r1 == r3) goto L_0x0a1d
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r0.typeTabs
            int r1 = r1.getVisibility()
            if (r1 == 0) goto L_0x0a1d
            org.telegram.ui.Components.PagerSlidingTabStrip r1 = r0.typeTabs
            r2 = 0
            r1.setVisibility(r2)
        L_0x0a1d:
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
            RecyclerView.Adapter adapter = this.gifGridView.getAdapter();
            GifAdapter gifAdapter2 = this.gifAdapter;
            if (adapter != gifAdapter2) {
                RecyclerView.Adapter adapter2 = this.gifGridView.getAdapter();
                GifAdapter gifAdapter3 = this.gifSearchAdapter;
                if (adapter2 == gifAdapter3 && i2 >= 0 && i2 < gifAdapter3.results.size()) {
                    this.delegate.onGifSelected(view, this.gifSearchAdapter.results.get(i2), this.gifSearchAdapter.bot, true, 0);
                    updateRecentGifs();
                }
            } else if (i2 >= 0) {
                if (i2 < gifAdapter2.recentItemsCount) {
                    this.delegate.onGifSelected(view, this.recentGifs.get(i2), "gif", true, 0);
                    return;
                }
                if (this.gifAdapter.recentItemsCount > 0) {
                    i2 = (i2 - this.gifAdapter.recentItemsCount) - 1;
                }
                if (i2 >= 0 && i2 < this.gifAdapter.results.size()) {
                    this.delegate.onGifSelected(view, this.gifAdapter.results.get(i2), this.gifAdapter.bot, true, 0);
                }
            }
        }
    }

    public /* synthetic */ void lambda$new$3$EmojiView(int i) {
        if (i != this.gifTrendingTabNum || !this.gifAdapter.results.isEmpty()) {
            this.gifGridView.stopScroll();
            this.gifTabs.onPageScrolled(i, 0);
            int i2 = 1;
            if (i == this.gifRecentTabNum || i == this.gifTrendingTabNum) {
                this.gifSearchField.searchEditText.setText("");
                if (i != this.gifTrendingTabNum || this.gifAdapter.trendingSectionItem < 1) {
                    GifLayoutManager gifLayoutManager2 = this.gifLayoutManager;
                    EmojiViewDelegate emojiViewDelegate = this.delegate;
                    if (emojiViewDelegate != null && emojiViewDelegate.isExpanded()) {
                        i2 = 0;
                    }
                    gifLayoutManager2.scrollToPositionWithOffset(i2, 0);
                } else {
                    this.gifLayoutManager.scrollToPositionWithOffset(this.gifAdapter.trendingSectionItem, -AndroidUtilities.dp(4.0f));
                }
                if (i == this.gifTrendingTabNum) {
                    ArrayList<String> arrayList = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
                    if (!arrayList.isEmpty()) {
                        this.gifSearchPreloader.preload(arrayList.get(0));
                    }
                }
            } else {
                ArrayList<String> arrayList2 = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
                this.gifSearchAdapter.searchEmoji(arrayList2.get(i - this.gifFirstEmojiTabNum));
                int i3 = i - this.gifFirstEmojiTabNum;
                if (i3 > 0) {
                    this.gifSearchPreloader.preload(arrayList2.get(i3 - 1));
                }
                if (i - this.gifFirstEmojiTabNum < arrayList2.size() - 1) {
                    this.gifSearchPreloader.preload(arrayList2.get((i - this.gifFirstEmojiTabNum) + 1));
                }
            }
            resetTabsY(2);
        }
    }

    public /* synthetic */ boolean lambda$new$4$EmojiView(View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.stickersGridView, getMeasuredHeight(), this.stickersOnItemClickListener, this.contentPreviewViewerDelegate);
    }

    public /* synthetic */ void lambda$new$5$EmojiView(View view, int i) {
        TLRPC$StickerSetCovered tLRPC$StickerSetCovered;
        RecyclerView.Adapter adapter = this.stickersGridView.getAdapter();
        StickersSearchGridAdapter stickersSearchGridAdapter2 = this.stickersSearchGridAdapter;
        if (adapter == stickersSearchGridAdapter2 && (tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) stickersSearchGridAdapter2.positionsToSets.get(i)) != null) {
            this.delegate.onShowStickerSet(tLRPC$StickerSetCovered.set, (TLRPC$InputStickerSet) null);
        } else if (view instanceof StickerEmojiCell) {
            ContentPreviewViewer.getInstance().reset();
            StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
            if (!stickerEmojiCell.isDisabled()) {
                stickerEmojiCell.disable();
                this.delegate.onStickerSelected(stickerEmojiCell, stickerEmojiCell.getSticker(), stickerEmojiCell.getParentObject(), true, 0);
            }
        }
    }

    public /* synthetic */ void lambda$new$6$EmojiView(Context context, int i) {
        if (i == this.trendingTabNum) {
            this.delegate.showTrendingStickersAlert(new TrendingStickersLayout(context, new TrendingStickersLayout.Delegate() {
                public boolean canSendSticker() {
                    return true;
                }

                public void onStickerSetAdd(TLRPC$StickerSetCovered tLRPC$StickerSetCovered, boolean z) {
                    EmojiView.this.delegate.onStickerSetAdd(tLRPC$StickerSetCovered);
                    if (z) {
                        EmojiView.this.updateStickerTabs();
                    }
                }

                public void onStickerSetRemove(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
                    EmojiView.this.delegate.onStickerSetRemove(tLRPC$StickerSetCovered);
                }

                public boolean onListViewInterceptTouchEvent(RecyclerListView recyclerListView, MotionEvent motionEvent) {
                    return ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, recyclerListView, EmojiView.this.getMeasuredHeight(), EmojiView.this.contentPreviewViewerDelegate);
                }

                public boolean onListViewTouchEvent(RecyclerListView recyclerListView, RecyclerListView.OnItemClickListener onItemClickListener, MotionEvent motionEvent) {
                    return ContentPreviewViewer.getInstance().onTouch(motionEvent, recyclerListView, EmojiView.this.getMeasuredHeight(), onItemClickListener, EmojiView.this.contentPreviewViewerDelegate);
                }

                public String[] getLastSearchKeyboardLanguage() {
                    return EmojiView.this.lastSearchKeyboardLanguage;
                }

                public void setLastSearchKeyboardLanguage(String[] strArr) {
                    String[] unused = EmojiView.this.lastSearchKeyboardLanguage = strArr;
                }

                public void onStickerSelected(TLRPC$Document tLRPC$Document, Object obj, boolean z, boolean z2, int i) {
                    EmojiView.this.delegate.onStickerSelected((View) null, tLRPC$Document, obj, z2, i);
                }

                public boolean canSchedule() {
                    return EmojiView.this.delegate.canSchedule();
                }

                public boolean isInScheduleMode() {
                    return EmojiView.this.delegate.isInScheduleMode();
                }
            }, this.primaryInstallingStickerSets, this.installingStickerSets, this.removingStickerSets));
        } else if (i == this.recentTabBum) {
            this.stickersGridView.stopScroll();
            this.stickersLayoutManager.scrollToPositionWithOffset(this.stickersGridAdapter.getPositionForPack("recent"), 0);
            resetTabsY(0);
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            int i2 = this.recentTabBum;
            scrollSlidingTabStrip.onPageScrolled(i2, i2 > 0 ? i2 : this.stickersTabOffset);
        } else if (i == this.favTabBum) {
            this.stickersGridView.stopScroll();
            this.stickersLayoutManager.scrollToPositionWithOffset(this.stickersGridAdapter.getPositionForPack("fav"), 0);
            resetTabsY(0);
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
                resetTabsY(0);
                checkScroll(0);
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
        for (int i = 0; i < 3; i++) {
            GridLayoutManager layoutManagerForType = getLayoutManagerForType(i);
            int findFirstVisibleItemPosition = layoutManagerForType.findFirstVisibleItemPosition();
            if (z) {
                if (findFirstVisibleItemPosition == 1 || findFirstVisibleItemPosition == 2) {
                    layoutManagerForType.scrollToPosition(0);
                    resetTabsY(i);
                }
            } else if (findFirstVisibleItemPosition == 0) {
                layoutManagerForType.scrollToPositionWithOffset(1, 0);
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
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        this.firstStickersAttach = false;
        this.firstGifAttach = false;
        this.firstEmojiAttach = false;
        for (int i = 0; i < 3; i++) {
            boolean z = true;
            if (i == 0) {
                searchField2 = this.emojiSearchField;
                recyclerListView = this.emojiGridView;
                scrollSlidingTabStrip = this.emojiTabs;
                linearLayoutManager = this.emojiLayoutManager;
            } else if (i == 1) {
                searchField2 = this.gifSearchField;
                recyclerListView = this.gifGridView;
                scrollSlidingTabStrip = this.gifTabs;
                linearLayoutManager = this.gifLayoutManager;
            } else {
                searchField2 = this.stickersSearchField;
                recyclerListView = this.stickersGridView;
                scrollSlidingTabStrip = this.stickersTab;
                linearLayoutManager = this.stickersLayoutManager;
            }
            if (searchField2 != null) {
                if (searchField != searchField2 || (emojiViewDelegate = this.delegate) == null || !emojiViewDelegate.isExpanded()) {
                    searchField2.setTranslationY((float) AndroidUtilities.dp(0.0f));
                    if (scrollSlidingTabStrip != null) {
                        scrollSlidingTabStrip.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
                    }
                    if (recyclerListView == this.stickersGridView) {
                        recyclerListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                    } else if (recyclerListView == this.emojiGridView || recyclerListView == this.gifGridView) {
                        recyclerListView.setPadding(0, 0, 0, 0);
                    }
                    if (recyclerListView == this.gifGridView) {
                        GifAdapter gifAdapter2 = this.gifSearchAdapter;
                        if (this.gifAdapter.results.size() <= 0) {
                            z = false;
                        }
                        boolean unused = gifAdapter2.showTrendingWhenSearchEmpty = z;
                        if (z) {
                            this.gifSearchAdapter.search("");
                            RecyclerView.Adapter adapter = this.gifGridView.getAdapter();
                            GifAdapter gifAdapter3 = this.gifSearchAdapter;
                            if (adapter != gifAdapter3) {
                                this.gifGridView.setAdapter(gifAdapter3);
                            }
                        }
                    }
                    linearLayoutManager.scrollToPositionWithOffset(0, 0);
                } else {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.searchAnimation = animatorSet2;
                    if (scrollSlidingTabStrip != null) {
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(scrollSlidingTabStrip, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))}), ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))}), ObjectAnimator.ofFloat(searchField2, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(0.0f)})});
                    } else {
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))}), ObjectAnimator.ofFloat(searchField2, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(0.0f)})});
                    }
                    this.searchAnimation.setDuration(200);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                recyclerListView.setTranslationY(0.0f);
                                if (recyclerListView == EmojiView.this.stickersGridView) {
                                    recyclerListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                                } else if (recyclerListView == EmojiView.this.emojiGridView || recyclerListView == EmojiView.this.gifGridView) {
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
            }
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
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.emojiTabShadowAnimator = animatorSet2;
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
        TLRPC$TL_messages_stickerSet stickerSetById;
        int positionForPack;
        boolean z2 = z;
        long j2 = j;
        AnimatorSet animatorSet = this.searchAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        int currentItem = this.pager.getCurrentItem();
        if (currentItem == 2 && j2 != -1 && (stickerSetById = MediaDataController.getInstance(this.currentAccount).getStickerSetById(j2)) != null && (positionForPack = this.stickersGridAdapter.getPositionForPack(stickerSetById)) >= 0) {
            this.stickersLayoutManager.scrollToPositionWithOffset(positionForPack, AndroidUtilities.dp(60.0f));
        }
        GifAdapter gifAdapter2 = this.gifSearchAdapter;
        if (gifAdapter2 != null) {
            boolean unused = gifAdapter2.showTrendingWhenSearchEmpty = false;
        }
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                searchField = this.emojiSearchField;
                recyclerListView = this.emojiGridView;
                gridLayoutManager = this.emojiLayoutManager;
                scrollSlidingTabStrip = this.emojiTabs;
            } else if (i == 1) {
                searchField = this.gifSearchField;
                recyclerListView = this.gifGridView;
                gridLayoutManager = this.gifLayoutManager;
                scrollSlidingTabStrip = this.gifTabs;
            } else {
                searchField = this.stickersSearchField;
                recyclerListView = this.stickersGridView;
                gridLayoutManager = this.stickersLayoutManager;
                scrollSlidingTabStrip = this.stickersTab;
            }
            if (searchField != null) {
                searchField.searchEditText.setText("");
                if (i != currentItem || !z2) {
                    searchField.setTranslationY((float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight));
                    if (scrollSlidingTabStrip != null) {
                        scrollSlidingTabStrip.setTranslationY(0.0f);
                    }
                    if (recyclerListView == this.stickersGridView) {
                        recyclerListView.setPadding(0, AndroidUtilities.dp(52.0f), 0, AndroidUtilities.dp(44.0f));
                    } else if (recyclerListView == this.gifGridView) {
                        recyclerListView.setPadding(0, AndroidUtilities.dp(52.0f), 0, AndroidUtilities.dp(44.0f));
                    } else if (recyclerListView == this.emojiGridView) {
                        recyclerListView.setPadding(0, AndroidUtilities.dp(38.0f), 0, AndroidUtilities.dp(44.0f));
                    }
                    gridLayoutManager.scrollToPositionWithOffset(1, 0);
                } else {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.searchAnimation = animatorSet2;
                    if (scrollSlidingTabStrip != null) {
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(scrollSlidingTabStrip, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight)}), ObjectAnimator.ofFloat(searchField, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight)})});
                    } else {
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(recyclerListView, View.TRANSLATION_Y, new float[]{(float) (-this.searchFieldHeight)}), ObjectAnimator.ofFloat(searchField, View.TRANSLATION_Y, new float[]{(float) (-this.searchFieldHeight)})});
                    }
                    this.searchAnimation.setDuration(200);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                int findFirstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                                int top = findFirstVisibleItemPosition != -1 ? (int) (((float) gridLayoutManager.findViewByPosition(findFirstVisibleItemPosition).getTop()) + recyclerListView.getTranslationY()) : 0;
                                recyclerListView.setTranslationY(0.0f);
                                if (recyclerListView == EmojiView.this.stickersGridView) {
                                    recyclerListView.setPadding(0, AndroidUtilities.dp(52.0f), 0, AndroidUtilities.dp(44.0f));
                                } else if (recyclerListView == EmojiView.this.gifGridView) {
                                    recyclerListView.setPadding(0, AndroidUtilities.dp(52.0f), 0, AndroidUtilities.dp(44.0f));
                                } else if (recyclerListView == EmojiView.this.emojiGridView) {
                                    recyclerListView.setPadding(0, AndroidUtilities.dp(38.0f), 0, AndroidUtilities.dp(44.0f));
                                }
                                if (findFirstVisibleItemPosition != -1) {
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
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.backspaceButtonAnimation = animatorSet2;
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
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.stickersButtonAnimation = animatorSet2;
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
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.bottomTabContainerAnimation = animatorSet2;
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
    public void checkTabsY(int i, int i2) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (i == 1) {
            checkEmojiTabY(this.emojiGridView, i2);
            return;
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
            RecyclerListView listViewForType = getListViewForType(i);
            if (i2 <= 0 || listViewForType == null || listViewForType.getVisibility() != 0 || (findViewHolderForAdapterPosition = listViewForType.findViewHolderForAdapterPosition(0)) == null || findViewHolderForAdapterPosition.itemView.getTop() + this.searchFieldHeight < listViewForType.getPaddingTop()) {
                int[] iArr = this.tabsMinusDy;
                iArr[i] = iArr[i] - i2;
                if (iArr[i] > 0) {
                    iArr[i] = 0;
                } else if (iArr[i] < (-AndroidUtilities.dp(288.0f))) {
                    this.tabsMinusDy[i] = -AndroidUtilities.dp(288.0f);
                }
                getTabsForType(i).setTranslationY((float) Math.max(-AndroidUtilities.dp(48.0f), this.tabsMinusDy[i]));
            }
        }
    }

    private void resetTabsY(int i) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
            ScrollSlidingTabStrip tabsForType = getTabsForType(i);
            this.tabsMinusDy[i] = 0;
            tabsForType.setTranslationY((float) 0);
        }
    }

    /* access modifiers changed from: private */
    public void animateTabsY(int i) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
            float dpf2 = AndroidUtilities.dpf2(i == 1 ? 38.0f : 48.0f);
            float f = ((float) this.tabsMinusDy[i]) / (-dpf2);
            if (f <= 0.0f || f >= 1.0f) {
                animateSearchField(i);
                return;
            }
            ScrollSlidingTabStrip tabsForType = getTabsForType(i);
            int i2 = f > 0.5f ? (int) (-Math.ceil((double) dpf2)) : 0;
            if (f > 0.5f) {
                animateSearchField(i, false, i2);
            }
            if (i == 1) {
                checkEmojiShadow(i2);
            }
            ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
            if (objectAnimatorArr[i] == null) {
                objectAnimatorArr[i] = ObjectAnimator.ofFloat(tabsForType, View.TRANSLATION_Y, new float[]{tabsForType.getTranslationY(), (float) i2});
                this.tabsYAnimators[i].addUpdateListener(new ValueAnimator.AnimatorUpdateListener(i) {
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        EmojiView.this.lambda$animateTabsY$8$EmojiView(this.f$1, valueAnimator);
                    }
                });
                this.tabsYAnimators[i].setDuration(200);
            } else {
                objectAnimatorArr[i].setFloatValues(new float[]{tabsForType.getTranslationY(), (float) i2});
            }
            this.tabsYAnimators[i].start();
        }
    }

    public /* synthetic */ void lambda$animateTabsY$8$EmojiView(int i, ValueAnimator valueAnimator) {
        this.tabsMinusDy[i] = (int) ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    /* access modifiers changed from: private */
    public void stopAnimatingTabsY(int i) {
        ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
        if (objectAnimatorArr[i] != null && objectAnimatorArr[i].isRunning()) {
            this.tabsYAnimators[i].cancel();
        }
    }

    private void animateSearchField(int i) {
        RecyclerListView listViewForType = getListViewForType(i);
        boolean z = true;
        int dp = AndroidUtilities.dp(i == 1 ? 38.0f : 48.0f);
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = listViewForType.findViewHolderForAdapterPosition(0);
        if (findViewHolderForAdapterPosition != null) {
            float bottom = ((float) (findViewHolderForAdapterPosition.itemView.getBottom() - (dp + this.tabsMinusDy[i]))) / ((float) this.searchFieldHeight);
            if (bottom > 0.0f || bottom < 1.0f) {
                if (bottom <= 0.5f) {
                    z = false;
                }
                animateSearchField(i, z, this.tabsMinusDy[i]);
            }
        }
    }

    private void animateSearchField(int i, boolean z, final int i2) {
        if (getListViewForType(i).findViewHolderForAdapterPosition(0) != null) {
            AnonymousClass29 r0 = new LinearSmoothScroller(this, getContext()) {
                /* access modifiers changed from: protected */
                public int getVerticalSnapPreference() {
                    return -1;
                }

                /* access modifiers changed from: protected */
                public int calculateTimeForDeceleration(int i) {
                    return super.calculateTimeForDeceleration(i) * 16;
                }

                public int calculateDtToFit(int i, int i2, int i3, int i4, int i5) {
                    return super.calculateDtToFit(i, i2, i3, i4, i5) + i2;
                }
            };
            r0.setTargetPosition(z ^ true ? 1 : 0);
            getLayoutManagerForType(i).startSmoothScroll(r0);
        }
    }

    private ScrollSlidingTabStrip getTabsForType(int i) {
        if (i == 0) {
            return this.stickersTab;
        }
        if (i == 1) {
            return this.emojiTabs;
        }
        if (i == 2) {
            return this.gifTabs;
        }
        throw new IllegalArgumentException("Unexpected argument: " + i);
    }

    private RecyclerListView getListViewForType(int i) {
        if (i == 0) {
            return this.stickersGridView;
        }
        if (i == 1) {
            return this.emojiGridView;
        }
        if (i == 2) {
            return this.gifGridView;
        }
        throw new IllegalArgumentException("Unexpected argument: " + i);
    }

    private GridLayoutManager getLayoutManagerForType(int i) {
        if (i == 0) {
            return this.stickersLayoutManager;
        }
        if (i == 1) {
            return this.emojiLayoutManager;
        }
        if (i == 2) {
            return this.gifLayoutManager;
        }
        throw new IllegalArgumentException("Unexpected argument: " + i);
    }

    /* access modifiers changed from: private */
    public SearchField getSearchFieldForType(int i) {
        if (i == 0) {
            return this.stickersSearchField;
        }
        if (i == 1) {
            return this.emojiSearchField;
        }
        if (i == 2) {
            return this.gifSearchField;
        }
        throw new IllegalArgumentException("Unexpected argument: " + i);
    }

    /* access modifiers changed from: private */
    public void checkEmojiSearchFieldScroll(boolean z) {
        RecyclerListView recyclerListView;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
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
            checkEmojiShadow(Math.round(this.emojiTabs.getTranslationY()));
        }
    }

    private void checkEmojiShadow(int i) {
        ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
        if (objectAnimatorArr[1] == null || !objectAnimatorArr[1].isRunning()) {
            boolean z = false;
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.emojiGridView.findViewHolderForAdapterPosition(0);
            int dp = AndroidUtilities.dp(38.0f) + i;
            if (dp > 0 && (findViewHolderForAdapterPosition == null || findViewHolderForAdapterPosition.itemView.getBottom() < dp)) {
                z = true;
            }
            showEmojiShadow(z, !this.isLayout);
        }
    }

    /* access modifiers changed from: private */
    public void checkEmojiTabY(View view, int i) {
        RecyclerListView recyclerListView;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (view == null) {
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.emojiTabs;
            this.tabsMinusDy[1] = 0;
            scrollSlidingTabStrip.setTranslationY((float) 0);
        } else if (view.getVisibility() == 0) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
                if (i > 0 && (recyclerListView = this.emojiGridView) != null && recyclerListView.getVisibility() == 0 && (findViewHolderForAdapterPosition = this.emojiGridView.findViewHolderForAdapterPosition(0)) != null) {
                    if (findViewHolderForAdapterPosition.itemView.getTop() + (this.needEmojiSearch ? this.searchFieldHeight : 0) >= this.emojiGridView.getPaddingTop()) {
                        return;
                    }
                }
                int[] iArr = this.tabsMinusDy;
                iArr[1] = iArr[1] - i;
                if (iArr[1] > 0) {
                    iArr[1] = 0;
                } else if (iArr[1] < (-AndroidUtilities.dp(288.0f))) {
                    this.tabsMinusDy[1] = -AndroidUtilities.dp(288.0f);
                }
                this.emojiTabs.setTranslationY((float) Math.max(-AndroidUtilities.dp(38.0f), this.tabsMinusDy[1]));
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkGifSearchFieldScroll(boolean z) {
        RecyclerListView recyclerListView;
        int findLastVisibleItemPosition;
        RecyclerListView recyclerListView2 = this.gifGridView;
        if (recyclerListView2 != null && (recyclerListView2.getAdapter() instanceof GifAdapter)) {
            GifAdapter gifAdapter2 = (GifAdapter) this.gifGridView.getAdapter();
            if (!gifAdapter2.searchEndReached && gifAdapter2.reqId == 0 && !gifAdapter2.results.isEmpty() && (findLastVisibleItemPosition = this.gifLayoutManager.findLastVisibleItemPosition()) != -1 && findLastVisibleItemPosition > this.gifLayoutManager.getItemCount() - 5) {
                gifAdapter2.search(gifAdapter2.lastSearchImageString, gifAdapter2.nextSearchOffset, true, gifAdapter2.lastSearchIsEmoji, gifAdapter2.lastSearchIsEmoji);
            }
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
    public void scrollGifsToTop() {
        GifLayoutManager gifLayoutManager2 = this.gifLayoutManager;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        gifLayoutManager2.scrollToPositionWithOffset((emojiViewDelegate == null || !emojiViewDelegate.isExpanded()) ? 1 : 0, 0);
        resetTabsY(2);
    }

    /* access modifiers changed from: private */
    public void checkScroll(int i) {
        GifAdapter gifAdapter2;
        int findFirstVisibleItemPosition;
        if (i == 0) {
            int findFirstVisibleItemPosition2 = this.stickersLayoutManager.findFirstVisibleItemPosition();
            if (findFirstVisibleItemPosition2 != -1 && this.stickersGridView != null) {
                int i2 = this.favTabBum;
                if (i2 <= 0 && (i2 = this.recentTabBum) <= 0) {
                    i2 = this.stickersTabOffset;
                }
                this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition2), i2);
            }
        } else if (i == 2 && this.gifGridView.getAdapter() == (gifAdapter2 = this.gifAdapter) && gifAdapter2.trendingSectionItem >= 0 && this.gifTrendingTabNum >= 0 && this.gifRecentTabNum >= 0 && (findFirstVisibleItemPosition = this.gifLayoutManager.findFirstVisibleItemPosition()) != -1) {
            this.gifTabs.onPageScrolled(findFirstVisibleItemPosition >= this.gifAdapter.trendingSectionItem ? this.gifTrendingTabNum : this.gifRecentTabNum, 0);
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
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                EmojiView.this.lambda$postBackspaceRunnable$9$EmojiView(this.f$1);
            }
        }, (long) i);
    }

    public /* synthetic */ void lambda$postBackspaceRunnable$9$EmojiView(int i) {
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
        ArrayList<TLRPC$Document> arrayList;
        ArrayList<TLRPC$Document> arrayList2;
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
        if (scrollSlidingTabStrip != null) {
            this.recentTabBum = -2;
            this.favTabBum = -2;
            this.trendingTabNum = -2;
            this.stickersTabOffset = 0;
            int currentPosition = scrollSlidingTabStrip.getCurrentPosition();
            this.stickersTab.beginUpdate((getParent() == null || getVisibility() != 0 || (this.installingStickerSets.size() == 0 && this.removingStickerSets.size() == 0)) ? false : true);
            MediaDataController instance = MediaDataController.getInstance(this.currentAccount);
            if (!instance.getFeaturedStickerSets().isEmpty()) {
                int i = instance.getUnreadStickerSets().isEmpty() ? 2 : 3;
                this.stickersTab.addIconTab(i, this.stickerIcons[i]).setContentDescription(LocaleController.getString("FeaturedStickers", NUM));
                int i2 = this.stickersTabOffset;
                this.trendingTabNum = i2;
                this.stickersTabOffset = i2 + 1;
            }
            if (!this.favouriteStickers.isEmpty()) {
                int i3 = this.stickersTabOffset;
                this.favTabBum = i3;
                this.stickersTabOffset = i3 + 1;
                this.stickersTab.addIconTab(1, this.stickerIcons[1]).setContentDescription(LocaleController.getString("FavoriteStickers", NUM));
            }
            if (!this.recentStickers.isEmpty()) {
                int i4 = this.stickersTabOffset;
                this.recentTabBum = i4;
                this.stickersTabOffset = i4 + 1;
                this.stickersTab.addIconTab(0, this.stickerIcons[0]).setContentDescription(LocaleController.getString("RecentStickers", NUM));
            }
            this.stickerSets.clear();
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = null;
            this.groupStickerSet = null;
            this.groupStickerPackPosition = -1;
            this.groupStickerPackNum = -10;
            ArrayList<TLRPC$TL_messages_stickerSet> stickerSets2 = instance.getStickerSets(0);
            int i5 = 0;
            while (true) {
                TLRPC$StickerSetCovered[] tLRPC$StickerSetCoveredArr = this.primaryInstallingStickerSets;
                if (i5 >= tLRPC$StickerSetCoveredArr.length) {
                    break;
                }
                TLRPC$StickerSetCovered tLRPC$StickerSetCovered = tLRPC$StickerSetCoveredArr[i5];
                if (tLRPC$StickerSetCovered != null) {
                    TLRPC$TL_messages_stickerSet stickerSetById = instance.getStickerSetById(tLRPC$StickerSetCovered.set.id);
                    if (stickerSetById == null || stickerSetById.set.archived) {
                        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = new TLRPC$TL_messages_stickerSet();
                        tLRPC$TL_messages_stickerSet2.set = tLRPC$StickerSetCovered.set;
                        TLRPC$Document tLRPC$Document = tLRPC$StickerSetCovered.cover;
                        if (tLRPC$Document != null) {
                            tLRPC$TL_messages_stickerSet2.documents.add(tLRPC$Document);
                        } else if (!tLRPC$StickerSetCovered.covers.isEmpty()) {
                            tLRPC$TL_messages_stickerSet2.documents.addAll(tLRPC$StickerSetCovered.covers);
                        }
                        if (!tLRPC$TL_messages_stickerSet2.documents.isEmpty()) {
                            this.stickerSets.add(tLRPC$TL_messages_stickerSet2);
                        }
                    } else {
                        this.primaryInstallingStickerSets[i5] = null;
                    }
                }
                i5++;
            }
            for (int i6 = 0; i6 < stickerSets2.size(); i6++) {
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet3 = stickerSets2.get(i6);
                if (!tLRPC$TL_messages_stickerSet3.set.archived && (arrayList2 = tLRPC$TL_messages_stickerSet3.documents) != null && !arrayList2.isEmpty()) {
                    this.stickerSets.add(tLRPC$TL_messages_stickerSet3);
                }
            }
            if (this.info != null) {
                long j = MessagesController.getEmojiSettings(this.currentAccount).getLong("group_hide_stickers_" + this.info.id, -1);
                TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.info.id));
                if (chat == null || this.info.stickerset == null || !ChatObject.hasAdminRights(chat)) {
                    this.groupStickersHidden = j != -1;
                } else {
                    TLRPC$StickerSet tLRPC$StickerSet = this.info.stickerset;
                    if (tLRPC$StickerSet != null) {
                        this.groupStickersHidden = j == tLRPC$StickerSet.id;
                    }
                }
                TLRPC$ChatFull tLRPC$ChatFull = this.info;
                TLRPC$StickerSet tLRPC$StickerSet2 = tLRPC$ChatFull.stickerset;
                if (tLRPC$StickerSet2 != null) {
                    TLRPC$TL_messages_stickerSet groupStickerSetById = instance.getGroupStickerSetById(tLRPC$StickerSet2);
                    if (!(groupStickerSetById == null || (arrayList = groupStickerSetById.documents) == null || arrayList.isEmpty() || groupStickerSetById.set == null)) {
                        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet4 = new TLRPC$TL_messages_stickerSet();
                        tLRPC$TL_messages_stickerSet4.documents = groupStickerSetById.documents;
                        tLRPC$TL_messages_stickerSet4.packs = groupStickerSetById.packs;
                        tLRPC$TL_messages_stickerSet4.set = groupStickerSetById.set;
                        if (this.groupStickersHidden) {
                            this.groupStickerPackNum = this.stickerSets.size();
                            this.stickerSets.add(tLRPC$TL_messages_stickerSet4);
                        } else {
                            this.groupStickerPackNum = 0;
                            this.stickerSets.add(0, tLRPC$TL_messages_stickerSet4);
                        }
                        if (this.info.can_set_stickers) {
                            tLRPC$TL_messages_stickerSet = tLRPC$TL_messages_stickerSet4;
                        }
                        this.groupStickerSet = tLRPC$TL_messages_stickerSet;
                    }
                } else if (tLRPC$ChatFull.can_set_stickers) {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet5 = new TLRPC$TL_messages_stickerSet();
                    if (this.groupStickersHidden) {
                        this.groupStickerPackNum = this.stickerSets.size();
                        this.stickerSets.add(tLRPC$TL_messages_stickerSet5);
                    } else {
                        this.groupStickerPackNum = 0;
                        this.stickerSets.add(0, tLRPC$TL_messages_stickerSet5);
                    }
                }
            }
            int i7 = 0;
            while (i7 < this.stickerSets.size()) {
                if (i7 == this.groupStickerPackNum) {
                    TLRPC$Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.info.id));
                    if (chat2 == null) {
                        this.stickerSets.remove(0);
                        i7--;
                    } else {
                        this.stickersTab.addStickerTab(chat2);
                    }
                } else {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet6 = this.stickerSets.get(i7);
                    TLRPC$Document tLRPC$Document2 = tLRPC$TL_messages_stickerSet6.documents.get(0);
                    TLObject tLObject = tLRPC$TL_messages_stickerSet6.set.thumb;
                    if (!(tLObject instanceof TLRPC$TL_photoSize)) {
                        tLObject = tLRPC$Document2;
                    }
                    this.stickersTab.addStickerTab(tLObject, tLRPC$Document2, tLRPC$TL_messages_stickerSet6).setContentDescription(tLRPC$TL_messages_stickerSet6.set.title + ", " + LocaleController.getString("AccDescrStickerSet", NUM));
                }
                i7++;
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
        int findFirstVisibleItemPosition;
        if (this.stickersTab != null && (findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition()) != -1) {
            int i = this.favTabBum;
            if (i <= 0 && (i = this.recentTabBum) <= 0) {
                i = this.stickersTabOffset;
            }
            this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition), i);
        }
    }

    private void updateGifTabs() {
        int i;
        int currentPosition = this.gifTabs.getCurrentPosition();
        boolean z = currentPosition == this.gifRecentTabNum;
        boolean z2 = this.gifRecentTabNum >= 0;
        boolean z3 = !this.recentGifs.isEmpty();
        this.gifTabs.beginUpdate(false);
        this.gifRecentTabNum = -2;
        this.gifTrendingTabNum = -2;
        this.gifFirstEmojiTabNum = -2;
        if (z3) {
            this.gifRecentTabNum = 0;
            this.gifTabs.addIconTab(0, this.gifIcons[0]).setContentDescription(LocaleController.getString("RecentStickers", NUM));
            i = 1;
        } else {
            i = 0;
        }
        this.gifTrendingTabNum = i;
        this.gifTabs.addIconTab(1, this.gifIcons[1]).setContentDescription(LocaleController.getString("FeaturedGifs", NUM));
        this.gifFirstEmojiTabNum = i + 1;
        int dp = AndroidUtilities.dp(13.0f);
        int dp2 = AndroidUtilities.dp(11.0f);
        ArrayList<String> arrayList = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            String str = arrayList.get(i2);
            Emoji.EmojiDrawable emojiDrawable = Emoji.getEmojiDrawable(str);
            if (emojiDrawable != null) {
                ImageView addIconTab = this.gifTabs.addIconTab(i2 + 3, emojiDrawable);
                addIconTab.setPadding(dp, dp2, dp, dp2);
                addIconTab.setContentDescription(str);
            }
        }
        this.gifTabs.commitUpdate();
        this.gifTabs.updateTabStyles();
        if (z && !z3) {
            this.gifTabs.selectTab(this.gifTrendingTabNum);
        } else if (!ViewCompat.isLaidOut(this.gifTabs)) {
        } else {
            if (z3 && !z2) {
                this.gifTabs.onPageScrolled(currentPosition + 1, 0);
            } else if (!z3 && z2) {
                this.gifTabs.onPageScrolled(currentPosition - 1, 0);
            }
        }
    }

    public void addRecentSticker(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document != null) {
            MediaDataController.getInstance(this.currentAccount).addRecentSticker(0, (Object) null, tLRPC$Document, (int) (System.currentTimeMillis() / 1000), false);
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

    public void addRecentGif(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document != null) {
            boolean isEmpty = this.recentGifs.isEmpty();
            updateRecentGifs();
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
        ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
        if (scrollSlidingTabStrip2 != null) {
            scrollSlidingTabStrip2.setIndicatorColor(Theme.getColor("chat_emojiPanelStickerPackSelectorLine"));
            this.stickersTab.setUnderlineColor(Theme.getColor("chat_emojiPanelShadowLine"));
            this.stickersTab.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip3 = this.gifTabs;
        if (scrollSlidingTabStrip3 != null) {
            scrollSlidingTabStrip3.setIndicatorColor(Theme.getColor("chat_emojiPanelStickerPackSelectorLine"));
            this.gifTabs.setUnderlineColor(Theme.getColor("chat_emojiPanelShadowLine"));
            this.gifTabs.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
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
        GifAdapter gifAdapter2 = this.gifSearchAdapter;
        if (gifAdapter2 != null) {
            gifAdapter2.progressEmptyView.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
            this.gifSearchAdapter.progressEmptyView.textView.setTextColor(Theme.getColor("chat_emojiPanelEmptyText"));
            this.gifSearchAdapter.progressEmptyView.progressView.setProgressColor(Theme.getColor("progressCircle"));
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
            if (i4 >= drawableArr3.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr3[i4], Theme.getColor("chat_emojiPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.stickerIcons[i4], Theme.getColor("chat_emojiPanelIconSelected"), true);
            i4++;
        }
        int i5 = 0;
        while (true) {
            Drawable[] drawableArr4 = this.gifIcons;
            if (i5 < drawableArr4.length) {
                Theme.setEmojiDrawableColor(drawableArr4[i5], Theme.getColor("chat_emojiPanelIcon"), false);
                Theme.setEmojiDrawableColor(this.gifIcons[i5], Theme.getColor("chat_emojiPanelIconSelected"), true);
                i5++;
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

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
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
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            if (scrollSlidingTabStrip != null) {
                int i2 = this.favTabBum;
                if (i2 >= 0) {
                    scrollSlidingTabStrip.selectTab(i2);
                    return;
                }
                int i3 = this.recentTabBum;
                if (i3 >= 0) {
                    scrollSlidingTabStrip.selectTab(i3);
                } else {
                    scrollSlidingTabStrip.selectTab(this.stickersTabOffset);
                }
            }
        } else if (i == 2) {
            showBackspaceButton(false, false);
            showStickerSettingsButton(false, false);
            if (this.pager.getCurrentItem() != 1) {
                this.pager.setCurrentItem(1, false);
            }
            ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.gifTabs;
            if (scrollSlidingTabStrip2 != null) {
                scrollSlidingTabStrip2.selectTab(0);
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
                    EmojiView.this.lambda$onAttachedToWindow$10$EmojiView();
                }
            });
        }
    }

    public /* synthetic */ void lambda$onAttachedToWindow$10$EmojiView() {
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
            updateRecentGifs();
            return;
        }
        int size = this.recentStickers.size();
        int size2 = this.favouriteStickers.size();
        this.recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(0);
        this.favouriteStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(2);
        for (int i = 0; i < this.favouriteStickers.size(); i++) {
            TLRPC$Document tLRPC$Document = this.favouriteStickers.get(i);
            int i2 = 0;
            while (true) {
                if (i2 >= this.recentStickers.size()) {
                    break;
                }
                TLRPC$Document tLRPC$Document2 = this.recentStickers.get(i2);
                if (tLRPC$Document2.dc_id == tLRPC$Document.dc_id && tLRPC$Document2.id == tLRPC$Document.id) {
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

    /* access modifiers changed from: private */
    public void updateRecentGifs() {
        GifAdapter gifAdapter2;
        int size = this.recentGifs.size();
        int calcDocumentsHash = MediaDataController.calcDocumentsHash(this.recentGifs, Integer.MAX_VALUE);
        ArrayList<TLRPC$Document> recentGifs2 = MediaDataController.getInstance(this.currentAccount).getRecentGifs();
        this.recentGifs = recentGifs2;
        int calcDocumentsHash2 = MediaDataController.calcDocumentsHash(recentGifs2, Integer.MAX_VALUE);
        if ((this.gifTabs != null && size == 0 && !this.recentGifs.isEmpty()) || (size != 0 && this.recentGifs.isEmpty())) {
            updateGifTabs();
        }
        if ((size != this.recentGifs.size() || calcDocumentsHash != calcDocumentsHash2) && (gifAdapter2 = this.gifAdapter) != null) {
            gifAdapter2.notifyDataSetChanged();
        }
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
        TLRPC$Chat chat;
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights;
        if (this.mediaBanTooltip.getVisibility() != 0 && (chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.currentChatId))) != null) {
            if (ChatObject.hasAdminRights(chat) || (tLRPC$TL_chatBannedRights = chat.default_banned_rights) == null || !tLRPC$TL_chatBannedRights.send_stickers) {
                TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2 = chat.banned_rights;
                if (tLRPC$TL_chatBannedRights2 != null) {
                    if (AndroidUtilities.isBannedForever(tLRPC$TL_chatBannedRights2)) {
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
                            EmojiView.AnonymousClass30.this.lambda$onAnimationEnd$0$EmojiView$30();
                        }
                    }, 5000);
                }

                public /* synthetic */ void lambda$onAnimationEnd$0$EmojiView$30() {
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
        boolean z;
        RecyclerListView recyclerListView = this.stickersGridView;
        if (recyclerListView != null) {
            try {
                int childCount = recyclerListView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = this.stickersGridView.getChildAt(i);
                    if (childAt instanceof FeaturedStickerSetInfoCell) {
                        if (((RecyclerListView.Holder) this.stickersGridView.getChildViewHolder(childAt)) != null) {
                            FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) childAt;
                            ArrayList<Long> unreadStickerSets = MediaDataController.getInstance(this.currentAccount).getUnreadStickerSets();
                            TLRPC$StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
                            boolean z2 = unreadStickerSets != null && unreadStickerSets.contains(Long.valueOf(stickerSet.set.id));
                            int i2 = 0;
                            while (true) {
                                if (i2 < this.primaryInstallingStickerSets.length) {
                                    if (this.primaryInstallingStickerSets[i2] != null && this.primaryInstallingStickerSets[i2].set.id == stickerSet.set.id) {
                                        z = true;
                                        break;
                                    }
                                    i2++;
                                } else {
                                    z = false;
                                    break;
                                }
                            }
                            featuredStickerSetInfoCell.setStickerSet(stickerSet, z2, true, 0, 0, z);
                            if (z2) {
                                MediaDataController.getInstance(this.currentAccount).markFaturedStickersByIdAsRead(stickerSet.set.id);
                            }
                            boolean z3 = this.installingStickerSets.indexOfKey(stickerSet.set.id) >= 0;
                            boolean z4 = this.removingStickerSets.indexOfKey(stickerSet.set.id) >= 0;
                            if (z3 || z4) {
                                if (z3 && featuredStickerSetInfoCell.isInstalled()) {
                                    this.installingStickerSets.remove(stickerSet.set.id);
                                    z3 = false;
                                } else if (z4 && !featuredStickerSetInfoCell.isInstalled()) {
                                    this.removingStickerSets.remove(stickerSet.set.id);
                                }
                            }
                            featuredStickerSetInfoCell.setAddDrawProgress(!z && z3, true);
                        }
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public boolean areThereAnyStickers() {
        StickersGridAdapter stickersGridAdapter2 = this.stickersGridAdapter;
        return stickersGridAdapter2 != null && stickersGridAdapter2.getItemCount() > 0;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC$StickerSet tLRPC$StickerSet;
        int i3 = 0;
        if (i == NotificationCenter.stickersDidLoad) {
            if (objArr[0].intValue() == 0) {
                updateStickerTabs();
                updateVisibleTrendingSets();
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
            updateVisibleTrendingSets();
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
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            if (tLRPC$ChatFull != null && (tLRPC$StickerSet = tLRPC$ChatFull.stickerset) != null && tLRPC$StickerSet.id == objArr[0].longValue()) {
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
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.gifTabs;
            if (scrollSlidingTabStrip != null) {
                scrollSlidingTabStrip.invalidateTabs();
            }
        } else if (i == NotificationCenter.newEmojiSuggestionsAvailable && this.emojiGridView != null && this.needEmojiSearch) {
            if ((this.emojiSearchField.progressDrawable.isAnimating() || this.emojiGridView.getAdapter() == this.emojiSearchAdapter) && !TextUtils.isEmpty(this.emojiSearchAdapter.lastSearchEmojiString)) {
                EmojiSearchAdapter emojiSearchAdapter2 = this.emojiSearchAdapter;
                emojiSearchAdapter2.search(emojiSearchAdapter2.lastSearchEmojiString);
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
            if (obj == null) {
                return 1;
            }
            if (obj instanceof TLRPC$Document) {
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
                return EmojiView.this.stickerSets.indexOf((TLRPC$TL_messages_stickerSet) obj) + EmojiView.this.stickersTabOffset;
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
                stickerSetGroupInfoCell = new StickerEmojiCell(this, this.context) {
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

        /* JADX WARNING: type inference failed for: r2v1 */
        /* JADX WARNING: type inference failed for: r2v2, types: [java.util.ArrayList] */
        /* JADX WARNING: type inference failed for: r2v8, types: [org.telegram.tgnet.TLRPC$Chat] */
        /* JADX WARNING: type inference failed for: r2v17 */
        /* JADX WARNING: type inference failed for: r2v18 */
        /* JADX WARNING: type inference failed for: r2v19 */
        /* JADX WARNING: type inference failed for: r2v20 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r7, int r8) {
            /*
                r6 = this;
                int r0 = r7.getItemViewType()
                r1 = 0
                if (r0 == 0) goto L_0x0154
                r2 = 0
                r3 = 1
                if (r0 == r3) goto L_0x00cd
                r4 = 2
                if (r0 == r4) goto L_0x0022
                r2 = 3
                if (r0 == r2) goto L_0x0013
                goto L_0x0176
            L_0x0013:
                android.view.View r7 = r7.itemView
                org.telegram.ui.Cells.StickerSetGroupInfoCell r7 = (org.telegram.ui.Cells.StickerSetGroupInfoCell) r7
                int r0 = r6.totalItems
                int r0 = r0 - r3
                if (r8 != r0) goto L_0x001d
                r1 = 1
            L_0x001d:
                r7.setIsLast(r1)
                goto L_0x0176
            L_0x0022:
                android.view.View r7 = r7.itemView
                org.telegram.ui.Cells.StickerSetNameCell r7 = (org.telegram.ui.Cells.StickerSetNameCell) r7
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                int r0 = r0.groupStickerPackPosition
                if (r8 != r0) goto L_0x008a
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                boolean r8 = r8.groupStickersHidden
                if (r8 == 0) goto L_0x0040
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = r8.groupStickerSet
                if (r8 != 0) goto L_0x0040
                r8 = 0
                goto L_0x004f
            L_0x0040:
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = r8.groupStickerSet
                if (r8 == 0) goto L_0x004c
                r8 = 2131165954(0x7var_, float:1.794614E38)
                goto L_0x004f
            L_0x004c:
                r8 = 2131165955(0x7var_, float:1.7946142E38)
            L_0x004f:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                org.telegram.tgnet.TLRPC$ChatFull r0 = r0.info
                if (r0 == 0) goto L_0x0071
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                int r0 = r0.currentAccount
                org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
                org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.info
                int r2 = r2.id
                java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                org.telegram.tgnet.TLRPC$Chat r2 = r0.getChat(r2)
            L_0x0071:
                r0 = 2131624896(0x7f0e03c0, float:1.8876985E38)
                java.lang.Object[] r3 = new java.lang.Object[r3]
                if (r2 == 0) goto L_0x007b
                java.lang.String r2 = r2.title
                goto L_0x007d
            L_0x007b:
                java.lang.String r2 = "Group Stickers"
            L_0x007d:
                r3[r1] = r2
                java.lang.String r1 = "CurrentGroupStickers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
                r7.setText(r0, r8)
                goto L_0x0176
            L_0x008a:
                android.util.SparseArray<java.lang.Object> r0 = r6.cache
                java.lang.Object r8 = r0.get(r8)
                boolean r0 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
                if (r0 == 0) goto L_0x00a1
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r8
                org.telegram.tgnet.TLRPC$StickerSet r8 = r8.set
                if (r8 == 0) goto L_0x0176
                java.lang.String r8 = r8.title
                r7.setText(r8, r1)
                goto L_0x0176
            L_0x00a1:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r0 = r0.recentStickers
                if (r8 != r0) goto L_0x00b7
                r8 = 2131626641(0x7f0e0a91, float:1.8880524E38)
                java.lang.String r0 = "RecentStickers"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r0, r8)
                r7.setText(r8, r1)
                goto L_0x0176
            L_0x00b7:
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r0 = r0.favouriteStickers
                if (r8 != r0) goto L_0x0176
                r8 = 2131625268(0x7f0e0534, float:1.887774E38)
                java.lang.String r0 = "FavoriteStickers"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r0, r8)
                r7.setText(r8, r1)
                goto L_0x0176
            L_0x00cd:
                android.view.View r7 = r7.itemView
                org.telegram.ui.Cells.EmptyCell r7 = (org.telegram.ui.Cells.EmptyCell) r7
                int r0 = r6.totalItems
                r1 = 1118044160(0x42a40000, float:82.0)
                if (r8 != r0) goto L_0x014c
                android.util.SparseIntArray r0 = r6.positionToRow
                int r8 = r8 - r3
                r4 = -2147483648(0xfffffffvar_, float:-0.0)
                int r8 = r0.get(r8, r4)
                if (r8 != r4) goto L_0x00e7
                r7.setHeight(r3)
                goto L_0x0176
            L_0x00e7:
                android.util.SparseArray<java.lang.Object> r0 = r6.rowStartPack
                java.lang.Object r8 = r0.get(r8)
                boolean r0 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
                if (r0 == 0) goto L_0x00f6
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r8
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r8.documents
                goto L_0x010f
            L_0x00f6:
                boolean r0 = r8 instanceof java.lang.String
                if (r0 == 0) goto L_0x010f
                java.lang.String r0 = "recent"
                boolean r8 = r0.equals(r8)
                if (r8 == 0) goto L_0x0109
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r2 = r8.recentStickers
                goto L_0x010f
            L_0x0109:
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r2 = r8.favouriteStickers
            L_0x010f:
                if (r2 != 0) goto L_0x0115
                r7.setHeight(r3)
                goto L_0x0176
            L_0x0115:
                boolean r8 = r2.isEmpty()
                if (r8 == 0) goto L_0x0125
                r8 = 1090519040(0x41000000, float:8.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r7.setHeight(r8)
                goto L_0x0176
            L_0x0125:
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                androidx.viewpager.widget.ViewPager r8 = r8.pager
                int r8 = r8.getHeight()
                int r0 = r2.size()
                float r0 = (float) r0
                int r2 = r6.stickersPerRow
                float r2 = (float) r2
                float r0 = r0 / r2
                double r4 = (double) r0
                double r4 = java.lang.Math.ceil(r4)
                int r0 = (int) r4
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r0 = r0 * r1
                int r8 = r8 - r0
                if (r8 <= 0) goto L_0x0148
                r3 = r8
            L_0x0148:
                r7.setHeight(r3)
                goto L_0x0176
            L_0x014c:
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r7.setHeight(r8)
                goto L_0x0176
            L_0x0154:
                android.util.SparseArray<java.lang.Object> r0 = r6.cache
                java.lang.Object r0 = r0.get(r8)
                org.telegram.tgnet.TLRPC$Document r0 = (org.telegram.tgnet.TLRPC$Document) r0
                android.view.View r7 = r7.itemView
                org.telegram.ui.Cells.StickerEmojiCell r7 = (org.telegram.ui.Cells.StickerEmojiCell) r7
                android.util.SparseArray<java.lang.Object> r2 = r6.cacheParents
                java.lang.Object r8 = r2.get(r8)
                r7.setSticker(r0, r8, r1)
                org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                java.util.ArrayList r8 = r8.recentStickers
                boolean r8 = r8.contains(r0)
                r7.setRecent(r8)
            L_0x0176:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersGridAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public void notifyDataSetChanged() {
            Object obj;
            ArrayList<TLRPC$Document> arrayList;
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
            ArrayList access$10800 = EmojiView.this.stickerSets;
            int i2 = -3;
            int i3 = -3;
            int i4 = 0;
            while (i3 < access$10800.size()) {
                if (i3 == i2) {
                    SparseArray<Object> sparseArray = this.cache;
                    int i5 = this.totalItems;
                    this.totalItems = i5 + 1;
                    sparseArray.put(i5, "search");
                    i4++;
                } else {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = null;
                    if (i3 == -2) {
                        arrayList = EmojiView.this.favouriteStickers;
                        this.packStartPosition.put("fav", Integer.valueOf(this.totalItems));
                        obj = "fav";
                    } else if (i3 == -1) {
                        arrayList = EmojiView.this.recentStickers;
                        this.packStartPosition.put("recent", Integer.valueOf(this.totalItems));
                        obj = "recent";
                    } else {
                        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = (TLRPC$TL_messages_stickerSet) access$10800.get(i3);
                        ArrayList<TLRPC$Document> arrayList2 = tLRPC$TL_messages_stickerSet2.documents;
                        this.packStartPosition.put(tLRPC$TL_messages_stickerSet2, Integer.valueOf(this.totalItems));
                        tLRPC$TL_messages_stickerSet = tLRPC$TL_messages_stickerSet2;
                        arrayList = arrayList2;
                        obj = null;
                    }
                    if (i3 == EmojiView.this.groupStickerPackNum) {
                        int unused = EmojiView.this.groupStickerPackPosition = this.totalItems;
                        if (arrayList.isEmpty()) {
                            this.rowStartPack.put(i4, tLRPC$TL_messages_stickerSet);
                            int i6 = i4 + 1;
                            this.positionToRow.put(this.totalItems, i4);
                            this.rowStartPack.put(i6, tLRPC$TL_messages_stickerSet);
                            this.positionToRow.put(this.totalItems + 1, i6);
                            SparseArray<Object> sparseArray2 = this.cache;
                            int i7 = this.totalItems;
                            this.totalItems = i7 + 1;
                            sparseArray2.put(i7, tLRPC$TL_messages_stickerSet);
                            SparseArray<Object> sparseArray3 = this.cache;
                            int i8 = this.totalItems;
                            this.totalItems = i8 + 1;
                            sparseArray3.put(i8, "group");
                            i4 = i6 + 1;
                        }
                    }
                    if (!arrayList.isEmpty()) {
                        int ceil = (int) Math.ceil((double) (((float) arrayList.size()) / ((float) this.stickersPerRow)));
                        if (tLRPC$TL_messages_stickerSet != null) {
                            this.cache.put(this.totalItems, tLRPC$TL_messages_stickerSet);
                        } else {
                            this.cache.put(this.totalItems, arrayList);
                        }
                        this.positionToRow.put(this.totalItems, i4);
                        int i9 = 0;
                        while (i9 < arrayList.size()) {
                            int i10 = i9 + 1;
                            int i11 = this.totalItems + i10;
                            this.cache.put(i11, arrayList.get(i9));
                            if (tLRPC$TL_messages_stickerSet != null) {
                                this.cacheParents.put(i11, tLRPC$TL_messages_stickerSet);
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
                            if (tLRPC$TL_messages_stickerSet != null) {
                                this.rowStartPack.put(i4 + i12, tLRPC$TL_messages_stickerSet);
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

        /* JADX WARNING: Code restructure failed: missing block: B:20:0x006b, code lost:
            r0 = r10;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r9, int r10) {
            /*
                r8 = this;
                int r0 = r9.getItemViewType()
                r1 = 0
                r2 = 1
                if (r0 == 0) goto L_0x0022
                if (r0 == r2) goto L_0x000c
                goto L_0x007c
            L_0x000c:
                android.view.View r9 = r9.itemView
                org.telegram.ui.Cells.StickerSetNameCell r9 = (org.telegram.ui.Cells.StickerSetNameCell) r9
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                java.lang.String[] r0 = r0.emojiTitles
                android.util.SparseIntArray r2 = r8.positionToSection
                int r10 = r2.get(r10)
                r10 = r0[r10]
                r9.setText(r10, r1)
                goto L_0x007c
            L_0x0022:
                android.view.View r9 = r9.itemView
                org.telegram.ui.Components.EmojiView$ImageViewEmoji r9 = (org.telegram.ui.Components.EmojiView.ImageViewEmoji) r9
                org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                boolean r0 = r0.needEmojiSearch
                if (r0 == 0) goto L_0x0030
                int r10 = r10 + -1
            L_0x0030:
                java.util.ArrayList<java.lang.String> r0 = org.telegram.messenger.Emoji.recentEmoji
                int r0 = r0.size()
                if (r10 >= r0) goto L_0x0043
                java.util.ArrayList<java.lang.String> r0 = org.telegram.messenger.Emoji.recentEmoji
                java.lang.Object r10 = r0.get(r10)
                java.lang.String r10 = (java.lang.String) r10
                r0 = r10
                r1 = 1
                goto L_0x006f
            L_0x0043:
                r3 = 0
            L_0x0044:
                java.lang.String[][] r4 = org.telegram.messenger.EmojiData.dataColored
                int r5 = r4.length
                r6 = 0
                if (r3 >= r5) goto L_0x006a
                r5 = r4[r3]
                int r5 = r5.length
                int r5 = r5 + r2
                int r5 = r5 + r0
                if (r10 >= r5) goto L_0x0066
                r3 = r4[r3]
                int r10 = r10 - r0
                int r10 = r10 - r2
                r10 = r3[r10]
                java.util.HashMap<java.lang.String, java.lang.String> r0 = org.telegram.messenger.Emoji.emojiColor
                java.lang.Object r0 = r0.get(r10)
                java.lang.String r0 = (java.lang.String) r0
                if (r0 == 0) goto L_0x006b
                java.lang.String r0 = org.telegram.ui.Components.EmojiView.addColorToCode(r10, r0)
                goto L_0x006c
            L_0x0066:
                int r3 = r3 + 1
                r0 = r5
                goto L_0x0044
            L_0x006a:
                r10 = r6
            L_0x006b:
                r0 = r10
            L_0x006c:
                r7 = r0
                r0 = r10
                r10 = r7
            L_0x006f:
                android.graphics.drawable.Drawable r2 = org.telegram.messenger.Emoji.getEmojiBigDrawable(r10)
                r9.setImageDrawable(r2, r1)
                r9.setTag(r0)
                r9.setContentDescription(r10)
            L_0x007c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiGridAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
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
                                    TLRPC$TL_messages_getEmojiURL tLRPC$TL_messages_getEmojiURL = new TLRPC$TL_messages_getEmojiURL();
                                    tLRPC$TL_messages_getEmojiURL.lang_code = EmojiSearchAdapter.this.lastSearchAlias != null ? EmojiSearchAdapter.this.lastSearchAlias : EmojiView.this.lastSearchKeyboardLanguage[0];
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
                                         org.telegram.ui.Components.EmojiView.access$5400(org.telegram.ui.Components.EmojiView):int type: STATIC)
                                         org.telegram.tgnet.ConnectionsManager.getInstance(int):org.telegram.tgnet.ConnectionsManager type: STATIC)
                                          (r1v3 'tLRPC$TL_messages_getEmojiURL' org.telegram.tgnet.TLRPC$TL_messages_getEmojiURL)
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
                                         org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable, long):void type: STATIC in method: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.1.onClick(android.view.View):void, dex: classes2.dex
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
                                         org.telegram.ui.Components.EmojiView.access$5400(org.telegram.ui.Components.EmojiView):int type: STATIC)
                                         org.telegram.tgnet.ConnectionsManager.getInstance(int):org.telegram.tgnet.ConnectionsManager type: STATIC)
                                          (r1v3 'tLRPC$TL_messages_getEmojiURL' org.telegram.tgnet.TLRPC$TL_messages_getEmojiURL)
                                          (wrap: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$vESTLZoOoKr-laMmjTYW6gGhW9U : 0x0057: CONSTRUCTOR  (r3v1 org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$vESTLZoOoKr-laMmjTYW6gGhW9U) = 
                                          (r4v0 'this' org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 A[THIS])
                                          (r5v2 'alertDialogArr' org.telegram.ui.ActionBar.AlertDialog[])
                                          (wrap: org.telegram.ui.ActionBar.BottomSheet$Builder : 0x0053: IGET  (r2v7 org.telegram.ui.ActionBar.BottomSheet$Builder) = 
                                          (r4v0 'this' org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 A[THIS])
                                         org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.1.val$builder org.telegram.ui.ActionBar.BottomSheet$Builder)
                                         call: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$vESTLZoOoKr-laMmjTYW6gGhW9U.<init>(org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1, org.telegram.ui.ActionBar.AlertDialog[], org.telegram.ui.ActionBar.BottomSheet$Builder):void type: CONSTRUCTOR)
                                         org.telegram.tgnet.ConnectionsManager.sendRequest(org.telegram.tgnet.TLObject, org.telegram.tgnet.RequestDelegate):int type: VIRTUAL)
                                         call: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$TwKjymEFVh1Ja7xLl-9KmhIF6eg.<init>(org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1, org.telegram.ui.ActionBar.AlertDialog[], int):void type: CONSTRUCTOR in method: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.1.onClick(android.view.View):void, dex: classes2.dex
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

                                public /* synthetic */ void lambda$onClick$1$EmojiView$EmojiSearchAdapter$2$1(AlertDialog[] alertDialogArr, BottomSheet.Builder builder, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                    AndroidUtilities.runOnUIThread(
                                    /*  JADX ERROR: Method code generation error
                                        jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0005: INVOKE  
                                          (wrap: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$k1LSrqBP6ptq3f6NASRqH9-nzpY : 0x0002: CONSTRUCTOR  (r4v1 org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$k1LSrqBP6ptq3f6NASRqH9-nzpY) = 
                                          (r0v0 'this' org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1 A[THIS])
                                          (r1v0 'alertDialogArr' org.telegram.ui.ActionBar.AlertDialog[])
                                          (r3v0 'tLObject' org.telegram.tgnet.TLObject)
                                          (r2v0 'builder' org.telegram.ui.ActionBar.BottomSheet$Builder)
                                         call: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$k1LSrqBP6ptq3f6NASRqH9-nzpY.<init>(org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1, org.telegram.ui.ActionBar.AlertDialog[], org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.BottomSheet$Builder):void type: CONSTRUCTOR)
                                         org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.1.lambda$onClick$1$EmojiView$EmojiSearchAdapter$2$1(org.telegram.ui.ActionBar.AlertDialog[], org.telegram.ui.ActionBar.BottomSheet$Builder, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void, dex: classes2.dex
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
                                         call: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$k1LSrqBP6ptq3f6NASRqH9-nzpY.<init>(org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1, org.telegram.ui.ActionBar.AlertDialog[], org.telegram.tgnet.TLObject, org.telegram.ui.ActionBar.BottomSheet$Builder):void type: CONSTRUCTOR in method: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.1.lambda$onClick$1$EmojiView$EmojiSearchAdapter$2$1(org.telegram.ui.ActionBar.AlertDialog[], org.telegram.ui.ActionBar.BottomSheet$Builder, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void, dex: classes2.dex
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
                                    if (tLObject instanceof TLRPC$TL_emojiURL) {
                                        Browser.openUrl(EmojiView.this.getContext(), ((TLRPC$TL_emojiURL) tLObject).url);
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
                                             org.telegram.ui.ActionBar.AlertDialog.setOnCancelListener(android.content.DialogInterface$OnCancelListener):void type: VIRTUAL in method: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.1.lambda$onClick$3$EmojiView$EmojiSearchAdapter$2$1(org.telegram.ui.ActionBar.AlertDialog[], int):void, dex: classes2.dex
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
                                             call: org.telegram.ui.Components.-$$Lambda$EmojiView$EmojiSearchAdapter$2$1$BvShOFTdaqiZ1q6_dGuWuV6cZ2o.<init>(org.telegram.ui.Components.EmojiView$EmojiSearchAdapter$2$1, int):void type: CONSTRUCTOR in method: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.2.1.lambda$onClick$3$EmojiView$EmojiSearchAdapter$2$1(org.telegram.ui.ActionBar.AlertDialog[], int):void, dex: classes2.dex
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
                                final String access$10700 = EmojiSearchAdapter.this.lastSearchEmojiString;
                                String[] currentKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                                if (!Arrays.equals(EmojiView.this.lastSearchKeyboardLanguage, currentKeyboardLanguage)) {
                                    MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(currentKeyboardLanguage);
                                }
                                String[] unused = EmojiView.this.lastSearchKeyboardLanguage = currentKeyboardLanguage;
                                MediaDataController.getInstance(EmojiView.this.currentAccount).getEmojiSuggestions(EmojiView.this.lastSearchKeyboardLanguage, EmojiSearchAdapter.this.lastSearchEmojiString, false, new MediaDataController.KeywordResultCallback() {
                                    public void run(ArrayList<MediaDataController.KeywordResult> arrayList, String str) {
                                        if (access$10700.equals(EmojiSearchAdapter.this.lastSearchEmojiString)) {
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
            }

            private class GifAdapter extends RecyclerListView.SelectionAdapter {
                /* access modifiers changed from: private */
                public TLRPC$User bot;
                private final Context context;
                private int firstResultItem;
                private int itemsCount;
                /* access modifiers changed from: private */
                public String lastSearchImageString;
                /* access modifiers changed from: private */
                public boolean lastSearchIsEmoji;
                private final int maxRecentRowsCount;
                /* access modifiers changed from: private */
                public String nextSearchOffset;
                /* access modifiers changed from: private */
                public final GifProgressEmptyView progressEmptyView;
                /* access modifiers changed from: private */
                public int recentItemsCount;
                /* access modifiers changed from: private */
                public int reqId;
                /* access modifiers changed from: private */
                public ArrayList<TLRPC$BotInlineResult> results;
                private HashMap<String, TLRPC$BotInlineResult> resultsMap;
                /* access modifiers changed from: private */
                public boolean searchEndReached;
                private Runnable searchRunnable;
                private boolean searchingUser;
                /* access modifiers changed from: private */
                public boolean showTrendingWhenSearchEmpty;
                /* access modifiers changed from: private */
                public int trendingSectionItem;
                private final boolean withRecent;

                public GifAdapter(EmojiView emojiView, Context context2) {
                    this(context2, false, 0);
                }

                /* JADX INFO: this call moved to the top of the method (can break code semantics) */
                public GifAdapter(EmojiView emojiView, Context context2, boolean z) {
                    this(context2, z, z ? Integer.MAX_VALUE : 0);
                }

                public GifAdapter(Context context2, boolean z, int i) {
                    GifProgressEmptyView gifProgressEmptyView;
                    this.results = new ArrayList<>();
                    this.resultsMap = new HashMap<>();
                    this.trendingSectionItem = -1;
                    this.firstResultItem = -1;
                    this.context = context2;
                    this.withRecent = z;
                    this.maxRecentRowsCount = i;
                    if (z) {
                        gifProgressEmptyView = null;
                    } else {
                        gifProgressEmptyView = new GifProgressEmptyView(context2);
                    }
                    this.progressEmptyView = gifProgressEmptyView;
                }

                public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                    return viewHolder.getItemViewType() == 0;
                }

                public int getItemCount() {
                    return this.itemsCount;
                }

                public int getItemViewType(int i) {
                    if (i == 0) {
                        return 1;
                    }
                    if (!this.withRecent || i != this.trendingSectionItem) {
                        return (this.withRecent || !this.results.isEmpty()) ? 0 : 3;
                    }
                    return 2;
                }

                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    GifProgressEmptyView gifProgressEmptyView;
                    if (i == 0) {
                        ContextLinkCell contextLinkCell = new ContextLinkCell(this.context);
                        contextLinkCell.setCanPreviewGif(true);
                        gifProgressEmptyView = contextLinkCell;
                    } else if (i == 1) {
                        View view = new View(EmojiView.this.getContext());
                        view.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                        gifProgressEmptyView = view;
                    } else if (i != 2) {
                        GifProgressEmptyView gifProgressEmptyView2 = this.progressEmptyView;
                        gifProgressEmptyView2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                        gifProgressEmptyView = gifProgressEmptyView2;
                    } else {
                        StickerSetNameCell stickerSetNameCell = new StickerSetNameCell(this.context, false);
                        stickerSetNameCell.setText(LocaleController.getString("FeaturedGifs", NUM), 0);
                        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-1, -2);
                        layoutParams.topMargin = AndroidUtilities.dp(2.5f);
                        layoutParams.bottomMargin = AndroidUtilities.dp(5.5f);
                        stickerSetNameCell.setLayoutParams(layoutParams);
                        gifProgressEmptyView = stickerSetNameCell;
                    }
                    return new RecyclerListView.Holder(gifProgressEmptyView);
                }

                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                    if (viewHolder.getItemViewType() == 0) {
                        ContextLinkCell contextLinkCell = (ContextLinkCell) viewHolder.itemView;
                        int i2 = this.firstResultItem;
                        if (i2 < 0 || i < i2) {
                            contextLinkCell.setGif((TLRPC$Document) EmojiView.this.recentGifs.get(i - 1), false);
                        } else {
                            contextLinkCell.setLink(this.results.get(i - i2), this.bot, true, false, false, true);
                        }
                    }
                }

                public void notifyDataSetChanged() {
                    updateRecentItemsCount();
                    updateItems();
                    super.notifyDataSetChanged();
                }

                private void updateItems() {
                    this.trendingSectionItem = -1;
                    this.firstResultItem = -1;
                    this.itemsCount = 1;
                    if (this.withRecent) {
                        this.itemsCount = this.recentItemsCount + 1;
                    }
                    if (!this.results.isEmpty()) {
                        if (this.withRecent && this.recentItemsCount > 0) {
                            int i = this.itemsCount;
                            this.itemsCount = i + 1;
                            this.trendingSectionItem = i;
                        }
                        int i2 = this.itemsCount;
                        this.firstResultItem = i2;
                        this.itemsCount = i2 + this.results.size();
                    } else if (!this.withRecent) {
                        this.itemsCount++;
                    }
                }

                private void updateRecentItemsCount() {
                    int i;
                    if (this.withRecent && (i = this.maxRecentRowsCount) != 0) {
                        if (i == Integer.MAX_VALUE) {
                            this.recentItemsCount = EmojiView.this.recentGifs.size();
                        } else if (EmojiView.this.gifGridView.getMeasuredWidth() != 0) {
                            int measuredWidth = EmojiView.this.gifGridView.getMeasuredWidth();
                            int spanCount = EmojiView.this.gifLayoutManager.getSpanCount();
                            int dp = AndroidUtilities.dp(100.0f);
                            this.recentItemsCount = 0;
                            int size = EmojiView.this.recentGifs.size();
                            int i2 = spanCount;
                            int i3 = 0;
                            int i4 = 0;
                            for (int i5 = 0; i5 < size; i5++) {
                                Size fixSize = EmojiView.this.gifLayoutManager.fixSize(EmojiView.this.gifLayoutManager.getSizeForItem((TLRPC$Document) EmojiView.this.recentGifs.get(i5)));
                                int min = Math.min(spanCount, (int) Math.floor((double) (((float) spanCount) * (((fixSize.width / fixSize.height) * ((float) dp)) / ((float) measuredWidth)))));
                                if (i2 < min) {
                                    this.recentItemsCount += i3;
                                    i4++;
                                    if (i4 == this.maxRecentRowsCount) {
                                        break;
                                    }
                                    i2 = spanCount;
                                    i3 = 0;
                                }
                                i3++;
                                i2 -= min;
                            }
                            if (i4 < this.maxRecentRowsCount) {
                                this.recentItemsCount += i3;
                            }
                        }
                    }
                }

                public void loadTrendingGifs() {
                    search("", "", true, true, true);
                }

                private void searchBotUser() {
                    if (!this.searchingUser) {
                        this.searchingUser = true;
                        TLRPC$TL_contacts_resolveUsername tLRPC$TL_contacts_resolveUsername = new TLRPC$TL_contacts_resolveUsername();
                        tLRPC$TL_contacts_resolveUsername.username = MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot;
                        ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tLRPC$TL_contacts_resolveUsername, new RequestDelegate() {
                            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                EmojiView.GifAdapter.this.lambda$searchBotUser$1$EmojiView$GifAdapter(tLObject, tLRPC$TL_error);
                            }
                        });
                    }
                }

                public /* synthetic */ void lambda$searchBotUser$1$EmojiView$GifAdapter(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    if (tLObject != null) {
                        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
                            public final /* synthetic */ TLObject f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                EmojiView.GifAdapter.this.lambda$null$0$EmojiView$GifAdapter(this.f$1);
                            }
                        });
                    }
                }

                public /* synthetic */ void lambda$null$0$EmojiView$GifAdapter(TLObject tLObject) {
                    TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer = (TLRPC$TL_contacts_resolvedPeer) tLObject;
                    MessagesController.getInstance(EmojiView.this.currentAccount).putUsers(tLRPC$TL_contacts_resolvedPeer.users, false);
                    MessagesController.getInstance(EmojiView.this.currentAccount).putChats(tLRPC$TL_contacts_resolvedPeer.chats, false);
                    MessagesStorage.getInstance(EmojiView.this.currentAccount).putUsersAndChats(tLRPC$TL_contacts_resolvedPeer.users, tLRPC$TL_contacts_resolvedPeer.chats, true, true);
                    String str = this.lastSearchImageString;
                    this.lastSearchImageString = null;
                    search(str, "", false);
                }

                public void search(final String str) {
                    if (!this.withRecent) {
                        int i = this.reqId;
                        if (i != 0) {
                            if (i >= 0) {
                                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                            }
                            this.reqId = 0;
                        }
                        this.lastSearchIsEmoji = false;
                        GifProgressEmptyView gifProgressEmptyView = this.progressEmptyView;
                        if (gifProgressEmptyView != null) {
                            gifProgressEmptyView.setLoadingState(false);
                        }
                        Runnable runnable = this.searchRunnable;
                        if (runnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(runnable);
                        }
                        if (TextUtils.isEmpty(str)) {
                            this.lastSearchImageString = null;
                            if (this.showTrendingWhenSearchEmpty) {
                                loadTrendingGifs();
                                return;
                            }
                            int currentPosition = EmojiView.this.gifTabs.getCurrentPosition();
                            if (currentPosition != EmojiView.this.gifRecentTabNum && currentPosition != EmojiView.this.gifTrendingTabNum) {
                                searchEmoji(MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchEmojies.get(currentPosition - EmojiView.this.gifFirstEmojiTabNum));
                            } else if (EmojiView.this.gifGridView.getAdapter() != EmojiView.this.gifAdapter) {
                                EmojiView.this.gifGridView.setAdapter(EmojiView.this.gifAdapter);
                            }
                        } else {
                            String lowerCase = str.toLowerCase();
                            this.lastSearchImageString = lowerCase;
                            if (!TextUtils.isEmpty(lowerCase)) {
                                AnonymousClass1 r0 = new Runnable() {
                                    public void run() {
                                        GifAdapter.this.search(str, "", true);
                                    }
                                };
                                this.searchRunnable = r0;
                                AndroidUtilities.runOnUIThread(r0, 300);
                            }
                        }
                    }
                }

                public void searchEmoji(String str) {
                    if (!this.lastSearchIsEmoji || !TextUtils.equals(this.lastSearchImageString, str)) {
                        search(str, "", true, true, true);
                    } else {
                        EmojiView.this.gifLayoutManager.scrollToPositionWithOffset(1, 0);
                    }
                }

                /* access modifiers changed from: protected */
                public void search(String str, String str2, boolean z) {
                    search(str, str2, z, false, false);
                }

                /* access modifiers changed from: protected */
                public void search(String str, String str2, boolean z, boolean z2, boolean z3) {
                    int i = this.reqId;
                    if (i != 0) {
                        if (i >= 0) {
                            ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                        }
                        this.reqId = 0;
                    }
                    this.lastSearchImageString = str;
                    this.lastSearchIsEmoji = z2;
                    GifProgressEmptyView gifProgressEmptyView = this.progressEmptyView;
                    if (gifProgressEmptyView != null) {
                        gifProgressEmptyView.setLoadingState(z2);
                    }
                    TLObject userOrChat = MessagesController.getInstance(EmojiView.this.currentAccount).getUserOrChat(MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot);
                    if (userOrChat instanceof TLRPC$User) {
                        if (!this.withRecent && TextUtils.isEmpty(str2)) {
                            EmojiView.this.gifSearchField.progressDrawable.startAnimation();
                        }
                        this.bot = (TLRPC$User) userOrChat;
                        String str3 = "gif_search_" + str + "_" + str2;
                        $$Lambda$EmojiView$GifAdapter$13vViOQytbq7KFh0nLcvkKdkwxU r2 = new RequestDelegate(str, str2, z, z2, z3, str3) {
                            public final /* synthetic */ String f$1;
                            public final /* synthetic */ String f$2;
                            public final /* synthetic */ boolean f$3;
                            public final /* synthetic */ boolean f$4;
                            public final /* synthetic */ boolean f$5;
                            public final /* synthetic */ String f$6;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                                this.f$4 = r5;
                                this.f$5 = r6;
                                this.f$6 = r7;
                            }

                            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                EmojiView.GifAdapter.this.lambda$search$3$EmojiView$GifAdapter(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tLRPC$TL_error);
                            }
                        };
                        if (!z3 && !this.withRecent && z2 && TextUtils.isEmpty(str2)) {
                            this.results.clear();
                            this.resultsMap.clear();
                            if (EmojiView.this.gifGridView.getAdapter() != this) {
                                EmojiView.this.gifGridView.setAdapter(this);
                            }
                            notifyDataSetChanged();
                            EmojiView.this.scrollGifsToTop();
                        }
                        if (z3 && EmojiView.this.gifCache.containsKey(str3)) {
                            lambda$null$2$EmojiView$GifAdapter(str, str2, z, z2, true, str3, (TLObject) EmojiView.this.gifCache.get(str3));
                        } else if (!EmojiView.this.gifSearchPreloader.isLoading(str3)) {
                            if (z3) {
                                this.reqId = -1;
                                MessagesStorage.getInstance(EmojiView.this.currentAccount).getBotCache(str3, r2);
                                return;
                            }
                            TLRPC$TL_messages_getInlineBotResults tLRPC$TL_messages_getInlineBotResults = new TLRPC$TL_messages_getInlineBotResults();
                            if (str == null) {
                                str = "";
                            }
                            tLRPC$TL_messages_getInlineBotResults.query = str;
                            tLRPC$TL_messages_getInlineBotResults.bot = MessagesController.getInstance(EmojiView.this.currentAccount).getInputUser(this.bot);
                            tLRPC$TL_messages_getInlineBotResults.offset = str2;
                            tLRPC$TL_messages_getInlineBotResults.peer = new TLRPC$TL_inputPeerEmpty();
                            this.reqId = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tLRPC$TL_messages_getInlineBotResults, r2, 2);
                        }
                    } else if (z) {
                        searchBotUser();
                        if (!this.withRecent) {
                            EmojiView.this.gifSearchField.progressDrawable.startAnimation();
                        }
                    }
                }

                public /* synthetic */ void lambda$search$3$EmojiView$GifAdapter(String str, String str2, boolean z, boolean z2, boolean z3, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(str, str2, z, z2, z3, str3, tLObject) {
                        public final /* synthetic */ String f$1;
                        public final /* synthetic */ String f$2;
                        public final /* synthetic */ boolean f$3;
                        public final /* synthetic */ boolean f$4;
                        public final /* synthetic */ boolean f$5;
                        public final /* synthetic */ String f$6;
                        public final /* synthetic */ TLObject f$7;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                            this.f$7 = r8;
                        }

                        public final void run() {
                            EmojiView.GifAdapter.this.lambda$null$2$EmojiView$GifAdapter(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                        }
                    });
                }

                /* access modifiers changed from: private */
                /* renamed from: processResponse */
                public void lambda$null$2$EmojiView$GifAdapter(String str, String str2, boolean z, boolean z2, boolean z3, String str3, TLObject tLObject) {
                    if (str != null && str.equals(this.lastSearchImageString)) {
                        boolean z4 = false;
                        this.reqId = 0;
                        if (!z3 || ((tLObject instanceof TLRPC$messages_BotResults) && !((TLRPC$messages_BotResults) tLObject).results.isEmpty())) {
                            if (!this.withRecent && TextUtils.isEmpty(str2)) {
                                this.results.clear();
                                this.resultsMap.clear();
                                EmojiView.this.gifSearchField.progressDrawable.stopAnimation();
                            }
                            if (tLObject instanceof TLRPC$messages_BotResults) {
                                int size = this.results.size();
                                TLRPC$messages_BotResults tLRPC$messages_BotResults = (TLRPC$messages_BotResults) tLObject;
                                if (!EmojiView.this.gifCache.containsKey(str3)) {
                                    EmojiView.this.gifCache.put(str3, tLRPC$messages_BotResults);
                                }
                                if (!z3 && tLRPC$messages_BotResults.cache_time != 0) {
                                    MessagesStorage.getInstance(EmojiView.this.currentAccount).saveBotCache(str3, tLRPC$messages_BotResults);
                                }
                                this.nextSearchOffset = tLRPC$messages_BotResults.next_offset;
                                int i = 0;
                                for (int i2 = 0; i2 < tLRPC$messages_BotResults.results.size(); i2++) {
                                    TLRPC$BotInlineResult tLRPC$BotInlineResult = tLRPC$messages_BotResults.results.get(i2);
                                    if (!this.resultsMap.containsKey(tLRPC$BotInlineResult.id)) {
                                        tLRPC$BotInlineResult.query_id = tLRPC$messages_BotResults.query_id;
                                        this.results.add(tLRPC$BotInlineResult);
                                        this.resultsMap.put(tLRPC$BotInlineResult.id, tLRPC$BotInlineResult);
                                        i++;
                                    }
                                }
                                if (size == this.results.size() || TextUtils.isEmpty(this.nextSearchOffset)) {
                                    z4 = true;
                                }
                                this.searchEndReached = z4;
                                if (i != 0) {
                                    if (!z2 || size != 0) {
                                        updateItems();
                                        if (!this.withRecent) {
                                            if (size != 0) {
                                                notifyItemChanged(size);
                                            }
                                            notifyItemRangeInserted(size + 1, i);
                                        } else if (size != 0) {
                                            notifyItemChanged(this.recentItemsCount + 1 + size);
                                            notifyItemRangeInserted(this.recentItemsCount + 1 + size + 1, i);
                                        } else {
                                            notifyItemRangeInserted(this.recentItemsCount + 1, i + 1);
                                        }
                                    } else {
                                        notifyDataSetChanged();
                                    }
                                } else if (this.results.isEmpty()) {
                                    notifyDataSetChanged();
                                }
                            } else {
                                notifyDataSetChanged();
                            }
                            if (!this.withRecent) {
                                if (EmojiView.this.gifGridView.getAdapter() != this) {
                                    EmojiView.this.gifGridView.setAdapter(this);
                                }
                                if (z2 && !TextUtils.isEmpty(str) && TextUtils.isEmpty(str2)) {
                                    EmojiView.this.scrollGifsToTop();
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                        search(str, str2, z, z2, false);
                    }
                }
            }

            private class GifSearchPreloader {
                private final List<String> loadingKeys;

                private GifSearchPreloader() {
                    this.loadingKeys = new ArrayList();
                }

                public boolean isLoading(String str) {
                    return this.loadingKeys.contains(str);
                }

                public void preload(String str) {
                    preload(str, "", true);
                }

                private void preload(String str, String str2, boolean z) {
                    String str3 = "gif_search_" + str + "_" + str2;
                    if (!z || !EmojiView.this.gifCache.containsKey(str3)) {
                        $$Lambda$EmojiView$GifSearchPreloader$_2YoU_ruuFj6HTgN5xDb48DENiU r2 = new RequestDelegate(str, str2, z, str3) {
                            public final /* synthetic */ String f$1;
                            public final /* synthetic */ String f$2;
                            public final /* synthetic */ boolean f$3;
                            public final /* synthetic */ String f$4;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                                this.f$4 = r5;
                            }

                            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                EmojiView.GifSearchPreloader.this.lambda$preload$1$EmojiView$GifSearchPreloader(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
                            }
                        };
                        if (z) {
                            this.loadingKeys.add(str3);
                            MessagesStorage.getInstance(EmojiView.this.currentAccount).getBotCache(str3, r2);
                            return;
                        }
                        MessagesController instance = MessagesController.getInstance(EmojiView.this.currentAccount);
                        TLObject userOrChat = instance.getUserOrChat(instance.gifSearchBot);
                        if (userOrChat instanceof TLRPC$User) {
                            this.loadingKeys.add(str3);
                            TLRPC$TL_messages_getInlineBotResults tLRPC$TL_messages_getInlineBotResults = new TLRPC$TL_messages_getInlineBotResults();
                            if (str == null) {
                                str = "";
                            }
                            tLRPC$TL_messages_getInlineBotResults.query = str;
                            tLRPC$TL_messages_getInlineBotResults.bot = instance.getInputUser((TLRPC$User) userOrChat);
                            tLRPC$TL_messages_getInlineBotResults.offset = str2;
                            tLRPC$TL_messages_getInlineBotResults.peer = new TLRPC$TL_inputPeerEmpty();
                            ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tLRPC$TL_messages_getInlineBotResults, r2, 2);
                        }
                    }
                }

                public /* synthetic */ void lambda$preload$1$EmojiView$GifSearchPreloader(String str, String str2, boolean z, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(str, str2, z, str3, tLObject) {
                        public final /* synthetic */ String f$1;
                        public final /* synthetic */ String f$2;
                        public final /* synthetic */ boolean f$3;
                        public final /* synthetic */ String f$4;
                        public final /* synthetic */ TLObject f$5;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                        }

                        public final void run() {
                            EmojiView.GifSearchPreloader.this.lambda$null$0$EmojiView$GifSearchPreloader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                        }
                    });
                }

                /* access modifiers changed from: private */
                /* renamed from: processResponse */
                public void lambda$null$0$EmojiView$GifSearchPreloader(String str, String str2, boolean z, String str3, TLObject tLObject) {
                    this.loadingKeys.remove(str3);
                    if (EmojiView.this.gifSearchAdapter.lastSearchIsEmoji && EmojiView.this.gifSearchAdapter.lastSearchImageString.equals(str)) {
                        EmojiView.this.gifSearchAdapter.lambda$null$2$EmojiView$GifAdapter(str, str2, false, true, z, str3, tLObject);
                    } else if (z && (!(tLObject instanceof TLRPC$messages_BotResults) || ((TLRPC$messages_BotResults) tLObject).results.isEmpty())) {
                        preload(str, str2, false);
                    } else if ((tLObject instanceof TLRPC$messages_BotResults) && !EmojiView.this.gifCache.containsKey(str3)) {
                        EmojiView.this.gifCache.put(str3, (TLRPC$messages_BotResults) tLObject);
                    }
                }
            }

            private class GifLayoutManager extends ExtendedGridLayoutManager {
                private Size size = new Size();

                public GifLayoutManager(Context context) {
                    super(context, 100, true);
                    setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(EmojiView.this) {
                        public int getSpanSize(int i) {
                            if (i == 0 || (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter && EmojiView.this.gifSearchAdapter.results.isEmpty())) {
                                return GifLayoutManager.this.getSpanCount();
                            }
                            return GifLayoutManager.this.getSpanSizeForItem(i - 1);
                        }
                    });
                }

                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v12, resolved type: java.lang.Object} */
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: org.telegram.tgnet.TLRPC$Document} */
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v25, resolved type: java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>} */
                /* JADX WARNING: type inference failed for: r4v1, types: [java.util.List] */
                /* JADX WARNING: type inference failed for: r4v3 */
                /* JADX WARNING: type inference failed for: r4v5 */
                /* JADX WARNING: type inference failed for: r4v9, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
                /* JADX WARNING: type inference failed for: r4v10, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
                /* JADX WARNING: type inference failed for: r4v11, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
                /* JADX WARNING: type inference failed for: r4v19, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
                /* JADX WARNING: type inference failed for: r4v20, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
                /* JADX WARNING: type inference failed for: r4v21, types: [java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute>] */
                /* access modifiers changed from: protected */
                /* JADX WARNING: Multi-variable type inference failed */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public org.telegram.ui.Components.Size getSizeForItem(int r4) {
                    /*
                        r3 = this;
                        org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                        org.telegram.ui.Components.RecyclerListView r0 = r0.gifGridView
                        androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
                        org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                        org.telegram.ui.Components.EmojiView$GifAdapter r1 = r1.gifAdapter
                        r2 = 0
                        if (r0 != r1) goto L_0x006e
                        org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                        org.telegram.ui.Components.EmojiView$GifAdapter r0 = r0.gifAdapter
                        int r0 = r0.recentItemsCount
                        if (r4 <= r0) goto L_0x0051
                        org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                        org.telegram.ui.Components.EmojiView$GifAdapter r0 = r0.gifAdapter
                        java.util.ArrayList r0 = r0.results
                        org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                        org.telegram.ui.Components.EmojiView$GifAdapter r1 = r1.gifAdapter
                        int r1 = r1.recentItemsCount
                        int r4 = r4 - r1
                        int r4 = r4 + -1
                        java.lang.Object r4 = r0.get(r4)
                        org.telegram.tgnet.TLRPC$BotInlineResult r4 = (org.telegram.tgnet.TLRPC$BotInlineResult) r4
                        org.telegram.tgnet.TLRPC$Document r0 = r4.document
                        if (r0 == 0) goto L_0x0043
                        java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r0.attributes
                        goto L_0x0094
                    L_0x0043:
                        org.telegram.tgnet.TLRPC$WebDocument r1 = r4.content
                        if (r1 == 0) goto L_0x004a
                        java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r1.attributes
                        goto L_0x0094
                    L_0x004a:
                        org.telegram.tgnet.TLRPC$WebDocument r4 = r4.thumb
                        if (r4 == 0) goto L_0x00a4
                        java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r4.attributes
                        goto L_0x0094
                    L_0x0051:
                        org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                        org.telegram.ui.Components.EmojiView$GifAdapter r0 = r0.gifAdapter
                        int r0 = r0.recentItemsCount
                        if (r4 != r0) goto L_0x005e
                        return r2
                    L_0x005e:
                        org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                        java.util.ArrayList r0 = r0.recentGifs
                        java.lang.Object r4 = r0.get(r4)
                        r2 = r4
                        org.telegram.tgnet.TLRPC$Document r2 = (org.telegram.tgnet.TLRPC$Document) r2
                        java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r2.attributes
                        goto L_0x00a8
                    L_0x006e:
                        org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                        org.telegram.ui.Components.EmojiView$GifAdapter r0 = r0.gifSearchAdapter
                        java.util.ArrayList r0 = r0.results
                        boolean r0 = r0.isEmpty()
                        if (r0 != 0) goto L_0x00a7
                        org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                        org.telegram.ui.Components.EmojiView$GifAdapter r0 = r0.gifSearchAdapter
                        java.util.ArrayList r0 = r0.results
                        java.lang.Object r4 = r0.get(r4)
                        org.telegram.tgnet.TLRPC$BotInlineResult r4 = (org.telegram.tgnet.TLRPC$BotInlineResult) r4
                        org.telegram.tgnet.TLRPC$Document r0 = r4.document
                        if (r0 == 0) goto L_0x0096
                        java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r0.attributes
                    L_0x0094:
                        r2 = r4
                        goto L_0x00a4
                    L_0x0096:
                        org.telegram.tgnet.TLRPC$WebDocument r1 = r4.content
                        if (r1 == 0) goto L_0x009d
                        java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r1.attributes
                        goto L_0x0094
                    L_0x009d:
                        org.telegram.tgnet.TLRPC$WebDocument r4 = r4.thumb
                        if (r4 == 0) goto L_0x00a4
                        java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r4.attributes
                        goto L_0x0094
                    L_0x00a4:
                        r4 = r2
                        r2 = r0
                        goto L_0x00a8
                    L_0x00a7:
                        r4 = r2
                    L_0x00a8:
                        org.telegram.ui.Components.Size r4 = r3.getSizeForItem(r2, r4)
                        return r4
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.GifLayoutManager.getSizeForItem(int):org.telegram.ui.Components.Size");
                }

                /* access modifiers changed from: protected */
                public int getFlowItemCount() {
                    if (EmojiView.this.gifGridView.getAdapter() != EmojiView.this.gifSearchAdapter || !EmojiView.this.gifSearchAdapter.results.isEmpty()) {
                        return getItemCount() - 1;
                    }
                    return 0;
                }

                public Size getSizeForItem(TLRPC$Document tLRPC$Document) {
                    return getSizeForItem(tLRPC$Document, tLRPC$Document.attributes);
                }

                public Size getSizeForItem(TLRPC$Document tLRPC$Document, List<TLRPC$DocumentAttribute> list) {
                    TLRPC$PhotoSize closestPhotoSizeWithSize;
                    int i;
                    int i2;
                    Size size2 = this.size;
                    size2.height = 100.0f;
                    size2.width = 100.0f;
                    if (!(tLRPC$Document == null || (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90)) == null || (i = closestPhotoSizeWithSize.w) == 0 || (i2 = closestPhotoSizeWithSize.h) == 0)) {
                        Size size3 = this.size;
                        size3.width = (float) i;
                        size3.height = (float) i2;
                    }
                    if (list != null) {
                        int i3 = 0;
                        while (true) {
                            if (i3 >= list.size()) {
                                break;
                            }
                            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = list.get(i3);
                            if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeImageSize) || (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo)) {
                                Size size4 = this.size;
                                size4.width = (float) tLRPC$DocumentAttribute.w;
                                size4.height = (float) tLRPC$DocumentAttribute.h;
                            } else {
                                i3++;
                            }
                        }
                    }
                    return this.size;
                }
            }

            private class GifProgressEmptyView extends FrameLayout {
                /* access modifiers changed from: private */
                public final ImageView imageView;
                private boolean loadingState;
                /* access modifiers changed from: private */
                public final RadialProgressView progressView;
                /* access modifiers changed from: private */
                public final TextView textView;

                public GifProgressEmptyView(Context context) {
                    super(context);
                    ImageView imageView2 = new ImageView(getContext());
                    this.imageView = imageView2;
                    imageView2.setScaleType(ImageView.ScaleType.CENTER);
                    this.imageView.setImageResource(NUM);
                    this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
                    addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 59.0f));
                    TextView textView2 = new TextView(getContext());
                    this.textView = textView2;
                    textView2.setText(LocaleController.getString("NoGIFsFound", NUM));
                    this.textView.setTextSize(1, 16.0f);
                    this.textView.setTextColor(Theme.getColor("chat_emojiPanelEmptyText"));
                    addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 9.0f));
                    RadialProgressView radialProgressView = new RadialProgressView(context);
                    this.progressView = radialProgressView;
                    radialProgressView.setVisibility(8);
                    this.progressView.setProgressColor(Theme.getColor("progressCircle"));
                    addView(this.progressView, LayoutHelper.createFrame(-2, -2, 17));
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    int i3;
                    int measuredHeight = EmojiView.this.gifGridView.getMeasuredHeight();
                    if (!this.loadingState) {
                        i3 = (int) (((float) (((measuredHeight - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3)) * 1.7f);
                    } else {
                        i3 = measuredHeight - AndroidUtilities.dp(92.0f);
                    }
                    super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(i3, NUM));
                }

                public void setLoadingState(boolean z) {
                    if (this.loadingState != z) {
                        this.loadingState = z;
                        int i = 8;
                        this.imageView.setVisibility(z ? 8 : 0);
                        this.textView.setVisibility(z ? 8 : 0);
                        RadialProgressView radialProgressView = this.progressView;
                        if (z) {
                            i = 0;
                        }
                        radialProgressView.setVisibility(i);
                    }
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
                public SparseArray<TLRPC$StickerSetCovered> positionsToSets = new SparseArray<>();
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

                    /* JADX WARNING: Code restructure failed: missing block: B:14:0x007b, code lost:
                        if (r5.charAt(r9) <= 57343) goto L_0x0097;
                     */
                    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0095, code lost:
                        if (r5.charAt(r9) != 9794) goto L_0x00b2;
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
                            int r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.access$14304(r0)
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
                            if (r5 > r6) goto L_0x0122
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r5 = r5.searchQuery
                            int r6 = r5.length()
                            r8 = 0
                        L_0x0059:
                            if (r8 >= r6) goto L_0x00d9
                            int r9 = r6 + -1
                            r10 = 2
                            if (r8 >= r9) goto L_0x00b2
                            char r9 = r5.charAt(r8)
                            r11 = 55356(0xd83c, float:7.757E-41)
                            if (r9 != r11) goto L_0x007d
                            int r9 = r8 + 1
                            char r11 = r5.charAt(r9)
                            r12 = 57339(0xdffb, float:8.0349E-41)
                            if (r11 < r12) goto L_0x007d
                            char r9 = r5.charAt(r9)
                            r11 = 57343(0xdfff, float:8.0355E-41)
                            if (r9 <= r11) goto L_0x0097
                        L_0x007d:
                            char r9 = r5.charAt(r8)
                            r11 = 8205(0x200d, float:1.1498E-41)
                            if (r9 != r11) goto L_0x00b2
                            int r9 = r8 + 1
                            char r11 = r5.charAt(r9)
                            r12 = 9792(0x2640, float:1.3722E-41)
                            if (r11 == r12) goto L_0x0097
                            char r9 = r5.charAt(r9)
                            r11 = 9794(0x2642, float:1.3724E-41)
                            if (r9 != r11) goto L_0x00b2
                        L_0x0097:
                            java.lang.CharSequence[] r9 = new java.lang.CharSequence[r10]
                            java.lang.CharSequence r10 = r5.subSequence(r1, r8)
                            r9[r1] = r10
                            int r10 = r8 + 2
                            int r11 = r5.length()
                            java.lang.CharSequence r5 = r5.subSequence(r10, r11)
                            r9[r7] = r5
                            java.lang.CharSequence r5 = android.text.TextUtils.concat(r9)
                            int r6 = r6 + -2
                            goto L_0x00d5
                        L_0x00b2:
                            char r9 = r5.charAt(r8)
                            r11 = 65039(0xfe0f, float:9.1139E-41)
                            if (r9 != r11) goto L_0x00d7
                            java.lang.CharSequence[] r9 = new java.lang.CharSequence[r10]
                            java.lang.CharSequence r10 = r5.subSequence(r1, r8)
                            r9[r1] = r10
                            int r10 = r8 + 1
                            int r11 = r5.length()
                            java.lang.CharSequence r5 = r5.subSequence(r10, r11)
                            r9[r7] = r5
                            java.lang.CharSequence r5 = android.text.TextUtils.concat(r9)
                            int r6 = r6 + -1
                        L_0x00d5:
                            int r8 = r8 + -1
                        L_0x00d7:
                            int r8 = r8 + r7
                            goto L_0x0059
                        L_0x00d9:
                            if (r4 == 0) goto L_0x00e6
                            java.lang.String r5 = r5.toString()
                            java.lang.Object r5 = r4.get(r5)
                            java.util.ArrayList r5 = (java.util.ArrayList) r5
                            goto L_0x00e7
                        L_0x00e6:
                            r5 = 0
                        L_0x00e7:
                            if (r5 == 0) goto L_0x0122
                            boolean r6 = r5.isEmpty()
                            if (r6 != 0) goto L_0x0122
                            r13.clear()
                            r2.addAll(r5)
                            int r6 = r5.size()
                            r8 = 0
                        L_0x00fa:
                            if (r8 >= r6) goto L_0x010a
                            java.lang.Object r9 = r5.get(r8)
                            org.telegram.tgnet.TLRPC$Document r9 = (org.telegram.tgnet.TLRPC$Document) r9
                            long r10 = r9.id
                            r3.put(r10, r9)
                            int r8 = r8 + 1
                            goto L_0x00fa
                        L_0x010a:
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.HashMap r5 = r5.emojiStickers
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r6 = r6.searchQuery
                            r5.put(r2, r6)
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.ArrayList r5 = r5.emojiArrays
                            r5.add(r2)
                        L_0x0122:
                            if (r4 == 0) goto L_0x0180
                            boolean r5 = r4.isEmpty()
                            if (r5 != 0) goto L_0x0180
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r5 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r5 = r5.searchQuery
                            int r5 = r5.length()
                            if (r5 <= r7) goto L_0x0180
                            java.lang.String[] r5 = org.telegram.messenger.AndroidUtilities.getCurrentKeyboardLanguage()
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                            java.lang.String[] r6 = r6.lastSearchKeyboardLanguage
                            boolean r6 = java.util.Arrays.equals(r6, r5)
                            if (r6 != 0) goto L_0x0157
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r6 = org.telegram.ui.Components.EmojiView.this
                            int r6 = r6.currentAccount
                            org.telegram.messenger.MediaDataController r6 = org.telegram.messenger.MediaDataController.getInstance(r6)
                            r6.fetchNewEmojiKeywords(r5)
                        L_0x0157:
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
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r7 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r7 = r7.searchQuery
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$1 r8 = new org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1$1
                            r8.<init>(r0, r4)
                            r5.getEmojiSuggestions(r6, r7, r1, r8)
                        L_0x0180:
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                            int r0 = r0.currentAccount
                            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
                            java.util.ArrayList r0 = r0.getStickerSets(r1)
                            int r4 = r0.size()
                            r5 = 0
                        L_0x0195:
                            r6 = 32
                            if (r5 >= r4) goto L_0x0211
                            java.lang.Object r7 = r0.get(r5)
                            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r7 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r7
                            org.telegram.tgnet.TLRPC$StickerSet r8 = r7.set
                            java.lang.String r8 = r8.title
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r9 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r9 = r9.searchQuery
                            int r8 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r8, r9)
                            if (r8 < 0) goto L_0x01d7
                            if (r8 == 0) goto L_0x01bd
                            org.telegram.tgnet.TLRPC$StickerSet r9 = r7.set
                            java.lang.String r9 = r9.title
                            int r10 = r8 + -1
                            char r9 = r9.charAt(r10)
                            if (r9 != r6) goto L_0x020e
                        L_0x01bd:
                            r13.clear()
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.ArrayList r6 = r6.localPacks
                            r6.add(r7)
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.HashMap r6 = r6.localPacksByName
                            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
                            r6.put(r7, r8)
                            goto L_0x020e
                        L_0x01d7:
                            org.telegram.tgnet.TLRPC$StickerSet r8 = r7.set
                            java.lang.String r8 = r8.short_name
                            if (r8 == 0) goto L_0x020e
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r9 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r9 = r9.searchQuery
                            int r8 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r8, r9)
                            if (r8 < 0) goto L_0x020e
                            if (r8 == 0) goto L_0x01f7
                            org.telegram.tgnet.TLRPC$StickerSet r9 = r7.set
                            java.lang.String r9 = r9.short_name
                            int r8 = r8 + -1
                            char r8 = r9.charAt(r8)
                            if (r8 != r6) goto L_0x020e
                        L_0x01f7:
                            r13.clear()
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.ArrayList r6 = r6.localPacks
                            r6.add(r7)
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r6 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.HashMap r6 = r6.localPacksByShortName
                            java.lang.Boolean r8 = java.lang.Boolean.TRUE
                            r6.put(r7, r8)
                        L_0x020e:
                            int r5 = r5 + 1
                            goto L_0x0195
                        L_0x0211:
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                            int r0 = r0.currentAccount
                            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
                            r4 = 3
                            java.util.ArrayList r0 = r0.getStickerSets(r4)
                            int r4 = r0.size()
                            r5 = 0
                        L_0x0227:
                            if (r5 >= r4) goto L_0x02a1
                            java.lang.Object r7 = r0.get(r5)
                            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r7 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r7
                            org.telegram.tgnet.TLRPC$StickerSet r8 = r7.set
                            java.lang.String r8 = r8.title
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r9 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r9 = r9.searchQuery
                            int r8 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r8, r9)
                            if (r8 < 0) goto L_0x0267
                            if (r8 == 0) goto L_0x024d
                            org.telegram.tgnet.TLRPC$StickerSet r9 = r7.set
                            java.lang.String r9 = r9.title
                            int r10 = r8 + -1
                            char r9 = r9.charAt(r10)
                            if (r9 != r6) goto L_0x029e
                        L_0x024d:
                            r13.clear()
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r9 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.ArrayList r9 = r9.localPacks
                            r9.add(r7)
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r9 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.HashMap r9 = r9.localPacksByName
                            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
                            r9.put(r7, r8)
                            goto L_0x029e
                        L_0x0267:
                            org.telegram.tgnet.TLRPC$StickerSet r8 = r7.set
                            java.lang.String r8 = r8.short_name
                            if (r8 == 0) goto L_0x029e
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r9 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.lang.String r9 = r9.searchQuery
                            int r8 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r8, r9)
                            if (r8 < 0) goto L_0x029e
                            if (r8 == 0) goto L_0x0287
                            org.telegram.tgnet.TLRPC$StickerSet r9 = r7.set
                            java.lang.String r9 = r9.short_name
                            int r8 = r8 + -1
                            char r8 = r9.charAt(r8)
                            if (r8 != r6) goto L_0x029e
                        L_0x0287:
                            r13.clear()
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r8 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.ArrayList r8 = r8.localPacks
                            r8.add(r7)
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r8 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.HashMap r8 = r8.localPacksByShortName
                            java.lang.Boolean r9 = java.lang.Boolean.TRUE
                            r8.put(r7, r9)
                        L_0x029e:
                            int r5 = r5 + 1
                            goto L_0x0227
                        L_0x02a1:
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.ArrayList r0 = r0.localPacks
                            boolean r0 = r0.isEmpty()
                            if (r0 == 0) goto L_0x02b9
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            java.util.HashMap r0 = r0.emojiStickers
                            boolean r0 = r0.isEmpty()
                            if (r0 != 0) goto L_0x02e2
                        L_0x02b9:
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.RecyclerListView r0 = r0.stickersGridView
                            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r4 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r4 = r4.stickersSearchGridAdapter
                            if (r0 == r4) goto L_0x02e2
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r0 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.RecyclerListView r0 = r0.stickersGridView
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r4 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            org.telegram.ui.Components.EmojiView r4 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r4 = r4.stickersSearchGridAdapter
                            r0.setAdapter(r4)
                        L_0x02e2:
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
                            if (r0 == 0) goto L_0x033a
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
                        L_0x033a:
                            org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter r0 = org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.this
                            r0.notifyDataSetChanged()
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.AnonymousClass1.run():void");
                    }

                    public /* synthetic */ void lambda$run$1$EmojiView$StickersSearchGridAdapter$1(TLRPC$TL_messages_searchStickerSets tLRPC$TL_messages_searchStickerSets, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        if (tLObject instanceof TLRPC$TL_messages_foundStickerSets) {
                            AndroidUtilities.runOnUIThread(
                            /*  JADX ERROR: Method code generation error
                                jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0009: INVOKE  
                                  (wrap: org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$jguHwpSdjaICAoSBsd6Lf5EhaUQ : 0x0006: CONSTRUCTOR  (r3v2 org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$jguHwpSdjaICAoSBsd6Lf5EhaUQ) = 
                                  (r0v0 'this' org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1 A[THIS])
                                  (r1v0 'tLRPC$TL_messages_searchStickerSets' org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets)
                                  (r2v0 'tLObject' org.telegram.tgnet.TLObject)
                                 call: org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$jguHwpSdjaICAoSBsd6Lf5EhaUQ.<init>(org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1, org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets, org.telegram.tgnet.TLObject):void type: CONSTRUCTOR)
                                 org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.1.lambda$run$1$EmojiView$StickersSearchGridAdapter$1(org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void, dex: classes2.dex
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
                                  (r1v0 'tLRPC$TL_messages_searchStickerSets' org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets)
                                  (r2v0 'tLObject' org.telegram.tgnet.TLObject)
                                 call: org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$jguHwpSdjaICAoSBsd6Lf5EhaUQ.<init>(org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1, org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets, org.telegram.tgnet.TLObject):void type: CONSTRUCTOR in method: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.1.lambda$run$1$EmojiView$StickersSearchGridAdapter$1(org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void, dex: classes2.dex
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
                                boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messages_foundStickerSets
                                if (r3 == 0) goto L_0x000c
                                org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$jguHwpSdjaICAoSBsd6Lf5EhaUQ r3 = new org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$jguHwpSdjaICAoSBsd6Lf5EhaUQ
                                r3.<init>(r0, r1, r2)
                                org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)
                            L_0x000c:
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.AnonymousClass1.lambda$run$1$EmojiView$StickersSearchGridAdapter$1(org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
                        }

                        public /* synthetic */ void lambda$null$0$EmojiView$StickersSearchGridAdapter$1(TLRPC$TL_messages_searchStickerSets tLRPC$TL_messages_searchStickerSets, TLObject tLObject) {
                            if (tLRPC$TL_messages_searchStickerSets.q.equals(StickersSearchGridAdapter.this.searchQuery)) {
                                clear();
                                EmojiView.this.stickersSearchField.progressDrawable.stopAnimation();
                                int unused = StickersSearchGridAdapter.this.reqId = 0;
                                if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersSearchGridAdapter) {
                                    EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersSearchGridAdapter);
                                }
                                StickersSearchGridAdapter.this.serverPacks.addAll(((TLRPC$TL_messages_foundStickerSets) tLObject).sets);
                                StickersSearchGridAdapter.this.notifyDataSetChanged();
                            }
                        }

                        public /* synthetic */ void lambda$run$3$EmojiView$StickersSearchGridAdapter$1(TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers, ArrayList arrayList, LongSparseArray longSparseArray, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            AndroidUtilities.runOnUIThread(
                            /*  JADX ERROR: Method code generation error
                                jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x000b: INVOKE  
                                  (wrap: org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$0O6pCOi0jii8pwBAvqjd7az_Jp8 : 0x0008: CONSTRUCTOR  (r0v0 org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$0O6pCOi0jii8pwBAvqjd7az_Jp8) = 
                                  (r6v0 'this' org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1 A[THIS])
                                  (r7v0 'tLRPC$TL_messages_getStickers' org.telegram.tgnet.TLRPC$TL_messages_getStickers)
                                  (r10v0 'tLObject' org.telegram.tgnet.TLObject)
                                  (r8v0 'arrayList' java.util.ArrayList)
                                  (r9v0 'longSparseArray' android.util.LongSparseArray)
                                 call: org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$0O6pCOi0jii8pwBAvqjd7az_Jp8.<init>(org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1, org.telegram.tgnet.TLRPC$TL_messages_getStickers, org.telegram.tgnet.TLObject, java.util.ArrayList, android.util.LongSparseArray):void type: CONSTRUCTOR)
                                 org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.1.lambda$run$3$EmojiView$StickersSearchGridAdapter$1(org.telegram.tgnet.TLRPC$TL_messages_getStickers, java.util.ArrayList, android.util.LongSparseArray, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void, dex: classes2.dex
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
                                  (r7v0 'tLRPC$TL_messages_getStickers' org.telegram.tgnet.TLRPC$TL_messages_getStickers)
                                  (r10v0 'tLObject' org.telegram.tgnet.TLObject)
                                  (r8v0 'arrayList' java.util.ArrayList)
                                  (r9v0 'longSparseArray' android.util.LongSparseArray)
                                 call: org.telegram.ui.Components.-$$Lambda$EmojiView$StickersSearchGridAdapter$1$0O6pCOi0jii8pwBAvqjd7az_Jp8.<init>(org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1, org.telegram.tgnet.TLRPC$TL_messages_getStickers, org.telegram.tgnet.TLObject, java.util.ArrayList, android.util.LongSparseArray):void type: CONSTRUCTOR in method: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.1.lambda$run$3$EmojiView$StickersSearchGridAdapter$1(org.telegram.tgnet.TLRPC$TL_messages_getStickers, java.util.ArrayList, android.util.LongSparseArray, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void, dex: classes2.dex
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

                        public /* synthetic */ void lambda$null$2$EmojiView$StickersSearchGridAdapter$1(TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers, TLObject tLObject, ArrayList arrayList, LongSparseArray longSparseArray) {
                            if (tLRPC$TL_messages_getStickers.emoticon.equals(StickersSearchGridAdapter.this.searchQuery)) {
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
                                }
                            }
                        }
                    };
                    /* access modifiers changed from: private */
                    public ArrayList<TLRPC$StickerSetCovered> serverPacks = new ArrayList<>();
                    /* access modifiers changed from: private */
                    public int totalItems;

                    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                        return false;
                    }

                    static /* synthetic */ int access$14304(StickersSearchGridAdapter stickersSearchGridAdapter) {
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
                        if (obj instanceof TLRPC$Document) {
                            return 0;
                        }
                        return obj instanceof TLRPC$StickerSetCovered ? 3 : 2;
                    }

                    public /* synthetic */ void lambda$onCreateViewHolder$0$EmojiView$StickersSearchGridAdapter(View view) {
                        FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
                        TLRPC$StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
                        if (EmojiView.this.installingStickerSets.indexOfKey(stickerSet.set.id) < 0 && EmojiView.this.removingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
                            if (featuredStickerSetInfoCell.isInstalled()) {
                                EmojiView.this.removingStickerSets.put(stickerSet.set.id, stickerSet);
                                EmojiView.this.delegate.onStickerSetRemove(featuredStickerSetInfoCell.getStickerSet());
                                return;
                            }
                            featuredStickerSetInfoCell.setAddDrawProgress(true, true);
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
                            r2 = 2131165947(0x7var_fb, float:1.7946125E38)
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
                            r2 = 2131625966(0x7f0e07ee, float:1.8879155E38)
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
                            r14.<init>(r13, r15)
                        L_0x00ca:
                            org.telegram.ui.Components.RecyclerListView$Holder r15 = new org.telegram.ui.Components.RecyclerListView$Holder
                            r15.<init>(r14)
                            return r15
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
                    }

                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: boolean} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: boolean} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: boolean} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v12, resolved type: java.lang.Object} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: int} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: boolean} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: boolean} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: java.lang.Integer} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v11, resolved type: boolean} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: boolean} */
                    /* JADX WARNING: Multi-variable type inference failed */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r10, int r11) {
                        /*
                            r9 = this;
                            int r0 = r10.getItemViewType()
                            r1 = 1
                            r2 = 0
                            if (r0 == 0) goto L_0x01ae
                            r3 = 0
                            if (r0 == r1) goto L_0x0128
                            r4 = 2
                            if (r0 == r4) goto L_0x00c4
                            r3 = 3
                            if (r0 == r3) goto L_0x0013
                            goto L_0x01e8
                        L_0x0013:
                            android.util.SparseArray<java.lang.Object> r0 = r9.cache
                            java.lang.Object r11 = r0.get(r11)
                            r4 = r11
                            org.telegram.tgnet.TLRPC$StickerSetCovered r4 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r4
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
                            if (r1 == 0) goto L_0x0076
                        L_0x004a:
                            if (r10 == 0) goto L_0x0061
                            boolean r11 = r3.isInstalled()
                            if (r11 == 0) goto L_0x0061
                            org.telegram.ui.Components.EmojiView r10 = org.telegram.ui.Components.EmojiView.this
                            android.util.LongSparseArray r10 = r10.installingStickerSets
                            org.telegram.tgnet.TLRPC$StickerSet r11 = r4.set
                            long r0 = r11.id
                            r10.remove(r0)
                            r10 = 0
                            goto L_0x0076
                        L_0x0061:
                            if (r1 == 0) goto L_0x0076
                            boolean r11 = r3.isInstalled()
                            if (r11 != 0) goto L_0x0076
                            org.telegram.ui.Components.EmojiView r11 = org.telegram.ui.Components.EmojiView.this
                            android.util.LongSparseArray r11 = r11.removingStickerSets
                            org.telegram.tgnet.TLRPC$StickerSet r0 = r4.set
                            long r0 = r0.id
                            r11.remove(r0)
                        L_0x0076:
                            r3.setAddDrawProgress(r10, r2)
                            java.lang.String r10 = r9.searchQuery
                            boolean r10 = android.text.TextUtils.isEmpty(r10)
                            if (r10 == 0) goto L_0x0084
                            r10 = -1
                            r7 = -1
                            goto L_0x008f
                        L_0x0084:
                            org.telegram.tgnet.TLRPC$StickerSet r10 = r4.set
                            java.lang.String r10 = r10.title
                            java.lang.String r11 = r9.searchQuery
                            int r10 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r10, r11)
                            r7 = r10
                        L_0x008f:
                            if (r7 < 0) goto L_0x009e
                            r5 = 0
                            r6 = 0
                            java.lang.String r10 = r9.searchQuery
                            int r8 = r10.length()
                            r3.setStickerSet(r4, r5, r6, r7, r8)
                            goto L_0x01e8
                        L_0x009e:
                            r3.setStickerSet(r4, r2)
                            java.lang.String r10 = r9.searchQuery
                            boolean r10 = android.text.TextUtils.isEmpty(r10)
                            if (r10 != 0) goto L_0x01e8
                            org.telegram.tgnet.TLRPC$StickerSet r10 = r4.set
                            java.lang.String r10 = r10.short_name
                            java.lang.String r11 = r9.searchQuery
                            int r10 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r10, r11)
                            if (r10 != 0) goto L_0x01e8
                            org.telegram.tgnet.TLRPC$StickerSet r10 = r4.set
                            java.lang.String r10 = r10.short_name
                            java.lang.String r11 = r9.searchQuery
                            int r11 = r11.length()
                            r3.setUrl(r10, r11)
                            goto L_0x01e8
                        L_0x00c4:
                            android.view.View r10 = r10.itemView
                            org.telegram.ui.Cells.StickerSetNameCell r10 = (org.telegram.ui.Cells.StickerSetNameCell) r10
                            android.util.SparseArray<java.lang.Object> r0 = r9.cache
                            java.lang.Object r11 = r0.get(r11)
                            boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
                            if (r0 == 0) goto L_0x01e8
                            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r11 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r11
                            java.lang.String r0 = r9.searchQuery
                            boolean r0 = android.text.TextUtils.isEmpty(r0)
                            if (r0 != 0) goto L_0x00fc
                            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_messages_stickerSet, java.lang.Boolean> r0 = r9.localPacksByShortName
                            boolean r0 = r0.containsKey(r11)
                            if (r0 == 0) goto L_0x00fc
                            org.telegram.tgnet.TLRPC$StickerSet r0 = r11.set
                            if (r0 == 0) goto L_0x00ed
                            java.lang.String r0 = r0.title
                            r10.setText(r0, r2)
                        L_0x00ed:
                            org.telegram.tgnet.TLRPC$StickerSet r11 = r11.set
                            java.lang.String r11 = r11.short_name
                            java.lang.String r0 = r9.searchQuery
                            int r0 = r0.length()
                            r10.setUrl(r11, r0)
                            goto L_0x01e8
                        L_0x00fc:
                            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_messages_stickerSet, java.lang.Integer> r0 = r9.localPacksByName
                            java.lang.Object r0 = r0.get(r11)
                            java.lang.Integer r0 = (java.lang.Integer) r0
                            org.telegram.tgnet.TLRPC$StickerSet r11 = r11.set
                            if (r11 == 0) goto L_0x0123
                            if (r0 == 0) goto L_0x0123
                            java.lang.String r11 = r11.title
                            int r0 = r0.intValue()
                            java.lang.String r1 = r9.searchQuery
                            boolean r1 = android.text.TextUtils.isEmpty(r1)
                            if (r1 != 0) goto L_0x011f
                            java.lang.String r1 = r9.searchQuery
                            int r1 = r1.length()
                            goto L_0x0120
                        L_0x011f:
                            r1 = 0
                        L_0x0120:
                            r10.setText(r11, r2, r0, r1)
                        L_0x0123:
                            r10.setUrl(r3, r2)
                            goto L_0x01e8
                        L_0x0128:
                            android.view.View r10 = r10.itemView
                            org.telegram.ui.Cells.EmptyCell r10 = (org.telegram.ui.Cells.EmptyCell) r10
                            int r0 = r9.totalItems
                            r2 = 1118044160(0x42a40000, float:82.0)
                            if (r11 != r0) goto L_0x01a6
                            android.util.SparseIntArray r0 = r9.positionToRow
                            int r11 = r11 - r1
                            r4 = -2147483648(0xfffffffvar_, float:-0.0)
                            int r11 = r0.get(r11, r4)
                            if (r11 != r4) goto L_0x0142
                            r10.setHeight(r1)
                            goto L_0x01e8
                        L_0x0142:
                            android.util.SparseArray<java.lang.Object> r0 = r9.rowStartPack
                            java.lang.Object r11 = r0.get(r11)
                            boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
                            if (r0 == 0) goto L_0x0159
                            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r11 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r11
                            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r11 = r11.documents
                            int r11 = r11.size()
                            java.lang.Integer r3 = java.lang.Integer.valueOf(r11)
                            goto L_0x0160
                        L_0x0159:
                            boolean r0 = r11 instanceof java.lang.Integer
                            if (r0 == 0) goto L_0x0160
                            r3 = r11
                            java.lang.Integer r3 = (java.lang.Integer) r3
                        L_0x0160:
                            if (r3 != 0) goto L_0x0167
                            r10.setHeight(r1)
                            goto L_0x01e8
                        L_0x0167:
                            int r11 = r3.intValue()
                            if (r11 != 0) goto L_0x0177
                            r11 = 1090519040(0x41000000, float:8.0)
                            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
                            r10.setHeight(r11)
                            goto L_0x01e8
                        L_0x0177:
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
                            if (r11 <= 0) goto L_0x01a2
                            r1 = r11
                        L_0x01a2:
                            r10.setHeight(r1)
                            goto L_0x01e8
                        L_0x01a6:
                            int r11 = org.telegram.messenger.AndroidUtilities.dp(r2)
                            r10.setHeight(r11)
                            goto L_0x01e8
                        L_0x01ae:
                            android.util.SparseArray<java.lang.Object> r0 = r9.cache
                            java.lang.Object r0 = r0.get(r11)
                            org.telegram.tgnet.TLRPC$Document r0 = (org.telegram.tgnet.TLRPC$Document) r0
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
                            if (r11 != 0) goto L_0x01e5
                            org.telegram.ui.Components.EmojiView r11 = org.telegram.ui.Components.EmojiView.this
                            java.util.ArrayList r11 = r11.favouriteStickers
                            boolean r11 = r11.contains(r0)
                            if (r11 == 0) goto L_0x01e4
                            goto L_0x01e5
                        L_0x01e4:
                            r1 = 0
                        L_0x01e5:
                            r10.setRecent(r1)
                        L_0x01e8:
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
                    }

                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: org.telegram.tgnet.TLRPC$StickerSetCovered} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: org.telegram.tgnet.TLRPC$TL_messages_stickerSet} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v21, resolved type: org.telegram.tgnet.TLRPC$StickerSetCovered} */
                    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v22, resolved type: org.telegram.tgnet.TLRPC$StickerSetCovered} */
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
                            if (r6 >= r8) goto L_0x01e3
                            if (r6 != r5) goto L_0x004f
                            android.util.SparseArray<java.lang.Object> r8 = r0.cache
                            int r9 = r0.totalItems
                            int r10 = r9 + 1
                            r0.totalItems = r10
                            java.lang.String r10 = "search"
                            r8.put(r9, r10)
                            int r7 = r7 + 1
                            r16 = r2
                            goto L_0x01db
                        L_0x004f:
                            if (r6 >= r3) goto L_0x005f
                            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r8 = r0.localPacks
                            java.lang.Object r8 = r8.get(r6)
                            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r8
                            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r8.documents
                            r16 = r2
                            goto L_0x0139
                        L_0x005f:
                            int r8 = r6 - r3
                            if (r8 >= r4) goto L_0x012b
                            java.util.ArrayList<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r8 = r0.emojiArrays
                            int r8 = r8.size()
                            java.lang.String r9 = ""
                            r10 = 0
                            r11 = 0
                        L_0x006d:
                            if (r10 >= r8) goto L_0x00f1
                            java.util.ArrayList<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r12 = r0.emojiArrays
                            java.lang.Object r12 = r12.get(r10)
                            java.util.ArrayList r12 = (java.util.ArrayList) r12
                            java.util.HashMap<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>, java.lang.String> r13 = r0.emojiStickers
                            java.lang.Object r13 = r13.get(r12)
                            java.lang.String r13 = (java.lang.String) r13
                            if (r13 == 0) goto L_0x0090
                            boolean r14 = r9.equals(r13)
                            if (r14 != 0) goto L_0x0090
                            android.util.SparseArray<java.lang.String> r9 = r0.positionToEmoji
                            int r14 = r0.totalItems
                            int r14 = r14 + r11
                            r9.put(r14, r13)
                            r9 = r13
                        L_0x0090:
                            int r13 = r12.size()
                            r14 = 0
                        L_0x0095:
                            if (r14 >= r13) goto L_0x00e5
                            int r15 = r0.totalItems
                            int r15 = r15 + r11
                            org.telegram.ui.Components.EmojiView r1 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.EmojiView$StickersGridAdapter r1 = r1.stickersGridAdapter
                            int r1 = r1.stickersPerRow
                            int r1 = r11 / r1
                            int r1 = r1 + r7
                            java.lang.Object r16 = r12.get(r14)
                            r5 = r16
                            org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC$Document) r5
                            r16 = r2
                            android.util.SparseArray<java.lang.Object> r2 = r0.cache
                            r2.put(r15, r5)
                            org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                            int r2 = r2.currentAccount
                            org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.getInstance(r2)
                            r17 = r8
                            r18 = r9
                            long r8 = org.telegram.messenger.MediaDataController.getStickerSetId(r5)
                            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = r2.getStickerSetById(r8)
                            if (r2 == 0) goto L_0x00d3
                            android.util.SparseArray<java.lang.Object> r5 = r0.cacheParent
                            r5.put(r15, r2)
                        L_0x00d3:
                            android.util.SparseIntArray r2 = r0.positionToRow
                            r2.put(r15, r1)
                            int r11 = r11 + 1
                            int r14 = r14 + 1
                            r2 = r16
                            r8 = r17
                            r9 = r18
                            r1 = 0
                            r5 = -1
                            goto L_0x0095
                        L_0x00e5:
                            r16 = r2
                            r17 = r8
                            r18 = r9
                            int r10 = r10 + 1
                            r1 = 0
                            r5 = -1
                            goto L_0x006d
                        L_0x00f1:
                            r16 = r2
                            float r1 = (float) r11
                            org.telegram.ui.Components.EmojiView r2 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.EmojiView$StickersGridAdapter r2 = r2.stickersGridAdapter
                            int r2 = r2.stickersPerRow
                            float r2 = (float) r2
                            float r1 = r1 / r2
                            double r1 = (double) r1
                            double r1 = java.lang.Math.ceil(r1)
                            int r1 = (int) r1
                            r2 = 0
                        L_0x0107:
                            if (r2 >= r1) goto L_0x0117
                            android.util.SparseArray<java.lang.Object> r5 = r0.rowStartPack
                            int r8 = r7 + r2
                            java.lang.Integer r9 = java.lang.Integer.valueOf(r11)
                            r5.put(r8, r9)
                            int r2 = r2 + 1
                            goto L_0x0107
                        L_0x0117:
                            int r2 = r0.totalItems
                            org.telegram.ui.Components.EmojiView r5 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.EmojiView$StickersGridAdapter r5 = r5.stickersGridAdapter
                            int r5 = r5.stickersPerRow
                            int r5 = r5 * r1
                            int r2 = r2 + r5
                            r0.totalItems = r2
                            int r7 = r7 + r1
                            goto L_0x01db
                        L_0x012b:
                            r16 = r2
                            int r8 = r8 - r4
                            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r1 = r0.serverPacks
                            java.lang.Object r1 = r1.get(r8)
                            r8 = r1
                            org.telegram.tgnet.TLRPC$StickerSetCovered r8 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r8
                            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r9 = r8.covers
                        L_0x0139:
                            boolean r1 = r9.isEmpty()
                            if (r1 == 0) goto L_0x0141
                            goto L_0x01db
                        L_0x0141:
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
                            if (r6 < r3) goto L_0x016f
                            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$StickerSetCovered
                            if (r2 == 0) goto L_0x016f
                            android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r2 = r0.positionsToSets
                            int r5 = r0.totalItems
                            r10 = r8
                            org.telegram.tgnet.TLRPC$StickerSetCovered r10 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r10
                            r2.put(r5, r10)
                        L_0x016f:
                            android.util.SparseIntArray r2 = r0.positionToRow
                            int r5 = r0.totalItems
                            r2.put(r5, r7)
                            int r2 = r9.size()
                            r5 = 0
                        L_0x017b:
                            if (r5 >= r2) goto L_0x01b8
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
                            org.telegram.tgnet.TLRPC$Document r5 = (org.telegram.tgnet.TLRPC$Document) r5
                            android.util.SparseArray<java.lang.Object> r13 = r0.cache
                            r13.put(r11, r5)
                            if (r8 == 0) goto L_0x01a3
                            android.util.SparseArray<java.lang.Object> r5 = r0.cacheParent
                            r5.put(r11, r8)
                        L_0x01a3:
                            android.util.SparseIntArray r5 = r0.positionToRow
                            r5.put(r11, r12)
                            if (r6 < r3) goto L_0x01b6
                            boolean r5 = r8 instanceof org.telegram.tgnet.TLRPC$StickerSetCovered
                            if (r5 == 0) goto L_0x01b6
                            android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r5 = r0.positionsToSets
                            r12 = r8
                            org.telegram.tgnet.TLRPC$StickerSetCovered r12 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r12
                            r5.put(r11, r12)
                        L_0x01b6:
                            r5 = r10
                            goto L_0x017b
                        L_0x01b8:
                            int r2 = r1 + 1
                            r5 = 0
                        L_0x01bb:
                            if (r5 >= r2) goto L_0x01c7
                            android.util.SparseArray<java.lang.Object> r9 = r0.rowStartPack
                            int r10 = r7 + r5
                            r9.put(r10, r8)
                            int r5 = r5 + 1
                            goto L_0x01bb
                        L_0x01c7:
                            int r5 = r0.totalItems
                            org.telegram.ui.Components.EmojiView r8 = org.telegram.ui.Components.EmojiView.this
                            org.telegram.ui.Components.EmojiView$StickersGridAdapter r8 = r8.stickersGridAdapter
                            int r8 = r8.stickersPerRow
                            int r1 = r1 * r8
                            int r1 = r1 + 1
                            int r5 = r5 + r1
                            r0.totalItems = r5
                            int r7 = r7 + r2
                        L_0x01db:
                            int r6 = r6 + 1
                            r2 = r16
                            r1 = 0
                            r5 = -1
                            goto L_0x0035
                        L_0x01e3:
                            super.notifyDataSetChanged()
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.notifyDataSetChanged():void");
                    }
                }
            }
