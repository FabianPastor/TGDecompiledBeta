package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.ContactsAdapter;
import org.telegram.ui.Adapters.SearchAdapter;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.ChipSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LetterSectionsListView;

public class GroupCreateActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private ArrayList<ChipSpan> allSpans = new ArrayList();
  private int beforeChangeIndex;
  private CharSequence changeString;
  private int chatType = 0;
  private GroupCreateActivityDelegate delegate;
  private TextView emptyTextView;
  private boolean ignoreChange;
  private boolean isAlwaysShare;
  private boolean isGroup;
  private boolean isNeverShare;
  private LetterSectionsListView listView;
  private ContactsAdapter listViewAdapter;
  private int maxCount = 5000;
  private SearchAdapter searchListViewAdapter;
  private boolean searchWas;
  private boolean searching;
  private HashMap<Integer, ChipSpan> selectedContacts = new HashMap();
  private EditText userSelectEditText;
  
  public GroupCreateActivity() {}
  
  public GroupCreateActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.chatType = paramBundle.getInt("chatType", 0);
    this.isAlwaysShare = paramBundle.getBoolean("isAlwaysShare", false);
    this.isNeverShare = paramBundle.getBoolean("isNeverShare", false);
    this.isGroup = paramBundle.getBoolean("isGroup", false);
    if (this.chatType == 0) {}
    for (int i = MessagesController.getInstance().maxMegagroupCount;; i = MessagesController.getInstance().maxBroadcastCount)
    {
      this.maxCount = i;
      return;
    }
  }
  
  private ChipSpan createAndPutChipForUser(TLRPC.User paramUser)
  {
    Object localObject3 = ((LayoutInflater)ApplicationLoader.applicationContext.getSystemService("layout_inflater")).inflate(2130903041, null);
    TextView localTextView = (TextView)((View)localObject3).findViewById(2131492879);
    Object localObject2 = UserObject.getUserName(paramUser);
    Object localObject1 = localObject2;
    if (((String)localObject2).length() == 0)
    {
      localObject1 = localObject2;
      if (paramUser.phone != null)
      {
        localObject1 = localObject2;
        if (paramUser.phone.length() != 0) {
          localObject1 = PhoneFormat.getInstance().format("+" + paramUser.phone);
        }
      }
    }
    localTextView.setText((String)localObject1 + ", ");
    int i = View.MeasureSpec.makeMeasureSpec(0, 0);
    ((View)localObject3).measure(i, i);
    ((View)localObject3).layout(0, 0, ((View)localObject3).getMeasuredWidth(), ((View)localObject3).getMeasuredHeight());
    localObject1 = Bitmap.createBitmap(((View)localObject3).getWidth(), ((View)localObject3).getHeight(), Bitmap.Config.ARGB_8888);
    localObject2 = new Canvas((Bitmap)localObject1);
    ((Canvas)localObject2).translate(-((View)localObject3).getScrollX(), -((View)localObject3).getScrollY());
    ((View)localObject3).draw((Canvas)localObject2);
    ((View)localObject3).setDrawingCacheEnabled(true);
    ((View)localObject3).getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
    ((View)localObject3).destroyDrawingCache();
    localObject2 = new BitmapDrawable((Bitmap)localObject1);
    ((BitmapDrawable)localObject2).setBounds(0, 0, ((Bitmap)localObject1).getWidth(), ((Bitmap)localObject1).getHeight());
    localObject1 = new SpannableStringBuilder("");
    localObject2 = new ChipSpan((Drawable)localObject2, 1);
    this.allSpans.add(localObject2);
    this.selectedContacts.put(Integer.valueOf(paramUser.id), localObject2);
    paramUser = this.allSpans.iterator();
    while (paramUser.hasNext())
    {
      localObject3 = (ImageSpan)paramUser.next();
      ((SpannableStringBuilder)localObject1).append("<<");
      ((SpannableStringBuilder)localObject1).setSpan(localObject3, ((SpannableStringBuilder)localObject1).length() - 2, ((SpannableStringBuilder)localObject1).length(), 33);
    }
    this.userSelectEditText.setText((CharSequence)localObject1);
    this.userSelectEditText.setSelection(((SpannableStringBuilder)localObject1).length());
    return (ChipSpan)localObject2;
  }
  
  private void updateVisibleRows(int paramInt)
  {
    if (this.listView != null)
    {
      int j = this.listView.getChildCount();
      int i = 0;
      while (i < j)
      {
        View localView = this.listView.getChildAt(i);
        if ((localView instanceof UserCell)) {
          ((UserCell)localView).update(paramInt);
        }
        i += 1;
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    this.searching = false;
    this.searchWas = false;
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    Object localObject1;
    Object localObject2;
    if (this.isAlwaysShare) {
      if (this.isGroup)
      {
        this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", 2131165281));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
        {
          public void onItemClick(int paramAnonymousInt)
          {
            if (paramAnonymousInt == -1) {
              GroupCreateActivity.this.finishFragment();
            }
            while ((paramAnonymousInt != 1) || (GroupCreateActivity.this.selectedContacts.isEmpty())) {
              return;
            }
            ArrayList localArrayList = new ArrayList();
            localArrayList.addAll(GroupCreateActivity.this.selectedContacts.keySet());
            if ((GroupCreateActivity.this.isAlwaysShare) || (GroupCreateActivity.this.isNeverShare))
            {
              if (GroupCreateActivity.this.delegate != null) {
                GroupCreateActivity.this.delegate.didSelectUsers(localArrayList);
              }
              GroupCreateActivity.this.finishFragment();
              return;
            }
            Bundle localBundle = new Bundle();
            localBundle.putIntegerArrayList("result", localArrayList);
            localBundle.putInt("chatType", GroupCreateActivity.this.chatType);
            GroupCreateActivity.this.presentFragment(new GroupCreateFinalActivity(localBundle));
          }
        });
        this.actionBar.createMenu().addItemWithWidth(1, 2130837720, AndroidUtilities.dp(56.0F));
        this.searchListViewAdapter = new SearchAdapter(paramContext, null, false, false, false, false);
        this.searchListViewAdapter.setCheckedMap(this.selectedContacts);
        this.searchListViewAdapter.setUseUserCell(true);
        this.listViewAdapter = new ContactsAdapter(paramContext, 1, false, null, false);
        this.listViewAdapter.setCheckedMap(this.selectedContacts);
        this.fragmentView = new LinearLayout(paramContext);
        localObject1 = (LinearLayout)this.fragmentView;
        ((LinearLayout)localObject1).setOrientation(1);
        localObject2 = new FrameLayout(paramContext);
        ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(-1, -2));
        this.userSelectEditText = new EditText(paramContext);
        this.userSelectEditText.setTextSize(1, 16.0F);
        this.userSelectEditText.setHintTextColor(-6842473);
        this.userSelectEditText.setTextColor(-14606047);
        this.userSelectEditText.setInputType(655536);
        this.userSelectEditText.setMinimumHeight(AndroidUtilities.dp(54.0F));
        this.userSelectEditText.setSingleLine(false);
        this.userSelectEditText.setLines(2);
        this.userSelectEditText.setMaxLines(2);
        this.userSelectEditText.setVerticalScrollBarEnabled(true);
        this.userSelectEditText.setHorizontalScrollBarEnabled(false);
        this.userSelectEditText.setPadding(0, 0, 0, 0);
        this.userSelectEditText.setImeOptions(268435462);
        EditText localEditText = this.userSelectEditText;
        if (!LocaleController.isRTL) {
          break label888;
        }
        i = 5;
        label345:
        localEditText.setGravity(i | 0x10);
        AndroidUtilities.clearCursorDrawable(this.userSelectEditText);
        ((FrameLayout)localObject2).addView(this.userSelectEditText, LayoutHelper.createFrame(-1, -2.0F, 51, 10.0F, 0.0F, 10.0F, 0.0F));
        if (!this.isAlwaysShare) {
          break label912;
        }
        if (!this.isGroup) {
          break label893;
        }
        this.userSelectEditText.setHint(LocaleController.getString("AlwaysAllowPlaceholder", 2131165282));
        label417:
        this.userSelectEditText.setTextIsSelectable(false);
        this.userSelectEditText.addTextChangedListener(new TextWatcher()
        {
          public void afterTextChanged(Editable paramAnonymousEditable)
          {
            int i;
            if (!GroupCreateActivity.this.ignoreChange)
            {
              int j = 0;
              i = GroupCreateActivity.this.userSelectEditText.getSelectionEnd();
              if (paramAnonymousEditable.toString().length() >= GroupCreateActivity.this.changeString.toString().length()) {
                break label462;
              }
              paramAnonymousEditable = "";
              try
              {
                localObject = GroupCreateActivity.this.changeString.toString().substring(i, GroupCreateActivity.this.beforeChangeIndex);
                paramAnonymousEditable = (Editable)localObject;
              }
              catch (Exception localException)
              {
                for (;;)
                {
                  Object localObject;
                  FileLog.e("tmessages", localException);
                }
                if ((GroupCreateActivity.this.isAlwaysShare) || (GroupCreateActivity.this.isNeverShare)) {
                  break label288;
                }
                GroupCreateActivity.this.actionBar.setSubtitle(LocaleController.formatString("MembersCount", 2131165864, new Object[] { Integer.valueOf(GroupCreateActivity.this.selectedContacts.size()), Integer.valueOf(GroupCreateActivity.this.maxCount) }));
                GroupCreateActivity.this.listView.invalidateViews();
              }
              if (paramAnonymousEditable.length() <= 0) {
                break label457;
              }
              i = j;
              if (GroupCreateActivity.this.searching)
              {
                i = j;
                if (GroupCreateActivity.this.searchWas) {
                  i = 1;
                }
              }
              paramAnonymousEditable = GroupCreateActivity.this.userSelectEditText.getText();
              j = 0;
              while (j < GroupCreateActivity.this.allSpans.size())
              {
                localObject = (ChipSpan)GroupCreateActivity.this.allSpans.get(j);
                if (paramAnonymousEditable.getSpanStart(localObject) == -1)
                {
                  GroupCreateActivity.this.allSpans.remove(localObject);
                  GroupCreateActivity.this.selectedContacts.remove(Integer.valueOf(((ChipSpan)localObject).uid));
                }
                j += 1;
              }
            }
            for (;;)
            {
              label288:
              if (i != 0)
              {
                paramAnonymousEditable = GroupCreateActivity.this.userSelectEditText.getText().toString().replace("<", "");
                if (paramAnonymousEditable.length() == 0) {
                  break;
                }
                GroupCreateActivity.access$902(GroupCreateActivity.this, true);
                GroupCreateActivity.access$1002(GroupCreateActivity.this, true);
                if (GroupCreateActivity.this.listView != null)
                {
                  GroupCreateActivity.this.listView.setAdapter(GroupCreateActivity.this.searchListViewAdapter);
                  GroupCreateActivity.this.searchListViewAdapter.notifyDataSetChanged();
                  GroupCreateActivity.this.listView.setFastScrollAlwaysVisible(false);
                  GroupCreateActivity.this.listView.setFastScrollEnabled(false);
                  GroupCreateActivity.this.listView.setVerticalScrollBarEnabled(true);
                }
                if (GroupCreateActivity.this.emptyTextView != null) {
                  GroupCreateActivity.this.emptyTextView.setText(LocaleController.getString("NoResult", 2131165949));
                }
                GroupCreateActivity.this.searchListViewAdapter.searchDialogs(paramAnonymousEditable);
              }
              return;
              label457:
              i = 1;
              continue;
              label462:
              i = 1;
            }
            GroupCreateActivity.this.searchListViewAdapter.searchDialogs(null);
            GroupCreateActivity.access$902(GroupCreateActivity.this, false);
            GroupCreateActivity.access$1002(GroupCreateActivity.this, false);
            GroupCreateActivity.this.listView.setAdapter(GroupCreateActivity.this.listViewAdapter);
            GroupCreateActivity.this.listViewAdapter.notifyDataSetChanged();
            GroupCreateActivity.this.listView.setFastScrollAlwaysVisible(true);
            GroupCreateActivity.this.listView.setFastScrollEnabled(true);
            GroupCreateActivity.this.listView.setVerticalScrollBarEnabled(false);
            GroupCreateActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", 2131165932));
          }
          
          public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
          {
            if (!GroupCreateActivity.this.ignoreChange)
            {
              GroupCreateActivity.access$602(GroupCreateActivity.this, GroupCreateActivity.this.userSelectEditText.getSelectionStart());
              GroupCreateActivity.access$802(GroupCreateActivity.this, new SpannableString(paramAnonymousCharSequence));
            }
          }
          
          public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        });
        localObject2 = new LinearLayout(paramContext);
        ((LinearLayout)localObject2).setVisibility(4);
        ((LinearLayout)localObject2).setOrientation(1);
        ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(-1, -1));
        ((LinearLayout)localObject2).setOnTouchListener(new View.OnTouchListener()
        {
          public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
          {
            return true;
          }
        });
        this.emptyTextView = new TextView(paramContext);
        this.emptyTextView.setTextColor(-8355712);
        this.emptyTextView.setTextSize(20.0F);
        this.emptyTextView.setGravity(17);
        this.emptyTextView.setText(LocaleController.getString("NoContacts", 2131165932));
        ((LinearLayout)localObject2).addView(this.emptyTextView, LayoutHelper.createLinear(-1, -1, 0.5F));
        ((LinearLayout)localObject2).addView(new FrameLayout(paramContext), LayoutHelper.createLinear(-1, -1, 0.5F));
        this.listView = new LetterSectionsListView(paramContext);
        this.listView.setEmptyView((View)localObject2);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setFastScrollEnabled(true);
        this.listView.setScrollBarStyle(33554432);
        this.listView.setAdapter(this.listViewAdapter);
        this.listView.setFastScrollAlwaysVisible(true);
        paramContext = this.listView;
        if (!LocaleController.isRTL) {
          break label983;
        }
      }
    }
    label888:
    label893:
    label912:
    label983:
    for (int i = 1;; i = 2)
    {
      paramContext.setVerticalScrollbarPosition(i);
      ((LinearLayout)localObject1).addView(this.listView, LayoutHelper.createLinear(-1, -1));
      this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          if ((GroupCreateActivity.this.searching) && (GroupCreateActivity.this.searchWas))
          {
            paramAnonymousAdapterView = (TLRPC.User)GroupCreateActivity.this.searchListViewAdapter.getItem(paramAnonymousInt);
            if (paramAnonymousAdapterView != null) {
              break label94;
            }
          }
          label94:
          boolean bool;
          label589:
          label732:
          do
          {
            int i;
            do
            {
              return;
              i = GroupCreateActivity.this.listViewAdapter.getSectionForPosition(paramAnonymousInt);
              paramAnonymousInt = GroupCreateActivity.this.listViewAdapter.getPositionInSectionForPosition(paramAnonymousInt);
            } while ((paramAnonymousInt < 0) || (i < 0));
            paramAnonymousAdapterView = (TLRPC.User)GroupCreateActivity.this.listViewAdapter.getItem(i, paramAnonymousInt);
            break;
            bool = true;
            if (GroupCreateActivity.this.selectedContacts.containsKey(Integer.valueOf(paramAnonymousAdapterView.id))) {
              bool = false;
            }
            for (;;)
            {
              try
              {
                Object localObject = (ChipSpan)GroupCreateActivity.this.selectedContacts.get(Integer.valueOf(paramAnonymousAdapterView.id));
                GroupCreateActivity.this.selectedContacts.remove(Integer.valueOf(paramAnonymousAdapterView.id));
                paramAnonymousAdapterView = new SpannableStringBuilder(GroupCreateActivity.this.userSelectEditText.getText());
                paramAnonymousAdapterView.delete(paramAnonymousAdapterView.getSpanStart(localObject), paramAnonymousAdapterView.getSpanEnd(localObject));
                GroupCreateActivity.this.allSpans.remove(localObject);
                GroupCreateActivity.access$502(GroupCreateActivity.this, true);
                GroupCreateActivity.this.userSelectEditText.setText(paramAnonymousAdapterView);
                GroupCreateActivity.this.userSelectEditText.setSelection(paramAnonymousAdapterView.length());
                GroupCreateActivity.access$502(GroupCreateActivity.this, false);
                if ((!GroupCreateActivity.this.isAlwaysShare) && (!GroupCreateActivity.this.isNeverShare)) {
                  GroupCreateActivity.this.actionBar.setSubtitle(LocaleController.formatString("MembersCount", 2131165864, new Object[] { Integer.valueOf(GroupCreateActivity.this.selectedContacts.size()), Integer.valueOf(GroupCreateActivity.this.maxCount) }));
                }
                if ((!GroupCreateActivity.this.searching) && (!GroupCreateActivity.this.searchWas)) {
                  break label732;
                }
                GroupCreateActivity.access$502(GroupCreateActivity.this, true);
                paramAnonymousAdapterView = new SpannableStringBuilder("");
                paramAnonymousView = GroupCreateActivity.this.allSpans.iterator();
                if (!paramAnonymousView.hasNext()) {
                  break label589;
                }
                localObject = (ImageSpan)paramAnonymousView.next();
                paramAnonymousAdapterView.append("<<");
                paramAnonymousAdapterView.setSpan(localObject, paramAnonymousAdapterView.length() - 2, paramAnonymousAdapterView.length(), 33);
                continue;
              }
              catch (Exception paramAnonymousAdapterView)
              {
                FileLog.e("tmessages", paramAnonymousAdapterView);
                continue;
              }
              if ((GroupCreateActivity.this.maxCount != 0) && (GroupCreateActivity.this.selectedContacts.size() == GroupCreateActivity.this.maxCount)) {
                break;
              }
              if ((GroupCreateActivity.this.chatType == 0) && (GroupCreateActivity.this.selectedContacts.size() == MessagesController.getInstance().maxGroupCount))
              {
                paramAnonymousAdapterView = new AlertDialog.Builder(GroupCreateActivity.this.getParentActivity());
                paramAnonymousAdapterView.setTitle(LocaleController.getString("AppName", 2131165299));
                paramAnonymousAdapterView.setMessage(LocaleController.getString("SoftUserLimitAlert", 2131166301));
                paramAnonymousAdapterView.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
                GroupCreateActivity.this.showDialog(paramAnonymousAdapterView.create());
                return;
              }
              GroupCreateActivity.access$502(GroupCreateActivity.this, true);
              GroupCreateActivity.this.createAndPutChipForUser(paramAnonymousAdapterView).uid = paramAnonymousAdapterView.id;
              GroupCreateActivity.access$502(GroupCreateActivity.this, false);
            }
            GroupCreateActivity.this.userSelectEditText.setText(paramAnonymousAdapterView);
            GroupCreateActivity.this.userSelectEditText.setSelection(paramAnonymousAdapterView.length());
            GroupCreateActivity.access$502(GroupCreateActivity.this, false);
            GroupCreateActivity.this.searchListViewAdapter.searchDialogs(null);
            GroupCreateActivity.access$902(GroupCreateActivity.this, false);
            GroupCreateActivity.access$1002(GroupCreateActivity.this, false);
            GroupCreateActivity.this.listView.setAdapter(GroupCreateActivity.this.listViewAdapter);
            GroupCreateActivity.this.listViewAdapter.notifyDataSetChanged();
            GroupCreateActivity.this.listView.setFastScrollAlwaysVisible(true);
            GroupCreateActivity.this.listView.setFastScrollEnabled(true);
            GroupCreateActivity.this.listView.setVerticalScrollBarEnabled(false);
            GroupCreateActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", 2131165932));
            return;
          } while (!(paramAnonymousView instanceof UserCell));
          ((UserCell)paramAnonymousView).setChecked(bool, true);
        }
      });
      this.listView.setOnScrollListener(new AbsListView.OnScrollListener()
      {
        public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          if (paramAnonymousAbsListView.isFastScrollEnabled()) {
            AndroidUtilities.clearDrawableAnimation(paramAnonymousAbsListView);
          }
        }
        
        public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt)
        {
          boolean bool = true;
          if (paramAnonymousInt == 1) {
            AndroidUtilities.hideKeyboard(GroupCreateActivity.this.userSelectEditText);
          }
          if (GroupCreateActivity.this.listViewAdapter != null)
          {
            paramAnonymousAbsListView = GroupCreateActivity.this.listViewAdapter;
            if (paramAnonymousInt == 0) {
              break label45;
            }
          }
          for (;;)
          {
            paramAnonymousAbsListView.setIsScrolling(bool);
            return;
            label45:
            bool = false;
          }
        }
      });
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", 2131165285));
      break;
      if (this.isNeverShare)
      {
        if (this.isGroup)
        {
          this.actionBar.setTitle(LocaleController.getString("NeverAllow", 2131165910));
          break;
        }
        this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", 2131165914));
        break;
      }
      localObject2 = this.actionBar;
      if (this.chatType == 0) {}
      for (localObject1 = LocaleController.getString("NewGroup", 2131165917);; localObject1 = LocaleController.getString("NewBroadcastList", 2131165915))
      {
        ((ActionBar)localObject2).setTitle((CharSequence)localObject1);
        this.actionBar.setSubtitle(LocaleController.formatString("MembersCount", 2131165864, new Object[] { Integer.valueOf(this.selectedContacts.size()), Integer.valueOf(this.maxCount) }));
        break;
      }
      i = 3;
      break label345;
      this.userSelectEditText.setHint(LocaleController.getString("AlwaysShareWithPlaceholder", 2131165284));
      break label417;
      if (this.isNeverShare)
      {
        if (this.isGroup)
        {
          this.userSelectEditText.setHint(LocaleController.getString("NeverAllowPlaceholder", 2131165911));
          break label417;
        }
        this.userSelectEditText.setHint(LocaleController.getString("NeverShareWithPlaceholder", 2131165913));
        break label417;
      }
      this.userSelectEditText.setHint(LocaleController.getString("SendMessageTo", 2131166241));
      break label417;
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.contactsDidLoaded) {
      if (this.listViewAdapter != null) {
        this.listViewAdapter.notifyDataSetChanged();
      }
    }
    do
    {
      do
      {
        return;
        if (paramInt != NotificationCenter.updateInterfaces) {
          break;
        }
        paramInt = ((Integer)paramVarArgs[0]).intValue();
      } while (((paramInt & 0x2) == 0) && ((paramInt & 0x1) == 0) && ((paramInt & 0x4) == 0));
      updateVisibleRows(paramInt);
      return;
    } while (paramInt != NotificationCenter.chatDidCreated);
    removeSelfFromStack();
  }
  
  public boolean onFragmentCreate()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidCreated);
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidCreated);
  }
  
  public void setDelegate(GroupCreateActivityDelegate paramGroupCreateActivityDelegate)
  {
    this.delegate = paramGroupCreateActivityDelegate;
  }
  
  public static abstract interface GroupCreateActivityDelegate
  {
    public abstract void didSelectUsers(ArrayList<Integer> paramArrayList);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/GroupCreateActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */