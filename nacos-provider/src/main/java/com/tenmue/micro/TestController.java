package com.tenmue.micro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class TestController {

    @Value("${name}")
    private String name;

    @Autowired
    private UserInfo userInfo;

    @RequestMapping(value = "/echo/{string}", method = RequestMethod.GET)
    public String echo(@PathVariable String string) {
        System.out.println("value is" + name);
        System.out.println(userInfo.getUsername() + "" + userInfo.getPassword());
        return "Hello Nacos Discovery " + string;
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello demo";
    }
}
