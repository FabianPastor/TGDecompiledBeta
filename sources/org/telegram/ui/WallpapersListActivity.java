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
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_account_getWallPapers;
import org.telegram.tgnet.TLRPC$TL_account_resetWallPapers;
import org.telegram.tgnet.TLRPC$TL_account_saveWallPaper;
import org.telegram.tgnet.TLRPC$TL_account_wallPapers;
import org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC$TL_documentEmpty;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC$TL_inputWallPaper;
import org.telegram.tgnet.TLRPC$TL_inputWallPaperNoFile;
import org.telegram.tgnet.TLRPC$TL_messages_getInlineBotResults;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.tgnet.TLRPC$TL_wallPaperNoFile;
import org.telegram.tgnet.TLRPC$TL_wallPaperSettings;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.tgnet.TLRPC$WallPaperSettings;
import org.telegram.tgnet.TLRPC$WebDocument;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.tgnet.TLRPC$messages_BotResults;
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

public class WallpapersListActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int[][] defaultColorsDark = {new int[]{-14797481, -15394250, -14924974, -14006975}, new int[]{-14867905, -14870478, -14997181, -15460815}, new int[]{-14666695, -15720408, -14861254, -15260107}, new int[]{-14932175, -15066075, -14208965, -15000799}, new int[]{-12968902, -14411460, -13029826, -15067598}, new int[]{-13885157, -12307670, -14542561, -12899018}, new int[]{-14797481, -15196106, -14924974, -15325638}, new int[]{-15658442, -15449521, -16047308, -12897955}, new int[]{-13809610, -15258855, -13221071, -15715791}, new int[]{-14865092}, new int[]{-15656154}, new int[]{-16051170}, new int[]{-14731745}, new int[]{-15524075}, new int[]{-15853808}, new int[]{-13685209}, new int[]{-14014945}, new int[]{-15132649}, new int[]{-12374480}, new int[]{-13755362}, new int[]{-14740716}, new int[]{-12374468}, new int[]{-13755352}, new int[]{-14740709}, new int[]{-12833213}, new int[]{-14083026}, new int[]{-14872031}, new int[]{-13554109}, new int[]{-14803922}, new int[]{-15461855}, new int[]{-13680833}, new int[]{-14602960}, new int[]{-15458784}, new int[]{-14211804}, new int[]{-15132906}, new int[]{-16777216}};
    private static final int[][] defaultColorsLight = {new int[]{-2368069, -9722489, -2762611, -7817084}, new int[]{-7487253, -4599318, -3755537, -1320977}, new int[]{-6832405, -5117462, -3755537, -1067044}, new int[]{-7676942, -7827988, -1859606, -9986835}, new int[]{-5190165, -6311702, -4461867, -5053475}, new int[]{-2430264, -6114049, -1258497, -4594945}, new int[]{-2298990, -7347754, -9985038, -8006011}, new int[]{-1399954, -990074, -876865, -1523602}, new int[]{-15438, -1916673, -6222, -471346}, new int[]{-2891798}, new int[]{-5913125}, new int[]{-9463352}, new int[]{-2956375}, new int[]{-5974898}, new int[]{-8537234}, new int[]{-1647186}, new int[]{-2769263}, new int[]{-3431303}, new int[]{-1326919}, new int[]{-2054243}, new int[]{-3573648}, new int[]{-1328696}, new int[]{-2056777}, new int[]{-2984557}, new int[]{-2440467}, new int[]{-2906649}, new int[]{-4880430}, new int[]{-4013331}, new int[]{-5921305}, new int[]{-8421424}, new int[]{-4005139}, new int[]{-5908761}, new int[]{-8406320}, new int[]{-2702663}, new int[]{-6518654}, new int[]{-16777216}};
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
    private ColorWallpaper catsWallpaper;
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
    /* access modifiers changed from: private */
    public HashMap<String, Object> localDict = new HashMap<>();
    /* access modifiers changed from: private */
    public ArrayList<ColorWallpaper> localWallPapers = new ArrayList<>();
    private ArrayList<Object> patterns = new ArrayList<>();
    private HashMap<Long, Object> patternsDict = new HashMap<>();
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
    public int selectedGradientColor1;
    /* access modifiers changed from: private */
    public int selectedGradientColor2;
    /* access modifiers changed from: private */
    public int selectedGradientColor3;
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

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public static class ColorWallpaper {
        public int color;
        public Bitmap defaultCache;
        public int gradientColor1;
        public int gradientColor2;
        public int gradientColor3;
        public int gradientRotation;
        public float intensity;
        public boolean isGradient;
        public boolean motion;
        public TLRPC$WallPaper parentWallpaper;
        public File path;
        public TLRPC$TL_wallPaper pattern;
        public long patternId;
        public String slug;

        public String getHash() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(this.color));
            sb.append(this.gradientColor1);
            sb.append(this.gradientColor2);
            sb.append(this.gradientColor3);
            sb.append(this.gradientRotation);
            sb.append(this.intensity);
            String str = this.slug;
            if (str == null) {
                str = "";
            }
            sb.append(str);
            return Utilities.MD5(sb.toString());
        }

        public ColorWallpaper(String str, int i, int i2, int i3) {
            this.slug = str;
            this.color = i | -16777216;
            int i4 = i2 == 0 ? 0 : -16777216 | i2;
            this.gradientColor1 = i4;
            this.gradientRotation = i4 == 0 ? 0 : i3;
            this.intensity = 1.0f;
        }

        public ColorWallpaper(String str, int i, int i2, int i3, int i4) {
            this.slug = str;
            this.color = i | -16777216;
            int i5 = 0;
            this.gradientColor1 = i2 == 0 ? 0 : i2 | -16777216;
            this.gradientColor2 = i3 == 0 ? 0 : i3 | -16777216;
            this.gradientColor3 = i4 != 0 ? i4 | -16777216 : i5;
            this.intensity = 1.0f;
            this.isGradient = true;
        }

        public ColorWallpaper(String str, int i, int i2, int i3, int i4, int i5, float f, boolean z, File file) {
            this.slug = str;
            this.color = i | -16777216;
            int i6 = 0;
            int i7 = i2 == 0 ? 0 : i2 | -16777216;
            this.gradientColor1 = i7;
            this.gradientColor2 = i3 == 0 ? 0 : i3 | -16777216;
            this.gradientColor3 = i4 != 0 ? i4 | -16777216 : i6;
            this.gradientRotation = i7 == 0 ? 45 : i5;
            this.intensity = f;
            this.path = file;
            this.motion = z;
        }

        public String getUrl() {
            String str;
            String str2;
            int i = this.gradientColor1;
            String str3 = null;
            if (i != 0) {
                str = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (i >> 16)) & 255), Integer.valueOf(((byte) (this.gradientColor1 >> 8)) & 255), Byte.valueOf((byte) (this.gradientColor1 & 255))}).toLowerCase();
            } else {
                str = null;
            }
            String lowerCase = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (this.color >> 16)) & 255), Integer.valueOf(((byte) (this.color >> 8)) & 255), Byte.valueOf((byte) (this.color & 255))}).toLowerCase();
            int i2 = this.gradientColor2;
            if (i2 != 0) {
                str2 = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (i2 >> 16)) & 255), Integer.valueOf(((byte) (this.gradientColor2 >> 8)) & 255), Byte.valueOf((byte) (this.gradientColor2 & 255))}).toLowerCase();
            } else {
                str2 = null;
            }
            int i3 = this.gradientColor3;
            if (i3 != 0) {
                str3 = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (i3 >> 16)) & 255), Integer.valueOf(((byte) (this.gradientColor3 >> 8)) & 255), Byte.valueOf((byte) (this.gradientColor3 & 255))}).toLowerCase();
            }
            if (str == null || str2 == null) {
                if (str != null) {
                    String str4 = lowerCase + "-" + str;
                    if (this.pattern != null) {
                        lowerCase = str4 + "&rotation=" + AndroidUtilities.getWallpaperRotation(this.gradientRotation, true);
                    } else {
                        lowerCase = str4 + "?rotation=" + AndroidUtilities.getWallpaperRotation(this.gradientRotation, true);
                    }
                }
            } else if (str3 != null) {
                lowerCase = lowerCase + "~" + str + "~" + str2 + "~" + str3;
            } else {
                lowerCase = lowerCase + "~" + str + "~" + str2;
            }
            if (this.pattern != null) {
                String str5 = "https://" + MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix + "/bg/" + this.pattern.slug + "?intensity=" + ((int) (this.intensity * 100.0f)) + "&bg_color=" + lowerCase;
                if (!this.motion) {
                    return str5;
                }
                return str5 + "&mode=motion";
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
            getMessagesStorage().getWallpapers();
        } else {
            int[][] iArr = Theme.isCurrentThemeDark() ? defaultColorsDark : defaultColorsLight;
            for (int i = 0; i < iArr.length; i++) {
                if (iArr[i].length == 1) {
                    this.wallPapers.add(new ColorWallpaper("c", iArr[i][0], 0, 45));
                } else {
                    this.wallPapers.add(new ColorWallpaper("c", iArr[i][0], iArr[i][1], iArr[i][2], iArr[i][3]));
                }
            }
            if (this.currentType == 1 && this.patterns.isEmpty()) {
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
                getMessagesStorage().getWallpapers();
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
        Paint paint = new Paint(1);
        this.colorFramePaint = paint;
        paint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
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
                        builder.setTitle(LocaleController.formatPluralString("DeleteBackground", WallpapersListActivity.this.selectedWallPapers.size(), new Object[0]));
                        builder.setMessage(LocaleController.formatString("DeleteChatBackgroundsAlert", NUM, new Object[0]));
                        builder.setPositiveButton(LocaleController.getString("Delete", NUM), new WallpapersListActivity$2$$ExternalSyntheticLambda0(this));
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
                    dialogsActivity.setDelegate(new WallpapersListActivity$2$$ExternalSyntheticLambda3(this));
                    WallpapersListActivity.this.presentFragment(dialogsActivity);
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$2(DialogInterface dialogInterface, int i) {
                AlertDialog unused = WallpapersListActivity.this.progressDialog = new AlertDialog(WallpapersListActivity.this.getParentActivity(), 3);
                WallpapersListActivity.this.progressDialog.setCanCancel(false);
                WallpapersListActivity.this.progressDialog.show();
                new ArrayList();
                int[] iArr = {0};
                for (int i2 = 0; i2 < WallpapersListActivity.this.selectedWallPapers.size(); i2++) {
                    Object valueAt = WallpapersListActivity.this.selectedWallPapers.valueAt(i2);
                    if (valueAt instanceof ColorWallpaper) {
                        ColorWallpaper colorWallpaper = (ColorWallpaper) valueAt;
                        TLRPC$WallPaper tLRPC$WallPaper = colorWallpaper.parentWallpaper;
                        if (tLRPC$WallPaper == null || tLRPC$WallPaper.id >= 0) {
                            valueAt = tLRPC$WallPaper;
                        } else {
                            WallpapersListActivity.this.getMessagesStorage().deleteWallpaper(colorWallpaper.parentWallpaper.id);
                            WallpapersListActivity.this.localWallPapers.remove(colorWallpaper);
                            WallpapersListActivity.this.localDict.remove(colorWallpaper.getHash());
                        }
                    }
                    if (valueAt instanceof TLRPC$WallPaper) {
                        iArr[0] = iArr[0] + 1;
                        TLRPC$WallPaper tLRPC$WallPaper2 = (TLRPC$WallPaper) valueAt;
                        TLRPC$TL_account_saveWallPaper tLRPC$TL_account_saveWallPaper = new TLRPC$TL_account_saveWallPaper();
                        tLRPC$TL_account_saveWallPaper.settings = new TLRPC$TL_wallPaperSettings();
                        tLRPC$TL_account_saveWallPaper.unsave = true;
                        if (valueAt instanceof TLRPC$TL_wallPaperNoFile) {
                            TLRPC$TL_inputWallPaperNoFile tLRPC$TL_inputWallPaperNoFile = new TLRPC$TL_inputWallPaperNoFile();
                            tLRPC$TL_inputWallPaperNoFile.id = tLRPC$WallPaper2.id;
                            tLRPC$TL_account_saveWallPaper.wallpaper = tLRPC$TL_inputWallPaperNoFile;
                        } else {
                            TLRPC$TL_inputWallPaper tLRPC$TL_inputWallPaper = new TLRPC$TL_inputWallPaper();
                            tLRPC$TL_inputWallPaper.id = tLRPC$WallPaper2.id;
                            tLRPC$TL_inputWallPaper.access_hash = tLRPC$WallPaper2.access_hash;
                            tLRPC$TL_account_saveWallPaper.wallpaper = tLRPC$TL_inputWallPaper;
                        }
                        String str = tLRPC$WallPaper2.slug;
                        if (str != null && str.equals(WallpapersListActivity.this.selectedBackgroundSlug)) {
                            String unused2 = WallpapersListActivity.this.selectedBackgroundSlug = Theme.hasWallpaperFromTheme() ? "t" : "d";
                            Theme.getActiveTheme().setOverrideWallpaper((Theme.OverrideWallpaperInfo) null);
                            Theme.reloadWallpaper();
                        }
                        ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(tLRPC$TL_account_saveWallPaper, new WallpapersListActivity$2$$ExternalSyntheticLambda2(this, iArr));
                    }
                }
                if (iArr[0] == 0) {
                    WallpapersListActivity.this.loadWallpapers(true);
                }
                WallpapersListActivity.this.selectedWallPapers.clear();
                WallpapersListActivity.this.actionBar.hideActionMode();
                WallpapersListActivity.this.actionBar.closeSearchField();
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$1(int[] iArr, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                AndroidUtilities.runOnUIThread(new WallpapersListActivity$2$$ExternalSyntheticLambda1(this, iArr));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$0(int[] iArr) {
                iArr[0] = iArr[0] - 1;
                if (iArr[0] == 0) {
                    WallpapersListActivity.this.loadWallpapers(true);
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$3(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                String str;
                ArrayList arrayList2 = arrayList;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < WallpapersListActivity.this.selectedWallPapers.size(); i++) {
                    Object valueAt = WallpapersListActivity.this.selectedWallPapers.valueAt(i);
                    if (valueAt instanceof TLRPC$TL_wallPaper) {
                        str = AndroidUtilities.getWallPaperUrl(valueAt);
                    } else if (valueAt instanceof ColorWallpaper) {
                        str = ((ColorWallpaper) valueAt).getUrl();
                    }
                    if (!TextUtils.isEmpty(str)) {
                        if (sb.length() > 0) {
                            sb.append(10);
                        }
                        sb.append(str);
                    }
                }
                WallpapersListActivity.this.selectedWallPapers.clear();
                WallpapersListActivity.this.actionBar.hideActionMode();
                WallpapersListActivity.this.actionBar.closeSearchField();
                if (arrayList.size() > 1 || ((Long) arrayList2.get(0)).longValue() == UserConfig.getInstance(WallpapersListActivity.this.currentAccount).getClientUserId() || charSequence != null) {
                    DialogsActivity dialogsActivity2 = dialogsActivity;
                    WallpapersListActivity.this.updateRowsSelection();
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        long longValue = ((Long) arrayList2.get(i2)).longValue();
                        if (charSequence != null) {
                            SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(charSequence.toString(), longValue, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                        }
                        if (!TextUtils.isEmpty(sb)) {
                            SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(sb.toString(), longValue, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                        }
                    }
                    dialogsActivity.finishFragment();
                    return;
                }
                long longValue2 = ((Long) arrayList2.get(0)).longValue();
                Bundle bundle = new Bundle();
                bundle.putBoolean("scrollToTopOnResume", true);
                if (DialogObject.isEncryptedDialog(longValue2)) {
                    bundle.putInt("enc_id", DialogObject.getEncryptedChatId(longValue2));
                } else {
                    if (DialogObject.isUserDialog(longValue2)) {
                        bundle.putLong("user_id", longValue2);
                    } else if (DialogObject.isChatDialog(longValue2)) {
                        bundle.putLong("chat_id", -longValue2);
                    }
                    if (!MessagesController.getInstance(WallpapersListActivity.this.currentAccount).checkCanOpenChat(bundle, dialogsActivity)) {
                        return;
                    }
                }
                NotificationCenter.getInstance(WallpapersListActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                WallpapersListActivity.this.presentFragment(new ChatActivity(bundle), true);
                SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(sb.toString(), longValue2, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
            }
        });
        if (this.currentType == 0) {
            ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
            this.searchItem = actionBarMenuItemSearchListener;
            actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("SearchBackgrounds", NUM));
            ActionBarMenu createActionMode = this.actionBar.createActionMode(false, (String) null);
            createActionMode.setBackgroundColor(Theme.getColor("actionBarDefault"));
            this.actionBar.setItemsColor(Theme.getColor("actionBarDefaultIcon"), true);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarDefaultSelector"), true);
            NumberTextView numberTextView = new NumberTextView(createActionMode.getContext());
            this.selectedMessagesCountTextView = numberTextView;
            numberTextView.setTextSize(18);
            this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.selectedMessagesCountTextView.setTextColor(Theme.getColor("actionBarDefaultIcon"));
            this.selectedMessagesCountTextView.setOnTouchListener(WallpapersListActivity$$ExternalSyntheticLambda1.INSTANCE);
            createActionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 65, 0, 0, 0));
            this.actionModeViews.add(createActionMode.addItemWithWidth(3, NUM, AndroidUtilities.dp(54.0f), (CharSequence) LocaleController.getString("Forward", NUM)));
            this.actionModeViews.add(createActionMode.addItemWithWidth(4, NUM, AndroidUtilities.dp(54.0f), (CharSequence) LocaleController.getString("Delete", NUM)));
            this.selectedWallPapers.clear();
        }
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        AnonymousClass4 r4 = new RecyclerListView(context) {
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
                    if (r2 == r1) goto L_0x006a
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
                L_0x006a:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpapersListActivity.AnonymousClass4.onDraw(android.graphics.Canvas):void");
            }
        };
        this.listView = r4;
        r4.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass5 r42 = new LinearLayoutManager(this, context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r42;
        recyclerListView.setLayoutManager(r42);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context);
        this.listAdapter = listAdapter2;
        recyclerListView2.setAdapter(listAdapter2);
        this.searchAdapter = new SearchAdapter(context);
        this.listView.setGlowColor(Theme.getColor("avatar_backgroundActionBarBlue"));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new WallpapersListActivity$$ExternalSyntheticLambda7(this));
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
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.searchEmptyView = emptyTextProgressView;
        emptyTextProgressView.setVisibility(8);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.searchEmptyView.setText(LocaleController.getString("NoResult", NUM));
        this.listView.setEmptyView(this.searchEmptyView);
        frameLayout2.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f));
        updateRows();
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(View view, int i) {
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
                builder.setPositiveButton(LocaleController.getString("Reset", NUM), new WallpapersListActivity$$ExternalSyntheticLambda0(this));
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(DialogInterface dialogInterface, int i) {
        if (this.actionBar.isActionModeShowed()) {
            this.selectedWallPapers.clear();
            this.actionBar.hideActionMode();
            updateRowsSelection();
        }
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        this.progressDialog = alertDialog;
        alertDialog.setCanCancel(false);
        this.progressDialog.show();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_resetWallPapers(), new WallpapersListActivity$$ExternalSyntheticLambda5(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1() {
        loadWallpapers(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new WallpapersListActivity$$ExternalSyntheticLambda2(this));
    }

    public void onResume() {
        super.onResume();
        MessagesController.getGlobalMainSettings();
        Theme.OverrideWallpaperInfo overrideWallpaperInfo = Theme.getActiveTheme().overrideWallpaper;
        if (overrideWallpaperInfo != null) {
            this.selectedBackgroundSlug = overrideWallpaperInfo.slug;
            this.selectedColor = overrideWallpaperInfo.color;
            this.selectedGradientColor1 = overrideWallpaperInfo.gradientColor1;
            this.selectedGradientColor2 = overrideWallpaperInfo.gradientColor2;
            this.selectedGradientColor3 = overrideWallpaperInfo.gradientColor3;
            this.selectedGradientRotation = overrideWallpaperInfo.rotation;
            this.selectedIntensity = overrideWallpaperInfo.intensity;
            this.selectedBackgroundMotion = overrideWallpaperInfo.isMotion;
            this.selectedBackgroundBlurred = overrideWallpaperInfo.isBlurred;
        } else {
            this.selectedBackgroundSlug = Theme.hasWallpaperFromTheme() ? "t" : "d";
            this.selectedColor = 0;
            this.selectedGradientColor1 = 0;
            this.selectedGradientColor2 = 0;
            this.selectedGradientColor3 = 0;
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
        Object obj2 = obj instanceof ColorWallpaper ? ((ColorWallpaper) obj).parentWallpaper : obj;
        if (this.actionBar.isActionModeShowed() || getParentActivity() == null || !(obj2 instanceof TLRPC$WallPaper)) {
            return false;
        }
        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        this.selectedWallPapers.put(((TLRPC$WallPaper) obj2).id, obj);
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v19, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v20, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onItemClick(org.telegram.ui.Cells.WallpaperCell r18, java.lang.Object r19, int r20) {
        /*
            r17 = this;
            r0 = r17
            r1 = r19
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            boolean r2 = r2.isActionModeShowed()
            r3 = 0
            r4 = 1
            if (r2 == 0) goto L_0x006c
            boolean r2 = r1 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r2 == 0) goto L_0x0018
            r2 = r1
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r2 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r2
            org.telegram.tgnet.TLRPC$WallPaper r2 = r2.parentWallpaper
            goto L_0x0019
        L_0x0018:
            r2 = r1
        L_0x0019:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$WallPaper
            if (r5 != 0) goto L_0x001e
            return
        L_0x001e:
            org.telegram.tgnet.TLRPC$WallPaper r2 = (org.telegram.tgnet.TLRPC$WallPaper) r2
            android.util.LongSparseArray<java.lang.Object> r5 = r0.selectedWallPapers
            long r6 = r2.id
            int r5 = r5.indexOfKey(r6)
            if (r5 < 0) goto L_0x0032
            android.util.LongSparseArray<java.lang.Object> r1 = r0.selectedWallPapers
            long r5 = r2.id
            r1.remove(r5)
            goto L_0x0039
        L_0x0032:
            android.util.LongSparseArray<java.lang.Object> r5 = r0.selectedWallPapers
            long r6 = r2.id
            r5.put(r6, r1)
        L_0x0039:
            android.util.LongSparseArray<java.lang.Object> r1 = r0.selectedWallPapers
            int r1 = r1.size()
            if (r1 != 0) goto L_0x0047
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r1.hideActionMode()
            goto L_0x0052
        L_0x0047:
            org.telegram.ui.Components.NumberTextView r1 = r0.selectedMessagesCountTextView
            android.util.LongSparseArray<java.lang.Object> r5 = r0.selectedWallPapers
            int r5 = r5.size()
            r1.setNumber(r5, r4)
        L_0x0052:
            r0.scrolling = r3
            android.util.LongSparseArray<java.lang.Object> r1 = r0.selectedWallPapers
            long r5 = r2.id
            int r1 = r1.indexOfKey(r5)
            if (r1 < 0) goto L_0x0064
            r1 = r18
            r2 = r20
            r3 = 1
            goto L_0x0068
        L_0x0064:
            r1 = r18
            r2 = r20
        L_0x0068:
            r1.setChecked(r2, r3, r4)
            goto L_0x00cd
        L_0x006c:
            java.lang.String r2 = r0.getWallPaperSlug(r1)
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            if (r5 == 0) goto L_0x00a4
            r5 = r1
            org.telegram.tgnet.TLRPC$TL_wallPaper r5 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r5
            boolean r6 = r5.pattern
            if (r6 == 0) goto L_0x00a4
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r8 = r5.slug
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r5.settings
            int r9 = r6.background_color
            int r10 = r6.second_background_color
            int r11 = r6.third_background_color
            int r12 = r6.fourth_background_color
            int r6 = r6.rotation
            int r13 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r6, r3)
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r5.settings
            int r7 = r6.intensity
            float r7 = (float) r7
            r14 = 1120403456(0x42CLASSNAME, float:100.0)
            float r14 = r7 / r14
            boolean r15 = r6.motion
            r16 = 0
            r7 = r1
            r7.<init>(r8, r9, r10, r11, r12, r13, r14, r15, r16)
            r1.pattern = r5
            r1.parentWallpaper = r5
        L_0x00a4:
            org.telegram.ui.ThemePreviewActivity r5 = new org.telegram.ui.ThemePreviewActivity
            r6 = 0
            r5.<init>(r1, r6, r4, r3)
            int r1 = r0.currentType
            if (r1 != r4) goto L_0x00b6
            org.telegram.ui.WallpapersListActivity$$ExternalSyntheticLambda8 r1 = new org.telegram.ui.WallpapersListActivity$$ExternalSyntheticLambda8
            r1.<init>(r0)
            r5.setDelegate(r1)
        L_0x00b6:
            java.lang.String r1 = r0.selectedBackgroundSlug
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x00c5
            boolean r1 = r0.selectedBackgroundBlurred
            boolean r2 = r0.selectedBackgroundMotion
            r5.setInitialModes(r1, r2)
        L_0x00c5:
            java.util.ArrayList<java.lang.Object> r1 = r0.patterns
            r5.setPatterns(r1)
            r0.presentFragment(r5)
        L_0x00cd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpapersListActivity.onItemClick(org.telegram.ui.Cells.WallpaperCell, java.lang.Object, int):void");
    }

    private String getWallPaperSlug(Object obj) {
        if (obj instanceof TLRPC$TL_wallPaper) {
            return ((TLRPC$TL_wallPaper) obj).slug;
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
        ColorWallpaper colorWallpaper;
        TLRPC$WallPaperSettings tLRPC$WallPaperSettings;
        int i3;
        TLRPC$WallPaperSettings tLRPC$WallPaperSettings2;
        TLRPC$WallPaperSettings tLRPC$WallPaperSettings3;
        int i4 = i;
        if (i4 == NotificationCenter.wallpapersDidLoad) {
            ArrayList arrayList = objArr[0];
            this.patterns.clear();
            this.patternsDict.clear();
            if (this.currentType != 1) {
                this.wallPapers.clear();
                this.localWallPapers.clear();
                this.localDict.clear();
                this.allWallPapers.clear();
                this.allWallPapersDict.clear();
                this.allWallPapers.addAll(arrayList);
            }
            int size = arrayList.size();
            ArrayList arrayList2 = null;
            for (int i5 = 0; i5 < size; i5++) {
                TLRPC$WallPaper tLRPC$WallPaper = (TLRPC$WallPaper) arrayList.get(i5);
                if (!"fqv01SQemVIBAAAApND8LDRUhRU".equals(tLRPC$WallPaper.slug)) {
                    if (tLRPC$WallPaper instanceof TLRPC$TL_wallPaper) {
                        TLRPC$Document tLRPC$Document = tLRPC$WallPaper.document;
                        if (!(tLRPC$Document instanceof TLRPC$TL_documentEmpty)) {
                            if (tLRPC$WallPaper.pattern && tLRPC$Document != null && !this.patternsDict.containsKey(Long.valueOf(tLRPC$Document.id))) {
                                this.patterns.add(tLRPC$WallPaper);
                                this.patternsDict.put(Long.valueOf(tLRPC$WallPaper.document.id), tLRPC$WallPaper);
                            }
                            this.allWallPapersDict.put(tLRPC$WallPaper.slug, tLRPC$WallPaper);
                            if (this.currentType != 1 && ((!tLRPC$WallPaper.pattern || !((tLRPC$WallPaperSettings3 = tLRPC$WallPaper.settings) == null || tLRPC$WallPaperSettings3.background_color == 0)) && (Theme.isCurrentThemeDark() || (tLRPC$WallPaperSettings2 = tLRPC$WallPaper.settings) == null || tLRPC$WallPaperSettings2.intensity >= 0))) {
                                this.wallPapers.add(tLRPC$WallPaper);
                            }
                        }
                    }
                    TLRPC$WallPaperSettings tLRPC$WallPaperSettings4 = tLRPC$WallPaper.settings;
                    int i6 = tLRPC$WallPaperSettings4.background_color;
                    if (i6 != 0) {
                        int i7 = tLRPC$WallPaperSettings4.second_background_color;
                        if (i7 == 0 || (i3 = tLRPC$WallPaperSettings4.third_background_color) == 0) {
                            colorWallpaper = new ColorWallpaper((String) null, i6, i7, tLRPC$WallPaperSettings4.rotation);
                        } else {
                            colorWallpaper = new ColorWallpaper((String) null, i6, i7, i3, tLRPC$WallPaperSettings4.fourth_background_color);
                        }
                        colorWallpaper.slug = tLRPC$WallPaper.slug;
                        TLRPC$WallPaperSettings tLRPC$WallPaperSettings5 = tLRPC$WallPaper.settings;
                        colorWallpaper.intensity = ((float) tLRPC$WallPaperSettings5.intensity) / 100.0f;
                        colorWallpaper.gradientRotation = AndroidUtilities.getWallpaperRotation(tLRPC$WallPaperSettings5.rotation, false);
                        colorWallpaper.parentWallpaper = tLRPC$WallPaper;
                        if (tLRPC$WallPaper.id < 0) {
                            String hash = colorWallpaper.getHash();
                            if (this.localDict.containsKey(hash)) {
                                if (arrayList2 == null) {
                                    arrayList2 = new ArrayList();
                                }
                                arrayList2.add(tLRPC$WallPaper);
                            } else {
                                this.localWallPapers.add(colorWallpaper);
                                this.localDict.put(hash, colorWallpaper);
                            }
                        }
                        if (Theme.isCurrentThemeDark() || (tLRPC$WallPaperSettings = tLRPC$WallPaper.settings) == null || tLRPC$WallPaperSettings.intensity >= 0) {
                            this.wallPapers.add(colorWallpaper);
                        }
                    }
                }
            }
            if (arrayList2 != null) {
                int size2 = arrayList2.size();
                for (int i8 = 0; i8 < size2; i8++) {
                    getMessagesStorage().deleteWallpaper(((TLRPC$WallPaper) arrayList2.get(i8)).id);
                }
            }
            this.selectedBackgroundSlug = Theme.getSelectedBackgroundSlug();
            fillWallpapersWithCustom();
            loadWallpapers(false);
        } else if (i4 == NotificationCenter.didSetNewWallpapper) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
            }
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.closeSearchField();
            }
        } else if (i4 == NotificationCenter.wallpapersNeedReload) {
            getMessagesStorage().getWallpapers();
        }
    }

    /* access modifiers changed from: private */
    public void loadWallpapers(boolean z) {
        long j = 0;
        if (!z) {
            int size = this.allWallPapers.size();
            long j2 = 0;
            for (int i = 0; i < size; i++) {
                Object obj = this.allWallPapers.get(i);
                if (obj instanceof TLRPC$WallPaper) {
                    long j3 = ((TLRPC$WallPaper) obj).id;
                    if (j3 >= 0) {
                        j2 = MediaDataController.calcHash(j2, j3);
                    }
                }
            }
            j = j2;
        }
        TLRPC$TL_account_getWallPapers tLRPC$TL_account_getWallPapers = new TLRPC$TL_account_getWallPapers();
        tLRPC$TL_account_getWallPapers.hash = j;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_getWallPapers, new WallpapersListActivity$$ExternalSyntheticLambda6(this, z)), this.classGuid);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadWallpapers$6(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new WallpapersListActivity$$ExternalSyntheticLambda3(this, tLObject, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadWallpapers$5(TLObject tLObject, boolean z) {
        ColorWallpaper colorWallpaper;
        int i;
        TLRPC$WallPaperSettings tLRPC$WallPaperSettings;
        TLRPC$WallPaperSettings tLRPC$WallPaperSettings2;
        TLRPC$WallPaperSettings tLRPC$WallPaperSettings3;
        TLRPC$Document tLRPC$Document;
        if (tLObject instanceof TLRPC$TL_account_wallPapers) {
            TLRPC$TL_account_wallPapers tLRPC$TL_account_wallPapers = (TLRPC$TL_account_wallPapers) tLObject;
            this.patterns.clear();
            this.patternsDict.clear();
            if (this.currentType != 1) {
                this.wallPapers.clear();
                this.allWallPapersDict.clear();
                this.allWallPapers.clear();
                this.allWallPapers.addAll(tLRPC$TL_account_wallPapers.wallpapers);
                this.wallPapers.addAll(this.localWallPapers);
            }
            int size = tLRPC$TL_account_wallPapers.wallpapers.size();
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC$WallPaper tLRPC$WallPaper = tLRPC$TL_account_wallPapers.wallpapers.get(i2);
                if (!"fqv01SQemVIBAAAApND8LDRUhRU".equals(tLRPC$WallPaper.slug)) {
                    if ((tLRPC$WallPaper instanceof TLRPC$TL_wallPaper) && !(tLRPC$WallPaper.document instanceof TLRPC$TL_documentEmpty)) {
                        this.allWallPapersDict.put(tLRPC$WallPaper.slug, tLRPC$WallPaper);
                        if (tLRPC$WallPaper.pattern && (tLRPC$Document = tLRPC$WallPaper.document) != null && !this.patternsDict.containsKey(Long.valueOf(tLRPC$Document.id))) {
                            this.patterns.add(tLRPC$WallPaper);
                            this.patternsDict.put(Long.valueOf(tLRPC$WallPaper.document.id), tLRPC$WallPaper);
                        }
                        if (this.currentType != 1 && ((!tLRPC$WallPaper.pattern || !((tLRPC$WallPaperSettings3 = tLRPC$WallPaper.settings) == null || tLRPC$WallPaperSettings3.background_color == 0)) && (Theme.isCurrentThemeDark() || (tLRPC$WallPaperSettings2 = tLRPC$WallPaper.settings) == null || tLRPC$WallPaperSettings2.intensity >= 0))) {
                            this.wallPapers.add(tLRPC$WallPaper);
                        }
                    } else if (tLRPC$WallPaper.settings.background_color != 0 && (Theme.isCurrentThemeDark() || (tLRPC$WallPaperSettings = tLRPC$WallPaper.settings) == null || tLRPC$WallPaperSettings.intensity >= 0)) {
                        TLRPC$WallPaperSettings tLRPC$WallPaperSettings4 = tLRPC$WallPaper.settings;
                        int i3 = tLRPC$WallPaperSettings4.second_background_color;
                        if (i3 == 0 || (i = tLRPC$WallPaperSettings4.third_background_color) == 0) {
                            colorWallpaper = new ColorWallpaper((String) null, tLRPC$WallPaperSettings4.background_color, i3, tLRPC$WallPaperSettings4.rotation);
                        } else {
                            colorWallpaper = new ColorWallpaper((String) null, tLRPC$WallPaperSettings4.background_color, i3, i, tLRPC$WallPaperSettings4.fourth_background_color);
                        }
                        colorWallpaper.slug = tLRPC$WallPaper.slug;
                        TLRPC$WallPaperSettings tLRPC$WallPaperSettings5 = tLRPC$WallPaper.settings;
                        colorWallpaper.intensity = ((float) tLRPC$WallPaperSettings5.intensity) / 100.0f;
                        colorWallpaper.gradientRotation = AndroidUtilities.getWallpaperRotation(tLRPC$WallPaperSettings5.rotation, false);
                        colorWallpaper.parentWallpaper = tLRPC$WallPaper;
                        this.wallPapers.add(colorWallpaper);
                    }
                }
            }
            fillWallpapersWithCustom();
            getMessagesStorage().putWallpapers(tLRPC$TL_account_wallPapers.wallpapers, 1);
        }
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            if (!z) {
                this.listView.smoothScrollToPosition(0);
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v0, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v0, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v28, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v40, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v10, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v12, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v13, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v14, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x019f, code lost:
        r1 = ((org.telegram.ui.WallpapersListActivity.ColorWallpaper) r4).parentWallpaper;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void fillWallpapersWithCustom() {
        /*
            r22 = this;
            r7 = r22
            int r0 = r7.currentType
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            org.telegram.messenger.MessagesController.getGlobalMainSettings()
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = r7.addedColorWallpaper
            r8 = 0
            if (r0 == 0) goto L_0x0016
            java.util.ArrayList<java.lang.Object> r1 = r7.wallPapers
            r1.remove(r0)
            r7.addedColorWallpaper = r8
        L_0x0016:
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = r7.addedFileWallpaper
            if (r0 == 0) goto L_0x0021
            java.util.ArrayList<java.lang.Object> r1 = r7.wallPapers
            r1.remove(r0)
            r7.addedFileWallpaper = r8
        L_0x0021:
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = r7.catsWallpaper
            if (r0 != 0) goto L_0x0041
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            r3 = -2368069(0xffffffffffdbddbb, float:NaN)
            r4 = -9722489(0xffffffffff6ba587, float:-3.1322805E38)
            r5 = -2762611(0xffffffffffd5d88d, float:NaN)
            r6 = -7817084(0xfffffffffvar_b884, float:NaN)
            java.lang.String r2 = "d"
            r1 = r0
            r1.<init>(r2, r3, r4, r5, r6)
            r7.catsWallpaper = r0
            r1 = 1051595899(0x3eae147b, float:0.34)
            r0.intensity = r1
            goto L_0x0046
        L_0x0041:
            java.util.ArrayList<java.lang.Object> r1 = r7.wallPapers
            r1.remove(r0)
        L_0x0046:
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = r7.themeWallpaper
            if (r0 == 0) goto L_0x004f
            java.util.ArrayList<java.lang.Object> r1 = r7.wallPapers
            r1.remove(r0)
        L_0x004f:
            java.util.ArrayList<java.lang.Object> r0 = r7.wallPapers
            int r0 = r0.size()
            r9 = 0
            r1 = 0
        L_0x0057:
            r2 = 981668463(0x3a83126f, float:0.001)
            r3 = 1120403456(0x42CLASSNAME, float:100.0)
            java.lang.String r10 = "c"
            if (r1 >= r0) goto L_0x011e
            java.util.ArrayList<java.lang.Object> r4 = r7.wallPapers
            java.lang.Object r4 = r4.get(r1)
            boolean r5 = r4 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r5 == 0) goto L_0x00b0
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r4 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r4
            java.lang.String r5 = r4.slug
            if (r5 == 0) goto L_0x007a
            java.util.HashMap<java.lang.String, java.lang.Object> r6 = r7.allWallPapersDict
            java.lang.Object r5 = r6.get(r5)
            org.telegram.tgnet.TLRPC$TL_wallPaper r5 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r5
            r4.pattern = r5
        L_0x007a:
            java.lang.String r5 = r4.slug
            boolean r5 = r10.equals(r5)
            if (r5 != 0) goto L_0x008e
            java.lang.String r5 = r4.slug
            if (r5 == 0) goto L_0x008e
            java.lang.String r6 = r7.selectedBackgroundSlug
            boolean r5 = android.text.TextUtils.equals(r6, r5)
            if (r5 == 0) goto L_0x011a
        L_0x008e:
            int r5 = r7.selectedColor
            int r6 = r4.color
            if (r5 != r6) goto L_0x011a
            int r5 = r7.selectedGradientColor1
            int r6 = r4.gradientColor1
            if (r5 != r6) goto L_0x011a
            int r6 = r7.selectedGradientColor2
            int r11 = r4.gradientColor2
            if (r6 != r11) goto L_0x011a
            int r6 = r7.selectedGradientColor3
            int r11 = r4.gradientColor3
            if (r6 != r11) goto L_0x011a
            if (r5 == 0) goto L_0x011f
            int r5 = r7.selectedGradientRotation
            int r6 = r4.gradientRotation
            if (r5 != r6) goto L_0x011a
            goto L_0x011f
        L_0x00b0:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            if (r5 == 0) goto L_0x011a
            org.telegram.tgnet.TLRPC$TL_wallPaper r4 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r4
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r4.settings
            if (r5 == 0) goto L_0x011a
            java.lang.String r5 = r7.selectedBackgroundSlug
            java.lang.String r6 = r4.slug
            boolean r5 = android.text.TextUtils.equals(r5, r6)
            if (r5 == 0) goto L_0x011a
            int r5 = r7.selectedColor
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r4.settings
            int r6 = r6.background_color
            int r6 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r6)
            if (r5 != r6) goto L_0x011a
            int r5 = r7.selectedGradientColor1
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r4.settings
            int r6 = r6.second_background_color
            int r6 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r6)
            if (r5 != r6) goto L_0x011a
            int r5 = r7.selectedGradientColor2
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r4.settings
            int r6 = r6.third_background_color
            int r6 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r6)
            if (r5 != r6) goto L_0x011a
            int r5 = r7.selectedGradientColor3
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r4.settings
            int r6 = r6.fourth_background_color
            int r6 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r6)
            if (r5 != r6) goto L_0x011a
            int r5 = r7.selectedGradientColor1
            if (r5 == 0) goto L_0x0104
            int r5 = r7.selectedGradientRotation
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r4.settings
            int r6 = r6.rotation
            int r6 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r6, r9)
            if (r5 != r6) goto L_0x011a
        L_0x0104:
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r4.settings
            int r5 = r5.intensity
            float r5 = (float) r5
            float r5 = r5 / r3
            float r5 = org.telegram.ui.ActionBar.Theme.getThemeIntensity(r5)
            float r6 = r7.selectedIntensity
            float r5 = r5 - r6
            float r5 = java.lang.Math.abs(r5)
            int r5 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r5 > 0) goto L_0x011a
            goto L_0x011f
        L_0x011a:
            int r1 = r1 + 1
            goto L_0x0057
        L_0x011e:
            r4 = r8
        L_0x011f:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$WallPaper
            if (r0 == 0) goto L_0x0199
            r0 = r4
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r0
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r1 = r1.overrideWallpaper
            org.telegram.tgnet.TLRPC$WallPaperSettings r1 = r0.settings
            if (r1 == 0) goto L_0x018e
            if (r1 == 0) goto L_0x018a
            int r5 = r7.selectedColor
            int r1 = r1.background_color
            int r1 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r1)
            if (r5 != r1) goto L_0x018e
            int r1 = r7.selectedGradientColor1
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r0.settings
            int r5 = r5.second_background_color
            int r5 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r5)
            if (r1 != r5) goto L_0x018e
            int r1 = r7.selectedGradientColor2
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r0.settings
            int r5 = r5.third_background_color
            int r5 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r5)
            if (r1 != r5) goto L_0x018e
            int r1 = r7.selectedGradientColor3
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r0.settings
            int r5 = r5.fourth_background_color
            int r5 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r5)
            if (r1 != r5) goto L_0x018e
            int r1 = r7.selectedGradientColor1
            if (r1 == 0) goto L_0x018a
            int r1 = r7.selectedGradientColor2
            if (r1 != 0) goto L_0x018a
            int r1 = r7.selectedGradientRotation
            org.telegram.tgnet.TLRPC$WallPaperSettings r5 = r0.settings
            int r5 = r5.rotation
            int r5 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r5, r9)
            if (r1 == r5) goto L_0x018a
            org.telegram.tgnet.TLRPC$WallPaperSettings r1 = r0.settings
            int r1 = r1.intensity
            float r1 = (float) r1
            float r1 = r1 / r3
            float r1 = org.telegram.ui.ActionBar.Theme.getThemeIntensity(r1)
            float r3 = r7.selectedIntensity
            float r1 = r1 - r3
            float r1 = java.lang.Math.abs(r1)
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x018a
            goto L_0x018e
        L_0x018a:
            java.lang.String r1 = r7.selectedBackgroundSlug
            r2 = r8
            goto L_0x0192
        L_0x018e:
            java.lang.String r1 = ""
            r2 = r0
            r4 = r8
        L_0x0192:
            long r5 = r0.id
            r11 = r2
            r12 = r4
            r3 = r5
            r5 = r1
            goto L_0x01af
        L_0x0199:
            java.lang.String r0 = r7.selectedBackgroundSlug
            boolean r1 = r4 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r1 == 0) goto L_0x01a9
            r1 = r4
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r1
            org.telegram.tgnet.TLRPC$WallPaper r1 = r1.parentWallpaper
            if (r1 == 0) goto L_0x01a9
            long r1 = r1.id
            goto L_0x01ab
        L_0x01a9:
            r1 = 0
        L_0x01ab:
            r5 = r0
            r12 = r4
            r11 = r8
            r3 = r1
        L_0x01af:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getCurrentTheme()
            boolean r6 = r0.isDark()
            java.util.ArrayList<java.lang.Object> r0 = r7.wallPapers     // Catch:{ Exception -> 0x01c5 }
            org.telegram.ui.WallpapersListActivity$$ExternalSyntheticLambda4 r13 = new org.telegram.ui.WallpapersListActivity$$ExternalSyntheticLambda4     // Catch:{ Exception -> 0x01c5 }
            r1 = r13
            r2 = r22
            r1.<init>(r2, r3, r5, r6)     // Catch:{ Exception -> 0x01c5 }
            java.util.Collections.sort(r0, r13)     // Catch:{ Exception -> 0x01c5 }
            goto L_0x01c9
        L_0x01c5:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01c9:
            boolean r0 = org.telegram.ui.ActionBar.Theme.hasWallpaperFromTheme()
            if (r0 == 0) goto L_0x01eb
            boolean r0 = org.telegram.ui.ActionBar.Theme.isThemeWallpaperPublic()
            if (r0 != 0) goto L_0x01eb
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = r7.themeWallpaper
            if (r0 != 0) goto L_0x01e3
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = new org.telegram.ui.WallpapersListActivity$FileWallpaper
            java.lang.String r1 = "t"
            r2 = -2
            r0.<init>((java.lang.String) r1, (int) r2, (int) r2)
            r7.themeWallpaper = r0
        L_0x01e3:
            java.util.ArrayList<java.lang.Object> r0 = r7.wallPapers
            org.telegram.ui.WallpapersListActivity$FileWallpaper r1 = r7.themeWallpaper
            r0.add(r9, r1)
            goto L_0x01ed
        L_0x01eb:
            r7.themeWallpaper = r8
        L_0x01ed:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            java.lang.String r1 = r7.selectedBackgroundSlug
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            java.lang.String r2 = "d"
            if (r1 != 0) goto L_0x0247
            java.lang.String r1 = r7.selectedBackgroundSlug
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x0206
            if (r12 != 0) goto L_0x0206
            goto L_0x0247
        L_0x0206:
            if (r12 != 0) goto L_0x02fc
            int r0 = r7.selectedColor
            if (r0 == 0) goto L_0x02fc
            java.lang.String r0 = r7.selectedBackgroundSlug
            boolean r0 = r10.equals(r0)
            if (r0 == 0) goto L_0x02fc
            int r13 = r7.selectedGradientColor1
            if (r13 == 0) goto L_0x0231
            int r14 = r7.selectedGradientColor2
            if (r14 == 0) goto L_0x0231
            int r15 = r7.selectedGradientColor3
            if (r15 == 0) goto L_0x0231
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r11 = r7.selectedBackgroundSlug
            int r12 = r7.selectedColor
            r10 = r0
            r10.<init>(r11, r12, r13, r14, r15)
            r7.addedColorWallpaper = r0
            int r1 = r7.selectedGradientRotation
            r0.gradientRotation = r1
            goto L_0x023e
        L_0x0231:
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r1 = r7.selectedBackgroundSlug
            int r4 = r7.selectedColor
            int r5 = r7.selectedGradientRotation
            r0.<init>(r1, r4, r13, r5)
            r7.addedColorWallpaper = r0
        L_0x023e:
            java.util.ArrayList<java.lang.Object> r0 = r7.wallPapers
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = r7.addedColorWallpaper
            r0.add(r9, r1)
            goto L_0x02fc
        L_0x0247:
            java.lang.String r1 = r7.selectedBackgroundSlug
            boolean r1 = r10.equals(r1)
            if (r1 != 0) goto L_0x028f
            int r14 = r7.selectedColor
            if (r14 == 0) goto L_0x028f
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r1 = r0.overrideWallpaper
            if (r1 == 0) goto L_0x02fc
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r13 = r7.selectedBackgroundSlug
            int r15 = r7.selectedGradientColor1
            int r4 = r7.selectedGradientColor2
            int r5 = r7.selectedGradientColor3
            int r6 = r7.selectedGradientRotation
            float r8 = r7.selectedIntensity
            boolean r10 = r7.selectedBackgroundMotion
            java.io.File r12 = new java.io.File
            java.io.File r3 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r0 = r0.overrideWallpaper
            java.lang.String r0 = r0.fileName
            r12.<init>(r3, r0)
            r0 = r12
            r12 = r1
            r16 = r4
            r17 = r5
            r18 = r6
            r19 = r8
            r20 = r10
            r21 = r0
            r12.<init>(r13, r14, r15, r16, r17, r18, r19, r20, r21)
            r7.addedColorWallpaper = r1
            r1.pattern = r11
            java.util.ArrayList<java.lang.Object> r0 = r7.wallPapers
            r0.add(r9, r1)
            goto L_0x02fc
        L_0x028f:
            int r12 = r7.selectedColor
            if (r12 == 0) goto L_0x02bf
            int r13 = r7.selectedGradientColor1
            if (r13 == 0) goto L_0x02ac
            int r14 = r7.selectedGradientColor2
            if (r14 == 0) goto L_0x02ac
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r11 = r7.selectedBackgroundSlug
            int r15 = r7.selectedGradientColor3
            r10 = r0
            r10.<init>(r11, r12, r13, r14, r15)
            r7.addedColorWallpaper = r0
            int r1 = r7.selectedGradientRotation
            r0.gradientRotation = r1
            goto L_0x02b7
        L_0x02ac:
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r1 = r7.selectedBackgroundSlug
            int r3 = r7.selectedGradientRotation
            r0.<init>(r1, r12, r13, r3)
            r7.addedColorWallpaper = r0
        L_0x02b7:
            java.util.ArrayList<java.lang.Object> r0 = r7.wallPapers
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = r7.addedColorWallpaper
            r0.add(r9, r1)
            goto L_0x02fc
        L_0x02bf:
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r1 = r0.overrideWallpaper
            if (r1 == 0) goto L_0x02fc
            java.util.HashMap<java.lang.String, java.lang.Object> r1 = r7.allWallPapersDict
            java.lang.String r3 = r7.selectedBackgroundSlug
            boolean r1 = r1.containsKey(r3)
            if (r1 != 0) goto L_0x02fc
            org.telegram.ui.WallpapersListActivity$FileWallpaper r1 = new org.telegram.ui.WallpapersListActivity$FileWallpaper
            java.lang.String r3 = r7.selectedBackgroundSlug
            java.io.File r4 = new java.io.File
            java.io.File r5 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r6 = r0.overrideWallpaper
            java.lang.String r6 = r6.fileName
            r4.<init>(r5, r6)
            java.io.File r5 = new java.io.File
            java.io.File r6 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r0 = r0.overrideWallpaper
            java.lang.String r0 = r0.originalFileName
            r5.<init>(r6, r0)
            r1.<init>((java.lang.String) r3, (java.io.File) r4, (java.io.File) r5)
            r7.addedFileWallpaper = r1
            java.util.ArrayList<java.lang.Object> r0 = r7.wallPapers
            org.telegram.ui.WallpapersListActivity$FileWallpaper r3 = r7.themeWallpaper
            if (r3 == 0) goto L_0x02f8
            r3 = 1
            goto L_0x02f9
        L_0x02f8:
            r3 = 0
        L_0x02f9:
            r0.add(r3, r1)
        L_0x02fc:
            java.lang.String r0 = r7.selectedBackgroundSlug
            boolean r0 = r2.equals(r0)
            if (r0 != 0) goto L_0x0316
            java.util.ArrayList<java.lang.Object> r0 = r7.wallPapers
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x030d
            goto L_0x0316
        L_0x030d:
            java.util.ArrayList<java.lang.Object> r0 = r7.wallPapers
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = r7.catsWallpaper
            r2 = 1
            r0.add(r2, r1)
            goto L_0x031d
        L_0x0316:
            java.util.ArrayList<java.lang.Object> r0 = r7.wallPapers
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = r7.catsWallpaper
            r0.add(r9, r1)
        L_0x031d:
            r22.updateRows()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpapersListActivity.fillWallpapersWithCustom():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ int lambda$fillWallpapersWithCustom$7(long j, String str, boolean z, Object obj, Object obj2) {
        if (obj instanceof ColorWallpaper) {
            obj = ((ColorWallpaper) obj).parentWallpaper;
        }
        if (obj2 instanceof ColorWallpaper) {
            obj2 = ((ColorWallpaper) obj2).parentWallpaper;
        }
        if (!(obj instanceof TLRPC$WallPaper) || !(obj2 instanceof TLRPC$WallPaper)) {
            return 0;
        }
        TLRPC$WallPaper tLRPC$WallPaper = (TLRPC$WallPaper) obj;
        TLRPC$WallPaper tLRPC$WallPaper2 = (TLRPC$WallPaper) obj2;
        if (j != 0) {
            if (tLRPC$WallPaper.id == j) {
                return -1;
            }
            if (tLRPC$WallPaper2.id == j) {
                return 1;
            }
        } else if (str.equals(tLRPC$WallPaper.slug)) {
            return -1;
        } else {
            if (str.equals(tLRPC$WallPaper2.slug)) {
                return 1;
            }
        }
        if (!z) {
            if ("qeZWES8rGVIEAAAARfWlK1lnfiI".equals(tLRPC$WallPaper.slug)) {
                return -1;
            }
            if ("qeZWES8rGVIEAAAARfWlK1lnfiI".equals(tLRPC$WallPaper2.slug)) {
                return 1;
            }
        }
        int indexOf = this.allWallPapers.indexOf(tLRPC$WallPaper);
        int indexOf2 = this.allWallPapers.indexOf(tLRPC$WallPaper2);
        boolean z2 = tLRPC$WallPaper.dark;
        if ((!z2 || !tLRPC$WallPaper2.dark) && (z2 || tLRPC$WallPaper2.dark)) {
            return (!z2 || tLRPC$WallPaper2.dark) ? z ? 1 : -1 : z ? -1 : 1;
        }
        if (indexOf > indexOf2) {
            return 1;
        }
        if (indexOf < indexOf2) {
            return -1;
        }
        return 0;
    }

    private void updateRows() {
        this.rowCount = 0;
        if (this.currentType == 0) {
            int i = 0 + 1;
            this.rowCount = i;
            this.uploadImageRow = 0;
            int i2 = i + 1;
            this.rowCount = i2;
            this.setColorRow = i;
            this.rowCount = i2 + 1;
            this.sectionRow = i2;
        } else {
            this.uploadImageRow = -1;
            this.setColorRow = -1;
            this.sectionRow = -1;
        }
        if (!this.wallPapers.isEmpty()) {
            int ceil = (int) Math.ceil((double) (((float) this.wallPapers.size()) / ((float) this.columnsCount)));
            this.totalWallpaperRows = ceil;
            int i3 = this.rowCount;
            this.wallPaperStartRow = i3;
            this.rowCount = i3 + ceil;
        } else {
            this.wallPaperStartRow = -1;
        }
        if (this.currentType == 0) {
            int i4 = this.rowCount;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.resetSectionRow = i4;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.resetRow = i5;
            this.rowCount = i6 + 1;
            this.resetInfoRow = i6;
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
                    WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda1 wallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda1 = new WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda1(this, str);
                    this.searchRunnable = wallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda1;
                    AndroidUtilities.runOnUIThread(wallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda1, 500);
                }
            }
            notifyDataSetChanged();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$processSearch$0(String str) {
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
                TLRPC$TL_contacts_resolveUsername tLRPC$TL_contacts_resolveUsername = new TLRPC$TL_contacts_resolveUsername();
                tLRPC$TL_contacts_resolveUsername.username = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).imageSearchBot;
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(tLRPC$TL_contacts_resolveUsername, new WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda3(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$searchBotUser$2(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject != null) {
                AndroidUtilities.runOnUIThread(new WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda2(this, tLObject));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$searchBotUser$1(TLObject tLObject) {
            TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer = (TLRPC$TL_contacts_resolvedPeer) tLObject;
            MessagesController.getInstance(WallpapersListActivity.this.currentAccount).putUsers(tLRPC$TL_contacts_resolvedPeer.users, false);
            MessagesController.getInstance(WallpapersListActivity.this.currentAccount).putChats(tLRPC$TL_contacts_resolvedPeer.chats, false);
            WallpapersListActivity.this.getMessagesStorage().putUsersAndChats(tLRPC$TL_contacts_resolvedPeer.users, tLRPC$TL_contacts_resolvedPeer.chats, true, true);
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
            if (userOrChat instanceof TLRPC$User) {
                TLRPC$TL_messages_getInlineBotResults tLRPC$TL_messages_getInlineBotResults = new TLRPC$TL_messages_getInlineBotResults();
                tLRPC$TL_messages_getInlineBotResults.query = "#wallpaper " + str;
                tLRPC$TL_messages_getInlineBotResults.bot = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).getInputUser((TLRPC$User) userOrChat);
                tLRPC$TL_messages_getInlineBotResults.offset = str2;
                tLRPC$TL_messages_getInlineBotResults.peer = new TLRPC$TL_inputPeerEmpty();
                int i = this.lastSearchToken + 1;
                this.lastSearchToken = i;
                this.imageReqId = ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(tLRPC$TL_messages_getInlineBotResults, new WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda4(this, i));
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).bindRequestToGuid(this.imageReqId, WallpapersListActivity.this.classGuid);
            } else if (z) {
                searchBotUser();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$searchImages$4(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda0(this, i, tLObject));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$searchImages$3(int i, TLObject tLObject) {
            if (i == this.lastSearchToken) {
                boolean z = false;
                this.imageReqId = 0;
                int size = this.searchResult.size();
                if (tLObject != null) {
                    TLRPC$messages_BotResults tLRPC$messages_BotResults = (TLRPC$messages_BotResults) tLObject;
                    this.nextImagesSearchOffset = tLRPC$messages_BotResults.next_offset;
                    int size2 = tLRPC$messages_BotResults.results.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        TLRPC$BotInlineResult tLRPC$BotInlineResult = tLRPC$messages_BotResults.results.get(i2);
                        if ("photo".equals(tLRPC$BotInlineResult.type) && !this.searchResultKeys.containsKey(tLRPC$BotInlineResult.id)) {
                            MediaController.SearchImage searchImage = new MediaController.SearchImage();
                            TLRPC$Photo tLRPC$Photo = tLRPC$BotInlineResult.photo;
                            if (tLRPC$Photo != null) {
                                TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Photo.sizes, AndroidUtilities.getPhotoSize());
                                TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$BotInlineResult.photo.sizes, 320);
                                if (closestPhotoSizeWithSize != null) {
                                    searchImage.width = closestPhotoSizeWithSize.w;
                                    searchImage.height = closestPhotoSizeWithSize.h;
                                    searchImage.photoSize = closestPhotoSizeWithSize;
                                    searchImage.photo = tLRPC$BotInlineResult.photo;
                                    searchImage.size = closestPhotoSizeWithSize.size;
                                    searchImage.thumbPhotoSize = closestPhotoSizeWithSize2;
                                }
                            } else if (tLRPC$BotInlineResult.content != null) {
                                int i3 = 0;
                                while (true) {
                                    if (i3 >= tLRPC$BotInlineResult.content.attributes.size()) {
                                        break;
                                    }
                                    TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$BotInlineResult.content.attributes.get(i3);
                                    if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeImageSize) {
                                        searchImage.width = tLRPC$DocumentAttribute.w;
                                        searchImage.height = tLRPC$DocumentAttribute.h;
                                        break;
                                    }
                                    i3++;
                                }
                                TLRPC$WebDocument tLRPC$WebDocument = tLRPC$BotInlineResult.thumb;
                                if (tLRPC$WebDocument != null) {
                                    searchImage.thumbUrl = tLRPC$WebDocument.url;
                                } else {
                                    searchImage.thumbUrl = null;
                                }
                                TLRPC$WebDocument tLRPC$WebDocument2 = tLRPC$BotInlineResult.content;
                                searchImage.imageUrl = tLRPC$WebDocument2.url;
                                searchImage.size = tLRPC$WebDocument2.size;
                            }
                            searchImage.id = tLRPC$BotInlineResult.id;
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
                    int access$4500 = size % WallpapersListActivity.this.columnsCount;
                    float f = (float) size;
                    int ceil = (int) Math.ceil((double) (f / ((float) WallpapersListActivity.this.columnsCount)));
                    if (access$4500 != 0) {
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$5(View view, int i) {
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
                        WallpapersListActivity.this.presentFragment(new ThemePreviewActivity(obj, (Bitmap) null, true, false));
                    }
                };
            } else if (i == 1) {
                AnonymousClass2 r1 = new RecyclerListView(this, this.mContext) {
                    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                        if (!(getParent() == null || getParent().getParent() == null)) {
                            getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                        }
                        return super.onInterceptTouchEvent(motionEvent);
                    }
                };
                r1.setItemAnimator((RecyclerView.ItemAnimator) null);
                r1.setLayoutAnimation((LayoutAnimationController) null);
                AnonymousClass3 r2 = new LinearLayoutManager(this, this.mContext) {
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                };
                r1.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
                r1.setClipToPadding(false);
                r2.setOrientation(0);
                r1.setLayoutManager(r2);
                r1.setAdapter(new CategoryAdapterRecycler());
                r1.setOnItemClickListener((RecyclerListView.OnItemClickListener) new WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda5(this));
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
                int access$4500 = i * WallpapersListActivity.this.columnsCount;
                int ceil = (int) Math.ceil((double) (((float) this.searchResult.size()) / ((float) WallpapersListActivity.this.columnsCount)));
                int access$45002 = WallpapersListActivity.this.columnsCount;
                boolean z = true;
                boolean z2 = access$4500 == 0;
                if (access$4500 / WallpapersListActivity.this.columnsCount != ceil - 1) {
                    z = false;
                }
                wallpaperCell.setParams(access$45002, z2, z);
                for (int i2 = 0; i2 < WallpapersListActivity.this.columnsCount; i2++) {
                    int i3 = access$4500 + i2;
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: org.telegram.ui.WallpapersListActivity$FileWallpaper} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v0, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: org.telegram.ui.WallpapersListActivity$FileWallpaper} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v26, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v7, resolved type: org.telegram.ui.WallpapersListActivity$FileWallpaper} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v8, resolved type: org.telegram.ui.WallpapersListActivity$FileWallpaper} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v30, resolved type: org.telegram.ui.WallpapersListActivity$FileWallpaper} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v9, resolved type: org.telegram.ui.WallpapersListActivity$FileWallpaper} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: org.telegram.ui.WallpapersListActivity$FileWallpaper} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: org.telegram.ui.WallpapersListActivity$FileWallpaper} */
        /* JADX WARNING: type inference failed for: r3v31, types: [org.telegram.tgnet.TLRPC$TL_wallPaper, org.telegram.tgnet.TLRPC$WallPaper] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r13, int r14) {
            /*
                r12 = this;
                int r0 = r13.getItemViewType()
                r1 = 0
                r2 = 1
                if (r0 == 0) goto L_0x0236
                r3 = 2
                if (r0 == r3) goto L_0x002a
                r1 = 3
                if (r0 == r1) goto L_0x0010
                goto L_0x027e
            L_0x0010:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r13 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r13
                org.telegram.ui.WallpapersListActivity r0 = org.telegram.ui.WallpapersListActivity.this
                int r0 = r0.resetInfoRow
                if (r14 != r0) goto L_0x027e
                r14 = 2131628055(0x7f0e1017, float:1.8883392E38)
                java.lang.String r0 = "ResetChatBackgroundsInfo"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                r13.setText(r14)
                goto L_0x027e
            L_0x002a:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.WallpaperCell r13 = (org.telegram.ui.Cells.WallpaperCell) r13
                org.telegram.ui.WallpapersListActivity r0 = org.telegram.ui.WallpapersListActivity.this
                int r0 = r0.wallPaperStartRow
                int r14 = r14 - r0
                org.telegram.ui.WallpapersListActivity r0 = org.telegram.ui.WallpapersListActivity.this
                int r0 = r0.columnsCount
                int r14 = r14 * r0
                org.telegram.ui.WallpapersListActivity r0 = org.telegram.ui.WallpapersListActivity.this
                int r0 = r0.columnsCount
                if (r14 != 0) goto L_0x0047
                r3 = 1
                goto L_0x0048
            L_0x0047:
                r3 = 0
            L_0x0048:
                org.telegram.ui.WallpapersListActivity r4 = org.telegram.ui.WallpapersListActivity.this
                int r4 = r4.columnsCount
                int r4 = r14 / r4
                org.telegram.ui.WallpapersListActivity r5 = org.telegram.ui.WallpapersListActivity.this
                int r5 = r5.totalWallpaperRows
                int r5 = r5 - r2
                if (r4 != r5) goto L_0x005b
                r4 = 1
                goto L_0x005c
            L_0x005b:
                r4 = 0
            L_0x005c:
                r13.setParams(r0, r3, r4)
                r0 = 0
            L_0x0060:
                org.telegram.ui.WallpapersListActivity r3 = org.telegram.ui.WallpapersListActivity.this
                int r3 = r3.columnsCount
                if (r0 >= r3) goto L_0x027e
                int r3 = r14 + r0
                org.telegram.ui.WallpapersListActivity r4 = org.telegram.ui.WallpapersListActivity.this
                java.util.ArrayList r4 = r4.wallPapers
                int r4 = r4.size()
                r5 = 0
                if (r3 >= r4) goto L_0x0083
                org.telegram.ui.WallpapersListActivity r4 = org.telegram.ui.WallpapersListActivity.this
                java.util.ArrayList r4 = r4.wallPapers
                java.lang.Object r3 = r4.get(r3)
                r6 = r3
                goto L_0x0084
            L_0x0083:
                r6 = r5
            L_0x0084:
                boolean r3 = r6 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
                r4 = 1120403456(0x42CLASSNAME, float:100.0)
                r7 = 0
                if (r3 == 0) goto L_0x013b
                r3 = r6
                org.telegram.tgnet.TLRPC$TL_wallPaper r3 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r3
                org.telegram.ui.ActionBar.Theme$ThemeInfo r7 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
                org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r7 = r7.overrideWallpaper
                org.telegram.ui.WallpapersListActivity r7 = org.telegram.ui.WallpapersListActivity.this
                java.lang.String r7 = r7.selectedBackgroundSlug
                java.lang.String r8 = r3.slug
                boolean r7 = r7.equals(r8)
                if (r7 == 0) goto L_0x0137
                org.telegram.ui.WallpapersListActivity r7 = org.telegram.ui.WallpapersListActivity.this
                java.lang.String r7 = r7.selectedBackgroundSlug
                java.lang.String r8 = r3.slug
                boolean r7 = r7.equals(r8)
                if (r7 == 0) goto L_0x0136
                org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r3.settings
                if (r7 == 0) goto L_0x0136
                org.telegram.ui.WallpapersListActivity r7 = org.telegram.ui.WallpapersListActivity.this
                int r7 = r7.selectedColor
                org.telegram.tgnet.TLRPC$WallPaperSettings r8 = r3.settings
                int r8 = r8.background_color
                int r8 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r8)
                if (r7 != r8) goto L_0x0137
                org.telegram.ui.WallpapersListActivity r7 = org.telegram.ui.WallpapersListActivity.this
                int r7 = r7.selectedGradientColor1
                org.telegram.tgnet.TLRPC$WallPaperSettings r8 = r3.settings
                int r8 = r8.second_background_color
                int r8 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r8)
                if (r7 != r8) goto L_0x0137
                org.telegram.ui.WallpapersListActivity r7 = org.telegram.ui.WallpapersListActivity.this
                int r7 = r7.selectedGradientColor2
                org.telegram.tgnet.TLRPC$WallPaperSettings r8 = r3.settings
                int r8 = r8.third_background_color
                int r8 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r8)
                if (r7 != r8) goto L_0x0137
                org.telegram.ui.WallpapersListActivity r7 = org.telegram.ui.WallpapersListActivity.this
                int r7 = r7.selectedGradientColor3
                org.telegram.tgnet.TLRPC$WallPaperSettings r8 = r3.settings
                int r8 = r8.fourth_background_color
                int r8 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r8)
                if (r7 != r8) goto L_0x0137
                org.telegram.ui.WallpapersListActivity r7 = org.telegram.ui.WallpapersListActivity.this
                int r7 = r7.selectedGradientColor1
                if (r7 == 0) goto L_0x0136
                org.telegram.ui.WallpapersListActivity r7 = org.telegram.ui.WallpapersListActivity.this
                int r7 = r7.selectedGradientColor2
                if (r7 != 0) goto L_0x0136
                org.telegram.ui.WallpapersListActivity r7 = org.telegram.ui.WallpapersListActivity.this
                int r7 = r7.selectedGradientRotation
                org.telegram.tgnet.TLRPC$WallPaperSettings r8 = r3.settings
                int r8 = r8.rotation
                int r8 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r8, r1)
                if (r7 == r8) goto L_0x0136
                boolean r7 = r3.pattern
                if (r7 == 0) goto L_0x0136
                org.telegram.tgnet.TLRPC$WallPaperSettings r7 = r3.settings
                int r7 = r7.intensity
                float r7 = (float) r7
                float r7 = r7 / r4
                float r4 = org.telegram.ui.ActionBar.Theme.getThemeIntensity(r7)
                org.telegram.ui.WallpapersListActivity r7 = org.telegram.ui.WallpapersListActivity.this
                float r7 = r7.selectedIntensity
                float r4 = r4 - r7
                float r4 = java.lang.Math.abs(r4)
                r7 = 981668463(0x3a83126f, float:0.001)
                int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
                if (r4 <= 0) goto L_0x0136
                goto L_0x0137
            L_0x0136:
                r5 = r3
            L_0x0137:
                long r7 = r3.id
                goto L_0x01f3
            L_0x013b:
                boolean r3 = r6 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
                if (r3 == 0) goto L_0x01dd
                r3 = r6
                org.telegram.ui.WallpapersListActivity$ColorWallpaper r3 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r3
                java.lang.String r9 = r3.slug
                java.lang.String r10 = "d"
                boolean r9 = r10.equals(r9)
                if (r9 == 0) goto L_0x015c
                org.telegram.ui.WallpapersListActivity r9 = org.telegram.ui.WallpapersListActivity.this
                java.lang.String r9 = r9.selectedBackgroundSlug
                java.lang.String r10 = r3.slug
                boolean r9 = r9.equals(r10)
                if (r9 == 0) goto L_0x015c
                goto L_0x01d4
            L_0x015c:
                int r9 = r3.color
                org.telegram.ui.WallpapersListActivity r10 = org.telegram.ui.WallpapersListActivity.this
                int r10 = r10.selectedColor
                if (r9 != r10) goto L_0x01d5
                int r9 = r3.gradientColor1
                org.telegram.ui.WallpapersListActivity r10 = org.telegram.ui.WallpapersListActivity.this
                int r10 = r10.selectedGradientColor1
                if (r9 != r10) goto L_0x01d5
                int r9 = r3.gradientColor2
                org.telegram.ui.WallpapersListActivity r10 = org.telegram.ui.WallpapersListActivity.this
                int r10 = r10.selectedGradientColor2
                if (r9 != r10) goto L_0x01d5
                int r9 = r3.gradientColor3
                org.telegram.ui.WallpapersListActivity r10 = org.telegram.ui.WallpapersListActivity.this
                int r10 = r10.selectedGradientColor3
                if (r9 != r10) goto L_0x01d5
                org.telegram.ui.WallpapersListActivity r9 = org.telegram.ui.WallpapersListActivity.this
                int r9 = r9.selectedGradientColor1
                if (r9 == 0) goto L_0x0197
                int r9 = r3.gradientRotation
                org.telegram.ui.WallpapersListActivity r10 = org.telegram.ui.WallpapersListActivity.this
                int r10 = r10.selectedGradientRotation
                if (r9 == r10) goto L_0x0197
                goto L_0x01d5
            L_0x0197:
                org.telegram.ui.WallpapersListActivity r9 = org.telegram.ui.WallpapersListActivity.this
                java.lang.String r9 = r9.selectedBackgroundSlug
                java.lang.String r10 = "c"
                boolean r9 = r10.equals(r9)
                if (r9 == 0) goto L_0x01a9
                java.lang.String r9 = r3.slug
                if (r9 != 0) goto L_0x01d5
            L_0x01a9:
                org.telegram.ui.WallpapersListActivity r9 = org.telegram.ui.WallpapersListActivity.this
                java.lang.String r9 = r9.selectedBackgroundSlug
                boolean r9 = r10.equals(r9)
                if (r9 != 0) goto L_0x01d4
                org.telegram.ui.WallpapersListActivity r9 = org.telegram.ui.WallpapersListActivity.this
                java.lang.String r9 = r9.selectedBackgroundSlug
                java.lang.String r10 = r3.slug
                boolean r9 = android.text.TextUtils.equals(r9, r10)
                if (r9 == 0) goto L_0x01d5
                float r9 = r3.intensity
                float r9 = r9 * r4
                int r9 = (int) r9
                org.telegram.ui.WallpapersListActivity r10 = org.telegram.ui.WallpapersListActivity.this
                float r10 = r10.selectedIntensity
                float r10 = r10 * r4
                int r4 = (int) r10
                if (r9 == r4) goto L_0x01d4
                goto L_0x01d5
            L_0x01d4:
                r5 = r6
            L_0x01d5:
                org.telegram.tgnet.TLRPC$WallPaper r3 = r3.parentWallpaper
                if (r3 == 0) goto L_0x01f3
                long r3 = r3.id
                r7 = r3
                goto L_0x01f3
            L_0x01dd:
                boolean r3 = r6 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper
                if (r3 == 0) goto L_0x01f3
                r3 = r6
                org.telegram.ui.WallpapersListActivity$FileWallpaper r3 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r3
                org.telegram.ui.WallpapersListActivity r4 = org.telegram.ui.WallpapersListActivity.this
                java.lang.String r4 = r4.selectedBackgroundSlug
                java.lang.String r3 = r3.slug
                boolean r3 = r4.equals(r3)
                if (r3 == 0) goto L_0x01f3
                r5 = r6
            L_0x01f3:
                r10 = r7
                r7 = r5
                org.telegram.ui.WallpapersListActivity r3 = org.telegram.ui.WallpapersListActivity.this
                int r4 = r3.currentType
                r8 = 0
                r9 = 0
                r3 = r13
                r5 = r0
                r3.setWallpaper(r4, r5, r6, r7, r8, r9)
                org.telegram.ui.WallpapersListActivity r3 = org.telegram.ui.WallpapersListActivity.this
                org.telegram.ui.ActionBar.ActionBar r3 = r3.actionBar
                boolean r3 = r3.isActionModeShowed()
                if (r3 == 0) goto L_0x0228
                org.telegram.ui.WallpapersListActivity r3 = org.telegram.ui.WallpapersListActivity.this
                android.util.LongSparseArray r3 = r3.selectedWallPapers
                int r3 = r3.indexOfKey(r10)
                if (r3 < 0) goto L_0x021c
                r3 = 1
                goto L_0x021d
            L_0x021c:
                r3 = 0
            L_0x021d:
                org.telegram.ui.WallpapersListActivity r4 = org.telegram.ui.WallpapersListActivity.this
                boolean r4 = r4.scrolling
                r4 = r4 ^ r2
                r13.setChecked(r0, r3, r4)
                goto L_0x0232
            L_0x0228:
                org.telegram.ui.WallpapersListActivity r3 = org.telegram.ui.WallpapersListActivity.this
                boolean r3 = r3.scrolling
                r3 = r3 ^ r2
                r13.setChecked(r0, r1, r3)
            L_0x0232:
                int r0 = r0 + 1
                goto L_0x0060
            L_0x0236:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.TextCell r13 = (org.telegram.ui.Cells.TextCell) r13
                org.telegram.ui.WallpapersListActivity r0 = org.telegram.ui.WallpapersListActivity.this
                int r0 = r0.uploadImageRow
                if (r14 != r0) goto L_0x0252
                r14 = 2131628230(0x7f0e10c6, float:1.8883747E38)
                java.lang.String r0 = "SelectFromGallery"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                r0 = 2131165859(0x7var_a3, float:1.7945947E38)
                r13.setTextAndIcon((java.lang.String) r14, (int) r0, (boolean) r2)
                goto L_0x027e
            L_0x0252:
                org.telegram.ui.WallpapersListActivity r0 = org.telegram.ui.WallpapersListActivity.this
                int r0 = r0.setColorRow
                if (r14 != r0) goto L_0x026a
                r14 = 2131628304(0x7f0e1110, float:1.8883897E38)
                java.lang.String r0 = "SetColor"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                r0 = 2131165834(0x7var_a, float:1.7945896E38)
                r13.setTextAndIcon((java.lang.String) r14, (int) r0, (boolean) r2)
                goto L_0x027e
            L_0x026a:
                org.telegram.ui.WallpapersListActivity r0 = org.telegram.ui.WallpapersListActivity.this
                int r0 = r0.resetRow
                if (r14 != r0) goto L_0x027e
                r14 = 2131628052(0x7f0e1014, float:1.8883386E38)
                java.lang.String r0 = "ResetChatBackgrounds"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                r13.setText(r14, r1)
            L_0x027e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpapersListActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
        arrayList.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        arrayList.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        return arrayList;
    }
}
