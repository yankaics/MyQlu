package com.hugo.myqlu.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hugo.myqlu.R;
import com.hugo.myqlu.bean.CourseBean;
import com.hugo.myqlu.dao.BaseInfoDao;
import com.hugo.myqlu.dao.CourseDao;
import com.hugo.myqlu.utils.HtmlUtils;
import com.hugo.myqlu.utils.PareseKbFromHtml;
import com.hugo.myqlu.utils.SpUtil;
import com.hugo.myqlu.utils.TextEncoderUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class LoginActivity extends Activity implements View.OnClickListener {


    @Bind(R.id.et_username)
    EditText etUsername;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.iv_codes)
    ImageView ivCodes;
    @Bind(R.id.tv_change)
    TextView tvChange;
    @Bind(R.id.pb_login)
    ProgressBar pbLogin;
    @Bind(R.id.tv_error)
    TextView tvError;
    @Bind(R.id.tv_cancle)
    TextView tvCancle;
    @Bind(R.id.tv_ok)
    TextView tvOk;
    @Bind(R.id.rootView)
    LinearLayout rootView;
    private String mainUrl = "http://210.44.159.4";
    private String codeUrl = "http://210.44.159.4/CheckCode.aspx";
    private String loginUrl = "http://210.44.159.4/default2.aspx";
    private String logoutUrl = "http://210.44.159.4/logout.aspx";
    private static String StuCenterUrl = "http://210.44.159.4/xs_main.aspx?xh=stuxh";
    private static String cjcxUrl = "http://210.44.159.4/xscj.aspx?xh=stuxh&xm=stuname&gnmkdm=N121605";
    private static String kscxUrl = "http://210.44.159.4/xskscx.aspx?xh=stuxh&xm=stuname%90&gnmkdm=N121604";
    private static String kbcxUrl = "http://210.44.159.4/xskbcx.aspx?xh=stuxh&xm=stuname&gnmkdm=N121603";
    private String noCodeLoginUrl = "http://210.44.159.4/default6.aspx";
    private String userId;
    private String password;
    private String code;
    private Context mContext = this;
    private String stuXH;
    private String stuName;
    private AlertDialog dialog;
    private List<CourseBean> allCourseList;
    private SharedPreferences sp;
    private String noCodeVIEWSTATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initData();
        initListener();
        showCodeimage();
    }

    private void initData() {
        sp = SpUtil.getSp(mContext, "config");
        noCodeVIEWSTATE = getString(R.string.noCodeVIEWSTATE);
    }

    private void initListener() {
        tvOk.setOnClickListener(this);
        tvChange.setOnClickListener(this);
        tvCancle.setOnClickListener(this);
        ivCodes.setOnClickListener(this);
    }

    //加载验证码
    public void showCodeimage() {
        OkHttpUtils.get().url(codeUrl).build()
                .connTimeOut(5000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        System.out.println("验证码加载失败");
                        //应该给验证码设置一个图片
                    }

                    @Override
                    public void onResponse(Bitmap response) {
                        System.out.println("验证码加载成功");
                        ivCodes.setImageBitmap(response);
                    }
                });
    }

    //登陆前检查
    private void attemptLogin() {
        View focusView = null;
        System.out.println("检查登陆");
        userId = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        code = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(userId)) {
            etUsername.setError("学号不能为空");
            focusView = etUsername;
        } else if (TextUtils.isEmpty(password)) {
            etPassword.setError("密码不能为空");
            focusView = etPassword;
        } else if (TextUtils.isEmpty(code)) {
            etCode.setError("验证码不能为空");
            focusView = etCode;
        }
        if (focusView != null) {
            focusView.requestFocus();
        } else {
            //向服务器请求登陆
            System.out.println("请求服务器");
            requestLogin();
        }
    }

    /**
     * 像服务器模拟登陆
     */
    private void requestLogin() {
        int visibility = tvError.getVisibility();
        if (visibility == View.VISIBLE) {
            tvError.setVisibility(View.INVISIBLE);
        }
        pbLogin.setVisibility(View.VISIBLE);
        final PostFormBuilder post = OkHttpUtils.post();
        post.url(loginUrl);
        post.tag(this);
        post.addParams("__VIEWSTATE", "dDwtMTMxNjk0NzYxNTs7PpK7CYMIAY8gja8M8G8YpGL8ZEAL");
        post.addParams("__VIEWSTATEGENERATOR", "92719903");
        post.addParams("txtUserName", userId);
        post.addParams("TextBox2", password);
        post.addParams("txtSecretCode", code);
        post.addParams("RadioButtonList1", "%D1%A7%C9%FA");
        post.addParams("Button1", "");
        post.addParams("lbLanguage", "");
        post.addHeader("Host", "210.44.159.4");
        post.addHeader("Referer", "//210.44.159.4/default2.aspx");
        post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36");
        post.build()
                .connTimeOut(5000)
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        pbLogin.setVisibility(View.GONE);
                        tvError.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response) {
                        View focusView = null;
                        System.out.println("onResponse");
                        if (response.contains("验证码不正确")) {
                            etCode.setError("验证码错误");
                            focusView = etCode;
                        } else if (response.contains("密码错误")) {
                            etPassword.setError("密码错误");
                            focusView = etPassword;
                        } else if (response.contains("用户名不存在")) {
                            etUsername.setError("用户名不存在");
                            focusView = etUsername;
                        }
                        if (focusView != null) {
                            focusView.requestFocus();
                            pbLogin.setVisibility(View.INVISIBLE);
                            changCodeImage();
                        } else {
                            //登陆成功
                            pbLogin.setVisibility(View.INVISIBLE);
                            //初始化用户数据
//                            initURL(response);
                            System.out.println("登录成功");
                            showSaveDataDialog(response);
                        }
                    }
                });
    }

    /**
     * 初始化用户数据
     *
     * @param response
     */
    private void initURL(String response) {
        HtmlUtils utils = new HtmlUtils(response);
        cjcxUrl = mainUrl + "/" + utils.encoder(response);
        String xhandName = utils.getXhandName();
        //initUrlData(xhandName);
        String[] split = xhandName.split(" ");
        //用户的学号
        stuXH = split[0];
        //用户的姓名
        stuName = split[1].replace("同学", "");
        //设置需要的url
        cjcxUrl = cjcxUrl.replace("stuxh", stuXH).replace("stuname", TextEncoderUtils.encoding(stuName));
        kbcxUrl = kbcxUrl.replace("stuxh", stuXH).replace("stuname", TextEncoderUtils.encoding(stuName));
        kscxUrl = kscxUrl.replace("stuxh", stuXH).replace("stuname", TextEncoderUtils.encoding(stuName));
        StuCenterUrl = StuCenterUrl.replace("stuxh", stuXH);
        System.out.println("url初始化成功");
        //初始化数据完成，保存至数据库
        //显示一个dialog：
    }

    private void showSaveDataDialog(String response) {
        System.out.println("显示dialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("正在同步数据...");
        View view = View.inflate(mContext, R.layout.dialog_save_data, null);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        initURL(response);
        initCourseData();
    }

    private void initCourseData() {
        if (stuName == null || stuXH == null) {
            return;
        }
        OkHttpUtils.post().url(kbcxUrl)
                .tag(this)
                .addHeader("gnmkdm", "N121603")
                .addParams("xm", TextEncoderUtils.encoding(stuName))
                .addHeader("Referer", kbcxUrl)
                .addHeader("Host", "210.44.159.4")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.75 Safari/537.36")
                .build()
                .connTimeOut(5000)
                .readTimeOut(5000)
                .execute(new StringCallback() {

                    @Override
                    public String parseNetworkResponse(Response response) throws IOException {
                        System.out.println(response.code());
                        return super.parseNetworkResponse(response);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        System.out.println(e.toString());
                        System.out.println("课表请求失败");
                        dialog.dismiss();
                        Snackbar.make(rootView, "未知错误，重启APP", Snackbar.LENGTH_LONG).setAction("重启", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = getBaseContext().getPackageManager()
                                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        }).show();

                    }

                    @Override
                    public void onResponse(String response) {
                        System.out.println("请求课表成功");
                        allCourseList = PareseKbFromHtml.getKB(response);
                        if (allCourseList.size() == 0) {
                            dialog.dismiss();
                            Toast.makeText(mContext, "同步失败", Toast.LENGTH_SHORT).show();
                        } else {
                            saveDataToDB();
                        }
                    }
                });
    }


    private void saveDataToDB() {
        System.out.println("正在保存数据到数据库");
        BaseInfoDao baseInfoDao = new BaseInfoDao(mContext);
        baseInfoDao.add("cjcxUrl", LoginActivity.cjcxUrl);
        baseInfoDao.add("kbcxUrl", LoginActivity.kbcxUrl);
        baseInfoDao.add("kscxUrl", LoginActivity.kscxUrl);
        baseInfoDao.add("StuCenterUrl", StuCenterUrl);
        baseInfoDao.add("stuName", stuName);
        baseInfoDao.add("stuXH", stuXH);
        baseInfoDao.add("noCodeLoginUrl", noCodeLoginUrl);

        baseInfoDao.add("stuXH", stuXH);
        baseInfoDao.add("mainUrl", mainUrl);
        baseInfoDao.add("logoutUrl", logoutUrl);
        //明文保存密码不安全，但是向服务器提交的时候同样是明文，没办法。。。
        baseInfoDao.add("password", password);
        CourseDao courseDao = new CourseDao(mContext);

        //保存课表到数据库中
        boolean saveSucess = true;
        for (CourseBean course : allCourseList) {
            String courseName = course.getCourseName();
            String courseTime = course.getCourseTime();
            String courstTimeDetail = course.getCourstTimeDetail();
            String courseTeacher = course.getCourseTeacher();
            String courseLocation = course.getCourseLocation();
            boolean isSucess = courseDao.add(courseName, courseTime, courstTimeDetail, courseTeacher, courseLocation);
            if (!isSucess) {
                System.out.println("!isSucess");
                dialog.dismiss();
                saveSucess = false;
                Toast.makeText(mContext, "保存课表失败", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        //数据保存成功
        if (saveSucess) {
            System.out.println("saveSucess");
            sp.edit().putBoolean("isFirstIn", false).commit();
            startActivity(new Intent(mContext, MainActivity.class));
            dialog.dismiss();
            finish();
        }
    }

    //切换验证码

    public void changCodeImage() {
        codeUrl += '?';
        showCodeimage();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                attemptLogin();
                break;
            case R.id.tv_cancle:
                break;
            case R.id.tv_change:
                changCodeImage();
                break;
            case R.id.iv_codes:
                changCodeImage();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RequestCall call = OkHttpUtils.get().url(kbcxUrl).build();
        call.cancel();
    }
}