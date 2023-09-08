from imports import *

from kafka import KafkaProducer
producer = KafkaProducer(
    acks=0,
    compression_type='gzip',
    bootstrap_servers=[constants.KAFKA_HOST],
    value_serializer=lambda x: json.dumps(x).encode('utf-8')
)

def send_push(article, mode):
    if mode == "created":
        title = "공지사항이 등록되었어요!"
    elif mode == "edited":
        title = "공지사항이 수정되었어요!"
    
    body = article['title']
    if article['provider'] == 16:
        body = "[SSU:Catch] " + body
        topic = "all"
    elif article['provider'] == 8:
        body = "[총학생회] " + body
        topic = "all"
    elif article['provider'] == 4:
        body = "[컴퓨터학부] " + body
        topic = "cse"
    elif article['provider'] == 2:
        body = "[소프트웨어학부] " + body
        topic = "sw"
    elif article['provider'] == 1:
        body = "[글로벌미디어학부] " + body
        topic = "media"

    message = {'type': 'topic', 'topic': topic, 'title': title, 'body': body, 'link': 'ssutoday://notice/' + str(article['idx'])}
    producer.send('pushMessage', value=message)
    producer.flush()