/**
 * ============================================================
 * app.ts
 * 
 * Kakfa Consumer for push-sender
 * 
 * Author jonghokim27
 * 2023-08-29
 * ============================================================
 */

/**
 * Module dependencies
 */
import * as Firebase from 'firebase-admin';
import { Kafka, KafkaConfig, Consumer, EachMessagePayload } from 'kafkajs';

/**
 * Kafka Initialize
 */
const kafkaConfig: KafkaConfig = { brokers: ['kafka:9092'] }
const kafka = new Kafka(kafkaConfig)
const consumer = kafka.consumer({groupId: "node"});

/**
 * Firebase Initialize
 */
Firebase.initializeApp({
    credential: Firebase.credential.cert('/app/firebase.json')
});

/**
 * Sleep function
 */
function sleep(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * Firebase Topic send function
 */
async function sendTopic(topic: string, title: string, body: string, link: string){
    await Firebase.messaging()
        .send({
            topic: topic,
            notification: {
                title: title,
                body: body
            },
            data: {
                link: link
            },
            android: {
                notification: {
                    priority: "high"
                }
            },
            apns: {
                payload: {
                    aps: {
                        contentAvailable: true
                    }
                },
                headers: {
                    "apns-priority": "10"
                }
            }
            // android: {
            //     notification: {
            //         sound: "default",
            //         priority: "high",
            //     }
            // },
            // apns: {
            //     payload: {
            //         aps: {
            //             sound: "cr"
            //         }
            //     }
            // }
        })
        .then(function (response) {
            console.log("Successfully sent message: " + response);
        })
        .catch(function (err) {
            console.log('Failed to send message: ', err);
        });
}

/**
 * Firebase Token send function
 */
async function sendToken(token: string, title: string, body: string, link: string){
    await Firebase.messaging()
        .send({
            token: token,
            notification: {
                title: title,
                body: body
            },
            data: {
                link: link
            },
            android: {
                notification: {
                    priority: "high"
                }
            },
            apns: {
                payload: {
                    aps: {
                        contentAvailable: true
                    }
                },
                headers: {
                    "apns-priority": "10"
                }
            }
            // android: {
            //     notification: {
            //         sound: "default",
            //         priority: "high",
            //     }
            // },
            // apns: {
            //     payload: {
            //         aps: {
            //             sound: "cr"
            //         }
            //     }
            // }
        })
        .then(function (response) {
            console.log("Successfully sent message: " + response);
        })
        .catch(function (err) {
            console.log('Failed to send message: ', err);
        });
}

/**
 * Interface for PushMessage
 */
interface PushMessage{
    type: string,
    topic?: string,
    token?: string,
    title: string,
    body: string,
    link: string,
}

/**
 * Main function
 */
async function run(){
    try{
        await consumer.connect();
        await consumer.subscribe({ topic: 'pushMessage', fromBeginning: true })
        await consumer.run({
            eachMessage: async ({ topic, partition, message }: EachMessagePayload) => {
                if(!message['value'])
                    return;

                let value: string = message.value.toString();
                let valueObj: PushMessage = JSON.parse(value, );

                if(valueObj.type == "topic"){
                    if(!valueObj['topic'])
                        return;
                    
                    // console.log(valueObj.title);
                    await sendTopic(valueObj.topic, valueObj.title, valueObj.body, valueObj.link);
                }
                else if(valueObj.type == "token"){
                    if(!valueObj['token'])
                        return;

                    await sendToken(valueObj.token, valueObj.title, valueObj.body, valueObj.link);
                }
            },
        })
    }
    catch(e: any){
        console.log(e);
        await sleep(1000);
        run();
        return;
    }
}

run();