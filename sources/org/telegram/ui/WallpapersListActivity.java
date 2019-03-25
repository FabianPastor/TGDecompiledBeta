package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
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
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
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
import org.telegram.tgnet.TLRPC.messages_BotResults;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
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
    private LongSparseArray<Object> allWallPapersDict = new LongSparseArray();
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
    private long selectedBackground;
    private boolean selectedBackgroundBlurred;
    private boolean selectedBackgroundMotion;
    private int selectedColor;
    private float selectedIntensity;
    private NumberTextView selectedMessagesCountTextView;
    private long selectedPattern;
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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(AndroidUtilities.dp(50.0f), AndroidUtilities.dp(62.0f));
        }

        public void setColor(int value) {
            this.color = value;
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
        public long id;
        public float intensity;
        public boolean motion;
        public File path;
        public TL_wallPaper pattern;
        public long patternId;

        public ColorWallpaper(long i, int c) {
            this.id = i;
            this.color = -16777216 | c;
            this.intensity = 1.0f;
        }

        public ColorWallpaper(long i, int c, long p, float in, boolean m, File ph) {
            this.id = i;
            this.color = -16777216 | c;
            this.patternId = p;
            this.intensity = in;
            this.path = ph;
            this.motion = m;
        }
    }

    public static class FileWallpaper {
        public long id;
        public File originalPath;
        public File path;
        public int resId;
        public int thumbResId;

        public FileWallpaper(long i, File f, File of) {
            this.id = i;
            this.path = f;
            this.originalPath = of;
        }

        public FileWallpaper(long i, String f) {
            this.id = i;
            this.path = new File(f);
        }

        public FileWallpaper(long i, int r, int t) {
            this.id = i;
            this.resId = r;
            this.thumbResId = t;
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public int getItemCount() {
            return WallpapersListActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            int i = NUM;
            CombinedDrawable combinedDrawable;
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
                    combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(context, i, "windowBackgroundGrayShadow"));
                    combinedDrawable.setFullsize(true);
                    view.setBackgroundDrawable(combinedDrawable);
                    break;
                case 3:
                    view = new TextInfoPrivacyCell(this.mContext);
                    combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    combinedDrawable.setFullsize(true);
                    view.setBackgroundDrawable(combinedDrawable);
                    break;
                default:
                    view = new WallpaperCell(this.mContext) {
                        /* Access modifiers changed, original: protected */
                        public void onWallpaperClick(Object wallPaper, int index) {
                            WallpapersListActivity.this.onItemClick(this, wallPaper, index);
                        }

                        /* Access modifiers changed, original: protected */
                        public boolean onWallpaperLongClick(Object wallPaper, int index) {
                            return WallpapersListActivity.this.onItemLongClick(this, wallPaper, index);
                        }
                    };
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    TextCell textCell = holder.itemView;
                    if (position == WallpapersListActivity.this.uploadImageRow) {
                        textCell.setTextAndIcon(LocaleController.getString("SelectFromGallery", NUM), NUM, true);
                        return;
                    } else if (position == WallpapersListActivity.this.setColorRow) {
                        textCell.setTextAndIcon(LocaleController.getString("SetColor", NUM), NUM, false);
                        return;
                    } else if (position == WallpapersListActivity.this.resetRow) {
                        textCell.setText(LocaleController.getString("ResetChatBackgrounds", NUM), false);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    WallpaperCell wallpaperCell = holder.itemView;
                    position = (position - WallpapersListActivity.this.wallPaperStartRow) * WallpapersListActivity.this.columnsCount;
                    wallpaperCell.setParams(WallpapersListActivity.this.columnsCount, position == 0, position / WallpapersListActivity.this.columnsCount == WallpapersListActivity.this.totalWallpaperRows + -1);
                    for (int a = 0; a < WallpapersListActivity.this.columnsCount; a++) {
                        long id;
                        int p = position + a;
                        TL_wallPaper wallPaper = p < WallpapersListActivity.this.wallPapers.size() ? WallpapersListActivity.this.wallPapers.get(p) : null;
                        wallpaperCell.setWallpaper(WallpapersListActivity.this.currentType, a, wallPaper, WallpapersListActivity.this.selectedBackground, null, false);
                        if (wallPaper instanceof TL_wallPaper) {
                            id = wallPaper.id;
                        } else {
                            id = 0;
                        }
                        if (WallpapersListActivity.this.actionBar.isActionModeShowed()) {
                            boolean z;
                            boolean z2;
                            if (WallpapersListActivity.this.selectedWallPapers.indexOfKey(id) >= 0) {
                                z = true;
                            } else {
                                z = false;
                            }
                            if (WallpapersListActivity.this.scrolling) {
                                z2 = false;
                            } else {
                                z2 = true;
                            }
                            wallpaperCell.setChecked(a, z, z2);
                        } else {
                            wallpaperCell.setChecked(a, false, !WallpapersListActivity.this.scrolling);
                        }
                    }
                    return;
                case 3:
                    TextInfoPrivacyCell cell = holder.itemView;
                    if (position == WallpapersListActivity.this.resetInfoRow) {
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
            private CategoryAdapterRecycler() {
            }

            /* synthetic */ CategoryAdapterRecycler(SearchAdapter x0, AnonymousClass1 x1) {
                this();
            }

            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new Holder(new ColorCell(SearchAdapter.this.mContext));
            }

            public boolean isEnabled(ViewHolder holder) {
                return true;
            }

            public void onBindViewHolder(ViewHolder holder, int position) {
                holder.itemView.setColor(WallpapersListActivity.searchColors[position]);
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

        private void processSearch(String text, boolean now) {
            if (!(text == null || this.selectedColor == null)) {
                text = "#color" + this.selectedColor + " " + text;
            }
            if (this.searchRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
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
                    this.searchRunnable = new WallpapersListActivity$SearchAdapter$$Lambda$0(this, textFinal);
                    AndroidUtilities.runOnUIThread(this.searchRunnable, 500);
                }
            }
            notifyDataSetChanged();
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$processSearch$0$WallpapersListActivity$SearchAdapter(String textFinal) {
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
                TL_contacts_resolveUsername req = new TL_contacts_resolveUsername();
                req.username = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).imageSearchBot;
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(req, new WallpapersListActivity$SearchAdapter$$Lambda$1(this));
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$searchBotUser$2$WallpapersListActivity$SearchAdapter(TLObject response, TL_error error) {
            if (response != null) {
                AndroidUtilities.runOnUIThread(new WallpapersListActivity$SearchAdapter$$Lambda$5(this, response));
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$1$WallpapersListActivity$SearchAdapter(TLObject response) {
            TL_contacts_resolvedPeer res = (TL_contacts_resolvedPeer) response;
            MessagesController.getInstance(WallpapersListActivity.this.currentAccount).putUsers(res.users, false);
            MessagesController.getInstance(WallpapersListActivity.this.currentAccount).putChats(res.chats, false);
            MessagesStorage.getInstance(WallpapersListActivity.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
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
            if (object instanceof User) {
                User user = (User) object;
                TL_messages_getInlineBotResults req = new TL_messages_getInlineBotResults();
                req.query = "#wallpaper " + query;
                req.bot = MessagesController.getInstance(WallpapersListActivity.this.currentAccount).getInputUser(user);
                req.offset = offset;
                req.peer = new TL_inputPeerEmpty();
                int token = this.lastSearchToken + 1;
                this.lastSearchToken = token;
                this.imageReqId = ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(req, new WallpapersListActivity$SearchAdapter$$Lambda$2(this, token));
                ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).bindRequestToGuid(this.imageReqId, WallpapersListActivity.this.classGuid);
            } else if (searchUser) {
                searchBotUser();
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$searchImages$4$WallpapersListActivity$SearchAdapter(int token, TLObject response, TL_error error) {
            AndroidUtilities.runOnUIThread(new WallpapersListActivity$SearchAdapter$$Lambda$4(this, token, response));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$3$WallpapersListActivity$SearchAdapter(int token, TLObject response) {
            if (token == this.lastSearchToken) {
                this.imageReqId = 0;
                int oldCount = this.searchResult.size();
                if (response != null) {
                    messages_BotResults res = (messages_BotResults) response;
                    this.nextImagesSearchOffset = res.next_offset;
                    int count = res.results.size();
                    for (int a = 0; a < count; a++) {
                        BotInlineResult result = (BotInlineResult) res.results.get(a);
                        if ("photo".equals(result.type) && !this.searchResultKeys.containsKey(result.id)) {
                            SearchImage bingImage = new SearchImage();
                            if (result.photo != null) {
                                PhotoSize size = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, AndroidUtilities.getPhotoSize());
                                PhotoSize size2 = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, 320);
                                if (size != null) {
                                    bingImage.width = size.w;
                                    bingImage.height = size.h;
                                    bingImage.photoSize = size;
                                    bingImage.photo = result.photo;
                                    bingImage.size = size.size;
                                    bingImage.thumbPhotoSize = size2;
                                }
                            } else if (result.content != null) {
                                for (int b = 0; b < result.content.attributes.size(); b++) {
                                    DocumentAttribute attribute = (DocumentAttribute) result.content.attributes.get(b);
                                    if (attribute instanceof TL_documentAttributeImageSize) {
                                        bingImage.width = attribute.w;
                                        bingImage.height = attribute.h;
                                        break;
                                    }
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
                            bingImage.localUrl = "";
                            this.searchResult.add(bingImage);
                            this.searchResultKeys.put(bingImage.id, bingImage);
                        }
                    }
                    boolean z = oldCount == this.searchResult.size() || this.nextImagesSearchOffset == null;
                    this.bingSearchEndReached = z;
                }
                if (oldCount != this.searchResult.size()) {
                    int oldRowCount = (int) Math.ceil((double) (((float) oldCount) / ((float) WallpapersListActivity.this.columnsCount)));
                    if (oldCount % WallpapersListActivity.this.columnsCount != 0) {
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

        public boolean isEnabled(ViewHolder holder) {
            return holder.getItemViewType() != 2;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new WallpaperCell(this.mContext) {
                        /* Access modifiers changed, original: protected */
                        public void onWallpaperClick(Object wallPaper, int index) {
                            WallpapersListActivity.this.presentFragment(new WallpaperActivity(wallPaper, null));
                        }
                    };
                    break;
                case 1:
                    View horizontalListView = new RecyclerListView(this.mContext) {
                        public boolean onInterceptTouchEvent(MotionEvent e) {
                            if (!(getParent() == null || getParent().getParent() == null)) {
                                getParent().getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            return super.onInterceptTouchEvent(e);
                        }
                    };
                    horizontalListView.setItemAnimator(null);
                    horizontalListView.setLayoutAnimation(null);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this.mContext) {
                        public boolean supportsPredictiveItemAnimations() {
                            return false;
                        }
                    };
                    horizontalListView.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
                    horizontalListView.setClipToPadding(false);
                    layoutManager.setOrientation(0);
                    horizontalListView.setLayoutManager(layoutManager);
                    horizontalListView.setAdapter(new CategoryAdapterRecycler(this, null));
                    horizontalListView.setOnItemClickListener(new WallpapersListActivity$SearchAdapter$$Lambda$3(this));
                    view = horizontalListView;
                    this.innerListView = horizontalListView;
                    break;
                case 2:
                    view = new GraySectionCell(this.mContext);
                    break;
            }
            if (viewType == 1) {
                view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(60.0f)));
            } else {
                view.setLayoutParams(new LayoutParams(-1, -2));
            }
            return new Holder(view);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onCreateViewHolder$5$WallpapersListActivity$SearchAdapter(View view1, int position) {
            String color = LocaleController.getString("BackgroundSearchColor", NUM);
            Spannable spannable = new SpannableString(color + " " + LocaleController.getString(WallpapersListActivity.searchColorsNames[position], WallpapersListActivity.searchColorsNamesR[position]));
            spannable.setSpan(new ForegroundColorSpan(Theme.getColor("actionBarDefaultSubtitle")), color.length(), spannable.length(), 33);
            WallpapersListActivity.this.searchItem.setSearchFieldCaption(spannable);
            WallpapersListActivity.this.searchItem.setSearchFieldHint(null);
            WallpapersListActivity.this.searchItem.setSearchFieldText("", true);
            this.selectedColor = WallpapersListActivity.searchColorsNames[position];
            processSearch("", true);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    WallpaperCell wallpaperCell = holder.itemView;
                    position *= WallpapersListActivity.this.columnsCount;
                    wallpaperCell.setParams(WallpapersListActivity.this.columnsCount, position == 0, position / WallpapersListActivity.this.columnsCount == ((int) Math.ceil((double) (((float) this.searchResult.size()) / ((float) WallpapersListActivity.this.columnsCount)))) + -1);
                    for (int a = 0; a < WallpapersListActivity.this.columnsCount; a++) {
                        int p = position + a;
                        wallpaperCell.setWallpaper(WallpapersListActivity.this.currentType, a, p < this.searchResult.size() ? this.searchResult.get(p) : null, 0, null, false);
                    }
                    return;
                case 2:
                    holder.itemView.setText(LocaleController.getString("SearchByColor", NUM));
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

    /* Access modifiers changed, original: final|bridge|synthetic */
    public final /* bridge */ /* synthetic */ void bridge$lambda$0$WallpapersListActivity() {
        loadWallpapers();
    }

    public WallpapersListActivity(int type) {
        this.currentType = type;
    }

    public boolean onFragmentCreate() {
        if (this.currentType == 0) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersNeedReload);
            MessagesStorage.getInstance(this.currentAccount).getWallpapers();
        } else {
            for (int a = 0; a < defaultColors.length; a++) {
                this.wallPapers.add(new ColorWallpaper((long) (-(a + 3)), defaultColors[a]));
            }
            if (this.currentType == 1 && this.patterns.isEmpty()) {
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
                MessagesStorage.getInstance(this.currentAccount).getWallpapers();
            }
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        if (this.currentType == 0) {
            this.searchAdapter.onDestroy();
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersNeedReload);
        } else if (this.currentType == 1) {
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
            public void didSelectWallpaper(File file, Bitmap bitmap, boolean gallery) {
                WallpapersListActivity.this.presentFragment(new WallpaperActivity(new FileWallpaper(-1, file, file), bitmap), gallery);
            }

            public void needOpenColorPicker() {
            }
        });
        this.hasOwnBackground = true;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChatBackground", NUM));
        } else if (this.currentType == 1) {
            this.actionBar.setTitle(LocaleController.getString("SelectColorTitle", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
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
                        Builder builder = new Builder(WallpapersListActivity.this.getParentActivity());
                        builder.setMessage(LocaleController.formatString("DeleteChatBackgroundsAlert", NUM, new Object[0]));
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setPositiveButton(LocaleController.getString("OK", NUM), new WallpapersListActivity$2$$Lambda$0(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                        WallpapersListActivity.this.showDialog(builder.create());
                    }
                } else if (id == 3) {
                    Bundle args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putInt("dialogsType", 3);
                    DialogsActivity fragment = new DialogsActivity(args);
                    fragment.setDelegate(new WallpapersListActivity$2$$Lambda$1(this));
                    WallpapersListActivity.this.presentFragment(fragment);
                }
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$onItemClick$2$WallpapersListActivity$2(DialogInterface dialogInterface, int i) {
                WallpapersListActivity.this.progressDialog = new AlertDialog(WallpapersListActivity.this.getParentActivity(), 3);
                WallpapersListActivity.this.progressDialog.setCanCacnel(false);
                WallpapersListActivity.this.progressDialog.show();
                ArrayList<Integer> ids = new ArrayList();
                int[] deleteCount = new int[]{WallpapersListActivity.this.selectedWallPapers.size()};
                for (int b = 0; b < WallpapersListActivity.this.selectedWallPapers.size(); b++) {
                    TL_wallPaper wallPaper = (TL_wallPaper) WallpapersListActivity.this.selectedWallPapers.valueAt(b);
                    TL_account_saveWallPaper req = new TL_account_saveWallPaper();
                    req.settings = new TL_wallPaperSettings();
                    req.unsave = true;
                    TL_inputWallPaper inputWallPaper = new TL_inputWallPaper();
                    inputWallPaper.id = wallPaper.id;
                    inputWallPaper.access_hash = wallPaper.access_hash;
                    req.wallpaper = inputWallPaper;
                    if (wallPaper.id == WallpapersListActivity.this.selectedBackground) {
                        WallpapersListActivity.this.selectedBackground = 1000001;
                        Editor editor = MessagesController.getGlobalMainSettings().edit();
                        editor.putLong("selectedBackground2", WallpapersListActivity.this.selectedBackground);
                        editor.putBoolean("selectedBackgroundBlurred", false);
                        editor.putBoolean("selectedBackgroundMotion", false);
                        editor.putInt("selectedColor", 0);
                        editor.putFloat("selectedIntensity", 1.0f);
                        editor.putLong("selectedPattern", 0);
                        editor.putBoolean("overrideThemeWallpaper", true);
                        editor.commit();
                        Theme.reloadWallpaper();
                    }
                    ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).sendRequest(req, new WallpapersListActivity$2$$Lambda$2(this, deleteCount));
                }
                WallpapersListActivity.this.selectedWallPapers.clear();
                WallpapersListActivity.this.actionBar.hideActionMode();
                WallpapersListActivity.this.actionBar.closeSearchField();
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$null$1$WallpapersListActivity$2(int[] deleteCount, TLObject response, TL_error error) {
                AndroidUtilities.runOnUIThread(new WallpapersListActivity$2$$Lambda$3(this, deleteCount));
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$null$0$WallpapersListActivity$2(int[] deleteCount) {
                deleteCount[0] = deleteCount[0] - 1;
                if (deleteCount[0] == 0) {
                    WallpapersListActivity.this.loadWallpapers();
                }
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$onItemClick$3$WallpapersListActivity$2(DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
                StringBuilder fmessage = new StringBuilder();
                for (int b = 0; b < WallpapersListActivity.this.selectedWallPapers.size(); b++) {
                    String link = AndroidUtilities.getWallPaperUrl((TL_wallPaper) WallpapersListActivity.this.selectedWallPapers.valueAt(b), WallpapersListActivity.this.currentAccount);
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
                long did;
                if (dids.size() > 1 || ((Long) dids.get(0)).longValue() == ((long) UserConfig.getInstance(WallpapersListActivity.this.currentAccount).getClientUserId()) || message != null) {
                    WallpapersListActivity.this.updateRowsSelection();
                    for (int a = 0; a < dids.size(); a++) {
                        did = ((Long) dids.get(a)).longValue();
                        if (message != null) {
                            SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(message.toString(), did, null, null, true, null, null, null);
                        }
                        SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(fmessage.toString(), did, null, null, true, null, null, null);
                    }
                    fragment1.finishFragment();
                    return;
                }
                did = ((Long) dids.get(0)).longValue();
                int lower_part = (int) did;
                int high_part = (int) (did >> 32);
                Bundle args1 = new Bundle();
                args1.putBoolean("scrollToTopOnResume", true);
                if (lower_part == 0) {
                    args1.putInt("enc_id", high_part);
                } else if (lower_part > 0) {
                    args1.putInt("user_id", lower_part);
                } else if (lower_part < 0) {
                    args1.putInt("chat_id", -lower_part);
                }
                if (lower_part == 0 || MessagesController.getInstance(WallpapersListActivity.this.currentAccount).checkCanOpenChat(args1, fragment1)) {
                    NotificationCenter.getInstance(WallpapersListActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    WallpapersListActivity.this.presentFragment(new ChatActivity(args1), true);
                    SendMessagesHelper.getInstance(WallpapersListActivity.this.currentAccount).sendMessage(fmessage.toString(), did, null, null, true, null, null, null);
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
            ActionBarMenu actionMode = this.actionBar.createActionMode(false);
            actionMode.setBackgroundColor(Theme.getColor("actionBarDefault"));
            this.actionBar.setItemsColor(Theme.getColor("actionBarDefaultIcon"), true);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarDefaultSelector"), true);
            this.selectedMessagesCountTextView = new NumberTextView(actionMode.getContext());
            this.selectedMessagesCountTextView.setTextSize(18);
            this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.selectedMessagesCountTextView.setTextColor(Theme.getColor("actionBarDefaultIcon"));
            this.selectedMessagesCountTextView.setOnTouchListener(WallpapersListActivity$$Lambda$0.$instance);
            actionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 65, 0, 0, 0));
            this.actionModeViews.add(actionMode.addItemWithWidth(3, NUM, AndroidUtilities.dp(54.0f)));
            this.actionModeViews.add(actionMode.addItemWithWidth(4, NUM, AndroidUtilities.dp(54.0f)));
            this.selectedWallPapers.clear();
        }
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context) {
            private Paint paint = new Paint();

            public boolean hasOverlappingRendering() {
                return false;
            }

            public void onDraw(Canvas c) {
                ViewHolder holder;
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
        this.listView.setOnItemClickListener(new WallpapersListActivity$$Lambda$1(this));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean z = true;
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(WallpapersListActivity.this.getParentActivity().getCurrentFocus());
                }
                WallpapersListActivity wallpapersListActivity = WallpapersListActivity.this;
                if (newState == 0) {
                    z = false;
                }
                wallpapersListActivity.scrolling = z;
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

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$3$WallpapersListActivity(View view, int position) {
        if (getParentActivity() != null && this.listView.getAdapter() != this.searchAdapter) {
            if (position == this.uploadImageRow) {
                this.updater.openGallery();
            } else if (position == this.setColorRow) {
                WallpapersListActivity activity = new WallpapersListActivity(1);
                activity.patterns = this.patterns;
                presentFragment(activity);
            } else if (position == this.resetRow) {
                Builder builder = new Builder(getParentActivity());
                builder.setMessage(LocaleController.getString("ResetChatBackgroundsAlert", NUM));
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setPositiveButton(LocaleController.getString("Reset", NUM), new WallpapersListActivity$$Lambda$6(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                showDialog(builder.create());
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$2$WallpapersListActivity(DialogInterface dialogInterface, int i) {
        if (this.actionBar.isActionModeShowed()) {
            this.selectedWallPapers.clear();
            this.actionBar.hideActionMode();
            updateRowsSelection();
        }
        this.progressDialog = new AlertDialog(getParentActivity(), 3);
        this.progressDialog.setCanCacnel(false);
        this.progressDialog.show();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_resetWallPapers(), new WallpapersListActivity$$Lambda$7(this));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$1$WallpapersListActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new WallpapersListActivity$$Lambda$8(this));
    }

    public void onResume() {
        super.onResume();
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        this.selectedBackground = Theme.getSelectedBackgroundId();
        this.selectedPattern = preferences.getLong("selectedPattern", 0);
        this.selectedColor = preferences.getInt("selectedColor", 0);
        this.selectedIntensity = preferences.getFloat("selectedIntensity", 1.0f);
        this.selectedBackgroundMotion = preferences.getBoolean("selectedBackgroundMotion", false);
        this.selectedBackgroundBlurred = preferences.getBoolean("selectedBackgroundBlurred", false);
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

    private boolean onItemLongClick(WallpaperCell view, Object object, int index) {
        if (this.actionBar.isActionModeShowed() || getParentActivity() == null || !(object instanceof TL_wallPaper)) {
            return false;
        }
        TL_wallPaper wallPaper = (TL_wallPaper) object;
        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
        this.selectedWallPapers.put(wallPaper.id, wallPaper);
        this.selectedMessagesCountTextView.setNumber(1, false);
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList();
        for (int i = 0; i < this.actionModeViews.size(); i++) {
            View view2 = (View) this.actionModeViews.get(i);
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

    private void onItemClick(WallpaperCell view, Object object, int index) {
        TL_wallPaper wallPaper;
        if (!this.actionBar.isActionModeShowed()) {
            long id = getWallPaperId(object);
            if (object instanceof TL_wallPaper) {
                wallPaper = (TL_wallPaper) object;
                if (wallPaper.pattern) {
                    ColorWallpaper colorWallpaper = new ColorWallpaper(wallPaper.id, wallPaper.settings.background_color, wallPaper.id, ((float) wallPaper.settings.intensity) / 100.0f, wallPaper.settings.motion, null);
                }
            }
            WallpaperActivity wallpaperActivity = new WallpaperActivity(object, null);
            if (this.currentType == 1) {
                wallpaperActivity.setDelegate(new WallpapersListActivity$$Lambda$2(this));
            }
            if (this.selectedBackground == id) {
                wallpaperActivity.setInitialModes(this.selectedBackgroundBlurred, this.selectedBackgroundMotion);
            }
            wallpaperActivity.setPatterns(this.patterns);
            presentFragment(wallpaperActivity);
        } else if (object instanceof TL_wallPaper) {
            boolean z;
            wallPaper = (TL_wallPaper) object;
            if (this.selectedWallPapers.indexOfKey(wallPaper.id) >= 0) {
                this.selectedWallPapers.remove(wallPaper.id);
            } else {
                this.selectedWallPapers.put(wallPaper.id, wallPaper);
            }
            if (this.selectedWallPapers.size() == 0) {
                this.actionBar.hideActionMode();
            } else {
                this.selectedMessagesCountTextView.setNumber(this.selectedWallPapers.size(), true);
            }
            this.scrolling = false;
            if (this.selectedWallPapers.indexOfKey(wallPaper.id) >= 0) {
                z = true;
            } else {
                z = false;
            }
            view.setChecked(index, z, true);
        }
    }

    private long getWallPaperId(Object object) {
        if (object instanceof TL_wallPaper) {
            return ((TL_wallPaper) object).id;
        }
        if (object instanceof ColorWallpaper) {
            return ((ColorWallpaper) object).id;
        }
        if (object instanceof FileWallpaper) {
            return ((FileWallpaper) object).id;
        }
        return 0;
    }

    private void updateRowsSelection() {
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
        if (id == NotificationCenter.wallpapersDidLoad) {
            ArrayList<TL_wallPaper> arrayList = args[0];
            this.patterns.clear();
            if (this.currentType != 1) {
                this.wallPapers.clear();
                this.allWallPapers.clear();
                this.allWallPapersDict.clear();
                this.allWallPapers.addAll(arrayList);
            }
            int N = arrayList.size();
            for (int a = 0; a < N; a++) {
                TL_wallPaper wallPaper = (TL_wallPaper) arrayList.get(a);
                if (wallPaper.pattern) {
                    this.patterns.add(wallPaper);
                }
                if (!(this.currentType == 1 || (wallPaper.pattern && wallPaper.settings == null))) {
                    this.allWallPapersDict.put(wallPaper.id, wallPaper);
                    this.wallPapers.add(wallPaper);
                }
            }
            this.selectedBackground = Theme.getSelectedBackgroundId();
            fillWallpapersWithCustom();
            loadWallpapers();
        } else if (id == NotificationCenter.didSetNewWallpapper) {
            if (this.listView != null) {
                this.listView.invalidateViews();
            }
            if (this.actionBar != null) {
                this.actionBar.closeSearchField();
            }
        } else if (id == NotificationCenter.wallpapersNeedReload) {
            MessagesStorage.getInstance(this.currentAccount).getWallpapers();
        }
    }

    private void loadWallpapers() {
        long acc = 0;
        int N = this.allWallPapers.size();
        for (int a = 0; a < N; a++) {
            TL_wallPaper object = this.allWallPapers.get(a);
            if (object instanceof TL_wallPaper) {
                TL_wallPaper wallPaper = object;
                acc = (((20261 * ((((20261 * acc) + 2147483648L) + ((long) ((int) (wallPaper.id >> 32)))) % 2147483648L)) + 2147483648L) + ((long) ((int) wallPaper.id))) % 2147483648L;
            }
        }
        TL_account_getWallPapers req = new TL_account_getWallPapers();
        req.hash = (int) acc;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new WallpapersListActivity$$Lambda$3(this)), this.classGuid);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadWallpapers$5$WallpapersListActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new WallpapersListActivity$$Lambda$5(this, response));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$4$WallpapersListActivity(TLObject response) {
        if (response instanceof TL_account_wallPapers) {
            TL_account_wallPapers res = (TL_account_wallPapers) response;
            this.patterns.clear();
            if (this.currentType != 1) {
                this.wallPapers.clear();
                this.allWallPapersDict.clear();
                this.allWallPapers.clear();
                this.allWallPapers.addAll(res.wallpapers);
            }
            int N = res.wallpapers.size();
            for (int a = 0; a < N; a++) {
                TL_wallPaper wallPaper = (TL_wallPaper) res.wallpapers.get(a);
                this.allWallPapersDict.put(wallPaper.id, wallPaper);
                if (wallPaper.pattern) {
                    this.patterns.add(wallPaper);
                }
                if (!(this.currentType == 1 || (wallPaper.pattern && wallPaper.settings == null))) {
                    this.wallPapers.add(wallPaper);
                }
            }
            fillWallpapersWithCustom();
            MessagesStorage.getInstance(this.currentAccount).putWallpapers(res.wallpapers, 1);
        }
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
            this.listView.smoothScrollToPosition(0);
        }
    }

    private void fillWallpapersWithCustom() {
        if (this.currentType == 0) {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            if (this.addedColorWallpaper != null) {
                this.wallPapers.remove(this.addedColorWallpaper);
                this.addedColorWallpaper = null;
            }
            if (this.addedFileWallpaper != null) {
                this.wallPapers.remove(this.addedFileWallpaper);
                this.addedFileWallpaper = null;
            }
            if (this.catsWallpaper == null) {
                this.catsWallpaper = new FileWallpaper(1000001, NUM, NUM);
            } else {
                this.wallPapers.remove(this.catsWallpaper);
            }
            if (this.themeWallpaper != null) {
                this.wallPapers.remove(this.themeWallpaper);
            }
            Collections.sort(this.wallPapers, new WallpapersListActivity$$Lambda$4(this, Theme.getCurrentTheme().isDark()));
            if (Theme.hasWallpaperFromTheme()) {
                if (this.themeWallpaper == null) {
                    this.themeWallpaper = new FileWallpaper(-2, -2, -2);
                }
                this.wallPapers.add(0, this.themeWallpaper);
            } else {
                this.themeWallpaper = null;
            }
            if (this.selectedBackground == -1 || (this.selectedBackground != 1000001 && ((this.selectedBackground < -100 || this.selectedBackground > 0) && this.allWallPapersDict.indexOfKey(this.selectedBackground) < 0))) {
                if (this.selectedPattern != 0) {
                    this.addedColorWallpaper = new ColorWallpaper(this.selectedBackground, this.selectedColor, this.selectedPattern, this.selectedIntensity, this.selectedBackgroundMotion, new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg"));
                    this.wallPapers.add(0, this.addedColorWallpaper);
                } else if (this.selectedColor != 0) {
                    this.addedColorWallpaper = new ColorWallpaper(this.selectedBackground, this.selectedColor);
                    this.wallPapers.add(0, this.addedColorWallpaper);
                } else {
                    this.addedFileWallpaper = new FileWallpaper(this.selectedBackground, new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg"), new File(ApplicationLoader.getFilesDirFixed(), this.selectedBackgroundBlurred ? "wallpaper_original.jpg" : "wallpaper.jpg"));
                    this.wallPapers.add(0, this.addedFileWallpaper);
                }
            } else if (this.selectedColor != 0 && this.selectedBackground >= -100 && this.selectedPattern < -1) {
                this.addedColorWallpaper = new ColorWallpaper(this.selectedBackground, this.selectedColor);
                this.wallPapers.add(0, this.addedColorWallpaper);
            }
            if (this.selectedBackground == 1000001) {
                this.wallPapers.add(0, this.catsWallpaper);
            } else {
                this.wallPapers.add(this.catsWallpaper);
            }
            updateRows();
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ int lambda$fillWallpapersWithCustom$6$WallpapersListActivity(boolean currentThemeDark, Object o1, Object o2) {
        if (!(o1 instanceof TL_wallPaper) || !(o2 instanceof TL_wallPaper)) {
            return 0;
        }
        TL_wallPaper wallPaper1 = (TL_wallPaper) o1;
        TL_wallPaper wallPaper2 = (TL_wallPaper) o2;
        if (wallPaper1.id == this.selectedBackground) {
            return -1;
        }
        if (wallPaper2.id == this.selectedBackground) {
            return 1;
        }
        int index1 = this.allWallPapers.indexOf(wallPaper1);
        int index2 = this.allWallPapers.indexOf(wallPaper2);
        if (!(wallPaper1.dark && wallPaper2.dark) && (wallPaper1.dark || wallPaper2.dark)) {
            if (!wallPaper1.dark || wallPaper2.dark) {
                if (currentThemeDark) {
                    return 1;
                }
                return -1;
            } else if (currentThemeDark) {
                return -1;
            } else {
                return 1;
            }
        } else if (index1 > index2) {
            return 1;
        } else {
            if (index2 >= index1) {
                return 0;
            }
            return -1;
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
            this.wallPaperStartRow = this.rowCount;
            this.rowCount += this.totalWallpaperRows;
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
        if (this.listAdapter != null) {
            this.scrolling = true;
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void fixLayout() {
        if (this.listView != null) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
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
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[21];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, 0, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, 0, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGray");
        int i = 0;
        themeDescriptionArr[10] = new ThemeDescription(this.listView, i, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGray");
        i = 0;
        themeDescriptionArr[13] = new ThemeDescription(this.listView, i, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        i = 0;
        themeDescriptionArr[14] = new ThemeDescription(this.listView, i, new Class[]{TextCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        i = 0;
        themeDescriptionArr[15] = new ThemeDescription(this.listView, i, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        i = 0;
        themeDescriptionArr[16] = new ThemeDescription(this.listView, i, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, "key_graySectionText");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection");
        themeDescriptionArr[18] = new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        themeDescriptionArr[19] = new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        themeDescriptionArr[20] = new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        return themeDescriptionArr;
    }
}
