FROM postgres:16-alpine

ENV POSTGRES_DB=guestbook_db
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres

EXPOSE 5432

CMD ["postgres"]