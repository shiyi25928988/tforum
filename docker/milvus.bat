@echo off
docker-compose -f milvus-standalone-docker-compose.yml down & docker-compose -f milvus-standalone-docker-compose.yml up -d
pause