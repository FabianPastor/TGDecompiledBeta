package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.TL_channels_setStickers;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.StickerSetCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class GroupStickersActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private int chatId;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimation;
    private boolean donePressed;
    private EditText editText;
    private ImageView eraseImageView;
    private int headerRow;
    private boolean ignoreTextChanges;
    private ChatFull info;
    private int infoRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private LinearLayout nameContainer;
    private int nameRow;
    private ContextProgressView progressView;
    private Runnable queryRunnable;
    private int reqId;
    private int rowCount;
    private boolean searchWas;
    private boolean searching;
    private int selectedStickerRow;
    private TL_messages_stickerSet selectedStickerSet;
    private int stickersEndRow;
    private int stickersShadowRow;
    private int stickersStartRow;
    private EditTextBoldCursor usernameTextView;

    /* renamed from: org.telegram.ui.GroupStickersActivity$3 */
    class C14163 implements TextWatcher {
        boolean ignoreTextChange;

        C14163() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (GroupStickersActivity.this.eraseImageView != null) {
                GroupStickersActivity.this.eraseImageView.setVisibility(s.length() > 0 ? 0 : 4);
            }
            if (!this.ignoreTextChange) {
                if (!GroupStickersActivity.this.ignoreTextChanges) {
                    if (s.length() > 5) {
                        this.ignoreTextChange = true;
                        try {
                            Uri uri = Uri.parse(s.toString());
                            if (uri != null) {
                                List<String> segments = uri.getPathSegments();
                                if (segments.size() == 2 && ((String) segments.get(0)).toLowerCase().equals("addstickers")) {
                                    GroupStickersActivity.this.usernameTextView.setText((CharSequence) segments.get(1));
                                    GroupStickersActivity.this.usernameTextView.setSelection(GroupStickersActivity.this.usernameTextView.length());
                                }
                            }
                        } catch (Exception e) {
                        }
                        this.ignoreTextChange = false;
                    }
                    GroupStickersActivity.this.resolveStickerSet();
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupStickersActivity$4 */
    class C14174 implements OnClickListener {
        C14174() {
        }

        public void onClick(View v) {
            GroupStickersActivity.this.searchWas = false;
            GroupStickersActivity.this.selectedStickerSet = null;
            GroupStickersActivity.this.usernameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            GroupStickersActivity.this.updateRows();
        }
    }

    /* renamed from: org.telegram.ui.GroupStickersActivity$9 */
    class C14209 implements Runnable {
        C14209() {
        }

        public void run() {
            if (GroupStickersActivity.this.usernameTextView != null) {
                GroupStickersActivity.this.usernameTextView.requestFocus();
                AndroidUtilities.showKeyboard(GroupStickersActivity.this.usernameTextView);
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupStickersActivity$1 */
    class C21491 extends ActionBarMenuOnItemClick {
        C21491() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                GroupStickersActivity.this.finishFragment();
            } else if (id == 1 && !GroupStickersActivity.this.donePressed) {
                GroupStickersActivity.this.donePressed = true;
                if (GroupStickersActivity.this.searching) {
                    GroupStickersActivity.this.showEditDoneProgress(true);
                    return;
                }
                GroupStickersActivity.this.saveStickerSet();
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupStickersActivity$6 */
    class C21506 implements OnItemClickListener {
        C21506() {
        }

        public void onItemClick(View view, int position) {
            if (GroupStickersActivity.this.getParentActivity() != null) {
                if (position == GroupStickersActivity.this.selectedStickerRow) {
                    if (GroupStickersActivity.this.selectedStickerSet != null) {
                        GroupStickersActivity.this.showDialog(new StickersAlert(GroupStickersActivity.this.getParentActivity(), GroupStickersActivity.this, null, GroupStickersActivity.this.selectedStickerSet, null));
                    }
                } else if (position >= GroupStickersActivity.this.stickersStartRow && position < GroupStickersActivity.this.stickersEndRow) {
                    boolean needScroll = GroupStickersActivity.this.selectedStickerRow == -1;
                    int row = GroupStickersActivity.this.layoutManager.findFirstVisibleItemPosition();
                    int top = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    Holder holder = (Holder) GroupStickersActivity.this.listView.findViewHolderForAdapterPosition(row);
                    if (holder != null) {
                        top = holder.itemView.getTop();
                    }
                    GroupStickersActivity.this.selectedStickerSet = (TL_messages_stickerSet) DataQuery.getInstance(GroupStickersActivity.this.currentAccount).getStickerSets(0).get(position - GroupStickersActivity.this.stickersStartRow);
                    GroupStickersActivity.this.ignoreTextChanges = true;
                    GroupStickersActivity.this.usernameTextView.setText(GroupStickersActivity.this.selectedStickerSet.set.short_name);
                    GroupStickersActivity.this.usernameTextView.setSelection(GroupStickersActivity.this.usernameTextView.length());
                    GroupStickersActivity.this.ignoreTextChanges = false;
                    AndroidUtilities.hideKeyboard(GroupStickersActivity.this.usernameTextView);
                    GroupStickersActivity.this.updateRows();
                    if (needScroll && top != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                        GroupStickersActivity.this.layoutManager.scrollToPositionWithOffset(row + 1, top);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupStickersActivity$7 */
    class C21517 extends OnScrollListener {
        C21517() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 1) {
                AndroidUtilities.hideKeyboard(GroupStickersActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return GroupStickersActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = false;
            int row;
            switch (holder.getItemViewType()) {
                case 0:
                    long id;
                    ArrayList<TL_messages_stickerSet> arrayList = DataQuery.getInstance(GroupStickersActivity.this.currentAccount).getStickerSets(0);
                    row = position - GroupStickersActivity.this.stickersStartRow;
                    StickerSetCell cell = holder.itemView;
                    TL_messages_stickerSet set = (TL_messages_stickerSet) arrayList.get(row);
                    cell.setStickersSet((TL_messages_stickerSet) arrayList.get(row), row != arrayList.size() - 1);
                    if (GroupStickersActivity.this.selectedStickerSet != null) {
                        id = GroupStickersActivity.this.selectedStickerSet.set.id;
                    } else if (GroupStickersActivity.this.info == null || GroupStickersActivity.this.info.stickerset == null) {
                        id = 0;
                        if (set.set.id == id) {
                            z = true;
                        }
                        cell.setChecked(z);
                        return;
                    } else {
                        id = GroupStickersActivity.this.info.stickerset.id;
                    }
                    if (set.set.id == id) {
                        z = true;
                    }
                    cell.setChecked(z);
                    return;
                case 1:
                    if (position == GroupStickersActivity.this.infoRow) {
                        String text = LocaleController.getString("ChooseStickerSetMy", R.string.ChooseStickerSetMy);
                        String botName = "@stickers";
                        row = text.indexOf(botName);
                        if (row != -1) {
                            try {
                                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
                                stringBuilder.setSpan(new URLSpanNoUnderline("@stickers") {
                                    public void onClick(View widget) {
                                        MessagesController.getInstance(GroupStickersActivity.this.currentAccount).openByUserName("stickers", GroupStickersActivity.this, 1);
                                    }
                                }, row, botName.length() + row, 18);
                                ((TextInfoPrivacyCell) holder.itemView).setText(stringBuilder);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                                ((TextInfoPrivacyCell) holder.itemView).setText(text);
                            }
                        } else {
                            ((TextInfoPrivacyCell) holder.itemView).setText(text);
                        }
                        return;
                    }
                    return;
                case 4:
                    ((HeaderCell) holder.itemView).setText(LocaleController.getString("ChooseFromYourStickers", R.string.ChooseFromYourStickers));
                    return;
                case 5:
                    StickerSetCell cell2 = holder.itemView;
                    if (GroupStickersActivity.this.selectedStickerSet != null) {
                        cell2.setStickersSet(GroupStickersActivity.this.selectedStickerSet, false);
                        return;
                    } else if (GroupStickersActivity.this.searching) {
                        cell2.setText(LocaleController.getString("Loading", R.string.Loading), null, 0, false);
                        return;
                    } else {
                        cell2.setText(LocaleController.getString("ChooseStickerSetNotFound", R.string.ChooseStickerSetNotFound), LocaleController.getString("ChooseStickerSetNotFoundInfo", R.string.ChooseStickerSetNotFoundInfo), R.drawable.ic_smiles2_sad, false);
                        return;
                    }
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            int type = holder.getItemViewType();
            if (!(type == 0 || type == 2)) {
                if (type != 5) {
                    return false;
                }
            }
            return true;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                case 5:
                    view = new StickerSetCell(this.mContext, viewType == 0 ? 3 : 2);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 2:
                    view = GroupStickersActivity.this.nameContainer;
                    break;
                case 3:
                    view = new ShadowSectionCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 4:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int i) {
            if (i >= GroupStickersActivity.this.stickersStartRow && i < GroupStickersActivity.this.stickersEndRow) {
                return 0;
            }
            if (i == GroupStickersActivity.this.infoRow) {
                return 1;
            }
            if (i == GroupStickersActivity.this.nameRow) {
                return 2;
            }
            if (i == GroupStickersActivity.this.stickersShadowRow) {
                return 3;
            }
            if (i == GroupStickersActivity.this.headerRow) {
                return 4;
            }
            if (i == GroupStickersActivity.this.selectedStickerRow) {
                return 5;
            }
            return 0;
        }
    }

    public GroupStickersActivity(int id) {
        this.chatId = id;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        DataQuery.getInstance(this.currentAccount).checkStickers(0);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoaded);
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoaded);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("GroupStickers", R.string.GroupStickers));
        this.actionBar.setActionBarMenuOnItemClick(new C21491());
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.progressView = new ContextProgressView(context2, 1);
        this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        this.progressView.setVisibility(4);
        this.nameContainer = new LinearLayout(context2) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
            }

            protected void onDraw(Canvas canvas) {
                if (GroupStickersActivity.this.selectedStickerSet != null) {
                    canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), Theme.dividerPaint);
                }
            }
        };
        this.nameContainer.setWeightSum(1.0f);
        this.nameContainer.setWillNotDraw(false);
        this.nameContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.nameContainer.setOrientation(0);
        this.nameContainer.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(14.0f), 0);
        this.editText = new EditText(context2);
        EditText editText = this.editText;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
        stringBuilder.append("/addstickers/");
        editText.setText(stringBuilder.toString());
        this.editText.setTextSize(1, 17.0f);
        this.editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.editText.setMaxLines(1);
        this.editText.setLines(1);
        this.editText.setEnabled(false);
        this.editText.setFocusable(false);
        this.editText.setBackgroundDrawable(null);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setGravity(16);
        this.editText.setSingleLine(true);
        this.editText.setInputType(163840);
        this.editText.setImeOptions(6);
        this.nameContainer.addView(this.editText, LayoutHelper.createLinear(-2, 42));
        this.usernameTextView = new EditTextBoldCursor(context2);
        this.usernameTextView.setTextSize(1, 17.0f);
        this.usernameTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.usernameTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        this.usernameTextView.setCursorWidth(1.5f);
        this.usernameTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.usernameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.usernameTextView.setMaxLines(1);
        this.usernameTextView.setLines(1);
        this.usernameTextView.setBackgroundDrawable(null);
        this.usernameTextView.setPadding(0, 0, 0, 0);
        this.usernameTextView.setSingleLine(true);
        this.usernameTextView.setGravity(16);
        this.usernameTextView.setInputType(163872);
        this.usernameTextView.setImeOptions(6);
        this.usernameTextView.setHint(LocaleController.getString("ChooseStickerSetPlaceholder", R.string.ChooseStickerSetPlaceholder));
        this.usernameTextView.addTextChangedListener(new C14163());
        this.nameContainer.addView(this.usernameTextView, LayoutHelper.createLinear(0, 42, 1.0f));
        this.eraseImageView = new ImageView(context2);
        this.eraseImageView.setScaleType(ScaleType.CENTER);
        this.eraseImageView.setImageResource(R.drawable.ic_close_white);
        this.eraseImageView.setPadding(AndroidUtilities.dp(16.0f), 0, 0, 0);
        this.eraseImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3), Mode.MULTIPLY));
        this.eraseImageView.setVisibility(4);
        this.eraseImageView.setOnClickListener(new C14174());
        this.nameContainer.addView(this.eraseImageView, LayoutHelper.createLinear(42, 42, 0.0f));
        if (!(this.info == null || r0.info.stickerset == null)) {
            r0.ignoreTextChanges = true;
            r0.usernameTextView.setText(r0.info.stickerset.short_name);
            r0.usernameTextView.setSelection(r0.usernameTextView.length());
            r0.ignoreTextChanges = false;
        }
        r0.listAdapter = new ListAdapter(context2);
        r0.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = r0.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        r0.listView = new RecyclerListView(context2);
        r0.listView.setFocusable(true);
        r0.listView.setItemAnimator(null);
        r0.listView.setLayoutAnimation(null);
        r0.layoutManager = new LinearLayoutManager(context2) {
            public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
                return false;
            }

            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        r0.layoutManager.setOrientation(1);
        r0.listView.setLayoutManager(r0.layoutManager);
        frameLayout.addView(r0.listView, LayoutHelper.createFrame(-1, -1.0f));
        r0.listView.setAdapter(r0.listAdapter);
        r0.listView.setOnItemClickListener(new C21506());
        r0.listView.setOnScrollListener(new C21517());
        return r0.fragmentView;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.stickersDidLoaded) {
            if (((Integer) args[0]).intValue() == 0) {
                updateRows();
            }
        } else if (id == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = args[0];
            if (chatFull.id == this.chatId) {
                if (this.info == null && chatFull.stickerset != null) {
                    this.selectedStickerSet = DataQuery.getInstance(this.currentAccount).getGroupStickerSetById(chatFull.stickerset);
                }
                this.info = chatFull;
                updateRows();
            }
        } else if (id == NotificationCenter.groupStickersDidLoaded) {
            long setId = ((Long) args[0]).longValue();
            if (this.info != null && this.info.stickerset != null && this.info.stickerset.id == ((long) id)) {
                updateRows();
            }
        }
    }

    public void setInfo(ChatFull chatFull) {
        this.info = chatFull;
        if (this.info != null && this.info.stickerset != null) {
            this.selectedStickerSet = DataQuery.getInstance(this.currentAccount).getGroupStickerSetById(this.info.stickerset);
        }
    }

    private void resolveStickerSet() {
        if (this.listAdapter != null) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            if (this.queryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.queryRunnable);
                this.queryRunnable = null;
            }
            this.selectedStickerSet = null;
            if (this.usernameTextView.length() <= 0) {
                this.searching = false;
                this.searchWas = false;
                if (this.selectedStickerRow != -1) {
                    updateRows();
                }
                return;
            }
            this.searching = true;
            this.searchWas = true;
            final String query = this.usernameTextView.getText().toString();
            TL_messages_stickerSet existingSet = DataQuery.getInstance(this.currentAccount).getStickerSetByName(query);
            if (existingSet != null) {
                this.selectedStickerSet = existingSet;
            }
            if (this.selectedStickerRow == -1) {
                updateRows();
            } else {
                this.listAdapter.notifyItemChanged(this.selectedStickerRow);
            }
            if (existingSet != null) {
                this.searching = false;
                return;
            }
            Runnable c14198 = new Runnable() {

                /* renamed from: org.telegram.ui.GroupStickersActivity$8$1 */
                class C21521 implements RequestDelegate {
                    C21521() {
                    }

                    public void run(final TLObject response, TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                GroupStickersActivity.this.searching = false;
                                if (response instanceof TL_messages_stickerSet) {
                                    GroupStickersActivity.this.selectedStickerSet = (TL_messages_stickerSet) response;
                                    if (GroupStickersActivity.this.donePressed) {
                                        GroupStickersActivity.this.saveStickerSet();
                                    } else if (GroupStickersActivity.this.selectedStickerRow != -1) {
                                        GroupStickersActivity.this.listAdapter.notifyItemChanged(GroupStickersActivity.this.selectedStickerRow);
                                    } else {
                                        GroupStickersActivity.this.updateRows();
                                    }
                                } else {
                                    if (GroupStickersActivity.this.selectedStickerRow != -1) {
                                        GroupStickersActivity.this.listAdapter.notifyItemChanged(GroupStickersActivity.this.selectedStickerRow);
                                    }
                                    if (GroupStickersActivity.this.donePressed) {
                                        GroupStickersActivity.this.donePressed = false;
                                        GroupStickersActivity.this.showEditDoneProgress(false);
                                        if (GroupStickersActivity.this.getParentActivity() != null) {
                                            Toast.makeText(GroupStickersActivity.this.getParentActivity(), LocaleController.getString("AddStickersNotFound", R.string.AddStickersNotFound), 0).show();
                                        }
                                    }
                                }
                                GroupStickersActivity.this.reqId = 0;
                            }
                        });
                    }
                }

                public void run() {
                    if (GroupStickersActivity.this.queryRunnable != null) {
                        TL_messages_getStickerSet req = new TL_messages_getStickerSet();
                        req.stickerset = new TL_inputStickerSetShortName();
                        req.stickerset.short_name = query;
                        GroupStickersActivity.this.reqId = ConnectionsManager.getInstance(GroupStickersActivity.this.currentAccount).sendRequest(req, new C21521());
                    }
                }
            };
            this.queryRunnable = c14198;
            AndroidUtilities.runOnUIThread(c14198, 500);
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.runOnUIThread(new C14209(), 100);
        }
    }

    private void saveStickerSet() {
        if (this.info != null && (this.info.stickerset == null || this.selectedStickerSet == null || this.selectedStickerSet.set.id != this.info.stickerset.id)) {
            if (this.info.stickerset != null || this.selectedStickerSet != null) {
                showEditDoneProgress(true);
                TL_channels_setStickers req = new TL_channels_setStickers();
                req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
                if (this.selectedStickerSet == null) {
                    req.stickerset = new TL_inputStickerSetEmpty();
                } else {
                    Editor edit = MessagesController.getEmojiSettings(this.currentAccount).edit();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("group_hide_stickers_");
                    stringBuilder.append(this.info.id);
                    edit.remove(stringBuilder.toString()).commit();
                    req.stickerset = new TL_inputStickerSetID();
                    req.stickerset.id = this.selectedStickerSet.set.id;
                    req.stickerset.access_hash = this.selectedStickerSet.set.access_hash;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (error == null) {
                                    if (GroupStickersActivity.this.selectedStickerSet == null) {
                                        GroupStickersActivity.this.info.stickerset = null;
                                    } else {
                                        GroupStickersActivity.this.info.stickerset = GroupStickersActivity.this.selectedStickerSet.set;
                                        DataQuery.getInstance(GroupStickersActivity.this.currentAccount).putGroupStickerSet(GroupStickersActivity.this.selectedStickerSet);
                                    }
                                    if (GroupStickersActivity.this.info.stickerset == null) {
                                        ChatFull access$2100 = GroupStickersActivity.this.info;
                                        access$2100.flags |= 256;
                                    } else {
                                        GroupStickersActivity.this.info.flags &= -257;
                                    }
                                    MessagesStorage.getInstance(GroupStickersActivity.this.currentAccount).updateChatInfo(GroupStickersActivity.this.info, false);
                                    NotificationCenter.getInstance(GroupStickersActivity.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, GroupStickersActivity.this.info, Integer.valueOf(0), Boolean.valueOf(true), null);
                                    GroupStickersActivity.this.finishFragment();
                                    return;
                                }
                                Context parentActivity = GroupStickersActivity.this.getParentActivity();
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                                stringBuilder.append("\n");
                                stringBuilder.append(error.text);
                                Toast.makeText(parentActivity, stringBuilder.toString(), 0).show();
                                GroupStickersActivity.this.donePressed = false;
                                GroupStickersActivity.this.showEditDoneProgress(false);
                            }
                        });
                    }
                });
                return;
            }
        }
        finishFragment();
    }

    private void updateRows() {
        ArrayList<TL_messages_stickerSet> stickerSets;
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.nameRow = i;
        if (this.selectedStickerSet == null) {
            if (!this.searchWas) {
                this.selectedStickerRow = -1;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.infoRow = i;
                stickerSets = DataQuery.getInstance(this.currentAccount).getStickerSets(0);
                if (stickerSets.isEmpty()) {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.headerRow = i;
                    this.stickersStartRow = this.rowCount;
                    this.stickersEndRow = this.rowCount + stickerSets.size();
                    this.rowCount += stickerSets.size();
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.stickersShadowRow = i;
                } else {
                    this.headerRow = -1;
                    this.stickersStartRow = -1;
                    this.stickersEndRow = -1;
                    this.stickersShadowRow = -1;
                }
                if (this.nameContainer != null) {
                    this.nameContainer.invalidate();
                }
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.selectedStickerRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.infoRow = i;
        stickerSets = DataQuery.getInstance(this.currentAccount).getStickerSets(0);
        if (stickerSets.isEmpty()) {
            this.headerRow = -1;
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersShadowRow = -1;
        } else {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.headerRow = i;
            this.stickersStartRow = this.rowCount;
            this.stickersEndRow = this.rowCount + stickerSets.size();
            this.rowCount += stickerSets.size();
            i = this.rowCount;
            this.rowCount = i + 1;
            this.stickersShadowRow = i;
        }
        if (this.nameContainer != null) {
            this.nameContainer.invalidate();
        }
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.usernameTextView.requestFocus();
            AndroidUtilities.showKeyboard(this.usernameTextView);
        }
    }

    private void showEditDoneProgress(final boolean show) {
        if (this.doneItem != null) {
            if (this.doneItemAnimation != null) {
                this.doneItemAnimation.cancel();
            }
            this.doneItemAnimation = new AnimatorSet();
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            if (show) {
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                animatorSet = this.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            } else {
                this.doneItem.getImageView().setVisibility(0);
                this.doneItem.setEnabled(true);
                animatorSet = this.doneItemAnimation;
                animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[]{1.0f});
                animatorSet.playTogether(animatorArr);
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (GroupStickersActivity.this.doneItemAnimation != null && GroupStickersActivity.this.doneItemAnimation.equals(animation)) {
                        if (show) {
                            GroupStickersActivity.this.doneItem.getImageView().setVisibility(4);
                        } else {
                            GroupStickersActivity.this.progressView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (GroupStickersActivity.this.doneItemAnimation != null && GroupStickersActivity.this.doneItemAnimation.equals(animation)) {
                        GroupStickersActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[24];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{StickerSetCell.class, TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[9] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[10] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[11] = new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[12] = new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteLinkText);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[19] = new ThemeDescription(this.nameContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, null, null, null, Theme.key_stickers_menuSelector);
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, null, null, null, Theme.key_stickers_menu);
        return themeDescriptionArr;
    }
}
