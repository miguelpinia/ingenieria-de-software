begin;

-- create role miguel with superuser;
-- alter role miguel with login;

-- createdb ejemplo -O miguel

drop schema if exists login cascade;
create schema login;

drop extension if exists pgcrypto;
create extension pgcrypto;

drop table if exists login.login;

create table login.login (
  id serial primary key
  , usuario text not null
  , password text not null
  , constraint usuarioUnico unique (usuario)
);

comment on table login.login
is
'El usuario USUARIO tiene la contraseña PASS después de aplicarle un hash';

create or replace function login.hash() returns trigger as $$
  begin
    if TG_OP = 'INSERT' then
       new.password = crypt(new.password, gen_salt('bf', 8)::text);
    end if;
    return new;
  end;
$$ language plpgsql;

comment on function login.hash()
is
'Cifra la contraseña del usuario al guardarla en la base de datos.';

create trigger cifra
before insert on login.login
for each row execute procedure login.hash();

create or replace function login.login(usuario text, contraseña text) returns boolean as $$
  select exists(select 1
                  from login.login
                 where usuario = usuario and
                       password = crypt(contraseña, password));
$$ language sql stable;

insert into login.login (usuario, password) values ('Miguel', 'password');

commit;
