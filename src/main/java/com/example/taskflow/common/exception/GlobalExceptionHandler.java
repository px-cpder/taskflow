package com.example.taskflow.common.exception;

import com.example.taskflow.common.result.ApiResult;
import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一捕获和处理Controller层抛出的各类异常，返回标准化的错误响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param e 业务异常对象
     * @return 状态码400的错误响应，包含业务异常消息
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResult<Void> handleBusinessException(BusinessException e) {
        return ApiResult.fail(400, e.getMessage());
    }

    /**
     * 处理@Valid注解参数校验异常（用于@RequestBody）
     * 提取第一个字段校验错误信息作为响应消息
     *
     * @param e 方法参数校验异常对象
     * @return 状态码400的错误响应，包含首个字段的校验错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("参数校验失败");

        return ApiResult.fail(400, message);
    }

    /**
     * 处理参数绑定异常（用于表单提交）
     * 提取第一个字段绑定错误信息作为响应消息
     *
     * @param e 参数绑定异常对象
     * @return 状态码400的错误响应，包含首个字段的绑定错误信息
     */
    @ExceptionHandler(BindException.class)
    public ApiResult<Void> handleBindException(BindException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("参数绑定失败");

        return ApiResult.fail(400, message);
    }

    /**
     * 处理约束违反异常（用于@RequestParam、@PathVariable等参数校验）
     *
     * @param e 约束违反异常对象
     * @return 状态码400的错误响应，包含约束违反消息
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult<Void> handleConstraintViolationException(ConstraintViolationException e) {
        return ApiResult.fail(400, e.getMessage());
    }

    /**
     * 处理未预见的系统级异常
     * 打印异常堆栈信息便于排查问题
     *
     * @param e 通用异常对象
     * @return 状态码500的错误响应，包含系统异常消息
     */
    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handleException(Exception e) {
        e.printStackTrace();
        return ApiResult.fail(500, "系统异常：" + e.getMessage());
    }
}
