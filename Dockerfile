FROM postgres:latest

# Copy database initialization scripts so the table is created automatically.
COPY docker/init.sql /docker-entrypoint-initdb.d/00-init-users.sql
