create table pedidos (
    id bigint(20) not null auto_increment,
    data_hora datetime not null,
    status varchar(255) not null,
    primary key (id)
)