package com.tenmue.micro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestController {

	@Autowired
    private RestTemplate restTemplate;
	
	@Autowired
	private LoadBalancerClient loadBalancer;

    @RequestMapping(value = "/echo/{str}", method = RequestMethod.GET)
    public String echo(@PathVariable String str) {
    	System.out.println("url is:"+loadBalancer.choose("service-provider"));
        return restTemplate.getForObject("http://service-provider/echo/" + str, String.class);
    }
}
