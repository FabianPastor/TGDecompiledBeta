package org.telegram.ui.Components;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v4.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
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
        wallpaperUpdaterDelegate = FileLoader.getDirectory(4);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Utilities.random.nextInt());
        stringBuilder.append(".jpg");
        this.currentWallpaperPath = new File(wallpaperUpdaterDelegate, stringBuilder.toString());
    }

    public void showAlert(final boolean z) {
        Builder builder = new Builder(this.parentActivity);
        builder.setItems(z ? new CharSequence[]{LocaleController.getString("FromCamera", C0446R.string.FromCamera), LocaleController.getString("FromGalley", C0446R.string.FromGalley), LocaleController.getString("SelectColor", C0446R.string.SelectColor), LocaleController.getString("Default", C0446R.string.Default), LocaleController.getString("Cancel", C0446R.string.Cancel)} : new CharSequence[]{LocaleController.getString("FromCamera", C0446R.string.FromCamera), LocaleController.getString("FromGalley", C0446R.string.FromGalley), LocaleController.getString("Cancel", C0446R.string.Cancel)}, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    try {
                        i = new Intent("android.media.action.IMAGE_CAPTURE");
                        File generatePicturePath = AndroidUtilities.generatePicturePath();
                        if (generatePicturePath != null) {
                            if (VERSION.SDK_INT >= 24) {
                                i.putExtra("output", FileProvider.getUriForFile(WallpaperUpdater.this.parentActivity, "org.telegram.messenger.provider", generatePicturePath));
                                i.addFlags(2);
                                i.addFlags(1);
                            } else {
                                i.putExtra("output", Uri.fromFile(generatePicturePath));
                            }
                            WallpaperUpdater.this.currentPicturePath = generatePicturePath.getAbsolutePath();
                        }
                        WallpaperUpdater.this.parentActivity.startActivityForResult(i, 10);
                    } catch (Throwable e) {
                        try {
                            FileLog.m3e(e);
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                    }
                } else if (i == 1) {
                    dialogInterface = new Intent("android.intent.action.PICK");
                    dialogInterface.setType("image/*");
                    WallpaperUpdater.this.parentActivity.startActivityForResult(dialogInterface, 11);
                } else if (z == null) {
                } else {
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

    public void setCurrentPicturePath(String str) {
        this.currentPicturePath = str;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        Throwable e;
        if (i2 == -1) {
            FileOutputStream fileOutputStream = null;
            if (i == 10) {
                AndroidUtilities.addMediaToGallery(this.currentPicturePath);
                try {
                    i = AndroidUtilities.getRealScreenSize();
                    i = ImageLoader.loadBitmap(this.currentPicturePath, null, (float) i.x, (float) i.y, true);
                    i2 = new FileOutputStream(this.currentWallpaperPath);
                    try {
                        i.compress(CompressFormat.JPEG, 87, i2);
                        this.delegate.didSelectWallpaper(this.currentWallpaperPath, i);
                        if (i2 != 0) {
                            try {
                                i2.close();
                            } catch (Throwable e2) {
                                FileLog.m3e(e2);
                            }
                        }
                    } catch (Exception e3) {
                        e2 = e3;
                        try {
                            FileLog.m3e(e2);
                            if (i2 != 0) {
                                i2.close();
                            }
                            this.currentPicturePath = null;
                        } catch (Throwable th) {
                            i = th;
                            fileOutputStream = i2;
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (Throwable e4) {
                                    FileLog.m3e(e4);
                                }
                            }
                            throw i;
                        }
                    }
                } catch (Exception e5) {
                    e2 = e5;
                    i2 = 0;
                    FileLog.m3e(e2);
                    if (i2 != 0) {
                        i2.close();
                    }
                    this.currentPicturePath = null;
                } catch (Throwable th2) {
                    i = th2;
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    throw i;
                }
                this.currentPicturePath = null;
            } else {
                if (i == 11 && intent != null) {
                    if (intent.getData() != 0) {
                        try {
                            i = AndroidUtilities.getRealScreenSize();
                            i = ImageLoader.loadBitmap(null, intent.getData(), (float) i.x, (float) i.y, true);
                            i.compress(CompressFormat.JPEG, 87, new FileOutputStream(this.currentWallpaperPath));
                            this.delegate.didSelectWallpaper(this.currentWallpaperPath, i);
                        } catch (Throwable e22) {
                            FileLog.m3e(e22);
                        }
                    }
                }
            }
        }
    }
}
