insert into users (username, password) values ('Alex', 'a');
insert into users (username, password) values ('JJ', 'Password123');
insert into users (username, password) values ('Ahmed', 'Password123');

insert into servers (address, name, password) values ('10.39.167.15', 'hollowbit.net', 'Test');
insert into servers (address, name, password) values ('10.39.167.16', 'testserver.net', 'Test');

insert into invites (invited_by_id, invited_user_id, server_id) values (1,2,'10.39.167.15');