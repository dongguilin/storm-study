package com.guilin.storm.study.bolt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.kafka.StringScheme;

import java.util.Map;

/**
 * Created by dongguilin on 2017/8/17.
 */
public class PrintBolt extends BaseBasicBolt {

    private static final Logger LOG = LoggerFactory.getLogger(PrintBolt.class);

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        LOG.info("[prepare] invoke...");
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        String str = input.getStringByField(StringScheme.STRING_SCHEME_KEY);
        LOG.info("[{}]", str);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        LOG.info("[declareOutputFields] invoke...");
        declarer.declareStream("print-stream", new Fields("json"));
    }

}
