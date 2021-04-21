package org.hypertrace.core.graphql.log.event.request;

import com.google.inject.AbstractModule;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.common.request.AttributeRequestBuilder;
import org.hypertrace.core.graphql.common.request.DefaultAttributeRequestBuilder;
import org.hypertrace.core.graphql.common.request.DefaultFilterRequestBuilder;
import org.hypertrace.core.graphql.common.request.FilterRequestBuilder;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeAssociator;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeScopeStringTranslator;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;

public class LogEventRequestModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(LogEventRequestBuilder.class).to(DefaultLogEventRequestBuilder.class);
    requireBinding(ArgumentDeserializer.class);
    bind(AttributeRequestBuilder.class).to(DefaultAttributeRequestBuilder.class);
    bind(FilterRequestBuilder.class).to(DefaultFilterRequestBuilder.class);
    requireBinding(AttributeStore.class);
    requireBinding(ArgumentDeserializer.class);
    requireBinding(AttributeAssociator.class);
    requireBinding(GraphQlSelectionFinder.class);
    requireBinding(AttributeScopeStringTranslator.class);
  }
}
