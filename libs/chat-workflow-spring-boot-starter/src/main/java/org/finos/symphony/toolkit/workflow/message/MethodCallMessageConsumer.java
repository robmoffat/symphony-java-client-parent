package org.finos.symphony.toolkit.workflow.message;

import java.util.List;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.actions.ActionConsumer;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

public class MethodCallMessageConsumer implements ActionConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(MethodCallMessageConsumer.class);

	private List<ChatHandlerMapping<?>> handlerMapping;
	private ErrorHandler errors;

	public MethodCallMessageConsumer(List<ChatHandlerMapping<?>> handlerMapping, ErrorHandler eh) {
		this.handlerMapping = handlerMapping;
		this.errors = eh;
	}

	@Override
	public void accept(Action t) {
		handlerMapping.stream().forEach(hm -> handle(hm, t));
	}

	private void handle(ChatHandlerMapping<?> hm, Action t) {
		hm.getExecutors(t).stream().forEach(e -> {
			try {
				e.execute();

			} catch (Throwable ex) {
				LOG.error("Coulnd't process {}, error {}", t, ex.getLocalizedMessage());
				errors.handleError(ex);
			}

		});
	}

}