package org.finos.symphony.toolkit.workflow.java.converters;

import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerExecutor;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.springframework.core.Ordered;

public class FormResponseConverter implements ResponseConverter {

	@Override
	public Response convert(Object source, ChatHandlerExecutor creator) {
		Addressable a = creator.action().getAddressable();
		return new FormResponse(a, source, false, null, null);
	}

	@Override
	public boolean canConvert(Object in) {
		Class<?> c = in.getClass();
		Work work = c.getAnnotation(Work.class);
		if (work != null) {
			return true;
		}
		
		return false;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}