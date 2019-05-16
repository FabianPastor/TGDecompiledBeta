package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.VcardItem;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.PhonebookSelectActivity.PhonebookSelectActivityDelegate;

public class PhonebookShareActivity extends BaseFragment {
    private ListAdapter adapter;
    private BackupImageView avatarImage;
    private FrameLayout bottomLayout;
    private User currentUser;
    private PhonebookSelectActivityDelegate delegate;
    private int detailRow;
    private int emptyRow;
    private int extraHeight;
    private View extraHeightView;
    private boolean isImport;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private TextView nameTextView;
    private ArrayList<VcardItem> other = new ArrayList();
    private int overscrollRow;
    private int phoneDividerRow;
    private int phoneEndRow;
    private int phoneStartRow;
    private ArrayList<VcardItem> phones = new ArrayList();
    private int rowCount;
    private View shadowView;
    private TextView shareTextView;
    private int vcardEndRow;
    private int vcardStartRow;

    public class TextCheckBoxCell extends FrameLayout {
        private CheckBoxSquare checkBox;
        private ImageView imageView;
        private TextView textView;
        final /* synthetic */ PhonebookShareActivity this$0;
        private TextView valueTextView;

        public TextCheckBoxCell(PhonebookShareActivity phonebookShareActivity, Context context) {
            float f;
            float f2;
            Context context2 = context;
            this.this$0 = phonebookShareActivity;
            super(context2);
            this.textView = new TextView(context2);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setSingleLine(false);
            int i = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.textView.setEllipsize(TruncateAt.END);
            TextView textView = this.textView;
            int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
            if (LocaleController.isRTL) {
                f = (float) (phonebookShareActivity.isImport ? 17 : 64);
            } else {
                f = 71.0f;
            }
            if (LocaleController.isRTL) {
                f2 = 71.0f;
            } else {
                f2 = (float) (phonebookShareActivity.isImport ? 17 : 64);
            }
            addView(textView, LayoutHelper.createFrame(-1, -1.0f, i2, f, 10.0f, f2, 0.0f));
            this.valueTextView = new TextView(context2);
            this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            textView = this.valueTextView;
            i2 = LocaleController.isRTL ? 5 : 3;
            if (LocaleController.isRTL) {
                f = (float) (phonebookShareActivity.isImport ? 17 : 64);
            } else {
                f = 71.0f;
            }
            if (LocaleController.isRTL) {
                f2 = 71.0f;
            } else {
                f2 = (float) (phonebookShareActivity.isImport ? 17 : 64);
            }
            addView(textView, LayoutHelper.createFrame(-2, -2.0f, i2, f, 35.0f, f2, 0.0f));
            this.imageView = new ImageView(context2);
            this.imageView.setScaleType(ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), Mode.MULTIPLY));
            addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 20.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
            if (!phonebookShareActivity.isImport) {
                this.checkBox = new CheckBoxSquare(context2, false);
                this.checkBox.setDuplicateParentStateEnabled(false);
                this.checkBox.setFocusable(false);
                this.checkBox.setFocusableInTouchMode(false);
                this.checkBox.setClickable(false);
                CheckBoxSquare checkBoxSquare = this.checkBox;
                if (LocaleController.isRTL) {
                    i = 3;
                }
                addView(checkBoxSquare, LayoutHelper.createFrame(18, 18.0f, i | 16, 19.0f, 0.0f, 19.0f, 0.0f));
            }
        }

        public void invalidate() {
            super.invalidate();
            CheckBoxSquare checkBoxSquare = this.checkBox;
            if (checkBoxSquare != null) {
                checkBoxSquare.invalidate();
            }
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            int i3 = i;
            int i4 = i2;
            measureChildWithMargins(this.textView, i3, 0, i4, 0);
            measureChildWithMargins(this.valueTextView, i, 0, i2, 0);
            measureChildWithMargins(this.imageView, i3, 0, i4, 0);
            CheckBoxSquare checkBoxSquare = this.checkBox;
            if (checkBoxSquare != null) {
                measureChildWithMargins(checkBoxSquare, i, 0, i2, 0);
            }
            setMeasuredDimension(MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(64.0f), (this.textView.getMeasuredHeight() + this.valueTextView.getMeasuredHeight()) + AndroidUtilities.dp(20.0f)));
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            int measuredHeight = this.textView.getMeasuredHeight() + AndroidUtilities.dp(13.0f);
            TextView textView = this.valueTextView;
            textView.layout(textView.getLeft(), measuredHeight, this.valueTextView.getRight(), this.valueTextView.getMeasuredHeight() + measuredHeight);
        }

        public void setVCardItem(VcardItem vcardItem, int i) {
            this.textView.setText(vcardItem.getValue(true));
            this.valueTextView.setText(vcardItem.getType());
            CheckBoxSquare checkBoxSquare = this.checkBox;
            if (checkBoxSquare != null) {
                checkBoxSquare.setChecked(vcardItem.checked, false);
            }
            if (i != 0) {
                this.imageView.setImageResource(i);
            } else {
                this.imageView.setImageDrawable(null);
            }
        }

        public void setChecked(boolean z) {
            CheckBoxSquare checkBoxSquare = this.checkBox;
            if (checkBoxSquare != null) {
                checkBoxSquare.setChecked(z, true);
            }
        }

        public boolean isChecked() {
            CheckBoxSquare checkBoxSquare = this.checkBox;
            return checkBoxSquare != null && checkBoxSquare.isChecked();
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return PhonebookShareActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                if (itemViewType == 1) {
                    VcardItem vcardItem;
                    TextCheckBoxCell textCheckBoxCell = (TextCheckBoxCell) viewHolder.itemView;
                    int i2 = 0;
                    if (i < PhonebookShareActivity.this.phoneStartRow || i >= PhonebookShareActivity.this.phoneEndRow) {
                        vcardItem = (VcardItem) PhonebookShareActivity.this.other.get(i - PhonebookShareActivity.this.vcardStartRow);
                        if (i == PhonebookShareActivity.this.vcardStartRow) {
                            i2 = NUM;
                        }
                    } else {
                        vcardItem = (VcardItem) PhonebookShareActivity.this.phones.get(i - PhonebookShareActivity.this.phoneStartRow);
                        if (i == PhonebookShareActivity.this.phoneStartRow) {
                            i2 = NUM;
                        }
                    }
                    textCheckBoxCell.setVCardItem(vcardItem, i2);
                }
            } else if (i == PhonebookShareActivity.this.overscrollRow) {
                ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(88.0f));
            } else {
                ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(16.0f));
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return (adapterPosition >= PhonebookShareActivity.this.phoneStartRow && adapterPosition < PhonebookShareActivity.this.phoneEndRow) || (adapterPosition >= PhonebookShareActivity.this.vcardStartRow && adapterPosition < PhonebookShareActivity.this.vcardEndRow);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View emptyCell;
            String str = "windowBackgroundWhite";
            if (i == 0) {
                emptyCell = new EmptyCell(this.mContext);
                emptyCell.setBackgroundColor(Theme.getColor(str));
            } else if (i == 1) {
                emptyCell = new TextCheckBoxCell(PhonebookShareActivity.this, this.mContext);
                emptyCell.setBackgroundColor(Theme.getColor(str));
            } else if (i != 2) {
                View view;
                if (i != 3) {
                    view = null;
                } else {
                    view = new ShadowSectionCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                }
                emptyCell = view;
            } else {
                emptyCell = new DividerCell(this.mContext);
                emptyCell.setBackgroundColor(Theme.getColor(str));
                emptyCell.setPadding(AndroidUtilities.dp(72.0f), AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            }
            emptyCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(emptyCell);
        }

        public int getItemViewType(int i) {
            if (i == PhonebookShareActivity.this.emptyRow || i == PhonebookShareActivity.this.overscrollRow) {
                return 0;
            }
            if ((i >= PhonebookShareActivity.this.phoneStartRow && i < PhonebookShareActivity.this.phoneEndRow) || (i >= PhonebookShareActivity.this.vcardStartRow && i < PhonebookShareActivity.this.vcardEndRow)) {
                return 1;
            }
            if (i != PhonebookShareActivity.this.phoneDividerRow && i == PhonebookShareActivity.this.detailRow) {
                return 3;
            }
            return 2;
        }
    }

    public PhonebookShareActivity(Contact contact, Uri uri, File file, String str) {
        ArrayList loadVCardFromStream;
        ArrayList arrayList = new ArrayList();
        if (uri != null) {
            loadVCardFromStream = AndroidUtilities.loadVCardFromStream(uri, this.currentAccount, false, arrayList, str);
        } else if (file != null) {
            loadVCardFromStream = AndroidUtilities.loadVCardFromStream(Uri.fromFile(file), this.currentAccount, false, arrayList, str);
            file.delete();
            this.isImport = true;
        } else {
            String str2 = contact.key;
            if (str2 != null) {
                loadVCardFromStream = AndroidUtilities.loadVCardFromStream(Uri.withAppendedPath(Contacts.CONTENT_VCARD_URI, str2), this.currentAccount, true, arrayList, str);
            } else {
                this.currentUser = contact.user;
                VcardItem vcardItem = new VcardItem();
                vcardItem.type = 0;
                ArrayList arrayList2 = vcardItem.vcardData;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("TEL;MOBILE:+");
                stringBuilder.append(this.currentUser.phone);
                str = stringBuilder.toString();
                vcardItem.fullData = str;
                arrayList2.add(str);
                this.phones.add(vcardItem);
                loadVCardFromStream = null;
            }
        }
        if (loadVCardFromStream != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                VcardItem vcardItem2 = (VcardItem) arrayList.get(i);
                if (vcardItem2.type == 0) {
                    Object obj;
                    for (int i2 = 0; i2 < this.phones.size(); i2++) {
                        if (((VcardItem) this.phones.get(i2)).getValue(false).equals(vcardItem2.getValue(false))) {
                            obj = 1;
                            break;
                        }
                    }
                    obj = null;
                    if (obj != null) {
                        vcardItem2.checked = false;
                    } else {
                        this.phones.add(vcardItem2);
                    }
                } else {
                    this.other.add(vcardItem2);
                }
            }
            if (loadVCardFromStream != null && !loadVCardFromStream.isEmpty()) {
                this.currentUser = (User) loadVCardFromStream.get(0);
                if (contact != null) {
                    User user = contact.user;
                    if (user != null) {
                        this.currentUser.photo = user.photo;
                    }
                }
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        if (this.currentUser == null) {
            return false;
        }
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.overscrollRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.emptyRow = i;
        if (this.phones.isEmpty()) {
            this.phoneStartRow = -1;
            this.phoneEndRow = -1;
        } else {
            i = this.rowCount;
            this.phoneStartRow = i;
            this.rowCount = i + this.phones.size();
            this.phoneEndRow = this.rowCount;
        }
        if (this.other.isEmpty()) {
            this.phoneDividerRow = -1;
            this.vcardStartRow = -1;
            this.vcardEndRow = -1;
        } else {
            if (this.phones.isEmpty()) {
                this.phoneDividerRow = -1;
            } else {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.phoneDividerRow = i;
            }
            i = this.rowCount;
            this.vcardStartRow = i;
            this.rowCount = i + this.other.size();
            this.vcardEndRow = this.rowCount;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.detailRow = i;
        return true;
    }

    public View createView(Context context) {
        String str = "avatar_backgroundActionBarBlue";
        this.actionBar.setBackgroundColor(Theme.getColor(str));
        this.actionBar.setItemsBackgroundColor(Theme.getColor("avatar_actionBarSelectorBlue"), false);
        this.actionBar.setItemsColor(Theme.getColor("avatar_actionBarIconBlue"), false);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAddToContainer(false);
        this.extraHeight = 88;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PhonebookShareActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context) {
            /* Access modifiers changed, original: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (view != PhonebookShareActivity.this.listView) {
                    return super.drawChild(canvas, view, j);
                }
                boolean drawChild = super.drawChild(canvas, view, j);
                if (PhonebookShareActivity.this.parentLayout != null) {
                    int childCount = getChildCount();
                    int i = 0;
                    int i2 = 0;
                    while (i2 < childCount) {
                        View childAt = getChildAt(i2);
                        if (childAt != view && (childAt instanceof ActionBar) && childAt.getVisibility() == 0) {
                            if (((ActionBar) childAt).getCastShadows()) {
                                i = childAt.getMeasuredHeight();
                            }
                            PhonebookShareActivity.this.parentLayout.drawHeaderShadow(canvas, i);
                        } else {
                            i2++;
                        }
                    }
                    PhonebookShareActivity.this.parentLayout.drawHeaderShadow(canvas, i);
                }
                return drawChild;
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass3 anonymousClass3 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = anonymousClass3;
        recyclerListView.setLayoutManager(anonymousClass3);
        this.listView.setGlowColor(Theme.getColor(str));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.listView.setAdapter(new ListAdapter(context));
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setOnItemClickListener(new -$$Lambda$PhonebookShareActivity$87T4KO49pRqiUUhu1YF8Uwv0ffg(this));
        this.listView.setOnItemLongClickListener(new -$$Lambda$PhonebookShareActivity$ih_UP_iiSTWRH840i078MY0XuHs(this));
        frameLayout.addView(this.actionBar);
        this.extraHeightView = new View(context);
        this.extraHeightView.setPivotY(0.0f);
        this.extraHeightView.setBackgroundColor(Theme.getColor(str));
        frameLayout.addView(this.extraHeightView, LayoutHelper.createFrame(-1, 88.0f));
        this.shadowView = new View(context);
        this.shadowView.setBackgroundResource(NUM);
        frameLayout.addView(this.shadowView, LayoutHelper.createFrame(-1, 3.0f));
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setPivotX(0.0f);
        this.avatarImage.setPivotY(0.0f);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.getColor("profile_title"));
        this.nameTextView.setTextSize(1, 18.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity(3);
        String str2 = "fonts/rmedium.ttf";
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface(str2));
        this.nameTextView.setPivotX(0.0f);
        this.nameTextView.setPivotY(0.0f);
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 8.0f, 10.0f, 0.0f));
        needLayout();
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (PhonebookShareActivity.this.layoutManager.getItemCount() != 0) {
                    i = 0;
                    View childAt = recyclerView.getChildAt(0);
                    if (childAt != null) {
                        if (PhonebookShareActivity.this.layoutManager.findFirstVisibleItemPosition() == 0) {
                            i2 = AndroidUtilities.dp(88.0f);
                            if (childAt.getTop() < 0) {
                                i = childAt.getTop();
                            }
                            i += i2;
                        }
                        if (PhonebookShareActivity.this.extraHeight != i) {
                            PhonebookShareActivity.this.extraHeight = i;
                            PhonebookShareActivity.this.needLayout();
                        }
                    }
                }
            }
        });
        this.bottomLayout = new FrameLayout(context);
        this.bottomLayout.setBackgroundDrawable(Theme.createSelectorWithBackgroundDrawable(Theme.getColor("passport_authorizeBackground"), Theme.getColor("passport_authorizeBackgroundSelected")));
        frameLayout.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 80));
        this.bottomLayout.setOnClickListener(new -$$Lambda$PhonebookShareActivity$r6QmJiqDVDqQ-a81o-t3bySVxWU(this));
        this.shareTextView = new TextView(context);
        this.shareTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.shareTextView.setTextColor(Theme.getColor("passport_authorizeText"));
        if (this.isImport) {
            this.shareTextView.setText(LocaleController.getString("AddContactChat", NUM));
        } else {
            this.shareTextView.setText(LocaleController.getString("ContactShare", NUM));
        }
        this.shareTextView.setTextSize(1, 14.0f);
        this.shareTextView.setGravity(17);
        this.shareTextView.setTypeface(AndroidUtilities.getTypeface(str2));
        this.bottomLayout.addView(this.shareTextView, LayoutHelper.createFrame(-2, -1, 17));
        View view = new View(context);
        view.setBackgroundResource(NUM);
        frameLayout.addView(view, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        Drawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setProfile(true);
        User user = this.currentUser;
        avatarDrawable.setInfo(5, user.first_name, user.last_name, false);
        avatarDrawable.setColor(Theme.getColor("avatar_backgroundInProfileBlue"));
        this.avatarImage.setImage(ImageLocation.getForUser(this.currentUser, false), "50_50", avatarDrawable, this.currentUser);
        TextView textView = this.nameTextView;
        User user2 = this.currentUser;
        textView.setText(ContactsController.formatName(user2.first_name, user2.last_name));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$PhonebookShareActivity(View view, int i) {
        VcardItem vcardItem;
        int i2 = this.phoneStartRow;
        if (i < i2 || i >= this.phoneEndRow) {
            i2 = this.vcardStartRow;
            vcardItem = (i < i2 || i >= this.vcardEndRow) ? null : (VcardItem) this.other.get(i - i2);
        } else {
            vcardItem = (VcardItem) this.phones.get(i - i2);
        }
        if (vcardItem != null) {
            boolean z = true;
            if (this.isImport) {
                int i3 = vcardItem.type;
                StringBuilder stringBuilder;
                if (i3 == 0) {
                    try {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("tel:");
                        stringBuilder2.append(vcardItem.getValue(false));
                        Intent intent = new Intent("android.intent.action.DIAL", Uri.parse(stringBuilder2.toString()));
                        intent.addFlags(NUM);
                        getParentActivity().startActivityForResult(intent, 500);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else if (i3 == 1) {
                    Context parentActivity = getParentActivity();
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("mailto:");
                    stringBuilder.append(vcardItem.getValue(false));
                    Browser.openUrl(parentActivity, stringBuilder.toString());
                } else if (i3 == 3) {
                    String value = vcardItem.getValue(false);
                    if (!value.startsWith("http")) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("http://");
                        stringBuilder.append(value);
                        value = stringBuilder.toString();
                    }
                    Browser.openUrl(getParentActivity(), value);
                } else {
                    Builder builder = new Builder(getParentActivity());
                    builder.setItems(new CharSequence[]{LocaleController.getString("Copy", NUM)}, new -$$Lambda$PhonebookShareActivity$g-17B0C2dU4u_JSxmNJh3l3Yxbg(this, vcardItem));
                    showDialog(builder.create());
                }
            } else {
                vcardItem.checked ^= 1;
                if (i >= this.phoneStartRow && i < this.phoneEndRow) {
                    for (i = 0; i < this.phones.size(); i++) {
                        if (((VcardItem) this.phones.get(i)).checked) {
                            break;
                        }
                    }
                    z = false;
                    this.bottomLayout.setEnabled(z);
                    this.shareTextView.setAlpha(z ? 1.0f : 0.5f);
                }
                ((TextCheckBoxCell) view).setChecked(vcardItem.checked);
            }
        }
    }

    public /* synthetic */ void lambda$null$0$PhonebookShareActivity(VcardItem vcardItem, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", vcardItem.getValue(false)));
                Toast.makeText(getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public /* synthetic */ boolean lambda$createView$3$PhonebookShareActivity(View view, int i) {
        VcardItem vcardItem;
        int i2 = this.phoneStartRow;
        if (i < i2 || i >= this.phoneEndRow) {
            i2 = this.vcardStartRow;
            vcardItem = (i < i2 || i >= this.vcardEndRow) ? null : (VcardItem) this.other.get(i - i2);
        } else {
            vcardItem = (VcardItem) this.phones.get(i - i2);
        }
        if (vcardItem == null) {
            return false;
        }
        Builder builder = new Builder(getParentActivity());
        builder.setItems(new CharSequence[]{LocaleController.getString("Copy", NUM)}, new -$$Lambda$PhonebookShareActivity$0f5AxQdt4YhBWK3SWXLFBDqSogw(this, vcardItem));
        showDialog(builder.create());
        return true;
    }

    public /* synthetic */ void lambda$null$2$PhonebookShareActivity(VcardItem vcardItem, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", vcardItem.getValue(false)));
                if (vcardItem.type == 0) {
                    Toast.makeText(getParentActivity(), LocaleController.getString("PhoneCopied", NUM), 0).show();
                } else if (vcardItem.type == 1) {
                    Toast.makeText(getParentActivity(), LocaleController.getString("EmailCopied", NUM), 0).show();
                } else if (vcardItem.type == 3) {
                    Toast.makeText(getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
                } else {
                    Toast.makeText(getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public /* synthetic */ void lambda$createView$4$PhonebookShareActivity(View view) {
        if (!this.isImport) {
            StringBuilder stringBuilder;
            String str = this.currentUser.restriction_reason;
            if (str != null) {
                stringBuilder = new StringBuilder(str);
            } else {
                stringBuilder = new StringBuilder(String.format(Locale.US, "BEGIN:VCARD\nVERSION:3.0\nFN:%1$s\nEND:VCARD", new Object[]{ContactsController.formatName(r10.first_name, r10.last_name)}));
            }
            int lastIndexOf = stringBuilder.lastIndexOf("END:VCARD");
            if (lastIndexOf >= 0) {
                String str2;
                this.currentUser.phone = null;
                int size = this.phones.size() - 1;
                while (true) {
                    str2 = "\n";
                    if (size < 0) {
                        break;
                    }
                    VcardItem vcardItem = (VcardItem) this.phones.get(size);
                    if (vcardItem.checked) {
                        User user = this.currentUser;
                        if (user.phone == null) {
                            user.phone = vcardItem.getValue(false);
                        }
                        for (int i = 0; i < vcardItem.vcardData.size(); i++) {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append((String) vcardItem.vcardData.get(i));
                            stringBuilder2.append(str2);
                            stringBuilder.insert(lastIndexOf, stringBuilder2.toString());
                        }
                    }
                    size--;
                }
                for (size = this.other.size() - 1; size >= 0; size--) {
                    VcardItem vcardItem2 = (VcardItem) this.other.get(size);
                    if (vcardItem2.checked) {
                        for (int size2 = vcardItem2.vcardData.size() - 1; size2 >= 0; size2--) {
                            StringBuilder stringBuilder3 = new StringBuilder();
                            stringBuilder3.append((String) vcardItem2.vcardData.get(size2));
                            stringBuilder3.append(str2);
                            stringBuilder.insert(lastIndexOf, stringBuilder3.toString());
                        }
                    }
                }
                this.currentUser.restriction_reason = stringBuilder.toString();
            }
            this.delegate.didSelectContact(this.currentUser);
            finishFragment();
        } else if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AddContactTitle", NUM));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            builder.setItems(new CharSequence[]{LocaleController.getString("CreateNewContact", NUM), LocaleController.getString("AddToExistingContact", NUM)}, new OnClickListener() {
                private void fillRowWithType(String str, ContentValues contentValues) {
                    boolean startsWith = str.startsWith("X-");
                    String str2 = "data3";
                    Integer valueOf = Integer.valueOf(0);
                    String str3 = "data2";
                    if (startsWith) {
                        contentValues.put(str3, valueOf);
                        contentValues.put(str2, str.substring(2));
                    } else if ("PREF".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(12));
                    } else if ("HOME".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(1));
                    } else if ("MOBILE".equalsIgnoreCase(str) || "CELL".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(2));
                    } else if ("OTHER".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(7));
                    } else if ("WORK".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(3));
                    } else if ("RADIO".equalsIgnoreCase(str) || "VOICE".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(14));
                    } else if ("PAGER".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(6));
                    } else if ("CALLBACK".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(8));
                    } else if ("CAR".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(9));
                    } else if ("ASSISTANT".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(19));
                    } else if ("MMS".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(20));
                    } else if (str.startsWith("FAX")) {
                        contentValues.put(str3, Integer.valueOf(4));
                    } else {
                        contentValues.put(str3, valueOf);
                        contentValues.put(str2, str);
                    }
                }

                private void fillUrlRowWithType(String str, ContentValues contentValues) {
                    boolean startsWith = str.startsWith("X-");
                    String str2 = "data3";
                    Integer valueOf = Integer.valueOf(0);
                    String str3 = "data2";
                    if (startsWith) {
                        contentValues.put(str3, valueOf);
                        contentValues.put(str2, str.substring(2));
                    } else if ("HOMEPAGE".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(1));
                    } else if ("BLOG".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(2));
                    } else if ("PROFILE".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(3));
                    } else if ("HOME".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(4));
                    } else if ("WORK".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(5));
                    } else if ("FTP".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(6));
                    } else if ("OTHER".equalsIgnoreCase(str)) {
                        contentValues.put(str3, Integer.valueOf(7));
                    } else {
                        contentValues.put(str3, valueOf);
                        contentValues.put(str2, str);
                    }
                }

                /* JADX WARNING: Removed duplicated region for block: B:97:0x02a7  */
                /* JADX WARNING: Removed duplicated region for block: B:96:0x029f  */
                public void onClick(android.content.DialogInterface r19, int r20) {
                    /*
                    r18 = this;
                    r1 = r18;
                    r0 = r20;
                    r2 = org.telegram.ui.PhonebookShareActivity.this;
                    r2 = r2.getParentActivity();
                    if (r2 != 0) goto L_0x000d;
                L_0x000c:
                    return;
                L_0x000d:
                    r2 = 0;
                    r3 = 1;
                    if (r0 != 0) goto L_0x001f;
                L_0x0011:
                    r2 = new android.content.Intent;
                    r0 = "android.intent.action.INSERT";
                    r2.<init>(r0);
                    r0 = "vnd.android.cursor.dir/raw_contact";
                    r2.setType(r0);
                    goto L_0x002e;
                L_0x001f:
                    if (r0 != r3) goto L_0x002e;
                L_0x0021:
                    r2 = new android.content.Intent;
                    r0 = "android.intent.action.INSERT_OR_EDIT";
                    r2.<init>(r0);
                    r0 = "vnd.android.cursor.item/contact";
                    r2.setType(r0);
                L_0x002e:
                    r0 = org.telegram.ui.PhonebookShareActivity.this;
                    r0 = r0.currentUser;
                    r0 = r0.first_name;
                    r4 = org.telegram.ui.PhonebookShareActivity.this;
                    r4 = r4.currentUser;
                    r4 = r4.last_name;
                    r0 = org.telegram.messenger.ContactsController.formatName(r0, r4);
                    r4 = "name";
                    r2.putExtra(r4, r0);
                    r0 = new java.util.ArrayList;
                    r0.<init>();
                    r4 = 0;
                    r5 = 0;
                L_0x004e:
                    r6 = org.telegram.ui.PhonebookShareActivity.this;
                    r6 = r6.phones;
                    r6 = r6.size();
                    r7 = "data1";
                    r8 = "mimetype";
                    if (r5 >= r6) goto L_0x0089;
                L_0x005e:
                    r6 = org.telegram.ui.PhonebookShareActivity.this;
                    r6 = r6.phones;
                    r6 = r6.get(r5);
                    r6 = (org.telegram.messenger.AndroidUtilities.VcardItem) r6;
                    r9 = new android.content.ContentValues;
                    r9.<init>();
                    r10 = "vnd.android.cursor.item/phone_v2";
                    r9.put(r8, r10);
                    r8 = r6.getValue(r4);
                    r9.put(r7, r8);
                    r6 = r6.getRawType(r4);
                    r1.fillRowWithType(r6, r9);
                    r0.add(r9);
                    r5 = r5 + 1;
                    goto L_0x004e;
                L_0x0089:
                    r5 = 0;
                    r6 = 0;
                L_0x008b:
                    r9 = org.telegram.ui.PhonebookShareActivity.this;
                    r9 = r9.other;
                    r9 = r9.size();
                    if (r5 >= r9) goto L_0x037d;
                L_0x0097:
                    r9 = org.telegram.ui.PhonebookShareActivity.this;
                    r9 = r9.other;
                    r9 = r9.get(r5);
                    r9 = (org.telegram.messenger.AndroidUtilities.VcardItem) r9;
                    r10 = r9.type;
                    if (r10 != r3) goto L_0x00ca;
                L_0x00a7:
                    r10 = new android.content.ContentValues;
                    r10.<init>();
                    r11 = "vnd.android.cursor.item/email_v2";
                    r10.put(r8, r11);
                    r11 = r9.getValue(r4);
                    r10.put(r7, r11);
                    r9 = r9.getRawType(r4);
                    r1.fillRowWithType(r9, r10);
                    r0.add(r10);
                L_0x00c3:
                    r16 = r2;
                    r17 = r5;
                L_0x00c7:
                    r9 = 0;
                    goto L_0x0375;
                L_0x00ca:
                    r11 = 3;
                    if (r10 != r11) goto L_0x00ea;
                L_0x00cd:
                    r10 = new android.content.ContentValues;
                    r10.<init>();
                    r11 = "vnd.android.cursor.item/website";
                    r10.put(r8, r11);
                    r11 = r9.getValue(r4);
                    r10.put(r7, r11);
                    r9 = r9.getRawType(r4);
                    r1.fillUrlRowWithType(r9, r10);
                    r0.add(r10);
                    goto L_0x00c3;
                L_0x00ea:
                    r12 = 4;
                    if (r10 != r12) goto L_0x0103;
                L_0x00ed:
                    r10 = new android.content.ContentValues;
                    r10.<init>();
                    r11 = "vnd.android.cursor.item/note";
                    r10.put(r8, r11);
                    r9 = r9.getValue(r4);
                    r10.put(r7, r9);
                    r0.add(r10);
                    goto L_0x00c3;
                L_0x0103:
                    r13 = 5;
                    r14 = "data2";
                    if (r10 != r13) goto L_0x0125;
                L_0x0108:
                    r10 = new android.content.ContentValues;
                    r10.<init>();
                    r12 = "vnd.android.cursor.item/contact_event";
                    r10.put(r8, r12);
                    r9 = r9.getValue(r4);
                    r10.put(r7, r9);
                    r9 = java.lang.Integer.valueOf(r11);
                    r10.put(r14, r9);
                    r0.add(r10);
                    goto L_0x00c3;
                L_0x0125:
                    r15 = "data6";
                    r13 = "OTHER";
                    r12 = "WORK";
                    r11 = "data4";
                    r3 = 2;
                    r4 = "data5";
                    if (r10 != r3) goto L_0x01c7;
                L_0x0132:
                    r10 = new android.content.ContentValues;
                    r10.<init>();
                    r3 = "vnd.android.cursor.item/postal-address_v2";
                    r10.put(r8, r3);
                    r3 = r9.getRawValue();
                    r16 = r2;
                    r2 = r3.length;
                    if (r2 <= 0) goto L_0x014f;
                L_0x0146:
                    r17 = r5;
                    r2 = 0;
                    r5 = r3[r2];
                    r10.put(r4, r5);
                    goto L_0x0151;
                L_0x014f:
                    r17 = r5;
                L_0x0151:
                    r2 = r3.length;
                    r4 = 1;
                    if (r2 <= r4) goto L_0x015a;
                L_0x0155:
                    r2 = r3[r4];
                    r10.put(r15, r2);
                L_0x015a:
                    r2 = r3.length;
                    r4 = 2;
                    if (r2 <= r4) goto L_0x0163;
                L_0x015e:
                    r2 = r3[r4];
                    r10.put(r11, r2);
                L_0x0163:
                    r2 = r3.length;
                    r4 = 3;
                    if (r2 <= r4) goto L_0x016e;
                L_0x0167:
                    r2 = r3[r4];
                    r4 = "data7";
                    r10.put(r4, r2);
                L_0x016e:
                    r2 = r3.length;
                    r4 = 4;
                    if (r2 <= r4) goto L_0x0179;
                L_0x0172:
                    r2 = r3[r4];
                    r4 = "data8";
                    r10.put(r4, r2);
                L_0x0179:
                    r2 = r3.length;
                    r4 = 5;
                    if (r2 <= r4) goto L_0x0184;
                L_0x017d:
                    r2 = r3[r4];
                    r4 = "data9";
                    r10.put(r4, r2);
                L_0x0184:
                    r2 = r3.length;
                    r4 = 6;
                    if (r2 <= r4) goto L_0x018f;
                L_0x0188:
                    r2 = r3[r4];
                    r3 = "data10";
                    r10.put(r3, r2);
                L_0x018f:
                    r2 = 0;
                    r3 = r9.getRawType(r2);
                    r2 = "HOME";
                    r2 = r2.equalsIgnoreCase(r3);
                    if (r2 == 0) goto L_0x01a5;
                L_0x019c:
                    r2 = 1;
                    r3 = java.lang.Integer.valueOf(r2);
                    r10.put(r14, r3);
                    goto L_0x01c2;
                L_0x01a5:
                    r2 = r12.equalsIgnoreCase(r3);
                    if (r2 == 0) goto L_0x01b4;
                L_0x01ab:
                    r2 = 2;
                    r2 = java.lang.Integer.valueOf(r2);
                    r10.put(r14, r2);
                    goto L_0x01c2;
                L_0x01b4:
                    r2 = r13.equalsIgnoreCase(r3);
                    if (r2 == 0) goto L_0x01c2;
                L_0x01ba:
                    r2 = 3;
                    r2 = java.lang.Integer.valueOf(r2);
                    r10.put(r14, r2);
                L_0x01c2:
                    r0.add(r10);
                    goto L_0x00c7;
                L_0x01c7:
                    r16 = r2;
                    r17 = r5;
                    r2 = 20;
                    if (r10 != r2) goto L_0x02c9;
                L_0x01cf:
                    r2 = new android.content.ContentValues;
                    r2.<init>();
                    r3 = "vnd.android.cursor.item/im";
                    r2.put(r8, r3);
                    r3 = 1;
                    r5 = r9.getRawType(r3);
                    r3 = 0;
                    r10 = r9.getRawType(r3);
                    r11 = r9.getValue(r3);
                    r2.put(r7, r11);
                    r11 = "AIM";
                    r11 = r11.equalsIgnoreCase(r5);
                    if (r11 == 0) goto L_0x01fd;
                L_0x01f3:
                    r5 = java.lang.Integer.valueOf(r3);
                    r2.put(r4, r5);
                L_0x01fa:
                    r3 = 1;
                    goto L_0x0297;
                L_0x01fd:
                    r3 = "MSN";
                    r3 = r3.equalsIgnoreCase(r5);
                    if (r3 == 0) goto L_0x020e;
                L_0x0205:
                    r3 = 1;
                    r5 = java.lang.Integer.valueOf(r3);
                    r2.put(r4, r5);
                    goto L_0x01fa;
                L_0x020e:
                    r3 = "YAHOO";
                    r3 = r3.equalsIgnoreCase(r5);
                    if (r3 == 0) goto L_0x021f;
                L_0x0216:
                    r3 = 2;
                    r5 = java.lang.Integer.valueOf(r3);
                    r2.put(r4, r5);
                    goto L_0x01fa;
                L_0x021f:
                    r3 = "SKYPE";
                    r3 = r3.equalsIgnoreCase(r5);
                    if (r3 == 0) goto L_0x0230;
                L_0x0227:
                    r3 = 3;
                    r5 = java.lang.Integer.valueOf(r3);
                    r2.put(r4, r5);
                    goto L_0x01fa;
                L_0x0230:
                    r3 = "QQ";
                    r3 = r3.equalsIgnoreCase(r5);
                    if (r3 == 0) goto L_0x0241;
                L_0x0238:
                    r3 = 4;
                    r3 = java.lang.Integer.valueOf(r3);
                    r2.put(r4, r3);
                    goto L_0x01fa;
                L_0x0241:
                    r3 = "GOOGLE-TALK";
                    r3 = r3.equalsIgnoreCase(r5);
                    if (r3 == 0) goto L_0x0252;
                L_0x0249:
                    r3 = 5;
                    r3 = java.lang.Integer.valueOf(r3);
                    r2.put(r4, r3);
                    goto L_0x01fa;
                L_0x0252:
                    r3 = "ICQ";
                    r3 = r3.equalsIgnoreCase(r5);
                    if (r3 == 0) goto L_0x0263;
                L_0x025a:
                    r3 = 6;
                    r3 = java.lang.Integer.valueOf(r3);
                    r2.put(r4, r3);
                    goto L_0x01fa;
                L_0x0263:
                    r3 = "JABBER";
                    r3 = r3.equalsIgnoreCase(r5);
                    if (r3 == 0) goto L_0x0274;
                L_0x026b:
                    r3 = 7;
                    r3 = java.lang.Integer.valueOf(r3);
                    r2.put(r4, r3);
                    goto L_0x01fa;
                L_0x0274:
                    r3 = "NETMEETING";
                    r3 = r3.equalsIgnoreCase(r5);
                    if (r3 == 0) goto L_0x0287;
                L_0x027c:
                    r3 = 8;
                    r3 = java.lang.Integer.valueOf(r3);
                    r2.put(r4, r3);
                    goto L_0x01fa;
                L_0x0287:
                    r3 = -1;
                    r3 = java.lang.Integer.valueOf(r3);
                    r2.put(r4, r3);
                    r3 = 1;
                    r4 = r9.getRawType(r3);
                    r2.put(r15, r4);
                L_0x0297:
                    r4 = "HOME";
                    r4 = r4.equalsIgnoreCase(r10);
                    if (r4 == 0) goto L_0x02a7;
                L_0x029f:
                    r4 = java.lang.Integer.valueOf(r3);
                    r2.put(r14, r4);
                    goto L_0x02c4;
                L_0x02a7:
                    r3 = r12.equalsIgnoreCase(r10);
                    if (r3 == 0) goto L_0x02b6;
                L_0x02ad:
                    r3 = 2;
                    r3 = java.lang.Integer.valueOf(r3);
                    r2.put(r14, r3);
                    goto L_0x02c4;
                L_0x02b6:
                    r3 = r13.equalsIgnoreCase(r10);
                    if (r3 == 0) goto L_0x02c4;
                L_0x02bc:
                    r3 = 3;
                    r3 = java.lang.Integer.valueOf(r3);
                    r2.put(r14, r3);
                L_0x02c4:
                    r0.add(r2);
                    goto L_0x00c7;
                L_0x02c9:
                    r2 = 6;
                    if (r10 != r2) goto L_0x00c7;
                L_0x02cc:
                    if (r6 == 0) goto L_0x02d0;
                L_0x02ce:
                    goto L_0x00c7;
                L_0x02d0:
                    r2 = new android.content.ContentValues;
                    r2.<init>();
                    r3 = "vnd.android.cursor.item/organization";
                    r2.put(r8, r3);
                    r3 = r17;
                L_0x02dd:
                    r5 = org.telegram.ui.PhonebookShareActivity.this;
                    r5 = r5.other;
                    r5 = r5.size();
                    if (r3 >= r5) goto L_0x0370;
                L_0x02e9:
                    r5 = org.telegram.ui.PhonebookShareActivity.this;
                    r5 = r5.other;
                    r5 = r5.get(r3);
                    r5 = (org.telegram.messenger.AndroidUtilities.VcardItem) r5;
                    r6 = r5.type;
                    r9 = 6;
                    if (r6 == r9) goto L_0x02fe;
                L_0x02fa:
                    r5 = 2;
                    r9 = 0;
                    goto L_0x036c;
                L_0x02fe:
                    r6 = 1;
                    r10 = r5.getRawType(r6);
                    r15 = "ORG";
                    r15 = r15.equalsIgnoreCase(r10);
                    if (r15 == 0) goto L_0x0328;
                L_0x030b:
                    r10 = r5.getRawValue();
                    r15 = r10.length;
                    if (r15 != 0) goto L_0x0313;
                L_0x0312:
                    goto L_0x02fa;
                L_0x0313:
                    r15 = r10.length;
                    if (r15 < r6) goto L_0x031c;
                L_0x0316:
                    r15 = 0;
                    r9 = r10[r15];
                    r2.put(r7, r9);
                L_0x031c:
                    r9 = r10.length;
                    r15 = 2;
                    if (r9 < r15) goto L_0x0325;
                L_0x0320:
                    r9 = r10[r6];
                    r2.put(r4, r9);
                L_0x0325:
                    r6 = 1;
                    r9 = 0;
                    goto L_0x034a;
                L_0x0328:
                    r6 = "TITLE";
                    r6 = r6.equalsIgnoreCase(r10);
                    if (r6 == 0) goto L_0x0339;
                L_0x0330:
                    r9 = 0;
                    r6 = r5.getValue(r9);
                    r2.put(r11, r6);
                    goto L_0x0349;
                L_0x0339:
                    r9 = 0;
                    r6 = "ROLE";
                    r6 = r6.equalsIgnoreCase(r10);
                    if (r6 == 0) goto L_0x0349;
                L_0x0342:
                    r6 = r5.getValue(r9);
                    r2.put(r11, r6);
                L_0x0349:
                    r6 = 1;
                L_0x034a:
                    r5 = r5.getRawType(r6);
                    r10 = r12.equalsIgnoreCase(r5);
                    if (r10 == 0) goto L_0x035c;
                L_0x0354:
                    r5 = java.lang.Integer.valueOf(r6);
                    r2.put(r14, r5);
                    goto L_0x036b;
                L_0x035c:
                    r5 = r13.equalsIgnoreCase(r5);
                    if (r5 == 0) goto L_0x036b;
                L_0x0362:
                    r5 = 2;
                    r6 = java.lang.Integer.valueOf(r5);
                    r2.put(r14, r6);
                    goto L_0x036c;
                L_0x036b:
                    r5 = 2;
                L_0x036c:
                    r3 = r3 + 1;
                    goto L_0x02dd;
                L_0x0370:
                    r9 = 0;
                    r0.add(r2);
                    r6 = 1;
                L_0x0375:
                    r5 = r17 + 1;
                    r2 = r16;
                    r3 = 1;
                    r4 = 0;
                    goto L_0x008b;
                L_0x037d:
                    r16 = r2;
                    r2 = "finishActivityOnSaveCompleted";
                    r3 = r16;
                    r4 = 1;
                    r3.putExtra(r2, r4);
                    r2 = "data";
                    r3.putParcelableArrayListExtra(r2, r0);
                    r0 = org.telegram.ui.PhonebookShareActivity.this;	 Catch:{ Exception -> 0x039b }
                    r0 = r0.getParentActivity();	 Catch:{ Exception -> 0x039b }
                    r0.startActivity(r3);	 Catch:{ Exception -> 0x039b }
                    r0 = org.telegram.ui.PhonebookShareActivity.this;	 Catch:{ Exception -> 0x039b }
                    r0.finishFragment();	 Catch:{ Exception -> 0x039b }
                    goto L_0x039f;
                L_0x039b:
                    r0 = move-exception;
                    org.telegram.messenger.FileLog.e(r0);
                L_0x039f:
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PhonebookShareActivity$AnonymousClass5.onClick(android.content.DialogInterface, int):void");
                }
            });
            builder.show();
        }
    }

    public void onResume() {
        super.onResume();
        fixLayout();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    public void setDelegate(PhonebookSelectActivityDelegate phonebookSelectActivityDelegate) {
        this.delegate = phonebookSelectActivityDelegate;
    }

    private void needLayout() {
        int i = 0;
        int currentActionBarHeight = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) recyclerListView.getLayoutParams();
            if (layoutParams.topMargin != currentActionBarHeight) {
                layoutParams.topMargin = currentActionBarHeight;
                this.listView.setLayoutParams(layoutParams);
                this.extraHeightView.setTranslationY((float) currentActionBarHeight);
            }
        }
        if (this.avatarImage != null) {
            float dp = ((float) this.extraHeight) / ((float) AndroidUtilities.dp(88.0f));
            this.extraHeightView.setScaleY(dp);
            this.shadowView.setTranslationY((float) (currentActionBarHeight + this.extraHeight));
            float f = ((18.0f * dp) + 42.0f) / 42.0f;
            this.avatarImage.setScaleX(f);
            this.avatarImage.setScaleY(f);
            if (this.actionBar.getOccupyStatusBar()) {
                i = AndroidUtilities.statusBarHeight;
            }
            float currentActionBarHeight2 = ((float) i) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (dp + 1.0f));
            float f2 = AndroidUtilities.density;
            currentActionBarHeight2 = (currentActionBarHeight2 - (21.0f * f2)) + ((f2 * 27.0f) * dp);
            this.avatarImage.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * dp);
            double d = (double) currentActionBarHeight2;
            this.avatarImage.setTranslationY((float) Math.ceil(d));
            this.nameTextView.setTranslationX((AndroidUtilities.density * -21.0f) * dp);
            this.nameTextView.setTranslationY((((float) Math.floor(d)) - ((float) Math.ceil((double) AndroidUtilities.density))) + ((float) Math.floor((double) ((AndroidUtilities.density * 7.0f) * dp))));
            dp = (dp * 0.12f) + 1.0f;
            this.nameTextView.setScaleX(dp);
            this.nameTextView.setScaleY(dp);
        }
    }

    private void fixLayout() {
        View view = this.fragmentView;
        if (view != null) {
            view.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (PhonebookShareActivity.this.fragmentView != null) {
                        PhonebookShareActivity.this.needLayout();
                        PhonebookShareActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[18];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCheckBoxCell.class}, null, null, null, "windowBackgroundWhite");
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        r1[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r1[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r1[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r1[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r1[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r1[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r1[9] = new ThemeDescription(this.shareTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "passport_authorizeText");
        r1[10] = new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "passport_authorizeBackground");
        r1[11] = new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "passport_authorizeBackgroundSelected");
        r1[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r1[13] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r1[14] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, "checkboxSquareUnchecked");
        r1[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, "checkboxSquareDisabled");
        r1[16] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, "checkboxSquareBackground");
        r1[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckBoxCell.class}, null, null, null, "checkboxSquareCheck");
        return r1;
    }
}
