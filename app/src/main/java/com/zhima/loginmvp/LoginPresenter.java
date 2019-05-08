package com.zhima.loginmvp;

import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.tendcloud.tenddata.TCAgent;
import com.tendcloud.tenddata.TDAccount;
import com.yasha.ys.manager.ConstantManager;
import com.yasha.ys.utils.Des3;
import com.yasha.ys.utils.ToolFile;
import com.yasha.ys.utils.ToolGson;
import com.yasha.ys.view.global.base.Callback;
import com.yasha.ys.view.global.base.DataModel;
import com.yasha.ys.view.global.base.MvpBasePresenter;
import com.yasha.ys.view.global.model.request.HeaderEncrypt;
import com.yasha.ys.view.global.model.response.LoginResponse;

import static com.yasha.ys.manager.ConstantManager.FORCE_INIT;

/**
 * @author Joh_hz
 * @date 2018/7/4
 * @Description
 */

public class LoginPresenter extends MvpBasePresenter<LoginView> {
    /**
     * 获取网络数据
     *
     * @param args1 参数
     */
    public void getData(String args1, String args2) {
        if (!isViewAttached() || TextUtils.isEmpty(args1) || TextUtils.isEmpty(args2)) {
            //如果没有View引用就不加载数据
            return;
        }
        //显示正在加载进度条
        getView().showLoading();
        // 调用Model请求数据
        try {
            DataModel.request(DataModel.API_LOGIN_DATA).params(args1, args2)
                    .execute(new Callback<LoginResponse>() {
                        @Override
                        public void onSuccess(LoginResponse response) {
                            //调用view接口显示数据
                            if (isViewAttached()) {
                                getView().hideLoading();
                                getView().showData(response);
                            }
                        }

                        @Override
                        public void onFailure(LoginResponse msg) {
                            //调用view接口提示失败信息
                            if (isViewAttached()) {
                                getView().showToast("");
                            }
                        }

                        @Override
                        public void onError() {
                            //调用view接口提示请求异常
                            if (isViewAttached()) {
                                getView().showErr();
                            }
                        }

                        @Override
                        public void onComplete() {
                            // 隐藏正在加载进度条
                            if (isViewAttached()) {
                                getView().hideLoading();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
