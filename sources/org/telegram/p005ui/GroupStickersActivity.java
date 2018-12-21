package org.telegram.p005ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
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
import org.telegram.p005ui.ActionBar.ActionBarMenuItem;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Cells.HeaderCell;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.Cells.StickerSetCell;
import org.telegram.p005ui.Cells.TextInfoPrivacyCell;
import org.telegram.p005ui.Cells.TextSettingsCell;
import org.telegram.p005ui.Components.ContextProgressView;
import org.telegram.p005ui.Components.EditTextBoldCursor;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.p005ui.Components.StickersAlert;
import org.telegram.p005ui.Components.URLSpanNoUnderline;
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

/* renamed from: org.telegram.ui.GroupStickersActivity */
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

    /* renamed from: org.telegram.ui.GroupStickersActivity$10 */
    class CLASSNAME implements RequestDelegate {
        CLASSNAME() {
        }

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
                        NotificationCenter.getInstance(GroupStickersActivity.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, GroupStickersActivity.this.info, Integer.valueOf(0), Boolean.valueOf(true), null);
                        GroupStickersActivity.this.lambda$checkDiscard$2$PollCreateActivity();
                        return;
                    }
                    Toast.makeText(GroupStickersActivity.this.getParentActivity(), LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text, 0).show();
                    GroupStickersActivity.this.donePressed = false;
                    GroupStickersActivity.this.showEditDoneProgress(false);
                }
            });
        }
    }

    /* renamed from: org.telegram.ui.GroupStickersActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                GroupStickersActivity.this.lambda$checkDiscard$2$PollCreateActivity();
            } else if (id == 1 && !GroupStickersActivity.this.donePressed) {
                GroupStickersActivity.this.donePressed = true;
                if (GroupStickersActivity.this.searching) {
                    GroupStickersActivity.this.showEditDoneProgress(true);
                } else {
                    GroupStickersActivity.this.saveStickerSet();
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupStickersActivity$3 */
    class CLASSNAME implements TextWatcher {
        boolean ignoreTextChange;

        CLASSNAME() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (GroupStickersActivity.this.eraseImageView != null) {
                GroupStickersActivity.this.eraseImageView.setVisibility(s.length() > 0 ? 0 : 4);
            }
            if (!this.ignoreTextChange && !GroupStickersActivity.this.ignoreTextChanges) {
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

    /* renamed from: org.telegram.ui.GroupStickersActivity$4 */
    class CLASSNAME implements OnClickListener {
        CLASSNAME() {
        }

        public void onClick(View v) {
            GroupStickersActivity.this.searchWas = false;
            GroupStickersActivity.this.selectedStickerSet = null;
            GroupStickersActivity.this.usernameTextView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            GroupStickersActivity.this.updateRows();
        }
    }

    /* renamed from: org.telegram.ui.GroupStickersActivity$6 */
    class CLASSNAME implements OnItemClickListener {
        CLASSNAME() {
        }

        public void onItemClick(View view, int position) {
            if (GroupStickersActivity.this.getParentActivity() != null) {
                if (position == GroupStickersActivity.this.selectedStickerRow) {
                    if (GroupStickersActivity.this.selectedStickerSet != null) {
                        GroupStickersActivity.this.showDialog(new StickersAlert(GroupStickersActivity.this.getParentActivity(), GroupStickersActivity.this, null, GroupStickersActivity.this.selectedStickerSet, null));
                    }
                } else if (position >= GroupStickersActivity.this.stickersStartRow && position < GroupStickersActivity.this.stickersEndRow) {
                    boolean needScroll;
                    if (GroupStickersActivity.this.selectedStickerRow == -1) {
                        needScroll = true;
                    } else {
                        needScroll = false;
                    }
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
    class CLASSNAME extends OnScrollListener {
        CLASSNAME() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 1) {
                AndroidUtilities.hideKeyboard(GroupStickersActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        }
    }

    /* renamed from: org.telegram.ui.GroupStickersActivity$9 */
    class CLASSNAME implements Runnable {
        CLASSNAME() {
        }

        public void run() {
            if (GroupStickersActivity.this.usernameTextView != null) {
                GroupStickersActivity.this.usernameTextView.requestFocus();
                AndroidUtilities.showKeyboard(GroupStickersActivity.this.usernameTextView);
            }
        }
    }

    /* renamed from: org.telegram.ui.GroupStickersActivity$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return GroupStickersActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            StickerSetCell cell;
            switch (holder.getItemViewType()) {
                case 0:
                    long id;
                    boolean z;
                    ArrayList<TL_messages_stickerSet> arrayList = DataQuery.getInstance(GroupStickersActivity.this.currentAccount).getStickerSets(0);
                    int row = position - GroupStickersActivity.this.stickersStartRow;
                    cell = holder.itemView;
                    TL_messages_stickerSet set = (TL_messages_stickerSet) arrayList.get(row);
                    cell.setStickersSet((TL_messages_stickerSet) arrayList.get(row), row != arrayList.size() + -1);
                    if (GroupStickersActivity.this.selectedStickerSet != null) {
                        id = GroupStickersActivity.this.selectedStickerSet.set.var_id;
                    } else if (GroupStickersActivity.this.info == null || GroupStickersActivity.this.info.stickerset == null) {
                        id = 0;
                    } else {
                        id = GroupStickersActivity.this.info.stickerset.var_id;
                    }
                    if (set.set.var_id == id) {
                        z = true;
                    } else {
                        z = false;
                    }
                    cell.setChecked(z);
                    return;
                case 1:
                    if (position == GroupStickersActivity.this.infoRow) {
                        String text = LocaleController.getString("ChooseStickerSetMy", R.string.ChooseStickerSetMy);
                        String botName = "@stickers";
                        int index = text.indexOf(botName);
                        if (index != -1) {
                            try {
                                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
                                stringBuilder.setSpan(new URLSpanNoUnderline("@stickers") {
                                    public void onClick(View widget) {
                                        MessagesController.getInstance(GroupStickersActivity.this.currentAccount).openByUserName("stickers", GroupStickersActivity.this, 1);
                                    }
                                }, index, botName.length() + index, 18);
                                ((TextInfoPrivacyCell) holder.itemView).setText(stringBuilder);
                                return;
                            } catch (Throwable e) {
                                FileLog.m13e(e);
                                ((TextInfoPrivacyCell) holder.itemView).setText(text);
                                return;
                            }
                        }
                        ((TextInfoPrivacyCell) holder.itemView).setText(text);
                        return;
                    }
                    return;
                case 4:
                    ((HeaderCell) holder.itemView).setText(LocaleController.getString("ChooseFromYourStickers", R.string.ChooseFromYourStickers));
                    return;
                case 5:
                    cell = (StickerSetCell) holder.itemView;
                    if (GroupStickersActivity.this.selectedStickerSet != null) {
                        cell.setStickersSet(GroupStickersActivity.this.selectedStickerSet, false);
                        return;
                    } else if (GroupStickersActivity.this.searching) {
                        cell.setText(LocaleController.getString("Loading", R.string.Loading), null, 0, false);
                        return;
                    } else {
                        cell.setText(LocaleController.getString("ChooseStickerSetNotFound", R.string.ChooseStickerSetNotFound), LocaleController.getString("ChooseStickerSetNotFoundInfo", R.string.ChooseStickerSetNotFoundInfo), R.drawable.ic_smiles2_sad, false);
                        return;
                    }
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 2 || type == 5;
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
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 2:
                    view = GroupStickersActivity.this.nameContainer;
                    break;
                case 3:
                    view = new ShadowSectionCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 4:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
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
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("GroupStickers", R.string.GroupStickers));
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.m9dp(56.0f));
        this.progressView = new ContextProgressView(context, 1);
        this.progressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.1f);
        this.progressView.setScaleY(0.1f);
        this.progressView.setVisibility(4);
        this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        this.nameContainer = new LinearLayout(context) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(42.0f), NUM));
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
        this.nameContainer.setPadding(AndroidUtilities.m9dp(17.0f), 0, AndroidUtilities.m9dp(14.0f), 0);
        this.editText = new EditText(context);
        this.editText.setText(MessagesController.getInstance(this.currentAccount).linkPrefix + "/addstickers/");
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
        this.usernameTextView = new EditTextBoldCursor(context);
        this.usernameTextView.setTextSize(1, 17.0f);
        this.usernameTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.usernameTextView.setCursorSize(AndroidUtilities.m9dp(20.0f));
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
        this.usernameTextView.addTextChangedListener(new CLASSNAME());
        this.nameContainer.addView(this.usernameTextView, LayoutHelper.createLinear(0, 42, 1.0f));
        this.eraseImageView = new ImageView(context);
        this.eraseImageView.setScaleType(ScaleType.CENTER);
        this.eraseImageView.setImageResource(R.drawable.ic_close_white);
        this.eraseImageView.setPadding(AndroidUtilities.m9dp(16.0f), 0, 0, 0);
        this.eraseImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3), Mode.MULTIPLY));
        this.eraseImageView.setVisibility(4);
        this.eraseImageView.setOnClickListener(new CLASSNAME());
        this.nameContainer.addView(this.eraseImageView, LayoutHelper.createLinear(42, 42, 0.0f));
        if (!(this.info == null || this.info.stickerset == null)) {
            this.ignoreTextChanges = true;
            this.usernameTextView.setText(this.info.stickerset.short_name);
            this.usernameTextView.setSelection(this.usernameTextView.length());
            this.ignoreTextChanges = false;
        }
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView = new RecyclerListView(context);
        this.listView.setFocusable(true);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.layoutManager = new LinearLayoutManager(context) {
            public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
                return false;
            }

            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new CLASSNAME());
        this.listView.setOnScrollListener(new CLASSNAME());
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.stickersDidLoad) {
            if (((Integer) args[0]).intValue() == 0) {
                updateRows();
            }
        } else if (id == NotificationCenter.chatInfoDidLoad) {
            ChatFull chatFull = args[0];
            if (chatFull.var_id == this.chatId) {
                if (this.info == null && chatFull.stickerset != null) {
                    this.selectedStickerSet = DataQuery.getInstance(this.currentAccount).getGroupStickerSetById(chatFull.stickerset);
                }
                this.info = chatFull;
                updateRows();
            }
        } else if (id == NotificationCenter.groupStickersDidLoad) {
            long setId = ((Long) args[0]).longValue();
            if (this.info != null && this.info.stickerset != null && this.info.stickerset.var_id == ((long) id)) {
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
                    return;
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
            Runnable CLASSNAME = new Runnable() {

                /* renamed from: org.telegram.ui.GroupStickersActivity$8$1 */
                class CLASSNAME implements RequestDelegate {
                    CLASSNAME() {
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
                        GroupStickersActivity.this.reqId = ConnectionsManager.getInstance(GroupStickersActivity.this.currentAccount).sendRequest(req, new CLASSNAME());
                    }
                }
            };
            this.queryRunnable = CLASSNAME;
            AndroidUtilities.runOnUIThread(CLASSNAME, 500);
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.runOnUIThread(new CLASSNAME(), 100);
        }
    }

    private void saveStickerSet() {
        if (this.info == null || (!(this.info.stickerset == null || this.selectedStickerSet == null || this.selectedStickerSet.set.var_id != this.info.stickerset.var_id) || (this.info.stickerset == null && this.selectedStickerSet == null))) {
            lambda$checkDiscard$2$PollCreateActivity();
            return;
        }
        showEditDoneProgress(true);
        TL_channels_setStickers req = new TL_channels_setStickers();
        req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
        if (this.selectedStickerSet == null) {
            req.stickerset = new TL_inputStickerSetEmpty();
        } else {
            MessagesController.getEmojiSettings(this.currentAccount).edit().remove("group_hide_stickers_" + this.info.var_id).commit();
            req.stickerset = new TL_inputStickerSetID();
            req.stickerset.var_id = this.selectedStickerSet.set.var_id;
            req.stickerset.access_hash = this.selectedStickerSet.set.access_hash;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new CLASSNAME());
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.nameRow = i;
        if (this.selectedStickerSet != null || this.searchWas) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.selectedStickerRow = i;
        } else {
            this.selectedStickerRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.infoRow = i;
        ArrayList<TL_messages_stickerSet> stickerSets = DataQuery.getInstance(this.currentAccount).getStickerSets(0);
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
