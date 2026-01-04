Swagger2 注解使用指南

### 1.1 类与配置层注解

#### `@EnableSwagger2`

- **位置:** 放在配置类（`Configuration`）上。
- **作用:** 开启 Swagger2 的自动配置功能。

#### `@Api`

- **位置:** 放在 Controller 类上。
- **作用:** 说明该 Controller 的主要职责（例如：用户管理模块）。
- **核心参数:**
  - `tags`: 标签，用于在 UI 界面上给接口分组。
  - `description`: (已废弃，建议用 tags) 描述。

```Java
package com.example.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
// 【重点】tags 用来在 Swagger 页面左侧菜单分组显示
@Api(tags = "用户管理模块")
public class UserController {
    // ... 接口方法
}
```

### 1.2 方法与接口层注解 (最常用)

这是您写 Controller 时最常打交道的部分。

#### `@ApiOperation`

- **位置:** Controller 的方法上。
- **作用:** 描述具体的一个接口功能。
- **核心参数:**
  - `value`: 接口的简要说明（如 "获取用户列表"）。
  - `notes`: 接口的详细备注（如 "仅限管理员权限"）。

#### `@ApiImplicitParam` & `@ApiImplicitParams`

- **位置:** Controller 的方法上。
- **作用:** 描述接口的入参（特别是 Query 参数或 Path 参数）。
- **核心参数:**
  - `name`: 参数名（必须与代码中变量名一致）。
  - `value`: 参数说明。
  - `required`: 是否必填 (true / false)。
  - `dataType`: 数据类型 (String, Integer 等)。
  - `paramType`: 参数存放位置 (非常重要！):
    - `query`: `?id=1` 这种形式。
    - `path`: `/user/{id}` 这种形式。
    - `body`: POST 请求体（通常配合 `@RequestBody`，但建议用 `@ApiModel` 替代此方式）。
    - `header`: 请求头。

**代码示例:**

```Java
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@GetMapping("/detail/{id}")
@ApiOperation(value = "查询用户详情", notes = "根据用户ID获取详细信息")
@ApiImplicitParams({
    @ApiImplicitParam(
        name = "id", 
        value = "用户ID", 
        required = true, 
        dataType = "Long", 
        paramType = "path",  // 因为在 URL 路径中
        example = "1001"
    )
})
public String getUserById(@PathVariable Long id) {
    return "用户详情...";
}
```

------

### 1.3 实体与模型层注解 (DTO / VO)

当您的接口使用 `@RequestBody` 接收对象，或者返回一个对象给前端时，这些注解非常关键。

#### `@ApiModel`

- **位置:** 实体类 (POJO/DTO/VO) 上。
- **作用:** 描述这个类是做什么的。

#### `@ApiModelProperty`

- **位置:** 实体类的字段上。
- **作用:** 描述字段含义、是否必填、示例值。
- **核心参数:**
  - `value`: 字段描述。
  - `required`: 是否必填。
  - `example`: 示例值（前端调试时会自动填充这个值，非常方便）。
  - `hidden`: 是否在文档中隐藏该字段。

**代码示例:**

```Java
package com.example.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "UserLoginDTO", description = "用户登录参数对象")
public class UserLoginDTO {

    @ApiModelProperty(value = "用户名", required = true, example = "admin")
    private String username;

    @ApiModelProperty(value = "密码", required = true, example = "123456")
    private String password;
    
    @ApiModelProperty(value = "内部版本号", hidden = true) // 前端看不到这个
    private String version;
}
```

------

## 4. 完整综合示例

下面展示一个标准的 Controller 写法，结合了上述所有知识点。

```Java
package com.example.controller;

import com.example.entity.UserLoginDTO;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Api(tags = "认证中心模块") // 1. 标记模块
public class AuthController {

    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "返回Token令牌") // 2. 标记方法
    // 3. 描述响应码 (可选，但推荐)
    @ApiResponses({
        @ApiResponse(code = 200, message = "登录成功"),
        @ApiResponse(code = 400, message = "参数错误"),
        @ApiResponse(code = 401, message = "密码错误")
    })
    // 这里的 UserLoginDTO 内部已经使用了 @ApiModelProperty
    public String login(@RequestBody UserLoginDTO loginDTO) {
        return "Login Success: " + loginDTO.getUsername();
    }

    @GetMapping("/check")
    @ApiOperation(value = "检查Token有效性")
    @ApiImplicitParam(
        name = "token", 
        value = "鉴权令牌", 
        required = true, 
        dataType = "String", 
        paramType = "query" // 对应 URL: /check?token=xxx
    )
    public boolean checkToken(@RequestParam String token) {
        return true;
    }
}
```

------

## 5. 常见易错点与提示 (Tips)

1. **`paramType` 填错导致接收不到参数**

   - 如果您的 Java 代码用 `@PathVariable`，Swagger 里必须写 `paramType = "path"`。
   - 如果您的 Java 代码用 `@RequestParam`，Swagger 里通常写 `paramType = "query"`。

2. **生产环境必须关闭**

   - Swagger 文档暴露了后端的所有接口细节。在项目上线（Production）时，务必在配置中通过 `@Profile({"dev", "test"})` 或配置文件属性来禁用 Swagger，防止安全泄露。

