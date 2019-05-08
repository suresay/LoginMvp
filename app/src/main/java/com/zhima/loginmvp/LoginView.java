package com.zhima.loginmvp;

import com.yasha.ys.view.global.base.MvpBaseView;
import com.yasha.ys.view.global.model.response.LoginResponse;

/**
 * @author Joh_hz
 * @date 2018/7/4
 * @Description
 */

public interface LoginView extends MvpBaseView {
    /**
     * 当数据请求成功后，调用此接口显示数据
     *
     * @param data 数据源
     */
    void showData(LoginResponse data);
}
