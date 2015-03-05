package com.talool.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.api.thrift.CustomerService_t;
import com.talool.core.Merchant;
import com.talool.core.service.ServiceException;

/**
 * 
 * 
 * 
 * @author clintz
 */
public class TServletTaloolService extends TServlet {
  private static final Logger LOG = LoggerFactory.getLogger(TServletTaloolService.class);
  private static final long serialVersionUID = 2766746006277115123L;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    RequestUtils.setRequest(request);
    super.doPost(request, response);
  }

  @Override
  protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
    if (request.getParameterMap().size() > 0) {
      doHealthCheck(request, response);
    } else {
      RequestUtils.setRequest(request);
      final PrintWriter out = response.getWriter();
      out.println("Not Supported");
    }
  }

  public void doHealthCheck(final HttpServletRequest request, final HttpServletResponse response) {
    boolean isInternalRequest = false;

    for (final String ipStartsWith : ServiceApiConfig.get().getAllowableHealthCheckIps()) {
      if (request.getRemoteAddr().startsWith(ipStartsWith)) {
        isInternalRequest = true;
      }
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug("Health check from IP: " + request.getRemoteAddr());
    }

    if (isInternalRequest == false) {
      return;
    }

    response.setContentType("text/html");
    PrintWriter out;

    try {
      final List<Merchant> merchants = ServiceFactory.get().getTaloolService().getMerchantByName("Talool");
      out = response.getWriter();
      out.println(merchants.get(0).getName());
    } catch (IOException | ServiceException e) {
      LOG.error(e.getLocalizedMessage(), e);
      try {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      } catch (IOException ex) {
        LOG.error(e.getLocalizedMessage(), ex);
      }
    }

  }

  public TServletTaloolService() {
    super(new CustomerService_t.Processor<CustomerServiceThriftImpl>(new CustomerServiceThriftImpl()), new TBinaryProtocol.Factory());
  }
}
