package hk.fish.fishpicturebackend.aop;

import hk.fish.fishpicturebackend.annotation.AuthCheck;
import hk.fish.fishpicturebackend.domain.entity.User;
import hk.fish.fishpicturebackend.domain.enums.UserRoleEnum;
import hk.fish.fishpicturebackend.exception.BusinessException;
import hk.fish.fishpicturebackend.exception.ErrorCode;
import hk.fish.fishpicturebackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthInterceptor {

    @Autowired
    private UserService userService;

    /**
     * 执行拦截
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint point, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        // 获取当前请求的所有属性
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        // 获取当前登录的用户
        User loginUser = userService.getLoginUser(request);
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        // 如果不需要权限，放行
        if(mustRoleEnum==null){
            return point.proceed();
        }
        // 必须有权限才通过
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if(userRoleEnum == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 必须有管理员权限，但是用户没管理员权限
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 通过权限校验
        return point.proceed();
    }
}
