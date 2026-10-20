CREATE TABLE IF NOT EXISTS clientes (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        nome VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL
    );

CREATE TABLE IF NOT EXISTS equipamentos (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            nome VARCHAR(255) NOT NULL,
    cliente_id BIGINT NOT NULL,
    CONSTRAINT fk_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
    );