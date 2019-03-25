package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.TL_chatInviteExported;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_exportChatInvite;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class GroupInviteActivity extends BaseFragment implements NotificationCenterDelegate {
    private int chat_id;
    private int copyLinkRow;
    private EmptyTextProgressView emptyView;
    private ExportedChatInvite invite;
    private int linkInfoRow;
    private int linkRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loading;
    private int revokeLinkRow;
    private int rowCount;
    private int shadowRow;
    private int shareLinkRow;

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == GroupInviteActivity.this.revokeLinkRow || position == GroupInviteActivity.this.copyLinkRow || position == GroupInviteActivity.this.shareLinkRow || position == GroupInviteActivity.this.linkRow;
        }

        public int getItemCount() {
            return GroupInviteActivity.this.loading ? 0 : GroupInviteActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                default:
                    view = new TextBlockCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell textCell = holder.itemView;
                    if (position == GroupInviteActivity.this.copyLinkRow) {
                        textCell.setText(LocaleController.getString("CopyLink", NUM), true);
                        return;
                    } else if (position == GroupInviteActivity.this.shareLinkRow) {
                        textCell.setText(LocaleController.getString("ShareLink", NUM), false);
                        return;
                    } else if (position == GroupInviteActivity.this.revokeLinkRow) {
                        textCell.setText(LocaleController.getString("RevokeLink", NUM), true);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = holder.itemView;
                    if (position == GroupInviteActivity.this.shadowRow) {
                        privacyCell.setText("");
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == GroupInviteActivity.this.linkInfoRow) {
                        Chat chat = MessagesController.getInstance(GroupInviteActivity.this.currentAccount).getChat(Integer.valueOf(GroupInviteActivity.this.chat_id));
                        if (!ChatObject.isChannel(chat) || chat.megagroup) {
                            privacyCell.setText(LocaleController.getString("LinkInfo", NUM));
                        } else {
                            privacyCell.setText(LocaleController.getString("ChannelLinkInfo", NUM));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    holder.itemView.setText(GroupInviteActivity.this.invite != null ? GroupInviteActivity.this.invite.link : "error", false);
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == GroupInviteActivity.this.copyLinkRow || position == GroupInviteActivity.this.shareLinkRow || position == GroupInviteActivity.this.revokeLinkRow) {
                return 0;
            }
            if (position == GroupInviteActivity.this.shadowRow || position == GroupInviteActivity.this.linkInfoRow) {
                return 1;
            }
            if (position == GroupInviteActivity.this.linkRow) {
                return 2;
            }
            return 0;
        }
    }

    public GroupInviteActivity(int cid) {
        this.chat_id = cid;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat_id, this.classGuid, true);
        this.loading = true;
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.linkRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.linkInfoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.copyLinkRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.revokeLinkRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.shareLinkRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.shadowRow = i;
        return true;
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("InviteLink", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    GroupInviteActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new GroupInviteActivity$$Lambda$0(this));
        return this.fragmentView;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$1$GroupInviteActivity(View view, int position) {
        if (getParentActivity() != null) {
            if (position == this.copyLinkRow || position == this.linkRow) {
                if (this.invite != null) {
                    try {
                        ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.invite.link));
                        Toast.makeText(getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            } else if (position == this.shareLinkRow) {
                if (this.invite != null) {
                    try {
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("text/plain");
                        intent.putExtra("android.intent.extra.TEXT", this.invite.link);
                        getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteToGroupByLink", NUM)), 500);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
            } else if (position == this.revokeLinkRow) {
                Builder builder = new Builder(getParentActivity());
                builder.setMessage(LocaleController.getString("RevokeAlert", NUM));
                builder.setTitle(LocaleController.getString("RevokeLink", NUM));
                builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new GroupInviteActivity$$Lambda$3(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                showDialog(builder.create());
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$0$GroupInviteActivity(DialogInterface dialogInterface, int i) {
        generateLink(true);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad) {
            ChatFull info = args[0];
            int guid = ((Integer) args[1]).intValue();
            if (info.id == this.chat_id && guid == this.classGuid) {
                this.invite = MessagesController.getInstance(this.currentAccount).getExportedInvite(this.chat_id);
                if (this.invite instanceof TL_chatInviteExported) {
                    this.loading = false;
                    if (this.listAdapter != null) {
                        this.listAdapter.notifyDataSetChanged();
                        return;
                    }
                    return;
                }
                generateLink(false);
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void generateLink(boolean newRequest) {
        this.loading = true;
        TL_messages_exportChatInvite req = new TL_messages_exportChatInvite();
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chat_id);
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new GroupInviteActivity$$Lambda$1(this, newRequest)), this.classGuid);
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$generateLink$3$GroupInviteActivity(boolean newRequest, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new GroupInviteActivity$$Lambda$2(this, error, response, newRequest));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$2$GroupInviteActivity(TL_error error, TLObject response, boolean newRequest) {
        if (error == null) {
            this.invite = (ExportedChatInvite) response;
            if (newRequest) {
                if (getParentActivity() != null) {
                    Builder builder = new Builder(getParentActivity());
                    builder.setMessage(LocaleController.getString("RevokeAlertNewLink", NUM));
                    builder.setTitle(LocaleController.getString("RevokeLink", NUM));
                    builder.setNegativeButton(LocaleController.getString("OK", NUM), null);
                    showDialog(builder.create());
                } else {
                    return;
                }
            }
        }
        this.loading = false;
        this.listAdapter.notifyDataSetChanged();
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[14];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextBlockCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[9] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        return themeDescriptionArr;
    }
}
