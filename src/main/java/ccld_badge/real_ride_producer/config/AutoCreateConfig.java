package ccld_badge.real_ride_producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AutoCreateConfig {

    @Bean
    public NewTopic libraryEvents() {

        return TopicBuilder.name("real-ride-events")
                .partitions(3)
                .replicas(3)
                .build();
    }

}
