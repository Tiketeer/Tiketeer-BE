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