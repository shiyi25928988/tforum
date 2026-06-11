package cc.shiyi.coleditor.common.config;

import cc.shiyi.coleditor.common.http.ResponseWrapper;
import cn.dev33.satoken.exception.NotLoginException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobleExceptionHandler {

    /**
     * 处理未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public ResponseWrapper<?> handleNotLoginException(NotLoginException e) {
        ResponseWrapper<?> responseWrapper = new ResponseWrapper<>();
        // 使用code 401表示未登录
        responseWrapper.setCode(401);
        responseWrapper.setMessage("登录已失效，请重新登录");
        return responseWrapper;
    }

    @ExceptionHandler(Exception.class)
    public ResponseWrapper<Exception> handleExposableException(Exception e) {
        ResponseWrapper<Exception> responseWrapper = new ResponseWrapper<>();
        return responseWrapper.fail(e, e.getMessage());
    }
}
