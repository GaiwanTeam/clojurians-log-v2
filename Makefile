.PHONY: pgcli deploy init dev sync-config

pgcli:
	pgcli -h localhost -p 54321 -d clojurians_log -U myuser

deploy:
	cd ops && ansible-playbook deploy.yml -i hosts

init:
	npm install
	mkdir -p resources/config
	touch resources/config/secrets.edn

dev:
	npm run dev

sync-config:
	rsync -a resources/config/ ark2:/srv/ox/clojurians-log-v2/resources/config
