package org.telegram.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Environment;
import android.os.StatFs;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;

public class DocumentSelectActivity
  extends BaseFragment
{
  private static final int done = 3;
  private ArrayList<View> actionModeViews = new ArrayList();
  private File currentDir;
  private DocumentSelectActivityDelegate delegate;
  private TextView emptyView;
  private ArrayList<HistoryEntry> history = new ArrayList();
  private ArrayList<ListItem> items = new ArrayList();
  private ListAdapter listAdapter;
  private ListView listView;
  private BroadcastReceiver receiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = new Runnable()
      {
        public void run()
        {
          try
          {
            if (DocumentSelectActivity.this.currentDir == null)
            {
              DocumentSelectActivity.this.listRoots();
              return;
            }
            DocumentSelectActivity.this.listFiles(DocumentSelectActivity.this.currentDir);
            return;
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
          }
        }
      };
      if ("android.intent.action.MEDIA_UNMOUNTED".equals(paramAnonymousIntent.getAction()))
      {
        DocumentSelectActivity.this.listView.postDelayed(paramAnonymousContext, 1000L);
        return;
      }
      paramAnonymousContext.run();
    }
  };
  private boolean receiverRegistered = false;
  private boolean scrolling;
  private HashMap<String, ListItem> selectedFiles = new HashMap();
  private NumberTextView selectedMessagesCountTextView;
  private long sizeLimit = 1610612736L;
  
  private void fixLayoutInternal()
  {
    if (this.selectedMessagesCountTextView == null) {
      return;
    }
    if ((!AndroidUtilities.isTablet()) && (ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == 2))
    {
      this.selectedMessagesCountTextView.setTextSize(18);
      return;
    }
    this.selectedMessagesCountTextView.setTextSize(20);
  }
  
  private String getRootSubtitle(String paramString)
  {
    try
    {
      Object localObject = new StatFs(paramString);
      long l1 = ((StatFs)localObject).getBlockCount() * ((StatFs)localObject).getBlockSize();
      long l2 = ((StatFs)localObject).getAvailableBlocks();
      long l3 = ((StatFs)localObject).getBlockSize();
      if (l1 == 0L) {
        return "";
      }
      localObject = LocaleController.formatString("FreeOfTotal", 2131165701, new Object[] { AndroidUtilities.formatFileSize(l2 * l3), AndroidUtilities.formatFileSize(l1) });
      return (String)localObject;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return paramString;
  }
  
  private boolean listFiles(File paramFile)
  {
    if (!paramFile.canRead())
    {
      if (((paramFile.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().toString())) || (paramFile.getAbsolutePath().startsWith("/sdcard")) || (paramFile.getAbsolutePath().startsWith("/mnt/sdcard"))) && (!Environment.getExternalStorageState().equals("mounted")) && (!Environment.getExternalStorageState().equals("mounted_ro")))
      {
        this.currentDir = paramFile;
        this.items.clear();
        if ("shared".equals(Environment.getExternalStorageState())) {
          this.emptyView.setText(LocaleController.getString("UsbActive", 2131166362));
        }
        for (;;)
        {
          AndroidUtilities.clearDrawableAnimation(this.listView);
          this.scrolling = true;
          this.listAdapter.notifyDataSetChanged();
          return true;
          this.emptyView.setText(LocaleController.getString("NotMounted", 2131165963));
        }
      }
      showErrorBox(LocaleController.getString("AccessError", 2131165206));
      return false;
    }
    this.emptyView.setText(LocaleController.getString("NoFiles", 2131165933));
    Object localObject;
    try
    {
      localObject = paramFile.listFiles();
      if (localObject == null)
      {
        showErrorBox(LocaleController.getString("UnknownError", 2131166352));
        return false;
      }
    }
    catch (Exception paramFile)
    {
      showErrorBox(paramFile.getLocalizedMessage());
      return false;
    }
    this.currentDir = paramFile;
    this.items.clear();
    Arrays.sort((Object[])localObject, new Comparator()
    {
      public int compare(File paramAnonymousFile1, File paramAnonymousFile2)
      {
        if (paramAnonymousFile1.isDirectory() != paramAnonymousFile2.isDirectory())
        {
          if (paramAnonymousFile1.isDirectory()) {
            return -1;
          }
          return 1;
        }
        return paramAnonymousFile1.getName().compareToIgnoreCase(paramAnonymousFile2.getName());
      }
    });
    int i = 0;
    if (i < localObject.length)
    {
      File localFile = localObject[i];
      if (localFile.getName().indexOf('.') == 0) {}
      ListItem localListItem;
      for (;;)
      {
        i += 1;
        break;
        localListItem = new ListItem(null);
        localListItem.title = localFile.getName();
        localListItem.file = localFile;
        if (!localFile.isDirectory()) {
          break label347;
        }
        localListItem.icon = 2130837719;
        localListItem.subtitle = LocaleController.getString("Folder", 2131165641);
        this.items.add(localListItem);
      }
      label347:
      String str = localFile.getName();
      paramFile = str.split("\\.");
      if (paramFile.length > 1) {}
      for (paramFile = paramFile[(paramFile.length - 1)];; paramFile = "?")
      {
        localListItem.ext = paramFile;
        localListItem.subtitle = AndroidUtilities.formatFileSize(localFile.length());
        paramFile = str.toLowerCase();
        if ((!paramFile.endsWith(".jpg")) && (!paramFile.endsWith(".png")) && (!paramFile.endsWith(".gif")) && (!paramFile.endsWith(".jpeg"))) {
          break;
        }
        localListItem.thumb = localFile.getAbsolutePath();
        break;
      }
    }
    paramFile = new ListItem(null);
    paramFile.title = "..";
    if (this.history.size() > 0)
    {
      localObject = (HistoryEntry)this.history.get(this.history.size() - 1);
      if (((HistoryEntry)localObject).dir == null) {
        paramFile.subtitle = LocaleController.getString("Folder", 2131165641);
      }
    }
    for (;;)
    {
      paramFile.icon = 2130837719;
      paramFile.file = null;
      this.items.add(0, paramFile);
      AndroidUtilities.clearDrawableAnimation(this.listView);
      this.scrolling = true;
      this.listAdapter.notifyDataSetChanged();
      return true;
      paramFile.subtitle = ((HistoryEntry)localObject).dir.toString();
      continue;
      paramFile.subtitle = LocaleController.getString("Folder", 2131165641);
    }
  }
  
  /* Error */
  @android.annotation.SuppressLint({"NewApi"})
  private void listRoots()
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: putfield 102	org/telegram/ui/DocumentSelectActivity:currentDir	Ljava/io/File;
    //   5: aload_0
    //   6: getfield 77	org/telegram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   9: invokevirtual 283	java/util/ArrayList:clear	()V
    //   12: new 419	java/util/HashSet
    //   15: dup
    //   16: invokespecial 420	java/util/HashSet:<init>	()V
    //   19: astore 6
    //   21: invokestatic 256	android/os/Environment:getExternalStorageDirectory	()Ljava/io/File;
    //   24: invokevirtual 423	java/io/File:getPath	()Ljava/lang/String;
    //   27: astore_2
    //   28: invokestatic 426	android/os/Environment:isExternalStorageRemovable	()Z
    //   31: pop
    //   32: invokestatic 272	android/os/Environment:getExternalStorageState	()Ljava/lang/String;
    //   35: astore_3
    //   36: aload_3
    //   37: ldc_w 274
    //   40: invokevirtual 278	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   43: ifne +13 -> 56
    //   46: aload_3
    //   47: ldc_w 280
    //   50: invokevirtual 278	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   53: ifeq +71 -> 124
    //   56: new 35	org/telegram/ui/DocumentSelectActivity$ListItem
    //   59: dup
    //   60: aload_0
    //   61: aconst_null
    //   62: invokespecial 344	org/telegram/ui/DocumentSelectActivity$ListItem:<init>	(Lorg/telegram/ui/DocumentSelectActivity;Lorg/telegram/ui/DocumentSelectActivity$1;)V
    //   65: astore_3
    //   66: invokestatic 426	android/os/Environment:isExternalStorageRemovable	()Z
    //   69: ifeq +615 -> 684
    //   72: aload_3
    //   73: ldc_w 428
    //   76: ldc_w 429
    //   79: invokestatic 294	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   82: putfield 348	org/telegram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   85: aload_3
    //   86: ldc_w 430
    //   89: putfield 358	org/telegram/ui/DocumentSelectActivity$ListItem:icon	I
    //   92: aload_3
    //   93: aload_0
    //   94: aload_2
    //   95: invokespecial 432	org/telegram/ui/DocumentSelectActivity:getRootSubtitle	(Ljava/lang/String;)Ljava/lang/String;
    //   98: putfield 364	org/telegram/ui/DocumentSelectActivity$ListItem:subtitle	Ljava/lang/String;
    //   101: aload_3
    //   102: invokestatic 256	android/os/Environment:getExternalStorageDirectory	()Ljava/io/File;
    //   105: putfield 351	org/telegram/ui/DocumentSelectActivity$ListItem:file	Ljava/io/File;
    //   108: aload_0
    //   109: getfield 77	org/telegram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   112: aload_3
    //   113: invokevirtual 367	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   116: pop
    //   117: aload 6
    //   119: aload_2
    //   120: invokevirtual 433	java/util/HashSet:add	(Ljava/lang/Object;)Z
    //   123: pop
    //   124: aconst_null
    //   125: astore_2
    //   126: aconst_null
    //   127: astore 4
    //   129: new 435	java/io/BufferedReader
    //   132: dup
    //   133: new 437	java/io/FileReader
    //   136: dup
    //   137: ldc_w 439
    //   140: invokespecial 440	java/io/FileReader:<init>	(Ljava/lang/String;)V
    //   143: invokespecial 443	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   146: astore_3
    //   147: aload_3
    //   148: invokevirtual 446	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   151: astore_2
    //   152: aload_2
    //   153: ifnull +582 -> 735
    //   156: aload_2
    //   157: ldc_w 448
    //   160: invokevirtual 452	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   163: ifne +13 -> 176
    //   166: aload_2
    //   167: ldc_w 454
    //   170: invokevirtual 452	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   173: ifeq -26 -> 147
    //   176: ldc -21
    //   178: aload_2
    //   179: invokestatic 457	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   182: new 459	java/util/StringTokenizer
    //   185: dup
    //   186: aload_2
    //   187: ldc_w 461
    //   190: invokespecial 463	java/util/StringTokenizer:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   193: astore 4
    //   195: aload 4
    //   197: invokevirtual 466	java/util/StringTokenizer:nextToken	()Ljava/lang/String;
    //   200: pop
    //   201: aload 4
    //   203: invokevirtual 466	java/util/StringTokenizer:nextToken	()Ljava/lang/String;
    //   206: astore 4
    //   208: aload 6
    //   210: aload 4
    //   212: invokevirtual 468	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   215: ifne -68 -> 147
    //   218: aload_2
    //   219: ldc_w 470
    //   222: invokevirtual 452	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   225: ifeq -78 -> 147
    //   228: aload_2
    //   229: ldc_w 472
    //   232: invokevirtual 452	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   235: ifne -88 -> 147
    //   238: aload_2
    //   239: ldc_w 474
    //   242: invokevirtual 452	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   245: ifne -98 -> 147
    //   248: aload_2
    //   249: ldc_w 476
    //   252: invokevirtual 452	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   255: ifne -108 -> 147
    //   258: aload_2
    //   259: ldc_w 478
    //   262: invokevirtual 452	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   265: ifne -118 -> 147
    //   268: aload_2
    //   269: ldc_w 480
    //   272: invokevirtual 452	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   275: ifne -128 -> 147
    //   278: aload 4
    //   280: astore_2
    //   281: new 243	java/io/File
    //   284: dup
    //   285: aload 4
    //   287: invokespecial 481	java/io/File:<init>	(Ljava/lang/String;)V
    //   290: invokevirtual 354	java/io/File:isDirectory	()Z
    //   293: ifne +69 -> 362
    //   296: aload 4
    //   298: bipush 47
    //   300: invokevirtual 484	java/lang/String:lastIndexOf	(I)I
    //   303: istore_1
    //   304: aload 4
    //   306: astore_2
    //   307: iload_1
    //   308: iconst_m1
    //   309: if_icmpeq +53 -> 362
    //   312: new 486	java/lang/StringBuilder
    //   315: dup
    //   316: invokespecial 487	java/lang/StringBuilder:<init>	()V
    //   319: ldc_w 489
    //   322: invokevirtual 493	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   325: aload 4
    //   327: iload_1
    //   328: iconst_1
    //   329: iadd
    //   330: invokevirtual 497	java/lang/String:substring	(I)Ljava/lang/String;
    //   333: invokevirtual 493	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   336: invokevirtual 498	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   339: astore 5
    //   341: aload 4
    //   343: astore_2
    //   344: new 243	java/io/File
    //   347: dup
    //   348: aload 5
    //   350: invokespecial 481	java/io/File:<init>	(Ljava/lang/String;)V
    //   353: invokevirtual 354	java/io/File:isDirectory	()Z
    //   356: ifeq +6 -> 362
    //   359: aload 5
    //   361: astore_2
    //   362: aload 6
    //   364: aload_2
    //   365: invokevirtual 433	java/util/HashSet:add	(Ljava/lang/Object;)Z
    //   368: pop
    //   369: new 35	org/telegram/ui/DocumentSelectActivity$ListItem
    //   372: dup
    //   373: aload_0
    //   374: aconst_null
    //   375: invokespecial 344	org/telegram/ui/DocumentSelectActivity$ListItem:<init>	(Lorg/telegram/ui/DocumentSelectActivity;Lorg/telegram/ui/DocumentSelectActivity$1;)V
    //   378: astore 4
    //   380: aload_2
    //   381: invokevirtual 383	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   384: ldc_w 500
    //   387: invokevirtual 452	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   390: ifeq +317 -> 707
    //   393: aload 4
    //   395: ldc_w 428
    //   398: ldc_w 429
    //   401: invokestatic 294	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   404: putfield 348	org/telegram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   407: aload 4
    //   409: ldc_w 430
    //   412: putfield 358	org/telegram/ui/DocumentSelectActivity$ListItem:icon	I
    //   415: aload 4
    //   417: aload_0
    //   418: aload_2
    //   419: invokespecial 432	org/telegram/ui/DocumentSelectActivity:getRootSubtitle	(Ljava/lang/String;)Ljava/lang/String;
    //   422: putfield 364	org/telegram/ui/DocumentSelectActivity$ListItem:subtitle	Ljava/lang/String;
    //   425: aload 4
    //   427: new 243	java/io/File
    //   430: dup
    //   431: aload_2
    //   432: invokespecial 481	java/io/File:<init>	(Ljava/lang/String;)V
    //   435: putfield 351	org/telegram/ui/DocumentSelectActivity$ListItem:file	Ljava/io/File;
    //   438: aload_0
    //   439: getfield 77	org/telegram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   442: aload 4
    //   444: invokevirtual 367	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   447: pop
    //   448: goto -301 -> 147
    //   451: astore_2
    //   452: ldc -21
    //   454: aload_2
    //   455: invokestatic 241	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   458: goto -311 -> 147
    //   461: astore 4
    //   463: aload_3
    //   464: astore_2
    //   465: ldc -21
    //   467: aload 4
    //   469: invokestatic 241	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   472: aload_3
    //   473: ifnull +7 -> 480
    //   476: aload_3
    //   477: invokevirtual 503	java/io/BufferedReader:close	()V
    //   480: new 35	org/telegram/ui/DocumentSelectActivity$ListItem
    //   483: dup
    //   484: aload_0
    //   485: aconst_null
    //   486: invokespecial 344	org/telegram/ui/DocumentSelectActivity$ListItem:<init>	(Lorg/telegram/ui/DocumentSelectActivity;Lorg/telegram/ui/DocumentSelectActivity$1;)V
    //   489: astore_2
    //   490: aload_2
    //   491: ldc_w 505
    //   494: putfield 348	org/telegram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   497: aload_2
    //   498: ldc_w 507
    //   501: ldc_w 508
    //   504: invokestatic 294	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   507: putfield 364	org/telegram/ui/DocumentSelectActivity$ListItem:subtitle	Ljava/lang/String;
    //   510: aload_2
    //   511: ldc_w 355
    //   514: putfield 358	org/telegram/ui/DocumentSelectActivity$ListItem:icon	I
    //   517: aload_2
    //   518: new 243	java/io/File
    //   521: dup
    //   522: ldc_w 505
    //   525: invokespecial 481	java/io/File:<init>	(Ljava/lang/String;)V
    //   528: putfield 351	org/telegram/ui/DocumentSelectActivity$ListItem:file	Ljava/io/File;
    //   531: aload_0
    //   532: getfield 77	org/telegram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   535: aload_2
    //   536: invokevirtual 367	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   539: pop
    //   540: new 243	java/io/File
    //   543: dup
    //   544: invokestatic 256	android/os/Environment:getExternalStorageDirectory	()Ljava/io/File;
    //   547: ldc_w 510
    //   550: invokespecial 513	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   553: astore_2
    //   554: aload_2
    //   555: invokevirtual 516	java/io/File:exists	()Z
    //   558: ifeq +49 -> 607
    //   561: new 35	org/telegram/ui/DocumentSelectActivity$ListItem
    //   564: dup
    //   565: aload_0
    //   566: aconst_null
    //   567: invokespecial 344	org/telegram/ui/DocumentSelectActivity$ListItem:<init>	(Lorg/telegram/ui/DocumentSelectActivity;Lorg/telegram/ui/DocumentSelectActivity$1;)V
    //   570: astore_3
    //   571: aload_3
    //   572: ldc_w 510
    //   575: putfield 348	org/telegram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   578: aload_3
    //   579: aload_2
    //   580: invokevirtual 259	java/io/File:toString	()Ljava/lang/String;
    //   583: putfield 364	org/telegram/ui/DocumentSelectActivity$ListItem:subtitle	Ljava/lang/String;
    //   586: aload_3
    //   587: ldc_w 355
    //   590: putfield 358	org/telegram/ui/DocumentSelectActivity$ListItem:icon	I
    //   593: aload_3
    //   594: aload_2
    //   595: putfield 351	org/telegram/ui/DocumentSelectActivity$ListItem:file	Ljava/io/File;
    //   598: aload_0
    //   599: getfield 77	org/telegram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   602: aload_3
    //   603: invokevirtual 367	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   606: pop
    //   607: new 35	org/telegram/ui/DocumentSelectActivity$ListItem
    //   610: dup
    //   611: aload_0
    //   612: aconst_null
    //   613: invokespecial 344	org/telegram/ui/DocumentSelectActivity$ListItem:<init>	(Lorg/telegram/ui/DocumentSelectActivity;Lorg/telegram/ui/DocumentSelectActivity$1;)V
    //   616: astore_2
    //   617: aload_2
    //   618: ldc_w 518
    //   621: ldc_w 519
    //   624: invokestatic 294	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   627: putfield 348	org/telegram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   630: aload_2
    //   631: ldc_w 521
    //   634: ldc_w 522
    //   637: invokestatic 294	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   640: putfield 364	org/telegram/ui/DocumentSelectActivity$ListItem:subtitle	Ljava/lang/String;
    //   643: aload_2
    //   644: ldc_w 523
    //   647: putfield 358	org/telegram/ui/DocumentSelectActivity$ListItem:icon	I
    //   650: aload_2
    //   651: aconst_null
    //   652: putfield 351	org/telegram/ui/DocumentSelectActivity$ListItem:file	Ljava/io/File;
    //   655: aload_0
    //   656: getfield 77	org/telegram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   659: aload_2
    //   660: invokevirtual 367	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   663: pop
    //   664: aload_0
    //   665: getfield 148	org/telegram/ui/DocumentSelectActivity:listView	Landroid/widget/ListView;
    //   668: invokestatic 304	org/telegram/messenger/AndroidUtilities:clearDrawableAnimation	(Landroid/view/View;)V
    //   671: aload_0
    //   672: iconst_1
    //   673: putfield 160	org/telegram/ui/DocumentSelectActivity:scrolling	Z
    //   676: aload_0
    //   677: getfield 306	org/telegram/ui/DocumentSelectActivity:listAdapter	Lorg/telegram/ui/DocumentSelectActivity$ListAdapter;
    //   680: invokevirtual 309	org/telegram/ui/DocumentSelectActivity$ListAdapter:notifyDataSetChanged	()V
    //   683: return
    //   684: aload_3
    //   685: ldc_w 525
    //   688: ldc_w 526
    //   691: invokestatic 294	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   694: putfield 348	org/telegram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   697: aload_3
    //   698: ldc_w 527
    //   701: putfield 358	org/telegram/ui/DocumentSelectActivity$ListItem:icon	I
    //   704: goto -612 -> 92
    //   707: aload 4
    //   709: ldc_w 529
    //   712: ldc_w 530
    //   715: invokestatic 294	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   718: putfield 348	org/telegram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   721: goto -314 -> 407
    //   724: astore_2
    //   725: aload_3
    //   726: ifnull +7 -> 733
    //   729: aload_3
    //   730: invokevirtual 503	java/io/BufferedReader:close	()V
    //   733: aload_2
    //   734: athrow
    //   735: aload_3
    //   736: ifnull +74 -> 810
    //   739: aload_3
    //   740: invokevirtual 503	java/io/BufferedReader:close	()V
    //   743: goto -263 -> 480
    //   746: astore_2
    //   747: ldc -21
    //   749: aload_2
    //   750: invokestatic 241	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   753: goto -273 -> 480
    //   756: astore_2
    //   757: ldc -21
    //   759: aload_2
    //   760: invokestatic 241	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   763: goto -283 -> 480
    //   766: astore_3
    //   767: ldc -21
    //   769: aload_3
    //   770: invokestatic 241	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   773: goto -40 -> 733
    //   776: astore_2
    //   777: ldc -21
    //   779: aload_2
    //   780: invokestatic 241	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   783: goto -176 -> 607
    //   786: astore_2
    //   787: goto -10 -> 777
    //   790: astore 4
    //   792: aload_2
    //   793: astore_3
    //   794: aload 4
    //   796: astore_2
    //   797: goto -72 -> 725
    //   800: astore_2
    //   801: aload 4
    //   803: astore_3
    //   804: aload_2
    //   805: astore 4
    //   807: goto -344 -> 463
    //   810: goto -330 -> 480
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	813	0	this	DocumentSelectActivity
    //   303	27	1	i	int
    //   27	405	2	localObject1	Object
    //   451	4	2	localException1	Exception
    //   464	196	2	localObject2	Object
    //   724	10	2	localObject3	Object
    //   746	4	2	localException2	Exception
    //   756	4	2	localException3	Exception
    //   776	4	2	localException4	Exception
    //   786	7	2	localException5	Exception
    //   796	1	2	localObject4	Object
    //   800	5	2	localException6	Exception
    //   35	705	3	localObject5	Object
    //   766	4	3	localException7	Exception
    //   793	11	3	localObject6	Object
    //   127	316	4	localObject7	Object
    //   461	247	4	localException8	Exception
    //   790	12	4	localObject8	Object
    //   805	1	4	localException9	Exception
    //   339	21	5	str	String
    //   19	344	6	localHashSet	java.util.HashSet
    // Exception table:
    //   from	to	target	type
    //   369	407	451	java/lang/Exception
    //   407	448	451	java/lang/Exception
    //   707	721	451	java/lang/Exception
    //   147	152	461	java/lang/Exception
    //   156	176	461	java/lang/Exception
    //   176	278	461	java/lang/Exception
    //   281	304	461	java/lang/Exception
    //   312	341	461	java/lang/Exception
    //   344	359	461	java/lang/Exception
    //   362	369	461	java/lang/Exception
    //   452	458	461	java/lang/Exception
    //   147	152	724	finally
    //   156	176	724	finally
    //   176	278	724	finally
    //   281	304	724	finally
    //   312	341	724	finally
    //   344	359	724	finally
    //   362	369	724	finally
    //   369	407	724	finally
    //   407	448	724	finally
    //   452	458	724	finally
    //   707	721	724	finally
    //   739	743	746	java/lang/Exception
    //   476	480	756	java/lang/Exception
    //   729	733	766	java/lang/Exception
    //   540	571	776	java/lang/Exception
    //   571	607	786	java/lang/Exception
    //   129	147	790	finally
    //   465	472	790	finally
    //   129	147	800	java/lang/Exception
  }
  
  private void showErrorBox(String paramString)
  {
    if (getParentActivity() == null) {
      return;
    }
    new AlertDialog.Builder(getParentActivity()).setTitle(LocaleController.getString("AppName", 2131165299)).setMessage(paramString).setPositiveButton(LocaleController.getString("OK", 2131166044), null).show();
  }
  
  public View createView(Context paramContext)
  {
    if (!this.receiverRegistered)
    {
      this.receiverRegistered = true;
      localObject = new IntentFilter();
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_BAD_REMOVAL");
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_CHECKING");
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_EJECT");
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_MOUNTED");
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_NOFS");
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_REMOVED");
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_SHARED");
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_UNMOUNTABLE");
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_UNMOUNTED");
      ((IntentFilter)localObject).addDataScheme("file");
      ApplicationLoader.applicationContext.registerReceiver(this.receiver, (IntentFilter)localObject);
    }
    this.actionBar.setBackButtonDrawable(new BackDrawable(false));
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("SelectFile", 2131166232));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          if (DocumentSelectActivity.this.actionBar.isActionModeShowed())
          {
            DocumentSelectActivity.this.selectedFiles.clear();
            DocumentSelectActivity.this.actionBar.hideActionMode();
            DocumentSelectActivity.this.listView.invalidateViews();
          }
        }
        while ((paramAnonymousInt != 3) || (DocumentSelectActivity.this.delegate == null))
        {
          return;
          DocumentSelectActivity.this.finishFragment();
          return;
        }
        ArrayList localArrayList = new ArrayList();
        localArrayList.addAll(DocumentSelectActivity.this.selectedFiles.keySet());
        DocumentSelectActivity.this.delegate.didSelectFiles(DocumentSelectActivity.this, localArrayList);
      }
    });
    this.selectedFiles.clear();
    this.actionModeViews.clear();
    Object localObject = this.actionBar.createActionMode();
    this.selectedMessagesCountTextView = new NumberTextView(((ActionBarMenu)localObject).getContext());
    this.selectedMessagesCountTextView.setTextSize(18);
    this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.selectedMessagesCountTextView.setTextColor(-9211021);
    this.selectedMessagesCountTextView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    ((ActionBarMenu)localObject).addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0F, 65, 0, 0, 0));
    this.actionModeViews.add(((ActionBarMenu)localObject).addItem(3, 2130837703, -986896, null, AndroidUtilities.dp(54.0F)));
    this.fragmentView = getParentActivity().getLayoutInflater().inflate(2130903040, null, false);
    this.listAdapter = new ListAdapter(paramContext);
    this.emptyView = ((TextView)this.fragmentView.findViewById(2131492878));
    this.emptyView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.listView = ((ListView)this.fragmentView.findViewById(2131492877));
    this.listView.setEmptyView(this.emptyView);
    this.listView.setAdapter(this.listAdapter);
    this.listView.setOnScrollListener(new AbsListView.OnScrollListener()
    {
      public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      
      public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt)
      {
        paramAnonymousAbsListView = DocumentSelectActivity.this;
        if (paramAnonymousInt != 0) {}
        for (boolean bool = true;; bool = false)
        {
          DocumentSelectActivity.access$802(paramAnonymousAbsListView, bool);
          return;
        }
      }
    });
    this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
    {
      public boolean onItemLongClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if ((DocumentSelectActivity.this.actionBar.isActionModeShowed()) || (paramAnonymousInt < 0) || (paramAnonymousInt >= DocumentSelectActivity.this.items.size())) {
          return false;
        }
        paramAnonymousAdapterView = (DocumentSelectActivity.ListItem)DocumentSelectActivity.this.items.get(paramAnonymousInt);
        Object localObject = paramAnonymousAdapterView.file;
        if ((localObject != null) && (!((File)localObject).isDirectory()))
        {
          if (!((File)localObject).canRead())
          {
            DocumentSelectActivity.this.showErrorBox(LocaleController.getString("AccessError", 2131165206));
            return false;
          }
          if ((DocumentSelectActivity.this.sizeLimit != 0L) && (((File)localObject).length() > DocumentSelectActivity.this.sizeLimit))
          {
            DocumentSelectActivity.this.showErrorBox(LocaleController.formatString("FileUploadLimit", 2131165634, new Object[] { AndroidUtilities.formatFileSize(DocumentSelectActivity.this.sizeLimit) }));
            return false;
          }
          if (((File)localObject).length() == 0L) {
            return false;
          }
          DocumentSelectActivity.this.selectedFiles.put(((File)localObject).toString(), paramAnonymousAdapterView);
          DocumentSelectActivity.this.selectedMessagesCountTextView.setNumber(1, false);
          paramAnonymousAdapterView = new AnimatorSet();
          localObject = new ArrayList();
          paramAnonymousInt = 0;
          while (paramAnonymousInt < DocumentSelectActivity.this.actionModeViews.size())
          {
            View localView = (View)DocumentSelectActivity.this.actionModeViews.get(paramAnonymousInt);
            AndroidUtilities.clearDrawableAnimation(localView);
            ((ArrayList)localObject).add(ObjectAnimator.ofFloat(localView, "scaleY", new float[] { 0.1F, 1.0F }));
            paramAnonymousInt += 1;
          }
          paramAnonymousAdapterView.playTogether((Collection)localObject);
          paramAnonymousAdapterView.setDuration(250L);
          paramAnonymousAdapterView.start();
          DocumentSelectActivity.access$802(DocumentSelectActivity.this, false);
          if ((paramAnonymousView instanceof SharedDocumentCell)) {
            ((SharedDocumentCell)paramAnonymousView).setChecked(true, true);
          }
          DocumentSelectActivity.this.actionBar.showActionMode();
        }
        return true;
      }
    });
    this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if ((paramAnonymousInt < 0) || (paramAnonymousInt >= DocumentSelectActivity.this.items.size())) {}
        label466:
        label552:
        do
        {
          DocumentSelectActivity.ListItem localListItem;
          do
          {
            return;
            localListItem = (DocumentSelectActivity.ListItem)DocumentSelectActivity.this.items.get(paramAnonymousInt);
            File localFile = localListItem.file;
            if (localFile == null)
            {
              if (localListItem.icon == 2130837773)
              {
                if (DocumentSelectActivity.this.delegate != null) {
                  DocumentSelectActivity.this.delegate.startDocumentSelectActivity();
                }
                DocumentSelectActivity.this.finishFragment(false);
                return;
              }
              paramAnonymousAdapterView = (DocumentSelectActivity.HistoryEntry)DocumentSelectActivity.this.history.remove(DocumentSelectActivity.this.history.size() - 1);
              DocumentSelectActivity.this.actionBar.setTitle(paramAnonymousAdapterView.title);
              if (paramAnonymousAdapterView.dir != null) {
                DocumentSelectActivity.this.listFiles(paramAnonymousAdapterView.dir);
              }
              for (;;)
              {
                DocumentSelectActivity.this.listView.setSelectionFromTop(paramAnonymousAdapterView.scrollItem, paramAnonymousAdapterView.scrollOffset);
                return;
                DocumentSelectActivity.this.listRoots();
              }
            }
            if (localFile.isDirectory())
            {
              paramAnonymousAdapterView = new DocumentSelectActivity.HistoryEntry(DocumentSelectActivity.this, null);
              paramAnonymousAdapterView.scrollItem = DocumentSelectActivity.this.listView.getFirstVisiblePosition();
              paramAnonymousAdapterView.scrollOffset = DocumentSelectActivity.this.listView.getChildAt(0).getTop();
              paramAnonymousAdapterView.dir = DocumentSelectActivity.this.currentDir;
              paramAnonymousAdapterView.title = DocumentSelectActivity.this.actionBar.getTitle();
              DocumentSelectActivity.this.history.add(paramAnonymousAdapterView);
              if (!DocumentSelectActivity.this.listFiles(localFile))
              {
                DocumentSelectActivity.this.history.remove(paramAnonymousAdapterView);
                return;
              }
              DocumentSelectActivity.this.actionBar.setTitle(localListItem.title);
              DocumentSelectActivity.this.listView.setSelection(0);
              return;
            }
            paramAnonymousAdapterView = localFile;
            if (!localFile.canRead())
            {
              DocumentSelectActivity.this.showErrorBox(LocaleController.getString("AccessError", 2131165206));
              paramAnonymousAdapterView = new File("/mnt/sdcard");
            }
            if ((DocumentSelectActivity.this.sizeLimit != 0L) && (paramAnonymousAdapterView.length() > DocumentSelectActivity.this.sizeLimit))
            {
              DocumentSelectActivity.this.showErrorBox(LocaleController.formatString("FileUploadLimit", 2131165634, new Object[] { AndroidUtilities.formatFileSize(DocumentSelectActivity.this.sizeLimit) }));
              return;
            }
          } while (paramAnonymousAdapterView.length() == 0L);
          if (DocumentSelectActivity.this.actionBar.isActionModeShowed())
          {
            if (DocumentSelectActivity.this.selectedFiles.containsKey(paramAnonymousAdapterView.toString()))
            {
              DocumentSelectActivity.this.selectedFiles.remove(paramAnonymousAdapterView.toString());
              if (!DocumentSelectActivity.this.selectedFiles.isEmpty()) {
                break label552;
              }
              DocumentSelectActivity.this.actionBar.hideActionMode();
            }
            for (;;)
            {
              DocumentSelectActivity.access$802(DocumentSelectActivity.this, false);
              if (!(paramAnonymousView instanceof SharedDocumentCell)) {
                break;
              }
              ((SharedDocumentCell)paramAnonymousView).setChecked(DocumentSelectActivity.this.selectedFiles.containsKey(localListItem.file.toString()), true);
              return;
              DocumentSelectActivity.this.selectedFiles.put(paramAnonymousAdapterView.toString(), localListItem);
              break label466;
              DocumentSelectActivity.this.selectedMessagesCountTextView.setNumber(DocumentSelectActivity.this.selectedFiles.size(), true);
            }
          }
        } while (DocumentSelectActivity.this.delegate == null);
        paramAnonymousView = new ArrayList();
        paramAnonymousView.add(paramAnonymousAdapterView.getAbsolutePath());
        DocumentSelectActivity.this.delegate.didSelectFiles(DocumentSelectActivity.this, paramAnonymousView);
      }
    });
    listRoots();
    return this.fragmentView;
  }
  
  public boolean onBackPressed()
  {
    if (this.history.size() > 0)
    {
      HistoryEntry localHistoryEntry = (HistoryEntry)this.history.remove(this.history.size() - 1);
      this.actionBar.setTitle(localHistoryEntry.title);
      if (localHistoryEntry.dir != null) {
        listFiles(localHistoryEntry.dir);
      }
      for (;;)
      {
        this.listView.setSelectionFromTop(localHistoryEntry.scrollItem, localHistoryEntry.scrollOffset);
        return false;
        listRoots();
      }
    }
    return super.onBackPressed();
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (this.listView != null) {
      this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          DocumentSelectActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
          DocumentSelectActivity.this.fixLayoutInternal();
          return true;
        }
      });
    }
  }
  
  public void onFragmentDestroy()
  {
    try
    {
      if (this.receiverRegistered) {
        ApplicationLoader.applicationContext.unregisterReceiver(this.receiver);
      }
      super.onFragmentDestroy();
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
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
    fixLayoutInternal();
  }
  
  public void setDelegate(DocumentSelectActivityDelegate paramDocumentSelectActivityDelegate)
  {
    this.delegate = paramDocumentSelectActivityDelegate;
  }
  
  public static abstract interface DocumentSelectActivityDelegate
  {
    public abstract void didSelectFiles(DocumentSelectActivity paramDocumentSelectActivity, ArrayList<String> paramArrayList);
    
    public abstract void startDocumentSelectActivity();
  }
  
  private class HistoryEntry
  {
    File dir;
    int scrollItem;
    int scrollOffset;
    String title;
    
    private HistoryEntry() {}
  }
  
  private class ListAdapter
    extends BaseFragmentAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getCount()
    {
      return DocumentSelectActivity.this.items.size();
    }
    
    public Object getItem(int paramInt)
    {
      return DocumentSelectActivity.this.items.get(paramInt);
    }
    
    public long getItemId(int paramInt)
    {
      return 0L;
    }
    
    public int getItemViewType(int paramInt)
    {
      if (((DocumentSelectActivity.ListItem)DocumentSelectActivity.this.items.get(paramInt)).subtitle.length() > 0) {
        return 0;
      }
      return 1;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      boolean bool1 = true;
      paramViewGroup = paramView;
      if (paramView == null) {
        paramViewGroup = new SharedDocumentCell(this.mContext);
      }
      paramView = (SharedDocumentCell)paramViewGroup;
      DocumentSelectActivity.ListItem localListItem = (DocumentSelectActivity.ListItem)DocumentSelectActivity.this.items.get(paramInt);
      boolean bool2;
      if (localListItem.icon != 0)
      {
        ((SharedDocumentCell)paramViewGroup).setTextAndValueAndTypeAndThumb(localListItem.title, localListItem.subtitle, null, null, localListItem.icon);
        if ((localListItem.file == null) || (!DocumentSelectActivity.this.actionBar.isActionModeShowed())) {
          break label198;
        }
        bool2 = DocumentSelectActivity.this.selectedFiles.containsKey(localListItem.file.toString());
        if (DocumentSelectActivity.this.scrolling) {
          break label192;
        }
      }
      label192:
      for (bool1 = true;; bool1 = false)
      {
        paramView.setChecked(bool2, bool1);
        return paramViewGroup;
        String str = localListItem.ext.toUpperCase().substring(0, Math.min(localListItem.ext.length(), 4));
        ((SharedDocumentCell)paramViewGroup).setTextAndValueAndTypeAndThumb(localListItem.title, localListItem.subtitle, str, localListItem.thumb, 0);
        break;
      }
      label198:
      if (!DocumentSelectActivity.this.scrolling) {}
      for (;;)
      {
        paramView.setChecked(false, bool1);
        return paramViewGroup;
        bool1 = false;
      }
    }
    
    public int getViewTypeCount()
    {
      return 2;
    }
  }
  
  private class ListItem
  {
    String ext = "";
    File file;
    int icon;
    String subtitle = "";
    String thumb;
    String title;
    
    private ListItem() {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/DocumentSelectActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */