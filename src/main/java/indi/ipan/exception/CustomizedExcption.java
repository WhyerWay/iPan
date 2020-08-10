package indi.ipan.exception;

import org.springframework.stereotype.Component;

@Component
public class CustomizedExcption extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private Integer code;
    
    public CustomizedExcption(ResultEnum resultEnum) {
        super(resultEnum.getMsg()); //TODO guess used to call parent's constructor
        this.code = resultEnum.getCode();
    }
    public CustomizedExcption() {}
    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
}
