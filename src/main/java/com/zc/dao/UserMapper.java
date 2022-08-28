package com.zc.dao;

import com.zc.domain.Users;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by IntelliJ IDEA
 *
 * @author: 山毛榉
 * @date : 2022/7/5 9:24
 * @version: 1.0
 * Description: mapper
 */
@Mapper
public interface UserMapper {

    Users getU(Integer id);
}
