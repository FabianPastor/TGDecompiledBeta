package net.hockeyapp.android;

import android.annotation.SuppressLint;
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
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.ImageUtils;
import net.hockeyapp.android.views.PaintView;

public class PaintActivity
  extends Activity
{
  private Uri mImageUri;
  private PaintView mPaintView;
  
  private String determineFilename(Uri paramUri, String paramString)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    Cursor localCursor = getApplicationContext().getContentResolver().query(paramUri, new String[] { "_data" }, null, null, null);
    paramUri = (Uri)localObject1;
    if (localCursor != null) {
      paramUri = (Uri)localObject2;
    }
    for (;;)
    {
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
      paramString = new File(paramUri).getName();
    }
  }
  
  @SuppressLint({"StaticFieldLeak"})
  private void makeResult()
  {
    this.mPaintView.setDrawingCacheEnabled(true);
    AsyncTaskUtils.execute(new AsyncTask()
    {
      File result;
      
      protected Boolean doInBackground(Void... paramAnonymousVarArgs)
      {
        paramAnonymousVarArgs = new File(PaintActivity.this.getCacheDir(), "HockeyApp");
        if ((!paramAnonymousVarArgs.exists()) && (!paramAnonymousVarArgs.mkdir())) {
          paramAnonymousVarArgs = Boolean.valueOf(false);
        }
        for (;;)
        {
          return paramAnonymousVarArgs;
          String str = PaintActivity.this.determineFilename(PaintActivity.this.mImageUri, PaintActivity.this.mImageUri.getLastPathSegment());
          this.result = new File(paramAnonymousVarArgs, str + ".jpg");
          for (int i = 1; this.result.exists(); i++) {
            this.result = new File(paramAnonymousVarArgs, str + "_" + i + ".jpg");
          }
          try
          {
            paramAnonymousVarArgs = new java/io/FileOutputStream;
            paramAnonymousVarArgs.<init>(this.result);
            this.val$bitmap.compress(Bitmap.CompressFormat.JPEG, 100, paramAnonymousVarArgs);
            paramAnonymousVarArgs.close();
            paramAnonymousVarArgs = Boolean.valueOf(true);
          }
          catch (IOException paramAnonymousVarArgs)
          {
            HockeyLog.error("Could not save image.", paramAnonymousVarArgs);
            paramAnonymousVarArgs = Boolean.valueOf(false);
          }
        }
      }
      
      protected void onPostExecute(Boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean.booleanValue())
        {
          paramAnonymousBoolean = new Intent();
          paramAnonymousBoolean.putExtra("imageUri", Uri.fromFile(this.result));
          if (PaintActivity.this.getParent() == null) {
            PaintActivity.this.setResult(-1, paramAnonymousBoolean);
          }
        }
        for (;;)
        {
          PaintActivity.this.finish();
          return;
          PaintActivity.this.getParent().setResult(-1, paramAnonymousBoolean);
          continue;
          if (PaintActivity.this.getParent() == null) {
            PaintActivity.this.setResult(0);
          } else {
            PaintActivity.this.getParent().setResult(0);
          }
        }
      }
    });
  }
  
  private void showPaintView()
  {
    int i = getResources().getDisplayMetrics().widthPixels;
    int j = getResources().getDisplayMetrics().heightPixels;
    this.mPaintView = new PaintView(this, this.mImageUri, i, j);
    LinearLayout localLinearLayout1 = new LinearLayout(this);
    localLinearLayout1.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
    localLinearLayout1.setGravity(17);
    localLinearLayout1.setOrientation(1);
    LinearLayout localLinearLayout2 = new LinearLayout(this);
    localLinearLayout2.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
    localLinearLayout2.setGravity(17);
    localLinearLayout2.setOrientation(0);
    localLinearLayout1.addView(localLinearLayout2);
    localLinearLayout2.addView(this.mPaintView);
    setContentView(localLinearLayout1);
    Toast.makeText(this, R.string.hockeyapp_paint_indicator_toast, 1).show();
  }
  
  @SuppressLint({"StaticFieldLeak"})
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getIntent().getExtras();
    if ((paramBundle == null) || (paramBundle.getParcelable("imageUri") == null)) {
      HockeyLog.error("Can't set up PaintActivity as image extra was not provided!");
    }
    for (;;)
    {
      return;
      this.mImageUri = ((Uri)paramBundle.getParcelable("imageUri"));
      AsyncTaskUtils.execute(new AsyncTask()
      {
        protected Integer doInBackground(Void... paramAnonymousVarArgs)
        {
          return Integer.valueOf(ImageUtils.determineOrientation(PaintActivity.this, PaintActivity.this.mImageUri));
        }
        
        protected void onPostExecute(Integer paramAnonymousInteger)
        {
          PaintActivity.this.setRequestedOrientation(paramAnonymousInteger.intValue());
          int i;
          if (PaintActivity.this.getResources().getDisplayMetrics().widthPixels > PaintActivity.this.getResources().getDisplayMetrics().heightPixels)
          {
            i = 0;
            if (i == paramAnonymousInteger.intValue()) {
              break label61;
            }
            HockeyLog.debug("Image loading skipped because activity will be destroyed for orientation change.");
          }
          for (;;)
          {
            return;
            i = 1;
            break;
            label61:
            PaintActivity.this.showPaintView();
          }
        }
      });
    }
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
          }
          for (;;)
          {
            return;
            PaintActivity.this.makeResult();
            continue;
            PaintActivity.this.finish();
          }
        }
      };
      new AlertDialog.Builder(this).setMessage(R.string.hockeyapp_paint_dialog_message).setPositiveButton(R.string.hockeyapp_paint_dialog_positive_button, paramKeyEvent).setNegativeButton(R.string.hockeyapp_paint_dialog_negative_button, paramKeyEvent).setNeutralButton(R.string.hockeyapp_paint_dialog_neutral_button, paramKeyEvent).show();
    }
    for (boolean bool = true;; bool = super.onKeyDown(paramInt, paramKeyEvent)) {
      return bool;
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    boolean bool = true;
    switch (paramMenuItem.getItemId())
    {
    default: 
      bool = super.onOptionsItemSelected(paramMenuItem);
    }
    for (;;)
    {
      return bool;
      makeResult();
      continue;
      this.mPaintView.undo();
      continue;
      this.mPaintView.clearImage();
    }
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