
FROM python:3.11-slim

WORKDIR /app
RUN pip install --no-cache-dir pika==1.3.2 requests==2.31.0

COPY simulator.py .
COPY config.json .

ENV RABBITMQ_HOST=rabbitmq
ENV RABBITMQ_PORT=5672
ENV RABBITMQ_USER=guest
ENV RABBITMQ_PASSWORD=guest
ENV INTERVAL_MINUTES=10

CMD ["python", "simulator.py"]