package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.EmojiSuggestion;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_foundStickerSets;
import org.telegram.tgnet.TLRPC.TL_messages_getStickers;
import org.telegram.tgnet.TLRPC.TL_messages_searchStickerSets;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_stickers;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
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
import org.telegram.ui.StickerPreviewViewer;
import org.telegram.ui.StickerPreviewViewer.StickerPreviewViewerDelegate;

public class EmojiView extends FrameLayout implements NotificationCenterDelegate {
    private static final OnScrollChangedListener NOP = new C11452();
    private static final Field superListenerField;
    private ArrayList<EmojiGridAdapter> adapters = new ArrayList();
    private ImageView backspaceButton;
    private boolean backspaceOnce;
    private boolean backspacePressed;
    private ImageView clearSearchImageView;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentBackgroundType = -1;
    private int currentChatId;
    private int currentPage;
    private Paint dotPaint;
    private DragListener dragListener;
    private ArrayList<GridView> emojiGrids = new ArrayList();
    private int emojiSize;
    private LinearLayout emojiTab;
    private int favTabBum = -2;
    private ArrayList<Document> favouriteStickers = new ArrayList();
    private int featuredStickersHash;
    private boolean firstAttach = true;
    private ExtendedGridLayoutManager flowLayoutManager;
    private int gifTabNum = -2;
    private GifsAdapter gifsAdapter;
    private RecyclerListView gifsGridView;
    private int groupStickerPackNum;
    private int groupStickerPackPosition;
    private TL_messages_stickerSet groupStickerSet;
    private boolean groupStickersHidden;
    private Drawable[] icons;
    private ChatFull info;
    private LongSparseArray<StickerSetCovered> installingStickerSets = new LongSparseArray();
    private boolean isLayout;
    private int lastNotifyWidth;
    private Listener listener;
    private int[] location = new int[2];
    private TextView mediaBanTooltip;
    private int minusDy;
    private TextView noRecentTextView;
    private int oldWidth;
    private Object outlineProvider;
    private ViewPager pager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private EmojiColorPickerView pickerView;
    private EmojiPopupWindow pickerViewPopup;
    private int popupHeight;
    private int popupWidth;
    private CloseProgressDrawable2 progressDrawable;
    private ArrayList<Document> recentGifs = new ArrayList();
    private ArrayList<Document> recentStickers = new ArrayList();
    private int recentTabBum = -2;
    private LongSparseArray<StickerSetCovered> removingStickerSets = new LongSparseArray();
    private AnimatorSet searchAnimation;
    private View searchBackground;
    private EditTextBoldCursor searchEditText;
    private FrameLayout searchEditTextContainer;
    private int searchFieldHeight = AndroidUtilities.dp(64.0f);
    private ImageView searchIconImageView;
    private View shadowLine;
    private boolean showGifs;
    private StickerPreviewViewerDelegate stickerPreviewViewerDelegate = new C20521();
    private ArrayList<TL_messages_stickerSet> stickerSets = new ArrayList();
    private Drawable stickersDrawable;
    private TextView stickersEmptyView;
    private StickersGridAdapter stickersGridAdapter;
    private RecyclerListView stickersGridView;
    private GridLayoutManager stickersLayoutManager;
    private OnItemClickListener stickersOnItemClickListener;
    private StickersSearchGridAdapter stickersSearchGridAdapter;
    private ScrollSlidingTabStrip stickersTab;
    private int stickersTabOffset;
    private FrameLayout stickersWrap;
    private boolean switchToGifTab;
    private TrendingGridAdapter trendingGridAdapter;
    private RecyclerListView trendingGridView;
    private GridLayoutManager trendingLayoutManager;
    private boolean trendingLoaded;
    private int trendingTabNum = -2;
    private ArrayList<View> views = new ArrayList();

    /* renamed from: org.telegram.ui.Components.EmojiView$2 */
    static class C11452 implements OnScrollChangedListener {
        public void onScrollChanged() {
        }

        C11452() {
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView$3 */
    class C11483 extends ViewOutlineProvider {
        C11483() {
        }

        @TargetApi(21)
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(view.getPaddingLeft(), view.getPaddingTop(), view.getMeasuredWidth() - view.getPaddingRight(), view.getMeasuredHeight() - view.getPaddingBottom(), (float) AndroidUtilities.dp(6.0f));
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView$6 */
    class C11496 implements OnTouchListener {
        C11496() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            return StickerPreviewViewer.getInstance().onTouch(motionEvent, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.stickersOnItemClickListener, EmojiView.this.stickerPreviewViewerDelegate);
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView$8 */
    class C11508 implements OnClickListener {
        C11508() {
        }

        public void onClick(View view) {
            EmojiView.this.searchEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
            AndroidUtilities.showKeyboard(EmojiView.this.searchEditText);
        }
    }

    public interface DragListener {
        void onDrag(int i);

        void onDragCancel();

        void onDragEnd(float f);

        void onDragStart();
    }

    private class EmojiColorPickerView extends View {
        private Drawable arrowDrawable = getResources().getDrawable(C0446R.drawable.stickers_back_arrow);
        private int arrowX;
        private Drawable backgroundDrawable = getResources().getDrawable(C0446R.drawable.stickers_back_all);
        private String currentEmoji;
        private RectF rect = new RectF();
        private Paint rectPaint = new Paint(1);
        private int selection;

        public void setEmoji(String str, int i) {
            this.currentEmoji = str;
            this.arrowX = i;
            this.rectPaint.setColor(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR);
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

        protected void onDraw(Canvas canvas) {
            int i = 0;
            this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(AndroidUtilities.isTablet() ? 60.0f : 52.0f));
            this.backgroundDrawable.draw(canvas);
            Drawable drawable = this.arrowDrawable;
            int dp = this.arrowX - AndroidUtilities.dp(9.0f);
            float f = 47.5f;
            int dp2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 55.5f : 47.5f);
            int dp3 = this.arrowX + AndroidUtilities.dp(9.0f);
            if (AndroidUtilities.isTablet()) {
                f = 55.5f;
            }
            drawable.setBounds(dp, dp2, dp3, AndroidUtilities.dp(f + 8.0f));
            this.arrowDrawable.draw(canvas);
            if (this.currentEmoji != null) {
                while (i < 6) {
                    int access$900 = (EmojiView.this.emojiSize * i) + AndroidUtilities.dp((float) (5 + (4 * i)));
                    dp = AndroidUtilities.dp(9.0f);
                    if (this.selection == i) {
                        this.rect.set((float) access$900, (float) (dp - ((int) AndroidUtilities.dpf2(3.5f))), (float) (EmojiView.this.emojiSize + access$900), (float) ((EmojiView.this.emojiSize + dp) + AndroidUtilities.dp(3.0f)));
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), this.rectPaint);
                    }
                    String str = this.currentEmoji;
                    if (i != 0) {
                        String str2;
                        switch (i) {
                            case 1:
                                str2 = "\ud83c\udffb";
                                break;
                            case 2:
                                str2 = "\ud83c\udffc";
                                break;
                            case 3:
                                str2 = "\ud83c\udffd";
                                break;
                            case 4:
                                str2 = "\ud83c\udffe";
                                break;
                            case 5:
                                str2 = "\ud83c\udfff";
                                break;
                            default:
                                str2 = TtmlNode.ANONYMOUS_REGION_ID;
                                break;
                        }
                        str = EmojiView.addColorToCode(str, str2);
                    }
                    Drawable emojiBigDrawable = Emoji.getEmojiBigDrawable(str);
                    if (emojiBigDrawable != null) {
                        emojiBigDrawable.setBounds(access$900, dp, EmojiView.this.emojiSize + access$900, EmojiView.this.emojiSize + dp);
                        emojiBigDrawable.draw(canvas);
                    }
                    i++;
                }
            }
        }
    }

    private class EmojiGridAdapter extends BaseAdapter {
        private int emojiPage;

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public EmojiGridAdapter(int i) {
            this.emojiPage = i;
        }

        public int getCount() {
            if (this.emojiPage == -1) {
                return Emoji.recentEmoji.size();
            }
            return EmojiData.dataColored[this.emojiPage].length;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            view = (ImageViewEmoji) view;
            if (view == null) {
                view = new ImageViewEmoji(EmojiView.this.getContext());
            }
            if (this.emojiPage == -1) {
                i = (String) Emoji.recentEmoji.get(i);
            } else {
                i = EmojiData.dataColored[this.emojiPage][i];
                String str = (String) Emoji.emojiColor.get(i);
                if (str != null) {
                    ViewGroup access$1400 = EmojiView.addColorToCode(i, str);
                    viewGroup = i;
                    i = access$1400;
                    view.setImageDrawable(Emoji.getEmojiBigDrawable(i));
                    view.setTag(viewGroup);
                    return view;
                }
            }
            viewGroup = i;
            view.setImageDrawable(Emoji.getEmojiBigDrawable(i));
            view.setTag(viewGroup);
            return view;
        }

        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            if (dataSetObserver != null) {
                super.unregisterDataSetObserver(dataSetObserver);
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
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r2 = this;
            r0 = org.telegram.ui.Components.EmojiView.superListenerField;
            if (r0 == 0) goto L_0x0021;
        L_0x0006:
            r0 = org.telegram.ui.Components.EmojiView.superListenerField;	 Catch:{ Exception -> 0x001e }
            r0 = r0.get(r2);	 Catch:{ Exception -> 0x001e }
            r0 = (android.view.ViewTreeObserver.OnScrollChangedListener) r0;	 Catch:{ Exception -> 0x001e }
            r2.mSuperScrollListener = r0;	 Catch:{ Exception -> 0x001e }
            r0 = org.telegram.ui.Components.EmojiView.superListenerField;	 Catch:{ Exception -> 0x001e }
            r1 = org.telegram.ui.Components.EmojiView.NOP;	 Catch:{ Exception -> 0x001e }
            r0.set(r2, r1);	 Catch:{ Exception -> 0x001e }
            goto L_0x0021;
        L_0x001e:
            r0 = 0;
            r2.mSuperScrollListener = r0;
        L_0x0021:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiPopupWindow.init():void");
        }

        private void unregisterListener() {
            if (this.mSuperScrollListener != null && this.mViewTreeObserver != null) {
                if (this.mViewTreeObserver.isAlive()) {
                    this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                }
                this.mViewTreeObserver = null;
            }
        }

        private void registerListener(View view) {
            if (this.mSuperScrollListener != null) {
                view = view.getWindowToken() != null ? view.getViewTreeObserver() : null;
                if (view != this.mViewTreeObserver) {
                    if (this.mViewTreeObserver != null && this.mViewTreeObserver.isAlive()) {
                        this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                    }
                    this.mViewTreeObserver = view;
                    if (view != null) {
                        view.addOnScrollChangedListener(this.mSuperScrollListener);
                    }
                }
            }
        }

        public void showAsDropDown(View view, int i, int i2) {
            try {
                super.showAsDropDown(view, i, i2);
                registerListener(view);
            } catch (Throwable e) {
                FileLog.m3e(e);
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
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r1 = this;
            r0 = 0;
            r1.setFocusable(r0);
            super.dismiss();	 Catch:{ Exception -> 0x0007 }
        L_0x0007:
            r1.unregisterListener();
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiPopupWindow.dismiss():void");
        }
    }

    private class ImageViewEmoji extends ImageView {
        private float lastX;
        private float lastY;
        private boolean touched;
        private float touchedX;
        private float touchedY;

        public ImageViewEmoji(Context context) {
            super(context);
            setOnClickListener(new OnClickListener(EmojiView.this) {
                public void onClick(View view) {
                    ImageViewEmoji.this.sendEmoji(null);
                }
            });
            setOnLongClickListener(new OnLongClickListener(EmojiView.this) {
                public boolean onLongClick(View view) {
                    String str = (String) view.getTag();
                    int i = 0;
                    if (EmojiData.emojiColoredMap.containsKey(str)) {
                        ImageViewEmoji.this.touched = true;
                        ImageViewEmoji.this.touchedX = ImageViewEmoji.this.lastX;
                        ImageViewEmoji.this.touchedY = ImageViewEmoji.this.lastY;
                        String str2 = (String) Emoji.emojiColor.get(str);
                        int i2 = 5;
                        if (str2 != null) {
                            int i3 = -1;
                            switch (str2.hashCode()) {
                                case 1773375:
                                    if (str2.equals("\ud83c\udffb")) {
                                        i3 = 0;
                                        break;
                                    }
                                    break;
                                case 1773376:
                                    if (str2.equals("\ud83c\udffc")) {
                                        i3 = true;
                                        break;
                                    }
                                    break;
                                case 1773377:
                                    if (str2.equals("\ud83c\udffd")) {
                                        i3 = 2;
                                        break;
                                    }
                                    break;
                                case 1773378:
                                    if (str2.equals("\ud83c\udffe")) {
                                        i3 = 3;
                                        break;
                                    }
                                    break;
                                case 1773379:
                                    if (str2.equals("\ud83c\udfff")) {
                                        i3 = 4;
                                        break;
                                    }
                                    break;
                                default:
                                    break;
                            }
                            switch (i3) {
                                case 0:
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
                                default:
                                    break;
                            }
                        }
                        EmojiView.this.pickerView.setSelection(0);
                        view.getLocationOnScreen(EmojiView.this.location);
                        int access$900 = EmojiView.this.emojiSize * EmojiView.this.pickerView.getSelection();
                        int selection = 4 * EmojiView.this.pickerView.getSelection();
                        if (!AndroidUtilities.isTablet()) {
                            i2 = 1;
                        }
                        access$900 += AndroidUtilities.dp((float) (selection - i2));
                        if (EmojiView.this.location[0] - access$900 < AndroidUtilities.dp(5.0f)) {
                            access$900 += (EmojiView.this.location[0] - access$900) - AndroidUtilities.dp(5.0f);
                        } else if ((EmojiView.this.location[0] - access$900) + EmojiView.this.popupWidth > AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0f)) {
                            access$900 += ((EmojiView.this.location[0] - access$900) + EmojiView.this.popupWidth) - (AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0f));
                        }
                        access$900 = -access$900;
                        if (view.getTop() < 0) {
                            i = view.getTop();
                        }
                        EmojiView.this.pickerView.setEmoji(str, (AndroidUtilities.dp(AndroidUtilities.isTablet() ? 30.0f : 22.0f) - access$900) + ((int) AndroidUtilities.dpf2(0.5f)));
                        EmojiView.this.pickerViewPopup.setFocusable(true);
                        EmojiView.this.pickerViewPopup.showAsDropDown(view, access$900, (((-view.getMeasuredHeight()) - EmojiView.this.popupHeight) + ((view.getMeasuredHeight() - EmojiView.this.emojiSize) / 2)) - i);
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        return true;
                    }
                    if (EmojiView.this.pager.getCurrentItem() == null) {
                        EmojiView.this.listener.onClearEmojiRecent();
                    }
                    return false;
                }
            });
            setBackgroundDrawable(Theme.getSelectorDrawable(null));
            setScaleType(ScaleType.CENTER);
        }

        private void sendEmoji(String str) {
            String str2 = str != null ? str : (String) getTag();
            new SpannableStringBuilder().append(str2);
            if (str == null) {
                if (EmojiView.this.pager.getCurrentItem() != null) {
                    str = (String) Emoji.emojiColor.get(str2);
                    if (str != null) {
                        str2 = EmojiView.addColorToCode(str2, str);
                    }
                }
                EmojiView.this.addEmojiToRecent(str2);
                if (EmojiView.this.listener != null) {
                    EmojiView.this.listener.onEmojiSelected(Emoji.fixEmoji(str2));
                }
            } else if (EmojiView.this.listener != null) {
                EmojiView.this.listener.onEmojiSelected(Emoji.fixEmoji(str));
            }
        }

