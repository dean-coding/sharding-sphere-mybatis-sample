package com.example.ds;

import com.example.ds.mapper.UserMapper;
import com.example.ds.mapper.UserNoShardingMapper;
import com.example.ds.model.User;
import com.example.ds.model.UserNoSharding;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShardingSphereMybatisSampleApplicationTests {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserNoShardingMapper userNoShardingMapper;

    @Test
    public void testInsertNoSharding() {
        userNoShardingMapper.createTableIfNotExists();
        userNoShardingMapper.truncateTable();
        int recordSize = 10;
        for (int i = 0; i < recordSize; i++) {
            userNoShardingMapper.insert(new UserNoSharding("dean_" + i, "pwd_" + i, "query_" + i));
        }

        List<UserNoSharding> userListNoSharding = userNoShardingMapper.selectAll();
        Assert.assertNotNull(userListNoSharding);
        Assert.assertEquals(recordSize, userListNoSharding.size());

    }


    @Test
    public void testInsertSharding() {
        userMapper.truncateTable();
        List<User> users = Lists.newArrayList();
        int recordSize = 10;
        for (int i = 0; i < recordSize; i++) {
            users.add(new User("dean_" + i, "pwd_" + i, "query_" + i));
        }
        Long result = userMapper.insertBatch(users);
        Assert.assertNotNull(result);
        Assert.assertEquals(recordSize, result.intValue());
    }


    @Test
    public void testExistsSharding() {
        Set<String> names = new HashSet<>();
        // 命中
        names.add("dean_1");
        names.add("dean_2");
        names.add("dean_3");
        // 未命中
        names.add("dean_10");
        names.add("dean_11");
        names.add("dean_12");
        int existsByName = userMapper.existsByName(names);
        Assert.assertEquals(existsByName, 3);
    }


    @Test
    public void testUpdateSharding() {
        // 重置密码pwd=12345
        List<String> dataList = Arrays.asList("dean_1", "dean_2", "dean_3", "dean_7");
        Long aLong = userMapper.resetInitPwdByNames(dataList);
        Assert.assertNotNull(aLong);
        Assert.assertEquals(aLong.intValue(), dataList.size());
    }


    @Test
    public void testLimitSharding() {
        int pageSize = 5;
        List<User> usersPage0 = userMapper.selectByLimit(0, pageSize);
        Assert.assertNotNull(usersPage0);
        Assert.assertEquals(usersPage0.size(), pageSize);
    }


    @Test
    public void testShardingVsNo() {
        int recordSize = 10000;
        testDb(recordSize);
        testDbBySharding(recordSize);
    }

    private void testDb(int recordSize) {
        String cate = "no-sharding";
        userNoShardingMapper.createTableIfNotExists();
        StopWatch s1 = new StopWatch();

        //=================
        s1.start();
        userNoShardingMapper.truncateTable();
        s1.stop();
        log.info("{}-清空表耗时：{}(ms)", cate, s1.getTotalTimeMillis());

        //=================
        s1.start();
        for (int i = 0; i < recordSize; i++) {
            userNoShardingMapper.insert(new UserNoSharding("dean_" + i, "pwd_" + i, "query_" + i));
        }
        s1.stop();
        log.info("{}-批量插入用时：{}(ms)", cate, s1.getTotalTimeMillis());

        //=================
        s1.start();
        List<UserNoSharding> users = userNoShardingMapper.selectAll();
        s1.stop();
        log.info("{}-查询[{}]条记录，用时：{}(ms)", cate, users.size(), s1.getTotalTimeMillis());
    }


    private void testDbBySharding(int recordSize) {
        String cate = "sharding";
        StopWatch s1 = new StopWatch(cate);

        //=================
        s1.start();
        userMapper.truncateTable();
        s1.stop();
        log.info("{}-清空表耗时：{}(ms)", cate, s1.getTotalTimeMillis());

        //=================
        s1.start();
        for (int i = 0; i < recordSize; i++) {
            userMapper.insert(new User("dean_" + i, "pwd_" + i, "query_" + i));
        }
        s1.stop();
        log.info("{}-批量插入用时：{}(ms)", cate, s1.getTotalTimeMillis());

        //=================
        s1.start();
        List<User> users = userMapper.selectAll();
        s1.stop();
        log.info("{}-查询[{}]条记录，用时：{}(ms)", cate, users.size(), s1.getTotalTimeMillis());
    }


}
