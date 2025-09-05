--liquibase formatted sql

--changeset DanielK:1


CREATE TABLE IF NOT EXISTS conversations (
                                             id SERIAL PRIMARY KEY,
                                             name TEXT,
                                             updated TIMESTAMP,
                                             created TIMESTAMP DEFAULT current_timestamp,
                                             user_id INTEGER
    );

CREATE TABLE IF NOT EXISTS models (
                                      id SERIAL PRIMARY KEY,
                                      provider TEXT,
                                      model_name TEXT,
                                      description TEXT,
                                      updated TIMESTAMP,
                                      created TIMESTAMP DEFAULT current_timestamp,
                                      input_multiplier DECIMAL(8, 4) NOT NULL,
    output_multiplier DECIMAL(8, 4) NOT NULL,
    cached_multiplier DECIMAL(8, 4) NOT NULL
    );

CREATE TABLE IF NOT EXISTS chat_messages (
                                             id SERIAL PRIMARY KEY,
                                             role VARCHAR(20) NOT NULL,
    content TEXT,
    model TEXT,
    temperature DECIMAL(2, 1),
    message_type VARCHAR(20) NOT NULL,
    status VARCHAR(20),
    error_details TEXT,
    input_token DECIMAL,
    output_token DECIMAL,
    created TIMESTAMP DEFAULT current_timestamp,
    conversation_id INTEGER REFERENCES conversations(id),
    model_id INTEGER REFERENCES models(id),
    native_tokens_sum DECIMAL,
    general_tokens_sum DECIMAL,
    is_service_message BOOL default false
    );

CREATE INDEX idx_role ON chat_messages(role);
CREATE INDEX idx_message_type ON chat_messages(message_type);
CREATE INDEX idx_created ON chat_messages(created);

INSERT INTO models (provider, model_name, input_multiplier, output_multiplier, cached_multiplier)
VALUES ('OPENAI', 'gpt-4o', 2.50, 1.25, 10.00);
