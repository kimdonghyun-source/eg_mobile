package kr.co.ajcc.wms.custom;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import kr.co.ajcc.wms.Utils;

public class CommonCompatActivity extends AppCompatActivity {
    LoadingDialog sLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(sLoadingDialog != null && !sLoadingDialog.isShowing() || sLoadingDialog == null) {
            sLoadingDialog = LoadingDialog.create(CommonCompatActivity.this, null, null);
        }
    }

    public void showLoadingBar(Context context){

        if(context != null && sLoadingDialog != null && !sLoadingDialog.isShowing()) {
            try{
                sLoadingDialog.show();
            }catch(Exception e){
                Utils.LogLine(e.getMessage());
            }
        }
    }

    public void dismissLoadingBar(Context context){
        if(context != null && sLoadingDialog != null && sLoadingDialog.isShowing()) {
            try{
                sLoadingDialog.dismiss();
            }catch(Exception e){
                Utils.LogLine(e.getMessage());
            }
        }
    }

    public Fragment getContent(String tag){
        FragmentManager fm = getSupportFragmentManager();
        Fragment BaseFragment = fm.findFragmentByTag(tag);
        return BaseFragment;
    }

    public void replaceContent(Fragment content, String tag, int resContent){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        clearAllContent();
        ft.replace(resContent, content, tag);
        ft.commitAllowingStateLoss();
    }

    public void removeContent(Fragment content){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        clearAllContent();
        ft.remove(content);
        ft.commitAllowingStateLoss();
    }

    //기존 fragment는 유지하고 새로운 fragment를 추가
    public void addContent(CommonFragment content, String tag, int layout_id){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        CommonFragment basecontent = (CommonFragment) getContent(tag);

        if(basecontent == null){
            ft.add(layout_id, content, tag);
            ft.addToBackStack(tag);
        } else {
            for(int i = 0;i < fm.getBackStackEntryCount(); i++){
                fm.popBackStack();
            }
            ft.add(layout_id, content, tag);
            ft.addToBackStack(tag);
        }
        ft.commitAllowingStateLoss();
    }

    //기존 fragment와 새로운 fragment를 교체
    public void changeContent(CommonFragment content, String tag, int layout_id){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        CommonFragment basecontent = (CommonFragment) getContent(tag);

        if(fm.getBackStackEntryCount() > 0){
            fm.popBackStack();
        }
        ft.add(layout_id, content, tag);
        ft.addToBackStack(tag);
        ft.commitAllowingStateLoss();
    }

    public void clearAllContent(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        for(int i = 0; i < fm.getBackStackEntryCount(); i++){
            String backTag = fm.getBackStackEntryAt(i).getName();
            fm.popBackStack(backTag,FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        ft.commitAllowingStateLoss();
    }
}
