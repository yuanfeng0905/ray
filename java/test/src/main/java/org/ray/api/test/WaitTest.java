package org.ray.api.test;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ray.api.Ray;
import org.ray.api.RayObject;
import org.ray.api.RayRemote;
import org.ray.api.WaitResult;

@RunWith(MyRunner.class)
public class WaitTest {

  @RayRemote
  public static String hi() {
    return "hi";
  }

  @RayRemote
  public static String delayHi() {
    try {
      Thread.sleep(100 * 1000);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "hi";
  }

  @Test
  public void test() {
    RayObject<String> obj1 = Ray.call(WaitTest::hi);
    RayObject<String> obj2 = Ray.call(WaitTest::delayHi);

    List<RayObject<String>> waitfor = ImmutableList.of(obj1, obj2);
    WaitResult<String> waitResult = Ray.wait(waitfor, 2, 2 * 1000);
    List<RayObject<String>> readys = waitResult.getReadyOnes();

    if (!readys.isEmpty()) {
      Assert.assertEquals(1, waitResult.getReadyOnes().size());
      Assert.assertEquals(1, waitResult.getRemainOnes().size());
      Assert.assertEquals("hi", readys.get(0).get());
    } else {
      Assert.assertEquals(0, waitResult.getReadyOnes().size());
      Assert.assertEquals(2, waitResult.getRemainOnes().size());
    }
  }

}
