package indi.ipan.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.dao.DuplicateKeyException;

import indi.ipan.result.Result;
import indi.ipan.result.ResultUtil;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result handle(Exception e) {
        if (e instanceof CustomizedExcption) {
            CustomizedExcption customizedExcption = (CustomizedExcption) e;
            return ResultUtil.error(customizedExcption.getCode(), customizedExcption.getMessage());
        } else if (e instanceof DuplicateKeyException) {
            return ResultUtil.error(10002, "Error: Duplicate primary key");
        } else {
            logger.error("Error: Unexpected error", e);
            return ResultUtil.error(500, "Error: Unexpected error");
        }
    }
}
