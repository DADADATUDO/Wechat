package com.winter.pojo;

import com.winter.app.enums.ResponseType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "通用响应体")
public class Response<T> {
    @ApiModelProperty(value = "状态码", example = "200")
    private String code;
    @ApiModelProperty(value = "响应消息", example = "操作成功")
    private String message;
    @ApiModelProperty(value = "响应数据")
    private T data;

    private Response(ResponseType responseType, String message, T data){
        this.code = responseType.getCode();
        this.message = message==null?responseType.getMessage():message;
        this.data = data;
    }

    // 成功响应
    public static <T> Response<T> success(){return new Response<>(ResponseType.SUCCESS,null,null);}
    public static <T> Response<T> success(T data){return new Response<>(ResponseType.SUCCESS,null,data);}
    public static <T> Response<T> success(String message,T data){return new Response<>(ResponseType.SUCCESS,message,data);}
    public static <T> Response<T> success(String message){return new Response<>(ResponseType.SUCCESS,message,null);}

    // 失败响应
    public static <T> Response<T> fail(){return new Response<>(ResponseType.FAILED,null,null);}
    public static <T> Response<T> fail(String message){return new Response<>(ResponseType.FAILED,message,null);}
    public static <T> Response<T> fail(T data){return new Response<>(ResponseType.FAILED,null,data);}
    public static <T> Response<T> fail(ResponseType type){return new Response<>(type,null,null);}
    public static <T> Response<T> fail(String message,T data){return new Response<>(ResponseType.FAILED,message,data);}


}
