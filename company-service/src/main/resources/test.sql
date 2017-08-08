INSERT INTO account (id, name, phone, password, roles) VALUES (1, 'admin', '18911111111', 'password', 'ADMIN');
INSERT INTO account (id, name, phone, password, roles)
VALUES (2, 'operator', '18922222222', 'password', 'OPERATOR,USER');
INSERT INTO account (id, name, phone, password, roles) VALUES (3, 'user', '18933333333', 'password', 'USER');
INSERT INTO client (client_id, client_secret, scope, resource, authority, grant_type)
VALUES ('backend1', 'password', 'backend', 'rest', 'backend', 'client_credentials');
INSERT INTO client (client_id, client_secret, scope, resource, authority, grant_type)
VALUES ('backend2', 'password', 'backend', 'rest', 'backend', 'client_credentials');
INSERT INTO client (client_id, client_secret, scope, resource, authority, grant_type)
VALUES ('app', 'password', 'app', 'rest', 'app', 'password');