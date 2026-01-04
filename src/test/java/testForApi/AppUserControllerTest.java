package testForApi;

import com.alibaba.fastjson.JSON;
import com.winter.app.controller.AppUserController;
import com.winter.app.mapper.ImUserMapper;
import com.winter.app.pojo.form.AppUserRegistryForm;
import com.winter.pojo.Response;
import com.winter.pojo.entity.ImUser;
import com.winter.utils.LoginUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AppUserController测试类
 * 测试用户注册API的各种场景
 */
@RunWith(SpringRunner.class)
public class AppUserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ImUserMapper imUserMapper;

    @InjectMocks
    private AppUserController appUserController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        // 添加验证支持
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator);
        
        mockMvc = MockMvcBuilders.standaloneSetup(appUserController)
                .setValidator(validator)
                .build();
    }

    @Test
    public void testRegistrySuccess() throws Exception {
        // 准备测试数据
        AppUserRegistryForm form = new AppUserRegistryForm();
        form.setUsername("testuser");
        form.setPassword("Test123!");
        form.setNickname("测试用户");

        // 模拟用户不存在
        when(imUserMapper.selectOne(any())).thenReturn(null);
        // 模拟插入成功
        when(imUserMapper.insert(any(ImUser.class))).thenReturn(1);

        // 执行请求并验证结果
        mockMvc.perform(post("/app/user/registry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("注册成功"));

        // 验证方法调用
        verify(imUserMapper, times(1)).selectOne(any());
        verify(imUserMapper, times(1)).insert(any(ImUser.class));
    }

    @Test
    public void testRegistryWithExistingUsername() throws Exception {
        // 准备测试数据
        AppUserRegistryForm form = new AppUserRegistryForm();
        form.setUsername("existinguser");
        form.setPassword("Test123!");
        form.setNickname("测试用户");

        // 模拟用户已存在
        ImUser existingUser = new ImUser();
        existingUser.setId(1);
        existingUser.setUserName("existinguser");
        when(imUserMapper.selectOne(any())).thenReturn(existingUser);

        // 执行请求并验证结果
        mockMvc.perform(post("/app/user/registry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.message").value("账号已存在"));

        // 验证方法调用
        verify(imUserMapper, times(1)).selectOne(any());
        verify(imUserMapper, never()).insert(any(ImUser.class));
    }

    @Test
    public void testRegistryWithEmptyUsername() throws Exception {
        // 准备测试数据
        AppUserRegistryForm form = new AppUserRegistryForm();
        form.setUsername("");
        form.setPassword("Test123!");
        form.setNickname("测试用户");

        // 执行请求并验证结果
        mockMvc.perform(post("/app/user/registry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(form)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegistryWithEmptyPassword() throws Exception {
        // 准备测试数据
        AppUserRegistryForm form = new AppUserRegistryForm();
        form.setUsername("testuser");
        form.setPassword("");
        form.setNickname("测试用户");

        // 执行请求并验证结果
        mockMvc.perform(post("/app/user/registry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(form)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegistryWithEmptyNickname() throws Exception {
        // 准备测试数据
        AppUserRegistryForm form = new AppUserRegistryForm();
        form.setUsername("testuser");
        form.setPassword("Test123!");
        form.setNickname("");

        // 执行请求并验证结果
        mockMvc.perform(post("/app/user/registry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(form)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegistryWithShortUsername() throws Exception {
        // 准备测试数据
        AppUserRegistryForm form = new AppUserRegistryForm();
        form.setUsername("test"); // 少于6个字符
        form.setPassword("Test123!");
        form.setNickname("测试用户");

        // 执行请求并验证结果
        mockMvc.perform(post("/app/user/registry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(form)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegistryWithShortPassword() throws Exception {
        // 准备测试数据
        AppUserRegistryForm form = new AppUserRegistryForm();
        form.setUsername("testuser");
        form.setPassword("Test1"); // 少于6个字符
        form.setNickname("测试用户");

        // 执行请求并验证结果
        mockMvc.perform(post("/app/user/registry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(form)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegistryWithInvalidPasswordFormat() throws Exception {
        // 准备测试数据
        AppUserRegistryForm form = new AppUserRegistryForm();
        form.setUsername("testuser");
        form.setPassword("testpassword"); // 只包含字母，不符合格式要求
        form.setNickname("测试用户");

        // 模拟用户不存在
        when(imUserMapper.selectOne(any())).thenReturn(null);
        // 模拟插入成功
        when(imUserMapper.insert(any(ImUser.class))).thenReturn(1);

        // 执行请求并验证结果
        mockMvc.perform(post("/app/user/registry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("注册成功"));
    }

    @Test
    public void testRegistryWithLongNickname() throws Exception {
        // 准备测试数据
        AppUserRegistryForm form = new AppUserRegistryForm();
        form.setUsername("testuser");
        form.setPassword("Test123!");
        form.setNickname("这是一个非常非常长的昵称，超过了二十个字符的限制"); // 超过20个字符

        // 执行请求并验证结果
        mockMvc.perform(post("/app/user/registry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(form)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPasswordEncryption() {
        // 准备测试数据
        String password = "Test123!";
        String salt = "abc123";
        
        // 测试密码加密
        String encryptedPassword = LoginUtil.formPwdToDbPwd(password, salt);
        
        // 验证加密后的密码不为空且不等于原始密码
        assertNotNull(encryptedPassword);
        assertNotEquals(password, encryptedPassword);
        
        // 验证相同输入产生相同输出
        String encryptedPassword2 = LoginUtil.formPwdToDbPwd(password, salt);
        assertEquals(encryptedPassword, encryptedPassword2);
        
        // 验证不同盐值产生不同输出
        String encryptedPassword3 = LoginUtil.formPwdToDbPwd(password, "def456");
        assertNotEquals(encryptedPassword, encryptedPassword3);
    }

    @Test
    public void testTrimFunction() {
        // 准备测试数据
        AppUserRegistryForm form = new AppUserRegistryForm();
        
        // 测试用户名trim
        form.setUsername("  testuser  ");
        assertEquals("testuser", form.getUsername());
        
        // 测试昵称trim
        form.setNickname("  测试用户  ");
        assertEquals("测试用户", form.getNickname());
        
        // 测试null值处理
        form.setUsername(null);
        assertNull(form.getUsername());
        
        form.setNickname(null);
        assertNull(form.getNickname());
    }
}