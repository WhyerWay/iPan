package indi.ipan.result;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@Component
public class ResultUtil {
    public Result success(Object object) {
        Result result = new Result();
        result.setCode(200);
        result.setMessage("success");
        result.setData(object);
        return result;
    }

    public Result success(Page page) {
        Result result = new Result();
        result.setCode(200);
        result.setMessage("success");
        result.setData(page.getRecords());
        result.setCount(page.getTotal());
        return result;
    }

    public Result success() {
        Result result = new Result();
        result.setCode(200);
        result.setMessage("success");
        result.setData(null);
        result.setCount(1L);
        return result;
    }

    public static Result error(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMessage(msg);
        return result;
    }

//     public static Result error(BindingResult bindingResult){
//            StringBuffer sb = new StringBuffer();
//            Result result = new Result();
//            result.setCode(400);
//            String msg;
//            for(int i=0;i<bindingResult.getAllErrors().size();i++){
//                sb.append(bindingResult.getFieldErrors().get(i).getDefaultMessage()).append(",");
//            }
//            result.setMessage(sb.toString());
//            return result;
//        }
}
