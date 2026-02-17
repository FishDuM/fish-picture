package hk.fish.fishpicturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hk.fish.fishpicturebackend.common.ResultUtils;
import hk.fish.fishpicturebackend.domain.dto.UserLoginRequest;
import hk.fish.fishpicturebackend.domain.dto.UserRegisterRequest;
import hk.fish.fishpicturebackend.domain.entity.User;
import hk.fish.fishpicturebackend.domain.enums.UserRoleEnum;
import hk.fish.fishpicturebackend.domain.vo.LoginUserVO;
import hk.fish.fishpicturebackend.exception.BusinessException;
import hk.fish.fishpicturebackend.exception.ErrorCode;
import hk.fish.fishpicturebackend.service.UserService;
import hk.fish.fishpicturebackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import static hk.fish.fishpicturebackend.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author 30574
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2026-02-17 18:20:55
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{


    /**
     * 用户注册
     * @param userRegisterRequest 用户注册请求实体类
     * @return
     */
    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        // 1、校验参数
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!checkPassword.equals(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入密码不一致");
        }
        // 2、检查必要参数是否重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        Long count = this.baseMapper.selectCount(userQueryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该账号已注册");
        }
        // 3、密码加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 4、插入数据到数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("小鱼_" + UUID.fastUUID());
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean result = this.save(user);
        if (!result) {
            ResultUtils.error(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        }
        return user.getId();
    }


    /**
     * 用户登录
     * @param userLoginRequest 用户登录请求实体类
     * @param request
     * @return
     */
    @Override
    public LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 1、校验
        if (StrUtil.hasBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码错误");
        }
        // 2、密码加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 3、查询数据库
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        userQueryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(userQueryWrapper);
        if (user == null) {
            log.info("userAccount can not match userPassword");
            // 4、不存在抛异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 5、保存登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (currentUser == null || currentUser.getId() == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 查数据库更新缓存
        Long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if(currentUser == null ){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public void userLogout(HttpServletRequest request) {
        Object objectUser = request.getSession().getAttribute(USER_LOGIN_STATE);
        if(objectUser == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
    }

    /**
     * 密码加密
     * @param password
     * @return
     */
    @Override
    public String getEncryptPassword(String password) {
        // 加盐混淆密码
        final String SALT = "fish";
        return DigestUtils.md5DigestAsHex((SALT + password).getBytes());
    }

    /**
     * 获取脱敏后的信息
     * @param user 用户
     * @return 脱敏后的用户信息
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if(user == null){
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }
}




