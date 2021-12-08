package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.PatternCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ColorPicker;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.WallpaperCheckBoxView;
import org.telegram.ui.Components.WallpaperParallaxEffect;
import org.telegram.ui.WallpapersListActivity;

public class ThemePreviewActivity extends BaseFragment implements DownloadController.FileDownloadProgressListener, NotificationCenter.NotificationCenterDelegate {
    public static final int SCREEN_TYPE_ACCENT_COLOR = 1;
    public static final int SCREEN_TYPE_CHANGE_BACKGROUND = 2;
    public static final int SCREEN_TYPE_PREVIEW = 0;
    private int TAG;
    /* access modifiers changed from: private */
    public Theme.ThemeAccent accent;
    /* access modifiers changed from: private */
    public ActionBar actionBar2;
    private HintView animationHint;
    private Runnable applyColorAction;
    private boolean applyColorScheduled;
    /* access modifiers changed from: private */
    public Theme.ThemeInfo applyingTheme;
    /* access modifiers changed from: private */
    public FrameLayout backgroundButtonsContainer;
    /* access modifiers changed from: private */
    public WallpaperCheckBoxView[] backgroundCheckBoxView;
    /* access modifiers changed from: private */
    public int backgroundColor;
    /* access modifiers changed from: private */
    public int backgroundGradientColor1;
    /* access modifiers changed from: private */
    public int backgroundGradientColor2;
    /* access modifiers changed from: private */
    public int backgroundGradientColor3;
    private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
    /* access modifiers changed from: private */
    public BackupImageView backgroundImage;
    /* access modifiers changed from: private */
    public ImageView backgroundPlayAnimationImageView;
    /* access modifiers changed from: private */
    public FrameLayout backgroundPlayAnimationView;
    /* access modifiers changed from: private */
    public AnimatorSet backgroundPlayViewAnimator;
    /* access modifiers changed from: private */
    public int backgroundRotation;
    private int backupAccentColor;
    private int backupAccentColor2;
    private long backupBackgroundGradientOverrideColor1;
    private long backupBackgroundGradientOverrideColor2;
    private long backupBackgroundGradientOverrideColor3;
    private long backupBackgroundOverrideColor;
    private int backupBackgroundRotation;
    private float backupIntensity;
    private int backupMyMessagesAccentColor;
    private boolean backupMyMessagesAnimated;
    private int backupMyMessagesGradientAccentColor1;
    private int backupMyMessagesGradientAccentColor2;
    private int backupMyMessagesGradientAccentColor3;
    private String backupSlug;
    /* access modifiers changed from: private */
    public final PorterDuff.Mode blendMode;
    private Bitmap blurredBitmap;
    /* access modifiers changed from: private */
    public FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    private TextView cancelButton;
    /* access modifiers changed from: private */
    public int checkColor;
    /* access modifiers changed from: private */
    public ColorPicker colorPicker;
    /* access modifiers changed from: private */
    public int colorType;
    /* access modifiers changed from: private */
    public float currentIntensity;
    /* access modifiers changed from: private */
    public Object currentWallpaper;
    private Bitmap currentWallpaperBitmap;
    private WallpaperActivityDelegate delegate;
    private boolean deleteOnCancel;
    private DialogsAdapter dialogsAdapter;
    private TextView doneButton;
    /* access modifiers changed from: private */
    public View dotsContainer;
    /* access modifiers changed from: private */
    public TextView dropDown;
    /* access modifiers changed from: private */
    public ActionBarMenuItem dropDownContainer;
    private boolean editingTheme;
    /* access modifiers changed from: private */
    public ImageView floatingButton;
    private FrameLayout frameLayout;
    private String imageFilter;
    private HeaderCell intensityCell;
    private SeekBarView intensitySeekBar;
    /* access modifiers changed from: private */
    public boolean isBlurred;
    /* access modifiers changed from: private */
    public boolean isMotion;
    private int lastPickedColor;
    private int lastPickedColorNum;
    private TLRPC.TL_wallPaper lastSelectedPattern;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public RecyclerListView listView2;
    private String loadingFile;
    private File loadingFileObject;
    private TLRPC.PhotoSize loadingSize;
    /* access modifiers changed from: private */
    public int maxWallpaperSize;
    private MessagesAdapter messagesAdapter;
    /* access modifiers changed from: private */
    public FrameLayout messagesButtonsContainer;
    /* access modifiers changed from: private */
    public WallpaperCheckBoxView[] messagesCheckBoxView;
    /* access modifiers changed from: private */
    public ImageView messagesPlayAnimationImageView;
    /* access modifiers changed from: private */
    public FrameLayout messagesPlayAnimationView;
    /* access modifiers changed from: private */
    public AnimatorSet messagesPlayViewAnimator;
    /* access modifiers changed from: private */
    public AnimatorSet motionAnimation;
    Theme.MessageDrawable msgOutDrawable;
    Theme.MessageDrawable msgOutDrawableSelected;
    Theme.MessageDrawable msgOutMediaDrawable;
    Theme.MessageDrawable msgOutMediaDrawableSelected;
    /* access modifiers changed from: private */
    public boolean nightTheme;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private Bitmap originalBitmap;
    /* access modifiers changed from: private */
    public FrameLayout page1;
    /* access modifiers changed from: private */
    public FrameLayout page2;
    /* access modifiers changed from: private */
    public WallpaperParallaxEffect parallaxEffect;
    /* access modifiers changed from: private */
    public float parallaxScale;
    /* access modifiers changed from: private */
    public int patternColor;
    /* access modifiers changed from: private */
    public FrameLayout[] patternLayout;
    /* access modifiers changed from: private */
    public AnimatorSet patternViewAnimation;
    /* access modifiers changed from: private */
    public ArrayList<Object> patterns;
    private PatternsAdapter patternsAdapter;
    private FrameLayout[] patternsButtonsContainer;
    private TextView[] patternsCancelButton;
    private HashMap<Long, Object> patternsDict;
    private LinearLayoutManager patternsLayoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView patternsListView;
    private TextView[] patternsSaveButton;
    private int previousBackgroundColor;
    private int previousBackgroundGradientColor1;
    private int previousBackgroundGradientColor2;
    private int previousBackgroundGradientColor3;
    private int previousBackgroundRotation;
    private float previousIntensity;
    private TLRPC.TL_wallPaper previousSelectedPattern;
    /* access modifiers changed from: private */
    public boolean progressVisible;
    /* access modifiers changed from: private */
    public RadialProgress2 radialProgress;
    /* access modifiers changed from: private */
    public boolean removeBackgroundOverride;
    private boolean rotatePreview;
    private FrameLayout saveButtonsContainer;
    private ActionBarMenuItem saveItem;
    /* access modifiers changed from: private */
    public final int screenType;
    /* access modifiers changed from: private */
    public TLRPC.TL_wallPaper selectedPattern;
    /* access modifiers changed from: private */
    public Drawable sheetDrawable;
    private boolean showColor;
    private List<ThemeDescription> themeDescriptions;
    /* access modifiers changed from: private */
    public UndoView undoView;
    public boolean useDefaultThemeForButtons;
    /* access modifiers changed from: private */
    public ViewPager viewPager;
    /* access modifiers changed from: private */
    public boolean wasScroll;
    /* access modifiers changed from: private */
    public long watchForKeyboardEndTime;

    public interface WallpaperActivityDelegate {
        void didSetNewBackground();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3945lambda$new$0$orgtelegramuiThemePreviewActivity() {
        this.applyColorScheduled = false;
        applyColor(this.lastPickedColor, this.lastPickedColorNum);
        this.lastPickedColorNum = -1;
    }

    public ThemePreviewActivity(Object wallPaper, Bitmap bitmap) {
        this(wallPaper, bitmap, false, false);
    }

    public ThemePreviewActivity(Object wallPaper, Bitmap bitmap, boolean rotate, boolean openColor) {
        this.useDefaultThemeForButtons = true;
        this.colorType = 1;
        this.msgOutDrawable = new Theme.MessageDrawable(0, true, false);
        this.msgOutDrawableSelected = new Theme.MessageDrawable(0, true, true);
        this.msgOutMediaDrawable = new Theme.MessageDrawable(1, true, false);
        this.msgOutMediaDrawableSelected = new Theme.MessageDrawable(1, true, true);
        this.lastPickedColorNum = -1;
        this.applyColorAction = new ThemePreviewActivity$$ExternalSyntheticLambda5(this);
        this.patternLayout = new FrameLayout[2];
        this.patternsCancelButton = new TextView[2];
        this.patternsSaveButton = new TextView[2];
        this.patternsButtonsContainer = new FrameLayout[2];
        this.patternsDict = new HashMap<>();
        this.currentIntensity = 0.5f;
        this.blendMode = PorterDuff.Mode.SRC_IN;
        this.parallaxScale = 1.0f;
        this.loadingFile = null;
        this.loadingFileObject = null;
        this.loadingSize = null;
        this.imageFilter = "640_360";
        this.maxWallpaperSize = 1920;
        this.screenType = 2;
        this.showColor = openColor;
        this.currentWallpaper = wallPaper;
        this.currentWallpaperBitmap = bitmap;
        this.rotatePreview = rotate;
        if (wallPaper instanceof WallpapersListActivity.ColorWallpaper) {
            WallpapersListActivity.ColorWallpaper object = (WallpapersListActivity.ColorWallpaper) wallPaper;
            this.isMotion = object.motion;
            TLRPC.TL_wallPaper tL_wallPaper = object.pattern;
            this.selectedPattern = tL_wallPaper;
            if (tL_wallPaper != null) {
                float f = object.intensity;
                this.currentIntensity = f;
                if (f < 0.0f && !Theme.getActiveTheme().isDark()) {
                    this.currentIntensity *= -1.0f;
                }
            }
        }
        this.msgOutDrawable.themePreview = true;
        this.msgOutMediaDrawable.themePreview = true;
        this.msgOutDrawableSelected.themePreview = true;
        this.msgOutMediaDrawableSelected.themePreview = true;
    }

    public ThemePreviewActivity(Theme.ThemeInfo themeInfo) {
        this(themeInfo, false, 0, false, false);
    }

    public ThemePreviewActivity(Theme.ThemeInfo themeInfo, boolean deleteFile, int screenType2, boolean edit, boolean night) {
        this.useDefaultThemeForButtons = true;
        this.colorType = 1;
        this.msgOutDrawable = new Theme.MessageDrawable(0, true, false);
        this.msgOutDrawableSelected = new Theme.MessageDrawable(0, true, true);
        this.msgOutMediaDrawable = new Theme.MessageDrawable(1, true, false);
        this.msgOutMediaDrawableSelected = new Theme.MessageDrawable(1, true, true);
        this.lastPickedColorNum = -1;
        this.applyColorAction = new ThemePreviewActivity$$ExternalSyntheticLambda5(this);
        this.patternLayout = new FrameLayout[2];
        this.patternsCancelButton = new TextView[2];
        this.patternsSaveButton = new TextView[2];
        this.patternsButtonsContainer = new FrameLayout[2];
        this.patternsDict = new HashMap<>();
        this.currentIntensity = 0.5f;
        this.blendMode = PorterDuff.Mode.SRC_IN;
        this.parallaxScale = 1.0f;
        this.loadingFile = null;
        this.loadingFileObject = null;
        this.loadingSize = null;
        this.imageFilter = "640_360";
        this.maxWallpaperSize = 1920;
        this.screenType = screenType2;
        this.nightTheme = night;
        this.applyingTheme = themeInfo;
        this.deleteOnCancel = deleteFile;
        this.editingTheme = edit;
        if (screenType2 == 1) {
            Theme.ThemeAccent accent2 = themeInfo.getAccent(!edit);
            this.accent = accent2;
            this.useDefaultThemeForButtons = false;
            this.backupAccentColor = accent2.accentColor;
            this.backupAccentColor2 = this.accent.accentColor2;
            this.backupMyMessagesAccentColor = this.accent.myMessagesAccentColor;
            this.backupMyMessagesGradientAccentColor1 = this.accent.myMessagesGradientAccentColor1;
            this.backupMyMessagesGradientAccentColor2 = this.accent.myMessagesGradientAccentColor2;
            this.backupMyMessagesGradientAccentColor3 = this.accent.myMessagesGradientAccentColor3;
            this.backupMyMessagesAnimated = this.accent.myMessagesAnimated;
            this.backupBackgroundOverrideColor = this.accent.backgroundOverrideColor;
            this.backupBackgroundGradientOverrideColor1 = this.accent.backgroundGradientOverrideColor1;
            this.backupBackgroundGradientOverrideColor2 = this.accent.backgroundGradientOverrideColor2;
            this.backupBackgroundGradientOverrideColor3 = this.accent.backgroundGradientOverrideColor3;
            this.backupIntensity = this.accent.patternIntensity;
            this.backupSlug = this.accent.patternSlug;
            this.backupBackgroundRotation = this.accent.backgroundRotation;
        } else {
            if (screenType2 == 0) {
                this.useDefaultThemeForButtons = false;
            }
            Theme.ThemeAccent accent3 = themeInfo.getAccent(false);
            this.accent = accent3;
            if (accent3 != null) {
                this.selectedPattern = accent3.pattern;
            }
        }
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent != null) {
            this.isMotion = themeAccent.patternMotion;
            if (!TextUtils.isEmpty(this.accent.patternSlug)) {
                this.currentIntensity = this.accent.patternIntensity;
            }
            Theme.applyThemeTemporary(this.applyingTheme, true);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.goingToPreviewTheme, new Object[0]);
        this.msgOutDrawable.themePreview = true;
        this.msgOutMediaDrawable.themePreview = true;
        this.msgOutDrawableSelected.themePreview = true;
        this.msgOutMediaDrawableSelected.themePreview = true;
    }

    public void setInitialModes(boolean blur, boolean motion) {
        this.isBlurred = blur;
        this.isMotion = motion;
    }

    public int getNavigationBarColor() {
        return super.getNavigationBarColor();
    }

