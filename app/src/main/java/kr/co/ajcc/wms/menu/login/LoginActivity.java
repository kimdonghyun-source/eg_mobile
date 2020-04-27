package kr.co.ajcc.wms.menu.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                if(Utils.isEmpty(et_user_id.getText().toString())) {
                    Utils.Toast(mContext, "아이디를 입력해주세요.");
                    et_user_id.requestFocus();
                    return;
                }
                if(Utils.isEmpty(et_pass.getText().toString())) {
                    Utils.Toast(mContext, "비밀번호를 입력해주세요.");
                    et_pass.requestFocus();
                    return;
                }
            }
        });

        findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tv_version = findViewById(R.id.tv_version);
        tv_version.setText(Utils.appVersionName(mContext));

        et_user_id = findViewById(R.id.et_user_id);
        et_pass = findViewById(R.id.et_pass);

        et_user_id.setText("axlrose");
        et_pass.setText("1234");
    }

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
                        if(model.getFlag() == ResultModel.SUCCESS) {
                            requestLogin();
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
