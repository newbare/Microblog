CREATE TABLE
    users
    (
        id BIGINT NOT NULL auto_increment,
        display_name CHARACTER VARYING(20) NOT NULL,
        email CHARACTER VARYING(20) NOT NULL,
        enabled BOOLEAN NOT NULL,
        password CHARACTER VARYING(20) NOT NULL,
        username CHARACTER VARYING(20) NOT NULL,
        user_picture_prefix CHARACTER VARYING(255) NOT NULL,
        PRIMARY KEY (id),
        CONSTRAINT uk_81nqioeq3njjrwqaltk2mcobj UNIQUE (email),
        CONSTRAINT uk_h6k33r31i2nvrri9lok4r163j UNIQUE (username)
    );
CREATE TABLE
    authorities
    (
        id BIGINT NOT NULL auto_increment,
        role CHARACTER VARYING(255) NOT NULL,
        user_id BIGINT NOT NULL,
        PRIMARY KEY (id),
        CONSTRAINT fk_no2x0gv3t2iemx3dk72cqttpt FOREIGN KEY (user_id) REFERENCES users (id)
    );
CREATE TABLE
    posts
    (
        id BIGINT NOT NULL auto_increment,
        MESSAGE CHARACTER VARYING(160) NOT NULL,
        TIMESTAMP TIMESTAMP(6) NOT NULL,
        user_id BIGINT NOT NULL,
        PRIMARY KEY (id),
        CONSTRAINT fk_p6h53fx2vcx6jpsc7hcduvmc5 FOREIGN KEY (user_id) REFERENCES users (id)
    );
CREATE TABLE
    subscriptions
(
    owner_id BIGINT NOT NULL auto_increment,
    followed_id BIGINT NOT NULL,
    PRIMARY KEY (owner_id, followed_id),
    CONSTRAINT fk_1oma42buank9xdt1crlvcbmr6 FOREIGN KEY (followed_id) REFERENCES users (id),
    CONSTRAINT fk_jy06vuirolxiirnalmjbf2q64 FOREIGN KEY (owner_id) REFERENCES users (id)
);
CREATE TABLE
    votes
(
    post_id BIGINT NOT NULL auto_increment,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, user_id),
    CONSTRAINT fk_q0e30si44pvusvmht53gc4toq FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_crxh4j0ikufv4go9hqlx86ydm FOREIGN KEY (post_id) REFERENCES posts (id)
);