    /* JADX WARNING: Removed duplicated region for block: B:118:0x0646  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x073e  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x0743  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0841  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x0989  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x0cf7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r42) {
        /*
            r41 = this;
            r6 = r41
            r7 = r42
            r8 = 1
            r6.hasOwnBackground = r8
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            r9 = 0
            if (r0 == 0) goto L_0x0013
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r0.setOccupyStatusBar(r9)
        L_0x0013:
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.page1 = r0
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r10 = r0.createMenu()
            r0 = 2131165495(0x7var_, float:1.7945209E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r10.addItem((int) r9, (int) r0)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.setIsSearchField(r8)
            org.telegram.ui.ThemePreviewActivity$1 r1 = new org.telegram.ui.ThemePreviewActivity$1
            r1.<init>()
            org.telegram.ui.ActionBar.ActionBarMenuItem r11 = r0.setActionBarMenuItemSearchListener(r1)
            r0 = 2131627616(0x7f0e0e60, float:1.8882501E38)
            java.lang.String r1 = "Search"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r11.setSearchFieldHint(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.ActionBar.MenuDrawable r1 = new org.telegram.ui.ActionBar.MenuDrawable
            r1.<init>()
            r0.setBackButtonDrawable(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r0.setAddToContainer(r9)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r1 = 2131628111(0x7f0e104f, float:1.8883505E38)
            java.lang.String r2 = "ThemePreview"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            org.telegram.ui.ThemePreviewActivity$2 r0 = new org.telegram.ui.ThemePreviewActivity$2
            r0.<init>(r7)
            r6.page1 = r0
            java.lang.String r1 = "windowBackgroundWhite"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBackgroundColor(r1)
            android.widget.FrameLayout r0 = r6.page1
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            r2 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r12 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r2)
            r0.addView(r1, r2)
            org.telegram.ui.Components.RecyclerListView r0 = new org.telegram.ui.Components.RecyclerListView
            r0.<init>(r7)
            r6.listView = r0
            r0.setVerticalScrollBarEnabled(r8)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            r13 = 0
            r0.setItemAnimator(r13)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            r0.setLayoutAnimation(r13)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            androidx.recyclerview.widget.LinearLayoutManager r1 = new androidx.recyclerview.widget.LinearLayoutManager
            r1.<init>(r7, r8, r9)
            r0.setLayoutManager(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            r14 = 2
            if (r1 == 0) goto L_0x00a3
            r1 = 1
            goto L_0x00a4
        L_0x00a3:
            r1 = 2
        L_0x00a4:
            r0.setVerticalScrollbarPosition(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            int r1 = r6.screenType
            if (r1 == 0) goto L_0x00b0
            r1 = 1094713344(0x41400000, float:12.0)
            goto L_0x00b1
        L_0x00b0:
            r1 = 0
        L_0x00b1:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setPadding(r9, r9, r9, r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda16 r1 = org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda16.INSTANCE
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            android.widget.FrameLayout r0 = r6.page1
            org.telegram.ui.Components.RecyclerListView r1 = r6.listView
            r5 = 51
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r12, (int) r12, (int) r5)
            r0.addView(r1, r2)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r7)
            r6.floatingButton = r0
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r1)
            r16 = 1113587712(0x42600000, float:56.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            java.lang.String r1 = "chats_actionBackground"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            java.lang.String r2 = "chats_actionPressedBackground"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r0, r1, r2)
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 21
            if (r1 >= r2) goto L_0x0122
            android.content.res.Resources r1 = r42.getResources()
            r3 = 2131165435(0x7var_fb, float:1.7945087E38)
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r3)
            android.graphics.drawable.Drawable r1 = r1.mutate()
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r4, r5)
            r1.setColorFilter(r3)
            org.telegram.ui.Components.CombinedDrawable r3 = new org.telegram.ui.Components.CombinedDrawable
            r3.<init>(r1, r0, r9, r9)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r3.setIconSize(r4, r5)
            r0 = r3
            r5 = r0
            goto L_0x0123
        L_0x0122:
            r5 = r0
        L_0x0123:
            android.widget.ImageView r0 = r6.floatingButton
            r0.setBackgroundDrawable(r5)
            android.widget.ImageView r0 = r6.floatingButton
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r3 = "chats_actionIcon"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r3, r4)
            r0.setColorFilter(r1)
            android.widget.ImageView r0 = r6.floatingButton
            r1 = 2131165434(0x7var_fa, float:1.7945085E38)
            r0.setImageResource(r1)
            int r0 = android.os.Build.VERSION.SDK_INT
            r18 = 1082130432(0x40800000, float:4.0)
            if (r0 < r2) goto L_0x01ac
            android.animation.StateListAnimator r0 = new android.animation.StateListAnimator
            r0.<init>()
            int[] r1 = new int[r8]
            r3 = 16842919(0x10100a7, float:2.3694026E-38)
            r1[r9] = r3
            android.widget.ImageView r3 = r6.floatingButton
            android.util.Property r4 = android.view.View.TRANSLATION_Z
            float[] r15 = new float[r14]
            r19 = 1073741824(0x40000000, float:2.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r12 = (float) r12
            r15[r9] = r12
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r12 = (float) r12
            r15[r8] = r12
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r15)
            r13 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r3 = r3.setDuration(r13)
            r0.addState(r1, r3)
            int[] r1 = new int[r9]
            android.widget.ImageView r3 = r6.floatingButton
            android.util.Property r4 = android.view.View.TRANSLATION_Z
            r13 = 2
            float[] r14 = new float[r13]
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r18)
            float r13 = (float) r13
            r14[r9] = r13
            r13 = 1073741824(0x40000000, float:2.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r14[r8] = r13
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r14)
            r13 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r3 = r3.setDuration(r13)
            r0.addState(r1, r3)
            android.widget.ImageView r1 = r6.floatingButton
            r1.setStateListAnimator(r0)
            android.widget.ImageView r1 = r6.floatingButton
            org.telegram.ui.ThemePreviewActivity$3 r3 = new org.telegram.ui.ThemePreviewActivity$3
            r3.<init>()
            r1.setOutlineProvider(r3)
        L_0x01ac:
            android.widget.FrameLayout r0 = r6.page1
            android.widget.ImageView r1 = r6.floatingButton
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r2) goto L_0x01b9
            r3 = 56
            r20 = 56
            goto L_0x01bd
        L_0x01b9:
            r3 = 60
            r20 = 60
        L_0x01bd:
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r2) goto L_0x01c4
            r21 = 1113587712(0x42600000, float:56.0)
            goto L_0x01c8
        L_0x01c4:
            r2 = 1114636288(0x42700000, float:60.0)
            r21 = 1114636288(0x42700000, float:60.0)
        L_0x01c8:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            r13 = 3
            if (r2 == 0) goto L_0x01cf
            r2 = 3
            goto L_0x01d0
        L_0x01cf:
            r2 = 5
        L_0x01d0:
            r22 = r2 | 80
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x01d9
            r23 = 1096810496(0x41600000, float:14.0)
            goto L_0x01db
        L_0x01d9:
            r23 = 0
        L_0x01db:
            r24 = 0
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x01e4
            r25 = 0
            goto L_0x01e6
        L_0x01e4:
            r25 = 1096810496(0x41600000, float:14.0)
        L_0x01e6:
            r26 = 1096810496(0x41600000, float:14.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r0.addView(r1, r2)
            org.telegram.ui.ThemePreviewActivity$DialogsAdapter r0 = new org.telegram.ui.ThemePreviewActivity$DialogsAdapter
            r0.<init>(r7)
            r6.dialogsAdapter = r0
            org.telegram.ui.Components.RecyclerListView r1 = r6.listView
            r1.setAdapter(r0)
            org.telegram.ui.ThemePreviewActivity$4 r0 = new org.telegram.ui.ThemePreviewActivity$4
            r0.<init>(r7)
            r6.page2 = r0
            org.telegram.ui.ThemePreviewActivity$MessagesAdapter r0 = new org.telegram.ui.ThemePreviewActivity$MessagesAdapter
            r0.<init>(r6, r7)
            r6.messagesAdapter = r0
            org.telegram.ui.ActionBar.ActionBar r0 = r41.createActionBar(r42)
            r6.actionBar2 = r0
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 == 0) goto L_0x021a
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            r0.setOccupyStatusBar(r9)
        L_0x021a:
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            org.telegram.ui.ActionBar.BackDrawable r1 = new org.telegram.ui.ActionBar.BackDrawable
            r1.<init>(r9)
            r0.setBackButtonDrawable(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            org.telegram.ui.ThemePreviewActivity$5 r1 = new org.telegram.ui.ThemePreviewActivity$5
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            org.telegram.ui.ThemePreviewActivity$6 r0 = new org.telegram.ui.ThemePreviewActivity$6
            r0.<init>(r7)
            r6.backgroundImage = r0
            android.widget.FrameLayout r1 = r6.page2
            r20 = -1
            r21 = -1082130432(0xffffffffbvar_, float:-1.0)
            r22 = 51
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r1.addView(r0, r2)
            int r0 = r6.screenType
            r1 = 2
            if (r0 != r1) goto L_0x025f
            org.telegram.ui.Components.BackupImageView r0 = r6.backgroundImage
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda10 r1 = new org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda10
            r1.<init>(r6)
            r0.setDelegate(r1)
        L_0x025f:
            org.telegram.ui.ThemePreviewActivity$MessagesAdapter r0 = r6.messagesAdapter
            boolean r0 = r0.showSecretMessages
            r4 = 4
            r19 = 1092616192(0x41200000, float:10.0)
            java.lang.String r20 = "fonts/rmedium.ttf"
            if (r0 == 0) goto L_0x0287
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            java.lang.String r1 = "Telegram Beta Chat"
            r0.setTitle(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            r1 = 505(0x1f9, float:7.08E-43)
            java.lang.String r2 = "Members"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
            r0.setSubtitle(r1)
            r17 = r5
            r3 = 0
            r15 = 51
            goto L_0x046d
        L_0x0287:
            int r0 = r6.screenType
            r1 = 2
            if (r0 != r1) goto L_0x02db
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            r1 = 2131624560(0x7f0e0270, float:1.8876303E38)
            java.lang.String r2 = "BackgroundPreview"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r0 == 0) goto L_0x02a8
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r0.getAccent(r9)
            if (r0 != 0) goto L_0x02c0
        L_0x02a8:
            java.lang.Object r0 = r6.currentWallpaper
            boolean r1 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r1 == 0) goto L_0x02ba
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r0
            java.lang.String r0 = r0.slug
            java.lang.String r1 = "d"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x02c0
        L_0x02ba:
            java.lang.Object r0 = r6.currentWallpaper
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper
            if (r0 == 0) goto L_0x02d4
        L_0x02c0:
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            r1 = 5
            r2 = 2131165860(0x7var_a4, float:1.794595E38)
            r0.addItem((int) r1, (int) r2)
            r17 = r5
            r3 = 0
            r15 = 51
            goto L_0x046d
        L_0x02d4:
            r17 = r5
            r3 = 0
            r15 = 51
            goto L_0x046d
        L_0x02db:
            if (r0 != r8) goto L_0x040f
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            org.telegram.ui.ActionBar.ActionBarMenu r3 = r0.createMenu()
            r0 = 2131627592(0x7f0e0e48, float:1.8882453E38)
            java.lang.String r1 = "Save"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.String r0 = r0.toUpperCase()
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r3.addItem((int) r4, (java.lang.CharSequence) r0)
            r6.saveItem = r0
            org.telegram.ui.ThemePreviewActivity$7 r2 = new org.telegram.ui.ThemePreviewActivity$7
            r21 = 0
            r22 = 0
            r0 = r2
            r1 = r41
            r12 = r2
            r2 = r42
            r24 = r3
            r15 = 4
            r4 = r21
            r17 = r5
            r15 = 51
            r5 = r22
            r0.<init>(r2, r3, r4, r5)
            r6.dropDownContainer = r12
            r12.setSubMenuOpenSide(r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.dropDownContainer
            r1 = 2131625015(0x7f0e0437, float:1.8877226E38)
            java.lang.String r2 = "ColorPickerBackground"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 2
            r0.addSubItem(r2, r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.dropDownContainer
            r2 = 2131625016(0x7f0e0438, float:1.8877228E38)
            java.lang.String r3 = "ColorPickerMainColor"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.addSubItem(r8, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.dropDownContainer
            r2 = 2131625017(0x7f0e0439, float:1.887723E38)
            java.lang.String r3 = "ColorPickerMyMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.addSubItem(r13, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.dropDownContainer
            r1.setAllowCloseAnimation(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.dropDownContainer
            r1.setForceSmoothKeyboard(r8)
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar2
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r6.dropDownContainer
            r25 = -2
            r26 = -1082130432(0xffffffffbvar_, float:-1.0)
            r27 = 51
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r3 == 0) goto L_0x035f
            r3 = 1115684864(0x42800000, float:64.0)
            r28 = 1115684864(0x42800000, float:64.0)
            goto L_0x0361
        L_0x035f:
            r28 = 1113587712(0x42600000, float:56.0)
        L_0x0361:
            r29 = 0
            r30 = 1109393408(0x42200000, float:40.0)
            r31 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r1.addView(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.dropDownContainer
            org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda24 r2 = new org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda24
            r2.<init>(r6)
            r1.setOnClickListener(r2)
            android.widget.TextView r1 = new android.widget.TextView
            r1.<init>(r7)
            r6.dropDown = r1
            r0 = 2
            r1.setImportantForAccessibility(r0)
            android.widget.TextView r1 = r6.dropDown
            r1.setGravity(r13)
            android.widget.TextView r1 = r6.dropDown
            r1.setSingleLine(r8)
            android.widget.TextView r1 = r6.dropDown
            r1.setLines(r8)
            android.widget.TextView r1 = r6.dropDown
            r1.setMaxLines(r8)
            android.widget.TextView r1 = r6.dropDown
            android.text.TextUtils$TruncateAt r2 = android.text.TextUtils.TruncateAt.END
            r1.setEllipsize(r2)
            android.widget.TextView r1 = r6.dropDown
            java.lang.String r2 = "actionBarDefaultTitle"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTextColor(r2)
            android.widget.TextView r1 = r6.dropDown
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r20)
            r1.setTypeface(r2)
            android.widget.TextView r1 = r6.dropDown
            r2 = 2131625016(0x7f0e0438, float:1.8877228E38)
            java.lang.String r3 = "ColorPickerMainColor"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.content.res.Resources r1 = r42.getResources()
            r2 = 2131165503(0x7var_f, float:1.7945225E38)
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r2)
            android.graphics.drawable.Drawable r1 = r1.mutate()
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            java.lang.String r3 = "actionBarDefaultTitle"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r3, r4)
            r1.setColorFilter(r2)
            android.widget.TextView r2 = r6.dropDown
            r3 = 0
            r2.setCompoundDrawablesWithIntrinsicBounds(r3, r3, r1, r3)
            android.widget.TextView r2 = r6.dropDown
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r2.setCompoundDrawablePadding(r4)
            android.widget.TextView r2 = r6.dropDown
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r2.setPadding(r9, r9, r4, r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r6.dropDownContainer
            android.widget.TextView r4 = r6.dropDown
            r25 = -2
            r26 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r27 = 16
            r28 = 1098907648(0x41800000, float:16.0)
            r30 = 0
            r31 = 1065353216(0x3var_, float:1.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r2.addView(r4, r5)
            goto L_0x046d
        L_0x040f:
            r17 = r5
            r3 = 0
            r15 = 51
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = r6.applyingTheme
            org.telegram.tgnet.TLRPC$TL_theme r1 = r1.info
            if (r1 == 0) goto L_0x0421
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = r6.applyingTheme
            org.telegram.tgnet.TLRPC$TL_theme r1 = r1.info
            java.lang.String r1 = r1.title
            goto L_0x0427
        L_0x0421:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = r6.applyingTheme
            java.lang.String r1 = r1.getName()
        L_0x0427:
            java.lang.String r2 = ".attheme"
            int r2 = r1.lastIndexOf(r2)
            if (r2 < 0) goto L_0x0433
            java.lang.String r1 = r1.substring(r9, r2)
        L_0x0433:
            org.telegram.ui.ActionBar.ActionBar r4 = r6.actionBar2
            r4.setTitle(r1)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r6.applyingTheme
            org.telegram.tgnet.TLRPC$TL_theme r4 = r4.info
            if (r4 == 0) goto L_0x0458
            org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r6.applyingTheme
            org.telegram.tgnet.TLRPC$TL_theme r4 = r4.info
            int r4 = r4.installs_count
            if (r4 <= 0) goto L_0x0458
            org.telegram.ui.ActionBar.ActionBar r4 = r6.actionBar2
            org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = r6.applyingTheme
            org.telegram.tgnet.TLRPC$TL_theme r5 = r5.info
            int r5 = r5.installs_count
            java.lang.String r12 = "ThemeInstallCount"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r12, r5)
            r4.setSubtitle(r5)
            goto L_0x046d
        L_0x0458:
            org.telegram.ui.ActionBar.ActionBar r4 = r6.actionBar2
            long r22 = java.lang.System.currentTimeMillis()
            r24 = 1000(0x3e8, double:4.94E-321)
            long r22 = r22 / r24
            r24 = 3600(0xe10, double:1.7786E-320)
            long r22 = r22 - r24
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatDateOnline(r22)
            r4.setSubtitle(r5)
        L_0x046d:
            org.telegram.ui.ThemePreviewActivity$8 r1 = new org.telegram.ui.ThemePreviewActivity$8
            r1.<init>(r7)
            r6.listView2 = r1
            org.telegram.ui.ThemePreviewActivity$9 r1 = new org.telegram.ui.ThemePreviewActivity$9
            r1.<init>()
            r1.setDelayAnimations(r9)
            org.telegram.ui.Components.RecyclerListView r2 = r6.listView2
            r2.setItemAnimator(r1)
            org.telegram.ui.Components.RecyclerListView r2 = r6.listView2
            r2.setVerticalScrollBarEnabled(r8)
            org.telegram.ui.Components.RecyclerListView r2 = r6.listView2
            r0 = 2
            r2.setOverScrollMode(r0)
            int r2 = r6.screenType
            if (r2 != r0) goto L_0x04a0
            org.telegram.ui.Components.RecyclerListView r2 = r6.listView2
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r5 = 1112539136(0x42500000, float:52.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r2.setPadding(r9, r4, r9, r5)
            goto L_0x04bf
        L_0x04a0:
            if (r2 != r8) goto L_0x04b2
            org.telegram.ui.Components.RecyclerListView r2 = r6.listView2
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r5 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r2.setPadding(r9, r4, r9, r5)
            goto L_0x04bf
        L_0x04b2:
            org.telegram.ui.Components.RecyclerListView r2 = r6.listView2
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r2.setPadding(r9, r4, r9, r5)
        L_0x04bf:
            org.telegram.ui.Components.RecyclerListView r2 = r6.listView2
            r2.setClipToPadding(r9)
            org.telegram.ui.Components.RecyclerListView r2 = r6.listView2
            androidx.recyclerview.widget.LinearLayoutManager r4 = new androidx.recyclerview.widget.LinearLayoutManager
            r4.<init>(r7, r8, r8)
            r2.setLayoutManager(r4)
            org.telegram.ui.Components.RecyclerListView r2 = r6.listView2
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x04d6
            r4 = 1
            goto L_0x04d7
        L_0x04d6:
            r4 = 2
        L_0x04d7:
            r2.setVerticalScrollbarPosition(r4)
            int r2 = r6.screenType
            if (r2 != r8) goto L_0x0503
            android.widget.FrameLayout r2 = r6.page2
            org.telegram.ui.Components.RecyclerListView r4 = r6.listView2
            r22 = -1
            r23 = -1082130432(0xffffffffbvar_, float:-1.0)
            r24 = 51
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 1133019136(0x43888000, float:273.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28)
            r2.addView(r4, r5)
            org.telegram.ui.Components.RecyclerListView r2 = r6.listView2
            org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda17 r4 = new org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda17
            r4.<init>(r6)
            r2.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r4)
            goto L_0x050f
        L_0x0503:
            android.widget.FrameLayout r2 = r6.page2
            org.telegram.ui.Components.RecyclerListView r4 = r6.listView2
            r5 = -1
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r5, (int) r15)
            r2.addView(r4, r12)
        L_0x050f:
            org.telegram.ui.Components.RecyclerListView r2 = r6.listView2
            org.telegram.ui.ThemePreviewActivity$10 r4 = new org.telegram.ui.ThemePreviewActivity$10
            r4.<init>()
            r2.setOnScrollListener(r4)
            android.widget.FrameLayout r2 = r6.page2
            org.telegram.ui.ActionBar.ActionBar r4 = r6.actionBar2
            r5 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r12 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r5)
            r2.addView(r4, r5)
            org.telegram.ui.Components.WallpaperParallaxEffect r2 = new org.telegram.ui.Components.WallpaperParallaxEffect
            r2.<init>(r7)
            r6.parallaxEffect = r2
            org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda18 r4 = new org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda18
            r4.<init>(r6)
            r2.setCallback(r4)
            int r2 = r6.screenType
            java.lang.String r12 = "chat_fieldOverlayText"
            r0 = 17
            r3 = -2
            if (r2 == r8) goto L_0x0549
            r4 = 2
            if (r2 != r4) goto L_0x0543
            goto L_0x0549
        L_0x0543:
            r23 = r1
            r32 = r10
            goto L_0x0d12
        L_0x0549:
            org.telegram.ui.Components.RadialProgress2 r4 = new org.telegram.ui.Components.RadialProgress2
            org.telegram.ui.Components.BackupImageView r2 = r6.backgroundImage
            r4.<init>(r2)
            r6.radialProgress = r4
            java.lang.String r2 = "chat_serviceBackground"
            java.lang.String r13 = "chat_serviceBackground"
            java.lang.String r5 = "chat_serviceText"
            java.lang.String r14 = "chat_serviceText"
            r4.setColors((java.lang.String) r2, (java.lang.String) r13, (java.lang.String) r5, (java.lang.String) r14)
            int r2 = r6.screenType
            r4 = 2
            if (r2 != r4) goto L_0x05c6
            org.telegram.ui.ThemePreviewActivity$11 r4 = new org.telegram.ui.ThemePreviewActivity$11
            r4.<init>(r7)
            r6.bottomOverlayChat = r4
            r4.setWillNotDraw(r9)
            android.widget.FrameLayout r4 = r6.bottomOverlayChat
            r5 = 1077936128(0x40400000, float:3.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r4.setPadding(r9, r5, r9, r9)
            android.widget.FrameLayout r4 = r6.page2
            android.widget.FrameLayout r5 = r6.bottomOverlayChat
            r13 = 80
            r14 = -1
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r14, (int) r15, (int) r13)
            r4.addView(r5, r13)
            android.widget.FrameLayout r4 = r6.bottomOverlayChat
            org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda25 r5 = new org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda25
            r5.<init>(r6)
            r4.setOnClickListener(r5)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r7)
            r6.bottomOverlayChatText = r4
            r5 = 1097859072(0x41700000, float:15.0)
            r4.setTextSize(r8, r5)
            android.widget.TextView r4 = r6.bottomOverlayChatText
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r20)
            r4.setTypeface(r5)
            android.widget.TextView r4 = r6.bottomOverlayChatText
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r4.setTextColor(r5)
            android.widget.TextView r4 = r6.bottomOverlayChatText
            r5 = 2131627761(0x7f0e0ef1, float:1.8882796E38)
            java.lang.String r13 = "SetBackground"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r5)
            r4.setText(r5)
            android.widget.FrameLayout r4 = r6.bottomOverlayChat
            android.widget.TextView r5 = r6.bottomOverlayChatText
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r3, (int) r0)
            r4.addView(r5, r13)
        L_0x05c6:
            android.graphics.Rect r4 = new android.graphics.Rect
            r4.<init>()
            android.content.res.Resources r5 = r42.getResources()
            r13 = 2131166073(0x7var_, float:1.7946381E38)
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r13)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            r6.sheetDrawable = r5
            r5.getPadding(r4)
            android.graphics.drawable.Drawable r5 = r6.sheetDrawable
            android.graphics.PorterDuffColorFilter r13 = new android.graphics.PorterDuffColorFilter
            java.lang.String r14 = "windowBackgroundWhite"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.MULTIPLY
            r13.<init>(r14, r2)
            r5.setColorFilter(r13)
            android.text.TextPaint r2 = new android.text.TextPaint
            r2.<init>(r8)
            r5 = 1096810496(0x41600000, float:14.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r13
            r2.setTextSize(r5)
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r20)
            r2.setTypeface(r5)
            int r5 = r6.screenType
            if (r5 == r8) goto L_0x0625
            java.lang.Object r5 = r6.currentWallpaper
            boolean r13 = r5 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r13 == 0) goto L_0x0612
            goto L_0x0625
        L_0x0612:
            r13 = 2
            boolean r14 = r5 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper
            if (r14 == 0) goto L_0x0639
            org.telegram.ui.WallpapersListActivity$FileWallpaper r5 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r5
            java.lang.String r14 = r5.slug
            java.lang.String r15 = "t"
            boolean r14 = r15.equals(r14)
            if (r14 == 0) goto L_0x0639
            r13 = 0
            goto L_0x0639
        L_0x0625:
            r13 = 3
            java.lang.Object r5 = r6.currentWallpaper
            boolean r14 = r5 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r14 == 0) goto L_0x0639
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r5 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r5
            java.lang.String r5 = r5.slug
            java.lang.String r14 = "d"
            boolean r5 = r14.equals(r5)
            if (r5 == 0) goto L_0x0639
            r13 = 0
        L_0x0639:
            java.lang.String[] r5 = new java.lang.String[r13]
            int[] r14 = new int[r13]
            org.telegram.ui.Components.WallpaperCheckBoxView[] r15 = new org.telegram.ui.Components.WallpaperCheckBoxView[r13]
            r6.backgroundCheckBoxView = r15
            r15 = 0
            r28 = 1065353216(0x3var_, float:1.0)
            if (r13 == 0) goto L_0x073e
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r7)
            r6.backgroundButtonsContainer = r3
            int r3 = r6.screenType
            if (r3 == r8) goto L_0x066f
            java.lang.Object r3 = r6.currentWallpaper
            boolean r3 = r3 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r3 == 0) goto L_0x0658
            goto L_0x066f
        L_0x0658:
            r3 = 2131624548(0x7f0e0264, float:1.8876279E38)
            java.lang.String r0 = "BackgroundBlurred"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r3)
            r5[r9] = r0
            r0 = 2131624558(0x7f0e026e, float:1.88763E38)
            java.lang.String r3 = "BackgroundMotion"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r5[r8] = r0
            goto L_0x0691
        L_0x066f:
            r0 = 2131624555(0x7f0e026b, float:1.8876293E38)
            java.lang.String r3 = "BackgroundColors"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r5[r9] = r0
            r0 = 2131624559(0x7f0e026f, float:1.8876301E38)
            java.lang.String r3 = "BackgroundPattern"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r5[r8] = r0
            r0 = 2131624558(0x7f0e026e, float:1.88763E38)
            java.lang.String r3 = "BackgroundMotion"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r3 = 2
            r5[r3] = r0
        L_0x0691:
            r3 = 0
        L_0x0692:
            int r0 = r5.length
            if (r3 >= r0) goto L_0x06ae
            r0 = r5[r3]
            float r0 = r2.measureText(r0)
            double r8 = (double) r0
            double r8 = java.lang.Math.ceil(r8)
            int r0 = (int) r8
            r14[r3] = r0
            r0 = r14[r3]
            int r15 = java.lang.Math.max(r15, r0)
            int r3 = r3 + 1
            r8 = 1
            r9 = 0
            goto L_0x0692
        L_0x06ae:
            org.telegram.ui.ThemePreviewActivity$12 r0 = new org.telegram.ui.ThemePreviewActivity$12
            r0.<init>(r7)
            r6.backgroundPlayAnimationView = r0
            r3 = 0
            r0.setWillNotDraw(r3)
            android.widget.FrameLayout r0 = r6.backgroundPlayAnimationView
            int r3 = r6.backgroundGradientColor1
            if (r3 == 0) goto L_0x06c1
            r3 = 0
            goto L_0x06c2
        L_0x06c1:
            r3 = 4
        L_0x06c2:
            r0.setVisibility(r3)
            android.widget.FrameLayout r0 = r6.backgroundPlayAnimationView
            int r3 = r6.backgroundGradientColor1
            if (r3 == 0) goto L_0x06ce
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x06d1
        L_0x06ce:
            r3 = 1036831949(0x3dcccccd, float:0.1)
        L_0x06d1:
            r0.setScaleX(r3)
            android.widget.FrameLayout r0 = r6.backgroundPlayAnimationView
            int r3 = r6.backgroundGradientColor1
            if (r3 == 0) goto L_0x06dd
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x06e0
        L_0x06dd:
            r3 = 1036831949(0x3dcccccd, float:0.1)
        L_0x06e0:
            r0.setScaleY(r3)
            android.widget.FrameLayout r0 = r6.backgroundPlayAnimationView
            int r3 = r6.backgroundGradientColor1
            if (r3 == 0) goto L_0x06ec
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x06ed
        L_0x06ec:
            r3 = 0
        L_0x06ed:
            r0.setAlpha(r3)
            android.widget.FrameLayout r0 = r6.backgroundPlayAnimationView
            int r3 = r6.backgroundGradientColor1
            if (r3 == 0) goto L_0x06fc
            r3 = 1
            java.lang.Integer r8 = java.lang.Integer.valueOf(r3)
            goto L_0x06fd
        L_0x06fc:
            r8 = 0
        L_0x06fd:
            r0.setTag(r8)
            android.widget.FrameLayout r0 = r6.backgroundButtonsContainer
            android.widget.FrameLayout r3 = r6.backgroundPlayAnimationView
            r23 = r1
            r8 = 48
            r9 = 17
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r8, (int) r8, (int) r9)
            r0.addView(r3, r1)
            android.widget.FrameLayout r0 = r6.backgroundPlayAnimationView
            org.telegram.ui.ThemePreviewActivity$13 r1 = new org.telegram.ui.ThemePreviewActivity$13
            r1.<init>()
            r0.setOnClickListener(r1)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r7)
            r6.backgroundPlayAnimationImageView = r0
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r1)
            android.widget.ImageView r0 = r6.backgroundPlayAnimationImageView
            r1 = 2131165281(0x7var_, float:1.7944775E38)
            r0.setImageResource(r1)
            android.widget.FrameLayout r0 = r6.backgroundPlayAnimationView
            android.widget.ImageView r1 = r6.backgroundPlayAnimationImageView
            r3 = 17
            r8 = -2
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r8, (int) r8, (int) r3)
            r0.addView(r1, r9)
            goto L_0x0740
        L_0x073e:
            r23 = r1
        L_0x0740:
            r0 = 0
        L_0x0741:
            if (r0 >= r13) goto L_0x0838
            r1 = r0
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r6.backgroundCheckBoxView
            org.telegram.ui.Components.WallpaperCheckBoxView r8 = new org.telegram.ui.Components.WallpaperCheckBoxView
            int r9 = r6.screenType
            r32 = r10
            r10 = 1
            if (r9 == r10) goto L_0x0755
            java.lang.Object r9 = r6.currentWallpaper
            boolean r9 = r9 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r9 == 0) goto L_0x0757
        L_0x0755:
            if (r0 == 0) goto L_0x0759
        L_0x0757:
            r9 = 1
            goto L_0x075a
        L_0x0759:
            r9 = 0
        L_0x075a:
            org.telegram.ui.Components.BackupImageView r10 = r6.backgroundImage
            r8.<init>(r7, r9, r10)
            r3[r0] = r8
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r6.backgroundCheckBoxView
            r3 = r3[r0]
            int r8 = r6.backgroundColor
            r3.setBackgroundColor(r8)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r6.backgroundCheckBoxView
            r3 = r3[r0]
            r8 = r5[r0]
            r9 = r14[r0]
            r3.setText(r8, r9, r15)
            int r3 = r6.screenType
            r8 = 1
            if (r3 == r8) goto L_0x0791
            java.lang.Object r3 = r6.currentWallpaper
            boolean r3 = r3 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r3 == 0) goto L_0x0781
            goto L_0x0791
        L_0x0781:
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r6.backgroundCheckBoxView
            r3 = r3[r0]
            if (r0 != 0) goto L_0x078a
            boolean r8 = r6.isBlurred
            goto L_0x078c
        L_0x078a:
            boolean r8 = r6.isMotion
        L_0x078c:
            r9 = 0
            r3.setChecked(r8, r9)
            goto L_0x07be
        L_0x0791:
            r3 = 1
            if (r0 != r3) goto L_0x07b1
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r6.backgroundCheckBoxView
            r3 = r3[r0]
            org.telegram.tgnet.TLRPC$TL_wallPaper r8 = r6.selectedPattern
            if (r8 != 0) goto L_0x07ab
            org.telegram.ui.ActionBar.Theme$ThemeAccent r8 = r6.accent
            if (r8 == 0) goto L_0x07a9
            java.lang.String r8 = r8.patternSlug
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 != 0) goto L_0x07a9
            goto L_0x07ab
        L_0x07a9:
            r8 = 0
            goto L_0x07ac
        L_0x07ab:
            r8 = 1
        L_0x07ac:
            r9 = 0
            r3.setChecked(r8, r9)
            goto L_0x07be
        L_0x07b1:
            r9 = 0
            r3 = 2
            if (r0 != r3) goto L_0x07be
            org.telegram.ui.Components.WallpaperCheckBoxView[] r8 = r6.backgroundCheckBoxView
            r8 = r8[r0]
            boolean r10 = r6.isMotion
            r8.setChecked(r10, r9)
        L_0x07be:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r8 = r8 + r15
            android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
            r10 = -2
            r9.<init>(r8, r10)
            r10 = 17
            r9.gravity = r10
            r10 = 3
            if (r13 != r10) goto L_0x07ec
            if (r0 == 0) goto L_0x07e1
            r3 = 2
            if (r0 != r3) goto L_0x07d6
            goto L_0x07e1
        L_0x07d6:
            int r22 = r8 / 2
            int r25 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r3 = r22 + r25
            r9.rightMargin = r3
            goto L_0x0804
        L_0x07e1:
            int r3 = r8 / 2
            int r22 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r3 = r3 + r22
            r9.leftMargin = r3
            goto L_0x0804
        L_0x07ec:
            r3 = 1
            if (r0 != r3) goto L_0x07fa
            int r3 = r8 / 2
            int r22 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r3 = r3 + r22
            r9.leftMargin = r3
            goto L_0x0804
        L_0x07fa:
            int r3 = r8 / 2
            int r22 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r3 = r3 + r22
            r9.rightMargin = r3
        L_0x0804:
            android.widget.FrameLayout r3 = r6.backgroundButtonsContainer
            org.telegram.ui.Components.WallpaperCheckBoxView[] r10 = r6.backgroundCheckBoxView
            r10 = r10[r0]
            r3.addView(r10, r9)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r6.backgroundCheckBoxView
            r10 = r3[r0]
            r3 = r3[r0]
            r25 = r5
            org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda2 r5 = new org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda2
            r5.<init>(r6, r1, r10)
            r3.setOnClickListener(r5)
            r3 = 2
            if (r0 != r3) goto L_0x0830
            org.telegram.ui.Components.WallpaperCheckBoxView[] r5 = r6.backgroundCheckBoxView
            r5 = r5[r0]
            r3 = 0
            r5.setAlpha(r3)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r5 = r6.backgroundCheckBoxView
            r5 = r5[r0]
            r3 = 4
            r5.setVisibility(r3)
        L_0x0830:
            int r0 = r0 + 1
            r5 = r25
            r10 = r32
            goto L_0x0741
        L_0x0838:
            r25 = r5
            r32 = r10
            int r0 = r6.screenType
            r1 = 1
            if (r0 != r1) goto L_0x0977
            r0 = 2
            java.lang.String[] r1 = new java.lang.String[r0]
            int[] r3 = new int[r0]
            org.telegram.ui.Components.WallpaperCheckBoxView[] r5 = new org.telegram.ui.Components.WallpaperCheckBoxView[r0]
            r6.messagesCheckBoxView = r5
            r5 = 0
            android.widget.FrameLayout r8 = new android.widget.FrameLayout
            r8.<init>(r7)
            r6.messagesButtonsContainer = r8
            r8 = 2131624546(0x7f0e0262, float:1.8876275E38)
            java.lang.String r9 = "BackgroundAnimate"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r9 = 0
            r1[r9] = r8
            r8 = 2131624555(0x7f0e026b, float:1.8876293E38)
            java.lang.String r9 = "BackgroundColors"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r9 = 1
            r1[r9] = r8
            r8 = 0
        L_0x086b:
            int r9 = r1.length
            if (r8 >= r9) goto L_0x0885
            r9 = r1[r8]
            float r9 = r2.measureText(r9)
            double r9 = (double) r9
            double r9 = java.lang.Math.ceil(r9)
            int r9 = (int) r9
            r3[r8] = r9
            r9 = r3[r8]
            int r5 = java.lang.Math.max(r5, r9)
            int r8 = r8 + 1
            goto L_0x086b
        L_0x0885:
            org.telegram.ui.ThemePreviewActivity$14 r8 = new org.telegram.ui.ThemePreviewActivity$14
            r8.<init>(r7)
            r6.messagesPlayAnimationView = r8
            r9 = 0
            r8.setWillNotDraw(r9)
            android.widget.FrameLayout r8 = r6.messagesPlayAnimationView
            org.telegram.ui.ActionBar.Theme$ThemeAccent r9 = r6.accent
            int r9 = r9.myMessagesGradientAccentColor1
            if (r9 == 0) goto L_0x089a
            r9 = 0
            goto L_0x089b
        L_0x089a:
            r9 = 4
        L_0x089b:
            r8.setVisibility(r9)
            android.widget.FrameLayout r8 = r6.messagesPlayAnimationView
            org.telegram.ui.ActionBar.Theme$ThemeAccent r9 = r6.accent
            int r9 = r9.myMessagesGradientAccentColor1
            if (r9 == 0) goto L_0x08a9
            r9 = 1065353216(0x3var_, float:1.0)
            goto L_0x08ac
        L_0x08a9:
            r9 = 1036831949(0x3dcccccd, float:0.1)
        L_0x08ac:
            r8.setScaleX(r9)
            android.widget.FrameLayout r8 = r6.messagesPlayAnimationView
            org.telegram.ui.ActionBar.Theme$ThemeAccent r9 = r6.accent
            int r9 = r9.myMessagesGradientAccentColor1
            if (r9 == 0) goto L_0x08ba
            r9 = 1065353216(0x3var_, float:1.0)
            goto L_0x08bd
        L_0x08ba:
            r9 = 1036831949(0x3dcccccd, float:0.1)
        L_0x08bd:
            r8.setScaleY(r9)
            android.widget.FrameLayout r8 = r6.messagesPlayAnimationView
            org.telegram.ui.ActionBar.Theme$ThemeAccent r9 = r6.accent
            int r9 = r9.myMessagesGradientAccentColor1
            if (r9 == 0) goto L_0x08cb
            r9 = 1065353216(0x3var_, float:1.0)
            goto L_0x08cc
        L_0x08cb:
            r9 = 0
        L_0x08cc:
            r8.setAlpha(r9)
            android.widget.FrameLayout r8 = r6.messagesButtonsContainer
            android.widget.FrameLayout r9 = r6.messagesPlayAnimationView
            r10 = 48
            r13 = 17
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r10, (int) r13)
            r8.addView(r9, r14)
            android.widget.FrameLayout r8 = r6.messagesPlayAnimationView
            org.telegram.ui.ThemePreviewActivity$15 r9 = new org.telegram.ui.ThemePreviewActivity$15
            r9.<init>()
            r8.setOnClickListener(r9)
            android.widget.ImageView r8 = new android.widget.ImageView
            r8.<init>(r7)
            r6.messagesPlayAnimationImageView = r8
            android.widget.ImageView$ScaleType r9 = android.widget.ImageView.ScaleType.CENTER
            r8.setScaleType(r9)
            android.widget.ImageView r8 = r6.messagesPlayAnimationImageView
            r9 = 2131165281(0x7var_, float:1.7944775E38)
            r8.setImageResource(r9)
            android.widget.FrameLayout r8 = r6.messagesPlayAnimationView
            android.widget.ImageView r9 = r6.messagesPlayAnimationImageView
            r10 = 17
            r13 = -2
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r13, (int) r10)
            r8.addView(r9, r14)
            r8 = 0
        L_0x090b:
            r0 = 2
            if (r8 >= r0) goto L_0x0977
            r9 = r8
            org.telegram.ui.Components.WallpaperCheckBoxView[] r10 = r6.messagesCheckBoxView
            org.telegram.ui.Components.WallpaperCheckBoxView r13 = new org.telegram.ui.Components.WallpaperCheckBoxView
            if (r8 != 0) goto L_0x0917
            r14 = 1
            goto L_0x0918
        L_0x0917:
            r14 = 0
        L_0x0918:
            org.telegram.ui.Components.BackupImageView r15 = r6.backgroundImage
            r13.<init>(r7, r14, r15)
            r10[r8] = r13
            org.telegram.ui.Components.WallpaperCheckBoxView[] r10 = r6.messagesCheckBoxView
            r10 = r10[r8]
            r13 = r1[r8]
            r14 = r3[r8]
            r10.setText(r13, r14, r5)
            if (r8 != 0) goto L_0x0938
            org.telegram.ui.Components.WallpaperCheckBoxView[] r10 = r6.messagesCheckBoxView
            r10 = r10[r8]
            org.telegram.ui.ActionBar.Theme$ThemeAccent r13 = r6.accent
            boolean r13 = r13.myMessagesAnimated
            r14 = 0
            r10.setChecked(r13, r14)
        L_0x0938:
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r10 = r10 + r5
            android.widget.FrameLayout$LayoutParams r13 = new android.widget.FrameLayout$LayoutParams
            r14 = -2
            r13.<init>(r10, r14)
            r14 = 17
            r13.gravity = r14
            r14 = 1
            if (r8 != r14) goto L_0x0954
            int r14 = r10 / 2
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r14 = r14 + r15
            r13.leftMargin = r14
            goto L_0x095d
        L_0x0954:
            int r14 = r10 / 2
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r14 = r14 + r15
            r13.rightMargin = r14
        L_0x095d:
            android.widget.FrameLayout r14 = r6.messagesButtonsContainer
            org.telegram.ui.Components.WallpaperCheckBoxView[] r15 = r6.messagesCheckBoxView
            r15 = r15[r8]
            r14.addView(r15, r13)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r14 = r6.messagesCheckBoxView
            r15 = r14[r8]
            r14 = r14[r8]
            org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda3 r0 = new org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda3
            r0.<init>(r6, r9, r15)
            r14.setOnClickListener(r0)
            int r8 = r8 + 1
            goto L_0x090b
        L_0x0977:
            int r0 = r6.screenType
            r1 = 1
            if (r0 == r1) goto L_0x0982
            java.lang.Object r0 = r6.currentWallpaper
            boolean r0 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r0 == 0) goto L_0x0ce7
        L_0x0982:
            r0 = 0
            r6.isBlurred = r0
            r0 = 0
        L_0x0986:
            r1 = 2
            if (r0 >= r1) goto L_0x0ce7
            r3 = r0
            android.widget.FrameLayout[] r5 = r6.patternLayout
            org.telegram.ui.ThemePreviewActivity$16 r8 = new org.telegram.ui.ThemePreviewActivity$16
            r8.<init>(r7, r3, r4)
            r5[r0] = r8
            r5 = 1
            if (r0 == r5) goto L_0x099e
            int r5 = r6.screenType
            r1 = 2
            if (r5 != r1) goto L_0x099c
            goto L_0x099e
        L_0x099c:
            r8 = 4
            goto L_0x09a6
        L_0x099e:
            android.widget.FrameLayout[] r5 = r6.patternLayout
            r5 = r5[r0]
            r8 = 4
            r5.setVisibility(r8)
        L_0x09a6:
            android.widget.FrameLayout[] r5 = r6.patternLayout
            r5 = r5[r0]
            r9 = 0
            r5.setWillNotDraw(r9)
            int r5 = r6.screenType
            r1 = 2
            if (r5 != r1) goto L_0x09c2
            if (r0 != 0) goto L_0x09b8
            r5 = 321(0x141, float:4.5E-43)
            goto L_0x09ba
        L_0x09b8:
            r5 = 316(0x13c, float:4.43E-43)
        L_0x09ba:
            r9 = 83
            r10 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r5, (int) r9)
            goto L_0x09d0
        L_0x09c2:
            r9 = 83
            r10 = -1
            if (r0 != 0) goto L_0x09ca
            r5 = 273(0x111, float:3.83E-43)
            goto L_0x09cc
        L_0x09ca:
            r5 = 316(0x13c, float:4.43E-43)
        L_0x09cc:
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r5, (int) r9)
        L_0x09d0:
            if (r0 != 0) goto L_0x09f1
            int r9 = r5.height
            r10 = 1094713344(0x41400000, float:12.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r13 = r4.top
            int r10 = r10 + r13
            int r9 = r9 + r10
            r5.height = r9
            android.widget.FrameLayout[] r9 = r6.patternLayout
            r9 = r9[r0]
            r10 = 1094713344(0x41400000, float:12.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r13 = r4.top
            int r10 = r10 + r13
            r13 = 0
            r9.setPadding(r13, r10, r13, r13)
        L_0x09f1:
            android.widget.FrameLayout r9 = r6.page2
            android.widget.FrameLayout[] r10 = r6.patternLayout
            r10 = r10[r0]
            r9.addView(r10, r5)
            r9 = 1101529088(0x41a80000, float:21.0)
            r10 = 1
            if (r0 == r10) goto L_0x0a04
            int r10 = r6.screenType
            r1 = 2
            if (r10 != r1) goto L_0x0b4a
        L_0x0a04:
            android.widget.FrameLayout[] r10 = r6.patternsButtonsContainer
            org.telegram.ui.ThemePreviewActivity$17 r13 = new org.telegram.ui.ThemePreviewActivity$17
            r13.<init>(r7)
            r10[r0] = r13
            android.widget.FrameLayout[] r10 = r6.patternsButtonsContainer
            r10 = r10[r0]
            r13 = 0
            r10.setWillNotDraw(r13)
            android.widget.FrameLayout[] r10 = r6.patternsButtonsContainer
            r10 = r10[r0]
            r14 = 1077936128(0x40400000, float:3.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r10.setPadding(r13, r14, r13, r13)
            android.widget.FrameLayout[] r10 = r6.patternsButtonsContainer
            r10 = r10[r0]
            r13 = 1
            r10.setClickable(r13)
            android.widget.FrameLayout[] r10 = r6.patternLayout
            r10 = r10[r0]
            android.widget.FrameLayout[] r13 = r6.patternsButtonsContainer
            r13 = r13[r0]
            r14 = 80
            r1 = -1
            r15 = 51
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r1, (int) r15, (int) r14)
            r10.addView(r13, r14)
            android.widget.TextView[] r1 = r6.patternsCancelButton
            android.widget.TextView r10 = new android.widget.TextView
            r10.<init>(r7)
            r1[r0] = r10
            android.widget.TextView[] r1 = r6.patternsCancelButton
            r1 = r1[r0]
            r10 = 1097859072(0x41700000, float:15.0)
            r13 = 1
            r1.setTextSize(r13, r10)
            android.widget.TextView[] r1 = r6.patternsCancelButton
            r1 = r1[r0]
            android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r20)
            r1.setTypeface(r10)
            android.widget.TextView[] r1 = r6.patternsCancelButton
            r1 = r1[r0]
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r1.setTextColor(r10)
            android.widget.TextView[] r1 = r6.patternsCancelButton
            r1 = r1[r0]
            r10 = 2131624692(0x7f0e02f4, float:1.887657E38)
            java.lang.String r13 = "Cancel"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            java.lang.String r10 = r10.toUpperCase()
            r1.setText(r10)
            android.widget.TextView[] r1 = r6.patternsCancelButton
            r1 = r1[r0]
            r10 = 17
            r1.setGravity(r10)
            android.widget.TextView[] r1 = r6.patternsCancelButton
            r1 = r1[r0]
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r14 = 0
            r1.setPadding(r10, r14, r13, r14)
            android.widget.TextView[] r1 = r6.patternsCancelButton
            r1 = r1[r0]
            java.lang.String r10 = "listSelectorSDK21"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r10, r14)
            r1.setBackgroundDrawable(r10)
            android.widget.FrameLayout[] r1 = r6.patternsButtonsContainer
            r1 = r1[r0]
            android.widget.TextView[] r10 = r6.patternsCancelButton
            r10 = r10[r0]
            r13 = 51
            r14 = -2
            r15 = -1
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r14, (int) r15, (int) r13)
            r1.addView(r10, r8)
            android.widget.TextView[] r1 = r6.patternsCancelButton
            r1 = r1[r0]
            org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda1 r8 = new org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda1
            r8.<init>(r6, r3)
            r1.setOnClickListener(r8)
            android.widget.TextView[] r1 = r6.patternsSaveButton
            android.widget.TextView r8 = new android.widget.TextView
            r8.<init>(r7)
            r1[r0] = r8
            android.widget.TextView[] r1 = r6.patternsSaveButton
            r1 = r1[r0]
            r8 = 1097859072(0x41700000, float:15.0)
            r10 = 1
            r1.setTextSize(r10, r8)
            android.widget.TextView[] r1 = r6.patternsSaveButton
            r1 = r1[r0]
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r20)
            r1.setTypeface(r8)
            android.widget.TextView[] r1 = r6.patternsSaveButton
            r1 = r1[r0]
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r1.setTextColor(r8)
            android.widget.TextView[] r1 = r6.patternsSaveButton
            r1 = r1[r0]
            r8 = 2131624312(0x7f0e0178, float:1.88758E38)
            java.lang.String r10 = "ApplyTheme"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            java.lang.String r8 = r8.toUpperCase()
            r1.setText(r8)
            android.widget.TextView[] r1 = r6.patternsSaveButton
            r1 = r1[r0]
            r8 = 17
            r1.setGravity(r8)
            android.widget.TextView[] r1 = r6.patternsSaveButton
            r1 = r1[r0]
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r13 = 0
            r1.setPadding(r8, r13, r10, r13)
            android.widget.TextView[] r1 = r6.patternsSaveButton
            r1 = r1[r0]
            java.lang.String r8 = "listSelectorSDK21"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r8, r13)
            r1.setBackgroundDrawable(r8)
            android.widget.FrameLayout[] r1 = r6.patternsButtonsContainer
            r1 = r1[r0]
            android.widget.TextView[] r8 = r6.patternsSaveButton
            r8 = r8[r0]
            r10 = 53
            r13 = -2
            r14 = -1
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r14, (int) r10)
            r1.addView(r8, r10)
            android.widget.TextView[] r1 = r6.patternsSaveButton
            r1 = r1[r0]
            org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda26 r8 = new org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda26
            r8.<init>(r6, r3)
            r1.setOnClickListener(r8)
        L_0x0b4a:
            r1 = 1
            if (r0 != r1) goto L_0x0c4e
            android.widget.TextView r8 = new android.widget.TextView
            r8.<init>(r7)
            r8.setLines(r1)
            r8.setSingleLine(r1)
            r1 = 2131624550(0x7f0e0266, float:1.8876283E38)
            java.lang.String r10 = "BackgroundChoosePattern"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r10, r1)
            r8.setText(r1)
            java.lang.String r1 = "windowBackgroundWhiteBlackText"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r8.setTextColor(r1)
            r1 = 1101004800(0x41a00000, float:20.0)
            r10 = 1
            r8.setTextSize(r10, r1)
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r20)
            r8.setTypeface(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r10 = 1086324736(0x40CLASSNAME, float:6.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r13 = 1090519040(0x41000000, float:8.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r8.setPadding(r1, r10, r9, r13)
            android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.MIDDLE
            r8.setEllipsize(r1)
            r1 = 16
            r8.setGravity(r1)
            android.widget.FrameLayout[] r1 = r6.patternLayout
            r1 = r1[r0]
            r33 = -1
            r34 = 1111490560(0x42400000, float:48.0)
            r35 = 51
            r36 = 0
            r37 = 1101529088(0x41a80000, float:21.0)
            r38 = 0
            r39 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r33, r34, r35, r36, r37, r38, r39)
            r1.addView(r8, r9)
            org.telegram.ui.ThemePreviewActivity$18 r1 = new org.telegram.ui.ThemePreviewActivity$18
            r1.<init>(r7)
            r6.patternsListView = r1
            androidx.recyclerview.widget.LinearLayoutManager r9 = new androidx.recyclerview.widget.LinearLayoutManager
            r10 = 0
            r9.<init>(r7, r10, r10)
            r6.patternsLayoutManager = r9
            r1.setLayoutManager(r9)
            org.telegram.ui.Components.RecyclerListView r1 = r6.patternsListView
            org.telegram.ui.ThemePreviewActivity$PatternsAdapter r9 = new org.telegram.ui.ThemePreviewActivity$PatternsAdapter
            r9.<init>(r7)
            r6.patternsAdapter = r9
            r1.setAdapter(r9)
            org.telegram.ui.Components.RecyclerListView r1 = r6.patternsListView
            org.telegram.ui.ThemePreviewActivity$19 r9 = new org.telegram.ui.ThemePreviewActivity$19
            r9.<init>()
            r1.addItemDecoration(r9)
            android.widget.FrameLayout[] r1 = r6.patternLayout
            r1 = r1[r0]
            org.telegram.ui.Components.RecyclerListView r9 = r6.patternsListView
            r34 = 1120403456(0x42CLASSNAME, float:100.0)
            r37 = 1117257728(0x42980000, float:76.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r33, r34, r35, r36, r37, r38, r39)
            r1.addView(r9, r10)
            org.telegram.ui.Components.RecyclerListView r1 = r6.patternsListView
            org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda15 r9 = new org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda15
            r9.<init>(r6)
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r9)
            org.telegram.ui.Cells.HeaderCell r1 = new org.telegram.ui.Cells.HeaderCell
            r1.<init>(r7)
            r6.intensityCell = r1
            r9 = 2131624557(0x7f0e026d, float:1.8876297E38)
            java.lang.String r10 = "BackgroundIntensity"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r1.setText(r9)
            android.widget.FrameLayout[] r1 = r6.patternLayout
            r1 = r1[r0]
            org.telegram.ui.Cells.HeaderCell r9 = r6.intensityCell
            r34 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r37 = 1127153664(0x432var_, float:175.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r33, r34, r35, r36, r37, r38, r39)
            r1.addView(r9, r10)
            org.telegram.ui.ThemePreviewActivity$20 r1 = new org.telegram.ui.ThemePreviewActivity$20
            r1.<init>(r7)
            r6.intensitySeekBar = r1
            float r9 = r6.currentIntensity
            r1.setProgress(r9)
            org.telegram.ui.Components.SeekBarView r1 = r6.intensitySeekBar
            r9 = 1
            r1.setReportChanges(r9)
            org.telegram.ui.Components.SeekBarView r1 = r6.intensitySeekBar
            org.telegram.ui.ThemePreviewActivity$21 r9 = new org.telegram.ui.ThemePreviewActivity$21
            r9.<init>()
            r1.setDelegate(r9)
            android.widget.FrameLayout[] r1 = r6.patternLayout
            r1 = r1[r0]
            org.telegram.ui.Components.SeekBarView r9 = r6.intensitySeekBar
            r34 = 1108869120(0x42180000, float:38.0)
            r36 = 1084227584(0x40a00000, float:5.0)
            r37 = 1129512960(0x43530000, float:211.0)
            r38 = 1084227584(0x40a00000, float:5.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r33, r34, r35, r36, r37, r38, r39)
            r1.addView(r9, r10)
            goto L_0x0ce3
        L_0x0c4e:
            org.telegram.ui.Components.ColorPicker r1 = new org.telegram.ui.Components.ColorPicker
            boolean r8 = r6.editingTheme
            org.telegram.ui.ThemePreviewActivity$22 r9 = new org.telegram.ui.ThemePreviewActivity$22
            r9.<init>()
            r1.<init>(r7, r8, r9)
            r6.colorPicker = r1
            int r8 = r6.screenType
            r9 = 1
            if (r8 != r9) goto L_0x0cca
            android.widget.FrameLayout[] r8 = r6.patternLayout
            r8 = r8[r0]
            r10 = -1
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r10, (int) r9)
            r8.addView(r1, r13)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r1 = r6.applyingTheme
            boolean r1 = r1.isDark()
            if (r1 == 0) goto L_0x0c7e
            org.telegram.ui.Components.ColorPicker r1 = r6.colorPicker
            r8 = 1045220557(0x3e4ccccd, float:0.2)
            r1.setMinBrightness(r8)
            goto L_0x0c8e
        L_0x0c7e:
            org.telegram.ui.Components.ColorPicker r1 = r6.colorPicker
            r8 = 1028443341(0x3d4ccccd, float:0.05)
            r1.setMinBrightness(r8)
            org.telegram.ui.Components.ColorPicker r1 = r6.colorPicker
            r8 = 1061997773(0x3f4ccccd, float:0.8)
            r1.setMaxBrightness(r8)
        L_0x0c8e:
            org.telegram.ui.ActionBar.Theme$ThemeAccent r1 = r6.accent
            int r1 = r1.accentColor2
            if (r1 == 0) goto L_0x0CLASSNAME
            r37 = 2
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r37 = 1
        L_0x0CLASSNAME:
            org.telegram.ui.Components.ColorPicker r1 = r6.colorPicker
            r34 = 1
            r8 = 1
            boolean r35 = r6.hasChanges(r8)
            r36 = 2
            r38 = 0
            r39 = 0
            r40 = 0
            r33 = r1
            r33.setType(r34, r35, r36, r37, r38, r39, r40)
            org.telegram.ui.Components.ColorPicker r1 = r6.colorPicker
            org.telegram.ui.ActionBar.Theme$ThemeAccent r8 = r6.accent
            int r8 = r8.accentColor
            r9 = 0
            r1.setColor(r8, r9)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r1 = r6.accent
            int r1 = r1.accentColor2
            if (r1 == 0) goto L_0x0cc9
            org.telegram.ui.Components.ColorPicker r1 = r6.colorPicker
            org.telegram.ui.ActionBar.Theme$ThemeAccent r8 = r6.accent
            int r8 = r8.accentColor2
            r9 = 1
            r1.setColor(r8, r9)
        L_0x0cc9:
            goto L_0x0ce3
        L_0x0cca:
            android.widget.FrameLayout[] r8 = r6.patternLayout
            r8 = r8[r0]
            r33 = -1
            r34 = -1082130432(0xffffffffbvar_, float:-1.0)
            r35 = 1
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r33, r34, r35, r36, r37, r38, r39)
            r8.addView(r1, r9)
        L_0x0ce3:
            int r0 = r0 + 1
            goto L_0x0986
        L_0x0ce7:
            r0 = 0
            r6.updateButtonState(r0, r0)
            org.telegram.ui.Components.BackupImageView r0 = r6.backgroundImage
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            boolean r0 = r0.hasBitmapImage()
            if (r0 != 0) goto L_0x0cfe
            android.widget.FrameLayout r0 = r6.page2
            r1 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r0.setBackgroundColor(r1)
        L_0x0cfe:
            int r0 = r6.screenType
            r1 = 1
            if (r0 == r1) goto L_0x0d12
            java.lang.Object r0 = r6.currentWallpaper
            boolean r0 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r0 != 0) goto L_0x0d12
            org.telegram.ui.Components.BackupImageView r0 = r6.backgroundImage
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            r0.setCrossfadeWithOldImage(r1)
        L_0x0d12:
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            org.telegram.ui.ThemePreviewActivity$MessagesAdapter r1 = r6.messagesAdapter
            r0.setAdapter(r1)
            org.telegram.ui.ThemePreviewActivity$23 r0 = new org.telegram.ui.ThemePreviewActivity$23
            r0.<init>(r7)
            r6.frameLayout = r0
            r1 = 0
            r0.setWillNotDraw(r1)
            android.widget.FrameLayout r0 = r6.frameLayout
            r6.fragmentView = r0
            android.widget.FrameLayout r0 = r6.frameLayout
            android.view.ViewTreeObserver r0 = r0.getViewTreeObserver()
            org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda4 r1 = new org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda4
            r1.<init>(r6)
            r6.onGlobalLayoutListener = r1
            r0.addOnGlobalLayoutListener(r1)
            androidx.viewpager.widget.ViewPager r0 = new androidx.viewpager.widget.ViewPager
            r0.<init>(r7)
            r6.viewPager = r0
            org.telegram.ui.ThemePreviewActivity$24 r1 = new org.telegram.ui.ThemePreviewActivity$24
            r1.<init>()
            r0.addOnPageChangeListener(r1)
            androidx.viewpager.widget.ViewPager r0 = r6.viewPager
            org.telegram.ui.ThemePreviewActivity$25 r1 = new org.telegram.ui.ThemePreviewActivity$25
            r1.<init>()
            r0.setAdapter(r1)
            androidx.viewpager.widget.ViewPager r0 = r6.viewPager
            java.lang.String r1 = "actionBarDefault"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            org.telegram.messenger.AndroidUtilities.setViewPagerEdgeEffectColor(r0, r1)
            android.widget.FrameLayout r0 = r6.frameLayout
            androidx.viewpager.widget.ViewPager r1 = r6.viewPager
            r33 = -1
            r34 = -1082130432(0xffffffffbvar_, float:-1.0)
            r35 = 51
            r36 = 0
            r37 = 0
            r38 = 0
            int r2 = r6.screenType
            if (r2 != 0) goto L_0x0d75
            r15 = 1111490560(0x42400000, float:48.0)
            r39 = 1111490560(0x42400000, float:48.0)
            goto L_0x0d77
        L_0x0d75:
            r39 = 0
        L_0x0d77:
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r33, r34, r35, r36, r37, r38, r39)
            r0.addView(r1, r2)
            org.telegram.ui.Components.UndoView r0 = new org.telegram.ui.Components.UndoView
            r0.<init>(r7, r6)
            r6.undoView = r0
            r1 = 1112276992(0x424CLASSNAME, float:51.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setAdditionalTranslationY(r1)
            android.widget.FrameLayout r0 = r6.frameLayout
            org.telegram.ui.Components.UndoView r1 = r6.undoView
            r33 = -1
            r34 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r35 = 83
            r36 = 1090519040(0x41000000, float:8.0)
            r37 = 0
            r38 = 1090519040(0x41000000, float:8.0)
            r39 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r33, r34, r35, r36, r37, r38, r39)
            r0.addView(r1, r2)
            int r0 = r6.screenType
            if (r0 != 0) goto L_0x0ee2
            android.view.View r0 = new android.view.View
            r0.<init>(r7)
            java.lang.String r1 = "dialogShadowLine"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBackgroundColor(r1)
            android.widget.FrameLayout$LayoutParams r1 = new android.widget.FrameLayout$LayoutParams
            r2 = 83
            r3 = -1
            r4 = 1
            r1.<init>(r3, r4, r2)
            r2 = 1111490560(0x42400000, float:48.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.bottomMargin = r2
            android.widget.FrameLayout r2 = r6.frameLayout
            r2.addView(r0, r1)
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r7)
            r6.saveButtonsContainer = r2
            java.lang.String r3 = "windowBackgroundWhite"
            int r3 = r6.getButtonsColor(r3)
            r2.setBackgroundColor(r3)
            android.widget.FrameLayout r2 = r6.frameLayout
            android.widget.FrameLayout r3 = r6.saveButtonsContainer
            r4 = 83
            r5 = 48
            r8 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r8, (int) r5, (int) r4)
            r2.addView(r3, r4)
            org.telegram.ui.ThemePreviewActivity$26 r2 = new org.telegram.ui.ThemePreviewActivity$26
            r2.<init>(r7)
            r6.dotsContainer = r2
            android.widget.FrameLayout r3 = r6.saveButtonsContainer
            r4 = 22
            r5 = 8
            r8 = 17
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r5, (int) r8)
            r3.addView(r2, r4)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r7)
            r6.cancelButton = r2
            r3 = 1096810496(0x41600000, float:14.0)
            r4 = 1
            r2.setTextSize(r4, r3)
            android.widget.TextView r2 = r6.cancelButton
            int r3 = r6.getButtonsColor(r12)
            r2.setTextColor(r3)
            android.widget.TextView r2 = r6.cancelButton
            r3 = 17
            r2.setGravity(r3)
            android.widget.TextView r2 = r6.cancelButton
            r3 = 251658240(0xvar_, float:6.3108872E-30)
            r4 = 0
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r3, r4)
            r2.setBackgroundDrawable(r3)
            android.widget.TextView r2 = r6.cancelButton
            r3 = 1105723392(0x41e80000, float:29.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r5 = 1105723392(0x41e80000, float:29.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r2.setPadding(r3, r4, r5, r4)
            android.widget.TextView r2 = r6.cancelButton
            r3 = 2131624692(0x7f0e02f4, float:1.887657E38)
            java.lang.String r4 = "Cancel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.String r3 = r3.toUpperCase()
            r2.setText(r3)
            android.widget.TextView r2 = r6.cancelButton
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r20)
            r2.setTypeface(r3)
            android.widget.FrameLayout r2 = r6.saveButtonsContainer
            android.widget.TextView r3 = r6.cancelButton
            r4 = 51
            r5 = -2
            r8 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r8, (int) r4)
            r2.addView(r3, r4)
            android.widget.TextView r2 = r6.cancelButton
            org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda22 r3 = new org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda22
            r3.<init>(r6)
            r2.setOnClickListener(r3)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r7)
            r6.doneButton = r2
            r3 = 1096810496(0x41600000, float:14.0)
            r4 = 1
            r2.setTextSize(r4, r3)
            android.widget.TextView r2 = r6.doneButton
            int r3 = r6.getButtonsColor(r12)
            r2.setTextColor(r3)
            android.widget.TextView r2 = r6.doneButton
            r3 = 17
            r2.setGravity(r3)
            android.widget.TextView r2 = r6.doneButton
            r3 = 251658240(0xvar_, float:6.3108872E-30)
            r4 = 0
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r3, r4)
            r2.setBackgroundDrawable(r3)
            android.widget.TextView r2 = r6.doneButton
            r3 = 1105723392(0x41e80000, float:29.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r5 = 1105723392(0x41e80000, float:29.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r2.setPadding(r3, r4, r5, r4)
            android.widget.TextView r2 = r6.doneButton
            r3 = 2131624312(0x7f0e0178, float:1.88758E38)
            java.lang.String r4 = "ApplyTheme"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.String r3 = r3.toUpperCase()
            r2.setText(r3)
            android.widget.TextView r2 = r6.doneButton
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r20)
            r2.setTypeface(r3)
            android.widget.FrameLayout r2 = r6.saveButtonsContainer
            android.widget.TextView r3 = r6.doneButton
            r4 = 53
            r5 = -2
            r8 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r8, (int) r4)
            r2.addView(r3, r4)
            android.widget.TextView r2 = r6.doneButton
            org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda23 r3 = new org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda23
            r3.<init>(r6)
            r2.setOnClickListener(r3)
        L_0x0ee2:
            int r0 = r6.screenType
            r1 = 1
            if (r0 != r1) goto L_0x0efe
            boolean r0 = org.telegram.ui.ActionBar.Theme.hasCustomWallpaper()
            if (r0 != 0) goto L_0x0efe
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            long r0 = r0.backgroundOverrideColor
            r2 = 4294967296(0xNUM, double:2.121995791E-314)
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0efe
            r0 = 2
            r6.selectColorType(r0)
        L_0x0efe:
            java.util.List r0 = r41.getThemeDescriptionsInternal()
            r6.themeDescriptions = r0
            r0 = 1
            r6.setCurrentImage(r0)
            r1 = 0
            r6.updatePlayAnimationView(r1)
            boolean r2 = r6.showColor
            if (r2 == 0) goto L_0x0var_
            r6.showPatternsView(r1, r0, r1)
        L_0x0var_:
            android.view.View r0 = r6.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.createView(android.content.Context):android.view.View");
    }

    static /* synthetic */ void lambda$createView$1(View view, int position) {
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3932lambda$createView$2$orgtelegramuiThemePreviewActivity(ImageReceiver imageReceiver, boolean set, boolean thumb, boolean memCache) {
        if (!(this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
            Drawable dr = imageReceiver.getDrawable();
            if (set && dr != null) {
                if (!Theme.hasThemeKey("chat_serviceBackground") || (this.backgroundImage.getBackground() instanceof MotionBackgroundDrawable)) {
                    Theme.applyChatServiceMessageColor(AndroidUtilities.calcDrawableColor(dr), dr);
                }
                this.listView2.invalidateViews();
                FrameLayout frameLayout2 = this.backgroundButtonsContainer;
                if (frameLayout2 != null) {
                    int N = frameLayout2.getChildCount();
                    for (int a = 0; a < N; a++) {
                        this.backgroundButtonsContainer.getChildAt(a).invalidate();
                    }
                }
                FrameLayout frameLayout3 = this.messagesButtonsContainer;
                if (frameLayout3 != null) {
                    int N2 = frameLayout3.getChildCount();
                    for (int a2 = 0; a2 < N2; a2++) {
                        this.messagesButtonsContainer.getChildAt(a2).invalidate();
                    }
                }
                RadialProgress2 radialProgress2 = this.radialProgress;
                if (radialProgress2 != null) {
                    radialProgress2.setColors("chat_serviceBackground", "chat_serviceBackground", "chat_serviceText", "chat_serviceText");
                }
                if (!thumb && this.isBlurred && this.blurredBitmap == null) {
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(false);
                    updateBlurred();
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(true);
                }
            }
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3933lambda$createView$3$orgtelegramuiThemePreviewActivity(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3934lambda$createView$4$orgtelegramuiThemePreviewActivity(View view, int position, float x, float y) {
        if (view instanceof ChatMessageCell) {
            ChatMessageCell cell = (ChatMessageCell) view;
            if (!cell.isInsideBackground(x, y)) {
                selectColorType(2);
            } else if (cell.getMessageObject().isOutOwner()) {
                selectColorType(3);
            } else {
                selectColorType(1);
            }
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3935lambda$createView$5$orgtelegramuiThemePreviewActivity(int offsetX, int offsetY, float angle) {
        float progress;
        if (this.isMotion) {
            Drawable background = this.backgroundImage.getBackground();
            if (this.motionAnimation != null) {
                progress = (this.backgroundImage.getScaleX() - 1.0f) / (this.parallaxScale - 1.0f);
            } else {
                progress = 1.0f;
            }
            this.backgroundImage.setTranslationX(((float) offsetX) * progress);
            this.backgroundImage.setTranslationY(((float) offsetY) * progress);
        }
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3936lambda$createView$6$orgtelegramuiThemePreviewActivity(View view) {
        boolean done;
        int rotation;
        int color;
        String slug;
        File toFile;
        Theme.OverrideWallpaperInfo wallpaperInfo;
        String slugStr;
        int color2;
        int color3;
        String slug2;
        File f;
        boolean done2;
        boolean sameFile = false;
        Theme.ThemeInfo theme = Theme.getActiveTheme();
        String originalFileName = theme.generateWallpaperName((Theme.ThemeAccent) null, this.isBlurred);
        String fileName = this.isBlurred ? theme.generateWallpaperName((Theme.ThemeAccent) null, false) : originalFileName;
        File toFile2 = new File(ApplicationLoader.getFilesDirFixed(), originalFileName);
        Object obj = this.currentWallpaper;
        if (obj instanceof TLRPC.TL_wallPaper) {
            if (this.originalBitmap != null) {
                try {
                    FileOutputStream stream = new FileOutputStream(toFile2);
                    this.originalBitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
                    stream.close();
                    done = true;
                } catch (Exception e) {
                    done = false;
                    FileLog.e((Throwable) e);
                }
            } else {
                ImageReceiver imageReceiver = this.backgroundImage.getImageReceiver();
                if (imageReceiver.hasNotThumb() || imageReceiver.hasStaticThumb()) {
                    Bitmap bitmap = imageReceiver.getBitmap();
                    try {
                        FileOutputStream stream2 = new FileOutputStream(toFile2);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream2);
                        stream2.close();
                        done2 = true;
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                        done2 = false;
                    }
                    done = done2;
                } else {
                    done = false;
                }
            }
            if (!done) {
                try {
                    done = AndroidUtilities.copyFile(FileLoader.getPathToAttach(((TLRPC.TL_wallPaper) this.currentWallpaper).document, true), toFile2);
                } catch (Exception e3) {
                    done = false;
                    FileLog.e((Throwable) e3);
                }
            }
        } else if (obj instanceof WallpapersListActivity.ColorWallpaper) {
            if (this.selectedPattern != null) {
                try {
                    WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) obj;
                    Bitmap bitmap2 = this.backgroundImage.getImageReceiver().getBitmap();
                    Bitmap dst = Bitmap.createBitmap(bitmap2.getWidth(), bitmap2.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(dst);
                    if (this.backgroundGradientColor2 == 0) {
                        if (this.backgroundGradientColor1 != 0) {
                            GradientDrawable gradientDrawable = new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(this.backgroundRotation), new int[]{this.backgroundColor, this.backgroundGradientColor1});
                            gradientDrawable.setBounds(0, 0, dst.getWidth(), dst.getHeight());
                            gradientDrawable.draw(canvas);
                        } else {
                            canvas.drawColor(this.backgroundColor);
                        }
                    }
                    Paint paint = new Paint(2);
                    paint.setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
                    paint.setAlpha((int) (Math.abs(this.currentIntensity) * 255.0f));
                    canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint);
                    FileOutputStream stream3 = new FileOutputStream(toFile2);
                    if (this.backgroundGradientColor2 != 0) {
                        dst.compress(Bitmap.CompressFormat.PNG, 100, stream3);
                    } else {
                        dst.compress(Bitmap.CompressFormat.JPEG, 87, stream3);
                    }
                    stream3.close();
                    done = true;
                } catch (Throwable e4) {
                    FileLog.e(e4);
                    done = false;
                }
            } else {
                done = true;
            }
        } else if (obj instanceof WallpapersListActivity.FileWallpaper) {
            WallpapersListActivity.FileWallpaper wallpaper = (WallpapersListActivity.FileWallpaper) obj;
            if (wallpaper.resId != 0 || "t".equals(wallpaper.slug)) {
                done = true;
            } else {
                try {
                    File fromFile = wallpaper.originalPath != null ? wallpaper.originalPath : wallpaper.path;
                    boolean equals = fromFile.equals(toFile2);
                    sameFile = equals;
                    if (equals) {
                        done = true;
                    } else {
                        done = AndroidUtilities.copyFile(fromFile, toFile2);
                    }
                } catch (Exception e5) {
                    done = false;
                    FileLog.e((Throwable) e5);
                }
            }
        } else if (obj instanceof MediaController.SearchImage) {
            MediaController.SearchImage wallpaper2 = (MediaController.SearchImage) obj;
            if (wallpaper2.photo != null) {
                f = FileLoader.getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(wallpaper2.photo.sizes, this.maxWallpaperSize, true), true);
            } else {
                f = ImageLoader.getHttpFilePath(wallpaper2.imageUrl, "jpg");
            }
            try {
                done = AndroidUtilities.copyFile(f, toFile2);
            } catch (Exception e6) {
                FileLog.e((Throwable) e6);
                done = false;
            }
        } else {
            done = false;
        }
        if (this.isBlurred) {
            try {
                FileOutputStream stream4 = new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), fileName));
                this.blurredBitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream4);
                stream4.close();
                done = true;
            } catch (Throwable e7) {
                FileLog.e(e7);
                done = false;
            }
        }
        int gradientColor1 = 0;
        int gradientColor2 = 0;
        int gradientColor3 = 0;
        File path = null;
        Object obj2 = this.currentWallpaper;
        Theme.ThemeInfo themeInfo = theme;
        if ((obj2 instanceof TLRPC.TL_wallPaper) != 0) {
            slug = ((TLRPC.TL_wallPaper) obj2).slug;
            color = 0;
            rotation = 45;
        } else if (obj2 instanceof WallpapersListActivity.ColorWallpaper) {
            if ("d".equals(((WallpapersListActivity.ColorWallpaper) obj2).slug)) {
                color3 = 0;
                slug = "d";
                rotation = 45;
            } else {
                TLRPC.TL_wallPaper tL_wallPaper = this.selectedPattern;
                if (tL_wallPaper != null) {
                    slug2 = tL_wallPaper.slug;
                } else {
                    slug2 = "c";
                }
                color3 = this.backgroundColor;
                gradientColor1 = this.backgroundGradientColor1;
                gradientColor2 = this.backgroundGradientColor2;
                gradientColor3 = this.backgroundGradientColor3;
                rotation = this.backgroundRotation;
                slug = slug2;
            }
            color = color3;
        } else if (obj2 instanceof WallpapersListActivity.FileWallpaper) {
            WallpapersListActivity.FileWallpaper wallPaper = (WallpapersListActivity.FileWallpaper) obj2;
            String slug3 = wallPaper.slug;
            path = wallPaper.path;
            slug = slug3;
            rotation = 45;
            color = 0;
        } else if (obj2 instanceof MediaController.SearchImage) {
            MediaController.SearchImage wallPaper2 = (MediaController.SearchImage) obj2;
            if (wallPaper2.photo != null) {
                color2 = 0;
                path = FileLoader.getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(wallPaper2.photo.sizes, this.maxWallpaperSize, true), true);
            } else {
                color2 = 0;
                path = ImageLoader.getHttpFilePath(wallPaper2.imageUrl, "jpg");
            }
            slug = "";
            rotation = 45;
            color = color2;
        } else {
            rotation = 45;
            color = 0;
            slug = "d";
        }
        Theme.OverrideWallpaperInfo wallpaperInfo2 = new Theme.OverrideWallpaperInfo();
        wallpaperInfo2.fileName = fileName;
        wallpaperInfo2.originalFileName = originalFileName;
        wallpaperInfo2.slug = slug;
        String str = fileName;
        wallpaperInfo2.isBlurred = this.isBlurred;
        wallpaperInfo2.isMotion = this.isMotion;
        wallpaperInfo2.color = color;
        wallpaperInfo2.gradientColor1 = gradientColor1;
        wallpaperInfo2.gradientColor2 = gradientColor2;
        wallpaperInfo2.gradientColor3 = gradientColor3;
        wallpaperInfo2.rotation = rotation;
        wallpaperInfo2.intensity = this.currentIntensity;
        Object obj3 = this.currentWallpaper;
        String str2 = originalFileName;
        if (obj3 instanceof WallpapersListActivity.ColorWallpaper) {
            WallpapersListActivity.ColorWallpaper colorWallpaper2 = (WallpapersListActivity.ColorWallpaper) obj3;
            if ("c".equals(slug) || "t".equals(slug) || "d".equals(slug)) {
                slugStr = null;
            } else {
                slugStr = slug;
            }
            float intensity = colorWallpaper2.intensity;
            if (intensity < 0.0f && !Theme.getActiveTheme().isDark()) {
                intensity *= -1.0f;
            }
            toFile = toFile2;
            if (colorWallpaper2.parentWallpaper == null || colorWallpaper2.color != color || colorWallpaper2.gradientColor1 != gradientColor1 || colorWallpaper2.gradientColor2 != gradientColor2 || colorWallpaper2.gradientColor3 != gradientColor3) {
                int i = gradientColor1;
            } else if (!TextUtils.equals(colorWallpaper2.slug, slugStr) || colorWallpaper2.gradientRotation != rotation) {
                int i2 = gradientColor1;
            } else if (this.selectedPattern == null || Math.abs(intensity - this.currentIntensity) < 0.001f) {
                int i3 = color;
                int i4 = gradientColor1;
                wallpaperInfo2.wallpaperId = colorWallpaper2.parentWallpaper.id;
                wallpaperInfo2.accessHash = colorWallpaper2.parentWallpaper.access_hash;
            } else {
                int i5 = color;
                int i6 = gradientColor1;
            }
        } else {
            toFile = toFile2;
            int i7 = color;
            int i8 = gradientColor1;
        }
        MessagesController.getInstance(this.currentAccount).saveWallpaperToServer(path, wallpaperInfo2, slug != null, 0);
        if (done) {
            Theme.serviceMessageColorBackup = Theme.getColor("chat_serviceBackground");
            if ("t".equals(wallpaperInfo2.slug)) {
                wallpaperInfo = null;
            } else {
                wallpaperInfo = wallpaperInfo2;
            }
            Theme.getActiveTheme().setOverrideWallpaper(wallpaperInfo);
            Theme.reloadWallpaper();
            if (!sameFile) {
                ImageLoader.getInstance().removeImage(ImageLoader.getHttpFileName(toFile.getAbsolutePath()) + "@100_100");
            }
        }
        WallpaperActivityDelegate wallpaperActivityDelegate = this.delegate;
        if (wallpaperActivityDelegate != null) {
            wallpaperActivityDelegate.didSetNewBackground();
        }
        finishFragment();
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3937lambda$createView$7$orgtelegramuiThemePreviewActivity(int num, WallpaperCheckBoxView view, View v) {
        if (this.backgroundButtonsContainer.getAlpha() == 1.0f && this.patternViewAnimation == null) {
            int i = this.screenType;
            if ((i == 1 || (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) && num == 2) {
                view.setChecked(!view.isChecked(), true);
                boolean isChecked = view.isChecked();
                this.isMotion = isChecked;
                this.parallaxEffect.setEnabled(isChecked);
                animateMotionChange();
                return;
            }
            boolean z = false;
            if (num == 1 && (i == 1 || (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper))) {
                if (this.backgroundCheckBoxView[1].isChecked()) {
                    this.lastSelectedPattern = this.selectedPattern;
                    this.backgroundImage.setImageDrawable((Drawable) null);
                    this.selectedPattern = null;
                    this.isMotion = false;
                    updateButtonState(false, true);
                    animateMotionChange();
                    if (this.patternLayout[1].getVisibility() == 0) {
                        if (this.screenType == 1) {
                            showPatternsView(0, true, true);
                        } else {
                            showPatternsView(num, this.patternLayout[num].getVisibility() != 0, true);
                        }
                    }
                } else {
                    selectPattern(this.lastSelectedPattern != null ? -1 : 0);
                    if (this.screenType == 1) {
                        showPatternsView(1, true, true);
                    } else {
                        showPatternsView(num, this.patternLayout[num].getVisibility() != 0, true);
                    }
                }
                WallpaperCheckBoxView wallpaperCheckBoxView = this.backgroundCheckBoxView[1];
                if (this.selectedPattern != null) {
                    z = true;
                }
                wallpaperCheckBoxView.setChecked(z, true);
                updateSelectedPattern(true);
                this.patternsListView.invalidateViews();
                updateMotionButton();
            } else if (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                if (this.patternLayout[num].getVisibility() != 0) {
                    z = true;
                }
                showPatternsView(num, z, true);
            } else if (i != 1) {
                view.setChecked(!view.isChecked(), true);
                if (num == 0) {
                    boolean isChecked2 = view.isChecked();
                    this.isBlurred = isChecked2;
                    if (isChecked2) {
                        this.backgroundImage.getImageReceiver().setForceCrossfade(true);
                    }
                    updateBlurred();
                    return;
                }
                boolean isChecked3 = view.isChecked();
                this.isMotion = isChecked3;
                this.parallaxEffect.setEnabled(isChecked3);
                animateMotionChange();
            }
        }
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3938lambda$createView$8$orgtelegramuiThemePreviewActivity(int num, WallpaperCheckBoxView view, View v) {
        if (this.messagesButtonsContainer.getAlpha() == 1.0f && num == 0) {
            view.setChecked(!view.isChecked(), true);
            this.accent.myMessagesAnimated = view.isChecked();
            Theme.refreshThemeColors(true, true);
            this.listView2.invalidateViews();
        }
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3939lambda$createView$9$orgtelegramuiThemePreviewActivity(int num, View v) {
        int i = num;
        if (this.patternViewAnimation == null) {
            if (i == 0) {
                this.backgroundRotation = this.previousBackgroundRotation;
                setBackgroundColor(this.previousBackgroundGradientColor3, 3, true, true);
                setBackgroundColor(this.previousBackgroundGradientColor2, 2, true, true);
                setBackgroundColor(this.previousBackgroundGradientColor1, 1, true, true);
                setBackgroundColor(this.previousBackgroundColor, 0, true, true);
            } else {
                TLRPC.TL_wallPaper tL_wallPaper = this.previousSelectedPattern;
                this.selectedPattern = tL_wallPaper;
                if (tL_wallPaper == null) {
                    this.backgroundImage.setImageDrawable((Drawable) null);
                } else {
                    this.backgroundImage.setImage(ImageLocation.getForDocument(tL_wallPaper.document), this.imageFilter, (ImageLocation) null, (String) null, "jpg", this.selectedPattern.document.size, 1, this.selectedPattern);
                }
                this.backgroundCheckBoxView[1].setChecked(this.selectedPattern != null, false);
                float f = this.previousIntensity;
                this.currentIntensity = f;
                this.intensitySeekBar.setProgress(f);
                this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
                updateButtonState(false, true);
                updateSelectedPattern(true);
            }
            if (this.screenType == 2) {
                showPatternsView(i, false, true);
                return;
            }
            if (this.selectedPattern == null) {
                if (this.isMotion) {
                    this.isMotion = false;
                    this.backgroundCheckBoxView[0].setChecked(false, true);
                    animateMotionChange();
                }
                updateMotionButton();
            }
            showPatternsView(0, true, true);
        }
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3927lambda$createView$10$orgtelegramuiThemePreviewActivity(int num, View v) {
        if (this.patternViewAnimation == null) {
            if (this.screenType == 2) {
                showPatternsView(num, false, true);
            } else {
                showPatternsView(0, true, true);
            }
        }
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3928lambda$createView$11$orgtelegramuiThemePreviewActivity(View view, int position) {
        boolean previousMotion = this.selectedPattern != null;
        selectPattern(position);
        if (previousMotion == (this.selectedPattern == null)) {
            animateMotionChange();
            updateMotionButton();
        }
        updateSelectedPattern(true);
        this.backgroundCheckBoxView[1].setChecked(this.selectedPattern != null, true);
        this.patternsListView.invalidateViews();
        int left = view.getLeft();
        int right = view.getRight();
        int extra = AndroidUtilities.dp(52.0f);
        if (left - extra < 0) {
            this.patternsListView.smoothScrollBy(left - extra, 0);
        } else if (right + extra > this.patternsListView.getMeasuredWidth()) {
            RecyclerListView recyclerListView = this.patternsListView;
            recyclerListView.smoothScrollBy((right + extra) - recyclerListView.getMeasuredWidth(), 0);
        }
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3929lambda$createView$12$orgtelegramuiThemePreviewActivity() {
        this.watchForKeyboardEndTime = SystemClock.elapsedRealtime() + 1500;
        this.frameLayout.invalidate();
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3930lambda$createView$13$orgtelegramuiThemePreviewActivity(View v) {
        cancelThemeApply(false);
    }

    /* renamed from: lambda$createView$14$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3931lambda$createView$14$orgtelegramuiThemePreviewActivity(View v) {
        Theme.ThemeAccent previousAccent;
        Theme.ThemeInfo previousTheme = Theme.getPreviousTheme();
        if (previousTheme != null) {
            if (previousTheme == null || previousTheme.prevAccentId < 0) {
                previousAccent = previousTheme.getAccent(false);
            } else {
                previousAccent = previousTheme.themeAccentsMap.get(previousTheme.prevAccentId);
            }
            if (this.accent != null) {
                saveAccentWallpaper();
                Theme.saveThemeAccents(this.applyingTheme, true, false, false, false);
                Theme.clearPreviousTheme();
                Theme.applyTheme(this.applyingTheme, this.nightTheme);
                this.parentLayout.rebuildAllFragmentViews(false, false);
            } else {
                this.parentLayout.rebuildAllFragmentViews(false, false);
                Theme.applyThemeFile(new File(this.applyingTheme.pathToFile), this.applyingTheme.name, this.applyingTheme.info, false);
                MessagesController.getInstance(this.applyingTheme.account).saveTheme(this.applyingTheme, (Theme.ThemeAccent) null, false, false);
                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
                editor.putString("lastDayTheme", this.applyingTheme.getKey());
                editor.commit();
            }
            finishFragment();
            if (this.screenType == 0) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didApplyNewTheme, previousTheme, previousAccent, Boolean.valueOf(this.deleteOnCancel));
            }
        }
    }

    /* access modifiers changed from: private */
    public void onColorsRotate() {
        if (this.screenType == 2) {
            this.backgroundRotation += 45;
            while (true) {
                int i = this.backgroundRotation;
                if (i >= 360) {
                    this.backgroundRotation = i - 360;
                } else {
                    setBackgroundColor(this.backgroundColor, 0, true, true);
                    return;
                }
            }
        } else {
            this.accent.backgroundRotation += 45;
            while (this.accent.backgroundRotation >= 360) {
                this.accent.backgroundRotation -= 360;
            }
            Theme.refreshThemeColors();
        }
    }

    /* access modifiers changed from: private */
    public void selectColorType(int id) {
        selectColorType(id, true);
    }

    private void selectColorType(int id, boolean ask) {
        int prevType;
        int i;
        int prevType2;
        int count;
        int count2;
        int i2 = id;
        if (getParentActivity() != null && this.colorType != i2 && this.patternViewAnimation == null) {
            if (!ask || i2 != 2 || (!Theme.hasCustomWallpaper() && this.accent.backgroundOverrideColor != 4294967296L)) {
                int prevType3 = this.colorType;
                this.colorType = i2;
                switch (i2) {
                    case 1:
                        prevType = prevType3;
                        this.dropDown.setText(LocaleController.getString("ColorPickerMainColor", NUM));
                        this.colorPicker.setType(1, hasChanges(1), 2, this.accent.accentColor2 != 0 ? 2 : 1, false, 0, false);
                        this.colorPicker.setColor(this.accent.accentColor, 0);
                        if (this.accent.accentColor2 != 0) {
                            this.colorPicker.setColor(this.accent.accentColor2, 1);
                        }
                        if (prevType == 2 || (prevType == 3 && this.accent.myMessagesGradientAccentColor2 != 0)) {
                            this.messagesAdapter.notifyItemRemoved(0);
                            break;
                        }
                    case 2:
                        this.dropDown.setText(LocaleController.getString("ColorPickerBackground", NUM));
                        int defaultBackground = Theme.getColor("chat_wallpaper");
                        int defaultGradient1 = Theme.hasThemeKey("chat_wallpaper_gradient_to") ? Theme.getColor("chat_wallpaper_gradient_to") : 0;
                        int defaultGradient2 = Theme.hasThemeKey("key_chat_wallpaper_gradient_to2") ? Theme.getColor("key_chat_wallpaper_gradient_to2") : 0;
                        int defaultGradient3 = Theme.hasThemeKey("key_chat_wallpaper_gradient_to3") ? Theme.getColor("key_chat_wallpaper_gradient_to3") : 0;
                        int backgroundGradientOverrideColor1 = (int) this.accent.backgroundGradientOverrideColor1;
                        if (backgroundGradientOverrideColor1 == 0 && this.accent.backgroundGradientOverrideColor1 != 0) {
                            defaultGradient1 = 0;
                        }
                        int backgroundGradientOverrideColor2 = (int) this.accent.backgroundGradientOverrideColor2;
                        if (backgroundGradientOverrideColor2 == 0 && this.accent.backgroundGradientOverrideColor2 != 0) {
                            defaultGradient2 = 0;
                        }
                        int backgroundGradientOverrideColor3 = (int) this.accent.backgroundGradientOverrideColor3;
                        if (backgroundGradientOverrideColor3 == 0) {
                            prevType2 = prevType3;
                            if (this.accent.backgroundGradientOverrideColor3 != 0) {
                                defaultGradient3 = 0;
                            }
                        } else {
                            prevType2 = prevType3;
                        }
                        int backgroundOverrideColor = (int) this.accent.backgroundOverrideColor;
                        if (backgroundGradientOverrideColor1 == 0 && defaultGradient1 == 0) {
                            count = 1;
                        } else if (backgroundGradientOverrideColor3 != 0 || defaultGradient3 != 0) {
                            count = 4;
                        } else if (backgroundGradientOverrideColor2 == 0 && defaultGradient2 == 0) {
                            count = 2;
                        } else {
                            count = 3;
                        }
                        this.colorPicker.setType(2, hasChanges(2), 4, count, false, this.accent.backgroundRotation, false);
                        this.colorPicker.setColor(backgroundGradientOverrideColor3 != 0 ? backgroundGradientOverrideColor3 : defaultGradient3, 3);
                        this.colorPicker.setColor(backgroundGradientOverrideColor2 != 0 ? backgroundGradientOverrideColor2 : defaultGradient2, 2);
                        this.colorPicker.setColor(backgroundGradientOverrideColor1 != 0 ? backgroundGradientOverrideColor1 : defaultGradient1, 1);
                        this.colorPicker.setColor(backgroundOverrideColor != 0 ? backgroundOverrideColor : defaultBackground, 0);
                        prevType = prevType2;
                        if (prevType == 1 || this.accent.myMessagesGradientAccentColor2 == 0) {
                            this.messagesAdapter.notifyItemInserted(0);
                        } else {
                            this.messagesAdapter.notifyItemChanged(0);
                        }
                        this.listView2.smoothScrollBy(0, AndroidUtilities.dp(60.0f));
                        break;
                    case 3:
                        this.dropDown.setText(LocaleController.getString("ColorPickerMyMessages", NUM));
                        if (this.accent.myMessagesGradientAccentColor1 == 0) {
                            count2 = 1;
                        } else if (this.accent.myMessagesGradientAccentColor3 != 0) {
                            count2 = 4;
                        } else if (this.accent.myMessagesGradientAccentColor2 != 0) {
                            count2 = 3;
                        } else {
                            count2 = 2;
                        }
                        this.colorPicker.setType(2, hasChanges(3), 4, count2, true, 0, false);
                        this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor3, 3);
                        this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor2, 2);
                        this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor1, 1);
                        this.colorPicker.setColor(this.accent.myMessagesAccentColor != 0 ? this.accent.myMessagesAccentColor : this.accent.accentColor, 0);
                        this.messagesCheckBoxView[1].setColor(0, this.accent.myMessagesAccentColor);
                        this.messagesCheckBoxView[1].setColor(1, this.accent.myMessagesGradientAccentColor1);
                        this.messagesCheckBoxView[1].setColor(2, this.accent.myMessagesGradientAccentColor2);
                        this.messagesCheckBoxView[1].setColor(3, this.accent.myMessagesGradientAccentColor3);
                        if (this.accent.myMessagesGradientAccentColor2 != 0) {
                            if (prevType3 == 1) {
                                this.messagesAdapter.notifyItemInserted(0);
                            } else {
                                this.messagesAdapter.notifyItemChanged(0);
                            }
                        } else if (prevType3 == 2) {
                            this.messagesAdapter.notifyItemRemoved(0);
                        }
                        this.listView2.smoothScrollBy(0, AndroidUtilities.dp(60.0f));
                        showAnimationHint();
                        prevType = prevType3;
                        break;
                    default:
                        prevType = prevType3;
                        break;
                }
                if (i2 == 1 || i2 == 3) {
                    if (prevType == 2) {
                        i = 1;
                        if (this.patternLayout[1].getVisibility() == 0) {
                            showPatternsView(0, true, true);
                        }
                    } else {
                        i = 1;
                    }
                    if (i2 != i) {
                        this.colorPicker.setMinBrightness(0.0f);
                        this.colorPicker.setMaxBrightness(1.0f);
                    } else if (this.applyingTheme.isDark()) {
                        this.colorPicker.setMinBrightness(0.2f);
                    } else {
                        this.colorPicker.setMinBrightness(0.05f);
                        this.colorPicker.setMaxBrightness(0.8f);
                    }
                } else {
                    this.colorPicker.setMinBrightness(0.0f);
                    this.colorPicker.setMaxBrightness(1.0f);
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("ChangeChatBackground", NUM));
                if (!Theme.hasCustomWallpaper() || Theme.isCustomWallpaperColor()) {
                    builder.setMessage(LocaleController.getString("ChangeColorToColor", NUM));
                    builder.setPositiveButton(LocaleController.getString("Reset", NUM), new ThemePreviewActivity$$ExternalSyntheticLambda19(this));
                    builder.setNegativeButton(LocaleController.getString("Continue", NUM), new ThemePreviewActivity$$ExternalSyntheticLambda20(this));
                } else {
                    builder.setMessage(LocaleController.getString("ChangeWallpaperToColor", NUM));
                    builder.setPositiveButton(LocaleController.getString("Change", NUM), new ThemePreviewActivity$$ExternalSyntheticLambda21(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                }
                showDialog(builder.create());
            }
        }
    }

    /* renamed from: lambda$selectColorType$15$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3946lambda$selectColorType$15$orgtelegramuiThemePreviewActivity(DialogInterface dialog, int which) {
        if (this.accent.backgroundOverrideColor == 4294967296L) {
            this.accent.backgroundOverrideColor = 0;
            this.accent.backgroundGradientOverrideColor1 = 0;
            this.accent.backgroundGradientOverrideColor2 = 0;
            this.accent.backgroundGradientOverrideColor3 = 0;
            updatePlayAnimationView(false);
            Theme.refreshThemeColors();
        }
        this.removeBackgroundOverride = true;
        Theme.resetCustomWallpaper(true);
        selectColorType(2, false);
    }

    /* renamed from: lambda$selectColorType$16$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3947lambda$selectColorType$16$orgtelegramuiThemePreviewActivity(DialogInterface dialog, int which) {
        if (Theme.isCustomWallpaperColor()) {
            Theme.ThemeAccent themeAccent = this.accent;
            themeAccent.backgroundOverrideColor = (long) themeAccent.overrideWallpaper.color;
            Theme.ThemeAccent themeAccent2 = this.accent;
            themeAccent2.backgroundGradientOverrideColor1 = (long) themeAccent2.overrideWallpaper.gradientColor1;
            Theme.ThemeAccent themeAccent3 = this.accent;
            themeAccent3.backgroundGradientOverrideColor2 = (long) themeAccent3.overrideWallpaper.gradientColor2;
            Theme.ThemeAccent themeAccent4 = this.accent;
            themeAccent4.backgroundGradientOverrideColor3 = (long) themeAccent4.overrideWallpaper.gradientColor3;
            Theme.ThemeAccent themeAccent5 = this.accent;
            themeAccent5.backgroundRotation = themeAccent5.overrideWallpaper.rotation;
            Theme.ThemeAccent themeAccent6 = this.accent;
            themeAccent6.patternSlug = themeAccent6.overrideWallpaper.slug;
            Theme.ThemeAccent themeAccent7 = this.accent;
            float f = themeAccent7.overrideWallpaper.intensity;
            themeAccent7.patternIntensity = f;
            this.currentIntensity = f;
            if (this.accent.patternSlug != null && !"c".equals(this.accent.patternSlug)) {
                int a = 0;
                int N = this.patterns.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) this.patterns.get(a);
                    if (wallPaper.pattern && this.accent.patternSlug.equals(wallPaper.slug)) {
                        this.selectedPattern = wallPaper;
                        break;
                    }
                    a++;
                }
            } else {
                this.selectedPattern = null;
            }
            this.removeBackgroundOverride = true;
            this.backgroundCheckBoxView[1].setChecked(this.selectedPattern != null, true);
            updatePlayAnimationView(false);
            Theme.refreshThemeColors();
        }
        Drawable background = this.backgroundImage.getBackground();
        if (background instanceof MotionBackgroundDrawable) {
            MotionBackgroundDrawable drawable = (MotionBackgroundDrawable) background;
            drawable.setPatternBitmap(100, (Bitmap) null);
            if (Theme.getActiveTheme().isDark()) {
                if (this.currentIntensity < 0.0f) {
                    this.backgroundImage.getImageReceiver().setGradientBitmap(drawable.getBitmap());
                }
                SeekBarView seekBarView = this.intensitySeekBar;
                if (seekBarView != null) {
                    seekBarView.setTwoSided(true);
                }
            } else {
                float f2 = this.currentIntensity;
                if (f2 < 0.0f) {
                    this.currentIntensity = -f2;
                }
            }
        }
        SeekBarView seekBarView2 = this.intensitySeekBar;
        if (seekBarView2 != null) {
            seekBarView2.setProgress(this.currentIntensity);
        }
        Theme.resetCustomWallpaper(true);
        selectColorType(2, false);
    }

    /* renamed from: lambda$selectColorType$17$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3948lambda$selectColorType$17$orgtelegramuiThemePreviewActivity(DialogInterface dialog, int which) {
        if (this.accent.backgroundOverrideColor == 4294967296L) {
            this.accent.backgroundOverrideColor = 0;
            this.accent.backgroundGradientOverrideColor1 = 0;
            this.accent.backgroundGradientOverrideColor2 = 0;
            this.accent.backgroundGradientOverrideColor3 = 0;
            updatePlayAnimationView(false);
            Theme.refreshThemeColors();
        }
        this.removeBackgroundOverride = true;
        Theme.resetCustomWallpaper(true);
        selectColorType(2, false);
    }

    private void selectPattern(int position) {
        TLRPC.TL_wallPaper wallPaper;
        if (position < 0 || position >= this.patterns.size()) {
            wallPaper = this.lastSelectedPattern;
        } else {
            wallPaper = (TLRPC.TL_wallPaper) this.patterns.get(position);
        }
        if (wallPaper != null) {
            this.backgroundImage.setImage(ImageLocation.getForDocument(wallPaper.document), this.imageFilter, (ImageLocation) null, (String) null, "jpg", wallPaper.document.size, 1, wallPaper);
            this.selectedPattern = wallPaper;
            this.isMotion = this.backgroundCheckBoxView[2].isChecked();
            updateButtonState(false, true);
        }
    }

    /* access modifiers changed from: private */
    public void saveAccentWallpaper() {
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent != null && !TextUtils.isEmpty(themeAccent.patternSlug)) {
            try {
                File toFile = this.accent.getPathToWallpaper();
                Drawable background = this.backgroundImage.getBackground();
                Bitmap bitmap = this.backgroundImage.getImageReceiver().getBitmap();
                if (background instanceof MotionBackgroundDrawable) {
                    FileOutputStream stream = new FileOutputStream(toFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 87, stream);
                    stream.close();
                    return;
                }
                Bitmap dst = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(dst);
                background.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                background.draw(canvas);
                Paint paint = new Paint(2);
                paint.setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
                paint.setAlpha((int) (this.currentIntensity * 255.0f));
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
                FileOutputStream stream2 = new FileOutputStream(toFile);
                dst.compress(Bitmap.CompressFormat.JPEG, 87, stream2);
                stream2.close();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean hasChanges(int type) {
        long backgroundGradientOverrideColorFull;
        int defaultBackgroundGradient;
        int currentGradient;
        if (this.editingTheme) {
            return false;
        }
        if (type == 1 || type == 2) {
            long j = this.backupBackgroundOverrideColor;
            if (j == 0) {
                int defaultBackground = Theme.getDefaultAccentColor("chat_wallpaper");
                int backgroundOverrideColor = (int) this.accent.backgroundOverrideColor;
                if ((backgroundOverrideColor == 0 ? defaultBackground : backgroundOverrideColor) != defaultBackground) {
                    return true;
                }
            } else if (j != this.accent.backgroundOverrideColor) {
                return true;
            }
            long j2 = this.backupBackgroundGradientOverrideColor1;
            if (j2 == 0 && this.backupBackgroundGradientOverrideColor2 == 0 && this.backupBackgroundGradientOverrideColor3 == 0) {
                for (int a = 0; a < 3; a++) {
                    if (a == 0) {
                        defaultBackgroundGradient = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                        backgroundGradientOverrideColorFull = this.accent.backgroundGradientOverrideColor1;
                    } else if (a == 1) {
                        defaultBackgroundGradient = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to2");
                        backgroundGradientOverrideColorFull = this.accent.backgroundGradientOverrideColor2;
                    } else {
                        defaultBackgroundGradient = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to3");
                        backgroundGradientOverrideColorFull = this.accent.backgroundGradientOverrideColor3;
                    }
                    int backgroundGradientOverrideColor = (int) backgroundGradientOverrideColorFull;
                    if (backgroundGradientOverrideColor != 0 || backgroundGradientOverrideColorFull == 0) {
                        currentGradient = backgroundGradientOverrideColor == 0 ? defaultBackgroundGradient : backgroundGradientOverrideColor;
                    } else {
                        currentGradient = 0;
                    }
                    if (currentGradient != defaultBackgroundGradient) {
                        return true;
                    }
                }
            } else if (!(j2 == this.accent.backgroundGradientOverrideColor1 && this.backupBackgroundGradientOverrideColor2 == this.accent.backgroundGradientOverrideColor2 && this.backupBackgroundGradientOverrideColor3 == this.accent.backgroundGradientOverrideColor3)) {
                return true;
            }
            if (this.accent.backgroundRotation != this.backupBackgroundRotation) {
                return true;
            }
        }
        if (type == 1 || type == 3) {
            if (this.backupAccentColor != this.accent.accentColor2) {
                return true;
            }
            int i = this.backupMyMessagesAccentColor;
            if (i != 0) {
                if (i != this.accent.myMessagesAccentColor) {
                    return true;
                }
            } else if (!(this.accent.myMessagesAccentColor == 0 || this.accent.myMessagesAccentColor == this.accent.accentColor)) {
                return true;
            }
            int i2 = this.backupMyMessagesGradientAccentColor1;
            if (i2 != 0) {
                if (i2 != this.accent.myMessagesGradientAccentColor1) {
                    return true;
                }
            } else if (this.accent.myMessagesGradientAccentColor1 != 0) {
                return true;
            }
            int i3 = this.backupMyMessagesGradientAccentColor2;
            if (i3 != 0) {
                if (i3 != this.accent.myMessagesGradientAccentColor2) {
                    return true;
                }
            } else if (this.accent.myMessagesGradientAccentColor2 != 0) {
                return true;
            }
            int i4 = this.backupMyMessagesGradientAccentColor3;
            if (i4 != 0) {
                if (i4 != this.accent.myMessagesGradientAccentColor3) {
                    return true;
                }
            } else if (this.accent.myMessagesGradientAccentColor3 != 0) {
                return true;
            }
            if (this.backupMyMessagesAnimated != this.accent.myMessagesAnimated) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x009c, code lost:
        if (r6.accent.patternMotion == r6.isMotion) goto L_0x009e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00aa, code lost:
        if (r6.accent.patternIntensity == r6.currentIntensity) goto L_0x00f8;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean checkDiscard() {
        /*
            r6 = this;
            int r0 = r6.screenType
            r1 = 1
            if (r0 != r1) goto L_0x00f8
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            int r0 = r0.accentColor
            int r2 = r6.backupAccentColor
            if (r0 != r2) goto L_0x00ac
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            int r0 = r0.accentColor2
            int r2 = r6.backupAccentColor2
            if (r0 != r2) goto L_0x00ac
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            int r0 = r0.myMessagesAccentColor
            int r2 = r6.backupMyMessagesAccentColor
            if (r0 != r2) goto L_0x00ac
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            int r0 = r0.myMessagesGradientAccentColor1
            int r2 = r6.backupMyMessagesGradientAccentColor1
            if (r0 != r2) goto L_0x00ac
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            int r0 = r0.myMessagesGradientAccentColor2
            int r2 = r6.backupMyMessagesGradientAccentColor2
            if (r0 != r2) goto L_0x00ac
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            int r0 = r0.myMessagesGradientAccentColor3
            int r2 = r6.backupMyMessagesGradientAccentColor3
            if (r0 != r2) goto L_0x00ac
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            boolean r0 = r0.myMessagesAnimated
            boolean r2 = r6.backupMyMessagesAnimated
            if (r0 != r2) goto L_0x00ac
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            long r2 = r0.backgroundOverrideColor
            long r4 = r6.backupBackgroundOverrideColor
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x00ac
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            long r2 = r0.backgroundGradientOverrideColor1
            long r4 = r6.backupBackgroundGradientOverrideColor1
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x00ac
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            long r2 = r0.backgroundGradientOverrideColor2
            long r4 = r6.backupBackgroundGradientOverrideColor2
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x00ac
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            long r2 = r0.backgroundGradientOverrideColor3
            long r4 = r6.backupBackgroundGradientOverrideColor3
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x00ac
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            float r0 = r0.patternIntensity
            float r2 = r6.backupIntensity
            float r0 = r0 - r2
            float r0 = java.lang.Math.abs(r0)
            r2 = 981668463(0x3a83126f, float:0.001)
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 > 0) goto L_0x00ac
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            int r0 = r0.backgroundRotation
            int r2 = r6.backupBackgroundRotation
            if (r0 != r2) goto L_0x00ac
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            java.lang.String r0 = r0.patternSlug
            org.telegram.tgnet.TLRPC$TL_wallPaper r2 = r6.selectedPattern
            if (r2 == 0) goto L_0x008a
            java.lang.String r2 = r2.slug
            goto L_0x008c
        L_0x008a:
            java.lang.String r2 = ""
        L_0x008c:
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x00ac
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = r6.selectedPattern
            if (r0 == 0) goto L_0x009e
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            boolean r0 = r0.patternMotion
            boolean r2 = r6.isMotion
            if (r0 != r2) goto L_0x00ac
        L_0x009e:
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = r6.selectedPattern
            if (r0 == 0) goto L_0x00f8
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r6.accent
            float r0 = r0.patternIntensity
            float r2 = r6.currentIntensity
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x00f8
        L_0x00ac:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r6.getParentActivity()
            r0.<init>((android.content.Context) r1)
            r1 = 2131627594(0x7f0e0e4a, float:1.8882457E38)
            java.lang.String r2 = "SaveChangesAlertTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131627593(0x7f0e0e49, float:1.8882455E38)
            java.lang.String r2 = "SaveChangesAlertText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            r1 = 2131627592(0x7f0e0e48, float:1.8882453E38)
            java.lang.String r2 = "Save"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda0
            r2.<init>(r6)
            r0.setPositiveButton(r1, r2)
            r1 = 2131626890(0x7f0e0b8a, float:1.8881029E38)
            java.lang.String r2 = "PassportDiscard"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda11 r2 = new org.telegram.ui.ThemePreviewActivity$$ExternalSyntheticLambda11
            r2.<init>(r6)
            r0.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r1 = r0.create()
            r6.showDialog(r1)
            r1 = 0
            return r1
        L_0x00f8:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.checkDiscard():boolean");
    }

    /* renamed from: lambda$checkDiscard$18$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3925lambda$checkDiscard$18$orgtelegramuiThemePreviewActivity(DialogInterface dialogInterface, int i) {
        this.actionBar2.getActionBarMenuOnItemClick().onItemClick(4);
    }

    /* renamed from: lambda$checkDiscard$19$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3926lambda$checkDiscard$19$orgtelegramuiThemePreviewActivity(DialogInterface dialog, int which) {
        cancelThemeApply(false);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.invalidateMotionBackground);
        int i = this.screenType;
        if (i == 1 || i == 0) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        }
        int i2 = this.screenType;
        if (i2 == 2 || i2 == 1) {
            Theme.setChangingWallpaper(true);
        }
        if (this.screenType == 0 && this.accent == null) {
            this.isMotion = Theme.isWallpaperMotion();
        } else {
            if (SharedConfig.getDevicePerformanceClass() == 0) {
                int w = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                int h = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                this.imageFilter = ((int) (((float) w) / AndroidUtilities.density)) + "_" + ((int) (((float) h) / AndroidUtilities.density)) + "_f";
            } else {
                this.imageFilter = ((int) (1080.0f / AndroidUtilities.density)) + "_" + ((int) (1920.0f / AndroidUtilities.density)) + "_f";
            }
            this.maxWallpaperSize = Math.min(1920, Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y));
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersNeedReload);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
            this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
            if (this.patterns == null) {
                this.patterns = new ArrayList<>();
                MessagesStorage.getInstance(this.currentAccount).getWallpapers();
            }
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.invalidateMotionBackground);
        FrameLayout frameLayout2 = this.frameLayout;
        if (!(frameLayout2 == null || this.onGlobalLayoutListener == null)) {
            frameLayout2.getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
        }
        int i = this.screenType;
        if (i == 2 || i == 1) {
            AndroidUtilities.runOnUIThread(ThemePreviewActivity$$ExternalSyntheticLambda9.INSTANCE);
        }
        int i2 = this.screenType;
        if (i2 == 2) {
            Bitmap bitmap = this.blurredBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.blurredBitmap = null;
            }
            Theme.applyChatServiceMessageColor();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
        } else if (i2 == 1 || i2 == 0) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        }
        if (!(this.screenType == 0 && this.accent == null)) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersNeedReload);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
        }
        super.onFragmentDestroy();
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        super.onTransitionAnimationStart(isOpen, backward);
        if (!isOpen && this.screenType == 2) {
            Theme.applyChatServiceMessageColor();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewWallpapper, new Object[0]);
        }
    }

    public void onResume() {
        super.onResume();
        DialogsAdapter dialogsAdapter2 = this.dialogsAdapter;
        if (dialogsAdapter2 != null) {
            dialogsAdapter2.notifyDataSetChanged();
        }
        MessagesAdapter messagesAdapter2 = this.messagesAdapter;
        if (messagesAdapter2 != null) {
            messagesAdapter2.notifyDataSetChanged();
        }
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(true);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onPause() {
        super.onPause();
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(false);
        }
    }

    public boolean isSwipeBackEnabled(MotionEvent event) {
        return false;
    }

    public void onFailedDownload(String fileName, boolean canceled) {
        updateButtonState(true, canceled);
    }

    public void onSuccessDownload(String fileName) {
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setProgress(1.0f, this.progressVisible);
        }
        updateButtonState(false, true);
    }

    public void onProgressDownload(String fileName, long downloadedSize, long totalSize) {
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setProgress(Math.min(1.0f, ((float) downloadedSize) / ((float) totalSize)), this.progressVisible);
            if (this.radialProgress.getIcon() != 10) {
                updateButtonState(false, true);
            }
        }
    }

    public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }

    private void updateBlurred() {
        if (this.isBlurred && this.blurredBitmap == null) {
            Bitmap bitmap = this.currentWallpaperBitmap;
            if (bitmap != null) {
                this.originalBitmap = bitmap;
                this.blurredBitmap = Utilities.blurWallpaper(bitmap);
            } else {
                ImageReceiver imageReceiver = this.backgroundImage.getImageReceiver();
                if (imageReceiver.hasNotThumb() || imageReceiver.hasStaticThumb()) {
                    this.originalBitmap = imageReceiver.getBitmap();
                    this.blurredBitmap = Utilities.blurWallpaper(imageReceiver.getBitmap());
                }
            }
        }
        if (this.isBlurred) {
            Bitmap bitmap2 = this.blurredBitmap;
            if (bitmap2 != null) {
                this.backgroundImage.setImageBitmap(bitmap2);
                return;
            }
            return;
        }
        setCurrentImage(false);
    }

    public boolean onBackPressed() {
        if (!checkDiscard()) {
            return false;
        }
        cancelThemeApply(true);
        return true;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        TLRPC.TL_wallPaper tL_wallPaper;
        TLRPC.TL_wallPaper tL_wallPaper2;
        if (id == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                int count = recyclerListView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.listView.getChildAt(a);
                    if (child instanceof DialogCell) {
                        ((DialogCell) child).update(0);
                    }
                }
            }
        } else if (id == NotificationCenter.invalidateMotionBackground) {
            RecyclerListView recyclerListView2 = this.listView2;
            if (recyclerListView2 != null) {
                recyclerListView2.invalidateViews();
            }
        } else if (id == NotificationCenter.didSetNewWallpapper) {
            if (this.page2 != null) {
                setCurrentImage(true);
            }
        } else if (id == NotificationCenter.wallpapersNeedReload) {
            Object obj = this.currentWallpaper;
            if (obj instanceof WallpapersListActivity.FileWallpaper) {
                WallpapersListActivity.FileWallpaper fileWallpaper = (WallpapersListActivity.FileWallpaper) obj;
                if (fileWallpaper.slug == null) {
                    fileWallpaper.slug = args[0];
                }
            }
        } else if (id == NotificationCenter.wallpapersDidLoad) {
            ArrayList<TLRPC.WallPaper> arrayList = args[0];
            this.patterns.clear();
            this.patternsDict.clear();
            boolean added = false;
            int N = arrayList.size();
            for (int a2 = 0; a2 < N; a2++) {
                TLRPC.WallPaper wallPaper = arrayList.get(a2);
                if ((wallPaper instanceof TLRPC.TL_wallPaper) && wallPaper.pattern) {
                    if (wallPaper.document != null && !this.patternsDict.containsKey(Long.valueOf(wallPaper.document.id))) {
                        this.patterns.add(wallPaper);
                        this.patternsDict.put(Long.valueOf(wallPaper.document.id), wallPaper);
                    }
                    Theme.ThemeAccent themeAccent = this.accent;
                    if (themeAccent != null && themeAccent.patternSlug.equals(wallPaper.slug)) {
                        this.selectedPattern = (TLRPC.TL_wallPaper) wallPaper;
                        added = true;
                        setCurrentImage(false);
                        updateButtonState(false, false);
                    } else if (this.accent == null && (tL_wallPaper2 = this.selectedPattern) != null && tL_wallPaper2.slug.equals(wallPaper.slug)) {
                        added = true;
                    }
                }
            }
            if (!added && (tL_wallPaper = this.selectedPattern) != null) {
                this.patterns.add(0, tL_wallPaper);
            }
            PatternsAdapter patternsAdapter2 = this.patternsAdapter;
            if (patternsAdapter2 != null) {
                patternsAdapter2.notifyDataSetChanged();
            }
            long acc = 0;
            int N2 = arrayList.size();
            for (int a3 = 0; a3 < N2; a3++) {
                TLRPC.WallPaper wallPaper2 = arrayList.get(a3);
                if (wallPaper2 instanceof TLRPC.TL_wallPaper) {
                    acc = MediaDataController.calcHash(acc, wallPaper2.id);
                }
            }
            TLRPC.TL_account_getWallPapers req = new TLRPC.TL_account_getWallPapers();
            req.hash = acc;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ThemePreviewActivity$$ExternalSyntheticLambda13(this)), this.classGuid);
        }
    }

    /* renamed from: lambda$didReceivedNotification$24$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3943xd7bvar_d0(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ThemePreviewActivity$$ExternalSyntheticLambda8(this, response));
    }

    /* renamed from: lambda$didReceivedNotification$23$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3942x766cvar_(TLObject response) {
        Theme.ThemeAccent themeAccent;
        TLRPC.TL_wallPaper tL_wallPaper;
        TLRPC.TL_wallPaper tL_wallPaper2;
        if (response instanceof TLRPC.TL_account_wallPapers) {
            TLRPC.TL_account_wallPapers res = (TLRPC.TL_account_wallPapers) response;
            this.patterns.clear();
            this.patternsDict.clear();
            boolean added2 = false;
            int N = res.wallpapers.size();
            for (int a = 0; a < N; a++) {
                if (res.wallpapers.get(a) instanceof TLRPC.TL_wallPaper) {
                    TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) res.wallpapers.get(a);
                    if (wallPaper.pattern) {
                        if (wallPaper.document != null && !this.patternsDict.containsKey(Long.valueOf(wallPaper.document.id))) {
                            this.patterns.add(wallPaper);
                            this.patternsDict.put(Long.valueOf(wallPaper.document.id), wallPaper);
                        }
                        Theme.ThemeAccent themeAccent2 = this.accent;
                        if (themeAccent2 != null && themeAccent2.patternSlug.equals(wallPaper.slug)) {
                            this.selectedPattern = wallPaper;
                            added2 = true;
                            setCurrentImage(false);
                            updateButtonState(false, false);
                        } else if (this.accent == null && (tL_wallPaper2 = this.selectedPattern) != null && tL_wallPaper2.slug.equals(wallPaper.slug)) {
                            added2 = true;
                        }
                    }
                }
            }
            if (!added2 && (tL_wallPaper = this.selectedPattern) != null) {
                this.patterns.add(0, tL_wallPaper);
            }
            PatternsAdapter patternsAdapter2 = this.patternsAdapter;
            if (patternsAdapter2 != null) {
                patternsAdapter2.notifyDataSetChanged();
            }
            MessagesStorage.getInstance(this.currentAccount).putWallpapers(res.wallpapers, 1);
        }
        if (this.selectedPattern == null && (themeAccent = this.accent) != null && !TextUtils.isEmpty(themeAccent.patternSlug)) {
            TLRPC.TL_account_getWallPaper req2 = new TLRPC.TL_account_getWallPaper();
            TLRPC.TL_inputWallPaperSlug inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
            inputWallPaperSlug.slug = this.accent.patternSlug;
            req2.wallpaper = inputWallPaperSlug;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(getConnectionsManager().sendRequest(req2, new ThemePreviewActivity$$ExternalSyntheticLambda12(this)), this.classGuid);
        }
    }

    /* renamed from: lambda$didReceivedNotification$22$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3941x151a5892(TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new ThemePreviewActivity$$ExternalSyntheticLambda7(this, response1));
    }

    /* renamed from: lambda$didReceivedNotification$21$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3940xb3c7bbf3(TLObject response1) {
        if (response1 instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) response1;
            if (wallPaper.pattern) {
                this.selectedPattern = wallPaper;
                setCurrentImage(false);
                updateButtonState(false, false);
                this.patterns.add(0, this.selectedPattern);
                PatternsAdapter patternsAdapter2 = this.patternsAdapter;
                if (patternsAdapter2 != null) {
                    patternsAdapter2.notifyDataSetChanged();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void cancelThemeApply(boolean back) {
        if (this.screenType != 2) {
            Theme.applyPreviousTheme();
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
            if (this.screenType == 1) {
                if (this.editingTheme) {
                    this.accent.accentColor = this.backupAccentColor;
                    this.accent.accentColor2 = this.backupAccentColor2;
                    this.accent.myMessagesAccentColor = this.backupMyMessagesAccentColor;
                    this.accent.myMessagesGradientAccentColor1 = this.backupMyMessagesGradientAccentColor1;
                    this.accent.myMessagesGradientAccentColor2 = this.backupMyMessagesGradientAccentColor2;
                    this.accent.myMessagesGradientAccentColor3 = this.backupMyMessagesGradientAccentColor3;
                    this.accent.myMessagesAnimated = this.backupMyMessagesAnimated;
                    this.accent.backgroundOverrideColor = this.backupBackgroundOverrideColor;
                    this.accent.backgroundGradientOverrideColor1 = this.backupBackgroundGradientOverrideColor1;
                    this.accent.backgroundGradientOverrideColor2 = this.backupBackgroundGradientOverrideColor2;
                    this.accent.backgroundGradientOverrideColor3 = this.backupBackgroundGradientOverrideColor3;
                    this.accent.backgroundRotation = this.backupBackgroundRotation;
                    this.accent.patternSlug = this.backupSlug;
                    this.accent.patternIntensity = this.backupIntensity;
                }
                Theme.saveThemeAccents(this.applyingTheme, false, true, false, false);
            } else {
                if (this.accent != null) {
                    Theme.saveThemeAccents(this.applyingTheme, false, this.deleteOnCancel, false, false);
                }
                this.parentLayout.rebuildAllFragmentViews(false, false);
                if (this.deleteOnCancel && this.applyingTheme.pathToFile != null && !Theme.isThemeInstalled(this.applyingTheme)) {
                    new File(this.applyingTheme.pathToFile).delete();
                }
            }
            if (!back) {
                finishFragment();
            }
        } else if (!back) {
            finishFragment();
        }
    }

    /* access modifiers changed from: private */
    public int getButtonsColor(String key) {
        return this.useDefaultThemeForButtons ? Theme.getDefaultColor(key) : Theme.getColor(key);
    }

    /* access modifiers changed from: private */
    public void scheduleApplyColor(int color, int num, boolean applyNow) {
        int i = num;
        if (i == -1) {
            int i2 = this.colorType;
            if (i2 == 1 || i2 == 2) {
                long j = this.backupBackgroundOverrideColor;
                if (j != 0) {
                    this.accent.backgroundOverrideColor = j;
                } else {
                    this.accent.backgroundOverrideColor = 0;
                }
                long j2 = this.backupBackgroundGradientOverrideColor1;
                if (j2 != 0) {
                    this.accent.backgroundGradientOverrideColor1 = j2;
                } else {
                    this.accent.backgroundGradientOverrideColor1 = 0;
                }
                long j3 = this.backupBackgroundGradientOverrideColor2;
                if (j3 != 0) {
                    this.accent.backgroundGradientOverrideColor2 = j3;
                } else {
                    this.accent.backgroundGradientOverrideColor2 = 0;
                }
                long j4 = this.backupBackgroundGradientOverrideColor3;
                if (j4 != 0) {
                    this.accent.backgroundGradientOverrideColor3 = j4;
                } else {
                    this.accent.backgroundGradientOverrideColor3 = 0;
                }
                this.accent.backgroundRotation = this.backupBackgroundRotation;
                if (this.colorType == 2) {
                    int defaultBackground = Theme.getDefaultAccentColor("chat_wallpaper");
                    int defaultBackgroundGradient1 = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                    int defaultBackgroundGradient2 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to2");
                    int defaultBackgroundGradient3 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to3");
                    int backgroundGradientOverrideColor1 = (int) this.accent.backgroundGradientOverrideColor1;
                    int backgroundGradientOverrideColor2 = (int) this.accent.backgroundGradientOverrideColor2;
                    int backgroundGradientOverrideColor3 = (int) this.accent.backgroundGradientOverrideColor3;
                    int backgroundOverrideColor = (int) this.accent.backgroundOverrideColor;
                    this.colorPicker.setColor(backgroundGradientOverrideColor3 != 0 ? backgroundGradientOverrideColor3 : defaultBackgroundGradient3, 3);
                    this.colorPicker.setColor(backgroundGradientOverrideColor2 != 0 ? backgroundGradientOverrideColor2 : defaultBackgroundGradient2, 2);
                    this.colorPicker.setColor(backgroundGradientOverrideColor1 != 0 ? backgroundGradientOverrideColor1 : defaultBackgroundGradient1, 1);
                    this.colorPicker.setColor(backgroundOverrideColor != 0 ? backgroundOverrideColor : defaultBackground, 0);
                }
            }
            int defaultBackground2 = this.colorType;
            if (defaultBackground2 == 1 || defaultBackground2 == 3) {
                int i3 = this.backupMyMessagesAccentColor;
                if (i3 != 0) {
                    this.accent.myMessagesAccentColor = i3;
                } else {
                    this.accent.myMessagesAccentColor = 0;
                }
                int i4 = this.backupMyMessagesGradientAccentColor1;
                if (i4 != 0) {
                    this.accent.myMessagesGradientAccentColor1 = i4;
                } else {
                    this.accent.myMessagesGradientAccentColor1 = 0;
                }
                int i5 = this.backupMyMessagesGradientAccentColor2;
                if (i5 != 0) {
                    this.accent.myMessagesGradientAccentColor2 = i5;
                } else {
                    this.accent.myMessagesGradientAccentColor2 = 0;
                }
                int i6 = this.backupMyMessagesGradientAccentColor3;
                if (i6 != 0) {
                    this.accent.myMessagesGradientAccentColor3 = i6;
                } else {
                    this.accent.myMessagesGradientAccentColor3 = 0;
                }
                if (this.colorType == 3) {
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor3, 3);
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor2, 2);
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor1, 1);
                    this.colorPicker.setColor(this.accent.myMessagesAccentColor != 0 ? this.accent.myMessagesAccentColor : this.accent.accentColor, 0);
                }
            }
            Theme.refreshThemeColors();
            this.listView2.invalidateViews();
            return;
        }
        int i7 = this.lastPickedColorNum;
        if (!(i7 == -1 || i7 == i)) {
            this.applyColorAction.run();
        }
        this.lastPickedColor = color;
        this.lastPickedColorNum = i;
        if (applyNow) {
            this.applyColorAction.run();
        } else if (!this.applyColorScheduled) {
            this.applyColorScheduled = true;
            this.fragmentView.postDelayed(this.applyColorAction, 16);
        }
    }

    private void applyColor(int color, int num) {
        int i = this.colorType;
        if (i == 1) {
            if (num == 0) {
                this.accent.accentColor = color;
                Theme.refreshThemeColors();
            } else if (num == 1) {
                this.accent.accentColor2 = color;
                Theme.refreshThemeColors(true, true);
                this.listView2.invalidateViews();
                this.colorPicker.setHasChanges(hasChanges(this.colorType));
                updatePlayAnimationView(true);
            }
        } else if (i == 2) {
            if (this.lastPickedColorNum == 0) {
                this.accent.backgroundOverrideColor = (long) color;
            } else if (num == 1) {
                int defaultGradientColor = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                if (color != 0 || defaultGradientColor == 0) {
                    this.accent.backgroundGradientOverrideColor1 = (long) color;
                } else {
                    this.accent.backgroundGradientOverrideColor1 = 4294967296L;
                }
            } else if (num == 2) {
                int defaultGradientColor2 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to2");
                if (color != 0 || defaultGradientColor2 == 0) {
                    this.accent.backgroundGradientOverrideColor2 = (long) color;
                } else {
                    this.accent.backgroundGradientOverrideColor2 = 4294967296L;
                }
            } else if (num == 3) {
                int defaultGradientColor3 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to3");
                if (color != 0 || defaultGradientColor3 == 0) {
                    this.accent.backgroundGradientOverrideColor3 = (long) color;
                } else {
                    this.accent.backgroundGradientOverrideColor3 = 4294967296L;
                }
            }
            Theme.refreshThemeColors(true, false);
            this.colorPicker.setHasChanges(hasChanges(this.colorType));
            updatePlayAnimationView(true);
        } else if (i == 3) {
            int i2 = this.lastPickedColorNum;
            if (i2 == 0) {
                this.accent.myMessagesAccentColor = color;
            } else if (i2 == 1) {
                this.accent.myMessagesGradientAccentColor1 = color;
            } else if (i2 == 2) {
                int prevColor = this.accent.myMessagesGradientAccentColor2;
                this.accent.myMessagesGradientAccentColor2 = color;
                if (prevColor != 0 && color == 0) {
                    this.messagesAdapter.notifyItemRemoved(0);
                } else if (prevColor == 0 && color != 0) {
                    this.messagesAdapter.notifyItemInserted(0);
                    showAnimationHint();
                }
            } else {
                this.accent.myMessagesGradientAccentColor3 = color;
            }
            int i3 = this.lastPickedColorNum;
            if (i3 >= 0) {
                this.messagesCheckBoxView[1].setColor(i3, color);
            }
            Theme.refreshThemeColors(true, true);
            this.listView2.invalidateViews();
            this.colorPicker.setHasChanges(hasChanges(this.colorType));
            updatePlayAnimationView(true);
        }
        int size = this.themeDescriptions.size();
        for (int i4 = 0; i4 < size; i4++) {
            ThemeDescription description = this.themeDescriptions.get(i4);
            description.setColor(Theme.getColor(description.getCurrentKey()), false, false);
        }
        this.listView.invalidateViews();
        this.listView2.invalidateViews();
        View view = this.dotsContainer;
        if (view != null) {
            view.invalidate();
        }
    }

    private void updateButtonState(boolean ifSame, boolean animated) {
        Object object;
        String fileName;
        File path;
        int size;
        FrameLayout frameLayout2;
        String fileName2;
        int size2;
        if (this.selectedPattern != null) {
            object = this.selectedPattern;
        } else {
            object = this.currentWallpaper;
        }
        if ((object instanceof TLRPC.TL_wallPaper) || (object instanceof MediaController.SearchImage)) {
            if (animated && !this.progressVisible) {
                animated = false;
            }
            if (object instanceof TLRPC.TL_wallPaper) {
                TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) object;
                fileName = FileLoader.getAttachFileName(wallPaper.document);
                if (!TextUtils.isEmpty(fileName)) {
                    path = FileLoader.getPathToAttach(wallPaper.document, true);
                    size = wallPaper.document.size;
                } else {
                    return;
                }
            } else {
                MediaController.SearchImage wallPaper2 = (MediaController.SearchImage) object;
                if (wallPaper2.photo != null) {
                    TLRPC.PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(wallPaper2.photo.sizes, this.maxWallpaperSize, true);
                    path = FileLoader.getPathToAttach(photoSize, true);
                    fileName2 = FileLoader.getAttachFileName(photoSize);
                    size2 = photoSize.size;
                } else {
                    path = ImageLoader.getHttpFilePath(wallPaper2.imageUrl, "jpg");
                    fileName2 = path.getName();
                    size2 = wallPaper2.size;
                }
                if (!TextUtils.isEmpty(fileName2)) {
                    size = size2;
                    fileName = fileName2;
                } else {
                    return;
                }
            }
            boolean exists = path.exists();
            boolean fileExists = exists;
            float f = 1.0f;
            if (exists) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                RadialProgress2 radialProgress2 = this.radialProgress;
                if (radialProgress2 != null) {
                    radialProgress2.setProgress(1.0f, animated);
                    this.radialProgress.setIcon(4, ifSame, animated);
                }
                this.backgroundImage.invalidate();
                if (this.screenType == 2) {
                    if (size != 0) {
                        this.actionBar2.setSubtitle(AndroidUtilities.formatFileSize((long) size));
                    } else {
                        this.actionBar2.setSubtitle((CharSequence) null);
                    }
                }
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, (MessageObject) null, this);
                if (this.radialProgress != null) {
                    boolean isLoadingFile = FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName);
                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress != null) {
                        this.radialProgress.setProgress(progress.floatValue(), animated);
                    } else {
                        this.radialProgress.setProgress(0.0f, animated);
                    }
                    this.radialProgress.setIcon(10, ifSame, animated);
                }
                if (this.screenType == 2) {
                    this.actionBar2.setSubtitle(LocaleController.getString("LoadingFullImage", NUM));
                }
                this.backgroundImage.invalidate();
            }
            if (this.selectedPattern == null && (frameLayout2 = this.backgroundButtonsContainer) != null) {
                frameLayout2.setAlpha(fileExists ? 1.0f : 0.5f);
            }
            int i = this.screenType;
            if (i == 0) {
                this.doneButton.setEnabled(fileExists);
                TextView textView = this.doneButton;
                if (!fileExists) {
                    f = 0.5f;
                }
                textView.setAlpha(f);
            } else if (i == 2) {
                this.bottomOverlayChat.setEnabled(fileExists);
                TextView textView2 = this.bottomOverlayChatText;
                if (!fileExists) {
                    f = 0.5f;
                }
                textView2.setAlpha(f);
            } else {
                this.saveItem.setEnabled(fileExists);
                ActionBarMenuItem actionBarMenuItem = this.saveItem;
                if (!fileExists) {
                    f = 0.5f;
                }
                actionBarMenuItem.setAlpha(f);
            }
        } else {
            RadialProgress2 radialProgress22 = this.radialProgress;
            if (radialProgress22 != null) {
                radialProgress22.setIcon(4, ifSame, animated);
            }
        }
    }

    public void setDelegate(WallpaperActivityDelegate wallpaperActivityDelegate) {
        this.delegate = wallpaperActivityDelegate;
    }

    public void setPatterns(ArrayList<Object> arrayList) {
        this.patterns = arrayList;
        if (this.screenType == 1 || (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
            WallpapersListActivity.ColorWallpaper wallPaper = (WallpapersListActivity.ColorWallpaper) this.currentWallpaper;
            if (wallPaper.patternId != 0) {
                int a = 0;
                int N = this.patterns.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    TLRPC.TL_wallPaper pattern = (TLRPC.TL_wallPaper) this.patterns.get(a);
                    if (pattern.id == wallPaper.patternId) {
                        this.selectedPattern = pattern;
                        break;
                    }
                    a++;
                }
                this.currentIntensity = wallPaper.intensity;
            }
        }
    }

    private void showAnimationHint() {
        if (this.page2 != null && this.messagesCheckBoxView != null && this.accent.myMessagesGradientAccentColor2 != 0) {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            if (!preferences.getBoolean("bganimationhint", false)) {
                if (this.animationHint == null) {
                    HintView hintView = new HintView(getParentActivity(), 8);
                    this.animationHint = hintView;
                    hintView.setShowingDuration(5000);
                    this.animationHint.setAlpha(0.0f);
                    this.animationHint.setVisibility(4);
                    this.animationHint.setText(LocaleController.getString("BackgroundAnimateInfo", NUM));
                    this.animationHint.setExtraTranslationY((float) AndroidUtilities.dp(6.0f));
                    this.frameLayout.addView(this.animationHint, LayoutHelper.createFrame(-2, -2.0f, 51, 10.0f, 0.0f, 10.0f, 0.0f));
                }
                AndroidUtilities.runOnUIThread(new ThemePreviewActivity$$ExternalSyntheticLambda6(this, preferences), 500);
            }
        }
    }

    /* renamed from: lambda$showAnimationHint$25$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3949lambda$showAnimationHint$25$orgtelegramuiThemePreviewActivity(SharedPreferences preferences) {
        if (this.colorType == 3) {
            preferences.edit().putBoolean("bganimationhint", true).commit();
            this.animationHint.showForView(this.messagesCheckBoxView[0], true);
        }
    }

    private void updateSelectedPattern(boolean animated) {
        int count = this.patternsListView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.patternsListView.getChildAt(a);
            if (child instanceof PatternCell) {
                ((PatternCell) child).updateSelected(animated);
            }
        }
    }

    private void updateMotionButton() {
        int i = this.screenType;
        float f = 1.0f;
        float f2 = 0.0f;
        if (i == 1 || i == 2) {
            if (this.selectedPattern == null && (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
                this.backgroundCheckBoxView[2].setChecked(false, true);
            }
            this.backgroundCheckBoxView[this.selectedPattern != null ? (char) 2 : 0].setVisibility(0);
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[2];
            WallpaperCheckBoxView wallpaperCheckBoxView = this.backgroundCheckBoxView[2];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = this.selectedPattern != null ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView, property, fArr);
            WallpaperCheckBoxView wallpaperCheckBoxView2 = this.backgroundCheckBoxView[0];
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (this.selectedPattern != null) {
                f = 0.0f;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(wallpaperCheckBoxView2, property2, fArr2);
            animatorSet.playTogether(animatorArr);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ThemePreviewActivity.this.backgroundCheckBoxView[ThemePreviewActivity.this.selectedPattern != null ? (char) 0 : 2].setVisibility(4);
                }
            });
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet.setDuration(200);
            animatorSet.start();
            return;
        }
        boolean isEnabled = this.backgroundCheckBoxView[0].isEnabled();
        TLRPC.TL_wallPaper tL_wallPaper = this.selectedPattern;
        if (isEnabled != (tL_wallPaper != null)) {
            if (tL_wallPaper == null) {
                this.backgroundCheckBoxView[0].setChecked(false, true);
            }
            this.backgroundCheckBoxView[0].setEnabled(this.selectedPattern != null);
            if (this.selectedPattern != null) {
                this.backgroundCheckBoxView[0].setVisibility(0);
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            int offset = (((FrameLayout.LayoutParams) this.backgroundCheckBoxView[1].getLayoutParams()).width + AndroidUtilities.dp(9.0f)) / 2;
            Animator[] animatorArr2 = new Animator[1];
            WallpaperCheckBoxView wallpaperCheckBoxView3 = this.backgroundCheckBoxView[0];
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            if (this.selectedPattern == null) {
                f = 0.0f;
            }
            fArr3[0] = f;
            animatorArr2[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView3, property3, fArr3);
            animatorSet2.playTogether(animatorArr2);
            Animator[] animatorArr3 = new Animator[1];
            WallpaperCheckBoxView wallpaperCheckBoxView4 = this.backgroundCheckBoxView[0];
            Property property4 = View.TRANSLATION_X;
            float[] fArr4 = new float[1];
            fArr4[0] = this.selectedPattern != null ? 0.0f : (float) offset;
            animatorArr3[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView4, property4, fArr4);
            animatorSet2.playTogether(animatorArr3);
            Animator[] animatorArr4 = new Animator[1];
            WallpaperCheckBoxView wallpaperCheckBoxView5 = this.backgroundCheckBoxView[1];
            Property property5 = View.TRANSLATION_X;
            float[] fArr5 = new float[1];
            if (this.selectedPattern == null) {
                f2 = (float) (-offset);
            }
            fArr5[0] = f2;
            animatorArr4[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView5, property5, fArr5);
            animatorSet2.playTogether(animatorArr4);
            animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet2.setDuration(200);
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ThemePreviewActivity.this.selectedPattern == null) {
                        ThemePreviewActivity.this.backgroundCheckBoxView[0].setVisibility(4);
                    }
                }
            });
            animatorSet2.start();
        }
    }

    /* access modifiers changed from: private */
    public void showPatternsView(int num, boolean show, boolean animated) {
        int index;
        int count;
        int i = num;
        char c = 0;
        boolean showMotion = show && i == 1 && this.selectedPattern != null;
        if (show) {
            if (i != 0) {
                this.previousSelectedPattern = this.selectedPattern;
                this.previousIntensity = this.currentIntensity;
                this.patternsAdapter.notifyDataSetChanged();
                ArrayList<Object> arrayList = this.patterns;
                if (arrayList != null) {
                    TLRPC.TL_wallPaper tL_wallPaper = this.selectedPattern;
                    if (tL_wallPaper == null) {
                        index = 0;
                    } else {
                        index = arrayList.indexOf(tL_wallPaper) + (this.screenType == 2 ? 1 : 0);
                    }
                    this.patternsLayoutManager.scrollToPositionWithOffset(index, (this.patternsListView.getMeasuredWidth() - AndroidUtilities.dp(124.0f)) / 2);
                }
            } else if (this.screenType == 2) {
                this.previousBackgroundColor = this.backgroundColor;
                int count2 = this.backgroundGradientColor1;
                this.previousBackgroundGradientColor1 = count2;
                int i2 = this.backgroundGradientColor2;
                this.previousBackgroundGradientColor2 = i2;
                int i3 = this.backgroundGradientColor3;
                this.previousBackgroundGradientColor3 = i3;
                int i4 = this.backupBackgroundRotation;
                this.previousBackgroundRotation = i4;
                if (i3 != 0) {
                    count = 4;
                } else if (i2 != 0) {
                    count = 3;
                } else if (count2 != 0) {
                    count = 2;
                } else {
                    count = 1;
                }
                this.colorPicker.setType(0, false, 4, count, false, i4, false);
                this.colorPicker.setColor(this.backgroundGradientColor3, 3);
                this.colorPicker.setColor(this.backgroundGradientColor2, 2);
                this.colorPicker.setColor(this.backgroundGradientColor1, 1);
                this.colorPicker.setColor(this.backgroundColor, 0);
            }
        }
        int index2 = this.screenType;
        if (index2 == 1 || index2 == 2) {
            this.backgroundCheckBoxView[showMotion ? (char) 2 : 0].setVisibility(0);
        }
        if (i == 1 && !this.intensitySeekBar.isTwoSided()) {
            float f = this.currentIntensity;
            if (f < 0.0f) {
                float f2 = -f;
                this.currentIntensity = f2;
                this.intensitySeekBar.setProgress(f2);
            }
        }
        float f3 = 1.0f;
        if (animated) {
            this.patternViewAnimation = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            int otherNum = i == 0 ? 1 : 0;
            if (show) {
                this.patternLayout[i].setVisibility(0);
                int i5 = this.screenType;
                if (i5 == 1) {
                    RecyclerListView recyclerListView = this.listView2;
                    Property property = View.TRANSLATION_Y;
                    float[] fArr = new float[1];
                    fArr[0] = i == 1 ? (float) (-AndroidUtilities.dp(21.0f)) : 0.0f;
                    animators.add(ObjectAnimator.ofFloat(recyclerListView, property, fArr));
                    WallpaperCheckBoxView wallpaperCheckBoxView = this.backgroundCheckBoxView[2];
                    Property property2 = View.ALPHA;
                    float[] fArr2 = new float[1];
                    fArr2[0] = showMotion ? 1.0f : 0.0f;
                    animators.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView, property2, fArr2));
                    WallpaperCheckBoxView wallpaperCheckBoxView2 = this.backgroundCheckBoxView[0];
                    Property property3 = View.ALPHA;
                    float[] fArr3 = new float[1];
                    fArr3[0] = showMotion ? 0.0f : 1.0f;
                    animators.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView2, property3, fArr3));
                    if (i == 1) {
                        animators.add(ObjectAnimator.ofFloat(this.patternLayout[i], View.ALPHA, new float[]{0.0f, 1.0f}));
                    } else {
                        this.patternLayout[i].setAlpha(1.0f);
                        animators.add(ObjectAnimator.ofFloat(this.patternLayout[otherNum], View.ALPHA, new float[]{0.0f}));
                    }
                    this.colorPicker.hideKeyboard();
                } else if (i5 == 2) {
                    animators.add(ObjectAnimator.ofFloat(this.listView2, View.TRANSLATION_Y, new float[]{(float) ((-this.patternLayout[i].getMeasuredHeight()) + AndroidUtilities.dp(48.0f))}));
                    WallpaperCheckBoxView wallpaperCheckBoxView3 = this.backgroundCheckBoxView[2];
                    Property property4 = View.ALPHA;
                    float[] fArr4 = new float[1];
                    fArr4[0] = showMotion ? 1.0f : 0.0f;
                    animators.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView3, property4, fArr4));
                    WallpaperCheckBoxView wallpaperCheckBoxView4 = this.backgroundCheckBoxView[0];
                    Property property5 = View.ALPHA;
                    float[] fArr5 = new float[1];
                    if (showMotion) {
                        f3 = 0.0f;
                    }
                    fArr5[0] = f3;
                    animators.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView4, property5, fArr5));
                    animators.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, new float[]{0.0f}));
                    if (this.patternLayout[otherNum].getVisibility() == 0) {
                        animators.add(ObjectAnimator.ofFloat(this.patternLayout[otherNum], View.ALPHA, new float[]{0.0f}));
                        animators.add(ObjectAnimator.ofFloat(this.patternLayout[i], View.ALPHA, new float[]{0.0f, 1.0f}));
                        this.patternLayout[i].setTranslationY(0.0f);
                    } else {
                        animators.add(ObjectAnimator.ofFloat(this.patternLayout[i], View.TRANSLATION_Y, new float[]{(float) this.patternLayout[i].getMeasuredHeight(), 0.0f}));
                    }
                } else {
                    if (i == 1) {
                        animators.add(ObjectAnimator.ofFloat(this.patternLayout[i], View.ALPHA, new float[]{0.0f, 1.0f}));
                    } else {
                        this.patternLayout[i].setAlpha(1.0f);
                        animators.add(ObjectAnimator.ofFloat(this.patternLayout[otherNum], View.ALPHA, new float[]{0.0f}));
                    }
                    this.colorPicker.hideKeyboard();
                }
            } else {
                animators.add(ObjectAnimator.ofFloat(this.listView2, View.TRANSLATION_Y, new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.patternLayout[i], View.TRANSLATION_Y, new float[]{(float) this.patternLayout[i].getMeasuredHeight()}));
                animators.add(ObjectAnimator.ofFloat(this.backgroundCheckBoxView[0], View.ALPHA, new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.backgroundCheckBoxView[2], View.ALPHA, new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, new float[]{1.0f}));
            }
            this.patternViewAnimation.playTogether(animators);
            final boolean z = show;
            final int i6 = otherNum;
            final int i7 = num;
            final boolean z2 = showMotion;
            this.patternViewAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = ThemePreviewActivity.this.patternViewAnimation = null;
                    if (z && ThemePreviewActivity.this.patternLayout[i6].getVisibility() == 0) {
                        ThemePreviewActivity.this.patternLayout[i6].setAlpha(1.0f);
                        ThemePreviewActivity.this.patternLayout[i6].setVisibility(4);
                    } else if (!z) {
                        ThemePreviewActivity.this.patternLayout[i7].setVisibility(4);
                    }
                    char c = 2;
                    if (ThemePreviewActivity.this.screenType == 1 || ThemePreviewActivity.this.screenType == 2) {
                        WallpaperCheckBoxView[] access$4600 = ThemePreviewActivity.this.backgroundCheckBoxView;
                        if (z2) {
                            c = 0;
                        }
                        access$4600[c].setVisibility(4);
                    } else if (i7 == 1) {
                        ThemePreviewActivity.this.patternLayout[i6].setAlpha(0.0f);
                    }
                }
            });
            this.patternViewAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.patternViewAnimation.setDuration(200);
            this.patternViewAnimation.start();
            return;
        }
        int otherNum2 = i == 0 ? 1 : 0;
        if (show) {
            this.patternLayout[i].setVisibility(0);
            int i8 = this.screenType;
            if (i8 == 1) {
                this.listView2.setTranslationY(i == 1 ? (float) (-AndroidUtilities.dp(21.0f)) : 0.0f);
                this.backgroundCheckBoxView[2].setAlpha(showMotion ? 1.0f : 0.0f);
                this.backgroundCheckBoxView[0].setAlpha(showMotion ? 0.0f : 1.0f);
                if (i == 1) {
                    this.patternLayout[i].setAlpha(1.0f);
                } else {
                    this.patternLayout[i].setAlpha(1.0f);
                    this.patternLayout[otherNum2].setAlpha(0.0f);
                }
                this.colorPicker.hideKeyboard();
            } else if (i8 == 2) {
                this.listView2.setTranslationY((float) ((-AndroidUtilities.dp(i == 0 ? 343.0f : 316.0f)) + AndroidUtilities.dp(48.0f)));
                this.backgroundCheckBoxView[2].setAlpha(showMotion ? 1.0f : 0.0f);
                this.backgroundCheckBoxView[0].setAlpha(showMotion ? 0.0f : 1.0f);
                this.backgroundImage.setAlpha(0.0f);
                if (this.patternLayout[otherNum2].getVisibility() == 0) {
                    this.patternLayout[otherNum2].setAlpha(0.0f);
                    this.patternLayout[i].setAlpha(1.0f);
                    this.patternLayout[i].setTranslationY(0.0f);
                } else {
                    this.patternLayout[i].setTranslationY(0.0f);
                }
            } else {
                if (i == 1) {
                    this.patternLayout[i].setAlpha(1.0f);
                } else {
                    this.patternLayout[i].setAlpha(1.0f);
                    this.patternLayout[otherNum2].setAlpha(0.0f);
                }
                this.colorPicker.hideKeyboard();
            }
        } else {
            this.listView2.setTranslationY(0.0f);
            FrameLayout[] frameLayoutArr = this.patternLayout;
            frameLayoutArr[i].setTranslationY((float) frameLayoutArr[i].getMeasuredHeight());
            this.backgroundCheckBoxView[0].setAlpha(1.0f);
            this.backgroundCheckBoxView[2].setAlpha(1.0f);
            this.backgroundImage.setAlpha(1.0f);
        }
        if (show && this.patternLayout[otherNum2].getVisibility() == 0) {
            this.patternLayout[otherNum2].setAlpha(1.0f);
            this.patternLayout[otherNum2].setVisibility(4);
        } else if (!show) {
            this.patternLayout[i].setVisibility(4);
        }
        int i9 = this.screenType;
        if (i9 == 1 || i9 == 2) {
            WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.backgroundCheckBoxView;
            if (!showMotion) {
                c = 2;
            }
            wallpaperCheckBoxViewArr[c].setVisibility(4);
        } else if (i == 1) {
            this.patternLayout[otherNum2].setAlpha(0.0f);
        }
    }

    private void animateMotionChange() {
        AnimatorSet animatorSet = this.motionAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.motionAnimation = animatorSet2;
        if (this.isMotion) {
            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, new float[]{this.parallaxScale}), ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, new float[]{this.parallaxScale})});
        } else {
            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_Y, new float[]{0.0f})});
        }
        this.motionAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.motionAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                AnimatorSet unused = ThemePreviewActivity.this.motionAnimation = null;
            }
        });
        this.motionAnimation.start();
    }

    private void updatePlayAnimationView(boolean animated) {
        boolean visible;
        int color1;
        int i = 1;
        if (Build.VERSION.SDK_INT >= 29) {
            int color2 = 0;
            int i2 = this.screenType;
            if (i2 == 0) {
                Theme.ThemeAccent themeAccent = this.accent;
                if (themeAccent != null) {
                    color2 = (int) themeAccent.backgroundGradientOverrideColor2;
                } else {
                    color2 = Theme.getColor("key_chat_wallpaper_gradient_to2");
                }
            } else if (i2 == 1) {
                int defaultBackgroundGradient2 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to2");
                int backgroundGradientOverrideColor2 = (int) this.accent.backgroundGradientOverrideColor2;
                if (backgroundGradientOverrideColor2 != 0 || this.accent.backgroundGradientOverrideColor2 == 0) {
                    color2 = backgroundGradientOverrideColor2 != 0 ? backgroundGradientOverrideColor2 : defaultBackgroundGradient2;
                } else {
                    color2 = 0;
                }
            } else {
                Object obj = this.currentWallpaper;
                if (obj instanceof WallpapersListActivity.ColorWallpaper) {
                    WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) obj;
                    color2 = this.backgroundGradientColor2;
                }
            }
            if (color2 == 0 || this.currentIntensity < 0.0f) {
                this.backgroundImage.getImageReceiver().setBlendMode((Object) null);
            } else {
                this.backgroundImage.getImageReceiver().setBlendMode(BlendMode.SOFT_LIGHT);
            }
        }
        if (this.backgroundPlayAnimationView != null) {
            int i3 = this.screenType;
            if (i3 == 2) {
                visible = this.backgroundGradientColor1 != 0;
            } else if (i3 == 1) {
                int defaultBackgroundGradient1 = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                int backgroundGradientOverrideColor1 = (int) this.accent.backgroundGradientOverrideColor1;
                if (backgroundGradientOverrideColor1 != 0 || this.accent.backgroundGradientOverrideColor1 == 0) {
                    color1 = backgroundGradientOverrideColor1 != 0 ? backgroundGradientOverrideColor1 : defaultBackgroundGradient1;
                } else {
                    color1 = 0;
                }
                visible = color1 != 0;
            } else {
                visible = false;
            }
            boolean wasVisible = this.backgroundPlayAnimationView.getTag() != null;
            this.backgroundPlayAnimationView.setTag(visible ? 1 : null);
            if (wasVisible != visible) {
                if (visible) {
                    this.backgroundPlayAnimationView.setVisibility(0);
                }
                AnimatorSet animatorSet = this.backgroundPlayViewAnimator;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                if (animated) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.backgroundPlayViewAnimator = animatorSet2;
                    Animator[] animatorArr = new Animator[6];
                    FrameLayout frameLayout2 = this.backgroundPlayAnimationView;
                    Property property = View.ALPHA;
                    float[] fArr = new float[1];
                    fArr[0] = visible ? 1.0f : 0.0f;
                    animatorArr[0] = ObjectAnimator.ofFloat(frameLayout2, property, fArr);
                    FrameLayout frameLayout3 = this.backgroundPlayAnimationView;
                    Property property2 = View.SCALE_X;
                    float[] fArr2 = new float[1];
                    fArr2[0] = visible ? 1.0f : 0.0f;
                    animatorArr[1] = ObjectAnimator.ofFloat(frameLayout3, property2, fArr2);
                    FrameLayout frameLayout4 = this.backgroundPlayAnimationView;
                    Property property3 = View.SCALE_Y;
                    float[] fArr3 = new float[1];
                    fArr3[0] = visible ? 1.0f : 0.0f;
                    animatorArr[2] = ObjectAnimator.ofFloat(frameLayout4, property3, fArr3);
                    WallpaperCheckBoxView wallpaperCheckBoxView = this.backgroundCheckBoxView[0];
                    Property property4 = View.TRANSLATION_X;
                    float[] fArr4 = new float[1];
                    fArr4[0] = visible ? (float) AndroidUtilities.dp(34.0f) : 0.0f;
                    animatorArr[3] = ObjectAnimator.ofFloat(wallpaperCheckBoxView, property4, fArr4);
                    WallpaperCheckBoxView wallpaperCheckBoxView2 = this.backgroundCheckBoxView[1];
                    Property property5 = View.TRANSLATION_X;
                    float[] fArr5 = new float[1];
                    fArr5[0] = visible ? (float) (-AndroidUtilities.dp(34.0f)) : 0.0f;
                    animatorArr[4] = ObjectAnimator.ofFloat(wallpaperCheckBoxView2, property5, fArr5);
                    WallpaperCheckBoxView wallpaperCheckBoxView3 = this.backgroundCheckBoxView[2];
                    Property property6 = View.TRANSLATION_X;
                    float[] fArr6 = new float[1];
                    fArr6[0] = visible ? (float) AndroidUtilities.dp(34.0f) : 0.0f;
                    animatorArr[5] = ObjectAnimator.ofFloat(wallpaperCheckBoxView3, property6, fArr6);
                    animatorSet2.playTogether(animatorArr);
                    this.backgroundPlayViewAnimator.setDuration(180);
                    this.backgroundPlayViewAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (ThemePreviewActivity.this.backgroundPlayAnimationView.getTag() == null) {
                                ThemePreviewActivity.this.backgroundPlayAnimationView.setVisibility(4);
                            }
                            AnimatorSet unused = ThemePreviewActivity.this.backgroundPlayViewAnimator = null;
                        }
                    });
                    this.backgroundPlayViewAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.backgroundPlayViewAnimator.start();
                } else {
                    this.backgroundPlayAnimationView.setAlpha(visible ? 1.0f : 0.0f);
                    this.backgroundPlayAnimationView.setScaleX(visible ? 1.0f : 0.0f);
                    this.backgroundPlayAnimationView.setScaleY(visible ? 1.0f : 0.0f);
                    this.backgroundCheckBoxView[0].setTranslationX(visible ? (float) AndroidUtilities.dp(34.0f) : 0.0f);
                    this.backgroundCheckBoxView[1].setTranslationX(visible ? (float) (-AndroidUtilities.dp(34.0f)) : 0.0f);
                    this.backgroundCheckBoxView[2].setTranslationX(visible ? (float) AndroidUtilities.dp(34.0f) : 0.0f);
                }
            }
        }
        FrameLayout frameLayout5 = this.messagesPlayAnimationView;
        if (frameLayout5 != null) {
            boolean wasVisible2 = frameLayout5.getTag() != null;
            FrameLayout frameLayout6 = this.messagesPlayAnimationView;
            if (1 == 0) {
                i = null;
            }
            frameLayout6.setTag(i);
            if (!wasVisible2) {
                if (1 != 0) {
                    this.messagesPlayAnimationView.setVisibility(0);
                }
                AnimatorSet animatorSet3 = this.messagesPlayViewAnimator;
                if (animatorSet3 != null) {
                    animatorSet3.cancel();
                }
                if (animated) {
                    AnimatorSet animatorSet4 = new AnimatorSet();
                    this.messagesPlayViewAnimator = animatorSet4;
                    Animator[] animatorArr2 = new Animator[5];
                    FrameLayout frameLayout7 = this.messagesPlayAnimationView;
                    Property property7 = View.ALPHA;
                    float[] fArr7 = new float[1];
                    fArr7[0] = 1 != 0 ? 1.0f : 0.0f;
                    animatorArr2[0] = ObjectAnimator.ofFloat(frameLayout7, property7, fArr7);
                    FrameLayout frameLayout8 = this.messagesPlayAnimationView;
                    Property property8 = View.SCALE_X;
                    float[] fArr8 = new float[1];
                    fArr8[0] = 1 != 0 ? 1.0f : 0.0f;
                    animatorArr2[1] = ObjectAnimator.ofFloat(frameLayout8, property8, fArr8);
                    FrameLayout frameLayout9 = this.messagesPlayAnimationView;
                    Property property9 = View.SCALE_Y;
                    float[] fArr9 = new float[1];
                    fArr9[0] = 1 != 0 ? 1.0f : 0.0f;
                    animatorArr2[2] = ObjectAnimator.ofFloat(frameLayout9, property9, fArr9);
                    WallpaperCheckBoxView wallpaperCheckBoxView4 = this.messagesCheckBoxView[0];
                    Property property10 = View.TRANSLATION_X;
                    float[] fArr10 = new float[1];
                    fArr10[0] = 1 != 0 ? (float) (-AndroidUtilities.dp(34.0f)) : 0.0f;
                    animatorArr2[3] = ObjectAnimator.ofFloat(wallpaperCheckBoxView4, property10, fArr10);
                    WallpaperCheckBoxView wallpaperCheckBoxView5 = this.messagesCheckBoxView[1];
                    Property property11 = View.TRANSLATION_X;
                    float[] fArr11 = new float[1];
                    fArr11[0] = 1 != 0 ? (float) AndroidUtilities.dp(34.0f) : 0.0f;
                    animatorArr2[4] = ObjectAnimator.ofFloat(wallpaperCheckBoxView5, property11, fArr11);
                    animatorSet4.playTogether(animatorArr2);
                    this.messagesPlayViewAnimator.setDuration(180);
                    this.messagesPlayViewAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (ThemePreviewActivity.this.messagesPlayAnimationView.getTag() == null) {
                                ThemePreviewActivity.this.messagesPlayAnimationView.setVisibility(4);
                            }
                            AnimatorSet unused = ThemePreviewActivity.this.messagesPlayViewAnimator = null;
                        }
                    });
                    this.messagesPlayViewAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    this.messagesPlayViewAnimator.start();
                    return;
                }
                this.messagesPlayAnimationView.setAlpha(1 != 0 ? 1.0f : 0.0f);
                this.messagesPlayAnimationView.setScaleX(1 != 0 ? 1.0f : 0.0f);
                this.messagesPlayAnimationView.setScaleY(1 != 0 ? 1.0f : 0.0f);
                this.messagesCheckBoxView[0].setTranslationX(1 != 0 ? (float) (-AndroidUtilities.dp(34.0f)) : 0.0f);
                this.messagesCheckBoxView[1].setTranslationX(1 != 0 ? (float) AndroidUtilities.dp(34.0f) : 0.0f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setBackgroundColor(int color, int num, boolean applyNow, boolean animated) {
        MotionBackgroundDrawable motionBackgroundDrawable;
        if (num == 0) {
            this.backgroundColor = color;
        } else if (num == 1) {
            this.backgroundGradientColor1 = color;
        } else if (num == 2) {
            this.backgroundGradientColor2 = color;
        } else if (num == 3) {
            this.backgroundGradientColor3 = color;
        }
        updatePlayAnimationView(animated);
        if (this.backgroundCheckBoxView != null) {
            int a = 0;
            while (true) {
                WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.backgroundCheckBoxView;
                if (a >= wallpaperCheckBoxViewArr.length) {
                    break;
                }
                if (wallpaperCheckBoxViewArr[a] != null) {
                    wallpaperCheckBoxViewArr[a].setColor(num, color);
                }
                a++;
            }
        }
        if (this.backgroundGradientColor2 != 0) {
            if (this.intensitySeekBar != null && Theme.getActiveTheme().isDark()) {
                this.intensitySeekBar.setTwoSided(true);
            }
            Drawable currentBackground = this.backgroundImage.getBackground();
            if (currentBackground instanceof MotionBackgroundDrawable) {
                motionBackgroundDrawable = (MotionBackgroundDrawable) currentBackground;
            } else {
                motionBackgroundDrawable = new MotionBackgroundDrawable();
                motionBackgroundDrawable.setParentView(this.backgroundImage);
                if (this.rotatePreview) {
                    motionBackgroundDrawable.rotatePreview(false);
                }
            }
            motionBackgroundDrawable.setColors(this.backgroundColor, this.backgroundGradientColor1, this.backgroundGradientColor2, this.backgroundGradientColor3);
            this.backgroundImage.setBackground(motionBackgroundDrawable);
            this.patternColor = motionBackgroundDrawable.getPatternColor();
            this.checkColor = NUM;
        } else if (this.backgroundGradientColor1 != 0) {
            this.backgroundImage.setBackground(new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(this.backgroundRotation), new int[]{this.backgroundColor, this.backgroundGradientColor1}));
            int patternColor2 = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(this.backgroundColor, this.backgroundGradientColor1));
            this.checkColor = patternColor2;
            this.patternColor = patternColor2;
        } else {
            this.backgroundImage.setBackgroundColor(this.backgroundColor);
            int patternColor3 = AndroidUtilities.getPatternColor(this.backgroundColor);
            this.checkColor = patternColor3;
            this.patternColor = patternColor3;
        }
        if (!Theme.hasThemeKey("chat_serviceBackground") || (this.backgroundImage.getBackground() instanceof MotionBackgroundDrawable)) {
            int i = this.checkColor;
            Theme.applyChatServiceMessageColor(new int[]{i, i, i, i}, this.backgroundImage.getBackground());
        } else if (Theme.getCachedWallpaper() instanceof MotionBackgroundDrawable) {
            int c = Theme.getColor("chat_serviceBackground");
            Theme.applyChatServiceMessageColor(new int[]{c, c, c, c}, this.backgroundImage.getBackground());
        }
        ImageView imageView = this.backgroundPlayAnimationImageView;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_serviceText"), PorterDuff.Mode.MULTIPLY));
        }
        ImageView imageView2 = this.messagesPlayAnimationImageView;
        if (imageView2 != null) {
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_serviceText"), PorterDuff.Mode.MULTIPLY));
        }
        BackupImageView backupImageView = this.backgroundImage;
        if (backupImageView != null) {
            backupImageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
            this.backgroundImage.getImageReceiver().setAlpha(Math.abs(this.currentIntensity));
            this.backgroundImage.invalidate();
            if (!Theme.getActiveTheme().isDark() || !(this.backgroundImage.getBackground() instanceof MotionBackgroundDrawable)) {
                this.backgroundImage.getImageReceiver().setGradientBitmap((Bitmap) null);
                SeekBarView seekBarView = this.intensitySeekBar;
                if (seekBarView != null) {
                    seekBarView.setTwoSided(false);
                }
            } else {
                SeekBarView seekBarView2 = this.intensitySeekBar;
                if (seekBarView2 != null) {
                    seekBarView2.setTwoSided(true);
                }
                if (this.currentIntensity < 0.0f) {
                    this.backgroundImage.getImageReceiver().setGradientBitmap(((MotionBackgroundDrawable) this.backgroundImage.getBackground()).getBitmap());
                }
            }
            SeekBarView seekBarView3 = this.intensitySeekBar;
            if (seekBarView3 != null) {
                seekBarView3.setProgress(this.currentIntensity);
            }
        }
        RecyclerListView recyclerListView = this.listView2;
        if (recyclerListView != null) {
            recyclerListView.invalidateViews();
        }
        FrameLayout frameLayout2 = this.backgroundButtonsContainer;
        if (frameLayout2 != null) {
            int N = frameLayout2.getChildCount();
            for (int a2 = 0; a2 < N; a2++) {
                this.backgroundButtonsContainer.getChildAt(a2).invalidate();
            }
        }
        FrameLayout frameLayout3 = this.messagesButtonsContainer;
        if (frameLayout3 != null) {
            int N2 = frameLayout3.getChildCount();
            for (int a3 = 0; a3 < N2; a3++) {
                this.messagesButtonsContainer.getChildAt(a3).invalidate();
            }
        }
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setColors("chat_serviceBackground", "chat_serviceBackground", "chat_serviceText", "chat_serviceText");
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:108:0x02a1  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02c2  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02cb  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x02e8  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0364  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x0391  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x03a3  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x03b5  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x03cc  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0202  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setCurrentImage(boolean r33) {
        /*
            r32 = this;
            r0 = r32
            int r1 = r0.screenType
            r2 = 0
            if (r1 != 0) goto L_0x0016
            org.telegram.ui.ActionBar.Theme$ThemeAccent r3 = r0.accent
            if (r3 != 0) goto L_0x0016
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            r1.setBackground(r3)
            goto L_0x03df
        L_0x0016:
            r3 = 3
            r4 = 2
            r5 = 0
            r6 = 1
            if (r1 != r4) goto L_0x017f
            java.lang.Object r1 = r0.currentWallpaper
            boolean r7 = r1 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper
            r8 = 100
            if (r7 == 0) goto L_0x0052
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r1
            if (r33 == 0) goto L_0x0030
            org.telegram.tgnet.TLRPC$Document r3 = r1.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r8)
        L_0x0030:
            r3 = r5
            org.telegram.ui.Components.BackupImageView r9 = r0.backgroundImage
            org.telegram.tgnet.TLRPC$Document r4 = r1.document
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument(r4)
            java.lang.String r11 = r0.imageFilter
            org.telegram.tgnet.TLRPC$Document r4 = r1.document
            org.telegram.messenger.ImageLocation r12 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC.PhotoSize) r3, (org.telegram.tgnet.TLRPC.Document) r4)
            org.telegram.tgnet.TLRPC$Document r4 = r1.document
            int r15 = r4.size
            r16 = 1
            java.lang.String r13 = "100_100_b"
            java.lang.String r14 = "jpg"
            r17 = r1
            r9.setImage(r10, r11, r12, r13, r14, r15, r16, r17)
            goto L_0x03df
        L_0x0052:
            boolean r7 = r1 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r7 == 0) goto L_0x00d8
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r1
            int r5 = r1.gradientRotation
            r0.backgroundRotation = r5
            int r5 = r1.color
            r0.setBackgroundColor(r5, r2, r6, r2)
            int r5 = r1.gradientColor1
            if (r5 == 0) goto L_0x006a
            int r5 = r1.gradientColor1
            r0.setBackgroundColor(r5, r6, r6, r2)
        L_0x006a:
            int r5 = r1.gradientColor2
            r0.setBackgroundColor(r5, r4, r6, r2)
            int r4 = r1.gradientColor3
            r0.setBackgroundColor(r4, r3, r6, r2)
            org.telegram.tgnet.TLRPC$TL_wallPaper r3 = r0.selectedPattern
            if (r3 == 0) goto L_0x0093
            org.telegram.ui.Components.BackupImageView r4 = r0.backgroundImage
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForDocument(r3)
            java.lang.String r6 = r0.imageFilter
            r7 = 0
            r8 = 0
            org.telegram.tgnet.TLRPC$TL_wallPaper r3 = r0.selectedPattern
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            int r10 = r3.size
            r11 = 1
            org.telegram.tgnet.TLRPC$TL_wallPaper r12 = r0.selectedPattern
            java.lang.String r9 = "jpg"
            r4.setImage(r5, r6, r7, r8, r9, r10, r11, r12)
            goto L_0x00d6
        L_0x0093:
            java.lang.String r3 = r1.slug
            java.lang.String r4 = "d"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x00d6
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r3.x
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r4.y
            int r3 = java.lang.Math.min(r3, r4)
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r4.x
            android.graphics.Point r5 = org.telegram.messenger.AndroidUtilities.displaySize
            int r5 = r5.y
            int r4 = java.lang.Math.max(r4, r5)
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 29
            if (r5 < r6) goto L_0x00be
            r5 = 1459617792(0x57000000, float:1.40737488E14)
            goto L_0x00ca
        L_0x00be:
            int r5 = r1.color
            int r6 = r1.gradientColor1
            int r7 = r1.gradientColor2
            int r8 = r1.gradientColor3
            int r5 = org.telegram.ui.Components.MotionBackgroundDrawable.getPatternColor(r5, r6, r7, r8)
        L_0x00ca:
            org.telegram.ui.Components.BackupImageView r6 = r0.backgroundImage
            r7 = 2131558431(0x7f0d001f, float:1.8742178E38)
            android.graphics.Bitmap r7 = org.telegram.messenger.SvgHelper.getBitmap((int) r7, (int) r3, (int) r4, (int) r5)
            r6.setImageBitmap(r7)
        L_0x00d6:
            goto L_0x03df
        L_0x00d8:
            boolean r3 = r1 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper
            if (r3 == 0) goto L_0x012e
            android.graphics.Bitmap r3 = r0.currentWallpaperBitmap
            if (r3 == 0) goto L_0x00e7
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            r1.setImageBitmap(r3)
            goto L_0x03df
        L_0x00e7:
            org.telegram.ui.WallpapersListActivity$FileWallpaper r1 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r1
            java.io.File r3 = r1.originalPath
            if (r3 == 0) goto L_0x00fb
            org.telegram.ui.Components.BackupImageView r3 = r0.backgroundImage
            java.io.File r4 = r1.originalPath
            java.lang.String r4 = r4.getAbsolutePath()
            java.lang.String r6 = r0.imageFilter
            r3.setImage(r4, r6, r5)
            goto L_0x012c
        L_0x00fb:
            java.io.File r3 = r1.path
            if (r3 == 0) goto L_0x010d
            org.telegram.ui.Components.BackupImageView r3 = r0.backgroundImage
            java.io.File r4 = r1.path
            java.lang.String r4 = r4.getAbsolutePath()
            java.lang.String r6 = r0.imageFilter
            r3.setImage(r4, r6, r5)
            goto L_0x012c
        L_0x010d:
            java.lang.String r3 = r1.slug
            java.lang.String r4 = "t"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x0121
            org.telegram.ui.Components.BackupImageView r3 = r0.backgroundImage
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedWallpaper(r2, r3)
            r3.setImageDrawable(r4)
            goto L_0x012c
        L_0x0121:
            int r3 = r1.resId
            if (r3 == 0) goto L_0x012c
            org.telegram.ui.Components.BackupImageView r3 = r0.backgroundImage
            int r4 = r1.resId
            r3.setImageResource(r4)
        L_0x012c:
            goto L_0x03df
        L_0x012e:
            boolean r3 = r1 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r3 == 0) goto L_0x03df
            org.telegram.messenger.MediaController$SearchImage r1 = (org.telegram.messenger.MediaController.SearchImage) r1
            org.telegram.tgnet.TLRPC$Photo r3 = r1.photo
            if (r3 == 0) goto L_0x0170
            org.telegram.tgnet.TLRPC$Photo r3 = r1.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r8)
            org.telegram.tgnet.TLRPC$Photo r4 = r1.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r4.sizes
            int r5 = r0.maxWallpaperSize
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5, r6)
            if (r4 != r3) goto L_0x014d
            r4 = 0
        L_0x014d:
            if (r4 == 0) goto L_0x0153
            int r5 = r4.size
            r15 = r5
            goto L_0x0154
        L_0x0153:
            r15 = 0
        L_0x0154:
            org.telegram.ui.Components.BackupImageView r9 = r0.backgroundImage
            org.telegram.tgnet.TLRPC$Photo r5 = r1.photo
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC.PhotoSize) r4, (org.telegram.tgnet.TLRPC.Photo) r5)
            java.lang.String r11 = r0.imageFilter
            org.telegram.tgnet.TLRPC$Photo r5 = r1.photo
            org.telegram.messenger.ImageLocation r12 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC.PhotoSize) r3, (org.telegram.tgnet.TLRPC.Photo) r5)
            r16 = 1
            java.lang.String r13 = "100_100_b"
            java.lang.String r14 = "jpg"
            r17 = r1
            r9.setImage(r10, r11, r12, r13, r14, r15, r16, r17)
            goto L_0x017d
        L_0x0170:
            org.telegram.ui.Components.BackupImageView r3 = r0.backgroundImage
            java.lang.String r4 = r1.imageUrl
            java.lang.String r5 = r0.imageFilter
            java.lang.String r6 = r1.thumbUrl
            java.lang.String r7 = "100_100_b"
            r3.setImage((java.lang.String) r4, (java.lang.String) r5, (java.lang.String) r6, (java.lang.String) r7)
        L_0x017d:
            goto L_0x03df
        L_0x017f:
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r1 = r0.backgroundGradientDisposable
            if (r1 == 0) goto L_0x0188
            r1.dispose()
            r0.backgroundGradientDisposable = r5
        L_0x0188:
            java.lang.String r1 = "chat_wallpaper"
            int r1 = org.telegram.ui.ActionBar.Theme.getDefaultAccentColor(r1)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r7 = r0.accent
            long r7 = r7.backgroundOverrideColor
            int r8 = (int) r7
            if (r8 == 0) goto L_0x0197
            r7 = r8
            goto L_0x0198
        L_0x0197:
            r7 = r1
        L_0x0198:
            java.lang.String r9 = "chat_wallpaper_gradient_to"
            int r9 = org.telegram.ui.ActionBar.Theme.getDefaultAccentColor(r9)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r10 = r0.accent
            long r10 = r10.backgroundGradientOverrideColor1
            int r11 = (int) r10
            r12 = 0
            if (r11 != 0) goto L_0x01b1
            org.telegram.ui.ActionBar.Theme$ThemeAccent r10 = r0.accent
            long r14 = r10.backgroundGradientOverrideColor1
            int r10 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1))
            if (r10 == 0) goto L_0x01b1
            r10 = 0
            goto L_0x01b6
        L_0x01b1:
            if (r11 == 0) goto L_0x01b5
            r10 = r11
            goto L_0x01b6
        L_0x01b5:
            r10 = r9
        L_0x01b6:
            java.lang.String r14 = "key_chat_wallpaper_gradient_to2"
            int r14 = org.telegram.ui.ActionBar.Theme.getDefaultAccentColor(r14)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r15 = r0.accent
            long r5 = r15.backgroundGradientOverrideColor2
            int r6 = (int) r5
            if (r6 != 0) goto L_0x01cd
            org.telegram.ui.ActionBar.Theme$ThemeAccent r5 = r0.accent
            long r3 = r5.backgroundGradientOverrideColor2
            int r5 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r5 == 0) goto L_0x01cd
            r3 = 0
            goto L_0x01d2
        L_0x01cd:
            if (r6 == 0) goto L_0x01d1
            r3 = r6
            goto L_0x01d2
        L_0x01d1:
            r3 = r14
        L_0x01d2:
            java.lang.String r4 = "key_chat_wallpaper_gradient_to3"
            int r4 = org.telegram.ui.ActionBar.Theme.getDefaultAccentColor(r4)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r5 = r0.accent
            r19 = r3
            long r2 = r5.backgroundGradientOverrideColor3
            int r3 = (int) r2
            if (r3 != 0) goto L_0x01ec
            org.telegram.ui.ActionBar.Theme$ThemeAccent r2 = r0.accent
            r5 = r1
            long r1 = r2.backgroundGradientOverrideColor3
            int r20 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r20 == 0) goto L_0x01ed
            r1 = 0
            goto L_0x01f2
        L_0x01ec:
            r5 = r1
        L_0x01ed:
            if (r3 == 0) goto L_0x01f1
            r1 = r3
            goto L_0x01f2
        L_0x01f1:
            r1 = r4
        L_0x01f2:
            org.telegram.ui.ActionBar.Theme$ThemeAccent r2 = r0.accent
            java.lang.String r2 = r2.patternSlug
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x02a1
            boolean r2 = org.telegram.ui.ActionBar.Theme.hasCustomWallpaper()
            if (r2 != 0) goto L_0x02a1
            if (r19 == 0) goto L_0x0231
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            android.graphics.drawable.Drawable r2 = r2.getBackground()
            boolean r12 = r2 instanceof org.telegram.ui.Components.MotionBackgroundDrawable
            if (r12 == 0) goto L_0x0212
            r12 = r2
            org.telegram.ui.Components.MotionBackgroundDrawable r12 = (org.telegram.ui.Components.MotionBackgroundDrawable) r12
            goto L_0x0224
        L_0x0212:
            org.telegram.ui.Components.MotionBackgroundDrawable r12 = new org.telegram.ui.Components.MotionBackgroundDrawable
            r12.<init>()
            org.telegram.ui.Components.BackupImageView r13 = r0.backgroundImage
            r12.setParentView(r13)
            boolean r13 = r0.rotatePreview
            if (r13 == 0) goto L_0x0224
            r13 = 0
            r12.rotatePreview(r13)
        L_0x0224:
            r13 = r19
            r12.setColors(r7, r10, r13, r1)
            r2 = r12
            r19 = r3
            r21 = r4
            r22 = r5
            goto L_0x0272
        L_0x0231:
            r13 = r19
            if (r10 == 0) goto L_0x0267
            org.telegram.ui.ActionBar.Theme$ThemeAccent r2 = r0.accent
            int r2 = r2.backgroundRotation
            android.graphics.drawable.GradientDrawable$Orientation r2 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r2)
            org.telegram.ui.Components.BackgroundGradientDrawable r12 = new org.telegram.ui.Components.BackgroundGradientDrawable
            r19 = r3
            r15 = 2
            int[] r3 = new int[r15]
            r18 = 0
            r3[r18] = r7
            r17 = 1
            r3[r17] = r10
            r12.<init>(r2, r3)
            r3 = r12
            org.telegram.ui.ThemePreviewActivity$33 r12 = new org.telegram.ui.ThemePreviewActivity$33
            r12.<init>()
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes r15 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen()
            r21 = r4
            r22 = r5
            r4 = 100
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r4 = r3.startDithering(r15, r12, r4)
            r0.backgroundGradientDisposable = r4
            r2 = r3
            goto L_0x0272
        L_0x0267:
            r19 = r3
            r21 = r4
            r22 = r5
            android.graphics.drawable.ColorDrawable r2 = new android.graphics.drawable.ColorDrawable
            r2.<init>(r7)
        L_0x0272:
            org.telegram.ui.Components.BackupImageView r3 = r0.backgroundImage
            r3.setBackground(r2)
            org.telegram.tgnet.TLRPC$TL_wallPaper r3 = r0.selectedPattern
            if (r3 == 0) goto L_0x02a0
            org.telegram.ui.Components.BackupImageView r4 = r0.backgroundImage
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            org.telegram.messenger.ImageLocation r24 = org.telegram.messenger.ImageLocation.getForDocument(r3)
            java.lang.String r3 = r0.imageFilter
            r26 = 0
            r27 = 0
            org.telegram.tgnet.TLRPC$TL_wallPaper r5 = r0.selectedPattern
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            int r5 = r5.size
            r30 = 1
            org.telegram.tgnet.TLRPC$TL_wallPaper r12 = r0.selectedPattern
            java.lang.String r28 = "jpg"
            r23 = r4
            r25 = r3
            r29 = r5
            r31 = r12
            r23.setImage(r24, r25, r26, r27, r28, r29, r30, r31)
        L_0x02a0:
            goto L_0x02c0
        L_0x02a1:
            r21 = r4
            r22 = r5
            r13 = r19
            r19 = r3
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            if (r2 == 0) goto L_0x02c0
            boolean r3 = r2 instanceof org.telegram.ui.Components.MotionBackgroundDrawable
            if (r3 == 0) goto L_0x02bb
            r3 = r2
            org.telegram.ui.Components.MotionBackgroundDrawable r3 = (org.telegram.ui.Components.MotionBackgroundDrawable) r3
            org.telegram.ui.Components.BackupImageView r4 = r0.backgroundImage
            r3.setParentView(r4)
        L_0x02bb:
            org.telegram.ui.Components.BackupImageView r3 = r0.backgroundImage
            r3.setBackground(r2)
        L_0x02c0:
            if (r10 != 0) goto L_0x02cb
            int r2 = org.telegram.messenger.AndroidUtilities.getPatternColor(r7)
            r0.checkColor = r2
            r0.patternColor = r2
            goto L_0x02e4
        L_0x02cb:
            if (r13 == 0) goto L_0x02d8
            int r2 = org.telegram.ui.Components.MotionBackgroundDrawable.getPatternColor(r7, r10, r13, r1)
            r0.patternColor = r2
            r2 = 754974720(0x2d000000, float:7.2759576E-12)
            r0.checkColor = r2
            goto L_0x02e4
        L_0x02d8:
            int r2 = org.telegram.messenger.AndroidUtilities.getAverageColor(r7, r10)
            int r2 = org.telegram.messenger.AndroidUtilities.getPatternColor(r2)
            r0.checkColor = r2
            r0.patternColor = r2
        L_0x02e4:
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            if (r2 == 0) goto L_0x0360
            org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            int r4 = r0.patternColor
            android.graphics.PorterDuff$Mode r5 = r0.blendMode
            r3.<init>(r4, r5)
            r2.setColorFilter(r3)
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
            float r3 = r0.currentIntensity
            float r3 = java.lang.Math.abs(r3)
            r2.setAlpha(r3)
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            r2.invalidate()
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r2 = r2.isDark()
            if (r2 == 0) goto L_0x0345
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            android.graphics.drawable.Drawable r2 = r2.getBackground()
            boolean r2 = r2 instanceof org.telegram.ui.Components.MotionBackgroundDrawable
            if (r2 == 0) goto L_0x0345
            org.telegram.ui.Components.SeekBarView r2 = r0.intensitySeekBar
            if (r2 == 0) goto L_0x0328
            r3 = 1
            r2.setTwoSided(r3)
        L_0x0328:
            float r2 = r0.currentIntensity
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 >= 0) goto L_0x0357
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
            org.telegram.ui.Components.BackupImageView r3 = r0.backgroundImage
            android.graphics.drawable.Drawable r3 = r3.getBackground()
            org.telegram.ui.Components.MotionBackgroundDrawable r3 = (org.telegram.ui.Components.MotionBackgroundDrawable) r3
            android.graphics.Bitmap r3 = r3.getBitmap()
            r2.setGradientBitmap(r3)
            goto L_0x0357
        L_0x0345:
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
            r3 = 0
            r2.setGradientBitmap(r3)
            org.telegram.ui.Components.SeekBarView r2 = r0.intensitySeekBar
            if (r2 == 0) goto L_0x0357
            r3 = 0
            r2.setTwoSided(r3)
        L_0x0357:
            org.telegram.ui.Components.SeekBarView r2 = r0.intensitySeekBar
            if (r2 == 0) goto L_0x0360
            float r3 = r0.currentIntensity
            r2.setProgress(r3)
        L_0x0360:
            org.telegram.ui.Components.WallpaperCheckBoxView[] r2 = r0.backgroundCheckBoxView
            if (r2 == 0) goto L_0x038b
            r2 = 0
        L_0x0365:
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r0.backgroundCheckBoxView
            int r4 = r3.length
            if (r2 >= r4) goto L_0x038b
            r3 = r3[r2]
            r4 = 0
            r3.setColor(r4, r7)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r0.backgroundCheckBoxView
            r3 = r3[r2]
            r4 = 1
            r3.setColor(r4, r10)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r0.backgroundCheckBoxView
            r3 = r3[r2]
            r5 = 2
            r3.setColor(r5, r13)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r0.backgroundCheckBoxView
            r3 = r3[r2]
            r12 = 3
            r3.setColor(r12, r1)
            int r2 = r2 + 1
            goto L_0x0365
        L_0x038b:
            android.widget.ImageView r2 = r0.backgroundPlayAnimationImageView
            java.lang.String r3 = "chat_serviceText"
            if (r2 == 0) goto L_0x039f
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r5, r12)
            r2.setColorFilter(r4)
        L_0x039f:
            android.widget.ImageView r2 = r0.messagesPlayAnimationImageView
            if (r2 == 0) goto L_0x03b1
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r3, r5)
            r2.setColorFilter(r4)
        L_0x03b1:
            android.widget.FrameLayout r2 = r0.backgroundButtonsContainer
            if (r2 == 0) goto L_0x03c8
            r3 = 0
            int r2 = r2.getChildCount()
        L_0x03ba:
            if (r3 >= r2) goto L_0x03c8
            android.widget.FrameLayout r4 = r0.backgroundButtonsContainer
            android.view.View r4 = r4.getChildAt(r3)
            r4.invalidate()
            int r3 = r3 + 1
            goto L_0x03ba
        L_0x03c8:
            android.widget.FrameLayout r2 = r0.messagesButtonsContainer
            if (r2 == 0) goto L_0x03df
            r3 = 0
            int r2 = r2.getChildCount()
        L_0x03d1:
            if (r3 >= r2) goto L_0x03df
            android.widget.FrameLayout r4 = r0.messagesButtonsContainer
            android.view.View r4 = r4.getChildAt(r3)
            r4.invalidate()
            int r3 = r3 + 1
            goto L_0x03d1
        L_0x03df:
            r1 = 0
            r0.rotatePreview = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.setCurrentImage(boolean):void");
    }

    public static class DialogsAdapter extends RecyclerListView.SelectionAdapter {
        private ArrayList<DialogCell.CustomDialog> dialogs = new ArrayList<>();
        private Context mContext;

        public DialogsAdapter(Context context) {
            this.mContext = context;
            int date = (int) (System.currentTimeMillis() / 1000);
            DialogCell.CustomDialog customDialog = new DialogCell.CustomDialog();
            customDialog.name = LocaleController.getString("ThemePreviewDialog1", NUM);
            customDialog.message = LocaleController.getString("ThemePreviewDialogMessage1", NUM);
            customDialog.id = 0;
            customDialog.unread_count = 0;
            customDialog.pinned = true;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = date;
            customDialog.verified = false;
            customDialog.isMedia = false;
            customDialog.sent = true;
            this.dialogs.add(customDialog);
            DialogCell.CustomDialog customDialog2 = new DialogCell.CustomDialog();
            customDialog2.name = LocaleController.getString("ThemePreviewDialog2", NUM);
            customDialog2.message = LocaleController.getString("ThemePreviewDialogMessage2", NUM);
            customDialog2.id = 1;
            customDialog2.unread_count = 2;
            customDialog2.pinned = false;
            customDialog2.muted = false;
            customDialog2.type = 0;
            customDialog2.date = date - 3600;
            customDialog2.verified = false;
            customDialog2.isMedia = false;
            customDialog2.sent = false;
            this.dialogs.add(customDialog2);
            DialogCell.CustomDialog customDialog3 = new DialogCell.CustomDialog();
            customDialog3.name = LocaleController.getString("ThemePreviewDialog3", NUM);
            customDialog3.message = LocaleController.getString("ThemePreviewDialogMessage3", NUM);
            customDialog3.id = 2;
            customDialog3.unread_count = 3;
            customDialog3.pinned = false;
            customDialog3.muted = true;
            customDialog3.type = 0;
            customDialog3.date = date - 7200;
            customDialog3.verified = false;
            customDialog3.isMedia = true;
            customDialog3.sent = false;
            this.dialogs.add(customDialog3);
            DialogCell.CustomDialog customDialog4 = new DialogCell.CustomDialog();
            customDialog4.name = LocaleController.getString("ThemePreviewDialog4", NUM);
            customDialog4.message = LocaleController.getString("ThemePreviewDialogMessage4", NUM);
            customDialog4.id = 3;
            customDialog4.unread_count = 0;
            customDialog4.pinned = false;
            customDialog4.muted = false;
            customDialog4.type = 2;
            customDialog4.date = date - 10800;
            customDialog4.verified = false;
            customDialog4.isMedia = false;
            customDialog4.sent = false;
            this.dialogs.add(customDialog4);
            DialogCell.CustomDialog customDialog5 = new DialogCell.CustomDialog();
            customDialog5.name = LocaleController.getString("ThemePreviewDialog5", NUM);
            customDialog5.message = LocaleController.getString("ThemePreviewDialogMessage5", NUM);
            customDialog5.id = 4;
            customDialog5.unread_count = 0;
            customDialog5.pinned = false;
            customDialog5.muted = false;
            customDialog5.type = 1;
            customDialog5.date = date - 14400;
            customDialog5.verified = false;
            customDialog5.isMedia = false;
            customDialog5.sent = true;
            this.dialogs.add(customDialog5);
            DialogCell.CustomDialog customDialog6 = new DialogCell.CustomDialog();
            customDialog6.name = LocaleController.getString("ThemePreviewDialog6", NUM);
            customDialog6.message = LocaleController.getString("ThemePreviewDialogMessage6", NUM);
            customDialog6.id = 5;
            customDialog6.unread_count = 0;
            customDialog6.pinned = false;
            customDialog6.muted = false;
            customDialog6.type = 0;
            customDialog6.date = date - 18000;
            customDialog6.verified = false;
            customDialog6.isMedia = false;
            customDialog6.sent = false;
            this.dialogs.add(customDialog6);
            DialogCell.CustomDialog customDialog7 = new DialogCell.CustomDialog();
            customDialog7.name = LocaleController.getString("ThemePreviewDialog7", NUM);
            customDialog7.message = LocaleController.getString("ThemePreviewDialogMessage7", NUM);
            customDialog7.id = 6;
            customDialog7.unread_count = 0;
            customDialog7.pinned = false;
            customDialog7.muted = false;
            customDialog7.type = 0;
            customDialog7.date = date - 21600;
            customDialog7.verified = true;
            customDialog7.isMedia = false;
            customDialog7.sent = false;
            this.dialogs.add(customDialog7);
            DialogCell.CustomDialog customDialog8 = new DialogCell.CustomDialog();
            customDialog8.name = LocaleController.getString("ThemePreviewDialog8", NUM);
            customDialog8.message = LocaleController.getString("ThemePreviewDialogMessage8", NUM);
            customDialog8.id = 0;
            customDialog8.unread_count = 0;
            customDialog8.pinned = false;
            customDialog8.muted = false;
            customDialog8.type = 0;
            customDialog8.date = date - 25200;
            customDialog8.verified = true;
            customDialog8.isMedia = false;
            customDialog8.sent = false;
            this.dialogs.add(customDialog8);
        }

        public int getItemCount() {
            return this.dialogs.size();
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view;
            if (viewType == 0) {
                view = new DialogCell((DialogsActivity) null, this.mContext, false, false);
            } else {
                view = new LoadingCell(this.mContext);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                DialogCell cell = (DialogCell) viewHolder.itemView;
                boolean z = true;
                if (i == getItemCount() - 1) {
                    z = false;
                }
                cell.useSeparator = z;
                cell.setDialog(this.dialogs.get(i));
            }
        }

        public int getItemViewType(int i) {
            if (i == this.dialogs.size()) {
                return 1;
            }
            return 0;
        }
    }

    public class MessagesAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private ArrayList<MessageObject> messages;
        /* access modifiers changed from: private */
        public boolean showSecretMessages;
        final /* synthetic */ ThemePreviewActivity this$0;

        public MessagesAdapter(ThemePreviewActivity this$02, Context context) {
            this.this$0 = this$02;
            this.showSecretMessages = this$02.screenType == 0 && Utilities.random.nextInt(100) <= 1;
            this.mContext = context;
            this.messages = new ArrayList<>();
            int date = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            if (this$02.screenType == 2) {
                TLRPC.Message message = new TLRPC.TL_message();
                if (this$02.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                    message.message = LocaleController.getString("BackgroundColorSinglePreviewLine2", NUM);
                } else {
                    message.message = LocaleController.getString("BackgroundPreviewLine2", NUM);
                }
                message.date = date + 60;
                message.dialog_id = 1;
                message.flags = 259;
                message.from_id = new TLRPC.TL_peerUser();
                message.from_id.user_id = UserConfig.getInstance(this$02.currentAccount).getClientUserId();
                message.id = 1;
                message.media = new TLRPC.TL_messageMediaEmpty();
                message.out = true;
                message.peer_id = new TLRPC.TL_peerUser();
                message.peer_id.user_id = 0;
                MessageObject messageObject = new MessageObject(this$02.currentAccount, message, true, false);
                messageObject.eventId = 1;
                messageObject.resetLayout();
                this.messages.add(messageObject);
                TLRPC.Message message2 = new TLRPC.TL_message();
                if (this$02.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                    message2.message = LocaleController.getString("BackgroundColorSinglePreviewLine1", NUM);
                } else {
                    message2.message = LocaleController.getString("BackgroundPreviewLine1", NUM);
                }
                message2.date = date + 60;
                message2.dialog_id = 1;
                message2.flags = 265;
                message2.from_id = new TLRPC.TL_peerUser();
                message2.id = 1;
                message2.media = new TLRPC.TL_messageMediaEmpty();
                message2.out = false;
                message2.peer_id = new TLRPC.TL_peerUser();
                message2.peer_id.user_id = UserConfig.getInstance(this$02.currentAccount).getClientUserId();
                MessageObject messageObject2 = new MessageObject(this$02.currentAccount, message2, true, false);
                messageObject2.eventId = 1;
                messageObject2.resetLayout();
                this.messages.add(messageObject2);
            } else if (this$02.screenType == 1) {
                TLRPC.Message message3 = new TLRPC.TL_message();
                message3.media = new TLRPC.TL_messageMediaDocument();
                message3.media.document = new TLRPC.TL_document();
                message3.media.document.mime_type = "audio/mp3";
                message3.media.document.file_reference = new byte[0];
                message3.media.document.id = -2147483648L;
                message3.media.document.size = 2621440;
                message3.media.document.dc_id = Integer.MIN_VALUE;
                TLRPC.TL_documentAttributeFilename attributeFilename = new TLRPC.TL_documentAttributeFilename();
                attributeFilename.file_name = LocaleController.getString("NewThemePreviewReply2", NUM) + ".mp3";
                message3.media.document.attributes.add(attributeFilename);
                message3.date = date + 60;
                message3.dialog_id = 1;
                message3.flags = 259;
                message3.from_id = new TLRPC.TL_peerUser();
                message3.from_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                message3.id = 1;
                message3.out = true;
                message3.peer_id = new TLRPC.TL_peerUser();
                message3.peer_id.user_id = 0;
                MessageObject replyMessageObject = new MessageObject(UserConfig.selectedAccount, message3, true, false);
                if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    TLRPC.Message message4 = new TLRPC.TL_message();
                    message4.message = "this is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text\nthis is very very long text";
                    message4.date = date + 960;
                    message4.dialog_id = 1;
                    message4.flags = 259;
                    message4.from_id = new TLRPC.TL_peerUser();
                    message4.from_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                    message4.id = 1;
                    message4.media = new TLRPC.TL_messageMediaEmpty();
                    message4.out = true;
                    message4.peer_id = new TLRPC.TL_peerUser();
                    message4.peer_id.user_id = 0;
                    MessageObject message1 = new MessageObject(UserConfig.selectedAccount, message4, true, false);
                    message1.resetLayout();
                    message1.eventId = 1;
                    this.messages.add(message1);
                }
                TLRPC.Message message5 = new TLRPC.TL_message();
                String text = LocaleController.getString("NewThemePreviewLine3", NUM);
                StringBuilder builder = new StringBuilder(text);
                int index1 = text.indexOf(42);
                int index2 = text.lastIndexOf(42);
                if (!(index1 == -1 || index2 == -1)) {
                    builder.replace(index2, index2 + 1, "");
                    builder.replace(index1, index1 + 1, "");
                    TLRPC.TL_messageEntityTextUrl entityUrl = new TLRPC.TL_messageEntityTextUrl();
                    entityUrl.offset = index1;
                    entityUrl.length = (index2 - index1) - 1;
                    entityUrl.url = "https://telegram.org";
                    message5.entities.add(entityUrl);
                }
                message5.message = builder.toString();
                message5.date = date + 960;
                message5.dialog_id = 1;
                message5.flags = 259;
                message5.from_id = new TLRPC.TL_peerUser();
                message5.from_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                message5.id = 1;
                message5.media = new TLRPC.TL_messageMediaEmpty();
                message5.out = true;
                message5.peer_id = new TLRPC.TL_peerUser();
                message5.peer_id.user_id = 0;
                MessageObject message12 = new MessageObject(UserConfig.selectedAccount, message5, true, false);
                message12.resetLayout();
                message12.eventId = 1;
                this.messages.add(message12);
                TLRPC.Message message6 = new TLRPC.TL_message();
                message6.message = LocaleController.getString("NewThemePreviewLine1", NUM);
                message6.date = date + 60;
                message6.dialog_id = 1;
                message6.flags = 265;
                message6.from_id = new TLRPC.TL_peerUser();
                message6.id = 1;
                message6.reply_to = new TLRPC.TL_messageReplyHeader();
                message6.reply_to.reply_to_msg_id = 5;
                message6.media = new TLRPC.TL_messageMediaEmpty();
                message6.out = false;
                message6.peer_id = new TLRPC.TL_peerUser();
                message6.peer_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                MessageObject message22 = new MessageObject(UserConfig.selectedAccount, message6, true, false);
                message22.customReplyName = LocaleController.getString("NewThemePreviewName", NUM);
                message12.customReplyName = "Test User";
                message22.eventId = 1;
                message22.resetLayout();
                message22.replyMessageObject = replyMessageObject;
                message12.replyMessageObject = message22;
                this.messages.add(message22);
                this.messages.add(replyMessageObject);
                TLRPC.Message message7 = new TLRPC.TL_message();
                message7.date = date + 120;
                message7.dialog_id = 1;
                message7.flags = 259;
                message7.out = false;
                message7.from_id = new TLRPC.TL_peerUser();
                message7.id = 1;
                message7.media = new TLRPC.TL_messageMediaDocument();
                message7.media.flags |= 3;
                message7.media.document = new TLRPC.TL_document();
                message7.media.document.mime_type = "audio/ogg";
                message7.media.document.file_reference = new byte[0];
                TLRPC.TL_documentAttributeAudio audio = new TLRPC.TL_documentAttributeAudio();
                audio.flags = 1028;
                audio.duration = 3;
                audio.voice = true;
                audio.waveform = new byte[]{0, 4, 17, -50, -93, 86, -103, -45, -12, -26, 63, -25, -3, 109, -114, -54, -4, -1, -1, -1, -1, -29, -1, -1, -25, -1, -1, -97, -43, 57, -57, -108, 1, -91, -4, -47, 21, 99, 10, 97, 43, 45, 115, -112, -77, 51, -63, 66, 40, 34, -122, -116, 48, -124, 16, 66, -120, 16, 68, 16, 33, 4, 1};
                message7.media.document.attributes.add(audio);
                message7.out = true;
                message7.peer_id = new TLRPC.TL_peerUser();
                TLRPC.TL_documentAttributeFilename tL_documentAttributeFilename = attributeFilename;
                message7.peer_id.user_id = 0;
                MessageObject messageObject3 = message22;
                MessageObject messageObject4 = new MessageObject(this$02.currentAccount, message7, true, false);
                messageObject4.audioProgressSec = 1;
                messageObject4.audioProgress = 0.3f;
                messageObject4.useCustomPhoto = true;
                this.messages.add(messageObject4);
            } else if (this.showSecretMessages) {
                TLRPC.TL_user user1 = new TLRPC.TL_user();
                user1.id = 2147483647L;
                user1.first_name = "Me";
                TLRPC.TL_user user2 = new TLRPC.TL_user();
                user2.id = NUM;
                user2.first_name = "Serj";
                ArrayList<TLRPC.User> users = new ArrayList<>();
                users.add(user1);
                users.add(user2);
                MessagesController.getInstance(this$02.currentAccount).putUsers(users, true);
                TLRPC.Message message8 = new TLRPC.TL_message();
                message8.message = "Guess why Half-Life 3 was never released.";
                message8.date = date + 960;
                message8.dialog_id = -1;
                message8.flags = 259;
                message8.id = NUM;
                message8.media = new TLRPC.TL_messageMediaEmpty();
                message8.out = false;
                message8.peer_id = new TLRPC.TL_peerChat();
                message8.peer_id.chat_id = 1;
                message8.from_id = new TLRPC.TL_peerUser();
                message8.from_id.user_id = user2.id;
                this.messages.add(new MessageObject(this$02.currentAccount, message8, true, false));
                TLRPC.Message message9 = new TLRPC.TL_message();
                message9.message = "No.\nAnd every unnecessary ping of the dev delays the release for 10 days.\nEvery request for ETA delays the release for 2 weeks.";
                message9.date = date + 960;
                message9.dialog_id = -1;
                message9.flags = 259;
                message9.id = 1;
                message9.media = new TLRPC.TL_messageMediaEmpty();
                message9.out = false;
                message9.peer_id = new TLRPC.TL_peerChat();
                message9.peer_id.chat_id = 1;
                message9.from_id = new TLRPC.TL_peerUser();
                message9.from_id.user_id = user2.id;
                this.messages.add(new MessageObject(this$02.currentAccount, message9, true, false));
                TLRPC.Message message10 = new TLRPC.TL_message();
                message10.message = "Is source code for Android coming anytime soon?";
                message10.date = date + 600;
                message10.dialog_id = -1;
                message10.flags = 259;
                message10.id = 1;
                message10.media = new TLRPC.TL_messageMediaEmpty();
                message10.out = false;
                message10.peer_id = new TLRPC.TL_peerChat();
                message10.peer_id.chat_id = 1;
                message10.from_id = new TLRPC.TL_peerUser();
                message10.from_id.user_id = user1.id;
                this.messages.add(new MessageObject(this$02.currentAccount, message10, true, false));
            } else {
                TLRPC.Message message11 = new TLRPC.TL_message();
                message11.message = LocaleController.getString("ThemePreviewLine1", NUM);
                message11.date = date + 60;
                message11.dialog_id = 1;
                message11.flags = 259;
                message11.from_id = new TLRPC.TL_peerUser();
                message11.from_id.user_id = UserConfig.getInstance(this$02.currentAccount).getClientUserId();
                message11.id = 1;
                message11.media = new TLRPC.TL_messageMediaEmpty();
                message11.out = true;
                message11.peer_id = new TLRPC.TL_peerUser();
                message11.peer_id.user_id = 0;
                MessageObject replyMessageObject2 = new MessageObject(this$02.currentAccount, message11, true, false);
                TLRPC.Message message13 = new TLRPC.TL_message();
                message13.message = LocaleController.getString("ThemePreviewLine2", NUM);
                message13.date = date + 960;
                message13.dialog_id = 1;
                message13.flags = 259;
                message13.from_id = new TLRPC.TL_peerUser();
                message13.from_id.user_id = UserConfig.getInstance(this$02.currentAccount).getClientUserId();
                message13.id = 1;
                message13.media = new TLRPC.TL_messageMediaEmpty();
                message13.out = true;
                message13.peer_id = new TLRPC.TL_peerUser();
                message13.peer_id.user_id = 0;
                this.messages.add(new MessageObject(this$02.currentAccount, message13, true, false));
                TLRPC.Message message14 = new TLRPC.TL_message();
                message14.date = date + 130;
                message14.dialog_id = 1;
                message14.flags = 259;
                message14.from_id = new TLRPC.TL_peerUser();
                message14.id = 5;
                message14.media = new TLRPC.TL_messageMediaDocument();
                message14.media.flags |= 3;
                message14.media.document = new TLRPC.TL_document();
                message14.media.document.mime_type = "audio/mp4";
                message14.media.document.file_reference = new byte[0];
                TLRPC.TL_documentAttributeAudio audio2 = new TLRPC.TL_documentAttributeAudio();
                audio2.duration = 243;
                audio2.performer = LocaleController.getString("ThemePreviewSongPerformer", NUM);
                audio2.title = LocaleController.getString("ThemePreviewSongTitle", NUM);
                message14.media.document.attributes.add(audio2);
                message14.out = false;
                message14.peer_id = new TLRPC.TL_peerUser();
                message14.peer_id.user_id = UserConfig.getInstance(this$02.currentAccount).getClientUserId();
                this.messages.add(new MessageObject(this$02.currentAccount, message14, true, false));
                TLRPC.Message message15 = new TLRPC.TL_message();
                message15.message = LocaleController.getString("ThemePreviewLine3", NUM);
                message15.date = date + 60;
                message15.dialog_id = 1;
                message15.flags = 265;
                message15.from_id = new TLRPC.TL_peerUser();
                message15.id = 1;
                message15.reply_to = new TLRPC.TL_messageReplyHeader();
                message15.reply_to.reply_to_msg_id = 5;
                message15.media = new TLRPC.TL_messageMediaEmpty();
                message15.out = false;
                message15.peer_id = new TLRPC.TL_peerUser();
                message15.peer_id.user_id = UserConfig.getInstance(this$02.currentAccount).getClientUserId();
                MessageObject messageObject5 = new MessageObject(this$02.currentAccount, message15, true, false);
                messageObject5.customReplyName = LocaleController.getString("ThemePreviewLine3Reply", NUM);
                messageObject5.replyMessageObject = replyMessageObject2;
                this.messages.add(messageObject5);
                TLRPC.Message message16 = new TLRPC.TL_message();
                message16.date = date + 120;
                message16.dialog_id = 1;
                message16.flags = 259;
                message16.from_id = new TLRPC.TL_peerUser();
                message16.from_id.user_id = UserConfig.getInstance(this$02.currentAccount).getClientUserId();
                message16.id = 1;
                message16.media = new TLRPC.TL_messageMediaDocument();
                message16.media.flags |= 3;
                message16.media.document = new TLRPC.TL_document();
                message16.media.document.mime_type = "audio/ogg";
                message16.media.document.file_reference = new byte[0];
                TLRPC.TL_documentAttributeAudio audio3 = new TLRPC.TL_documentAttributeAudio();
                audio3.flags = 1028;
                audio3.duration = 3;
                audio3.voice = true;
                audio3.waveform = new byte[]{0, 4, 17, -50, -93, 86, -103, -45, -12, -26, 63, -25, -3, 109, -114, -54, -4, -1, -1, -1, -1, -29, -1, -1, -25, -1, -1, -97, -43, 57, -57, -108, 1, -91, -4, -47, 21, 99, 10, 97, 43, 45, 115, -112, -77, 51, -63, 66, 40, 34, -122, -116, 48, -124, 16, 66, -120, 16, 68, 16, 33, 4, 1};
                message16.media.document.attributes.add(audio3);
                message16.out = true;
                message16.peer_id = new TLRPC.TL_peerUser();
                message16.peer_id.user_id = 0;
                MessageObject messageObject6 = new MessageObject(this$02.currentAccount, message16, true, false);
                messageObject6.audioProgressSec = 1;
                messageObject6.audioProgress = 0.3f;
                messageObject6.useCustomPhoto = true;
                this.messages.add(messageObject6);
                this.messages.add(replyMessageObject2);
                TLRPC.TL_message tL_message = new TLRPC.TL_message();
                tL_message.date = date + 10;
                tL_message.dialog_id = 1;
                tL_message.flags = 257;
                tL_message.from_id = new TLRPC.TL_peerUser();
                tL_message.id = 1;
                tL_message.media = new TLRPC.TL_messageMediaPhoto();
                tL_message.media.flags |= 3;
                tL_message.media.photo = new TLRPC.TL_photo();
                tL_message.media.photo.file_reference = new byte[0];
                tL_message.media.photo.has_stickers = false;
                tL_message.media.photo.id = 1;
                tL_message.media.photo.access_hash = 0;
                tL_message.media.photo.date = date;
                TLRPC.TL_photoSize photoSize = new TLRPC.TL_photoSize();
                photoSize.size = 0;
                photoSize.w = 500;
                photoSize.h = 302;
                photoSize.type = "s";
                photoSize.location = new TLRPC.TL_fileLocationUnavailable();
                tL_message.media.photo.sizes.add(photoSize);
                tL_message.message = LocaleController.getString("ThemePreviewLine4", NUM);
                tL_message.out = false;
                tL_message.peer_id = new TLRPC.TL_peerUser();
                tL_message.peer_id.user_id = UserConfig.getInstance(this$02.currentAccount).getClientUserId();
                MessageObject messageObject7 = new MessageObject(this$02.currentAccount, tL_message, true, false);
                messageObject7.useCustomPhoto = true;
                this.messages.add(messageObject7);
                TLRPC.TL_message tL_message2 = tL_message;
            }
        }

        private boolean hasButtons() {
            if (this.this$0.messagesButtonsContainer != null && this.this$0.screenType == 1 && this.this$0.colorType == 3 && this.this$0.accent.myMessagesGradientAccentColor2 != 0) {
                return true;
            }
            if (this.this$0.backgroundButtonsContainer != null) {
                if (this.this$0.screenType == 2) {
                    return true;
                }
                if (this.this$0.screenType == 1 && this.this$0.colorType == 2) {
                    return true;
                }
                return false;
            }
            return false;
        }

        public int getItemCount() {
            int count = this.messages.size();
            if (hasButtons()) {
                return count + 1;
            }
            return count;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: org.telegram.ui.ThemePreviewActivity$MessagesAdapter$5} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v21, resolved type: org.telegram.ui.ThemePreviewActivity$MessagesAdapter$5} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v22, resolved type: org.telegram.ui.ThemePreviewActivity$MessagesAdapter$5} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v23, resolved type: org.telegram.ui.Cells.ChatActionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v24, resolved type: org.telegram.ui.Cells.ChatMessageCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r6, int r7) {
            /*
                r5 = this;
                r0 = -1
                if (r7 != 0) goto L_0x001d
                org.telegram.ui.Cells.ChatMessageCell r1 = new org.telegram.ui.Cells.ChatMessageCell
                android.content.Context r2 = r5.mContext
                r3 = 0
                org.telegram.ui.ThemePreviewActivity$MessagesAdapter$1 r4 = new org.telegram.ui.ThemePreviewActivity$MessagesAdapter$1
                r4.<init>()
                r1.<init>(r2, r3, r4)
                r2 = r1
                org.telegram.ui.Cells.ChatMessageCell r2 = (org.telegram.ui.Cells.ChatMessageCell) r2
                org.telegram.ui.ThemePreviewActivity$MessagesAdapter$2 r3 = new org.telegram.ui.ThemePreviewActivity$MessagesAdapter$2
                r3.<init>()
                r2.setDelegate(r3)
                goto L_0x00a7
            L_0x001d:
                r1 = 1
                if (r7 != r1) goto L_0x0033
                org.telegram.ui.Cells.ChatActionCell r1 = new org.telegram.ui.Cells.ChatActionCell
                android.content.Context r2 = r5.mContext
                r1.<init>(r2)
                r2 = r1
                org.telegram.ui.Cells.ChatActionCell r2 = (org.telegram.ui.Cells.ChatActionCell) r2
                org.telegram.ui.ThemePreviewActivity$MessagesAdapter$3 r3 = new org.telegram.ui.ThemePreviewActivity$MessagesAdapter$3
                r3.<init>()
                r2.setDelegate(r3)
                goto L_0x00a7
            L_0x0033:
                r1 = 2
                r2 = 17
                r3 = 76
                if (r7 != r1) goto L_0x0071
                org.telegram.ui.ThemePreviewActivity r1 = r5.this$0
                android.widget.FrameLayout r1 = r1.backgroundButtonsContainer
                android.view.ViewParent r1 = r1.getParent()
                if (r1 == 0) goto L_0x005b
                org.telegram.ui.ThemePreviewActivity r1 = r5.this$0
                android.widget.FrameLayout r1 = r1.backgroundButtonsContainer
                android.view.ViewParent r1 = r1.getParent()
                android.view.ViewGroup r1 = (android.view.ViewGroup) r1
                org.telegram.ui.ThemePreviewActivity r4 = r5.this$0
                android.widget.FrameLayout r4 = r4.backgroundButtonsContainer
                r1.removeView(r4)
            L_0x005b:
                org.telegram.ui.ThemePreviewActivity$MessagesAdapter$4 r1 = new org.telegram.ui.ThemePreviewActivity$MessagesAdapter$4
                android.content.Context r4 = r5.mContext
                r1.<init>(r4)
                org.telegram.ui.ThemePreviewActivity r4 = r5.this$0
                android.widget.FrameLayout r4 = r4.backgroundButtonsContainer
                android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r0, (int) r3, (int) r2)
                r1.addView(r4, r2)
                goto L_0x00a7
            L_0x0071:
                org.telegram.ui.ThemePreviewActivity r1 = r5.this$0
                android.widget.FrameLayout r1 = r1.messagesButtonsContainer
                android.view.ViewParent r1 = r1.getParent()
                if (r1 == 0) goto L_0x0092
                org.telegram.ui.ThemePreviewActivity r1 = r5.this$0
                android.widget.FrameLayout r1 = r1.messagesButtonsContainer
                android.view.ViewParent r1 = r1.getParent()
                android.view.ViewGroup r1 = (android.view.ViewGroup) r1
                org.telegram.ui.ThemePreviewActivity r4 = r5.this$0
                android.widget.FrameLayout r4 = r4.messagesButtonsContainer
                r1.removeView(r4)
            L_0x0092:
                org.telegram.ui.ThemePreviewActivity$MessagesAdapter$5 r1 = new org.telegram.ui.ThemePreviewActivity$MessagesAdapter$5
                android.content.Context r4 = r5.mContext
                r1.<init>(r4)
                org.telegram.ui.ThemePreviewActivity r4 = r5.this$0
                android.widget.FrameLayout r4 = r4.messagesButtonsContainer
                android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r0, (int) r3, (int) r2)
                r1.addView(r4, r2)
                r2 = r1
            L_0x00a7:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r2 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r3 = -2
                r2.<init>((int) r0, (int) r3)
                r1.setLayoutParams(r2)
                org.telegram.ui.Components.RecyclerListView$Holder r0 = new org.telegram.ui.Components.RecyclerListView$Holder
                r0.<init>(r1)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.MessagesAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean pinnedBotton;
            int type = holder.getItemViewType();
            if (type != 2 && type != 3) {
                if (hasButtons()) {
                    position--;
                }
                MessageObject message = this.messages.get(position);
                View view = holder.itemView;
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell messageCell = (ChatMessageCell) view;
                    boolean pinnedTop = false;
                    messageCell.isChat = false;
                    int nextType = getItemViewType(position - 1);
                    int prevType = getItemViewType(position + 1);
                    if ((message.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) || nextType != holder.getItemViewType()) {
                        pinnedBotton = false;
                    } else {
                        MessageObject nextMessage = this.messages.get(position - 1);
                        pinnedBotton = nextMessage.isOutOwner() == message.isOutOwner() && Math.abs(nextMessage.messageOwner.date - message.messageOwner.date) <= 300;
                    }
                    if (prevType != holder.getItemViewType() || position + 1 >= this.messages.size()) {
                        pinnedTop = false;
                    } else {
                        MessageObject prevMessage = this.messages.get(position + 1);
                        if (!(prevMessage.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) && prevMessage.isOutOwner() == message.isOutOwner() && Math.abs(prevMessage.messageOwner.date - message.messageOwner.date) <= 300) {
                            pinnedTop = true;
                        }
                    }
                    messageCell.isChat = this.showSecretMessages;
                    messageCell.setFullyDraw(true);
                    messageCell.setMessageObject(message, (MessageObject.GroupedMessages) null, pinnedBotton, pinnedTop);
                } else if (view instanceof ChatActionCell) {
                    ChatActionCell actionCell = (ChatActionCell) view;
                    actionCell.setMessageObject(message);
                    actionCell.setAlpha(1.0f);
                }
            }
        }

        public int getItemViewType(int position) {
            if (hasButtons()) {
                if (position != 0) {
                    position--;
                } else if (this.this$0.colorType == 3) {
                    return 3;
                } else {
                    return 2;
                }
            }
            if (position < 0 || position >= this.messages.size()) {
                return 4;
            }
            return this.messages.get(position).contentType;
        }
    }

    private class PatternsAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public PatternsAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public int getItemCount() {
            if (ThemePreviewActivity.this.patterns != null) {
                return ThemePreviewActivity.this.patterns.size();
            }
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(new PatternCell(this.mContext, ThemePreviewActivity.this.maxWallpaperSize, new PatternCell.PatternCellDelegate() {
                public TLRPC.TL_wallPaper getSelectedPattern() {
                    return ThemePreviewActivity.this.selectedPattern;
                }

                public int getCheckColor() {
                    return ThemePreviewActivity.this.checkColor;
                }

                public int getBackgroundColor() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundColor;
                    }
                    int defaultBackground = Theme.getDefaultAccentColor("chat_wallpaper");
                    int backgroundOverrideColor = (int) ThemePreviewActivity.this.accent.backgroundOverrideColor;
                    return backgroundOverrideColor != 0 ? backgroundOverrideColor : defaultBackground;
                }

                public int getBackgroundGradientColor1() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor1;
                    }
                    int defaultBackgroundGradient = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                    int backgroundGradientOverrideColor = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor1;
                    return backgroundGradientOverrideColor != 0 ? backgroundGradientOverrideColor : defaultBackgroundGradient;
                }

                public int getBackgroundGradientColor2() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor2;
                    }
                    int defaultBackgroundGradient = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to2");
                    int backgroundGradientOverrideColor = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor2;
                    return backgroundGradientOverrideColor != 0 ? backgroundGradientOverrideColor : defaultBackgroundGradient;
                }

                public int getBackgroundGradientColor3() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor3;
                    }
                    int defaultBackgroundGradient = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to3");
                    int backgroundGradientOverrideColor = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor3;
                    return backgroundGradientOverrideColor != 0 ? backgroundGradientOverrideColor : defaultBackgroundGradient;
                }

                public int getBackgroundGradientAngle() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundRotation;
                    }
                    return ThemePreviewActivity.this.accent.backgroundRotation;
                }

                public float getIntensity() {
                    return ThemePreviewActivity.this.currentIntensity;
                }

                public int getPatternColor() {
                    return ThemePreviewActivity.this.patternColor;
                }
            }));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            PatternCell view = (PatternCell) holder.itemView;
            view.setPattern((TLRPC.TL_wallPaper) ThemePreviewActivity.this.patterns.get(position));
            view.getImageReceiver().setColorFilter(new PorterDuffColorFilter(ThemePreviewActivity.this.patternColor, ThemePreviewActivity.this.blendMode));
            if (Build.VERSION.SDK_INT >= 29) {
                int color2 = 0;
                if (ThemePreviewActivity.this.screenType == 1) {
                    int defaultBackgroundGradient2 = Theme.getDefaultAccentColor("key_chat_wallpaper_gradient_to2");
                    int backgroundGradientOverrideColor2 = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor2;
                    if (backgroundGradientOverrideColor2 != 0 || ThemePreviewActivity.this.accent.backgroundGradientOverrideColor2 == 0) {
                        color2 = backgroundGradientOverrideColor2 != 0 ? backgroundGradientOverrideColor2 : defaultBackgroundGradient2;
                    } else {
                        color2 = 0;
                    }
                } else if (ThemePreviewActivity.this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                    color2 = ThemePreviewActivity.this.backgroundGradientColor2;
                }
                if (color2 == 0 || ThemePreviewActivity.this.currentIntensity < 0.0f) {
                    view.getImageReceiver().setBlendMode((Object) null);
                } else {
                    ThemePreviewActivity.this.backgroundImage.getImageReceiver().setBlendMode(BlendMode.SOFT_LIGHT);
                }
            }
        }
    }

    private List<ThemeDescription> getThemeDescriptionsInternal() {
        ThemeDescription.ThemeDescriptionDelegate descriptionDelegate = new ThemePreviewActivity$$ExternalSyntheticLambda14(this);
        List<ThemeDescription> items = new ArrayList<>();
        items.add(new ThemeDescription(this.page1, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, descriptionDelegate, "windowBackgroundWhite"));
        items.add(new ThemeDescription(this.viewPager, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        items.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        items.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        items.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        items.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearch"));
        items.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchPlaceholder"));
        items.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        items.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        items.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBTITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubtitle"));
        items.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        items.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, descriptionDelegate, "actionBarDefaultSubmenuBackground"));
        items.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, descriptionDelegate, "actionBarDefaultSubmenuItem"));
        items.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        items.add(new ThemeDescription(this.listView2, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        items.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionIcon"));
        items.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
        items.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionPressedBackground"));
        if (!this.useDefaultThemeForButtons) {
            items.add(new ThemeDescription(this.saveButtonsContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            items.add(new ThemeDescription(this.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
            items.add(new ThemeDescription(this.doneButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
        }
        ColorPicker colorPicker2 = this.colorPicker;
        if (colorPicker2 != null) {
            colorPicker2.provideThemeDescriptions(items);
        }
        if (this.patternLayout != null) {
            for (int a = 0; a < this.patternLayout.length; a++) {
                items.add(new ThemeDescription(this.patternLayout[a], 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelShadow"));
                items.add(new ThemeDescription(this.patternLayout[a], 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelBackground"));
            }
            for (int a2 = 0; a2 < this.patternsButtonsContainer.length; a2++) {
                items.add(new ThemeDescription(this.patternsButtonsContainer[a2], 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelShadow"));
                items.add(new ThemeDescription(this.patternsButtonsContainer[a2], 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelBackground"));
            }
            items.add(new ThemeDescription(this.bottomOverlayChat, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelShadow"));
            items.add(new ThemeDescription(this.bottomOverlayChat, 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelBackground"));
            items.add(new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
            for (TextView themeDescription : this.patternsSaveButton) {
                items.add(new ThemeDescription(themeDescription, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
            }
            for (TextView themeDescription2 : this.patternsCancelButton) {
                items.add(new ThemeDescription(themeDescription2, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
            }
            items.add(new ThemeDescription((View) this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"innerPaint1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
            items.add(new ThemeDescription((View) this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"outerPaint1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
            items.add(new ThemeDescription((View) this.intensityCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubble"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleSelected"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgInDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{this.msgOutDrawable, this.msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubble"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{this.msgOutDrawable, this.msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{this.msgOutDrawable, this.msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient2"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{this.msgOutDrawable, this.msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient3"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleSelected"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgOutDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextIn"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextOut"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheck"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckSelected"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckRead"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckReadSelected"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaSentCheck"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyLine"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyLine"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyNameText"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyNameText"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMessageText"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMessageText"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMediaMessageSelectedText"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMediaMessageSelectedText"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeText"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeText"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeSelectedText"));
            items.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeSelectedText"));
        }
        return items;
    }

    /* renamed from: lambda$getThemeDescriptionsInternal$26$org-telegram-ui-ThemePreviewActivity  reason: not valid java name */
    public /* synthetic */ void m3944x2bbe16a4() {
        ActionBarMenuItem actionBarMenuItem = this.dropDownContainer;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
            this.dropDownContainer.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
        }
        Drawable drawable = this.sheetDrawable;
        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhite"), PorterDuff.Mode.MULTIPLY));
        }
    }
}
