package org.telegram.ui.Components;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
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
import android.view.ViewGroup.LayoutParams;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
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
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.StickerPreviewViewer;

public class EmojiView
  extends FrameLayout
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final ViewTreeObserver.OnScrollChangedListener NOP = new ViewTreeObserver.OnScrollChangedListener()
  {
    public void onScrollChanged() {}
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
  private ExtendedGridLayoutManager flowLayoutManager;
  private int gifTabNum = -2;
  private GifsAdapter gifsAdapter;
  private RecyclerListView gifsGridView;
  private int[] icons = { 2130837725, 2130837726, 2130837724, 2130837722, 2130837723, 2130837727, 2130837765 };
  private HashMap<Long, TLRPC.StickerSetCovered> installingStickerSets = new HashMap();
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
  private ArrayList<TLRPC.Document> recentGifs = new ArrayList();
  private ArrayList<TLRPC.Document> recentStickers = new ArrayList();
  private int recentTabBum = -2;
  private HashMap<Long, TLRPC.StickerSetCovered> removingStickerSets = new HashMap();
  private boolean showGifs;
  private ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = new ArrayList();
  private TextView stickersEmptyView;
  private StickersGridAdapter stickersGridAdapter;
  private RecyclerListView stickersGridView;
  private GridLayoutManager stickersLayoutManager;
  private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
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
  
  static
  {
    Object localObject = null;
    try
    {
      Field localField = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
      localObject = localField;
      localField.setAccessible(true);
      localObject = localField;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      for (;;) {}
    }
    superListenerField = (Field)localObject;
  }
  
  public EmojiView(boolean paramBoolean1, boolean paramBoolean2, Context paramContext)
  {
    super(paramContext);
    this.showGifs = paramBoolean2;
    this.dotDrawable = paramContext.getResources().getDrawable(2130837550);
    if (Build.VERSION.SDK_INT >= 21) {
      this.outlineProvider = new ViewOutlineProvider()
      {
        @TargetApi(21)
        public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
        {
          paramAnonymousOutline.setRoundRect(paramAnonymousView.getPaddingLeft(), paramAnonymousView.getPaddingTop(), paramAnonymousView.getMeasuredWidth() - paramAnonymousView.getPaddingRight(), paramAnonymousView.getMeasuredHeight() - paramAnonymousView.getPaddingBottom(), AndroidUtilities.dp(6.0F));
        }
      };
    }
    int i = 0;
    if (i < EmojiData.dataColored.length + 1)
    {
      localObject1 = new GridView(paramContext);
      if (AndroidUtilities.isTablet()) {
        ((GridView)localObject1).setColumnWidth(AndroidUtilities.dp(60.0F));
      }
      for (;;)
      {
        ((GridView)localObject1).setNumColumns(-1);
        localObject2 = new EmojiGridAdapter(i - 1);
        AndroidUtilities.setListViewEdgeEffectColor((AbsListView)localObject1, -657673);
        ((GridView)localObject1).setAdapter((ListAdapter)localObject2);
        this.adapters.add(localObject2);
        this.emojiGrids.add(localObject1);
        localObject2 = new FrameLayout(paramContext);
        ((FrameLayout)localObject2).addView((View)localObject1, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 48.0F, 0.0F, 0.0F));
        this.views.add(localObject2);
        i += 1;
        break;
        ((GridView)localObject1).setColumnWidth(AndroidUtilities.dp(45.0F));
      }
    }
    if (paramBoolean1)
    {
      this.stickersWrap = new FrameLayout(paramContext);
      StickersQuery.checkStickers(0);
      StickersQuery.checkFeaturedStickers();
      this.stickersGridView = new RecyclerListView(paramContext)
      {
        public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          boolean bool = StickerPreviewViewer.getInstance().onInterceptTouchEvent(paramAnonymousMotionEvent, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight());
          return (super.onInterceptTouchEvent(paramAnonymousMotionEvent)) || (bool);
        }
        
        public void setVisibility(int paramAnonymousInt)
        {
          if (((EmojiView.this.gifsGridView != null) && (EmojiView.this.gifsGridView.getVisibility() == 0)) || ((EmojiView.this.trendingGridView != null) && (EmojiView.this.trendingGridView.getVisibility() == 0)))
          {
            super.setVisibility(8);
            return;
          }
          super.setVisibility(paramAnonymousInt);
        }
      };
      localObject1 = this.stickersGridView;
      localObject2 = new GridLayoutManager(paramContext, 5);
      this.stickersLayoutManager = ((GridLayoutManager)localObject2);
      ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject2);
      this.stickersLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
      {
        public int getSpanSize(int paramAnonymousInt)
        {
          if (paramAnonymousInt == EmojiView.StickersGridAdapter.access$2800(EmojiView.this.stickersGridAdapter)) {
            return EmojiView.StickersGridAdapter.access$2900(EmojiView.this.stickersGridAdapter);
          }
          return 1;
        }
      });
      this.stickersGridView.setPadding(0, AndroidUtilities.dp(52.0F), 0, 0);
      this.stickersGridView.setClipToPadding(false);
      this.views.add(this.stickersWrap);
      localObject1 = this.stickersGridView;
      localObject2 = new StickersGridAdapter(paramContext);
      this.stickersGridAdapter = ((StickersGridAdapter)localObject2);
      ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
      this.stickersGridView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return StickerPreviewViewer.getInstance().onTouch(paramAnonymousMotionEvent, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.stickersOnItemClickListener);
        }
      });
      this.stickersOnItemClickListener = new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if (!(paramAnonymousView instanceof StickerEmojiCell)) {}
          do
          {
            return;
            StickerPreviewViewer.getInstance().reset();
            paramAnonymousView = (StickerEmojiCell)paramAnonymousView;
          } while (paramAnonymousView.isDisabled());
          paramAnonymousView.disable();
          EmojiView.this.listener.onStickerSelected(paramAnonymousView.getSticker());
        }
      };
      this.stickersGridView.setOnItemClickListener(this.stickersOnItemClickListener);
      this.stickersGridView.setGlowColor(-657673);
      this.stickersWrap.addView(this.stickersGridView);
      this.trendingGridView = new RecyclerListView(paramContext);
      this.trendingGridView.setItemAnimator(null);
      this.trendingGridView.setLayoutAnimation(null);
      localObject1 = this.trendingGridView;
      localObject2 = new GridLayoutManager(paramContext, 5)
      {
        public boolean supportsPredictiveItemAnimations()
        {
          return false;
        }
      };
      this.trendingLayoutManager = ((GridLayoutManager)localObject2);
      ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject2);
      this.trendingLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
      {
        public int getSpanSize(int paramAnonymousInt)
        {
          if (((EmojiView.TrendingGridAdapter.access$3200(EmojiView.this.trendingGridAdapter).get(Integer.valueOf(paramAnonymousInt)) instanceof Integer)) || (paramAnonymousInt == EmojiView.TrendingGridAdapter.access$3300(EmojiView.this.trendingGridAdapter))) {
            return EmojiView.TrendingGridAdapter.access$3400(EmojiView.this.trendingGridAdapter);
          }
          return 1;
        }
      });
      this.trendingGridView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          EmojiView.this.checkStickersTabY(paramAnonymousRecyclerView, paramAnonymousInt2);
        }
      });
      this.trendingGridView.setClipToPadding(false);
      this.trendingGridView.setPadding(0, AndroidUtilities.dp(48.0F), 0, 0);
      localObject1 = this.trendingGridView;
      localObject2 = new TrendingGridAdapter(paramContext);
      this.trendingGridAdapter = ((TrendingGridAdapter)localObject2);
      ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
      this.trendingGridView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          paramAnonymousView = (TLRPC.StickerSetCovered)EmojiView.TrendingGridAdapter.access$3600(EmojiView.this.trendingGridAdapter).get(Integer.valueOf(paramAnonymousInt));
          if (paramAnonymousView != null) {
            EmojiView.this.listener.onShowStickerSet(paramAnonymousView);
          }
        }
      });
      this.trendingGridAdapter.notifyDataSetChanged();
      this.trendingGridView.setGlowColor(-657673);
      this.trendingGridView.setVisibility(8);
      this.stickersWrap.addView(this.trendingGridView);
      if (paramBoolean2)
      {
        this.gifsGridView = new RecyclerListView(paramContext);
        this.gifsGridView.setClipToPadding(false);
        this.gifsGridView.setPadding(0, AndroidUtilities.dp(48.0F), 0, 0);
        localObject1 = this.gifsGridView;
        localObject2 = new ExtendedGridLayoutManager(paramContext, 100)
        {
          private Size size = new Size();
          
          protected Size getSizeForItem(int paramAnonymousInt)
          {
            float f2 = 100.0F;
            TLRPC.Document localDocument = (TLRPC.Document)EmojiView.this.recentGifs.get(paramAnonymousInt);
            Object localObject = this.size;
            float f1;
            if ((localDocument.thumb != null) && (localDocument.thumb.w != 0))
            {
              f1 = localDocument.thumb.w;
              ((Size)localObject).width = f1;
              localObject = this.size;
              f1 = f2;
              if (localDocument.thumb != null)
              {
                f1 = f2;
                if (localDocument.thumb.h != 0) {
                  f1 = localDocument.thumb.h;
                }
              }
              ((Size)localObject).height = f1;
              paramAnonymousInt = 0;
            }
            for (;;)
            {
              if (paramAnonymousInt < localDocument.attributes.size())
              {
                localObject = (TLRPC.DocumentAttribute)localDocument.attributes.get(paramAnonymousInt);
                if (((localObject instanceof TLRPC.TL_documentAttributeImageSize)) || ((localObject instanceof TLRPC.TL_documentAttributeVideo)))
                {
                  this.size.width = ((TLRPC.DocumentAttribute)localObject).w;
                  this.size.height = ((TLRPC.DocumentAttribute)localObject).h;
                }
              }
              else
              {
                return this.size;
                f1 = 100.0F;
                break;
              }
              paramAnonymousInt += 1;
            }
          }
        };
        this.flowLayoutManager = ((ExtendedGridLayoutManager)localObject2);
        ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject2);
        this.flowLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
        {
          public int getSpanSize(int paramAnonymousInt)
          {
            return EmojiView.this.flowLayoutManager.getSpanSizeForItem(paramAnonymousInt);
          }
        });
        this.gifsGridView.addItemDecoration(new RecyclerView.ItemDecoration()
        {
          public void getItemOffsets(Rect paramAnonymousRect, View paramAnonymousView, RecyclerView paramAnonymousRecyclerView, RecyclerView.State paramAnonymousState)
          {
            int i = 0;
            paramAnonymousRect.left = 0;
            paramAnonymousRect.top = 0;
            paramAnonymousRect.bottom = 0;
            int j = paramAnonymousRecyclerView.getChildAdapterPosition(paramAnonymousView);
            if (!EmojiView.this.flowLayoutManager.isFirstRow(j)) {
              paramAnonymousRect.top = AndroidUtilities.dp(2.0F);
            }
            if (EmojiView.this.flowLayoutManager.isLastInRow(j)) {}
            for (;;)
            {
              paramAnonymousRect.right = i;
              return;
              i = AndroidUtilities.dp(2.0F);
            }
          }
        });
        this.gifsGridView.setOverScrollMode(2);
        localObject1 = this.gifsGridView;
        localObject2 = new GifsAdapter(paramContext);
        this.gifsAdapter = ((GifsAdapter)localObject2);
        ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
        this.gifsGridView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
          public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
          {
            EmojiView.this.checkStickersTabY(paramAnonymousRecyclerView, paramAnonymousInt2);
          }
        });
        this.gifsGridView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
        {
          public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
          {
            if ((paramAnonymousInt < 0) || (paramAnonymousInt >= EmojiView.this.recentGifs.size()) || (EmojiView.this.listener == null)) {
              return;
            }
            EmojiView.this.listener.onGifSelected((TLRPC.Document)EmojiView.this.recentGifs.get(paramAnonymousInt));
          }
        });
        this.gifsGridView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
        {
          public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
          {
            if ((paramAnonymousInt < 0) || (paramAnonymousInt >= EmojiView.this.recentGifs.size())) {
              return false;
            }
            final TLRPC.Document localDocument = (TLRPC.Document)EmojiView.this.recentGifs.get(paramAnonymousInt);
            paramAnonymousView = new AlertDialog.Builder(paramAnonymousView.getContext());
            paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165299));
            paramAnonymousView.setMessage(LocaleController.getString("DeleteGif", 2131165572));
            paramAnonymousView.setPositiveButton(LocaleController.getString("OK", 2131166044).toUpperCase(), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                StickersQuery.removeRecentGif(localDocument);
                EmojiView.access$3702(EmojiView.this, StickersQuery.getRecentGifs());
                if (EmojiView.this.gifsAdapter != null) {
                  EmojiView.this.gifsAdapter.notifyDataSetChanged();
                }
                if (EmojiView.this.recentGifs.isEmpty()) {
                  EmojiView.this.updateStickerTabs();
                }
              }
            });
            paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
            paramAnonymousView.show().setCanceledOnTouchOutside(true);
            return true;
          }
        });
        this.gifsGridView.setVisibility(8);
        this.stickersWrap.addView(this.gifsGridView);
      }
      this.stickersEmptyView = new TextView(paramContext);
      this.stickersEmptyView.setText(LocaleController.getString("NoStickers", 2131165957));
      this.stickersEmptyView.setTextSize(1, 18.0F);
      this.stickersEmptyView.setTextColor(-7829368);
      this.stickersWrap.addView(this.stickersEmptyView, LayoutHelper.createFrame(-2, -2.0F, 17, 0.0F, 48.0F, 0.0F, 0.0F));
      this.stickersGridView.setEmptyView(this.stickersEmptyView);
      this.stickersTab = new ScrollSlidingTabStrip(paramContext)
      {
        boolean first = true;
        float lastTranslateX;
        float lastX;
        boolean startedScroll;
        
        public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
          }
          return super.onInterceptTouchEvent(paramAnonymousMotionEvent);
        }
        
        public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          boolean bool = false;
          if (this.first)
          {
            this.first = false;
            this.lastX = paramAnonymousMotionEvent.getX();
          }
          float f = EmojiView.this.stickersTab.getTranslationX();
          if ((EmojiView.this.stickersTab.getScrollX() == 0) && (f == 0.0F))
          {
            if ((this.startedScroll) || (this.lastX - paramAnonymousMotionEvent.getX() >= 0.0F)) {
              break label220;
            }
            if (EmojiView.this.pager.beginFakeDrag())
            {
              this.startedScroll = true;
              this.lastTranslateX = EmojiView.this.stickersTab.getTranslationX();
            }
          }
          int i;
          if (this.startedScroll) {
            i = (int)(paramAnonymousMotionEvent.getX() - this.lastX + f - this.lastTranslateX);
          }
          for (;;)
          {
            label220:
            try
            {
              EmojiView.this.pager.fakeDragBy(i);
              this.lastTranslateX = f;
              this.lastX = paramAnonymousMotionEvent.getX();
              if ((paramAnonymousMotionEvent.getAction() == 3) || (paramAnonymousMotionEvent.getAction() == 1))
              {
                this.first = true;
                if (this.startedScroll)
                {
                  EmojiView.this.pager.endFakeDrag();
                  this.startedScroll = false;
                }
              }
              if ((this.startedScroll) || (super.onTouchEvent(paramAnonymousMotionEvent))) {
                bool = true;
              }
              return bool;
            }
            catch (Exception localException1) {}
            if ((!this.startedScroll) || (this.lastX - paramAnonymousMotionEvent.getX() <= 0.0F) || (!EmojiView.this.pager.isFakeDragging())) {
              break;
            }
            EmojiView.this.pager.endFakeDrag();
            this.startedScroll = false;
            break;
            try
            {
              EmojiView.this.pager.endFakeDrag();
              this.startedScroll = false;
              FileLog.e("tmessages", localException1);
            }
            catch (Exception localException2)
            {
              for (;;) {}
            }
          }
        }
      };
      this.stickersTab.setUnderlineHeight(AndroidUtilities.dp(1.0F));
      this.stickersTab.setIndicatorColor(-1907225);
      this.stickersTab.setUnderlineColor(-1907225);
      this.stickersTab.setBackgroundColor(-657673);
      this.stickersTab.setVisibility(4);
      addView(this.stickersTab, LayoutHelper.createFrame(-1, 48, 51));
      this.stickersTab.setTranslationX(AndroidUtilities.displaySize.x);
      updateStickerTabs();
      this.stickersTab.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate()
      {
        public void onPageSelected(int paramAnonymousInt)
        {
          int i = 8;
          if (EmojiView.this.gifsGridView != null)
          {
            if (paramAnonymousInt != EmojiView.this.gifTabNum + 1) {
              break label75;
            }
            if (EmojiView.this.gifsGridView.getVisibility() != 0)
            {
              EmojiView.this.listener.onGifTab(true);
              EmojiView.this.showGifTab();
            }
          }
          if (paramAnonymousInt == 0) {
            EmojiView.this.pager.setCurrentItem(0);
          }
          label75:
          do
          {
            Object localObject;
            do
            {
              return;
              if (paramAnonymousInt == EmojiView.this.trendingTabNum + 1)
              {
                if (EmojiView.this.trendingGridView.getVisibility() == 0) {
                  break;
                }
                EmojiView.this.showTrendingTab();
                break;
              }
              if (EmojiView.this.gifsGridView.getVisibility() == 0)
              {
                EmojiView.this.listener.onGifTab(false);
                EmojiView.this.gifsGridView.setVisibility(8);
                EmojiView.this.stickersGridView.setVisibility(0);
                localObject = EmojiView.this.stickersEmptyView;
                if (EmojiView.this.stickersGridAdapter.getItemCount() != 0) {}
                for (;;)
                {
                  ((TextView)localObject).setVisibility(i);
                  EmojiView.this.saveNewPage();
                  break;
                  i = 0;
                }
              }
              if (EmojiView.this.trendingGridView.getVisibility() != 0) {
                break;
              }
              EmojiView.this.trendingGridView.setVisibility(8);
              EmojiView.this.stickersGridView.setVisibility(0);
              localObject = EmojiView.this.stickersEmptyView;
              if (EmojiView.this.stickersGridAdapter.getItemCount() != 0) {}
              for (;;)
              {
                ((TextView)localObject).setVisibility(i);
                EmojiView.this.saveNewPage();
                break;
                i = 0;
              }
            } while ((paramAnonymousInt == EmojiView.this.gifTabNum + 1) || (paramAnonymousInt == EmojiView.this.trendingTabNum + 1));
            if (paramAnonymousInt == EmojiView.this.recentTabBum + 1)
            {
              EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
              EmojiView.this.checkStickersTabY(null, 0);
              localObject = EmojiView.this.stickersTab;
              i = EmojiView.this.recentTabBum;
              if (EmojiView.this.recentTabBum > 0) {}
              for (paramAnonymousInt = EmojiView.this.recentTabBum;; paramAnonymousInt = EmojiView.this.stickersTabOffset)
              {
                ((ScrollSlidingTabStrip)localObject).onPageScrolled(i + 1, paramAnonymousInt + 1);
                return;
              }
            }
            i = paramAnonymousInt - 1 - EmojiView.this.stickersTabOffset;
            if (i < EmojiView.this.stickerSets.size()) {
              break label443;
            }
          } while (EmojiView.this.listener == null);
          EmojiView.this.listener.onStickersSettingsClick();
          return;
          label443:
          paramAnonymousInt = i;
          if (i >= EmojiView.this.stickerSets.size()) {
            paramAnonymousInt = EmojiView.this.stickerSets.size() - 1;
          }
          EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(EmojiView.this.stickersGridAdapter.getPositionForPack((TLRPC.TL_messages_stickerSet)EmojiView.this.stickerSets.get(paramAnonymousInt)), 0);
          EmojiView.this.checkStickersTabY(null, 0);
          EmojiView.this.checkScroll();
        }
      });
      this.stickersGridView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          EmojiView.this.checkScroll();
          EmojiView.this.checkStickersTabY(paramAnonymousRecyclerView, paramAnonymousInt2);
        }
      });
    }
    this.pager = new ViewPager(paramContext)
    {
      public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if (getParent() != null) {
          getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(paramAnonymousMotionEvent);
      }
    };
    this.pager.setAdapter(new EmojiPagesAdapter(null));
    this.emojiTab = new LinearLayout(paramContext)
    {
      public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if (getParent() != null) {
          getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(paramAnonymousMotionEvent);
      }
    };
    this.emojiTab.setOrientation(0);
    addView(this.emojiTab, LayoutHelper.createFrame(-1, 48.0F));
    this.pagerSlidingTabStrip = new PagerSlidingTabStrip(paramContext);
    this.pagerSlidingTabStrip.setViewPager(this.pager);
    this.pagerSlidingTabStrip.setShouldExpand(true);
    this.pagerSlidingTabStrip.setIndicatorHeight(AndroidUtilities.dp(2.0F));
    this.pagerSlidingTabStrip.setUnderlineHeight(AndroidUtilities.dp(1.0F));
    this.pagerSlidingTabStrip.setIndicatorColor(-13920542);
    this.pagerSlidingTabStrip.setUnderlineColor(-1907225);
    this.emojiTab.addView(this.pagerSlidingTabStrip, LayoutHelper.createLinear(0, 48, 1.0F));
    this.pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
    {
      public void onPageScrollStateChanged(int paramAnonymousInt) {}
      
      public void onPageScrolled(int paramAnonymousInt1, float paramAnonymousFloat, int paramAnonymousInt2)
      {
        EmojiView.this.onPageScrolled(paramAnonymousInt1, EmojiView.this.getMeasuredWidth() - EmojiView.this.getPaddingLeft() - EmojiView.this.getPaddingRight(), paramAnonymousInt2);
      }
      
      public void onPageSelected(int paramAnonymousInt)
      {
        EmojiView.this.saveNewPage();
      }
    });
    Object localObject1 = new FrameLayout(paramContext);
    this.emojiTab.addView((View)localObject1, LayoutHelper.createLinear(52, 48));
    this.backspaceButton = new ImageView(paramContext)
    {
      public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if (paramAnonymousMotionEvent.getAction() == 0)
        {
          EmojiView.access$5502(EmojiView.this, true);
          EmojiView.access$5602(EmojiView.this, false);
          EmojiView.this.postBackspaceRunnable(350);
        }
        for (;;)
        {
          super.onTouchEvent(paramAnonymousMotionEvent);
          return true;
          if ((paramAnonymousMotionEvent.getAction() == 3) || (paramAnonymousMotionEvent.getAction() == 1))
          {
            EmojiView.access$5502(EmojiView.this, false);
            if ((!EmojiView.this.backspaceOnce) && (EmojiView.this.listener != null) && (EmojiView.this.listener.onBackspace())) {
              EmojiView.this.backspaceButton.performHapticFeedback(3);
            }
          }
        }
      }
    };
    this.backspaceButton.setImageResource(2130837767);
    this.backspaceButton.setBackgroundResource(2130837721);
    this.backspaceButton.setScaleType(ImageView.ScaleType.CENTER);
    ((FrameLayout)localObject1).addView(this.backspaceButton, LayoutHelper.createFrame(52, 48.0F));
    Object localObject2 = new View(paramContext);
    ((View)localObject2).setBackgroundColor(-1907225);
    ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(52, 1, 83));
    localObject1 = new TextView(paramContext);
    ((TextView)localObject1).setText(LocaleController.getString("NoRecent", 2131165946));
    ((TextView)localObject1).setTextSize(18.0F);
    ((TextView)localObject1).setTextColor(-7829368);
    ((TextView)localObject1).setGravity(17);
    ((TextView)localObject1).setClickable(false);
    ((TextView)localObject1).setFocusable(false);
    ((FrameLayout)this.views.get(0)).addView((View)localObject1, LayoutHelper.createFrame(-2, -2.0F, 17, 0.0F, 48.0F, 0.0F, 0.0F));
    ((GridView)this.emojiGrids.get(0)).setEmptyView((View)localObject1);
    addView(this.pager, 0, LayoutHelper.createFrame(-1, -1, 51));
    if (AndroidUtilities.isTablet())
    {
      f = 40.0F;
      this.emojiSize = AndroidUtilities.dp(f);
      this.pickerView = new EmojiColorPickerView(paramContext);
      paramContext = this.pickerView;
      if (!AndroidUtilities.isTablet()) {
        break label1831;
      }
      i = 40;
      label1669:
      i = AndroidUtilities.dp(i * 6 + 10 + 20);
      this.popupWidth = i;
      if (!AndroidUtilities.isTablet()) {
        break label1838;
      }
    }
    label1831:
    label1838:
    for (float f = 64.0F;; f = 56.0F)
    {
      int j = AndroidUtilities.dp(f);
      this.popupHeight = j;
      this.pickerViewPopup = new EmojiPopupWindow(paramContext, i, j);
      this.pickerViewPopup.setOutsideTouchable(true);
      this.pickerViewPopup.setClippingEnabled(true);
      this.pickerViewPopup.setInputMethodMode(2);
      this.pickerViewPopup.setSoftInputMode(0);
      this.pickerViewPopup.getContentView().setFocusableInTouchMode(true);
      this.pickerViewPopup.getContentView().setOnKeyListener(new View.OnKeyListener()
      {
        public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if ((paramAnonymousInt == 82) && (paramAnonymousKeyEvent.getRepeatCount() == 0) && (paramAnonymousKeyEvent.getAction() == 1) && (EmojiView.this.pickerViewPopup != null) && (EmojiView.this.pickerViewPopup.isShowing()))
          {
            EmojiView.this.pickerViewPopup.dismiss();
            return true;
          }
          return false;
        }
      });
      this.currentPage = getContext().getSharedPreferences("emoji", 0).getInt("selected_page", 0);
      loadRecents();
      return;
      f = 32.0F;
      break;
      i = 32;
      break label1669;
    }
  }
  
  private static String addColorToCode(String paramString1, String paramString2)
  {
    String str1 = null;
    String str2;
    if (!paramString1.endsWith("‍♀"))
    {
      str2 = paramString1;
      if (!paramString1.endsWith("‍♂")) {}
    }
    else
    {
      str1 = paramString1.substring(paramString1.length() - 2);
      str2 = paramString1.substring(0, paramString1.length() - 2);
    }
    paramString2 = str2 + paramString2;
    paramString1 = paramString2;
    if (str1 != null) {
      paramString1 = paramString2 + str1;
    }
    return paramString1;
  }
  
  private void checkDocuments(boolean paramBoolean)
  {
    int i;
    if (paramBoolean)
    {
      i = this.recentGifs.size();
      this.recentGifs = StickersQuery.getRecentGifs();
      if (this.gifsAdapter != null) {
        this.gifsAdapter.notifyDataSetChanged();
      }
      if (i != this.recentGifs.size()) {
        updateStickerTabs();
      }
    }
    do
    {
      return;
      i = this.recentStickers.size();
      this.recentStickers = StickersQuery.getRecentStickers(0);
      if (this.stickersGridAdapter != null) {
        this.stickersGridAdapter.notifyDataSetChanged();
      }
    } while (i == this.recentStickers.size());
    updateStickerTabs();
  }
  
  private void checkPanels()
  {
    int j = 8;
    if (this.stickersTab == null) {}
    label176:
    do
    {
      do
      {
        return;
        if ((this.trendingTabNum == -2) && (this.trendingGridView != null) && (this.trendingGridView.getVisibility() == 0))
        {
          this.gifsGridView.setVisibility(8);
          this.trendingGridView.setVisibility(8);
          this.stickersGridView.setVisibility(0);
          localObject = this.stickersEmptyView;
          if (this.stickersGridAdapter.getItemCount() != 0)
          {
            i = 8;
            ((TextView)localObject).setVisibility(i);
          }
        }
        else
        {
          if ((this.gifTabNum != -2) || (this.gifsGridView == null) || (this.gifsGridView.getVisibility() != 0)) {
            continue;
          }
          this.listener.onGifTab(false);
          this.gifsGridView.setVisibility(8);
          this.trendingGridView.setVisibility(8);
          this.stickersGridView.setVisibility(0);
          localObject = this.stickersEmptyView;
          if (this.stickersGridAdapter.getItemCount() == 0) {
            break label176;
          }
        }
        for (i = j;; i = 0)
        {
          ((TextView)localObject).setVisibility(i);
          return;
          i = 0;
          break;
        }
      } while (this.gifTabNum == -2);
      if ((this.gifsGridView != null) && (this.gifsGridView.getVisibility() == 0))
      {
        localObject = this.stickersTab;
        j = this.gifTabNum;
        if (this.recentTabBum > 0) {}
        for (i = this.recentTabBum;; i = this.stickersTabOffset)
        {
          ((ScrollSlidingTabStrip)localObject).onPageScrolled(j + 1, i + 1);
          return;
        }
      }
      if ((this.trendingGridView != null) && (this.trendingGridView.getVisibility() == 0))
      {
        localObject = this.stickersTab;
        j = this.trendingTabNum;
        if (this.recentTabBum > 0) {}
        for (i = this.recentTabBum;; i = this.stickersTabOffset)
        {
          ((ScrollSlidingTabStrip)localObject).onPageScrolled(j + 1, i + 1);
          return;
        }
      }
      i = this.stickersLayoutManager.findFirstVisibleItemPosition();
    } while (i == -1);
    Object localObject = this.stickersTab;
    j = this.stickersGridAdapter.getTabForPosition(i);
    if (this.recentTabBum > 0) {}
    for (int i = this.recentTabBum;; i = this.stickersTabOffset)
    {
      ((ScrollSlidingTabStrip)localObject).onPageScrolled(j + 1, i + 1);
      return;
    }
  }
  
  private void checkScroll()
  {
    int i = this.stickersLayoutManager.findFirstVisibleItemPosition();
    if (i == -1) {
      return;
    }
    checkStickersScroll(i);
  }
  
  private void checkStickersScroll(int paramInt)
  {
    if (this.stickersGridView == null) {
      return;
    }
    if (this.stickersGridView.getVisibility() != 0)
    {
      if ((this.gifsGridView != null) && (this.gifsGridView.getVisibility() != 0)) {
        this.gifsGridView.setVisibility(0);
      }
      if ((this.stickersEmptyView != null) && (this.stickersEmptyView.getVisibility() == 0)) {
        this.stickersEmptyView.setVisibility(8);
      }
      localScrollSlidingTabStrip = this.stickersTab;
      i = this.gifTabNum;
      if (this.recentTabBum > 0) {}
      for (paramInt = this.recentTabBum;; paramInt = this.stickersTabOffset)
      {
        localScrollSlidingTabStrip.onPageScrolled(i + 1, paramInt + 1);
        return;
      }
    }
    ScrollSlidingTabStrip localScrollSlidingTabStrip = this.stickersTab;
    int i = this.stickersGridAdapter.getTabForPosition(paramInt);
    if (this.recentTabBum > 0) {}
    for (paramInt = this.recentTabBum;; paramInt = this.stickersTabOffset)
    {
      localScrollSlidingTabStrip.onPageScrolled(i + 1, paramInt + 1);
      return;
    }
  }
  
  private void checkStickersTabY(View paramView, int paramInt)
  {
    if (paramView == null)
    {
      paramView = this.stickersTab;
      this.minusDy = 0;
      paramView.setTranslationY(0);
    }
    while (paramView.getVisibility() != 0) {
      return;
    }
    this.minusDy -= paramInt;
    if (this.minusDy > 0) {
      this.minusDy = 0;
    }
    for (;;)
    {
      this.stickersTab.setTranslationY(Math.max(-AndroidUtilities.dp(47.0F), this.minusDy));
      return;
      if (this.minusDy < -AndroidUtilities.dp(288.0F)) {
        this.minusDy = (-AndroidUtilities.dp(288.0F));
      }
    }
  }
  
  private String convert(long paramLong)
  {
    Object localObject1 = "";
    int i = 0;
    for (;;)
    {
      if (i >= 4) {
        return (String)localObject1;
      }
      int j = (int)(0xFFFF & paramLong >> (3 - i) * 16);
      Object localObject2 = localObject1;
      if (j != 0) {
        localObject2 = (String)localObject1 + (char)j;
      }
      i += 1;
      localObject1 = localObject2;
    }
  }
  
  private void onPageScrolled(int paramInt1, int paramInt2, int paramInt3)
  {
    boolean bool = true;
    int j = 0;
    if (this.stickersTab == null) {
      return;
    }
    int i = paramInt2;
    if (paramInt2 == 0) {
      i = AndroidUtilities.displaySize.x;
    }
    paramInt2 = 0;
    Object localObject;
    if (paramInt1 == 5)
    {
      paramInt2 = -paramInt3;
      paramInt1 = paramInt2;
      if (this.listener != null)
      {
        localObject = this.listener;
        if (paramInt3 != 0)
        {
          label58:
          ((Listener)localObject).onStickersTab(bool);
          paramInt1 = paramInt2;
        }
      }
      else
      {
        label69:
        if (this.emojiTab.getTranslationX() == paramInt1) {
          break label185;
        }
        this.emojiTab.setTranslationX(paramInt1);
        this.stickersTab.setTranslationX(i + paramInt1);
        localObject = this.stickersTab;
        if (paramInt1 >= 0) {
          break label187;
        }
      }
    }
    label185:
    label187:
    for (paramInt1 = j;; paramInt1 = 4)
    {
      ((ScrollSlidingTabStrip)localObject).setVisibility(paramInt1);
      return;
      bool = false;
      break label58;
      if (paramInt1 == 6)
      {
        paramInt2 = -i;
        paramInt1 = paramInt2;
        if (this.listener == null) {
          break label69;
        }
        this.listener.onStickersTab(true);
        paramInt1 = paramInt2;
        break label69;
      }
      paramInt1 = paramInt2;
      if (this.listener == null) {
        break label69;
      }
      this.listener.onStickersTab(false);
      paramInt1 = paramInt2;
      break label69;
      break;
    }
  }
  
  private void postBackspaceRunnable(final int paramInt)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (!EmojiView.this.backspacePressed) {
          return;
        }
        if ((EmojiView.this.listener != null) && (EmojiView.this.listener.onBackspace())) {
          EmojiView.this.backspaceButton.performHapticFeedback(3);
        }
        EmojiView.access$5602(EmojiView.this, true);
        EmojiView.this.postBackspaceRunnable(Math.max(50, paramInt - 100));
      }
    }, paramInt);
  }
  
  private void reloadStickersAdapter()
  {
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
  
  private void saveEmojiColors()
  {
    SharedPreferences localSharedPreferences = getContext().getSharedPreferences("emoji", 0);
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = emojiColor.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (localStringBuilder.length() != 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append((String)localEntry.getKey());
      localStringBuilder.append("=");
      localStringBuilder.append((String)localEntry.getValue());
    }
    localSharedPreferences.edit().putString("color", localStringBuilder.toString()).commit();
  }
  
  private void saveNewPage()
  {
    int i;
    if (this.pager.getCurrentItem() == 6) {
      if ((this.gifsGridView != null) && (this.gifsGridView.getVisibility() == 0)) {
        i = 2;
      }
    }
    for (;;)
    {
      if (this.currentPage != i)
      {
        this.currentPage = i;
        getContext().getSharedPreferences("emoji", 0).edit().putInt("selected_page", i).commit();
      }
      return;
      i = 1;
      continue;
      i = 0;
    }
  }
  
  private void saveRecentEmoji()
  {
    SharedPreferences localSharedPreferences = getContext().getSharedPreferences("emoji", 0);
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = this.emojiUseHistory.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (localStringBuilder.length() != 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append((String)localEntry.getKey());
      localStringBuilder.append("=");
      localStringBuilder.append(localEntry.getValue());
    }
    localSharedPreferences.edit().putString("emojis2", localStringBuilder.toString()).commit();
  }
  
  private void showGifTab()
  {
    this.gifsGridView.setVisibility(0);
    this.stickersGridView.setVisibility(8);
    this.stickersEmptyView.setVisibility(8);
    this.trendingGridView.setVisibility(8);
    ScrollSlidingTabStrip localScrollSlidingTabStrip = this.stickersTab;
    int j = this.gifTabNum;
    if (this.recentTabBum > 0) {}
    for (int i = this.recentTabBum;; i = this.stickersTabOffset)
    {
      localScrollSlidingTabStrip.onPageScrolled(j + 1, i + 1);
      saveNewPage();
      return;
    }
  }
  
  private void showTrendingTab()
  {
    this.trendingGridView.setVisibility(0);
    this.stickersGridView.setVisibility(8);
    this.stickersEmptyView.setVisibility(8);
    this.gifsGridView.setVisibility(8);
    ScrollSlidingTabStrip localScrollSlidingTabStrip = this.stickersTab;
    int j = this.trendingTabNum;
    if (this.recentTabBum > 0) {}
    for (int i = this.recentTabBum;; i = this.stickersTabOffset)
    {
      localScrollSlidingTabStrip.onPageScrolled(j + 1, i + 1);
      saveNewPage();
      return;
    }
  }
  
  private void sortEmoji()
  {
    this.recentEmoji.clear();
    Iterator localIterator = this.emojiUseHistory.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      this.recentEmoji.add(localEntry.getKey());
    }
    Collections.sort(this.recentEmoji, new Comparator()
    {
      public int compare(String paramAnonymousString1, String paramAnonymousString2)
      {
        int i = 0;
        Integer localInteger2 = (Integer)EmojiView.this.emojiUseHistory.get(paramAnonymousString1);
        Integer localInteger1 = (Integer)EmojiView.this.emojiUseHistory.get(paramAnonymousString2);
        paramAnonymousString1 = localInteger2;
        if (localInteger2 == null) {
          paramAnonymousString1 = Integer.valueOf(0);
        }
        paramAnonymousString2 = localInteger1;
        if (localInteger1 == null) {
          paramAnonymousString2 = Integer.valueOf(0);
        }
        if (paramAnonymousString1.intValue() > paramAnonymousString2.intValue()) {
          i = -1;
        }
        while (paramAnonymousString1.intValue() >= paramAnonymousString2.intValue()) {
          return i;
        }
        return 1;
      }
    });
    while (this.recentEmoji.size() > 50) {
      this.recentEmoji.remove(this.recentEmoji.size() - 1);
    }
  }
  
  private void updateStickerTabs()
  {
    if (this.stickersTab == null) {
      return;
    }
    this.recentTabBum = -2;
    this.gifTabNum = -2;
    this.trendingTabNum = -2;
    this.stickersTabOffset = 0;
    int j = this.stickersTab.getCurrentPosition();
    this.stickersTab.removeTabs();
    this.stickersTab.addIconTab(2130837763);
    if ((this.showGifs) && (!this.recentGifs.isEmpty()))
    {
      this.stickersTab.addIconTab(2130837769);
      this.gifTabNum = this.stickersTabOffset;
      this.stickersTabOffset += 1;
    }
    ArrayList localArrayList = StickersQuery.getUnreadStickerSets();
    if ((this.trendingGridAdapter != null) && (this.trendingGridAdapter.getItemCount() != 0) && (!localArrayList.isEmpty()))
    {
      localObject = this.stickersTab.addIconTabWithCounter(2130837771);
      this.trendingTabNum = this.stickersTabOffset;
      this.stickersTabOffset += 1;
      ((TextView)localObject).setText(String.format("%d", new Object[] { Integer.valueOf(localArrayList.size()) }));
    }
    if (!this.recentStickers.isEmpty())
    {
      this.recentTabBum = this.stickersTabOffset;
      this.stickersTabOffset += 1;
      this.stickersTab.addIconTab(2130837761);
    }
    this.stickerSets.clear();
    Object localObject = StickersQuery.getStickerSets(0);
    int i = 0;
    if (i < ((ArrayList)localObject).size())
    {
      TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)((ArrayList)localObject).get(i);
      if ((localTL_messages_stickerSet.set.archived) || (localTL_messages_stickerSet.documents == null) || (localTL_messages_stickerSet.documents.isEmpty())) {}
      for (;;)
      {
        i += 1;
        break;
        this.stickerSets.add(localTL_messages_stickerSet);
      }
    }
    i = 0;
    while (i < this.stickerSets.size())
    {
      this.stickersTab.addStickerTab((TLRPC.Document)((TLRPC.TL_messages_stickerSet)this.stickerSets.get(i)).documents.get(0));
      i += 1;
    }
    if ((this.trendingGridAdapter != null) && (this.trendingGridAdapter.getItemCount() != 0) && (localArrayList.isEmpty()))
    {
      this.trendingTabNum = (this.stickersTabOffset + this.stickerSets.size());
      this.stickersTab.addIconTab(2130837771);
    }
    this.stickersTab.addIconTab(2130837770);
    this.stickersTab.updateTabStyles();
    if (j != 0) {
      this.stickersTab.onPageScrolled(j, j);
    }
    if ((this.switchToGifTab) && (this.gifTabNum >= 0) && (this.gifsGridView.getVisibility() != 0))
    {
      showGifTab();
      this.switchToGifTab = false;
    }
    checkPanels();
  }
  
  private void updateVisibleTrendingSets()
  {
    int i = this.trendingLayoutManager.findFirstVisibleItemPosition();
    if (i == -1) {}
    int j;
    do
    {
      return;
      j = this.trendingLayoutManager.findLastVisibleItemPosition();
    } while (j == -1);
    this.trendingGridAdapter.notifyItemRangeChanged(i, j - i + 1);
  }
  
  public void addRecentGif(TLRPC.Document paramDocument)
  {
    if (paramDocument == null) {}
    boolean bool;
    do
    {
      return;
      StickersQuery.addRecentGif(paramDocument, (int)(System.currentTimeMillis() / 1000L));
      bool = this.recentGifs.isEmpty();
      this.recentGifs = StickersQuery.getRecentGifs();
      if (this.gifsAdapter != null) {
        this.gifsAdapter.notifyDataSetChanged();
      }
    } while (!bool);
    updateStickerTabs();
  }
  
  public void addRecentSticker(TLRPC.Document paramDocument)
  {
    if (paramDocument == null) {}
    boolean bool;
    do
    {
      return;
      StickersQuery.addRecentSticker(0, paramDocument, (int)(System.currentTimeMillis() / 1000L));
      bool = this.recentStickers.isEmpty();
      this.recentStickers = StickersQuery.getRecentStickers(0);
      if (this.stickersGridAdapter != null) {
        this.stickersGridAdapter.notifyDataSetChanged();
      }
    } while (!bool);
    updateStickerTabs();
  }
  
  public void clearRecentEmoji()
  {
    getContext().getSharedPreferences("emoji", 0).edit().putBoolean("filled_default", true).commit();
    this.emojiUseHistory.clear();
    this.recentEmoji.clear();
    saveRecentEmoji();
    ((EmojiGridAdapter)this.adapters.get(0)).notifyDataSetChanged();
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.stickersDidLoaded) {
      if (((Integer)paramVarArgs[0]).intValue() == 0)
      {
        if (this.trendingGridAdapter != null)
        {
          if (!this.trendingLoaded) {
            break label50;
          }
          updateVisibleTrendingSets();
        }
        updateStickerTabs();
        reloadStickersAdapter();
        checkPanels();
      }
    }
    label50:
    label102:
    do
    {
      boolean bool;
      do
      {
        return;
        this.trendingGridAdapter.notifyDataSetChanged();
        break;
        if (paramInt != NotificationCenter.recentDocumentsDidLoaded) {
          break label102;
        }
        bool = ((Boolean)paramVarArgs[0]).booleanValue();
      } while ((!bool) && (((Integer)paramVarArgs[1]).intValue() != 0));
      checkDocuments(bool);
      return;
    } while (paramInt != NotificationCenter.featuredStickersDidLoaded);
    if (this.trendingGridAdapter != null)
    {
      if (!this.trendingLoaded) {
        break label167;
      }
      updateVisibleTrendingSets();
    }
    while (this.pagerSlidingTabStrip != null)
    {
      int i = this.pagerSlidingTabStrip.getChildCount();
      paramInt = 0;
      while (paramInt < i)
      {
        this.pagerSlidingTabStrip.getChildAt(paramInt).invalidate();
        paramInt += 1;
      }
      label167:
      this.trendingGridAdapter.notifyDataSetChanged();
    }
    updateStickerTabs();
  }
  
  public int getCurrentPage()
  {
    return this.currentPage;
  }
  
  public void invalidateViews()
  {
    int i = 0;
    while (i < this.emojiGrids.size())
    {
      ((GridView)this.emojiGrids.get(i)).invalidateViews();
      i += 1;
    }
  }
  
  public void loadRecents()
  {
    SharedPreferences localSharedPreferences = getContext().getSharedPreferences("emoji", 0);
    for (;;)
    {
      int j;
      try
      {
        this.emojiUseHistory.clear();
        Object localObject1;
        int i;
        Object localObject2;
        if (localSharedPreferences.contains("emojis"))
        {
          localObject1 = localSharedPreferences.getString("emojis", "");
          if ((localObject1 != null) && (((String)localObject1).length() > 0))
          {
            String[] arrayOfString1 = ((String)localObject1).split(",");
            int k = arrayOfString1.length;
            i = 0;
            if (i < k)
            {
              String[] arrayOfString2 = arrayOfString1[i].split("=");
              long l = Utilities.parseLong(arrayOfString2[0]).longValue();
              localObject1 = "";
              j = 0;
              localObject2 = localObject1;
              if (j < 4)
              {
                char c = (char)(int)l;
                localObject1 = String.valueOf(c) + (String)localObject1;
                l >>= 16;
                if (l != 0L) {
                  break label800;
                }
                localObject2 = localObject1;
              }
              if (((String)localObject2).length() <= 0) {
                break label793;
              }
              this.emojiUseHistory.put(localObject2, Utilities.parseInt(arrayOfString2[1]));
              break label793;
            }
          }
          localSharedPreferences.edit().remove("emojis").commit();
          saveRecentEmoji();
          if ((!this.emojiUseHistory.isEmpty()) || (localSharedPreferences.getBoolean("filled_default", false))) {
            continue;
          }
          localObject1 = new String[34];
          localObject1[0] = "😂";
          localObject1[1] = "😘";
          localObject1[2] = "❤";
          localObject1[3] = "😍";
          localObject1[4] = "😊";
          localObject1[5] = "😁";
          localObject1[6] = "👍";
          localObject1[7] = "☺";
          localObject1[8] = "😔";
          localObject1[9] = "😄";
          localObject1[10] = "😭";
          localObject1[11] = "💋";
          localObject1[12] = "😒";
          localObject1[13] = "😳";
          localObject1[14] = "😜";
          localObject1[15] = "🙈";
          localObject1[16] = "😉";
          localObject1[17] = "😃";
          localObject1[18] = "😢";
          localObject1[19] = "😝";
          localObject1[20] = "😱";
          localObject1[21] = "😡";
          localObject1[22] = "😏";
          localObject1[23] = "😞";
          localObject1[24] = "😅";
          localObject1[25] = "😚";
          localObject1[26] = "🙊";
          localObject1[27] = "😌";
          localObject1[28] = "😀";
          localObject1[29] = "😋";
          localObject1[30] = "😆";
          localObject1[31] = "👌";
          localObject1[32] = "😐";
          localObject1[33] = "😕";
          i = 0;
          if (i < localObject1.length)
          {
            this.emojiUseHistory.put(localObject1[i], Integer.valueOf(localObject1.length - i));
            i += 1;
            continue;
          }
        }
        else
        {
          localObject1 = localSharedPreferences.getString("emojis2", "");
          if ((localObject1 == null) || (((String)localObject1).length() <= 0)) {
            continue;
          }
          localObject1 = ((String)localObject1).split(",");
          j = localObject1.length;
          i = 0;
          if (i < j)
          {
            localObject2 = localObject1[i].split("=");
            this.emojiUseHistory.put(localObject2[0], Utilities.parseInt(localObject2[1]));
            i += 1;
            continue;
          }
          continue;
        }
        localSharedPreferences.edit().putBoolean("filled_default", true).commit();
        saveRecentEmoji();
        sortEmoji();
        ((EmojiGridAdapter)this.adapters.get(0)).notifyDataSetChanged();
        i += 1;
      }
      catch (Exception localException1)
      {
        try
        {
          localObject1 = localSharedPreferences.getString("color", "");
          if ((localObject1 != null) && (((String)localObject1).length() > 0))
          {
            localObject1 = ((String)localObject1).split(",");
            i = 0;
            if (i < localObject1.length)
            {
              localObject2 = localObject1[i].split("=");
              emojiColor.put(localObject2[0], localObject2[1]);
              i += 1;
              continue;
              localException1 = localException1;
              FileLog.e("tmessages", localException1);
              continue;
            }
          }
          return;
        }
        catch (Exception localException2)
        {
          FileLog.e("tmessages", localException2);
        }
      }
      label793:
      continue;
      label800:
      j += 1;
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.stickersGridAdapter != null)
    {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.featuredStickersDidLoaded);
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          EmojiView.this.updateStickerTabs();
          EmojiView.this.reloadStickersAdapter();
        }
      });
    }
  }
  
  public void onDestroy()
  {
    if (this.stickersGridAdapter != null)
    {
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stickersDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recentDocumentsDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if ((this.pickerViewPopup != null) && (this.pickerViewPopup.isShowing())) {
      this.pickerViewPopup.dismiss();
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.lastNotifyWidth != paramInt3 - paramInt1)
    {
      this.lastNotifyWidth = (paramInt3 - paramInt1);
      reloadStickersAdapter();
    }
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void onMeasure(int paramInt1, int paramInt2)
  {
    this.isLayout = true;
    if (AndroidUtilities.isInMultiwindow) {
      if (this.currentBackgroundType != 1)
      {
        if (Build.VERSION.SDK_INT >= 21)
        {
          setOutlineProvider((ViewOutlineProvider)this.outlineProvider);
          setClipToOutline(true);
          setElevation(AndroidUtilities.dp(2.0F));
        }
        setBackgroundResource(2130837986);
        this.emojiTab.setBackgroundDrawable(null);
        this.currentBackgroundType = 1;
      }
    }
    for (;;)
    {
      FrameLayout.LayoutParams localLayoutParams2 = (FrameLayout.LayoutParams)this.emojiTab.getLayoutParams();
      Object localObject = null;
      localLayoutParams2.width = View.MeasureSpec.getSize(paramInt1);
      if (this.stickersTab != null)
      {
        FrameLayout.LayoutParams localLayoutParams1 = (FrameLayout.LayoutParams)this.stickersTab.getLayoutParams();
        localObject = localLayoutParams1;
        if (localLayoutParams1 != null)
        {
          localLayoutParams1.width = localLayoutParams2.width;
          localObject = localLayoutParams1;
        }
      }
      if (localLayoutParams2.width != this.oldWidth)
      {
        if ((this.stickersTab != null) && (localObject != null))
        {
          onPageScrolled(this.pager.getCurrentItem(), localLayoutParams2.width - getPaddingLeft() - getPaddingRight(), 0);
          this.stickersTab.setLayoutParams((ViewGroup.LayoutParams)localObject);
        }
        this.emojiTab.setLayoutParams(localLayoutParams2);
        this.oldWidth = localLayoutParams2.width;
      }
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(localLayoutParams2.width, 1073741824), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt2), 1073741824));
      this.isLayout = false;
      return;
      if (this.currentBackgroundType != 0)
      {
        if (Build.VERSION.SDK_INT >= 21)
        {
          setOutlineProvider(null);
          setClipToOutline(false);
          setElevation(0.0F);
        }
        setBackgroundColor(-657673);
        this.emojiTab.setBackgroundColor(-657673);
        this.currentBackgroundType = 0;
      }
    }
  }
  
  public void onOpen(boolean paramBoolean)
  {
    boolean bool = true;
    if (this.stickersTab != null)
    {
      if ((this.currentPage != 0) && (!paramBoolean)) {
        break label55;
      }
      if (this.pager.getCurrentItem() == 6)
      {
        ViewPager localViewPager = this.pager;
        if (paramBoolean) {
          break label50;
        }
        paramBoolean = bool;
        localViewPager.setCurrentItem(0, paramBoolean);
      }
    }
    label50:
    label55:
    label151:
    do
    {
      do
      {
        do
        {
          return;
          paramBoolean = false;
          break;
          if (this.currentPage != 1) {
            break label151;
          }
          if (this.pager.getCurrentItem() != 6) {
            this.pager.setCurrentItem(6);
          }
        } while (this.stickersTab.getCurrentPosition() != this.gifTabNum + 1);
        if (this.recentTabBum >= 0)
        {
          this.stickersTab.selectTab(this.recentTabBum + 1);
          return;
        }
        if (this.gifTabNum >= 0)
        {
          this.stickersTab.selectTab(this.gifTabNum + 2);
          return;
        }
        this.stickersTab.selectTab(1);
        return;
      } while (this.currentPage != 2);
      if (this.pager.getCurrentItem() != 6) {
        this.pager.setCurrentItem(6);
      }
    } while (this.stickersTab.getCurrentPosition() == this.gifTabNum + 1);
    if ((this.gifTabNum >= 0) && (!this.recentGifs.isEmpty()))
    {
      this.stickersTab.selectTab(this.gifTabNum + 1);
      return;
    }
    this.switchToGifTab = true;
  }
  
  public void requestLayout()
  {
    if (this.isLayout) {
      return;
    }
    super.requestLayout();
  }
  
  public void setListener(Listener paramListener)
  {
    this.listener = paramListener;
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    Listener localListener;
    if (paramInt != 8)
    {
      sortEmoji();
      ((EmojiGridAdapter)this.adapters.get(0)).notifyDataSetChanged();
      if (this.stickersGridAdapter != null)
      {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentDocumentsDidLoaded);
        updateStickerTabs();
        reloadStickersAdapter();
        if ((this.gifsGridView != null) && (this.gifsGridView.getVisibility() == 0) && (this.listener != null))
        {
          localListener = this.listener;
          if ((this.pager == null) || (this.pager.getCurrentItem() < 6)) {
            break label163;
          }
        }
      }
    }
    label163:
    for (boolean bool = true;; bool = false)
    {
      localListener.onGifTab(bool);
      if (this.trendingGridAdapter != null)
      {
        this.trendingLoaded = false;
        this.trendingGridAdapter.notifyDataSetChanged();
      }
      checkDocuments(true);
      checkDocuments(false);
      StickersQuery.loadRecents(0, true, true);
      StickersQuery.loadRecents(0, false, true);
      return;
    }
  }
  
  public void switchToGifRecent()
  {
    if ((this.gifTabNum >= 0) && (!this.recentGifs.isEmpty())) {
      this.stickersTab.selectTab(this.gifTabNum + 1);
    }
    for (;;)
    {
      this.pager.setCurrentItem(6);
      return;
      this.switchToGifTab = true;
    }
  }
  
  private class EmojiColorPickerView
    extends View
  {
    private Drawable arrowDrawable = getResources().getDrawable(2130837992);
    private int arrowX;
    private Drawable backgroundDrawable = getResources().getDrawable(2130837991);
    private String currentEmoji;
    private RectF rect = new RectF();
    private Paint rectPaint = new Paint(1);
    private int selection;
    
    public EmojiColorPickerView(Context paramContext)
    {
      super();
    }
    
    public String getEmoji()
    {
      return this.currentEmoji;
    }
    
    public int getSelection()
    {
      return this.selection;
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      Object localObject = this.backgroundDrawable;
      int i = getMeasuredWidth();
      float f;
      int j;
      label67:
      int k;
      label95:
      label135:
      String str;
      if (AndroidUtilities.isTablet())
      {
        f = 60.0F;
        ((Drawable)localObject).setBounds(0, 0, i, AndroidUtilities.dp(f));
        this.backgroundDrawable.draw(paramCanvas);
        localObject = this.arrowDrawable;
        i = this.arrowX;
        j = AndroidUtilities.dp(9.0F);
        if (!AndroidUtilities.isTablet()) {
          break label373;
        }
        f = 55.5F;
        k = AndroidUtilities.dp(f);
        int m = this.arrowX;
        int n = AndroidUtilities.dp(9.0F);
        if (!AndroidUtilities.isTablet()) {
          break label379;
        }
        f = 55.5F;
        ((Drawable)localObject).setBounds(i - j, k, n + m, AndroidUtilities.dp(f + 8.0F));
        this.arrowDrawable.draw(paramCanvas);
        if (this.currentEmoji == null) {
          return;
        }
        i = 0;
        if (i >= 6) {
          return;
        }
        j = EmojiView.this.emojiSize * i + AndroidUtilities.dp(i * 4 + 5);
        k = AndroidUtilities.dp(9.0F);
        if (this.selection == i)
        {
          this.rect.set(j, k - (int)AndroidUtilities.dpf2(3.5F), EmojiView.this.emojiSize + j, EmojiView.this.emojiSize + k + AndroidUtilities.dp(3.0F));
          paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F), this.rectPaint);
        }
        str = this.currentEmoji;
        localObject = str;
        if (i != 0) {
          switch (i)
          {
          default: 
            localObject = "";
          }
        }
      }
      for (;;)
      {
        localObject = EmojiView.addColorToCode(str, (String)localObject);
        localObject = Emoji.getEmojiBigDrawable((String)localObject);
        if (localObject != null)
        {
          ((Drawable)localObject).setBounds(j, k, EmojiView.this.emojiSize + j, EmojiView.this.emojiSize + k);
          ((Drawable)localObject).draw(paramCanvas);
        }
        i += 1;
        break label135;
        f = 52.0F;
        break;
        label373:
        f = 47.5F;
        break label67;
        label379:
        f = 47.5F;
        break label95;
        localObject = "🏻";
        continue;
        localObject = "🏼";
        continue;
        localObject = "🏽";
        continue;
        localObject = "🏾";
        continue;
        localObject = "🏿";
      }
    }
    
    public void setEmoji(String paramString, int paramInt)
    {
      this.currentEmoji = paramString;
      this.arrowX = paramInt;
      this.rectPaint.setColor(788529152);
      invalidate();
    }
    
    public void setSelection(int paramInt)
    {
      if (this.selection == paramInt) {
        return;
      }
      this.selection = paramInt;
      invalidate();
    }
  }
  
  private class EmojiGridAdapter
    extends BaseAdapter
  {
    private int emojiPage;
    
    public EmojiGridAdapter(int paramInt)
    {
      this.emojiPage = paramInt;
    }
    
    public int getCount()
    {
      if (this.emojiPage == -1) {
        return EmojiView.this.recentEmoji.size();
      }
      return EmojiData.dataColored[this.emojiPage].length;
    }
    
    public Object getItem(int paramInt)
    {
      return null;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      paramView = (EmojiView.ImageViewEmoji)paramView;
      paramViewGroup = paramView;
      if (paramView == null) {
        paramViewGroup = new EmojiView.ImageViewEmoji(EmojiView.this, EmojiView.this.getContext());
      }
      Object localObject;
      if (this.emojiPage == -1)
      {
        localObject = (String)EmojiView.this.recentEmoji.get(paramInt);
        paramView = (View)localObject;
      }
      for (;;)
      {
        paramViewGroup.setImageDrawable(Emoji.getEmojiBigDrawable(paramView));
        paramViewGroup.setTag(localObject);
        return paramViewGroup;
        String str1 = EmojiData.dataColored[this.emojiPage][paramInt];
        String str2 = str1;
        String str3 = (String)EmojiView.emojiColor.get(str1);
        localObject = str1;
        paramView = str2;
        if (str3 != null)
        {
          paramView = EmojiView.addColorToCode(str2, str3);
          localObject = str1;
        }
      }
    }
    
    public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
    {
      if (paramDataSetObserver != null) {
        super.unregisterDataSetObserver(paramDataSetObserver);
      }
    }
  }
  
  private class EmojiPagesAdapter
    extends PagerAdapter
    implements PagerSlidingTabStrip.IconTabProvider
  {
    private EmojiPagesAdapter() {}
    
    public void customOnDraw(Canvas paramCanvas, int paramInt)
    {
      if ((paramInt == 6) && (!StickersQuery.getUnreadStickerSets().isEmpty()) && (EmojiView.this.dotDrawable != null))
      {
        paramInt = paramCanvas.getWidth() / 2 + AndroidUtilities.dp(4.0F);
        int i = paramCanvas.getHeight() / 2 - AndroidUtilities.dp(13.0F);
        EmojiView.this.dotDrawable.setBounds(paramInt, i, EmojiView.this.dotDrawable.getIntrinsicWidth() + paramInt, EmojiView.this.dotDrawable.getIntrinsicHeight() + i);
        EmojiView.this.dotDrawable.draw(paramCanvas);
      }
    }
    
    public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
    {
      if (paramInt == 6) {}
      for (paramObject = EmojiView.this.stickersWrap;; paramObject = (View)EmojiView.this.views.get(paramInt))
      {
        paramViewGroup.removeView((View)paramObject);
        return;
      }
    }
    
    public int getCount()
    {
      return EmojiView.this.views.size();
    }
    
    public int getPageIconResId(int paramInt)
    {
      return EmojiView.this.icons[paramInt];
    }
    
    public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
    {
      if (paramInt == 6) {}
      for (Object localObject = EmojiView.this.stickersWrap;; localObject = (View)EmojiView.this.views.get(paramInt))
      {
        paramViewGroup.addView((View)localObject);
        return localObject;
      }
    }
    
    public boolean isViewFromObject(View paramView, Object paramObject)
    {
      return paramView == paramObject;
    }
    
    public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
    {
      if (paramDataSetObserver != null) {
        super.unregisterDataSetObserver(paramDataSetObserver);
      }
    }
  }
  
  private class EmojiPopupWindow
    extends PopupWindow
  {
    private ViewTreeObserver.OnScrollChangedListener mSuperScrollListener;
    private ViewTreeObserver mViewTreeObserver;
    
    public EmojiPopupWindow()
    {
      init();
    }
    
    public EmojiPopupWindow(int paramInt1, int paramInt2)
    {
      super(paramInt2);
      init();
    }
    
    public EmojiPopupWindow(Context paramContext)
    {
      super();
      init();
    }
    
    public EmojiPopupWindow(View paramView)
    {
      super();
      init();
    }
    
    public EmojiPopupWindow(View paramView, int paramInt1, int paramInt2)
    {
      super(paramInt1, paramInt2);
      init();
    }
    
    public EmojiPopupWindow(View paramView, int paramInt1, int paramInt2, boolean paramBoolean)
    {
      super(paramInt1, paramInt2, paramBoolean);
      init();
    }
    
    private void init()
    {
      if (EmojiView.superListenerField != null) {}
      try
      {
        this.mSuperScrollListener = ((ViewTreeObserver.OnScrollChangedListener)EmojiView.superListenerField.get(this));
        EmojiView.superListenerField.set(this, EmojiView.NOP);
        return;
      }
      catch (Exception localException)
      {
        this.mSuperScrollListener = null;
      }
    }
    
    private void registerListener(View paramView)
    {
      if (this.mSuperScrollListener != null) {
        if (paramView.getWindowToken() == null) {
          break label73;
        }
      }
      label73:
      for (paramView = paramView.getViewTreeObserver();; paramView = null)
      {
        if (paramView != this.mViewTreeObserver)
        {
          if ((this.mViewTreeObserver != null) && (this.mViewTreeObserver.isAlive())) {
            this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
          }
          this.mViewTreeObserver = paramView;
          if (paramView != null) {
            paramView.addOnScrollChangedListener(this.mSuperScrollListener);
          }
        }
        return;
      }
    }
    
    private void unregisterListener()
    {
      if ((this.mSuperScrollListener != null) && (this.mViewTreeObserver != null))
      {
        if (this.mViewTreeObserver.isAlive()) {
          this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
        }
        this.mViewTreeObserver = null;
      }
    }
    
    public void dismiss()
    {
      setFocusable(false);
      try
      {
        super.dismiss();
        unregisterListener();
        return;
      }
      catch (Exception localException)
      {
        for (;;) {}
      }
    }
    
    public void showAsDropDown(View paramView, int paramInt1, int paramInt2)
    {
      try
      {
        super.showAsDropDown(paramView, paramInt1, paramInt2);
        registerListener(paramView);
        return;
      }
      catch (Exception paramView)
      {
        FileLog.e("tmessages", paramView);
      }
    }
    
    public void showAtLocation(View paramView, int paramInt1, int paramInt2, int paramInt3)
    {
      super.showAtLocation(paramView, paramInt1, paramInt2, paramInt3);
      unregisterListener();
    }
    
    public void update(View paramView, int paramInt1, int paramInt2)
    {
      super.update(paramView, paramInt1, paramInt2);
      registerListener(paramView);
    }
    
    public void update(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.update(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
      registerListener(paramView);
    }
  }
  
  private class GifsAdapter
    extends RecyclerView.Adapter
  {
    private Context mContext;
    
    public GifsAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getItemCount()
    {
      return EmojiView.this.recentGifs.size();
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      TLRPC.Document localDocument = (TLRPC.Document)EmojiView.this.recentGifs.get(paramInt);
      if (localDocument != null) {
        ((ContextLinkCell)paramViewHolder.itemView).setGif(localDocument, false);
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = new ContextLinkCell(this.mContext);
      return new EmojiView.Holder(EmojiView.this, paramViewGroup);
    }
  }
  
  private class Holder
    extends RecyclerView.ViewHolder
  {
    public Holder(View paramView)
    {
      super();
    }
  }
  
  private class ImageViewEmoji
    extends ImageView
  {
    private float lastX;
    private float lastY;
    private boolean touched;
    private float touchedX;
    private float touchedY;
    
    public ImageViewEmoji(Context paramContext)
    {
      super();
      setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          EmojiView.ImageViewEmoji.this.sendEmoji(null);
        }
      });
      setOnLongClickListener(new View.OnLongClickListener()
      {
        public boolean onLongClick(View paramAnonymousView)
        {
          int j = 0;
          String str = (String)paramAnonymousView.getTag();
          if (EmojiData.emojiColoredMap.containsKey(str))
          {
            EmojiView.ImageViewEmoji.access$102(EmojiView.ImageViewEmoji.this, true);
            EmojiView.ImageViewEmoji.access$202(EmojiView.ImageViewEmoji.this, EmojiView.ImageViewEmoji.this.lastX);
            EmojiView.ImageViewEmoji.access$402(EmojiView.ImageViewEmoji.this, EmojiView.ImageViewEmoji.this.lastY);
            Object localObject = (String)EmojiView.emojiColor.get(str);
            int i;
            label156:
            int k;
            if (localObject != null)
            {
              i = -1;
              switch (((String)localObject).hashCode())
              {
              default: 
                switch (i)
                {
                default: 
                  paramAnonymousView.getLocationOnScreen(EmojiView.this.location);
                  k = EmojiView.this.emojiSize;
                  int m = EmojiView.this.pickerView.getSelection();
                  int n = EmojiView.this.pickerView.getSelection();
                  if (AndroidUtilities.isTablet())
                  {
                    i = 5;
                    label220:
                    k = m * k + AndroidUtilities.dp(n * 4 - i);
                    if (EmojiView.this.location[0] - k >= AndroidUtilities.dp(5.0F)) {
                      break label607;
                    }
                    i = k + (EmojiView.this.location[0] - k - AndroidUtilities.dp(5.0F));
                    label286:
                    k = -i;
                    i = j;
                    if (paramAnonymousView.getTop() < 0) {
                      i = paramAnonymousView.getTop();
                    }
                    localObject = EmojiView.this.pickerView;
                    if (!AndroidUtilities.isTablet()) {
                      break label697;
                    }
                  }
                  break;
                }
                break;
              }
            }
            label607:
            label697:
            for (float f = 30.0F;; f = 22.0F)
            {
              ((EmojiView.EmojiColorPickerView)localObject).setEmoji(str, AndroidUtilities.dp(f) - k + (int)AndroidUtilities.dpf2(0.5F));
              EmojiView.this.pickerViewPopup.setFocusable(true);
              EmojiView.this.pickerViewPopup.showAsDropDown(paramAnonymousView, k, -paramAnonymousView.getMeasuredHeight() - EmojiView.this.popupHeight + (paramAnonymousView.getMeasuredHeight() - EmojiView.this.emojiSize) / 2 - i);
              paramAnonymousView.getParent().requestDisallowInterceptTouchEvent(true);
              return true;
              if (!((String)localObject).equals("🏻")) {
                break;
              }
              i = 0;
              break;
              if (!((String)localObject).equals("🏼")) {
                break;
              }
              i = 1;
              break;
              if (!((String)localObject).equals("🏽")) {
                break;
              }
              i = 2;
              break;
              if (!((String)localObject).equals("🏾")) {
                break;
              }
              i = 3;
              break;
              if (!((String)localObject).equals("🏿")) {
                break;
              }
              i = 4;
              break;
              EmojiView.this.pickerView.setSelection(1);
              break label156;
              EmojiView.this.pickerView.setSelection(2);
              break label156;
              EmojiView.this.pickerView.setSelection(3);
              break label156;
              EmojiView.this.pickerView.setSelection(4);
              break label156;
              EmojiView.this.pickerView.setSelection(5);
              break label156;
              EmojiView.this.pickerView.setSelection(0);
              break label156;
              i = 1;
              break label220;
              i = k;
              if (EmojiView.this.location[0] - k + EmojiView.this.popupWidth <= AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0F)) {
                break label286;
              }
              i = k + (EmojiView.this.location[0] - k + EmojiView.this.popupWidth - (AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0F)));
              break label286;
            }
          }
          if (EmojiView.this.pager.getCurrentItem() == 0) {
            EmojiView.this.listener.onClearEmojiRecent();
          }
          return false;
        }
      });
      setBackgroundResource(2130837796);
      setScaleType(ImageView.ScaleType.CENTER);
    }
    
    private void sendEmoji(String paramString)
    {
      Object localObject1;
      if (paramString != null)
      {
        localObject1 = paramString;
        if (paramString != null) {
          break label285;
        }
        paramString = (String)localObject1;
        if (EmojiView.this.pager.getCurrentItem() != 0)
        {
          localObject2 = (String)EmojiView.emojiColor.get(localObject1);
          paramString = (String)localObject1;
          if (localObject2 != null) {
            paramString = EmojiView.addColorToCode((String)localObject1, (String)localObject2);
          }
        }
        localObject2 = (Integer)EmojiView.this.emojiUseHistory.get(paramString);
        localObject1 = localObject2;
        if (localObject2 == null) {
          localObject1 = Integer.valueOf(0);
        }
        if ((((Integer)localObject1).intValue() == 0) && (EmojiView.this.emojiUseHistory.size() > 50))
        {
          i = EmojiView.this.recentEmoji.size() - 1;
          if (i >= 0)
          {
            localObject2 = (String)EmojiView.this.recentEmoji.get(i);
            EmojiView.this.emojiUseHistory.remove(localObject2);
            EmojiView.this.recentEmoji.remove(i);
            if (EmojiView.this.emojiUseHistory.size() > 50) {
              break label278;
            }
          }
        }
        EmojiView.this.emojiUseHistory.put(paramString, Integer.valueOf(((Integer)localObject1).intValue() + 1));
        if (EmojiView.this.pager.getCurrentItem() != 0) {
          EmojiView.this.sortEmoji();
        }
        EmojiView.this.saveRecentEmoji();
        ((EmojiView.EmojiGridAdapter)EmojiView.this.adapters.get(0)).notifyDataSetChanged();
        if (EmojiView.this.listener != null) {
          EmojiView.this.listener.onEmojiSelected(Emoji.fixEmoji(paramString));
        }
      }
      label278:
      label285:
      while (EmojiView.this.listener == null) {
        for (;;)
        {
          Object localObject2;
          int i;
          return;
          localObject1 = (String)getTag();
          break;
          i -= 1;
        }
      }
      EmojiView.this.listener.onEmojiSelected(Emoji.fixEmoji(paramString));
    }
    
    public void onMeasure(int paramInt1, int paramInt2)
    {
      setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), View.MeasureSpec.getSize(paramInt1));
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      if (this.touched)
      {
        if ((paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3)) {
          break label306;
        }
        if ((EmojiView.this.pickerViewPopup != null) && (EmojiView.this.pickerViewPopup.isShowing()))
        {
          EmojiView.this.pickerViewPopup.dismiss();
          localObject1 = null;
        }
        switch (EmojiView.this.pickerView.getSelection())
        {
        default: 
          localObject2 = (String)getTag();
          if (EmojiView.this.pager.getCurrentItem() != 0) {
            if (localObject1 != null)
            {
              EmojiView.emojiColor.put(localObject2, localObject1);
              localObject1 = EmojiView.addColorToCode((String)localObject2, (String)localObject1);
              setImageDrawable(Emoji.getEmojiBigDrawable((String)localObject1));
              sendEmoji(null);
              EmojiView.this.saveEmojiColors();
              this.touched = false;
              this.touchedX = -10000.0F;
              this.touchedY = -10000.0F;
            }
          }
          break;
        }
      }
      label306:
      while (paramMotionEvent.getAction() != 2)
      {
        Object localObject1;
        for (;;)
        {
          this.lastX = paramMotionEvent.getX();
          this.lastY = paramMotionEvent.getY();
          return super.onTouchEvent(paramMotionEvent);
          localObject1 = "🏻";
          continue;
          localObject1 = "🏼";
          continue;
          localObject1 = "🏽";
          continue;
          localObject1 = "🏾";
          continue;
          localObject1 = "🏿";
          continue;
          EmojiView.emojiColor.remove(localObject2);
          localObject1 = localObject2;
        }
        Object localObject2 = new StringBuilder().append((String)localObject2);
        if (localObject1 != null) {}
        for (;;)
        {
          sendEmoji((String)localObject1);
          break;
          localObject1 = "";
        }
      }
      int j = 0;
      int i = j;
      if (this.touchedX != -10000.0F)
      {
        if ((Math.abs(this.touchedX - paramMotionEvent.getX()) > AndroidUtilities.getPixelsInCM(0.2F, true)) || (Math.abs(this.touchedY - paramMotionEvent.getY()) > AndroidUtilities.getPixelsInCM(0.2F, false)))
        {
          this.touchedX = -10000.0F;
          this.touchedY = -10000.0F;
          i = j;
        }
      }
      else
      {
        label393:
        if (i != 0) {
          break label509;
        }
        getLocationOnScreen(EmojiView.this.location);
        float f1 = EmojiView.this.location[0];
        float f2 = paramMotionEvent.getX();
        EmojiView.this.pickerView.getLocationOnScreen(EmojiView.this.location);
        j = (int)((f1 + f2 - (EmojiView.this.location[0] + AndroidUtilities.dp(3.0F))) / (EmojiView.this.emojiSize + AndroidUtilities.dp(4.0F)));
        if (j >= 0) {
          break label511;
        }
        i = 0;
      }
      for (;;)
      {
        EmojiView.this.pickerView.setSelection(i);
        break;
        i = 1;
        break label393;
        label509:
        break;
        label511:
        i = j;
        if (j > 5) {
          i = 5;
        }
      }
    }
  }
  
  public static abstract interface Listener
  {
    public abstract boolean onBackspace();
    
    public abstract void onClearEmojiRecent();
    
    public abstract void onEmojiSelected(String paramString);
    
    public abstract void onGifSelected(TLRPC.Document paramDocument);
    
    public abstract void onGifTab(boolean paramBoolean);
    
    public abstract void onShowStickerSet(TLRPC.StickerSetCovered paramStickerSetCovered);
    
    public abstract void onStickerSelected(TLRPC.Document paramDocument);
    
    public abstract void onStickerSetAdd(TLRPC.StickerSetCovered paramStickerSetCovered);
    
    public abstract void onStickerSetRemove(TLRPC.StickerSetCovered paramStickerSetCovered);
    
    public abstract void onStickersSettingsClick();
    
    public abstract void onStickersTab(boolean paramBoolean);
  }
  
  private class StickersGridAdapter
    extends RecyclerView.Adapter
  {
    private HashMap<Integer, TLRPC.Document> cache = new HashMap();
    private Context context;
    private HashMap<TLRPC.TL_messages_stickerSet, Integer> packStartRow = new HashMap();
    private HashMap<Integer, TLRPC.TL_messages_stickerSet> rowStartPack = new HashMap();
    private int stickersPerRow;
    private int totalItems;
    
    public StickersGridAdapter(Context paramContext)
    {
      this.context = paramContext;
    }
    
    public Object getItem(int paramInt)
    {
      return this.cache.get(Integer.valueOf(paramInt));
    }
    
    public int getItemCount()
    {
      if (this.totalItems != 0) {
        return this.totalItems + 1;
      }
      return 0;
    }
    
    public int getItemViewType(int paramInt)
    {
      if (this.cache.get(Integer.valueOf(paramInt)) != null) {
        return 0;
      }
      return 1;
    }
    
    public int getPositionForPack(TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet)
    {
      return ((Integer)this.packStartRow.get(paramTL_messages_stickerSet)).intValue() * this.stickersPerRow;
    }
    
    public int getTabForPosition(int paramInt)
    {
      if (this.stickersPerRow == 0)
      {
        int j = EmojiView.this.getMeasuredWidth();
        int i = j;
        if (j == 0) {
          i = AndroidUtilities.displaySize.x;
        }
        this.stickersPerRow = (i / AndroidUtilities.dp(72.0F));
      }
      paramInt /= this.stickersPerRow;
      TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)this.rowStartPack.get(Integer.valueOf(paramInt));
      if (localTL_messages_stickerSet == null) {
        return EmojiView.this.recentTabBum;
      }
      return EmojiView.this.stickerSets.indexOf(localTL_messages_stickerSet) + EmojiView.this.stickersTabOffset;
    }
    
    public void notifyDataSetChanged()
    {
      int j = EmojiView.this.getMeasuredWidth();
      int i = j;
      if (j == 0) {
        i = AndroidUtilities.displaySize.x;
      }
      this.stickersPerRow = (i / AndroidUtilities.dp(72.0F));
      EmojiView.this.stickersLayoutManager.setSpanCount(this.stickersPerRow);
      this.rowStartPack.clear();
      this.packStartRow.clear();
      this.cache.clear();
      this.totalItems = 0;
      ArrayList localArrayList2 = EmojiView.this.stickerSets;
      i = -1;
      if (i < localArrayList2.size())
      {
        TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = null;
        int k = this.totalItems / this.stickersPerRow;
        ArrayList localArrayList1;
        if (i == -1)
        {
          localArrayList1 = EmojiView.this.recentStickers;
          label119:
          if (!localArrayList1.isEmpty()) {
            break label169;
          }
        }
        for (;;)
        {
          i += 1;
          break;
          localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)localArrayList2.get(i);
          localArrayList1 = localTL_messages_stickerSet.documents;
          this.packStartRow.put(localTL_messages_stickerSet, Integer.valueOf(k));
          break label119;
          label169:
          int m = (int)Math.ceil(localArrayList1.size() / this.stickersPerRow);
          j = 0;
          while (j < localArrayList1.size())
          {
            this.cache.put(Integer.valueOf(this.totalItems + j), localArrayList1.get(j));
            j += 1;
          }
          this.totalItems += this.stickersPerRow * m;
          j = 0;
          while (j < m)
          {
            this.rowStartPack.put(Integer.valueOf(k + j), localTL_messages_stickerSet);
            j += 1;
          }
        }
      }
      super.notifyDataSetChanged();
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      Object localObject;
      switch (paramViewHolder.getItemViewType())
      {
      default: 
        return;
      case 0: 
        localObject = (TLRPC.Document)this.cache.get(Integer.valueOf(paramInt));
        ((StickerEmojiCell)paramViewHolder.itemView).setSticker((TLRPC.Document)localObject, false);
        return;
      }
      if (paramInt == this.totalItems)
      {
        paramInt = (paramInt - 1) / this.stickersPerRow;
        localObject = (TLRPC.TL_messages_stickerSet)this.rowStartPack.get(Integer.valueOf(paramInt));
        if (localObject == null)
        {
          ((EmptyCell)paramViewHolder.itemView).setHeight(1);
          return;
        }
        paramInt = EmojiView.this.pager.getHeight() - (int)Math.ceil(((TLRPC.TL_messages_stickerSet)localObject).documents.size() / this.stickersPerRow) * AndroidUtilities.dp(82.0F);
        paramViewHolder = (EmptyCell)paramViewHolder.itemView;
        if (paramInt > 0) {}
        for (;;)
        {
          paramViewHolder.setHeight(paramInt);
          return;
          paramInt = 1;
        }
      }
      ((EmptyCell)paramViewHolder.itemView).setHeight(AndroidUtilities.dp(82.0F));
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = null;
      switch (paramInt)
      {
      }
      for (;;)
      {
        return new EmojiView.Holder(EmojiView.this, paramViewGroup);
        paramViewGroup = new StickerEmojiCell(this.context)
        {
          public void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
          {
            super.onMeasure(paramAnonymousInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0F), 1073741824));
          }
        };
        continue;
        paramViewGroup = new EmptyCell(this.context);
      }
    }
  }
  
  private class TrendingGridAdapter
    extends RecyclerView.Adapter
  {
    private HashMap<Integer, Object> cache = new HashMap();
    private Context context;
    private HashMap<Integer, TLRPC.StickerSetCovered> positionsToSets = new HashMap();
    private ArrayList<TLRPC.StickerSetCovered> sets = new ArrayList();
    private int stickersPerRow;
    private int totalItems;
    
    public TrendingGridAdapter(Context paramContext)
    {
      this.context = paramContext;
    }
    
    public Object getItem(int paramInt)
    {
      return this.cache.get(Integer.valueOf(paramInt));
    }
    
    public int getItemCount()
    {
      return this.totalItems;
    }
    
    public int getItemViewType(int paramInt)
    {
      Object localObject = this.cache.get(Integer.valueOf(paramInt));
      if (localObject != null)
      {
        if ((localObject instanceof TLRPC.Document)) {
          return 0;
        }
        return 2;
      }
      return 1;
    }
    
    public void notifyDataSetChanged()
    {
      if (EmojiView.this.trendingLoaded) {
        return;
      }
      int j = EmojiView.this.getMeasuredWidth();
      int i = j;
      if (j == 0) {
        i = AndroidUtilities.displaySize.x;
      }
      this.stickersPerRow = (i / AndroidUtilities.dp(72.0F));
      EmojiView.this.trendingLayoutManager.setSpanCount(this.stickersPerRow);
      this.cache.clear();
      this.positionsToSets.clear();
      this.sets.clear();
      this.totalItems = 0;
      j = 0;
      ArrayList localArrayList = StickersQuery.getFeaturedStickerSets();
      i = 0;
      if (i < localArrayList.size())
      {
        TLRPC.StickerSetCovered localStickerSetCovered = (TLRPC.StickerSetCovered)localArrayList.get(i);
        int k = j;
        if (!StickersQuery.isStickerPackInstalled(localStickerSetCovered.set.id)) {
          if ((!localStickerSetCovered.covers.isEmpty()) || (localStickerSetCovered.cover != null)) {
            break label158;
          }
        }
        for (k = j;; k = j + 1)
        {
          i += 1;
          j = k;
          break;
          label158:
          this.sets.add(localStickerSetCovered);
          this.positionsToSets.put(Integer.valueOf(this.totalItems), localStickerSetCovered);
          HashMap localHashMap = this.cache;
          k = this.totalItems;
          this.totalItems = (k + 1);
          localHashMap.put(Integer.valueOf(k), Integer.valueOf(j));
          k = this.totalItems / this.stickersPerRow;
          if (!localStickerSetCovered.covers.isEmpty())
          {
            int n = (int)Math.ceil(localStickerSetCovered.covers.size() / this.stickersPerRow);
            m = 0;
            for (;;)
            {
              k = n;
              if (m >= localStickerSetCovered.covers.size()) {
                break;
              }
              this.cache.put(Integer.valueOf(this.totalItems + m), localStickerSetCovered.covers.get(m));
              m += 1;
            }
          }
          k = 1;
          this.cache.put(Integer.valueOf(this.totalItems), localStickerSetCovered.cover);
          int m = 0;
          while (m < this.stickersPerRow * k)
          {
            this.positionsToSets.put(Integer.valueOf(this.totalItems + m), localStickerSetCovered);
            m += 1;
          }
          this.totalItems += this.stickersPerRow * k;
        }
      }
      if (this.totalItems != 0) {
        EmojiView.access$6202(EmojiView.this, true);
      }
      super.notifyDataSetChanged();
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool3 = false;
      switch (paramViewHolder.getItemViewType())
      {
      default: 
        return;
      case 0: 
        localObject = (TLRPC.Document)this.cache.get(Integer.valueOf(paramInt));
        ((StickerEmojiCell)paramViewHolder.itemView).setSticker((TLRPC.Document)localObject, false);
        return;
      case 1: 
        ((EmptyCell)paramViewHolder.itemView).setHeight(AndroidUtilities.dp(82.0F));
        return;
      }
      ArrayList localArrayList = StickersQuery.getUnreadStickerSets();
      Object localObject = (TLRPC.StickerSetCovered)this.sets.get(((Integer)this.cache.get(Integer.valueOf(paramInt))).intValue());
      boolean bool1;
      boolean bool4;
      boolean bool5;
      boolean bool2;
      if ((localArrayList != null) && (localArrayList.contains(Long.valueOf(((TLRPC.StickerSetCovered)localObject).set.id))))
      {
        bool1 = true;
        paramViewHolder = (FeaturedStickerSetInfoCell)paramViewHolder.itemView;
        paramViewHolder.setStickerSet((TLRPC.StickerSetCovered)localObject, bool1);
        if (bool1) {
          StickersQuery.markFaturedStickersByIdAsRead(((TLRPC.StickerSetCovered)localObject).set.id);
        }
        bool4 = EmojiView.this.installingStickerSets.containsKey(Long.valueOf(((TLRPC.StickerSetCovered)localObject).set.id));
        bool5 = EmojiView.this.removingStickerSets.containsKey(Long.valueOf(((TLRPC.StickerSetCovered)localObject).set.id));
        if (!bool4)
        {
          bool2 = bool4;
          bool1 = bool5;
          if (!bool5) {}
        }
        else
        {
          if ((!bool4) || (!paramViewHolder.isInstalled())) {
            break label300;
          }
          EmojiView.this.installingStickerSets.remove(Long.valueOf(((TLRPC.StickerSetCovered)localObject).set.id));
          bool2 = false;
          bool1 = bool5;
        }
      }
      for (;;)
      {
        if (!bool2)
        {
          bool2 = bool3;
          if (!bool1) {}
        }
        else
        {
          bool2 = true;
        }
        paramViewHolder.setDrawProgress(bool2);
        return;
        bool1 = false;
        break;
        label300:
        bool2 = bool4;
        bool1 = bool5;
        if (bool5)
        {
          bool2 = bool4;
          bool1 = bool5;
          if (!paramViewHolder.isInstalled())
          {
            EmojiView.this.removingStickerSets.remove(Long.valueOf(((TLRPC.StickerSetCovered)localObject).set.id));
            bool1 = false;
            bool2 = bool4;
          }
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = null;
      switch (paramInt)
      {
      }
      for (;;)
      {
        return new EmojiView.Holder(EmojiView.this, paramViewGroup);
        paramViewGroup = new StickerEmojiCell(this.context)
        {
          public void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
          {
            super.onMeasure(paramAnonymousInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0F), 1073741824));
          }
        };
        continue;
        paramViewGroup = new EmptyCell(this.context);
        continue;
        paramViewGroup = new FeaturedStickerSetInfoCell(this.context, 17);
        ((FeaturedStickerSetInfoCell)paramViewGroup).setAddOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = (FeaturedStickerSetInfoCell)paramAnonymousView.getParent();
            TLRPC.StickerSetCovered localStickerSetCovered = paramAnonymousView.getStickerSet();
            if ((EmojiView.this.installingStickerSets.containsKey(Long.valueOf(localStickerSetCovered.set.id))) || (EmojiView.this.removingStickerSets.containsKey(Long.valueOf(localStickerSetCovered.set.id)))) {
              return;
            }
            if (paramAnonymousView.isInstalled())
            {
              EmojiView.this.removingStickerSets.put(Long.valueOf(localStickerSetCovered.set.id), localStickerSetCovered);
              EmojiView.this.listener.onStickerSetRemove(paramAnonymousView.getStickerSet());
            }
            for (;;)
            {
              paramAnonymousView.setDrawProgress(true);
              return;
              EmojiView.this.installingStickerSets.put(Long.valueOf(localStickerSetCovered.set.id), localStickerSetCovered);
              EmojiView.this.listener.onStickerSetAdd(paramAnonymousView.getStickerSet());
            }
          }
        });
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/EmojiView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */