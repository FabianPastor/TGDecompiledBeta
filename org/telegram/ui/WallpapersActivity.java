package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_account_getWallPapers;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.TL_wallPaperSolid;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.tgnet.TLRPC.WallPaper;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.WallpaperCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

public class WallpapersActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private ImageView backgroundImage;
  private String currentPicturePath;
  private View doneButton;
  private ListAdapter listAdapter;
  private String loadingFile = null;
  private File loadingFileObject = null;
  private TLRPC.PhotoSize loadingSize = null;
  private FrameLayout progressView;
  private View progressViewBackground;
  private int selectedBackground;
  private int selectedColor;
  private ArrayList<TLRPC.WallPaper> wallPapers = new ArrayList();
  private HashMap<Integer, TLRPC.WallPaper> wallpappersByIds = new HashMap();
  
  private void loadWallpapers()
  {
    TLRPC.TL_account_getWallPapers localTL_account_getWallPapers = new TLRPC.TL_account_getWallPapers();
    int i = ConnectionsManager.getInstance().sendRequest(localTL_account_getWallPapers, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error != null) {
          return;
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            WallpapersActivity.this.wallPapers.clear();
            Object localObject1 = (TLRPC.Vector)paramAnonymousTLObject;
            WallpapersActivity.this.wallpappersByIds.clear();
            localObject1 = ((TLRPC.Vector)localObject1).objects.iterator();
            while (((Iterator)localObject1).hasNext())
            {
              Object localObject2 = ((Iterator)localObject1).next();
              WallpapersActivity.this.wallPapers.add((TLRPC.WallPaper)localObject2);
              WallpapersActivity.this.wallpappersByIds.put(Integer.valueOf(((TLRPC.WallPaper)localObject2).id), (TLRPC.WallPaper)localObject2);
            }
            if (WallpapersActivity.this.listAdapter != null) {
              WallpapersActivity.this.listAdapter.notifyDataSetChanged();
            }
            if (WallpapersActivity.this.backgroundImage != null) {
              WallpapersActivity.this.processSelectedBackground();
            }
            MessagesStorage.getInstance().putWallpapers(WallpapersActivity.this.wallPapers);
          }
        });
      }
    });
    ConnectionsManager.getInstance().bindRequestToGuid(i, this.classGuid);
  }
  
  private void processSelectedBackground()
  {
    Object localObject1 = (TLRPC.WallPaper)this.wallpappersByIds.get(Integer.valueOf(this.selectedBackground));
    Object localObject3;
    if ((this.selectedBackground != -1) && (this.selectedBackground != 1000001) && (localObject1 != null) && ((localObject1 instanceof TLRPC.TL_wallPaper)))
    {
      int i = AndroidUtilities.displaySize.x;
      int k = AndroidUtilities.displaySize.y;
      int m = k;
      int j = i;
      if (i > k)
      {
        j = k;
        m = i;
      }
      localObject1 = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.WallPaper)localObject1).sizes, Math.min(j, m));
      if (localObject1 == null) {
        return;
      }
      localObject3 = ((TLRPC.PhotoSize)localObject1).location.volume_id + "_" + ((TLRPC.PhotoSize)localObject1).location.local_id + ".jpg";
      File localFile = new File(FileLoader.getInstance().getDirectory(4), (String)localObject3);
      if (!localFile.exists())
      {
        int[] arrayOfInt = AndroidUtilities.calcDrawableColor(this.backgroundImage.getDrawable());
        this.progressViewBackground.getBackground().setColorFilter(new PorterDuffColorFilter(arrayOfInt[0], PorterDuff.Mode.MULTIPLY));
        this.loadingFile = ((String)localObject3);
        this.loadingFileObject = localFile;
        this.doneButton.setEnabled(false);
        this.progressView.setVisibility(0);
        this.loadingSize = ((TLRPC.PhotoSize)localObject1);
        this.selectedColor = 0;
        FileLoader.getInstance().loadFile((TLRPC.PhotoSize)localObject1, null, true);
        this.backgroundImage.setBackgroundColor(0);
        return;
      }
      if (this.loadingFile != null) {
        FileLoader.getInstance().cancelLoadFile(this.loadingSize);
      }
      this.loadingFileObject = null;
      this.loadingFile = null;
      this.loadingSize = null;
      try
      {
        this.backgroundImage.setImageURI(Uri.fromFile(localFile));
        this.backgroundImage.setBackgroundColor(0);
        this.selectedColor = 0;
        this.doneButton.setEnabled(true);
        this.progressView.setVisibility(8);
        return;
      }
      catch (Throwable localThrowable)
      {
        for (;;)
        {
          FileLog.e("tmessages", localThrowable);
        }
      }
    }
    if (this.loadingFile != null) {
      FileLoader.getInstance().cancelLoadFile(this.loadingSize);
    }
    if (this.selectedBackground == 1000001)
    {
      this.backgroundImage.setImageResource(2130837545);
      this.backgroundImage.setBackgroundColor(0);
      this.selectedColor = 0;
    }
    for (;;)
    {
      this.loadingFileObject = null;
      this.loadingFile = null;
      this.loadingSize = null;
      this.doneButton.setEnabled(true);
      this.progressView.setVisibility(8);
      return;
      Object localObject2;
      if (this.selectedBackground == -1)
      {
        localObject3 = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper-temp.jpg");
        localObject2 = localObject3;
        if (!((File)localObject3).exists()) {
          localObject2 = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg");
        }
        if (((File)localObject2).exists())
        {
          this.backgroundImage.setImageURI(Uri.fromFile((File)localObject2));
        }
        else
        {
          this.selectedBackground = 1000001;
          processSelectedBackground();
        }
      }
      else
      {
        if (localObject2 == null) {
          break;
        }
        if ((localObject2 instanceof TLRPC.TL_wallPaperSolid))
        {
          this.backgroundImage.getDrawable();
          this.backgroundImage.setImageBitmap(null);
          this.selectedColor = (0xFF000000 | ((TLRPC.WallPaper)localObject2).bg_color);
          this.backgroundImage.setBackgroundColor(this.selectedColor);
        }
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("ChatBackground", 2131165489));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          WallpapersActivity.this.finishFragment();
        }
        while (paramAnonymousInt != 1) {
          return;
        }
        Object localObject = (TLRPC.WallPaper)WallpapersActivity.this.wallpappersByIds.get(Integer.valueOf(WallpapersActivity.this.selectedBackground));
        File localFile;
        if ((localObject != null) && (((TLRPC.WallPaper)localObject).id != 1000001) && ((localObject instanceof TLRPC.TL_wallPaper)))
        {
          paramAnonymousInt = AndroidUtilities.displaySize.x;
          int j = AndroidUtilities.displaySize.y;
          int k = j;
          int i = paramAnonymousInt;
          if (paramAnonymousInt > j)
          {
            i = j;
            k = paramAnonymousInt;
          }
          localObject = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.WallPaper)localObject).sizes, Math.min(i, k));
          localObject = ((TLRPC.PhotoSize)localObject).location.volume_id + "_" + ((TLRPC.PhotoSize)localObject).location.local_id + ".jpg";
          localObject = new File(FileLoader.getInstance().getDirectory(4), (String)localObject);
          localFile = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg");
        }
        for (;;)
        {
          boolean bool;
          try
          {
            bool = AndroidUtilities.copyFile((File)localObject, localFile);
            if (bool)
            {
              localObject = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
              ((SharedPreferences.Editor)localObject).putInt("selectedBackground", WallpapersActivity.this.selectedBackground);
              ((SharedPreferences.Editor)localObject).putInt("selectedColor", WallpapersActivity.this.selectedColor);
              ((SharedPreferences.Editor)localObject).commit();
              ApplicationLoader.reloadWallpaper();
            }
            WallpapersActivity.this.finishFragment();
            return;
          }
          catch (Exception localException)
          {
            bool = false;
            FileLog.e("tmessages", localException);
            continue;
          }
          if (WallpapersActivity.this.selectedBackground == -1) {
            bool = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper-temp.jpg").renameTo(new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg"));
          } else {
            bool = true;
          }
        }
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837720, AndroidUtilities.dp(56.0F));
    FrameLayout localFrameLayout = new FrameLayout(paramContext);
    this.fragmentView = localFrameLayout;
    this.backgroundImage = new ImageView(paramContext);
    this.backgroundImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
    localFrameLayout.addView(this.backgroundImage, LayoutHelper.createFrame(-1, -1.0F));
    this.backgroundImage.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.progressView = new FrameLayout(paramContext);
    this.progressView.setVisibility(4);
    localFrameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, 52.0F));
    this.progressViewBackground = new View(paramContext);
    this.progressViewBackground.setBackgroundResource(2130838001);
    this.progressView.addView(this.progressViewBackground, LayoutHelper.createFrame(36, 36, 17));
    Object localObject = new ProgressBar(paramContext);
    try
    {
      ((ProgressBar)localObject).setIndeterminateDrawable(paramContext.getResources().getDrawable(2130837801));
      ((ProgressBar)localObject).setIndeterminate(true);
      AndroidUtilities.setProgressBarAnimationDuration((ProgressBar)localObject, 1500);
      this.progressView.addView((View)localObject, LayoutHelper.createFrame(32, 32, 17));
      localObject = new RecyclerListView(paramContext);
      ((RecyclerListView)localObject).setClipToPadding(false);
      ((RecyclerListView)localObject).setTag(Integer.valueOf(8));
      ((RecyclerListView)localObject).setPadding(AndroidUtilities.dp(40.0F), 0, AndroidUtilities.dp(40.0F), 0);
      LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(paramContext);
      localLinearLayoutManager.setOrientation(0);
      ((RecyclerListView)localObject).setLayoutManager(localLinearLayoutManager);
      ((RecyclerListView)localObject).setDisallowInterceptTouchEvents(true);
      ((RecyclerListView)localObject).setOverScrollMode(2);
      paramContext = new ListAdapter(paramContext);
      this.listAdapter = paramContext;
      ((RecyclerListView)localObject).setAdapter(paramContext);
      localFrameLayout.addView((View)localObject, LayoutHelper.createFrame(-1, 102, 83));
      ((RecyclerListView)localObject).setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if (paramAnonymousInt == 0) {
            if (WallpapersActivity.this.getParentActivity() != null) {}
          }
          while ((paramAnonymousInt - 1 < 0) || (paramAnonymousInt - 1 >= WallpapersActivity.this.wallPapers.size()))
          {
            return;
            paramAnonymousView = new AlertDialog.Builder(WallpapersActivity.this.getParentActivity());
            String str1 = LocaleController.getString("FromCamera", 2131165703);
            String str2 = LocaleController.getString("FromGalley", 2131165710);
            String str3 = LocaleController.getString("Cancel", 2131165386);
            DialogInterface.OnClickListener local1 = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                if (paramAnonymous2Int == 0) {
                  try
                  {
                    paramAnonymous2DialogInterface = new Intent("android.media.action.IMAGE_CAPTURE");
                    File localFile = AndroidUtilities.generatePicturePath();
                    if (localFile != null)
                    {
                      if (Build.VERSION.SDK_INT < 24) {
                        break label94;
                      }
                      paramAnonymous2DialogInterface.putExtra("output", FileProvider.getUriForFile(WallpapersActivity.this.getParentActivity(), "org.telegram.messenger.beta.provider", localFile));
                      paramAnonymous2DialogInterface.addFlags(2);
                      paramAnonymous2DialogInterface.addFlags(1);
                    }
                    for (;;)
                    {
                      WallpapersActivity.access$302(WallpapersActivity.this, localFile.getAbsolutePath());
                      WallpapersActivity.this.startActivityForResult(paramAnonymous2DialogInterface, 10);
                      return;
                      label94:
                      paramAnonymous2DialogInterface.putExtra("output", Uri.fromFile(localFile));
                    }
                    if (paramAnonymous2Int != 1) {
                      return;
                    }
                  }
                  catch (Exception paramAnonymous2DialogInterface)
                  {
                    FileLog.e("tmessages", paramAnonymous2DialogInterface);
                    return;
                  }
                }
                paramAnonymous2DialogInterface = new Intent("android.intent.action.PICK");
                paramAnonymous2DialogInterface.setType("image/*");
                WallpapersActivity.this.startActivityForResult(paramAnonymous2DialogInterface, 11);
              }
            };
            paramAnonymousView.setItems(new CharSequence[] { str1, str2, str3 }, local1);
            WallpapersActivity.this.showDialog(paramAnonymousView.create());
            return;
          }
          paramAnonymousView = (TLRPC.WallPaper)WallpapersActivity.this.wallPapers.get(paramAnonymousInt - 1);
          WallpapersActivity.access$002(WallpapersActivity.this, paramAnonymousView.id);
          WallpapersActivity.this.listAdapter.notifyDataSetChanged();
          WallpapersActivity.this.processSelectedBackground();
        }
      });
      processSelectedBackground();
      return this.fragmentView;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.FileDidFailedLoad)
    {
      paramVarArgs = (String)paramVarArgs[0];
      if ((this.loadingFile != null) && (this.loadingFile.equals(paramVarArgs)))
      {
        this.loadingFileObject = null;
        this.loadingFile = null;
        this.loadingSize = null;
        this.progressView.setVisibility(8);
        this.doneButton.setEnabled(false);
      }
    }
    do
    {
      do
      {
        return;
        if (paramInt != NotificationCenter.FileDidLoaded) {
          break;
        }
        paramVarArgs = (String)paramVarArgs[0];
      } while ((this.loadingFile == null) || (!this.loadingFile.equals(paramVarArgs)));
      this.backgroundImage.setImageURI(Uri.fromFile(this.loadingFileObject));
      this.progressView.setVisibility(8);
      this.backgroundImage.setBackgroundColor(0);
      this.doneButton.setEnabled(true);
      this.loadingFileObject = null;
      this.loadingFile = null;
      this.loadingSize = null;
      return;
    } while (paramInt != NotificationCenter.wallpapersDidLoaded);
    this.wallPapers = ((ArrayList)paramVarArgs[0]);
    this.wallpappersByIds.clear();
    paramVarArgs = this.wallPapers.iterator();
    while (paramVarArgs.hasNext())
    {
      TLRPC.WallPaper localWallPaper = (TLRPC.WallPaper)paramVarArgs.next();
      this.wallpappersByIds.put(Integer.valueOf(localWallPaper.id), localWallPaper);
    }
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
    if ((!this.wallPapers.isEmpty()) && (this.backgroundImage != null)) {
      processSelectedBackground();
    }
    loadWallpapers();
  }
  
  /* Error */
  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    // Byte code:
    //   0: iload_2
    //   1: iconst_m1
    //   2: if_icmpne +136 -> 138
    //   5: iload_1
    //   6: bipush 10
    //   8: if_icmpne +209 -> 217
    //   11: aload_0
    //   12: getfield 87	org/telegram/ui/WallpapersActivity:currentPicturePath	Ljava/lang/String;
    //   15: invokestatic 545	org/telegram/messenger/AndroidUtilities:addMediaToGallery	(Ljava/lang/String;)V
    //   18: aconst_null
    //   19: astore 4
    //   21: aconst_null
    //   22: astore 5
    //   24: aload 4
    //   26: astore_3
    //   27: invokestatic 549	org/telegram/messenger/AndroidUtilities:getRealScreenSize	()Landroid/graphics/Point;
    //   30: astore 6
    //   32: aload 4
    //   34: astore_3
    //   35: aload_0
    //   36: getfield 87	org/telegram/ui/WallpapersActivity:currentPicturePath	Ljava/lang/String;
    //   39: aconst_null
    //   40: aload 6
    //   42: getfield 153	android/graphics/Point:x	I
    //   45: i2f
    //   46: aload 6
    //   48: getfield 156	android/graphics/Point:y	I
    //   51: i2f
    //   52: iconst_1
    //   53: invokestatic 555	org/telegram/messenger/ImageLoader:loadBitmap	(Ljava/lang/String;Landroid/net/Uri;FFZ)Landroid/graphics/Bitmap;
    //   56: astore 6
    //   58: aload 4
    //   60: astore_3
    //   61: new 557	java/io/FileOutputStream
    //   64: dup
    //   65: new 209	java/io/File
    //   68: dup
    //   69: invokestatic 310	org/telegram/messenger/ApplicationLoader:getFilesDirFixed	()Ljava/io/File;
    //   72: ldc_w 312
    //   75: invokespecial 219	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   78: invokespecial 560	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   81: astore 4
    //   83: aload 6
    //   85: getstatic 566	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
    //   88: bipush 87
    //   90: aload 4
    //   92: invokevirtual 572	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   95: pop
    //   96: aload_0
    //   97: iconst_m1
    //   98: putfield 76	org/telegram/ui/WallpapersActivity:selectedBackground	I
    //   101: aload_0
    //   102: iconst_0
    //   103: putfield 83	org/telegram/ui/WallpapersActivity:selectedColor	I
    //   106: aload_0
    //   107: getfield 102	org/telegram/ui/WallpapersActivity:backgroundImage	Landroid/widget/ImageView;
    //   110: invokevirtual 229	android/widget/ImageView:getDrawable	()Landroid/graphics/drawable/Drawable;
    //   113: pop
    //   114: aload_0
    //   115: getfield 102	org/telegram/ui/WallpapersActivity:backgroundImage	Landroid/widget/ImageView;
    //   118: aload 6
    //   120: invokevirtual 320	android/widget/ImageView:setImageBitmap	(Landroid/graphics/Bitmap;)V
    //   123: aload 4
    //   125: ifnull +8 -> 133
    //   128: aload 4
    //   130: invokevirtual 575	java/io/FileOutputStream:close	()V
    //   133: aload_0
    //   134: aconst_null
    //   135: putfield 87	org/telegram/ui/WallpapersActivity:currentPicturePath	Ljava/lang/String;
    //   138: return
    //   139: astore_3
    //   140: ldc_w 294
    //   143: aload_3
    //   144: invokestatic 300	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   147: goto -14 -> 133
    //   150: astore_3
    //   151: aload 5
    //   153: astore 4
    //   155: aload_3
    //   156: astore 5
    //   158: aload 4
    //   160: astore_3
    //   161: ldc_w 294
    //   164: aload 5
    //   166: invokestatic 300	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   169: aload 4
    //   171: ifnull -38 -> 133
    //   174: aload 4
    //   176: invokevirtual 575	java/io/FileOutputStream:close	()V
    //   179: goto -46 -> 133
    //   182: astore_3
    //   183: ldc_w 294
    //   186: aload_3
    //   187: invokestatic 300	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   190: goto -57 -> 133
    //   193: astore 4
    //   195: aload_3
    //   196: ifnull +7 -> 203
    //   199: aload_3
    //   200: invokevirtual 575	java/io/FileOutputStream:close	()V
    //   203: aload 4
    //   205: athrow
    //   206: astore_3
    //   207: ldc_w 294
    //   210: aload_3
    //   211: invokestatic 300	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   214: goto -11 -> 203
    //   217: iload_1
    //   218: bipush 11
    //   220: if_icmpne -82 -> 138
    //   223: aload_3
    //   224: ifnull -86 -> 138
    //   227: aload_3
    //   228: invokevirtual 581	android/content/Intent:getData	()Landroid/net/Uri;
    //   231: ifnull -93 -> 138
    //   234: invokestatic 549	org/telegram/messenger/AndroidUtilities:getRealScreenSize	()Landroid/graphics/Point;
    //   237: astore 4
    //   239: aconst_null
    //   240: aload_3
    //   241: invokevirtual 581	android/content/Intent:getData	()Landroid/net/Uri;
    //   244: aload 4
    //   246: getfield 153	android/graphics/Point:x	I
    //   249: i2f
    //   250: aload 4
    //   252: getfield 156	android/graphics/Point:y	I
    //   255: i2f
    //   256: iconst_1
    //   257: invokestatic 555	org/telegram/messenger/ImageLoader:loadBitmap	(Ljava/lang/String;Landroid/net/Uri;FFZ)Landroid/graphics/Bitmap;
    //   260: astore_3
    //   261: new 557	java/io/FileOutputStream
    //   264: dup
    //   265: new 209	java/io/File
    //   268: dup
    //   269: invokestatic 310	org/telegram/messenger/ApplicationLoader:getFilesDirFixed	()Ljava/io/File;
    //   272: ldc_w 312
    //   275: invokespecial 219	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   278: invokespecial 560	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   281: astore 4
    //   283: aload_3
    //   284: getstatic 566	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
    //   287: bipush 87
    //   289: aload 4
    //   291: invokevirtual 572	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   294: pop
    //   295: aload_0
    //   296: iconst_m1
    //   297: putfield 76	org/telegram/ui/WallpapersActivity:selectedBackground	I
    //   300: aload_0
    //   301: iconst_0
    //   302: putfield 83	org/telegram/ui/WallpapersActivity:selectedColor	I
    //   305: aload_0
    //   306: getfield 102	org/telegram/ui/WallpapersActivity:backgroundImage	Landroid/widget/ImageView;
    //   309: invokevirtual 229	android/widget/ImageView:getDrawable	()Landroid/graphics/drawable/Drawable;
    //   312: pop
    //   313: aload_0
    //   314: getfield 102	org/telegram/ui/WallpapersActivity:backgroundImage	Landroid/widget/ImageView;
    //   317: aload_3
    //   318: invokevirtual 320	android/widget/ImageView:setImageBitmap	(Landroid/graphics/Bitmap;)V
    //   321: return
    //   322: astore_3
    //   323: ldc_w 294
    //   326: aload_3
    //   327: invokestatic 300	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   330: return
    //   331: astore 5
    //   333: aload 4
    //   335: astore_3
    //   336: aload 5
    //   338: astore 4
    //   340: goto -145 -> 195
    //   343: astore 5
    //   345: goto -187 -> 158
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	348	0	this	WallpapersActivity
    //   0	348	1	paramInt1	int
    //   0	348	2	paramInt2	int
    //   0	348	3	paramIntent	Intent
    //   19	156	4	localObject1	Object
    //   193	11	4	localObject2	Object
    //   237	102	4	localObject3	Object
    //   22	143	5	localIntent	Intent
    //   331	6	5	localObject4	Object
    //   343	1	5	localException	Exception
    //   30	89	6	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   128	133	139	java/lang/Exception
    //   27	32	150	java/lang/Exception
    //   35	58	150	java/lang/Exception
    //   61	83	150	java/lang/Exception
    //   174	179	182	java/lang/Exception
    //   27	32	193	finally
    //   35	58	193	finally
    //   61	83	193	finally
    //   161	169	193	finally
    //   199	203	206	java/lang/Exception
    //   234	321	322	java/lang/Exception
    //   83	123	331	finally
    //   83	123	343	java/lang/Exception
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.wallpapersDidLoaded);
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
    this.selectedBackground = localSharedPreferences.getInt("selectedBackground", 1000001);
    this.selectedColor = localSharedPreferences.getInt("selectedColor", 0);
    MessagesStorage.getInstance().getWallpapers();
    new File(ApplicationLoader.getFilesDirFixed(), "wallpaper-temp.jpg").delete();
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailedLoad);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.wallpapersDidLoaded);
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
    processSelectedBackground();
  }
  
  public void restoreSelfArgs(Bundle paramBundle)
  {
    this.currentPicturePath = paramBundle.getString("path");
  }
  
  public void saveSelfArgs(Bundle paramBundle)
  {
    if (this.currentPicturePath != null) {
      paramBundle.putString("path", this.currentPicturePath);
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
      return WallpapersActivity.this.wallPapers.size() + 1;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      WallpaperCell localWallpaperCell = (WallpaperCell)paramViewHolder.itemView;
      if (paramInt == 0) {}
      for (paramViewHolder = null;; paramViewHolder = (TLRPC.WallPaper)WallpapersActivity.this.wallPapers.get(paramInt - 1))
      {
        localWallpaperCell.setWallpaper(paramViewHolder, WallpapersActivity.this.selectedBackground);
        return;
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      return new Holder(new WallpaperCell(this.mContext));
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/WallpapersActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */