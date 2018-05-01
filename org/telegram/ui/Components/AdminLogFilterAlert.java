package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventsFilter;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.CheckBoxUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.StickerPreviewViewer;

public class AdminLogFilterAlert
  extends BottomSheet
{
  private ListAdapter adapter;
  private int adminsRow;
  private int allAdminsRow;
  private ArrayList<TLRPC.ChannelParticipant> currentAdmins;
  private TLRPC.TL_channelAdminLogEventsFilter currentFilter;
  private AdminLogFilterAlertDelegate delegate;
  private int deleteRow;
  private int editRow;
  private boolean ignoreLayout;
  private int infoRow;
  private boolean isMegagroup;
  private int leavingRow;
  private RecyclerListView listView;
  private int membersRow;
  private FrameLayout pickerBottomLayout;
  private int pinnedRow;
  private int reqId;
  private int restrictionsRow;
  private BottomSheet.BottomSheetCell saveButton;
  private int scrollOffsetY;
  private SparseArray<TLRPC.User> selectedAdmins;
  private Drawable shadowDrawable;
  private Pattern urlPattern;
  
  public AdminLogFilterAlert(Context paramContext, TLRPC.TL_channelAdminLogEventsFilter paramTL_channelAdminLogEventsFilter, SparseArray<TLRPC.User> paramSparseArray, boolean paramBoolean)
  {
    super(paramContext, false);
    if (paramTL_channelAdminLogEventsFilter != null)
    {
      this.currentFilter = new TLRPC.TL_channelAdminLogEventsFilter();
      this.currentFilter.join = paramTL_channelAdminLogEventsFilter.join;
      this.currentFilter.leave = paramTL_channelAdminLogEventsFilter.leave;
      this.currentFilter.invite = paramTL_channelAdminLogEventsFilter.invite;
      this.currentFilter.ban = paramTL_channelAdminLogEventsFilter.ban;
      this.currentFilter.unban = paramTL_channelAdminLogEventsFilter.unban;
      this.currentFilter.kick = paramTL_channelAdminLogEventsFilter.kick;
      this.currentFilter.unkick = paramTL_channelAdminLogEventsFilter.unkick;
      this.currentFilter.promote = paramTL_channelAdminLogEventsFilter.promote;
      this.currentFilter.demote = paramTL_channelAdminLogEventsFilter.demote;
      this.currentFilter.info = paramTL_channelAdminLogEventsFilter.info;
      this.currentFilter.settings = paramTL_channelAdminLogEventsFilter.settings;
      this.currentFilter.pinned = paramTL_channelAdminLogEventsFilter.pinned;
      this.currentFilter.edit = paramTL_channelAdminLogEventsFilter.edit;
      this.currentFilter.delete = paramTL_channelAdminLogEventsFilter.delete;
    }
    if (paramSparseArray != null) {
      this.selectedAdmins = paramSparseArray.clone();
    }
    this.isMegagroup = paramBoolean;
    int i = 1;
    if (this.isMegagroup)
    {
      this.restrictionsRow = 1;
      i = 1 + 1;
      int j = i + 1;
      this.adminsRow = i;
      i = j + 1;
      this.membersRow = j;
      int k = i + 1;
      this.infoRow = i;
      j = k + 1;
      this.deleteRow = k;
      i = j + 1;
      this.editRow = j;
      if (!this.isMegagroup) {
        break label680;
      }
      j = i + 1;
      this.pinnedRow = i;
      i = j;
    }
    for (;;)
    {
      this.leavingRow = i;
      this.allAdminsRow = (i + 2);
      this.shadowDrawable = paramContext.getResources().getDrawable(NUM).mutate();
      this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
      this.containerView = new FrameLayout(paramContext)
      {
        protected void onDraw(Canvas paramAnonymousCanvas)
        {
          AdminLogFilterAlert.this.shadowDrawable.setBounds(0, AdminLogFilterAlert.this.scrollOffsetY - AdminLogFilterAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
          AdminLogFilterAlert.this.shadowDrawable.draw(paramAnonymousCanvas);
        }
        
        public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if ((paramAnonymousMotionEvent.getAction() == 0) && (AdminLogFilterAlert.this.scrollOffsetY != 0) && (paramAnonymousMotionEvent.getY() < AdminLogFilterAlert.this.scrollOffsetY)) {
            AdminLogFilterAlert.this.dismiss();
          }
          for (boolean bool = true;; bool = super.onInterceptTouchEvent(paramAnonymousMotionEvent)) {
            return bool;
          }
        }
        
        protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
        {
          super.onLayout(paramAnonymousBoolean, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
          AdminLogFilterAlert.this.updateLayout();
        }
        
        protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          paramAnonymousInt2 = View.MeasureSpec.getSize(paramAnonymousInt2);
          int i = paramAnonymousInt2;
          if (Build.VERSION.SDK_INT >= 21) {
            i = paramAnonymousInt2 - AndroidUtilities.statusBarHeight;
          }
          getMeasuredWidth();
          int j = AndroidUtilities.dp(48.0F);
          int k;
          if (AdminLogFilterAlert.this.isMegagroup)
          {
            paramAnonymousInt2 = 9;
            paramAnonymousInt2 = paramAnonymousInt2 * AndroidUtilities.dp(48.0F) + j + AdminLogFilterAlert.backgroundPaddingTop;
            k = paramAnonymousInt2;
            if (AdminLogFilterAlert.this.currentAdmins != null) {
              k = paramAnonymousInt2 + ((AdminLogFilterAlert.this.currentAdmins.size() + 1) * AndroidUtilities.dp(48.0F) + AndroidUtilities.dp(20.0F));
            }
            if (k >= i / 5 * 3.2F) {
              break label227;
            }
          }
          label227:
          for (j = 0;; j = i / 5 * 2)
          {
            paramAnonymousInt2 = j;
            if (j != 0)
            {
              paramAnonymousInt2 = j;
              if (k < i) {
                paramAnonymousInt2 = j - (i - k);
              }
            }
            j = paramAnonymousInt2;
            if (paramAnonymousInt2 == 0) {
              j = AdminLogFilterAlert.backgroundPaddingTop;
            }
            if (AdminLogFilterAlert.this.listView.getPaddingTop() != j)
            {
              AdminLogFilterAlert.access$602(AdminLogFilterAlert.this, true);
              AdminLogFilterAlert.this.listView.setPadding(0, j, 0, 0);
              AdminLogFilterAlert.access$602(AdminLogFilterAlert.this, false);
            }
            super.onMeasure(paramAnonymousInt1, View.MeasureSpec.makeMeasureSpec(Math.min(k, i), NUM));
            return;
            paramAnonymousInt2 = 7;
            break;
          }
        }
        
        public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if ((!AdminLogFilterAlert.this.isDismissed()) && (super.onTouchEvent(paramAnonymousMotionEvent))) {}
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
        
        public void requestLayout()
        {
          if (AdminLogFilterAlert.this.ignoreLayout) {}
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
          boolean bool2 = StickerPreviewViewer.getInstance().onInterceptTouchEvent(paramAnonymousMotionEvent, AdminLogFilterAlert.this.listView, 0, null);
          if ((super.onInterceptTouchEvent(paramAnonymousMotionEvent)) || (bool2)) {
            bool1 = true;
          }
          return bool1;
        }
        
        public void requestLayout()
        {
          if (AdminLogFilterAlert.this.ignoreLayout) {}
          for (;;)
          {
            return;
            super.requestLayout();
          }
        }
      };
      this.listView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
      paramSparseArray = this.listView;
      paramTL_channelAdminLogEventsFilter = new ListAdapter(paramContext);
      this.adapter = paramTL_channelAdminLogEventsFilter;
      paramSparseArray.setAdapter(paramTL_channelAdminLogEventsFilter);
      this.listView.setVerticalScrollBarEnabled(false);
      this.listView.setClipToPadding(false);
      this.listView.setEnabled(true);
      this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          AdminLogFilterAlert.this.updateLayout();
        }
      });
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if ((paramAnonymousView instanceof CheckBoxCell))
          {
            paramAnonymousView = (CheckBoxCell)paramAnonymousView;
            bool1 = paramAnonymousView.isChecked();
            if (!bool1)
            {
              bool2 = true;
              paramAnonymousView.setChecked(bool2, true);
              if (paramAnonymousInt != 0) {
                break label386;
              }
              if (!bool1) {
                break label368;
              }
              AdminLogFilterAlert.access$1002(AdminLogFilterAlert.this, new TLRPC.TL_channelAdminLogEventsFilter());
              localTL_channelAdminLogEventsFilter1 = AdminLogFilterAlert.this.currentFilter;
              localTL_channelAdminLogEventsFilter2 = AdminLogFilterAlert.this.currentFilter;
              localTL_channelAdminLogEventsFilter3 = AdminLogFilterAlert.this.currentFilter;
              localTL_channelAdminLogEventsFilter4 = AdminLogFilterAlert.this.currentFilter;
              localObject = AdminLogFilterAlert.this.currentFilter;
              localTL_channelAdminLogEventsFilter5 = AdminLogFilterAlert.this.currentFilter;
              localTL_channelAdminLogEventsFilter6 = AdminLogFilterAlert.this.currentFilter;
              localTL_channelAdminLogEventsFilter7 = AdminLogFilterAlert.this.currentFilter;
              localTL_channelAdminLogEventsFilter8 = AdminLogFilterAlert.this.currentFilter;
              localTL_channelAdminLogEventsFilter9 = AdminLogFilterAlert.this.currentFilter;
              localTL_channelAdminLogEventsFilter10 = AdminLogFilterAlert.this.currentFilter;
              paramAnonymousView = AdminLogFilterAlert.this.currentFilter;
              localTL_channelAdminLogEventsFilter11 = AdminLogFilterAlert.this.currentFilter;
              AdminLogFilterAlert.this.currentFilter.delete = false;
              localTL_channelAdminLogEventsFilter11.edit = false;
              paramAnonymousView.pinned = false;
              localTL_channelAdminLogEventsFilter10.settings = false;
              localTL_channelAdminLogEventsFilter9.info = false;
              localTL_channelAdminLogEventsFilter8.demote = false;
              localTL_channelAdminLogEventsFilter7.promote = false;
              localTL_channelAdminLogEventsFilter6.unkick = false;
              localTL_channelAdminLogEventsFilter5.kick = false;
              ((TLRPC.TL_channelAdminLogEventsFilter)localObject).unban = false;
              localTL_channelAdminLogEventsFilter4.ban = false;
              localTL_channelAdminLogEventsFilter3.invite = false;
              localTL_channelAdminLogEventsFilter2.leave = false;
              localTL_channelAdminLogEventsFilter1.join = false;
              label258:
              i = AdminLogFilterAlert.this.listView.getChildCount();
              paramAnonymousInt = 0;
              label272:
              if (paramAnonymousInt >= i) {
                break label865;
              }
              localObject = AdminLogFilterAlert.this.listView.getChildAt(paramAnonymousInt);
              paramAnonymousView = AdminLogFilterAlert.this.listView.findContainingViewHolder((View)localObject);
              j = paramAnonymousView.getAdapterPosition();
              if ((paramAnonymousView.getItemViewType() == 0) && (j > 0) && (j < AdminLogFilterAlert.this.allAdminsRow - 1))
              {
                paramAnonymousView = (CheckBoxCell)localObject;
                if (bool1) {
                  break label380;
                }
              }
            }
            label368:
            label380:
            for (bool2 = true;; bool2 = false)
            {
              paramAnonymousView.setChecked(bool2, true);
              paramAnonymousInt++;
              break label272;
              bool2 = false;
              break;
              AdminLogFilterAlert.access$1002(AdminLogFilterAlert.this, null);
              break label258;
            }
            label386:
            if (paramAnonymousInt == AdminLogFilterAlert.this.allAdminsRow)
            {
              if (bool1)
              {
                AdminLogFilterAlert.access$1202(AdminLogFilterAlert.this, new SparseArray());
                i = AdminLogFilterAlert.this.listView.getChildCount();
                paramAnonymousInt = 0;
                label430:
                if (paramAnonymousInt >= i) {
                  break label865;
                }
                paramAnonymousView = AdminLogFilterAlert.this.listView.getChildAt(paramAnonymousInt);
                localObject = AdminLogFilterAlert.this.listView.findContainingViewHolder(paramAnonymousView);
                ((RecyclerView.ViewHolder)localObject).getAdapterPosition();
                if (((RecyclerView.ViewHolder)localObject).getItemViewType() == 2)
                {
                  paramAnonymousView = (CheckBoxUserCell)paramAnonymousView;
                  if (bool1) {
                    break label513;
                  }
                }
              }
              label513:
              for (bool2 = true;; bool2 = false)
              {
                paramAnonymousView.setChecked(bool2, true);
                paramAnonymousInt++;
                break label430;
                AdminLogFilterAlert.access$1202(AdminLogFilterAlert.this, null);
                break;
              }
            }
            else
            {
              if (AdminLogFilterAlert.this.currentFilter == null)
              {
                AdminLogFilterAlert.access$1002(AdminLogFilterAlert.this, new TLRPC.TL_channelAdminLogEventsFilter());
                localTL_channelAdminLogEventsFilter9 = AdminLogFilterAlert.this.currentFilter;
                localTL_channelAdminLogEventsFilter6 = AdminLogFilterAlert.this.currentFilter;
                localTL_channelAdminLogEventsFilter7 = AdminLogFilterAlert.this.currentFilter;
                paramAnonymousView = AdminLogFilterAlert.this.currentFilter;
                localTL_channelAdminLogEventsFilter11 = AdminLogFilterAlert.this.currentFilter;
                localTL_channelAdminLogEventsFilter4 = AdminLogFilterAlert.this.currentFilter;
                localTL_channelAdminLogEventsFilter10 = AdminLogFilterAlert.this.currentFilter;
                localTL_channelAdminLogEventsFilter5 = AdminLogFilterAlert.this.currentFilter;
                localTL_channelAdminLogEventsFilter1 = AdminLogFilterAlert.this.currentFilter;
                localTL_channelAdminLogEventsFilter2 = AdminLogFilterAlert.this.currentFilter;
                localTL_channelAdminLogEventsFilter3 = AdminLogFilterAlert.this.currentFilter;
                localObject = AdminLogFilterAlert.this.currentFilter;
                localTL_channelAdminLogEventsFilter8 = AdminLogFilterAlert.this.currentFilter;
                AdminLogFilterAlert.this.currentFilter.delete = true;
                localTL_channelAdminLogEventsFilter8.edit = true;
                ((TLRPC.TL_channelAdminLogEventsFilter)localObject).pinned = true;
                localTL_channelAdminLogEventsFilter3.settings = true;
                localTL_channelAdminLogEventsFilter2.info = true;
                localTL_channelAdminLogEventsFilter1.demote = true;
                localTL_channelAdminLogEventsFilter5.promote = true;
                localTL_channelAdminLogEventsFilter10.unkick = true;
                localTL_channelAdminLogEventsFilter4.kick = true;
                localTL_channelAdminLogEventsFilter11.unban = true;
                paramAnonymousView.ban = true;
                localTL_channelAdminLogEventsFilter7.invite = true;
                localTL_channelAdminLogEventsFilter6.leave = true;
                localTL_channelAdminLogEventsFilter9.join = true;
                paramAnonymousView = AdminLogFilterAlert.this.listView.findViewHolderForAdapterPosition(0);
                if (paramAnonymousView != null) {
                  ((CheckBoxCell)paramAnonymousView.itemView).setChecked(false, true);
                }
              }
              if (paramAnonymousInt != AdminLogFilterAlert.this.restrictionsRow) {
                break label1100;
              }
              localObject = AdminLogFilterAlert.this.currentFilter;
              localTL_channelAdminLogEventsFilter4 = AdminLogFilterAlert.this.currentFilter;
              localTL_channelAdminLogEventsFilter1 = AdminLogFilterAlert.this.currentFilter;
              paramAnonymousView = AdminLogFilterAlert.this.currentFilter;
              if (AdminLogFilterAlert.this.currentFilter.kick) {
                break label1094;
              }
              bool2 = true;
              paramAnonymousView.unban = bool2;
              localTL_channelAdminLogEventsFilter1.unkick = bool2;
              localTL_channelAdminLogEventsFilter4.ban = bool2;
              ((TLRPC.TL_channelAdminLogEventsFilter)localObject).kick = bool2;
            }
            label865:
            if ((AdminLogFilterAlert.this.currentFilter != null) && (!AdminLogFilterAlert.this.currentFilter.join) && (!AdminLogFilterAlert.this.currentFilter.leave) && (!AdminLogFilterAlert.this.currentFilter.leave) && (!AdminLogFilterAlert.this.currentFilter.invite) && (!AdminLogFilterAlert.this.currentFilter.ban) && (!AdminLogFilterAlert.this.currentFilter.unban) && (!AdminLogFilterAlert.this.currentFilter.kick) && (!AdminLogFilterAlert.this.currentFilter.unkick) && (!AdminLogFilterAlert.this.currentFilter.promote) && (!AdminLogFilterAlert.this.currentFilter.demote) && (!AdminLogFilterAlert.this.currentFilter.info) && (!AdminLogFilterAlert.this.currentFilter.settings) && (!AdminLogFilterAlert.this.currentFilter.pinned) && (!AdminLogFilterAlert.this.currentFilter.edit) && (!AdminLogFilterAlert.this.currentFilter.delete))
            {
              AdminLogFilterAlert.this.saveButton.setEnabled(false);
              AdminLogFilterAlert.this.saveButton.setAlpha(0.5F);
            }
          }
          label1094:
          label1100:
          while (!(paramAnonymousView instanceof CheckBoxUserCell)) {
            for (;;)
            {
              boolean bool1;
              TLRPC.TL_channelAdminLogEventsFilter localTL_channelAdminLogEventsFilter1;
              TLRPC.TL_channelAdminLogEventsFilter localTL_channelAdminLogEventsFilter2;
              TLRPC.TL_channelAdminLogEventsFilter localTL_channelAdminLogEventsFilter3;
              TLRPC.TL_channelAdminLogEventsFilter localTL_channelAdminLogEventsFilter4;
              TLRPC.TL_channelAdminLogEventsFilter localTL_channelAdminLogEventsFilter5;
              TLRPC.TL_channelAdminLogEventsFilter localTL_channelAdminLogEventsFilter6;
              TLRPC.TL_channelAdminLogEventsFilter localTL_channelAdminLogEventsFilter7;
              TLRPC.TL_channelAdminLogEventsFilter localTL_channelAdminLogEventsFilter8;
              TLRPC.TL_channelAdminLogEventsFilter localTL_channelAdminLogEventsFilter9;
              TLRPC.TL_channelAdminLogEventsFilter localTL_channelAdminLogEventsFilter10;
              TLRPC.TL_channelAdminLogEventsFilter localTL_channelAdminLogEventsFilter11;
              int i;
              int j;
              return;
              bool2 = false;
              continue;
              if (paramAnonymousInt == AdminLogFilterAlert.this.adminsRow)
              {
                paramAnonymousView = AdminLogFilterAlert.this.currentFilter;
                localObject = AdminLogFilterAlert.this.currentFilter;
                if (!AdminLogFilterAlert.this.currentFilter.demote) {}
                for (bool2 = true;; bool2 = false)
                {
                  ((TLRPC.TL_channelAdminLogEventsFilter)localObject).demote = bool2;
                  paramAnonymousView.promote = bool2;
                  break;
                }
              }
              if (paramAnonymousInt == AdminLogFilterAlert.this.membersRow)
              {
                paramAnonymousView = AdminLogFilterAlert.this.currentFilter;
                localObject = AdminLogFilterAlert.this.currentFilter;
                if (!AdminLogFilterAlert.this.currentFilter.join) {}
                for (bool2 = true;; bool2 = false)
                {
                  ((TLRPC.TL_channelAdminLogEventsFilter)localObject).join = bool2;
                  paramAnonymousView.invite = bool2;
                  break;
                }
              }
              if (paramAnonymousInt == AdminLogFilterAlert.this.infoRow)
              {
                localObject = AdminLogFilterAlert.this.currentFilter;
                paramAnonymousView = AdminLogFilterAlert.this.currentFilter;
                if (!AdminLogFilterAlert.this.currentFilter.info) {}
                for (bool2 = true;; bool2 = false)
                {
                  paramAnonymousView.settings = bool2;
                  ((TLRPC.TL_channelAdminLogEventsFilter)localObject).info = bool2;
                  break;
                }
              }
              if (paramAnonymousInt == AdminLogFilterAlert.this.deleteRow)
              {
                paramAnonymousView = AdminLogFilterAlert.this.currentFilter;
                if (!AdminLogFilterAlert.this.currentFilter.delete) {}
                for (bool2 = true;; bool2 = false)
                {
                  paramAnonymousView.delete = bool2;
                  break;
                }
              }
              if (paramAnonymousInt == AdminLogFilterAlert.this.editRow)
              {
                paramAnonymousView = AdminLogFilterAlert.this.currentFilter;
                if (!AdminLogFilterAlert.this.currentFilter.edit) {}
                for (bool2 = true;; bool2 = false)
                {
                  paramAnonymousView.edit = bool2;
                  break;
                }
              }
              if (paramAnonymousInt == AdminLogFilterAlert.this.pinnedRow)
              {
                paramAnonymousView = AdminLogFilterAlert.this.currentFilter;
                if (!AdminLogFilterAlert.this.currentFilter.pinned) {}
                for (bool2 = true;; bool2 = false)
                {
                  paramAnonymousView.pinned = bool2;
                  break;
                }
              }
              if (paramAnonymousInt == AdminLogFilterAlert.this.leavingRow)
              {
                paramAnonymousView = AdminLogFilterAlert.this.currentFilter;
                if (!AdminLogFilterAlert.this.currentFilter.leave) {}
                for (bool2 = true;; bool2 = false)
                {
                  paramAnonymousView.leave = bool2;
                  break;
                }
                AdminLogFilterAlert.this.saveButton.setEnabled(true);
                AdminLogFilterAlert.this.saveButton.setAlpha(1.0F);
              }
            }
          }
          paramAnonymousView = (CheckBoxUserCell)paramAnonymousView;
          if (AdminLogFilterAlert.this.selectedAdmins == null)
          {
            AdminLogFilterAlert.access$1202(AdminLogFilterAlert.this, new SparseArray());
            localObject = AdminLogFilterAlert.this.listView.findViewHolderForAdapterPosition(AdminLogFilterAlert.this.allAdminsRow);
            if (localObject != null) {
              ((CheckBoxCell)((RecyclerView.ViewHolder)localObject).itemView).setChecked(false, true);
            }
            for (paramAnonymousInt = 0; paramAnonymousInt < AdminLogFilterAlert.this.currentAdmins.size(); paramAnonymousInt++)
            {
              localObject = MessagesController.getInstance(AdminLogFilterAlert.this.currentAccount).getUser(Integer.valueOf(((TLRPC.ChannelParticipant)AdminLogFilterAlert.this.currentAdmins.get(paramAnonymousInt)).user_id));
              AdminLogFilterAlert.this.selectedAdmins.put(((TLRPC.User)localObject).id, localObject);
            }
          }
          boolean bool2 = paramAnonymousView.isChecked();
          Object localObject = paramAnonymousView.getCurrentUser();
          if (bool2)
          {
            AdminLogFilterAlert.this.selectedAdmins.remove(((TLRPC.User)localObject).id);
            label1703:
            if (bool2) {
              break label1741;
            }
          }
          label1741:
          for (bool2 = true;; bool2 = false)
          {
            paramAnonymousView.setChecked(bool2, true);
            break;
            AdminLogFilterAlert.this.selectedAdmins.put(((TLRPC.User)localObject).id, localObject);
            break label1703;
          }
        }
      });
      this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, 48.0F));
      paramTL_channelAdminLogEventsFilter = new View(paramContext);
      paramTL_channelAdminLogEventsFilter.setBackgroundResource(NUM);
      this.containerView.addView(paramTL_channelAdminLogEventsFilter, LayoutHelper.createFrame(-1, 3.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
      this.saveButton = new BottomSheet.BottomSheetCell(paramContext, 1);
      this.saveButton.setBackgroundDrawable(Theme.getSelectorDrawable(false));
      this.saveButton.setTextAndIcon(LocaleController.getString("Save", NUM).toUpperCase(), 0);
      this.saveButton.setTextColor(Theme.getColor("dialogTextBlue2"));
      this.saveButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          AdminLogFilterAlert.this.delegate.didSelectRights(AdminLogFilterAlert.this.currentFilter, AdminLogFilterAlert.this.selectedAdmins);
          AdminLogFilterAlert.this.dismiss();
        }
      });
      this.containerView.addView(this.saveButton, LayoutHelper.createFrame(-1, 48, 83));
      this.adapter.notifyDataSetChanged();
      return;
      this.restrictionsRow = -1;
      break;
      label680:
      this.pinnedRow = -1;
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
  
  public void setAdminLogFilterAlertDelegate(AdminLogFilterAlertDelegate paramAdminLogFilterAlertDelegate)
  {
    this.delegate = paramAdminLogFilterAlertDelegate;
  }
  
  public void setCurrentAdmins(ArrayList<TLRPC.ChannelParticipant> paramArrayList)
  {
    this.currentAdmins = paramArrayList;
    if (this.adapter != null) {
      this.adapter.notifyDataSetChanged();
    }
  }
  
  public static abstract interface AdminLogFilterAlertDelegate
  {
    public abstract void didSelectRights(TLRPC.TL_channelAdminLogEventsFilter paramTL_channelAdminLogEventsFilter, SparseArray<TLRPC.User> paramSparseArray);
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
      int i;
      if (AdminLogFilterAlert.this.isMegagroup)
      {
        i = 9;
        if (AdminLogFilterAlert.this.currentAdmins == null) {
          break label46;
        }
      }
      label46:
      for (int j = AdminLogFilterAlert.this.currentAdmins.size() + 2;; j = 0)
      {
        return i + j;
        i = 7;
        break;
      }
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((paramInt < AdminLogFilterAlert.this.allAdminsRow - 1) || (paramInt == AdminLogFilterAlert.this.allAdminsRow)) {
        paramInt = 0;
      }
      for (;;)
      {
        return paramInt;
        if (paramInt == AdminLogFilterAlert.this.allAdminsRow - 1) {
          paramInt = 1;
        } else {
          paramInt = 2;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool = true;
      if (paramViewHolder.getItemViewType() != 1) {}
      for (;;)
      {
        return bool;
        bool = false;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool1 = false;
      boolean bool2 = false;
      boolean bool3 = false;
      boolean bool4 = false;
      boolean bool5 = false;
      boolean bool6 = false;
      boolean bool7 = false;
      boolean bool8 = false;
      boolean bool9 = false;
      boolean bool10 = true;
      boolean bool11 = true;
      switch (paramViewHolder.getItemViewType())
      {
      case 1: 
      default: 
      case 0: 
        for (;;)
        {
          return;
          paramViewHolder = (CheckBoxCell)paramViewHolder.itemView;
          if (paramInt == 0)
          {
            localObject = LocaleController.getString("EventLogFilterAll", NUM);
            if (AdminLogFilterAlert.this.currentFilter == null) {}
            for (bool3 = true;; bool3 = false)
            {
              paramViewHolder.setText((String)localObject, "", bool3, true);
              break;
            }
          }
          if (paramInt == AdminLogFilterAlert.this.restrictionsRow)
          {
            localObject = LocaleController.getString("EventLogFilterNewRestrictions", NUM);
            if (AdminLogFilterAlert.this.currentFilter != null)
            {
              bool3 = bool9;
              if (AdminLogFilterAlert.this.currentFilter.kick)
              {
                bool3 = bool9;
                if (AdminLogFilterAlert.this.currentFilter.ban)
                {
                  bool3 = bool9;
                  if (AdminLogFilterAlert.this.currentFilter.unkick)
                  {
                    bool3 = bool9;
                    if (!AdminLogFilterAlert.this.currentFilter.unban) {}
                  }
                }
              }
            }
            else
            {
              bool3 = true;
            }
            paramViewHolder.setText((String)localObject, "", bool3, true);
          }
          else if (paramInt == AdminLogFilterAlert.this.adminsRow)
          {
            localObject = LocaleController.getString("EventLogFilterNewAdmins", NUM);
            if (AdminLogFilterAlert.this.currentFilter != null)
            {
              bool3 = bool1;
              if (AdminLogFilterAlert.this.currentFilter.promote)
              {
                bool3 = bool1;
                if (!AdminLogFilterAlert.this.currentFilter.demote) {}
              }
            }
            else
            {
              bool3 = true;
            }
            paramViewHolder.setText((String)localObject, "", bool3, true);
          }
          else if (paramInt == AdminLogFilterAlert.this.membersRow)
          {
            localObject = LocaleController.getString("EventLogFilterNewMembers", NUM);
            if (AdminLogFilterAlert.this.currentFilter != null)
            {
              bool3 = bool2;
              if (AdminLogFilterAlert.this.currentFilter.invite)
              {
                bool3 = bool2;
                if (!AdminLogFilterAlert.this.currentFilter.join) {}
              }
            }
            else
            {
              bool3 = true;
            }
            paramViewHolder.setText((String)localObject, "", bool3, true);
          }
          else if (paramInt == AdminLogFilterAlert.this.infoRow)
          {
            if (AdminLogFilterAlert.this.isMegagroup)
            {
              localObject = LocaleController.getString("EventLogFilterGroupInfo", NUM);
              if ((AdminLogFilterAlert.this.currentFilter == null) || (AdminLogFilterAlert.this.currentFilter.info)) {
                bool3 = true;
              }
              paramViewHolder.setText((String)localObject, "", bool3, true);
            }
            else
            {
              localObject = LocaleController.getString("EventLogFilterChannelInfo", NUM);
              if (AdminLogFilterAlert.this.currentFilter != null)
              {
                bool3 = bool4;
                if (!AdminLogFilterAlert.this.currentFilter.info) {}
              }
              else
              {
                bool3 = true;
              }
              paramViewHolder.setText((String)localObject, "", bool3, true);
            }
          }
          else if (paramInt == AdminLogFilterAlert.this.deleteRow)
          {
            localObject = LocaleController.getString("EventLogFilterDeletedMessages", NUM);
            if (AdminLogFilterAlert.this.currentFilter != null)
            {
              bool3 = bool5;
              if (!AdminLogFilterAlert.this.currentFilter.delete) {}
            }
            else
            {
              bool3 = true;
            }
            paramViewHolder.setText((String)localObject, "", bool3, true);
          }
          else if (paramInt == AdminLogFilterAlert.this.editRow)
          {
            localObject = LocaleController.getString("EventLogFilterEditedMessages", NUM);
            if (AdminLogFilterAlert.this.currentFilter != null)
            {
              bool3 = bool6;
              if (!AdminLogFilterAlert.this.currentFilter.edit) {}
            }
            else
            {
              bool3 = true;
            }
            paramViewHolder.setText((String)localObject, "", bool3, true);
          }
          else if (paramInt == AdminLogFilterAlert.this.pinnedRow)
          {
            localObject = LocaleController.getString("EventLogFilterPinnedMessages", NUM);
            if (AdminLogFilterAlert.this.currentFilter != null)
            {
              bool3 = bool7;
              if (!AdminLogFilterAlert.this.currentFilter.pinned) {}
            }
            else
            {
              bool3 = true;
            }
            paramViewHolder.setText((String)localObject, "", bool3, true);
          }
          else
          {
            if (paramInt == AdminLogFilterAlert.this.leavingRow)
            {
              localObject = LocaleController.getString("EventLogFilterLeavingMembers", NUM);
              bool3 = bool11;
              if (AdminLogFilterAlert.this.currentFilter != null) {
                if (!AdminLogFilterAlert.this.currentFilter.leave) {
                  break label774;
                }
              }
              label774:
              for (bool3 = bool11;; bool3 = false)
              {
                paramViewHolder.setText((String)localObject, "", bool3, false);
                break;
              }
            }
            if (paramInt == AdminLogFilterAlert.this.allAdminsRow)
            {
              localObject = LocaleController.getString("EventLogAllAdmins", NUM);
              bool3 = bool8;
              if (AdminLogFilterAlert.this.selectedAdmins == null) {
                bool3 = true;
              }
              paramViewHolder.setText((String)localObject, "", bool3, true);
            }
          }
        }
      }
      paramViewHolder = (CheckBoxUserCell)paramViewHolder.itemView;
      int i = ((TLRPC.ChannelParticipant)AdminLogFilterAlert.this.currentAdmins.get(paramInt - AdminLogFilterAlert.this.allAdminsRow - 1)).user_id;
      Object localObject = MessagesController.getInstance(AdminLogFilterAlert.this.currentAccount).getUser(Integer.valueOf(i));
      if ((AdminLogFilterAlert.this.selectedAdmins == null) || (AdminLogFilterAlert.this.selectedAdmins.indexOfKey(i) >= 0))
      {
        bool3 = true;
        label916:
        if (paramInt == getItemCount() - 1) {
          break label945;
        }
      }
      for (;;)
      {
        paramViewHolder.setUser((TLRPC.User)localObject, bool3, bool10);
        break;
        bool3 = false;
        break label916;
        label945:
        bool10 = false;
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
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new CheckBoxCell(this.context, 1);
        paramViewGroup.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        continue;
        ShadowSectionCell localShadowSectionCell = new ShadowSectionCell(this.context);
        localShadowSectionCell.setSize(18);
        paramViewGroup = new FrameLayout(this.context);
        ((FrameLayout)paramViewGroup).addView(localShadowSectionCell, LayoutHelper.createFrame(-1, -1.0F));
        paramViewGroup.setBackgroundColor(Theme.getColor("dialogBackgroundGray"));
        continue;
        paramViewGroup = new CheckBoxUserCell(this.context, true);
      }
    }
    
    public void onViewAttachedToWindow(RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool1 = true;
      boolean bool2 = true;
      boolean bool3 = true;
      boolean bool4 = true;
      boolean bool5 = true;
      boolean bool6 = true;
      boolean bool7 = true;
      boolean bool8 = true;
      boolean bool9 = true;
      boolean bool10 = true;
      int i = paramViewHolder.getAdapterPosition();
      switch (paramViewHolder.getItemViewType())
      {
      case 1: 
      default: 
      case 0: 
        label199:
        label269:
        label339:
        label398:
        label457:
        label516:
        label575:
        label634:
        do
        {
          return;
          paramViewHolder = (CheckBoxCell)paramViewHolder.itemView;
          if (i == 0)
          {
            if (AdminLogFilterAlert.this.currentFilter == null) {}
            for (bool8 = true;; bool8 = false)
            {
              paramViewHolder.setChecked(bool8, false);
              break;
            }
          }
          if (i == AdminLogFilterAlert.this.restrictionsRow)
          {
            bool8 = bool10;
            if (AdminLogFilterAlert.this.currentFilter != null) {
              if ((!AdminLogFilterAlert.this.currentFilter.kick) || (!AdminLogFilterAlert.this.currentFilter.ban) || (!AdminLogFilterAlert.this.currentFilter.unkick) || (!AdminLogFilterAlert.this.currentFilter.unban)) {
                break label199;
              }
            }
            for (bool8 = bool10;; bool8 = false)
            {
              paramViewHolder.setChecked(bool8, false);
              break;
            }
          }
          if (i == AdminLogFilterAlert.this.adminsRow)
          {
            bool8 = bool1;
            if (AdminLogFilterAlert.this.currentFilter != null) {
              if ((!AdminLogFilterAlert.this.currentFilter.promote) || (!AdminLogFilterAlert.this.currentFilter.demote)) {
                break label269;
              }
            }
            for (bool8 = bool1;; bool8 = false)
            {
              paramViewHolder.setChecked(bool8, false);
              break;
            }
          }
          if (i == AdminLogFilterAlert.this.membersRow)
          {
            bool8 = bool2;
            if (AdminLogFilterAlert.this.currentFilter != null) {
              if ((!AdminLogFilterAlert.this.currentFilter.invite) || (!AdminLogFilterAlert.this.currentFilter.join)) {
                break label339;
              }
            }
            for (bool8 = bool2;; bool8 = false)
            {
              paramViewHolder.setChecked(bool8, false);
              break;
            }
          }
          if (i == AdminLogFilterAlert.this.infoRow)
          {
            bool8 = bool3;
            if (AdminLogFilterAlert.this.currentFilter != null) {
              if (!AdminLogFilterAlert.this.currentFilter.info) {
                break label398;
              }
            }
            for (bool8 = bool3;; bool8 = false)
            {
              paramViewHolder.setChecked(bool8, false);
              break;
            }
          }
          if (i == AdminLogFilterAlert.this.deleteRow)
          {
            bool8 = bool4;
            if (AdminLogFilterAlert.this.currentFilter != null) {
              if (!AdminLogFilterAlert.this.currentFilter.delete) {
                break label457;
              }
            }
            for (bool8 = bool4;; bool8 = false)
            {
              paramViewHolder.setChecked(bool8, false);
              break;
            }
          }
          if (i == AdminLogFilterAlert.this.editRow)
          {
            bool8 = bool5;
            if (AdminLogFilterAlert.this.currentFilter != null) {
              if (!AdminLogFilterAlert.this.currentFilter.edit) {
                break label516;
              }
            }
            for (bool8 = bool5;; bool8 = false)
            {
              paramViewHolder.setChecked(bool8, false);
              break;
            }
          }
          if (i == AdminLogFilterAlert.this.pinnedRow)
          {
            bool8 = bool6;
            if (AdminLogFilterAlert.this.currentFilter != null) {
              if (!AdminLogFilterAlert.this.currentFilter.pinned) {
                break label575;
              }
            }
            for (bool8 = bool6;; bool8 = false)
            {
              paramViewHolder.setChecked(bool8, false);
              break;
            }
          }
          if (i == AdminLogFilterAlert.this.leavingRow)
          {
            bool8 = bool7;
            if (AdminLogFilterAlert.this.currentFilter != null) {
              if (!AdminLogFilterAlert.this.currentFilter.leave) {
                break label634;
              }
            }
            for (bool8 = bool7;; bool8 = false)
            {
              paramViewHolder.setChecked(bool8, false);
              break;
            }
          }
        } while (i != AdminLogFilterAlert.this.allAdminsRow);
        if (AdminLogFilterAlert.this.selectedAdmins == null) {}
        for (;;)
        {
          paramViewHolder.setChecked(bool8, false);
          break;
          bool8 = false;
        }
      }
      paramViewHolder = (CheckBoxUserCell)paramViewHolder.itemView;
      i = ((TLRPC.ChannelParticipant)AdminLogFilterAlert.this.currentAdmins.get(i - AdminLogFilterAlert.this.allAdminsRow - 1)).user_id;
      bool8 = bool9;
      if (AdminLogFilterAlert.this.selectedAdmins != null) {
        if (AdminLogFilterAlert.this.selectedAdmins.indexOfKey(i) < 0) {
          break label759;
        }
      }
      label759:
      for (bool8 = bool9;; bool8 = false)
      {
        paramViewHolder.setChecked(bool8, false);
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/AdminLogFilterAlert.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */