package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.GreySectionCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;

public class GroupCreateFinalActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, AvatarUpdater.AvatarUpdaterDelegate
{
  private static final int done_button = 1;
  private TLRPC.FileLocation avatar;
  private AvatarDrawable avatarDrawable;
  private BackupImageView avatarImage;
  private AvatarUpdater avatarUpdater = new AvatarUpdater();
  private int chatType = 0;
  private boolean createAfterUpload;
  private boolean donePressed;
  private ListAdapter listAdapter;
  private ListView listView;
  private EditText nameTextView;
  private String nameToSet = null;
  private ProgressDialog progressDialog = null;
  private ArrayList<Integer> selectedContacts;
  private TLRPC.InputFile uploadedAvatar;
  
  public GroupCreateFinalActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.chatType = paramBundle.getInt("chatType", 0);
    this.avatarDrawable = new AvatarDrawable();
  }
  
  private void updateVisibleRows(int paramInt)
  {
    if (this.listView == null) {}
    for (;;)
    {
      return;
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
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    LinearLayout localLinearLayout;
    Object localObject;
    boolean bool;
    if (this.chatType == 1)
    {
      this.actionBar.setTitle(LocaleController.getString("NewBroadcastList", 2131165915));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(final int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            GroupCreateFinalActivity.this.finishFragment();
          }
          while ((paramAnonymousInt != 1) || (GroupCreateFinalActivity.this.donePressed) || (GroupCreateFinalActivity.this.nameTextView.getText().length() == 0)) {
            return;
          }
          GroupCreateFinalActivity.access$002(GroupCreateFinalActivity.this, true);
          if (GroupCreateFinalActivity.this.chatType == 1)
          {
            MessagesController.getInstance().createChat(GroupCreateFinalActivity.this.nameTextView.getText().toString(), GroupCreateFinalActivity.this.selectedContacts, null, GroupCreateFinalActivity.this.chatType, GroupCreateFinalActivity.this);
            return;
          }
          if (GroupCreateFinalActivity.this.avatarUpdater.uploadingAvatar != null)
          {
            GroupCreateFinalActivity.access$502(GroupCreateFinalActivity.this, true);
            return;
          }
          GroupCreateFinalActivity.access$602(GroupCreateFinalActivity.this, new ProgressDialog(GroupCreateFinalActivity.this.getParentActivity()));
          GroupCreateFinalActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", 2131165834));
          GroupCreateFinalActivity.this.progressDialog.setCanceledOnTouchOutside(false);
          GroupCreateFinalActivity.this.progressDialog.setCancelable(false);
          paramAnonymousInt = MessagesController.getInstance().createChat(GroupCreateFinalActivity.this.nameTextView.getText().toString(), GroupCreateFinalActivity.this.selectedContacts, null, GroupCreateFinalActivity.this.chatType, GroupCreateFinalActivity.this);
          GroupCreateFinalActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", 2131165386), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              ConnectionsManager.getInstance().cancelRequest(paramAnonymousInt, true);
              GroupCreateFinalActivity.access$002(GroupCreateFinalActivity.this, false);
              try
              {
                paramAnonymous2DialogInterface.dismiss();
                return;
              }
              catch (Exception paramAnonymous2DialogInterface)
              {
                FileLog.e("tmessages", paramAnonymous2DialogInterface);
              }
            }
          });
          GroupCreateFinalActivity.this.progressDialog.show();
        }
      });
      this.actionBar.createMenu().addItemWithWidth(1, 2130837720, AndroidUtilities.dp(56.0F));
      this.fragmentView = new LinearLayout(paramContext);
      localLinearLayout = (LinearLayout)this.fragmentView;
      localLinearLayout.setOrientation(1);
      FrameLayout localFrameLayout = new FrameLayout(paramContext);
      localLinearLayout.addView(localFrameLayout);
      localObject = (LinearLayout.LayoutParams)localFrameLayout.getLayoutParams();
      ((LinearLayout.LayoutParams)localObject).width = -1;
      ((LinearLayout.LayoutParams)localObject).height = -2;
      ((LinearLayout.LayoutParams)localObject).gravity = 51;
      localFrameLayout.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.avatarImage = new BackupImageView(paramContext);
      this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0F));
      localObject = this.avatarDrawable;
      if (this.chatType != 1) {
        break label838;
      }
      bool = true;
      label194:
      ((AvatarDrawable)localObject).setInfo(5, null, null, bool);
      this.avatarImage.setImageDrawable(this.avatarDrawable);
      localFrameLayout.addView(this.avatarImage);
      localObject = (FrameLayout.LayoutParams)this.avatarImage.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).width = AndroidUtilities.dp(64.0F);
      ((FrameLayout.LayoutParams)localObject).height = AndroidUtilities.dp(64.0F);
      ((FrameLayout.LayoutParams)localObject).topMargin = AndroidUtilities.dp(12.0F);
      ((FrameLayout.LayoutParams)localObject).bottomMargin = AndroidUtilities.dp(12.0F);
      if (!LocaleController.isRTL) {
        break label843;
      }
      i = 0;
      label287:
      ((FrameLayout.LayoutParams)localObject).leftMargin = i;
      if (!LocaleController.isRTL) {
        break label853;
      }
      i = AndroidUtilities.dp(16.0F);
      label306:
      ((FrameLayout.LayoutParams)localObject).rightMargin = i;
      if (!LocaleController.isRTL) {
        break label858;
      }
      i = 5;
      label320:
      ((FrameLayout.LayoutParams)localObject).gravity = (i | 0x30);
      this.avatarImage.setLayoutParams((ViewGroup.LayoutParams)localObject);
      if (this.chatType != 1)
      {
        this.avatarDrawable.setDrawPhoto(true);
        this.avatarImage.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (GroupCreateFinalActivity.this.getParentActivity() == null) {
              return;
            }
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(GroupCreateFinalActivity.this.getParentActivity());
            if (GroupCreateFinalActivity.this.avatar != null)
            {
              paramAnonymousView = new CharSequence[3];
              paramAnonymousView[0] = LocaleController.getString("FromCamera", 2131165703);
              paramAnonymousView[1] = LocaleController.getString("FromGalley", 2131165710);
              paramAnonymousView[2] = LocaleController.getString("DeletePhoto", 2131165576);
            }
            for (;;)
            {
              localBuilder.setItems(paramAnonymousView, new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  if (paramAnonymous2Int == 0) {
                    GroupCreateFinalActivity.this.avatarUpdater.openCamera();
                  }
                  do
                  {
                    return;
                    if (paramAnonymous2Int == 1)
                    {
                      GroupCreateFinalActivity.this.avatarUpdater.openGallery();
                      return;
                    }
                  } while (paramAnonymous2Int != 2);
                  GroupCreateFinalActivity.access$702(GroupCreateFinalActivity.this, null);
                  GroupCreateFinalActivity.access$802(GroupCreateFinalActivity.this, null);
                  GroupCreateFinalActivity.this.avatarImage.setImage(GroupCreateFinalActivity.this.avatar, "50_50", GroupCreateFinalActivity.this.avatarDrawable);
                }
              });
              GroupCreateFinalActivity.this.showDialog(localBuilder.create());
              return;
              paramAnonymousView = new CharSequence[2];
              paramAnonymousView[0] = LocaleController.getString("FromCamera", 2131165703);
              paramAnonymousView[1] = LocaleController.getString("FromGalley", 2131165710);
            }
          }
        });
      }
      this.nameTextView = new EditText(paramContext);
      EditText localEditText = this.nameTextView;
      if (this.chatType != 0) {
        break label863;
      }
      localObject = LocaleController.getString("EnterGroupNamePlaceholder", 2131165621);
      label405:
      localEditText.setHint((CharSequence)localObject);
      if (this.nameToSet != null)
      {
        this.nameTextView.setText(this.nameToSet);
        this.nameToSet = null;
      }
      this.nameTextView.setMaxLines(4);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label877;
      }
      i = 5;
      label457:
      ((EditText)localObject).setGravity(i | 0x10);
      this.nameTextView.setTextSize(1, 16.0F);
      this.nameTextView.setHintTextColor(-6842473);
      this.nameTextView.setImeOptions(268435456);
      this.nameTextView.setInputType(16384);
      this.nameTextView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0F));
      localObject = new InputFilter.LengthFilter(100);
      this.nameTextView.setFilters(new InputFilter[] { localObject });
      AndroidUtilities.clearCursorDrawable(this.nameTextView);
      this.nameTextView.setTextColor(-14606047);
      localFrameLayout.addView(this.nameTextView);
      localObject = (FrameLayout.LayoutParams)this.nameTextView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).width = -1;
      ((FrameLayout.LayoutParams)localObject).height = -2;
      if (!LocaleController.isRTL) {
        break label882;
      }
      i = AndroidUtilities.dp(16.0F);
      label614:
      ((FrameLayout.LayoutParams)localObject).leftMargin = i;
      if (!LocaleController.isRTL) {
        break label892;
      }
    }
    label838:
    label843:
    label853:
    label858:
    label863:
    label877:
    label882:
    label892:
    for (int i = AndroidUtilities.dp(96.0F);; i = AndroidUtilities.dp(16.0F))
    {
      ((FrameLayout.LayoutParams)localObject).rightMargin = i;
      ((FrameLayout.LayoutParams)localObject).gravity = 16;
      this.nameTextView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      if (this.chatType != 1) {
        this.nameTextView.addTextChangedListener(new TextWatcher()
        {
          public void afterTextChanged(Editable paramAnonymousEditable)
          {
            AvatarDrawable localAvatarDrawable = GroupCreateFinalActivity.this.avatarDrawable;
            if (GroupCreateFinalActivity.this.nameTextView.length() > 0) {}
            for (paramAnonymousEditable = GroupCreateFinalActivity.this.nameTextView.getText().toString();; paramAnonymousEditable = null)
            {
              localAvatarDrawable.setInfo(5, paramAnonymousEditable, null, false);
              GroupCreateFinalActivity.this.avatarImage.invalidate();
              return;
            }
          }
          
          public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
          
          public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        });
      }
      localObject = new GreySectionCell(paramContext);
      ((GreySectionCell)localObject).setText(LocaleController.formatPluralString("Members", this.selectedContacts.size()));
      localLinearLayout.addView((View)localObject);
      this.listView = new ListView(paramContext);
      this.listView.setDivider(null);
      this.listView.setDividerHeight(0);
      this.listView.setVerticalScrollBarEnabled(false);
      localObject = this.listView;
      paramContext = new ListAdapter(paramContext);
      this.listAdapter = paramContext;
      ((ListView)localObject).setAdapter(paramContext);
      localLinearLayout.addView(this.listView);
      paramContext = (LinearLayout.LayoutParams)this.listView.getLayoutParams();
      paramContext.width = -1;
      paramContext.height = -1;
      this.listView.setLayoutParams(paramContext);
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("NewGroup", 2131165917));
      break;
      bool = false;
      break label194;
      i = AndroidUtilities.dp(16.0F);
      break label287;
      i = 0;
      break label306;
      i = 3;
      break label320;
      localObject = LocaleController.getString("EnterListName", 2131165622);
      break label405;
      i = 3;
      break label457;
      i = AndroidUtilities.dp(96.0F);
      break label614;
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.updateInterfaces)
    {
      paramInt = ((Integer)paramVarArgs[0]).intValue();
      if (((paramInt & 0x2) != 0) || ((paramInt & 0x1) != 0) || ((paramInt & 0x4) != 0)) {
        updateVisibleRows(paramInt);
      }
    }
    for (;;)
    {
      return;
      if (paramInt == NotificationCenter.chatDidFailCreate)
      {
        if (this.progressDialog != null) {}
        try
        {
          this.progressDialog.dismiss();
          this.donePressed = false;
          return;
        }
        catch (Exception paramVarArgs)
        {
          for (;;)
          {
            FileLog.e("tmessages", paramVarArgs);
          }
        }
      }
      if (paramInt != NotificationCenter.chatDidCreated) {
        continue;
      }
      if (this.progressDialog != null) {}
      try
      {
        this.progressDialog.dismiss();
        paramInt = ((Integer)paramVarArgs[0]).intValue();
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        paramVarArgs = new Bundle();
        paramVarArgs.putInt("chat_id", paramInt);
        presentFragment(new ChatActivity(paramVarArgs), true);
        if (this.uploadedAvatar == null) {
          continue;
        }
        MessagesController.getInstance().changeChatAvatar(paramInt, this.uploadedAvatar);
        return;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
      }
    }
  }
  
  public void didUploadedPhoto(final TLRPC.InputFile paramInputFile, final TLRPC.PhotoSize paramPhotoSize1, TLRPC.PhotoSize paramPhotoSize2)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        GroupCreateFinalActivity.access$802(GroupCreateFinalActivity.this, paramInputFile);
        GroupCreateFinalActivity.access$702(GroupCreateFinalActivity.this, paramPhotoSize1.location);
        GroupCreateFinalActivity.this.avatarImage.setImage(GroupCreateFinalActivity.this.avatar, "50_50", GroupCreateFinalActivity.this.avatarDrawable);
        if (GroupCreateFinalActivity.this.createAfterUpload)
        {
          FileLog.e("tmessages", "avatar did uploaded");
          MessagesController.getInstance().createChat(GroupCreateFinalActivity.this.nameTextView.getText().toString(), GroupCreateFinalActivity.this.selectedContacts, null, GroupCreateFinalActivity.this.chatType, GroupCreateFinalActivity.this);
        }
      }
    });
  }
  
  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.avatarUpdater.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public boolean onFragmentCreate()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidCreated);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidFailCreate);
    this.avatarUpdater.parentFragment = this;
    this.avatarUpdater.delegate = this;
    this.selectedContacts = getArguments().getIntegerArrayList("result");
    final Object localObject1 = new ArrayList();
    final Object localObject2 = this.selectedContacts.iterator();
    final Object localObject3;
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (Integer)((Iterator)localObject2).next();
      if (MessagesController.getInstance().getUser((Integer)localObject3) == null) {
        ((ArrayList)localObject1).add(localObject3);
      }
    }
    if (!((ArrayList)localObject1).isEmpty())
    {
      localObject3 = new Semaphore(0);
      localObject2 = new ArrayList();
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          localObject2.addAll(MessagesStorage.getInstance().getUsers(localObject1));
          localObject3.release();
        }
      });
      try
      {
        ((Semaphore)localObject3).acquire();
        if (((ArrayList)localObject1).size() != ((ArrayList)localObject2).size()) {
          return false;
        }
      }
      catch (Exception localException)
      {
        do
        {
          for (;;)
          {
            FileLog.e("tmessages", localException);
          }
        } while (((ArrayList)localObject2).isEmpty());
        localObject1 = ((ArrayList)localObject2).iterator();
      }
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (TLRPC.User)((Iterator)localObject1).next();
        MessagesController.getInstance().putUser((TLRPC.User)localObject2, true);
      }
    }
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidCreated);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidFailCreate);
    this.avatarUpdater.clear();
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
  }
  
  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      this.nameTextView.requestFocus();
      AndroidUtilities.showKeyboard(this.nameTextView);
    }
  }
  
  public void restoreSelfArgs(Bundle paramBundle)
  {
    if (this.avatarUpdater != null) {
      this.avatarUpdater.currentPicturePath = paramBundle.getString("path");
    }
    paramBundle = paramBundle.getString("nameTextView");
    if (paramBundle != null)
    {
      if (this.nameTextView != null) {
        this.nameTextView.setText(paramBundle);
      }
    }
    else {
      return;
    }
    this.nameToSet = paramBundle;
  }
  
  public void saveSelfArgs(Bundle paramBundle)
  {
    if ((this.avatarUpdater != null) && (this.avatarUpdater.currentPicturePath != null)) {
      paramBundle.putString("path", this.avatarUpdater.currentPicturePath);
    }
    if (this.nameTextView != null)
    {
      String str = this.nameTextView.getText().toString();
      if ((str != null) && (str.length() != 0)) {
        paramBundle.putString("nameTextView", str);
      }
    }
  }
  
  private class ListAdapter
    extends BaseFragmentAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public boolean areAllItemsEnabled()
    {
      return false;
    }
    
    public int getCount()
    {
      return GroupCreateFinalActivity.this.selectedContacts.size();
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      paramViewGroup = paramView;
      if (paramView == null) {
        paramViewGroup = new UserCell(this.mContext, 1, 0, false);
      }
      paramView = MessagesController.getInstance().getUser((Integer)GroupCreateFinalActivity.this.selectedContacts.get(paramInt));
      ((UserCell)paramViewGroup).setData(paramView, null, null, 0);
      return paramViewGroup;
    }
    
    public int getViewTypeCount()
    {
      return 1;
    }
    
    public boolean isEnabled(int paramInt)
    {
      return false;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/GroupCreateFinalActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */