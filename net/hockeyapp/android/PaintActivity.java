package net.hockeyapp.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.ImageUtils;
import net.hockeyapp.android.views.PaintView;

public class PaintActivity extends Activity {
    private Uri mImageUri;
    private PaintView mPaintView;

    @SuppressLint({"StaticFieldLeak"})
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras == null || extras.getParcelable("imageUri") == null) {
            HockeyLog.error("Can't set up PaintActivity as image extra was not provided!");
            return;
        }
        this.mImageUri = (Uri) extras.getParcelable("imageUri");
        AsyncTaskUtils.execute(new AsyncTask<Void, Object, Integer>() {
            protected Integer doInBackground(Void... voids) {
                return Integer.valueOf(ImageUtils.determineOrientation(PaintActivity.this, PaintActivity.this.mImageUri));
            }

            protected void onPostExecute(Integer desiredOrientation) {
                PaintActivity.this.setRequestedOrientation(desiredOrientation.intValue());
                if ((PaintActivity.this.getResources().getDisplayMetrics().widthPixels > PaintActivity.this.getResources().getDisplayMetrics().heightPixels ? 0 : 1) != desiredOrientation.intValue()) {
                    HockeyLog.debug("Image loading skipped because activity will be destroyed for orientation change.");
                } else {
                    PaintActivity.this.showPaintView();
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, getString(R.string.hockeyapp_paint_menu_save));
        menu.add(0, 2, 0, getString(R.string.hockeyapp_paint_menu_undo));
        menu.add(0, 3, 0, getString(R.string.hockeyapp_paint_menu_clear));
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                makeResult();
                return true;
            case 2:
                this.mPaintView.undo();
                return true;
            case 3:
                this.mPaintView.clearImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || this.mPaintView.isClear()) {
            return super.onKeyDown(keyCode, event);
        }
        OnClickListener dialogClickListener = new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case -2:
                        PaintActivity.this.finish();
                        return;
                    case -1:
                        PaintActivity.this.makeResult();
                        return;
                    default:
                        return;
                }
            }
        };
        new Builder(this).setMessage(R.string.hockeyapp_paint_dialog_message).setPositiveButton(R.string.hockeyapp_paint_dialog_positive_button, dialogClickListener).setNegativeButton(R.string.hockeyapp_paint_dialog_negative_button, dialogClickListener).setNeutralButton(R.string.hockeyapp_paint_dialog_neutral_button, dialogClickListener).show();
        return true;
    }

    private void showPaintView() {
        this.mPaintView = new PaintView(this, this.mImageUri, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        LinearLayout vLayout = new LinearLayout(this);
        vLayout.setLayoutParams(new LayoutParams(-1, -1));
        vLayout.setGravity(17);
        vLayout.setOrientation(1);
        LinearLayout hLayout = new LinearLayout(this);
        hLayout.setLayoutParams(new LayoutParams(-1, -1));
        hLayout.setGravity(17);
        hLayout.setOrientation(0);
        vLayout.addView(hLayout);
        hLayout.addView(this.mPaintView);
        setContentView(vLayout);
        Toast.makeText(this, R.string.hockeyapp_paint_indicator_toast, 1).show();
    }

    @SuppressLint({"StaticFieldLeak"})
    private void makeResult() {
        this.mPaintView.setDrawingCacheEnabled(true);
        final Bitmap bitmap = this.mPaintView.getDrawingCache();
        AsyncTaskUtils.execute(new AsyncTask<Void, Object, Boolean>() {
            File result;

            protected Boolean doInBackground(Void... args) {
                File hockeyAppCache = new File(PaintActivity.this.getCacheDir(), "HockeyApp");
                if (!hockeyAppCache.exists() && !hockeyAppCache.mkdir()) {
                    return Boolean.valueOf(false);
                }
                String imageName = PaintActivity.this.determineFilename(PaintActivity.this.mImageUri, PaintActivity.this.mImageUri.getLastPathSegment());
                this.result = new File(hockeyAppCache, imageName + ".jpg");
                int suffix = 1;
                while (this.result.exists()) {
                    this.result = new File(hockeyAppCache, imageName + "_" + suffix + ".jpg");
                    suffix++;
                }
                try {
                    FileOutputStream out = new FileOutputStream(this.result);
                    bitmap.compress(CompressFormat.JPEG, 100, out);
                    out.close();
                    return Boolean.valueOf(true);
                } catch (Throwable e) {
                    HockeyLog.error("Could not save image.", e);
                    return Boolean.valueOf(false);
                }
            }

            protected void onPostExecute(Boolean success) {
                if (success.booleanValue()) {
                    Intent intent = new Intent();
                    intent.putExtra("imageUri", Uri.fromFile(this.result));
                    if (PaintActivity.this.getParent() == null) {
                        PaintActivity.this.setResult(-1, intent);
                    } else {
                        PaintActivity.this.getParent().setResult(-1, intent);
                    }
                } else if (PaintActivity.this.getParent() == null) {
                    PaintActivity.this.setResult(0);
                } else {
                    PaintActivity.this.getParent().setResult(0);
                }
                PaintActivity.this.finish();
            }
        });
    }

    private String determineFilename(Uri uri, String fallback) {
        String path = null;
        Cursor metaCursor = getApplicationContext().getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
        if (metaCursor != null) {
            try {
                if (metaCursor.moveToFirst()) {
                    path = metaCursor.getString(0);
                }
                metaCursor.close();
            } catch (Throwable th) {
                metaCursor.close();
            }
        }
        return path == null ? fallback : new File(path).getName();
    }
}
