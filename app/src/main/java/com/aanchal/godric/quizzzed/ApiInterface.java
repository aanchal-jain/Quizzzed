package com.aanchal.godric.quizzzed;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.Part;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("insertUser.jsp")
    Call<User> registerUser(@Field("name") String username,
                            @Field("img") String image);
    @Multipart
    @POST("picUpload.jsp")
    Call<ImageBean> postImg(@Part MultipartBody.Part img);

    @FormUrlEncoded
    @POST("getUserInfo.jsp")
    Call<UserDetailsList> getInfo(@Field("username") String username);


    @FormUrlEncoded
    @POST("updateUser.jsp")
    Call<User> updateUser(@Field("prev_name") String prev_name,
                          @Field("new_name") String new_name,
                          @Field("img") String image);

    @FormUrlEncoded
    @POST("insertCode.jsp")
    Call<User> insertCode(@Field("name") String username,
                          @Field("code") String code);

    @FormUrlEncoded
    @POST("getPlayersList.jsp")
    Call<Players> getPlayers(@Field("username") String username,
                             @Field("game_code") String code);

    @FormUrlEncoded
    @POST("setQuestions.jsp")
    Call<User> setQuestions(@Field("game_code") String game_code,
                            @Field("match_length") int match_length);

    @FormUrlEncoded
    @POST("newConnection.jsp")
    Call<User> makeConnection(@Field("game_code") String game_code,
                              @Field("username") String username);

    @FormUrlEncoded
    @POST("setMatchLength.jsp")
    Call<User> setMatchLength(@Field("game_code") String game_code,
                              @Field("match_length") int match_length);

    @FormUrlEncoded
    @POST("setStatus.jsp")
    Call<User> setStatus(@Field("game_code") String game_code);

    @FormUrlEncoded
    @POST("getMatchLength.jsp")
    Call<Player> getMatchLength(@Field("game_code") String game_code);

    @FormUrlEncoded
    @POST("getQuestions.jsp")
    Call<QuestionsList> getQuestions(@Field("game_code") String game_code);

    @FormUrlEncoded
    @POST("deductCoins.jsp")
    Call<User> deductCoins(@Field("username") String username);

    @FormUrlEncoded
    @POST("setScore.jsp")
    Call<User> sendScore(@Field("username") String username,
                         @Field("score") int score,
                         @Field("game_code") String game_code);
    @FormUrlEncoded
    @POST("fetchStats.jsp")
    Call<Stats> fetchStats(@Field("username") String username);

    @FormUrlEncoded
    @POST("setMatchWon.jsp")
    Call<User> setMatchWon(@Field("username") String username);

    @FormUrlEncoded
    @POST("getScore.jsp")
    Call<Stats> getScore(@Field("username") String username,
                         @Field("game_code") String game_code);

    @FormUrlEncoded
    @POST("getScoreboard.jsp")
    Call<Players> getScoreboard(@Field("username") String username,
                                @Field("game_code") String code);

    @FormUrlEncoded
    @POST("deleteUser.jsp")
    Call<User> deleteUser(@Field("username") String username,
                          @Field("game_code") String code);

    @FormUrlEncoded
    @POST("deleteConnection.jsp")
    Call<User> deleteConnection(@Field("username") String username,
                                @Field("game_code") String game_code);
}
