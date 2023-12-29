package com.ht.elearning.push_notification.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Platform {
    WEB("web"), ANDROID("android"), IOS("ios");
    private final String value;
}
