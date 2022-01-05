package kr.co.leeku.wms.network;

import java.util.concurrent.TimeUnit;


import kr.co.leeku.wms.BuildConfig;
import kr.co.leeku.wms.model.OsrDetailModel;
import kr.co.leeku.wms.model.OsrListModel;
import kr.co.leeku.wms.model.RemeltModel;
import kr.co.leeku.wms.model.ResultModel;
import kr.co.leeku.wms.model.ShipCustomListModel;
import kr.co.leeku.wms.model.ShipListModel;
import kr.co.leeku.wms.model.ShipListPcodeModel;
import kr.co.leeku.wms.model.ShipScanModel;
import kr.co.leeku.wms.model.ShipWhListModel;
import kr.co.leeku.wms.model.UserInfoModel;
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
     * 창고리스트
     * @param proc 프로시저
     * @param p_mode
     * @param p_fac_code
     * @param p_plan_date
     * */
    @POST("R2JsonProc.asp")
    Call<ShipWhListModel> WhList(
            @Query("proc") String proc,
            @Query("param1") String p_mode,
            @Query("param2") String p_fac_code,
            @Query("param3") String p_plan_date
    );

    /**
     * 창고리스트
     * @param proc 프로시저
     * @param p_mode
     * @param p_plan_date
     * */
    @POST("R2JsonProc.asp")
    Call<ShipWhListModel> WhList1(
            @Query("proc") String proc,
            @Query("param1") String p_mode,
            @Query("param2") String p_plan_date
    );

    /**
     * 거래처리스트
     * @param proc 프로시저
     * @param p_mode
     * @param p_fac_code
     * @param p_plan_date
     * @param p_wh_code
     * */
    @POST("R2JsonProc.asp")
    Call<ShipCustomListModel> CustomList(
            @Query("proc") String proc,
            @Query("param1") String p_mode,
            @Query("param2") String p_fac_code,
            @Query("param3") String p_plan_date,
            @Query("param4") String p_wh_code
    );

    /**
     * 출하등록 조회
     * @param proc 프로시저
     * @param p_mode
     * @param p_fac_code
     * @param p_plan_date
     * @param p_wh_code
     * @param p_cst_code
     * @param p_deli_place
     * */
    @POST("R2JsonProc.asp")
    Call<ShipListModel> ShipList(
            @Query("proc") String proc,
            @Query("param1") String p_mode,
            @Query("param2") String p_fac_code,
            @Query("param3") String p_plan_date,
            @Query("param4") String p_wh_code,
            @Query("param5") String p_cst_code,
            @Query("param6") String p_deli_place
    );

    /**
     * 출하등록 저장
     * @param proc 프로시저
     * @param p_ship_date
     * @param p_fac_code
     * @param p_ship_no
     * @param p_wh_code
     * @param p_cst_code
     * @param p_deli_place
     * @param p_plt_wgt
     * @param p_lbl_list
     * @param p_user_id
     * */
    @POST("R2JsonProc.asp")
    Call<ShipListModel> ShipSave(
            @Query("proc") String proc,
            @Query("param1") String p_ship_date,
            @Query("param2") String p_fac_code,
            @Query("param3") String p_ship_no,
            @Query("param4") String p_wh_code,
            @Query("param5") String p_cst_code,
            @Query("param6") String p_deli_place,
            @Query("param7") String p_plt_wgt,
            @Query("param8") String p_lbl_list,
            @Query("param9") String p_user_id
    );

    /**
     * 출하등록 바코드 스캔(P코드미포함)
     * @param proc 프로시저
     * @param p_mode
     * @param p_fac_code
     * @param p_plan_date
     * @param p_wh_code
     * @param p_cst_code
     * @param p_deli_place
     * @param p_pack_no
     * @param p_lbl_id
     * */
    @POST("R2JsonProc.asp")
    Call<ShipScanModel> ShipBarcodeScan(
            @Query("proc") String proc,
            @Query("param1") String p_mode,
            @Query("param2") String p_fac_code,
            @Query("param3") String p_plan_date,
            @Query("param4") String p_wh_code,
            @Query("param5") String p_cst_code,
            @Query("param6") String p_deli_place,
            @Query("param7") String p_pack_no,
            @Query("param8") String p_lbl_id
    );

    /**
     * 출하등록 바코드 스캔(P코드포함)
     * @param proc 프로시저
     * @param p_mode
     * @param p_fac_code
     * @param p_plan_date
     * @param p_wh_code
     * @param p_cst_code
     * @param p_deli_place
     * @param p_pack_no
     * @param p_lbl_id
     * */
    @POST("R2JsonProc.asp")
    Call<ShipListPcodeModel> ShipBarcodeScanPcode(
            @Query("proc") String proc,
            @Query("param1") String p_mode,
            @Query("param2") String p_fac_code,
            @Query("param3") String p_plan_date,
            @Query("param4") String p_wh_code,
            @Query("param5") String p_cst_code,
            @Query("param6") String p_deli_place,
            @Query("param7") String p_pack_no,
            @Query("param8") String p_lbl_id
    );

    /**
     * 출하등록저장
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_ship_add.asp")
    Call<ResultModel> postShipSave(
            @Body RequestBody body
    );

    /**
     * 외주출고의뢰 조회
     * @param proc 프로시저
     * @param p_mode 구분
     * @param m_date 일자
     * */
    @POST("R2JsonProc.asp")
    Call<OsrListModel> OsrList(
            @Query("proc") String proc,
            @Query("param1") String p_mode,
            @Query("param2") String m_date
    );

    /**
     * 외주출고의뢰 바코드스캔
     * @param proc 프로시저
     * @param p_mode 구분
     * @param m_date 일자
     * @param wh_code 창고코드
     * @param ood_no 전표번호
     * @param barcode 바코드스캔
     * */
    @POST("R2JsonProc.asp")
    Call<OsrDetailModel> OsrDetailList(
            @Query("proc") String proc,
            @Query("param1") String p_mode,
            @Query("param2") String m_date,
            @Query("param3") String wh_code,
            @Query("param4") String ood_no,
            @Query("param5") String barcode
    );

    /**
     * 외주출고 저장
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_osr_add.asp")
    Call<ResultModel> postOsrSave(
            @Body RequestBody body
    );

    /**
     * 제품재용해등록 바코드스캔
     * @param proc 프로시저
     * @param gubun 구분
     * @param m_date 일자
     * @param barcode 바코드
     * */
    @POST("R2JsonProc.asp")
    Call<RemeltModel> RemeltList(
            @Query("proc") String proc,
            @Query("param1") String gubun,
            @Query("param2") String m_date,
            @Query("param3") String barcode
    );

    /**
     * 제품재용해 저장
     * */
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("R2JsonProc_remelt_add.asp")
    Call<ResultModel> postRemeltSave(
            @Body RequestBody body
    );

    //R2JsonProc_plt_mrg_save.asp

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
