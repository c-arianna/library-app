CREATE DATABASE IF NOT EXISTS library;

CREATE USER IF NOT EXISTS 'libraryApp' IDENTIFIED WITH mysql_native_password BY 'libraryAppTest!';
GRANT ALL PRIVILEGES ON library . * TO 'libraryApp'@'%';

FLUSH PRIVILEGES;