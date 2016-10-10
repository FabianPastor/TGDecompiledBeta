package org.telegram.ui;

import android.app.AlarmManager;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import java.io.File;
import java.util.ArrayList;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ClearCacheService;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.query.BotQuery;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;

public class CacheControlActivity
  extends BaseFragment
{
  private long audioSize = -1L;
  private int cacheInfoRow;
  private int cacheRow;
  private long cacheSize = -1L;
  private boolean calculating = true;
  private volatile boolean canceled = false;
  private boolean[] clear = new boolean[6];
  private int databaseInfoRow;
  private int databaseRow;
  private long databaseSize = -1L;
  private long documentsSize = -1L;
  private int keepMediaInfoRow;
  private int keepMediaRow;
  private ListAdapter listAdapter;
  private long musicSize = -1L;
  private long photoSize = -1L;
  private int rowCount;
  private long totalSize = -1L;
  private long videoSize = -1L;
  
  private void cleanupFolders()
  {
    final ProgressDialog localProgressDialog = new ProgressDialog(getParentActivity());
    localProgressDialog.setMessage(LocaleController.getString("Loading", 2131165834));
    localProgressDialog.setCanceledOnTouchOutside(false);
    localProgressDialog.setCancelable(false);
    localProgressDialog.show();
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        final boolean bool2 = false;
        int k = 0;
        boolean bool1;
        int i;
        int m;
        int j;
        Object localObject;
        if (k < 6)
        {
          if (CacheControlActivity.this.clear[k] == 0) {
            bool1 = bool2;
          }
          do
          {
            k += 1;
            bool2 = bool1;
            break;
            i = -1;
            m = 0;
            if (k != 0) {
              break label480;
            }
            i = 0;
            j = m;
            bool1 = bool2;
          } while (i == -1);
          localObject = FileLoader.getInstance().checkDirectory(i);
          if (localObject == null) {}
        }
        for (;;)
        {
          try
          {
            localObject = ((File)localObject).listFiles();
            if (localObject != null)
            {
              m = 0;
              label90:
              if (m < localObject.length)
              {
                String str = localObject[m].getName().toLowerCase();
                if ((j == 1) || (j == 2))
                {
                  if (str.endsWith(".mp3")) {
                    break label466;
                  }
                  if (!str.endsWith(".m4a")) {
                    break label546;
                  }
                  break label466;
                }
                if ((str.equals(".nomedia")) || (!localObject[m].isFile())) {
                  break label471;
                }
                localObject[m].delete();
              }
            }
          }
          catch (Throwable localThrowable)
          {
            FileLog.e("tmessages", localThrowable);
          }
          if (i == 4)
          {
            CacheControlActivity.access$002(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(4), j));
            bool1 = true;
            break;
          }
          if (i == 1)
          {
            CacheControlActivity.access$702(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(1), j));
            bool1 = bool2;
            break;
          }
          if (i == 3)
          {
            if (j == 1)
            {
              CacheControlActivity.access$502(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(3), j));
              bool1 = bool2;
              break;
            }
            CacheControlActivity.access$602(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(3), j));
            bool1 = bool2;
            break;
          }
          if (i == 0)
          {
            bool1 = true;
            CacheControlActivity.access$302(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(0), j));
            break;
          }
          bool1 = bool2;
          if (i != 2) {
            break;
          }
          CacheControlActivity.access$402(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(2), j));
          bool1 = bool2;
          break;
          CacheControlActivity.access$802(CacheControlActivity.this, CacheControlActivity.this.cacheSize + CacheControlActivity.this.videoSize + CacheControlActivity.this.audioSize + CacheControlActivity.this.photoSize + CacheControlActivity.this.documentsSize + CacheControlActivity.this.musicSize);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (bool2) {
                ImageLoader.getInstance().clearMemory();
              }
              if (CacheControlActivity.this.listAdapter != null) {
                CacheControlActivity.this.listAdapter.notifyDataSetChanged();
              }
              try
              {
                CacheControlActivity.2.this.val$progressDialog.dismiss();
                return;
              }
              catch (Exception localException)
              {
                FileLog.e("tmessages", localException);
              }
            }
          });
          return;
          label466:
          if (j == 1) {
            label471:
            label480:
            label546:
            do
            {
              m += 1;
              break label90;
              if (k == 1)
              {
                i = 2;
                j = m;
                break;
              }
              if (k == 2)
              {
                i = 3;
                j = 1;
                break;
              }
              if (k == 3)
              {
                i = 3;
                j = 2;
                break;
              }
              if (k == 4)
              {
                i = 1;
                j = m;
                break;
              }
              j = m;
              if (k != 5) {
                break;
              }
              i = 4;
              j = m;
              break;
            } while (j == 2);
          }
        }
      }
    });
  }
  
  private long getDirectorySize(File paramFile, int paramInt)
  {
    long l2;
    if ((paramFile == null) || (this.canceled)) {
      l2 = 0L;
    }
    long l1;
    int i;
    label73:
    File localFile;
    for (;;)
    {
      return l2;
      l1 = 0L;
      if (!paramFile.isDirectory()) {
        break;
      }
      long l3 = l1;
      try
      {
        paramFile = paramFile.listFiles();
        l2 = l1;
        if (paramFile != null)
        {
          i = 0;
          l2 = l1;
          l3 = l1;
          if (i < paramFile.length)
          {
            l3 = l1;
            if (!this.canceled) {
              break label198;
            }
            return 0L;
          }
        }
      }
      catch (Throwable paramFile)
      {
        String str;
        FileLog.e("tmessages", paramFile);
        return l3;
      }
    }
    l3 = l1;
    str = localFile.getName().toLowerCase();
    l3 = l1;
    if (!str.endsWith(".mp3"))
    {
      l3 = l1;
      if (!str.endsWith(".m4a")) {
        break label236;
      }
    }
    for (;;)
    {
      l3 = l1;
      if (localFile.isDirectory())
      {
        l3 = l1;
        l2 = l1 + getDirectorySize(localFile, paramInt);
      }
      else
      {
        l3 = l1;
        l2 = localFile.length();
        l2 = l1 + l2;
        break label225;
        l2 = l1;
        if (!paramFile.isFile()) {
          break;
        }
        return 0L + paramFile.length();
        label198:
        localFile = paramFile[i];
        if (paramInt == 1) {
          break label73;
        }
        if (paramInt != 2) {
          continue;
        }
        break label73;
        if (paramInt != 1) {
          continue;
        }
        l2 = l1;
      }
      label225:
      label236:
      do
      {
        i += 1;
        l1 = l2;
        break;
        l2 = l1;
      } while (paramInt == 2);
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("CacheSettings", 2131165381));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          CacheControlActivity.this.finishFragment();
        }
      }
    });
    this.listAdapter = new ListAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    localFrameLayout.setBackgroundColor(-986896);
    paramContext = new ListView(paramContext);
    paramContext.setDivider(null);
    paramContext.setDividerHeight(0);
    paramContext.setVerticalScrollBarEnabled(false);
    paramContext.setDrawSelectorOnTop(true);
    localFrameLayout.addView(paramContext, LayoutHelper.createFrame(-1, -1.0F));
    paramContext.setAdapter(this.listAdapter);
    paramContext.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        if (CacheControlActivity.this.getParentActivity() == null) {}
        Object localObject2;
        do
        {
          return;
          if (paramAnonymousInt == CacheControlActivity.this.keepMediaRow)
          {
            paramAnonymousAdapterView = new BottomSheet.Builder(CacheControlActivity.this.getParentActivity());
            paramAnonymousView = LocaleController.formatPluralString("Weeks", 1);
            localObject1 = LocaleController.formatPluralString("Months", 1);
            localObject2 = LocaleController.getString("KeepMediaForever", 2131165780);
            DialogInterface.OnClickListener local1 = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("keep_media", paramAnonymous2Int).commit();
                if (CacheControlActivity.this.listAdapter != null) {
                  CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                }
                paramAnonymous2DialogInterface = PendingIntent.getService(ApplicationLoader.applicationContext, 0, new Intent(ApplicationLoader.applicationContext, ClearCacheService.class), 0);
                AlarmManager localAlarmManager = (AlarmManager)ApplicationLoader.applicationContext.getSystemService("alarm");
                if (paramAnonymous2Int == 2)
                {
                  localAlarmManager.cancel(paramAnonymous2DialogInterface);
                  return;
                }
                localAlarmManager.setInexactRepeating(2, 86400000L, 86400000L, paramAnonymous2DialogInterface);
              }
            };
            paramAnonymousAdapterView.setItems(new CharSequence[] { paramAnonymousView, localObject1, localObject2 }, local1);
            CacheControlActivity.this.showDialog(paramAnonymousAdapterView.create());
            return;
          }
          if (paramAnonymousInt == CacheControlActivity.this.databaseRow)
          {
            paramAnonymousAdapterView = new AlertDialog.Builder(CacheControlActivity.this.getParentActivity());
            paramAnonymousAdapterView.setTitle(LocaleController.getString("AppName", 2131165299));
            paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
            paramAnonymousAdapterView.setMessage(LocaleController.getString("LocalDatabaseClear", 2131165838));
            paramAnonymousAdapterView.setPositiveButton(LocaleController.getString("CacheClear", 2131165379), new DialogInterface.OnClickListener()
            {
              public void onClick(final DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                paramAnonymous2DialogInterface = new ProgressDialog(CacheControlActivity.this.getParentActivity());
                paramAnonymous2DialogInterface.setMessage(LocaleController.getString("Loading", 2131165834));
                paramAnonymous2DialogInterface.setCanceledOnTouchOutside(false);
                paramAnonymous2DialogInterface.setCancelable(false);
                paramAnonymous2DialogInterface.show();
                MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
                {
                  public void run()
                  {
                    for (;;)
                    {
                      int j;
                      SQLitePreparedStatement localSQLitePreparedStatement;
                      try
                      {
                        SQLiteDatabase localSQLiteDatabase = MessagesStorage.getInstance().getDatabase();
                        ArrayList localArrayList = new ArrayList();
                        Object localObject2 = localSQLiteDatabase.queryFinalized("SELECT did FROM dialogs WHERE 1", new Object[0]);
                        new StringBuilder();
                        long l1;
                        int i;
                        if (((SQLiteCursor)localObject2).next())
                        {
                          l1 = ((SQLiteCursor)localObject2).longValue(0);
                          i = (int)l1;
                          j = (int)(l1 >> 32);
                          if ((i == 0) || (j == 1)) {
                            continue;
                          }
                          localArrayList.add(Long.valueOf(l1));
                          continue;
                        }
                        Long localLong;
                        SQLiteCursor localSQLiteCursor1;
                        long l2;
                        SQLiteCursor localSQLiteCursor2;
                        int k;
                        NativeByteBuffer localNativeByteBuffer;
                        TLRPC.Message localMessage;
                        ((SQLitePreparedStatement)localObject2).dispose();
                      }
                      catch (Exception localException1)
                      {
                        FileLog.e("tmessages", localException1);
                        return;
                        ((SQLiteCursor)localObject2).dispose();
                        localObject2 = localException1.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
                        localSQLitePreparedStatement = localException1.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                        localException1.beginTransaction();
                        j = 0;
                        if (j < localArrayList.size())
                        {
                          localLong = (Long)localArrayList.get(j);
                          i = 0;
                          localSQLiteCursor1 = localException1.queryFinalized("SELECT COUNT(mid) FROM messages WHERE uid = " + localLong, new Object[0]);
                          if (localSQLiteCursor1.next()) {
                            i = localSQLiteCursor1.intValue(0);
                          }
                          localSQLiteCursor1.dispose();
                          if (i <= 2) {
                            break label704;
                          }
                          localSQLiteCursor1 = localException1.queryFinalized("SELECT last_mid_i, last_mid FROM dialogs WHERE did = " + localLong, new Object[0]);
                          i = -1;
                          if (localSQLiteCursor1.next())
                          {
                            l1 = localSQLiteCursor1.longValue(0);
                            l2 = localSQLiteCursor1.longValue(1);
                            localSQLiteCursor2 = localException1.queryFinalized("SELECT data FROM messages WHERE uid = " + localLong + " AND mid IN (" + l1 + "," + l2 + ")", new Object[0]);
                            k = i;
                            try
                            {
                              if (localSQLiteCursor2.next())
                              {
                                localNativeByteBuffer = localSQLiteCursor2.byteBufferValue(0);
                                i = k;
                                if (localNativeByteBuffer == null) {
                                  continue;
                                }
                                localMessage = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                                localNativeByteBuffer.reuse();
                                i = k;
                                if (localMessage == null) {
                                  continue;
                                }
                                i = localMessage.id;
                              }
                            }
                            catch (Exception localException2)
                            {
                              FileLog.e("tmessages", localException2);
                              localSQLiteCursor2.dispose();
                              localException1.executeFast("DELETE FROM messages WHERE uid = " + localLong + " AND mid != " + l1 + " AND mid != " + l2).stepThis().dispose();
                              localException1.executeFast("DELETE FROM messages_holes WHERE uid = " + localLong).stepThis().dispose();
                              localException1.executeFast("DELETE FROM bot_keyboard WHERE uid = " + localLong).stepThis().dispose();
                              localException1.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + localLong).stepThis().dispose();
                              localException1.executeFast("DELETE FROM media_v2 WHERE uid = " + localLong).stepThis().dispose();
                              localException1.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + localLong).stepThis().dispose();
                              BotQuery.clearBotKeyboard(localLong.longValue(), null);
                              if (k != -1) {
                                MessagesStorage.createFirstHoles(localLong.longValue(), (SQLitePreparedStatement)localObject2, localSQLitePreparedStatement, k);
                              }
                            }
                          }
                          localSQLiteCursor1.dispose();
                        }
                      }
                      finally
                      {
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            try
                            {
                              CacheControlActivity.4.2.1.this.val$progressDialog.dismiss();
                              if (CacheControlActivity.this.listAdapter != null)
                              {
                                File localFile = new File(ApplicationLoader.getFilesDirFixed(), "cache4.db");
                                CacheControlActivity.access$1402(CacheControlActivity.this, localFile.length());
                                CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                              }
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
                      }
                      localSQLitePreparedStatement.dispose();
                      ((SQLiteDatabase)localObject1).commitTransaction();
                      ((SQLiteDatabase)localObject1).executeFast("VACUUM").stepThis().dispose();
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          try
                          {
                            CacheControlActivity.4.2.1.this.val$progressDialog.dismiss();
                            if (CacheControlActivity.this.listAdapter != null)
                            {
                              File localFile = new File(ApplicationLoader.getFilesDirFixed(), "cache4.db");
                              CacheControlActivity.access$1402(CacheControlActivity.this, localFile.length());
                              CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                            }
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
                      label704:
                      j += 1;
                    }
                  }
                });
              }
            });
            CacheControlActivity.this.showDialog(paramAnonymousAdapterView.create());
            return;
          }
        } while ((paramAnonymousInt != CacheControlActivity.this.cacheRow) || (CacheControlActivity.this.totalSize <= 0L) || (CacheControlActivity.this.getParentActivity() == null));
        paramAnonymousView = new BottomSheet.Builder(CacheControlActivity.this.getParentActivity());
        paramAnonymousView.setApplyTopPadding(false);
        paramAnonymousView.setApplyBottomPadding(false);
        Object localObject1 = new LinearLayout(CacheControlActivity.this.getParentActivity());
        ((LinearLayout)localObject1).setOrientation(1);
        paramAnonymousInt = 0;
        if (paramAnonymousInt < 6)
        {
          paramAnonymousLong = 0L;
          paramAnonymousAdapterView = null;
          if (paramAnonymousInt == 0)
          {
            paramAnonymousLong = CacheControlActivity.this.photoSize;
            paramAnonymousAdapterView = LocaleController.getString("LocalPhotoCache", 2131165842);
            label321:
            if (paramAnonymousLong <= 0L) {
              break label541;
            }
            CacheControlActivity.this.clear[paramAnonymousInt] = 1;
            localObject2 = new CheckBoxCell(CacheControlActivity.this.getParentActivity());
            ((CheckBoxCell)localObject2).setTag(Integer.valueOf(paramAnonymousInt));
            ((CheckBoxCell)localObject2).setBackgroundResource(2130837796);
            ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(-1, 48));
            ((CheckBoxCell)localObject2).setText(paramAnonymousAdapterView, AndroidUtilities.formatFileSize(paramAnonymousLong), true, true);
            ((CheckBoxCell)localObject2).setOnClickListener(new View.OnClickListener()
            {
              public void onClick(View paramAnonymous2View)
              {
                paramAnonymous2View = (CheckBoxCell)paramAnonymous2View;
                int i = ((Integer)paramAnonymous2View.getTag()).intValue();
                boolean[] arrayOfBoolean = CacheControlActivity.this.clear;
                if (CacheControlActivity.this.clear[i] == 0) {}
                for (int j = 1;; j = 0)
                {
                  arrayOfBoolean[i] = j;
                  paramAnonymous2View.setChecked(CacheControlActivity.this.clear[i], true);
                  return;
                }
              }
            });
          }
          for (;;)
          {
            paramAnonymousInt += 1;
            break;
            if (paramAnonymousInt == 1)
            {
              paramAnonymousLong = CacheControlActivity.this.videoSize;
              paramAnonymousAdapterView = LocaleController.getString("LocalVideoCache", 2131165843);
              break label321;
            }
            if (paramAnonymousInt == 2)
            {
              paramAnonymousLong = CacheControlActivity.this.documentsSize;
              paramAnonymousAdapterView = LocaleController.getString("LocalDocumentCache", 2131165840);
              break label321;
            }
            if (paramAnonymousInt == 3)
            {
              paramAnonymousLong = CacheControlActivity.this.musicSize;
              paramAnonymousAdapterView = LocaleController.getString("LocalMusicCache", 2131165841);
              break label321;
            }
            if (paramAnonymousInt == 4)
            {
              paramAnonymousLong = CacheControlActivity.this.audioSize;
              paramAnonymousAdapterView = LocaleController.getString("LocalAudioCache", 2131165835);
              break label321;
            }
            if (paramAnonymousInt != 5) {
              break label321;
            }
            paramAnonymousLong = CacheControlActivity.this.cacheSize;
            paramAnonymousAdapterView = LocaleController.getString("LocalCache", 2131165836);
            break label321;
            label541:
            CacheControlActivity.this.clear[paramAnonymousInt] = 0;
          }
        }
        paramAnonymousAdapterView = new BottomSheet.BottomSheetCell(CacheControlActivity.this.getParentActivity(), 1);
        paramAnonymousAdapterView.setBackgroundResource(2130837796);
        paramAnonymousAdapterView.setTextAndIcon(LocaleController.getString("ClearMediaCache", 2131165511).toUpperCase(), 0);
        paramAnonymousAdapterView.setTextColor(-3319206);
        paramAnonymousAdapterView.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymous2View)
          {
            try
            {
              if (CacheControlActivity.this.visibleDialog != null) {
                CacheControlActivity.this.visibleDialog.dismiss();
              }
              CacheControlActivity.this.cleanupFolders();
              return;
            }
            catch (Exception paramAnonymous2View)
            {
              for (;;)
              {
                FileLog.e("tmessages", paramAnonymous2View);
              }
            }
          }
        });
        ((LinearLayout)localObject1).addView(paramAnonymousAdapterView, LayoutHelper.createLinear(-1, 48));
        paramAnonymousView.setCustomView((View)localObject1);
        CacheControlActivity.this.showDialog(paramAnonymousView.create());
      }
    });
    return this.fragmentView;
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.keepMediaRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.keepMediaInfoRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.cacheRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.cacheInfoRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.databaseRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.databaseInfoRow = i;
    this.databaseSize = new File(ApplicationLoader.getFilesDirFixed(), "cache4.db").length();
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        CacheControlActivity.access$002(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(4), 0));
        if (CacheControlActivity.this.canceled) {}
        do
        {
          do
          {
            do
            {
              do
              {
                return;
                CacheControlActivity.access$302(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(0), 0));
              } while (CacheControlActivity.this.canceled);
              CacheControlActivity.access$402(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(2), 0));
            } while (CacheControlActivity.this.canceled);
            CacheControlActivity.access$502(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(3), 1));
          } while (CacheControlActivity.this.canceled);
          CacheControlActivity.access$602(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(3), 2));
        } while (CacheControlActivity.this.canceled);
        CacheControlActivity.access$702(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(1), 0));
        CacheControlActivity.access$802(CacheControlActivity.this, CacheControlActivity.this.cacheSize + CacheControlActivity.this.videoSize + CacheControlActivity.this.audioSize + CacheControlActivity.this.photoSize + CacheControlActivity.this.documentsSize + CacheControlActivity.this.musicSize);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            CacheControlActivity.access$902(CacheControlActivity.this, false);
            if (CacheControlActivity.this.listAdapter != null) {
              CacheControlActivity.this.listAdapter.notifyDataSetChanged();
            }
          }
        });
      }
    });
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    this.canceled = true;
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
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
      return CacheControlActivity.this.rowCount;
    }
    
    public Object getItem(int paramInt)
    {
      return null;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((paramInt == CacheControlActivity.this.databaseRow) || (paramInt == CacheControlActivity.this.cacheRow) || (paramInt == CacheControlActivity.this.keepMediaRow)) {}
      while ((paramInt != CacheControlActivity.this.databaseInfoRow) && (paramInt != CacheControlActivity.this.cacheInfoRow) && (paramInt != CacheControlActivity.this.keepMediaInfoRow)) {
        return 0;
      }
      return 1;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = getItemViewType(paramInt);
      TextSettingsCell localTextSettingsCell;
      Object localObject;
      if (i == 0)
      {
        paramViewGroup = paramView;
        if (paramView == null)
        {
          paramViewGroup = new TextSettingsCell(this.mContext);
          paramViewGroup.setBackgroundColor(-1);
        }
        localTextSettingsCell = (TextSettingsCell)paramViewGroup;
        if (paramInt == CacheControlActivity.this.databaseRow)
        {
          localTextSettingsCell.setTextAndValue(LocaleController.getString("LocalDatabase", 2131165837), AndroidUtilities.formatFileSize(CacheControlActivity.this.databaseSize), false);
          localObject = paramViewGroup;
        }
      }
      do
      {
        do
        {
          do
          {
            return (View)localObject;
            if (paramInt == CacheControlActivity.this.cacheRow)
            {
              if (CacheControlActivity.this.calculating)
              {
                localTextSettingsCell.setTextAndValue(LocaleController.getString("ClearMediaCache", 2131165511), LocaleController.getString("CalculatingSize", 2131165382), false);
                return paramViewGroup;
              }
              localObject = LocaleController.getString("ClearMediaCache", 2131165511);
              if (CacheControlActivity.this.totalSize == 0L) {}
              for (paramView = LocaleController.getString("CacheEmpty", 2131165380);; paramView = AndroidUtilities.formatFileSize(CacheControlActivity.this.totalSize))
              {
                localTextSettingsCell.setTextAndValue((String)localObject, paramView, false);
                return paramViewGroup;
              }
            }
            localObject = paramViewGroup;
          } while (paramInt != CacheControlActivity.this.keepMediaRow);
          paramInt = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("keep_media", 2);
          if (paramInt == 0) {
            paramView = LocaleController.formatPluralString("Weeks", 1);
          }
          for (;;)
          {
            localTextSettingsCell.setTextAndValue(LocaleController.getString("KeepMedia", 2131165779), paramView, false);
            return paramViewGroup;
            if (paramInt == 1) {
              paramView = LocaleController.formatPluralString("Months", 1);
            } else {
              paramView = LocaleController.getString("KeepMediaForever", 2131165780);
            }
          }
          localObject = paramView;
        } while (i != 1);
        paramViewGroup = paramView;
        if (paramView == null) {
          paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        }
        if (paramInt == CacheControlActivity.this.databaseInfoRow)
        {
          ((TextInfoPrivacyCell)paramViewGroup).setText(LocaleController.getString("LocalDatabaseInfo", 2131165839));
          paramViewGroup.setBackgroundResource(2130837689);
          return paramViewGroup;
        }
        if (paramInt == CacheControlActivity.this.cacheInfoRow)
        {
          ((TextInfoPrivacyCell)paramViewGroup).setText("");
          paramViewGroup.setBackgroundResource(2130837688);
          return paramViewGroup;
        }
        localObject = paramViewGroup;
      } while (paramInt != CacheControlActivity.this.keepMediaInfoRow);
      ((TextInfoPrivacyCell)paramViewGroup).setText(AndroidUtilities.replaceTags(LocaleController.getString("KeepMediaInfo", 2131165781)));
      paramViewGroup.setBackgroundResource(2130837688);
      return paramViewGroup;
    }
    
    public int getViewTypeCount()
    {
      return 2;
    }
    
    public boolean hasStableIds()
    {
      return false;
    }
    
    public boolean isEmpty()
    {
      return false;
    }
    
    public boolean isEnabled(int paramInt)
    {
      return (paramInt == CacheControlActivity.this.databaseRow) || ((paramInt == CacheControlActivity.this.cacheRow) && (CacheControlActivity.this.totalSize > 0L)) || (paramInt == CacheControlActivity.this.keepMediaRow);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/CacheControlActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */