create table orders (
    id bigint primary key auto_increment comment "Row ID",
    biz_id varchar(36) not null comment "Business ID",
    distance int not null comment "Distance in meters",
    status varchar(50) comment "Order Status",
    created_at datetime not null default CURRENT_TIMESTAMP comment "Created At",
    updated_at datetime not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment "Updated At",
    version int unsigned not null default 0 comment "Version for Optimistic Lock",

    unique key biz_id_index (biz_id),
    key status_index (status),
    key created_at_index (created_at)
);