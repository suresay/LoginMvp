package com.zhima.loginmvp;

import android.os.Handler;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.tendcloud.tenddata.TCAgent;
import com.tendcloud.tenddata.TDAccount;
import com.yasha.ys.app.MyApplicationLike;
import com.yasha.ys.inter.SubscriberOnNextListener;
import com.yasha.ys.manager.ConstantManager;
import com.yasha.ys.retrofit.HttpResultFunc;
import com.yasha.ys.retrofit.ProgressSubscriber;
import com.yasha.ys.retrofit.RetrofitManager;
import com.yasha.ys.utils.Des3;
import com.yasha.ys.utils.ToolFile;
import com.yasha.ys.utils.ToolGson;
import com.yasha.ys.view.global.base.BaseModel;
import com.yasha.ys.view.global.base.Callback;
import com.yasha.ys.view.global.model.request.HeaderEncrypt;
import com.yasha.ys.view.global.model.request.LoginRequest;
import com.yasha.ys.view.global.model.request.UserEncrypt;
import com.yasha.ys.view.global.model.response.LoginResponse;

import static com.yasha.ys.manager.ConstantManager.FORCE_INIT;

/**
 * Created by liuheng on 2018/5/22.
 */

public class LoginModel extends BaseModel<LoginResponse> {

    /**
     * 获取网络接口数据
     *
     * @param callback 数据回调接口
     */
    @Override
    public void execute(final Callback<LoginResponse> callback) {

        UserEncrypt userEncrypt = new UserEncrypt(mParams[0], mParams[1], 0);
        String object = ToolGson.toJson(userEncrypt);
        //用户名，密码字符串加密
        LoginRequest request = null;
        try {
            request = new LoginRequest(Des3.encode(object, Des3.key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        RetrofitManager.getInstance().toSubscribe(RetrofitManager.getInstance().getApiService().
                        login(request).map(new HttpResultFunc<LoginResponse>()),
                new ProgressSubscriber<>(new SubscriberOnNextListener<LoginResponse>() {
                    @Override
                    public void onNext(LoginResponse response) {
                        ToolFile.putString(ConstantManager.SP_USER_NAME_REMEMBER, mParams[0]);
                        if (ConstantManager.isDebug) {
                            ToolFile.putString(ConstantManager.SP_PASSWORD_REMEMBER, mParams[1]);
                        }

                        // 组装header通用信息
                        HeaderEncrypt headerEncrypt = new HeaderEncrypt();
                        headerEncrypt.setDevice(DeviceUtils.getManufacturer() + ":" + DeviceUtils.getModel() + ",OS:" + DeviceUtils.getSDKVersionName());
                        headerEncrypt.setJwt(response.getJwtstr());
                        headerEncrypt.setVersion(AppUtils.getAppVersionCode());
                        headerEncrypt.setIp(NetworkUtils.getIPAddress(true));
                        // 登录成功，记录组装header
                        try {
                            ToolFile.putString(ConstantManager.SP_TOKEN, Des3.encode(ToolGson.toJson(headerEncrypt), Des3.key));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // 记录JWTSTR
                        ToolFile.putString(ConstantManager.JWTSTR, response.getJwtstr());
                        // 记录工号
                        ToolFile.putString(ConstantManager.SP_UID, response.getUid());
                        // 记录用户名
                        ToolFile.putString(ConstantManager.SP_USER_NAME, response.getName());
                        // TCAgent 统计
                        TCAgent.onLogin(ToolFile.getString(ConstantManager.SP_UID), TDAccount.AccountType.TYPE1, ToolFile.getString(ConstantManager.SP_USER_NAME));
                        // 写入用户模块权限数据,写入成功后记录登录返回的权限时间戳
                        // 记录时间戳
                        ToolFile.putLong(ConstantManager.SP_PERMISSIONS, response.getAclTimestamp());
                        // 标记用户登录
                        SPUtils.getInstance().put(FORCE_INIT, true);
                        callback.onSuccess(response);
                        callback.onComplete();
                    }
                }, MyApplicationLike.getMyApplication()));
    }
}
