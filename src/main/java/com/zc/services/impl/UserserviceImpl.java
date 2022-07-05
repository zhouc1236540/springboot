package com.zc.services.impl;

import com.zc.dao.UserMapper;
import com.zc.domain.User;
import com.zc.domain.Users;
import com.zc.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA
 *
 * @author: 山毛榉
 * @date : 2022/7/5 9:34
 * @version: 1.0
 * Description: 接口实现
 */
@Service
public class UserserviceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    /**
     * @return
     */
    @Override
    public User getUser() {
        return userMapper.getUser();
    }

    /**
     * @return
     */
    @Override
    public Users getU(Integer id) {
        return userMapper.getU(id);
    }
}
