package com.guilin.storm.study.topo;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.metric.LoggingMetricsConsumer;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import com.guilin.storm.study.bolt.PrintBolt;
import kafka.api.OffsetRequest;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;
import storm.kafka.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongguilin on 2017/8/20.
 */
public class LagOffsetSimpleTopo {

    public static void main(String[] args) {
        String topic = "guilin-topic31";
        System.setProperty("rootpath", "logs/" + topic + "/latestTime1/lag");
        String path = SimpleTopo.class.getClassLoader().getResource("log4j-lagoffset.properties").getPath();
        PropertyConfigurator.configure(path);
        int topology_workers = 2;
        int spoutTask = 2;
        int printBoltTask = 2;
        String brokerZkPath = "/lagoffset/offset";


        Config config = new Config();
        config.put(Config.TOPOLOGY_DEBUG, true);
        config.put(Config.TOPOLOGY_ACKER_EXECUTORS, 0);
        config.put(Config.TOPOLOGY_WORKERS, topology_workers);

        config.put(Config.STORM_MESSAGING_NETTY_MAX_SLEEP_MS, 5000 * 4);
        config.put(Config.STORM_ZOOKEEPER_SESSION_TIMEOUT, 20000 * 100);
        config.put(Config.STORM_ZOOKEEPER_CONNECTION_TIMEOUT, 15000 * 100);
        config.put(Config.STORM_ZOOKEEPER_RETRY_TIMES, 10);

        config.registerMetricsConsumer(LoggingMetricsConsumer.class, 1);
        config.put(Config.NIMBUS_TASK_TIMEOUT_SECS, 600);
        config.put(Config.SUPERVISOR_WORKER_TIMEOUT_SECS, 600);


        BrokerHosts hosts = new ZkHosts("ubuntu:2181", "/kafka/brokers");// zookeeper地址
        SpoutConfig spoutConfig = new SpoutConfig(hosts, topic, brokerZkPath, "SimpleTopo");
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        spoutConfig.useStartOffsetTimeIfOffsetOutOfRange = true;
        spoutConfig.startOffsetTime = OffsetRequest.LatestTime();
        spoutConfig.consumeRangeOffsetSpout = true;

        List<String> list = new ArrayList<>();
        list.add("ubuntu");
        spoutConfig.zkServers = list;
        spoutConfig.zkPort = 2181;

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("KafkaSpout", new KafkaSpout(spoutConfig), spoutTask);
        builder.setBolt("printBolt", new PrintBolt(), printBoltTask).shuffleGrouping("KafkaSpout");

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("SimpleTopo-lagoffset", config, builder.createTopology());
    }
}
