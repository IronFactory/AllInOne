package com.ironfactory.allinoneenglish.controllers.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ironfactory.allinoneenglish.Global;
import com.ironfactory.allinoneenglish.R;
import com.ironfactory.allinoneenglish.controllers.adapters.TabAdapter;
import com.ironfactory.allinoneenglish.controllers.fragments.LatelyFragment;
import com.ironfactory.allinoneenglish.controllers.fragments.MineFragment;
import com.ironfactory.allinoneenglish.entities.CourseEntity;
import com.ironfactory.allinoneenglish.managers.DBManger;
import com.ironfactory.allinoneenglish.utils.FontUtils;
import com.ironfactory.allinoneenglish.utils.VLCUtils;

import java.io.File;
import java.util.Date;

/**
* TODO : 초기화면 메뉴 선택 탭 액티비티
 * http://blog.naver.com/PostView.nhn?blogId=just4u78&logNo=220630233740&parentCategoryNo=&categoryNo=&viewDate=&isShowPopularPosts=false&from=postView
* */
public class TabActivity extends FragmentActivity implements MineFragment.OnPlayVideo, LatelyFragment.OnPlayVideo {

    private static final String TAG = "TabActivity";
    private static final int HOME = 0;
    private static final int STUDY = 1;
    private static final int MINE = 2;
    private static final int LATELY = 3;
    private static final int SET = 4;

    private TabAdapter tabAdapter;

    private ViewPager viewPager;

    private LinearLayout menuLayout[] = new LinearLayout[5];
    private ImageView menuImage[] = new ImageView[5];
    private TextView menuText[] = new TextView[5];

