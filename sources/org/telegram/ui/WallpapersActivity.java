package org.telegram.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
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
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.WallpaperCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.WallpaperUpdater;
import org.telegram.ui.Components.WallpaperUpdater.WallpaperUpdaterDelegate;

public class WallpapersActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private ImageView backgroundImage;
    private View doneButton;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private String loadingFile = null;
    private File loadingFileObject = null;
    private PhotoSize loadingSize = null;
    private boolean overrideThemeWallpaper;
    private FrameLayout progressView;
    private View progressViewBackground;
    private int selectedBackground;
    private int selectedColor;
    private Drawable themedWallpaper;
    private WallpaperUpdater updater;
    private ArrayList<WallPaper> wallPapers = new ArrayList();
    private File wallpaperFile;
    private SparseArray<WallPaper> wallpappersByIds = new SparseArray();

    /* renamed from: org.telegram.ui.WallpapersActivity$3 */
    class C17703 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C17703() {
        }
    }

    /* renamed from: org.telegram.ui.WallpapersActivity$1 */
    class C23131 implements WallpaperUpdaterDelegate {
        public void needOpenColorPicker() {
        }

        C23131() {
        }

        public void didSelectWallpaper(File file, Bitmap bitmap) {
            WallpapersActivity.this.selectedBackground = -1;
            WallpapersActivity.this.overrideThemeWallpaper = true;
            WallpapersActivity.this.selectedColor = 0;
            WallpapersActivity.this.wallpaperFile = file;
            WallpapersActivity.this.backgroundImage.getDrawable();
            WallpapersActivity.this.backgroundImage.setImageBitmap(bitmap);
        }
    }

    /* renamed from: org.telegram.ui.WallpapersActivity$2 */
    class C23142 extends ActionBarMenuOnItemClick {
        C23142() {
        }

        public void onItemClick(int i) {
            String str;
            if (i == -1) {
                WallpapersActivity.this.finishFragment();
                return;
            }
            boolean z = true;
            if (i == 1) {
                WallPaper wallPaper = (WallPaper) WallpapersActivity.this.wallpappersByIds.get(WallpapersActivity.this.selectedBackground);
                if (wallPaper != null && wallPaper.id != 1000001 && (wallPaper instanceof TL_wallPaper)) {
                    int i2 = AndroidUtilities.displaySize.x;
                    int i3 = AndroidUtilities.displaySize.y;
                    if (i2 > i3) {
                        int i4 = i3;
                        i3 = i2;
                        i2 = i4;
                    }
                    i = FileLoader.getClosestPhotoSizeWithSize(wallPaper.sizes, Math.min(i2, i3));
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(i.location.volume_id);
                    stringBuilder.append("_");
                    stringBuilder.append(i.location.local_id);
                    stringBuilder.append(".jpg");
                    try {
                        i = AndroidUtilities.copyFile(new File(FileLoader.getDirectory(4), stringBuilder.toString()), new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg"));
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                        i = 0;
                        if (i != 0) {
                            i = MessagesController.getGlobalMainSettings().edit();
                            i.putInt("selectedBackground", WallpapersActivity.this.selectedBackground);
                            i.putInt("selectedColor", WallpapersActivity.this.selectedColor);
                            str = "overrideThemeWallpaper";
                            if (Theme.hasWallpaperFromTheme()) {
                            }
                            z = false;
                            i.putBoolean(str, z);
                            i.commit();
                            Theme.reloadWallpaper();
                        }
                        WallpapersActivity.this.finishFragment();
                    }
                } else if (WallpapersActivity.this.selectedBackground == -1) {
                    try {
                        i = AndroidUtilities.copyFile(WallpapersActivity.this.updater.getCurrentWallpaperPath(), new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg"));
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                        i = 0;
                        if (i != 0) {
                            i = MessagesController.getGlobalMainSettings().edit();
                            i.putInt("selectedBackground", WallpapersActivity.this.selectedBackground);
                            i.putInt("selectedColor", WallpapersActivity.this.selectedColor);
                            str = "overrideThemeWallpaper";
                            if (Theme.hasWallpaperFromTheme()) {
                            }
                            z = false;
                            i.putBoolean(str, z);
                            i.commit();
                            Theme.reloadWallpaper();
                        }
                        WallpapersActivity.this.finishFragment();
                    }
                } else {
                    i = 1;
                }
                if (i != 0) {
                    i = MessagesController.getGlobalMainSettings().edit();
                    i.putInt("selectedBackground", WallpapersActivity.this.selectedBackground);
                    i.putInt("selectedColor", WallpapersActivity.this.selectedColor);
                    str = "overrideThemeWallpaper";
                    if (Theme.hasWallpaperFromTheme() || !WallpapersActivity.this.overrideThemeWallpaper) {
                        z = false;
                    }
                    i.putBoolean(str, z);
                    i.commit();
                    Theme.reloadWallpaper();
                }
                WallpapersActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.WallpapersActivity$4 */
    class C23154 implements OnItemClickListener {
        C23154() {
        }

        public void onItemClick(View view, int i) {
            if (i == 0) {
                WallpapersActivity.this.updater.showAlert(false);
            } else {
                if (!Theme.hasWallpaperFromTheme()) {
                    i--;
                } else if (i == 1) {
                    WallpapersActivity.this.selectedBackground = -2;
                    WallpapersActivity.this.overrideThemeWallpaper = false;
                    WallpapersActivity.this.listAdapter.notifyDataSetChanged();
                    WallpapersActivity.this.processSelectedBackground();
                    return;
                } else {
                    i -= 2;
                }
                WallpapersActivity.this.selectedBackground = ((WallPaper) WallpapersActivity.this.wallPapers.get(i)).id;
                WallpapersActivity.this.overrideThemeWallpaper = true;
                WallpapersActivity.this.listAdapter.notifyDataSetChanged();
                WallpapersActivity.this.processSelectedBackground();
            }
        }
    }

    /* renamed from: org.telegram.ui.WallpapersActivity$5 */
    class C23165 implements RequestDelegate {
        C23165() {
        }

        public void run(final TLObject tLObject, TL_error tL_error) {
            if (tL_error == null) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        WallpapersActivity.this.wallPapers.clear();
                        Vector vector = (Vector) tLObject;
                        WallpapersActivity.this.wallpappersByIds.clear();
                        Iterator it = vector.objects.iterator();
                        while (it.hasNext()) {
                            WallPaper wallPaper = (WallPaper) it.next();
                            WallpapersActivity.this.wallPapers.add(wallPaper);
                            WallpapersActivity.this.wallpappersByIds.put(wallPaper.id, wallPaper);
                        }
                        if (WallpapersActivity.this.listAdapter != null) {
                            WallpapersActivity.this.listAdapter.notifyDataSetChanged();
                        }
                        if (WallpapersActivity.this.backgroundImage != null) {
                            WallpapersActivity.this.processSelectedBackground();
                        }
                        MessagesStorage.getInstance(WallpapersActivity.this.currentAccount).putWallpapers(WallpapersActivity.this.wallPapers);
                    }
                });
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            int size = 1 + WallpapersActivity.this.wallPapers.size();
            return Theme.hasWallpaperFromTheme() ? size + 1 : size;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new Holder(new WallpaperCell(this.mContext));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            WallpaperCell wallpaperCell = (WallpaperCell) viewHolder.itemView;
            int i2 = -2;
            if (i == 0) {
                if (Theme.hasWallpaperFromTheme() == 0 || WallpapersActivity.this.overrideThemeWallpaper != 0) {
                    i2 = WallpapersActivity.this.selectedBackground;
                }
                wallpaperCell.setWallpaper(null, i2, null, false);
            } else {
                if (!Theme.hasWallpaperFromTheme()) {
                    i--;
                } else if (i == 1) {
                    if (WallpapersActivity.this.overrideThemeWallpaper != 0) {
                        i2 = -1;
                    }
                    wallpaperCell.setWallpaper(null, i2, WallpapersActivity.this.themedWallpaper, true);
                    return;
                } else {
                    i -= 2;
                }
                WallPaper wallPaper = (WallPaper) WallpapersActivity.this.wallPapers.get(i);
                if (!Theme.hasWallpaperFromTheme() || WallpapersActivity.this.overrideThemeWallpaper) {
                    i2 = WallpapersActivity.this.selectedBackground;
                }
                wallpaperCell.setWallpaper(wallPaper, i2, null, false);
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidFailedLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoaded);
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        this.selectedBackground = globalMainSettings.getInt("selectedBackground", 1000001);
        this.overrideThemeWallpaper = globalMainSettings.getBoolean("overrideThemeWallpaper", false);
        this.selectedColor = globalMainSettings.getInt("selectedColor", 0);
        MessagesStorage.getInstance(this.currentAccount).getWallpapers();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.updater.cleanup();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailedLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoaded);
    }

    public View createView(Context context) {
        this.themedWallpaper = Theme.getThemedWallpaper(true);
        this.updater = new WallpaperUpdater(getParentActivity(), new C23131());
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ChatBackground", C0446R.string.ChatBackground));
        this.actionBar.setActionBarMenuOnItemClick(new C23142());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        View frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        this.backgroundImage = new ImageView(context);
        this.backgroundImage.setScaleType(ScaleType.CENTER_CROP);
        frameLayout.addView(this.backgroundImage, LayoutHelper.createFrame(-1, -1.0f));
        this.backgroundImage.setOnTouchListener(new C17703());
        this.progressView = new FrameLayout(context);
        this.progressView.setVisibility(4);
        frameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 52.0f));
        this.progressViewBackground = new View(context);
        this.progressViewBackground.setBackgroundResource(C0446R.drawable.system_loader);
        this.progressView.addView(this.progressViewBackground, LayoutHelper.createFrame(36, 36, 17));
        View radialProgressView = new RadialProgressView(context);
        radialProgressView.setSize(AndroidUtilities.dp(28.0f));
        radialProgressView.setProgressColor(-1);
        this.progressView.addView(radialProgressView, LayoutHelper.createFrame(32, 32, 17));
        this.listView = new RecyclerListView(context);
        this.listView.setClipToPadding(false);
        this.listView.setTag(Integer.valueOf(8));
        this.listView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        LayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(0);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setDisallowInterceptTouchEvents(true);
        this.listView.setOverScrollMode(2);
        RecyclerListView recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, 102, 83));
        this.listView.setOnItemClickListener(new C23154());
        processSelectedBackground();
        return this.fragmentView;
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        this.updater.onActivityResult(i, i2, intent);
    }

    public void saveSelfArgs(Bundle bundle) {
        String currentPicturePath = this.updater.getCurrentPicturePath();
        if (currentPicturePath != null) {
            bundle.putString("path", currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
        this.updater.setCurrentPicturePath(bundle.getString("path"));
    }

    private void processSelectedBackground() {
        if (!Theme.hasWallpaperFromTheme() || this.overrideThemeWallpaper) {
            WallPaper wallPaper = (WallPaper) this.wallpappersByIds.get(this.selectedBackground);
            if (this.selectedBackground == -1 || this.selectedBackground == 1000001 || wallPaper == null || !(wallPaper instanceof TL_wallPaper)) {
                if (this.loadingFile != null) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.loadingSize);
                }
                if (this.selectedBackground == 1000001) {
                    this.backgroundImage.setImageResource(C0446R.drawable.background_hd);
                    this.backgroundImage.setBackgroundColor(0);
                    this.selectedColor = 0;
                } else if (this.selectedBackground == -1) {
                    File file;
                    if (this.wallpaperFile != null) {
                        file = this.wallpaperFile;
                    } else {
                        file = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg");
                    }
                    if (file.exists()) {
                        this.backgroundImage.setImageURI(Uri.fromFile(file));
                    } else {
                        this.selectedBackground = 1000001;
                        this.overrideThemeWallpaper = true;
                        processSelectedBackground();
                    }
                } else if (wallPaper != null) {
                    if (wallPaper instanceof TL_wallPaperSolid) {
                        this.backgroundImage.getDrawable();
                        this.backgroundImage.setImageBitmap(null);
                        this.selectedColor = wallPaper.bg_color | Theme.ACTION_BAR_VIDEO_EDIT_COLOR;
                        this.backgroundImage.setBackgroundColor(this.selectedColor);
                    }
                } else {
                    return;
                }
                this.loadingFileObject = null;
                this.loadingFile = null;
                this.loadingSize = null;
                this.doneButton.setEnabled(true);
                this.progressView.setVisibility(8);
            } else {
                int i = AndroidUtilities.displaySize.x;
                int i2 = AndroidUtilities.displaySize.y;
                if (i > i2) {
                    int i3 = i2;
                    i2 = i;
                    i = i3;
                }
                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(wallPaper.sizes, Math.min(i, i2));
                if (closestPhotoSizeWithSize != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(closestPhotoSizeWithSize.location.volume_id);
                    stringBuilder.append("_");
                    stringBuilder.append(closestPhotoSizeWithSize.location.local_id);
                    stringBuilder.append(".jpg");
                    String stringBuilder2 = stringBuilder.toString();
                    File file2 = new File(FileLoader.getDirectory(4), stringBuilder2);
                    if (file2.exists()) {
                        if (this.loadingFile != null) {
                            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.loadingSize);
                        }
                        this.loadingFileObject = null;
                        this.loadingFile = null;
                        this.loadingSize = null;
                        try {
                            this.backgroundImage.setImageURI(Uri.fromFile(file2));
                        } catch (Throwable th) {
                            FileLog.m3e(th);
                        }
                        this.backgroundImage.setBackgroundColor(0);
                        this.selectedColor = 0;
                        this.doneButton.setEnabled(true);
                        this.progressView.setVisibility(8);
                    } else {
                        this.progressViewBackground.getBackground().setColorFilter(new PorterDuffColorFilter(AndroidUtilities.calcDrawableColor(this.backgroundImage.getDrawable())[0], Mode.MULTIPLY));
                        this.loadingFile = stringBuilder2;
                        this.loadingFileObject = file2;
                        this.doneButton.setEnabled(false);
                        this.progressView.setVisibility(0);
                        this.loadingSize = closestPhotoSizeWithSize;
                        this.selectedColor = 0;
                        FileLoader.getInstance(this.currentAccount).loadFile(closestPhotoSizeWithSize, null, 1);
                        this.backgroundImage.setBackgroundColor(0);
                    }
                } else {
                    return;
                }
            }
        }
        this.backgroundImage.setImageDrawable(Theme.getThemedWallpaper(false));
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        if (i == NotificationCenter.FileDidFailedLoad) {
            str = (String) objArr[0];
            if (this.loadingFile != 0 && this.loadingFile.equals(str) != 0) {
                this.loadingFileObject = null;
                this.loadingFile = null;
                this.loadingSize = null;
                this.progressView.setVisibility(8);
                this.doneButton.setEnabled(false);
            }
        } else if (i == NotificationCenter.FileDidLoaded) {
            str = (String) objArr[0];
            if (this.loadingFile != 0 && this.loadingFile.equals(str) != 0) {
                this.backgroundImage.setImageURI(Uri.fromFile(this.loadingFileObject));
                this.progressView.setVisibility(8);
                this.backgroundImage.setBackgroundColor(0);
                this.doneButton.setEnabled(1);
                this.loadingFileObject = null;
                this.loadingFile = null;
                this.loadingSize = null;
            }
        } else if (i == NotificationCenter.wallpapersDidLoaded) {
            this.wallPapers = (ArrayList) objArr[0];
            this.wallpappersByIds.clear();
            i = this.wallPapers.iterator();
            while (i.hasNext() != 0) {
                WallPaper wallPaper = (WallPaper) i.next();
                this.wallpappersByIds.put(wallPaper.id, wallPaper);
            }
            if (this.listAdapter != 0) {
                this.listAdapter.notifyDataSetChanged();
            }
            if (this.wallPapers.isEmpty() == 0 && this.backgroundImage != 0) {
                processSelectedBackground();
            }
            loadWallpapers();
        }
    }

    private void loadWallpapers() {
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getWallPapers(), new C23165()), this.classGuid);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        processSelectedBackground();
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector)};
    }
}
