package ru.gosuslugi.pgu.lk.notifier.config;

import lombok.RequiredArgsConstructor;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.core.client.loadbalance.RoundRobinConnectionLoadBalancingPolicy;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.util.StringUtils;

import javax.jms.Destination;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Конфигурация частично скопирована с ArtemisConnectionFactoryConfiguration.
 * Добавлена возможности задать несколько jms брокеров
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(CustomJmsProperties.class)
public class JmsConfig {

    private final CustomJmsProperties properties;

    @Bean
    public Destination lkNotifyDestination(
            @Value("${spring.jms.template.default-destination}") String destinationName
    ) {
        return new ActiveMQQueue(destinationName);
    }

    @Bean(name = "jmsConnectionFactory")
    CachingConnectionFactory cachingJmsConnectionFactory(JmsProperties jmsProperties) {
        JmsProperties.Cache cacheProperties = jmsProperties.getCache();
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(createNativeConnectionFactory());
        connectionFactory.setCacheConsumers(cacheProperties.isConsumers());
        connectionFactory.setCacheProducers(cacheProperties.isProducers());
        connectionFactory.setSessionCacheSize(cacheProperties.getSessionCacheSize());
        return connectionFactory;
    }

    private ActiveMQConnectionFactory createNativeConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(false, getTransportConfiguration());
        String user = this.properties.getUsername();
        if (StringUtils.hasText(user)) {
            connectionFactory.setUser(user);
            connectionFactory.setPassword(this.properties.getPassword());
        }

        connectionFactory.setConnectionLoadBalancingPolicyClassName(RoundRobinConnectionLoadBalancingPolicy.class.getName());
        connectionFactory.setCallTimeout(30000);
        connectionFactory.setCallFailoverTimeout(5000);
        connectionFactory.setClientFailureCheckPeriod(this.properties.getClientFailureCheckPeriod());
        connectionFactory.setConnectionTTL(this.properties.getConnectionTTL());
        connectionFactory.setRetryInterval(this.properties.getRetryInterval());
        connectionFactory.setRetryIntervalMultiplier(this.properties.getRetryIntervalMultiplier());
        connectionFactory.setMaxRetryInterval(this.properties.getMaxRetryInterval());
        connectionFactory.setReconnectAttempts(this.properties.getReconnectAttempts());
//        connectionFactory.setConfirmationWindowSize(10485760);
        connectionFactory.setAutoGroup(false);
        connectionFactory.setConsumerWindowSize(0);
        connectionFactory.setUseGlobalPools(false);
        connectionFactory.setScheduledThreadPoolMaxSize(10);
        connectionFactory.setThreadPoolMaxSize(50);

        return connectionFactory;
    }

    private TransportConfiguration[] getTransportConfiguration() {
        String[] connectionUrlArray = properties.getConnection().split("[;]");
        List<TransportConfiguration> configurationList = new LinkedList<>();

        for (String connectionUrl : connectionUrlArray) {
            String[] params = connectionUrl.split("[:]");
            if (params.length != 2) {
                throw new IllegalArgumentException("Incorrect connection string: " + connectionUrl);
            }

            Map<String, Object> param = new HashMap<>();
            param.put("host", params[0]);
            param.put("port", Long.valueOf(params[1]));
            configurationList.add(new TransportConfiguration(NettyConnectorFactory.class.getName(), param));
        }

        return configurationList.toArray(new TransportConfiguration[0]);
    }

}
