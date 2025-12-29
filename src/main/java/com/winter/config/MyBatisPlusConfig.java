package com.winter.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * 用于配置 MyBatis-Plus 的全局行为，如分页插件、逻辑删除插件等
 */
@Configuration
public class MyBatisPlusConfig {
    
    /**
     * 配置 MyBatis-Plus 拦截器
     * 添加分页插件以支持自动分页功能
     * 
     * @return MybatisPlusInterceptor 实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 创建分页插件实例
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        
        // 设置数据库类型为 MySQL，用于适配不同数据库的分页语法
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        
        // 设置溢出总页数后是否进行处理，默认为 false
        // 当设置为 true 时，如果页码超过总页数，则显示最后一页的内容
        paginationInnerInterceptor.setOverflow(true);

        // 将分页插件添加到拦截器链中
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }

}