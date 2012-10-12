package com.profiler.common.hbase;

import com.profiler.common.util.PropertyUtils;
import junit.framework.Assert;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseConfigurationFactoryBean;
import org.springframework.data.hadoop.hbase.HbaseSystemException;

import java.io.IOException;
import java.util.Properties;

/**
 *
 */
public class HbaseTemplate2Test {

    private static HbaseConfigurationFactoryBean hbaseConfigurationFactoryBean;

    @BeforeClass
    public static void beforeClass() throws IOException {
        String path = HbaseTemplate2Test.class.getClassLoader().getResource("test-hbase.properties").getPath();
        Properties properties = PropertyUtils.readProperties(path);

        Configuration cfg = HBaseConfiguration.create();
        cfg.set("hbase.zookeeper.quorum", properties.getProperty("hbase.client.host"));
        cfg.set("hbase.zookeeper.property.clientPort", properties.getProperty("hbase.client.port"));
        hbaseConfigurationFactoryBean = new HbaseConfigurationFactoryBean();
        hbaseConfigurationFactoryBean.setConfiguration(cfg);
        hbaseConfigurationFactoryBean.afterPropertiesSet();
    }

    @AfterClass
    public static void afterClass() {
        if (hbaseConfigurationFactoryBean != null) {
            hbaseConfigurationFactoryBean.destroy();
        }

    }


    @Test
    public void notExist() throws Exception {

        HbaseTemplate2 hbaseTemplate2 = new HbaseTemplate2();
        hbaseTemplate2.setConfiguration(hbaseConfigurationFactoryBean.getObject());

        try {
            hbaseTemplate2.put("NOT_EXIST", new byte[0], "familyName".getBytes(), "columnName".getBytes(), new byte[0]);
            Assert.fail("exceptions");
        } catch (HbaseSystemException e) {
            if (!(e.getCause() instanceof TableNotFoundException)) {
                Assert.fail("unexpected exception :" + e.getCause());
            }
        }


    }
}
