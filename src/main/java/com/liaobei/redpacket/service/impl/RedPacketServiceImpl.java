package com.liaobei.redpacket.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.liaobei.redpacket.common.exception.RedPacketException;
import com.liaobei.redpacket.common.lock.RedisLock;
import com.liaobei.redpacket.common.lock.RedisLockHelper;
import com.liaobei.redpacket.common.po.AccountPO;
import com.liaobei.redpacket.common.po.RedPacketPO;
import com.liaobei.redpacket.common.request.DispatchRequest;
import com.liaobei.redpacket.common.request.GrabRequest;
import com.liaobei.redpacket.common.response.BaseResponse;
import com.liaobei.redpacket.common.response.GrabResponse;
import com.liaobei.redpacket.common.response.MessageResponse;
import com.liaobei.redpacket.common.utils.BigDecimalUtils;
import com.liaobei.redpacket.common.utils.ConvertUtils;
import com.liaobei.redpacket.common.utils.RedisUtils;
import com.liaobei.redpacket.dao.AccountMapper;
import com.liaobei.redpacket.dao.RedPacketMapper;
import com.liaobei.redpacket.service.RedPacketService;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: liaobei
 */
@Service
@Slf4j
public class RedPacketServiceImpl implements RedPacketService {
    private static final String RED_PACKET_KEY_PREFIX = "redPacketCacheKey";
    private static final String GRAB_RED_PACKET_KEY_PREFIX = "grabRedPacketCacheKey";
    private static final String REDIS_LOCK_SUFFIX = "redisLockSuffix";
    private static ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);

    static {
        executor.schedule(() -> {
            // todo

        }, 1, TimeUnit.HOURS);
    }

    @Resource
    private RedPacketMapper redPacketMapper;
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RedisLockHelper redisLockHelper;



    @Override
    @Transactional(rollbackOn = Exception.class)
    public BaseResponse dispatch(DispatchRequest request) {
        redisLockHelper.lock(geneKey(REDIS_LOCK_SUFFIX, request.getUserId().toString()), 3000);
        validate(request);
        RedPacketPO redPacketPO = ConvertUtils.convert(request, RedPacketPO.class);
        redPacketMapper.insert(redPacketPO);
        redisUtils.set(redPacketPO.getId().toString(), redPacketPO.getTotal(), 10);

        List<BigDecimal> diffList = initIndex(request.getCount(), redPacketPO.getTotal());

        BigDecimal total = new BigDecimal(redPacketPO.getTotal().toString());
        BigDecimal consume = new BigDecimal("0");
        for (BigDecimal d : diffList) {
            String small = total.multiply(new BigDecimal(d.toString())).toString();
            log.debug("small is {}", small);

            consume = consume.add(new BigDecimal(Double.valueOf(small).toString()));
            redisUtils.lSet(geneKey(RED_PACKET_KEY_PREFIX, redPacketPO.getId().toString()), Double.valueOf(small));
        }
        log.debug("small is {}", total.subtract(new BigDecimal(consume.toString())).doubleValue());
        redisUtils.lSet(geneKey(RED_PACKET_KEY_PREFIX, redPacketPO.getId().toString()), total.subtract(consume).doubleValue());
        redisLockHelper.unlock(geneKey(REDIS_LOCK_SUFFIX, request.getUserId().toString()));
        return MessageResponse.success("发送成功");
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public BaseResponse grab(GrabRequest request) {
        redisLockHelper.lock(geneKey(REDIS_LOCK_SUFFIX, request.getUserId().toString()), 3000);
        // 参数校验
        grabArgsValidate(request);
        Double count = Double.valueOf(redisUtils.rifhtPop(geneKey(RED_PACKET_KEY_PREFIX, request.getRedPacketId().toString())).toString());
        AccountPO accountPO = accountMapper.selectById(request.getUserId());
        accountPO.setBalance(new BigDecimal(accountPO.getBalance().toString()).add(new BigDecimal(count.toString())).doubleValue());
        accountMapper.updateById(accountPO);

        RedPacketPO redPacketPO = redPacketMapper.selectById(request.getRedPacketId());
        redPacketPO.setGrabCount(redPacketPO.getGrabCount() + 1);
        redPacketPO.setGrabTotal(BigDecimalUtils.doubleAdd(redPacketPO.getGrabTotal(), count));
        redPacketMapper.updateById(redPacketPO);
        redisUtils.hset(geneKey(GRAB_RED_PACKET_KEY_PREFIX), request.getRedPacketId().toString(), request.getUserId());
        redisLockHelper.unlock(geneKey(REDIS_LOCK_SUFFIX, request.getUserId().toString()));
        return GrabResponse.success(count);
    }

    private void grabArgsValidate(GrabRequest request) {
        if (request.getUserId().equals(
                Long.valueOf(Optional.ofNullable(redisUtils.hget(GRAB_RED_PACKET_KEY_PREFIX, request.getRedPacketId().toString())).orElse(-1).toString()))) {
            throw new RedPacketException("已领取");
        }
        RedPacketPO redPacketPO = redPacketMapper.selectById(request.getRedPacketId());
        if (redPacketPO.getGrabCount().equals(redPacketPO.getCount()) || redPacketPO.getGrabTotal().equals(redPacketPO.getTotal())) {
            throw new RedPacketException("红包已经被抢完了");
        }
    }

    private String geneKey(String... args) {
        return Joiner.on(";").join(args);
    }

    /**
     * 参数校验
     *
     * @param request
     */
    private void validate(DispatchRequest request) {
        if (request.getTotal() < 1) {
            throw new RedPacketException("金额需大于1元");
        }
        if (request.getCount() > 100) {
            throw new RedPacketException("红包数量不能超过100");
        }
        // if (StringUtils.isEmpty(request.getRemark())) {
        // throw new RedPacketException("备注不能为空");
        // }
        AccountPO accountPO = accountMapper.selectById(request.getUserId());
        Double left;
        if ((left = (new BigDecimal(accountPO.getBalance().toString()).subtract(new BigDecimal(request.getTotal().toString())).doubleValue())) < 0) {
            throw new RedPacketException("余额不足");
        }
        accountPO.setBalance(left);
        accountMapper.updateById(accountPO);
    }

    private List<BigDecimal> initIndex(Integer count, Double total) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Double> indexList = Lists.newArrayList();
        while (indexList.size() < count - 1) {
            Double element = (double) random.nextInt(1, (int) (total * 100)) / (total * 100);
            if (!indexList.contains(element)) {
                indexList.add(element);
            }
        }
        Collections.sort(indexList);

        List<BigDecimal> diffList = Lists.newArrayList();
        for (int i = 0; i < count - 1;) {
            if (i == 0) {
                diffList.add(new BigDecimal(indexList.get(0).toString()));
                i++;
                continue;
            }

            BigDecimal decimal = new BigDecimal(indexList.get(i).toString()).subtract(new BigDecimal(indexList.get(i - 1).toString()));
            diffList.add(decimal);
            i++;
        }

        return diffList;
    }
}
