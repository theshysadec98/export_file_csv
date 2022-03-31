drop table if exists dummy;

create table device
(
    id      bigserial not null
        constraint device_pk
            primary key,
    highway varchar(255) not null,
    name    varchar(255) not null
);

create table direction_lane
(
	id serial not null
		constraint direction_lane_pk
			primary key,
	direction varchar(255) not null,
	lane varchar(255) not null
);

create table traffic_information
(
	id bigserial not null
		constraint traffic_information_pk
			primary key,
	device_id bigint not null
		constraint traffic_information_device_id_fk
			references device,
	direction_lane_id int not null
		constraint traffic_information_direction_lane_id_fk
			references direction_lane,
	saturation int,
	speed int,
	vehicles int,
	created_date timestamp not null,
	period_date timestamp not null
);

