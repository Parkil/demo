create table service_pricing
(
    id   bigint auto_increment comment '식별자'
        primary key,
    name varchar(255) not null comment '서비스 요금제 명'
);

create table company
(
    credits                      int          not null comment '보유 크레딧',
    connected_service_pricing_id bigint       null,
    id                           bigint auto_increment comment '회사 식별자'
        primary key,
    name                         varchar(255) not null comment '회사명',
    constraint UK4syyu9nyeo6110naqaknyymdk
        unique (connected_service_pricing_id),
    constraint FK45t28m06wy60lp9qgw3t7m0xo
        foreign key (connected_service_pricing_id) references service_pricing (id)
);

create table feature_info
(
    deduction_credits    int          not null comment '차감되는 크레딧',
    deduction_criteria   tinyint      not null comment '크레딧 차감 기준',
    limited_amount       int          not null comment '제한량',
    restriction_criteria tinyint      not null comment '제한 기준',
    usage_criteria       tinyint      not null comment '사용 기준',
    code                 varchar(255) not null comment '기능 식별 코드'
        primary key,
    name                 varchar(255) not null comment '기능 명',
    check (`deduction_criteria` between 0 and 0),
    check (`restriction_criteria` between 0 and 1),
    check (`usage_criteria` between 0 and 1)
);

create table service_pricing_feature_list
(
    service_pricing_id bigint       not null,
    feature_list_code  varchar(255) not null,
    constraint FK1jk6cetuvl1qgoaqmab9m31yp
        foreign key (service_pricing_id) references service_pricing (id),
    constraint FKam4od33gx05s33h7whak42br9
        foreign key (feature_list_code) references feature_info (code)
);

create table usage_log
(
    usage_credit int          not null comment '사용 크레딧',
    company_id   bigint       not null comment '회사 ID',
    id           bigint auto_increment
        primary key,
    reg_dtm      datetime(6)  null,
    company_name varchar(255) not null comment '회사 명',
    feature_code varchar(255) not null comment '기능 코드',
    feature_name varchar(255) not null comment '기능 명'
);

