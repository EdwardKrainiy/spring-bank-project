CREATE TABLE accounts (
                       id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                       user_id BIGINT NULL DEFAULT NULL,
                       amount DECIMAL NULL DEFAULT NULL,
                       currency VARCHAR(3) NULL DEFAULT NULL,
                       account_number VARCHAR(256) NULL DEFAULT NULL,

                       CONSTRAINT unique_account_number UNIQUE (account_number)
);