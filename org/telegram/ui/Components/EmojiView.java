package org.telegram.ui.Components;

import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
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
import android.os.Build.VERSION;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.PagerSlidingTabStrip.IconTabProvider;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate;
import org.telegram.ui.StickerPreviewViewer;
import org.telegram.ui.StickerPreviewViewer.StickerPreviewViewerDelegate;

public class EmojiView extends FrameLayout implements NotificationCenterDelegate {
    private static final OnScrollChangedListener NOP = new OnScrollChangedListener() {
        public void onScrollChanged() {
        }
    };
    private static HashMap<String, String> emojiColor = new HashMap();
    private static final Field superListenerField;
    private ArrayList<EmojiGridAdapter> adapters = new ArrayList();
    private ImageView backspaceButton;
    private boolean backspaceOnce;
    private boolean backspacePressed;
    private int currentBackgroundType = -1;
    private int currentPage;
    private Drawable dotDrawable;
    private ArrayList<GridView> emojiGrids = new ArrayList();
    private int emojiSize;
    private LinearLayout emojiTab;
    private HashMap<String, Integer> emojiUseHistory = new HashMap();
    private int featuresStickersHash;
    private ExtendedGridLayoutManager flowLayoutManager;
    private int gifTabNum = -2;
    private GifsAdapter gifsAdapter;
    private RecyclerListView gifsGridView;
    private Drawable[] icons;
    private HashMap<Long, StickerSetCovered> installingStickerSets = new HashMap();
    private boolean isLayout;
    private int lastNotifyWidth;
    private Listener listener;
    private int[] location = new int[2];
    private int minusDy;
    private int oldWidth;
    private Object outlineProvider;
    private ViewPager pager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private EmojiColorPickerView pickerView;
    private EmojiPopupWindow pickerViewPopup;
    private int popupHeight;
    private int popupWidth;
    private ArrayList<String> recentEmoji = new ArrayList();
    private ArrayList<Document> recentGifs = new ArrayList();
    private ArrayList<Document> recentStickers = new ArrayList();
    private int recentTabBum = -2;
    private HashMap<Long, StickerSetCovered> removingStickerSets = new HashMap();
    private boolean showGifs;
    private StickerPreviewViewerDelegate stickerPreviewViewerDelegate = new StickerPreviewViewerDelegate() {
        public void sentSticker(Document sticker) {
            EmojiView.this.listener.onStickerSelected(sticker);
        }

        public void openSet(InputStickerSet set) {
            if (set != null) {
                TL_messages_stickerSet stickerSet;
                int position;
                if (set.id != 0) {
                    stickerSet = StickersQuery.getStickerSetById(Long.valueOf(set.id));
                } else if (set.short_name != null) {
                    stickerSet = StickersQuery.getStickerSetByName(set.short_name);
                } else {
                    stickerSet = null;
                }
                if (stickerSet != null) {
                    position = EmojiView.this.stickersGridAdapter.getPositionForPack(stickerSet);
                } else {
                    position = -1;
                }
                if (position != -1) {
                    EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(position, 0);
                } else {
                    EmojiView.this.listener.onShowStickerSet(null, set);
                }
            }
        }
    };
    private ArrayList<TL_messages_stickerSet> stickerSets = new ArrayList();
    private TextView stickersEmptyView;
    private StickersGridAdapter stickersGridAdapter;
    private RecyclerListView stickersGridView;
    private GridLayoutManager stickersLayoutManager;
    private OnItemClickListener stickersOnItemClickListener;
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

    private class EmojiColorPickerView extends View {
        private Drawable arrowDrawable = getResources().getDrawable(R.drawable.stickers_back_arrow);
        private int arrowX;
        private Drawable backgroundDrawable = getResources().getDrawable(R.drawable.stickers_back_all);
        private String currentEmoji;
        private RectF rect = new RectF();
        private Paint rectPaint = new Paint(1);
        private int selection;

