package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Cells.GroupCreateSectionCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.GroupCreateDividerItemDecoration;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.FastScrollAdapter;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

public class GroupCreateActivity
  extends BaseFragment
  implements View.OnClickListener, NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private GroupCreateAdapter adapter;
  private ArrayList<GroupCreateSpan> allSpans = new ArrayList();
  private int chatId;
  private int chatType = 0;
  private int containerHeight;
  private GroupCreateSpan currentDeletingSpan;
  private AnimatorSet currentDoneButtonAnimation;
  private GroupCreateActivityDelegate delegate;
  private View doneButton;
  private boolean doneButtonVisible;
  private EditTextBoldCursor editText;
  private EmptyTextProgressView emptyView;
  private int fieldY;
  private boolean ignoreScrollEvent;
  private boolean isAlwaysShare;
  private boolean isGroup;
  private boolean isNeverShare;
  private GroupCreateDividerItemDecoration itemDecoration;
  private RecyclerListView listView;
  private int maxCount = MessagesController.getInstance(this.currentAccount).maxMegagroupCount;
  private ScrollView scrollView;
  private boolean searchWas;
  private boolean searching;
  private SparseArray<GroupCreateSpan> selectedContacts = new SparseArray();
  private SpansContainer spansContainer;
  
  public GroupCreateActivity() {}
  
  public GroupCreateActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.chatType = paramBundle.getInt("chatType", 0);
    this.isAlwaysShare = paramBundle.getBoolean("isAlwaysShare", false);
    this.isNeverShare = paramBundle.getBoolean("isNeverShare", false);
    this.isGroup = paramBundle.getBoolean("isGroup", false);
    this.chatId = paramBundle.getInt("chatId");
    if (this.chatType == 0) {}
    for (int i = MessagesController.getInstance(this.currentAccount).maxMegagroupCount;; i = MessagesController.getInstance(this.currentAccount).maxBroadcastCount)
    {
      this.maxCount = i;
      return;
    }
  }
  
  private void checkVisibleRows()
  {
    int i = this.listView.getChildCount();
    int j = 0;
    if (j < i)
    {
      Object localObject = this.listView.getChildAt(j);
      GroupCreateUserCell localGroupCreateUserCell;
      if ((localObject instanceof GroupCreateUserCell))
      {
        localGroupCreateUserCell = (GroupCreateUserCell)localObject;
        localObject = localGroupCreateUserCell.getUser();
        if (localObject != null) {
          if (this.selectedContacts.indexOfKey(((TLRPC.User)localObject).id) < 0) {
            break label78;
          }
        }
      }
      label78:
      for (boolean bool = true;; bool = false)
      {
        localGroupCreateUserCell.setChecked(bool, true);
        j++;
        break;
      }
    }
  }
  
  private void closeSearch()
  {
    this.searching = false;
    this.searchWas = false;
    this.itemDecoration.setSearching(false);
    this.adapter.setSearching(false);
    this.adapter.searchDialogs(null);
    this.listView.setFastScrollVisible(true);
    this.listView.setVerticalScrollBarEnabled(false);
    this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
  }
  
  private boolean onDonePressed()
  {
    boolean bool1 = false;
    ArrayList localArrayList;
    int i;
    Object localObject;
    if (this.chatType == 2)
    {
      localArrayList = new ArrayList();
      for (i = 0; i < this.selectedContacts.size(); i++)
      {
        localObject = MessagesController.getInstance(this.currentAccount).getInputUser(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.selectedContacts.keyAt(i))));
        if (localObject != null) {
          localArrayList.add(localObject);
        }
      }
      MessagesController.getInstance(this.currentAccount).addUsersToChannel(this.chatId, localArrayList, null);
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
      localObject = new Bundle();
      ((Bundle)localObject).putInt("chat_id", this.chatId);
      presentFragment(new ChatActivity((Bundle)localObject), true);
    }
    for (;;)
    {
      boolean bool2 = true;
      do
      {
        do
        {
          return bool2;
          bool2 = bool1;
        } while (!this.doneButtonVisible);
        bool2 = bool1;
      } while (this.selectedContacts.size() == 0);
      localArrayList = new ArrayList();
      for (i = 0; i < this.selectedContacts.size(); i++) {
        localArrayList.add(Integer.valueOf(this.selectedContacts.keyAt(i)));
      }
      if ((this.isAlwaysShare) || (this.isNeverShare))
      {
        if (this.delegate != null) {
          this.delegate.didSelectUsers(localArrayList);
        }
        finishFragment();
      }
      else
      {
        localObject = new Bundle();
        ((Bundle)localObject).putIntegerArrayList("result", localArrayList);
        ((Bundle)localObject).putInt("chatType", this.chatType);
        presentFragment(new GroupCreateFinalActivity((Bundle)localObject));
      }
    }
  }
  
  private void updateHint()
  {
    if ((!this.isAlwaysShare) && (!this.isNeverShare))
    {
      if (this.chatType == 2) {
        this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", this.selectedContacts.size()));
      }
    }
    else if (this.chatType != 2)
    {
      if ((!this.doneButtonVisible) || (!this.allSpans.isEmpty())) {
        break label279;
      }
      if (this.currentDoneButtonAnimation != null) {
        this.currentDoneButtonAnimation.cancel();
      }
      this.currentDoneButtonAnimation = new AnimatorSet();
      this.currentDoneButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.doneButton, "scaleX", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.doneButton, "scaleY", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.doneButton, "alpha", new float[] { 0.0F }) });
      this.currentDoneButtonAnimation.setDuration(180L);
      this.currentDoneButtonAnimation.start();
    }
    for (this.doneButtonVisible = false;; this.doneButtonVisible = true)
    {
      label279:
      do
      {
        return;
        if (this.selectedContacts.size() == 0)
        {
          this.actionBar.setSubtitle(LocaleController.formatString("MembersCountZero", NUM, new Object[] { LocaleController.formatPluralString("Members", this.maxCount) }));
          break;
        }
        this.actionBar.setSubtitle(LocaleController.formatString("MembersCount", NUM, new Object[] { Integer.valueOf(this.selectedContacts.size()), Integer.valueOf(this.maxCount) }));
        break;
      } while ((this.doneButtonVisible) || (this.allSpans.isEmpty()));
      if (this.currentDoneButtonAnimation != null) {
        this.currentDoneButtonAnimation.cancel();
      }
      this.currentDoneButtonAnimation = new AnimatorSet();
      this.currentDoneButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.doneButton, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.doneButton, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.doneButton, "alpha", new float[] { 1.0F }) });
      this.currentDoneButtonAnimation.setDuration(180L);
      this.currentDoneButtonAnimation.start();
    }
  }
  
  public View createView(Context paramContext)
  {
    int i = 1;
    this.searching = false;
    this.searchWas = false;
    this.allSpans.clear();
    this.selectedContacts.clear();
    this.currentDeletingSpan = null;
    boolean bool;
    label88:
    Object localObject1;
    Object localObject2;
    if (this.chatType == 2)
    {
      bool = true;
      this.doneButtonVisible = bool;
      this.actionBar.setBackButtonImage(NUM);
      this.actionBar.setAllowOverlayTitle(true);
      if (this.chatType != 2) {
        break label768;
      }
      this.actionBar.setTitle(LocaleController.getString("ChannelAddMembers", NUM));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            GroupCreateActivity.this.finishFragment();
          }
          for (;;)
          {
            return;
            if (paramAnonymousInt == 1) {
              GroupCreateActivity.this.onDonePressed();
            }
          }
        }
      });
      this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0F));
      if (this.chatType != 2)
      {
        this.doneButton.setScaleX(0.0F);
        this.doneButton.setScaleY(0.0F);
        this.doneButton.setAlpha(0.0F);
      }
      this.fragmentView = new ViewGroup(paramContext)
      {
        protected boolean drawChild(Canvas paramAnonymousCanvas, View paramAnonymousView, long paramAnonymousLong)
        {
          boolean bool = super.drawChild(paramAnonymousCanvas, paramAnonymousView, paramAnonymousLong);
          if ((paramAnonymousView == GroupCreateActivity.this.listView) || (paramAnonymousView == GroupCreateActivity.this.emptyView)) {
            GroupCreateActivity.this.parentLayout.drawHeaderShadow(paramAnonymousCanvas, GroupCreateActivity.this.scrollView.getMeasuredHeight());
          }
          return bool;
        }
        
        protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
        {
          GroupCreateActivity.this.scrollView.layout(0, 0, GroupCreateActivity.this.scrollView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight());
          GroupCreateActivity.this.listView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.listView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.listView.getMeasuredHeight());
          GroupCreateActivity.this.emptyView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.emptyView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.emptyView.getMeasuredHeight());
        }
        
        protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          int i = View.MeasureSpec.getSize(paramAnonymousInt1);
          paramAnonymousInt2 = View.MeasureSpec.getSize(paramAnonymousInt2);
          setMeasuredDimension(i, paramAnonymousInt2);
          if ((AndroidUtilities.isTablet()) || (paramAnonymousInt2 > i)) {}
          for (paramAnonymousInt1 = AndroidUtilities.dp(144.0F);; paramAnonymousInt1 = AndroidUtilities.dp(56.0F))
          {
            GroupCreateActivity.this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(paramAnonymousInt1, Integer.MIN_VALUE));
            GroupCreateActivity.this.listView.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(paramAnonymousInt2 - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
            GroupCreateActivity.this.emptyView.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(paramAnonymousInt2 - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
            return;
          }
        }
      };
      localObject1 = (ViewGroup)this.fragmentView;
      this.scrollView = new ScrollView(paramContext)
      {
        public boolean requestChildRectangleOnScreen(View paramAnonymousView, Rect paramAnonymousRect, boolean paramAnonymousBoolean)
        {
          boolean bool = false;
          if (GroupCreateActivity.this.ignoreScrollEvent) {
            GroupCreateActivity.access$302(GroupCreateActivity.this, false);
          }
          for (paramAnonymousBoolean = bool;; paramAnonymousBoolean = super.requestChildRectangleOnScreen(paramAnonymousView, paramAnonymousRect, paramAnonymousBoolean))
          {
            return paramAnonymousBoolean;
            paramAnonymousRect.offset(paramAnonymousView.getLeft() - paramAnonymousView.getScrollX(), paramAnonymousView.getTop() - paramAnonymousView.getScrollY());
            paramAnonymousRect.top += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(20.0F);
            paramAnonymousRect.bottom += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(50.0F);
          }
        }
      };
      this.scrollView.setVerticalScrollBarEnabled(false);
      AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("windowBackgroundWhite"));
      ((ViewGroup)localObject1).addView(this.scrollView);
      this.spansContainer = new SpansContainer(paramContext);
      this.scrollView.addView(this.spansContainer, LayoutHelper.createFrame(-1, -2.0F));
      this.editText = new EditTextBoldCursor(paramContext)
      {
        public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if (GroupCreateActivity.this.currentDeletingSpan != null)
          {
            GroupCreateActivity.this.currentDeletingSpan.cancelDeleteAnimation();
            GroupCreateActivity.access$1502(GroupCreateActivity.this, null);
          }
          return super.onTouchEvent(paramAnonymousMotionEvent);
        }
      };
      this.editText.setTextSize(1, 18.0F);
      this.editText.setHintColor(Theme.getColor("groupcreate_hintText"));
      this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.editText.setCursorColor(Theme.getColor("groupcreate_cursor"));
      this.editText.setCursorWidth(1.5F);
      this.editText.setInputType(655536);
      this.editText.setSingleLine(true);
      this.editText.setBackgroundDrawable(null);
      this.editText.setVerticalScrollBarEnabled(false);
      this.editText.setHorizontalScrollBarEnabled(false);
      this.editText.setTextIsSelectable(false);
      this.editText.setPadding(0, 0, 0, 0);
      this.editText.setImeOptions(268435462);
      localObject2 = this.editText;
      if (!LocaleController.isRTL) {
        break label920;
      }
      j = 5;
      label414:
      ((EditTextBoldCursor)localObject2).setGravity(j | 0x10);
      this.spansContainer.addView(this.editText);
      if (this.chatType != 2) {
        break label926;
      }
      this.editText.setHintText(LocaleController.getString("AddMutual", NUM));
      label459:
      this.editText.setCustomSelectionActionModeCallback(new ActionMode.Callback()
      {
        public boolean onActionItemClicked(ActionMode paramAnonymousActionMode, MenuItem paramAnonymousMenuItem)
        {
          return false;
        }
        
        public boolean onCreateActionMode(ActionMode paramAnonymousActionMode, Menu paramAnonymousMenu)
        {
          return false;
        }
        
        public void onDestroyActionMode(ActionMode paramAnonymousActionMode) {}
        
        public boolean onPrepareActionMode(ActionMode paramAnonymousActionMode, Menu paramAnonymousMenu)
        {
          return false;
        }
      });
      this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if ((paramAnonymousInt == 6) && (GroupCreateActivity.this.onDonePressed())) {}
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
      });
      this.editText.setOnKeyListener(new View.OnKeyListener()
      {
        private boolean wasEmpty;
        
        public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          boolean bool = true;
          if (paramAnonymousInt == 67)
          {
            if (paramAnonymousKeyEvent.getAction() != 0) {
              break label50;
            }
            if (GroupCreateActivity.this.editText.length() == 0)
            {
              bool = true;
              this.wasEmpty = bool;
            }
          }
          else
          {
            label38:
            bool = false;
          }
          for (;;)
          {
            return bool;
            bool = false;
            break;
            label50:
            if ((paramAnonymousKeyEvent.getAction() != 1) || (!this.wasEmpty) || (GroupCreateActivity.this.allSpans.isEmpty())) {
              break label38;
            }
            GroupCreateActivity.this.spansContainer.removeSpan((GroupCreateSpan)GroupCreateActivity.this.allSpans.get(GroupCreateActivity.this.allSpans.size() - 1));
            GroupCreateActivity.this.updateHint();
            GroupCreateActivity.this.checkVisibleRows();
          }
        }
      });
      this.editText.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable)
        {
          if (GroupCreateActivity.this.editText.length() != 0)
          {
            GroupCreateActivity.access$1902(GroupCreateActivity.this, true);
            GroupCreateActivity.access$2002(GroupCreateActivity.this, true);
            GroupCreateActivity.this.adapter.setSearching(true);
            GroupCreateActivity.this.itemDecoration.setSearching(true);
            GroupCreateActivity.this.adapter.searchDialogs(GroupCreateActivity.this.editText.getText().toString());
            GroupCreateActivity.this.listView.setFastScrollVisible(false);
            GroupCreateActivity.this.listView.setVerticalScrollBarEnabled(true);
            GroupCreateActivity.this.emptyView.setText(LocaleController.getString("NoResult", NUM));
          }
          for (;;)
          {
            return;
            GroupCreateActivity.this.closeSearch();
          }
        }
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      });
      this.emptyView = new EmptyTextProgressView(paramContext);
      if (!ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
        break label1049;
      }
      this.emptyView.showProgress();
      label551:
      this.emptyView.setShowAtCenter(true);
      this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
      ((ViewGroup)localObject1).addView(this.emptyView);
      localObject2 = new LinearLayoutManager(paramContext, 1, false);
      this.listView = new RecyclerListView(paramContext);
      this.listView.setFastScrollEnabled();
      this.listView.setEmptyView(this.emptyView);
      RecyclerListView localRecyclerListView = this.listView;
      paramContext = new GroupCreateAdapter(paramContext);
      this.adapter = paramContext;
      localRecyclerListView.setAdapter(paramContext);
      this.listView.setLayoutManager((RecyclerView.LayoutManager)localObject2);
      this.listView.setVerticalScrollBarEnabled(false);
      paramContext = this.listView;
      if (!LocaleController.isRTL) {
        break label1059;
      }
    }
    label768:
    label920:
    label926:
    label1049:
    label1059:
    for (int j = i;; j = 2)
    {
      paramContext.setVerticalScrollbarPosition(j);
      localObject2 = this.listView;
      paramContext = new GroupCreateDividerItemDecoration();
      this.itemDecoration = paramContext;
      ((RecyclerListView)localObject2).addItemDecoration(paramContext);
      ((ViewGroup)localObject1).addView(this.listView);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          boolean bool1 = false;
          if (!(paramAnonymousView instanceof GroupCreateUserCell)) {}
          label46:
          label151:
          label370:
          label388:
          for (;;)
          {
            return;
            paramAnonymousView = (GroupCreateUserCell)paramAnonymousView;
            Object localObject = paramAnonymousView.getUser();
            if (localObject != null)
            {
              if (GroupCreateActivity.this.selectedContacts.indexOfKey(((TLRPC.User)localObject).id) >= 0)
              {
                paramAnonymousInt = 1;
                if (paramAnonymousInt == 0) {
                  break label151;
                }
                localObject = (GroupCreateSpan)GroupCreateActivity.this.selectedContacts.get(((TLRPC.User)localObject).id);
                GroupCreateActivity.this.spansContainer.removeSpan((GroupCreateSpan)localObject);
                GroupCreateActivity.this.updateHint();
                if ((!GroupCreateActivity.this.searching) && (!GroupCreateActivity.this.searchWas)) {
                  break label370;
                }
                AndroidUtilities.showKeyboard(GroupCreateActivity.this.editText);
              }
              for (;;)
              {
                if (GroupCreateActivity.this.editText.length() <= 0) {
                  break label388;
                }
                GroupCreateActivity.this.editText.setText(null);
                break;
                paramAnonymousInt = 0;
                break label46;
                if ((GroupCreateActivity.this.maxCount != 0) && (GroupCreateActivity.this.selectedContacts.size() == GroupCreateActivity.this.maxCount)) {
                  break;
                }
                if ((GroupCreateActivity.this.chatType == 0) && (GroupCreateActivity.this.selectedContacts.size() == MessagesController.getInstance(GroupCreateActivity.this.currentAccount).maxGroupCount))
                {
                  paramAnonymousView = new AlertDialog.Builder(GroupCreateActivity.this.getParentActivity());
                  paramAnonymousView.setTitle(LocaleController.getString("AppName", NUM));
                  paramAnonymousView.setMessage(LocaleController.getString("SoftUserLimitAlert", NUM));
                  paramAnonymousView.setPositiveButton(LocaleController.getString("OK", NUM), null);
                  GroupCreateActivity.this.showDialog(paramAnonymousView.create());
                  break;
                }
                MessagesController localMessagesController = MessagesController.getInstance(GroupCreateActivity.this.currentAccount);
                if (!GroupCreateActivity.this.searching) {}
                for (boolean bool2 = true;; bool2 = false)
                {
                  localMessagesController.putUser((TLRPC.User)localObject, bool2);
                  localObject = new GroupCreateSpan(GroupCreateActivity.this.editText.getContext(), (TLRPC.User)localObject);
                  GroupCreateActivity.this.spansContainer.addSpan((GroupCreateSpan)localObject);
                  ((GroupCreateSpan)localObject).setOnClickListener(GroupCreateActivity.this);
                  break;
                }
                bool2 = bool1;
                if (paramAnonymousInt == 0) {
                  bool2 = true;
                }
                paramAnonymousView.setChecked(bool2, true);
              }
            }
          }
        }
      });
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
        {
          if (paramAnonymousInt == 1) {
            AndroidUtilities.hideKeyboard(GroupCreateActivity.this.editText);
          }
        }
      });
      updateHint();
      return this.fragmentView;
      bool = false;
      break;
      if (this.isAlwaysShare)
      {
        if (this.isGroup)
        {
          this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", NUM));
          break label88;
        }
        this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", NUM));
        break label88;
      }
      if (this.isNeverShare)
      {
        if (this.isGroup)
        {
          this.actionBar.setTitle(LocaleController.getString("NeverAllow", NUM));
          break label88;
        }
        this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", NUM));
        break label88;
      }
      localObject2 = this.actionBar;
      if (this.chatType == 0) {}
      for (localObject1 = LocaleController.getString("NewGroup", NUM);; localObject1 = LocaleController.getString("NewBroadcastList", NUM))
      {
        ((ActionBar)localObject2).setTitle((CharSequence)localObject1);
        break;
      }
      j = 3;
      break label414;
      if (this.isAlwaysShare)
      {
        if (this.isGroup)
        {
          this.editText.setHintText(LocaleController.getString("AlwaysAllowPlaceholder", NUM));
          break label459;
        }
        this.editText.setHintText(LocaleController.getString("AlwaysShareWithPlaceholder", NUM));
        break label459;
      }
      if (this.isNeverShare)
      {
        if (this.isGroup)
        {
          this.editText.setHintText(LocaleController.getString("NeverAllowPlaceholder", NUM));
          break label459;
        }
        this.editText.setHintText(LocaleController.getString("NeverShareWithPlaceholder", NUM));
        break label459;
      }
      this.editText.setHintText(LocaleController.getString("SendMessageTo", NUM));
      break label459;
      this.emptyView.showTextView();
      break label551;
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.contactsDidLoaded)
    {
      if (this.emptyView != null) {
        this.emptyView.showTextView();
      }
      if (this.adapter != null) {
        this.adapter.notifyDataSetChanged();
      }
    }
    for (;;)
    {
      return;
      if (paramInt1 == NotificationCenter.updateInterfaces)
      {
        if (this.listView != null)
        {
          int i = ((Integer)paramVarArgs[0]).intValue();
          paramInt2 = this.listView.getChildCount();
          if (((i & 0x2) != 0) || ((i & 0x1) != 0) || ((i & 0x4) != 0)) {
            for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
            {
              paramVarArgs = this.listView.getChildAt(paramInt1);
              if ((paramVarArgs instanceof GroupCreateUserCell)) {
                ((GroupCreateUserCell)paramVarArgs).update(i);
              }
            }
          }
        }
      }
      else if (paramInt1 == NotificationCenter.chatDidCreated) {
        removeSelfFromStack();
      }
    }
  }
  
  public int getContainerHeight()
  {
    return this.containerHeight;
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription.ThemeDescriptionDelegate local11 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor()
      {
        if (GroupCreateActivity.this.listView != null)
        {
          int i = GroupCreateActivity.this.listView.getChildCount();
          for (int j = 0; j < i; j++)
          {
            View localView = GroupCreateActivity.this.listView.getChildAt(j);
            if ((localView instanceof GroupCreateUserCell)) {
              ((GroupCreateUserCell)localView).update(0);
            }
          }
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollActive");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollInactive");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollText");
    Object localObject1 = this.listView;
    Object localObject2 = Theme.dividerPaint;
    ThemeDescription localThemeDescription12 = new ThemeDescription((View)localObject1, 0, new Class[] { View.class }, (Paint)localObject2, null, null, "divider");
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
    ThemeDescription localThemeDescription14 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
    ThemeDescription localThemeDescription15 = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription16 = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "groupcreate_hintText");
    localObject1 = new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "groupcreate_cursor");
    ThemeDescription localThemeDescription17 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { GroupCreateSectionCell.class }, null, null, null, "graySection");
    ThemeDescription localThemeDescription18 = new ThemeDescription(this.listView, 0, new Class[] { GroupCreateSectionCell.class }, new String[] { "drawable" }, null, null, null, "groupcreate_sectionShadow");
    ThemeDescription localThemeDescription19 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { GroupCreateSectionCell.class }, new String[] { "textView" }, null, null, null, "groupcreate_sectionText");
    ThemeDescription localThemeDescription20 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { GroupCreateUserCell.class }, new String[] { "textView" }, null, null, null, "groupcreate_sectionText");
    ThemeDescription localThemeDescription21 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { GroupCreateUserCell.class }, new String[] { "checkBox" }, null, null, null, "groupcreate_checkbox");
    ThemeDescription localThemeDescription22 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { GroupCreateUserCell.class }, new String[] { "checkBox" }, null, null, null, "groupcreate_checkboxCheck");
    ThemeDescription localThemeDescription23 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[] { GroupCreateUserCell.class }, new String[] { "statusTextView" }, null, null, null, "groupcreate_onlineText");
    ThemeDescription localThemeDescription24 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[] { GroupCreateUserCell.class }, new String[] { "statusTextView" }, null, null, null, "groupcreate_offlineText");
    localObject2 = this.listView;
    Drawable localDrawable1 = Theme.avatar_photoDrawable;
    Drawable localDrawable2 = Theme.avatar_broadcastDrawable;
    Drawable localDrawable3 = Theme.avatar_savedDrawable;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, localThemeDescription12, localThemeDescription13, localThemeDescription14, localThemeDescription15, localThemeDescription16, localObject1, localThemeDescription17, localThemeDescription18, localThemeDescription19, localThemeDescription20, localThemeDescription21, localThemeDescription22, localThemeDescription23, localThemeDescription24, new ThemeDescription((View)localObject2, 0, new Class[] { GroupCreateUserCell.class }, null, new Drawable[] { localDrawable1, localDrawable2, localDrawable3 }, null, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local11, "avatar_backgroundPink"), new ThemeDescription(this.spansContainer, 0, new Class[] { GroupCreateSpan.class }, null, null, null, "avatar_backgroundGroupCreateSpanBlue"), new ThemeDescription(this.spansContainer, 0, new Class[] { GroupCreateSpan.class }, null, null, null, "groupcreate_spanBackground"), new ThemeDescription(this.spansContainer, 0, new Class[] { GroupCreateSpan.class }, null, null, null, "groupcreate_spanText"), new ThemeDescription(this.spansContainer, 0, new Class[] { GroupCreateSpan.class }, null, null, null, "avatar_backgroundBlue") };
  }
  
  public void onClick(View paramView)
  {
    paramView = (GroupCreateSpan)paramView;
    if (paramView.isDeleting())
    {
      this.currentDeletingSpan = null;
      this.spansContainer.removeSpan(paramView);
      updateHint();
      checkVisibleRows();
    }
    for (;;)
    {
      return;
      if (this.currentDeletingSpan != null) {
        this.currentDeletingSpan.cancelDeleteAnimation();
      }
      this.currentDeletingSpan = paramView;
      paramView.startDeleteAnimation();
    }
  }
  
  public boolean onFragmentCreate()
  {
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.editText != null) {
      this.editText.requestFocus();
    }
  }
  
  @Keep
  public void setContainerHeight(int paramInt)
  {
    this.containerHeight = paramInt;
    if (this.spansContainer != null) {
      this.spansContainer.requestLayout();
    }
  }
  
  public void setDelegate(GroupCreateActivityDelegate paramGroupCreateActivityDelegate)
  {
    this.delegate = paramGroupCreateActivityDelegate;
  }
  
  public static abstract interface GroupCreateActivityDelegate
  {
    public abstract void didSelectUsers(ArrayList<Integer> paramArrayList);
  }
  
  public class GroupCreateAdapter
    extends RecyclerListView.FastScrollAdapter
  {
    private ArrayList<TLRPC.User> contacts = new ArrayList();
    private Context context;
    private SearchAdapterHelper searchAdapterHelper;
    private ArrayList<TLRPC.User> searchResult = new ArrayList();
    private ArrayList<CharSequence> searchResultNames = new ArrayList();
    private Timer searchTimer;
    private boolean searching;
    
    public GroupCreateAdapter(Context paramContext)
    {
      this.context = paramContext;
      ArrayList localArrayList = ContactsController.getInstance(GroupCreateActivity.this.currentAccount).contacts;
      int i = 0;
      if (i < localArrayList.size())
      {
        paramContext = MessagesController.getInstance(GroupCreateActivity.this.currentAccount).getUser(Integer.valueOf(((TLRPC.TL_contact)localArrayList.get(i)).user_id));
        if ((paramContext == null) || (paramContext.self) || (paramContext.deleted)) {}
        for (;;)
        {
          i++;
          break;
          this.contacts.add(paramContext);
        }
      }
      this.searchAdapterHelper = new SearchAdapterHelper(true);
      this.searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate()
      {
        public void onDataSetChanged()
        {
          GroupCreateActivity.GroupCreateAdapter.this.notifyDataSetChanged();
        }
        
        public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> paramAnonymousArrayList, HashMap<String, SearchAdapterHelper.HashtagObject> paramAnonymousHashMap) {}
      });
    }
    
    private void updateSearchResults(final ArrayList<TLRPC.User> paramArrayList, final ArrayList<CharSequence> paramArrayList1)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          GroupCreateActivity.GroupCreateAdapter.access$3402(GroupCreateActivity.GroupCreateAdapter.this, paramArrayList);
          GroupCreateActivity.GroupCreateAdapter.access$3502(GroupCreateActivity.GroupCreateAdapter.this, paramArrayList1);
          GroupCreateActivity.GroupCreateAdapter.this.notifyDataSetChanged();
        }
      });
    }
    
    public int getItemCount()
    {
      int i;
      int j;
      if (this.searching)
      {
        i = this.searchResult.size();
        j = this.searchAdapterHelper.getGlobalSearch().size();
        k = i;
        if (j == 0) {}
      }
      for (int k = i + (j + 1);; k = this.contacts.size()) {
        return k;
      }
    }
    
    public int getItemViewType(int paramInt)
    {
      int i = 1;
      int j = i;
      if (this.searching)
      {
        j = i;
        if (paramInt == this.searchResult.size()) {
          j = 0;
        }
      }
      return j;
    }
    
    public String getLetter(int paramInt)
    {
      Object localObject1 = null;
      Object localObject2 = localObject1;
      if (paramInt >= 0)
      {
        if (paramInt < this.contacts.size()) {
          break label23;
        }
        localObject2 = localObject1;
      }
      for (;;)
      {
        return (String)localObject2;
        label23:
        TLRPC.User localUser = (TLRPC.User)this.contacts.get(paramInt);
        localObject2 = localObject1;
        if (localUser != null)
        {
          if (LocaleController.nameDisplayOrder == 1)
          {
            if (!TextUtils.isEmpty(localUser.first_name))
            {
              localObject2 = localUser.first_name.substring(0, 1).toUpperCase();
              continue;
            }
            if (!TextUtils.isEmpty(localUser.last_name)) {
              localObject2 = localUser.last_name.substring(0, 1).toUpperCase();
            }
          }
          else
          {
            if (!TextUtils.isEmpty(localUser.last_name))
            {
              localObject2 = localUser.last_name.substring(0, 1).toUpperCase();
              continue;
            }
            if (!TextUtils.isEmpty(localUser.first_name))
            {
              localObject2 = localUser.first_name.substring(0, 1).toUpperCase();
              continue;
            }
          }
          localObject2 = "";
        }
      }
    }
    
    public int getPositionForScrollProgress(float paramFloat)
    {
      return (int)(getItemCount() * paramFloat);
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      GroupCreateUserCell localGroupCreateUserCell;
      Object localObject1;
      CharSequence localCharSequence;
      int i;
      int j;
      Object localObject2;
      Object localObject3;
      Object localObject4;
      switch (paramViewHolder.getItemViewType())
      {
      default: 
        localGroupCreateUserCell = (GroupCreateUserCell)paramViewHolder.itemView;
        localObject1 = null;
        localCharSequence = null;
        if (this.searching)
        {
          i = this.searchResult.size();
          j = this.searchAdapterHelper.getGlobalSearch().size();
          if ((paramInt >= 0) && (paramInt < i))
          {
            paramViewHolder = (TLRPC.User)this.searchResult.get(paramInt);
            localObject2 = localCharSequence;
            localObject3 = paramViewHolder;
            localObject4 = localObject1;
            if (paramViewHolder != null)
            {
              if (paramInt >= i) {
                break label328;
              }
              localCharSequence = (CharSequence)this.searchResultNames.get(paramInt);
              localObject2 = localCharSequence;
              localObject3 = paramViewHolder;
              localObject4 = localObject1;
              if (localCharSequence != null)
              {
                localObject2 = localCharSequence;
                localObject3 = paramViewHolder;
                localObject4 = localObject1;
                if (!TextUtils.isEmpty(paramViewHolder.username))
                {
                  localObject2 = localCharSequence;
                  localObject3 = paramViewHolder;
                  localObject4 = localObject1;
                  if (localCharSequence.toString().startsWith("@" + paramViewHolder.username))
                  {
                    localObject4 = localCharSequence;
                    localObject2 = null;
                    localObject3 = paramViewHolder;
                  }
                }
              }
            }
            label215:
            localGroupCreateUserCell.setUser((TLRPC.User)localObject3, (CharSequence)localObject2, (CharSequence)localObject4);
            if (GroupCreateActivity.this.selectedContacts.indexOfKey(((TLRPC.User)localObject3).id) < 0) {
              break label549;
            }
          }
        }
        break;
      }
      label328:
      label501:
      label549:
      for (boolean bool = true;; bool = false)
      {
        localGroupCreateUserCell.setChecked(bool, false);
        for (;;)
        {
          return;
          paramViewHolder = (GroupCreateSectionCell)paramViewHolder.itemView;
          if (this.searching) {
            paramViewHolder.setText(LocaleController.getString("GlobalSearch", NUM));
          }
        }
        if ((paramInt > i) && (paramInt <= j + i))
        {
          paramViewHolder = (TLRPC.User)this.searchAdapterHelper.getGlobalSearch().get(paramInt - i - 1);
          break;
        }
        paramViewHolder = null;
        break;
        localObject2 = localCharSequence;
        localObject3 = paramViewHolder;
        localObject4 = localObject1;
        if (paramInt <= i) {
          break label215;
        }
        localObject2 = localCharSequence;
        localObject3 = paramViewHolder;
        localObject4 = localObject1;
        if (TextUtils.isEmpty(paramViewHolder.username)) {
          break label215;
        }
        localObject2 = this.searchAdapterHelper.getLastFoundUsername();
        localObject4 = localObject2;
        if (((String)localObject2).startsWith("@")) {
          localObject4 = ((String)localObject2).substring(1);
        }
        try
        {
          localObject2 = new android/text/SpannableStringBuilder;
          ((SpannableStringBuilder)localObject2).<init>();
          ((SpannableStringBuilder)localObject2).append("@");
          ((SpannableStringBuilder)localObject2).append(paramViewHolder.username);
          paramInt = paramViewHolder.username.toLowerCase().indexOf((String)localObject4);
          if (paramInt != -1)
          {
            i = ((String)localObject4).length();
            if (paramInt != 0) {
              break label501;
            }
            i++;
          }
          for (;;)
          {
            localObject4 = new android/text/style/ForegroundColorSpan;
            ((ForegroundColorSpan)localObject4).<init>(Theme.getColor("windowBackgroundWhiteBlueText4"));
            ((SpannableStringBuilder)localObject2).setSpan(localObject4, paramInt, paramInt + i, 33);
            localObject4 = localObject2;
            localObject2 = localCharSequence;
            localObject3 = paramViewHolder;
            break;
            paramInt++;
          }
        }
        catch (Exception localException)
        {
          localObject5 = paramViewHolder.username;
          localObject2 = localCharSequence;
          localObject3 = paramViewHolder;
        }
        localObject3 = (TLRPC.User)this.contacts.get(paramInt);
        localObject2 = localCharSequence;
        Object localObject5 = localObject1;
        break label215;
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      }
      for (paramViewGroup = new GroupCreateUserCell(this.context, true);; paramViewGroup = new GroupCreateSectionCell(this.context)) {
        return new RecyclerListView.Holder(paramViewGroup);
      }
    }
    
    public void onViewRecycled(RecyclerView.ViewHolder paramViewHolder)
    {
      if ((paramViewHolder.itemView instanceof GroupCreateUserCell)) {
        ((GroupCreateUserCell)paramViewHolder.itemView).recycle();
      }
    }
    
    public void searchDialogs(final String paramString)
    {
      try
      {
        if (this.searchTimer != null) {
          this.searchTimer.cancel();
        }
        if (paramString == null)
        {
          this.searchResult.clear();
          this.searchResultNames.clear();
          this.searchAdapterHelper.queryServerSearch(null, true, false, false, false, 0, false);
          notifyDataSetChanged();
          return;
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
          continue;
          this.searchTimer = new Timer();
          this.searchTimer.schedule(new TimerTask()
          {
            public void run()
            {
              try
              {
                GroupCreateActivity.GroupCreateAdapter.this.searchTimer.cancel();
                GroupCreateActivity.GroupCreateAdapter.access$3002(GroupCreateActivity.GroupCreateAdapter.this, null);
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    GroupCreateActivity.GroupCreateAdapter.this.searchAdapterHelper.queryServerSearch(GroupCreateActivity.GroupCreateAdapter.2.this.val$query, true, false, false, false, 0, false);
                    Utilities.searchQueue.postRunnable(new Runnable()
                    {
                      public void run()
                      {
                        Object localObject = GroupCreateActivity.GroupCreateAdapter.2.this.val$query.trim().toLowerCase();
                        if (((String)localObject).length() == 0) {
                          GroupCreateActivity.GroupCreateAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
                        }
                        for (;;)
                        {
                          return;
                          String str1 = LocaleController.getInstance().getTranslitString((String)localObject);
                          String str2;
                          if (!((String)localObject).equals(str1))
                          {
                            str2 = str1;
                            if (str1.length() != 0) {}
                          }
                          else
                          {
                            str2 = null;
                          }
                          int i;
                          String[] arrayOfString;
                          ArrayList localArrayList1;
                          ArrayList localArrayList2;
                          int j;
                          label130:
                          String str3;
                          int k;
                          int m;
                          int n;
                          if (str2 != null)
                          {
                            i = 1;
                            arrayOfString = new String[i + 1];
                            arrayOfString[0] = localObject;
                            if (str2 != null) {
                              arrayOfString[1] = str2;
                            }
                            localArrayList1 = new ArrayList();
                            localArrayList2 = new ArrayList();
                            j = 0;
                            if (j < GroupCreateActivity.GroupCreateAdapter.this.contacts.size())
                            {
                              localObject = (TLRPC.User)GroupCreateActivity.GroupCreateAdapter.this.contacts.get(j);
                              str3 = ContactsController.formatName(((TLRPC.User)localObject).first_name, ((TLRPC.User)localObject).last_name).toLowerCase();
                              str1 = LocaleController.getInstance().getTranslitString(str3);
                              str2 = str1;
                              if (str3.equals(str1)) {
                                str2 = null;
                              }
                              k = 0;
                              m = arrayOfString.length;
                              n = 0;
                            }
                          }
                          else
                          {
                            for (;;)
                            {
                              if (n < m)
                              {
                                str1 = arrayOfString[n];
                                if ((!str3.startsWith(str1)) && (!str3.contains(" " + str1)) && ((str2 == null) || ((!str2.startsWith(str1)) && (!str2.contains(" " + str1))))) {
                                  break label360;
                                }
                                i = 1;
                                label312:
                                if (i == 0) {
                                  break label446;
                                }
                                if (i != 1) {
                                  break label392;
                                }
                                localArrayList2.add(AndroidUtilities.generateSearchName(((TLRPC.User)localObject).first_name, ((TLRPC.User)localObject).last_name, str1));
                              }
                              for (;;)
                              {
                                localArrayList1.add(localObject);
                                j++;
                                break label130;
                                i = 0;
                                break;
                                label360:
                                i = k;
                                if (((TLRPC.User)localObject).username == null) {
                                  break label312;
                                }
                                i = k;
                                if (!((TLRPC.User)localObject).username.startsWith(str1)) {
                                  break label312;
                                }
                                i = 2;
                                break label312;
                                label392:
                                localArrayList2.add(AndroidUtilities.generateSearchName("@" + ((TLRPC.User)localObject).username, null, "@" + str1));
                              }
                              label446:
                              n++;
                              k = i;
                            }
                          }
                          GroupCreateActivity.GroupCreateAdapter.this.updateSearchResults(localArrayList1, localArrayList2);
                        }
                      }
                    });
                  }
                });
                return;
              }
              catch (Exception localException)
              {
                for (;;)
                {
                  FileLog.e(localException);
                }
              }
            }
          }, 200L, 300L);
        }
      }
    }
    
    public void setSearching(boolean paramBoolean)
    {
      if (this.searching == paramBoolean) {}
      for (;;)
      {
        return;
        this.searching = paramBoolean;
        notifyDataSetChanged();
      }
    }
  }
  
  private class SpansContainer
    extends ViewGroup
  {
    private View addingSpan;
    private boolean animationStarted;
    private ArrayList<Animator> animators = new ArrayList();
    private AnimatorSet currentAnimation;
    private View removingSpan;
    
    public SpansContainer(Context paramContext)
    {
      super();
    }
    
    public void addSpan(GroupCreateSpan paramGroupCreateSpan)
    {
      GroupCreateActivity.this.allSpans.add(paramGroupCreateSpan);
      GroupCreateActivity.this.selectedContacts.put(paramGroupCreateSpan.getUid(), paramGroupCreateSpan);
      GroupCreateActivity.this.editText.setHintVisible(false);
      if (this.currentAnimation != null)
      {
        this.currentAnimation.setupEndValues();
        this.currentAnimation.cancel();
      }
      this.animationStarted = false;
      this.currentAnimation = new AnimatorSet();
      this.currentAnimation.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          GroupCreateActivity.SpansContainer.access$602(GroupCreateActivity.SpansContainer.this, null);
          GroupCreateActivity.SpansContainer.access$702(GroupCreateActivity.SpansContainer.this, null);
          GroupCreateActivity.SpansContainer.access$802(GroupCreateActivity.SpansContainer.this, false);
          GroupCreateActivity.this.editText.setAllowDrawCursor(true);
        }
      });
      this.currentAnimation.setDuration(150L);
      this.addingSpan = paramGroupCreateSpan;
      this.animators.clear();
      this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleX", new float[] { 0.01F, 1.0F }));
      this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleY", new float[] { 0.01F, 1.0F }));
      this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "alpha", new float[] { 0.0F, 1.0F }));
      addView(paramGroupCreateSpan);
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramInt2 = getChildCount();
      for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
      {
        View localView = getChildAt(paramInt1);
        localView.layout(0, 0, localView.getMeasuredWidth(), localView.getMeasuredHeight());
      }
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int i = getChildCount();
      int j = View.MeasureSpec.getSize(paramInt1);
      int k = j - AndroidUtilities.dp(32.0F);
      int m = 0;
      paramInt2 = AndroidUtilities.dp(12.0F);
      int n = 0;
      paramInt1 = AndroidUtilities.dp(12.0F);
      int i1 = 0;
      int i2;
      int i3;
      while (i1 < i)
      {
        View localView = getChildAt(i1);
        if (!(localView instanceof GroupCreateSpan))
        {
          i2 = paramInt2;
          paramInt2 = paramInt1;
          i1++;
          paramInt1 = paramInt2;
          paramInt2 = i2;
        }
        else
        {
          localView.measure(View.MeasureSpec.makeMeasureSpec(j, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0F), NUM));
          i3 = m;
          i2 = paramInt2;
          if (localView != this.removingSpan)
          {
            i3 = m;
            i2 = paramInt2;
            if (localView.getMeasuredWidth() + m > k)
            {
              i2 = paramInt2 + (localView.getMeasuredHeight() + AndroidUtilities.dp(12.0F));
              i3 = 0;
            }
          }
          m = n;
          paramInt2 = paramInt1;
          if (localView.getMeasuredWidth() + n > k)
          {
            paramInt2 = paramInt1 + (localView.getMeasuredHeight() + AndroidUtilities.dp(12.0F));
            m = 0;
          }
          paramInt1 = AndroidUtilities.dp(16.0F) + i3;
          if (!this.animationStarted)
          {
            if (localView != this.removingSpan) {
              break label287;
            }
            localView.setTranslationX(AndroidUtilities.dp(16.0F) + m);
            localView.setTranslationY(paramInt2);
          }
          for (;;)
          {
            paramInt1 = i3;
            if (localView != this.removingSpan) {
              paramInt1 = i3 + (localView.getMeasuredWidth() + AndroidUtilities.dp(9.0F));
            }
            n = m + (localView.getMeasuredWidth() + AndroidUtilities.dp(9.0F));
            m = paramInt1;
            break;
            label287:
            if (this.removingSpan != null)
            {
              if (localView.getTranslationX() != paramInt1) {
                this.animators.add(ObjectAnimator.ofFloat(localView, "translationX", new float[] { paramInt1 }));
              }
              if (localView.getTranslationY() != i2) {
                this.animators.add(ObjectAnimator.ofFloat(localView, "translationY", new float[] { i2 }));
              }
            }
            else
            {
              localView.setTranslationX(paramInt1);
              localView.setTranslationY(i2);
            }
          }
        }
      }
      if (AndroidUtilities.isTablet())
      {
        i1 = AndroidUtilities.dp(366.0F) / 3;
        i3 = m;
        i2 = paramInt2;
        if (k - m < i1)
        {
          i3 = 0;
          i2 = paramInt2 + AndroidUtilities.dp(44.0F);
        }
        paramInt2 = paramInt1;
        if (k - n < i1) {
          paramInt2 = paramInt1 + AndroidUtilities.dp(44.0F);
        }
        GroupCreateActivity.this.editText.measure(View.MeasureSpec.makeMeasureSpec(k - i3, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0F), NUM));
        if (this.animationStarted) {
          break label786;
        }
        paramInt1 = AndroidUtilities.dp(44.0F);
        m = i3 + AndroidUtilities.dp(16.0F);
        GroupCreateActivity.access$102(GroupCreateActivity.this, i2);
        if (this.currentAnimation == null) {
          break label741;
        }
        paramInt1 = i2 + AndroidUtilities.dp(44.0F);
        if (GroupCreateActivity.this.containerHeight != paramInt1) {
          this.animators.add(ObjectAnimator.ofInt(GroupCreateActivity.this, "containerHeight", new int[] { paramInt1 }));
        }
        if (GroupCreateActivity.this.editText.getTranslationX() != m) {
          this.animators.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, "translationX", new float[] { m }));
        }
        if (GroupCreateActivity.this.editText.getTranslationY() != GroupCreateActivity.this.fieldY) {
          this.animators.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, "translationY", new float[] { GroupCreateActivity.this.fieldY }));
        }
        GroupCreateActivity.this.editText.setAllowDrawCursor(false);
        this.currentAnimation.playTogether(this.animators);
        this.currentAnimation.start();
        this.animationStarted = true;
      }
      for (;;)
      {
        setMeasuredDimension(j, GroupCreateActivity.this.containerHeight);
        return;
        i1 = (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(164.0F)) / 3;
        break;
        label741:
        GroupCreateActivity.access$202(GroupCreateActivity.this, paramInt2 + paramInt1);
        GroupCreateActivity.this.editText.setTranslationX(m);
        GroupCreateActivity.this.editText.setTranslationY(GroupCreateActivity.this.fieldY);
        continue;
        label786:
        if ((this.currentAnimation != null) && (!GroupCreateActivity.this.ignoreScrollEvent) && (this.removingSpan == null)) {
          GroupCreateActivity.this.editText.bringPointIntoView(GroupCreateActivity.this.editText.getSelectionStart());
        }
      }
    }
    
    public void removeSpan(final GroupCreateSpan paramGroupCreateSpan)
    {
      GroupCreateActivity.access$302(GroupCreateActivity.this, true);
      GroupCreateActivity.this.selectedContacts.remove(paramGroupCreateSpan.getUid());
      GroupCreateActivity.this.allSpans.remove(paramGroupCreateSpan);
      paramGroupCreateSpan.setOnClickListener(null);
      if (this.currentAnimation != null)
      {
        this.currentAnimation.setupEndValues();
        this.currentAnimation.cancel();
      }
      this.animationStarted = false;
      this.currentAnimation = new AnimatorSet();
      this.currentAnimation.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          GroupCreateActivity.SpansContainer.this.removeView(paramGroupCreateSpan);
          GroupCreateActivity.SpansContainer.access$902(GroupCreateActivity.SpansContainer.this, null);
          GroupCreateActivity.SpansContainer.access$702(GroupCreateActivity.SpansContainer.this, null);
          GroupCreateActivity.SpansContainer.access$802(GroupCreateActivity.SpansContainer.this, false);
          GroupCreateActivity.this.editText.setAllowDrawCursor(true);
          if (GroupCreateActivity.this.allSpans.isEmpty()) {
            GroupCreateActivity.this.editText.setHintVisible(true);
          }
        }
      });
      this.currentAnimation.setDuration(150L);
      this.removingSpan = paramGroupCreateSpan;
      this.animators.clear();
      this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleX", new float[] { 1.0F, 0.01F }));
      this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleY", new float[] { 1.0F, 0.01F }));
      this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "alpha", new float[] { 1.0F, 0.0F }));
      requestLayout();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/GroupCreateActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */