package com.rideeaze.services.network;

import com.rideeaze.services.network.model.data.AccountInfoDetails;
import com.rideeaze.services.network.model.data.HttpAlertPassengerData;
import com.rideeaze.services.network.model.data.HttpFeecode;
import com.rideeaze.services.network.model.data.HttpVehicle;
import com.rideeaze.services.network.model.request.HttpAlertPassenger;
import com.rideeaze.services.network.model.request.HttpCancelPassengerRequest;
import com.rideeaze.services.network.model.request.HttpDriverTokenRequest;
import com.rideeaze.services.network.model.request.HttpEmailPassengerRequest;
import com.rideeaze.services.network.model.request.HttpEmailReceipt;
import com.rideeaze.services.network.model.request.HttpGetInvoiceRequest;
import com.rideeaze.services.network.model.request.HttpLoginRequest;
import com.rideeaze.services.network.model.request.HttpNewCabDescribeRequest;
import com.rideeaze.services.network.model.request.HttpPickUpRequest;
import com.rideeaze.services.network.model.request.HttpRegisterNewAccountRequest;
import com.rideeaze.services.network.model.request.HttpReplyPickupRequest;
import com.rideeaze.services.network.model.request.HttpReviewPassengerRequest;
import com.rideeaze.services.network.model.request.HttpSendInvoiceToPassengerRequest;
import com.rideeaze.services.network.model.request.HttpSendStatusCabRequest;
import com.rideeaze.services.network.model.request.HttpUpdateDriverschedule;
import com.rideeaze.services.network.model.request.HttpUpdatePositionRequest;
import com.rideeaze.services.network.model.request.HttpUpdateResStatusRequest;
import com.rideeaze.services.network.model.response.HttpAccountDataResponse;
import com.rideeaze.services.network.model.response.HttpDriverScheduleResponse;
import com.rideeaze.services.network.model.response.HttpGetInvoiceData;
import com.rideeaze.services.network.model.response.HttpPendingRequestPickUp;
import com.rideeaze.services.network.model.response.HttpSendInvoiceToPassengerData;
import com.rideeaze.services.network.model.response.HttpSendReplyPkUpData;
import com.rideeaze.services.network.model.response.JsonResponse;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by adventis on 10/15/15.
 */
public interface NetworkApi {

    @POST("/rest-api/LoginDriver")
    Observable<JsonResponse<HttpAccountDataResponse>> loginDriver(@Body HttpLoginRequest httpLoginRequest);

    @POST("/rest-api/CreateDriverAccount")
    Observable<JsonResponse<HttpAccountDataResponse>> createDriverAccount(@Body AccountInfoDetails httpRegisterNewAccountRequest);

    @POST("/api/GetPickupsForDriver")
    Observable<JsonResponse<List<HttpPendingRequestPickUp>>> getPickupsForDriver(@Body HttpPickUpRequest httpPickUpRequest);

    @POST("/api/SendReplyOfPickup")
    Observable<JsonResponse<HttpSendReplyPkUpData>> sendReplyOfPickup(@Body HttpReplyPickupRequest httpReplyPickupRequest);

    @POST("/api/SendReplyOfPickup")
    Call<JsonResponse<HttpSendReplyPkUpData>> sendReplyOfPickupSync(@Body HttpReplyPickupRequest httpReplyPickupRequest);

    @POST("/api/UpdateReservationStatus")
    Observable<JsonResponse<String>> updateReservationStatus(@Body HttpUpdateResStatusRequest httpUpdateResStatusRequest);

    @POST("/api/GetInvoiceDetails")
    Observable<JsonResponse<HttpGetInvoiceData>> getInvoiceDetails(@Body HttpGetInvoiceRequest httpGetInvoiceRequest);

    @POST("/rest-api/RegisterExisting")
    Observable<JsonResponse<String>> updateUserInfo(@Header("Token") String token, @Body AccountInfoDetails httpRegisterNewAccountRequest);

    @GET("/rest-api/GetUserInfo")
    Observable<JsonResponse<AccountInfoDetails>> getUserInfo(@Header("Token") String token);

    @POST("/api/GetFeeCodeList")
    Observable<JsonResponse<List<HttpFeecode>>> getFeeCodeList(@Body HttpDriverTokenRequest httpDriverTokenRequest);

    @POST("/api/SendInvoiceToPassenger")
    Observable<JsonResponse<HttpSendInvoiceToPassengerData>> sendInvoiceToPassenger(@Body HttpSendInvoiceToPassengerRequest httpSendInvoiceToPassengerRequest);

    @POST("/api/EMailReceiptToPassenger")
    Observable<JsonResponse<String>> sendEmailToPassenger(@Body HttpEmailPassengerRequest httpEmailPassengerRequest);

    @POST("/api/GetStatusCab")
    Observable<JsonResponse<String>> getStatusCab(@Body HttpDriverTokenRequest httpDriverTokenRequest);

    @POST("/api/GiveReviewToPassenger")
    Observable<JsonResponse<String>> giveReviewToPassenger(@Body HttpReviewPassengerRequest httpReviewPassengerRequest);

    @POST("/api/SendCancelToPassenger")
    Observable<JsonResponse<String>> sendCancelPassenger(@Body HttpCancelPassengerRequest httpCancelPassengerRequest);

    @POST("/api/EMailReceiptToUnknownPassenger")
    Observable<JsonResponse<String>> sendEmailReceiptPassenger(@Body HttpEmailReceipt httpEmailReceipt);

    @POST("/api/AlertPassenger")
    Observable<JsonResponse<HttpAlertPassengerData>> alertPassenger(@Body HttpAlertPassenger httpAlertPassenger);

