package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.GroupCreateSectionCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.GroupCreateDividerItemDecoration;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class GroupCreateFinalActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, AvatarUpdater.AvatarUpdaterDelegate
{
  private static final int done_button = 1;
  private GroupCreateAdapter adapter;
  private TLRPC.FileLocation avatar;
  private AvatarDrawable avatarDrawable;
  private BackupImageView avatarImage;
  private AvatarUpdater avatarUpdater = new AvatarUpdater();
  private int chatType = 0;
  private boolean createAfterUpload;
  private ActionBarMenuItem doneItem;
  private AnimatorSet doneItemAnimation;
  private boolean donePressed;
  private EditTextBoldCursor editText;
  private FrameLayout editTextContainer;
  private RecyclerView listView;
  private String nameToSet;
  private ContextProgressView progressView;
  private int reqId;
  private ArrayList<Integer> selectedContacts;
  private TLRPC.InputFile uploadedAvatar;
  
  public GroupCreateFinalActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.chatType = paramBundle.getInt("chatType", 0);
    this.avatarDrawable = new AvatarDrawable();
  }
  
  private void showEditDoneProgress(final boolean paramBoolean)
  {
    if (this.doneItem == null) {
      return;
    }
    if (this.doneItemAnimation != null) {
      this.doneItemAnimation.cancel();
    }
    this.doneItemAnimation = new AnimatorSet();
    if (paramBoolean)
    {
      this.progressView.setVisibility(0);
      this.doneItem.setEnabled(false);
      this.doneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[] { 1.0F }) });
    }
    for (;;)
    {
      this.doneItemAnimation.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if ((GroupCreateFinalActivity.this.doneItemAnimation != null) && (GroupCreateFinalActivity.this.doneItemAnimation.equals(paramAnonymousAnimator))) {
            GroupCreateFinalActivity.access$1802(GroupCreateFinalActivity.this, null);
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((GroupCreateFinalActivity.this.doneItemAnimation != null) && (GroupCreateFinalActivity.this.doneItemAnimation.equals(paramAnonymousAnimator)))
          {
            if (paramBoolean) {
              break label43;
            }
            GroupCreateFinalActivity.this.progressView.setVisibility(4);
          }
          for (;;)
          {
            return;
            label43:
            GroupCreateFinalActivity.this.doneItem.getImageView().setVisibility(4);
          }
        }
      });
      this.doneItemAnimation.setDuration(150L);
      this.doneItemAnimation.start();
      break;
      this.doneItem.getImageView().setVisibility(0);
      this.doneItem.setEnabled(true);
      this.doneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[] { 1.0F }) });
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("NewGroup", NUM));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          GroupCreateFinalActivity.this.finishFragment();
        }
        for (;;)
        {
          return;
          if ((paramAnonymousInt == 1) && (!GroupCreateFinalActivity.this.donePressed)) {
            if (GroupCreateFinalActivity.this.editText.length() == 0)
            {
              Vibrator localVibrator = (Vibrator)GroupCreateFinalActivity.this.getParentActivity().getSystemService("vibrator");
              if (localVibrator != null) {
                localVibrator.vibrate(200L);
              }
              AndroidUtilities.shakeView(GroupCreateFinalActivity.this.editText, 2.0F, 0);
            }
            else
            {
              GroupCreateFinalActivity.access$102(GroupCreateFinalActivity.this, true);
              AndroidUtilities.hideKeyboard(GroupCreateFinalActivity.this.editText);
              GroupCreateFinalActivity.this.editText.setEnabled(false);
              if (GroupCreateFinalActivity.this.avatarUpdater.uploadingAvatar != null)
              {
                GroupCreateFinalActivity.access$402(GroupCreateFinalActivity.this, true);
              }
              else
              {
                GroupCreateFinalActivity.this.showEditDoneProgress(true);
                GroupCreateFinalActivity.access$602(GroupCreateFinalActivity.this, MessagesController.getInstance(GroupCreateFinalActivity.this.currentAccount).createChat(GroupCreateFinalActivity.this.editText.getText().toString(), GroupCreateFinalActivity.this.selectedContacts, null, GroupCreateFinalActivity.this.chatType, GroupCreateFinalActivity.this));
              }
            }
          }
        }
      }
    });
    this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0F));
    this.progressView = new ContextProgressView(paramContext, 1);
    this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0F));
    this.progressView.setVisibility(4);
    this.fragmentView = new LinearLayout(paramContext)
    {
      protected boolean drawChild(Canvas paramAnonymousCanvas, View paramAnonymousView, long paramAnonymousLong)
      {
        boolean bool = super.drawChild(paramAnonymousCanvas, paramAnonymousView, paramAnonymousLong);
        if (paramAnonymousView == GroupCreateFinalActivity.this.listView) {
          GroupCreateFinalActivity.this.parentLayout.drawHeaderShadow(paramAnonymousCanvas, GroupCreateFinalActivity.this.editTextContainer.getMeasuredHeight());
        }
        return bool;
      }
    };
    LinearLayout localLinearLayout = (LinearLayout)this.fragmentView;
    localLinearLayout.setOrientation(1);
    this.editTextContainer = new FrameLayout(paramContext);
    localLinearLayout.addView(this.editTextContainer, LayoutHelper.createLinear(-1, -2));
    this.avatarImage = new BackupImageView(paramContext);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0F));
    Object localObject1 = this.avatarDrawable;
    boolean bool;
    label242:
    float f1;
    label251:
    float f2;
    if (this.chatType == 1)
    {
      bool = true;
      ((AvatarDrawable)localObject1).setInfo(5, null, null, bool);
      this.avatarImage.setImageDrawable(this.avatarDrawable);
      Object localObject2 = this.editTextContainer;
      localObject1 = this.avatarImage;
      if (!LocaleController.isRTL) {
        break label763;
      }
      i = 5;
      if (!LocaleController.isRTL) {
        break label769;
      }
      f1 = 0.0F;
      if (!LocaleController.isRTL) {
        break label777;
      }
      f2 = 16.0F;
      label262:
      ((FrameLayout)localObject2).addView((View)localObject1, LayoutHelper.createFrame(64, 64.0F, i | 0x30, f1, 16.0F, f2, 16.0F));
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
            paramAnonymousView[0] = LocaleController.getString("FromCamera", NUM);
            paramAnonymousView[1] = LocaleController.getString("FromGalley", NUM);
            paramAnonymousView[2] = LocaleController.getString("DeletePhoto", NUM);
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
                for (;;)
                {
                  return;
                  if (paramAnonymous2Int == 1)
                  {
                    GroupCreateFinalActivity.this.avatarUpdater.openGallery();
                  }
                  else if (paramAnonymous2Int == 2)
                  {
                    GroupCreateFinalActivity.access$1302(GroupCreateFinalActivity.this, null);
                    GroupCreateFinalActivity.access$1402(GroupCreateFinalActivity.this, null);
                    GroupCreateFinalActivity.this.avatarImage.setImage(GroupCreateFinalActivity.this.avatar, "50_50", GroupCreateFinalActivity.this.avatarDrawable);
                  }
                }
              }
            });
            GroupCreateFinalActivity.this.showDialog(localBuilder.create());
            break;
            paramAnonymousView = new CharSequence[2];
            paramAnonymousView[0] = LocaleController.getString("FromCamera", NUM);
            paramAnonymousView[1] = LocaleController.getString("FromGalley", NUM);
          }
        }
      });
      this.editText = new EditTextBoldCursor(paramContext);
      localObject2 = this.editText;
      if (this.chatType != 0) {
        break label783;
      }
      localObject1 = LocaleController.getString("EnterGroupNamePlaceholder", NUM);
      label349:
      ((EditTextBoldCursor)localObject2).setHint((CharSequence)localObject1);
      if (this.nameToSet != null)
      {
        this.editText.setText(this.nameToSet);
        this.nameToSet = null;
      }
      this.editText.setMaxLines(4);
      localObject1 = this.editText;
      if (!LocaleController.isRTL) {
        break label796;
      }
      i = 5;
      label400:
      ((EditTextBoldCursor)localObject1).setGravity(i | 0x10);
      this.editText.setTextSize(1, 18.0F);
      this.editText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
      this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.editText.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
      this.editText.setImeOptions(268435456);
      this.editText.setInputType(16384);
      this.editText.setPadding(0, 0, 0, AndroidUtilities.dp(8.0F));
      localObject1 = new InputFilter.LengthFilter(100);
      this.editText.setFilters(new InputFilter[] { localObject1 });
      this.editText.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.editText.setCursorSize(AndroidUtilities.dp(20.0F));
      this.editText.setCursorWidth(1.5F);
      localObject2 = this.editTextContainer;
      localObject1 = this.editText;
      if (!LocaleController.isRTL) {
        break label802;
      }
      f1 = 16.0F;
      label577:
      if (!LocaleController.isRTL) {
        break label810;
      }
      f2 = 96.0F;
      label588:
      ((FrameLayout)localObject2).addView((View)localObject1, LayoutHelper.createFrame(-1, -2.0F, 16, f1, 0.0F, f2, 0.0F));
      this.editText.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable)
        {
          AvatarDrawable localAvatarDrawable = GroupCreateFinalActivity.this.avatarDrawable;
          if (GroupCreateFinalActivity.this.editText.length() > 0) {}
          for (paramAnonymousEditable = GroupCreateFinalActivity.this.editText.getText().toString();; paramAnonymousEditable = null)
          {
            localAvatarDrawable.setInfo(5, paramAnonymousEditable, null, false);
            GroupCreateFinalActivity.this.avatarImage.invalidate();
            return;
          }
        }
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      });
      localObject2 = new LinearLayoutManager(paramContext, 1, false);
      this.listView = new RecyclerListView(paramContext);
      localObject1 = this.listView;
      paramContext = new GroupCreateAdapter(paramContext);
      this.adapter = paramContext;
      ((RecyclerView)localObject1).setAdapter(paramContext);
      this.listView.setLayoutManager((RecyclerView.LayoutManager)localObject2);
      this.listView.setVerticalScrollBarEnabled(false);
      paramContext = this.listView;
      if (!LocaleController.isRTL) {
        break label818;
      }
    }
    label763:
    label769:
    label777:
    label783:
    label796:
    label802:
    label810:
    label818:
    for (int i = 1;; i = 2)
    {
      paramContext.setVerticalScrollbarPosition(i);
      this.listView.addItemDecoration(new GroupCreateDividerItemDecoration());
      localLinearLayout.addView(this.listView, LayoutHelper.createLinear(-1, -1));
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
        {
          if (paramAnonymousInt == 1) {
            AndroidUtilities.hideKeyboard(GroupCreateFinalActivity.this.editText);
          }
        }
      });
      return this.fragmentView;
      bool = false;
      break;
      i = 3;
      break label242;
      f1 = 16.0F;
      break label251;
      f2 = 0.0F;
      break label262;
      localObject1 = LocaleController.getString("EnterListName", NUM);
      break label349;
      i = 3;
      break label400;
      f1 = 96.0F;
      break label577;
      f2 = 16.0F;
      break label588;
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.updateInterfaces) {
      if (this.listView != null) {}
    }
    for (;;)
    {
      return;
      int i = ((Integer)paramVarArgs[0]).intValue();
      if (((i & 0x2) != 0) || ((i & 0x1) != 0) || ((i & 0x4) != 0))
      {
        paramInt2 = this.listView.getChildCount();
        for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
        {
          paramVarArgs = this.listView.getChildAt(paramInt1);
          if ((paramVarArgs instanceof GroupCreateUserCell)) {
            ((GroupCreateUserCell)paramVarArgs).update(i);
          }
        }
        continue;
        if (paramInt1 == NotificationCenter.chatDidFailCreate)
        {
          this.reqId = 0;
          this.donePressed = false;
          showEditDoneProgress(false);
          if (this.editText != null) {
            this.editText.setEnabled(true);
          }
        }
        else if (paramInt1 == NotificationCenter.chatDidCreated)
        {
          this.reqId = 0;
          paramInt1 = ((Integer)paramVarArgs[0]).intValue();
          NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
          paramVarArgs = new Bundle();
          paramVarArgs.putInt("chat_id", paramInt1);
          presentFragment(new ChatActivity(paramVarArgs), true);
          if (this.uploadedAvatar != null) {
            MessagesController.getInstance(this.currentAccount).changeChatAvatar(paramInt1, this.uploadedAvatar);
          }
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
        GroupCreateFinalActivity.access$1402(GroupCreateFinalActivity.this, paramInputFile);
        GroupCreateFinalActivity.access$1302(GroupCreateFinalActivity.this, paramPhotoSize1.location);
        GroupCreateFinalActivity.this.avatarImage.setImage(GroupCreateFinalActivity.this.avatar, "50_50", GroupCreateFinalActivity.this.avatarDrawable);
        if (GroupCreateFinalActivity.this.createAfterUpload) {
          MessagesController.getInstance(GroupCreateFinalActivity.this.currentAccount).createChat(GroupCreateFinalActivity.this.editText.getText().toString(), GroupCreateFinalActivity.this.selectedContacts, null, GroupCreateFinalActivity.this.chatType, GroupCreateFinalActivity.this);
        }
      }
    });
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription.ThemeDescriptionDelegate local9 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor()
      {
        AvatarDrawable localAvatarDrawable;
        if (GroupCreateFinalActivity.this.listView != null)
        {
          int i = GroupCreateFinalActivity.this.listView.getChildCount();
          for (int j = 0; j < i; j++)
          {
            localObject = GroupCreateFinalActivity.this.listView.getChildAt(j);
            if ((localObject instanceof GroupCreateUserCell)) {
              ((GroupCreateUserCell)localObject).update(0);
            }
          }
          localAvatarDrawable = GroupCreateFinalActivity.this.avatarDrawable;
          if (GroupCreateFinalActivity.this.editText.length() <= 0) {
            break label117;
          }
        }
        label117:
        for (Object localObject = GroupCreateFinalActivity.this.editText.getText().toString();; localObject = null)
        {
          localAvatarDrawable.setInfo(5, (String)localObject, null, false);
          GroupCreateFinalActivity.this.avatarImage.invalidate();
          return;
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollActive");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollInactive");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollText");
    Object localObject1 = this.listView;
    Object localObject2 = Theme.dividerPaint;
    ThemeDescription localThemeDescription11 = new ThemeDescription((View)localObject1, 0, new Class[] { View.class }, (Paint)localObject2, null, null, "divider");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "groupcreate_hintText");
    ThemeDescription localThemeDescription14 = new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "groupcreate_cursor");
    ThemeDescription localThemeDescription15 = new ThemeDescription(this.editText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField");
    ThemeDescription localThemeDescription16 = new ThemeDescription(this.editText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated");
    ThemeDescription localThemeDescription17 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { GroupCreateSectionCell.class }, null, null, null, "graySection");
    ThemeDescription localThemeDescription18 = new ThemeDescription(this.listView, 0, new Class[] { GroupCreateSectionCell.class }, new String[] { "drawable" }, null, null, null, "groupcreate_sectionShadow");
    ThemeDescription localThemeDescription19 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { GroupCreateSectionCell.class }, new String[] { "textView" }, null, null, null, "groupcreate_sectionText");
    ThemeDescription localThemeDescription20 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { GroupCreateUserCell.class }, new String[] { "textView" }, null, null, null, "groupcreate_sectionText");
    localObject1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[] { GroupCreateUserCell.class }, new String[] { "statusTextView" }, null, null, null, "groupcreate_onlineText");
    ThemeDescription localThemeDescription21 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[] { GroupCreateUserCell.class }, new String[] { "statusTextView" }, null, null, null, "groupcreate_offlineText");
    RecyclerView localRecyclerView = this.listView;
    Drawable localDrawable1 = Theme.avatar_photoDrawable;
    Drawable localDrawable2 = Theme.avatar_broadcastDrawable;
    localObject2 = Theme.avatar_savedDrawable;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, localThemeDescription12, localThemeDescription13, localThemeDescription14, localThemeDescription15, localThemeDescription16, localThemeDescription17, localThemeDescription18, localThemeDescription19, localThemeDescription20, localObject1, localThemeDescription21, new ThemeDescription(localRecyclerView, 0, new Class[] { GroupCreateUserCell.class }, null, new Drawable[] { localDrawable1, localDrawable2, localObject2 }, local9, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local9, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local9, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local9, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local9, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local9, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local9, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local9, "avatar_backgroundPink"), new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressInner2"), new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressOuter2"), new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText") };
  }
  
  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.avatarUpdater.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public boolean onFragmentCreate()
  {
    boolean bool = false;
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidFailCreate);
    this.avatarUpdater.parentFragment = this;
    this.avatarUpdater.delegate = this;
    this.selectedContacts = getArguments().getIntegerArrayList("result");
    final Object localObject1 = new ArrayList();
    final Object localObject2;
    for (int i = 0; i < this.selectedContacts.size(); i++)
    {
      localObject2 = (Integer)this.selectedContacts.get(i);
      if (MessagesController.getInstance(this.currentAccount).getUser((Integer)localObject2) == null) {
        ((ArrayList)localObject1).add(localObject2);
      }
    }
    final CountDownLatch localCountDownLatch;
    if (!((ArrayList)localObject1).isEmpty())
    {
      localCountDownLatch = new CountDownLatch(1);
      localObject2 = new ArrayList();
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          localObject2.addAll(MessagesStorage.getInstance(GroupCreateFinalActivity.this.currentAccount).getUsers(localObject1));
          localCountDownLatch.countDown();
        }
      });
    }
    for (;;)
    {
      try
      {
        localCountDownLatch.await();
        if (((ArrayList)localObject1).size() != ((ArrayList)localObject2).size()) {
          return bool;
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        continue;
        if (((ArrayList)localObject2).isEmpty()) {
          continue;
        }
        localObject2 = ((ArrayList)localObject2).iterator();
      }
      while (((Iterator)localObject2).hasNext())
      {
        localObject1 = (TLRPC.User)((Iterator)localObject2).next();
        MessagesController.getInstance(this.currentAccount).putUser((TLRPC.User)localObject1, true);
      }
      bool = super.onFragmentCreate();
    }
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidFailCreate);
    this.avatarUpdater.clear();
    if (this.reqId != 0) {
      ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.adapter != null) {
      this.adapter.notifyDataSetChanged();
    }
  }
  
  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      this.editText.requestFocus();
      AndroidUtilities.showKeyboard(this.editText);
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
      if (this.editText == null) {
        break label49;
      }
      this.editText.setText(paramBundle);
    }
    for (;;)
    {
      return;
      label49:
      this.nameToSet = paramBundle;
    }
  }
  
  public void saveSelfArgs(Bundle paramBundle)
  {
    if ((this.avatarUpdater != null) && (this.avatarUpdater.currentPicturePath != null)) {
      paramBundle.putString("path", this.avatarUpdater.currentPicturePath);
    }
    if (this.editText != null)
    {
      String str = this.editText.getText().toString();
      if ((str != null) && (str.length() != 0)) {
        paramBundle.putString("nameTextView", str);
      }
    }
  }
  
  public class GroupCreateAdapter
    extends RecyclerListView.SelectionAdapter
  {
    private Context context;
    
    public GroupCreateAdapter(Context paramContext)
    {
      this.context = paramContext;
    }
    
    public int getItemCount()
    {
      return GroupCreateFinalActivity.this.selectedContacts.size() + 1;
    }
    
    public int getItemViewType(int paramInt)
    {
      switch (paramInt)
      {
      }
      for (paramInt = 1;; paramInt = 0) {
        return paramInt;
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return false;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      default: 
        ((GroupCreateUserCell)paramViewHolder.itemView).setUser(MessagesController.getInstance(GroupCreateFinalActivity.this.currentAccount).getUser((Integer)GroupCreateFinalActivity.this.selectedContacts.get(paramInt - 1)), null, null);
      }
      for (;;)
      {
        return;
        ((GroupCreateSectionCell)paramViewHolder.itemView).setText(LocaleController.formatPluralString("Members", GroupCreateFinalActivity.this.selectedContacts.size()));
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      }
      for (paramViewGroup = new GroupCreateUserCell(this.context, false);; paramViewGroup = new GroupCreateSectionCell(this.context)) {
        return new RecyclerListView.Holder(paramViewGroup);
      }
    }
    
    public void onViewRecycled(RecyclerView.ViewHolder paramViewHolder)
    {
      if (paramViewHolder.getItemViewType() == 1) {
        ((GroupCreateUserCell)paramViewHolder.itemView).recycle();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/GroupCreateFinalActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */