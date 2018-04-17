package org.telegram.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class ChannelPermissionsActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private int addUsersRow;
    private TL_channelAdminRights adminRights = new TL_channelAdminRights();
    private int changeInfoRow;
    private int chatId;
    private int embedLinksRow;
    private int forwardRow;
    private int forwardShadowRow;
    private HeaderCell headerCell2;
    private boolean historyHidden;
    private ChatFull info;
    private LinearLayout linearLayout;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private int permissionsHeaderRow;
    private RadioButtonCell radioButtonCell3;
    private RadioButtonCell radioButtonCell4;
    private int rightsShadowRow;
    private int rowCount = 0;
    private int sendMediaRow;
    private int sendStickersRow;

    /* renamed from: org.telegram.ui.ChannelPermissionsActivity$4 */
    class C09944 implements OnClickListener {
        C09944() {
        }

        public void onClick(View v) {
            ChannelPermissionsActivity.this.radioButtonCell3.setChecked(true, true);
            ChannelPermissionsActivity.this.radioButtonCell4.setChecked(false, true);
            ChannelPermissionsActivity.this.historyHidden = false;
        }
    }

    /* renamed from: org.telegram.ui.ChannelPermissionsActivity$5 */
    class C09955 implements OnClickListener {
        C09955() {
        }

        public void onClick(View v) {
            ChannelPermissionsActivity.this.radioButtonCell3.setChecked(false, true);
            ChannelPermissionsActivity.this.radioButtonCell4.setChecked(true, true);
            ChannelPermissionsActivity.this.historyHidden = true;
        }
    }

    /* renamed from: org.telegram.ui.ChannelPermissionsActivity$1 */
    class C19851 extends ActionBarMenuOnItemClick {
        C19851() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChannelPermissionsActivity.this.finishFragment();
            } else if (id == 1) {
                if (!(ChannelPermissionsActivity.this.headerCell2 == null || ChannelPermissionsActivity.this.headerCell2.getVisibility() != 0 || ChannelPermissionsActivity.this.info == null || ChannelPermissionsActivity.this.info.hidden_prehistory == ChannelPermissionsActivity.this.historyHidden)) {
                    ChannelPermissionsActivity.this.info.hidden_prehistory = ChannelPermissionsActivity.this.historyHidden;
                    MessagesController.getInstance(ChannelPermissionsActivity.this.currentAccount).toogleChannelInvitesHistory(ChannelPermissionsActivity.this.chatId, ChannelPermissionsActivity.this.historyHidden);
                }
                ChannelPermissionsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelPermissionsActivity$3 */
    class C19863 implements OnItemClickListener {
        C19863() {
        }

        public void onItemClick(View view, int position) {
            if (view instanceof TextCheckCell2) {
                TextCheckCell2 checkCell = (TextCheckCell2) view;
                if (checkCell.isEnabled()) {
                    checkCell.setChecked(checkCell.isChecked() ^ 1);
                    if (position == ChannelPermissionsActivity.this.changeInfoRow) {
                        ChannelPermissionsActivity.this.adminRights.change_info ^= 1;
                    } else if (position == ChannelPermissionsActivity.this.addUsersRow) {
                        ChannelPermissionsActivity.this.adminRights.invite_users ^= 1;
                    } else if (position == ChannelPermissionsActivity.this.sendMediaRow) {
                        ChannelPermissionsActivity.this.adminRights.ban_users ^= 1;
                    } else if (position == ChannelPermissionsActivity.this.sendStickersRow) {
                        ChannelPermissionsActivity.this.adminRights.add_admins ^= 1;
                    } else if (position == ChannelPermissionsActivity.this.embedLinksRow) {
                        ChannelPermissionsActivity.this.adminRights.pin_messages ^= 1;
                    }
                }
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getItemViewType() == 1;
        }

        public int getItemCount() {
            return ChannelPermissionsActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextCheckCell2(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                default:
                    view = ChannelPermissionsActivity.this.linearLayout;
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder.getItemViewType() == 2) {
                ShadowSectionCell shadowCell = holder.itemView;
                int access$1500 = ChannelPermissionsActivity.this.rightsShadowRow;
                int i = R.drawable.greydivider_bottom;
                if (position == access$1500) {
                    Context context = this.mContext;
                    if (ChannelPermissionsActivity.this.forwardShadowRow != -1) {
                        i = R.drawable.greydivider;
                    }
                    shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(context, i, Theme.key_windowBackgroundGrayShadow));
                    return;
                }
                shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            }
        }

        public int getItemViewType(int position) {
            if (position != ChannelPermissionsActivity.this.rightsShadowRow) {
                if (position != ChannelPermissionsActivity.this.forwardShadowRow) {
                    if (!(position == ChannelPermissionsActivity.this.changeInfoRow || position == ChannelPermissionsActivity.this.addUsersRow || position == ChannelPermissionsActivity.this.sendMediaRow || position == ChannelPermissionsActivity.this.sendStickersRow)) {
                        if (position != ChannelPermissionsActivity.this.embedLinksRow) {
                            if (position == ChannelPermissionsActivity.this.forwardRow) {
                                return 3;
                            }
                            return position == ChannelPermissionsActivity.this.permissionsHeaderRow ? 0 : 0;
                        }
                    }
                    return 1;
                }
            }
            return 2;
        }
    }

    public ChannelPermissionsActivity(int channelId) {
        this.chatId = channelId;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.permissionsHeaderRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.sendMediaRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.sendStickersRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.embedLinksRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.addUsersRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.changeInfoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.rightsShadowRow = i;
        Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        if (chat == null || !TextUtils.isEmpty(chat.username)) {
            this.forwardRow = -1;
            this.forwardShadowRow = -1;
            return;
        }
        int i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.forwardRow = i2;
        i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.forwardShadowRow = i2;
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoaded);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C19851());
        this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new C19863());
        this.linearLayout = new LinearLayout(context);
        this.linearLayout.setOrientation(1);
        this.linearLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout.setLayoutParams(new LayoutParams(-1, -2));
        this.headerCell2 = new HeaderCell(context);
        this.headerCell2.setText(LocaleController.getString("ChatHistory", R.string.ChatHistory));
        this.headerCell2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.linearLayout.addView(this.headerCell2);
        this.radioButtonCell3 = new RadioButtonCell(context);
        this.radioButtonCell3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.radioButtonCell3.setTextAndValue(LocaleController.getString("ChatHistoryVisible", R.string.ChatHistoryVisible), LocaleController.getString("ChatHistoryVisibleInfo", R.string.ChatHistoryVisibleInfo), true ^ this.historyHidden);
        this.linearLayout.addView(this.radioButtonCell3, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell3.setOnClickListener(new C09944());
        this.radioButtonCell4 = new RadioButtonCell(context);
        this.radioButtonCell4.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.radioButtonCell4.setTextAndValue(LocaleController.getString("ChatHistoryHidden", R.string.ChatHistoryHidden), LocaleController.getString("ChatHistoryHiddenInfo", R.string.ChatHistoryHiddenInfo), this.historyHidden);
        this.linearLayout.addView(this.radioButtonCell4, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell4.setOnClickListener(new C09955());
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = args[0];
            if (chatFull.id == this.chatId) {
                if (this.info == null) {
                    this.historyHidden = chatFull.hidden_prehistory;
                    if (this.radioButtonCell3 != null) {
                        this.radioButtonCell3.setChecked(this.historyHidden ^ 1, false);
                        this.radioButtonCell4.setChecked(this.historyHidden, false);
                    }
                }
                this.info = chatFull;
            }
        }
    }

    public void setInfo(ChatFull chatFull) {
        if (this.info == null && chatFull != null) {
            this.historyHidden = chatFull.hidden_prehistory;
        }
        this.info = chatFull;
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[28];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCheckCell2.class, HeaderCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r1[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r1[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r1[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r1[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r1[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r1[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r1[9] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[10] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r1[11] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb);
        r1[12] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        r1[13] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked);
        r1[14] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        r1[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r1[16] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r1[17] = new ThemeDescription(this.linearLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r1[18] = new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r1[19] = new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        r1[20] = new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        r1[21] = new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[22] = new ThemeDescription(this.radioButtonCell3, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r1[23] = new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r1[24] = new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        r1[25] = new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        r1[26] = new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[27] = new ThemeDescription(this.radioButtonCell4, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        return r1;
    }
}
