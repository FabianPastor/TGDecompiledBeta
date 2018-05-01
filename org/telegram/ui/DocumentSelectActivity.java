package org.telegram.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Environment;
import android.os.StatFs;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class DocumentSelectActivity
  extends BaseFragment
{
  private static final int done = 3;
  private ArrayList<View> actionModeViews = new ArrayList();
  private File currentDir;
  private DocumentSelectActivityDelegate delegate;
  private EmptyTextProgressView emptyView;
  private ArrayList<HistoryEntry> history = new ArrayList();
  private ArrayList<ListItem> items = new ArrayList();
  private LinearLayoutManager layoutManager;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
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
            if (DocumentSelectActivity.this.currentDir == null) {
              DocumentSelectActivity.this.listRoots();
            }
            for (;;)
            {
              return;
              DocumentSelectActivity.this.listFiles(DocumentSelectActivity.this.currentDir);
            }
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
            }
          }
        }
      };
      if ("android.intent.action.MEDIA_UNMOUNTED".equals(paramAnonymousIntent.getAction())) {
        DocumentSelectActivity.this.listView.postDelayed(paramAnonymousContext, 1000L);
      }
      for (;;)
      {
        return;
        paramAnonymousContext.run();
      }
    }
  };
  private boolean receiverRegistered = false;
  private ArrayList<ListItem> recentItems = new ArrayList();
  private boolean scrolling;
  private HashMap<String, ListItem> selectedFiles = new HashMap();
  private NumberTextView selectedMessagesCountTextView;
  private long sizeLimit = 1610612736L;
  
  private void fixLayoutInternal()
  {
    if (this.selectedMessagesCountTextView == null) {}
    for (;;)
    {
      return;
      if ((!AndroidUtilities.isTablet()) && (ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == 2)) {
        this.selectedMessagesCountTextView.setTextSize(18);
      } else {
        this.selectedMessagesCountTextView.setTextSize(20);
      }
    }
  }
  
  private String getRootSubtitle(String paramString)
  {
    for (;;)
    {
      try
      {
        localObject = new android/os/StatFs;
        ((StatFs)localObject).<init>(paramString);
        l1 = ((StatFs)localObject).getBlockCount() * ((StatFs)localObject).getBlockSize();
        l2 = ((StatFs)localObject).getAvailableBlocks();
        l3 = ((StatFs)localObject).getBlockSize();
        if (l1 != 0L) {
          continue;
        }
        paramString = "";
      }
      catch (Exception localException)
      {
        Object localObject;
        long l1;
        long l2;
        long l3;
        FileLog.e(localException);
        continue;
      }
      return paramString;
      localObject = LocaleController.formatString("FreeOfTotal", NUM, new Object[] { AndroidUtilities.formatFileSize(l2 * l3), AndroidUtilities.formatFileSize(l1) });
      paramString = (String)localObject;
    }
  }
  
  private boolean listFiles(File paramFile)
  {
    boolean bool;
    if (!paramFile.canRead()) {
      if (((paramFile.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().toString())) || (paramFile.getAbsolutePath().startsWith("/sdcard")) || (paramFile.getAbsolutePath().startsWith("/mnt/sdcard"))) && (!Environment.getExternalStorageState().equals("mounted")) && (!Environment.getExternalStorageState().equals("mounted_ro")))
      {
        this.currentDir = paramFile;
        this.items.clear();
        if ("shared".equals(Environment.getExternalStorageState()))
        {
          this.emptyView.setText(LocaleController.getString("UsbActive", NUM));
          AndroidUtilities.clearDrawableAnimation(this.listView);
          this.scrolling = true;
          this.listAdapter.notifyDataSetChanged();
          bool = true;
        }
      }
    }
    for (;;)
    {
      return bool;
      this.emptyView.setText(LocaleController.getString("NotMounted", NUM));
      break;
      showErrorBox(LocaleController.getString("AccessError", NUM));
      bool = false;
      continue;
      try
      {
        localObject = paramFile.listFiles();
        if (localObject != null) {
          break label214;
        }
        showErrorBox(LocaleController.getString("UnknownError", NUM));
        bool = false;
      }
      catch (Exception paramFile)
      {
        showErrorBox(paramFile.getLocalizedMessage());
        bool = false;
      }
    }
    label214:
    this.currentDir = paramFile;
    this.items.clear();
    Arrays.sort((Object[])localObject, new Comparator()
    {
      public int compare(File paramAnonymousFile1, File paramAnonymousFile2)
      {
        int i;
        if (paramAnonymousFile1.isDirectory() != paramAnonymousFile2.isDirectory()) {
          if (paramAnonymousFile1.isDirectory()) {
            i = -1;
          }
        }
        for (;;)
        {
          return i;
          i = 1;
          continue;
          i = paramAnonymousFile1.getName().compareToIgnoreCase(paramAnonymousFile2.getName());
        }
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
        i++;
        break;
        localListItem = new ListItem(null);
        localListItem.title = localFile.getName();
        localListItem.file = localFile;
        if (!localFile.isDirectory()) {
          break label344;
        }
        localListItem.icon = NUM;
        localListItem.subtitle = LocaleController.getString("Folder", NUM);
        this.items.add(localListItem);
      }
      label344:
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
    Object localObject = new ListItem(null);
    ((ListItem)localObject).title = "..";
    if (this.history.size() > 0)
    {
      paramFile = (HistoryEntry)this.history.get(this.history.size() - 1);
      if (paramFile.dir == null) {
        ((ListItem)localObject).subtitle = LocaleController.getString("Folder", NUM);
      }
    }
    for (;;)
    {
      ((ListItem)localObject).icon = NUM;
      ((ListItem)localObject).file = null;
      this.items.add(0, localObject);
      AndroidUtilities.clearDrawableAnimation(this.listView);
      this.scrolling = true;
      this.listAdapter.notifyDataSetChanged();
      bool = true;
      break;
      ((ListItem)localObject).subtitle = paramFile.dir.toString();
      continue;
      ((ListItem)localObject).subtitle = LocaleController.getString("Folder", NUM);
    }
  }
  
  /* Error */
  @android.annotation.SuppressLint({"NewApi"})
  private void listRoots()
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: putfield 107	org/telegram/ui/DocumentSelectActivity:currentDir	Ljava/io/File;
    //   5: aload_0
    //   6: getfield 80	org/telegram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   9: invokevirtual 295	java/util/ArrayList:clear	()V
    //   12: new 425	java/util/HashSet
    //   15: dup
    //   16: invokespecial 426	java/util/HashSet:<init>	()V
    //   19: astore_1
    //   20: invokestatic 268	android/os/Environment:getExternalStorageDirectory	()Ljava/io/File;
    //   23: invokevirtual 429	java/io/File:getPath	()Ljava/lang/String;
    //   26: astore_2
    //   27: invokestatic 432	android/os/Environment:isExternalStorageRemovable	()Z
    //   30: pop
    //   31: invokestatic 284	android/os/Environment:getExternalStorageState	()Ljava/lang/String;
    //   34: astore_3
    //   35: aload_3
    //   36: ldc_w 286
    //   39: invokevirtual 290	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   42: ifne +13 -> 55
    //   45: aload_3
    //   46: ldc_w 292
    //   49: invokevirtual 290	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   52: ifeq +70 -> 122
    //   55: new 35	org/telegram/ui/DocumentSelectActivity$ListItem
    //   58: dup
    //   59: aload_0
    //   60: aconst_null
    //   61: invokespecial 350	org/telegram/ui/DocumentSelectActivity$ListItem:<init>	(Lorg/telegram/ui/DocumentSelectActivity;Lorg/telegram/ui/DocumentSelectActivity$1;)V
    //   64: astore_3
    //   65: invokestatic 432	android/os/Environment:isExternalStorageRemovable	()Z
    //   68: ifeq +652 -> 720
    //   71: aload_3
    //   72: ldc_w 434
    //   75: ldc_w 435
    //   78: invokestatic 306	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   81: putfield 354	org/telegram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   84: aload_3
    //   85: ldc_w 436
    //   88: putfield 364	org/telegram/ui/DocumentSelectActivity$ListItem:icon	I
    //   91: aload_3
    //   92: aload_0
    //   93: aload_2
    //   94: invokespecial 438	org/telegram/ui/DocumentSelectActivity:getRootSubtitle	(Ljava/lang/String;)Ljava/lang/String;
    //   97: putfield 370	org/telegram/ui/DocumentSelectActivity$ListItem:subtitle	Ljava/lang/String;
    //   100: aload_3
    //   101: invokestatic 268	android/os/Environment:getExternalStorageDirectory	()Ljava/io/File;
    //   104: putfield 357	org/telegram/ui/DocumentSelectActivity$ListItem:file	Ljava/io/File;
    //   107: aload_0
    //   108: getfield 80	org/telegram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   111: aload_3
    //   112: invokevirtual 373	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   115: pop
    //   116: aload_1
    //   117: aload_2
    //   118: invokevirtual 439	java/util/HashSet:add	(Ljava/lang/Object;)Z
    //   121: pop
    //   122: aconst_null
    //   123: astore 4
    //   125: aconst_null
    //   126: astore 5
    //   128: aload 4
    //   130: astore_2
    //   131: new 441	java/io/BufferedReader
    //   134: astore_3
    //   135: aload 4
    //   137: astore_2
    //   138: new 443	java/io/FileReader
    //   141: astore 6
    //   143: aload 4
    //   145: astore_2
    //   146: aload 6
    //   148: ldc_w 445
    //   151: invokespecial 446	java/io/FileReader:<init>	(Ljava/lang/String;)V
    //   154: aload 4
    //   156: astore_2
    //   157: aload_3
    //   158: aload 6
    //   160: invokespecial 449	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   163: aload_3
    //   164: invokevirtual 452	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   167: astore_2
    //   168: aload_2
    //   169: ifnull +607 -> 776
    //   172: aload_2
    //   173: ldc_w 454
    //   176: invokevirtual 458	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   179: ifne +13 -> 192
    //   182: aload_2
    //   183: ldc_w 460
    //   186: invokevirtual 458	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   189: ifeq -26 -> 163
    //   192: getstatic 465	org/telegram/messenger/BuildVars:LOGS_ENABLED	Z
    //   195: ifeq +7 -> 202
    //   198: aload_2
    //   199: invokestatic 468	org/telegram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   202: new 470	java/util/StringTokenizer
    //   205: astore 4
    //   207: aload 4
    //   209: aload_2
    //   210: ldc_w 472
    //   213: invokespecial 475	java/util/StringTokenizer:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   216: aload 4
    //   218: invokevirtual 478	java/util/StringTokenizer:nextToken	()Ljava/lang/String;
    //   221: pop
    //   222: aload 4
    //   224: invokevirtual 478	java/util/StringTokenizer:nextToken	()Ljava/lang/String;
    //   227: astore 4
    //   229: aload_1
    //   230: aload 4
    //   232: invokevirtual 480	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   235: ifne -72 -> 163
    //   238: aload_2
    //   239: ldc_w 482
    //   242: invokevirtual 458	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   245: ifeq -82 -> 163
    //   248: aload_2
    //   249: ldc_w 484
    //   252: invokevirtual 458	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   255: ifne -92 -> 163
    //   258: aload_2
    //   259: ldc_w 486
    //   262: invokevirtual 458	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   265: ifne -102 -> 163
    //   268: aload_2
    //   269: ldc_w 488
    //   272: invokevirtual 458	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   275: ifne -112 -> 163
    //   278: aload_2
    //   279: ldc_w 490
    //   282: invokevirtual 458	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   285: ifne -122 -> 163
    //   288: aload_2
    //   289: ldc_w 492
    //   292: invokevirtual 458	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   295: ifne -132 -> 163
    //   298: new 255	java/io/File
    //   301: astore 5
    //   303: aload 5
    //   305: aload 4
    //   307: invokespecial 493	java/io/File:<init>	(Ljava/lang/String;)V
    //   310: aload 4
    //   312: astore_2
    //   313: aload 5
    //   315: invokevirtual 360	java/io/File:isDirectory	()Z
    //   318: ifne +79 -> 397
    //   321: aload 4
    //   323: bipush 47
    //   325: invokevirtual 496	java/lang/String:lastIndexOf	(I)I
    //   328: istore 7
    //   330: aload 4
    //   332: astore_2
    //   333: iload 7
    //   335: iconst_m1
    //   336: if_icmpeq +61 -> 397
    //   339: new 498	java/lang/StringBuilder
    //   342: astore_2
    //   343: aload_2
    //   344: invokespecial 499	java/lang/StringBuilder:<init>	()V
    //   347: aload_2
    //   348: ldc_w 501
    //   351: invokevirtual 505	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   354: aload 4
    //   356: iload 7
    //   358: iconst_1
    //   359: iadd
    //   360: invokevirtual 509	java/lang/String:substring	(I)Ljava/lang/String;
    //   363: invokevirtual 505	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   366: invokevirtual 510	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   369: astore 5
    //   371: new 255	java/io/File
    //   374: astore 6
    //   376: aload 6
    //   378: aload 5
    //   380: invokespecial 493	java/io/File:<init>	(Ljava/lang/String;)V
    //   383: aload 4
    //   385: astore_2
    //   386: aload 6
    //   388: invokevirtual 360	java/io/File:isDirectory	()Z
    //   391: ifeq +6 -> 397
    //   394: aload 5
    //   396: astore_2
    //   397: aload_1
    //   398: aload_2
    //   399: invokevirtual 439	java/util/HashSet:add	(Ljava/lang/Object;)Z
    //   402: pop
    //   403: new 35	org/telegram/ui/DocumentSelectActivity$ListItem
    //   406: astore 4
    //   408: aload 4
    //   410: aload_0
    //   411: aconst_null
    //   412: invokespecial 350	org/telegram/ui/DocumentSelectActivity$ListItem:<init>	(Lorg/telegram/ui/DocumentSelectActivity;Lorg/telegram/ui/DocumentSelectActivity$1;)V
    //   415: aload_2
    //   416: invokevirtual 389	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   419: ldc_w 512
    //   422: invokevirtual 458	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   425: ifeq +318 -> 743
    //   428: aload 4
    //   430: ldc_w 434
    //   433: ldc_w 435
    //   436: invokestatic 306	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   439: putfield 354	org/telegram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   442: aload 4
    //   444: ldc_w 436
    //   447: putfield 364	org/telegram/ui/DocumentSelectActivity$ListItem:icon	I
    //   450: aload 4
    //   452: aload_0
    //   453: aload_2
    //   454: invokespecial 438	org/telegram/ui/DocumentSelectActivity:getRootSubtitle	(Ljava/lang/String;)Ljava/lang/String;
    //   457: putfield 370	org/telegram/ui/DocumentSelectActivity$ListItem:subtitle	Ljava/lang/String;
    //   460: new 255	java/io/File
    //   463: astore 5
    //   465: aload 5
    //   467: aload_2
    //   468: invokespecial 493	java/io/File:<init>	(Ljava/lang/String;)V
    //   471: aload 4
    //   473: aload 5
    //   475: putfield 357	org/telegram/ui/DocumentSelectActivity$ListItem:file	Ljava/io/File;
    //   478: aload_0
    //   479: getfield 80	org/telegram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   482: aload 4
    //   484: invokevirtual 373	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   487: pop
    //   488: goto -325 -> 163
    //   491: astore_2
    //   492: aload_2
    //   493: invokestatic 253	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   496: goto -333 -> 163
    //   499: astore 4
    //   501: aload_3
    //   502: astore_2
    //   503: aload 4
    //   505: invokestatic 253	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   508: aload_3
    //   509: ifnull +7 -> 516
    //   512: aload_3
    //   513: invokevirtual 515	java/io/BufferedReader:close	()V
    //   516: new 35	org/telegram/ui/DocumentSelectActivity$ListItem
    //   519: dup
    //   520: aload_0
    //   521: aconst_null
    //   522: invokespecial 350	org/telegram/ui/DocumentSelectActivity$ListItem:<init>	(Lorg/telegram/ui/DocumentSelectActivity;Lorg/telegram/ui/DocumentSelectActivity$1;)V
    //   525: astore_2
    //   526: aload_2
    //   527: ldc_w 517
    //   530: putfield 354	org/telegram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   533: aload_2
    //   534: ldc_w 519
    //   537: ldc_w 520
    //   540: invokestatic 306	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   543: putfield 370	org/telegram/ui/DocumentSelectActivity$ListItem:subtitle	Ljava/lang/String;
    //   546: aload_2
    //   547: ldc_w 361
    //   550: putfield 364	org/telegram/ui/DocumentSelectActivity$ListItem:icon	I
    //   553: aload_2
    //   554: new 255	java/io/File
    //   557: dup
    //   558: ldc_w 517
    //   561: invokespecial 493	java/io/File:<init>	(Ljava/lang/String;)V
    //   564: putfield 357	org/telegram/ui/DocumentSelectActivity$ListItem:file	Ljava/io/File;
    //   567: aload_0
    //   568: getfield 80	org/telegram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   571: aload_2
    //   572: invokevirtual 373	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   575: pop
    //   576: new 255	java/io/File
    //   579: astore_3
    //   580: aload_3
    //   581: invokestatic 268	android/os/Environment:getExternalStorageDirectory	()Ljava/io/File;
    //   584: ldc_w 522
    //   587: invokespecial 525	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   590: aload_3
    //   591: invokevirtual 528	java/io/File:exists	()Z
    //   594: ifeq +49 -> 643
    //   597: new 35	org/telegram/ui/DocumentSelectActivity$ListItem
    //   600: astore_2
    //   601: aload_2
    //   602: aload_0
    //   603: aconst_null
    //   604: invokespecial 350	org/telegram/ui/DocumentSelectActivity$ListItem:<init>	(Lorg/telegram/ui/DocumentSelectActivity;Lorg/telegram/ui/DocumentSelectActivity$1;)V
    //   607: aload_2
    //   608: ldc_w 522
    //   611: putfield 354	org/telegram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   614: aload_2
    //   615: aload_3
    //   616: invokevirtual 271	java/io/File:toString	()Ljava/lang/String;
    //   619: putfield 370	org/telegram/ui/DocumentSelectActivity$ListItem:subtitle	Ljava/lang/String;
    //   622: aload_2
    //   623: ldc_w 361
    //   626: putfield 364	org/telegram/ui/DocumentSelectActivity$ListItem:icon	I
    //   629: aload_2
    //   630: aload_3
    //   631: putfield 357	org/telegram/ui/DocumentSelectActivity$ListItem:file	Ljava/io/File;
    //   634: aload_0
    //   635: getfield 80	org/telegram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   638: aload_2
    //   639: invokevirtual 373	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   642: pop
    //   643: new 35	org/telegram/ui/DocumentSelectActivity$ListItem
    //   646: dup
    //   647: aload_0
    //   648: aconst_null
    //   649: invokespecial 350	org/telegram/ui/DocumentSelectActivity$ListItem:<init>	(Lorg/telegram/ui/DocumentSelectActivity;Lorg/telegram/ui/DocumentSelectActivity$1;)V
    //   652: astore_2
    //   653: aload_2
    //   654: ldc_w 530
    //   657: ldc_w 531
    //   660: invokestatic 306	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   663: putfield 354	org/telegram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   666: aload_2
    //   667: ldc_w 533
    //   670: ldc_w 534
    //   673: invokestatic 306	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   676: putfield 370	org/telegram/ui/DocumentSelectActivity$ListItem:subtitle	Ljava/lang/String;
    //   679: aload_2
    //   680: ldc_w 535
    //   683: putfield 364	org/telegram/ui/DocumentSelectActivity$ListItem:icon	I
    //   686: aload_2
    //   687: aconst_null
    //   688: putfield 357	org/telegram/ui/DocumentSelectActivity$ListItem:file	Ljava/io/File;
    //   691: aload_0
    //   692: getfield 80	org/telegram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   695: aload_2
    //   696: invokevirtual 373	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   699: pop
    //   700: aload_0
    //   701: getfield 162	org/telegram/ui/DocumentSelectActivity:listView	Lorg/telegram/ui/Components/RecyclerListView;
    //   704: invokestatic 315	org/telegram/messenger/AndroidUtilities:clearDrawableAnimation	(Landroid/view/View;)V
    //   707: aload_0
    //   708: iconst_1
    //   709: putfield 174	org/telegram/ui/DocumentSelectActivity:scrolling	Z
    //   712: aload_0
    //   713: getfield 115	org/telegram/ui/DocumentSelectActivity:listAdapter	Lorg/telegram/ui/DocumentSelectActivity$ListAdapter;
    //   716: invokevirtual 318	org/telegram/ui/DocumentSelectActivity$ListAdapter:notifyDataSetChanged	()V
    //   719: return
    //   720: aload_3
    //   721: ldc_w 537
    //   724: ldc_w 538
    //   727: invokestatic 306	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   730: putfield 354	org/telegram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   733: aload_3
    //   734: ldc_w 539
    //   737: putfield 364	org/telegram/ui/DocumentSelectActivity$ListItem:icon	I
    //   740: goto -649 -> 91
    //   743: aload 4
    //   745: ldc_w 541
    //   748: ldc_w 542
    //   751: invokestatic 306	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   754: putfield 354	org/telegram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   757: goto -315 -> 442
    //   760: astore_2
    //   761: aload_3
    //   762: astore 4
    //   764: aload 4
    //   766: ifnull +8 -> 774
    //   769: aload 4
    //   771: invokevirtual 515	java/io/BufferedReader:close	()V
    //   774: aload_2
    //   775: athrow
    //   776: aload_3
    //   777: ifnull +63 -> 840
    //   780: aload_3
    //   781: invokevirtual 515	java/io/BufferedReader:close	()V
    //   784: goto -268 -> 516
    //   787: astore_2
    //   788: aload_2
    //   789: invokestatic 253	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   792: goto -276 -> 516
    //   795: astore_2
    //   796: aload_2
    //   797: invokestatic 253	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   800: goto -284 -> 516
    //   803: astore_3
    //   804: aload_3
    //   805: invokestatic 253	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   808: goto -34 -> 774
    //   811: astore_2
    //   812: aload_2
    //   813: invokestatic 253	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   816: goto -173 -> 643
    //   819: astore_2
    //   820: goto -8 -> 812
    //   823: astore_3
    //   824: aload_2
    //   825: astore 4
    //   827: aload_3
    //   828: astore_2
    //   829: goto -65 -> 764
    //   832: astore 4
    //   834: aload 5
    //   836: astore_3
    //   837: goto -336 -> 501
    //   840: goto -324 -> 516
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	843	0	this	DocumentSelectActivity
    //   19	379	1	localHashSet	java.util.HashSet
    //   26	442	2	localObject1	Object
    //   491	2	2	localException1	Exception
    //   502	194	2	localObject2	Object
    //   760	15	2	localObject3	Object
    //   787	2	2	localException2	Exception
    //   795	2	2	localException3	Exception
    //   811	2	2	localException4	Exception
    //   819	6	2	localException5	Exception
    //   828	1	2	localObject4	Object
    //   34	747	3	localObject5	Object
    //   803	2	3	localException6	Exception
    //   823	5	3	localObject6	Object
    //   836	1	3	localObject7	Object
    //   123	360	4	localObject8	Object
    //   499	245	4	localException7	Exception
    //   762	64	4	localObject9	Object
    //   832	1	4	localException8	Exception
    //   126	709	5	localObject10	Object
    //   141	246	6	localObject11	Object
    //   328	32	7	i	int
    // Exception table:
    //   from	to	target	type
    //   403	442	491	java/lang/Exception
    //   442	488	491	java/lang/Exception
    //   743	757	491	java/lang/Exception
    //   163	168	499	java/lang/Exception
    //   172	192	499	java/lang/Exception
    //   192	202	499	java/lang/Exception
    //   202	310	499	java/lang/Exception
    //   313	330	499	java/lang/Exception
    //   339	383	499	java/lang/Exception
    //   386	394	499	java/lang/Exception
    //   397	403	499	java/lang/Exception
    //   492	496	499	java/lang/Exception
    //   163	168	760	finally
    //   172	192	760	finally
    //   192	202	760	finally
    //   202	310	760	finally
    //   313	330	760	finally
    //   339	383	760	finally
    //   386	394	760	finally
    //   397	403	760	finally
    //   403	442	760	finally
    //   442	488	760	finally
    //   492	496	760	finally
    //   743	757	760	finally
    //   780	784	787	java/lang/Exception
    //   512	516	795	java/lang/Exception
    //   769	774	803	java/lang/Exception
    //   576	607	811	java/lang/Exception
    //   607	643	819	java/lang/Exception
    //   131	135	823	finally
    //   138	143	823	finally
    //   146	154	823	finally
    //   157	163	823	finally
    //   503	508	823	finally
    //   131	135	832	java/lang/Exception
    //   138	143	832	java/lang/Exception
    //   146	154	832	java/lang/Exception
    //   157	163	832	java/lang/Exception
  }
  
  private void showErrorBox(String paramString)
  {
    if (getParentActivity() == null) {}
    for (;;)
    {
      return;
      new AlertDialog.Builder(getParentActivity()).setTitle(LocaleController.getString("AppName", NUM)).setMessage(paramString).setPositiveButton(LocaleController.getString("OK", NUM), null).show();
    }
  }
  
  public View createView(Context paramContext)
  {
    if (!this.receiverRegistered)
    {
      this.receiverRegistered = true;
      localObject1 = new IntentFilter();
      ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_BAD_REMOVAL");
      ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_CHECKING");
      ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_EJECT");
      ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_MOUNTED");
      ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_NOFS");
      ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_REMOVED");
      ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_SHARED");
      ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_UNMOUNTABLE");
      ((IntentFilter)localObject1).addAction("android.intent.action.MEDIA_UNMOUNTED");
      ((IntentFilter)localObject1).addDataScheme("file");
      ApplicationLoader.applicationContext.registerReceiver(this.receiver, (IntentFilter)localObject1);
    }
    this.actionBar.setBackButtonDrawable(new BackDrawable(false));
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("SelectFile", NUM));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        Object localObject;
        if (paramAnonymousInt == -1)
        {
          if (DocumentSelectActivity.this.actionBar.isActionModeShowed())
          {
            DocumentSelectActivity.this.selectedFiles.clear();
            DocumentSelectActivity.this.actionBar.hideActionMode();
            int i = DocumentSelectActivity.this.listView.getChildCount();
            for (paramAnonymousInt = 0; paramAnonymousInt < i; paramAnonymousInt++)
            {
              localObject = DocumentSelectActivity.this.listView.getChildAt(paramAnonymousInt);
              if ((localObject instanceof SharedDocumentCell)) {
                ((SharedDocumentCell)localObject).setChecked(false, true);
              }
            }
          }
          DocumentSelectActivity.this.finishFragment();
        }
        for (;;)
        {
          return;
          if ((paramAnonymousInt == 3) && (DocumentSelectActivity.this.delegate != null))
          {
            localObject = new ArrayList();
            ((ArrayList)localObject).addAll(DocumentSelectActivity.this.selectedFiles.keySet());
            DocumentSelectActivity.this.delegate.didSelectFiles(DocumentSelectActivity.this, (ArrayList)localObject);
            localObject = DocumentSelectActivity.this.selectedFiles.values().iterator();
            while (((Iterator)localObject).hasNext()) {
              ((DocumentSelectActivity.ListItem)((Iterator)localObject).next()).date = System.currentTimeMillis();
            }
          }
        }
      }
    });
    this.selectedFiles.clear();
    this.actionModeViews.clear();
    Object localObject1 = this.actionBar.createActionMode();
    this.selectedMessagesCountTextView = new NumberTextView(((ActionBarMenu)localObject1).getContext());
    this.selectedMessagesCountTextView.setTextSize(18);
    this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.selectedMessagesCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
    this.selectedMessagesCountTextView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    ((ActionBarMenu)localObject1).addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0F, 65, 0, 0, 0));
    this.actionModeViews.add(((ActionBarMenu)localObject1).addItemWithWidth(3, NUM, AndroidUtilities.dp(54.0F)));
    this.fragmentView = new FrameLayout(paramContext);
    localObject1 = (FrameLayout)this.fragmentView;
    this.emptyView = new EmptyTextProgressView(paramContext);
    this.emptyView.showTextView();
    ((FrameLayout)localObject1).addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setVerticalScrollBarEnabled(false);
    RecyclerListView localRecyclerListView = this.listView;
    Object localObject2 = new LinearLayoutManager(paramContext, 1, false);
    this.layoutManager = ((LinearLayoutManager)localObject2);
    localRecyclerListView.setLayoutManager((RecyclerView.LayoutManager)localObject2);
    this.listView.setEmptyView(this.emptyView);
    localObject2 = this.listView;
    paramContext = new ListAdapter(paramContext);
    this.listAdapter = paramContext;
    ((RecyclerListView)localObject2).setAdapter(paramContext);
    ((FrameLayout)localObject1).addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
      {
        paramAnonymousRecyclerView = DocumentSelectActivity.this;
        if (paramAnonymousInt != 0) {}
        for (boolean bool = true;; bool = false)
        {
          DocumentSelectActivity.access$802(paramAnonymousRecyclerView, bool);
          return;
        }
      }
    });
    this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
    {
      public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        boolean bool;
        if (DocumentSelectActivity.this.actionBar.isActionModeShowed()) {
          bool = false;
        }
        for (;;)
        {
          return bool;
          Object localObject1 = DocumentSelectActivity.this.listAdapter.getItem(paramAnonymousInt);
          if (localObject1 == null)
          {
            bool = false;
          }
          else
          {
            Object localObject2 = ((DocumentSelectActivity.ListItem)localObject1).file;
            if ((localObject2 != null) && (!((File)localObject2).isDirectory()))
            {
              if (!((File)localObject2).canRead())
              {
                DocumentSelectActivity.this.showErrorBox(LocaleController.getString("AccessError", NUM));
                bool = false;
              }
              else if ((DocumentSelectActivity.this.sizeLimit != 0L) && (((File)localObject2).length() > DocumentSelectActivity.this.sizeLimit))
              {
                DocumentSelectActivity.this.showErrorBox(LocaleController.formatString("FileUploadLimit", NUM, new Object[] { AndroidUtilities.formatFileSize(DocumentSelectActivity.this.sizeLimit) }));
                bool = false;
              }
              else if (((File)localObject2).length() == 0L)
              {
                bool = false;
              }
              else
              {
                DocumentSelectActivity.this.selectedFiles.put(((File)localObject2).toString(), localObject1);
                DocumentSelectActivity.this.selectedMessagesCountTextView.setNumber(1, false);
                localObject2 = new AnimatorSet();
                localObject1 = new ArrayList();
                for (paramAnonymousInt = 0; paramAnonymousInt < DocumentSelectActivity.this.actionModeViews.size(); paramAnonymousInt++)
                {
                  View localView = (View)DocumentSelectActivity.this.actionModeViews.get(paramAnonymousInt);
                  AndroidUtilities.clearDrawableAnimation(localView);
                  ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(localView, "scaleY", new float[] { 0.1F, 1.0F }));
                }
                ((AnimatorSet)localObject2).playTogether((Collection)localObject1);
                ((AnimatorSet)localObject2).setDuration(250L);
                ((AnimatorSet)localObject2).start();
                DocumentSelectActivity.access$802(DocumentSelectActivity.this, false);
                if ((paramAnonymousView instanceof SharedDocumentCell)) {
                  ((SharedDocumentCell)paramAnonymousView).setChecked(true, true);
                }
                DocumentSelectActivity.this.actionBar.showActionMode();
              }
            }
            else {
              bool = true;
            }
          }
        }
      }
    });
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        DocumentSelectActivity.ListItem localListItem = DocumentSelectActivity.this.listAdapter.getItem(paramAnonymousInt);
        if (localListItem == null) {}
        for (;;)
        {
          return;
          File localFile = localListItem.file;
          if (localFile == null)
          {
            if (localListItem.icon == NUM)
            {
              if (DocumentSelectActivity.this.delegate != null) {
                DocumentSelectActivity.this.delegate.startDocumentSelectActivity();
              }
              DocumentSelectActivity.this.finishFragment(false);
            }
            else
            {
              paramAnonymousView = (DocumentSelectActivity.HistoryEntry)DocumentSelectActivity.this.history.remove(DocumentSelectActivity.this.history.size() - 1);
              DocumentSelectActivity.this.actionBar.setTitle(paramAnonymousView.title);
              if (paramAnonymousView.dir != null) {
                DocumentSelectActivity.this.listFiles(paramAnonymousView.dir);
              }
              for (;;)
              {
                DocumentSelectActivity.this.layoutManager.scrollToPositionWithOffset(paramAnonymousView.scrollItem, paramAnonymousView.scrollOffset);
                break;
                DocumentSelectActivity.this.listRoots();
              }
            }
          }
          else
          {
            Object localObject;
            if (localFile.isDirectory())
            {
              paramAnonymousView = new DocumentSelectActivity.HistoryEntry(DocumentSelectActivity.this, null);
              paramAnonymousView.scrollItem = DocumentSelectActivity.this.layoutManager.findLastVisibleItemPosition();
              localObject = DocumentSelectActivity.this.layoutManager.findViewByPosition(paramAnonymousView.scrollItem);
              if (localObject != null) {
                paramAnonymousView.scrollOffset = ((View)localObject).getTop();
              }
              paramAnonymousView.dir = DocumentSelectActivity.this.currentDir;
              paramAnonymousView.title = DocumentSelectActivity.this.actionBar.getTitle();
              DocumentSelectActivity.this.history.add(paramAnonymousView);
              if (!DocumentSelectActivity.this.listFiles(localFile)) {
                DocumentSelectActivity.this.history.remove(paramAnonymousView);
              } else {
                DocumentSelectActivity.this.actionBar.setTitle(localListItem.title);
              }
            }
            else
            {
              localObject = localFile;
              if (!localFile.canRead())
              {
                DocumentSelectActivity.this.showErrorBox(LocaleController.getString("AccessError", NUM));
                localObject = new File("/mnt/sdcard");
              }
              if ((DocumentSelectActivity.this.sizeLimit != 0L) && (((File)localObject).length() > DocumentSelectActivity.this.sizeLimit))
              {
                DocumentSelectActivity.this.showErrorBox(LocaleController.formatString("FileUploadLimit", NUM, new Object[] { AndroidUtilities.formatFileSize(DocumentSelectActivity.this.sizeLimit) }));
              }
              else if (((File)localObject).length() != 0L)
              {
                if (DocumentSelectActivity.this.actionBar.isActionModeShowed())
                {
                  if (DocumentSelectActivity.this.selectedFiles.containsKey(((File)localObject).toString()))
                  {
                    DocumentSelectActivity.this.selectedFiles.remove(((File)localObject).toString());
                    label462:
                    if (!DocumentSelectActivity.this.selectedFiles.isEmpty()) {
                      break label549;
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
                    break;
                    DocumentSelectActivity.this.selectedFiles.put(((File)localObject).toString(), localListItem);
                    break label462;
                    label549:
                    DocumentSelectActivity.this.selectedMessagesCountTextView.setNumber(DocumentSelectActivity.this.selectedFiles.size(), true);
                  }
                }
                if (DocumentSelectActivity.this.delegate != null)
                {
                  paramAnonymousView = new ArrayList();
                  paramAnonymousView.add(((File)localObject).getAbsolutePath());
                  DocumentSelectActivity.this.delegate.didSelectFiles(DocumentSelectActivity.this, paramAnonymousView);
                }
              }
            }
          }
        }
      }
    });
    listRoots();
    return this.fragmentView;
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, null, null, null, null, "actionBarActionModeDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, null, null, null, null, "actionBarActionModeDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, null, null, null, null, "actionBarActionModeDefaultTop"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, null, null, null, null, "actionBarActionModeDefaultSelector"), new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "actionBarActionModeDefaultIcon"), new ThemeDescription(this.listView, 0, new Class[] { GraySectionCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { GraySectionCell.class }, null, null, null, "graySection"), new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { SharedDocumentCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { SharedDocumentCell.class }, new String[] { "dateTextView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[] { SharedDocumentCell.class }, new String[] { "checkBox" }, null, null, null, "checkbox"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { SharedDocumentCell.class }, new String[] { "checkBox" }, null, null, null, "checkboxCheck"), new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[] { SharedDocumentCell.class }, new String[] { "thumbImageView" }, null, null, null, "files_folderIcon"), new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { SharedDocumentCell.class }, new String[] { "thumbImageView" }, null, null, null, "files_folderIconBackground"), new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { SharedDocumentCell.class }, new String[] { "extTextView" }, null, null, null, "files_iconText") };
  }
  
  public void loadRecentFiles()
  {
    Object localObject1;
    try
    {
      File[] arrayOfFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
      int i = 0;
      if (i < arrayOfFile.length)
      {
        localObject1 = arrayOfFile[i];
        if (((File)localObject1).isDirectory()) {}
        for (;;)
        {
          i++;
          break;
          ListItem localListItem = new org/telegram/ui/DocumentSelectActivity$ListItem;
          localListItem.<init>(this, null);
          localListItem.title = ((File)localObject1).getName();
          localListItem.file = ((File)localObject1);
          String str = ((File)localObject1).getName();
          Object localObject2 = str.split("\\.");
          if (localObject2.length <= 1) {
            break label195;
          }
          localObject2 = localObject2[(localObject2.length - 1)];
          localListItem.ext = ((String)localObject2);
          localListItem.subtitle = AndroidUtilities.formatFileSize(((File)localObject1).length());
          localObject2 = str.toLowerCase();
          if ((((String)localObject2).endsWith(".jpg")) || (((String)localObject2).endsWith(".png")) || (((String)localObject2).endsWith(".gif")) || (((String)localObject2).endsWith(".jpeg"))) {
            localListItem.thumb = ((File)localObject1).getAbsolutePath();
          }
          this.recentItems.add(localListItem);
        }
        return;
      }
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
    for (;;)
    {
      label195:
      Object localObject3 = "?";
      break;
      localObject1 = this.recentItems;
      localObject3 = new org/telegram/ui/DocumentSelectActivity$7;
      ((7)localObject3).<init>(this);
      Collections.sort((List)localObject1, (Comparator)localObject3);
    }
  }
  
  public boolean onBackPressed()
  {
    if (this.history.size() > 0)
    {
      HistoryEntry localHistoryEntry = (HistoryEntry)this.history.remove(this.history.size() - 1);
      this.actionBar.setTitle(localHistoryEntry.title);
      if (localHistoryEntry.dir != null)
      {
        listFiles(localHistoryEntry.dir);
        this.layoutManager.scrollToPositionWithOffset(localHistoryEntry.scrollItem, localHistoryEntry.scrollOffset);
      }
    }
    for (boolean bool = false;; bool = super.onBackPressed())
    {
      return bool;
      listRoots();
      break;
    }
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
  
  public boolean onFragmentCreate()
  {
    loadRecentFiles();
    return super.onFragmentCreate();
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
        FileLog.e(localException);
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
    extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public DocumentSelectActivity.ListItem getItem(int paramInt)
    {
      DocumentSelectActivity.ListItem localListItem;
      if (paramInt < DocumentSelectActivity.this.items.size()) {
        localListItem = (DocumentSelectActivity.ListItem)DocumentSelectActivity.this.items.get(paramInt);
      }
      for (;;)
      {
        return localListItem;
        if ((DocumentSelectActivity.this.history.isEmpty()) && (!DocumentSelectActivity.this.recentItems.isEmpty()) && (paramInt != DocumentSelectActivity.this.items.size()))
        {
          paramInt -= DocumentSelectActivity.this.items.size() + 1;
          if (paramInt < DocumentSelectActivity.this.recentItems.size())
          {
            localListItem = (DocumentSelectActivity.ListItem)DocumentSelectActivity.this.recentItems.get(paramInt);
            continue;
          }
        }
        localListItem = null;
      }
    }
    
    public int getItemCount()
    {
      int i = DocumentSelectActivity.this.items.size();
      int j = i;
      if (DocumentSelectActivity.this.history.isEmpty())
      {
        j = i;
        if (!DocumentSelectActivity.this.recentItems.isEmpty()) {
          j = i + (DocumentSelectActivity.this.recentItems.size() + 1);
        }
      }
      return j;
    }
    
    public int getItemViewType(int paramInt)
    {
      if (getItem(paramInt) != null) {}
      for (paramInt = 1;; paramInt = 0) {
        return paramInt;
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      if (paramViewHolder.getItemViewType() != 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool1 = true;
      DocumentSelectActivity.ListItem localListItem;
      SharedDocumentCell localSharedDocumentCell;
      boolean bool2;
      if (paramViewHolder.getItemViewType() == 1)
      {
        localListItem = getItem(paramInt);
        localSharedDocumentCell = (SharedDocumentCell)paramViewHolder.itemView;
        if (localListItem.icon == 0) {
          break label118;
        }
        localSharedDocumentCell.setTextAndValueAndTypeAndThumb(localListItem.title, localListItem.subtitle, null, null, localListItem.icon);
        if ((localListItem.file == null) || (!DocumentSelectActivity.this.actionBar.isActionModeShowed())) {
          break label173;
        }
        bool2 = DocumentSelectActivity.this.selectedFiles.containsKey(localListItem.file.toString());
        if (DocumentSelectActivity.this.scrolling) {
          break label168;
        }
      }
      label118:
      label168:
      for (bool1 = true;; bool1 = false)
      {
        localSharedDocumentCell.setChecked(bool2, bool1);
        return;
        paramViewHolder = localListItem.ext.toUpperCase().substring(0, Math.min(localListItem.ext.length(), 4));
        localSharedDocumentCell.setTextAndValueAndTypeAndThumb(localListItem.title, localListItem.subtitle, paramViewHolder, localListItem.thumb, 0);
        break;
      }
      label173:
      if (!DocumentSelectActivity.this.scrolling) {}
      for (;;)
      {
        localSharedDocumentCell.setChecked(false, bool1);
        break;
        bool1 = false;
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        paramViewGroup = new SharedDocumentCell(this.mContext);
      }
      for (;;)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new GraySectionCell(this.mContext);
        ((GraySectionCell)paramViewGroup).setText(LocaleController.getString("Recent", NUM).toUpperCase());
      }
    }
  }
  
  private class ListItem
  {
    long date;
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