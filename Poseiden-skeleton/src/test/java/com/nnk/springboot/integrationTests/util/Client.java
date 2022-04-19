package com.nnk.springboot.integrationTests.util;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public class Client {
    public static RequestPostProcessor johnBoyd(){
        return user("john.boyd@gmail.com").password("john.boyd");
    }
}
