package org.example.test.infrastructure;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.vo.RuleTreeVO;
import org.example.domain.strategy.model.vo.StrategyAwardStockKeyVO;
import org.example.domain.strategy.repository.IStrategyRepository;
import org.example.infrastructure.persistent.redis.IRedisService;
import org.example.types.common.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

//策略仓储测试
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyRepositoryTest {

    @Resource
    private IStrategyRepository strategyRepository;
    @Resource
    private IRedisService redisService;
//规则树
    @Test
    public void queryRuleTreeVOByTreeId() {
        RuleTreeVO ruleTreeVO = strategyRepository.queryRuleTreeVOByTreeId("tree_lock");
        log.info("测试结果：{}", JSON.toJSONString(ruleTreeVO));
    }

    @Test
    public void test_cacheStrategyAwardCount() {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + "300001" + Constants.UNDERLINE + "102";
        strategyRepository.cacheStrategyAwardCount(cacheKey, 10);
    }

    @Test
    public void test_subtractionAwardStock() {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + "300001" + Constants.UNDERLINE + "102";
        strategyRepository.subtractionAwardStock(cacheKey);
    }

    @Test
    public void test_getValue() {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + "300001" + Constants.UNDERLINE + "102";
        Integer value = redisService.getValue(cacheKey);
        log.info("测试结果：{}", value);
    }

    @Test
    public void test_queue() throws InterruptedException {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUERY_KEY;
        RBlockingQueue<StrategyAwardStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        RDelayedQueue<StrategyAwardStockKeyVO> delayedQueue = redisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(StrategyAwardStockKeyVO.builder()
                .strategyId(100001L)
                .awardId(102)
                .build(), 3, TimeUnit.SECONDS);

        new CountDownLatch(1).await();
    }

}

