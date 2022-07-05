package com.zc.services;

import com.zc.domain.User;
import com.zc.domain.Users;

/**
 * Created by IntelliJ IDEA
 *
 * @author: 山毛榉
 * @date : 2022/7/5 9:33
 * @version: 1.0
 * Description: 服务接口
 */
public interface UserService {
    User getUser();
    Users getU(Integer id);
}
