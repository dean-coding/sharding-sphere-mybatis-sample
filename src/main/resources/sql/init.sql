

CREATE TABLE `t_user_no_sharding` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(200) DEFAULT NULL,
  `pwd` varchar(200) DEFAULT NULL,
  `assisted_query_pwd` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_user_0 (
  user_id INT NOT NULL AUTO_INCREMENT,
  user_name VARCHAR(200),
  pwd VARCHAR(200),
  assisted_query_pwd VARCHAR(200),
  PRIMARY KEY (user_id)
);


CREATE TABLE IF NOT EXISTS t_user_1 like  t_user_0;
CREATE TABLE IF NOT EXISTS t_user_2 like  t_user_0;
CREATE TABLE IF NOT EXISTS t_user_3 like  t_user_0;