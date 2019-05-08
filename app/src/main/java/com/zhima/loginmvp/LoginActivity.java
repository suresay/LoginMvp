package com.zhima.loginmvp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.tendcloud.tenddata.TCAgent;
import com.tendcloud.tenddata.TDAccount;
import com.yasha.ys.BuildConfig;
import com.yasha.ys.R;
import com.yasha.ys.base.BaseActivity;
import com.yasha.ys.db.model.global.ContactsDeptDB;
import com.yasha.ys.db.model.global.ContactsPersonDB;
import com.yasha.ys.db.model.global.PermissionsDB;
import com.yasha.ys.db.realm.RealmOperationHelper;
import com.yasha.ys.inter.SubscriberOnNextListener;
import com.yasha.ys.manager.ConstantManager;
import com.yasha.ys.manager.DialogManager;
import com.yasha.ys.manager.NotificationManager;
import com.yasha.ys.manager.StatusBarManager;
import com.yasha.ys.retrofit.HttpResultFunc;
import com.yasha.ys.retrofit.ProgressSubscriber;
import com.yasha.ys.retrofit.RetrofitManager;
import com.yasha.ys.utils.Des3;
import com.yasha.ys.utils.ToolFile;
import com.yasha.ys.utils.ToolGson;
import com.yasha.ys.view.global.activity.ForgetPasswordActivity;
import com.yasha.ys.view.global.base.MvpBaseActivity;
import com.yasha.ys.view.global.model.request.HeaderEncrypt;
import com.yasha.ys.view.global.model.request.LoginRequest;
import com.yasha.ys.view.global.model.request.UserEncrypt;
import com.yasha.ys.view.global.model.response.LoginResponse;
import com.yasha.ys.view.global.splash.Workaround;
import com.yasha.ys.view.home.activity.HomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yasha.ys.manager.ConstantManager.FORCE_INIT;

/**
 * Title : 登录页面
 * Description :
 * Author : Jerry xu    date : 2017/2/14  21:15
 * Update : Jerry xu    date : 2017/9/26 15:19
 * Version : 1.0.0
 * Copyright : copyright(c) 浙江亚厦股份有限公司 2017 ~ 2020 版权所有
 */
public class LoginActivity extends MvpBaseActivity implements LoginView {

    /**
     * 是否需要清除数据
     * #0 默认，首次登录，不清除数据
     * #1 设备异地登录，本地强制下线，并清除数据
     * #2 其他类型（退出登录，Token过期等），清除数据
     */
    public final static String IS_CLEAR_DATA = "clear_data";

    @BindView(R.id.ti_login_name)
    TextInputLayout tiLoginName;
    @BindView(R.id.tv_url)
    TextView tvUrl;
    @BindView(R.id.at_login_name)
    EditText atLoginName;
    @BindView(R.id.ti_password)
    TextInputLayout tiPassword;
    @BindView(R.id.et_password)
    EditText etPassword;

    @BindView(R.id.btn_login)
    Button btnLogin;
    LoginPresenter presenter;

    /**
     * 登陆
     */
    @OnClick(R.id.btn_login)
    void login() {
        loginApp();
    }

    /**
     * 忘记密码
     */
    @OnClick(R.id.tv_forget_psw)
    void forgetPassWord() {
        startActivity(new Intent(this, ForgetPasswordActivity.class));
    }

    /**
     * 关闭键盘
     */
    @OnClick(R.id.ll_content)
    void closeInput() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputManger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        presenter = new LoginPresenter();
        presenter.attachView(this);

        initView();
    }

    public void initView() {
        Workaround.assistActivity(this);
        // 状态栏透明
        StatusBarManager.TransparentStatusBar(this);

        int isClearData = getIntent().getIntExtra(IS_CLEAR_DATA, 0);
        // 踢出多设备
        if (!ConstantManager.isDebug && isClearData == 1) {
            DialogManager.defaultDialog(getContext(), "提示", "您的账号在别的设备上登录，您被迫下线!",
                    "知道了", null, null);
        }

        atLoginName.setText(ToolFile.getString(ConstantManager.SP_USER_NAME_REMEMBER));
        etPassword.setText(ToolFile.getString(ConstantManager.SP_PASSWORD_REMEMBER));

    }

    /**
     * 登录
     */
    private void loginApp() {
        tiLoginName.setErrorEnabled(false);
        tiPassword.setErrorEnabled(false);

        String loginName = atLoginName.getText().toString();
        String password = etPassword.getText().toString();

        //用户名验证
        if (TextUtils.isEmpty(loginName)) {
            tiLoginName.setError("请输入账号");
        } else if (TextUtils.isEmpty(password)) {
            tiPassword.setError("请输入密码");
        } else if (!isPasswordValid(password)) {
            tiPassword.setError("密码过短");
        } else {
            presenter.getData(loginName, password);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 7;
    }

    @Override
    public void showData(LoginResponse response) {
        startActivity(new Intent(getContext(), HomeActivity.class));
        finish();
    }
}
