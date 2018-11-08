package org.telegram.p005ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SharedConfig.ProxyInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.DefaultItemAnimator;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.C0646ActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Cells.HeaderCell;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.Cells.TextCheckCell;
import org.telegram.p005ui.Cells.TextInfoPrivacyCell;
import org.telegram.p005ui.Cells.TextSettingsCell;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.p005ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestTimeDelegate;

/* renamed from: org.telegram.ui.ProxyListActivity */
public class ProxyListActivity extends BaseFragment implements NotificationCenterDelegate {
    private int callsDetailRow;
    private int callsRow;
    private int connectionsHeaderRow;
    private int currentConnectionState;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int proxyAddRow;
    private int proxyDetailRow;
    private int proxyEndRow;
    private int proxyStartRow;
    private int rowCount;
    private int useProxyDetailRow;
    private boolean useProxyForCalls;
    private int useProxyRow;
    private boolean useProxySettings;

    /* renamed from: org.telegram.ui.ProxyListActivity$TextDetailProxyCell */
    public class TextDetailProxyCell extends FrameLayout {
        private Drawable checkDrawable;
        private ImageView checkImageView;
        private int color;
        private ProxyInfo currentInfo;
        private TextView textView;
        final /* synthetic */ ProxyListActivity this$0;
        private TextView valueTextView;

