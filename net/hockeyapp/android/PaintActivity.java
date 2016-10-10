package net.hockeyapp.android;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.views.PaintView;

public class PaintActivity
  extends Activity
{
  public static final String EXTRA_IMAGE_URI = "imageUri";
  private static final int MENU_CLEAR_ID = 3;
  private static final int MENU_SAVE_ID = 1;
  private static final int MENU_UNDO_ID = 2;
  private String mImageName;
  private PaintView mPaintView;
  
  private String determineFilename(Uri paramUri, String paramString)
  {
    Object localObject2 = null;
    Object localObject1 = null;
    Cursor localCursor = getApplicationContext().getContentResolver().query(paramUri, new String[] { "_data" }, null, null, null);
    paramUri = (Uri)localObject2;
    if (localCursor != null) {
      paramUri = (Uri)localObject1;
    }
    try
    {
      if (localCursor.moveToFirst()) {
        paramUri = localCursor.getString(0);
      }
      localCursor.close();
      if (paramUri == null) {
        return paramString;
      }
    }
    finally
    {
      localCursor.close();
    }
    return new File(paramUri).getName();
  }
  
  private void makeResult()
  {
    Object localObject = new File(getCacheDir(), "HockeyApp");
    ((File)localObject).mkdir();
    File localFile = new File((File)localObject, this.mImageName + ".jpg");
    int i = 1;
    while (localFile.exists())
    {
      localFile = new File((File)localObject, this.mImageName + "_" + i + ".jpg");
      i += 1;
    }
    this.mPaintView.setDrawingCacheEnabled(true);
    new AsyncTask()
    {
      protected Void doInBackground(File... paramAnonymousVarArgs)
      {
        try
        {
          paramAnonymousVarArgs = new FileOutputStream(paramAnonymousVarArgs[0]);
          this.val$bitmap.compress(Bitmap.CompressFormat.JPEG, 100, paramAnonymousVarArgs);
          paramAnonymousVarArgs.close();
          return null;
        }
        catch (IOException paramAnonymousVarArgs)
        {
          for (;;)
          {
            paramAnonymousVarArgs.printStackTrace();
            HockeyLog.error("Could not save image.", paramAnonymousVarArgs);
          }
        }
      }
    }.execute(new File[] { localFile });
    localObject = new Intent();
    ((Intent)localObject).putExtra("imageUri", Uri.fromFile(localFile));
    if (getParent() == null) {
      setResult(-1, (Intent)localObject);
    }
    for (;;)
    {
      finish();
      return;
      getParent().setResult(-1, (Intent)localObject);
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = (Uri)getIntent().getExtras().getParcelable("imageUri");
    this.mImageName = determineFilename(paramBundle, paramBundle.getLastPathSegment());
    int j = getResources().getDisplayMetrics().widthPixels;
    int k = getResources().getDisplayMetrics().heightPixels;
    if (j > k) {}
    for (int i = 0;; i = 1)
    {
      int m = PaintView.determineOrientation(getContentResolver(), paramBundle);
      setRequestedOrientation(m);
      if (i == m) {
        break;
      }
      HockeyLog.debug("Image loading skipped because activity will be destroyed for orientation change.");
      return;
    }
    this.mPaintView = new PaintView(this, paramBundle, j, k);
    paramBundle = new LinearLayout(this);
    paramBundle.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
    paramBundle.setGravity(17);
    paramBundle.setOrientation(1);
    LinearLayout localLinearLayout = new LinearLayout(this);
    localLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
    localLinearLayout.setGravity(17);
    localLinearLayout.setOrientation(0);
    paramBundle.addView(localLinearLayout);
    localLinearLayout.addView(this.mPaintView);
    setContentView(paramBundle);
    Toast.makeText(this, getString(R.string.hockeyapp_paint_indicator_toast), 1).show();
  }
  
  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    super.onCreateOptionsMenu(paramMenu);
    paramMenu.add(0, 1, 0, getString(R.string.hockeyapp_paint_menu_save));
    paramMenu.add(0, 2, 0, getString(R.string.hockeyapp_paint_menu_undo));
    paramMenu.add(0, 3, 0, getString(R.string.hockeyapp_paint_menu_clear));
    return true;
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramInt == 4) && (!this.mPaintView.isClear()))
    {
      paramKeyEvent = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          switch (paramAnonymousInt)
          {
          default: 
            return;
          case -1: 
            PaintActivity.this.makeResult();
            return;
          }
          PaintActivity.this.finish();
        }
      };
      new AlertDialog.Builder(this).setMessage(R.string.hockeyapp_paint_dialog_message).setPositiveButton(R.string.hockeyapp_paint_dialog_positive_button, paramKeyEvent).setNegativeButton(R.string.hockeyapp_paint_dialog_negative_button, paramKeyEvent).setNeutralButton(R.string.hockeyapp_paint_dialog_neutral_button, paramKeyEvent).show();
      return true;
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    case 1: 
      makeResult();
      return true;
    case 2: 
      this.mPaintView.undo();
      return true;
    }
    this.mPaintView.clearImage();
    return true;
  }
  
  public boolean onPrepareOptionsMenu(Menu paramMenu)
  {
    super.onPrepareOptionsMenu(paramMenu);
    return true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/PaintActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */