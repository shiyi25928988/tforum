@echo off
docker run -itd --name loki --privileged=true -uroot -p 3100:3100 -p 9096:9096 -v %cd%:/home/loki grafana/loki:latest -config.file=/home/loki/loki-local-config.yaml && docker run -itd --name grafana -p 3000:3000 grafana/grafana:latest
pause