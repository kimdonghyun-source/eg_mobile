package kr.co.ajcc.wms.network;

import java.util.concurrent.TimeUnit;

import kr.co.ajcc.wms.BuildConfig;
import kr.co.ajcc.wms.model.CustomerInfoModel;
import kr.co.ajcc.wms.model.DeliveryOrderModel;
import kr.co.ajcc.wms.model.LocationModel;
import kr.co.ajcc.wms.model.LotItemsModel;
import kr.co.ajcc.wms.model.MaterialOutDetailModel;
import kr.co.ajcc.wms.model.MaterialOutListModel;
import kr.co.ajcc.wms.model.PalletSnanModel;
import kr.co.ajcc.wms.model.ResultModel;
import kr.co.ajcc.wms.model.UserInfoModel;
import kr.co.ajcc.wms.model.WarehouseModel;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiClientService {
    /**
     * 로그인
     * @param proc  프로시져
     * @param user_id 아이디
     * @param pass 비밀번호
     * @param version 앱버전
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<UserInfoModel> postLogin(
            @Query("proc") String proc,
            @Query("param1") String user_id,
            @Query("param2") String pass,
            @Query("param3") String version
    );

    /**
     * 창고 리스트
     * @param proc
     * @param type 창고타입(M : 원자재, P : 완제품)
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<WarehouseModel> postWarehouse(
            @Query("proc") String proc,
            @Query("param1") String type
    );

    /**
     * 로케이션 리스트
     * @param proc
     * @param code 창고코드
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<LocationModel> postLocation(
            @Query("proc") String proc,
            @Query("param1") String code
    );

    /**
     * 로케이션 리스트
     * @param proc
     * @param code 창고코드
     * @param type 창고타입(M : 원자재, P : 완제품)
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<LocationModel> postScanLocation(
            @Query("proc") String proc,
            @Query("param1") String code,
            @Query("param2") String type
    );

    /**
     * 창고와 로케이션 정보 조회
     * @param proc 프로시져
     * @param code 로케이션 코드
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<LocationModel> postWarehouseLocation(
            @Query("proc") String proc,
            @Query("param1") String code
    );

    /**
     * 로트 재고정보
     * @param proc 프로시져
     * @param code 로케이션코드
     * @param lot 로트번호
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<LotItemsModel> postLotItems(
            @Query("proc") String proc,
            @Query("param1") String code,
            @Query("param2") String lot
    );

    //로케이션 이동
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_mat_etc_save.asp")
    Call<ResultModel> postSendLocation(
            @Body RequestBody body
    );

    /**
     * 팔레트 바코드 스캔(시리얼번호)
     * @param proc 프로시져
     * @param barcode 스캔 바코드
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<PalletSnanModel> postScanPallet(
            @Query("proc") String proc,
            @Query("param1") String barcode
    );

    //제품입고 저장
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_wms_sin_save.asp")
    Call<ResultModel> postSendProductionIn(
            @Body RequestBody body
    );

    /**
     * 자재불출 지시조회
     * @param proc 프로시져
     * @param param1 불출지시 일자
     * @param param2 불출창고 코드
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<MaterialOutListModel> postOutOrderList(
            @Query("proc") String proc,
            @Query("param1") String param1,
            @Query("param2") String param2
    );

    /**
     * 자재불출 지시 상세
     * @param proc 프로시져
     * @param param1 자재불출번호
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<MaterialOutDetailModel> postOutOrderDetail(
            @Query("proc") String proc,
            @Query("param1") String param1
    );

    /**
     * 출고지시서 상세
     * @param proc 프로시져
     * @param param1 출고지시번호
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<CustomerInfoModel> postShipReqList(
            @Query("proc") String proc,
            @Query("param1") String param1,
            @Query("param2") String param2
    );

    /**
     * 출고지시서 상세
     * @param proc 프로시져
     * @param param1 출고지시번호
     * @return
     */
    @POST("R2JsonProc.asp")
    Call<DeliveryOrderModel> postShipReqDetail(
            @Query("proc") String proc,
            @Query("param1") String param1
    );
    //로그 찍기
    //태그 OkHttp 입력(adb logcat OkHttp:D *:S)
    // HttpLoggingInterceptor.Level.BODY  모든 바디 로그 온
    // HttpLoggingInterceptor.Level.NONE  로그 오프
    public static final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);

    //타임아웃 1분
    public static final OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(interceptor);

    //Gson으로 리턴
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.API_SERVER)
            .addConverterFactory(GsonConverterFactory.create())
            .client(builder.build())
            .build();

    //String으로 리턴
    public static final Retrofit retrofitString = new Retrofit.Builder()
            .baseUrl(BuildConfig.API_SERVER)
            .addConverterFactory(new ToStringConverterFactory())
            .client(builder.build())
            .build();
}
