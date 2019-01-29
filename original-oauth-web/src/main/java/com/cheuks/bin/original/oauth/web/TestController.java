package com.cheuks.bin.original.oauth.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cheuks.bin.original.oauth.model.User;
import com.cheuks.bin.original.oauth.security.OauthAuthenticationToken;
import com.cheuks.bin.original.oauth.util.Result;
import com.cheuks.bin.original.oauth.util.ResultFactory;

//@Profile({ "dev", "rc" })
@Controller
//@RequestMapping(value = { "/", "/account" })
@RequestMapping(value = { "/" })
public class TestController {

	ResultFactory resultFactory = new ResultFactory();

	@ResponseBody
	@GetMapping("/test/test")
	public Result<Object> p(HttpServletRequest request) throws Throwable {
		OauthAuthenticationToken auth=(OauthAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		System.err.println(((User)auth.getPrincipal()).getUserName());
		return resultFactory.createSuccess();
	}

}