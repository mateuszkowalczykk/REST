CREATE TABLE USERS_API_LOG
(
    LOGIN         NVARCHAR(255) NOT NULL,
    REQUEST_COUNT BIGINT NOT NULL DEFAULT 1,
    CONSTRAINT USERS_API_LOG_PRIMARY_KEY PRIMARY KEY (LOGIN)
);
