package hk.fish.fishpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import hk.fish.fishpicturebackend.domain.dto.user.UserLoginRequest;
import hk.fish.fishpicturebackend.domain.dto.user.UserQueryRequest;
import hk.fish.fishpicturebackend.domain.dto.user.UserRegisterRequest;
import hk.fish.fishpicturebackend.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import hk.fish.fishpicturebackend.domain.vo.LoginUserVO;
import hk.fish.fishpicturebackend.domain.vo.UserVO;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 30574
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2026-02-17 18:20:55
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求实体类
     * @return 新用户Id
     */
    long userRegister(@RequestBody UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求实体类
     * @return 新用户Id
     */
    LoginUserVO userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 系统内部获取当前登录用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     * @param request
     * @return
     */
    void userLogout(HttpServletRequest request);

    /**
     * 获取加密后的密码
     *
     * @param password
     * @return
     */
    String getEncryptPassword(String password);

    /**
     * 获得脱敏后的用户登录信息
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获得脱敏后的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获得脱敏后的用户信息列表
     *
     * @param userVOList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userVOList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
}
