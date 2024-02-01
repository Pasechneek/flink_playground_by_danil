
Java, Maven, Zookeeper, Kafka, Mariadb should be installed 

Provide env global variables like in .env.example file

Before run the project please install dependencies using Maven;

-- Mariadb 

```
CREATE DATABASE db_example;
CREATE USER 'user'@'localhost' IDENTIFIED BY 'password';
```

Check
```
select host, user from mysql.user where user='user' ;
```

log_bin must be switched on. To do this run in mariadb cli [root]:

Put into **/etc/mysql/my.cnf** the following:

```
[mysqld]
log_bin = ON
binlog_format = ROW
```

Check in mariadb CLI
```
show global variables like "%log_bin%";
```

The variables must be like
```
+---------------------------------+-------------------------+
| Variable_name                   | Value                   |
+---------------------------------+-------------------------+
| log_bin                         | ON                      |
| log_bin_basename                | /var/lib/mysql/ON       |
| log_bin_compress                | OFF                     |
| log_bin_compress_min_len        | 256                     |
| log_bin_index                   | /var/lib/mysql/ON.index |
| log_bin_trust_function_creators | OFF                     |
| sql_log_bin                     | ON                      |
+---------------------------------+-------------------------+
```


Grant cdc previlegue run followind in mariadb cli

```
GRANT SELECT, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'user' IDENTIFIED BY 'password';

GRANT update, insert, delete, select , SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'user';

GRANT SELECT, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'user' IDENTIFIED BY 'password';

GRANT update, insert, delete, select , SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'user';

GRANT update, insert, delete, select , SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'user';

GRANT CREATE ON db_example.* TO 'user'@'localhost' identified by 'password';
GRANT SELECT ON db_example.* TO 'user'@'localhost' identified by 'password';
GRANT ALTER ON db_example.* TO 'user'@'localhost' identified by 'password';
GRANT INSERT ON db_example.* TO 'user'@'localhost' identified by 'password';
GRANT REPLICATION SLAVE ON *.* TO 'user'@'localhost';
GRANT REPLICATION CLIENT ON *.* TO 'user'@'localhost';

FLUSH PRIVILEGES;
```
GRANT SHOW MASTER STATUS ON *.* TO 'user'; --problem
GRANT SHOW MASTER STATUS ON *.* TO 'user' IDENTIFIED BY 'password'; --problem



# Additional info: MariaDB configuration file
```
The MariaDB/MySQL tools read configuration files in the following order:
0. "/etc/mysql/my.cnf" symlinks to this file, reason why all the rest is read.
1. "/etc/mysql/mariadb.cnf" (this file) to set global defaults,
2. "/etc/mysql/conf.d/*.cnf" to set global options.
3. "/etc/mysql/mariadb.conf.d/*.cnf" to set MariaDB-only options.
4. "~/.my.cnf" to set user-specific options.
```


Please, check the variables in mariadb. It should be like:

