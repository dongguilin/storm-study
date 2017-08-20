package com.guilin.storm.study.topo;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import com.guilin.storm.study.bolt.PrintBolt;
import kafka.api.OffsetRequest;
import storm.kafka.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongguilin on 2017/8/20.
 */
public class SimpleTopo {

    public static void main(String[] args) {
        Config config = new Config();
//        config.put(Config.TOPOLOGY_DEBUG, true);
//        config.put(Config.TOPOLOGY_ACKER_EXECUTORS, 0);
//        config.put(Config.TOPOLOGY_WORKERS, 2);
//
//        config.put(Config.STORM_MESSAGING_NETTY_MAX_SLEEP_MS, 5000 * 4);
//        config.put(Config.STORM_ZOOKEEPER_SESSION_TIMEOUT, 20000 * 100);
//        config.put(Config.STORM_ZOOKEEPER_CONNECTION_TIMEOUT, 15000 * 100);
//        config.put(Config.STORM_ZOOKEEPER_RETRY_TIMES, 10);
//
//        config.registerMetricsConsumer(LoggingMetricsConsumer.class, 1);
//        config.put(Config.NIMBUS_TASK_TIMEOUT_SECS, 600);
//        config.put(Config.SUPERVISOR_WORKER_TIMEOUT_SECS, 600);


        BrokerHosts hosts = new ZkHosts("ubuntu:2181", "/kafka");// zookeeper地址
        String topic = "guilin-topic";
        String brokerZkPath = "/offset";
        SpoutConfig spoutConfig = new SpoutConfig(hosts, topic, brokerZkPath, "SimpleTopo");
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        spoutConfig.useStartOffsetTimeIfOffsetOutOfRange = true;
        spoutConfig.startOffsetTime = OffsetRequest.EarliestTime();
        spoutConfig.consumeRangeOffsetSpout=false;

        List<String> list = new ArrayList<>();
        list.add("ubuntu");
        spoutConfig.zkServers=list;
        spoutConfig.zkPort=2181;

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("KafkaSpout", new KafkaSpout(spoutConfig), 2);
        builder.setBolt("printBolt", new PrintBolt(), 2).shuffleGrouping("KafkaSpout");
//        builder.setBolt("bolt", new SequenceBolt()).shuffleGrouping("spout");

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("SimpleTopo", config, builder.createTopology());
    }
}
