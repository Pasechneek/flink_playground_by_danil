


--Mariadb config
log_bin must be switched on 

SET GLOBAL log_bin = 1;  (or ON)


GRANT CDC


GRANT SELECT, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'user' IDENTIFIED BY 'password';
GRANT update, insert, delete, select , SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'user';

GRANT SELECT, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'hello' IDENTIFIED BY 'hello';
GRANT update, insert, delete, select , SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'hello';

GRANT SELECT, SHOW MASTER STATUS ON *.* TO 'hello' IDENTIFIED BY 'hello';

GRANT REPLICATION CLIENT
ON *.*
TO 'USERNAME'@'HOSTNAME';

GRANT REPLICATION CLIENT
ON *.*
TO 'hello'@'localhost';


FLUSH PRIVILEGES;

GRANT update, insert, delete, select , SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'user';
GRANT SHOW MASTER STATUS ON *.* TO 'user';



```
SET GLOBAL binlog_format = 'ROW';
--and
SET [SESSION] sql_log_bin = {0|1}
SET Local log_bin = 1;
```

# or

```
SET SESSION binlog_format = 'ROW';
--and
SET [SESSION] sql_log_bin = {0|1}
```

# The MariaDB configuration file
#
# The MariaDB/MySQL tools read configuration files in the following order:
# 0. "/etc/mysql/my.cnf" symlinks to this file, reason why all the rest is read.
# 1. "/etc/mysql/mariadb.cnf" (this file) to set global defaults,
# 2. "/etc/mysql/conf.d/*.cnf" to set global options.
# 3. "/etc/mysql/mariadb.conf.d/*.cnf" to set MariaDB-only options.
# 4. "~/.my.cnf" to set user-specific options.



Please, check the variables in mariadb. It should be like:

```
MariaDB [(none)]> show global variables like "%log_bin%";
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
