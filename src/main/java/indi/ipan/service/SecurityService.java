package indi.ipan.service;

import indi.ipan.result.Result;

public interface SecurityService {
    
    /**
     * send code to given email
     * @param email email address
     * @param code code, can be null
     * @return (200, success, 1) if success, others if not
     */
    @SuppressWarnings("rawtypes")
    public Result sendCode(String email, String code);
    /**
     * generate 6 numbers or characters code by default 
     * @return code
     */
    public String generateCode();
    /**
     * verify code for given email
     * @param email email address
     * @param code code
     * @return true if success, false if not
     */
    public Boolean verifyCodeByEmail(String email, String code);
}
