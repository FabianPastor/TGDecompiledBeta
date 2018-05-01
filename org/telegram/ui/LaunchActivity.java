package org.telegram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager.TaskDescription;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StatFs;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.support.widget.DefaultItemAnimator;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatInvite;
import org.telegram.tgnet.TLRPC.LangPackString;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputGameShortName;
import org.telegram.tgnet.TLRPC.TL_inputMediaGame;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_langpack_getStrings;
import org.telegram.tgnet.TLRPC.TL_messages_checkChatInvite;
import org.telegram.tgnet.TLRPC.TL_messages_importChatInvite;
import org.telegram.tgnet.TLRPC.TL_userContact_old2;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.Adapters.DrawerLayoutAdapter;
import org.telegram.ui.Cells.DrawerAddCell;
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.JoinGroupAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PasscodeView.PasscodeViewDelegate;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.SharingLocationsAlert;
import org.telegram.ui.Components.SharingLocationsAlert.SharingLocationsAlertDelegate;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.ThemeEditorView;

public class LaunchActivity
  extends Activity
  implements NotificationCenter.NotificationCenterDelegate, ActionBarLayout.ActionBarLayoutDelegate, DialogsActivity.DialogsActivityDelegate
{
  private static ArrayList<BaseFragment> layerFragmentsStack = new ArrayList();
  private static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList();
  private static ArrayList<BaseFragment> rightFragmentsStack = new ArrayList();
  private ActionBarLayout actionBarLayout;
  private View backgroundTablet;
  private ArrayList<TLRPC.User> contactsToSend;
  private int currentAccount;
  private int currentConnectionState;
  private String documentsMimeType;
  private ArrayList<String> documentsOriginalPathsArray;
  private ArrayList<String> documentsPathsArray;
  private ArrayList<Uri> documentsUrisArray;
  private DrawerLayoutAdapter drawerLayoutAdapter;
  protected DrawerLayoutContainer drawerLayoutContainer;
  private HashMap<String, String> englishLocaleStrings;
  private boolean finished;
  private ActionBarLayout layersActionBarLayout;
  private boolean loadingLocaleDialog;
  private AlertDialog localeDialog;
  private Runnable lockRunnable;
  private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
  private Intent passcodeSaveIntent;
  private boolean passcodeSaveIntentIsNew;
  private boolean passcodeSaveIntentIsRestore;
  private PasscodeView passcodeView;
  private ArrayList<SendMessagesHelper.SendingMediaInfo> photoPathsArray;
  private ActionBarLayout rightActionBarLayout;
  private String sendingText;
  private FrameLayout shadowTablet;
  private FrameLayout shadowTabletSide;
  private RecyclerListView sideMenu;
  private HashMap<String, String> systemLocaleStrings;
  private boolean tabletFullSize;
  private String videoPath;
  private AlertDialog visibleDialog;
  
  private void checkCurrentAccount()
  {
    if (this.currentAccount != UserConfig.selectedAccount)
    {
      NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
      NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mainUserInfoChanged);
      NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdatedConnectionState);
      NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowAlert);
      NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
      NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openArticle);
      NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.hasNewContactsToImport);
    }
    this.currentAccount = UserConfig.selectedAccount;
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mainUserInfoChanged);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdatedConnectionState);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needShowAlert);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.openArticle);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.hasNewContactsToImport);
    updateCurrentConnectionState(this.currentAccount);
  }
  
  private void checkFreeDiscSpace()
  {
    if (Build.VERSION.SDK_INT >= 26) {}
    for (;;)
    {
      return;
      Utilities.globalQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          if (!UserConfig.getInstance(LaunchActivity.this.currentAccount).isClientActivated()) {
            return;
          }
          for (;;)
          {
            try
            {
              SharedPreferences localSharedPreferences = MessagesController.getGlobalMainSettings();
              if (Math.abs(localSharedPreferences.getLong("last_space_check", 0L) - System.currentTimeMillis()) < 259200000L) {
                break;
              }
              File localFile = FileLoader.getDirectory(4);
              if (localFile == null) {
                break;
              }
              Object localObject = new android/os/StatFs;
              ((StatFs)localObject).<init>(localFile.getAbsolutePath());
              if (Build.VERSION.SDK_INT >= 18) {
                break label139;
              }
              l1 = Math.abs(((StatFs)localObject).getAvailableBlocks() * ((StatFs)localObject).getBlockSize());
              localSharedPreferences.edit().putLong("last_space_check", System.currentTimeMillis()).commit();
              if (l1 >= 104857600L) {
                break;
              }
              localObject = new org/telegram/ui/LaunchActivity$23$1;
              ((1)localObject).<init>(this);
              AndroidUtilities.runOnUIThread((Runnable)localObject);
            }
            catch (Throwable localThrowable) {}
            break;
            label139:
            long l1 = localThrowable.getAvailableBlocksLong();
            long l2 = localThrowable.getBlockSizeLong();
            l1 *= l2;
          }
        }
      }, 2000L);
    }
  }
  
  private void checkLayout()
  {
    int i = 0;
    int j = 8;
    if ((!AndroidUtilities.isTablet()) || (this.rightActionBarLayout == null)) {
      return;
    }
    if ((!AndroidUtilities.isInMultiwindow) && ((!AndroidUtilities.isSmallTablet()) || (getResources().getConfiguration().orientation == 2)))
    {
      this.tabletFullSize = false;
      if (this.actionBarLayout.fragmentsStack.size() >= 2)
      {
        for (k = 1; k < this.actionBarLayout.fragmentsStack.size(); k = k - 1 + 1)
        {
          localObject = (BaseFragment)this.actionBarLayout.fragmentsStack.get(k);
          if ((localObject instanceof ChatActivity)) {
            ((ChatActivity)localObject).setIgnoreAttachOnPause(true);
          }
          ((BaseFragment)localObject).onPause();
          this.actionBarLayout.fragmentsStack.remove(k);
          this.rightActionBarLayout.fragmentsStack.add(localObject);
        }
        if (this.passcodeView.getVisibility() != 0)
        {
          this.actionBarLayout.showLastFragment();
          this.rightActionBarLayout.showLastFragment();
        }
      }
      localObject = this.rightActionBarLayout;
      if (this.rightActionBarLayout.fragmentsStack.isEmpty())
      {
        k = 8;
        label198:
        ((ActionBarLayout)localObject).setVisibility(k);
        localObject = this.backgroundTablet;
        if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
          break label266;
        }
        k = 0;
        label225:
        ((View)localObject).setVisibility(k);
        localObject = this.shadowTabletSide;
        if (this.actionBarLayout.fragmentsStack.isEmpty()) {
          break label272;
        }
      }
      label266:
      label272:
      for (k = i;; k = 8)
      {
        ((FrameLayout)localObject).setVisibility(k);
        break;
        k = 0;
        break label198;
        k = 8;
        break label225;
      }
    }
    this.tabletFullSize = true;
    if (!this.rightActionBarLayout.fragmentsStack.isEmpty())
    {
      for (k = 0; this.rightActionBarLayout.fragmentsStack.size() > 0; k = k - 1 + 1)
      {
        localObject = (BaseFragment)this.rightActionBarLayout.fragmentsStack.get(k);
        if ((localObject instanceof ChatActivity)) {
          ((ChatActivity)localObject).setIgnoreAttachOnPause(true);
        }
        ((BaseFragment)localObject).onPause();
        this.rightActionBarLayout.fragmentsStack.remove(k);
        this.actionBarLayout.fragmentsStack.add(localObject);
      }
      if (this.passcodeView.getVisibility() != 0) {
        this.actionBarLayout.showLastFragment();
      }
    }
    this.shadowTabletSide.setVisibility(8);
    this.rightActionBarLayout.setVisibility(8);
    Object localObject = this.backgroundTablet;
    if (!this.actionBarLayout.fragmentsStack.isEmpty()) {}
    for (int k = j;; k = 0)
    {
      ((View)localObject).setVisibility(k);
      break;
    }
  }
  
  private String getStringForLanguageAlert(HashMap<String, String> paramHashMap, String paramString, int paramInt)
  {
    String str = (String)paramHashMap.get(paramString);
    paramHashMap = str;
    if (str == null) {
      paramHashMap = LocaleController.getString(paramString, paramInt);
    }
    return paramHashMap;
  }
  
  private boolean handleIntent(Intent paramIntent, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (AndroidUtilities.handleProxyIntent(this, paramIntent)) {}
    int i;
    final int[] arrayOfInt;
    for (paramBoolean2 = true;; paramBoolean2 = false)
    {
      return paramBoolean2;
      if ((PhotoViewer.hasInstance()) && (PhotoViewer.getInstance().isVisible()) && ((paramIntent == null) || (!"android.intent.action.MAIN".equals(paramIntent.getAction())))) {
        PhotoViewer.getInstance().closePhoto(false, true);
      }
      i = paramIntent.getFlags();
      arrayOfInt = new int[1];
      arrayOfInt[0] = paramIntent.getIntExtra("currentAccount", UserConfig.selectedAccount);
      switchToAccount(arrayOfInt[0], true);
      if ((paramBoolean3) || ((!AndroidUtilities.needShowPasscode(true)) && (!SharedConfig.isWaitingForPasscodeEnter))) {
        break;
      }
      showPasscodeActivity();
      this.passcodeSaveIntent = paramIntent;
      this.passcodeSaveIntentIsNew = paramBoolean1;
      this.passcodeSaveIntentIsRestore = paramBoolean2;
      UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }
    boolean bool1 = false;
    localObject1 = Integer.valueOf(0);
    localInteger1 = Integer.valueOf(0);
    Integer localInteger2 = Integer.valueOf(0);
    Integer localInteger3 = Integer.valueOf(0);
    Integer localInteger4 = Integer.valueOf(0);
    Integer localInteger5 = Integer.valueOf(0);
    long l = 0L;
    if (SharedConfig.directShare)
    {
      if ((paramIntent == null) || (paramIntent.getExtras() == null)) {
        break label788;
      }
      l = paramIntent.getExtras().getLong("dialogId", 0L);
    }
    int j;
    int k;
    int m;
    Object localObject2;
    Object localObject14;
    Object localObject15;
    for (;;)
    {
      j = 0;
      k = 0;
      m = 0;
      this.photoPathsArray = null;
      this.videoPath = null;
      this.sendingText = null;
      this.documentsPathsArray = null;
      this.documentsOriginalPathsArray = null;
      this.documentsMimeType = null;
      this.documentsUrisArray = null;
      this.contactsToSend = null;
      localObject2 = localInteger5;
      localObject3 = localInteger4;
      localObject4 = localInteger1;
      localObject5 = localInteger2;
      localObject6 = localInteger3;
      localObject7 = localObject1;
      n = j;
      i1 = m;
      i2 = k;
      if (!UserConfig.getInstance(this.currentAccount).isClientActivated()) {
        break label1141;
      }
      localObject2 = localInteger5;
      localObject3 = localInteger4;
      localObject4 = localInteger1;
      localObject5 = localInteger2;
      localObject6 = localInteger3;
      localObject7 = localObject1;
      n = j;
      i1 = m;
      i2 = k;
      if ((0x100000 & i) != 0) {
        break label1141;
      }
      localObject2 = localInteger5;
      localObject3 = localInteger4;
      localObject4 = localInteger1;
      localObject5 = localInteger2;
      localObject6 = localInteger3;
      localObject7 = localObject1;
      n = j;
      i1 = m;
      i2 = k;
      if (paramIntent == null) {
        break label1141;
      }
      localObject2 = localInteger5;
      localObject3 = localInteger4;
      localObject4 = localInteger1;
      localObject5 = localInteger2;
      localObject6 = localInteger3;
      localObject7 = localObject1;
      n = j;
      i1 = m;
      i2 = k;
      if (paramIntent.getAction() == null) {
        break label1141;
      }
      localObject2 = localInteger5;
      localObject3 = localInteger4;
      localObject4 = localInteger1;
      localObject5 = localInteger2;
      localObject6 = localInteger3;
      localObject7 = localObject1;
      n = j;
      i1 = m;
      i2 = k;
      if (paramBoolean2) {
        break label1141;
      }
      if (!"android.intent.action.SEND".equals(paramIntent.getAction())) {
        break label2182;
      }
      i = 0;
      i1 = 0;
      localObject4 = paramIntent.getType();
      if ((localObject4 == null) || (!((String)localObject4).equals("text/x-vcard"))) {
        break label1661;
      }
      try
      {
        Object localObject8 = (Uri)paramIntent.getExtras().get("android.intent.extra.STREAM");
        if (localObject8 == null) {
          break label1655;
        }
        localObject14 = getContentResolver().openInputStream((Uri)localObject8);
        localObject15 = new java/util/ArrayList;
        ((ArrayList)localObject15).<init>();
        localObject16 = null;
        localObject2 = new java/io/BufferedReader;
        localObject8 = new java/io/InputStreamReader;
        ((InputStreamReader)localObject8).<init>((InputStream)localObject14, "UTF-8");
        ((BufferedReader)localObject2).<init>((Reader)localObject8);
        for (;;)
        {
          label589:
          localObject8 = ((BufferedReader)localObject2).readLine();
          if (localObject8 == null) {
            break label1470;
          }
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d((String)localObject8);
          }
          localObject3 = ((String)localObject8).split(":");
          if (localObject3.length == 2)
          {
            if ((localObject3[0].equals("BEGIN")) && (localObject3[1].equals("VCARD")))
            {
              localObject8 = new org/telegram/ui/LaunchActivity$VcardData;
              ((VcardData)localObject8).<init>(this, null);
              ((ArrayList)localObject15).add(localObject8);
              label675:
              localObject16 = localObject8;
              if (localObject8 == null) {
                continue;
              }
              if ((!localObject3[0].startsWith("FN")) && ((!localObject3[0].startsWith("ORG")) || (!TextUtils.isEmpty(((VcardData)localObject8).name)))) {
                break label1413;
              }
              localObject4 = null;
              localObject7 = null;
              localObject6 = localObject3[0].split(";");
              n = localObject6.length;
              i = 0;
              label747:
              if (i >= n) {
                break label886;
              }
              localObject5 = localObject6[i].split("=");
              if (localObject5.length == 2) {
                break label834;
              }
              localObject16 = localObject7;
            }
            for (;;)
            {
              i++;
              localObject7 = localObject16;
              break label747;
              label788:
              l = 0L;
              break;
              localObject8 = localObject16;
              if (!localObject3[0].equals("END")) {
                break label675;
              }
              localObject8 = localObject16;
              if (!localObject3[1].equals("VCARD")) {
                break label675;
              }
              localObject8 = null;
              break label675;
              label834:
              if (localObject5[0].equals("CHARSET"))
              {
                localObject16 = localObject5[1];
              }
              else
              {
                localObject16 = localObject7;
                if (localObject5[0].equals("ENCODING"))
                {
                  localObject4 = localObject5[1];
                  localObject16 = localObject7;
                }
              }
            }
            label886:
            ((VcardData)localObject8).name = localObject3[1];
            localObject16 = localObject8;
            if (localObject4 != null)
            {
              localObject16 = localObject8;
              if (((String)localObject4).equalsIgnoreCase("QUOTED-PRINTABLE"))
              {
                label919:
                if ((((VcardData)localObject8).name.endsWith("=")) && (localObject4 != null))
                {
                  ((VcardData)localObject8).name = ((VcardData)localObject8).name.substring(0, ((VcardData)localObject8).name.length() - 1);
                  localObject3 = ((BufferedReader)localObject2).readLine();
                  if (localObject3 != null) {
                    break label1377;
                  }
                }
                localObject4 = AndroidUtilities.decodeQuotedPrintable(((VcardData)localObject8).name.getBytes());
                localObject16 = localObject8;
                if (localObject4 != null)
                {
                  localObject16 = localObject8;
                  if (localObject4.length != 0)
                  {
                    localObject3 = new java/lang/String;
                    ((String)localObject3).<init>((byte[])localObject4, (String)localObject7);
                    localObject16 = localObject8;
                    if (localObject3 != null)
                    {
                      ((VcardData)localObject8).name = ((String)localObject3);
                      localObject16 = localObject8;
                    }
                  }
                }
              }
            }
          }
        }
        localObject2 = localInteger5;
      }
      catch (Exception localException1)
      {
        FileLog.e(localException1);
        i = 1;
      }
    }
    label1053:
    Object localObject3 = localInteger4;
    Object localObject4 = localInteger1;
    Object localObject5 = localInteger2;
    Object localObject6 = localInteger3;
    final Object localObject7 = localObject1;
    int n = j;
    int i1 = m;
    int i2 = k;
    if (i != 0)
    {
      Toast.makeText(this, "Unsupported content", 0).show();
      i2 = k;
      i1 = m;
      n = j;
      localObject7 = localObject1;
      localObject6 = localInteger3;
      localObject5 = localInteger2;
      localObject4 = localInteger1;
      localObject3 = localInteger4;
      localObject2 = localInteger5;
    }
    label1141:
    label1276:
    label1341:
    label1377:
    label1413:
    label1470:
    label1655:
    label1661:
    label1996:
    label2165:
    label2182:
    Object localObject17;
    Integer localInteger6;
    Object localObject19;
    Object localObject20;
    String str1;
    String str2;
    Object localObject21;
    Object localObject22;
    Object localObject23;
    Object localObject24;
    Object localObject25;
    boolean bool5;
    String str4;
    Object localObject26;
    Object localObject27;
    Object localObject28;
    Object localObject11;
    for (;;)
    {
      if (((Integer)localObject7).intValue() != 0)
      {
        Object localObject9 = new Bundle();
        ((Bundle)localObject9).putInt("user_id", ((Integer)localObject7).intValue());
        if (((Integer)localObject6).intValue() != 0) {
          ((Bundle)localObject9).putInt("message_id", ((Integer)localObject6).intValue());
        }
        if (!mainFragmentsStack.isEmpty())
        {
          paramBoolean2 = bool1;
          paramBoolean3 = paramBoolean1;
          if (!MessagesController.getInstance(arrayOfInt[0]).checkCanOpenChat((Bundle)localObject9, (BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1))) {}
        }
        else
        {
          localObject9 = new ChatActivity((Bundle)localObject9);
          paramBoolean2 = bool1;
          paramBoolean3 = paramBoolean1;
          if (this.actionBarLayout.presentFragment((BaseFragment)localObject9, false, true, true))
          {
            paramBoolean2 = true;
            paramBoolean3 = paramBoolean1;
          }
        }
        if ((!paramBoolean2) && (!paramBoolean3))
        {
          if (!AndroidUtilities.isTablet()) {
            break label7439;
          }
          if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            break label7385;
          }
          if (this.layersActionBarLayout.fragmentsStack.isEmpty())
          {
            this.layersActionBarLayout.addFragmentToStack(new LoginActivity());
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
          }
          this.actionBarLayout.showLastFragment();
          if (AndroidUtilities.isTablet())
          {
            this.layersActionBarLayout.showLastFragment();
            this.rightActionBarLayout.showLastFragment();
          }
        }
        paramIntent.setAction(null);
        break;
        localObject16 = new java/lang/StringBuilder;
        ((StringBuilder)localObject16).<init>();
        ((VcardData)localObject9).name += (String)localObject3;
        break label919;
        localObject16 = localObject9;
        if (!localObject3[0].startsWith("TEL")) {
          break label589;
        }
        localObject7 = PhoneFormat.stripExceptNumbers(localObject3[1], true);
        localObject16 = localObject9;
        if (((String)localObject7).length() <= 0) {
          break label589;
        }
        ((VcardData)localObject9).phones.add(localObject7);
        localObject16 = localObject9;
        break label589;
        try
        {
          ((BufferedReader)localObject2).close();
          ((InputStream)localObject14).close();
          n = 0;
          i = i1;
          if (n >= ((ArrayList)localObject15).size()) {
            break label1053;
          }
          localObject9 = (VcardData)((ArrayList)localObject15).get(n);
          if ((((VcardData)localObject9).name != null) && (!((VcardData)localObject9).phones.isEmpty()))
          {
            if (this.contactsToSend == null)
            {
              localObject7 = new java/util/ArrayList;
              ((ArrayList)localObject7).<init>();
              this.contactsToSend = ((ArrayList)localObject7);
            }
            for (i = 0; i < ((VcardData)localObject9).phones.size(); i++)
            {
              localObject7 = (String)((VcardData)localObject9).phones.get(i);
              localObject4 = new org/telegram/tgnet/TLRPC$TL_userContact_old2;
              ((TLRPC.TL_userContact_old2)localObject4).<init>();
              ((TLRPC.User)localObject4).phone = ((String)localObject7);
              ((TLRPC.User)localObject4).first_name = ((VcardData)localObject9).name;
              ((TLRPC.User)localObject4).last_name = "";
              ((TLRPC.User)localObject4).id = 0;
              this.contactsToSend.add(localObject4);
            }
          }
        }
        catch (Exception localException2)
        {
          for (;;)
          {
            FileLog.e(localException2);
            continue;
            n++;
          }
        }
        i = 1;
        break label1053;
        localObject7 = paramIntent.getStringExtra("android.intent.extra.TEXT");
        Object localObject10 = localObject7;
        if (localObject7 == null)
        {
          localObject16 = paramIntent.getCharSequenceExtra("android.intent.extra.TEXT");
          localObject10 = localObject7;
          if (localObject16 != null) {
            localObject10 = ((CharSequence)localObject16).toString();
          }
        }
        localObject16 = paramIntent.getStringExtra("android.intent.extra.SUBJECT");
        if ((localObject10 != null) && (((String)localObject10).length() != 0)) {
          if (!((String)localObject10).startsWith("http://"))
          {
            localObject7 = localObject10;
            if (!((String)localObject10).startsWith("https://")) {}
          }
          else
          {
            localObject7 = localObject10;
            if (localObject16 != null)
            {
              localObject7 = localObject10;
              if (((String)localObject16).length() != 0) {
                localObject7 = (String)localObject16 + "\n" + (String)localObject10;
              }
            }
          }
        }
        for (this.sendingText = ((String)localObject7);; this.sendingText = ((String)localObject16)) {
          do
          {
            localObject7 = paramIntent.getParcelableExtra("android.intent.extra.STREAM");
            if (localObject7 == null) {
              break label2165;
            }
            localObject10 = localObject7;
            if (!(localObject7 instanceof Uri)) {
              localObject10 = Uri.parse(localObject7.toString());
            }
            localObject16 = (Uri)localObject10;
            n = i;
            if (localObject16 != null)
            {
              n = i;
              if (AndroidUtilities.isInternalUri((Uri)localObject16)) {
                n = 1;
              }
            }
            i = n;
            if (n != 0) {
              break;
            }
            if ((localObject16 == null) || (((localObject4 == null) || (!((String)localObject4).startsWith("image/"))) && (!((Uri)localObject16).toString().toLowerCase().endsWith(".jpg")))) {
              break label1996;
            }
            if (this.photoPathsArray == null) {
              this.photoPathsArray = new ArrayList();
            }
            localObject10 = new SendMessagesHelper.SendingMediaInfo();
            ((SendMessagesHelper.SendingMediaInfo)localObject10).uri = ((Uri)localObject16);
            this.photoPathsArray.add(localObject10);
            i = n;
            break;
          } while ((localObject16 == null) || (((String)localObject16).length() <= 0));
        }
        localObject7 = AndroidUtilities.getPath((Uri)localObject16);
        if (localObject7 != null)
        {
          localObject10 = localObject7;
          if (((String)localObject7).startsWith("file:")) {
            localObject10 = ((String)localObject7).replace("file://", "");
          }
          if ((localObject4 != null) && (((String)localObject4).startsWith("video/")))
          {
            this.videoPath = ((String)localObject10);
            i = n;
            break label1053;
          }
          if (this.documentsPathsArray == null)
          {
            this.documentsPathsArray = new ArrayList();
            this.documentsOriginalPathsArray = new ArrayList();
          }
          this.documentsPathsArray.add(localObject10);
          this.documentsOriginalPathsArray.add(((Uri)localObject16).toString());
          i = n;
          break label1053;
        }
        if (this.documentsUrisArray == null) {
          this.documentsUrisArray = new ArrayList();
        }
        this.documentsUrisArray.add(localObject16);
        this.documentsMimeType = ((String)localObject4);
        i = n;
        break label1053;
        i = i1;
        if (this.sendingText != null) {
          break label1053;
        }
        i = 1;
        break label1053;
        if (paramIntent.getAction().equals("android.intent.action.SEND_MULTIPLE"))
        {
          i1 = 0;
          try
          {
            localObject7 = paramIntent.getParcelableArrayListExtra("android.intent.extra.STREAM");
            localObject15 = paramIntent.getType();
            localObject10 = localObject7;
            if (localObject7 != null)
            {
              for (i = 0; i < ((ArrayList)localObject7).size(); i = n + 1)
              {
                localObject4 = (Parcelable)((ArrayList)localObject7).get(i);
                localObject10 = localObject4;
                if (!(localObject4 instanceof Uri)) {
                  localObject10 = Uri.parse(localObject4.toString());
                }
                localObject10 = (Uri)localObject10;
                n = i;
                if (localObject10 != null)
                {
                  n = i;
                  if (AndroidUtilities.isInternalUri((Uri)localObject10))
                  {
                    ((ArrayList)localObject7).remove(i);
                    n = i - 1;
                  }
                }
              }
              localObject10 = localObject7;
              if (((ArrayList)localObject7).isEmpty()) {
                localObject10 = null;
              }
            }
            if (localObject10 != null)
            {
              if ((localObject15 != null) && (((String)localObject15).startsWith("image/"))) {
                for (n = 0;; n++)
                {
                  i = i1;
                  if (n >= ((ArrayList)localObject10).size()) {
                    break;
                  }
                  localObject4 = (Parcelable)((ArrayList)localObject10).get(n);
                  localObject7 = localObject4;
                  if (!(localObject4 instanceof Uri)) {
                    localObject7 = Uri.parse(localObject4.toString());
                  }
                  localObject7 = (Uri)localObject7;
                  if (this.photoPathsArray == null)
                  {
                    localObject4 = new java/util/ArrayList;
                    ((ArrayList)localObject4).<init>();
                    this.photoPathsArray = ((ArrayList)localObject4);
                  }
                  localObject4 = new org/telegram/messenger/SendMessagesHelper$SendingMediaInfo;
                  ((SendMessagesHelper.SendingMediaInfo)localObject4).<init>();
                  ((SendMessagesHelper.SendingMediaInfo)localObject4).uri = ((Uri)localObject7);
                  this.photoPathsArray.add(localObject4);
                }
              }
              n = 0;
              i = i1;
              if (n < ((ArrayList)localObject10).size())
              {
                localObject4 = (Parcelable)((ArrayList)localObject10).get(n);
                localObject7 = localObject4;
                if (!(localObject4 instanceof Uri)) {
                  localObject7 = Uri.parse(localObject4.toString());
                }
                localObject14 = (Uri)localObject7;
                localObject4 = AndroidUtilities.getPath((Uri)localObject14);
                localObject16 = localObject7.toString();
                localObject7 = localObject16;
                if (localObject16 == null) {
                  localObject7 = localObject4;
                }
                if (localObject4 != null)
                {
                  localObject16 = localObject4;
                  if (((String)localObject4).startsWith("file:")) {
                    localObject16 = ((String)localObject4).replace("file://", "");
                  }
                  if (this.documentsPathsArray == null)
                  {
                    localObject4 = new java/util/ArrayList;
                    ((ArrayList)localObject4).<init>();
                    this.documentsPathsArray = ((ArrayList)localObject4);
                    localObject4 = new java/util/ArrayList;
                    ((ArrayList)localObject4).<init>();
                    this.documentsOriginalPathsArray = ((ArrayList)localObject4);
                  }
                  this.documentsPathsArray.add(localObject16);
                  this.documentsOriginalPathsArray.add(localObject7);
                }
                for (;;)
                {
                  n++;
                  break;
                  if (this.documentsUrisArray == null)
                  {
                    localObject7 = new java/util/ArrayList;
                    ((ArrayList)localObject7).<init>();
                    this.documentsUrisArray = ((ArrayList)localObject7);
                  }
                  this.documentsUrisArray.add(localObject14);
                  this.documentsMimeType = ((String)localObject15);
                }
              }
              localObject2 = localInteger5;
            }
          }
          catch (Exception localException3)
          {
            FileLog.e(localException3);
            i = 1;
          }
          for (;;)
          {
            localObject3 = localInteger4;
            localObject4 = localInteger1;
            localObject5 = localInteger2;
            localObject6 = localInteger3;
            localObject7 = localObject1;
            n = j;
            i1 = m;
            i2 = k;
            if (i == 0) {
              break;
            }
            Toast.makeText(this, "Unsupported content", 0).show();
            localObject2 = localInteger5;
            localObject3 = localInteger4;
            localObject4 = localInteger1;
            localObject5 = localInteger2;
            localObject6 = localInteger3;
            localObject7 = localObject1;
            n = j;
            i1 = m;
            i2 = k;
            break;
            i = 1;
          }
        }
        if ("android.intent.action.VIEW".equals(paramIntent.getAction()))
        {
          localObject17 = paramIntent.getData();
          localObject2 = localInteger5;
          localObject3 = localInteger4;
          localObject4 = localInteger1;
          localObject5 = localInteger2;
          localObject6 = localInteger3;
          localObject7 = localObject1;
          n = j;
          i1 = m;
          i2 = k;
          if (localObject17 == null) {
            continue;
          }
          localObject18 = null;
          localInteger6 = null;
          localObject19 = null;
          localObject20 = null;
          str1 = null;
          str2 = null;
          localObject21 = null;
          localObject22 = null;
          localObject23 = null;
          localObject24 = null;
          localObject16 = null;
          localObject25 = null;
          boolean bool2 = false;
          boolean bool3 = false;
          boolean bool4 = false;
          paramBoolean3 = false;
          bool5 = false;
          String str3 = ((Uri)localObject17).getScheme();
          localObject2 = localInteger6;
          localObject3 = localObject19;
          localObject6 = localObject20;
          localObject5 = str1;
          str4 = str2;
          localObject7 = localObject21;
          paramBoolean2 = bool5;
          localObject4 = localObject25;
          localObject26 = localObject23;
          localObject27 = localObject22;
          localObject28 = localObject24;
          localObject15 = localInteger1;
          localObject14 = localInteger3;
          localObject11 = localObject1;
          if (str3 != null)
          {
            if ((!str3.equals("http")) && (!str3.equals("https"))) {
              break label4132;
            }
            str3 = ((Uri)localObject17).getHost().toLowerCase();
            if ((!str3.equals("telegram.me")) && (!str3.equals("t.me")) && (!str3.equals("telegram.dog")))
            {
              localObject2 = localInteger6;
              localObject3 = localObject19;
              localObject6 = localObject20;
              localObject5 = str1;
              str4 = str2;
              localObject7 = localObject21;
              paramBoolean2 = bool5;
              localObject4 = localObject25;
              localObject26 = localObject23;
              localObject27 = localObject22;
              localObject28 = localObject24;
              localObject15 = localInteger1;
              localObject14 = localInteger3;
              localObject11 = localObject1;
              if (!str3.equals("telesco.pe")) {}
            }
            else
            {
              str3 = ((Uri)localObject17).getPath();
              localObject2 = localInteger6;
              localObject3 = localObject19;
              localObject6 = localObject20;
              localObject5 = str1;
              str4 = str2;
              localObject7 = localObject21;
              paramBoolean2 = bool5;
              localObject4 = localObject25;
              localObject26 = localObject23;
              localObject27 = localObject22;
              localObject28 = localObject24;
              localObject15 = localInteger1;
              localObject14 = localInteger3;
              localObject11 = localObject1;
              if (str3 != null)
              {
                localObject2 = localInteger6;
                localObject3 = localObject19;
                localObject6 = localObject20;
                localObject5 = str1;
                str4 = str2;
                localObject7 = localObject21;
                paramBoolean2 = bool5;
                localObject4 = localObject25;
                localObject26 = localObject23;
                localObject27 = localObject22;
                localObject28 = localObject24;
                localObject15 = localInteger1;
                localObject14 = localInteger3;
                localObject11 = localObject1;
                if (str3.length() > 1)
                {
                  str3 = str3.substring(1);
                  if (!str3.startsWith("joinchat/")) {
                    break label3463;
                  }
                  localObject3 = str3.replace("joinchat/", "");
                  localObject11 = localObject1;
                  localObject14 = localInteger3;
                  localObject15 = localInteger1;
                  localObject28 = localObject24;
                  localObject27 = localObject22;
                  localObject26 = localObject23;
                  localObject4 = localObject25;
                  paramBoolean2 = bool5;
                  localObject7 = localObject21;
                  str4 = str2;
                  localObject5 = str1;
                  localObject6 = localObject20;
                  localObject2 = localInteger6;
                }
              }
            }
          }
          label3325:
          label3463:
          label4132:
          label5062:
          do
          {
            for (;;)
            {
              localObject16 = localObject7;
              if (localObject7 != null)
              {
                localObject16 = localObject7;
                if (((String)localObject7).startsWith("@")) {
                  localObject16 = " " + (String)localObject7;
                }
              }
              if ((localObject27 == null) && (localObject28 == null)) {
                break label5379;
              }
              localObject7 = new Bundle();
              ((Bundle)localObject7).putString("phone", (String)localObject27);
              ((Bundle)localObject7).putString("hash", (String)localObject28);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  LaunchActivity.this.presentFragment(new CancelAccountDeletionActivity(localObject7));
                }
              });
              localObject2 = localInteger5;
              localObject3 = localInteger4;
              localObject4 = localObject15;
              localObject5 = localInteger2;
              localObject6 = localObject14;
              localObject7 = localObject11;
              n = j;
              i1 = m;
              i2 = k;
              break;
              if (str3.startsWith("addstickers/"))
              {
                localObject6 = str3.replace("addstickers/", "");
                localObject2 = localInteger6;
                localObject3 = localObject19;
                localObject5 = str1;
                str4 = str2;
                localObject7 = localObject21;
                paramBoolean2 = bool5;
                localObject4 = localObject25;
                localObject26 = localObject23;
                localObject27 = localObject22;
                localObject28 = localObject24;
                localObject15 = localInteger1;
                localObject14 = localInteger3;
                localObject11 = localObject1;
              }
              else
              {
                if (str3.startsWith("iv/"))
                {
                  ((Uri)localObject17).getQueryParameter("url");
                  ((Uri)localObject17).getQueryParameter("rhash");
                  throw new NullPointerException();
                }
                if ((str3.startsWith("msg/")) || (str3.startsWith("share/")))
                {
                  localObject7 = ((Uri)localObject17).getQueryParameter("url");
                  localObject11 = localObject7;
                  if (localObject7 == null) {
                    localObject11 = "";
                  }
                  localObject7 = localObject11;
                  paramBoolean3 = bool3;
                  if (((Uri)localObject17).getQueryParameter("text") != null)
                  {
                    localObject7 = localObject11;
                    paramBoolean3 = bool2;
                    if (((String)localObject11).length() > 0)
                    {
                      paramBoolean3 = true;
                      localObject7 = (String)localObject11 + "\n";
                    }
                    localObject7 = (String)localObject7 + ((Uri)localObject17).getQueryParameter("text");
                  }
                  localObject16 = localObject7;
                  if (((String)localObject7).length() > 16384) {}
                  for (localObject16 = ((String)localObject7).substring(0, 16384);; localObject16 = ((String)localObject16).substring(0, ((String)localObject16).length() - 1))
                  {
                    localObject2 = localInteger6;
                    localObject3 = localObject19;
                    localObject6 = localObject20;
                    localObject5 = str1;
                    str4 = str2;
                    localObject7 = localObject16;
                    paramBoolean2 = paramBoolean3;
                    localObject4 = localObject25;
                    localObject26 = localObject23;
                    localObject27 = localObject22;
                    localObject28 = localObject24;
                    localObject15 = localInteger1;
                    localObject14 = localInteger3;
                    localObject11 = localObject1;
                    if (!((String)localObject16).endsWith("\n")) {
                      break;
                    }
                  }
                }
                if (str3.startsWith("confirmphone"))
                {
                  localObject27 = ((Uri)localObject17).getQueryParameter("phone");
                  localObject28 = ((Uri)localObject17).getQueryParameter("hash");
                  localObject2 = localInteger6;
                  localObject3 = localObject19;
                  localObject6 = localObject20;
                  localObject5 = str1;
                  str4 = str2;
                  localObject7 = localObject21;
                  paramBoolean2 = bool5;
                  localObject4 = localObject25;
                  localObject26 = localObject23;
                  localObject15 = localInteger1;
                  localObject14 = localInteger3;
                  localObject11 = localObject1;
                }
                else
                {
                  localObject2 = localInteger6;
                  localObject3 = localObject19;
                  localObject6 = localObject20;
                  localObject5 = str1;
                  str4 = str2;
                  localObject7 = localObject21;
                  paramBoolean2 = bool5;
                  localObject4 = localObject25;
                  localObject26 = localObject23;
                  localObject27 = localObject22;
                  localObject28 = localObject24;
                  localObject15 = localInteger1;
                  localObject14 = localInteger3;
                  localObject11 = localObject1;
                  if (str3.length() >= 1)
                  {
                    localObject7 = ((Uri)localObject17).getPathSegments();
                    localObject2 = localObject18;
                    localObject4 = localObject16;
                    if (((List)localObject7).size() > 0)
                    {
                      localObject11 = (String)((List)localObject7).get(0);
                      localObject2 = localObject11;
                      localObject4 = localObject16;
                      if (((List)localObject7).size() > 1)
                      {
                        localObject7 = Utilities.parseInt((String)((List)localObject7).get(1));
                        localObject2 = localObject11;
                        localObject4 = localObject7;
                        if (((Integer)localObject7).intValue() == 0)
                        {
                          localObject4 = null;
                          localObject2 = localObject11;
                        }
                      }
                    }
                    localObject5 = ((Uri)localObject17).getQueryParameter("start");
                    str4 = ((Uri)localObject17).getQueryParameter("startgroup");
                    localObject26 = ((Uri)localObject17).getQueryParameter("game");
                    localObject3 = localObject19;
                    localObject6 = localObject20;
                    localObject7 = localObject21;
                    paramBoolean2 = bool5;
                    localObject27 = localObject22;
                    localObject28 = localObject24;
                    localObject15 = localInteger1;
                    localObject14 = localInteger3;
                    localObject11 = localObject1;
                    continue;
                    localObject2 = localInteger6;
                    localObject3 = localObject19;
                    localObject6 = localObject20;
                    localObject5 = str1;
                    str4 = str2;
                    localObject7 = localObject21;
                    paramBoolean2 = bool5;
                    localObject4 = localObject25;
                    localObject26 = localObject23;
                    localObject27 = localObject22;
                    localObject28 = localObject24;
                    localObject15 = localInteger1;
                    localObject14 = localInteger3;
                    localObject11 = localObject1;
                    if (str3.equals("tg"))
                    {
                      localObject16 = ((Uri)localObject17).toString();
                      if ((((String)localObject16).startsWith("tg:resolve")) || (((String)localObject16).startsWith("tg://resolve")))
                      {
                        localObject11 = Uri.parse(((String)localObject16).replace("tg:resolve", "tg://telegram.org").replace("tg://resolve", "tg://telegram.org"));
                        localObject18 = ((Uri)localObject11).getQueryParameter("domain");
                        localObject16 = ((Uri)localObject11).getQueryParameter("start");
                        str2 = ((Uri)localObject11).getQueryParameter("startgroup");
                        str1 = ((Uri)localObject11).getQueryParameter("game");
                        localInteger6 = Utilities.parseInt(((Uri)localObject11).getQueryParameter("post"));
                        localObject2 = localObject18;
                        localObject3 = localObject19;
                        localObject6 = localObject20;
                        localObject5 = localObject16;
                        str4 = str2;
                        localObject7 = localObject21;
                        paramBoolean2 = bool5;
                        localObject4 = localInteger6;
                        localObject26 = str1;
                        localObject27 = localObject22;
                        localObject28 = localObject24;
                        localObject15 = localInteger1;
                        localObject14 = localInteger3;
                        localObject11 = localObject1;
                        if (localInteger6.intValue() == 0)
                        {
                          localObject4 = null;
                          localObject2 = localObject18;
                          localObject3 = localObject19;
                          localObject6 = localObject20;
                          localObject5 = localObject16;
                          str4 = str2;
                          localObject7 = localObject21;
                          paramBoolean2 = bool5;
                          localObject26 = str1;
                          localObject27 = localObject22;
                          localObject28 = localObject24;
                          localObject15 = localInteger1;
                          localObject14 = localInteger3;
                          localObject11 = localObject1;
                        }
                      }
                      else if ((((String)localObject16).startsWith("tg:join")) || (((String)localObject16).startsWith("tg://join")))
                      {
                        localObject3 = Uri.parse(((String)localObject16).replace("tg:join", "tg://telegram.org").replace("tg://join", "tg://telegram.org")).getQueryParameter("invite");
                        localObject2 = localInteger6;
                        localObject6 = localObject20;
                        localObject5 = str1;
                        str4 = str2;
                        localObject7 = localObject21;
                        paramBoolean2 = bool5;
                        localObject4 = localObject25;
                        localObject26 = localObject23;
                        localObject27 = localObject22;
                        localObject28 = localObject24;
                        localObject15 = localInteger1;
                        localObject14 = localInteger3;
                        localObject11 = localObject1;
                      }
                      else if ((((String)localObject16).startsWith("tg:addstickers")) || (((String)localObject16).startsWith("tg://addstickers")))
                      {
                        localObject6 = Uri.parse(((String)localObject16).replace("tg:addstickers", "tg://telegram.org").replace("tg://addstickers", "tg://telegram.org")).getQueryParameter("set");
                        localObject2 = localInteger6;
                        localObject3 = localObject19;
                        localObject5 = str1;
                        str4 = str2;
                        localObject7 = localObject21;
                        paramBoolean2 = bool5;
                        localObject4 = localObject25;
                        localObject26 = localObject23;
                        localObject27 = localObject22;
                        localObject28 = localObject24;
                        localObject15 = localInteger1;
                        localObject14 = localInteger3;
                        localObject11 = localObject1;
                      }
                      else
                      {
                        if ((((String)localObject16).startsWith("tg:msg")) || (((String)localObject16).startsWith("tg://msg")) || (((String)localObject16).startsWith("tg://share")) || (((String)localObject16).startsWith("tg:share")))
                        {
                          localObject4 = Uri.parse(((String)localObject16).replace("tg:msg", "tg://telegram.org").replace("tg://msg", "tg://telegram.org").replace("tg://share", "tg://telegram.org").replace("tg:share", "tg://telegram.org"));
                          localObject7 = ((Uri)localObject4).getQueryParameter("url");
                          localObject11 = localObject7;
                          if (localObject7 == null) {
                            localObject11 = "";
                          }
                          localObject7 = localObject11;
                          if (((Uri)localObject4).getQueryParameter("text") != null)
                          {
                            localObject7 = localObject11;
                            paramBoolean3 = bool4;
                            if (((String)localObject11).length() > 0)
                            {
                              paramBoolean3 = true;
                              localObject7 = (String)localObject11 + "\n";
                            }
                            localObject7 = (String)localObject7 + ((Uri)localObject4).getQueryParameter("text");
                          }
                          localObject16 = localObject7;
                          if (((String)localObject7).length() > 16384) {}
                          for (localObject16 = ((String)localObject7).substring(0, 16384);; localObject16 = ((String)localObject16).substring(0, ((String)localObject16).length() - 1))
                          {
                            localObject2 = localInteger6;
                            localObject3 = localObject19;
                            localObject6 = localObject20;
                            localObject5 = str1;
                            str4 = str2;
                            localObject7 = localObject16;
                            paramBoolean2 = paramBoolean3;
                            localObject4 = localObject25;
                            localObject26 = localObject23;
                            localObject27 = localObject22;
                            localObject28 = localObject24;
                            localObject15 = localInteger1;
                            localObject14 = localInteger3;
                            localObject11 = localObject1;
                            if (!((String)localObject16).endsWith("\n")) {
                              break;
                            }
                          }
                        }
                        if ((!((String)localObject16).startsWith("tg:confirmphone")) && (!((String)localObject16).startsWith("tg://confirmphone"))) {
                          break label5062;
                        }
                        localObject11 = Uri.parse(((String)localObject16).replace("tg:confirmphone", "tg://telegram.org").replace("tg://confirmphone", "tg://telegram.org"));
                        localObject27 = ((Uri)localObject11).getQueryParameter("phone");
                        localObject28 = ((Uri)localObject11).getQueryParameter("hash");
                        localObject2 = localInteger6;
                        localObject3 = localObject19;
                        localObject6 = localObject20;
                        localObject5 = str1;
                        str4 = str2;
                        localObject7 = localObject21;
                        paramBoolean2 = bool5;
                        localObject4 = localObject25;
                        localObject26 = localObject23;
                        localObject15 = localInteger1;
                        localObject14 = localInteger3;
                        localObject11 = localObject1;
                      }
                    }
                  }
                }
              }
            }
            if (((String)localObject16).startsWith("tg:openmessage")) {
              break label5139;
            }
            localObject2 = localInteger6;
            localObject3 = localObject19;
            localObject6 = localObject20;
            localObject5 = str1;
            str4 = str2;
            localObject7 = localObject21;
            paramBoolean2 = bool5;
            localObject4 = localObject25;
            localObject26 = localObject23;
            localObject27 = localObject22;
            localObject28 = localObject24;
            localObject15 = localInteger1;
            localObject14 = localInteger3;
            localObject11 = localObject1;
          } while (!((String)localObject16).startsWith("tg://openmessage"));
          label5139:
          localObject4 = Uri.parse(((String)localObject16).replace("tg:openmessage", "tg://telegram.org").replace("tg://openmessage", "tg://telegram.org"));
          localObject7 = ((Uri)localObject4).getQueryParameter("user_id");
          localObject11 = ((Uri)localObject4).getQueryParameter("chat_id");
          localObject17 = ((Uri)localObject4).getQueryParameter("message_id");
          if (localObject7 == null) {}
        }
      }
    }
    for (;;)
    {
      try
      {
        i = Integer.parseInt((String)localObject7);
        localObject18 = Integer.valueOf(i);
        localObject16 = localInteger1;
      }
      catch (NumberFormatException localNumberFormatException3)
      {
        label5379:
        label7385:
        label7439:
        localObject16 = localInteger1;
        localObject18 = localObject1;
        continue;
      }
      localObject2 = localInteger6;
      localObject3 = localObject19;
      localObject6 = localObject20;
      localObject5 = str1;
      str4 = str2;
      localObject7 = localObject21;
      paramBoolean2 = bool5;
      localObject4 = localObject25;
      localObject26 = localObject23;
      localObject27 = localObject22;
      localObject28 = localObject24;
      localObject15 = localObject16;
      localObject14 = localInteger3;
      localObject11 = localObject18;
      if (localObject17 == null) {
        break label3325;
      }
      try
      {
        i = Integer.parseInt((String)localObject17);
        localObject14 = Integer.valueOf(i);
        localObject2 = localInteger6;
        localObject3 = localObject19;
        localObject6 = localObject20;
        localObject5 = str1;
        str4 = str2;
        localObject7 = localObject21;
        paramBoolean2 = bool5;
        localObject4 = localObject25;
        localObject26 = localObject23;
        localObject27 = localObject22;
        localObject28 = localObject24;
        localObject15 = localObject16;
        localObject11 = localObject18;
      }
      catch (NumberFormatException localNumberFormatException1)
      {
        localObject2 = localInteger6;
        localObject3 = localObject19;
        localObject6 = localObject20;
        localObject5 = str1;
        str4 = str2;
        localObject7 = localObject21;
        paramBoolean2 = bool5;
        localObject4 = localObject25;
        localObject26 = localObject23;
        localObject27 = localObject22;
        localObject28 = localObject24;
        localObject15 = localObject16;
        localObject14 = localInteger3;
        localObject13 = localObject18;
      }
      localObject16 = localInteger1;
      localObject18 = localObject1;
      if (localObject11 != null)
      {
        try
        {
          i = Integer.parseInt((String)localObject11);
          localObject16 = Integer.valueOf(i);
          localObject18 = localObject1;
        }
        catch (NumberFormatException localNumberFormatException2)
        {
          Object localObject12;
          Object localObject13;
          localObject16 = localInteger1;
          localObject18 = localObject1;
        }
        if ((localObject2 != null) || (localObject3 != null) || (localObject6 != null) || (localObject16 != null) || (localObject26 != null) || (0 != 0))
        {
          runLinkRequest(arrayOfInt[0], (String)localObject2, (String)localObject3, (String)localObject6, (String)localObject5, str4, (String)localObject16, paramBoolean2, (Integer)localObject4, (String)localObject26, null, 0);
          localObject2 = localInteger5;
          localObject3 = localInteger4;
          localObject4 = localObject15;
          localObject5 = localInteger2;
          localObject6 = localObject14;
          localObject7 = localObject11;
          n = j;
          i1 = m;
          i2 = k;
          break;
        }
        localObject16 = localObject11;
        try
        {
          localObject1 = getContentResolver().query(paramIntent.getData(), null, null, null, null);
          localObject2 = localInteger5;
          localObject3 = localInteger4;
          localObject4 = localObject15;
          localObject5 = localInteger2;
          localObject6 = localObject14;
          localObject7 = localObject11;
          n = j;
          i1 = m;
          i2 = k;
          if (localObject1 == null) {
            break;
          }
          localObject7 = localObject11;
          localObject16 = localObject11;
          if (((Cursor)localObject1).moveToFirst())
          {
            localObject16 = localObject11;
            n = Utilities.parseInt(((Cursor)localObject1).getString(((Cursor)localObject1).getColumnIndex("account_name"))).intValue();
            i = 0;
            if (i < 3)
            {
              localObject16 = localObject11;
              if (UserConfig.getInstance(i).getClientUserId() != n) {
                continue;
              }
              arrayOfInt[0] = i;
              localObject16 = localObject11;
              switchToAccount(arrayOfInt[0], true);
            }
            localObject16 = localObject11;
            i = ((Cursor)localObject1).getInt(((Cursor)localObject1).getColumnIndex("DATA4"));
            localObject16 = localObject11;
            NotificationCenter.getInstance(arrayOfInt[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            localObject16 = localObject11;
            localObject7 = Integer.valueOf(i);
          }
          localObject16 = localObject7;
          ((Cursor)localObject1).close();
          localObject2 = localInteger5;
          localObject3 = localInteger4;
          localObject4 = localObject15;
          localObject5 = localInteger2;
          localObject6 = localObject14;
          n = j;
          i1 = m;
          i2 = k;
        }
        catch (Exception localException4)
        {
          FileLog.e(localException4);
          localObject2 = localInteger5;
          localObject3 = localInteger4;
          localObject4 = localObject15;
          localObject5 = localInteger2;
          localObject6 = localObject14;
          localObject7 = localObject16;
          n = j;
          i1 = m;
          i2 = k;
        }
        break;
        i++;
        continue;
        if (paramIntent.getAction().equals("org.telegram.messenger.OPEN_ACCOUNT"))
        {
          localObject3 = Integer.valueOf(1);
          localObject2 = localInteger5;
          localObject4 = localInteger1;
          localObject5 = localInteger2;
          localObject6 = localInteger3;
          localObject7 = localObject1;
          n = j;
          i1 = m;
          i2 = k;
          break;
        }
        if (paramIntent.getAction().equals("new_dialog"))
        {
          localObject2 = Integer.valueOf(1);
          localObject3 = localInteger4;
          localObject4 = localInteger1;
          localObject5 = localInteger2;
          localObject6 = localInteger3;
          localObject7 = localObject1;
          n = j;
          i1 = m;
          i2 = k;
          break;
        }
        if (paramIntent.getAction().startsWith("com.tmessages.openchat"))
        {
          i1 = paramIntent.getIntExtra("chatId", 0);
          i = paramIntent.getIntExtra("userId", 0);
          n = paramIntent.getIntExtra("encId", 0);
          if (i1 != 0)
          {
            NotificationCenter.getInstance(arrayOfInt[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            localObject4 = Integer.valueOf(i1);
            localObject2 = localInteger5;
            localObject3 = localInteger4;
            localObject5 = localInteger2;
            localObject6 = localInteger3;
            localObject7 = localObject1;
            n = j;
            i1 = m;
            i2 = k;
            break;
          }
          if (i != 0)
          {
            NotificationCenter.getInstance(arrayOfInt[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            localObject7 = Integer.valueOf(i);
            localObject2 = localInteger5;
            localObject3 = localInteger4;
            localObject4 = localInteger1;
            localObject5 = localInteger2;
            localObject6 = localInteger3;
            n = j;
            i1 = m;
            i2 = k;
            break;
          }
          if (n != 0)
          {
            NotificationCenter.getInstance(arrayOfInt[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            localObject5 = Integer.valueOf(n);
            localObject2 = localInteger5;
            localObject3 = localInteger4;
            localObject4 = localInteger1;
            localObject6 = localInteger3;
            localObject7 = localObject1;
            n = j;
            i1 = m;
            i2 = k;
            break;
          }
          n = 1;
          localObject2 = localInteger5;
          localObject3 = localInteger4;
          localObject4 = localInteger1;
          localObject5 = localInteger2;
          localObject6 = localInteger3;
          localObject7 = localObject1;
          i1 = m;
          i2 = k;
          break;
        }
        if (paramIntent.getAction().equals("com.tmessages.openplayer"))
        {
          i2 = 1;
          localObject2 = localInteger5;
          localObject3 = localInteger4;
          localObject4 = localInteger1;
          localObject5 = localInteger2;
          localObject6 = localInteger3;
          localObject7 = localObject1;
          n = j;
          i1 = m;
          break;
        }
        localObject2 = localInteger5;
        localObject3 = localInteger4;
        localObject4 = localInteger1;
        localObject5 = localInteger2;
        localObject6 = localInteger3;
        localObject7 = localObject1;
        n = j;
        i1 = m;
        i2 = k;
        if (!paramIntent.getAction().equals("org.tmessages.openlocations")) {
          break;
        }
        i1 = 1;
        localObject2 = localInteger5;
        localObject3 = localInteger4;
        localObject4 = localInteger1;
        localObject5 = localInteger2;
        localObject6 = localInteger3;
        localObject7 = localObject1;
        n = j;
        i2 = k;
        break;
        if (((Integer)localObject4).intValue() != 0)
        {
          localObject12 = new Bundle();
          ((Bundle)localObject12).putInt("chat_id", ((Integer)localObject4).intValue());
          if (((Integer)localObject6).intValue() != 0) {
            ((Bundle)localObject12).putInt("message_id", ((Integer)localObject6).intValue());
          }
          if (!mainFragmentsStack.isEmpty())
          {
            paramBoolean2 = bool1;
            paramBoolean3 = paramBoolean1;
            if (!MessagesController.getInstance(arrayOfInt[0]).checkCanOpenChat((Bundle)localObject12, (BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1))) {
              break label1276;
            }
          }
          localObject12 = new ChatActivity((Bundle)localObject12);
          paramBoolean2 = bool1;
          paramBoolean3 = paramBoolean1;
          if (!this.actionBarLayout.presentFragment((BaseFragment)localObject12, false, true, true)) {
            break label1276;
          }
          paramBoolean2 = true;
          paramBoolean3 = paramBoolean1;
          break label1276;
        }
        if (((Integer)localObject5).intValue() != 0)
        {
          localObject12 = new Bundle();
          ((Bundle)localObject12).putInt("enc_id", ((Integer)localObject5).intValue());
          localObject12 = new ChatActivity((Bundle)localObject12);
          paramBoolean2 = bool1;
          paramBoolean3 = paramBoolean1;
          if (!this.actionBarLayout.presentFragment((BaseFragment)localObject12, false, true, true)) {
            break label1276;
          }
          paramBoolean2 = true;
          paramBoolean3 = paramBoolean1;
          break label1276;
        }
        if (n != 0)
        {
          if (!AndroidUtilities.isTablet())
          {
            this.actionBarLayout.removeAllFragments();
            paramBoolean2 = false;
            paramBoolean3 = false;
            break label1276;
          }
          if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
            continue;
          }
          i = 0;
          if (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0)
          {
            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
            i = i - 1 + 1;
            continue;
          }
          this.layersActionBarLayout.closeLastFragment(false);
          continue;
        }
        if (i2 != 0)
        {
          if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
            ((BaseFragment)this.actionBarLayout.fragmentsStack.get(0)).showDialog(new AudioPlayerAlert(this));
          }
          paramBoolean2 = false;
          paramBoolean3 = paramBoolean1;
          break label1276;
        }
        if (i1 != 0)
        {
          if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
            ((BaseFragment)this.actionBarLayout.fragmentsStack.get(0)).showDialog(new SharingLocationsAlert(this, new SharingLocationsAlert.SharingLocationsAlertDelegate()
            {
              public void didSelectLocation(LocationController.SharingLocationInfo paramAnonymousSharingLocationInfo)
              {
                arrayOfInt[0] = paramAnonymousSharingLocationInfo.messageObject.currentAccount;
                LaunchActivity.this.switchToAccount(arrayOfInt[0], true);
                LocationActivity localLocationActivity = new LocationActivity(2);
                localLocationActivity.setMessageObject(paramAnonymousSharingLocationInfo.messageObject);
                localLocationActivity.setDelegate(new LocationActivity.LocationActivityDelegate()
                {
                  public void didSelectLocation(TLRPC.MessageMedia paramAnonymous2MessageMedia, int paramAnonymous2Int)
                  {
                    SendMessagesHelper.getInstance(LaunchActivity.8.this.val$intentAccount[0]).sendMessage(paramAnonymous2MessageMedia, this.val$dialog_id, null, null, null);
                  }
                });
                LaunchActivity.this.presentFragment(localLocationActivity);
              }
            }));
          }
          paramBoolean2 = false;
          paramBoolean3 = paramBoolean1;
          break label1276;
        }
        if ((this.videoPath != null) || (this.photoPathsArray != null) || (this.sendingText != null) || (this.documentsPathsArray != null) || (this.contactsToSend != null) || (this.documentsUrisArray != null))
        {
          if (!AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(arrayOfInt[0]).postNotificationName(NotificationCenter.closeChats, new Object[0]);
          }
          if (l == 0L)
          {
            localObject12 = new Bundle();
            ((Bundle)localObject12).putBoolean("onlySelect", true);
            ((Bundle)localObject12).putInt("dialogsType", 3);
            ((Bundle)localObject12).putBoolean("allowSwitchAccount", true);
            if (this.contactsToSend != null)
            {
              ((Bundle)localObject12).putString("selectAlertString", LocaleController.getString("SendContactTo", NUM));
              ((Bundle)localObject12).putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", NUM));
              localObject12 = new DialogsActivity((Bundle)localObject12);
              ((DialogsActivity)localObject12).setDelegate(this);
              if (!AndroidUtilities.isTablet()) {
                continue;
              }
              if ((this.layersActionBarLayout.fragmentsStack.size() <= 0) || (!(this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity))) {
                continue;
              }
              paramBoolean2 = true;
              this.actionBarLayout.presentFragment((BaseFragment)localObject12, paramBoolean2, true, true);
              paramBoolean2 = true;
              if ((!SecretMediaViewer.hasInstance()) || (!SecretMediaViewer.getInstance().isVisible())) {
                continue;
              }
              SecretMediaViewer.getInstance().closePhoto(false, false);
              this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
              if (!AndroidUtilities.isTablet()) {
                continue;
              }
              this.actionBarLayout.showLastFragment();
              this.rightActionBarLayout.showLastFragment();
              paramBoolean3 = paramBoolean1;
              break label1276;
            }
            ((Bundle)localObject12).putString("selectAlertString", LocaleController.getString("SendMessagesTo", NUM));
            ((Bundle)localObject12).putString("selectAlertStringGroup", LocaleController.getString("SendMessagesToGroup", NUM));
            continue;
            paramBoolean2 = false;
            continue;
            if ((this.actionBarLayout.fragmentsStack.size() > 1) && ((this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity)))
            {
              paramBoolean2 = true;
              continue;
            }
            paramBoolean2 = false;
            continue;
            if ((PhotoViewer.hasInstance()) && (PhotoViewer.getInstance().isVisible()))
            {
              PhotoViewer.getInstance().closePhoto(false, true);
              continue;
            }
            if ((!ArticleViewer.hasInstance()) || (!ArticleViewer.getInstance().isVisible())) {
              continue;
            }
            ArticleViewer.getInstance().close(false, true);
            continue;
            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            paramBoolean3 = paramBoolean1;
            break label1276;
          }
          localObject12 = new ArrayList();
          ((ArrayList)localObject12).add(Long.valueOf(l));
          didSelectDialogs(null, (ArrayList)localObject12, null, false);
          paramBoolean2 = bool1;
          paramBoolean3 = paramBoolean1;
          break label1276;
        }
        if (((Integer)localObject3).intValue() != 0)
        {
          this.actionBarLayout.presentFragment(new SettingsActivity(), false, true, true);
          if (AndroidUtilities.isTablet())
          {
            this.actionBarLayout.showLastFragment();
            this.rightActionBarLayout.showLastFragment();
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            paramBoolean2 = true;
            paramBoolean3 = paramBoolean1;
            break label1276;
          }
          this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
          continue;
        }
        paramBoolean2 = bool1;
        paramBoolean3 = paramBoolean1;
        if (((Integer)localObject2).intValue() == 0) {
          break label1276;
        }
        localObject12 = new Bundle();
        ((Bundle)localObject12).putBoolean("destroyAfterSelect", true);
        this.actionBarLayout.presentFragment(new ContactsActivity((Bundle)localObject12), false, true, true);
        if (AndroidUtilities.isTablet())
        {
          this.actionBarLayout.showLastFragment();
          this.rightActionBarLayout.showLastFragment();
          this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
          paramBoolean2 = true;
          paramBoolean3 = paramBoolean1;
          break label1276;
        }
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        continue;
        if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
          break label1341;
        }
        localObject12 = new DialogsActivity(null);
        ((DialogsActivity)localObject12).setSideMenu(this.sideMenu);
        this.actionBarLayout.addFragmentToStack((BaseFragment)localObject12);
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        break label1341;
        if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
          break label1341;
        }
        if (!UserConfig.getInstance(this.currentAccount).isClientActivated())
        {
          this.actionBarLayout.addFragmentToStack(new LoginActivity());
          this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
          break label1341;
        }
        localObject12 = new DialogsActivity(null);
        ((DialogsActivity)localObject12).setSideMenu(this.sideMenu);
        this.actionBarLayout.addFragmentToStack((BaseFragment)localObject12);
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        break label1341;
        break label3325;
      }
    }
  }
  
  private void onFinish()
  {
    if (this.finished) {}
    for (;;)
    {
      return;
      this.finished = true;
      if (this.lockRunnable != null)
      {
        AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
        this.lockRunnable = null;
      }
      if (this.currentAccount != -1)
      {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mainUserInfoChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdatedConnectionState);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needShowAlert);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openArticle);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.hasNewContactsToImport);
      }
      NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
      NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
      NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.reloadInterface);
      NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
      NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needSetDayNightTheme);
      NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeOtherAppActivities);
      NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
      NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.notificationsCountUpdated);
    }
  }
  
  private void onPasscodePause()
  {
    if (this.lockRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
      this.lockRunnable = null;
    }
    if (SharedConfig.passcodeHash.length() != 0)
    {
      SharedConfig.lastPauseTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
      this.lockRunnable = new Runnable()
      {
        public void run()
        {
          if (LaunchActivity.this.lockRunnable == this)
          {
            if (!AndroidUtilities.needShowPasscode(true)) {
              break label46;
            }
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("lock app");
            }
            LaunchActivity.this.showPasscodeActivity();
          }
          for (;;)
          {
            LaunchActivity.access$2202(LaunchActivity.this, null);
            return;
            label46:
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("didn't pass lock check");
            }
          }
        }
      };
      if (SharedConfig.appLocked) {
        AndroidUtilities.runOnUIThread(this.lockRunnable, 1000L);
      }
    }
    for (;;)
    {
      SharedConfig.saveConfig();
      return;
      if (SharedConfig.autoLockIn != 0)
      {
        AndroidUtilities.runOnUIThread(this.lockRunnable, SharedConfig.autoLockIn * 1000L + 1000L);
        continue;
        SharedConfig.lastPauseTime = 0;
      }
    }
  }
  
  private void onPasscodeResume()
  {
    if (this.lockRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
      this.lockRunnable = null;
    }
    if (AndroidUtilities.needShowPasscode(true)) {
      showPasscodeActivity();
    }
    if (SharedConfig.lastPauseTime != 0)
    {
      SharedConfig.lastPauseTime = 0;
      SharedConfig.saveConfig();
    }
  }
  
  private void runLinkRequest(final int paramInt1, final String paramString1, final String paramString2, final String paramString3, final String paramString4, final String paramString5, final String paramString6, final boolean paramBoolean, final Integer paramInteger, final String paramString7, final String[] paramArrayOfString, int paramInt2)
  {
    final AlertDialog localAlertDialog = new AlertDialog(this, 1);
    localAlertDialog.setMessage(LocaleController.getString("Loading", NUM));
    localAlertDialog.setCanceledOnTouchOutside(false);
    localAlertDialog.setCancelable(false);
    int i = 0;
    final int j;
    if (paramString1 != null)
    {
      paramString2 = new TLRPC.TL_contacts_resolveUsername();
      paramString2.username = paramString1;
      j = ConnectionsManager.getInstance(paramInt1).sendRequest(paramString2, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (!LaunchActivity.this.isFinishing()) {}
              try
              {
                LaunchActivity.9.this.val$progressDialog.dismiss();
                final TLRPC.TL_contacts_resolvedPeer localTL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer)paramAnonymousTLObject;
                if ((paramAnonymousTL_error == null) && (LaunchActivity.this.actionBarLayout != null) && ((LaunchActivity.9.this.val$game == null) || ((LaunchActivity.9.this.val$game != null) && (!localTL_contacts_resolvedPeer.users.isEmpty()))))
                {
                  MessagesController.getInstance(LaunchActivity.9.this.val$intentAccount).putUsers(localTL_contacts_resolvedPeer.users, false);
                  MessagesController.getInstance(LaunchActivity.9.this.val$intentAccount).putChats(localTL_contacts_resolvedPeer.chats, false);
                  MessagesStorage.getInstance(LaunchActivity.9.this.val$intentAccount).putUsersAndChats(localTL_contacts_resolvedPeer.users, localTL_contacts_resolvedPeer.chats, false, true);
                  if (LaunchActivity.9.this.val$game != null)
                  {
                    localObject2 = new Bundle();
                    ((Bundle)localObject2).putBoolean("onlySelect", true);
                    ((Bundle)localObject2).putBoolean("cantSendToChannels", true);
                    ((Bundle)localObject2).putInt("dialogsType", 1);
                    ((Bundle)localObject2).putString("selectAlertString", LocaleController.getString("SendGameTo", NUM));
                    ((Bundle)localObject2).putString("selectAlertStringGroup", LocaleController.getString("SendGameToGroup", NUM));
                    localObject2 = new DialogsActivity((Bundle)localObject2);
                    ((DialogsActivity)localObject2).setDelegate(new DialogsActivity.DialogsActivityDelegate()
                    {
                      public void didSelectDialogs(DialogsActivity paramAnonymous3DialogsActivity, ArrayList<Long> paramAnonymous3ArrayList, CharSequence paramAnonymous3CharSequence, boolean paramAnonymous3Boolean)
                      {
                        long l = ((Long)paramAnonymous3ArrayList.get(0)).longValue();
                        paramAnonymous3ArrayList = new TLRPC.TL_inputMediaGame();
                        paramAnonymous3ArrayList.id = new TLRPC.TL_inputGameShortName();
                        paramAnonymous3ArrayList.id.short_name = LaunchActivity.9.this.val$game;
                        paramAnonymous3ArrayList.id.bot_id = MessagesController.getInstance(LaunchActivity.9.this.val$intentAccount).getInputUser((TLRPC.User)localTL_contacts_resolvedPeer.users.get(0));
                        SendMessagesHelper.getInstance(LaunchActivity.9.this.val$intentAccount).sendGame(MessagesController.getInstance(LaunchActivity.9.this.val$intentAccount).getInputPeer((int)l), paramAnonymous3ArrayList, 0L, 0L);
                        paramAnonymous3ArrayList = new Bundle();
                        paramAnonymous3ArrayList.putBoolean("scrollToTopOnResume", true);
                        int i = (int)l;
                        int j = (int)(l >> 32);
                        if (i != 0) {
                          if (j == 1) {
                            paramAnonymous3ArrayList.putInt("chat_id", i);
                          }
                        }
                        for (;;)
                        {
                          if (MessagesController.getInstance(LaunchActivity.9.this.val$intentAccount).checkCanOpenChat(paramAnonymous3ArrayList, paramAnonymous3DialogsActivity))
                          {
                            NotificationCenter.getInstance(LaunchActivity.9.this.val$intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(paramAnonymous3ArrayList), true, false, true);
                          }
                          return;
                          if (i > 0)
                          {
                            paramAnonymous3ArrayList.putInt("user_id", i);
                          }
                          else if (i < 0)
                          {
                            paramAnonymous3ArrayList.putInt("chat_id", -i);
                            continue;
                            paramAnonymous3ArrayList.putInt("enc_id", j);
                          }
                        }
                      }
                    });
                    if (AndroidUtilities.isTablet()) {
                      if ((LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() > 0) && ((LaunchActivity.this.layersActionBarLayout.fragmentsStack.get(LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity)))
                      {
                        bool = true;
                        LaunchActivity.this.actionBarLayout.presentFragment((BaseFragment)localObject2, bool, true, true);
                        if ((!SecretMediaViewer.hasInstance()) || (!SecretMediaViewer.getInstance().isVisible())) {
                          break label466;
                        }
                        SecretMediaViewer.getInstance().closePhoto(false, false);
                        LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                        if (!AndroidUtilities.isTablet()) {
                          break label518;
                        }
                        LaunchActivity.this.actionBarLayout.showLastFragment();
                        LaunchActivity.this.rightActionBarLayout.showLastFragment();
                        return;
                      }
                    }
                  }
                }
              }
              catch (Exception localException1)
              {
                for (;;)
                {
                  Object localObject2;
                  FileLog.e(localException1);
                  continue;
                  boolean bool = false;
                  continue;
                  if ((LaunchActivity.this.actionBarLayout.fragmentsStack.size() > 1) && ((LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity))) {}
                  for (bool = true;; bool = false) {
                    break;
                  }
                  label466:
                  if ((PhotoViewer.hasInstance()) && (PhotoViewer.getInstance().isVisible()))
                  {
                    PhotoViewer.getInstance().closePhoto(false, true);
                  }
                  else if ((ArticleViewer.hasInstance()) && (ArticleViewer.getInstance().isVisible()))
                  {
                    ArticleViewer.getInstance().close(false, true);
                    continue;
                    label518:
                    LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    continue;
                    final Object localObject1;
                    if (LaunchActivity.9.this.val$botChat != null)
                    {
                      if (!localException1.users.isEmpty()) {}
                      for (TLRPC.User localUser = (TLRPC.User)localException1.users.get(0);; localObject1 = null)
                      {
                        if ((localUser != null) && ((!localUser.bot) || (!localUser.bot_nochats))) {
                          break label623;
                        }
                        try
                        {
                          Toast.makeText(LaunchActivity.this, LocaleController.getString("BotCantJoinGroups", NUM), 0).show();
                        }
                        catch (Exception localException2)
                        {
                          FileLog.e(localException2);
                        }
                        break;
                      }
                      label623:
                      localObject2 = new Bundle();
                      ((Bundle)localObject2).putBoolean("onlySelect", true);
                      ((Bundle)localObject2).putInt("dialogsType", 2);
                      ((Bundle)localObject2).putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", NUM, new Object[] { UserObject.getUserName((TLRPC.User)localObject1), "%1$s" }));
                      localObject2 = new DialogsActivity((Bundle)localObject2);
                      ((DialogsActivity)localObject2).setDelegate(new DialogsActivity.DialogsActivityDelegate()
                      {
                        public void didSelectDialogs(DialogsActivity paramAnonymous3DialogsActivity, ArrayList<Long> paramAnonymous3ArrayList, CharSequence paramAnonymous3CharSequence, boolean paramAnonymous3Boolean)
                        {
                          long l = ((Long)paramAnonymous3ArrayList.get(0)).longValue();
                          paramAnonymous3DialogsActivity = new Bundle();
                          paramAnonymous3DialogsActivity.putBoolean("scrollToTopOnResume", true);
                          paramAnonymous3DialogsActivity.putInt("chat_id", -(int)l);
                          if ((LaunchActivity.mainFragmentsStack.isEmpty()) || (MessagesController.getInstance(LaunchActivity.9.this.val$intentAccount).checkCanOpenChat(paramAnonymous3DialogsActivity, (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))))
                          {
                            NotificationCenter.getInstance(LaunchActivity.9.this.val$intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            MessagesController.getInstance(LaunchActivity.9.this.val$intentAccount).addUserToChat(-(int)l, localObject1, null, 0, LaunchActivity.9.this.val$botChat, null);
                            LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(paramAnonymous3DialogsActivity), true, false, true);
                          }
                        }
                      });
                      LaunchActivity.this.presentFragment((BaseFragment)localObject2);
                    }
                    else
                    {
                      int i = 0;
                      localObject2 = new Bundle();
                      long l;
                      label774:
                      int j;
                      if (!((TLRPC.TL_contacts_resolvedPeer)localObject1).chats.isEmpty())
                      {
                        ((Bundle)localObject2).putInt("chat_id", ((TLRPC.Chat)((TLRPC.TL_contacts_resolvedPeer)localObject1).chats.get(0)).id);
                        l = -((TLRPC.Chat)((TLRPC.TL_contacts_resolvedPeer)localObject1).chats.get(0)).id;
                        j = i;
                        if (LaunchActivity.9.this.val$botUser != null)
                        {
                          j = i;
                          if (((TLRPC.TL_contacts_resolvedPeer)localObject1).users.size() > 0)
                          {
                            j = i;
                            if (((TLRPC.User)((TLRPC.TL_contacts_resolvedPeer)localObject1).users.get(0)).bot)
                            {
                              ((Bundle)localObject2).putString("botUser", LaunchActivity.9.this.val$botUser);
                              j = 1;
                            }
                          }
                        }
                        if (LaunchActivity.9.this.val$messageId != null) {
                          ((Bundle)localObject2).putInt("message_id", LaunchActivity.9.this.val$messageId.intValue());
                        }
                        if (LaunchActivity.mainFragmentsStack.isEmpty()) {
                          break label1003;
                        }
                      }
                      label1003:
                      for (localObject1 = (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1); (localObject1 == null) || (MessagesController.getInstance(LaunchActivity.9.this.val$intentAccount).checkCanOpenChat((Bundle)localObject2, (BaseFragment)localObject1)); localObject1 = null)
                      {
                        if ((j == 0) || (localObject1 == null) || (!(localObject1 instanceof ChatActivity)) || (((ChatActivity)localObject1).getDialogId() != l)) {
                          break label1008;
                        }
                        ((ChatActivity)localObject1).setBotUser(LaunchActivity.9.this.val$botUser);
                        break;
                        ((Bundle)localObject2).putInt("user_id", ((TLRPC.User)((TLRPC.TL_contacts_resolvedPeer)localObject1).users.get(0)).id);
                        l = ((TLRPC.User)((TLRPC.TL_contacts_resolvedPeer)localObject1).users.get(0)).id;
                        break label774;
                      }
                      label1008:
                      localObject1 = new ChatActivity((Bundle)localObject2);
                      NotificationCenter.getInstance(LaunchActivity.9.this.val$intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                      LaunchActivity.this.actionBarLayout.presentFragment((BaseFragment)localObject1, false, true, true);
                      continue;
                      try
                      {
                        Toast.makeText(LaunchActivity.this, LocaleController.getString("NoUsernameFound", NUM), 0).show();
                      }
                      catch (Exception localException3)
                      {
                        FileLog.e(localException3);
                      }
                    }
                  }
                }
              }
            }
          });
        }
      });
    }
    for (;;)
    {
      if (j != 0) {
        localAlertDialog.setButton(-2, LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            ConnectionsManager.getInstance(paramInt1).cancelRequest(j, true);
            try
            {
              paramAnonymousDialogInterface.dismiss();
              return;
            }
            catch (Exception paramAnonymousDialogInterface)
            {
              for (;;)
              {
                FileLog.e(paramAnonymousDialogInterface);
              }
            }
          }
        });
      }
      try
      {
        localAlertDialog.show();
        for (;;)
        {
          return;
          if (paramString2 != null)
          {
            if (paramInt2 == 0)
            {
              TLRPC.TL_messages_checkChatInvite localTL_messages_checkChatInvite = new TLRPC.TL_messages_checkChatInvite();
              localTL_messages_checkChatInvite.hash = paramString2;
              j = ConnectionsManager.getInstance(paramInt1).sendRequest(localTL_messages_checkChatInvite, new RequestDelegate()
              {
                public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
                {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      if (!LaunchActivity.this.isFinishing()) {}
                      Object localObject2;
                      try
                      {
                        LaunchActivity.10.this.val$progressDialog.dismiss();
                        if ((paramAnonymousTL_error == null) && (LaunchActivity.this.actionBarLayout != null))
                        {
                          Object localObject1 = (TLRPC.ChatInvite)paramAnonymousTLObject;
                          if ((((TLRPC.ChatInvite)localObject1).chat != null) && (!ChatObject.isLeftFromChat(((TLRPC.ChatInvite)localObject1).chat)))
                          {
                            MessagesController.getInstance(LaunchActivity.10.this.val$intentAccount).putChat(((TLRPC.ChatInvite)localObject1).chat, false);
                            localObject3 = new ArrayList();
                            ((ArrayList)localObject3).add(((TLRPC.ChatInvite)localObject1).chat);
                            MessagesStorage.getInstance(LaunchActivity.10.this.val$intentAccount).putUsersAndChats(null, (ArrayList)localObject3, false, true);
                            localObject3 = new Bundle();
                            ((Bundle)localObject3).putInt("chat_id", ((TLRPC.ChatInvite)localObject1).chat.id);
                            if ((LaunchActivity.mainFragmentsStack.isEmpty()) || (MessagesController.getInstance(LaunchActivity.10.this.val$intentAccount).checkCanOpenChat((Bundle)localObject3, (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))))
                            {
                              localObject1 = new ChatActivity((Bundle)localObject3);
                              NotificationCenter.getInstance(LaunchActivity.10.this.val$intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                              LaunchActivity.this.actionBarLayout.presentFragment((BaseFragment)localObject1, false, true, true);
                            }
                            return;
                          }
                        }
                      }
                      catch (Exception localException)
                      {
                        for (;;)
                        {
                          FileLog.e(localException);
                          continue;
                          if (((localException.chat != null) || ((localException.channel) && (!localException.megagroup))) && ((localException.chat == null) || ((ChatObject.isChannel(localException.chat)) && (!localException.chat.megagroup)) || (LaunchActivity.mainFragmentsStack.isEmpty()))) {
                            break;
                          }
                          localObject3 = (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1);
                          ((BaseFragment)localObject3).showDialog(new JoinGroupAlert(LaunchActivity.this, localException, LaunchActivity.10.this.val$group, (BaseFragment)localObject3));
                        }
                        Object localObject3 = new AlertDialog.Builder(LaunchActivity.this);
                        ((AlertDialog.Builder)localObject3).setTitle(LocaleController.getString("AppName", NUM));
                        if (localException.chat != null) {}
                        for (localObject2 = localException.chat.title;; localObject2 = ((TLRPC.ChatInvite)localObject2).title)
                        {
                          ((AlertDialog.Builder)localObject3).setMessage(LocaleController.formatString("ChannelJoinTo", NUM, new Object[] { localObject2 }));
                          ((AlertDialog.Builder)localObject3).setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
                          {
                            public void onClick(DialogInterface paramAnonymous3DialogInterface, int paramAnonymous3Int)
                            {
                              LaunchActivity.this.runLinkRequest(LaunchActivity.10.this.val$intentAccount, LaunchActivity.10.this.val$username, LaunchActivity.10.this.val$group, LaunchActivity.10.this.val$sticker, LaunchActivity.10.this.val$botUser, LaunchActivity.10.this.val$botChat, LaunchActivity.10.this.val$message, LaunchActivity.10.this.val$hasUrl, LaunchActivity.10.this.val$messageId, LaunchActivity.10.this.val$game, LaunchActivity.10.this.val$instantView, 1);
                            }
                          });
                          ((AlertDialog.Builder)localObject3).setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                          LaunchActivity.this.showAlertDialog((AlertDialog.Builder)localObject3);
                          break;
                        }
                        localObject2 = new AlertDialog.Builder(LaunchActivity.this);
                        ((AlertDialog.Builder)localObject2).setTitle(LocaleController.getString("AppName", NUM));
                        if (!paramAnonymousTL_error.text.startsWith("FLOOD_WAIT")) {
                          break label546;
                        }
                      }
                      ((AlertDialog.Builder)localObject2).setMessage(LocaleController.getString("FloodWait", NUM));
                      for (;;)
                      {
                        ((AlertDialog.Builder)localObject2).setPositiveButton(LocaleController.getString("OK", NUM), null);
                        LaunchActivity.this.showAlertDialog((AlertDialog.Builder)localObject2);
                        break;
                        label546:
                        ((AlertDialog.Builder)localObject2).setMessage(LocaleController.getString("JoinToGroupErrorNotExist", NUM));
                      }
                    }
                  });
                }
              }, 2);
              break;
            }
            j = i;
            if (paramInt2 != 1) {
              break;
            }
            paramString1 = new TLRPC.TL_messages_importChatInvite();
            paramString1.hash = paramString2;
            ConnectionsManager.getInstance(paramInt1).sendRequest(paramString1, new RequestDelegate()
            {
              public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
              {
                if (paramAnonymousTL_error == null)
                {
                  TLRPC.Updates localUpdates = (TLRPC.Updates)paramAnonymousTLObject;
                  MessagesController.getInstance(paramInt1).processUpdates(localUpdates, false);
                }
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    if (!LaunchActivity.this.isFinishing()) {}
                    AlertDialog.Builder localBuilder;
                    try
                    {
                      LaunchActivity.11.this.val$progressDialog.dismiss();
                      if (paramAnonymousTL_error == null)
                      {
                        if (LaunchActivity.this.actionBarLayout != null)
                        {
                          Object localObject1 = (TLRPC.Updates)paramAnonymousTLObject;
                          if (!((TLRPC.Updates)localObject1).chats.isEmpty())
                          {
                            Object localObject2 = (TLRPC.Chat)((TLRPC.Updates)localObject1).chats.get(0);
                            ((TLRPC.Chat)localObject2).left = false;
                            ((TLRPC.Chat)localObject2).kicked = false;
                            MessagesController.getInstance(LaunchActivity.11.this.val$intentAccount).putUsers(((TLRPC.Updates)localObject1).users, false);
                            MessagesController.getInstance(LaunchActivity.11.this.val$intentAccount).putChats(((TLRPC.Updates)localObject1).chats, false);
                            localObject1 = new Bundle();
                            ((Bundle)localObject1).putInt("chat_id", ((TLRPC.Chat)localObject2).id);
                            if ((LaunchActivity.mainFragmentsStack.isEmpty()) || (MessagesController.getInstance(LaunchActivity.11.this.val$intentAccount).checkCanOpenChat((Bundle)localObject1, (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))))
                            {
                              localObject2 = new ChatActivity((Bundle)localObject1);
                              NotificationCenter.getInstance(LaunchActivity.11.this.val$intentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                              LaunchActivity.this.actionBarLayout.presentFragment((BaseFragment)localObject2, false, true, true);
                            }
                          }
                        }
                        return;
                      }
                    }
                    catch (Exception localException)
                    {
                      for (;;)
                      {
                        FileLog.e(localException);
                      }
                      localBuilder = new AlertDialog.Builder(LaunchActivity.this);
                      localBuilder.setTitle(LocaleController.getString("AppName", NUM));
                      if (!paramAnonymousTL_error.text.startsWith("FLOOD_WAIT")) {
                        break label318;
                      }
                    }
                    localBuilder.setMessage(LocaleController.getString("FloodWait", NUM));
                    for (;;)
                    {
                      localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                      LaunchActivity.this.showAlertDialog(localBuilder);
                      break;
                      label318:
                      if (paramAnonymousTL_error.text.equals("USERS_TOO_MUCH")) {
                        localBuilder.setMessage(LocaleController.getString("JoinToGroupErrorFull", NUM));
                      } else {
                        localBuilder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", NUM));
                      }
                    }
                  }
                });
              }
            }, 2);
            j = i;
            break;
          }
          if (paramString3 == null) {
            break label309;
          }
          if (!mainFragmentsStack.isEmpty())
          {
            paramString1 = new TLRPC.TL_inputStickerSetShortName();
            paramString1.short_name = paramString3;
            paramString2 = (BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1);
            paramString2.showDialog(new StickersAlert(this, paramString2, paramString1, null, null));
          }
        }
        label309:
        if (paramString6 != null)
        {
          paramString1 = new Bundle();
          paramString1.putBoolean("onlySelect", true);
          paramString1 = new DialogsActivity(paramString1);
          paramString1.setDelegate(new DialogsActivity.DialogsActivityDelegate()
          {
            public void didSelectDialogs(DialogsActivity paramAnonymousDialogsActivity, ArrayList<Long> paramAnonymousArrayList, CharSequence paramAnonymousCharSequence, boolean paramAnonymousBoolean)
            {
              long l = ((Long)paramAnonymousArrayList.get(0)).longValue();
              paramAnonymousArrayList = new Bundle();
              paramAnonymousArrayList.putBoolean("scrollToTopOnResume", true);
              paramAnonymousArrayList.putBoolean("hasUrl", paramBoolean);
              int i = (int)l;
              int j = (int)(l >> 32);
              if (i != 0) {
                if (j == 1) {
                  paramAnonymousArrayList.putInt("chat_id", i);
                }
              }
              for (;;)
              {
                if (MessagesController.getInstance(paramInt1).checkCanOpenChat(paramAnonymousArrayList, paramAnonymousDialogsActivity))
                {
                  NotificationCenter.getInstance(paramInt1).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                  DataQuery.getInstance(paramInt1).saveDraft(l, paramString6, null, null, false);
                  LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(paramAnonymousArrayList), true, false, true);
                }
                return;
                if (i > 0)
                {
                  paramAnonymousArrayList.putInt("user_id", i);
                }
                else if (i < 0)
                {
                  paramAnonymousArrayList.putInt("chat_id", -i);
                  continue;
                  paramAnonymousArrayList.putInt("enc_id", j);
                }
              }
            }
          });
          presentFragment(paramString1, false, true);
          j = i;
          continue;
        }
        j = i;
        if (paramArrayOfString == null) {
          continue;
        }
        j = i;
      }
      catch (Exception paramString1)
      {
        for (;;) {}
      }
    }
  }
  
  private void showLanguageAlert(boolean paramBoolean)
  {
    String str2;
    LocaleController.LocaleInfo[] arrayOfLocaleInfo;
    try
    {
      if (this.loadingLocaleDialog) {}
      for (;;)
      {
        return;
        String str1 = MessagesController.getGlobalMainSettings().getString("language_showed2", "");
        str2 = LocaleController.getSystemLocaleStringIso639().toLowerCase();
        if ((paramBoolean) || (!str1.equals(str2))) {
          break;
        }
        if (BuildVars.LOGS_ENABLED)
        {
          localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          FileLog.d("alert already showed for " + str1);
        }
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
      arrayOfLocaleInfo = new LocaleController.LocaleInfo[2];
      if (!str2.contains("-")) {
        break label572;
      }
    }
    Object localObject2 = str2.split("-")[0];
    label114:
    Object localObject1;
    if ("in".equals(localObject2)) {
      localObject1 = "id";
    }
    label129:
    for (int i = 0;; i++)
    {
      Object localObject3;
      if (i < LocaleController.getInstance().languages.size())
      {
        localObject3 = (LocaleController.LocaleInfo)LocaleController.getInstance().languages.get(i);
        if (((LocaleController.LocaleInfo)localObject3).shortName.equals("en")) {
          arrayOfLocaleInfo[0] = localObject3;
        }
        if ((((LocaleController.LocaleInfo)localObject3).shortName.replace("_", "-").equals(str2)) || (((LocaleController.LocaleInfo)localObject3).shortName.equals(localObject2)) || ((localObject1 != null) && (((LocaleController.LocaleInfo)localObject3).shortName.equals(localObject1)))) {
          arrayOfLocaleInfo[1] = localObject3;
        }
        if ((arrayOfLocaleInfo[0] == null) || (arrayOfLocaleInfo[1] == null)) {}
      }
      else
      {
        if ((arrayOfLocaleInfo[0] == null) || (arrayOfLocaleInfo[1] == null) || (arrayOfLocaleInfo[0] == arrayOfLocaleInfo[1])) {
          break;
        }
        if (BuildVars.LOGS_ENABLED)
        {
          localObject1 = new java/lang/StringBuilder;
          ((StringBuilder)localObject1).<init>();
          FileLog.d("show lang alert for " + arrayOfLocaleInfo[0].getKey() + " and " + arrayOfLocaleInfo[1].getKey());
        }
        this.systemLocaleStrings = null;
        this.englishLocaleStrings = null;
        this.loadingLocaleDialog = true;
        localObject2 = new org/telegram/tgnet/TLRPC$TL_langpack_getStrings;
        ((TLRPC.TL_langpack_getStrings)localObject2).<init>();
        ((TLRPC.TL_langpack_getStrings)localObject2).lang_code = arrayOfLocaleInfo[1].shortName.replace("_", "-");
        ((TLRPC.TL_langpack_getStrings)localObject2).keys.add("English");
        ((TLRPC.TL_langpack_getStrings)localObject2).keys.add("ChooseYourLanguage");
        ((TLRPC.TL_langpack_getStrings)localObject2).keys.add("ChooseYourLanguageOther");
        ((TLRPC.TL_langpack_getStrings)localObject2).keys.add("ChangeLanguageLater");
        localObject1 = ConnectionsManager.getInstance(this.currentAccount);
        localObject3 = new org/telegram/ui/LaunchActivity$27;
        ((27)localObject3).<init>(this, arrayOfLocaleInfo, str2);
        ((ConnectionsManager)localObject1).sendRequest((TLObject)localObject2, (RequestDelegate)localObject3, 8);
        localObject2 = new org/telegram/tgnet/TLRPC$TL_langpack_getStrings;
        ((TLRPC.TL_langpack_getStrings)localObject2).<init>();
        ((TLRPC.TL_langpack_getStrings)localObject2).lang_code = arrayOfLocaleInfo[0].shortName.replace("_", "-");
        ((TLRPC.TL_langpack_getStrings)localObject2).keys.add("English");
        ((TLRPC.TL_langpack_getStrings)localObject2).keys.add("ChooseYourLanguage");
        ((TLRPC.TL_langpack_getStrings)localObject2).keys.add("ChooseYourLanguageOther");
        ((TLRPC.TL_langpack_getStrings)localObject2).keys.add("ChangeLanguageLater");
        localObject1 = ConnectionsManager.getInstance(this.currentAccount);
        localObject3 = new org/telegram/ui/LaunchActivity$28;
        ((28)localObject3).<init>(this, arrayOfLocaleInfo, str2);
        ((ConnectionsManager)localObject1).sendRequest((TLObject)localObject2, (RequestDelegate)localObject3, 8);
        break;
        label572:
        localObject2 = str2;
        break label114;
        if ("iw".equals(localObject2))
        {
          localObject1 = "he";
          break label129;
        }
        if ("jw".equals(localObject2))
        {
          localObject1 = "jv";
          break label129;
        }
        localObject1 = null;
        break label129;
      }
    }
  }
  
  private void showLanguageAlertInternal(LocaleController.LocaleInfo paramLocaleInfo1, LocaleController.LocaleInfo paramLocaleInfo2, String paramString)
  {
    try
    {
      this.loadingLocaleDialog = false;
      int i;
      AlertDialog.Builder localBuilder;
      LinearLayout localLinearLayout;
      LanguageCell[] arrayOfLanguageCell;
      LocaleController.LocaleInfo[] arrayOfLocaleInfo1;
      Object localObject;
      if ((paramLocaleInfo1.builtIn) || (LocaleController.getInstance().isCurrentLocalLocale()))
      {
        i = 1;
        localBuilder = new org/telegram/ui/ActionBar/AlertDialog$Builder;
        localBuilder.<init>(this);
        localBuilder.setTitle(getStringForLanguageAlert(this.systemLocaleStrings, "ChooseYourLanguage", NUM));
        localBuilder.setSubtitle(getStringForLanguageAlert(this.englishLocaleStrings, "ChooseYourLanguage", NUM));
        localLinearLayout = new android/widget/LinearLayout;
        localLinearLayout.<init>(this);
        localLinearLayout.setOrientation(1);
        arrayOfLanguageCell = new LanguageCell[2];
        arrayOfLocaleInfo1 = new LocaleController.LocaleInfo[1];
        LocaleController.LocaleInfo[] arrayOfLocaleInfo2 = new LocaleController.LocaleInfo[2];
        String str = getStringForLanguageAlert(this.systemLocaleStrings, "English", NUM);
        if (i == 0) {
          break label330;
        }
        localObject = paramLocaleInfo1;
        label134:
        arrayOfLocaleInfo2[0] = localObject;
        if (i == 0) {
          break label336;
        }
        localObject = paramLocaleInfo2;
        label148:
        arrayOfLocaleInfo2[1] = localObject;
        if (i == 0) {
          break label342;
        }
        label159:
        arrayOfLocaleInfo1[0] = paramLocaleInfo1;
        i = 0;
        label167:
        if (i >= 2) {
          break label358;
        }
        paramLocaleInfo1 = new org/telegram/ui/Cells/LanguageCell;
        paramLocaleInfo1.<init>(this, true);
        arrayOfLanguageCell[i] = paramLocaleInfo1;
        localObject = arrayOfLanguageCell[i];
        LocaleController.LocaleInfo localLocaleInfo = arrayOfLocaleInfo2[i];
        if (arrayOfLocaleInfo2[i] != paramLocaleInfo2) {
          break label347;
        }
        paramLocaleInfo1 = str;
        label215:
        ((LanguageCell)localObject).setLanguage(localLocaleInfo, paramLocaleInfo1, true);
        arrayOfLanguageCell[i].setTag(Integer.valueOf(i));
        arrayOfLanguageCell[i].setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 2));
        paramLocaleInfo1 = arrayOfLanguageCell[i];
        if (i != 0) {
          break label352;
        }
      }
      label330:
      label336:
      label342:
      label347:
      label352:
      for (boolean bool = true;; bool = false)
      {
        paramLocaleInfo1.setLanguageSelected(bool);
        localLinearLayout.addView(arrayOfLanguageCell[i], LayoutHelper.createLinear(-1, 48));
        paramLocaleInfo1 = arrayOfLanguageCell[i];
        localObject = new org/telegram/ui/LaunchActivity$24;
        ((24)localObject).<init>(this, arrayOfLocaleInfo1, arrayOfLanguageCell);
        paramLocaleInfo1.setOnClickListener((View.OnClickListener)localObject);
        i++;
        break label167;
        i = 0;
        break;
        localObject = paramLocaleInfo2;
        break label134;
        localObject = paramLocaleInfo1;
        break label148;
        paramLocaleInfo1 = paramLocaleInfo2;
        break label159;
        paramLocaleInfo1 = null;
        break label215;
      }
      label358:
      paramLocaleInfo2 = new org/telegram/ui/Cells/LanguageCell;
      paramLocaleInfo2.<init>(this, true);
      paramLocaleInfo2.setValue(getStringForLanguageAlert(this.systemLocaleStrings, "ChooseYourLanguageOther", NUM), getStringForLanguageAlert(this.englishLocaleStrings, "ChooseYourLanguageOther", NUM));
      paramLocaleInfo1 = new org/telegram/ui/LaunchActivity$25;
      paramLocaleInfo1.<init>(this);
      paramLocaleInfo2.setOnClickListener(paramLocaleInfo1);
      localLinearLayout.addView(paramLocaleInfo2, LayoutHelper.createLinear(-1, 48));
      localBuilder.setView(localLinearLayout);
      paramLocaleInfo2 = LocaleController.getString("OK", NUM);
      paramLocaleInfo1 = new org/telegram/ui/LaunchActivity$26;
      paramLocaleInfo1.<init>(this, arrayOfLocaleInfo1);
      localBuilder.setNegativeButton(paramLocaleInfo2, paramLocaleInfo1);
      this.localeDialog = showAlertDialog(localBuilder);
      MessagesController.getGlobalMainSettings().edit().putString("language_showed2", paramString).commit();
      return;
    }
    catch (Exception paramLocaleInfo1)
    {
      for (;;)
      {
        FileLog.e(paramLocaleInfo1);
      }
    }
  }
  
  private void showPasscodeActivity()
  {
    if (this.passcodeView == null) {
      return;
    }
    SharedConfig.appLocked = true;
    if ((SecretMediaViewer.hasInstance()) && (SecretMediaViewer.getInstance().isVisible())) {
      SecretMediaViewer.getInstance().closePhoto(false, false);
    }
    for (;;)
    {
      this.passcodeView.onShow();
      SharedConfig.isWaitingForPasscodeEnter = true;
      this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
      this.passcodeView.setDelegate(new PasscodeView.PasscodeViewDelegate()
      {
        public void didAcceptedPassword()
        {
          SharedConfig.isWaitingForPasscodeEnter = false;
          if (LaunchActivity.this.passcodeSaveIntent != null)
          {
            LaunchActivity.this.handleIntent(LaunchActivity.this.passcodeSaveIntent, LaunchActivity.this.passcodeSaveIntentIsNew, LaunchActivity.this.passcodeSaveIntentIsRestore, true);
            LaunchActivity.access$902(LaunchActivity.this, null);
          }
          LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
          LaunchActivity.this.actionBarLayout.showLastFragment();
          if (AndroidUtilities.isTablet())
          {
            LaunchActivity.this.layersActionBarLayout.showLastFragment();
            LaunchActivity.this.rightActionBarLayout.showLastFragment();
          }
        }
      });
      break;
      if ((PhotoViewer.hasInstance()) && (PhotoViewer.getInstance().isVisible())) {
        PhotoViewer.getInstance().closePhoto(false, true);
      } else if ((ArticleViewer.hasInstance()) && (ArticleViewer.getInstance().isVisible())) {
        ArticleViewer.getInstance().close(false, true);
      }
    }
  }
  
  private void switchToAvailableAccountOrLogout()
  {
    int i = -1;
    int j = 0;
    int k = i;
    if (j < 3)
    {
      if (UserConfig.getInstance(j).isClientActivated()) {
        k = j;
      }
    }
    else
    {
      if (k == -1) {
        break label41;
      }
      switchToAccount(k, true);
    }
    for (;;)
    {
      return;
      j++;
      break;
      label41:
      if (this.drawerLayoutAdapter != null) {
        this.drawerLayoutAdapter.notifyDataSetChanged();
      }
      Iterator localIterator = this.actionBarLayout.fragmentsStack.iterator();
      while (localIterator.hasNext()) {
        ((BaseFragment)localIterator.next()).onFragmentDestroy();
      }
      this.actionBarLayout.fragmentsStack.clear();
      if (AndroidUtilities.isTablet())
      {
        localIterator = this.layersActionBarLayout.fragmentsStack.iterator();
        while (localIterator.hasNext()) {
          ((BaseFragment)localIterator.next()).onFragmentDestroy();
        }
        this.layersActionBarLayout.fragmentsStack.clear();
        localIterator = this.rightActionBarLayout.fragmentsStack.iterator();
        while (localIterator.hasNext()) {
          ((BaseFragment)localIterator.next()).onFragmentDestroy();
        }
        this.rightActionBarLayout.fragmentsStack.clear();
      }
      startActivity(new Intent(this, IntroActivity.class));
      onFinish();
      finish();
    }
  }
  
  private void updateCurrentConnectionState(int paramInt)
  {
    if (this.actionBarLayout == null) {
      return;
    }
    String str1 = null;
    String str2 = null;
    Object localObject = null;
    if (this.currentConnectionState == 2) {
      str1 = LocaleController.getString("WaitingForNetwork", NUM);
    }
    for (;;)
    {
      this.actionBarLayout.setTitleOverlayText(str1, str2, (Runnable)localObject);
      break;
      if (this.currentConnectionState == 1)
      {
        str1 = LocaleController.getString("Connecting", NUM);
        localObject = new Runnable()
        {
          public void run()
          {
            if (AndroidUtilities.isTablet()) {
              if ((LaunchActivity.layerFragmentsStack.isEmpty()) || (!(LaunchActivity.layerFragmentsStack.get(LaunchActivity.layerFragmentsStack.size() - 1) instanceof ProxySettingsActivity))) {
                break label65;
              }
            }
            for (;;)
            {
              return;
              if ((LaunchActivity.mainFragmentsStack.isEmpty()) || (!(LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1) instanceof ProxySettingsActivity))) {
                label65:
                LaunchActivity.this.presentFragment(new ProxySettingsActivity());
              }
            }
          }
        };
      }
      else if (this.currentConnectionState == 5)
      {
        str1 = LocaleController.getString("Updating", NUM);
      }
      else if (this.currentConnectionState == 4)
      {
        str1 = LocaleController.getString("ConnectingToProxy", NUM);
        str2 = LocaleController.getString("ConnectingToProxyTapToDisable", NUM);
        localObject = new Runnable()
        {
          public void run()
          {
            if ((LaunchActivity.this.actionBarLayout == null) || (LaunchActivity.this.actionBarLayout.fragmentsStack.isEmpty())) {}
            for (;;)
            {
              return;
              SharedPreferences localSharedPreferences = MessagesController.getGlobalMainSettings();
              BaseFragment localBaseFragment = (BaseFragment)LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1);
              AlertDialog.Builder localBuilder = new AlertDialog.Builder(LaunchActivity.this);
              localBuilder.setTitle(LocaleController.getString("Proxy", NUM));
              localBuilder.setMessage(LocaleController.formatString("ConnectingToProxyDisableAlert", NUM, new Object[] { localSharedPreferences.getString("proxy_ip", "") }));
              localBuilder.setPositiveButton(LocaleController.getString("ConnectingToProxyDisable", NUM), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  paramAnonymous2DialogInterface = MessagesController.getGlobalMainSettings().edit();
                  paramAnonymous2DialogInterface.putBoolean("proxy_enabled", false);
                  paramAnonymous2DialogInterface.commit();
                  for (paramAnonymous2Int = 0; paramAnonymous2Int < 3; paramAnonymous2Int++) {
                    ConnectionsManager.native_setProxySettings(paramAnonymous2Int, "", 0, "", "");
                  }
                  NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
                }
              });
              localBuilder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
              localBaseFragment.showDialog(localBuilder.create());
            }
          }
        };
      }
    }
  }
  
  public void didReceivedNotification(int paramInt1, final int paramInt2, final Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.appDidLogout) {
      switchToAvailableAccountOrLogout();
    }
    label318:
    label1113:
    for (;;)
    {
      return;
      if (paramInt1 == NotificationCenter.closeOtherAppActivities)
      {
        if (paramVarArgs[0] != this)
        {
          onFinish();
          finish();
        }
      }
      else if (paramInt1 == NotificationCenter.didUpdatedConnectionState)
      {
        paramInt1 = ConnectionsManager.getInstance(paramInt2).getConnectionState();
        if (this.currentConnectionState != paramInt1)
        {
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("switch to state " + paramInt1);
          }
          this.currentConnectionState = paramInt1;
          updateCurrentConnectionState(paramInt2);
        }
      }
      else if (paramInt1 == NotificationCenter.mainUserInfoChanged)
      {
        this.drawerLayoutAdapter.notifyDataSetChanged();
      }
      else
      {
        final Object localObject;
        AlertDialog.Builder localBuilder;
        if (paramInt1 == NotificationCenter.needShowAlert)
        {
          localObject = (Integer)paramVarArgs[0];
          localBuilder = new AlertDialog.Builder(this);
          localBuilder.setTitle(LocaleController.getString("AppName", NUM));
          localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), null);
          if (((Integer)localObject).intValue() != 2) {
            localBuilder.setNegativeButton(LocaleController.getString("MoreInfo", NUM), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
              {
                if (!LaunchActivity.mainFragmentsStack.isEmpty()) {
                  MessagesController.getInstance(paramInt2).openByUserName("spambot", (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1), 1);
                }
              }
            });
          }
          if (((Integer)localObject).intValue() == 0) {
            localBuilder.setMessage(LocaleController.getString("NobodyLikesSpam1", NUM));
          }
          for (;;)
          {
            if (mainFragmentsStack.isEmpty()) {
              break label318;
            }
            ((BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(localBuilder.create());
            break;
            if (((Integer)localObject).intValue() == 1) {
              localBuilder.setMessage(LocaleController.getString("NobodyLikesSpam2", NUM));
            } else if (((Integer)localObject).intValue() == 2) {
              localBuilder.setMessage((String)paramVarArgs[1]);
            }
          }
        }
        else if (paramInt1 == NotificationCenter.wasUnableToFindCurrentLocation)
        {
          paramVarArgs = (HashMap)paramVarArgs[0];
          localObject = new AlertDialog.Builder(this);
          ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", NUM));
          ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", NUM), null);
          ((AlertDialog.Builder)localObject).setNegativeButton(LocaleController.getString("ShareYouLocationUnableManually", NUM), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
              if (LaunchActivity.mainFragmentsStack.isEmpty()) {}
              for (;;)
              {
                return;
                if (AndroidUtilities.isGoogleMapsInstalled((BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1)))
                {
                  paramAnonymousDialogInterface = new LocationActivity(0);
                  paramAnonymousDialogInterface.setDelegate(new LocationActivity.LocationActivityDelegate()
                  {
                    public void didSelectLocation(TLRPC.MessageMedia paramAnonymous2MessageMedia, int paramAnonymous2Int)
                    {
                      Iterator localIterator = LaunchActivity.19.this.val$waitingForLocation.entrySet().iterator();
                      while (localIterator.hasNext())
                      {
                        MessageObject localMessageObject = (MessageObject)((Map.Entry)localIterator.next()).getValue();
                        SendMessagesHelper.getInstance(LaunchActivity.19.this.val$account).sendMessage(paramAnonymous2MessageMedia, localMessageObject.getDialogId(), localMessageObject, null, null);
                      }
                    }
                  });
                  LaunchActivity.this.presentFragment(paramAnonymousDialogInterface);
                }
              }
            }
          });
          ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("ShareYouLocationUnable", NUM));
          if (!mainFragmentsStack.isEmpty()) {
            ((BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(((AlertDialog.Builder)localObject).create());
          }
        }
        else if (paramInt1 == NotificationCenter.didSetNewWallpapper)
        {
          if (this.sideMenu != null)
          {
            paramVarArgs = this.sideMenu.getChildAt(0);
            if (paramVarArgs != null) {
              paramVarArgs.invalidate();
            }
          }
        }
        else if (paramInt1 == NotificationCenter.didSetPasscode)
        {
          if ((SharedConfig.passcodeHash.length() > 0) && (!SharedConfig.allowScreenCapture)) {
            try
            {
              getWindow().setFlags(8192, 8192);
            }
            catch (Exception paramVarArgs)
            {
              FileLog.e(paramVarArgs);
            }
          } else {
            try
            {
              getWindow().clearFlags(8192);
            }
            catch (Exception paramVarArgs)
            {
              FileLog.e(paramVarArgs);
            }
          }
        }
        else if (paramInt1 == NotificationCenter.reloadInterface)
        {
          rebuildAllFragments(false);
        }
        else if (paramInt1 == NotificationCenter.suggestedLangpack)
        {
          showLanguageAlert(false);
        }
        else if (paramInt1 == NotificationCenter.openArticle)
        {
          if (!mainFragmentsStack.isEmpty())
          {
            ArticleViewer.getInstance().setParentActivity(this, (BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1));
            ArticleViewer.getInstance().open((TLRPC.TL_webPage)paramVarArgs[0], (String)paramVarArgs[1]);
          }
        }
        else if (paramInt1 == NotificationCenter.hasNewContactsToImport)
        {
          if ((this.actionBarLayout != null) && (!this.actionBarLayout.fragmentsStack.isEmpty()))
          {
            ((Integer)paramVarArgs[0]).intValue();
            localObject = (HashMap)paramVarArgs[1];
            final boolean bool1 = ((Boolean)paramVarArgs[2]).booleanValue();
            final boolean bool2 = ((Boolean)paramVarArgs[3]).booleanValue();
            paramVarArgs = (BaseFragment)this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
            localBuilder = new AlertDialog.Builder(this);
            localBuilder.setTitle(LocaleController.getString("UpdateContactsTitle", NUM));
            localBuilder.setMessage(LocaleController.getString("UpdateContactsMessage", NUM));
            localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
              {
                ContactsController.getInstance(paramInt2).syncPhoneBookByAlert(localObject, bool1, bool2, false);
              }
            });
            localBuilder.setNegativeButton(LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
              {
                ContactsController.getInstance(paramInt2).syncPhoneBookByAlert(localObject, bool1, bool2, true);
              }
            });
            localBuilder.setOnBackButtonListener(new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
              {
                ContactsController.getInstance(paramInt2).syncPhoneBookByAlert(localObject, bool1, bool2, true);
              }
            });
            localObject = localBuilder.create();
            paramVarArgs.showDialog((Dialog)localObject);
            ((AlertDialog)localObject).setCanceledOnTouchOutside(false);
          }
        }
        else if (paramInt1 == NotificationCenter.didSetNewTheme)
        {
          if (!((Boolean)paramVarArgs[0]).booleanValue())
          {
            if (this.sideMenu != null)
            {
              this.sideMenu.setBackgroundColor(Theme.getColor("chats_menuBackground"));
              this.sideMenu.setGlowColor(Theme.getColor("chats_menuBackground"));
              this.sideMenu.getAdapter().notifyDataSetChanged();
            }
            if (Build.VERSION.SDK_INT >= 21) {
              try
              {
                paramVarArgs = new android/app/ActivityManager$TaskDescription;
                paramVarArgs.<init>(null, null, Theme.getColor("actionBarDefault") | 0xFF000000);
                setTaskDescription(paramVarArgs);
              }
              catch (Exception paramVarArgs) {}
            }
          }
        }
        else if (paramInt1 == NotificationCenter.needSetDayNightTheme)
        {
          paramVarArgs = (Theme.ThemeInfo)paramVarArgs[0];
          this.actionBarLayout.animateThemedValues(paramVarArgs);
          if (AndroidUtilities.isTablet())
          {
            this.layersActionBarLayout.animateThemedValues(paramVarArgs);
            this.rightActionBarLayout.animateThemedValues(paramVarArgs);
          }
        }
        else if ((paramInt1 == NotificationCenter.notificationsCountUpdated) && (this.sideMenu != null))
        {
          paramVarArgs = (Integer)paramVarArgs[0];
          paramInt2 = this.sideMenu.getChildCount();
          for (paramInt1 = 0;; paramInt1++)
          {
            if (paramInt1 >= paramInt2) {
              break label1113;
            }
            localObject = this.sideMenu.getChildAt(paramInt1);
            if (((localObject instanceof DrawerUserCell)) && (((DrawerUserCell)localObject).getAccountNumber() == paramVarArgs.intValue()))
            {
              ((View)localObject).invalidate();
              break;
            }
          }
        }
      }
    }
  }
  
  public void didSelectDialogs(DialogsActivity paramDialogsActivity, ArrayList<Long> paramArrayList, CharSequence paramCharSequence, boolean paramBoolean)
  {
    long l = ((Long)paramArrayList.get(0)).longValue();
    int i = (int)l;
    int j = (int)(l >> 32);
    paramArrayList = new Bundle();
    int k;
    if (paramDialogsActivity != null)
    {
      k = paramDialogsActivity.getCurrentAccount();
      paramArrayList.putBoolean("scrollToTopOnResume", true);
      if (!AndroidUtilities.isTablet()) {
        NotificationCenter.getInstance(k).postNotificationName(NotificationCenter.closeChats, new Object[0]);
      }
      if (i == 0) {
        break label151;
      }
      if (j != 1) {
        break label116;
      }
      paramArrayList.putInt("chat_id", i);
      label93:
      if (MessagesController.getInstance(k).checkCanOpenChat(paramArrayList, paramDialogsActivity)) {
        break label163;
      }
    }
    for (;;)
    {
      return;
      k = this.currentAccount;
      break;
      label116:
      if (i > 0)
      {
        paramArrayList.putInt("user_id", i);
        break label93;
      }
      if (i >= 0) {
        break label93;
      }
      paramArrayList.putInt("chat_id", -i);
      break label93;
      label151:
      paramArrayList.putInt("enc_id", j);
      break label93;
      label163:
      paramCharSequence = new ChatActivity(paramArrayList);
      paramArrayList = this.actionBarLayout;
      if (paramDialogsActivity != null)
      {
        paramBoolean = true;
        if (paramDialogsActivity != null) {
          break label420;
        }
      }
      label420:
      for (boolean bool = true;; bool = false)
      {
        paramArrayList.presentFragment(paramCharSequence, paramBoolean, bool, true);
        if (this.videoPath != null)
        {
          paramCharSequence.openVideoEditor(this.videoPath, this.sendingText);
          this.sendingText = null;
        }
        if (this.photoPathsArray != null)
        {
          if ((this.sendingText != null) && (this.sendingText.length() <= 200) && (this.photoPathsArray.size() == 1))
          {
            ((SendMessagesHelper.SendingMediaInfo)this.photoPathsArray.get(0)).caption = this.sendingText;
            this.sendingText = null;
          }
          SendMessagesHelper.prepareSendingMedia(this.photoPathsArray, l, null, null, false, false);
        }
        if (this.sendingText != null) {
          SendMessagesHelper.prepareSendingText(this.sendingText, l);
        }
        if ((this.documentsPathsArray != null) || (this.documentsUrisArray != null)) {
          SendMessagesHelper.prepareSendingDocuments(this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, this.documentsMimeType, l, null, null);
        }
        if ((this.contactsToSend == null) || (this.contactsToSend.isEmpty())) {
          break label426;
        }
        paramArrayList = this.contactsToSend.iterator();
        while (paramArrayList.hasNext())
        {
          paramDialogsActivity = (TLRPC.User)paramArrayList.next();
          SendMessagesHelper.getInstance(k).sendMessage(paramDialogsActivity, l, null, null, null);
        }
        paramBoolean = false;
        break;
      }
      label426:
      this.photoPathsArray = null;
      this.videoPath = null;
      this.sendingText = null;
      this.documentsPathsArray = null;
      this.documentsOriginalPathsArray = null;
      this.contactsToSend = null;
    }
  }
  
  public ActionBarLayout getActionBarLayout()
  {
    return this.actionBarLayout;
  }
  
  public ActionBarLayout getLayersActionBarLayout()
  {
    return this.layersActionBarLayout;
  }
  
  public int getMainFragmentsCount()
  {
    return mainFragmentsStack.size();
  }
  
  public ActionBarLayout getRightActionBarLayout()
  {
    return this.rightActionBarLayout;
  }
  
  public boolean needAddFragmentToStack(BaseFragment paramBaseFragment, ActionBarLayout paramActionBarLayout)
  {
    boolean bool1 = false;
    boolean bool2;
    if (AndroidUtilities.isTablet())
    {
      DrawerLayoutContainer localDrawerLayoutContainer = this.drawerLayoutContainer;
      if ((!(paramBaseFragment instanceof LoginActivity)) && (!(paramBaseFragment instanceof CountrySelectActivity)) && (this.layersActionBarLayout.getVisibility() != 0))
      {
        bool2 = true;
        localDrawerLayoutContainer.setAllowOpenDrawer(bool2, true);
        if (!(paramBaseFragment instanceof DialogsActivity)) {
          break label169;
        }
        if ((!((DialogsActivity)paramBaseFragment).isMainDialogList()) || (paramActionBarLayout == this.actionBarLayout)) {
          break label493;
        }
        this.actionBarLayout.removeAllFragments();
        this.actionBarLayout.addFragmentToStack(paramBaseFragment);
        this.layersActionBarLayout.removeAllFragments();
        this.layersActionBarLayout.setVisibility(8);
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        bool2 = bool1;
        if (!this.tabletFullSize)
        {
          this.shadowTabletSide.setVisibility(0);
          bool2 = bool1;
          if (this.rightActionBarLayout.fragmentsStack.isEmpty())
          {
            this.backgroundTablet.setVisibility(0);
            bool2 = bool1;
          }
        }
      }
      for (;;)
      {
        return bool2;
        bool2 = false;
        break;
        label169:
        if ((paramBaseFragment instanceof ChatActivity))
        {
          int i;
          if ((!this.tabletFullSize) && (paramActionBarLayout != this.rightActionBarLayout))
          {
            this.rightActionBarLayout.setVisibility(0);
            this.backgroundTablet.setVisibility(8);
            this.rightActionBarLayout.removeAllFragments();
            this.rightActionBarLayout.addFragmentToStack(paramBaseFragment);
            bool2 = bool1;
            if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
              continue;
            }
            for (i = 0; this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
              this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
            }
            this.layersActionBarLayout.closeLastFragment(true);
            bool2 = bool1;
            continue;
          }
          if ((this.tabletFullSize) && (paramActionBarLayout != this.actionBarLayout))
          {
            this.actionBarLayout.addFragmentToStack(paramBaseFragment);
            bool2 = bool1;
            if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
              continue;
            }
            for (i = 0; this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
              this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
            }
            this.layersActionBarLayout.closeLastFragment(true);
            bool2 = bool1;
          }
        }
        else if (paramActionBarLayout != this.layersActionBarLayout)
        {
          this.layersActionBarLayout.setVisibility(0);
          this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
          if ((paramBaseFragment instanceof LoginActivity))
          {
            this.backgroundTablet.setVisibility(0);
            this.shadowTabletSide.setVisibility(8);
            this.shadowTablet.setBackgroundColor(0);
          }
          for (;;)
          {
            this.layersActionBarLayout.addFragmentToStack(paramBaseFragment);
            bool2 = bool1;
            break;
            this.shadowTablet.setBackgroundColor(NUM);
          }
        }
        label493:
        bool2 = true;
      }
    }
    bool1 = true;
    if ((paramBaseFragment instanceof LoginActivity))
    {
      bool2 = bool1;
      if (mainFragmentsStack.size() == 0) {
        bool2 = false;
      }
    }
    for (;;)
    {
      this.drawerLayoutContainer.setAllowOpenDrawer(bool2, false);
      bool2 = true;
      break;
      bool2 = bool1;
      if ((paramBaseFragment instanceof CountrySelectActivity))
      {
        bool2 = bool1;
        if (mainFragmentsStack.size() == 1) {
          bool2 = false;
        }
      }
    }
  }
  
  public boolean needCloseLastFragment(ActionBarLayout paramActionBarLayout)
  {
    boolean bool = false;
    if (AndroidUtilities.isTablet())
    {
      if ((paramActionBarLayout == this.actionBarLayout) && (paramActionBarLayout.fragmentsStack.size() <= 1))
      {
        onFinish();
        finish();
        return bool;
      }
      if (paramActionBarLayout == this.rightActionBarLayout) {
        if (!this.tabletFullSize) {
          this.backgroundTablet.setVisibility(0);
        }
      }
    }
    for (;;)
    {
      bool = true;
      break;
      if ((paramActionBarLayout == this.layersActionBarLayout) && (this.actionBarLayout.fragmentsStack.isEmpty()) && (this.layersActionBarLayout.fragmentsStack.size() == 1))
      {
        onFinish();
        finish();
        break;
        if (paramActionBarLayout.fragmentsStack.size() <= 1)
        {
          onFinish();
          finish();
          break;
        }
        if ((paramActionBarLayout.fragmentsStack.size() >= 2) && (!(paramActionBarLayout.fragmentsStack.get(0) instanceof LoginActivity))) {
          this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        }
      }
    }
  }
  
  public boolean needPresentFragment(BaseFragment paramBaseFragment, boolean paramBoolean1, boolean paramBoolean2, ActionBarLayout paramActionBarLayout)
  {
    boolean bool1 = true;
    boolean bool2 = true;
    boolean bool3 = true;
    boolean bool4 = true;
    boolean bool5 = false;
    if ((ArticleViewer.hasInstance()) && (ArticleViewer.getInstance().isVisible())) {
      ArticleViewer.getInstance().close(false, true);
    }
    if (AndroidUtilities.isTablet())
    {
      DrawerLayoutContainer localDrawerLayoutContainer = this.drawerLayoutContainer;
      boolean bool6;
      if ((!(paramBaseFragment instanceof LoginActivity)) && (!(paramBaseFragment instanceof CountrySelectActivity)) && (this.layersActionBarLayout.getVisibility() != 0))
      {
        bool6 = true;
        localDrawerLayoutContainer.setAllowOpenDrawer(bool6, true);
        if ((!(paramBaseFragment instanceof DialogsActivity)) || (!((DialogsActivity)paramBaseFragment).isMainDialogList()) || (paramActionBarLayout == this.actionBarLayout)) {
          break label208;
        }
        this.actionBarLayout.removeAllFragments();
        this.actionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, false);
        this.layersActionBarLayout.removeAllFragments();
        this.layersActionBarLayout.setVisibility(8);
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        paramBoolean1 = bool5;
        if (!this.tabletFullSize)
        {
          this.shadowTabletSide.setVisibility(0);
          paramBoolean1 = bool5;
          if (this.rightActionBarLayout.fragmentsStack.isEmpty())
          {
            this.backgroundTablet.setVisibility(0);
            paramBoolean1 = bool5;
          }
        }
      }
      for (;;)
      {
        return paramBoolean1;
        bool6 = false;
        break;
        label208:
        if ((paramBaseFragment instanceof ChatActivity))
        {
          int i;
          if (((!this.tabletFullSize) && (paramActionBarLayout == this.rightActionBarLayout)) || ((this.tabletFullSize) && (paramActionBarLayout == this.actionBarLayout)))
          {
            if ((!this.tabletFullSize) || (paramActionBarLayout != this.actionBarLayout) || (this.actionBarLayout.fragmentsStack.size() != 1)) {
              paramBoolean1 = true;
            }
            while (!this.layersActionBarLayout.fragmentsStack.isEmpty())
            {
              i = 0;
              for (;;)
              {
                if (this.layersActionBarLayout.fragmentsStack.size() - 1 > 0)
                {
                  this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
                  i = i - 1 + 1;
                  continue;
                  paramBoolean1 = false;
                  break;
                }
              }
              paramActionBarLayout = this.layersActionBarLayout;
              if (paramBoolean2) {
                break label387;
              }
            }
            label387:
            for (bool6 = bool4;; bool6 = false)
            {
              paramActionBarLayout.closeLastFragment(bool6);
              if (!paramBoolean1) {
                this.actionBarLayout.presentFragment(paramBaseFragment, false, paramBoolean2, false);
              }
              break;
            }
          }
          if ((!this.tabletFullSize) && (paramActionBarLayout != this.rightActionBarLayout))
          {
            this.rightActionBarLayout.setVisibility(0);
            this.backgroundTablet.setVisibility(8);
            this.rightActionBarLayout.removeAllFragments();
            this.rightActionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, true, false);
            paramBoolean1 = bool5;
            if (!this.layersActionBarLayout.fragmentsStack.isEmpty())
            {
              for (i = 0; this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
                this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
              }
              paramBaseFragment = this.layersActionBarLayout;
              if (!paramBoolean2) {}
              for (paramBoolean1 = bool1;; paramBoolean1 = false)
              {
                paramBaseFragment.closeLastFragment(paramBoolean1);
                paramBoolean1 = bool5;
                break;
              }
            }
          }
          else
          {
            if ((this.tabletFullSize) && (paramActionBarLayout != this.actionBarLayout))
            {
              paramActionBarLayout = this.actionBarLayout;
              if (this.actionBarLayout.fragmentsStack.size() > 1) {}
              for (paramBoolean1 = true;; paramBoolean1 = false)
              {
                paramActionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, false);
                paramBoolean1 = bool5;
                if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                  break;
                }
                for (i = 0; this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
                  this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
                }
              }
              paramBaseFragment = this.layersActionBarLayout;
              if (!paramBoolean2) {}
              for (paramBoolean1 = bool2;; paramBoolean1 = false)
              {
                paramBaseFragment.closeLastFragment(paramBoolean1);
                paramBoolean1 = bool5;
                break;
              }
            }
            if (!this.layersActionBarLayout.fragmentsStack.isEmpty())
            {
              for (i = 0; this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
                this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
              }
              paramActionBarLayout = this.layersActionBarLayout;
              if (!paramBoolean2)
              {
                paramBoolean1 = true;
                label761:
                paramActionBarLayout.closeLastFragment(paramBoolean1);
              }
            }
            else
            {
              paramActionBarLayout = this.actionBarLayout;
              if (this.actionBarLayout.fragmentsStack.size() <= 1) {
                break label811;
              }
            }
            label811:
            for (paramBoolean1 = bool3;; paramBoolean1 = false)
            {
              paramActionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, false);
              paramBoolean1 = bool5;
              break;
              paramBoolean1 = false;
              break label761;
            }
          }
        }
        else
        {
          if (paramActionBarLayout != this.layersActionBarLayout)
          {
            this.layersActionBarLayout.setVisibility(0);
            this.drawerLayoutContainer.setAllowOpenDrawer(false, true);
            if ((paramBaseFragment instanceof LoginActivity))
            {
              this.backgroundTablet.setVisibility(0);
              this.shadowTabletSide.setVisibility(8);
              this.shadowTablet.setBackgroundColor(0);
            }
            for (;;)
            {
              this.layersActionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, false);
              paramBoolean1 = bool5;
              break;
              this.shadowTablet.setBackgroundColor(NUM);
            }
          }
          paramBoolean1 = true;
        }
      }
    }
    paramBoolean2 = true;
    if ((paramBaseFragment instanceof LoginActivity))
    {
      paramBoolean1 = paramBoolean2;
      if (mainFragmentsStack.size() == 0) {
        paramBoolean1 = false;
      }
    }
    for (;;)
    {
      this.drawerLayoutContainer.setAllowOpenDrawer(paramBoolean1, false);
      paramBoolean1 = true;
      break;
      paramBoolean1 = paramBoolean2;
      if ((paramBaseFragment instanceof CountrySelectActivity))
      {
        paramBoolean1 = paramBoolean2;
        if (mainFragmentsStack.size() == 1) {
          paramBoolean1 = false;
        }
      }
    }
  }
  
  public void onActionModeFinished(ActionMode paramActionMode)
  {
    super.onActionModeFinished(paramActionMode);
    if ((Build.VERSION.SDK_INT >= 23) && (paramActionMode.getType() == 1)) {}
    for (;;)
    {
      return;
      this.actionBarLayout.onActionModeFinished(paramActionMode);
      if (AndroidUtilities.isTablet())
      {
        this.rightActionBarLayout.onActionModeFinished(paramActionMode);
        this.layersActionBarLayout.onActionModeFinished(paramActionMode);
      }
    }
  }
  
  public void onActionModeStarted(ActionMode paramActionMode)
  {
    super.onActionModeStarted(paramActionMode);
    try
    {
      Menu localMenu = paramActionMode.getMenu();
      if ((localMenu != null) && (!this.actionBarLayout.extendActionMode(localMenu)) && (AndroidUtilities.isTablet()) && (!this.rightActionBarLayout.extendActionMode(localMenu))) {
        this.layersActionBarLayout.extendActionMode(localMenu);
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
        continue;
        this.actionBarLayout.onActionModeStarted(paramActionMode);
        if (AndroidUtilities.isTablet())
        {
          this.rightActionBarLayout.onActionModeStarted(paramActionMode);
          this.layersActionBarLayout.onActionModeStarted(paramActionMode);
        }
      }
    }
    if ((Build.VERSION.SDK_INT >= 23) && (paramActionMode.getType() == 1)) {
      return;
    }
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if ((SharedConfig.passcodeHash.length() != 0) && (SharedConfig.lastPauseTime != 0))
    {
      SharedConfig.lastPauseTime = 0;
      UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    ThemeEditorView localThemeEditorView = ThemeEditorView.getInstance();
    if (localThemeEditorView != null) {
      localThemeEditorView.onActivityResult(paramInt1, paramInt2, paramIntent);
    }
    if (this.actionBarLayout.fragmentsStack.size() != 0) {
      ((BaseFragment)this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(paramInt1, paramInt2, paramIntent);
    }
    if (AndroidUtilities.isTablet())
    {
      if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
        ((BaseFragment)this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(paramInt1, paramInt2, paramIntent);
      }
      if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
        ((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onActivityResultFragment(paramInt1, paramInt2, paramIntent);
      }
    }
  }
  
  public void onBackPressed()
  {
    if (this.passcodeView.getVisibility() == 0) {
      finish();
    }
    for (;;)
    {
      return;
      if ((SecretMediaViewer.hasInstance()) && (SecretMediaViewer.getInstance().isVisible())) {
        SecretMediaViewer.getInstance().closePhoto(true, false);
      } else if ((PhotoViewer.hasInstance()) && (PhotoViewer.getInstance().isVisible())) {
        PhotoViewer.getInstance().closePhoto(true, false);
      } else if ((ArticleViewer.hasInstance()) && (ArticleViewer.getInstance().isVisible())) {
        ArticleViewer.getInstance().close(true, false);
      } else if (this.drawerLayoutContainer.isDrawerOpened()) {
        this.drawerLayoutContainer.closeDrawer(false);
      } else if (AndroidUtilities.isTablet())
      {
        if (this.layersActionBarLayout.getVisibility() == 0)
        {
          this.layersActionBarLayout.onBackPressed();
        }
        else
        {
          int i = 0;
          int j = i;
          if (this.rightActionBarLayout.getVisibility() == 0)
          {
            j = i;
            if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
              if (((BaseFragment)this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onBackPressed()) {
                break label216;
              }
            }
          }
          label216:
          for (j = 1;; j = 0)
          {
            if (j != 0) {
              break label219;
            }
            this.actionBarLayout.onBackPressed();
            break;
          }
        }
      }
      else {
        label219:
        this.actionBarLayout.onBackPressed();
      }
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    AndroidUtilities.checkDisplaySize(this, paramConfiguration);
    super.onConfigurationChanged(paramConfiguration);
    checkLayout();
    Object localObject = PipRoundVideoView.getInstance();
    if (localObject != null) {
      ((PipRoundVideoView)localObject).onConfigurationChanged();
    }
    localObject = EmbedBottomSheet.getInstance();
    if (localObject != null) {
      ((EmbedBottomSheet)localObject).onConfigurationChanged(paramConfiguration);
    }
    localObject = PhotoViewer.getPipInstance();
    if (localObject != null) {
      ((PhotoViewer)localObject).onConfigurationChanged(paramConfiguration);
    }
    paramConfiguration = ThemeEditorView.getInstance();
    if (paramConfiguration != null) {
      paramConfiguration.onConfigurationChanged();
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    ApplicationLoader.postInitApplication();
    AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
    this.currentAccount = UserConfig.selectedAccount;
    Object localObject1;
    if (!UserConfig.getInstance(this.currentAccount).isClientActivated())
    {
      localObject1 = getIntent();
      if ((localObject1 != null) && (((Intent)localObject1).getAction() != null) && (("android.intent.action.SEND".equals(((Intent)localObject1).getAction())) || (((Intent)localObject1).getAction().equals("android.intent.action.SEND_MULTIPLE"))))
      {
        super.onCreate(paramBundle);
        finish();
      }
    }
    for (;;)
    {
      return;
      Object localObject3 = MessagesController.getGlobalMainSettings();
      long l = ((SharedPreferences)localObject3).getLong("intro_crashed_time", 0L);
      boolean bool1 = ((Intent)localObject1).getBooleanExtra("fromIntro", false);
      if (bool1) {
        ((SharedPreferences)localObject3).edit().putLong("intro_crashed_time", 0L).commit();
      }
      if ((Math.abs(l - System.currentTimeMillis()) >= 120000L) && (localObject1 != null) && (!bool1) && (ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).getAll().isEmpty()))
      {
        localObject3 = new Intent(this, IntroActivity.class);
        ((Intent)localObject3).setData(((Intent)localObject1).getData());
        startActivity((Intent)localObject3);
        super.onCreate(paramBundle);
        finish();
        continue;
      }
      requestWindowFeature(1);
      setTheme(NUM);
      if (Build.VERSION.SDK_INT >= 21) {}
      try
      {
        localObject1 = new android/app/ActivityManager$TaskDescription;
        ((ActivityManager.TaskDescription)localObject1).<init>(null, null, Theme.getColor("actionBarDefault") | 0xFF000000);
        setTaskDescription((ActivityManager.TaskDescription)localObject1);
        getWindow().setBackgroundDrawableResource(NUM);
        if ((SharedConfig.passcodeHash.length() > 0) && (!SharedConfig.allowScreenCapture)) {}
        try
        {
          getWindow().setFlags(8192, 8192);
          super.onCreate(paramBundle);
          if (Build.VERSION.SDK_INT >= 24) {
            AndroidUtilities.isInMultiwindow = isInMultiWindowMode();
          }
          Theme.createChatResources(this, false);
          if ((SharedConfig.passcodeHash.length() != 0) && (SharedConfig.appLocked)) {
            SharedConfig.lastPauseTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
          }
          i = getResources().getIdentifier("status_bar_height", "dimen", "android");
          if (i > 0) {
            AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(i);
          }
          this.actionBarLayout = new ActionBarLayout(this);
          this.drawerLayoutContainer = new DrawerLayoutContainer(this);
          setContentView(this.drawerLayoutContainer, new ViewGroup.LayoutParams(-1, -1));
          if (AndroidUtilities.isTablet())
          {
            getWindow().setSoftInputMode(16);
            localObject1 = new RelativeLayout(this)
            {
              private boolean inLayout;
              
              protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
              {
                int i = paramAnonymousInt3 - paramAnonymousInt1;
                if ((!AndroidUtilities.isInMultiwindow) && ((!AndroidUtilities.isSmallTablet()) || (getResources().getConfiguration().orientation == 2)))
                {
                  paramAnonymousInt3 = i / 100 * 35;
                  paramAnonymousInt1 = paramAnonymousInt3;
                  if (paramAnonymousInt3 < AndroidUtilities.dp(320.0F)) {
                    paramAnonymousInt1 = AndroidUtilities.dp(320.0F);
                  }
                  LaunchActivity.this.shadowTabletSide.layout(paramAnonymousInt1, 0, LaunchActivity.this.shadowTabletSide.getMeasuredWidth() + paramAnonymousInt1, LaunchActivity.this.shadowTabletSide.getMeasuredHeight());
                  LaunchActivity.this.actionBarLayout.layout(0, 0, LaunchActivity.this.actionBarLayout.getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getMeasuredHeight());
                  LaunchActivity.this.rightActionBarLayout.layout(paramAnonymousInt1, 0, LaunchActivity.this.rightActionBarLayout.getMeasuredWidth() + paramAnonymousInt1, LaunchActivity.this.rightActionBarLayout.getMeasuredHeight());
                }
                for (;;)
                {
                  paramAnonymousInt1 = (i - LaunchActivity.this.layersActionBarLayout.getMeasuredWidth()) / 2;
                  paramAnonymousInt2 = (paramAnonymousInt4 - paramAnonymousInt2 - LaunchActivity.this.layersActionBarLayout.getMeasuredHeight()) / 2;
                  LaunchActivity.this.layersActionBarLayout.layout(paramAnonymousInt1, paramAnonymousInt2, LaunchActivity.this.layersActionBarLayout.getMeasuredWidth() + paramAnonymousInt1, LaunchActivity.this.layersActionBarLayout.getMeasuredHeight() + paramAnonymousInt2);
                  LaunchActivity.this.backgroundTablet.layout(0, 0, LaunchActivity.this.backgroundTablet.getMeasuredWidth(), LaunchActivity.this.backgroundTablet.getMeasuredHeight());
                  LaunchActivity.this.shadowTablet.layout(0, 0, LaunchActivity.this.shadowTablet.getMeasuredWidth(), LaunchActivity.this.shadowTablet.getMeasuredHeight());
                  return;
                  LaunchActivity.this.actionBarLayout.layout(0, 0, LaunchActivity.this.actionBarLayout.getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getMeasuredHeight());
                }
              }
              
              protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
              {
                this.inLayout = true;
                int i = View.MeasureSpec.getSize(paramAnonymousInt1);
                int j = View.MeasureSpec.getSize(paramAnonymousInt2);
                setMeasuredDimension(i, j);
                if ((!AndroidUtilities.isInMultiwindow) && ((!AndroidUtilities.isSmallTablet()) || (getResources().getConfiguration().orientation == 2)))
                {
                  LaunchActivity.access$002(LaunchActivity.this, false);
                  paramAnonymousInt2 = i / 100 * 35;
                  paramAnonymousInt1 = paramAnonymousInt2;
                  if (paramAnonymousInt2 < AndroidUtilities.dp(320.0F)) {
                    paramAnonymousInt1 = AndroidUtilities.dp(320.0F);
                  }
                  LaunchActivity.this.actionBarLayout.measure(View.MeasureSpec.makeMeasureSpec(paramAnonymousInt1, NUM), View.MeasureSpec.makeMeasureSpec(j, NUM));
                  LaunchActivity.this.shadowTabletSide.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1.0F), NUM), View.MeasureSpec.makeMeasureSpec(j, NUM));
                  LaunchActivity.this.rightActionBarLayout.measure(View.MeasureSpec.makeMeasureSpec(i - paramAnonymousInt1, NUM), View.MeasureSpec.makeMeasureSpec(j, NUM));
                }
                for (;;)
                {
                  LaunchActivity.this.backgroundTablet.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(j, NUM));
                  LaunchActivity.this.shadowTablet.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(j, NUM));
                  LaunchActivity.this.layersActionBarLayout.measure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(530.0F), i), NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(528.0F), j), NUM));
                  this.inLayout = false;
                  return;
                  LaunchActivity.access$002(LaunchActivity.this, true);
                  LaunchActivity.this.actionBarLayout.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(j, NUM));
                }
              }
              
              public void requestLayout()
              {
                if (this.inLayout) {}
                for (;;)
                {
                  return;
                  super.requestLayout();
                }
              }
            };
            this.drawerLayoutContainer.addView((View)localObject1, LayoutHelper.createFrame(-1, -1.0F));
            this.backgroundTablet = new View(this);
            localObject3 = (BitmapDrawable)getResources().getDrawable(NUM);
            ((BitmapDrawable)localObject3).setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            this.backgroundTablet.setBackgroundDrawable((Drawable)localObject3);
            ((RelativeLayout)localObject1).addView(this.backgroundTablet, LayoutHelper.createRelative(-1, -1));
            ((RelativeLayout)localObject1).addView(this.actionBarLayout);
            this.rightActionBarLayout = new ActionBarLayout(this);
            this.rightActionBarLayout.init(rightFragmentsStack);
            this.rightActionBarLayout.setDelegate(this);
            ((RelativeLayout)localObject1).addView(this.rightActionBarLayout);
            this.shadowTabletSide = new FrameLayout(this);
            this.shadowTabletSide.setBackgroundColor(NUM);
            ((RelativeLayout)localObject1).addView(this.shadowTabletSide);
            this.shadowTablet = new FrameLayout(this);
            localObject3 = this.shadowTablet;
            if (layerFragmentsStack.isEmpty())
            {
              i = 8;
              ((FrameLayout)localObject3).setVisibility(i);
              this.shadowTablet.setBackgroundColor(NUM);
              ((RelativeLayout)localObject1).addView(this.shadowTablet);
              this.shadowTablet.setOnTouchListener(new View.OnTouchListener()
              {
                public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
                {
                  int i;
                  boolean bool;
                  if ((!LaunchActivity.this.actionBarLayout.fragmentsStack.isEmpty()) && (paramAnonymousMotionEvent.getAction() == 1))
                  {
                    float f1 = paramAnonymousMotionEvent.getX();
                    float f2 = paramAnonymousMotionEvent.getY();
                    paramAnonymousView = new int[2];
                    LaunchActivity.this.layersActionBarLayout.getLocationOnScreen(paramAnonymousView);
                    i = paramAnonymousView[0];
                    int j = paramAnonymousView[1];
                    if ((LaunchActivity.this.layersActionBarLayout.checkTransitionAnimation()) || ((f1 > i) && (f1 < LaunchActivity.this.layersActionBarLayout.getWidth() + i) && (f2 > j) && (f2 < LaunchActivity.this.layersActionBarLayout.getHeight() + j))) {
                      bool = false;
                    }
                  }
                  for (;;)
                  {
                    return bool;
                    if (!LaunchActivity.this.layersActionBarLayout.fragmentsStack.isEmpty())
                    {
                      for (i = 0; LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
                        LaunchActivity.this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)LaunchActivity.this.layersActionBarLayout.fragmentsStack.get(0));
                      }
                      LaunchActivity.this.layersActionBarLayout.closeLastFragment(true);
                    }
                    bool = true;
                    continue;
                    bool = false;
                  }
                }
              });
              this.shadowTablet.setOnClickListener(new View.OnClickListener()
              {
                public void onClick(View paramAnonymousView) {}
              });
              this.layersActionBarLayout = new ActionBarLayout(this);
              this.layersActionBarLayout.setRemoveActionBarExtraHeight(true);
              this.layersActionBarLayout.setBackgroundView(this.shadowTablet);
              this.layersActionBarLayout.setUseAlphaAnimations(true);
              this.layersActionBarLayout.setBackgroundResource(NUM);
              this.layersActionBarLayout.init(layerFragmentsStack);
              this.layersActionBarLayout.setDelegate(this);
              this.layersActionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
              localObject3 = this.layersActionBarLayout;
              if (!layerFragmentsStack.isEmpty()) {
                break label1477;
              }
              i = 8;
              ((ActionBarLayout)localObject3).setVisibility(i);
              ((RelativeLayout)localObject1).addView(this.layersActionBarLayout);
              this.sideMenu = new RecyclerListView(this);
              ((DefaultItemAnimator)this.sideMenu.getItemAnimator()).setDelayAnimations(false);
              this.sideMenu.setBackgroundColor(Theme.getColor("chats_menuBackground"));
              this.sideMenu.setLayoutManager(new LinearLayoutManager(this, 1, false));
              localObject3 = this.sideMenu;
              localObject1 = new DrawerLayoutAdapter(this);
              this.drawerLayoutAdapter = ((DrawerLayoutAdapter)localObject1);
              ((RecyclerListView)localObject3).setAdapter((RecyclerView.Adapter)localObject1);
              this.drawerLayoutContainer.setDrawerLayout(this.sideMenu);
              localObject3 = (FrameLayout.LayoutParams)this.sideMenu.getLayoutParams();
              localObject1 = AndroidUtilities.getRealScreenSize();
              if (!AndroidUtilities.isTablet()) {
                break label1506;
              }
              i = AndroidUtilities.dp(320.0F);
              ((FrameLayout.LayoutParams)localObject3).width = i;
              ((FrameLayout.LayoutParams)localObject3).height = -1;
              this.sideMenu.setLayoutParams((ViewGroup.LayoutParams)localObject3);
              this.sideMenu.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
              {
                public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
                {
                  boolean bool = false;
                  if (paramAnonymousInt == 0)
                  {
                    paramAnonymousView = LaunchActivity.this.drawerLayoutAdapter;
                    if (!LaunchActivity.this.drawerLayoutAdapter.isAccountsShowed()) {
                      bool = true;
                    }
                    paramAnonymousView.setAccountsShowed(bool, true);
                  }
                  for (;;)
                  {
                    return;
                    if ((paramAnonymousView instanceof DrawerUserCell))
                    {
                      LaunchActivity.this.switchToAccount(((DrawerUserCell)paramAnonymousView).getAccountNumber(), true);
                      LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                    }
                    else
                    {
                      if ((paramAnonymousView instanceof DrawerAddCell))
                      {
                        int i = -1;
                        for (paramAnonymousInt = 0;; paramAnonymousInt++)
                        {
                          int j = i;
                          if (paramAnonymousInt < 3)
                          {
                            if (!UserConfig.getInstance(paramAnonymousInt).isClientActivated()) {
                              j = paramAnonymousInt;
                            }
                          }
                          else
                          {
                            if (j >= 0) {
                              LaunchActivity.this.presentFragment(new LoginActivity(j));
                            }
                            LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                            break;
                          }
                        }
                      }
                      paramAnonymousInt = LaunchActivity.this.drawerLayoutAdapter.getId(paramAnonymousInt);
                      if (paramAnonymousInt == 2)
                      {
                        LaunchActivity.this.presentFragment(new GroupCreateActivity());
                        LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                      }
                      else if (paramAnonymousInt == 3)
                      {
                        paramAnonymousView = new Bundle();
                        paramAnonymousView.putBoolean("onlyUsers", true);
                        paramAnonymousView.putBoolean("destroyAfterSelect", true);
                        paramAnonymousView.putBoolean("createSecretChat", true);
                        paramAnonymousView.putBoolean("allowBots", false);
                        LaunchActivity.this.presentFragment(new ContactsActivity(paramAnonymousView));
                        LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                      }
                      else
                      {
                        if (paramAnonymousInt == 4)
                        {
                          paramAnonymousView = MessagesController.getGlobalMainSettings();
                          if ((!BuildVars.DEBUG_VERSION) && (paramAnonymousView.getBoolean("channel_intro", false)))
                          {
                            paramAnonymousView = new Bundle();
                            paramAnonymousView.putInt("step", 0);
                            LaunchActivity.this.presentFragment(new ChannelCreateActivity(paramAnonymousView));
                          }
                          for (;;)
                          {
                            LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                            break;
                            LaunchActivity.this.presentFragment(new ChannelIntroActivity());
                            paramAnonymousView.edit().putBoolean("channel_intro", true).commit();
                          }
                        }
                        if (paramAnonymousInt == 6)
                        {
                          LaunchActivity.this.presentFragment(new ContactsActivity(null));
                          LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                        }
                        else if (paramAnonymousInt == 7)
                        {
                          LaunchActivity.this.presentFragment(new InviteContactsActivity());
                          LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                        }
                        else if (paramAnonymousInt == 8)
                        {
                          LaunchActivity.this.presentFragment(new SettingsActivity());
                          LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                        }
                        else if (paramAnonymousInt == 9)
                        {
                          Browser.openUrl(LaunchActivity.this, LocaleController.getString("TelegramFaqUrl", NUM));
                          LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                        }
                        else if (paramAnonymousInt == 10)
                        {
                          LaunchActivity.this.presentFragment(new CallLogActivity());
                          LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                        }
                        else if (paramAnonymousInt == 11)
                        {
                          paramAnonymousView = new Bundle();
                          paramAnonymousView.putInt("user_id", UserConfig.getInstance(LaunchActivity.this.currentAccount).getClientUserId());
                          LaunchActivity.this.presentFragment(new ChatActivity(paramAnonymousView));
                          LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                        }
                      }
                    }
                  }
                }
              });
              this.drawerLayoutContainer.setParentActionBarLayout(this.actionBarLayout);
              this.actionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
              this.actionBarLayout.init(mainFragmentsStack);
              this.actionBarLayout.setDelegate(this);
              Theme.loadWallpaper();
              this.passcodeView = new PasscodeView(this);
              this.drawerLayoutContainer.addView(this.passcodeView, LayoutHelper.createFrame(-1, -1.0F));
              checkCurrentAccount();
              NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeOtherAppActivities, new Object[] { this });
              this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
              NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.reloadInterface);
              NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
              NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
              NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needSetDayNightTheme);
              NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeOtherAppActivities);
              NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
              NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
              NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.notificationsCountUpdated);
              if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
                break label1907;
              }
              if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
                break label1538;
              }
              this.actionBarLayout.addFragmentToStack(new LoginActivity());
              this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
              if (paramBundle == null) {}
            }
          }
          try
          {
            localObject3 = paramBundle.getString("fragment");
            if (localObject3 != null)
            {
              localObject1 = paramBundle.getBundle("args");
              i = -1;
              int j = ((String)localObject3).hashCode();
              switch (j)
              {
              default: 
                switch (i)
                {
                }
                break;
              }
            }
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              FileLog.e(localException2);
              continue;
              localObject2 = new org/telegram/ui/SettingsActivity;
              ((SettingsActivity)localObject2).<init>();
              this.actionBarLayout.addFragmentToStack((BaseFragment)localObject2);
              ((SettingsActivity)localObject2).restoreSelfArgs(paramBundle);
              continue;
              if (localObject2 != null)
              {
                localObject3 = new org/telegram/ui/GroupCreateFinalActivity;
                ((GroupCreateFinalActivity)localObject3).<init>((Bundle)localObject2);
                if (this.actionBarLayout.addFragmentToStack((BaseFragment)localObject3))
                {
                  ((GroupCreateFinalActivity)localObject3).restoreSelfArgs(paramBundle);
                  continue;
                  if (localObject2 != null)
                  {
                    localObject3 = new org/telegram/ui/ChannelCreateActivity;
                    ((ChannelCreateActivity)localObject3).<init>((Bundle)localObject2);
                    if (this.actionBarLayout.addFragmentToStack((BaseFragment)localObject3))
                    {
                      ((ChannelCreateActivity)localObject3).restoreSelfArgs(paramBundle);
                      continue;
                      if (localObject2 != null)
                      {
                        localObject3 = new org/telegram/ui/ChannelEditActivity;
                        ((ChannelEditActivity)localObject3).<init>((Bundle)localObject2);
                        if (this.actionBarLayout.addFragmentToStack((BaseFragment)localObject3))
                        {
                          ((ChannelEditActivity)localObject3).restoreSelfArgs(paramBundle);
                          continue;
                          if (localObject2 != null)
                          {
                            localObject3 = new org/telegram/ui/ProfileActivity;
                            ((ProfileActivity)localObject3).<init>((Bundle)localObject2);
                            if (this.actionBarLayout.addFragmentToStack((BaseFragment)localObject3))
                            {
                              ((ProfileActivity)localObject3).restoreSelfArgs(paramBundle);
                              continue;
                              localObject2 = new org/telegram/ui/WallpapersActivity;
                              ((WallpapersActivity)localObject2).<init>();
                              this.actionBarLayout.addFragmentToStack((BaseFragment)localObject2);
                              ((WallpapersActivity)localObject2).restoreSelfArgs(paramBundle);
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
          checkLayout();
          localObject1 = getIntent();
          if (paramBundle != null)
          {
            bool1 = true;
            handleIntent((Intent)localObject1, false, bool1, false);
          }
          try
          {
            paramBundle = Build.DISPLAY;
            localObject1 = Build.USER;
            if (paramBundle == null) {
              break label2088;
            }
            paramBundle = paramBundle.toLowerCase();
            if (localObject1 == null) {
              break label2095;
            }
            localObject1 = paramBundle.toLowerCase();
            if ((paramBundle.contains("flyme")) || (((String)localObject1).contains("flyme")))
            {
              AndroidUtilities.incorrectDisplaySizeFix = true;
              localObject3 = getWindow().getDecorView().getRootView();
              paramBundle = ((View)localObject3).getViewTreeObserver();
              localObject1 = new org/telegram/ui/LaunchActivity$5;
              ((5)localObject1).<init>(this, (View)localObject3);
              this.onGlobalLayoutListener = ((ViewTreeObserver.OnGlobalLayoutListener)localObject1);
              paramBundle.addOnGlobalLayoutListener((ViewTreeObserver.OnGlobalLayoutListener)localObject1);
            }
          }
          catch (Exception paramBundle)
          {
            for (;;)
            {
              DialogsActivity localDialogsActivity;
              Object localObject2;
              boolean bool2;
              FileLog.e(paramBundle);
            }
          }
          MediaController.getInstance().setBaseActivity(this, true);
        }
        catch (Exception localException1)
        {
          for (;;)
          {
            FileLog.e(localException1);
            continue;
            int i = 0;
            continue;
            label1477:
            i = 0;
            continue;
            this.drawerLayoutContainer.addView(this.actionBarLayout, new ViewGroup.LayoutParams(-1, -1));
            continue;
            label1506:
            i = Math.min(AndroidUtilities.dp(320.0F), Math.min(localException1.x, localException1.y) - AndroidUtilities.dp(56.0F));
            continue;
            label1538:
            localDialogsActivity = new DialogsActivity(null);
            localDialogsActivity.setSideMenu(this.sideMenu);
            this.actionBarLayout.addFragmentToStack(localDialogsActivity);
            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            continue;
            if (((String)localObject3).equals("chat"))
            {
              i = 0;
              continue;
              if (((String)localObject3).equals("settings"))
              {
                i = 1;
                continue;
                if (((String)localObject3).equals("group"))
                {
                  i = 2;
                  continue;
                  if (((String)localObject3).equals("channel"))
                  {
                    i = 3;
                    continue;
                    if (((String)localObject3).equals("edit"))
                    {
                      i = 4;
                      continue;
                      if (((String)localObject3).equals("chat_profile"))
                      {
                        i = 5;
                        continue;
                        if (((String)localObject3).equals("wallpapers"))
                        {
                          i = 6;
                          continue;
                          if (localDialogsActivity != null)
                          {
                            localObject3 = new org/telegram/ui/ChatActivity;
                            ((ChatActivity)localObject3).<init>(localDialogsActivity);
                            if (this.actionBarLayout.addFragmentToStack((BaseFragment)localObject3))
                            {
                              ((ChatActivity)localObject3).restoreSelfArgs(paramBundle);
                              continue;
                              label1907:
                              localObject2 = (BaseFragment)this.actionBarLayout.fragmentsStack.get(0);
                              if ((localObject2 instanceof DialogsActivity)) {
                                ((DialogsActivity)localObject2).setSideMenu(this.sideMenu);
                              }
                              bool1 = true;
                              if (AndroidUtilities.isTablet()) {
                                if ((this.actionBarLayout.fragmentsStack.size() > 1) || (!this.layersActionBarLayout.fragmentsStack.isEmpty())) {
                                  break label2076;
                                }
                              }
                              label2076:
                              for (bool2 = true;; bool2 = false)
                              {
                                bool1 = bool2;
                                if (this.layersActionBarLayout.fragmentsStack.size() == 1)
                                {
                                  bool1 = bool2;
                                  if ((this.layersActionBarLayout.fragmentsStack.get(0) instanceof LoginActivity)) {
                                    bool1 = false;
                                  }
                                }
                                bool2 = bool1;
                                if (this.actionBarLayout.fragmentsStack.size() == 1)
                                {
                                  bool2 = bool1;
                                  if ((this.actionBarLayout.fragmentsStack.get(0) instanceof LoginActivity)) {
                                    bool2 = false;
                                  }
                                }
                                this.drawerLayoutContainer.setAllowOpenDrawer(bool2, false);
                                break;
                              }
                              bool1 = false;
                              continue;
                              label2088:
                              paramBundle = "";
                              continue;
                              label2095:
                              localObject2 = "";
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
      catch (Exception localException3)
      {
        for (;;) {}
      }
    }
  }
  
  protected void onDestroy()
  {
    if (PhotoViewer.getPipInstance() != null) {
      PhotoViewer.getPipInstance().destroyPhotoViewer();
    }
    if (PhotoViewer.hasInstance()) {
      PhotoViewer.getInstance().destroyPhotoViewer();
    }
    if (SecretMediaViewer.hasInstance()) {
      SecretMediaViewer.getInstance().destroyPhotoViewer();
    }
    if (ArticleViewer.hasInstance()) {
      ArticleViewer.getInstance().destroyArticleViewer();
    }
    if (StickerPreviewViewer.hasInstance()) {
      StickerPreviewViewer.getInstance().destroy();
    }
    Object localObject = PipRoundVideoView.getInstance();
    MediaController.getInstance().setBaseActivity(this, false);
    MediaController.getInstance().setFeedbackView(this.actionBarLayout, false);
    if (localObject != null) {
      ((PipRoundVideoView)localObject).close(false);
    }
    Theme.destroyResources();
    localObject = EmbedBottomSheet.getInstance();
    if (localObject != null) {
      ((EmbedBottomSheet)localObject).destroy();
    }
    localObject = ThemeEditorView.getInstance();
    if (localObject != null) {
      ((ThemeEditorView)localObject).destroy();
    }
    try
    {
      if (this.visibleDialog != null)
      {
        this.visibleDialog.dismiss();
        this.visibleDialog = null;
      }
    }
    catch (Exception localException1)
    {
      try
      {
        for (;;)
        {
          if (this.onGlobalLayoutListener != null) {
            getWindow().getDecorView().getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
          }
          super.onDestroy();
          onFinish();
          return;
          localException1 = localException1;
          FileLog.e(localException1);
        }
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e(localException2);
        }
      }
    }
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    boolean bool;
    if ((paramInt == 82) && (!SharedConfig.isWaitingForPasscodeEnter))
    {
      if ((PhotoViewer.hasInstance()) && (PhotoViewer.getInstance().isVisible())) {}
      for (bool = super.onKeyUp(paramInt, paramKeyEvent);; bool = super.onKeyUp(paramInt, paramKeyEvent))
      {
        return bool;
        if ((!ArticleViewer.hasInstance()) || (!ArticleViewer.getInstance().isVisible())) {
          break;
        }
      }
      if (!AndroidUtilities.isTablet()) {
        break label159;
      }
      if ((this.layersActionBarLayout.getVisibility() != 0) || (this.layersActionBarLayout.fragmentsStack.isEmpty())) {
        break label110;
      }
      this.layersActionBarLayout.onKeyUp(paramInt, paramKeyEvent);
    }
    for (;;)
    {
      bool = super.onKeyUp(paramInt, paramKeyEvent);
      break;
      label110:
      if ((this.rightActionBarLayout.getVisibility() == 0) && (!this.rightActionBarLayout.fragmentsStack.isEmpty()))
      {
        this.rightActionBarLayout.onKeyUp(paramInt, paramKeyEvent);
      }
      else
      {
        this.actionBarLayout.onKeyUp(paramInt, paramKeyEvent);
        continue;
        label159:
        if (this.actionBarLayout.fragmentsStack.size() == 1)
        {
          if (!this.drawerLayoutContainer.isDrawerOpened())
          {
            if (getCurrentFocus() != null) {
              AndroidUtilities.hideKeyboard(getCurrentFocus());
            }
            this.drawerLayoutContainer.openDrawer(false);
          }
          else
          {
            this.drawerLayoutContainer.closeDrawer(false);
          }
        }
        else {
          this.actionBarLayout.onKeyUp(paramInt, paramKeyEvent);
        }
      }
    }
  }
  
  public void onLowMemory()
  {
    super.onLowMemory();
    this.actionBarLayout.onLowMemory();
    if (AndroidUtilities.isTablet())
    {
      this.rightActionBarLayout.onLowMemory();
      this.layersActionBarLayout.onLowMemory();
    }
  }
  
  public void onMultiWindowModeChanged(boolean paramBoolean)
  {
    AndroidUtilities.isInMultiwindow = paramBoolean;
    checkLayout();
  }
  
  protected void onNewIntent(Intent paramIntent)
  {
    super.onNewIntent(paramIntent);
    handleIntent(paramIntent, true, false, false);
  }
  
  protected void onPause()
  {
    super.onPause();
    SharedConfig.lastAppPauseTime = System.currentTimeMillis();
    ApplicationLoader.mainInterfacePaused = true;
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ApplicationLoader.mainInterfacePausedStageQueue = true;
        ApplicationLoader.mainInterfacePausedStageQueueTime = 0L;
      }
    });
    onPasscodePause();
    this.actionBarLayout.onPause();
    if (AndroidUtilities.isTablet())
    {
      this.rightActionBarLayout.onPause();
      this.layersActionBarLayout.onPause();
    }
    if (this.passcodeView != null) {
      this.passcodeView.onPause();
    }
    ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
    AndroidUtilities.unregisterUpdates();
    if ((PhotoViewer.hasInstance()) && (PhotoViewer.getInstance().isVisible())) {
      PhotoViewer.getInstance().onPause();
    }
  }
  
  public boolean onPreIme()
  {
    boolean bool = true;
    if ((SecretMediaViewer.hasInstance()) && (SecretMediaViewer.getInstance().isVisible())) {
      SecretMediaViewer.getInstance().closePhoto(true, false);
    }
    for (;;)
    {
      return bool;
      if ((PhotoViewer.hasInstance()) && (PhotoViewer.getInstance().isVisible())) {
        PhotoViewer.getInstance().closePhoto(true, false);
      } else if ((ArticleViewer.hasInstance()) && (ArticleViewer.getInstance().isVisible())) {
        ArticleViewer.getInstance().close(true, false);
      } else {
        bool = false;
      }
    }
  }
  
  public void onRebuildAllFragments(ActionBarLayout paramActionBarLayout, boolean paramBoolean)
  {
    if ((AndroidUtilities.isTablet()) && (paramActionBarLayout == this.layersActionBarLayout))
    {
      this.rightActionBarLayout.rebuildAllFragmentViews(paramBoolean, paramBoolean);
      this.actionBarLayout.rebuildAllFragmentViews(paramBoolean, paramBoolean);
    }
    this.drawerLayoutAdapter.notifyDataSetChanged();
  }
  
  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    super.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfInt);
    int i;
    int j;
    if ((paramInt == 3) || (paramInt == 4) || (paramInt == 5) || (paramInt == 19) || (paramInt == 20))
    {
      i = 1;
      j = i;
      if (paramArrayOfInt.length > 0)
      {
        j = i;
        if (paramArrayOfInt[0] == 0) {
          if (paramInt == 4) {
            ImageLoader.getInstance().checkMediaPaths();
          }
        }
      }
    }
    for (;;)
    {
      return;
      if (paramInt == 5)
      {
        ContactsController.getInstance(this.currentAccount).forceImportContacts();
      }
      else if (paramInt == 3)
      {
        if (SharedConfig.inappCamera) {
          CameraController.getInstance().initCamera();
        }
      }
      else
      {
        if (paramInt != 19)
        {
          j = i;
          if (paramInt != 20) {}
        }
        else
        {
          j = 0;
        }
        if (j != 0)
        {
          paramArrayOfString = new AlertDialog.Builder(this);
          paramArrayOfString.setTitle(LocaleController.getString("AppName", NUM));
          if (paramInt == 3) {
            paramArrayOfString.setMessage(LocaleController.getString("PermissionNoAudio", NUM));
          }
          for (;;)
          {
            paramArrayOfString.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener()
            {
              @TargetApi(9)
              public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
              {
                try
                {
                  Intent localIntent = new android/content/Intent;
                  localIntent.<init>("android.settings.APPLICATION_DETAILS_SETTINGS");
                  paramAnonymousDialogInterface = new java/lang/StringBuilder;
                  paramAnonymousDialogInterface.<init>();
                  localIntent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                  LaunchActivity.this.startActivity(localIntent);
                  return;
                }
                catch (Exception paramAnonymousDialogInterface)
                {
                  for (;;)
                  {
                    FileLog.e(paramAnonymousDialogInterface);
                  }
                }
              }
            });
            paramArrayOfString.setPositiveButton(LocaleController.getString("OK", NUM), null);
            paramArrayOfString.show();
            break;
            if (paramInt == 4) {
              paramArrayOfString.setMessage(LocaleController.getString("PermissionStorage", NUM));
            } else if (paramInt == 5) {
              paramArrayOfString.setMessage(LocaleController.getString("PermissionContacts", NUM));
            } else if ((paramInt == 19) || (paramInt == 20)) {
              paramArrayOfString.setMessage(LocaleController.getString("PermissionNoCamera", NUM));
            }
          }
          if ((paramInt == 2) && (paramArrayOfInt.length > 0) && (paramArrayOfInt[0] == 0)) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.locationPermissionGranted, new Object[0]);
          }
        }
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
          ((BaseFragment)this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(paramInt, paramArrayOfString, paramArrayOfInt);
        }
        if (AndroidUtilities.isTablet())
        {
          if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
            ((BaseFragment)this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(paramInt, paramArrayOfString, paramArrayOfInt);
          }
          if (this.layersActionBarLayout.fragmentsStack.size() != 0) {
            ((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(paramInt, paramArrayOfString, paramArrayOfInt);
          }
        }
      }
    }
  }
  
  protected void onResume()
  {
    super.onResume();
    MediaController.getInstance().setFeedbackView(this.actionBarLayout, true);
    showLanguageAlert(false);
    ApplicationLoader.mainInterfacePaused = false;
    org.telegram.messenger.NotificationsController.lastNoDataNotificationTime = 0L;
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ApplicationLoader.mainInterfacePausedStageQueue = false;
        ApplicationLoader.mainInterfacePausedStageQueueTime = System.currentTimeMillis();
      }
    });
    checkFreeDiscSpace();
    MediaController.checkGallery();
    onPasscodeResume();
    if (this.passcodeView.getVisibility() != 0)
    {
      this.actionBarLayout.onResume();
      if (AndroidUtilities.isTablet())
      {
        this.rightActionBarLayout.onResume();
        this.layersActionBarLayout.onResume();
      }
    }
    for (;;)
    {
      AndroidUtilities.checkForCrashes(this);
      AndroidUtilities.checkForUpdates(this);
      ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
      updateCurrentConnectionState(this.currentAccount);
      if ((PhotoViewer.hasInstance()) && (PhotoViewer.getInstance().isVisible())) {
        PhotoViewer.getInstance().onResume();
      }
      if ((PipRoundVideoView.getInstance() != null) && (MediaController.getInstance().isMessagePaused()))
      {
        MessageObject localMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if (localMessageObject != null) {
          MediaController.getInstance().seekToProgress(localMessageObject, localMessageObject.audioProgress);
        }
      }
      return;
      this.actionBarLayout.dismissDialogs();
      if (AndroidUtilities.isTablet())
      {
        this.rightActionBarLayout.dismissDialogs();
        this.layersActionBarLayout.dismissDialogs();
      }
      this.passcodeView.onResume();
    }
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    for (;;)
    {
      try
      {
        super.onSaveInstanceState(paramBundle);
        localBaseFragment = null;
        if (!AndroidUtilities.isTablet()) {
          continue;
        }
        if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
          continue;
        }
        localBaseFragment = (BaseFragment)this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1);
        if (localBaseFragment == null) {
          continue;
        }
        localBundle = localBaseFragment.getArguments();
        if ((!(localBaseFragment instanceof ChatActivity)) || (localBundle == null)) {
          continue;
        }
        paramBundle.putBundle("args", localBundle);
        paramBundle.putString("fragment", "chat");
      }
      catch (Exception paramBundle)
      {
        BaseFragment localBaseFragment;
        Bundle localBundle;
        FileLog.e(paramBundle);
        continue;
        if ((!(localBaseFragment instanceof GroupCreateFinalActivity)) || (localBundle == null)) {
          continue;
        }
        paramBundle.putBundle("args", localBundle);
        paramBundle.putString("fragment", "group");
        continue;
        if (!(localBaseFragment instanceof WallpapersActivity)) {
          continue;
        }
        paramBundle.putString("fragment", "wallpapers");
        continue;
        if ((!(localBaseFragment instanceof ProfileActivity)) || (!((ProfileActivity)localBaseFragment).isChat()) || (localBundle == null)) {
          continue;
        }
        paramBundle.putBundle("args", localBundle);
        paramBundle.putString("fragment", "chat_profile");
        continue;
        if ((!(localBaseFragment instanceof ChannelCreateActivity)) || (localBundle == null) || (localBundle.getInt("step") != 0)) {
          continue;
        }
        paramBundle.putBundle("args", localBundle);
        paramBundle.putString("fragment", "channel");
        continue;
        if ((!(localBaseFragment instanceof ChannelEditActivity)) || (localBundle == null)) {
          continue;
        }
        paramBundle.putBundle("args", localBundle);
        paramBundle.putString("fragment", "edit");
        continue;
      }
      localBaseFragment.saveSelfArgs(paramBundle);
      return;
      if (!this.rightActionBarLayout.fragmentsStack.isEmpty())
      {
        localBaseFragment = (BaseFragment)this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1);
      }
      else if (!this.actionBarLayout.fragmentsStack.isEmpty())
      {
        localBaseFragment = (BaseFragment)this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
        continue;
        if (!this.actionBarLayout.fragmentsStack.isEmpty())
        {
          localBaseFragment = (BaseFragment)this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
          continue;
          if (!(localBaseFragment instanceof SettingsActivity)) {
            continue;
          }
          paramBundle.putString("fragment", "settings");
        }
      }
    }
  }
  
  protected void onStart()
  {
    super.onStart();
    Browser.bindCustomTabsService(this);
  }
  
  protected void onStop()
  {
    super.onStop();
    Browser.unbindCustomTabsService(this);
  }
  
  public void presentFragment(BaseFragment paramBaseFragment)
  {
    this.actionBarLayout.presentFragment(paramBaseFragment);
  }
  
  public boolean presentFragment(BaseFragment paramBaseFragment, boolean paramBoolean1, boolean paramBoolean2)
  {
    return this.actionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, true);
  }
  
  public void rebuildAllFragments(boolean paramBoolean)
  {
    if (this.layersActionBarLayout != null) {
      this.layersActionBarLayout.rebuildAllFragmentViews(paramBoolean, paramBoolean);
    }
    for (;;)
    {
      return;
      this.actionBarLayout.rebuildAllFragmentViews(paramBoolean, paramBoolean);
    }
  }
  
  public AlertDialog showAlertDialog(AlertDialog.Builder paramBuilder)
  {
    Object localObject = null;
    try
    {
      if (this.visibleDialog != null)
      {
        this.visibleDialog.dismiss();
        this.visibleDialog = null;
      }
    }
    catch (Exception localException)
    {
      try
      {
        for (;;)
        {
          this.visibleDialog = paramBuilder.show();
          this.visibleDialog.setCanceledOnTouchOutside(true);
          paramBuilder = this.visibleDialog;
          DialogInterface.OnDismissListener local14 = new org/telegram/ui/LaunchActivity$14;
          local14.<init>(this);
          paramBuilder.setOnDismissListener(local14);
          paramBuilder = this.visibleDialog;
          return paramBuilder;
          localException = localException;
          FileLog.e(localException);
        }
      }
      catch (Exception paramBuilder)
      {
        for (;;)
        {
          FileLog.e(paramBuilder);
          paramBuilder = (AlertDialog.Builder)localObject;
        }
      }
    }
  }
  
  public void switchToAccount(int paramInt, boolean paramBoolean)
  {
    if (paramInt == UserConfig.selectedAccount) {
      return;
    }
    ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
    UserConfig.selectedAccount = paramInt;
    UserConfig.getInstance(0).saveConfig(false);
    checkCurrentAccount();
    if (AndroidUtilities.isTablet())
    {
      this.layersActionBarLayout.removeAllFragments();
      this.rightActionBarLayout.removeAllFragments();
      if (!this.tabletFullSize)
      {
        this.shadowTabletSide.setVisibility(0);
        if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
          this.backgroundTablet.setVisibility(0);
        }
      }
    }
    if (paramBoolean) {
      this.actionBarLayout.removeAllFragments();
    }
    for (;;)
    {
      DialogsActivity localDialogsActivity = new DialogsActivity(null);
      localDialogsActivity.setSideMenu(this.sideMenu);
      this.actionBarLayout.addFragmentToStack(localDialogsActivity, 0);
      this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
      this.actionBarLayout.showLastFragment();
      if (AndroidUtilities.isTablet())
      {
        this.layersActionBarLayout.showLastFragment();
        this.rightActionBarLayout.showLastFragment();
      }
      if (ApplicationLoader.mainInterfacePaused) {
        break;
      }
      ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
      break;
      this.actionBarLayout.removeFragmentFromStack(0);
    }
  }
  
  private class VcardData
  {
    String name;
    ArrayList<String> phones = new ArrayList();
    
    private VcardData() {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/LaunchActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */