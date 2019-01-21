package org.telegram.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_account_getWallPapers;
import org.telegram.tgnet.TLRPC.TL_account_wallPapers;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_getInlineBotResults;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_BotResults;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.WallpaperCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.WallpaperUpdater;
import org.telegram.ui.Components.WallpaperUpdater.WallpaperUpdaterDelegate;

public class WallpapersListActivity extends BaseFragment implements NotificationCenterDelegate {
    public static final int TYPE_ALL = 0;
    public static final int TYPE_COLOR = 1;
    private static final int[] defaultColors = new int[]{-1, -2826262, -4993567, -9783318, -16740912, -2891046, -3610935, -3808859, -10375058, -3289169, -5789547, -8622222, -10322, -18835, -2193583, -1059360, -2383431, -20561, -955808, -1524502, -6974739, -2507680, -5145015, -2765065, -2142101, -7613748, -12811138, -14524116, -14398084, -12764283, -10129027, -15195603, -16777216};
    public static boolean disableFeatures = true;
    private ColorWallpaper addedColorWallpaper;
    private FileWallpaper addedFileWallpaper;
    private boolean bingSearchEndReached = true;
    private FileWallpaper catsWallpaper;
    private int columnsCount = 3;
    private int currentType;
    private int imageReqId;
    private String lastSearchImageString;
    private String lastSearchString;
    private int lastSearchToken;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loadingWallpapers;
    private String nextImagesSearchOffset;
    private int rowCount;
    private ArrayList<SearchImage> searchResult = new ArrayList();
    private HashMap<String, SearchImage> searchResultKeys = new HashMap();
    private HashMap<String, SearchImage> searchResultUrls = new HashMap();
    private boolean searching;
    private boolean searchingUser;
    private int sectionRow;
    private long selectedBackground;
    private int selectedColor;
    private int setColorRow;
    private FileWallpaper themeWallpaper;
    private int totalWallpaperRows;
    private WallpaperUpdater updater;
    private int uploadImageRow;
    private int wallPaperStartRow;
    private ArrayList<Object> wallPapers = new ArrayList();

    public static class ColorWallpaper {
        public int color;
        public long id;

        public ColorWallpaper(long i, int c) {
            this.id = i;
            this.color = c;
        }
    }

    public static class FileWallpaper {
        public long id;
        public File path;
        public int resId;
        public int thumbResId;

        public FileWallpaper(long i, File f) {
            this.id = i;
            this.path = f;
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
            switch (viewType) {
                case 0:
                    view = new TextCell(this.mContext);
                    break;
                case 1:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                default:
                    view = new WallpaperCell(this.mContext) {
                        protected void onWallpaperClick(Object wallPaper) {
                            WallpaperActivity wallpaperActivity = new WallpaperActivity(wallPaper, null);
                            if (WallpapersListActivity.this.currentType == 1) {
                                wallpaperActivity.setDelegate(new WallpapersListActivity$ListAdapter$1$$Lambda$0(WallpapersListActivity.this));
                            }
                            WallpapersListActivity.this.presentFragment(wallpaperActivity);
                        }

                        protected void onWallpaperLongClick(Object wallPaper) {
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
                        if (WallpapersListActivity.disableFeatures) {
                            textCell.setTextAndIcon(LocaleController.getString("SelectImage", R.string.SelectImage), R.drawable.profile_photos, true);
                            return;
                        } else {
                            textCell.setTextAndIcon(LocaleController.getString("UploadImage", R.string.UploadImage), R.drawable.profile_photos, true);
                            return;
                        }
                    } else if (position == WallpapersListActivity.this.setColorRow) {
                        textCell.setTextAndIcon(LocaleController.getString("SetColor", R.string.SetColor), R.drawable.menu_palette, false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    View sectionCell = holder.itemView;
                    sectionCell.setTag(Integer.valueOf(position));
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, WallpapersListActivity.this.wallPaperStartRow == -1 ? R.drawable.greydivider_bottom : R.drawable.greydivider, "windowBackgroundGrayShadow"));
                    combinedDrawable.setFullsize(true);
                    sectionCell.setBackgroundDrawable(combinedDrawable);
                    return;
                default:
                    WallpaperCell wallpaperCell = holder.itemView;
                    position = (position - WallpapersListActivity.this.wallPaperStartRow) * WallpapersListActivity.this.columnsCount;
                    wallpaperCell.setParams(WallpapersListActivity.this.columnsCount, position == 0, position / WallpapersListActivity.this.columnsCount == WallpapersListActivity.this.totalWallpaperRows + -1);
                    for (int a = 0; a < WallpapersListActivity.this.columnsCount; a++) {
                        int p = position + a;
                        wallpaperCell.setWallpaper(WallpapersListActivity.this.currentType, a, p < WallpapersListActivity.this.wallPapers.size() ? WallpapersListActivity.this.wallPapers.get(p) : null, WallpapersListActivity.this.selectedBackground, null, false);
                    }
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == WallpapersListActivity.this.uploadImageRow || position == WallpapersListActivity.this.setColorRow) {
                return 0;
            }
            if (position == WallpapersListActivity.this.sectionRow) {
                return 1;
            }
            return 2;
        }
    }

