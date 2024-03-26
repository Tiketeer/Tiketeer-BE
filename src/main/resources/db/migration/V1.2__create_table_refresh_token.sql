create table refresh_tokens (
    refresh_token_id binary(16) primary key,
    member_id binary(16) not null,
    created_at timestamp(6) not null,
    expired_at timestamp(6) not null
);