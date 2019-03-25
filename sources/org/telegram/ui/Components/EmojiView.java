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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
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
import org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate$$CC;
import org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate;

public class EmojiView extends FrameLayout implements NotificationCenterDelegate {
    private static final OnScrollChangedListener NOP = EmojiView$$Lambda$9.$instance;
    private static final Field superListenerField;
    private ImageView backspaceButton;
    private AnimatorSet backspaceButtonAnimation;
    private boolean backspaceOnce;
    private boolean backspacePressed;
    private FrameLayout bottomTabContainer;
    private AnimatorSet bottomTabContainerAnimation;
    private View bottomTabContainerBackground;
    private ContentPreviewViewerDelegate contentPreviewViewerDelegate = new ContentPreviewViewerDelegate() {
        public boolean needOpen() {
            return ContentPreviewViewer$ContentPreviewViewerDelegate$$CC.needOpen(this);
        }

        public void sendSticker(Document sticker, Object parent) {
            EmojiView.this.delegate.onStickerSelected(sticker, parent);
        }

        public boolean needSend() {
            return true;
        }

        public void openSet(InputStickerSet set) {
            if (set != null) {
                EmojiView.this.delegate.onShowStickerSet(null, set);
            }
        }

        public void sendGif(Document gif) {
            if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter) {
                EmojiView.this.delegate.onGifSelected(gif, "gif");
            } else if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter) {
                EmojiView.this.delegate.onGifSelected(gif, EmojiView.this.gifSearchAdapter.bot);
            }
        }

        public void gifAddedOrDeleted() {
            EmojiView.this.recentGifs = DataQuery.getInstance(EmojiView.this.currentAccount).getRecentGifs();
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
    private String lastSearchKeyboardLanguage;
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
    private boolean scrolledToTrending;
    private AnimatorSet searchAnimation;
    private ImageView searchButton;
    private int searchFieldHeight = AndroidUtilities.dp(64.0f);
    private View shadowLine;
    private boolean showGifs;
    private Drawable[] stickerIcons;
    private ArrayList<TL_messages_stickerSet> stickerSets = new ArrayList();
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

    public interface EmojiViewDelegate {
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

        public void setEmoji(String emoji, int arrowPosition) {
            this.currentEmoji = emoji;
            this.arrowX = arrowPosition;
            this.rectPaint.setColor(NUM);
            invalidate();
        }

        public String getEmoji() {
            return this.currentEmoji;
        }

        public void setSelection(int position) {
            if (this.selection != position) {
                this.selection = position;
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
            float f;
            this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(AndroidUtilities.isTablet() ? 60.0f : 52.0f));
            this.backgroundDrawable.draw(canvas);
            Drawable drawable = this.arrowDrawable;
            int dp = this.arrowX - AndroidUtilities.dp(9.0f);
            int dp2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 55.5f : 47.5f);
            int dp3 = AndroidUtilities.dp(9.0f) + this.arrowX;
            if (AndroidUtilities.isTablet()) {
                f = 55.5f;
            } else {
                f = 47.5f;
            }
            drawable.setBounds(dp, dp2, dp3, AndroidUtilities.dp(f + 8.0f));
            this.arrowDrawable.draw(canvas);
            if (this.currentEmoji != null) {
                for (int a = 0; a < 6; a++) {
                    int x = (EmojiView.this.emojiSize * a) + AndroidUtilities.dp((float) ((a * 4) + 5));
                    int y = AndroidUtilities.dp(9.0f);
                    if (this.selection == a) {
                        this.rect.set((float) x, (float) (y - ((int) AndroidUtilities.dpf2(3.5f))), (float) (EmojiView.this.emojiSize + x), (float) ((EmojiView.this.emojiSize + y) + AndroidUtilities.dp(3.0f)));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.rectPaint);
                    }
                    String code = this.currentEmoji;
                    if (a != 0) {
                        String color;
                        switch (a) {
                            case 1:
                                color = "🏻";
                                break;
                            case 2:
                                color = "🏼";
                                break;
                            case 3:
                                color = "🏽";
                                break;
                            case 4:
                                color = "🏾";
                                break;
                            case 5:
                                color = "🏿";
                                break;
                            default:
                                color = "";
                                break;
                        }
                        code = EmojiView.addColorToCode(code, color);
                    }
                    Drawable drawable2 = Emoji.getEmojiBigDrawable(code);
                    if (drawable2 != null) {
                        drawable2.setBounds(x, y, EmojiView.this.emojiSize + x, EmojiView.this.emojiSize + y);
                        drawable2.draw(canvas);
                    }
                }
            }
        }
    }

    private class EmojiGridAdapter extends SelectionAdapter {
        private int itemCount;
        private SparseIntArray positionToSection;
        private SparseIntArray sectionToPosition;

        private EmojiGridAdapter() {
            this.positionToSection = new SparseIntArray();
            this.sectionToPosition = new SparseIntArray();
        }

        /* synthetic */ EmojiGridAdapter(EmojiView x0, AnonymousClass1 x1) {
            this();
        }

        public int getItemCount() {
            return this.itemCount;
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new ImageViewEmoji(EmojiView.this.getContext());
                    break;
                case 1:
                    view = new StickerSetNameCell(EmojiView.this.getContext(), true);
                    break;
                default:
                    view = new View(EmojiView.this.getContext());
                    view.setLayoutParams(new LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    String code;
                    String coloredCode;
                    boolean recent;
                    ImageViewEmoji imageView = holder.itemView;
                    if (EmojiView.this.needEmojiSearch) {
                        position--;
                    }
                    int count = Emoji.recentEmoji.size();
                    if (position < count) {
                        code = (String) Emoji.recentEmoji.get(position);
                        coloredCode = code;
                        recent = true;
                    } else {
                        code = null;
                        coloredCode = null;
                        int a = 0;
                        while (a < EmojiData.dataColored.length) {
                            int size = EmojiData.dataColored[a].length + 1;
                            if (position < count + size) {
                                code = EmojiData.dataColored[a][(position - count) - 1];
                                coloredCode = code;
                                String color = (String) Emoji.emojiColor.get(code);
                                if (color != null) {
                                    coloredCode = EmojiView.addColorToCode(coloredCode, color);
                                }
                                recent = false;
                            } else {
                                count += size;
                                a++;
                            }
                        }
                        recent = false;
                    }
                    imageView.setImageDrawable(Emoji.getEmojiBigDrawable(coloredCode), recent);
                    imageView.setTag(code);
                    imageView.setContentDescription(coloredCode);
                    return;
                case 1:
                    holder.itemView.setText(EmojiView.this.emojiTitles[this.positionToSection.get(position)], 0);
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (EmojiView.this.needEmojiSearch && position == 0) {
                return 2;
            }
            if (this.positionToSection.indexOfKey(position) >= 0) {
                return 1;
            }
            return 0;
        }

        public void notifyDataSetChanged() {
            this.positionToSection.clear();
            this.itemCount = (EmojiView.this.needEmojiSearch ? 1 : 0) + Emoji.recentEmoji.size();
            for (int a = 0; a < EmojiData.dataColored.length; a++) {
                this.positionToSection.put(this.itemCount, a);
                this.sectionToPosition.put(a, this.itemCount);
                this.itemCount += EmojiData.dataColored[a].length + 1;
            }
            EmojiView.this.updateEmojiTabs();
            super.notifyDataSetChanged();
        }
    }

    private class EmojiPagesAdapter extends PagerAdapter implements IconTabProvider {
        private EmojiPagesAdapter() {
        }

        /* synthetic */ EmojiPagesAdapter(EmojiView x0, AnonymousClass1 x1) {
            this();
        }

        public void destroyItem(ViewGroup viewGroup, int position, Object object) {
            viewGroup.removeView((View) EmojiView.this.views.get(position));
        }

        public boolean canScrollToTab(int position) {
            boolean z = true;
            if ((position != 1 && position != 2) || EmojiView.this.currentChatId == 0) {
                return true;
            }
            EmojiView emojiView = EmojiView.this;
            if (position != 1) {
                z = false;
            }
            emojiView.showStickerBanHint(z);
            return false;
        }

        public int getCount() {
            return EmojiView.this.views.size();
        }

        public Drawable getPageIconDrawable(int position) {
            return EmojiView.this.tabIcons[position];
        }

        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return LocaleController.getString("Emoji", NUM);
                case 1:
                    return LocaleController.getString("AccDescrGIFs", NUM);
                case 2:
                    return LocaleController.getString("AccDescrStickers", NUM);
                default:
                    return null;
            }
        }

        public void customOnDraw(Canvas canvas, int position) {
            if (position == 2 && !DataQuery.getInstance(EmojiView.this.currentAccount).getUnreadStickerSets().isEmpty() && EmojiView.this.dotPaint != null) {
                canvas.drawCircle((float) ((canvas.getWidth() / 2) + AndroidUtilities.dp(9.0f)), (float) ((canvas.getHeight() / 2) - AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(5.0f), EmojiView.this.dotPaint);
            }
        }

        public Object instantiateItem(ViewGroup viewGroup, int position) {
            View view = (View) EmojiView.this.views.get(position);
            viewGroup.addView(view);
            return view;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
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

        public EmojiPopupWindow(int width, int height) {
            super(width, height);
            init();
        }

        public EmojiPopupWindow(View contentView) {
            super(contentView);
            init();
        }

        public EmojiPopupWindow(View contentView, int width, int height, boolean focusable) {
            super(contentView, width, height, focusable);
            init();
        }

        public EmojiPopupWindow(View contentView, int width, int height) {
            super(contentView, width, height);
            init();
        }

        private void init() {
            if (EmojiView.superListenerField != null) {
                try {
                    this.mSuperScrollListener = (OnScrollChangedListener) EmojiView.superListenerField.get(this);
                    EmojiView.superListenerField.set(this, EmojiView.NOP);
                } catch (Exception e) {
                    this.mSuperScrollListener = null;
                }
            }
        }

        private void unregisterListener() {
            if (this.mSuperScrollListener != null && this.mViewTreeObserver != null) {
                if (this.mViewTreeObserver.isAlive()) {
                    this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                }
                this.mViewTreeObserver = null;
            }
        }

        private void registerListener(View anchor) {
            if (this.mSuperScrollListener != null) {
                ViewTreeObserver vto = anchor.getWindowToken() != null ? anchor.getViewTreeObserver() : null;
                if (vto != this.mViewTreeObserver) {
                    if (this.mViewTreeObserver != null && this.mViewTreeObserver.isAlive()) {
                        this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                    }
                    this.mViewTreeObserver = vto;
                    if (vto != null) {
                        vto.addOnScrollChangedListener(this.mSuperScrollListener);
                    }
                }
            }
        }

        public void showAsDropDown(View anchor, int xoff, int yoff) {
            try {
                super.showAsDropDown(anchor, xoff, yoff);
                registerListener(anchor);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void update(View anchor, int xoff, int yoff, int width, int height) {
            super.update(anchor, xoff, yoff, width, height);
            registerListener(anchor);
        }

        public void update(View anchor, int width, int height) {
            super.update(anchor, width, height);
            registerListener(anchor);
        }

        public void showAtLocation(View parent, int gravity, int x, int y) {
            super.showAtLocation(parent, gravity, x, y);
            unregisterListener();
        }

        public void dismiss() {
            setFocusable(false);
            try {
                super.dismiss();
            } catch (Exception e) {
            }
            unregisterListener();
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

        /* synthetic */ EmojiSearchAdapter(EmojiView x0, AnonymousClass1 x1) {
            this();
        }

        public int getItemCount() {
            if (this.result.isEmpty() && !this.searchWas) {
                return Emoji.recentEmoji.size() + 1;
            }
            if (this.result.isEmpty()) {
                return 2;
            }
            return this.result.size() + 1;
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new ImageViewEmoji(EmojiView.this.getContext());
                    break;
                case 1:
                    view = new View(EmojiView.this.getContext());
                    view.setLayoutParams(new LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    break;
                default:
                    View frameLayout = new FrameLayout(EmojiView.this.getContext()) {
                        /* Access modifiers changed, original: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            int parentHeight;
                            View parent = (View) EmojiView.this.getParent();
                            if (parent != null) {
                                parentHeight = (int) (((float) parent.getMeasuredHeight()) - EmojiView.this.getY());
                            } else {
                                parentHeight = AndroidUtilities.dp(120.0f);
                            }
                            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(parentHeight - EmojiView.this.searchFieldHeight, NUM));
                        }
                    };
                    TextView textView = new TextView(EmojiView.this.getContext());
                    textView.setText(LocaleController.getString("NoEmojiFound", NUM));
                    textView.setTextSize(1, 16.0f);
                    textView.setTextColor(Theme.getColor("chat_emojiPanelEmptyText"));
                    frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 10.0f, 0.0f, 0.0f));
                    ImageView imageView = new ImageView(EmojiView.this.getContext());
                    imageView.setScaleType(ScaleType.CENTER);
                    imageView.setImageResource(NUM);
                    imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelEmptyText"), Mode.MULTIPLY));
                    frameLayout.addView(imageView, LayoutHelper.createFrame(48, 48, 85));
                    imageView.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            final boolean[] loadingUrl = new boolean[1];
                            final Builder builder = new Builder(EmojiView.this.getContext());
                            LinearLayout linearLayout = new LinearLayout(EmojiView.this.getContext());
                            linearLayout.setOrientation(1);
                            linearLayout.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                            ImageView imageView1 = new ImageView(EmojiView.this.getContext());
                            imageView1.setImageResource(NUM);
                            linearLayout.addView(imageView1, LayoutHelper.createLinear(-2, -2, 49, 0, 15, 0, 0));
                            TextView textView = new TextView(EmojiView.this.getContext());
                            textView.setText(LocaleController.getString("EmojiSuggestions", NUM));
                            textView.setTextSize(1, 15.0f);
                            textView.setTextColor(Theme.getColor("dialogTextBlue2"));
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
                            String str = "EmojiSuggestionsUrl";
                            Object[] objArr = new Object[1];
                            objArr[0] = EmojiSearchAdapter.this.lastSearchAlias != null ? EmojiSearchAdapter.this.lastSearchAlias : EmojiView.this.lastSearchKeyboardLanguage;
                            textView.setText(LocaleController.formatString(str, NUM, objArr));
                            textView.setTextSize(1, 15.0f);
                            textView.setTextColor(Theme.getColor("dialogTextLink"));
                            textView.setGravity(LocaleController.isRTL ? 5 : 3);
                            linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 51, 0, 18, 0, 16));
                            textView.setOnClickListener(new OnClickListener() {
                                public void onClick(View v) {
                                    if (!loadingUrl[0]) {
                                        loadingUrl[0] = true;
                                        AlertDialog[] progressDialog = new AlertDialog[]{new AlertDialog(EmojiView.this.getContext(), 3)};
                                        TL_messages_getEmojiURL req = new TL_messages_getEmojiURL();
                                        req.lang_code = EmojiSearchAdapter.this.lastSearchAlias != null ? EmojiSearchAdapter.this.lastSearchAlias : EmojiView.this.lastSearchKeyboardLanguage;
                                        AndroidUtilities.runOnUIThread(new EmojiView$EmojiSearchAdapter$2$1$$Lambda$1(this, progressDialog, ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(req, new EmojiView$EmojiSearchAdapter$2$1$$Lambda$0(this, progressDialog, builder))), 1000);
                                    }
                                }

                                /* Access modifiers changed, original: final|synthetic */
                                public final /* synthetic */ void lambda$onClick$1$EmojiView$EmojiSearchAdapter$2$1(AlertDialog[] progressDialog, Builder builder, TLObject response, TL_error error) {
                                    AndroidUtilities.runOnUIThread(new EmojiView$EmojiSearchAdapter$2$1$$Lambda$3(this, progressDialog, response, builder));
                                }

                                /* Access modifiers changed, original: final|synthetic */
                                public final /* synthetic */ void lambda$null$0$EmojiView$EmojiSearchAdapter$2$1(AlertDialog[] progressDialog, TLObject response, Builder builder) {
                                    try {
                                        progressDialog[0].dismiss();
                                    } catch (Throwable th) {
                                    }
                                    progressDialog[0] = null;
                                    if (response instanceof TL_emojiURL) {
                                        Browser.openUrl(EmojiView.this.getContext(), ((TL_emojiURL) response).url);
                                        builder.getDismissRunnable().run();
                                    }
                                }

                                /* Access modifiers changed, original: final|synthetic */
                                public final /* synthetic */ void lambda$onClick$3$EmojiView$EmojiSearchAdapter$2$1(AlertDialog[] progressDialog, int requestId) {
                                    if (progressDialog[0] != null) {
                                        progressDialog[0].setOnCancelListener(new EmojiView$EmojiSearchAdapter$2$1$$Lambda$2(this, requestId));
                                        progressDialog[0].show();
                                    }
                                }

                                /* Access modifiers changed, original: final|synthetic */
                                public final /* synthetic */ void lambda$null$2$EmojiView$EmojiSearchAdapter$2$1(int requestId, DialogInterface dialog) {
                                    ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(requestId, true);
                                }
                            });
                            builder.setCustomView(linearLayout);
                            builder.show();
                        }
                    });
                    view = frameLayout;
                    view.setLayoutParams(new LayoutParams(-1, -2));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    String code;
                    String coloredCode;
                    boolean recent;
                    ImageViewEmoji imageView = holder.itemView;
                    position--;
                    if (!this.result.isEmpty() || this.searchWas) {
                        code = ((KeywordResult) this.result.get(position)).emoji;
                        coloredCode = code;
                        recent = false;
                    } else {
                        code = (String) Emoji.recentEmoji.get(position);
                        coloredCode = code;
                        recent = true;
                    }
                    imageView.setImageDrawable(Emoji.getEmojiBigDrawable(coloredCode), recent);
                    imageView.setTag(code);
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            if (position == 1 && this.searchWas && this.result.isEmpty()) {
                return 2;
            }
            return 0;
        }

        public void search(String text) {
            if (TextUtils.isEmpty(text)) {
                this.lastSearchEmojiString = null;
                if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiAdapter) {
                    EmojiView.this.emojiGridView.setAdapter(EmojiView.this.emojiAdapter);
                    this.searchWas = false;
                }
                notifyDataSetChanged();
            } else {
                this.lastSearchEmojiString = text.toLowerCase();
            }
            if (this.searchRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
            }
            if (!TextUtils.isEmpty(this.lastSearchEmojiString)) {
                AnonymousClass3 anonymousClass3 = new Runnable() {
                    public void run() {
                        EmojiView.this.emojiSearchField.progressDrawable.startAnimation();
                        final String query = EmojiSearchAdapter.this.lastSearchEmojiString;
                        String newLanguage = DataQuery.getInstance(EmojiView.this.currentAccount).getCurrentKeyboardLanguage();
                        if (!(TextUtils.isEmpty(EmojiView.this.lastSearchKeyboardLanguage) || EmojiView.this.lastSearchKeyboardLanguage.equals(newLanguage))) {
                            DataQuery.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(newLanguage);
                        }
                        EmojiView.this.lastSearchKeyboardLanguage = newLanguage;
                        DataQuery.getInstance(EmojiView.this.currentAccount).getEmojiSuggestions(EmojiView.this.lastSearchKeyboardLanguage, EmojiSearchAdapter.this.lastSearchEmojiString, false, new KeywordResultCallback() {
                            public void run(ArrayList<KeywordResult> param, String alias) {
                                if (query.equals(EmojiSearchAdapter.this.lastSearchEmojiString)) {
                                    EmojiSearchAdapter.this.lastSearchAlias = alias;
                                    EmojiView.this.emojiSearchField.progressDrawable.stopAnimation();
                                    EmojiSearchAdapter.this.searchWas = true;
                                    if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiSearchAdapter) {
                                        EmojiView.this.emojiGridView.setAdapter(EmojiView.this.emojiSearchAdapter);
                                    }
                                    EmojiSearchAdapter.this.result = param;
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

        public GifAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public int getItemCount() {
            return EmojiView.this.recentGifs.size() + 1;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            return 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View cell = new ContextLinkCell(this.mContext);
                    cell.setContentDescription(LocaleController.getString("AttachGif", NUM));
                    cell.setCanPreviewGif(true);
                    view = cell;
                    break;
                default:
                    view = new View(EmojiView.this.getContext());
                    view.setLayoutParams(new LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    Document document = (Document) EmojiView.this.recentGifs.get(position - 1);
                    if (document != null) {
                        ((ContextLinkCell) holder.itemView).setGif(document, false);
                        return;
                    }
                    return;
                default:
                    return;
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

        public GifSearchAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public int getItemCount() {
            return (this.results.isEmpty() ? 1 : this.results.size()) + 1;
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            if (this.results.isEmpty()) {
                return 2;
            }
            return 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View cell = new ContextLinkCell(this.mContext);
                    cell.setContentDescription(LocaleController.getString("AttachGif", NUM));
                    cell.setCanPreviewGif(true);
                    view = cell;
                    break;
                case 1:
                    view = new View(EmojiView.this.getContext());
                    view.setLayoutParams(new LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    break;
                default:
                    View frameLayout = new FrameLayout(EmojiView.this.getContext()) {
                        /* Access modifiers changed, original: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((int) (((float) (((EmojiView.this.gifGridView.getMeasuredHeight() - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3)) * 1.7f), NUM));
                        }
                    };
                    ImageView imageView = new ImageView(EmojiView.this.getContext());
                    imageView.setScaleType(ScaleType.CENTER);
                    imageView.setImageResource(NUM);
                    imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelEmptyText"), Mode.MULTIPLY));
                    frameLayout.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 59.0f));
                    TextView textView = new TextView(EmojiView.this.getContext());
                    textView.setText(LocaleController.getString("NoGIFsFound", NUM));
                    textView.setTextSize(1, 16.0f);
                    textView.setTextColor(Theme.getColor("chat_emojiPanelEmptyText"));
                    frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 9.0f));
                    view = frameLayout;
                    view.setLayoutParams(new LayoutParams(-1, -2));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    holder.itemView.setLink((BotInlineResult) this.results.get(position - 1), true, false, false);
                    return;
                default:
                    return;
            }
        }

        public void search(final String text) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            if (TextUtils.isEmpty(text)) {
                this.lastSearchImageString = null;
                if (EmojiView.this.gifGridView.getAdapter() != EmojiView.this.gifAdapter) {
                    EmojiView.this.gifGridView.setAdapter(EmojiView.this.gifAdapter);
                }
                notifyDataSetChanged();
            } else {
                this.lastSearchImageString = text.toLowerCase();
            }
            if (this.searchRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
            }
            if (!TextUtils.isEmpty(this.lastSearchImageString)) {
                AnonymousClass2 anonymousClass2 = new Runnable() {
                    public void run() {
                        GifSearchAdapter.this.search(text, "", true);
                    }
                };
                this.searchRunnable = anonymousClass2;
                AndroidUtilities.runOnUIThread(anonymousClass2, 300);
            }
        }

        private void searchBotUser() {
            if (!this.searchingUser) {
                this.searchingUser = true;
                TL_contacts_resolveUsername req = new TL_contacts_resolveUsername();
                req.username = MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot;
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(req, new EmojiView$GifSearchAdapter$$Lambda$0(this));
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$searchBotUser$1$EmojiView$GifSearchAdapter(TLObject response, TL_error error) {
            if (response != null) {
                AndroidUtilities.runOnUIThread(new EmojiView$GifSearchAdapter$$Lambda$3(this, response));
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$0$EmojiView$GifSearchAdapter(TLObject response) {
            TL_contacts_resolvedPeer res = (TL_contacts_resolvedPeer) response;
            MessagesController.getInstance(EmojiView.this.currentAccount).putUsers(res.users, false);
            MessagesController.getInstance(EmojiView.this.currentAccount).putChats(res.chats, false);
            MessagesStorage.getInstance(EmojiView.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
            String str = this.lastSearchImageString;
            this.lastSearchImageString = null;
            search(str, "", false);
        }

        private void search(String query, String offset, boolean searchUser) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            this.lastSearchImageString = query;
            TLObject object = MessagesController.getInstance(EmojiView.this.currentAccount).getUserOrChat(MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot);
            if (object instanceof User) {
                if (TextUtils.isEmpty(offset)) {
                    EmojiView.this.gifSearchField.progressDrawable.startAnimation();
                }
                this.bot = (User) object;
                TL_messages_getInlineBotResults req = new TL_messages_getInlineBotResults();
                if (query == null) {
                    query = "";
                }
                req.query = query;
                req.bot = MessagesController.getInstance(EmojiView.this.currentAccount).getInputUser(this.bot);
                req.offset = offset;
                req.peer = new TL_inputPeerEmpty();
                this.reqId = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(req, new EmojiView$GifSearchAdapter$$Lambda$1(this, req, offset), 2);
            } else if (searchUser) {
                searchBotUser();
                EmojiView.this.gifSearchField.progressDrawable.startAnimation();
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$search$3$EmojiView$GifSearchAdapter(TL_messages_getInlineBotResults req, String offset, TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new EmojiView$GifSearchAdapter$$Lambda$2(this, req, offset, response));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$2$EmojiView$GifSearchAdapter(TL_messages_getInlineBotResults req, String offset, TLObject response) {
            boolean z = false;
            if (req.query.equals(this.lastSearchImageString)) {
                if (EmojiView.this.gifGridView.getAdapter() != EmojiView.this.gifSearchAdapter) {
                    EmojiView.this.gifGridView.setAdapter(EmojiView.this.gifSearchAdapter);
                }
                if (TextUtils.isEmpty(offset)) {
                    this.results.clear();
                    this.resultsMap.clear();
                    EmojiView.this.gifSearchField.progressDrawable.stopAnimation();
                }
                this.reqId = 0;
                if (response instanceof messages_BotResults) {
                    int addedCount = 0;
                    int oldCount = this.results.size();
                    messages_BotResults res = (messages_BotResults) response;
                    this.nextSearchOffset = res.next_offset;
                    for (int a = 0; a < res.results.size(); a++) {
                        BotInlineResult result = (BotInlineResult) res.results.get(a);
                        if (!this.resultsMap.containsKey(result.id)) {
                            result.query_id = res.query_id;
                            this.results.add(result);
                            this.resultsMap.put(result.id, result);
                            addedCount++;
                        }
                    }
                    if (oldCount == this.results.size()) {
                        z = true;
                    }
                    this.searchEndReached = z;
                    if (addedCount != 0) {
                        if (oldCount != 0) {
                            notifyItemChanged(oldCount);
                        }
                        notifyItemRangeInserted(oldCount + 1, addedCount);
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

    private class ImageViewEmoji extends ImageView {
        private boolean isRecent;

        public ImageViewEmoji(Context context) {
            super(context);
            setScaleType(ScaleType.CENTER);
        }

        private void sendEmoji(String override) {
            String code;
            EmojiView.this.showBottomTab(true, true);
            if (override != null) {
                code = override;
            } else {
                code = (String) getTag();
            }
            new SpannableStringBuilder().append(code);
            if (override == null) {
                if (!this.isRecent) {
                    String color = (String) Emoji.emojiColor.get(code);
                    if (color != null) {
                        code = EmojiView.addColorToCode(code, color);
                    }
                }
                EmojiView.this.addEmojiToRecent(code);
                if (EmojiView.this.delegate != null) {
                    EmojiView.this.delegate.onEmojiSelected(Emoji.fixEmoji(code));
                }
            } else if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.onEmojiSelected(Emoji.fixEmoji(override));
            }
        }

        public void setImageDrawable(Drawable drawable, boolean recent) {
            super.setImageDrawable(drawable);
            this.isRecent = recent;
        }

        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec));
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

        public SearchField(Context context, final int type) {
            super(context);
            this.shadowView = new View(context);
            this.shadowView.setAlpha(0.0f);
            this.shadowView.setTag(Integer.valueOf(1));
            this.shadowView.setBackgroundColor(Theme.getColor("chat_emojiPanelShadowLine"));
            addView(this.shadowView, new FrameLayout.LayoutParams(-1, EmojiView.this.getShadowHeight(), 83));
            this.backgroundView = new View(context);
            this.backgroundView.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
            addView(this.backgroundView, new FrameLayout.LayoutParams(-1, EmojiView.this.searchFieldHeight));
            this.searchBackground = new View(context);
            this.searchBackground.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor("chat_emojiSearchBackground")));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 14.0f, 14.0f, 0.0f));
            this.searchIconImageView = new ImageView(context);
            this.searchIconImageView.setScaleType(ScaleType.CENTER);
            this.searchIconImageView.setImageResource(NUM);
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiSearchIcon"), Mode.MULTIPLY));
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
            this.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiSearchIcon"), Mode.MULTIPLY));
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 14.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new EmojiView$SearchField$$Lambda$0(this));
            this.searchEditText = new EditTextBoldCursor(context, EmojiView.this) {
                public boolean onTouchEvent(MotionEvent event) {
                    int i = 1;
                    if (event.getAction() == 0) {
                        if (!EmojiView.this.delegate.isSearchOpened()) {
                            EmojiView.this.openSearch(SearchField.this);
                        }
                        EmojiViewDelegate access$000 = EmojiView.this.delegate;
                        if (type == 1) {
                            i = 2;
                        }
                        access$000.onSearchOpenClose(i);
                        SearchField.this.searchEditText.requestFocus();
                        AndroidUtilities.showKeyboard(SearchField.this.searchEditText);
                        if (EmojiView.this.trendingGridView != null && EmojiView.this.trendingGridView.getVisibility() == 0) {
                            EmojiView.this.showTrendingTab(false);
                        }
                    }
                    return super.onTouchEvent(event);
                }
            };
            this.searchEditText.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(Theme.getColor("chat_emojiSearchIcon"));
            this.searchEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.searchEditText.setBackgroundDrawable(null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(NUM);
            if (type == 0) {
                this.searchEditText.setHint(LocaleController.getString("SearchStickersHint", NUM));
            } else if (type == 1) {
                this.searchEditText.setHint(LocaleController.getString("SearchEmojiHint", NUM));
            } else if (type == 2) {
                this.searchEditText.setHint(LocaleController.getString("SearchGifsTitle", NUM));
            }
            this.searchEditText.setCursorColor(Theme.getColor("featuredStickers_addedIcon"));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 12.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher(EmojiView.this) {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    boolean show;
                    boolean showed;
                    float f = 1.0f;
                    if (SearchField.this.searchEditText.length() > 0) {
                        show = true;
                    } else {
                        show = false;
                    }
                    if (SearchField.this.clearSearchImageView.getAlpha() != 0.0f) {
                        showed = true;
                    } else {
                        showed = false;
                    }
                    if (show != showed) {
                        float f2;
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        if (show) {
                            f2 = 1.0f;
                        } else {
                            f2 = 0.0f;
                        }
                        ViewPropertyAnimator duration = animate.alpha(f2).setDuration(150);
                        if (show) {
                            f2 = 1.0f;
                        } else {
                            f2 = 0.1f;
                        }
                        ViewPropertyAnimator scaleX = duration.scaleX(f2);
                        if (!show) {
                            f = 0.1f;
                        }
                        scaleX.scaleY(f).start();
                    }
                    if (type == 0) {
                        EmojiView.this.stickersSearchGridAdapter.search(SearchField.this.searchEditText.getText().toString());
                    } else if (type == 1) {
                        EmojiView.this.emojiSearchAdapter.search(SearchField.this.searchEditText.getText().toString());
                    } else if (type == 2) {
                        EmojiView.this.gifSearchAdapter.search(SearchField.this.searchEditText.getText().toString());
                    }
                }
            });
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$0$EmojiView$SearchField(View v) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }

        private void showShadow(boolean show, boolean animated) {
            Object obj = null;
            float f = 1.0f;
            if (!show || this.shadowView.getTag() != null) {
                if (show || this.shadowView.getTag() == null) {
                    if (this.shadowAnimator != null) {
                        this.shadowAnimator.cancel();
                        this.shadowAnimator = null;
                    }
                    View view = this.shadowView;
                    if (!show) {
                        obj = Integer.valueOf(1);
                    }
                    view.setTag(obj);
                    if (animated) {
                        float f2;
                        this.shadowAnimator = new AnimatorSet();
                        AnimatorSet animatorSet = this.shadowAnimator;
                        Animator[] animatorArr = new Animator[1];
                        View view2 = this.shadowView;
                        Property property = View.ALPHA;
                        float[] fArr = new float[1];
                        if (show) {
                            f2 = 1.0f;
                        } else {
                            f2 = 0.0f;
                        }
                        fArr[0] = f2;
                        animatorArr[0] = ObjectAnimator.ofFloat(view2, property, fArr);
                        animatorSet.playTogether(animatorArr);
                        this.shadowAnimator.setDuration(200);
                        this.shadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                        this.shadowAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                SearchField.this.shadowAnimator = null;
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

        public StickersGridAdapter(Context context) {
            this.context = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public int getItemCount() {
            return this.totalItems != 0 ? this.totalItems + 1 : 0;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public int getPositionForPack(Object pack) {
            Integer pos = (Integer) this.packStartPosition.get(pack);
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
            if (object instanceof Document) {
                return 0;
            }
            if (object instanceof String) {
                return 3;
            }
            return 2;
        }

        public int getTabForPosition(int position) {
            if (position == 0) {
                position = 1;
            }
            if (this.stickersPerRow == 0) {
                int width = EmojiView.this.getMeasuredWidth();
                if (width == 0) {
                    width = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
            }
            int row = this.positionToRow.get(position, Integer.MIN_VALUE);
            if (row == Integer.MIN_VALUE) {
                return (EmojiView.this.stickerSets.size() - 1) + EmojiView.this.stickersTabOffset;
            }
            TL_messages_stickerSet pack = this.rowStartPack.get(row);
            if (!(pack instanceof String)) {
                return EmojiView.this.stickersTabOffset + EmojiView.this.stickerSets.indexOf(pack);
            } else if ("recent".equals(pack)) {
                return EmojiView.this.recentTabBum;
            } else {
                return EmojiView.this.favTabBum;
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new StickerEmojiCell(this.context) {
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                        }
                    };
                    break;
                case 1:
                    view = new EmptyCell(this.context);
                    break;
                case 2:
                    view = new StickerSetNameCell(this.context, false);
                    ((StickerSetNameCell) view).setOnIconClickListener(new EmojiView$StickersGridAdapter$$Lambda$0(this));
                    break;
                case 3:
                    view = new StickerSetGroupInfoCell(this.context);
                    ((StickerSetGroupInfoCell) view).setAddOnClickListener(new EmojiView$StickersGridAdapter$$Lambda$1(this));
                    view.setLayoutParams(new LayoutParams(-1, -2));
                    break;
                case 4:
                    view = new View(this.context);
                    view.setLayoutParams(new LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    break;
            }
            return new Holder(view);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onCreateViewHolder$0$EmojiView$StickersGridAdapter(View v) {
            if (EmojiView.this.groupStickerSet == null) {
                MessagesController.getEmojiSettings(EmojiView.this.currentAccount).edit().putLong("group_hide_stickers_" + EmojiView.this.info.id, EmojiView.this.info.stickerset != null ? EmojiView.this.info.stickerset.id : 0).commit();
                EmojiView.this.updateStickerTabs();
                if (EmojiView.this.stickersGridAdapter != null) {
                    EmojiView.this.stickersGridAdapter.notifyDataSetChanged();
                }
            } else if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.onStickersGroupClick(EmojiView.this.info.id);
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onCreateViewHolder$1$EmojiView$StickersGridAdapter(View v) {
            if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.onStickersGroupClick(EmojiView.this.info.id);
            }
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    Document sticker = (Document) this.cache.get(position);
                    StickerEmojiCell cell = holder.itemView;
                    cell.setSticker(sticker, this.cacheParents.get(position), false);
                    boolean z = EmojiView.this.recentStickers.contains(sticker) || EmojiView.this.favouriteStickers.contains(sticker);
                    cell.setRecent(z);
                    return;
                case 1:
                    EmptyCell cell2 = holder.itemView;
                    if (position == this.totalItems) {
                        int row = this.positionToRow.get(position - 1, Integer.MIN_VALUE);
                        if (row == Integer.MIN_VALUE) {
                            cell2.setHeight(1);
                            return;
                        }
                        ArrayList<Document> documents;
                        Object pack = this.rowStartPack.get(row);
                        if (pack instanceof TL_messages_stickerSet) {
                            documents = ((TL_messages_stickerSet) pack).documents;
                        } else if (!(pack instanceof String)) {
                            documents = null;
                        } else if ("recent".equals(pack)) {
                            documents = EmojiView.this.recentStickers;
                        } else {
                            documents = EmojiView.this.favouriteStickers;
                        }
                        if (documents == null) {
                            cell2.setHeight(1);
                            return;
                        } else if (documents.isEmpty()) {
                            cell2.setHeight(AndroidUtilities.dp(8.0f));
                            return;
                        } else {
                            int height = EmojiView.this.pager.getHeight() - (((int) Math.ceil((double) (((float) documents.size()) / ((float) this.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                            if (height <= 0) {
                                height = 1;
                            }
                            cell2.setHeight(height);
                            return;
                        }
                    }
                    cell2.setHeight(AndroidUtilities.dp(82.0f));
                    return;
                case 2:
                    StickerSetNameCell cell3 = holder.itemView;
                    if (position == EmojiView.this.groupStickerPackPosition) {
                        int icon = (EmojiView.this.groupStickersHidden && EmojiView.this.groupStickerSet == null) ? 0 : EmojiView.this.groupStickerSet != null ? NUM : NUM;
                        Chat chat = EmojiView.this.info != null ? MessagesController.getInstance(EmojiView.this.currentAccount).getChat(Integer.valueOf(EmojiView.this.info.id)) : null;
                        String str = "CurrentGroupStickers";
                        Object[] objArr = new Object[1];
                        objArr[0] = chat != null ? chat.title : "Group Stickers";
                        cell3.setText(LocaleController.formatString(str, NUM, objArr), icon);
                        return;
                    }
                    TL_messages_stickerSet object = this.cache.get(position);
                    if (object instanceof TL_messages_stickerSet) {
                        TL_messages_stickerSet set = object;
                        if (set.set != null) {
                            cell3.setText(set.set.title, 0);
                            return;
                        }
                        return;
                    } else if (object == EmojiView.this.recentStickers) {
                        cell3.setText(LocaleController.getString("RecentStickers", NUM), 0);
                        return;
                    } else if (object == EmojiView.this.favouriteStickers) {
                        cell3.setText(LocaleController.getString("FavoriteStickers", NUM), 0);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    holder.itemView.setIsLast(position == this.totalItems + -1);
                    return;
                default:
                    return;
            }
        }

        public void notifyDataSetChanged() {
            int width = EmojiView.this.getMeasuredWidth();
            if (width == 0) {
                width = AndroidUtilities.displaySize.x;
            }
            this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
            EmojiView.this.stickersLayoutManager.setSpanCount(this.stickersPerRow);
            this.rowStartPack.clear();
            this.packStartPosition.clear();
            this.positionToRow.clear();
            this.cache.clear();
            this.totalItems = 0;
            ArrayList<TL_messages_stickerSet> packs = EmojiView.this.stickerSets;
            int startRow = 0;
            int a = -3;
            while (a < packs.size()) {
                TL_messages_stickerSet pack = null;
                SparseArray sparseArray;
                int i;
                if (a == -3) {
                    sparseArray = this.cache;
                    i = this.totalItems;
                    this.totalItems = i + 1;
                    sparseArray.put(i, "search");
                    startRow++;
                } else {
                    ArrayList<Document> documents;
                    String key;
                    if (a == -2) {
                        documents = EmojiView.this.favouriteStickers;
                        key = "fav";
                        this.packStartPosition.put(key, Integer.valueOf(this.totalItems));
                    } else if (a == -1) {
                        documents = EmojiView.this.recentStickers;
                        key = "recent";
                        this.packStartPosition.put(key, Integer.valueOf(this.totalItems));
                    } else {
                        key = null;
                        pack = (TL_messages_stickerSet) packs.get(a);
                        documents = pack.documents;
                        this.packStartPosition.put(pack, Integer.valueOf(this.totalItems));
                    }
                    if (a == EmojiView.this.groupStickerPackNum) {
                        EmojiView.this.groupStickerPackPosition = this.totalItems;
                        if (documents.isEmpty()) {
                            this.rowStartPack.put(startRow, pack);
                            int startRow2 = startRow + 1;
                            this.positionToRow.put(this.totalItems, startRow);
                            this.rowStartPack.put(startRow2, pack);
                            startRow = startRow2 + 1;
                            this.positionToRow.put(this.totalItems + 1, startRow2);
                            sparseArray = this.cache;
                            i = this.totalItems;
                            this.totalItems = i + 1;
                            sparseArray.put(i, pack);
                            sparseArray = this.cache;
                            i = this.totalItems;
                            this.totalItems = i + 1;
                            sparseArray.put(i, "group");
                        }
                    }
                    if (!documents.isEmpty()) {
                        int b;
                        int count = (int) Math.ceil((double) (((float) documents.size()) / ((float) this.stickersPerRow)));
                        if (pack != null) {
                            this.cache.put(this.totalItems, pack);
                        } else {
                            this.cache.put(this.totalItems, documents);
                        }
                        this.positionToRow.put(this.totalItems, startRow);
                        for (b = 0; b < documents.size(); b++) {
                            int num = (b + 1) + this.totalItems;
                            this.cache.put(num, documents.get(b));
                            if (pack != null) {
                                this.cacheParents.put(num, pack);
                            } else {
                                this.cacheParents.put(num, key);
                            }
                            this.positionToRow.put((b + 1) + this.totalItems, (startRow + 1) + (b / this.stickersPerRow));
                        }
                        for (b = 0; b < count + 1; b++) {
                            if (pack != null) {
                                this.rowStartPack.put(startRow + b, pack);
                            } else {
                                this.rowStartPack.put(startRow + b, a == -1 ? "recent" : "fav");
                            }
                        }
                        this.totalItems += (this.stickersPerRow * count) + 1;
                        startRow += count + 1;
                    }
                }
                a++;
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
                if (!StickersSearchGridAdapter.this.cleared) {
                    StickersSearchGridAdapter.this.cleared = true;
                    StickersSearchGridAdapter.this.emojiStickers.clear();
                    StickersSearchGridAdapter.this.emojiArrays.clear();
                    StickersSearchGridAdapter.this.localPacks.clear();
                    StickersSearchGridAdapter.this.serverPacks.clear();
                    StickersSearchGridAdapter.this.localPacksByShortName.clear();
                    StickersSearchGridAdapter.this.localPacksByName.clear();
                }
            }

            public void run() {
                if (!TextUtils.isEmpty(StickersSearchGridAdapter.this.searchQuery)) {
                    int a;
                    int size;
                    TL_messages_stickerSet set;
                    int index;
                    EmojiView.this.stickersSearchField.progressDrawable.startAnimation();
                    StickersSearchGridAdapter.this.cleared = false;
                    final int lastId = StickersSearchGridAdapter.access$13104(StickersSearchGridAdapter.this);
                    ArrayList<Document> emojiStickersArray = new ArrayList(0);
                    LongSparseArray<Document> emojiStickersMap = new LongSparseArray(0);
                    final HashMap<String, ArrayList<Document>> allStickers = DataQuery.getInstance(EmojiView.this.currentAccount).getAllStickers();
                    if (StickersSearchGridAdapter.this.searchQuery.length() <= 14) {
                        CharSequence emoji = StickersSearchGridAdapter.this.searchQuery;
                        int length = emoji.length();
                        a = 0;
                        while (a < length) {
                            CharSequence[] charSequenceArr;
                            if (a < length - 1 && ((emoji.charAt(a) == 55356 && emoji.charAt(a + 1) >= 57339 && emoji.charAt(a + 1) <= 57343) || (emoji.charAt(a) == 8205 && (emoji.charAt(a + 1) == 9792 || emoji.charAt(a + 1) == 9794)))) {
                                charSequenceArr = new CharSequence[2];
                                charSequenceArr[0] = emoji.subSequence(0, a);
                                charSequenceArr[1] = emoji.subSequence(a + 2, emoji.length());
                                emoji = TextUtils.concat(charSequenceArr);
                                length -= 2;
                                a--;
                            } else if (emoji.charAt(a) == 65039) {
                                charSequenceArr = new CharSequence[2];
                                charSequenceArr[0] = emoji.subSequence(0, a);
                                charSequenceArr[1] = emoji.subSequence(a + 1, emoji.length());
                                emoji = TextUtils.concat(charSequenceArr);
                                length--;
                                a--;
                            }
                            a++;
                        }
                        ArrayList<Document> newStickers = allStickers != null ? (ArrayList) allStickers.get(emoji.toString()) : null;
                        if (!(newStickers == null || newStickers.isEmpty())) {
                            clear();
                            emojiStickersArray.addAll(newStickers);
                            size = newStickers.size();
                            for (a = 0; a < size; a++) {
                                Document document = (Document) newStickers.get(a);
                                emojiStickersMap.put(document.id, document);
                            }
                            StickersSearchGridAdapter.this.emojiStickers.put(emojiStickersArray, StickersSearchGridAdapter.this.searchQuery);
                            StickersSearchGridAdapter.this.emojiArrays.add(emojiStickersArray);
                        }
                    }
                    if (!(allStickers == null || allStickers.isEmpty() || StickersSearchGridAdapter.this.searchQuery.length() <= 1)) {
                        String newLanguage = DataQuery.getInstance(EmojiView.this.currentAccount).getCurrentKeyboardLanguage();
                        if (!(TextUtils.isEmpty(EmojiView.this.lastSearchKeyboardLanguage) || EmojiView.this.lastSearchKeyboardLanguage.equals(newLanguage))) {
                            DataQuery.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(newLanguage);
                        }
                        EmojiView.this.lastSearchKeyboardLanguage = newLanguage;
                        DataQuery.getInstance(EmojiView.this.currentAccount).getEmojiSuggestions(EmojiView.this.lastSearchKeyboardLanguage, StickersSearchGridAdapter.this.searchQuery, false, new KeywordResultCallback() {
                            public void run(ArrayList<KeywordResult> param, String alias) {
                                if (lastId == StickersSearchGridAdapter.this.emojiSearchId) {
                                    boolean added = false;
                                    int size = param.size();
                                    for (int a = 0; a < size; a++) {
                                        String emoji = ((KeywordResult) param.get(a)).emoji;
                                        ArrayList<Document> newStickers = allStickers != null ? (ArrayList) allStickers.get(emoji) : null;
                                        if (!(newStickers == null || newStickers.isEmpty())) {
                                            AnonymousClass1.this.clear();
                                            if (!StickersSearchGridAdapter.this.emojiStickers.containsKey(newStickers)) {
                                                StickersSearchGridAdapter.this.emojiStickers.put(newStickers, emoji);
                                                StickersSearchGridAdapter.this.emojiArrays.add(newStickers);
                                                added = true;
                                            }
                                        }
                                    }
                                    if (added) {
                                        StickersSearchGridAdapter.this.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                    }
                    ArrayList<TL_messages_stickerSet> local = DataQuery.getInstance(EmojiView.this.currentAccount).getStickerSets(0);
                    size = local.size();
                    for (a = 0; a < size; a++) {
                        set = (TL_messages_stickerSet) local.get(a);
                        index = set.set.title.toLowerCase().indexOf(StickersSearchGridAdapter.this.searchQuery);
                        if (index >= 0) {
                            if (index == 0 || set.set.title.charAt(index - 1) == ' ') {
                                clear();
                                StickersSearchGridAdapter.this.localPacks.add(set);
                                StickersSearchGridAdapter.this.localPacksByName.put(set, Integer.valueOf(index));
                            }
                        } else if (set.set.short_name != null) {
                            index = set.set.short_name.toLowerCase().indexOf(StickersSearchGridAdapter.this.searchQuery);
                            if (index >= 0 && (index == 0 || set.set.short_name.charAt(index - 1) == ' ')) {
                                clear();
                                StickersSearchGridAdapter.this.localPacks.add(set);
                                StickersSearchGridAdapter.this.localPacksByShortName.put(set, Boolean.valueOf(true));
                            }
                        }
                    }
                    local = DataQuery.getInstance(EmojiView.this.currentAccount).getStickerSets(3);
                    size = local.size();
                    for (a = 0; a < size; a++) {
                        set = (TL_messages_stickerSet) local.get(a);
                        index = set.set.title.toLowerCase().indexOf(StickersSearchGridAdapter.this.searchQuery);
                        if (index >= 0) {
                            if (index == 0 || set.set.title.charAt(index - 1) == ' ') {
                                clear();
                                StickersSearchGridAdapter.this.localPacks.add(set);
                                StickersSearchGridAdapter.this.localPacksByName.put(set, Integer.valueOf(index));
                            }
                        } else if (set.set.short_name != null) {
                            index = set.set.short_name.toLowerCase().indexOf(StickersSearchGridAdapter.this.searchQuery);
                            if (index >= 0 && (index == 0 || set.set.short_name.charAt(index - 1) == ' ')) {
                                clear();
                                StickersSearchGridAdapter.this.localPacks.add(set);
                                StickersSearchGridAdapter.this.localPacksByShortName.put(set, Boolean.valueOf(true));
                            }
                        }
                    }
                    if (!((StickersSearchGridAdapter.this.localPacks.isEmpty() && StickersSearchGridAdapter.this.emojiStickers.isEmpty()) || EmojiView.this.stickersGridView.getAdapter() == EmojiView.this.stickersSearchGridAdapter)) {
                        EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersSearchGridAdapter);
                    }
                    TLObject req = new TL_messages_searchStickerSets();
                    req.q = StickersSearchGridAdapter.this.searchQuery;
                    StickersSearchGridAdapter.this.reqId = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(req, new EmojiView$StickersSearchGridAdapter$1$$Lambda$0(this, req));
                    if (Emoji.isValidEmoji(StickersSearchGridAdapter.this.searchQuery)) {
                        TLObject req2 = new TL_messages_getStickers();
                        req2.emoticon = StickersSearchGridAdapter.this.searchQuery;
                        req2.hash = 0;
                        StickersSearchGridAdapter.this.reqId2 = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(req2, new EmojiView$StickersSearchGridAdapter$1$$Lambda$1(this, req2, emojiStickersArray, emojiStickersMap));
                    }
                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                }
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$run$1$EmojiView$StickersSearchGridAdapter$1(TL_messages_searchStickerSets req, TLObject response, TL_error error) {
                if (response instanceof TL_messages_foundStickerSets) {
                    AndroidUtilities.runOnUIThread(new EmojiView$StickersSearchGridAdapter$1$$Lambda$3(this, req, response));
                }
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$null$0$EmojiView$StickersSearchGridAdapter$1(TL_messages_searchStickerSets req, TLObject response) {
                if (req.q.equals(StickersSearchGridAdapter.this.searchQuery)) {
                    clear();
                    EmojiView.this.stickersSearchField.progressDrawable.stopAnimation();
                    StickersSearchGridAdapter.this.reqId = 0;
                    if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersSearchGridAdapter) {
                        EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersSearchGridAdapter);
                    }
                    StickersSearchGridAdapter.this.serverPacks.addAll(((TL_messages_foundStickerSets) response).sets);
                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                }
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$run$3$EmojiView$StickersSearchGridAdapter$1(TL_messages_getStickers req2, ArrayList emojiStickersArray, LongSparseArray emojiStickersMap, TLObject response, TL_error error) {
                AndroidUtilities.runOnUIThread(new EmojiView$StickersSearchGridAdapter$1$$Lambda$2(this, req2, response, emojiStickersArray, emojiStickersMap));
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$null$2$EmojiView$StickersSearchGridAdapter$1(TL_messages_getStickers req2, TLObject response, ArrayList emojiStickersArray, LongSparseArray emojiStickersMap) {
                if (req2.emoticon.equals(StickersSearchGridAdapter.this.searchQuery)) {
                    StickersSearchGridAdapter.this.reqId2 = 0;
                    if (response instanceof TL_messages_stickers) {
                        TL_messages_stickers res = (TL_messages_stickers) response;
                        int oldCount = emojiStickersArray.size();
                        int size = res.stickers.size();
                        for (int a = 0; a < size; a++) {
                            Document document = (Document) res.stickers.get(a);
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
                    }
                }
            }
        };
        private ArrayList<StickerSetCovered> serverPacks = new ArrayList();
        private int totalItems;

        static /* synthetic */ int access$13104(StickersSearchGridAdapter x0) {
            int i = x0.emojiSearchId + 1;
            x0.emojiSearchId = i;
            return i;
        }

        public StickersSearchGridAdapter(Context context) {
            this.context = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public int getItemCount() {
            if (this.totalItems != 1) {
                return this.totalItems + 1;
            }
            return 2;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public void search(String text) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            if (this.reqId2 != 0) {
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId2, true);
                this.reqId2 = 0;
            }
            if (TextUtils.isEmpty(text)) {
                this.searchQuery = null;
                this.localPacks.clear();
                this.emojiStickers.clear();
                this.serverPacks.clear();
                if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersGridAdapter) {
                    EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersGridAdapter);
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
            if (object instanceof Document) {
                return 0;
            }
            if (object instanceof StickerSetCovered) {
                return 3;
            }
            return 2;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new StickerEmojiCell(this.context) {
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                        }
                    };
                    break;
                case 1:
                    view = new EmptyCell(this.context);
                    break;
                case 2:
                    view = new StickerSetNameCell(this.context, false);
                    break;
                case 3:
                    view = new FeaturedStickerSetInfoCell(this.context, 17);
                    ((FeaturedStickerSetInfoCell) view).setAddOnClickListener(new EmojiView$StickersSearchGridAdapter$$Lambda$0(this));
                    break;
                case 4:
                    view = new View(this.context);
                    view.setLayoutParams(new LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    break;
                case 5:
                    View frameLayout = new FrameLayout(this.context) {
                        /* Access modifiers changed, original: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((int) (((float) (((EmojiView.this.stickersGridView.getMeasuredHeight() - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3)) * 1.7f), NUM));
                        }
                    };
                    ImageView imageView = new ImageView(this.context);
                    imageView.setScaleType(ScaleType.CENTER);
                    imageView.setImageResource(NUM);
                    imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelEmptyText"), Mode.MULTIPLY));
                    frameLayout.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 59.0f));
                    TextView textView = new TextView(this.context);
                    textView.setText(LocaleController.getString("NoStickersFound", NUM));
                    textView.setTextSize(1, 16.0f);
                    textView.setTextColor(Theme.getColor("chat_emojiPanelEmptyText"));
                    frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 9.0f));
                    view = frameLayout;
                    view.setLayoutParams(new LayoutParams(-1, -2));
                    break;
            }
            return new Holder(view);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onCreateViewHolder$0$EmojiView$StickersSearchGridAdapter(View v) {
            FeaturedStickerSetInfoCell parent1 = (FeaturedStickerSetInfoCell) v.getParent();
            StickerSetCovered pack = parent1.getStickerSet();
            if (EmojiView.this.installingStickerSets.indexOfKey(pack.set.id) < 0 && EmojiView.this.removingStickerSets.indexOfKey(pack.set.id) < 0) {
                if (parent1.isInstalled()) {
                    EmojiView.this.removingStickerSets.put(pack.set.id, pack);
                    EmojiView.this.delegate.onStickerSetRemove(parent1.getStickerSet());
                } else {
                    EmojiView.this.installingStickerSets.put(pack.set.id, pack);
                    EmojiView.this.delegate.onStickerSetAdd(parent1.getStickerSet());
                }
                parent1.setDrawProgress(true);
            }
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z;
            switch (holder.getItemViewType()) {
                case 0:
                    Document sticker = (Document) this.cache.get(position);
                    StickerEmojiCell cell = holder.itemView;
                    cell.setSticker(sticker, this.cacheParent.get(position), (String) this.positionToEmoji.get(position), false);
                    z = EmojiView.this.recentStickers.contains(sticker) || EmojiView.this.favouriteStickers.contains(sticker);
                    cell.setRecent(z);
                    return;
                case 1:
                    EmptyCell cell2 = holder.itemView;
                    if (position == this.totalItems) {
                        int row = this.positionToRow.get(position - 1, Integer.MIN_VALUE);
                        if (row == Integer.MIN_VALUE) {
                            cell2.setHeight(1);
                            return;
                        }
                        Integer count;
                        Object pack = this.rowStartPack.get(row);
                        if (pack instanceof TL_messages_stickerSet) {
                            count = Integer.valueOf(((TL_messages_stickerSet) pack).documents.size());
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
                            int height = EmojiView.this.pager.getHeight() - (((int) Math.ceil((double) (((float) count.intValue()) / ((float) EmojiView.this.stickersGridAdapter.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                            if (height <= 0) {
                                height = 1;
                            }
                            cell2.setHeight(height);
                            return;
                        }
                    }
                    cell2.setHeight(AndroidUtilities.dp(82.0f));
                    return;
                case 2:
                    StickerSetNameCell cell3 = holder.itemView;
                    TL_messages_stickerSet object = this.cache.get(position);
                    if (object instanceof TL_messages_stickerSet) {
                        TL_messages_stickerSet set = object;
                        if (TextUtils.isEmpty(this.searchQuery) || !this.localPacksByShortName.containsKey(set)) {
                            Integer start = (Integer) this.localPacksByName.get(set);
                            if (!(set.set == null || start == null)) {
                                cell3.setText(set.set.title, 0, start.intValue(), !TextUtils.isEmpty(this.searchQuery) ? this.searchQuery.length() : 0);
                            }
                            cell3.setUrl(null, 0);
                            return;
                        }
                        if (set.set != null) {
                            cell3.setText(set.set.title, 0);
                        }
                        cell3.setUrl(set.set.short_name, this.searchQuery.length());
                        return;
                    }
                    return;
                case 3:
                    StickerSetCovered stickerSetCovered = (StickerSetCovered) this.cache.get(position);
                    FeaturedStickerSetInfoCell cell4 = holder.itemView;
                    boolean installing = EmojiView.this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                    boolean removing = EmojiView.this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                    if (installing || removing) {
                        if (installing && cell4.isInstalled()) {
                            EmojiView.this.installingStickerSets.remove(stickerSetCovered.set.id);
                            installing = false;
                        } else if (removing && !cell4.isInstalled()) {
                            EmojiView.this.removingStickerSets.remove(stickerSetCovered.set.id);
                            removing = false;
                        }
                    }
                    z = installing || removing;
                    cell4.setDrawProgress(z);
                    int idx = TextUtils.isEmpty(this.searchQuery) ? -1 : stickerSetCovered.set.title.toLowerCase().indexOf(this.searchQuery);
                    if (idx >= 0) {
                        cell4.setStickerSet(stickerSetCovered, false, idx, this.searchQuery.length());
                        return;
                    }
                    cell4.setStickerSet(stickerSetCovered, false);
                    if (!TextUtils.isEmpty(this.searchQuery) && stickerSetCovered.set.short_name.toLowerCase().startsWith(this.searchQuery)) {
                        cell4.setUrl(stickerSetCovered.set.short_name, this.searchQuery.length());
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void notifyDataSetChanged() {
            this.rowStartPack.clear();
            this.positionToRow.clear();
            this.cache.clear();
            this.positionsToSets.clear();
            this.positionToEmoji.clear();
            this.totalItems = 0;
            int startRow = 0;
            int a = -1;
            int serverCount = this.serverPacks.size();
            int localCount = this.localPacks.size();
            int emojiCount = this.emojiArrays.isEmpty() ? 0 : 1;
            while (a < (serverCount + localCount) + emojiCount) {
                if (a == -1) {
                    SparseArray sparseArray = this.cache;
                    int i = this.totalItems;
                    this.totalItems = i + 1;
                    sparseArray.put(i, "search");
                    startRow++;
                } else {
                    ArrayList<Document> documents;
                    Object pack;
                    int N;
                    int size;
                    int b;
                    int num;
                    int row;
                    int count;
                    int idx = a;
                    if (idx < localCount) {
                        TL_messages_stickerSet set = (TL_messages_stickerSet) this.localPacks.get(idx);
                        documents = set.documents;
                        pack = set;
                    } else {
                        idx -= localCount;
                        if (idx < emojiCount) {
                            int documentsCount = 0;
                            String lastEmoji = "";
                            N = this.emojiArrays.size();
                            for (int i2 = 0; i2 < N; i2++) {
                                documents = (ArrayList) this.emojiArrays.get(i2);
                                String emoji = (String) this.emojiStickers.get(documents);
                                if (!(emoji == null || lastEmoji.equals(emoji))) {
                                    lastEmoji = emoji;
                                    this.positionToEmoji.put(this.totalItems + documentsCount, lastEmoji);
                                }
                                size = documents.size();
                                for (b = 0; b < size; b++) {
                                    num = documentsCount + this.totalItems;
                                    row = startRow + (documentsCount / EmojiView.this.stickersGridAdapter.stickersPerRow);
                                    Document document = (Document) documents.get(b);
                                    this.cache.put(num, document);
                                    TL_messages_stickerSet parent = DataQuery.getInstance(EmojiView.this.currentAccount).getStickerSetById(DataQuery.getStickerSetId(document));
                                    if (parent != null) {
                                        this.cacheParent.put(num, parent);
                                    }
                                    this.positionToRow.put(num, row);
                                    if (a >= localCount && (null instanceof StickerSetCovered)) {
                                        this.positionsToSets.put(num, (StickerSetCovered) null);
                                    }
                                    documentsCount++;
                                }
                            }
                            count = (int) Math.ceil((double) (((float) documentsCount) / ((float) EmojiView.this.stickersGridAdapter.stickersPerRow)));
                            N = count;
                            for (b = 0; b < N; b++) {
                                this.rowStartPack.put(startRow + b, Integer.valueOf(documentsCount));
                            }
                            this.totalItems += EmojiView.this.stickersGridAdapter.stickersPerRow * count;
                            startRow += count;
                        } else {
                            StickerSetCovered set2 = (StickerSetCovered) this.serverPacks.get(idx - emojiCount);
                            documents = set2.covers;
                            StickerSetCovered pack2 = set2;
                        }
                    }
                    if (!documents.isEmpty()) {
                        count = (int) Math.ceil((double) (((float) documents.size()) / ((float) EmojiView.this.stickersGridAdapter.stickersPerRow)));
                        this.cache.put(this.totalItems, pack2);
                        if (a >= localCount && (pack2 instanceof StickerSetCovered)) {
                            this.positionsToSets.put(this.totalItems, (StickerSetCovered) pack2);
                        }
                        this.positionToRow.put(this.totalItems, startRow);
                        size = documents.size();
                        for (b = 0; b < size; b++) {
                            num = (b + 1) + this.totalItems;
                            row = (startRow + 1) + (b / EmojiView.this.stickersGridAdapter.stickersPerRow);
                            this.cache.put(num, (Document) documents.get(b));
                            if (pack2 != null) {
                                this.cacheParent.put(num, pack2);
                            }
                            this.positionToRow.put(num, row);
                            if (a >= localCount && (pack2 instanceof StickerSetCovered)) {
                                this.positionsToSets.put(num, (StickerSetCovered) pack2);
                            }
                        }
                        N = count + 1;
                        for (b = 0; b < N; b++) {
                            this.rowStartPack.put(startRow + b, pack2);
                        }
                        this.totalItems += (EmojiView.this.stickersGridAdapter.stickersPerRow * count) + 1;
                        startRow += count + 1;
                    }
                }
                a++;
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

        public TrendingGridAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            return this.totalItems;
        }

        public Object getItem(int i) {
            return this.cache.get(i);
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public int getItemViewType(int position) {
            Object object = this.cache.get(position);
            if (object == null) {
                return 1;
            }
            if (object instanceof Document) {
                return 0;
            }
            return 2;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new StickerEmojiCell(this.context) {
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
                        }
                    };
                    break;
                case 1:
                    view = new EmptyCell(this.context);
                    break;
                case 2:
                    view = new FeaturedStickerSetInfoCell(this.context, 17);
                    ((FeaturedStickerSetInfoCell) view).setAddOnClickListener(new EmojiView$TrendingGridAdapter$$Lambda$0(this));
                    break;
            }
            return new Holder(view);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onCreateViewHolder$0$EmojiView$TrendingGridAdapter(View v) {
            FeaturedStickerSetInfoCell parent1 = (FeaturedStickerSetInfoCell) v.getParent();
            StickerSetCovered pack = parent1.getStickerSet();
            if (EmojiView.this.installingStickerSets.indexOfKey(pack.set.id) < 0 && EmojiView.this.removingStickerSets.indexOfKey(pack.set.id) < 0) {
                if (parent1.isInstalled()) {
                    EmojiView.this.removingStickerSets.put(pack.set.id, pack);
                    EmojiView.this.delegate.onStickerSetRemove(parent1.getStickerSet());
                } else {
                    EmojiView.this.installingStickerSets.put(pack.set.id, pack);
                    EmojiView.this.delegate.onStickerSetAdd(parent1.getStickerSet());
                }
                parent1.setDrawProgress(true);
            }
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    ((StickerEmojiCell) holder.itemView).setSticker((Document) this.cache.get(position), this.positionsToSets.get(position), false);
                    return;
                case 1:
                    ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                    return;
                case 2:
                    boolean unread;
                    boolean installing;
                    boolean removing;
                    ArrayList<Long> unreadStickers = DataQuery.getInstance(EmojiView.this.currentAccount).getUnreadStickerSets();
                    StickerSetCovered stickerSetCovered = (StickerSetCovered) this.sets.get(((Integer) this.cache.get(position)).intValue());
                    if (unreadStickers == null || !unreadStickers.contains(Long.valueOf(stickerSetCovered.set.id))) {
                        unread = false;
                    } else {
                        unread = true;
                    }
                    FeaturedStickerSetInfoCell cell = holder.itemView;
                    cell.setStickerSet(stickerSetCovered, unread);
                    if (unread) {
                        DataQuery.getInstance(EmojiView.this.currentAccount).markFaturedStickersByIdAsRead(stickerSetCovered.set.id);
                    }
                    if (EmojiView.this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0) {
                        installing = true;
                    } else {
                        installing = false;
                    }
                    if (EmojiView.this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0) {
                        removing = true;
                    } else {
                        removing = false;
                    }
                    if (installing || removing) {
                        if (installing && cell.isInstalled()) {
                            EmojiView.this.installingStickerSets.remove(stickerSetCovered.set.id);
                            installing = false;
                        } else if (removing && !cell.isInstalled()) {
                            EmojiView.this.removingStickerSets.remove(stickerSetCovered.set.id);
                            removing = false;
                        }
                    }
                    if (installing || removing) {
                        z = true;
                    }
                    cell.setDrawProgress(z);
                    return;
                default:
                    return;
            }
        }

        public void notifyDataSetChanged() {
            int width = EmojiView.this.getMeasuredWidth();
            if (width == 0) {
                if (AndroidUtilities.isTablet()) {
                    int smallSide = AndroidUtilities.displaySize.x;
                    int leftSide = (smallSide * 35) / 100;
                    if (leftSide < AndroidUtilities.dp(320.0f)) {
                        leftSide = AndroidUtilities.dp(320.0f);
                    }
                    width = smallSide - leftSide;
                } else {
                    width = AndroidUtilities.displaySize.x;
                }
                if (width == 0) {
                    width = 1080;
                }
            }
            this.stickersPerRow = Math.max(1, width / AndroidUtilities.dp(72.0f));
            EmojiView.this.trendingLayoutManager.setSpanCount(this.stickersPerRow);
            if (!EmojiView.this.trendingLoaded) {
                this.cache.clear();
                this.positionsToSets.clear();
                this.sets.clear();
                this.totalItems = 0;
                int num = 0;
                ArrayList<StickerSetCovered> packs = DataQuery.getInstance(EmojiView.this.currentAccount).getFeaturedStickerSets();
                for (int a = 0; a < packs.size(); a++) {
                    StickerSetCovered pack = (StickerSetCovered) packs.get(a);
                    if (!(DataQuery.getInstance(EmojiView.this.currentAccount).isStickerPackInstalled(pack.set.id) || (pack.covers.isEmpty() && pack.cover == null))) {
                        int count;
                        int b;
                        this.sets.add(pack);
                        this.positionsToSets.put(this.totalItems, pack);
                        SparseArray sparseArray = this.cache;
                        int i = this.totalItems;
                        this.totalItems = i + 1;
                        int num2 = num + 1;
                        sparseArray.put(i, Integer.valueOf(num));
                        int startRow = this.totalItems / this.stickersPerRow;
                        if (pack.covers.isEmpty()) {
                            count = 1;
                            this.cache.put(this.totalItems, pack.cover);
                        } else {
                            count = (int) Math.ceil((double) (((float) pack.covers.size()) / ((float) this.stickersPerRow)));
                            for (b = 0; b < pack.covers.size(); b++) {
                                this.cache.put(this.totalItems + b, pack.covers.get(b));
                            }
                        }
                        for (b = 0; b < this.stickersPerRow * count; b++) {
                            this.positionsToSets.put(this.totalItems + b, pack);
                        }
                        this.totalItems += this.stickersPerRow * count;
                        num = num2;
                    }
                }
                if (this.totalItems != 0) {
                    EmojiView.this.trendingLoaded = true;
                    EmojiView.this.featuredStickersHash = DataQuery.getInstance(EmojiView.this.currentAccount).getFeaturesStickersHashWithoutUnread();
                }
                super.notifyDataSetChanged();
            }
        }
    }

    static {
        Field f = null;
        try {
            f = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
        }
        superListenerField = f;
    }

    static final /* synthetic */ void lambda$static$0$EmojiView() {
    }

    public EmojiView(boolean needStickers, boolean needGif, Context context, boolean needSearch, ChatFull chatFull) {
        int i;
        super(context);
        this.needEmojiSearch = needSearch;
        Drawable[] drawableArr = new Drawable[3];
        drawableArr[0] = Theme.createEmojiIconSelectorDrawable(context, NUM, Theme.getColor("chat_emojiBottomPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected"));
        drawableArr[1] = Theme.createEmojiIconSelectorDrawable(context, NUM, Theme.getColor("chat_emojiBottomPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected"));
        drawableArr[2] = Theme.createEmojiIconSelectorDrawable(context, NUM, Theme.getColor("chat_emojiBottomPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected"));
        this.tabIcons = drawableArr;
        drawableArr = new Drawable[9];
        drawableArr[0] = Theme.createEmojiIconSelectorDrawable(context, NUM, Theme.getColor("chat_emojiPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected"));
        drawableArr[1] = Theme.createEmojiIconSelectorDrawable(context, NUM, Theme.getColor("chat_emojiPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected"));
        drawableArr[2] = Theme.createEmojiIconSelectorDrawable(context, NUM, Theme.getColor("chat_emojiPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected"));
        drawableArr[3] = Theme.createEmojiIconSelectorDrawable(context, NUM, Theme.getColor("chat_emojiPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected"));
        drawableArr[4] = Theme.createEmojiIconSelectorDrawable(context, NUM, Theme.getColor("chat_emojiPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected"));
        drawableArr[5] = Theme.createEmojiIconSelectorDrawable(context, NUM, Theme.getColor("chat_emojiPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected"));
        drawableArr[6] = Theme.createEmojiIconSelectorDrawable(context, NUM, Theme.getColor("chat_emojiPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected"));
        drawableArr[7] = Theme.createEmojiIconSelectorDrawable(context, NUM, Theme.getColor("chat_emojiPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected"));
        drawableArr[8] = Theme.createEmojiIconSelectorDrawable(context, NUM, Theme.getColor("chat_emojiPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected"));
        this.emojiIcons = drawableArr;
        drawableArr = new Drawable[3];
        drawableArr[0] = Theme.createEmojiIconSelectorDrawable(context, NUM, Theme.getColor("chat_emojiBottomPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected"));
        drawableArr[1] = Theme.createEmojiIconSelectorDrawable(context, NUM, Theme.getColor("chat_emojiBottomPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected"));
        drawableArr[2] = Theme.createEmojiIconSelectorDrawable(context, NUM, Theme.getColor("chat_emojiBottomPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected"));
        this.stickerIcons = drawableArr;
        this.emojiTitles = new String[]{LocaleController.getString("Emoji1", NUM), LocaleController.getString("Emoji2", NUM), LocaleController.getString("Emoji3", NUM), LocaleController.getString("Emoji4", NUM), LocaleController.getString("Emoji5", NUM), LocaleController.getString("Emoji6", NUM), LocaleController.getString("Emoji7", NUM), LocaleController.getString("Emoji8", NUM)};
        this.showGifs = needGif;
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
        this.emojiContainer = new FrameLayout(context);
        this.views.add(this.emojiContainer);
        this.emojiGridView = new RecyclerListView(context) {
            private boolean ignoreLayout;

            /* Access modifiers changed, original: protected */
            public void onMeasure(int widthSpec, int heightSpec) {
                this.ignoreLayout = true;
                EmojiView.this.emojiLayoutManager.setSpanCount(MeasureSpec.getSize(widthSpec) / AndroidUtilities.dp(AndroidUtilities.isTablet() ? 60.0f : 45.0f));
                this.ignoreLayout = false;
                super.onMeasure(widthSpec, heightSpec);
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                if (EmojiView.this.needEmojiSearch && EmojiView.this.firstEmojiAttach) {
                    this.ignoreLayout = true;
                    EmojiView.this.emojiLayoutManager.scrollToPositionWithOffset(1, 0);
                    EmojiView.this.firstEmojiAttach = false;
                    this.ignoreLayout = false;
                }
                super.onLayout(changed, l, t, r, b);
                EmojiView.this.checkEmojiSearchFieldScroll(true);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            public boolean onTouchEvent(MotionEvent event) {
                if (EmojiView.this.emojiTouchedView == null) {
                    EmojiView.this.emojiLastX = event.getX();
                    EmojiView.this.emojiLastY = event.getY();
                    return super.onTouchEvent(event);
                } else if (event.getAction() == 1 || event.getAction() == 3) {
                    if (EmojiView.this.pickerViewPopup != null && EmojiView.this.pickerViewPopup.isShowing()) {
                        EmojiView.this.pickerViewPopup.dismiss();
                        String color = null;
                        switch (EmojiView.this.pickerView.getSelection()) {
                            case 1:
                                color = "🏻";
                                break;
                            case 2:
                                color = "🏼";
                                break;
                            case 3:
                                color = "🏽";
                                break;
                            case 4:
                                color = "🏾";
                                break;
                            case 5:
                                color = "🏿";
                                break;
                        }
                        String code = (String) EmojiView.this.emojiTouchedView.getTag();
                        if (EmojiView.this.emojiTouchedView.isRecent) {
                            code = code.replace("🏻", "").replace("🏼", "").replace("🏽", "").replace("🏾", "").replace("🏿", "");
                            if (color != null) {
                                EmojiView.this.emojiTouchedView.sendEmoji(EmojiView.addColorToCode(code, color));
                            } else {
                                EmojiView.this.emojiTouchedView.sendEmoji(code);
                            }
                        } else {
                            if (color != null) {
                                Emoji.emojiColor.put(code, color);
                                code = EmojiView.addColorToCode(code, color);
                            } else {
                                Emoji.emojiColor.remove(code);
                            }
                            EmojiView.this.emojiTouchedView.setImageDrawable(Emoji.getEmojiBigDrawable(code), EmojiView.this.emojiTouchedView.isRecent);
                            EmojiView.this.emojiTouchedView.sendEmoji(null);
                            Emoji.saveEmojiColors();
                        }
                    }
                    EmojiView.this.emojiTouchedView = null;
                    EmojiView.this.emojiTouchedX = -10000.0f;
                    EmojiView.this.emojiTouchedY = -10000.0f;
                    return true;
                } else if (event.getAction() != 2) {
                    return true;
                } else {
                    boolean ignore = false;
                    if (EmojiView.this.emojiTouchedX != -10000.0f) {
                        if (Math.abs(EmojiView.this.emojiTouchedX - event.getX()) > AndroidUtilities.getPixelsInCM(0.2f, true) || Math.abs(EmojiView.this.emojiTouchedY - event.getY()) > AndroidUtilities.getPixelsInCM(0.2f, false)) {
                            EmojiView.this.emojiTouchedX = -10000.0f;
                            EmojiView.this.emojiTouchedY = -10000.0f;
                        } else {
                            ignore = true;
                        }
                    }
                    if (ignore) {
                        return true;
                    }
                    getLocationOnScreen(EmojiView.this.location);
                    float x = ((float) EmojiView.this.location[0]) + event.getX();
                    EmojiView.this.pickerView.getLocationOnScreen(EmojiView.this.location);
                    int position = (int) ((x - ((float) (EmojiView.this.location[0] + AndroidUtilities.dp(3.0f)))) / ((float) (EmojiView.this.emojiSize + AndroidUtilities.dp(4.0f))));
                    if (position < 0) {
                        position = 0;
                    } else if (position > 5) {
                        position = 5;
                    }
                    EmojiView.this.pickerView.setSelection(position);
                    return true;
                }
            }
        };
        this.emojiGridView.setInstantClick(true);
        RecyclerListView recyclerListView = this.emojiGridView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 8);
        this.emojiLayoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.emojiGridView.setTopGlowOffset(AndroidUtilities.dp(38.0f));
        this.emojiGridView.setBottomGlowOffset(AndroidUtilities.dp(48.0f));
        this.emojiGridView.setPadding(0, AndroidUtilities.dp(38.0f), 0, 0);
        this.emojiGridView.setGlowColor(Theme.getColor("chat_emojiPanelBackground"));
        this.emojiGridView.setClipToPadding(false);
        this.emojiLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            public int getSpanSize(int position) {
                if (EmojiView.this.emojiGridView.getAdapter() == EmojiView.this.emojiSearchAdapter) {
                    if (position == 0 || (position == 1 && EmojiView.this.emojiSearchAdapter.searchWas && EmojiView.this.emojiSearchAdapter.result.isEmpty())) {
                        return EmojiView.this.emojiLayoutManager.getSpanCount();
                    }
                    return 1;
                } else if ((!EmojiView.this.needEmojiSearch || position != 0) && EmojiView.this.emojiAdapter.positionToSection.indexOfKey(position) < 0) {
                    return 1;
                } else {
                    return EmojiView.this.emojiLayoutManager.getSpanCount();
                }
            }
        });
        recyclerListView = this.emojiGridView;
        EmojiGridAdapter emojiGridAdapter = new EmojiGridAdapter(this, null);
        this.emojiAdapter = emojiGridAdapter;
        recyclerListView.setAdapter(emojiGridAdapter);
        this.emojiSearchAdapter = new EmojiSearchAdapter(this, null);
        this.emojiContainer.addView(this.emojiGridView, LayoutHelper.createFrame(-1, -1.0f));
        this.emojiGridView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && EmojiView.this.emojiSearchField != null) {
                    EmojiView.this.emojiSearchField.hideKeyboard();
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int i = 1;
                int position = EmojiView.this.emojiLayoutManager.findFirstVisibleItemPosition();
                if (position != -1) {
                    int i2;
                    int tab = 0;
                    int size = Emoji.recentEmoji.size();
                    if (EmojiView.this.needEmojiSearch) {
                        i2 = 1;
                    } else {
                        i2 = 0;
                    }
                    int count = size + i2;
                    if (position >= count) {
                        int a = 0;
                        while (a < EmojiData.dataColored.length) {
                            int size2 = EmojiData.dataColored[a].length + 1;
                            if (position < count + size2) {
                                if (Emoji.recentEmoji.isEmpty()) {
                                    i = 0;
                                }
                                tab = a + i;
                            } else {
                                count += size2;
                                a++;
                            }
                        }
                    }
                    EmojiView.this.emojiTabs.onPageScrolled(tab, 0);
                }
                EmojiView.this.checkEmojiTabY(recyclerView, dy);
                EmojiView.this.checkEmojiSearchFieldScroll(false);
                EmojiView.this.checkBottomTabScroll((float) dy);
            }
        });
        this.emojiGridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (view instanceof ImageViewEmoji) {
                    ((ImageViewEmoji) view).sendEmoji(null);
                }
            }
        });
        this.emojiGridView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemClick(View view, int position) {
                if (view instanceof ImageViewEmoji) {
                    ImageViewEmoji viewEmoji = (ImageViewEmoji) view;
                    String code = (String) viewEmoji.getTag();
                    String color = null;
                    String toCheck = code.replace("🏻", "");
                    if (toCheck != code) {
                        color = "🏻";
                    }
                    if (color == null) {
                        toCheck = code.replace("🏼", "");
                        if (toCheck != code) {
                            color = "🏼";
                        }
                    }
                    if (color == null) {
                        toCheck = code.replace("🏽", "");
                        if (toCheck != code) {
                            color = "🏽";
                        }
                    }
                    if (color == null) {
                        toCheck = code.replace("🏾", "");
                        if (toCheck != code) {
                            color = "🏾";
                        }
                    }
                    if (color == null) {
                        toCheck = code.replace("🏿", "");
                        if (toCheck != code) {
                            color = "🏿";
                        }
                    }
                    if (EmojiData.emojiColoredMap.containsKey(toCheck)) {
                        EmojiView.this.emojiTouchedView = viewEmoji;
                        EmojiView.this.emojiTouchedX = EmojiView.this.emojiLastX;
                        EmojiView.this.emojiTouchedY = EmojiView.this.emojiLastY;
                        if (color == null && !viewEmoji.isRecent) {
                            color = (String) Emoji.emojiColor.get(toCheck);
                        }
                        if (color != null) {
                            Object obj = -1;
                            switch (color.hashCode()) {
                                case 1773375:
                                    if (color.equals("🏻")) {
                                        obj = null;
                                        break;
                                    }
                                    break;
                                case 1773376:
                                    if (color.equals("🏼")) {
                                        obj = 1;
                                        break;
                                    }
                                    break;
                                case 1773377:
                                    if (color.equals("🏽")) {
                                        obj = 2;
                                        break;
                                    }
                                    break;
                                case 1773378:
                                    if (color.equals("🏾")) {
                                        obj = 3;
                                        break;
                                    }
                                    break;
                                case 1773379:
                                    if (color.equals("🏿")) {
                                        obj = 4;
                                        break;
                                    }
                                    break;
                            }
                            switch (obj) {
                                case null:
                                    EmojiView.this.pickerView.setSelection(1);
                                    break;
                                case 1:
                                    EmojiView.this.pickerView.setSelection(2);
                                    break;
                                case 2:
                                    EmojiView.this.pickerView.setSelection(3);
                                    break;
                                case 3:
                                    EmojiView.this.pickerView.setSelection(4);
                                    break;
                                case 4:
                                    EmojiView.this.pickerView.setSelection(5);
                                    break;
                            }
                        }
                        EmojiView.this.pickerView.setSelection(0);
                        viewEmoji.getLocationOnScreen(EmojiView.this.location);
                        int x = (EmojiView.this.pickerView.getSelection() * EmojiView.this.emojiSize) + AndroidUtilities.dp((float) ((EmojiView.this.pickerView.getSelection() * 4) - (AndroidUtilities.isTablet() ? 5 : 1)));
                        if (EmojiView.this.location[0] - x < AndroidUtilities.dp(5.0f)) {
                            x += (EmojiView.this.location[0] - x) - AndroidUtilities.dp(5.0f);
                        } else if ((EmojiView.this.location[0] - x) + EmojiView.this.popupWidth > AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0f)) {
                            x += ((EmojiView.this.location[0] - x) + EmojiView.this.popupWidth) - (AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0f));
                        }
                        int xOffset = -x;
                        int yOffset = viewEmoji.getTop() < 0 ? viewEmoji.getTop() : 0;
                        EmojiView.this.pickerView.setEmoji(toCheck, (AndroidUtilities.dp(AndroidUtilities.isTablet() ? 30.0f : 22.0f) - xOffset) + ((int) AndroidUtilities.dpf2(0.5f)));
                        EmojiView.this.pickerViewPopup.setFocusable(true);
                        EmojiView.this.pickerViewPopup.showAsDropDown(view, xOffset, (((-view.getMeasuredHeight()) - EmojiView.this.popupHeight) + ((view.getMeasuredHeight() - EmojiView.this.emojiSize) / 2)) - yOffset);
                        EmojiView.this.pager.requestDisallowInterceptTouchEvent(true);
                        EmojiView.this.emojiGridView.hideSelector();
                        return true;
                    } else if (viewEmoji.isRecent) {
                        ViewHolder holder = EmojiView.this.emojiGridView.findContainingViewHolder(view);
                        if (holder != null && holder.getAdapterPosition() <= Emoji.recentEmoji.size()) {
                            EmojiView.this.delegate.onClearEmojiRecent();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        this.emojiTabs = new ScrollSlidingTabStrip(context);
        if (needSearch) {
            this.emojiSearchField = new SearchField(context, 1);
            this.emojiContainer.addView(this.emojiSearchField, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + getShadowHeight()));
            this.emojiSearchField.searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        EmojiView.this.lastSearchKeyboardLanguage = DataQuery.getInstance(EmojiView.this.currentAccount).getCurrentKeyboardLanguage();
                        DataQuery.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(EmojiView.this.lastSearchKeyboardLanguage);
                    }
                }
            });
        }
        this.emojiTabs.setShouldExpand(true);
        this.emojiTabs.setIndicatorHeight(-1);
        this.emojiTabs.setUnderlineHeight(-1);
        this.emojiTabs.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
        this.emojiContainer.addView(this.emojiTabs, LayoutHelper.createFrame(-1, 38.0f));
        this.emojiTabs.setDelegate(new ScrollSlidingTabStripDelegate() {
            public void onPageSelected(int page) {
                if (!Emoji.recentEmoji.isEmpty()) {
                    if (page == 0) {
                        EmojiView.this.emojiLayoutManager.scrollToPositionWithOffset(EmojiView.this.needEmojiSearch ? 1 : 0, 0);
                        return;
                    }
                    page--;
                }
                EmojiView.this.emojiGridView.stopScroll();
                EmojiView.this.emojiLayoutManager.scrollToPositionWithOffset(EmojiView.this.emojiAdapter.sectionToPosition.get(page), 0);
                EmojiView.this.checkEmojiTabY(null, 0);
            }
        });
        this.emojiTabsShadow = new View(context);
        this.emojiTabsShadow.setAlpha(0.0f);
        this.emojiTabsShadow.setTag(Integer.valueOf(1));
        this.emojiTabsShadow.setBackgroundColor(Theme.getColor("chat_emojiPanelShadowLine"));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(38.0f);
        this.emojiContainer.addView(this.emojiTabsShadow, layoutParams);
        if (needStickers) {
            if (needGif) {
                this.gifContainer = new FrameLayout(context);
                this.views.add(this.gifContainer);
                this.gifGridView = new RecyclerListView(context) {
                    private boolean ignoreLayout;

                    public boolean onInterceptTouchEvent(MotionEvent event) {
                        boolean result = ContentPreviewViewer.getInstance().onInterceptTouchEvent(event, EmojiView.this.gifGridView, 0, EmojiView.this.contentPreviewViewerDelegate);
                        if (super.onInterceptTouchEvent(event) || result) {
                            return true;
                        }
                        return false;
                    }

                    /* Access modifiers changed, original: protected */
                    public void onLayout(boolean changed, int l, int t, int r, int b) {
                        if (EmojiView.this.firstGifAttach && EmojiView.this.gifAdapter.getItemCount() > 1) {
                            this.ignoreLayout = true;
                            EmojiView.this.gifLayoutManager.scrollToPositionWithOffset(1, 0);
                            EmojiView.this.firstGifAttach = false;
                            this.ignoreLayout = false;
                        }
                        super.onLayout(changed, l, t, r, b);
                        EmojiView.this.checkGifSearchFieldScroll(true);
                    }

                    public void requestLayout() {
                        if (!this.ignoreLayout) {
                            super.requestLayout();
                        }
                    }
                };
                this.gifGridView.setClipToPadding(false);
                recyclerListView = this.gifGridView;
                AnonymousClass11 anonymousClass11 = new ExtendedGridLayoutManager(context, 100) {
                    private Size size = new Size();

                    /* Access modifiers changed, original: protected */
                    public Size getSizeForItem(int i) {
                        Document document;
                        ArrayList<DocumentAttribute> attributes;
                        if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter) {
                            document = (Document) EmojiView.this.recentGifs.get(i);
                            attributes = document.attributes;
                        } else if (EmojiView.this.gifSearchAdapter.results.isEmpty()) {
                            document = null;
                            attributes = null;
                        } else {
                            BotInlineResult result = (BotInlineResult) EmojiView.this.gifSearchAdapter.results.get(i);
                            document = result.document;
                            if (document != null) {
                                attributes = document.attributes;
                            } else if (result.content != null) {
                                attributes = result.content.attributes;
                            } else if (result.thumb != null) {
                                attributes = result.thumb.attributes;
                            } else {
                                attributes = null;
                            }
                        }
                        Size size = this.size;
                        this.size.height = 100.0f;
                        size.width = 100.0f;
                        if (document != null) {
                            PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                            if (!(thumb == null || thumb.w == 0 || thumb.h == 0)) {
                                this.size.width = (float) thumb.w;
                                this.size.height = (float) thumb.h;
                            }
                        }
                        if (attributes != null) {
                            for (int b = 0; b < attributes.size(); b++) {
                                DocumentAttribute attribute = (DocumentAttribute) attributes.get(b);
                                if ((attribute instanceof TL_documentAttributeImageSize) || (attribute instanceof TL_documentAttributeVideo)) {
                                    this.size.width = (float) attribute.w;
                                    this.size.height = (float) attribute.h;
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
                recyclerListView.setLayoutManager(anonymousClass11);
                this.gifLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
                    public int getSpanSize(int position) {
                        if (position == 0 || (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter && EmojiView.this.gifSearchAdapter.results.isEmpty())) {
                            return EmojiView.this.gifLayoutManager.getSpanCount();
                        }
                        return EmojiView.this.gifLayoutManager.getSpanSizeForItem(position - 1);
                    }
                });
                this.gifGridView.addItemDecoration(new ItemDecoration() {
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                        int i = 0;
                        int position = parent.getChildAdapterPosition(view);
                        if (position != 0) {
                            outRect.left = 0;
                            outRect.bottom = 0;
                            if (EmojiView.this.gifLayoutManager.isFirstRow(position - 1)) {
                                outRect.top = 0;
                            } else {
                                outRect.top = AndroidUtilities.dp(2.0f);
                            }
                            if (!EmojiView.this.gifLayoutManager.isLastInRow(position - 1)) {
                                i = AndroidUtilities.dp(2.0f);
                            }
                            outRect.right = i;
                            return;
                        }
                        outRect.left = 0;
                        outRect.top = 0;
                        outRect.bottom = 0;
                        outRect.right = 0;
                    }
                });
                this.gifGridView.setOverScrollMode(2);
                recyclerListView = this.gifGridView;
                GifAdapter gifAdapter = new GifAdapter(context);
                this.gifAdapter = gifAdapter;
                recyclerListView.setAdapter(gifAdapter);
                this.gifSearchAdapter = new GifSearchAdapter(context);
                this.gifGridView.setOnScrollListener(new OnScrollListener() {
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        if (newState == 1) {
                            EmojiView.this.gifSearchField.hideKeyboard();
                        }
                    }

                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        EmojiView.this.checkGifSearchFieldScroll(false);
                        EmojiView.this.checkBottomTabScroll((float) dy);
                    }
                });
                this.gifGridView.setOnTouchListener(new EmojiView$$Lambda$0(this));
                this.gifOnItemClickListener = new EmojiView$$Lambda$1(this);
                this.gifGridView.setOnItemClickListener(this.gifOnItemClickListener);
                this.gifContainer.addView(this.gifGridView, LayoutHelper.createFrame(-1, -1.0f));
                this.gifSearchField = new SearchField(context, 2);
                this.gifContainer.addView(this.gifSearchField, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + getShadowHeight()));
            }
            this.stickersContainer = new FrameLayout(context);
            DataQuery.getInstance(this.currentAccount).checkStickers(0);
            DataQuery.getInstance(this.currentAccount).checkFeaturedStickers();
            this.stickersGridView = new RecyclerListView(context) {
                boolean ignoreLayout;

                public boolean onInterceptTouchEvent(MotionEvent event) {
                    return super.onInterceptTouchEvent(event) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(event, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.contentPreviewViewerDelegate);
                }

                public void setVisibility(int visibility) {
                    if (EmojiView.this.trendingGridView == null || EmojiView.this.trendingGridView.getVisibility() != 0) {
                        super.setVisibility(visibility);
                    } else {
                        super.setVisibility(8);
                    }
                }

                /* Access modifiers changed, original: protected */
                public void onLayout(boolean changed, int l, int t, int r, int b) {
                    if (EmojiView.this.firstStickersAttach && EmojiView.this.stickersGridAdapter.getItemCount() > 0) {
                        this.ignoreLayout = true;
                        EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(1, 0);
                        EmojiView.this.firstStickersAttach = false;
                        this.ignoreLayout = false;
                    }
                    super.onLayout(changed, l, t, r, b);
                    EmojiView.this.checkStickersSearchFieldScroll(true);
                }

                public void requestLayout() {
                    if (!this.ignoreLayout) {
                        super.requestLayout();
                    }
                }
            };
            recyclerListView = this.stickersGridView;
            gridLayoutManager = new GridLayoutManager(context, 5);
            this.stickersLayoutManager = gridLayoutManager;
            recyclerListView.setLayoutManager(gridLayoutManager);
            this.stickersLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
                public int getSpanSize(int position) {
                    if (EmojiView.this.stickersGridView.getAdapter() == EmojiView.this.stickersGridAdapter) {
                        if (position == 0) {
                            return EmojiView.this.stickersGridAdapter.stickersPerRow;
                        }
                        if (position == EmojiView.this.stickersGridAdapter.totalItems || (EmojiView.this.stickersGridAdapter.cache.get(position) != null && !(EmojiView.this.stickersGridAdapter.cache.get(position) instanceof Document))) {
                            return EmojiView.this.stickersGridAdapter.stickersPerRow;
                        }
                        return 1;
                    } else if (position == EmojiView.this.stickersSearchGridAdapter.totalItems || (EmojiView.this.stickersSearchGridAdapter.cache.get(position) != null && !(EmojiView.this.stickersSearchGridAdapter.cache.get(position) instanceof Document))) {
                        return EmojiView.this.stickersGridAdapter.stickersPerRow;
                    } else {
                        return 1;
                    }
                }
            });
            this.stickersGridView.setPadding(0, AndroidUtilities.dp(52.0f), 0, 0);
            this.stickersGridView.setClipToPadding(false);
            this.views.add(this.stickersContainer);
            this.stickersSearchGridAdapter = new StickersSearchGridAdapter(context);
            recyclerListView = this.stickersGridView;
            StickersGridAdapter stickersGridAdapter = new StickersGridAdapter(context);
            this.stickersGridAdapter = stickersGridAdapter;
            recyclerListView.setAdapter(stickersGridAdapter);
            this.stickersGridView.setOnTouchListener(new EmojiView$$Lambda$2(this));
            this.stickersOnItemClickListener = new EmojiView$$Lambda$3(this);
            this.stickersGridView.setOnItemClickListener(this.stickersOnItemClickListener);
            this.stickersGridView.setGlowColor(Theme.getColor("chat_emojiPanelBackground"));
            this.stickersContainer.addView(this.stickersGridView);
            this.stickersTab = new ScrollSlidingTabStrip(context) {
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

                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    if (ev.getAction() == 0) {
                        this.draggingHorizontally = false;
                        this.draggingVertically = false;
                        this.downX = ev.getRawX();
                        this.downY = ev.getRawY();
                    } else if (!(this.draggingVertically || this.draggingHorizontally || EmojiView.this.dragListener == null || Math.abs(ev.getRawY() - this.downY) < ((float) this.touchslop))) {
                        this.draggingVertically = true;
                        this.downY = ev.getRawY();
                        EmojiView.this.dragListener.onDragStart();
                        if (!this.startedScroll) {
                            return true;
                        }
                        EmojiView.this.pager.endFakeDrag();
                        this.startedScroll = false;
                        return true;
                    }
                    return super.onInterceptTouchEvent(ev);
                }

                public boolean onTouchEvent(MotionEvent ev) {
                    boolean z = false;
                    if (this.first) {
                        this.first = false;
                        this.lastX = ev.getX();
                    }
                    if (ev.getAction() == 0) {
                        this.draggingHorizontally = false;
                        this.draggingVertically = false;
                        this.downX = ev.getRawX();
                        this.downY = ev.getRawY();
                    } else if (!(this.draggingVertically || this.draggingHorizontally || EmojiView.this.dragListener == null)) {
                        if (Math.abs(ev.getRawX() - this.downX) >= ((float) this.touchslop)) {
                            this.draggingHorizontally = true;
                        } else if (Math.abs(ev.getRawY() - this.downY) >= ((float) this.touchslop)) {
                            this.draggingVertically = true;
                            this.downY = ev.getRawY();
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
                        this.vTracker.addMovement(ev);
                        if (ev.getAction() == 1 || ev.getAction() == 3) {
                            this.vTracker.computeCurrentVelocity(1000);
                            float velocity = this.vTracker.getYVelocity();
                            this.vTracker.recycle();
                            this.vTracker = null;
                            if (ev.getAction() == 1) {
                                EmojiView.this.dragListener.onDragEnd(velocity);
                            } else {
                                EmojiView.this.dragListener.onDragCancel();
                            }
                            this.first = true;
                            this.draggingHorizontally = false;
                            this.draggingVertically = false;
                            return true;
                        }
                        EmojiView.this.dragListener.onDrag(Math.round(ev.getRawY() - this.downY));
                        return true;
                    }
                    float newTranslationX = EmojiView.this.stickersTab.getTranslationX();
                    if (EmojiView.this.stickersTab.getScrollX() == 0 && newTranslationX == 0.0f) {
                        if (this.startedScroll || this.lastX - ev.getX() >= 0.0f) {
                            if (this.startedScroll && this.lastX - ev.getX() > 0.0f && EmojiView.this.pager.isFakeDragging()) {
                                EmojiView.this.pager.endFakeDrag();
                                this.startedScroll = false;
                            }
                        } else if (EmojiView.this.pager.beginFakeDrag()) {
                            this.startedScroll = true;
                            this.lastTranslateX = EmojiView.this.stickersTab.getTranslationX();
                        }
                    }
                    if (this.startedScroll) {
                        int dx = (int) (((ev.getX() - this.lastX) + newTranslationX) - this.lastTranslateX);
                        try {
                            this.lastTranslateX = newTranslationX;
                        } catch (Exception e) {
                            try {
                                EmojiView.this.pager.endFakeDrag();
                            } catch (Exception e2) {
                            }
                            this.startedScroll = false;
                            FileLog.e(e);
                        }
                    }
                    this.lastX = ev.getX();
                    if (ev.getAction() == 3 || ev.getAction() == 1) {
                        this.first = true;
                        this.draggingHorizontally = false;
                        this.draggingVertically = false;
                        if (this.startedScroll) {
                            EmojiView.this.pager.endFakeDrag();
                            this.startedScroll = false;
                        }
                    }
                    if (this.startedScroll || super.onTouchEvent(ev)) {
                        z = true;
                    }
                    return z;
                }
            };
            this.stickersSearchField = new SearchField(context, 0);
            this.stickersContainer.addView(this.stickersSearchField, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + getShadowHeight()));
            this.trendingGridView = new RecyclerListView(context);
            this.trendingGridView.setItemAnimator(null);
            this.trendingGridView.setLayoutAnimation(null);
            recyclerListView = this.trendingGridView;
            AnonymousClass18 anonymousClass18 = new GridLayoutManager(context, 5) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            this.trendingLayoutManager = anonymousClass18;
            recyclerListView.setLayoutManager(anonymousClass18);
            this.trendingLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
                public int getSpanSize(int position) {
                    if ((EmojiView.this.trendingGridAdapter.cache.get(position) instanceof Integer) || position == EmojiView.this.trendingGridAdapter.totalItems) {
                        return EmojiView.this.trendingGridAdapter.stickersPerRow;
                    }
                    return 1;
                }
            });
            this.trendingGridView.setOnScrollListener(new OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    EmojiView.this.checkStickersTabY(recyclerView, dy);
                    EmojiView.this.checkBottomTabScroll((float) dy);
                }
            });
            this.trendingGridView.setClipToPadding(false);
            this.trendingGridView.setPadding(0, AndroidUtilities.dp(48.0f), 0, 0);
            recyclerListView = this.trendingGridView;
            TrendingGridAdapter trendingGridAdapter = new TrendingGridAdapter(context);
            this.trendingGridAdapter = trendingGridAdapter;
            recyclerListView.setAdapter(trendingGridAdapter);
            this.trendingGridView.setOnItemClickListener(new EmojiView$$Lambda$4(this));
            this.trendingGridAdapter.notifyDataSetChanged();
            this.trendingGridView.setGlowColor(Theme.getColor("chat_emojiPanelBackground"));
            this.trendingGridView.setVisibility(8);
            this.stickersContainer.addView(this.trendingGridView);
            this.stickersTab.setUnderlineHeight(getShadowHeight());
            this.stickersTab.setIndicatorHeight(AndroidUtilities.dp(2.0f));
            this.stickersTab.setIndicatorColor(Theme.getColor("chat_emojiPanelStickerPackSelectorLine"));
            this.stickersTab.setUnderlineColor(Theme.getColor("chat_emojiPanelShadowLine"));
            this.stickersTab.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
            this.stickersContainer.addView(this.stickersTab, LayoutHelper.createFrame(-1, 48, 51));
            updateStickerTabs();
            this.stickersTab.setDelegate(new EmojiView$$Lambda$5(this));
            this.stickersGridView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 1) {
                        EmojiView.this.stickersSearchField.hideKeyboard();
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    EmojiView.this.checkScroll();
                    EmojiView.this.checkStickersTabY(recyclerView, dy);
                    EmojiView.this.checkStickersSearchFieldScroll(false);
                    EmojiView.this.checkBottomTabScroll((float) dy);
                }
            });
        }
        this.pager = new ViewPager(context) {
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(ev);
            }

            public void setCurrentItem(int item, boolean smoothScroll) {
                int i = 1;
                if (item != getCurrentItem()) {
                    super.setCurrentItem(item, smoothScroll);
                } else if (item == 0) {
                    RecyclerListView access$3600 = EmojiView.this.emojiGridView;
                    if (!EmojiView.this.needEmojiSearch) {
                        i = 0;
                    }
                    access$3600.smoothScrollToPosition(i);
                } else if (item == 1) {
                    EmojiView.this.gifGridView.smoothScrollToPosition(1);
                } else {
                    EmojiView.this.stickersGridView.smoothScrollToPosition(1);
                }
            }
        };
        this.pager.setAdapter(new EmojiPagesAdapter(this, null));
        this.topShadow = new View(context);
        this.topShadow.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, -1907225));
        addView(this.topShadow, LayoutHelper.createFrame(-1, 6.0f));
        this.backspaceButton = new ImageView(context) {
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == 0) {
                    EmojiView.this.backspacePressed = true;
                    EmojiView.this.backspaceOnce = false;
                    EmojiView.this.postBackspaceRunnable(350);
                } else if (event.getAction() == 3 || event.getAction() == 1) {
                    EmojiView.this.backspacePressed = false;
                    if (!(EmojiView.this.backspaceOnce || EmojiView.this.delegate == null || !EmojiView.this.delegate.onBackspace())) {
                        EmojiView.this.backspaceButton.performHapticFeedback(3);
                    }
                }
                super.onTouchEvent(event);
                return true;
            }
        };
        this.backspaceButton.setImageResource(NUM);
        this.backspaceButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelBackspace"), Mode.MULTIPLY));
        this.backspaceButton.setScaleType(ScaleType.CENTER);
        this.bottomTabContainer = new FrameLayout(context) {
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(ev);
            }
        };
        this.shadowLine = new View(context);
        this.shadowLine.setBackgroundColor(Theme.getColor("chat_emojiPanelShadowLine"));
        this.bottomTabContainer.addView(this.shadowLine, new FrameLayout.LayoutParams(-1, getShadowHeight()));
        this.bottomTabContainerBackground = new View(context);
        this.bottomTabContainer.addView(this.bottomTabContainerBackground, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(44.0f), 83));
        if (needSearch) {
            addView(this.bottomTabContainer, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(44.0f) + getShadowHeight(), 83));
            this.bottomTabContainer.addView(this.backspaceButton, LayoutHelper.createFrame(52, 44, 85));
            this.typeTabs = new PagerSlidingTabStrip(context);
            this.typeTabs.setViewPager(this.pager);
            this.typeTabs.setShouldExpand(false);
            this.typeTabs.setIndicatorHeight(0);
            this.typeTabs.setUnderlineHeight(0);
            this.typeTabs.setTabPaddingLeftRight(AndroidUtilities.dp(10.0f));
            this.bottomTabContainer.addView(this.typeTabs, LayoutHelper.createFrame(-2, 44, 81));
            this.typeTabs.setOnPageChangeListener(new OnPageChangeListener() {
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    SearchField currentField;
                    EmojiView.this.onPageScrolled(position, (EmojiView.this.getMeasuredWidth() - EmojiView.this.getPaddingLeft()) - EmojiView.this.getPaddingRight(), positionOffsetPixels);
                    EmojiView.this.showBottomTab(true, true);
                    int p = EmojiView.this.pager.getCurrentItem();
                    if (p == 0) {
                        currentField = EmojiView.this.emojiSearchField;
                    } else if (p == 1) {
                        currentField = EmojiView.this.gifSearchField;
                    } else {
                        currentField = EmojiView.this.stickersSearchField;
                    }
                    String currentFieldText = currentField.searchEditText.getText().toString();
                    for (int a = 0; a < 3; a++) {
                        SearchField field;
                        if (a == 0) {
                            field = EmojiView.this.emojiSearchField;
                        } else if (a == 1) {
                            field = EmojiView.this.gifSearchField;
                        } else {
                            field = EmojiView.this.stickersSearchField;
                        }
                        if (!(field == null || field == currentField || field.searchEditText == null || field.searchEditText.getText().toString().equals(currentFieldText))) {
                            field.searchEditText.setText(currentFieldText);
                            field.searchEditText.setSelection(currentFieldText.length());
                        }
                    }
                }

                public void onPageSelected(int position) {
                    EmojiView.this.saveNewPage();
                    EmojiView.this.showBackspaceButton(position == 0, true);
                    if (!EmojiView.this.delegate.isSearchOpened()) {
                        return;
                    }
                    if (position == 0) {
                        if (EmojiView.this.emojiSearchField != null) {
                            EmojiView.this.emojiSearchField.searchEditText.requestFocus();
                        }
                    } else if (position == 1) {
                        if (EmojiView.this.gifSearchField != null) {
                            EmojiView.this.gifSearchField.searchEditText.requestFocus();
                        }
                    } else if (EmojiView.this.stickersSearchField != null) {
                        EmojiView.this.stickersSearchField.searchEditText.requestFocus();
                    }
                }

                public void onPageScrollStateChanged(int state) {
                }
            });
            this.searchButton = new ImageView(context);
            this.searchButton.setImageResource(NUM);
            this.searchButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelBackspace"), Mode.MULTIPLY));
            this.searchButton.setScaleType(ScaleType.CENTER);
            this.searchButton.setContentDescription(LocaleController.getString("Search", NUM));
            this.bottomTabContainer.addView(this.searchButton, LayoutHelper.createFrame(52, 44, 83));
            this.searchButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    SearchField currentField;
                    int currentItem = EmojiView.this.pager.getCurrentItem();
                    if (currentItem == 0) {
                        currentField = EmojiView.this.emojiSearchField;
                    } else if (currentItem == 1) {
                        currentField = EmojiView.this.gifSearchField;
                    } else {
                        currentField = EmojiView.this.stickersSearchField;
                    }
                    if (currentField != null) {
                        currentField.searchEditText.requestFocus();
                        MotionEvent event = MotionEvent.obtain(0, 0, 0, 0.0f, 0.0f, 0);
                        currentField.searchEditText.onTouchEvent(event);
                        event.recycle();
                        event = MotionEvent.obtain(0, 0, 1, 0.0f, 0.0f, 0);
                        currentField.searchEditText.onTouchEvent(event);
                        event.recycle();
                    }
                }
            });
        } else {
            int i2;
            addView(this.bottomTabContainer, LayoutHelper.createFrame((VERSION.SDK_INT >= 21 ? 40 : 44) + 20, (float) ((VERSION.SDK_INT >= 21 ? 40 : 44) + 12), (LocaleController.isRTL ? 3 : 5) | 80, 0.0f, 0.0f, 2.0f, 0.0f));
            Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chat_emojiPanelBackground"), Theme.getColor("chat_emojiPanelBackground"));
            if (VERSION.SDK_INT < 21) {
                Drawable shadowDrawable = context.getResources().getDrawable(NUM).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
                Drawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                drawable = combinedDrawable;
            } else {
                StateListAnimator animator = new StateListAnimator();
                animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                this.backspaceButton.setStateListAnimator(animator);
                this.backspaceButton.setOutlineProvider(new ViewOutlineProvider() {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                    }
                });
            }
            this.backspaceButton.setPadding(0, 0, AndroidUtilities.dp(2.0f), 0);
            this.backspaceButton.setBackgroundDrawable(drawable);
            this.backspaceButton.setContentDescription(LocaleController.getString("AccDescrBackspace", NUM));
            this.backspaceButton.setFocusable(true);
            FrameLayout frameLayout = this.bottomTabContainer;
            ImageView imageView = this.backspaceButton;
            i = VERSION.SDK_INT >= 21 ? 40 : 44;
            if (VERSION.SDK_INT >= 21) {
                i2 = 40;
            } else {
                i2 = 44;
            }
            frameLayout.addView(imageView, LayoutHelper.createFrame(i, (float) i2, 51, 10.0f, 0.0f, 10.0f, 0.0f));
            this.shadowLine.setVisibility(8);
            this.bottomTabContainerBackground.setVisibility(8);
        }
        addView(this.pager, 0, LayoutHelper.createFrame(-1, -1, 51));
        this.mediaBanTooltip = new CorrectlyMeasuringTextView(context);
        this.mediaBanTooltip.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor("chat_gifSaveHintBackground")));
        this.mediaBanTooltip.setTextColor(Theme.getColor("chat_gifSaveHintText"));
        this.mediaBanTooltip.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f));
        this.mediaBanTooltip.setGravity(16);
        this.mediaBanTooltip.setTextSize(1, 14.0f);
        this.mediaBanTooltip.setVisibility(4);
        addView(this.mediaBanTooltip, LayoutHelper.createFrame(-2, -2.0f, 81, 5.0f, 0.0f, 5.0f, 53.0f));
        this.emojiSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 32.0f);
        this.pickerView = new EmojiColorPickerView(context);
        EmojiColorPickerView emojiColorPickerView = this.pickerView;
        int dp = AndroidUtilities.dp((float) ((((AndroidUtilities.isTablet() ? 40 : 32) * 6) + 10) + 20));
        this.popupWidth = dp;
        i = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 64.0f : 56.0f);
        this.popupHeight = i;
        this.pickerViewPopup = new EmojiPopupWindow(emojiColorPickerView, dp, i);
        this.pickerViewPopup.setOutsideTouchable(true);
        this.pickerViewPopup.setClippingEnabled(true);
        this.pickerViewPopup.setInputMethodMode(2);
        this.pickerViewPopup.setSoftInputMode(0);
        this.pickerViewPopup.getContentView().setFocusableInTouchMode(true);
        this.pickerViewPopup.getContentView().setOnKeyListener(new EmojiView$$Lambda$6(this));
        this.currentPage = MessagesController.getGlobalEmojiSettings().getInt("selected_page", 0);
        Emoji.loadRecentEmoji();
        this.emojiAdapter.notifyDataSetChanged();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$new$1$EmojiView(View v, MotionEvent event) {
        return ContentPreviewViewer.getInstance().onTouch(event, this.gifGridView, 0, this.gifOnItemClickListener, this.contentPreviewViewerDelegate);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$2$EmojiView(View view, int position) {
        if (this.delegate != null) {
            position--;
            if (this.gifGridView.getAdapter() == this.gifAdapter) {
                if (position >= 0 && position < this.recentGifs.size()) {
                    this.delegate.onGifSelected(this.recentGifs.get(position), "gif");
                }
            } else if (this.gifGridView.getAdapter() == this.gifSearchAdapter && position >= 0 && position < this.gifSearchAdapter.results.size()) {
                this.delegate.onGifSelected(this.gifSearchAdapter.results.get(position), this.gifSearchAdapter.bot);
                this.recentGifs = DataQuery.getInstance(this.currentAccount).getRecentGifs();
                if (this.gifAdapter != null) {
                    this.gifAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$new$3$EmojiView(View v, MotionEvent event) {
        return ContentPreviewViewer.getInstance().onTouch(event, this.stickersGridView, getMeasuredHeight(), this.stickersOnItemClickListener, this.contentPreviewViewerDelegate);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$4$EmojiView(View view, int position) {
        if (this.stickersGridView.getAdapter() == this.stickersSearchGridAdapter) {
            StickerSetCovered pack = (StickerSetCovered) this.stickersSearchGridAdapter.positionsToSets.get(position);
            if (pack != null) {
                this.delegate.onShowStickerSet(pack.set, null);
                return;
            }
        }
        if (view instanceof StickerEmojiCell) {
            ContentPreviewViewer.getInstance().reset();
            StickerEmojiCell cell = (StickerEmojiCell) view;
            if (!cell.isDisabled()) {
                cell.disable();
                this.delegate.onStickerSelected(cell.getSticker(), cell.getParentObject());
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$5$EmojiView(View view, int position) {
        StickerSetCovered pack = (StickerSetCovered) this.trendingGridAdapter.positionsToSets.get(position);
        if (pack != null) {
            this.delegate.onShowStickerSet(pack.set, null);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$6$EmojiView(int page) {
        if (page == this.trendingTabNum) {
            if (this.trendingGridView.getVisibility() != 0) {
                showTrendingTab(true);
            }
        } else if (this.trendingGridView.getVisibility() == 0) {
            showTrendingTab(false);
            saveNewPage();
        }
        if (page != this.trendingTabNum) {
            if (page == this.recentTabBum) {
                this.stickersGridView.stopScroll();
                this.stickersLayoutManager.scrollToPositionWithOffset(this.stickersGridAdapter.getPositionForPack("recent"), 0);
                checkStickersTabY(null, 0);
                this.stickersTab.onPageScrolled(this.recentTabBum, this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset);
            } else if (page == this.favTabBum) {
                this.stickersGridView.stopScroll();
                this.stickersLayoutManager.scrollToPositionWithOffset(this.stickersGridAdapter.getPositionForPack("fav"), 0);
                checkStickersTabY(null, 0);
                this.stickersTab.onPageScrolled(this.favTabBum, this.favTabBum > 0 ? this.favTabBum : this.stickersTabOffset);
            } else {
                int index = page - this.stickersTabOffset;
                if (index < this.stickerSets.size()) {
                    if (index >= this.stickerSets.size()) {
                        index = this.stickerSets.size() - 1;
                    }
                    this.firstStickersAttach = false;
                    this.stickersGridView.stopScroll();
                    this.stickersLayoutManager.scrollToPositionWithOffset(this.stickersGridAdapter.getPositionForPack(this.stickerSets.get(index)), 0);
                    checkStickersTabY(null, 0);
                    checkScroll();
                } else if (this.delegate != null) {
                    this.delegate.onStickersSettingsClick();
                }
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$new$7$EmojiView(View v, int keyCode, KeyEvent event) {
        if (keyCode != 82 || event.getRepeatCount() != 0 || event.getAction() != 1 || this.pickerViewPopup == null || !this.pickerViewPopup.isShowing()) {
            return false;
        }
        this.pickerViewPopup.dismiss();
        return true;
    }

    private int getShadowHeight() {
        if (AndroidUtilities.density >= 4.0f) {
            return 3;
        }
        if (AndroidUtilities.density >= 2.0f) {
            return 2;
        }
        return 1;
    }

    private static String addColorToCode(String code, String color) {
        String end = null;
        int length = code.length();
        if (length > 2 && code.charAt(code.length() - 2) == 8205) {
            end = code.substring(code.length() - 2);
            code = code.substring(0, code.length() - 2);
        } else if (length > 3 && code.charAt(code.length() - 3) == 8205) {
            end = code.substring(code.length() - 3);
            code = code.substring(0, code.length() - 3);
        }
        code = code + color;
        if (end != null) {
            return code + end;
        }
        return code;
    }

    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        if (this.bottomTabContainer.getTag() != null) {
            return;
        }
        if (this.delegate == null || !this.delegate.isSearchOpened()) {
            View parent = (View) getParent();
            if (parent != null) {
                this.bottomTabContainer.setTranslationY(-((getY() + ((float) getMeasuredHeight())) - ((float) parent.getHeight())));
            }
        }
    }

    public void addEmojiToRecent(String code) {
        if (Emoji.isValidEmoji(code)) {
            int oldCount = Emoji.recentEmoji.size();
            Emoji.addRecentEmoji(code);
            if (!(getVisibility() == 0 && this.pager.getCurrentItem() == 0)) {
                Emoji.sortEmoji();
                this.emojiAdapter.notifyDataSetChanged();
            }
            Emoji.saveRecentEmoji();
        }
    }

    public void showSearchField(boolean show) {
        for (int a = 0; a < 3; a++) {
            GridLayoutManager layoutManager;
            ScrollSlidingTabStrip tabStrip;
            if (a == 0) {
                layoutManager = this.emojiLayoutManager;
                tabStrip = this.emojiTabs;
            } else if (a == 1) {
                layoutManager = this.gifLayoutManager;
                tabStrip = null;
            } else {
                layoutManager = this.stickersLayoutManager;
                tabStrip = this.stickersTab;
            }
            if (layoutManager != null) {
                int position = layoutManager.findFirstVisibleItemPosition();
                if (show) {
                    if (position == 1 || position == 2) {
                        layoutManager.scrollToPosition(0);
                        if (tabStrip != null) {
                            tabStrip.setTranslationY(0.0f);
                        }
                    }
                } else if (position == 0) {
                    layoutManager.scrollToPositionWithOffset(1, 0);
                }
            }
        }
    }

    public void hideSearchKeyboard() {
        if (this.stickersSearchField != null) {
            this.stickersSearchField.hideKeyboard();
        }
        if (this.gifSearchField != null) {
            this.gifSearchField.hideKeyboard();
        }
        if (this.emojiSearchField != null) {
            this.emojiSearchField.hideKeyboard();
        }
    }

    private void openSearch(SearchField searchField) {
        if (this.searchAnimation != null) {
            this.searchAnimation.cancel();
            this.searchAnimation = null;
        }
        this.firstStickersAttach = false;
        this.firstGifAttach = false;
        this.firstEmojiAttach = false;
        for (int a = 0; a < 3; a++) {
            SearchField currentField;
            RecyclerListView gridView;
            ScrollSlidingTabStrip tabStrip;
            GridLayoutManager layoutManager;
            if (a == 0) {
                currentField = this.emojiSearchField;
                gridView = this.emojiGridView;
                tabStrip = this.emojiTabs;
                layoutManager = this.emojiLayoutManager;
            } else if (a == 1) {
                currentField = this.gifSearchField;
                gridView = this.gifGridView;
                tabStrip = null;
                layoutManager = this.gifLayoutManager;
            } else {
                currentField = this.stickersSearchField;
                gridView = this.stickersGridView;
                tabStrip = this.stickersTab;
                layoutManager = this.stickersLayoutManager;
            }
            if (currentField != null) {
                if (currentField == this.gifSearchField || searchField != currentField || this.delegate == null || !this.delegate.isExpanded()) {
                    currentField.setTranslationY((float) AndroidUtilities.dp(0.0f));
                    if (tabStrip != null) {
                        tabStrip.setTranslationY((float) (-AndroidUtilities.dp(48.0f)));
                    }
                    if (gridView == this.stickersGridView) {
                        gridView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                    } else if (gridView == this.emojiGridView) {
                        gridView.setPadding(0, 0, 0, 0);
                    }
                    layoutManager.scrollToPositionWithOffset(0, 0);
                } else {
                    this.searchAnimation = new AnimatorSet();
                    AnimatorSet animatorSet;
                    Animator[] animatorArr;
                    if (tabStrip != null) {
                        animatorSet = this.searchAnimation;
                        animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofFloat(tabStrip, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))});
                        animatorArr[1] = ObjectAnimator.ofFloat(gridView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))});
                        animatorArr[2] = ObjectAnimator.ofFloat(currentField, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(0.0f)});
                        animatorSet.playTogether(animatorArr);
                    } else {
                        animatorSet = this.searchAnimation;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(gridView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f))});
                        animatorArr[1] = ObjectAnimator.ofFloat(currentField, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(0.0f)});
                        animatorSet.playTogether(animatorArr);
                    }
                    this.searchAnimation.setDuration(200);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(EmojiView.this.searchAnimation)) {
                                gridView.setTranslationY(0.0f);
                                if (gridView == EmojiView.this.stickersGridView) {
                                    gridView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                                } else if (gridView == EmojiView.this.emojiGridView) {
                                    gridView.setPadding(0, 0, 0, 0);
                                }
                                EmojiView.this.searchAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            if (animation.equals(EmojiView.this.searchAnimation)) {
                                EmojiView.this.searchAnimation = null;
                            }
                        }
                    });
                    this.searchAnimation.start();
                }
            }
        }
    }

    private void showEmojiShadow(boolean show, boolean animated) {
        Object obj = null;
        float f = 1.0f;
        if (!show || this.emojiTabsShadow.getTag() != null) {
            if (show || this.emojiTabsShadow.getTag() == null) {
                if (this.emojiTabShadowAnimator != null) {
                    this.emojiTabShadowAnimator.cancel();
                    this.emojiTabShadowAnimator = null;
                }
                View view = this.emojiTabsShadow;
                if (!show) {
                    obj = Integer.valueOf(1);
                }
                view.setTag(obj);
                if (animated) {
                    float f2;
                    this.emojiTabShadowAnimator = new AnimatorSet();
                    AnimatorSet animatorSet = this.emojiTabShadowAnimator;
                    Animator[] animatorArr = new Animator[1];
                    View view2 = this.emojiTabsShadow;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    if (show) {
                        f2 = 1.0f;
                    } else {
                        f2 = 0.0f;
                    }
                    fArr[0] = f2;
                    animatorArr[0] = ObjectAnimator.ofFloat(view2, property, fArr);
                    animatorSet.playTogether(animatorArr);
                    this.emojiTabShadowAnimator.setDuration(200);
                    this.emojiTabShadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.emojiTabShadowAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            EmojiView.this.emojiTabShadowAnimator = null;
                        }
                    });
                    this.emojiTabShadowAnimator.start();
                    return;
                }
                View view3 = this.emojiTabsShadow;
                if (!show) {
                    f = 0.0f;
                }
                view3.setAlpha(f);
            }
        }
    }

    public void closeSearch(boolean animated) {
        closeSearch(animated, -1);
    }

    public void closeSearch(boolean animated, long scrollToSet) {
        if (this.searchAnimation != null) {
            this.searchAnimation.cancel();
            this.searchAnimation = null;
        }
        int currentItem = this.pager.getCurrentItem();
        if (currentItem == 2 && scrollToSet != -1) {
            TL_messages_stickerSet set = DataQuery.getInstance(this.currentAccount).getStickerSetById(scrollToSet);
            if (set != null) {
                int pos = this.stickersGridAdapter.getPositionForPack(set);
                if (pos >= 0) {
                    this.stickersLayoutManager.scrollToPositionWithOffset(pos, AndroidUtilities.dp(60.0f));
                }
            }
        }
        for (int a = 0; a < 3; a++) {
            SearchField currentField;
            RecyclerListView gridView;
            GridLayoutManager layoutManager;
            ScrollSlidingTabStrip tabStrip;
            if (a == 0) {
                currentField = this.emojiSearchField;
                gridView = this.emojiGridView;
                layoutManager = this.emojiLayoutManager;
                tabStrip = this.emojiTabs;
            } else if (a == 1) {
                currentField = this.gifSearchField;
                gridView = this.gifGridView;
                layoutManager = this.gifLayoutManager;
                tabStrip = null;
            } else {
                currentField = this.stickersSearchField;
                gridView = this.stickersGridView;
                layoutManager = this.stickersLayoutManager;
                tabStrip = this.stickersTab;
            }
            if (currentField != null) {
                currentField.searchEditText.setText("");
                if (a == currentItem && animated) {
                    this.searchAnimation = new AnimatorSet();
                    AnimatorSet animatorSet;
                    Animator[] animatorArr;
                    if (tabStrip != null) {
                        animatorSet = this.searchAnimation;
                        animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofFloat(tabStrip, View.TRANSLATION_Y, new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(gridView, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight)});
                        animatorArr[2] = ObjectAnimator.ofFloat(currentField, View.TRANSLATION_Y, new float[]{(float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight)});
                        animatorSet.playTogether(animatorArr);
                    } else {
                        animatorSet = this.searchAnimation;
                        animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(gridView, View.TRANSLATION_Y, new float[]{(float) (-this.searchFieldHeight)});
                        animatorArr[1] = ObjectAnimator.ofFloat(currentField, View.TRANSLATION_Y, new float[]{(float) (-this.searchFieldHeight)});
                        animatorSet.playTogether(animatorArr);
                    }
                    this.searchAnimation.setDuration(200);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(EmojiView.this.searchAnimation)) {
                                int pos = layoutManager.findFirstVisibleItemPosition();
                                int firstVisPos = layoutManager.findFirstVisibleItemPosition();
                                int top = 0;
                                if (firstVisPos != -1) {
                                    top = (int) (((float) layoutManager.findViewByPosition(firstVisPos).getTop()) + gridView.getTranslationY());
                                }
                                gridView.setTranslationY(0.0f);
                                if (gridView == EmojiView.this.stickersGridView) {
                                    gridView.setPadding(0, AndroidUtilities.dp(52.0f), 0, 0);
                                } else if (gridView == EmojiView.this.emojiGridView) {
                                    gridView.setPadding(0, AndroidUtilities.dp(38.0f), 0, 0);
                                }
                                if (gridView == EmojiView.this.gifGridView) {
                                    layoutManager.scrollToPositionWithOffset(1, 0);
                                } else if (firstVisPos != -1) {
                                    layoutManager.scrollToPositionWithOffset(firstVisPos, top - gridView.getPaddingTop());
                                }
                                EmojiView.this.searchAnimation = null;
                            }
                        }

                        public void onAnimationCancel(Animator animation) {
                            if (animation.equals(EmojiView.this.searchAnimation)) {
                                EmojiView.this.searchAnimation = null;
                            }
                        }
                    });
                    this.searchAnimation.start();
                } else {
                    layoutManager.scrollToPositionWithOffset(1, 0);
                    currentField.setTranslationY((float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight));
                    if (tabStrip != null) {
                        tabStrip.setTranslationY(0.0f);
                    }
                    if (gridView == this.stickersGridView) {
                        gridView.setPadding(0, AndroidUtilities.dp(52.0f), 0, 0);
                    } else if (gridView == this.emojiGridView) {
                        gridView.setPadding(0, AndroidUtilities.dp(38.0f), 0, 0);
                    }
                }
            }
        }
        if (!animated) {
            this.delegate.onSearchOpenClose(0);
        }
    }

    private void checkStickersSearchFieldScroll(boolean isLayout) {
        boolean z = true;
        boolean z2 = false;
        ViewHolder holder;
        SearchField searchField;
        if (this.delegate != null && this.delegate.isSearchOpened()) {
            holder = this.stickersGridView.findViewHolderForAdapterPosition(0);
            if (holder == null) {
                searchField = this.stickersSearchField;
                if (!isLayout) {
                    z2 = true;
                }
                searchField.showShadow(true, z2);
                return;
            }
            SearchField searchField2 = this.stickersSearchField;
            boolean z3 = holder.itemView.getTop() < this.stickersGridView.getPaddingTop();
            if (isLayout) {
                z = false;
            }
            searchField2.showShadow(z3, z);
        } else if (this.stickersSearchField != null && this.stickersGridView != null) {
            holder = this.stickersGridView.findViewHolderForAdapterPosition(0);
            if (holder != null) {
                this.stickersSearchField.setTranslationY((float) holder.itemView.getTop());
            } else {
                this.stickersSearchField.setTranslationY((float) (-this.searchFieldHeight));
            }
            searchField = this.stickersSearchField;
            if (isLayout) {
                z = false;
            }
            searchField.showShadow(false, z);
        }
    }

    private void checkBottomTabScroll(float dy) {
        int offset;
        this.lastBottomScrollDy += dy;
        if (this.pager.getCurrentItem() == 0) {
            offset = AndroidUtilities.dp(38.0f);
        } else {
            offset = AndroidUtilities.dp(48.0f);
        }
        if (this.lastBottomScrollDy >= ((float) offset)) {
            showBottomTab(false, true);
        } else if (this.lastBottomScrollDy <= ((float) (-offset))) {
            showBottomTab(true, true);
        } else if ((this.bottomTabContainer.getTag() == null && this.lastBottomScrollDy < 0.0f) || (this.bottomTabContainer.getTag() != null && this.lastBottomScrollDy > 0.0f)) {
            this.lastBottomScrollDy = 0.0f;
        }
    }

    private void showBackspaceButton(boolean show, boolean animated) {
        Object obj = null;
        float f = 1.0f;
        if (!show || this.backspaceButton.getTag() != null) {
            if (show || this.backspaceButton.getTag() == null) {
                if (this.backspaceButtonAnimation != null) {
                    this.backspaceButtonAnimation.cancel();
                    this.backspaceButtonAnimation = null;
                }
                ImageView imageView = this.backspaceButton;
                if (!show) {
                    obj = Integer.valueOf(1);
                }
                imageView.setTag(obj);
                if (animated) {
                    float f2;
                    this.backspaceButtonAnimation = new AnimatorSet();
                    AnimatorSet animatorSet = this.backspaceButtonAnimation;
                    Animator[] animatorArr = new Animator[1];
                    ImageView imageView2 = this.backspaceButton;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    if (show) {
                        f2 = 1.0f;
                    } else {
                        f2 = 0.0f;
                    }
                    fArr[0] = f2;
                    animatorArr[0] = ObjectAnimator.ofFloat(imageView2, property, fArr);
                    animatorSet.playTogether(animatorArr);
                    this.backspaceButtonAnimation.setDuration(200);
                    this.backspaceButtonAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.backspaceButtonAnimation.start();
                    return;
                }
                ImageView imageView3 = this.backspaceButton;
                if (!show) {
                    f = 0.0f;
                }
                imageView3.setAlpha(f);
            }
        }
    }

    private void showBottomTab(boolean show, boolean animated) {
        float f = 54.0f;
        float f2 = 0.0f;
        this.lastBottomScrollDy = 0.0f;
        if (!show || this.bottomTabContainer.getTag() != null) {
            if (!show && this.bottomTabContainer.getTag() != null) {
                return;
            }
            if (this.delegate == null || !this.delegate.isSearchOpened()) {
                if (this.bottomTabContainerAnimation != null) {
                    this.bottomTabContainerAnimation.cancel();
                    this.bottomTabContainerAnimation = null;
                }
                this.bottomTabContainer.setTag(show ? null : Integer.valueOf(1));
                float f3;
                View view;
                if (animated) {
                    this.bottomTabContainerAnimation = new AnimatorSet();
                    AnimatorSet animatorSet = this.bottomTabContainerAnimation;
                    Animator[] animatorArr = new Animator[2];
                    FrameLayout frameLayout = this.bottomTabContainer;
                    Property property = View.TRANSLATION_Y;
                    float[] fArr = new float[1];
                    if (show) {
                        f3 = 0.0f;
                    } else {
                        if (this.needEmojiSearch) {
                            f3 = 49.0f;
                        } else {
                            f3 = 54.0f;
                        }
                        f3 = (float) AndroidUtilities.dp(f3);
                    }
                    fArr[0] = f3;
                    animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
                    view = this.shadowLine;
                    Property property2 = View.TRANSLATION_Y;
                    float[] fArr2 = new float[1];
                    if (!show) {
                        f2 = (float) AndroidUtilities.dp(49.0f);
                    }
                    fArr2[0] = f2;
                    animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
                    animatorSet.playTogether(animatorArr);
                    this.bottomTabContainerAnimation.setDuration(200);
                    this.bottomTabContainerAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.bottomTabContainerAnimation.start();
                    return;
                }
                FrameLayout frameLayout2 = this.bottomTabContainer;
                if (show) {
                    f3 = 0.0f;
                } else {
                    if (this.needEmojiSearch) {
                        f = 49.0f;
                    }
                    f3 = (float) AndroidUtilities.dp(f);
                }
                frameLayout2.setTranslationY(f3);
                view = this.shadowLine;
                if (!show) {
                    f2 = (float) AndroidUtilities.dp(49.0f);
                }
                view.setTranslationY(f2);
            }
        }
    }

    private void checkStickersTabY(View list, int dy) {
        if (list == null) {
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            this.stickersMinusDy = 0;
            scrollSlidingTabStrip.setTranslationY((float) null);
        } else if (list.getVisibility() != 0) {
        } else {
            if (this.delegate == null || !this.delegate.isSearchOpened()) {
                if (dy > 0 && this.stickersGridView != null && this.stickersGridView.getVisibility() == 0) {
                    ViewHolder holder = this.stickersGridView.findViewHolderForAdapterPosition(0);
                    if (holder != null && holder.itemView.getTop() + this.searchFieldHeight >= this.stickersGridView.getPaddingTop()) {
                        return;
                    }
                }
                this.stickersMinusDy -= dy;
                if (this.stickersMinusDy > 0) {
                    this.stickersMinusDy = 0;
                } else if (this.stickersMinusDy < (-AndroidUtilities.dp(288.0f))) {
                    this.stickersMinusDy = -AndroidUtilities.dp(288.0f);
                }
                this.stickersTab.setTranslationY((float) Math.max(-AndroidUtilities.dp(48.0f), this.stickersMinusDy));
            }
        }
    }

    private void checkEmojiSearchFieldScroll(boolean isLayout) {
        boolean z = true;
        ViewHolder holder;
        SearchField searchField;
        boolean z2;
        if (this.delegate != null && this.delegate.isSearchOpened()) {
            holder = this.emojiGridView.findViewHolderForAdapterPosition(0);
            if (holder == null) {
                searchField = this.emojiSearchField;
                if (isLayout) {
                    z2 = false;
                } else {
                    z2 = true;
                }
                searchField.showShadow(true, z2);
            } else {
                this.emojiSearchField.showShadow(holder.itemView.getTop() < this.emojiGridView.getPaddingTop(), !isLayout);
            }
            if (isLayout) {
                z = false;
            }
            showEmojiShadow(false, z);
        } else if (this.emojiSearchField != null && this.emojiGridView != null) {
            holder = this.emojiGridView.findViewHolderForAdapterPosition(0);
            if (holder != null) {
                this.emojiSearchField.setTranslationY((float) holder.itemView.getTop());
            } else {
                this.emojiSearchField.setTranslationY((float) (-this.searchFieldHeight));
            }
            searchField = this.emojiSearchField;
            if (isLayout) {
                z2 = false;
            } else {
                z2 = true;
            }
            searchField.showShadow(false, z2);
            if (holder == null || ((float) holder.itemView.getTop()) < ((float) (AndroidUtilities.dp(38.0f) - this.searchFieldHeight)) + this.emojiTabs.getTranslationY()) {
                z2 = true;
            } else {
                z2 = false;
            }
            if (isLayout) {
                z = false;
            }
            showEmojiShadow(z2, z);
        }
    }

    private void checkEmojiTabY(View list, int dy) {
        if (list == null) {
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.emojiTabs;
            this.emojiMinusDy = 0;
            scrollSlidingTabStrip.setTranslationY((float) null);
            this.emojiTabsShadow.setTranslationY((float) this.emojiMinusDy);
        } else if (list.getVisibility() != 0) {
        } else {
            if (this.delegate == null || !this.delegate.isSearchOpened()) {
                if (dy > 0 && this.emojiGridView != null && this.emojiGridView.getVisibility() == 0) {
                    ViewHolder holder = this.emojiGridView.findViewHolderForAdapterPosition(0);
                    if (holder != null) {
                        int i;
                        int top = holder.itemView.getTop();
                        if (this.needEmojiSearch) {
                            i = this.searchFieldHeight;
                        } else {
                            i = 0;
                        }
                        if (i + top >= this.emojiGridView.getPaddingTop()) {
                            return;
                        }
                    }
                }
                this.emojiMinusDy -= dy;
                if (this.emojiMinusDy > 0) {
                    this.emojiMinusDy = 0;
                } else if (this.emojiMinusDy < (-AndroidUtilities.dp(288.0f))) {
                    this.emojiMinusDy = -AndroidUtilities.dp(288.0f);
                }
                this.emojiTabs.setTranslationY((float) Math.max(-AndroidUtilities.dp(38.0f), this.emojiMinusDy));
                this.emojiTabsShadow.setTranslationY(this.emojiTabs.getTranslationY());
            }
        }
    }

    private void checkGifSearchFieldScroll(boolean isLayout) {
        boolean z = true;
        boolean z2 = false;
        if (!(this.gifGridView == null || this.gifGridView.getAdapter() != this.gifSearchAdapter || this.gifSearchAdapter.searchEndReached || this.gifSearchAdapter.reqId != 0 || this.gifSearchAdapter.results.isEmpty())) {
            int position = this.gifLayoutManager.findLastVisibleItemPosition();
            if (position != -1 && position > this.gifLayoutManager.getItemCount() - 5) {
                this.gifSearchAdapter.search(this.gifSearchAdapter.lastSearchImageString, this.gifSearchAdapter.nextSearchOffset, true);
            }
        }
        ViewHolder holder;
        SearchField searchField;
        if (this.delegate != null && this.delegate.isSearchOpened()) {
            holder = this.gifGridView.findViewHolderForAdapterPosition(0);
            if (holder == null) {
                searchField = this.gifSearchField;
                if (!isLayout) {
                    z2 = true;
                }
                searchField.showShadow(true, z2);
                return;
            }
            SearchField searchField2 = this.gifSearchField;
            boolean z3 = holder.itemView.getTop() < this.gifGridView.getPaddingTop();
            if (isLayout) {
                z = false;
            }
            searchField2.showShadow(z3, z);
        } else if (this.gifSearchField != null && this.gifGridView != null) {
            holder = this.gifGridView.findViewHolderForAdapterPosition(0);
            if (holder != null) {
                this.gifSearchField.setTranslationY((float) holder.itemView.getTop());
            } else {
                this.gifSearchField.setTranslationY((float) (-this.searchFieldHeight));
            }
            searchField = this.gifSearchField;
            if (isLayout) {
                z = false;
            }
            searchField.showShadow(false, z);
        }
    }

    private void checkScroll() {
        int firstVisibleItem = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (firstVisibleItem != -1 && this.stickersGridView != null) {
            int firstTab;
            if (this.favTabBum > 0) {
                firstTab = this.favTabBum;
            } else if (this.recentTabBum > 0) {
                firstTab = this.recentTabBum;
            } else {
                firstTab = this.stickersTabOffset;
            }
            this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(firstVisibleItem), firstTab);
        }
    }

    private void saveNewPage() {
        if (this.pager != null) {
            int newPage;
            int currentItem = this.pager.getCurrentItem();
            if (currentItem == 2) {
                newPage = 1;
            } else if (currentItem == 1) {
                newPage = 2;
            } else {
                newPage = 0;
            }
            if (this.currentPage != newPage) {
                this.currentPage = newPage;
                MessagesController.getGlobalEmojiSettings().edit().putInt("selected_page", newPage).commit();
            }
        }
    }

    public void clearRecentEmoji() {
        Emoji.clearRecentEmoji();
        this.emojiAdapter.notifyDataSetChanged();
    }

    private void showTrendingTab(boolean show) {
        if (show) {
            this.trendingGridView.setVisibility(0);
            this.stickersGridView.setVisibility(8);
            this.stickersSearchField.setVisibility(8);
            this.stickersTab.onPageScrolled(this.trendingTabNum, this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset);
            saveNewPage();
            return;
        }
        this.trendingGridView.setVisibility(8);
        this.stickersGridView.setVisibility(0);
        this.stickersSearchField.setVisibility(0);
    }

    private void onPageScrolled(int position, int width, int positionOffsetPixels) {
        int i = 2;
        if (this.delegate != null) {
            if (position == 1) {
                EmojiViewDelegate emojiViewDelegate = this.delegate;
                if (positionOffsetPixels == 0) {
                    i = 0;
                }
                emojiViewDelegate.onTabOpened(i);
            } else if (position == 2) {
                this.delegate.onTabOpened(3);
            } else {
                this.delegate.onTabOpened(0);
            }
        }
    }

    private void postBackspaceRunnable(int time) {
        AndroidUtilities.runOnUIThread(new EmojiView$$Lambda$7(this, time), (long) time);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$postBackspaceRunnable$8$EmojiView(int time) {
        if (this.backspacePressed) {
            if (this.delegate != null && this.delegate.onBackspace()) {
                this.backspaceButton.performHapticFeedback(3);
            }
            this.backspaceOnce = true;
            postBackspaceRunnable(Math.max(50, time - 100));
        }
    }

    public void switchToGifRecent() {
        showBackspaceButton(false, false);
        this.pager.setCurrentItem(1, false);
    }

    private void updateEmojiTabs() {
        int newHas;
        if (Emoji.recentEmoji.isEmpty()) {
            newHas = 0;
        } else {
            newHas = 1;
        }
        if (this.hasRecentEmoji == -1 || this.hasRecentEmoji != newHas) {
            this.hasRecentEmoji = newHas;
            this.emojiTabs.removeTabs();
            String[] descriptions = new String[]{LocaleController.getString("RecentStickers", NUM), LocaleController.getString("Emoji1", NUM), LocaleController.getString("Emoji2", NUM), LocaleController.getString("Emoji3", NUM), LocaleController.getString("Emoji4", NUM), LocaleController.getString("Emoji5", NUM), LocaleController.getString("Emoji6", NUM), LocaleController.getString("Emoji7", NUM), LocaleController.getString("Emoji8", NUM)};
            for (int a = 0; a < this.emojiIcons.length; a++) {
                if (a != 0 || !Emoji.recentEmoji.isEmpty()) {
                    this.emojiTabs.addIconTab(this.emojiIcons[a]).setContentDescription(descriptions[a]);
                }
            }
            this.emojiTabs.updateTabStyles();
        }
    }

    private void updateStickerTabs() {
        if (this.stickersTab != null) {
            int a;
            TL_messages_stickerSet pack;
            Chat chat;
            this.recentTabBum = -2;
            this.favTabBum = -2;
            this.trendingTabNum = -2;
            this.stickersTabOffset = 0;
            int lastPosition = this.stickersTab.getCurrentPosition();
            this.stickersTab.removeTabs();
            ArrayList<Long> unread = DataQuery.getInstance(this.currentAccount).getUnreadStickerSets();
            boolean hasStickers = false;
            if (!(this.trendingGridAdapter == null || this.trendingGridAdapter.getItemCount() == 0 || unread.isEmpty())) {
                this.stickersCounter = this.stickersTab.addIconTabWithCounter(this.stickerIcons[2]);
                this.trendingTabNum = this.stickersTabOffset;
                this.stickersTabOffset++;
                this.stickersCounter.setText(String.format("%d", new Object[]{Integer.valueOf(unread.size())}));
            }
            if (!this.favouriteStickers.isEmpty()) {
                this.favTabBum = this.stickersTabOffset;
                this.stickersTabOffset++;
                this.stickersTab.addIconTab(this.stickerIcons[1]).setContentDescription(LocaleController.getString("FavoriteStickers", NUM));
                hasStickers = true;
            }
            if (!this.recentStickers.isEmpty()) {
                this.recentTabBum = this.stickersTabOffset;
                this.stickersTabOffset++;
                this.stickersTab.addIconTab(this.stickerIcons[0]).setContentDescription(LocaleController.getString("RecentStickers", NUM));
                hasStickers = true;
            }
            this.stickerSets.clear();
            this.groupStickerSet = null;
            this.groupStickerPackPosition = -1;
            this.groupStickerPackNum = -10;
            ArrayList<TL_messages_stickerSet> packs = DataQuery.getInstance(this.currentAccount).getStickerSets(0);
            for (a = 0; a < packs.size(); a++) {
                pack = (TL_messages_stickerSet) packs.get(a);
                if (!(pack.set.archived || pack.documents == null || pack.documents.isEmpty())) {
                    this.stickerSets.add(pack);
                    hasStickers = true;
                }
            }
            if (this.info != null) {
                long hiddenStickerSetId = MessagesController.getEmojiSettings(this.currentAccount).getLong("group_hide_stickers_" + this.info.id, -1);
                chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.info.id));
                if (chat == null || this.info.stickerset == null || !ChatObject.hasAdminRights(chat)) {
                    this.groupStickersHidden = hiddenStickerSetId != -1;
                } else if (this.info.stickerset != null) {
                    this.groupStickersHidden = hiddenStickerSetId == this.info.stickerset.id;
                }
                if (this.info.stickerset != null) {
                    pack = DataQuery.getInstance(this.currentAccount).getGroupStickerSetById(this.info.stickerset);
                    if (!(pack == null || pack.documents == null || pack.documents.isEmpty() || pack.set == null)) {
                        TL_messages_stickerSet set = new TL_messages_stickerSet();
                        set.documents = pack.documents;
                        set.packs = pack.packs;
                        set.set = pack.set;
                        if (this.groupStickersHidden) {
                            this.groupStickerPackNum = this.stickerSets.size();
                            this.stickerSets.add(set);
                        } else {
                            this.groupStickerPackNum = 0;
                            this.stickerSets.add(0, set);
                        }
                        if (!this.info.can_set_stickers) {
                            set = null;
                        }
                        this.groupStickerSet = set;
                    }
                } else if (this.info.can_set_stickers) {
                    pack = new TL_messages_stickerSet();
                    if (this.groupStickersHidden) {
                        this.groupStickerPackNum = this.stickerSets.size();
                        this.stickerSets.add(pack);
                    } else {
                        this.groupStickerPackNum = 0;
                        this.stickerSets.add(0, pack);
                    }
                }
            }
            a = 0;
            while (a < this.stickerSets.size()) {
                if (a == this.groupStickerPackNum) {
                    chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.info.id));
                    if (chat == null) {
                        this.stickerSets.remove(0);
                        a--;
                    } else {
                        this.stickersTab.addStickerTab(chat);
                        hasStickers = true;
                    }
                } else {
                    TLObject thumb;
                    TL_messages_stickerSet stickerSet = (TL_messages_stickerSet) this.stickerSets.get(a);
                    if (stickerSet.set.thumb instanceof TL_photoSize) {
                        thumb = stickerSet.set.thumb;
                    } else {
                        thumb = (TLObject) stickerSet.documents.get(0);
                    }
                    this.stickersTab.addStickerTab(thumb, stickerSet).setContentDescription(stickerSet.set.title + ", " + LocaleController.getString("AccDescrStickerSet", NUM));
                    hasStickers = true;
                }
                a++;
            }
            if (!(this.trendingGridAdapter == null || this.trendingGridAdapter.getItemCount() == 0 || !unread.isEmpty())) {
                this.trendingTabNum = this.stickersTabOffset + this.stickerSets.size();
                this.stickersTab.addIconTab(this.stickerIcons[2]).setContentDescription(LocaleController.getString("FeaturedStickers", NUM));
            }
            Drawable drawable = getContext().getResources().getDrawable(NUM);
            Theme.setDrawableColorByKey(drawable, "chat_emojiBottomPanelIcon");
            this.stickersTab.addIconTab(drawable).setContentDescription(LocaleController.getString("Settings", NUM));
            this.stickersTab.updateTabStyles();
            if (lastPosition != 0) {
                this.stickersTab.onPageScrolled(lastPosition, lastPosition);
            }
            checkPanels();
            if (hasStickers || this.trendingTabNum < 0) {
                if (this.scrolledToTrending) {
                    showTrendingTab(false);
                    checkScroll();
                }
            } else if (!this.scrolledToTrending) {
                showTrendingTab(true);
                this.scrolledToTrending = true;
            }
        }
    }

    private void checkPanels() {
        if (this.stickersTab != null) {
            if (this.trendingTabNum == -2 && this.trendingGridView != null && this.trendingGridView.getVisibility() == 0) {
                this.trendingGridView.setVisibility(8);
                this.stickersGridView.setVisibility(0);
                this.stickersSearchField.setVisibility(0);
            }
            if (this.trendingGridView == null || this.trendingGridView.getVisibility() != 0) {
                int position = this.stickersLayoutManager.findFirstVisibleItemPosition();
                if (position != -1) {
                    int firstTab;
                    if (this.favTabBum > 0) {
                        firstTab = this.favTabBum;
                    } else if (this.recentTabBum > 0) {
                        firstTab = this.recentTabBum;
                    } else {
                        firstTab = this.stickersTabOffset;
                    }
                    this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(position), firstTab);
                    return;
                }
                return;
            }
            this.stickersTab.onPageScrolled(this.trendingTabNum, this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset);
        }
    }

    public void addRecentSticker(Document document) {
        if (document != null) {
            DataQuery.getInstance(this.currentAccount).addRecentSticker(0, null, document, (int) (System.currentTimeMillis() / 1000), false);
            boolean wasEmpty = this.recentStickers.isEmpty();
            this.recentStickers = DataQuery.getInstance(this.currentAccount).getRecentStickers(0);
            if (this.stickersGridAdapter != null) {
                this.stickersGridAdapter.notifyDataSetChanged();
            }
            if (wasEmpty) {
                updateStickerTabs();
            }
        }
    }

    public void addRecentGif(Document document) {
        if (document != null) {
            boolean wasEmpty = this.recentGifs.isEmpty();
            this.recentGifs = DataQuery.getInstance(this.currentAccount).getRecentGifs();
            if (this.gifAdapter != null) {
                this.gifAdapter.notifyDataSetChanged();
            }
            if (wasEmpty) {
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
        int a;
        if (AndroidUtilities.isInMultiwindow || this.forseMultiwindowLayout) {
            Drawable background = getBackground();
            if (background != null) {
                background.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelBackground"), Mode.MULTIPLY));
            }
        } else {
            setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
            if (this.needEmojiSearch) {
                this.bottomTabContainerBackground.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
            }
        }
        if (this.emojiTabs != null) {
            this.emojiTabs.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
            this.emojiTabsShadow.setBackgroundColor(Theme.getColor("chat_emojiPanelShadowLine"));
        }
        for (a = 0; a < 3; a++) {
            SearchField searchField;
            if (a == 0) {
                searchField = this.stickersSearchField;
            } else if (a == 1) {
                searchField = this.emojiSearchField;
            } else {
                searchField = this.gifSearchField;
            }
            if (searchField != null) {
                searchField.backgroundView.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
                searchField.shadowView.setBackgroundColor(Theme.getColor("chat_emojiPanelShadowLine"));
                searchField.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiSearchIcon"), Mode.MULTIPLY));
                searchField.searchIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiSearchIcon"), Mode.MULTIPLY));
                Theme.setDrawableColorByKey(searchField.searchBackground.getBackground(), "chat_emojiSearchBackground");
                searchField.searchBackground.invalidate();
                searchField.searchEditText.setHintTextColor(Theme.getColor("chat_emojiSearchIcon"));
                searchField.searchEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            }
        }
        if (this.dotPaint != null) {
            this.dotPaint.setColor(Theme.getColor("chat_emojiPanelNewTrending"));
        }
        if (this.emojiGridView != null) {
            this.emojiGridView.setGlowColor(Theme.getColor("chat_emojiPanelBackground"));
        }
        if (this.stickersGridView != null) {
            this.stickersGridView.setGlowColor(Theme.getColor("chat_emojiPanelBackground"));
        }
        if (this.trendingGridView != null) {
            this.trendingGridView.setGlowColor(Theme.getColor("chat_emojiPanelBackground"));
        }
        if (this.stickersTab != null) {
            this.stickersTab.setIndicatorColor(Theme.getColor("chat_emojiPanelStickerPackSelectorLine"));
            this.stickersTab.setUnderlineColor(Theme.getColor("chat_emojiPanelShadowLine"));
            this.stickersTab.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
        }
        if (this.backspaceButton != null) {
            this.backspaceButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelBackspace"), Mode.MULTIPLY));
        }
        if (this.searchButton != null) {
            this.searchButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelBackspace"), Mode.MULTIPLY));
        }
        if (this.shadowLine != null) {
            this.shadowLine.setBackgroundColor(Theme.getColor("chat_emojiPanelShadowLine"));
        }
        if (this.mediaBanTooltip != null) {
            ((ShapeDrawable) this.mediaBanTooltip.getBackground()).getPaint().setColor(Theme.getColor("chat_gifSaveHintBackground"));
            this.mediaBanTooltip.setTextColor(Theme.getColor("chat_gifSaveHintText"));
        }
        if (this.stickersCounter != null) {
            this.stickersCounter.setTextColor(Theme.getColor("chat_emojiPanelBadgeText"));
            Theme.setDrawableColor(this.stickersCounter.getBackground(), Theme.getColor("chat_emojiPanelBadgeBackground"));
            this.stickersCounter.invalidate();
        }
        for (a = 0; a < this.tabIcons.length; a++) {
            Theme.setEmojiDrawableColor(this.tabIcons[a], Theme.getColor("chat_emojiBottomPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.tabIcons[a], Theme.getColor("chat_emojiPanelIconSelected"), true);
        }
        for (a = 0; a < this.emojiIcons.length; a++) {
            Theme.setEmojiDrawableColor(this.emojiIcons[a], Theme.getColor("chat_emojiPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.emojiIcons[a], Theme.getColor("chat_emojiPanelIconSelected"), true);
        }
        for (a = 0; a < this.stickerIcons.length; a++) {
            Theme.setEmojiDrawableColor(this.stickerIcons[a], Theme.getColor("chat_emojiPanelIcon"), false);
            Theme.setEmojiDrawableColor(this.stickerIcons[a], Theme.getColor("chat_emojiPanelIconSelected"), true);
        }
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.isLayout = true;
        if (AndroidUtilities.isInMultiwindow || this.forseMultiwindowLayout) {
            if (this.currentBackgroundType != 1) {
                if (VERSION.SDK_INT >= 21) {
                    setOutlineProvider((ViewOutlineProvider) this.outlineProvider);
                    setClipToOutline(true);
                    setElevation((float) AndroidUtilities.dp(2.0f));
                }
                setBackgroundResource(NUM);
                getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelBackground"), Mode.MULTIPLY));
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
            setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
            if (this.needEmojiSearch) {
                this.bottomTabContainerBackground.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
            }
            this.currentBackgroundType = 0;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), NUM));
        this.isLayout = false;
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.lastNotifyWidth != right - left) {
            this.lastNotifyWidth = right - left;
            reloadStickersAdapter();
        }
        View parent = (View) getParent();
        if (parent != null) {
            int newHeight = bottom - top;
            int newHeight2 = parent.getHeight();
            if (!(this.lastNotifyHeight == newHeight && this.lastNotifyHeight2 == newHeight2)) {
                if (this.delegate != null && this.delegate.isSearchOpened()) {
                    this.bottomTabContainer.setTranslationY((float) AndroidUtilities.dp(49.0f));
                } else if (this.bottomTabContainer.getTag() == null) {
                    if (newHeight < this.lastNotifyHeight) {
                        this.bottomTabContainer.setTranslationY(0.0f);
                    } else {
                        this.bottomTabContainer.setTranslationY(-((getY() + ((float) getMeasuredHeight())) - ((float) parent.getHeight())));
                    }
                }
                this.lastNotifyHeight = newHeight;
                this.lastNotifyHeight2 = newHeight2;
            }
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    private void reloadStickersAdapter() {
        if (this.stickersGridAdapter != null) {
            this.stickersGridAdapter.notifyDataSetChanged();
        }
        if (this.trendingGridAdapter != null) {
            this.trendingGridAdapter.notifyDataSetChanged();
        }
        if (this.stickersSearchGridAdapter != null) {
            this.stickersSearchGridAdapter.notifyDataSetChanged();
        }
        if (ContentPreviewViewer.getInstance().isVisible()) {
            ContentPreviewViewer.getInstance().close();
        }
        ContentPreviewViewer.getInstance().reset();
    }

    public void setDelegate(EmojiViewDelegate emojiViewDelegate) {
        this.delegate = emojiViewDelegate;
    }

    public void setDragListener(DragListener listener) {
        this.dragListener = listener;
    }

    public void setChatInfo(ChatFull chatInfo) {
        this.info = chatInfo;
        updateStickerTabs();
    }

    public void invalidateViews() {
        this.emojiGridView.invalidateViews();
    }

    public void setForseMultiwindowLayout(boolean value) {
        this.forseMultiwindowLayout = value;
    }

    public void onOpen(boolean forceEmoji) {
        boolean z = true;
        if (this.stickersTab != null) {
            if (!(this.currentPage == 0 || this.currentChatId == 0)) {
                this.currentPage = 0;
            }
            if (this.currentPage == 0 || forceEmoji) {
                showBackspaceButton(true, false);
                if (this.pager.getCurrentItem() != 0) {
                    ViewPager viewPager = this.pager;
                    if (forceEmoji) {
                        z = false;
                    }
                    viewPager.setCurrentItem(0, z);
                }
            } else if (this.currentPage == 1) {
                showBackspaceButton(false, false);
                if (this.pager.getCurrentItem() != 2) {
                    this.pager.setCurrentItem(2, false);
                }
                if (this.recentTabBum >= 0) {
                    this.stickersTab.selectTab(this.recentTabBum);
                } else if (this.favTabBum >= 0) {
                    this.stickersTab.selectTab(this.favTabBum);
                } else {
                    this.stickersTab.selectTab(0);
                }
            } else if (this.currentPage == 2) {
                showBackspaceButton(false, false);
                if (this.pager.getCurrentItem() != 1) {
                    this.pager.setCurrentItem(1, false);
                }
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
            AndroidUtilities.runOnUIThread(new EmojiView$$Lambda$8(this));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onAttachedToWindow$9$EmojiView() {
        updateStickerTabs();
        reloadStickersAdapter();
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != 8) {
            Emoji.sortEmoji();
            this.emojiAdapter.notifyDataSetChanged();
            if (this.stickersGridAdapter != null) {
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
                updateStickerTabs();
                reloadStickersAdapter();
            }
            if (this.trendingGridAdapter != null) {
                this.trendingLoaded = false;
                this.trendingGridAdapter.notifyDataSetChanged();
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
        if (this.pickerViewPopup != null && this.pickerViewPopup.isShowing()) {
            this.pickerViewPopup.dismiss();
        }
    }

    private void checkDocuments(boolean isGif) {
        if (isGif) {
            this.recentGifs = DataQuery.getInstance(this.currentAccount).getRecentGifs();
            if (this.gifAdapter != null) {
                this.gifAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        int previousCount = this.recentStickers.size();
        int previousCount2 = this.favouriteStickers.size();
        this.recentStickers = DataQuery.getInstance(this.currentAccount).getRecentStickers(0);
        this.favouriteStickers = DataQuery.getInstance(this.currentAccount).getRecentStickers(2);
        for (int a = 0; a < this.favouriteStickers.size(); a++) {
            Document favSticker = (Document) this.favouriteStickers.get(a);
            for (int b = 0; b < this.recentStickers.size(); b++) {
                Document recSticker = (Document) this.recentStickers.get(b);
                if (recSticker.dc_id == favSticker.dc_id && recSticker.id == favSticker.id) {
                    this.recentStickers.remove(b);
                    break;
                }
            }
        }
        if (!(previousCount == this.recentStickers.size() && previousCount2 == this.favouriteStickers.size())) {
            updateStickerTabs();
        }
        if (this.stickersGridAdapter != null) {
            this.stickersGridAdapter.notifyDataSetChanged();
        }
        checkPanels();
    }

    public void setStickersBanned(boolean value, int chatId) {
        if (this.typeTabs != null) {
            if (value) {
                this.currentChatId = chatId;
            } else {
                this.currentChatId = 0;
            }
            View view = this.typeTabs.getTab(2);
            if (view != null) {
                view.setAlpha(this.currentChatId != 0 ? 0.5f : 1.0f);
                if (this.currentChatId != 0 && this.pager.getCurrentItem() != 0) {
                    showBackspaceButton(true, true);
                    this.pager.setCurrentItem(0, false);
                }
            }
        }
    }

    public void showStickerBanHint(boolean gif) {
        if (this.mediaBanTooltip.getVisibility() != 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.currentChatId));
            if (chat != null) {
                if (ChatObject.hasAdminRights(chat) || chat.default_banned_rights == null || !chat.default_banned_rights.send_stickers) {
                    if (chat.banned_rights == null) {
                        return;
                    }
                    if (AndroidUtilities.isBannedForever(chat.banned_rights)) {
                        if (gif) {
                            this.mediaBanTooltip.setText(LocaleController.getString("AttachGifRestrictedForever", NUM));
                        } else {
                            this.mediaBanTooltip.setText(LocaleController.getString("AttachStickersRestrictedForever", NUM));
                        }
                    } else if (gif) {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachGifRestricted", NUM, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
                    } else {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachStickersRestricted", NUM, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
                    }
                } else if (gif) {
                    this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachGifRestricted", NUM));
                } else {
                    this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachStickersRestricted", NUM));
                }
                this.mediaBanTooltip.setVisibility(0);
                AnimatorSet AnimatorSet = new AnimatorSet();
                AnimatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mediaBanTooltip, View.ALPHA, new float[]{0.0f, 1.0f})});
                AnimatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        AndroidUtilities.runOnUIThread(new EmojiView$31$$Lambda$0(this), 5000);
                    }

                    /* Access modifiers changed, original: final|synthetic */
                    public final /* synthetic */ void lambda$onAnimationEnd$0$EmojiView$31() {
                        if (EmojiView.this.mediaBanTooltip != null) {
                            AnimatorSet AnimatorSet1 = new AnimatorSet();
                            Animator[] animatorArr = new Animator[1];
                            animatorArr[0] = ObjectAnimator.ofFloat(EmojiView.this.mediaBanTooltip, View.ALPHA, new float[]{0.0f});
                            AnimatorSet1.playTogether(animatorArr);
                            AnimatorSet1.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation1) {
                                    if (EmojiView.this.mediaBanTooltip != null) {
                                        EmojiView.this.mediaBanTooltip.setVisibility(4);
                                    }
                                }
                            });
                            AnimatorSet1.setDuration(300);
                            AnimatorSet1.start();
                        }
                    }
                });
                AnimatorSet.setDuration(300);
                AnimatorSet.start();
            }
        }
    }

    private void updateVisibleTrendingSets() {
        if (this.trendingGridAdapter != null && this.trendingGridAdapter != null) {
            for (int b = 0; b < 2; b++) {
                RecyclerListView gridView;
                if (b == 0) {
                    try {
                        gridView = this.trendingGridView;
                    } catch (Exception e) {
                        FileLog.e(e);
                        return;
                    }
                }
                gridView = this.stickersGridView;
                int count = gridView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = gridView.getChildAt(a);
                    if ((child instanceof FeaturedStickerSetInfoCell) && ((Holder) gridView.getChildViewHolder(child)) != null) {
                        FeaturedStickerSetInfoCell cell = (FeaturedStickerSetInfoCell) child;
                        ArrayList<Long> unreadStickers = DataQuery.getInstance(this.currentAccount).getUnreadStickerSets();
                        StickerSetCovered stickerSetCovered = cell.getStickerSet();
                        boolean unread = unreadStickers != null && unreadStickers.contains(Long.valueOf(stickerSetCovered.set.id));
                        cell.setStickerSet(stickerSetCovered, unread);
                        if (unread) {
                            DataQuery.getInstance(this.currentAccount).markFaturedStickersByIdAsRead(stickerSetCovered.set.id);
                        }
                        boolean installing = this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                        boolean removing = this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                        if (installing || removing) {
                            if (installing && cell.isInstalled()) {
                                this.installingStickerSets.remove(stickerSetCovered.set.id);
                                installing = false;
                            } else if (removing) {
                                if (!cell.isInstalled()) {
                                    this.removingStickerSets.remove(stickerSetCovered.set.id);
                                    removing = false;
                                }
                            }
                        }
                        boolean z = installing || removing;
                        cell.setDrawProgress(z);
                    }
                }
            }
        }
    }

    public boolean areThereAnyStickers() {
        return this.stickersGridAdapter != null && this.stickersGridAdapter.getItemCount() > 0;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int count;
        int a;
        if (id == NotificationCenter.stickersDidLoad) {
            if (((Integer) args[0]).intValue() == 0) {
                if (this.trendingGridAdapter != null) {
                    if (this.trendingLoaded) {
                        updateVisibleTrendingSets();
                    } else {
                        this.trendingGridAdapter.notifyDataSetChanged();
                    }
                }
                updateStickerTabs();
                reloadStickersAdapter();
                checkPanels();
            }
        } else if (id == NotificationCenter.recentDocumentsDidLoad) {
            boolean isGif = ((Boolean) args[0]).booleanValue();
            int type = ((Integer) args[1]).intValue();
            if (isGif || type == 0 || type == 2) {
                checkDocuments(isGif);
            }
        } else if (id == NotificationCenter.featuredStickersDidLoad) {
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
            if (this.typeTabs != null) {
                count = this.typeTabs.getChildCount();
                for (a = 0; a < count; a++) {
                    this.typeTabs.getChildAt(a).invalidate();
                }
            }
            updateStickerTabs();
        } else if (id == NotificationCenter.groupStickersDidLoad) {
            if (this.info != null && this.info.stickerset != null && this.info.stickerset.id == ((Long) args[0]).longValue()) {
                updateStickerTabs();
            }
        } else if (id == NotificationCenter.emojiDidLoad) {
            if (this.stickersGridView != null) {
                count = this.stickersGridView.getChildCount();
                for (a = 0; a < count; a++) {
                    View child = this.stickersGridView.getChildAt(a);
                    if ((child instanceof StickerSetNameCell) || (child instanceof StickerEmojiCell)) {
                        child.invalidate();
                    }
                }
            }
        } else if (id != NotificationCenter.newEmojiSuggestionsAvailable || this.emojiGridView == null || !this.needEmojiSearch) {
        } else {
            if ((this.emojiSearchField.progressDrawable.isAnimating() || this.emojiGridView.getAdapter() == this.emojiSearchAdapter) && !TextUtils.isEmpty(this.emojiSearchAdapter.lastSearchEmojiString)) {
                this.emojiSearchAdapter.search(this.emojiSearchAdapter.lastSearchEmojiString);
            }
        }
    }
}