        public void setEmoji(String emoji, int arrowPosition) {
            this.currentEmoji = emoji;
            this.arrowX = arrowPosition;
            this.rectPaint.setColor(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR);
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

        protected void onDraw(Canvas canvas) {
            float f;
            this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(AndroidUtilities.isTablet() ? BitmapDescriptorFactory.HUE_YELLOW : 52.0f));
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

    private class EmojiGridAdapter extends BaseAdapter {
        private int emojiPage;

        public EmojiGridAdapter(int page) {
            this.emojiPage = page;
        }

        public Object getItem(int position) {
            return null;
        }

        public int getCount() {
            if (this.emojiPage == -1) {
                return EmojiView.this.recentEmoji.size();
            }
            return EmojiData.dataColored[this.emojiPage].length;
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View view, ViewGroup paramViewGroup) {
            String code;
            String coloredCode;
            ImageViewEmoji imageView = (ImageViewEmoji) view;
            if (imageView == null) {
                imageView = new ImageViewEmoji(EmojiView.this.getContext());
            }
            if (this.emojiPage == -1) {
                code = (String) EmojiView.this.recentEmoji.get(position);
                coloredCode = code;
            } else {
                code = EmojiData.dataColored[this.emojiPage][position];
                coloredCode = code;
                String color = (String) EmojiView.emojiColor.get(code);
                if (color != null) {
                    coloredCode = EmojiView.addColorToCode(coloredCode, color);
                }
            }
            imageView.setImageDrawable(Emoji.getEmojiBigDrawable(coloredCode));
            imageView.setTag(code);
            return imageView;
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
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
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

    private class ImageViewEmoji extends ImageView {
        private float lastX;
        private float lastY;
        private boolean touched;
        private float touchedX;
        private float touchedY;

        public ImageViewEmoji(Context context) {
            super(context);
            setOnClickListener(new OnClickListener(EmojiView.this) {
                public void onClick(View v) {
                    ImageViewEmoji.this.sendEmoji(null);
                }
            });
            setOnLongClickListener(new OnLongClickListener(EmojiView.this) {
                public boolean onLongClick(View view) {
                    int yOffset = 0;
                    String code = (String) view.getTag();
                    if (EmojiData.emojiColoredMap.containsKey(code)) {
                        int i;
                        ImageViewEmoji.this.touched = true;
                        ImageViewEmoji.this.touchedX = ImageViewEmoji.this.lastX;
                        ImageViewEmoji.this.touchedY = ImageViewEmoji.this.lastY;
                        String color = (String) EmojiView.emojiColor.get(code);
                        if (color != null) {
                            i = -1;
                            switch (color.hashCode()) {
                                case 1773375:
                                    if (color.equals("🏻")) {
                                        i = 0;
                                        break;
                                    }
                                    break;
                                case 1773376:
                                    if (color.equals("🏼")) {
                                        boolean z = true;
                                        break;
                                    }
                                    break;
                                case 1773377:
                                    if (color.equals("🏽")) {
                                        i = 2;
                                        break;
                                    }
                                    break;
                                case 1773378:
                                    if (color.equals("🏾")) {
                                        i = 3;
                                        break;
                                    }
                                    break;
                                case 1773379:
                                    if (color.equals("🏿")) {
                                        i = 4;
                                        break;
                                    }
                                    break;
                            }
                            switch (i) {
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
                            }
                        }
                        EmojiView.this.pickerView.setSelection(0);
                        view.getLocationOnScreen(EmojiView.this.location);
                        int selection = EmojiView.this.pickerView.getSelection() * EmojiView.this.emojiSize;
                        int selection2 = EmojiView.this.pickerView.getSelection() * 4;
                        if (AndroidUtilities.isTablet()) {
                            i = 5;
                        } else {
                            i = 1;
                        }
                        int x = selection + AndroidUtilities.dp((float) (selection2 - i));
                        if (EmojiView.this.location[0] - x < AndroidUtilities.dp(5.0f)) {
                            x += (EmojiView.this.location[0] - x) - AndroidUtilities.dp(5.0f);
                        } else if ((EmojiView.this.location[0] - x) + EmojiView.this.popupWidth > AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0f)) {
                            x += ((EmojiView.this.location[0] - x) + EmojiView.this.popupWidth) - (AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0f));
                        }
                        int xOffset = -x;
                        if (view.getTop() < 0) {
                            yOffset = view.getTop();
                        }
                        EmojiView.this.pickerView.setEmoji(code, (AndroidUtilities.dp(AndroidUtilities.isTablet() ? BitmapDescriptorFactory.HUE_ORANGE : 22.0f) - xOffset) + ((int) AndroidUtilities.dpf2(0.5f)));
                        EmojiView.this.pickerViewPopup.setFocusable(true);
                        EmojiView.this.pickerViewPopup.showAsDropDown(view, xOffset, (((-view.getMeasuredHeight()) - EmojiView.this.popupHeight) + ((view.getMeasuredHeight() - EmojiView.this.emojiSize) / 2)) - yOffset);
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        return true;
                    }
                    if (EmojiView.this.pager.getCurrentItem() == 0) {
                        EmojiView.this.listener.onClearEmojiRecent();
                    }
                    return false;
                }
            });
            setBackgroundDrawable(Theme.getSelectorDrawable(false));
            setScaleType(ScaleType.CENTER);
        }

        private void sendEmoji(String override) {
            String code;
            if (override != null) {
                code = override;
            } else {
                code = (String) getTag();
            }
            if (override == null) {
                if (EmojiView.this.pager.getCurrentItem() != 0) {
                    String color = (String) EmojiView.emojiColor.get(code);
                    if (color != null) {
                        code = EmojiView.addColorToCode(code, color);
                    }
                }
                Integer count = (Integer) EmojiView.this.emojiUseHistory.get(code);
                if (count == null) {
                    count = Integer.valueOf(0);
                }
                if (count.intValue() == 0 && EmojiView.this.emojiUseHistory.size() > 50) {
                    for (int a = EmojiView.this.recentEmoji.size() - 1; a >= 0; a--) {
                        EmojiView.this.emojiUseHistory.remove((String) EmojiView.this.recentEmoji.get(a));
                        EmojiView.this.recentEmoji.remove(a);
                        if (EmojiView.this.emojiUseHistory.size() <= 50) {
                            break;
                        }
                    }
                }
                EmojiView.this.emojiUseHistory.put(code, Integer.valueOf(count.intValue() + 1));
                if (EmojiView.this.pager.getCurrentItem() != 0) {
                    EmojiView.this.sortEmoji();
                }
                EmojiView.this.saveRecentEmoji();
                ((EmojiGridAdapter) EmojiView.this.adapters.get(0)).notifyDataSetChanged();
                if (EmojiView.this.listener != null) {
                    EmojiView.this.listener.onEmojiSelected(Emoji.fixEmoji(code));
                }
            } else if (EmojiView.this.listener != null) {
                EmojiView.this.listener.onEmojiSelected(Emoji.fixEmoji(override));
            }
        }

        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec));
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (this.touched) {
                if (event.getAction() == 1 || event.getAction() == 3) {
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
                        String code = (String) getTag();
                        if (EmojiView.this.pager.getCurrentItem() != 0) {
                            if (color != null) {
                                EmojiView.emojiColor.put(code, color);
                                code = EmojiView.addColorToCode(code, color);
                            } else {
                                EmojiView.emojiColor.remove(code);
                            }
                            setImageDrawable(Emoji.getEmojiBigDrawable(code));
                            sendEmoji(null);
                            EmojiView.this.saveEmojiColors();
                        } else {
                            StringBuilder append = new StringBuilder().append(code);
                            if (color == null) {
                                color = "";
                            }
                            sendEmoji(append.append(color).toString());
                        }
                    }
                    this.touched = false;
                    this.touchedX = -10000.0f;
                    this.touchedY = -10000.0f;
                } else if (event.getAction() == 2) {
                    boolean ignore = false;
                    if (this.touchedX != -10000.0f) {
                        if (Math.abs(this.touchedX - event.getX()) > AndroidUtilities.getPixelsInCM(0.2f, true) || Math.abs(this.touchedY - event.getY()) > AndroidUtilities.getPixelsInCM(0.2f, false)) {
                            this.touchedX = -10000.0f;
                            this.touchedY = -10000.0f;
                        } else {
                            ignore = true;
                        }
                    }
                    if (!ignore) {
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
                    }
                }
            }
            this.lastX = event.getX();
            this.lastY = event.getY();
            return super.onTouchEvent(event);
        }
    }

    public interface Listener {
        boolean onBackspace();

        void onClearEmojiRecent();

        void onEmojiSelected(String str);

        void onGifSelected(Document document);

        void onGifTab(boolean z);

        void onShowStickerSet(StickerSet stickerSet, InputStickerSet inputStickerSet);

        void onStickerSelected(Document document);

        void onStickerSetAdd(StickerSetCovered stickerSetCovered);

        void onStickerSetRemove(StickerSetCovered stickerSetCovered);

        void onStickersSettingsClick();

        void onStickersTab(boolean z);
    }

    private class EmojiPagesAdapter extends PagerAdapter implements IconTabProvider {
        private EmojiPagesAdapter() {
        }

        public void destroyItem(ViewGroup viewGroup, int position, Object object) {
            View view;
            if (position == 6) {
                view = EmojiView.this.stickersWrap;
            } else {
                view = (View) EmojiView.this.views.get(position);
            }
            viewGroup.removeView(view);
        }

        public int getCount() {
            return EmojiView.this.views.size();
        }

        public Drawable getPageIconDrawable(int position) {
            return EmojiView.this.icons[position];
        }

        public void customOnDraw(Canvas canvas, int position) {
            if (position == 6 && !StickersQuery.getUnreadStickerSets().isEmpty() && EmojiView.this.dotDrawable != null) {
                int x = (canvas.getWidth() / 2) + AndroidUtilities.dp(4.0f);
                int y = (canvas.getHeight() / 2) - AndroidUtilities.dp(13.0f);
                EmojiView.this.dotDrawable.setBounds(x, y, EmojiView.this.dotDrawable.getIntrinsicWidth() + x, EmojiView.this.dotDrawable.getIntrinsicHeight() + y);
                EmojiView.this.dotDrawable.draw(canvas);
            }
        }

        public Object instantiateItem(ViewGroup viewGroup, int position) {
            View view;
            if (position == 6) {
                view = EmojiView.this.stickersWrap;
            } else {
                view = (View) EmojiView.this.views.get(position);
            }
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

    private class GifsAdapter extends SelectionAdapter {
        private Context mContext;

        public GifsAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public int getItemCount() {
            return EmojiView.this.recentGifs.size();
        }

        public long getItemId(int i) {
            return (long) i;
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
        private HashMap<Integer, Document> cache = new HashMap();
        private Context context;
        private HashMap<TL_messages_stickerSet, Integer> packStartRow = new HashMap();
        private HashMap<Integer, TL_messages_stickerSet> rowStartPack = new HashMap();
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
            return this.cache.get(Integer.valueOf(i));
        }

        public int getPositionForPack(TL_messages_stickerSet stickerSet) {
            Integer pos = (Integer) this.packStartRow.get(stickerSet);
            if (pos == null) {
                return -1;
            }
            return pos.intValue() * this.stickersPerRow;
        }

        public int getItemViewType(int position) {
            if (this.cache.get(Integer.valueOf(position)) != null) {
                return 0;
            }
            return 1;
        }

        public int getTabForPosition(int position) {
            if (this.stickersPerRow == 0) {
                int width = EmojiView.this.getMeasuredWidth();
                if (width == 0) {
                    width = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
            }
            TL_messages_stickerSet pack = (TL_messages_stickerSet) this.rowStartPack.get(Integer.valueOf(position / this.stickersPerRow));
            if (pack == null) {
                return EmojiView.this.recentTabBum;
            }
            return EmojiView.this.stickerSets.indexOf(pack) + EmojiView.this.stickersTabOffset;
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
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    Document sticker = (Document) this.cache.get(Integer.valueOf(position));
                    ((StickerEmojiCell) holder.itemView).setSticker(sticker, false);
                    ((StickerEmojiCell) holder.itemView).setRecent(EmojiView.this.recentStickers.contains(sticker));
                    return;
                case 1:
                    if (position == this.totalItems) {
                        TL_messages_stickerSet pack = (TL_messages_stickerSet) this.rowStartPack.get(Integer.valueOf((position - 1) / this.stickersPerRow));
                        if (pack == null) {
                            ((EmptyCell) holder.itemView).setHeight(1);
                            return;
                        }
                        int height = EmojiView.this.pager.getHeight() - (((int) Math.ceil((double) (((float) pack.documents.size()) / ((float) this.stickersPerRow)))) * AndroidUtilities.dp(82.0f));
                        EmptyCell emptyCell = (EmptyCell) holder.itemView;
                        if (height <= 0) {
                            height = 1;
                        }
                        emptyCell.setHeight(height);
                        return;
                    }
                    ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(82.0f));
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
            this.packStartRow.clear();
            this.cache.clear();
            this.totalItems = 0;
            ArrayList<TL_messages_stickerSet> packs = EmojiView.this.stickerSets;
            for (int a = -1; a < packs.size(); a++) {
                ArrayList<Document> documents;
                TL_messages_stickerSet pack = null;
                int startRow = this.totalItems / this.stickersPerRow;
                if (a == -1) {
                    documents = EmojiView.this.recentStickers;
                } else {
                    pack = (TL_messages_stickerSet) packs.get(a);
                    documents = pack.documents;
                    this.packStartRow.put(pack, Integer.valueOf(startRow));
                }
                if (!documents.isEmpty()) {
                    int b;
                    int count = (int) Math.ceil((double) (((float) documents.size()) / ((float) this.stickersPerRow)));
                    for (b = 0; b < documents.size(); b++) {
                        this.cache.put(Integer.valueOf(this.totalItems + b), documents.get(b));
                    }
                    this.totalItems += this.stickersPerRow * count;
                    for (b = 0; b < count; b++) {
                        this.rowStartPack.put(Integer.valueOf(startRow + b), pack);
                    }
                }
            }
            super.notifyDataSetChanged();
        }
    }

    private class TrendingGridAdapter extends SelectionAdapter {
        private HashMap<Integer, Object> cache = new HashMap();
        private Context context;
        private HashMap<Integer, StickerSetCovered> positionsToSets = new HashMap();
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
            return this.cache.get(Integer.valueOf(i));
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public int getItemViewType(int position) {
            Object object = this.cache.get(Integer.valueOf(position));
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
                    ((FeaturedStickerSetInfoCell) view).setAddOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            FeaturedStickerSetInfoCell parent = (FeaturedStickerSetInfoCell) v.getParent();
                            StickerSetCovered pack = parent.getStickerSet();
                            if (!EmojiView.this.installingStickerSets.containsKey(Long.valueOf(pack.set.id)) && !EmojiView.this.removingStickerSets.containsKey(Long.valueOf(pack.set.id))) {
                                if (parent.isInstalled()) {
                                    EmojiView.this.removingStickerSets.put(Long.valueOf(pack.set.id), pack);
                                    EmojiView.this.listener.onStickerSetRemove(parent.getStickerSet());
                                } else {
                                    EmojiView.this.installingStickerSets.put(Long.valueOf(pack.set.id), pack);
                                    EmojiView.this.listener.onStickerSetAdd(parent.getStickerSet());
                                }
                                parent.setDrawProgress(true);
                            }
                        }
                    });
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    ((StickerEmojiCell) holder.itemView).setSticker((Document) this.cache.get(Integer.valueOf(position)), false);
                    return;
                case 1:
                    ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                    return;
                case 2:
                    boolean unread;
                    ArrayList<Long> unreadStickers = StickersQuery.getUnreadStickerSets();
                    StickerSetCovered stickerSetCovered = (StickerSetCovered) this.sets.get(((Integer) this.cache.get(Integer.valueOf(position))).intValue());
                    if (unreadStickers == null || !unreadStickers.contains(Long.valueOf(stickerSetCovered.set.id))) {
                        unread = false;
                    } else {
                        unread = true;
                    }
                    FeaturedStickerSetInfoCell cell = holder.itemView;
                    cell.setStickerSet(stickerSetCovered, unread);
                    if (unread) {
                        StickersQuery.markFaturedStickersByIdAsRead(stickerSetCovered.set.id);
                    }
                    boolean installing = EmojiView.this.installingStickerSets.containsKey(Long.valueOf(stickerSetCovered.set.id));
                    boolean removing = EmojiView.this.removingStickerSets.containsKey(Long.valueOf(stickerSetCovered.set.id));
                    if (installing || removing) {
                        if (installing && cell.isInstalled()) {
                            EmojiView.this.installingStickerSets.remove(Long.valueOf(stickerSetCovered.set.id));
                            installing = false;
                        } else if (removing && !cell.isInstalled()) {
                            EmojiView.this.removingStickerSets.remove(Long.valueOf(stickerSetCovered.set.id));
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
            if (!EmojiView.this.trendingLoaded) {
                int width = EmojiView.this.getMeasuredWidth();
                if (width == 0) {
                    width = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
                EmojiView.this.trendingLayoutManager.setSpanCount(this.stickersPerRow);
                this.cache.clear();
                this.positionsToSets.clear();
                this.sets.clear();
                this.totalItems = 0;
                int num = 0;
                ArrayList<StickerSetCovered> packs = StickersQuery.getFeaturedStickerSets();
                for (int a = 0; a < packs.size(); a++) {
                    StickerSetCovered pack = (StickerSetCovered) packs.get(a);
                    if (!(StickersQuery.isStickerPackInstalled(pack.set.id) || (pack.covers.isEmpty() && pack.cover == null))) {
                        int count;
                        int b;
                        this.sets.add(pack);
                        this.positionsToSets.put(Integer.valueOf(this.totalItems), pack);
                        HashMap hashMap = this.cache;
                        int i = this.totalItems;
                        this.totalItems = i + 1;
                        int num2 = num + 1;
                        hashMap.put(Integer.valueOf(i), Integer.valueOf(num));
                        int startRow = this.totalItems / this.stickersPerRow;
                        if (pack.covers.isEmpty()) {
                            count = 1;
                            this.cache.put(Integer.valueOf(this.totalItems), pack.cover);
                        } else {
                            count = (int) Math.ceil((double) (((float) pack.covers.size()) / ((float) this.stickersPerRow)));
                            for (b = 0; b < pack.covers.size(); b++) {
                                this.cache.put(Integer.valueOf(this.totalItems + b), pack.covers.get(b));
                            }
                        }
                        for (b = 0; b < this.stickersPerRow * count; b++) {
                            this.positionsToSets.put(Integer.valueOf(this.totalItems + b), pack);
                        }
                        this.totalItems += this.stickersPerRow * count;
                        num = num2;
                    }
                }
                if (this.totalItems != 0) {
                    EmojiView.this.trendingLoaded = true;
                    EmojiView.this.featuresStickersHash = StickersQuery.getFeaturesStickersHashWithoutUnread();
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

    private static String addColorToCode(String code, String color) {
        String end = null;
        int lenght = code.length();
        if (lenght > 2 && code.charAt(code.length() - 2) == '‍') {
            end = code.substring(code.length() - 2);
            code = code.substring(0, code.length() - 2);
        } else if (lenght > 3 && code.charAt(code.length() - 3) == '‍') {
            end = code.substring(code.length() - 3);
            code = code.substring(0, code.length() - 3);
        }
        code = code + color;
        if (end != null) {
            return code + end;
        }
        return code;
    }

    public EmojiView(boolean needStickers, boolean needGif, Context context) {
        FrameLayout frameLayout;
        super(context);
        Drawable stickersDrawable = context.getResources().getDrawable(R.drawable.ic_smiles2_stickers);
        Theme.setDrawableColorByKey(stickersDrawable, Theme.key_chat_emojiPanelIcon);
        Drawable[] drawableArr = new Drawable[7];
        drawableArr[0] = Theme.createEmojiIconSelectorDrawable(context, R.drawable.ic_smiles2_recent, Theme.getColor(Theme.key_chat_emojiPanelIcon), Theme.getColor(Theme.key_chat_emojiPanelIconSelected));
        drawableArr[1] = Theme.createEmojiIconSelectorDrawable(context, R.drawable.ic_smiles2_smile, Theme.getColor(Theme.key_chat_emojiPanelIcon), Theme.getColor(Theme.key_chat_emojiPanelIconSelected));
        drawableArr[2] = Theme.createEmojiIconSelectorDrawable(context, R.drawable.ic_smiles2_nature, Theme.getColor(Theme.key_chat_emojiPanelIcon), Theme.getColor(Theme.key_chat_emojiPanelIconSelected));
        drawableArr[3] = Theme.createEmojiIconSelectorDrawable(context, R.drawable.ic_smiles2_food, Theme.getColor(Theme.key_chat_emojiPanelIcon), Theme.getColor(Theme.key_chat_emojiPanelIconSelected));
        drawableArr[4] = Theme.createEmojiIconSelectorDrawable(context, R.drawable.ic_smiles2_car, Theme.getColor(Theme.key_chat_emojiPanelIcon), Theme.getColor(Theme.key_chat_emojiPanelIconSelected));
        drawableArr[5] = Theme.createEmojiIconSelectorDrawable(context, R.drawable.ic_smiles2_objects, Theme.getColor(Theme.key_chat_emojiPanelIcon), Theme.getColor(Theme.key_chat_emojiPanelIconSelected));
        drawableArr[6] = stickersDrawable;
        this.icons = drawableArr;
        this.showGifs = needGif;
        this.dotDrawable = context.getResources().getDrawable(R.drawable.bluecircle);
        if (VERSION.SDK_INT >= 21) {
            this.outlineProvider = new ViewOutlineProvider() {
                @TargetApi(21)
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(view.getPaddingLeft(), view.getPaddingTop(), view.getMeasuredWidth() - view.getPaddingRight(), view.getMeasuredHeight() - view.getPaddingBottom(), (float) AndroidUtilities.dp(6.0f));
                }
            };
        }
        for (int i = 0; i < EmojiData.dataColored.length + 1; i++) {
            GridView gridView = new GridView(context);
            if (AndroidUtilities.isTablet()) {
                gridView.setColumnWidth(AndroidUtilities.dp(BitmapDescriptorFactory.HUE_YELLOW));
            } else {
                gridView.setColumnWidth(AndroidUtilities.dp(45.0f));
            }
            gridView.setNumColumns(-1);
            EmojiGridAdapter emojiGridAdapter = new EmojiGridAdapter(i - 1);
            gridView.setAdapter(emojiGridAdapter);
            this.adapters.add(emojiGridAdapter);
            this.emojiGrids.add(gridView);
            frameLayout = new FrameLayout(context);
            frameLayout.addView(gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 0.0f));
            this.views.add(frameLayout);
        }
        if (needStickers) {
            this.stickersWrap = new FrameLayout(context);
            StickersQuery.checkStickers(0);
            StickersQuery.checkFeaturedStickers();
            this.stickersGridView = new RecyclerListView(context) {
                public boolean onInterceptTouchEvent(MotionEvent event) {
                    return super.onInterceptTouchEvent(event) || StickerPreviewViewer.getInstance().onInterceptTouchEvent(event, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.stickerPreviewViewerDelegate);
                }

                public void setVisibility(int visibility) {
                    if ((EmojiView.this.gifsGridView == null || EmojiView.this.gifsGridView.getVisibility() != 0) && (EmojiView.this.trendingGridView == null || EmojiView.this.trendingGridView.getVisibility() != 0)) {
                        super.setVisibility(visibility);
                    } else {
                        super.setVisibility(8);
                    }
                }
            };
            RecyclerListView recyclerListView = this.stickersGridView;
            LayoutManager gridLayoutManager = new GridLayoutManager(context, 5);
            this.stickersLayoutManager = gridLayoutManager;
            recyclerListView.setLayoutManager(gridLayoutManager);
            this.stickersLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
                public int getSpanSize(int position) {
                    if (position == EmojiView.this.stickersGridAdapter.totalItems) {
                        return EmojiView.this.stickersGridAdapter.stickersPerRow;
                    }
                    return 1;
                }
            });
            this.stickersGridView.setPadding(0, AndroidUtilities.dp(52.0f), 0, 0);
            this.stickersGridView.setClipToPadding(false);
            this.views.add(this.stickersWrap);
            recyclerListView = this.stickersGridView;
            Adapter stickersGridAdapter = new StickersGridAdapter(context);
            this.stickersGridAdapter = stickersGridAdapter;
            recyclerListView.setAdapter(stickersGridAdapter);
            this.stickersGridView.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return StickerPreviewViewer.getInstance().onTouch(event, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.stickersOnItemClickListener, EmojiView.this.stickerPreviewViewerDelegate);
                }
            });
            this.stickersOnItemClickListener = new OnItemClickListener() {
                public void onItemClick(View view, int position) {
                    if (view instanceof StickerEmojiCell) {
                        StickerPreviewViewer.getInstance().reset();
                        StickerEmojiCell cell = (StickerEmojiCell) view;
                        if (!cell.isDisabled()) {
                            cell.disable();
                            EmojiView.this.listener.onStickerSelected(cell.getSticker());
                        }
                    }
                }
            };
            this.stickersGridView.setOnItemClickListener(this.stickersOnItemClickListener);
            this.stickersGridView.setGlowColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
            this.stickersWrap.addView(this.stickersGridView);
            this.trendingGridView = new RecyclerListView(context);
            this.trendingGridView.setItemAnimator(null);
            this.trendingGridView.setLayoutAnimation(null);
            recyclerListView = this.trendingGridView;
            gridLayoutManager = new GridLayoutManager(context, 5) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            this.trendingLayoutManager = gridLayoutManager;
            recyclerListView.setLayoutManager(gridLayoutManager);
            this.trendingLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
                public int getSpanSize(int position) {
                    if ((EmojiView.this.trendingGridAdapter.cache.get(Integer.valueOf(position)) instanceof Integer) || position == EmojiView.this.trendingGridAdapter.totalItems) {
                        return EmojiView.this.trendingGridAdapter.stickersPerRow;
                    }
                    return 1;
                }
            });
            this.trendingGridView.setOnScrollListener(new OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    EmojiView.this.checkStickersTabY(recyclerView, dy);
                }
            });
            this.trendingGridView.setClipToPadding(false);
            this.trendingGridView.setPadding(0, AndroidUtilities.dp(48.0f), 0, 0);
            recyclerListView = this.trendingGridView;
            stickersGridAdapter = new TrendingGridAdapter(context);
            this.trendingGridAdapter = stickersGridAdapter;
            recyclerListView.setAdapter(stickersGridAdapter);
            this.trendingGridView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(View view, int position) {
                    StickerSetCovered pack = (StickerSetCovered) EmojiView.this.trendingGridAdapter.positionsToSets.get(Integer.valueOf(position));
                    if (pack != null) {
                        EmojiView.this.listener.onShowStickerSet(pack.set, null);
                    }
                }
            });
            this.trendingGridAdapter.notifyDataSetChanged();
            this.trendingGridView.setGlowColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
            this.trendingGridView.setVisibility(8);
            this.stickersWrap.addView(this.trendingGridView);
            if (needGif) {
                this.gifsGridView = new RecyclerListView(context);
                this.gifsGridView.setClipToPadding(false);
                this.gifsGridView.setPadding(0, AndroidUtilities.dp(48.0f), 0, 0);
                recyclerListView = this.gifsGridView;
                gridLayoutManager = new ExtendedGridLayoutManager(context, 100) {
                    private Size size = new Size();

                    protected Size getSizeForItem(int i) {
                        float f;
                        float f2 = 100.0f;
                        Document document = (Document) EmojiView.this.recentGifs.get(i);
                        Size size = this.size;
                        if (document.thumb == null || document.thumb.w == 0) {
                            f = 100.0f;
                        } else {
                            f = (float) document.thumb.w;
                        }
                        size.width = f;
                        Size size2 = this.size;
                        if (!(document.thumb == null || document.thumb.h == 0)) {
                            f2 = (float) document.thumb.h;
                        }
                        size2.height = f2;
                        for (int b = 0; b < document.attributes.size(); b++) {
                            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(b);
                            if ((attribute instanceof TL_documentAttributeImageSize) || (attribute instanceof TL_documentAttributeVideo)) {
                                this.size.width = (float) attribute.w;
                                this.size.height = (float) attribute.h;
                                break;
                            }
                        }
                        return this.size;
                    }
                };
                this.flowLayoutManager = gridLayoutManager;
                recyclerListView.setLayoutManager(gridLayoutManager);
                this.flowLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
                    public int getSpanSize(int position) {
                        return EmojiView.this.flowLayoutManager.getSpanSizeForItem(position);
                    }
                });
                this.gifsGridView.addItemDecoration(new ItemDecoration() {
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                        int i = 0;
                        outRect.left = 0;
                        outRect.top = 0;
                        outRect.bottom = 0;
                        int position = parent.getChildAdapterPosition(view);
                        if (!EmojiView.this.flowLayoutManager.isFirstRow(position)) {
                            outRect.top = AndroidUtilities.dp(2.0f);
                        }
                        if (!EmojiView.this.flowLayoutManager.isLastInRow(position)) {
                            i = AndroidUtilities.dp(2.0f);
                        }
                        outRect.right = i;
                    }
                });
                this.gifsGridView.setOverScrollMode(2);
                recyclerListView = this.gifsGridView;
                stickersGridAdapter = new GifsAdapter(context);
                this.gifsAdapter = stickersGridAdapter;
                recyclerListView.setAdapter(stickersGridAdapter);
                this.gifsGridView.setOnScrollListener(new OnScrollListener() {
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        EmojiView.this.checkStickersTabY(recyclerView, dy);
                    }
                });
                this.gifsGridView.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(View view, int position) {
                        if (position >= 0 && position < EmojiView.this.recentGifs.size() && EmojiView.this.listener != null) {
                            EmojiView.this.listener.onGifSelected((Document) EmojiView.this.recentGifs.get(position));
                        }
                    }
                });
                this.gifsGridView.setOnItemLongClickListener(new OnItemLongClickListener() {
                    public boolean onItemClick(View view, int position) {
                        if (position < 0 || position >= EmojiView.this.recentGifs.size()) {
                            return false;
                        }
                        final Document searchImage = (Document) EmojiView.this.recentGifs.get(position);
                        Builder builder = new Builder(view.getContext());
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("DeleteGif", R.string.DeleteGif));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK).toUpperCase(), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                StickersQuery.removeRecentGif(searchImage);
                                EmojiView.this.recentGifs = StickersQuery.getRecentGifs();
                                if (EmojiView.this.gifsAdapter != null) {
                                    EmojiView.this.gifsAdapter.notifyDataSetChanged();
                                }
                                if (EmojiView.this.recentGifs.isEmpty()) {
                                    EmojiView.this.updateStickerTabs();
                                }
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        builder.show().setCanceledOnTouchOutside(true);
                        return true;
                    }
                });
                this.gifsGridView.setVisibility(8);
                this.stickersWrap.addView(this.gifsGridView);
            }
            this.stickersEmptyView = new TextView(context);
            this.stickersEmptyView.setText(LocaleController.getString("NoStickers", R.string.NoStickers));
            this.stickersEmptyView.setTextSize(1, 18.0f);
            this.stickersEmptyView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelEmptyText));
            this.stickersWrap.addView(this.stickersEmptyView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 48.0f, 0.0f, 0.0f));
            this.stickersGridView.setEmptyView(this.stickersEmptyView);
            this.stickersTab = new ScrollSlidingTabStrip(context) {
                boolean first = true;
                float lastTranslateX;
                float lastX;
                boolean startedScroll;

                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    return super.onInterceptTouchEvent(ev);
                }

                public boolean onTouchEvent(MotionEvent ev) {
                    if (this.first) {
                        this.first = false;
                        this.lastX = ev.getX();
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
                        try {
                            EmojiView.this.pager.fakeDragBy((float) ((int) (((ev.getX() - this.lastX) + newTranslationX) - this.lastTranslateX)));
                            this.lastTranslateX = newTranslationX;
                        } catch (Throwable e) {
                            try {
                                EmojiView.this.pager.endFakeDrag();
                            } catch (Exception e2) {
                            }
                            this.startedScroll = false;
                            FileLog.e("tmessages", e);
                        }
                    }
                    this.lastX = ev.getX();
                    if (ev.getAction() == 3 || ev.getAction() == 1) {
                        this.first = true;
                        if (this.startedScroll) {
                            EmojiView.this.pager.endFakeDrag();
                            this.startedScroll = false;
                        }
                    }
                    if (this.startedScroll || super.onTouchEvent(ev)) {
                        return true;
                    }
                    return false;
                }
            };
            this.stickersTab.setUnderlineHeight(AndroidUtilities.dp(1.0f));
            this.stickersTab.setIndicatorColor(Theme.getColor(Theme.key_chat_emojiPanelStickerPackSelector));
            this.stickersTab.setUnderlineColor(Theme.getColor(Theme.key_chat_emojiPanelStickerPackSelector));
            this.stickersTab.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
            this.stickersTab.setVisibility(4);
            addView(this.stickersTab, LayoutHelper.createFrame(-1, 48, 51));
            this.stickersTab.setTranslationX((float) AndroidUtilities.displaySize.x);
            updateStickerTabs();
            this.stickersTab.setDelegate(new ScrollSlidingTabStripDelegate() {
                public void onPageSelected(int page) {
                    int i = 8;
                    if (EmojiView.this.gifsGridView != null) {
                        if (page == EmojiView.this.gifTabNum + 1) {
                            if (EmojiView.this.gifsGridView.getVisibility() != 0) {
                                EmojiView.this.listener.onGifTab(true);
                                EmojiView.this.showGifTab();
                            }
                        } else if (page == EmojiView.this.trendingTabNum + 1) {
                            if (EmojiView.this.trendingGridView.getVisibility() != 0) {
                                EmojiView.this.showTrendingTab();
                            }
                        } else if (EmojiView.this.gifsGridView.getVisibility() == 0) {
                            EmojiView.this.listener.onGifTab(false);
                            EmojiView.this.gifsGridView.setVisibility(8);
                            EmojiView.this.stickersGridView.setVisibility(0);
                            r3 = EmojiView.this.stickersEmptyView;
                            if (EmojiView.this.stickersGridAdapter.getItemCount() == 0) {
                                i = 0;
                            }
                            r3.setVisibility(i);
                            EmojiView.this.saveNewPage();
                        } else if (EmojiView.this.trendingGridView.getVisibility() == 0) {
                            EmojiView.this.trendingGridView.setVisibility(8);
                            EmojiView.this.stickersGridView.setVisibility(0);
                            r3 = EmojiView.this.stickersEmptyView;
                            if (EmojiView.this.stickersGridAdapter.getItemCount() == 0) {
                                i = 0;
                            }
                            r3.setVisibility(i);
                            EmojiView.this.saveNewPage();
                        }
                    }
                    if (page == 0) {
                        EmojiView.this.pager.setCurrentItem(0);
                    } else if (page != EmojiView.this.gifTabNum + 1 && page != EmojiView.this.trendingTabNum + 1) {
                        if (page == EmojiView.this.recentTabBum + 1) {
                            EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
                            EmojiView.this.checkStickersTabY(null, 0);
                            EmojiView.this.stickersTab.onPageScrolled(EmojiView.this.recentTabBum + 1, (EmojiView.this.recentTabBum > 0 ? EmojiView.this.recentTabBum : EmojiView.this.stickersTabOffset) + 1);
                            return;
                        }
                        int index = (page - 1) - EmojiView.this.stickersTabOffset;
                        if (index < EmojiView.this.stickerSets.size()) {
                            if (index >= EmojiView.this.stickerSets.size()) {
                                index = EmojiView.this.stickerSets.size() - 1;
                            }
                            EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(EmojiView.this.stickersGridAdapter.getPositionForPack((TL_messages_stickerSet) EmojiView.this.stickerSets.get(index)), 0);
                            EmojiView.this.checkStickersTabY(null, 0);
                            EmojiView.this.checkScroll();
                        } else if (EmojiView.this.listener != null) {
                            EmojiView.this.listener.onStickersSettingsClick();
                        }
                    }
                }
            });
            this.stickersGridView.setOnScrollListener(new OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    EmojiView.this.checkScroll();
                    EmojiView.this.checkStickersTabY(recyclerView, dy);
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
        };
        EmojiView emojiView = this;
        this.pager.setAdapter(new EmojiPagesAdapter());
        this.emojiTab = new LinearLayout(context) {
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onInterceptTouchEvent(ev);
            }
        };
        this.emojiTab.setOrientation(0);
        addView(this.emojiTab, LayoutHelper.createFrame(-1, 48.0f));
        this.pagerSlidingTabStrip = new PagerSlidingTabStrip(context);
        this.pagerSlidingTabStrip.setViewPager(this.pager);
        this.pagerSlidingTabStrip.setShouldExpand(true);
        this.pagerSlidingTabStrip.setIndicatorHeight(AndroidUtilities.dp(2.0f));
        this.pagerSlidingTabStrip.setUnderlineHeight(AndroidUtilities.dp(1.0f));
        this.pagerSlidingTabStrip.setIndicatorColor(Theme.getColor(Theme.key_chat_emojiPanelIconSelector));
        this.pagerSlidingTabStrip.setUnderlineColor(Theme.getColor(Theme.key_chat_emojiPanelShadowLine));
        this.emojiTab.addView(this.pagerSlidingTabStrip, LayoutHelper.createLinear(0, 48, 1.0f));
        this.pagerSlidingTabStrip.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                EmojiView.this.onPageScrolled(position, (EmojiView.this.getMeasuredWidth() - EmojiView.this.getPaddingLeft()) - EmojiView.this.getPaddingRight(), positionOffsetPixels);
            }

            public void onPageSelected(int position) {
                EmojiView.this.saveNewPage();
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        frameLayout = new FrameLayout(context);
        this.emojiTab.addView(frameLayout, LayoutHelper.createLinear(52, 48));
        this.backspaceButton = new ImageView(context) {
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == 0) {
                    EmojiView.this.backspacePressed = true;
                    EmojiView.this.backspaceOnce = false;
                    EmojiView.this.postBackspaceRunnable(350);
                } else if (event.getAction() == 3 || event.getAction() == 1) {
                    EmojiView.this.backspacePressed = false;
                    if (!(EmojiView.this.backspaceOnce || EmojiView.this.listener == null || !EmojiView.this.listener.onBackspace())) {
                        EmojiView.this.backspaceButton.performHapticFeedback(3);
                    }
                }
                super.onTouchEvent(event);
                return true;
            }
        };
        this.backspaceButton.setImageResource(R.drawable.ic_smiles_backspace);
        this.backspaceButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelBackspace), Mode.MULTIPLY));
        this.backspaceButton.setScaleType(ScaleType.CENTER);
        frameLayout.addView(this.backspaceButton, LayoutHelper.createFrame(52, 48.0f));
        View view = new View(context);
        view.setBackgroundColor(Theme.getColor(Theme.key_chat_emojiPanelShadowLine));
        frameLayout.addView(view, LayoutHelper.createFrame(52, 1, 83));
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString("NoRecent", R.string.NoRecent));
        textView.setTextSize(1, 18.0f);
        textView.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelEmptyText));
        textView.setGravity(17);
        textView.setClickable(false);
        textView.setFocusable(false);
        ((FrameLayout) this.views.get(0)).addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 48.0f, 0.0f, 0.0f));
        ((GridView) this.emojiGrids.get(0)).setEmptyView(textView);
        addView(this.pager, 0, LayoutHelper.createFrame(-1, -1, 51));
        this.emojiSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 32.0f);
        this.pickerView = new EmojiColorPickerView(context);
        View view2 = this.pickerView;
        int dp = AndroidUtilities.dp((float) ((((AndroidUtilities.isTablet() ? 40 : 32) * 6) + 10) + 20));
        this.popupWidth = dp;
        int dp2 = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 64.0f : 56.0f);
        this.popupHeight = dp2;
        this.pickerViewPopup = new EmojiPopupWindow(view2, dp, dp2);
        this.pickerViewPopup.setOutsideTouchable(true);
        this.pickerViewPopup.setClippingEnabled(true);
        this.pickerViewPopup.setInputMethodMode(2);
        this.pickerViewPopup.setSoftInputMode(0);
        this.pickerViewPopup.getContentView().setFocusableInTouchMode(true);
        this.pickerViewPopup.getContentView().setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != 82 || event.getRepeatCount() != 0 || event.getAction() != 1 || EmojiView.this.pickerViewPopup == null || !EmojiView.this.pickerViewPopup.isShowing()) {
                    return false;
                }
                EmojiView.this.pickerViewPopup.dismiss();
                return true;
            }
        });
        this.currentPage = getContext().getSharedPreferences("emoji", 0).getInt("selected_page", 0);
        loadRecents();
    }

    private void checkStickersTabY(View list, int dy) {
        if (list == null) {
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            this.minusDy = 0;
            scrollSlidingTabStrip.setTranslationY((float) null);
        } else if (list.getVisibility() == 0) {
            this.minusDy -= dy;
            if (this.minusDy > 0) {
                this.minusDy = 0;
            } else if (this.minusDy < (-AndroidUtilities.dp(288.0f))) {
                this.minusDy = -AndroidUtilities.dp(288.0f);
            }
            this.stickersTab.setTranslationY((float) Math.max(-AndroidUtilities.dp(47.0f), this.minusDy));
        }
    }

    private void checkScroll() {
        int firstVisibleItem = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (firstVisibleItem != -1) {
            checkStickersScroll(firstVisibleItem);
        }
    }

    private void saveNewPage() {
        int newPage;
        if (this.pager.getCurrentItem() != 6) {
            newPage = 0;
        } else if (this.gifsGridView == null || this.gifsGridView.getVisibility() != 0) {
            newPage = 1;
        } else {
            newPage = 2;
        }
        if (this.currentPage != newPage) {
            this.currentPage = newPage;
            getContext().getSharedPreferences("emoji", 0).edit().putInt("selected_page", newPage).commit();
        }
    }

    public void clearRecentEmoji() {
        getContext().getSharedPreferences("emoji", 0).edit().putBoolean("filled_default", true).commit();
        this.emojiUseHistory.clear();
        this.recentEmoji.clear();
        saveRecentEmoji();
        ((EmojiGridAdapter) this.adapters.get(0)).notifyDataSetChanged();
    }

    private void showTrendingTab() {
        this.trendingGridView.setVisibility(0);
        this.stickersGridView.setVisibility(8);
        this.stickersEmptyView.setVisibility(8);
        this.gifsGridView.setVisibility(8);
        this.stickersTab.onPageScrolled(this.trendingTabNum + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
        saveNewPage();
    }

    private void showGifTab() {
        this.gifsGridView.setVisibility(0);
        this.stickersGridView.setVisibility(8);
        this.stickersEmptyView.setVisibility(8);
        this.trendingGridView.setVisibility(8);
        this.stickersTab.onPageScrolled(this.gifTabNum + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
        saveNewPage();
    }

    private void checkStickersScroll(int firstVisibleItem) {
        if (this.stickersGridView != null) {
            if (this.stickersGridView.getVisibility() != 0) {
                if (!(this.gifsGridView == null || this.gifsGridView.getVisibility() == 0)) {
                    this.gifsGridView.setVisibility(0);
                }
                if (this.stickersEmptyView != null && this.stickersEmptyView.getVisibility() == 0) {
                    this.stickersEmptyView.setVisibility(8);
                }
                this.stickersTab.onPageScrolled(this.gifTabNum + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
                return;
            }
            this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(firstVisibleItem) + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
        }
    }

    private void onPageScrolled(int position, int width, int positionOffsetPixels) {
        boolean z = true;
        int i = 0;
        if (this.stickersTab != null) {
            if (width == 0) {
                width = AndroidUtilities.displaySize.x;
            }
            int margin = 0;
            if (position == 5) {
                margin = -positionOffsetPixels;
                if (this.listener != null) {
                    Listener listener = this.listener;
                    if (positionOffsetPixels == 0) {
                        z = false;
                    }
                    listener.onStickersTab(z);
                }
            } else if (position == 6) {
                margin = -width;
                if (this.listener != null) {
                    this.listener.onStickersTab(true);
                }
            } else if (this.listener != null) {
                this.listener.onStickersTab(false);
            }
            if (this.emojiTab.getTranslationX() != ((float) margin)) {
                this.emojiTab.setTranslationX((float) margin);
                this.stickersTab.setTranslationX((float) (width + margin));
                ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
                if (margin >= 0) {
                    i = 4;
                }
                scrollSlidingTabStrip.setVisibility(i);
            }
        }
    }

    private void postBackspaceRunnable(final int time) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (EmojiView.this.backspacePressed) {
                    if (EmojiView.this.listener != null && EmojiView.this.listener.onBackspace()) {
                        EmojiView.this.backspaceButton.performHapticFeedback(3);
                    }
                    EmojiView.this.backspaceOnce = true;
                    EmojiView.this.postBackspaceRunnable(Math.max(50, time - 100));
                }
            }
        }, (long) time);
    }

    private void saveRecentEmoji() {
        SharedPreferences preferences = getContext().getSharedPreferences("emoji", 0);
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, Integer> entry : this.emojiUseHistory.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append((String) entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
        }
        preferences.edit().putString("emojis2", stringBuilder.toString()).commit();
    }

    private void saveEmojiColors() {
        SharedPreferences preferences = getContext().getSharedPreferences("emoji", 0);
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, String> entry : emojiColor.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append((String) entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append((String) entry.getValue());
        }
        preferences.edit().putString(TtmlNode.ATTR_TTS_COLOR, stringBuilder.toString()).commit();
    }

    public void switchToGifRecent() {
        if (this.gifTabNum < 0 || this.recentGifs.isEmpty()) {
            this.switchToGifTab = true;
        } else {
            this.stickersTab.selectTab(this.gifTabNum + 1);
        }
        this.pager.setCurrentItem(6);
    }

    private void sortEmoji() {
        this.recentEmoji.clear();
        for (Entry<String, Integer> entry : this.emojiUseHistory.entrySet()) {
            this.recentEmoji.add(entry.getKey());
        }
        Collections.sort(this.recentEmoji, new Comparator<String>() {
            public int compare(String lhs, String rhs) {
                Integer count1 = (Integer) EmojiView.this.emojiUseHistory.get(lhs);
                Integer count2 = (Integer) EmojiView.this.emojiUseHistory.get(rhs);
                if (count1 == null) {
                    count1 = Integer.valueOf(0);
                }
                if (count2 == null) {
                    count2 = Integer.valueOf(0);
                }
                if (count1.intValue() > count2.intValue()) {
                    return -1;
                }
                if (count1.intValue() < count2.intValue()) {
                    return 1;
                }
                return 0;
            }
        });
        while (this.recentEmoji.size() > 50) {
            this.recentEmoji.remove(this.recentEmoji.size() - 1);
        }
    }

    private void updateStickerTabs() {
        if (this.stickersTab != null) {
            int a;
            this.recentTabBum = -2;
            this.gifTabNum = -2;
            this.trendingTabNum = -2;
            this.stickersTabOffset = 0;
            int lastPosition = this.stickersTab.getCurrentPosition();
            this.stickersTab.removeTabs();
            Drawable drawable = getContext().getResources().getDrawable(R.drawable.ic_smiles2_smile);
            Theme.setDrawableColorByKey(drawable, Theme.key_chat_emojiPanelIcon);
            this.stickersTab.addIconTab(drawable);
            if (this.showGifs && !this.recentGifs.isEmpty()) {
                drawable = getContext().getResources().getDrawable(R.drawable.ic_smiles_gif);
                Theme.setDrawableColorByKey(drawable, Theme.key_chat_emojiPanelIcon);
                this.stickersTab.addIconTab(drawable);
                this.gifTabNum = this.stickersTabOffset;
                this.stickersTabOffset++;
            }
            ArrayList<Long> unread = StickersQuery.getUnreadStickerSets();
            if (!(this.trendingGridAdapter == null || this.trendingGridAdapter.getItemCount() == 0 || unread.isEmpty())) {
                TextView stickersCounter = this.stickersTab.addIconTabWithCounter(R.drawable.ic_smiles_trend);
                this.trendingTabNum = this.stickersTabOffset;
                this.stickersTabOffset++;
                stickersCounter.setText(String.format("%d", new Object[]{Integer.valueOf(unread.size())}));
            }
            if (!this.recentStickers.isEmpty()) {
                this.recentTabBum = this.stickersTabOffset;
                this.stickersTabOffset++;
                drawable = getContext().getResources().getDrawable(R.drawable.ic_smiles2_recent);
                Theme.setDrawableColorByKey(drawable, Theme.key_chat_emojiPanelIcon);
                this.stickersTab.addIconTab(drawable);
            }
            this.stickerSets.clear();
            ArrayList<TL_messages_stickerSet> packs = StickersQuery.getStickerSets(0);
            for (a = 0; a < packs.size(); a++) {
                TL_messages_stickerSet pack = (TL_messages_stickerSet) packs.get(a);
                if (!(pack.set.archived || pack.documents == null || pack.documents.isEmpty())) {
                    this.stickerSets.add(pack);
                }
            }
            for (a = 0; a < this.stickerSets.size(); a++) {
                this.stickersTab.addStickerTab((Document) ((TL_messages_stickerSet) this.stickerSets.get(a)).documents.get(0));
            }
            if (!(this.trendingGridAdapter == null || this.trendingGridAdapter.getItemCount() == 0 || !unread.isEmpty())) {
                drawable = getContext().getResources().getDrawable(R.drawable.ic_smiles_trend);
                Theme.setDrawableColorByKey(drawable, Theme.key_chat_emojiPanelIcon);
                this.trendingTabNum = this.stickersTabOffset + this.stickerSets.size();
                this.stickersTab.addIconTab(drawable);
            }
            drawable = getContext().getResources().getDrawable(R.drawable.ic_smiles_settings);
            Theme.setDrawableColorByKey(drawable, Theme.key_chat_emojiPanelIcon);
            this.stickersTab.addIconTab(drawable);
            this.stickersTab.updateTabStyles();
            if (lastPosition != 0) {
                this.stickersTab.onPageScrolled(lastPosition, lastPosition);
            }
            if (this.switchToGifTab && this.gifTabNum >= 0 && this.gifsGridView.getVisibility() != 0) {
                showGifTab();
                this.switchToGifTab = false;
            }
            checkPanels();
        }
    }

    private void checkPanels() {
        int i = 8;
        if (this.stickersTab != null) {
            if (this.trendingTabNum == -2 && this.trendingGridView != null && this.trendingGridView.getVisibility() == 0) {
                this.gifsGridView.setVisibility(8);
                this.trendingGridView.setVisibility(8);
                this.stickersGridView.setVisibility(0);
                this.stickersEmptyView.setVisibility(this.stickersGridAdapter.getItemCount() != 0 ? 8 : 0);
            }
            if (this.gifTabNum == -2 && this.gifsGridView != null && this.gifsGridView.getVisibility() == 0) {
                this.listener.onGifTab(false);
                this.gifsGridView.setVisibility(8);
                this.trendingGridView.setVisibility(8);
                this.stickersGridView.setVisibility(0);
                TextView textView = this.stickersEmptyView;
                if (this.stickersGridAdapter.getItemCount() == 0) {
                    i = 0;
                }
                textView.setVisibility(i);
            } else if (this.gifTabNum == -2) {
            } else {
                if (this.gifsGridView != null && this.gifsGridView.getVisibility() == 0) {
                    this.stickersTab.onPageScrolled(this.gifTabNum + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
                } else if (this.trendingGridView == null || this.trendingGridView.getVisibility() != 0) {
                    int position = this.stickersLayoutManager.findFirstVisibleItemPosition();
                    if (position != -1) {
                        this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(position) + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
                    }
                } else {
                    this.stickersTab.onPageScrolled(this.trendingTabNum + 1, (this.recentTabBum > 0 ? this.recentTabBum : this.stickersTabOffset) + 1);
                }
            }
        }
    }

    public void addRecentSticker(Document document) {
        if (document != null) {
            StickersQuery.addRecentSticker(0, document, (int) (System.currentTimeMillis() / 1000));
            boolean wasEmpty = this.recentStickers.isEmpty();
            this.recentStickers = StickersQuery.getRecentStickers(0);
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
            StickersQuery.addRecentGif(document, (int) (System.currentTimeMillis() / 1000));
            boolean wasEmpty = this.recentGifs.isEmpty();
            this.recentGifs = StickersQuery.getRecentGifs();
            if (this.gifsAdapter != null) {
                this.gifsAdapter.notifyDataSetChanged();
            }
            if (wasEmpty) {
                updateStickerTabs();
            }
        }
    }

    public void loadRecents() {
        String str;
        String[] args2;
        SharedPreferences preferences = getContext().getSharedPreferences("emoji", 0);
        try {
            this.emojiUseHistory.clear();
            if (preferences.contains("emojis")) {
                str = preferences.getString("emojis", "");
                if (str != null && str.length() > 0) {
                    for (String arg : str.split(",")) {
                        args2 = arg.split("=");
                        long value = Utilities.parseLong(args2[0]).longValue();
                        String string = "";
                        for (int a = 0; a < 4; a++) {
                            string = String.valueOf((char) ((int) value)) + string;
                            value >>= 16;
                            if (value == 0) {
                                break;
                            }
                        }
                        if (string.length() > 0) {
                            this.emojiUseHistory.put(string, Utilities.parseInt(args2[1]));
                        }
                    }
                }
                preferences.edit().remove("emojis").commit();
                saveRecentEmoji();
            } else {
                str = preferences.getString("emojis2", "");
                if (str != null && str.length() > 0) {
                    for (String arg2 : str.split(",")) {
                        args2 = arg2.split("=");
                        this.emojiUseHistory.put(args2[0], Utilities.parseInt(args2[1]));
                    }
                }
            }
            if (this.emojiUseHistory.isEmpty() && !preferences.getBoolean("filled_default", false)) {
                String[] newRecent = new String[]{"😂", "😘", "❤", "😍", "😊", "😁", "👍", "☺", "😔", "😄", "😭", "💋", "😒", "😳", "😜", "🙈", "😉", "😃", "😢", "😝", "😱", "😡", "😏", "😞", "😅", "😚", "🙊", "😌", "😀", "😋", "😆", "👌", "😐", "😕"};
                for (int i = 0; i < newRecent.length; i++) {
                    this.emojiUseHistory.put(newRecent[i], Integer.valueOf(newRecent.length - i));
                }
                preferences.edit().putBoolean("filled_default", true).commit();
                saveRecentEmoji();
            }
            sortEmoji();
            ((EmojiGridAdapter) this.adapters.get(0)).notifyDataSetChanged();
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
        try {
            str = preferences.getString(TtmlNode.ATTR_TTS_COLOR, "");
            if (str != null && str.length() > 0) {
                String[] args = str.split(",");
                for (String arg22 : args) {
                    args2 = arg22.split("=");
                    emojiColor.put(args2[0], args2[1]);
                }
            }
        } catch (Throwable e2) {
            FileLog.e("tmessages", e2);
        }
    }

    public void requestLayout() {
        if (!this.isLayout) {
            super.requestLayout();
        }
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.isLayout = true;
        if (AndroidUtilities.isInMultiwindow) {
            if (this.currentBackgroundType != 1) {
                if (VERSION.SDK_INT >= 21) {
                    setOutlineProvider((ViewOutlineProvider) this.outlineProvider);
                    setClipToOutline(true);
                    setElevation((float) AndroidUtilities.dp(2.0f));
                }
                setBackgroundResource(R.drawable.smiles_popup);
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
        LayoutParams layoutParams = (LayoutParams) this.emojiTab.getLayoutParams();
        LayoutParams layoutParams1 = null;
        layoutParams.width = MeasureSpec.getSize(widthMeasureSpec);
        if (this.stickersTab != null) {
            layoutParams1 = (LayoutParams) this.stickersTab.getLayoutParams();
            if (layoutParams1 != null) {
                layoutParams1.width = layoutParams.width;
            }
        }
        if (layoutParams.width != this.oldWidth) {
            if (!(this.stickersTab == null || layoutParams1 == null)) {
                onPageScrolled(this.pager.getCurrentItem(), (layoutParams.width - getPaddingLeft()) - getPaddingRight(), 0);
                this.stickersTab.setLayoutParams(layoutParams1);
            }
            this.emojiTab.setLayoutParams(layoutParams);
            this.oldWidth = layoutParams.width;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(layoutParams.width, NUM), MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), NUM));
        this.isLayout = false;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.lastNotifyWidth != right - left) {
            this.lastNotifyWidth = right - left;
            reloadStickersAdapter();
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
        if (StickerPreviewViewer.getInstance().isVisible()) {
            StickerPreviewViewer.getInstance().close();
        }
        StickerPreviewViewer.getInstance().reset();
    }

    public void setListener(Listener value) {
        this.listener = value;
    }

    public void invalidateViews() {
        for (int a = 0; a < this.emojiGrids.size(); a++) {
            ((GridView) this.emojiGrids.get(a)).invalidateViews();
        }
    }

    public void onOpen(boolean forceEmoji) {
        boolean z = true;
        if (this.stickersTab == null) {
            return;
        }
        if (this.currentPage == 0 || forceEmoji) {
            if (this.pager.getCurrentItem() == 6) {
                ViewPager viewPager = this.pager;
                if (forceEmoji) {
                    z = false;
                }
                viewPager.setCurrentItem(0, z);
            }
        } else if (this.currentPage == 1) {
            if (this.pager.getCurrentItem() != 6) {
                this.pager.setCurrentItem(6);
            }
            if (this.stickersTab.getCurrentPosition() != this.gifTabNum + 1) {
                return;
            }
            if (this.recentTabBum >= 0) {
                this.stickersTab.selectTab(this.recentTabBum + 1);
            } else if (this.gifTabNum >= 0) {
                this.stickersTab.selectTab(this.gifTabNum + 2);
            } else {
                this.stickersTab.selectTab(1);
            }
        } else if (this.currentPage == 2) {
            if (this.pager.getCurrentItem() != 6) {
                this.pager.setCurrentItem(6);
            }
            if (this.stickersTab.getCurrentPosition() == this.gifTabNum + 1) {
                return;
            }
            if (this.gifTabNum < 0 || this.recentGifs.isEmpty()) {
                this.switchToGifTab = true;
            } else {
                this.stickersTab.selectTab(this.gifTabNum + 1);
            }
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.featuredStickersDidLoaded);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    EmojiView.this.updateStickerTabs();
                    EmojiView.this.reloadStickersAdapter();
                }
            });
        }
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != 8) {
            sortEmoji();
            ((EmojiGridAdapter) this.adapters.get(0)).notifyDataSetChanged();
            if (this.stickersGridAdapter != null) {
                NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
                NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentDocumentsDidLoaded);
                updateStickerTabs();
                reloadStickersAdapter();
                if (!(this.gifsGridView == null || this.gifsGridView.getVisibility() != 0 || this.listener == null)) {
                    Listener listener = this.listener;
                    boolean z = this.pager != null && this.pager.getCurrentItem() >= 6;
                    listener.onGifTab(z);
                }
            }
            if (this.trendingGridAdapter != null) {
                this.trendingLoaded = false;
                this.trendingGridAdapter.notifyDataSetChanged();
            }
            checkDocuments(true);
            checkDocuments(false);
            StickersQuery.loadRecents(0, true, true);
            StickersQuery.loadRecents(0, false, true);
        }
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void onDestroy() {
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stickersDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recentDocumentsDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.pickerViewPopup != null && this.pickerViewPopup.isShowing()) {
            this.pickerViewPopup.dismiss();
        }
    }

    private void checkDocuments(boolean isGif) {
        int previousCount;
        if (isGif) {
            previousCount = this.recentGifs.size();
            this.recentGifs = StickersQuery.getRecentGifs();
            if (this.gifsAdapter != null) {
                this.gifsAdapter.notifyDataSetChanged();
            }
            if (previousCount != this.recentGifs.size()) {
                updateStickerTabs();
                return;
            }
            return;
        }
        previousCount = this.recentStickers.size();
        this.recentStickers = StickersQuery.getRecentStickers(0);
        if (this.stickersGridAdapter != null) {
            this.stickersGridAdapter.notifyDataSetChanged();
        }
        if (previousCount != this.recentStickers.size()) {
            updateStickerTabs();
        }
    }

    private void updateVisibleTrendingSets() {
        if (this.trendingGridAdapter != null && this.trendingGridAdapter != null) {
            try {
                int count = this.trendingGridView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.trendingGridView.getChildAt(a);
                    if ((child instanceof FeaturedStickerSetInfoCell) && ((Holder) this.trendingGridView.getChildViewHolder(child)) != null) {
                        FeaturedStickerSetInfoCell cell = (FeaturedStickerSetInfoCell) child;
                        ArrayList<Long> unreadStickers = StickersQuery.getUnreadStickerSets();
                        StickerSetCovered stickerSetCovered = cell.getStickerSet();
                        boolean unread = unreadStickers != null && unreadStickers.contains(Long.valueOf(stickerSetCovered.set.id));
                        cell.setStickerSet(stickerSetCovered, unread);
                        if (unread) {
                            StickersQuery.markFaturedStickersByIdAsRead(stickerSetCovered.set.id);
                        }
                        boolean installing = this.installingStickerSets.containsKey(Long.valueOf(stickerSetCovered.set.id));
                        boolean removing = this.removingStickerSets.containsKey(Long.valueOf(stickerSetCovered.set.id));
                        if (installing || removing) {
                            if (installing && cell.isInstalled()) {
                                this.installingStickerSets.remove(Long.valueOf(stickerSetCovered.set.id));
                                installing = false;
                            } else if (removing) {
                                if (!cell.isInstalled()) {
                                    this.removingStickerSets.remove(Long.valueOf(stickerSetCovered.set.id));
                                    removing = false;
                                }
                            }
                        }
                        boolean z = installing || removing;
                        cell.setDrawProgress(z);
                    }
                }
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.stickersDidLoaded) {
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
        } else if (id == NotificationCenter.recentDocumentsDidLoaded) {
            boolean isGif = ((Boolean) args[0]).booleanValue();
            if (isGif || ((Integer) args[1]).intValue() == 0) {
                checkDocuments(isGif);
            }
        } else if (id == NotificationCenter.featuredStickersDidLoaded) {
            if (this.trendingGridAdapter != null) {
                if (this.featuresStickersHash != StickersQuery.getFeaturesStickersHashWithoutUnread()) {
                    this.trendingLoaded = false;
                }
                if (this.trendingLoaded) {
                    updateVisibleTrendingSets();
                } else {
                    this.trendingGridAdapter.notifyDataSetChanged();
                }
            }
            if (this.pagerSlidingTabStrip != null) {
                int count = this.pagerSlidingTabStrip.getChildCount();
                for (int a = 0; a < count; a++) {
                    this.pagerSlidingTabStrip.getChildAt(a).invalidate();
                }
            }
            updateStickerTabs();
        }
    }
}
