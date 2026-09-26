create table pagamentos(
    id bigint not null auto_increment,
	valor decimal(10,2) not null constraint valor_positivo check (valor > 0),
    nome varchar(100) not null,
    numero varchar(19) not null,
	expiracao varchar(7) not null,
	codigo varchar(3) not null constraint tamanho_minimo check (length(codigo) = 3),
    status varchar(30) not null,   
    pedido_id bigint not null,
    forma_pagamento_id bigint not null,

    primary key(id)
);