package org.hypertrace.core.graphql.common.schema.typefunctions;

import graphql.annotations.processor.ProcessingElementsContainer;
import graphql.annotations.processor.typeFunctions.TypeFunction;
import graphql.language.ArrayValue;
import graphql.language.BooleanValue;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.ObjectField;
import graphql.language.ObjectValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import java.lang.reflect.AnnotatedType;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UnknownScalar implements TypeFunction {

  private static final GraphQLScalarType ATTRIBUTE_VALUE_SCALAR =
      GraphQLScalarType.newScalar()
          .name("Unknown")
          .description("A value of unknown type: A string, int, float, boolean, array or object")
          .coercing(
              new Coercing<>() {
                @Override
                public Object serialize(Object fetcherResult) throws CoercingSerializeException {
                  // Use default serializer
                  return fetcherResult;
                }

                @Override
                public Object parseValue(Object input) throws CoercingParseValueException {
                  return this.parseFromAst(input, CoercingParseValueException::new);
                }

                @Override
                public Object parseLiteral(Object input) throws CoercingParseLiteralException {
                  return this.parseFromAst(input, CoercingParseLiteralException::new);
                }

                private <E> Object parseFromAst(Object input, Function<Exception, E> errorWrapper) {
                  Function<Object, Object> recurse =
                      value -> this.parseFromAst(value, errorWrapper);

                  if (input instanceof StringValue) {
                    return ((StringValue) input).getValue();
                  }
                  if (input instanceof IntValue) {
                    return ((IntValue) input).getValue();
                  }
                  if (input instanceof FloatValue) {
                    return ((FloatValue) input).getValue();
                  }
                  if (input instanceof BooleanValue) {
                    return ((BooleanValue) input).isValue();
                  }
                  if (input instanceof ArrayValue) {
                    return ((ArrayValue) input)
                        .getValues().stream().map(recurse).collect(Collectors.toUnmodifiableList());
                  }
                  if (input instanceof ObjectValue) {
                    return ((ObjectValue) input)
                        .getObjectFields().stream()
                            .collect(
                                Collectors.toUnmodifiableMap(
                                    ObjectField::getName,
                                    field -> recurse.apply(field.getValue())));
                  }

                  return errorWrapper.apply(
                      new IllegalArgumentException(
                          String.format(
                              "Unsupported input of type %s",
                              input.getClass().getCanonicalName())));
                }
              })
          .build();

  @Override
  public boolean canBuildType(Class<?> aClass, AnnotatedType annotatedType) {
    return Object.class == aClass;
  }

  @Override
  public GraphQLScalarType buildType(
      boolean input,
      Class<?> aClass,
      AnnotatedType annotatedType,
      ProcessingElementsContainer container) {
    return ATTRIBUTE_VALUE_SCALAR;
  }
}
