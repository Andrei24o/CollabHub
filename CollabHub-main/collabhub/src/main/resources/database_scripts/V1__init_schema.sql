-- V1__init_schema.sql

CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email    VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL,
    role     VARCHAR(50) DEFAULT 'ROLE_USER',
    active   BOOLEAN     DEFAULT TRUE
);

CREATE TABLE projects
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    status      VARCHAR(50),
    owner_id    INTEGER      NOT NULL REFERENCES users (id),
    is_deleted  BOOLEAN   DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE project_members
(
    project_id INTEGER NOT NULL REFERENCES projects (id),
    user_id    INTEGER NOT NULL REFERENCES users (id),
    PRIMARY KEY (project_id, user_id)
);

CREATE TABLE tasks
(
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    priority    VARCHAR(50),
    status      VARCHAR(50),
    deadline    TIMESTAMP,
    project_id  INTEGER      NOT NULL REFERENCES projects (id),
    assignee_id INTEGER REFERENCES users (id),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE documents
(
    id          SERIAL PRIMARY KEY,
    file_name   VARCHAR(255) NOT NULL,
    file_type   VARCHAR(100),
    file_size   BIGINT,
    project_id  INTEGER      NOT NULL REFERENCES projects (id),
    uploader_id INTEGER      NOT NULL REFERENCES users (id),
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);