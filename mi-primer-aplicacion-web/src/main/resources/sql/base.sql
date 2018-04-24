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

drop table if exists public.usuario cascade;

create table public.usuario (
  id serial primary key,
  login_id int not null references login.login(id),
  nombre text not null,
  correo text not null,
  fotografia bytea,
  constraint correo_unico unique(correo),
  constraint email_valido check (correo ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')
);

insert into public.usuario (login_id, nombre, correo) values (1, 'miguel', 'miguel_pinia@ciencias.unam.mx');

drop table if exists public.pregunta cascade;

create table public.pregunta (
  id serial primary key
  , usuario_id int not null references public.usuario(id)
  , contenido text not null
);

insert into public.pregunta (usuario_id, contenido) values (1, 'Mi primer pregunta'),
                                                           (1, 'Mi segunda pregunta');

drop table if exists public.respuesta;

create table public.respuesta (
  id serial primary key
  , usuario_id int not null references public.usuario(id)
  , pregunta_id int not null references public.pregunta(id)
  , contenido text not null
);

insert into public.respuesta (usuario_id, pregunta_id, contenido) values (1, 1, 'Respuesta 1'),
                                                                         (1, 1, 'Respuesta 2'),
                                                                         (1, 2, 'Respuesta 1 pregunta 2');

commit;