    public WallpapersListActivity(int type) {
        this.currentType = type;
    }

    public boolean onFragmentCreate() {
        if (this.currentType == 0) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
            MessagesStorage.getInstance(this.currentAccount).getWallpapers();
        } else {
            for (int a = 0; a < defaultColors.length; a++) {
                this.wallPapers.add(new ColorWallpaper((long) (-(a + 3)), defaultColors[a]));
            }
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        if (this.currentType == 0) {
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        }
        this.updater.cleanup();
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        this.updater = new WallpaperUpdater(getParentActivity(), this, new WallpaperUpdaterDelegate() {
            public void didSelectWallpaper(File file, Bitmap bitmap, boolean gallery) {
                WallpapersListActivity.this.presentFragment(new WallpaperActivity(new FileWallpaper(-1, file), bitmap), gallery);
            }

            public void needOpenColorPicker() {
            }
        });
        this.hasOwnBackground = true;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChatBackground", R.string.ChatBackground));
        } else if (this.currentType == 1) {
            this.actionBar.setTitle(LocaleController.getString("SelectColorTitle", R.string.SelectColorTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    WallpapersListActivity.this.lambda$checkDiscard$70$PassportActivity();
                }
            }
        });
        if (this.currentType == 0) {
            ActionBarMenuItem searchItem = this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
                public void onSearchExpand() {
                }

                public void onTextChanged(EditText editText) {
                    if (editText.getText().length() == 0) {
                        WallpapersListActivity.this.searchResult.clear();
                        WallpapersListActivity.this.searchResultKeys.clear();
                        WallpapersListActivity.this.lastSearchString = null;
                        WallpapersListActivity.this.bingSearchEndReached = true;
                        WallpapersListActivity.this.searching = false;
                        if (WallpapersListActivity.this.imageReqId != 0) {
                            ConnectionsManager.getInstance(WallpapersListActivity.this.currentAccount).cancelRequest(WallpapersListActivity.this.imageReqId, true);
                            WallpapersListActivity.this.imageReqId = 0;
                        }
                        WallpapersListActivity.this.updateSearchInterface();
                    }
                }

                public void onSearchPressed(EditText editText) {
                    WallpapersListActivity.this.processSearch(editText);
                }
            });
            searchItem.setSearchFieldHint(LocaleController.getString("SearchImagesTitle", R.string.SearchImagesTitle));
            searchItem.setVisibility(8);
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
                if (WallpapersListActivity.this.wallPaperStartRow == -1) {
                    holder = findViewHolderForAdapterPosition(WallpapersListActivity.this.sectionRow);
                } else {
                    holder = null;
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
        LayoutManager anonymousClass5 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = anonymousClass5;
        recyclerListView.setLayoutManager(anonymousClass5);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setGlowColor(Theme.getColor("avatar_backgroundActionBarBlue"));
        this.listView.setOnItemClickListener(new WallpapersListActivity$$Lambda$0(this));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(WallpapersListActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = WallpapersListActivity.this.layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(WallpapersListActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                if (visibleItemCount > 0) {
                    int totalItemCount = WallpapersListActivity.this.layoutManager.getItemCount();
                    if (visibleItemCount != 0 && firstVisibleItem + visibleItemCount > totalItemCount - 2 && !WallpapersListActivity.this.searching && !WallpapersListActivity.this.bingSearchEndReached) {
                        WallpapersListActivity.this.searchImages(WallpapersListActivity.this.lastSearchString, WallpapersListActivity.this.nextImagesSearchOffset, true);
                    }
                }
            }
        });
        updateRows();
        updateSearchInterface();
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$0$WallpapersListActivity(View view, int position) {
        if (position == this.uploadImageRow) {
            this.updater.showAlert(false);
        } else if (position == this.setColorRow) {
            presentFragment(new WallpapersListActivity(1));
        }
    }

    public void onResume() {
        super.onResume();
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        this.selectedBackground = Theme.getSelectedBackgroundId();
        this.selectedColor = preferences.getInt("selectedColor", 0);
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.wallpapersDidLoad) {
            ArrayList<TL_wallPaper> arrayList = args[0];
            this.wallPapers.clear();
            this.wallPapers.addAll(arrayList);
            fillWallpapersWithCustom();
            loadWallpapers();
        } else if (id == NotificationCenter.didSetNewWallpapper && this.listView != null) {
            this.listView.invalidateViews();
        }
    }

    private void loadWallpapers() {
        long acc = 0;
        int N = this.wallPapers.size();
        for (int a = 0; a < N; a++) {
            TL_wallPaper object = this.wallPapers.get(a);
            if (object instanceof TL_wallPaper) {
                TL_wallPaper wallPaper = object;
                acc = (((20261 * ((((20261 * acc) + 2147483648L) + ((long) ((int) (wallPaper.id >> 32)))) % 2147483648L)) + 2147483648L) + ((long) ((int) wallPaper.id))) % 2147483648L;
            }
        }
        TL_account_getWallPapers req = new TL_account_getWallPapers();
        req.hash = (int) acc;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new WallpapersListActivity$$Lambda$1(this)), this.classGuid);
    }

    final /* synthetic */ void lambda$loadWallpapers$2$WallpapersListActivity(TLObject response, TL_error error) {
        if (response instanceof TL_account_wallPapers) {
            AndroidUtilities.runOnUIThread(new WallpapersListActivity$$Lambda$6(this, response));
        }
    }

    final /* synthetic */ void lambda$null$1$WallpapersListActivity(TLObject response) {
        TL_account_wallPapers res = (TL_account_wallPapers) response;
        this.wallPapers.clear();
        this.wallPapers.addAll(res.wallpapers);
        fillWallpapersWithCustom();
        MessagesStorage.getInstance(this.currentAccount).putWallpapers(res.wallpapers, true);
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
                this.catsWallpaper = new FileWallpaper(1000001, R.drawable.background_hd, R.drawable.catstile);
            } else {
                this.wallPapers.remove(this.catsWallpaper);
            }
            this.wallPapers.add(0, this.catsWallpaper);
            if (this.themeWallpaper != null) {
                this.wallPapers.remove(this.themeWallpaper);
            }
            if (Theme.hasWallpaperFromTheme()) {
                if (this.themeWallpaper == null) {
                    this.themeWallpaper = new FileWallpaper(-2, -2, -2);
                }
                this.wallPapers.add(0, this.themeWallpaper);
            } else {
                this.themeWallpaper = null;
            }
            if (this.selectedColor != 0) {
                this.addedColorWallpaper = new ColorWallpaper(this.selectedBackground, this.selectedColor);
                this.wallPapers.add(0, this.addedColorWallpaper);
            } else if (this.selectedBackground == -1) {
                this.addedFileWallpaper = new FileWallpaper(this.selectedBackground, new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg"));
                this.wallPapers.add(0, this.addedFileWallpaper);
            }
            updateRows();
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        if (this.currentType == 0) {
            int i = this.rowCount;
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
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void processSearch(EditText editText) {
        if (editText.getText().toString().length() != 0) {
            this.searchResult.clear();
            this.searchResultKeys.clear();
            this.bingSearchEndReached = true;
            searchImages(editText.getText().toString(), "", true);
            this.lastSearchString = editText.getText().toString();
            if (this.lastSearchString.length() == 0) {
                this.lastSearchString = null;
            }
            updateSearchInterface();
        }
    }

    private void updateSearchInterface() {
    }

    private void searchBotUser() {
        if (!this.searchingUser) {
            this.searchingUser = true;
            TL_contacts_resolveUsername req = new TL_contacts_resolveUsername();
            req.username = MessagesController.getInstance(this.currentAccount).imageSearchBot;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new WallpapersListActivity$$Lambda$2(this));
        }
    }

    final /* synthetic */ void lambda$searchBotUser$4$WallpapersListActivity(TLObject response, TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new WallpapersListActivity$$Lambda$5(this, response));
        }
    }

    final /* synthetic */ void lambda$null$3$WallpapersListActivity(TLObject response) {
        TL_contacts_resolvedPeer res = (TL_contacts_resolvedPeer) response;
        MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
        String str = this.lastSearchImageString;
        this.lastSearchImageString = null;
        searchImages(str, "", false);
    }

    private void searchImages(String query, String offset, boolean searchUser) {
        if (this.searching) {
            this.searching = false;
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
        }
        this.lastSearchImageString = query;
        this.searching = true;
        TLObject object = MessagesController.getInstance(this.currentAccount).getUserOrChat(MessagesController.getInstance(this.currentAccount).imageSearchBot);
        if (object instanceof User) {
            User user = (User) object;
            TL_messages_getInlineBotResults req = new TL_messages_getInlineBotResults();
            if (query == null) {
                query = "";
            }
            req.query = query;
            req.bot = MessagesController.getInstance(this.currentAccount).getInputUser(user);
            req.offset = offset;
            req.peer = new TL_inputPeerEmpty();
            int token = this.lastSearchToken + 1;
            this.lastSearchToken = token;
            this.imageReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new WallpapersListActivity$$Lambda$3(this, token));
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(this.imageReqId, this.classGuid);
        } else if (searchUser) {
            searchBotUser();
        }
    }

    final /* synthetic */ void lambda$searchImages$6$WallpapersListActivity(int token, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new WallpapersListActivity$$Lambda$4(this, token, response));
    }

    final /* synthetic */ void lambda$null$5$WallpapersListActivity(int token, TLObject response) {
        if (token == this.lastSearchToken) {
            int addedCount = 0;
            if (response != null) {
                messages_BotResults res = (messages_BotResults) response;
                this.nextImagesSearchOffset = res.next_offset;
                boolean added = false;
                int count = res.results.size();
                for (int a = 0; a < count; a++) {
                    BotInlineResult result = (BotInlineResult) res.results.get(a);
                    if ("photo".equals(result.type) && !this.searchResultKeys.containsKey(result.id)) {
                        added = true;
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
                        addedCount++;
                        added = true;
                    }
                }
                boolean z = !added || this.nextImagesSearchOffset == null;
                this.bingSearchEndReached = z;
            }
            this.searching = false;
            if (addedCount != 0) {
                this.listAdapter.notifyItemRangeInserted(this.searchResult.size(), addedCount);
            } else if (this.bingSearchEndReached) {
                this.listAdapter.notifyItemRemoved(this.searchResult.size() - 1);
            }
            if (!(this.searching && this.searchResult.isEmpty()) && this.loadingWallpapers && this.lastSearchString == null) {
            }
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
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[7];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[6] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        return themeDescriptionArr;
    }
}
