package com.example.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.em.ExtApiIdempotent;
import com.example.entity.User;
import com.example.utils.ConstantUtils;
import com.example.uuid.BaseRedisService;
import com.example.uuid.RedisToken;

/**
 * 处理rpc调用请求
 * 
 * @author Administrator
 *
 */

@RestController
public class UserController {

	@Autowired
	private RedisToken redisToken;

	@RequestMapping(value = "/getToken")
	public String getToken() {
		return redisToken.getToken();
	}

	@RequestMapping(value = "/addUser")
	public String addOrder(User user, HttpServletRequest request) {
		// 获取请求头中的token令牌
		String token = request.getHeader("token");
		if (StringUtils.isEmpty(token)) {
			return "请求参数错误";
		}

		boolean isToken = redisToken.checkToken(token);
		if (!isToken) {
			System.out.println("请不要重新提交");
			return "请勿重复提交!";
		} else {
			// 业务逻辑处理
			System.out.println("校验成功，处理业务逻辑");
			return "添加成功";
		}

	}
	
	/**
	 * 使用注解验证
	 * @param user
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/addUser1")
	@ExtApiIdempotent(value = ConstantUtils.EXTAPIHEAD)
	public String addOrder1(User user, HttpServletRequest request) {
	
		return "success";

	}
	

}
