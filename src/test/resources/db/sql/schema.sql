drop table if exists events;
drop table if exists book_view;
drop table if exists user_view;
drop table if exists loan_view;

CREATE TABLE IF NOT EXISTS events (
    id bigint not null auto_increment,
    aggregate_id varchar(100) not null,
    aggregate_type varchar(50) not null,
    event_type varchar(50) not null,
    event_version int not null,
    payload json not null,
    occurred_at timestamp not null,
    primary key (id),
    unique key uq_aggregate_version (aggregate_type, aggregate_id, event_version),
    key idx_aggregate (aggregate_id),
    key idx_event_type (event_type)
);

CREATE TABLE IF NOT EXISTS book_view (
  isbn varchar(17) not null,
  author varchar(100) not null,
  title varchar(100) not null,
  description varchar(500),
  total_copies integer not null default 0,
  borrowed_copies integer not null default 0,
  available_copies integer not null default 0,
  reserved_copies integer not null default 0,
  created_at timestamp not null default current_timestamp,
  updated_at timestamp not null default current_timestamp,
  primary key (isbn)
);

CREATE TABLE IF NOT EXISTS user_view(
	id varchar(36) not null,
	username varchar(100) not null,
  created_at timestamp not null default current_timestamp,
  updated_at timestamp not null default current_timestamp,
	primary key(id)
);

CREATE TABLE IF NOT EXISTS loan_view (
  id varchar(36) not null,
  isbn varchar(17) not null,
  user_id varchar(36) not null,
  start_date date not null,
  end_date date not null,
  status ENUM('pending', 'reserved', 'confirmed', 'canceled', 'returned', 'failed') not null default 'pending',
  created_at timestamp not null default current_timestamp,
  updated_at timestamp not null default current_timestamp,
  primary key(id)
);