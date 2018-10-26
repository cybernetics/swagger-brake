package com.arnoldgalovics.blog.swagger.breaker.core.model.transformer;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;

import com.arnoldgalovics.blog.swagger.breaker.core.model.Schema;
import com.arnoldgalovics.blog.swagger.breaker.core.model.service.SchemaStore;
import io.swagger.v3.oas.models.Components;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ComponentsTransformer implements Transformer<Components, SchemaStore> {
    private final SchemaTransformer schemaTransformer;

    @Override
    public SchemaStore transform(Components from) {
        Collection<Pair<String, Schema>> schemas = from.getSchemas()
            .entrySet()
            .stream().map(e -> {
                Schema schema = schemaTransformer.transform(e.getValue());
                return new ImmutablePair<>(e.getKey(), schema);
            })
            .collect(toList());
        return new SchemaStore(schemas.stream().collect(toMap(Pair::getKey, Pair::getValue)));
    }


}