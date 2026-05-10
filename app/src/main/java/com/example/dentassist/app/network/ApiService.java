package com.example.dentassist.app.network;

import android.media.MediaCodec;
import com.example.dentassist.models.QueueRequestModel;
import com.example.dentassist.models.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @POST("api/mobile/login.php")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/mobile/register.php")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @POST("api/mobile/forgot_password.php")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("api/mobile/dashboard.php")
    Call<DashboardResponse> getDashboardData(@Header("Authorization") String token);

    @POST("api/mobile/profile.php")
    Call<ProfileResponse> getProfile(@Header("Authorization") String token);

    @POST("api/mobile/profile_update.php")
    Call<GenericResponse> updateProfile(@Header("Authorization") String token, @Body ProfileUpdateRequest request);

    @POST("api/mobile/change_password.php")
    Call<GenericResponse> changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);

    @POST("api/mobile/bills.php")
    Call<BillsResponse> getBills(@Header("Authorization") String token);

    @POST("api/mobile/referrals.php")
    Call<ReferralsResponse> getReferrals(@Header("Authorization") String token);

    @POST("api/mobile/subscription.php")
    Call<SubscriptionResponse> getSubscription(@Header("Authorization") String token);

    @POST("api/mobile/subscription.php")
    Call<GenericResponse> subscribe(@Header("Authorization") String token, @Body SubscribeRequest request);

    @GET("api/mobile/booking_calendar.php")
    Call<BookingCalendarResponse> getBookingCalendar(
            @Header("Authorization") String token,
            @Query("doctor_id") int doctorId,
            @Query("week") int weekOffset
    );

    @POST("api/mobile/book_appointment.php")
    Call<GenericResponse> bookAppointment(
            @Header("Authorization") String token,
            @Body BookAppointmentRequest request
    );

    @POST("api/mobile/queue_request.php")
    Call<GenericResponse> submitQueueRequest(@Header("Authorization") String token, @Body QueueRequestModel request);

    @POST("api/mobile/cancel_request.php")
    Call<GenericResponse> cancelRequest(@Header("Authorization") String token, @Body CancelRequest request);

    @POST("api/mobile/owo_payment_info.php")
    Call<OwoPaymentInfoResponse> getOwoPaymentInfo(@Header("Authorization") String token, @Body OwoInfoRequest request);

    @POST("api/mobile/confirm_payment.php")
    Call<GenericResponse> confirmOwoPayment(@Header("Authorization") String token, @Body PaymentConfirmRequest request);

    @POST("api/mobile/points.php")
    Call<PointsResponse> getPoints(@Header("Authorization") String token);

    @GET("api/mobile/invoice_detail.php")
    Call<InvoiceDetailResponse> getInvoiceDetail(@Header("Authorization") String token, @Query("invoice_id") int invoiceId);

    @POST("api/mobile/tooth_chart.php")
    Call<ToothChartResponse> getToothChart(@Header("Authorization") String token);



}