package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
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
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
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
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.TL_userProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_userProfilePhotoEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
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
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class SettingsActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int edit_name = 1;
  private static final int logout = 2;
  private int askQuestionRow;
  private int autoplayGifsRow;
  private AvatarDrawable avatarDrawable;
  private BackupImageView avatarImage;
  private AvatarUpdater avatarUpdater = new AvatarUpdater();
  private int backgroundRow;
  private int bioRow;
  private int clearLogsRow;
  private int contactsReimportRow;
  private int contactsSectionRow;
  private int contactsSortRow;
  private int customTabsRow;
  private int dataRow;
  private int directShareRow;
  private int dumpCallStatsRow;
  private int emojiRow;
  private int emptyRow;
  private int enableAnimationsRow;
  private int extraHeight;
  private View extraHeightView;
  private int forceTcpInCallsRow;
  private int languageRow;
  private LinearLayoutManager layoutManager;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int messagesSectionRow;
  private int messagesSectionRow2;
  private TextView nameTextView;
  private int notificationRow;
  private int numberRow;
  private int numberSectionRow;
  private TextView onlineTextView;
  private int overscrollRow;
  private int privacyPolicyRow;
  private int privacyRow;
  private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider()
  {
    public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramAnonymousMessageObject, TLRPC.FileLocation paramAnonymousFileLocation, int paramAnonymousInt)
    {
      Object localObject1 = null;
      paramAnonymousInt = 0;
      if (paramAnonymousFileLocation == null) {
        paramAnonymousMessageObject = (MessageObject)localObject1;
      }
      Object localObject2;
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
                  return paramAnonymousMessageObject;
                  localObject2 = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
                  paramAnonymousMessageObject = (MessageObject)localObject1;
                } while (localObject2 == null);
                paramAnonymousMessageObject = (MessageObject)localObject1;
              } while (((TLRPC.User)localObject2).photo == null);
              paramAnonymousMessageObject = (MessageObject)localObject1;
            } while (((TLRPC.User)localObject2).photo.photo_big == null);
            localObject2 = ((TLRPC.User)localObject2).photo.photo_big;
            paramAnonymousMessageObject = (MessageObject)localObject1;
          } while (((TLRPC.FileLocation)localObject2).local_id != paramAnonymousFileLocation.local_id);
          paramAnonymousMessageObject = (MessageObject)localObject1;
        } while (((TLRPC.FileLocation)localObject2).volume_id != paramAnonymousFileLocation.volume_id);
        paramAnonymousMessageObject = (MessageObject)localObject1;
      } while (((TLRPC.FileLocation)localObject2).dc_id != paramAnonymousFileLocation.dc_id);
      paramAnonymousFileLocation = new int[2];
      SettingsActivity.this.avatarImage.getLocationInWindow(paramAnonymousFileLocation);
      paramAnonymousMessageObject = new PhotoViewer.PlaceProviderObject();
      paramAnonymousMessageObject.viewX = paramAnonymousFileLocation[0];
      int i = paramAnonymousFileLocation[1];
      if (Build.VERSION.SDK_INT >= 21) {}
      for (;;)
      {
        paramAnonymousMessageObject.viewY = (i - paramAnonymousInt);
        paramAnonymousMessageObject.parentView = SettingsActivity.this.avatarImage;
        paramAnonymousMessageObject.imageReceiver = SettingsActivity.this.avatarImage.getImageReceiver();
        paramAnonymousMessageObject.dialogId = UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId();
        paramAnonymousMessageObject.thumb = paramAnonymousMessageObject.imageReceiver.getBitmapSafe();
        paramAnonymousMessageObject.size = -1;
        paramAnonymousMessageObject.radius = SettingsActivity.this.avatarImage.getImageReceiver().getRoundRadius();
        paramAnonymousMessageObject.scale = SettingsActivity.this.avatarImage.getScaleX();
        break;
        paramAnonymousInt = AndroidUtilities.statusBarHeight;
      }
    }
    
    public void willHidePhotoViewer()
    {
      SettingsActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
    }
  };
  private int raiseToSpeakRow;
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
  private int themeRow;
  private int usernameRow;
  private int versionRow;
  private ImageView writeButton;
  private AnimatorSet writeButtonAnimation;
  
  private void fixLayout()
  {
    if (this.fragmentView == null) {}
    for (;;)
    {
      return;
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
  }
  
  private void needLayout()
  {
    float f1;
    label130:
    final boolean bool1;
    label163:
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
          break label620;
        }
        i = AndroidUtilities.statusBarHeight;
        ((ImageView)localObject).setTranslationY(i + ActionBar.getCurrentActionBarHeight() + this.extraHeight - AndroidUtilities.dp(29.5F));
        if (f1 <= 0.2F) {
          break label625;
        }
        bool1 = true;
        if (this.writeButton.getTag() != null) {
          break label631;
        }
        bool2 = true;
        label176:
        if (bool1 != bool2)
        {
          if (!bool1) {
            break label637;
          }
          this.writeButton.setTag(null);
          this.writeButton.setVisibility(0);
          label204:
          if (this.writeButtonAnimation != null)
          {
            localObject = this.writeButtonAnimation;
            this.writeButtonAnimation = null;
            ((AnimatorSet)localObject).cancel();
          }
          this.writeButtonAnimation = new AnimatorSet();
          if (!bool1) {
            break label651;
          }
          this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
          this.writeButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[] { 1.0F }) });
          label326:
          this.writeButtonAnimation.setDuration(150L);
          this.writeButtonAnimation.addListener(new AnimatorListenerAdapter()
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
                SettingsActivity.access$7802(SettingsActivity.this, null);
                return;
              }
            }
          });
          this.writeButtonAnimation.start();
        }
        this.avatarImage.setScaleX((42.0F + 18.0F * f1) / 42.0F);
        this.avatarImage.setScaleY((42.0F + 18.0F * f1) / 42.0F);
        if (!this.actionBar.getOccupyStatusBar()) {
          break label743;
        }
      }
    }
    label620:
    label625:
    label631:
    label637:
    label651:
    label743:
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
      break label130;
      bool1 = false;
      break label163;
      bool2 = false;
      break label176;
      this.writeButton.setTag(Integer.valueOf(0));
      break label204;
      this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
      this.writeButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[] { 0.2F }), ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[] { 0.2F }), ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[] { 0.0F }) });
      break label326;
    }
  }
  
  private void performAskAQuestion()
  {
    final SharedPreferences localSharedPreferences = MessagesController.getMainSettings(this.currentAccount);
    int i = localSharedPreferences.getInt("support_id", 0);
    Object localObject1 = null;
    Object localObject3;
    if (i != 0)
    {
      localObject2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
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
      byte[] arrayOfByte = Base64.decode((String)localObject3, 0);
      localObject1 = localObject2;
      if (arrayOfByte != null)
      {
        localObject3 = new org/telegram/tgnet/SerializedData;
        ((SerializedData)localObject3).<init>(arrayOfByte);
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
      for (;;)
      {
        FileLog.e(localException);
        TLRPC.User localUser = null;
        continue;
        MessagesController.getInstance(this.currentAccount).putUser(localUser, true);
        localObject2 = new Bundle();
        ((Bundle)localObject2).putInt("user_id", localUser.id);
        presentFragment(new ChatActivity((Bundle)localObject2));
      }
    }
    if (localObject1 == null)
    {
      localObject2 = new AlertDialog(getParentActivity(), 1);
      ((AlertDialog)localObject2).setMessage(LocaleController.getString("Loading", NUM));
      ((AlertDialog)localObject2).setCanceledOnTouchOutside(false);
      ((AlertDialog)localObject2).setCancelable(false);
      ((AlertDialog)localObject2).show();
      localObject1 = new TLRPC.TL_help_getSupport();
      ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject1, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                Object localObject = SettingsActivity.12.this.val$preferences.edit();
                ((SharedPreferences.Editor)localObject).putInt("support_id", paramAnonymousTLObject.user.id);
                SerializedData localSerializedData = new SerializedData();
                paramAnonymousTLObject.user.serializeToStream(localSerializedData);
                ((SharedPreferences.Editor)localObject).putString("support_user", Base64.encodeToString(localSerializedData.toByteArray(), 0));
                ((SharedPreferences.Editor)localObject).commit();
                localSerializedData.cleanup();
                try
                {
                  SettingsActivity.12.this.val$progressDialog.dismiss();
                  localObject = new ArrayList();
                  ((ArrayList)localObject).add(paramAnonymousTLObject.user);
                  MessagesStorage.getInstance(SettingsActivity.this.currentAccount).putUsersAndChats((ArrayList)localObject, null, true, true);
                  MessagesController.getInstance(SettingsActivity.this.currentAccount).putUser(paramAnonymousTLObject.user, false);
                  localObject = new Bundle();
                  ((Bundle)localObject).putInt("user_id", paramAnonymousTLObject.user.id);
                  SettingsActivity.this.presentFragment(new ChatActivity((Bundle)localObject));
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
            });
          }
          for (;;)
          {
            return;
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                try
                {
                  SettingsActivity.12.this.val$progressDialog.dismiss();
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
            });
          }
        }
      });
      return;
    }
  }
  
  private void sendLogs()
  {
    Object localObject2;
    try
    {
      ArrayList localArrayList = new java/util/ArrayList;
      localArrayList.<init>();
      File localFile = ApplicationLoader.applicationContext.getExternalFilesDir(null);
      Object localObject1 = new java/io/File;
      localObject2 = new java/lang/StringBuilder;
      ((StringBuilder)localObject2).<init>();
      ((File)localObject1).<init>(localFile.getAbsolutePath() + "/logs");
      localObject1 = ((File)localObject1).listFiles();
      int i = localObject1.length;
      int j = 0;
      if (j < i)
      {
        localObject2 = localObject1[j];
        if (Build.VERSION.SDK_INT >= 24) {
          localArrayList.add(FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", (File)localObject2));
        }
        for (;;)
        {
          j++;
          break;
          localArrayList.add(Uri.fromFile((File)localObject2));
        }
        return;
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    for (;;)
    {
      if (!localException.isEmpty())
      {
        localObject2 = new android/content/Intent;
        ((Intent)localObject2).<init>("android.intent.action.SEND_MULTIPLE");
        if (Build.VERSION.SDK_INT >= 24) {
          ((Intent)localObject2).addFlags(1);
        }
        ((Intent)localObject2).setType("message/rfc822");
        ((Intent)localObject2).putExtra("android.intent.extra.EMAIL", "");
        ((Intent)localObject2).putExtra("android.intent.extra.SUBJECT", "last logs");
        ((Intent)localObject2).putParcelableArrayListExtra("android.intent.extra.STREAM", localException);
        getParentActivity().startActivityForResult(Intent.createChooser((Intent)localObject2, "Select email application."), 500);
      }
    }
  }
  
  private void updateUserData()
  {
    boolean bool1 = true;
    TLRPC.User localUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
    Object localObject = null;
    TLRPC.FileLocation localFileLocation = null;
    if (localUser.photo != null)
    {
      localObject = localUser.photo.photo_small;
      localFileLocation = localUser.photo.photo_big;
    }
    this.avatarDrawable = new AvatarDrawable(localUser, true);
    this.avatarDrawable.setColor(Theme.getColor("avatar_backgroundInProfileBlue"));
    if (this.avatarImage != null)
    {
      this.avatarImage.setImage((TLObject)localObject, "50_50", this.avatarDrawable);
      localObject = this.avatarImage.getImageReceiver();
      if (PhotoViewer.isShowingImage(localFileLocation)) {
        break label183;
      }
      bool2 = true;
      ((ImageReceiver)localObject).setVisible(bool2, false);
      this.nameTextView.setText(UserObject.getUserName(localUser));
      this.onlineTextView.setText(LocaleController.getString("Online", NUM));
      localObject = this.avatarImage.getImageReceiver();
      if (PhotoViewer.isShowingImage(localFileLocation)) {
        break label189;
      }
    }
    label183:
    label189:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      ((ImageReceiver)localObject).setVisible(bool2, false);
      return;
      bool2 = false;
      break;
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
    this.actionBar.setItemsBackgroundColor(Theme.getColor("avatar_actionBarSelectorBlue"), false);
    this.actionBar.setItemsColor(Theme.getColor("avatar_actionBarIconBlue"), false);
    this.actionBar.setBackButtonImage(NUM);
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
        for (;;)
        {
          return;
          if (paramAnonymousInt == 1)
          {
            SettingsActivity.this.presentFragment(new ChangeNameActivity());
          }
          else if ((paramAnonymousInt == 2) && (SettingsActivity.this.getParentActivity() != null))
          {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
            localBuilder.setMessage(LocaleController.getString("AreYouSureLogout", NUM));
            localBuilder.setTitle(LocaleController.getString("AppName", NUM));
            localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                MessagesController.getInstance(SettingsActivity.this.currentAccount).performLogout(true);
              }
            });
            localBuilder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            SettingsActivity.this.showDialog(localBuilder.create());
          }
        }
      }
    });
    Object localObject1 = this.actionBar.createMenu().addItem(0, NUM);
    ((ActionBarMenuItem)localObject1).addSubItem(1, LocaleController.getString("EditName", NUM));
    ((ActionBarMenuItem)localObject1).addSubItem(2, LocaleController.getString("LogOut", NUM));
    this.listAdapter = new ListAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext)
    {
      protected boolean drawChild(Canvas paramAnonymousCanvas, View paramAnonymousView, long paramAnonymousLong)
      {
        boolean bool1;
        if (paramAnonymousView == SettingsActivity.this.listView)
        {
          bool1 = super.drawChild(paramAnonymousCanvas, paramAnonymousView, paramAnonymousLong);
          bool2 = bool1;
          if (SettingsActivity.this.parentLayout != null)
          {
            int i = 0;
            int j = getChildCount();
            int k = 0;
            int m = i;
            if (k < j)
            {
              View localView = getChildAt(k);
              if (localView == paramAnonymousView) {}
              while ((!(localView instanceof ActionBar)) || (localView.getVisibility() != 0))
              {
                k++;
                break;
              }
              m = i;
              if (((ActionBar)localView).getCastShadows()) {
                m = localView.getMeasuredHeight();
              }
            }
            SettingsActivity.this.parentLayout.drawHeaderShadow(paramAnonymousCanvas, m);
          }
        }
        for (boolean bool2 = bool1;; bool2 = super.drawChild(paramAnonymousCanvas, paramAnonymousView, paramAnonymousLong)) {
          return bool2;
        }
      }
    };
    this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.listView = new RecyclerListView(paramContext);
    this.listView.setVerticalScrollBarEnabled(false);
    Object localObject2 = this.listView;
    localObject1 = new LinearLayoutManager(paramContext, 1, false)
    {
      public boolean supportsPredictiveItemAnimations()
      {
        return false;
      }
    };
    this.layoutManager = ((LinearLayoutManager)localObject1);
    ((RecyclerListView)localObject2).setLayoutManager((RecyclerView.LayoutManager)localObject1);
    this.listView.setGlowColor(Theme.getColor("avatar_backgroundActionBarBlue"));
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
    this.listView.setAdapter(this.listAdapter);
    this.listView.setItemAnimator(null);
    this.listView.setLayoutAnimation(null);
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(final View paramAnonymousView, final int paramAnonymousInt)
      {
        if (paramAnonymousInt == SettingsActivity.this.textSizeRow) {
          if (SettingsActivity.this.getParentActivity() != null) {}
        }
        boolean bool1;
        label167:
        label220:
        label673:
        label727:
        label1600:
        label1733:
        label1760:
        label1902:
        label1962:
        label2004:
        do
        {
          do
          {
            do
            {
              for (;;)
              {
                return;
                localObject1 = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
                ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("TextSize", NUM));
                paramAnonymousView = new NumberPicker(SettingsActivity.this.getParentActivity());
                paramAnonymousView.setMinValue(12);
                paramAnonymousView.setMaxValue(30);
                paramAnonymousView.setValue(SharedConfig.fontSize);
                ((AlertDialog.Builder)localObject1).setView(paramAnonymousView);
                ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("Done", NUM), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                  {
                    paramAnonymous2DialogInterface = MessagesController.getGlobalMainSettings().edit();
                    paramAnonymous2DialogInterface.putInt("fons_size", paramAnonymousView.getValue());
                    SharedConfig.fontSize = paramAnonymousView.getValue();
                    paramAnonymous2DialogInterface.commit();
                    if (SettingsActivity.this.listAdapter != null) {
                      SettingsActivity.this.listAdapter.notifyItemChanged(paramAnonymousInt);
                    }
                  }
                });
                SettingsActivity.this.showDialog(((AlertDialog.Builder)localObject1).create());
                continue;
                if (paramAnonymousInt == SettingsActivity.this.enableAnimationsRow)
                {
                  localObject1 = MessagesController.getGlobalMainSettings();
                  bool1 = ((SharedPreferences)localObject1).getBoolean("view_animations", true);
                  localObject1 = ((SharedPreferences)localObject1).edit();
                  if (!bool1)
                  {
                    bool2 = true;
                    ((SharedPreferences.Editor)localObject1).putBoolean("view_animations", bool2);
                    ((SharedPreferences.Editor)localObject1).commit();
                    if (!(paramAnonymousView instanceof TextCheckCell)) {
                      continue;
                    }
                    paramAnonymousView = (TextCheckCell)paramAnonymousView;
                    if (bool1) {
                      break label220;
                    }
                  }
                  for (bool2 = true;; bool2 = false)
                  {
                    paramAnonymousView.setChecked(bool2);
                    break;
                    bool2 = false;
                    break label167;
                  }
                }
                else if (paramAnonymousInt == SettingsActivity.this.notificationRow)
                {
                  SettingsActivity.this.presentFragment(new NotificationsSettingsActivity());
                }
                else if (paramAnonymousInt == SettingsActivity.this.backgroundRow)
                {
                  SettingsActivity.this.presentFragment(new WallpapersActivity());
                }
                else
                {
                  final Object localObject2;
                  Object localObject3;
                  int i;
                  int j;
                  if (paramAnonymousInt == SettingsActivity.this.askQuestionRow)
                  {
                    if (SettingsActivity.this.getParentActivity() != null)
                    {
                      paramAnonymousView = new TextView(SettingsActivity.this.getParentActivity());
                      localObject1 = new SpannableString(Html.fromHtml(LocaleController.getString("AskAQuestionInfo", NUM).replace("\n", "<br>")));
                      localObject2 = (URLSpan[])((Spannable)localObject1).getSpans(0, ((Spannable)localObject1).length(), URLSpan.class);
                      for (paramAnonymousInt = 0; paramAnonymousInt < localObject2.length; paramAnonymousInt++)
                      {
                        localObject3 = localObject2[paramAnonymousInt];
                        i = ((Spannable)localObject1).getSpanStart(localObject3);
                        j = ((Spannable)localObject1).getSpanEnd(localObject3);
                        ((Spannable)localObject1).removeSpan(localObject3);
                        ((Spannable)localObject1).setSpan(new URLSpanNoUnderline(((URLSpan)localObject3).getURL())
                        {
                          public void onClick(View paramAnonymous2View)
                          {
                            SettingsActivity.this.dismissCurrentDialig();
                            super.onClick(paramAnonymous2View);
                          }
                        }, i, j, 0);
                      }
                      paramAnonymousView.setText((CharSequence)localObject1);
                      paramAnonymousView.setTextSize(1, 16.0F);
                      paramAnonymousView.setLinkTextColor(Theme.getColor("dialogTextLink"));
                      paramAnonymousView.setHighlightColor(Theme.getColor("dialogLinkSelection"));
                      paramAnonymousView.setPadding(AndroidUtilities.dp(23.0F), 0, AndroidUtilities.dp(23.0F), 0);
                      paramAnonymousView.setMovementMethod(new SettingsActivity.LinkMovementMethodMy(null));
                      paramAnonymousView.setTextColor(Theme.getColor("dialogTextBlack"));
                      localObject1 = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
                      ((AlertDialog.Builder)localObject1).setView(paramAnonymousView);
                      ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AskAQuestion", NUM));
                      ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("AskButton", NUM), new DialogInterface.OnClickListener()
                      {
                        public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                        {
                          SettingsActivity.this.performAskAQuestion();
                        }
                      });
                      ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                      SettingsActivity.this.showDialog(((AlertDialog.Builder)localObject1).create());
                    }
                  }
                  else if (paramAnonymousInt == SettingsActivity.this.sendLogsRow)
                  {
                    SettingsActivity.this.sendLogs();
                  }
                  else if (paramAnonymousInt == SettingsActivity.this.clearLogsRow)
                  {
                    FileLog.cleanupLogs();
                  }
                  else if (paramAnonymousInt == SettingsActivity.this.sendByEnterRow)
                  {
                    localObject1 = MessagesController.getGlobalMainSettings();
                    bool1 = ((SharedPreferences)localObject1).getBoolean("send_by_enter", false);
                    localObject1 = ((SharedPreferences)localObject1).edit();
                    if (!bool1)
                    {
                      bool2 = true;
                      ((SharedPreferences.Editor)localObject1).putBoolean("send_by_enter", bool2);
                      ((SharedPreferences.Editor)localObject1).commit();
                      if (!(paramAnonymousView instanceof TextCheckCell)) {
                        continue;
                      }
                      paramAnonymousView = (TextCheckCell)paramAnonymousView;
                      if (bool1) {
                        break label727;
                      }
                    }
                    for (bool2 = true;; bool2 = false)
                    {
                      paramAnonymousView.setChecked(bool2);
                      break;
                      bool2 = false;
                      break label673;
                    }
                  }
                  else if (paramAnonymousInt == SettingsActivity.this.raiseToSpeakRow)
                  {
                    SharedConfig.toogleRaiseToSpeak();
                    if ((paramAnonymousView instanceof TextCheckCell)) {
                      ((TextCheckCell)paramAnonymousView).setChecked(SharedConfig.raiseToSpeak);
                    }
                  }
                  else if (paramAnonymousInt == SettingsActivity.this.autoplayGifsRow)
                  {
                    SharedConfig.toggleAutoplayGifs();
                    if ((paramAnonymousView instanceof TextCheckCell)) {
                      ((TextCheckCell)paramAnonymousView).setChecked(SharedConfig.autoplayGifs);
                    }
                  }
                  else if (paramAnonymousInt == SettingsActivity.this.saveToGalleryRow)
                  {
                    SharedConfig.toggleSaveToGallery();
                    if ((paramAnonymousView instanceof TextCheckCell)) {
                      ((TextCheckCell)paramAnonymousView).setChecked(SharedConfig.saveToGallery);
                    }
                  }
                  else if (paramAnonymousInt == SettingsActivity.this.customTabsRow)
                  {
                    SharedConfig.toggleCustomTabs();
                    if ((paramAnonymousView instanceof TextCheckCell)) {
                      ((TextCheckCell)paramAnonymousView).setChecked(SharedConfig.customTabs);
                    }
                  }
                  else if (paramAnonymousInt == SettingsActivity.this.directShareRow)
                  {
                    SharedConfig.toggleDirectShare();
                    if ((paramAnonymousView instanceof TextCheckCell)) {
                      ((TextCheckCell)paramAnonymousView).setChecked(SharedConfig.directShare);
                    }
                  }
                  else if (paramAnonymousInt == SettingsActivity.this.privacyRow)
                  {
                    SettingsActivity.this.presentFragment(new PrivacySettingsActivity());
                  }
                  else if (paramAnonymousInt == SettingsActivity.this.dataRow)
                  {
                    SettingsActivity.this.presentFragment(new DataSettingsActivity());
                  }
                  else if (paramAnonymousInt == SettingsActivity.this.languageRow)
                  {
                    SettingsActivity.this.presentFragment(new LanguageSelectActivity());
                  }
                  else if (paramAnonymousInt == SettingsActivity.this.themeRow)
                  {
                    SettingsActivity.this.presentFragment(new ThemeActivity(0));
                  }
                  else if (paramAnonymousInt == SettingsActivity.this.switchBackendButtonRow)
                  {
                    if (SettingsActivity.this.getParentActivity() != null)
                    {
                      paramAnonymousView = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
                      paramAnonymousView.setMessage(LocaleController.getString("AreYouSure", NUM));
                      paramAnonymousView.setTitle(LocaleController.getString("AppName", NUM));
                      paramAnonymousView.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
                      {
                        public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                        {
                          SharedConfig.pushAuthKey = null;
                          SharedConfig.pushAuthKeyId = null;
                          SharedConfig.saveConfig();
                          ConnectionsManager.getInstance(SettingsActivity.this.currentAccount).switchBackend();
                        }
                      });
                      paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                      SettingsActivity.this.showDialog(paramAnonymousView.create());
                    }
                  }
                  else if (paramAnonymousInt == SettingsActivity.this.telegramFaqRow)
                  {
                    Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("TelegramFaqUrl", NUM));
                  }
                  else if (paramAnonymousInt == SettingsActivity.this.privacyPolicyRow)
                  {
                    Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", NUM));
                  }
                  else if (paramAnonymousInt != SettingsActivity.this.contactsReimportRow)
                  {
                    Object localObject4;
                    if (paramAnonymousInt == SettingsActivity.this.contactsSortRow)
                    {
                      if (SettingsActivity.this.getParentActivity() != null)
                      {
                        localObject4 = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
                        ((AlertDialog.Builder)localObject4).setTitle(LocaleController.getString("SortBy", NUM));
                        localObject2 = LocaleController.getString("Default", NUM);
                        localObject1 = LocaleController.getString("SortFirstName", NUM);
                        localObject3 = LocaleController.getString("SortLastName", NUM);
                        paramAnonymousView = new DialogInterface.OnClickListener()
                        {
                          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                          {
                            paramAnonymous2DialogInterface = MessagesController.getGlobalMainSettings().edit();
                            paramAnonymous2DialogInterface.putInt("sortContactsBy", paramAnonymous2Int);
                            paramAnonymous2DialogInterface.commit();
                            if (SettingsActivity.this.listAdapter != null) {
                              SettingsActivity.this.listAdapter.notifyItemChanged(paramAnonymousInt);
                            }
                          }
                        };
                        ((AlertDialog.Builder)localObject4).setItems(new CharSequence[] { localObject2, localObject1, localObject3 }, paramAnonymousView);
                        ((AlertDialog.Builder)localObject4).setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                        SettingsActivity.this.showDialog(((AlertDialog.Builder)localObject4).create());
                      }
                    }
                    else if (paramAnonymousInt == SettingsActivity.this.usernameRow)
                    {
                      SettingsActivity.this.presentFragment(new ChangeUsernameActivity());
                    }
                    else if (paramAnonymousInt == SettingsActivity.this.bioRow)
                    {
                      if (MessagesController.getInstance(SettingsActivity.this.currentAccount).getUserFull(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()) != null) {
                        SettingsActivity.this.presentFragment(new ChangeBioActivity());
                      }
                    }
                    else if (paramAnonymousInt == SettingsActivity.this.numberRow)
                    {
                      SettingsActivity.this.presentFragment(new ChangePhoneHelpActivity());
                    }
                    else if (paramAnonymousInt == SettingsActivity.this.stickersRow)
                    {
                      SettingsActivity.this.presentFragment(new StickersActivity(0));
                    }
                    else
                    {
                      if (paramAnonymousInt != SettingsActivity.this.emojiRow) {
                        break;
                      }
                      if (SettingsActivity.this.getParentActivity() != null)
                      {
                        localObject2 = new boolean[2];
                        localObject3 = new BottomSheet.Builder(SettingsActivity.this.getParentActivity());
                        ((BottomSheet.Builder)localObject3).setApplyTopPadding(false);
                        ((BottomSheet.Builder)localObject3).setApplyBottomPadding(false);
                        localObject1 = new LinearLayout(SettingsActivity.this.getParentActivity());
                        ((LinearLayout)localObject1).setOrientation(1);
                        i = 0;
                        if (Build.VERSION.SDK_INT >= 19)
                        {
                          j = 2;
                          if (i >= j) {
                            break label1760;
                          }
                          paramAnonymousView = null;
                          if (i != 0) {
                            break label1733;
                          }
                          localObject2[i] = SharedConfig.allowBigEmoji;
                          paramAnonymousView = LocaleController.getString("EmojiBigSize", NUM);
                        }
                        for (;;)
                        {
                          localObject4 = new CheckBoxCell(SettingsActivity.this.getParentActivity(), 1);
                          ((CheckBoxCell)localObject4).setTag(Integer.valueOf(i));
                          ((CheckBoxCell)localObject4).setBackgroundDrawable(Theme.getSelectorDrawable(false));
                          ((LinearLayout)localObject1).addView((View)localObject4, LayoutHelper.createLinear(-1, 48));
                          ((CheckBoxCell)localObject4).setText(paramAnonymousView, "", localObject2[i], true);
                          ((CheckBoxCell)localObject4).setTextColor(Theme.getColor("dialogTextBlack"));
                          ((CheckBoxCell)localObject4).setOnClickListener(new View.OnClickListener()
                          {
                            public void onClick(View paramAnonymous2View)
                            {
                              paramAnonymous2View = (CheckBoxCell)paramAnonymous2View;
                              int i = ((Integer)paramAnonymous2View.getTag()).intValue();
                              boolean[] arrayOfBoolean = localObject2;
                              if (localObject2[i] == 0) {}
                              for (int j = 1;; j = 0)
                              {
                                arrayOfBoolean[i] = j;
                                paramAnonymous2View.setChecked(localObject2[i], true);
                                return;
                              }
                            }
                          });
                          i++;
                          break;
                          j = 1;
                          break label1600;
                          if (i == 1)
                          {
                            localObject2[i] = SharedConfig.useSystemEmoji;
                            paramAnonymousView = LocaleController.getString("EmojiUseDefault", NUM);
                          }
                        }
                        paramAnonymousView = new BottomSheet.BottomSheetCell(SettingsActivity.this.getParentActivity(), 1);
                        paramAnonymousView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        paramAnonymousView.setTextAndIcon(LocaleController.getString("Save", NUM).toUpperCase(), 0);
                        paramAnonymousView.setTextColor(Theme.getColor("dialogTextBlue2"));
                        paramAnonymousView.setOnClickListener(new View.OnClickListener()
                        {
                          public void onClick(View paramAnonymous2View)
                          {
                            try
                            {
                              if (SettingsActivity.this.visibleDialog != null) {
                                SettingsActivity.this.visibleDialog.dismiss();
                              }
                              paramAnonymous2View = MessagesController.getGlobalMainSettings().edit();
                              int i = localObject2[0];
                              SharedConfig.allowBigEmoji = i;
                              paramAnonymous2View.putBoolean("allowBigEmoji", i);
                              int j = localObject2[1];
                              SharedConfig.useSystemEmoji = j;
                              paramAnonymous2View.putBoolean("useSystemEmoji", j);
                              paramAnonymous2View.commit();
                              if (SettingsActivity.this.listAdapter != null) {
                                SettingsActivity.this.listAdapter.notifyItemChanged(paramAnonymousInt);
                              }
                              return;
                            }
                            catch (Exception paramAnonymous2View)
                            {
                              for (;;)
                              {
                                FileLog.e(paramAnonymous2View);
                              }
                            }
                          }
                        });
                        ((LinearLayout)localObject1).addView(paramAnonymousView, LayoutHelper.createLinear(-1, 48));
                        ((BottomSheet.Builder)localObject3).setCustomView((View)localObject1);
                        SettingsActivity.this.showDialog(((BottomSheet.Builder)localObject3).create());
                      }
                    }
                  }
                }
              }
              if (paramAnonymousInt != SettingsActivity.this.dumpCallStatsRow) {
                break label1962;
              }
              localObject1 = MessagesController.getGlobalMainSettings();
              bool1 = ((SharedPreferences)localObject1).getBoolean("dbg_dump_call_stats", false);
              localObject1 = ((SharedPreferences)localObject1).edit();
              if (bool1) {
                break;
              }
              bool2 = true;
              ((SharedPreferences.Editor)localObject1).putBoolean("dbg_dump_call_stats", bool2);
              ((SharedPreferences.Editor)localObject1).commit();
            } while (!(paramAnonymousView instanceof TextCheckCell));
            paramAnonymousView = (TextCheckCell)paramAnonymousView;
            if (!bool1) {}
            for (bool2 = true;; bool2 = false)
            {
              paramAnonymousView.setChecked(bool2);
              break;
              bool2 = false;
              break label1902;
            }
          } while (paramAnonymousInt != SettingsActivity.this.forceTcpInCallsRow);
          Object localObject1 = MessagesController.getGlobalMainSettings();
          bool1 = ((SharedPreferences)localObject1).getBoolean("dbg_force_tcp_in_calls", false);
          localObject1 = ((SharedPreferences)localObject1).edit();
          if (bool1) {
            break;
          }
          bool2 = true;
          ((SharedPreferences.Editor)localObject1).putBoolean("dbg_force_tcp_in_calls", bool2);
          ((SharedPreferences.Editor)localObject1).commit();
        } while (!(paramAnonymousView instanceof TextCheckCell));
        paramAnonymousView = (TextCheckCell)paramAnonymousView;
        if (!bool1) {}
        for (boolean bool2 = true;; bool2 = false)
        {
          paramAnonymousView.setChecked(bool2);
          break;
          bool2 = false;
          break label2004;
        }
      }
    });
    this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
    {
      private int pressCount = 0;
      
      public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        boolean bool = false;
        String str5;
        if (paramAnonymousInt == SettingsActivity.this.versionRow)
        {
          this.pressCount += 1;
          if ((this.pressCount < 2) && (!BuildVars.DEBUG_PRIVATE_VERSION)) {
            break label252;
          }
          AlertDialog.Builder localBuilder = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
          localBuilder.setTitle(LocaleController.getString("DebugMenu", NUM));
          String str1 = LocaleController.getString("DebugMenuImportContacts", NUM);
          String str2 = LocaleController.getString("DebugMenuReloadContacts", NUM);
          String str3 = LocaleController.getString("DebugMenuResetContacts", NUM);
          String str4 = LocaleController.getString("DebugMenuResetDialogs", NUM);
          if (!BuildVars.LOGS_ENABLED) {
            break label229;
          }
          paramAnonymousView = LocaleController.getString("DebugMenuDisableLogs", NUM);
          if (!SharedConfig.inappCamera) {
            break label240;
          }
          str5 = LocaleController.getString("DebugMenuDisableCamera", NUM);
          label131:
          String str6 = LocaleController.getString("DebugMenuClearMediaCache", NUM);
          DialogInterface.OnClickListener local1 = new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              boolean bool = true;
              if (paramAnonymous2Int == 0)
              {
                UserConfig.getInstance(SettingsActivity.this.currentAccount).syncContacts = true;
                UserConfig.getInstance(SettingsActivity.this.currentAccount).saveConfig(false);
                ContactsController.getInstance(SettingsActivity.this.currentAccount).forceImportContacts();
              }
              for (;;)
              {
                return;
                if (paramAnonymous2Int == 1)
                {
                  ContactsController.getInstance(SettingsActivity.this.currentAccount).loadContacts(false, 0);
                }
                else if (paramAnonymous2Int == 2)
                {
                  ContactsController.getInstance(SettingsActivity.this.currentAccount).resetImportedContacts();
                }
                else if (paramAnonymous2Int == 3)
                {
                  MessagesController.getInstance(SettingsActivity.this.currentAccount).forceResetDialogs();
                }
                else
                {
                  if (paramAnonymous2Int == 4)
                  {
                    if (!BuildVars.LOGS_ENABLED) {}
                    for (;;)
                    {
                      BuildVars.LOGS_ENABLED = bool;
                      ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).commit();
                      break;
                      bool = false;
                    }
                  }
                  if (paramAnonymous2Int == 5) {
                    SharedConfig.toggleInappCamera();
                  } else if (paramAnonymous2Int == 6) {
                    MessagesStorage.getInstance(SettingsActivity.this.currentAccount).clearSentMedia();
                  } else if (paramAnonymous2Int == 7) {
                    SharedConfig.toggleRoundCamera16to9();
                  }
                }
              }
            }
          };
          localBuilder.setItems(new CharSequence[] { str1, str2, str3, str4, paramAnonymousView, str5, str6 }, local1);
          localBuilder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
          SettingsActivity.this.showDialog(localBuilder.create());
        }
        for (;;)
        {
          bool = true;
          return bool;
          label229:
          paramAnonymousView = LocaleController.getString("DebugMenuEnableLogs", NUM);
          break;
          label240:
          str5 = LocaleController.getString("DebugMenuEnableCamera", NUM);
          break label131;
          try
          {
            label252:
            Toast.makeText(SettingsActivity.this.getParentActivity(), "¯\\_(ツ)_/¯", 0).show();
          }
          catch (Exception paramAnonymousView)
          {
            FileLog.e(paramAnonymousView);
          }
        }
      }
    });
    localFrameLayout.addView(this.actionBar);
    this.extraHeightView = new View(paramContext);
    this.extraHeightView.setPivotY(0.0F);
    this.extraHeightView.setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
    localFrameLayout.addView(this.extraHeightView, LayoutHelper.createFrame(-1, 88.0F));
    this.shadowView = new View(paramContext);
    this.shadowView.setBackgroundResource(NUM);
    localFrameLayout.addView(this.shadowView, LayoutHelper.createFrame(-1, 3.0F));
    this.avatarImage = new BackupImageView(paramContext);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0F));
    this.avatarImage.setPivotX(0.0F);
    this.avatarImage.setPivotY(0.0F);
    localFrameLayout.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0F, 51, 64.0F, 0.0F, 0.0F, 0.0F));
    this.avatarImage.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
        if ((paramAnonymousView != null) && (paramAnonymousView.photo != null) && (paramAnonymousView.photo.photo_big != null))
        {
          PhotoViewer.getInstance().setParentActivity(SettingsActivity.this.getParentActivity());
          PhotoViewer.getInstance().openPhoto(paramAnonymousView.photo.photo_big, SettingsActivity.this.provider);
        }
      }
    });
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setTextColor(Theme.getColor("profile_title"));
    this.nameTextView.setTextSize(1, 18.0F);
    this.nameTextView.setLines(1);
    this.nameTextView.setMaxLines(1);
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.nameTextView.setGravity(3);
    this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.nameTextView.setPivotX(0.0F);
    this.nameTextView.setPivotY(0.0F);
    localFrameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0F, 51, 118.0F, 0.0F, 48.0F, 0.0F));
    this.onlineTextView = new TextView(paramContext);
    this.onlineTextView.setTextColor(Theme.getColor("avatar_subtitleInProfileBlue"));
    this.onlineTextView.setTextSize(1, 14.0F);
    this.onlineTextView.setLines(1);
    this.onlineTextView.setMaxLines(1);
    this.onlineTextView.setSingleLine(true);
    this.onlineTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.onlineTextView.setGravity(3);
    localFrameLayout.addView(this.onlineTextView, LayoutHelper.createFrame(-2, -2.0F, 51, 118.0F, 0.0F, 48.0F, 0.0F));
    this.writeButton = new ImageView(paramContext);
    localObject2 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0F), Theme.getColor("profile_actionBackground"), Theme.getColor("profile_actionPressedBackground"));
    localObject1 = localObject2;
    if (Build.VERSION.SDK_INT < 21)
    {
      paramContext = paramContext.getResources().getDrawable(NUM).mutate();
      paramContext.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
      localObject1 = new CombinedDrawable(paramContext, (Drawable)localObject2, 0, 0);
      ((CombinedDrawable)localObject1).setIconSize(AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
    }
    this.writeButton.setBackgroundDrawable((Drawable)localObject1);
    this.writeButton.setImageResource(NUM);
    this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("profile_actionIcon"), PorterDuff.Mode.MULTIPLY));
    this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
    if (Build.VERSION.SDK_INT >= 21)
    {
      paramContext = new StateListAnimator();
      localObject1 = ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
      paramContext.addState(new int[] { 16842919 }, (Animator)localObject1);
      localObject1 = ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
      paramContext.addState(new int[0], (Animator)localObject1);
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
    paramContext = this.writeButton;
    int i;
    if (Build.VERSION.SDK_INT >= 21)
    {
      i = 56;
      if (Build.VERSION.SDK_INT < 21) {
        break label1120;
      }
    }
    label1120:
    for (float f = 56.0F;; f = 60.0F)
    {
      localFrameLayout.addView(paramContext, LayoutHelper.createFrame(i, f, 53, 0.0F, 0.0F, 16.0F, 0.0F));
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
            TLRPC.User localUser = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
            paramAnonymousView = localUser;
            if (localUser == null) {
              paramAnonymousView = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
            }
          } while (paramAnonymousView == null);
          int i = 0;
          if ((paramAnonymousView.photo != null) && (paramAnonymousView.photo.photo_big != null) && (!(paramAnonymousView.photo instanceof TLRPC.TL_userProfilePhotoEmpty)))
          {
            paramAnonymousView = new CharSequence[3];
            paramAnonymousView[0] = LocaleController.getString("FromCamera", NUM);
            paramAnonymousView[1] = LocaleController.getString("FromGalley", NUM);
            paramAnonymousView[2] = LocaleController.getString("DeletePhoto", NUM);
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
                for (;;)
                {
                  return;
                  if (paramAnonymous2Int == 1) {
                    SettingsActivity.this.avatarUpdater.openGallery();
                  } else if (paramAnonymous2Int == 2) {
                    MessagesController.getInstance(SettingsActivity.this.currentAccount).deleteUserPhoto(null);
                  }
                }
              }
            });
            SettingsActivity.this.showDialog(localBuilder.create());
            break;
            paramAnonymousView = new CharSequence[2];
            paramAnonymousView[0] = LocaleController.getString("FromCamera", NUM);
            paramAnonymousView[1] = LocaleController.getString("FromGalley", NUM);
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
          for (;;)
          {
            return;
            paramAnonymousInt1 = 0;
            paramAnonymousRecyclerView = paramAnonymousRecyclerView.getChildAt(0);
            if (paramAnonymousRecyclerView != null)
            {
              if (SettingsActivity.this.layoutManager.findFirstVisibleItemPosition() == 0)
              {
                int i = AndroidUtilities.dp(88.0F);
                paramAnonymousInt1 = paramAnonymousInt2;
                if (paramAnonymousRecyclerView.getTop() < 0) {
                  paramAnonymousInt1 = paramAnonymousRecyclerView.getTop();
                }
                paramAnonymousInt1 = i + paramAnonymousInt1;
              }
              if (SettingsActivity.this.extraHeight != paramAnonymousInt1)
              {
                SettingsActivity.access$7402(SettingsActivity.this, paramAnonymousInt1);
                SettingsActivity.this.needLayout();
              }
            }
          }
        }
      });
      return this.fragmentView;
      i = 60;
      break;
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.updateInterfaces)
    {
      paramInt1 = ((Integer)paramVarArgs[0]).intValue();
      if (((paramInt1 & 0x2) != 0) || ((paramInt1 & 0x1) != 0)) {
        updateUserData();
      }
    }
    for (;;)
    {
      return;
      if (paramInt1 == NotificationCenter.featuredStickersDidLoaded)
      {
        if (this.listAdapter != null) {
          this.listAdapter.notifyItemChanged(this.stickersRow);
        }
      }
      else if (paramInt1 == NotificationCenter.userInfoDidLoaded)
      {
        if ((((Integer)paramVarArgs[0]).intValue() == UserConfig.getInstance(this.currentAccount).getClientUserId()) && (this.listAdapter != null)) {
          this.listAdapter.notifyItemChanged(this.bioRow);
        }
      }
      else if ((paramInt1 == NotificationCenter.emojiDidLoaded) && (this.listView != null)) {
        this.listView.invalidateViews();
      }
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { EmptyCell.class, TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, TextInfoCell.class, TextDetailSettingsCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarBlue");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.extraHeightView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "avatar_actionBarIconBlue");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorBlue");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "profile_title");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.onlineTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfileBlue");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, localThemeDescription12, localThemeDescription13, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, 0, new Class[] { TextDetailSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextDetailSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText5"), new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable }, null, "avatar_text"), new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { this.avatarDrawable }, null, "avatar_backgroundInProfileBlue"), new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "profile_actionIcon"), new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "profile_actionBackground"), new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "profile_actionPressedBackground") };
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
        ConnectionsManager.getInstance(SettingsActivity.this.currentAccount).sendRequest(paramAnonymousPhotoSize1, new RequestDelegate()
        {
          public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
          {
            if (paramAnonymous2TL_error == null)
            {
              paramAnonymous2TL_error = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
              if (paramAnonymous2TL_error != null) {
                break label239;
              }
              paramAnonymous2TL_error = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
              if (paramAnonymous2TL_error != null) {}
            }
            else
            {
              return;
            }
            MessagesController.getInstance(SettingsActivity.this.currentAccount).putUser(paramAnonymous2TL_error, false);
            label85:
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
              MessagesStorage.getInstance(SettingsActivity.this.currentAccount).clearUserPhotos(paramAnonymous2TL_error.id);
              paramAnonymous2TLObject = new ArrayList();
              paramAnonymous2TLObject.add(paramAnonymous2TL_error);
              MessagesStorage.getInstance(SettingsActivity.this.currentAccount).putUsersAndChats(paramAnonymous2TLObject, null, false, true);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationCenter.getInstance(SettingsActivity.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1535) });
                  NotificationCenter.getInstance(SettingsActivity.this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                  UserConfig.getInstance(SettingsActivity.this.currentAccount).saveConfig(true);
                }
              });
              break;
              label239:
              UserConfig.getInstance(SettingsActivity.this.currentAccount).setCurrentUser(paramAnonymous2TL_error);
              break label85;
              if (localPhotoSize != null) {
                paramAnonymous2TL_error.photo.photo_small = localPhotoSize.location;
              }
            }
          }
        });
      }
    };
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userInfoDidLoaded);
    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
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
    this.bioRow = i;
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
    this.dataRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.backgroundRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.themeRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.languageRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.enableAnimationsRow = i;
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
    this.raiseToSpeakRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.sendByEnterRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.autoplayGifsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.saveToGalleryRow = i;
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
    if (BuildVars.LOGS_ENABLED)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.sendLogsRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.clearLogsRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.dumpCallStatsRow = i;
      if (!BuildVars.DEBUG_VERSION) {
        break label772;
      }
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.forceTcpInCallsRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    label772:
    for (this.switchBackendButtonRow = i;; this.switchBackendButtonRow = -1)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.versionRow = i;
      DataQuery.getInstance(this.currentAccount).checkFeaturedStickers();
      MessagesController.getInstance(this.currentAccount).loadFullUser(UserConfig.getInstance(this.currentAccount).getCurrentUser(), this.classGuid, true);
      return true;
      this.sendLogsRow = -1;
      this.clearLogsRow = -1;
      this.dumpCallStatsRow = -1;
      break;
    }
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    if (this.avatarImage != null) {
      this.avatarImage.setImageDrawable(null);
    }
    MessagesController.getInstance(this.currentAccount).cancelLoadFullUser(UserConfig.getInstance(this.currentAccount).getClientUserId());
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoaded);
    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
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
  
  private static class LinkMovementMethodMy
    extends LinkMovementMethod
  {
    public boolean onTouchEvent(TextView paramTextView, Spannable paramSpannable, MotionEvent paramMotionEvent)
    {
      try
      {
        bool = super.onTouchEvent(paramTextView, paramSpannable, paramMotionEvent);
        return bool;
      }
      catch (Exception paramTextView)
      {
        for (;;)
        {
          FileLog.e(paramTextView);
          boolean bool = false;
        }
      }
    }
  }
  
  private class ListAdapter
    extends RecyclerListView.SelectionAdapter
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
      int i = 2;
      int j;
      if ((paramInt == SettingsActivity.this.emptyRow) || (paramInt == SettingsActivity.this.overscrollRow)) {
        j = 0;
      }
      for (;;)
      {
        return j;
        if ((paramInt == SettingsActivity.this.settingsSectionRow) || (paramInt == SettingsActivity.this.supportSectionRow) || (paramInt == SettingsActivity.this.messagesSectionRow) || (paramInt == SettingsActivity.this.contactsSectionRow))
        {
          j = 1;
        }
        else if ((paramInt == SettingsActivity.this.enableAnimationsRow) || (paramInt == SettingsActivity.this.sendByEnterRow) || (paramInt == SettingsActivity.this.saveToGalleryRow) || (paramInt == SettingsActivity.this.autoplayGifsRow) || (paramInt == SettingsActivity.this.raiseToSpeakRow) || (paramInt == SettingsActivity.this.customTabsRow) || (paramInt == SettingsActivity.this.directShareRow) || (paramInt == SettingsActivity.this.dumpCallStatsRow) || (paramInt == SettingsActivity.this.forceTcpInCallsRow))
        {
          j = 3;
        }
        else
        {
          j = i;
          if (paramInt != SettingsActivity.this.notificationRow)
          {
            j = i;
            if (paramInt != SettingsActivity.this.themeRow)
            {
              j = i;
              if (paramInt != SettingsActivity.this.backgroundRow)
              {
                j = i;
                if (paramInt != SettingsActivity.this.askQuestionRow)
                {
                  j = i;
                  if (paramInt != SettingsActivity.this.sendLogsRow)
                  {
                    j = i;
                    if (paramInt != SettingsActivity.this.privacyRow)
                    {
                      j = i;
                      if (paramInt != SettingsActivity.this.clearLogsRow)
                      {
                        j = i;
                        if (paramInt != SettingsActivity.this.switchBackendButtonRow)
                        {
                          j = i;
                          if (paramInt != SettingsActivity.this.telegramFaqRow)
                          {
                            j = i;
                            if (paramInt != SettingsActivity.this.contactsReimportRow)
                            {
                              j = i;
                              if (paramInt != SettingsActivity.this.textSizeRow)
                              {
                                j = i;
                                if (paramInt != SettingsActivity.this.languageRow)
                                {
                                  j = i;
                                  if (paramInt != SettingsActivity.this.contactsSortRow)
                                  {
                                    j = i;
                                    if (paramInt != SettingsActivity.this.stickersRow)
                                    {
                                      j = i;
                                      if (paramInt != SettingsActivity.this.privacyPolicyRow)
                                      {
                                        j = i;
                                        if (paramInt != SettingsActivity.this.emojiRow)
                                        {
                                          j = i;
                                          if (paramInt != SettingsActivity.this.dataRow) {
                                            if (paramInt == SettingsActivity.this.versionRow)
                                            {
                                              j = 5;
                                            }
                                            else if ((paramInt == SettingsActivity.this.numberRow) || (paramInt == SettingsActivity.this.usernameRow) || (paramInt == SettingsActivity.this.bioRow))
                                            {
                                              j = 6;
                                            }
                                            else if ((paramInt != SettingsActivity.this.settingsSectionRow2) && (paramInt != SettingsActivity.this.messagesSectionRow2) && (paramInt != SettingsActivity.this.supportSectionRow2))
                                            {
                                              j = i;
                                              if (paramInt != SettingsActivity.this.numberSectionRow) {}
                                            }
                                            else
                                            {
                                              j = 4;
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      if ((i == SettingsActivity.this.textSizeRow) || (i == SettingsActivity.this.enableAnimationsRow) || (i == SettingsActivity.this.notificationRow) || (i == SettingsActivity.this.backgroundRow) || (i == SettingsActivity.this.numberRow) || (i == SettingsActivity.this.askQuestionRow) || (i == SettingsActivity.this.sendLogsRow) || (i == SettingsActivity.this.sendByEnterRow) || (i == SettingsActivity.this.autoplayGifsRow) || (i == SettingsActivity.this.privacyRow) || (i == SettingsActivity.this.clearLogsRow) || (i == SettingsActivity.this.languageRow) || (i == SettingsActivity.this.usernameRow) || (i == SettingsActivity.this.bioRow) || (i == SettingsActivity.this.switchBackendButtonRow) || (i == SettingsActivity.this.telegramFaqRow) || (i == SettingsActivity.this.contactsSortRow) || (i == SettingsActivity.this.contactsReimportRow) || (i == SettingsActivity.this.saveToGalleryRow) || (i == SettingsActivity.this.stickersRow) || (i == SettingsActivity.this.raiseToSpeakRow) || (i == SettingsActivity.this.privacyPolicyRow) || (i == SettingsActivity.this.customTabsRow) || (i == SettingsActivity.this.directShareRow) || (i == SettingsActivity.this.versionRow) || (i == SettingsActivity.this.emojiRow) || (i == SettingsActivity.this.dataRow) || (i == SettingsActivity.this.themeRow) || (i == SettingsActivity.this.dumpCallStatsRow) || (i == SettingsActivity.this.forceTcpInCallsRow)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      }
      Object localObject;
      do
      {
        for (;;)
        {
          return;
          if (paramInt == SettingsActivity.this.overscrollRow)
          {
            ((EmptyCell)paramViewHolder.itemView).setHeight(AndroidUtilities.dp(88.0F));
          }
          else
          {
            ((EmptyCell)paramViewHolder.itemView).setHeight(AndroidUtilities.dp(16.0F));
            continue;
            localObject = (TextSettingsCell)paramViewHolder.itemView;
            if (paramInt == SettingsActivity.this.textSizeRow)
            {
              paramViewHolder = MessagesController.getGlobalMainSettings();
              if (AndroidUtilities.isTablet()) {}
              for (paramInt = 18;; paramInt = 16)
              {
                paramInt = paramViewHolder.getInt("fons_size", paramInt);
                ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("TextSize", NUM), String.format("%d", new Object[] { Integer.valueOf(paramInt) }), true);
                break;
              }
            }
            if (paramInt == SettingsActivity.this.languageRow)
            {
              ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("Language", NUM), LocaleController.getCurrentLanguageName(), true);
            }
            else if (paramInt == SettingsActivity.this.themeRow)
            {
              ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("Theme", NUM), Theme.getCurrentThemeName(), true);
            }
            else
            {
              if (paramInt == SettingsActivity.this.contactsSortRow)
              {
                paramInt = MessagesController.getGlobalMainSettings().getInt("sortContactsBy", 0);
                if (paramInt == 0) {
                  paramViewHolder = LocaleController.getString("Default", NUM);
                }
                for (;;)
                {
                  ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("SortBy", NUM), paramViewHolder, true);
                  break;
                  if (paramInt == 1) {
                    paramViewHolder = LocaleController.getString("FirstName", NUM);
                  } else {
                    paramViewHolder = LocaleController.getString("LastName", NUM);
                  }
                }
              }
              if (paramInt == SettingsActivity.this.notificationRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("NotificationsAndSounds", NUM), true);
              }
              else if (paramInt == SettingsActivity.this.backgroundRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("ChatBackground", NUM), true);
              }
              else if (paramInt == SettingsActivity.this.sendLogsRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("DebugSendLogs", NUM), true);
              }
              else if (paramInt == SettingsActivity.this.clearLogsRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("DebugClearLogs", NUM), true);
              }
              else if (paramInt == SettingsActivity.this.askQuestionRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("AskAQuestion", NUM), true);
              }
              else if (paramInt == SettingsActivity.this.privacyRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("PrivacySettings", NUM), true);
              }
              else if (paramInt == SettingsActivity.this.dataRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("DataSettings", NUM), true);
              }
              else if (paramInt == SettingsActivity.this.switchBackendButtonRow)
              {
                ((TextSettingsCell)localObject).setText("Switch Backend", true);
              }
              else if (paramInt == SettingsActivity.this.telegramFaqRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("TelegramFAQ", NUM), true);
              }
              else if (paramInt == SettingsActivity.this.contactsReimportRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("ImportContacts", NUM), true);
              }
              else
              {
                if (paramInt == SettingsActivity.this.stickersRow)
                {
                  paramInt = DataQuery.getInstance(SettingsActivity.this.currentAccount).getUnreadStickerSets().size();
                  String str = LocaleController.getString("StickersName", NUM);
                  if (paramInt != 0) {}
                  for (paramViewHolder = String.format("%d", new Object[] { Integer.valueOf(paramInt) });; paramViewHolder = "")
                  {
                    ((TextSettingsCell)localObject).setTextAndValue(str, paramViewHolder, true);
                    break;
                  }
                }
                if (paramInt == SettingsActivity.this.privacyPolicyRow)
                {
                  ((TextSettingsCell)localObject).setText(LocaleController.getString("PrivacyPolicy", NUM), true);
                }
                else if (paramInt == SettingsActivity.this.emojiRow)
                {
                  ((TextSettingsCell)localObject).setText(LocaleController.getString("Emoji", NUM), true);
                  continue;
                  paramViewHolder = (TextCheckCell)paramViewHolder.itemView;
                  localObject = MessagesController.getGlobalMainSettings();
                  if (paramInt == SettingsActivity.this.enableAnimationsRow)
                  {
                    paramViewHolder.setTextAndCheck(LocaleController.getString("EnableAnimations", NUM), ((SharedPreferences)localObject).getBoolean("view_animations", true), false);
                  }
                  else if (paramInt == SettingsActivity.this.sendByEnterRow)
                  {
                    paramViewHolder.setTextAndCheck(LocaleController.getString("SendByEnter", NUM), ((SharedPreferences)localObject).getBoolean("send_by_enter", false), true);
                  }
                  else if (paramInt == SettingsActivity.this.saveToGalleryRow)
                  {
                    paramViewHolder.setTextAndCheck(LocaleController.getString("SaveToGallerySettings", NUM), SharedConfig.saveToGallery, false);
                  }
                  else if (paramInt == SettingsActivity.this.autoplayGifsRow)
                  {
                    paramViewHolder.setTextAndCheck(LocaleController.getString("AutoplayGifs", NUM), SharedConfig.autoplayGifs, true);
                  }
                  else if (paramInt == SettingsActivity.this.raiseToSpeakRow)
                  {
                    paramViewHolder.setTextAndCheck(LocaleController.getString("RaiseToSpeak", NUM), SharedConfig.raiseToSpeak, true);
                  }
                  else if (paramInt == SettingsActivity.this.customTabsRow)
                  {
                    paramViewHolder.setTextAndValueAndCheck(LocaleController.getString("ChromeCustomTabs", NUM), LocaleController.getString("ChromeCustomTabsInfo", NUM), SharedConfig.customTabs, false, true);
                  }
                  else if (paramInt == SettingsActivity.this.directShareRow)
                  {
                    paramViewHolder.setTextAndValueAndCheck(LocaleController.getString("DirectShare", NUM), LocaleController.getString("DirectShareInfo", NUM), SharedConfig.directShare, false, true);
                  }
                  else if (paramInt == SettingsActivity.this.dumpCallStatsRow)
                  {
                    paramViewHolder.setTextAndCheck("Dump detailed call stats", ((SharedPreferences)localObject).getBoolean("dbg_dump_call_stats", false), true);
                  }
                  else if (paramInt == SettingsActivity.this.forceTcpInCallsRow)
                  {
                    paramViewHolder.setTextAndValueAndCheck("Force TCP in calls", "This disables UDP", ((SharedPreferences)localObject).getBoolean("dbg_force_tcp_in_calls", false), false, true);
                    continue;
                    if (paramInt == SettingsActivity.this.settingsSectionRow2) {
                      ((HeaderCell)paramViewHolder.itemView).setText(LocaleController.getString("SETTINGS", NUM));
                    } else if (paramInt == SettingsActivity.this.supportSectionRow2) {
                      ((HeaderCell)paramViewHolder.itemView).setText(LocaleController.getString("Support", NUM));
                    } else if (paramInt == SettingsActivity.this.messagesSectionRow2) {
                      ((HeaderCell)paramViewHolder.itemView).setText(LocaleController.getString("MessagesSettings", NUM));
                    } else if (paramInt == SettingsActivity.this.numberSectionRow) {
                      ((HeaderCell)paramViewHolder.itemView).setText(LocaleController.getString("Info", NUM));
                    }
                  }
                }
              }
            }
          }
        }
        localObject = (TextDetailSettingsCell)paramViewHolder.itemView;
        if (paramInt == SettingsActivity.this.numberRow)
        {
          paramViewHolder = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
          if ((paramViewHolder != null) && (paramViewHolder.phone != null) && (paramViewHolder.phone.length() != 0)) {}
          for (paramViewHolder = PhoneFormat.getInstance().format("+" + paramViewHolder.phone);; paramViewHolder = LocaleController.getString("NumberUnknown", NUM))
          {
            ((TextDetailSettingsCell)localObject).setTextAndValue(paramViewHolder, LocaleController.getString("Phone", NUM), true);
            break;
          }
        }
        if (paramInt == SettingsActivity.this.usernameRow)
        {
          paramViewHolder = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
          if ((paramViewHolder != null) && (!TextUtils.isEmpty(paramViewHolder.username))) {}
          for (paramViewHolder = "@" + paramViewHolder.username;; paramViewHolder = LocaleController.getString("UsernameEmpty", NUM))
          {
            ((TextDetailSettingsCell)localObject).setTextAndValue(paramViewHolder, LocaleController.getString("Username", NUM), true);
            break;
          }
        }
      } while (paramInt != SettingsActivity.this.bioRow);
      paramViewHolder = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUserFull(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId());
      if (paramViewHolder == null) {
        paramViewHolder = LocaleController.getString("Loading", NUM);
      }
      for (;;)
      {
        ((TextDetailSettingsCell)localObject).setTextWithEmojiAndValue(paramViewHolder, LocaleController.getString("UserBio", NUM), false);
        break;
        if (!TextUtils.isEmpty(paramViewHolder.about)) {
          paramViewHolder = paramViewHolder.about;
        } else {
          paramViewHolder = LocaleController.getString("UserBioEmpty", NUM);
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
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new EmptyCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new ShadowSectionCell(this.mContext);
        continue;
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextCheckCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new HeaderCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        TextInfoCell localTextInfoCell = new TextInfoCell(this.mContext);
        localTextInfoCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        for (;;)
        {
          try
          {
            PackageInfo localPackageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            paramInt = localPackageInfo.versionCode / 10;
            paramViewGroup = "";
            switch (localPackageInfo.versionCode % 10)
            {
            default: 
              ((TextInfoCell)localTextInfoCell).setText(LocaleController.formatString("TelegramVersion", NUM, new Object[] { String.format(Locale.US, "v%s (%d) %s", new Object[] { localPackageInfo.versionName, Integer.valueOf(paramInt), paramViewGroup }) }));
              paramViewGroup = localTextInfoCell;
            }
          }
          catch (Exception paramViewGroup)
          {
            FileLog.e(paramViewGroup);
            paramViewGroup = localTextInfoCell;
          }
          break;
          paramViewGroup = "arm-v7a";
          continue;
          paramViewGroup = "x86";
          continue;
          paramViewGroup = "arm64-v8a";
          continue;
          paramViewGroup = "x86_64";
          continue;
          paramViewGroup = new java/lang/StringBuilder;
          paramViewGroup.<init>();
          paramViewGroup = "universal " + Build.CPU_ABI + " " + Build.CPU_ABI2;
        }
        paramViewGroup = new TextDetailSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/SettingsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */