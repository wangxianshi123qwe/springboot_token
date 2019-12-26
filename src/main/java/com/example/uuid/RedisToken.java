package com.example.uuid;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Encoder;

/**
 * 生成tokean放在redis中
 * 
 * @author Administrator
 *
 */
@Component
public class RedisToken {

	@Autowired
	private BaseRedisService baseRedisService;

	private static final long TOKENTIME = 60 * 60;

	public String getToken() {
		String token = (System.currentTimeMillis() + new Random().nextInt(999999999)) + "";
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			byte md5[] = md.digest(token.getBytes());
			BASE64Encoder encoder = new BASE64Encoder();
			String tokenValue = encoder.encode(md5);
			baseRedisService.setString(tokenValue, tokenValue, TOKENTIME);
			return tokenValue;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String createToken() {
		String token = "token" + UUID.randomUUID();
		baseRedisService.setString(token, token, TOKENTIME);
		return token;
	}

	public boolean checkToken(String tokenKey) {
		String tokenValue = baseRedisService.getString(tokenKey);
		if (StringUtils.isEmpty(tokenValue)) {
			return false;
		}
		// 保证每个接口对应的token只能访问一次，保证接口幂等性问题
		baseRedisService.delKey(tokenKey);
		return true;
	}

}
