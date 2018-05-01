package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.SharingLiveLocationCell;
import org.telegram.ui.StickerPreviewViewer;

public class SharingLocationsAlert
  extends BottomSheet
  implements NotificationCenter.NotificationCenterDelegate
{
  private ListAdapter adapter;
  private SharingLocationsAlertDelegate delegate;
  private boolean ignoreLayout;
  private RecyclerListView listView;
  private int reqId;
  private int scrollOffsetY;
  private Drawable shadowDrawable;
  private TextView textView;
  private Pattern urlPattern;
  
  public SharingLocationsAlert(Context paramContext, SharingLocationsAlertDelegate paramSharingLocationsAlertDelegate)
  {
    super(paramContext, false);
    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
    this.delegate = paramSharingLocationsAlertDelegate;
    this.shadowDrawable = paramContext.getResources().getDrawable(NUM).mutate();
    this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
    this.containerView = new FrameLayout(paramContext)
    {
      protected void onDraw(Canvas paramAnonymousCanvas)
      {
        SharingLocationsAlert.this.shadowDrawable.setBounds(0, SharingLocationsAlert.this.scrollOffsetY - SharingLocationsAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
        SharingLocationsAlert.this.shadowDrawable.draw(paramAnonymousCanvas);
      }
      
      public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if ((paramAnonymousMotionEvent.getAction() == 0) && (SharingLocationsAlert.this.scrollOffsetY != 0) && (paramAnonymousMotionEvent.getY() < SharingLocationsAlert.this.scrollOffsetY)) {
          SharingLocationsAlert.this.dismiss();
        }
        for (boolean bool = true;; bool = super.onInterceptTouchEvent(paramAnonymousMotionEvent)) {
          return bool;
        }
      }
      
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        super.onLayout(paramAnonymousBoolean, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
        SharingLocationsAlert.this.updateLayout();
      }
      
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        paramAnonymousInt2 = View.MeasureSpec.getSize(paramAnonymousInt2);
        int i = paramAnonymousInt2;
        if (Build.VERSION.SDK_INT >= 21) {
          i = paramAnonymousInt2 - AndroidUtilities.statusBarHeight;
        }
        getMeasuredWidth();
        int j = AndroidUtilities.dp(56.0F) + AndroidUtilities.dp(56.0F) + 1 + LocationController.getLocationsCount() * AndroidUtilities.dp(54.0F);
        if (j < i / 5 * 3) {
          paramAnonymousInt2 = AndroidUtilities.dp(8.0F);
        }
        for (;;)
        {
          if (SharingLocationsAlert.this.listView.getPaddingTop() != paramAnonymousInt2)
          {
            SharingLocationsAlert.access$202(SharingLocationsAlert.this, true);
            SharingLocationsAlert.this.listView.setPadding(0, paramAnonymousInt2, 0, AndroidUtilities.dp(8.0F));
            SharingLocationsAlert.access$202(SharingLocationsAlert.this, false);
          }
          super.onMeasure(paramAnonymousInt1, View.MeasureSpec.makeMeasureSpec(Math.min(j, i), NUM));
          return;
          int k = i / 5 * 2;
          paramAnonymousInt2 = k;
          if (j < i) {
            paramAnonymousInt2 = k - (i - j);
          }
        }
      }
      
      public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if ((!SharingLocationsAlert.this.isDismissed()) && (super.onTouchEvent(paramAnonymousMotionEvent))) {}
        for (boolean bool = true;; bool = false) {
          return bool;
        }
      }
      
      public void requestLayout()
      {
        if (SharingLocationsAlert.this.ignoreLayout) {}
        for (;;)
        {
          return;
          super.requestLayout();
        }
      }
    };
    this.containerView.setWillNotDraw(false);
    this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
    this.listView = new RecyclerListView(paramContext)
    {
      public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        boolean bool1 = false;
        boolean bool2 = StickerPreviewViewer.getInstance().onInterceptTouchEvent(paramAnonymousMotionEvent, SharingLocationsAlert.this.listView, 0, null);
        if ((super.onInterceptTouchEvent(paramAnonymousMotionEvent)) || (bool2)) {
          bool1 = true;
        }
        return bool1;
      }
      
      public void requestLayout()
      {
        if (SharingLocationsAlert.this.ignoreLayout) {}
        for (;;)
        {
          return;
          super.requestLayout();
        }
      }
    };
    this.listView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
    RecyclerListView localRecyclerListView = this.listView;
    paramSharingLocationsAlertDelegate = new ListAdapter(paramContext);
    this.adapter = paramSharingLocationsAlertDelegate;
    localRecyclerListView.setAdapter(paramSharingLocationsAlertDelegate);
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setClipToPadding(false);
    this.listView.setEnabled(true);
    this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
    this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        SharingLocationsAlert.this.updateLayout();
      }
    });
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        
        if ((paramAnonymousInt < 0) || (paramAnonymousInt >= LocationController.getLocationsCount())) {}
        for (;;)
        {
          return;
          SharingLocationsAlert.this.delegate.didSelectLocation(SharingLocationsAlert.this.getLocation(paramAnonymousInt));
          SharingLocationsAlert.this.dismiss();
        }
      }
    });
    this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, 48.0F));
    paramSharingLocationsAlertDelegate = new View(paramContext);
    paramSharingLocationsAlertDelegate.setBackgroundResource(NUM);
    this.containerView.addView(paramSharingLocationsAlertDelegate, LayoutHelper.createFrame(-1, 3.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
    paramContext = new PickerBottomLayout(paramContext, false);
    paramContext.setBackgroundColor(Theme.getColor("dialogBackground"));
    this.containerView.addView(paramContext, LayoutHelper.createFrame(-1, 48, 83));
    paramContext.cancelButton.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
    paramContext.cancelButton.setTextColor(Theme.getColor("dialogTextRed"));
    paramContext.cancelButton.setText(LocaleController.getString("StopAllLocationSharings", NUM));
    paramContext.cancelButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        for (int i = 0; i < 3; i++) {
          LocationController.getInstance(i).removeAllLocationSharings();
        }
        SharingLocationsAlert.this.dismiss();
      }
    });
    paramContext.doneButtonTextView.setTextColor(Theme.getColor("dialogTextBlue2"));
    paramContext.doneButtonTextView.setText(LocaleController.getString("Close", NUM).toUpperCase());
    paramContext.doneButton.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
    paramContext.doneButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        SharingLocationsAlert.this.dismiss();
      }
    });
    paramContext.doneButtonBadgeTextView.setVisibility(8);
    this.adapter.notifyDataSetChanged();
  }
  
  private LocationController.SharingLocationInfo getLocation(int paramInt)
  {
    for (int i = 0;; i++)
    {
      if (i >= 3) {
        break label47;
      }
      localObject = LocationController.getInstance(i).sharingLocationsUI;
      if (paramInt < ((ArrayList)localObject).size()) {
        break;
      }
      paramInt -= ((ArrayList)localObject).size();
    }
    label47:
    for (Object localObject = (LocationController.SharingLocationInfo)((ArrayList)localObject).get(paramInt);; localObject = null) {
      return (LocationController.SharingLocationInfo)localObject;
    }
  }
  
  @SuppressLint({"NewApi"})
  private void updateLayout()
  {
    int i = 0;
    Object localObject;
    int j;
    if (this.listView.getChildCount() <= 0)
    {
      localObject = this.listView;
      j = this.listView.getPaddingTop();
      this.scrollOffsetY = j;
      ((RecyclerListView)localObject).setTopGlowOffset(j);
      this.containerView.invalidate();
    }
    for (;;)
    {
      return;
      View localView = this.listView.getChildAt(0);
      localObject = (RecyclerListView.Holder)this.listView.findContainingViewHolder(localView);
      int k = localView.getTop() - AndroidUtilities.dp(8.0F);
      j = i;
      if (k > 0)
      {
        j = i;
        if (localObject != null)
        {
          j = i;
          if (((RecyclerListView.Holder)localObject).getAdapterPosition() == 0) {
            j = k;
          }
        }
      }
      if (this.scrollOffsetY != j)
      {
        localObject = this.listView;
        this.scrollOffsetY = j;
        ((RecyclerListView)localObject).setTopGlowOffset(j);
        this.containerView.invalidate();
      }
    }
  }
  
  protected boolean canDismissWithSwipe()
  {
    return false;
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.liveLocationsChanged)
    {
      if (LocationController.getLocationsCount() != 0) {
        break label18;
      }
      dismiss();
    }
    for (;;)
    {
      return;
      label18:
      this.adapter.notifyDataSetChanged();
    }
  }
  
  public void dismiss()
  {
    super.dismiss();
    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
  }
  
  private class ListAdapter
    extends RecyclerListView.SelectionAdapter
  {
    private Context context;
    
    public ListAdapter(Context paramContext)
    {
      this.context = paramContext;
    }
    
    public int getItemCount()
    {
      return LocationController.getLocationsCount() + 1;
    }
    
    public int getItemViewType(int paramInt)
    {
      if (paramInt == 0) {}
      for (paramInt = 1;; paramInt = 0) {
        return paramInt;
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      if (paramViewHolder.getItemViewType() == 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      }
      for (;;)
      {
        return;
        ((SharingLiveLocationCell)paramViewHolder.itemView).setDialog(SharingLocationsAlert.this.getLocation(paramInt - 1));
        continue;
        if (SharingLocationsAlert.this.textView != null) {
          SharingLocationsAlert.this.textView.setText(LocaleController.formatString("SharingLiveLocationTitle", NUM, new Object[] { LocaleController.formatPluralString("Chats", LocationController.getLocationsCount()) }));
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        paramViewGroup = new FrameLayout(this.context)
        {
          protected void onDraw(Canvas paramAnonymousCanvas)
          {
            paramAnonymousCanvas.drawLine(0.0F, AndroidUtilities.dp(40.0F), getMeasuredWidth(), AndroidUtilities.dp(40.0F), Theme.dividerPaint);
          }
          
          protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
          {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramAnonymousInt1), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0F) + 1, NUM));
          }
        };
        paramViewGroup.setWillNotDraw(false);
        SharingLocationsAlert.access$802(SharingLocationsAlert.this, new TextView(this.context));
        SharingLocationsAlert.this.textView.setTextColor(Theme.getColor("dialogIcon"));
        SharingLocationsAlert.this.textView.setTextSize(1, 14.0F);
        SharingLocationsAlert.this.textView.setGravity(17);
        SharingLocationsAlert.this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0F));
        paramViewGroup.addView(SharingLocationsAlert.this.textView, LayoutHelper.createFrame(-1, 40.0F));
      }
      for (;;)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new SharingLiveLocationCell(this.context, false);
      }
    }
  }
  
  public static abstract interface SharingLocationsAlertDelegate
  {
    public abstract void didSelectLocation(LocationController.SharingLocationInfo paramSharingLocationInfo);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/SharingLocationsAlert.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */