/**
 * @filename    : KafkaProvider.java
 * @description : Provider for Kafka (Producer)
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProvider {
    /**
     * DI
     */
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Send message to Kafka
     * @param topic topic of message
     * @param message message to send
     */
    public void sendMessage(@NotNull String topic, @NotNull String message){
        kafkaTemplate.send(topic, message);
        kafkaTemplate.flush();
    }
}
