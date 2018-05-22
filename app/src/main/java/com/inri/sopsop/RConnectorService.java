package com.inri.sopsop;

import com.inri.sopsop.model.Result;
import com.inri.sopsop.model.StatisticsResult;
import com.inri.sopsop.model.UserResult;

import java.util.ArrayList;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public interface RConnectorService {
    //String API_ENDPOINT = "http://127.0.0.1:3000";
    //String API_ENDPOINT = "http://10.0.3.2:3000";
    String API_ENDPOINT = "https://firrael.herokuapp.com";

    @FormUrlEncoded
    @POST("/user/login")
    Observable<UserResult> login(@Field("login") String login, @Field("password") String password);

    @FormUrlEncoded
    @POST("/user/startup_login")
    Observable<UserResult> startupLogin(@Field("login") String login, @Field("token") String token);

    @FormUrlEncoded
    @POST("/user")
    Observable<UserResult> createAccount(@Field("login") String login, @Field("password") String password, @Field("email") String email, @Field("age") int age, @Field("time") int time);

    @FormUrlEncoded
    @POST("/user/fcm_token")
    Observable<Result> sendFCMToken(@Field("user_id") long userId, @Field("fcm_token") String fcmToken);

    @FormUrlEncoded
    @POST("/user/update")
    Observable<UserResult> updateInfo(@Field("user_id") long userId, @Field("email") String email, @Field("age") int age, @Field("time") int time);

    @FormUrlEncoded
    @POST("/user/results_reaction")
    Observable<Result> sendReactionResults(@Field("user_id") long userId, @Field("times[]") ArrayList<Double> times);

    @FormUrlEncoded
    @POST("/user/results_complex")
    Observable<Result> sendComplexMotorReactionResults(@Field("user_id") long userId, @Field("wins") long wins, @Field("fails") long fails, @Field("misses") long misses);

    @FormUrlEncoded
    @POST("/user/results_stability")
    Observable<Result> sendAttentionStabilityResults(@Field("user_id") long userId, @Field("times[]") ArrayList<Double> times, @Field("misses") long misses, @Field("errors_value") long errors);

    @FormUrlEncoded
    @POST("/user/results_focusing")
    Observable<Result> sendFocusingResults(@Field("user_id") long userId, @Field("times[]") ArrayList<Double> times, @Field("error_values[]") ArrayList<Long> errors);

    @FormUrlEncoded
    @POST("/user/results_volume")
    Observable<Result> sendAttentionDistributionResults(@Field("user_id") long userId, @Field("wins") long wins, @Field("fails") long fails, @Field("misses") long misses);

    @FormUrlEncoded
    @POST("/user/results_attention_volume")
    Observable<Result> sendAttentionVolumeResults(@Field("user_id") long userId, @Field("time") Double time, @Field("wins") long wins);

    @FormUrlEncoded
    @POST("/user/statistics")
    Observable<StatisticsResult> fetchStatistics(@Field("user_id") long userId);
}