/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.monitoring;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.testing.junit4.MultipleAttemptsRule;
import com.google.monitoring.v3.UptimeCheckConfig;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

/** Integration (system) tests for {@link com.example.monitoring.UptimeSample}. */
@RunWith(JUnit4.class)
@SuppressWarnings("checkstyle:abbreviationaswordinname")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UptimeIT {
  @Rule public final MultipleAttemptsRule multipleAttemptsRule = new MultipleAttemptsRule(5);
  private ByteArrayOutputStream bout;
  private PrintStream out;
  private PrintStream originalPrintStream;
  private static String checkName;

  private static UptimeCheckConfig config =
      UptimeCheckConfig.newBuilder()
          .setDisplayName("check-" + UUID.randomUUID().toString().substring(0, 6))
          .build();

  @Before
  public void setUp() {
    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    originalPrintStream = System.out;
    System.setOut(out);
  }

  @After
  public void tearDown() {
    // restores print statements in the original method
    System.out.flush();
    System.setOut(originalPrintStream);
  }

  @Test
  public void test1_CreateUptimeCheck() throws Exception {
    UptimeSample.main("create", "-n", config.getDisplayName(), "-o", "test.example.com", "-a", "/");
    String actual = bout.toString();
    assertThat(actual).contains(config.getDisplayName());
    checkName = actual.split(":")[1].trim();
  }

  @Test
  public void test2_UpdateUptimeCheck() throws Exception {
    UptimeSample.main("update", "-n", checkName, "-a", "/updated");
    assertThat(bout.toString()).contains("/updated");
  }

  @Test
  public void test2_GetUptimeCheck() throws Exception {
    UptimeSample.main("get", "-n", checkName);
    assertThat(bout.toString()).contains(config.getDisplayName());
  }

  @Test
  public void test2_ListUptimeChecks() throws Exception {
    UptimeSample.main("list");
    assertThat(bout.toString()).contains(config.getDisplayName());
  }

  @Test
  public void test2_ListUptimeIps() throws Exception {
    // Create a few uptime check configs to list.
    UptimeSample.main("listIPs");
    String output = bout.toString();
    assertThat(output).contains("USA - ");
    assertThat(output).contains("EUROPE - ");
    assertThat(output).contains("SOUTH_AMERICA - ");
    assertThat(output).contains("ASIA_PACIFIC - ");
  }

  @Test
  public void test3_DeleteUptimeCheck() throws Exception {
    UptimeSample.main("delete", "-n", checkName);
  }
}
