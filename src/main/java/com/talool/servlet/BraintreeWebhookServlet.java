package com.talool.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.core.service.ServiceException;
import com.talool.payment.braintree.BraintreeUtil;
import com.talool.service.ServiceFactory;

/**
 * A servlet that handles braintree webhooks
 * 
 * @author clintz
 * 
 */
public class BraintreeWebhookServlet extends HttpServlet
{
	private static final long serialVersionUID = -8339501150117128457L;
	private static final Logger LOG = LoggerFactory.getLogger(BraintreeWebhookServlet.class);

	private static final String BT_CHALLENGE_PARAM = "bt_challenge";
	private static final String BT_SIGNATURE_PARAM = "bt_signature";
	private static final String BT_PAYLOAD = "bt_payload";

	/**
	 * doGet simply verifies the destination endpoint for Braintree
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		resp.setContentType("text/html");

		LOG.info("Verification request from braintree");

		if (req.getParameter(BT_CHALLENGE_PARAM) == null)
		{
			LOG.error("Missing braintree challenge param");
			resp.setStatus(500);
			return;
		}

		resp.getWriter().write(BraintreeUtil.get().verifyWebhook(req.getParameter("bt_challenge")));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		final String btSignatureParam = req.getParameter(BT_SIGNATURE_PARAM);
		final String btPayloadParam = req.getParameter(BT_PAYLOAD);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Received braintree webhook request");
		}
		try
		{
			ServiceFactory.get().getTaloolService().processBraintreeNotification(btSignatureParam, btPayloadParam);
		}
		catch (ServiceException e)
		{
			LOG.error("Problem processing braintree webhook: " + e.getLocalizedMessage(), e);
			resp.setStatus(500);
		}

	}

}
