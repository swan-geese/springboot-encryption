package io.github.swangeese.springbootencryption.config;

//import com.paraview.aws.trace.sync.TraceableTaskDecorator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2023/4/25 11:09 AM
 * @description 次要/不重要数据线程池配置,该线程池中最好不要做核心业务的处理，否则会导致业务处理不了
 * 预留功能：当工作队列已满,线程数为最大线程数的时候,抛弃任务并抛出RejectedExecutionException异常时进行告警处理
 */
@EnableAsync
@Configuration
public class MinorThreadPoolConfig {

    @Value("${threadPool.minor.corePoolSize:20}")
    private Integer corePoolSize;

    @Value("${threadPool.minor.maxPoolSize:50}")
    private Integer maxPoolSize;

    @Value("${threadPool.minor.queueCapacity:10000}")
    private Integer queueCapacity;

    @Bean(name = "minorThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor minorThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        // 设置最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        // 设置工作队列大小
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        // 设置线程名称前缀
        threadPoolTaskExecutor.setThreadNamePrefix("audit-consumer minorThreadPool-->");
        // 设置线程池包装类，将全链路ID传递到子线程中
//        threadPoolTaskExecutor.setTaskDecorator(new TraceableTaskDecorator());
        // 设置拒绝策略.当工作队列已满,线程数为最大线程数的时候,抛弃任务并抛出RejectedExecutionException异常
        //注意：因此该线程池中最好不要做核心业务的处理，否则会导致业务处理不了
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 初始化线程池
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Bean(name = "pasgAuditThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        // 设置最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        // 设置工作队列大小
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        // 设置线程名称前缀
        threadPoolTaskExecutor.setThreadNamePrefix("pasg-audit-consumer threadPool-->");
        // 设置线程池包装类，将全链路ID传递到子线程中
//        threadPoolTaskExecutor.setTaskDecorator(new TraceableTaskDecorator());
        // 设置拒绝策略.当工作队列已满,线程数为最大线程数的时候, 拒绝接收新的请求
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 初始化线程池
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
