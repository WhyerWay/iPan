package indi.ipan.exception;

public enum ResultEnum {
    UNKONW_ERROR(-1, "Error: Unexpected error") // used in unreachable else branch
    , SUCCESS(0, "Success")
    , INVALID_INPUT(444, "Error: Invalid input parameter")
    , UNEXPECTED_DATABASE_OPERATION_RESULT(555, "Error: Unexpected database operation result")
    , FILE_SYSTEM_OPERATION_ERROR(556, "Error: File system operation fail")
    , TARGET_FILE_NOT_EXIST(556, "Error: Target file does not exist")
    , EMPTY_FILE_ERROR(557, "Error: Empty file")
    , MISMATCH_RESULT_OF_DATABASE_AND_FILE_SYSTEM(558, "Error: Mismatch operation result of database and file system");
    
    private Integer code;
    private String msg;
    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

//
//    public Integer getCode() {
//        return code;
//    }
//
//    public String getMsg() {
//        return msg;
//    }
}
