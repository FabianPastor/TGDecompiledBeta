package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_getSupport;
import org.telegram.tgnet.TLRPC.TL_help_support;
import org.telegram.tgnet.TLRPC.TL_photos_photo;
import org.telegram.tgnet.TLRPC.TL_photos_uploadProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_userProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_userProfilePhotoEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

public class SettingsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, PhotoViewer.PhotoViewerProvider
{
  private static final int edit_name = 1;
  private static final int logout = 2;
  private int askQuestionRow;
  private int autoplayGifsRow;
  private BackupImageView avatarImage;
  private AvatarUpdater avatarUpdater = new AvatarUpdater();
  private int backgroundRow;
  private int cacheRow;
  private int clearLogsRow;
  private int contactsReimportRow;
  private int contactsSectionRow;
  private int contactsSortRow;
  private int customTabsRow;
  private int directShareRow;
  private int emptyRow;
  private int enableAnimationsRow;
  private int extraHeight;
  private View extraHeightView;
  private int languageRow;
  private LinearLayoutManager layoutManager;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int mediaDownloadSection;
  private int mediaDownloadSection2;
  private int messagesSectionRow;
  private int messagesSectionRow2;
  private int mobileDownloadRow;
  private TextView nameTextView;
  private int notificationRow;
  private int numberRow;
  private int numberSectionRow;
  private TextView onlineTextView;
  private int overscrollRow;
  private int privacyPolicyRow;
  private int privacyRow;
  private int raiseToSpeakRow;
  private int roamingDownloadRow;
  private int rowCount;
  private int saveToGalleryRow;
  private int sendByEnterRow;
  private int sendLogsRow;
  private int settingsSectionRow;
  private int settingsSectionRow2;
  private View shadowView;
  private int stickersRow;
  private int supportSectionRow;
  private int supportSectionRow2;
  private int switchBackendButtonRow;
  private int telegramFaqRow;
  private int textSizeRow;
  private int usernameRow;
  private int versionRow;
  private int wifiDownloadRow;
  private ImageView writeButton;
  private AnimatorSet writeButtonAnimation;
  
