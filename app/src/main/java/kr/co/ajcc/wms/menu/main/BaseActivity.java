package kr.co.ajcc.wms.menu.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import kr.co.ajcc.wms.GlobalApplication;
import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.common.Define;
import kr.co.ajcc.wms.common.Utils;
import kr.co.ajcc.wms.custom.CommonCompatActivity;
import kr.co.ajcc.wms.custom.CommonFragment;
import kr.co.ajcc.wms.menu.config.ConfigFragment;
import kr.co.ajcc.wms.menu.location.LocationFragment;
import kr.co.ajcc.wms.menu.material_out.MaterialOutFragment;
import kr.co.ajcc.wms.menu.material_out.MaterialPickingFragment;
import kr.co.ajcc.wms.menu.pallet.PalletFragment;
import kr.co.ajcc.wms.menu.popup.TwoBtnPopup;
import kr.co.ajcc.wms.menu.product_out.ProductOutFragment;
import kr.co.ajcc.wms.menu.product_out.ProductPickingFragment;
import kr.co.ajcc.wms.menu.production_in.ProductionInFragment;
import kr.co.ajcc.wms.menu.registration.RegistrationFragment;
import kr.co.ajcc.wms.model.UserInfoModel;

public class BaseActivity extends CommonCompatActivity {
    Context mContext;

    //사이드 메뉴 레이아웃
    DrawerLayout drawer;
    View drawer_layout;
    //사이드 메뉴 리스트
    //DrawerMenu mDrawerMenu;
    //좌측 메뉴 리스트 아답터
    ListAdapter mAdapter;
    //다른 메뉴 이동시 묻는 팝업
    TwoBtnPopup mTwoBtnPopup;
    //메뉴 타이틀
    ImageView iv_title;
    //선택된 메뉴 postion
    int mSelectMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_base);

        mContext = BaseActivity.this;

        findViewById(R.id.bt_back).setOnClickListener(onClickListener);

        drawer = findViewById(R.id.drawer);
        drawer_layout = findViewById(R.id.drawer_layout);
        findViewById(R.id.bt_drawer).setOnClickListener(onClickListener);
        findViewById(R.id.bt_close).setOnClickListener(onClickListener);

        ArrayList<String> list = new ArrayList<>();
        list.add("로케이션 이동");
        list.add("자재 불출");
        list.add("생산 입고");
        list.add("제품출고");
        list.add("Pallet 관리");
        list.add("프린터 설정");

        ListView listView = findViewById(R.id.list);
        mAdapter = new ListAdapter();
        mAdapter.setData(list);
        listView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        GlobalApplication application = (GlobalApplication)getApplicationContext();
        UserInfoModel.Items model = application.getUserInfoModel();

        TextView tv_name = findViewById(R.id.tv_name);
        tv_name.setText(model.getDpt_name()+" "+model.getEmp_name());

        iv_title = findViewById(R.id.iv_title);

        int menu = getIntent().getIntExtra("menu", 0);

        Bundle args = getIntent().getBundleExtra("args");

        mSelectMenu = menu-2;

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
                replaceContent(fragment, Define.TAG_PRODUCTION_IN, R.id.fl_content);
                break;
            }
            case Define.MENU_PRODUCT_OUT: {
                CommonFragment fragment = new ProductOutFragment();
                replaceContent(fragment, Define.TAG_PRODUCT_OUT, R.id.fl_content);
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
            case Define.MENU_PRODUCT_PICKING: {
                CommonFragment fragment = new ProductPickingFragment();
                fragment.setArguments(args);
                replaceContent(fragment, Define.TAG_PRODUCT_PICKING, R.id.fl_content);
                break;
            }
            case Define.MENU_MATERIAL_PICKING: {
                CommonFragment fragment = new MaterialPickingFragment();
                fragment.setArguments(args);
                replaceContent(fragment, Define.TAG_MATERIAL_PICKING, R.id.fl_content);
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
            case Define.MENU_PRODUCT_PICKING: {
                image = R.drawable.prod_picking_title;
                break;
            }
            case Define.MENU_MATERIAL_PICKING: {
                image = R.drawable.menu_release_title;
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
                case R.id.bt_drawer:
                    drawer.openDrawer(drawer_layout);
                    break;
                case R.id.bt_close:
                    drawer.closeDrawers();
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
            if (drawer.isDrawerOpen(drawer_layout)) {
                drawer.closeDrawers();
                return;
            }

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

    class ListAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        ArrayList<String> mList;

        public void setData(ArrayList<String> list){
            mList = list;
        }

        public ListAdapter() {
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            if (mList == null) {
                return 0;
            }

            return mList.size();
        }

        @Override
        public String getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                holder = new ViewHolder();
                v = mInflater.inflate(R.layout.cell_drawer, null);
                v.setTag(holder);

                holder.tv_menu = v.findViewById(R.id.tv_menu);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            if(mSelectMenu == position){
                holder.tv_menu.setSelected(true);
            }else{
                holder.tv_menu.setSelected(false);
            }

            final String menu = mList.get(position);
            holder.tv_menu.setText((position+1)+". "+menu);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSelectMenu == position){
                        drawer.closeDrawer(drawer_layout);
                        return;
                    }
                    mTwoBtnPopup = new TwoBtnPopup(BaseActivity.this, menu+" 메뉴로 이동하시겠습니까?", R.drawable.popup_title_alert, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                //팝업 닫기
                                mTwoBtnPopup.hideDialog();
                                //좌측메뉴 닫기
                                drawer.closeDrawer(drawer_layout);
                                //선택한 메뉴 기억
                                mSelectMenu = position;
                                //0이 없고 입고등록이 삭제되어 +2 해줘야함
                                switch (position+2){
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
                                setTitleImage(position+2);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            });

            return v;
        }

        class ViewHolder {
            TextView tv_menu;
        }
    }
}
