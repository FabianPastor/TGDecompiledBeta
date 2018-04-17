package org.telegram.ui.Components;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v4.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.AlertDialog.Builder;

public class WallpaperUpdater {
    private String currentPicturePath;
    private File currentWallpaperPath;
    private WallpaperUpdaterDelegate delegate;
    private Activity parentActivity;
    private File picturePath = null;

    public interface WallpaperUpdaterDelegate {
        void didSelectWallpaper(File file, Bitmap bitmap);

        void needOpenColorPicker();
    }

    public WallpaperUpdater(Activity activity, WallpaperUpdaterDelegate wallpaperUpdaterDelegate) {
        this.parentActivity = activity;
        this.delegate = wallpaperUpdaterDelegate;
        File directory = FileLoader.getDirectory(4);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Utilities.random.nextInt());
        stringBuilder.append(".jpg");
        this.currentWallpaperPath = new File(directory, stringBuilder.toString());
    }

    public void showAlert(final boolean fromTheme) {
        CharSequence[] items;
        Builder builder = new Builder(this.parentActivity);
        if (fromTheme) {
            items = new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley), LocaleController.getString("SelectColor", R.string.SelectColor), LocaleController.getString("Default", R.string.Default), LocaleController.getString("Cancel", R.string.Cancel)};
        } else {
            items = new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley), LocaleController.getString("Cancel", R.string.Cancel)};
        }
        builder.setItems(items, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    try {
                        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                        File image = AndroidUtilities.generatePicturePath();
                        if (image != null) {
                            if (VERSION.SDK_INT >= 24) {
                                takePictureIntent.putExtra("output", FileProvider.getUriForFile(WallpaperUpdater.this.parentActivity, "org.telegram.messenger.beta.provider", image));
                                takePictureIntent.addFlags(2);
                                takePictureIntent.addFlags(1);
                            } else {
                                takePictureIntent.putExtra("output", Uri.fromFile(image));
                            }
                            WallpaperUpdater.this.currentPicturePath = image.getAbsolutePath();
                        }
                        WallpaperUpdater.this.parentActivity.startActivityForResult(takePictureIntent, 10);
                    } catch (Throwable e) {
                        try {
                            FileLog.m3e(e);
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                    }
                } else if (i == 1) {
                    Intent photoPickerIntent = new Intent("android.intent.action.PICK");
                    photoPickerIntent.setType("image/*");
                    WallpaperUpdater.this.parentActivity.startActivityForResult(photoPickerIntent, 11);
                } else if (fromTheme != null) {
                    if (i == 2) {
                        WallpaperUpdater.this.delegate.needOpenColorPicker();
                    } else if (i == 3) {
                        WallpaperUpdater.this.delegate.didSelectWallpaper(null, null);
                    }
                }
            }
        });
        builder.show();
    }

    public void cleanup() {
        this.currentWallpaperPath.delete();
    }

    public File getCurrentWallpaperPath() {
        return this.currentWallpaperPath;
    }

    public String getCurrentPicturePath() {
        return this.currentPicturePath;
    }

    public void setCurrentPicturePath(String value) {
        this.currentPicturePath = value;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            Bitmap bitmap;
            if (requestCode == 10) {
                AndroidUtilities.addMediaToGallery(this.currentPicturePath);
                FileOutputStream stream = null;
                try {
                    Point screenSize = AndroidUtilities.getRealScreenSize();
                    bitmap = ImageLoader.loadBitmap(this.currentPicturePath, null, (float) screenSize.x, (float) screenSize.y, true);
                    stream = new FileOutputStream(this.currentWallpaperPath);
                    bitmap.compress(CompressFormat.JPEG, 87, stream);
                    this.delegate.didSelectWallpaper(this.currentWallpaperPath, bitmap);
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                    if (stream != null) {
                        stream.close();
                    }
                } catch (Throwable th) {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (Throwable e3) {
                            FileLog.m3e(e3);
                        }
                    }
                }
                this.currentPicturePath = null;
            } else {
                if (requestCode == 11 && data != null) {
                    if (data.getData() != null) {
                        try {
                            Point screenSize2 = AndroidUtilities.getRealScreenSize();
                            bitmap = ImageLoader.loadBitmap(null, data.getData(), (float) screenSize2.x, (float) screenSize2.y, true);
                            bitmap.compress(CompressFormat.JPEG, 87, new FileOutputStream(this.currentWallpaperPath));
                            this.delegate.didSelectWallpaper(this.currentWallpaperPath, bitmap);
                        } catch (Throwable e4) {
                            FileLog.m3e(e4);
                        }
                    }
                }
            }
        }
    }
}
