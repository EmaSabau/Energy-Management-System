import pika
import json
import time
import random
from datetime import datetime, timedelta
import logging
import threading

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(threadName)-15s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

class EnergyMeterSimulator:
    def __init__(self, device_id, device_name, max_consumption_w, 
                 rabbitmq_host='localhost', rabbitmq_port=5672,
                 rabbitmq_user='guest', rabbitmq_password='guest', 
                 queue_name='device_data_queue'):
        self.device_id = device_id
        self.device_name = device_name
        self.max_consumption_w = max_consumption_w
        self.queue_name = queue_name
        
        credentials = pika.PlainCredentials(rabbitmq_user, rabbitmq_password)
        parameters = pika.ConnectionParameters(host=rabbitmq_host, port=rabbitmq_port, credentials=credentials)
        
        try:
            self.connection = pika.BlockingConnection(parameters)
            self.channel = self.connection.channel()
            self.channel.queue_declare(queue=queue_name, durable=True)
            logger.info(f"[{self.device_name}] Conectat la RabbitMQ")
        except Exception as e:
            logger.error(f"[{self.device_name}] Eroare conectare RabbitMQ: {e}")
            raise

    def generate_value_for_hour(self, hour):
        """Calcul realist bazat pe profilul orar."""
        if 0 <= hour < 6: # Noapte (Standby)
            multiplier = random.uniform(0.05, 0.15)
        elif 18 <= hour < 22: # Seara (Utilizare intensa)
            multiplier = random.uniform(0.6, 0.85)
        else: # Zi (Utilizare medie)
            multiplier = random.uniform(0.2, 0.45)
            
        value = (self.max_consumption_w / 1000) * multiplier * (1/6)
        #value=100
        return round(value, 4)

    def send_measurement(self, timestamp):
        measurement = {
            "timestamp": timestamp.isoformat(),
            "device_id": self.device_id,
            "measurement_value": self.generate_value_for_hour(timestamp.hour)
        }
        try:
            self.channel.basic_publish(
                exchange='',
                routing_key=self.queue_name,
                body=json.dumps(measurement),
                properties=pika.BasicProperties(delivery_mode=2)
            )
            logger.info(f"[{self.device_name}] Trimis: {measurement['measurement_value']} kWh pt ora {timestamp.strftime('%H:%M')}")
        except Exception as e:
            logger.error(f"[{self.device_name}] Eroare: {e}")

    def run_simulation(self):
        """Executa backfill si apoi continua in timp real."""
        now = datetime.now()
        # 1. BACKFILL: Trimitem datele de la 00:00 pana acum
        logger.info(f"[{self.device_name}] Incepere Backfill...")
        current_ts = now.replace(hour=0, minute=0, second=0, microsecond=0)
        
        while current_ts < now:
            self.send_measurement(current_ts)
            current_ts += timedelta(minutes=10)
        
        logger.info(f"[{self.device_name}] Backfill finalizat. Trecere in mod Real-Time (10 min).")

        try:
            while True:
                # Asteptam pana la urmatorul interval de 10 minute fix (ex: :10, :20, :30)
                wait_seconds = 600 - (datetime.now().minute % 10 * 60 + datetime.now().second)
                time.sleep(wait_seconds)
                
                self.send_measurement(datetime.now())
        except KeyboardInterrupt:
            self.connection.close()

def load_config():
    with open('config.json', 'r', encoding='utf-8') as f:
        return json.load(f)

if __name__ == "__main__":
    config = load_config()
    devices = config.get('devices', [])
    rb_cfg = config.get('rabbitmq', {})
    
    for dev in devices:
        sim = EnergyMeterSimulator(
            device_id=dev['device_id'],
            device_name=dev.get('name', 'Device'),
            max_consumption_w=dev.get('max_consumption_w', 1000),
            rabbitmq_host=rb_cfg.get('host', 'localhost'),
            queue_name=rb_cfg.get('queue_name', 'device_data_queue')
        )
        threading.Thread(target=sim.run_simulation, daemon=True).start()
        
    while True:
        time.sleep(1)