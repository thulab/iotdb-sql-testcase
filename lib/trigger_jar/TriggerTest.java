/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.iotdb.commons.path.PartialPath;
import org.apache.iotdb.db.engine.trigger.api.Trigger;
import org.apache.iotdb.trigger.api.TriggerAttributes;
import org.apache.iotdb.db.engine.trigger.sink.local.LocalIoTDBConfiguration;
import org.apache.iotdb.db.engine.trigger.sink.local.LocalIoTDBEvent;
import org.apache.iotdb.db.engine.trigger.sink.local.LocalIoTDBHandler;
import org.apache.iotdb.db.engine.trigger.sink.mqtt.MQTTConfiguration;
import org.apache.iotdb.db.engine.trigger.sink.mqtt.MQTTEvent;
import org.apache.iotdb.db.engine.trigger.sink.mqtt.MQTTHandler;
import org.apache.iotdb.db.utils.windowing.configuration.SlidingSizeWindowConfiguration;
import org.apache.iotdb.db.utils.windowing.handler.SlidingSizeWindowEvaluationHandler;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.utils.Binary;

import org.fusesource.mqtt.client.QoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TriggerTest implements Trigger {

  private static final Logger LOGGER = LoggerFactory.getLogger(TriggerTest.class);

  private static final String TARGET_DEVICE = "root.target.alerting";
  private String remote_ip = "127.0.0.1";
  private String ts_type="double";
  private String trig_name="test";
  TSDataType[] open_ts_type = new TSDataType[] {TSDataType.DOUBLE};
  private final LocalIoTDBHandler localIoTDBHandler = new LocalIoTDBHandler();
  private final MQTTHandler mqttHandler = new MQTTHandler();

  private SlidingSizeWindowEvaluationHandler windowEvaluationHandler;

  @Override
  public void onCreate(TriggerAttributes attributes) throws Exception {
    LOGGER.info("onCreate(TriggerAttributes attributes)");
    remote_ip = attributes.getString("remote_ip");
    ts_type = attributes.getString("ts_type").toLowerCase();
    trig_name = attributes.getString("trig_name");


//    openSinkHandlers();
    switch(ts_type){
       case "double":
          windowEvaluationHandler =
           new SlidingSizeWindowEvaluationHandler(
              new SlidingSizeWindowConfiguration(TSDataType.DOUBLE, 1, 1),
              window -> {

                 localIoTDBHandler.onEvent(new LocalIoTDBEvent(window.getTime(0), window.getDouble(0)));
                 mqttHandler.onEvent(
                    new MQTTEvent("double", QoS.EXACTLY_ONCE, false, window.getTime(0), window.getDouble(0)));
            });
            open_ts_type = new TSDataType[] {TSDataType.DOUBLE};
            break;
              case "int32":
          windowEvaluationHandler =
           new SlidingSizeWindowEvaluationHandler(
              new SlidingSizeWindowConfiguration(TSDataType.INT32, 1, 1),
              window -> {

                 localIoTDBHandler.onEvent(new LocalIoTDBEvent(window.getTime(0), window.getInt(0)));
                 mqttHandler.onEvent(
                    new MQTTEvent("int32", QoS.EXACTLY_ONCE, false, window.getTime(0), window.getInt(0)));
            });
            open_ts_type = new TSDataType[] {TSDataType.INT32};
            break;
			
      case "int64":
          windowEvaluationHandler =
           new SlidingSizeWindowEvaluationHandler(
              new SlidingSizeWindowConfiguration(TSDataType.INT64, 1, 1),
              window -> {

                 localIoTDBHandler.onEvent(new LocalIoTDBEvent(window.getTime(0), window.getLong(0)));
                 mqttHandler.onEvent(
                    new MQTTEvent("int64", QoS.EXACTLY_ONCE, false, window.getTime(0), window.getLong(0)));
            });
            open_ts_type = new TSDataType[] {TSDataType.INT64};
            break;
      case "float":
          windowEvaluationHandler =
           new SlidingSizeWindowEvaluationHandler(
              new SlidingSizeWindowConfiguration(TSDataType.FLOAT, 1, 1),
              window -> {

                 localIoTDBHandler.onEvent(new LocalIoTDBEvent(window.getTime(0), window.getFloat(0)));
                 mqttHandler.onEvent(
                    new MQTTEvent("float", QoS.EXACTLY_ONCE, false, window.getTime(0), window.getFloat(0)));
            });
            open_ts_type = new TSDataType[] {TSDataType.FLOAT};
            break;
      case "boolean":
          windowEvaluationHandler =
           new SlidingSizeWindowEvaluationHandler(
              new SlidingSizeWindowConfiguration(TSDataType.BOOLEAN, 1, 1),
              window -> {

                 localIoTDBHandler.onEvent(new LocalIoTDBEvent(window.getTime(0), window.getBoolean(0)));
                 mqttHandler.onEvent(
                    new MQTTEvent("boolean", QoS.EXACTLY_ONCE, false, window.getTime(0), window.getBoolean(0)));
            });
            open_ts_type = new TSDataType[] {TSDataType.BOOLEAN};
            break;
      case "text":
          windowEvaluationHandler =
           new SlidingSizeWindowEvaluationHandler(
              new SlidingSizeWindowConfiguration(TSDataType.TEXT, 1, 1),
              window -> {

                 localIoTDBHandler.onEvent(new LocalIoTDBEvent(window.getTime(0), window.getBinary(0)));
                 mqttHandler.onEvent(
                    new MQTTEvent("text", QoS.EXACTLY_ONCE, false, window.getTime(0), window.getBinary(0)));
            });
            open_ts_type = new TSDataType[] {TSDataType.TEXT};
            break;	
      default:
         LOGGER.info("Invalid datatype.");
    }
    openSinkHandlers();

  }

  @Override
  public void onDrop() throws Exception {
    LOGGER.info("onDrop()");
    closeSinkHandlers();
  }

  @Override
  public void onStart() throws Exception {
    LOGGER.info("onStart()");
    openSinkHandlers();
  }

  @Override
  public void onStop() throws Exception {
    LOGGER.info("onStop()");
    closeSinkHandlers();
  }

  @Override
  public Double fire(long timestamp, Double value,PartialPath partialPath) {
    windowEvaluationHandler.collect(timestamp, value);
    return value;
  }
  
  @Override
  public Integer fire(long timestamp, Integer  value,PartialPath partialPath) {
    windowEvaluationHandler.collect(timestamp, value);
    return value;
  }

  @Override
  public Long fire(long timestamp, Long  value,PartialPath partialPath) {
    windowEvaluationHandler.collect(timestamp, value);
    return value;
  }
  @Override
  public Float fire(long timestamp, Float  value,PartialPath partialPath) {
    windowEvaluationHandler.collect(timestamp, value);
    return value;
  }
  @Override
  public Boolean fire(long timestamp, Boolean  value,PartialPath partialPath) {
    windowEvaluationHandler.collect(timestamp, value);
    return value;
  }
  @Override
  public Binary fire(long timestamp, Binary  value,PartialPath partialPath) {
    windowEvaluationHandler.collect(timestamp, value);
    return value;
  }
  @Override
  public double[] fire(long[] timestamps, double[] values,PartialPath partialPath) {
    for (int i = 0; i < timestamps.length; ++i) {
      windowEvaluationHandler.collect(timestamps[i], values[i]);
    }
    return values;
  }

  @Override
  public int[] fire(long[] timestamps, int[] values,PartialPath partialPath) {
    for (int i = 0; i < timestamps.length; ++i) {
      windowEvaluationHandler.collect(timestamps[i], values[i]);
    }
    return values;
  }


  @Override
  public long[] fire(long[] timestamps, long[] values,PartialPath partialPath) {
    for (int i = 0; i < timestamps.length; ++i) {
      windowEvaluationHandler.collect(timestamps[i], values[i]);
    }
    return values;
  }


  @Override
  public float[] fire(long[] timestamps, float[] values,PartialPath partialPath) {
    for (int i = 0; i < timestamps.length; ++i) {
      windowEvaluationHandler.collect(timestamps[i], values[i]);
    }
    return values;
  }


  @Override
  public boolean[] fire(long[] timestamps, boolean[] values,PartialPath partialPath) {
    for (int i = 0; i < timestamps.length; ++i) {
      windowEvaluationHandler.collect(timestamps[i], values[i]);
    }
    return values;
  }

  @Override
  public Binary[] fire(long[] timestamps, Binary[] values,PartialPath partialPath) {
    for (int i = 0; i < timestamps.length; ++i) {
      windowEvaluationHandler.collect(timestamps[i], values[i]);
    }
    return values;
  }

  private void openSinkHandlers() throws Exception {
    localIoTDBHandler.open(
        new LocalIoTDBConfiguration(
            TARGET_DEVICE, new String[] {"local_"+trig_name}, open_ts_type));
    mqttHandler.open(
        new MQTTConfiguration(
            remote_ip,
            1883,
            "root",
            "root",
            new PartialPath(TARGET_DEVICE),
            new String[] {"remote"+trig_name}));
  }

  private void closeSinkHandlers() throws Exception {
    localIoTDBHandler.close();
    mqttHandler.close();
  }
}

