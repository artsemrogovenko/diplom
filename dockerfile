FROM postgres:latest

# Устанавливаем пароль для пользователя postgres
ENV POSTGRES_PASSWORD=sa
ENV POSTGRES_USER=sa

# Создаем базы данных
ENV POSTGRES_DB=accounts,storage,specification,tasks


# Необходимо для переносимости данных базы данных между хостом и контейнером
VOLUME ./containers/database:/var/lib/postgresql/data

# Экспонируем порт 5432 контейнера на хост машине
EXPOSE 5432:5432
