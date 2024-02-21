package com.calebklc.orderservice.order.mapper;

import com.calebklc.orderservice.core.mapper.BaseMapper;
import com.calebklc.orderservice.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    int insert(Order record);

    Optional<Order> findByBizId(String bizId);

    int updateStatus(Order record);

    int deleteByBizId(String bizId);
}