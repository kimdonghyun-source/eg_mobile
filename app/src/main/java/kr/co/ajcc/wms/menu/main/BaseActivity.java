package kr.co.ajcc.wms.menu.main;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.common.Define;
import kr.co.ajcc.wms.custom.CommonCompatActivity;
import kr.co.ajcc.wms.custom.CommonFragment;
import kr.co.ajcc.wms.menu.config.ConfigFragment;
import kr.co.ajcc.wms.menu.location.LocationFragment;
import kr.co.ajcc.wms.menu.material_out.MaterialOutFragment;
import kr.co.ajcc.wms.menu.pallet.PalletFragment;
import kr.co.ajcc.wms.menu.product_out.ProductOutFragment;
import kr.co.ajcc.wms.menu.production_in.ProductionInFragment;
import kr.co.ajcc.wms.menu.registration.RegistrationFragment;

public class BaseActivity extends CommonCompatActivity {
    Context mContext;

    //사이드 메뉴 레이아웃
    DrawerLayout drawer;
    View mMenuView;
    //사이드 메뉴 리스트
    //DrawerMenu mDrawerMenu;

    ImageView iv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_base);

        mContext = BaseActivity.this;

        findViewById(R.id.bt_back).setOnClickListener(onClickListener);

        iv_title = findViewById(R.id.iv_title);

        int menu = getIntent().getIntExtra("menu", 0);

        switch (menu){
            case Define.MENU_REGISTRATION: {
                CommonFragment fragment = new RegistrationFragment();
//                Bundle args = new Bundle();
//                args.putSerializable(Define.ARG_INFO, mainTabList);
//                fragment.setArguments(args);
                replaceContent(fragment, Define.TAG_REGISTRATION, R.id.fl_content);
                break;
            }
            case Define.MENU_LOCATION: {
                CommonFragment fragment = new LocationFragment();
                replaceContent(fragment, Define.TAG_LOCATION, R.id.fl_content);
                break;
            }
            case Define.MENU_MATERIAL_OUT: {
                CommonFragment fragment = new MaterialOutFragment();
                replaceContent(fragment, Define.TAG_MATERIAL_OUT, R.id.fl_content);
                break;
            }
            case Define.MENU_PRODUCTION_IN: {
                CommonFragment fragment = new ProductionInFragment();
                replaceContent(fragment, Define.TAG_MATERIAL_OUT, R.id.fl_content);
                break;
            }
            case Define.MENU_PRODUCT_OUT: {
                CommonFragment fragment = new ProductOutFragment();
                replaceContent(fragment, Define.TAG_PRODUCTION_IN, R.id.fl_content);
                break;
            }
            case Define.MENU_PALLET: {
                CommonFragment fragment = new PalletFragment();
                replaceContent(fragment, Define.TAG_PALLET, R.id.fl_content);
                break;
            }
            case Define.MENU_CONFIG: {
                CommonFragment fragment = new ConfigFragment();
                replaceContent(fragment, Define.TAG_CONFIG, R.id.fl_content);
                break;
            }
        }

        setTitleImage(menu);
    }

    private void setTitleImage(int menu){
        int image = 0;
        switch (menu){
            case Define.MENU_REGISTRATION: {
                image = R.drawable.menu_inhouse_title;
                break;
            }
            case Define.MENU_LOCATION: {
                image = R.drawable.menu_moveloc_title;
                break;
            }
            case Define.MENU_MATERIAL_OUT: {
                image = R.drawable.menu_release_title;
                break;
            }
            case Define.MENU_PRODUCTION_IN: {
                image = R.drawable.menu_inproduct_title;
                break;
            }
            case Define.MENU_PRODUCT_OUT: {
                image = R.drawable.menu_outproduct_title;
                break;
            }
            case Define.MENU_PALLET: {
                image = R.drawable.pallet_title;
                break;
            }
            case Define.MENU_CONFIG: {
                image = R.drawable.menu_setting_title;
                break;
            }
        }
        iv_title.setBackgroundResource(image);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int view = v.getId();
            switch (view) {
                case R.id.bt_back:
                    backPressed();
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        backPressed();
    }

    private void backPressed(){
        try {
            //사이드 메뉴가 열려있으면 닫아준다.
            /*if (mDrawerLayout.isDrawerOpen(mMenuView)) {
                mDrawerLayout.closeDrawers();
                return;
            }*/

            FragmentManager mFragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
            int count = mFragmentManager.getBackStackEntryCount();

            if (count >= 1) {
                mFragmentManager.popBackStack();
            } else {
                finish();
            }
        }catch (Exception e){
            finish();
        }
    }
}
