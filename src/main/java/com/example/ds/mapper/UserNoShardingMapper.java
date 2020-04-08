package com.example.ds.mapper;

import com.example.ds.model.UserNoSharding;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserNoShardingMapper {
    
    /**
     * Create table if not exist.
     */
    void createTableIfNotExists();
    
    /**
     * Drop table.
     */
    void dropTable();
    
    /**
     * Truncate table.
     */
    void truncateTable();
    
    /**
     * insert one entity.
     * @param entity entity
     * @return count or primary key
     */
    Long insert(UserNoSharding entity);
    
    /**
     * Do delete.
     * @param key key
     */
    void delete(String key);
    
    /**
     * select all.
     * @return list of entity
     */
    List<UserNoSharding> selectAll();
}
