package com.yixin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;

@RestController
@RequestMapping("/")
public class CdrController {

    Logger logger = LoggerFactory.getLogger(CdrController.class);

    @RequestMapping(value = "cdrIndex",method = RequestMethod.POST)
    public void cdrIndex(HttpServletRequest request){
        String cdr = request.getParameter("cdr");
        logger.info("cdr = "+cdr);
        logger.info("bb = " + URLDecoder.decode(cdr));
    }
}
