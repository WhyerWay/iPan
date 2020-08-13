package indi.ipan.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import com.google.code.kaptcha.Producer;

import indi.ipan.result.Result;
import indi.ipan.result.ResultUtil;
import indi.ipan.service.SecurityService;
import indi.ipan.util.RedisUtil;

@Service
public class SecurityServiceImpl implements SecurityService{

    @Autowired
    private Producer kaptchaProducer;
    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private JavaMailSender mailSender;
    
    private static final Long DEFAULT_EXPIRE_TIME = 60L * 5; // 5 minutes
    private static final String DEFAULT_MAIL_SERVER = "huang_qw@qq.com";
    private static final String DEFAULT_CODE_MAIL_TITLE = "iPan code";
    private static final String DEFAULT_CODE_MAIL_PREFIX = "Your code for binding email address in iPan is: ";
    private static final String DEFAULT_CODE_MAIL_SUFFIX = ". The code will expire after 5 minutes";
    
    @SuppressWarnings("rawtypes")
    @Override
    public Result sendCode(String email, String code) {
        if (code == null) {
            code = this.generateCode();
        }
        redisUtil.set(email, code, DEFAULT_EXPIRE_TIME);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject(DEFAULT_CODE_MAIL_TITLE);
        simpleMailMessage.setText(DEFAULT_CODE_MAIL_PREFIX + code + DEFAULT_CODE_MAIL_SUFFIX);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setFrom(DEFAULT_MAIL_SERVER);
        mailSender.send(simpleMailMessage);
        return resultUtil.success();
    }

    @Override
    public String generateCode() {
        return kaptchaProducer.createText();
    }

    @Override
    public Boolean verifyCodeByEmail(String email, String code) {
        Boolean ok;
        try {
            ok = redisUtil.get(email).equals(code);
        } catch (NullPointerException e) {
            return false;
        }
        return ok;
    }

}