  private void fixLayout()
  {
    if (this.fragmentView == null) {
      return;
    }
    this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        if (SettingsActivity.this.fragmentView != null)
        {
          SettingsActivity.this.needLayout();
          SettingsActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
        }
        return true;
      }
    });
  }
  
  private void needLayout()
  {
    float f1;
    label135:
    final boolean bool1;
    label169:
    boolean bool2;
    if (this.actionBar.getOccupyStatusBar())
    {
      i = AndroidUtilities.statusBarHeight;
      i += ActionBar.getCurrentActionBarHeight();
      Object localObject;
      if (this.listView != null)
      {
        localObject = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
        if (((FrameLayout.LayoutParams)localObject).topMargin != i)
        {
          ((FrameLayout.LayoutParams)localObject).topMargin = i;
          this.listView.setLayoutParams((ViewGroup.LayoutParams)localObject);
          this.extraHeightView.setTranslationY(i);
        }
      }
      if (this.avatarImage != null)
      {
        f1 = this.extraHeight / AndroidUtilities.dp(88.0F);
        this.extraHeightView.setScaleY(f1);
        this.shadowView.setTranslationY(this.extraHeight + i);
        localObject = this.writeButton;
        if (!this.actionBar.getOccupyStatusBar()) {
          break label624;
        }
        i = AndroidUtilities.statusBarHeight;
        ((ImageView)localObject).setTranslationY(i + ActionBar.getCurrentActionBarHeight() + this.extraHeight - AndroidUtilities.dp(29.5F));
        if (f1 <= 0.2F) {
          break label629;
        }
        bool1 = true;
        if (this.writeButton.getTag() != null) {
          break label635;
        }
        bool2 = true;
        label182:
        if (bool1 != bool2)
        {
          if (!bool1) {
            break label641;
          }
          this.writeButton.setTag(null);
          this.writeButton.setVisibility(0);
          label210:
          if (this.writeButtonAnimation != null)
          {
            localObject = this.writeButtonAnimation;
            this.writeButtonAnimation = null;
            ((AnimatorSet)localObject).cancel();
          }
          this.writeButtonAnimation = new AnimatorSet();
          if (!bool1) {
            break label655;
          }
          this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
          this.writeButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[] { 1.0F }) });
          label334:
          this.writeButtonAnimation.setDuration(150L);
          this.writeButtonAnimation.addListener(new AnimatorListenerAdapterProxy()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if ((SettingsActivity.this.writeButtonAnimation != null) && (SettingsActivity.this.writeButtonAnimation.equals(paramAnonymousAnimator)))
              {
                paramAnonymousAnimator = SettingsActivity.this.writeButton;
                if (!bool1) {
                  break label56;
                }
              }
              label56:
              for (int i = 0;; i = 8)
              {
                paramAnonymousAnimator.setVisibility(i);
                SettingsActivity.access$4102(SettingsActivity.this, null);
                return;
              }
            }
          });
          this.writeButtonAnimation.start();
        }
        this.avatarImage.setScaleX((42.0F + 18.0F * f1) / 42.0F);
        this.avatarImage.setScaleY((42.0F + 18.0F * f1) / 42.0F);
        if (!this.actionBar.getOccupyStatusBar()) {
          break label747;
        }
      }
    }
    label624:
    label629:
    label635:
    label641:
    label655:
    label747:
    for (int i = AndroidUtilities.statusBarHeight;; i = 0)
    {
      float f2 = i + ActionBar.getCurrentActionBarHeight() / 2.0F * (1.0F + f1) - 21.0F * AndroidUtilities.density + 27.0F * AndroidUtilities.density * f1;
      this.avatarImage.setTranslationX(-AndroidUtilities.dp(47.0F) * f1);
      this.avatarImage.setTranslationY((float)Math.ceil(f2));
      this.nameTextView.setTranslationX(-21.0F * AndroidUtilities.density * f1);
      this.nameTextView.setTranslationY((float)Math.floor(f2) - (float)Math.ceil(AndroidUtilities.density) + (float)Math.floor(7.0F * AndroidUtilities.density * f1));
      this.onlineTextView.setTranslationX(-21.0F * AndroidUtilities.density * f1);
      this.onlineTextView.setTranslationY((float)Math.floor(f2) + AndroidUtilities.dp(22.0F) + (float)Math.floor(11.0F * AndroidUtilities.density) * f1);
      this.nameTextView.setScaleX(1.0F + 0.12F * f1);
      this.nameTextView.setScaleY(1.0F + 0.12F * f1);
      return;
      i = 0;
      break;
      i = 0;
      break label135;
      bool1 = false;
      break label169;
      bool2 = false;
      break label182;
      this.writeButton.setTag(Integer.valueOf(0));
      break label210;
      this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
      this.writeButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[] { 0.2F }), ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[] { 0.2F }), ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[] { 0.0F }) });
      break label334;
    }
  }
  
  private void performAskAQuestion()
  {
    final SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
    int i = localSharedPreferences.getInt("support_id", 0);
    final Object localObject1 = null;
    Object localObject2;
    Object localObject3;
    if (i != 0)
    {
      localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(i));
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject3 = localSharedPreferences.getString("support_user", null);
        localObject1 = localObject2;
        if (localObject3 == null) {}
      }
    }
    try
    {
      localObject3 = Base64.decode((String)localObject3, 0);
      localObject1 = localObject2;
      if (localObject3 != null)
      {
        localObject3 = new SerializedData((byte[])localObject3);
        localObject2 = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject3, ((SerializedData)localObject3).readInt32(false), false);
        localObject1 = localObject2;
        if (localObject2 != null)
        {
          localObject1 = localObject2;
          if (((TLRPC.User)localObject2).id == 333000) {
            localObject1 = null;
          }
        }
        ((SerializedData)localObject3).cleanup();
      }
    }
    catch (Exception localException)
    {
      TLRPC.User localUser;
      for (;;)
      {
        FileLog.e("tmessages", localException);
        localUser = null;
      }
      MessagesController.getInstance().putUser(localUser, true);
      localObject2 = new Bundle();
      ((Bundle)localObject2).putInt("user_id", localUser.id);
      presentFragment(new ChatActivity((Bundle)localObject2));
    }
    if (localObject1 == null)
    {
      localObject1 = new ProgressDialog(getParentActivity());
      ((ProgressDialog)localObject1).setMessage(LocaleController.getString("Loading", 2131165834));
      ((ProgressDialog)localObject1).setCanceledOnTouchOutside(false);
      ((ProgressDialog)localObject1).setCancelable(false);
      ((ProgressDialog)localObject1).show();
      localObject2 = new TLRPC.TL_help_getSupport();
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject2, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                Object localObject = SettingsActivity.10.this.val$preferences.edit();
                ((SharedPreferences.Editor)localObject).putInt("support_id", paramAnonymousTLObject.user.id);
                SerializedData localSerializedData = new SerializedData();
                paramAnonymousTLObject.user.serializeToStream(localSerializedData);
                ((SharedPreferences.Editor)localObject).putString("support_user", Base64.encodeToString(localSerializedData.toByteArray(), 0));
                ((SharedPreferences.Editor)localObject).commit();
                localSerializedData.cleanup();
                try
                {
                  SettingsActivity.10.this.val$progressDialog.dismiss();
                  localObject = new ArrayList();
                  ((ArrayList)localObject).add(paramAnonymousTLObject.user);
                  MessagesStorage.getInstance().putUsersAndChats((ArrayList)localObject, null, true, true);
                  MessagesController.getInstance().putUser(paramAnonymousTLObject.user, false);
                  localObject = new Bundle();
                  ((Bundle)localObject).putInt("user_id", paramAnonymousTLObject.user.id);
                  SettingsActivity.this.presentFragment(new ChatActivity((Bundle)localObject));
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
            });
            return;
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              try
              {
                SettingsActivity.10.this.val$progressDialog.dismiss();
                return;
              }
              catch (Exception localException)
              {
                FileLog.e("tmessages", localException);
              }
            }
          });
        }
      });
      return;
    }
  }
  
  private void sendLogs()
  {
    int i = 0;
    try
    {
      ArrayList localArrayList = new ArrayList();
      Object localObject = ApplicationLoader.applicationContext.getExternalFilesDir(null);
      localObject = new File(((File)localObject).getAbsolutePath() + "/logs").listFiles();
      int j = localObject.length;
      while (i < j)
      {
        localArrayList.add(Uri.fromFile(localObject[i]));
        i += 1;
      }
      if (localArrayList.isEmpty()) {
        return;
      }
      localObject = new Intent("android.intent.action.SEND_MULTIPLE");
      ((Intent)localObject).setType("message/rfc822");
      ((Intent)localObject).putExtra("android.intent.extra.EMAIL", new String[] { BuildVars.SEND_LOGS_EMAIL });
      ((Intent)localObject).putExtra("android.intent.extra.SUBJECT", "last logs");
      ((Intent)localObject).putParcelableArrayListExtra("android.intent.extra.STREAM", localArrayList);
      getParentActivity().startActivityForResult(Intent.createChooser((Intent)localObject, "Select email application."), 500);
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  private void updateUserData()
  {
    boolean bool2 = true;
    TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
    Object localObject = null;
    TLRPC.FileLocation localFileLocation = null;
    if (localUser.photo != null)
    {
      localObject = localUser.photo.photo_small;
      localFileLocation = localUser.photo.photo_big;
    }
    AvatarDrawable localAvatarDrawable = new AvatarDrawable(localUser, true);
    localAvatarDrawable.setColor(-11500111);
    if (this.avatarImage != null)
    {
      this.avatarImage.setImage((TLObject)localObject, "50_50", localAvatarDrawable);
      localObject = this.avatarImage.getImageReceiver();
      if (PhotoViewer.getInstance().isShowingImage(localFileLocation)) {
        break label174;
      }
      bool1 = true;
      ((ImageReceiver)localObject).setVisible(bool1, false);
      this.nameTextView.setText(UserObject.getUserName(localUser));
      this.onlineTextView.setText(LocaleController.getString("Online", 2131166046));
      localObject = this.avatarImage.getImageReceiver();
      if (PhotoViewer.getInstance().isShowingImage(localFileLocation)) {
        break label179;
      }
    }
    label174:
    label179:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      ((ImageReceiver)localObject).setVisible(bool1, false);
      return;
      bool1 = false;
      break;
    }
  }
  
  public boolean allowCaption()
  {
    return true;
  }
  
  public boolean cancelButtonPressed()
  {
    return true;
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(5));
    this.actionBar.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId(5));
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAddToContainer(false);
    this.extraHeight = 88;
    if (AndroidUtilities.isTablet()) {
      this.actionBar.setOccupyStatusBar(false);
    }
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          SettingsActivity.this.finishFragment();
        }
        do
        {
          return;
          if (paramAnonymousInt == 1)
          {
            SettingsActivity.this.presentFragment(new ChangeNameActivity());
            return;
          }
        } while ((paramAnonymousInt != 2) || (SettingsActivity.this.getParentActivity() == null));
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
        localBuilder.setMessage(LocaleController.getString("AreYouSureLogout", 2131165324));
        localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
        localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            MessagesController.getInstance().performLogout(true);
          }
        });
        localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
        SettingsActivity.this.showDialog(localBuilder.create());
      }
    });
    Object localObject1 = this.actionBar.createMenu().addItem(0, 2130837708);
    ((ActionBarMenuItem)localObject1).addSubItem(1, LocaleController.getString("EditName", 2131165596), 0);
    ((ActionBarMenuItem)localObject1).addSubItem(2, LocaleController.getString("LogOut", 2131165844), 0);
    this.listAdapter = new ListAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext)
    {
      protected boolean drawChild(@NonNull Canvas paramAnonymousCanvas, @NonNull View paramAnonymousView, long paramAnonymousLong)
      {
        if (paramAnonymousView == SettingsActivity.this.listView)
        {
          boolean bool = super.drawChild(paramAnonymousCanvas, paramAnonymousView, paramAnonymousLong);
          if (SettingsActivity.this.parentLayout != null)
          {
            int k = 0;
            int m = getChildCount();
            int i = 0;
            int j = k;
            if (i < m)
            {
              View localView = getChildAt(i);
              if (localView == paramAnonymousView) {}
              while ((!(localView instanceof ActionBar)) || (localView.getVisibility() != 0))
              {
                i += 1;
                break;
              }
              j = k;
              if (((ActionBar)localView).getCastShadows()) {
                j = localView.getMeasuredHeight();
              }
            }
            SettingsActivity.this.parentLayout.drawHeaderShadow(paramAnonymousCanvas, j);
          }
          return bool;
        }
        return super.drawChild(paramAnonymousCanvas, paramAnonymousView, paramAnonymousLong);
      }
    };
    localObject1 = (FrameLayout)this.fragmentView;
    this.listView = new RecyclerListView(paramContext);
    this.listView.setVerticalScrollBarEnabled(false);
    Object localObject2 = this.listView;
    LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(paramContext, 1, false);
    this.layoutManager = localLinearLayoutManager;
    ((RecyclerListView)localObject2).setLayoutManager(localLinearLayoutManager);
    this.listView.setGlowColor(AvatarDrawable.getProfileBackColorForId(5));
    ((FrameLayout)localObject1).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
    this.listView.setAdapter(this.listAdapter);
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramAnonymousView, final int paramAnonymousInt)
      {
        if (paramAnonymousInt == SettingsActivity.this.textSizeRow) {
          if (SettingsActivity.this.getParentActivity() != null) {}
        }
        label243:
        label611:
        label1310:
        label1333:
        label1478:
        label1484:
        label1692:
        do
        {
          Object localObject3;
          Object localObject4;
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        do
                        {
                          do
                          {
                            boolean bool2;
                            do
                            {
                              do
                              {
                                do
                                {
                                  return;
                                  paramAnonymousView = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
                                  paramAnonymousView.setTitle(LocaleController.getString("TextSize", 2131166331));
                                  localObject1 = new NumberPicker(SettingsActivity.this.getParentActivity());
                                  ((NumberPicker)localObject1).setMinValue(12);
                                  ((NumberPicker)localObject1).setMaxValue(30);
                                  ((NumberPicker)localObject1).setValue(MessagesController.getInstance().fontSize);
                                  paramAnonymousView.setView((View)localObject1);
                                  paramAnonymousView.setNegativeButton(LocaleController.getString("Done", 2131165590), new DialogInterface.OnClickListener()
                                  {
                                    public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                                    {
                                      paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                                      paramAnonymous2DialogInterface.putInt("fons_size", localObject1.getValue());
                                      MessagesController.getInstance().fontSize = localObject1.getValue();
                                      paramAnonymous2DialogInterface.commit();
                                      if (SettingsActivity.this.listAdapter != null) {
                                        SettingsActivity.this.listAdapter.notifyItemChanged(paramAnonymousInt);
                                      }
                                    }
                                  });
                                  SettingsActivity.this.showDialog(paramAnonymousView.create());
                                  return;
                                  if (paramAnonymousInt != SettingsActivity.this.enableAnimationsRow) {
                                    break label243;
                                  }
                                  localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                                  bool2 = ((SharedPreferences)localObject1).getBoolean("view_animations", true);
                                  localObject1 = ((SharedPreferences)localObject1).edit();
                                  if (bool2) {
                                    break;
                                  }
                                  bool1 = true;
                                  ((SharedPreferences.Editor)localObject1).putBoolean("view_animations", bool1);
                                  ((SharedPreferences.Editor)localObject1).commit();
                                } while (!(paramAnonymousView instanceof TextCheckCell));
                                paramAnonymousView = (TextCheckCell)paramAnonymousView;
                                if (!bool2) {}
                                for (bool1 = true;; bool1 = false)
                                {
                                  paramAnonymousView.setChecked(bool1);
                                  return;
                                  bool1 = false;
                                  break;
                                }
                                if (paramAnonymousInt == SettingsActivity.this.notificationRow)
                                {
                                  SettingsActivity.this.presentFragment(new NotificationsSettingsActivity());
                                  return;
                                }
                                if (paramAnonymousInt == SettingsActivity.this.backgroundRow)
                                {
                                  SettingsActivity.this.presentFragment(new WallpapersActivity());
                                  return;
                                }
                                if (paramAnonymousInt != SettingsActivity.this.askQuestionRow) {
                                  break;
                                }
                              } while (SettingsActivity.this.getParentActivity() == null);
                              paramAnonymousView = new TextView(SettingsActivity.this.getParentActivity());
                              paramAnonymousView.setText(Html.fromHtml(LocaleController.getString("AskAQuestionInfo", 2131165333)));
                              paramAnonymousView.setTextSize(18.0F);
                              paramAnonymousView.setLinkTextColor(-14255946);
                              paramAnonymousView.setPadding(AndroidUtilities.dp(8.0F), AndroidUtilities.dp(5.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(6.0F));
                              paramAnonymousView.setMovementMethod(new SettingsActivity.LinkMovementMethodMy(null));
                              localObject1 = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
                              ((AlertDialog.Builder)localObject1).setView(paramAnonymousView);
                              ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("AskButton", 2131165334), new DialogInterface.OnClickListener()
                              {
                                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                                {
                                  SettingsActivity.this.performAskAQuestion();
                                }
                              });
                              ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
                              SettingsActivity.this.showDialog(((AlertDialog.Builder)localObject1).create());
                              return;
                              if (paramAnonymousInt == SettingsActivity.this.sendLogsRow)
                              {
                                SettingsActivity.this.sendLogs();
                                return;
                              }
                              if (paramAnonymousInt == SettingsActivity.this.clearLogsRow)
                              {
                                FileLog.cleanupLogs();
                                return;
                              }
                              if (paramAnonymousInt != SettingsActivity.this.sendByEnterRow) {
                                break label611;
                              }
                              localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                              bool2 = ((SharedPreferences)localObject1).getBoolean("send_by_enter", false);
                              localObject1 = ((SharedPreferences)localObject1).edit();
                              if (bool2) {
                                break;
                              }
                              bool1 = true;
                              ((SharedPreferences.Editor)localObject1).putBoolean("send_by_enter", bool1);
                              ((SharedPreferences.Editor)localObject1).commit();
                            } while (!(paramAnonymousView instanceof TextCheckCell));
                            paramAnonymousView = (TextCheckCell)paramAnonymousView;
                            if (!bool2) {}
                            for (bool1 = true;; bool1 = false)
                            {
                              paramAnonymousView.setChecked(bool1);
                              return;
                              bool1 = false;
                              break;
                            }
                            if (paramAnonymousInt != SettingsActivity.this.raiseToSpeakRow) {
                              break;
                            }
                            MediaController.getInstance().toogleRaiseToSpeak();
                          } while (!(paramAnonymousView instanceof TextCheckCell));
                          ((TextCheckCell)paramAnonymousView).setChecked(MediaController.getInstance().canRaiseToSpeak());
                          return;
                          if (paramAnonymousInt != SettingsActivity.this.autoplayGifsRow) {
                            break;
                          }
                          MediaController.getInstance().toggleAutoplayGifs();
                        } while (!(paramAnonymousView instanceof TextCheckCell));
                        ((TextCheckCell)paramAnonymousView).setChecked(MediaController.getInstance().canAutoplayGifs());
                        return;
                        if (paramAnonymousInt != SettingsActivity.this.saveToGalleryRow) {
                          break;
                        }
                        MediaController.getInstance().toggleSaveToGallery();
                      } while (!(paramAnonymousView instanceof TextCheckCell));
                      ((TextCheckCell)paramAnonymousView).setChecked(MediaController.getInstance().canSaveToGallery());
                      return;
                      if (paramAnonymousInt != SettingsActivity.this.customTabsRow) {
                        break;
                      }
                      MediaController.getInstance().toggleCustomTabs();
                    } while (!(paramAnonymousView instanceof TextCheckCell));
                    ((TextCheckCell)paramAnonymousView).setChecked(MediaController.getInstance().canCustomTabs());
                    return;
                    if (paramAnonymousInt != SettingsActivity.this.directShareRow) {
                      break;
                    }
                    MediaController.getInstance().toggleDirectShare();
                  } while (!(paramAnonymousView instanceof TextCheckCell));
                  ((TextCheckCell)paramAnonymousView).setChecked(MediaController.getInstance().canDirectShare());
                  return;
                  if (paramAnonymousInt == SettingsActivity.this.privacyRow)
                  {
                    SettingsActivity.this.presentFragment(new PrivacySettingsActivity());
                    return;
                  }
                  if (paramAnonymousInt == SettingsActivity.this.languageRow)
                  {
                    SettingsActivity.this.presentFragment(new LanguageSelectActivity());
                    return;
                  }
                  if (paramAnonymousInt != SettingsActivity.this.switchBackendButtonRow) {
                    break;
                  }
                } while (SettingsActivity.this.getParentActivity() == null);
                paramAnonymousView = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
                paramAnonymousView.setMessage(LocaleController.getString("AreYouSure", 2131165313));
                paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165299));
                paramAnonymousView.setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                  {
                    ConnectionsManager.getInstance().switchBackend();
                  }
                });
                paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
                SettingsActivity.this.showDialog(paramAnonymousView.create());
                return;
                if (paramAnonymousInt == SettingsActivity.this.telegramFaqRow)
                {
                  Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("TelegramFaqUrl", 2131166327));
                  return;
                }
                if (paramAnonymousInt == SettingsActivity.this.privacyPolicyRow)
                {
                  Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", 2131166142));
                  return;
                }
              } while (paramAnonymousInt == SettingsActivity.this.contactsReimportRow);
              if (paramAnonymousInt != SettingsActivity.this.contactsSortRow) {
                break;
              }
            } while (SettingsActivity.this.getParentActivity() == null);
            paramAnonymousView = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
            paramAnonymousView.setTitle(LocaleController.getString("SortBy", 2131166302));
            localObject1 = LocaleController.getString("Default", 2131165559);
            localObject2 = LocaleController.getString("SortFirstName", 2131166303);
            localObject3 = LocaleController.getString("SortLastName", 2131166304);
            localObject4 = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                paramAnonymous2DialogInterface.putInt("sortContactsBy", paramAnonymous2Int);
                paramAnonymous2DialogInterface.commit();
                if (SettingsActivity.this.listAdapter != null) {
                  SettingsActivity.this.listAdapter.notifyItemChanged(paramAnonymousInt);
                }
              }
            };
            paramAnonymousView.setItems(new CharSequence[] { localObject1, localObject2, localObject3 }, (DialogInterface.OnClickListener)localObject4);
            paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
            SettingsActivity.this.showDialog(paramAnonymousView.create());
            return;
            if ((paramAnonymousInt != SettingsActivity.this.wifiDownloadRow) && (paramAnonymousInt != SettingsActivity.this.mobileDownloadRow) && (paramAnonymousInt != SettingsActivity.this.roamingDownloadRow)) {
              break;
            }
          } while (SettingsActivity.this.getParentActivity() == null);
          final Object localObject1 = new boolean[6];
          Object localObject2 = new BottomSheet.Builder(SettingsActivity.this.getParentActivity());
          int i = 0;
          int j;
          if (paramAnonymousInt == SettingsActivity.this.mobileDownloadRow)
          {
            i = MediaController.getInstance().mobileDataDownloadMask;
            ((BottomSheet.Builder)localObject2).setApplyTopPadding(false);
            ((BottomSheet.Builder)localObject2).setApplyBottomPadding(false);
            localObject3 = new LinearLayout(SettingsActivity.this.getParentActivity());
            ((LinearLayout)localObject3).setOrientation(1);
            j = 0;
            if (j >= 6) {
              break label1692;
            }
            paramAnonymousView = null;
            if (j != 0) {
              break label1484;
            }
            if ((i & 0x1) == 0) {
              break label1478;
            }
            bool1 = true;
            localObject1[j] = bool1;
            paramAnonymousView = LocaleController.getString("AttachPhoto", 2131165343);
          }
          do
          {
            localObject4 = new CheckBoxCell(SettingsActivity.this.getParentActivity());
            ((CheckBoxCell)localObject4).setTag(Integer.valueOf(j));
            ((CheckBoxCell)localObject4).setBackgroundResource(2130837796);
            ((LinearLayout)localObject3).addView((View)localObject4, LayoutHelper.createLinear(-1, 48));
            ((CheckBoxCell)localObject4).setText(paramAnonymousView, "", localObject1[j], true);
            ((CheckBoxCell)localObject4).setOnClickListener(new View.OnClickListener()
            {
              public void onClick(View paramAnonymous2View)
              {
                paramAnonymous2View = (CheckBoxCell)paramAnonymous2View;
                int i = ((Integer)paramAnonymous2View.getTag()).intValue();
                boolean[] arrayOfBoolean = localObject1;
                if (localObject1[i] == 0) {}
                for (int j = 1;; j = 0)
                {
                  arrayOfBoolean[i] = j;
                  paramAnonymous2View.setChecked(localObject1[i], true);
                  return;
                }
              }
            });
            j += 1;
            break label1310;
            if (paramAnonymousInt == SettingsActivity.this.wifiDownloadRow)
            {
              i = MediaController.getInstance().wifiDownloadMask;
              break;
            }
            if (paramAnonymousInt != SettingsActivity.this.roamingDownloadRow) {
              break;
            }
            i = MediaController.getInstance().roamingDownloadMask;
            break;
            bool1 = false;
            break label1333;
            if (j == 1)
            {
              if ((i & 0x2) != 0) {}
              for (bool1 = true;; bool1 = false)
              {
                localObject1[j] = bool1;
                paramAnonymousView = LocaleController.getString("AttachAudio", 2131165335);
                break;
              }
            }
            if (j == 2)
            {
              if ((i & 0x4) != 0) {}
              for (bool1 = true;; bool1 = false)
              {
                localObject1[j] = bool1;
                paramAnonymousView = LocaleController.getString("AttachVideo", 2131165345);
                break;
              }
            }
            if (j == 3)
            {
              if ((i & 0x8) != 0) {}
              for (bool1 = true;; bool1 = false)
              {
                localObject1[j] = bool1;
                paramAnonymousView = LocaleController.getString("AttachDocument", 2131165338);
                break;
              }
            }
            if (j == 4)
            {
              if ((i & 0x10) != 0) {}
              for (bool1 = true;; bool1 = false)
              {
                localObject1[j] = bool1;
                paramAnonymousView = LocaleController.getString("AttachMusic", 2131165342);
                break;
              }
            }
          } while (j != 5);
          if ((i & 0x20) != 0) {}
          for (boolean bool1 = true;; bool1 = false)
          {
            localObject1[j] = bool1;
            paramAnonymousView = LocaleController.getString("AttachGif", 2131165340);
            break;
          }
          paramAnonymousView = new BottomSheet.BottomSheetCell(SettingsActivity.this.getParentActivity(), 1);
          paramAnonymousView.setBackgroundResource(2130837796);
          paramAnonymousView.setTextAndIcon(LocaleController.getString("Save", 2131166199).toUpperCase(), 0);
          paramAnonymousView.setTextColor(-12940081);
          paramAnonymousView.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymous2View)
            {
              int k;
              try
              {
                if (SettingsActivity.this.visibleDialog != null) {
                  SettingsActivity.this.visibleDialog.dismiss();
                }
                k = 0;
                j = 0;
                for (;;)
                {
                  if (j >= 6) {
                    break label149;
                  }
                  i = k;
                  if (localObject1[j] != 0)
                  {
                    if (j != 0) {
                      break;
                    }
                    i = k | 0x1;
                  }
                  j += 1;
                  k = i;
                }
              }
              catch (Exception paramAnonymous2View)
              {
                for (;;)
                {
                  int j;
                  int i;
                  FileLog.e("tmessages", paramAnonymous2View);
                  continue;
                  if (j == 1)
                  {
                    i = k | 0x2;
                  }
                  else if (j == 2)
                  {
                    i = k | 0x4;
                  }
                  else if (j == 3)
                  {
                    i = k | 0x8;
                  }
                  else if (j == 4)
                  {
                    i = k | 0x10;
                  }
                  else
                  {
                    i = k;
                    if (j == 5) {
                      i = k | 0x20;
                    }
                  }
                }
                label149:
                paramAnonymous2View = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                if (paramAnonymousInt != SettingsActivity.this.mobileDownloadRow) {
                  break label238;
                }
              }
              paramAnonymous2View.putInt("mobileDataDownloadMask", k);
              MediaController.getInstance().mobileDataDownloadMask = k;
              for (;;)
              {
                paramAnonymous2View.commit();
                if (SettingsActivity.this.listAdapter != null) {
                  SettingsActivity.this.listAdapter.notifyItemChanged(paramAnonymousInt);
                }
                return;
                label238:
                if (paramAnonymousInt == SettingsActivity.this.wifiDownloadRow)
                {
                  paramAnonymous2View.putInt("wifiDownloadMask", k);
                  MediaController.getInstance().wifiDownloadMask = k;
                }
                else if (paramAnonymousInt == SettingsActivity.this.roamingDownloadRow)
                {
                  paramAnonymous2View.putInt("roamingDownloadMask", k);
                  MediaController.getInstance().roamingDownloadMask = k;
                }
              }
            }
          });
          ((LinearLayout)localObject3).addView(paramAnonymousView, LayoutHelper.createLinear(-1, 48));
          ((BottomSheet.Builder)localObject2).setCustomView((View)localObject3);
          SettingsActivity.this.showDialog(((BottomSheet.Builder)localObject2).create());
          return;
          if (paramAnonymousInt == SettingsActivity.this.usernameRow)
          {
            SettingsActivity.this.presentFragment(new ChangeUsernameActivity());
            return;
          }
          if (paramAnonymousInt == SettingsActivity.this.numberRow)
          {
            SettingsActivity.this.presentFragment(new ChangePhoneHelpActivity());
            return;
          }
          if (paramAnonymousInt == SettingsActivity.this.stickersRow)
          {
            SettingsActivity.this.presentFragment(new StickersActivity(0));
            return;
          }
        } while (paramAnonymousInt != SettingsActivity.this.cacheRow);
        SettingsActivity.this.presentFragment(new CacheControlActivity());
      }
    });
    this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
    {
      private int pressCount = 0;
      
      public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        if (paramAnonymousInt == SettingsActivity.this.versionRow)
        {
          this.pressCount += 1;
          if (this.pressCount >= 2)
          {
            paramAnonymousView = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
            paramAnonymousView.setTitle("Debug Menu");
            DialogInterface.OnClickListener local1 = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                if (paramAnonymous2Int == 0) {
                  ContactsController.getInstance().forceImportContacts();
                }
                while (paramAnonymous2Int != 1) {
                  return;
                }
                ContactsController.getInstance().loadContacts(false, true);
              }
            };
            paramAnonymousView.setItems(new CharSequence[] { "Import Contacts", "Reload Contacts" }, local1);
            paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
            SettingsActivity.this.showDialog(paramAnonymousView.create());
            return true;
          }
          try
          {
            Toast.makeText(SettingsActivity.this.getParentActivity(), "¯\\_(ツ)_/¯", 0).show();
            return true;
          }
          catch (Exception paramAnonymousView)
          {
            FileLog.e("tmessages", paramAnonymousView);
            return true;
          }
        }
        return false;
      }
    });
    ((FrameLayout)localObject1).addView(this.actionBar);
    this.extraHeightView = new View(paramContext);
    this.extraHeightView.setPivotY(0.0F);
    this.extraHeightView.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(5));
    ((FrameLayout)localObject1).addView(this.extraHeightView, LayoutHelper.createFrame(-1, 88.0F));
    this.shadowView = new View(paramContext);
    this.shadowView.setBackgroundResource(2130837693);
    ((FrameLayout)localObject1).addView(this.shadowView, LayoutHelper.createFrame(-1, 3.0F));
    this.avatarImage = new BackupImageView(paramContext);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0F));
    this.avatarImage.setPivotX(0.0F);
    this.avatarImage.setPivotY(0.0F);
    ((FrameLayout)localObject1).addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0F, 51, 64.0F, 0.0F, 0.0F, 0.0F));
    this.avatarImage.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
        if ((paramAnonymousView != null) && (paramAnonymousView.photo != null) && (paramAnonymousView.photo.photo_big != null))
        {
          PhotoViewer.getInstance().setParentActivity(SettingsActivity.this.getParentActivity());
          PhotoViewer.getInstance().openPhoto(paramAnonymousView.photo.photo_big, SettingsActivity.this);
        }
      }
    });
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setTextColor(-1);
    this.nameTextView.setTextSize(1, 18.0F);
    this.nameTextView.setLines(1);
    this.nameTextView.setMaxLines(1);
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.nameTextView.setGravity(3);
    this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.nameTextView.setPivotX(0.0F);
    this.nameTextView.setPivotY(0.0F);
    ((FrameLayout)localObject1).addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0F, 51, 118.0F, 0.0F, 48.0F, 0.0F));
    this.onlineTextView = new TextView(paramContext);
    this.onlineTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(5));
    this.onlineTextView.setTextSize(1, 14.0F);
    this.onlineTextView.setLines(1);
    this.onlineTextView.setMaxLines(1);
    this.onlineTextView.setSingleLine(true);
    this.onlineTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.onlineTextView.setGravity(3);
    ((FrameLayout)localObject1).addView(this.onlineTextView, LayoutHelper.createFrame(-2, -2.0F, 51, 118.0F, 0.0F, 48.0F, 0.0F));
    this.writeButton = new ImageView(paramContext);
    this.writeButton.setBackgroundResource(2130837685);
    this.writeButton.setImageResource(2130837679);
    this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramContext = new StateListAnimator();
      localObject2 = ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
      paramContext.addState(new int[] { 16842919 }, (Animator)localObject2);
      localObject2 = ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
      paramContext.addState(new int[0], (Animator)localObject2);
      this.writeButton.setStateListAnimator(paramContext);
      this.writeButton.setOutlineProvider(new ViewOutlineProvider()
      {
        @SuppressLint({"NewApi"})
        public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
        {
          paramAnonymousOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
        }
      });
    }
    ((FrameLayout)localObject1).addView(this.writeButton, LayoutHelper.createFrame(-2, -2.0F, 53, 0.0F, 0.0F, 16.0F, 0.0F));
    this.writeButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (SettingsActivity.this.getParentActivity() == null) {}
        AlertDialog.Builder localBuilder;
        do
        {
          return;
          localBuilder = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
          TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
          paramAnonymousView = localUser;
          if (localUser == null) {
            paramAnonymousView = UserConfig.getCurrentUser();
          }
        } while (paramAnonymousView == null);
        int i = 0;
        if ((paramAnonymousView.photo != null) && (paramAnonymousView.photo.photo_big != null) && (!(paramAnonymousView.photo instanceof TLRPC.TL_userProfilePhotoEmpty)))
        {
          paramAnonymousView = new CharSequence[3];
          paramAnonymousView[0] = LocaleController.getString("FromCamera", 2131165703);
          paramAnonymousView[1] = LocaleController.getString("FromGalley", 2131165710);
          paramAnonymousView[2] = LocaleController.getString("DeletePhoto", 2131165576);
          i = 1;
        }
        for (;;)
        {
          localBuilder.setItems(paramAnonymousView, new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              if (paramAnonymous2Int == 0) {
                SettingsActivity.this.avatarUpdater.openCamera();
              }
              do
              {
                return;
                if (paramAnonymous2Int == 1)
                {
                  SettingsActivity.this.avatarUpdater.openGallery();
                  return;
                }
              } while (paramAnonymous2Int != 2);
              MessagesController.getInstance().deleteUserPhoto(null);
            }
          });
          SettingsActivity.this.showDialog(localBuilder.create());
          return;
          paramAnonymousView = new CharSequence[2];
          paramAnonymousView[0] = LocaleController.getString("FromCamera", 2131165703);
          paramAnonymousView[1] = LocaleController.getString("FromGalley", 2131165710);
        }
      }
    });
    needLayout();
    this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        paramAnonymousInt2 = 0;
        if (SettingsActivity.this.layoutManager.getItemCount() == 0) {}
        do
        {
          do
          {
            return;
            paramAnonymousInt1 = 0;
            paramAnonymousRecyclerView = paramAnonymousRecyclerView.getChildAt(0);
          } while (paramAnonymousRecyclerView == null);
          if (SettingsActivity.this.layoutManager.findFirstVisibleItemPosition() == 0)
          {
            int i = AndroidUtilities.dp(88.0F);
            paramAnonymousInt1 = paramAnonymousInt2;
            if (paramAnonymousRecyclerView.getTop() < 0) {
              paramAnonymousInt1 = paramAnonymousRecyclerView.getTop();
            }
            paramAnonymousInt1 = i + paramAnonymousInt1;
          }
        } while (SettingsActivity.this.extraHeight == paramAnonymousInt1);
        SettingsActivity.access$3902(SettingsActivity.this, paramAnonymousInt1);
        SettingsActivity.this.needLayout();
      }
    });
    return this.fragmentView;
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.updateInterfaces)
    {
      paramInt = ((Integer)paramVarArgs[0]).intValue();
      if (((paramInt & 0x2) != 0) || ((paramInt & 0x1) != 0)) {
        updateUserData();
      }
    }
    while ((paramInt != NotificationCenter.featuredStickersDidLoaded) || (this.listAdapter == null)) {
      return;
    }
    this.listAdapter.notifyItemChanged(this.stickersRow);
  }
  
  public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    if (paramFileLocation == null) {}
    do
    {
      do
      {
        return null;
        paramMessageObject = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
      } while ((paramMessageObject == null) || (paramMessageObject.photo == null) || (paramMessageObject.photo.photo_big == null));
      paramMessageObject = paramMessageObject.photo.photo_big;
    } while ((paramMessageObject.local_id != paramFileLocation.local_id) || (paramMessageObject.volume_id != paramFileLocation.volume_id) || (paramMessageObject.dc_id != paramFileLocation.dc_id));
    paramMessageObject = new int[2];
    this.avatarImage.getLocationInWindow(paramMessageObject);
    paramFileLocation = new PhotoViewer.PlaceProviderObject();
    paramFileLocation.viewX = paramMessageObject[0];
    paramFileLocation.viewY = (paramMessageObject[1] - AndroidUtilities.statusBarHeight);
    paramFileLocation.parentView = this.avatarImage;
    paramFileLocation.imageReceiver = this.avatarImage.getImageReceiver();
    paramFileLocation.dialogId = UserConfig.getClientUserId();
    paramFileLocation.thumb = paramFileLocation.imageReceiver.getBitmap();
    paramFileLocation.size = -1;
    paramFileLocation.radius = this.avatarImage.getImageReceiver().getRoundRadius();
    paramFileLocation.scale = this.avatarImage.getScaleX();
    return paramFileLocation;
  }
  
  public int getSelectedCount()
  {
    return 0;
  }
  
  public Bitmap getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    return null;
  }
  
  public boolean isPhotoChecked(int paramInt)
  {
    return false;
  }
  
  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.avatarUpdater.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    fixLayout();
  }
  
  protected void onDialogDismiss(Dialog paramDialog)
  {
    MediaController.getInstance().checkAutodownloadSettings();
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.avatarUpdater.parentFragment = this;
    this.avatarUpdater.delegate = new AvatarUpdater.AvatarUpdaterDelegate()
    {
      public void didUploadedPhoto(TLRPC.InputFile paramAnonymousInputFile, TLRPC.PhotoSize paramAnonymousPhotoSize1, TLRPC.PhotoSize paramAnonymousPhotoSize2)
      {
        paramAnonymousPhotoSize1 = new TLRPC.TL_photos_uploadProfilePhoto();
        paramAnonymousPhotoSize1.file = paramAnonymousInputFile;
        ConnectionsManager.getInstance().sendRequest(paramAnonymousPhotoSize1, new RequestDelegate()
        {
          public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
          {
            if (paramAnonymous2TL_error == null)
            {
              paramAnonymous2TL_error = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
              if (paramAnonymous2TL_error != null) {
                break label174;
              }
              paramAnonymous2TL_error = UserConfig.getCurrentUser();
              if (paramAnonymous2TL_error != null) {}
            }
            else
            {
              return;
            }
            MessagesController.getInstance().putUser(paramAnonymous2TL_error, false);
            paramAnonymous2TLObject = (TLRPC.TL_photos_photo)paramAnonymous2TLObject;
            Object localObject = paramAnonymous2TLObject.photo.sizes;
            TLRPC.PhotoSize localPhotoSize = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject, 100);
            localObject = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject, 1000);
            paramAnonymous2TL_error.photo = new TLRPC.TL_userProfilePhoto();
            paramAnonymous2TL_error.photo.photo_id = paramAnonymous2TLObject.photo.id;
            if (localPhotoSize != null) {
              paramAnonymous2TL_error.photo.photo_small = localPhotoSize.location;
            }
            if (localObject != null) {
              paramAnonymous2TL_error.photo.photo_big = ((TLRPC.PhotoSize)localObject).location;
            }
            for (;;)
            {
              MessagesStorage.getInstance().clearUserPhotos(paramAnonymous2TL_error.id);
              paramAnonymous2TLObject = new ArrayList();
              paramAnonymous2TLObject.add(paramAnonymous2TL_error);
              MessagesStorage.getInstance().putUsersAndChats(paramAnonymous2TLObject, null, false, true);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1535) });
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                  UserConfig.saveConfig(true);
                }
              });
              return;
              label174:
              UserConfig.setCurrentUser(paramAnonymous2TL_error);
              break;
              if (localPhotoSize != null) {
                paramAnonymous2TL_error.photo.photo_small = localPhotoSize.location;
              }
            }
          }
        });
      }
    };
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.featuredStickersDidLoaded);
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.overscrollRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.emptyRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.numberSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.numberRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.usernameRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.settingsSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.settingsSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.notificationRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.privacyRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.backgroundRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.languageRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.enableAnimationsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.mediaDownloadSection = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.mediaDownloadSection2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.mobileDownloadRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.wifiDownloadRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.roamingDownloadRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.autoplayGifsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.saveToGalleryRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messagesSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messagesSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.customTabsRow = i;
    if (Build.VERSION.SDK_INT >= 23)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.directShareRow = i;
    }
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.stickersRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.textSizeRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.cacheRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.raiseToSpeakRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.sendByEnterRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.supportSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.supportSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.askQuestionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.telegramFaqRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.privacyPolicyRow = i;
    if (BuildVars.DEBUG_VERSION)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.sendLogsRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.clearLogsRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.switchBackendButtonRow = i;
    }
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.versionRow = i;
    StickersQuery.checkFeaturedStickers();
    MessagesController.getInstance().loadFullUser(UserConfig.getCurrentUser(), this.classGuid, true);
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    if (this.avatarImage != null) {
      this.avatarImage.setImageDrawable(null);
    }
    MessagesController.getInstance().cancelLoadFullUser(UserConfig.getClientUserId());
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
    this.avatarUpdater.clear();
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
    updateUserData();
    fixLayout();
  }
  
  public void restoreSelfArgs(Bundle paramBundle)
  {
    if (this.avatarUpdater != null) {
      this.avatarUpdater.currentPicturePath = paramBundle.getString("path");
    }
  }
  
  public void saveSelfArgs(Bundle paramBundle)
  {
    if ((this.avatarUpdater != null) && (this.avatarUpdater.currentPicturePath != null)) {
      paramBundle.putString("path", this.avatarUpdater.currentPicturePath);
    }
  }
  
  public void sendButtonPressed(int paramInt) {}
  
  public void setPhotoChecked(int paramInt) {}
  
  public void updatePhotoAtIndex(int paramInt) {}
  
  public void willHidePhotoViewer()
  {
    this.avatarImage.getImageReceiver().setVisible(true, true);
  }
  
  public void willSwitchFromPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt) {}
  
  private static class LinkMovementMethodMy
    extends LinkMovementMethod
  {
    public boolean onTouchEvent(@NonNull TextView paramTextView, @NonNull Spannable paramSpannable, @NonNull MotionEvent paramMotionEvent)
    {
      try
      {
        boolean bool = super.onTouchEvent(paramTextView, paramSpannable, paramMotionEvent);
        return bool;
      }
      catch (Exception paramTextView)
      {
        FileLog.e("tmessages", paramTextView);
      }
      return false;
    }
  }
  
  private class ListAdapter
    extends RecyclerView.Adapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getItemCount()
    {
      return SettingsActivity.this.rowCount;
    }
    
    public int getItemViewType(int paramInt)
    {
      int j = 2;
      int i;
      if ((paramInt == SettingsActivity.this.emptyRow) || (paramInt == SettingsActivity.this.overscrollRow)) {
        i = 0;
      }
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        do
                        {
                          do
                          {
                            do
                            {
                              do
                              {
                                do
                                {
                                  do
                                  {
                                    do
                                    {
                                      return i;
                                      if ((paramInt == SettingsActivity.this.settingsSectionRow) || (paramInt == SettingsActivity.this.supportSectionRow) || (paramInt == SettingsActivity.this.messagesSectionRow) || (paramInt == SettingsActivity.this.mediaDownloadSection) || (paramInt == SettingsActivity.this.contactsSectionRow)) {
                                        return 1;
                                      }
                                      if ((paramInt == SettingsActivity.this.enableAnimationsRow) || (paramInt == SettingsActivity.this.sendByEnterRow) || (paramInt == SettingsActivity.this.saveToGalleryRow) || (paramInt == SettingsActivity.this.autoplayGifsRow) || (paramInt == SettingsActivity.this.raiseToSpeakRow) || (paramInt == SettingsActivity.this.customTabsRow) || (paramInt == SettingsActivity.this.directShareRow)) {
                                        return 3;
                                      }
                                      i = j;
                                    } while (paramInt == SettingsActivity.this.notificationRow);
                                    i = j;
                                  } while (paramInt == SettingsActivity.this.backgroundRow);
                                  i = j;
                                } while (paramInt == SettingsActivity.this.askQuestionRow);
                                i = j;
                              } while (paramInt == SettingsActivity.this.sendLogsRow);
                              i = j;
                            } while (paramInt == SettingsActivity.this.privacyRow);
                            i = j;
                          } while (paramInt == SettingsActivity.this.clearLogsRow);
                          i = j;
                        } while (paramInt == SettingsActivity.this.switchBackendButtonRow);
                        i = j;
                      } while (paramInt == SettingsActivity.this.telegramFaqRow);
                      i = j;
                    } while (paramInt == SettingsActivity.this.contactsReimportRow);
                    i = j;
                  } while (paramInt == SettingsActivity.this.textSizeRow);
                  i = j;
                } while (paramInt == SettingsActivity.this.languageRow);
                i = j;
              } while (paramInt == SettingsActivity.this.contactsSortRow);
              i = j;
            } while (paramInt == SettingsActivity.this.stickersRow);
            i = j;
          } while (paramInt == SettingsActivity.this.cacheRow);
          i = j;
        } while (paramInt == SettingsActivity.this.privacyPolicyRow);
        if (paramInt == SettingsActivity.this.versionRow) {
          return 5;
        }
        if ((paramInt == SettingsActivity.this.wifiDownloadRow) || (paramInt == SettingsActivity.this.mobileDownloadRow) || (paramInt == SettingsActivity.this.roamingDownloadRow) || (paramInt == SettingsActivity.this.numberRow) || (paramInt == SettingsActivity.this.usernameRow)) {
          return 6;
        }
        if ((paramInt == SettingsActivity.this.settingsSectionRow2) || (paramInt == SettingsActivity.this.messagesSectionRow2) || (paramInt == SettingsActivity.this.supportSectionRow2) || (paramInt == SettingsActivity.this.numberSectionRow)) {
          break;
        }
        i = j;
      } while (paramInt != SettingsActivity.this.mediaDownloadSection2);
      return 4;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      int j = 1;
      int i;
      switch (paramViewHolder.getItemViewType())
      {
      case 1: 
      case 5: 
      default: 
        i = 0;
        if (i != 0)
        {
          if ((paramInt != SettingsActivity.this.textSizeRow) && (paramInt != SettingsActivity.this.enableAnimationsRow) && (paramInt != SettingsActivity.this.notificationRow) && (paramInt != SettingsActivity.this.backgroundRow) && (paramInt != SettingsActivity.this.numberRow) && (paramInt != SettingsActivity.this.askQuestionRow) && (paramInt != SettingsActivity.this.sendLogsRow) && (paramInt != SettingsActivity.this.sendByEnterRow) && (paramInt != SettingsActivity.this.autoplayGifsRow) && (paramInt != SettingsActivity.this.privacyRow) && (paramInt != SettingsActivity.this.wifiDownloadRow) && (paramInt != SettingsActivity.this.mobileDownloadRow) && (paramInt != SettingsActivity.this.clearLogsRow) && (paramInt != SettingsActivity.this.roamingDownloadRow) && (paramInt != SettingsActivity.this.languageRow) && (paramInt != SettingsActivity.this.usernameRow) && (paramInt != SettingsActivity.this.switchBackendButtonRow) && (paramInt != SettingsActivity.this.telegramFaqRow) && (paramInt != SettingsActivity.this.contactsSortRow) && (paramInt != SettingsActivity.this.contactsReimportRow) && (paramInt != SettingsActivity.this.saveToGalleryRow) && (paramInt != SettingsActivity.this.stickersRow) && (paramInt != SettingsActivity.this.cacheRow) && (paramInt != SettingsActivity.this.raiseToSpeakRow) && (paramInt != SettingsActivity.this.privacyPolicyRow) && (paramInt != SettingsActivity.this.customTabsRow) && (paramInt != SettingsActivity.this.directShareRow) && (paramInt != SettingsActivity.this.versionRow)) {
            break label2373;
          }
          if (paramViewHolder.itemView.getBackground() == null) {
            paramViewHolder.itemView.setBackgroundResource(2130837796);
          }
        }
        break;
      }
      label2373:
      while (paramViewHolder.itemView.getBackground() == null)
      {
        return;
        if (paramInt == SettingsActivity.this.overscrollRow) {
          ((EmptyCell)paramViewHolder.itemView).setHeight(AndroidUtilities.dp(88.0F));
        }
        for (;;)
        {
          i = 0;
          break;
          ((EmptyCell)paramViewHolder.itemView).setHeight(AndroidUtilities.dp(16.0F));
        }
        Object localObject2 = (TextSettingsCell)paramViewHolder.itemView;
        if (paramInt == SettingsActivity.this.textSizeRow)
        {
          localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
          if (AndroidUtilities.isTablet()) {}
          for (i = 18;; i = 16)
          {
            i = ((SharedPreferences)localObject1).getInt("fons_size", i);
            ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("TextSize", 2131166331), String.format("%d", new Object[] { Integer.valueOf(i) }), true);
            i = j;
            break;
          }
        }
        if (paramInt == SettingsActivity.this.languageRow)
        {
          ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("Language", 2131165784), LocaleController.getCurrentLanguageName(), true);
          i = j;
          break;
        }
        if (paramInt == SettingsActivity.this.contactsSortRow)
        {
          i = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("sortContactsBy", 0);
          if (i == 0) {
            localObject1 = LocaleController.getString("Default", 2131165559);
          }
          for (;;)
          {
            ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("SortBy", 2131166302), (String)localObject1, true);
            i = j;
            break;
            if (i == 1) {
              localObject1 = LocaleController.getString("FirstName", 2131166303);
            } else {
              localObject1 = LocaleController.getString("LastName", 2131166304);
            }
          }
        }
        if (paramInt == SettingsActivity.this.notificationRow)
        {
          ((TextSettingsCell)localObject2).setText(LocaleController.getString("NotificationsAndSounds", 2131166031), true);
          i = j;
          break;
        }
        if (paramInt == SettingsActivity.this.backgroundRow)
        {
          ((TextSettingsCell)localObject2).setText(LocaleController.getString("ChatBackground", 2131165489), true);
          i = j;
          break;
        }
        if (paramInt == SettingsActivity.this.sendLogsRow)
        {
          ((TextSettingsCell)localObject2).setText("Send Logs", true);
          i = j;
          break;
        }
        if (paramInt == SettingsActivity.this.clearLogsRow)
        {
          ((TextSettingsCell)localObject2).setText("Clear Logs", true);
          i = j;
          break;
        }
        if (paramInt == SettingsActivity.this.askQuestionRow)
        {
          ((TextSettingsCell)localObject2).setText(LocaleController.getString("AskAQuestion", 2131165332), true);
          i = j;
          break;
        }
        if (paramInt == SettingsActivity.this.privacyRow)
        {
          ((TextSettingsCell)localObject2).setText(LocaleController.getString("PrivacySettings", 2131166143), true);
          i = j;
          break;
        }
        if (paramInt == SettingsActivity.this.switchBackendButtonRow)
        {
          ((TextSettingsCell)localObject2).setText("Switch Backend", true);
          i = j;
          break;
        }
        if (paramInt == SettingsActivity.this.telegramFaqRow)
        {
          ((TextSettingsCell)localObject2).setText(LocaleController.getString("TelegramFAQ", 2131166326), true);
          i = j;
          break;
        }
        if (paramInt == SettingsActivity.this.contactsReimportRow)
        {
          ((TextSettingsCell)localObject2).setText(LocaleController.getString("ImportContacts", 2131165745), true);
          i = j;
          break;
        }
        String str;
        if (paramInt == SettingsActivity.this.stickersRow)
        {
          i = StickersQuery.getUnreadStickerSets().size();
          str = LocaleController.getString("Stickers", 2131166310);
          if (i != 0) {}
          for (localObject1 = String.format("%d", new Object[] { Integer.valueOf(i) });; localObject1 = "")
          {
            ((TextSettingsCell)localObject2).setTextAndValue(str, (String)localObject1, true);
            i = j;
            break;
          }
        }
        if (paramInt == SettingsActivity.this.cacheRow)
        {
          ((TextSettingsCell)localObject2).setText(LocaleController.getString("CacheSettings", 2131165381), true);
          i = j;
          break;
        }
        i = j;
        if (paramInt != SettingsActivity.this.privacyPolicyRow) {
          break;
        }
        ((TextSettingsCell)localObject2).setText(LocaleController.getString("PrivacyPolicy", 2131166141), true);
        i = j;
        break;
        Object localObject1 = (TextCheckCell)paramViewHolder.itemView;
        localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        if (paramInt == SettingsActivity.this.enableAnimationsRow)
        {
          ((TextCheckCell)localObject1).setTextAndCheck(LocaleController.getString("EnableAnimations", 2131165601), ((SharedPreferences)localObject2).getBoolean("view_animations", true), false);
          i = j;
          break;
        }
        if (paramInt == SettingsActivity.this.sendByEnterRow)
        {
          ((TextCheckCell)localObject1).setTextAndCheck(LocaleController.getString("SendByEnter", 2131166234), ((SharedPreferences)localObject2).getBoolean("send_by_enter", false), false);
          i = j;
          break;
        }
        if (paramInt == SettingsActivity.this.saveToGalleryRow)
        {
          ((TextCheckCell)localObject1).setTextAndCheck(LocaleController.getString("SaveToGallerySettings", 2131166203), MediaController.getInstance().canSaveToGallery(), false);
          i = j;
          break;
        }
        if (paramInt == SettingsActivity.this.autoplayGifsRow)
        {
          ((TextCheckCell)localObject1).setTextAndCheck(LocaleController.getString("AutoplayGifs", 2131165354), MediaController.getInstance().canAutoplayGifs(), true);
          i = j;
          break;
        }
        if (paramInt == SettingsActivity.this.raiseToSpeakRow)
        {
          ((TextCheckCell)localObject1).setTextAndCheck(LocaleController.getString("RaiseToSpeak", 2131166145), MediaController.getInstance().canRaiseToSpeak(), true);
          i = j;
          break;
        }
        if (paramInt == SettingsActivity.this.customTabsRow)
        {
          ((TextCheckCell)localObject1).setTextAndValueAndCheck(LocaleController.getString("ChromeCustomTabs", 2131165506), LocaleController.getString("ChromeCustomTabsInfo", 2131165507), MediaController.getInstance().canCustomTabs(), false, true);
          i = j;
          break;
        }
        i = j;
        if (paramInt != SettingsActivity.this.directShareRow) {
          break;
        }
        ((TextCheckCell)localObject1).setTextAndValueAndCheck(LocaleController.getString("DirectShare", 2131165585), LocaleController.getString("DirectShareInfo", 2131165586), MediaController.getInstance().canDirectShare(), false, true);
        i = j;
        break;
        if (paramInt == SettingsActivity.this.settingsSectionRow2)
        {
          ((HeaderCell)paramViewHolder.itemView).setText(LocaleController.getString("SETTINGS", 2131166196));
          i = j;
          break;
        }
        if (paramInt == SettingsActivity.this.supportSectionRow2)
        {
          ((HeaderCell)paramViewHolder.itemView).setText(LocaleController.getString("Support", 2131166322));
          i = j;
          break;
        }
        if (paramInt == SettingsActivity.this.messagesSectionRow2)
        {
          ((HeaderCell)paramViewHolder.itemView).setText(LocaleController.getString("MessagesSettings", 2131165879));
          i = j;
          break;
        }
        if (paramInt == SettingsActivity.this.mediaDownloadSection2)
        {
          ((HeaderCell)paramViewHolder.itemView).setText(LocaleController.getString("AutomaticMediaDownload", 2131165353));
          i = j;
          break;
        }
        i = j;
        if (paramInt != SettingsActivity.this.numberSectionRow) {
          break;
        }
        ((HeaderCell)paramViewHolder.itemView).setText(LocaleController.getString("Info", 2131165752));
        i = j;
        break;
        TextDetailSettingsCell localTextDetailSettingsCell = (TextDetailSettingsCell)paramViewHolder.itemView;
        if ((paramInt == SettingsActivity.this.mobileDownloadRow) || (paramInt == SettingsActivity.this.wifiDownloadRow) || (paramInt == SettingsActivity.this.roamingDownloadRow))
        {
          ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
          if (paramInt == SettingsActivity.this.mobileDownloadRow)
          {
            str = LocaleController.getString("WhenUsingMobileData", 2131166397);
            i = MediaController.getInstance().mobileDataDownloadMask;
          }
          for (;;)
          {
            localObject2 = "";
            if ((i & 0x1) != 0) {
              localObject2 = "" + LocaleController.getString("AttachPhoto", 2131165343);
            }
            localObject1 = localObject2;
            if ((i & 0x2) != 0)
            {
              localObject1 = localObject2;
              if (((String)localObject2).length() != 0) {
                localObject1 = (String)localObject2 + ", ";
              }
              localObject1 = (String)localObject1 + LocaleController.getString("AttachAudio", 2131165335);
            }
            localObject2 = localObject1;
            if ((i & 0x4) != 0)
            {
              localObject2 = localObject1;
              if (((String)localObject1).length() != 0) {
                localObject2 = (String)localObject1 + ", ";
              }
              localObject2 = (String)localObject2 + LocaleController.getString("AttachVideo", 2131165345);
            }
            localObject1 = localObject2;
            if ((i & 0x8) != 0)
            {
              localObject1 = localObject2;
              if (((String)localObject2).length() != 0) {
                localObject1 = (String)localObject2 + ", ";
              }
              localObject1 = (String)localObject1 + LocaleController.getString("AttachDocument", 2131165338);
            }
            localObject2 = localObject1;
            if ((i & 0x10) != 0)
            {
              localObject2 = localObject1;
              if (((String)localObject1).length() != 0) {
                localObject2 = (String)localObject1 + ", ";
              }
              localObject2 = (String)localObject2 + LocaleController.getString("AttachMusic", 2131165342);
            }
            localObject1 = localObject2;
            if ((i & 0x20) != 0)
            {
              localObject1 = localObject2;
              if (((String)localObject2).length() != 0) {
                localObject1 = (String)localObject2 + ", ";
              }
              localObject1 = (String)localObject1 + LocaleController.getString("AttachGif", 2131165340);
            }
            localObject2 = localObject1;
            if (((String)localObject1).length() == 0) {
              localObject2 = LocaleController.getString("NoMediaAutoDownload", 2131165938);
            }
            localTextDetailSettingsCell.setTextAndValue(str, (String)localObject2, true);
            i = j;
            break;
            if (paramInt == SettingsActivity.this.wifiDownloadRow)
            {
              str = LocaleController.getString("WhenConnectedOnWiFi", 2131166395);
              i = MediaController.getInstance().wifiDownloadMask;
            }
            else
            {
              str = LocaleController.getString("WhenRoaming", 2131166396);
              i = MediaController.getInstance().roamingDownloadMask;
            }
          }
        }
        if (paramInt == SettingsActivity.this.numberRow)
        {
          localObject1 = UserConfig.getCurrentUser();
          if ((localObject1 != null) && (((TLRPC.User)localObject1).phone != null) && (((TLRPC.User)localObject1).phone.length() != 0)) {}
          for (localObject1 = PhoneFormat.getInstance().format("+" + ((TLRPC.User)localObject1).phone);; localObject1 = LocaleController.getString("NumberUnknown", 2131166043))
          {
            localTextDetailSettingsCell.setTextAndValue((String)localObject1, LocaleController.getString("Phone", 2131166103), true);
            i = j;
            break;
          }
        }
        i = j;
        if (paramInt != SettingsActivity.this.usernameRow) {
          break;
        }
        localObject1 = UserConfig.getCurrentUser();
        if ((localObject1 != null) && (((TLRPC.User)localObject1).username != null) && (((TLRPC.User)localObject1).username.length() != 0)) {}
        for (localObject1 = "@" + ((TLRPC.User)localObject1).username;; localObject1 = LocaleController.getString("UsernameEmpty", 2131166368))
        {
          localTextDetailSettingsCell.setTextAndValue((String)localObject1, LocaleController.getString("Username", 2131166365), false);
          i = j;
          break;
        }
      }
      paramViewHolder.itemView.setBackgroundDrawable(null);
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = null;
      TextInfoCell local3;
      switch (paramInt)
      {
      default: 
      case 0: 
      case 1: 
      case 2: 
      case 3: 
      case 4: 
        for (;;)
        {
          paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
          return new Holder(paramViewGroup);
          paramViewGroup = new EmptyCell(this.mContext);
          continue;
          paramViewGroup = new ShadowSectionCell(this.mContext);
          continue;
          paramViewGroup = new TextSettingsCell(this.mContext)
          {
            public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
            {
              if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramAnonymousMotionEvent.getAction() == 0) || (paramAnonymousMotionEvent.getAction() == 2))) {
                getBackground().setHotspot(paramAnonymousMotionEvent.getX(), paramAnonymousMotionEvent.getY());
              }
              return super.onTouchEvent(paramAnonymousMotionEvent);
            }
          };
          continue;
          paramViewGroup = new TextCheckCell(this.mContext)
          {
            public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
            {
              if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramAnonymousMotionEvent.getAction() == 0) || (paramAnonymousMotionEvent.getAction() == 2))) {
                getBackground().setHotspot(paramAnonymousMotionEvent.getX(), paramAnonymousMotionEvent.getY());
              }
              return super.onTouchEvent(paramAnonymousMotionEvent);
            }
          };
          continue;
          paramViewGroup = new HeaderCell(this.mContext);
        }
      case 5: 
        local3 = new TextInfoCell(this.mContext)
        {
          public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
          {
            if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramAnonymousMotionEvent.getAction() == 0) || (paramAnonymousMotionEvent.getAction() == 2))) {
              getBackground().setHotspot(paramAnonymousMotionEvent.getX(), paramAnonymousMotionEvent.getY());
            }
            return super.onTouchEvent(paramAnonymousMotionEvent);
          }
        };
      }
      for (;;)
      {
        try
        {
          PackageInfo localPackageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
          paramInt = localPackageInfo.versionCode / 10;
          paramViewGroup = "";
          switch (localPackageInfo.versionCode % 10)
          {
          case 0: 
            ((TextInfoCell)local3).setText(String.format(Locale.US, "Telegram for Android v%s (%d) %s", new Object[] { localPackageInfo.versionName, Integer.valueOf(paramInt), paramViewGroup }));
            paramViewGroup = local3;
          }
        }
        catch (Exception paramViewGroup)
        {
          FileLog.e("tmessages", paramViewGroup);
          paramViewGroup = local3;
        }
        break;
        paramViewGroup = "arm";
        continue;
        paramViewGroup = "universal";
        continue;
        paramViewGroup = new TextDetailSettingsCell(this.mContext)
        {
          public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
          {
            if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramAnonymousMotionEvent.getAction() == 0) || (paramAnonymousMotionEvent.getAction() == 2))) {
              getBackground().setHotspot(paramAnonymousMotionEvent.getX(), paramAnonymousMotionEvent.getY());
            }
            return super.onTouchEvent(paramAnonymousMotionEvent);
          }
        };
        break;
        continue;
        paramViewGroup = "arm-v7a";
        continue;
        paramViewGroup = "x86";
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
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/SettingsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */