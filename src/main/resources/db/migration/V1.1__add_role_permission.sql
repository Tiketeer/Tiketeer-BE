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