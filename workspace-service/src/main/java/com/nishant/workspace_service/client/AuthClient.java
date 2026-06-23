package com.nishant.workspace_service.client;

import java.beans.JavaBean;

import  org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import com.nishant.workspace_service.dto.UserProfileResponse;
import com.nishant.workspace_service.dto.WorkSpaceRequest;

import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "auth-service")
public interface AuthClient {
  @GetMapping("/auth/me")
    UserProfileResponse getUserProfile(@RequestHeader("X-User-Email") String email);
}