package org.telegram.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.LongSparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.WallpaperCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.WallpaperUpdater;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.WallpapersListActivity;

public class WallpapersListActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final int TYPE_ALL = 0;
    public static final int TYPE_COLOR = 1;
    private static final int[] defaultColors = {-1, -2826262, -4993567, -9783318, -16740912, -2891046, -3610935, -3808859, -10375058, -3289169, -5789547, -8622222, -10322, -18835, -2193583, -1059360, -2383431, -20561, -955808, -1524502, -6974739, -2507680, -5145015, -2765065, -2142101, -7613748, -12811138, -14524116, -14398084, -12764283, -10129027, -15195603, -16777216};
    private static final int delete = 4;
    private static final int forward = 3;
    /* access modifiers changed from: private */
    public static final int[] searchColors = {-16746753, -65536, -30208, -13824, -16718798, -14702165, -9240406, -409915, -9224159, -16777216, -10725281, -1};
    /* access modifiers changed from: private */
    public static final String[] searchColorsNames = {"Blue", "Red", "Orange", "Yellow", "Green", "Teal", "Purple", "Pink", "Brown", "Black", "Gray", "White"};
    /* access modifiers changed from: private */
    public static final int[] searchColorsNamesR = {NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM};
    private ArrayList<View> actionModeViews = new ArrayList<>();
    private ColorWallpaper addedColorWallpaper;
    private FileWallpaper addedFileWallpaper;
    private ArrayList<Object> allWallPapers = new ArrayList<>();
    private HashMap<String, Object> allWallPapersDict = new HashMap<>();
    private FileWallpaper catsWallpaper;
    /* access modifiers changed from: private */
    public Paint colorFramePaint;
    /* access modifiers changed from: private */
    public Paint colorPaint;
    /* access modifiers changed from: private */
    public int columnsCount = 3;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private boolean loadingWallpapers;
    private ArrayList<Object> patterns = new ArrayList<>();
    /* access modifiers changed from: private */
    public AlertDialog progressDialog;
    /* access modifiers changed from: private */
    public int resetInfoRow;
    /* access modifiers changed from: private */
    public int resetRow;
    /* access modifiers changed from: private */
    public int resetSectionRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public boolean scrolling;
    /* access modifiers changed from: private */
    public SearchAdapter searchAdapter;
    /* access modifiers changed from: private */
    public EmptyTextProgressView searchEmptyView;
    /* access modifiers changed from: private */
    public ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public int sectionRow;
    private boolean selectedBackgroundBlurred;
    private boolean selectedBackgroundMotion;
    /* access modifiers changed from: private */
    public String selectedBackgroundSlug = "";
    /* access modifiers changed from: private */
    public int selectedColor;
    /* access modifiers changed from: private */
    public int selectedGradientColor;
    /* access modifiers changed from: private */
    public int selectedGradientRotation;
    /* access modifiers changed from: private */
    public float selectedIntensity;
    private NumberTextView selectedMessagesCountTextView;
    /* access modifiers changed from: private */
    public LongSparseArray<Object> selectedWallPapers = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public int setColorRow;
    private FileWallpaper themeWallpaper;
    /* access modifiers changed from: private */
    public int totalWallpaperRows;
    private WallpaperUpdater updater;
    /* access modifiers changed from: private */
    public int uploadImageRow;
    /* access modifiers changed from: private */
    public int wallPaperStartRow;
    /* access modifiers changed from: private */
    public ArrayList<Object> wallPapers = new ArrayList<>();

    static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public static class ColorWallpaper {
        public int color;
        public int gradientColor;
        public int gradientRotation;
        public float intensity;
        public boolean motion;
        public File path;
        public TLRPC.TL_wallPaper pattern;
        public long patternId;
        public String slug;

        public ColorWallpaper(String str, int i, int i2, int i3) {
            this.slug = str;
            this.color = i | -16777216;
            this.gradientColor = i2 == 0 ? 0 : -16777216 | i2;
            this.gradientRotation = this.gradientColor == 0 ? 45 : i3;
            this.intensity = 1.0f;
        }

        public ColorWallpaper(String str, int i, int i2, int i3, float f, boolean z, File file) {
            this.slug = str;
            this.color = i | -16777216;
            this.gradientColor = i2 == 0 ? 0 : -16777216 | i2;
            this.gradientRotation = this.gradientColor == 0 ? 45 : i3;
            this.intensity = f;
            this.path = file;
            this.motion = z;
        }

        public String getUrl() {
            String str;
            int i = this.gradientColor;
            if (i != 0) {
                str = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (i >> 16)) & 255), Integer.valueOf(((byte) (this.gradientColor >> 8)) & 255), Byte.valueOf((byte) (this.gradientColor & 255))}).toLowerCase();
            } else {
                str = null;
            }
            String lowerCase = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (this.color >> 16)) & 255), Integer.valueOf(((byte) (this.color >> 8)) & 255), Byte.valueOf((byte) (this.color & 255))}).toLowerCase();
            if (str != null) {
                String str2 = lowerCase + "-" + str;
                if (this.pattern != null) {
                    lowerCase = str2 + "&rotation=" + AndroidUtilities.getWallpaperRotation(this.gradientRotation, true);
                } else {
                    lowerCase = str2 + "?rotation=" + AndroidUtilities.getWallpaperRotation(this.gradientRotation, true);
                }
            }
            if (this.pattern != null) {
                String str3 = "https://" + MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix + "/bg/" + this.pattern.slug + "?intensity=" + ((int) (this.intensity * 100.0f)) + "&bg_color=" + lowerCase;
                if (!this.motion) {
                    return str3;
                }
                return str3 + "&mode=motion";
            }
            return "https://" + MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix + "/bg/" + lowerCase;
        }
    }

    public static class FileWallpaper {
        public File originalPath;
        public File path;
        public int resId;
        public String slug;
        public int thumbResId;

        public FileWallpaper(String str, File file, File file2) {
            this.slug = str;
            this.path = file;
            this.originalPath = file2;
        }

        public FileWallpaper(String str, String str2) {
            this.slug = str;
            this.path = new File(str2);
        }

        public FileWallpaper(String str, int i, int i2) {
            this.slug = str;
            this.resId = i;
            this.thumbResId = i2;
        }
    }

    public WallpapersListActivity(int i) {
        this.currentType = i;
    }

    public boolean onFragmentCreate() {
        if (this.currentType == 0) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersNeedReload);
            MessagesStorage.getInstance(this.currentAccount).getWallpapers();
        } else {
            int i = 0;
            while (true) {
                int[] iArr = defaultColors;
                if (i >= iArr.length) {
                    break;
                }
                this.wallPapers.add(new ColorWallpaper("c", iArr[i], 0, 45));
                i++;
            }
            if (this.currentType == 1 && this.patterns.isEmpty()) {
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
                MessagesStorage.getInstance(this.currentAccount).getWallpapers();
            }
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        int i = this.currentType;
        if (i == 0) {
            this.searchAdapter.onDestroy();
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersNeedReload);
        } else if (i == 1) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
        }
        this.updater.cleanup();
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        this.colorPaint = new Paint(1);
        this.colorFramePaint = new Paint(1);
        this.colorFramePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        this.colorFramePaint.setStyle(Paint.Style.STROKE);
        this.colorFramePaint.setColor(NUM);
        this.updater = new WallpaperUpdater(getParentActivity(), this, new WallpaperUpdater.WallpaperUpdaterDelegate() {
            public void needOpenColorPicker() {
            }

            public void didSelectWallpaper(File file, Bitmap bitmap, boolean z) {
                WallpapersListActivity.this.presentFragment(new ThemePreviewActivity(new FileWallpaper("", file, file), bitmap), z);
            }
        });
        this.hasOwnBackground = true;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        int i = this.currentType;
        if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChatBackground", NUM));
        } else if (i == 1) {
            this.actionBar.setTitle(LocaleController.getString("SelectColorTitle", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (WallpapersListActivity.this.actionBar.isActionModeShowed()) {
                        WallpapersListActivity.this.selectedWallPapers.clear();
                        WallpapersListActivity.this.actionBar.hideActionMode();
                        WallpapersListActivity.this.updateRowsSelection();
                        return;
                    }
                    WallpapersListActivity.this.finishFragment();
                } else if (i == 4) {
                    if (WallpapersListActivity.this.getParentActivity() != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) WallpapersListActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.formatPluralString("DeleteBackground", WallpapersListActivity.this.selectedWallPapers.size()));
                        builder.setMessage(LocaleController.formatString("DeleteChatBackgroundsAlert", NUM, new Object[0]));
                        builder.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                WallpapersListActivity.AnonymousClass2.this.lambda$onItemClick$2$WallpapersListActivity$2(dialogInterface, i);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        AlertDialog create = builder.create();
                        WallpapersListActivity.this.showDialog(create);
                        TextView textView = (TextView) create.getButton(-1);
                        if (textView != null) {
                            textView.setTextColor(Theme.getColor("dialogTextRed2"));
                        }
                    }
                } else if (i == 3) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("onlySelect", true);
                    bundle.putInt("dialogsType", 3);
                    DialogsActivity dialogsActivity = new DialogsActivity(bundle);
                    dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
                        public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                            WallpapersListActivity.AnonymousClass2.this.lambda$onItemClick$3$WallpapersListActivity$2(dialogsActivity, arrayList, charSequence, z);
                        }
                    });
                    WallpapersListActivity.this.presentFragment(dialogsActivity);
                }
            }

            public /* synthetic */ void lambda$onItemClick$2$WallpapersListActivity$2(DialogInterface dialogInterface, int i) {
                WallpapersListActivity wallpapersListActivity = WallpapersListActivity.this;
                AlertDialog unused = wallpapersListActivity.progressDialog = new AlertDialog(wallpapersListActivity.getParentActivity(), 3);
                WallpapersListActivity.this.progressDialog.setCanCacnel(false);
                WallpapersListActivity.this.progressDialog.show();
                new ArrayList();
                int[] iArr = {WallpapersListActivity.this.selectedWallPapers.size()};
                for (int i2 = 0; i2 < WallpapersListActivity.this.selectedWallPapers.size(); i2++) {
                    TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) WallpapersListActivity.this.selectedWallPapers.valueAt(i2);
                    TLRPC.TL_account_saveWallPaper tL_account_saveWallPaper = new TLRPC.TL_account_saveWallPaper();
                    tL_account_saveWallPaper.settings = new TLRPC.TL_wallPaperSettings();
                    tL_account_saveWallPaper.unsave = true;
                    TLRPC.TL_inputWallPaper tL_inputWallPaper = new TLRPC.TL_inputWallPaper();
                    tL_inputWallPaper.id = tL_wallPaper.id;
                    tL_inputWallPaper.access_hash = tL_wallPaper.access_hash;
                    tL_account_saveWallPaper.wallpaper = tL_inputWallPaper;
                    if (tL_wallPaper.slug.equals(WallpapersListActivity.this.selectedBackgroundSlug)) {
                        String unused2 = WallpapersListActivity.this.selectedBackgroundSlug = Theme.hasWallpaperFromTheme() ? "t" : "d";
                        Theme.getActiveTheme().setOverrideWallpaper((Theme.OverrideWallpaperInfo) null);
                        Theme.reloadWallpaper();
                    }
                    ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(tL_account_saveWallPaper, new RequestDelegate(iArr) {
                        private final /* synthetic */ int[] f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            WallpapersListActivity.AnonymousClass2.this.lambda$null$1$WallpapersListActivity$2(this.f$1, tLObject, tL_error);
                        }
                    });
                }
                WallpapersListActivity.this.selectedWallPapers.clear();
                WallpapersListActivity.this.actionBar.hideActionMode();
                WallpapersListActivity.this.actionBar.closeSearchField();
            }

            public /* synthetic */ void lambda$null$1$WallpapersListActivity$2(int[] iArr, TLObject tLObject, TLRPC.TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable(iArr) {
                    private final /* synthetic */ int[] f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        WallpapersListActivity.AnonymousClass2.this.lambda$null$0$WallpapersListActivity$2(this.f$1);
                    }
                });
            }

            public /* synthetic */ void lambda$null$0$WallpapersListActivity$2(int[] iArr) {
                iArr[0] = iArr[0] - 1;
                if (iArr[0] == 0) {
                    WallpapersListActivity.this.loadWallpapers();
                }
            }

            public /* synthetic */ void lambda$onItemClick$3$WallpapersListActivity$2(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                ArrayList arrayList2 = arrayList;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < WallpapersListActivity.this.selectedWallPapers.size(); i++) {
                    String wallPaperUrl = AndroidUtilities.getWallPaperUrl((TLRPC.TL_wallPaper) WallpapersListActivity.this.selectedWallPapers.valueAt(i));
                    if (!TextUtils.isEmpty(wallPaperUrl)) {
                        if (sb.length() > 0) {
                            sb.append(10);
                        }
                        sb.append(wallPaperUrl);
                    }
                }
                WallpapersListActivity.this.selectedWallPapers.clear();
                WallpapersListActivity.this.actionBar.hideActionMode();
                WallpapersListActivity.this.actionBar.closeSearchField();
                if (arrayList.size() > 1 || ((Long) arrayList2.get(0)).longValue() == ((long) UserConfig.getInstance(WallpapersListActivity.this.currentAccount).getClientUserId()) || charSequence != null) {
                    DialogsActivity dialogsActivity2 = dialogsActivity;
                    WallpapersListActivity.this.updateRowsSelection();
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        long longValue = ((Long) arrayList2.get(i2)).longValue();
                        if (charSequence != null) {
                            SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(charSequence.toString(), longValue, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                        }
                        SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(sb.toString(), longValue, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                    }
                    dialogsActivity.finishFragment();
                    return;
                }
                long longValue2 = ((Long) arrayList2.get(0)).longValue();
                int i3 = (int) longValue2;
                int i4 = (int) (longValue2 >> 32);
                Bundle bundle = new Bundle();
                bundle.putBoolean("scrollToTopOnResume", true);
                if (i3 == 0) {
                    bundle.putInt("enc_id", i4);
                } else if (i3 > 0) {
                    bundle.putInt("user_id", i3);
                } else if (i3 < 0) {
                    bundle.putInt("chat_id", -i3);
                }
                if (i3 == 0 || MessagesController.getInstance(WallpapersListActivity.this.currentAccount).checkCanOpenChat(bundle, dialogsActivity)) {
                    NotificationCenter.getInstance(WallpapersListActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    WallpapersListActivity.this.presentFragment(new ChatActivity(bundle), true);
                    SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(sb.toString(), longValue2, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                }
            }
        });
        if (this.currentType == 0) {
            this.searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
                public void onSearchExpand() {
                    WallpapersListActivity.this.listView.setAdapter(WallpapersListActivity.this.searchAdapter);
                    WallpapersListActivity.this.listView.invalidate();
                }

                public void onSearchCollapse() {
                    WallpapersListActivity.this.listView.setAdapter(WallpapersListActivity.this.listAdapter);
                    WallpapersListActivity.this.listView.invalidate();
                    WallpapersListActivity.this.searchAdapter.processSearch((String) null, true);
                    WallpapersListActivity.this.searchItem.setSearchFieldCaption((CharSequence) null);
                    onCaptionCleared();
                }

                public void onTextChanged(EditText editText) {
                    WallpapersListActivity.this.searchAdapter.processSearch(editText.getText().toString(), false);
                }

                public void onCaptionCleared() {
                    WallpapersListActivity.this.searchAdapter.clearColor();
                    WallpapersListActivity.this.searchItem.setSearchFieldHint(LocaleController.getString("SearchBackgrounds", NUM));
                }
            });
            this.searchItem.setSearchFieldHint(LocaleController.getString("SearchBackgrounds", NUM));
            ActionBarMenu createActionMode = this.actionBar.createActionMode(false);
            createActionMode.setBackgroundColor(Theme.getColor("actionBarDefault"));
            this.actionBar.setItemsColor(Theme.getColor("actionBarDefaultIcon"), true);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarDefaultSelector"), true);
            this.selectedMessagesCountTextView = new NumberTextView(createActionMode.getContext());
            this.selectedMessagesCountTextView.setTextSize(18);
            this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.selectedMessagesCountTextView.setTextColor(Theme.getColor("actionBarDefaultIcon"));
            this.selectedMessagesCountTextView.setOnTouchListener($$Lambda$WallpapersListActivity$kDCUFe0ixQyVZvdd5on4Sg4XaNc.INSTANCE);
            createActionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 65, 0, 0, 0));
            this.actionModeViews.add(createActionMode.addItemWithWidth(3, NUM, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(createActionMode.addItemWithWidth(4, NUM, AndroidUtilities.dp(54.0f)));
            this.selectedWallPapers.clear();
        }
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context) {
            private Paint paint = new Paint();

            public boolean hasOverlappingRendering() {
                return false;
            }

            /* JADX WARNING: Code restructure failed: missing block: B:9:0x0033, code lost:
                if (r0.itemView.getBottom() >= r1) goto L_0x0035;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onDraw(android.graphics.Canvas r15) {
                /*
                    r14 = this;
                    androidx.recyclerview.widget.RecyclerView$Adapter r0 = r14.getAdapter()
                    org.telegram.ui.WallpapersListActivity r1 = org.telegram.ui.WallpapersListActivity.this
                    org.telegram.ui.WallpapersListActivity$ListAdapter r1 = r1.listAdapter
                    if (r0 != r1) goto L_0x0020
                    org.telegram.ui.WallpapersListActivity r0 = org.telegram.ui.WallpapersListActivity.this
                    int r0 = r0.resetInfoRow
                    r1 = -1
                    if (r0 == r1) goto L_0x0020
                    org.telegram.ui.WallpapersListActivity r0 = org.telegram.ui.WallpapersListActivity.this
                    int r0 = r0.resetInfoRow
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r14.findViewHolderForAdapterPosition(r0)
                    goto L_0x0021
                L_0x0020:
                    r0 = 0
                L_0x0021:
                    int r1 = r14.getMeasuredHeight()
                    if (r0 == 0) goto L_0x0035
                    android.view.View r2 = r0.itemView
                    int r2 = r2.getBottom()
                    android.view.View r0 = r0.itemView
                    int r0 = r0.getBottom()
                    if (r0 < r1) goto L_0x0036
                L_0x0035:
                    r2 = r1
                L_0x0036:
                    android.graphics.Paint r0 = r14.paint
                    java.lang.String r3 = "windowBackgroundWhite"
                    int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                    r0.setColor(r3)
                    r5 = 0
                    r6 = 0
                    int r0 = r14.getMeasuredWidth()
                    float r7 = (float) r0
                    float r10 = (float) r2
                    android.graphics.Paint r9 = r14.paint
                    r4 = r15
                    r8 = r10
                    r4.drawRect(r5, r6, r7, r8, r9)
                    if (r2 == r1) goto L_0x006c
                    android.graphics.Paint r0 = r14.paint
                    java.lang.String r2 = "windowBackgroundGray"
                    int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                    r0.setColor(r2)
                    r9 = 0
                    int r0 = r14.getMeasuredWidth()
                    float r11 = (float) r0
                    float r12 = (float) r1
                    android.graphics.Paint r13 = r14.paint
                    r8 = r15
                    r8.drawRect(r9, r10, r11, r12, r13)
                L_0x006c:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpapersListActivity.AnonymousClass4.onDraw(android.graphics.Canvas):void");
            }
        };
        this.listView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass5 r4 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r4;
        recyclerListView.setLayoutManager(r4);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context);
        this.listAdapter = listAdapter2;
        recyclerListView2.setAdapter(listAdapter2);
        this.searchAdapter = new SearchAdapter(context);
        this.listView.setGlowColor(Theme.getColor("avatar_backgroundActionBarBlue"));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                WallpapersListActivity.this.lambda$createView$3$WallpapersListActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                boolean z = true;
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(WallpapersListActivity.this.getParentActivity().getCurrentFocus());
                }
                WallpapersListActivity wallpapersListActivity = WallpapersListActivity.this;
                if (i == 0) {
                    z = false;
                }
                boolean unused = wallpapersListActivity.scrolling = z;
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                int i3;
                if (WallpapersListActivity.this.listView.getAdapter() == WallpapersListActivity.this.searchAdapter) {
                    int findFirstVisibleItemPosition = WallpapersListActivity.this.layoutManager.findFirstVisibleItemPosition();
                    if (findFirstVisibleItemPosition == -1) {
                        i3 = 0;
                    } else {
                        i3 = Math.abs(WallpapersListActivity.this.layoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
                    }
                    if (i3 > 0) {
                        int itemCount = WallpapersListActivity.this.layoutManager.getItemCount();
                        if (i3 != 0 && findFirstVisibleItemPosition + i3 > itemCount - 2) {
                            WallpapersListActivity.this.searchAdapter.loadMoreResults();
                        }
                    }
                }
            }
        });
        this.searchEmptyView = new EmptyTextProgressView(context);
        this.searchEmptyView.setVisibility(8);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.searchEmptyView.setText(LocaleController.getString("NoResult", NUM));
        this.listView.setEmptyView(this.searchEmptyView);
        frameLayout.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f));
        updateRows();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$3$WallpapersListActivity(View view, int i) {
        if (getParentActivity() != null && this.listView.getAdapter() != this.searchAdapter) {
            if (i == this.uploadImageRow) {
                this.updater.openGallery();
            } else if (i == this.setColorRow) {
                WallpapersListActivity wallpapersListActivity = new WallpapersListActivity(1);
                wallpapersListActivity.patterns = this.patterns;
                presentFragment(wallpapersListActivity);
            } else if (i == this.resetRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("ResetChatBackgroundsAlertTitle", NUM));
                builder.setMessage(LocaleController.getString("ResetChatBackgroundsAlert", NUM));
                builder.setPositiveButton(LocaleController.getString("Reset", NUM), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        WallpapersListActivity.this.lambda$null$2$WallpapersListActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog create = builder.create();
                showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$2$WallpapersListActivity(DialogInterface dialogInterface, int i) {
        if (this.actionBar.isActionModeShowed()) {
            this.selectedWallPapers.clear();
            this.actionBar.hideActionMode();
            updateRowsSelection();
        }
        this.progressDialog = new AlertDialog(getParentActivity(), 3);
        this.progressDialog.setCanCacnel(false);
        this.progressDialog.show();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resetWallPapers(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WallpapersListActivity.this.lambda$null$1$WallpapersListActivity(tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$WallpapersListActivity(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                WallpapersListActivity.this.loadWallpapers();
            }
        });
    }

    public void onResume() {
        super.onResume();
        MessagesController.getGlobalMainSettings();
        Theme.OverrideWallpaperInfo overrideWallpaperInfo = Theme.getActiveTheme().overrideWallpaper;
        if (overrideWallpaperInfo != null) {
            this.selectedBackgroundSlug = overrideWallpaperInfo.slug;
            this.selectedColor = overrideWallpaperInfo.color;
            this.selectedGradientColor = overrideWallpaperInfo.gradientColor;
            this.selectedGradientRotation = overrideWallpaperInfo.rotation;
            this.selectedIntensity = overrideWallpaperInfo.intensity;
            this.selectedBackgroundMotion = overrideWallpaperInfo.isMotion;
            this.selectedBackgroundBlurred = overrideWallpaperInfo.isBlurred;
        } else {
            this.selectedBackgroundSlug = Theme.hasWallpaperFromTheme() ? "t" : "d";
            this.selectedColor = 0;
            this.selectedGradientColor = 0;
            this.selectedGradientRotation = 45;
            this.selectedIntensity = 1.0f;
            this.selectedBackgroundMotion = false;
            this.selectedBackgroundBlurred = false;
        }
        fillWallpapersWithCustom();
        fixLayout();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
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

    /* access modifiers changed from: private */
    public boolean onItemLongClick(WallpaperCell wallpaperCell, Object obj, int i) {
        if (this.actionBar.isActionModeShowed() || getParentActivity() == null || !(obj instanceof TLRPC.TL_wallPaper)) {
            return false;
        }
        TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) obj;
        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        this.selectedWallPapers.put(tL_wallPaper.id, tL_wallPaper);
        this.selectedMessagesCountTextView.setNumber(1, false);
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.actionModeViews.size(); i2++) {
            View view = this.actionModeViews.get(i2);
            AndroidUtilities.clearDrawableAnimation(view);
            arrayList.add(ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{0.1f, 1.0f}));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.setDuration(250);
        animatorSet.start();
        this.scrolling = false;
        this.actionBar.showActionMode();
        wallpaperCell.setChecked(i, true, true);
        return true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v9, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v10, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onItemClick(org.telegram.ui.Cells.WallpaperCell r12, java.lang.Object r13, int r14) {
        /*
            r11 = this;
            org.telegram.ui.ActionBar.ActionBar r0 = r11.actionBar
            boolean r0 = r0.isActionModeShowed()
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x0054
            boolean r0 = r13 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper
            if (r0 != 0) goto L_0x000f
            return
        L_0x000f:
            org.telegram.tgnet.TLRPC$TL_wallPaper r13 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r13
            android.util.LongSparseArray<java.lang.Object> r0 = r11.selectedWallPapers
            long r3 = r13.id
            int r0 = r0.indexOfKey(r3)
            if (r0 < 0) goto L_0x0023
            android.util.LongSparseArray<java.lang.Object> r0 = r11.selectedWallPapers
            long r3 = r13.id
            r0.remove(r3)
            goto L_0x002a
        L_0x0023:
            android.util.LongSparseArray<java.lang.Object> r0 = r11.selectedWallPapers
            long r3 = r13.id
            r0.put(r3, r13)
        L_0x002a:
            android.util.LongSparseArray<java.lang.Object> r0 = r11.selectedWallPapers
            int r0 = r0.size()
            if (r0 != 0) goto L_0x0038
            org.telegram.ui.ActionBar.ActionBar r0 = r11.actionBar
            r0.hideActionMode()
            goto L_0x0043
        L_0x0038:
            org.telegram.ui.Components.NumberTextView r0 = r11.selectedMessagesCountTextView
            android.util.LongSparseArray<java.lang.Object> r3 = r11.selectedWallPapers
            int r3 = r3.size()
            r0.setNumber(r3, r2)
        L_0x0043:
            r11.scrolling = r1
            android.util.LongSparseArray<java.lang.Object> r0 = r11.selectedWallPapers
            long r3 = r13.id
            int r13 = r0.indexOfKey(r3)
            if (r13 < 0) goto L_0x0050
            r1 = 1
        L_0x0050:
            r12.setChecked(r14, r1, r2)
            goto L_0x00ae
        L_0x0054:
            java.lang.String r12 = r11.getWallPaperSlug(r13)
            boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper
            if (r14 == 0) goto L_0x0085
            r14 = r13
            org.telegram.tgnet.TLRPC$TL_wallPaper r14 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r14
            boolean r0 = r14.pattern
            if (r0 == 0) goto L_0x0085
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r13 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r4 = r14.slug
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r14.settings
            int r5 = r0.background_color
            int r6 = r0.second_background_color
            int r0 = r0.rotation
            int r7 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r0, r1)
            org.telegram.tgnet.TLRPC$WallPaperSettings r0 = r14.settings
            int r1 = r0.intensity
            float r1 = (float) r1
            r3 = 1120403456(0x42CLASSNAME, float:100.0)
            float r8 = r1 / r3
            boolean r9 = r0.motion
            r10 = 0
            r3 = r13
            r3.<init>(r4, r5, r6, r7, r8, r9, r10)
            r13.pattern = r14
        L_0x0085:
            org.telegram.ui.ThemePreviewActivity r14 = new org.telegram.ui.ThemePreviewActivity
            r0 = 0
            r14.<init>(r13, r0)
            int r13 = r11.currentType
            if (r13 != r2) goto L_0x0097
            org.telegram.ui.-$$Lambda$obLeTjuOecC5VgqnkRKKKiZf1rM r13 = new org.telegram.ui.-$$Lambda$obLeTjuOecC5VgqnkRKKKiZf1rM
            r13.<init>()
            r14.setDelegate(r13)
        L_0x0097:
            java.lang.String r13 = r11.selectedBackgroundSlug
            boolean r12 = r13.equals(r12)
            if (r12 == 0) goto L_0x00a6
            boolean r12 = r11.selectedBackgroundBlurred
            boolean r13 = r11.selectedBackgroundMotion
            r14.setInitialModes(r12, r13)
        L_0x00a6:
            java.util.ArrayList<java.lang.Object> r12 = r11.patterns
            r14.setPatterns(r12)
            r11.presentFragment(r14)
        L_0x00ae:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpapersListActivity.onItemClick(org.telegram.ui.Cells.WallpaperCell, java.lang.Object, int):void");
    }

    private String getWallPaperSlug(Object obj) {
        if (obj instanceof TLRPC.TL_wallPaper) {
            return ((TLRPC.TL_wallPaper) obj).slug;
        }
        if (obj instanceof ColorWallpaper) {
            return ((ColorWallpaper) obj).slug;
        }
        if (obj instanceof FileWallpaper) {
            return ((FileWallpaper) obj).slug;
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void updateRowsSelection() {
        int childCount = this.listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof WallpaperCell) {
                WallpaperCell wallpaperCell = (WallpaperCell) childAt;
                for (int i2 = 0; i2 < 5; i2++) {
                    wallpaperCell.setChecked(i2, false, true);
                }
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC.WallPaperSettings wallPaperSettings;
        if (i == NotificationCenter.wallpapersDidLoad) {
            ArrayList arrayList = objArr[0];
            this.patterns.clear();
            if (this.currentType != 1) {
                this.wallPapers.clear();
                this.allWallPapers.clear();
                this.allWallPapersDict.clear();
                this.allWallPapers.addAll(arrayList);
            }
            int size = arrayList.size();
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) arrayList.get(i3);
                if (tL_wallPaper.pattern) {
                    this.patterns.add(tL_wallPaper);
                }
                if (this.currentType != 1 && (!tL_wallPaper.pattern || !((wallPaperSettings = tL_wallPaper.settings) == null || wallPaperSettings.background_color == 0))) {
                    this.allWallPapersDict.put(tL_wallPaper.slug, tL_wallPaper);
                    this.wallPapers.add(tL_wallPaper);
                }
            }
            this.selectedBackgroundSlug = Theme.getSelectedBackgroundSlug();
            fillWallpapersWithCustom();
            loadWallpapers();
        } else if (i == NotificationCenter.didSetNewWallpapper) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
            }
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.closeSearchField();
            }
        } else if (i == NotificationCenter.wallpapersNeedReload) {
            MessagesStorage.getInstance(this.currentAccount).getWallpapers();
        }
    }

    /* access modifiers changed from: private */
    public void loadWallpapers() {
        int size = this.allWallPapers.size();
        long j = 0;
        for (int i = 0; i < size; i++) {
            Object obj = this.allWallPapers.get(i);
            if (obj instanceof TLRPC.TL_wallPaper) {
                long j2 = ((TLRPC.TL_wallPaper) obj).id;
                j = (((((((j * 20261) + 2147483648L) + ((long) ((int) (j2 >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) j2))) % 2147483648L;
            }
        }
        TLRPC.TL_account_getWallPapers tL_account_getWallPapers = new TLRPC.TL_account_getWallPapers();
        tL_account_getWallPapers.hash = (int) j;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_getWallPapers, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WallpapersListActivity.this.lambda$loadWallpapers$5$WallpapersListActivity(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$loadWallpapers$5$WallpapersListActivity(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                WallpapersListActivity.this.lambda$null$4$WallpapersListActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$4$WallpapersListActivity(TLObject tLObject) {
        TLRPC.WallPaperSettings wallPaperSettings;
        if (tLObject instanceof TLRPC.TL_account_wallPapers) {
            TLRPC.TL_account_wallPapers tL_account_wallPapers = (TLRPC.TL_account_wallPapers) tLObject;
            this.patterns.clear();
            if (this.currentType != 1) {
                this.wallPapers.clear();
                this.allWallPapersDict.clear();
                this.allWallPapers.clear();
                this.allWallPapers.addAll(tL_account_wallPapers.wallpapers);
            }
            int size = tL_account_wallPapers.wallpapers.size();
            for (int i = 0; i < size; i++) {
                TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) tL_account_wallPapers.wallpapers.get(i);
                this.allWallPapersDict.put(tL_wallPaper.slug, tL_wallPaper);
                if (tL_wallPaper.pattern) {
                    this.patterns.add(tL_wallPaper);
                }
                if (this.currentType != 1 && (!tL_wallPaper.pattern || !((wallPaperSettings = tL_wallPaper.settings) == null || wallPaperSettings.background_color == 0))) {
                    this.wallPapers.add(tL_wallPaper);
                }
            }
            fillWallpapersWithCustom();
            MessagesStorage.getInstance(this.currentAccount).putWallpapers(tL_account_wallPapers.wallpapers, 1);
        }
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.listView.smoothScrollToPosition(0);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x00ae  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00c4  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x013c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void fillWallpapersWithCustom() {
        /*
            r15 = this;
            int r0 = r15.currentType
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            org.telegram.messenger.MessagesController.getGlobalMainSettings()
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = r15.addedColorWallpaper
            r1 = 0
            if (r0 == 0) goto L_0x0014
            java.util.ArrayList<java.lang.Object> r2 = r15.wallPapers
            r2.remove(r0)
            r15.addedColorWallpaper = r1
        L_0x0014:
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = r15.addedFileWallpaper
            if (r0 == 0) goto L_0x001f
            java.util.ArrayList<java.lang.Object> r2 = r15.wallPapers
            r2.remove(r0)
            r15.addedFileWallpaper = r1
        L_0x001f:
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = r15.catsWallpaper
            java.lang.String r2 = "d"
            if (r0 != 0) goto L_0x0033
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = new org.telegram.ui.WallpapersListActivity$FileWallpaper
            r3 = 2131165277(0x7var_d, float:1.7944767E38)
            r4 = 2131165314(0x7var_, float:1.7944842E38)
            r0.<init>((java.lang.String) r2, (int) r3, (int) r4)
            r15.catsWallpaper = r0
            goto L_0x0038
        L_0x0033:
            java.util.ArrayList<java.lang.Object> r3 = r15.wallPapers
            r3.remove(r0)
        L_0x0038:
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = r15.themeWallpaper
            if (r0 == 0) goto L_0x0041
            java.util.ArrayList<java.lang.Object> r3 = r15.wallPapers
            r3.remove(r0)
        L_0x0041:
            java.util.HashMap<java.lang.String, java.lang.Object> r0 = r15.allWallPapersDict
            java.lang.String r3 = r15.selectedBackgroundSlug
            java.lang.Object r0 = r0.get(r3)
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper
            r4 = 0
            if (r3 == 0) goto L_0x008a
            r3 = r0
            org.telegram.tgnet.TLRPC$TL_wallPaper r3 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r3
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r5 = r5.overrideWallpaper
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r3.settings
            if (r5 == 0) goto L_0x0087
            int r6 = r15.selectedColor
            int r7 = r5.background_color
            if (r6 != r7) goto L_0x0082
            int r6 = r15.selectedGradientColor
            int r7 = r5.second_background_color
            if (r6 != r7) goto L_0x0082
            if (r6 == 0) goto L_0x0087
            int r6 = r15.selectedGradientRotation
            int r5 = r5.rotation
            int r5 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r5, r4)
            if (r6 == r5) goto L_0x0087
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r3.settings
            int r5 = r5.intensity
            float r5 = (float) r5
            float r6 = r15.selectedIntensity
            float r5 = r5 - r6
            r6 = 981668463(0x3a83126f, float:0.001)
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 <= 0) goto L_0x0087
        L_0x0082:
            java.lang.String r0 = ""
            r5 = r3
            r3 = r1
            goto L_0x0090
        L_0x0087:
            java.lang.String r3 = r15.selectedBackgroundSlug
            goto L_0x008c
        L_0x008a:
            java.lang.String r3 = r15.selectedBackgroundSlug
        L_0x008c:
            r5 = r1
            r14 = r3
            r3 = r0
            r0 = r14
        L_0x0090:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r6 = org.telegram.ui.ActionBar.Theme.getCurrentTheme()
            boolean r6 = r6.isDark()
            java.util.ArrayList<java.lang.Object> r7 = r15.wallPapers
            org.telegram.ui.-$$Lambda$WallpapersListActivity$jUk8XvxaDgRddt16z7pmf9R62MM r8 = new org.telegram.ui.-$$Lambda$WallpapersListActivity$jUk8XvxaDgRddt16z7pmf9R62MM
            r8.<init>(r0, r6)
            java.util.Collections.sort(r7, r8)
            boolean r0 = org.telegram.ui.ActionBar.Theme.hasWallpaperFromTheme()
            if (r0 == 0) goto L_0x00c4
            boolean r0 = org.telegram.ui.ActionBar.Theme.isThemeWallpaperPublic()
            if (r0 != 0) goto L_0x00c4
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = r15.themeWallpaper
            if (r0 != 0) goto L_0x00bc
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = new org.telegram.ui.WallpapersListActivity$FileWallpaper
            r1 = -2
            java.lang.String r6 = "t"
            r0.<init>((java.lang.String) r6, (int) r1, (int) r1)
            r15.themeWallpaper = r0
        L_0x00bc:
            java.util.ArrayList<java.lang.Object> r0 = r15.wallPapers
            org.telegram.ui.WallpapersListActivity$FileWallpaper r1 = r15.themeWallpaper
            r0.add(r4, r1)
            goto L_0x00c6
        L_0x00c4:
            r15.themeWallpaper = r1
        L_0x00c6:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            java.lang.String r1 = r15.selectedBackgroundSlug
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            java.lang.String r6 = "c"
            if (r1 != 0) goto L_0x0103
            java.lang.String r1 = r15.selectedBackgroundSlug
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x00df
            if (r3 != 0) goto L_0x00df
            goto L_0x0103
        L_0x00df:
            int r0 = r15.selectedColor
            if (r0 == 0) goto L_0x0183
            java.lang.String r0 = r15.selectedBackgroundSlug
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x0183
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r1 = r15.selectedBackgroundSlug
            int r3 = r15.selectedColor
            int r5 = r15.selectedGradientColor
            int r6 = r15.selectedGradientRotation
            r0.<init>(r1, r3, r5, r6)
            r15.addedColorWallpaper = r0
            java.util.ArrayList<java.lang.Object> r0 = r15.wallPapers
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = r15.addedColorWallpaper
            r0.add(r4, r1)
            goto L_0x0183
        L_0x0103:
            java.lang.String r1 = r15.selectedBackgroundSlug
            boolean r1 = r6.equals(r1)
            if (r1 != 0) goto L_0x013c
            int r8 = r15.selectedColor
            if (r8 == 0) goto L_0x013c
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r1 = r0.overrideWallpaper
            if (r1 == 0) goto L_0x0183
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r7 = r15.selectedBackgroundSlug
            int r9 = r15.selectedGradientColor
            int r10 = r15.selectedGradientRotation
            float r11 = r15.selectedIntensity
            boolean r12 = r15.selectedBackgroundMotion
            java.io.File r13 = new java.io.File
            java.io.File r3 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r0 = r0.overrideWallpaper
            java.lang.String r0 = r0.fileName
            r13.<init>(r3, r0)
            r6 = r1
            r6.<init>(r7, r8, r9, r10, r11, r12, r13)
            r15.addedColorWallpaper = r1
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = r15.addedColorWallpaper
            r0.pattern = r5
            java.util.ArrayList<java.lang.Object> r1 = r15.wallPapers
            r1.add(r4, r0)
            goto L_0x0183
        L_0x013c:
            int r1 = r15.selectedColor
            if (r1 == 0) goto L_0x0155
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r3 = r15.selectedBackgroundSlug
            int r5 = r15.selectedGradientColor
            int r6 = r15.selectedGradientRotation
            r0.<init>(r3, r1, r5, r6)
            r15.addedColorWallpaper = r0
            java.util.ArrayList<java.lang.Object> r0 = r15.wallPapers
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = r15.addedColorWallpaper
            r0.add(r4, r1)
            goto L_0x0183
        L_0x0155:
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r1 = r0.overrideWallpaper
            if (r1 == 0) goto L_0x0183
            org.telegram.ui.WallpapersListActivity$FileWallpaper r1 = new org.telegram.ui.WallpapersListActivity$FileWallpaper
            java.lang.String r3 = r15.selectedBackgroundSlug
            java.io.File r5 = new java.io.File
            java.io.File r6 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r7 = r0.overrideWallpaper
            java.lang.String r7 = r7.fileName
            r5.<init>(r6, r7)
            java.io.File r6 = new java.io.File
            java.io.File r7 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r0 = r0.overrideWallpaper
            java.lang.String r0 = r0.originalFileName
            r6.<init>(r7, r0)
            r1.<init>((java.lang.String) r3, (java.io.File) r5, (java.io.File) r6)
            r15.addedFileWallpaper = r1
            java.util.ArrayList<java.lang.Object> r0 = r15.wallPapers
            org.telegram.ui.WallpapersListActivity$FileWallpaper r1 = r15.addedFileWallpaper
            r0.add(r4, r1)
        L_0x0183:
            java.lang.String r0 = r15.selectedBackgroundSlug
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0193
            java.util.ArrayList<java.lang.Object> r0 = r15.wallPapers
            org.telegram.ui.WallpapersListActivity$FileWallpaper r1 = r15.catsWallpaper
            r0.add(r4, r1)
            goto L_0x019a
        L_0x0193:
            java.util.ArrayList<java.lang.Object> r0 = r15.wallPapers
            org.telegram.ui.WallpapersListActivity$FileWallpaper r1 = r15.catsWallpaper
            r0.add(r1)
        L_0x019a:
            r15.updateRows()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpapersListActivity.fillWallpapersWithCustom():void");
    }

    public /* synthetic */ int lambda$fillWallpapersWithCustom$6$WallpapersListActivity(String str, boolean z, Object obj, Object obj2) {
        if (!(obj instanceof TLRPC.TL_wallPaper) || !(obj2 instanceof TLRPC.TL_wallPaper)) {
            return 0;
        }
        TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) obj;
        TLRPC.TL_wallPaper tL_wallPaper2 = (TLRPC.TL_wallPaper) obj2;
        if (str.equals(tL_wallPaper.slug)) {
            return -1;
        }
        if (str.equals(tL_wallPaper2.slug)) {
            return 1;
        }
        int indexOf = this.allWallPapers.indexOf(tL_wallPaper);
        int indexOf2 = this.allWallPapers.indexOf(tL_wallPaper2);
        if ((!tL_wallPaper.dark || !tL_wallPaper2.dark) && (tL_wallPaper.dark || tL_wallPaper2.dark)) {
            if (!tL_wallPaper.dark || tL_wallPaper2.dark) {
                if (z) {
                    return 1;
                }
                return -1;
            } else if (z) {
                return -1;
            } else {
                return 1;
            }
        } else if (indexOf > indexOf2) {
            return 1;
        } else {
            if (indexOf < indexOf2) {
                return -1;
            }
            return 0;
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        if (this.currentType == 0) {
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.uploadImageRow = i;
            int i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.setColorRow = i2;
            int i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.sectionRow = i3;
        } else {
            this.uploadImageRow = -1;
            this.setColorRow = -1;
            this.sectionRow = -1;
        }
        if (!this.wallPapers.isEmpty()) {
            this.totalWallpaperRows = (int) Math.ceil((double) (((float) this.wallPapers.size()) / ((float) this.columnsCount)));
            int i4 = this.rowCount;
            this.wallPaperStartRow = i4;
            this.rowCount = i4 + this.totalWallpaperRows;
        } else {
            this.wallPaperStartRow = -1;
        }
        if (this.currentType == 0) {
            int i5 = this.rowCount;
            this.rowCount = i5 + 1;
            this.resetSectionRow = i5;
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.resetRow = i6;
            int i7 = this.rowCount;
            this.rowCount = i7 + 1;
            this.resetInfoRow = i7;
        } else {
            this.resetSectionRow = -1;
            this.resetRow = -1;
            this.resetInfoRow = -1;
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            this.scrolling = true;
            listAdapter2.notifyDataSetChanged();
        }
    }

    private void fixLayout() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    WallpapersListActivity.this.fixLayoutInternal();
                    if (WallpapersListActivity.this.listView == null) {
                        return true;
                    }
                    WallpapersListActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void fixLayoutInternal() {
        if (getParentActivity() != null) {
            int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
            if (AndroidUtilities.isTablet()) {
                this.columnsCount = 3;
            } else if (rotation == 3 || rotation == 1) {
                this.columnsCount = 5;
            } else {
                this.columnsCount = 3;
            }
            updateRows();
        }
    }

    private class ColorCell extends View {
        private int color;

        public ColorCell(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(AndroidUtilities.dp(50.0f), AndroidUtilities.dp(62.0f));
        }

        public void setColor(int i) {
            this.color = i;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            WallpapersListActivity.this.colorPaint.setColor(this.color);
            canvas.drawCircle((float) AndroidUtilities.dp(25.0f), (float) AndroidUtilities.dp(31.0f), (float) AndroidUtilities.dp(18.0f), WallpapersListActivity.this.colorPaint);
            if (this.color == Theme.getColor("windowBackgroundWhite")) {
                canvas.drawCircle((float) AndroidUtilities.dp(25.0f), (float) AndroidUtilities.dp(31.0f), (float) AndroidUtilities.dp(18.0f), WallpapersListActivity.this.colorFramePaint);
            }
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private boolean bingSearchEndReached = true;
        private int imageReqId;
        private RecyclerListView innerListView;
        private String lastSearchImageString;
        private String lastSearchString;
        private int lastSearchToken;
        /* access modifiers changed from: private */
        public Context mContext;
        private String nextImagesSearchOffset;
        private ArrayList<MediaController.SearchImage> searchResult = new ArrayList<>();
        private HashMap<String, MediaController.SearchImage> searchResultKeys = new HashMap<>();
        private Runnable searchRunnable;
        private boolean searchingUser;
        private String selectedColor;

        private class CategoryAdapterRecycler extends RecyclerListView.SelectionAdapter {
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return true;
            }

            private CategoryAdapterRecycler() {
            }

            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                SearchAdapter searchAdapter = SearchAdapter.this;
                return new RecyclerListView.Holder(new ColorCell(searchAdapter.mContext));
            }

            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                ((ColorCell) viewHolder.itemView).setColor(WallpapersListActivity.searchColors[i]);
            }

            public int getItemCount() {
                return WallpapersListActivity.searchColors.length;
            }
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public RecyclerListView getInnerListView() {
            return this.innerListView;
        }

        public void onDestroy() {
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
        }

        public void clearColor() {
            this.selectedColor = null;
            processSearch((String) null, true);
        }

        /* access modifiers changed from: private */
        public void processSearch(String str, boolean z) {
            if (!(str == null || this.selectedColor == null)) {
                str = "#color" + this.selectedColor + " " + str;
            }
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(str)) {
                this.searchResult.clear();
                this.searchResultKeys.clear();
                this.bingSearchEndReached = true;
                this.lastSearchString = null;
                if (this.imageReqId != 0) {
                    ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).cancelRequest(this.imageReqId, true);
                    this.imageReqId = 0;
                }
                WallpapersListActivity.this.searchEmptyView.showTextView();
            } else {
                WallpapersListActivity.this.searchEmptyView.showProgress();
                if (z) {
                    doSearch(str);
                } else {
                    this.searchRunnable = new Runnable(str) {
                        private final /* synthetic */ String f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            WallpapersListActivity.SearchAdapter.this.lambda$processSearch$0$WallpapersListActivity$SearchAdapter(this.f$1);
                        }
                    };
                    AndroidUtilities.runOnUIThread(this.searchRunnable, 500);
                }
            }
            notifyDataSetChanged();
        }

        public /* synthetic */ void lambda$processSearch$0$WallpapersListActivity$SearchAdapter(String str) {
            doSearch(str);
            this.searchRunnable = null;
        }

        private void doSearch(String str) {
            this.searchResult.clear();
            this.searchResultKeys.clear();
            this.bingSearchEndReached = true;
            searchImages(str, "", true);
            this.lastSearchString = str;
            notifyDataSetChanged();
        }

        private void searchBotUser() {
            if (!this.searchingUser) {
                this.searchingUser = true;
                TLRPC.TL_contacts_resolveUsername tL_contacts_resolveUsername = new TLRPC.TL_contacts_resolveUsername();
                tL_contacts_resolveUsername.username = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).imageSearchBot;
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(tL_contacts_resolveUsername, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        WallpapersListActivity.SearchAdapter.this.lambda$searchBotUser$2$WallpapersListActivity$SearchAdapter(tLObject, tL_error);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$searchBotUser$2$WallpapersListActivity$SearchAdapter(TLObject tLObject, TLRPC.TL_error tL_error) {
            if (tLObject != null) {
                AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
                    private final /* synthetic */ TLObject f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        WallpapersListActivity.SearchAdapter.this.lambda$null$1$WallpapersListActivity$SearchAdapter(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$null$1$WallpapersListActivity$SearchAdapter(TLObject tLObject) {
            TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer) tLObject;
            MessagesController.getInstance(WallpapersListActivity.this.currentAccount).putUsers(tL_contacts_resolvedPeer.users, false);
            MessagesController.getInstance(WallpapersListActivity.this.currentAccount).putChats(tL_contacts_resolvedPeer.chats, false);
            MessagesStorage.getInstance(WallpapersListActivity.this.currentAccount).putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, true, true);
            String str = this.lastSearchImageString;
            this.lastSearchImageString = null;
            searchImages(str, "", false);
        }

        public void loadMoreResults() {
            if (!this.bingSearchEndReached && this.imageReqId == 0) {
                searchImages(this.lastSearchString, this.nextImagesSearchOffset, true);
            }
        }

        private void searchImages(String str, String str2, boolean z) {
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
            this.lastSearchImageString = str;
            TLObject userOrChat = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).getUserOrChat(MessagesController.getInstance(WallpapersListActivity.this.currentAccount).imageSearchBot);
            if (userOrChat instanceof TLRPC.User) {
                TLRPC.TL_messages_getInlineBotResults tL_messages_getInlineBotResults = new TLRPC.TL_messages_getInlineBotResults();
                tL_messages_getInlineBotResults.query = "#wallpaper " + str;
                tL_messages_getInlineBotResults.bot = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).getInputUser((TLRPC.User) userOrChat);
                tL_messages_getInlineBotResults.offset = str2;
                tL_messages_getInlineBotResults.peer = new TLRPC.TL_inputPeerEmpty();
                int i = this.lastSearchToken + 1;
                this.lastSearchToken = i;
                this.imageReqId = ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(tL_messages_getInlineBotResults, new RequestDelegate(i) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        WallpapersListActivity.SearchAdapter.this.lambda$searchImages$4$WallpapersListActivity$SearchAdapter(this.f$1, tLObject, tL_error);
                    }
                });
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).bindRequestToGuid(this.imageReqId, WallpapersListActivity.this.classGuid);
            } else if (z) {
                searchBotUser();
            }
        }

        public /* synthetic */ void lambda$searchImages$4$WallpapersListActivity$SearchAdapter(int i, TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable(i, tLObject) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ TLObject f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    WallpapersListActivity.SearchAdapter.this.lambda$null$3$WallpapersListActivity$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$null$3$WallpapersListActivity$SearchAdapter(int i, TLObject tLObject) {
            if (i == this.lastSearchToken) {
                boolean z = false;
                this.imageReqId = 0;
                int size = this.searchResult.size();
                if (tLObject != null) {
                    TLRPC.messages_BotResults messages_botresults = (TLRPC.messages_BotResults) tLObject;
                    this.nextImagesSearchOffset = messages_botresults.next_offset;
                    int size2 = messages_botresults.results.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        TLRPC.BotInlineResult botInlineResult = messages_botresults.results.get(i2);
                        if ("photo".equals(botInlineResult.type) && !this.searchResultKeys.containsKey(botInlineResult.id)) {
                            MediaController.SearchImage searchImage = new MediaController.SearchImage();
                            TLRPC.Photo photo = botInlineResult.photo;
                            if (photo != null) {
                                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                                TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, 320);
                                if (closestPhotoSizeWithSize != null) {
                                    searchImage.width = closestPhotoSizeWithSize.w;
                                    searchImage.height = closestPhotoSizeWithSize.h;
                                    searchImage.photoSize = closestPhotoSizeWithSize;
                                    searchImage.photo = botInlineResult.photo;
                                    searchImage.size = closestPhotoSizeWithSize.size;
                                    searchImage.thumbPhotoSize = closestPhotoSizeWithSize2;
                                }
                            } else if (botInlineResult.content != null) {
                                int i3 = 0;
                                while (true) {
                                    if (i3 >= botInlineResult.content.attributes.size()) {
                                        break;
                                    }
                                    TLRPC.DocumentAttribute documentAttribute = botInlineResult.content.attributes.get(i3);
                                    if (documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) {
                                        searchImage.width = documentAttribute.w;
                                        searchImage.height = documentAttribute.h;
                                        break;
                                    }
                                    i3++;
                                }
                                TLRPC.WebDocument webDocument = botInlineResult.thumb;
                                if (webDocument != null) {
                                    searchImage.thumbUrl = webDocument.url;
                                } else {
                                    searchImage.thumbUrl = null;
                                }
                                TLRPC.WebDocument webDocument2 = botInlineResult.content;
                                searchImage.imageUrl = webDocument2.url;
                                searchImage.size = webDocument2.size;
                            }
                            searchImage.id = botInlineResult.id;
                            searchImage.type = 0;
                            this.searchResult.add(searchImage);
                            this.searchResultKeys.put(searchImage.id, searchImage);
                        }
                    }
                    if (size == this.searchResult.size() || this.nextImagesSearchOffset == null) {
                        z = true;
                    }
                    this.bingSearchEndReached = z;
                }
                if (size != this.searchResult.size()) {
                    int access$4300 = size % WallpapersListActivity.this.columnsCount;
                    float f = (float) size;
                    int ceil = (int) Math.ceil((double) (f / ((float) WallpapersListActivity.this.columnsCount)));
                    if (access$4300 != 0) {
                        notifyItemChanged(((int) Math.ceil((double) (f / ((float) WallpapersListActivity.this.columnsCount)))) - 1);
                    }
                    WallpapersListActivity.this.searchAdapter.notifyItemRangeInserted(ceil, ((int) Math.ceil((double) (((float) this.searchResult.size()) / ((float) WallpapersListActivity.this.columnsCount)))) - ceil);
                }
                WallpapersListActivity.this.searchEmptyView.showTextView();
            }
        }

        public int getItemCount() {
            if (TextUtils.isEmpty(this.lastSearchString)) {
                return 2;
            }
            return (int) Math.ceil((double) (((float) this.searchResult.size()) / ((float) WallpapersListActivity.this.columnsCount)));
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 2;
        }

        public /* synthetic */ void lambda$onCreateViewHolder$5$WallpapersListActivity$SearchAdapter(View view, int i) {
            String string = LocaleController.getString("BackgroundSearchColor", NUM);
            SpannableString spannableString = new SpannableString(string + " " + LocaleController.getString(WallpapersListActivity.searchColorsNames[i], WallpapersListActivity.searchColorsNamesR[i]));
            spannableString.setSpan(new ForegroundColorSpan(Theme.getColor("actionBarDefaultSubtitle")), string.length(), spannableString.length(), 33);
            WallpapersListActivity.this.searchItem.setSearchFieldCaption(spannableString);
            WallpapersListActivity.this.searchItem.setSearchFieldHint((CharSequence) null);
            WallpapersListActivity.this.searchItem.setSearchFieldText("", true);
            this.selectedColor = WallpapersListActivity.searchColorsNames[i];
            processSearch("", true);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = null;
            if (i == 0) {
                view = new WallpaperCell(this.mContext) {
                    /* access modifiers changed from: protected */
                    public void onWallpaperClick(Object obj, int i) {
                        WallpapersListActivity.this.presentFragment(new ThemePreviewActivity(obj, (Bitmap) null));
                    }
                };
            } else if (i == 1) {
                AnonymousClass2 r1 = new RecyclerListView(this.mContext) {
                    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                        if (!(getParent() == null || getParent().getParent() == null)) {
                            getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                        }
                        return super.onInterceptTouchEvent(motionEvent);
                    }
                };
                r1.setItemAnimator((RecyclerView.ItemAnimator) null);
                r1.setLayoutAnimation((LayoutAnimationController) null);
                AnonymousClass3 r2 = new LinearLayoutManager(this.mContext) {
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                };
                r1.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
                r1.setClipToPadding(false);
                r2.setOrientation(0);
                r1.setLayoutManager(r2);
                r1.setAdapter(new CategoryAdapterRecycler());
                r1.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                    public final void onItemClick(View view, int i) {
                        WallpapersListActivity.SearchAdapter.this.lambda$onCreateViewHolder$5$WallpapersListActivity$SearchAdapter(view, i);
                    }
                });
                this.innerListView = r1;
                view = r1;
            } else if (i == 2) {
                view = new GraySectionCell(this.mContext);
            }
            if (i == 1) {
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(60.0f)));
            } else {
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                WallpaperCell wallpaperCell = (WallpaperCell) viewHolder.itemView;
                int access$4300 = i * WallpapersListActivity.this.columnsCount;
                int ceil = (int) Math.ceil((double) (((float) this.searchResult.size()) / ((float) WallpapersListActivity.this.columnsCount)));
                int access$43002 = WallpapersListActivity.this.columnsCount;
                boolean z = true;
                boolean z2 = access$4300 == 0;
                if (access$4300 / WallpapersListActivity.this.columnsCount != ceil - 1) {
                    z = false;
                }
                wallpaperCell.setParams(access$43002, z2, z);
                for (int i2 = 0; i2 < WallpapersListActivity.this.columnsCount; i2++) {
                    int i3 = access$4300 + i2;
                    wallpaperCell.setWallpaper(WallpapersListActivity.this.currentType, i2, i3 < this.searchResult.size() ? this.searchResult.get(i3) : null, "", (Drawable) null, false);
                }
            } else if (itemViewType == 2) {
                ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString("SearchByColor", NUM));
            }
        }

        public int getItemViewType(int i) {
            if (TextUtils.isEmpty(this.lastSearchString)) {
                return i == 0 ? 2 : 1;
            }
            return 0;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public int getItemCount() {
            return WallpapersListActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            View shadowSectionCell;
            if (i != 0) {
                int i2 = NUM;
                if (i == 1) {
                    shadowSectionCell = new ShadowSectionCell(this.mContext);
                    Context context = this.mContext;
                    if (WallpapersListActivity.this.wallPaperStartRow != -1) {
                        i2 = NUM;
                    }
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(context, i2, "windowBackgroundGrayShadow"));
                    combinedDrawable.setFullsize(true);
                    shadowSectionCell.setBackgroundDrawable(combinedDrawable);
                } else if (i != 3) {
                    view = new WallpaperCell(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void onWallpaperClick(Object obj, int i) {
                            WallpapersListActivity.this.onItemClick(this, obj, i);
                        }

                        /* access modifiers changed from: protected */
                        public boolean onWallpaperLongClick(Object obj, int i) {
                            return WallpapersListActivity.this.onItemLongClick(this, obj, i);
                        }
                    };
                } else {
                    shadowSectionCell = new TextInfoPrivacyCell(this.mContext);
                    CombinedDrawable combinedDrawable2 = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    combinedDrawable2.setFullsize(true);
                    shadowSectionCell.setBackgroundDrawable(combinedDrawable2);
                }
                view = shadowSectionCell;
            } else {
                view = new TextCell(this.mContext);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            long j;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                TextCell textCell = (TextCell) viewHolder.itemView;
                if (i == WallpapersListActivity.this.uploadImageRow) {
                    textCell.setTextAndIcon(LocaleController.getString("SelectFromGallery", NUM), NUM, true);
                } else if (i == WallpapersListActivity.this.setColorRow) {
                    textCell.setTextAndIcon(LocaleController.getString("SetColor", NUM), NUM, false);
                } else if (i == WallpapersListActivity.this.resetRow) {
                    textCell.setText(LocaleController.getString("ResetChatBackgrounds", NUM), false);
                }
            } else if (itemViewType == 2) {
                WallpaperCell wallpaperCell = (WallpaperCell) viewHolder.itemView;
                int access$5200 = (i - WallpapersListActivity.this.wallPaperStartRow) * WallpapersListActivity.this.columnsCount;
                wallpaperCell.setParams(WallpapersListActivity.this.columnsCount, access$5200 == 0, access$5200 / WallpapersListActivity.this.columnsCount == WallpapersListActivity.this.totalWallpaperRows - 1);
                for (int i2 = 0; i2 < WallpapersListActivity.this.columnsCount; i2++) {
                    int i3 = access$5200 + i2;
                    Object obj = i3 < WallpapersListActivity.this.wallPapers.size() ? WallpapersListActivity.this.wallPapers.get(i3) : null;
                    String str = "";
                    if (obj instanceof TLRPC.TL_wallPaper) {
                        TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) obj;
                        Theme.OverrideWallpaperInfo overrideWallpaperInfo = Theme.getActiveTheme().overrideWallpaper;
                        if (!WallpapersListActivity.this.selectedBackgroundSlug.equals(tL_wallPaper.slug) || tL_wallPaper.settings == null || (WallpapersListActivity.this.selectedColor == tL_wallPaper.settings.background_color && WallpapersListActivity.this.selectedGradientColor == tL_wallPaper.settings.second_background_color && (WallpapersListActivity.this.selectedGradientColor == 0 || WallpapersListActivity.this.selectedGradientRotation == AndroidUtilities.getWallpaperRotation(tL_wallPaper.settings.rotation, false) || ((float) tL_wallPaper.settings.intensity) - WallpapersListActivity.this.selectedIntensity <= 0.001f))) {
                            str = WallpapersListActivity.this.selectedBackgroundSlug;
                        }
                        j = tL_wallPaper.id;
                    } else {
                        if (obj instanceof ColorWallpaper) {
                            ColorWallpaper colorWallpaper = (ColorWallpaper) obj;
                            if (colorWallpaper.color == WallpapersListActivity.this.selectedColor && colorWallpaper.gradientColor == WallpapersListActivity.this.selectedGradientColor) {
                                str = WallpapersListActivity.this.selectedBackgroundSlug;
                            }
                        } else {
                            str = WallpapersListActivity.this.selectedBackgroundSlug;
                        }
                        j = 0;
                    }
                    long j2 = j;
                    wallpaperCell.setWallpaper(WallpapersListActivity.this.currentType, i2, obj, str, (Drawable) null, false);
                    if (WallpapersListActivity.this.actionBar.isActionModeShowed()) {
                        wallpaperCell.setChecked(i2, WallpapersListActivity.this.selectedWallPapers.indexOfKey(j2) >= 0, !WallpapersListActivity.this.scrolling);
                    } else {
                        wallpaperCell.setChecked(i2, false, !WallpapersListActivity.this.scrolling);
                    }
                }
            } else if (itemViewType == 3) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == WallpapersListActivity.this.resetInfoRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("ResetChatBackgroundsInfo", NUM));
                }
            }
        }

        public int getItemViewType(int i) {
            if (i == WallpapersListActivity.this.uploadImageRow || i == WallpapersListActivity.this.setColorRow || i == WallpapersListActivity.this.resetRow) {
                return 0;
            }
            if (i == WallpapersListActivity.this.sectionRow || i == WallpapersListActivity.this.resetSectionRow) {
                return 1;
            }
            return i == WallpapersListActivity.this.resetInfoRow ? 3 : 2;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"), new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"), new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"), new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"), new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite")};
    }
}
