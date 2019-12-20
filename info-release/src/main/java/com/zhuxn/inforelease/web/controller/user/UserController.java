package com.zhuxn.inforelease.web.controller.user;

import com.beagledata.commons.AjaxResult;
import com.zhuxn.inforelease.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuxn
 * @date 2019/11/29 14:30
 */
@RestController
@RequestMapping(value = "user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * @author zhuxn
     * 修改密码
     */
    @RequestMapping("changePwd")
    public AjaxResult changePwd(String oldpassword, String newpassword) {
        return userService.changePassword(oldpassword,newpassword);
    }
}
