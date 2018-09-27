package com.kid.brain.provider.request;

import com.kid.brain.provider.request.model.AccountResponse;
import com.kid.brain.provider.request.model.AddKidParams;
import com.kid.brain.provider.request.model.BookingParams;
import com.kid.brain.provider.request.model.CategoryResponse;
import com.kid.brain.provider.request.model.EditProfileParams;
import com.kid.brain.provider.request.model.KidResponse;
import com.kid.brain.provider.request.model.LevelResponse;
import com.kid.brain.provider.request.model.LoginParams;
import com.kid.brain.provider.request.model.PasswordParams;
import com.kid.brain.provider.request.model.QuestionResponse;
import com.kid.brain.provider.request.model.ResultSavingParams;
import com.kid.brain.provider.request.model.history.TestResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIService {

    String BASE_URL = "http://103.101.161.178/";
    String ENDPOINT = "KidsBrain/";

    String SIGN_IN = ENDPOINT + "user/login";
    String SIGN_UP = ENDPOINT + "user/signup";
    String UPDATE_PROFILE = ENDPOINT + "user";
    String FORGOT_PASSWORD = ENDPOINT + "user/forgotPassword";
    String UPDATE_PASSWORD = ENDPOINT + "user";

    String LEVELS = ENDPOINT + "api/listAgeRange";
    String CATEGORIES = ENDPOINT + "api";
    String QUESTIONS = ENDPOINT + "api/categoryDetail";

    String ADD_KID = ENDPOINT + "user";
    String KID_PROFILE = ENDPOINT + "children";
    String UPDATE_KID = ENDPOINT + "children";
    String DELETE_KID = ENDPOINT + "children";
    String SAVE_SEARCH_KID_HISTORY = ENDPOINT + "children";


    String HISTORIES = ENDPOINT + "histories";
    String HISTORY = ENDPOINT + "history";

    String RESULT_SAVING = ENDPOINT + "test";
    String RESULT_QUESTION_HISTORY = ENDPOINT + "test";

    String BOOKING = ENDPOINT + "test";
    String SEARCH_HISTORY_CODE = ENDPOINT + "test";

    /*****************************************
     * Todo: USER
     *****************************************
     * + login: Đăng nhập
     * + signUp: Đăng ký
     * + updateProfile: Cập nhật thông tin tài khoản đăng nhập
     *****************************************/
    @POST(APIService.SIGN_IN)
    Call<AccountResponse> login(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                                @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                                @Body LoginParams account);

    @POST(APIService.SIGN_UP)
    Call<AccountResponse> signUp(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                                 @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                                 @Body LoginParams account);

    @PUT(APIService.UPDATE_PROFILE + "/{user_id}/updateInfo")
    Call<AccountResponse> updateProfile(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                                        @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                                        @Path("user_id") long userId,
                                        @Body EditProfileParams account);
    @POST(APIService.FORGOT_PASSWORD)
    Call<AccountResponse> forgotPassword(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                                      @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                                      @Body RequestBody email);

    @PUT(APIService.UPDATE_PASSWORD + "/{user_id}/updatePassword")
    Call<AccountResponse> updatePassword(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                                      @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                                         @Path("user_id") long userId,
                                      @Body PasswordParams params);
    /*****************************************
     * Todo: KID
     *****************************************
     * + fetchKid: Lấy thông tin chi tiết của trẻ
     * + addKid: Thêm mới trẻ
     * + updateKid: Cập nhật thông tin trẻ
     * + deleteKid: Xoá thông tin trẻ
     *****************************************/
    @GET(APIService.KID_PROFILE + "/{kid_id}")
    Call<KidResponse> fetchKid(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                               @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                               @Path("kid_id") long kidId);

    @POST(APIService.ADD_KID + "/{parent_id}/addChildren")
    Call<KidResponse> addKid(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                             @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                             @Path("parent_id") long parentId,
                             @Body AddKidParams account);

    @PUT(APIService.UPDATE_KID + "/{kid_id}/updateInfo")
    Call<KidResponse> updateKid(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                                @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                                @Path("kid_id") long kidId,
                                @Body AddKidParams account);

    @GET(APIService.DELETE_KID + "/{user_id}/{kid_id}/delete")
    Call<KidResponse> deleteKid(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                                @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                                @Path("user_id") long userId,
                                @Path("kid_id") long kidId);


    /*****************************************
     * Todo: LEVEL and CATEGORIES
     *****************************************
     * + fetchLevels: Lấy danh sách độ tuổi
     * + fetchCategoriesByLevel: Lấy danh mục theo độ tuổi
     *****************************************/
    @GET(APIService.LEVELS)
    Call<LevelResponse> fetchLevels(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                                    @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode);

    @GET(APIService.CATEGORIES + "/{cate_type}/{level_id}")
    Call<CategoryResponse> fetchCategoriesByLevel(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                                                  @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                                                  @Path("cate_type") int cateType,
                                                  @Path("level_id") int levelId);

    /*****************************************
     * Todo: QUESTIONS
     *****************************************
     * + fetchQuestionsByLevelIdCateId: Lấy danh sách câu hỏi theo độ tuổi và danh mục
     *****************************************/
    @GET(APIService.QUESTIONS + "/{level_id}/{cate_id}")
    Call<QuestionResponse> fetchQuestionsByLevelIdCateId(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                                                         @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                                                         @Path("level_id") int levelId,
                                                         @Path("cate_id") int cateId);


    /*****************************************
     * Todo: SEARCH
     *****************************************
     * + booking: Đặt lịch tư vấn
     *****************************************/
    @GET(APIService.SEARCH_HISTORY_CODE + "/{history_id}")
    Call<TestResponse> searchHistoryCode(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                               @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                               @Path("history_id") String historyCode);

    @GET(APIService.SAVE_SEARCH_KID_HISTORY + "/{user_id}/{kid_id}/saveAs")
    Call<KidResponse> saveSearchHistory(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                                         @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                                         @Path("user_id") long userId,
                                         @Path("kid_id") long kidId);

    /*****************************************
     * Todo: BOOKING
     *****************************************
     * + booking: Đặt lịch tư vấn
     *****************************************/
    @POST(APIService.BOOKING + "/{history_id}/makeAppoinment")
    Call<TestResponse> booking(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                               @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                               @Path("history_id") String historyId,
                               @Body BookingParams params);

    @POST(APIService.BOOKING + "/{history_id}/sendResults")
    Call<TestResponse> sendResults(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                                   @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                                   @Path("history_id") String historyId,
                                   @Body RequestBody mailTo);


    /*****************************************
     * Todo: RESULT TESTED
     *****************************************
     * + saveResult: Lưu lịch sử kết quả kiểm tra
     * + fetchHistoryDetail: Lấy thông tin chi tiết lịch sử kết quả kiểm tra
     * + fetchQuestionHistoryDetail: Xem lại các câu hỏi đã trả lời
     *****************************************/
    @POST(APIService.RESULT_SAVING + "/{kid_id}/save")
    Call<TestResponse> saveResult(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                                  @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                                  @Path("kid_id") long kidId,
                                  @Body ResultSavingParams body);

    @GET(APIService.RESULT_SAVING + "/{history_id}")
    Call<TestResponse> fetchHistoryDetail(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                                          @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                                          @Path("history_id") String historyId);

    @GET(APIService.RESULT_QUESTION_HISTORY + "/{history_id}/{category_id}")
    Call<QuestionResponse> fetchQuestionHistoryDetail(@Header(WebserviceConfig.HEADER_CONTENT_TYPE) String contentType,
                                                      @Header(WebserviceConfig.HEADER_ACCEPT_LANGUAGE) String languageCode,
                                                      @Path("history_id") String historyId,
                                                      @Path("category_id") long categoryId);

}
