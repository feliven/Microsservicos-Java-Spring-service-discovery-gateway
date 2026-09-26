create table item_do_pedido (
    id bigint(20) not null auto_increment,
    descricao varchar(255) default null,
    quantidade int(11) not null,
    pedido_id bigint(20) not null,
    primary key (id),
    foreign key (pedido_id) references pedidos(id)
)