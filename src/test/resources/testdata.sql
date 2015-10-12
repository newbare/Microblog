INSERT INTO users (username, displayname, email, password, enabled) values ('test1', 'test1name', 'test1email', 'test1password', true);
INSERT INTO authorities (role, user_id) values ('ROLE_USER', (SELECT u.id from users u where u.username = 'test1'));

INSERT INTO users (username, displayname, email, password, enabled) values ('test2', 'test2name', 'test2email', 'test2password', true);
INSERT INTO authorities (role, user_id) values ('ROLE_USER', (SELECT u.id from users u where u.username = 'test2'));

INSERT INTO users (username, displayname, email, password, enabled) values ('test3', 'test3name', 'test3email', 'test3password', true);
INSERT INTO authorities (role, user_id) values ('ROLE_USER', (SELECT u.id from users u where u.username = 'test3'));

INSERT INTO posts (message, timestamp, user_id) values ('first message from test1', '2015-01-01 00:00:00',(SELECT u.id from users u where u.username = 'test1'));
INSERT INTO posts (message, timestamp, user_id) values ('second message from test1', '2015-01-01 00:00:05',(SELECT u.id from users u where u.username = 'test1'));
INSERT INTO posts (message, timestamp, user_id) values ('third message from test1', '2015-01-01 00:00:10',(SELECT u.id from users u where u.username = 'test1'));

INSERT INTO posts (message, timestamp, user_id) values ('first message from test2', '2015-01-01 00:00:15',(SELECT u.id from users u where u.username = 'test2'));
INSERT INTO posts (message, timestamp, user_id) values ('second message from test2', '2015-01-01 00:00:20',(SELECT u.id from users u where u.username = 'test2'));
INSERT INTO posts (message, timestamp, user_id) values ('third message from test2', '2015-01-01 00:00:25',(SELECT u.id from users u where u.username = 'test2'));

INSERT INTO posts (message, timestamp, user_id) values ('first message from test3', '2015-01-01 00:00:30',(SELECT u.id from users u where u.username = 'test3'));
INSERT INTO posts (message, timestamp, user_id) values ('second message from test3', '2015-01-01 00:00:35',(SELECT u.id from users u where u.username = 'test3'));
INSERT INTO posts (message, timestamp, user_id) values ('third message from test3', '2015-01-01 00:00:40',(SELECT u.id from users u where u.username = 'test3'));

INSERT INTO subscriptions (owner_id, followed_id) values ((SELECT u.id from users u where u.username = 'test1'), (SELECT u.id from users u where u.username = 'test2'));
INSERT INTO votes (post_id, user_id) values ((SELECT p.id from posts p where p.message = 'first message from test2'), (SELECT u.id from users u where u.username = 'test1'));