    private VLCUtils vlcUtils;
    private MaterialDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        init();
    }

    /**
     * TODO : 생성자
     * */
    private void init() {
//        searchByFileFilter(new File(Environment.MEDIA_MOUNTED));
//        searchByFileFilter(new File("/storage"));
        Global.searchAllFile(new File("/storage"), ".abcde");
        checkDB();
        vlcUtils = new VLCUtils(this);

        tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.tab_activity_container);
        viewPager.setAdapter(tabAdapter);

        menuLayout[0] = (LinearLayout) findViewById(R.id.activity_tab_home);
        menuImage[0] = (ImageView) findViewById(R.id.activity_tab_home_image);
        menuText[0] = (TextView) findViewById(R.id.activity_tab_home_text);
        menuLayout[1] = (LinearLayout) findViewById(R.id.activity_tab_study);
        menuImage[1] = (ImageView) findViewById(R.id.activity_tab_study_image);
        menuText[1] = (TextView) findViewById(R.id.activity_tab_study_text);
        menuLayout[2] = (LinearLayout) findViewById(R.id.activity_tab_mine);
        menuImage[2] = (ImageView) findViewById(R.id.activity_tab_mine_image);
        menuText[2] = (TextView) findViewById(R.id.activity_tab_mine_text);
        menuLayout[3] = (LinearLayout) findViewById(R.id.activity_tab_lately);
        menuImage[3] = (ImageView) findViewById(R.id.activity_tab_lately_image);
        menuText[3] = (TextView) findViewById(R.id.activity_tab_lately_text);
        menuLayout[4] = (LinearLayout) findViewById(R.id.activity_tab_set);
        menuImage[4] = (ImageView) findViewById(R.id.activity_tab_set_image);
        menuText[4] = (TextView) findViewById(R.id.activity_tab_set_text);

        setListener();
        setBtnLayout(HOME);
        viewPager.setOffscreenPageLimit(5);

        if (!vlcUtils.isInstalledVlc()) {
            vlcUtils.installVlc();
        }

        FontUtils.setGlobalFont(this, getWindow().getDecorView(), Global.NANUM);
    }

    private void setListener() {
        for (int i = 0; i < menuLayout.length; i++) {
            final int I = i;
            menuLayout[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setBtnLayout(I);
                    viewPager.setCurrentItem(I);
                }
            });
        }
    }

    private void setBtnLayout(int btn) {
        setBtnImage(btn);
        setBtnText(btn);
        setBtnParams(btn);
        setBtnPadding(btn);

        for (int i = 0; i < menuLayout.length; i++) {
            if (i == btn) {
                menuLayout[i].setBackgroundResource(R.drawable.menu_selected);
            } else {
                menuLayout[i].setBackgroundColor(getResources().getColor(R.color.menu_not_selected));
            }
        }
    }

    private void setBtnPadding(int btn) {
        for (int i = 0; i < menuLayout.length; i++) {
            if (i == btn) {
                int padding = getResources().getDimensionPixelSize(R.dimen.btn_menu_margin);
                menuLayout[i].setPadding(0, 0, padding, 0);
            } else {
                menuLayout[i].setPadding(0, 0, 0, 0);
            }
        }
    }

    private void setBtnParams(int btn) {
        for (int i = 0; i < menuLayout.length; i++) {
            if (i == btn) {
                menuLayout[i].setLayoutParams(getSelectedBtnParams());
            } else {
                menuLayout[i].setLayoutParams(getNotSelectedBtnParams());
            }
        }
    }

    private void setBtnImage(int btn) {
        if (btn == HOME) {
            menuImage[0].setImageResource(R.drawable.ic_home_selected);
            menuImage[1].setImageResource(R.drawable.ic_study_not_selected);
            menuImage[2].setImageResource(R.drawable.ic_mine_not_selected);
            menuImage[3].setImageResource(R.drawable.ic_lately_not_selected);
            menuImage[4].setImageResource(R.drawable.ic_set_not_selected);
        } else if (btn == STUDY) {
            menuImage[0].setImageResource(R.drawable.ic_home_not_selected);
            menuImage[1].setImageResource(R.drawable.ic_study_selected);
            menuImage[2].setImageResource(R.drawable.ic_mine_not_selected);
            menuImage[3].setImageResource(R.drawable.ic_lately_not_selected);
            menuImage[4].setImageResource(R.drawable.ic_set_not_selected);
        } else if (btn == MINE) {
            menuImage[0].setImageResource(R.drawable.ic_home_not_selected);
            menuImage[1].setImageResource(R.drawable.ic_study_not_selected);
            menuImage[2].setImageResource(R.drawable.ic_mine_selected);
            menuImage[3].setImageResource(R.drawable.ic_lately_not_selected);
            menuImage[4].setImageResource(R.drawable.ic_set_not_selected);
        } else if (btn == LATELY) {
            menuImage[0].setImageResource(R.drawable.ic_home_not_selected);
            menuImage[1].setImageResource(R.drawable.ic_study_not_selected);
            menuImage[2].setImageResource(R.drawable.ic_mine_not_selected);
            menuImage[3].setImageResource(R.drawable.ic_lately_selected);
            menuImage[4].setImageResource(R.drawable.ic_set_not_selected);
        } else if (btn == SET) {
            menuImage[0].setImageResource(R.drawable.ic_home_not_selected);
            menuImage[1].setImageResource(R.drawable.ic_study_not_selected);
            menuImage[2].setImageResource(R.drawable.ic_mine_not_selected);
            menuImage[3].setImageResource(R.drawable.ic_lately_not_selected);
            menuImage[4].setImageResource(R.drawable.ic_set_selected);
        }
    }

    private void setBtnText(int btn) {
        for (int i = 0; i < menuLayout.length; i++) {
            if (i == btn) {
                menuText[i].setTextColor(Color.WHITE);
            } else {
                menuText[i].setTextColor(Color.BLACK);
            }
        }
    }

    private LinearLayout.LayoutParams getSelectedBtnParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
        );
        params.weight = 1;
        params.gravity = Gravity.CENTER;
        params.bottomMargin = 10;
        return params;
    }

    private LinearLayout.LayoutParams getNotSelectedBtnParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
        );
        params.weight = 1;
        params.gravity = Gravity.CENTER;
        params.bottomMargin = 10;
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.fab_margin);
        return params;
    }

    private void checkDB() {
        Global.dbManager = new DBManger(getApplicationContext(), Global.APP_NAME, null, 1);
        if (Global.dbManager.getCourses().size() == 0) {
            for (int i = 0; i < Global.files.size(); i++) {
                Global.dbManager.insertCourse(new CourseEntity(i, new Date(0), false));
            }
        }
        Global.courses = Global.dbManager.getCourses();
    }

    @Override
    public void onPlay() {
        if (dialog == null) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
            builder.progress(true, 0);
            builder.cancelable(false);
            builder.title("잠시만 기다려주세요");
            builder.content("암호화 해제 중입니다.\n5 ~ 15초 가량 소요될 수 있습니다.");
            dialog = builder.build();
        }
        dialog.show();
    }

    @Override
    public void onStopPlay() {
        dialog.cancel();
    }
}