-- AccountProtection PostgreSQL 加载数据库初始文件。

CREATE TABLE IF NOT EXISTS "{prefix}player_account_info"
(
    "uuid"       VARCHAR(36) NOT NULL PRIMARY KEY,
    "secret_key" TEXT        NOT NULL,
    "zone_code"  INT         NOT NULL,
    "last_ip"    TEXT        NOT NULL,
    "times"      SMALLINT    NOT NULL,
    "bind"       BOOLEAN     NOT NULL
);