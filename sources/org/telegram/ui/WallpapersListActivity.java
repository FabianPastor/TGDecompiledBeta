package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
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
import org.telegram.ui.Components.ColorPicker;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.WallpaperUpdater;

public class WallpapersListActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final int TYPE_ALL = 0;
    public static final int TYPE_COLOR = 1;
    private static final int[][] defaultColorsDark = {new int[]{-14797481, -15394250, -14924974, -14006975}, new int[]{-14867905, -14870478, -14997181, -15460815}, new int[]{-14666695, -15720408, -14861254, -15260107}, new int[]{-14932175, -15066075, -14208965, -15000799}, new int[]{-12968902, -14411460, -13029826, -15067598}, new int[]{-13885157, -12307670, -14542561, -12899018}, new int[]{-14797481, -15196106, -14924974, -15325638}, new int[]{-15658442, -15449521, -16047308, -12897955}, new int[]{-13809610, -15258855, -13221071, -15715791}, new int[]{-14865092}, new int[]{-15656154}, new int[]{-16051170}, new int[]{-14731745}, new int[]{-15524075}, new int[]{-15853808}, new int[]{-13685209}, new int[]{-14014945}, new int[]{-15132649}, new int[]{-12374480}, new int[]{-13755362}, new int[]{-14740716}, new int[]{-12374468}, new int[]{-13755352}, new int[]{-14740709}, new int[]{-12833213}, new int[]{-14083026}, new int[]{-14872031}, new int[]{-13554109}, new int[]{-14803922}, new int[]{-15461855}, new int[]{-13680833}, new int[]{-14602960}, new int[]{-15458784}, new int[]{-14211804}, new int[]{-15132906}, new int[]{-16777216}};
    private static final int[][] defaultColorsLight = {new int[]{-2368069, -9722489, -2762611, -7817084}, new int[]{-7487253, -4599318, -3755537, -1320977}, new int[]{-6832405, -5117462, -3755537, -1067044}, new int[]{-7676942, -7827988, -1859606, -9986835}, new int[]{-5190165, -6311702, -4461867, -5053475}, new int[]{-2430264, -6114049, -1258497, -4594945}, new int[]{-2298990, -7347754, -9985038, -8006011}, new int[]{-1399954, -990074, -876865, -1523602}, new int[]{-15438, -1916673, -6222, -471346}, new int[]{-2891798}, new int[]{-5913125}, new int[]{-9463352}, new int[]{-2956375}, new int[]{-5974898}, new int[]{-8537234}, new int[]{-1647186}, new int[]{-2769263}, new int[]{-3431303}, new int[]{-1326919}, new int[]{-2054243}, new int[]{-3573648}, new int[]{-1328696}, new int[]{-2056777}, new int[]{-2984557}, new int[]{-2440467}, new int[]{-2906649}, new int[]{-4880430}, new int[]{-4013331}, new int[]{-5921305}, new int[]{-8421424}, new int[]{-4005139}, new int[]{-5908761}, new int[]{-8406320}, new int[]{-2702663}, new int[]{-6518654}, new int[]{-16777216}};
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
    private boolean loadingWallpapers;
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
        public TLRPC.WallPaper parentWallpaper;
        public File path;
        public TLRPC.TL_wallPaper pattern;
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

        public ColorWallpaper(String s, int c, int gc, int r) {
            this.slug = s;
            this.color = c | -16777216;
            int i = 0;
            int i2 = gc == 0 ? 0 : -16777216 | gc;
            this.gradientColor1 = i2;
            this.gradientRotation = i2 != 0 ? r : i;
            this.intensity = 1.0f;
        }

        public ColorWallpaper(String s, int c, int gc1, int gc2, int gc3) {
            this.slug = s;
            this.color = c | -16777216;
            int i = 0;
            this.gradientColor1 = gc1 == 0 ? 0 : gc1 | -16777216;
            this.gradientColor2 = gc2 == 0 ? 0 : gc2 | -16777216;
            this.gradientColor3 = gc3 != 0 ? gc3 | -16777216 : i;
            this.intensity = 1.0f;
            this.isGradient = true;
        }

        public ColorWallpaper(String s, int c) {
            this.slug = s;
            int i = -16777216 | c;
            this.color = i;
            float[] hsv = new float[3];
            Color.colorToHSV(i, hsv);
            if (hsv[0] > 180.0f) {
                hsv[0] = hsv[0] - 60.0f;
            } else {
                hsv[0] = hsv[0] + 60.0f;
            }
            this.gradientColor1 = Color.HSVToColor(255, hsv);
            this.gradientColor2 = ColorPicker.generateGradientColors(this.color);
            this.gradientColor3 = ColorPicker.generateGradientColors(this.gradientColor1);
            this.intensity = 1.0f;
            this.isGradient = true;
        }

        public ColorWallpaper(String s, int c, int gc1, int gc2, int gc3, int r, float in, boolean m, File ph) {
            this.slug = s;
            this.color = c | -16777216;
            int i = 0;
            int i2 = gc1 == 0 ? 0 : gc1 | -16777216;
            this.gradientColor1 = i2;
            this.gradientColor2 = gc2 == 0 ? 0 : gc2 | -16777216;
            this.gradientColor3 = gc3 != 0 ? gc3 | -16777216 : i;
            this.gradientRotation = i2 != 0 ? r : 45;
            this.intensity = in;
            this.path = ph;
            this.motion = m;
        }

        public String getUrl() {
            String color2;
            String color3;
            int i = this.gradientColor1;
            String color4 = null;
            if (i != 0) {
                color2 = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (i >> 16)) & 255), Integer.valueOf(((byte) (this.gradientColor1 >> 8)) & 255), Byte.valueOf((byte) (this.gradientColor1 & 255))}).toLowerCase();
            } else {
                color2 = null;
            }
            String color1 = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (this.color >> 16)) & 255), Integer.valueOf(((byte) (this.color >> 8)) & 255), Byte.valueOf((byte) (this.color & 255))}).toLowerCase();
            int i2 = this.gradientColor2;
            if (i2 != 0) {
                color3 = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (i2 >> 16)) & 255), Integer.valueOf(((byte) (this.gradientColor2 >> 8)) & 255), Byte.valueOf((byte) (this.gradientColor2 & 255))}).toLowerCase();
            } else {
                color3 = null;
            }
            int i3 = this.gradientColor3;
            if (i3 != 0) {
                color4 = String.format("%02x%02x%02x", new Object[]{Integer.valueOf(((byte) (i3 >> 16)) & 255), Integer.valueOf(((byte) (this.gradientColor3 >> 8)) & 255), Byte.valueOf((byte) (this.gradientColor3 & 255))}).toLowerCase();
            }
            if (color2 == null || color3 == null) {
                if (color2 != null) {
                    String color12 = color1 + "-" + color2;
                    if (this.pattern != null) {
                        color1 = color12 + "&rotation=" + AndroidUtilities.getWallpaperRotation(this.gradientRotation, true);
                    } else {
                        color1 = color12 + "?rotation=" + AndroidUtilities.getWallpaperRotation(this.gradientRotation, true);
                    }
                }
            } else if (color4 != null) {
                color1 = color1 + "~" + color2 + "~" + color3 + "~" + color4;
            } else {
                color1 = color1 + "~" + color2 + "~" + color3;
            }
            if (this.pattern != null) {
                String link = "https://" + MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix + "/bg/" + this.pattern.slug + "?intensity=" + ((int) (this.intensity * 100.0f)) + "&bg_color=" + color1;
                if (!this.motion) {
                    return link;
                }
                return link + "&mode=motion";
            }
            return "https://" + MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix + "/bg/" + color1;
        }
    }

    public static class FileWallpaper {
        public File originalPath;
        public File path;
        public int resId;
        public String slug;
        public int thumbResId;

        public FileWallpaper(String s, File f, File of) {
            this.slug = s;
            this.path = f;
            this.originalPath = of;
        }

        public FileWallpaper(String s, String f) {
            this.slug = s;
            this.path = new File(f);
        }

        public FileWallpaper(String s, int r, int t) {
            this.slug = s;
            this.resId = r;
            this.thumbResId = t;
        }
    }

    public WallpapersListActivity(int type) {
        this.currentType = type;
    }

    public boolean onFragmentCreate() {
        if (this.currentType == 0) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersNeedReload);
            getMessagesStorage().getWallpapers();
        } else {
            int[][] defaultColors = Theme.isCurrentThemeDark() ? defaultColorsDark : defaultColorsLight;
            for (int a = 0; a < defaultColors.length; a++) {
                if (defaultColors[a].length == 1) {
                    this.wallPapers.add(new ColorWallpaper("c", defaultColors[a][0], 0, 45));
                } else {
                    this.wallPapers.add(new ColorWallpaper("c", defaultColors[a][0], defaultColors[a][1], defaultColors[a][2], defaultColors[a][3]));
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
            public void didSelectWallpaper(File file, Bitmap bitmap, boolean gallery) {
                WallpapersListActivity.this.presentFragment(new ThemePreviewActivity(new FileWallpaper("", file, file), bitmap), gallery);
            }

            public void needOpenColorPicker() {
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
            public void onItemClick(int id) {
                if (id == -1) {
                    if (WallpapersListActivity.this.actionBar.isActionModeShowed()) {
                        WallpapersListActivity.this.selectedWallPapers.clear();
                        WallpapersListActivity.this.actionBar.hideActionMode();
                        WallpapersListActivity.this.updateRowsSelection();
                        return;
                    }
                    WallpapersListActivity.this.finishFragment();
                } else if (id == 4) {
                    if (WallpapersListActivity.this.getParentActivity() != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) WallpapersListActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.formatPluralString("DeleteBackground", WallpapersListActivity.this.selectedWallPapers.size()));
                        builder.setMessage(LocaleController.formatString("DeleteChatBackgroundsAlert", NUM, new Object[0]));
                        builder.setPositiveButton(LocaleController.getString("Delete", NUM), new WallpapersListActivity$2$$ExternalSyntheticLambda0(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        AlertDialog alertDialog = builder.create();
                        WallpapersListActivity.this.showDialog(alertDialog);
                        TextView button = (TextView) alertDialog.getButton(-1);
                        if (button != null) {
                            button.setTextColor(Theme.getColor("dialogTextRed2"));
                        }
                    }
                } else if (id == 3) {
                    Bundle args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putInt("dialogsType", 3);
                    DialogsActivity fragment = new DialogsActivity(args);
                    fragment.setDelegate(new WallpapersListActivity$2$$ExternalSyntheticLambda3(this));
                    WallpapersListActivity.this.presentFragment(fragment);
                }
            }

            /* renamed from: lambda$onItemClick$2$org-telegram-ui-WallpapersListActivity$2  reason: not valid java name */
            public /* synthetic */ void m4084lambda$onItemClick$2$orgtelegramuiWallpapersListActivity$2(DialogInterface dialogInterface, int i) {
                AlertDialog unused = WallpapersListActivity.this.progressDialog = new AlertDialog(WallpapersListActivity.this.getParentActivity(), 3);
                WallpapersListActivity.this.progressDialog.setCanCacnel(false);
                WallpapersListActivity.this.progressDialog.show();
                new ArrayList();
                int[] deleteCount = {0};
                for (int b = 0; b < WallpapersListActivity.this.selectedWallPapers.size(); b++) {
                    Object object = WallpapersListActivity.this.selectedWallPapers.valueAt(b);
                    boolean z = object instanceof ColorWallpaper;
                    Object object2 = object;
                    if (z) {
                        ColorWallpaper colorWallpaper = (ColorWallpaper) object;
                        if (colorWallpaper.parentWallpaper == null || colorWallpaper.parentWallpaper.id >= 0) {
                            object2 = colorWallpaper.parentWallpaper;
                        } else {
                            WallpapersListActivity.this.getMessagesStorage().deleteWallpaper(colorWallpaper.parentWallpaper.id);
                            WallpapersListActivity.this.localWallPapers.remove(colorWallpaper);
                            WallpapersListActivity.this.localDict.remove(colorWallpaper.getHash());
                            object2 = object;
                        }
                    }
                    if (object2 instanceof TLRPC.WallPaper) {
                        deleteCount[0] = deleteCount[0] + 1;
                        TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) object2;
                        TLRPC.TL_account_saveWallPaper req = new TLRPC.TL_account_saveWallPaper();
                        req.settings = new TLRPC.TL_wallPaperSettings();
                        req.unsave = true;
                        if (object2 instanceof TLRPC.TL_wallPaperNoFile) {
                            TLRPC.TL_inputWallPaperNoFile inputWallPaper = new TLRPC.TL_inputWallPaperNoFile();
                            inputWallPaper.id = wallPaper.id;
                            req.wallpaper = inputWallPaper;
                        } else {
                            TLRPC.TL_inputWallPaper inputWallPaper2 = new TLRPC.TL_inputWallPaper();
                            inputWallPaper2.id = wallPaper.id;
                            inputWallPaper2.access_hash = wallPaper.access_hash;
                            req.wallpaper = inputWallPaper2;
                        }
                        if (wallPaper.slug != null && wallPaper.slug.equals(WallpapersListActivity.this.selectedBackgroundSlug)) {
                            String unused2 = WallpapersListActivity.this.selectedBackgroundSlug = Theme.hasWallpaperFromTheme() ? "t" : "d";
                            Theme.getActiveTheme().setOverrideWallpaper((Theme.OverrideWallpaperInfo) null);
                            Theme.reloadWallpaper();
                        }
                        ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(req, new WallpapersListActivity$2$$ExternalSyntheticLambda2(this, deleteCount));
                    }
                }
                if (deleteCount[0] == 0) {
                    WallpapersListActivity.this.loadWallpapers(true);
                }
                WallpapersListActivity.this.selectedWallPapers.clear();
                WallpapersListActivity.this.actionBar.hideActionMode();
                WallpapersListActivity.this.actionBar.closeSearchField();
            }

            /* renamed from: lambda$onItemClick$1$org-telegram-ui-WallpapersListActivity$2  reason: not valid java name */
            public /* synthetic */ void m4083lambda$onItemClick$1$orgtelegramuiWallpapersListActivity$2(int[] deleteCount, TLObject response, TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new WallpapersListActivity$2$$ExternalSyntheticLambda1(this, deleteCount));
            }

            /* renamed from: lambda$onItemClick$0$org-telegram-ui-WallpapersListActivity$2  reason: not valid java name */
            public /* synthetic */ void m4082lambda$onItemClick$0$orgtelegramuiWallpapersListActivity$2(int[] deleteCount) {
                deleteCount[0] = deleteCount[0] - 1;
                if (deleteCount[0] == 0) {
                    WallpapersListActivity.this.loadWallpapers(true);
                }
            }

            /* renamed from: lambda$onItemClick$3$org-telegram-ui-WallpapersListActivity$2  reason: not valid java name */
            public /* synthetic */ void m4085lambda$onItemClick$3$orgtelegramuiWallpapersListActivity$2(DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
                String link;
                ArrayList arrayList = dids;
                StringBuilder fmessage = new StringBuilder();
                for (int b = 0; b < WallpapersListActivity.this.selectedWallPapers.size(); b++) {
                    Object object = WallpapersListActivity.this.selectedWallPapers.valueAt(b);
                    if (object instanceof TLRPC.TL_wallPaper) {
                        link = AndroidUtilities.getWallPaperUrl(object);
                    } else if (object instanceof ColorWallpaper) {
                        link = ((ColorWallpaper) object).getUrl();
                    }
                    if (!TextUtils.isEmpty(link)) {
                        if (fmessage.length() > 0) {
                            fmessage.append(10);
                        }
                        fmessage.append(link);
                    }
                }
                WallpapersListActivity.this.selectedWallPapers.clear();
                WallpapersListActivity.this.actionBar.hideActionMode();
                WallpapersListActivity.this.actionBar.closeSearchField();
                if (dids.size() > 1 || ((Long) arrayList.get(0)).longValue() == UserConfig.getInstance(WallpapersListActivity.this.currentAccount).getClientUserId() || message != null) {
                    WallpapersListActivity.this.updateRowsSelection();
                    for (int a = 0; a < dids.size(); a++) {
                        long did = ((Long) arrayList.get(a)).longValue();
                        if (message != null) {
                            SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(message.toString(), did, (MessageObject) null, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                        }
                        if (!TextUtils.isEmpty(fmessage)) {
                            SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(fmessage.toString(), did, (MessageObject) null, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                        }
                    }
                    fragment1.finishFragment();
                    return;
                }
                long did2 = ((Long) arrayList.get(0)).longValue();
                Bundle args1 = new Bundle();
                args1.putBoolean("scrollToTopOnResume", true);
                if (DialogObject.isEncryptedDialog(did2)) {
                    args1.putInt("enc_id", DialogObject.getEncryptedChatId(did2));
                    DialogsActivity dialogsActivity = fragment1;
                } else {
                    if (DialogObject.isUserDialog(did2)) {
                        args1.putLong("user_id", did2);
                    } else if (DialogObject.isChatDialog(did2)) {
                        args1.putLong("chat_id", -did2);
                    }
                    if (!MessagesController.getInstance(WallpapersListActivity.this.currentAccount).checkCanOpenChat(args1, fragment1)) {
                        return;
                    }
                }
                NotificationCenter.getInstance(WallpapersListActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                WallpapersListActivity.this.presentFragment(new ChatActivity(args1), true);
                long j = did2;
                SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(fmessage.toString(), did2, (MessageObject) null, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
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
            ActionBarMenu actionMode = this.actionBar.createActionMode(false, (String) null);
            actionMode.setBackgroundColor(Theme.getColor("actionBarDefault"));
            this.actionBar.setItemsColor(Theme.getColor("actionBarDefaultIcon"), true);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarDefaultSelector"), true);
            NumberTextView numberTextView = new NumberTextView(actionMode.getContext());
            this.selectedMessagesCountTextView = numberTextView;
            numberTextView.setTextSize(18);
            this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.selectedMessagesCountTextView.setTextColor(Theme.getColor("actionBarDefaultIcon"));
            this.selectedMessagesCountTextView.setOnTouchListener(WallpapersListActivity$$ExternalSyntheticLambda1.INSTANCE);
            actionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 65, 0, 0, 0));
            this.actionModeViews.add(actionMode.addItemWithWidth(3, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("Forward", NUM)));
            this.actionModeViews.add(actionMode.addItemWithWidth(4, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("Delete", NUM)));
            this.selectedWallPapers.clear();
        }
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        AnonymousClass4 r4 = new RecyclerListView(context) {
            private Paint paint = new Paint();

            public boolean hasOverlappingRendering() {
                return false;
            }

            public void onDraw(Canvas c) {
                RecyclerView.ViewHolder holder;
                int bottom;
                if (getAdapter() != WallpapersListActivity.this.listAdapter || WallpapersListActivity.this.resetInfoRow == -1) {
                    holder = null;
                } else {
                    holder = findViewHolderForAdapterPosition(WallpapersListActivity.this.resetInfoRow);
                }
                int height = getMeasuredHeight();
                if (holder != null) {
                    bottom = holder.itemView.getBottom();
                    if (holder.itemView.getBottom() >= height) {
                        bottom = height;
                    }
                } else {
                    bottom = height;
                }
                this.paint.setColor(Theme.getColor("windowBackgroundWhite"));
                c.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) bottom, this.paint);
                if (bottom != height) {
                    this.paint.setColor(Theme.getColor("windowBackgroundGray"));
                    c.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) height, this.paint);
                }
            }
        };
        this.listView = r4;
        r4.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass5 r42 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r42;
        recyclerListView.setLayoutManager(r42);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context);
        this.listAdapter = listAdapter2;
        recyclerListView2.setAdapter(listAdapter2);
        this.searchAdapter = new SearchAdapter(context);
        this.listView.setGlowColor(Theme.getColor("avatar_backgroundActionBarBlue"));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new WallpapersListActivity$$ExternalSyntheticLambda7(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean z = true;
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(WallpapersListActivity.this.getParentActivity().getCurrentFocus());
                }
                WallpapersListActivity wallpapersListActivity = WallpapersListActivity.this;
                if (newState == 0) {
                    z = false;
                }
                boolean unused = wallpapersListActivity.scrolling = z;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (WallpapersListActivity.this.listView.getAdapter() == WallpapersListActivity.this.searchAdapter) {
                    int firstVisibleItem = WallpapersListActivity.this.layoutManager.findFirstVisibleItemPosition();
                    int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(WallpapersListActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                    if (visibleItemCount > 0) {
                        int totalItemCount = WallpapersListActivity.this.layoutManager.getItemCount();
                        if (visibleItemCount != 0 && firstVisibleItem + visibleItemCount > totalItemCount - 2) {
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
        frameLayout.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f));
        updateRows();
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-WallpapersListActivity  reason: not valid java name */
    public /* synthetic */ void m4078lambda$createView$4$orgtelegramuiWallpapersListActivity(View view, int position) {
        if (getParentActivity() != null && this.listView.getAdapter() != this.searchAdapter) {
            if (position == this.uploadImageRow) {
                this.updater.openGallery();
            } else if (position == this.setColorRow) {
                WallpapersListActivity activity = new WallpapersListActivity(1);
                activity.patterns = this.patterns;
                presentFragment(activity);
            } else if (position == this.resetRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("ResetChatBackgroundsAlertTitle", NUM));
                builder.setMessage(LocaleController.getString("ResetChatBackgroundsAlert", NUM));
                builder.setPositiveButton(LocaleController.getString("Reset", NUM), new WallpapersListActivity$$ExternalSyntheticLambda0(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog dialog = builder.create();
                showDialog(dialog);
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-WallpapersListActivity  reason: not valid java name */
    public /* synthetic */ void m4077lambda$createView$3$orgtelegramuiWallpapersListActivity(DialogInterface dialogInterface, int i) {
        if (this.actionBar.isActionModeShowed()) {
            this.selectedWallPapers.clear();
            this.actionBar.hideActionMode();
            updateRowsSelection();
        }
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        this.progressDialog = alertDialog;
        alertDialog.setCanCacnel(false);
        this.progressDialog.show();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resetWallPapers(), new WallpapersListActivity$$ExternalSyntheticLambda5(this));
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-WallpapersListActivity  reason: not valid java name */
    public /* synthetic */ void m4075lambda$createView$1$orgtelegramuiWallpapersListActivity() {
        loadWallpapers(false);
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-WallpapersListActivity  reason: not valid java name */
    public /* synthetic */ void m4076lambda$createView$2$orgtelegramuiWallpapersListActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new WallpapersListActivity$$ExternalSyntheticLambda2(this));
    }

    public void onResume() {
        super.onResume();
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        Theme.OverrideWallpaperInfo overrideWallpaper = Theme.getActiveTheme().overrideWallpaper;
        if (overrideWallpaper != null) {
            this.selectedBackgroundSlug = overrideWallpaper.slug;
            this.selectedColor = overrideWallpaper.color;
            this.selectedGradientColor1 = overrideWallpaper.gradientColor1;
            this.selectedGradientColor2 = overrideWallpaper.gradientColor2;
            this.selectedGradientColor3 = overrideWallpaper.gradientColor3;
            this.selectedGradientRotation = overrideWallpaper.rotation;
            this.selectedIntensity = overrideWallpaper.intensity;
            this.selectedBackgroundMotion = overrideWallpaper.isMotion;
            this.selectedBackgroundBlurred = overrideWallpaper.isBlurred;
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

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.updater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        String currentPicturePath = this.updater.getCurrentPicturePath();
        if (currentPicturePath != null) {
            args.putString("path", currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        this.updater.setCurrentPicturePath(args.getString("path"));
    }

    /* access modifiers changed from: private */
    public boolean onItemLongClick(WallpaperCell view, Object object, int index) {
        Object originalObject = object;
        if (object instanceof ColorWallpaper) {
            object = ((ColorWallpaper) object).parentWallpaper;
        }
        if (this.actionBar.isActionModeShowed() || getParentActivity() == null || !(object instanceof TLRPC.WallPaper)) {
            return false;
        }
        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        this.selectedWallPapers.put(((TLRPC.WallPaper) object).id, originalObject);
        this.selectedMessagesCountTextView.setNumber(1, false);
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        for (int i = 0; i < this.actionModeViews.size(); i++) {
            View view2 = this.actionModeViews.get(i);
            AndroidUtilities.clearDrawableAnimation(view2);
            animators.add(ObjectAnimator.ofFloat(view2, View.SCALE_Y, new float[]{0.1f, 1.0f}));
        }
        animatorSet.playTogether(animators);
        animatorSet.setDuration(250);
        animatorSet.start();
        this.scrolling = false;
        this.actionBar.showActionMode();
        view.setChecked(index, true, true);
        return true;
    }

    /* access modifiers changed from: private */
    public void onItemClick(WallpaperCell view, Object object, int index) {
        Object object2 = object;
        boolean z = false;
        if (this.actionBar.isActionModeShowed()) {
            Object originalObject = object;
            if (object2 instanceof ColorWallpaper) {
                object2 = ((ColorWallpaper) object2).parentWallpaper;
            }
            if (object2 instanceof TLRPC.WallPaper) {
                TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) object2;
                if (this.selectedWallPapers.indexOfKey(wallPaper.id) >= 0) {
                    this.selectedWallPapers.remove(wallPaper.id);
                } else {
                    this.selectedWallPapers.put(wallPaper.id, originalObject);
                }
                if (this.selectedWallPapers.size() == 0) {
                    this.actionBar.hideActionMode();
                } else {
                    this.selectedMessagesCountTextView.setNumber(this.selectedWallPapers.size(), true);
                }
                this.scrolling = false;
                if (this.selectedWallPapers.indexOfKey(wallPaper.id) >= 0) {
                    z = true;
                }
                view.setChecked(index, z, true);
                return;
            }
            return;
        }
        WallpaperCell wallpaperCell = view;
        int i = index;
        String slug = getWallPaperSlug(object2);
        if (object2 instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper wallPaper2 = (TLRPC.TL_wallPaper) object2;
            if (wallPaper2.pattern) {
                ColorWallpaper colorWallpaper = new ColorWallpaper(wallPaper2.slug, wallPaper2.settings.background_color, wallPaper2.settings.second_background_color, wallPaper2.settings.third_background_color, wallPaper2.settings.fourth_background_color, AndroidUtilities.getWallpaperRotation(wallPaper2.settings.rotation, false), ((float) wallPaper2.settings.intensity) / 100.0f, wallPaper2.settings.motion, (File) null);
                colorWallpaper.pattern = wallPaper2;
                colorWallpaper.parentWallpaper = wallPaper2;
                object2 = colorWallpaper;
            }
        }
        ThemePreviewActivity wallpaperActivity = new ThemePreviewActivity(object2, (Bitmap) null, true, false);
        if (this.currentType == 1) {
            wallpaperActivity.setDelegate(new WallpapersListActivity$$ExternalSyntheticLambda8(this));
        }
        if (this.selectedBackgroundSlug.equals(slug)) {
            wallpaperActivity.setInitialModes(this.selectedBackgroundBlurred, this.selectedBackgroundMotion);
        }
        wallpaperActivity.setPatterns(this.patterns);
        presentFragment(wallpaperActivity);
    }

    private String getWallPaperSlug(Object object) {
        if (object instanceof TLRPC.TL_wallPaper) {
            return ((TLRPC.TL_wallPaper) object).slug;
        }
        if (object instanceof ColorWallpaper) {
            return ((ColorWallpaper) object).slug;
        }
        if (object instanceof FileWallpaper) {
            return ((FileWallpaper) object).slug;
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void updateRowsSelection() {
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof WallpaperCell) {
                WallpaperCell cell = (WallpaperCell) child;
                for (int b = 0; b < 5; b++) {
                    cell.setChecked(b, false, true);
                }
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        ColorWallpaper colorWallpaper;
        int i = id;
        if (i == NotificationCenter.wallpapersDidLoad) {
            ArrayList<TLRPC.WallPaper> arrayList = args[0];
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
            ArrayList<TLRPC.WallPaper> wallPapersToDelete = null;
            int N = arrayList.size();
            for (int a = 0; a < N; a++) {
                TLRPC.WallPaper wallPaper = arrayList.get(a);
                if (!"fqv01SQemVIBAAAApND8LDRUhRU".equals(wallPaper.slug)) {
                    if ((wallPaper instanceof TLRPC.TL_wallPaper) && !(wallPaper.document instanceof TLRPC.TL_documentEmpty)) {
                        if (wallPaper.pattern && wallPaper.document != null && !this.patternsDict.containsKey(Long.valueOf(wallPaper.document.id))) {
                            this.patterns.add(wallPaper);
                            this.patternsDict.put(Long.valueOf(wallPaper.document.id), wallPaper);
                        }
                        this.allWallPapersDict.put(wallPaper.slug, wallPaper);
                        if (this.currentType != 1 && ((!wallPaper.pattern || !(wallPaper.settings == null || wallPaper.settings.background_color == 0)) && (Theme.isCurrentThemeDark() || wallPaper.settings == null || wallPaper.settings.intensity >= 0))) {
                            this.wallPapers.add(wallPaper);
                        }
                    } else if (wallPaper.settings.background_color != 0) {
                        if (wallPaper.settings.second_background_color == 0 || wallPaper.settings.third_background_color == 0) {
                            colorWallpaper = new ColorWallpaper((String) null, wallPaper.settings.background_color, wallPaper.settings.second_background_color, wallPaper.settings.rotation);
                        } else {
                            colorWallpaper = new ColorWallpaper((String) null, wallPaper.settings.background_color, wallPaper.settings.second_background_color, wallPaper.settings.third_background_color, wallPaper.settings.fourth_background_color);
                        }
                        colorWallpaper.slug = wallPaper.slug;
                        colorWallpaper.intensity = ((float) wallPaper.settings.intensity) / 100.0f;
                        colorWallpaper.gradientRotation = AndroidUtilities.getWallpaperRotation(wallPaper.settings.rotation, false);
                        colorWallpaper.parentWallpaper = wallPaper;
                        if (wallPaper.id < 0) {
                            String hash = colorWallpaper.getHash();
                            if (this.localDict.containsKey(hash)) {
                                if (wallPapersToDelete == null) {
                                    wallPapersToDelete = new ArrayList<>();
                                }
                                wallPapersToDelete.add(wallPaper);
                            } else {
                                this.localWallPapers.add(colorWallpaper);
                                this.localDict.put(hash, colorWallpaper);
                            }
                        }
                        if (Theme.isCurrentThemeDark() || wallPaper.settings == null || wallPaper.settings.intensity >= 0) {
                            this.wallPapers.add(colorWallpaper);
                        }
                    }
                }
            }
            if (wallPapersToDelete != null) {
                int N2 = wallPapersToDelete.size();
                for (int a2 = 0; a2 < N2; a2++) {
                    getMessagesStorage().deleteWallpaper(wallPapersToDelete.get(a2).id);
                }
            }
            this.selectedBackgroundSlug = Theme.getSelectedBackgroundSlug();
            fillWallpapersWithCustom();
            loadWallpapers(false);
        } else if (i == NotificationCenter.didSetNewWallpapper) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
            }
            if (this.actionBar != null) {
                this.actionBar.closeSearchField();
            }
        } else if (i == NotificationCenter.wallpapersNeedReload) {
            getMessagesStorage().getWallpapers();
        }
    }

    /* access modifiers changed from: private */
    public void loadWallpapers(boolean force) {
        long acc = 0;
        if (!force) {
            int N = this.allWallPapers.size();
            for (int a = 0; a < N; a++) {
                Object object = this.allWallPapers.get(a);
                if (object instanceof TLRPC.WallPaper) {
                    TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) object;
                    if (wallPaper.id >= 0) {
                        acc = MediaDataController.calcHash(acc, wallPaper.id);
                    }
                }
            }
        }
        TLRPC.TL_account_getWallPapers req = new TLRPC.TL_account_getWallPapers();
        req.hash = acc;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new WallpapersListActivity$$ExternalSyntheticLambda6(this, force)), this.classGuid);
    }

    /* renamed from: lambda$loadWallpapers$6$org-telegram-ui-WallpapersListActivity  reason: not valid java name */
    public /* synthetic */ void m4081lambda$loadWallpapers$6$orgtelegramuiWallpapersListActivity(boolean force, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new WallpapersListActivity$$ExternalSyntheticLambda3(this, response, force));
    }

    /* renamed from: lambda$loadWallpapers$5$org-telegram-ui-WallpapersListActivity  reason: not valid java name */
    public /* synthetic */ void m4080lambda$loadWallpapers$5$orgtelegramuiWallpapersListActivity(TLObject response, boolean force) {
        ColorWallpaper colorWallpaper;
        if (response instanceof TLRPC.TL_account_wallPapers) {
            TLRPC.TL_account_wallPapers res = (TLRPC.TL_account_wallPapers) response;
            this.patterns.clear();
            this.patternsDict.clear();
            if (this.currentType != 1) {
                this.wallPapers.clear();
                this.allWallPapersDict.clear();
                this.allWallPapers.clear();
                this.allWallPapers.addAll(res.wallpapers);
                this.wallPapers.addAll(this.localWallPapers);
            }
            int N = res.wallpapers.size();
            for (int a = 0; a < N; a++) {
                TLRPC.WallPaper wallPaper = res.wallpapers.get(a);
                if (!"fqv01SQemVIBAAAApND8LDRUhRU".equals(wallPaper.slug)) {
                    if ((wallPaper instanceof TLRPC.TL_wallPaper) && !(wallPaper.document instanceof TLRPC.TL_documentEmpty)) {
                        this.allWallPapersDict.put(wallPaper.slug, wallPaper);
                        if (wallPaper.pattern && wallPaper.document != null && !this.patternsDict.containsKey(Long.valueOf(wallPaper.document.id))) {
                            this.patterns.add(wallPaper);
                            this.patternsDict.put(Long.valueOf(wallPaper.document.id), wallPaper);
                        }
                        if (this.currentType != 1 && ((!wallPaper.pattern || !(wallPaper.settings == null || wallPaper.settings.background_color == 0)) && (Theme.isCurrentThemeDark() || wallPaper.settings == null || wallPaper.settings.intensity >= 0))) {
                            this.wallPapers.add(wallPaper);
                        }
                    } else if (wallPaper.settings.background_color != 0 && (Theme.isCurrentThemeDark() || wallPaper.settings == null || wallPaper.settings.intensity >= 0)) {
                        if (wallPaper.settings.second_background_color == 0 || wallPaper.settings.third_background_color == 0) {
                            colorWallpaper = new ColorWallpaper((String) null, wallPaper.settings.background_color, wallPaper.settings.second_background_color, wallPaper.settings.rotation);
                        } else {
                            colorWallpaper = new ColorWallpaper((String) null, wallPaper.settings.background_color, wallPaper.settings.second_background_color, wallPaper.settings.third_background_color, wallPaper.settings.fourth_background_color);
                        }
                        colorWallpaper.slug = wallPaper.slug;
                        colorWallpaper.intensity = ((float) wallPaper.settings.intensity) / 100.0f;
                        colorWallpaper.gradientRotation = AndroidUtilities.getWallpaperRotation(wallPaper.settings.rotation, false);
                        colorWallpaper.parentWallpaper = wallPaper;
                        this.wallPapers.add(colorWallpaper);
                    }
                }
            }
            fillWallpapersWithCustom();
            getMessagesStorage().putWallpapers(res.wallpapers, 1);
        }
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            if (!force) {
                this.listView.smoothScrollToPosition(0);
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v3, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v16, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v17, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v26, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v18, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v29, resolved type: org.telegram.ui.WallpapersListActivity$ColorWallpaper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v19, resolved type: org.telegram.tgnet.TLRPC$TL_wallPaper} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void fillWallpapersWithCustom() {
        /*
            r33 = this;
            r7 = r33
            int r0 = r7.currentType
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            android.content.SharedPreferences r8 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = r7.addedColorWallpaper
            r9 = 0
            if (r0 == 0) goto L_0x0017
            java.util.ArrayList<java.lang.Object> r1 = r7.wallPapers
            r1.remove(r0)
            r7.addedColorWallpaper = r9
        L_0x0017:
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = r7.addedFileWallpaper
            if (r0 == 0) goto L_0x0022
            java.util.ArrayList<java.lang.Object> r1 = r7.wallPapers
            r1.remove(r0)
            r7.addedFileWallpaper = r9
        L_0x0022:
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = r7.catsWallpaper
            if (r0 != 0) goto L_0x0042
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
            goto L_0x0047
        L_0x0042:
            java.util.ArrayList<java.lang.Object> r1 = r7.wallPapers
            r1.remove(r0)
        L_0x0047:
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = r7.themeWallpaper
            if (r0 == 0) goto L_0x0050
            java.util.ArrayList<java.lang.Object> r1 = r7.wallPapers
            r1.remove(r0)
        L_0x0050:
            r0 = 0
            r1 = 0
            java.util.ArrayList<java.lang.Object> r2 = r7.wallPapers
            int r2 = r2.size()
        L_0x0058:
            r3 = 981668463(0x3a83126f, float:0.001)
            r4 = 1120403456(0x42CLASSNAME, float:100.0)
            java.lang.String r10 = "c"
            r11 = 0
            if (r1 >= r2) goto L_0x012b
            java.util.ArrayList<java.lang.Object> r5 = r7.wallPapers
            java.lang.Object r5 = r5.get(r1)
            boolean r6 = r5 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r6 == 0) goto L_0x00ba
            r6 = r5
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r6 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r6
            java.lang.String r12 = r6.slug
            if (r12 == 0) goto L_0x007f
            java.util.HashMap<java.lang.String, java.lang.Object> r12 = r7.allWallPapersDict
            java.lang.String r13 = r6.slug
            java.lang.Object r12 = r12.get(r13)
            org.telegram.tgnet.TLRPC$TL_wallPaper r12 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r12
            r6.pattern = r12
        L_0x007f:
            java.lang.String r12 = r6.slug
            boolean r12 = r10.equals(r12)
            if (r12 != 0) goto L_0x0095
            java.lang.String r12 = r6.slug
            if (r12 == 0) goto L_0x0095
            java.lang.String r12 = r7.selectedBackgroundSlug
            java.lang.String r13 = r6.slug
            boolean r12 = android.text.TextUtils.equals(r12, r13)
            if (r12 == 0) goto L_0x0126
        L_0x0095:
            int r12 = r7.selectedColor
            int r13 = r6.color
            if (r12 != r13) goto L_0x0126
            int r12 = r7.selectedGradientColor1
            int r13 = r6.gradientColor1
            if (r12 != r13) goto L_0x0126
            int r12 = r7.selectedGradientColor2
            int r13 = r6.gradientColor2
            if (r12 != r13) goto L_0x0126
            int r12 = r7.selectedGradientColor3
            int r13 = r6.gradientColor3
            if (r12 != r13) goto L_0x0126
            int r12 = r7.selectedGradientColor1
            if (r12 == 0) goto L_0x00b7
            int r12 = r7.selectedGradientRotation
            int r13 = r6.gradientRotation
            if (r12 != r13) goto L_0x0126
        L_0x00b7:
            r0 = r6
            goto L_0x012b
        L_0x00ba:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper
            if (r6 == 0) goto L_0x0126
            r6 = r5
            org.telegram.tgnet.TLRPC$TL_wallPaper r6 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r6
            org.telegram.tgnet.TLRPC$WallPaperSettings r12 = r6.settings
            if (r12 == 0) goto L_0x0127
            java.lang.String r12 = r7.selectedBackgroundSlug
            java.lang.String r13 = r6.slug
            boolean r12 = android.text.TextUtils.equals(r12, r13)
            if (r12 == 0) goto L_0x0127
            int r12 = r7.selectedColor
            org.telegram.tgnet.TLRPC$WallPaperSettings r13 = r6.settings
            int r13 = r13.background_color
            int r13 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r13)
            if (r12 != r13) goto L_0x0127
            int r12 = r7.selectedGradientColor1
            org.telegram.tgnet.TLRPC$WallPaperSettings r13 = r6.settings
            int r13 = r13.second_background_color
            int r13 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r13)
            if (r12 != r13) goto L_0x0127
            int r12 = r7.selectedGradientColor2
            org.telegram.tgnet.TLRPC$WallPaperSettings r13 = r6.settings
            int r13 = r13.third_background_color
            int r13 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r13)
            if (r12 != r13) goto L_0x0127
            int r12 = r7.selectedGradientColor3
            org.telegram.tgnet.TLRPC$WallPaperSettings r13 = r6.settings
            int r13 = r13.fourth_background_color
            int r13 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r13)
            if (r12 != r13) goto L_0x0127
            int r12 = r7.selectedGradientColor1
            if (r12 == 0) goto L_0x010f
            int r12 = r7.selectedGradientRotation
            org.telegram.tgnet.TLRPC$WallPaperSettings r13 = r6.settings
            int r13 = r13.rotation
            int r13 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r13, r11)
            if (r12 != r13) goto L_0x0127
        L_0x010f:
            org.telegram.tgnet.TLRPC$WallPaperSettings r12 = r6.settings
            int r12 = r12.intensity
            float r12 = (float) r12
            float r12 = r12 / r4
            float r12 = org.telegram.ui.ActionBar.Theme.getThemeIntensity(r12)
            float r13 = r7.selectedIntensity
            float r12 = r12 - r13
            float r12 = java.lang.Math.abs(r12)
            int r12 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r12 > 0) goto L_0x0127
            r0 = r6
            goto L_0x012b
        L_0x0126:
        L_0x0127:
            int r1 = r1 + 1
            goto L_0x0058
        L_0x012b:
            r1 = 0
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.WallPaper
            if (r2 == 0) goto L_0x01a9
            r2 = r0
            org.telegram.tgnet.TLRPC$TL_wallPaper r2 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r2
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r5 = r5.overrideWallpaper
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r2.settings
            if (r6 == 0) goto L_0x019e
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r2.settings
            if (r6 == 0) goto L_0x019b
            int r6 = r7.selectedColor
            org.telegram.tgnet.TLRPC$WallPaperSettings r12 = r2.settings
            int r12 = r12.background_color
            int r12 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r12)
            if (r6 != r12) goto L_0x019e
            int r6 = r7.selectedGradientColor1
            org.telegram.tgnet.TLRPC$WallPaperSettings r12 = r2.settings
            int r12 = r12.second_background_color
            int r12 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r12)
            if (r6 != r12) goto L_0x019e
            int r6 = r7.selectedGradientColor2
            org.telegram.tgnet.TLRPC$WallPaperSettings r12 = r2.settings
            int r12 = r12.third_background_color
            int r12 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r12)
            if (r6 != r12) goto L_0x019e
            int r6 = r7.selectedGradientColor3
            org.telegram.tgnet.TLRPC$WallPaperSettings r12 = r2.settings
            int r12 = r12.fourth_background_color
            int r12 = org.telegram.ui.ActionBar.Theme.getWallpaperColor(r12)
            if (r6 != r12) goto L_0x019e
            int r6 = r7.selectedGradientColor1
            if (r6 == 0) goto L_0x019b
            int r6 = r7.selectedGradientColor2
            if (r6 != 0) goto L_0x019b
            int r6 = r7.selectedGradientRotation
            org.telegram.tgnet.TLRPC$WallPaperSettings r12 = r2.settings
            int r12 = r12.rotation
            int r12 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r12, r11)
            if (r6 == r12) goto L_0x019b
            org.telegram.tgnet.TLRPC$WallPaperSettings r6 = r2.settings
            int r6 = r6.intensity
            float r6 = (float) r6
            float r6 = r6 / r4
            float r4 = org.telegram.ui.ActionBar.Theme.getThemeIntensity(r6)
            float r6 = r7.selectedIntensity
            float r4 = r4 - r6
            float r4 = java.lang.Math.abs(r4)
            int r3 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x019b
            goto L_0x019e
        L_0x019b:
            java.lang.String r3 = r7.selectedBackgroundSlug
            goto L_0x01a2
        L_0x019e:
            r1 = r2
            r0 = 0
            java.lang.String r3 = ""
        L_0x01a2:
            long r4 = r2.id
            r12 = r0
            r13 = r1
            r14 = r3
            r15 = r4
            goto L_0x01c8
        L_0x01a9:
            java.lang.String r3 = r7.selectedBackgroundSlug
            boolean r2 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r2 == 0) goto L_0x01c2
            r2 = r0
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r2 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r2
            org.telegram.tgnet.TLRPC$WallPaper r2 = r2.parentWallpaper
            if (r2 == 0) goto L_0x01c2
            r2 = r0
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r2 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r2
            org.telegram.tgnet.TLRPC$WallPaper r2 = r2.parentWallpaper
            long r4 = r2.id
            r12 = r0
            r13 = r1
            r14 = r3
            r15 = r4
            goto L_0x01c8
        L_0x01c2:
            r4 = 0
            r12 = r0
            r13 = r1
            r14 = r3
            r15 = r4
        L_0x01c8:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getCurrentTheme()
            boolean r17 = r0.isDark()
            java.util.ArrayList<java.lang.Object> r0 = r7.wallPapers     // Catch:{ Exception -> 0x01e3 }
            org.telegram.ui.WallpapersListActivity$$ExternalSyntheticLambda4 r6 = new org.telegram.ui.WallpapersListActivity$$ExternalSyntheticLambda4     // Catch:{ Exception -> 0x01e3 }
            r1 = r6
            r2 = r33
            r3 = r15
            r5 = r14
            r9 = r6
            r6 = r17
            r1.<init>(r2, r3, r5, r6)     // Catch:{ Exception -> 0x01e3 }
            java.util.Collections.sort(r0, r9)     // Catch:{ Exception -> 0x01e3 }
            goto L_0x01e7
        L_0x01e3:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01e7:
            boolean r0 = org.telegram.ui.ActionBar.Theme.hasWallpaperFromTheme()
            if (r0 == 0) goto L_0x0209
            boolean r0 = org.telegram.ui.ActionBar.Theme.isThemeWallpaperPublic()
            if (r0 != 0) goto L_0x0209
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = r7.themeWallpaper
            if (r0 != 0) goto L_0x0201
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = new org.telegram.ui.WallpapersListActivity$FileWallpaper
            java.lang.String r1 = "t"
            r2 = -2
            r0.<init>((java.lang.String) r1, (int) r2, (int) r2)
            r7.themeWallpaper = r0
        L_0x0201:
            java.util.ArrayList<java.lang.Object> r0 = r7.wallPapers
            org.telegram.ui.WallpapersListActivity$FileWallpaper r1 = r7.themeWallpaper
            r0.add(r11, r1)
            goto L_0x020c
        L_0x0209:
            r1 = 0
            r7.themeWallpaper = r1
        L_0x020c:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            java.lang.String r1 = r7.selectedBackgroundSlug
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            java.lang.String r2 = "d"
            if (r1 != 0) goto L_0x028c
            java.lang.String r1 = r7.selectedBackgroundSlug
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x0226
            if (r12 != 0) goto L_0x0226
            goto L_0x028c
        L_0x0226:
            if (r12 != 0) goto L_0x0282
            int r1 = r7.selectedColor
            if (r1 == 0) goto L_0x0282
            java.lang.String r1 = r7.selectedBackgroundSlug
            boolean r1 = r10.equals(r1)
            if (r1 == 0) goto L_0x0282
            int r1 = r7.selectedGradientColor1
            if (r1 == 0) goto L_0x0262
            int r1 = r7.selectedGradientColor2
            if (r1 == 0) goto L_0x0262
            int r1 = r7.selectedGradientColor3
            if (r1 == 0) goto L_0x0262
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r4 = r7.selectedBackgroundSlug
            int r5 = r7.selectedColor
            int r6 = r7.selectedGradientColor1
            int r9 = r7.selectedGradientColor2
            int r10 = r7.selectedGradientColor3
            r18 = r1
            r19 = r4
            r20 = r5
            r21 = r6
            r22 = r9
            r23 = r10
            r18.<init>(r19, r20, r21, r22, r23)
            r7.addedColorWallpaper = r1
            int r4 = r7.selectedGradientRotation
            r1.gradientRotation = r4
            goto L_0x0271
        L_0x0262:
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r4 = r7.selectedBackgroundSlug
            int r5 = r7.selectedColor
            int r6 = r7.selectedGradientColor1
            int r9 = r7.selectedGradientRotation
            r1.<init>(r4, r5, r6, r9)
            r7.addedColorWallpaper = r1
        L_0x0271:
            java.util.ArrayList<java.lang.Object> r1 = r7.wallPapers
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r4 = r7.addedColorWallpaper
            r1.add(r11, r4)
            r28 = r8
            r29 = r12
            r30 = r14
            r31 = r15
            goto L_0x037d
        L_0x0282:
            r28 = r8
            r29 = r12
            r30 = r14
            r31 = r15
            goto L_0x037d
        L_0x028c:
            java.lang.String r1 = r7.selectedBackgroundSlug
            boolean r1 = r10.equals(r1)
            if (r1 != 0) goto L_0x02f2
            int r1 = r7.selectedColor
            if (r1 == 0) goto L_0x02f2
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r1 = r0.overrideWallpaper
            if (r1 == 0) goto L_0x02e8
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r4 = r7.selectedBackgroundSlug
            int r5 = r7.selectedColor
            int r6 = r7.selectedGradientColor1
            int r9 = r7.selectedGradientColor2
            int r10 = r7.selectedGradientColor3
            int r3 = r7.selectedGradientRotation
            float r11 = r7.selectedIntensity
            r28 = r8
            boolean r8 = r7.selectedBackgroundMotion
            r29 = r12
            java.io.File r12 = new java.io.File
            r30 = r14
            java.io.File r14 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            r31 = r15
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r15 = r0.overrideWallpaper
            java.lang.String r15 = r15.fileName
            r12.<init>(r14, r15)
            r18 = r1
            r19 = r4
            r20 = r5
            r21 = r6
            r22 = r9
            r23 = r10
            r24 = r3
            r25 = r11
            r26 = r8
            r27 = r12
            r18.<init>(r19, r20, r21, r22, r23, r24, r25, r26, r27)
            r7.addedColorWallpaper = r1
            r1.pattern = r13
            java.util.ArrayList<java.lang.Object> r1 = r7.wallPapers
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r3 = r7.addedColorWallpaper
            r4 = 0
            r1.add(r4, r3)
            goto L_0x037d
        L_0x02e8:
            r28 = r8
            r29 = r12
            r30 = r14
            r31 = r15
            goto L_0x037d
        L_0x02f2:
            r28 = r8
            r29 = r12
            r30 = r14
            r31 = r15
            int r1 = r7.selectedColor
            if (r1 == 0) goto L_0x0340
            int r1 = r7.selectedGradientColor1
            if (r1 == 0) goto L_0x0328
            int r1 = r7.selectedGradientColor2
            if (r1 == 0) goto L_0x0328
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r3 = r7.selectedBackgroundSlug
            int r4 = r7.selectedColor
            int r5 = r7.selectedGradientColor1
            int r6 = r7.selectedGradientColor2
            int r8 = r7.selectedGradientColor3
            r18 = r1
            r19 = r3
            r20 = r4
            r21 = r5
            r22 = r6
            r23 = r8
            r18.<init>(r19, r20, r21, r22, r23)
            r7.addedColorWallpaper = r1
            int r3 = r7.selectedGradientRotation
            r1.gradientRotation = r3
            goto L_0x0337
        L_0x0328:
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper
            java.lang.String r3 = r7.selectedBackgroundSlug
            int r4 = r7.selectedColor
            int r5 = r7.selectedGradientColor1
            int r6 = r7.selectedGradientRotation
            r1.<init>(r3, r4, r5, r6)
            r7.addedColorWallpaper = r1
        L_0x0337:
            java.util.ArrayList<java.lang.Object> r1 = r7.wallPapers
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r3 = r7.addedColorWallpaper
            r4 = 0
            r1.add(r4, r3)
            goto L_0x037d
        L_0x0340:
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r1 = r0.overrideWallpaper
            if (r1 == 0) goto L_0x037d
            java.util.HashMap<java.lang.String, java.lang.Object> r1 = r7.allWallPapersDict
            java.lang.String r3 = r7.selectedBackgroundSlug
            boolean r1 = r1.containsKey(r3)
            if (r1 != 0) goto L_0x037d
            org.telegram.ui.WallpapersListActivity$FileWallpaper r1 = new org.telegram.ui.WallpapersListActivity$FileWallpaper
            java.lang.String r3 = r7.selectedBackgroundSlug
            java.io.File r4 = new java.io.File
            java.io.File r5 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r6 = r0.overrideWallpaper
            java.lang.String r6 = r6.fileName
            r4.<init>(r5, r6)
            java.io.File r5 = new java.io.File
            java.io.File r6 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r8 = r0.overrideWallpaper
            java.lang.String r8 = r8.originalFileName
            r5.<init>(r6, r8)
            r1.<init>((java.lang.String) r3, (java.io.File) r4, (java.io.File) r5)
            r7.addedFileWallpaper = r1
            java.util.ArrayList<java.lang.Object> r3 = r7.wallPapers
            org.telegram.ui.WallpapersListActivity$FileWallpaper r4 = r7.themeWallpaper
            if (r4 == 0) goto L_0x0379
            r4 = 1
            goto L_0x037a
        L_0x0379:
            r4 = 0
        L_0x037a:
            r3.add(r4, r1)
        L_0x037d:
            java.lang.String r1 = r7.selectedBackgroundSlug
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x0397
            java.util.ArrayList<java.lang.Object> r1 = r7.wallPapers
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x038e
            goto L_0x0397
        L_0x038e:
            java.util.ArrayList<java.lang.Object> r1 = r7.wallPapers
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r2 = r7.catsWallpaper
            r3 = 1
            r1.add(r3, r2)
            goto L_0x039f
        L_0x0397:
            java.util.ArrayList<java.lang.Object> r1 = r7.wallPapers
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r2 = r7.catsWallpaper
            r3 = 0
            r1.add(r3, r2)
        L_0x039f:
            r33.updateRows()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpapersListActivity.fillWallpapersWithCustom():void");
    }

    /* renamed from: lambda$fillWallpapersWithCustom$7$org-telegram-ui-WallpapersListActivity  reason: not valid java name */
    public /* synthetic */ int m4079x891910bd(long idFinal, String slugFinal, boolean currentThemeDark, Object o1, Object o2) {
        if (o1 instanceof ColorWallpaper) {
            o1 = ((ColorWallpaper) o1).parentWallpaper;
        }
        if (o2 instanceof ColorWallpaper) {
            o2 = ((ColorWallpaper) o2).parentWallpaper;
        }
        if (!(o1 instanceof TLRPC.WallPaper) || !(o2 instanceof TLRPC.WallPaper)) {
            return 0;
        }
        TLRPC.WallPaper wallPaper1 = (TLRPC.WallPaper) o1;
        TLRPC.WallPaper wallPaper2 = (TLRPC.WallPaper) o2;
        if (idFinal != 0) {
            if (wallPaper1.id == idFinal) {
                return -1;
            }
            if (wallPaper2.id == idFinal) {
                return 1;
            }
        } else if (slugFinal.equals(wallPaper1.slug)) {
            return -1;
        } else {
            if (slugFinal.equals(wallPaper2.slug)) {
                return 1;
            }
        }
        if (!currentThemeDark) {
            if ("qeZWES8rGVIEAAAARfWlK1lnfiI".equals(wallPaper1.slug)) {
                return -1;
            }
            if ("qeZWES8rGVIEAAAARfWlK1lnfiI".equals(wallPaper2.slug)) {
                return 1;
            }
        }
        int index1 = this.allWallPapers.indexOf(wallPaper1);
        int index2 = this.allWallPapers.indexOf(wallPaper2);
        if ((!wallPaper1.dark || !wallPaper2.dark) && (wallPaper1.dark || wallPaper2.dark)) {
            return (!wallPaper1.dark || wallPaper2.dark) ? currentThemeDark ? 1 : -1 : currentThemeDark ? -1 : 1;
        }
        if (index1 > index2) {
            return 1;
        }
        if (index1 < index2) {
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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(AndroidUtilities.dp(50.0f), AndroidUtilities.dp(62.0f));
        }

        public void setColor(int value) {
            this.color = value;
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
            private CategoryAdapterRecycler() {
            }

            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerListView.Holder(new ColorCell(SearchAdapter.this.mContext));
            }

            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return true;
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((ColorCell) holder.itemView).setColor(WallpapersListActivity.searchColors[position]);
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
        public void processSearch(String text, boolean now) {
            if (!(text == null || this.selectedColor == null)) {
                text = "#color" + this.selectedColor + " " + text;
            }
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(text)) {
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
                String textFinal = text;
                if (now) {
                    doSearch(textFinal);
                } else {
                    WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda1 wallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda1 = new WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda1(this, textFinal);
                    this.searchRunnable = wallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda1;
                    AndroidUtilities.runOnUIThread(wallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda1, 500);
                }
            }
            notifyDataSetChanged();
        }

        /* renamed from: lambda$processSearch$0$org-telegram-ui-WallpapersListActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m4087xd2243515(String textFinal) {
            doSearch(textFinal);
            this.searchRunnable = null;
        }

        private void doSearch(String textFinal) {
            this.searchResult.clear();
            this.searchResultKeys.clear();
            this.bingSearchEndReached = true;
            searchImages(textFinal, "", true);
            this.lastSearchString = textFinal;
            notifyDataSetChanged();
        }

        private void searchBotUser() {
            if (!this.searchingUser) {
                this.searchingUser = true;
                TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
                req.username = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).imageSearchBot;
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(req, new WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda3(this));
            }
        }

        /* renamed from: lambda$searchBotUser$2$org-telegram-ui-WallpapersListActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m4089x7955a680(TLObject response, TLRPC.TL_error error) {
            if (response != null) {
                AndroidUtilities.runOnUIThread(new WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda2(this, response));
            }
        }

        /* renamed from: lambda$searchBotUser$1$org-telegram-ui-WallpapersListActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m4088x5f3a27e1(TLObject response) {
            TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
            MessagesController.getInstance(WallpapersListActivity.this.currentAccount).putUsers(res.users, false);
            MessagesController.getInstance(WallpapersListActivity.this.currentAccount).putChats(res.chats, false);
            WallpapersListActivity.this.getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
            String str = this.lastSearchImageString;
            this.lastSearchImageString = null;
            searchImages(str, "", false);
        }

        public void loadMoreResults() {
            if (!this.bingSearchEndReached && this.imageReqId == 0) {
                searchImages(this.lastSearchString, this.nextImagesSearchOffset, true);
            }
        }

        private void searchImages(String query, String offset, boolean searchUser) {
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
            this.lastSearchImageString = query;
            TLObject object = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).getUserOrChat(MessagesController.getInstance(WallpapersListActivity.this.currentAccount).imageSearchBot);
            if (object instanceof TLRPC.User) {
                TLRPC.TL_messages_getInlineBotResults req = new TLRPC.TL_messages_getInlineBotResults();
                req.query = "#wallpaper " + query;
                req.bot = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).getInputUser((TLRPC.User) object);
                req.offset = offset;
                req.peer = new TLRPC.TL_inputPeerEmpty();
                int token = this.lastSearchToken + 1;
                this.lastSearchToken = token;
                this.imageReqId = ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(req, new WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda4(this, token));
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).bindRequestToGuid(this.imageReqId, WallpapersListActivity.this.classGuid);
            } else if (searchUser) {
                searchBotUser();
            }
        }

        /* renamed from: lambda$searchImages$4$org-telegram-ui-WallpapersListActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m4091xe82ce30e(int token, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda0(this, token, response));
        }

        /* renamed from: lambda$searchImages$3$org-telegram-ui-WallpapersListActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m4090xce11646f(int token, TLObject response) {
            if (token == this.lastSearchToken) {
                boolean z = false;
                this.imageReqId = 0;
                int oldCount = this.searchResult.size();
                if (response != null) {
                    TLRPC.messages_BotResults res = (TLRPC.messages_BotResults) response;
                    this.nextImagesSearchOffset = res.next_offset;
                    int count = res.results.size();
                    for (int a = 0; a < count; a++) {
                        TLRPC.BotInlineResult result = res.results.get(a);
                        if ("photo".equals(result.type) && !this.searchResultKeys.containsKey(result.id)) {
                            MediaController.SearchImage bingImage = new MediaController.SearchImage();
                            if (result.photo != null) {
                                TLRPC.PhotoSize size = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, AndroidUtilities.getPhotoSize());
                                TLRPC.PhotoSize size2 = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, 320);
                                if (size != null) {
                                    bingImage.width = size.w;
                                    bingImage.height = size.h;
                                    bingImage.photoSize = size;
                                    bingImage.photo = result.photo;
                                    bingImage.size = size.size;
                                    bingImage.thumbPhotoSize = size2;
                                }
                            } else if (result.content != null) {
                                int b = 0;
                                while (true) {
                                    if (b >= result.content.attributes.size()) {
                                        break;
                                    }
                                    TLRPC.DocumentAttribute attribute = result.content.attributes.get(b);
                                    if (attribute instanceof TLRPC.TL_documentAttributeImageSize) {
                                        bingImage.width = attribute.w;
                                        bingImage.height = attribute.h;
                                        break;
                                    }
                                    b++;
                                }
                                if (result.thumb != null) {
                                    bingImage.thumbUrl = result.thumb.url;
                                } else {
                                    bingImage.thumbUrl = null;
                                }
                                bingImage.imageUrl = result.content.url;
                                bingImage.size = result.content.size;
                            }
                            bingImage.id = result.id;
                            bingImage.type = 0;
                            this.searchResult.add(bingImage);
                            this.searchResultKeys.put(bingImage.id, bingImage);
                        }
                    }
                    if (oldCount == this.searchResult.size() || this.nextImagesSearchOffset == null) {
                        z = true;
                    }
                    this.bingSearchEndReached = z;
                }
                if (oldCount != this.searchResult.size()) {
                    int prevLastRow = oldCount % WallpapersListActivity.this.columnsCount;
                    int oldRowCount = (int) Math.ceil((double) (((float) oldCount) / ((float) WallpapersListActivity.this.columnsCount)));
                    if (prevLastRow != 0) {
                        notifyItemChanged(((int) Math.ceil((double) (((float) oldCount) / ((float) WallpapersListActivity.this.columnsCount)))) - 1);
                    }
                    WallpapersListActivity.this.searchAdapter.notifyItemRangeInserted(oldRowCount, ((int) Math.ceil((double) (((float) this.searchResult.size()) / ((float) WallpapersListActivity.this.columnsCount)))) - oldRowCount);
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

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 2;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: org.telegram.ui.WallpapersListActivity$SearchAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.ui.WallpapersListActivity$SearchAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r8, int r9) {
            /*
                r7 = this;
                r0 = 0
                switch(r9) {
                    case 0: goto L_0x004e;
                    case 1: goto L_0x000e;
                    case 2: goto L_0x0005;
                    default: goto L_0x0004;
                }
            L_0x0004:
                goto L_0x0057
            L_0x0005:
                org.telegram.ui.Cells.GraySectionCell r1 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r2 = r7.mContext
                r1.<init>(r2)
                r0 = r1
                goto L_0x0057
            L_0x000e:
                org.telegram.ui.WallpapersListActivity$SearchAdapter$2 r1 = new org.telegram.ui.WallpapersListActivity$SearchAdapter$2
                android.content.Context r2 = r7.mContext
                r1.<init>(r2)
                r2 = 0
                r1.setItemAnimator(r2)
                r1.setLayoutAnimation(r2)
                org.telegram.ui.WallpapersListActivity$SearchAdapter$3 r3 = new org.telegram.ui.WallpapersListActivity$SearchAdapter$3
                android.content.Context r4 = r7.mContext
                r3.<init>(r4)
                r4 = 1088421888(0x40e00000, float:7.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                r6 = 0
                r1.setPadding(r5, r6, r4, r6)
                r1.setClipToPadding(r6)
                r3.setOrientation(r6)
                r1.setLayoutManager(r3)
                org.telegram.ui.WallpapersListActivity$SearchAdapter$CategoryAdapterRecycler r4 = new org.telegram.ui.WallpapersListActivity$SearchAdapter$CategoryAdapterRecycler
                r4.<init>()
                r1.setAdapter(r4)
                org.telegram.ui.WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda5 r2 = new org.telegram.ui.WallpapersListActivity$SearchAdapter$$ExternalSyntheticLambda5
                r2.<init>(r7)
                r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r2)
                r0 = r1
                r7.innerListView = r1
                goto L_0x0057
            L_0x004e:
                org.telegram.ui.WallpapersListActivity$SearchAdapter$1 r1 = new org.telegram.ui.WallpapersListActivity$SearchAdapter$1
                android.content.Context r2 = r7.mContext
                r1.<init>(r2)
                r0 = r1
            L_0x0057:
                r1 = 1
                r2 = -1
                if (r9 != r1) goto L_0x006a
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r3 = 1114636288(0x42700000, float:60.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r1.<init>((int) r2, (int) r3)
                r0.setLayoutParams(r1)
                goto L_0x0073
            L_0x006a:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r3 = -2
                r1.<init>((int) r2, (int) r3)
                r0.setLayoutParams(r1)
            L_0x0073:
                org.telegram.ui.Components.RecyclerListView$Holder r1 = new org.telegram.ui.Components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpapersListActivity.SearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* renamed from: lambda$onCreateViewHolder$5$org-telegram-ui-WallpapersListActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m4086x5CLASSNAME(View view1, int position) {
            String color = LocaleController.getString("BackgroundSearchColor", NUM);
            Spannable spannable = new SpannableString(color + " " + LocaleController.getString(WallpapersListActivity.searchColorsNames[position], WallpapersListActivity.searchColorsNamesR[position]));
            spannable.setSpan(new ForegroundColorSpan(Theme.getColor("actionBarDefaultSubtitle")), color.length(), spannable.length(), 33);
            WallpapersListActivity.this.searchItem.setSearchFieldCaption(spannable);
            WallpapersListActivity.this.searchItem.setSearchFieldHint((CharSequence) null);
            WallpapersListActivity.this.searchItem.setSearchFieldText("", true);
            this.selectedColor = WallpapersListActivity.searchColorsNames[position];
            processSearch("", true);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    WallpaperCell wallpaperCell = (WallpaperCell) holder.itemView;
                    int position2 = position * WallpapersListActivity.this.columnsCount;
                    int totalRows = (int) Math.ceil((double) (((float) this.searchResult.size()) / ((float) WallpapersListActivity.this.columnsCount)));
                    int access$4500 = WallpapersListActivity.this.columnsCount;
                    boolean z = false;
                    boolean z2 = position2 == 0;
                    if (position2 / WallpapersListActivity.this.columnsCount == totalRows - 1) {
                        z = true;
                    }
                    wallpaperCell.setParams(access$4500, z2, z);
                    for (int a = 0; a < WallpapersListActivity.this.columnsCount; a++) {
                        int p = position2 + a;
                        wallpaperCell.setWallpaper(WallpapersListActivity.this.currentType, a, p < this.searchResult.size() ? this.searchResult.get(p) : null, "", (Drawable) null, false);
                    }
                    return;
                case 2:
                    ((GraySectionCell) holder.itemView).setText(LocaleController.getString("SearchByColor", NUM));
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (!TextUtils.isEmpty(this.lastSearchString)) {
                return 0;
            }
            if (position == 0) {
                return 2;
            }
            return 1;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public int getItemCount() {
            return WallpapersListActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            int i = NUM;
            switch (viewType) {
                case 0:
                    view = new TextCell(this.mContext);
                    break;
                case 1:
                    view = new ShadowSectionCell(this.mContext);
                    Context context = this.mContext;
                    if (WallpapersListActivity.this.wallPaperStartRow != -1) {
                        i = NUM;
                    }
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(context, i, "windowBackgroundGrayShadow"));
                    combinedDrawable.setFullsize(true);
                    view.setBackgroundDrawable(combinedDrawable);
                    break;
                case 3:
                    view = new TextInfoPrivacyCell(this.mContext);
                    CombinedDrawable combinedDrawable2 = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    combinedDrawable2.setFullsize(true);
                    view.setBackgroundDrawable(combinedDrawable2);
                    break;
                default:
                    view = new WallpaperCell(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void onWallpaperClick(Object wallPaper, int index) {
                            WallpapersListActivity.this.onItemClick(this, wallPaper, index);
                        }

                        /* access modifiers changed from: protected */
                        public boolean onWallpaperLongClick(Object wallPaper, int index) {
                            return WallpapersListActivity.this.onItemLongClick(this, wallPaper, index);
                        }
                    };
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Object selectedWallpaper;
            long id;
            Object selectedWallpaper2;
            Object selectedWallpaper3;
            long id2;
            Object selectedWallpaper4;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    TextCell textCell = (TextCell) viewHolder.itemView;
                    if (i == WallpapersListActivity.this.uploadImageRow) {
                        textCell.setTextAndIcon(LocaleController.getString("SelectFromGallery", NUM), NUM, true);
                        return;
                    } else if (i == WallpapersListActivity.this.setColorRow) {
                        textCell.setTextAndIcon(LocaleController.getString("SetColor", NUM), NUM, true);
                        return;
                    } else if (i == WallpapersListActivity.this.resetRow) {
                        textCell.setText(LocaleController.getString("ResetChatBackgrounds", NUM), false);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    WallpaperCell wallpaperCell = (WallpaperCell) viewHolder.itemView;
                    int position2 = (i - WallpapersListActivity.this.wallPaperStartRow) * WallpapersListActivity.this.columnsCount;
                    wallpaperCell.setParams(WallpapersListActivity.this.columnsCount, position2 == 0, position2 / WallpapersListActivity.this.columnsCount == WallpapersListActivity.this.totalWallpaperRows - 1);
                    int a = 0;
                    while (a < WallpapersListActivity.this.columnsCount) {
                        int p = position2 + a;
                        Object object = p < WallpapersListActivity.this.wallPapers.size() ? WallpapersListActivity.this.wallPapers.get(p) : null;
                        if (object instanceof TLRPC.TL_wallPaper) {
                            TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) object;
                            Theme.OverrideWallpaperInfo overrideWallpaperInfo = Theme.getActiveTheme().overrideWallpaper;
                            if (!WallpapersListActivity.this.selectedBackgroundSlug.equals(wallPaper.slug) || !(!WallpapersListActivity.this.selectedBackgroundSlug.equals(wallPaper.slug) || wallPaper.settings == null || (WallpapersListActivity.this.selectedColor == Theme.getWallpaperColor(wallPaper.settings.background_color) && WallpapersListActivity.this.selectedGradientColor1 == Theme.getWallpaperColor(wallPaper.settings.second_background_color) && WallpapersListActivity.this.selectedGradientColor2 == Theme.getWallpaperColor(wallPaper.settings.third_background_color) && WallpapersListActivity.this.selectedGradientColor3 == Theme.getWallpaperColor(wallPaper.settings.fourth_background_color) && (WallpapersListActivity.this.selectedGradientColor1 == 0 || WallpapersListActivity.this.selectedGradientColor2 != 0 || WallpapersListActivity.this.selectedGradientRotation == AndroidUtilities.getWallpaperRotation(wallPaper.settings.rotation, z) || !wallPaper.pattern || Math.abs(Theme.getThemeIntensity(((float) wallPaper.settings.intensity) / 100.0f) - WallpapersListActivity.this.selectedIntensity) <= 0.001f)))) {
                                selectedWallpaper4 = null;
                            } else {
                                selectedWallpaper4 = wallPaper;
                            }
                            selectedWallpaper = selectedWallpaper4;
                            id = wallPaper.id;
                        } else if (object instanceof ColorWallpaper) {
                            ColorWallpaper colorWallpaper = (ColorWallpaper) object;
                            if ("d".equals(colorWallpaper.slug) && WallpapersListActivity.this.selectedBackgroundSlug.equals(colorWallpaper.slug)) {
                                selectedWallpaper3 = object;
                            } else if (colorWallpaper.color != WallpapersListActivity.this.selectedColor || colorWallpaper.gradientColor1 != WallpapersListActivity.this.selectedGradientColor1 || colorWallpaper.gradientColor2 != WallpapersListActivity.this.selectedGradientColor2 || colorWallpaper.gradientColor3 != WallpapersListActivity.this.selectedGradientColor3 || (WallpapersListActivity.this.selectedGradientColor1 != 0 && colorWallpaper.gradientRotation != WallpapersListActivity.this.selectedGradientRotation)) {
                                selectedWallpaper3 = null;
                            } else if ((!"c".equals(WallpapersListActivity.this.selectedBackgroundSlug) || colorWallpaper.slug == null) && ("c".equals(WallpapersListActivity.this.selectedBackgroundSlug) || (TextUtils.equals(WallpapersListActivity.this.selectedBackgroundSlug, colorWallpaper.slug) && ((int) (colorWallpaper.intensity * 100.0f)) == ((int) (WallpapersListActivity.this.selectedIntensity * 100.0f))))) {
                                selectedWallpaper3 = object;
                            } else {
                                selectedWallpaper3 = null;
                            }
                            if (colorWallpaper.parentWallpaper != null) {
                                id2 = colorWallpaper.parentWallpaper.id;
                            } else {
                                id2 = 0;
                            }
                            selectedWallpaper = selectedWallpaper3;
                            id = id2;
                        } else if (object instanceof FileWallpaper) {
                            if (WallpapersListActivity.this.selectedBackgroundSlug.equals(((FileWallpaper) object).slug)) {
                                selectedWallpaper2 = object;
                            } else {
                                selectedWallpaper2 = null;
                            }
                            selectedWallpaper = selectedWallpaper2;
                            id = 0;
                        } else {
                            selectedWallpaper = null;
                            id = 0;
                        }
                        long id3 = id;
                        wallpaperCell.setWallpaper(WallpapersListActivity.this.currentType, a, object, selectedWallpaper, (Drawable) null, false);
                        if (WallpapersListActivity.this.actionBar.isActionModeShowed()) {
                            wallpaperCell.setChecked(a, WallpapersListActivity.this.selectedWallPapers.indexOfKey(id3) >= 0, !WallpapersListActivity.this.scrolling);
                        } else {
                            wallpaperCell.setChecked(a, false, !WallpapersListActivity.this.scrolling);
                        }
                        a++;
                        z = false;
                    }
                    return;
                case 3:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == WallpapersListActivity.this.resetInfoRow) {
                        cell.setText(LocaleController.getString("ResetChatBackgroundsInfo", NUM));
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == WallpapersListActivity.this.uploadImageRow || position == WallpapersListActivity.this.setColorRow || position == WallpapersListActivity.this.resetRow) {
                return 0;
            }
            if (position == WallpapersListActivity.this.sectionRow || position == WallpapersListActivity.this.resetSectionRow) {
                return 1;
            }
            if (position == WallpapersListActivity.this.resetInfoRow) {
                return 3;
            }
            return 2;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
        themeDescriptions.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        themeDescriptions.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        themeDescriptions.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        return themeDescriptions;
    }
}
