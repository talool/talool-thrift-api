package com.talool.service;

import org.apache.commons.lang.time.StopWatch;

import com.talool.service.ResponseTimer.MethodTimer;

/**
 * A ResponseTimer because i dont want to have to use AOP to log response time requests or use a
 * Servlt filter.
 * 
 * This is a ThreadLocal implementation for convenience
 * 
 * @author clintz
 * 
 */
public class ResponseTimer extends ThreadLocal<MethodTimer> {
  static class MethodTimer {
    StopWatch watch = new StopWatch();
    String method;

    public MethodTimer() {}
  }

  @Override
  protected MethodTimer initialValue() {
    return new MethodTimer();
  }
}
