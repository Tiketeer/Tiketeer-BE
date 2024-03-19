create table members (
    enabled boolean not null,
    created_at timestamp(6) not null,
    last_login_at timestamp(6),
    point bigint not null,
    member_id binary(16) not null,
    role_id binary(16) not null,
    email varchar(255) not null,
    password varchar(255),
    profile_url varchar(255),
    primary key (member_id)
);

create table otps (
    created_at timestamp(6) not null,
    expired_at timestamp(6) not null,
    member_id binary(16) unique not null,
    password binary(16) not null,
    primary key (password)
);

create table permissions (
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    permission_id binary(16) not null,
    name ENUM('TICKETING_READ','TICKETING_WRITE'),
    primary key (permission_id)
);

create table purchases (
    created_at timestamp(6) not null,
    member_id binary(16) not null,
    purchase_id binary(16) not null,
    primary key (purchase_id)
);

create table role_permissions (
    permission_id binary(16) not null,
    role_id binary(16) not null,
    role_permission_id binary(16) not null,
    primary key (role_permission_id)
);

create table roles (
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    role_id binary(16) not null,
    name ENUM('BUYER','SELLER'),
    primary key (role_id)
);

create table ticketings (
    running_minutes integer not null,
    created_at timestamp(6) not null,
    event_time timestamp(6) not null,
    price bigint not null,
    sale_end timestamp(6) not null,
    sale_start timestamp(6) not null,
    owner_id binary(16) not null,
    ticketing_id binary(16) not null,
    category varchar(255),
    description TEXT,
    location varchar(255) not null,
    title varchar(255) not null,
    primary key (ticketing_id)
);

create table tickets (
    created_at timestamp(6) not null,
    purchase_id binary(16),
    ticket_id binary(16) not null,
    ticketing_id binary(16) not null,
    primary key (ticket_id)
);

set @seller_id = UUID_TO_BIN(uuid()), @buyer_id = UUID_TO_BIN(uuid()), @read_permission_id = UUID_TO_BIN(uuid()), @write_permission_Id = UUID_TO_BIN(uuid());
insert into roles (role_id, name, created_at, updated_at)
values (@buyer_id, 'BUYER', now(), now());

insert into roles (role_id, name, created_at, updated_at)
values (@seller_id, 'SELLER', now(), now());

insert into permissions (permission_id, name, created_at, updated_at)
values (@read_permission_id, 'TICKETING_READ', now(), now());

insert into permissions (permission_id, name, created_at, updated_at)
values (@write_permission_Id, 'TICKETING_WRITE', now(), now());

insert into role_permissions (role_permission_id, role_id, permission_id)
values (UUID_TO_BIN(uuid()), @buyer_id, @read_permission_id);

insert into role_permissions (role_permission_id, role_id, permission_id)
values (UUID_TO_BIN(uuid()), @seller_id, @read_permission_id);

insert into role_permissions (role_permission_id, role_id, permission_id)
values (UUID_TO_BIN(uuid()), @seller_id, @write_permission_Id);