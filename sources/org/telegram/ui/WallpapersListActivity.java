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
import android.graphics.Paint.Style;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.LongSparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_account_getWallPapers;
import org.telegram.tgnet.TLRPC.TL_account_resetWallPapers;
import org.telegram.tgnet.TLRPC.TL_account_saveWallPaper;
import org.telegram.tgnet.TLRPC.TL_account_wallPapers;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_inputWallPaper;
import org.telegram.tgnet.TLRPC.TL_messages_getInlineBotResults;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.TL_wallPaperSettings;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WallPaperSettings;
import org.telegram.tgnet.TLRPC.WebDocument;
import org.telegram.tgnet.TLRPC.messages_BotResults;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.OverrideWallpaperInfo;
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
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.WallpaperUpdater;
import org.telegram.ui.Components.WallpaperUpdater.WallpaperUpdaterDelegate;

public class WallpapersListActivity extends BaseFragment implements NotificationCenterDelegate {
    public static final int TYPE_ALL = 0;
    public static final int TYPE_COLOR = 1;
    private static final int[] defaultColors = new int[]{-1, -2826262, -4993567, -9783318, -16740912, -2891046, -3610935, -3808859, -10375058, -3289169, -5789547, -8622222, -10322, -18835, -2193583, -1059360, -2383431, -20561, -955808, -1524502, -6974739, -2507680, -5145015, -2765065, -2142101, -7613748, -12811138, -14524116, -14398084, -12764283, -10129027, -15195603, -16777216};
    private static final int delete = 4;
    private static final int forward = 3;
    private static final int[] searchColors = new int[]{-16746753, -65536, -30208, -13824, -16718798, -14702165, -9240406, -409915, -9224159, -16777216, -10725281, -1};
    private static final String[] searchColorsNames = new String[]{"Blue", "Red", "Orange", "Yellow", "Green", "Teal", "Purple", "Pink", "Brown", "Black", "Gray", "White"};
    private static final int[] searchColorsNamesR = new int[]{NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM};
    private ArrayList<View> actionModeViews = new ArrayList();
    private ColorWallpaper addedColorWallpaper;
    private FileWallpaper addedFileWallpaper;
    private ArrayList<Object> allWallPapers = new ArrayList();
    private HashMap<String, Object> allWallPapersDict = new HashMap();
    private FileWallpaper catsWallpaper;
    private Paint colorFramePaint;
    private Paint colorPaint;
    private int columnsCount = 3;
    private int currentType;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loadingWallpapers;
    private ArrayList<Object> patterns = new ArrayList();
    private AlertDialog progressDialog;
    private int resetInfoRow;
    private int resetRow;
    private int resetSectionRow;
    private int rowCount;
    private boolean scrolling;
    private SearchAdapter searchAdapter;
    private EmptyTextProgressView searchEmptyView;
    private ActionBarMenuItem searchItem;
    private int sectionRow;
    private boolean selectedBackgroundBlurred;
    private boolean selectedBackgroundMotion;
    private String selectedBackgroundSlug = "";
    private int selectedColor;
    private int selectedGradientColor;
    private int selectedGradientRotation;
    private float selectedIntensity;
    private NumberTextView selectedMessagesCountTextView;
    private LongSparseArray<Object> selectedWallPapers = new LongSparseArray();
    private int setColorRow;
    private FileWallpaper themeWallpaper;
    private int totalWallpaperRows;
    private WallpaperUpdater updater;
    private int uploadImageRow;
    private int wallPaperStartRow;
    private ArrayList<Object> wallPapers = new ArrayList();

    private class ColorCell extends View {
        private int color;

        public ColorCell(Context context) {
            super(context);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(AndroidUtilities.dp(50.0f), AndroidUtilities.dp(62.0f));
        }

        public void setColor(int i) {
            this.color = i;
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            WallpapersListActivity.this.colorPaint.setColor(this.color);
            canvas.drawCircle((float) AndroidUtilities.dp(25.0f), (float) AndroidUtilities.dp(31.0f), (float) AndroidUtilities.dp(18.0f), WallpapersListActivity.this.colorPaint);
            if (this.color == Theme.getColor("windowBackgroundWhite")) {
                canvas.drawCircle((float) AndroidUtilities.dp(25.0f), (float) AndroidUtilities.dp(31.0f), (float) AndroidUtilities.dp(18.0f), WallpapersListActivity.this.colorFramePaint);
            }
        }
    }

    public static class ColorWallpaper {
        public int color;
        public int gradientColor;
        public int gradientRotation;
        public float intensity;
        public boolean motion;
        public File path;
        public TL_wallPaper pattern;
        public long patternId;
        public String slug;

        public ColorWallpaper(String str, int i, int i2, int i3) {
            this.slug = str;
            this.color = i | -16777216;
            this.gradientColor = i2 == 0 ? 0 : -16777216 | i2;
            if (this.gradientColor == 0) {
                i3 = 45;
            }
            this.gradientRotation = i3;
            this.intensity = 1.0f;
        }

        public ColorWallpaper(String str, int i, int i2, int i3, float f, boolean z, File file) {
            this.slug = str;
            this.color = i | -16777216;
            this.gradientColor = i2 == 0 ? 0 : -16777216 | i2;
            if (this.gradientColor == 0) {
                i3 = 45;
            }
            this.gradientRotation = i3;
            this.intensity = f;
            this.path = file;
            this.motion = z;
        }

