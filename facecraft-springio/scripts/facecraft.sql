-- Load tables
create table `servers` (
  `address` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255),
  `password` VARCHAR(255),
  PRIMARY KEY(address)
) ;

create table `users` (
  `user_id` BIGINT(19) NOT NULL AUTO_INCREMENT,
  `device_token` VARCHAR(255),
  `password` VARCHAR(255),
  `username` VARCHAR(255),
  PRIMARY KEY(user_id)
) ;

create table servers_owners (
  servers_owned_address VARCHAR(255) NOT NULL,
  owners_user_id BIGINT(19) NOT NULL,
  FOREIGN KEY(servers_owned_address)
      REFERENCES servers(address)
      ON DELETE CASCADE,
  FOREIGN KEY(owners_user_id)
      REFERENCES users(user_id)
      ON DELETE CASCADE
) ;

create table `servers_members` (
  `servers_part_of_address` VARCHAR(255) NOT NULL,
  `members_user_id` BIGINT(19) NOT NULL,
  FOREIGN KEY(`servers_part_of_address`)
      REFERENCES servers(address)
      ON DELETE CASCADE,
  FOREIGN KEY(`members_user_id`)
      REFERENCES users(user_id)
      ON DELETE CASCADE
) ;

create table `console_output` (
    `id` BIGINT(19) NOT NULL AUTO_INCREMENT,
    `message` VARCHAR(255),
    `server` VARCHAR(255),
    PRIMARY KEY(id),
    FOREIGN KEY(`server`)
        REFERENCES servers(address)
        ON DELETE CASCADE
) ;

create table `events` (
  `event_id` BIGINT(19) NOT NULL AUTO_INCREMENT,
  `date` TIMESTAMP,
  `description` VARCHAR(255),
  `title` VARCHAR(255),
  `server_id` VARCHAR (255),
  PRIMARY KEY(`event_id`),
  FOREIGN KEY(`server_id`)
      REFERENCES servers(`address`)
      ON DELETE CASCADE
) ;

create table `invites` (
  `id` BIGINT(19) NOT NULL AUTO_INCREMENT,
  `invited_by_id` BIGINT(19) NOT NULL,
  `invited_user_id` BIGINT(19) NOT NULL,
  `server_id` VARCHAR(255) NOT NULL,
  PRIMARY KEY(id),
  FOREIGN KEY (`invited_by_id`)
      REFERENCES users(user_id)
      ON DELETE NO ACTION,
  FOREIGN KEY (`invited_user_id`)
      REFERENCES users(user_id)
      ON DELETE NO ACTION,
  FOREIGN KEY (`server_id`)
      REFERENCES servers(address)
      ON DELETE NO ACTION
) ;

create table `messages` (
  `message_id` VARCHAR(255) NOT NULL,
  `date` VARCHAR(255),
  `message` VARCHAR(255),
  `sender_type` VARCHAR(255),
  `server` VARCHAR(255),
  `username` VARCHAR(255)
) ;