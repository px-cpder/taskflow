package com.example.taskflow.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.taskflow.task.entity.TaskLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskLogMapper extends BaseMapper<TaskLog> {
}