docker run --name mysql -p 3306:3306 -v /home/data/mysql:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=123456 -d mysql:8.0.41


