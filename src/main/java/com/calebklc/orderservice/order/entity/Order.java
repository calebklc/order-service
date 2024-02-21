package com.calebklc.orderservice.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    /**
     * Row ID
     */
    private Long id;

    /**
     * Business ID
     */
    private String bizId;

    /**
     * Distance in meters
     */
    private Long distance;

    /**
     * Order Status
     */
    private String status;

    /**
     * Created At
     */
    private LocalDateTime createdAt;

    /**
     * Updated At
     */
    private LocalDateTime updatedAt;

    /**
     * Version for Optimistic Lock
     */
    private Integer version;
}