.PHONY: pgcli deploy init dev sync-config

psql:
	docker exec -it clojurians-log-v2_db_1 psql -U myuser clojurians_log

pgcli:
	echo "Password is: mypass"
	pgcli -h localhost -p 54321 -d clojurians_log -U myuser

deploy:
	cd ops && ansible-playbook deploy.yml -i hosts

dev:
	npm run dev

sync-config:
	rsync -a resources/config/ ark2:/srv/ox/clojurians-log-v2/resources/config