        public String getUrl() {
            String toLowerCase;
            StringBuilder stringBuilder;
            String str = "%02x%02x%02x";
            if (this.gradientColor != 0) {
                toLowerCase = String.format(str, new Object[]{Integer.valueOf(((byte) (this.gradientColor >> 16)) & 255), Integer.valueOf(((byte) (this.gradientColor >> 8)) & 255), Byte.valueOf((byte) (this.gradientColor & 255))}).toLowerCase();
            } else {
                toLowerCase = null;
            }
            String toLowerCase2 = String.format(str, new Object[]{Integer.valueOf(((byte) (this.color >> 16)) & 255), Integer.valueOf(((byte) (this.color >> 8)) & 255), Byte.valueOf((byte) (this.color & 255))}).toLowerCase();
            if (toLowerCase != null) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(toLowerCase2);
                stringBuilder2.append("-");
                stringBuilder2.append(toLowerCase);
                toLowerCase = stringBuilder2.toString();
                if (this.pattern != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(toLowerCase);
                    stringBuilder.append("&rotation=");
                    stringBuilder.append(AndroidUtilities.getWallpaperRotation(this.gradientRotation, true));
                    toLowerCase2 = stringBuilder.toString();
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(toLowerCase);
                    stringBuilder.append("?rotation=");
                    stringBuilder.append(AndroidUtilities.getWallpaperRotation(this.gradientRotation, true));
                    toLowerCase2 = stringBuilder.toString();
                }
            }
            String str2 = "/bg/";
            String str3 = "https://";
            StringBuilder stringBuilder3;
            if (this.pattern != null) {
                stringBuilder3 = new StringBuilder();
                stringBuilder3.append(str3);
                stringBuilder3.append(MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix);
                stringBuilder3.append(str2);
                stringBuilder3.append(this.pattern.slug);
                stringBuilder3.append("?intensity=");
                stringBuilder3.append((int) (this.intensity * 100.0f));
                stringBuilder3.append("&bg_color=");
                stringBuilder3.append(toLowerCase2);
                toLowerCase = stringBuilder3.toString();
                if (this.motion) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(toLowerCase);
                    stringBuilder.append("&mode=motion");
                    toLowerCase = stringBuilder.toString();
                }
                return toLowerCase;
            }
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append(str3);
            stringBuilder3.append(MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix);
            stringBuilder3.append(str2);
            stringBuilder3.append(toLowerCase2);
            return stringBuilder3.toString();
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

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public int getItemCount() {
            return WallpapersListActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View anonymousClass1;
            if (i != 0) {
                View shadowSectionCell;
                String str = "windowBackgroundGray";
                String str2 = "windowBackgroundGrayShadow";
                int i2 = NUM;
                CombinedDrawable combinedDrawable;
                if (i == 1) {
                    shadowSectionCell = new ShadowSectionCell(this.mContext);
                    Context context = this.mContext;
                    if (WallpapersListActivity.this.wallPaperStartRow != -1) {
                        i2 = NUM;
                    }
                    combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(str)), Theme.getThemedDrawable(context, i2, str2));
                    combinedDrawable.setFullsize(true);
                    shadowSectionCell.setBackgroundDrawable(combinedDrawable);
                } else if (i != 3) {
                    anonymousClass1 = new WallpaperCell(this.mContext) {
                        /* Access modifiers changed, original: protected */
                        public void onWallpaperClick(Object obj, int i) {
                            WallpapersListActivity.this.onItemClick(this, obj, i);
                        }

                        /* Access modifiers changed, original: protected */
                        public boolean onWallpaperLongClick(Object obj, int i) {
                            return WallpapersListActivity.this.onItemLongClick(this, obj, i);
                        }
                    };
                } else {
                    shadowSectionCell = new TextInfoPrivacyCell(this.mContext);
                    combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(str)), Theme.getThemedDrawable(this.mContext, NUM, str2));
                    combinedDrawable.setFullsize(true);
                    shadowSectionCell.setBackgroundDrawable(combinedDrawable);
                }
                anonymousClass1 = shadowSectionCell;
            } else {
                anonymousClass1 = new TextCell(this.mContext);
            }
            return new Holder(anonymousClass1);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
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
                i = (i - WallpapersListActivity.this.wallPaperStartRow) * WallpapersListActivity.this.columnsCount;
                wallpaperCell.setParams(WallpapersListActivity.this.columnsCount, i == 0, i / WallpapersListActivity.this.columnsCount == WallpapersListActivity.this.totalWallpaperRows - 1);
                for (itemViewType = 0; itemViewType < WallpapersListActivity.this.columnsCount; itemViewType++) {
                    long j;
                    int i2 = i + itemViewType;
                    Object obj = i2 < WallpapersListActivity.this.wallPapers.size() ? WallpapersListActivity.this.wallPapers.get(i2) : null;
                    String str = "";
                    if (obj instanceof TL_wallPaper) {
                        TL_wallPaper tL_wallPaper = (TL_wallPaper) obj;
                        OverrideWallpaperInfo overrideWallpaperInfo = Theme.getActiveTheme().overrideWallpaper;
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
                    wallpaperCell.setWallpaper(WallpapersListActivity.this.currentType, itemViewType, obj, str, null, false);
                    if (WallpapersListActivity.this.actionBar.isActionModeShowed()) {
                        wallpaperCell.setChecked(itemViewType, WallpapersListActivity.this.selectedWallPapers.indexOfKey(j2) >= 0, WallpapersListActivity.this.scrolling ^ 1);
                    } else {
                        wallpaperCell.setChecked(itemViewType, false, WallpapersListActivity.this.scrolling ^ 1);
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

    private class SearchAdapter extends SelectionAdapter {
        private boolean bingSearchEndReached = true;
        private int imageReqId;
        private RecyclerListView innerListView;
        private String lastSearchImageString;
        private String lastSearchString;
        private int lastSearchToken;
        private Context mContext;
        private String nextImagesSearchOffset;
        private ArrayList<SearchImage> searchResult = new ArrayList();
        private HashMap<String, SearchImage> searchResultKeys = new HashMap();
        private Runnable searchRunnable;
        private boolean searchingUser;
        private String selectedColor;

        private class CategoryAdapterRecycler extends SelectionAdapter {
            public boolean isEnabled(ViewHolder viewHolder) {
                return true;
            }

            private CategoryAdapterRecycler() {
            }

            /* synthetic */ CategoryAdapterRecycler(SearchAdapter searchAdapter, AnonymousClass1 anonymousClass1) {
                this();
            }

            public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                SearchAdapter searchAdapter = SearchAdapter.this;
                return new Holder(new ColorCell(searchAdapter.mContext));
            }

            public void onBindViewHolder(ViewHolder viewHolder, int i) {
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
            processSearch(null, true);
        }

        private void processSearch(String str, boolean z) {
            CharSequence str2;
            if (!(str2 == null || this.selectedColor == null)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("#color");
                stringBuilder.append(this.selectedColor);
                stringBuilder.append(" ");
                stringBuilder.append(str2);
                str2 = stringBuilder.toString();
            }
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(str2)) {
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
                    doSearch(str2);
                } else {
                    this.searchRunnable = new -$$Lambda$WallpapersListActivity$SearchAdapter$A_mUNZ6ShjO2kmNuqCpt4p0OR-4(this, str2);
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
                TL_contacts_resolveUsername tL_contacts_resolveUsername = new TL_contacts_resolveUsername();
                tL_contacts_resolveUsername.username = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).imageSearchBot;
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(tL_contacts_resolveUsername, new -$$Lambda$WallpapersListActivity$SearchAdapter$4mWogDs9zLmdvnE5jd5DGo_Q4ak(this));
            }
        }

        public /* synthetic */ void lambda$searchBotUser$2$WallpapersListActivity$SearchAdapter(TLObject tLObject, TL_error tL_error) {
            if (tLObject != null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$WallpapersListActivity$SearchAdapter$pRImZ4AMa9Bj9s9qQH5-U-q-hcU(this, tLObject));
            }
        }

        public /* synthetic */ void lambda$null$1$WallpapersListActivity$SearchAdapter(TLObject tLObject) {
            TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TL_contacts_resolvedPeer) tLObject;
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
            if (userOrChat instanceof User) {
                User user = (User) userOrChat;
                TL_messages_getInlineBotResults tL_messages_getInlineBotResults = new TL_messages_getInlineBotResults();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("#wallpaper ");
                stringBuilder.append(str);
                tL_messages_getInlineBotResults.query = stringBuilder.toString();
                tL_messages_getInlineBotResults.bot = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).getInputUser(user);
                tL_messages_getInlineBotResults.offset = str2;
                tL_messages_getInlineBotResults.peer = new TL_inputPeerEmpty();
                int i = this.lastSearchToken + 1;
                this.lastSearchToken = i;
                this.imageReqId = ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(tL_messages_getInlineBotResults, new -$$Lambda$WallpapersListActivity$SearchAdapter$EUxupb60ZwBIv1eg9iblwmPpQ9k(this, i));
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).bindRequestToGuid(this.imageReqId, WallpapersListActivity.this.classGuid);
                return;
            }
            if (z) {
                searchBotUser();
            }
        }

        public /* synthetic */ void lambda$searchImages$4$WallpapersListActivity$SearchAdapter(int i, TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$WallpapersListActivity$SearchAdapter$pzB92okjUzYhICjYxXfrmR5fs8g(this, i, tLObject));
        }

        public /* synthetic */ void lambda$null$3$WallpapersListActivity$SearchAdapter(int i, TLObject tLObject) {
            if (i == this.lastSearchToken) {
                boolean z = false;
                this.imageReqId = 0;
                int size = this.searchResult.size();
                if (tLObject != null) {
                    messages_BotResults messages_botresults = (messages_BotResults) tLObject;
                    this.nextImagesSearchOffset = messages_botresults.next_offset;
                    int size2 = messages_botresults.results.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        BotInlineResult botInlineResult = (BotInlineResult) messages_botresults.results.get(i2);
                        if ("photo".equals(botInlineResult.type) && !this.searchResultKeys.containsKey(botInlineResult.id)) {
                            SearchImage searchImage = new SearchImage();
                            Photo photo = botInlineResult.photo;
                            if (photo != null) {
                                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                                PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(botInlineResult.photo.sizes, 320);
                                if (closestPhotoSizeWithSize != null) {
                                    searchImage.width = closestPhotoSizeWithSize.w;
                                    searchImage.height = closestPhotoSizeWithSize.h;
                                    searchImage.photoSize = closestPhotoSizeWithSize;
                                    searchImage.photo = botInlineResult.photo;
                                    searchImage.size = closestPhotoSizeWithSize.size;
                                    searchImage.thumbPhotoSize = closestPhotoSizeWithSize2;
                                }
                            } else if (botInlineResult.content != null) {
                                for (int i3 = 0; i3 < botInlineResult.content.attributes.size(); i3++) {
                                    DocumentAttribute documentAttribute = (DocumentAttribute) botInlineResult.content.attributes.get(i3);
                                    if (documentAttribute instanceof TL_documentAttributeImageSize) {
                                        searchImage.width = documentAttribute.w;
                                        searchImage.height = documentAttribute.h;
                                        break;
                                    }
                                }
                                WebDocument webDocument = botInlineResult.thumb;
                                if (webDocument != null) {
                                    searchImage.thumbUrl = webDocument.url;
                                } else {
                                    searchImage.thumbUrl = null;
                                }
                                webDocument = botInlineResult.content;
                                searchImage.imageUrl = webDocument.url;
                                searchImage.size = webDocument.size;
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
                    i = size % WallpapersListActivity.this.columnsCount;
                    float f = (float) size;
                    size = (int) Math.ceil((double) (f / ((float) WallpapersListActivity.this.columnsCount)));
                    if (i != 0) {
                        notifyItemChanged(((int) Math.ceil((double) (f / ((float) WallpapersListActivity.this.columnsCount)))) - 1);
                    }
                    WallpapersListActivity.this.searchAdapter.notifyItemRangeInserted(size, ((int) Math.ceil((double) (((float) this.searchResult.size()) / ((float) WallpapersListActivity.this.columnsCount)))) - size);
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

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 2;
        }

        public /* synthetic */ void lambda$onCreateViewHolder$5$WallpapersListActivity$SearchAdapter(View view, int i) {
            String string = LocaleController.getString("BackgroundSearchColor", NUM);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(string);
            stringBuilder.append(" ");
            stringBuilder.append(LocaleController.getString(WallpapersListActivity.searchColorsNames[i], WallpapersListActivity.searchColorsNamesR[i]));
            SpannableString spannableString = new SpannableString(stringBuilder.toString());
            spannableString.setSpan(new ForegroundColorSpan(Theme.getColor("actionBarDefaultSubtitle")), string.length(), spannableString.length(), 33);
            WallpapersListActivity.this.searchItem.setSearchFieldCaption(spannableString);
            WallpapersListActivity.this.searchItem.setSearchFieldHint(null);
            String str = "";
            WallpapersListActivity.this.searchItem.setSearchFieldText(str, true);
            this.selectedColor = WallpapersListActivity.searchColorsNames[i];
            processSearch(str, true);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = null;
            if (i == 0) {
                view = new WallpaperCell(this.mContext) {
                    /* Access modifiers changed, original: protected */
                    public void onWallpaperClick(Object obj, int i) {
                        WallpapersListActivity.this.presentFragment(new ThemePreviewActivity(obj, null));
                    }
                };
            } else if (i == 1) {
                View anonymousClass2 = new RecyclerListView(this.mContext) {
                    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                        if (!(getParent() == null || getParent().getParent() == null)) {
                            getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                        }
                        return super.onInterceptTouchEvent(motionEvent);
                    }
                };
                anonymousClass2.setItemAnimator(null);
                anonymousClass2.setLayoutAnimation(null);
                AnonymousClass3 anonymousClass3 = new LinearLayoutManager(this.mContext) {
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                };
                anonymousClass2.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
                anonymousClass2.setClipToPadding(false);
                anonymousClass3.setOrientation(0);
                anonymousClass2.setLayoutManager(anonymousClass3);
                anonymousClass2.setAdapter(new CategoryAdapterRecycler(this, null));
                anonymousClass2.setOnItemClickListener(new -$$Lambda$WallpapersListActivity$SearchAdapter$oLQrjTlnoZYzJvyN-dx2q-irBAw(this));
                this.innerListView = anonymousClass2;
                view = anonymousClass2;
            } else if (i == 2) {
                view = new GraySectionCell(this.mContext);
            }
            if (i == 1) {
                view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(60.0f)));
            } else {
                view.setLayoutParams(new LayoutParams(-1, -2));
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                WallpaperCell wallpaperCell = (WallpaperCell) viewHolder.itemView;
                i *= WallpapersListActivity.this.columnsCount;
                itemViewType = (int) Math.ceil((double) (((float) this.searchResult.size()) / ((float) WallpapersListActivity.this.columnsCount)));
                int access$4300 = WallpapersListActivity.this.columnsCount;
                boolean z = true;
                boolean z2 = i == 0;
                if (i / WallpapersListActivity.this.columnsCount != itemViewType - 1) {
                    z = false;
                }
                wallpaperCell.setParams(access$4300, z2, z);
                for (int i2 = 0; i2 < WallpapersListActivity.this.columnsCount; i2++) {
                    itemViewType = i + i2;
                    wallpaperCell.setWallpaper(WallpapersListActivity.this.currentType, i2, itemViewType < this.searchResult.size() ? this.searchResult.get(itemViewType) : null, "", null, false);
                }
            } else if (itemViewType == 2) {
                ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString("SearchByColor", NUM));
            }
        }

        public int getItemViewType(int i) {
            if (TextUtils.isEmpty(this.lastSearchString)) {
                return i == 0 ? 2 : 1;
            } else {
                return 0;
            }
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
        this.colorFramePaint.setStyle(Style.STROKE);
        this.colorFramePaint.setColor(NUM);
        this.updater = new WallpaperUpdater(getParentActivity(), this, new WallpaperUpdaterDelegate() {
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
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (WallpapersListActivity.this.actionBar.isActionModeShowed()) {
                        WallpapersListActivity.this.selectedWallPapers.clear();
                        WallpapersListActivity.this.actionBar.hideActionMode();
                        WallpapersListActivity.this.updateRowsSelection();
                    } else {
                        WallpapersListActivity.this.finishFragment();
                    }
                } else if (i == 4) {
                    if (WallpapersListActivity.this.getParentActivity() != null) {
                        Builder builder = new Builder(WallpapersListActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.formatPluralString("DeleteBackground", WallpapersListActivity.this.selectedWallPapers.size()));
                        builder.setMessage(LocaleController.formatString("DeleteChatBackgroundsAlert", NUM, new Object[0]));
                        builder.setPositiveButton(LocaleController.getString("Delete", NUM), new -$$Lambda$WallpapersListActivity$2$K8aHn505ku1qQXr9dNGqIjZPpyE(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
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
                    dialogsActivity.setDelegate(new -$$Lambda$WallpapersListActivity$2$_pR5kSEFy3SmzguvrtK_EnN-KAw(this));
                    WallpapersListActivity.this.presentFragment(dialogsActivity);
                }
            }

            public /* synthetic */ void lambda$onItemClick$2$WallpapersListActivity$2(DialogInterface dialogInterface, int i) {
                WallpapersListActivity wallpapersListActivity = WallpapersListActivity.this;
                wallpapersListActivity.progressDialog = new AlertDialog(wallpapersListActivity.getParentActivity(), 3);
                i = 0;
                WallpapersListActivity.this.progressDialog.setCanCacnel(false);
                WallpapersListActivity.this.progressDialog.show();
                ArrayList arrayList = new ArrayList();
                int[] iArr = new int[]{WallpapersListActivity.this.selectedWallPapers.size()};
                while (i < WallpapersListActivity.this.selectedWallPapers.size()) {
                    TL_wallPaper tL_wallPaper = (TL_wallPaper) WallpapersListActivity.this.selectedWallPapers.valueAt(i);
                    TL_account_saveWallPaper tL_account_saveWallPaper = new TL_account_saveWallPaper();
                    tL_account_saveWallPaper.settings = new TL_wallPaperSettings();
                    tL_account_saveWallPaper.unsave = true;
                    TL_inputWallPaper tL_inputWallPaper = new TL_inputWallPaper();
                    tL_inputWallPaper.id = tL_wallPaper.id;
                    tL_inputWallPaper.access_hash = tL_wallPaper.access_hash;
                    tL_account_saveWallPaper.wallpaper = tL_inputWallPaper;
                    if (tL_wallPaper.slug.equals(WallpapersListActivity.this.selectedBackgroundSlug)) {
                        WallpapersListActivity.this.selectedBackgroundSlug = Theme.hasWallpaperFromTheme() ? "t" : "d";
                        Theme.getActiveTheme().setOverrideWallpaper(null);
                        Theme.reloadWallpaper();
                    }
                    ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(tL_account_saveWallPaper, new -$$Lambda$WallpapersListActivity$2$tLgeSLFOeRml08rU2UfCXZWRhT0(this, iArr));
                    i++;
                }
                WallpapersListActivity.this.selectedWallPapers.clear();
                WallpapersListActivity.this.actionBar.hideActionMode();
                WallpapersListActivity.this.actionBar.closeSearchField();
            }

            public /* synthetic */ void lambda$null$1$WallpapersListActivity$2(int[] iArr, TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$WallpapersListActivity$2$O4h5LLObyWcOKyfH3kpnNS7geto(this, iArr));
            }

            public /* synthetic */ void lambda$null$0$WallpapersListActivity$2(int[] iArr) {
                iArr[0] = iArr[0] - 1;
                if (iArr[0] == 0) {
                    WallpapersListActivity.this.loadWallpapers();
                }
            }

            public /* synthetic */ void lambda$onItemClick$3$WallpapersListActivity$2(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                int i;
                ArrayList arrayList2 = arrayList;
                StringBuilder stringBuilder = new StringBuilder();
                int i2 = 0;
                for (i = 0; i < WallpapersListActivity.this.selectedWallPapers.size(); i++) {
                    String wallPaperUrl = AndroidUtilities.getWallPaperUrl((TL_wallPaper) WallpapersListActivity.this.selectedWallPapers.valueAt(i));
                    if (!TextUtils.isEmpty(wallPaperUrl)) {
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(10);
                        }
                        stringBuilder.append(wallPaperUrl);
                    }
                }
                WallpapersListActivity.this.selectedWallPapers.clear();
                WallpapersListActivity.this.actionBar.hideActionMode();
                WallpapersListActivity.this.actionBar.closeSearchField();
                if (arrayList.size() > 1 || ((Long) arrayList2.get(0)).longValue() == ((long) UserConfig.getInstance(WallpapersListActivity.this.currentAccount).getClientUserId()) || charSequence != null) {
                    DialogsActivity dialogsActivity2 = dialogsActivity;
                    WallpapersListActivity.this.updateRowsSelection();
                    while (i2 < arrayList.size()) {
                        long longValue = ((Long) arrayList2.get(i2)).longValue();
                        if (charSequence != null) {
                            SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(charSequence.toString(), longValue, null, null, true, null, null, null, true, 0);
                        }
                        SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(stringBuilder.toString(), longValue, null, null, true, null, null, null, true, 0);
                        i2++;
                    }
                    dialogsActivity.finishFragment();
                } else {
                    long longValue2 = ((Long) arrayList2.get(0)).longValue();
                    int i3 = (int) longValue2;
                    i = (int) (longValue2 >> 32);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("scrollToTopOnResume", true);
                    if (i3 == 0) {
                        bundle.putInt("enc_id", i);
                    } else if (i3 > 0) {
                        bundle.putInt("user_id", i3);
                    } else if (i3 < 0) {
                        bundle.putInt("chat_id", -i3);
                    }
                    if (i3 == 0 || MessagesController.getInstance(WallpapersListActivity.this.currentAccount).checkCanOpenChat(bundle, dialogsActivity)) {
                        NotificationCenter.getInstance(WallpapersListActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        WallpapersListActivity.this.presentFragment(new ChatActivity(bundle), true);
                        SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(stringBuilder.toString(), longValue2, null, null, true, null, null, null, true, 0);
                    }
                }
            }
        });
        if (this.currentType == 0) {
            this.searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
                public void onSearchExpand() {
                    WallpapersListActivity.this.listView.setAdapter(WallpapersListActivity.this.searchAdapter);
                    WallpapersListActivity.this.listView.invalidate();
                }

                public void onSearchCollapse() {
                    WallpapersListActivity.this.listView.setAdapter(WallpapersListActivity.this.listAdapter);
                    WallpapersListActivity.this.listView.invalidate();
                    WallpapersListActivity.this.searchAdapter.processSearch(null, true);
                    WallpapersListActivity.this.searchItem.setSearchFieldCaption(null);
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
            String str = "actionBarDefaultIcon";
            this.actionBar.setItemsColor(Theme.getColor(str), true);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarDefaultSelector"), true);
            this.selectedMessagesCountTextView = new NumberTextView(createActionMode.getContext());
            this.selectedMessagesCountTextView.setTextSize(18);
            this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.selectedMessagesCountTextView.setTextColor(Theme.getColor(str));
            this.selectedMessagesCountTextView.setOnTouchListener(-$$Lambda$WallpapersListActivity$kDCUFe0ixQyVZvdd5on4Sg4XaNc.INSTANCE);
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

            /* JADX WARNING: Missing block: B:9:0x0033, code skipped:
            if (r0.itemView.getBottom() >= r1) goto L_0x0035;
     */
            public void onDraw(android.graphics.Canvas r15) {
                /*
                r14 = this;
                r0 = r14.getAdapter();
                r1 = org.telegram.ui.WallpapersListActivity.this;
                r1 = r1.listAdapter;
                if (r0 != r1) goto L_0x0020;
            L_0x000c:
                r0 = org.telegram.ui.WallpapersListActivity.this;
                r0 = r0.resetInfoRow;
                r1 = -1;
                if (r0 == r1) goto L_0x0020;
            L_0x0015:
                r0 = org.telegram.ui.WallpapersListActivity.this;
                r0 = r0.resetInfoRow;
                r0 = r14.findViewHolderForAdapterPosition(r0);
                goto L_0x0021;
            L_0x0020:
                r0 = 0;
            L_0x0021:
                r1 = r14.getMeasuredHeight();
                if (r0 == 0) goto L_0x0035;
            L_0x0027:
                r2 = r0.itemView;
                r2 = r2.getBottom();
                r0 = r0.itemView;
                r0 = r0.getBottom();
                if (r0 < r1) goto L_0x0036;
            L_0x0035:
                r2 = r1;
            L_0x0036:
                r0 = r14.paint;
                r3 = "windowBackgroundWhite";
                r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
                r0.setColor(r3);
                r5 = 0;
                r6 = 0;
                r0 = r14.getMeasuredWidth();
                r7 = (float) r0;
                r10 = (float) r2;
                r9 = r14.paint;
                r4 = r15;
                r8 = r10;
                r4.drawRect(r5, r6, r7, r8, r9);
                if (r2 == r1) goto L_0x006c;
            L_0x0053:
                r0 = r14.paint;
                r2 = "windowBackgroundGray";
                r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
                r0.setColor(r2);
                r9 = 0;
                r0 = r14.getMeasuredWidth();
                r11 = (float) r0;
                r12 = (float) r1;
                r13 = r14.paint;
                r8 = r15;
                r8.drawRect(r9, r10, r11, r12, r13);
            L_0x006c:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpapersListActivity$AnonymousClass4.onDraw(android.graphics.Canvas):void");
            }
        };
        this.listView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass5 anonymousClass5 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = anonymousClass5;
        recyclerListView.setLayoutManager(anonymousClass5);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.searchAdapter = new SearchAdapter(context);
        this.listView.setGlowColor(Theme.getColor("avatar_backgroundActionBarBlue"));
        this.listView.setOnItemClickListener(new -$$Lambda$WallpapersListActivity$PdMdfLigg_iWQcF5CiT4cSCfNQ8(this));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                boolean z = true;
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(WallpapersListActivity.this.getParentActivity().getCurrentFocus());
                }
                WallpapersListActivity wallpapersListActivity = WallpapersListActivity.this;
                if (i == 0) {
                    z = false;
                }
                wallpapersListActivity.scrolling = z;
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (WallpapersListActivity.this.listView.getAdapter() == WallpapersListActivity.this.searchAdapter) {
                    int findFirstVisibleItemPosition = WallpapersListActivity.this.layoutManager.findFirstVisibleItemPosition();
                    if (findFirstVisibleItemPosition == -1) {
                        i = 0;
                    } else {
                        i = Math.abs(WallpapersListActivity.this.layoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
                    }
                    if (i > 0) {
                        i2 = WallpapersListActivity.this.layoutManager.getItemCount();
                        if (i != 0 && findFirstVisibleItemPosition + i > i2 - 2) {
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
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("ResetChatBackgroundsAlertTitle", NUM));
                builder.setMessage(LocaleController.getString("ResetChatBackgroundsAlert", NUM));
                builder.setPositiveButton(LocaleController.getString("Reset", NUM), new -$$Lambda$WallpapersListActivity$IP2pJT3LolM38wDLb7Brfxjps6Q(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
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
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_resetWallPapers(), new -$$Lambda$WallpapersListActivity$YGDNH55IuocBWL16sPri4eLBLwQ(this));
    }

    public /* synthetic */ void lambda$null$1$WallpapersListActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$WallpapersListActivity$KngR6Mye-mVGWrCIie5abe_0Iqw(this));
    }

    public void onResume() {
        super.onResume();
        MessagesController.getGlobalMainSettings();
        OverrideWallpaperInfo overrideWallpaperInfo = Theme.getActiveTheme().overrideWallpaper;
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

    private boolean onItemLongClick(WallpaperCell wallpaperCell, Object obj, int i) {
        if (this.actionBar.isActionModeShowed() || getParentActivity() == null || !(obj instanceof TL_wallPaper)) {
            return false;
        }
        TL_wallPaper tL_wallPaper = (TL_wallPaper) obj;
        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        this.selectedWallPapers.put(tL_wallPaper.id, tL_wallPaper);
        this.selectedMessagesCountTextView.setNumber(1, false);
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.actionModeViews.size(); i2++) {
            View view = (View) this.actionModeViews.get(i2);
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

    private void onItemClick(WallpaperCell wallpaperCell, Object obj, int i) {
        boolean z = false;
        if (!this.actionBar.isActionModeShowed()) {
            String wallPaperSlug = getWallPaperSlug(obj);
            if (obj instanceof TL_wallPaper) {
                TL_wallPaper tL_wallPaper = (TL_wallPaper) obj;
                if (tL_wallPaper.pattern) {
                    String str = tL_wallPaper.slug;
                    WallPaperSettings wallPaperSettings = tL_wallPaper.settings;
                    int i2 = wallPaperSettings.background_color;
                    int i3 = wallPaperSettings.second_background_color;
                    int wallpaperRotation = AndroidUtilities.getWallpaperRotation(wallPaperSettings.rotation, false);
                    wallPaperSettings = tL_wallPaper.settings;
                    ColorWallpaper colorWallpaper = new ColorWallpaper(str, i2, i3, wallpaperRotation, ((float) wallPaperSettings.intensity) / 100.0f, wallPaperSettings.motion, null);
                    colorWallpaper.pattern = tL_wallPaper;
                }
            }
            ThemePreviewActivity themePreviewActivity = new ThemePreviewActivity(obj, null);
            if (this.currentType == 1) {
                themePreviewActivity.setDelegate(new -$$Lambda$obLeTjuOecC5VgqnkRKKKiZf1rM(this));
            }
            if (this.selectedBackgroundSlug.equals(wallPaperSlug)) {
                themePreviewActivity.setInitialModes(this.selectedBackgroundBlurred, this.selectedBackgroundMotion);
            }
            themePreviewActivity.setPatterns(this.patterns);
            presentFragment(themePreviewActivity);
        } else if (obj instanceof TL_wallPaper) {
            TL_wallPaper tL_wallPaper2 = (TL_wallPaper) obj;
            if (this.selectedWallPapers.indexOfKey(tL_wallPaper2.id) >= 0) {
                this.selectedWallPapers.remove(tL_wallPaper2.id);
            } else {
                this.selectedWallPapers.put(tL_wallPaper2.id, tL_wallPaper2);
            }
            if (this.selectedWallPapers.size() == 0) {
                this.actionBar.hideActionMode();
            } else {
                this.selectedMessagesCountTextView.setNumber(this.selectedWallPapers.size(), true);
            }
            this.scrolling = false;
            if (this.selectedWallPapers.indexOfKey(tL_wallPaper2.id) >= 0) {
                z = true;
            }
            wallpaperCell.setChecked(i, z, true);
        }
    }

    private String getWallPaperSlug(Object obj) {
        if (obj instanceof TL_wallPaper) {
            return ((TL_wallPaper) obj).slug;
        }
        if (obj instanceof ColorWallpaper) {
            return ((ColorWallpaper) obj).slug;
        }
        return obj instanceof FileWallpaper ? ((FileWallpaper) obj).slug : null;
    }

    private void updateRowsSelection() {
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
        if (i == NotificationCenter.wallpapersDidLoad) {
            i = 0;
            ArrayList arrayList = (ArrayList) objArr[0];
            this.patterns.clear();
            if (this.currentType != 1) {
                this.wallPapers.clear();
                this.allWallPapers.clear();
                this.allWallPapersDict.clear();
                this.allWallPapers.addAll(arrayList);
            }
            int size = arrayList.size();
            while (i < size) {
                TL_wallPaper tL_wallPaper = (TL_wallPaper) arrayList.get(i);
                if (tL_wallPaper.pattern) {
                    this.patterns.add(tL_wallPaper);
                }
                if (this.currentType != 1) {
                    if (tL_wallPaper.pattern) {
                        WallPaperSettings wallPaperSettings = tL_wallPaper.settings;
                        if (wallPaperSettings != null) {
                            if (wallPaperSettings.background_color == 0) {
                            }
                        }
                    }
                    this.allWallPapersDict.put(tL_wallPaper.slug, tL_wallPaper);
                    this.wallPapers.add(tL_wallPaper);
                }
                i++;
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

    private void loadWallpapers() {
        int size = this.allWallPapers.size();
        long j = 0;
        for (int i = 0; i < size; i++) {
            Object obj = this.allWallPapers.get(i);
            if (obj instanceof TL_wallPaper) {
                long j2 = ((TL_wallPaper) obj).id;
                j = (((((((j * 20261) + 2147483648L) + ((long) ((int) (j2 >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) j2))) % 2147483648L;
            }
        }
        TL_account_getWallPapers tL_account_getWallPapers = new TL_account_getWallPapers();
        tL_account_getWallPapers.hash = (int) j;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_getWallPapers, new -$$Lambda$WallpapersListActivity$rkTFtQlkaXRQn8loH7KW7DjD08g(this)), this.classGuid);
    }

    public /* synthetic */ void lambda$loadWallpapers$5$WallpapersListActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$WallpapersListActivity$oC7eCMcO7bYehKvUV1pnkpGiLa0(this, tLObject));
    }

    public /* synthetic */ void lambda$null$4$WallpapersListActivity(TLObject tLObject) {
        if (tLObject instanceof TL_account_wallPapers) {
            TL_account_wallPapers tL_account_wallPapers = (TL_account_wallPapers) tLObject;
            this.patterns.clear();
            if (this.currentType != 1) {
                this.wallPapers.clear();
                this.allWallPapersDict.clear();
                this.allWallPapers.clear();
                this.allWallPapers.addAll(tL_account_wallPapers.wallpapers);
            }
            int size = tL_account_wallPapers.wallpapers.size();
            for (int i = 0; i < size; i++) {
                TL_wallPaper tL_wallPaper = (TL_wallPaper) tL_account_wallPapers.wallpapers.get(i);
                this.allWallPapersDict.put(tL_wallPaper.slug, tL_wallPaper);
                if (tL_wallPaper.pattern) {
                    this.patterns.add(tL_wallPaper);
                }
                if (this.currentType != 1) {
                    if (tL_wallPaper.pattern) {
                        WallPaperSettings wallPaperSettings = tL_wallPaper.settings;
                        if (wallPaperSettings != null) {
                            if (wallPaperSettings.background_color == 0) {
                            }
                        }
                    }
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

    /* JADX WARNING: Removed duplicated region for block: B:54:0x010b  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0155  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0140  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0193  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x018b  */
    /* JADX WARNING: Missing block: B:28:0x0080, code skipped:
            if ((((float) r3.settings.intensity) - r15.selectedIntensity) <= 0.001f) goto L_0x0087;
     */
    private void fillWallpapersWithCustom() {
        /*
        r15 = this;
        r0 = r15.currentType;
        if (r0 == 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        org.telegram.messenger.MessagesController.getGlobalMainSettings();
        r0 = r15.addedColorWallpaper;
        r1 = 0;
        if (r0 == 0) goto L_0x0014;
    L_0x000d:
        r2 = r15.wallPapers;
        r2.remove(r0);
        r15.addedColorWallpaper = r1;
    L_0x0014:
        r0 = r15.addedFileWallpaper;
        if (r0 == 0) goto L_0x001f;
    L_0x0018:
        r2 = r15.wallPapers;
        r2.remove(r0);
        r15.addedFileWallpaper = r1;
    L_0x001f:
        r0 = r15.catsWallpaper;
        r2 = "d";
        if (r0 != 0) goto L_0x0033;
    L_0x0025:
        r0 = new org.telegram.ui.WallpapersListActivity$FileWallpaper;
        r3 = NUM; // 0x7var_c float:1.7944765E38 double:1.0529355485E-314;
        r4 = NUM; // 0x7var_ float:1.794484E38 double:1.052935567E-314;
        r0.<init>(r2, r3, r4);
        r15.catsWallpaper = r0;
        goto L_0x0038;
    L_0x0033:
        r3 = r15.wallPapers;
        r3.remove(r0);
    L_0x0038:
        r0 = r15.themeWallpaper;
        if (r0 == 0) goto L_0x0041;
    L_0x003c:
        r3 = r15.wallPapers;
        r3.remove(r0);
    L_0x0041:
        r0 = r15.allWallPapersDict;
        r3 = r15.selectedBackgroundSlug;
        r0 = r0.get(r3);
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper;
        r4 = 0;
        if (r3 == 0) goto L_0x008a;
    L_0x004e:
        r3 = r0;
        r3 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r3;
        r5 = org.telegram.ui.ActionBar.Theme.getActiveTheme();
        r5 = r5.overrideWallpaper;
        r5 = r3.settings;
        if (r5 == 0) goto L_0x0087;
    L_0x005b:
        r6 = r15.selectedColor;
        r7 = r5.background_color;
        if (r6 != r7) goto L_0x0082;
    L_0x0061:
        r6 = r15.selectedGradientColor;
        r7 = r5.second_background_color;
        if (r6 != r7) goto L_0x0082;
    L_0x0067:
        if (r6 == 0) goto L_0x0087;
    L_0x0069:
        r6 = r15.selectedGradientRotation;
        r5 = r5.rotation;
        r5 = org.telegram.messenger.AndroidUtilities.getWallpaperRotation(r5, r4);
        if (r6 == r5) goto L_0x0087;
    L_0x0073:
        r5 = r3.settings;
        r5 = r5.intensity;
        r5 = (float) r5;
        r6 = r15.selectedIntensity;
        r5 = r5 - r6;
        r6 = NUM; // 0x3a83126f float:0.001 double:4.85008663E-315;
        r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1));
        if (r5 <= 0) goto L_0x0087;
    L_0x0082:
        r0 = "";
        r5 = r3;
        r3 = r1;
        goto L_0x0090;
    L_0x0087:
        r3 = r15.selectedBackgroundSlug;
        goto L_0x008c;
    L_0x008a:
        r3 = r15.selectedBackgroundSlug;
    L_0x008c:
        r5 = r1;
        r14 = r3;
        r3 = r0;
        r0 = r14;
    L_0x0090:
        r6 = org.telegram.ui.ActionBar.Theme.getCurrentTheme();
        r6 = r6.isDark();
        r7 = r15.wallPapers;
        r8 = new org.telegram.ui.-$$Lambda$WallpapersListActivity$jUk8XvxaDgRddt16z7pmf9R62MM;
        r8.<init>(r15, r0, r6);
        java.util.Collections.sort(r7, r8);
        r0 = org.telegram.ui.ActionBar.Theme.hasWallpaperFromTheme();
        if (r0 == 0) goto L_0x00c4;
    L_0x00a8:
        r0 = org.telegram.ui.ActionBar.Theme.isThemeWallpaperPublic();
        if (r0 != 0) goto L_0x00c4;
    L_0x00ae:
        r0 = r15.themeWallpaper;
        if (r0 != 0) goto L_0x00bc;
    L_0x00b2:
        r0 = new org.telegram.ui.WallpapersListActivity$FileWallpaper;
        r1 = -2;
        r6 = "t";
        r0.<init>(r6, r1, r1);
        r15.themeWallpaper = r0;
    L_0x00bc:
        r0 = r15.wallPapers;
        r1 = r15.themeWallpaper;
        r0.add(r4, r1);
        goto L_0x00c6;
    L_0x00c4:
        r15.themeWallpaper = r1;
    L_0x00c6:
        r0 = org.telegram.ui.ActionBar.Theme.getActiveTheme();
        r1 = r15.selectedBackgroundSlug;
        r1 = android.text.TextUtils.isEmpty(r1);
        r6 = "c";
        if (r1 != 0) goto L_0x0103;
    L_0x00d4:
        r1 = r15.selectedBackgroundSlug;
        r1 = r2.equals(r1);
        if (r1 != 0) goto L_0x00df;
    L_0x00dc:
        if (r3 != 0) goto L_0x00df;
    L_0x00de:
        goto L_0x0103;
    L_0x00df:
        r0 = r15.selectedColor;
        if (r0 == 0) goto L_0x0183;
    L_0x00e3:
        r0 = r15.selectedBackgroundSlug;
        r0 = r6.equals(r0);
        if (r0 == 0) goto L_0x0183;
    L_0x00eb:
        r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper;
        r1 = r15.selectedBackgroundSlug;
        r3 = r15.selectedColor;
        r5 = r15.selectedGradientColor;
        r6 = r15.selectedGradientRotation;
        r0.<init>(r1, r3, r5, r6);
        r15.addedColorWallpaper = r0;
        r0 = r15.wallPapers;
        r1 = r15.addedColorWallpaper;
        r0.add(r4, r1);
        goto L_0x0183;
    L_0x0103:
        r1 = r15.selectedBackgroundSlug;
        r1 = r6.equals(r1);
        if (r1 != 0) goto L_0x013c;
    L_0x010b:
        r8 = r15.selectedColor;
        if (r8 == 0) goto L_0x013c;
    L_0x010f:
        r1 = r0.overrideWallpaper;
        if (r1 == 0) goto L_0x0183;
    L_0x0113:
        r1 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper;
        r7 = r15.selectedBackgroundSlug;
        r9 = r15.selectedGradientColor;
        r10 = r15.selectedGradientRotation;
        r11 = r15.selectedIntensity;
        r12 = r15.selectedBackgroundMotion;
        r13 = new java.io.File;
        r3 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
        r0 = r0.overrideWallpaper;
        r0 = r0.fileName;
        r13.<init>(r3, r0);
        r6 = r1;
        r6.<init>(r7, r8, r9, r10, r11, r12, r13);
        r15.addedColorWallpaper = r1;
        r0 = r15.addedColorWallpaper;
        r0.pattern = r5;
        r1 = r15.wallPapers;
        r1.add(r4, r0);
        goto L_0x0183;
    L_0x013c:
        r1 = r15.selectedColor;
        if (r1 == 0) goto L_0x0155;
    L_0x0140:
        r0 = new org.telegram.ui.WallpapersListActivity$ColorWallpaper;
        r3 = r15.selectedBackgroundSlug;
        r5 = r15.selectedGradientColor;
        r6 = r15.selectedGradientRotation;
        r0.<init>(r3, r1, r5, r6);
        r15.addedColorWallpaper = r0;
        r0 = r15.wallPapers;
        r1 = r15.addedColorWallpaper;
        r0.add(r4, r1);
        goto L_0x0183;
    L_0x0155:
        r1 = r0.overrideWallpaper;
        if (r1 == 0) goto L_0x0183;
    L_0x0159:
        r1 = new org.telegram.ui.WallpapersListActivity$FileWallpaper;
        r3 = r15.selectedBackgroundSlug;
        r5 = new java.io.File;
        r6 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
        r7 = r0.overrideWallpaper;
        r7 = r7.fileName;
        r5.<init>(r6, r7);
        r6 = new java.io.File;
        r7 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
        r0 = r0.overrideWallpaper;
        r0 = r0.originalFileName;
        r6.<init>(r7, r0);
        r1.<init>(r3, r5, r6);
        r15.addedFileWallpaper = r1;
        r0 = r15.wallPapers;
        r1 = r15.addedFileWallpaper;
        r0.add(r4, r1);
    L_0x0183:
        r0 = r15.selectedBackgroundSlug;
        r0 = r2.equals(r0);
        if (r0 == 0) goto L_0x0193;
    L_0x018b:
        r0 = r15.wallPapers;
        r1 = r15.catsWallpaper;
        r0.add(r4, r1);
        goto L_0x019a;
    L_0x0193:
        r0 = r15.wallPapers;
        r1 = r15.catsWallpaper;
        r0.add(r1);
    L_0x019a:
        r15.updateRows();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.WallpapersListActivity.fillWallpapersWithCustom():void");
    }

    public /* synthetic */ int lambda$fillWallpapersWithCustom$6$WallpapersListActivity(String str, boolean z, Object obj, Object obj2) {
        if (!(obj instanceof TL_wallPaper) || !(obj2 instanceof TL_wallPaper)) {
            return 0;
        }
        TL_wallPaper tL_wallPaper = (TL_wallPaper) obj;
        TL_wallPaper tL_wallPaper2 = (TL_wallPaper) obj2;
        if (str.equals(tL_wallPaper.slug)) {
            return -1;
        }
        if (str.equals(tL_wallPaper2.slug)) {
            return 1;
        }
        int indexOf = this.allWallPapers.indexOf(tL_wallPaper);
        int indexOf2 = this.allWallPapers.indexOf(tL_wallPaper2);
        if (!(tL_wallPaper.dark && tL_wallPaper2.dark) && (tL_wallPaper.dark || tL_wallPaper2.dark)) {
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
        int i;
        this.rowCount = 0;
        if (this.currentType == 0) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.uploadImageRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.setColorRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.sectionRow = i;
        } else {
            this.uploadImageRow = -1;
            this.setColorRow = -1;
            this.sectionRow = -1;
        }
        if (this.wallPapers.isEmpty()) {
            this.wallPaperStartRow = -1;
        } else {
            this.totalWallpaperRows = (int) Math.ceil((double) (((float) this.wallPapers.size()) / ((float) this.columnsCount)));
            i = this.rowCount;
            this.wallPaperStartRow = i;
            this.rowCount = i + this.totalWallpaperRows;
        }
        if (this.currentType == 0) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.resetSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.resetRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.resetInfoRow = i;
        } else {
            this.resetSectionRow = -1;
            this.resetRow = -1;
            this.resetInfoRow = -1;
        }
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            this.scrolling = true;
            listAdapter.notifyDataSetChanged();
        }
    }

    private void fixLayout() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    WallpapersListActivity.this.fixLayoutInternal();
                    if (WallpapersListActivity.this.listView != null) {
                        WallpapersListActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    private void fixLayoutInternal() {
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

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[21];
        r1[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r1[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGray");
        View view = this.listView;
        Class[] clsArr = new Class[]{TextInfoPrivacyCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        r1[10] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText4");
        r1[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r1[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGray");
        r1[13] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r1[14] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        r1[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        r1[16] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, "key_graySectionText");
        r1[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection");
        r1[18] = new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        r1[19] = new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        r1[20] = new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        return r1;
    }
}
