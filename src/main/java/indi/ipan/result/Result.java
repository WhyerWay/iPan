package indi.ipan.result;


import org.springframework.stereotype.Component;

@Component
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private Long count;
    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
    public Long getCount() {
        return count;
    }
    public void setCount(Long count) {
        this.count = count;
    }
    @Override
    public String toString() {
        if (data != null) {
            return "Result[code=" + code 
                    + ",message=" + message 
                    + ",count=" + count 
                    + ",data=" + data.toString() 
                    + "]";
        }else {
            return "Result[code=" + code 
                    + ",message=" + message 
                    + ",count=" + count 
                    + "]";
        }
        
    }
}
