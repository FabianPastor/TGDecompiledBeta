package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.TL_channels_exportInvite;
import org.telegram.tgnet.TLRPC.TL_chatInviteExported;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_exportChatInvite;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;

public class GroupInviteActivity extends BaseFragment implements NotificationCenterDelegate {
    private int chat_id;
    private int copyLinkRow;
    private ExportedChatInvite invite;
    private int linkInfoRow;
    private int linkRow;
    private ListAdapter listAdapter;
    private boolean loading;
    private int revokeLinkRow;
    private int rowCount;
    private int shadowRow;
    private int shareLinkRow;

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i == GroupInviteActivity.this.revokeLinkRow || i == GroupInviteActivity.this.copyLinkRow || i == GroupInviteActivity.this.shareLinkRow || i == GroupInviteActivity.this.linkRow;
        }

        public int getCount() {
            return GroupInviteActivity.this.loading ? 0 : GroupInviteActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return false;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int type = getItemViewType(i);
            if (type == 0) {
                if (view == null) {
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                TextSettingsCell textCell = (TextSettingsCell) view;
                if (i == GroupInviteActivity.this.copyLinkRow) {
                    textCell.setText(LocaleController.getString("CopyLink", R.string.CopyLink), true);
                } else if (i == GroupInviteActivity.this.shareLinkRow) {
                    textCell.setText(LocaleController.getString("ShareLink", R.string.ShareLink), false);
                } else if (i == GroupInviteActivity.this.revokeLinkRow) {
                    textCell.setText(LocaleController.getString("RevokeLink", R.string.RevokeLink), true);
                }
            } else if (type == 1) {
                if (view == null) {
                    view = new TextInfoPrivacyCell(this.mContext);
                }
                if (i == GroupInviteActivity.this.shadowRow) {
                    ((TextInfoPrivacyCell) view).setText("");
                    view.setBackgroundResource(R.drawable.greydivider_bottom);
                } else if (i == GroupInviteActivity.this.linkInfoRow) {
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(GroupInviteActivity.this.chat_id));
                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                        ((TextInfoPrivacyCell) view).setText(LocaleController.getString("LinkInfo", R.string.LinkInfo));
                    } else {
                        ((TextInfoPrivacyCell) view).setText(LocaleController.getString("ChannelLinkInfo", R.string.ChannelLinkInfo));
                    }
                    view.setBackgroundResource(R.drawable.greydivider);
                }
            } else if (type == 2) {
                if (view == null) {
                    view = new TextBlockCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                ((TextBlockCell) view).setText(GroupInviteActivity.this.invite != null ? GroupInviteActivity.this.invite.link : "error", false);
            }
            return view;
        }

        public int getItemViewType(int i) {
            if (i == GroupInviteActivity.this.copyLinkRow || i == GroupInviteActivity.this.shareLinkRow || i == GroupInviteActivity.this.revokeLinkRow) {
                return 0;
            }
            if (i == GroupInviteActivity.this.shadowRow || i == GroupInviteActivity.this.linkInfoRow) {
                return 1;
            }
            if (i == GroupInviteActivity.this.linkRow) {
                return 2;
            }
            return 0;
        }

        public int getViewTypeCount() {
            return 3;
        }

        public boolean isEmpty() {
            return GroupInviteActivity.this.loading;
        }
    }

    public GroupInviteActivity(int cid) {
        this.chat_id = cid;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        MessagesController.getInstance().loadFullChat(this.chat_id, this.classGuid, true);
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
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("InviteLink", R.string.InviteLink));
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
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        FrameLayout progressView = new FrameLayout(context);
        frameLayout.addView(progressView, LayoutHelper.createFrame(-1, -1.0f));
        progressView.addView(new ProgressBar(context), LayoutHelper.createFrame(-2, -2, 17));
        ListView listView = new ListView(context);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setEmptyView(progressView);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDrawSelectorOnTop(true);
        frameLayout.addView(listView, LayoutHelper.createFrame(-1, -1, 51));
        listView.setAdapter(this.listAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (GroupInviteActivity.this.getParentActivity() != null) {
                    if (i == GroupInviteActivity.this.copyLinkRow || i == GroupInviteActivity.this.linkRow) {
                        if (GroupInviteActivity.this.invite != null) {
                            try {
                                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", GroupInviteActivity.this.invite.link));
                                Toast.makeText(GroupInviteActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", R.string.LinkCopied), 0).show();
                            } catch (Throwable e) {
                                FileLog.e("tmessages", e);
                            }
                        }
                    } else if (i == GroupInviteActivity.this.shareLinkRow) {
                        if (GroupInviteActivity.this.invite != null) {
                            try {
                                Intent intent = new Intent("android.intent.action.SEND");
                                intent.setType("text/plain");
                                intent.putExtra("android.intent.extra.TEXT", GroupInviteActivity.this.invite.link);
                                GroupInviteActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteToGroupByLink", R.string.InviteToGroupByLink)), 500);
                            } catch (Throwable e2) {
                                FileLog.e("tmessages", e2);
                            }
                        }
                    } else if (i == GroupInviteActivity.this.revokeLinkRow) {
                        Builder builder = new Builder(GroupInviteActivity.this.getParentActivity());
                        builder.setMessage(LocaleController.getString("RevokeAlert", R.string.RevokeAlert));
                        builder.setTitle(LocaleController.getString("RevokeLink", R.string.RevokeLink));
                        builder.setPositiveButton(LocaleController.getString("RevokeButton", R.string.RevokeButton), new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                GroupInviteActivity.this.generateLink(true);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        GroupInviteActivity.this.showDialog(builder.create());
                    }
                }
            }
        });
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoaded) {
            ChatFull info = args[0];
            int guid = ((Integer) args[1]).intValue();
            if (info.id == this.chat_id && guid == this.classGuid) {
                this.invite = MessagesController.getInstance().getExportedInvite(this.chat_id);
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

    private void generateLink(final boolean newRequest) {
        TLObject request;
        this.loading = true;
        TLObject req;
        if (ChatObject.isChannel(this.chat_id)) {
            req = new TL_channels_exportInvite();
            req.channel = MessagesController.getInputChannel(this.chat_id);
            request = req;
        } else {
            req = new TL_messages_exportChatInvite();
            req.chat_id = this.chat_id;
            request = req;
        }
        ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(request, new RequestDelegate() {
            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (error == null) {
                            GroupInviteActivity.this.invite = (ExportedChatInvite) response;
                            if (newRequest) {
                                if (GroupInviteActivity.this.getParentActivity() != null) {
                                    Builder builder = new Builder(GroupInviteActivity.this.getParentActivity());
                                    builder.setMessage(LocaleController.getString("RevokeAlertNewLink", R.string.RevokeAlertNewLink));
                                    builder.setTitle(LocaleController.getString("RevokeLink", R.string.RevokeLink));
                                    builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
                                    GroupInviteActivity.this.showDialog(builder.create());
                                } else {
                                    return;
                                }
                            }
                        }
                        GroupInviteActivity.this.loading = false;
                        GroupInviteActivity.this.listAdapter.notifyDataSetChanged();
                    }
                });
            }
        }), this.classGuid);
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }
}
