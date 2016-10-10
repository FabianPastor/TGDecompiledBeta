package org.telegram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
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
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NativeCrashManager;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.query.DraftQuery;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatInvite;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputGameShortName;
import org.telegram.tgnet.TLRPC.TL_inputMediaGame;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_messages_checkChatInvite;
import org.telegram.tgnet.TLRPC.TL_messages_importChatInvite;
import org.telegram.tgnet.TLRPC.TL_userContact_old2;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DrawerLayoutAdapter;
import org.telegram.ui.Components.JoinGroupAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PasscodeView.PasscodeViewDelegate;
import org.telegram.ui.Components.StickersAlert;

public class LaunchActivity
  extends Activity
  implements ActionBarLayout.ActionBarLayoutDelegate, NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate
{
  private static ArrayList<BaseFragment> layerFragmentsStack = new ArrayList();
  private static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList();
  private static ArrayList<BaseFragment> rightFragmentsStack = new ArrayList();
  private ActionBarLayout actionBarLayout;
  private ImageView backgroundTablet;
  private ArrayList<TLRPC.User> contactsToSend;
  private int currentConnectionState;
  private String documentsMimeType;
  private ArrayList<String> documentsOriginalPathsArray;
  private ArrayList<String> documentsPathsArray;
  private ArrayList<Uri> documentsUrisArray;
  private DrawerLayoutAdapter drawerLayoutAdapter;
  protected DrawerLayoutContainer drawerLayoutContainer;
  private boolean finished;
  private ActionBarLayout layersActionBarLayout;
  private ListView listView;
  private Runnable lockRunnable;
  private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
  private Intent passcodeSaveIntent;
  private boolean passcodeSaveIntentIsNew;
  private boolean passcodeSaveIntentIsRestore;
  private PasscodeView passcodeView;
  private ArrayList<Uri> photoPathsArray;
  private ActionBarLayout rightActionBarLayout;
  private String sendingText;
  private FrameLayout shadowTablet;
  private FrameLayout shadowTabletSide;
  private boolean tabletFullSize;
  private String videoPath;
  private AlertDialog visibleDialog;
  
  private void checkLayout()
  {
    int j = 0;
    int k = 8;
    if (!AndroidUtilities.isTablet()) {
      return;
    }
    if ((!AndroidUtilities.isInMultiwindow) && ((!AndroidUtilities.isSmallTablet()) || (getResources().getConfiguration().orientation == 2)))
    {
      this.tabletFullSize = false;
      if (this.actionBarLayout.fragmentsStack.size() >= 2)
      {
        for (i = 1; i < this.actionBarLayout.fragmentsStack.size(); i = i - 1 + 1)
        {
          localObject = (BaseFragment)this.actionBarLayout.fragmentsStack.get(i);
          ((BaseFragment)localObject).onPause();
          this.actionBarLayout.fragmentsStack.remove(i);
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
        i = 8;
        ((ActionBarLayout)localObject).setVisibility(i);
        localObject = this.backgroundTablet;
        if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
          break label240;
        }
        i = 0;
        label201:
        ((ImageView)localObject).setVisibility(i);
        localObject = this.shadowTabletSide;
        if (this.actionBarLayout.fragmentsStack.isEmpty()) {
          break label246;
        }
      }
      label240:
      label246:
      for (i = j;; i = 8)
      {
        ((FrameLayout)localObject).setVisibility(i);
        return;
        i = 0;
        break;
        i = 8;
        break label201;
      }
    }
    this.tabletFullSize = true;
    if (!this.rightActionBarLayout.fragmentsStack.isEmpty())
    {
      for (i = 0; this.rightActionBarLayout.fragmentsStack.size() > 0; i = i - 1 + 1)
      {
        localObject = (BaseFragment)this.rightActionBarLayout.fragmentsStack.get(i);
        ((BaseFragment)localObject).onPause();
        this.rightActionBarLayout.fragmentsStack.remove(i);
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
    for (int i = k;; i = 0)
    {
      ((ImageView)localObject).setVisibility(i);
      return;
    }
  }
  
  private boolean handleIntent(Intent paramIntent, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    int i = paramIntent.getFlags();
    if ((!paramBoolean3) && ((AndroidUtilities.needShowPasscode(true)) || (UserConfig.isWaitingForPasscodeEnter)))
    {
      showPasscodeActivity();
      this.passcodeSaveIntent = paramIntent;
      this.passcodeSaveIntentIsNew = paramBoolean1;
      this.passcodeSaveIntentIsRestore = paramBoolean2;
      UserConfig.saveConfig(false);
      return false;
    }
    boolean bool1 = false;
    Integer localInteger1 = Integer.valueOf(0);
    Integer localInteger2 = Integer.valueOf(0);
    Integer localInteger3 = Integer.valueOf(0);
    Integer localInteger4 = Integer.valueOf(0);
    long l;
    int m;
    int n;
    Object localObject7;
    Object localObject8;
    Object localObject9;
    int j;
    int k;
    Object localObject10;
    Object localObject11;
    Object localObject12;
    Object localObject6;
    Object localObject13;
    label555:
    Object localObject14;
    label629:
    label681:
    label714:
    label914:
    label1021:
    label1079:
    label1145:
    label1202:
    label1281:
    label1396:
    Object localObject3;
    if ((paramIntent != null) && (paramIntent.getExtras() != null))
    {
      l = paramIntent.getExtras().getLong("dialogId", 0L);
      m = 0;
      n = 0;
      this.photoPathsArray = null;
      this.videoPath = null;
      this.sendingText = null;
      this.documentsPathsArray = null;
      this.documentsOriginalPathsArray = null;
      this.documentsMimeType = null;
      this.documentsUrisArray = null;
      this.contactsToSend = null;
      localObject7 = localInteger4;
      localObject8 = localInteger2;
      localObject9 = localInteger3;
      Object localObject1 = localInteger1;
      j = m;
      k = n;
      if (UserConfig.isClientActivated())
      {
        localObject7 = localInteger4;
        localObject8 = localInteger2;
        localObject9 = localInteger3;
        localObject1 = localInteger1;
        j = m;
        k = n;
        if ((0x100000 & i) == 0)
        {
          localObject7 = localInteger4;
          localObject8 = localInteger2;
          localObject9 = localInteger3;
          localObject1 = localInteger1;
          j = m;
          k = n;
          if (paramIntent != null)
          {
            localObject7 = localInteger4;
            localObject8 = localInteger2;
            localObject9 = localInteger3;
            localObject1 = localInteger1;
            j = m;
            k = n;
            if (paramIntent.getAction() != null)
            {
              localObject7 = localInteger4;
              localObject8 = localInteger2;
              localObject9 = localInteger3;
              localObject1 = localInteger1;
              j = m;
              k = n;
              if (!paramBoolean2)
              {
                if (!"android.intent.action.SEND".equals(paramIntent.getAction())) {
                  break label1901;
                }
                i = 0;
                k = 0;
                localObject7 = paramIntent.getType();
                if ((localObject7 == null) || (!((String)localObject7).equals("text/x-vcard"))) {
                  break label1396;
                }
                try
                {
                  localObject1 = (Uri)paramIntent.getExtras().get("android.intent.extra.STREAM");
                  if (localObject1 != null)
                  {
                    localObject10 = getContentResolver().openInputStream((Uri)localObject1);
                    localObject9 = new ArrayList();
                    localObject8 = null;
                    localObject11 = new BufferedReader(new InputStreamReader((InputStream)localObject10, "UTF-8"));
                    for (;;)
                    {
                      localObject1 = ((BufferedReader)localObject11).readLine();
                      if (localObject1 == null) {
                        break label1202;
                      }
                      FileLog.e("tmessages", (String)localObject1);
                      localObject12 = ((String)localObject1).split(":");
                      if (localObject12.length == 2)
                      {
                        if ((localObject12[0].equals("BEGIN")) && (localObject12[1].equals("VCARD")))
                        {
                          localObject1 = new VcardData(null);
                          ((ArrayList)localObject9).add(localObject1);
                        }
                        for (;;)
                        {
                          localObject8 = localObject1;
                          if (localObject1 == null) {
                            break;
                          }
                          if ((!localObject12[0].startsWith("FN")) && ((!localObject12[0].startsWith("ORG")) || (!TextUtils.isEmpty(((VcardData)localObject1).name)))) {
                            break label1145;
                          }
                          localObject7 = null;
                          localObject6 = null;
                          localObject13 = localObject12[0].split(";");
                          j = localObject13.length;
                          i = 0;
                          if (i >= j) {
                            break label681;
                          }
                          localObject14 = localObject13[i].split("=");
                          if (localObject14.length == 2) {
                            break label629;
                          }
                          localObject8 = localObject6;
                          break label6005;
                          localObject1 = localObject8;
                          if (localObject12[0].equals("END"))
                          {
                            localObject1 = localObject8;
                            if (localObject12[1].equals("VCARD")) {
                              localObject1 = null;
                            }
                          }
                        }
                        if (localObject14[0].equals("CHARSET"))
                        {
                          localObject8 = localObject14[1];
                          break label6005;
                        }
                        localObject8 = localObject6;
                        if (!localObject14[0].equals("ENCODING")) {
                          break label6005;
                        }
                        localObject7 = localObject14[1];
                        localObject8 = localObject6;
                        break label6005;
                        ((VcardData)localObject1).name = localObject12[1];
                        localObject8 = localObject1;
                        if (localObject7 != null)
                        {
                          localObject8 = localObject1;
                          if (((String)localObject7).equalsIgnoreCase("QUOTED-PRINTABLE"))
                          {
                            if ((((VcardData)localObject1).name.endsWith("=")) && (localObject7 != null))
                            {
                              ((VcardData)localObject1).name = ((VcardData)localObject1).name.substring(0, ((VcardData)localObject1).name.length() - 1);
                              localObject8 = ((BufferedReader)localObject11).readLine();
                              if (localObject8 != null) {
                                break;
                              }
                            }
                            localObject7 = AndroidUtilities.decodeQuotedPrintable(((VcardData)localObject1).name.getBytes());
                            localObject8 = localObject1;
                            if (localObject7 != null)
                            {
                              localObject8 = localObject1;
                              if (localObject7.length != 0)
                              {
                                localObject6 = new String((byte[])localObject7, (String)localObject6);
                                localObject8 = localObject1;
                                if (localObject6 != null)
                                {
                                  ((VcardData)localObject1).name = ((String)localObject6);
                                  localObject8 = localObject1;
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                    localObject7 = localInteger4;
                  }
                }
                catch (Exception localException1)
                {
                  FileLog.e("tmessages", localException1);
                  i = 1;
                }
              }
            }
          }
        }
      }
      for (;;)
      {
        localObject8 = localInteger2;
        localObject9 = localInteger3;
        Object localObject2 = localInteger1;
        j = m;
        k = n;
        if (i != 0)
        {
          Toast.makeText(this, "Unsupported content", 0).show();
          k = n;
          j = m;
          localObject2 = localInteger1;
          localObject9 = localInteger3;
          localObject8 = localInteger2;
          localObject7 = localInteger4;
        }
        if (((Integer)localObject2).intValue() == 0) {
          break label4949;
        }
        localObject6 = new Bundle();
        ((Bundle)localObject6).putInt("user_id", ((Integer)localObject2).intValue());
        if (!mainFragmentsStack.isEmpty())
        {
          paramBoolean2 = bool1;
          paramBoolean3 = paramBoolean1;
          if (!MessagesController.checkCanOpenChat((Bundle)localObject6, (BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1))) {}
        }
        else
        {
          localObject2 = new ChatActivity((Bundle)localObject6);
          paramBoolean2 = bool1;
          paramBoolean3 = paramBoolean1;
          if (this.actionBarLayout.presentFragment((BaseFragment)localObject2, false, true, true))
          {
            paramBoolean2 = true;
            paramBoolean3 = paramBoolean1;
          }
        }
        if ((!paramBoolean2) && (!paramBoolean3))
        {
          if (!AndroidUtilities.isTablet()) {
            break label5931;
          }
          if (UserConfig.isClientActivated()) {
            break label5890;
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
        return paramBoolean2;
        ((VcardData)localObject2).name += (String)localObject8;
        break label714;
        localObject8 = localObject2;
        if (!localObject12[0].startsWith("TEL")) {
          break;
        }
        localObject6 = PhoneFormat.stripExceptNumbers(localObject12[1], true);
        localObject8 = localObject2;
        if (((String)localObject6).length() <= 0) {
          break;
        }
        ((VcardData)localObject2).phones.add(localObject6);
        localObject8 = localObject2;
        break;
        try
        {
          ((BufferedReader)localObject11).close();
          ((InputStream)localObject10).close();
          j = 0;
          i = k;
          if (j >= ((ArrayList)localObject9).size()) {
            continue;
          }
          localObject2 = (VcardData)((ArrayList)localObject9).get(j);
          if ((((VcardData)localObject2).name != null) && (!((VcardData)localObject2).phones.isEmpty()))
          {
            if (this.contactsToSend != null) {
              break label6024;
            }
            this.contactsToSend = new ArrayList();
            break label6024;
            while (i < ((VcardData)localObject2).phones.size())
            {
              localObject6 = (String)((VcardData)localObject2).phones.get(i);
              localObject7 = new TLRPC.TL_userContact_old2();
              ((TLRPC.User)localObject7).phone = ((String)localObject6);
              ((TLRPC.User)localObject7).first_name = ((VcardData)localObject2).name;
              ((TLRPC.User)localObject7).last_name = "";
              ((TLRPC.User)localObject7).id = 0;
              this.contactsToSend.add(localObject7);
              i += 1;
            }
          }
        }
        catch (Exception localException2)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException2);
            continue;
            j += 1;
          }
        }
        i = 1;
        continue;
        localObject6 = paramIntent.getStringExtra("android.intent.extra.TEXT");
        localObject3 = localObject6;
        if (localObject6 == null)
        {
          localObject8 = paramIntent.getCharSequenceExtra("android.intent.extra.TEXT");
          localObject3 = localObject6;
          if (localObject8 != null) {
            localObject3 = ((CharSequence)localObject8).toString();
          }
        }
        localObject8 = paramIntent.getStringExtra("android.intent.extra.SUBJECT");
        if ((localObject3 != null) && (((String)localObject3).length() != 0)) {
          if (!((String)localObject3).startsWith("http://"))
          {
            localObject6 = localObject3;
            if (!((String)localObject3).startsWith("https://")) {}
          }
          else
          {
            localObject6 = localObject3;
            if (localObject8 != null)
            {
              localObject6 = localObject3;
              if (((String)localObject8).length() != 0) {
                localObject6 = (String)localObject8 + "\n" + (String)localObject3;
              }
            }
          }
        }
        for (this.sendingText = ((String)localObject6);; this.sendingText = ((String)localObject8)) {
          do
          {
            localObject6 = paramIntent.getParcelableExtra("android.intent.extra.STREAM");
            if (localObject6 == null) {
              break label1884;
            }
            localObject3 = localObject6;
            if (!(localObject6 instanceof Uri)) {
              localObject3 = Uri.parse(localObject6.toString());
            }
            localObject8 = (Uri)localObject3;
            j = i;
            if (localObject8 != null)
            {
              j = i;
              if (AndroidUtilities.isInternalUri((Uri)localObject8)) {
                j = 1;
              }
            }
            i = j;
            if (j != 0) {
              break;
            }
            if ((localObject8 == null) || (((localObject7 == null) || (!((String)localObject7).startsWith("image/"))) && (!((Uri)localObject8).toString().toLowerCase().endsWith(".jpg")))) {
              break label1715;
            }
            if (this.photoPathsArray == null) {
              this.photoPathsArray = new ArrayList();
            }
            this.photoPathsArray.add(localObject8);
            i = j;
            break;
          } while ((localObject8 == null) || (((String)localObject8).length() <= 0));
        }
        label1715:
        localObject6 = AndroidUtilities.getPath((Uri)localObject8);
        if (localObject6 != null)
        {
          localObject3 = localObject6;
          if (((String)localObject6).startsWith("file:")) {
            localObject3 = ((String)localObject6).replace("file://", "");
          }
          if ((localObject7 != null) && (((String)localObject7).startsWith("video/")))
          {
            this.videoPath = ((String)localObject3);
            i = j;
          }
          else
          {
            if (this.documentsPathsArray == null)
            {
              this.documentsPathsArray = new ArrayList();
              this.documentsOriginalPathsArray = new ArrayList();
            }
            this.documentsPathsArray.add(localObject3);
            this.documentsOriginalPathsArray.add(((Uri)localObject8).toString());
            i = j;
          }
        }
        else
        {
          if (this.documentsUrisArray == null) {
            this.documentsUrisArray = new ArrayList();
          }
          this.documentsUrisArray.add(localObject8);
          this.documentsMimeType = ((String)localObject7);
          i = j;
          continue;
          label1884:
          i = k;
          if (this.sendingText == null) {
            i = 1;
          }
        }
      }
      label1901:
      if (paramIntent.getAction().equals("android.intent.action.SEND_MULTIPLE")) {
        k = 0;
      }
    }
    for (;;)
    {
      try
      {
        localObject6 = paramIntent.getParcelableArrayListExtra("android.intent.extra.STREAM");
        localObject8 = paramIntent.getType();
        localObject3 = localObject6;
        if (localObject6 != null)
        {
          i = 0;
          if (i < ((ArrayList)localObject6).size())
          {
            localObject7 = (Parcelable)((ArrayList)localObject6).get(i);
            localObject3 = localObject7;
            if (!(localObject7 instanceof Uri)) {
              localObject3 = Uri.parse(localObject7.toString());
            }
            localObject3 = (Uri)localObject3;
            j = i;
            if (localObject3 == null) {
              break label6030;
            }
            j = i;
            if (!AndroidUtilities.isInternalUri((Uri)localObject3)) {
              break label6030;
            }
            ((ArrayList)localObject6).remove(i);
            j = i - 1;
            break label6030;
          }
          localObject3 = localObject6;
          if (((ArrayList)localObject6).isEmpty()) {
            localObject3 = null;
          }
        }
        if (localObject3 != null)
        {
          if ((localObject8 == null) || (!((String)localObject8).startsWith("image/"))) {
            break label6039;
          }
          j = 0;
          i = k;
          if (j < ((ArrayList)localObject3).size())
          {
            localObject7 = (Parcelable)((ArrayList)localObject3).get(j);
            localObject6 = localObject7;
            if (!(localObject7 instanceof Uri)) {
              localObject6 = Uri.parse(localObject7.toString());
            }
            localObject6 = (Uri)localObject6;
            if (this.photoPathsArray == null) {
              this.photoPathsArray = new ArrayList();
            }
            this.photoPathsArray.add(localObject6);
            j += 1;
            continue;
            i = k;
            if (j < ((ArrayList)localObject3).size())
            {
              localObject6 = (Parcelable)((ArrayList)localObject3).get(j);
              localObject7 = localObject6;
              if (!(localObject6 instanceof Uri)) {
                localObject7 = Uri.parse(localObject6.toString());
              }
              localObject6 = AndroidUtilities.getPath((Uri)localObject7);
              localObject8 = localObject7.toString();
              localObject7 = localObject8;
              if (localObject8 == null) {
                localObject7 = localObject6;
              }
              if (localObject6 != null)
              {
                localObject8 = localObject6;
                if (((String)localObject6).startsWith("file:")) {
                  localObject8 = ((String)localObject6).replace("file://", "");
                }
                if (this.documentsPathsArray == null)
                {
                  this.documentsPathsArray = new ArrayList();
                  this.documentsOriginalPathsArray = new ArrayList();
                }
                this.documentsPathsArray.add(localObject8);
                this.documentsOriginalPathsArray.add(localObject7);
              }
              j += 1;
              continue;
            }
          }
        }
        else
        {
          i = 1;
        }
      }
      catch (Exception localException3)
      {
        FileLog.e("tmessages", localException3);
        i = 1;
        continue;
      }
      localObject7 = localInteger4;
      localObject8 = localInteger2;
      localObject9 = localInteger3;
      localObject3 = localInteger1;
      j = m;
      k = n;
      if (i == 0) {
        break label914;
      }
      Toast.makeText(this, "Unsupported content", 0).show();
      localObject7 = localInteger4;
      localObject8 = localInteger2;
      localObject9 = localInteger3;
      localObject3 = localInteger1;
      j = m;
      k = n;
      break label914;
      if ("android.intent.action.VIEW".equals(paramIntent.getAction()))
      {
        Uri localUri = paramIntent.getData();
        localObject7 = localInteger4;
        localObject8 = localInteger2;
        localObject9 = localInteger3;
        final Object localObject4 = localInteger1;
        j = m;
        k = n;
        if (localUri == null) {
          break label914;
        }
        Object localObject20 = null;
        String str1 = null;
        Object localObject16 = null;
        Object localObject17 = null;
        String str2 = null;
        String str3 = null;
        localObject6 = null;
        Object localObject18 = null;
        String str4 = null;
        Object localObject19 = null;
        String str5 = null;
        Integer localInteger5 = null;
        boolean bool2 = false;
        boolean bool4 = false;
        boolean bool3 = false;
        boolean bool5 = false;
        paramBoolean3 = false;
        String str6 = localUri.getScheme();
        localObject7 = str1;
        localObject8 = localObject16;
        localObject9 = localObject17;
        localObject10 = str2;
        localObject11 = str3;
        localObject12 = localObject6;
        paramBoolean2 = paramBoolean3;
        localObject4 = localInteger5;
        localObject13 = str4;
        localObject14 = localObject18;
        Object localObject15 = localObject19;
        if (str6 != null)
        {
          if ((!str6.equals("http")) && (!str6.equals("https"))) {
            break label3499;
          }
          str6 = localUri.getHost().toLowerCase();
          if (!str6.equals("telegram.me"))
          {
            localObject7 = str1;
            localObject8 = localObject16;
            localObject9 = localObject17;
            localObject10 = str2;
            localObject11 = str3;
            localObject12 = localObject6;
            paramBoolean2 = paramBoolean3;
            localObject4 = localInteger5;
            localObject13 = str4;
            localObject14 = localObject18;
            localObject15 = localObject19;
            if (!str6.equals("telegram.dog")) {}
          }
          else
          {
            str6 = localUri.getPath();
            localObject7 = str1;
            localObject8 = localObject16;
            localObject9 = localObject17;
            localObject10 = str2;
            localObject11 = str3;
            localObject12 = localObject6;
            paramBoolean2 = paramBoolean3;
            localObject4 = localInteger5;
            localObject13 = str4;
            localObject14 = localObject18;
            localObject15 = localObject19;
            if (str6 != null)
            {
              localObject7 = str1;
              localObject8 = localObject16;
              localObject9 = localObject17;
              localObject10 = str2;
              localObject11 = str3;
              localObject12 = localObject6;
              paramBoolean2 = paramBoolean3;
              localObject4 = localInteger5;
              localObject13 = str4;
              localObject14 = localObject18;
              localObject15 = localObject19;
              if (str6.length() > 1)
              {
                str6 = str6.substring(1);
                if (!str6.startsWith("joinchat/")) {
                  break label2927;
                }
                localObject8 = str6.replace("joinchat/", "");
                localObject15 = localObject19;
                localObject14 = localObject18;
                localObject13 = str4;
                localObject4 = localInteger5;
                paramBoolean2 = paramBoolean3;
                localObject12 = localObject6;
                localObject11 = str3;
                localObject10 = str2;
                localObject9 = localObject17;
                localObject7 = str1;
              }
            }
          }
        }
        for (;;)
        {
          if ((localObject14 == null) && (localObject15 == null)) {
            break label4367;
          }
          localObject4 = new Bundle();
          ((Bundle)localObject4).putString("phone", (String)localObject14);
          ((Bundle)localObject4).putString("hash", (String)localObject15);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              LaunchActivity.this.presentFragment(new CancelAccountDeletionActivity(localObject4));
            }
          });
          localObject7 = localInteger4;
          localObject8 = localInteger2;
          localObject9 = localInteger3;
          localObject4 = localInteger1;
          j = m;
          k = n;
          break;
          label2927:
          if (str6.startsWith("addstickers/"))
          {
            localObject9 = str6.replace("addstickers/", "");
            localObject7 = str1;
            localObject8 = localObject16;
            localObject10 = str2;
            localObject11 = str3;
            localObject12 = localObject6;
            paramBoolean2 = paramBoolean3;
            localObject4 = localInteger5;
            localObject13 = str4;
            localObject14 = localObject18;
            localObject15 = localObject19;
          }
          else
          {
            if ((str6.startsWith("msg/")) || (str6.startsWith("share/")))
            {
              localObject6 = localUri.getQueryParameter("url");
              localObject4 = localObject6;
              if (localObject6 == null) {
                localObject4 = "";
              }
              localObject7 = localObject4;
              paramBoolean3 = bool4;
              if (localUri.getQueryParameter("text") != null)
              {
                localObject6 = localObject4;
                paramBoolean3 = bool2;
                if (((String)localObject4).length() > 0)
                {
                  paramBoolean3 = true;
                  localObject6 = (String)localObject4 + "\n";
                }
                localObject7 = (String)localObject6 + localUri.getQueryParameter("text");
              }
              localObject6 = localObject7;
              if (((String)localObject7).length() > 16384) {}
              for (localObject6 = ((String)localObject7).substring(0, 16384);; localObject6 = ((String)localObject6).substring(0, ((String)localObject6).length() - 1))
              {
                localObject7 = str1;
                localObject8 = localObject16;
                localObject9 = localObject17;
                localObject10 = str2;
                localObject11 = str3;
                localObject12 = localObject6;
                paramBoolean2 = paramBoolean3;
                localObject4 = localInteger5;
                localObject13 = str4;
                localObject14 = localObject18;
                localObject15 = localObject19;
                if (!((String)localObject6).endsWith("\n")) {
                  break;
                }
              }
            }
            if (str6.startsWith("confirmphone"))
            {
              localObject14 = localUri.getQueryParameter("phone");
              localObject15 = localUri.getQueryParameter("hash");
              localObject7 = str1;
              localObject8 = localObject16;
              localObject9 = localObject17;
              localObject10 = str2;
              localObject11 = str3;
              localObject12 = localObject6;
              paramBoolean2 = paramBoolean3;
              localObject4 = localInteger5;
              localObject13 = str4;
            }
            else
            {
              localObject7 = str1;
              localObject8 = localObject16;
              localObject9 = localObject17;
              localObject10 = str2;
              localObject11 = str3;
              localObject12 = localObject6;
              paramBoolean2 = paramBoolean3;
              localObject4 = localInteger5;
              localObject13 = str4;
              localObject14 = localObject18;
              localObject15 = localObject19;
              if (str6.length() >= 1)
              {
                localObject9 = localUri.getPathSegments();
                localObject7 = localObject20;
                localObject4 = str5;
                if (((List)localObject9).size() > 0)
                {
                  localObject8 = (String)((List)localObject9).get(0);
                  localObject7 = localObject8;
                  localObject4 = str5;
                  if (((List)localObject9).size() > 1)
                  {
                    localObject9 = Utilities.parseInt((String)((List)localObject9).get(1));
                    localObject7 = localObject8;
                    localObject4 = localObject9;
                    if (((Integer)localObject9).intValue() == 0)
                    {
                      localObject4 = null;
                      localObject7 = localObject8;
                    }
                  }
                }
                localObject10 = localUri.getQueryParameter("start");
                localObject11 = localUri.getQueryParameter("startgroup");
                localObject13 = localUri.getQueryParameter("game");
                localObject8 = localObject16;
                localObject9 = localObject17;
                localObject12 = localObject6;
                paramBoolean2 = paramBoolean3;
                localObject14 = localObject18;
                localObject15 = localObject19;
                continue;
                label3499:
                localObject7 = str1;
                localObject8 = localObject16;
                localObject9 = localObject17;
                localObject10 = str2;
                localObject11 = str3;
                localObject12 = localObject6;
                paramBoolean2 = paramBoolean3;
                localObject4 = localInteger5;
                localObject13 = str4;
                localObject14 = localObject18;
                localObject15 = localObject19;
                if (str6.equals("tg"))
                {
                  str5 = localUri.toString();
                  if ((str5.startsWith("tg:resolve")) || (str5.startsWith("tg://resolve")))
                  {
                    localObject4 = Uri.parse(str5.replace("tg:resolve", "tg://telegram.org").replace("tg://resolve", "tg://telegram.org"));
                    str1 = ((Uri)localObject4).getQueryParameter("domain");
                    str2 = ((Uri)localObject4).getQueryParameter("start");
                    str3 = ((Uri)localObject4).getQueryParameter("startgroup");
                    str4 = ((Uri)localObject4).getQueryParameter("game");
                    localInteger5 = Utilities.parseInt(((Uri)localObject4).getQueryParameter("post"));
                    localObject7 = str1;
                    localObject8 = localObject16;
                    localObject9 = localObject17;
                    localObject10 = str2;
                    localObject11 = str3;
                    localObject12 = localObject6;
                    paramBoolean2 = paramBoolean3;
                    localObject4 = localInteger5;
                    localObject13 = str4;
                    localObject14 = localObject18;
                    localObject15 = localObject19;
                    if (localInteger5.intValue() == 0)
                    {
                      localObject4 = null;
                      localObject7 = str1;
                      localObject8 = localObject16;
                      localObject9 = localObject17;
                      localObject10 = str2;
                      localObject11 = str3;
                      localObject12 = localObject6;
                      paramBoolean2 = paramBoolean3;
                      localObject13 = str4;
                      localObject14 = localObject18;
                      localObject15 = localObject19;
                    }
                  }
                  else if ((str5.startsWith("tg:join")) || (str5.startsWith("tg://join")))
                  {
                    localObject8 = Uri.parse(str5.replace("tg:join", "tg://telegram.org").replace("tg://join", "tg://telegram.org")).getQueryParameter("invite");
                    localObject7 = str1;
                    localObject9 = localObject17;
                    localObject10 = str2;
                    localObject11 = str3;
                    localObject12 = localObject6;
                    paramBoolean2 = paramBoolean3;
                    localObject4 = localInteger5;
                    localObject13 = str4;
                    localObject14 = localObject18;
                    localObject15 = localObject19;
                  }
                  else if ((str5.startsWith("tg:addstickers")) || (str5.startsWith("tg://addstickers")))
                  {
                    localObject9 = Uri.parse(str5.replace("tg:addstickers", "tg://telegram.org").replace("tg://addstickers", "tg://telegram.org")).getQueryParameter("set");
                    localObject7 = str1;
                    localObject8 = localObject16;
                    localObject10 = str2;
                    localObject11 = str3;
                    localObject12 = localObject6;
                    paramBoolean2 = paramBoolean3;
                    localObject4 = localInteger5;
                    localObject13 = str4;
                    localObject14 = localObject18;
                    localObject15 = localObject19;
                  }
                  else
                  {
                    if ((str5.startsWith("tg:msg")) || (str5.startsWith("tg://msg")) || (str5.startsWith("tg://share")) || (str5.startsWith("tg:share")))
                    {
                      localObject8 = Uri.parse(str5.replace("tg:msg", "tg://telegram.org").replace("tg://msg", "tg://telegram.org").replace("tg://share", "tg://telegram.org").replace("tg:share", "tg://telegram.org"));
                      localObject6 = ((Uri)localObject8).getQueryParameter("url");
                      localObject4 = localObject6;
                      if (localObject6 == null) {
                        localObject4 = "";
                      }
                      localObject7 = localObject4;
                      paramBoolean3 = bool5;
                      if (((Uri)localObject8).getQueryParameter("text") != null)
                      {
                        localObject6 = localObject4;
                        paramBoolean3 = bool3;
                        if (((String)localObject4).length() > 0)
                        {
                          paramBoolean3 = true;
                          localObject6 = (String)localObject4 + "\n";
                        }
                        localObject7 = (String)localObject6 + ((Uri)localObject8).getQueryParameter("text");
                      }
                      localObject6 = localObject7;
                      if (((String)localObject7).length() > 16384) {}
                      for (localObject6 = ((String)localObject7).substring(0, 16384);; localObject6 = ((String)localObject6).substring(0, ((String)localObject6).length() - 1))
                      {
                        localObject7 = str1;
                        localObject8 = localObject16;
                        localObject9 = localObject17;
                        localObject10 = str2;
                        localObject11 = str3;
                        localObject12 = localObject6;
                        paramBoolean2 = paramBoolean3;
                        localObject4 = localInteger5;
                        localObject13 = str4;
                        localObject14 = localObject18;
                        localObject15 = localObject19;
                        if (!((String)localObject6).endsWith("\n")) {
                          break;
                        }
                      }
                    }
                    if (!str5.startsWith("tg:confirmphone"))
                    {
                      localObject7 = str1;
                      localObject8 = localObject16;
                      localObject9 = localObject17;
                      localObject10 = str2;
                      localObject11 = str3;
                      localObject12 = localObject6;
                      paramBoolean2 = paramBoolean3;
                      localObject4 = localInteger5;
                      localObject13 = str4;
                      localObject14 = localObject18;
                      localObject15 = localObject19;
                      if (!str5.startsWith("tg://confirmphone")) {}
                    }
                    else
                    {
                      localObject14 = localUri.getQueryParameter("phone");
                      localObject15 = localUri.getQueryParameter("hash");
                      localObject7 = str1;
                      localObject8 = localObject16;
                      localObject9 = localObject17;
                      localObject10 = str2;
                      localObject11 = str3;
                      localObject12 = localObject6;
                      paramBoolean2 = paramBoolean3;
                      localObject4 = localInteger5;
                      localObject13 = str4;
                    }
                  }
                }
              }
            }
          }
        }
        label4367:
        if ((localObject7 != null) || (localObject8 != null) || (localObject9 != null) || (localObject12 != null) || (localObject13 != null))
        {
          runLinkRequest((String)localObject7, (String)localObject8, (String)localObject9, (String)localObject10, (String)localObject11, (String)localObject12, paramBoolean2, (Integer)localObject4, (String)localObject13, 0);
          localObject7 = localInteger4;
          localObject8 = localInteger2;
          localObject9 = localInteger3;
          localObject4 = localInteger1;
          j = m;
          k = n;
          break label914;
        }
        localObject6 = localInteger1;
        try
        {
          localObject10 = getContentResolver().query(paramIntent.getData(), null, null, null, null);
          localObject7 = localInteger4;
          localObject8 = localInteger2;
          localObject9 = localInteger3;
          localObject4 = localInteger1;
          j = m;
          k = n;
          if (localObject10 == null) {
            break label914;
          }
          localObject4 = localInteger1;
          localObject6 = localInteger1;
          if (((Cursor)localObject10).moveToFirst())
          {
            localObject6 = localInteger1;
            i = ((Cursor)localObject10).getInt(((Cursor)localObject10).getColumnIndex("DATA4"));
            localObject6 = localInteger1;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            localObject6 = localInteger1;
            localObject4 = Integer.valueOf(i);
          }
          localObject6 = localObject4;
          ((Cursor)localObject10).close();
          localObject7 = localInteger4;
          localObject8 = localInteger2;
          localObject9 = localInteger3;
          j = m;
          k = n;
        }
        catch (Exception localException4)
        {
          FileLog.e("tmessages", localException4);
          localObject7 = localInteger4;
          localObject8 = localInteger2;
          localObject9 = localInteger3;
          localObject5 = localObject6;
          j = m;
          k = n;
        }
        break label914;
      }
      if (paramIntent.getAction().equals("org.telegram.messenger.OPEN_ACCOUNT"))
      {
        localObject7 = Integer.valueOf(1);
        localObject8 = localInteger2;
        localObject9 = localInteger3;
        localObject5 = localInteger1;
        j = m;
        k = n;
        break label914;
      }
      if (paramIntent.getAction().startsWith("com.tmessages.openchat"))
      {
        i = paramIntent.getIntExtra("chatId", 0);
        j = paramIntent.getIntExtra("userId", 0);
        k = paramIntent.getIntExtra("encId", 0);
        if (i != 0)
        {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
          localObject8 = Integer.valueOf(i);
          localObject7 = localInteger4;
          localObject9 = localInteger3;
          localObject5 = localInteger1;
          j = m;
          k = n;
          break label914;
        }
        if (j != 0)
        {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
          localObject5 = Integer.valueOf(j);
          localObject7 = localInteger4;
          localObject8 = localInteger2;
          localObject9 = localInteger3;
          j = m;
          k = n;
          break label914;
        }
        if (k != 0)
        {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
          localObject9 = Integer.valueOf(k);
          localObject7 = localInteger4;
          localObject8 = localInteger2;
          localObject5 = localInteger1;
          j = m;
          k = n;
          break label914;
        }
        j = 1;
        localObject7 = localInteger4;
        localObject8 = localInteger2;
        localObject9 = localInteger3;
        localObject5 = localInteger1;
        k = n;
        break label914;
      }
      localObject7 = localInteger4;
      localObject8 = localInteger2;
      localObject9 = localInteger3;
      Object localObject5 = localInteger1;
      j = m;
      k = n;
      if (!paramIntent.getAction().equals("com.tmessages.openplayer")) {
        break label914;
      }
      k = 1;
      localObject7 = localInteger4;
      localObject8 = localInteger2;
      localObject9 = localInteger3;
      localObject5 = localInteger1;
      j = m;
      break label914;
      label4949:
      if (((Integer)localObject8).intValue() != 0)
      {
        localObject5 = new Bundle();
        ((Bundle)localObject5).putInt("chat_id", ((Integer)localObject8).intValue());
        if (!mainFragmentsStack.isEmpty())
        {
          paramBoolean2 = bool1;
          paramBoolean3 = paramBoolean1;
          if (!MessagesController.checkCanOpenChat((Bundle)localObject5, (BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1))) {
            break label1021;
          }
        }
        localObject5 = new ChatActivity((Bundle)localObject5);
        paramBoolean2 = bool1;
        paramBoolean3 = paramBoolean1;
        if (!this.actionBarLayout.presentFragment((BaseFragment)localObject5, false, true, true)) {
          break label1021;
        }
        paramBoolean2 = true;
        paramBoolean3 = paramBoolean1;
        break label1021;
      }
      if (((Integer)localObject9).intValue() != 0)
      {
        localObject5 = new Bundle();
        ((Bundle)localObject5).putInt("enc_id", ((Integer)localObject9).intValue());
        localObject5 = new ChatActivity((Bundle)localObject5);
        paramBoolean2 = bool1;
        paramBoolean3 = paramBoolean1;
        if (!this.actionBarLayout.presentFragment((BaseFragment)localObject5, false, true, true)) {
          break label1021;
        }
        paramBoolean2 = true;
        paramBoolean3 = paramBoolean1;
        break label1021;
      }
      if (j != 0)
      {
        if (!AndroidUtilities.isTablet()) {
          this.actionBarLayout.removeAllFragments();
        }
        for (;;)
        {
          paramBoolean2 = false;
          paramBoolean3 = false;
          break;
          if (!this.layersActionBarLayout.fragmentsStack.isEmpty())
          {
            for (i = 0; this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
              this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
            }
            this.layersActionBarLayout.closeLastFragment(false);
          }
        }
      }
      if (k != 0)
      {
        if (AndroidUtilities.isTablet())
        {
          i = 0;
          for (;;)
          {
            if (i < this.layersActionBarLayout.fragmentsStack.size())
            {
              localObject5 = (BaseFragment)this.layersActionBarLayout.fragmentsStack.get(i);
              if ((localObject5 instanceof AudioPlayerActivity)) {
                this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)localObject5);
              }
            }
            else
            {
              this.actionBarLayout.showLastFragment();
              this.rightActionBarLayout.showLastFragment();
              this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
              this.actionBarLayout.presentFragment(new AudioPlayerActivity(), false, true, true);
              paramBoolean2 = true;
              paramBoolean3 = paramBoolean1;
              break;
            }
            i += 1;
          }
        }
        i = 0;
        for (;;)
        {
          if (i < this.actionBarLayout.fragmentsStack.size())
          {
            localObject5 = (BaseFragment)this.actionBarLayout.fragmentsStack.get(i);
            if ((localObject5 instanceof AudioPlayerActivity)) {
              this.actionBarLayout.removeFragmentFromStack((BaseFragment)localObject5);
            }
          }
          else
          {
            this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            break;
          }
          i += 1;
        }
      }
      if ((this.videoPath != null) || (this.photoPathsArray != null) || (this.sendingText != null) || (this.documentsPathsArray != null) || (this.contactsToSend != null) || (this.documentsUrisArray != null))
      {
        if (!AndroidUtilities.isTablet()) {
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        if (l == 0L)
        {
          localObject5 = new Bundle();
          ((Bundle)localObject5).putBoolean("onlySelect", true);
          if (this.contactsToSend != null)
          {
            ((Bundle)localObject5).putString("selectAlertString", LocaleController.getString("SendContactTo", 2131166242));
            ((Bundle)localObject5).putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", 2131166236));
            label5550:
            localObject5 = new DialogsActivity((Bundle)localObject5);
            ((DialogsActivity)localObject5).setDelegate(this);
            if (!AndroidUtilities.isTablet()) {
              break label5725;
            }
            if ((this.layersActionBarLayout.fragmentsStack.size() <= 0) || (!(this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity))) {
              break label5720;
            }
          }
          label5720:
          for (paramBoolean2 = true;; paramBoolean2 = false)
          {
            this.actionBarLayout.presentFragment((BaseFragment)localObject5, paramBoolean2, true, true);
            paramBoolean2 = true;
            if (PhotoViewer.getInstance().isVisible()) {
              PhotoViewer.getInstance().closePhoto(false, true);
            }
            this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
            if (!AndroidUtilities.isTablet()) {
              break label5777;
            }
            this.actionBarLayout.showLastFragment();
            this.rightActionBarLayout.showLastFragment();
            paramBoolean3 = paramBoolean1;
            break;
            ((Bundle)localObject5).putString("selectAlertString", LocaleController.getString("SendMessagesTo", 2131166242));
            ((Bundle)localObject5).putString("selectAlertStringGroup", LocaleController.getString("SendMessagesToGroup", 2131166243));
            break label5550;
          }
          label5725:
          if ((this.actionBarLayout.fragmentsStack.size() > 1) && ((this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity))) {}
          for (paramBoolean2 = true;; paramBoolean2 = false) {
            break;
          }
          label5777:
          this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
          paramBoolean3 = paramBoolean1;
          break label1021;
        }
        didSelectDialog(null, l, false);
        paramBoolean2 = bool1;
        paramBoolean3 = paramBoolean1;
        break label1021;
      }
      paramBoolean2 = bool1;
      paramBoolean3 = paramBoolean1;
      if (((Integer)localObject7).intValue() == 0) {
        break label1021;
      }
      this.actionBarLayout.presentFragment(new SettingsActivity(), false, true, true);
      if (AndroidUtilities.isTablet())
      {
        this.actionBarLayout.showLastFragment();
        this.rightActionBarLayout.showLastFragment();
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
      }
      for (;;)
      {
        paramBoolean2 = true;
        paramBoolean3 = paramBoolean1;
        break;
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
      }
      label5890:
      if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
        break label1079;
      }
      this.actionBarLayout.addFragmentToStack(new DialogsActivity(null));
      this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
      break label1079;
      label5931:
      if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
        break label1079;
      }
      if (!UserConfig.isClientActivated())
      {
        this.actionBarLayout.addFragmentToStack(new LoginActivity());
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        break label1079;
      }
      this.actionBarLayout.addFragmentToStack(new DialogsActivity(null));
      this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
      break label1079;
      label6005:
      i += 1;
      localObject6 = localObject8;
      break label555;
      l = 0L;
      break;
      label6024:
      i = 0;
      break label1281;
      label6030:
      i = j + 1;
      continue;
      label6039:
      j = 0;
    }
  }
  
  private void onFinish()
  {
    if (this.finished) {
      return;
    }
    this.finished = true;
    if (this.lockRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
      this.lockRunnable = null;
    }
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mainUserInfoChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeOtherAppActivities);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didUpdatedConnectionState);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.needShowAlert);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
  }
  
  private void onPasscodePause()
  {
    if (this.lockRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
      this.lockRunnable = null;
    }
    if (UserConfig.passcodeHash.length() != 0)
    {
      UserConfig.lastPauseTime = ConnectionsManager.getInstance().getCurrentTime();
      this.lockRunnable = new Runnable()
      {
        public void run()
        {
          if (LaunchActivity.this.lockRunnable == this)
          {
            if (!AndroidUtilities.needShowPasscode(true)) {
              break label42;
            }
            FileLog.e("tmessages", "lock app");
            LaunchActivity.this.showPasscodeActivity();
          }
          for (;;)
          {
            LaunchActivity.access$1502(LaunchActivity.this, null);
            return;
            label42:
            FileLog.e("tmessages", "didn't pass lock check");
          }
        }
      };
      if (UserConfig.appLocked) {
        AndroidUtilities.runOnUIThread(this.lockRunnable, 1000L);
      }
    }
    for (;;)
    {
      UserConfig.saveConfig(false);
      return;
      if (UserConfig.autoLockIn != 0)
      {
        AndroidUtilities.runOnUIThread(this.lockRunnable, UserConfig.autoLockIn * 1000L + 1000L);
        continue;
        UserConfig.lastPauseTime = 0;
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
    if (UserConfig.lastPauseTime != 0)
    {
      UserConfig.lastPauseTime = 0;
      UserConfig.saveConfig(false);
    }
  }
  
  private void runLinkRequest(final String paramString1, final String paramString2, final String paramString3, final String paramString4, final String paramString5, final String paramString6, final boolean paramBoolean, final Integer paramInteger, final String paramString7, int paramInt)
  {
    final ProgressDialog localProgressDialog = new ProgressDialog(this);
    localProgressDialog.setMessage(LocaleController.getString("Loading", 2131165834));
    localProgressDialog.setCanceledOnTouchOutside(false);
    localProgressDialog.setCancelable(false);
    int j = 0;
    final int i;
    if (paramString1 != null)
    {
      paramString2 = new TLRPC.TL_contacts_resolveUsername();
      paramString2.username = paramString1;
      i = ConnectionsManager.getInstance().sendRequest(paramString2, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (!LaunchActivity.this.isFinishing()) {}
              Object localObject2;
              label953:
              label957:
              for (;;)
              {
                try
                {
                  LaunchActivity.8.this.val$progressDialog.dismiss();
                  final TLRPC.TL_contacts_resolvedPeer localTL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer)paramAnonymousTLObject;
                  if ((paramAnonymousTL_error != null) || (LaunchActivity.this.actionBarLayout == null) || ((LaunchActivity.8.this.val$game != null) && ((LaunchActivity.8.this.val$game == null) || (localTL_contacts_resolvedPeer.users.isEmpty())))) {
                    break label1003;
                  }
                  MessagesController.getInstance().putUsers(localTL_contacts_resolvedPeer.users, false);
                  MessagesController.getInstance().putChats(localTL_contacts_resolvedPeer.chats, false);
                  MessagesStorage.getInstance().putUsersAndChats(localTL_contacts_resolvedPeer.users, localTL_contacts_resolvedPeer.chats, false, true);
                  if (LaunchActivity.8.this.val$game != null)
                  {
                    localObject2 = new Bundle();
                    ((Bundle)localObject2).putBoolean("onlySelect", true);
                    ((Bundle)localObject2).putBoolean("cantSendToChannels", true);
                    ((Bundle)localObject2).putInt("dialogsType", 1);
                    ((Bundle)localObject2).putString("selectAlertString", LocaleController.getString("SendGameTo", 2131166237));
                    ((Bundle)localObject2).putString("selectAlertStringGroup", LocaleController.getString("SendGameToGroup", 2131166238));
                    localObject2 = new DialogsActivity((Bundle)localObject2);
                    ((DialogsActivity)localObject2).setDelegate(new DialogsActivity.DialogsActivityDelegate()
                    {
                      public void didSelectDialog(DialogsActivity paramAnonymous3DialogsActivity, long paramAnonymous3Long, boolean paramAnonymous3Boolean)
                      {
                        Object localObject = new TLRPC.TL_inputMediaGame();
                        ((TLRPC.TL_inputMediaGame)localObject).id = new TLRPC.TL_inputGameShortName();
                        ((TLRPC.TL_inputMediaGame)localObject).id.short_name = LaunchActivity.8.this.val$game;
                        ((TLRPC.TL_inputMediaGame)localObject).id.bot_id = MessagesController.getInputUser((TLRPC.User)localTL_contacts_resolvedPeer.users.get(0));
                        SendMessagesHelper.getInstance().sendGame(MessagesController.getInputPeer((int)paramAnonymous3Long), (TLRPC.TL_inputMediaGame)localObject, 0L, 0L);
                        localObject = new Bundle();
                        ((Bundle)localObject).putBoolean("scrollToTopOnResume", true);
                        int i = (int)paramAnonymous3Long;
                        int j = (int)(paramAnonymous3Long >> 32);
                        if (i != 0) {
                          if (j == 1) {
                            ((Bundle)localObject).putInt("chat_id", i);
                          }
                        }
                        for (;;)
                        {
                          if (MessagesController.checkCanOpenChat((Bundle)localObject, paramAnonymous3DialogsActivity))
                          {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity((Bundle)localObject), true, false, true);
                          }
                          return;
                          if (i > 0)
                          {
                            ((Bundle)localObject).putInt("user_id", i);
                          }
                          else if (i < 0)
                          {
                            ((Bundle)localObject).putInt("chat_id", -i);
                            continue;
                            ((Bundle)localObject).putInt("enc_id", j);
                          }
                        }
                      }
                    });
                    if (AndroidUtilities.isTablet()) {
                      if ((LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() > 0) && ((LaunchActivity.this.layersActionBarLayout.fragmentsStack.get(LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity)))
                      {
                        bool = true;
                        LaunchActivity.this.actionBarLayout.presentFragment((BaseFragment)localObject2, bool, true, true);
                        if (PhotoViewer.getInstance().isVisible()) {
                          PhotoViewer.getInstance().closePhoto(false, true);
                        }
                        LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
                        if (!AndroidUtilities.isTablet()) {
                          continue;
                        }
                        LaunchActivity.this.actionBarLayout.showLastFragment();
                        LaunchActivity.this.rightActionBarLayout.showLastFragment();
                        return;
                      }
                    }
                  }
                }
                catch (Exception localException1)
                {
                  FileLog.e("tmessages", localException1);
                  continue;
                  boolean bool = false;
                  continue;
                  if ((LaunchActivity.this.actionBarLayout.fragmentsStack.size() > 1) && ((LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1) instanceof DialogsActivity)))
                  {
                    bool = true;
                    continue;
                  }
                  bool = false;
                  continue;
                  LaunchActivity.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
                  return;
                }
                if (LaunchActivity.8.this.val$botChat != null)
                {
                  if (!localException1.users.isEmpty()) {}
                  for (TLRPC.User localUser = (TLRPC.User)localException1.users.get(0); (localUser == null) || ((localUser.bot) && (localUser.bot_nochats)); localObject1 = null) {
                    try
                    {
                      Toast.makeText(LaunchActivity.this, LocaleController.getString("BotCantJoinGroups", 2131165366), 0).show();
                      return;
                    }
                    catch (Exception localException2)
                    {
                      FileLog.e("tmessages", localException2);
                      return;
                    }
                  }
                  localObject2 = new Bundle();
                  ((Bundle)localObject2).putBoolean("onlySelect", true);
                  ((Bundle)localObject2).putInt("dialogsType", 2);
                  ((Bundle)localObject2).putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", 2131165274, new Object[] { UserObject.getUserName((TLRPC.User)localObject1), "%1$s" }));
                  localObject2 = new DialogsActivity((Bundle)localObject2);
                  ((DialogsActivity)localObject2).setDelegate(new DialogsActivity.DialogsActivityDelegate()
                  {
                    public void didSelectDialog(DialogsActivity paramAnonymous3DialogsActivity, long paramAnonymous3Long, boolean paramAnonymous3Boolean)
                    {
                      paramAnonymous3DialogsActivity = new Bundle();
                      paramAnonymous3DialogsActivity.putBoolean("scrollToTopOnResume", true);
                      paramAnonymous3DialogsActivity.putInt("chat_id", -(int)paramAnonymous3Long);
                      if ((LaunchActivity.mainFragmentsStack.isEmpty()) || (MessagesController.checkCanOpenChat(paramAnonymous3DialogsActivity, (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))))
                      {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        MessagesController.getInstance().addUserToChat(-(int)paramAnonymous3Long, localObject1, null, 0, LaunchActivity.8.this.val$botChat, null);
                        LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(paramAnonymous3DialogsActivity), true, false, true);
                      }
                    }
                  });
                  LaunchActivity.this.presentFragment((BaseFragment)localObject2);
                  return;
                }
                int j = 0;
                localObject2 = new Bundle();
                long l;
                int i;
                if (!((TLRPC.TL_contacts_resolvedPeer)localObject1).chats.isEmpty())
                {
                  ((Bundle)localObject2).putInt("chat_id", ((TLRPC.Chat)((TLRPC.TL_contacts_resolvedPeer)localObject1).chats.get(0)).id);
                  l = -((TLRPC.Chat)((TLRPC.TL_contacts_resolvedPeer)localObject1).chats.get(0)).id;
                  i = j;
                  if (LaunchActivity.8.this.val$botUser != null)
                  {
                    i = j;
                    if (((TLRPC.TL_contacts_resolvedPeer)localObject1).users.size() > 0)
                    {
                      i = j;
                      if (((TLRPC.User)((TLRPC.TL_contacts_resolvedPeer)localObject1).users.get(0)).bot)
                      {
                        ((Bundle)localObject2).putString("botUser", LaunchActivity.8.this.val$botUser);
                        i = 1;
                      }
                    }
                  }
                  if (LaunchActivity.8.this.val$messageId != null) {
                    ((Bundle)localObject2).putInt("message_id", LaunchActivity.8.this.val$messageId.intValue());
                  }
                  if (LaunchActivity.mainFragmentsStack.isEmpty()) {
                    break label953;
                  }
                }
                for (localObject1 = (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1);; localObject1 = null)
                {
                  if ((localObject1 != null) && (!MessagesController.checkCanOpenChat((Bundle)localObject2, (BaseFragment)localObject1))) {
                    break label957;
                  }
                  if ((i == 0) || (localObject1 == null) || (!(localObject1 instanceof ChatActivity)) || (((ChatActivity)localObject1).getDialogId() != l)) {
                    break label959;
                  }
                  ((ChatActivity)localObject1).setBotUser(LaunchActivity.8.this.val$botUser);
                  return;
                  ((Bundle)localObject2).putInt("user_id", ((TLRPC.User)((TLRPC.TL_contacts_resolvedPeer)localObject1).users.get(0)).id);
                  l = ((TLRPC.User)((TLRPC.TL_contacts_resolvedPeer)localObject1).users.get(0)).id;
                  break;
                }
              }
              label959:
              final Object localObject1 = new ChatActivity((Bundle)localObject2);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
              LaunchActivity.this.actionBarLayout.presentFragment((BaseFragment)localObject1, false, true, true);
              return;
              try
              {
                label1003:
                Toast.makeText(LaunchActivity.this, LocaleController.getString("NoUsernameFound", 2131165958), 0).show();
                return;
              }
              catch (Exception localException3)
              {
                FileLog.e("tmessages", localException3);
              }
            }
          });
        }
      });
    }
    for (;;)
    {
      if (i != 0)
      {
        localProgressDialog.setButton(-2, LocaleController.getString("Cancel", 2131165386), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            ConnectionsManager.getInstance().cancelRequest(i, true);
            try
            {
              paramAnonymousDialogInterface.dismiss();
              return;
            }
            catch (Exception paramAnonymousDialogInterface)
            {
              FileLog.e("tmessages", paramAnonymousDialogInterface);
            }
          }
        });
        localProgressDialog.show();
      }
      do
      {
        return;
        if (paramString2 != null)
        {
          if (paramInt == 0)
          {
            TLRPC.TL_messages_checkChatInvite localTL_messages_checkChatInvite = new TLRPC.TL_messages_checkChatInvite();
            localTL_messages_checkChatInvite.hash = paramString2;
            i = ConnectionsManager.getInstance().sendRequest(localTL_messages_checkChatInvite, new RequestDelegate()
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
                      LaunchActivity.9.this.val$progressDialog.dismiss();
                      if ((paramAnonymousTL_error == null) && (LaunchActivity.this.actionBarLayout != null))
                      {
                        Object localObject1 = (TLRPC.ChatInvite)paramAnonymousTLObject;
                        if ((((TLRPC.ChatInvite)localObject1).chat != null) && (!ChatObject.isLeftFromChat(((TLRPC.ChatInvite)localObject1).chat)))
                        {
                          MessagesController.getInstance().putChat(((TLRPC.ChatInvite)localObject1).chat, false);
                          localObject3 = new ArrayList();
                          ((ArrayList)localObject3).add(((TLRPC.ChatInvite)localObject1).chat);
                          MessagesStorage.getInstance().putUsersAndChats(null, (ArrayList)localObject3, false, true);
                          localObject3 = new Bundle();
                          ((Bundle)localObject3).putInt("chat_id", ((TLRPC.ChatInvite)localObject1).chat.id);
                          if ((LaunchActivity.mainFragmentsStack.isEmpty()) || (MessagesController.checkCanOpenChat((Bundle)localObject3, (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))))
                          {
                            localObject1 = new ChatActivity((Bundle)localObject3);
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
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
                        FileLog.e("tmessages", localException);
                      }
                      if (((localException.chat == null) && ((!localException.channel) || (localException.megagroup))) || ((localException.chat != null) && ((!ChatObject.isChannel(localException.chat)) || (localException.chat.megagroup)) && (!LaunchActivity.mainFragmentsStack.isEmpty())))
                      {
                        localObject3 = (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1);
                        ((BaseFragment)localObject3).showDialog(new JoinGroupAlert(LaunchActivity.this, localException, LaunchActivity.9.this.val$group, (BaseFragment)localObject3));
                        return;
                      }
                      Object localObject3 = new AlertDialog.Builder(LaunchActivity.this);
                      ((AlertDialog.Builder)localObject3).setTitle(LocaleController.getString("AppName", 2131165299));
                      if (((!localException.megagroup) && (localException.channel)) || ((ChatObject.isChannel(localException.chat)) && (!localException.chat.megagroup)))
                      {
                        if (localException.chat != null) {}
                        for (localObject2 = localException.chat.title;; localObject2 = ((TLRPC.ChatInvite)localObject2).title)
                        {
                          ((AlertDialog.Builder)localObject3).setMessage(LocaleController.formatString("ChannelJoinTo", 2131165430, new Object[] { localObject2 }));
                          ((AlertDialog.Builder)localObject3).setPositiveButton(LocaleController.getString("OK", 2131166044), new DialogInterface.OnClickListener()
                          {
                            public void onClick(DialogInterface paramAnonymous3DialogInterface, int paramAnonymous3Int)
                            {
                              LaunchActivity.this.runLinkRequest(LaunchActivity.9.this.val$username, LaunchActivity.9.this.val$group, LaunchActivity.9.this.val$sticker, LaunchActivity.9.this.val$botUser, LaunchActivity.9.this.val$botChat, LaunchActivity.9.this.val$message, LaunchActivity.9.this.val$hasUrl, LaunchActivity.9.this.val$messageId, LaunchActivity.9.this.val$game, 1);
                            }
                          });
                          ((AlertDialog.Builder)localObject3).setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
                          LaunchActivity.this.showAlertDialog((AlertDialog.Builder)localObject3);
                          return;
                        }
                      }
                      if (((TLRPC.ChatInvite)localObject2).chat != null) {}
                      for (localObject2 = ((TLRPC.ChatInvite)localObject2).chat.title;; localObject2 = ((TLRPC.ChatInvite)localObject2).title)
                      {
                        ((AlertDialog.Builder)localObject3).setMessage(LocaleController.formatString("JoinToGroup", 2131165775, new Object[] { localObject2 }));
                        break;
                      }
                      localObject2 = new AlertDialog.Builder(LaunchActivity.this);
                      ((AlertDialog.Builder)localObject2).setTitle(LocaleController.getString("AppName", 2131165299));
                      if (!paramAnonymousTL_error.text.startsWith("FLOOD_WAIT")) {
                        break label592;
                      }
                    }
                    ((AlertDialog.Builder)localObject2).setMessage(LocaleController.getString("FloodWait", 2131165639));
                    for (;;)
                    {
                      ((AlertDialog.Builder)localObject2).setPositiveButton(LocaleController.getString("OK", 2131166044), null);
                      LaunchActivity.this.showAlertDialog((AlertDialog.Builder)localObject2);
                      return;
                      label592:
                      ((AlertDialog.Builder)localObject2).setMessage(LocaleController.getString("JoinToGroupErrorNotExist", 2131165777));
                    }
                  }
                });
              }
            }, 2);
            break;
          }
          i = j;
          if (paramInt != 1) {
            break;
          }
          paramString1 = new TLRPC.TL_messages_importChatInvite();
          paramString1.hash = paramString2;
          ConnectionsManager.getInstance().sendRequest(paramString1, new RequestDelegate()
          {
            public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
            {
              if (paramAnonymousTL_error == null)
              {
                TLRPC.Updates localUpdates = (TLRPC.Updates)paramAnonymousTLObject;
                MessagesController.getInstance().processUpdates(localUpdates, false);
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if (!LaunchActivity.this.isFinishing()) {}
                  AlertDialog.Builder localBuilder;
                  try
                  {
                    LaunchActivity.10.this.val$progressDialog.dismiss();
                    if (paramAnonymousTL_error == null)
                    {
                      if (LaunchActivity.this.actionBarLayout != null)
                      {
                        Object localObject2 = (TLRPC.Updates)paramAnonymousTLObject;
                        if (!((TLRPC.Updates)localObject2).chats.isEmpty())
                        {
                          Object localObject1 = (TLRPC.Chat)((TLRPC.Updates)localObject2).chats.get(0);
                          ((TLRPC.Chat)localObject1).left = false;
                          ((TLRPC.Chat)localObject1).kicked = false;
                          MessagesController.getInstance().putUsers(((TLRPC.Updates)localObject2).users, false);
                          MessagesController.getInstance().putChats(((TLRPC.Updates)localObject2).chats, false);
                          localObject2 = new Bundle();
                          ((Bundle)localObject2).putInt("chat_id", ((TLRPC.Chat)localObject1).id);
                          if ((LaunchActivity.mainFragmentsStack.isEmpty()) || (MessagesController.checkCanOpenChat((Bundle)localObject2, (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))))
                          {
                            localObject1 = new ChatActivity((Bundle)localObject2);
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            LaunchActivity.this.actionBarLayout.presentFragment((BaseFragment)localObject1, false, true, true);
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
                      FileLog.e("tmessages", localException);
                    }
                    localBuilder = new AlertDialog.Builder(LaunchActivity.this);
                    localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
                    if (!paramAnonymousTL_error.text.startsWith("FLOOD_WAIT")) {
                      break label287;
                    }
                  }
                  localBuilder.setMessage(LocaleController.getString("FloodWait", 2131165639));
                  for (;;)
                  {
                    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
                    LaunchActivity.this.showAlertDialog(localBuilder);
                    return;
                    label287:
                    if (paramAnonymousTL_error.text.equals("USERS_TOO_MUCH")) {
                      localBuilder.setMessage(LocaleController.getString("JoinToGroupErrorFull", 2131165776));
                    } else {
                      localBuilder.setMessage(LocaleController.getString("JoinToGroupErrorNotExist", 2131165777));
                    }
                  }
                }
              });
            }
          }, 2);
          i = j;
          break;
        }
        if (paramString3 == null) {
          break label294;
        }
      } while (mainFragmentsStack.isEmpty());
      paramString1 = new TLRPC.TL_inputStickerSetShortName();
      paramString1.short_name = paramString3;
      paramString2 = (BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1);
      paramString2.showDialog(new StickersAlert(this, paramString2, paramString1, null, null));
      return;
      label294:
      i = j;
      if (paramString6 != null)
      {
        paramString1 = new Bundle();
        paramString1.putBoolean("onlySelect", true);
        paramString1 = new DialogsActivity(paramString1);
        paramString1.setDelegate(new DialogsActivity.DialogsActivityDelegate()
        {
          public void didSelectDialog(DialogsActivity paramAnonymousDialogsActivity, long paramAnonymousLong, boolean paramAnonymousBoolean)
          {
            Bundle localBundle = new Bundle();
            localBundle.putBoolean("scrollToTopOnResume", true);
            localBundle.putBoolean("hasUrl", paramBoolean);
            int i = (int)paramAnonymousLong;
            int j = (int)(paramAnonymousLong >> 32);
            if (i != 0) {
              if (j == 1) {
                localBundle.putInt("chat_id", i);
              }
            }
            for (;;)
            {
              if (MessagesController.checkCanOpenChat(localBundle, paramAnonymousDialogsActivity))
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                DraftQuery.saveDraft(paramAnonymousLong, paramString6, null, null, true);
                LaunchActivity.this.actionBarLayout.presentFragment(new ChatActivity(localBundle), true, false, true);
              }
              return;
              if (i > 0)
              {
                localBundle.putInt("user_id", i);
              }
              else if (i < 0)
              {
                localBundle.putInt("chat_id", -i);
                continue;
                localBundle.putInt("enc_id", j);
              }
            }
          }
        });
        presentFragment(paramString1, false, true);
        i = j;
      }
    }
  }
  
  private void showPasscodeActivity()
  {
    if (this.passcodeView == null) {
      return;
    }
    UserConfig.appLocked = true;
    if (PhotoViewer.getInstance().isVisible()) {
      PhotoViewer.getInstance().closePhoto(false, true);
    }
    this.passcodeView.onShow();
    UserConfig.isWaitingForPasscodeEnter = true;
    this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
    this.passcodeView.setDelegate(new PasscodeView.PasscodeViewDelegate()
    {
      public void didAcceptedPassword()
      {
        UserConfig.isWaitingForPasscodeEnter = false;
        if (LaunchActivity.this.passcodeSaveIntent != null)
        {
          LaunchActivity.this.handleIntent(LaunchActivity.this.passcodeSaveIntent, LaunchActivity.this.passcodeSaveIntentIsNew, LaunchActivity.this.passcodeSaveIntentIsRestore, true);
          LaunchActivity.access$702(LaunchActivity.this, null);
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
  }
  
  private void updateCurrentConnectionState()
  {
    String str = null;
    if (this.currentConnectionState == 2) {
      str = LocaleController.getString("WaitingForNetwork", 2131166387);
    }
    for (;;)
    {
      this.actionBarLayout.setTitleOverlayText(str);
      return;
      if (this.currentConnectionState == 1) {
        str = LocaleController.getString("Connecting", 2131165519);
      } else if (this.currentConnectionState == 4) {
        str = LocaleController.getString("Updating", 2131166360);
      }
    }
  }
  
  public void didReceivedNotification(int paramInt, final Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.appDidLogout)
    {
      if (this.drawerLayoutAdapter != null) {
        this.drawerLayoutAdapter.notifyDataSetChanged();
      }
      paramVarArgs = this.actionBarLayout.fragmentsStack.iterator();
      while (paramVarArgs.hasNext()) {
        ((BaseFragment)paramVarArgs.next()).onFragmentDestroy();
      }
      this.actionBarLayout.fragmentsStack.clear();
      if (AndroidUtilities.isTablet())
      {
        paramVarArgs = this.layersActionBarLayout.fragmentsStack.iterator();
        while (paramVarArgs.hasNext()) {
          ((BaseFragment)paramVarArgs.next()).onFragmentDestroy();
        }
        this.layersActionBarLayout.fragmentsStack.clear();
        paramVarArgs = this.rightActionBarLayout.fragmentsStack.iterator();
        while (paramVarArgs.hasNext()) {
          ((BaseFragment)paramVarArgs.next()).onFragmentDestroy();
        }
        this.rightActionBarLayout.fragmentsStack.clear();
      }
      startActivity(new Intent(this, IntroActivity.class));
      onFinish();
      finish();
    }
    do
    {
      do
      {
        Object localObject;
        do
        {
          for (;;)
          {
            return;
            if (paramInt == NotificationCenter.closeOtherAppActivities)
            {
              if (paramVarArgs[0] != this)
              {
                onFinish();
                finish();
              }
            }
            else if (paramInt == NotificationCenter.didUpdatedConnectionState)
            {
              paramInt = ConnectionsManager.getInstance().getConnectionState();
              if (this.currentConnectionState != paramInt)
              {
                FileLog.d("tmessages", "switch to state " + paramInt);
                this.currentConnectionState = paramInt;
                updateCurrentConnectionState();
              }
            }
            else
            {
              if (paramInt == NotificationCenter.mainUserInfoChanged)
              {
                this.drawerLayoutAdapter.notifyDataSetChanged();
                return;
              }
              if (paramInt != NotificationCenter.needShowAlert) {
                break;
              }
              localObject = (Integer)paramVarArgs[0];
              AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
              localBuilder.setTitle(LocaleController.getString("AppName", 2131165299));
              localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
              if (((Integer)localObject).intValue() != 2) {
                localBuilder.setNegativeButton(LocaleController.getString("MoreInfo", 2131165905), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                  {
                    if (!LaunchActivity.mainFragmentsStack.isEmpty()) {
                      MessagesController.openByUserName("spambot", (BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1), 1);
                    }
                  }
                });
              }
              if (((Integer)localObject).intValue() == 0) {
                localBuilder.setMessage(LocaleController.getString("NobodyLikesSpam1", 2131165960));
              }
              while (!mainFragmentsStack.isEmpty())
              {
                ((BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(localBuilder.create());
                return;
                if (((Integer)localObject).intValue() == 1) {
                  localBuilder.setMessage(LocaleController.getString("NobodyLikesSpam2", 2131165961));
                } else if (((Integer)localObject).intValue() == 2) {
                  localBuilder.setMessage((String)paramVarArgs[1]);
                }
              }
            }
          }
          if (paramInt != NotificationCenter.wasUnableToFindCurrentLocation) {
            break;
          }
          paramVarArgs = (HashMap)paramVarArgs[0];
          localObject = new AlertDialog.Builder(this);
          ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165299));
          ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166044), null);
          ((AlertDialog.Builder)localObject).setNegativeButton(LocaleController.getString("ShareYouLocationUnableManually", 2131166283), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
            {
              if (LaunchActivity.mainFragmentsStack.isEmpty()) {}
              while (!AndroidUtilities.isGoogleMapsInstalled((BaseFragment)LaunchActivity.mainFragmentsStack.get(LaunchActivity.mainFragmentsStack.size() - 1))) {
                return;
              }
              paramAnonymousDialogInterface = new LocationActivity();
              paramAnonymousDialogInterface.setDelegate(new LocationActivity.LocationActivityDelegate()
              {
                public void didSelectLocation(TLRPC.MessageMedia paramAnonymous2MessageMedia)
                {
                  Iterator localIterator = LaunchActivity.16.this.val$waitingForLocation.entrySet().iterator();
                  while (localIterator.hasNext())
                  {
                    MessageObject localMessageObject = (MessageObject)((Map.Entry)localIterator.next()).getValue();
                    SendMessagesHelper.getInstance().sendMessage(paramAnonymous2MessageMedia, localMessageObject.getDialogId(), localMessageObject, null, null);
                  }
                }
              });
              LaunchActivity.this.presentFragment(paramAnonymousDialogInterface);
            }
          });
          ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("ShareYouLocationUnable", 2131166282));
        } while (mainFragmentsStack.isEmpty());
        ((BaseFragment)mainFragmentsStack.get(mainFragmentsStack.size() - 1)).showDialog(((AlertDialog.Builder)localObject).create());
        return;
      } while ((paramInt != NotificationCenter.didSetNewWallpapper) || (this.listView == null));
      paramVarArgs = this.listView.getChildAt(0);
    } while (paramVarArgs == null);
    paramVarArgs.invalidate();
  }
  
  public void didSelectDialog(DialogsActivity paramDialogsActivity, long paramLong, boolean paramBoolean)
  {
    int i;
    int j;
    if (paramLong != 0L)
    {
      i = (int)paramLong;
      j = (int)(paramLong >> 32);
      localObject1 = new Bundle();
      ((Bundle)localObject1).putBoolean("scrollToTopOnResume", true);
      if (!AndroidUtilities.isTablet()) {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
      }
      if (i == 0) {
        break label122;
      }
      if (j != 1) {
        break label85;
      }
      ((Bundle)localObject1).putInt("chat_id", i);
    }
    while (!MessagesController.checkCanOpenChat((Bundle)localObject1, paramDialogsActivity))
    {
      return;
      label85:
      if (i > 0)
      {
        ((Bundle)localObject1).putInt("user_id", i);
      }
      else if (i < 0)
      {
        ((Bundle)localObject1).putInt("chat_id", -i);
        continue;
        label122:
        ((Bundle)localObject1).putInt("enc_id", j);
      }
    }
    Object localObject1 = new ChatActivity((Bundle)localObject1);
    if (this.videoPath != null)
    {
      if (Build.VERSION.SDK_INT >= 16)
      {
        if (AndroidUtilities.isTablet())
        {
          this.actionBarLayout.presentFragment((BaseFragment)localObject1, false, true, true);
          localObject2 = this.videoPath;
          if (paramDialogsActivity == null) {
            break label277;
          }
        }
        label277:
        for (paramBoolean = true;; paramBoolean = false)
        {
          if ((!((ChatActivity)localObject1).openVideoEditor((String)localObject2, paramBoolean, false)) && (paramDialogsActivity != null) && (!AndroidUtilities.isTablet())) {
            paramDialogsActivity.finishFragment(true);
          }
          this.photoPathsArray = null;
          this.videoPath = null;
          this.sendingText = null;
          this.documentsPathsArray = null;
          this.documentsOriginalPathsArray = null;
          this.contactsToSend = null;
          return;
          this.actionBarLayout.addFragmentToStack((BaseFragment)localObject1, this.actionBarLayout.fragmentsStack.size() - 1);
          break;
        }
      }
      localObject2 = this.actionBarLayout;
      if (paramDialogsActivity != null)
      {
        paramBoolean = true;
        label296:
        if (paramDialogsActivity != null) {
          break label339;
        }
      }
      label339:
      for (bool = true;; bool = false)
      {
        ((ActionBarLayout)localObject2).presentFragment((BaseFragment)localObject1, paramBoolean, bool, true);
        SendMessagesHelper.prepareSendingVideo(this.videoPath, 0L, 0L, 0, 0, null, paramLong, null);
        break;
        paramBoolean = false;
        break label296;
      }
    }
    Object localObject2 = this.actionBarLayout;
    if (paramDialogsActivity != null)
    {
      paramBoolean = true;
      label358:
      if (paramDialogsActivity != null) {
        break label578;
      }
    }
    label578:
    for (boolean bool = true;; bool = false)
    {
      ((ActionBarLayout)localObject2).presentFragment((BaseFragment)localObject1, paramBoolean, bool, true);
      if (this.photoPathsArray != null)
      {
        localObject1 = null;
        paramDialogsActivity = (DialogsActivity)localObject1;
        if (this.sendingText != null)
        {
          paramDialogsActivity = (DialogsActivity)localObject1;
          if (this.sendingText.length() <= 200)
          {
            paramDialogsActivity = (DialogsActivity)localObject1;
            if (this.photoPathsArray.size() == 1)
            {
              paramDialogsActivity = new ArrayList();
              paramDialogsActivity.add(this.sendingText);
              this.sendingText = null;
            }
          }
        }
        SendMessagesHelper.prepareSendingPhotos(null, this.photoPathsArray, paramLong, null, paramDialogsActivity, null);
      }
      if (this.sendingText != null) {
        SendMessagesHelper.prepareSendingText(this.sendingText, paramLong);
      }
      if ((this.documentsPathsArray != null) || (this.documentsUrisArray != null)) {
        SendMessagesHelper.prepareSendingDocuments(this.documentsPathsArray, this.documentsOriginalPathsArray, this.documentsUrisArray, this.documentsMimeType, paramLong, null);
      }
      if ((this.contactsToSend == null) || (this.contactsToSend.isEmpty())) {
        break;
      }
      paramDialogsActivity = this.contactsToSend.iterator();
      while (paramDialogsActivity.hasNext())
      {
        localObject1 = (TLRPC.User)paramDialogsActivity.next();
        SendMessagesHelper.getInstance().sendMessage((TLRPC.User)localObject1, paramLong, null, null, null);
      }
      break;
      paramBoolean = false;
      break label358;
    }
  }
  
  public boolean needAddFragmentToStack(BaseFragment paramBaseFragment, ActionBarLayout paramActionBarLayout)
  {
    if (AndroidUtilities.isTablet())
    {
      DrawerLayoutContainer localDrawerLayoutContainer = this.drawerLayoutContainer;
      if ((!(paramBaseFragment instanceof LoginActivity)) && (!(paramBaseFragment instanceof CountrySelectActivity)) && (this.layersActionBarLayout.getVisibility() != 0))
      {
        bool = true;
        localDrawerLayoutContainer.setAllowOpenDrawer(bool, true);
        if (!(paramBaseFragment instanceof DialogsActivity)) {
          break label157;
        }
        if ((!((DialogsActivity)paramBaseFragment).isMainDialogList()) || (paramActionBarLayout == this.actionBarLayout)) {
          break label457;
        }
        this.actionBarLayout.removeAllFragments();
        this.actionBarLayout.addFragmentToStack(paramBaseFragment);
        this.layersActionBarLayout.removeAllFragments();
        this.layersActionBarLayout.setVisibility(8);
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        if (!this.tabletFullSize)
        {
          this.shadowTabletSide.setVisibility(0);
          if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
            this.backgroundTablet.setVisibility(0);
          }
        }
      }
      label157:
      label282:
      do
      {
        do
        {
          return false;
          bool = false;
          break;
          if (!(paramBaseFragment instanceof ChatActivity)) {
            break label376;
          }
          if ((this.tabletFullSize) || (paramActionBarLayout == this.rightActionBarLayout)) {
            break label282;
          }
          this.rightActionBarLayout.setVisibility(0);
          this.backgroundTablet.setVisibility(8);
          this.rightActionBarLayout.removeAllFragments();
          this.rightActionBarLayout.addFragmentToStack(paramBaseFragment);
        } while (this.layersActionBarLayout.fragmentsStack.isEmpty());
        for (i = 0; this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
          this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
        }
        this.layersActionBarLayout.closeLastFragment(true);
        return false;
        if ((!this.tabletFullSize) || (paramActionBarLayout == this.actionBarLayout)) {
          break label457;
        }
        this.actionBarLayout.addFragmentToStack(paramBaseFragment);
      } while (this.layersActionBarLayout.fragmentsStack.isEmpty());
      for (int i = 0; this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
      }
      this.layersActionBarLayout.closeLastFragment(true);
      return false;
      label376:
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
          this.layersActionBarLayout.addFragmentToStack(paramBaseFragment);
          return false;
          this.shadowTablet.setBackgroundColor(2130706432);
        }
      }
      label457:
      return true;
    }
    paramActionBarLayout = this.drawerLayoutContainer;
    if ((!(paramBaseFragment instanceof LoginActivity)) && (!(paramBaseFragment instanceof CountrySelectActivity))) {}
    for (boolean bool = true;; bool = false)
    {
      paramActionBarLayout.setAllowOpenDrawer(bool, false);
      return true;
    }
  }
  
  public boolean needCloseLastFragment(ActionBarLayout paramActionBarLayout)
  {
    if (AndroidUtilities.isTablet())
    {
      if ((paramActionBarLayout == this.actionBarLayout) && (paramActionBarLayout.fragmentsStack.size() <= 1))
      {
        onFinish();
        finish();
        return false;
      }
      if (paramActionBarLayout == this.rightActionBarLayout) {
        if (!this.tabletFullSize) {
          this.backgroundTablet.setVisibility(0);
        }
      }
    }
    while (paramActionBarLayout.fragmentsStack.size() > 1)
    {
      do
      {
        return true;
      } while ((paramActionBarLayout != this.layersActionBarLayout) || (!this.actionBarLayout.fragmentsStack.isEmpty()) || (this.layersActionBarLayout.fragmentsStack.size() != 1));
      onFinish();
      finish();
      return false;
    }
    onFinish();
    finish();
    return false;
  }
  
  public boolean needPresentFragment(BaseFragment paramBaseFragment, boolean paramBoolean1, boolean paramBoolean2, ActionBarLayout paramActionBarLayout)
  {
    boolean bool5 = true;
    boolean bool3 = true;
    boolean bool4 = true;
    boolean bool2 = true;
    if (AndroidUtilities.isTablet())
    {
      DrawerLayoutContainer localDrawerLayoutContainer = this.drawerLayoutContainer;
      boolean bool1;
      if ((!(paramBaseFragment instanceof LoginActivity)) && (!(paramBaseFragment instanceof CountrySelectActivity)) && (this.layersActionBarLayout.getVisibility() != 0))
      {
        bool1 = true;
        localDrawerLayoutContainer.setAllowOpenDrawer(bool1, true);
        if ((!(paramBaseFragment instanceof DialogsActivity)) || (!((DialogsActivity)paramBaseFragment).isMainDialogList()) || (paramActionBarLayout == this.actionBarLayout)) {
          break label173;
        }
        this.actionBarLayout.removeAllFragments();
        this.actionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, false);
        this.layersActionBarLayout.removeAllFragments();
        this.layersActionBarLayout.setVisibility(8);
        this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
        if (!this.tabletFullSize)
        {
          this.shadowTabletSide.setVisibility(0);
          if (this.rightActionBarLayout.fragmentsStack.isEmpty()) {
            this.backgroundTablet.setVisibility(0);
          }
        }
      }
      label173:
      label351:
      do
      {
        return false;
        bool1 = false;
        break;
        if (!(paramBaseFragment instanceof ChatActivity)) {
          break label762;
        }
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
              break label351;
            }
          }
          for (bool1 = bool2;; bool1 = false)
          {
            paramActionBarLayout.closeLastFragment(bool1);
            if (!paramBoolean1) {
              this.actionBarLayout.presentFragment(paramBaseFragment, false, paramBoolean2, false);
            }
            return paramBoolean1;
          }
        }
        if ((this.tabletFullSize) || (paramActionBarLayout == this.rightActionBarLayout)) {
          break label496;
        }
        this.rightActionBarLayout.setVisibility(0);
        this.backgroundTablet.setVisibility(8);
        this.rightActionBarLayout.removeAllFragments();
        this.rightActionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, true, false);
      } while (this.layersActionBarLayout.fragmentsStack.isEmpty());
      for (int i = 0; this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
        this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
      }
      paramBaseFragment = this.layersActionBarLayout;
      if (!paramBoolean2) {}
      for (paramBoolean1 = bool5;; paramBoolean1 = false)
      {
        paramBaseFragment.closeLastFragment(paramBoolean1);
        return false;
      }
      label496:
      if ((this.tabletFullSize) && (paramActionBarLayout != this.actionBarLayout))
      {
        paramActionBarLayout = this.actionBarLayout;
        if (this.actionBarLayout.fragmentsStack.size() > 1) {}
        for (paramBoolean1 = true;; paramBoolean1 = false)
        {
          paramActionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, false);
          if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
            break;
          }
          for (i = 0; this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
            this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(0));
          }
        }
        paramBaseFragment = this.layersActionBarLayout;
        if (!paramBoolean2) {}
        for (paramBoolean1 = bool3;; paramBoolean1 = false)
        {
          paramBaseFragment.closeLastFragment(paramBoolean1);
          return false;
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
          paramActionBarLayout.closeLastFragment(paramBoolean1);
        }
      }
      else
      {
        paramActionBarLayout = this.actionBarLayout;
        if (this.actionBarLayout.fragmentsStack.size() <= 1) {
          break label757;
        }
      }
      label757:
      for (paramBoolean1 = bool4;; paramBoolean1 = false)
      {
        paramActionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, false);
        return false;
        paramBoolean1 = false;
        break;
      }
      label762:
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
          return false;
          this.shadowTablet.setBackgroundColor(2130706432);
        }
      }
      return true;
    }
    paramActionBarLayout = this.drawerLayoutContainer;
    if ((!(paramBaseFragment instanceof LoginActivity)) && (!(paramBaseFragment instanceof CountrySelectActivity))) {}
    for (paramBoolean1 = true;; paramBoolean1 = false)
    {
      paramActionBarLayout.setAllowOpenDrawer(paramBoolean1, false);
      return true;
    }
  }
  
  public void onActionModeFinished(ActionMode paramActionMode)
  {
    super.onActionModeFinished(paramActionMode);
    if ((Build.VERSION.SDK_INT >= 23) && (paramActionMode.getType() == 1)) {}
    do
    {
      return;
      this.actionBarLayout.onActionModeFinished(paramActionMode);
    } while (!AndroidUtilities.isTablet());
    this.rightActionBarLayout.onActionModeFinished(paramActionMode);
    this.layersActionBarLayout.onActionModeFinished(paramActionMode);
  }
  
  public void onActionModeStarted(ActionMode paramActionMode)
  {
    super.onActionModeStarted(paramActionMode);
    if ((Build.VERSION.SDK_INT >= 23) && (paramActionMode.getType() == 1)) {}
    do
    {
      return;
      this.actionBarLayout.onActionModeStarted(paramActionMode);
    } while (!AndroidUtilities.isTablet());
    this.rightActionBarLayout.onActionModeStarted(paramActionMode);
    this.layersActionBarLayout.onActionModeStarted(paramActionMode);
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if ((UserConfig.passcodeHash.length() != 0) && (UserConfig.lastPauseTime != 0))
    {
      UserConfig.lastPauseTime = 0;
      UserConfig.saveConfig(false);
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
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
      if (PhotoViewer.getInstance().isVisible())
      {
        PhotoViewer.getInstance().closePhoto(true, false);
        return;
      }
      if (this.drawerLayoutContainer.isDrawerOpened())
      {
        this.drawerLayoutContainer.closeDrawer(false);
        return;
      }
      if (!AndroidUtilities.isTablet()) {
        break;
      }
      if (this.layersActionBarLayout.getVisibility() == 0)
      {
        this.layersActionBarLayout.onBackPressed();
        return;
      }
      int j = 0;
      int i = j;
      if (this.rightActionBarLayout.getVisibility() == 0)
      {
        i = j;
        if (!this.rightActionBarLayout.fragmentsStack.isEmpty()) {
          if (((BaseFragment)this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onBackPressed()) {
            break label150;
          }
        }
      }
      label150:
      for (i = 1; i == 0; i = 0)
      {
        this.actionBarLayout.onBackPressed();
        return;
      }
    }
    this.actionBarLayout.onBackPressed();
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    AndroidUtilities.checkDisplaySize(this, paramConfiguration);
    super.onConfigurationChanged(paramConfiguration);
    checkLayout();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    ApplicationLoader.postInitApplication();
    NativeCrashManager.handleDumpFiles(this);
    AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
    if (!UserConfig.isClientActivated())
    {
      localObject1 = getIntent();
      if ((localObject1 != null) && (((Intent)localObject1).getAction() != null) && (("android.intent.action.SEND".equals(((Intent)localObject1).getAction())) || (((Intent)localObject1).getAction().equals("android.intent.action.SEND_MULTIPLE"))))
      {
        super.onCreate(paramBundle);
        finish();
        return;
      }
      if ((localObject1 != null) && (!((Intent)localObject1).getBooleanExtra("fromIntro", false)) && (ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).getAll().isEmpty()))
      {
        startActivity(new Intent(this, IntroActivity.class));
        super.onCreate(paramBundle);
        finish();
        return;
      }
    }
    requestWindowFeature(1);
    setTheme(2131296262);
    getWindow().setBackgroundDrawableResource(2130838023);
    super.onCreate(paramBundle);
    if (Build.VERSION.SDK_INT >= 24) {
      AndroidUtilities.isInMultiwindow = isInMultiWindowMode();
    }
    Theme.loadRecources(this);
    if ((UserConfig.passcodeHash.length() != 0) && (UserConfig.appLocked)) {
      UserConfig.lastPauseTime = ConnectionsManager.getInstance().getCurrentTime();
    }
    int i = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (i > 0) {
      AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(i);
    }
    this.actionBarLayout = new ActionBarLayout(this);
    this.drawerLayoutContainer = new DrawerLayoutContainer(this);
    setContentView(this.drawerLayoutContainer, new ViewGroup.LayoutParams(-1, -1));
    Object localObject3;
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
            paramAnonymousInt3 = (i - LaunchActivity.this.layersActionBarLayout.getMeasuredWidth()) / 2;
            if (Build.VERSION.SDK_INT < 21) {
              break label348;
            }
          }
          label348:
          for (paramAnonymousInt1 = AndroidUtilities.statusBarHeight;; paramAnonymousInt1 = 0)
          {
            paramAnonymousInt1 += (paramAnonymousInt4 - paramAnonymousInt2 - LaunchActivity.this.layersActionBarLayout.getMeasuredHeight()) / 2;
            LaunchActivity.this.layersActionBarLayout.layout(paramAnonymousInt3, paramAnonymousInt1, LaunchActivity.this.layersActionBarLayout.getMeasuredWidth() + paramAnonymousInt3, LaunchActivity.this.layersActionBarLayout.getMeasuredHeight() + paramAnonymousInt1);
            LaunchActivity.this.backgroundTablet.layout(0, 0, LaunchActivity.this.backgroundTablet.getMeasuredWidth(), LaunchActivity.this.backgroundTablet.getMeasuredHeight());
            LaunchActivity.this.shadowTablet.layout(0, 0, LaunchActivity.this.shadowTablet.getMeasuredWidth(), LaunchActivity.this.shadowTablet.getMeasuredHeight());
            return;
            LaunchActivity.this.actionBarLayout.layout(0, 0, LaunchActivity.this.actionBarLayout.getMeasuredWidth(), LaunchActivity.this.actionBarLayout.getMeasuredHeight());
            break;
          }
        }
        
        protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          this.inLayout = true;
          int j = View.MeasureSpec.getSize(paramAnonymousInt1);
          int i = View.MeasureSpec.getSize(paramAnonymousInt2);
          setMeasuredDimension(j, i);
          ActionBarLayout localActionBarLayout;
          if ((!AndroidUtilities.isInMultiwindow) && ((!AndroidUtilities.isSmallTablet()) || (getResources().getConfiguration().orientation == 2)))
          {
            LaunchActivity.access$002(LaunchActivity.this, false);
            paramAnonymousInt2 = j / 100 * 35;
            paramAnonymousInt1 = paramAnonymousInt2;
            if (paramAnonymousInt2 < AndroidUtilities.dp(320.0F)) {
              paramAnonymousInt1 = AndroidUtilities.dp(320.0F);
            }
            LaunchActivity.this.actionBarLayout.measure(View.MeasureSpec.makeMeasureSpec(paramAnonymousInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(i, 1073741824));
            LaunchActivity.this.shadowTabletSide.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(i, 1073741824));
            LaunchActivity.this.rightActionBarLayout.measure(View.MeasureSpec.makeMeasureSpec(j - paramAnonymousInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(i, 1073741824));
            LaunchActivity.this.backgroundTablet.measure(View.MeasureSpec.makeMeasureSpec(j, 1073741824), View.MeasureSpec.makeMeasureSpec(i, 1073741824));
            LaunchActivity.this.shadowTablet.measure(View.MeasureSpec.makeMeasureSpec(j, 1073741824), View.MeasureSpec.makeMeasureSpec(i, 1073741824));
            localActionBarLayout = LaunchActivity.this.layersActionBarLayout;
            paramAnonymousInt2 = View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(530.0F), j), 1073741824);
            j = AndroidUtilities.dp(528.0F);
            if (Build.VERSION.SDK_INT < 21) {
              break label306;
            }
          }
          label306:
          for (paramAnonymousInt1 = AndroidUtilities.statusBarHeight;; paramAnonymousInt1 = 0)
          {
            localActionBarLayout.measure(paramAnonymousInt2, View.MeasureSpec.makeMeasureSpec(Math.min(j - paramAnonymousInt1, i), 1073741824));
            this.inLayout = false;
            return;
            LaunchActivity.access$002(LaunchActivity.this, true);
            LaunchActivity.this.actionBarLayout.measure(View.MeasureSpec.makeMeasureSpec(j, 1073741824), View.MeasureSpec.makeMeasureSpec(i, 1073741824));
            break;
          }
        }
        
        public void requestLayout()
        {
          if (this.inLayout) {
            return;
          }
          super.requestLayout();
        }
      };
      this.drawerLayoutContainer.addView((View)localObject1, LayoutHelper.createFrame(-1, -1.0F));
      this.backgroundTablet = new ImageView(this);
      this.backgroundTablet.setScaleType(ImageView.ScaleType.CENTER_CROP);
      this.backgroundTablet.setImageResource(2130837588);
      ((RelativeLayout)localObject1).addView(this.backgroundTablet, LayoutHelper.createRelative(-1, -1));
      ((RelativeLayout)localObject1).addView(this.actionBarLayout);
      this.rightActionBarLayout = new ActionBarLayout(this);
      this.rightActionBarLayout.init(rightFragmentsStack);
      this.rightActionBarLayout.setDelegate(this);
      ((RelativeLayout)localObject1).addView(this.rightActionBarLayout);
      this.shadowTabletSide = new FrameLayout(this);
      this.shadowTabletSide.setBackgroundColor(1076449908);
      ((RelativeLayout)localObject1).addView(this.shadowTabletSide);
      this.shadowTablet = new FrameLayout(this);
      localObject3 = this.shadowTablet;
      if (layerFragmentsStack.isEmpty())
      {
        i = 8;
        ((FrameLayout)localObject3).setVisibility(i);
        this.shadowTablet.setBackgroundColor(2130706432);
        ((RelativeLayout)localObject1).addView(this.shadowTablet);
        this.shadowTablet.setOnTouchListener(new View.OnTouchListener()
        {
          public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
          {
            if ((!LaunchActivity.this.actionBarLayout.fragmentsStack.isEmpty()) && (paramAnonymousMotionEvent.getAction() == 1))
            {
              float f1 = paramAnonymousMotionEvent.getX();
              float f2 = paramAnonymousMotionEvent.getY();
              paramAnonymousView = new int[2];
              LaunchActivity.this.layersActionBarLayout.getLocationOnScreen(paramAnonymousView);
              int i = paramAnonymousView[0];
              int j = paramAnonymousView[1];
              if ((LaunchActivity.this.layersActionBarLayout.checkTransitionAnimation()) || ((f1 > i) && (f1 < LaunchActivity.this.layersActionBarLayout.getWidth() + i) && (f2 > j) && (f2 < LaunchActivity.this.layersActionBarLayout.getHeight() + j))) {
                return false;
              }
              if (!LaunchActivity.this.layersActionBarLayout.fragmentsStack.isEmpty())
              {
                for (i = 0; LaunchActivity.this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
                  LaunchActivity.this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)LaunchActivity.this.layersActionBarLayout.fragmentsStack.get(0));
                }
                LaunchActivity.this.layersActionBarLayout.closeLastFragment(true);
              }
              return true;
            }
            return false;
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
        this.layersActionBarLayout.setBackgroundResource(2130837577);
        this.layersActionBarLayout.init(layerFragmentsStack);
        this.layersActionBarLayout.setDelegate(this);
        this.layersActionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        localObject3 = this.layersActionBarLayout;
        if (!layerFragmentsStack.isEmpty()) {
          break label1230;
        }
        i = 8;
        label638:
        ((ActionBarLayout)localObject3).setVisibility(i);
        ((RelativeLayout)localObject1).addView(this.layersActionBarLayout);
        label653:
        this.listView = new ListView(this)
        {
          public boolean hasOverlappingRendering()
          {
            return false;
          }
        };
        this.listView.setBackgroundColor(-1);
        localObject1 = this.listView;
        localObject3 = new DrawerLayoutAdapter(this);
        this.drawerLayoutAdapter = ((DrawerLayoutAdapter)localObject3);
        ((ListView)localObject1).setAdapter((ListAdapter)localObject3);
        this.listView.setChoiceMode(1);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setVerticalScrollBarEnabled(false);
        this.drawerLayoutContainer.setDrawerLayout(this.listView);
        localObject1 = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
        localObject3 = AndroidUtilities.getRealScreenSize();
        if (!AndroidUtilities.isTablet()) {
          break label1258;
        }
        i = AndroidUtilities.dp(320.0F);
        label776:
        ((FrameLayout.LayoutParams)localObject1).width = i;
        ((FrameLayout.LayoutParams)localObject1).height = -1;
        this.listView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
          public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
          {
            if (paramAnonymousInt == 0)
            {
              paramAnonymousAdapterView = new Bundle();
              paramAnonymousAdapterView.putInt("user_id", UserConfig.getClientUserId());
              LaunchActivity.this.presentFragment(new ChatActivity(paramAnonymousAdapterView));
              LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
            }
            do
            {
              do
              {
                do
                {
                  return;
                  if (paramAnonymousInt != 2) {
                    break;
                  }
                } while (!MessagesController.isFeatureEnabled("chat_create", (BaseFragment)LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1)));
                LaunchActivity.this.presentFragment(new GroupCreateActivity());
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                return;
                if (paramAnonymousInt == 3)
                {
                  paramAnonymousAdapterView = new Bundle();
                  paramAnonymousAdapterView.putBoolean("onlyUsers", true);
                  paramAnonymousAdapterView.putBoolean("destroyAfterSelect", true);
                  paramAnonymousAdapterView.putBoolean("createSecretChat", true);
                  paramAnonymousAdapterView.putBoolean("allowBots", false);
                  LaunchActivity.this.presentFragment(new ContactsActivity(paramAnonymousAdapterView));
                  LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                  return;
                }
                if (paramAnonymousInt != 4) {
                  break;
                }
              } while (!MessagesController.isFeatureEnabled("broadcast_create", (BaseFragment)LaunchActivity.this.actionBarLayout.fragmentsStack.get(LaunchActivity.this.actionBarLayout.fragmentsStack.size() - 1)));
              paramAnonymousAdapterView = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
              if (paramAnonymousAdapterView.getBoolean("channel_intro", false))
              {
                paramAnonymousAdapterView = new Bundle();
                paramAnonymousAdapterView.putInt("step", 0);
                LaunchActivity.this.presentFragment(new ChannelCreateActivity(paramAnonymousAdapterView));
              }
              for (;;)
              {
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                return;
                LaunchActivity.this.presentFragment(new ChannelIntroActivity());
                paramAnonymousAdapterView.edit().putBoolean("channel_intro", true).commit();
              }
              if (paramAnonymousInt == 6)
              {
                LaunchActivity.this.presentFragment(new ContactsActivity(null));
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                return;
              }
              if (paramAnonymousInt == 7) {
                try
                {
                  paramAnonymousAdapterView = new Intent("android.intent.action.SEND");
                  paramAnonymousAdapterView.setType("text/plain");
                  paramAnonymousAdapterView.putExtra("android.intent.extra.TEXT", ContactsController.getInstance().getInviteText());
                  LaunchActivity.this.startActivityForResult(Intent.createChooser(paramAnonymousAdapterView, LocaleController.getString("InviteFriends", 2131165761)), 500);
                  LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                  return;
                }
                catch (Exception paramAnonymousAdapterView)
                {
                  for (;;)
                  {
                    FileLog.e("tmessages", paramAnonymousAdapterView);
                  }
                }
              }
              if (paramAnonymousInt == 8)
              {
                LaunchActivity.this.presentFragment(new SettingsActivity());
                LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
                return;
              }
            } while (paramAnonymousInt != 9);
            Browser.openUrl(LaunchActivity.this, LocaleController.getString("TelegramFaqUrl", 2131166327));
            LaunchActivity.this.drawerLayoutContainer.closeDrawer(false);
          }
        });
        this.drawerLayoutContainer.setParentActionBarLayout(this.actionBarLayout);
        this.actionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
        this.actionBarLayout.init(mainFragmentsStack);
        this.actionBarLayout.setDelegate(this);
        ApplicationLoader.loadWallpaper();
        this.passcodeView = new PasscodeView(this);
        this.drawerLayoutContainer.addView(this.passcodeView);
        localObject1 = (FrameLayout.LayoutParams)this.passcodeView.getLayoutParams();
        ((FrameLayout.LayoutParams)localObject1).width = -1;
        ((FrameLayout.LayoutParams)localObject1).height = -1;
        this.passcodeView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeOtherAppActivities, new Object[] { this });
        this.currentConnectionState = ConnectionsManager.getInstance().getConnectionState();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.mainUserInfoChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeOtherAppActivities);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didUpdatedConnectionState);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.needShowAlert);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.wasUnableToFindCurrentLocation);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        if (!this.actionBarLayout.fragmentsStack.isEmpty()) {
          break label1686;
        }
        if (UserConfig.isClientActivated()) {
          break label1291;
        }
        this.actionBarLayout.addFragmentToStack(new LoginActivity());
        this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
        label1051:
        if (paramBundle == null) {}
      }
    }
    try
    {
      localObject1 = paramBundle.getString("fragment");
      if (localObject1 != null)
      {
        localObject3 = paramBundle.getBundle("args");
        i = -1;
        int j = ((String)localObject1).hashCode();
        switch (j)
        {
        default: 
          label1152:
          switch (i)
          {
          }
          break;
        }
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        label1196:
        FileLog.e("tmessages", localException);
        continue;
        localObject2 = new SettingsActivity();
        this.actionBarLayout.addFragmentToStack((BaseFragment)localObject2);
        ((SettingsActivity)localObject2).restoreSelfArgs(paramBundle);
        continue;
        if (localObject3 != null)
        {
          localObject2 = new GroupCreateFinalActivity((Bundle)localObject3);
          if (this.actionBarLayout.addFragmentToStack((BaseFragment)localObject2))
          {
            ((GroupCreateFinalActivity)localObject2).restoreSelfArgs(paramBundle);
            continue;
            if (localObject3 != null)
            {
              localObject2 = new ChannelCreateActivity((Bundle)localObject3);
              if (this.actionBarLayout.addFragmentToStack((BaseFragment)localObject2))
              {
                ((ChannelCreateActivity)localObject2).restoreSelfArgs(paramBundle);
                continue;
                if (localObject3 != null)
                {
                  localObject2 = new ChannelEditActivity((Bundle)localObject3);
                  if (this.actionBarLayout.addFragmentToStack((BaseFragment)localObject2))
                  {
                    ((ChannelEditActivity)localObject2).restoreSelfArgs(paramBundle);
                    continue;
                    if (localObject3 != null)
                    {
                      localObject2 = new ProfileActivity((Bundle)localObject3);
                      if (this.actionBarLayout.addFragmentToStack((BaseFragment)localObject2))
                      {
                        ((ProfileActivity)localObject2).restoreSelfArgs(paramBundle);
                        continue;
                        localObject2 = new WallpapersActivity();
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
    Object localObject1 = getIntent();
    if (paramBundle != null) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      handleIntent((Intent)localObject1, false, bool1, false);
      return;
      i = 0;
      break;
      label1230:
      i = 0;
      break label638;
      this.drawerLayoutContainer.addView(this.actionBarLayout, new ViewGroup.LayoutParams(-1, -1));
      break label653;
      label1258:
      i = Math.min(AndroidUtilities.dp(320.0F), Math.min(((Point)localObject3).x, ((Point)localObject3).y) - AndroidUtilities.dp(56.0F));
      break label776;
      label1291:
      this.actionBarLayout.addFragmentToStack(new DialogsActivity(null));
      this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
      break label1051;
      if (!((String)localObject1).equals("chat")) {
        break label1152;
      }
      i = 0;
      break label1152;
      if (!((String)localObject1).equals("settings")) {
        break label1152;
      }
      i = 1;
      break label1152;
      if (!((String)localObject1).equals("group")) {
        break label1152;
      }
      i = 2;
      break label1152;
      if (!((String)localObject1).equals("channel")) {
        break label1152;
      }
      i = 3;
      break label1152;
      if (!((String)localObject1).equals("edit")) {
        break label1152;
      }
      i = 4;
      break label1152;
      if (!((String)localObject1).equals("chat_profile")) {
        break label1152;
      }
      i = 5;
      break label1152;
      if (!((String)localObject1).equals("wallpapers")) {
        break label1152;
      }
      i = 6;
      break label1152;
      if (localObject3 == null) {
        break label1196;
      }
      localObject1 = new ChatActivity((Bundle)localObject3);
      if (!this.actionBarLayout.addFragmentToStack((BaseFragment)localObject1)) {
        break label1196;
      }
      ((ChatActivity)localObject1).restoreSelfArgs(paramBundle);
      break label1196;
      Object localObject2;
      label1686:
      bool1 = true;
      if (AndroidUtilities.isTablet()) {
        if ((this.actionBarLayout.fragmentsStack.size() > 1) || (!this.layersActionBarLayout.fragmentsStack.isEmpty())) {
          break label1822;
        }
      }
      label1822:
      for (boolean bool2 = true;; bool2 = false)
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
    }
  }
  
  protected void onDestroy()
  {
    PhotoViewer.getInstance().destroyPhotoViewer();
    SecretPhotoViewer.getInstance().destroyPhotoViewer();
    StickerPreviewViewer.getInstance().destroy();
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
        if (this.onGlobalLayoutListener != null)
        {
          View localView = getWindow().getDecorView().getRootView();
          if (Build.VERSION.SDK_INT >= 16) {
            break label94;
          }
          localView.getViewTreeObserver().removeGlobalOnLayoutListener(this.onGlobalLayoutListener);
        }
        for (;;)
        {
          super.onDestroy();
          onFinish();
          return;
          localException1 = localException1;
          FileLog.e("tmessages", localException1);
          break;
          label94:
          localException1.getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
        }
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException2);
        }
      }
    }
  }
  
  public boolean onKeyUp(int paramInt, @NonNull KeyEvent paramKeyEvent)
  {
    if ((paramInt == 82) && (!UserConfig.isWaitingForPasscodeEnter))
    {
      if (PhotoViewer.getInstance().isVisible()) {
        return super.onKeyUp(paramInt, paramKeyEvent);
      }
      if (!AndroidUtilities.isTablet()) {
        break label123;
      }
      if ((this.layersActionBarLayout.getVisibility() != 0) || (this.layersActionBarLayout.fragmentsStack.isEmpty())) {
        break label74;
      }
      this.layersActionBarLayout.onKeyUp(paramInt, paramKeyEvent);
    }
    for (;;)
    {
      return super.onKeyUp(paramInt, paramKeyEvent);
      label74:
      if ((this.rightActionBarLayout.getVisibility() == 0) && (!this.rightActionBarLayout.fragmentsStack.isEmpty()))
      {
        this.rightActionBarLayout.onKeyUp(paramInt, paramKeyEvent);
      }
      else
      {
        this.actionBarLayout.onKeyUp(paramInt, paramKeyEvent);
        continue;
        label123:
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
    ApplicationLoader.mainInterfacePaused = true;
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
    ConnectionsManager.getInstance().setAppPaused(true, false);
    AndroidUtilities.unregisterUpdates();
    if (PhotoViewer.getInstance().isVisible()) {
      PhotoViewer.getInstance().onPause();
    }
  }
  
  public boolean onPreIme()
  {
    if (PhotoViewer.getInstance().isVisible())
    {
      PhotoViewer.getInstance().closePhoto(true, false);
      return true;
    }
    return false;
  }
  
  public void onRebuildAllFragments(ActionBarLayout paramActionBarLayout)
  {
    if ((AndroidUtilities.isTablet()) && (paramActionBarLayout == this.layersActionBarLayout))
    {
      this.rightActionBarLayout.rebuildAllFragmentViews(true);
      this.rightActionBarLayout.showLastFragment();
      this.actionBarLayout.rebuildAllFragmentViews(true);
      this.actionBarLayout.showLastFragment();
    }
    this.drawerLayoutAdapter.notifyDataSetChanged();
  }
  
  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    super.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfInt);
    int j;
    int i;
    if ((paramInt == 3) || (paramInt == 4) || (paramInt == 5) || (paramInt == 19) || (paramInt == 20))
    {
      j = 1;
      i = j;
      if (paramArrayOfInt.length > 0)
      {
        i = j;
        if (paramArrayOfInt[0] == 0) {
          if (paramInt == 4) {
            ImageLoader.getInstance().checkMediaPaths();
          }
        }
      }
    }
    do
    {
      do
      {
        do
        {
          return;
          if (paramInt == 5)
          {
            ContactsController.getInstance().readContacts();
            return;
          }
        } while (paramInt == 3);
        if (paramInt != 19)
        {
          i = j;
          if (paramInt != 20) {}
        }
        else
        {
          i = 0;
        }
        if (i != 0)
        {
          paramArrayOfString = new AlertDialog.Builder(this);
          paramArrayOfString.setTitle(LocaleController.getString("AppName", 2131165299));
          if (paramInt == 3) {
            paramArrayOfString.setMessage(LocaleController.getString("PermissionNoAudio", 2131166097));
          }
          for (;;)
          {
            paramArrayOfString.setNegativeButton(LocaleController.getString("PermissionOpenSettings", 2131166101), new DialogInterface.OnClickListener()
            {
              @TargetApi(9)
              public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
              {
                try
                {
                  paramAnonymousDialogInterface = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                  paramAnonymousDialogInterface.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                  LaunchActivity.this.startActivity(paramAnonymousDialogInterface);
                  return;
                }
                catch (Exception paramAnonymousDialogInterface)
                {
                  FileLog.e("tmessages", paramAnonymousDialogInterface);
                }
              }
            });
            paramArrayOfString.setPositiveButton(LocaleController.getString("OK", 2131166044), null);
            paramArrayOfString.show();
            return;
            if (paramInt == 4) {
              paramArrayOfString.setMessage(LocaleController.getString("PermissionStorage", 2131166102));
            } else if (paramInt == 5) {
              paramArrayOfString.setMessage(LocaleController.getString("PermissionContacts", 2131166096));
            } else if ((paramInt == 19) || (paramInt == 20)) {
              paramArrayOfString.setMessage(LocaleController.getString("PermissionNoCamera", 2131166098));
            }
          }
          if ((paramInt == 2) && (paramArrayOfInt.length > 0) && (paramArrayOfInt[0] == 0)) {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.locationPermissionGranted, new Object[0]);
          }
        }
        if (this.actionBarLayout.fragmentsStack.size() != 0) {
          ((BaseFragment)this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(paramInt, paramArrayOfString, paramArrayOfInt);
        }
      } while (!AndroidUtilities.isTablet());
      if (this.rightActionBarLayout.fragmentsStack.size() != 0) {
        ((BaseFragment)this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(paramInt, paramArrayOfString, paramArrayOfInt);
      }
    } while (this.layersActionBarLayout.fragmentsStack.size() == 0);
    ((BaseFragment)this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1)).onRequestPermissionsResultFragment(paramInt, paramArrayOfString, paramArrayOfInt);
  }
  
  protected void onResume()
  {
    super.onResume();
    ApplicationLoader.mainInterfacePaused = false;
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
      ConnectionsManager.getInstance().setAppPaused(false, false);
      updateCurrentConnectionState();
      if (PhotoViewer.getInstance().isVisible()) {
        PhotoViewer.getInstance().onResume();
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
      BaseFragment localBaseFragment;
      Bundle localBundle;
      try
      {
        super.onSaveInstanceState(paramBundle);
        localBaseFragment = null;
        if (AndroidUtilities.isTablet())
        {
          if (!this.layersActionBarLayout.fragmentsStack.isEmpty())
          {
            localBaseFragment = (BaseFragment)this.layersActionBarLayout.fragmentsStack.get(this.layersActionBarLayout.fragmentsStack.size() - 1);
            if (localBaseFragment == null) {
              break;
            }
            localBundle = localBaseFragment.getArguments();
            if (((localBaseFragment instanceof ChatActivity)) && (localBundle != null))
            {
              paramBundle.putBundle("args", localBundle);
              paramBundle.putString("fragment", "chat");
              localBaseFragment.saveSelfArgs(paramBundle);
            }
          }
          else
          {
            if (!this.rightActionBarLayout.fragmentsStack.isEmpty())
            {
              localBaseFragment = (BaseFragment)this.rightActionBarLayout.fragmentsStack.get(this.rightActionBarLayout.fragmentsStack.size() - 1);
              continue;
            }
            if (this.actionBarLayout.fragmentsStack.isEmpty()) {
              continue;
            }
            localBaseFragment = (BaseFragment)this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
            continue;
          }
        }
        else
        {
          if (this.actionBarLayout.fragmentsStack.isEmpty()) {
            continue;
          }
          localBaseFragment = (BaseFragment)this.actionBarLayout.fragmentsStack.get(this.actionBarLayout.fragmentsStack.size() - 1);
          continue;
        }
        if ((localBaseFragment instanceof SettingsActivity))
        {
          paramBundle.putString("fragment", "settings");
          continue;
        }
        if (!(localBaseFragment instanceof GroupCreateFinalActivity)) {
          break label283;
        }
      }
      catch (Exception paramBundle)
      {
        FileLog.e("tmessages", paramBundle);
        return;
      }
      if (localBundle != null)
      {
        paramBundle.putBundle("args", localBundle);
        paramBundle.putString("fragment", "group");
      }
      else
      {
        label283:
        if ((localBaseFragment instanceof WallpapersActivity))
        {
          paramBundle.putString("fragment", "wallpapers");
        }
        else if (((localBaseFragment instanceof ProfileActivity)) && (((ProfileActivity)localBaseFragment).isChat()) && (localBundle != null))
        {
          paramBundle.putBundle("args", localBundle);
          paramBundle.putString("fragment", "chat_profile");
        }
        else if (((localBaseFragment instanceof ChannelCreateActivity)) && (localBundle != null) && (localBundle.getInt("step") == 0))
        {
          paramBundle.putBundle("args", localBundle);
          paramBundle.putString("fragment", "channel");
        }
        else if (((localBaseFragment instanceof ChannelEditActivity)) && (localBundle != null))
        {
          paramBundle.putBundle("args", localBundle);
          paramBundle.putString("fragment", "edit");
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
  
  public AlertDialog showAlertDialog(AlertDialog.Builder paramBuilder)
  {
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
      for (;;)
      {
        try
        {
          this.visibleDialog = paramBuilder.show();
          this.visibleDialog.setCanceledOnTouchOutside(true);
          this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
          {
            public void onDismiss(DialogInterface paramAnonymousDialogInterface)
            {
              LaunchActivity.access$1402(LaunchActivity.this, null);
            }
          });
          paramBuilder = this.visibleDialog;
          return paramBuilder;
        }
        catch (Exception paramBuilder)
        {
          FileLog.e("tmessages", paramBuilder);
        }
        localException = localException;
        FileLog.e("tmessages", localException);
      }
    }
    return null;
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