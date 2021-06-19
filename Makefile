.PHONY=pgcli

pgcli:
	pgcli -h localhost -p 5432 -d clojurians_log -U myuser
