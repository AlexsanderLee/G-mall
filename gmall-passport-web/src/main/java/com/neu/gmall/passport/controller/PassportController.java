package com.neu.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.neu.gmall.bean.Constants;
import com.neu.gmall.bean.UmsMember;
import com.neu.gmall.service.UserService;
import com.neu.gmall.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

@Controller
public class PassportController {

    @Reference
    private UserService userService;

    //社交登陆功能：省略

    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token,String currentIp){
        //认证
        Map<String,Object> map = new HashMap<>();
        //此为拦截器发送的请求，request和浏览器的不同，不可以通过request请求获取IP
        Map<String, Object> decode = JwtUtil.decode(token, Constants.key, currentIp);

        if(decode!=null){
            map.put("status","success");
            map.put(Constants.memberId,(String)decode.get(Constants.memberId));
            map.put(Constants.nickname,(String)decode.get(Constants.nickname));
        }else{
            map.put("status","fail");
        }


        return JSON.toJSONString(map);
    }

    //登录后需要同步购物车
    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember  umsMember, HttpServletRequest request){
        String token = "";
        //调用服务验证用户名密码
        UmsMember umsMemberLogin = userService.login(umsMember);
        if(umsMemberLogin !=null){
            //jwt 制作一份token
            String nickname = umsMemberLogin.getNickname();
            Long id = umsMemberLogin.getId();
            Map<String,Object> map = new HashMap<>();
            map.put(Constants.nickname,nickname);
            //此处以String加密，解密是也为String
            map.put(Constants.memberId,String.valueOf(id));

            String ip = request.getHeader("x-forwarded-for");
            if(StringUtils.isBlank(ip)){
                ip=request.getRemoteAddr();
                if(StringUtils.isBlank(ip)){
                    ip=Constants.ip;
                }
            }
            //按照设计的算法加密
            token = JwtUtil.encode(Constants.key,map,ip);

            //将token存入redis
            userService.addUserToken(id,token);

        }else {
            //登录失败
            token = "fail";
        }
        return token;
    }

    //ReturnUrl：名称要与html表单中一致
    @RequestMapping("index")
    public String index(String ReturnUrl,ModelMap modelMap){

        if(StringUtils.isNotBlank(ReturnUrl))
            modelMap.put("ReturnUrl",ReturnUrl);
        return "index";
    }

}
