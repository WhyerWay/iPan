package indi.ipan.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import indi.ipan.model.User;

@Mapper
public interface UserMapper extends BaseMapper<User>{

}
