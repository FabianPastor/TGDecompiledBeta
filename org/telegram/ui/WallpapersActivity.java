package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
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
import com.google.android.gms.common.ConnectionResult;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_account_getWallPapers;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.TL_wallPaperSolid;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.tgnet.TLRPC.WallPaper;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.WallpaperCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

public class WallpapersActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private ImageView backgroundImage;
    private String currentPicturePath;
    private View doneButton;
    private ListAdapter listAdapter;
    private String loadingFile = null;
    private File loadingFileObject = null;
    private PhotoSize loadingSize = null;
    private FrameLayout progressView;
    private View progressViewBackground;
    private int selectedBackground;
    private int selectedColor;
    private ArrayList<WallPaper> wallPapers = new ArrayList();
    private HashMap<Integer, WallPaper> wallpappersByIds = new HashMap();

    private class ListAdapter extends Adapter {
        private Context mContext;

        private class Holder extends ViewHolder {
            public Holder(View itemView) {
                super(itemView);
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return WallpapersActivity.this.wallPapers.size() + 1;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new Holder(new WallpaperCell(this.mContext));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ((WallpaperCell) viewHolder.itemView).setWallpaper(i == 0 ? null : (WallPaper) WallpapersActivity.this.wallPapers.get(i - 1), WallpapersActivity.this.selectedBackground);
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.wallpapersDidLoaded);
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        this.selectedBackground = preferences.getInt("selectedBackground", 1000001);
        this.selectedColor = preferences.getInt("selectedColor", 0);
        MessagesStorage.getInstance().getWallpapers();
        new File(ApplicationLoader.getFilesDirFixed(), "wallpaper-temp.jpg").delete();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailedLoad);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.wallpapersDidLoaded);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ChatBackground", R.string.ChatBackground));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    WallpapersActivity.this.finishFragment();
                } else if (id == 1) {
                    boolean done;
                    WallPaper wallPaper = (WallPaper) WallpapersActivity.this.wallpappersByIds.get(Integer.valueOf(WallpapersActivity.this.selectedBackground));
                    if (wallPaper != null && wallPaper.id != 1000001 && (wallPaper instanceof TL_wallPaper)) {
                        int width = AndroidUtilities.displaySize.x;
                        int height = AndroidUtilities.displaySize.y;
                        if (width > height) {
                            int temp = width;
                            width = height;
                            height = temp;
                        }
                        PhotoSize size = FileLoader.getClosestPhotoSizeWithSize(wallPaper.sizes, Math.min(width, height));
                        try {
                            done = AndroidUtilities.copyFile(new File(FileLoader.getInstance().getDirectory(4), size.location.volume_id + "_" + size.location.local_id + ".jpg"), new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg"));
                        } catch (Throwable e) {
                            done = false;
                            FileLog.e("tmessages", e);
                        }
                    } else if (WallpapersActivity.this.selectedBackground == -1) {
                        done = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper-temp.jpg").renameTo(new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg"));
                    } else {
                        done = true;
                    }
                    if (done) {
                        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                        editor.putInt("selectedBackground", WallpapersActivity.this.selectedBackground);
                        editor.putInt("selectedColor", WallpapersActivity.this.selectedColor);
                        editor.commit();
                        ApplicationLoader.reloadWallpaper();
                    }
                    WallpapersActivity.this.finishFragment();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        this.backgroundImage = new ImageView(context);
        this.backgroundImage.setScaleType(ScaleType.CENTER_CROP);
        frameLayout.addView(this.backgroundImage, LayoutHelper.createFrame(-1, -1.0f));
        this.backgroundImage.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        this.progressView = new FrameLayout(context);
        this.progressView.setVisibility(4);
        frameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 52.0f));
        this.progressViewBackground = new View(context);
        this.progressViewBackground.setBackgroundResource(R.drawable.system_loader);
        this.progressView.addView(this.progressViewBackground, LayoutHelper.createFrame(36, 36, 17));
        ProgressBar progressBar = new ProgressBar(context);
        try {
            progressBar.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.loading_animation));
        } catch (Exception e) {
        }
        progressBar.setIndeterminate(true);
        AndroidUtilities.setProgressBarAnimationDuration(progressBar, ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED);
        this.progressView.addView(progressBar, LayoutHelper.createFrame(32, 32, 17));
        RecyclerListView listView = new RecyclerListView(context);
        listView.setClipToPadding(false);
        listView.setTag(Integer.valueOf(8));
        listView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(0);
        listView.setLayoutManager(layoutManager);
        listView.setDisallowInterceptTouchEvents(true);
        listView.setOverScrollMode(2);
        Adapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        listView.setAdapter(listAdapter);
        frameLayout.addView(listView, LayoutHelper.createFrame(-1, 102, 83));
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (position == 0) {
                    if (WallpapersActivity.this.getParentActivity() != null) {
                        Builder builder = new Builder(WallpapersActivity.this.getParentActivity());
                        builder.setItems(new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley), LocaleController.getString("Cancel", R.string.Cancel)}, new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    try {
                                        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                                        File image = AndroidUtilities.generatePicturePath();
                                        if (image != null) {
                                            if (VERSION.SDK_INT >= 24) {
                                                takePictureIntent.putExtra("output", FileProvider.getUriForFile(WallpapersActivity.this.getParentActivity(), "org.telegram.messenger.beta.provider", image));
                                                takePictureIntent.addFlags(2);
                                                takePictureIntent.addFlags(1);
                                            } else {
                                                takePictureIntent.putExtra("output", Uri.fromFile(image));
                                            }
                                            WallpapersActivity.this.currentPicturePath = image.getAbsolutePath();
                                        }
                                        WallpapersActivity.this.startActivityForResult(takePictureIntent, 10);
                                    } catch (Throwable e) {
                                        FileLog.e("tmessages", e);
                                    }
                                } else if (i == 1) {
                                    Intent photoPickerIntent = new Intent("android.intent.action.PICK");
                                    photoPickerIntent.setType("image/*");
                                    WallpapersActivity.this.startActivityForResult(photoPickerIntent, 11);
                                }
                            }
                        });
                        WallpapersActivity.this.showDialog(builder.create());
                    }
                } else if (position - 1 >= 0 && position - 1 < WallpapersActivity.this.wallPapers.size()) {
                    WallpapersActivity.this.selectedBackground = ((WallPaper) WallpapersActivity.this.wallPapers.get(position - 1)).id;
                    WallpapersActivity.this.listAdapter.notifyDataSetChanged();
                    WallpapersActivity.this.processSelectedBackground();
                }
            }
        });
        processSelectedBackground();
        return this.fragmentView;
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        Throwable e;
        Throwable th;
        if (resultCode != -1) {
            return;
        }
        Point screenSize;
        Bitmap bitmap;
        Drawable drawable;
        if (requestCode == 10) {
            AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            FileOutputStream stream = null;
            try {
                screenSize = AndroidUtilities.getRealScreenSize();
                bitmap = ImageLoader.loadBitmap(this.currentPicturePath, null, (float) screenSize.x, (float) screenSize.y, true);
                FileOutputStream stream2 = new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "wallpaper-temp.jpg"));
                try {
                    bitmap.compress(CompressFormat.JPEG, 87, stream2);
                    this.selectedBackground = -1;
                    this.selectedColor = 0;
                    drawable = this.backgroundImage.getDrawable();
                    this.backgroundImage.setImageBitmap(bitmap);
                    if (stream2 != null) {
                        try {
                            stream2.close();
                        } catch (Throwable e2) {
                            FileLog.e("tmessages", e2);
                            stream = stream2;
                        }
                    }
                    stream = stream2;
                } catch (Exception e3) {
                    e2 = e3;
                    stream = stream2;
                    try {
                        FileLog.e("tmessages", e2);
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (Throwable e22) {
                                FileLog.e("tmessages", e22);
                            }
                        }
                        this.currentPicturePath = null;
                    } catch (Throwable th2) {
                        th = th2;
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (Throwable e222) {
                                FileLog.e("tmessages", e222);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    stream = stream2;
                    if (stream != null) {
                        stream.close();
                    }
                    throw th;
                }
            } catch (Exception e4) {
                e222 = e4;
                FileLog.e("tmessages", e222);
                if (stream != null) {
                    stream.close();
                }
                this.currentPicturePath = null;
            }
            this.currentPicturePath = null;
        } else if (requestCode == 11 && data != null && data.getData() != null) {
            try {
                screenSize = AndroidUtilities.getRealScreenSize();
                bitmap = ImageLoader.loadBitmap(null, data.getData(), (float) screenSize.x, (float) screenSize.y, true);
                bitmap.compress(CompressFormat.JPEG, 87, new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "wallpaper-temp.jpg")));
                this.selectedBackground = -1;
                this.selectedColor = 0;
                drawable = this.backgroundImage.getDrawable();
                this.backgroundImage.setImageBitmap(bitmap);
            } catch (Throwable e2222) {
                FileLog.e("tmessages", e2222);
            }
        }
    }

    public void saveSelfArgs(Bundle args) {
        if (this.currentPicturePath != null) {
            args.putString("path", this.currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        this.currentPicturePath = args.getString("path");
    }

    private void processSelectedBackground() {
        WallPaper wallPaper = (WallPaper) this.wallpappersByIds.get(Integer.valueOf(this.selectedBackground));
        if (this.selectedBackground == -1 || this.selectedBackground == 1000001 || wallPaper == null || !(wallPaper instanceof TL_wallPaper)) {
            if (this.loadingFile != null) {
                FileLoader.getInstance().cancelLoadFile(this.loadingSize);
            }
            if (this.selectedBackground == 1000001) {
                this.backgroundImage.setImageResource(R.drawable.background_hd);
                this.backgroundImage.setBackgroundColor(0);
                this.selectedColor = 0;
            } else if (this.selectedBackground == -1) {
                File toFile = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper-temp.jpg");
                if (!toFile.exists()) {
                    toFile = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg");
                }
                if (toFile.exists()) {
                    this.backgroundImage.setImageURI(Uri.fromFile(toFile));
                } else {
                    this.selectedBackground = 1000001;
                    processSelectedBackground();
                }
            } else if (wallPaper == null) {
                return;
            } else {
                if (wallPaper instanceof TL_wallPaperSolid) {
                    Drawable drawable = this.backgroundImage.getDrawable();
                    this.backgroundImage.setImageBitmap(null);
                    this.selectedColor = -16777216 | wallPaper.bg_color;
                    this.backgroundImage.setBackgroundColor(this.selectedColor);
                }
            }
            this.loadingFileObject = null;
            this.loadingFile = null;
            this.loadingSize = null;
            this.doneButton.setEnabled(true);
            this.progressView.setVisibility(8);
            return;
        }
        int width = AndroidUtilities.displaySize.x;
        int height = AndroidUtilities.displaySize.y;
        if (width > height) {
            int temp = width;
            width = height;
            height = temp;
        }
        PhotoSize size = FileLoader.getClosestPhotoSizeWithSize(wallPaper.sizes, Math.min(width, height));
        if (size != null) {
            String fileName = size.location.volume_id + "_" + size.location.local_id + ".jpg";
            File f = new File(FileLoader.getInstance().getDirectory(4), fileName);
            if (f.exists()) {
                if (this.loadingFile != null) {
                    FileLoader.getInstance().cancelLoadFile(this.loadingSize);
                }
                this.loadingFileObject = null;
                this.loadingFile = null;
                this.loadingSize = null;
                try {
                    this.backgroundImage.setImageURI(Uri.fromFile(f));
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
                this.backgroundImage.setBackgroundColor(0);
                this.selectedColor = 0;
                this.doneButton.setEnabled(true);
                this.progressView.setVisibility(8);
                return;
            }
            this.progressViewBackground.getBackground().setColorFilter(new PorterDuffColorFilter(AndroidUtilities.calcDrawableColor(this.backgroundImage.getDrawable())[0], Mode.MULTIPLY));
            this.loadingFile = fileName;
            this.loadingFileObject = f;
            this.doneButton.setEnabled(false);
            this.progressView.setVisibility(0);
            this.loadingSize = size;
            this.selectedColor = 0;
            FileLoader.getInstance().loadFile(size, null, true);
            this.backgroundImage.setBackgroundColor(0);
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        String location;
        if (id == NotificationCenter.FileDidFailedLoad) {
            location = args[0];
            if (this.loadingFile != null && this.loadingFile.equals(location)) {
                this.loadingFileObject = null;
                this.loadingFile = null;
                this.loadingSize = null;
                this.progressView.setVisibility(8);
                this.doneButton.setEnabled(false);
            }
        } else if (id == NotificationCenter.FileDidLoaded) {
            location = (String) args[0];
            if (this.loadingFile != null && this.loadingFile.equals(location)) {
                this.backgroundImage.setImageURI(Uri.fromFile(this.loadingFileObject));
                this.progressView.setVisibility(8);
                this.backgroundImage.setBackgroundColor(0);
                this.doneButton.setEnabled(true);
                this.loadingFileObject = null;
                this.loadingFile = null;
                this.loadingSize = null;
            }
        } else if (id == NotificationCenter.wallpapersDidLoaded) {
            this.wallPapers = (ArrayList) args[0];
            this.wallpappersByIds.clear();
            Iterator it = this.wallPapers.iterator();
            while (it.hasNext()) {
                WallPaper wallPaper = (WallPaper) it.next();
                this.wallpappersByIds.put(Integer.valueOf(wallPaper.id), wallPaper);
            }
            if (this.listAdapter != null) {
                this.listAdapter.notifyDataSetChanged();
            }
            if (!(this.wallPapers.isEmpty() || this.backgroundImage == null)) {
                processSelectedBackground();
            }
            loadWallpapers();
        }
    }

    private void loadWallpapers() {
        ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(new TL_account_getWallPapers(), new RequestDelegate() {
            public void run(final TLObject response, TL_error error) {
                if (error == null) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            WallpapersActivity.this.wallPapers.clear();
                            Vector res = response;
                            WallpapersActivity.this.wallpappersByIds.clear();
                            Iterator it = res.objects.iterator();
                            while (it.hasNext()) {
                                Object obj = it.next();
                                WallpapersActivity.this.wallPapers.add((WallPaper) obj);
                                WallpapersActivity.this.wallpappersByIds.put(Integer.valueOf(((WallPaper) obj).id), (WallPaper) obj);
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
            }
        }), this.classGuid);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        processSelectedBackground();
    }
}
