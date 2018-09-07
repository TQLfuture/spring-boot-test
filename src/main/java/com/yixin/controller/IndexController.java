package com.yixin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class IndexController {

    Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("index")
    public void index(){
        logger.info("hello");

    }
}