    @POST("/api/SearchCompanyVehicles")
    Observable<JsonResponse<List<HttpVehicle>>> searchCompanyVehicles(@Body HttpDriverTokenRequest httpDriverTokenRequest);

    @POST("/api/SendStatusCab")
    Observable<JsonResponse<String>> sendStatusCab(@Body HttpSendStatusCabRequest httpSendStatusCabRequest);

    @POST("/api/SendUpdatedPosition")
    Observable<JsonResponse<String>> sendUpdatedPosition(@Body HttpUpdatePositionRequest httpUpdatePositionRequest);

    @Headers("Content-Type: application/json")
    @POST("/rest-api/UpdateDriverSchedule")
    Observable<JsonResponse<String>> updateDriverSchedule(@Header("Token") String token,@Body HttpUpdateDriverschedule driverShedule);

    @Headers("Content-Type: application/json")
    @POST("/rest-api/GetDriverSchedule")
    Observable<JsonResponse<HttpDriverScheduleResponse>> getDriverSchedule(@Header("Token") String token, @Body HttpDriverTokenRequest httpDriverTokenRequest);


    @POST("/api/NewCabDescribe")
    Observable<JsonResponse<String>> newCabDescribe(@Body HttpNewCabDescribeRequest httpNewCabDescribeRequest);


   /* @FormUrlEncoded
    @POST("api/VerifyCoupon")
    Observable<JsonServerResponse<VerfiyCouponData>> sendVeridyCoupon(@Field("MerchantId") String merchantId, @Field("CouponCode") String couponCode);

    @POST("api/SendPickup")
    Observable<JsonServerResponse<SendPickupData>> sendPickup(@Body SendPickupRequest request);

    @POST("api/SaveFlightDetail")
    Observable<JsonServerResponse<SaveFlightDetailData>> saveFlightDetail(@Body SaveFlightDetailRequest request);

    @POST("api/GetFlightsInfoFromFV")
    Observable<JsonServerResponse<List<SaveFlightDetailRequest>>> getFlightsInfoFromFV(@Body GetFlightsInfoFromFVRequest request);

    @POST("api/SearchDrivers")
    Observable<JsonServerResponse<List<SearchDriversData>>> searchDrivers(@Body SearchDriversRequest request);

    @POST("api/SearchDrivers")
    Observable<JsonServerResponse<List<SearchDriversLaterData>>> searchDriversLater(@Body SearchDriversRequest request);

    @POST("api/SendCancelToDriver")
    Observable<JsonServerResponse<String>> sendCancelToDriver(@Body SendCancelToDriverRequest request);

    @POST("api/UpdatePickupTime")
    Observable<JsonServerResponse<String>> updatePickupTime(@Body UpdatePickupTimeRequest request);

    @FormUrlEncoded
    @POST("api/GetPickupsForPassenger")
    Observable<JsonServerResponse<List<PickUpReservationData>>> getPickupsForPassenger(@Field("PhoneToken") String phoneToken);

    @POST("api/GetPickup")
    Observable<JsonServerResponse<List<PickUpData>>> getPickup(@Body PickUpRequest request);

    @FormUrlEncoded
    @POST("api/GetAirlines")
    Observable<JsonServerResponse<List<AirlineData>>> getAirlines(@Field("search") String request);

    @FormUrlEncoded
    @POST("api/GetFlightViewAirports")
    Observable<JsonServerResponse<List<AirlineData>>> getAirports(@Field("search") String request);

    @POST("api/SendInvoiceToDriver")
    Observable<JsonServerResponse<String>> sendInvoiceToDriver(@Body SendInvoiceToDriverRequest request);

    @POST("api/RegisterPassenger")
    Observable<JsonServerResponse<RegisterPassengerData>> registerPassenger(@Body RegisterPassengerRequest request);

    @POST("api/StorePaymentID")
    Observable<JsonServerResponse<StorePaymentIdData>> storePaymentID(@Body StorePaymentIdRequest request);

    @POST("api/GetInvoiceDetails")
    Observable<JsonServerResponse<GetInvoiceDetailsData>> getInvoiceDetails(@Body GetInvoiceDetailsRequest request);

    @POST("api/GiveReviewToDriver")
    Observable<JsonServerResponse<String>> giveReviewToDriver(@Body GiveReviewToDriverRequest request);

    @POST("api/GetReservationsLocation")
    Observable<JsonServerResponse<List<GetReservationLocationData>>> getReservationsLocation(@Body GetReservationLocationRequest request);

    @POST("api/SetCompanyLink")
    Observable<JsonServerResponse<SetCompanyLinkData>> setCompanyLink(@Body SetCompanyLinkRequest request);

    @POST("api/GetDriverLocation")
    Observable<JsonServerResponse<GetDriverLocationData>> getDriverLocation(@Body DriverTokenRequest request);


    *//*@POST("api/GetDriverLocation")
    Observable<JsonServerResponse<GetDriverLocationData>> getDriverLocation(@Body DriverTokenRequest request);*//*

    @POST("api/GetFlightDetails")
    Observable<JsonServerResponse<GetFlightDetailsData>> getFlightDetails(@Body ReservationIdRequest request);

    @GET("/maps/api/place/autocomplete/json?sensor=false")
    GooglePlacesResponse getNamesOfPlace(@Query(value = "input", encoded = true) String input, @Query(value = "key") String key);

    @GET("/maps/api/place/textsearch/json?sensor=true")
    Observable<GoogleGetLocationByNameApiResponse> getLocationByName(@Query(value = "query", encoded = true) String query, @Query(value = "key") String key);

    @GET("/maps/api/directions/json?sensor=false&mode=driving&alternatives=true")
    Observable<GoogleRoutesResponse> getRoutes(@Query(value = "origin") String origin, @Query(value = "destination") String dstLat);*/

}
