package com.progress.codeshare.esbservice.fileWriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import com.sonicsw.xq.XQConstants;
import com.sonicsw.xq.XQEnvelope;
import com.sonicsw.xq.XQInitContext;
import com.sonicsw.xq.XQMessage;
import com.sonicsw.xq.XQParameters;
import com.sonicsw.xq.XQPart;
import com.sonicsw.xq.XQService;
import com.sonicsw.xq.XQServiceContext;
import com.sonicsw.xq.XQServiceException;

public final class FileWriterService implements XQService {
	private static final String PARAM_FILE = "name";
	private static final String PARAM_DIRECTORY = "directory";
	private static final String PARAM_MESSAGE_PART = "messagePart";

	public void destroy() {
	}

	public void init(XQInitContext ctx) {
	}

	public void service(final XQServiceContext ctx) throws XQServiceException {
		Writer writer = null;

		try {
			final XQParameters params = ctx.getParameters();

			final int messagePart = params.getIntParameter(PARAM_MESSAGE_PART,
					XQConstants.PARAM_STRING);
			final String directory = params.getParameter(PARAM_DIRECTORY,
					XQConstants.PARAM_STRING);
			final String file = params.getParameter(PARAM_FILE,
					XQConstants.PARAM_STRING);

			writer = new BufferedWriter(new FileWriter(directory + file));

			while (ctx.hasNextIncoming()) {
				final XQEnvelope env = ctx.getNextIncoming();

				final XQMessage msg = env.getMessage();

				final XQPart part = msg.getPart(messagePart);

				writer.write((String) part.getContent());

				final Iterator addressIterator = env.getAddresses();

				if (addressIterator.hasNext())
					ctx.addOutgoing(env);

			}

		} catch (final Exception e) {
			throw new XQServiceException(e);
		} finally {

			if (writer != null) {

				try {
					writer.close();
				} catch (final IOException e) {
					throw new XQServiceException(e);
				}

			}

		}

	}

}