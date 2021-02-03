package com.example.demoapp.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.springdata20.repository.config.EnableIgniteRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Collections;

@Configuration
@Slf4j
@EnableIgniteRepositories(basePackages = "com.example.demoapp.cache.repository")
public class IgniteConfig {

    /**
     * @see TcpDiscoveryMulticastIpFinder#setAddresses(Collection)
     * */
    @Value("${ignite.discovery.multicast.addresses:127.0.0.1:47500..47509}")
    private Collection<String> igniteContactPoints;

    @Bean
    public Ignite igniteInstance(
            IgniteConfiguration igniteConfiguration
    ) {
        log.info("starting Ignite instance using contact points: {}", igniteContactPoints);
        Ignite ignite = Ignition.start(igniteConfiguration);
        log.info("Started Ignite instance");
        return ignite;
    }

    @Bean
    public IgniteConfiguration igniteConfiguration(
//            List<CacheConfiguration> cacheConfigurations
    ) {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setIgniteInstanceName("ignite-clnt-1");
        igniteConfiguration.setClientMode(true);
        igniteConfiguration.setDaemon(false);
        igniteConfiguration.setPeerClassLoadingEnabled(true);
//        igniteConfiguration.setLocalHost("127.0.0.1");

        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(igniteContactPoints);
        log.info("igniteContactPoints={}", igniteContactPoints);
        tcpDiscoverySpi.setIpFinder(ipFinder);
        tcpDiscoverySpi.setLocalPort(47500);
//         Changing local port range. This is an optional action.
//        tcpDiscoverySpi.setLocalPortRange(9);
//        tcpDiscoverySpi.setLocalAddress("localhost");
        igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);

        TcpCommunicationSpi communicationSpi = new TcpCommunicationSpi();
//        communicationSpi.setLocalAddress("localhost");
        communicationSpi.setLocalPort(48100);
        communicationSpi.setSlowClientQueueLimit(1000);
        igniteConfiguration.setCommunicationSpi(communicationSpi);

//        CacheConfiguration[] caches = cacheConfigurations.toArray(new CacheConfiguration[0]);
//        igniteConfiguration.setCacheConfiguration(caches);

        return igniteConfiguration;

    }

//    @Bean
//    public CacheConfiguration<UUID, Product> productCache() {
//        CacheConfiguration<UUID, Product> productCache = new CacheConfiguration<>("Product");
//        productCache.setIndexedTypes(UUID.class, Product.class);
//        return productCache;
//    }
}
