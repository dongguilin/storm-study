package com.guilin.storm.study.bolt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import storm.kafka.StringScheme;

import java.util.Map;

/**
 * Created by dongguilin on 2017/8/17.
 */
public class PrintBolt extends BaseBasicBolt {

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        System.out.println("[prepare] invoke...");
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        String str = input.getStringByField(StringScheme.STRING_SCHEME_KEY);
        System.out.println("[收到信息]");
        System.out.println(str);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("print-stream", new Fields("json", "json-id"));
    }
}