        public TextDetailProxyCell(final ProxyListActivity this$0, Context context) {
            int i;
            int i2;
            int i3 = 17;
            int i4 = 3;
            this.this$0 = this$0;
            super(context);
            this.textView = new TextView(context);
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TruncateAt.END);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            View view = this.textView;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            addView(view, LayoutHelper.createFrame(-2, -2.0f, i | 48, (float) (LocaleController.isRTL ? 56 : 17), 10.0f, (float) (LocaleController.isRTL ? 17 : 56), 0.0f));
            this.valueTextView = new TextView(context);
            this.valueTextView.setTextSize(1, 13.0f);
            TextView textView = this.valueTextView;
            if (LocaleController.isRTL) {
                i2 = 5;
            } else {
                i2 = 3;
            }
            textView.setGravity(i2);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setCompoundDrawablePadding(AndroidUtilities.m10dp(6.0f));
            this.valueTextView.setEllipsize(TruncateAt.END);
            this.valueTextView.setPadding(0, 0, 0, 0);
            view = this.valueTextView;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            i |= 48;
            float f = (float) (LocaleController.isRTL ? 56 : 17);
            if (!LocaleController.isRTL) {
                i3 = 56;
            }
            addView(view, LayoutHelper.createFrame(-2, -2.0f, i, f, 35.0f, (float) i3, 0.0f));
            this.checkImageView = new ImageView(context);
            this.checkImageView.setImageResource(R.drawable.profile_info);
            this.checkImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3), Mode.MULTIPLY));
            this.checkImageView.setScaleType(ScaleType.CENTER);
            View view2 = this.checkImageView;
            if (!LocaleController.isRTL) {
                i4 = 5;
            }
            addView(view2, LayoutHelper.createFrame(48, 48.0f, i4 | 48, 8.0f, 8.0f, 8.0f, 0.0f));
            this.checkImageView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    TextDetailProxyCell.this.this$0.presentFragment(new ProxySettingsActivity(TextDetailProxyCell.this.currentInfo));
                }
            });
            setWillNotDraw(false);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m10dp(64.0f) + 1, NUM));
        }

        public void setProxy(ProxyInfo proxyInfo) {
            this.textView.setText(proxyInfo.address + ":" + proxyInfo.port);
            this.currentInfo = proxyInfo;
            updateStatus();
        }

        public void updateStatus() {
            String colorKey;
            if (SharedConfig.currentProxy == this.currentInfo && this.this$0.useProxySettings) {
                if (this.this$0.currentConnectionState == 3 || this.this$0.currentConnectionState == 5) {
                    colorKey = Theme.key_windowBackgroundWhiteBlueText6;
                    if (this.currentInfo.ping != 0) {
                        this.valueTextView.setText(LocaleController.getString("Connected", R.string.Connected) + ", " + LocaleController.formatString("Ping", R.string.Ping, Long.valueOf(this.currentInfo.ping)));
                    } else {
                        this.valueTextView.setText(LocaleController.getString("Connected", R.string.Connected));
                    }
                    if (!(this.currentInfo.checking || this.currentInfo.available)) {
                        this.currentInfo.availableCheckTime = 0;
                    }
                } else {
                    colorKey = Theme.key_windowBackgroundWhiteGrayText2;
                    this.valueTextView.setText(LocaleController.getString("Connecting", R.string.Connecting));
                }
            } else if (this.currentInfo.checking) {
                this.valueTextView.setText(LocaleController.getString("Checking", R.string.Checking));
                colorKey = Theme.key_windowBackgroundWhiteGrayText2;
            } else if (this.currentInfo.available) {
                if (this.currentInfo.ping != 0) {
                    this.valueTextView.setText(LocaleController.getString("Available", R.string.Available) + ", " + LocaleController.formatString("Ping", R.string.Ping, Long.valueOf(this.currentInfo.ping)));
                } else {
                    this.valueTextView.setText(LocaleController.getString("Available", R.string.Available));
                }
                colorKey = Theme.key_windowBackgroundWhiteGreenText;
            } else {
                this.valueTextView.setText(LocaleController.getString("Unavailable", R.string.Unavailable));
                colorKey = Theme.key_windowBackgroundWhiteRedText4;
            }
            this.color = Theme.getColor(colorKey);
            this.valueTextView.setTag(colorKey);
            this.valueTextView.setTextColor(this.color);
            if (this.checkDrawable != null) {
                this.checkDrawable.setColorFilter(new PorterDuffColorFilter(this.color, Mode.MULTIPLY));
            }
        }

        public void setChecked(boolean checked) {
            if (checked) {
                if (this.checkDrawable == null) {
                    this.checkDrawable = getResources().getDrawable(R.drawable.proxy_check).mutate();
                }
                if (this.checkDrawable != null) {
                    this.checkDrawable.setColorFilter(new PorterDuffColorFilter(this.color, Mode.MULTIPLY));
                }
                if (LocaleController.isRTL) {
                    this.valueTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, this.checkDrawable, null);
                    return;
                } else {
                    this.valueTextView.setCompoundDrawablesWithIntrinsicBounds(this.checkDrawable, null, null, null);
                    return;
                }
            }
            this.valueTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

        public void setValue(CharSequence value) {
            this.valueTextView.setText(value);
        }

        protected void onDraw(Canvas canvas) {
            canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }

    /* renamed from: org.telegram.ui.ProxyListActivity$1 */
    class C22751 extends ActionBarMenuOnItemClick {
        C22751() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ProxyListActivity.this.lambda$checkDiscard$69$PassportActivity();
            }
        }
    }

    /* renamed from: org.telegram.ui.ProxyListActivity$2 */
    class C22762 implements OnItemClickListener {
        C22762() {
        }

        public void onItemClick(View view, int position) {
            Editor editor;
            Holder holder;
            int a;
            if (position == ProxyListActivity.this.useProxyRow) {
                SharedPreferences preferences;
                if (SharedConfig.currentProxy == null) {
                    if (SharedConfig.proxyList.isEmpty()) {
                        ProxyListActivity.this.presentFragment(new ProxySettingsActivity());
                        return;
                    }
                    SharedConfig.currentProxy = (ProxyInfo) SharedConfig.proxyList.get(0);
                    if (!ProxyListActivity.this.useProxySettings) {
                        preferences = MessagesController.getGlobalMainSettings();
                        editor = MessagesController.getGlobalMainSettings().edit();
                        editor.putString("proxy_ip", SharedConfig.currentProxy.address);
                        editor.putString("proxy_pass", SharedConfig.currentProxy.password);
                        editor.putString("proxy_user", SharedConfig.currentProxy.username);
                        editor.putInt("proxy_port", SharedConfig.currentProxy.port);
                        editor.putString("proxy_secret", SharedConfig.currentProxy.secret);
                        editor.commit();
                    }
                }
                ProxyListActivity.this.useProxySettings = !ProxyListActivity.this.useProxySettings;
                preferences = MessagesController.getGlobalMainSettings();
                ((TextCheckCell) view).setChecked(ProxyListActivity.this.useProxySettings);
                if (!ProxyListActivity.this.useProxySettings) {
                    holder = (Holder) ProxyListActivity.this.listView.findViewHolderForAdapterPosition(ProxyListActivity.this.callsRow);
                    if (holder != null) {
                        holder.itemView.setChecked(false);
                    }
                    ProxyListActivity.this.useProxyForCalls = false;
                }
                editor = MessagesController.getGlobalMainSettings().edit();
                editor.putBoolean("proxy_enabled", ProxyListActivity.this.useProxySettings);
                editor.commit();
                ConnectionsManager.setProxySettings(ProxyListActivity.this.useProxySettings, SharedConfig.currentProxy.address, SharedConfig.currentProxy.port, SharedConfig.currentProxy.username, SharedConfig.currentProxy.password, SharedConfig.currentProxy.secret);
                NotificationCenter.getGlobalInstance().removeObserver(ProxyListActivity.this, NotificationCenter.proxySettingsChanged);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
                NotificationCenter.getGlobalInstance().addObserver(ProxyListActivity.this, NotificationCenter.proxySettingsChanged);
                for (a = ProxyListActivity.this.proxyStartRow; a < ProxyListActivity.this.proxyEndRow; a++) {
                    holder = (Holder) ProxyListActivity.this.listView.findViewHolderForAdapterPosition(a);
                    if (holder != null) {
                        holder.itemView.updateStatus();
                    }
                }
            } else if (position == ProxyListActivity.this.callsRow) {
                ProxyListActivity.this.useProxyForCalls = !ProxyListActivity.this.useProxyForCalls;
                ((TextCheckCell) view).setChecked(ProxyListActivity.this.useProxyForCalls);
                editor = MessagesController.getGlobalMainSettings().edit();
                editor.putBoolean("proxy_enabled_calls", ProxyListActivity.this.useProxyForCalls);
                editor.commit();
            } else if (position >= ProxyListActivity.this.proxyStartRow && position < ProxyListActivity.this.proxyEndRow) {
                ProxyInfo info = (ProxyInfo) SharedConfig.proxyList.get(position - ProxyListActivity.this.proxyStartRow);
                ProxyListActivity.this.useProxySettings = true;
                editor = MessagesController.getGlobalMainSettings().edit();
                editor.putString("proxy_ip", info.address);
                editor.putString("proxy_pass", info.password);
                editor.putString("proxy_user", info.username);
                editor.putInt("proxy_port", info.port);
                editor.putString("proxy_secret", info.secret);
                editor.putBoolean("proxy_enabled", ProxyListActivity.this.useProxySettings);
                if (!info.secret.isEmpty()) {
                    ProxyListActivity.this.useProxyForCalls = false;
                    editor.putBoolean("proxy_enabled_calls", false);
                }
                editor.commit();
                SharedConfig.currentProxy = info;
                for (a = ProxyListActivity.this.proxyStartRow; a < ProxyListActivity.this.proxyEndRow; a++) {
                    holder = (Holder) ProxyListActivity.this.listView.findViewHolderForAdapterPosition(a);
                    if (holder != null) {
                        TextDetailProxyCell cell = (TextDetailProxyCell) holder.itemView;
                        cell.setChecked(cell.currentInfo == info);
                        cell.updateStatus();
                    }
                }
                ProxyListActivity.this.updateRows(false);
                holder = (Holder) ProxyListActivity.this.listView.findViewHolderForAdapterPosition(ProxyListActivity.this.useProxyRow);
                if (holder != null) {
                    ((TextCheckCell) holder.itemView).setChecked(true);
                }
                ConnectionsManager.setProxySettings(ProxyListActivity.this.useProxySettings, SharedConfig.currentProxy.address, SharedConfig.currentProxy.port, SharedConfig.currentProxy.username, SharedConfig.currentProxy.password, SharedConfig.currentProxy.secret);
            } else if (position == ProxyListActivity.this.proxyAddRow) {
                ProxyListActivity.this.presentFragment(new ProxySettingsActivity());
            }
        }
    }

    /* renamed from: org.telegram.ui.ProxyListActivity$3 */
    class C22773 implements OnItemLongClickListener {
        C22773() {
        }

        public boolean onItemClick(View view, int position) {
            if (position < ProxyListActivity.this.proxyStartRow || position >= ProxyListActivity.this.proxyEndRow) {
                return false;
            }
            final ProxyInfo info = (ProxyInfo) SharedConfig.proxyList.get(position - ProxyListActivity.this.proxyStartRow);
            Builder builder = new Builder(ProxyListActivity.this.getParentActivity());
            builder.setMessage(LocaleController.getString("DeleteProxy", R.string.DeleteProxy));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    SharedConfig.deleteProxy(info);
                    if (SharedConfig.currentProxy == null) {
                        ProxyListActivity.this.useProxyForCalls = false;
                        ProxyListActivity.this.useProxySettings = false;
                    }
                    NotificationCenter.getGlobalInstance().removeObserver(ProxyListActivity.this, NotificationCenter.proxySettingsChanged);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
                    NotificationCenter.getGlobalInstance().addObserver(ProxyListActivity.this, NotificationCenter.proxySettingsChanged);
                    ProxyListActivity.this.updateRows(true);
                }
            });
            ProxyListActivity.this.showDialog(builder.create());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ProxyListActivity$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ProxyListActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    if (position == ProxyListActivity.this.proxyDetailRow && ProxyListActivity.this.callsRow == -1) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 1:
                    TextSettingsCell textCell = holder.itemView;
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    if (position == ProxyListActivity.this.proxyAddRow) {
                        textCell.setText(LocaleController.getString("AddProxy", R.string.AddProxy), false);
                        return;
                    }
                    return;
                case 2:
                    HeaderCell headerCell = holder.itemView;
                    if (position == ProxyListActivity.this.connectionsHeaderRow) {
                        headerCell.setText(LocaleController.getString("ProxyConnections", R.string.ProxyConnections));
                        return;
                    }
                    return;
                case 3:
                    TextCheckCell checkCell = holder.itemView;
                    if (position == ProxyListActivity.this.useProxyRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UseProxySettings", R.string.UseProxySettings), ProxyListActivity.this.useProxySettings, false);
                        return;
                    } else if (position == ProxyListActivity.this.callsRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UseProxyForCalls", R.string.UseProxyForCalls), ProxyListActivity.this.useProxyForCalls, false);
                        return;
                    } else {
                        return;
                    }
                case 4:
                    TextInfoPrivacyCell cell = holder.itemView;
                    if (position == ProxyListActivity.this.callsDetailRow) {
                        cell.setText(LocaleController.getString("UseProxyForCallsInfo", R.string.UseProxyForCallsInfo));
                        cell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                    return;
                case 5:
                    TextDetailProxyCell cell2 = holder.itemView;
                    ProxyInfo info = (ProxyInfo) SharedConfig.proxyList.get(position - ProxyListActivity.this.proxyStartRow);
                    cell2.setProxy(info);
                    if (SharedConfig.currentProxy == info) {
                        z = true;
                    }
                    cell2.setChecked(z);
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(ViewHolder holder) {
            if (holder.getItemViewType() == 3) {
                TextCheckCell checkCell = holder.itemView;
                int position = holder.getAdapterPosition();
                if (position == ProxyListActivity.this.useProxyRow) {
                    checkCell.setChecked(ProxyListActivity.this.useProxySettings);
                } else if (position == ProxyListActivity.this.callsRow) {
                    checkCell.setChecked(ProxyListActivity.this.useProxyForCalls);
                }
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == ProxyListActivity.this.useProxyRow || position == ProxyListActivity.this.callsRow || position == ProxyListActivity.this.proxyAddRow || (position >= ProxyListActivity.this.proxyStartRow && position < ProxyListActivity.this.proxyEndRow);
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 1:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 5:
                    view = new TextDetailProxyCell(ProxyListActivity.this, this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == ProxyListActivity.this.useProxyDetailRow || position == ProxyListActivity.this.proxyDetailRow) {
                return 0;
            }
            if (position == ProxyListActivity.this.proxyAddRow) {
                return 1;
            }
            if (position == ProxyListActivity.this.useProxyRow || position == ProxyListActivity.this.callsRow) {
                return 3;
            }
            if (position == ProxyListActivity.this.connectionsHeaderRow) {
                return 2;
            }
            if (position < ProxyListActivity.this.proxyStartRow || position >= ProxyListActivity.this.proxyEndRow) {
                return 4;
            }
            return 5;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        SharedConfig.loadProxyList();
        this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxyCheckDone);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdatedConnectionState);
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        boolean z = preferences.getBoolean("proxy_enabled", false) && !SharedConfig.proxyList.isEmpty();
        this.useProxySettings = z;
        this.useProxyForCalls = preferences.getBoolean("proxy_enabled_calls", false);
        updateRows(true);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxyCheckDone);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdatedConnectionState);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("ProxySettings", R.string.ProxySettings));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new C22751());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C22762());
        this.listView.setOnItemLongClickListener(new C22773());
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    private void updateRows(boolean notify) {
        boolean change = true;
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.useProxyRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.useProxyDetailRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.connectionsHeaderRow = i;
        if (SharedConfig.proxyList.isEmpty()) {
            this.proxyStartRow = -1;
            this.proxyEndRow = -1;
        } else {
            this.proxyStartRow = this.rowCount;
            this.rowCount += SharedConfig.proxyList.size();
            this.proxyEndRow = this.rowCount;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.proxyAddRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.proxyDetailRow = i;
        if (SharedConfig.currentProxy == null || SharedConfig.currentProxy.secret.isEmpty()) {
            if (this.callsRow != -1) {
                change = false;
            }
            int i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.callsRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.callsDetailRow = i2;
            if (!notify && change) {
                this.listAdapter.notifyItemChanged(this.proxyDetailRow);
                this.listAdapter.notifyItemRangeInserted(this.proxyDetailRow + 1, 2);
            }
        } else {
            if (this.callsRow == -1) {
                change = false;
            }
            this.callsRow = -1;
            this.callsDetailRow = -1;
            if (!notify && change) {
                this.listAdapter.notifyItemChanged(this.proxyDetailRow);
                this.listAdapter.notifyItemRangeRemoved(this.proxyDetailRow + 1, 2);
            }
        }
        checkProxyList();
        if (notify && this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void checkProxyList() {
        int count = SharedConfig.proxyList.size();
        for (int a = 0; a < count; a++) {
            final ProxyInfo proxyInfo = (ProxyInfo) SharedConfig.proxyList.get(a);
            if (!proxyInfo.checking && SystemClock.elapsedRealtime() - proxyInfo.availableCheckTime >= 120000) {
                proxyInfo.checking = true;
                proxyInfo.proxyCheckPingId = ConnectionsManager.getInstance(this.currentAccount).checkProxy(proxyInfo.address, proxyInfo.port, proxyInfo.username, proxyInfo.password, proxyInfo.secret, new RequestTimeDelegate() {
                    public void run(final long time) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                proxyInfo.availableCheckTime = SystemClock.elapsedRealtime();
                                proxyInfo.checking = false;
                                if (time == -1) {
                                    proxyInfo.available = false;
                                    proxyInfo.ping = 0;
                                } else {
                                    proxyInfo.ping = time;
                                    proxyInfo.available = true;
                                }
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxyCheckDone, proxyInfo);
                            }
                        });
                    }
                });
            }
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int idx;
        Holder holder;
        if (id == NotificationCenter.proxySettingsChanged) {
            updateRows(true);
        } else if (id == NotificationCenter.didUpdatedConnectionState) {
            int state = ConnectionsManager.getInstance(account).getConnectionState();
            if (this.currentConnectionState != state) {
                this.currentConnectionState = state;
                if (this.listView != null && SharedConfig.currentProxy != null) {
                    idx = SharedConfig.proxyList.indexOf(SharedConfig.currentProxy);
                    if (idx >= 0) {
                        holder = (Holder) this.listView.findViewHolderForAdapterPosition(this.proxyStartRow + idx);
                        if (holder != null) {
                            holder.itemView.updateStatus();
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.proxyCheckDone && this.listView != null) {
            idx = SharedConfig.proxyList.indexOf(args[0]);
            if (idx >= 0) {
                holder = (Holder) this.listView.findViewHolderForAdapterPosition(this.proxyStartRow + idx);
                if (holder != null) {
                    ((TextDetailProxyCell) holder.itemView).updateStatus();
                }
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r9 = new ThemeDescription[27];
        r9[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, TextDetailProxyCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r9[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r9[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r9[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r9[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r9[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r9[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r9[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r9[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r9[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r9[10] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[11] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r9[12] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailProxyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[13] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG) | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueText6);
        r9[14] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG) | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r9[15] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG) | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGreenText);
        r9[16] = new ThemeDescription(this.listView, (ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG) | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText4);
        r9[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"checkImageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r9[18] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r9[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r9[21] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb);
        r9[22] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        r9[23] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked);
        r9[24] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        r9[25] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r9[26] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        return r9;
    }
}