3. **UI 界面访问地址**

   - Swagger2 的默认访问地址通常是：`http://localhost:8080/swagger-ui.html`
   - 如果您配置了 Context Path (如 `/api`)，地址则是：`http://localhost:8080/api/swagger-ui.html`

4. **实体类更新不及时**

   - 如果您修改了实体类的字段（比如把 `name` 改成了 `username`），记得同时更新 `@ApiModelProperty` 的描述，否则前端看文档时会很困惑。

   -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



# 主要注解介绍

# controller层
```java
@Api(tags = "用户管理", description = "用户相关的API接口")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @ApiOperation(
        value = "获取用户列表", 
        notes = "分页获取用户列表信息，支持用户名模糊查询",
        response = Response.class
    )
    @ApiImplicitParams({
        @ApiImplicitParam(name = "username", value = "用户名", paramType = "query", dataType = "string"),
        @ApiImplicitParam(name = "page", value = "页码", paramType = "query", dataType = "int", defaultValue = "1"),
        @ApiImplicitParam(name = "size", value = "每页数量", paramType = "query", dataType = "int", defaultValue = "10")
    })
    @GetMapping
    public Response<List<User>> getUsers(
        @RequestParam(required = false) String username,
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer size
    ) {
        // 实现逻辑
        return Response.success(null);
    }

    @ApiOperation(
        value = "获取用户详情", 
        notes = "根据用户ID获取用户详细信息",
        response = User.class
    )
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, paramType = "path", dataType = "long")
    @GetMapping("/{id}")
    public Response<User> getUserById(
        @ApiParam(value = "用户ID", required = true) 
        @PathVariable Long id
    ) {
        // 实现逻辑
        return Response.success(null);
    }

    @ApiOperation(
        value = "创建用户", 
        notes = "创建一个新的用户",
        response = User.class
    )
    @PostMapping
    public Response<User> createUser(
        @ApiParam(value = "用户信息", required = true) 
        @RequestBody @Valid User user
    ) {
        // 实现逻辑
        return Response.success(user);
    }

    @ApiOperation(
        value = "更新用户", 
        notes = "根据用户ID更新用户信息",
        response = User.class
    )
    @PutMapping("/{id}")
    public Response<User> updateUser(
        @ApiParam(value = "用户ID", required = true) 
        @PathVariable Long id,
        @ApiParam(value = "用户更新信息", required = true) 
        @RequestBody @Valid User user
    ) {
        // 实现逻辑
        return Response.success(user);
    }

    @ApiOperation(
        value = "删除用户", 
        notes = "根据用户ID删除用户"
    )
    @DeleteMapping("/{id}")
    public Response<Void> deleteUser(
        @ApiParam(value = "用户ID", required = true) 
        @PathVariable Long id
    ) {
        // 实现逻辑
        return Response.success();
    }
}
```

# entity层
```java
@ApiModel(description = "用户信息实体")
public class User {
    
    @ApiModelProperty(value = "用户ID", example = "1", required = true, position = 1)
    private Long id;
    
    @ApiModelProperty(value = "用户名", example = "张三", required = true, position = 2)
    private String username;
    
    @ApiModelProperty(value = "真实姓名", example = "张三", position = 3)
    private String realName;
    
    @ApiModelProperty(value = "邮箱", example = "zhangsan@example.com", position = 4)
    private String email;
    
    @ApiModelProperty(value = "手机号", example = "13800138000", position = 5)
    private String phone;
    
    @ApiModelProperty(value = "年龄", example = "25", position = 6)
    private Integer age;
    
    @ApiModelProperty(value = "性别", example = "1", notes = "0-女，1-男", position = 7)
    private Integer gender;
    
    @ApiModelProperty(value = "头像URL", example = "http://example.com/avatar.jpg", position = 8)
    private String avatar;
    
    @ApiModelProperty(value = "状态", example = "1", notes = "0-禁用，1-启用", position = 9)
    private Integer status;
    
    @ApiModelProperty(value = "创建时间", position = 10)
    private Date createTime;
    
    @ApiModelProperty(value = "更新时间", position = 11)
    private Date updateTime;
    
    // getter和setter方法
}
```

# 响应层
```java
@ApiModel(description = "通用响应体")
public class Response<T> {
    
    @ApiModelProperty(value = "状态码", example = "200", required = true)
    private Integer code;
    
    @ApiModelProperty(value = "消息", example = "操作成功", required = true)
    private String message;
    
    @ApiModelProperty(value = "数据")
    private T data;
    
    @ApiModelProperty(value = "时间戳", example = "1609459200000")
    private Long timestamp;
    
    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setCode(200);
        response.setMessage("操作成功");
        response.setData(data);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
    
    public static <T> Response<T> fail(String message) {
        Response<T> response = new Response<>();
        response.setCode(500);
        response.setMessage(message);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
    
    // getter和setter方法
}
```
# 配置类
```java
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.winter.app.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("用户管理系统API")
                .description("用户管理系统的API文档")
                .contact(new Contact("开发团队", "http://www.example.com", "dev@example.com"))
                .version("1.0")
                .build();
    }
}```