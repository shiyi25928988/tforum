package cc.shiyi.coleditor.common.config;

import cc.shiyi.coleditor.common.http.ResponseWrapper;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobleExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseWrapper<Exception> handleExposableException(Exception e) {
        ResponseWrapper<Exception> responseWrapper = new ResponseWrapper<>();
        return responseWrapper.fail(e, e.getMessage());
    }
}
