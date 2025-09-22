-- PROBLEMA NO SEU SCRIPT SQL:
-- Esta linha está incorreta: FOREIGN KEY (id) REFERENCES Tecnico(id)
-- Deveria ser removida pois id é primary key, não foreign key

-- Script correto seria:
CREATE TABLE Agendamento
(
id			INT			IDENTITY,
horaAgendamento		DATE,
dataAgendamento DATE,
tecnico_id		INT,
especialidade_id		INT,
usuario_id		INT,
descricao		VARCHAR(200),
urgencia		VARCHAR(200),
situacao		VARCHAR(200),
preco			DECIMAL(15,2),
PRIMARY KEY (id),
FOREIGN KEY			(tecnico_id)	REFERENCES Tecnico(id),
-- FOREIGN KEY			(id)	REFERENCES Tecnico(id), -- ESTA LINHA DEVE SER REMOVIDA
FOREIGN KEY			(especialidade_id)	REFERENCES Especialidade(id),
FOREIGN KEY			(usuario_id)	REFERENCES Usuario(id)
);