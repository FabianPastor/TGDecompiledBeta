package com.google.firebase.analytics;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.annotation.Size;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.measurement.AppMeasurement;
import com.google.android.gms.measurement.internal.zzx;

public final class FirebaseAnalytics
{
  private final zzx anq;
  
  public FirebaseAnalytics(zzx paramzzx)
  {
    zzac.zzy(paramzzx);
    this.anq = paramzzx;
  }
  
  @RequiresPermission(allOf={"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE", "android.permission.WAKE_LOCK"})
  public static FirebaseAnalytics getInstance(Context paramContext)
  {
    return zzx.zzdt(paramContext).zzbwz();
  }
  
  public void logEvent(@NonNull @Size(max=32L, min=1L) String paramString, Bundle paramBundle)
  {
    this.anq.zzbwy().logEvent(paramString, paramBundle);
  }
  
  public void setAnalyticsCollectionEnabled(boolean paramBoolean)
  {
    this.anq.zzbwy().setMeasurementEnabled(paramBoolean);
  }
  
  public void setMinimumSessionDuration(long paramLong)
  {
    this.anq.zzbwy().setMinimumSessionDuration(paramLong);
  }
  
  public void setSessionTimeoutDuration(long paramLong)
  {
    this.anq.zzbwy().setSessionTimeoutDuration(paramLong);
  }
  
  public void setUserId(String paramString)
  {
    this.anq.zzbwy().setUserId(paramString);
  }
  
  public void setUserProperty(@NonNull @Size(max=24L, min=1L) String paramString1, @Nullable @Size(max=36L) String paramString2)
  {
    this.anq.zzbwy().setUserProperty(paramString1, paramString2);
  }
  
  public static class Event
  {
    public static final String ADD_PAYMENT_INFO = "add_payment_info";
    public static final String ADD_TO_CART = "add_to_cart";
    public static final String ADD_TO_WISHLIST = "add_to_wishlist";
    public static final String APP_OPEN = "app_open";
    public static final String BEGIN_CHECKOUT = "begin_checkout";
    public static final String EARN_VIRTUAL_CURRENCY = "earn_virtual_currency";
    public static final String ECOMMERCE_PURCHASE = "ecommerce_purchase";
    public static final String GENERATE_LEAD = "generate_lead";
    public static final String JOIN_GROUP = "join_group";
    public static final String LEVEL_UP = "level_up";
    public static final String LOGIN = "login";
    public static final String POST_SCORE = "post_score";
    public static final String PRESENT_OFFER = "present_offer";
    public static final String PURCHASE_REFUND = "purchase_refund";
    public static final String SEARCH = "search";
    public static final String SELECT_CONTENT = "select_content";
    public static final String SHARE = "share";
    public static final String SIGN_UP = "sign_up";
    public static final String SPEND_VIRTUAL_CURRENCY = "spend_virtual_currency";
    public static final String TUTORIAL_BEGIN = "tutorial_begin";
    public static final String TUTORIAL_COMPLETE = "tutorial_complete";
    public static final String UNLOCK_ACHIEVEMENT = "unlock_achievement";
    public static final String VIEW_ITEM = "view_item";
    public static final String VIEW_ITEM_LIST = "view_item_list";
    public static final String VIEW_SEARCH_RESULTS = "view_search_results";
  }
  
  public static class Param
  {
    public static final String ACHIEVEMENT_ID = "achievement_id";
    public static final String CHARACTER = "character";
    public static final String CONTENT_TYPE = "content_type";
    public static final String COUPON = "coupon";
    public static final String CURRENCY = "currency";
    public static final String DESTINATION = "destination";
    public static final String END_DATE = "end_date";
    public static final String FLIGHT_NUMBER = "flight_number";
    public static final String GROUP_ID = "group_id";
    public static final String ITEM_CATEGORY = "item_category";
    public static final String ITEM_ID = "item_id";
    public static final String ITEM_LOCATION_ID = "item_location_id";
    public static final String ITEM_NAME = "item_name";
    public static final String LEVEL = "level";
    public static final String LOCATION = "location";
    public static final String NUMBER_OF_NIGHTS = "number_of_nights";
    public static final String NUMBER_OF_PASSENGERS = "number_of_passengers";
    public static final String NUMBER_OF_ROOMS = "number_of_rooms";
    public static final String ORIGIN = "origin";
    public static final String PRICE = "price";
    public static final String QUANTITY = "quantity";
    public static final String SCORE = "score";
    public static final String SEARCH_TERM = "search_term";
    public static final String SHIPPING = "shipping";
    public static final String SIGN_UP_METHOD = "sign_up_method";
    public static final String START_DATE = "start_date";
    public static final String TAX = "tax";
    public static final String TRANSACTION_ID = "transaction_id";
    public static final String TRAVEL_CLASS = "travel_class";
    public static final String VALUE = "value";
    public static final String VIRTUAL_CURRENCY_NAME = "virtual_currency_name";
  }
  
  public static class UserProperty
  {
    public static final String SIGN_UP_METHOD = "sign_up_method";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/analytics/FirebaseAnalytics.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */