.PHONY=pgcli deploy

pgcli:
	pgcli -h localhost -p 54321 -d clojurians_log -U myuser

deploy:
	cd ops && ansible-playbook deploy.yml -i hosts
