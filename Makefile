up:
	docker-compose down
	docker-compose up --build -d

clean:
	docker-compose down
	docker volume rm $(docker volume ls)
	docker rmi $(docker images -a -q)

down:
	docker-compose down

db:
	docker exec -it flink_sandbox_mariadb_1 bash
