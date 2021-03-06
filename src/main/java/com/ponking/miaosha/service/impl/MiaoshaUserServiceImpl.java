package com.ponking.miaosha.service.impl;

import com.ponking.miaosha.dao.MiaoshaUserDao;
import com.ponking.miaosha.exception.GlobalException;
import com.ponking.miaosha.model.entity.MiaoshaUser;
import com.ponking.miaosha.model.vo.LoginVo;
import com.ponking.miaosha.redis.MiaoshaUserKey;
import com.ponking.miaosha.redis.RedisService;
import com.ponking.miaosha.result.ResultStatus;
import com.ponking.miaosha.service.MiaoshaUserService;
import com.ponking.miaosha.util.MD5Util;
import com.ponking.miaosha.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ponking
 * @ClassName MiaoshaUserServiceImpl
 * @date 2020/3/29--15:40
 **/
@Service
public class MiaoshaUserServiceImpl implements MiaoshaUserService {

    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    private RedisService redisService;

    @Autowired
    private MiaoshaUserDao miaoshaUserDao;

    @Override
    public MiaoshaUser getById(Long id) {
        return miaoshaUserDao.getById(id);
    }

    @Override
    public MiaoshaUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        //延长有效期
        if (miaoshaUser != null) {
            addCookie(response, token, miaoshaUser);
        }
        return miaoshaUser;
    }


    @Override
    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(ResultStatus.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
        if (miaoshaUser == null) {
            throw new GlobalException(ResultStatus.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = miaoshaUser.getPassword();
        String saltDB = miaoshaUser.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(ResultStatus.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(response, token, miaoshaUser);
        return true;
    }

    private void addCookie(HttpServletResponse response, String token, MiaoshaUser miaoshaUser) {
        redisService.set(MiaoshaUserKey.token, token, miaoshaUser);
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
