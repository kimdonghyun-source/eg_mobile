package kr.co.ajcc.wms.menu.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.Utils;
import kr.co.ajcc.wms.custom.CommonCompatActivity;
import kr.co.ajcc.wms.menu.main.MainActivity;
import kr.co.ajcc.wms.model.ResultModel;
import kr.co.ajcc.wms.model.UserInfoModel;
import kr.co.ajcc.wms.network.ApiClientService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends CommonCompatActivity {
    Context mContext;

    EditText et_user_id;
    EditText et_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_login);

        mContext = this;

        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLogin();
            }
        });

        TextView tv_version = findViewById(R.id.tv_version);
        tv_version.setText(Utils.appVersionName(mContext));

        et_user_id = findViewById(R.id.et_user_id);
        et_pass = findViewById(R.id.et_pass);
    }

    //원격접수 취소
    private void requestLogin() {
        ApiClientService service = ApiClientService.retrofit.create(ApiClientService.class);

        //proc, id, pass, version
        Call<UserInfoModel> call = service.postLogin("sp_pda_login", et_user_id.getText().toString(), et_pass.getText().toString(), Utils.appVersionName(mContext));

        call.enqueue(new Callback<UserInfoModel>() {
            @Override
            public void onResponse(Call<UserInfoModel> call, Response<UserInfoModel> response) {
                if(response.isSuccessful()){
                    UserInfoModel model = response.body();
                    if (model != null) {
                        if(model.getFlag().equals(ResultModel.SUCCESS)) {
                        }else{
                            Utils.Toast(mContext, model.getMSG());
                        }
                    }
                }else{
                    Utils.LogLine(response.message());
                    Utils.Toast(mContext, response.code()+" : "+response.message());
                }
            }

            @Override
            public void onFailure(Call<UserInfoModel> call, Throwable t) {
                Utils.LogLine(t.getMessage());
                Utils.Toast(mContext, getString(R.string.error_network));
            }
        });
    }
}
