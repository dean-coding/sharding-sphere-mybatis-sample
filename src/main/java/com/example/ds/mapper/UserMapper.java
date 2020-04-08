package com.example.ds.mapper;

import com.example.ds.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface UserMapper {

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
     *
     * @param entity entity
     * @return count or primary key
     */
    Long insert(User entity);

    Long insertBatch(@Param("datas") List<User> datas);

    Long resetInitPwdByNames(@Param("names") List<String> names);

    /**
     * Do delete.
     *
     * @param key key
     */
    void delete(String key);

    /**
     * select all.
     *
     * @return list of entity
     */
    List<User> selectAll();

    List<User> selectByLimit(@Param("page") int page, @Param("size") int size);

    int existsByName(@Param("names") Set<String> names);

}
