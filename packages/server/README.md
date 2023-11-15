# ssutoday-server
## Directory

[**push-sender**](https://github.com/jonghokim27/ssutoday/tree/main/packages/server/push-sender): Typescript project for sending push notifications via Firebase  
[**python-crawler**](https://github.com/jonghokim27/ssutoday/blob/main/packages/server/python-crawler): Python project for crawling SSU notices  
redis-server: Redis server config  
[**spring-backend**](https://github.com/jonghokim27/ssutoday/blob/main/packages/server/spring-backend): Spring boot project for Backend  
docker-compose.yml: Docker compose config

## Run the servers
```
docker compose up -d --build
```

## Stop the servers
```
docker compose down
```

## .env
.env must be created in the same directory as docker-compose.yml
```
# DB
MYSQL_HOST=
MYSQL_USER=
MYSQL_PASSWORD=
MYSQL_DB=

# AUTH
JWT_SECRET_KEY=
CLIENT_KEY=

# AWS
AWS_ACCESS_KEY_ID=
AWS_SECRET_ACCESS_KEY=
```

## Database DDL (Mysql 8.x)
```
create table Article
(
    idx       int auto_increment
        primary key,
    provider  varchar(10)                        not null,
    articleNo varchar(200)                       not null,
    title     varchar(500)                       not null,
    content   mediumtext                         null,
    url       varchar(1000)                      not null,
    createdAt datetime default CURRENT_TIMESTAMP not null,
    updatedAt datetime                           null,
    constraint idx
        unique (idx)
);

create table Config
(
    `key` varchar(100) not null
        primary key,
    value varchar(100) not null,
    constraint `key`
        unique (`key`)
);

create table Room
(
    no       varchar(10)  not null
        primary key,
    name     varchar(50)  not null,
    major    json         not null,
    capacity int          not null,
    location varchar(50)  not null,
    tags     varchar(50)  not null,
    image    varchar(200) not null,
    bigImage varchar(200) not null,
    constraint no
        unique (no)
);

create table SSOClient
(
    id          varchar(20)  not null
        primary key,
    secret      varchar(500) not null,
    callbackUrl varchar(200) not null,
    constraint id
        unique (id)
);

create table Student
(
    id         int                                not null
        primary key,
    name       varchar(100)                       not null,
    major      varchar(10)                        not null,
    xnApiToken varchar(500)                       null,
    isAdmin    int                                not null,
    createdAt  datetime default CURRENT_TIMESTAMP not null,
    updatedAt  datetime                           null,
    constraint id
        unique (id)
);

create table Device
(
    idx       int auto_increment
        primary key,
    StudentId int                                not null,
    osType    varchar(10)                        not null,
    uuid      varchar(200)                       not null,
    pushToken varchar(200)                       not null,
    notice    int                                not null,
    reserve   int                                not null,
    lms       int                                not null,
    createdAt datetime default CURRENT_TIMESTAMP not null,
    updatedAt datetime                           null,
    constraint idx
        unique (idx),
    constraint Device_Student_id_fk
        foreign key (StudentId) references Student (id)
            on update cascade
);

create table Reserve
(
    idx        int auto_increment
        primary key,
    StudentId  int                                not null,
    roomNo     varchar(10)                        not null,
    date       date                               not null,
    startBlock int                                not null,
    endBlock   int                                not null,
    createdAt  datetime default CURRENT_TIMESTAMP not null,
    deletedAt  datetime                           null,
    constraint idx
        unique (idx),
    constraint Reservation_Student_id_fk
        foreign key (StudentId) references Student (id)
            on update cascade,
    constraint Reserve_Room_no_fk
        foreign key (roomNo) references Room (no)
);

create table ReserveRequest
(
    idx        int auto_increment
        primary key,
    StudentId  int                                not null,
    roomNo     varchar(10)                        not null,
    date       date                               not null,
    startBlock int                                not null,
    endBlock   int                                not null,
    status     int                                not null,
    createdAt  datetime default CURRENT_TIMESTAMP not null,
    updatedAt  datetime                           null,
    constraint idx
        unique (idx),
    constraint ReserveRequest_Room_no_fk
        foreign key (roomNo) references Room (no),
    constraint ReserveRequest_Student_id_fk
        foreign key (StudentId) references Student (id)
);

create table VerifyPhoto
(
    idx        int auto_increment
        primary key,
    ReserveIdx int                                not null,
    url        varchar(200)                       not null,
    createdAt  datetime default CURRENT_TIMESTAMP not null,
    constraint idx
        unique (idx),
    constraint VerifyPhoto_Reserve_idx_fk
        foreign key (ReserveIdx) references Reserve (idx)
);

create table Version
(
    idx             int         not null
        primary key,
    osType          varchar(10) not null,
    requiredVersion varchar(10) not null,
    constraint idx
        unique (idx),
    constraint osType
        unique (osType)
);
```