        public void onMeasure(int i, int i2) {
            setMeasuredDimension(MeasureSpec.getSize(i), MeasureSpec.getSize(i));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.touched) {
                boolean z = true;
                if (motionEvent.getAction() != 1) {
                    if (motionEvent.getAction() != 3) {
                        if (motionEvent.getAction() == 2) {
                            float x;
                            int dp;
                            if (this.touchedX != -10000.0f) {
                                if (Math.abs(this.touchedX - motionEvent.getX()) > AndroidUtilities.getPixelsInCM(0.2f, true) || Math.abs(this.touchedY - motionEvent.getY()) > AndroidUtilities.getPixelsInCM(0.2f, false)) {
                                    this.touchedX = -10000.0f;
                                    this.touchedY = -10000.0f;
                                }
                                if (!z) {
                                    getLocationOnScreen(EmojiView.this.location);
                                    x = ((float) EmojiView.this.location[0]) + motionEvent.getX();
                                    EmojiView.this.pickerView.getLocationOnScreen(EmojiView.this.location);
                                    dp = (int) ((x - ((float) (EmojiView.this.location[0] + AndroidUtilities.dp(3.0f)))) / ((float) (EmojiView.this.emojiSize + AndroidUtilities.dp(4.0f))));
                                    if (dp < 0) {
                                        dp = 0;
                                    } else if (dp > 5) {
                                        dp = 5;
                                    }
                                    EmojiView.this.pickerView.setSelection(dp);
                                }
                            }
                            z = false;
                            if (z) {
                                getLocationOnScreen(EmojiView.this.location);
                                x = ((float) EmojiView.this.location[0]) + motionEvent.getX();
                                EmojiView.this.pickerView.getLocationOnScreen(EmojiView.this.location);
                                dp = (int) ((x - ((float) (EmojiView.this.location[0] + AndroidUtilities.dp(3.0f)))) / ((float) (EmojiView.this.emojiSize + AndroidUtilities.dp(4.0f))));
                                if (dp < 0) {
                                    dp = 0;
                                } else if (dp > 5) {
                                    dp = 5;
                                }
                                EmojiView.this.pickerView.setSelection(dp);
                            }
                        }
                    }
                }
                if (EmojiView.this.pickerViewPopup != null && EmojiView.this.pickerViewPopup.isShowing()) {
                    String str;
                    EmojiView.this.pickerViewPopup.dismiss();
                    switch (EmojiView.this.pickerView.getSelection()) {
                        case 1:
                            str = "\ud83c\udffb";
                            break;
                        case 2:
                            str = "\ud83c\udffc";
                            break;
                        case 3:
                            str = "\ud83c\udffd";
                            break;
                        case 4:
                            str = "\ud83c\udffe";
                            break;
                        case 5:
                            str = "\ud83c\udfff";
                            break;
                        default:
                            str = null;
                            break;
                    }
                    String str2 = (String) getTag();
                    if (EmojiView.this.pager.getCurrentItem() != 0) {
                        if (str != null) {
                            Emoji.emojiColor.put(str2, str);
                            str2 = EmojiView.addColorToCode(str2, str);
                        } else {
                            Emoji.emojiColor.remove(str2);
                        }
                        setImageDrawable(Emoji.getEmojiBigDrawable(str2));
                        sendEmoji(null);
                        Emoji.saveEmojiColors();
                    } else if (str != null) {
                        sendEmoji(EmojiView.addColorToCode(str2, str));
                    } else {
                        sendEmoji(str2);
                    }
                }
                this.touched = false;
                this.touchedX = -10000.0f;
                this.touchedY = -10000.0f;
            }
            this.lastX = motionEvent.getX();
            this.lastY = motionEvent.getY();
            return super.onTouchEvent(motionEvent);
        }
    }

    public interface Listener {
        boolean isExpanded();

        boolean isSearchOpened();

        boolean onBackspace();

        void onClearEmojiRecent();

        void onEmojiSelected(String str);

        void onGifSelected(Document document);

        void onGifTab(boolean z);

        void onSearchOpenClose(boolean z);

        void onShowStickerSet(StickerSet stickerSet, InputStickerSet inputStickerSet);

        void onStickerSelected(Document document);

        void onStickerSetAdd(StickerSetCovered stickerSetCovered);

        void onStickerSetRemove(StickerSetCovered stickerSetCovered);

        void onStickersGroupClick(int i);

        void onStickersSettingsClick();

        void onStickersTab(boolean z);
    }

    /* renamed from: org.telegram.ui.Components.EmojiView$1 */
    class C20521 implements StickerPreviewViewerDelegate {
        public boolean needSend() {
            return true;
        }

        C20521() {
        }

        public void sendSticker(Document document) {
            EmojiView.this.listener.onStickerSelected(document);
        }

        public void openSet(InputStickerSet inputStickerSet) {
            if (inputStickerSet != null) {
                EmojiView.this.listener.onShowStickerSet(null, inputStickerSet);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView$5 */
    class C20535 extends SpanSizeLookup {
        C20535() {
        }

        public int getSpanSize(int i) {
            if (EmojiView.this.stickersGridView.getAdapter() == EmojiView.this.stickersGridAdapter) {
                if (i == 0) {
                    return EmojiView.this.stickersGridAdapter.stickersPerRow;
                }
                if (i == EmojiView.this.stickersGridAdapter.totalItems || (EmojiView.this.stickersGridAdapter.cache.get(i) != null && (EmojiView.this.stickersGridAdapter.cache.get(i) instanceof Document) == 0)) {
                    return EmojiView.this.stickersGridAdapter.stickersPerRow;
                }
                return 1;
            } else if (i == EmojiView.this.stickersSearchGridAdapter.totalItems || (EmojiView.this.stickersSearchGridAdapter.cache.get(i) != null && (EmojiView.this.stickersSearchGridAdapter.cache.get(i) instanceof Document) == 0)) {
                return EmojiView.this.stickersGridAdapter.stickersPerRow;
            } else {
                return 1;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.EmojiView$7 */
    class C20547 implements OnItemClickListener {
        C20547() {
        }

        public void onItemClick(View view, int i) {
            if (EmojiView.this.stickersGridView.getAdapter() == EmojiView.this.stickersSearchGridAdapter) {
                StickerSetCovered stickerSetCovered = (StickerSetCovered) EmojiView.this.stickersSearchGridAdapter.positionsToSets.get(i);
                if (stickerSetCovered != null) {
                    EmojiView.this.listener.onShowStickerSet(stickerSetCovered.set, null);
                    return;
                }
            }
            if ((view instanceof StickerEmojiCell) != 0) {
                StickerPreviewViewer.getInstance().reset();
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
                if (stickerEmojiCell.isDisabled() == 0) {
                    stickerEmojiCell.disable();
                    EmojiView.this.listener.onStickerSelected(stickerEmojiCell.getSticker());
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

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            if (i == 6) {
                i = EmojiView.this.stickersWrap;
            } else {
                i = (View) EmojiView.this.views.get(i);
            }
            viewGroup.removeView(i);
        }

        public boolean canScrollToTab(int i) {
            if (i != 6 || EmojiView.this.currentChatId == 0) {
                return true;
            }
            EmojiView.this.showStickerBanHint();
            return false;
        }

        public int getCount() {
            return EmojiView.this.views.size();
        }

        public Drawable getPageIconDrawable(int i) {
            return EmojiView.this.icons[i];
        }

        public void customOnDraw(Canvas canvas, int i) {
            if (i == 6 && DataQuery.getInstance(EmojiView.this.currentAccount).getUnreadStickerSets().isEmpty() == 0 && EmojiView.this.dotPaint != 0) {
                canvas.drawCircle((float) ((canvas.getWidth() / 2) + AndroidUtilities.dp(9.0f)), (float) ((canvas.getHeight() / 2) - AndroidUtilities.dp(8.0f)), (float) AndroidUtilities.dp(5.0f), EmojiView.this.dotPaint);
            }
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            if (i == 6) {
                i = EmojiView.this.stickersWrap;
            } else {
                i = (View) EmojiView.this.views.get(i);
            }
            viewGroup.addView(i);
            return i;
        }

        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            if (dataSetObserver != null) {
                super.unregisterDataSetObserver(dataSetObserver);
            }
        }
    }

    private class GifsAdapter extends SelectionAdapter {
        private Context mContext;

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public GifsAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return EmojiView.this.recentGifs.size();
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new Holder(new ContextLinkCell(this.mContext));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            Document document = (Document) EmojiView.this.recentGifs.get(i);
            if (document != null) {
                ((ContextLinkCell) viewHolder.itemView).setGif(document, false);
            }
        }
    }

    private class StickersGridAdapter extends SelectionAdapter {
        private SparseArray<Object> cache = new SparseArray();
        private Context context;
        private HashMap<Object, Integer> packStartPosition = new HashMap();
        private SparseIntArray positionToRow = new SparseIntArray();
        private SparseArray<Object> rowStartPack = new SparseArray();
        private int stickersPerRow;
        private int totalItems;

        /* renamed from: org.telegram.ui.Components.EmojiView$StickersGridAdapter$2 */
        class C11542 implements OnClickListener {
            C11542() {
            }

            public void onClick(View view) {
                if (EmojiView.this.groupStickerSet == null) {
                    view = MessagesController.getEmojiSettings(EmojiView.this.currentAccount).edit();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("group_hide_stickers_");
                    stringBuilder.append(EmojiView.this.info.id);
                    view.putLong(stringBuilder.toString(), EmojiView.this.info.stickerset != null ? EmojiView.this.info.stickerset.id : 0).commit();
                    EmojiView.this.updateStickerTabs();
                    if (EmojiView.this.stickersGridAdapter != null) {
                        EmojiView.this.stickersGridAdapter.notifyDataSetChanged();
                    }
                } else if (EmojiView.this.listener != null) {
                    EmojiView.this.listener.onStickersGroupClick(EmojiView.this.info.id);
                }
            }
        }

        /* renamed from: org.telegram.ui.Components.EmojiView$StickersGridAdapter$3 */
        class C11553 implements OnClickListener {
            C11553() {
            }

            public void onClick(View view) {
                if (EmojiView.this.listener != null) {
                    EmojiView.this.listener.onStickersGroupClick(EmojiView.this.info.id);
                }
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public StickersGridAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            return this.totalItems != 0 ? this.totalItems + 1 : 0;
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
            i = this.cache.get(i);
            if (i == 0) {
                return 1;
            }
            if (i instanceof Document) {
                return 0;
            }
            return (i instanceof String) != 0 ? 3 : 2;
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
            i = this.rowStartPack.get(i);
            if (!(i instanceof String)) {
                return EmojiView.this.stickerSets.indexOf((TL_messages_stickerSet) i) + EmojiView.this.stickersTabOffset;
            } else if ("recent".equals(i) != 0) {
                return EmojiView.this.recentTabBum;
            } else {
                return EmojiView.this.favTabBum;
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    i = new StickerEmojiCell(this.context) {
                        public void onMeasure(int i, int i2) {
                            super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(NUM), NUM));
                        }
                    };
                    break;
                case 1:
                    i = new EmptyCell(this.context);
                    break;
                case 2:
                    i = new StickerSetNameCell(this.context);
                    ((StickerSetNameCell) i).setOnIconClickListener(new C11542());
                    break;
                case 3:
                    i = new StickerSetGroupInfoCell(this.context);
                    ((StickerSetGroupInfoCell) i).setAddOnClickListener(new C11553());
                    i.setLayoutParams(new LayoutParams(-1, -2));
                    break;
                case 4:
                    i = new View(this.context);
                    i.setLayoutParams(new LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    break;
                default:
                    i = 0;
                    break;
            }
            return new Holder(i);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ArrayList arrayList = null;
            boolean z = false;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    Document document = (Document) this.cache.get(i);
                    StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) viewHolder.itemView;
                    stickerEmojiCell.setSticker(document, false);
                    if (EmojiView.this.recentStickers.contains(document) || EmojiView.this.favouriteStickers.contains(document) != 0) {
                        z = true;
                    }
                    stickerEmojiCell.setRecent(z);
                    return;
                case 1:
                    EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
                    if (i == this.totalItems) {
                        i = this.positionToRow.get(i - 1, Integer.MIN_VALUE);
                        if (i == Integer.MIN_VALUE) {
                            emptyCell.setHeight(1);
                            return;
                        }
                        i = this.rowStartPack.get(i);
                        if (i instanceof TL_messages_stickerSet) {
                            arrayList = ((TL_messages_stickerSet) i).documents;
                        } else if (i instanceof String) {
                            if ("recent".equals(i) != 0) {
                                arrayList = EmojiView.this.recentStickers;
                            } else {
                                arrayList = EmojiView.this.favouriteStickers;
                            }
                        }
                        if (arrayList == null) {
                            emptyCell.setHeight(1);
                            return;
                        } else if (arrayList.isEmpty() != 0) {
                            emptyCell.setHeight(AndroidUtilities.dp(NUM));
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
                case 2:
                    StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                    if (i == EmojiView.this.groupStickerPackPosition) {
                        Chat chat;
                        i = (EmojiView.this.groupStickersHidden == 0 || EmojiView.this.groupStickerSet != 0) ? EmojiView.this.groupStickerSet != 0 ? C0446R.drawable.stickersclose : C0446R.drawable.stickerset_close : 0;
                        if (EmojiView.this.info != null) {
                            chat = MessagesController.getInstance(EmojiView.this.currentAccount).getChat(Integer.valueOf(EmojiView.this.info.id));
                        }
                        String str = "CurrentGroupStickers";
                        Object[] objArr = new Object[1];
                        objArr[0] = chat != null ? chat.title : "Group Stickers";
                        stickerSetNameCell.setText(LocaleController.formatString(str, C0446R.string.CurrentGroupStickers, objArr), i);
                        return;
                    }
                    i = this.cache.get(i);
                    if (i instanceof TL_messages_stickerSet) {
                        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) i;
                        if (tL_messages_stickerSet.set != null) {
                            stickerSetNameCell.setText(tL_messages_stickerSet.set.title, 0);
                            return;
                        }
                        return;
                    } else if (i == EmojiView.this.recentStickers) {
                        stickerSetNameCell.setText(LocaleController.getString("RecentStickers", C0446R.string.RecentStickers), 0);
                        return;
                    } else if (i == EmojiView.this.favouriteStickers) {
                        stickerSetNameCell.setText(LocaleController.getString("FavoriteStickers", C0446R.string.FavoriteStickers), 0);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    StickerSetGroupInfoCell stickerSetGroupInfoCell = (StickerSetGroupInfoCell) viewHolder.itemView;
                    if (i == this.totalItems - 1) {
                        z = true;
                    }
                    stickerSetGroupInfoCell.setIsLast(z);
                    return;
                default:
                    return;
            }
        }

        public void notifyDataSetChanged() {
            int measuredWidth = EmojiView.this.getMeasuredWidth();
            if (measuredWidth == 0) {
                measuredWidth = AndroidUtilities.displaySize.x;
            }
            r0.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
            EmojiView.this.stickersLayoutManager.setSpanCount(r0.stickersPerRow);
            r0.rowStartPack.clear();
            r0.packStartPosition.clear();
            r0.positionToRow.clear();
            r0.cache.clear();
            measuredWidth = 0;
            r0.totalItems = 0;
            ArrayList access$6000 = EmojiView.this.stickerSets;
            int i = 0;
            int i2 = -3;
            while (i2 < access$6000.size()) {
                Object obj = null;
                int i3;
                if (i2 == -3) {
                    SparseArray sparseArray = r0.cache;
                    i3 = r0.totalItems;
                    r0.totalItems = i3 + 1;
                    sparseArray.put(i3, "search");
                    i++;
                } else {
                    ArrayList access$7800;
                    int i4;
                    if (i2 == -2) {
                        access$7800 = EmojiView.this.favouriteStickers;
                        r0.packStartPosition.put("fav", Integer.valueOf(r0.totalItems));
                    } else if (i2 == -1) {
                        access$7800 = EmojiView.this.recentStickers;
                        r0.packStartPosition.put("recent", Integer.valueOf(r0.totalItems));
                    } else {
                        obj = (TL_messages_stickerSet) access$6000.get(i2);
                        access$7800 = obj.documents;
                        r0.packStartPosition.put(obj, Integer.valueOf(r0.totalItems));
                    }
                    if (i2 == EmojiView.this.groupStickerPackNum) {
                        EmojiView.this.groupStickerPackPosition = r0.totalItems;
                        if (access$7800.isEmpty()) {
                            r0.rowStartPack.put(i, obj);
                            i4 = i + 1;
                            r0.positionToRow.put(r0.totalItems, i);
                            r0.rowStartPack.put(i4, obj);
                            int i5 = i4 + 1;
                            r0.positionToRow.put(r0.totalItems + 1, i4);
                            SparseArray sparseArray2 = r0.cache;
                            i3 = r0.totalItems;
                            r0.totalItems = i3 + 1;
                            sparseArray2.put(i3, obj);
                            sparseArray2 = r0.cache;
                            int i6 = r0.totalItems;
                            r0.totalItems = i6 + 1;
                            sparseArray2.put(i6, "group");
                            i = i5;
                        }
                    }
                    if (!access$7800.isEmpty()) {
                        i4 = (int) Math.ceil((double) (((float) access$7800.size()) / ((float) r0.stickersPerRow)));
                        if (obj != null) {
                            r0.cache.put(r0.totalItems, obj);
                        } else {
                            r0.cache.put(r0.totalItems, access$7800);
                        }
                        r0.positionToRow.put(r0.totalItems, i);
                        int i7 = measuredWidth;
                        while (i7 < access$7800.size()) {
                            int i8 = 1 + i7;
                            r0.cache.put(r0.totalItems + i8, access$7800.get(i7));
                            r0.positionToRow.put(r0.totalItems + i8, (i + 1) + (i7 / r0.stickersPerRow));
                            i7 = i8;
                        }
                        measuredWidth = 0;
                        while (true) {
                            i3 = i4 + 1;
                            if (measuredWidth >= i3) {
                                break;
                            }
                            if (obj != null) {
                                r0.rowStartPack.put(i + measuredWidth, obj);
                            } else {
                                r0.rowStartPack.put(i + measuredWidth, i2 == -1 ? "recent" : "fav");
                            }
                            measuredWidth++;
                        }
                        r0.totalItems += (i4 * r0.stickersPerRow) + 1;
                        i += i3;
                    }
                }
                i2++;
                measuredWidth = 0;
            }
            super.notifyDataSetChanged();
        }
    }

    private class StickersSearchGridAdapter extends SelectionAdapter {
        private SparseArray<Object> cache = new SparseArray();
        boolean cleared;
        private Context context;
        private ArrayList<ArrayList<Document>> emojiArrays = new ArrayList();
        private HashMap<ArrayList<Document>, String> emojiStickers = new HashMap();
        private ArrayList<TL_messages_stickerSet> localPacks = new ArrayList();
        private HashMap<TL_messages_stickerSet, Integer> localPacksByName = new HashMap();
        private HashMap<TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap();
        private SparseIntArray positionToRow = new SparseIntArray();
        private SparseArray<StickerSetCovered> positionsToSets = new SparseArray();
        private int reqId;
        private int reqId2;
        private SparseArray<Object> rowStartPack = new SparseArray();
        private String searchQuery;
        private Runnable searchRunnable = new C11581();
        private ArrayList<StickerSetCovered> serverPacks = new ArrayList();
        private int totalItems;

        /* renamed from: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$1 */
        class C11581 implements Runnable {
            C11581() {
            }

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

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                if (!TextUtils.isEmpty(StickersSearchGridAdapter.this.searchQuery)) {
                    int length;
                    int i;
                    int i2;
                    int i3;
                    EmojiView.this.progressDrawable.startAnimation();
                    int i4 = 0;
                    StickersSearchGridAdapter.this.cleared = false;
                    final ArrayList arrayList = new ArrayList(0);
                    final LongSparseArray longSparseArray = new LongSparseArray(0);
                    HashMap allStickers = DataQuery.getInstance(EmojiView.this.currentAccount).getAllStickers();
                    if (StickersSearchGridAdapter.this.searchQuery.length() <= 14) {
                        CharSequence access$9300 = StickersSearchGridAdapter.this.searchQuery;
                        length = access$9300.length();
                        CharSequence charSequence = access$9300;
                        i = 0;
                        while (i < length) {
                            if (i < length - 1) {
                                if (charSequence.charAt(i) == '\ud83c') {
                                    i2 = i + 1;
                                    if (charSequence.charAt(i2) >= '\udffb') {
                                    }
                                }
                                if (charSequence.charAt(i) == '\u200d') {
                                    i2 = i + 1;
                                    if (charSequence.charAt(i2) != '\u2640') {
                                    }
                                    charSequence = TextUtils.concat(new CharSequence[]{charSequence.subSequence(0, i), charSequence.subSequence(i + 2, charSequence.length())});
                                    length -= 2;
                                    i--;
                                    i++;
                                }
                            }
                            if (charSequence.charAt(i) == '\ufe0f') {
                                charSequence = TextUtils.concat(new CharSequence[]{charSequence.subSequence(0, i), charSequence.subSequence(i + 1, charSequence.length())});
                                length--;
                                i--;
                            }
                            i++;
                        }
                        ArrayList arrayList2 = allStickers != null ? (ArrayList) allStickers.get(charSequence.toString()) : null;
                        if (!(arrayList2 == null || arrayList2.isEmpty())) {
                            clear();
                            arrayList.addAll(arrayList2);
                            length = arrayList2.size();
                            for (i3 = 0; i3 < length; i3++) {
                                Document document = (Document) arrayList2.get(i3);
                                longSparseArray.put(document.id, document);
                            }
                            StickersSearchGridAdapter.this.emojiStickers.put(arrayList, StickersSearchGridAdapter.this.searchQuery);
                            StickersSearchGridAdapter.this.emojiArrays.add(arrayList);
                        }
                    }
                    if (!(allStickers == null || allStickers.isEmpty() || StickersSearchGridAdapter.this.searchQuery.length() <= 1)) {
                        String access$93002;
                        if (StickersSearchGridAdapter.this.searchQuery.startsWith(":")) {
                            access$93002 = StickersSearchGridAdapter.this.searchQuery;
                        } else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(":");
                            stringBuilder.append(StickersSearchGridAdapter.this.searchQuery);
                            access$93002 = stringBuilder.toString();
                        }
                        Object[] suggestion = Emoji.getSuggestion(access$93002);
                        if (suggestion != null) {
                            length = Math.min(10, suggestion.length);
                            for (i3 = 0; i3 < length; i3++) {
                                EmojiSuggestion emojiSuggestion = (EmojiSuggestion) suggestion[i3];
                                emojiSuggestion.emoji = emojiSuggestion.emoji.replace("\ufe0f", TtmlNode.ANONYMOUS_REGION_ID);
                                ArrayList arrayList3 = allStickers != null ? (ArrayList) allStickers.get(emojiSuggestion.emoji) : null;
                                if (!(arrayList3 == null || arrayList3.isEmpty())) {
                                    clear();
                                    if (!StickersSearchGridAdapter.this.emojiStickers.containsKey(arrayList3)) {
                                        StickersSearchGridAdapter.this.emojiStickers.put(arrayList3, emojiSuggestion.emoji);
                                        StickersSearchGridAdapter.this.emojiArrays.add(arrayList3);
                                    }
                                }
                            }
                        }
                    }
                    ArrayList stickerSets = DataQuery.getInstance(EmojiView.this.currentAccount).getStickerSets(0);
                    i = stickerSets.size();
                    for (length = 0; length < i; length++) {
                        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) stickerSets.get(length);
                        i2 = tL_messages_stickerSet.set.title.toLowerCase().indexOf(StickersSearchGridAdapter.this.searchQuery);
                        if (i2 >= 0) {
                            if (i2 == 0 || tL_messages_stickerSet.set.title.charAt(i2 - 1) == ' ') {
                                clear();
                                StickersSearchGridAdapter.this.localPacks.add(tL_messages_stickerSet);
                                StickersSearchGridAdapter.this.localPacksByName.put(tL_messages_stickerSet, Integer.valueOf(i2));
                            }
                        } else if (tL_messages_stickerSet.set.short_name != null) {
                            i2 = tL_messages_stickerSet.set.short_name.toLowerCase().indexOf(StickersSearchGridAdapter.this.searchQuery);
                            if (i2 >= 0 && (i2 == 0 || tL_messages_stickerSet.set.short_name.charAt(i2 - 1) == ' ')) {
                                clear();
                                StickersSearchGridAdapter.this.localPacks.add(tL_messages_stickerSet);
                                StickersSearchGridAdapter.this.localPacksByShortName.put(tL_messages_stickerSet, Boolean.valueOf(true));
                            }
                        }
                    }
                    stickerSets = DataQuery.getInstance(EmojiView.this.currentAccount).getStickerSets(3);
                    i = stickerSets.size();
                    while (i4 < i) {
                        TL_messages_stickerSet tL_messages_stickerSet2 = (TL_messages_stickerSet) stickerSets.get(i4);
                        i3 = tL_messages_stickerSet2.set.title.toLowerCase().indexOf(StickersSearchGridAdapter.this.searchQuery);
                        if (i3 >= 0) {
                            if (i3 == 0 || tL_messages_stickerSet2.set.title.charAt(i3 - 1) == ' ') {
                                clear();
                                StickersSearchGridAdapter.this.localPacks.add(tL_messages_stickerSet2);
                                StickersSearchGridAdapter.this.localPacksByName.put(tL_messages_stickerSet2, Integer.valueOf(i3));
                            }
                        } else if (tL_messages_stickerSet2.set.short_name != null) {
                            i3 = tL_messages_stickerSet2.set.short_name.toLowerCase().indexOf(StickersSearchGridAdapter.this.searchQuery);
                            if (i3 >= 0 && (i3 == 0 || tL_messages_stickerSet2.set.short_name.charAt(i3 - 1) == ' ')) {
                                clear();
                                StickersSearchGridAdapter.this.localPacks.add(tL_messages_stickerSet2);
                                StickersSearchGridAdapter.this.localPacksByShortName.put(tL_messages_stickerSet2, Boolean.valueOf(true));
                            }
                        }
                        i4++;
                    }
                    if (!((StickersSearchGridAdapter.this.localPacks.isEmpty() && StickersSearchGridAdapter.this.emojiStickers.isEmpty()) || EmojiView.this.stickersGridView.getAdapter() == EmojiView.this.stickersSearchGridAdapter)) {
                        EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersSearchGridAdapter);
                    }
                    final TLObject tL_messages_searchStickerSets = new TL_messages_searchStickerSets();
                    tL_messages_searchStickerSets.f52q = StickersSearchGridAdapter.this.searchQuery;
                    StickersSearchGridAdapter.this.reqId = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tL_messages_searchStickerSets, new RequestDelegate() {
                        public void run(final TLObject tLObject, TL_error tL_error) {
                            if ((tLObject instanceof TL_messages_foundStickerSets) != null) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        if (tL_messages_searchStickerSets.f52q.equals(StickersSearchGridAdapter.this.searchQuery)) {
                                            C11581.this.clear();
                                            EmojiView.this.progressDrawable.stopAnimation();
                                            StickersSearchGridAdapter.this.reqId = 0;
                                            if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersSearchGridAdapter) {
                                                EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersSearchGridAdapter);
                                            }
                                            StickersSearchGridAdapter.this.serverPacks.addAll(((TL_messages_foundStickerSets) tLObject).sets);
                                            StickersSearchGridAdapter.this.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                    });
                    if (Emoji.isValidEmoji(StickersSearchGridAdapter.this.searchQuery)) {
                        tL_messages_searchStickerSets = new TL_messages_getStickers();
                        tL_messages_searchStickerSets.emoticon = StickersSearchGridAdapter.this.searchQuery;
                        tL_messages_searchStickerSets.hash = TtmlNode.ANONYMOUS_REGION_ID;
                        tL_messages_searchStickerSets.exclude_featured = true;
                        StickersSearchGridAdapter.this.reqId2 = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tL_messages_searchStickerSets, new RequestDelegate() {
                            public void run(final TLObject tLObject, TL_error tL_error) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        if (tL_messages_searchStickerSets.emoticon.equals(StickersSearchGridAdapter.this.searchQuery)) {
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
                                });
                            }
                        });
                    }
                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                }
            }
        }

        /* renamed from: org.telegram.ui.Components.EmojiView$StickersSearchGridAdapter$3 */
        class C11593 implements OnClickListener {
            C11593() {
            }

            public void onClick(View view) {
                FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
                StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
                if (EmojiView.this.installingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
                    if (EmojiView.this.removingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
                        if (featuredStickerSetInfoCell.isInstalled()) {
                            EmojiView.this.removingStickerSets.put(stickerSet.set.id, stickerSet);
                            EmojiView.this.listener.onStickerSetRemove(featuredStickerSetInfoCell.getStickerSet());
                        } else {
                            EmojiView.this.installingStickerSets.put(stickerSet.set.id, stickerSet);
                            EmojiView.this.listener.onStickerSetAdd(featuredStickerSetInfoCell.getStickerSet());
                        }
                        featuredStickerSetInfoCell.setDrawProgress(true);
                    }
                }
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public StickersSearchGridAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            return this.totalItems != 1 ? this.totalItems + 1 : 2;
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
            i = this.cache.get(i);
            if (i == 0) {
                return 1;
            }
            if (i instanceof Document) {
                return 0;
            }
            return (i instanceof StickerSetCovered) != 0 ? 3 : 2;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    i = new StickerEmojiCell(this.context) {
                        public void onMeasure(int i, int i2) {
                            super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(NUM), NUM));
                        }
                    };
                    break;
                case 1:
                    i = new EmptyCell(this.context);
                    break;
                case 2:
                    i = new StickerSetNameCell(this.context);
                    break;
                case 3:
                    i = new FeaturedStickerSetInfoCell(this.context, 17);
                    ((FeaturedStickerSetInfoCell) i).setAddOnClickListener(new C11593());
                    break;
                case 4:
                    i = new View(this.context);
                    i.setLayoutParams(new LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    break;
                case 5:
                    i = new FrameLayout(this.context) {
                        protected void onMeasure(int i, int i2) {
                            super.onMeasure(i, MeasureSpec.makeMeasureSpec((int) (((float) (((EmojiView.this.stickersGridView.getMeasuredHeight() - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3)) * NUM), NUM));
                        }
                    };
                    View imageView = new ImageView(this.context);
                    imageView.setScaleType(ScaleType.CENTER);
                    imageView.setImageResource(C0446R.drawable.stickers_none);
                    imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelEmptyText), Mode.MULTIPLY));
                    i.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 48.0f));
                    imageView = new TextView(this.context);
                    imageView.setText(LocaleController.getString("NoStickersFound", C0446R.string.NoStickersFound));
                    imageView.setTextSize(1, 18.0f);
                    imageView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelEmptyText));
                    imageView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    i.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 30.0f, 0.0f, 0.0f));
                    i.setLayoutParams(new LayoutParams(-1, -2));
                    break;
                default:
                    i = 0;
                    break;
            }
            return new Holder(i);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ArrayList arrayList = null;
            boolean z = true;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    Document document = (Document) this.cache.get(i);
                    StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) viewHolder.itemView;
                    stickerEmojiCell.setSticker(document, false);
                    if (!EmojiView.this.recentStickers.contains(document)) {
                        if (EmojiView.this.favouriteStickers.contains(document) == 0) {
                            z = false;
                        }
                    }
                    stickerEmojiCell.setRecent(z);
                    return;
                case 1:
                    EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
                    if (i == this.totalItems) {
                        i = this.positionToRow.get(i - 1, Integer.MIN_VALUE);
                        if (i == Integer.MIN_VALUE) {
                            emptyCell.setHeight(1);
                            return;
                        }
                        i = this.rowStartPack.get(i);
                        if (i instanceof TL_messages_stickerSet) {
                            arrayList = ((TL_messages_stickerSet) i).documents;
                        } else if (i instanceof ArrayList) {
                            arrayList = (ArrayList) i;
                        }
                        if (arrayList == null) {
                            emptyCell.setHeight(1);
                            return;
                        } else if (arrayList.isEmpty() != 0) {
                            emptyCell.setHeight(AndroidUtilities.dp(NUM));
                            return;
                        } else {
                            i = EmojiView.this.pager.getHeight() - (((int) Math.ceil((double) (((float) arrayList.size()) / ((float) EmojiView.this.stickersGridAdapter.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                            if (i <= 0) {
                                i = 1;
                            }
                            emptyCell.setHeight(i);
                            return;
                        }
                    }
                    emptyCell.setHeight(AndroidUtilities.dp(82.0f));
                    return;
                case 2:
                    StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                    i = this.cache.get(i);
                    if (i instanceof TL_messages_stickerSet) {
                        TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) i;
                        if (TextUtils.isEmpty(this.searchQuery) || !this.localPacksByShortName.containsKey(tL_messages_stickerSet)) {
                            Integer num = (Integer) this.localPacksByName.get(tL_messages_stickerSet);
                            if (!(tL_messages_stickerSet.set == null || num == null)) {
                                stickerSetNameCell.setText(tL_messages_stickerSet.set.title, 0, num.intValue(), !TextUtils.isEmpty(this.searchQuery) ? this.searchQuery.length() : 0);
                            }
                            stickerSetNameCell.setUrl(null, 0);
                            return;
                        }
                        if (tL_messages_stickerSet.set != null) {
                            stickerSetNameCell.setText(tL_messages_stickerSet.set.title, 0);
                        }
                        stickerSetNameCell.setUrl(tL_messages_stickerSet.set.short_name, this.searchQuery.length());
                        return;
                    } else if (i instanceof ArrayList) {
                        stickerSetNameCell.setText((CharSequence) this.emojiStickers.get(i), 0);
                        stickerSetNameCell.setUrl(null, 0);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    StickerSetCovered stickerSetCovered = (StickerSetCovered) this.cache.get(i);
                    FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) viewHolder.itemView;
                    boolean z2 = EmojiView.this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                    boolean z3 = EmojiView.this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                    if (z2 || z3) {
                        if (z2 && featuredStickerSetInfoCell.isInstalled()) {
                            EmojiView.this.installingStickerSets.remove(stickerSetCovered.set.id);
                            z2 = false;
                        } else if (z3 && !featuredStickerSetInfoCell.isInstalled()) {
                            EmojiView.this.removingStickerSets.remove(stickerSetCovered.set.id);
                            z3 = false;
                        }
                    }
                    if (!z2) {
                        if (!z3) {
                            z = false;
                        }
                    }
                    featuredStickerSetInfoCell.setDrawProgress(z);
                    int indexOf = TextUtils.isEmpty(this.searchQuery) ? -1 : stickerSetCovered.set.title.toLowerCase().indexOf(this.searchQuery);
                    if (indexOf >= 0) {
                        featuredStickerSetInfoCell.setStickerSet(stickerSetCovered, false, indexOf, this.searchQuery.length());
                        return;
                    }
                    featuredStickerSetInfoCell.setStickerSet(stickerSetCovered, false);
                    if (!TextUtils.isEmpty(this.searchQuery) && stickerSetCovered.set.short_name.toLowerCase().startsWith(this.searchQuery)) {
                        featuredStickerSetInfoCell.setUrl(stickerSetCovered.set.short_name, this.searchQuery.length());
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
            int i = 0;
            this.totalItems = 0;
            int size = this.serverPacks.size();
            int size2 = this.localPacks.size();
            int size3 = this.emojiArrays.size();
            int i2 = -1;
            int i3 = 0;
            int i4 = -1;
            while (i4 < (size + size2) + size3) {
                Object obj = null;
                int i5;
                if (i4 == i2) {
                    SparseArray sparseArray = r0.cache;
                    i5 = r0.totalItems;
                    r0.totalItems = i5 + 1;
                    sparseArray.put(i5, "search");
                    i3++;
                } else {
                    ArrayList arrayList;
                    if (i4 < size2) {
                        obj = (TL_messages_stickerSet) r0.localPacks.get(i4);
                        arrayList = obj.documents;
                    } else {
                        i5 = i4 - size2;
                        if (i5 < size3) {
                            arrayList = (ArrayList) r0.emojiArrays.get(i5);
                        } else {
                            obj = (StickerSetCovered) r0.serverPacks.get(i5 - size3);
                            arrayList = obj.covers;
                        }
                    }
                    if (!arrayList.isEmpty()) {
                        int ceil = (int) Math.ceil((double) (((float) arrayList.size()) / ((float) EmojiView.this.stickersGridAdapter.stickersPerRow)));
                        if (obj != null) {
                            r0.cache.put(r0.totalItems, obj);
                        } else {
                            r0.cache.put(r0.totalItems, arrayList);
                        }
                        if (i4 >= size2 && (obj instanceof StickerSetCovered)) {
                            r0.positionsToSets.put(r0.totalItems, (StickerSetCovered) obj);
                        }
                        r0.positionToRow.put(r0.totalItems, i3);
                        int size4 = arrayList.size();
                        int i6 = i;
                        while (i6 < size4) {
                            int i7 = 1 + i6;
                            r0.cache.put(r0.totalItems + i7, arrayList.get(i6));
                            r0.positionToRow.put(r0.totalItems + i7, (i3 + 1) + (i6 / EmojiView.this.stickersGridAdapter.stickersPerRow));
                            if (i4 >= size2 && (obj instanceof StickerSetCovered)) {
                                r0.positionsToSets.put(r0.totalItems + i7, (StickerSetCovered) obj);
                            }
                            i6 = i7;
                        }
                        i = 0;
                        while (true) {
                            i2 = ceil + 1;
                            if (i >= i2) {
                                break;
                            }
                            if (obj != null) {
                                r0.rowStartPack.put(i3 + i, obj);
                            } else {
                                r0.rowStartPack.put(i3 + i, arrayList);
                            }
                            i++;
                        }
                        r0.totalItems += (ceil * EmojiView.this.stickersGridAdapter.stickersPerRow) + 1;
                        i3 += i2;
                    }
                }
                i4++;
                i = 0;
                i2 = -1;
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

        /* renamed from: org.telegram.ui.Components.EmojiView$TrendingGridAdapter$2 */
        class C11612 implements OnClickListener {
            C11612() {
            }

            public void onClick(View view) {
                FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
                StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
                if (EmojiView.this.installingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
                    if (EmojiView.this.removingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
                        if (featuredStickerSetInfoCell.isInstalled()) {
                            EmojiView.this.removingStickerSets.put(stickerSet.set.id, stickerSet);
                            EmojiView.this.listener.onStickerSetRemove(featuredStickerSetInfoCell.getStickerSet());
                        } else {
                            EmojiView.this.installingStickerSets.put(stickerSet.set.id, stickerSet);
                            EmojiView.this.listener.onStickerSetAdd(featuredStickerSetInfoCell.getStickerSet());
                        }
                        featuredStickerSetInfoCell.setDrawProgress(true);
                    }
                }
            }
        }

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
            i = this.cache.get(i);
            if (i != 0) {
                return (i instanceof Document) != 0 ? 0 : 2;
            } else {
                return 1;
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new StickerEmojiCell(this.context) {
                        public void onMeasure(int i, int i2) {
                            super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(NUM), NUM));
                        }
                    };
                    break;
                case 1:
                    viewGroup = new EmptyCell(this.context);
                    break;
                case 2:
                    viewGroup = new FeaturedStickerSetInfoCell(this.context, 17);
                    ((FeaturedStickerSetInfoCell) viewGroup).setAddOnClickListener(new C11612());
                    break;
                default:
                    viewGroup = null;
                    break;
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    ((StickerEmojiCell) viewHolder.itemView).setSticker((Document) this.cache.get(i), false);
                    return;
                case 1:
                    ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(NUM));
                    return;
                case 2:
                    ArrayList unreadStickerSets = DataQuery.getInstance(EmojiView.this.currentAccount).getUnreadStickerSets();
                    StickerSetCovered stickerSetCovered = (StickerSetCovered) this.sets.get(((Integer) this.cache.get(i)).intValue());
                    boolean z2 = unreadStickerSets != null && unreadStickerSets.contains(Long.valueOf(stickerSetCovered.set.id));
                    FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) viewHolder.itemView;
                    featuredStickerSetInfoCell.setStickerSet(stickerSetCovered, z2);
                    if (z2) {
                        DataQuery.getInstance(EmojiView.this.currentAccount).markFaturedStickersByIdAsRead(stickerSetCovered.set.id);
                    }
                    z2 = EmojiView.this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                    boolean z3 = EmojiView.this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
                    if (z2 || z3) {
                        if (z2 && featuredStickerSetInfoCell.isInstalled()) {
                            EmojiView.this.installingStickerSets.remove(stickerSetCovered.set.id);
                            z2 = false;
                        } else if (z3 && !featuredStickerSetInfoCell.isInstalled()) {
                            EmojiView.this.removingStickerSets.remove(stickerSetCovered.set.id);
                            z3 = false;
                        }
                    }
                    if (z2 || r3) {
                        z = true;
                    }
                    featuredStickerSetInfoCell.setDrawProgress(z);
                    return;
                default:
                    return;
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
                int i3 = i2;
                while (i2 < featuredStickerSets.size()) {
                    StickerSetCovered stickerSetCovered = (StickerSetCovered) featuredStickerSets.get(i2);
                    if (!DataQuery.getInstance(EmojiView.this.currentAccount).isStickerPackInstalled(stickerSetCovered.set.id)) {
                        if (!stickerSetCovered.covers.isEmpty() || stickerSetCovered.cover != null) {
                            int i4;
                            this.sets.add(stickerSetCovered);
                            this.positionsToSets.put(this.totalItems, stickerSetCovered);
                            SparseArray sparseArray = this.cache;
                            int i5 = this.totalItems;
                            this.totalItems = i5 + 1;
                            int i6 = i3 + 1;
                            sparseArray.put(i5, Integer.valueOf(i3));
                            i3 = this.totalItems / this.stickersPerRow;
                            if (stickerSetCovered.covers.isEmpty()) {
                                this.cache.put(this.totalItems, stickerSetCovered.cover);
                                i3 = 1;
                            } else {
                                i3 = (int) Math.ceil((double) (((float) stickerSetCovered.covers.size()) / ((float) this.stickersPerRow)));
                                for (i4 = 0; i4 < stickerSetCovered.covers.size(); i4++) {
                                    this.cache.put(this.totalItems + i4, stickerSetCovered.covers.get(i4));
                                }
                            }
                            for (i4 = 0; i4 < this.stickersPerRow * i3; i4++) {
                                this.positionsToSets.put(this.totalItems + i4, stickerSetCovered);
                            }
                            this.totalItems += i3 * this.stickersPerRow;
                            i3 = i6;
                        }
                    }
                    i2++;
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
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r0 = 0;
        r1 = android.widget.PopupWindow.class;	 Catch:{ NoSuchFieldException -> 0x000e }
        r2 = "mOnScrollChangedListener";	 Catch:{ NoSuchFieldException -> 0x000e }
        r1 = r1.getDeclaredField(r2);	 Catch:{ NoSuchFieldException -> 0x000e }
        r0 = 1;
        r1.setAccessible(r0);	 Catch:{ NoSuchFieldException -> 0x000f }
        goto L_0x000f;
    L_0x000e:
        r1 = r0;
    L_0x000f:
        superListenerField = r1;
        r0 = new org.telegram.ui.Components.EmojiView$2;
        r0.<init>();
        NOP = r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.<clinit>():void");
    }

    private static String addColorToCode(String str, String str2) {
        String substring;
        int length = str.length();
        if (length > 2 && str.charAt(str.length() - 2) == '\u200d') {
            substring = str.substring(str.length() - 2);
            str = str.substring(0, str.length() - 2);
        } else if (length <= 3 || str.charAt(str.length() - 3) != '\u200d') {
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
        str2 = new StringBuilder();
        str2.append(str);
        str2.append(substring);
        return str2.toString();
    }

    public void addEmojiToRecent(String str) {
        if (Emoji.isValidEmoji(str)) {
            Emoji.addRecentEmoji(str);
            if (!(getVisibility() == null && this.pager.getCurrentItem() == null)) {
                Emoji.sortEmoji();
            }
            Emoji.saveRecentEmoji();
            ((EmojiGridAdapter) this.adapters.get(0)).notifyDataSetChanged();
        }
    }

    public EmojiView(boolean z, boolean z2, Context context, ChatFull chatFull) {
        boolean z3 = z2;
        Context context2 = context;
        super(context2);
        this.stickersDrawable = context.getResources().getDrawable(C0446R.drawable.ic_smiles2_stickers);
        Theme.setDrawableColorByKey(this.stickersDrawable, Theme.key_chat_emojiPanelIcon);
        this.icons = new Drawable[]{Theme.createEmojiIconSelectorDrawable(context2, C0446R.drawable.ic_smiles2_recent, Theme.getColor(Theme.key_chat_emojiPanelIcon), Theme.getColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context2, C0446R.drawable.ic_smiles2_smile, Theme.getColor(Theme.key_chat_emojiPanelIcon), Theme.getColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context2, C0446R.drawable.ic_smiles2_nature, Theme.getColor(Theme.key_chat_emojiPanelIcon), Theme.getColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context2, C0446R.drawable.ic_smiles2_food, Theme.getColor(Theme.key_chat_emojiPanelIcon), Theme.getColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context2, C0446R.drawable.ic_smiles2_car, Theme.getColor(Theme.key_chat_emojiPanelIcon), Theme.getColor(Theme.key_chat_emojiPanelIconSelected)), Theme.createEmojiIconSelectorDrawable(context2, C0446R.drawable.ic_smiles2_objects, Theme.getColor(Theme.key_chat_emojiPanelIcon), Theme.getColor(Theme.key_chat_emojiPanelIconSelected)), this.stickersDrawable};
        this.showGifs = z3;
        this.info = chatFull;
        this.dotPaint = new Paint(1);
        this.dotPaint.setColor(Theme.getColor(Theme.key_chat_emojiPanelNewTrending));
        if (VERSION.SDK_INT >= 21) {
            r0.outlineProvider = new C11483();
        }
        for (int i = 0; i < EmojiData.dataColored.length + 1; i++) {
            View gridView = new GridView(context2);
            if (AndroidUtilities.isTablet()) {
                gridView.setColumnWidth(AndroidUtilities.dp(60.0f));
            } else {
                gridView.setColumnWidth(AndroidUtilities.dp(45.0f));
            }
            gridView.setNumColumns(-1);
            ListAdapter emojiGridAdapter = new EmojiGridAdapter(i - 1);
            gridView.setAdapter(emojiGridAdapter);
            r0.adapters.add(emojiGridAdapter);
            r0.emojiGrids.add(gridView);
            FrameLayout frameLayout = new FrameLayout(context2);
            frameLayout.addView(gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
            r0.views.add(frameLayout);
        }
        if (z) {
            r0.stickersWrap = new FrameLayout(context2);
            DataQuery.getInstance(r0.currentAccount).checkStickers(0);
            DataQuery.getInstance(r0.currentAccount).checkFeaturedStickers();
            r0.stickersGridView = new RecyclerListView(context2) {
                boolean ignoreLayout;

                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    boolean onInterceptTouchEvent = StickerPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.stickerPreviewViewerDelegate);
                    if (super.onInterceptTouchEvent(motionEvent) == null) {
                        if (!onInterceptTouchEvent) {
                            return null;
                        }
                    }
                    return true;
                }

                public void setVisibility(int i) {
                    if ((EmojiView.this.gifsGridView == null || EmojiView.this.gifsGridView.getVisibility() != 0) && (EmojiView.this.trendingGridView == null || EmojiView.this.trendingGridView.getVisibility() != 0)) {
                        super.setVisibility(i);
                    } else {
                        super.setVisibility(8);
                    }
                }

                protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    if (EmojiView.this.firstAttach && EmojiView.this.stickersGridAdapter.getItemCount() > 0) {
                        this.ignoreLayout = true;
                        EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(1, 0);
                        EmojiView.this.firstAttach = false;
                        this.ignoreLayout = false;
                    }
                    super.onLayout(z, i, i2, i3, i4);
                    EmojiView.this.checkSearchFieldScroll();
                }

                public void requestLayout() {
                    if (!this.ignoreLayout) {
                        super.requestLayout();
                    }
                }
            };
            RecyclerListView recyclerListView = r0.stickersGridView;
            LayoutManager gridLayoutManager = new GridLayoutManager(context2, 5);
            r0.stickersLayoutManager = gridLayoutManager;
            recyclerListView.setLayoutManager(gridLayoutManager);
            r0.stickersLayoutManager.setSpanSizeLookup(new C20535());
            r0.stickersGridView.setPadding(0, AndroidUtilities.dp(52.0f), 0, 0);
            r0.stickersGridView.setClipToPadding(false);
            r0.views.add(r0.stickersWrap);
            r0.stickersSearchGridAdapter = new StickersSearchGridAdapter(context2);
            recyclerListView = r0.stickersGridView;
            Adapter stickersGridAdapter = new StickersGridAdapter(context2);
            r0.stickersGridAdapter = stickersGridAdapter;
            recyclerListView.setAdapter(stickersGridAdapter);
            r0.stickersGridView.setOnTouchListener(new C11496());
            r0.stickersOnItemClickListener = new C20547();
            r0.stickersGridView.setOnItemClickListener(r0.stickersOnItemClickListener);
            r0.stickersGridView.setGlowColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
            r0.stickersWrap.addView(r0.stickersGridView);
            r0.searchEditTextContainer = new FrameLayout(context2);
            r0.searchEditTextContainer.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
            r0.stickersWrap.addView(r0.searchEditTextContainer, new LayoutParams(-1, r0.searchFieldHeight));
            r0.searchBackground = new View(context2);
            r0.searchBackground.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor(Theme.key_chat_emojiSearchBackground)));
            r0.searchEditTextContainer.addView(r0.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 14.0f, 14.0f, 0.0f));
            r0.searchIconImageView = new ImageView(context2);
            r0.searchIconImageView.setScaleType(ScaleType.CENTER);
            r0.searchIconImageView.setImageResource(C0446R.drawable.sticker_search);
            r0.searchIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelIcon), Mode.MULTIPLY));
            r0.searchEditTextContainer.addView(r0.searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 14.0f, 14.0f, 0.0f, 0.0f));
            r0.clearSearchImageView = new ImageView(context2);
            r0.clearSearchImageView.setScaleType(ScaleType.CENTER);
            ImageView imageView = r0.clearSearchImageView;
            Drawable closeProgressDrawable2 = new CloseProgressDrawable2();
            r0.progressDrawable = closeProgressDrawable2;
            imageView.setImageDrawable(closeProgressDrawable2);
            r0.clearSearchImageView.setScaleX(0.1f);
            r0.clearSearchImageView.setScaleY(0.1f);
            r0.clearSearchImageView.setAlpha(0.0f);
            r0.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelIcon), Mode.MULTIPLY));
            r0.searchEditTextContainer.addView(r0.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 14.0f, 14.0f, 0.0f));
            r0.clearSearchImageView.setOnClickListener(new C11508());
            r0.searchEditText = new EditTextBoldCursor(context2) {

                /* renamed from: org.telegram.ui.Components.EmojiView$9$1 */
                class C11511 extends AnimatorListenerAdapter {
                    C11511() {
                    }

                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(EmojiView.this.searchAnimation) != null) {
                            EmojiView.this.stickersGridView.setTranslationY(0.0f);
                            EmojiView.this.stickersGridView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                            EmojiView.this.searchAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        if (animator.equals(EmojiView.this.searchAnimation) != null) {
                            EmojiView.this.searchAnimation = null;
                        }
                    }
                }

                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getAction() == 0) {
                        if (!EmojiView.this.listener.isSearchOpened()) {
                            if (EmojiView.this.searchAnimation != null) {
                                EmojiView.this.searchAnimation.cancel();
                                EmojiView.this.searchAnimation = null;
                            }
                            if (EmojiView.this.listener == null || !EmojiView.this.listener.isExpanded()) {
                                EmojiView.this.searchEditTextContainer.setTranslationY((float) AndroidUtilities.dp(0.0f));
                                EmojiView.this.stickersTab.setTranslationY((float) (-AndroidUtilities.dp(47.0f)));
                                EmojiView.this.stickersGridView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                            } else {
                                EmojiView.this.searchAnimation = new AnimatorSet();
                                AnimatorSet access$3400 = EmojiView.this.searchAnimation;
                                r5 = new Animator[3];
                                r5[0] = ObjectAnimator.ofFloat(EmojiView.this.stickersTab, "translationY", new float[]{(float) (-AndroidUtilities.dp(47.0f))});
                                r5[1] = ObjectAnimator.ofFloat(EmojiView.this.stickersGridView, "translationY", new float[]{(float) (-AndroidUtilities.dp(48.0f))});
                                r5[2] = ObjectAnimator.ofFloat(EmojiView.this.searchEditTextContainer, "translationY", new float[]{(float) AndroidUtilities.dp(0.0f)});
                                access$3400.playTogether(r5);
                                EmojiView.this.searchAnimation.setDuration(200);
                                EmojiView.this.searchAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                                EmojiView.this.searchAnimation.addListener(new C11511());
                                EmojiView.this.searchAnimation.start();
                            }
                        }
                        EmojiView.this.listener.onSearchOpenClose(true);
                        EmojiView.this.searchEditText.requestFocus();
                        AndroidUtilities.showKeyboard(EmojiView.this.searchEditText);
                    }
                    return super.onTouchEvent(motionEvent);
                }
            };
            r0.searchEditText.setTextSize(1, 16.0f);
            r0.searchEditText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r0.searchEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.searchEditText.setBackgroundDrawable(null);
            r0.searchEditText.setPadding(0, 0, 0, 0);
            r0.searchEditText.setMaxLines(1);
            r0.searchEditText.setLines(1);
            r0.searchEditText.setSingleLine(true);
            r0.searchEditText.setImeOptions(268435459);
            r0.searchEditText.setHint(LocaleController.getString("SearchStickersHint", C0446R.string.SearchStickersHint));
            r0.searchEditText.setCursorColor(Theme.getColor(Theme.key_featuredStickers_addedIcon));
            r0.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            r0.searchEditText.setCursorWidth(1.5f);
            r0.searchEditTextContainer.addView(r0.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 46.0f, 12.0f, 46.0f, 0.0f));
            r0.searchEditText.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    Editable editable2 = null;
                    editable = EmojiView.this.searchEditText.length() > null ? 1 : null;
                    float f = 0.0f;
                    if (EmojiView.this.clearSearchImageView.getAlpha() != 0.0f) {
                        editable2 = 1;
                    }
                    if (editable != editable2) {
                        ViewPropertyAnimator animate = EmojiView.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (editable != null) {
                            f = 1.0f;
                        }
                        animate = animate.alpha(f).setDuration(150).scaleX(editable != null ? 1.0f : 0.1f);
                        if (editable == null) {
                            f2 = 0.1f;
                        }
                        animate.scaleY(f2).start();
                    }
                    EmojiView.this.stickersSearchGridAdapter.search(EmojiView.this.searchEditText.getText().toString());
                }
            });
            r0.trendingGridView = new RecyclerListView(context2);
            r0.trendingGridView.setItemAnimator(null);
            r0.trendingGridView.setLayoutAnimation(null);
            recyclerListView = r0.trendingGridView;
            LayoutManager anonymousClass11 = new GridLayoutManager(context2, 5) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            r0.trendingLayoutManager = anonymousClass11;
            recyclerListView.setLayoutManager(anonymousClass11);
            r0.trendingLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
                public int getSpanSize(int i) {
                    if (!(EmojiView.this.trendingGridAdapter.cache.get(i) instanceof Integer)) {
                        if (i != EmojiView.this.trendingGridAdapter.totalItems) {
                            return 1;
                        }
                    }
                    return EmojiView.this.trendingGridAdapter.stickersPerRow;
                }
            });
            r0.trendingGridView.setOnScrollListener(new OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    EmojiView.this.checkStickersTabY(recyclerView, i2);
                }
            });
            r0.trendingGridView.setClipToPadding(false);
            r0.trendingGridView.setPadding(0, AndroidUtilities.dp(48.0f), 0, 0);
            recyclerListView = r0.trendingGridView;
            Adapter trendingGridAdapter = new TrendingGridAdapter(context2);
            r0.trendingGridAdapter = trendingGridAdapter;
            recyclerListView.setAdapter(trendingGridAdapter);
            r0.trendingGridView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(View view, int i) {
                    StickerSetCovered stickerSetCovered = (StickerSetCovered) EmojiView.this.trendingGridAdapter.positionsToSets.get(i);
                    if (stickerSetCovered != null) {
                        EmojiView.this.listener.onShowStickerSet(stickerSetCovered.set, null);
                    }
                }
            });
            r0.trendingGridAdapter.notifyDataSetChanged();
            r0.trendingGridView.setGlowColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
            r0.trendingGridView.setVisibility(8);
            r0.stickersWrap.addView(r0.trendingGridView);
            if (z3) {
                r0.gifsGridView = new RecyclerListView(context2);
                r0.gifsGridView.setClipToPadding(false);
                r0.gifsGridView.setPadding(0, AndroidUtilities.dp(48.0f), 0, 0);
                RecyclerListView recyclerListView2 = r0.gifsGridView;
                LayoutManager anonymousClass15 = new ExtendedGridLayoutManager(context2, 100) {
                    private Size size = new Size();

                    protected Size getSizeForItem(int i) {
                        Document document = (Document) EmojiView.this.recentGifs.get(i);
                        Size size = this.size;
                        float f = 100.0f;
                        float f2 = (document.thumb == null || document.thumb.f43w == 0) ? 100.0f : (float) document.thumb.f43w;
                        size.width = f2;
                        size = this.size;
                        if (!(document.thumb == null || document.thumb.f42h == 0)) {
                            f = (float) document.thumb.f42h;
                        }
                        size.height = f;
                        int i2 = 0;
                        while (i2 < document.attributes.size()) {
                            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i2);
                            if (!(documentAttribute instanceof TL_documentAttributeImageSize)) {
                                if (!(documentAttribute instanceof TL_documentAttributeVideo)) {
                                    i2++;
                                }
                            }
                            this.size.width = (float) documentAttribute.f36w;
                            this.size.height = (float) documentAttribute.f35h;
                        }
                        return this.size;
                    }
                };
                r0.flowLayoutManager = anonymousClass15;
                recyclerListView2.setLayoutManager(anonymousClass15);
                r0.flowLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
                    public int getSpanSize(int i) {
                        return EmojiView.this.flowLayoutManager.getSpanSizeForItem(i);
                    }
                });
                r0.gifsGridView.addItemDecoration(new ItemDecoration() {
                    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
                        state = null;
                        rect.left = 0;
                        rect.top = 0;
                        rect.bottom = 0;
                        view = recyclerView.getChildAdapterPosition(view);
                        if (EmojiView.this.flowLayoutManager.isFirstRow(view) == null) {
                            rect.top = AndroidUtilities.dp(2.0f);
                        }
                        if (EmojiView.this.flowLayoutManager.isLastInRow(view) == null) {
                            state = AndroidUtilities.dp(2.0f);
                        }
                        rect.right = state;
                    }
                });
                r0.gifsGridView.setOverScrollMode(2);
                recyclerListView2 = r0.gifsGridView;
                Adapter gifsAdapter = new GifsAdapter(context2);
                r0.gifsAdapter = gifsAdapter;
                recyclerListView2.setAdapter(gifsAdapter);
                r0.gifsGridView.setOnScrollListener(new OnScrollListener() {
                    public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                        EmojiView.this.checkStickersTabY(recyclerView, i2);
                    }
                });
                r0.gifsGridView.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(View view, int i) {
                        if (i >= 0 && i < EmojiView.this.recentGifs.size()) {
                            if (EmojiView.this.listener != null) {
                                EmojiView.this.listener.onGifSelected((Document) EmojiView.this.recentGifs.get(i));
                            }
                        }
                    }
                });
                r0.gifsGridView.setOnItemLongClickListener(new OnItemLongClickListener() {
                    public boolean onItemClick(View view, int i) {
                        if (i >= 0) {
                            if (i < EmojiView.this.recentGifs.size()) {
                                final Document document = (Document) EmojiView.this.recentGifs.get(i);
                                Builder builder = new Builder(view.getContext());
                                builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                                builder.setMessage(LocaleController.getString("DeleteGif", C0446R.string.DeleteGif));
                                builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK).toUpperCase(), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DataQuery.getInstance(EmojiView.this.currentAccount).removeRecentGif(document);
                                        EmojiView.this.recentGifs = DataQuery.getInstance(EmojiView.this.currentAccount).getRecentGifs();
                                        if (EmojiView.this.gifsAdapter != null) {
                                            EmojiView.this.gifsAdapter.notifyDataSetChanged();
                                        }
                                        if (EmojiView.this.recentGifs.isEmpty() != null) {
                                            EmojiView.this.updateStickerTabs();
                                            if (EmojiView.this.stickersGridAdapter != null) {
                                                EmojiView.this.stickersGridAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), 0);
                                builder.show().setCanceledOnTouchOutside(true);
                                return true;
                            }
                        }
                        return null;
                    }
                });
                r0.gifsGridView.setVisibility(8);
                r0.stickersWrap.addView(r0.gifsGridView);
            }
            r0.stickersEmptyView = new TextView(context2);
            r0.stickersEmptyView.setText(LocaleController.getString("NoStickers", C0446R.string.NoStickers));
            r0.stickersEmptyView.setTextSize(1, 18.0f);
            r0.stickersEmptyView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelEmptyText));
            r0.stickersEmptyView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            r0.stickersWrap.addView(r0.stickersEmptyView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 48.0f, 0.0f, 0.0f));
            r0.stickersGridView.setEmptyView(r0.stickersEmptyView);
            r0.stickersTab = new ScrollSlidingTabStrip(context2) {
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
                        if (this.startedScroll != null) {
                            EmojiView.this.pager.endFakeDrag();
                            this.startedScroll = false;
                        }
                        return true;
                    }
                    return super.onInterceptTouchEvent(motionEvent);
                }

                public boolean onTouchEvent(android.view.MotionEvent r8) {
                    /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
                    /*
                    r7 = this;
                    r0 = r7.first;
                    r1 = 0;
                    if (r0 == 0) goto L_0x000d;
                L_0x0005:
                    r7.first = r1;
                    r0 = r8.getX();
                    r7.lastX = r0;
                L_0x000d:
                    r0 = r8.getAction();
                    r2 = 1;
                    if (r0 != 0) goto L_0x0025;
                L_0x0014:
                    r7.draggingHorizontally = r1;
                    r7.draggingVertically = r1;
                    r0 = r8.getRawX();
                    r7.downX = r0;
                    r0 = r8.getRawY();
                    r7.downY = r0;
                    goto L_0x007c;
                L_0x0025:
                    r0 = r7.draggingVertically;
                    if (r0 != 0) goto L_0x007c;
                L_0x0029:
                    r0 = r7.draggingHorizontally;
                    if (r0 != 0) goto L_0x007c;
                L_0x002d:
                    r0 = org.telegram.ui.Components.EmojiView.this;
                    r0 = r0.dragListener;
                    if (r0 == 0) goto L_0x007c;
                L_0x0035:
                    r0 = r8.getRawX();
                    r3 = r7.downX;
                    r0 = r0 - r3;
                    r0 = java.lang.Math.abs(r0);
                    r3 = r7.touchslop;
                    r3 = (float) r3;
                    r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
                    if (r0 < 0) goto L_0x004a;
                L_0x0047:
                    r7.draggingHorizontally = r2;
                    goto L_0x007c;
                L_0x004a:
                    r0 = r8.getRawY();
                    r3 = r7.downY;
                    r0 = r0 - r3;
                    r0 = java.lang.Math.abs(r0);
                    r3 = r7.touchslop;
                    r3 = (float) r3;
                    r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
                    if (r0 < 0) goto L_0x007c;
                L_0x005c:
                    r7.draggingVertically = r2;
                    r0 = r8.getRawY();
                    r7.downY = r0;
                    r0 = org.telegram.ui.Components.EmojiView.this;
                    r0 = r0.dragListener;
                    r0.onDragStart();
                    r0 = r7.startedScroll;
                    if (r0 == 0) goto L_0x007c;
                L_0x0071:
                    r0 = org.telegram.ui.Components.EmojiView.this;
                    r0 = r0.pager;
                    r0.endFakeDrag();
                    r7.startedScroll = r1;
                L_0x007c:
                    r0 = r7.draggingVertically;
                    r3 = 3;
                    if (r0 == 0) goto L_0x00e7;
                L_0x0081:
                    r0 = r7.vTracker;
                    if (r0 != 0) goto L_0x008b;
                L_0x0085:
                    r0 = android.view.VelocityTracker.obtain();
                    r7.vTracker = r0;
                L_0x008b:
                    r0 = r7.vTracker;
                    r0.addMovement(r8);
                    r0 = r8.getAction();
                    if (r0 == r2) goto L_0x00b2;
                L_0x0096:
                    r0 = r8.getAction();
                    if (r0 != r3) goto L_0x009d;
                L_0x009c:
                    goto L_0x00b2;
                L_0x009d:
                    r0 = org.telegram.ui.Components.EmojiView.this;
                    r0 = r0.dragListener;
                    r8 = r8.getRawY();
                    r1 = r7.downY;
                    r8 = r8 - r1;
                    r8 = java.lang.Math.round(r8);
                    r0.onDrag(r8);
                    goto L_0x00e6;
                L_0x00b2:
                    r0 = r7.vTracker;
                    r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
                    r0.computeCurrentVelocity(r3);
                    r0 = r7.vTracker;
                    r0 = r0.getYVelocity();
                    r3 = r7.vTracker;
                    r3.recycle();
                    r3 = 0;
                    r7.vTracker = r3;
                    r8 = r8.getAction();
                    if (r8 != r2) goto L_0x00d7;
                L_0x00cd:
                    r8 = org.telegram.ui.Components.EmojiView.this;
                    r8 = r8.dragListener;
                    r8.onDragEnd(r0);
                    goto L_0x00e0;
                L_0x00d7:
                    r8 = org.telegram.ui.Components.EmojiView.this;
                    r8 = r8.dragListener;
                    r8.onDragCancel();
                L_0x00e0:
                    r7.first = r2;
                    r7.draggingHorizontally = r1;
                    r7.draggingVertically = r1;
                L_0x00e6:
                    return r2;
                L_0x00e7:
                    r0 = org.telegram.ui.Components.EmojiView.this;
                    r0 = r0.stickersTab;
                    r0 = r0.getTranslationX();
                    r4 = org.telegram.ui.Components.EmojiView.this;
                    r4 = r4.stickersTab;
                    r4 = r4.getScrollX();
                    if (r4 != 0) goto L_0x0152;
                L_0x00fd:
                    r4 = 0;
                    r5 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
                    if (r5 != 0) goto L_0x0152;
                L_0x0102:
                    r5 = r7.startedScroll;
                    if (r5 != 0) goto L_0x012c;
                L_0x0106:
                    r5 = r7.lastX;
                    r6 = r8.getX();
                    r5 = r5 - r6;
                    r5 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1));
                    if (r5 >= 0) goto L_0x012c;
                L_0x0111:
                    r4 = org.telegram.ui.Components.EmojiView.this;
                    r4 = r4.pager;
                    r4 = r4.beginFakeDrag();
                    if (r4 == 0) goto L_0x0152;
                L_0x011d:
                    r7.startedScroll = r2;
                    r4 = org.telegram.ui.Components.EmojiView.this;
                    r4 = r4.stickersTab;
                    r4 = r4.getTranslationX();
                    r7.lastTranslateX = r4;
                    goto L_0x0152;
                L_0x012c:
                    r5 = r7.startedScroll;
                    if (r5 == 0) goto L_0x0152;
                L_0x0130:
                    r5 = r7.lastX;
                    r6 = r8.getX();
                    r5 = r5 - r6;
                    r4 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1));
                    if (r4 <= 0) goto L_0x0152;
                L_0x013b:
                    r4 = org.telegram.ui.Components.EmojiView.this;
                    r4 = r4.pager;
                    r4 = r4.isFakeDragging();
                    if (r4 == 0) goto L_0x0152;
                L_0x0147:
                    r4 = org.telegram.ui.Components.EmojiView.this;
                    r4 = r4.pager;
                    r4.endFakeDrag();
                    r7.startedScroll = r1;
                L_0x0152:
                    r4 = r7.startedScroll;
                    if (r4 == 0) goto L_0x017e;
                L_0x0156:
                    r4 = r8.getX();
                    r5 = r7.lastX;
                    r4 = r4 - r5;
                    r4 = r4 + r0;
                    r5 = r7.lastTranslateX;
                    r4 = r4 - r5;
                    r4 = (int) r4;
                    r5 = org.telegram.ui.Components.EmojiView.this;	 Catch:{ Exception -> 0x016f }
                    r5 = r5.pager;	 Catch:{ Exception -> 0x016f }
                    r4 = (float) r4;	 Catch:{ Exception -> 0x016f }
                    r5.fakeDragBy(r4);	 Catch:{ Exception -> 0x016f }
                    r7.lastTranslateX = r0;	 Catch:{ Exception -> 0x016f }
                    goto L_0x017e;
                L_0x016f:
                    r0 = move-exception;
                    r4 = org.telegram.ui.Components.EmojiView.this;	 Catch:{ Exception -> 0x0179 }
                    r4 = r4.pager;	 Catch:{ Exception -> 0x0179 }
                    r4.endFakeDrag();	 Catch:{ Exception -> 0x0179 }
                L_0x0179:
                    r7.startedScroll = r1;
                    org.telegram.messenger.FileLog.m3e(r0);
                L_0x017e:
                    r0 = r8.getX();
                    r7.lastX = r0;
                    r0 = r8.getAction();
                    if (r0 == r3) goto L_0x0190;
                L_0x018a:
                    r0 = r8.getAction();
                    if (r0 != r2) goto L_0x01a5;
                L_0x0190:
                    r7.first = r2;
                    r7.draggingHorizontally = r1;
                    r7.draggingVertically = r1;
                    r0 = r7.startedScroll;
                    if (r0 == 0) goto L_0x01a5;
                L_0x019a:
                    r0 = org.telegram.ui.Components.EmojiView.this;
                    r0 = r0.pager;
                    r0.endFakeDrag();
                    r7.startedScroll = r1;
                L_0x01a5:
                    r0 = r7.startedScroll;
                    if (r0 != 0) goto L_0x01af;
                L_0x01a9:
                    r8 = super.onTouchEvent(r8);
                    if (r8 == 0) goto L_0x01b0;
                L_0x01af:
                    r1 = r2;
                L_0x01b0:
                    return r1;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.21.onTouchEvent(android.view.MotionEvent):boolean");
                }
            };
            r0.stickersTab.setUnderlineHeight(AndroidUtilities.dp(1.0f));
            r0.stickersTab.setIndicatorColor(Theme.getColor(Theme.key_chat_emojiPanelStickerPackSelector));
            r0.stickersTab.setUnderlineColor(Theme.getColor(Theme.key_chat_emojiPanelStickerPackSelector));
            r0.stickersTab.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
            r0.stickersTab.setVisibility(4);
            addView(r0.stickersTab, LayoutHelper.createFrame(-1, 48, 51));
            r0.stickersTab.setTranslationX((float) AndroidUtilities.displaySize.x);
            updateStickerTabs();
            r0.stickersTab.setDelegate(new ScrollSlidingTabStripDelegate() {
                public void onPageSelected(int i) {
                    if (EmojiView.this.gifsGridView != null) {
                        if (i == EmojiView.this.gifTabNum + 1) {
                            if (EmojiView.this.gifsGridView.getVisibility() != 0) {
                                EmojiView.this.listener.onGifTab(true);
                                EmojiView.this.showGifTab();
                            }
                        } else if (i != EmojiView.this.trendingTabNum + 1) {
                            int i2 = 8;
                            TextView access$5400;
                            if (EmojiView.this.gifsGridView.getVisibility() == 0) {
                                EmojiView.this.listener.onGifTab(false);
                                EmojiView.this.gifsGridView.setVisibility(8);
                                EmojiView.this.stickersGridView.setVisibility(0);
                                EmojiView.this.searchEditTextContainer.setVisibility(0);
                                EmojiView.this.stickersGridView.getVisibility();
                                access$5400 = EmojiView.this.stickersEmptyView;
                                if (EmojiView.this.stickersGridAdapter.getItemCount() == 0) {
                                    i2 = 0;
                                }
                                access$5400.setVisibility(i2);
                                EmojiView.this.checkScroll();
                                EmojiView.this.saveNewPage();
                            } else if (EmojiView.this.trendingGridView.getVisibility() == 0) {
                                EmojiView.this.trendingGridView.setVisibility(8);
                                EmojiView.this.stickersGridView.setVisibility(0);
                                EmojiView.this.searchEditTextContainer.setVisibility(0);
                                access$5400 = EmojiView.this.stickersEmptyView;
                                if (EmojiView.this.stickersGridAdapter.getItemCount() == 0) {
                                    i2 = 0;
                                }
                                access$5400.setVisibility(i2);
                                EmojiView.this.saveNewPage();
                            }
                        } else if (EmojiView.this.trendingGridView.getVisibility() != 0) {
                            EmojiView.this.showTrendingTab();
                        }
                    }
                    if (i == 0) {
                        EmojiView.this.pager.setCurrentItem(0);
                        return;
                    }
                    if (i != EmojiView.this.gifTabNum + 1) {
                        if (i != EmojiView.this.trendingTabNum + 1) {
                            if (i == EmojiView.this.recentTabBum + 1) {
                                EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(EmojiView.this.stickersGridAdapter.getPositionForPack("recent"), 0);
                                EmojiView.this.checkStickersTabY(null, 0);
                                EmojiView.this.stickersTab.onPageScrolled(EmojiView.this.recentTabBum + 1, (EmojiView.this.recentTabBum > 0 ? EmojiView.this.recentTabBum : EmojiView.this.stickersTabOffset) + 1);
                            } else if (i == EmojiView.this.favTabBum + 1) {
                                EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(EmojiView.this.stickersGridAdapter.getPositionForPack("fav"), 0);
                                EmojiView.this.checkStickersTabY(null, 0);
                                EmojiView.this.stickersTab.onPageScrolled(EmojiView.this.favTabBum + 1, (EmojiView.this.favTabBum > 0 ? EmojiView.this.favTabBum : EmojiView.this.stickersTabOffset) + 1);
                            } else {
                                i = (i - 1) - EmojiView.this.stickersTabOffset;
                                if (i >= EmojiView.this.stickerSets.size()) {
                                    if (EmojiView.this.listener != 0) {
                                        EmojiView.this.listener.onStickersSettingsClick();
                                    }
                                    return;
                                }
                                if (i >= EmojiView.this.stickerSets.size()) {
                                    i = EmojiView.this.stickerSets.size() - 1;
                                }
                                EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(EmojiView.this.stickersGridAdapter.getPositionForPack(EmojiView.this.stickerSets.get(i)), 0);
                                EmojiView.this.checkStickersTabY(null, 0);
                                EmojiView.this.checkScroll();
                            }
                        }
                    }
                }
            });
            r0.stickersGridView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    if (i == 1) {
                        AndroidUtilities.hideKeyboard(EmojiView.this.searchEditText);
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    EmojiView.this.checkScroll();
                    EmojiView.this.checkStickersTabY(recyclerView, i2);
                    EmojiView.this.checkSearchFieldScroll();
                }
            });
        }
        r0.pager = new ViewPager(context2) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(motionEvent);
            }
        };
        r0.pager.setAdapter(new EmojiPagesAdapter());
        r0.emojiTab = new LinearLayout(context2) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(motionEvent);
            }
        };
        r0.emojiTab.setOrientation(0);
        addView(r0.emojiTab, LayoutHelper.createFrame(-1, 48.0f));
        r0.pagerSlidingTabStrip = new PagerSlidingTabStrip(context2);
        r0.pagerSlidingTabStrip.setViewPager(r0.pager);
        r0.pagerSlidingTabStrip.setShouldExpand(true);
        r0.pagerSlidingTabStrip.setIndicatorHeight(AndroidUtilities.dp(2.0f));
        r0.pagerSlidingTabStrip.setUnderlineHeight(AndroidUtilities.dp(1.0f));
        r0.pagerSlidingTabStrip.setIndicatorColor(Theme.getColor(Theme.key_chat_emojiPanelIconSelector));
        r0.pagerSlidingTabStrip.setUnderlineColor(Theme.getColor(Theme.key_chat_emojiPanelShadowLine));
        r0.emojiTab.addView(r0.pagerSlidingTabStrip, LayoutHelper.createLinear(0, 48, 1.0f));
        r0.pagerSlidingTabStrip.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {
            }

            public void onPageScrolled(int i, float f, int i2) {
                EmojiView.this.onPageScrolled(i, (EmojiView.this.getMeasuredWidth() - EmojiView.this.getPaddingLeft()) - EmojiView.this.getPaddingRight(), i2);
            }

            public void onPageSelected(int i) {
                EmojiView.this.saveNewPage();
            }
        });
        View frameLayout2 = new FrameLayout(context2);
        r0.emojiTab.addView(frameLayout2, LayoutHelper.createLinear(52, 48));
        r0.backspaceButton = new ImageView(context2) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    EmojiView.this.backspacePressed = true;
                    EmojiView.this.backspaceOnce = false;
                    EmojiView.this.postBackspaceRunnable(350);
                } else if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
                    EmojiView.this.backspacePressed = false;
                    if (!(EmojiView.this.backspaceOnce || EmojiView.this.listener == null || !EmojiView.this.listener.onBackspace())) {
                        EmojiView.this.backspaceButton.performHapticFeedback(3);
                    }
                }
                super.onTouchEvent(motionEvent);
                return true;
            }
        };
        r0.backspaceButton.setImageResource(C0446R.drawable.ic_smiles_backspace);
        r0.backspaceButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelBackspace), Mode.MULTIPLY));
        r0.backspaceButton.setScaleType(ScaleType.CENTER);
        frameLayout2.addView(r0.backspaceButton, LayoutHelper.createFrame(52, 48.0f));
        r0.shadowLine = new View(context2);
        r0.shadowLine.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelShadowLine));
        frameLayout2.addView(r0.shadowLine, LayoutHelper.createFrame(52, 1, 83));
        r0.noRecentTextView = new TextView(context2);
        r0.noRecentTextView.setText(LocaleController.getString("NoRecent", C0446R.string.NoRecent));
        r0.noRecentTextView.setTextSize(1, 18.0f);
        r0.noRecentTextView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelEmptyText));
        r0.noRecentTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.noRecentTextView.setGravity(17);
        r0.noRecentTextView.setClickable(false);
        r0.noRecentTextView.setFocusable(false);
        ((FrameLayout) r0.views.get(0)).addView(r0.noRecentTextView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 48.0f, 0.0f, 0.0f));
        ((GridView) r0.emojiGrids.get(0)).setEmptyView(r0.noRecentTextView);
        addView(r0.pager, 0, LayoutHelper.createFrame(-1, -1, 51));
        r0.mediaBanTooltip = new CorrectlyMeasuringTextView(context2);
        r0.mediaBanTooltip.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor(Theme.key_chat_gifSaveHintBackground)));
        r0.mediaBanTooltip.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
        r0.mediaBanTooltip.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(7.0f));
        r0.mediaBanTooltip.setGravity(16);
        r0.mediaBanTooltip.setTextSize(1, 14.0f);
        r0.mediaBanTooltip.setVisibility(4);
        addView(r0.mediaBanTooltip, LayoutHelper.createFrame(-2, -2.0f, 53, 30.0f, 53.0f, 5.0f, 0.0f));
        r0.emojiSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 32.0f);
        r0.pickerView = new EmojiColorPickerView(context2);
        View view = r0.pickerView;
        int dp = AndroidUtilities.dp((float) ((((AndroidUtilities.isTablet() ? 40 : 32) * 6) + 10) + 20));
        r0.popupWidth = dp;
        int dp2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 64.0f : 56.0f);
        r0.popupHeight = dp2;
        r0.pickerViewPopup = new EmojiPopupWindow(view, dp, dp2);
        r0.pickerViewPopup.setOutsideTouchable(true);
        r0.pickerViewPopup.setClippingEnabled(true);
        r0.pickerViewPopup.setInputMethodMode(2);
        r0.pickerViewPopup.setSoftInputMode(0);
        r0.pickerViewPopup.getContentView().setFocusableInTouchMode(true);
        r0.pickerViewPopup.getContentView().setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i != 82 || keyEvent.getRepeatCount() != null || keyEvent.getAction() != 1 || EmojiView.this.pickerViewPopup == null || EmojiView.this.pickerViewPopup.isShowing() == null) {
                    return null;
                }
                EmojiView.this.pickerViewPopup.dismiss();
                return true;
            }
        });
        r0.currentPage = MessagesController.getGlobalEmojiSettings().getInt("selected_page", 0);
        Emoji.loadRecentEmoji();
        ((EmojiGridAdapter) r0.adapters.get(0)).notifyDataSetChanged();
    }

    public void showSearchField(boolean z) {
        boolean findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (z) {
            if (findFirstVisibleItemPosition || findFirstVisibleItemPosition) {
                this.stickersLayoutManager.scrollToPosition(0);
                this.stickersTab.setTranslationY(0.0f);
            }
        } else if (!findFirstVisibleItemPosition) {
            this.stickersLayoutManager.scrollToPositionWithOffset(1, 0);
        }
    }

    public void hideSearchKeyboard() {
        AndroidUtilities.hideKeyboard(this.searchEditText);
    }

    public void closeSearch(boolean z) {
        closeSearch(z, -1);
    }

    public void closeSearch(boolean z, long j) {
        if (this.searchAnimation != null) {
            this.searchAnimation.cancel();
            this.searchAnimation = null;
        }
        this.searchEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
        if (j != -1) {
            j = DataQuery.getInstance(this.currentAccount).getStickerSetById(j);
            if (j != null) {
                j = this.stickersGridAdapter.getPositionForPack(j);
                if (j >= null) {
                    this.stickersLayoutManager.scrollToPositionWithOffset(j, AndroidUtilities.dp(60.0f));
                }
            }
        }
        if (z) {
            this.searchAnimation = new AnimatorSet();
            z = this.searchAnimation;
            r2 = new Animator[3];
            r2[0] = ObjectAnimator.ofFloat(this.stickersTab, "translationY", new float[]{0.0f});
            r2[1] = ObjectAnimator.ofFloat(this.stickersGridView, "translationY", new float[]{(float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight)});
            r2[2] = ObjectAnimator.ofFloat(this.searchEditTextContainer, "translationY", new float[]{(float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight)});
            z.playTogether(r2);
            this.searchAnimation.setDuration(200);
            this.searchAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.searchAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(EmojiView.this.searchAnimation) != null) {
                        EmojiView.this.stickersLayoutManager.findFirstVisibleItemPosition();
                        animator = EmojiView.this.stickersLayoutManager.findFirstVisibleItemPosition();
                        int top = animator != -1 ? (int) (((float) EmojiView.this.stickersLayoutManager.findViewByPosition(animator).getTop()) + EmojiView.this.stickersGridView.getTranslationY()) : 0;
                        EmojiView.this.stickersGridView.setTranslationY(0.0f);
                        EmojiView.this.stickersGridView.setPadding(0, AndroidUtilities.dp(52.0f), 0, 0);
                        if (animator != -1) {
                            EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(animator, top - EmojiView.this.stickersGridView.getPaddingTop());
                        }
                        EmojiView.this.searchAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(EmojiView.this.searchAnimation) != null) {
                        EmojiView.this.searchAnimation = null;
                    }
                }
            });
            this.searchAnimation.start();
            return;
        }
        this.stickersLayoutManager.scrollToPositionWithOffset(1, 0);
        this.searchEditTextContainer.setTranslationY((float) (AndroidUtilities.dp(48.0f) - this.searchFieldHeight));
        this.stickersTab.setTranslationY(0.0f);
        this.stickersGridView.setPadding(0, AndroidUtilities.dp(NUM), 0, 0);
        this.listener.onSearchOpenClose(false);
    }

    private void checkStickersTabY(View view, int i) {
        if (view == null) {
            view = this.stickersTab;
            this.minusDy = 0;
            view.setTranslationY((float) null);
        } else if (view.getVisibility() == null) {
            if (this.listener == null || this.listener.isSearchOpened() == null) {
                if (i > 0 && this.stickersGridView != null && this.stickersGridView.getVisibility() == null) {
                    view = this.stickersGridView.findViewHolderForAdapterPosition(0);
                    if (view != null && view.itemView.getTop() + this.searchFieldHeight >= this.stickersGridView.getPaddingTop()) {
                        return;
                    }
                }
                this.minusDy -= i;
                if (this.minusDy > null) {
                    this.minusDy = 0;
                } else if (this.minusDy < (-AndroidUtilities.dp(288.0f))) {
                    this.minusDy = -AndroidUtilities.dp(288.0f);
                }
                this.stickersTab.setTranslationY((float) Math.max(-AndroidUtilities.dp(NUM), this.minusDy));
            }
        }
    }

    private void checkSearchFieldScroll() {
        if (this.stickersGridView != null) {
            if (this.listener == null || !this.listener.isSearchOpened()) {
                ViewHolder findViewHolderForAdapterPosition = this.stickersGridView.findViewHolderForAdapterPosition(0);
                if (findViewHolderForAdapterPosition != null) {
                    this.searchEditTextContainer.setTranslationY((float) findViewHolderForAdapterPosition.itemView.getTop());
                } else {
                    this.searchEditTextContainer.setTranslationY((float) (-this.searchFieldHeight));
                }
            }
        }
    }

    private void checkScroll() {
        int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (findFirstVisibleItemPosition != -1 && this.stickersGridView != null) {
            int i;
            if (this.favTabBum > 0) {
                i = this.favTabBum;
            } else if (this.recentTabBum > 0) {
                i = this.recentTabBum;
            } else {
                i = this.stickersTabOffset;
            }
            if (this.stickersGridView.getVisibility() != 0) {
                if (!(this.gifsGridView == null || this.gifsGridView.getVisibility() == 0)) {
                    this.gifsGridView.setVisibility(0);
                }
                if (this.stickersEmptyView != null && this.stickersEmptyView.getVisibility() == 0) {
                    this.stickersEmptyView.setVisibility(8);
                }
                this.stickersTab.onPageScrolled(this.gifTabNum + 1, i + 1);
                return;
            }
            this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition) + 1, i + 1);
        }
    }

    private void saveNewPage() {
        int i = this.pager.getCurrentItem() == 6 ? (this.gifsGridView == null || this.gifsGridView.getVisibility() != 0) ? 1 : 2 : 0;
        if (this.currentPage != i) {
            this.currentPage = i;
            MessagesController.getGlobalEmojiSettings().edit().putInt("selected_page", i).commit();
        }
    }

    public void clearRecentEmoji() {
        Emoji.clearRecentEmoji();
        ((EmojiGridAdapter) this.adapters.get(0)).notifyDataSetChanged();
    }

    private void showTrendingTab() {
        this.trendingGridView.setVisibility(0);
        this.stickersGridView.setVisibility(8);
        this.firstAttach = true;
        this.searchEditTextContainer.setVisibility(8);
        this.stickersEmptyView.setVisibility(8);
        this.gifsGridView.setVisibility(8);
        this.stickersTab.onPageScrolled(this.trendingTabNum + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
        saveNewPage();
    }

    private void showGifTab() {
        this.gifsGridView.setVisibility(0);
        this.stickersGridView.setVisibility(8);
        this.firstAttach = true;
        this.searchEditTextContainer.setVisibility(8);
        this.stickersEmptyView.setVisibility(8);
        this.trendingGridView.setVisibility(8);
        this.stickersTab.onPageScrolled(this.gifTabNum + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
        saveNewPage();
    }

    private void onPageScrolled(int i, int i2, int i3) {
        if (this.stickersTab != null) {
            if (i2 == 0) {
                i2 = AndroidUtilities.displaySize.x;
            }
            boolean z = true;
            int i4 = 0;
            if (i == 5) {
                i = -i3;
                if (this.listener != null) {
                    Listener listener = this.listener;
                    if (i3 == 0) {
                        z = false;
                    }
                    listener.onStickersTab(z);
                }
            } else if (i == 6) {
                i = -i2;
                if (this.listener != 0) {
                    this.listener.onStickersTab(true);
                }
            } else {
                if (this.listener != 0) {
                    this.listener.onStickersTab(false);
                }
                i = 0;
            }
            float f = (float) i;
            if (this.emojiTab.getTranslationX() != f) {
                this.emojiTab.setTranslationX(f);
                this.stickersTab.setTranslationX((float) (i2 + i));
                i2 = this.stickersTab;
                if (i >= 0) {
                    i4 = 4;
                }
                i2.setVisibility(i4);
            }
        }
    }

    private void postBackspaceRunnable(final int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (EmojiView.this.backspacePressed) {
                    if (EmojiView.this.listener != null && EmojiView.this.listener.onBackspace()) {
                        EmojiView.this.backspaceButton.performHapticFeedback(3);
                    }
                    EmojiView.this.backspaceOnce = true;
                    EmojiView.this.postBackspaceRunnable(Math.max(50, i - 100));
                }
            }
        }, (long) i);
    }

    public void switchToGifRecent() {
        if (this.gifTabNum < 0 || this.recentGifs.isEmpty()) {
            this.switchToGifTab = true;
        } else {
            this.stickersTab.selectTab(this.gifTabNum + 1);
        }
        this.pager.setCurrentItem(6);
    }

    private void updateStickerTabs() {
        if (this.stickersTab != null) {
            Drawable drawable;
            this.recentTabBum = -2;
            this.favTabBum = -2;
            this.gifTabNum = -2;
            this.trendingTabNum = -2;
            this.stickersTabOffset = 0;
            int currentPosition = this.stickersTab.getCurrentPosition();
            this.stickersTab.removeTabs();
            Drawable drawable2 = getContext().getResources().getDrawable(C0446R.drawable.ic_smiles2_smile);
            Theme.setDrawableColorByKey(drawable2, Theme.key_chat_emojiPanelIcon);
            this.stickersTab.addIconTab(drawable2);
            if (this.showGifs && !this.recentGifs.isEmpty()) {
                drawable2 = getContext().getResources().getDrawable(C0446R.drawable.ic_smiles_gif);
                Theme.setDrawableColorByKey(drawable2, Theme.key_chat_emojiPanelIcon);
                this.stickersTab.addIconTab(drawable2);
                this.gifTabNum = this.stickersTabOffset;
                this.stickersTabOffset++;
            }
            ArrayList unreadStickerSets = DataQuery.getInstance(this.currentAccount).getUnreadStickerSets();
            if (!(this.trendingGridAdapter == null || this.trendingGridAdapter.getItemCount() == 0 || unreadStickerSets.isEmpty())) {
                drawable = getContext().getResources().getDrawable(C0446R.drawable.ic_smiles_trend);
                Theme.setDrawableColorByKey(drawable, Theme.key_chat_emojiPanelIcon);
                TextView addIconTabWithCounter = this.stickersTab.addIconTabWithCounter(drawable);
                this.trendingTabNum = this.stickersTabOffset;
                this.stickersTabOffset++;
                addIconTabWithCounter.setText(String.format("%d", new Object[]{Integer.valueOf(unreadStickerSets.size())}));
            }
            if (!this.favouriteStickers.isEmpty()) {
                this.favTabBum = this.stickersTabOffset;
                this.stickersTabOffset++;
                drawable = getContext().getResources().getDrawable(C0446R.drawable.staredstickerstab);
                Theme.setDrawableColorByKey(drawable, Theme.key_chat_emojiPanelIcon);
                this.stickersTab.addIconTab(drawable);
            }
            if (!this.recentStickers.isEmpty()) {
                this.recentTabBum = this.stickersTabOffset;
                this.stickersTabOffset++;
                drawable = getContext().getResources().getDrawable(C0446R.drawable.ic_smiles2_recent);
                Theme.setDrawableColorByKey(drawable, Theme.key_chat_emojiPanelIcon);
                this.stickersTab.addIconTab(drawable);
            }
            this.stickerSets.clear();
            TL_messages_stickerSet tL_messages_stickerSet = null;
            this.groupStickerSet = null;
            this.groupStickerPackPosition = -1;
            this.groupStickerPackNum = -10;
            ArrayList stickerSets = DataQuery.getInstance(this.currentAccount).getStickerSets(0);
            for (int i = 0; i < stickerSets.size(); i++) {
                TL_messages_stickerSet tL_messages_stickerSet2 = (TL_messages_stickerSet) stickerSets.get(i);
                if (!(tL_messages_stickerSet2.set.archived || tL_messages_stickerSet2.documents == null)) {
                    if (!tL_messages_stickerSet2.documents.isEmpty()) {
                        this.stickerSets.add(tL_messages_stickerSet2);
                    }
                }
            }
            if (this.info != null) {
                TL_messages_stickerSet groupStickerSetById;
                TL_messages_stickerSet tL_messages_stickerSet3;
                SharedPreferences emojiSettings = MessagesController.getEmojiSettings(this.currentAccount);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("group_hide_stickers_");
                stringBuilder.append(this.info.id);
                long j = emojiSettings.getLong(stringBuilder.toString(), -1);
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.info.id));
                if (!(chat == null || this.info.stickerset == null)) {
                    if (ChatObject.hasAdminRights(chat)) {
                        if (this.info.stickerset != null) {
                            this.groupStickersHidden = j == this.info.stickerset.id;
                        }
                        if (this.info.stickerset != null) {
                            groupStickerSetById = DataQuery.getInstance(this.currentAccount).getGroupStickerSetById(this.info.stickerset);
                            if (!(groupStickerSetById == null || groupStickerSetById.documents == null || groupStickerSetById.documents.isEmpty() || groupStickerSetById.set == null)) {
                                tL_messages_stickerSet3 = new TL_messages_stickerSet();
                                tL_messages_stickerSet3.documents = groupStickerSetById.documents;
                                tL_messages_stickerSet3.packs = groupStickerSetById.packs;
                                tL_messages_stickerSet3.set = groupStickerSetById.set;
                                if (this.groupStickersHidden) {
                                    this.groupStickerPackNum = 0;
                                    this.stickerSets.add(0, tL_messages_stickerSet3);
                                } else {
                                    this.groupStickerPackNum = this.stickerSets.size();
                                    this.stickerSets.add(tL_messages_stickerSet3);
                                }
                                if (this.info.can_set_stickers) {
                                    tL_messages_stickerSet = tL_messages_stickerSet3;
                                }
                                this.groupStickerSet = tL_messages_stickerSet;
                            }
                        } else if (this.info.can_set_stickers) {
                            tL_messages_stickerSet = new TL_messages_stickerSet();
                            if (this.groupStickersHidden) {
                                this.groupStickerPackNum = 0;
                                this.stickerSets.add(0, tL_messages_stickerSet);
                            } else {
                                this.groupStickerPackNum = this.stickerSets.size();
                                this.stickerSets.add(tL_messages_stickerSet);
                            }
                        }
                    }
                }
                this.groupStickersHidden = j != -1;
                if (this.info.stickerset != null) {
                    groupStickerSetById = DataQuery.getInstance(this.currentAccount).getGroupStickerSetById(this.info.stickerset);
                    tL_messages_stickerSet3 = new TL_messages_stickerSet();
                    tL_messages_stickerSet3.documents = groupStickerSetById.documents;
                    tL_messages_stickerSet3.packs = groupStickerSetById.packs;
                    tL_messages_stickerSet3.set = groupStickerSetById.set;
                    if (this.groupStickersHidden) {
                        this.groupStickerPackNum = 0;
                        this.stickerSets.add(0, tL_messages_stickerSet3);
                    } else {
                        this.groupStickerPackNum = this.stickerSets.size();
                        this.stickerSets.add(tL_messages_stickerSet3);
                    }
                    if (this.info.can_set_stickers) {
                        tL_messages_stickerSet = tL_messages_stickerSet3;
                    }
                    this.groupStickerSet = tL_messages_stickerSet;
                } else if (this.info.can_set_stickers) {
                    tL_messages_stickerSet = new TL_messages_stickerSet();
                    if (this.groupStickersHidden) {
                        this.groupStickerPackNum = 0;
                        this.stickerSets.add(0, tL_messages_stickerSet);
                    } else {
                        this.groupStickerPackNum = this.stickerSets.size();
                        this.stickerSets.add(tL_messages_stickerSet);
                    }
                }
            }
            int i2 = 0;
            while (i2 < this.stickerSets.size()) {
                if (i2 == this.groupStickerPackNum) {
                    Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.info.id));
                    if (chat2 == null) {
                        this.stickerSets.remove(0);
                        i2--;
                    } else {
                        this.stickersTab.addStickerTab(chat2);
                    }
                } else {
                    this.stickersTab.addStickerTab((Document) ((TL_messages_stickerSet) this.stickerSets.get(i2)).documents.get(0));
                }
                i2++;
            }
            if (!(this.trendingGridAdapter == null || this.trendingGridAdapter.getItemCount() == 0 || !unreadStickerSets.isEmpty())) {
                drawable2 = getContext().getResources().getDrawable(C0446R.drawable.ic_smiles_trend);
                Theme.setDrawableColorByKey(drawable2, Theme.key_chat_emojiPanelIcon);
                this.trendingTabNum = this.stickersTabOffset + this.stickerSets.size();
                this.stickersTab.addIconTab(drawable2);
            }
            drawable2 = getContext().getResources().getDrawable(C0446R.drawable.ic_smiles_settings);
            Theme.setDrawableColorByKey(drawable2, Theme.key_chat_emojiPanelIcon);
            this.stickersTab.addIconTab(drawable2);
            this.stickersTab.updateTabStyles();
            if (currentPosition != 0) {
                this.stickersTab.onPageScrolled(currentPosition, currentPosition);
            }
            if (this.switchToGifTab && this.gifTabNum >= 0 && this.gifsGridView.getVisibility() != 0) {
                showGifTab();
                this.switchToGifTab = false;
            }
            checkPanels();
        }
    }

    private void checkPanels() {
        if (this.stickersTab != null) {
            int i = 8;
            if (this.trendingTabNum == -2 && this.trendingGridView != null && this.trendingGridView.getVisibility() == 0) {
                this.gifsGridView.setVisibility(8);
                this.trendingGridView.setVisibility(8);
                this.stickersGridView.setVisibility(0);
                this.searchEditTextContainer.setVisibility(0);
                this.stickersEmptyView.setVisibility(this.stickersGridAdapter.getItemCount() != 0 ? 8 : 0);
            }
            if (this.gifTabNum == -2 && this.gifsGridView != null && this.gifsGridView.getVisibility() == 0) {
                this.listener.onGifTab(false);
                this.gifsGridView.setVisibility(8);
                this.trendingGridView.setVisibility(8);
                this.stickersGridView.setVisibility(0);
                this.searchEditTextContainer.setVisibility(0);
                TextView textView = this.stickersEmptyView;
                if (this.stickersGridAdapter.getItemCount() == 0) {
                    i = 0;
                }
                textView.setVisibility(i);
            } else if (this.gifTabNum != -2) {
                if (this.gifsGridView != null && this.gifsGridView.getVisibility() == 0) {
                    this.stickersTab.onPageScrolled(this.gifTabNum + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
                } else if (this.trendingGridView == null || this.trendingGridView.getVisibility() != 0) {
                    int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
                    if (findFirstVisibleItemPosition != -1) {
                        int i2;
                        if (this.favTabBum > 0) {
                            i2 = this.favTabBum;
                        } else if (this.recentTabBum > 0) {
                            i2 = this.recentTabBum;
                        } else {
                            i2 = this.stickersTabOffset;
                        }
                        this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition) + 1, i2 + 1);
                    }
                } else {
                    this.stickersTab.onPageScrolled(this.trendingTabNum + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
                }
            }
        }
    }

    public void addRecentSticker(Document document) {
        if (document != null) {
            DataQuery.getInstance(this.currentAccount).addRecentSticker(0, document, (int) (System.currentTimeMillis() / 1000), false);
            document = this.recentStickers.isEmpty();
            this.recentStickers = DataQuery.getInstance(this.currentAccount).getRecentStickers(0);
            if (this.stickersGridAdapter != null) {
                this.stickersGridAdapter.notifyDataSetChanged();
            }
            if (document != null) {
                updateStickerTabs();
            }
        }
    }

    public void addRecentGif(Document document) {
        if (document != null) {
            document = this.recentGifs.isEmpty();
            this.recentGifs = DataQuery.getInstance(this.currentAccount).getRecentGifs();
            if (this.gifsAdapter != null) {
                this.gifsAdapter.notifyDataSetChanged();
            }
            if (document != null) {
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
        if (AndroidUtilities.isInMultiwindow) {
            getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelBackground), Mode.MULTIPLY));
        } else {
            setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
            this.emojiTab.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
        }
        if (this.searchEditTextContainer != null) {
            this.searchEditTextContainer.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
        }
        if (this.dotPaint != null) {
            this.dotPaint.setColor(Theme.getColor(Theme.key_chat_emojiPanelNewTrending));
        }
        if (this.stickersGridView != null) {
            this.stickersGridView.setGlowColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
        }
        if (this.trendingGridView != null) {
            this.trendingGridView.setGlowColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
        }
        if (this.stickersEmptyView != null) {
            this.stickersEmptyView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelEmptyText));
        }
        if (this.stickersTab != null) {
            this.stickersTab.setIndicatorColor(Theme.getColor(Theme.key_chat_emojiPanelStickerPackSelector));
            this.stickersTab.setUnderlineColor(Theme.getColor(Theme.key_chat_emojiPanelStickerPackSelector));
            this.stickersTab.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
        }
        if (this.pagerSlidingTabStrip != null) {
            this.pagerSlidingTabStrip.setIndicatorColor(Theme.getColor(Theme.key_chat_emojiPanelIconSelector));
            this.pagerSlidingTabStrip.setUnderlineColor(Theme.getColor(Theme.key_chat_emojiPanelShadowLine));
        }
        if (this.backspaceButton != null) {
            this.backspaceButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelBackspace), Mode.MULTIPLY));
        }
        if (this.searchIconImageView != null) {
            this.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelIcon), Mode.MULTIPLY));
        }
        if (this.shadowLine != null) {
            this.shadowLine.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelShadowLine));
        }
        if (this.noRecentTextView != null) {
            this.noRecentTextView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelEmptyText));
        }
        if (this.mediaBanTooltip != null) {
            ((ShapeDrawable) this.mediaBanTooltip.getBackground()).getPaint().setColor(Theme.getColor(Theme.key_chat_gifSaveHintBackground));
            this.mediaBanTooltip.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
        }
        Theme.setDrawableColorByKey(this.stickersDrawable, Theme.key_chat_emojiPanelIcon);
        for (int i = 0; i < this.icons.length - 1; i++) {
            Theme.setEmojiDrawableColor(this.icons[i], Theme.getColor(Theme.key_chat_emojiPanelIcon), false);
            Theme.setEmojiDrawableColor(this.icons[i], Theme.getColor(Theme.key_chat_emojiPanelIconSelected), true);
        }
        if (this.searchBackground != null) {
            Theme.setDrawableColorByKey(this.searchBackground.getBackground(), Theme.key_chat_emojiSearchBackground);
            this.searchBackground.invalidate();
        }
    }

    public void onMeasure(int i, int i2) {
        this.isLayout = true;
        ViewGroup.LayoutParams layoutParams = null;
        if (AndroidUtilities.isInMultiwindow) {
            if (this.currentBackgroundType != 1) {
                if (VERSION.SDK_INT >= 21) {
                    setOutlineProvider((ViewOutlineProvider) this.outlineProvider);
                    setClipToOutline(true);
                    setElevation((float) AndroidUtilities.dp(2.0f));
                }
                setBackgroundResource(C0446R.drawable.smiles_popup);
                getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelBackground), Mode.MULTIPLY));
                this.emojiTab.setBackgroundDrawable(null);
                this.currentBackgroundType = 1;
            }
        } else if (this.currentBackgroundType != 0) {
            if (VERSION.SDK_INT >= 21) {
                setOutlineProvider(null);
                setClipToOutline(false);
                setElevation(0.0f);
            }
            setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
            this.emojiTab.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
            this.currentBackgroundType = 0;
        }
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.emojiTab.getLayoutParams();
        layoutParams2.width = MeasureSpec.getSize(i);
        if (this.stickersTab != 0) {
            layoutParams = (FrameLayout.LayoutParams) this.stickersTab.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.width = layoutParams2.width;
            }
        }
        if (layoutParams2.width != this.oldWidth) {
            if (!(this.stickersTab == 0 || layoutParams == null)) {
                onPageScrolled(this.pager.getCurrentItem(), (layoutParams2.width - getPaddingLeft()) - getPaddingRight(), 0);
                this.stickersTab.setLayoutParams(layoutParams);
            }
            this.emojiTab.setLayoutParams(layoutParams2);
            this.oldWidth = layoutParams2.width;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(layoutParams2.width, NUM), MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i2), NUM));
        this.isLayout = false;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        if (this.lastNotifyWidth != i5) {
            this.lastNotifyWidth = i5;
            reloadStickersAdapter();
        }
        super.onLayout(z, i, i2, i3, i4);
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
        if (StickerPreviewViewer.getInstance().isVisible()) {
            StickerPreviewViewer.getInstance().close();
        }
        StickerPreviewViewer.getInstance().reset();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    public void setChatInfo(ChatFull chatFull) {
        this.info = chatFull;
        updateStickerTabs();
    }

    public void invalidateViews() {
        for (int i = 0; i < this.emojiGrids.size(); i++) {
            ((GridView) this.emojiGrids.get(i)).invalidateViews();
        }
    }

    public void onOpen(boolean z) {
        if (this.stickersTab != null) {
            if (!(this.currentPage == 0 || this.currentChatId == 0)) {
                this.currentPage = 0;
            }
            if (this.currentPage != 0) {
                if (!z) {
                    if (this.currentPage) {
                        if (!this.pager.getCurrentItem()) {
                            this.pager.setCurrentItem(6);
                        }
                        if (this.stickersTab.getCurrentPosition() != this.gifTabNum + 1) {
                            return;
                        }
                        if (this.recentTabBum < false) {
                            this.stickersTab.selectTab(this.recentTabBum + 1);
                            return;
                        } else if (this.favTabBum < false) {
                            this.stickersTab.selectTab(this.favTabBum + 1);
                            return;
                        } else if (this.gifTabNum < false) {
                            this.stickersTab.selectTab(this.gifTabNum + 2);
                            return;
                        } else {
                            this.stickersTab.selectTab(1);
                            return;
                        }
                    } else if (this.currentPage) {
                        if (!this.pager.getCurrentItem()) {
                            this.pager.setCurrentItem(6);
                        }
                        if (this.stickersTab.getCurrentPosition() == this.gifTabNum + 1) {
                            return;
                        }
                        if (this.gifTabNum >= false || this.recentGifs.isEmpty()) {
                            this.switchToGifTab = true;
                            return;
                        } else {
                            this.stickersTab.selectTab(this.gifTabNum + 1);
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }
            if (this.pager.getCurrentItem() == 6) {
                this.pager.setCurrentItem(0, z ^ true);
            }
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentImagesDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoaded);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    EmojiView.this.updateStickerTabs();
                    EmojiView.this.reloadStickersAdapter();
                }
            });
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i != 8) {
            Emoji.sortEmoji();
            ((EmojiGridAdapter) this.adapters.get(0)).notifyDataSetChanged();
            if (this.stickersGridAdapter != 0) {
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoaded);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoaded);
                updateStickerTabs();
                reloadStickersAdapter();
                if (!(this.gifsGridView == 0 || this.gifsGridView.getVisibility() != 0 || this.listener == 0)) {
                    i = this.listener;
                    boolean z = this.pager != null && this.pager.getCurrentItem() >= 6;
                    i.onGifTab(z);
                }
            }
            if (this.trendingGridAdapter != 0) {
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
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentDocumentsDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoaded);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.pickerViewPopup != null && this.pickerViewPopup.isShowing()) {
            this.pickerViewPopup.dismiss();
        }
    }

    private void checkDocuments(boolean z) {
        if (z) {
            z = this.recentGifs.size();
            this.recentGifs = DataQuery.getInstance(this.currentAccount).getRecentGifs();
            if (this.gifsAdapter != null) {
                this.gifsAdapter.notifyDataSetChanged();
            }
            if (z != this.recentGifs.size()) {
                updateStickerTabs();
            }
            if (this.stickersGridAdapter) {
                this.stickersGridAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        z = this.recentStickers.size();
        boolean size = this.favouriteStickers.size();
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
        if (!(z == this.recentStickers.size() && size == this.favouriteStickers.size())) {
            updateStickerTabs();
        }
        if (this.stickersGridAdapter) {
            this.stickersGridAdapter.notifyDataSetChanged();
        }
        checkPanels();
    }

    public void setStickersBanned(boolean z, int i) {
        if (z) {
            this.currentChatId = i;
        } else {
            this.currentChatId = 0;
        }
        z = this.pagerSlidingTabStrip.getTab(6);
        if (z) {
            z.setAlpha(this.currentChatId != 0 ? 0.5f : 1.0f);
            if (this.currentChatId && this.pager.getCurrentItem()) {
                this.pager.setCurrentItem(0);
            }
        }
    }

    public void showStickerBanHint() {
        if (this.mediaBanTooltip.getVisibility() != 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.currentChatId));
            if (chat != null) {
                if (chat.banned_rights != null) {
                    if (AndroidUtilities.isBannedForever(chat.banned_rights.until_date)) {
                        this.mediaBanTooltip.setText(LocaleController.getString("AttachStickersRestrictedForever", C0446R.string.AttachStickersRestrictedForever));
                    } else {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachStickersRestricted", C0446R.string.AttachStickersRestricted, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
                    }
                    this.mediaBanTooltip.setVisibility(0);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.mediaBanTooltip, "alpha", new float[]{0.0f, 1.0f})});
                    animatorSet.addListener(new AnimatorListenerAdapter() {

                        /* renamed from: org.telegram.ui.Components.EmojiView$32$1 */
                        class C11471 implements Runnable {

                            /* renamed from: org.telegram.ui.Components.EmojiView$32$1$1 */
                            class C11461 extends AnimatorListenerAdapter {
                                C11461() {
                                }

                                public void onAnimationEnd(Animator animator) {
                                    if (EmojiView.this.mediaBanTooltip != null) {
                                        EmojiView.this.mediaBanTooltip.setVisibility(4);
                                    }
                                }
                            }

                            C11471() {
                            }

                            public void run() {
                                if (EmojiView.this.mediaBanTooltip != null) {
                                    AnimatorSet animatorSet = new AnimatorSet();
                                    Animator[] animatorArr = new Animator[1];
                                    animatorArr[0] = ObjectAnimator.ofFloat(EmojiView.this.mediaBanTooltip, "alpha", new float[]{0.0f});
                                    animatorSet.playTogether(animatorArr);
                                    animatorSet.addListener(new C11461());
                                    animatorSet.setDuration(300);
                                    animatorSet.start();
                                }
                            }
                        }

                        public void onAnimationEnd(Animator animator) {
                            AndroidUtilities.runOnUIThread(new C11471(), DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                        }
                    });
                    animatorSet.setDuration(300);
                    animatorSet.start();
                }
            }
        }
    }

    private void updateVisibleTrendingSets() {
        if (this.trendingGridAdapter != null) {
            if (this.trendingGridAdapter != null) {
                try {
                    int childCount = this.trendingGridView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = this.trendingGridView.getChildAt(i);
                        if (childAt instanceof FeaturedStickerSetInfoCell) {
                            if (((Holder) this.trendingGridView.getChildViewHolder(childAt)) != null) {
                                FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) childAt;
                                ArrayList unreadStickerSets = DataQuery.getInstance(this.currentAccount).getUnreadStickerSets();
                                StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
                                boolean z = true;
                                boolean z2 = unreadStickerSets != null && unreadStickerSets.contains(Long.valueOf(stickerSet.set.id));
                                featuredStickerSetInfoCell.setStickerSet(stickerSet, z2);
                                if (z2) {
                                    DataQuery.getInstance(this.currentAccount).markFaturedStickersByIdAsRead(stickerSet.set.id);
                                }
                                z2 = this.installingStickerSets.indexOfKey(stickerSet.set.id) >= 0;
                                boolean z3 = this.removingStickerSets.indexOfKey(stickerSet.set.id) >= 0;
                                if (z2 || z3) {
                                    if (z2 && featuredStickerSetInfoCell.isInstalled()) {
                                        this.installingStickerSets.remove(stickerSet.set.id);
                                        z2 = false;
                                    } else if (z3 && !featuredStickerSetInfoCell.isInstalled()) {
                                        this.removingStickerSets.remove(stickerSet.set.id);
                                        z3 = false;
                                    }
                                }
                                if (!z2) {
                                    if (!z3) {
                                        z = false;
                                    }
                                }
                                featuredStickerSetInfoCell.setDrawProgress(z);
                            }
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }
    }

    public boolean areThereAnyStickers() {
        return this.stickersGridAdapter != null && this.stickersGridAdapter.getItemCount() > 0;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = 0;
        if (i == NotificationCenter.stickersDidLoaded) {
            if (((Integer) objArr[0]).intValue() == 0) {
                if (this.trendingGridAdapter != 0) {
                    if (this.trendingLoaded != 0) {
                        updateVisibleTrendingSets();
                    } else {
                        this.trendingGridAdapter.notifyDataSetChanged();
                    }
                }
                updateStickerTabs();
                reloadStickersAdapter();
                checkPanels();
            }
        } else if (i == NotificationCenter.recentDocumentsDidLoaded) {
            i = ((Boolean) objArr[0]).booleanValue();
            i2 = ((Integer) objArr[1]).intValue();
            if (i != 0 || i2 == 0 || i2 == 2) {
                checkDocuments(i);
            }
        } else if (i == NotificationCenter.featuredStickersDidLoaded) {
            if (this.trendingGridAdapter != 0) {
                if (this.featuredStickersHash != DataQuery.getInstance(this.currentAccount).getFeaturesStickersHashWithoutUnread()) {
                    this.trendingLoaded = false;
                }
                if (this.trendingLoaded != 0) {
                    updateVisibleTrendingSets();
                } else {
                    this.trendingGridAdapter.notifyDataSetChanged();
                }
            }
            if (this.pagerSlidingTabStrip != 0) {
                i = this.pagerSlidingTabStrip.getChildCount();
                while (i3 < i) {
                    this.pagerSlidingTabStrip.getChildAt(i3).invalidate();
                    i3++;
                }
            }
            updateStickerTabs();
        } else if (i == NotificationCenter.groupStickersDidLoaded) {
            if (this.info != 0 && this.info.stickerset != 0 && this.info.stickerset.id == ((Long) objArr[0]).longValue()) {
                updateStickerTabs();
            }
        } else if (i == NotificationCenter.emojiDidLoaded && this.stickersGridView != 0) {
            i = this.stickersGridView.getChildCount();
            while (i3 < i) {
                i2 = this.stickersGridView.getChildAt(i3);
                if ((i2 instanceof StickerSetNameCell) != null) {
                    i2.invalidate();
                }
                i3++;
            }
        }
    }
}
