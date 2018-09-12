package com.sankuai.sjst.nio.client;


import com.sankuai.sjst.nio.client.tools.BenchmarkRunner;
import org.junit.Test;


public class MultiConnectTest {

    @Test
    public void test() throws Exception {
        System.out.println(BenchmarkRunner.run(100));
    }

}
