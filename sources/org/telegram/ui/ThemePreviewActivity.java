package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_account_getWallPaper;
import org.telegram.tgnet.TLRPC$TL_account_getWallPapers;
import org.telegram.tgnet.TLRPC$TL_account_wallPapers;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageReplyHeader;
import org.telegram.tgnet.TLRPC$TL_peerChat;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_photoSize;
import org.telegram.tgnet.TLRPC$TL_user;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.PatternCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ColorPicker;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.WallpaperCheckBoxView;
import org.telegram.ui.Components.WallpaperParallaxEffect;
import org.telegram.ui.WallpapersListActivity;

public class ThemePreviewActivity extends BaseFragment implements DownloadController.FileDownloadProgressListener, NotificationCenter.NotificationCenterDelegate {
    private int TAG;
    /* access modifiers changed from: private */
    public Theme.ThemeAccent accent;
    /* access modifiers changed from: private */
    public ActionBar actionBar2;
    private Runnable applyColorAction;
    private boolean applyColorScheduled;
    /* access modifiers changed from: private */
    public Theme.ThemeInfo applyingTheme;
    /* access modifiers changed from: private */
    public int backgroundColor;
    /* access modifiers changed from: private */
    public int backgroundGradientColor;
    private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
    /* access modifiers changed from: private */
    public BackupImageView backgroundImage;
    /* access modifiers changed from: private */
    public int backgroundRotation;
    private int backupAccentColor;
    private long backupBackgroundGradientOverrideColor;
    private long backupBackgroundOverrideColor;
    private int backupBackgroundRotation;
    private int backupMyMessagesAccentColor;
    private int backupMyMessagesGradientAccentColor;
    /* access modifiers changed from: private */
    public final PorterDuff.Mode blendMode;
    private Bitmap blurredBitmap;
    /* access modifiers changed from: private */
    public FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    /* access modifiers changed from: private */
    public FrameLayout buttonsContainer;
    private TextView cancelButton;
    /* access modifiers changed from: private */
    public WallpaperCheckBoxView[] checkBoxView;
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
    private TLRPC$TL_wallPaper lastSelectedPattern;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public RecyclerListView listView2;
    /* access modifiers changed from: private */
    public int maxWallpaperSize;
    private MessagesAdapter messagesAdapter;
    /* access modifiers changed from: private */
    public AnimatorSet motionAnimation;
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
    private LinearLayoutManager patternsLayoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView patternsListView;
    private TextView[] patternsSaveButton;
    private int previousBackgroundColor;
    private int previousBackgroundGradientColor;
    private int previousBackgroundRotation;
    private float previousIntensity;
    private TLRPC$TL_wallPaper previousSelectedPattern;
    /* access modifiers changed from: private */
    public boolean progressVisible;
    /* access modifiers changed from: private */
    public RadialProgress2 radialProgress;
    /* access modifiers changed from: private */
    public boolean removeBackgroundOverride;
    private FrameLayout saveButtonsContainer;
    private ActionBarMenuItem saveItem;
    /* access modifiers changed from: private */
    public final int screenType;
    /* access modifiers changed from: private */
    public TLRPC$TL_wallPaper selectedPattern;
    /* access modifiers changed from: private */
    public Drawable sheetDrawable;
    private List<ThemeDescription> themeDescriptions;
    private boolean useDefaultThemeForButtons;
    /* access modifiers changed from: private */
    public ViewPager viewPager;
    /* access modifiers changed from: private */
    public long watchForKeyboardEndTime;

    public interface WallpaperActivityDelegate {
        void didSetNewBackground();
    }

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return false;
    }

    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ThemePreviewActivity() {
        this.applyColorScheduled = false;
        applyColor(this.lastPickedColor, this.lastPickedColorNum);
    }

    public ThemePreviewActivity(Object obj, Bitmap bitmap) {
        this.useDefaultThemeForButtons = true;
        this.colorType = 1;
        this.applyColorAction = new Runnable() {
            public final void run() {
                ThemePreviewActivity.this.lambda$new$0$ThemePreviewActivity();
            }
        };
        this.patternLayout = new FrameLayout[2];
        this.patternsCancelButton = new TextView[2];
        this.patternsSaveButton = new TextView[2];
        this.patternsButtonsContainer = new FrameLayout[2];
        this.currentIntensity = 0.5f;
        this.blendMode = PorterDuff.Mode.SRC_IN;
        this.parallaxScale = 1.0f;
        this.imageFilter = "640_360";
        this.maxWallpaperSize = 1920;
        this.screenType = 2;
        this.currentWallpaper = obj;
        this.currentWallpaperBitmap = bitmap;
        if (obj instanceof WallpapersListActivity.ColorWallpaper) {
            WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) obj;
            this.isMotion = colorWallpaper.motion;
            TLRPC$TL_wallPaper tLRPC$TL_wallPaper = colorWallpaper.pattern;
            this.selectedPattern = tLRPC$TL_wallPaper;
            if (tLRPC$TL_wallPaper != null) {
                this.currentIntensity = colorWallpaper.intensity;
            }
        }
    }

    public ThemePreviewActivity(Theme.ThemeInfo themeInfo) {
        this(themeInfo, false, 0, false, false);
    }

    public ThemePreviewActivity(Theme.ThemeInfo themeInfo, boolean z, int i, boolean z2, boolean z3) {
        this.useDefaultThemeForButtons = true;
        this.colorType = 1;
        this.applyColorAction = new Runnable() {
            public final void run() {
                ThemePreviewActivity.this.lambda$new$0$ThemePreviewActivity();
            }
        };
        this.patternLayout = new FrameLayout[2];
        this.patternsCancelButton = new TextView[2];
        this.patternsSaveButton = new TextView[2];
        this.patternsButtonsContainer = new FrameLayout[2];
        this.currentIntensity = 0.5f;
        this.blendMode = PorterDuff.Mode.SRC_IN;
        this.parallaxScale = 1.0f;
        this.imageFilter = "640_360";
        this.maxWallpaperSize = 1920;
        this.screenType = i;
        this.nightTheme = z3;
        this.applyingTheme = themeInfo;
        this.deleteOnCancel = z;
        this.editingTheme = z2;
        if (i == 1) {
            Theme.ThemeAccent accent2 = themeInfo.getAccent(!z2);
            this.accent = accent2;
            this.useDefaultThemeForButtons = false;
            this.backupAccentColor = accent2.accentColor;
            this.backupMyMessagesAccentColor = accent2.myMessagesAccentColor;
            this.backupMyMessagesGradientAccentColor = accent2.myMessagesGradientAccentColor;
            this.backupBackgroundOverrideColor = accent2.backgroundOverrideColor;
            this.backupBackgroundGradientOverrideColor = accent2.backgroundGradientOverrideColor;
            this.backupBackgroundRotation = accent2.backgroundRotation;
        } else {
            Theme.ThemeAccent accent3 = themeInfo.getAccent(false);
            this.accent = accent3;
            if (accent3 != null) {
                this.selectedPattern = accent3.pattern;
            }
        }
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent != null) {
            this.isMotion = themeAccent.patternMotion;
            if (!TextUtils.isEmpty(themeAccent.patternSlug)) {
                this.currentIntensity = this.accent.patternIntensity;
            }
            Theme.applyThemeTemporary(this.applyingTheme, true);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.goingToPreviewTheme, new Object[0]);
    }

    public void setInitialModes(boolean z, boolean z2) {
        this.isBlurred = z;
        this.isMotion = z2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:110:0x063f  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0645  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x06f9  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0710  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0a5a  */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x0ad9  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x0ade  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x0aeb  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0441  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0451  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0485  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0487  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x048f  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x04b4  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x050e  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x05a8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r34) {
        /*
            r33 = this;
            r6 = r33
            r7 = r34
            r8 = 1
            r6.hasOwnBackground = r8
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.page1 = r0
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            r9 = 0
            r1 = 2131165479(0x7var_, float:1.7945176E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.addItem((int) r9, (int) r1)
            r0.setIsSearchField(r8)
            org.telegram.ui.ThemePreviewActivity$1 r1 = new org.telegram.ui.ThemePreviewActivity$1
            r1.<init>(r6)
            r0.setActionBarMenuItemSearchListener(r1)
            java.lang.String r1 = "Search"
            r2 = 2131627264(0x7f0e0d00, float:1.8881788E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)
            r0.setSearchFieldHint(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.ActionBar.MenuDrawable r1 = new org.telegram.ui.ActionBar.MenuDrawable
            r1.<init>()
            r0.setBackButtonDrawable(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r0.setAddToContainer(r9)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            java.lang.String r1 = "ThemePreview"
            r2 = 2131627711(0x7f0e0ebf, float:1.8882694E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)
            r0.setTitle(r1)
            org.telegram.ui.ThemePreviewActivity$2 r0 = new org.telegram.ui.ThemePreviewActivity$2
            r0.<init>(r7)
            r6.page1 = r0
            java.lang.String r10 = "windowBackgroundWhite"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r0.setBackgroundColor(r1)
            android.widget.FrameLayout r0 = r6.page1
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            r11 = -1
            r2 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r2)
            r0.addView(r1, r2)
            org.telegram.ui.Components.RecyclerListView r0 = new org.telegram.ui.Components.RecyclerListView
            r0.<init>(r7)
            r6.listView = r0
            r0.setVerticalScrollBarEnabled(r8)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            r12 = 0
            r0.setItemAnimator(r12)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            r0.setLayoutAnimation(r12)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            androidx.recyclerview.widget.LinearLayoutManager r1 = new androidx.recyclerview.widget.LinearLayoutManager
            r1.<init>(r7, r8, r9)
            r0.setLayoutManager(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            r13 = 2
            if (r1 == 0) goto L_0x0097
            r1 = 1
            goto L_0x0098
        L_0x0097:
            r1 = 2
        L_0x0098:
            r0.setVerticalScrollbarPosition(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            int r1 = r6.screenType
            if (r1 == 0) goto L_0x00a4
            r1 = 1094713344(0x41400000, float:12.0)
            goto L_0x00a5
        L_0x00a4:
            r1 = 0
        L_0x00a5:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setPadding(r9, r9, r9, r1)
            android.widget.FrameLayout r0 = r6.page1
            org.telegram.ui.Components.RecyclerListView r1 = r6.listView
            r5 = 51
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r11, r5)
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
            if (r1 >= r2) goto L_0x010d
            android.content.res.Resources r3 = r34.getResources()
            r4 = 2131165419(0x7var_eb, float:1.7945055E38)
            android.graphics.drawable.Drawable r3 = r3.getDrawable(r4)
            android.graphics.drawable.Drawable r3 = r3.mutate()
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r14 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r5, r14)
            r3.setColorFilter(r4)
            org.telegram.ui.Components.CombinedDrawable r4 = new org.telegram.ui.Components.CombinedDrawable
            r4.<init>(r3, r0, r9, r9)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r4.setIconSize(r0, r3)
            r0 = r4
        L_0x010d:
            android.widget.ImageView r3 = r6.floatingButton
            r3.setBackgroundDrawable(r0)
            android.widget.ImageView r0 = r6.floatingButton
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            java.lang.String r4 = "chats_actionIcon"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r4, r5)
            r0.setColorFilter(r3)
            android.widget.ImageView r0 = r6.floatingButton
            r3 = 2131165418(0x7var_ea, float:1.7945053E38)
            r0.setImageResource(r3)
            r14 = 1082130432(0x40800000, float:4.0)
            if (r1 < r2) goto L_0x0193
            android.animation.StateListAnimator r0 = new android.animation.StateListAnimator
            r0.<init>()
            int[] r3 = new int[r8]
            r4 = 16842919(0x10100a7, float:2.3694026E-38)
            r3[r9] = r4
            android.widget.ImageView r4 = r6.floatingButton
            float[] r5 = new float[r13]
            r17 = 1073741824(0x40000000, float:2.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r17)
            float r15 = (float) r15
            r5[r9] = r15
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r15 = (float) r15
            r5[r8] = r15
            java.lang.String r15 = "translationZ"
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r15, r5)
            r11 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r4 = r4.setDuration(r11)
            r0.addState(r3, r4)
            int[] r3 = new int[r9]
            android.widget.ImageView r4 = r6.floatingButton
            float[] r5 = new float[r13]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r11 = (float) r11
            r5[r9] = r11
            r11 = 1073741824(0x40000000, float:2.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r5[r8] = r11
            java.lang.String r11 = "translationZ"
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r11, r5)
            r11 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r4 = r4.setDuration(r11)
            r0.addState(r3, r4)
            android.widget.ImageView r3 = r6.floatingButton
            r3.setStateListAnimator(r0)
            android.widget.ImageView r0 = r6.floatingButton
            org.telegram.ui.ThemePreviewActivity$3 r3 = new org.telegram.ui.ThemePreviewActivity$3
            r3.<init>(r6)
            r0.setOutlineProvider(r3)
        L_0x0193:
            android.widget.FrameLayout r0 = r6.page1
            android.widget.ImageView r3 = r6.floatingButton
            if (r1 < r2) goto L_0x019e
            r4 = 56
            r18 = 56
            goto L_0x01a2
        L_0x019e:
            r4 = 60
            r18 = 60
        L_0x01a2:
            if (r1 < r2) goto L_0x01a7
            r19 = 1113587712(0x42600000, float:56.0)
            goto L_0x01ab
        L_0x01a7:
            r1 = 1114636288(0x42700000, float:60.0)
            r19 = 1114636288(0x42700000, float:60.0)
        L_0x01ab:
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            r11 = 3
            if (r1 == 0) goto L_0x01b2
            r2 = 3
            goto L_0x01b3
        L_0x01b2:
            r2 = 5
        L_0x01b3:
            r12 = 80
            r20 = r2 | 80
            r5 = 1096810496(0x41600000, float:14.0)
            if (r1 == 0) goto L_0x01be
            r21 = 1096810496(0x41600000, float:14.0)
            goto L_0x01c0
        L_0x01be:
            r21 = 0
        L_0x01c0:
            r22 = 0
            if (r1 == 0) goto L_0x01c7
            r23 = 0
            goto L_0x01c9
        L_0x01c7:
            r23 = 1096810496(0x41600000, float:14.0)
        L_0x01c9:
            r24 = 1096810496(0x41600000, float:14.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r0.addView(r3, r1)
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
            org.telegram.ui.ActionBar.ActionBar r0 = r33.createActionBar(r34)
            r6.actionBar2 = r0
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
            java.lang.Object r0 = r6.currentWallpaper
            boolean r1 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r1 == 0) goto L_0x0213
            r1 = 3
            goto L_0x0214
        L_0x0213:
            r1 = 2
        L_0x0214:
            boolean r2 = r0 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper
            if (r2 == 0) goto L_0x0226
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r0
            java.lang.String r0 = r0.slug
            java.lang.String r2 = "t"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0226
            r4 = 0
            goto L_0x0227
        L_0x0226:
            r4 = r1
        L_0x0227:
            android.widget.FrameLayout r0 = r6.page2
            org.telegram.ui.Components.BackupImageView r1 = r6.backgroundImage
            r18 = -1
            r19 = -1082130432(0xffffffffbvar_, float:-1.0)
            r20 = 51
            r21 = 0
            r22 = 0
            r23 = 0
            r24 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r0.addView(r1, r2)
            int r0 = r6.screenType
            if (r0 != r13) goto L_0x0252
            org.telegram.ui.Components.BackupImageView r0 = r6.backgroundImage
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$3DklAbGOKEnRLGlO1ah2mYRlhK4 r1 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$3DklAbGOKEnRLGlO1ah2mYRlhK4
            r1.<init>()
            r0.setDelegate(r1)
        L_0x0252:
            org.telegram.ui.ThemePreviewActivity$MessagesAdapter r0 = r6.messagesAdapter
            boolean r0 = r0.showSecretMessages
            java.lang.String r18 = "fonts/rmedium.ttf"
            if (r0 == 0) goto L_0x0278
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            java.lang.String r1 = "Telegram Beta Chat"
            r0.setTitle(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            r1 = 505(0x1f9, float:7.08E-43)
            java.lang.String r2 = "Members"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
            r0.setSubtitle(r1)
        L_0x0270:
            r25 = r4
            r12 = 51
            r19 = 1096810496(0x41600000, float:14.0)
            goto L_0x0423
        L_0x0278:
            int r0 = r6.screenType
            if (r0 != r13) goto L_0x02a2
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            r1 = 2131624525(0x7f0e024d, float:1.8876232E38)
            java.lang.String r2 = "BackgroundPreview"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            java.lang.Object r0 = r6.currentWallpaper
            boolean r1 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r1 != 0) goto L_0x0294
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            if (r0 == 0) goto L_0x0270
        L_0x0294:
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            r1 = 5
            r2 = 2131165525(0x7var_, float:1.794527E38)
            r0.addItem((int) r1, (int) r2)
            goto L_0x0270
        L_0x02a2:
            if (r0 != r8) goto L_0x03d6
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            org.telegram.ui.ActionBar.ActionBarMenu r3 = r0.createMenu()
            r0 = 4
            r1 = 2131627242(0x7f0e0cea, float:1.8881743E38)
            java.lang.String r2 = "Save"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r1 = r1.toUpperCase()
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r3.addItem((int) r0, (java.lang.CharSequence) r1)
            r6.saveItem = r0
            org.telegram.ui.ThemePreviewActivity$7 r2 = new org.telegram.ui.ThemePreviewActivity$7
            r19 = 0
            r20 = 0
            r0 = r2
            r1 = r33
            r15 = r2
            r2 = r34
            r25 = r4
            r4 = r19
            r12 = 51
            r19 = 1096810496(0x41600000, float:14.0)
            r5 = r20
            r0.<init>(r2, r3, r4, r5)
            r6.dropDownContainer = r15
            r15.setSubMenuOpenSide(r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.dropDownContainer
            r1 = 2131624922(0x7f0e03da, float:1.8877037E38)
            java.lang.String r2 = "ColorPickerMainColor"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.addSubItem(r8, r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.dropDownContainer
            r1 = 2131624921(0x7f0e03d9, float:1.8877035E38)
            java.lang.String r2 = "ColorPickerBackground"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.addSubItem(r13, r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.dropDownContainer
            r1 = 2131624923(0x7f0e03db, float:1.887704E38)
            java.lang.String r2 = "ColorPickerMyMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.addSubItem(r11, r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.dropDownContainer
            r0.setAllowCloseAnimation(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.dropDownContainer
            r0.setForceSmoothKeyboard(r8)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.dropDownContainer
            r26 = -2
            r27 = -1082130432(0xffffffffbvar_, float:-1.0)
            r28 = 51
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x0325
            r2 = 1115684864(0x42800000, float:64.0)
            r29 = 1115684864(0x42800000, float:64.0)
            goto L_0x0327
        L_0x0325:
            r29 = 1113587712(0x42600000, float:56.0)
        L_0x0327:
            r30 = 0
            r31 = 1109393408(0x42200000, float:40.0)
            r32 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r0.addView(r1, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.dropDownContainer
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$3Ub5xDYSZaUSX-jPrJclhg9PrhI r1 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$3Ub5xDYSZaUSX-jPrJclhg9PrhI
            r1.<init>()
            r0.setOnClickListener(r1)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.dropDown = r0
            r0.setImportantForAccessibility(r13)
            android.widget.TextView r0 = r6.dropDown
            r0.setGravity(r11)
            android.widget.TextView r0 = r6.dropDown
            r0.setSingleLine(r8)
            android.widget.TextView r0 = r6.dropDown
            r0.setLines(r8)
            android.widget.TextView r0 = r6.dropDown
            r0.setMaxLines(r8)
            android.widget.TextView r0 = r6.dropDown
            android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.END
            r0.setEllipsize(r1)
            android.widget.TextView r0 = r6.dropDown
            java.lang.String r1 = "actionBarDefaultTitle"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r6.dropDown
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r18)
            r0.setTypeface(r1)
            android.widget.TextView r0 = r6.dropDown
            r1 = 2131624922(0x7f0e03da, float:1.8877037E38)
            java.lang.String r2 = "ColorPickerMainColor"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            android.content.res.Resources r0 = r34.getResources()
            r1 = 2131165487(0x7var_f, float:1.7945193E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r2 = "actionBarDefaultTitle"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r2, r3)
            r0.setColorFilter(r1)
            android.widget.TextView r1 = r6.dropDown
            r2 = 0
            r1.setCompoundDrawablesWithIntrinsicBounds(r2, r2, r0, r2)
            android.widget.TextView r0 = r6.dropDown
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r0.setCompoundDrawablePadding(r1)
            android.widget.TextView r0 = r6.dropDown
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setPadding(r9, r9, r1, r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.dropDownContainer
            android.widget.TextView r1 = r6.dropDown
            r26 = -2
            r27 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r28 = 16
            r29 = 1098907648(0x41800000, float:16.0)
            r31 = 0
            r32 = 1065353216(0x3var_, float:1.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r0.addView(r1, r2)
            goto L_0x0423
        L_0x03d6:
            r25 = r4
            r12 = 51
            r19 = 1096810496(0x41600000, float:14.0)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r6.applyingTheme
            org.telegram.tgnet.TLRPC$TL_theme r1 = r0.info
            if (r1 == 0) goto L_0x03e5
            java.lang.String r0 = r1.title
            goto L_0x03e9
        L_0x03e5:
            java.lang.String r0 = r0.getName()
        L_0x03e9:
            java.lang.String r1 = ".attheme"
            int r1 = r0.lastIndexOf(r1)
            if (r1 < 0) goto L_0x03f5
            java.lang.String r0 = r0.substring(r9, r1)
        L_0x03f5:
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar2
            r1.setTitle(r0)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r6.applyingTheme
            org.telegram.tgnet.TLRPC$TL_theme r0 = r0.info
            if (r0 == 0) goto L_0x0410
            int r0 = r0.installs_count
            if (r0 <= 0) goto L_0x0410
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar2
            java.lang.String r2 = "ThemeInstallCount"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            r1.setSubtitle(r0)
            goto L_0x0423
        L_0x0410:
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar2
            long r1 = java.lang.System.currentTimeMillis()
            r3 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 / r3
            r3 = 3600(0xe10, double:1.7786E-320)
            long r1 = r1 - r3
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatDateOnline(r1)
            r0.setSubtitle(r1)
        L_0x0423:
            org.telegram.ui.ThemePreviewActivity$8 r0 = new org.telegram.ui.ThemePreviewActivity$8
            r0.<init>(r7)
            r6.listView2 = r0
            androidx.recyclerview.widget.RecyclerView$ItemAnimator r0 = r0.getItemAnimator()
            androidx.recyclerview.widget.DefaultItemAnimator r0 = (androidx.recyclerview.widget.DefaultItemAnimator) r0
            r0.setDelayAnimations(r9)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            r0.setVerticalScrollBarEnabled(r8)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            r0.setOverScrollMode(r13)
            int r0 = r6.screenType
            if (r0 != r13) goto L_0x0451
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r2 = 1112539136(0x42500000, float:52.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r9, r1, r9, r2)
            goto L_0x0470
        L_0x0451:
            if (r0 != r8) goto L_0x0463
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r9, r1, r9, r2)
            goto L_0x0470
        L_0x0463:
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r0.setPadding(r9, r1, r9, r2)
        L_0x0470:
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            r0.setClipToPadding(r9)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            androidx.recyclerview.widget.LinearLayoutManager r1 = new androidx.recyclerview.widget.LinearLayoutManager
            r1.<init>(r7, r8, r8)
            r0.setLayoutManager(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x0487
            r1 = 1
            goto L_0x0488
        L_0x0487:
            r1 = 2
        L_0x0488:
            r0.setVerticalScrollbarPosition(r1)
            int r0 = r6.screenType
            if (r0 != r8) goto L_0x04b4
            android.widget.FrameLayout r0 = r6.page2
            org.telegram.ui.Components.RecyclerListView r1 = r6.listView2
            r26 = -1
            r27 = -1082130432(0xffffffffbvar_, float:-1.0)
            r28 = 51
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 1133707264(0x43930000, float:294.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r26, r27, r28, r29, r30, r31, r32)
            r0.addView(r1, r2)
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$IeR7ZrpLnhauHFlY_dTbimRzJvw r1 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$IeR7ZrpLnhauHFlY_dTbimRzJvw
            r1.<init>()
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r1)
            r2 = -1
            goto L_0x04c0
        L_0x04b4:
            android.widget.FrameLayout r0 = r6.page2
            org.telegram.ui.Components.RecyclerListView r1 = r6.listView2
            r2 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r2, r12)
            r0.addView(r1, r3)
        L_0x04c0:
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            org.telegram.ui.ThemePreviewActivity$9 r1 = new org.telegram.ui.ThemePreviewActivity$9
            r1.<init>()
            r0.setOnScrollListener(r1)
            android.widget.FrameLayout r0 = r6.page2
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar2
            r3 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3)
            r0.addView(r1, r3)
            org.telegram.ui.Components.WallpaperParallaxEffect r0 = new org.telegram.ui.Components.WallpaperParallaxEffect
            r0.<init>(r7)
            r6.parallaxEffect = r0
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$ABmg6S5ZDUW-jEfnoHQAUjq33TE r1 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$ABmg6S5ZDUW-jEfnoHQAUjq33TE
            r1.<init>()
            r0.setCallback(r1)
            int r0 = r6.screenType
            java.lang.String r2 = "chat_fieldOverlayText"
            r3 = 17
            r4 = -2
            if (r0 == r8) goto L_0x04f6
            if (r0 != r13) goto L_0x04f2
            goto L_0x04f6
        L_0x04f2:
            r24 = r10
            goto L_0x0a7d
        L_0x04f6:
            org.telegram.ui.Components.RadialProgress2 r0 = new org.telegram.ui.Components.RadialProgress2
            org.telegram.ui.Components.BackupImageView r5 = r6.backgroundImage
            r0.<init>(r5)
            r6.radialProgress = r0
            java.lang.String r5 = "chat_serviceBackground"
            java.lang.String r11 = "chat_serviceBackground"
            java.lang.String r14 = "chat_serviceText"
            java.lang.String r15 = "chat_serviceText"
            r0.setColors((java.lang.String) r5, (java.lang.String) r11, (java.lang.String) r14, (java.lang.String) r15)
            int r0 = r6.screenType
            if (r0 != r13) goto L_0x0573
            org.telegram.ui.ThemePreviewActivity$10 r0 = new org.telegram.ui.ThemePreviewActivity$10
            r0.<init>(r6, r7)
            r6.bottomOverlayChat = r0
            r0.setWillNotDraw(r9)
            android.widget.FrameLayout r0 = r6.bottomOverlayChat
            r5 = 1077936128(0x40400000, float:3.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r0.setPadding(r9, r5, r9, r9)
            android.widget.FrameLayout r0 = r6.page2
            android.widget.FrameLayout r5 = r6.bottomOverlayChat
            r11 = 80
            r14 = -1
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r12, r11)
            r11 = r15
            r0.addView(r5, r11)
            android.widget.FrameLayout r0 = r6.bottomOverlayChat
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$NXtlW056EBGd-1j0JY7lIBFFmJc r5 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$NXtlW056EBGd-1j0JY7lIBFFmJc
            r5.<init>()
            r0.setOnClickListener(r5)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.bottomOverlayChatText = r0
            r5 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r8, r5)
            android.widget.TextView r0 = r6.bottomOverlayChatText
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r18)
            r0.setTypeface(r5)
            android.widget.TextView r0 = r6.bottomOverlayChatText
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setTextColor(r5)
            android.widget.TextView r0 = r6.bottomOverlayChatText
            r5 = 2131627398(0x7f0e0d86, float:1.888206E38)
            java.lang.String r11 = "SetBackground"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r5)
            r0.setText(r5)
            android.widget.FrameLayout r0 = r6.bottomOverlayChat
            android.widget.TextView r5 = r6.bottomOverlayChatText
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r4, r3)
            r0.addView(r5, r11)
        L_0x0573:
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            android.content.res.Resources r5 = r34.getResources()
            r11 = 2131166018(0x7var_, float:1.794627E38)
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r11)
            android.graphics.drawable.Drawable r5 = r5.mutate()
            r6.sheetDrawable = r5
            r5.getPadding(r0)
            android.graphics.drawable.Drawable r5 = r6.sheetDrawable
            android.graphics.PorterDuffColorFilter r11 = new android.graphics.PorterDuffColorFilter
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            android.graphics.PorterDuff$Mode r15 = android.graphics.PorterDuff.Mode.MULTIPLY
            r11.<init>(r14, r15)
            r5.setColorFilter(r11)
            r5 = r25
            java.lang.String[] r11 = new java.lang.String[r5]
            int[] r14 = new int[r5]
            org.telegram.ui.Components.WallpaperCheckBoxView[] r15 = new org.telegram.ui.Components.WallpaperCheckBoxView[r5]
            r6.checkBoxView = r15
            if (r5 == 0) goto L_0x063f
            android.widget.FrameLayout r15 = new android.widget.FrameLayout
            r15.<init>(r7)
            r6.buttonsContainer = r15
            int r15 = r6.screenType
            if (r15 != r8) goto L_0x05ca
            r15 = 2131624523(0x7f0e024b, float:1.8876228E38)
            java.lang.String r3 = "BackgroundMotion"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r15)
            r11[r9] = r3
            r3 = 2131624524(0x7f0e024c, float:1.887623E38)
            java.lang.String r15 = "BackgroundPattern"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            r11[r8] = r3
            goto L_0x0608
        L_0x05ca:
            java.lang.Object r3 = r6.currentWallpaper
            boolean r3 = r3 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r3 == 0) goto L_0x05f2
            r3 = 2131624516(0x7f0e0244, float:1.8876214E38)
            java.lang.String r15 = "BackgroundColor"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            r11[r9] = r3
            r3 = 2131624524(0x7f0e024c, float:1.887623E38)
            java.lang.String r15 = "BackgroundPattern"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            r11[r8] = r3
            r3 = 2131624523(0x7f0e024b, float:1.8876228E38)
            java.lang.String r15 = "BackgroundMotion"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            r11[r13] = r3
            goto L_0x0608
        L_0x05f2:
            r3 = 2131624513(0x7f0e0241, float:1.8876208E38)
            java.lang.String r15 = "BackgroundBlurred"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            r11[r9] = r3
            r3 = 2131624523(0x7f0e024b, float:1.8876228E38)
            java.lang.String r15 = "BackgroundMotion"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            r11[r8] = r3
        L_0x0608:
            android.text.TextPaint r3 = new android.text.TextPaint
            r3.<init>(r8)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r19)
            float r15 = (float) r15
            r3.setTextSize(r15)
            android.graphics.Typeface r15 = org.telegram.messenger.AndroidUtilities.getTypeface(r18)
            r3.setTypeface(r15)
            r12 = 0
            r15 = 0
        L_0x061e:
            if (r15 >= r5) goto L_0x063c
            r1 = r11[r15]
            float r1 = r3.measureText(r1)
            r24 = r10
            double r9 = (double) r1
            double r9 = java.lang.Math.ceil(r9)
            int r1 = (int) r9
            r14[r15] = r1
            r1 = r14[r15]
            int r12 = java.lang.Math.max(r12, r1)
            int r15 = r15 + 1
            r10 = r24
            r9 = 0
            goto L_0x061e
        L_0x063c:
            r24 = r10
            goto L_0x0642
        L_0x063f:
            r24 = r10
            r12 = 0
        L_0x0642:
            r1 = 0
        L_0x0643:
            if (r1 >= r5) goto L_0x06f4
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r6.checkBoxView
            org.telegram.ui.Components.WallpaperCheckBoxView r9 = new org.telegram.ui.Components.WallpaperCheckBoxView
            int r10 = r6.screenType
            if (r10 == r8) goto L_0x0658
            java.lang.Object r10 = r6.currentWallpaper
            boolean r10 = r10 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r10 == 0) goto L_0x0658
            if (r1 == 0) goto L_0x0656
            goto L_0x0658
        L_0x0656:
            r10 = 0
            goto L_0x0659
        L_0x0658:
            r10 = 1
        L_0x0659:
            r9.<init>(r7, r10)
            r3[r1] = r9
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r6.checkBoxView
            r3 = r3[r1]
            int r9 = r6.backgroundColor
            r3.setBackgroundColor(r9)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r6.checkBoxView
            r3 = r3[r1]
            r9 = r11[r1]
            r10 = r14[r1]
            r3.setText(r9, r10, r12)
            int r3 = r6.screenType
            if (r3 == r8) goto L_0x06aa
            java.lang.Object r3 = r6.currentWallpaper
            boolean r3 = r3 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r3 == 0) goto L_0x069b
            if (r1 != r8) goto L_0x068e
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r6.checkBoxView
            r3 = r3[r1]
            org.telegram.tgnet.TLRPC$TL_wallPaper r9 = r6.selectedPattern
            if (r9 == 0) goto L_0x0688
            r9 = 1
            goto L_0x0689
        L_0x0688:
            r9 = 0
        L_0x0689:
            r10 = 0
            r3.setChecked(r9, r10)
            goto L_0x06aa
        L_0x068e:
            r10 = 0
            if (r1 != r13) goto L_0x06aa
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r6.checkBoxView
            r3 = r3[r1]
            boolean r9 = r6.isMotion
            r3.setChecked(r9, r10)
            goto L_0x06aa
        L_0x069b:
            r10 = 0
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r6.checkBoxView
            r3 = r3[r1]
            if (r1 != 0) goto L_0x06a5
            boolean r9 = r6.isBlurred
            goto L_0x06a7
        L_0x06a5:
            boolean r9 = r6.isMotion
        L_0x06a7:
            r3.setChecked(r9, r10)
        L_0x06aa:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r3 = r3 + r12
            android.widget.FrameLayout$LayoutParams r9 = new android.widget.FrameLayout$LayoutParams
            r9.<init>(r3, r4)
            r10 = 19
            r9.gravity = r10
            if (r1 != r8) goto L_0x06c2
            r10 = 1091567616(0x41100000, float:9.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r3 = r3 + r10
            goto L_0x06c3
        L_0x06c2:
            r3 = 0
        L_0x06c3:
            r9.leftMargin = r3
            android.widget.FrameLayout r3 = r6.buttonsContainer
            org.telegram.ui.Components.WallpaperCheckBoxView[] r10 = r6.checkBoxView
            r10 = r10[r1]
            r3.addView(r10, r9)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r6.checkBoxView
            r9 = r3[r1]
            r3 = r3[r1]
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$Zkb4RoK82_TsWuCaUwahmVvROlg r10 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$Zkb4RoK82_TsWuCaUwahmVvROlg
            r10.<init>(r1, r9)
            r3.setOnClickListener(r10)
            if (r1 != r13) goto L_0x06ef
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r6.checkBoxView
            r3 = r3[r1]
            r9 = 0
            r3.setAlpha(r9)
            org.telegram.ui.Components.WallpaperCheckBoxView[] r3 = r6.checkBoxView
            r3 = r3[r1]
            r10 = 4
            r3.setVisibility(r10)
            goto L_0x06f0
        L_0x06ef:
            r9 = 0
        L_0x06f0:
            int r1 = r1 + 1
            goto L_0x0643
        L_0x06f4:
            r9 = 0
            int r1 = r6.screenType
            if (r1 != r8) goto L_0x06fc
            r33.updateCheckboxes()
        L_0x06fc:
            int r1 = r6.screenType
            if (r1 == r8) goto L_0x070a
            java.lang.Object r1 = r6.currentWallpaper
            boolean r1 = r1 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r1 == 0) goto L_0x0707
            goto L_0x070a
        L_0x0707:
            r0 = 0
            goto L_0x0a4b
        L_0x070a:
            r1 = 0
            r6.isBlurred = r1
            r1 = 0
        L_0x070e:
            if (r1 >= r13) goto L_0x0707
            android.widget.FrameLayout[] r3 = r6.patternLayout
            org.telegram.ui.ThemePreviewActivity$11 r5 = new org.telegram.ui.ThemePreviewActivity$11
            r5.<init>(r7, r1, r0)
            r3[r1] = r5
            if (r1 == r8) goto L_0x071f
            int r3 = r6.screenType
            if (r3 != r13) goto L_0x0727
        L_0x071f:
            android.widget.FrameLayout[] r3 = r6.patternLayout
            r3 = r3[r1]
            r5 = 4
            r3.setVisibility(r5)
        L_0x0727:
            android.widget.FrameLayout[] r3 = r6.patternLayout
            r3 = r3[r1]
            r5 = 0
            r3.setWillNotDraw(r5)
            int r3 = r6.screenType
            if (r3 != r13) goto L_0x0742
            if (r1 != 0) goto L_0x0738
            r3 = 342(0x156, float:4.79E-43)
            goto L_0x073a
        L_0x0738:
            r3 = 316(0x13c, float:4.43E-43)
        L_0x073a:
            r5 = 83
            r10 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r3, r5)
            goto L_0x0750
        L_0x0742:
            r5 = 83
            r10 = -1
            if (r1 != 0) goto L_0x074a
            r3 = 294(0x126, float:4.12E-43)
            goto L_0x074c
        L_0x074a:
            r3 = 316(0x13c, float:4.43E-43)
        L_0x074c:
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r3, r5)
        L_0x0750:
            if (r1 != 0) goto L_0x0770
            int r5 = r3.height
            r10 = 1094713344(0x41400000, float:12.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r12 = r0.top
            int r11 = r11 + r12
            int r5 = r5 + r11
            r3.height = r5
            android.widget.FrameLayout[] r5 = r6.patternLayout
            r5 = r5[r1]
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r10)
            int r12 = r0.top
            int r11 = r11 + r12
            r12 = 0
            r5.setPadding(r12, r11, r12, r12)
            goto L_0x0772
        L_0x0770:
            r10 = 1094713344(0x41400000, float:12.0)
        L_0x0772:
            android.widget.FrameLayout r5 = r6.page2
            android.widget.FrameLayout[] r11 = r6.patternLayout
            r11 = r11[r1]
            r5.addView(r11, r3)
            r3 = 1101529088(0x41a80000, float:21.0)
            if (r1 == r8) goto L_0x0788
            int r5 = r6.screenType
            if (r5 != r13) goto L_0x0784
            goto L_0x0788
        L_0x0784:
            r12 = 80
            goto L_0x08ca
        L_0x0788:
            android.widget.FrameLayout[] r5 = r6.patternsButtonsContainer
            org.telegram.ui.ThemePreviewActivity$12 r11 = new org.telegram.ui.ThemePreviewActivity$12
            r11.<init>(r6, r7)
            r5[r1] = r11
            android.widget.FrameLayout[] r5 = r6.patternsButtonsContainer
            r5 = r5[r1]
            r11 = 0
            r5.setWillNotDraw(r11)
            android.widget.FrameLayout[] r5 = r6.patternsButtonsContainer
            r5 = r5[r1]
            r12 = 1077936128(0x40400000, float:3.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r5.setPadding(r11, r12, r11, r11)
            android.widget.FrameLayout[] r5 = r6.patternsButtonsContainer
            r5 = r5[r1]
            r5.setClickable(r8)
            android.widget.FrameLayout[] r5 = r6.patternLayout
            r5 = r5[r1]
            android.widget.FrameLayout[] r11 = r6.patternsButtonsContainer
            r11 = r11[r1]
            r12 = 80
            r14 = 51
            r15 = -1
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r14, r12)
            r5.addView(r11, r9)
            android.widget.TextView[] r5 = r6.patternsCancelButton
            android.widget.TextView r9 = new android.widget.TextView
            r9.<init>(r7)
            r5[r1] = r9
            android.widget.TextView[] r5 = r6.patternsCancelButton
            r5 = r5[r1]
            r9 = 1097859072(0x41700000, float:15.0)
            r5.setTextSize(r8, r9)
            android.widget.TextView[] r5 = r6.patternsCancelButton
            r5 = r5[r1]
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r18)
            r5.setTypeface(r9)
            android.widget.TextView[] r5 = r6.patternsCancelButton
            r5 = r5[r1]
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r5.setTextColor(r9)
            android.widget.TextView[] r5 = r6.patternsCancelButton
            r5 = r5[r1]
            r9 = 2131624639(0x7f0e02bf, float:1.8876463E38)
            java.lang.String r11 = "Cancel"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            java.lang.String r9 = r9.toUpperCase()
            r5.setText(r9)
            android.widget.TextView[] r5 = r6.patternsCancelButton
            r5 = r5[r1]
            r9 = 17
            r5.setGravity(r9)
            android.widget.TextView[] r5 = r6.patternsCancelButton
            r5 = r5[r1]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r14 = 0
            r5.setPadding(r9, r14, r11, r14)
            android.widget.TextView[] r5 = r6.patternsCancelButton
            r5 = r5[r1]
            java.lang.String r9 = "listSelectorSDK21"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r9, r14)
            r5.setBackgroundDrawable(r9)
            android.widget.FrameLayout[] r5 = r6.patternsButtonsContainer
            r5 = r5[r1]
            android.widget.TextView[] r9 = r6.patternsCancelButton
            r9 = r9[r1]
            r11 = 51
            r14 = -1
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r14, r11)
            r11 = r15
            r5.addView(r9, r11)
            android.widget.TextView[] r5 = r6.patternsCancelButton
            r5 = r5[r1]
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$D5ZZLdzfkbC-dsmokOSacZTVr64 r9 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$D5ZZLdzfkbC-dsmokOSacZTVr64
            r9.<init>(r1)
            r5.setOnClickListener(r9)
            android.widget.TextView[] r5 = r6.patternsSaveButton
            android.widget.TextView r9 = new android.widget.TextView
            r9.<init>(r7)
            r5[r1] = r9
            android.widget.TextView[] r5 = r6.patternsSaveButton
            r5 = r5[r1]
            r9 = 1097859072(0x41700000, float:15.0)
            r5.setTextSize(r8, r9)
            android.widget.TextView[] r5 = r6.patternsSaveButton
            r5 = r5[r1]
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r18)
            r5.setTypeface(r9)
            android.widget.TextView[] r5 = r6.patternsSaveButton
            r5 = r5[r1]
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r5.setTextColor(r9)
            android.widget.TextView[] r5 = r6.patternsSaveButton
            r5 = r5[r1]
            r9 = 2131624286(0x7f0e015e, float:1.8875747E38)
            java.lang.String r11 = "ApplyTheme"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            java.lang.String r9 = r9.toUpperCase()
            r5.setText(r9)
            android.widget.TextView[] r5 = r6.patternsSaveButton
            r5 = r5[r1]
            r9 = 17
            r5.setGravity(r9)
            android.widget.TextView[] r5 = r6.patternsSaveButton
            r5 = r5[r1]
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r14 = 0
            r5.setPadding(r9, r14, r11, r14)
            android.widget.TextView[] r5 = r6.patternsSaveButton
            r5 = r5[r1]
            java.lang.String r9 = "listSelectorSDK21"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r9, r14)
            r5.setBackgroundDrawable(r9)
            android.widget.FrameLayout[] r5 = r6.patternsButtonsContainer
            r5 = r5[r1]
            android.widget.TextView[] r9 = r6.patternsSaveButton
            r9 = r9[r1]
            r11 = 53
            r14 = -1
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r14, r11)
            r5.addView(r9, r11)
            android.widget.TextView[] r5 = r6.patternsSaveButton
            r5 = r5[r1]
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$RFrQ-_KR8FyrO3Hmtb8A-thk6yU r9 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$RFrQ-_KR8FyrO3Hmtb8A-thk6yU
            r9.<init>(r1)
            r5.setOnClickListener(r9)
        L_0x08ca:
            if (r1 != r8) goto L_0x09cc
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r7)
            r5.setLines(r8)
            r5.setSingleLine(r8)
            r9 = 2131624515(0x7f0e0243, float:1.8876212E38)
            java.lang.String r11 = "BackgroundChoosePattern"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r5.setText(r9)
            java.lang.String r9 = "windowBackgroundWhiteBlackText"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r5.setTextColor(r9)
            r9 = 1101004800(0x41a00000, float:20.0)
            r5.setTextSize(r8, r9)
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r18)
            r5.setTypeface(r9)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r11 = 1086324736(0x40CLASSNAME, float:6.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r14 = 1090519040(0x41000000, float:8.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r5.setPadding(r9, r11, r3, r14)
            android.text.TextUtils$TruncateAt r3 = android.text.TextUtils.TruncateAt.MIDDLE
            r5.setEllipsize(r3)
            r3 = 16
            r5.setGravity(r3)
            android.widget.FrameLayout[] r3 = r6.patternLayout
            r3 = r3[r1]
            r25 = -1
            r26 = 1111490560(0x42400000, float:48.0)
            r27 = 51
            r28 = 0
            r29 = 1101529088(0x41a80000, float:21.0)
            r30 = 0
            r31 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r3.addView(r5, r9)
            org.telegram.ui.ThemePreviewActivity$13 r3 = new org.telegram.ui.ThemePreviewActivity$13
            r3.<init>(r6, r7)
            r6.patternsListView = r3
            androidx.recyclerview.widget.LinearLayoutManager r5 = new androidx.recyclerview.widget.LinearLayoutManager
            r9 = 0
            r5.<init>(r7, r9, r9)
            r6.patternsLayoutManager = r5
            r3.setLayoutManager(r5)
            org.telegram.ui.Components.RecyclerListView r3 = r6.patternsListView
            org.telegram.ui.ThemePreviewActivity$PatternsAdapter r5 = new org.telegram.ui.ThemePreviewActivity$PatternsAdapter
            r5.<init>(r7)
            r6.patternsAdapter = r5
            r3.setAdapter(r5)
            org.telegram.ui.Components.RecyclerListView r3 = r6.patternsListView
            org.telegram.ui.ThemePreviewActivity$14 r5 = new org.telegram.ui.ThemePreviewActivity$14
            r5.<init>(r6)
            r3.addItemDecoration(r5)
            android.widget.FrameLayout[] r3 = r6.patternLayout
            r3 = r3[r1]
            org.telegram.ui.Components.RecyclerListView r5 = r6.patternsListView
            r26 = 1120403456(0x42CLASSNAME, float:100.0)
            r29 = 1117257728(0x42980000, float:76.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r3.addView(r5, r9)
            org.telegram.ui.Components.RecyclerListView r3 = r6.patternsListView
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$ROv1UVuUNXvx6q2zGvg8pqr-1P0 r5 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$ROv1UVuUNXvx6q2zGvg8pqr-1P0
            r5.<init>()
            r3.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r5)
            org.telegram.ui.Cells.HeaderCell r3 = new org.telegram.ui.Cells.HeaderCell
            r3.<init>(r7)
            r6.intensityCell = r3
            r5 = 2131624522(0x7f0e024a, float:1.8876226E38)
            java.lang.String r9 = "BackgroundIntensity"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r3.setText(r5)
            android.widget.FrameLayout[] r3 = r6.patternLayout
            r3 = r3[r1]
            org.telegram.ui.Cells.HeaderCell r5 = r6.intensityCell
            r26 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r29 = 1127153664(0x432var_, float:175.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r3.addView(r5, r9)
            org.telegram.ui.ThemePreviewActivity$15 r3 = new org.telegram.ui.ThemePreviewActivity$15
            r3.<init>(r6, r7)
            r6.intensitySeekBar = r3
            float r5 = r6.currentIntensity
            r3.setProgress(r5)
            org.telegram.ui.Components.SeekBarView r3 = r6.intensitySeekBar
            r3.setReportChanges(r8)
            org.telegram.ui.Components.SeekBarView r3 = r6.intensitySeekBar
            org.telegram.ui.ThemePreviewActivity$16 r5 = new org.telegram.ui.ThemePreviewActivity$16
            r5.<init>()
            r3.setDelegate(r5)
            android.widget.FrameLayout[] r3 = r6.patternLayout
            r3 = r3[r1]
            org.telegram.ui.Components.SeekBarView r5 = r6.intensitySeekBar
            r26 = 1108869120(0x42180000, float:38.0)
            r28 = 1084227584(0x40a00000, float:5.0)
            r29 = 1129512960(0x43530000, float:211.0)
            r30 = 1084227584(0x40a00000, float:5.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r3.addView(r5, r9)
            goto L_0x0a46
        L_0x09cc:
            org.telegram.ui.Components.ColorPicker r3 = new org.telegram.ui.Components.ColorPicker
            boolean r5 = r6.editingTheme
            org.telegram.ui.ThemePreviewActivity$17 r9 = new org.telegram.ui.ThemePreviewActivity$17
            r9.<init>()
            r3.<init>(r7, r5, r9)
            r6.colorPicker = r3
            int r5 = r6.screenType
            if (r5 != r8) goto L_0x0a2d
            android.widget.FrameLayout[] r5 = r6.patternLayout
            r5 = r5[r1]
            r9 = -1
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r9, r8)
            r5.addView(r3, r11)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r6.applyingTheme
            boolean r3 = r3.isDark()
            if (r3 == 0) goto L_0x09fb
            org.telegram.ui.Components.ColorPicker r3 = r6.colorPicker
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            r3.setMinBrightness(r5)
            goto L_0x0a0b
        L_0x09fb:
            org.telegram.ui.Components.ColorPicker r3 = r6.colorPicker
            r5 = 1028443341(0x3d4ccccd, float:0.05)
            r3.setMinBrightness(r5)
            org.telegram.ui.Components.ColorPicker r3 = r6.colorPicker
            r5 = 1061997773(0x3f4ccccd, float:0.8)
            r3.setMaxBrightness(r5)
        L_0x0a0b:
            org.telegram.ui.Components.ColorPicker r3 = r6.colorPicker
            r26 = 1
            boolean r27 = r6.hasChanges(r8)
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r25 = r3
            r25.setType(r26, r27, r28, r29, r30, r31, r32)
            org.telegram.ui.Components.ColorPicker r3 = r6.colorPicker
            org.telegram.ui.ActionBar.Theme$ThemeAccent r5 = r6.accent
            int r5 = r5.accentColor
            r9 = 0
            r3.setColor(r5, r9)
            goto L_0x0a46
        L_0x0a2d:
            android.widget.FrameLayout[] r5 = r6.patternLayout
            r5 = r5[r1]
            r25 = -1
            r26 = -1082130432(0xffffffffbvar_, float:-1.0)
            r27 = 1
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r5.addView(r3, r9)
        L_0x0a46:
            int r1 = r1 + 1
            r9 = 0
            goto L_0x070e
        L_0x0a4b:
            r6.updateButtonState(r0, r0)
            org.telegram.ui.Components.BackupImageView r0 = r6.backgroundImage
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            boolean r0 = r0.hasBitmapImage()
            if (r0 != 0) goto L_0x0a61
            android.widget.FrameLayout r0 = r6.page2
            r1 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r0.setBackgroundColor(r1)
        L_0x0a61:
            int r0 = r6.screenType
            if (r0 == r8) goto L_0x0a7d
            java.lang.Object r0 = r6.currentWallpaper
            boolean r0 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r0 != 0) goto L_0x0a7d
            org.telegram.ui.Components.BackupImageView r0 = r6.backgroundImage
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            r0.setCrossfadeWithOldImage(r8)
            org.telegram.ui.Components.BackupImageView r0 = r6.backgroundImage
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            r0.setForceCrossfade(r8)
        L_0x0a7d:
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView2
            org.telegram.ui.ThemePreviewActivity$MessagesAdapter r1 = r6.messagesAdapter
            r0.setAdapter(r1)
            org.telegram.ui.ThemePreviewActivity$18 r0 = new org.telegram.ui.ThemePreviewActivity$18
            r0.<init>(r7)
            r6.frameLayout = r0
            r1 = 0
            r0.setWillNotDraw(r1)
            android.widget.FrameLayout r0 = r6.frameLayout
            r6.fragmentView = r0
            android.view.ViewTreeObserver r0 = r0.getViewTreeObserver()
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$uUPPP8HkXREwDK9Dk3mAvuD_7_U r1 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$uUPPP8HkXREwDK9Dk3mAvuD_7_U
            r1.<init>()
            r6.onGlobalLayoutListener = r1
            r0.addOnGlobalLayoutListener(r1)
            androidx.viewpager.widget.ViewPager r0 = new androidx.viewpager.widget.ViewPager
            r0.<init>(r7)
            r6.viewPager = r0
            org.telegram.ui.ThemePreviewActivity$19 r1 = new org.telegram.ui.ThemePreviewActivity$19
            r1.<init>()
            r0.addOnPageChangeListener(r1)
            androidx.viewpager.widget.ViewPager r0 = r6.viewPager
            org.telegram.ui.ThemePreviewActivity$20 r1 = new org.telegram.ui.ThemePreviewActivity$20
            r1.<init>()
            r0.setAdapter(r1)
            androidx.viewpager.widget.ViewPager r0 = r6.viewPager
            java.lang.String r1 = "actionBarDefault"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            org.telegram.messenger.AndroidUtilities.setViewPagerEdgeEffectColor(r0, r1)
            android.widget.FrameLayout r0 = r6.frameLayout
            androidx.viewpager.widget.ViewPager r1 = r6.viewPager
            r25 = -1
            r26 = -1082130432(0xffffffffbvar_, float:-1.0)
            r27 = 51
            r28 = 0
            r29 = 0
            r30 = 0
            int r3 = r6.screenType
            if (r3 != 0) goto L_0x0ade
            r3 = 1111490560(0x42400000, float:48.0)
            r31 = 1111490560(0x42400000, float:48.0)
            goto L_0x0ae0
        L_0x0ade:
            r31 = 0
        L_0x0ae0:
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r0.addView(r1, r3)
            int r0 = r6.screenType
            if (r0 != 0) goto L_0x0CLASSNAME
            android.view.View r0 = new android.view.View
            r0.<init>(r7)
            java.lang.String r1 = "dialogShadowLine"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBackgroundColor(r1)
            android.widget.FrameLayout$LayoutParams r1 = new android.widget.FrameLayout$LayoutParams
            r3 = 83
            r5 = -1
            r1.<init>(r5, r8, r3)
            r3 = 1111490560(0x42400000, float:48.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.bottomMargin = r3
            android.widget.FrameLayout r3 = r6.frameLayout
            r3.addView(r0, r1)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.saveButtonsContainer = r0
            r1 = r24
            int r1 = r6.getButtonsColor(r1)
            r0.setBackgroundColor(r1)
            android.widget.FrameLayout r0 = r6.frameLayout
            android.widget.FrameLayout r1 = r6.saveButtonsContainer
            r3 = 48
            r5 = 83
            r9 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r3, r5)
            r0.addView(r1, r3)
            org.telegram.ui.ThemePreviewActivity$21 r0 = new org.telegram.ui.ThemePreviewActivity$21
            r0.<init>(r7)
            r6.dotsContainer = r0
            android.widget.FrameLayout r1 = r6.saveButtonsContainer
            r3 = 22
            r5 = 8
            r9 = 17
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r5, r9)
            r1.addView(r0, r3)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.cancelButton = r0
            r1 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r8, r1)
            android.widget.TextView r0 = r6.cancelButton
            int r3 = r6.getButtonsColor(r2)
            r0.setTextColor(r3)
            android.widget.TextView r0 = r6.cancelButton
            r0.setGravity(r9)
            android.widget.TextView r0 = r6.cancelButton
            r3 = 251658240(0xvar_, float:6.3108872E-30)
            r5 = 0
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r3, r5)
            r0.setBackgroundDrawable(r3)
            android.widget.TextView r0 = r6.cancelButton
            r3 = 1105723392(0x41e80000, float:29.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r0.setPadding(r9, r5, r10, r5)
            android.widget.TextView r0 = r6.cancelButton
            r5 = 2131624639(0x7f0e02bf, float:1.8876463E38)
            java.lang.String r9 = "Cancel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            java.lang.String r5 = r5.toUpperCase()
            r0.setText(r5)
            android.widget.TextView r0 = r6.cancelButton
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r18)
            r0.setTypeface(r5)
            android.widget.FrameLayout r0 = r6.saveButtonsContainer
            android.widget.TextView r5 = r6.cancelButton
            r9 = 51
            r10 = -1
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r10, r9)
            r0.addView(r5, r9)
            android.widget.TextView r0 = r6.cancelButton
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$goge0erCvvlxD0B94Y-Gqi2Nn7g r5 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$goge0erCvvlxD0B94Y-Gqi2Nn7g
            r5.<init>()
            r0.setOnClickListener(r5)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.doneButton = r0
            r0.setTextSize(r8, r1)
            android.widget.TextView r0 = r6.doneButton
            int r1 = r6.getButtonsColor(r2)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r6.doneButton
            r1 = 17
            r0.setGravity(r1)
            android.widget.TextView r0 = r6.doneButton
            r1 = 251658240(0xvar_, float:6.3108872E-30)
            r2 = 0
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            android.widget.TextView r0 = r6.doneButton
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r0.setPadding(r1, r2, r3, r2)
            android.widget.TextView r0 = r6.doneButton
            r1 = 2131624286(0x7f0e015e, float:1.8875747E38)
            java.lang.String r2 = "ApplyTheme"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r1 = r1.toUpperCase()
            r0.setText(r1)
            android.widget.TextView r0 = r6.doneButton
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r18)
            r0.setTypeface(r1)
            android.widget.FrameLayout r0 = r6.saveButtonsContainer
            android.widget.TextView r1 = r6.doneButton
            r2 = 53
            r3 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3, r2)
            r0.addView(r1, r2)
            android.widget.TextView r0 = r6.doneButton
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$SU8HGXYz76CywS0ZuqdTe39JJ3o r1 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$SU8HGXYz76CywS0ZuqdTe39JJ3o
            r1.<init>()
            r0.setOnClickListener(r1)
        L_0x0CLASSNAME:
            java.util.List r0 = r33.getThemeDescriptionsInternal()
            r6.themeDescriptions = r0
            r6.setCurrentImage(r8)
            android.view.View r0 = r6.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$1 */
    public /* synthetic */ void lambda$createView$1$ThemePreviewActivity(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        if (!(this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
            Drawable drawable = imageReceiver.getDrawable();
            if (z && drawable != null) {
                if (!Theme.hasThemeKey("chat_serviceBackground")) {
                    Theme.applyChatServiceMessageColor(AndroidUtilities.calcDrawableColor(drawable));
                }
                this.listView2.invalidateViews();
                FrameLayout frameLayout2 = this.buttonsContainer;
                if (frameLayout2 != null) {
                    int childCount = frameLayout2.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        this.buttonsContainer.getChildAt(i).invalidate();
                    }
                }
                RadialProgress2 radialProgress2 = this.radialProgress;
                if (radialProgress2 != null) {
                    radialProgress2.setColors("chat_serviceBackground", "chat_serviceBackground", "chat_serviceText", "chat_serviceText");
                }
                if (!z2 && this.isBlurred && this.blurredBitmap == null) {
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(false);
                    updateBlurred();
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(true);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$2 */
    public /* synthetic */ void lambda$createView$2$ThemePreviewActivity(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$3 */
    public /* synthetic */ void lambda$createView$3$ThemePreviewActivity(View view, int i, float f, float f2) {
        if (view instanceof ChatMessageCell) {
            ChatMessageCell chatMessageCell = (ChatMessageCell) view;
            if (!chatMessageCell.isInsideBackground(f, f2)) {
                selectColorType(2);
            } else if (chatMessageCell.getMessageObject().isOutOwner()) {
                selectColorType(3);
            } else {
                selectColorType(1);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$4 */
    public /* synthetic */ void lambda$createView$4$ThemePreviewActivity(int i, int i2) {
        if (this.isMotion) {
            float f = 1.0f;
            if (this.motionAnimation != null) {
                f = (this.backgroundImage.getScaleX() - 1.0f) / (this.parallaxScale - 1.0f);
            }
            this.backgroundImage.setTranslationX(((float) i) * f);
            this.backgroundImage.setTranslationY(((float) i2) * f);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x021a  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x021d  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x0227  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x026a  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0177 A[SYNTHETIC, Splitter:B:76:0x0177] */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x01a0  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x01aa  */
    /* renamed from: lambda$createView$5 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$5$ThemePreviewActivity(android.view.View r20) {
        /*
            r19 = this;
            r1 = r19
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r2 = r1.isBlurred
            r3 = 0
            java.lang.String r2 = r0.generateWallpaperName(r3, r2)
            boolean r4 = r1.isBlurred
            r5 = 0
            if (r4 == 0) goto L_0x0018
            java.lang.String r0 = r0.generateWallpaperName(r3, r5)
            r4 = r0
            goto L_0x0019
        L_0x0018:
            r4 = r2
        L_0x0019:
            java.io.File r6 = new java.io.File
            java.io.File r0 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            r6.<init>(r0, r2)
            java.lang.Object r0 = r1.currentWallpaper
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            java.lang.String r8 = "jpg"
            java.lang.String r9 = "t"
            r10 = 87
            r11 = 1
            if (r7 == 0) goto L_0x008e
            android.graphics.Bitmap r0 = r1.originalBitmap
            if (r0 == 0) goto L_0x0049
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0044 }
            r0.<init>(r6)     // Catch:{ Exception -> 0x0044 }
            android.graphics.Bitmap r7 = r1.originalBitmap     // Catch:{ Exception -> 0x0044 }
            android.graphics.Bitmap$CompressFormat r12 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ Exception -> 0x0044 }
            r7.compress(r12, r10, r0)     // Catch:{ Exception -> 0x0044 }
            r0.close()     // Catch:{ Exception -> 0x0044 }
        L_0x0042:
            r0 = 1
            goto L_0x0075
        L_0x0044:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x005c
        L_0x0049:
            org.telegram.ui.Components.BackupImageView r0 = r1.backgroundImage
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            boolean r7 = r0.hasNotThumb()
            if (r7 != 0) goto L_0x005e
            boolean r7 = r0.hasStaticThumb()
            if (r7 == 0) goto L_0x005c
            goto L_0x005e
        L_0x005c:
            r0 = 0
            goto L_0x0075
        L_0x005e:
            android.graphics.Bitmap r0 = r0.getBitmap()
            java.io.FileOutputStream r7 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0070 }
            r7.<init>(r6)     // Catch:{ Exception -> 0x0070 }
            android.graphics.Bitmap$CompressFormat r12 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ Exception -> 0x0070 }
            r0.compress(r12, r10, r7)     // Catch:{ Exception -> 0x0070 }
            r7.close()     // Catch:{ Exception -> 0x0070 }
            goto L_0x0042
        L_0x0070:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x005c
        L_0x0075:
            if (r0 != 0) goto L_0x0118
            java.lang.Object r0 = r1.currentWallpaper
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r0
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r11)
            boolean r0 = org.telegram.messenger.AndroidUtilities.copyFile((java.io.File) r0, (java.io.File) r6)     // Catch:{ Exception -> 0x0087 }
            goto L_0x0118
        L_0x0087:
            r0 = move-exception
            r7 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
            goto L_0x0171
        L_0x008e:
            boolean r7 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r7 == 0) goto L_0x011a
            org.telegram.tgnet.TLRPC$TL_wallPaper r7 = r1.selectedPattern
            if (r7 == 0) goto L_0x0117
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r0 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r0     // Catch:{ all -> 0x0112 }
            org.telegram.ui.Components.BackupImageView r0 = r1.backgroundImage     // Catch:{ all -> 0x0112 }
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()     // Catch:{ all -> 0x0112 }
            android.graphics.Bitmap r0 = r0.getBitmap()     // Catch:{ all -> 0x0112 }
            int r7 = r0.getWidth()     // Catch:{ all -> 0x0112 }
            int r12 = r0.getHeight()     // Catch:{ all -> 0x0112 }
            android.graphics.Bitmap$Config r13 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0112 }
            android.graphics.Bitmap r7 = android.graphics.Bitmap.createBitmap(r7, r12, r13)     // Catch:{ all -> 0x0112 }
            android.graphics.Canvas r12 = new android.graphics.Canvas     // Catch:{ all -> 0x0112 }
            r12.<init>(r7)     // Catch:{ all -> 0x0112 }
            int r13 = r1.backgroundGradientColor     // Catch:{ all -> 0x0112 }
            r14 = 2
            if (r13 == 0) goto L_0x00de
            android.graphics.drawable.GradientDrawable r13 = new android.graphics.drawable.GradientDrawable     // Catch:{ all -> 0x0112 }
            int r15 = r1.backgroundRotation     // Catch:{ all -> 0x0112 }
            android.graphics.drawable.GradientDrawable$Orientation r15 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r15)     // Catch:{ all -> 0x0112 }
            int[] r3 = new int[r14]     // Catch:{ all -> 0x0112 }
            int r10 = r1.backgroundColor     // Catch:{ all -> 0x0112 }
            r3[r5] = r10     // Catch:{ all -> 0x0112 }
            int r10 = r1.backgroundGradientColor     // Catch:{ all -> 0x0112 }
            r3[r11] = r10     // Catch:{ all -> 0x0112 }
            r13.<init>(r15, r3)     // Catch:{ all -> 0x0112 }
            int r3 = r7.getWidth()     // Catch:{ all -> 0x0112 }
            int r10 = r7.getHeight()     // Catch:{ all -> 0x0112 }
            r13.setBounds(r5, r5, r3, r10)     // Catch:{ all -> 0x0112 }
            r13.draw(r12)     // Catch:{ all -> 0x0112 }
            goto L_0x00e3
        L_0x00de:
            int r3 = r1.backgroundColor     // Catch:{ all -> 0x0112 }
            r12.drawColor(r3)     // Catch:{ all -> 0x0112 }
        L_0x00e3:
            android.graphics.Paint r3 = new android.graphics.Paint     // Catch:{ all -> 0x0112 }
            r3.<init>(r14)     // Catch:{ all -> 0x0112 }
            android.graphics.PorterDuffColorFilter r10 = new android.graphics.PorterDuffColorFilter     // Catch:{ all -> 0x0112 }
            int r13 = r1.patternColor     // Catch:{ all -> 0x0112 }
            android.graphics.PorterDuff$Mode r14 = r1.blendMode     // Catch:{ all -> 0x0112 }
            r10.<init>(r13, r14)     // Catch:{ all -> 0x0112 }
            r3.setColorFilter(r10)     // Catch:{ all -> 0x0112 }
            r10 = 1132396544(0x437var_, float:255.0)
            float r13 = r1.currentIntensity     // Catch:{ all -> 0x0112 }
            float r13 = r13 * r10
            int r10 = (int) r13     // Catch:{ all -> 0x0112 }
            r3.setAlpha(r10)     // Catch:{ all -> 0x0112 }
            r10 = 0
            r12.drawBitmap(r0, r10, r10, r3)     // Catch:{ all -> 0x0112 }
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ all -> 0x0112 }
            r0.<init>(r6)     // Catch:{ all -> 0x0112 }
            android.graphics.Bitmap$CompressFormat r3 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x0112 }
            r10 = 87
            r7.compress(r3, r10, r0)     // Catch:{ all -> 0x0112 }
            r0.close()     // Catch:{ all -> 0x0112 }
            goto L_0x0117
        L_0x0112:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0171
        L_0x0117:
            r0 = 1
        L_0x0118:
            r7 = 0
            goto L_0x0173
        L_0x011a:
            boolean r3 = r0 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper
            if (r3 == 0) goto L_0x014a
            org.telegram.ui.WallpapersListActivity$FileWallpaper r0 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r0
            int r3 = r0.resId
            if (r3 != 0) goto L_0x0117
            java.lang.String r3 = r0.slug
            boolean r3 = r9.equals(r3)
            if (r3 == 0) goto L_0x012d
            goto L_0x0117
        L_0x012d:
            java.io.File r3 = r0.originalPath     // Catch:{ Exception -> 0x0143 }
            if (r3 == 0) goto L_0x0132
            goto L_0x0134
        L_0x0132:
            java.io.File r3 = r0.path     // Catch:{ Exception -> 0x0143 }
        L_0x0134:
            boolean r7 = r3.equals(r6)     // Catch:{ Exception -> 0x0143 }
            if (r7 == 0) goto L_0x013c
            r0 = 1
            goto L_0x0173
        L_0x013c:
            boolean r0 = org.telegram.messenger.AndroidUtilities.copyFile((java.io.File) r3, (java.io.File) r6)     // Catch:{ Exception -> 0x0141 }
            goto L_0x0173
        L_0x0141:
            r0 = move-exception
            goto L_0x0145
        L_0x0143:
            r0 = move-exception
            r7 = 0
        L_0x0145:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            goto L_0x0173
        L_0x014a:
            boolean r3 = r0 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r3 == 0) goto L_0x0171
            org.telegram.messenger.MediaController$SearchImage r0 = (org.telegram.messenger.MediaController.SearchImage) r0
            org.telegram.tgnet.TLRPC$Photo r3 = r0.photo
            if (r3 == 0) goto L_0x0161
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r3.sizes
            int r3 = r1.maxWallpaperSize
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r3, r11)
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r11)
            goto L_0x0167
        L_0x0161:
            java.lang.String r0 = r0.imageUrl
            java.io.File r0 = org.telegram.messenger.ImageLoader.getHttpFilePath(r0, r8)
        L_0x0167:
            boolean r0 = org.telegram.messenger.AndroidUtilities.copyFile((java.io.File) r0, (java.io.File) r6)     // Catch:{ Exception -> 0x016c }
            goto L_0x0118
        L_0x016c:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x0171:
            r0 = 0
            goto L_0x0118
        L_0x0173:
            boolean r3 = r1.isBlurred
            if (r3 == 0) goto L_0x0198
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x0193 }
            java.io.File r3 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x0193 }
            r0.<init>(r3, r4)     // Catch:{ all -> 0x0193 }
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ all -> 0x0193 }
            r3.<init>(r0)     // Catch:{ all -> 0x0193 }
            android.graphics.Bitmap r0 = r1.blurredBitmap     // Catch:{ all -> 0x0193 }
            android.graphics.Bitmap$CompressFormat r10 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x0193 }
            r12 = 87
            r0.compress(r10, r12, r3)     // Catch:{ all -> 0x0193 }
            r3.close()     // Catch:{ all -> 0x0193 }
            r0 = 1
            goto L_0x0198
        L_0x0193:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
        L_0x0198:
            r3 = 45
            java.lang.Object r10 = r1.currentWallpaper
            boolean r12 = r10 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            if (r12 == 0) goto L_0x01aa
            org.telegram.tgnet.TLRPC$TL_wallPaper r10 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r10
            java.lang.String r8 = r10.slug
        L_0x01a4:
            r3 = 0
            r10 = 0
            r12 = 45
        L_0x01a8:
            r14 = 0
            goto L_0x01f5
        L_0x01aa:
            boolean r12 = r10 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r12 == 0) goto L_0x01c1
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r10 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r10
            org.telegram.tgnet.TLRPC$TL_wallPaper r3 = r1.selectedPattern
            if (r3 == 0) goto L_0x01b7
            java.lang.String r3 = r3.slug
            goto L_0x01b9
        L_0x01b7:
            java.lang.String r3 = "c"
        L_0x01b9:
            r8 = r3
            int r3 = r1.backgroundColor
            int r10 = r1.backgroundGradientColor
            int r12 = r1.backgroundRotation
            goto L_0x01a8
        L_0x01c1:
            boolean r12 = r10 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper
            if (r12 == 0) goto L_0x01d1
            org.telegram.ui.WallpapersListActivity$FileWallpaper r10 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r10
            java.lang.String r8 = r10.slug
            java.io.File r10 = r10.path
        L_0x01cb:
            r14 = r10
            r3 = 0
            r10 = 0
            r12 = 45
            goto L_0x01f5
        L_0x01d1:
            boolean r12 = r10 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r12 == 0) goto L_0x01f2
            org.telegram.messenger.MediaController$SearchImage r10 = (org.telegram.messenger.MediaController.SearchImage) r10
            org.telegram.tgnet.TLRPC$Photo r12 = r10.photo
            if (r12 == 0) goto L_0x01e8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r12.sizes
            int r10 = r1.maxWallpaperSize
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r10, r11)
            java.io.File r8 = org.telegram.messenger.FileLoader.getPathToAttach(r8, r11)
            goto L_0x01ee
        L_0x01e8:
            java.lang.String r10 = r10.imageUrl
            java.io.File r8 = org.telegram.messenger.ImageLoader.getHttpFilePath(r10, r8)
        L_0x01ee:
            r10 = r8
            java.lang.String r8 = ""
            goto L_0x01cb
        L_0x01f2:
            java.lang.String r8 = "d"
            goto L_0x01a4
        L_0x01f5:
            org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo r15 = new org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo
            r15.<init>()
            r15.fileName = r4
            r15.originalFileName = r2
            r15.slug = r8
            boolean r2 = r1.isBlurred
            r15.isBlurred = r2
            boolean r2 = r1.isMotion
            r15.isMotion = r2
            r15.color = r3
            r15.gradientColor = r10
            r15.rotation = r12
            float r2 = r1.currentIntensity
            r15.intensity = r2
            int r2 = r1.currentAccount
            org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r2)
            if (r8 == 0) goto L_0x021d
            r16 = 1
            goto L_0x021f
        L_0x021d:
            r16 = 0
        L_0x021f:
            r17 = 0
            r2 = r15
            r13.saveWallpaperToServer(r14, r15, r16, r17)
            if (r0 == 0) goto L_0x0266
            java.lang.String r0 = "chat_serviceBackground"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            org.telegram.ui.ActionBar.Theme.serviceMessageColorBackup = r0
            java.lang.String r0 = r2.slug
            boolean r0 = r9.equals(r0)
            if (r0 == 0) goto L_0x0239
            r3 = 0
            goto L_0x023a
        L_0x0239:
            r3 = r2
        L_0x023a:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            r0.setOverrideWallpaper(r3)
            org.telegram.ui.ActionBar.Theme.reloadWallpaper()
            if (r7 != 0) goto L_0x0266
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = r6.getAbsolutePath()
            java.lang.String r3 = org.telegram.messenger.ImageLoader.getHttpFileName(r3)
            r2.append(r3)
            java.lang.String r3 = "@100_100"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r0.removeImage(r2)
        L_0x0266:
            org.telegram.ui.ThemePreviewActivity$WallpaperActivityDelegate r0 = r1.delegate
            if (r0 == 0) goto L_0x026d
            r0.didSetNewBackground()
        L_0x026d:
            r19.finishFragment()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.lambda$createView$5$ThemePreviewActivity(android.view.View):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$6 */
    public /* synthetic */ void lambda$createView$6$ThemePreviewActivity(int i, WallpaperCheckBoxView wallpaperCheckBoxView, View view) {
        if (this.buttonsContainer.getAlpha() == 1.0f && this.patternViewAnimation == null) {
            int i2 = this.screenType;
            boolean z = true;
            if (!(i2 == 1 && i == 0)) {
                Object obj = this.currentWallpaper;
                if (!(obj instanceof WallpapersListActivity.ColorWallpaper) || i != 2) {
                    boolean z2 = false;
                    if (i == 1 && (i2 == 1 || (obj instanceof WallpapersListActivity.ColorWallpaper))) {
                        if (this.checkBoxView[1].isChecked()) {
                            this.lastSelectedPattern = this.selectedPattern;
                            this.backgroundImage.setImageDrawable((Drawable) null);
                            this.selectedPattern = null;
                            this.isMotion = false;
                            updateButtonState(false, true);
                            animateMotionChange();
                            if (this.patternLayout[1].getVisibility() == 0) {
                                if (this.screenType == 1) {
                                    showPatternsView(0, true);
                                } else {
                                    showPatternsView(i, this.patternLayout[i].getVisibility() != 0);
                                }
                            }
                        } else {
                            selectPattern(this.lastSelectedPattern != null ? -1 : 0);
                            if (this.screenType == 1) {
                                showPatternsView(1, true);
                            } else {
                                showPatternsView(i, this.patternLayout[i].getVisibility() != 0);
                            }
                        }
                        WallpaperCheckBoxView wallpaperCheckBoxView2 = this.checkBoxView[1];
                        if (this.selectedPattern != null) {
                            z2 = true;
                        }
                        wallpaperCheckBoxView2.setChecked(z2, true);
                        updateSelectedPattern(true);
                        this.patternsListView.invalidateViews();
                        updateMotionButton();
                        return;
                    } else if (obj instanceof WallpapersListActivity.ColorWallpaper) {
                        if (this.patternLayout[i].getVisibility() == 0) {
                            z = false;
                        }
                        showPatternsView(i, z);
                        return;
                    } else {
                        wallpaperCheckBoxView.setChecked(!wallpaperCheckBoxView.isChecked(), true);
                        if (i == 0) {
                            this.isBlurred = wallpaperCheckBoxView.isChecked();
                            updateBlurred();
                            return;
                        }
                        boolean isChecked = wallpaperCheckBoxView.isChecked();
                        this.isMotion = isChecked;
                        this.parallaxEffect.setEnabled(isChecked);
                        animateMotionChange();
                        return;
                    }
                }
            }
            wallpaperCheckBoxView.setChecked(!wallpaperCheckBoxView.isChecked(), true);
            boolean isChecked2 = wallpaperCheckBoxView.isChecked();
            this.isMotion = isChecked2;
            this.parallaxEffect.setEnabled(isChecked2);
            animateMotionChange();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$7 */
    public /* synthetic */ void lambda$createView$7$ThemePreviewActivity(int i, View view) {
        if (this.patternViewAnimation == null) {
            if (i == 0) {
                this.backgroundRotation = this.previousBackgroundRotation;
                setBackgroundColor(this.previousBackgroundGradientColor, 1, true);
                setBackgroundColor(this.previousBackgroundColor, 0, true);
            } else {
                TLRPC$TL_wallPaper tLRPC$TL_wallPaper = this.previousSelectedPattern;
                this.selectedPattern = tLRPC$TL_wallPaper;
                if (tLRPC$TL_wallPaper == null) {
                    this.backgroundImage.setImageDrawable((Drawable) null);
                } else {
                    BackupImageView backupImageView = this.backgroundImage;
                    ImageLocation forDocument = ImageLocation.getForDocument(tLRPC$TL_wallPaper.document);
                    String str = this.imageFilter;
                    TLRPC$TL_wallPaper tLRPC$TL_wallPaper2 = this.selectedPattern;
                    backupImageView.setImage(forDocument, str, (ImageLocation) null, (String) null, "jpg", tLRPC$TL_wallPaper2.document.size, 1, tLRPC$TL_wallPaper2);
                }
                this.checkBoxView[1].setChecked(this.selectedPattern != null, false);
                float f = this.previousIntensity;
                this.currentIntensity = f;
                this.intensitySeekBar.setProgress(f);
                this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
                updateButtonState(false, true);
                updateSelectedPattern(true);
            }
            if (this.screenType == 2) {
                showPatternsView(i, false);
                return;
            }
            if (this.selectedPattern == null) {
                if (this.isMotion) {
                    this.isMotion = false;
                    this.checkBoxView[0].setChecked(false, true);
                    animateMotionChange();
                }
                updateMotionButton();
            }
            showPatternsView(0, true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$8 */
    public /* synthetic */ void lambda$createView$8$ThemePreviewActivity(int i, View view) {
        if (this.patternViewAnimation == null) {
            if (this.screenType == 2) {
                showPatternsView(i, false);
            } else {
                showPatternsView(0, true);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$9 */
    public /* synthetic */ void lambda$createView$9$ThemePreviewActivity(View view, int i) {
        boolean z = this.selectedPattern != null;
        selectPattern(i);
        if (z == (this.selectedPattern == null)) {
            animateMotionChange();
            updateMotionButton();
        }
        updateSelectedPattern(true);
        this.checkBoxView[1].setChecked(this.selectedPattern != null, true);
        this.patternsListView.invalidateViews();
        int left = view.getLeft();
        int right = view.getRight();
        int dp = AndroidUtilities.dp(52.0f);
        int i2 = left - dp;
        if (i2 < 0) {
            this.patternsListView.smoothScrollBy(i2, 0);
            return;
        }
        int i3 = right + dp;
        if (i3 > this.patternsListView.getMeasuredWidth()) {
            RecyclerListView recyclerListView = this.patternsListView;
            recyclerListView.smoothScrollBy(i3 - recyclerListView.getMeasuredWidth(), 0);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$10 */
    public /* synthetic */ void lambda$createView$10$ThemePreviewActivity() {
        this.watchForKeyboardEndTime = SystemClock.elapsedRealtime() + 1500;
        this.frameLayout.invalidate();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$11 */
    public /* synthetic */ void lambda$createView$11$ThemePreviewActivity(View view) {
        cancelThemeApply(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$12 */
    public /* synthetic */ void lambda$createView$12$ThemePreviewActivity(View view) {
        Theme.ThemeAccent themeAccent;
        int i;
        Theme.ThemeInfo previousTheme = Theme.getPreviousTheme();
        if (previousTheme != null) {
            if (previousTheme == null || (i = previousTheme.prevAccentId) < 0) {
                themeAccent = previousTheme.getAccent(false);
            } else {
                themeAccent = previousTheme.themeAccentsMap.get(i);
            }
            if (this.accent != null) {
                saveAccentWallpaper();
                Theme.saveThemeAccents(this.applyingTheme, true, false, false, false);
                Theme.clearPreviousTheme();
                Theme.applyTheme(this.applyingTheme, this.nightTheme);
                this.parentLayout.rebuildAllFragmentViews(false, false);
            } else {
                this.parentLayout.rebuildAllFragmentViews(false, false);
                File file = new File(this.applyingTheme.pathToFile);
                Theme.ThemeInfo themeInfo = this.applyingTheme;
                Theme.applyThemeFile(file, themeInfo.name, themeInfo.info, false);
                MessagesController.getInstance(this.applyingTheme.account).saveTheme(this.applyingTheme, (Theme.ThemeAccent) null, false, false);
                SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
                edit.putString("lastDayTheme", this.applyingTheme.getKey());
                edit.commit();
            }
            finishFragment();
            if (this.screenType == 0) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didApplyNewTheme, previousTheme, themeAccent, Boolean.valueOf(this.deleteOnCancel));
            }
        }
    }

    /* access modifiers changed from: private */
    public void selectColorType(int i) {
        int i2 = i;
        if (getParentActivity() != null && this.colorType != i2 && this.patternViewAnimation == null) {
            if (i2 != 2 || (!Theme.hasCustomWallpaper() && this.accent.backgroundOverrideColor != 4294967296L)) {
                int i3 = this.colorType;
                this.colorType = i2;
                if (i2 == 1) {
                    this.dropDown.setText(LocaleController.getString("ColorPickerMainColor", NUM));
                    this.colorPicker.setType(1, hasChanges(1), false, false, false, 0, false);
                    this.colorPicker.setColor(this.accent.accentColor, 0);
                } else if (i2 == 2) {
                    this.dropDown.setText(LocaleController.getString("ColorPickerBackground", NUM));
                    int color = Theme.getColor("chat_wallpaper");
                    int color2 = Theme.hasThemeKey("chat_wallpaper_gradient_to") ? Theme.getColor("chat_wallpaper_gradient_to") : 0;
                    Theme.ThemeAccent themeAccent = this.accent;
                    long j = themeAccent.backgroundGradientOverrideColor;
                    int i4 = (int) j;
                    if (i4 == 0 && j != 0) {
                        color2 = 0;
                    }
                    int i5 = (int) themeAccent.backgroundOverrideColor;
                    this.colorPicker.setType(2, hasChanges(2), true, (i4 == 0 && color2 == 0) ? false : true, false, this.accent.backgroundRotation, false);
                    ColorPicker colorPicker2 = this.colorPicker;
                    if (i4 == 0) {
                        i4 = color2;
                    }
                    colorPicker2.setColor(i4, 1);
                    ColorPicker colorPicker3 = this.colorPicker;
                    if (i5 != 0) {
                        color = i5;
                    }
                    colorPicker3.setColor(color, 0);
                    this.messagesAdapter.notifyItemInserted(0);
                    this.listView2.smoothScrollBy(0, AndroidUtilities.dp(60.0f));
                } else if (i2 == 3) {
                    this.dropDown.setText(LocaleController.getString("ColorPickerMyMessages", NUM));
                    this.colorPicker.setType(2, hasChanges(3), true, this.accent.myMessagesGradientAccentColor != 0, true, 0, false);
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor, 1);
                    ColorPicker colorPicker4 = this.colorPicker;
                    Theme.ThemeAccent themeAccent2 = this.accent;
                    int i6 = themeAccent2.myMessagesAccentColor;
                    if (i6 == 0) {
                        i6 = themeAccent2.accentColor;
                    }
                    colorPicker4.setColor(i6, 0);
                }
                if (i2 == 1 || i2 == 3) {
                    if (i3 == 2) {
                        this.messagesAdapter.notifyItemRemoved(0);
                        if (this.patternLayout[1].getVisibility() == 0) {
                            showPatternsView(0, true);
                        }
                    }
                    if (this.applyingTheme.isDark()) {
                        this.colorPicker.setMinBrightness(0.2f);
                        return;
                    }
                    this.colorPicker.setMinBrightness(0.05f);
                    this.colorPicker.setMaxBrightness(0.8f);
                    return;
                }
                this.colorPicker.setMinBrightness(0.0f);
                this.colorPicker.setMaxBrightness(1.0f);
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("ChangeChatBackground", NUM));
            builder.setMessage(LocaleController.getString("ChangeWallpaperToColor", NUM));
            builder.setPositiveButton(LocaleController.getString("Change", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ThemePreviewActivity.this.lambda$selectColorType$13$ThemePreviewActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$selectColorType$13 */
    public /* synthetic */ void lambda$selectColorType$13$ThemePreviewActivity(DialogInterface dialogInterface, int i) {
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent.backgroundOverrideColor == 4294967296L) {
            themeAccent.backgroundOverrideColor = 0;
            themeAccent.backgroundGradientOverrideColor = 0;
            Theme.refreshThemeColors();
        }
        this.removeBackgroundOverride = true;
        Theme.resetCustomWallpaper(true);
        selectColorType(2);
    }

    private void selectPattern(int i) {
        TLRPC$TL_wallPaper tLRPC$TL_wallPaper;
        if (i < 0 || i >= this.patterns.size()) {
            tLRPC$TL_wallPaper = this.lastSelectedPattern;
        } else {
            tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) this.patterns.get(i);
        }
        if (tLRPC$TL_wallPaper != null) {
            this.backgroundImage.setImage(ImageLocation.getForDocument(tLRPC$TL_wallPaper.document), this.imageFilter, (ImageLocation) null, (String) null, "jpg", tLRPC$TL_wallPaper.document.size, 1, tLRPC$TL_wallPaper);
            this.selectedPattern = tLRPC$TL_wallPaper;
            if (this.screenType == 1) {
                this.isMotion = this.checkBoxView[0].isChecked();
            } else {
                this.isMotion = this.checkBoxView[2].isChecked();
            }
            updateButtonState(false, true);
        }
    }

    private void updateCheckboxes() {
        WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.checkBoxView;
        if (wallpaperCheckBoxViewArr != null) {
            boolean z = true;
            if (this.screenType == 1) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) wallpaperCheckBoxViewArr[1].getLayoutParams();
                this.checkBoxView[1].setChecked(this.selectedPattern != null, false);
                int dp = (layoutParams.width + AndroidUtilities.dp(9.0f)) / 2;
                float f = 0.0f;
                this.checkBoxView[1].setTranslationX(this.selectedPattern != null ? 0.0f : (float) (-dp));
                this.checkBoxView[0].setTranslationX(this.selectedPattern != null ? 0.0f : (float) dp);
                this.checkBoxView[0].setChecked(this.isMotion, false);
                WallpaperCheckBoxView wallpaperCheckBoxView = this.checkBoxView[0];
                if (this.selectedPattern == null) {
                    z = false;
                }
                wallpaperCheckBoxView.setEnabled(z);
                this.checkBoxView[0].setVisibility(this.selectedPattern != null ? 0 : 4);
                WallpaperCheckBoxView wallpaperCheckBoxView2 = this.checkBoxView[0];
                if (this.selectedPattern != null) {
                    f = 1.0f;
                }
                wallpaperCheckBoxView2.setAlpha(f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void saveAccentWallpaper() {
        Theme.ThemeAccent themeAccent = this.accent;
        if (themeAccent != null && !TextUtils.isEmpty(themeAccent.patternSlug)) {
            try {
                File pathToWallpaper = this.accent.getPathToWallpaper();
                Drawable background = this.backgroundImage.getBackground();
                Bitmap bitmap = this.backgroundImage.getImageReceiver().getBitmap();
                Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                background.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                background.draw(canvas);
                Paint paint = new Paint(2);
                paint.setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
                paint.setAlpha((int) (this.currentIntensity * 255.0f));
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
                FileOutputStream fileOutputStream = new FileOutputStream(pathToWallpaper);
                createBitmap.compress(Bitmap.CompressFormat.JPEG, 87, fileOutputStream);
                fileOutputStream.close();
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    private boolean hasChanges(int i) {
        if (this.editingTheme) {
            return false;
        }
        if (i == 1 || i == 2) {
            long j = this.backupBackgroundOverrideColor;
            if (j == 0) {
                int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper");
                int i2 = (int) this.accent.backgroundOverrideColor;
                if (i2 == 0) {
                    i2 = defaultAccentColor;
                }
                if (i2 != defaultAccentColor) {
                    return true;
                }
            } else if (j != this.accent.backgroundOverrideColor) {
                return true;
            }
            long j2 = this.backupBackgroundGradientOverrideColor;
            if (j2 == 0) {
                int defaultAccentColor2 = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                long j3 = this.accent.backgroundGradientOverrideColor;
                int i3 = (int) j3;
                if (i3 == 0 && j3 != 0) {
                    i3 = 0;
                } else if (i3 == 0) {
                    i3 = defaultAccentColor2;
                }
                if (i3 != defaultAccentColor2) {
                    return true;
                }
            } else if (j2 != this.accent.backgroundGradientOverrideColor) {
                return true;
            }
            if (this.accent.backgroundRotation != this.backupBackgroundRotation) {
                return true;
            }
        }
        if (i == 1 || i == 3) {
            int i4 = this.backupMyMessagesAccentColor;
            if (i4 == 0) {
                Theme.ThemeAccent themeAccent = this.accent;
                int i5 = themeAccent.myMessagesAccentColor;
                if (!(i5 == 0 || i5 == themeAccent.accentColor)) {
                    return true;
                }
            } else if (i4 != this.accent.myMessagesAccentColor) {
                return true;
            }
            int i6 = this.backupMyMessagesGradientAccentColor;
            if (i6 != 0) {
                if (i6 != this.accent.myMessagesGradientAccentColor) {
                    return true;
                }
            } else if (this.accent.myMessagesGradientAccentColor != 0) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x004a, code lost:
        if (r7.accent.patternMotion == r7.isMotion) goto L_0x004c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0056, code lost:
        if (r7.accent.patternIntensity == r7.currentIntensity) goto L_0x00a4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean checkDiscard() {
        /*
            r7 = this;
            int r0 = r7.screenType
            r1 = 1
            if (r0 != r1) goto L_0x00a4
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r7.accent
            int r2 = r0.accentColor
            int r3 = r7.backupAccentColor
            if (r2 != r3) goto L_0x0058
            int r2 = r0.myMessagesAccentColor
            int r3 = r7.backupMyMessagesAccentColor
            if (r2 != r3) goto L_0x0058
            int r2 = r0.myMessagesGradientAccentColor
            int r3 = r7.backupMyMessagesGradientAccentColor
            if (r2 != r3) goto L_0x0058
            long r2 = r0.backgroundOverrideColor
            long r4 = r7.backupBackgroundOverrideColor
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x0058
            long r2 = r0.backgroundGradientOverrideColor
            long r4 = r7.backupBackgroundGradientOverrideColor
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x0058
            int r2 = r0.backgroundRotation
            int r3 = r7.backupBackgroundRotation
            if (r2 != r3) goto L_0x0058
            java.lang.String r0 = r0.patternSlug
            org.telegram.tgnet.TLRPC$TL_wallPaper r2 = r7.selectedPattern
            if (r2 == 0) goto L_0x0038
            java.lang.String r2 = r2.slug
            goto L_0x003a
        L_0x0038:
            java.lang.String r2 = ""
        L_0x003a:
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0058
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = r7.selectedPattern
            if (r0 == 0) goto L_0x004c
            org.telegram.ui.ActionBar.Theme$ThemeAccent r2 = r7.accent
            boolean r2 = r2.patternMotion
            boolean r3 = r7.isMotion
            if (r2 != r3) goto L_0x0058
        L_0x004c:
            if (r0 == 0) goto L_0x00a4
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r7.accent
            float r0 = r0.patternIntensity
            float r2 = r7.currentIntensity
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x00a4
        L_0x0058:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r7.getParentActivity()
            r0.<init>((android.content.Context) r1)
            r1 = 2131627244(0x7f0e0cec, float:1.8881747E38)
            java.lang.String r2 = "SaveChangesAlertTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setTitle(r1)
            r1 = 2131627243(0x7f0e0ceb, float:1.8881745E38)
            java.lang.String r2 = "SaveChangesAlertText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            r1 = 2131627242(0x7f0e0cea, float:1.8881743E38)
            java.lang.String r2 = "Save"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$pxBSDL12u1QQRgPR-5x1n5jhGTQ r2 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$pxBSDL12u1QQRgPR-5x1n5jhGTQ
            r2.<init>()
            r0.setPositiveButton(r1, r2)
            r1 = 2131626641(0x7f0e0a91, float:1.8880524E38)
            java.lang.String r2 = "PassportDiscard"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.-$$Lambda$ThemePreviewActivity$AQaEabc8D05wFw_l3fgiPTyYnXg r2 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$AQaEabc8D05wFw_l3fgiPTyYnXg
            r2.<init>()
            r0.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r7.showDialog(r0)
            r0 = 0
            return r0
        L_0x00a4:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.checkDiscard():boolean");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkDiscard$14 */
    public /* synthetic */ void lambda$checkDiscard$14$ThemePreviewActivity(DialogInterface dialogInterface, int i) {
        this.actionBar2.getActionBarMenuOnItemClick().onItemClick(4);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkDiscard$15 */
    public /* synthetic */ void lambda$checkDiscard$15$ThemePreviewActivity(DialogInterface dialogInterface, int i) {
        cancelThemeApply(false);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        if (this.screenType == 1) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        }
        if (this.screenType == 0 && this.accent == null) {
            this.isMotion = Theme.isWallpaperMotion();
        } else {
            if (SharedConfig.getDevicePerformanceClass() == 0) {
                Point point = AndroidUtilities.displaySize;
                int min = Math.min(point.x, point.y);
                Point point2 = AndroidUtilities.displaySize;
                int max = Math.max(point2.x, point2.y);
                this.imageFilter = ((int) (((float) min) / AndroidUtilities.density)) + "_" + ((int) (((float) max) / AndroidUtilities.density)) + "_f";
            } else {
                this.imageFilter = ((int) (1080.0f / AndroidUtilities.density)) + "_" + ((int) (1920.0f / AndroidUtilities.density)) + "_f";
            }
            Point point3 = AndroidUtilities.displaySize;
            this.maxWallpaperSize = Math.min(1920, Math.max(point3.x, point3.y));
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
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        FrameLayout frameLayout2 = this.frameLayout;
        if (!(frameLayout2 == null || this.onGlobalLayoutListener == null)) {
            frameLayout2.getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
        }
        int i = this.screenType;
        if (i == 2) {
            Bitmap bitmap = this.blurredBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.blurredBitmap = null;
            }
            Theme.applyChatServiceMessageColor();
        } else if (i == 1) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        }
        if (!(this.screenType == 0 && this.accent == null)) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersNeedReload);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
        }
        super.onFragmentDestroy();
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

    public void onFailedDownload(String str, boolean z) {
        updateButtonState(true, z);
    }

    public void onSuccessDownload(String str) {
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setProgress(1.0f, this.progressVisible);
        }
        updateButtonState(false, true);
    }

    public void onProgressDownload(String str, long j, long j2) {
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), this.progressVisible);
            if (this.radialProgress.getIcon() != 10) {
                updateButtonState(false, true);
            }
        }
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC$TL_wallPaper tLRPC$TL_wallPaper;
        if (i == NotificationCenter.emojiDidLoad) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                int childCount = recyclerListView.getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = this.listView.getChildAt(i3);
                    if (childAt instanceof DialogCell) {
                        ((DialogCell) childAt).update(0);
                    }
                }
            }
        } else if (i == NotificationCenter.didSetNewWallpapper) {
            if (this.page2 != null) {
                setCurrentImage(true);
            }
        } else if (i == NotificationCenter.wallpapersNeedReload) {
            Object obj = this.currentWallpaper;
            if (obj instanceof WallpapersListActivity.FileWallpaper) {
                WallpapersListActivity.FileWallpaper fileWallpaper = (WallpapersListActivity.FileWallpaper) obj;
                if (fileWallpaper.slug == null) {
                    fileWallpaper.slug = objArr[0];
                }
            }
        } else if (i == NotificationCenter.wallpapersDidLoad) {
            ArrayList arrayList = objArr[0];
            this.patterns.clear();
            int size = arrayList.size();
            boolean z = false;
            for (int i4 = 0; i4 < size; i4++) {
                TLRPC$TL_wallPaper tLRPC$TL_wallPaper2 = (TLRPC$TL_wallPaper) arrayList.get(i4);
                if (tLRPC$TL_wallPaper2.pattern) {
                    this.patterns.add(tLRPC$TL_wallPaper2);
                    Theme.ThemeAccent themeAccent = this.accent;
                    if (themeAccent != null && themeAccent.patternSlug.equals(tLRPC$TL_wallPaper2.slug)) {
                        this.selectedPattern = tLRPC$TL_wallPaper2;
                        setCurrentImage(false);
                        updateButtonState(false, false);
                        updateCheckboxes();
                        z = true;
                    }
                }
            }
            if (!z && (tLRPC$TL_wallPaper = this.selectedPattern) != null) {
                this.patterns.add(0, tLRPC$TL_wallPaper);
            }
            PatternsAdapter patternsAdapter2 = this.patternsAdapter;
            if (patternsAdapter2 != null) {
                patternsAdapter2.notifyDataSetChanged();
            }
            long j = 0;
            int size2 = arrayList.size();
            for (int i5 = 0; i5 < size2; i5++) {
                long j2 = ((TLRPC$TL_wallPaper) arrayList.get(i5)).id;
                j = (((((((j * 20261) + 2147483648L) + ((long) ((int) (j2 >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) j2))) % 2147483648L;
            }
            TLRPC$TL_account_getWallPapers tLRPC$TL_account_getWallPapers = new TLRPC$TL_account_getWallPapers();
            tLRPC$TL_account_getWallPapers.hash = (int) j;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_getWallPapers, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ThemePreviewActivity.this.lambda$didReceivedNotification$19$ThemePreviewActivity(tLObject, tLRPC$TL_error);
                }
            }), this.classGuid);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$19 */
    public /* synthetic */ void lambda$didReceivedNotification$19$ThemePreviewActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            public final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ThemePreviewActivity.this.lambda$null$18$ThemePreviewActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$18 */
    public /* synthetic */ void lambda$null$18$ThemePreviewActivity(TLObject tLObject) {
        Theme.ThemeAccent themeAccent;
        TLRPC$TL_wallPaper tLRPC$TL_wallPaper;
        if (tLObject instanceof TLRPC$TL_account_wallPapers) {
            TLRPC$TL_account_wallPapers tLRPC$TL_account_wallPapers = (TLRPC$TL_account_wallPapers) tLObject;
            this.patterns.clear();
            int size = tLRPC$TL_account_wallPapers.wallpapers.size();
            boolean z = false;
            for (int i = 0; i < size; i++) {
                TLRPC$TL_wallPaper tLRPC$TL_wallPaper2 = (TLRPC$TL_wallPaper) tLRPC$TL_account_wallPapers.wallpapers.get(i);
                if (tLRPC$TL_wallPaper2.pattern) {
                    this.patterns.add(tLRPC$TL_wallPaper2);
                    Theme.ThemeAccent themeAccent2 = this.accent;
                    if (themeAccent2 != null && themeAccent2.patternSlug.equals(tLRPC$TL_wallPaper2.slug)) {
                        this.selectedPattern = tLRPC$TL_wallPaper2;
                        setCurrentImage(false);
                        updateButtonState(false, false);
                        updateCheckboxes();
                        z = true;
                    }
                }
            }
            if (!z && (tLRPC$TL_wallPaper = this.selectedPattern) != null) {
                this.patterns.add(0, tLRPC$TL_wallPaper);
            }
            PatternsAdapter patternsAdapter2 = this.patternsAdapter;
            if (patternsAdapter2 != null) {
                patternsAdapter2.notifyDataSetChanged();
            }
            MessagesStorage.getInstance(this.currentAccount).putWallpapers(tLRPC$TL_account_wallPapers.wallpapers, 1);
        }
        if (this.selectedPattern == null && (themeAccent = this.accent) != null && !TextUtils.isEmpty(themeAccent.patternSlug)) {
            TLRPC$TL_account_getWallPaper tLRPC$TL_account_getWallPaper = new TLRPC$TL_account_getWallPaper();
            TLRPC$TL_inputWallPaperSlug tLRPC$TL_inputWallPaperSlug = new TLRPC$TL_inputWallPaperSlug();
            tLRPC$TL_inputWallPaperSlug.slug = this.accent.patternSlug;
            tLRPC$TL_account_getWallPaper.wallpaper = tLRPC$TL_inputWallPaperSlug;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_account_getWallPaper, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ThemePreviewActivity.this.lambda$null$17$ThemePreviewActivity(tLObject, tLRPC$TL_error);
                }
            }), this.classGuid);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$17 */
    public /* synthetic */ void lambda$null$17$ThemePreviewActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            public final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ThemePreviewActivity.this.lambda$null$16$ThemePreviewActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$16 */
    public /* synthetic */ void lambda$null$16$ThemePreviewActivity(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_wallPaper) {
            TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) tLObject;
            if (tLRPC$TL_wallPaper.pattern) {
                this.selectedPattern = tLRPC$TL_wallPaper;
                setCurrentImage(false);
                updateButtonState(false, false);
                updateCheckboxes();
                this.patterns.add(0, this.selectedPattern);
                PatternsAdapter patternsAdapter2 = this.patternsAdapter;
                if (patternsAdapter2 != null) {
                    patternsAdapter2.notifyDataSetChanged();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void cancelThemeApply(boolean z) {
        if (this.screenType != 2) {
            Theme.applyPreviousTheme();
            if (this.screenType == 1) {
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
                if (this.editingTheme) {
                    Theme.ThemeAccent themeAccent = this.accent;
                    themeAccent.accentColor = this.backupAccentColor;
                    themeAccent.myMessagesAccentColor = this.backupMyMessagesAccentColor;
                    themeAccent.myMessagesGradientAccentColor = this.backupMyMessagesGradientAccentColor;
                    themeAccent.backgroundOverrideColor = this.backupBackgroundOverrideColor;
                    themeAccent.backgroundGradientOverrideColor = this.backupBackgroundGradientOverrideColor;
                    themeAccent.backgroundRotation = this.backupBackgroundRotation;
                }
                Theme.saveThemeAccents(this.applyingTheme, false, true, false, false);
            } else {
                if (this.accent != null) {
                    Theme.saveThemeAccents(this.applyingTheme, false, this.deleteOnCancel, false, false);
                }
                this.parentLayout.rebuildAllFragmentViews(false, false);
                if (this.deleteOnCancel) {
                    Theme.ThemeInfo themeInfo = this.applyingTheme;
                    if (themeInfo.pathToFile != null && !Theme.isThemeInstalled(themeInfo)) {
                        new File(this.applyingTheme.pathToFile).delete();
                    }
                }
            }
            if (!z) {
                finishFragment();
            }
        } else if (!z) {
            finishFragment();
        }
    }

    /* access modifiers changed from: private */
    public int getButtonsColor(String str) {
        return this.useDefaultThemeForButtons ? Theme.getDefaultColor(str) : Theme.getColor(str);
    }

    /* access modifiers changed from: private */
    public void scheduleApplyColor(int i, int i2, boolean z) {
        if (i2 == -1) {
            int i3 = this.colorType;
            if (i3 == 1 || i3 == 2) {
                long j = this.backupBackgroundOverrideColor;
                if (j != 0) {
                    this.accent.backgroundOverrideColor = j;
                } else {
                    this.accent.backgroundOverrideColor = 0;
                }
                long j2 = this.backupBackgroundGradientOverrideColor;
                if (j2 != 0) {
                    this.accent.backgroundGradientOverrideColor = j2;
                } else {
                    this.accent.backgroundGradientOverrideColor = 0;
                }
                this.accent.backgroundRotation = this.backupBackgroundRotation;
                if (i3 == 2) {
                    int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper");
                    int defaultAccentColor2 = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                    Theme.ThemeAccent themeAccent = this.accent;
                    int i4 = (int) themeAccent.backgroundGradientOverrideColor;
                    int i5 = (int) themeAccent.backgroundOverrideColor;
                    ColorPicker colorPicker2 = this.colorPicker;
                    if (i4 != 0) {
                        defaultAccentColor2 = i4;
                    }
                    colorPicker2.setColor(defaultAccentColor2, 1);
                    ColorPicker colorPicker3 = this.colorPicker;
                    if (i5 != 0) {
                        defaultAccentColor = i5;
                    }
                    colorPicker3.setColor(defaultAccentColor, 0);
                }
            }
            int i6 = this.colorType;
            if (i6 == 1 || i6 == 3) {
                int i7 = this.backupMyMessagesAccentColor;
                if (i7 != 0) {
                    this.accent.myMessagesAccentColor = i7;
                } else {
                    this.accent.myMessagesAccentColor = 0;
                }
                int i8 = this.backupMyMessagesGradientAccentColor;
                if (i8 != 0) {
                    this.accent.myMessagesGradientAccentColor = i8;
                } else {
                    this.accent.myMessagesGradientAccentColor = 0;
                }
                if (i6 == 3) {
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor, 1);
                    ColorPicker colorPicker4 = this.colorPicker;
                    Theme.ThemeAccent themeAccent2 = this.accent;
                    int i9 = themeAccent2.myMessagesAccentColor;
                    if (i9 == 0) {
                        i9 = themeAccent2.accentColor;
                    }
                    colorPicker4.setColor(i9, 0);
                }
            }
            Theme.refreshThemeColors();
            this.listView2.invalidateViews();
            return;
        }
        this.lastPickedColor = i;
        this.lastPickedColorNum = i2;
        if (z) {
            this.applyColorAction.run();
        } else if (!this.applyColorScheduled) {
            this.applyColorScheduled = true;
            this.fragmentView.postDelayed(this.applyColorAction, 16);
        }
    }

    private void applyColor(int i, int i2) {
        int i3 = this.colorType;
        if (i3 == 1) {
            this.accent.accentColor = i;
            Theme.refreshThemeColors();
        } else if (i3 == 2) {
            if (this.lastPickedColorNum == 0) {
                this.accent.backgroundOverrideColor = (long) i;
            } else {
                int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                if (i != 0 || defaultAccentColor == 0) {
                    this.accent.backgroundGradientOverrideColor = (long) i;
                } else {
                    this.accent.backgroundGradientOverrideColor = 4294967296L;
                }
            }
            Theme.refreshThemeColors();
            this.colorPicker.setHasChanges(hasChanges(this.colorType));
        } else if (i3 == 3) {
            if (this.lastPickedColorNum == 0) {
                this.accent.myMessagesAccentColor = i;
            } else {
                this.accent.myMessagesGradientAccentColor = i;
            }
            Theme.refreshThemeColors();
            this.listView2.invalidateViews();
            this.colorPicker.setHasChanges(hasChanges(this.colorType));
        }
        int size = this.themeDescriptions.size();
        for (int i4 = 0; i4 < size; i4++) {
            ThemeDescription themeDescription = this.themeDescriptions.get(i4);
            themeDescription.setColor(Theme.getColor(themeDescription.getCurrentKey()), false, false);
        }
        this.listView.invalidateViews();
        this.listView2.invalidateViews();
        View view = this.dotsContainer;
        if (view != null) {
            view.invalidate();
        }
    }

    private void updateButtonState(boolean z, boolean z2) {
        File file;
        String str;
        int i;
        FrameLayout frameLayout2;
        String str2;
        File file2;
        Object obj = this.selectedPattern;
        if (obj == null) {
            obj = this.currentWallpaper;
        }
        boolean z3 = obj instanceof TLRPC$TL_wallPaper;
        if (z3 || (obj instanceof MediaController.SearchImage)) {
            if (z2 && !this.progressVisible) {
                z2 = false;
            }
            if (z3) {
                TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) obj;
                str = FileLoader.getAttachFileName(tLRPC$TL_wallPaper.document);
                if (!TextUtils.isEmpty(str)) {
                    file = FileLoader.getPathToAttach(tLRPC$TL_wallPaper.document, true);
                    i = tLRPC$TL_wallPaper.document.size;
                } else {
                    return;
                }
            } else {
                MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                TLRPC$Photo tLRPC$Photo = searchImage.photo;
                if (tLRPC$Photo != null) {
                    TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Photo.sizes, this.maxWallpaperSize, true);
                    file2 = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true);
                    str2 = FileLoader.getAttachFileName(closestPhotoSizeWithSize);
                    i = closestPhotoSizeWithSize.size;
                } else {
                    file2 = ImageLoader.getHttpFilePath(searchImage.imageUrl, "jpg");
                    str2 = file2.getName();
                    i = searchImage.size;
                }
                String str3 = str2;
                file = file2;
                str = str3;
                if (TextUtils.isEmpty(str)) {
                    return;
                }
            }
            boolean exists = file.exists();
            float f = 1.0f;
            if (exists) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                RadialProgress2 radialProgress2 = this.radialProgress;
                if (radialProgress2 != null) {
                    radialProgress2.setProgress(1.0f, z2);
                    this.radialProgress.setIcon(4, z, z2);
                }
                this.backgroundImage.invalidate();
                if (this.screenType == 2) {
                    if (i != 0) {
                        this.actionBar2.setSubtitle(AndroidUtilities.formatFileSize((long) i));
                    } else {
                        this.actionBar2.setSubtitle((CharSequence) null);
                    }
                }
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(str, (MessageObject) null, this);
                if (this.radialProgress != null) {
                    FileLoader.getInstance(this.currentAccount).isLoadingFile(str);
                    Float fileProgress = ImageLoader.getInstance().getFileProgress(str);
                    if (fileProgress != null) {
                        this.radialProgress.setProgress(fileProgress.floatValue(), z2);
                    } else {
                        this.radialProgress.setProgress(0.0f, z2);
                    }
                    this.radialProgress.setIcon(10, z, z2);
                }
                if (this.screenType == 2) {
                    this.actionBar2.setSubtitle(LocaleController.getString("LoadingFullImage", NUM));
                }
                this.backgroundImage.invalidate();
            }
            if (this.selectedPattern == null && (frameLayout2 = this.buttonsContainer) != null) {
                frameLayout2.setAlpha(exists ? 1.0f : 0.5f);
            }
            int i2 = this.screenType;
            if (i2 == 0) {
                this.doneButton.setEnabled(exists);
                TextView textView = this.doneButton;
                if (!exists) {
                    f = 0.5f;
                }
                textView.setAlpha(f);
            } else if (i2 == 2) {
                this.bottomOverlayChat.setEnabled(exists);
                TextView textView2 = this.bottomOverlayChatText;
                if (!exists) {
                    f = 0.5f;
                }
                textView2.setAlpha(f);
            } else {
                this.saveItem.setEnabled(exists);
                ActionBarMenuItem actionBarMenuItem = this.saveItem;
                if (!exists) {
                    f = 0.5f;
                }
                actionBarMenuItem.setAlpha(f);
            }
        } else {
            RadialProgress2 radialProgress22 = this.radialProgress;
            if (radialProgress22 != null) {
                radialProgress22.setIcon(4, z, z2);
            }
        }
    }

    public void setDelegate(WallpaperActivityDelegate wallpaperActivityDelegate) {
        this.delegate = wallpaperActivityDelegate;
    }

    public void setPatterns(ArrayList<Object> arrayList) {
        this.patterns = arrayList;
        if (this.screenType == 1 || (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
            WallpapersListActivity.ColorWallpaper colorWallpaper = (WallpapersListActivity.ColorWallpaper) this.currentWallpaper;
            if (colorWallpaper.patternId != 0) {
                int i = 0;
                int size = arrayList.size();
                while (true) {
                    if (i >= size) {
                        break;
                    }
                    TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) this.patterns.get(i);
                    if (tLRPC$TL_wallPaper.id == colorWallpaper.patternId) {
                        this.selectedPattern = tLRPC$TL_wallPaper;
                        break;
                    }
                    i++;
                }
                this.currentIntensity = colorWallpaper.intensity;
            }
        }
    }

    private void updateSelectedPattern(boolean z) {
        int childCount = this.patternsListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.patternsListView.getChildAt(i);
            if (childAt instanceof PatternCell) {
                ((PatternCell) childAt).updateSelected(z);
            }
        }
    }

    private void updateMotionButton() {
        float f = 1.0f;
        float f2 = 0.0f;
        if (this.screenType == 2) {
            this.checkBoxView[this.selectedPattern != null ? (char) 2 : 0].setVisibility(0);
            if (this.selectedPattern == null && (this.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
                this.checkBoxView[2].setChecked(false, true);
            }
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[2];
            WallpaperCheckBoxView wallpaperCheckBoxView = this.checkBoxView[2];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = this.selectedPattern != null ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView, property, fArr);
            WallpaperCheckBoxView wallpaperCheckBoxView2 = this.checkBoxView[0];
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (this.selectedPattern != null) {
                f = 0.0f;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(wallpaperCheckBoxView2, property2, fArr2);
            animatorSet.playTogether(animatorArr);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ThemePreviewActivity.this.checkBoxView[ThemePreviewActivity.this.selectedPattern != null ? (char) 0 : 2].setVisibility(4);
                }
            });
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet.setDuration(200);
            animatorSet.start();
            return;
        }
        boolean isEnabled = this.checkBoxView[0].isEnabled();
        TLRPC$TL_wallPaper tLRPC$TL_wallPaper = this.selectedPattern;
        if (isEnabled != (tLRPC$TL_wallPaper != null)) {
            if (tLRPC$TL_wallPaper == null) {
                this.checkBoxView[0].setChecked(false, true);
            }
            this.checkBoxView[0].setEnabled(this.selectedPattern != null);
            if (this.selectedPattern != null) {
                this.checkBoxView[0].setVisibility(0);
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            int dp = (((FrameLayout.LayoutParams) this.checkBoxView[1].getLayoutParams()).width + AndroidUtilities.dp(9.0f)) / 2;
            Animator[] animatorArr2 = new Animator[1];
            WallpaperCheckBoxView wallpaperCheckBoxView3 = this.checkBoxView[0];
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            if (this.selectedPattern == null) {
                f = 0.0f;
            }
            fArr3[0] = f;
            animatorArr2[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView3, property3, fArr3);
            animatorSet2.playTogether(animatorArr2);
            Animator[] animatorArr3 = new Animator[1];
            WallpaperCheckBoxView wallpaperCheckBoxView4 = this.checkBoxView[0];
            Property property4 = View.TRANSLATION_X;
            float[] fArr4 = new float[1];
            fArr4[0] = this.selectedPattern != null ? 0.0f : (float) dp;
            animatorArr3[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView4, property4, fArr4);
            animatorSet2.playTogether(animatorArr3);
            Animator[] animatorArr4 = new Animator[1];
            WallpaperCheckBoxView wallpaperCheckBoxView5 = this.checkBoxView[1];
            Property property5 = View.TRANSLATION_X;
            float[] fArr5 = new float[1];
            if (this.selectedPattern == null) {
                f2 = (float) (-dp);
            }
            fArr5[0] = f2;
            animatorArr4[0] = ObjectAnimator.ofFloat(wallpaperCheckBoxView5, property5, fArr5);
            animatorSet2.playTogether(animatorArr4);
            animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet2.setDuration(200);
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ThemePreviewActivity.this.selectedPattern == null) {
                        ThemePreviewActivity.this.checkBoxView[0].setVisibility(4);
                    }
                }
            });
            animatorSet2.start();
        }
    }

    private void showPatternsView(int i, boolean z) {
        int i2;
        int i3 = i;
        final boolean z2 = z && i3 == 1 && this.selectedPattern != null;
        if (z) {
            if (i3 != 0) {
                this.previousSelectedPattern = this.selectedPattern;
                this.previousIntensity = this.currentIntensity;
                this.patternsAdapter.notifyDataSetChanged();
                ArrayList<Object> arrayList = this.patterns;
                if (arrayList != null) {
                    TLRPC$TL_wallPaper tLRPC$TL_wallPaper = this.selectedPattern;
                    if (tLRPC$TL_wallPaper == null) {
                        i2 = 0;
                    } else {
                        i2 = arrayList.indexOf(tLRPC$TL_wallPaper) + (this.screenType == 2 ? 1 : 0);
                    }
                    this.patternsLayoutManager.scrollToPositionWithOffset(i2, (this.patternsListView.getMeasuredWidth() - AndroidUtilities.dp(124.0f)) / 2);
                }
            } else if (this.screenType == 2) {
                this.previousBackgroundColor = this.backgroundColor;
                int i4 = this.backgroundGradientColor;
                this.previousBackgroundGradientColor = i4;
                int i5 = this.backupBackgroundRotation;
                this.previousBackgroundRotation = i5;
                this.colorPicker.setType(0, false, true, i4 != 0, false, i5, false);
                this.colorPicker.setColor(this.backgroundGradientColor, 1);
                this.colorPicker.setColor(this.backgroundColor, 0);
            }
        }
        if (this.screenType == 2) {
            this.checkBoxView[z2 ? (char) 2 : 0].setVisibility(0);
        }
        this.patternViewAnimation = new AnimatorSet();
        ArrayList arrayList2 = new ArrayList();
        int i6 = i3 == 0 ? 1 : 0;
        float f = 1.0f;
        if (z) {
            this.patternLayout[i3].setVisibility(0);
            if (this.screenType == 2) {
                arrayList2.add(ObjectAnimator.ofFloat(this.listView2, View.TRANSLATION_Y, new float[]{(float) ((-this.patternLayout[i3].getMeasuredHeight()) + AndroidUtilities.dp(48.0f))}));
                WallpaperCheckBoxView wallpaperCheckBoxView = this.checkBoxView[2];
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z2 ? 1.0f : 0.0f;
                arrayList2.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView, property, fArr));
                WallpaperCheckBoxView wallpaperCheckBoxView2 = this.checkBoxView[0];
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                if (z2) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                arrayList2.add(ObjectAnimator.ofFloat(wallpaperCheckBoxView2, property2, fArr2));
                arrayList2.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, new float[]{0.0f}));
                if (this.patternLayout[i6].getVisibility() == 0) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i6], View.ALPHA, new float[]{0.0f}));
                    arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i3], View.ALPHA, new float[]{0.0f, 1.0f}));
                    this.patternLayout[i3].setTranslationY(0.0f);
                } else {
                    FrameLayout[] frameLayoutArr = this.patternLayout;
                    arrayList2.add(ObjectAnimator.ofFloat(frameLayoutArr[i3], View.TRANSLATION_Y, new float[]{(float) frameLayoutArr[i3].getMeasuredHeight(), 0.0f}));
                }
            } else {
                if (i3 == 1) {
                    arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i3], View.ALPHA, new float[]{0.0f, 1.0f}));
                } else {
                    this.patternLayout[i3].setAlpha(1.0f);
                    arrayList2.add(ObjectAnimator.ofFloat(this.patternLayout[i6], View.ALPHA, new float[]{0.0f}));
                }
                this.colorPicker.hideKeyboard();
            }
        } else {
            arrayList2.add(ObjectAnimator.ofFloat(this.listView2, View.TRANSLATION_Y, new float[]{0.0f}));
            FrameLayout[] frameLayoutArr2 = this.patternLayout;
            arrayList2.add(ObjectAnimator.ofFloat(frameLayoutArr2[i3], View.TRANSLATION_Y, new float[]{(float) frameLayoutArr2[i3].getMeasuredHeight()}));
            arrayList2.add(ObjectAnimator.ofFloat(this.checkBoxView[0], View.ALPHA, new float[]{1.0f}));
            arrayList2.add(ObjectAnimator.ofFloat(this.checkBoxView[2], View.ALPHA, new float[]{0.0f}));
            arrayList2.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, new float[]{1.0f}));
        }
        this.patternViewAnimation.playTogether(arrayList2);
        final boolean z3 = z;
        final int i7 = i6;
        final int i8 = i;
        this.patternViewAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                AnimatorSet unused = ThemePreviewActivity.this.patternViewAnimation = null;
                if (z3 && ThemePreviewActivity.this.patternLayout[i7].getVisibility() == 0) {
                    ThemePreviewActivity.this.patternLayout[i7].setAlpha(1.0f);
                    ThemePreviewActivity.this.patternLayout[i7].setVisibility(4);
                } else if (!z3) {
                    ThemePreviewActivity.this.patternLayout[i8].setVisibility(4);
                }
                char c = 2;
                if (ThemePreviewActivity.this.screenType == 2) {
                    WallpaperCheckBoxView[] access$5700 = ThemePreviewActivity.this.checkBoxView;
                    if (z2) {
                        c = 0;
                    }
                    access$5700[c].setVisibility(4);
                } else if (i8 == 1) {
                    ThemePreviewActivity.this.patternLayout[i7].setAlpha(0.0f);
                }
            }
        });
        this.patternViewAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.patternViewAnimation.setDuration(200);
        this.patternViewAnimation.start();
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
            public void onAnimationEnd(Animator animator) {
                AnimatorSet unused = ThemePreviewActivity.this.motionAnimation = null;
            }
        });
        this.motionAnimation.start();
    }

    /* access modifiers changed from: private */
    public void setBackgroundColor(int i, int i2, boolean z) {
        if (i2 == 0) {
            this.backgroundColor = i;
        } else {
            this.backgroundGradientColor = i;
        }
        if (this.checkBoxView != null) {
            int i3 = 0;
            while (true) {
                WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.checkBoxView;
                if (i3 >= wallpaperCheckBoxViewArr.length) {
                    break;
                }
                if (wallpaperCheckBoxViewArr[i3] != null) {
                    if (i2 == 0) {
                        wallpaperCheckBoxViewArr[i3].setBackgroundColor(i);
                    } else {
                        wallpaperCheckBoxViewArr[i3].setBackgroundGradientColor(i);
                    }
                }
                i3++;
            }
        }
        if (this.backgroundGradientColor != 0) {
            this.backgroundImage.setBackground(new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(this.backgroundRotation), new int[]{this.backgroundColor, this.backgroundGradientColor}));
            this.patternColor = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(this.backgroundColor, this.backgroundGradientColor));
        } else {
            this.backgroundImage.setBackgroundColor(this.backgroundColor);
            this.patternColor = AndroidUtilities.getPatternColor(this.backgroundColor);
        }
        if (!Theme.hasThemeKey("chat_serviceBackground")) {
            int i4 = this.patternColor;
            Theme.applyChatServiceMessageColor(new int[]{i4, i4, i4, i4});
        }
        BackupImageView backupImageView = this.backgroundImage;
        if (backupImageView != null) {
            backupImageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
            this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
            this.backgroundImage.invalidate();
        }
        RecyclerListView recyclerListView = this.listView2;
        if (recyclerListView != null) {
            recyclerListView.invalidateViews();
        }
        FrameLayout frameLayout2 = this.buttonsContainer;
        if (frameLayout2 != null) {
            int childCount = frameLayout2.getChildCount();
            for (int i5 = 0; i5 < childCount; i5++) {
                this.buttonsContainer.getChildAt(i5).invalidate();
            }
        }
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setColors("chat_serviceBackground", "chat_serviceBackground", "chat_serviceText", "chat_serviceText");
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: android.graphics.drawable.ColorDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: org.telegram.ui.Components.BackgroundGradientDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v8, resolved type: android.graphics.drawable.ColorDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v9, resolved type: android.graphics.drawable.ColorDrawable} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setCurrentImage(boolean r17) {
        /*
            r16 = this;
            r0 = r16
            int r1 = r0.screenType
            if (r1 != 0) goto L_0x0015
            org.telegram.ui.ActionBar.Theme$ThemeAccent r2 = r0.accent
            if (r2 != 0) goto L_0x0015
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            r1.setBackground(r2)
            goto L_0x0200
        L_0x0015:
            r2 = 2
            r3 = 0
            r4 = 1
            r5 = 0
            if (r1 != r2) goto L_0x0124
            java.lang.Object r1 = r0.currentWallpaper
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            r6 = 100
            if (r2 == 0) goto L_0x004e
            r15 = r1
            org.telegram.tgnet.TLRPC$TL_wallPaper r15 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r15
            if (r17 == 0) goto L_0x0030
            org.telegram.tgnet.TLRPC$Document r1 = r15.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r1.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r1, r6)
        L_0x0030:
            org.telegram.ui.Components.BackupImageView r7 = r0.backgroundImage
            org.telegram.tgnet.TLRPC$Document r1 = r15.document
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            java.lang.String r9 = r0.imageFilter
            org.telegram.tgnet.TLRPC$Document r1 = r15.document
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r3, (org.telegram.tgnet.TLRPC$Document) r1)
            org.telegram.tgnet.TLRPC$Document r1 = r15.document
            int r13 = r1.size
            r14 = 1
            java.lang.String r11 = "100_100_b"
            java.lang.String r12 = "jpg"
            r7.setImage(r8, r9, r10, r11, r12, r13, r14, r15)
            goto L_0x0200
        L_0x004e:
            boolean r2 = r1 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper
            if (r2 == 0) goto L_0x0082
            org.telegram.ui.WallpapersListActivity$ColorWallpaper r1 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r1
            int r2 = r1.gradientRotation
            r0.backgroundRotation = r2
            int r2 = r1.color
            r0.setBackgroundColor(r2, r5, r4)
            int r1 = r1.gradientColor
            if (r1 == 0) goto L_0x0064
            r0.setBackgroundColor(r1, r4, r4)
        L_0x0064:
            org.telegram.tgnet.TLRPC$TL_wallPaper r1 = r0.selectedPattern
            if (r1 == 0) goto L_0x0200
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            java.lang.String r4 = r0.imageFilter
            r5 = 0
            r6 = 0
            org.telegram.tgnet.TLRPC$TL_wallPaper r10 = r0.selectedPattern
            org.telegram.tgnet.TLRPC$Document r1 = r10.document
            int r8 = r1.size
            r9 = 1
            java.lang.String r7 = "jpg"
            r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10)
            goto L_0x0200
        L_0x0082:
            boolean r2 = r1 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper
            if (r2 == 0) goto L_0x00d5
            android.graphics.Bitmap r2 = r0.currentWallpaperBitmap
            if (r2 == 0) goto L_0x0091
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            r1.setImageBitmap(r2)
            goto L_0x0200
        L_0x0091:
            org.telegram.ui.WallpapersListActivity$FileWallpaper r1 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r1
            java.io.File r2 = r1.originalPath
            if (r2 == 0) goto L_0x00a4
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            java.lang.String r2 = r2.getAbsolutePath()
            java.lang.String r4 = r0.imageFilter
            r1.setImage(r2, r4, r3)
            goto L_0x0200
        L_0x00a4:
            java.io.File r2 = r1.path
            if (r2 == 0) goto L_0x00b5
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            java.lang.String r2 = r2.getAbsolutePath()
            java.lang.String r4 = r0.imageFilter
            r1.setImage(r2, r4, r3)
            goto L_0x0200
        L_0x00b5:
            java.lang.String r2 = r1.slug
            java.lang.String r3 = "t"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x00ca
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedWallpaper(r5, r1)
            r1.setImageDrawable(r2)
            goto L_0x0200
        L_0x00ca:
            int r1 = r1.resId
            if (r1 == 0) goto L_0x0200
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            r2.setImageResource(r1)
            goto L_0x0200
        L_0x00d5:
            boolean r2 = r1 instanceof org.telegram.messenger.MediaController.SearchImage
            if (r2 == 0) goto L_0x0200
            r15 = r1
            org.telegram.messenger.MediaController$SearchImage r15 = (org.telegram.messenger.MediaController.SearchImage) r15
            org.telegram.tgnet.TLRPC$Photo r1 = r15.photo
            if (r1 == 0) goto L_0x0115
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r1.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r1, r6)
            org.telegram.tgnet.TLRPC$Photo r2 = r15.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            int r6 = r0.maxWallpaperSize
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6, r4)
            if (r2 != r1) goto L_0x00f3
            goto L_0x00f4
        L_0x00f3:
            r3 = r2
        L_0x00f4:
            if (r3 == 0) goto L_0x00fa
            int r5 = r3.size
            r13 = r5
            goto L_0x00fb
        L_0x00fa:
            r13 = 0
        L_0x00fb:
            org.telegram.ui.Components.BackupImageView r7 = r0.backgroundImage
            org.telegram.tgnet.TLRPC$Photo r2 = r15.photo
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r3, (org.telegram.tgnet.TLRPC$Photo) r2)
            java.lang.String r9 = r0.imageFilter
            org.telegram.tgnet.TLRPC$Photo r2 = r15.photo
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r1, (org.telegram.tgnet.TLRPC$Photo) r2)
            r14 = 1
            java.lang.String r11 = "100_100_b"
            java.lang.String r12 = "jpg"
            r7.setImage(r8, r9, r10, r11, r12, r13, r14, r15)
            goto L_0x0200
        L_0x0115:
            org.telegram.ui.Components.BackupImageView r1 = r0.backgroundImage
            java.lang.String r2 = r15.imageUrl
            java.lang.String r3 = r0.imageFilter
            java.lang.String r4 = r15.thumbUrl
            java.lang.String r5 = "100_100_b"
            r1.setImage((java.lang.String) r2, (java.lang.String) r3, (java.lang.String) r4, (java.lang.String) r5)
            goto L_0x0200
        L_0x0124:
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r1 = r0.backgroundGradientDisposable
            if (r1 == 0) goto L_0x012d
            r1.dispose()
            r0.backgroundGradientDisposable = r3
        L_0x012d:
            java.lang.String r1 = "chat_wallpaper"
            int r1 = org.telegram.ui.ActionBar.Theme.getDefaultAccentColor(r1)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r3 = r0.accent
            long r6 = r3.backgroundOverrideColor
            int r3 = (int) r6
            if (r3 == 0) goto L_0x013b
            r1 = r3
        L_0x013b:
            java.lang.String r3 = "chat_wallpaper_gradient_to"
            int r3 = org.telegram.ui.ActionBar.Theme.getDefaultAccentColor(r3)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r6 = r0.accent
            long r7 = r6.backgroundGradientOverrideColor
            int r9 = (int) r7
            if (r9 != 0) goto L_0x0150
            r10 = 0
            int r12 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r12 == 0) goto L_0x0150
            r3 = 0
            goto L_0x0153
        L_0x0150:
            if (r9 == 0) goto L_0x0153
            r3 = r9
        L_0x0153:
            java.lang.String r6 = r6.patternSlug
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x01af
            boolean r6 = org.telegram.ui.ActionBar.Theme.hasCustomWallpaper()
            if (r6 != 0) goto L_0x01af
            if (r3 == 0) goto L_0x0188
            org.telegram.ui.ActionBar.Theme$ThemeAccent r6 = r0.accent
            int r6 = r6.backgroundRotation
            android.graphics.drawable.GradientDrawable$Orientation r6 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r6)
            org.telegram.ui.Components.BackgroundGradientDrawable r7 = new org.telegram.ui.Components.BackgroundGradientDrawable
            int[] r2 = new int[r2]
            r2[r5] = r1
            r2[r4] = r3
            r7.<init>(r6, r2)
            org.telegram.ui.ThemePreviewActivity$26 r2 = new org.telegram.ui.ThemePreviewActivity$26
            r2.<init>()
            org.telegram.ui.Components.BackgroundGradientDrawable$Sizes r4 = org.telegram.ui.Components.BackgroundGradientDrawable.Sizes.ofDeviceScreen()
            r8 = 100
            org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r2 = r7.startDithering(r4, r2, r8)
            r0.backgroundGradientDisposable = r2
            goto L_0x018d
        L_0x0188:
            android.graphics.drawable.ColorDrawable r7 = new android.graphics.drawable.ColorDrawable
            r7.<init>(r1)
        L_0x018d:
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            r2.setBackground(r7)
            org.telegram.tgnet.TLRPC$TL_wallPaper r2 = r0.selectedPattern
            if (r2 == 0) goto L_0x01b8
            org.telegram.ui.Components.BackupImageView r6 = r0.backgroundImage
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForDocument(r2)
            java.lang.String r8 = r0.imageFilter
            r9 = 0
            r10 = 0
            org.telegram.tgnet.TLRPC$TL_wallPaper r14 = r0.selectedPattern
            org.telegram.tgnet.TLRPC$Document r2 = r14.document
            int r12 = r2.size
            r13 = 1
            java.lang.String r11 = "jpg"
            r6.setImage(r7, r8, r9, r10, r11, r12, r13, r14)
            goto L_0x01b8
        L_0x01af:
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            r2.setBackground(r4)
        L_0x01b8:
            if (r3 != 0) goto L_0x01c1
            int r2 = org.telegram.messenger.AndroidUtilities.getPatternColor(r1)
            r0.patternColor = r2
            goto L_0x01cb
        L_0x01c1:
            int r2 = org.telegram.messenger.AndroidUtilities.getAverageColor(r1, r3)
            int r2 = org.telegram.messenger.AndroidUtilities.getPatternColor(r2)
            r0.patternColor = r2
        L_0x01cb:
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            if (r2 == 0) goto L_0x01ef
            org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            int r4 = r0.patternColor
            android.graphics.PorterDuff$Mode r6 = r0.blendMode
            r3.<init>(r4, r6)
            r2.setColorFilter(r3)
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            org.telegram.messenger.ImageReceiver r2 = r2.getImageReceiver()
            float r3 = r0.currentIntensity
            r2.setAlpha(r3)
            org.telegram.ui.Components.BackupImageView r2 = r0.backgroundImage
            r2.invalidate()
        L_0x01ef:
            org.telegram.ui.Components.WallpaperCheckBoxView[] r2 = r0.checkBoxView
            if (r2 == 0) goto L_0x0200
        L_0x01f3:
            org.telegram.ui.Components.WallpaperCheckBoxView[] r2 = r0.checkBoxView
            int r3 = r2.length
            if (r5 >= r3) goto L_0x0200
            r2 = r2[r5]
            r2.setBackgroundColor(r1)
            int r5 = r5 + 1
            goto L_0x01f3
        L_0x0200:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.setCurrentImage(boolean):void");
    }

    public static class DialogsAdapter extends RecyclerListView.SelectionAdapter {
        private ArrayList<DialogCell.CustomDialog> dialogs = new ArrayList<>();
        private Context mContext;

        public DialogsAdapter(Context context) {
            this.mContext = context;
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            DialogCell.CustomDialog customDialog = new DialogCell.CustomDialog();
            customDialog.name = LocaleController.getString("ThemePreviewDialog1", NUM);
            customDialog.message = LocaleController.getString("ThemePreviewDialogMessage1", NUM);
            customDialog.id = 0;
            customDialog.unread_count = 0;
            customDialog.pinned = true;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = currentTimeMillis;
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
            customDialog2.date = currentTimeMillis - 3600;
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
            customDialog3.date = currentTimeMillis - 7200;
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
            customDialog4.date = currentTimeMillis - 10800;
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
            customDialog5.date = currentTimeMillis - 14400;
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
            customDialog6.date = currentTimeMillis - 18000;
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
            customDialog7.date = currentTimeMillis - 21600;
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
            customDialog8.date = currentTimeMillis - 25200;
            customDialog8.verified = true;
            customDialog8.isMedia = false;
            customDialog8.sent = false;
            this.dialogs.add(customDialog8);
        }

        public int getItemCount() {
            return this.dialogs.size();
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = null;
            if (i == 0) {
                view = new DialogCell((DialogsActivity) null, this.mContext, false, false);
            } else if (i == 1) {
                view = new LoadingCell(this.mContext);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                boolean z = true;
                if (i == getItemCount() - 1) {
                    z = false;
                }
                dialogCell.useSeparator = z;
                dialogCell.setDialog(this.dialogs.get(i));
            }
        }

        public int getItemViewType(int i) {
            return i == this.dialogs.size() ? 1 : 0;
        }
    }

    public class MessagesAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private ArrayList<MessageObject> messages;
        /* access modifiers changed from: private */
        public boolean showSecretMessages;
        final /* synthetic */ ThemePreviewActivity this$0;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public MessagesAdapter(ThemePreviewActivity themePreviewActivity, Context context) {
            this.this$0 = themePreviewActivity;
            this.showSecretMessages = themePreviewActivity.screenType == 0 && Utilities.random.nextInt(100) <= 1;
            this.mContext = context;
            this.messages = new ArrayList<>();
            int currentTimeMillis = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            if (themePreviewActivity.screenType == 2) {
                TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
                if (themePreviewActivity.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                    tLRPC$TL_message.message = LocaleController.getString("BackgroundColorSinglePreviewLine2", NUM);
                } else {
                    tLRPC$TL_message.message = LocaleController.getString("BackgroundPreviewLine2", NUM);
                }
                int i = currentTimeMillis + 60;
                tLRPC$TL_message.date = i;
                tLRPC$TL_message.dialog_id = 1;
                tLRPC$TL_message.flags = 259;
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                tLRPC$TL_message.from_id = tLRPC$TL_peerUser;
                tLRPC$TL_peerUser.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                tLRPC$TL_message.id = 1;
                tLRPC$TL_message.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser2 = new TLRPC$TL_peerUser();
                tLRPC$TL_message.peer_id = tLRPC$TL_peerUser2;
                tLRPC$TL_peerUser2.user_id = 0;
                MessageObject messageObject = new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message, true, false);
                messageObject.eventId = 1;
                messageObject.resetLayout();
                this.messages.add(messageObject);
                TLRPC$TL_message tLRPC$TL_message2 = new TLRPC$TL_message();
                if (themePreviewActivity.currentWallpaper instanceof WallpapersListActivity.ColorWallpaper) {
                    tLRPC$TL_message2.message = LocaleController.getString("BackgroundColorSinglePreviewLine1", NUM);
                } else {
                    tLRPC$TL_message2.message = LocaleController.getString("BackgroundPreviewLine1", NUM);
                }
                tLRPC$TL_message2.date = i;
                tLRPC$TL_message2.dialog_id = 1;
                tLRPC$TL_message2.flags = 265;
                tLRPC$TL_message2.from_id = new TLRPC$TL_peerUser();
                tLRPC$TL_message2.id = 1;
                tLRPC$TL_message2.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message2.out = false;
                TLRPC$TL_peerUser tLRPC$TL_peerUser3 = new TLRPC$TL_peerUser();
                tLRPC$TL_message2.peer_id = tLRPC$TL_peerUser3;
                tLRPC$TL_peerUser3.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                MessageObject messageObject2 = new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message2, true, false);
                messageObject2.eventId = 1;
                messageObject2.resetLayout();
                this.messages.add(messageObject2);
            } else if (themePreviewActivity.screenType == 1) {
                TLRPC$TL_message tLRPC$TL_message3 = new TLRPC$TL_message();
                TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument = new TLRPC$TL_messageMediaDocument();
                tLRPC$TL_message3.media = tLRPC$TL_messageMediaDocument;
                tLRPC$TL_messageMediaDocument.document = new TLRPC$TL_document();
                TLRPC$Document tLRPC$Document = tLRPC$TL_message3.media.document;
                tLRPC$Document.mime_type = "audio/mp3";
                tLRPC$Document.file_reference = new byte[0];
                String str = "audio/ogg";
                tLRPC$Document.id = -2147483648L;
                tLRPC$Document.size = 2621440;
                tLRPC$Document.dc_id = Integer.MIN_VALUE;
                TLRPC$TL_documentAttributeFilename tLRPC$TL_documentAttributeFilename = new TLRPC$TL_documentAttributeFilename();
                tLRPC$TL_documentAttributeFilename.file_name = LocaleController.getString("NewThemePreviewReply2", NUM) + ".mp3";
                tLRPC$TL_message3.media.document.attributes.add(tLRPC$TL_documentAttributeFilename);
                int i2 = currentTimeMillis + 60;
                tLRPC$TL_message3.date = i2;
                tLRPC$TL_message3.dialog_id = 1;
                tLRPC$TL_message3.flags = 259;
                TLRPC$TL_peerUser tLRPC$TL_peerUser4 = new TLRPC$TL_peerUser();
                tLRPC$TL_message3.from_id = tLRPC$TL_peerUser4;
                tLRPC$TL_peerUser4.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                tLRPC$TL_message3.id = 1;
                tLRPC$TL_message3.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser5 = new TLRPC$TL_peerUser();
                tLRPC$TL_message3.peer_id = tLRPC$TL_peerUser5;
                tLRPC$TL_peerUser5.user_id = 0;
                MessageObject messageObject3 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message3, true, false);
                TLRPC$TL_message tLRPC$TL_message4 = new TLRPC$TL_message();
                String string = LocaleController.getString("NewThemePreviewLine3", NUM);
                StringBuilder sb = new StringBuilder(string);
                int indexOf = string.indexOf(42);
                int lastIndexOf = string.lastIndexOf(42);
                if (!(indexOf == -1 || lastIndexOf == -1)) {
                    sb.replace(lastIndexOf, lastIndexOf + 1, "");
                    sb.replace(indexOf, indexOf + 1, "");
                    TLRPC$TL_messageEntityTextUrl tLRPC$TL_messageEntityTextUrl = new TLRPC$TL_messageEntityTextUrl();
                    tLRPC$TL_messageEntityTextUrl.offset = indexOf;
                    tLRPC$TL_messageEntityTextUrl.length = (lastIndexOf - indexOf) - 1;
                    tLRPC$TL_messageEntityTextUrl.url = "https://telegram.org";
                    tLRPC$TL_message4.entities.add(tLRPC$TL_messageEntityTextUrl);
                }
                tLRPC$TL_message4.message = sb.toString();
                tLRPC$TL_message4.date = currentTimeMillis + 960;
                tLRPC$TL_message4.dialog_id = 1;
                tLRPC$TL_message4.flags = 259;
                TLRPC$TL_peerUser tLRPC$TL_peerUser6 = new TLRPC$TL_peerUser();
                tLRPC$TL_message4.from_id = tLRPC$TL_peerUser6;
                tLRPC$TL_peerUser6.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                tLRPC$TL_message4.id = 1;
                tLRPC$TL_message4.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message4.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser7 = new TLRPC$TL_peerUser();
                tLRPC$TL_message4.peer_id = tLRPC$TL_peerUser7;
                tLRPC$TL_peerUser7.user_id = 0;
                MessageObject messageObject4 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message4, true, false);
                messageObject4.resetLayout();
                messageObject4.eventId = 1;
                this.messages.add(messageObject4);
                TLRPC$TL_message tLRPC$TL_message5 = new TLRPC$TL_message();
                tLRPC$TL_message5.message = LocaleController.getString("NewThemePreviewLine1", NUM);
                tLRPC$TL_message5.date = i2;
                tLRPC$TL_message5.dialog_id = 1;
                tLRPC$TL_message5.flags = 265;
                tLRPC$TL_message5.from_id = new TLRPC$TL_peerUser();
                tLRPC$TL_message5.id = 1;
                TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = new TLRPC$TL_messageReplyHeader();
                tLRPC$TL_message5.reply_to = tLRPC$TL_messageReplyHeader;
                tLRPC$TL_messageReplyHeader.reply_to_msg_id = 5;
                tLRPC$TL_message5.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message5.out = false;
                TLRPC$TL_peerUser tLRPC$TL_peerUser8 = new TLRPC$TL_peerUser();
                tLRPC$TL_message5.peer_id = tLRPC$TL_peerUser8;
                tLRPC$TL_peerUser8.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                MessageObject messageObject5 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message5, true, false);
                messageObject5.customReplyName = LocaleController.getString("NewThemePreviewName", NUM);
                messageObject5.eventId = 1;
                messageObject5.resetLayout();
                messageObject5.replyMessageObject = messageObject3;
                this.messages.add(messageObject5);
                this.messages.add(messageObject3);
                TLRPC$TL_message tLRPC$TL_message6 = new TLRPC$TL_message();
                tLRPC$TL_message6.date = currentTimeMillis + 120;
                tLRPC$TL_message6.dialog_id = 1;
                tLRPC$TL_message6.flags = 259;
                tLRPC$TL_message6.out = false;
                tLRPC$TL_message6.from_id = new TLRPC$TL_peerUser();
                tLRPC$TL_message6.id = 1;
                TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument2 = new TLRPC$TL_messageMediaDocument();
                tLRPC$TL_message6.media = tLRPC$TL_messageMediaDocument2;
                tLRPC$TL_messageMediaDocument2.flags |= 3;
                tLRPC$TL_messageMediaDocument2.document = new TLRPC$TL_document();
                TLRPC$Document tLRPC$Document2 = tLRPC$TL_message6.media.document;
                tLRPC$Document2.mime_type = str;
                tLRPC$Document2.file_reference = new byte[0];
                TLRPC$TL_documentAttributeAudio tLRPC$TL_documentAttributeAudio = new TLRPC$TL_documentAttributeAudio();
                tLRPC$TL_documentAttributeAudio.flags = 1028;
                tLRPC$TL_documentAttributeAudio.duration = 3;
                tLRPC$TL_documentAttributeAudio.voice = true;
                tLRPC$TL_documentAttributeAudio.waveform = new byte[]{0, 4, 17, -50, -93, 86, -103, -45, -12, -26, 63, -25, -3, 109, -114, -54, -4, -1, -1, -1, -1, -29, -1, -1, -25, -1, -1, -97, -43, 57, -57, -108, 1, -91, -4, -47, 21, 99, 10, 97, 43, 45, 115, -112, -77, 51, -63, 66, 40, 34, -122, -116, 48, -124, 16, 66, -120, 16, 68, 16, 33, 4, 1};
                tLRPC$TL_message6.media.document.attributes.add(tLRPC$TL_documentAttributeAudio);
                tLRPC$TL_message6.out = true;
                TLRPC$TL_peerUser tLRPC$TL_peerUser9 = new TLRPC$TL_peerUser();
                tLRPC$TL_message6.peer_id = tLRPC$TL_peerUser9;
                tLRPC$TL_peerUser9.user_id = 0;
                MessageObject messageObject6 = new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message6, true, false);
                messageObject6.audioProgressSec = 1;
                messageObject6.audioProgress = 0.3f;
                messageObject6.useCustomPhoto = true;
                this.messages.add(messageObject6);
            } else {
                String str2 = "audio/ogg";
                if (this.showSecretMessages) {
                    TLRPC$TL_user tLRPC$TL_user = new TLRPC$TL_user();
                    tLRPC$TL_user.id = Integer.MAX_VALUE;
                    tLRPC$TL_user.first_name = "Me";
                    TLRPC$TL_user tLRPC$TL_user2 = new TLRPC$TL_user();
                    tLRPC$TL_user2.id = NUM;
                    tLRPC$TL_user2.first_name = "Serj";
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(tLRPC$TL_user);
                    arrayList.add(tLRPC$TL_user2);
                    MessagesController.getInstance(themePreviewActivity.currentAccount).putUsers(arrayList, true);
                    TLRPC$TL_message tLRPC$TL_message7 = new TLRPC$TL_message();
                    tLRPC$TL_message7.message = "Guess why Half-Life 3 was never released.";
                    int i3 = currentTimeMillis + 960;
                    tLRPC$TL_message7.date = i3;
                    tLRPC$TL_message7.dialog_id = -1;
                    tLRPC$TL_message7.flags = 259;
                    tLRPC$TL_message7.id = NUM;
                    tLRPC$TL_message7.media = new TLRPC$TL_messageMediaEmpty();
                    tLRPC$TL_message7.out = false;
                    TLRPC$TL_peerChat tLRPC$TL_peerChat = new TLRPC$TL_peerChat();
                    tLRPC$TL_message7.peer_id = tLRPC$TL_peerChat;
                    tLRPC$TL_peerChat.chat_id = 1;
                    TLRPC$TL_peerUser tLRPC$TL_peerUser10 = new TLRPC$TL_peerUser();
                    tLRPC$TL_message7.from_id = tLRPC$TL_peerUser10;
                    tLRPC$TL_peerUser10.user_id = tLRPC$TL_user2.id;
                    this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message7, true, false));
                    TLRPC$TL_message tLRPC$TL_message8 = new TLRPC$TL_message();
                    tLRPC$TL_message8.message = "No.\nAnd every unnecessary ping of the dev delays the release for 10 days.\nEvery request for ETA delays the release for 2 weeks.";
                    tLRPC$TL_message8.date = i3;
                    tLRPC$TL_message8.dialog_id = -1;
                    tLRPC$TL_message8.flags = 259;
                    tLRPC$TL_message8.id = 1;
                    tLRPC$TL_message8.media = new TLRPC$TL_messageMediaEmpty();
                    tLRPC$TL_message8.out = false;
                    TLRPC$TL_peerChat tLRPC$TL_peerChat2 = new TLRPC$TL_peerChat();
                    tLRPC$TL_message8.peer_id = tLRPC$TL_peerChat2;
                    tLRPC$TL_peerChat2.chat_id = 1;
                    TLRPC$TL_peerUser tLRPC$TL_peerUser11 = new TLRPC$TL_peerUser();
                    tLRPC$TL_message8.from_id = tLRPC$TL_peerUser11;
                    tLRPC$TL_peerUser11.user_id = tLRPC$TL_user2.id;
                    this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message8, true, false));
                    TLRPC$TL_message tLRPC$TL_message9 = new TLRPC$TL_message();
                    tLRPC$TL_message9.message = "Is source code for Android coming anytime soon?";
                    tLRPC$TL_message9.date = currentTimeMillis + 600;
                    tLRPC$TL_message9.dialog_id = -1;
                    tLRPC$TL_message9.flags = 259;
                    tLRPC$TL_message9.id = 1;
                    tLRPC$TL_message9.media = new TLRPC$TL_messageMediaEmpty();
                    tLRPC$TL_message9.out = false;
                    TLRPC$TL_peerChat tLRPC$TL_peerChat3 = new TLRPC$TL_peerChat();
                    tLRPC$TL_message9.peer_id = tLRPC$TL_peerChat3;
                    tLRPC$TL_peerChat3.chat_id = 1;
                    TLRPC$TL_peerUser tLRPC$TL_peerUser12 = new TLRPC$TL_peerUser();
                    tLRPC$TL_message9.from_id = tLRPC$TL_peerUser12;
                    tLRPC$TL_peerUser12.user_id = tLRPC$TL_user.id;
                    this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message9, true, false));
                } else {
                    TLRPC$TL_message tLRPC$TL_message10 = new TLRPC$TL_message();
                    tLRPC$TL_message10.message = LocaleController.getString("ThemePreviewLine1", NUM);
                    int i4 = currentTimeMillis + 60;
                    tLRPC$TL_message10.date = i4;
                    tLRPC$TL_message10.dialog_id = 1;
                    tLRPC$TL_message10.flags = 259;
                    TLRPC$TL_peerUser tLRPC$TL_peerUser13 = new TLRPC$TL_peerUser();
                    tLRPC$TL_message10.from_id = tLRPC$TL_peerUser13;
                    tLRPC$TL_peerUser13.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                    tLRPC$TL_message10.id = 1;
                    tLRPC$TL_message10.media = new TLRPC$TL_messageMediaEmpty();
                    tLRPC$TL_message10.out = true;
                    TLRPC$TL_peerUser tLRPC$TL_peerUser14 = new TLRPC$TL_peerUser();
                    tLRPC$TL_message10.peer_id = tLRPC$TL_peerUser14;
                    tLRPC$TL_peerUser14.user_id = 0;
                    MessageObject messageObject7 = new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message10, true, false);
                    TLRPC$TL_message tLRPC$TL_message11 = new TLRPC$TL_message();
                    tLRPC$TL_message11.message = LocaleController.getString("ThemePreviewLine2", NUM);
                    tLRPC$TL_message11.date = currentTimeMillis + 960;
                    tLRPC$TL_message11.dialog_id = 1;
                    tLRPC$TL_message11.flags = 259;
                    TLRPC$TL_peerUser tLRPC$TL_peerUser15 = new TLRPC$TL_peerUser();
                    tLRPC$TL_message11.from_id = tLRPC$TL_peerUser15;
                    tLRPC$TL_peerUser15.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                    tLRPC$TL_message11.id = 1;
                    tLRPC$TL_message11.media = new TLRPC$TL_messageMediaEmpty();
                    tLRPC$TL_message11.out = true;
                    TLRPC$TL_peerUser tLRPC$TL_peerUser16 = new TLRPC$TL_peerUser();
                    tLRPC$TL_message11.peer_id = tLRPC$TL_peerUser16;
                    tLRPC$TL_peerUser16.user_id = 0;
                    this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message11, true, false));
                    TLRPC$TL_message tLRPC$TL_message12 = new TLRPC$TL_message();
                    tLRPC$TL_message12.date = currentTimeMillis + 130;
                    tLRPC$TL_message12.dialog_id = 1;
                    tLRPC$TL_message12.flags = 259;
                    tLRPC$TL_message12.from_id = new TLRPC$TL_peerUser();
                    tLRPC$TL_message12.id = 5;
                    TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument3 = new TLRPC$TL_messageMediaDocument();
                    tLRPC$TL_message12.media = tLRPC$TL_messageMediaDocument3;
                    tLRPC$TL_messageMediaDocument3.flags |= 3;
                    tLRPC$TL_messageMediaDocument3.document = new TLRPC$TL_document();
                    TLRPC$Document tLRPC$Document3 = tLRPC$TL_message12.media.document;
                    tLRPC$Document3.mime_type = "audio/mp4";
                    tLRPC$Document3.file_reference = new byte[0];
                    TLRPC$TL_documentAttributeAudio tLRPC$TL_documentAttributeAudio2 = new TLRPC$TL_documentAttributeAudio();
                    tLRPC$TL_documentAttributeAudio2.duration = 243;
                    tLRPC$TL_documentAttributeAudio2.performer = LocaleController.getString("ThemePreviewSongPerformer", NUM);
                    tLRPC$TL_documentAttributeAudio2.title = LocaleController.getString("ThemePreviewSongTitle", NUM);
                    tLRPC$TL_message12.media.document.attributes.add(tLRPC$TL_documentAttributeAudio2);
                    tLRPC$TL_message12.out = false;
                    TLRPC$TL_peerUser tLRPC$TL_peerUser17 = new TLRPC$TL_peerUser();
                    tLRPC$TL_message12.peer_id = tLRPC$TL_peerUser17;
                    tLRPC$TL_peerUser17.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                    this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message12, true, false));
                    TLRPC$TL_message tLRPC$TL_message13 = new TLRPC$TL_message();
                    tLRPC$TL_message13.message = LocaleController.getString("ThemePreviewLine3", NUM);
                    tLRPC$TL_message13.date = i4;
                    tLRPC$TL_message13.dialog_id = 1;
                    tLRPC$TL_message13.flags = 265;
                    tLRPC$TL_message13.from_id = new TLRPC$TL_peerUser();
                    tLRPC$TL_message13.id = 1;
                    TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader2 = new TLRPC$TL_messageReplyHeader();
                    tLRPC$TL_message13.reply_to = tLRPC$TL_messageReplyHeader2;
                    tLRPC$TL_messageReplyHeader2.reply_to_msg_id = 5;
                    tLRPC$TL_message13.media = new TLRPC$TL_messageMediaEmpty();
                    tLRPC$TL_message13.out = false;
                    TLRPC$TL_peerUser tLRPC$TL_peerUser18 = new TLRPC$TL_peerUser();
                    tLRPC$TL_message13.peer_id = tLRPC$TL_peerUser18;
                    tLRPC$TL_peerUser18.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                    MessageObject messageObject8 = new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message13, true, false);
                    messageObject8.customReplyName = LocaleController.getString("ThemePreviewLine3Reply", NUM);
                    messageObject8.replyMessageObject = messageObject7;
                    this.messages.add(messageObject8);
                    TLRPC$TL_message tLRPC$TL_message14 = new TLRPC$TL_message();
                    tLRPC$TL_message14.date = currentTimeMillis + 120;
                    tLRPC$TL_message14.dialog_id = 1;
                    tLRPC$TL_message14.flags = 259;
                    TLRPC$TL_peerUser tLRPC$TL_peerUser19 = new TLRPC$TL_peerUser();
                    tLRPC$TL_message14.from_id = tLRPC$TL_peerUser19;
                    tLRPC$TL_peerUser19.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                    tLRPC$TL_message14.id = 1;
                    TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument4 = new TLRPC$TL_messageMediaDocument();
                    tLRPC$TL_message14.media = tLRPC$TL_messageMediaDocument4;
                    tLRPC$TL_messageMediaDocument4.flags |= 3;
                    tLRPC$TL_messageMediaDocument4.document = new TLRPC$TL_document();
                    TLRPC$Document tLRPC$Document4 = tLRPC$TL_message14.media.document;
                    tLRPC$Document4.mime_type = str2;
                    tLRPC$Document4.file_reference = new byte[0];
                    TLRPC$TL_documentAttributeAudio tLRPC$TL_documentAttributeAudio3 = new TLRPC$TL_documentAttributeAudio();
                    tLRPC$TL_documentAttributeAudio3.flags = 1028;
                    tLRPC$TL_documentAttributeAudio3.duration = 3;
                    tLRPC$TL_documentAttributeAudio3.voice = true;
                    tLRPC$TL_documentAttributeAudio3.waveform = new byte[]{0, 4, 17, -50, -93, 86, -103, -45, -12, -26, 63, -25, -3, 109, -114, -54, -4, -1, -1, -1, -1, -29, -1, -1, -25, -1, -1, -97, -43, 57, -57, -108, 1, -91, -4, -47, 21, 99, 10, 97, 43, 45, 115, -112, -77, 51, -63, 66, 40, 34, -122, -116, 48, -124, 16, 66, -120, 16, 68, 16, 33, 4, 1};
                    tLRPC$TL_message14.media.document.attributes.add(tLRPC$TL_documentAttributeAudio3);
                    tLRPC$TL_message14.out = true;
                    TLRPC$TL_peerUser tLRPC$TL_peerUser20 = new TLRPC$TL_peerUser();
                    tLRPC$TL_message14.peer_id = tLRPC$TL_peerUser20;
                    tLRPC$TL_peerUser20.user_id = 0;
                    MessageObject messageObject9 = new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message14, true, false);
                    messageObject9.audioProgressSec = 1;
                    messageObject9.audioProgress = 0.3f;
                    messageObject9.useCustomPhoto = true;
                    this.messages.add(messageObject9);
                    this.messages.add(messageObject7);
                    TLRPC$TL_message tLRPC$TL_message15 = new TLRPC$TL_message();
                    tLRPC$TL_message15.date = currentTimeMillis + 10;
                    tLRPC$TL_message15.dialog_id = 1;
                    tLRPC$TL_message15.flags = 257;
                    tLRPC$TL_message15.from_id = new TLRPC$TL_peerUser();
                    tLRPC$TL_message15.id = 1;
                    TLRPC$TL_messageMediaPhoto tLRPC$TL_messageMediaPhoto = new TLRPC$TL_messageMediaPhoto();
                    tLRPC$TL_message15.media = tLRPC$TL_messageMediaPhoto;
                    tLRPC$TL_messageMediaPhoto.flags |= 3;
                    tLRPC$TL_messageMediaPhoto.photo = new TLRPC$TL_photo();
                    TLRPC$Photo tLRPC$Photo = tLRPC$TL_message15.media.photo;
                    tLRPC$Photo.file_reference = new byte[0];
                    tLRPC$Photo.has_stickers = false;
                    tLRPC$Photo.id = 1;
                    tLRPC$Photo.access_hash = 0;
                    tLRPC$Photo.date = currentTimeMillis;
                    TLRPC$TL_photoSize tLRPC$TL_photoSize = new TLRPC$TL_photoSize();
                    tLRPC$TL_photoSize.size = 0;
                    tLRPC$TL_photoSize.w = 500;
                    tLRPC$TL_photoSize.h = 302;
                    tLRPC$TL_photoSize.type = "s";
                    tLRPC$TL_photoSize.location = new TLRPC$TL_fileLocationUnavailable();
                    tLRPC$TL_message15.media.photo.sizes.add(tLRPC$TL_photoSize);
                    tLRPC$TL_message15.message = LocaleController.getString("ThemePreviewLine4", NUM);
                    tLRPC$TL_message15.out = false;
                    TLRPC$TL_peerUser tLRPC$TL_peerUser21 = new TLRPC$TL_peerUser();
                    tLRPC$TL_message15.peer_id = tLRPC$TL_peerUser21;
                    tLRPC$TL_peerUser21.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                    MessageObject messageObject10 = new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message15, true, false);
                    messageObject10.useCustomPhoto = true;
                    this.messages.add(messageObject10);
                }
            }
            TLRPC$TL_message tLRPC$TL_message16 = new TLRPC$TL_message();
            tLRPC$TL_message16.message = LocaleController.formatDateChat((long) currentTimeMillis);
            tLRPC$TL_message16.id = 0;
            tLRPC$TL_message16.date = currentTimeMillis;
            MessageObject messageObject11 = new MessageObject(themePreviewActivity.currentAccount, tLRPC$TL_message16, false, false);
            messageObject11.type = 10;
            messageObject11.contentType = 1;
            messageObject11.isDateObject = true;
            this.messages.add(messageObject11);
        }

        private boolean hasButtons() {
            if (this.this$0.buttonsContainer != null) {
                if (this.this$0.screenType != 2) {
                    return this.this$0.screenType == 1 && this.this$0.colorType == 2;
                }
                return true;
            }
        }

        public int getItemCount() {
            int size = this.messages.size();
            return hasButtons() ? size + 1 : size;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: org.telegram.ui.ThemePreviewActivity$MessagesAdapter$3} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: org.telegram.ui.ThemePreviewActivity$MessagesAdapter$3} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: org.telegram.ui.Cells.ChatActionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v12, resolved type: org.telegram.ui.Cells.ChatMessageCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v13, resolved type: org.telegram.ui.ThemePreviewActivity$MessagesAdapter$3} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v14, resolved type: org.telegram.ui.ThemePreviewActivity$MessagesAdapter$3} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v15, resolved type: org.telegram.ui.ThemePreviewActivity$MessagesAdapter$3} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r4, int r5) {
            /*
                r3 = this;
                r4 = -2
                if (r5 != 0) goto L_0x0013
                org.telegram.ui.Cells.ChatMessageCell r5 = new org.telegram.ui.Cells.ChatMessageCell
                android.content.Context r0 = r3.mContext
                r5.<init>(r0)
                org.telegram.ui.ThemePreviewActivity$MessagesAdapter$1 r0 = new org.telegram.ui.ThemePreviewActivity$MessagesAdapter$1
                r0.<init>(r3)
                r5.setDelegate(r0)
                goto L_0x0064
            L_0x0013:
                r0 = 1
                if (r5 != r0) goto L_0x0026
                org.telegram.ui.Cells.ChatActionCell r5 = new org.telegram.ui.Cells.ChatActionCell
                android.content.Context r0 = r3.mContext
                r5.<init>(r0)
                org.telegram.ui.ThemePreviewActivity$MessagesAdapter$2 r0 = new org.telegram.ui.ThemePreviewActivity$MessagesAdapter$2
                r0.<init>(r3)
                r5.setDelegate(r0)
                goto L_0x0064
            L_0x0026:
                r0 = 2
                if (r5 != r0) goto L_0x0063
                org.telegram.ui.ThemePreviewActivity r5 = r3.this$0
                android.widget.FrameLayout r5 = r5.buttonsContainer
                android.view.ViewParent r5 = r5.getParent()
                if (r5 == 0) goto L_0x004a
                org.telegram.ui.ThemePreviewActivity r5 = r3.this$0
                android.widget.FrameLayout r5 = r5.buttonsContainer
                android.view.ViewParent r5 = r5.getParent()
                android.view.ViewGroup r5 = (android.view.ViewGroup) r5
                org.telegram.ui.ThemePreviewActivity r0 = r3.this$0
                android.widget.FrameLayout r0 = r0.buttonsContainer
                r5.removeView(r0)
            L_0x004a:
                org.telegram.ui.ThemePreviewActivity$MessagesAdapter$3 r5 = new org.telegram.ui.ThemePreviewActivity$MessagesAdapter$3
                android.content.Context r0 = r3.mContext
                r5.<init>(r3, r0)
                org.telegram.ui.ThemePreviewActivity r0 = r3.this$0
                android.widget.FrameLayout r0 = r0.buttonsContainer
                r1 = 34
                r2 = 17
                android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r1, r2)
                r5.addView(r0, r1)
                goto L_0x0064
            L_0x0063:
                r5 = 0
            L_0x0064:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r1 = -1
                r0.<init>((int) r1, (int) r4)
                r5.setLayoutParams(r0)
                org.telegram.ui.Components.RecyclerListView$Holder r4 = new org.telegram.ui.Components.RecyclerListView$Holder
                r4.<init>(r5)
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.MessagesAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* JADX WARNING: Removed duplicated region for block: B:19:0x0068  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r10, int r11) {
            /*
                r9 = this;
                int r0 = r10.getItemViewType()
                r1 = 2
                if (r0 == r1) goto L_0x00ac
                boolean r0 = r9.hasButtons()
                if (r0 == 0) goto L_0x000f
                int r11 = r11 + -1
            L_0x000f:
                java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r9.messages
                java.lang.Object r0 = r0.get(r11)
                org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
                android.view.View r1 = r10.itemView
                boolean r2 = r1 instanceof org.telegram.ui.Cells.ChatMessageCell
                if (r2 == 0) goto L_0x009e
                org.telegram.ui.Cells.ChatMessageCell r1 = (org.telegram.ui.Cells.ChatMessageCell) r1
                r2 = 0
                r1.isChat = r2
                int r3 = r11 + -1
                int r4 = r9.getItemViewType(r3)
                r5 = 1
                int r11 = r11 + r5
                int r6 = r9.getItemViewType(r11)
                org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
                org.telegram.tgnet.TLRPC$ReplyMarkup r7 = r7.reply_markup
                boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_replyInlineMarkup
                r8 = 300(0x12c, float:4.2E-43)
                if (r7 != 0) goto L_0x0061
                int r7 = r10.getItemViewType()
                if (r4 != r7) goto L_0x0061
                java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r9.messages
                java.lang.Object r3 = r4.get(r3)
                org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
                boolean r4 = r3.isOutOwner()
                boolean r7 = r0.isOutOwner()
                if (r4 != r7) goto L_0x0061
                org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
                int r3 = r3.date
                org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
                int r4 = r4.date
                int r3 = r3 - r4
                int r3 = java.lang.Math.abs(r3)
                if (r3 > r8) goto L_0x0061
                r3 = 1
                goto L_0x0062
            L_0x0061:
                r3 = 0
            L_0x0062:
                int r10 = r10.getItemViewType()
                if (r6 != r10) goto L_0x0092
                java.util.ArrayList<org.telegram.messenger.MessageObject> r10 = r9.messages
                java.lang.Object r10 = r10.get(r11)
                org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
                org.telegram.tgnet.TLRPC$Message r11 = r10.messageOwner
                org.telegram.tgnet.TLRPC$ReplyMarkup r11 = r11.reply_markup
                boolean r11 = r11 instanceof org.telegram.tgnet.TLRPC$TL_replyInlineMarkup
                if (r11 != 0) goto L_0x0092
                boolean r11 = r10.isOutOwner()
                boolean r4 = r0.isOutOwner()
                if (r11 != r4) goto L_0x0092
                org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
                int r10 = r10.date
                org.telegram.tgnet.TLRPC$Message r11 = r0.messageOwner
                int r11 = r11.date
                int r10 = r10 - r11
                int r10 = java.lang.Math.abs(r10)
                if (r10 > r8) goto L_0x0092
                r2 = 1
            L_0x0092:
                boolean r10 = r9.showSecretMessages
                r1.isChat = r10
                r1.setFullyDraw(r5)
                r10 = 0
                r1.setMessageObject(r0, r10, r3, r2)
                goto L_0x00ac
            L_0x009e:
                boolean r10 = r1 instanceof org.telegram.ui.Cells.ChatActionCell
                if (r10 == 0) goto L_0x00ac
                org.telegram.ui.Cells.ChatActionCell r1 = (org.telegram.ui.Cells.ChatActionCell) r1
                r1.setMessageObject(r0)
                r10 = 1065353216(0x3var_, float:1.0)
                r1.setAlpha(r10)
            L_0x00ac:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.MessagesAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (hasButtons()) {
                if (i == 0) {
                    return 2;
                }
                i--;
            }
            if (i < 0 || i >= this.messages.size()) {
                return 4;
            }
            return this.messages.get(i).contentType;
        }
    }

    private class PatternsAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public PatternsAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            if (ThemePreviewActivity.this.patterns != null) {
                return ThemePreviewActivity.this.patterns.size();
            }
            return 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new PatternCell(this.mContext, ThemePreviewActivity.this.maxWallpaperSize, new PatternCell.PatternCellDelegate() {
                public TLRPC$TL_wallPaper getSelectedPattern() {
                    return ThemePreviewActivity.this.selectedPattern;
                }

                public int getPatternColor() {
                    return ThemePreviewActivity.this.patternColor;
                }

                public int getBackgroundColor() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundColor;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper");
                    int i = (int) ThemePreviewActivity.this.accent.backgroundOverrideColor;
                    return i != 0 ? i : defaultAccentColor;
                }

                public int getBackgroundGradientColor() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                    int i = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor;
                    return i != 0 ? i : defaultAccentColor;
                }

                public int getBackgroundGradientAngle() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundRotation;
                    }
                    return ThemePreviewActivity.this.accent.backgroundRotation;
                }
            }));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            PatternCell patternCell = (PatternCell) viewHolder.itemView;
            patternCell.setPattern((TLRPC$TL_wallPaper) ThemePreviewActivity.this.patterns.get(i));
            patternCell.getImageReceiver().setColorFilter(new PorterDuffColorFilter(ThemePreviewActivity.this.patternColor, ThemePreviewActivity.this.blendMode));
        }
    }

    private List<ThemeDescription> getThemeDescriptionsInternal() {
        $$Lambda$ThemePreviewActivity$azQIq8BXBPyo46ggOeS0gfmFo7o r9 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ThemePreviewActivity.this.lambda$getThemeDescriptionsInternal$20$ThemePreviewActivity();
            }
        };
        ArrayList arrayList = new ArrayList();
        $$Lambda$ThemePreviewActivity$azQIq8BXBPyo46ggOeS0gfmFo7o r7 = r9;
        arrayList.add(new ThemeDescription(this.page1, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.viewPager, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBTITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubtitle"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView2, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionIcon"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionPressedBackground"));
        if (!this.useDefaultThemeForButtons) {
            arrayList.add(new ThemeDescription(this.saveButtonsContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
            arrayList.add(new ThemeDescription(this.doneButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
        }
        ColorPicker colorPicker2 = this.colorPicker;
        if (colorPicker2 != null) {
            colorPicker2.provideThemeDescriptions(arrayList);
        }
        if (this.patternLayout != null) {
            for (int i = 0; i < this.patternLayout.length; i++) {
                arrayList.add(new ThemeDescription(this.patternLayout[i], 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelShadow"));
                arrayList.add(new ThemeDescription(this.patternLayout[i], 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelBackground"));
            }
            for (int i2 = 0; i2 < this.patternsButtonsContainer.length; i2++) {
                arrayList.add(new ThemeDescription(this.patternsButtonsContainer[i2], 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelShadow"));
                arrayList.add(new ThemeDescription(this.patternsButtonsContainer[i2], 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelBackground"));
            }
            arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelShadow"));
            arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelBackground"));
            arrayList.add(new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
            for (TextView themeDescription : this.patternsSaveButton) {
                arrayList.add(new ThemeDescription(themeDescription, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
            }
            for (TextView themeDescription2 : this.patternsCancelButton) {
                arrayList.add(new ThemeDescription(themeDescription2, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
            }
            arrayList.add(new ThemeDescription((View) this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"innerPaint1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
            arrayList.add(new ThemeDescription((View) this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"outerPaint1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
            arrayList.add(new ThemeDescription((View) this.intensityCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubble"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgInDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubble"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgOutDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextIn"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextOut"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheck"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckRead"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckReadSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaSentCheck"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyLine"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyLine"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyNameText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyNameText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMessageText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMessageText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMediaMessageSelectedText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMediaMessageSelectedText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeSelectedText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeSelectedText"));
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getThemeDescriptionsInternal$20 */
    public /* synthetic */ void lambda$getThemeDescriptionsInternal$20$ThemePreviewActivity() {
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
